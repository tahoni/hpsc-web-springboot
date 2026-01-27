package za.co.hpsc.web.models.division;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.models.ipsc.division.DisciplinesHandgun;
import za.co.hpsc.web.models.ipsc.division.DisciplinesInDivision;
import za.co.hpsc.web.models.ipsc.division.DivisionToDisciplinesInDivisionMapper;

import static org.junit.jupiter.api.Assertions.*;

class DivisionToDisciplinesInDivisionMapperTest {

    @Test
    void testGetDisciplinesForDivision_withValidDivision_thenReturnsCorrectDisciplinesForDivision() {
        // Arrange
        Division division = Division.HANDGUN;

        // Act
        DisciplinesInDivision result = DivisionToDisciplinesInDivisionMapper.getDisciplinesForDivision(division);

        // Assert
        assertNotNull(result);
        assertInstanceOf(DisciplinesHandgun.class, result);
        assertEquals(Division.HANDGUN, result.getDivision());
    }

    @Test
    void testGetDisciplinesForDivision_withNullDivision_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                DivisionToDisciplinesInDivisionMapper.getDisciplinesForDivision(null));
    }

    @Test
    void testGetDisciplinesForDivision_withDivisionNoMapping_thenThrowsException() {
        // Arrange
        Division unmappedDivision = Division.NONE;

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                DivisionToDisciplinesInDivisionMapper.getDisciplinesForDivision(unmappedDivision));
    }
}