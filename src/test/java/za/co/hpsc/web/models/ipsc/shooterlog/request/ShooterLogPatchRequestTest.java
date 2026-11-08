package za.co.hpsc.web.models.ipsc.shooterlog.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShooterLogPatchRequestTest {

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenEmptyObject_thenLeavesEveryFieldNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Act
        ShooterLogPatchRequest request = mapper.readValue("{}", ShooterLogPatchRequest.class);

        // Assert
        assertNull(request.getStartDate());
        assertNull(request.getEndDate());
        assertNull(request.getMatchIds());
    }

    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "startDate": "2026-10-01",
                  "endDate": "2026-10-31",
                  "matchIds": [12]
                }
                """;

        // Act
        ShooterLogPatchRequest request = mapper.readValue(json, ShooterLogPatchRequest.class);

        // Assert
        assertEquals(LocalDate.of(2026, 10, 1), request.getStartDate());
        assertEquals(LocalDate.of(2026, 10, 31), request.getEndDate());
        assertEquals(List.of(12L), request.getMatchIds());
    }

    @Test
    void testJsonDeserialization_whenMatchIdsIsAnEmptyList_thenKeepsItEmptyRatherThanNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Act
        ShooterLogPatchRequest request = mapper.readValue("{\"matchIds\": []}", ShooterLogPatchRequest.class);

        // Assert
        assertNotNull(request.getMatchIds());
        assertTrue(request.getMatchIds().isEmpty());
    }
}
