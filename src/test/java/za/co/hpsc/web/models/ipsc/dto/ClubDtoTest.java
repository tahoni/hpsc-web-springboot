package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;

import static org.junit.jupiter.api.Assertions.*;


public class ClubDtoTest {

    // Constructor mapping

    // Single parameter: Club entity
    @Test
    void testConstructor_whenClubEntityProvided_thenMapsFields() {
        // Arrange
        Club club = new Club();
        club.setId(10L);
        club.setName("Alpha Club");
        club.setAbbreviation("AC");

        // Act
        ClubDto dto = new ClubDto(club);

        // Assert
        assertEquals(10L, dto.getId());
        assertEquals("Alpha Club", dto.getName());
        assertEquals("AC", dto.getAbbreviation());
    }

    // Single parameter: ClubResponse
    @Test
    void testConstructor_whenClubResponseProvided_thenMapsFields() {
        // Arrange
        ClubResponse response = new ClubResponse();
        response.setClubId(7);
        response.setClubName("Bravo Club");
        response.setClubCode("BC");

        // Act
        ClubDto dto = new ClubDto(response);

        // Assert
        assertEquals(7, dto.getIndex());
        assertEquals("Bravo Club", dto.getName());
        assertEquals("BC", dto.getAbbreviation());
    }

    @Test
    void testConstructor_withClubIdentifier_thenMapsNameAndAbbreviation() {
        // Arrange
        ClubIdentifier identifier = ClubIdentifier.values()[0];
        String expectedName = identifier.getName();

        // Act
        ClubDto dto = new ClubDto(identifier);

        // Assert
        assertEquals(expectedName, dto.getName());
        assertEquals(expectedName, dto.getAbbreviation());
    }

    @Test
    void testConstructor_withClubAndIdentifier_whenClubNotNull_thenUsesClub() {
        // Arrange
        Club club = new Club();
        club.setId(22L);
        club.setName("Charlie Club");
        club.setAbbreviation("CC");

        ClubIdentifier identifier = ClubIdentifier.values()[0];

        // Act
        ClubDto dto = new ClubDto(club, identifier);

        // Assert
        assertEquals(22L, dto.getId());
        assertEquals("Charlie Club", dto.getName());
        assertEquals("CC", dto.getAbbreviation());
    }

    @Test
    void testConstructor_whenClubAndIdentifierProvidedAndClubNull_thenUsesIdentifier() {
        // Arrange
        ClubIdentifier identifier = ClubIdentifier.values()[0];
        String expectedName = identifier.getName();

        // Act
        ClubDto dto = new ClubDto(null, identifier);

        // Assert
        assertNull(dto.getId());
        assertEquals(expectedName, dto.getName());
        assertEquals(expectedName, dto.getAbbreviation());
    }

    @Test
    void testConstructor_whenClubEntityNull_thenLeavesDefaults() {
        // Arrange
        ClubDto dto = new ClubDto((Club) null);

        // Act
        String name = dto.getName();

        // Assert
        assertEquals("", name);
        assertNull(dto.getId());
        assertNull(dto.getAbbreviation());
    }

    @Test
    void testConstructor_whenClubResponseNull_thenLeavesDefaults() {
        // Arrange
        ClubDto dto = new ClubDto((ClubResponse) null);

        // Act
        String name = dto.getName();

        // Assert
        assertEquals("", name);
        assertNull(dto.getIndex());
        assertNull(dto.getAbbreviation());
    }

    @Test
    void testConstructor_whenClubIdentifierNull_thenLeavesDefaults() {
        // Arrange
        ClubDto dto = new ClubDto((ClubIdentifier) null);

        // Act
        String name = dto.getName();

        // Assert
        assertEquals("", name);
        assertNull(dto.getAbbreviation());
    }

    @Test
    void testConstructor_whenClubAndIdentifierBothNull_thenLeavesDefaults() {
        // Arrange
        ClubDto dto = new ClubDto(null, null);

        // Act
        String name = dto.getName();

        // Assert
        assertEquals("", name);
        assertNull(dto.getId());
        assertNull(dto.getAbbreviation());
    }

    @Test
    void testConstructor_whenClubResponseHasNullFields_thenKeepsNulls() {
        // Arrange
        ClubResponse response = new ClubResponse();

        // Act
        ClubDto dto = new ClubDto(response);

        // Assert
        assertNull(dto.getIndex());
        assertNull(dto.getName());
        assertNull(dto.getAbbreviation());
    }


    // init() mapping
    @Test
    void testInit_whenClubResponseProvided_thenMapsFields() {
        // Arrange
        ClubDto dto = new ClubDto();
        ClubResponse response = new ClubResponse();
        response.setClubId(15);
        response.setClubName("Delta Club");
        response.setClubCode("DC");

        // Act
        dto.init(response);

        // Assert
        assertEquals(15, dto.getIndex());
        assertEquals("Delta Club", dto.getName());
        assertEquals("DC", dto.getAbbreviation());
    }

    @Test
    void testInit_whenClubResponseNull_thenNoChanges() {
        // Arrange
        ClubDto dto = new ClubDto();
        dto.setIndex(3);
        dto.setName("Echo Club");
        dto.setAbbreviation("EC");

        // Act
        dto.init(null);

        // Assert
        assertEquals(3, dto.getIndex());
        assertEquals("Echo Club", dto.getName());
        assertEquals("EC", dto.getAbbreviation());
    }

    @Test
    void testInit_whenClubResponseHasNullFields_thenOverridesToNulls() {
        // Arrange
        ClubDto dto = new ClubDto();
        dto.setIndex(9);
        dto.setName("Foxtrot Club");
        dto.setAbbreviation("FC");
        ClubResponse response = new ClubResponse();

        // Act
        dto.init(response);

        // Assert
        assertNull(dto.getIndex());
        assertNull(dto.getName());
        assertNull(dto.getAbbreviation());
    }


    // toString() behavior
    @Test
    void testToString_whenAbbreviationProvided_thenReturnsNameWithAbbreviation() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("HPSC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }

    @Test
    void testToString_whenAbbreviationMissing_thenReturnsName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club", result);
    }

    @Test
    void testToString_whenAbbreviationNull_thenReturnsName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation(null);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club", result);
    }

    @Test
    void testToString_whenAbbreviationBlank_thenReturnsName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("   ");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club", result);
    }

    @Test
    void testToString_whenAbbreviationEmpty_thenReturnsName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club", result);
    }

    @Test
    void testToString_whenAbbreviationSameAsName_thenReturnsNameOnly() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("HPSC");
        clubDto.setAbbreviation("HPSC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("HPSC", result);
    }

    @Test
    void testToString_whenNameNull_thenReturnsAbbreviationOnly() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName(null);
        clubDto.setAbbreviation("HPSC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("HPSC", result);
    }

    @Test
    void testToString_whenNameEmpty_thenReturnsAbbreviationOnly() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation("HPSC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("HPSC", result);
    }

    @Test
    void testToString_whenNameBlank_thenReturnsAbbreviationOnly() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("   ");
        clubDto.setAbbreviation("HPSC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("HPSC", result);
    }

    // Combined null/empty/blank name and abbreviation handling
    @Test
    void testToString_whenNameEmptyAndAbbreviationNull_thenReturnsEmptyName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation(null);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenNameEmptyAndAbbreviationEmpty_thenReturnsEmptyName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation("");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenNameEmptyAndAbbreviationBlank_thenReturnsEmptyName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation("   ");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenNameNullAndAbbreviationEmpty_thenReturnsEmptyString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName(null);
        clubDto.setAbbreviation("");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenNameNullAndAbbreviationBlank_thenReturnsEmptyString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName(null);
        clubDto.setAbbreviation("   ");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenNameBlankAndAbbreviationNull_thenReturnsBlankName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("   ");
        clubDto.setAbbreviation(null);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    // Special characters and long names
    @Test
    void testToString_whenNameHasSpecialCharacters_thenIncludesCharactersInOutput() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Club-A & Co. (Pty) Ltd.");
        clubDto.setAbbreviation("CAC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Club-A & Co. (Pty) Ltd. (CAC)", result);
    }

    @Test
    void testToString_whenAbbreviationHasSpecialCharacters_thenIncludesCharactersInOutput() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("South African Shooting");
        clubDto.setAbbreviation("SA-SC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("South African Shooting (SA-SC)", result);
    }

    @Test
    void testToString_whenNameIsVeryLong_thenReturnsFullName() {
        // Arrange
        String longName = "This is an extremely long club name with many words and details that exceed normal length " +
                "and continues to be quite lengthy for testing purposes";
        ClubDto clubDto = new ClubDto();
        clubDto.setName(longName);
        clubDto.setAbbreviation("LONG");

        // Act
        String result = clubDto.toString();

        // Assert
        assertTrue(result.contains(longName));
        assertEquals(longName + " (LONG)", result);
    }

    @Test
    void testToString_whenAbbreviationIsVeryLong_thenReturnsFullAbbreviation() {
        // Arrange
        String longAbbrev = "VERYLONGABBREVIATIONFORTEST";
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Club");
        clubDto.setAbbreviation(longAbbrev);

        // Act
        String result = clubDto.toString();

        // Assert
        assertTrue(result.contains(longAbbrev));
        assertEquals("Club (" + longAbbrev + ")", result);
    }

    // Numeric and whitespace variations
    @Test
    void testToString_whenNameIsNumeric_thenReturnsNumericString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("123456");
        clubDto.setAbbreviation("NUM");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("123456 (NUM)", result);
    }

    @Test
    void testToString_whenNameHasLeadingWhitespace_thenIncludesWhitespace() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("   Leading Space Club");
        clubDto.setAbbreviation("LSC");

        // Act
        String result = clubDto.toString();

        // Assert
        // Name is not blank (has content), so included; then result is trimmed
        assertEquals("Leading Space Club (LSC)", result);
    }

    @Test
    void testToString_whenNameHasTrailingWhitespace_thenIncludesWhitespace() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Trailing Space Club   ");
        clubDto.setAbbreviation("TSC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Trailing Space Club (TSC)", result);
    }

    @Test
    void testToString_whenNameHasMixedWhitespace_thenIncludesAllWhitespace() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("  Mixed   Spaces  ");
        clubDto.setAbbreviation("MIX");

        // Act
        String result = clubDto.toString();

        // Assert
        // Name is not blank, so included; result is trimmed
        assertEquals("Mixed   Spaces (MIX)", result);
    }

    // Fully and partially populated variations
    @Test
    void testToString_whenFullyPopulated_thenReturnsCompleteString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setId(999L);
        clubDto.setIndex(888);
        clubDto.setName("Complete Club");
        clubDto.setAbbreviation("CC");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Complete Club (CC)", result);
    }

    @Test
    void testToString_whenPartiallyPopulated_thenIgnoresIdAndIndex() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setId(777L);
        // Index not set
        clubDto.setName("Partial Club");
        // Abbreviation not set

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Partial Club", result);
        assertFalse(result.contains("777"));
    }

    @Test
    void testToString_whenOnlyIdSet_thenReturnsEmptyString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setId(123L);
        // Name and abbreviation not set (defaulted to "")

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
        assertFalse(result.contains("123"));
    }

    @Test
    void testToString_whenOnlyIndexSet_thenReturnsEmptyString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setIndex(456);
        // Name and abbreviation not set (defaulted to "")

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
        assertFalse(result.contains("456"));
    }

    // Name and abbreviation same value variations
    @Test
    void testToString_whenNameAndAbbreviationBothSame_thenReturnsValueOnce() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        String value = "SingleValue";
        clubDto.setName(value);
        clubDto.setAbbreviation(value);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals(value, result);
        assertEquals(value.length(), result.length());
    }

    @Test
    void testToString_whenNameAndAbbreviationCaseSensitiveDifferent_thenReturnsValueOnce() {
        // Arrange - equalsIgnoreCase means "hpsc" == "HPSC"
        ClubDto clubDto = new ClubDto();
        clubDto.setName("HPSC");
        clubDto.setAbbreviation("hpsc");

        // Act
        String result = clubDto.toString();

        // Assert
        // equalsIgnoreCase means abbreviation is considered same as name, so only name is shown
        assertEquals("HPSC", result);
    }

    // Whitespace-only abbreviation variations
    @Test
    void testToString_whenAbbreviationIsMultipleSpaces_thenTreatsAsBlank() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Club Name");
        clubDto.setAbbreviation("     ");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Club Name", result);
    }

    @Test
    void testToString_whenAbbreviationIsTab_thenTreatsAsBlank() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Club Name");
        clubDto.setAbbreviation("\t");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Club Name", result);
    }

    // Edge case combinations
    @Test
    void testToString_whenNameBlankAndAbbreviationBlank_thenReturnsEmpty() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("   ");
        clubDto.setAbbreviation("   ");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenNameBlankAndAbbreviationEmpty_thenReturnsEmpty() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("   ");
        clubDto.setAbbreviation("");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenNameNullAndAbbreviationNull_thenReturnsEmptyString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName(null);
        clubDto.setAbbreviation(null);

        // Act
        String result = clubDto.toString();

        // Assert
        // Both name and abbreviation are null, so nothing is appended; result is ""
        assertEquals("", result);
    }

    @Test
    void testToString_whenMultipleCallsWithSameValues_thenReturnsConsistentResults() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Consistent Club");
        clubDto.setAbbreviation("CC");

        // Act
        String result1 = clubDto.toString();
        String result2 = clubDto.toString();
        String result3 = clubDto.toString();

        // Assert
        assertEquals(result1, result2);
        assertEquals(result2, result3);
        assertEquals("Consistent Club (CC)", result1);
    }

    @Test
    void testToString_whenValueChangedAfterCall_thenReflectsNewValues() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Original");
        clubDto.setAbbreviation("ORG");

        // Act
        String result1 = clubDto.toString();
        clubDto.setName("Modified");
        clubDto.setAbbreviation("MOD");
        String result2 = clubDto.toString();

        // Assert
        assertEquals("Original (ORG)", result1);
        assertEquals("Modified (MOD)", result2);
        assertNotEquals(result1, result2);
    }

    @Test
    void testToString_whenNullValuesInConstructor_thenHandlesGracefully() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        // Leave abbreviation as null (default)

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("Test Club", result);
    }

    @Test
    void testToString_whenEmptyStringsInConstructor_thenHandlesGracefully() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation("");

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("", result);
    }
}


