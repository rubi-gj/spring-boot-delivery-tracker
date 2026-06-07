package deliverytracker.service;

import deliverytracker.entity.Hub;
import deliverytracker.repository.HubRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service layer for Hub operations.
 */
@Service
public class HubService {

    private final HubRepository hubRepository;

    public HubService(HubRepository hubRepository) {
        this.hubRepository = hubRepository;
    }

    public Hub createHub(Hub hub) {
        return hubRepository.save(hub);
    }

    public List<Hub> findAll() {
        return hubRepository.findAll();
    }

    public Hub findByCity(String city) {
        return hubRepository.findByCity(city)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Hub not found in city: " + city));
    }
}