package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;
import za.co.hpsc.web.services.IpscMatchService;

import java.util.Optional;

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
    void testGetMatch_whenServiceReturnsEmptyOptional_thenThrowsFatalException() {
        Long matchId = 8L;
        when(ipscMatchService.getMatch(matchId)).thenReturn(Optional.empty());

        assertThrows(FatalException.class, () -> ipscMatchController.getMatch(matchId));

        verify(ipscMatchService).getMatch(matchId);
    }

    @Test
    void testInsertMatch_whenServiceThrowsFatalException_thenExceptionPropagates() throws FatalException {
        MatchOnlyRequest matchOnlyRequest = new MatchOnlyRequest();
        doThrow(new FatalException("insert failed")).when(ipscMatchService).insertMatch(matchOnlyRequest);

        assertThrows(FatalException.class, () -> ipscMatchController.insertMatch(matchOnlyRequest));

        verify(ipscMatchService).insertMatch(matchOnlyRequest);
    }
}