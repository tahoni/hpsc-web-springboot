package za.co.hpsc.web.models.ipsc.match.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchResponseTest {
    private final ObjectMapper mapper = new ObjectMapper();

    // messages
    @Test
    void testMessages_whenEmpty_thenIsLeftOutOfTheJson() throws Exception {
        // Arrange
        MatchResponse response = new MatchResponse();

        // Act
        JsonNode json = mapper.readTree(mapper.writeValueAsString(response));

        // Assert
        assertTrue(response.getMessages().isEmpty());
        assertFalse(json.has("messages"));
        assertFalse(json.has("warnings"));
    }

    @Test
    void testMessages_whenFilled_thenIsWrittenToTheJsonAsMessages() throws Exception {
        // Arrange
        MatchResponse response = new MatchResponse();
        response.getMessages().add("Outside the dates of shooter log 7.");

        // Act
        JsonNode json = mapper.readTree(mapper.writeValueAsString(response));

        // Assert
        assertEquals(List.of("Outside the dates of shooter log 7."),
                mapper.convertValue(json.get("messages"), List.class));
        assertFalse(json.has("warnings"));
    }
}
