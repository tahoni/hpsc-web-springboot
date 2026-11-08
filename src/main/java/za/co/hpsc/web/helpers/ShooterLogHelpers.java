package za.co.hpsc.web.helpers;

import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.exceptions.ValidationException;

import java.time.LocalDate;

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
            LocalDate matchDate = match.getScheduledDate().toLocalDate();
            if (matchDate.isBefore(shooterLog.getStartDate()) || matchDate.isAfter(shooterLog.getEndDate())) {
                throw new ValidationException("Match with ID " + match.getId() + " is scheduled for " + matchDate
                        + ", outside the shooter log's dates " + shooterLog.getStartDate() + " to "
                        + shooterLog.getEndDate() + ".");
            }
        }
    }
}
