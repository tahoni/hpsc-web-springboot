package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitorCategoryTest {

    // fromCode()
    @Test
    void testFromCode_withMatch_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.fromCode(7);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.SENIOR_LADY, result.get());
    }

    @Test
    void testFromCode_withNullInput_thenReturnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.fromCode(null);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }

    @Test
    void testFromCode_withZeroInput_thenReturnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.fromCode(0);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }

    @Test
    void testFromCode_withNoMatch_returnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.fromCode(10);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }

    // fromName()
    @Test
    void testFromName_withExactMatch_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.fromName("Junior");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.JUNIOR, result.get());
    }

    @Test
    void testFromName_withCaseInsensitiveMatch_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.fromName("junior");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.JUNIOR, result.get());
    }

    @Test
    void testFromName_withWhitespace_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.fromName("  Junior  ");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.JUNIOR, result.get());
    }

    @Test
    void testFromName_withNullInput_thenReturnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.fromName(null);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }

    @Test
    void testFromName_withBlankInput_thenReturnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.fromName("   ");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }

    @Test
    void testFromName_withNoMatch_returnsNoneCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.fromName("Nonexistent Category");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.NONE, result.get());
    }

    @Test
    void testFromName_withMatchWithSpecialCharacters_thenReturnsCorrectCategory() {
        // Act
        Optional<CompetitorCategory> result = CompetitorCategory.fromName("Lady, Senior");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(CompetitorCategory.SENIOR_LADY, result.get());
    }
}