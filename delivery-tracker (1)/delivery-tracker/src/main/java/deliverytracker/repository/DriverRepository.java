package deliverytracker.repository;

import deliverytracker.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository
        extends JpaRepository<Driver, Long> {

    List<Driver> findByAvailableTrue();

    List<Driver> findByCurrentLocation(String location);

    Optional<Driver> findByUsername(String username);

    Optional<Driver> findByUsernameAndPassword(
            String username, String password);
}