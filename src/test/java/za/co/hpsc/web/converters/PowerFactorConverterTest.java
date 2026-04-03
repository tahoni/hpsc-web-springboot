package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.PowerFactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PowerFactorConverterTest {

    private final PowerFactorConverter converter = new PowerFactorConverter();

    @Test
    void convertToDatabaseColumnReturnsNameForMinor() {
        String databaseValue = converter.convertToDatabaseColumn(PowerFactor.MINOR);

        assertEquals("Minor", databaseValue);
    }

    @Test
    void convertToDatabaseColumnReturnsNameForMajor() {
        String databaseValue = converter.convertToDatabaseColumn(PowerFactor.MAJOR);

        assertEquals("Major", databaseValue);
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNullPowerFactor() {
        String databaseValue = converter.convertToDatabaseColumn(null);

        assertNull(databaseValue);
    }

    @Test
    void convertToEntityAttributeReturnsPowerFactorForKnownName() {
        PowerFactor entityValue = converter.convertToEntityAttribute("Minor");

        assertEquals(PowerFactor.MINOR, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsPowerFactorForCaseInsensitiveName() {
        PowerFactor entityValue = converter.convertToEntityAttribute("mAJor");

        assertEquals(PowerFactor.MAJOR, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForNullName() {
        PowerFactor entityValue = converter.convertToEntityAttribute(null);

        assertNull(entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForBlankName() {
        PowerFactor entityValue = converter.convertToEntityAttribute("   ");

        assertNull(entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForUnknownName() {
        PowerFactor entityValue = converter.convertToEntityAttribute("Unknown PowerFactor");

        assertNull(entityValue);
    }
}

