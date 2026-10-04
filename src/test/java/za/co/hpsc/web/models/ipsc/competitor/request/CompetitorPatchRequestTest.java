package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompetitorPatchRequestTest {

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenEmptyObject_thenLeavesEveryFieldNull() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Act
        CompetitorPatchRequest request = mapper.readValue("{}", CompetitorPatchRequest.class);

        // Assert
        assertNull(request.getFirstName());
        assertNull(request.getLastName());
        assertNull(request.getDateOfBirth());
        assertNull(request.getHomeClub());
        assertNull(request.getClubNumber());
        assertNull(request.getPaidUpSapsa());
        assertNull(request.getPaidUpClub());
        assertNull(request.getIsVerified());
    }

    @Test
    void testJsonDeserialization_whenEmailAddressesAreOmitted_thenEmailAddressesIsNull() throws Exception {
        // Arrange - null, not an empty list, is what tells the patch to leave the addresses alone
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "nickName": "Janie"
                }
                """;

        // Act
        CompetitorPatchRequest request = mapper.readValue(json, CompetitorPatchRequest.class);

        // Assert
        assertEquals("Janie", request.getNickName());
        assertNull(request.getEmailAddresses());
    }

    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "firstName": "Jane",
                  "lastName": "Doe",
                  "middleNames": "Ann",
                  "nickName": "Janie",
                  "dateOfBirth": "1990-01-01",
                  "gender": "Female",
                  "homeClub": "Test Club",
                  "sapsaNumber": 12345,
                  "competitorNumber": "C-1",
                  "clubNumber": "HPSC-001",
                  "idNumber": "9001015800083",
                  "cellphoneNumber": "0821234567",
                  "paidUpSapsa": true,
                  "paidUpClub": false,
                  "isVerified": true,
                  "emailAddresses": ["jane.doe@example.com", "jane2.doe@example.com"]
                }
                """;

        // Act
        CompetitorPatchRequest request = mapper.readValue(json, CompetitorPatchRequest.class);

        // Assert
        assertEquals("Jane", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("Ann", request.getMiddleNames());
        assertEquals("Janie", request.getNickName());
        assertEquals(LocalDate.of(1990, 1, 1), request.getDateOfBirth());
        assertEquals("Female", request.getGender());
        assertEquals("Test Club", request.getHomeClub());
        assertEquals(12345, request.getSapsaNumber());
        assertEquals("C-1", request.getCompetitorNumber());
        assertEquals("HPSC-001", request.getClubNumber());
        assertEquals("9001015800083", request.getIdNumber());
        assertEquals("0821234567", request.getCellphoneNumber());
        assertEquals(Boolean.TRUE, request.getPaidUpSapsa());
        assertEquals(Boolean.FALSE, request.getPaidUpClub());
        assertEquals(Boolean.TRUE, request.getIsVerified());
        assertEquals(List.of("jane.doe@example.com", "jane2.doe@example.com"), request.getEmailAddresses());
    }
}
