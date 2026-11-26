package za.co.hpsc.web.configs;

import org.springframework.stereotype.Component;
import za.co.hpsc.web.exceptions.RecalculationInProgressException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Whether {@link StartupRecalculationRunner} is recalculating, so a request to calculate shooter results or shooter log
 * overall results can be refused while it is, instead of both writing the same results at once. The thread doing the
 * recalculation is the one thread that may calculate while it runs.
 *
 * @since 18.2.0
 */
@Component
public class StartupRecalculationStatus {
    private final AtomicBoolean running = new AtomicBoolean();
    private volatile Thread owner;

    /**
     * Marks the recalculation as running. Called before its thread starts, so there is no moment in which it is about
     * to run but a calculation is still allowed.
     */
    public void begin() {
        running.set(true);
    }

    /**
     * Makes the calling thread the one that is recalculating, which {@link #ensureNotRunning()} lets through.
     */
    public void attach() {
        owner = Thread.currentThread();
    }

    /**
     * Marks the recalculation as finished, whether or not it succeeded.
     */
    public void end() {
        owner = null;
        running.set(false);
    }

    /**
     * Whether the recalculation is running.
     *
     * @return {@code true} while it is.
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Refuses a calculation made while the recalculation is running, other than by the recalculation itself.
     *
     * @throws RecalculationInProgressException if the recalculation is running and the calling thread is not its own.
     */
    public void ensureNotRunning() throws RecalculationInProgressException {
        if (running.get() && (owner != Thread.currentThread())) {
            throw new RecalculationInProgressException("Results are being recalculated after the application "
                    + "started; try again shortly");
        }
    }
}
