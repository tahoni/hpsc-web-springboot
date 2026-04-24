package za.co.hpsc.web.models.image.request;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImageRequestForCSVTest {
    @NoArgsConstructor
    private static class TestImageRequestForCsv extends ImageRequestForCsv {
        TestImageRequestForCsv(String title, String filePath, String fileName) {
            super(title, filePath, fileName);
        }
    }

    @Test
    void testConstructor_whenRequiredFieldsProvided_thenMapsRequiredFieldsAndLeavesOptionalFieldsUnset() {
        // Arrange & Act
        TestImageRequestForCsv request = new TestImageRequestForCsv(
                "Range Photo",
                "/images/2026/04",
                "range-photo.jpg"
        );

        // Assert
        assertEquals("Range Photo", request.getTitle());
        assertEquals("/images/2026/04", request.getFilePath());
        assertEquals("range-photo.jpg", request.getFileName());
        assertNull(request.getSummary());
        assertNull(request.getDescription());
        assertNull(request.getCategory());
        assertNull(request.getTags());
    }

    @Test
    void testConstructor_whenRequiredFieldsAreNull_thenStoresNullValues() {
        // Arrange & Act
        TestImageRequestForCsv request = new TestImageRequestForCsv(null, null, null);

        // Assert
        assertNull(request.getTitle());
        assertNull(request.getFilePath());
        assertNull(request.getFileName());
    }

    @Test
    void testSetters_whenOptionalFieldsProvided_thenUpdatesMetadataAndTags() {
        // Arrange
        TestImageRequestForCsv request = new TestImageRequestForCsv("Title", "/path", "file.png");
        List<String> tags = List.of("club", "finals");

        // Act
        request.setSummary("Summary");
        request.setDescription("Description");
        request.setCategory("Gallery");
        request.setTags(tags);

        // Assert
        assertEquals("Summary", request.getSummary());
        assertEquals("Description", request.getDescription());
        assertEquals("Gallery", request.getCategory());
        assertEquals(tags, request.getTags());
    }

    @Test
    void testJsonDeserialization_whenUnknownProperty_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String jsonUnknownProperty = """
                {
                  "title": "Podium Shot",
                  "name": "Podium Shot"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> objectMapper.readValue(jsonUnknownProperty, TestImageRequestForCsv.class));
    }
}
