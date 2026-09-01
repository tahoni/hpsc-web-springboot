package za.co.hpsc.web.models.ipsc.scores.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.PowerFactor;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchStageScoresRequestTest {

    // JSON serialization
    @Test
    void testJsonSerialization_whenFullyPopulated_thenSerializesAllFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        MatchStageScoresRequest request = new MatchStageScoresRequest(
                1L, 2, "Jane Doe", new BigDecimal("95.5"), new BigDecimal("85.0"), 90,
                new BigDecimal("7.5"), new BigDecimal("12.0"), Division.OPEN, ClubIdentifier.HPSC,
                List.of(CompetitorCategory.LADY), PowerFactor.MAJOR, "HPSC-001",
                8, 1, 0, 0, 0, 0, 0, 0);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals(1, node.get("matchId").asInt());
        assertEquals(2, node.get("stageNumber").asInt());
        assertEquals("Jane Doe", node.get("name").asText());
        assertEquals("HPSC-001", node.get("membershipNumber").asText());
        assertEquals(8, node.get("alpha").asInt());
    }

    @Test
    void testJsonSerialization_whenOnlyRequiredFieldsSet_thenSerializesWithNullOptionals() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        MatchStageScoresRequest request = new MatchStageScoresRequest(
                1L, 2, "Jane Doe", null, null, null, null, null, null, null, null, null,
                "HPSC-001", null, null, null, null, null, null, null, null);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals(1, node.get("matchId").asInt());
        assertEquals(2, node.get("stageNumber").asInt());
        assertEquals("Jane Doe", node.get("name").asText());
        assertEquals("HPSC-001", node.get("membershipNumber").asText());
        assertTrue(node.get("stagePercentage").isNull());
    }

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "matchId": 1,
                  "stageNumber": 2,
                  "name": "Jane Doe",
                  "stagePercentage": 95.5,
                  "stagePoints": 85.0,
                  "points": 90,
                  "hitFactor": 7.5,
                  "time": 12.0,
                  "division": "OPEN",
                  "club": "HPSC",
                  "categories": ["LADY"],
                  "powerFactor": "MAJOR",
                  "membershipNumber": "HPSC-001",
                  "alpha": 8,
                  "charlie": 1,
                  "delta": 0,
                  "misses": 0,
                  "noPenaltyMisses": 0,
                  "noShoots": 0,
                  "proceduralErrors": 0,
                  "additionalPenalties": 0
                }
                """;

        // Act
        MatchStageScoresRequest request = mapper.readValue(json, MatchStageScoresRequest.class);

        // Assert
        assertEquals(1L, request.getMatchId());
        assertEquals(2, request.getStageNumber());
        assertEquals("Jane Doe", request.getName());
        assertEquals(new BigDecimal("95.5"), request.getStagePercentage());
        assertEquals(Division.OPEN, request.getDivision());
        assertEquals(ClubIdentifier.HPSC, request.getClub());
        assertEquals(List.of(CompetitorCategory.LADY), request.getCategories());
        assertEquals(PowerFactor.MAJOR, request.getPowerFactor());
        assertEquals("HPSC-001", request.getMembershipNumber());
        assertEquals(8, request.getAlpha());
    }

    @Test
    void testJsonDeserialization_whenOnlyRequiredFieldsProvided_thenLeavesOptionalFieldsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "matchId": 1,
                  "stageNumber": 2,
                  "name": "Jane Doe",
                  "membershipNumber": "HPSC-001"
                }
                """;

        // Act
        MatchStageScoresRequest request = mapper.readValue(json, MatchStageScoresRequest.class);

        // Assert
        assertEquals(1L, request.getMatchId());
        assertEquals(2, request.getStageNumber());
        assertEquals("Jane Doe", request.getName());
        assertEquals("HPSC-001", request.getMembershipNumber());
        assertNull(request.getStagePercentage());
        assertNull(request.getDivision());
    }

    @Test
    void testJsonDeserialization_whenMatchIdMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "stageNumber": 2,
                  "name": "Jane Doe",
                  "membershipNumber": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchStageScoresRequest.class));
    }

    @Test
    void testJsonDeserialization_whenStageNumberMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "matchId": 1,
                  "name": "Jane Doe",
                  "membershipNumber": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchStageScoresRequest.class));
    }

    @Test
    void testJsonDeserialization_whenNameMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "matchId": 1,
                  "stageNumber": 2,
                  "membershipNumber": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchStageScoresRequest.class));
    }

    @Test
    void testJsonDeserialization_whenMembershipNumberMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "matchId": 1,
                  "stageNumber": 2,
                  "name": "Jane Doe"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchStageScoresRequest.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue("{}", MatchStageScoresRequest.class));
    }
}
