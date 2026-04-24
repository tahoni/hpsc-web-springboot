package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.PowerFactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PowerFactorConverterTest {

    private final PowerFactorConverter converter = new PowerFactorConverter();

    @Test
    void testConvertToDatabaseColumn_whenPowerFactorIsMinor_thenReturnsMinorName() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(PowerFactor.MINOR);

        // Assert
        assertEquals("Minor", databaseValue);
    }

    @Test
    void testConvertToDatabaseColumn_whenPowerFactorIsMajor_thenReturnsMajorName() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(PowerFactor.MAJOR);

        // Assert
        assertEquals("Major", databaseValue);
    }

    @Test
    void testConvertToDatabaseColumn_whenPowerFactorIsNull_thenReturnsNull() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(null);

        // Assert
        assertNull(databaseValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsKnown_thenReturnsPowerFactor() {
        // Act
        PowerFactor entityValue = converter.convertToEntityAttribute("Minor");

        // Assert
        assertEquals(PowerFactor.MINOR, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameHasDifferentCase_thenReturnsPowerFactor() {
        // Act
        PowerFactor entityValue = converter.convertToEntityAttribute("mAJor");

        // Assert
        assertEquals(PowerFactor.MAJOR, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsNull_thenReturnsNull() {
        // Act
        PowerFactor entityValue = converter.convertToEntityAttribute(null);

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsBlank_thenReturnsNull() {
        // Act
        PowerFactor entityValue = converter.convertToEntityAttribute("   ");

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsUnknown_thenReturnsNull() {
        // Act
        PowerFactor entityValue = converter.convertToEntityAttribute("Unknown PowerFactor");

        // Assert
        assertNull(entityValue);
    }
}

