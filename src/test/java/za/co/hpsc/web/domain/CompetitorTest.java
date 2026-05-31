package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CompetitorTest {

    // toString()
    @Test
    void testToString_whenOnlyFirstAndLastNameAreSet_thenReturnsFirstSpaceLast() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("John Doe", result);
    }

    @Test
    void testToString_whenMiddleNameIsSet_thenReturnsFirstMiddleLast() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("John");
        competitor.setMiddleNames("William");
        competitor.setLastName("Doe");

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("John William Doe", result);
    }

    @Test
    void testToString_whenMiddleNameIsNull_thenOmitsMiddleName() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setMiddleNames(null);
        competitor.setLastName("Smith");

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("Jane Smith", result);
    }

    @Test
    void testToString_whenMiddleNameIsBlank_thenOmitsMiddleName() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setMiddleNames("   ");
        competitor.setLastName("Smith");

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("Jane Smith", result);
    }

    @Test
    void testToString_whenMiddleNameIsEmpty_thenOmitsMiddleName() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setMiddleNames("");
        competitor.setLastName("Smith");

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("Jane Smith", result);
    }

    @Test
    void testToString_whenMultipleMiddleNamesAreSet_thenIncludesAllMiddleNames() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("Mary");
        competitor.setMiddleNames("Anne Louise");
        competitor.setLastName("Windsor");

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("Mary Anne Louise Windsor", result);
    }

    @Test
    void testToString_whenMiddleNameContainsOnlyTab_thenOmitsMiddleName() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("Tom");
        competitor.setMiddleNames("\t");
        competitor.setLastName("Hardy");

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("Tom Hardy", result);
    }

    @Test
    void testToString_whenCalledMultipleTimes_thenReturnsConsistentResult() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("Consistent");
        competitor.setLastName("Result");

        // Act
        String result1 = competitor.toString();
        String result2 = competitor.toString();

        // Assert
        assertEquals(result1, result2);
    }

    @Test
    void testToString_whenFirstNameIsNullAndLastNameIsPresent_thenReturnsFirstNameOnly() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName(null);
        competitor.setMiddleNames(null);
        competitor.setLastName("Doe");

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("Doe", result);
    }

    @Test
    void testToString_whenLastNameIsNullAndMiddleNameIsPresent_thenReturnsLastNameOnly() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("John");
        competitor.setMiddleNames("William");
        competitor.setLastName(null);

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("John William", result);
    }

    // onInsert()
    @Test
    void testOnInsert_whenInvoked_thenSetsDateCreatedAndDateUpdatedToSameCurrentValue() {
        // Arrange
        Competitor competitor = new Competitor();
        LocalDateTime before = LocalDateTime.now();

        // Act
        competitor.onInsert();
        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertNotNull(competitor.getDateCreated());
        assertNotNull(competitor.getDateUpdated());
        assertEquals(competitor.getDateCreated(), competitor.getDateUpdated());
        assertFalse(competitor.getDateCreated().isBefore(before));
        assertFalse(competitor.getDateCreated().isAfter(after));
    }

    // onUpdate()
    @Test
    void testOnUpdate_whenInvoked_thenUpdatesDateUpdatedAndKeepsDateCreatedUnchanged() {
        // Arrange
        Competitor competitor = new Competitor();
        LocalDateTime created = LocalDateTime.of(2026, 1, 1, 9, 0);
        LocalDateTime oldUpdated = LocalDateTime.of(2026, 1, 2, 9, 0);
        competitor.setDateCreated(created);
        competitor.setDateUpdated(oldUpdated);
        LocalDateTime before = LocalDateTime.now();

        // Act
        competitor.onUpdate();
        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertEquals(created, competitor.getDateCreated());
        assertNotNull(competitor.getDateUpdated());
        assertFalse(competitor.getDateUpdated().isBefore(before));
        assertFalse(competitor.getDateUpdated().isAfter(after));
    }
}
