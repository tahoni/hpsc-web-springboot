package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ClubIdentifierTest {
    @Test
    void getByAbbreviationReturnsMatchingClubForExactAbbreviation() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation("HPSC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void getByAbbreviationReturnsMatchingClubForCaseInsensitiveInput() {
        // Arrange
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
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("Hartbeespoortdam Practical Shooting Club");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void getByNameReturnsMatchingClubForCaseInsensitiveName() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("safari outdoor shooting club");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.SOSC, result.get());
    }

    @Test
    void getByNameReturnsEmptyOptionalForNullInput() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getByNameReturnsEmptyOptionalForBlankInput() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("   ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getByNameReturnsEmptyOptionalForEmptyInput() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getByNameReturnsEmptyOptionalWhenNoClubMatches() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("Nonexistent Club");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getByNameReturnsMatchingClubForPmpsc() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("Pretoria Military Practical Shooting Club");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void getByNameReturnsMatchingClubForVisitor() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("Visitor");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.VISITOR, result.get());
    }

    @Test
    void getByNameReturnsEmptyOptionalForUnknown() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("UNKNOWN");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getByAbbreviationReturnsEmptyOptionalForEmptyInput() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation("");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getByAbbreviationReturnsMatchingClubForPmpsc() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation("PMPSC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void getByAbbreviationReturnsMatchingClubForVisitor() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation("V");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.VISITOR, result.get());
    }

    @Test
    void getByCodeReturnsMatchingClubForSosc() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("AAA");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.SOSC, result.get());
    }

    @Test
    void getByCodeReturnsMatchingClubForHpsc() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("BBB");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void getByCodeReturnsMatchingClubForVisitor() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("UUU");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.VISITOR, result.get());
    }

    @Test
    void getByCodeIsCaseInsensitive() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("ccc");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void getByCodeReturnsEmptyOptionalForEmptyInput() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void toStringReturnsNameAndAbbreviationForHpsc() {
        // Act
        String result = ClubIdentifier.HPSC.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }

    @Test
    void toStringReturnsNameAndAbbreviationForSosc() {
        // Act
        String result = ClubIdentifier.SOSC.toString();

        // Assert
        assertEquals("Safari Outdoor Shooting Club (SOSC)", result);
    }

    @Test
    void toStringReturnsEmptyParenthesesForUnknown() {
        // Act
        String result = ClubIdentifier.UNKNOWN.toString();

        // Assert
        assertEquals(" ()", result);
    }
}
