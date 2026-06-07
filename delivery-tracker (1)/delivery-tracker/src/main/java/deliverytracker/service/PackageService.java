package deliverytracker.service;

import deliverytracker.entity.Driver;
import deliverytracker.entity.Package;
import deliverytracker.exception.InvalidStatusException;
import deliverytracker.exception.PackageNotFoundException;
import deliverytracker.observer.PackageEventManager;
import deliverytracker.repository.DriverRepository;
import deliverytracker.repository.PackageRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


/*
 * PackageService.java — Business logic layer for package operations.
 * R1: Uses HashMap (cache) and ArrayList (status validation).
 * R3: Uses lambda expressions for filtering and sorting.
 * R4: Uses Stream API — groupingBy, filter, sorted, collect.
 * R9: Calls PackageEventManager to notify Observer on status change.
 * R10: Throws PackageNotFoundException and InvalidStatusException.
 */



@Service
public class PackageService {

    private final PackageRepository packageRepository;
    private final PackageEventManager eventManager;
    private final DriverRepository driverRepository;

    // R1 — HashMap: O(1) lookup cache by tracking code
    // Avoids repeated database queries for the same package
    private final Map<String, Package> packageCache
            = new HashMap<>();

    // R1 — ArrayList: ordered list of valid statuses
    // Order matters — used with indexOf() for transition validation
    // CREATED, PICKED UP , IN_TRANSIT , DELIVERED
    private final List<String> validStatuses = new ArrayList<>(
            Arrays.asList(
                    "CREATED",
                    "PICKED_UP",
                    "IN_TRANSIT",
                    "DELIVERED"
            )
    );

    public PackageService(PackageRepository packageRepository,
                          PackageEventManager eventManager,
                          DriverRepository driverRepository) {
        this.packageRepository = packageRepository;
        this.eventManager = eventManager;
        this.driverRepository = driverRepository;
    }

    //
    public Package createPackage(Package pkg) {
        Package saved = packageRepository.save(pkg);
        packageCache.put(saved.getTrackingCode(), saved);
        return saved;
    }

    // R3 — Lambda: computeIfAbsent uses Function lambda
    // Checks cache first, queries DB only on cache miss
    public Package findByTrackingCode(String trackingCode) {
        return packageCache.computeIfAbsent(
                trackingCode,
                code -> packageRepository            //if not in cache, load from DB
                        .findByTrackingCode(code)
                        .orElseThrow(() ->
                                new PackageNotFoundException(code))
        );
    }

    // Find all packages by recipient phone                //NOT DONE YET IN PROGRESS
    // Used for customer login on tracking page
    public List<Package> findByPhone(String phone) {
        return packageRepository.findByRecipientEmail(phone);
    }
    // Find all packages by recipient email               ///NOT DONE YET IN PROGRESS
    // Used for customer login on tracking page
    public List<Package> findByEmail(String email) {
        return packageRepository.findByRecipientEmail(email);
    }

    // Returns all packages FROM admin panel
    public List<Package> findAll() {
        return packageRepository.findAll();
    }

    // Assigns a driver to a package
    // Sets package status to PICKED_UP
    // Sets driver availability to false
    public Package assignDriver(String trackingCode,
                                Long driverId) {
        Package pkg = packageRepository
                .findByTrackingCode(trackingCode)
                .orElseThrow(() ->
                        new PackageNotFoundException(trackingCode));

        Driver driver = driverRepository
                .findById(driverId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Driver not found: " + driverId));

        pkg.setAssignedDriver(driver);
        pkg.setStatus("PICKED_UP");
        pkg.setCurrentLocation(driver.getCurrentLocation());

        driver.setAvailable(false);
        driverRepository.save(driver);

        Package updated = packageRepository.save(pkg);
        packageCache.put(trackingCode, updated);

        // Njofto observers
        eventManager.notifyObservers(
                updated, "CREATED", "PICKED_UP");

        return updated;
    }

    // Perditeso statusin
    public Package updateStatus(String trackingCode,
                                String newStatus,
                                String location) {
        if (!validStatuses.contains(newStatus)) {
            throw new InvalidStatusException(
                    "UNKNOWN", newStatus);
        }

        Package pkg = findByTrackingCode(trackingCode);
        String oldStatus = pkg.getStatus();

        int currentIndex = validStatuses.indexOf(oldStatus);
        int newIndex = validStatuses.indexOf(newStatus);

        if (newIndex < currentIndex) {
            throw new InvalidStatusException(
                    oldStatus, newStatus);
        }

        pkg.setStatus(newStatus);
        pkg.setCurrentLocation(location);

        Package updated = packageRepository.save(pkg);
        packageCache.put(trackingCode, updated);

        // R9 - Observer Pattern
        eventManager.notifyObservers(
                updated, oldStatus, newStatus);

        return updated;
    }

    public List<Package> findDelayedPackages() {
        return packageRepository.findAll()
                .stream()
                .filter(p -> p.getStatus().equals("IN_TRANSIT"))
                .filter(p -> p.getCreatedAt() != null &&
                        p.getCreatedAt()
                                .isBefore(java.time.LocalDateTime
                                        .now().minusHours(24)))
                .collect(Collectors.toList());
    }


    public Map<String, Long> getStatistics() {
        return packageRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Package::getStatus,
                        Collectors.counting()
                ));
    }


    public List<Package> findByDestination(String destination) {
        return packageRepository.findAll()
                .stream()
                .filter(p -> p.getDestination()
                        .equalsIgnoreCase(destination))
                .sorted(Comparator.comparing(
                        Package::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }


    public void deletePackage(Long id) {
        if (!packageRepository.existsById(id)) {
            throw new PackageNotFoundException(id);
        }
        packageRepository.deleteById(id);
    }
}