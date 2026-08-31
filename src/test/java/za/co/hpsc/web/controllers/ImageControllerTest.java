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
import za.co.hpsc.web.models.image.response.ImageResponse;
import za.co.hpsc.web.models.image.response.ImageResponseHolder;
import za.co.hpsc.web.services.ImageService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    private static final String VALID_CSV = """
            title,summary,description,category,tags,filePath,fileName
            Image 1,Summary 1,Description 1,Category 1,Tag1|Tag2,/path/to/image1,image1.png
            """;

    // processCsv()
    @Test
    void testProcessCsv_whenValidCsvData_thenReturns200() throws ValidationException, FatalException {
        // Arrange
        ImageResponseHolder holder = new ImageResponseHolder(List.of());
        when(imageService.createImages(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<ImageResponseHolder> response = imageController.createImages(VALID_CSV);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testProcessCsv_whenValidCsvData_thenResponseBodyIsReturnedFromService() throws ValidationException, FatalException {
        // Arrange
        ImageResponseHolder holder = new ImageResponseHolder(List.of());
        when(imageService.createImages(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<ImageResponseHolder> response = imageController.createImages(VALID_CSV);

        // Assert
        assertNotNull(response.getBody());
        assertSame(holder, response.getBody());
    }

    @Test
    void testProcessCsv_whenValidCsvData_thenDelegatesProcessingToService() throws ValidationException, FatalException {
        // Arrange
        ImageResponseHolder holder = new ImageResponseHolder(List.of());
        when(imageService.createImages(VALID_CSV)).thenReturn(holder);

        // Act
        imageController.createImages(VALID_CSV);

        // Assert
        verify(imageService).createImages(VALID_CSV);
    }

    @Test
    void testProcessCsv_whenServiceReturnsHolderWithImages_thenResponseBodyContainsImages() throws ValidationException, FatalException {
        // Arrange
        ImageResponse image = new ImageResponse();
        ImageResponseHolder holder = new ImageResponseHolder(List.of(image));
        when(imageService.createImages(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<ImageResponseHolder> response = imageController.createImages(VALID_CSV);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getImages().size());
    }

    @Test
    void testProcessCsv_whenServiceReturnsEmptyHolder_thenResponseBodyHasEmptyList() throws ValidationException, FatalException {
        // Arrange
        ImageResponseHolder holder = new ImageResponseHolder(Collections.emptyList());
        when(imageService.createImages(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<ImageResponseHolder> response = imageController.createImages(VALID_CSV);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getImages().isEmpty());
    }

    @Test
    void testProcessCsv_whenServiceThrowsValidationException_thenExceptionPropagates() throws ValidationException, FatalException {
        // Arrange
        when(imageService.createImages(anyString())).thenThrow(new ValidationException("Invalid CSV format"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> imageController.createImages("invalid,csv"));
    }

    @Test
    void testProcessCsv_whenServiceThrowsFatalException_thenExceptionPropagates() throws ValidationException, FatalException {
        // Arrange
        when(imageService.createImages(anyString())).thenThrow(new FatalException("Unexpected processing error"));

        // Act & Assert
        assertThrows(FatalException.class, () -> imageController.createImages(VALID_CSV));
    }

    @Test
    void testProcessCsv_whenCsvDataIsNull_thenDelegatesToService() throws ValidationException, FatalException {
        // Arrange
        ImageResponseHolder holder = new ImageResponseHolder(List.of());
        when(imageService.createImages(null)).thenReturn(holder);

        // Act
        ResponseEntity<ImageResponseHolder> response = imageController.createImages(null);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(imageService).createImages(null);
    }

    @Test
    void testProcessCsv_whenCsvDataIsEmpty_thenDelegatesToService() throws ValidationException, FatalException {
        // Arrange
        when(imageService.createImages("")).thenThrow(new ValidationException("CSV data is empty"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> imageController.createImages(""));
    }

    @Test
    void testProcessCsv_whenServiceInvokedOnce_thenNoAdditionalInteractions() throws ValidationException, FatalException {
        // Arrange
        ImageResponseHolder holder = new ImageResponseHolder(List.of());
        when(imageService.createImages(VALID_CSV)).thenReturn(holder);

        // Act
        imageController.createImages(VALID_CSV);

        // Assert
        verifyNoMoreInteractions(imageService);
    }
}