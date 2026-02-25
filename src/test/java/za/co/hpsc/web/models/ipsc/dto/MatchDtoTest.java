package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// TODO: add tests for other methods
public class MatchDtoTest {

    @Test
    void testToString_withClubAndNullClubName_thenReturnsMatchNameAtNameOfClub() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("HPSC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Club Shoot");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Eufees Club Shoot @ Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }

    @Test
    void testToString_withClubAndClubName_thenReturnsMatchNameAtNameOfClub() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("HPSC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Club Shoot");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Eufees Club Shoot @ Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }

    @Test
    void testToString_withoutClubAndClubName_thenReturnsMatchName() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Club Shoot");

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Eufees Club Shoot", result);
    }
}
