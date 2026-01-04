package za.co.hpsc.web.models;

class ImageResponseTest {

/*
    @Test
    void setMimeType_withValidMimeType_thenSetsMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse();
        String validMimeType = "image/png";

        // Act
        imageResponse.setMimeType(validMimeType);

        // Assert
        assertEquals(validMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withBlankMimeType_thenFallbackToFileNameInference() {
        // Arrange
        String fileName = "example.jpg";
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setFileName(fileName);

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
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setFileName(fileName);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        Optional<String> expectedMimeType = MediaTypeFactory.getMediaType(fileName).map(Object::toString);
        assertEquals(expectedMimeType.orElse(null), imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNullOrBlankMimeTypeAndNoFileName_thenDoesNotSetMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse();

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertNull(imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNoMimeTypeAndFileNameWithoutRecognizedExtension_thenDoesNotSetMimeType() {
        // Arrange
        String fileName = "example.unknown";
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setFileName(fileName);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        Optional<String> expectedMimeType = MediaTypeFactory.getMediaType(fileName).map(Object::toString);
        assertEquals(expectedMimeType.orElse(null), imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withoutFileNameAndMimeType_thenDoesNotSetMimeType() {
        // Arrange
        String existingMimeType = "image/jpeg";
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setMimeType(existingMimeType);

        // Act
        imageResponse.setMimeType("");

        // Assert
        assertEquals(existingMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNoMimeTypeAndFileNameWithoutRecognizedExtension_thenDoesNotModifyMimeType() {
        // Arrange
        String fileName = "example.unknown";
        String existingMimeType = "image/jpeg";
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setFileName(fileName);
        imageResponse.setMimeType(existingMimeType);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertEquals(existingMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withoutFileNameAndMimeType_thenDoesNotModifyMimeType() {
        // Arrange
        String existingMimeType = "image/jpeg";
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setMimeType(existingMimeType);

        // Act
        imageResponse.setMimeType("");

        // Assert
        assertEquals(existingMimeType, imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withUpperCaseFileExtension_thenInferMimeTypeCorrectly() {
        // Arrange
        String fileName = "IMAGE.PNG";
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setFileName(fileName);

        // Act
        imageResponse.setMimeType(null);

        // Assert
        Optional<String> expectedMimeType = MediaTypeFactory.getMediaType(fileName).map(Object::toString);
        assertEquals(expectedMimeType.orElse(null), imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNullFileNameAndMimeType_thenDoesNotSetMimeType() {
        // Arrange
        ImageResponse imageResponse = new ImageResponse();

        // Act
        imageResponse.setMimeType(null);

        // Assert
        assertNull(imageResponse.getMimeType());
    }

    @Test
    void setMimeType_withNoParameters_thenInferMimeTypeFromFileName() {
        // Arrange
        String fileName = "example.gif";
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setFileName(fileName);

        // Act
        imageResponse.setMimeType();

        // Assert
        Optional<String> expectedMimeType = MediaTypeFactory.getMediaType(fileName).map(Object::toString);
        assertEquals(expectedMimeType.orElse(null), imageResponse.getMimeType());
    }
*/
}