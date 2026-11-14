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
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogPatchRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.request.ShooterLogRequest;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogBulkResponse;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogBulkResponseHolder;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogResponse;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogRow;
import za.co.hpsc.web.services.IpscShooterLogService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscShooterLogControllerTest {

    @Mock
    private IpscShooterLogService ipscShooterLogService;

    @InjectMocks
    private IpscShooterLogController ipscShooterLogController;

    // createShooterLogs()
    @Test
    void testCreateShooterLogs_whenSomeRowsSucceed_thenReturns201WithServiceBody() throws FatalException {
        // Arrange
        ShooterLogBulkResponseHolder holder = new ShooterLogBulkResponseHolder(List.of(
                new ShooterLogBulkResponse(1, true, "", new ShooterLogResponse(), null),
                new ShooterLogBulkResponse(1, false, "No IPSC match found with ID 9", null, new ShooterLogRow())));
        when(ipscShooterLogService.createShooterLogs("csv")).thenReturn(holder);

        // Act
        ResponseEntity<ShooterLogBulkResponseHolder> result = ipscShooterLogController.createShooterLogs("csv");

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertSame(holder, result.getBody());
    }

    @Test
    void testCreateShooterLogs_whenEveryRowFails_thenReturns422WithServiceBody() throws FatalException {
        // Arrange
        ShooterLogBulkResponseHolder holder = new ShooterLogBulkResponseHolder(List.of(
                new ShooterLogBulkResponse(1, false, "Shooter log start date is required.", null, new ShooterLogRow())));
        when(ipscShooterLogService.createShooterLogs("csv")).thenReturn(holder);

        // Act
        ResponseEntity<ShooterLogBulkResponseHolder> result = ipscShooterLogController.createShooterLogs("csv");

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, result.getStatusCode());
        assertSame(holder, result.getBody());
    }

    @Test
    void testCreateShooterLogs_whenThereAreNoRows_thenReturns201() throws FatalException {
        // Arrange
        ShooterLogBulkResponseHolder holder = new ShooterLogBulkResponseHolder(List.of());
        when(ipscShooterLogService.createShooterLogs("csv")).thenReturn(holder);

        // Act
        ResponseEntity<ShooterLogBulkResponseHolder> result = ipscShooterLogController.createShooterLogs("csv");

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    // createShooterLog()
    @Test
    void testCreateShooterLog_whenServiceSucceeds_thenReturns201WithServiceBody() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest();
        ShooterLogResponse response = new ShooterLogResponse();
        when(ipscShooterLogService.createShooterLog(request)).thenReturn(response);

        // Act
        ResponseEntity<ShooterLogResponse> result = ipscShooterLogController.createShooterLog(request);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // deleteShooterLog()
    @Test
    void testDeleteShooterLog_whenServiceSucceeds_thenReturns204() {
        // Act
        ResponseEntity<Void> result = ipscShooterLogController.deleteShooterLog(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(ipscShooterLogService).deleteShooterLog(1L);
    }

    // getAllShooterLogs()
    @Test
    void testGetAllShooterLogs_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        List<ShooterLogResponse> response = List.of(new ShooterLogResponse());
        when(ipscShooterLogService.getAllShooterLogs()).thenReturn(response);

        // Act
        ResponseEntity<List<ShooterLogResponse>> result = ipscShooterLogController.getAllShooterLogs();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // getShooterLog()
    @Test
    void testGetShooterLog_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        ShooterLogResponse response = new ShooterLogResponse();
        when(ipscShooterLogService.getShooterLog(1L)).thenReturn(response);

        // Act
        ResponseEntity<ShooterLogResponse> result = ipscShooterLogController.getShooterLog(1L);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // patchShooterLog()
    @Test
    void testPatchShooterLog_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        ShooterLogResponse response = new ShooterLogResponse();
        when(ipscShooterLogService.patchShooterLog(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<ShooterLogResponse> result = ipscShooterLogController.patchShooterLog(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // updateShooterLog()
    @Test
    void testUpdateShooterLog_whenServiceSucceeds_thenReturns200WithServiceBody() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest();
        ShooterLogResponse response = new ShooterLogResponse();
        when(ipscShooterLogService.updateShooterLog(1L, request)).thenReturn(response);

        // Act
        ResponseEntity<ShooterLogResponse> result = ipscShooterLogController.updateShooterLog(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    // updateShooterLogs()
    @Test
    void testUpdateShooterLogs_whenSomeRowsSucceed_thenReturns200WithServiceBody() throws FatalException {
        // Arrange
        ShooterLogBulkResponseHolder holder = new ShooterLogBulkResponseHolder(List.of(
                new ShooterLogBulkResponse(1, true, "", new ShooterLogResponse(), null),
                new ShooterLogBulkResponse(1, false, "No shooter log found with ID 9", null, new ShooterLogRow())));
        when(ipscShooterLogService.updateShooterLogs("csv")).thenReturn(holder);

        // Act
        ResponseEntity<ShooterLogBulkResponseHolder> result = ipscShooterLogController.updateShooterLogs("csv");

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(holder, result.getBody());
    }

    @Test
    void testUpdateShooterLogs_whenEveryRowFails_thenReturns422WithServiceBody() throws FatalException {
        // Arrange
        ShooterLogBulkResponseHolder holder = new ShooterLogBulkResponseHolder(List.of(
                new ShooterLogBulkResponse(1, false, "Shooter log ID is required.", null, new ShooterLogRow())));
        when(ipscShooterLogService.updateShooterLogs("csv")).thenReturn(holder);

        // Act
        ResponseEntity<ShooterLogBulkResponseHolder> result = ipscShooterLogController.updateShooterLogs("csv");

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, result.getStatusCode());
        assertSame(holder, result.getBody());
    }

    @Test
    void testUpdateShooterLogs_whenThereAreNoRows_thenReturns200() throws FatalException {
        // Arrange
        when(ipscShooterLogService.updateShooterLogs("csv")).thenReturn(new ShooterLogBulkResponseHolder(List.of()));

        // Act
        ResponseEntity<ShooterLogBulkResponseHolder> result = ipscShooterLogController.updateShooterLogs("csv");

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    // patchShooterLogs()
    @Test
    void testPatchShooterLogs_whenSomeRowsSucceed_thenReturns200WithServiceBody() throws FatalException {
        // Arrange
        ShooterLogBulkResponseHolder holder = new ShooterLogBulkResponseHolder(List.of(
                new ShooterLogBulkResponse(1, true, "", new ShooterLogResponse(), null),
                new ShooterLogBulkResponse(1, false, "No shooter log found with ID 9", null, new ShooterLogRow())));
        when(ipscShooterLogService.patchShooterLogs("csv")).thenReturn(holder);

        // Act
        ResponseEntity<ShooterLogBulkResponseHolder> result = ipscShooterLogController.patchShooterLogs("csv");

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(holder, result.getBody());
    }

    @Test
    void testPatchShooterLogs_whenEveryRowFails_thenReturns422WithServiceBody() throws FatalException {
        // Arrange
        ShooterLogBulkResponseHolder holder = new ShooterLogBulkResponseHolder(List.of(
                new ShooterLogBulkResponse(1, false, "Shooter log ID is required.", null, new ShooterLogRow())));
        when(ipscShooterLogService.patchShooterLogs("csv")).thenReturn(holder);

        // Act
        ResponseEntity<ShooterLogBulkResponseHolder> result = ipscShooterLogController.patchShooterLogs("csv");

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, result.getStatusCode());
        assertSame(holder, result.getBody());
    }

    @Test
    void testPatchShooterLogs_whenThereAreNoRows_thenReturns200() throws FatalException {
        // Arrange
        when(ipscShooterLogService.patchShooterLogs("csv")).thenReturn(new ShooterLogBulkResponseHolder(List.of()));

        // Act
        ResponseEntity<ShooterLogBulkResponseHolder> result = ipscShooterLogController.patchShooterLogs("csv");

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
