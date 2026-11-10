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
                new ShooterLogBulkResponse(true, "", new ShooterLogResponse(), null),
                new ShooterLogBulkResponse(false, "No IPSC match found with ID 9", null, new ShooterLogRow())));
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
                new ShooterLogBulkResponse(false, "Shooter log start date is required.", null, new ShooterLogRow())));
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

    @Test
    void testCreateShooterLogs_whenServiceThrowsValidationException_thenPropagates() throws FatalException {
        // Arrange
        when(ipscShooterLogService.createShooterLogs("bad")).thenThrow(new ValidationException("bad csv"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogController.createShooterLogs("bad"));
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

    @Test
    void testCreateShooterLog_whenServiceThrowsValidationException_thenPropagates() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest();
        when(ipscShooterLogService.createShooterLog(request)).thenThrow(new ValidationException("invalid"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogController.createShooterLog(request));
    }

    @Test
    void testCreateShooterLog_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest();
        when(ipscShooterLogService.createShooterLog(request)).thenThrow(new NonFatalException("no match"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogController.createShooterLog(request));
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

    @Test
    void testDeleteShooterLog_whenServiceThrowsValidationException_thenPropagates() {
        // Arrange
        doThrow(new ValidationException("has overall results")).when(ipscShooterLogService).deleteShooterLog(1L);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogController.deleteShooterLog(1L));
    }

    @Test
    void testDeleteShooterLog_whenServiceThrowsNonFatalException_thenPropagates() {
        // Arrange
        doThrow(new NonFatalException("not found")).when(ipscShooterLogService).deleteShooterLog(99L);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogController.deleteShooterLog(99L));
    }

    // getAllShooterLogs()
    @Test
    void testGetAllShooterLogs_whenNoShooterLogs_thenReturnsEmptyList() {
        // Arrange
        when(ipscShooterLogService.getAllShooterLogs()).thenReturn(List.of());

        // Act
        ResponseEntity<List<ShooterLogResponse>> result = ipscShooterLogController.getAllShooterLogs();

        // Assert
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
    }

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

    @Test
    void testGetShooterLog_whenServiceThrows_thenPropagatesNonFatalException() {
        // Arrange
        when(ipscShooterLogService.getShooterLog(99L)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogController.getShooterLog(99L));
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

    @Test
    void testPatchShooterLog_whenServiceThrows_thenPropagatesNonFatalException() {
        // Arrange
        ShooterLogPatchRequest request = new ShooterLogPatchRequest();
        when(ipscShooterLogService.patchShooterLog(99L, request)).thenThrow(new NonFatalException("not found"));

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscShooterLogController.patchShooterLog(99L, request));
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

    @Test
    void testUpdateShooterLog_whenServiceThrows_thenPropagatesValidationException() {
        // Arrange
        ShooterLogRequest request = new ShooterLogRequest();
        when(ipscShooterLogService.updateShooterLog(1L, request)).thenThrow(new ValidationException("invalid"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogController.updateShooterLog(1L, request));
    }

    // updateShooterLogs()
    @Test
    void testUpdateShooterLogs_whenSomeRowsSucceed_thenReturns200WithServiceBody() throws FatalException {
        // Arrange
        ShooterLogBulkResponseHolder holder = new ShooterLogBulkResponseHolder(List.of(
                new ShooterLogBulkResponse(true, "", new ShooterLogResponse(), null),
                new ShooterLogBulkResponse(false, "No shooter log found with ID 9", null, new ShooterLogRow())));
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
                new ShooterLogBulkResponse(false, "Shooter log ID is required.", null, new ShooterLogRow())));
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

    @Test
    void testUpdateShooterLogs_whenServiceThrowsValidationException_thenPropagates() throws FatalException {
        // Arrange
        when(ipscShooterLogService.updateShooterLogs("csv")).thenThrow(new ValidationException("bad"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscShooterLogController.updateShooterLogs("csv"));
    }

    // patchShooterLogs()
    @Test
    void testPatchShooterLogs_whenSomeRowsSucceed_thenReturns200WithServiceBody() throws FatalException {
        // Arrange
        ShooterLogBulkResponseHolder holder = new ShooterLogBulkResponseHolder(List.of(
                new ShooterLogBulkResponse(true, "", new ShooterLogResponse(), null),
                new ShooterLogBulkResponse(false, "No shooter log found with ID 9", null, new ShooterLogRow())));
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
                new ShooterLogBulkResponse(false, "Shooter log ID is required.", null, new ShooterLogRow())));
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
