package za.co.hpsc.web.exceptions;

/**
 * Represents a validation exception used to indicate that an input validation error
 * has occurred.
 *
 * <p>
 * This class extends {@link IllegalArgumentException}, allowing it to be used in scenarios
 * where invalid arguments are encountered.
 * </p>
 */
public class ValidationException extends IllegalArgumentException {
    /**
     * Constructs a new {@code ValidationException} with no detail message or cause.
     */
    public ValidationException() {
        super();
    }

    /**
     * Constructs a new {@code ValidationException} with the specified detail message.
     *
     * @param message the detail message.
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ValidationException} with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause. May be {@code null} if the cause is nonexistent or unknown.
     * @since 1.0.0
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code ValidationException} with the specified cause.
     *
     * @param cause the cause. May be {@code null} if the cause is nonexistent or unknown.
     * @since 1.0.0
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }
}
