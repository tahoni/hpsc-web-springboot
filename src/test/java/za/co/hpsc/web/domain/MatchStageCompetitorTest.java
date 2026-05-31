package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MatchStageCompetitorTest {

    // toString()
    @Test
    void testToString_whenMatchStageAndCompetitorAreSet_thenFormatsStageAndCompetitorWithColon() {
        // Arrange
        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageName("El Presidente");
        matchStage.setStageNumber(3);

        Competitor competitor = new Competitor();
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);

        // Act
        String result = entity.toString();

        // Assert
        assertEquals("El Presidente (3): John Doe", result);
    }

    @Test
    void testToString_whenMatchStageIsNull_thenReturnsCompetitorOnly() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setLastName("Smith");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(null);
        entity.setCompetitor(competitor);

        // Act
        String result = entity.toString();

        // Assert
        assertEquals("Jane Smith", result);
    }

    @Test
    void testToString_whenCompetitorIsNull_thenStageOnly() {
        // Arrange
        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageName("Stage One");
        matchStage.setStageNumber(1);

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(matchStage);
        entity.setCompetitor(null);

        // Act
        String result = entity.toString();

        // Assert
        assertEquals("Stage One (1)", result);
    }

    @Test
    void testToString_whenBothMatchStageAndCompetitorAreNull_thenReturnsEmptyString() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(null);
        entity.setCompetitor(null);

        // Act
        String result = entity.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenCompetitorHasMiddleName_thenIncludesMiddleNameInOutput() {
        // Arrange
        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageName("Finals");
        matchStage.setStageNumber(5);

        Competitor competitor = new Competitor();
        competitor.setFirstName("Alice");
        competitor.setMiddleNames("Grace");
        competitor.setLastName("Cooper");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);

        // Act
        String result = entity.toString();

        // Assert
        assertEquals("Finals (5): Alice Grace Cooper", result);
    }

    @Test
    void testToString_whenCalledMultipleTimes_thenReturnsConsistentResult() {
        // Arrange
        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageName("Consistent Stage");
        matchStage.setStageNumber(2);

        Competitor competitor = new Competitor();
        competitor.setFirstName("Bob");
        competitor.setLastName("Brown");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);

        // Act
        String result1 = entity.toString();
        String result2 = entity.toString();
        String result3 = entity.toString();

        // Assert
        assertEquals(result1, result2);
        assertEquals(result2, result3);
    }

    @Test
    void testToString_whenCalled_thenContainsStageInfoAndCompetitorName() {
        // Arrange
        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageName("HPSC Stage 3");
        matchStage.setStageNumber(3);

        Competitor competitor = new Competitor();
        competitor.setFirstName("Sarah");
        competitor.setLastName("Connor");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);

        // Act
        String result = entity.toString();

        // Assert
        assertEquals("HPSC Stage 3 (3): Sarah Connor", result);
    }

    // onInsert()
    @Test
    void testOnInsert_whenInvoked_thenInitializesDateCreatedAndDateUpdatedWithSameValue() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        LocalDateTime before = LocalDateTime.now();

        // Act
        entity.onInsert();
        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getDateUpdated());
        assertEquals(entity.getDateCreated(), entity.getDateUpdated());
        assertFalse(entity.getDateCreated().isBefore(before));
        assertFalse(entity.getDateCreated().isAfter(after));
    }

    // onUpdate()
    @Test
    void testOnUpdate_whenInvoked_thenUpdatesDateUpdatedWithoutChangingDateCreated() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.onInsert();
        LocalDateTime created = entity.getDateCreated();
        LocalDateTime updatedBefore = entity.getDateUpdated();

        // Act
        entity.onUpdate();

        // Assert
        assertEquals(created, entity.getDateCreated());
        assertNotNull(entity.getDateUpdated());
        assertFalse(entity.getDateUpdated().isBefore(updatedBefore));
    }
}

