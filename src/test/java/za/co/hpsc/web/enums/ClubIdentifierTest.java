package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ClubIdentifierTest {
    // fromAbbreviation()
    @Test
    void testFromAbbreviation_whenAbbreviationIsExact_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromAbbreviation("HPSC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void testFromAbbreviation_whenInputIsCaseInsensitive_thenReturnsMatchingClub() {
        // Arrange
        String searchName = "sosc";

        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromAbbreviation(searchName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.SOSC, result.get());
    }

    @Test
    void testFromAbbreviation_whenInputIsNull_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromAbbreviation(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromAbbreviation_whenInputIsBlank_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromAbbreviation(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromAbbreviation_whenNoClubMatches_thenReturnsEmptyOptional() {
        // Arrange
        String searchName = "Nonexistent Club";

        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromAbbreviation(searchName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromAbbreviation_whenInputIsEmpty_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromAbbreviation("");

        // Assert
        assertFalse(result.isPresent());
    }

    // fromCode()
    @Test
    void testFromCode_whenCodeIsKnown_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromCode("A PMPSC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void testFromCode_whenInputIsNull_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromCode(null);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromCode_whenInputIsBlank_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromCode("   ");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromCode_whenNoClubMatches_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromCode("ZZZ");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromCode_whenInputIsEmpty_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromCode("");

        // Assert
        assertTrue(result.isEmpty());
    }

    // fromName()
    @Test
    void testFromName_whenClubNameIsExact_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromName("Hartbeespoortdam Practical Shooting Club");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void testFromName_whenNameIsCaseInsensitive_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromName("safari outdoor shooting club");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.SOSC, result.get());
    }

    @Test
    void testFromName_whenInputIsNull_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_whenInputIsBlank_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromName("   ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_whenInputIsEmpty_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromName("");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_whenNoClubMatches_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromName("Nonexistent Club");

        // Assert
        assertFalse(result.isPresent());
    }

    // toString()
    @Test
    void testToString_whenIdentifierIsHpsc_thenReturnsNameAndAbbreviation() {
        // Act
        String result = ClubIdentifier.HPSC.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }
}
