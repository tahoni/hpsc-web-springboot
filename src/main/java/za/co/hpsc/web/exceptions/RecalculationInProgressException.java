package za.co.hpsc.web.exceptions;

/**
 * Thrown when a calculation is requested while the application is still recalculating results it marked as out of date
 * when it started, so two calculations never write the same results at once. It is handled centrally as an HTTP
 * {@code 409 Conflict}, and the request can be repeated once the recalculation has finished.
 *
 * @since 18.2.0
 */
public class RecalculationInProgressException extends NonFatalException {
    /**
     * Constructs a new {@code RecalculationInProgressException} with the specified detail message.
     *
     * @param message the detail message.
     * @since 18.2.0
     */
    public RecalculationInProgressException(String message) {
        super(message);
    }
}
