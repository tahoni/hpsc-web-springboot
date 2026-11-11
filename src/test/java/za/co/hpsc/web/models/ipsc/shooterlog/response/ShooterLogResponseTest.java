package za.co.hpsc.web.models.ipsc.shooterlog.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShooterLogResponseTest {

    // ShooterLogResponse(ShooterLog)
    @Test
    void testConstructor_whenLogHasMatches_thenListsTheirIds() {
        // Arrange
        IpscMatch first = new IpscMatch();
        first.setId(10L);
        IpscMatch second = new IpscMatch();
        second.setId(11L);
        ShooterLog shooterLog = shooterLog();
        shooterLog.setMatches(List.of(first, second));

        // Act
        ShooterLogResponse response = new ShooterLogResponse(shooterLog);

        // Assert
        assertEquals(5L, response.getShooterLogId());
        assertEquals(LocalDate.of(2026, 9, 1), response.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), response.getEndDate());
        assertEquals(List.of(10L, 11L), response.getMatchIds());
    }

    @Test
    void testConstructor_whenLogsMatchesWereNotLoaded_thenMatchIdsIsEmpty() {
        // Arrange
        ShooterLog shooterLog = shooterLog();
        shooterLog.setMatches(null);

        // Act
        ShooterLogResponse response = new ShooterLogResponse(shooterLog);

        // Assert
        assertTrue(response.getMatchIds().isEmpty());
    }

    // Helpers
    private ShooterLog shooterLog() {
        ShooterLog shooterLog = new ShooterLog();
        shooterLog.setId(5L);
        shooterLog.setStartDate(LocalDate.of(2026, 9, 1));
        shooterLog.setEndDate(LocalDate.of(2026, 9, 30));
        return shooterLog;
    }
}
