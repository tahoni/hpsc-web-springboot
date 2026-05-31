package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MatchCompetitorTest {

    // toString()
    @Test
    void testToString_whenMatchAndCompetitorAreSet_thenFormatsMatchAndCompetitorWithColon() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("Spring Classic", LocalDateTime.of(2026, 4, 24, 10, 30)));
        matchCompetitor.setCompetitor(buildCompetitor("John", "Doe"));

        // Act
        String result = matchCompetitor.toString();

        // Assert
        assertEquals("Spring Classic (2026-04-24 10:30): John Doe", result);
    }

    @Test
    void testToString_whenCompetitorHasMiddleName_thenIncludesMiddleNameInOutput() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("Club Match", LocalDateTime.of(2026, 6, 1, 9, 0)));
        Competitor competitor = buildCompetitor("Jane", "Smith");
        competitor.setMiddleNames("Anne");
        matchCompetitor.setCompetitor(competitor);

        // Act
        String result = matchCompetitor.toString();

        // Assert
        assertTrue(result.contains("Jane Anne Smith"));
    }

    @Test
    void testToString_whenCalledMultipleTimes_thenReturnsConsistentResult() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("Consistent Match", LocalDateTime.of(2026, 4, 24, 10, 30)));
        matchCompetitor.setCompetitor(buildCompetitor("Alice", "Cooper"));

        // Act
        String result1 = matchCompetitor.toString();
        String result2 = matchCompetitor.toString();

        // Assert
        assertEquals(result1, result2);
    }

    @Test
    void testToString_whenCalled_thenContainsMatchNameInOutput() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("HPSC League Round 1", LocalDateTime.of(2026, 5, 15, 8, 0)));
        matchCompetitor.setCompetitor(buildCompetitor("Bob", "Brown"));

        // Act
        String result = matchCompetitor.toString();

        // Assert
        assertTrue(result.contains("HPSC League Round 1"));
    }

    @Test
    void testToString_whenCalled_thenContainsCompetitorNameInOutput() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("Club Shoot", LocalDateTime.of(2026, 5, 15, 8, 0)));
        matchCompetitor.setCompetitor(buildCompetitor("Bob", "Brown"));

        // Act
        String result = matchCompetitor.toString();

        // Assert
        assertTrue(result.contains("Bob Brown"));
    }

    @Test
    void testToString_whenMatchIsNull_thenReturnsCompetitorNameOnly() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(null);
        matchCompetitor.setCompetitor(buildCompetitor("John", "Doe"));

        // Act
        String result = matchCompetitor.toString();

        // Assert
        assertEquals("John Doe", result);
    }

    @Test
    void testToString_whenCompetitorIsNull_thenReturnsMatchNameOnly() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("Spring Classic", LocalDateTime.of(2026, 4, 24, 10, 30)));
        matchCompetitor.setCompetitor(null);

        // Act
        String result = matchCompetitor.toString();

        // Assert
        assertEquals("Spring Classic (2026-04-24 10:30)", result);
    }

    // onInsert()
    @Test
    void testOnInsert_whenInvoked_thenSetsDateCreatedAndDateUpdatedToSameValue() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();

        // Act
        matchCompetitor.onInsert();

        // Assert
        assertNotNull(matchCompetitor.getDateCreated());
        assertNotNull(matchCompetitor.getDateUpdated());
        assertEquals(matchCompetitor.getDateCreated(), matchCompetitor.getDateUpdated());
    }

    // onUpdate()
    @Test
    void testOnUpdate_whenInvoked_thenRefreshesDateUpdatedAndPreservesDateCreated() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.onInsert();
        LocalDateTime createdBeforeUpdate = matchCompetitor.getDateCreated();
        LocalDateTime updatedBeforeUpdate = matchCompetitor.getDateUpdated();

        // Act
        matchCompetitor.onUpdate();

        // Assert
        assertEquals(createdBeforeUpdate, matchCompetitor.getDateCreated());
        assertNotNull(matchCompetitor.getDateUpdated());
        assertFalse(matchCompetitor.getDateUpdated().isBefore(updatedBeforeUpdate));
    }

    // Helpers
    private IpscMatch buildMatch(String name, LocalDateTime scheduledDate) {
        IpscMatch match = new IpscMatch();
        match.setName(name);
        match.setScheduledDate(scheduledDate);
        return match;
    }

    private Competitor buildCompetitor(String firstName, String lastName) {
        Competitor competitor = new Competitor();
        competitor.setFirstName(firstName);
        competitor.setLastName(lastName);
        return competitor;
    }
}

