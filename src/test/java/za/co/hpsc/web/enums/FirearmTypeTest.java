package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FirearmTypeTest {

    // fromName()
    @Test
    void testFromName_withExactMatch_thenReturnsCorrectFirearmType() {
        // Arrange
        String inputName = "Handgun";

        // Act
        Optional<FirearmType> result = FirearmType.fromName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(FirearmType.HANDGUN, result.get());
    }

    @Test
    void testFromName_withCaseInsensitiveMatch_thenReturnsCorrectFirearmType() {
        // Arrange
        String inputName = "pcc";

        // Act
        Optional<FirearmType> result = FirearmType.fromName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(FirearmType.PCC, result.get());
    }

    @Test
    void testFromName_withAlternateSeparatorMatch_thenReturnsCorrectFirearmType() {
        // Arrange
        String inputName = "Pistol-Caliber-Carbine";

        // Act
        Optional<FirearmType> result = FirearmType.fromName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(FirearmType.PCC, result.get());
    }

    @Test
    void testFromName_withDot22_thenReturnsCorrectFirearmType() {
        // Arrange
        String inputName = ".22LR";

        // Act
        Optional<FirearmType> result = FirearmType.fromName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(FirearmType.HANDGUN_22, result.get());
    }

    @Test
    void testFromName_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<FirearmType> result = FirearmType.fromName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_withBlankInput_thenReturnsEmptyOptional() {
        // Arrange
        String inputName = "   ";

        // Act
        Optional<FirearmType> result = FirearmType.fromName(inputName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String inputName = "NonExistentFirearmType";

        // Act
        Optional<FirearmType> result = FirearmType.fromName(inputName);

        // Assert
        assertFalse(result.isPresent());
    }

    // toString()
    @Test
    void testToString_whenSingleNameConstructorUsed_thenReturnsThatName() {
        // Act
        String result = FirearmType.HANDGUN.toString();

        // Assert
        assertEquals("Handgun", result);
    }

    @Test
    void testToString_whenMultiNameConstructorUsed_thenReturnsFirstName() {
        // Act
        String result = FirearmType.PCC.toString();

        // Assert
        assertEquals("PCC", result);
    }
}