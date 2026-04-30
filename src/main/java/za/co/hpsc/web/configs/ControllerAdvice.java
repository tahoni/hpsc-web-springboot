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

import java.time.LocalDateTime;

/**
 * Global exception handling advice for REST controllers. This class provides
 * centralised exception handling for exceptions occurring within the application's
 * controller classes by using defined exception handler methods.
 *
 * <p>
 * Each handler method captures a specific type of exception or a broader category
 * of exceptions and constructs an appropriate error response as a {@link ResponseEntity},
 * containing an error description, timestamp, and any additional relevant details.
 * The responses generated are intended to provide structured feedback to clients
 * regarding errors arising during API interactions, ensuring consistent error formats.
 * The {@code @RestControllerAdvice} annotation specifies that this class applies
 * globally to controllers within the specified package.
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(FatalException.class)
    public ResponseEntity<ControllerResponse> handleGeneralException(FatalException ex,
                                                                     WebRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ControllerResponse errorResponse = buildErrorResponse(ex.getMessage(), status);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ControllerResponse> handleValidationException(ValidationException ex,
                                                                        WebRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ControllerResponse errorResponse = buildErrorResponse(ex.getMessage(), status);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(NonFatalException.class)
    public ResponseEntity<ControllerResponse> handleNonFatalException(NonFatalException ex,
                                                                      WebRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.NOT_FOUND;
        ControllerResponse errorResponse = buildErrorResponse(ex.getMessage(), status);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ControllerResponse> handleHttpMessageConversionException(HttpMessageConversionException ex,
                                                                                   WebRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ControllerResponse errorResponse = buildErrorResponse(ex.getMessage(), status);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ControllerResponse> handleUnhandledException(Exception ex,
                                                                       WebRequest request) {
        logError(ex, request);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ((ex.getMessage() != null) ? ex.getMessage() : "Unexpected error occurred");
        ControllerResponse errorResponse = buildErrorResponse(message, status);
        return new ResponseEntity<>(errorResponse, status);
    }

    protected ControllerResponse buildErrorResponse(String message, HttpStatus status) {
        return new ControllerResponse(LocalDateTime.now(), false, message, status.getReasonPhrase());
    }

    protected void logError(Throwable throwable, WebRequest request) {
        if (request != null) {
            log.error("Request URL: {}", request.getDescription(false));
        }
        logError(throwable);
    }

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