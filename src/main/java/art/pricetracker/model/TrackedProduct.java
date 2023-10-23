package art.pricetracker.model;

import art.pricetracker.service.WebScraperService;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class TrackedProduct {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String url;

    private String imageUrl;

    private BigDecimal currentPrice;

    private String website;

    private String quantity;

    private Boolean available;

    private boolean notify;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy="trackedProduct")
    @JsonManagedReference
    private List<PriceHistory> priceHistory = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonBackReference
    private User user;

    public TrackedProduct(ProductJson productJson) {
        this.url = productJson.getUrl();
        var productData = WebScraperService.scrapeProductPage(url);

        this.name = productData.getName();
        this.imageUrl = productData.getImageUrl();
        this.currentPrice = productData.getPrice();
        this.website = productData.getWebsite();
        this.quantity = productData.getQuantity();
        this.available = productData.getAvailable();
        this.notify = productData.isNotify();
    }

    public TrackedProduct(CustomProductJson customProductJson) {
        this.url = "custom.product";
        this.name = customProductJson.getName();
        this.imageUrl = customProductJson.getImageUrl();
        this.currentPrice = customProductJson.getPrice();
        this.website = "Custom";
        this.quantity = customProductJson.getQuantity();
        this.available = true;
        this.notify = false;
    }
}