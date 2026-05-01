package za.co.hpsc.web.utils;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IpscUtilTest {

    @Test
    void testClubToString_withNameAndAbbreviation_thenReturnsFormattedClubString() {
        // Act
        String result = IpscUtil.clubTostring("Hartbeespoortdam Practical Shooting Club", "HPSC");

        // Assert
        assertEquals("Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }

    @Test
    void testClubToString_withNullNameAndAbbreviationProvided_thenReturnsAbbreviationWithoutParentheses() {
        // Act
        String result = IpscUtil.clubTostring(null, "HPSC");

        // Assert
        assertEquals("HPSC", result);
    }

    @Test
    void testClubToString_withNameAndNullAbbreviation_thenReturnsTrimmedName() {
        // Act
        String result = IpscUtil.clubTostring("  HPSC Club  ", null);

        // Assert
        assertEquals("HPSC Club", result);
    }

    @Test
    void testClubToString_withNameSameAsAbbreviationIgnoringCase_thenReturnsNameOnly() {
        // Act
        String result = IpscUtil.clubTostring("HPSC", "hpsc");

        // Assert
        assertEquals("HPSC", result);
    }

    @Test
    void testClubToString_withBlankValues_thenReturnsEmptyString() {
        // Act
        String result = IpscUtil.clubTostring("   ", "   ");

        // Assert
        assertEquals("", result);
    }

    @Test
    void testMatchToString_withMatchNameAndClubDetails_thenReturnsFormattedMatchString() {
        // Act
        String result = IpscUtil.matchToString("League Match", "HPSC Club", "HPSC");

        // Assert
        assertEquals("League Match @ HPSC Club (HPSC)", result);
    }

    @Test
    void testMatchToString_withNullMatchNameAndClubDetails_thenReturnsClubOnlyPrefixedWithAtSign() {
        // Act
        String result = IpscUtil.matchToString(null, "HPSC Club", "HPSC");

        // Assert
        assertEquals("@ HPSC Club (HPSC)", result);
    }

    @Test
    void testMatchToString_withMatchNameAndBlankClubDetails_thenReturnsMatchNameOnly() {
        // Act
        String result = IpscUtil.matchToString("  Club Shoot  ", "   ", "   ");

        // Assert
        assertEquals("Club Shoot", result);
    }

    @Test
    void testMatchToString_withWhitespaceInInputs_thenReturnsTrimmedValues() {
        // Act
        String result = IpscUtil.matchToString("  Night Shoot  ", "  HPSC Club  ", "  HC  ");

        // Assert
        assertEquals("Night Shoot @ HPSC Club (HC)", result);
    }

    @Test
    void testMatchToString_withClubDtoNull_thenReturnsTrimmedMatchNameOnly() {
        // Act
        String result = IpscUtil.matchToString("  Steel Challenge  ", null);

        // Assert
        assertEquals("Steel Challenge", result);
    }

    @Test
    void testMatchToString_withClubDtoProvided_thenReturnsFormattedMatchString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("HPSC");

        // Act
        String result = IpscUtil.matchToString("Eufees Club Shoot", clubDto);

        // Assert
        assertEquals("Eufees Club Shoot @ Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }
}

