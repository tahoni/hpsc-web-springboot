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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpscShooterResultControllerTest {

    @Mock
    private IpscShooterResultService ipscShooterResultService;

    @InjectMocks
    private IpscShooterResultController ipscShooterResultController;

    // createShooterResults()
    @Test
    void testCreateShooterResults_whenServiceSucceeds_thenReturns200WithServiceBody() throws NonFatalException {
        // Arrange
        ShooterResponseHolder response = new ShooterResponseHolder();
        when(ipscShooterResultService.calculateMatchShooterResult(1L)).thenReturn(response);

        // Act
        ResponseEntity<ShooterResponseHolder> result = ipscShooterResultController.createShooterResults(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testCreateShooterResults_whenServiceThrows_thenPropagatesNonFatalException() throws NonFatalException {
        // Arrange
        when(ipscShooterResultService.calculateMatchShooterResult(99L)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterResultController.createShooterResults(99L));
    }

    // getAllShooterResults()
    @Test
    void testGetAllShooterResults_whenNoMatches_thenReturnsEmptyList() {
        // Arrange
        when(ipscShooterResultService.getAllShooterResults()).thenReturn(List.of());

        // Act
        ResponseEntity<List<ShooterResponseHolder>> result = ipscShooterResultController.getAllShooterResults();

        // Assert
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void testGetAllShooterResults_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        List<ShooterResponseHolder> response = List.of(new ShooterResponseHolder());
        when(ipscShooterResultService.getAllShooterResults()).thenReturn(response);

        // Act
        ResponseEntity<List<ShooterResponseHolder>> result = ipscShooterResultController.getAllShooterResults();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // getShooterResults()
    @Test
    void testGetShooterResults_whenServiceSucceeds_thenReturns200WithServiceBody() throws NonFatalException {
        // Arrange
        ShooterResponseHolder response = new ShooterResponseHolder();
        when(ipscShooterResultService.getShooterResults(1L)).thenReturn(response);

        // Act
        ResponseEntity<ShooterResponseHolder> result = ipscShooterResultController.getShooterResults(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testGetShooterResults_whenServiceThrows_thenPropagatesNonFatalException() throws NonFatalException {
        // Arrange
        when(ipscShooterResultService.getShooterResults(99L)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterResultController.getShooterResults(99L));
    }
}
