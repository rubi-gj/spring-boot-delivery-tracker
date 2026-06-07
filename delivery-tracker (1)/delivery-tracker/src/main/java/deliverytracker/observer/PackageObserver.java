package deliverytracker.observer;



import deliverytracker.entity.Package;

/**
 * Observer interface for package status changes.
 * Covers R9 - Design Patterns (Observer Pattern)
 */
public interface PackageObserver {
    void onStatusChanged(Package pkg, String oldStatus,
                         String newStatus);
}