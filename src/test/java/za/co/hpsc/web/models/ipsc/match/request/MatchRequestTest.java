package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class MatchRequestTest {

    // JSON serialization
    @Test
    void testJsonSerialization_whenFullyPopulated_thenSerializesAllFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        MatchRequest request = new MatchRequest(1L, LocalDate.of(2026, 4, 10), "Club Championship",
                "Test Club", "Pistol", "Level 1", LocalTime.of(8, 0), LocalTime.of(17, 0),
                "https://example.com/matches/1"
        );

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals(1, node.get("matchId").asInt());
        assertEquals("2026-04-10", node.get("matchDate").asText());
        assertEquals("Club Championship", node.get("matchName").asText());
        assertEquals("08:00", node.get("startTime").asText());
        assertEquals("17:00", node.get("endTime").asText());
        assertEquals("Test Club", node.get("club").asText());
        assertEquals("Pistol", node.get("matchFirearmType").asText());
        assertEquals("Level 1", node.get("matchCategory").asText());
        assertEquals("https://example.com/matches/1", node.get("url").asText());
    }

    @Test
    void testJsonSerialization_whenOptionalFieldsAreNull_thenSerializesNulls() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        MatchRequest request = new MatchRequest(
                null, LocalDate.of(2026, 4, 10), "Club Championship", null, null, null, null, null, null);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertTrue(node.get("matchId").isNull());
        assertEquals("2026-04-10", node.get("matchDate").asText());
        assertEquals("Club Championship", node.get("matchName").asText());
        assertTrue(node.get("startTime").isNull());
        assertTrue(node.get("endTime").isNull());
        assertTrue(node.get("club").isNull());
        assertTrue(node.get("matchFirearmType").isNull());
        assertTrue(node.get("matchCategory").isNull());
        assertTrue(node.get("url").isNull());
    }

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "matchId": 1,
                  "matchDate": "2026-04-10",
                  "matchName": "Club Championship",
                  "startTime": "08:00",
                  "endTime": "17:00",
                  "club": "Test Club",
                  "matchFirearmType": "Pistol",
                  "matchCategory": "Level 1",
                  "url": "https://example.com/matches/1"
                }
                """;

        // Act
        MatchRequest request = mapper.readValue(json, MatchRequest.class);

        // Assert
        assertEquals(1L, request.getMatchId());
        assertEquals(LocalDate.of(2026, 4, 10), request.getMatchDate());
        assertEquals("Club Championship", request.getMatchName());
        assertEquals(LocalTime.of(8, 0), request.getStartTime());
        assertEquals(LocalTime.of(17, 0), request.getEndTime());
        assertEquals("Test Club", request.getClub());
        assertEquals("Pistol", request.getMatchFirearmType());
        assertEquals("Level 1", request.getMatchCategory());
        assertEquals("https://example.com/matches/1", request.getUrl());
    }

    @Test
    void testJsonDeserialization_whenOnlyRequiredFieldsProvided_thenLeavesOptionalFieldsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "matchDate": "2026-04-10",
                  "matchName": "Club Championship",
                  "matchFirearmType": "Pistol",
                  "matchCategory": "Level 1"
                }
                """;

        // Act
        MatchRequest request = mapper.readValue(json, MatchRequest.class);

        // Assert
        assertEquals(LocalDate.of(2026, 4, 10), request.getMatchDate());
        assertEquals("Club Championship", request.getMatchName());
        assertEquals("Pistol", request.getMatchFirearmType());
        assertEquals("Level 1", request.getMatchCategory());
        assertNull(request.getMatchId());
        assertNull(request.getStartTime());
        assertNull(request.getEndTime());
        assertNull(request.getClub());
        assertNull(request.getUrl());
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
    void testJsonDeserialization_whenMatchFirearmTypeMissing_thenMatchFirearmTypeIsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "matchDate": "2026-04-10",
                  "matchName": "Club Championship",
                  "matchCategory": "Level 1"
                }
                """;

        // Act
        MatchRequest request = mapper.readValue(json, MatchRequest.class);

        // Assert
        assertNull(request.getMatchFirearmType());
    }

    @Test
    void testJsonDeserialization_whenMatchCategoryMissing_thenMatchCategoryIsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "matchDate": "2026-04-10",
                  "matchName": "Club Championship",
                  "matchFirearmType": "Pistol"
                }
                """;

        // Act
        MatchRequest request = mapper.readValue(json, MatchRequest.class);

        // Assert
        assertNull(request.getMatchCategory());
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue("{}", MatchRequest.class));
    }

    // validate()
    @Test
    void testValidate_whenRequiredFieldsPresent_thenDoesNotThrow() {
        // Act & Assert
        assertDoesNotThrow(validRequest()::validate);
    }

    @Test
    void testValidate_whenMatchNameIsNullEmptyOrBlank_thenThrowsValidationException() {
        for (String matchName : new String[]{null, "", "   "}) {
            // Arrange
            MatchRequest request = validRequest();
            request.setMatchName(matchName);

            // Act
            ValidationException exception = assertThrows(ValidationException.class, request::validate);

            // Assert
            assertEquals("Match name is required.", exception.getMessage());
        }
    }

    @Test
    void testValidate_whenMatchDateIsNull_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest();
        request.setMatchDate(null);

        // Act
        ValidationException exception = assertThrows(ValidationException.class, request::validate);

        // Assert
        assertEquals("Match date is required.", exception.getMessage());
    }

    @Test
    void testValidate_whenMatchFirearmTypeIsNullEmptyOrBlank_thenThrowsValidationException() {
        for (String firearmType : new String[]{null, "", "   "}) {
            // Arrange
            MatchRequest request = validRequest();
            request.setMatchFirearmType(firearmType);

            // Act
            ValidationException exception = assertThrows(ValidationException.class, request::validate);

            // Assert
            assertEquals("Match firearm type is required.", exception.getMessage());
        }
    }

    @Test
    void testValidate_whenOptionalFieldsAreNull_thenDoesNotThrow() {
        // Arrange
        MatchRequest request = validRequest();
        request.setClub(null);
        request.setMatchCategory(null);
        request.setStartTime(null);
        request.setEndTime(null);
        request.setUrl(null);

        // Act & Assert
        assertDoesNotThrow(request::validate);
    }

    // Helpers
    private MatchRequest validRequest() {
        MatchRequest request = new MatchRequest();
        request.setMatchName("Club Championship");
        request.setMatchDate(LocalDate.of(2026, 4, 10));
        request.setMatchFirearmType("Pistol");
        return request;
    }
}
