package za.co.hpsc.web.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationExceptionTest {

    @Test
    void testConstructor_whenNoArgumentsProvided_thenCreatesExceptionWithNullMessage() {
        // Act
        ValidationException exception = new ValidationException();

        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_whenMessageIsProvided_thenCreatesExceptionWithMessage() {
        // Arrange
        String message = "Input validation failed";

        // Act
        ValidationException exception = new ValidationException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_whenMessageAndCauseAreProvided_thenCreatesExceptionWithBoth() {
        // Arrange
        String message = "Validation failed due to invalid format";
        Throwable cause = new IllegalArgumentException("Invalid argument");

        // Act
        ValidationException exception = new ValidationException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
        assertInstanceOf(IllegalArgumentException.class, cause);
    }

    @Test
    void testConstructor_whenOnlyCauseIsProvided_thenCreatesExceptionWithCauseAsMessage() {
        // Arrange
        Throwable cause = new NumberFormatException("Invalid number format");

        // Act
        ValidationException exception = new ValidationException(cause);

        // Assert
        assertSame(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Invalid number format"));
    }

    @Test
    void testInheritance_whenInstantiated_thenIsInstanceOfIllegalArgumentException() {
        // Act
        ValidationException exception = new ValidationException("Test");

        // Assert
        assertInstanceOf(IllegalArgumentException.class, exception);
    }

    @Test
    void testInheritance_whenInstantiated_thenIsInstanceOfException() {
        // Act
        ValidationException exception = new ValidationException("Test");

        // Assert
        assertInstanceOf(Exception.class, exception);
    }

    @Test
    void testConstructor_whenMessageIsEmpty_thenCreatesExceptionWithEmptyMessage() {
        // Act
        ValidationException exception = new ValidationException("");

        // Assert
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_whenCauseChainIsProvided_thenPreservesCauseChain() {
        // Arrange
        Throwable rootCause = new RuntimeException("Root error");
        Throwable intermediateCause = new IllegalArgumentException("Invalid input", rootCause);
        String message = "Validation error";

        // Act
        ValidationException exception = new ValidationException(message, intermediateCause);

        // Assert
        assertSame(intermediateCause, exception.getCause());
        assertSame(rootCause, intermediateCause.getCause());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructor_whenNullMessageAndNullCauseAreProvided_thenCreatesExceptionWithBothNull() {
        // Act
        ValidationException exception = new ValidationException(null, null);

        // Assert
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }
}

