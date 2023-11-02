package art.pricetracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.math.BigDecimal;

public class WebScarperUtil {

    public static ProductData scrapeWefindsupply(String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            // Extract data from page
            String name = doc.select(".product__title h1").text();

            Element imageElement = doc.select("#Slider-Gallery-template--15921346576538__main img").first();
            String imageUrl = imageElement.attr("src");

            BigDecimal price = new BigDecimal(doc.getElementsByClass("price-item price-item--regular").text().trim().substring(1));

            String quantity = doc.select(".inventoryNote.form__label").text().trim();

            boolean available = !doc.select(".product-form__submit.button.button--full-width.button--secondary").hasAttr("disabled");

            // Create and return product data object
            return new ProductData(name, imageUrl, price, url, "We Find Supply", quantity, available, false);
        } catch (IOException e) {
            return null;
        }
    }

    public static ProductData scrapeAmazon(String url) {
        try {
            Document doc;
            do {
                doc = Jsoup.connect(url).get();
            } while(doc.select(".a-offscreen").isEmpty() || doc.select(".imgTagWrapper").isEmpty());

            String name = doc.select("#productTitle").text();

            String allImages = doc.select("#landingImage").attr("data-a-dynamic-image");
            var objectMapper = new ObjectMapper();
            var jsonNode = objectMapper.readTree(allImages);
            String imageUrl = jsonNode.fieldNames().next();

            BigDecimal price = new BigDecimal(doc.getElementsByClass("a-offscreen").get(0).text().substring(1));

            return new ProductData(name, imageUrl, price, url, "Amazon", "0", false, false);

        } catch (IOException e) {
            return null;
        }
    }
}
