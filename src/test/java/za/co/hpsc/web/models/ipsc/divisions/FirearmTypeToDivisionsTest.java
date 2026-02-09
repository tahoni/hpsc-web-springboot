package za.co.hpsc.web.models.ipsc.divisions;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.exceptions.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FirearmTypeToDivisionsTest {

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnHandgun_whenDivisionIsOpen() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.OPEN);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnHandgun_whenDivisionIsStandard() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.STANDARD);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnHandgun_whenDivisionIsModified() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.MODIFIED);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnHandgun_whenDivisionIsClassic() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.CLASSIC);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnHandgun_whenDivisionIsProduction() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.PRODUCTION);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnHandgun_whenDivisionIsProductionOptics() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.PRODUCTION_OPTICS);
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnHandgun_whenDivisionIsProductionOpticsLight() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.PRODUCTION_OPTICS_LIGHT);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnHandgun_whenDivisionIsOptics() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.OPTICS);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnHandgun_whenDivisionIsRevolver() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.REVOLVER);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnPcc_whenDivisionIsPccOptics() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.PCC_OPTICS);
        // Assert
        assertEquals(FirearmType.PCC, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnPcc_whenDivisionIsPccIron() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.PCC_IRON);
        // Assert
        assertEquals(FirearmType.PCC, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnShotgun_whenDivisionIsShotgunOpen() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.SHOTGUN_OPEN);
        // Assert
        assertEquals(FirearmType.SHOTGUN, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnShotgun_whenDivisionIsShotgunModified() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.SHOTGUN_MODIFIED);
        // Assert
        assertEquals(FirearmType.SHOTGUN, result);
    }

    @Test
    void testTestGetFirearmTypeFromDivision_shouldReturnShotgun_whenDivisionIsShotgunStandard() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.SHOTGUN_STANDARD);
        // Assert
        assertEquals(FirearmType.SHOTGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldReturnShotgun_whenDivisionIsShotgunStandardManual() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.SHOTGUN_STANDARD_MANUAL);
        // Assert
        assertEquals(FirearmType.SHOTGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldReturnRifle_whenDivisionIsRifleSemiAutoOpen() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.RIFLE_SEMI_AUTO_OPEN);
        // Assert
        assertEquals(FirearmType.RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldReturnRifle_whenDivisionIsRifleSemiAutoStandard() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.RIFLE_SEMI_AUTO_STANDARD);
        // Assert
        assertEquals(FirearmType.RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldReturnRifle_whenDivisionIsRifleManualActionContemporary() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.RIFLE_MANUAL_ACTION_CONTEMPORARY);
        // Assert
        assertEquals(FirearmType.RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldReturnRifle_whenDivisionIsRifleManualActionBolt() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.RIFLE_MANUAL_ACTION_BOLT);
        // Assert
        assertEquals(FirearmType.RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldReturnHandgun22_whenDivisionIsOpen22() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.OPEN_22);
        // Assert
        assertEquals(FirearmType.HANDGUN_22, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldReturnHandgun22_whenDivisionIsStandard22() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.STANDARD_22);
        // Assert
        assertEquals(FirearmType.HANDGUN_22, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldReturnHandgun22_whenDivisionIsClassic22() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.CLASSIC_22);
        // Assert
        assertEquals(FirearmType.HANDGUN_22, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldReturnHandgun22_whenDivisionIsOptics22() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.OPTICS_22);
        // Assert
        assertEquals(FirearmType.HANDGUN_22, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldReturnMiniRifle_whenDivisionIsMiniRifleOpen() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.MINI_RIFLE_OPEN);
        // Assert
        assertEquals(FirearmType.MINI_RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldReturnMiniRifle_whenDivisionIsMiniRifleStandard() throws ValidationException {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.MINI_RIFLE_STANDARD);
        // Assert
        assertEquals(FirearmType.MINI_RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_shouldThrowValidationException_whenDivisionIsNull() {
        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            FirearmTypeToDivisions.getFirearmTypeFromDivision(null);
        });
        // Assert
        assertEquals("Division cannot be null", exception.getMessage());
    }

    @Test
    void testGetDivisionsForFirearmType_shouldReturnDivisionsHandgun_whenFirearmTypeIsHandgun() throws ValidationException {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.HANDGUN);
        // Assert
        assertEquals(DivisionsHandgun.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_shouldReturnDivisionsPcc_whenFirearmTypeIsPcc() throws ValidationException {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.PCC);
        // Assert
        assertEquals(DivisionsPcc.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_shouldReturnDivisionsShotgun_whenFirearmTypeIsShotgun() throws ValidationException {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.SHOTGUN);
        // Assert
        assertEquals(DivisionsShotgun.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_shouldReturnDivisionsRifle_whenFirearmTypeIsRifle() throws ValidationException {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.RIFLE);
        // Assert
        assertEquals(DivisionsRifle.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_shouldReturnDivisions22Handgun_whenFirearmTypeIsHandgun22() throws ValidationException {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.HANDGUN_22);
        // Assert
        assertEquals(Divisions22Handgun.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_shouldReturnDivisionsMiniRifle_whenFirearmTypeIsMiniRifle() throws ValidationException {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.MINI_RIFLE);
        // Assert
        assertEquals(DivisionsMiniRifle.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_shouldThrowValidationException_whenFirearmTypeIsNull() {
        // Act
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            FirearmTypeToDivisions.getDivisionsForFirearmType(null);
        });
        // Assert
        assertEquals("Firearm type cannot be null", exception.getMessage());
    }
}