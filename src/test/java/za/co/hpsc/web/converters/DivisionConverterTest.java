package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.Division;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

// TODO: standard naming
class DivisionConverterTest {

    private final DivisionConverter converter = new DivisionConverter();

    @Test
    void convertToDatabaseColumnReturnsNameForValidDivision() {
        String databaseValue = converter.convertToDatabaseColumn(Division.PRODUCTION_OPTICS);

        assertEquals("Production Optics Division", databaseValue);
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNullDivision() {
        String databaseValue = converter.convertToDatabaseColumn(null);

        assertNull(databaseValue);
    }

    @Test
    void convertToEntityAttributeReturnsDivisionForKnownName() {
        Division entityValue = converter.convertToEntityAttribute("Open Division");

        assertEquals(Division.OPEN, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsDivisionForCaseInsensitiveName() {
        Division entityValue = converter.convertToEntityAttribute("production optics division");

        assertEquals(Division.PRODUCTION_OPTICS, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsDivisionForPartialName() {
        Division entityValue = converter.convertToEntityAttribute("Standard Manual");

        assertEquals(Division.SHOTGUN_STANDARD_MANUAL, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForNullName() {
        Division entityValue = converter.convertToEntityAttribute(null);

        assertNull(entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForBlankName() {
        Division entityValue = converter.convertToEntityAttribute("   ");

        assertNull(entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForUnknownName() {
        Division entityValue = converter.convertToEntityAttribute("Unknown Division");

        assertNull(entityValue);
    }
}

