package za.co.hpsc.web.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a standardised response structure typically used by controllers.
 *
 * <p>
 * The {@code ControllerResponse} class encapsulates metadata about the response,
 * including a timestamp, success status, message, and error details.
 * It provides constructors for different initialisation scenarios, enabling flexible
 * response creation based on specific requirements.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
public class ControllerResponse {
    @NotNull
    private LocalDateTime timestamp;
    @NotNull
    private boolean success = false;
    private String message;
    private String error;

    /**
     * Constructs a new {@code ControllerResponse} object with the specified timestamp,
     * message, and error details.
     * <p>
     * This constructor initialises the {@code ControllerResponse} with the provided values
     * and sets the {@code success} field to {@code false}.
     *
     * @param timestamp the timestamp of the response, specifying when it was created.
     *                  Must not be null.
     * @param message   a message providing additional information about the response.
     *                  Can be null.
     * @param error     a description of any error associated with the response.
     *                  Can be null.
     */
    public ControllerResponse(@NotNull LocalDateTime timestamp, String message, String error) {
        this.timestamp = timestamp;
        this.message = message;
        this.error = error;
        this.success = false;
    }

    /**
     * Constructs a new {@code ControllerResponse} object with the specified timestamp,
     * success status, message, and error details.
     *
     * @param timestamp the timestamp of the response, specifying when it was created.
     *                  Must not be null.
     * @param success   a boolean indicating whether the operation was successful.
     * @param message   a message providing additional information about the response.
     *                  Can be null.
     * @param error     a description of any error associated with the response.
     *                  Can be null.
     */
    public ControllerResponse(@NotNull LocalDateTime timestamp, boolean success, String message, String error) {
        this.timestamp = timestamp;
        this.success = success;
        this.message = message;
        this.error = error;
    }
}
