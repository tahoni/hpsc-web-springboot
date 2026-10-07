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

    // getFirearmType()
    @Test
    void testGetFirearmType_withEachDivision_thenReturnsFirearmTypeItIsShotWith() {
        // Act & Assert
        assertEquals(FirearmType.HANDGUN, Division.OPEN.getFirearmType());
        assertEquals(FirearmType.HANDGUN, Division.REVOLVER.getFirearmType());
        assertEquals(FirearmType.RIFLE, Division.RIFLE_SEMI_AUTO_OPEN.getFirearmType());
        assertEquals(FirearmType.SHOTGUN, Division.SHOTGUN_STANDARD_MANUAL.getFirearmType());
        assertEquals(FirearmType.PCC, Division.PCC_IRON.getFirearmType());
        assertEquals(FirearmType.HANDGUN_22, Division.CLASSIC_22.getFirearmType());
        assertEquals(FirearmType.MINI_RIFLE, Division.MINI_RIFLE_OPEN.getFirearmType());
    }

    // names
    @Test
    void testNames_acrossAllDivisions_areUnique() {
        // Act
        long distinctNames = java.util.Arrays.stream(Division.values()).map(Division::getName).distinct().count();

        // Assert
        assertEquals(Division.values().length, distinctNames);
    }

    @Test
    void testFromName_withNonHandgunName_thenReturnsThatDivision() {
        // Act & Assert
        assertEquals(Division.SHOTGUN_OPEN, Division.fromName("Shotgun Open Division").orElseThrow());
        assertEquals(Division.OPEN_22, Division.fromName(".22 Open Division").orElseThrow());
        assertEquals(Division.MINI_RIFLE_STANDARD, Division.fromName("Mini Rifle Standard Division").orElseThrow());
    }
}
