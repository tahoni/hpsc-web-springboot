package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DivisionTest {

    // fromAbbreviation()
    @Test
    void testFromAbbreviation_withExactMatch_thenReturnsCorrectDivision() {
        // Arrange & Act
        Optional<Division> result = Division.fromAbbreviation("O");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.OPEN, result.get());
    }

    @Test
    void testFromAbbreviation_withCaseInsensitiveMatch_thenReturnsCorrectDivision() {
        // Arrange
        String abbreviation = "pCc";

        // Act
        Optional<Division> result = Division.fromAbbreviation(abbreviation);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PCC_IRON, result.get());
    }

    @Test
    void testFromAbbreviation_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String abbreviation = "ZZ";

        // Act
        Optional<Division> result = Division.fromAbbreviation(abbreviation);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromAbbreviation_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.fromAbbreviation(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromAbbreviation_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.fromAbbreviation(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    // fromAbbreviationOrName()
    @Test
    void testFromAbbreviationOrName_withExactName_thenReturnsCorrectDivision() {
        // Arrange & Act
        Optional<Division> result = Division.fromAbbreviationOrName("Production Optics Division");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PRODUCTION_OPTICS, result.get());
    }

    @Test
    void testFromAbbreviationOrName_withExactAbbreviation_thenReturnsCorrectDivision() {
        // Arrange & Act
        Optional<Division> result = Division.fromAbbreviationOrName("POL");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PRODUCTION_OPTICS_LIGHT, result.get());
    }

    @Test
    void testFromAbbreviationOrName_withPartialNameMatch_thenReturnsCorrectDivision() {
        // Arrange & Act
        Optional<Division> result = Division.fromAbbreviationOrName("Standard Manual");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.SHOTGUN_STANDARD_MANUAL, result.get());
    }

    @Test
    void testFromAbbreviationOrName_withMixedCaseAbbreviation_thenReturnsCorrectDivision() {
        // Arrange
        String searchValue = "pCCo";

        // Act
        Optional<Division> result = Division.fromAbbreviationOrName(searchValue);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PCC_OPTICS, result.get());
    }

    @Test
    void testFromAbbreviationOrName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String searchValue = "Nonexistent";

        // Act
        Optional<Division> result = Division.fromAbbreviationOrName(searchValue);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromAbbreviationOrName_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.fromAbbreviationOrName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromAbbreviationOrName_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.fromAbbreviationOrName(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    // fromCode()
    @Test
    void testFromCode_withMatch_thenReturnsCorrectDivision() {
        // Act
        Optional<Division> result = Division.fromCode(29);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PCC_OPTICS, result.get());
    }

    @Test
    void testFromCode_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.fromCode(null);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromCode_withZeroInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.fromCode(0);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromCode_withNoMatch_returnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.fromCode(100);

        // Assert
        assertTrue(result.isEmpty());
    }

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
