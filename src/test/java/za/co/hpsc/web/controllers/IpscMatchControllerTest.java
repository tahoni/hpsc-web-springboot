package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.shared.MatchWithStages;
import za.co.hpsc.web.services.IpscMatchService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void insertMatch_delegatesToService_withProvidedMatchWithStages() throws FatalException {
        MatchWithStages matchWithStages = new MatchWithStages();

        ipscMatchController.insertMatch(matchWithStages);

        verify(ipscMatchService).insertMatch(matchWithStages);
    }

    @Test
    void updateMatch_delegatesToService_withPathVariableAndBody() throws FatalException {
        Long matchId = 42L;
        MatchWithStages matchWithStages = new MatchWithStages();

        ipscMatchController.updateMatch(matchId, matchWithStages);

        verify(ipscMatchService).updateMatch(matchId, matchWithStages);
    }

    @Test
    void modifyMatch_delegatesToService_withPathVariableAndBody() throws FatalException {
        Long matchId = 84L;
        MatchWithStages matchWithStages = new MatchWithStages();

        ipscMatchController.modifyMatch(matchId, matchWithStages);

        verify(ipscMatchService).modifyMatch(matchId, matchWithStages);
    }

    @Test
    void getMatch_returnsMatchWithStages_whenServiceFindsMatch() throws FatalException {
        Long matchId = 7L;
        MatchWithStages expected = new MatchWithStages();
        when(ipscMatchService.getMatch(matchId)).thenReturn(Optional.of(expected));

        MatchResponse result = ipscMatchController.getMatch(matchId);

        assertEquals(expected, result);
        verify(ipscMatchService).getMatch(matchId);
    }

    @Test
    void getMatch_returnsNull_whenServiceReturnsEmptyOptional() throws FatalException {
        Long matchId = 8L;
        when(ipscMatchService.getMatch(matchId)).thenReturn(Optional.empty());

        assertThrows(FatalException.class, () -> ipscMatchController.getMatch(matchId));

        verify(ipscMatchService).getMatch(matchId);
    }

    @Test
    void insertMatch_propagatesFatalException_fromService() throws FatalException {
        MatchWithStages matchWithStages = new MatchWithStages();
        doThrow(new FatalException("insert failed")).when(ipscMatchService).insertMatch(matchWithStages);

        assertThrows(FatalException.class, () -> ipscMatchController.insertMatch(matchWithStages));

        verify(ipscMatchService).insertMatch(matchWithStages);
    }
}