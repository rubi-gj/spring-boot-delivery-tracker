package deliverytracker.observer;

import deliverytracker.entity.Package;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Observer that records status change history.
 * Covers R9 - Design Patterns (Observer Pattern)
 * Covers R1 - Collections (HashMap + ArrayList)
 */
@Component
public class StatusHistoryObserver
        implements PackageObserver {

    // R1 - Collections: HashMap per historine
    private final Map<String, List<String>> history
            = new HashMap<>();

    @Override
    public void onStatusChanged(Package pkg,
                                String oldStatus,
                                String newStatus) {
        String key = pkg.getTrackingCode();

        // R1 - Collections: ArrayList per cdo pakete
        history.computeIfAbsent(key,
                k -> new ArrayList<>());

        String record = "[" + LocalDateTime.now() + "] " +
                oldStatus + " → " + newStatus +
                " @ " + pkg.getCurrentLocation();

        history.get(key).add(record);

        System.out.println(
                "STATUS CHANGE: Package " + key +
                        " | " + record);
    }

    // Merr historine e nje pakete
    public List<String> getHistory(String trackingCode) {
        return history.getOrDefault(
                trackingCode, new ArrayList<>());
    }

    // Merr te gjitha historite
    public Map<String, List<String>> getAllHistory() {
        return Collections.unmodifiableMap(history);
    }
}