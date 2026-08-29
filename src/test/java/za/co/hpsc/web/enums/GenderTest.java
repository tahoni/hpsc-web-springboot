package za.co.hpsc.web.enums;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenderTest {

    // fromName()
    @Test
    void testFromName_withExactMatch_thenReturnsCorrectGender() {
        // Arrange
        String validName = "Male";

        // Act
        Optional<Gender> result = Gender.fromName(validName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Gender.Male, result.get());
    }

    @Test
    void testFromName_withCaseInsensitiveMatch_thenReturnsCorrectGender() {
        // Arrange
        String validName = "fEMALE";

        // Act
        Optional<Gender> result = Gender.fromName(validName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Gender.Female, result.get());
    }

    @Test
    void testFromName_withNoMatch_thenReturnsEmptyOptional() {
        // Arrange
        String invalidName = "Invalid";

        // Act
        Optional<Gender> result = Gender.fromName(invalidName);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_withNullInput_thenReturnsEmptyOptional() {
        // Act
        Optional<Gender> result = Gender.fromName(null);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFromName_withBlankInput_thenReturnsEmptyOptional() {
        // Arrange
        String blankName = "   ";

        // Act
        Optional<Gender> result = Gender.fromName(blankName);

        // Assert
        assertFalse(result.isPresent());
    }

    // toString()
    @Test
    void testToString_whenGenderIsMale_thenReturnsMale() {
        // Act
        String result = Gender.Male.toString();

        // Assert
        assertEquals("Male", result);
    }

    @Test
    void testToString_whenGenderIsFemale_thenReturnsFemale() {
        // Act
        String result = Gender.Female.toString();

        // Assert
        assertEquals("Female", result);
    }
}
