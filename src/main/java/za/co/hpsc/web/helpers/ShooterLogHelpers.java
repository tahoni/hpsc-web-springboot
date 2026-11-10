package za.co.hpsc.web.helpers;

import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Helper methods shared by the services that read or write a {@link ShooterLog}.
 *
 * @since 16.1.2
 */
public final class ShooterLogHelpers {

    private ShooterLogHelpers() {
        // Helper class, not to be instantiated
    }

    /**
     * Checks that every match a shooter log covers is scheduled on a date within the log's start and end dates,
     * both inclusive.
     *
     * @param shooterLog the shooter log to check; must not be null. A log without a start or end date, or without
     *                   matches, has nothing to check.
     * @throws ValidationException if a match is scheduled before the log's start date or after its end date.
     */
    public static void validateMatchesWithinDates(ShooterLog shooterLog) {
        if ((shooterLog.getStartDate() == null) || (shooterLog.getEndDate() == null)
                || (shooterLog.getMatches() == null)) {
            return;
        }

        for (IpscMatch match : shooterLog.getMatches()) {
            if (!isWithinDates(match, shooterLog)) {
                throw new ValidationException("Match with ID " + match.getId() + " is scheduled for "
                        + match.getScheduledDate().toLocalDate() + ", outside the shooter log's dates "
                        + shooterLog.getStartDate() + " to " + shooterLog.getEndDate() + ".");
            }
        }
    }

    /**
     * Whether a match is scheduled within a shooter log's start and end dates, both inclusive.
     *
     * @param match      the match to check; must not be null.
     * @param shooterLog the shooter log whose dates apply; must not be null. A log without a start or end date has
     *                   no bounds to check.
     * @return {@code true} if the match is within the log's dates, or the log has no dates.
     */
    public static boolean isWithinDates(IpscMatch match, ShooterLog shooterLog) {
        if ((shooterLog.getStartDate() == null) || (shooterLog.getEndDate() == null)) {
            return true;
        }
        LocalDate matchDate = match.getScheduledDate().toLocalDate();
        return !matchDate.isBefore(shooterLog.getStartDate()) && !matchDate.isAfter(shooterLog.getEndDate());
    }

    /**
     * Describes a match that is scheduled outside a shooter log's start and end dates, for reporting when the match
     * is read.
     *
     * @param match      the match to check; must not be null.
     * @param shooterLog the shooter log that covers the match; must not be null.
     * @return the warning, or empty if the match is within the log's dates.
     */
    public static Optional<String> describeOutsideDates(IpscMatch match, ShooterLog shooterLog) {
        if (isWithinDates(match, shooterLog)) {
            return Optional.empty();
        }
        return Optional.of("Match is scheduled for " + match.getScheduledDate().toLocalDate() + ", outside the dates "
                + shooterLog.getStartDate() + " to " + shooterLog.getEndDate() + " of shooter log with ID "
                + shooterLog.getId() + ".");
    }
}
