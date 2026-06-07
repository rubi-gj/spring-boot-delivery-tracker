package deliverytracker.controller;

import deliverytracker.entity.Driver;
import deliverytracker.entity.Package;
import deliverytracker.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "*")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // POST /api/drivers → Shto driver te ri
    @PostMapping
    public ResponseEntity<Driver> createDriver(
            @RequestBody Driver driver) {
        return ResponseEntity.ok(
                driverService.createDriver(driver));
    }

    // GET /api/drivers → Te gjithe drivers
    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(
                driverService.findAll());
    }

    // GET /api/drivers/available → Drivers te lire
    @GetMapping("/available")
    public ResponseEntity<List<Driver>> getAvailable() {
        return ResponseEntity.ok(
                driverService.findAvailableDrivers());
    }

    // POST /api/drivers/login → Login per driver
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> credentials) {

        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<Driver> driver = driverService
                .login(username, password);

        Map<String, Object> response = new HashMap<>();

        if (driver.isPresent()) {
            response.put("success", true);
            response.put("driverId", driver.get().getId());
            response.put("name", driver.get().getName());
            response.put("username", driver.get().getUsername());
            response.put("location",
                    driver.get().getCurrentLocation());
            response.put("available",
                    driver.get().isAvailable());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message",
                    "Invalid username or password!");
            return ResponseEntity.status(401).body(response);
        }
    }

    // POST /api/drivers/assign → Cakto pakete
    @PostMapping("/assign")
    public ResponseEntity<Package> assignPackage(
            @RequestParam Long driverId,
            @RequestParam String trackingCode) {
        return ResponseEntity.ok(
                driverService.assignPackage(
                        driverId, trackingCode));
    }

    // PUT /api/drivers/update-location → Perditeso vendodhjen
    @PutMapping("/update-location")
    public ResponseEntity<Package> updateLocation(
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                driverService.updateLocation(
                        body.get("username"),
                        body.get("trackingCode"),
                        body.get("location"),
                        body.get("status")
                ));
    }

    // GET /api/drivers/{username}/packages
    @GetMapping("/{username}/packages")
    public ResponseEntity<List<Package>> getDriverPackages(
            @PathVariable String username) {
        return ResponseEntity.ok(
                driverService.getDriverPackages(username));
    }

    // PUT /api/drivers/{id} → Perditeso driver
    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(
            @PathVariable Long id,
            @RequestBody Driver driver) {
        return ResponseEntity.ok(
                driverService.updateDriver(id, driver));
    }
}