package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.CompetitorCategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

// TODO: standard naming
class CompetitorCategoryConverterTest {

    private final CompetitorCategoryConverter converter = new CompetitorCategoryConverter();

    @Test
    void convertToDatabaseColumnReturnsNameForValidCategory() {
        String databaseValue = converter.convertToDatabaseColumn(CompetitorCategory.SENIOR_LADY);

        assertEquals("Lady, Senior", databaseValue);
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNullCategory() {
        String databaseValue = converter.convertToDatabaseColumn(null);

        assertNull(databaseValue);
    }

    @Test
    void convertToEntityAttributeReturnsCategoryForKnownName() {
        CompetitorCategory entityValue = converter.convertToEntityAttribute("Junior");

        assertEquals(CompetitorCategory.JUNIOR, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsCategoryForCaseInsensitiveName() {
        CompetitorCategory entityValue = converter.convertToEntityAttribute("super junior");

        assertEquals(CompetitorCategory.SUPER_JUNIOR, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsCategoryForSpecialCharactersName() {
        CompetitorCategory entityValue = converter.convertToEntityAttribute("Lady, Senior");

        assertEquals(CompetitorCategory.SENIOR_LADY, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNoneForNullName() {
        CompetitorCategory entityValue = converter.convertToEntityAttribute(null);

        assertEquals(CompetitorCategory.NONE, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNoneForBlankName() {
        CompetitorCategory entityValue = converter.convertToEntityAttribute("   ");

        assertEquals(CompetitorCategory.NONE, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNoneForUnknownName() {
        CompetitorCategory entityValue = converter.convertToEntityAttribute("Unknown Category");

        assertEquals(CompetitorCategory.NONE, entityValue);
    }
}

