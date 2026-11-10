package za.co.hpsc.web.models.ipsc.shooterlog.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShooterLogRowTest {

    @Test
    void testConstructor_whenRequestIsNull_thenEveryValueIsEmpty() {
        // Act
        ShooterLogRow row = new ShooterLogRow(null);

        // Assert
        assertEquals("", row.getStartDate());
        assertEquals("", row.getEndDate());
        assertEquals("", row.getMatchIds());
    }

    @Test
    void testConstructor_whenRequestSuppliesEverything_thenDescribesItAsText() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                List.of(10L, 11L));

        // Act
        ShooterLogRow row = new ShooterLogRow(request);

        // Assert
        assertEquals("2026-09-01", row.getStartDate());
        assertEquals("2026-09-30", row.getEndDate());
        assertEquals("10;11", row.getMatchIds());
    }

    @Test
    void testConstructor_whenValuesAreMissing_thenReportsThemAsEmpty() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(null, LocalDate.of(2026, 9, 30),
                Arrays.asList(10L, null, 12L));

        // Act
        ShooterLogRow row = new ShooterLogRow(request);

        // Assert
        assertEquals("", row.getStartDate());
        assertEquals("2026-09-30", row.getEndDate());
        assertEquals("10;;12", row.getMatchIds());
    }

    @Test
    void testConstructor_whenMatchIdsAreNull_thenMatchIdsIsEmpty() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest();
        request.setMatchIds(null);

        // Act & Assert
        assertEquals("", new ShooterLogRow(request).getMatchIds());
    }
}
