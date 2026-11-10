package za.co.hpsc.web.models.ipsc.competitor.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.exceptions.ValidationException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompetitorVerifyRequestTest {
    private final ObjectMapper mapper = new ObjectMapper();

    // JSON binding
    @Test
    void testDeserialise_whenCompetitorIdsAreGiven_thenBindsThem() throws Exception {
        // Act
        CompetitorVerifyRequest request =
                mapper.readValue("{\"competitorIds\": [1, 2, 3]}", CompetitorVerifyRequest.class);

        // Assert
        assertEquals(List.of(1L, 2L, 3L), request.getCompetitorIds());
    }

    @Test
    void testDeserialise_whenCompetitorIdsAreMissing_thenFails() {
        // Act & Assert
        assertThrows(Exception.class, () -> mapper.readValue("{}", CompetitorVerifyRequest.class));
    }

    // validate()
    @Test
    void testValidate_whenAtLeastOneIdIsGiven_thenDoesNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> new CompetitorVerifyRequest(List.of(1L)).validate());
    }

    @Test
    void testValidate_whenIdsAreNullOrEmpty_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> new CompetitorVerifyRequest(null).validate());
        assertThrows(ValidationException.class, () -> new CompetitorVerifyRequest(List.of()).validate());
    }

    @Test
    void testValidate_whenAnIdIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class,
                () -> new CompetitorVerifyRequest(Arrays.asList(1L, null)).validate());
    }

    @Test
    void testValidate_whenTheListIsImmutable_thenDoesNotFailOnTheNullCheck() {
        // Act & Assert: List.of rejects contains(null), so the check must not rely on it
        assertDoesNotThrow(() -> new CompetitorVerifyRequest(List.of(1L, 2L)).validate());
    }
}
