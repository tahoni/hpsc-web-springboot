package za.co.hpsc.web.models.ipsc.match.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class MatchPatchRequestTest {

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenEmptyObject_thenLeavesEveryFieldNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Act
        MatchPatchRequest request = mapper.readValue("{}", MatchPatchRequest.class);

        // Assert
        assertNull(request.getMatchDate());
        assertNull(request.getStartTime());
        assertNull(request.getEndTime());
        assertNull(request.getMatchName());
        assertNull(request.getClub());
        assertNull(request.getMatchFirearmType());
        assertNull(request.getMatchCategory());
        assertNull(request.getUrl());
    }

    @Test
    void testJsonDeserialization_whenOnlyOneFieldProvided_thenLeavesTheRestNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "matchName": "Renamed Championship"
                }
                """;

        // Act
        MatchPatchRequest request = mapper.readValue(json, MatchPatchRequest.class);

        // Assert
        assertEquals("Renamed Championship", request.getMatchName());
        assertNull(request.getMatchDate());
        assertNull(request.getMatchFirearmType());
        assertNull(request.getMatchCategory());
    }

    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "matchDate": "2026-04-10",
                  "startTime": "08:00",
                  "endTime": "17:00",
                  "matchName": "Club Championship",
                  "club": "Test Club",
                  "matchFirearmType": "Pistol",
                  "matchCategory": "Level 1",
                  "url": "https://example.com/matches/1"
                }
                """;

        // Act
        MatchPatchRequest request = mapper.readValue(json, MatchPatchRequest.class);

        // Assert
        assertEquals(LocalDate.of(2026, 4, 10), request.getMatchDate());
        assertEquals(LocalTime.of(8, 0), request.getStartTime());
        assertEquals(LocalTime.of(17, 0), request.getEndTime());
        assertEquals("Club Championship", request.getMatchName());
        assertEquals("Test Club", request.getClub());
        assertEquals("Pistol", request.getMatchFirearmType());
        assertEquals("Level 1", request.getMatchCategory());
        assertEquals("https://example.com/matches/1", request.getUrl());
    }
}
