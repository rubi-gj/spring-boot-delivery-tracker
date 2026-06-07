package deliverytracker.exception;

/**
 * Exception thrown when a package is not found.
 * This is an unchecked exception (extends RuntimeException).
 * Covers requirement R10 - Custom Exception Handling.
 */
public class PackageNotFoundException
        extends RuntimeException {

    public PackageNotFoundException(String trackingCode) {
        super("Package not found with tracking code: "
                + trackingCode);
    }

    public PackageNotFoundException(Long id) {
        super("Package not found with id: " + id);
    }
}