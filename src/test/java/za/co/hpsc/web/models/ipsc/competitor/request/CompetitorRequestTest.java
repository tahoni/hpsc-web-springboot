package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompetitorRequestTest {

    // JSON serialization
    @Test
    void testJsonSerialization_whenFullyPopulated_thenSerializesAllFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CompetitorRequest request = new CompetitorRequest(
                1L, "Jane", "Doe", "Ann", "Janie", LocalDate.of(1990, 1, 1), "Female", "Test Club",
                12345, "C-1", "HPSC-001", "9001015800083", "0821234567", List.of("jane.doe@example.com"));

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals(1, node.get("competitorId").asInt());
        assertEquals("Jane", node.get("firstName").asText());
        assertEquals("Doe", node.get("lastName").asText());
        assertEquals("Ann", node.get("middleNames").asText());
        assertEquals("Janie", node.get("nickname").asText());
        assertEquals("1990-01-01", node.get("dateOfBirth").asText());
        assertEquals("Female", node.get("gender").asText());
        assertEquals("Test Club", node.get("homeClub").asText());
        assertEquals(12345, node.get("sapsaNumber").asInt());
        assertEquals("C-1", node.get("competitorNumber").asText());
        assertEquals("HPSC-001", node.get("clubNumber").asText());
        assertEquals("9001015800083", node.get("idNumber").asText());
        assertEquals("0821234567", node.get("cellphoneNumber").asText());
        assertEquals(1, node.get("emailAddresses").size());
        assertEquals("jane.doe@example.com", node.get("emailAddresses").get(0).asText());
    }

    @Test
    void testJsonSerialization_whenMultipleEmailAddresses_thenSerializesAllOfThem() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CompetitorRequest request = new CompetitorRequest(
                1L, "Jane", "Doe", "Ann", "Janie", LocalDate.of(1990, 1, 1), "Female", "Test Club",
                12345, "C-1", "HPSC-001", "9001015800083", "0821234567",
                List.of("jane.doe@example.com", "jane2.doe@example.com"));

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals(2, node.get("emailAddresses").size());
        assertEquals("jane.doe@example.com", node.get("emailAddresses").get(0).asText());
        assertEquals("jane2.doe@example.com", node.get("emailAddresses").get(1).asText());
    }

    @Test
    void testJsonSerialization_whenOnlyRequiredFieldsSet_thenSerializesWithNullOptionals() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CompetitorRequest request = new CompetitorRequest(
                null, "Jane", "Doe", null, null, null, null, null, null, null, "HPSC-001", null, null, null);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertTrue(node.get("competitorId").isNull());
        assertEquals("Jane", node.get("firstName").asText());
        assertEquals("Doe", node.get("lastName").asText());
        assertEquals("HPSC-001", node.get("clubNumber").asText());
        assertTrue(node.get("middleNames").isNull());
        assertTrue(node.get("dateOfBirth").isNull());
        assertTrue(node.get("competitorNumber").isNull());
    }

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "competitorId": 1,
                  "firstName": "Jane",
                  "lastName": "Doe",
                  "middleNames": "Ann",
                  "nickname": "Janie",
                  "dateOfBirth": "1990-01-01",
                  "gender": "Female",
                  "homeClub": "Test Club",
                  "sapsaNumber": 12345,
                  "competitorNumber": "C-1",
                  "clubNumber": "HPSC-001",
                  "idNumber": "9001015800083",
                  "cellphoneNumber": "0821234567",
                  "emailAddresses": ["jane.doe@example.com"]
                }
                """;

        // Act
        CompetitorRequest request = mapper.readValue(json, CompetitorRequest.class);

        // Assert
        assertEquals(1L, request.getCompetitorId());
        assertEquals("Jane", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("Ann", request.getMiddleNames());
        assertEquals("Janie", request.getNickname());
        assertEquals(LocalDate.of(1990, 1, 1), request.getDateOfBirth());
        assertEquals("Female", request.getGender());
        assertEquals("Test Club", request.getHomeClub());
        assertEquals(12345, request.getSapsaNumber());
        assertEquals("C-1", request.getCompetitorNumber());
        assertEquals("HPSC-001", request.getClubNumber());
        assertEquals("9001015800083", request.getIdNumber());
        assertEquals("0821234567", request.getCellphoneNumber());
        assertEquals(List.of("jane.doe@example.com"), request.getEmailAddresses());
    }

    @Test
    void testJsonDeserialization_whenMultipleEmailAddressesProvided_thenMapsAllOfThem() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "firstName": "Jane",
                  "lastName": "Doe",
                  "clubNumber": "HPSC-001",
                  "emailAddresses": ["jane.doe@example.com", "jane2.doe@example.com"]
                }
                """;

        // Act
        CompetitorRequest request = mapper.readValue(json, CompetitorRequest.class);

        // Assert
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), request.getEmailAddresses());
    }

    @Test
    void testJsonDeserialization_whenOnlyRequiredFieldsProvided_thenLeavesOptionalFieldsNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "firstName": "Jane",
                  "lastName": "Doe",
                  "clubNumber": "HPSC-001"
                }
                """;

        // Act
        CompetitorRequest request = mapper.readValue(json, CompetitorRequest.class);

        // Assert
        assertEquals("Jane", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("HPSC-001", request.getClubNumber());
        assertNull(request.getCompetitorId());
        assertNull(request.getCompetitorNumber());
        assertNull(request.getGender());
        assertNull(request.getHomeClub());
    }

    @Test
    void testJsonDeserialization_whenCompetitorNumberMissing_thenDoesNotThrow() {
        // Arrange - competitorNumber isn't required; only firstName, lastName and clubNumber are
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "firstName": "Jane",
                  "lastName": "Doe",
                  "clubNumber": "HPSC-001"
                }
                """;

        // Act & Assert
        assertDoesNotThrow(() -> mapper.readValue(json, CompetitorRequest.class));
    }

    @Test
    void testJsonDeserialization_whenFirstNameMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "lastName": "Doe",
                  "clubNumber": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, CompetitorRequest.class));
    }

    @Test
    void testJsonDeserialization_whenLastNameMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "firstName": "Jane",
                  "clubNumber": "HPSC-001"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, CompetitorRequest.class));
    }

    @Test
    void testJsonDeserialization_whenClubNumberMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "firstName": "Jane",
                  "lastName": "Doe"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, CompetitorRequest.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue("{}", CompetitorRequest.class));
    }
}
