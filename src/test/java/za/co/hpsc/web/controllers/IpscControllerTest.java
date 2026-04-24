package za.co.hpsc.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.holders.records.IpscMatchRecordHolder;
import za.co.hpsc.web.services.IpscService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IpscControllerTest {

    @Mock
    private IpscService ipscService;

    @InjectMocks
    private IpscController ipscController;

    private static final String VALID_CAB_CONTENT = "{\"matches\": []}";

    // =====================================================================
    // importWinMssCabData
    // =====================================================================

    @Test
    void testImportWinMssCabData_whenValidContent_thenReturns200() throws ValidationException, FatalException {
        // Arrange
        IpscMatchRecordHolder holder = new IpscMatchRecordHolder(List.of());
        when(ipscService.importWinMssCabFile(VALID_CAB_CONTENT)).thenReturn(List.of(holder));

        // Act
        ResponseEntity<List<IpscMatchRecordHolder>> response = ipscController.importWinMssCabData(VALID_CAB_CONTENT);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testImportWinMssCabData_whenValidContent_thenResponseBodyIsReturnedFromService() throws ValidationException, FatalException {
        // Arrange
        IpscMatchRecordHolder holder = new IpscMatchRecordHolder(List.of());
        List<IpscMatchRecordHolder> serviceResult = List.of(holder);
        when(ipscService.importWinMssCabFile(VALID_CAB_CONTENT)).thenReturn(serviceResult);

        // Act
        ResponseEntity<List<IpscMatchRecordHolder>> response = ipscController.importWinMssCabData(VALID_CAB_CONTENT);

        // Assert
        assertNotNull(response.getBody());
        assertSame(serviceResult, response.getBody());
    }

    @Test
    void testImportWinMssCabData_whenValidContent_thenDelegatesProcessingToService() throws ValidationException, FatalException {
        // Arrange
        when(ipscService.importWinMssCabFile(VALID_CAB_CONTENT)).thenReturn(List.of());

        // Act
        ipscController.importWinMssCabData(VALID_CAB_CONTENT);

        // Assert
        verify(ipscService, times(1)).importWinMssCabFile(VALID_CAB_CONTENT);
    }

    @Test
    void testImportWinMssCabData_whenServiceReturnsMultipleHolders_thenResponseBodyContainsAll() throws ValidationException, FatalException {
        // Arrange
        IpscMatchRecordHolder first = new IpscMatchRecordHolder(List.of());
        IpscMatchRecordHolder second = new IpscMatchRecordHolder(List.of());
        when(ipscService.importWinMssCabFile(VALID_CAB_CONTENT)).thenReturn(List.of(first, second));

        // Act
        ResponseEntity<List<IpscMatchRecordHolder>> response = ipscController.importWinMssCabData(VALID_CAB_CONTENT);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testImportWinMssCabData_whenServiceReturnsEmptyList_thenResponseBodyIsEmpty() throws ValidationException, FatalException {
        // Arrange
        when(ipscService.importWinMssCabFile(VALID_CAB_CONTENT)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<IpscMatchRecordHolder>> response = ipscController.importWinMssCabData(VALID_CAB_CONTENT);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testImportWinMssCabData_whenServiceThrowsValidationException_thenExceptionPropagates() throws ValidationException, FatalException {
        // Arrange
        when(ipscService.importWinMssCabFile(anyString())).thenThrow(new ValidationException("Invalid CAB file content"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscController.importWinMssCabData("invalid content"));
    }

    @Test
    void testImportWinMssCabData_whenServiceThrowsFatalException_thenExceptionPropagates() throws ValidationException, FatalException {
        // Arrange
        when(ipscService.importWinMssCabFile(anyString())).thenThrow(new FatalException("Critical processing failure"));

        // Act & Assert
        assertThrows(FatalException.class, () -> ipscController.importWinMssCabData(VALID_CAB_CONTENT));
    }

    @Test
    void testImportWinMssCabData_whenContentIsNull_thenDelegatesToService() throws ValidationException, FatalException {
        // Arrange
        when(ipscService.importWinMssCabFile(null)).thenReturn(List.of());

        // Act
        ResponseEntity<List<IpscMatchRecordHolder>> response = ipscController.importWinMssCabData(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ipscService, times(1)).importWinMssCabFile(null);
    }

    @Test
    void testImportWinMssCabData_whenContentIsEmpty_thenDelegatesToService() throws ValidationException, FatalException {
        // Arrange
        when(ipscService.importWinMssCabFile("")).thenThrow(new ValidationException("Content must not be empty"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscController.importWinMssCabData(""));
    }

    @Test
    void testImportWinMssCabData_whenServiceInvokedOnce_thenNoAdditionalInteractions() throws ValidationException, FatalException {
        // Arrange
        when(ipscService.importWinMssCabFile(VALID_CAB_CONTENT)).thenReturn(List.of());

        // Act
        ipscController.importWinMssCabData(VALID_CAB_CONTENT);

        // Assert
        verifyNoMoreInteractions(ipscService);
    }
}
