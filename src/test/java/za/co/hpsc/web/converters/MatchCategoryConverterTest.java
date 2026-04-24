package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.MatchCategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MatchCategoryConverterTest {

    private final MatchCategoryConverter converter = new MatchCategoryConverter();

    @Test
    void testConvertToDatabaseColumn_whenCategoryIsValid_thenReturnsName() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(MatchCategory.CLUB_SHOOT);

        // Assert
        assertEquals("Club Shoot", databaseValue);
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
        MatchCategory entityValue = converter.convertToEntityAttribute("League");

        // Assert
        assertEquals(MatchCategory.LEAGUE, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameHasDifferentCase_thenReturnsCategory() {
        // Act
        MatchCategory entityValue = converter.convertToEntityAttribute("club shoot");

        // Assert
        assertEquals(MatchCategory.CLUB_SHOOT, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsNull_thenReturnsNull() {
        // Act
        MatchCategory entityValue = converter.convertToEntityAttribute(null);

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsBlank_thenReturnsNull() {
        // Act
        MatchCategory entityValue = converter.convertToEntityAttribute("   ");

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsUnknown_thenReturnsNull() {
        // Act
        MatchCategory entityValue = converter.convertToEntityAttribute("Unknown Category");

        // Assert
        assertNull(entityValue);
    }
}

