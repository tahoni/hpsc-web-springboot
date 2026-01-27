package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MatchCategoryTest {

    @Test
    void testGetByName_withExactMatch_thenReturnsCorrectCategory() {
        // Arrange
        String inputName = "Club Shoot";

        // Act
        Optional<MatchCategory> result = MatchCategory.getByName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(MatchCategory.CLUB_SHOOT, result.get());
    }

    @Test
    void testGetByName_withCaseInsensitiveMatch_thenReturnsCorrectCategory() {
        // Arrange
        String inputName = "league";

        // Act
        Optional<MatchCategory> result = MatchCategory.getByName(inputName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(MatchCategory.LEAGUE, result.get());
    }

    @Test
    void testGetByName_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<MatchCategory> result = MatchCategory.getByName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withBlankInput_thenReturnsEmptyOptional() {
        // Arrange
        String inputName = "   ";

        // Act
        Optional<MatchCategory> result = MatchCategory.getByName(inputName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetByName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String inputName = "NonExistentCategory";

        // Act
        Optional<MatchCategory> result = MatchCategory.getByName(inputName);

        // Assert
        assertFalse(result.isPresent());
    }
}