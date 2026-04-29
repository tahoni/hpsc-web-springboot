package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.request.MatchSearchRequest;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.services.IpscMatchService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscMatchControllerTest {

    @Mock
    private IpscMatchService ipscMatchService;

    private IpscMatchController ipscMatchController;

    @BeforeEach
    void setUp() {
        ipscMatchController = new IpscMatchController(ipscMatchService);
    }

    @Test
    void insertMatch_delegatesToService_withProvidedMatchResponse() throws FatalException {
        MatchResponse matchResponse = new MatchResponse();

        ipscMatchController.insertMatch(matchResponse);

        verify(ipscMatchService).insertMatch(matchResponse);
    }

    @Test
    void updateMatch_delegatesToService_withPathVariableAndBody() throws FatalException {
        Long matchId = 42L;
        MatchResponse matchResponse = new MatchResponse();

        ipscMatchController.updateMatch(matchId, matchResponse);

        verify(ipscMatchService).updateMatch(matchId, matchResponse);
    }

    @Test
    void modifyMatch_delegatesToService_withPathVariableAndBody() throws FatalException {
        Long matchId = 84L;
        MatchResponse matchResponse = new MatchResponse();

        ipscMatchController.modifyMatch(matchId, matchResponse);

        verify(ipscMatchService).modifyMatch(matchId, matchResponse);
    }

    @Test
    void getMatches_returnsListFromService_forGivenSearchRequest() {
        MatchSearchRequest request = new MatchSearchRequest();
        List<MatchResponse> expected = List.of(new MatchResponse(), new MatchResponse());
        when(ipscMatchService.getMatches(request)).thenReturn(expected);

        List<MatchResponse> result = ipscMatchController.getMatches(request);

        assertEquals(expected, result);
        verify(ipscMatchService).getMatches(request);
    }

    @Test
    void getMatches_returnsEmptyList_whenServiceReturnsEmptyList() {
        MatchSearchRequest request = new MatchSearchRequest();
        when(ipscMatchService.getMatches(request)).thenReturn(List.of());

        List<MatchResponse> result = ipscMatchController.getMatches(request);

        assertTrue(result.isEmpty());
        verify(ipscMatchService).getMatches(request);
    }

    @Test
    void getMatch_returnsMatchResponse_whenServiceFindsMatch() {
        Long matchId = 7L;
        MatchResponse expected = new MatchResponse();
        when(ipscMatchService.getMatch(matchId)).thenReturn(Optional.of(expected));

        MatchResponse result = ipscMatchController.getMatch(matchId);

        assertEquals(expected, result);
        verify(ipscMatchService).getMatch(matchId);
    }

    @Test
    void getMatch_returnsNull_whenServiceReturnsEmptyOptional() {
        Long matchId = 8L;
        when(ipscMatchService.getMatch(matchId)).thenReturn(Optional.empty());

        MatchResponse result = ipscMatchController.getMatch(matchId);

        assertNull(result);
        verify(ipscMatchService).getMatch(matchId);
    }

    @Test
    void insertMatch_propagatesFatalException_fromService() throws FatalException {
        MatchResponse matchResponse = new MatchResponse();
        doThrow(new FatalException("insert failed")).when(ipscMatchService).insertMatch(matchResponse);

        assertThrows(FatalException.class, () -> ipscMatchController.insertMatch(matchResponse));

        verify(ipscMatchService).insertMatch(matchResponse);
    }

    @Test
    void updateMatch_propagatesFatalException_fromService() throws FatalException {
        Long matchId = 9L;
        MatchResponse matchResponse = new MatchResponse();
        doThrow(new FatalException("update failed")).when(ipscMatchService).updateMatch(matchId, matchResponse);

        assertThrows(FatalException.class, () -> ipscMatchController.updateMatch(matchId, matchResponse));

        verify(ipscMatchService).updateMatch(matchId, matchResponse);
    }

    @Test
    void modifyMatch_propagatesFatalException_fromService() throws FatalException {
        Long matchId = 10L;
        MatchResponse matchResponse = new MatchResponse();
        doThrow(new FatalException("modify failed")).when(ipscMatchService).modifyMatch(matchId, matchResponse);

        assertThrows(FatalException.class, () -> ipscMatchController.modifyMatch(matchId, matchResponse));

        verify(ipscMatchService).modifyMatch(matchId, matchResponse);
    }
}