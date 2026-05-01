package za.co.hpsc.web.models.ipsc.match.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MatchOnlyDtoTest {

    @Test
    void testConstructor_withNoArgs_thenInitialisesDefaultValues() {
        // Act
        MatchOnlyDto dto = new MatchOnlyDto();

        // Assert
        assertNull(dto.getId());
        assertNull(dto.getClub());
        assertNull(dto.getClubName());
        assertEquals("", dto.getName());
        assertNull(dto.getScheduledDate());
        assertNull(dto.getMatchFirearmType());
        assertNull(dto.getMatchCategory());
        assertNull(dto.getDateEdited());
    }

    @Test
    void testConstructor_withAllArgs_thenInitialisesAllFields() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("HPSC");
        LocalDateTime scheduledDate = LocalDateTime.of(2026, 5, 10, 8, 0);
        LocalDateTime dateEdited = LocalDateTime.of(2026, 5, 9, 20, 0);

        // Act
        MatchOnlyDto dto = new MatchOnlyDto(
                12L,
                clubDto,
                "HPSC",
                "League Match",
                scheduledDate,
                FirearmType.HANDGUN,
                MatchCategory.LEAGUE,
                dateEdited
        );

        // Assert
        assertEquals(12L, dto.getId());
        assertEquals(clubDto, dto.getClub());
        assertEquals("HPSC", dto.getClubName());
        assertEquals("League Match", dto.getName());
        assertEquals(scheduledDate, dto.getScheduledDate());
        assertEquals(FirearmType.HANDGUN, dto.getMatchFirearmType());
        assertEquals(MatchCategory.LEAGUE, dto.getMatchCategory());
        assertEquals(dateEdited, dto.getDateEdited());
    }

    @Test
    void testInit_withNullRequest_thenKeepsExistingValues() {
        // Arrange
        MatchOnlyDto dto = new MatchOnlyDto();
        LocalDateTime scheduledDate = LocalDateTime.of(2026, 6, 1, 9, 0);
        LocalDateTime dateEdited = LocalDateTime.of(2026, 6, 1, 10, 0);
        dto.setId(50L);
        dto.setClubName("Existing Club");
        dto.setName("Existing Match");
        dto.setScheduledDate(scheduledDate);
        dto.setMatchFirearmType(FirearmType.RIFLE);
        dto.setMatchCategory(MatchCategory.LEAGUE);
        dto.setDateEdited(dateEdited);

        // Act
        dto.init(null);

        // Assert
        assertEquals(50L, dto.getId());
        assertEquals("Existing Club", dto.getClubName());
        assertEquals("Existing Match", dto.getName());
        assertEquals(scheduledDate, dto.getScheduledDate());
        assertEquals(FirearmType.RIFLE, dto.getMatchFirearmType());
        assertEquals(MatchCategory.LEAGUE, dto.getMatchCategory());
        assertEquals(dateEdited, dto.getDateEdited());
    }

    @Test
    void testInit_withKnownFirearmName_thenMapsFieldsAndSetsDefaults() {
        // Arrange
        MatchOnlyDto dto = new MatchOnlyDto();
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Club That Must Stay Unchanged");
        dto.setClub(clubDto);
        dto.setMatchFirearmType(FirearmType.RIFLE);

        MatchOnlyRequest request = new MatchOnlyRequest(
                77L,
                "New Match",
                LocalDateTime.of(2026, 7, 1, 11, 0),
                "New Club",
                "Handgun",
                5
        );

        LocalDateTime beforeInit = LocalDateTime.now();

        // Act
        dto.init(request);

        LocalDateTime afterInit = LocalDateTime.now();

        // Assert
        assertEquals(77L, dto.getId());
        assertEquals("New Club", dto.getClubName());
        assertEquals("New Match", dto.getName());
        assertEquals(LocalDateTime.of(2026, 7, 1, 11, 0), dto.getScheduledDate());
        assertEquals(FirearmType.HANDGUN, dto.getMatchFirearmType());
        assertEquals(IpscConstants.DEFAULT_MATCH_CATEGORY, dto.getMatchCategory());
        assertNotNull(dto.getDateEdited());
        assertTrue(dto.getDateEdited().isAfter(beforeInit.minusSeconds(1)) &&
                dto.getDateEdited().isBefore(afterInit.plusSeconds(1)));
        assertEquals(clubDto, dto.getClub());
    }

    @Test
    void testInit_withUnknownFirearmName_thenKeepsExistingFirearmType() {
        // Arrange
        MatchOnlyDto dto = new MatchOnlyDto();
        dto.setMatchFirearmType(FirearmType.SHOTGUN);

        MatchOnlyRequest request = new MatchOnlyRequest(
                88L,
                "Unknown Firearm Match",
                LocalDateTime.of(2026, 8, 12, 10, 0),
                "Unknown Club",
                "Laser Cannon",
                4
        );

        // Act
        dto.init(request);

        // Assert
        assertEquals(FirearmType.SHOTGUN, dto.getMatchFirearmType());
        assertEquals(IpscConstants.DEFAULT_MATCH_CATEGORY, dto.getMatchCategory());
        assertNotNull(dto.getDateEdited());
    }

    @Test
    void testInit_withNullFirearmName_thenKeepsExistingFirearmType() {
        // Arrange
        MatchOnlyDto dto = new MatchOnlyDto();
        dto.setMatchFirearmType(FirearmType.PCC);

        MatchOnlyRequest request = new MatchOnlyRequest();
        request.setMatchId(99L);
        request.setMatchName("Null Firearm Match");
        request.setMatchDate(LocalDateTime.of(2026, 9, 5, 9, 30));
        request.setClub("Club X");
        request.setFirearm(null);

        // Act
        dto.init(request);

        // Assert
        assertEquals(FirearmType.PCC, dto.getMatchFirearmType());
        assertEquals(IpscConstants.DEFAULT_MATCH_CATEGORY, dto.getMatchCategory());
        assertNotNull(dto.getDateEdited());
    }

    @Test
    void testToString_withNameAndClubName_thenReturnsFormattedString() {
        // Arrange
        MatchOnlyDto dto = new MatchOnlyDto();
        dto.setName("Championship Match");
        dto.setClubName("HPSC");

        // Act
        String result = dto.toString();

        // Assert
        assertEquals("Championship Match @ HPSC", result);
    }

    @Test
    void testToString_withBlankNameAndBlankClubName_thenReturnsEmptyString() {
        // Arrange
        MatchOnlyDto dto = new MatchOnlyDto();
        dto.setName("   ");
        dto.setClubName("   ");

        // Act
        String result = dto.toString();

        // Assert
        assertEquals("", result);
    }
}

