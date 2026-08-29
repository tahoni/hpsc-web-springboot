package za.co.hpsc.web.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ControllerResponse;
import za.co.hpsc.web.utils.ValueUtil;

import java.time.LocalDateTime;

/**
 * Global exception handling advice for REST controllers. This class provides
 * centralised exception handling for exceptions occurring within the application's
 * controller classes by using defined exception handler methods.
 *
 * <p>
 * Each handler method captures a specific type of exception or a broader category
 * of exceptions and constructs an appropriate error response as a {@link ResponseEntity},
 * containing an error description, timestamp and any additional relevant details.
 * The responses generated are intended to provide structured feedback to clients
 * regarding errors arising during API interactions, ensuring consistent error formats.
 * The {@code @RestControllerAdvice} annotation specifies that this class applies
 * globally to controllers within the specified package.
 * </p>
 *
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    /**
     * Handles a {@link FatalException} raised by a controller, mapping it to an HTTP
     * {@code 500 Internal Server Error} response.
     *
     * @param ex      the fatal exception that was thrown.
     * @param request the current web request, used to log the request URL.
     * @return a {@link ResponseEntity} carrying a {@link ControllerResponse} that describes the error.
     */
    @ExceptionHandler(FatalException.class)
    public ResponseEntity<ControllerResponse> handleGeneralException(FatalException ex,
                                                                     WebRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ControllerResponse errorResponse = buildErrorResponse(ex.getMessage(), status);
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles a {@link ValidationException} raised by a controller, mapping it to an HTTP
     * {@code 400 Bad Request} response.
     *
     * @param ex      the validation exception that was thrown.
     * @param request the current web request, used to log the request URL.
     * @return a {@link ResponseEntity} carrying a {@link ControllerResponse} that describes the error.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ControllerResponse> handleValidationException(ValidationException ex,
                                                                        WebRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ControllerResponse errorResponse = buildErrorResponse(ex.getMessage(), status);
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles a {@link NonFatalException} raised by a controller, mapping it to an HTTP
     * {@code 404 Not Found} response.
     *
     * @param ex      the non-fatal exception that was thrown.
     * @param request the current web request, used to log the request URL.
     * @return a {@link ResponseEntity} carrying a {@link ControllerResponse} that describes the error.
     */
    @ExceptionHandler(NonFatalException.class)
    public ResponseEntity<ControllerResponse> handleNonFatalException(NonFatalException ex,
                                                                      WebRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.NOT_FOUND;
        ControllerResponse errorResponse = buildErrorResponse(ex.getMessage(), status);
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles an {@link HttpMessageConversionException} raised while reading or writing an HTTP
     * message body (e.g. malformed request JSON), mapping it to an HTTP {@code 400 Bad Request}
     * response.
     *
     * @param ex      the message conversion exception that was thrown.
     * @param request the current web request, used to log the request URL.
     * @return a {@link ResponseEntity} carrying a {@link ControllerResponse} that describes the error.
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ControllerResponse> handleHttpMessageConversionException(HttpMessageConversionException ex,
                                                                                   WebRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ControllerResponse errorResponse = buildErrorResponse(ex.getMessage(), status);
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Catch-all handler for any exception not covered by a more specific handler, mapping it
     * to an HTTP {@code 500 Internal Server Error} response.
     *
     * <p>
     * If the exception carries no message, a generic "Unexpected error occurred" message is
     * used instead.
     * </p>
     *
     * @param ex      the exception that was thrown.
     * @param request the current web request, used to log the request URL.
     * @return a {@link ResponseEntity} carrying a {@link ControllerResponse} that describes the error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ControllerResponse> handleUnhandledException(Exception ex,
                                                                       WebRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ValueUtil.nullAsDefaultString(ex.getMessage(), "Unexpected error occurred");
        ControllerResponse errorResponse = buildErrorResponse(message, status);
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Builds a {@link ControllerResponse} describing an error, timestamped at the moment of
     * the call.
     *
     * @param message the error message to include in the response.
     * @param status  the HTTP status the error corresponds to; its reason phrase is used as
     *                the response's status description.
     * @return a {@link ControllerResponse} describing the error.
     */
    protected ControllerResponse buildErrorResponse(String message, HttpStatus status) {
        return new ControllerResponse(LocalDateTime.now(), false, message, status.getReasonPhrase());
    }

    /**
     * Logs the given throwable, along with the request URL if a request is available.
     *
     * <p>
     * Delegates to {@link #logError(Throwable)} to log the throwable itself.
     * </p>
     *
     * @param throwable the throwable to log.
     * @param request   the current web request; may be {@code null}, in which case only the
     *                  throwable is logged.
     */
    protected void logError(Throwable throwable, WebRequest request) {
        if (request != null) {
            log.error("Request URL: {}", request.getDescription(false));
        }
        logError(throwable);
    }

    /**
     * Logs the given throwable's class name, message and stack trace, along with its cause,
     * if any.
     *
     * @param throwable the throwable to log. If {@code null}, a generic "null throwable"
     *                  error is logged instead.
     */
    protected void logError(Throwable throwable) {
        if (throwable == null) {
            log.error("Unhandled error: null throwable");
            return;
        }

        log.error("Exception: {}", throwable.getClass().getName());
        log.error("Error message: {})", throwable.getMessage(), throwable);

        Throwable cause = throwable.getCause();
        if (cause != null) {
            log.error("Original Exception: {}", cause.getMessage(), cause);
        }
    }
}
