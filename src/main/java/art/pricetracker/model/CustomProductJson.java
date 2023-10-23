package art.pricetracker.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CustomProductJson {

    private String imageUrl;
    private String name;
    private String quantity;
    private BigDecimal price;
}