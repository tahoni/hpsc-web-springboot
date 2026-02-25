package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ClubIdentifierTest {
    @Test
    void testGetByName_withExactName_thenReturnsCorrectDiscipline() {
        // Arrange & Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName("HPSC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.HPSC, result.get());
    }

    @Test
    void testGetByName_withCaseInsensitiveMatch_thenReturnsCorrectClubReference() {
        // Arrange
        String searchName = "sosc";

        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName(searchName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.SOSC, result.get());
    }

    @Test
    void testGetByName_withNulInput_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String searchName = "Nonexistent Club";

        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByName(searchName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByCode_withMatch_thenReturnsCorrectClubReference() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("CCC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubIdentifier.PMPSC, result.get());
    }

    @Test
    void testGetByCode_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode(null);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByCode_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("   ");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByCode_withNoMatch_returnsEmptyOptional() {
        // Act
        Optional<ClubIdentifier> result = ClubIdentifier.getByCode("ZZZ");

        // Assert
        assertTrue(result.isEmpty());
    }
}
