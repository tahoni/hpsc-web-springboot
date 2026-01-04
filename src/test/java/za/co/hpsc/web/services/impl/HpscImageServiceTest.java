package za.co.hpsc.web.services.impl;

import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import za.co.hpsc.web.models.ImageRequest;
import za.co.hpsc.web.models.ImageResponse;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class HpscImageServiceTest {

    @InjectMocks
    private final HpscImageService hpscImageService = new HpscImageService();

    @Test
    void testReadImages_withValidCsv_thenCreatesImageRequestList() {
        // Arrange
        String csvData = """
                title,summary,description,category,tags,filePath,fileName
                Image 1,Summary 1,Description 1,Category 1,Tag1|Tag2,/path/to/image1,image1.png
                Image 2,Summary 2,Description 2,Category 2,Tag3|Tag4,/path/to/image2,image2.png
                """;

        // Act
        List<ImageRequest> imageRequests = assertDoesNotThrow(() ->
                hpscImageService.readImages(csvData));

        // Assert
        assertNotNull(imageRequests);
        assertThat(imageRequests).hasSize(2);

        ImageRequest firstRequest = imageRequests.getFirst();
        assertEquals("Image 1", firstRequest.getTitle());
        assertEquals("Summary 1", firstRequest.getSummary());
        assertEquals("Description 1", firstRequest.getDescription());
        assertEquals("Category 1", firstRequest.getCategory());
        assertEquals("/path/to/image1", firstRequest.getFilePath());
        assertEquals("image1.png", firstRequest.getFileName());
        List<String> tags = firstRequest.getTags();
        assertEquals(2, tags.size());
        assertTrue(tags.contains("Tag1"));

        ImageRequest secondRequest = imageRequests.get(1);
        assertEquals("Image 2", secondRequest.getTitle());
    }

    @Test
    void testReadImages_withValidCsvRearrangedColumns_thenCreatesImageRequestList() {
        // Arrange
        String csvData = """
                summary,title,description,category,tags,filePath,fileName
                Summary 1,Image 1,Description 1,Category 1,Tag1|Tag2,/path/to/image1,image1.png
                Summary 2,Image 2,Description 2,Category 2,Tag3|Tag4,/path/to/image2,image2.png
                """;

        // Act
        List<ImageRequest> imageRequests = assertDoesNotThrow(() ->
                hpscImageService.readImages(csvData));

        // Assert
        assertNotNull(imageRequests);
        assertThat(imageRequests).hasSize(2);

        ImageRequest firstRequest = imageRequests.getFirst();
        assertEquals("Image 1", firstRequest.getTitle());
        assertEquals("Summary 1", firstRequest.getSummary());
        assertEquals("Description 1", firstRequest.getDescription());
        assertEquals("Category 1", firstRequest.getCategory());
        assertEquals("/path/to/image1", firstRequest.getFilePath());
        assertEquals("image1.png", firstRequest.getFileName());
        List<String> tags = firstRequest.getTags();
        assertEquals(2, tags.size());
        assertTrue(tags.contains("Tag1"));

        ImageRequest secondRequest = imageRequests.get(1);
        assertEquals("Image 2", secondRequest.getTitle());
    }

    @Test
    void testReadImages_withEmptyCsv_thenReturnsEmptyList() {
        // Arrange
        String emptyCsvData = "title,summary,description,category,tags,filePath,fileName\n";

        // Act
        List<ImageRequest> imageRequests = assertDoesNotThrow(() ->
                hpscImageService.readImages(emptyCsvData));

        // Assert
        assertNotNull(imageRequests);
        assertTrue(imageRequests.isEmpty());
    }

    @Test
    void testReadImages_withMissingCsvColumns_thenThrowsException() {
        // Arrange
        String csvData = """
                title,filePath,fileName
                Image 1,/path/to/image1,image1.png
                Image 2,/path/to/image2,image2.png
                """;

        // Act & Assert
        assertThrows(CsvReadException.class, () ->
                hpscImageService.readImages(csvData));
    }

    @Test
    void testReadImages_withInvalidCsv_thenThrowsException() {
        // Arrange
        String invalidCsvData = """
                summary,description,category,tags,filePath,fileName
                Invalid Row Without Correct Columns
                """;

        // Act & Assert
        assertThrows(IOException.class, () ->
                hpscImageService.readImages(invalidCsvData));
    }

    @Test
    void testReadImages_withNullCsv_thenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                hpscImageService.readImages(null));
    }

    @Test
    void testMapImages_withValidImageRequestList_thenCreatesImageResponseList() {
        // Arrange
        ImageRequest request1 = new ImageRequest("Image 1", "Summary 1", "Description 1",
                "Category 1", List.of("Tag1", "|Tag2"), "/path/to/image1", "image1.png");
        ImageRequest request2 = new ImageRequest("Image 2", "Summary 2", "Description 2",
                "Category 2", List.of("Tag3", "Tag4"), "/path/to/image2", "image2.png");
        List<ImageRequest> imageRequestList = List.of(request1, request2);

        // Act
        List<ImageResponse> imageResponseList =
                hpscImageService.mapImages(imageRequestList);

        // Assert
        assertNotNull(imageResponseList);
        assertThat(imageResponseList).hasSize(2);

        ImageResponse firstResponse = imageResponseList.getFirst();
        assertEquals("Image 1", firstResponse.getTitle());
        assertEquals("image1.png", firstResponse.getFileName());
        assertNotNull(firstResponse.getId());
        List<String> firstTags = firstResponse.getTags();
        assertEquals(2, firstTags.size());
        assertTrue(firstTags.contains("Tag1"));

        ImageResponse secondResponse = imageResponseList.get(1);
        assertEquals("Image 2", secondResponse.getTitle());
        assertEquals("image2.png", secondResponse.getFileName());
        assertNotNull(secondResponse.getId());
        List<String> secondTags = secondResponse.getTags();
        assertEquals(2, secondTags.size());
        assertTrue(secondTags.contains("Tag4"));
    }

    @Test
    void testMapImages_withEmptyImageRequestList_thenReturnsEmptyList() {
        // Arrange
        List<ImageRequest> emptyRequestList = List.of();

        // Act
        List<ImageResponse> imageResponseList =
                hpscImageService.mapImages(emptyRequestList);

        // Assert
        assertNotNull(imageResponseList);
        assertTrue(imageResponseList.isEmpty());
    }

    @Test
    void testMapImages_withNullImageRequestList_thenReturnsEmptyList() {
        // Arrange
        List<ImageRequest> emptyRequestList = List.of();

        // Act
        List<ImageResponse> imageResponseList =
                hpscImageService.mapImages(emptyRequestList);

        // Assert
        assertNotNull(imageResponseList);
        assertTrue(imageResponseList.isEmpty());
    }
}