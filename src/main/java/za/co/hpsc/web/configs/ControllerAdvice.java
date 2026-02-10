package za.co.hpsc.web.configs;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
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
@RestControllerAdvice("za.co.hpsc.web.controllers")
public class ControllerAdvice {

    /**
     * Handles generic exceptions that occur during the processing of requests.
     * Constructs a response entity containing an error message, a timestamp,
     * and additional error details to provide comprehensive feedback to the client.
     *
     * @param ex      the exception that was thrown.
     * @param request the current web request context.
     * @return a {@link ResponseEntity} containing a structured error response
     * with HTTP status 500 (Internal Server Error).
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ControllerResponse> handleGeneralException(Exception ex,
                                                                     WebRequest request) {
        ControllerResponse errorResponse = new ControllerResponse(LocalDateTime.now(), ex.getMessage(),
                "Internal Server Error");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * Handles validation exceptions that occur during the processing of requests.
     * Constructs a response entity containing an error message, a timestamp,
     * and additional error details to notify the client of a bad request.
     *
     * @param ex      the exception that was thrown.
     * @param request the current web request context.
     * @return a {@link ResponseEntity} containing a structured error response
     * with HTTP status 400 (Bad Request).
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, MismatchedInputException.class})
    public ResponseEntity<ControllerResponse> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                             WebRequest request) {
        ControllerResponse errorResponse = new ControllerResponse(LocalDateTime.now(), ex.getMessage(),
                "Bad Request");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
