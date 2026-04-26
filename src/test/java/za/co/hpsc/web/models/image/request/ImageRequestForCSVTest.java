package za.co.hpsc.web.models.image.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImageRequestForCSVTest {
    private static class TestImageRequestForCsv extends ImageRequestForCsv {
        @JsonCreator
        TestImageRequestForCsv(@JsonProperty(value = "title", required = true) String title,
                               @JsonProperty(value = "filePath", required = true) String filePath,
                               @JsonProperty(value = "fileName", required = true) String fileName) {
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
    void testJsonSerialization_whenFullyPopulated_thenSerializesAllFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        TestImageRequestForCsv request = new TestImageRequestForCsv("Range Photo", "/images/2026/04", "range-photo.jpg");
        request.setSummary("A photo from the range");
        request.setDescription("Taken during the club shoot on 24 April 2026");
        request.setCategory("Gallery");
        request.setTags(List.of("club", "finals"));

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals("Range Photo", node.get("title").asText());
        assertEquals("/images/2026/04", node.get("filePath").asText());
        assertEquals("range-photo.jpg", node.get("fileName").asText());
        assertEquals("A photo from the range", node.get("summary").asText());
        assertEquals("Taken during the club shoot on 24 April 2026", node.get("description").asText());
        assertEquals("Gallery", node.get("category").asText());
        assertEquals("club", node.get("tags").get(0).asText());
        assertEquals("finals", node.get("tags").get(1).asText());
    }

    @Test
    void testJsonSerialization_whenOnlyRequiredFieldsSet_thenSerializesWithNullOptionals() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        TestImageRequestForCsv request = new TestImageRequestForCsv("Range Photo", "/images/2026/04", "range-photo.jpg");

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals("Range Photo", node.get("title").asText());
        assertEquals("/images/2026/04", node.get("filePath").asText());
        assertEquals("range-photo.jpg", node.get("fileName").asText());
        assertTrue(node.get("summary").isNull());
        assertTrue(node.get("description").isNull());
        assertTrue(node.get("category").isNull());
        assertTrue(node.get("tags").isNull());
    }

    @Test
    void testJsonDeserialization_whenTitleMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String json = """
                {
                  "filePath": "/images/2026/04",
                  "fileName": "range-photo.jpg"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestImageRequestForCsv.class));
    }

    @Test
    void testJsonDeserialization_whenFilePathMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String json = """
                {
                  "title": "Range Photo",
                  "fileName": "range-photo.jpg"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestImageRequestForCsv.class));
    }

    @Test
    void testJsonDeserialization_whenFileNameMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String json = """
                {
                  "title": "Range Photo",
                  "filePath": "/images/2026/04"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestImageRequestForCsv.class));
    }

    @Test
    void testJsonDeserialization_whenAllRequiredFieldsMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String json = """
                {
                  "summary": "A photo from the range",
                  "category": "Gallery"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestImageRequestForCsv.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String json = "{}";

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestImageRequestForCsv.class));
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

    @Test
    void testJsonDeserialization_whenValidPayloadProvided_thenCreatesInstanceWithAllFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "title": "Range Photo",
                  "filePath": "/images/2026/04",
                  "fileName": "range-photo.jpg",
                  "summary": "A photo from the range",
                  "description": "Taken during the club shoot on 24 April 2026",
                  "category": "Gallery",
                  "tags": ["club", "finals"]
                }
                """;

        // Act
        TestImageRequestForCsv request = mapper.readValue(json, TestImageRequestForCsv.class);

        // Assert
        assertEquals("Range Photo", request.getTitle());
        assertEquals("/images/2026/04", request.getFilePath());
        assertEquals("range-photo.jpg", request.getFileName());
        assertEquals("A photo from the range", request.getSummary());
        assertEquals("Taken during the club shoot on 24 April 2026", request.getDescription());
        assertEquals("Gallery", request.getCategory());
        assertEquals(List.of("club", "finals"), request.getTags());
    }
}
