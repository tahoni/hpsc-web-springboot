package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ClubTest {

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

    @Test
    void testConstructor_whenClubDtoIsProvided_thenMapsAllFields() {
        // Arrange
        ClubDto dto = new ClubDto();
        dto.setId(5L);
        dto.setName("Alpha Club");
        dto.setAbbreviation("AC");

        // Act
        Club club = new Club(dto);

        // Assert
        assertEquals(5L, club.getId());
        assertEquals("Alpha Club", club.getName());
        assertEquals("AC", club.getAbbreviation());
    }

    @Test
    void testConstructor_whenClubDtoHasNullId_thenIdIsNull() {
        // Arrange
        ClubDto dto = new ClubDto();
        dto.setName("Beta Club");
        dto.setAbbreviation("BC");

        // Act
        Club club = new Club(dto);

        // Assert
        assertNull(club.getId());
        assertEquals("Beta Club", club.getName());
        assertEquals("BC", club.getAbbreviation());
    }

    @Test
    void testConstructor_whenClubDtoHasNullFields_thenFieldsAreNull() {
        // Arrange
        ClubDto dto = new ClubDto();
        dto.setId(null);
        dto.setName(null);
        dto.setAbbreviation(null);

        // Act
        Club club = new Club(dto);

        // Assert
        assertNull(club.getId());
        assertNull(club.getName());
        assertNull(club.getAbbreviation());
    }

    @Test
    void testInit_whenNameAndAbbreviationAreValid_thenUpdatesFields() {
        // Arrange
        Club club = new Club("Old Name", "ON");
        ClubDto dto = new ClubDto();
        dto.setName("New Name");
        dto.setAbbreviation("NN");

        // Act
        club.init(dto);

        // Assert
        assertEquals("New Name", club.getName());
        assertEquals("NN", club.getAbbreviation());
    }

    @Test
    void testInit_whenDtoIsNull_thenFieldsAreUnchanged() {
        // Arrange
        Club club = new Club("Unchanged Name", "UN");

        // Act
        club.init(null);

        // Assert
        assertEquals("Unchanged Name", club.getName());
        assertEquals("UN", club.getAbbreviation());
    }

    @Test
    void testInit_whenDtoNameIsNull_thenNameIsRetained() {
        // Arrange
        Club club = new Club("Retained Name", "RN");
        ClubDto dto = new ClubDto();
        dto.setName(null);
        dto.setAbbreviation("NEW");

        // Act
        club.init(dto);

        // Assert
        assertEquals("Retained Name", club.getName());
        assertEquals("NEW", club.getAbbreviation());
    }

    @Test
    void testInit_whenDtoNameIsBlank_thenNameIsRetained() {
        // Arrange
        Club club = new Club("Retained Name", "RN");
        ClubDto dto = new ClubDto();
        dto.setName("   ");
        dto.setAbbreviation("NEW");

        // Act
        club.init(dto);

        // Assert
        assertEquals("Retained Name", club.getName());
        assertEquals("NEW", club.getAbbreviation());
    }

    @Test
    void testInit_whenDtoNameIsEmpty_thenNameIsRetained() {
        // Arrange
        Club club = new Club("Retained Name", "RN");
        ClubDto dto = new ClubDto();
        dto.setName("");
        dto.setAbbreviation("NEW");

        // Act
        club.init(dto);

        // Assert
        assertEquals("Retained Name", club.getName());
    }

    @Test
    void testInit_whenDtoAbbreviationIsNull_thenAbbreviationIsRetained() {
        // Arrange
        Club club = new Club("Club Name", "OLD");
        ClubDto dto = new ClubDto();
        dto.setName("New Name");
        dto.setAbbreviation(null);

        // Act
        club.init(dto);

        // Assert
        assertEquals("New Name", club.getName());
        assertEquals("OLD", club.getAbbreviation());
    }

    @Test
    void testInit_whenDtoAbbreviationIsBlank_thenAbbreviationIsRetained() {
        // Arrange
        Club club = new Club("Club Name", "OLD");
        ClubDto dto = new ClubDto();
        dto.setName("New Name");
        dto.setAbbreviation("   ");

        // Act
        club.init(dto);

        // Assert
        assertEquals("OLD", club.getAbbreviation());
    }

    @Test
    void testInit_whenDtoAbbreviationIsEmpty_thenAbbreviationIsRetained() {
        // Arrange
        Club club = new Club("Club Name", "OLD");
        ClubDto dto = new ClubDto();
        dto.setName("New Name");
        dto.setAbbreviation("");

        // Act
        club.init(dto);

        // Assert
        assertEquals("OLD", club.getAbbreviation());
    }

    @Test
    void testInit_whenBothDtoFieldsAreBlank_thenBothFieldsAreRetained() {
        // Arrange
        Club club = new Club("Retained Name", "RN");
        ClubDto dto = new ClubDto();
        dto.setName("   ");
        dto.setAbbreviation("   ");

        // Act
        club.init(dto);

        // Assert
        assertEquals("Retained Name", club.getName());
        assertEquals("RN", club.getAbbreviation());
    }

    @Test
    void testInit_whenCalledMultipleTimes_thenLastNonBlankValueWins() {
        // Arrange
        Club club = new Club("Original", "OR");

        ClubDto first = new ClubDto();
        first.setName("Second");
        first.setAbbreviation("SC");

        ClubDto second = new ClubDto();
        second.setName("Third");
        second.setAbbreviation("   "); // blank — should retain "SC"

        // Act
        club.init(first);
        club.init(second);

        // Assert
        assertEquals("Third", club.getName());
        assertEquals("SC", club.getAbbreviation());
    }

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
    void testToString_whenNameIsChangedViaInit_thenReflectsUpdatedName() {
        // Arrange
        Club club = new Club("Original Name", "ON");
        ClubDto dto = new ClubDto();
        dto.setName("Updated Name");
        dto.setAbbreviation("UN");
        club.init(dto);

        // Act
        String result = club.toString();

        // Assert
        assertEquals("Updated Name (UN)", result);
    }

    @Test
    void testConstructor_whenClubDtoIsNull_thenThrowsNullPointerException() {
        // Arrange, Act & Assert
        assertThrows(NullPointerException.class, () -> new Club(null));
    }

    @Test
    void testInit_whenDtoValuesContainWhitespace_thenAppliesValuesWithoutTrimming() {
        // Arrange
        Club club = new Club("Original Name", "ON");
        ClubDto dto = new ClubDto();
        dto.setName("  Updated Name  ");
        dto.setAbbreviation("  UN  ");

        // Act
        club.init(dto);

        // Assert
        assertEquals("  Updated Name  ", club.getName());
        assertEquals("  UN  ", club.getAbbreviation());
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

