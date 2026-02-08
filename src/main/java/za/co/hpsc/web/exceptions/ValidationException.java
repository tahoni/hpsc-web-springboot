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
     * Constructs a new {@code ValidationException} exception with null as its detail message.
     * The cause is not initialised, and may subsequently be initialised by a call
     * to {@link #initCause}.
     */
    public ValidationException() {
        super();
    }

    /**
     * Constructs a new {@code ValidationException} exception with the specified detail message.
     * The cause is not initialised, and may subsequently be initialised by a call
     * to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval
     *                by the {@link #getMessage()} method.
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ValidationException} exception with the specified detail message
     * and cause.
     *
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i> automatically
     * incorporated in this exception's detail message.
     * </p>
     *
     * @param message the detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). A null value is permitted and indicates
     *                that the cause is nonexistent or unknown.
     * @since 1.5
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code ValidationException} exception with the specified cause and a
     * detail message of {@code (cause == null ? null : cause.toString())} (which typically
     * contains the class and detail message of {@code cause}).
     *
     * <p>
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwable objets.
     * </p>
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). A null value is permitted and indicates
     *              that the cause is nonexistent or unknown.
     * @since 1.5
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }
}
