package za.co.hpsc.web.models.ipsc.match.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchRowTest {

    // MatchRow(MatchRequest)
    @Test
    void testConstructor_whenRequestIsNull_thenEveryValueIsEmpty() {
        // Act
        MatchRow row = new MatchRow(null);

        // Assert
        assertEquals("", row.getMatchDate());
        assertEquals("", row.getMatchName());
        assertEquals("", row.getClub());
        assertEquals("", row.getMatchFirearmType());
        assertEquals("", row.getMatchCategory());
        assertEquals("", row.getStartTime());
        assertEquals("", row.getEndTime());
        assertEquals("", row.getUrl());
    }

    @Test
    void testConstructor_whenRequestSuppliesEverything_thenDescribesItAsText() {
        // Arrange
        MatchRequest request = new MatchRequest(LocalDate.of(2026, 9, 12), "Club Championship", "Test Club",
                "HANDGUN", "CLUB_SHOOT", LocalTime.of(8, 0), LocalTime.of(17, 30), "https://example.com/matches/1");

        // Act
        MatchRow row = new MatchRow(request);

        // Assert
        assertEquals("2026-09-12", row.getMatchDate());
        assertEquals("Club Championship", row.getMatchName());
        assertEquals("Test Club", row.getClub());
        assertEquals("HANDGUN", row.getMatchFirearmType());
        assertEquals("CLUB_SHOOT", row.getMatchCategory());
        assertEquals("08:00", row.getStartTime());
        assertEquals("17:30", row.getEndTime());
        assertEquals("https://example.com/matches/1", row.getUrl());
    }

    @Test
    void testConstructor_whenValuesAreMissing_thenReportsThemAsEmpty() {
        // Arrange
        MatchRequest request = new MatchRequest(null, "Club Championship", null, null, null, null, null, null);

        // Act
        MatchRow row = new MatchRow(request);

        // Assert
        assertEquals("", row.getMatchDate());
        assertEquals("Club Championship", row.getMatchName());
        assertEquals("", row.getClub());
        assertEquals("", row.getMatchFirearmType());
        assertEquals("", row.getStartTime());
        assertEquals("", row.getUrl());
    }
}
