package deliverytracker.concurrency;



import deliverytracker.entity.Package;
import deliverytracker.repository.PackageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Background job that checks package statuses.
 * Covers R5 - Concurrency / Multithreading
 * Runs every 30 seconds automatically.
 */
@Component
public class PackageStatusChecker {

    private final PackageRepository packageRepository;

    private final ExecutorService executorService
            = Executors.newFixedThreadPool(2);

    public PackageStatusChecker(
            PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }


    @Scheduled(fixedRate = 30000)
    public void checkPackageStatuses() {
        System.out.println(
                "[" + LocalDateTime.now() + "] " +
                        "Checking package statuses...");


        executorService.submit(() -> {
            try {
                List<Package> inTransit = packageRepository
                        .findByStatus("IN_TRANSIT");

                inTransit.forEach(pkg -> {
                    System.out.println(
                            "Package " + pkg.getTrackingCode() +
                                    " is IN_TRANSIT → Location: " +
                                    pkg.getCurrentLocation());
                });

                System.out.println(
                        "Total IN_TRANSIT packages: " +
                                inTransit.size());

            } catch (Exception e) {
                System.err.println(
                        "Error checking statuses: " +
                                e.getMessage());
            }
        });
    }
}