package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FirearmTypeTest {

    @Test
    void testGetByName_withExactMatch_thenReturnsCorrectFirearmType() {
        // Arrange
        String inputName = "Handgun";

        // Act
        Optional<FirearmType> result = FirearmType.getByName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(FirearmType.HANDGUN, result.get());
    }

    @Test
    void testGetByName_withCaseInsensitiveMatch_thenReturnsCorrectFirearmType() {
        // Arrange
        String inputName = "pcc";

        // Act
        Optional<FirearmType> result = FirearmType.getByName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(FirearmType.PCC, result.get());
    }

    @Test
    void testGetByName_withAlternateSeparatorMatch_thenReturnsCorrectFirearmType() {
        // Arrange
        String inputName = "Pistol-Caliber-Carbine";

        // Act
        Optional<FirearmType> result = FirearmType.getByName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(FirearmType.PCC, result.get());
    }

    @Test
    void testGetByName_withDot22_thenReturnsCorrectFirearmType() {
        // Arrange
        String inputName = ".22LR";

        // Act
        Optional<FirearmType> result = FirearmType.getByName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(FirearmType.HANDGUN_22, result.get());
    }

    @Test
    void testGetByName_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<FirearmType> result = FirearmType.getByName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withBlankInput_thenReturnsEmptyOptional() {
        // Arrange
        String inputName = "   ";

        // Act
        Optional<FirearmType> result = FirearmType.getByName(inputName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String inputName = "NonExistentFirearmType";

        // Act
        Optional<FirearmType> result = FirearmType.getByName(inputName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByCode_withMatch_thenReturnsCorrectFirearmType() {
        // Act
        Optional<FirearmType> result = FirearmType.getByCode(7);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(FirearmType.PCC, result.get());
    }

    @Test
    void testGetByCode_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<FirearmType> result = FirearmType.getByCode(null);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByCode_withZeroInput_thenReturnsEmptyOptional() {
        // Act
        Optional<FirearmType> result = FirearmType.getByCode(0);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByCode_withNoMatch_returnsEmptyOptional() {
        // Act
        Optional<FirearmType> result = FirearmType.getByCode(100);

        // Assert
        assertTrue(result.isEmpty());
    }
}