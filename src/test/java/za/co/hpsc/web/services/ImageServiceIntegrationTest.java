package za.co.hpsc.web.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.image.response.ImageResponse;
import za.co.hpsc.web.models.image.response.ImageResponseHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class ImageServiceIntegrationTest {

    private static final String CSV_HEADER =
            "title,summary,description,category,tags,filePath,fileName\n";

    @Autowired
    private ImageService imageService;

    // Test Group: Null/Empty/Blank Input Handling

    @Test
    public void testProcessCsv_whenCsvDataIsNull_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> imageService.processCsv(null));
    }

    @Test
    public void testProcessCsv_whenCsvDataIsEmpty_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> imageService.processCsv(""));
    }

    @Test
    public void testProcessCsv_whenCsvDataIsBlank_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> imageService.processCsv("   \t\n  "));
    }

    // Test Group: Invalid CSV Format Handling

    @Test
    public void testProcessCsv_whenCsvIsPlainText_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                imageService.processCsv("This is not valid CSV data"));
    }

    @Test
    public void testProcessCsv_whenRequiredColumnsAreMissing_thenThrowsValidationException() {
        String csvData = """
                summary,description,tags
                A summary,A description,Tag1
                """;
        assertThrows(ValidationException.class, () -> imageService.processCsv(csvData));
    }

    @Test
    public void testProcessCsv_whenRowHasTooFewValues_thenThrowsValidationException() {
        String csvData = """
                title,summary,description,category,tags,filePath,fileName
                Incomplete Row
                """;
        assertThrows(ValidationException.class, () -> imageService.processCsv(csvData));
    }

    // Test Group: Valid Single Image Processing

    @Test
    public void testProcessCsv_whenSingleImageWithAllFields_thenReturnsMappedResponse() {
        String csvData = CSV_HEADER +
                "Landscape Shot,Beautiful landscape,A wide open field,Nature,mountains|plains,/photos/nature,landscape.jpg\n";

        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<ImageResponse> images = responseHolder.getImages();
        assertEquals(1, images.size());

        ImageResponse image = images.getFirst();
        assertNotNull(image.getUuid());
        assertEquals("Landscape Shot", image.getTitle());
        assertEquals("Beautiful landscape", image.getSummary());
        assertEquals("A wide open field", image.getDescription());
        assertEquals("Nature", image.getCategory());
        assertEquals(List.of("mountains", "plains"), image.getTags());
        assertEquals("/photos/nature", image.getFilePath());
        assertEquals("landscape.jpg", image.getFileName());
        assertEquals("image/jpeg", image.getMimeType());
    }

    @Test
    public void testProcessCsv_whenHeaderOnlyWithNoDataRows_thenReturnsEmptyImageList() {
        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(CSV_HEADER));

        assertNotNull(responseHolder);
        assertTrue(responseHolder.getImages().isEmpty());
    }

    // Test Group: Valid Multiple Images Processing

    @Test
    public void testProcessCsv_whenMultipleImages_thenReturnsAllMappedResponses() {
        String csvData = CSV_HEADER +
                "Photo A,Summary A,Desc A,Events,Tag1|Tag2,/photos/a,a.png\n" +
                "Photo B,Summary B,Desc B,Portraits,Tag3,/photos/b,b.jpg\n" +
                "Photo C,,,,,/photos/c,c.gif\n";

        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<ImageResponse> images = responseHolder.getImages();
        assertEquals(3, images.size());

        assertEquals("Photo A", images.get(0).getTitle());
        assertEquals("Photo B", images.get(1).getTitle());
        assertEquals("Photo C", images.get(2).getTitle());
    }

    @Test
    public void testProcessCsv_whenOptionalFieldsAreEmpty_thenReturnsEmptyStringsAndEmptyTagList() {
        String csvData = CSV_HEADER +
                "Minimal Image,,,,,/photos/minimal,minimal.png\n";

        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<ImageResponse> images = responseHolder.getImages();
        assertEquals(1, images.size());

        ImageResponse image = images.getFirst();
        assertEquals("Minimal Image", image.getTitle());
        assertEquals("", image.getSummary());
        assertEquals("", image.getDescription());
        assertEquals("", image.getCategory());
        assertNotNull(image.getTags());
        assertTrue(image.getTags().isEmpty());
        assertEquals("/photos/minimal", image.getFilePath());
        assertEquals("minimal.png", image.getFileName());
        assertEquals("image/png", image.getMimeType());
    }

    // Test Group: CSV Field Parsing

    @Test
    public void testProcessCsv_whenColumnsAreReordered_thenMapsAllFieldsCorrectly() {
        String csvData = """
                fileName,filePath,tags,category,description,summary,title
                portrait.png,/photos/portrait,outdoor|sunlight,Portraits,Golden hour portrait,Warm tones,Golden Hour
                """;

        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<ImageResponse> images = responseHolder.getImages();
        assertEquals(1, images.size());

        ImageResponse image = images.getFirst();
        assertEquals("Golden Hour", image.getTitle());
        assertEquals("Warm tones", image.getSummary());
        assertEquals("Golden hour portrait", image.getDescription());
        assertEquals("Portraits", image.getCategory());
        assertEquals(List.of("outdoor", "sunlight"), image.getTags());
        assertEquals("/photos/portrait", image.getFilePath());
        assertEquals("portrait.png", image.getFileName());
    }

    @Test
    public void testProcessCsv_whenQuotedFieldsContainCommas_thenPreservesCompleteFieldValues() {
        String csvData = CSV_HEADER +
                "\"Prize, Giving\",\"Summary, with comma\",\"Description, with comma\",\"Events, Outdoor\",\"gold|silver\",/photos/quoted,quoted.jpg\n";

        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<ImageResponse> images = responseHolder.getImages();
        assertEquals(1, images.size());

        ImageResponse image = images.getFirst();
        assertEquals("Prize, Giving", image.getTitle());
        assertEquals("Summary, with comma", image.getSummary());
        assertEquals("Description, with comma", image.getDescription());
        assertEquals("Events, Outdoor", image.getCategory());
        assertEquals(List.of("gold", "silver"), image.getTags());
        assertEquals("quoted.jpg", image.getFileName());
        assertEquals("image/jpeg", image.getMimeType());
    }

    @Test
    public void testProcessCsv_whenCsvUsesWindowsLineEndings_thenProcessesAllRows() {
        String csvData = "title,summary,description,category,tags,filePath,fileName\r\n"
                + "Windows A,Summary A,Description A,Category A,tag1|tag2,/windows/a,a.png\r\n"
                + "Windows B,Summary B,Description B,Category B,tag3,/windows/b,b.gif\r\n";

        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<ImageResponse> images = responseHolder.getImages();
        assertEquals(2, images.size());
        assertEquals("Windows A", images.get(0).getTitle());
        assertEquals(List.of("tag1", "tag2"), images.get(0).getTags());
        assertEquals("Windows B", images.get(1).getTitle());
        assertEquals("image/gif", images.get(1).getMimeType());
    }

    @Test
    public void testProcessCsv_whenTagsUsePipeSeparator_thenParsesEachTagAsListEntry() {
        String csvDataWithTags = CSV_HEADER +
                "Wildlife,,,Nature,lion|tiger|cheetah,/photos/wild,animal.png\n";

        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(csvDataWithTags));

        assertNotNull(responseHolder);
        ImageResponse image = responseHolder.getImages().getFirst();
        assertEquals(List.of("lion", "tiger", "cheetah"), image.getTags());
    }

    @Test
    public void testProcessCsv_whenSingleTag_thenReturnsOneElementTagList() {
        String csvDataWithTag = CSV_HEADER +
                "Solo Tag Image,,,Nature,wildlife,/photos/solo,solo.png\n";

        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(csvDataWithTag));

        assertNotNull(responseHolder);
        assertEquals(List.of("wildlife"), responseHolder.getImages().getFirst().getTags());
    }

    // Test Group: MIME Type Detection

    @Test
    public void testProcessCsv_whenFileNameIsPng_thenMimeTypeIsImagePng() {
        String csvData = CSV_HEADER + "PNG Image,,,,,/photos,image.png\n";

        ImageResponse image = assertDoesNotThrow(() ->
                imageService.processCsv(csvData)).getImages().getFirst();

        assertEquals("image/png", image.getMimeType());
    }

    @Test
    public void testProcessCsv_whenFileNameIsJpeg_thenMimeTypeIsImageJpeg() {
        String csvData = CSV_HEADER + "JPEG Image,,,,,/photos,image.jpeg\n";

        ImageResponse image = assertDoesNotThrow(() ->
                imageService.processCsv(csvData)).getImages().getFirst();

        assertEquals("image/jpeg", image.getMimeType());
    }

    @Test
    public void testProcessCsv_whenFileNameIsJpg_thenMimeTypeIsImageJpeg() {
        String csvData = CSV_HEADER + "JPG Image,,,,,/photos,image.jpg\n";

        ImageResponse image = assertDoesNotThrow(() ->
                imageService.processCsv(csvData)).getImages().getFirst();

        assertEquals("image/jpeg", image.getMimeType());
    }

    @Test
    public void testProcessCsv_whenFileNameIsGif_thenMimeTypeIsImageGif() {
        String csvData = CSV_HEADER + "GIF Image,,,,,/photos,image.gif\n";

        ImageResponse image = assertDoesNotThrow(() ->
                imageService.processCsv(csvData)).getImages().getFirst();

        assertEquals("image/gif", image.getMimeType());
    }

    @Test
    public void testProcessCsv_whenFileNameHasUnknownExtension_thenMimeTypeIsEmpty() {
        String csvData = CSV_HEADER + "Unknown Image,,,,,/photos,image.unknownext\n";

        ImageResponse image = assertDoesNotThrow(() ->
                imageService.processCsv(csvData)).getImages().getFirst();

        assertEquals("", image.getMimeType());
    }

    @Test
    public void testProcessCsv_whenFileNameIsEmpty_thenMimeTypeIsEmpty() {
        String csvData = CSV_HEADER + "No File Name,,,,,/photos,\n";

        ImageResponse image = assertDoesNotThrow(() ->
                imageService.processCsv(csvData)).getImages().getFirst();

        assertEquals("", image.getMimeType());
    }

    // Test Group: UUID Generation

    @Test
    public void testProcessCsv_whenMultipleImages_thenEachResponseHasUniqueUuid() {
        String csvData = CSV_HEADER +
                "Image A,,,,,/photos/a,a.png\n" +
                "Image B,,,,,/photos/b,b.png\n" +
                "Image C,,,,,/photos/c,c.png\n";

        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(csvData));

        List<ImageResponse> images = responseHolder.getImages();
        assertEquals(3, images.size());
        assertNotEquals(images.get(0).getUuid(), images.get(1).getUuid());
        assertNotEquals(images.get(1).getUuid(), images.get(2).getUuid());
        assertNotEquals(images.get(0).getUuid(), images.get(2).getUuid());
    }

    // Test Group: Large Dataset Processing

    @Test
    public void testProcessCsv_whenLargeDataset_thenProcessesAllRowsCorrectly() {
        StringBuilder csvData = new StringBuilder(CSV_HEADER);
        for (int i = 0; i < 1000; i++) {
            csvData.append("Title ").append(i).append(",Summary ").append(i)
                    .append(",Description ").append(i).append(",Category ").append(i % 10)
                    .append(",Tag").append(i % 5).append(",/path/").append(i)
                    .append(",image").append(i).append(".png\n");
        }

        ImageResponseHolder responseHolder = assertDoesNotThrow(() ->
                imageService.processCsv(csvData.toString()));

        assertNotNull(responseHolder);
        List<ImageResponse> images = responseHolder.getImages();
        assertEquals(1000, images.size());

        assertEquals("Title 0", images.get(0).getTitle());
        assertEquals("Title 499", images.get(499).getTitle());
        assertEquals("image/png", images.get(999).getMimeType());
        assertEquals("Title 999", images.get(999).getTitle());
        assertEquals("/path/999", images.get(999).getFilePath());
        assertEquals("image999.png", images.get(999).getFileName());
    }
}