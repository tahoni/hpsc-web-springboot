package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DivisionTest {

    // fromName()
    @Test
    void testFromName_withExactName_thenReturnsCorrectDiscipline() {
        // Arrange & Act
        Optional<Division> result = Division.fromName("Open Division");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.OPEN, result.get());
    }

    @Test
    void testFromName_withCaseInsensitiveMatch_thenReturnsCorrectDivision() {
        // Arrange
        String searchName = "open division";

        // Act
        Optional<Division> result = Division.fromName(searchName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.OPEN, result.get());
    }

    @Test
    void testFromName_withPartialMatch_thenReturnsCorrectDivision() {
        // Arrange
        String searchName = "Open";

        // Act
        Optional<Division> result = Division.fromName(searchName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.OPEN, result.get());
    }

    @Test
    void testFromName_withNulInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.fromName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.fromName(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String searchName = "Nonexistent Division";

        // Act
        Optional<Division> result = Division.fromName(searchName);

        // Assert
        assertFalse(result.isPresent());
    }
}
