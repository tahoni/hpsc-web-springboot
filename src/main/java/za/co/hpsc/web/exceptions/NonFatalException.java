package za.co.hpsc.web.exceptions;

/**
 * Represents a non-fatal exception that indicates an error condition which does not
 * necessarily prevent the application from continuing its operation.
 *
 * <p>
 * This class extends {@link RuntimeException} and is typically used in scenarios where
 * an issue needs to be handled but does not terminate the application.
 * </p>
 */
public class NonFatalException extends RuntimeException {
    /**
     * Constructs a new {@code NonFatalException} with no detail message or cause.
     */
    public NonFatalException() {
        super();
    }

    /**
     * Constructs a new {@code NonFatalException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public NonFatalException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code NonFatalException} with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause. May be {@code null} if the cause is nonexistent or unknown.
     * @since 1.0.0
     */
    public NonFatalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code NonFatalException} with the specified cause.
     *
     * @param cause the cause. May be {@code null} if the cause is nonexistent or unknown.
     * @since 1.0.0
     */
    public NonFatalException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code NonFatalException} with the specified detail message, cause,
     * suppression enablement and writable stack trace enablement.
     *
     * @param message            the detail message.
     * @param cause              the cause. May be {@code null} if the cause is nonexistent or unknown.
     * @param enableSuppression  whether suppression is enabled.
     * @param writableStackTrace whether the stack trace should be writable.
     * @since 1.0.0
     */
    public NonFatalException(String message, Throwable cause, boolean enableSuppression,
                             boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
