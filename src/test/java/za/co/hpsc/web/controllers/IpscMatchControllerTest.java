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
import za.co.hpsc.web.models.ipsc.match.request.MatchPatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponseHolder;
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

    // createMatches()
    private static final String VALID_CSV = """
            MatchDate,MatchName,Club,MatchFirearmType,MatchCategory
            2026-04-10,Club Championship,Test Club,Pistol,Level 1
            """;

    @Test
    void testCreateMatches_whenServiceSucceeds_thenReturns201() throws ValidationException, NonFatalException, FatalException {
        // Arrange
        MatchResponseHolder holder = new MatchResponseHolder(List.of());
        when(ipscMatchService.createMatches(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<MatchResponseHolder> result = ipscMatchController.createMatches(VALID_CSV);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testCreateMatches_whenServiceSucceeds_thenResponseBodyIsReturnedFromService() throws ValidationException, NonFatalException, FatalException {
        // Arrange
        MatchResponseHolder holder = new MatchResponseHolder(List.of(new MatchResponse()));
        when(ipscMatchService.createMatches(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<MatchResponseHolder> result = ipscMatchController.createMatches(VALID_CSV);

        // Assert
        assertSame(holder, result.getBody());
    }

    // deleteMatch()
    @Test
    void testDeleteMatch_whenServiceSucceeds_thenReturns204() throws ValidationException, NonFatalException {
        // Act
        ResponseEntity<Void> result = ipscMatchController.deleteMatch(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void testDeleteMatch_whenServiceSucceeds_thenDelegatesToService() throws ValidationException, NonFatalException {
        // Act
        ipscMatchController.deleteMatch(1L);

        // Assert
        verify(ipscMatchService).deleteMatch(1L);
        verifyNoMoreInteractions(ipscMatchService);
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

    // patchMatch()
    @Test
    void testPatchMatch_whenServiceSucceeds_thenReturns200() throws ValidationException, NonFatalException {
        // Arrange
        MatchPatchRequest request = new MatchPatchRequest();
        MatchResponse response = new MatchResponse();
        when(ipscMatchService.patchMatch(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<MatchResponse> result = ipscMatchController.patchMatch(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
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

}
