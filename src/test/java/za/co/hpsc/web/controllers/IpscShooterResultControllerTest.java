package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResponseHolder;
import za.co.hpsc.web.services.IpscShooterResultService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscShooterResultControllerTest {

    @Mock
    private IpscShooterResultService ipscShooterResultService;

    @InjectMocks
    private IpscShooterResultController ipscShooterResultController;

    // getShooterResults()
    @Test
    void testGetShooterResults_whenServiceSucceeds_thenReturns200() throws NonFatalException {
        // Arrange
        when(ipscShooterResultService.getShooterResults(1L)).thenReturn(new ShooterResponseHolder());

        // Act
        ResponseEntity<ShooterResponseHolder> result = ipscShooterResultController.getShooterResults(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testGetShooterResults_whenServiceSucceeds_thenResponseBodyIsReturnedFromService() throws NonFatalException {
        // Arrange
        ShooterResponseHolder results = new ShooterResponseHolder();
        when(ipscShooterResultService.getShooterResults(1L)).thenReturn(results);

        // Act
        ResponseEntity<ShooterResponseHolder> result = ipscShooterResultController.getShooterResults(1L);

        // Assert
        assertSame(results, result.getBody());
    }

    @Test
    void testGetShooterResults_whenMatchNotFound_thenPropagatesNonFatalException() throws NonFatalException {
        // Arrange
        when(ipscShooterResultService.getShooterResults(99L)).thenThrow(new NonFatalException("No match"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterResultController.getShooterResults(99L));
    }

    // getAllShooterResults()
    @Test
    void testGetAllShooterResults_whenServiceSucceeds_thenReturns200() {
        // Arrange
        when(ipscShooterResultService.getAllShooterResults()).thenReturn(List.of());

        // Act
        ResponseEntity<List<ShooterResponseHolder>> result = ipscShooterResultController.getAllShooterResults();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testGetAllShooterResults_whenServiceSucceeds_thenResponseBodyIsReturnedFromService() {
        // Arrange
        List<ShooterResponseHolder> results = List.of(new ShooterResponseHolder());
        when(ipscShooterResultService.getAllShooterResults()).thenReturn(results);

        // Act
        ResponseEntity<List<ShooterResponseHolder>> result = ipscShooterResultController.getAllShooterResults();

        // Assert
        assertSame(results, result.getBody());
    }
}
