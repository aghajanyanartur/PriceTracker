package art.pricetracker.entity.pricehistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceHistoryService {

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;


    public PriceHistory create(PriceHistory priceHistory) {
        return priceHistoryRepository.save(priceHistory);
    }
}
