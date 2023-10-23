package art.pricetracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;

@Service
public class WebScraperService {

    public static ProductData scrapeProductPage(String url) {
        URL givenUrl = null;
        try {
            givenUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String websiteName = givenUrl.getHost();

        if (websiteName.equals("www.ebay.com")) {
            try {
                Document doc;
                do {
                    doc = Jsoup.connect(url).get();
                } while(doc.select(".x-price-primary").isEmpty() || doc.select(".ux-image-magnify__image--original").isEmpty());

                String name = doc.getElementsByClass("ux-textspans ux-textspans--BOLD").get(1).text();

                String imageUrl = doc.select(".ux-image-magnify__image--original").get(0).attr("src");

                BigDecimal price = new BigDecimal(doc.getElementsByClass("x-price-primary").get(0).text().substring(4));

                return new ProductData(name, imageUrl, price, url, websiteName, "0", false, false);

            } catch (IOException e) {
                return null;
            }
        } else if(websiteName.equals("wefindsupply.com")) {
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
                return new ProductData(name, imageUrl, price, url, websiteName, quantity, available, false);
            } catch (IOException e) {
                return null;
            }
        } else {
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

                return new ProductData(name, imageUrl, price, url, websiteName, "0", false, false);

            } catch (IOException e) {
                return null;
            }
        }
    }
}