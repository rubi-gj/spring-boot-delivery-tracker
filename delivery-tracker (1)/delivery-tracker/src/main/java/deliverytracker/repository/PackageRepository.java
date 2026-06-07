package deliverytracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository
        extends JpaRepository<deliverytracker.entity.Package, Long> {

    Optional<deliverytracker.entity.Package>
    findByTrackingCode(String trackingCode);

    List<deliverytracker.entity.Package>
    findByStatus(String status);

    List<deliverytracker.entity.Package>
    findBySenderName(String senderName);

    List<deliverytracker.entity.Package>
    findByDestination(String destination);

    List<deliverytracker.entity.Package>
    findByRecipientEmail(String email);

    List<deliverytracker.entity.Package>
    findByAssignedDriverId(Long driverId);

    @Query("SELECT p FROM Package p WHERE p.status = 'IN_TRANSIT'")
    List<deliverytracker.entity.Package> findAllInTransit();
}