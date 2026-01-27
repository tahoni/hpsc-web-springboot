package za.co.hpsc.web.models.image;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import za.co.hpsc.web.models.image.request.ImageRequest;
import za.co.hpsc.web.models.image.response.ImageResponse;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ImageResponseTest {

    @Test
    void testConstructor_withMimeType_thenUseProvidedMimeType() {
        // Act
        ImageResponse response = new ImageResponse(
                UUID.randomUUID(), "Title", "path/to", "image.jpg", MediaType.IMAGE_PNG_VALUE
        );

        // Assert
        assertEquals(MediaType.IMAGE_PNG_VALUE, response.getMimeType());
    }

    @Test
    void testConstructor_withNoMimeType_thenInfersMimeTypeFromFileName() {
        // Act
        ImageResponse jpgResponse = new ImageResponse(
                null, "Title", "path/", "photo.jpg", ""
        );
        ImageResponse pngResponse = new ImageResponse(
                null, "Title", "path/", "graphic.png", null
        );

        // Assert
        assertEquals(MediaType.IMAGE_JPEG_VALUE, jpgResponse.getMimeType());
        assertEquals(MediaType.IMAGE_PNG_VALUE, pngResponse.getMimeType());
    }

    @Test
    void testFullConstructor_thenInitializesAllFields() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        List<String> tags = List.of("nature", "forest");

        // Act
        ImageResponse response = new ImageResponse(
                uuid, "Forest", "A summary", "Long description",
                "Nature", tags, "/images", "forest.webp", "image/webp"
        );

        // Assert
        assertEquals(uuid, response.getUuid());
        assertEquals("Forest", response.getTitle());
        assertEquals("A summary", response.getSummary());
        assertEquals("Long description", response.getDescription());
        assertEquals("Nature", response.getCategory());
        assertEquals("/images", response.getFilePath());
        assertEquals("forest.webp", response.getFileName());
        assertEquals("image/webp", response.getMimeType());

        assertEquals(2, response.getTags().size());
        assertTrue(response.getTags().containsAll(tags));
    }

    @Test
    void testConstructor_withImageRequestMapsFields_thenInitializesAllFieldsAndInfersMimeType() {
        // Arrange
        ImageRequest request = new ImageRequest();
        request.setTitle("Request Title");
        request.setSummary("Summary");
        request.setDescription("Desc");
        request.setCategory("Cat");
        request.setTags(List.of("t1"));
        request.setFilePath("/path");
        request.setFileName("file.png");

        // Act
        ImageResponse response = new ImageResponse(request);

        // Assert
        assertEquals("Request Title", response.getTitle());
        assertEquals("file.png", response.getFileName());
        assertEquals(MediaType.IMAGE_PNG_VALUE, response.getMimeType());
        assertNotNull(response.getUuid());
    }

    @Test
    void setMimeType_withValidMimeType_thenSetMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "", "");
        String validMimeType = "image/png";

        // Act
        imageResponse.setMimeType(validMimeType);

        // Assert
        assertEquals(validMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNullOrBlankValues_doesNotAlterExistingMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "example.jpg", "image/jpeg");

        // Act
        imageResponse.setMimeType(null);
        String mimeTypeAfterNullInput = imageResponse.getMimeType();
        imageResponse.setMimeType("");
        String mimeTypeAfterBlankInput = imageResponse.getMimeType();

        // Assert
        assertEquals(MediaType.IMAGE_JPEG_VALUE, mimeTypeAfterNullInput);
        assertEquals(MediaType.IMAGE_JPEG_VALUE, mimeTypeAfterBlankInput);
    }

    @Test
    void setMimeType_withBlankMimeType_thenInferMimeTypeFromFileName() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "example.jpg", "");

        // Act
        imageResponse.setMimeType("");

        // Assert
        assertEquals(MediaType.IMAGE_JPEG_VALUE, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNullMimeType_thenInferMimeTypeFromFileName() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "example.gif", "");

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals("image/gif", imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNullOrBlankMimeTypeAndNoFileName_thenDoesNotSetMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "", "");

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals("", imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNoMimeTypeAndFileNameWithoutRecognizedExtension_thenDoesNotSetMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "example.unknown", "");

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals("", imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withoutFileNameAndMimeType_thenDoesNotSetMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "", "");

        // Act
        imageResponse.setMimeType("");

        // Assert
        assertEquals("", imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNoMimeTypeAndFileNameWithoutRecognizedExtension_thenDoesNotModifyMimeType() {
        // Arrange
        String existingMimeType = MediaType.IMAGE_JPEG_VALUE;
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "example.unknown", existingMimeType);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals(existingMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withoutFileNameAndMimeType_thenDoesNotModifyMimeType() {
        // Arrange
        String existingMimeType = MediaType.IMAGE_JPEG_VALUE;
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "", existingMimeType);

        // Act
        imageResponse.setMimeType("");

        // Assert
        assertEquals(existingMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withUpperCaseFileExtension_thenInferMimeTypeCorrectly() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "IMAGE.PNG", "");

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals(MediaType.IMAGE_PNG_VALUE, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNullFileNameAndMimeType_thenDoesNotSetMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", null, "");

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals("", imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withBlankFileNameAndMimeType_thenDoesNotSetMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "   ", "");

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals("", imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withUncommonValidMimeType_thenRetainsMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "example.custom", "");
        String validMimeType = "custom/type";

        // Act
        imageResponse.setMimeType(validMimeType);

        // Assert
        assertEquals(validMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withOverwriteExistingMimeType_thenUpdatesMimeType() {
        // Arrange
        String existingMimeType = MediaType.IMAGE_JPEG_VALUE;
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "example.jpg", existingMimeType);
        String newMimeType = MediaType.IMAGE_PNG_VALUE;

        // Act
        imageResponse.setMimeType(newMimeType);

        // Assert
        assertEquals(newMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withConflictingFileNameAndValidMimeType_thenUsesExplicitMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "example.jpg", "");
        String validMimeType = MediaType.IMAGE_JPEG_VALUE;

        // Act
        imageResponse.setMimeType(validMimeType);

        // Assert
        assertEquals(validMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNoParameters_thenInferMimeTypeFromFileName() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse(UUID.randomUUID(), "Title", "/path/to/image", "example.gif", "");

        // Act
        imageResponse.setMimeType();

        // Assert
        assertEquals(MediaType.IMAGE_GIF_VALUE, imageResponse.getMimeType());
    }
}