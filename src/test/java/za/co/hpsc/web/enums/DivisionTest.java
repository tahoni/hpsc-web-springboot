package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DivisionTest {

    @Test
    void testGetByName_withExactMatch_thenReturnsCorrectDivision() {
        // Arrange
        String inputName = "Handgun";

        // Act
        Optional<Division> result = Division.getByName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.HANDGUN, result.get());
    }

    @Test
    void testGetByName_withCaseInsensitiveMatch_thenReturnsCorrectDivision() {
        // Arrange
        String inputName = "pcc";

        // Act
        Optional<Division> result = Division.getByName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PCC, result.get());
    }

    @Test
    void testGetByName_withAlternateSeparatorMatch_thenReturnsCorrectDivision() {
        // Arrange
        String inputName = "Pistol-Caliber-Carbine";

        // Act
        Optional<Division> result = Division.getByName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.PCC, result.get());
    }

    @Test
    void testGetByName_withDot22_thenReturnsCorrectDivision() {
        // Arrange
        String inputName = ".22LR";

        // Act
        Optional<Division> result = Division.getByName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.HANDGUN_22, result.get());
    }

    @Test
    void testGetByName_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Division> result = Division.getByName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withBlankInput_thenReturnsEmptyOptional() {
        // Arrange
        String inputName = "   ";

        // Act
        Optional<Division> result = Division.getByName(inputName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String inputName = "NonExistentDivision";

        // Act
        Optional<Division> result = Division.getByName(inputName);

        // Assert
        assertFalse(result.isPresent());
    }
}