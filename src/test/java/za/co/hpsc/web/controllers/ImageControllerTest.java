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

    // createImages()
    @Test
    void testCreateImages_whenValidCsvData_thenReturns200() throws ValidationException, FatalException {
        // Arrange
        ImageResponseHolder holder = new ImageResponseHolder(List.of());
        when(imageService.createImages(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<ImageResponseHolder> response = imageController.createImages(VALID_CSV);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateImages_whenValidCsvData_thenResponseBodyIsReturnedFromService() throws ValidationException, FatalException {
        // Arrange
        ImageResponseHolder holder = new ImageResponseHolder(List.of());
        when(imageService.createImages(VALID_CSV)).thenReturn(holder);

        // Act
        ResponseEntity<ImageResponseHolder> response = imageController.createImages(VALID_CSV);

        // Assert
        assertNotNull(response.getBody());
        assertSame(holder, response.getBody());
    }

}