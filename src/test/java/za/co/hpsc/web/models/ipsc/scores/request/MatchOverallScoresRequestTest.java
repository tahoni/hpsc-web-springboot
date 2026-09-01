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

class MatchOverallScoresRequestTest {

    // JSON serialization
    @Test
    void testJsonSerialization_whenFullyPopulated_thenSerializesAllFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        MatchOverallScoresRequest request = new MatchOverallScoresRequest(
                1L, "Jane Doe", new BigDecimal("95.5"), new BigDecimal("450"), new BigDecimal("60.2"),
                new BigDecimal("88.0"), new BigDecimal("7.5"), Division.OPEN, ClubIdentifier.HPSC,
                List.of(CompetitorCategory.LADY), PowerFactor.MAJOR, "HPSC-001",
                40, 8, 2, 1, 0, 1, 1, 2);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals(1, node.get("matchId").asInt());
        assertEquals("Jane Doe", node.get("name").asText());
        assertEquals("HPSC-001", node.get("membershipNumber").asText());
        assertEquals(40, node.get("alpha").asInt());
        assertEquals("LADY", node.get("categories").get(0).asText());
    }

    @Test
    void testJsonSerialization_whenOnlyRequiredFieldsSet_thenSerializesWithNullOptionals() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        MatchOverallScoresRequest request = new MatchOverallScoresRequest(
                1L, "Jane Doe", null, null, null, null, null, null, null, null, null,
                "HPSC-001", null, null, null, null, null, null, null, null);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals(1, node.get("matchId").asInt());
        assertEquals("Jane Doe", node.get("name").asText());
        assertEquals("HPSC-001", node.get("membershipNumber").asText());
        assertTrue(node.get("percentage").isNull());
        assertTrue(node.get("division").isNull());
    }

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "matchId": 1,
                  "name": "Jane Doe",
                  "percentage": 95.5,
                  "points": 450,
                  "time": 60.2,
                  "percentageOfPossiblePoints": 88.0,
                  "hitFactor": 7.5,
                  "division": "OPEN",
                  "club": "HPSC",
                  "categories": ["LADY"],
                  "powerFactor": "MAJOR",
                  "membershipNumber": "HPSC-001",
                  "alpha": 40,
                  "charlie": 8,
                  "delta": 2,
                  "misses": 1,
                  "noPenaltyMisses": 0,
                  "noShoots": 1,
                  "proceduralErrors": 1,
                  "additionalPenalties": 2
                }
                """;

        // Act
        MatchOverallScoresRequest request = mapper.readValue(json, MatchOverallScoresRequest.class);

        // Assert
        assertEquals(1L, request.getMatchId());
        assertEquals("Jane Doe", request.getName());
        assertEquals(new BigDecimal("95.5"), request.getPercentage());
        assertEquals(Division.OPEN, request.getDivision());
        assertEquals(ClubIdentifier.HPSC, request.getClub());
        assertEquals(List.of(CompetitorCategory.LADY), request.getCategories());
        assertEquals(PowerFactor.MAJOR, request.getPowerFactor());
        assertEquals("HPSC-001", request.getMembershipNumber());
        assertEquals(40, request.getAlpha());
    }

    @Test
    void testJsonDeserialization_whenOnlyRequiredFieldsProvided_thenLeavesOptionalFieldsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "matchId": 1,
                  "name": "Jane Doe",
                  "membershipNumber": "HPSC-001"
                }
                """;

        // Act
        MatchOverallScoresRequest request = mapper.readValue(json, MatchOverallScoresRequest.class);

        // Assert
        assertEquals(1L, request.getMatchId());
        assertEquals("Jane Doe", request.getName());
        assertEquals("HPSC-001", request.getMembershipNumber());
        assertNull(request.getPercentage());
        assertNull(request.getDivision());
    }

    @Test
    void testJsonDeserialization_whenMatchIdMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "name": "Jane Doe",
                  "membershipNumber": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchOverallScoresRequest.class));
    }

    @Test
    void testJsonDeserialization_whenNameMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "matchId": 1,
                  "membershipNumber": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchOverallScoresRequest.class));
    }

    @Test
    void testJsonDeserialization_whenMembershipNumberMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "matchId": 1,
                  "name": "Jane Doe"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchOverallScoresRequest.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue("{}", MatchOverallScoresRequest.class));
    }
}
