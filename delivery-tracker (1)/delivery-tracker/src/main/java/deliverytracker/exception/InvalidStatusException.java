package deliverytracker.exception;

/**
 * Exception thrown when an invalid status transition is attempted.
 * For example: trying to set DELIVERED before IN_TRANSIT.
 * Covers requirement R10 - Custom Exception Handling.
 */
public class InvalidStatusException
        extends RuntimeException {

    public InvalidStatusException(String currentStatus,
                                  String newStatus) {
        super("Cannot transition from "
                + currentStatus + " to " + newStatus);
    }
}