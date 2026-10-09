package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MatchCompetitorPatchRequestTest {

    @Test
    void testNewInstance_whenNothingSet_thenEveryFieldIsNull() {
        // Act
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();

        // Assert
        assertNull(request.getCompetitorId());
        assertNull(request.getMatchId());
        assertNull(request.getMatchClub());
        assertNull(request.getCompetitorCategory());
        assertNull(request.getFirearmType());
        assertNull(request.getDivision());
        assertNull(request.getPowerFactor());
        assertNull(request.getPoints());
        assertNull(request.getIsVisitor());
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenEveryFieldIsNull() throws Exception {
        // Act
        MatchCompetitorPatchRequest request = new ObjectMapper().readValue("{}", MatchCompetitorPatchRequest.class);

        // Assert
        assertNull(request.getCompetitorId());
        assertNull(request.getDivision());
        assertNull(request.getIsVisitor());
    }

    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        String json = """
                {
                  "competitorId": 1,
                  "matchId": 2,
                  "matchClub": "HPSC",
                  "competitorCategory": "Junior",
                  "firearmType": "Handgun",
                  "division": "Open",
                  "powerFactor": "Major",
                  "points": 95.5,
                  "isVisitor": true
                }
                """;

        // Act
        MatchCompetitorPatchRequest request = new ObjectMapper().readValue(json, MatchCompetitorPatchRequest.class);

        // Assert
        assertEquals(1L, request.getCompetitorId());
        assertEquals(2L, request.getMatchId());
        assertEquals("HPSC", request.getMatchClub());
        assertEquals("Junior", request.getCompetitorCategory());
        assertEquals("Handgun", request.getFirearmType());
        assertEquals("Open", request.getDivision());
        assertEquals("Major", request.getPowerFactor());
        assertEquals(0, new BigDecimal("95.5").compareTo(request.getPoints()));
        assertEquals(Boolean.TRUE, request.getIsVisitor());
    }
}
