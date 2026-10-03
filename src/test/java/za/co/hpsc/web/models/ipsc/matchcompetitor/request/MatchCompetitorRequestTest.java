package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchCompetitorRequestTest {

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        String json = """
                {
                  "competitorId": 1,
                  "matchId": 2,
                  "matchClub": "HPSC",
                  "competitorCategory": ["Junior"],
                  "firearmType": "Handgun",
                  "division": "Open Division",
                  "powerFactor": "Major",
                  "matchPoints": 95.5,
                  "overallRanking": 2,
                  "clubRanking": 1,
                  "isVisitor": false
                }
                """;

        // Act
        MatchCompetitorRequest request = new ObjectMapper().readValue(json, MatchCompetitorRequest.class);

        // Assert
        assertEquals(1L, request.getCompetitorId());
        assertEquals(2L, request.getMatchId());
        assertEquals("HPSC", request.getMatchClub());
        assertEquals(List.of("Junior"), request.getCompetitorCategory());
        assertEquals("Handgun", request.getFirearmType());
        assertEquals("Open Division", request.getDivision());
        assertEquals("Major", request.getPowerFactor());
        assertEquals(0, new BigDecimal("95.5").compareTo(request.getMatchPoints()));
        assertEquals(0, new BigDecimal("2").compareTo(request.getOverallRanking()));
        assertEquals(0, new BigDecimal("1").compareTo(request.getClubRanking()));
        assertEquals(Boolean.FALSE, request.getIsVisitor());
    }

    @Test
    void testJsonDeserialization_whenOnlyRequiredFieldsProvided_thenOptionalsAreNull() throws Exception {
        // Arrange
        String json = """
                {
                  "competitorId": 1,
                  "matchId": 2,
                  "competitorCategory": ["Junior"],
                  "firearmType": "Handgun",
                  "division": "Open Division"
                }
                """;

        // Act
        MatchCompetitorRequest request = new ObjectMapper().readValue(json, MatchCompetitorRequest.class);

        // Assert
        assertNull(request.getMatchClub());
        assertNull(request.getPowerFactor());
        assertNull(request.getMatchPoints());
        assertNull(request.getOverallRanking());
        assertNull(request.getClubRanking());
        assertNull(request.getIsVisitor());
    }
}
