package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.CompetitorCategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CompetitorCategoryConverterTest {

    private final CompetitorCategoryConverter converter = new CompetitorCategoryConverter();

    @Test
    void testConvertToDatabaseColumn_whenCategoryIsValid_thenReturnsName() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(CompetitorCategory.SENIOR_LADY);

        // Assert
        assertEquals("Lady, Senior", databaseValue);
    }

    @Test
    void testConvertToDatabaseColumn_whenCategoryIsNull_thenReturnsNull() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(null);

        // Assert
        assertNull(databaseValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsKnown_thenReturnsCategory() {
        // Act
        CompetitorCategory entityValue = converter.convertToEntityAttribute("Junior");

        // Assert
        assertEquals(CompetitorCategory.JUNIOR, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameHasDifferentCase_thenReturnsCategory() {
        // Act
        CompetitorCategory entityValue = converter.convertToEntityAttribute("super junior");

        // Assert
        assertEquals(CompetitorCategory.SUPER_JUNIOR, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameContainsSpecialCharacters_thenReturnsCategory() {
        // Act
        CompetitorCategory entityValue = converter.convertToEntityAttribute("Lady, Senior");

        // Assert
        assertEquals(CompetitorCategory.SENIOR_LADY, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsNull_thenReturnsNone() {
        // Act
        CompetitorCategory entityValue = converter.convertToEntityAttribute(null);

        // Assert
        assertEquals(CompetitorCategory.NONE, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsBlank_thenReturnsNone() {
        // Act
        CompetitorCategory entityValue = converter.convertToEntityAttribute("   ");

        // Assert
        assertEquals(CompetitorCategory.NONE, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsUnknown_thenReturnsNone() {
        // Act
        CompetitorCategory entityValue = converter.convertToEntityAttribute("Unknown Category");

        // Assert
        assertEquals(CompetitorCategory.NONE, entityValue);
    }
}

