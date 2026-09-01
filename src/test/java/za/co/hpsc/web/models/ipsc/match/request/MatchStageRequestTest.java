package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchStageRequestTest {

    // JSON serialization
    @Test
    void testJsonSerialization_whenFullyPopulated_thenSerializesAllFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        MatchStageRequest request = new MatchStageRequest(1L, 2, "Stage 1 - The Bank Job");

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals(1, node.get("matchId").asInt());
        assertEquals(2, node.get("stageNumber").asInt());
        assertEquals("Stage 1 - The Bank Job", node.get("stageName").asText());
    }

    @Test
    void testJsonSerialization_whenOptionalFieldsAreNull_thenSerializesNulls() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        MatchStageRequest request = new MatchStageRequest(null, 1, null);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertTrue(node.get("matchId").isNull());
        assertEquals(1, node.get("stageNumber").asInt());
        assertTrue(node.get("stageName").isNull());
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
                  "stageName": "Stage 1 - The Bank Job"
                }
                """;

        // Act
        MatchStageRequest request = mapper.readValue(json, MatchStageRequest.class);

        // Assert
        assertEquals(1L, request.getMatchId());
        assertEquals(2, request.getStageNumber());
        assertEquals("Stage 1 - The Bank Job", request.getStageName());
    }

    @Test
    void testJsonDeserialization_whenStageNumberMissing_thenThrowsMismatchedInputException() {
        // Arrange - the @JsonCreator constructor's stageNumber param is marked
        // @JsonProperty(required = true), so Jackson enforces it as a required creator property
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "stageName": "Stage 1 - The Bank Job"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, MatchStageRequest.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue("{}", MatchStageRequest.class));
    }
}
