package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponseHolder;
import za.co.hpsc.web.services.IpscShooterLogOverallService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpscShooterLogOverallControllerTest {

    @Mock
    private IpscShooterLogOverallService ipscShooterLogOverallService;

    @InjectMocks
    private IpscShooterLogOverallController ipscShooterLogOverallController;

    // createShooterLogOverallResults()
    @Test
    void testCreateShooterLogOverallResults_whenServiceSucceeds_thenReturns200WithServiceBody()
            throws NonFatalException {
        // Arrange
        ShooterLogOverallResponseHolder response = new ShooterLogOverallResponseHolder();
        when(ipscShooterLogOverallService.calculateShooterLogOverall(1L)).thenReturn(response);

        // Act
        ResponseEntity<ShooterLogOverallResponseHolder> result =
                ipscShooterLogOverallController.createShooterLogOverallResults(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testCreateShooterLogOverallResults_whenServiceThrows_thenPropagatesNonFatalException()
            throws NonFatalException {
        // Arrange
        when(ipscShooterLogOverallService.calculateShooterLogOverall(99L))
                .thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscShooterLogOverallController.createShooterLogOverallResults(99L));
    }

    // getAllShooterLogOverallResults()
    @Test
    void testGetAllShooterLogOverallResults_whenNoShooterLogs_thenReturnsEmptyList() {
        // Arrange
        when(ipscShooterLogOverallService.getAllShooterLogOverallResults()).thenReturn(List.of());

        // Act
        ResponseEntity<List<ShooterLogOverallResponseHolder>> result =
                ipscShooterLogOverallController.getAllShooterLogOverallResults();

        // Assert
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void testGetAllShooterLogOverallResults_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        List<ShooterLogOverallResponseHolder> response = List.of(new ShooterLogOverallResponseHolder());
        when(ipscShooterLogOverallService.getAllShooterLogOverallResults()).thenReturn(response);

        // Act
        ResponseEntity<List<ShooterLogOverallResponseHolder>> result =
                ipscShooterLogOverallController.getAllShooterLogOverallResults();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // getShooterLogOverallResults()
    @Test
    void testGetShooterLogOverallResults_whenServiceSucceeds_thenReturns200WithServiceBody() throws NonFatalException {
        // Arrange
        ShooterLogOverallResponseHolder response = new ShooterLogOverallResponseHolder();
        when(ipscShooterLogOverallService.getShooterLogOverallResults(1L)).thenReturn(response);

        // Act
        ResponseEntity<ShooterLogOverallResponseHolder> result =
                ipscShooterLogOverallController.getShooterLogOverallResults(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testGetShooterLogOverallResults_whenServiceThrows_thenPropagatesNonFatalException() throws NonFatalException {
        // Arrange
        when(ipscShooterLogOverallService.getShooterLogOverallResults(99L))
                .thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogOverallController.getShooterLogOverallResults(99L));
    }
}
