package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.Division;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DivisionConverterTest {

    private final DivisionConverter converter = new DivisionConverter();

    @Test
    void testConvertToDatabaseColumn_whenDivisionIsValid_thenReturnsName() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(Division.PRODUCTION_OPTICS);

        // Assert
        assertEquals("Production Optics Division", databaseValue);
    }

    @Test
    void testConvertToDatabaseColumn_whenDivisionIsNull_thenReturnsNull() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(null);

        // Assert
        assertNull(databaseValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsKnown_thenReturnsDivision() {
        // Act
        Division entityValue = converter.convertToEntityAttribute("Open Division");

        // Assert
        assertEquals(Division.OPEN, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameHasDifferentCase_thenReturnsDivision() {
        // Act
        Division entityValue = converter.convertToEntityAttribute("production optics division");

        // Assert
        assertEquals(Division.PRODUCTION_OPTICS, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsPartialMatch_thenReturnsDivision() {
        // Act
        Division entityValue = converter.convertToEntityAttribute("Standard Manual");

        // Assert
        assertEquals(Division.SHOTGUN_STANDARD_MANUAL, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsNull_thenReturnsNull() {
        // Act
        Division entityValue = converter.convertToEntityAttribute(null);

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsBlank_thenReturnsNull() {
        // Act
        Division entityValue = converter.convertToEntityAttribute("   ");

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsUnknown_thenReturnsNull() {
        // Act
        Division entityValue = converter.convertToEntityAttribute("Unknown Division");

        // Assert
        assertNull(entityValue);
    }
}

