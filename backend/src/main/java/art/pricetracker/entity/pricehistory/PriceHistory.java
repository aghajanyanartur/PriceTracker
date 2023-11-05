package art.pricetracker.entity.pricehistory;

import art.pricetracker.entity.trackedproduct.TrackedProduct;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
//@Data
public class PriceHistory {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="tracked_product_id")
    @JsonBackReference
    private TrackedProduct product;

    private BigDecimal price;

    @Column(name="scraped_at")
    private Instant scrapedAt;

    private PriceHistory() {}

    public PriceHistory(TrackedProduct product) {
        this.product = product;
        this.price = product.getCurrentPrice();
    }

    @PrePersist
    public void setScrapingTime() {
        this.scrapedAt = Instant.now();
    }
}