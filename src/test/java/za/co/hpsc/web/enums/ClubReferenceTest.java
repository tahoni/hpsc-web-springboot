package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ClubReferenceTest {
    @Test
    void testGetByName_withExactName_thenReturnsCorrectDiscipline() {
        // Arrange & Act
        Optional<ClubReference> result = ClubReference.getByName("HPSC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubReference.HPSC, result.get());
    }

    @Test
    void testGetByName_withCaseInsensitiveMatch_thenReturnsCorrectClubReference() {
        // Arrange
        String searchName = "sosc";

        // Act
        Optional<ClubReference> result = ClubReference.getByName(searchName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubReference.SOSC, result.get());
    }

    @Test
    void testGetByName_withNulInput_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubReference> result = ClubReference.getByName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubReference> result = ClubReference.getByName(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String searchName = "Nonexistent Club";

        // Act
        Optional<ClubReference> result = ClubReference.getByName(searchName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByCode_withMatch_thenReturnsCorrectClubReference() {
        // Act
        Optional<ClubReference> result = ClubReference.getByCode("CCC");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ClubReference.PMPSC, result.get());
    }

    @Test
    void testGetByCode_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubReference> result = ClubReference.getByCode(null);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByCode_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubReference> result = ClubReference.getByCode("   ");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByCode_withNoMatch_returnsEmptyOptional() {
        // Act
        Optional<ClubReference> result = ClubReference.getByCode("ZZZ");

        // Assert
        assertTrue(result.isEmpty());
    }
}
