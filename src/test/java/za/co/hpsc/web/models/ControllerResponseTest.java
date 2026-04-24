package za.co.hpsc.web.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerResponseTest {
    @Test
    void testDefaultConstructor_whenInstantiated_thenUsesFieldDefaults() {
        // Arrange & Act
        ControllerResponse response = new ControllerResponse();

        // Assert
        assertNull(response.getTimestamp());
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testConstructorWithTimestampMessageAndError_whenValuesProvided_thenSetsFieldsAndSuccessFalse() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 4, 24, 10, 15);
        String message = "Operation completed";
        String error = null;

        // Act
        ControllerResponse response = new ControllerResponse(timestamp, message, error);

        // Assert
        assertEquals(timestamp, response.getTimestamp());
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testConstructorWithAllArguments_whenSuccessTrue_thenKeepsSuccessTrueAndMapsAllFields() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 4, 24, 11, 0);
        boolean success = true;
        String message = "Saved successfully";
        String error = null;

        // Act
        ControllerResponse response = new ControllerResponse(timestamp, success, message, error);

        // Assert
        assertEquals(timestamp, response.getTimestamp());
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testConstructorWithAllArguments_whenSuccessFalseAndErrorProvided_thenMapsFailureState() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 4, 24, 11, 30);
        boolean success = false;
        String message = "Save failed";
        String error = "Validation error";

        // Act
        ControllerResponse response = new ControllerResponse(timestamp, success, message, error);

        // Assert
        assertEquals(timestamp, response.getTimestamp());
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(error, response.getError());
    }

    @Test
    void testConstructorWithAllArguments_whenTimestampIsNull_thenAllowsNullTimestamp() {
        // Arrange
        LocalDateTime timestamp = null;
        boolean success = true;
        String message = "Message";
        String error = "Error";

        // Act
        ControllerResponse response = new ControllerResponse(timestamp, success, message, error);

        // Assert
        assertNull(response.getTimestamp());
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(error, response.getError());
    }
}
