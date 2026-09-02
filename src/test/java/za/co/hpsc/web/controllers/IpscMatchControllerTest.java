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
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.services.IpscMatchService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscMatchControllerTest {

    @Mock
    private IpscMatchService ipscMatchService;

    @InjectMocks
    private IpscMatchController ipscMatchController;

    // createMatch()
    @Test
    void testCreateMatch_whenServiceSucceeds_thenReturns201() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        MatchResponse response = new MatchResponse();
        when(ipscMatchService.createMatch(request)).thenReturn(response);

        // Act
        ResponseEntity<MatchResponse> result = ipscMatchController.createMatch(request);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testCreateMatch_whenServiceSucceeds_thenResponseBodyIsReturnedFromService() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        MatchResponse response = new MatchResponse();
        when(ipscMatchService.createMatch(request)).thenReturn(response);

        // Act
        ResponseEntity<MatchResponse> result = ipscMatchController.createMatch(request);

        // Assert
        assertSame(response, result.getBody());
    }

    @Test
    void testCreateMatch_whenServiceSucceeds_thenDelegatesToService() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        when(ipscMatchService.createMatch(request)).thenReturn(new MatchResponse());

        // Act
        ipscMatchController.createMatch(request);

        // Assert
        verify(ipscMatchService).createMatch(request);
        verifyNoMoreInteractions(ipscMatchService);
    }

    @Test
    void testCreateMatch_whenServiceThrowsValidationException_thenExceptionPropagates() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        when(ipscMatchService.createMatch(request)).thenThrow(new ValidationException("Match name is required."));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchController.createMatch(request));
    }

    @Test
    void testCreateMatch_whenServiceThrowsNonFatalException_thenExceptionPropagates() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        when(ipscMatchService.createMatch(request)).thenThrow(new NonFatalException("No club found with name Unknown"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchController.createMatch(request));
    }

    // getMatch()
    @Test
    void testGetMatch_whenServiceSucceeds_thenReturns200() throws NonFatalException {
        // Arrange
        MatchResponse response = new MatchResponse();
        when(ipscMatchService.getMatch(1L)).thenReturn(response);

        // Act
        ResponseEntity<MatchResponse> result = ipscMatchController.getMatch(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testGetMatch_whenServiceSucceeds_thenDelegatesToService() throws NonFatalException {
        // Arrange
        when(ipscMatchService.getMatch(1L)).thenReturn(new MatchResponse());

        // Act
        ipscMatchController.getMatch(1L);

        // Assert
        verify(ipscMatchService).getMatch(1L);
        verifyNoMoreInteractions(ipscMatchService);
    }

    @Test
    void testGetMatch_whenServiceThrowsNonFatalException_thenExceptionPropagates() throws NonFatalException {
        // Arrange
        when(ipscMatchService.getMatch(99L)).thenThrow(new NonFatalException("No IPSC match found with ID 99"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchController.getMatch(99L));
    }

    // getAllMatches()
    @Test
    void testGetAllMatches_whenServiceSucceeds_thenReturns200() {
        // Arrange
        List<MatchResponse> response = List.of(new MatchResponse());
        when(ipscMatchService.getAllMatches()).thenReturn(response);

        // Act
        ResponseEntity<List<MatchResponse>> result = ipscMatchController.getAllMatches();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testGetAllMatches_whenServiceSucceeds_thenDelegatesToService() {
        // Arrange
        when(ipscMatchService.getAllMatches()).thenReturn(List.of());

        // Act
        ipscMatchController.getAllMatches();

        // Assert
        verify(ipscMatchService).getAllMatches();
        verifyNoMoreInteractions(ipscMatchService);
    }

    // patchMatch()
    @Test
    void testPatchMatch_whenServiceSucceeds_thenReturns200() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        MatchResponse response = new MatchResponse();
        when(ipscMatchService.patchMatch(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<MatchResponse> result = ipscMatchController.patchMatch(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testPatchMatch_whenServiceSucceeds_thenDelegatesToService() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        when(ipscMatchService.patchMatch(1L, request)).thenReturn(new MatchResponse());

        // Act
        ipscMatchController.patchMatch(1L, request);

        // Assert
        verify(ipscMatchService).patchMatch(1L, request);
        verifyNoMoreInteractions(ipscMatchService);
    }

    @Test
    void testPatchMatch_whenServiceThrowsNonFatalException_thenExceptionPropagates() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        when(ipscMatchService.patchMatch(99L, request)).thenThrow(new NonFatalException("No IPSC match found with ID 99"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchController.patchMatch(99L, request));
    }

    // updateMatch()
    @Test
    void testUpdateMatch_whenServiceSucceeds_thenReturns200() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        MatchResponse response = new MatchResponse();
        when(ipscMatchService.updateMatch(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<MatchResponse> result = ipscMatchController.updateMatch(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testUpdateMatch_whenServiceSucceeds_thenDelegatesToService() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        when(ipscMatchService.updateMatch(1L, request)).thenReturn(new MatchResponse());

        // Act
        ipscMatchController.updateMatch(1L, request);

        // Assert
        verify(ipscMatchService).updateMatch(1L, request);
        verifyNoMoreInteractions(ipscMatchService);
    }

    @Test
    void testUpdateMatch_whenServiceThrowsValidationException_thenExceptionPropagates() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        when(ipscMatchService.updateMatch(1L, request)).thenThrow(new ValidationException("Match name is required."));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchController.updateMatch(1L, request));
    }

    @Test
    void testUpdateMatch_whenServiceThrowsNonFatalException_thenExceptionPropagates() throws ValidationException, NonFatalException {
        // Arrange
        MatchRequest request = new MatchRequest();
        when(ipscMatchService.updateMatch(99L, request)).thenThrow(new NonFatalException("No IPSC match found with ID 99"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchController.updateMatch(99L, request));
    }
}
