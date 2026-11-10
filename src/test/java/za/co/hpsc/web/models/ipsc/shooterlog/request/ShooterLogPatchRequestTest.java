package za.co.hpsc.web.models.ipsc.shooterlog.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.exceptions.ValidationException;

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
    void testJsonDeserialization_whenAddAndRemoveMatchIdsAreGiven_thenMapsThem() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Act
        ShooterLogPatchRequest request =
                mapper.readValue("{\"addMatchIds\": [3, 4], \"removeMatchIds\": [5]}", ShooterLogPatchRequest.class);

        // Assert
        assertEquals(List.of(3L, 4L), request.getAddMatchIds());
        assertEquals(List.of(5L), request.getRemoveMatchIds());
        assertNull(request.getMatchIds());
    }

    @Test
    void testValidate_whenNothingIsAddedOrRemoved_thenDoesNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> new ShooterLogPatchRequest().validate());
    }

    @Test
    void testValidate_whenAddAndRemoveMatchIdsDiffer_thenDoesNotThrow() {
        // Arrange
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setAddMatchIds(List.of(1L, 2L));
        request.setRemoveMatchIds(List.of(3L));

        // Act & Assert
        assertDoesNotThrow(request::validate);
    }

    @Test
    void testValidate_whenAMatchIsBothAddedAndRemoved_thenThrowsValidationException() {
        // Arrange
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        request.setAddMatchIds(List.of(1L, 2L));
        request.setRemoveMatchIds(List.of(2L));

        // Act & Assert
        assertThrows(ValidationException.class, request::validate);
    }

    @Test
    void testValidate_whenAnAddedOrRemovedMatchIdIsNull_thenThrowsValidationException() {
        // Arrange
        ShooterLogPatchRequest withNullAdd = new ShooterLogPatchRequest();
        withNullAdd.setAddMatchIds(java.util.Arrays.asList(1L, null));
        ShooterLogPatchRequest withNullRemove = new ShooterLogPatchRequest();
        withNullRemove.setRemoveMatchIds(java.util.Arrays.asList((Long) null));

        // Act & Assert
        assertThrows(ValidationException.class, withNullAdd::validate);
        assertThrows(ValidationException.class, withNullRemove::validate);
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
