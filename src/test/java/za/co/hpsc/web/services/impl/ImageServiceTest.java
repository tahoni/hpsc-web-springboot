package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import za.co.hpsc.web.models.ImageResponse;
import za.co.hpsc.web.models.ImageResponseHolder;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class ImageServiceTest {

    @InjectMocks
    private final HpscImageService hpscImageService = new HpscImageService();

    @Test
    void testProcessCsv_withValidData_thenReturnsListOfImages() {
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
    void testProcessCsv_withInvalidData_thenThrowsException() {
        // Arrange
        String invalidCsvData = """
                summary,description,category,tags,filePath,fileName
                Summary 1
                """;

        // Act & Assert
        assertThrows(IOException.class, () ->
                hpscImageService.processCsv(invalidCsvData));
    }

    @Test
    void testProcessCsv_withEmptyData_thenReturnsEmptyList() {
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
    void testProcessCsv_withNullData_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                hpscImageService.processCsv(null));
    }
}