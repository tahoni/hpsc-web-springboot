package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.image.ImageResponse;
import za.co.hpsc.web.models.image.ImageResponseHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private HpscImageService hpscImageService;

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

        // Assert the first image
        ImageResponse imageResponse1 = responses.getFirst();
        assertEquals("Image 1", imageResponse1.getTitle());
        assertEquals("Summary 1", imageResponse1.getSummary());
        assertEquals("Description 1", imageResponse1.getDescription());
        assertEquals("Category 1", imageResponse1.getCategory());
        assertEquals(List.of("Tag1", "Tag2"), imageResponse1.getTags());
        assertEquals("/path/to/image1", imageResponse1.getFilePath());
        assertEquals("image1.png", imageResponse1.getFileName());

        // Assert the second image
        ImageResponse imageResponse2 = responses.get(1);
        assertEquals("Image 2", imageResponse2.getTitle());
        assertEquals("Summary 2", imageResponse2.getSummary());
        assertEquals("Description 2", imageResponse2.getDescription());
        assertEquals("Category 2", imageResponse2.getCategory());
        assertEquals(List.of("Tag3"), imageResponse2.getTags());
        assertEquals("/path/to/image2", imageResponse2.getFilePath());
        assertEquals("image2.png", imageResponse2.getFileName());
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