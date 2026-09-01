package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchRequestTest {

    // JSON serialization
    @Test
    void testJsonSerialization_whenFullyPopulated_thenSerializesAllFieldsIncludingStages() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        MatchStageRequest stage = new MatchStageRequest(1L, 1, "Stage 1 - The Bank Job");
        MatchRequest request = new MatchRequest(1L, LocalDate.of(2026, 4, 10), "Club Championship",
                "Test Club", "Pistol", "Level 1", List.of(stage));

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals(1, node.get("matchId").asInt());
        assertEquals("2026-04-10", node.get("matchDate").asText());
        assertEquals("Club Championship", node.get("matchName").asText());
        assertEquals("Test Club", node.get("club").asText());
        assertEquals("Pistol", node.get("matchFirearmType").asText());
        assertEquals("Level 1", node.get("matchCategory").asText());
        assertEquals(1, node.get("stages").size());
        assertEquals(1, node.get("stages").get(0).get("stageNumber").asInt());
        assertEquals("Stage 1 - The Bank Job", node.get("stages").get(0).get("stageName").asText());
    }

    @Test
    void testJsonSerialization_whenOptionalFieldsAreNull_thenSerializesNulls() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        MatchRequest request = new MatchRequest(
                null, LocalDate.of(2026, 4, 10), "Club Championship", null, null, null, null);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertTrue(node.get("matchId").isNull());
        assertEquals("2026-04-10", node.get("matchDate").asText());
        assertEquals("Club Championship", node.get("matchName").asText());
        assertTrue(node.get("club").isNull());
        assertTrue(node.get("matchFirearmType").isNull());
        assertTrue(node.get("matchCategory").isNull());
        assertTrue(node.get("stages").isNull());
    }

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFieldsIncludingStages() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "matchId": 1,
                  "matchDate": "2026-04-10",
                  "matchName": "Club Championship",
                  "club": "Test Club",
                  "matchFirearmType": "Pistol",
                  "matchCategory": "Level 1",
                  "stages": [
                    { "stageNumber": 1, "stageName": "Stage 1 - The Bank Job" },
                    { "stageNumber": 2, "stageName": "Stage 2 - The Vault" }
                  ]
                }
                """;

        // Act
        MatchRequest request = mapper.readValue(json, MatchRequest.class);

        // Assert
        assertEquals(1L, request.getMatchId());
        assertEquals(LocalDate.of(2026, 4, 10), request.getMatchDate());
        assertEquals("Club Championship", request.getMatchName());
        assertEquals("Test Club", request.getClub());
        assertEquals("Pistol", request.getMatchFirearmType());
        assertEquals("Level 1", request.getMatchCategory());
        assertEquals(2, request.getStages().size());
        assertEquals(1, request.getStages().get(0).getStageNumber());
        assertEquals("Stage 1 - The Bank Job", request.getStages().get(0).getStageName());
        assertEquals(2, request.getStages().get(1).getStageNumber());
        assertEquals("Stage 2 - The Vault", request.getStages().get(1).getStageName());
    }

    @Test
    void testJsonDeserialization_whenOnlyRequiredFieldsProvided_thenLeavesOptionalFieldsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "matchDate": "2026-04-10",
                  "matchName": "Club Championship"
                }
                """;

        // Act
        MatchRequest request = mapper.readValue(json, MatchRequest.class);

        // Assert
        assertEquals(LocalDate.of(2026, 4, 10), request.getMatchDate());
        assertEquals("Club Championship", request.getMatchName());
        assertNull(request.getMatchId());
        assertNull(request.getClub());
        assertNull(request.getStages());
    }

    @Test
    void testJsonDeserialization_whenMatchDateMissing_thenThrowsMismatchedInputException() {
        // Arrange - the @JsonCreator constructor's matchDate/matchName params are marked
        // @JsonProperty(required = true), so Jackson enforces them as required creator properties
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "matchName": "Club Championship"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchRequest.class));
    }

    @Test
    void testJsonDeserialization_whenMatchNameMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "matchDate": "2026-04-10"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchRequest.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue("{}", MatchRequest.class));
    }
}
