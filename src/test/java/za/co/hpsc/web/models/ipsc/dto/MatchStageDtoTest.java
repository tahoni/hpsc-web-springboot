package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// TODO: test club name
// TODO: add tests for other methods
public class MatchStageDtoTest {

    @Disabled
    @Test
    void testToString_withStageNumber_thenReturnsStageNumberAndMatch() {
        // Arrange
        MatchDto matchDto = new MatchDto();
//        matchDto.setClubName(ClubIdentifier.HPSC);
        matchDto.setName("Eufees Club Shoot");
        MatchStageDto clubDto = new MatchStageDto();
        clubDto.setStageNumber(1);
        clubDto.setMatch(matchDto);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("1 for Eufees Clubshoot @ HPSC", result);
    }

    @Disabled
    @Test
    void testToString_withoutStageNumber_thenReturnsStageNumberAndMatch() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Clubshoot");
//        matchDto.setClubName(ClubIdentifier.HPSC);
        matchDto.setName("Eufees Club Shoot");
        MatchStageDto clubDto = new MatchStageDto();
        clubDto.setMatch(matchDto);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("0 for Eufees Clubshoot @ HPSC", result);
    }

    @Disabled
    @Test
    void testToString_withNullStageNumber_thenReturnsStageNumberAndMatch() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Clubshoot");
//        matchDto.setClubName(ClubIdentifier.HPSC);
        matchDto.setName("Eufees Club Shoot");
        MatchStageDto clubDto = new MatchStageDto();
        clubDto.setStageNumber(null);
        clubDto.setMatch(matchDto);

        // Act
        String result = clubDto.toString();

        // Assert
        assertEquals("0 for Eufees Clubshoot @ HPSC", result);
    }
}
