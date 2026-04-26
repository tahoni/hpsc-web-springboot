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
import za.co.hpsc.web.models.award.response.AwardCeremonyResponse;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponseHolder;
import za.co.hpsc.web.services.AwardService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AwardControllerTest {

    @Mock
    private AwardService awardService;

    @InjectMocks
    private AwardController awardController;

    private static final String VALID_CSV = """
            title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
            Award 1,Summary 1,Description 1,Category 1,tag1|tag2,2023-10-10,/path/to/image,Ceremony 1,Ceremony Summary,Ceremony Description,Ceremony Category,tags1,John Doe,Jane Smith,Bob Jones,w1.png,w2.png,w3.png
            """;

    // =====================================================================
    // processCsv
    // =====================================================================

    @Test
    void testProcessCsv_whenValidCsvData_thenReturns200() throws ValidationException, FatalException {
        // Arrange
        AwardCeremonyResponseHolder holder = new AwardCeremonyResponseHolder(List.of());
        when(awardService.processCsv(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<AwardCeremonyResponseHolder> response = awardController.processCsv(VALID_CSV);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testProcessCsv_whenValidCsvData_thenResponseBodyIsReturnedFromService() throws ValidationException, FatalException {
        // Arrange
        AwardCeremonyResponseHolder holder = new AwardCeremonyResponseHolder(List.of());
        when(awardService.processCsv(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<AwardCeremonyResponseHolder> response = awardController.processCsv(VALID_CSV);

        // Assert
        assertNotNull(response.getBody());
        assertSame(holder, response.getBody());
    }

    @Test
    void testProcessCsv_whenValidCsvData_thenDelegatesProcessingToService() throws ValidationException, FatalException {
        // Arrange
        AwardCeremonyResponseHolder holder = new AwardCeremonyResponseHolder(List.of());
        when(awardService.processCsv(VALID_CSV)).thenReturn(holder);

        // Act
        awardController.processCsv(VALID_CSV);

        // Assert
        verify(awardService, times(1)).processCsv(VALID_CSV);
    }

    @Test
    void testProcessCsv_whenServiceReturnsHolderWithCeremonies_thenResponseBodyContainsCeremonies() throws ValidationException, FatalException {
        // Arrange
        AwardCeremonyResponse ceremony = new AwardCeremonyResponse();
        AwardCeremonyResponseHolder holder = new AwardCeremonyResponseHolder(List.of(ceremony));
        when(awardService.processCsv(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<AwardCeremonyResponseHolder> response = awardController.processCsv(VALID_CSV);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getAwardCeremonies().size());
    }

    @Test
    void testProcessCsv_whenServiceReturnsEmptyHolder_thenResponseBodyHasEmptyList() throws ValidationException, FatalException {
        // Arrange
        AwardCeremonyResponseHolder holder = new AwardCeremonyResponseHolder(Collections.emptyList());
        when(awardService.processCsv(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<AwardCeremonyResponseHolder> response = awardController.processCsv(VALID_CSV);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getAwardCeremonies().isEmpty());
    }

    @Test
    void testProcessCsv_whenServiceThrowsValidationException_thenExceptionPropagates() throws ValidationException, FatalException {
        // Arrange
        when(awardService.processCsv(anyString())).thenThrow(new ValidationException("Invalid CSV format"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> awardController.processCsv("invalid,csv"));
    }

    @Test
    void testProcessCsv_whenServiceThrowsFatalException_thenExceptionPropagates() throws ValidationException, FatalException {
        // Arrange
        when(awardService.processCsv(anyString())).thenThrow(new FatalException("Unexpected processing error"));

        // Act & Assert
        assertThrows(FatalException.class, () -> awardController.processCsv(VALID_CSV));
    }

    @Test
    void testProcessCsv_whenCsvDataIsNull_thenDelegatesToService() throws ValidationException, FatalException {
        // Arrange
        AwardCeremonyResponseHolder holder = new AwardCeremonyResponseHolder(List.of());
        when(awardService.processCsv(null)).thenReturn(holder);

        // Act
        ResponseEntity<AwardCeremonyResponseHolder> response = awardController.processCsv(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(awardService, times(1)).processCsv(null);
    }

    @Test
    void testProcessCsv_whenCsvDataIsEmpty_thenDelegatesToService() throws ValidationException, FatalException {
        // Arrange
        when(awardService.processCsv("")).thenThrow(new ValidationException("CSV data is empty"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> awardController.processCsv(""));
    }

    @Test
    void testProcessCsv_whenServiceInvokedOnce_thenNoAdditionalInteractions() throws ValidationException, FatalException {
        // Arrange
        AwardCeremonyResponseHolder holder = new AwardCeremonyResponseHolder(List.of());
        when(awardService.processCsv(VALID_CSV)).thenReturn(holder);

        // Act
        awardController.processCsv(VALID_CSV);

        // Assert
        verifyNoMoreInteractions(awardService);
    }
}
