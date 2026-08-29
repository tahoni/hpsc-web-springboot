package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.Gender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GenderConverterTest {

    private final GenderConverter converter = new GenderConverter();

    // convertToDatabaseColumn()
    @Test
    void testConvertToDatabaseColumn_whenGenderIsMale_thenReturnsMaleName() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(Gender.Male);

        // Assert
        assertEquals("Male", databaseValue);
    }

    @Test
    void testConvertToDatabaseColumn_whenGenderIsFemale_thenReturnsFemaleName() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(Gender.Female);

        // Assert
        assertEquals("Female", databaseValue);
    }

    @Test
    void testConvertToDatabaseColumn_whenGenderIsNull_thenReturnsNull() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(null);

        // Assert
        assertNull(databaseValue);
    }

    // convertToEntityAttribute()
    @Test
    void testConvertToEntityAttribute_whenNameIsMale_thenReturnsMale() {
        // Act
        Gender entityValue = converter.convertToEntityAttribute("Male");

        // Assert
        assertEquals(Gender.Male, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsFemale_thenReturnsFemale() {
        // Act
        Gender entityValue = converter.convertToEntityAttribute("Female");

        // Assert
        assertEquals(Gender.Female, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameHasDifferentCase_thenReturnsGender() {
        // Act
        Gender entityValue = converter.convertToEntityAttribute("male");

        // Assert
        assertEquals(Gender.Male, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsNull_thenReturnsNull() {
        // Act
        Gender entityValue = converter.convertToEntityAttribute(null);

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsBlank_thenReturnsNull() {
        // Act
        Gender entityValue = converter.convertToEntityAttribute("   ");

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenNameIsUnknown_thenReturnsNull() {
        // Act
        Gender entityValue = converter.convertToEntityAttribute("Unknown Gender");

        // Assert
        assertNull(entityValue);
    }
}
