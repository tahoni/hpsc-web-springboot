package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.FirearmType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FirearmTypeConverterTest {

    private final FirearmTypeConverter converter = new FirearmTypeConverter();

    @Test
    void convertToDatabaseColumnReturnsNameForSingleNameType() {
        String databaseValue = converter.convertToDatabaseColumn(FirearmType.HANDGUN);

        assertEquals("Handgun", databaseValue);
    }

    @Test
    void convertToDatabaseColumnReturnsFirstNameForMultiNameType() {
        String databaseValue = converter.convertToDatabaseColumn(FirearmType.PCC);

        assertEquals("PCC", databaseValue);
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNullType() {
        String databaseValue = converter.convertToDatabaseColumn(null);

        assertNull(databaseValue);
    }

    @Test
    void convertToEntityAttributeReturnsTypeForKnownName() {
        FirearmType entityValue = converter.convertToEntityAttribute("Shotgun");

        assertEquals(FirearmType.SHOTGUN, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsTypeForCaseInsensitiveName() {
        FirearmType entityValue = converter.convertToEntityAttribute("rifle");

        assertEquals(FirearmType.RIFLE, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsTypeForAliasName() {
        FirearmType entityValue = converter.convertToEntityAttribute("Pistol Caliber Carbine");

        assertEquals(FirearmType.PCC, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsTypeForAlternateSeparator() {
        FirearmType entityValue = converter.convertToEntityAttribute("Mini-Rifle");

        assertEquals(FirearmType.MINI_RIFLE, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForNullName() {
        FirearmType entityValue = converter.convertToEntityAttribute(null);

        assertNull(entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForBlankName() {
        FirearmType entityValue = converter.convertToEntityAttribute("   ");

        assertNull(entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForUnknownName() {
        FirearmType entityValue = converter.convertToEntityAttribute("Unknown Type");

        assertNull(entityValue);
    }
}

