package za.co.hpsc.web.models.ipsc.divisions;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


class FirearmTypeToDivisionsTest {

    // getDivisionsForFirearmType

    @Test
    void testGetDivisionsForFirearmType_whenFirearmTypeIsHandgun_thenReturnsDivisionsHandgun() {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.HANDGUN);
        // Assert
        assertEquals(DivisionsHandgun.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_whenFirearmTypeIsPcc_thenReturnsDivisionsPcc() {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.PCC);
        // Assert
        assertEquals(DivisionsPcc.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_whenFirearmTypeIsShotgun_thenReturnsDivisionsShotgun() {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.SHOTGUN);
        // Assert
        assertEquals(DivisionsShotgun.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_whenFirearmTypeIsRifle_thenReturnsDivisionsRifle() {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.RIFLE);
        // Assert
        assertEquals(DivisionsRifle.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_whenFirearmTypeIsHandgun22_thenReturnsDivisions22Handgun() {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.HANDGUN_22);
        // Assert
        assertEquals(Divisions22Handgun.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_whenFirearmTypeIsMiniRifle_thenReturnsDivisionsMiniRifle() {
        // Act
        DivisionsForFirearmType result = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.MINI_RIFLE);
        // Assert
        assertEquals(DivisionsMiniRifle.getInstance(), result);
    }

    @Test
    void testGetDivisionsForFirearmType_whenAllFirearmTypes_thenReturnsMappedSingletons() {
        // Act
        DivisionsForFirearmType resultHandgun = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.HANDGUN);
        DivisionsForFirearmType resultPcc = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.PCC);
        DivisionsForFirearmType resultShotgun = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.SHOTGUN);
        DivisionsForFirearmType resultRifle = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.RIFLE);
        DivisionsForFirearmType resultHandgun22 = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.HANDGUN_22);
        DivisionsForFirearmType resultMiniRifle = FirearmTypeToDivisions.getDivisionsForFirearmType(FirearmType.MINI_RIFLE);

        // Assert
        assertEquals(DivisionsHandgun.getInstance(), resultHandgun);
        assertEquals(DivisionsPcc.getInstance(), resultPcc);
        assertEquals(DivisionsShotgun.getInstance(), resultShotgun);
        assertEquals(DivisionsRifle.getInstance(), resultRifle);
        assertEquals(Divisions22Handgun.getInstance(), resultHandgun22);
        assertEquals(DivisionsMiniRifle.getInstance(), resultMiniRifle);
    }


    // getFirearmTypeFromDivision

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsOpen_thenReturnsHandgun() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.OPEN).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsStandard_thenReturnsHandgun() {
        // Act
        FirearmType result =
                FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.STANDARD).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsModified_thenReturnsHandgun() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.MODIFIED).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsClassic_thenReturnsHandgun() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.CLASSIC).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsProduction_thenReturnsHandgun() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.PRODUCTION).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsProductionOptics_thenReturnsHandgun() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.PRODUCTION_OPTICS).orElse(null);
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsProductionOpticsLight_thenReturnsHandgun() {
        // Act
        FirearmType result =
                FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.PRODUCTION_OPTICS_LIGHT).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsOptics_thenReturnsHandgun() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.OPTICS).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsRevolver_thenReturnsHandgun() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.REVOLVER).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsPccOptics_thenReturnsPcc() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.PCC_OPTICS).orElse(null);
        // Assert
        assertEquals(FirearmType.PCC, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsPccIron_thenReturnsPcc() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.PCC_IRON).orElse(null);
        // Assert
        assertEquals(FirearmType.PCC, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsShotgunOpen_thenReturnsShotgun() {
        // Act
        FirearmType result =
                FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.SHOTGUN_OPEN).orElse(null);
        // Assert
        assertEquals(FirearmType.SHOTGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsShotgunModified_thenReturnsShotgun() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.SHOTGUN_MODIFIED).orElse(null);
        // Assert
        assertEquals(FirearmType.SHOTGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsShotgunStandard_thenReturnsShotgun() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.SHOTGUN_STANDARD).orElse(null);
        // Assert
        assertEquals(FirearmType.SHOTGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsShotgunStandardManual_thenReturnsShotgun() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.SHOTGUN_STANDARD_MANUAL).orElse(null);
        // Assert
        assertEquals(FirearmType.SHOTGUN, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsRifleSemiAutoOpen_thenReturnsRifle() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.RIFLE_SEMI_AUTO_OPEN).orElse(null);
        // Assert
        assertEquals(FirearmType.RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsRifleSemiAutoStandard_thenReturnsRifle() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.RIFLE_SEMI_AUTO_STANDARD).orElse(null);
        // Assert
        assertEquals(FirearmType.RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsRifleManualActionContemporary_thenReturnsRifle() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.RIFLE_MANUAL_ACTION_CONTEMPORARY).orElse(null);
        // Assert
        assertEquals(FirearmType.RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsRifleManualActionBolt_thenReturnsRifle() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.RIFLE_MANUAL_ACTION_BOLT).orElse(null);
        // Assert
        assertEquals(FirearmType.RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsOpen22_thenReturnsHandgun22() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.OPEN_22).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN_22, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsStandard22_thenReturnsHandgun22() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.STANDARD_22).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN_22, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsClassic22_thenReturnsHandgun22() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.CLASSIC_22).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN_22, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsOptics22_thenReturnsHandgun22() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.OPTICS_22).orElse(null);
        // Assert
        assertEquals(FirearmType.HANDGUN_22, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsMiniRifleOpen_thenReturnsMiniRifle() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.MINI_RIFLE_OPEN).orElse(null);
        // Assert
        assertEquals(FirearmType.MINI_RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsMiniRifleStandard_thenReturnsMiniRifle() {
        // Act
        FirearmType result = FirearmTypeToDivisions.getFirearmTypeFromDivision(Division.MINI_RIFLE_STANDARD).orElse(null);
        // Assert
        assertEquals(FirearmType.MINI_RIFLE, result);
    }

    @Test
    void testGetFirearmTypeFromDivision_whenDivisionIsNull_thenReturnsEmpty() {
        // Act
        Optional<FirearmType> firearmTypeOptional = FirearmTypeToDivisions.getFirearmTypeFromDivision(null);
        // Assert
        assertEquals(Optional.empty(), firearmTypeOptional);
    }
}
