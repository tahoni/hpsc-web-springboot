package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.ClubIdentifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatchStageDtoTest {

    @Test
    void testToString_withStageNumber_thenReturnsStageNumberAndMatch() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Clubshoot");
        matchDto.setClubIdentifier(ClubIdentifier.HPSC);
        MatchStageDto clubDto = new MatchStageDto();
        clubDto.setStageNumber(1);
        clubDto.setMatch(matchDto);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("1 for Eufees Clubshoot @ HPSC", result);
    }

    @Test
    void testToString_withoutStageNumber_thenReturnsStageNumberAndMatch() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Clubshoot");
        matchDto.setClubIdentifier(ClubIdentifier.HPSC);
        MatchStageDto clubDto = new MatchStageDto();
        clubDto.setMatch(matchDto);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("0 for Eufees Clubshoot @ HPSC", result);
    }

    @Test
    void testToString_withNullStageNumber_thenReturnsStageNumberAndMatch() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Clubshoot");
        matchDto.setClubIdentifier(ClubIdentifier.HPSC);
        MatchStageDto clubDto = new MatchStageDto();
        clubDto.setStageNumber(null);
        clubDto.setMatch(matchDto);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("0 for Eufees Clubshoot @ HPSC", result);
    }
}
