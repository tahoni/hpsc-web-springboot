package za.co.hpsc.web.models.ipsc.shooterlog.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShooterLogRequestTest {

    // JSON deserialization
    @Test
    void testJsonDeserialization_whenAllFieldsProvided_thenMapsOntoFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "startDate": "2026-09-01",
                  "endDate": "2026-09-30",
                  "matchIds": [10, 11]
                }
                """;

        // Act
        ShooterLogRequest request = mapper.readValue(json, ShooterLogRequest.class);

        // Assert
        assertEquals(LocalDate.of(2026, 9, 1), request.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), request.getEndDate());
        assertEquals(List.of(10L, 11L), request.getMatchIds());
    }

    @Test
    void testJsonDeserialization_whenMatchIdsOmitted_thenDefaultsToAnEmptyList() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "startDate": "2026-09-01",
                  "endDate": "2026-09-30"
                }
                """;

        // Act
        ShooterLogRequest request = mapper.readValue(json, ShooterLogRequest.class);

        // Assert
        assertNotNull(request.getMatchIds());
        assertTrue(request.getMatchIds().isEmpty());
    }

    @Test
    void testJsonDeserialization_whenStartDateOmitted_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = """
                {
                  "endDate": "2026-09-30"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, ShooterLogRequest.class));
    }

    // validate()
    @Test
    void testValidate_whenEndDateIsBeforeStartDate_thenThrowsValidationException() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 30), LocalDate.of(2026, 9, 1), null);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, request::validate);
        assertEquals("Shooter log end date cannot be before its start date.", exception.getMessage());
    }

    @Test
    void testValidate_whenEndDateIsMissing_thenThrowsValidationException() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), null, null);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, request::validate);
        assertEquals("Shooter log end date is required.", exception.getMessage());
    }

    @Test
    void testValidate_whenStartDateIsMissing_thenThrowsValidationException() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(null, LocalDate.of(2026, 9, 30), null);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, request::validate);
        assertEquals("Shooter log start date is required.", exception.getMessage());
    }

    @Test
    void testValidate_whenStartAndEndDateAreTheSameDay_thenDoesNotThrow() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 1), null);

        // Act & Assert
        assertDoesNotThrow(request::validate);
    }
}
