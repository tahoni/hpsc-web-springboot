package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetitorCategoryTest {

    @Test
    void testGetByName_withExactMatch_thenReturnsCorrectCategory() {
        // Act
        CompetitorCategory category = CompetitorCategory.getByName("Junior");

        // Assert
        assertEquals(CompetitorCategory.JUNIOR, category);
    }

    @Test
    void testGetByName_withCaseInsensitiveMatch_thenReturnsCorrectCategory() {
        // Act
        CompetitorCategory category = CompetitorCategory.getByName("junior");

        // Assert
        assertEquals(CompetitorCategory.JUNIOR, category);
    }

    @Test
    void testGetByName_withWhitespace_thenReturnsCorrectCategory() {
        // Act
        CompetitorCategory category = CompetitorCategory.getByName("  Junior  ");

        // Assert
        assertEquals(CompetitorCategory.JUNIOR, category);
    }

    @Test
    void testGetByName_withNullInput_thenReturnsNone() {
        // Act
        CompetitorCategory category = CompetitorCategory.getByName(null);

        // Assert
        assertEquals(CompetitorCategory.NONE, category);
    }

    @Test
    void testGetByName_withBlankInput_thenReturnsNone() {
        // Act
        CompetitorCategory category = CompetitorCategory.getByName("   ");

        // Assert
        assertEquals(CompetitorCategory.NONE, category);
    }

    @Test
    void testGetByName_withNoMatch_returnsNone() {
        // Act
        CompetitorCategory category = CompetitorCategory.getByName("Nonexistent Category");

        // Assert
        assertEquals(CompetitorCategory.NONE, category);
    }

    @Test
    void testGetByName_withMatchWithSpecialCharacters_thenReturnsCorrectCategory() {
        // Act
        CompetitorCategory category = CompetitorCategory.getByName("Lady, Senior");

        // Assert
        assertEquals(CompetitorCategory.SUPER_LADY, category);
    }
}