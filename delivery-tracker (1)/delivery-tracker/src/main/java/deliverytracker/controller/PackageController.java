package deliverytracker.controller;

import deliverytracker.entity.Package;
import deliverytracker.observer.StatusHistoryObserver;
import deliverytracker.service.PackageService;
import deliverytracker.util.JdbcReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/packages")
@CrossOrigin(origins = "*")
public class PackageController {

    private final PackageService packageService;
    private final StatusHistoryObserver historyObserver;
    private final JdbcReportService jdbcReportService;

    public PackageController(
            PackageService packageService,
            StatusHistoryObserver historyObserver,
            JdbcReportService jdbcReportService) {
        this.packageService = packageService;
        this.historyObserver = historyObserver;
        this.jdbcReportService = jdbcReportService;
    }

    // POST /api/packages → Krijo pakete
    @PostMapping
    public ResponseEntity<Package> createPackage(
            @RequestBody Package pkg) {
        return ResponseEntity.ok(
                packageService.createPackage(pkg));
    }

    // GET /api/packages → Te gjitha paketat
    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages() {
        return ResponseEntity.ok(
                packageService.findAll());
    }

    // GET /api/packages/track/{code} → Track paketen
    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<Package> trackPackage(
            @PathVariable String trackingCode) {
        return ResponseEntity.ok(
                packageService.findByTrackingCode(trackingCode));
    }

    // GET /api/packages/phone/{phone}
    @GetMapping("/phone/{phone}")
    public ResponseEntity<List<Package>> getByPhone(
            @PathVariable String phone) {
        return ResponseEntity.ok(
                packageService.findByPhone(phone));
    }

    // GET /api/packages/email/{email}
    @GetMapping("/email/{email}")
    public ResponseEntity<List<Package>> getByEmail(
            @PathVariable String email) {
        return ResponseEntity.ok(
                packageService.findByEmail(email));
    }

    // PUT /api/packages/{code}/status
    @PutMapping("/{trackingCode}/status")
    public ResponseEntity<Package> updateStatus(
            @PathVariable String trackingCode,
            @RequestParam String status,
            @RequestParam String location) {
        return ResponseEntity.ok(
                packageService.updateStatus(
                        trackingCode, status, location));
    }

    // PUT /api/packages/{code}/assign
    @PutMapping("/{trackingCode}/assign")
    public ResponseEntity<Package> assignDriver(
            @PathVariable String trackingCode,
            @RequestParam Long driverId) {
        return ResponseEntity.ok(
                packageService.assignDriver(
                        trackingCode, driverId));
    }

    // GET /api/packages/delayed
    @GetMapping("/delayed")
    public ResponseEntity<List<Package>> getDelayed() {
        return ResponseEntity.ok(
                packageService.findDelayedPackages());
    }

    // GET /api/packages/stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(
                packageService.getStatistics());
    }

    // GET /api/packages/destination/{dest}
    @GetMapping("/destination/{destination}")
    public ResponseEntity<List<Package>> getByDestination(
            @PathVariable String destination) {
        return ResponseEntity.ok(
                packageService.findByDestination(destination));
    }

    // GET /api/packages/{code}/history
    @GetMapping("/{trackingCode}/history")
    public ResponseEntity<List<String>> getHistory(
            @PathVariable String trackingCode) {
        return ResponseEntity.ok(
                historyObserver.getHistory(trackingCode));
    }

    // GET /api/packages/report
    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getReport() {
        Map<String, Object> report = new java.util.HashMap<>();
        report.put("totalPackages",
                jdbcReportService.getTotalPackages());
        report.put("byStatus",
                jdbcReportService.getPackageCountByStatus());
        report.put("recentPackages",
                jdbcReportService.getRecentPackages());
        return ResponseEntity.ok(report);
    }

    // DELETE /api/packages/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(
            @PathVariable Long id) {
        packageService.deletePackage(id);
        return ResponseEntity.ok().build();
    }
}