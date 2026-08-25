package za.co.hpsc.web.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerResponseTest {

    // ControllerResponse(boolean, String)
    @Test
    void testBooleanConstructor_whenSuccessTrue_thenMapsMessageAndClearsError() {
        // Arrange
        String message = "Saved successfully";

        // Act
        ControllerResponse response = new ControllerResponse(true, message);

        // Assert
        assertNotNull(response.getTimestamp());
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals("", response.getError());
    }

    @Test
    void testBooleanConstructor_whenSuccessFalse_thenMapsErrorAndClearsMessage() {
        // Arrange
        String message = "Save failed";

        // Act
        ControllerResponse response = new ControllerResponse(false, message);

        // Assert
        assertNotNull(response.getTimestamp());
        assertFalse(response.isSuccess());
        assertEquals("", response.getMessage());
        assertEquals(message, response.getError());
    }

    // ControllerResponse(LocalDateTime, String, String)
    @Test
    void testShortConstructor_whenValuesProvided_thenSetsFieldsAndSuccessFalse() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 4, 24, 10, 15);
        String message = "Operation completed";

        // Act
        ControllerResponse response = new ControllerResponse(timestamp, message, null);

        // Assert
        assertEquals(timestamp, response.getTimestamp());
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testShortConstructor_whenNonNullNonBlankErrorProvided_thenDerivesSuccessTrue() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 4, 24, 10, 30);
        String message = "Operation completed";
        String error = "Some error text";

        // Act
        ControllerResponse response = new ControllerResponse(timestamp, message, error);

        // Assert
        assertEquals(timestamp, response.getTimestamp());
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(error, response.getError());
    }

    @Test
    void testShortConstructor_whenErrorIsBlank_thenDerivesSuccessFalse() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 4, 24, 10, 45);
        String message = "Operation completed";

        // Act
        ControllerResponse response = new ControllerResponse(timestamp, message, "   ");

        // Assert
        assertFalse(response.isSuccess());
    }

    // ControllerResponse(LocalDateTime, boolean, String, String)
    @Test
    void testFullConstructor_whenSuccessTrue_thenKeepsSuccessTrueAndMapsAllFields() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 4, 24, 11, 0);
        boolean success = true;
        String message = "Saved successfully";

        // Act
        ControllerResponse response = new ControllerResponse(timestamp, success, message, null);

        // Assert
        assertEquals(timestamp, response.getTimestamp());
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testFullConstructor_whenSuccessFalseAndErrorProvided_thenMapsFailureState() {
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
    void testFullConstructor_whenTimestampIsNull_thenAllowsNullTimestamp() {
        // Arrange
        boolean success = true;
        String message = "Message";
        String error = "Error";

        // Act
        ControllerResponse response = new ControllerResponse(null, success, message, error);

        // Assert
        assertNull(response.getTimestamp());
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(error, response.getError());
    }
}
