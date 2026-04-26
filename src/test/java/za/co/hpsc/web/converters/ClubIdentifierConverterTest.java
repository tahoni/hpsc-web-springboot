package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.ClubIdentifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClubIdentifierConverterTest {

    private final ClubIdentifierConverter converter = new ClubIdentifierConverter();

    @Test
    void testConvertToDatabaseColumn_whenClubIdentifierIsValid_thenReturnsAbbreviation() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(ClubIdentifier.HPSC);

        // Assert
        assertEquals("HPSC", databaseValue);
    }

    @Test
    void testConvertToDatabaseColumn_whenClubIdentifierIsNull_thenReturnsNull() {
        // Act
        String databaseValue = converter.convertToDatabaseColumn(null);

        // Assert
        assertNull(databaseValue);
    }

    @Test
    void testConvertToEntityAttribute_whenAbbreviationIsKnown_thenReturnsClubIdentifier() {
        // Act
        ClubIdentifier entityValue = converter.convertToEntityAttribute("SOSC");

        // Assert
        assertEquals(ClubIdentifier.SOSC, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenAbbreviationHasDifferentCase_thenReturnsClubIdentifier() {
        // Act
        ClubIdentifier entityValue = converter.convertToEntityAttribute("hpsc");

        // Assert
        assertEquals(ClubIdentifier.HPSC, entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenAbbreviationIsNull_thenReturnsNull() {
        // Act
        ClubIdentifier entityValue = converter.convertToEntityAttribute(null);

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenAbbreviationIsBlank_thenReturnsNull() {
        // Act
        ClubIdentifier entityValue = converter.convertToEntityAttribute("   ");

        // Assert
        assertNull(entityValue);
    }

    @Test
    void testConvertToEntityAttribute_whenAbbreviationIsUnknown_thenReturnsNull() {
        // Act
        ClubIdentifier entityValue = converter.convertToEntityAttribute("ZZZ");

        // Assert
        assertNull(entityValue);
    }
}

