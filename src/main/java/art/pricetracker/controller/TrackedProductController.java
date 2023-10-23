package art.pricetracker.controller;

import art.pricetracker.model.CustomProductJson;
import art.pricetracker.model.PriceHistory;
import art.pricetracker.model.ProductJson;
import art.pricetracker.model.TrackedProduct;
import art.pricetracker.repository.PriceHistoryRepository;
import art.pricetracker.repository.TrackedProductRepository;
import art.pricetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class TrackedProductController {

    @Autowired
    private TrackedProductRepository productRepo;

    @Autowired
    private PriceHistoryRepository priceRepo;

    @Autowired
    private UserRepository userRepo;


    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> addProduct(@RequestBody ProductJson productJson, Principal principal) {
        var product = new TrackedProduct(productJson);
        product.setUser(userRepo.getReferenceById(principal.getName()));
        productRepo.save(product);
        priceRepo.save(new PriceHistory(product, product.getCurrentPrice()));
        return ResponseEntity.ok("Product added successfully");
    }

    @PostMapping("/custom")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> addProduct(@RequestBody CustomProductJson productJson, Principal principal) {
        var product = new TrackedProduct(productJson);
        product.setUser(userRepo.getReferenceById(principal.getName()));
        productRepo.save(product);
        priceRepo.save(new PriceHistory(product, product.getCurrentPrice()));
        return ResponseEntity.ok("Product added successfully");
    }

    @GetMapping
    public ResponseEntity<List<TrackedProduct>> getProducts(Principal principal) {
        List<TrackedProduct> products = productRepo.findByUser(userRepo.getReferenceById(principal.getName()));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "http://localhost:3000");

        return ResponseEntity.ok()
                .headers(headers)
                .body(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackedProduct> getProduct(@PathVariable Long id, Principal principal) {
        Optional<TrackedProduct> found = productRepo.findByUserAndId(userRepo.getReferenceById(principal.getName()), id);

        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackedProduct> updateProduct(@PathVariable Long id, @RequestBody TrackedProduct updates,
                                                        Principal principal) {

        TrackedProduct product = productRepo.findByUserAndId(userRepo.getReferenceById(principal.getName()), id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        product.setNotify(updates.isNotify());

        TrackedProduct updated = productRepo.save(product);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, Principal principal) {

        TrackedProduct product = productRepo.findByUserAndId(userRepo.getReferenceById(principal.getName()), id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        productRepo.delete(product);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<TrackedProduct> searchProducts(@RequestParam String query, Principal principal) {

        return productRepo.findByUserAndNameContainingIgnoreCase(userRepo.getReferenceById(principal.getName()), query);
    }
}