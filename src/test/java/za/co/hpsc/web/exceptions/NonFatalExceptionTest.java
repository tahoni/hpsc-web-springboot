package za.co.hpsc.web.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NonFatalExceptionTest {

    @Test
    void testConstructor_whenNoArgumentsProvided_thenCreatesExceptionWithNullMessage() {
        // Act
        NonFatalException exception = new NonFatalException();

        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_whenMessageIsProvided_thenCreatesExceptionWithMessage() {
        // Arrange
        String message = "An error occurred during processing";

        // Act
        NonFatalException exception = new NonFatalException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_whenMessageAndCauseAreProvided_thenCreatesExceptionWithBoth() {
        // Arrange
        String message = "Processing error: Invalid data format";
        Throwable cause = new IllegalArgumentException("Invalid argument format");

        // Act
        NonFatalException exception = new NonFatalException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testConstructor_whenOnlyCauseIsProvided_thenCreatesExceptionWithCauseAsMessage() {
        // Arrange
        Throwable cause = new IllegalStateException("State error");

        // Act
        NonFatalException exception = new NonFatalException(cause);

        // Assert
        assertSame(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("State error"));
    }

    @Test
    void testConstructor_whenAllParametersAreProvided_thenCreatesExceptionWithAllSettings() {
        // Arrange
        String message = "Processing warning";
        Throwable cause = new Exception("Non-fatal issue");
        boolean enableSuppression = true;
        boolean writableStackTrace = true;

        // Act
        NonFatalException exception = new NonFatalException(message, cause, enableSuppression, writableStackTrace);

        // Assert
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testConstructor_whenSuppressionIsDisabled_thenCreatesExceptionWithSuppressionDisabled() {
        // Arrange
        String message = "Warning message";
        Throwable cause = new RuntimeException("Issue");
        boolean enableSuppression = false;
        boolean writableStackTrace = false;

        // Act
        NonFatalException exception = new NonFatalException(message, cause, enableSuppression, writableStackTrace);

        // Assert
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testInheritance_whenInstantiated_thenIsInstanceOfRuntimeException() {
        // Act
        NonFatalException exception = new NonFatalException("Test");

        // Assert
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void testInheritance_whenInstantiated_thenIsInstanceOfException() {
        // Act
        NonFatalException exception = new NonFatalException("Test");

        // Assert
        assertInstanceOf(Exception.class, exception);
    }

    @Test
    void testConstructor_whenMessageIsEmpty_thenCreatesExceptionWithEmptyMessage() {
        // Act
        NonFatalException exception = new NonFatalException("");

        // Assert
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_whenCauseChainIsProvided_thenPreservesCauseChain() {
        // Arrange
        Throwable rootCause = new RuntimeException("Root issue");
        Throwable intermediateCause = new IllegalArgumentException("Invalid input", rootCause);
        String message = "Non-fatal error";

        // Act
        NonFatalException exception = new NonFatalException(message, intermediateCause);

        // Assert
        assertSame(intermediateCause, exception.getCause());
        assertSame(rootCause, intermediateCause.getCause());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructor_whenNullMessageAndNullCauseAreProvided_thenCreatesExceptionWithBothNull() {
        // Act
        NonFatalException exception = new NonFatalException(null, null);

        // Assert
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_whenNullMessageWithAllParameters_thenCreatesExceptionWithNullMessage() {
        // Arrange
        Throwable cause = new Exception("Cause");

        // Act
        NonFatalException exception = new NonFatalException(null, cause, true, true);

        // Assert
        assertNull(exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testConstructor_whenBothSuppressionAndStackTraceAreDisabled_thenCreatesExceptionWithBothDisabled() {
        // Arrange
        String message = "No stack trace";
        Throwable cause = new RuntimeException("Cause");

        // Act
        NonFatalException exception = new NonFatalException(message, cause, false, false);

        // Assert
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}

