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
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.services.MatchCompetitorService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchCompetitorControllerTest {

    @Mock
    private MatchCompetitorService matchCompetitorService;

    @InjectMocks
    private MatchCompetitorController matchCompetitorController;

    // createMatchCompetitor()
    @Test
    void testCreateMatchCompetitor_whenServiceSucceeds_thenReturns201() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        when(matchCompetitorService.createMatchCompetitor(request)).thenReturn(new MatchCompetitorResponse());

        // Act
        ResponseEntity<MatchCompetitorResponse> result = matchCompetitorController.createMatchCompetitor(request);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testCreateMatchCompetitor_whenServiceSucceeds_thenResponseBodyIsReturnedFromService() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        MatchCompetitorResponse response = new MatchCompetitorResponse();
        when(matchCompetitorService.createMatchCompetitor(request)).thenReturn(response);

        // Act
        ResponseEntity<MatchCompetitorResponse> result = matchCompetitorController.createMatchCompetitor(request);

        // Assert
        assertSame(response, result.getBody());
    }

    @Test
    void testCreateMatchCompetitor_whenServiceThrowsValidationException_thenPropagates() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        when(matchCompetitorService.createMatchCompetitor(request)).thenThrow(new ValidationException("invalid"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorController.createMatchCompetitor(request));
    }

    @Test
    void testCreateMatchCompetitor_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        when(matchCompetitorService.createMatchCompetitor(request)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchCompetitorController.createMatchCompetitor(request));
    }

    // updateMatchCompetitor()
    @Test
    void testUpdateMatchCompetitor_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        MatchCompetitorResponse response = new MatchCompetitorResponse();
        when(matchCompetitorService.updateMatchCompetitor(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<MatchCompetitorResponse> result = matchCompetitorController.updateMatchCompetitor(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testUpdateMatchCompetitor_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        when(matchCompetitorService.updateMatchCompetitor(1L, request)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchCompetitorController.updateMatchCompetitor(1L, request));
    }

    @Test
    void testUpdateMatchCompetitor_whenServiceThrowsValidationException_thenPropagates() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        when(matchCompetitorService.updateMatchCompetitor(1L, request)).thenThrow(new ValidationException("invalid"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorController.updateMatchCompetitor(1L, request));
    }

    // patchMatchCompetitor()
    @Test
    void testPatchMatchCompetitor_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        MatchCompetitorResponse response = new MatchCompetitorResponse();
        when(matchCompetitorService.patchMatchCompetitor(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<MatchCompetitorResponse> result = matchCompetitorController.patchMatchCompetitor(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testPatchMatchCompetitor_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        when(matchCompetitorService.patchMatchCompetitor(1L, request)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchCompetitorController.patchMatchCompetitor(1L, request));
    }

    @Test
    void testPatchMatchCompetitor_whenServiceThrowsValidationException_thenPropagates() {
        // Arrange
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        when(matchCompetitorService.patchMatchCompetitor(1L, request)).thenThrow(new ValidationException("invalid"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorController.patchMatchCompetitor(1L, request));
    }

    // getMatchCompetitor()
    @Test
    void testGetMatchCompetitor_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        MatchCompetitorResponse response = new MatchCompetitorResponse();
        when(matchCompetitorService.getMatchCompetitor(1L)).thenReturn(response);

        // Act
        ResponseEntity<MatchCompetitorResponse> result = matchCompetitorController.getMatchCompetitor(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testGetMatchCompetitor_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        when(matchCompetitorService.getMatchCompetitor(1L)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchCompetitorController.getMatchCompetitor(1L));
    }

    // getAllMatchCompetitors()
    @Test
    void testGetAllMatchCompetitors_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        List<MatchCompetitorResponse> response = List.of(new MatchCompetitorResponse());
        when(matchCompetitorService.getAllMatchCompetitors()).thenReturn(response);

        // Act
        ResponseEntity<List<MatchCompetitorResponse>> result = matchCompetitorController.getAllMatchCompetitors();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testGetAllMatchCompetitors_whenNoneExist_thenReturnsEmptyList() {
        // Arrange
        when(matchCompetitorService.getAllMatchCompetitors()).thenReturn(List.of());

        // Act
        ResponseEntity<List<MatchCompetitorResponse>> result = matchCompetitorController.getAllMatchCompetitors();

        // Assert
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
    }

    // deleteMatchCompetitor()
    @Test
    void testDeleteMatchCompetitor_whenServiceSucceeds_thenReturns204WithNoBody() {
        // Act
        ResponseEntity<Void> result = matchCompetitorController.deleteMatchCompetitor(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(matchCompetitorService).deleteMatchCompetitor(1L);
    }

    @Test
    void testDeleteMatchCompetitor_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        doThrow(new NonFatalException("not found")).when(matchCompetitorService).deleteMatchCompetitor(1L);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchCompetitorController.deleteMatchCompetitor(1L));
    }

    @Test
    void testDeleteMatchCompetitor_whenServiceThrowsValidationException_thenPropagates() {
        // Arrange
        doThrow(new ValidationException("referenced")).when(matchCompetitorService).deleteMatchCompetitor(1L);

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchCompetitorController.deleteMatchCompetitor(1L));
    }
}
