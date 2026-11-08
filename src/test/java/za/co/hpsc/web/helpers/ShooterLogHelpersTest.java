package za.co.hpsc.web.helpers;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShooterLogHelpersTest {

    // validateMatchesWithinDates()
    @Test
    void testValidateMatchesWithinDates_whenLogHasNoMatches_thenDoesNotThrow() {
        // Arrange
        ShooterLog shooterLog = shooterLog(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        // Act & Assert
        assertDoesNotThrow(() -> ShooterLogHelpers.validateMatchesWithinDates(shooterLog));
    }

    @Test
    void testValidateMatchesWithinDates_whenMatchesAreNotLoaded_thenDoesNotThrow() {
        // Arrange
        ShooterLog shooterLog = shooterLog(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));
        shooterLog.setMatches(null);

        // Act & Assert
        assertDoesNotThrow(() -> ShooterLogHelpers.validateMatchesWithinDates(shooterLog));
    }

    @Test
    void testValidateMatchesWithinDates_whenLogHasNoStartDate_thenDoesNotThrow() {
        // Arrange
        ShooterLog shooterLog = shooterLog(null, LocalDate.of(2026, 9, 30), match(10L, LocalDate.of(2026, 12, 1)));

        // Act & Assert
        assertDoesNotThrow(() -> ShooterLogHelpers.validateMatchesWithinDates(shooterLog));
    }

    @Test
    void testValidateMatchesWithinDates_whenLogHasNoEndDate_thenDoesNotThrow() {
        // Arrange
        ShooterLog shooterLog = shooterLog(LocalDate.of(2026, 9, 1), null, match(10L, LocalDate.of(2026, 12, 1)));

        // Act & Assert
        assertDoesNotThrow(() -> ShooterLogHelpers.validateMatchesWithinDates(shooterLog));
    }

    @Test
    void testValidateMatchesWithinDates_whenMatchesAreOnTheBoundaryDates_thenDoesNotThrow() {
        // Arrange
        ShooterLog shooterLog = shooterLog(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                match(10L, LocalDate.of(2026, 9, 1)), match(11L, LocalDate.of(2026, 9, 30)));

        // Act & Assert
        assertDoesNotThrow(() -> ShooterLogHelpers.validateMatchesWithinDates(shooterLog));
    }

    @Test
    void testValidateMatchesWithinDates_whenAMatchIsAfterTheEndDate_thenThrowsValidationException() {
        // Arrange
        ShooterLog shooterLog = shooterLog(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                match(10L, LocalDate.of(2026, 9, 12)), match(11L, LocalDate.of(2026, 10, 1)));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ShooterLogHelpers.validateMatchesWithinDates(shooterLog));
        assertEquals("Match with ID 11 is scheduled for 2026-10-01, outside the shooter log's dates 2026-09-01 to "
                + "2026-09-30.", exception.getMessage());
    }

    @Test
    void testValidateMatchesWithinDates_whenAMatchIsBeforeTheStartDate_thenThrowsValidationException() {
        // Arrange
        ShooterLog shooterLog = shooterLog(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                match(10L, LocalDate.of(2026, 8, 31)));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ShooterLogHelpers.validateMatchesWithinDates(shooterLog));
        assertTrue(exception.getMessage().startsWith("Match with ID 10 is scheduled for 2026-08-31"));
    }

    // isWithinDates()
    @Test
    void testIsWithinDates_whenMatchIsOnTheBoundaryDates_thenReturnsTrue() {
        // Arrange
        ShooterLog shooterLog = shooterLog(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        // Act & Assert
        assertTrue(ShooterLogHelpers.isWithinDates(match(10L, LocalDate.of(2026, 9, 1)), shooterLog));
        assertTrue(ShooterLogHelpers.isWithinDates(match(11L, LocalDate.of(2026, 9, 30)), shooterLog));
    }

    @Test
    void testIsWithinDates_whenMatchIsOutsideTheDates_thenReturnsFalse() {
        // Arrange
        ShooterLog shooterLog = shooterLog(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        // Act & Assert
        assertFalse(ShooterLogHelpers.isWithinDates(match(10L, LocalDate.of(2026, 8, 31)), shooterLog));
        assertFalse(ShooterLogHelpers.isWithinDates(match(11L, LocalDate.of(2026, 10, 1)), shooterLog));
    }

    @Test
    void testIsWithinDates_whenLogHasNoStartOrEndDate_thenReturnsTrue() {
        // Arrange
        IpscMatch match = match(10L, LocalDate.of(2030, 1, 1));

        // Act & Assert
        assertTrue(ShooterLogHelpers.isWithinDates(match, shooterLog(null, LocalDate.of(2026, 9, 30))));
        assertTrue(ShooterLogHelpers.isWithinDates(match, shooterLog(LocalDate.of(2026, 9, 1), null)));
    }

    // Helpers
    private ShooterLog shooterLog(LocalDate startDate, LocalDate endDate, IpscMatch... matches) {
        ShooterLog shooterLog = new ShooterLog();
        shooterLog.setStartDate(startDate);
        shooterLog.setEndDate(endDate);
        shooterLog.setMatches(List.of(matches));
        return shooterLog;
    }

    private IpscMatch match(Long id, LocalDate date) {
        IpscMatch match = new IpscMatch();
        match.setId(id);
                match.setScheduledDate(date.atStartOfDay());
        return match;
    }
}
