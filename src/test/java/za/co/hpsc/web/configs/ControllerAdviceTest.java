package za.co.hpsc.web.configs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.RecalculationInProgressException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ControllerResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControllerAdviceTest {

    @InjectMocks
    private ControllerAdvice controllerAdvice;

    @Mock
    private WebRequest webRequest;

    // handleGeneralException()
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
    void testHandleGeneralException_whenFatalExceptionThrown_thenResponseBodyDescribesTheError() {
        // Arrange
        FatalException ex = new FatalException("Database connection failed");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleGeneralException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Database connection failed", response.getBody().getMessage());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertFalse(response.getBody().isSuccess());
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

    // handleValidationException()
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
    void testHandleValidationException_whenValidationExceptionThrown_thenResponseBodyDescribesTheError() {
        // Arrange
        ValidationException ex = new ValidationException("Field 'name' must not be blank");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleValidationException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Field 'name' must not be blank", response.getBody().getMessage());
        assertEquals("Bad Request", response.getBody().getError());
        assertFalse(response.getBody().isSuccess());
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

    // handleRecalculationInProgressException()
    @Test
    void testHandleRecalculationInProgressException_whenThrown_thenReturns409AndTheMessage() {
        // Arrange
        RecalculationInProgressException ex = new RecalculationInProgressException("try again shortly");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleRecalculationInProgressException(ex,
                webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("try again shortly", response.getBody().getMessage());
    }

    // handleNonFatalException()
    @Test
    void testHandleNonFatalException_whenNonFatalExceptionThrown_thenReturns404() {
        // Arrange
        NonFatalException ex = new NonFatalException("Match not found");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleNonFatalException(ex,
                webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testHandleNonFatalException_whenNonFatalExceptionThrown_thenResponseBodyDescribesTheError() {
        // Arrange
        NonFatalException ex = new NonFatalException("Competitor already enrolled");

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleNonFatalException(ex,
                webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Competitor already enrolled", response.getBody().getMessage());
        assertEquals("Not Found", response.getBody().getError());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testHandleNonFatalException_whenNonFatalExceptionHasNullMessage_thenResponseBodyMessageIsNull() {
        // Arrange
        NonFatalException ex = new NonFatalException((String) null);

        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleNonFatalException(ex,
                webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertNull(response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // handleHttpMessageConversionException()
    @Test
    void testHandleHttpMessageConversionException_whenThrown_thenReturns400() {
        // Arrange
        HttpMessageConversionException ex = new HttpMessageConversionException("Invalid JSON input");

        // Act
        ResponseEntity<ControllerResponse> response =
                controllerAdvice.handleHttpMessageConversionException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleHttpMessageConversionException_whenThrown_thenResponseBodyDescribesTheError() {
        // Arrange
        HttpMessageConversionException ex = new HttpMessageConversionException("Cannot deserialize value");

        // Act
        ResponseEntity<ControllerResponse> response =
                controllerAdvice.handleHttpMessageConversionException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Cannot deserialize value", response.getBody().getMessage());
        assertEquals("Bad Request", response.getBody().getError());
        assertFalse(response.getBody().isSuccess());
    }

    // handleUnhandledException()
    @Test
    void testHandleUnhandledException_whenThrown_thenReturns500() {
        // Arrange
        Exception ex = new Exception("Something went wrong");

        // Act
        ResponseEntity<ControllerResponse> response =
                controllerAdvice.handleUnhandledException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testHandleUnhandledException_whenThrown_thenResponseBodyDescribesTheError() {
        // Arrange
        Exception ex = new Exception("Uncaught processing error");

        // Act
        ResponseEntity<ControllerResponse> response =
                controllerAdvice.handleUnhandledException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Uncaught processing error", response.getBody().getMessage());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testHandleUnhandledException_whenMessageIsNull_thenResponseBodyUsesDefaultMessage() {
        // Arrange
        Exception ex = new Exception((String) null);

        // Act
        ResponseEntity<ControllerResponse> response =
                controllerAdvice.handleUnhandledException(ex, webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Unexpected error occurred", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // logError()
    @Test
    void testLogError_whenThrowableIsNull_thenDoesNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> controllerAdvice.logError(null));
    }

    @Test
    void testLogError_whenThrowableHasCause_thenDoesNotThrow() {
        // Arrange
        Exception cause = new IllegalStateException("Root cause");
        Exception ex = new Exception("Wrapping exception", cause);

        // Act & Assert
        assertDoesNotThrow(() -> controllerAdvice.logError(ex));
    }

    @Test
    void testLogError_whenRequestIsNull_thenDoesNotThrow() {
        // Arrange
        Exception ex = new Exception("No request context");

        // Act & Assert
        assertDoesNotThrow(() -> controllerAdvice.logError(ex, null));
    }

    // handleMethodArgumentNotValidException()
    private MethodArgumentNotValidException notValidException(String... messages) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        for (String message : messages) {
            bindingResult.addError(new ObjectError("request", message));
        }
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        return ex;
    }

    @Test
    void testHandleMethodArgumentNotValidException_whenThrown_thenReturns400() {
        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice
                .handleMethodArgumentNotValidException(notValidException("First name is required."), webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleMethodArgumentNotValidException_whenSeveralErrors_thenMessageListsAllSorted() {
        // Act
        ResponseEntity<ControllerResponse> response = controllerAdvice.handleMethodArgumentNotValidException(
                notValidException("Last name is required.", "First name is required."), webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("First name is required. Last name is required.", response.getBody().getMessage());
    }

    @Test
    void testHandleMethodArgumentNotValidException_whenNoMessages_thenUsesDefaultMessage() {
        // Act
        ResponseEntity<ControllerResponse> response =
                controllerAdvice.handleMethodArgumentNotValidException(notValidException(), webRequest);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Request validation failed", response.getBody().getMessage());
    }
}
