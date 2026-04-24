package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.MatchCategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

// TODO: standard naming
class MatchCategoryConverterTest {

    private final MatchCategoryConverter converter = new MatchCategoryConverter();

    @Test
    void convertToDatabaseColumnReturnsNameForValidCategory() {
        String databaseValue = converter.convertToDatabaseColumn(MatchCategory.CLUB_SHOOT);

        assertEquals("Club Shoot", databaseValue);
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNullCategory() {
        String databaseValue = converter.convertToDatabaseColumn(null);

        assertNull(databaseValue);
    }

    @Test
    void convertToEntityAttributeReturnsCategoryForKnownName() {
        MatchCategory entityValue = converter.convertToEntityAttribute("League");

        assertEquals(MatchCategory.LEAGUE, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsCategoryForCaseInsensitiveName() {
        MatchCategory entityValue = converter.convertToEntityAttribute("club shoot");

        assertEquals(MatchCategory.CLUB_SHOOT, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForNullName() {
        MatchCategory entityValue = converter.convertToEntityAttribute(null);

        assertNull(entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForBlankName() {
        MatchCategory entityValue = converter.convertToEntityAttribute("   ");

        assertNull(entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForUnknownName() {
        MatchCategory entityValue = converter.convertToEntityAttribute("Unknown Category");

        assertNull(entityValue);
    }
}

