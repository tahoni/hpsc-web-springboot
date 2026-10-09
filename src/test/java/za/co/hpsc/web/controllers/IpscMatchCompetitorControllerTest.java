package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // createMatchCompetitors()
    private static final String VALID_CSV = """
            CompetitorId,MatchId,Cats,FirearmType,Div
            1,2,Junior,Handgun,Open Division
            """;

    @Test
    void testCreateMatchCompetitors_whenServiceSucceeds_thenReturns201() throws Exception {
        // Arrange
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV, null))
                .thenReturn(new MatchCompetitorBulkResponseHolder(List.of()));

        // Act
        ResponseEntity<MatchCompetitorBulkResponseHolder> result =
                ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV, null);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testCreateMatchCompetitors_whenServiceSucceeds_thenResponseBodyIsReturnedFromService() throws Exception {
        // Arrange
        MatchCompetitorBulkResponseHolder holder =
                new MatchCompetitorBulkResponseHolder(List.of(new MatchCompetitorBulkResponse()));
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV, null)).thenReturn(holder);

        // Act
        ResponseEntity<MatchCompetitorBulkResponseHolder> result =
                ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV, null);

        // Assert
        assertSame(holder, result.getBody());
    }

    @Test
    void testCreateMatchCompetitors_whenClubIsGiven_thenPassesItToService() throws Exception {
        // Arrange
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV, "HPSC"))
                .thenReturn(new MatchCompetitorBulkResponseHolder(List.of()));

        // Act
        ResponseEntity<MatchCompetitorBulkResponseHolder> result =
                ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV, "HPSC");

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(ipscMatchCompetitorService).createMatchCompetitors(VALID_CSV, "HPSC");
    }

    @Test
    void testCreateMatchCompetitors_whenEveryRowFails_thenReturns422WithTheResults() throws Exception {
        // Arrange
        MatchCompetitorBulkResponse failed = new MatchCompetitorBulkResponse(false, "No competitor found",
                null, null);
        MatchCompetitorBulkResponseHolder holder = new MatchCompetitorBulkResponseHolder(List.of(failed, failed));
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV, null)).thenReturn(holder);

        // Act
        ResponseEntity<MatchCompetitorBulkResponseHolder> result =
                ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV, null);

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, result.getStatusCode());
        assertSame(holder, result.getBody());
    }

    @Test
    void testCreateMatchCompetitors_whenSomeRowsFail_thenReturns201() throws Exception {
        // Arrange
        MatchCompetitorBulkResponse failed = new MatchCompetitorBulkResponse(false, "No competitor found",
                null, null);
        MatchCompetitorBulkResponse created = new MatchCompetitorBulkResponse(true, "",
                null, null);
        when(ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV, null))
                .thenReturn(new MatchCompetitorBulkResponseHolder(List.of(failed, created)));

        // Act
        ResponseEntity<MatchCompetitorBulkResponseHolder> result =
                ipscMatchCompetitorController.createMatchCompetitors(VALID_CSV, null);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
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

}
