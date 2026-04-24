package za.co.hpsc.web.exceptions;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FatalExceptionTest {

    @Test
    void testConstructor_whenNoArgumentsProvided_thenCreatesExceptionWithNullMessage() {
        // Act
        FatalException exception = new FatalException();

        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_whenMessageIsProvided_thenCreatesExceptionWithMessage() {
        // Arrange
        String message = "Fatal system error occurred";

        // Act
        FatalException exception = new FatalException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_whenMessageAndCauseAreProvided_thenCreatesExceptionWithBoth() {
        // Arrange
        String message = "Fatal error: Database connection failed";
        Throwable cause = new RuntimeException("Connection timeout");

        // Act
        FatalException exception = new FatalException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testConstructor_whenOnlyCauseIsProvided_thenCreatesExceptionWithCauseAsMessage() {
        // Arrange
        Throwable cause = new IOException("File system error");

        // Act
        FatalException exception = new FatalException(cause);

        // Assert
        assertSame(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("File system error"));
    }

    @Test
    void testConstructor_whenAllParametersAreProvided_thenCreatesExceptionWithAllSettings() {
        // Arrange
        String message = "Critical failure";
        Throwable cause = new Exception("Underlying cause");
        boolean enableSuppression = true;
        boolean writableStackTrace = false;

        // Act
        FatalException exception = new FatalException(message, cause, enableSuppression, writableStackTrace);

        // Assert
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testConstructor_whenSuppressionIsDisabled_thenCreatesExceptionWithSuppressionDisabled() {
        // Arrange
        String message = "Fatal error";
        Throwable cause = new RuntimeException("Cause");
        boolean enableSuppression = false;
        boolean writableStackTrace = true;

        // Act
        FatalException exception = new FatalException(message, cause, enableSuppression, writableStackTrace);

        // Assert
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testInheritance_whenInstantiated_thenIsInstanceOfException() {
        // Act
        FatalException exception = new FatalException("Test");

        // Assert
        assertInstanceOf(Exception.class, exception);
    }

    @Test
    void testInheritance_whenInstantiated_thenIsInstanceOfThrowable() {
        // Act
        FatalException exception = new FatalException("Test");

        // Assert
        assertInstanceOf(Throwable.class, exception);
    }

    @Test
    void testConstructor_whenMessageIsEmpty_thenCreatesExceptionWithEmptyMessage() {
        // Act
        FatalException exception = new FatalException("");

        // Assert
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_whenCauseChainIsProvided_thenPreservesCauseChain() {
        // Arrange
        Throwable rootCause = new RuntimeException("Root failure");
        Throwable intermediateCause = new IOException("IO error", rootCause);
        String message = "Fatal system error";

        // Act
        FatalException exception = new FatalException(message, intermediateCause);

        // Assert
        assertSame(intermediateCause, exception.getCause());
        assertSame(rootCause, intermediateCause.getCause());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testConstructor_whenNullMessageAndNullCauseAreProvided_thenCreatesExceptionWithBothNull() {
        // Act
        FatalException exception = new FatalException(null, null);

        // Assert
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructor_whenNullMessageWithAllParameters_thenCreatesExceptionWithNullMessage() {
        // Arrange
        Throwable cause = new Exception("Cause");

        // Act
        FatalException exception = new FatalException(null, cause, true, true);

        // Assert
        assertNull(exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}

