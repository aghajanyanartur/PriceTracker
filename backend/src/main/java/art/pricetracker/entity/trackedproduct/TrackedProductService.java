package art.pricetracker.entity.trackedproduct;

import art.pricetracker.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackedProductService {

    @Autowired
    private TrackedProductRepository trackedProductRepository;

    public TrackedProduct create(TrackedProduct trackedProduct) {
        return trackedProductRepository.save(trackedProduct);
    }

    public TrackedProduct getById(Long id) {
        return trackedProductRepository.findById(id).orElseThrow(() -> new RuntimeException("Tracked product not found"));
    }

    public List<TrackedProduct> getByUser(User user){
        return trackedProductRepository.findByUser(user);
    }

    public TrackedProduct getByUserAndId(User user, Long id) {
        return trackedProductRepository.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("Tracked product not found"));
    }

    public void delete(Long id) {
        trackedProductRepository.deleteById(id);
    }
}
