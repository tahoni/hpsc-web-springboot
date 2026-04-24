package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.ipsc.dto.CompetitorDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CompetitorTest {

    // =====================================================================
    // init(CompetitorDto)
    // =====================================================================

    @Test
    void testInit_withFullyPopulatedDto_thenMapsAllFields() {
        // Arrange
        Competitor competitor = new Competitor();
        CompetitorDto dto = new CompetitorDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setMiddleNames("William");
        dto.setSapsaNumber(12345);
        dto.setCompetitorNumber("C001");
        dto.setDateOfBirth(LocalDate.of(1990, 6, 15));

        // Act
        competitor.init(dto);

        // Assert
        assertEquals("John", competitor.getFirstName());
        assertEquals("Doe", competitor.getLastName());
        assertEquals("William", competitor.getMiddleNames());
        assertEquals(12345, competitor.getSapsaNumber());
        assertEquals("C001", competitor.getCompetitorNumber());
        assertEquals(LocalDate.of(1990, 6, 15), competitor.getDateOfBirth());
    }

    @Test
    void testInit_withNullMiddleNames_thenMiddleNamesIsNull() {
        // Arrange
        Competitor competitor = new Competitor();
        CompetitorDto dto = new CompetitorDto();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setMiddleNames(null);
        dto.setCompetitorNumber("C002");

        // Act
        competitor.init(dto);

        // Assert
        assertEquals("Jane", competitor.getFirstName());
        assertEquals("Smith", competitor.getLastName());
        assertNull(competitor.getMiddleNames());
    }

    @Test
    void testInit_withNullSapsaNumber_thenSapsaNumberIsNull() {
        // Arrange
        Competitor competitor = new Competitor();
        CompetitorDto dto = new CompetitorDto();
        dto.setFirstName("Alice");
        dto.setLastName("Jones");
        dto.setSapsaNumber(null);
        dto.setCompetitorNumber("C003");

        // Act
        competitor.init(dto);

        // Assert
        assertNull(competitor.getSapsaNumber());
    }

    @Test
    void testInit_withNullDateOfBirth_thenDateOfBirthIsNull() {
        // Arrange
        Competitor competitor = new Competitor();
        CompetitorDto dto = new CompetitorDto();
        dto.setFirstName("Bob");
        dto.setLastName("Brown");
        dto.setDateOfBirth(null);
        dto.setCompetitorNumber("C004");

        // Act
        competitor.init(dto);

        // Assert
        assertNull(competitor.getDateOfBirth());
    }

    @Test
    void testInit_calledTwice_thenLastDtoValueOverwrites() {
        // Arrange
        Competitor competitor = new Competitor();

        CompetitorDto first = new CompetitorDto();
        first.setFirstName("First");
        first.setLastName("Version");
        first.setCompetitorNumber("V1");

        CompetitorDto second = new CompetitorDto();
        second.setFirstName("Second");
        second.setLastName("Version");
        second.setCompetitorNumber("V2");

        // Act
        competitor.init(first);
        competitor.init(second);

        // Assert
        assertEquals("Second", competitor.getFirstName());
        assertEquals("V2", competitor.getCompetitorNumber());
    }

    // =====================================================================
    // toString()
    // =====================================================================

    @Test
    void testToString_withFirstAndLastNameOnly_thenReturnsFirstSpaceLast() {
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
    void testToString_withMiddleName_thenReturnsFirstMiddleLast() {
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
    void testToString_withNullMiddleName_thenOmitsMiddleName() {
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
    void testToString_withBlankMiddleName_thenOmitsMiddleName() {
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
    void testToString_withEmptyMiddleName_thenOmitsMiddleName() {
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
    void testToString_withMultipleMiddleNames_thenIncludesAllMiddleNames() {
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
    void testToString_withMiddleNameContainingOnlyTab_thenOmitsMiddleName() {
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
    void testToString_afterInitWithMiddleName_thenReturnsFullName() {
        // Arrange
        Competitor competitor = new Competitor();
        CompetitorDto dto = new CompetitorDto();
        dto.setFirstName("Alice");
        dto.setMiddleNames("Grace");
        dto.setLastName("Cooper");
        dto.setCompetitorNumber("C010");
        competitor.init(dto);

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("Alice Grace Cooper", result);
    }

    @Test
    void testToString_afterInitWithoutMiddleName_thenReturnsFirstAndLastOnly() {
        // Arrange
        Competitor competitor = new Competitor();
        CompetitorDto dto = new CompetitorDto();
        dto.setFirstName("Alice");
        dto.setMiddleNames(null);
        dto.setLastName("Cooper");
        dto.setCompetitorNumber("C011");
        competitor.init(dto);

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("Alice Cooper", result);
    }

    @Test
    void testToString_calledMultipleTimes_thenReturnsConsistentResult() {
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
    void init_whenDtoIsNull_thenThrowsNullPointerException() {
        // Arrange
        Competitor competitor = new Competitor();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> competitor.init(null));
    }

    // TODO: fix method
    @Test
    void toString_whenFirstNameIsNullAndLastNamePresent_thenIncludesNullLiteralForFirstName() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName(null);
        competitor.setMiddleNames(null);
        competitor.setLastName("Doe");

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("null Doe", result);
    }

    // TODO: fix method
    @Test
    void toString_whenLastNameIsNullAndMiddleNamePresent_thenIncludesNullLiteralForLastName() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("John");
        competitor.setMiddleNames("William");
        competitor.setLastName(null);

        // Act
        String result = competitor.toString();

        // Assert
        assertEquals("John William null", result);
    }

    @Test
    void onInsert_whenInvoked_thenSetsDateCreatedAndDateUpdatedToSameCurrentValue() {
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

    @Test
    void onUpdate_whenInvoked_thenUpdatesDateUpdatedAndKeepsDateCreatedUnchanged() {
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
