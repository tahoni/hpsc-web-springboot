package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MatchCategoryTest {

    // fromName()
    @Test
    void testFromName_withExactMatch_thenReturnsCorrectCategory() {
        // Arrange
        String inputName = "Club Shoot";

        // Act
        Optional<MatchCategory> result = MatchCategory.fromName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(MatchCategory.CLUB_SHOOT, result.get());
    }

    @Test
    void testFromName_withCaseInsensitiveMatch_thenReturnsCorrectCategory() {
        // Arrange
        String inputName = "league";

        // Act
        Optional<MatchCategory> result = MatchCategory.fromName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(MatchCategory.LEAGUE, result.get());
    }

    @Test
    void testFromName_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<MatchCategory> result = MatchCategory.fromName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_withBlankInput_thenReturnsEmptyOptional() {
        // Arrange
        String inputName = "   ";

        // Act
        Optional<MatchCategory> result = MatchCategory.fromName(inputName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String inputName = "NonExistentCategory";

        // Act
        Optional<MatchCategory> result = MatchCategory.fromName(inputName);

        // Assert
        assertFalse(result.isPresent());
    }
}