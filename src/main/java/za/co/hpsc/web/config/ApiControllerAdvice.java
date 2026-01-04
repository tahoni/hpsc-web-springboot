package za.co.hpsc.web.config;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice("za.co.hpsc.web.controllers")
public class ApiControllerAdvice {

    /**
     * Handles generic exceptions that occur during the processing of requests.
     * Constructs a response entity containing an error message, a timestamp,
     * and additional error details to provide comprehensive feedback to the client.
     *
     * @param ex      the exception that was thrown
     * @param request the current web request context
     * @return a {@code ResponseEntity} containing a structured error response
     * with HTTP status 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("error", "Internal Server Error");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * Handles exceptions of type {@code RuntimeException} that occur during the processing of requests.
     * Constructs a response entity containing an error message, a timestamp,
     * and additional error details to provide feedback to the client.
     *
     * @param ex      the runtime exception that was thrown
     * @param request the current web request context
     * @return a {@code ResponseEntity} containing a structured error response
     * with HTTP status 500 (Internal Server Error)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("error", "Internal Server Error");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles exceptions of type {@code MismatchedInputException} that occur during
     * the processing of requests. Constructs a response entity containing an error
     * message, a timestamp, and details about the mismatched input, providing feedback
     * to the client about the bad request.
     *
     * @param ex      the {@code MismatchedInputException}, that was thrown
     * @param request the current web request context
     * @return a {@code ResponseEntity} containing a structured error response
     * with HTTP status 400 (Bad Request)
     */
    @ExceptionHandler(MismatchedInputException.class)
    public ResponseEntity<Object> handleMismatchedInputException(MismatchedInputException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("error", "Bad Request");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions of type {@code IllegalArgumentException} that occur during
     * the processing of requests.
     * Constructs a response entity containing an error message, a timestamp,
     * and additional error details to notify the client of a bad request.
     *
     * @param ex      the {@code IllegalArgumentException} that was thrown
     * @param request the current web request context
     * @return a {@code ResponseEntity} containing a structured error response
     * with HTTP status 400 (Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("error", "Bad Request");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
