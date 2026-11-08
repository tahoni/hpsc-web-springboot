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

    // createShooterLogOveralls()
    @Test
    void testCreateShooterLogOveralls_whenServiceSucceeds_thenReturns200WithServiceBody() throws NonFatalException {
        // Arrange
        ShooterLogOverallResponseHolder response = new ShooterLogOverallResponseHolder();
        when(ipscShooterLogOverallService.calculateShooterLogOverall(1L)).thenReturn(response);

        // Act
        ResponseEntity<ShooterLogOverallResponseHolder> result =
                ipscShooterLogOverallController.createShooterLogOveralls(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testCreateShooterLogOveralls_whenServiceThrows_thenPropagatesNonFatalException() throws NonFatalException {
        // Arrange
        when(ipscShooterLogOverallService.calculateShooterLogOverall(99L))
                .thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogOverallController.createShooterLogOveralls(99L));
    }

    // getAllShooterLogOveralls()
    @Test
    void testGetAllShooterLogOveralls_whenNoShooterLogs_thenReturnsEmptyList() {
        // Arrange
        when(ipscShooterLogOverallService.getAllShooterLogOveralls()).thenReturn(List.of());

        // Act
        ResponseEntity<List<ShooterLogOverallResponseHolder>> result =
                ipscShooterLogOverallController.getAllShooterLogOveralls();

        // Assert
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void testGetAllShooterLogOveralls_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        List<ShooterLogOverallResponseHolder> response = List.of(new ShooterLogOverallResponseHolder());
        when(ipscShooterLogOverallService.getAllShooterLogOveralls()).thenReturn(response);

        // Act
        ResponseEntity<List<ShooterLogOverallResponseHolder>> result =
                ipscShooterLogOverallController.getAllShooterLogOveralls();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // getShooterLogOveralls()
    @Test
    void testGetShooterLogOveralls_whenServiceSucceeds_thenReturns200WithServiceBody() throws NonFatalException {
        // Arrange
        ShooterLogOverallResponseHolder response = new ShooterLogOverallResponseHolder();
        when(ipscShooterLogOverallService.getShooterLogOveralls(1L)).thenReturn(response);

        // Act
        ResponseEntity<ShooterLogOverallResponseHolder> result =
                ipscShooterLogOverallController.getShooterLogOveralls(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void testGetShooterLogOveralls_whenServiceThrows_thenPropagatesNonFatalException() throws NonFatalException {
        // Arrange
        when(ipscShooterLogOverallService.getShooterLogOveralls(99L)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogOverallController.getShooterLogOveralls(99L));
    }
}
