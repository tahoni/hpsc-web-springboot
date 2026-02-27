package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.image.request.ImageRequest;
import za.co.hpsc.web.models.image.response.ImageResponse;
import za.co.hpsc.web.models.image.response.ImageResponseHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageServiceImpl imageService;

    // =====================================================================
    // Tests for processCsv - Valid Data Processing
    // =====================================================================

    @Test
    public void testProcessCsv_whenValidCsvData_thenReturnsImageResponseHolder() {
        // Arrange
        String csvData = """
                title,summary,description,category,tags,filePath,fileName
                Image 1,Summary 1,Description 1,Category 1,Tag1|Tag2,/path/to/image1,image1.png
                Image 2,Summary 2,Description 2,Category 2,Tag3,/path/to/image2,image2.png
                """;

        // Act
        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(csvData));

        // Assert - Verify holder structure
        List<ImageResponse> responses = responseHolder.getImages();
        assertEquals(2, responses.size());

        // Assert - Verify first image
        ImageResponse imageResponse1 = responses.getFirst();
        assertEquals("Image 1", imageResponse1.getTitle());
        assertEquals("Summary 1", imageResponse1.getSummary());
        assertEquals("Description 1", imageResponse1.getDescription());
        assertEquals("Category 1", imageResponse1.getCategory());
        assertEquals(List.of("Tag1", "Tag2"), imageResponse1.getTags());
        assertEquals("/path/to/image1", imageResponse1.getFilePath());
        assertEquals("image1.png", imageResponse1.getFileName());

        // Assert - Verify second image
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
    public void testProcessCsv_whenEmptyCsvData_thenReturnsEmptyList() {
        // Arrange - CSV with only header, no data rows
        String emptyCsvData = "title,summary,description,category,tags,filePath,fileName\n";

        // Act
        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(emptyCsvData));

        // Assert
        List<ImageResponse> responses = responseHolder.getImages();
        assertTrue(responses.isEmpty());
    }

    // =====================================================================
    // Tests for processCsv - Input Validation and Error Handling
    // =====================================================================

    @Test
    public void testProcessCsv_whenInvalidCsvData_thenThrowsValidationException() {
        // Arrange - CSV with incomplete row
        String invalidCsvData = """
                summary,description,category,tags,filePath,fileName
                Summary 1
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                imageService.processCsv(invalidCsvData));
    }

    @Test
    public void testProcessCsv_whenNullCsvData_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                imageService.processCsv(null));
    }

    @Test
    public void testProcessCsv_whenInvalidCsvFormat_thenThrowsValidationException() {
        // Arrange - Invalid CSV format
        String csvData = "Invalid CSV Format";

        // Act & Assert
        assertThrows(ValidationException.class, () -> imageService.processCsv(csvData));
    }

    // =====================================================================
    // Tests for readImages - Valid Data Processing
    // =====================================================================

    @Test
    public void testReadImages_whenValidCsv_thenReturnsImageRequestList() {
        // Arrange
        String csvData = """
                title,summary,description,category,tags,filePath,fileName
                Image 1,Summary 1,Description 1,Category 1,Tag1|Tag2,/path/to/image1,image1.png
                Image 2,Summary 2,Description 2,Category 2,Tag3|Tag4,/path/to/image2,image2.png
                """;

        // Act
        List<ImageRequest> imageRequests = assertDoesNotThrow(() ->
                imageService.readImages(csvData));

        // Assert - Verify list size
        assertNotNull(imageRequests);
        assertEquals(2, imageRequests.size());

        // Assert - Verify first image
        ImageRequest firstRequest = imageRequests.getFirst();
        assertEquals("Image 1", firstRequest.getTitle());
        assertEquals("Summary 1", firstRequest.getSummary());
        assertEquals("Description 1", firstRequest.getDescription());
        assertEquals("Category 1", firstRequest.getCategory());
        assertEquals("/path/to/image1", firstRequest.getFilePath());
        assertEquals("image1.png", firstRequest.getFileName());
        List<String> firstTags = firstRequest.getTags();
        assertEquals(2, firstTags.size());
        assertTrue(firstTags.containsAll(List.of("Tag1", "Tag2")));

        // Assert - Verify second image
        ImageRequest secondRequest = imageRequests.get(1);
        assertEquals("Image 2", secondRequest.getTitle());
        assertEquals("Summary 2", secondRequest.getSummary());
        assertEquals("Description 2", secondRequest.getDescription());
        assertEquals("Category 2", secondRequest.getCategory());
        assertEquals("/path/to/image2", secondRequest.getFilePath());
        assertEquals("image2.png", secondRequest.getFileName());
        List<String> secondTags = secondRequest.getTags();
        assertEquals(2, secondTags.size());
        assertTrue(secondTags.containsAll(List.of("Tag4", "Tag3")));
    }

    @Test
    public void testReadImages_whenValidCsvWithRearrangedColumns_thenReturnsImageRequestList() {
        // Arrange - CSV with columns in different order
        String csvData = """
                summary,title,description,category,tags,filePath,fileName
                Summary 1,Image 1,Description 1,Category 1,Tag1|Tag2,/path/to/image1,image1.png
                Summary 2,Image 2,Description 2,Category 2,Tag3|Tag4,/path/to/image2,image2.png
                """;

        // Act
        List<ImageRequest> imageRequests = assertDoesNotThrow(() ->
                imageService.readImages(csvData));

        // Assert - Verify list size and data
        assertNotNull(imageRequests);
        assertEquals(2, imageRequests.size());

        // Assert - Verify first image
        ImageRequest firstRequest = imageRequests.getFirst();
        assertEquals("Image 1", firstRequest.getTitle());
        assertEquals("Summary 1", firstRequest.getSummary());
        assertEquals("Description 1", firstRequest.getDescription());
        assertEquals("Category 1", firstRequest.getCategory());
        assertEquals("/path/to/image1", firstRequest.getFilePath());
        assertEquals("image1.png", firstRequest.getFileName());
        List<String> firstTags = firstRequest.getTags();
        assertEquals(2, firstTags.size());
        assertTrue(firstTags.containsAll(List.of("Tag1", "Tag2")));

        // Assert - Verify second image
        ImageRequest secondRequest = imageRequests.get(1);
        assertEquals("Image 2", secondRequest.getTitle());
        assertEquals("Summary 2", secondRequest.getSummary());
        assertEquals("Description 2", secondRequest.getDescription());
        assertEquals("Category 2", secondRequest.getCategory());
        assertEquals("/path/to/image2", secondRequest.getFilePath());
        assertEquals("image2.png", secondRequest.getFileName());
        List<String> secondTags = secondRequest.getTags();
        assertEquals(2, secondTags.size());
        assertTrue(secondTags.containsAll(List.of("Tag4", "Tag3")));
    }

    @Test
    public void testReadImages_whenLargeCsv_thenReturnsImageRequestList() {
        // Arrange - Create large CSV with 1000 rows
        StringBuilder largeCsv = new StringBuilder("title,summary,description,category,tags,filePath,fileName\n");

        for (int i = 0; i < 1000; i++) {
            largeCsv.append("Title ").append(i).append(",Summary ").append(i).append(",Description ").append(i)
                    .append(",Category ").append(i % 10).append(",Tag").append(i % 10);
            largeCsv.append(",path/to/image").append(i).append(",image").append(i).append(".png\n");
        }

        // Act
        List<ImageRequest> imageRequests = assertDoesNotThrow(() ->
                imageService.readImages(largeCsv.toString()));

        // Assert - Verify total count
        assertNotNull(imageRequests);
        assertEquals(1000, imageRequests.size());

        // Assert - Verify random images throughout the list
        assertEquals("Title 250", imageRequests.get(250).getTitle());
        assertEquals("Summary 250", imageRequests.get(250).getSummary());
        assertEquals("Tag0", imageRequests.get(500).getTags().getFirst());
        assertEquals("path/to/image750", imageRequests.get(750).getFilePath());
        assertEquals("image750.png", imageRequests.get(750).getFileName());

        // Assert - Verify last image has complete data
        assertEquals("Title 999", imageRequests.get(999).getTitle());
        assertEquals("Summary 999", imageRequests.get(999).getSummary());
        assertEquals("Description 999", imageRequests.get(999).getDescription());
        assertEquals("Category 9", imageRequests.get(999).getCategory());
        assertEquals("Tag9", imageRequests.get(999).getTags().getFirst());
        assertEquals("path/to/image999", imageRequests.get(999).getFilePath());
        assertEquals("image999.png", imageRequests.get(999).getFileName());
    }

    @Test
    public void testReadImages_whenEmptyCsvData_thenReturnsEmptyList() {
        // Arrange - CSV with only header, no data rows
        String emptyCsvData = "title,summary,description,category,tags,filePath,fileName\n";

        // Act
        List<ImageRequest> imageRequests = assertDoesNotThrow(() ->
                imageService.readImages(emptyCsvData));

        // Assert
        assertNotNull(imageRequests);
        assertTrue(imageRequests.isEmpty());
    }

    // =====================================================================
    // Tests for readImages - Input Validation and Error Handling
    // =====================================================================

    @Test
    public void testReadImages_whenMissingCsvColumns_thenThrowsValidationException() {
        // Arrange - CSV with missing required columns
        String csvData = """
                title,filePath,fileName
                Image 1,/path/to/image1,image1.png
                Image 2,/path/to/image2,image2.png
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                imageService.readImages(csvData));
    }

    @Test
    public void testReadImages_whenInvalidCsvData_thenThrowsValidationException() {
        // Arrange - CSV row with incorrect number of columns
        String invalidCsvData = """
                title,summary,description,category,tags,filePath,fileName
                Invalid Row Without Correct Columns
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                imageService.readImages(invalidCsvData));
    }

    @Test
    public void testReadImages_whenInvalidCsvStructure_thenThrowsValidationException() {
        // Arrange - CSV with invalid headers
        String invalidCsvStructure = """
                Invalid_Header1,Invalid_Header2,Invalid_Header3
                value1,value2
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                imageService.readImages(invalidCsvStructure));
    }

    @Test
    public void testReadImages_whenInvalidCsvFormat_thenThrowsValidationException() {
        // Arrange - Single column with no proper header
        String invalidCsv = """
                Invalid CSV With One Column and no Header
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                imageService.readImages(invalidCsv));
    }

    @Test
    public void testReadImages_whenBlankCsv_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                imageService.readImages("    "));
    }

    @Test
    public void testReadImages_whenEmptyStringCsv_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                imageService.readImages(""));
    }

    @Test
    public void testReadImages_whenNullCsv_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                imageService.readImages(null));
    }

    // =====================================================================
    // Tests for mapImages - Valid Data Processing
    // =====================================================================

    @Test
    public void testMapImages_whenValidImageRequestList_thenReturnsImageResponseList() {
        // Arrange
        ImageRequest request1 = new ImageRequest("Image 1", "Summary 1", "Description 1",
                "Category 1", List.of("Tag1", "Tag2"), "/path/to/image1", "image1.png");
        ImageRequest request2 = new ImageRequest("Image 2", "Summary 2", "Description 2",
                "Category 2", List.of("Tag3", "Tag4"), "/path/to/image2", "image2.png");
        List<ImageRequest> imageRequestList = List.of(request1, request2);

        // Act
        List<ImageResponse> imageResponseList =
                imageService.mapImages(imageRequestList);

        // Assert - Verify list size
        assertNotNull(imageResponseList);
        assertEquals(2, imageResponseList.size());

        // Assert - Verify first image
        ImageResponse firstResponse = imageResponseList.getFirst();
        assertEquals("Image 1", firstResponse.getTitle());
        assertEquals("image1.png", firstResponse.getFileName());
        assertNotNull(firstResponse.getUuid());
        List<String> firstTags = firstResponse.getTags();
        assertEquals(2, firstTags.size());
        assertTrue(firstTags.containsAll(List.of("Tag1", "Tag2")));

        // Assert - Verify second image
        ImageResponse secondResponse = imageResponseList.get(1);
        assertEquals("Image 2", secondResponse.getTitle());
        assertEquals("image2.png", secondResponse.getFileName());
        assertNotNull(secondResponse.getUuid());
        List<String> secondTags = secondResponse.getTags();
        assertEquals(2, secondTags.size());
        assertTrue(secondTags.containsAll(List.of("Tag4", "Tag3")));
    }

    // =====================================================================
    // Tests for mapImages - Input Validation and Error Handling
    // =====================================================================

    @Test
    public void testMapImages_whenEmptyImageRequestList_thenReturnsEmptyList() {
        // Act
        List<ImageResponse> imageResponseList =
                imageService.mapImages(List.of());

        // Assert
        assertNotNull(imageResponseList);
        assertTrue(imageResponseList.isEmpty());
    }

    @Test
    public void testMapImages_whenNullImageRequestList_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                imageService.mapImages(null));
    }
}

