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
import java.util.List;

@Component
public class PriceUpdateScheduler {

    @Autowired
    private PriceHistoryRepository priceRepo;

    @Autowired
    private EmailSenderService emailSender;

    private final TrackedProductRepository productRepo;

    @Autowired
    public PriceUpdateScheduler(TrackedProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @Scheduled(cron = "0 0 12 * * ?") // daily at 12pm
//    @Scheduled(fixedRate = 2000) // Update every 10 seconds
    public void updatePrices() {
        List<TrackedProduct> products = productRepo.findAll();

        products.forEach(product -> {

            // Avoid searching for custom products
            String url = product.getUrl();

            if(!url.equals("custom.product")) {
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
                            String emailMessage = "Price changed: " + product.getName() +
                                    " in " + product.getWebsite() + " is now $" + product.getCurrentPrice();
                            emailSender.sendSimpleEmail("donotreply.pricetracker@gmail.com", emailMessage);
                        }
                    }

                    if (quantityChanged) {
                        product.setQuantity(newQuantity);

                        if (product.isNotify()) {
                            String emailMessage = "Price changed: " + product.getName() +
                                    " in " + product.getWebsite() + " is now $" + product.getCurrentPrice();
                            emailSender.sendSimpleEmail("donotreply.pricetracker@gmail.com", emailMessage);
                        }
                    }

                    if (availabilityChanged) {
                        product.setAvailable(newAvailable);
                    }

                    // Save the product only if changes occurred
                    productRepo.save(product);
                }
            }
        });
    }
}