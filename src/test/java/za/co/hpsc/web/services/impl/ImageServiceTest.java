package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ImageResponse;
import za.co.hpsc.web.models.ImageResponseHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImageServiceTest {

    @InjectMocks
    private final HpscImageService hpscImageService = new HpscImageService();

    @Test
    void testProcessCsv_withValidCsvData_thenReturnsListOfImages() {
        // Arrange
        String csvData = """
                title,summary,description,category,tags,filePath,fileName
                Image 1,Summary 1,Description 1,Category 1,Tag1|Tag2,/path/to/image1,image1.png
                Image 2,Summary 2,Description 2,Category 2,Tag3,/path/to/image2,image2.png
                """;

        // Act
        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                hpscImageService.processCsv(csvData));

        // Assert
        List<ImageResponse> responses = responseHolder.getImages();
        assertEquals(2, responses.size());
        assertEquals("Image 1", responses.get(0).getTitle());
        assertEquals("Summary 2", responses.get(1).getSummary());
    }

    @Test
    void testProcessCsv_withInvalidCsvData_thenThrowsException() {
        // Arrange
        String invalidCsvData = """
                summary,description,category,tags,filePath,fileName
                Summary 1
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                hpscImageService.processCsv(invalidCsvData));
    }

    @Test
    void testProcessCsv_withEmptyCsvData_thenReturnsEmptyList() {
        // Arrange
        String emptyCsvData = "title,summary,description,category,tags,filePath,fileName\n";

        // Act
        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                hpscImageService.processCsv(emptyCsvData));

        // Assert
        List<ImageResponse> responses = responseHolder.getImages();
        assertTrue(responses.isEmpty());
    }

    @Test
    void testProcessCsv_withNullCsvData_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                hpscImageService.processCsv(null));
    }

    @Test
    void testProcessCsv_withInvalidCsvFormat_thenThrowsException() {
        // Arrange
        String csvData = "Invalid CSV Format";

        // Act & Assert
        assertThrows(ValidationException.class, () -> hpscImageService.processCsv(csvData));
    }
}