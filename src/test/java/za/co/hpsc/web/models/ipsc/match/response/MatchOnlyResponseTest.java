package za.co.hpsc.web.models.ipsc.match.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.common.response.StageResponse;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MatchOnlyResponseTest {

    @Test
    void testConstructor_withNoArgs_thenInitialisesDefaultFields() {
        // Act
        MatchOnlyResponse response = new MatchOnlyResponse();

        // Assert
        assertNull(response.getMatchId());
        assertNull(response.getMatchName());
        assertNull(response.getMatchDate());
        assertNull(response.getClub());
        assertNull(response.getFirearm());
        assertEquals(0, response.getSquadCount());
        assertNull(response.getStages());
    }

    @Test
    void testConstructor_withStagesList_thenInitialisesStagesField() {
        // Arrange
        StageResponse stage = new StageResponse();
        stage.setStageId(1);
        List<StageResponse> stages = List.of(stage);

        // Act
        MatchOnlyResponse response = new MatchOnlyResponse(stages);

        // Assert
        assertEquals(stages, response.getStages());
        assertNull(response.getMatchId());
        assertEquals(0, response.getSquadCount());
    }

    @Test
    void testConstructor_withMatchIdAndMatchDto_thenMapsMatchFieldsFromDto() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("HPSC Club");

        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setName("Summer Shoot");
        matchOnlyDto.setScheduledDate(LocalDateTime.of(2026, 5, 1, 9, 0));
        matchOnlyDto.setClub(clubDto);
        matchOnlyDto.setMatchFirearmType(FirearmType.HANDGUN);

        // Act
        MatchOnlyResponse response = new MatchOnlyResponse(42L, matchOnlyDto);

        // Assert
        assertEquals(42L, response.getMatchId());
        assertEquals("Summer Shoot", response.getMatchName());
        assertEquals(LocalDateTime.of(2026, 5, 1, 9, 0), response.getMatchDate());
        assertEquals("HPSC Club", response.getClub());
        assertEquals("Handgun", response.getFirearm());
        assertEquals(0, response.getSquadCount());
        assertNull(response.getStages());
    }

    @Test
    void testConstructor_withMatchOnlyRequest_thenCopiesAllRequestFields() {
        // Arrange
        MatchOnlyRequest request = new MatchOnlyRequest(
                10L,
                "League Match",
                LocalDateTime.of(2026, 6, 10, 8, 30),
                "Some Club",
                "Rifle",
                7
        );

        // Act
        MatchOnlyResponse response = new MatchOnlyResponse(request);

        // Assert
        assertEquals(10L, response.getMatchId());
        assertEquals("League Match", response.getMatchName());
        assertEquals(LocalDateTime.of(2026, 6, 10, 8, 30), response.getMatchDate());
        assertEquals("Some Club", response.getClub());
        assertEquals("Rifle", response.getFirearm());
        assertEquals(7, response.getSquadCount());
        assertNull(response.getStages());
    }

    @Test
    void testInit_withFullUpdateTrue_thenOverwritesAllFieldsIncludingNulls() {
        // Arrange
        MatchOnlyResponse response = new MatchOnlyResponse();
        response.setMatchId(1L);
        response.setMatchName("Old Name");
        response.setMatchDate(LocalDateTime.of(2026, 7, 1, 10, 0));
        response.setClub("Old Club");
        response.setFirearm("Handgun");
        response.setSquadCount(5);

        StageResponse stage = new StageResponse();
        stage.setStageId(2);
        List<StageResponse> stages = List.of(stage);
        response.setStages(stages);

        MatchOnlyRequest right = new MatchOnlyRequest();
        right.setMatchName(null);
        right.setMatchDate(LocalDateTime.of(2026, 8, 2, 12, 0));
        right.setClub(null);
        right.setFirearm("Shotgun");
        right.setSquadCount(null);

        // Act
        response.init(99L, right, true);

        // Assert
        assertEquals(99L, response.getMatchId());
        assertNull(response.getMatchName());
        assertEquals(LocalDateTime.of(2026, 8, 2, 12, 0), response.getMatchDate());
        assertNull(response.getClub());
        assertEquals("Shotgun", response.getFirearm());
        assertNull(response.getSquadCount());
        assertEquals(stages, response.getStages());
    }

    @Test
    void testInit_withFullUpdateFalse_thenUpdatesOnlyNonNullFields() {
        // Arrange
        MatchOnlyResponse response = new MatchOnlyResponse();
        response.setMatchId(11L);
        response.setMatchName("Original Name");
        response.setMatchDate(LocalDateTime.of(2026, 9, 3, 11, 0));
        response.setClub("Original Club");
        response.setFirearm("PCC");
        response.setSquadCount(4);

        MatchOnlyRequest right = new MatchOnlyRequest();
        right.setMatchName("Updated Name");
        right.setMatchDate(null);
        right.setClub("Updated Club");
        right.setFirearm(null);
        right.setSquadCount(12);

        // Act
        response.init(77L, right, false);

        // Assert
        assertEquals(77L, response.getMatchId());
        assertEquals("Updated Name", response.getMatchName());
        assertEquals(LocalDateTime.of(2026, 9, 3, 11, 0), response.getMatchDate());
        assertEquals("Updated Club", response.getClub());
        assertEquals("PCC", response.getFirearm());
        assertEquals(12, response.getSquadCount());
    }
}

