package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.services.IpscCompetitorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscCompetitorControllerTest {

    @Mock
    private IpscCompetitorService ipscCompetitorService;

    @InjectMocks
    private IpscCompetitorController ipscCompetitorController;

    // createCompetitor()
    @Test
    void testCreateCompetitor_whenServiceSucceeds_thenReturns201() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        CompetitorResponse response = new CompetitorResponse();
        when(ipscCompetitorService.createCompetitor(request)).thenReturn(response);

        // Act
        ResponseEntity<CompetitorResponse> result = ipscCompetitorController.createCompetitor(request);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testCreateCompetitor_whenServiceSucceeds_thenResponseBodyIsReturnedFromService() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        CompetitorResponse response = new CompetitorResponse();
        when(ipscCompetitorService.createCompetitor(request)).thenReturn(response);

        // Act
        ResponseEntity<CompetitorResponse> result = ipscCompetitorController.createCompetitor(request);

        // Assert
        assertSame(response, result.getBody());
    }

    @Test
    void testCreateCompetitor_whenServiceSucceeds_thenDelegatesToService() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        when(ipscCompetitorService.createCompetitor(request)).thenReturn(new CompetitorResponse());

        // Act
        ipscCompetitorController.createCompetitor(request);

        // Assert
        verify(ipscCompetitorService).createCompetitor(request);
        verifyNoMoreInteractions(ipscCompetitorService);
    }

    @Test
    void testCreateCompetitor_whenServiceThrowsValidationException_thenExceptionPropagates() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        when(ipscCompetitorService.createCompetitor(request)).thenThrow(new ValidationException("First name is required."));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorController.createCompetitor(request));
    }

    @Test
    void testCreateCompetitor_whenServiceThrowsNonFatalException_thenExceptionPropagates() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        when(ipscCompetitorService.createCompetitor(request)).thenThrow(new NonFatalException("No club found with name Unknown"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorController.createCompetitor(request));
    }

    // getCompetitor()
    @Test
    void testGetCompetitor_whenServiceSucceeds_thenReturns200() throws NonFatalException {
        // Arrange
        CompetitorResponse response = new CompetitorResponse();
        when(ipscCompetitorService.getCompetitor(1L)).thenReturn(response);

        // Act
        ResponseEntity<CompetitorResponse> result = ipscCompetitorController.getCompetitor(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testGetCompetitor_whenServiceSucceeds_thenDelegatesToService() throws NonFatalException {
        // Arrange
        when(ipscCompetitorService.getCompetitor(1L)).thenReturn(new CompetitorResponse());

        // Act
        ipscCompetitorController.getCompetitor(1L);

        // Assert
        verify(ipscCompetitorService).getCompetitor(1L);
        verifyNoMoreInteractions(ipscCompetitorService);
    }

    @Test
    void testGetCompetitor_whenServiceThrowsNonFatalException_thenExceptionPropagates() throws NonFatalException {
        // Arrange
        when(ipscCompetitorService.getCompetitor(99L)).thenThrow(new NonFatalException("No competitor found with ID 99"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorController.getCompetitor(99L));
    }

    // patchCompetitor()
    @Test
    void testPatchCompetitor_whenServiceSucceeds_thenReturns200() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        CompetitorResponse response = new CompetitorResponse();
        when(ipscCompetitorService.patchCompetitor(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<CompetitorResponse> result = ipscCompetitorController.patchCompetitor(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testPatchCompetitor_whenServiceSucceeds_thenDelegatesToService() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        when(ipscCompetitorService.patchCompetitor(1L, request)).thenReturn(new CompetitorResponse());

        // Act
        ipscCompetitorController.patchCompetitor(1L, request);

        // Assert
        verify(ipscCompetitorService).patchCompetitor(1L, request);
        verifyNoMoreInteractions(ipscCompetitorService);
    }

    @Test
    void testPatchCompetitor_whenServiceThrowsNonFatalException_thenExceptionPropagates() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        when(ipscCompetitorService.patchCompetitor(99L, request)).thenThrow(new NonFatalException("No competitor found with ID 99"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorController.patchCompetitor(99L, request));
    }

    // updateCompetitor()
    @Test
    void testUpdateCompetitor_whenServiceSucceeds_thenReturns200() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        CompetitorResponse response = new CompetitorResponse();
        when(ipscCompetitorService.updateCompetitor(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<CompetitorResponse> result = ipscCompetitorController.updateCompetitor(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testUpdateCompetitor_whenServiceSucceeds_thenDelegatesToService() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        when(ipscCompetitorService.updateCompetitor(1L, request)).thenReturn(new CompetitorResponse());

        // Act
        ipscCompetitorController.updateCompetitor(1L, request);

        // Assert
        verify(ipscCompetitorService).updateCompetitor(1L, request);
        verifyNoMoreInteractions(ipscCompetitorService);
    }

    @Test
    void testUpdateCompetitor_whenServiceThrowsValidationException_thenExceptionPropagates() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        when(ipscCompetitorService.updateCompetitor(1L, request)).thenThrow(new ValidationException("First name is required."));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscCompetitorController.updateCompetitor(1L, request));
    }

    @Test
    void testUpdateCompetitor_whenServiceThrowsNonFatalException_thenExceptionPropagates() throws ValidationException, NonFatalException {
        // Arrange
        CompetitorRequest request = new CompetitorRequest();
        when(ipscCompetitorService.updateCompetitor(99L, request)).thenThrow(new NonFatalException("No competitor found with ID 99"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscCompetitorController.updateCompetitor(99L, request));
    }
}
