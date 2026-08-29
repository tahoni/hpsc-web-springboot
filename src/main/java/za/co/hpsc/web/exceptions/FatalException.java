package za.co.hpsc.web.exceptions;

/**
 * Represents a fatal exception that generally indicates an unrecoverable error condition
 * within an application.
 *
 * <p>
 * This class extends {@link Exception} and is typically used in scenarios where an
 * operation cannot proceed due to a critical failure.
 * </p>
 *
 * @since 1.0.0
 */
public class FatalException extends Exception {
    /**
     * Constructs a new {@code FatalException} with no detail message or cause.
     */
    public FatalException() {
        super();
    }

    /**
     * Constructs a new {@code FatalException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public FatalException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code FatalException} with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause. May be {@code null} if the cause is nonexistent or unknown.
     */
    public FatalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code FatalException} with the specified cause.
     *
     * @param cause the cause. May be {@code null} if the cause is nonexistent or unknown.
     */
    public FatalException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code FatalException} with the specified detail message, cause,
     * suppression enablement and writable stack trace enablement.
     *
     * @param message            the detail message.
     * @param cause              the cause. May be {@code null} if the cause is nonexistent or unknown.
     * @param enableSuppression  whether suppression is enabled.
     * @param writableStackTrace whether the stack trace should be writable.
     */
    public FatalException(String message, Throwable cause, boolean enableSuppression,
                          boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
