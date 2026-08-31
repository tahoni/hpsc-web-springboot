package za.co.hpsc.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.image.response.ImageResponse;
import za.co.hpsc.web.models.image.response.ImageResponseHolder;
import za.co.hpsc.web.services.impl.ImageServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ImageService} contract, exercised entirely through the
 * interface type. Covers {@code processCsv} - the interface's only declared method.
 * Impl-specific helper methods ({@code readImages}, {@code mapImages}) are covered by
 * {@link za.co.hpsc.web.services.impl.ImageServiceImplTest}.
 */
@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    private static final String CSV_HEADER =
            "title,summary,description,category,tags,filePath,fileName\n";

    @InjectMocks
    private ImageServiceImpl imageServiceImpl;

    private ImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = imageServiceImpl;
    }

    // processCsv()
    @Test
    void testProcessCsv_whenCsvDataIsNull_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> imageService.createImages(null));
    }

    @Test
    void testProcessCsv_whenCsvDataIsEmpty_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> imageService.createImages(""));
    }

    @Test
    void testProcessCsv_whenCsvDataIsBlank_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> imageService.createImages("   \t\n  "));
    }

    @Test
    void testProcessCsv_whenCsvIsPlainText_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> imageService.createImages("Invalid CSV Format"));
    }

    @Test
    void testProcessCsv_whenRequiredColumnsAreMissing_thenThrowsValidationException() {
        // Arrange
        String csvData = """
                summary,description,tags
                A summary,A description,Tag1
                """;

        // Act / Assert
        assertThrows(ValidationException.class, () -> imageService.createImages(csvData));
    }

    @Test
    void testProcessCsv_whenHeaderOnlyWithNoDataRows_thenReturnsEmptyImageList() {
        // Act
        ImageResponseHolder responseHolder = assertDoesNotThrow(() -> imageService.createImages(CSV_HEADER));

        // Assert
        assertNotNull(responseHolder);
        assertTrue(responseHolder.getImages().isEmpty());
    }

    @Test
    void testProcessCsv_whenSingleImageWithAllFields_thenReturnsMappedResponse() {
        // Arrange
        String csvData = CSV_HEADER +
                "Landscape Shot,Beautiful landscape,A wide open field,Nature,mountains|plains,/photos/nature,landscape.jpg\n";

        // Act
        ImageResponseHolder responseHolder = assertDoesNotThrow(() -> imageService.createImages(csvData));

        // Assert
        List<ImageResponse> images = responseHolder.getImages();
        assertEquals(1, images.size());
        ImageResponse image = images.getFirst();
        assertNotNull(image.getUuid());
        assertEquals("Landscape Shot", image.getTitle());
        assertEquals("Beautiful landscape", image.getSummary());
        assertEquals(List.of("mountains", "plains"), image.getTags());
        assertEquals("/photos/nature", image.getFilePath());
        assertEquals("landscape.jpg", image.getFileName());
        assertEquals("image/jpeg", image.getMimeType());
    }

    @Test
    void testProcessCsv_whenMultipleImages_thenReturnsAllMappedResponses() {
        // Arrange
        String csvData = CSV_HEADER +
                "Photo A,Summary A,Desc A,Events,Tag1|Tag2,/photos/a,a.png\n" +
                "Photo B,Summary B,Desc B,Portraits,Tag3,/photos/b,b.jpg\n";

        // Act
        ImageResponseHolder responseHolder = assertDoesNotThrow(() -> imageService.createImages(csvData));

        // Assert
        List<ImageResponse> images = responseHolder.getImages();
        assertEquals(2, images.size());
        assertEquals("Photo A", images.get(0).getTitle());
        assertEquals("Photo B", images.get(1).getTitle());
    }

    @Test
    void testProcessCsv_whenColumnsAreReordered_thenMapsAllFieldsCorrectly() {
        // Arrange
        String csvData = """
                fileName,filePath,tags,category,description,summary,title
                portrait.png,/photos/portrait,outdoor|sunlight,Portraits,Golden hour portrait,Warm tones,Golden Hour
                """;

        // Act
        ImageResponseHolder responseHolder = assertDoesNotThrow(() -> imageService.createImages(csvData));

        // Assert
        ImageResponse image = responseHolder.getImages().getFirst();
        assertEquals("Golden Hour", image.getTitle());
        assertEquals("Warm tones", image.getSummary());
        assertEquals(List.of("outdoor", "sunlight"), image.getTags());
        assertEquals("portrait.png", image.getFileName());
    }
}
