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

    @Test
    void testFromAbbreviation_whenAbbreviationIsPmpsc_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromAbbreviation("PMPSC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void testFromAbbreviation_whenAbbreviationIsVisitor_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromAbbreviation("V");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.VISITOR, result.get());
    }

    // fromCode()
    @Test
    void testFromCode_whenCodeIsKnown_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromCode("CCC");

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
    void testFromCode_whenCodeIsSosc_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromCode("AAA");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.SOSC, result.get());
    }

    @Test
    void testFromCode_whenCodeIsHpsc_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromCode("BBB");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void testFromCode_whenCodeIsVisitor_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromCode("UUU");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.VISITOR, result.get());
    }

    @Test
    void testFromCode_whenCodeIsCaseInsensitive_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromCode("ccc");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
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

    @Test
    void testFromName_whenNameIsPmpsc_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromName("Pretoria Military Practical Shooting Club");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void testFromName_whenNameIsVisitor_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromName("Visitor");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.VISITOR, result.get());
    }

    @Test
    void testFromName_whenNameIsUnknown_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.fromName("UNKNOWN");

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

    @Test
    void testToString_whenIdentifierIsSosc_thenReturnsNameAndAbbreviation() {
        // Act
        String result = ClubIdentifier.SOSC.toString();

        // Assert
        assertEquals("Safari Outdoor Shooting Club (SOSC)", result);
    }

    @Test
    void testToString_whenIdentifierIsUnknown_thenReturnsEmptyParentheses() {
        // Act
        String result = ClubIdentifier.UNKNOWN.toString();

        // Assert
        assertEquals(" ()", result);
    }
}
