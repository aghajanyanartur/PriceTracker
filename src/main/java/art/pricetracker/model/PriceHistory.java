package art.pricetracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
public class PriceHistory {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="tracked_product_id")
    @JsonBackReference
    private TrackedProduct trackedProduct;

    private BigDecimal price;

    @Column(name="scraped_at")
    private Instant scrapedAt;

    public PriceHistory() {}

    public PriceHistory(TrackedProduct trackedProduct, BigDecimal price) {
        this.trackedProduct = trackedProduct;
        this.price = price;
    }

    @PrePersist
    public void setScrapingTime() {
        this.scrapedAt = Instant.now();
    }
}