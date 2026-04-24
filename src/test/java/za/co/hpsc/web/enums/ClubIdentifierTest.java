package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ClubIdentifierTest {
    @Test
    void testGetByAbbreviation_whenAbbreviationIsExact_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation("HPSC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void testGetByAbbreviation_whenInputIsCaseInsensitive_thenReturnsMatchingClub() {
        // Arrange
        String searchName = "sosc";

        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation(searchName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.SOSC, result.get());
    }

    @Test
    void testGetByAbbreviation_whenInputIsNull_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_whenInputIsBlank_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_whenNoClubMatches_thenReturnsEmptyOptional() {
        // Arrange
        String searchName = "Nonexistent Club";

        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation(searchName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByCode_whenCodeIsKnown_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("CCC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void testGetByCode_whenInputIsNull_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode(null);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByCode_whenInputIsBlank_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("   ");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByCode_whenNoClubMatches_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("ZZZ");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByName_whenClubNameIsExact_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("Hartbeespoortdam Practical Shooting Club");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void testGetByName_whenNameIsCaseInsensitive_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("safari outdoor shooting club");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.SOSC, result.get());
    }

    @Test
    void testGetByName_whenInputIsNull_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_whenInputIsBlank_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("   ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_whenInputIsEmpty_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_whenNoClubMatches_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("Nonexistent Club");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_whenNameIsPmpsc_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("Pretoria Military Practical Shooting Club");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void testGetByName_whenNameIsVisitor_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("Visitor");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.VISITOR, result.get());
    }

    @Test
    void testGetByName_whenNameIsUnknown_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("UNKNOWN");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_whenInputIsEmpty_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation("");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_whenAbbreviationIsPmpsc_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation("PMPSC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void testGetByAbbreviation_whenAbbreviationIsVisitor_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByAbbreviation("V");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.VISITOR, result.get());
    }

    @Test
    void testGetByCode_whenCodeIsSosc_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("AAA");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.SOSC, result.get());
    }

    @Test
    void testGetByCode_whenCodeIsHpsc_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("BBB");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void testGetByCode_whenCodeIsVisitor_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("UUU");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.VISITOR, result.get());
    }

    @Test
    void testGetByCode_whenCodeIsCaseInsensitive_thenReturnsMatchingClub() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("ccc");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void testGetByCode_whenInputIsEmpty_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("");

        // Assert
        assertTrue(result.isEmpty());
    }

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
