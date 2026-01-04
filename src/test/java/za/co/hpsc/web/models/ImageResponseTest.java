package za.co.hpsc.web.models;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaTypeFactory;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageResponseTest {

    private static final String TITLE = "Title";
    private static final String FILE_PATH = "/path/to/image";
    private static final UUID ID = UUID.randomUUID();

    @Test
    void setMimeType_withValidMimeType_thenSetMimeType() {
        // Arrange
        String fileName = "";
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, mimeType);
        String validMimeType = "image/png";

        // Act
        imageResponse.setMimeType(validMimeType);

        // Assert
        assertEquals(validMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNullOrBlankValues_doesNotAlterExistingMimeType() {
        // Arrange
        String fileName = "example.jpg";
        String existingMimeType = "image/jpeg";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, existingMimeType);

        // Act
        imageResponse.setMimeType(null);
        String mimeTypeAfterNullInput = imageResponse.getMimeType();
        imageResponse.setMimeType("");
        String mimeTypeAfterBlankInput = imageResponse.getMimeType();

        // Assert
        assertEquals(existingMimeType, mimeTypeAfterNullInput);
        assertEquals(existingMimeType, mimeTypeAfterBlankInput);
    }

    @Test
    void setMimeType_withBlankMimeType_thenInferMimeTypeFromFileName() {
        // Arrange
        String fileName = "example.jpg";
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, mimeType);

        // Act
        imageResponse.setMimeType("");

        // Assert
        Optional<String> expectedMimeType = MediaTypeFactory.getMediaType(fileName).map(Object::toString);
        assertEquals(expectedMimeType.orElse(null), imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNullMimeType_thenInferMimeTypeFromFileName() {
        // Arrange
        String fileName = "example.gif";
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, mimeType);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        Optional<String> expectedMimeType = MediaTypeFactory.getMediaType(fileName).map(Object::toString);
        assertEquals(expectedMimeType.orElse(null), imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNullOrBlankMimeTypeAndNoFileName_thenDoesNotSetMimeType() {
        // Arrange
        String fileName = "";
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, mimeType);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals("", imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNoMimeTypeAndFileNameWithoutRecognizedExtension_thenDoesNotSetMimeType() {
        // Arrange
        String fileName = "example.unknown";
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, mimeType);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        Optional<String> expectedMimeType = MediaTypeFactory.getMediaType(fileName).map(Object::toString);
        assertEquals(expectedMimeType.orElse(""), imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withoutFileNameAndMimeType_thenDoesNotSetMimeType() {
        // Arrange
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, "", ID, mimeType);

        // Act
        imageResponse.setMimeType("");

        // Assert
        assertEquals("", imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNoMimeTypeAndFileNameWithoutRecognizedExtension_thenDoesNotModifyMimeType() {
        // Arrange
        String fileName = "example.unknown";
        String existingMimeType = "image/jpeg";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, existingMimeType);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals(existingMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withoutFileNameAndMimeType_thenDoesNotModifyMimeType() {
        // Arrange
        String fileName = "";
        String existingMimeType = "image/jpeg";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, existingMimeType);

        // Act
        imageResponse.setMimeType("");

        // Assert
        assertEquals(existingMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withUpperCaseFileExtension_thenInferMimeTypeCorrectly() {
        // Arrange
        String fileName = "IMAGE.PNG";
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, mimeType);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        Optional<String> expectedMimeType = MediaTypeFactory.getMediaType(fileName).map(Object::toString);
        assertEquals(expectedMimeType.orElse(null), imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNullFileNameAndMimeType_thenDoesNotSetMimeType() {
        // Arrange
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, null, ID, mimeType);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals("", imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withBlankFileNameAndMimeType_thenDoesNotSetMimeType() {
        // Arrange
        String fileName = "   ";
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, mimeType);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals("", imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withUncommonValidMimeType_thenRetainsMimeType() {
        // Arrange
        String fileName = "example.custom";
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, mimeType);
        String validMimeType = "custom/type";

        // Act
        imageResponse.setMimeType(validMimeType);

        // Assert
        assertEquals(validMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withOverwriteExistingMimeType_thenUpdatesMimeType() {
        // Arrange
        String fileName = "example.jpg";
        String existingMimeType = "image/jpeg";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, existingMimeType);
        String newMimeType = "image/png";

        // Act
        imageResponse.setMimeType(newMimeType);

        // Assert
        assertEquals(newMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withConflictingFileNameAndValidMimeType_thenUsesExplicitMimeType() {
        // Arrange
        String fileName = "example.jpg";
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, mimeType);
        String validMimeType = "application/pdf";

        // Act
        imageResponse.setMimeType(validMimeType);

        // Assert
        assertEquals(validMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNoParameters_thenInferMimeTypeFromFileName() {
        // Arrange
        String fileName = "example.gif";
        String mimeType = "";
        ImageResponse imageResponse = new ImageResponse(TITLE, FILE_PATH, fileName, ID, mimeType);

        // Act
        imageResponse.setMimeType();

        // Assert
        Optional<String> expectedMimeType = MediaTypeFactory.getMediaType(fileName).map(Object::toString);
        assertEquals(expectedMimeType.orElse(null), imageResponse.getMimeType());
    }
}