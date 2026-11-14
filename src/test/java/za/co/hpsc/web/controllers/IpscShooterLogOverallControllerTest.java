package za.co.hpsc.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogResponse;
import za.co.hpsc.web.models.ipsc.shooterlogoverall.response.ShooterLogOverallResponse;
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

    // matchIds
    @Test
    void testCreateShooterLogOverallResults_whenOverallsHaveMatchIds_thenReturnsThem() throws NonFatalException {
        // Arrange
        when(ipscShooterLogOverallService.calculateShooterLogOverall(1L))
                .thenReturn(holderWithMatchIds(List.of(10L, 11L)));

        // Act
        ResponseEntity<ShooterLogOverallResponseHolder> result =
                ipscShooterLogOverallController.createShooterLogOverallResults(1L);

        // Assert
        assertNotNull(result.getBody());
        assertEquals(List.of(10L, 11L), result.getBody().getShooterLogOverallResults().getFirst().getMatchIds());
    }

    @Test
    void testGetAllShooterLogOverallResults_whenOverallsHaveMatchIds_thenReturnsThem() {
        // Arrange
        when(ipscShooterLogOverallService.getAllShooterLogOverallResults())
                .thenReturn(List.of(holderWithMatchIds(List.of(10L, 11L))));

        // Act
        ResponseEntity<List<ShooterLogOverallResponseHolder>> result =
                ipscShooterLogOverallController.getAllShooterLogOverallResults();

        // Assert
        assertNotNull(result.getBody());
        assertEquals(List.of(10L, 11L),
                result.getBody().getFirst().getShooterLogOverallResults().getFirst().getMatchIds());
    }

    @Test
    void testGetShooterLogOverallResults_whenOverallsHaveMatchIds_thenReturnsThem() throws NonFatalException {
        // Arrange
        when(ipscShooterLogOverallService.getShooterLogOverallResults(1L))
                .thenReturn(holderWithMatchIds(List.of(10L, 11L)));

        // Act
        ResponseEntity<ShooterLogOverallResponseHolder> result =
                ipscShooterLogOverallController.getShooterLogOverallResults(1L);

        // Assert
        assertNotNull(result.getBody());
        assertEquals(List.of(10L, 11L), result.getBody().getShooterLogOverallResults().getFirst().getMatchIds());
    }

    @Test
    void testGetShooterLogOverallResults_whenOverallsHaveNoMatchIds_thenReturnsAnEmptyList()
            throws NonFatalException {
        // Arrange
        when(ipscShooterLogOverallService.getShooterLogOverallResults(1L))
                .thenReturn(holderWithMatchIds(List.of()));

        // Act
        ResponseEntity<ShooterLogOverallResponseHolder> result =
                ipscShooterLogOverallController.getShooterLogOverallResults(1L);

        // Assert
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getShooterLogOverallResults().getFirst().getMatchIds().isEmpty());
    }

    @Test
    void testGetShooterLogOverallResults_whenResponseIsSerialised_thenMatchIdsIsAJsonArrayOfIds()
            throws NonFatalException, JsonProcessingException {
        // Arrange
        when(ipscShooterLogOverallService.getShooterLogOverallResults(1L))
                .thenReturn(holderWithMatchIds(List.of(10L, 11L)));
        ResponseEntity<ShooterLogOverallResponseHolder> result =
                ipscShooterLogOverallController.getShooterLogOverallResults(1L);

        // Act
        JsonNode json = new ObjectMapper().findAndRegisterModules().valueToTree(result.getBody());

        // Assert
        JsonNode matchIds = json.get("shooterLogOverallResults").get(0).get("matchIds");
        assertTrue(matchIds.isArray());
        assertEquals(2, matchIds.size());
        assertEquals(10L, matchIds.get(0).asLong());
        assertEquals(11L, matchIds.get(1).asLong());
    }

    // Helpers
    private ShooterLogOverallResponseHolder holderWithMatchIds(List<Long> matchIds) {
        ShooterLogOverallResponse overall = new ShooterLogOverallResponse();
        overall.setShooterLogOverallId(50L);
        overall.setMatchIds(matchIds);
        return new ShooterLogOverallResponseHolder(new ShooterLogResponse(), List.of(overall));
    }
}
