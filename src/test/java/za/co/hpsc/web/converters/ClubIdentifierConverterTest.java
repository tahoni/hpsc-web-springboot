package za.co.hpsc.web.converters;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.ClubIdentifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

// TODO: standard naming
class ClubIdentifierConverterTest {

    private final ClubIdentifierConverter converter = new ClubIdentifierConverter();

    @Test
    void convertToDatabaseColumnReturnsAbbreviationForValidClubIdentifier() {
        String databaseValue = converter.convertToDatabaseColumn(ClubIdentifier.HPSC);

        assertEquals("HPSC", databaseValue);
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNullClubIdentifier() {
        String databaseValue = converter.convertToDatabaseColumn(null);

        assertNull(databaseValue);
    }

    @Test
    void convertToEntityAttributeReturnsClubIdentifierForKnownAbbreviation() {
        ClubIdentifier entityValue = converter.convertToEntityAttribute("SOSC");

        assertEquals(ClubIdentifier.SOSC, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsClubIdentifierForCaseInsensitiveAbbreviation() {
        ClubIdentifier entityValue = converter.convertToEntityAttribute("hpsc");

        assertEquals(ClubIdentifier.HPSC, entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForNullAbbreviation() {
        ClubIdentifier entityValue = converter.convertToEntityAttribute(null);

        assertNull(entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForBlankAbbreviation() {
        ClubIdentifier entityValue = converter.convertToEntityAttribute("   ");

        assertNull(entityValue);
    }

    @Test
    void convertToEntityAttributeReturnsNullForUnknownAbbreviation() {
        ClubIdentifier entityValue = converter.convertToEntityAttribute("ZZZ");

        assertNull(entityValue);
    }
}

