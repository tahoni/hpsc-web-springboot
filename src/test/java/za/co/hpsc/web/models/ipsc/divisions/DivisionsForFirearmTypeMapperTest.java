package za.co.hpsc.web.models.ipsc.divisions;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.FirearmType;

import static org.junit.jupiter.api.Assertions.*;

class DivisionsForFirearmTypeMapperTest {

    @Test
    void testGetDisciplinesForFirearmType_withValidDivision_thenReturnsCorrectDisciplinesForDivision() {
        // Arrange
        FirearmType division = FirearmType.HANDGUN;

        // Act
        DivisionsForFirearmType result =
                FirearmTypeToDivisions.getDivisionsForFirearmType(division);

        // Assert
        assertNotNull(result);
        assertInstanceOf(DivisionsHandgun.class, result);
        assertEquals(FirearmType.HANDGUN, result.getFirearmType());
    }

    @Test
    void testGetDisciplinesForFirearmType_withNullDivision_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                FirearmTypeToDivisions.getDivisionsForFirearmType(null));
    }
}