package za.co.hpsc.web.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationExceptionTest {

    // ValidationException()
    @Test
    void testNoArgsConstructor_whenInvoked_thenHasNoMessageOrCause() {
        // Act
        ValidationException exception = new ValidationException();

        // Assert
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    // ValidationException(String)
    @Test
    void testMessageConstructor_whenMessageProvided_thenMessageIsSetAndCauseIsNull() {
        // Arrange
        String message = "Invalid input";

        // Act
        ValidationException exception = new ValidationException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    // ValidationException(String, Throwable)
    @Test
    void testMessageAndCauseConstructor_whenBothProvided_thenBothAreSet() {
        // Arrange
        String message = "Invalid input";
        Throwable cause = new IllegalStateException("Root cause");

        // Act
        ValidationException exception = new ValidationException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    // ValidationException(Throwable)
    @Test
    void testCauseConstructor_whenCauseProvided_thenCauseIsSetAndMessageIsCauseToString() {
        // Arrange
        Throwable cause = new IllegalStateException("Root cause");

        // Act
        ValidationException exception = new ValidationException(cause);

        // Assert
        assertEquals(cause, exception.getCause());
        assertEquals(cause.toString(), exception.getMessage());
    }
}
