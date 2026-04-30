package za.co.hpsc.web.models.ipsc.common.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.ipsc.common.request.MatchRequest;

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
}