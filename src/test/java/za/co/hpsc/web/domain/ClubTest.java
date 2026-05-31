package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ClubTest {

    // All args constructor
    @Test
    void testConstructor_whenNameAndAbbreviationAreProvided_thenSetsFields() {
        // Arrange & Act
        Club club = new Club("Hillcrest Practical Shooting Club", "HPSC");

        // Assert
        assertEquals("Hillcrest Practical Shooting Club", club.getName());
        assertEquals("HPSC", club.getAbbreviation());
        assertNull(club.getId());
    }

    @Test
    void testConstructor_whenNameIsNull_thenNameIsNull() {
        // Arrange & Act
        Club club = new Club(null, "HPSC");

        // Assert
        assertNull(club.getName());
        assertEquals("HPSC", club.getAbbreviation());
    }

    @Test
    void testConstructor_whenAbbreviationIsNull_thenAbbreviationIsNull() {
        // Arrange & Act
        Club club = new Club("Hillcrest Practical Shooting Club", null);

        // Assert
        assertEquals("Hillcrest Practical Shooting Club", club.getName());
        assertNull(club.getAbbreviation());
    }

    // toString()
    @Test
    void testToString_whenAbbreviationIsDifferentFromName_thenReturnsNameWithAbbreviation() {
        // Arrange
        Club club = new Club("Hillcrest Practical Shooting Club", "HPSC");

        // Act
        String result = club.toString();

        // Assert
        assertEquals("Hillcrest Practical Shooting Club (HPSC)", result);
    }

    @Test
    void testToString_whenAbbreviationIsNull_thenReturnsNameOnly() {
        // Arrange
        Club club = new Club("Hillcrest Practical Shooting Club", null);

        // Act
        String result = club.toString();

        // Assert
        assertEquals("Hillcrest Practical Shooting Club", result);
    }

    @Test
    void testToString_whenAbbreviationIsBlank_thenReturnsNameOnly() {
        // Arrange
        Club club = new Club("Hillcrest Practical Shooting Club", "   ");

        // Act
        String result = club.toString();

        // Assert
        assertEquals("Hillcrest Practical Shooting Club", result);
    }

    @Test
    void testToString_whenAbbreviationIsEmpty_thenReturnsNameOnly() {
        // Arrange
        Club club = new Club("Hillcrest Practical Shooting Club", "");

        // Act
        String result = club.toString();

        // Assert
        assertEquals("Hillcrest Practical Shooting Club", result);
    }

    @Test
    void testToString_whenAbbreviationEqualsCaseSensitiveName_thenReturnsNameOnly() {
        // Arrange
        Club club = new Club("HPSC", "HPSC");

        // Act
        String result = club.toString();

        // Assert
        assertEquals("HPSC", result);
    }

    @Test
    void testToString_whenAbbreviationEqualsNameCaseInsensitively_thenReturnsNameOnly() {
        // Arrange
        Club club = new Club("HPSC", "hpsc");

        // Act
        String result = club.toString();

        // Assert
        assertEquals("HPSC", result);
    }

    @Test
    void testToString_whenAbbreviationDiffersOnlyByCase_thenReturnsNameOnly() {
        // Arrange
        Club club = new Club("Alpha", "alpha");

        // Act
        String result = club.toString();

        // Assert
        assertEquals("Alpha", result);
    }

    @Test
    void testToString_whenNameAndAbbreviationAreDistinct_thenFormatsWithParentheses() {
        // Arrange
        Club club = new Club("South African Shooting", "SAS");

        // Act
        String result = club.toString();

        // Assert
        assertTrue(result.contains("(SAS)"));
        assertEquals("South African Shooting (SAS)", result);
    }

    @Test
    void testToString_whenCalledMultipleTimes_thenReturnsConsistentResult() {
        // Arrange
        Club club = new Club("Consistent Club", "CC");

        // Act
        String result1 = club.toString();
        String result2 = club.toString();

        // Assert
        assertEquals(result1, result2);
    }

    @Test
    void testToString_whenNameIsNullAndAbbreviationIsPresent_thenReturnsAbbreviationOnly() {
        // Arrange
        Club club = new Club(null, "AB");

        // Act
        String result = club.toString();

        // Assert
        assertEquals("(AB)", result);
    }

    // onInsert()
    @Test
    void testOnInsert_whenInvoked_thenInitializesCreatedAndUpdatedDatesToSameCurrentValue() {
        // Arrange
        Club club = new Club("Timing Club", "TC");
        LocalDateTime before = LocalDateTime.now();

        // Act
        club.onInsert();
        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertNotNull(club.getDateCreated());
        assertNotNull(club.getDateUpdated());
        assertEquals(club.getDateCreated(), club.getDateUpdated());
        assertFalse(club.getDateCreated().isBefore(before));
        assertFalse(club.getDateCreated().isAfter(after));
    }

    // onUpdate()
    @Test
    void testOnUpdate_whenInvoked_thenUpdatesDateUpdatedAndKeepsDateCreatedUnchanged() {
        // Arrange
        Club club = new Club("Update Club", "UC");
        LocalDateTime fixedCreated = LocalDateTime.of(2026, 4, 20, 9, 0);
        club.setDateCreated(fixedCreated);
        club.setDateUpdated(LocalDateTime.of(2026, 4, 20, 9, 0));
        LocalDateTime before = LocalDateTime.now();

        // Act
        club.onUpdate();
        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertEquals(fixedCreated, club.getDateCreated());
        assertNotNull(club.getDateUpdated());
        assertFalse(club.getDateUpdated().isBefore(before));
        assertFalse(club.getDateUpdated().isAfter(after));
    }
}

