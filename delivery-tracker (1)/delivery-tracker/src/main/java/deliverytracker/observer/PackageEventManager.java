package deliverytracker.observer;

import deliverytracker.entity.Package;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages observers and notifies them on status changes.
 * Covers R9 - Design Patterns (Observer Pattern)
 */
@Component
public class PackageEventManager {

    // R1 - Collections: Lista e observers
    private final List<PackageObserver> observers
            = new ArrayList<>();

    public PackageEventManager(
            StatusHistoryObserver statusHistoryObserver) {
        // Regjistro observer automatikisht
        observers.add(statusHistoryObserver);
    }

    // Shto observer te ri
    public void addObserver(PackageObserver observer) {
        observers.add(observer);
    }

    // Njofto te gjithe observers
    public void notifyObservers(Package pkg,
                                String oldStatus,
                                String newStatus) {
        observers.forEach(observer ->
                observer.onStatusChanged(pkg, oldStatus, newStatus));
    }
}