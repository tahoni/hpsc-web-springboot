package za.co.hpsc.web.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FatalExceptionTest {

    // FatalException()
    @Test
    void testNoArgsConstructor_whenInvoked_thenHasNoMessageOrCause() {
        // Act
        FatalException exception = new FatalException();

        // Assert
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    // FatalException(String)
    @Test
    void testMessageConstructor_whenMessageProvided_thenMessageIsSetAndCauseIsNull() {
        // Arrange
        String message = "Something went wrong";

        // Act
        FatalException exception = new FatalException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    // FatalException(String, Throwable)
    @Test
    void testMessageAndCauseConstructor_whenBothProvided_thenBothAreSet() {
        // Arrange
        String message = "Something went wrong";
        Throwable cause = new IllegalStateException("Root cause");

        // Act
        FatalException exception = new FatalException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    // FatalException(Throwable)
    @Test
    void testCauseConstructor_whenCauseProvided_thenCauseIsSetAndMessageIsCauseToString() {
        // Arrange
        Throwable cause = new IllegalStateException("Root cause");

        // Act
        FatalException exception = new FatalException(cause);

        // Assert
        assertEquals(cause, exception.getCause());
        assertEquals(cause.toString(), exception.getMessage());
    }

    // FatalException(String, Throwable, boolean, boolean)
    @Test
    void testFullConstructor_whenSuppressionAndStackTraceDisabled_thenSetsFieldsAndDisablesBoth() {
        // Arrange
        String message = "Something went wrong";
        Throwable cause = new IllegalStateException("Root cause");

        // Act
        FatalException exception = new FatalException(message, cause, false, false);
        exception.addSuppressed(new RuntimeException("Should not be recorded"));

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(0, exception.getSuppressed().length);
        assertEquals(0, exception.getStackTrace().length);
    }

    @Test
    void testFullConstructor_whenSuppressionAndStackTraceEnabled_thenBothAreRecorded() {
        // Arrange
        String message = "Something went wrong";
        Throwable cause = new IllegalStateException("Root cause");
        Throwable suppressed = new RuntimeException("Recorded");

        // Act
        FatalException exception = new FatalException(message, cause, true, true);
        exception.addSuppressed(suppressed);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertArrayEquals(new Throwable[]{suppressed}, exception.getSuppressed());
        assertTrue(exception.getStackTrace().length > 0);
    }
}
