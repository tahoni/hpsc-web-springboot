package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitorCategoryTest {

    @Test
    void testGetByName_withExactMatch_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> category = CompetitorCategory.getByName("Junior");

        // Assert
        assertTrue(category.isPresent());
        assertEquals(CompetitorCategory.JUNIOR, category.get());
    }

    @Test
    void testGetByName_withCaseInsensitiveMatch_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> category = CompetitorCategory.getByName("junior");

        // Assert
        assertTrue(category.isPresent());
        assertEquals(CompetitorCategory.JUNIOR, category.get());
    }

    @Test
    void testGetByName_withWhitespace_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> category = CompetitorCategory.getByName("  Junior  ");

        // Assert
        assertTrue(category.isPresent());
        assertEquals(CompetitorCategory.JUNIOR, category.get());
    }

    @Test
    void testGetByName_withNullInput_thenReturnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> category = CompetitorCategory.getByName(null);

        // Assert
        assertTrue(category.isPresent());
        assertEquals(CompetitorCategory.NONE, category.get());
    }

    @Test
    void testGetByName_withBlankInput_thenReturnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> category = CompetitorCategory.getByName("   ");

        // Assert
        assertTrue(category.isPresent());
        assertEquals(CompetitorCategory.NONE, category.get());
    }

    @Test
    void testGetByName_withNoMatch_returnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> category = CompetitorCategory.getByName("Nonexistent Category");

        // Assert
        assertTrue(category.isPresent());
        assertEquals(CompetitorCategory.NONE, category.get());
    }

    @Test
    void testGetByName_withMatchWithSpecialCharacters_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> category = CompetitorCategory.getByName("Lady, Senior");

        // Assert
        assertTrue(category.isPresent());
        assertEquals(CompetitorCategory.SUPER_LADY, category.get());
    }
}