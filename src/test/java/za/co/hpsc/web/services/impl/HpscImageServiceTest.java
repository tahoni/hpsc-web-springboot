package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ImageRequest;
import za.co.hpsc.web.models.ImageResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HpscImageServiceTest {

    @InjectMocks
    private final HpscImageService hpscImageService = new HpscImageService();

    @Test
    void testReadImages_withValidCsv_thenReturnsImageRequestList() {
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
        assertEquals(2, imageRequests.size());

        // Assert the first image
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

        // Assert the second image
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
    void testReadImages_withValidCsvRearrangedColumns_thenReturnsImageRequestList() {
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
        assertEquals(2, imageRequests.size());

        // Assert the first image
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

        // Assert the second image
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
    public void testReadImages_withLargeCsv_thenReturnsImageRequestList() {
        // Arrange
        StringBuilder largeCsv = new StringBuilder("title,summary,description,category,tags,filePath,fileName\n");

        for (int i = 0; i < 1000; i++) {
            // Appends title, summary, description, category to CSV
            largeCsv.append("Title ").append(i).append(",Summary ").append(i).append(",Description ").append(i)
                    .append(",Category ").append(i % 10).append(",Tag").append(i % 10);
            // Appends file path and file name
            largeCsv.append(",path/to/image").append(i).append(",image").append(i).append(".png\n");
        }

        // Act
        List<ImageRequest> awardRequests = assertDoesNotThrow(() ->
                hpscImageService.readImages(largeCsv.toString()));

        // Assert
        assertNotNull(awardRequests);
        assertEquals(1000, awardRequests.size());
        // Assert random images
        assertEquals("Title 250", awardRequests.get(250).getTitle());
        assertEquals("Summary 250", awardRequests.get(250).getSummary());
        assertEquals("Tag0", awardRequests.get(500).getTags().getFirst());
        assertEquals("path/to/image750", awardRequests.get(750).getFilePath());
        assertEquals("image750.png", awardRequests.get(750).getFileName());
        // Assert last image
        assertEquals("Title 999", awardRequests.get(999).getTitle());
        assertEquals("Summary 999", awardRequests.get(999).getSummary());
        assertEquals("Category 9", awardRequests.get(999).getCategory());
        assertEquals("Tag9", awardRequests.get(999).getTags().getFirst());
        assertEquals("path/to/image999", awardRequests.get(999).getFilePath());
        assertEquals("image999.png", awardRequests.get(999).getFileName());
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
        assertThrows(ValidationException.class, () ->
                hpscImageService.readImages(csvData));
    }

    @Test
    void testReadImages_withInvalidCsvData_thenThrowsException() {
        // Arrange
        String invalidCsvData = """
                title,summary,description,category,tags,filePath,fileName
                Invalid Row Without Correct Columns
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                hpscImageService.readImages(invalidCsvData));
    }

    @Test
    void testReadImages_withEmptyCsvData_thenReturnsEmptyList() {
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
    void testReadImages_withInvalidCsvStructure_thenThrowsException() {
        // Arrange
        String invalidCsvStructure = """
                Invalid_Header1,Invalid_Header2,Invalid_Header3
                value1,value2
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                hpscImageService.readImages(invalidCsvStructure));
    }

    @Test
    void testReadImages_withInvalidCsv_thenThrowsException() {
        // Arrange
        String invalidCsv = """
                Invalid CSV With One Column and no Header
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                hpscImageService.readImages(invalidCsv));
    }

    @Test
    void testReadImages_withBlankCsv_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                hpscImageService.readImages("    "));
    }

    @Test
    void testReadImages_withEmptyCsv_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                hpscImageService.readImages(""));
    }

    @Test
    void testReadImages_withNullCsv_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                hpscImageService.readImages(null));
    }

    @Test
    void testMapImages_withValidImageRequestList_thenReturnsImageResponseList() {
        // Arrange
        ImageRequest request1 = new ImageRequest("Image 1", "Summary 1", "Description 1",
                "Category 1", List.of("Tag1", "Tag2"), "/path/to/image1", "image1.png");
        ImageRequest request2 = new ImageRequest("Image 2", "Summary 2", "Description 2",
                "Category 2", List.of("Tag3", "Tag4"), "/path/to/image2", "image2.png");
        List<ImageRequest> imageRequestList = List.of(request1, request2);

        // Act
        List<ImageResponse> imageResponseList =
                hpscImageService.mapImages(imageRequestList);

        // Assert
        assertNotNull(imageResponseList);
        assertEquals(2, imageResponseList.size());

        // Assert the first image
        ImageResponse firstResponse = imageResponseList.getFirst();
        assertEquals("Image 1", firstResponse.getTitle());
        assertEquals("image1.png", firstResponse.getFileName());
        assertNotNull(firstResponse.getUuid());
        List<String> firstTags = firstResponse.getTags();
        assertEquals(2, firstTags.size());
        assertTrue(firstTags.containsAll(List.of("Tag1", "Tag2")));

        // Assert the second image
        ImageResponse secondResponse = imageResponseList.get(1);
        assertEquals("Image 2", secondResponse.getTitle());
        assertEquals("image2.png", secondResponse.getFileName());
        assertNotNull(secondResponse.getUuid());
        List<String> secondTags = secondResponse.getTags();
        assertEquals(2, secondTags.size());
        assertTrue(secondTags.containsAll(List.of("Tag4", "Tag3")));
    }

    @Test
    void testMapImages_withEmptyImageRequestList_thenReturnsEmptyList() {
        // Act
        List<ImageResponse> imageResponseList =
                hpscImageService.mapImages(List.of());

        // Assert
        assertNotNull(imageResponseList);
        assertTrue(imageResponseList.isEmpty());
    }

    @Test
    void testMapImages_withNullImageRequestList_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                hpscImageService.mapImages(null));
    }
}