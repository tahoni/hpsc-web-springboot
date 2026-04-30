package za.co.hpsc.web.models.ipsc.response;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.models.ipsc.common.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.common.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.common.response.MatchResponse;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MatchResponseTest {

    @Test
    void constructFromMatchRequest_copiesAllFields() {
        MatchRequest request = new MatchRequest();
        request.setMatchId(1);
        request.setMatchName("Spring Classic");
        request.setMatchDate(LocalDateTime.of(2026, 5, 10, 9, 0));
        request.setClubId(42);
        request.setSquadCount(8);
        request.setFirearmId(3);

        MatchResponse response = new MatchResponse(request);

        assertEquals(1, response.getMatchId());
        assertEquals("Spring Classic", response.getMatchName());
        assertEquals(LocalDateTime.of(2026, 5, 10, 9, 0), response.getMatchDate());
        assertEquals(42, response.getClubId());
        assertEquals(8, response.getSquadCount());
        assertEquals(3, response.getFirearmId());
    }

    @Test
    void constructFromMatchRequest_withNullOptionalFields_setsNulls() {
        MatchRequest request = new MatchRequest();
        request.setMatchId(2);
        request.setMatchDate(LocalDateTime.of(2026, 6, 1, 10, 0));

        MatchResponse response = new MatchResponse(request);

        assertNull(response.getMatchName());
        assertNull(response.getClubId());
        assertNull(response.getSquadCount());
        assertNull(response.getFirearmId());
    }

    @Disabled("need to fix method")
    @Test
    void constructFromMatchDto_withClub_resolvesClubId() {
        IpscMatch match = new IpscMatch();
        match.setId(10L);
        match.setName("Regional Shoot");
        match.setScheduledDate(LocalDateTime.of(2026, 7, 15, 8, 0));
        Club club = new Club();
        club.setId(5L);
        match.setClub(club);

        MatchDto dto = new MatchDto(match);
        MatchResponse response = new MatchResponse(11L, dto);

        assertEquals(11, response.getMatchId());
        assertEquals("Regional Shoot", response.getMatchName());
        assertEquals(LocalDateTime.of(2026, 7, 15, 8, 0), response.getMatchDate());
        assertEquals(5, response.getClubId());
    }

    @Test
    void constructFromMatchDto_withNullClub_setsClubIdNull() {
        IpscMatch match = new IpscMatch();
        match.setId(11L);
        match.setName("Open Match");
        match.setScheduledDate(LocalDateTime.of(2026, 8, 1, 9, 0));
        match.setClub(null);

        MatchDto dto = new MatchDto(match);
        MatchResponse response = new MatchResponse(10L, dto);

        assertEquals(10, response.getMatchId());
        assertNull(response.getClubId());
    }

    @Test
    void init_withFullUpdate_overwritesAllFields() {
        MatchResponse base = new MatchResponse(1, "Old Name",
                LocalDateTime.of(2026, 1, 1, 8, 0), 10, 4, 2);

        MatchResponse update = new MatchResponse(1, "New Name",
                LocalDateTime.of(2026, 6, 1, 9, 0), 20, 6, 3);

        base.init(10L, update, true);

        assertEquals(10, base.getMatchId());
        assertEquals("New Name", base.getMatchName());
        assertEquals(LocalDateTime.of(2026, 6, 1, 9, 0), base.getMatchDate());
        assertEquals(20, base.getClubId());
        assertEquals(6, base.getSquadCount());
        assertEquals(3, base.getFirearmId());
    }

    @Test
    void init_withFullUpdate_andNullFields_overwritesWithNulls() {
        MatchResponse base = new MatchResponse(1, "Old Name",
                LocalDateTime.of(2026, 1, 1, 8, 0), 10, 4, 2);

        MatchResponse update = new MatchResponse();
        update.setMatchId(1);

        base.init(1L, update, true);

        assertEquals(1, base.getMatchId());
        assertNull(base.getMatchName());
        assertNull(base.getMatchDate());
        assertNull(base.getClubId());
        assertNull(base.getSquadCount());
        assertNull(base.getFirearmId());
    }

    @Test
    void init_withPartialUpdate_overwritesOnlyNonNullFields() {
        MatchResponse base = new MatchResponse(1, "Original Name",
                LocalDateTime.of(2026, 1, 1, 8, 0), 10, 4, 2);

        MatchResponse patch = new MatchResponse();
        patch.setMatchId(1);
        patch.setMatchName("Patched Name");

        base.init(1L, patch, false);

        assertEquals(1, base.getMatchId());
        assertEquals("Patched Name", base.getMatchName());
        assertEquals(LocalDateTime.of(2026, 1, 1, 8, 0), base.getMatchDate());
        assertEquals(10, base.getClubId());
        assertEquals(4, base.getSquadCount());
        assertEquals(2, base.getFirearmId());
    }

    @Test
    void init_withPartialUpdate_allNullIncoming_retainsAllExistingValues() {
        MatchResponse base = new MatchResponse(1, "Keep Me",
                LocalDateTime.of(2026, 3, 1, 10, 0), 7, 3, 1);

        MatchResponse patch = new MatchResponse();
        patch.setMatchId(1);

        base.init(2L, patch, false);

        assertEquals(2, base.getMatchId());
        assertEquals("Keep Me", base.getMatchName());
        assertEquals(LocalDateTime.of(2026, 3, 1, 10, 0), base.getMatchDate());
        assertEquals(7, base.getClubId());
        assertEquals(3, base.getSquadCount());
        assertEquals(1, base.getFirearmId());
    }

    @Test
    void init_alwaysOverwritesMatchId_regardlessOfMode() {
        MatchResponse base = new MatchResponse();
        base.setMatchId(1);

        MatchResponse incoming = new MatchResponse();
        incoming.setMatchId(99);

        base.init(100L, incoming, false);

        assertEquals(100, base.getMatchId());
    }

    @Test
    void init_whenFullUpdateTrue_overwritesAllFieldsIncludingNulls() {
        // Arrange
        MatchResponse target = new MatchResponse(
                1,
                "Old Match",
                LocalDateTime.of(2026, 4, 1, 10, 0),
                10,
                5,
                2
        );
        MatchResponse source = new MatchResponse(
                99,
                null,
                null,
                null,
                null,
                null
        );

        // Act
        target.init(99L, source, true);

        // Assert
        assertEquals(99, target.getMatchId());
        assertNull(target.getMatchName());
        assertNull(target.getMatchDate());
        assertNull(target.getClubId());
        assertNull(target.getSquadCount());
        assertNull(target.getFirearmId());
    }

    @Test
    void init_whenFullUpdateTrue_overwritesAllFieldsWithSourceValues() {
        // Arrange
        MatchResponse target = new MatchResponse(
                1,
                "Old Name",
                LocalDateTime.of(2026, 4, 1, 10, 0),
                1,
                3,
                1
        );
        MatchResponse source = new MatchResponse(
                2,
                "New Name",
                LocalDateTime.of(2026, 5, 2, 12, 0),
                5,
                8,
                4
        );

        // Act
        target.init(200L, source, true);

        // Assert
        assertEquals(200, target.getMatchId());
        assertEquals("New Name", target.getMatchName());
        assertEquals(LocalDateTime.of(2026, 5, 2, 12, 0), target.getMatchDate());
        assertEquals(5, target.getClubId());
        assertEquals(8, target.getSquadCount());
        assertEquals(4, target.getFirearmId());
    }

    @Test
    void init_whenFullUpdateFalse_updatesOnlyNonNullFields() {
        // Arrange
        MatchResponse target = new MatchResponse(
                1,
                "Existing Name",
                LocalDateTime.of(2026, 4, 1, 10, 0),
                10,
                6,
                2
        );
        MatchResponse source = new MatchResponse(
                2,
                "Patched Name",
                null,
                null,
                9,
                null
        );

        // Act
        target.init(300L, source, false);

        // Assert
        assertEquals(300, target.getMatchId());
        assertEquals("Patched Name", target.getMatchName());
        assertEquals(LocalDateTime.of(2026, 4, 1, 10, 0), target.getMatchDate());
        assertEquals(10, target.getClubId());
        assertEquals(9, target.getSquadCount());
        assertEquals(2, target.getFirearmId());
    }

    @Test
    void init_whenFullUpdateFalse_andAllSourceFieldsNull_keepsExistingFields() {
        // Arrange
        MatchResponse target = new MatchResponse(
                1,
                "Keep Name",
                LocalDateTime.of(2026, 4, 1, 10, 0),
                10,
                6,
                2
        );
        MatchResponse source = new MatchResponse(
                2,
                null,
                null,
                null,
                null,
                null
        );

        // Act
        target.init(400L, source, false);

        // Assert
        assertEquals(400, target.getMatchId());
        assertEquals("Keep Name", target.getMatchName());
        assertEquals(LocalDateTime.of(2026, 4, 1, 10, 0), target.getMatchDate());
        assertEquals(10, target.getClubId());
        assertEquals(6, target.getSquadCount());
        assertEquals(2, target.getFirearmId());
    }

    @Test
    void init_whenMatchIdProvided_overwritesIdRegardlessOfUpdateMode() {
        // Arrange
        MatchResponse target = new MatchResponse(
                1,
                "Name",
                LocalDateTime.of(2026, 4, 1, 10, 0),
                10,
                6,
                2
        );
        MatchResponse source = new MatchResponse(
                2,
                "Other Name",
                LocalDateTime.of(2026, 5, 1, 10, 0),
                11,
                7,
                3
        );

        // Act
        target.init(777L, source, false);

        // Assert
        assertEquals(777, target.getMatchId());
    }
}