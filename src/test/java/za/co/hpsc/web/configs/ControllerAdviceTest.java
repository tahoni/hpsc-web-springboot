package za.co.hpsc.web.configs;

import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ControllerResponse;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ControllerAdviceTest {

    @InjectMocks
    private ControllerAdvice controllerAdvice;

    @Mock
    private WebRequest webRequest;

    // =====================================================================
    // handleGeneralException(FatalException, WebRequest)
    // =====================================================================

    @Test
    void testHandleGeneralException_whenFatalExceptionThrown_thenReturns500() {
        // Arrange
        FatalException ex = new FatalException("A fatal error occurred");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleGeneralException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testHandleGeneralException_whenFatalExceptionThrown_thenResponseBodyContainsExceptionMessage() {
        // Arrange
        FatalException ex = new FatalException("Database connection failed");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleGeneralException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Database connection failed", response.getBody().getMessage());
    }

    @Test
    void testHandleGeneralException_whenFatalExceptionThrown_thenResponseBodyContainsInternalServerErrorLabel() {
        // Arrange
        FatalException ex = new FatalException("Something went wrong");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleGeneralException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().getError());
    }

    @Test
    void testHandleGeneralException_whenFatalExceptionThrown_thenResponseBodySuccessIsFalse() {
        // Arrange
        FatalException ex = new FatalException("Critical failure");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleGeneralException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testHandleGeneralException_whenFatalExceptionThrown_thenResponseBodyTimestampIsNotNull() {
        // Arrange
        FatalException ex = new FatalException("Timestamp check");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleGeneralException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleGeneralException_whenFatalExceptionHasNullMessage_thenResponseBodyMessageIsNull() {
        // Arrange
        FatalException ex = new FatalException((String) null);

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleGeneralException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertNull(response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testHandleGeneralException_whenFatalExceptionHasBlankMessage_thenResponseBodyPreservesBlankMessage() {
        // Arrange
        FatalException ex = new FatalException("   ");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleGeneralException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("   ", response.getBody().getMessage());
    }

    // =====================================================================
    // handleValidationException(ValidationException, WebRequest)
    // =====================================================================

    @Test
    void testHandleValidationException_whenValidationExceptionThrown_thenReturns400() {
        // Arrange
        ValidationException ex = new ValidationException("Invalid input data");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleValidationException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleValidationException_whenValidationExceptionThrown_thenResponseBodyContainsExceptionMessage() {
        // Arrange
        ValidationException ex = new ValidationException("Field 'name' must not be blank");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleValidationException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Field 'name' must not be blank", response.getBody().getMessage());
    }

    @Test
    void testHandleValidationException_whenValidationExceptionThrown_thenResponseBodyContainsBadRequestLabel() {
        // Arrange
        ValidationException ex = new ValidationException("Validation failed");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleValidationException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().getError());
    }

    @Test
    void testHandleValidationException_whenValidationExceptionThrown_thenResponseBodySuccessIsFalse() {
        // Arrange
        ValidationException ex = new ValidationException("Validation failed");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleValidationException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testHandleValidationException_whenValidationExceptionThrown_thenResponseBodyTimestampIsNotNull() {
        // Arrange
        ValidationException ex = new ValidationException("Timestamp check");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleValidationException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleValidationException_whenValidationExceptionHasNullMessage_thenResponseBodyMessageIsNull() {
        // Arrange
        ValidationException ex = new ValidationException((String) null);

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleValidationException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertNull(response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // =====================================================================
    // handleNonFatalException(NonFatalException, WebRequest)
    // =====================================================================

    @Test
    void testHandleNonFatalException_whenNonFatalExceptionThrown_thenReturns422() {
        // Arrange
        NonFatalException ex = new NonFatalException("Match not found");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleNonFataException(ex,
                webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testHandleNonFatalException_whenNonFatalExceptionThrown_thenResponseBodyContainsExceptionMessage() {
        // Arrange
        NonFatalException ex = new NonFatalException("Competitor already enrolled");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleNonFataException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Competitor already enrolled", response.getBody().getMessage());
    }

    @Test
    void testHandleNonFatalException_whenNonFatalExceptionThrown_thenResponseBodyContainsUnprocessableContentLabel() {
        // Arrange
        NonFatalException ex = new NonFatalException("Cannot process request");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleNonFataException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Not Found", response.getBody().getError());
    }

    @Test
    void testHandleNonFatalException_whenNonFatalExceptionThrown_thenResponseBodySuccessIsFalse() {
        // Arrange
        NonFatalException ex = new NonFatalException("Business rule violated");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleNonFataException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testHandleNonFatalException_whenNonFatalExceptionThrown_thenResponseBodyTimestampIsNotNull() {
        // Arrange
        NonFatalException ex = new NonFatalException("Timestamp check");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleNonFataException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleNonFatalException_whenNonFatalExceptionHasNullMessage_thenResponseBodyMessageIsNull() {
        // Arrange
        NonFatalException ex = new NonFatalException((String) null);

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleNonFataException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertNull(response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testHandleNonFatalException_whenNonFatalExceptionHasBlankMessage_thenResponseBodyPreservesBlankMessage() {
        // Arrange
        NonFatalException ex = new NonFatalException("   ");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleNonFataException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("   ", response.getBody().getMessage());
    }
}

