package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PowerFactorTest {

    // fromAbbreviation()
    @Test
    void testFromAbbreviation_withExactMatch_thenReturnsCorrectPowerFactor() {
        // Arrange
        String validAbbreviation = "Min";

        // Act
        Optional<PowerFactor> result = PowerFactor.fromAbbreviation(validAbbreviation);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(PowerFactor.MINOR, result.get());
    }

    @Test
    void testFromAbbreviation_withCaseInsensitiveMatch_thenReturnsCorrectPowerFactor() {
        // Arrange
        String validAbbreviation = "mAJ";

        // Act
        Optional<PowerFactor> result = PowerFactor.fromAbbreviation(validAbbreviation);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(PowerFactor.MAJOR, result.get());
    }

    @Test
    void testFromAbbreviation_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String invalidAbbreviation = "Xyz";

        // Act
        Optional<PowerFactor> result = PowerFactor.fromAbbreviation(invalidAbbreviation);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromAbbreviation_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<PowerFactor> result = PowerFactor.fromAbbreviation(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromAbbreviation_withBlankInput_thenReturnsEmptyOptional() {
        // Arrange
        String blankAbbreviation = "   ";

        // Act
        Optional<PowerFactor> result = PowerFactor.fromAbbreviation(blankAbbreviation);

        // Assert
        assertFalse(result.isPresent());
    }

    // fromName()
    @Test
    void testFromName_withExactMatch_thenReturnsCorrectPowerFactor() {
        // Arrange
        String validName = "Minor";

        // Act
        Optional<PowerFactor> result = PowerFactor.fromName(validName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(PowerFactor.MINOR, result.get());
    }

    @Test
    void testFromName_withCaseInsensitiveMatch_thenReturnsCorrectPowerFactor() {
        // Arrange
        String validName = "mAJor";

        // Act
        Optional<PowerFactor> result = PowerFactor.fromName(validName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(PowerFactor.MAJOR, result.get());
    }

    @Test
    void testFromName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String invalidName = "Invalid";

        // Act
        Optional<PowerFactor> result = PowerFactor.fromName(invalidName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<PowerFactor> result = PowerFactor.fromName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_withEmptyInput_thenReturnsEmptyOptional() {
        // Arrange
        String emptyName = "";

        // Act
        Optional<PowerFactor> result = PowerFactor.fromName(emptyName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_withBlankInput_thenReturnsEmptyOptional() {
        // Arrange
        String blankName = "   ";

        // Act
        Optional<PowerFactor> result = PowerFactor.fromName(blankName);

        // Assert
        assertFalse(result.isPresent());
    }
}
