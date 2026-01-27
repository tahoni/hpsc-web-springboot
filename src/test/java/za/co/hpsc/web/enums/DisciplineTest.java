package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DisciplineTest {

    @Test
    void testGetByName_withExactName_thenReturnsCorrectDiscipline() {
        // Arrange & Act
        Optional<Discipline> result = Discipline.getByName("Open Division");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Discipline.OPEN, result.get());
    }

    @Test
    void testGetByName_withCaseInsensitiveMatch_thenReturnsCorrectDiscipline() {
        // Arrange
        String searchName = "open division";

        // Act
        Optional<Discipline> result = Discipline.getByName(searchName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Discipline.OPEN, result.get());
    }

    @Test
    void testGetByName_withPartialMatch_thenReturnsCorrectDiscipline() {
        // Arrange
        String searchName = "Open";

        // Act
        Optional<Discipline> result = Discipline.getByName(searchName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Discipline.OPEN, result.get());
    }

    @Test
    void testGetByName_withNulInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Discipline> result = Discipline.getByName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Discipline> result = Discipline.getByName(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String searchName = "Nonexistent Division";

        // Act
        Optional<Discipline> result = Discipline.getByName(searchName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_withExactMatch_thenReturnsCorrectDiscipline() {
        // Arrange & Act
        Optional<Discipline> result = Discipline.getByAbbreviation("O");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Discipline.OPEN, result.get());
    }

    @Test
    void testGetByAbbreviation_withCaseInsensitiveMatch_thenReturnsCorrectDiscipline() {
        // Arrange
        String abbreviation = "pCc";

        // Act
        Optional<Discipline> result = Discipline.getByAbbreviation(abbreviation);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Discipline.PCC_OPTICS, result.get());
    }

    @Test
    void testGetByAbbreviation_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String abbreviation = "ZZ";

        // Act
        Optional<Discipline> result = Discipline.getByAbbreviation(abbreviation);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Discipline> result = Discipline.getByAbbreviation(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Discipline> result = Discipline.getByAbbreviation(" ");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviationOrName_withExactName_thenReturnsCorrectDiscipline() {
        // Arrange & Act
        Optional<Discipline> result = Discipline.getByAbbreviationOrName("Production Optics Division");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Discipline.PRODUCTION_OPTICS, result.get());
    }

    @Test
    void testGetByAbbreviationOrName_withExactAbbreviation_thenReturnsCorrectDiscipline() {
        // Arrange & Act
        Optional<Discipline> result = Discipline.getByAbbreviationOrName("POL");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Discipline.PRODUCTION_OPTICS_LIGHT, result.get());
    }

    @Test
    void testGetByAbbreviationOrName_withPartialNameMatch_thenReturnsCorrectDiscipline() {
        // Arrange & Act
        Optional<Discipline> result = Discipline.getByAbbreviationOrName("Standard Manual");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Discipline.SHOTGUN_STANDARD_MANUAL, result.get());
    }

    @Test
    void testGetByAbbreviationOrName_withMixedCaseAbbreviation_thenReturnsCorrectDiscipline() {
        // Arrange
        String searchValue = "pCC";

        // Act
        Optional<Discipline> result = Discipline.getByAbbreviationOrName(searchValue);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Discipline.PCC_OPTICS, result.get());
    }

    @Test
    void testGetByAbbreviationOrName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String searchValue = "Nonexistent";

        // Act
        Optional<Discipline> result = Discipline.getByAbbreviationOrName(searchValue);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviationOrName_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Discipline> result = Discipline.getByAbbreviationOrName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviationOrName_withBlankInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Discipline> result = Discipline.getByAbbreviationOrName(" ");

        // Assert
        assertFalse(result.isPresent());
    }
}