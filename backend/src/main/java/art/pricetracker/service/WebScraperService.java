package art.pricetracker.service;

import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

@Service
public class WebScraperService {

    public static ProductData scrapeProductPage(String url) {

        String websiteName = getWebsiteName(url);

        if (websiteName.equals("www.amazon.com")) {
            return WebScarperUtil.scrapeAmazon(url);
        } else if(websiteName.equals("wefindsupply.com")) {
            return WebScarperUtil.scrapeWefindsupply(url);
        } else {
            return null;
        }
    }

    private static String getWebsiteName(String url) {
        URL givenUrl;
        try {
            givenUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return givenUrl.getHost();
    }
}