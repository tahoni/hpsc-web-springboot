package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.matchresult.response.MatchResultResponseHolder;
import za.co.hpsc.web.services.IpscMatchResultService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpscMatchResultControllerTest {

    @Mock
    private IpscMatchResultService ipscMatchResultService;

    @InjectMocks
    private IpscMatchResultController ipscMatchResultController;

    // createMatchResults()
    @Test
    void testCreateMatchResults_whenServiceSucceeds_thenReturns200WithServiceBody() throws NonFatalException {
        // Arrange
        MatchResultResponseHolder response = new MatchResultResponseHolder();
        when(ipscMatchResultService.calculateMatchResults(1L)).thenReturn(response);

        // Act
        ResponseEntity<MatchResultResponseHolder> result = ipscMatchResultController.createMatchResults(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // getAllMatchResults()
    @Test
    void testGetAllMatchResults_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        List<MatchResultResponseHolder> response = List.of(new MatchResultResponseHolder());
        when(ipscMatchResultService.getAllMatchResults()).thenReturn(response);

        // Act
        ResponseEntity<List<MatchResultResponseHolder>> result = ipscMatchResultController.getAllMatchResults();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // getMatchResults()
    @Test
    void testGetMatchResults_whenServiceSucceeds_thenReturns200WithServiceBody() throws NonFatalException {
        // Arrange
        MatchResultResponseHolder response = new MatchResultResponseHolder();
        when(ipscMatchResultService.getMatchResults(1L)).thenReturn(response);

        // Act
        ResponseEntity<MatchResultResponseHolder> result = ipscMatchResultController.getMatchResults(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

}
