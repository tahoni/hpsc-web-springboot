package za.co.hpsc.web.models.ipsc.matchcompetitor.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchCompetitorRequestTest {

    // JSON serialization
    @Test
    void testJsonSerialization_whenFullyPopulated_thenSerializesAllFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        MatchCompetitorRequest request = new MatchCompetitorRequest(7L, 1L, "Jane Doe", 2L, "HPSC", List.of("Junior", "Lady"),
                "Handgun", "Open Division", "Major", new BigDecimal("95.5"), new BigDecimal("98.25"),
                new BigDecimal("41.5"), new BigDecimal("93.75"), new BigDecimal("5.2"), 30, 4, 1, 2, 1, 0, 3, 0,
                new BigDecimal("2"), new BigDecimal("1"), false);

        // Act
        JsonNode node = mapper.readTree(mapper.writeValueAsString(request));

        // Assert
        assertEquals(7, node.get("matchCompetitorId").asInt());
        assertEquals(1, node.get("competitorId").asInt());
        assertEquals("Jane Doe", node.get("name").asText());
        assertEquals(2, node.get("matchId").asInt());
        assertEquals("HPSC", node.get("matchClub").asText());
        assertEquals(2, node.get("competitorCategory").size());
        assertEquals("Junior", node.get("competitorCategory").get(0).asText());
        assertEquals("Lady", node.get("competitorCategory").get(1).asText());
        assertEquals("Handgun", node.get("firearmType").asText());
        assertEquals("Open Division", node.get("division").asText());
        assertEquals("Major", node.get("powerFactor").asText());
        assertEquals(0, new BigDecimal("95.5").compareTo(node.get("points").decimalValue()));
        assertEquals(0, new BigDecimal("98.25").compareTo(node.get("percentage").decimalValue()));
        assertEquals(0, new BigDecimal("41.5").compareTo(node.get("time").decimalValue()));
        assertEquals(0, new BigDecimal("93.75").compareTo(node.get("percentageOfPossiblePoints").decimalValue()));
        assertEquals(0, new BigDecimal("5.2").compareTo(node.get("hitFactor").decimalValue()));
        assertEquals(30, node.get("alpha").asInt());
        assertEquals(4, node.get("charlie").asInt());
        assertEquals(1, node.get("delta").asInt());
        assertEquals(2, node.get("misses").asInt());
        assertEquals(1, node.get("noPenaltyMisses").asInt());
        assertEquals(0, node.get("noShoots").asInt());
        assertEquals(3, node.get("proceduralErrors").asInt());
        assertEquals(0, node.get("additionalPenalties").asInt());
        assertEquals(2, node.get("overallRanking").asInt());
        assertEquals(1, node.get("clubRanking").asInt());
        assertFalse(node.get("isVisitor").asBoolean());
    }

    @Test
    void testJsonSerialization_whenOnlyRequiredFieldsSet_thenSerializesWithNullOptionals() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        MatchCompetitorRequest request = new MatchCompetitorRequest(null, 1L, null, 2L, null, List.of("Junior"), null,
                "Open Division", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null);

        // Act
        JsonNode node = mapper.readTree(mapper.writeValueAsString(request));

        // Assert
        assertEquals(1, node.get("competitorId").asInt());
        assertEquals(2, node.get("matchId").asInt());
        assertEquals("Junior", node.get("competitorCategory").get(0).asText());
        assertEquals("Open Division", node.get("division").asText());
        assertTrue(node.get("matchCompetitorId").isNull());
        assertTrue(node.get("matchClub").isNull());
        assertTrue(node.get("firearmType").isNull());
        assertTrue(node.get("powerFactor").isNull());
        assertTrue(node.get("points").isNull());
        assertTrue(node.get("overallRanking").isNull());
        assertTrue(node.get("clubRanking").isNull());
        assertTrue(node.get("isVisitor").isNull());
    }

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        String json = """
                {
                  "matchCompetitorId": 7,
                  "competitorId": 1,
                  "matchId": 2,
                  "matchClub": "HPSC",
                  "competitorCategory": ["Junior"],
                  "firearmType": "Handgun",
                  "division": "Open Division",
                  "powerFactor": "Major",
                  "points": 95.5,
                  "overallRanking": 2,
                  "clubRanking": 1,
                  "isVisitor": false
                }
                """;

        // Act
        MatchCompetitorRequest request = new ObjectMapper().readValue(json, MatchCompetitorRequest.class);

        // Assert
        assertEquals(7L, request.getMatchCompetitorId());
        assertEquals(1L, request.getCompetitorId());
        assertEquals(2L, request.getMatchId());
        assertEquals("HPSC", request.getMatchClub());
        assertEquals(List.of("Junior"), request.getCompetitorCategory());
        assertEquals("Handgun", request.getFirearmType());
        assertEquals("Open Division", request.getDivision());
        assertEquals("Major", request.getPowerFactor());
        assertEquals(0, new BigDecimal("95.5").compareTo(request.getPoints()));
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
        assertNull(request.getMatchCompetitorId());
        assertNull(request.getMatchClub());
        assertNull(request.getPowerFactor());
        assertNull(request.getPoints());
        assertNull(request.getOverallRanking());
        assertNull(request.getClubRanking());
        assertNull(request.getIsVisitor());
    }

    @Test
    void testJsonDeserialization_whenMultipleCompetitorCategoriesProvided_thenMapsAllOfThem() throws Exception {
        // Arrange
        String json = """
                {
                  "competitorId": 1,
                  "matchId": 2,
                  "competitorCategory": ["Junior", "Lady"],
                  "division": "Open Division"
                }
                """;

        // Act
        MatchCompetitorRequest request = new ObjectMapper().readValue(json, MatchCompetitorRequest.class);

        // Assert
        assertEquals(List.of("Junior", "Lady"), request.getCompetitorCategory());
    }

    @Test
    void testJsonDeserialization_whenFirearmTypeMissing_thenFirearmTypeIsNull() throws Exception {
        // Arrange - firearmType isn't a Jackson-required property
        String json = """
                {
                  "competitorId": 1,
                  "matchId": 2,
                  "competitorCategory": ["Junior"],
                  "division": "Open Division"
                }
                """;

        // Act
        MatchCompetitorRequest request = new ObjectMapper().readValue(json, MatchCompetitorRequest.class);

        // Assert
        assertNull(request.getFirearmType());
    }

    @Test
    void testJsonDeserialization_whenCompetitorIdMissingAndNameGiven_thenDeserializesWithNameOnly() throws Exception {
        // Arrange
        String json = """
                {
                  "name": "Jane Doe",
                  "matchId": 2,
                  "competitorCategory": ["Junior"],
                  "division": "Open Division"
                }
                """;

        // Act
        MatchCompetitorRequest request = new ObjectMapper().readValue(json, MatchCompetitorRequest.class);

        // Assert
        assertNull(request.getCompetitorId());
        assertEquals("Jane Doe", request.getCompetitorName());
    }

    @Test
    void testJsonDeserialization_whenMatchIdMissing_thenThrowsMismatchedInputException() {
        // Arrange
        String json = """
                {
                  "competitorId": 1,
                  "competitorCategory": ["Junior"],
                  "division": "Open Division"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> new ObjectMapper().readValue(json, MatchCompetitorRequest.class));
    }

    @Test
    void testJsonDeserialization_whenCompetitorCategoryMissing_thenThrowsMismatchedInputException() {
        // Arrange
        String json = """
                {
                  "competitorId": 1,
                  "matchId": 2,
                  "division": "Open Division"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> new ObjectMapper().readValue(json, MatchCompetitorRequest.class));
    }

    @Test
    void testJsonDeserialization_whenDivisionMissing_thenThrowsMismatchedInputException() {
        // Arrange
        String json = """
                {
                  "competitorId": 1,
                  "matchId": 2,
                  "competitorCategory": ["Junior"]
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> new ObjectMapper().readValue(json, MatchCompetitorRequest.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> new ObjectMapper().readValue("{}", MatchCompetitorRequest.class));
    }
}
