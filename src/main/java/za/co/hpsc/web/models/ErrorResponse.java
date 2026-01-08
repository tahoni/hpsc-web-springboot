package za.co.hpsc.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents an error response structure containing details about an error
 * that occurred during the execution of a request.
 *
 * <p>
 * The {@code ErrorResponse} class provides information about the error,
 * including the timestamp of when the error occurred, a descriptive message,
 * and the error type or details.
 * This class is useful for conveying error details to a client in a structured
 * format when an application encounters issues.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse extends Response {
    private LocalDateTime timestamp;
    private String message;
    private String error;
}
