package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ClubIdentifierTest {
    @Test
    void getByAbbreviationReturnsMatchingClubForExactAbbreviation() {
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation("HPSC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void getByAbbreviationReturnsMatchingClubForCaseInsensitiveInput() {
        String searchName = "sosc";

        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation(searchName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.SOSC, result.get());
    }

    @Test
    void getByAbbreviationReturnsEmptyOptionalForNullInput() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getByAbbreviationReturnsEmptyOptionalForBlankInput() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getByAbbreviationReturnsEmptyOptionalWhenNoClubMatches() {
        // Arrange
        String searchName = "Nonexistent Club";

        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation(searchName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getByCodeReturnsMatchingClubForKnownCode() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("CCC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void getByCodeReturnsEmptyOptionalForNullInput() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode(null);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getByCodeReturnsEmptyOptionalForBlankInput() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("   ");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getByCodeReturnsEmptyOptionalWhenNoClubMatches() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("ZZZ");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getByNameReturnsMatchingClubForExactClubName() {
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("Hartbeespoortdam Practical Shooting Club");

        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void getByNameReturnsMatchingClubForCaseInsensitiveName() {
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("safari outdoor shooting club");

        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.SOSC, result.get());
    }
}
