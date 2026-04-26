package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.FirearmType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FirearmTypeConverterTest {

    private final FirearmTypeConverter converter = new FirearmTypeConverter();

    @Test
    void testConvertToDatabaseColumn_whenTypeHasSingleName_thenReturnsName() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(FirearmType.HANDGUN);

        // Assert
        assertEquals("Handgun", databaseValue);
    }

    @Test
    void testConvertToDatabaseColumn_whenTypeHasMultipleNames_thenReturnsFirstName() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(FirearmType.PCC);

        // Assert
        assertEquals("PCC", databaseValue);
    }

    @Test
    void testConvertToDatabaseColumn_whenTypeIsNull_thenReturnsNull() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(null);

        // Assert
        assertNull(databaseValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsKnown_thenReturnsType() {
        // Act
        FirearmType entityValue = converter.convertToEntityAttribute("Shotgun");

        // Assert
        assertEquals(FirearmType.SHOTGUN, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameHasDifferentCase_thenReturnsType() {
        // Act
        FirearmType entityValue = converter.convertToEntityAttribute("rifle");

        // Assert
        assertEquals(FirearmType.RIFLE, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsAlias_thenReturnsType() {
        // Act
        FirearmType entityValue = converter.convertToEntityAttribute("Pistol Caliber Carbine");

        // Assert
        assertEquals(FirearmType.PCC, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameUsesAlternateSeparator_thenReturnsType() {
        // Act
        FirearmType entityValue = converter.convertToEntityAttribute("Mini-Rifle");

        // Assert
        assertEquals(FirearmType.MINI_RIFLE, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsNull_thenReturnsNull() {
        // Act
        FirearmType entityValue = converter.convertToEntityAttribute(null);

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsBlank_thenReturnsNull() {
        // Act
        FirearmType entityValue = converter.convertToEntityAttribute("   ");

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsUnknown_thenReturnsNull() {
        // Act
        FirearmType entityValue = converter.convertToEntityAttribute("Unknown Type");

        // Assert
        assertNull(entityValue);
    }
}

