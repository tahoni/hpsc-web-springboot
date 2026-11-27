package za.co.hpsc.web.configs;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.exceptions.RecalculationInProgressException;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StartupRecalculationStatusTest {

    private final StartupRecalculationStatus status = new StartupRecalculationStatus();

    @Test
    void testEnsureNotRunning_whenNeverBegun_thenAllowsTheCalculation() {
        // Act & Assert
        assertFalse(status.isRunning());
        assertDoesNotThrow(status::ensureNotRunning);
    }

    @Test
    void testEnsureNotRunning_whenRunningAndCalledFromAnotherThread_thenThrowsRecalculationInProgress() {
        // Arrange
        status.begin();

        // Act & Assert
        assertTrue(status.isRunning());
        assertThrows(RecalculationInProgressException.class, status::ensureNotRunning);
    }

    @Test
    void testEnsureNotRunning_whenRunningBeforeTheThreadAttached_thenStillThrows() {
        // Arrange
        status.begin();

        // Act & Assert
        // No owner yet, so no thread may calculate: the runner's own thread has not started
        assertThrows(RecalculationInProgressException.class, status::ensureNotRunning);
    }

    @Test
    void testEnsureNotRunning_whenCalledFromTheAttachedThread_thenAllowsTheCalculation() {
        // Arrange
        status.begin();
        status.attach();

        // Act & Assert
        assertDoesNotThrow(status::ensureNotRunning);
    }

    @Test
    void testEnsureNotRunning_whenRunningAndAnotherThreadIsAttached_thenThrowsForThisThread()
            throws InterruptedException {
        // Arrange
        status.begin();
        Thread owner = new Thread(status::attach);
        owner.start();
        owner.join();

        // Act & Assert
        assertThrows(RecalculationInProgressException.class, status::ensureNotRunning);
    }

    @Test
    void testEnsureNotRunning_whenEnded_thenAllowsEveryThreadAgain() throws InterruptedException {
        // Arrange
        status.begin();
        status.attach();
        status.end();
        AtomicReference<Throwable> thrown = new AtomicReference<>();
        Thread other = new Thread(() -> {
            try {
                status.ensureNotRunning();
            } catch (RuntimeException e) {
                thrown.set(e);
            }
        });

        // Act
        other.start();
        other.join();

        // Assert
        assertFalse(status.isRunning());
        assertDoesNotThrow(status::ensureNotRunning);
        assertFalse(thrown.get() != null, () -> "Unexpected " + thrown.get());
    }

    @Test
    void testEnsureNotRunning_whenRejected_thenTheExceptionIsANonFatalExceptionWithAMessage() {
        // Arrange
        status.begin();

        // Act
        RecalculationInProgressException ex =
                assertThrows(RecalculationInProgressException.class, status::ensureNotRunning);

        // Assert
        assertInstanceOf(za.co.hpsc.web.exceptions.NonFatalException.class, ex);
        assertTrue(ex.getMessage().contains("try again"));
    }
}
