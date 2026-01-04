package za.co.hpsc.web.exceptions;

/**
 * Represents a non-fatal exception that indicates an error condition
 * which does not necessarily prevent the application from continuing
 * its operation. This class extends {@link RuntimeException} and is
 * typically used in scenarios where an issue needs to be handled but
 * does not terminate the application.
 */
public class NonFatalException extends RuntimeException {
    /**
     * Constructs a new non-fatal exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public NonFatalException() {
        super();
    }

    /**
     * Constructs a new non-fatal exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public NonFatalException(String message) {
        super(message);
    }

    /**
     * Constructs a new non-fatal exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this non-fatal exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public NonFatalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new non-fatal exception with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}).  This constructor is useful for non-fatal exceptions
     * that are little more than wrappers for other throwables (for example, {@link
     * NullPointerException}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public NonFatalException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new non-fatal exception with the specified detail
     * message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     *
     * @param message            the detail message.
     * @param cause              the cause.  (A {@code null} value is permitted,
     *                           and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression  whether suppression is enabled or not
     * @param writableStackTrace whether the stack trace should be writable
     * @since 1.7
     */
    public NonFatalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
