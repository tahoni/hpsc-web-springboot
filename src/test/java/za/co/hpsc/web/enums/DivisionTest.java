package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DivisionTest {

    @Test
    void testGetByName_withExactName_thenReturnsCorrectDiscipline() {
        // Arrange & Act
        Optional<Division> result = Division.getByName("Open Division");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.OPEN, result.get());
    }

    @Test
    void testGetByName_withCaseInsensitiveMatch_thenReturnsCorrectDivision() {
        // Arrange
        String searchName = "open division";

        // Act
        Optional<Division> result = Division.getByName(searchName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.OPEN, result.get());
    }

    @Test
    void testGetByName_withPartialMatch_thenReturnsCorrectDivision() {
        // Arrange
        String searchName = "Open";

        // Act
        Optional<Division> result = Division.getByName(searchName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.OPEN, result.get());
    }

    @Test
    void testGetByName_withNulInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.getByName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.getByName(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String searchName = "Nonexistent Division";

        // Act
        Optional<Division> result = Division.getByName(searchName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_withExactMatch_thenReturnsCorrectDivision() {
        // Arrange & Act
        Optional<Division> result = Division.getByAbbreviation("O");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.OPEN, result.get());
    }

    @Test
    void testGetByAbbreviation_withCaseInsensitiveMatch_thenReturnsCorrectDivision() {
        // Arrange
        String abbreviation = "pCc";

        // Act
        Optional<Division> result = Division.getByAbbreviation(abbreviation);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PCC_OPTICS, result.get());
    }

    @Test
    void testGetByAbbreviation_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String abbreviation = "ZZ";

        // Act
        Optional<Division> result = Division.getByAbbreviation(abbreviation);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.getByAbbreviation(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.getByAbbreviation(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviationOrName_withExactName_thenReturnsCorrectDivision() {
        // Arrange & Act
        Optional<Division> result = Division.getByAbbreviationOrName("Production Optics Division");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PRODUCTION_OPTICS, result.get());
    }

    @Test
    void testGetByAbbreviationOrName_withExactAbbreviation_thenReturnsCorrectDivision() {
        // Arrange & Act
        Optional<Division> result = Division.getByAbbreviationOrName("POL");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PRODUCTION_OPTICS_LIGHT, result.get());
    }

    @Test
    void testGetByAbbreviationOrName_withPartialNameMatch_thenReturnsCorrectDivision() {
        // Arrange & Act
        Optional<Division> result = Division.getByAbbreviationOrName("Standard Manual");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.SHOTGUN_STANDARD_MANUAL, result.get());
    }

    @Test
    void testGetByAbbreviationOrName_withMixedCaseAbbreviation_thenReturnsCorrectDivision() {
        // Arrange
        String searchValue = "pCC";

        // Act
        Optional<Division> result = Division.getByAbbreviationOrName(searchValue);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PCC_OPTICS, result.get());
    }

    @Test
    void testGetByAbbreviationOrName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String searchValue = "Nonexistent";

        // Act
        Optional<Division> result = Division.getByAbbreviationOrName(searchValue);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviationOrName_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.getByAbbreviationOrName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviationOrName_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.getByAbbreviationOrName(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByCode_withMatch_thenReturnsCorrectDivision() {
        // Act
        Optional<Division> result = Division.getByCode(29);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PCC_OPTICS, result.get());
    }

    @Test
    void testGetByCode_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.getByCode(null);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByCode_withZeroInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.getByCode(0);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByCode_withNoMatch_returnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.getByCode(100);

        // Assert
        assertTrue(result.isEmpty());
    }
}