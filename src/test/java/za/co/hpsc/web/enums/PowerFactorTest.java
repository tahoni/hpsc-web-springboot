package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PowerFactorTest {

    @Test
    void testGetByName_withExactMatch_thenReturnsCorrectPowerFactor() {
        // Arrange
        String validName = "Minor";

        // Act
        Optional<PowerFactor> result = PowerFactor.getByName(validName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(PowerFactor.MINOR, result.get());
    }

    @Test
    void testGetByName_withCaseInsenstivieMatch_thenReturnsCorrectPowerFactor() {
        // Arrange
        String validName = "mAJor";

        // Act
        Optional<PowerFactor> result = PowerFactor.getByName(validName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(PowerFactor.MAJOR, result.get());
    }

    @Test
    void testGetByName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String invalidName = "Invalid";

        // Act
        Optional<PowerFactor> result = PowerFactor.getByName(invalidName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<PowerFactor> result = PowerFactor.getByName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withEmptyInput_thenReturnsEmptyOptional() {
        // Arrange
        String emptyName = "";

        // Act
        Optional<PowerFactor> result = PowerFactor.getByName(emptyName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withBlankInput_thenReturnsEmptyOptional() {
        // Arrange
        String blankName = "   ";

        // Act
        Optional<PowerFactor> result = PowerFactor.getByName(blankName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_withExactMatch_thenReturnsCorrectPowerFactor() {
        // Arrange
        String validAbbreviation = "Min";

        // Act
        Optional<PowerFactor> result = PowerFactor.getByAbbreviation(validAbbreviation);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(PowerFactor.MINOR, result.get());
    }

    @Test
    void testGetByAbbreviation_withCaseInsensitiveMatch_thenReturnsCorrectPowerFactor() {
        // Arrange
        String validAbbreviation = "mAJ";

        // Act
        Optional<PowerFactor> result = PowerFactor.getByAbbreviation(validAbbreviation);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(PowerFactor.MAJOR, result.get());
    }

    @Test
    void testGetByAbbreviation_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String invalidAbbreviation = "Xyz";

        // Act
        Optional<PowerFactor> result = PowerFactor.getByAbbreviation(invalidAbbreviation);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<PowerFactor> result = PowerFactor.getByAbbreviation(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByAbbreviation_withBlankInput_thenReturnsEmptyOptional() {
        // Arrange
        String blankAbbreviation = "   ";

        // Act
        Optional<PowerFactor> result = PowerFactor.getByAbbreviation(blankAbbreviation);

        // Assert
        assertFalse(result.isPresent());
    }
}