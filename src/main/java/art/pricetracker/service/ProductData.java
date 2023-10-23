package art.pricetracker.service;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductData {

    private String name;
    private String imageUrl;
    private BigDecimal price;
    private String productUrl;
    private String website;
    private String quantity;
    private Boolean available;
    private boolean notify;

    public ProductData(String name, String imageUrl, BigDecimal price, String productUrl,
                       String website, String quantity, Boolean available, boolean notify) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.productUrl = productUrl;
        this.website = website;
        this.quantity = quantity;
        this.available = available;
        this.notify = notify;
    }

    public ProductData() {
    }

    @Override
    public String toString() {
        return "ProductData{" +
                "name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", price=" + price +
                ", productUrl='" + productUrl + '\'' +
                ", website='" + website + '\'' +
                ", quantity='" + quantity + '\'' +
                ", available=" + available +
                ", notify=" + notify +
                '}';
    }
}
