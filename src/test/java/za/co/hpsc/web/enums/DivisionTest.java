package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DivisionTest {

    // fromName()
    @Test
    void testFromName_withExactName_thenReturnsCorrectDiscipline() {
        // Arrange & Act
        Optional<Division> result = Division.fromName("Open");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Division.OPEN, result.get());
    }

    @Test
    void testFromName_withCaseInsensitiveMatch_thenReturnsCorrectDivision() {
        // Arrange
        String searchName = "open";

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
    void testGetFirearmType_withEachDivision_thenReturnsNonNullFirearmType() {
        // Act & Assert
        for (Division division : Division.values()) {
            assertNotNull(division.getFirearmType(), division.name());
        }
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
        assertEquals(Division.SHOTGUN_OPEN, Division.fromName("Shotgun Open").orElseThrow());
        assertEquals(Division.OPEN_22, Division.fromName(".22 Open").orElseThrow());
        assertEquals(Division.MINI_RIFLE_STANDARD, Division.fromName("Mini Rifle Standard").orElseThrow());
    }
}
