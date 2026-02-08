package za.co.hpsc.web.models.ipsc.disciplines;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.FirearmType;

import static org.junit.jupiter.api.Assertions.*;

class DivisionToDisciplinesForFirearmTypeMapperTest {

    @Test
    void testGetDisciplinesForFirearmType_withValidDivision_thenReturnsCorrectDisciplinesForDivision() {
        // Arrange
        FirearmType division = FirearmType.HANDGUN;

        // Act
        DisciplinesForFirearmType result =
                FirearmTypeToDisciplinesForFirearmType.getDisciplinesForFirearmType(division);

        // Assert
        assertNotNull(result);
        assertInstanceOf(DisciplinesHandgun.class, result);
        assertEquals(FirearmType.HANDGUN, result.getFirearmType());
    }

    @Test
    void testGetDisciplinesForFirearmType_withNullDivision_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                FirearmTypeToDisciplinesForFirearmType.getDisciplinesForFirearmType(null));
    }
}