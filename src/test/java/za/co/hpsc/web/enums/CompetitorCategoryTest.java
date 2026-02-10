package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitorCategoryTest {

    @Test
    void testGetByName_withExactMatch_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.getByName("Junior");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.JUNIOR, result.get());
    }

    @Test
    void testGetByName_withCaseInsensitiveMatch_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.getByName("junior");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.JUNIOR, result.get());
    }

    @Test
    void testGetByName_withWhitespace_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.getByName("  Junior  ");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.JUNIOR, result.get());
    }

    @Test
    void testGetByName_withNullInput_thenReturnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.getByName(null);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }

    @Test
    void testGetByName_withBlankInput_thenReturnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.getByName("   ");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }

    @Test
    void testGetByName_withNoMatch_returnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.getByName("Nonexistent Category");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }

    @Test
    void testGetByName_withMatchWithSpecialCharacters_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.getByName("Lady, Senior");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.SENIOR_LADY, result.get());
    }

    @Test
    void testGetByCode_withMatch_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.getByCode(7);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.SENIOR_LADY, result.get());
    }

    @Test
    void testGetByCode_withNullInput_thenReturnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.getByCode(null);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }

    @Test
    void testGetByCode_withZeroInput_thenReturnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.getByCode(0);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }

    @Test
    void testGetByCode_withNoMatch_returnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.getByCode(10);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }
}