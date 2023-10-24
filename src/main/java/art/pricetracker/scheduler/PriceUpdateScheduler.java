package art.pricetracker.scheduler;

import art.pricetracker.model.PriceHistory;
import art.pricetracker.model.TrackedProduct;
import art.pricetracker.repository.PriceHistoryRepository;
import art.pricetracker.repository.TrackedProductRepository;
import art.pricetracker.service.EmailSenderService;
import art.pricetracker.service.ProductData;
import art.pricetracker.service.WebScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class PriceUpdateScheduler {

    @Autowired
    private PriceHistoryRepository priceRepo;

    @Autowired
    private EmailSenderService emailSender;

    private final TrackedProductRepository productRepo;

    private final String emailMessageTemplate = "Price changed: %s in %s is now $%s";

    @Autowired
    public PriceUpdateScheduler(TrackedProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @Scheduled(cron = "0 0 12 * * ?") // daily at 12pm
//    @Scheduled(fixedRate = 2000) // Update every 10 seconds
    public void updateProducts() {

        List<TrackedProduct> products = productRepo.findByUrlNot("custom.product");

        List<TrackedProduct> productsToUpdate = new ArrayList<>();

        products.parallelStream().forEach(product -> {

            String url = product.getUrl();

            ProductData data = WebScraperService.scrapeProductPage(url);

            BigDecimal newPrice = data.getPrice();
            String newQuantity = data.getQuantity();
            boolean newAvailable = data.getAvailable();

            boolean priceChanged = !newPrice.equals(product.getCurrentPrice());
            boolean quantityChanged = !newQuantity.equals(product.getQuantity());
            boolean availabilityChanged = newAvailable != product.getAvailable();

            if (priceChanged || quantityChanged || availabilityChanged) {
                if (priceChanged) {
                    product.setCurrentPrice(newPrice);

                    // Save the price history only if changes occurred
                    priceRepo.save(new PriceHistory(product, product.getCurrentPrice()));

                    if (product.isNotify()) {
                        emailSender.sendSimpleEmail(String.format(emailMessageTemplate, product.getName(), product.getWebsite(), product.getCurrentPrice()));
                    }
                }

                if (quantityChanged) {
                    product.setQuantity(newQuantity);
                }

                if (availabilityChanged) {
                    product.setAvailable(newAvailable);
                }

                productsToUpdate.add(product);

            }
            productRepo.saveAll(productsToUpdate);
        });
    }
}