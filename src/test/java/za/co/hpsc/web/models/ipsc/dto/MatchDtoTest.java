package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.ClubIdentifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatchDtoTest {

    @Test
    void testToString_withClubAndNullClubName_thenReturnsMatchNameAtNameOfClub() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("HPSC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Clubshoot");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Eufees Clubshoot @ Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }

    @Test
    void testToString_withClubAndClubName_thenReturnsMatchNameAtNameOfClub() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("HPSC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Clubshoot");
        matchDto.setClub(clubDto);
        matchDto.setClubName(ClubIdentifier.HPSC);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Eufees Clubshoot @ Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }

    @Test
    void testToString_withNullClubButClubName_thenReturnsMatchNameAtClubName() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Clubshoot");
        matchDto.setClub(null);
        matchDto.setClubName(ClubIdentifier.HPSC);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Eufees Clubshoot @ HPSC", result);
    }

    @Test
    void testToString_withoutClubButClubName_thenReturnsMatchNameAtClubName() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Clubshoot");
        matchDto.setClubName(ClubIdentifier.HPSC);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Eufees Clubshoot @ HPSC", result);
    }

    @Test
    void testToString_withoutClubAndClubName_thenReturnsMatchName() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Clubshoot");

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Eufees Clubshoot", result);
    }

    @Test
    void testToString_witNullClubAndClubName_thenReturnsMatchName() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Clubshoot");
        matchDto.setClub(null);
        matchDto.setClubName(null);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Eufees Clubshoot", result);
    }
}
