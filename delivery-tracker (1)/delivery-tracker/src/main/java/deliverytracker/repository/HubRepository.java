package deliverytracker.repository;

import deliverytracker.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HubRepository
        extends JpaRepository<Hub, Long> {

    Optional<Hub> findByCity(String city);
    Optional<Hub> findByName(String name);
}