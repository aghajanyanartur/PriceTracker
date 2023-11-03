package art.pricetracker.controller;

import art.pricetracker.entity.pricehistory.PriceHistoryService;
import art.pricetracker.entity.trackedproduct.CustomProductJson;
import art.pricetracker.entity.trackedproduct.ProductJson;
import art.pricetracker.entity.trackedproduct.TrackedProduct;
import art.pricetracker.entity.trackedproduct.TrackedProductService;
import art.pricetracker.entity.pricehistory.PriceHistory;
import art.pricetracker.entity.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class TrackedProductController {

    @Autowired
    private TrackedProductService productService;

    @Autowired
    private PriceHistoryService priceService;

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody ProductJson productJson, Principal principal) {
        var product = new TrackedProduct(productJson);
        product.setUser(userService.getByName(principal.getName()));
        productService.create(product);
        priceService.create(new PriceHistory(product));
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PostMapping("/custom")
    public ResponseEntity<?> addProduct(@RequestBody CustomProductJson productJson, Principal principal) {
        var product = new TrackedProduct(productJson);
        product.setUser(userService.getByName(principal.getName()));
        productService.create(product);
        priceService.create(new PriceHistory(product));
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TrackedProduct>> getProducts(Principal principal) {
        List<TrackedProduct> products = productService.getByUser(userService.getByName(principal.getName()));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "http://localhost:3000");

        return ResponseEntity.ok().headers(headers).body(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackedProduct> getProduct(@PathVariable Long id, Principal principal) {
        TrackedProduct found = productService.getByUserAndId(userService.getByName(principal.getName()), id);

        return ResponseEntity.ok(found);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackedProduct> updateProduct(@PathVariable Long id, @RequestBody TrackedProduct updates,
                                                        Principal principal) {

        TrackedProduct product = productService.getByUserAndId(userService.getByName(principal.getName()), id);

        product.setNotify(updates.isNotify());

        TrackedProduct updated = productService.create(product);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, Principal principal) {

        productService.delete(id);

        return ResponseEntity.noContent().build();
    }
}