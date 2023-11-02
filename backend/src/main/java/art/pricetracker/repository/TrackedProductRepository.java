package art.pricetracker.repository;

import art.pricetracker.model.TrackedProduct;
import art.pricetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackedProductRepository extends JpaRepository<TrackedProduct, Long> {

    List<TrackedProduct> findByUserAndNameContainingIgnoreCase(User user, String query);

    List<TrackedProduct> findByUser(User user);

    Optional<TrackedProduct> findByUserAndId(User user, Long id);

    List<TrackedProduct> findByUrlNot(String url);
}