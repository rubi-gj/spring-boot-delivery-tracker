package deliverytracker.service;

import deliverytracker.entity.Driver;
import deliverytracker.entity.Package;
import deliverytracker.repository.DriverRepository;
import deliverytracker.repository.PackageRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final PackageRepository packageRepository;

    public DriverService(DriverRepository driverRepository,
                         PackageRepository packageRepository) {
        this.driverRepository = driverRepository;
        this.packageRepository = packageRepository;
    }


    public Driver createDriver(Driver driver) {
        return driverRepository.save(driver);
    }


    public List<Driver> findAll() {
        return driverRepository.findAll();
    }

    public List<Driver> findAvailableDrivers() {
        return driverRepository.findByAvailableTrue();
    }

    // Login per driver
    public Optional<Driver> login(String username,
                                  String password) {
        return driverRepository
                .findByUsernameAndPassword(username, password);
    }


    public Package assignPackage(Long driverId,
                                 String trackingCode) {
        Driver driver = driverRepository
                .findById(driverId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Driver not found: " + driverId));

        Package pkg = packageRepository
                .findByTrackingCode(trackingCode)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Package not found: " + trackingCode));

        pkg.setAssignedDriver(driver);
        pkg.setStatus("PICKED_UP");
        pkg.setCurrentLocation(driver.getCurrentLocation());

        driver.setAvailable(false);
        driverRepository.save(driver);

        return packageRepository.save(pkg);
    }


    public Package updateLocation(String username,
                                  String trackingCode,
                                  String location,
                                  String status) {
        Driver driver = driverRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Driver not found"));

        Package pkg = packageRepository
                .findByTrackingCode(trackingCode)
                .orElseThrow(() ->
                        new RuntimeException("Package not found"));

        pkg.setCurrentLocation(location);
        pkg.setStatus(status);

        driver.setCurrentLocation(location);

        if (status.equals("DELIVERED")) {
            driver.setAvailable(true);
        }

        driverRepository.save(driver);
        return packageRepository.save(pkg);
    }

    public List<Package> getDriverPackages(String username) {
        Driver driver = driverRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Driver not found"));

        return packageRepository
                .findByAssignedDriverId(driver.getId());
    }

    public Driver updateDriver(Long id, Driver driver) {
        driver.setId(id);
        return driverRepository.save(driver);
    }
}