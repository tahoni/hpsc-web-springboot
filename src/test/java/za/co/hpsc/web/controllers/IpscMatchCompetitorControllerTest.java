package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorBulkResponse;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorBulkResponseHolder;
import za.co.hpsc.web.services.IpscMatchCompetitorService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscMatchCompetitorControllerTest {

    @Mock
    private IpscMatchCompetitorService ipscMatchCompetitorService;

    @InjectMocks
    private IpscMatchCompetitorController ipscMatchCompetitorController;

    // createMatchCompetitor()
    @Test
    void testCreateMatchCompetitor_whenServiceSucceeds_thenReturns201() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        when(ipscMatchCompetitorService.createMatchCompetitor(request)).thenReturn(new MatchCompetitorResponse());

        // Act
        ResponseEntity<MatchCompetitorResponse> result = ipscMatchCompetitorController.createMatchCompetitor(request);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testCreateMatchCompetitor_whenServiceSucceeds_thenResponseBodyIsReturnedFromService() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        MatchCompetitorResponse response = new MatchCompetitorResponse();
        when(ipscMatchCompetitorService.createMatchCompetitor(request)).thenReturn(response);

        // Act
        ResponseEntity<MatchCompetitorResponse> result = ipscMatchCompetitorController.createMatchCompetitor(request);

        // Assert
        assertSame(response, result.getBody());
    }

    @Test
    void testCreateMatchCompetitor_whenServiceThrowsValidationException_thenPropagates() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        when(ipscMatchCompetitorService.createMatchCompetitor(request)).thenThrow(new ValidationException("invalid"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorController.createMatchCompetitor(request));
    }

    @Test
    void testCreateMatchCompetitor_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        when(ipscMatchCompetitorService.createMatchCompetitor(request)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorController.createMatchCompetitor(request));
    }

    // createMatchCompetitors()
    private static final String VALID_CSV = """
            CompetitorId,MatchId,Cats,FirearmType,Div
            1,2,Junior,Handgun,Open Division
            """;

    @Test
    void testCreateMatchCompetitors_whenServiceSucceeds_thenReturns201() throws Exception {
        // Arrange
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV))
                .thenReturn(new MatchCompetitorBulkResponseHolder(List.of()));

        // Act
        ResponseEntity<MatchCompetitorBulkResponseHolder> result =
                ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testCreateMatchCompetitors_whenServiceSucceeds_thenResponseBodyIsReturnedFromService() throws Exception {
        // Arrange
        MatchCompetitorBulkResponseHolder holder =
                new MatchCompetitorBulkResponseHolder(List.of(new MatchCompetitorBulkResponse()));
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<MatchCompetitorBulkResponseHolder> result =
                ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV);

        // Assert
        assertSame(holder, result.getBody());
    }

    @Test
    void testCreateMatchCompetitors_whenServiceSucceeds_thenDelegatesToService() throws Exception {
        // Arrange
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV))
                .thenReturn(new MatchCompetitorBulkResponseHolder(List.of()));

        // Act
        ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV);

        // Assert
        verify(ipscMatchCompetitorService).createMatchCompetitors(VALID_CSV);
    }

    @Test
    void testCreateMatchCompetitors_whenEveryRowFails_thenReturns422WithTheResults() throws Exception {
        // Arrange
        MatchCompetitorBulkResponse failed = new MatchCompetitorBulkResponse(false, "No competitor found",
                new MatchCompetitorResponse());
        MatchCompetitorBulkResponseHolder holder = new MatchCompetitorBulkResponseHolder(List.of(failed, failed));
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<MatchCompetitorBulkResponseHolder> result =
                ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV);

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
        assertSame(holder, result.getBody());
    }

    @Test
    void testCreateMatchCompetitors_whenSomeRowsFail_thenReturns201() throws Exception {
        // Arrange
        MatchCompetitorBulkResponse failed = new MatchCompetitorBulkResponse(false, "No competitor found",
                new MatchCompetitorResponse());
        MatchCompetitorBulkResponse created = new MatchCompetitorBulkResponse(true, "", new MatchCompetitorResponse());
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV))
                .thenReturn(new MatchCompetitorBulkResponseHolder(List.of(failed, created)));

        // Act
        ResponseEntity<MatchCompetitorBulkResponseHolder> result =
                ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testCreateMatchCompetitors_whenServiceThrowsValidationException_thenPropagates() throws Exception {
        // Arrange
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV))
                .thenThrow(new ValidationException("invalid"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV));
    }

    @Test
    void testCreateMatchCompetitors_whenServiceThrowsNonFatalException_thenPropagates() throws Exception {
        // Arrange
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV))
                .thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV));
    }

    @Test
    void testCreateMatchCompetitors_whenServiceThrowsFatalException_thenPropagates() throws Exception {
        // Arrange
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV))
                .thenThrow(new FatalException("Error reading CSV data"));

        // Act & Assert
        assertThrows(FatalException.class, () -> ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV));
    }

    // updateMatchCompetitor()
    @Test
    void testUpdateMatchCompetitor_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        MatchCompetitorResponse response = new MatchCompetitorResponse();
        when(ipscMatchCompetitorService.updateMatchCompetitor(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<MatchCompetitorResponse> result = ipscMatchCompetitorController.updateMatchCompetitor(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testUpdateMatchCompetitor_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        when(ipscMatchCompetitorService.updateMatchCompetitor(1L, request)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorController.updateMatchCompetitor(1L, request));
    }

    @Test
    void testUpdateMatchCompetitor_whenServiceThrowsValidationException_thenPropagates() {
        // Arrange
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        when(ipscMatchCompetitorService.updateMatchCompetitor(1L, request)).thenThrow(new ValidationException("invalid"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorController.updateMatchCompetitor(1L, request));
    }

    // patchMatchCompetitor()
    @Test
    void testPatchMatchCompetitor_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        MatchCompetitorResponse response = new MatchCompetitorResponse();
        when(ipscMatchCompetitorService.patchMatchCompetitor(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<MatchCompetitorResponse> result = ipscMatchCompetitorController.patchMatchCompetitor(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testPatchMatchCompetitor_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        when(ipscMatchCompetitorService.patchMatchCompetitor(1L, request)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorController.patchMatchCompetitor(1L, request));
    }

    @Test
    void testPatchMatchCompetitor_whenServiceThrowsValidationException_thenPropagates() {
        // Arrange
        MatchCompetitorPatchRequest request = new MatchCompetitorPatchRequest();
        when(ipscMatchCompetitorService.patchMatchCompetitor(1L, request)).thenThrow(new ValidationException("invalid"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorController.patchMatchCompetitor(1L, request));
    }

    // getMatchCompetitor()
    @Test
    void testGetMatchCompetitor_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        MatchCompetitorResponse response = new MatchCompetitorResponse();
        when(ipscMatchCompetitorService.getMatchCompetitor(1L)).thenReturn(response);

        // Act
        ResponseEntity<MatchCompetitorResponse> result = ipscMatchCompetitorController.getMatchCompetitor(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testGetMatchCompetitor_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        when(ipscMatchCompetitorService.getMatchCompetitor(1L)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorController.getMatchCompetitor(1L));
    }

    // getAllMatchCompetitors()
    @Test
    void testGetAllMatchCompetitors_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        List<MatchCompetitorResponse> response = List.of(new MatchCompetitorResponse());
        when(ipscMatchCompetitorService.getAllMatchCompetitors()).thenReturn(response);

        // Act
        ResponseEntity<List<MatchCompetitorResponse>> result = ipscMatchCompetitorController.getAllMatchCompetitors();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testGetAllMatchCompetitors_whenNoneExist_thenReturnsEmptyList() {
        // Arrange
        when(ipscMatchCompetitorService.getAllMatchCompetitors()).thenReturn(List.of());

        // Act
        ResponseEntity<List<MatchCompetitorResponse>> result = ipscMatchCompetitorController.getAllMatchCompetitors();

        // Assert
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
    }

    // deleteMatchCompetitor()
    @Test
    void testDeleteMatchCompetitor_whenServiceSucceeds_thenReturns204WithNoBody() {
        // Act
        ResponseEntity<Void> result = ipscMatchCompetitorController.deleteMatchCompetitor(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(ipscMatchCompetitorService).deleteMatchCompetitor(1L);
    }

    @Test
    void testDeleteMatchCompetitor_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        doThrow(new NonFatalException("not found")).when(ipscMatchCompetitorService).deleteMatchCompetitor(1L);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorController.deleteMatchCompetitor(1L));
    }

    @Test
    void testDeleteMatchCompetitor_whenServiceThrowsValidationException_thenPropagates() {
        // Arrange
        doThrow(new ValidationException("referenced")).when(ipscMatchCompetitorService).deleteMatchCompetitor(1L);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorController.deleteMatchCompetitor(1L));
    }
}
