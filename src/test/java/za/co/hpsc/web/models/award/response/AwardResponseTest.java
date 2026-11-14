package za.co.hpsc.web.models.award.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.award.request.AwardRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AwardResponseTest {

    // AwardResponse(AwardRequest)
    @Test
    void testConstructor_withAwardRequest_thenInitialisesAllFields() {
        // Arrange
        AwardRequest request = new AwardRequest("Request Title", "Request Ceremony",
                "Winner 1", "Winner 2", "Winner 3");
        request.setSummary("Request Sum");
        request.setDescription("Request Desc");
        request.setCategory("Request Cat");
        request.setTags(List.of("Request-tag"));
        request.setImageFilePath("/path/to/img");
        request.setDate(LocalDate.of(2023, 10, 10));
        request.setCeremonyDescription("Ceremony Desc");
        request.setCeremonySummary("Ceremony Sum");
        request.setCeremonyCategory("Ceremony Cat");
        request.setCeremonyTags(List.of("ceremony-tag"));
        request.setFirstPlaceName("Winner 1");
        request.setSecondPlaceName("Winner 2");
        request.setThirdPlaceName("Winner 3");
        request.setFirstPlaceImageFileName("win1.png");
        request.setSecondPlaceImageFileName("win2.png");
        request.setThirdPlaceImageFileName("win3.png");

        // Act
        AwardResponse response = new AwardResponse(request);

        // Assert meta-data
        assertNotNull(response.getUuid());
        assertEquals("Request Sum", response.getSummary());
        assertEquals("Request Desc", response.getDescription());
        assertEquals("Request Cat", response.getCategory());
        assertEquals(1, response.getTags().size());
        assertTrue(response.getTags().contains("Request-tag"));

        // Assert places (names)
        assertEquals("Winner 1", response.getFirstPlace().getName());
        assertEquals("Winner 2", response.getSecondPlace().getName());
        assertEquals("Winner 3", response.getThirdPlace().getName());
        // Assert places (images)
        assertEquals("win1.png", response.getFirstPlace().getImageFilePath());
        assertEquals("win2.png", response.getSecondPlace().getImageFilePath());
        assertEquals("win3.png", response.getThirdPlace().getImageFilePath());
        // Assert places (place numbers)
        assertEquals(1, response.getFirstPlace().getPlace());
        assertEquals(2, response.getSecondPlace().getPlace());
        assertEquals(3, response.getThirdPlace().getPlace());
    }

    // AwardResponse(String, String, String, String, List<String>, String, String, String, String, String, String)
    @Test
    void testConstructor_withIndividualStrings_thenInitialisesAllFields() {
        // Act
        AwardResponse response = new AwardResponse(
                "T", "S", "D", "C", List.of("tag"),
                "First Name", "Second Name", "Third Name",
                "img1.jpg", "img2.jpg", "img3.jpg"
        );

        // Assert meta-data
        assertEquals("T", response.getTitle());
        assertEquals("S", response.getSummary());
        assertEquals("D", response.getDescription());
        assertEquals("C", response.getCategory());
        assertEquals(1, response.getTags().size());
        assertTrue(response.getTags().contains("tag"));

        // Assert places (names)
        assertEquals("First Name", response.getFirstPlace().getName());
        assertEquals("Second Name", response.getSecondPlace().getName());
        assertEquals("Third Name", response.getThirdPlace().getName());
        // Assert places (images)
        assertEquals("img1.jpg", response.getFirstPlace().getImageFilePath());
        assertEquals("img2.jpg", response.getSecondPlace().getImageFilePath());
        assertEquals("img3.jpg", response.getThirdPlace().getImageFilePath());
        // Assert places (place numbers)
        assertEquals(1, response.getFirstPlace().getPlace());
        assertEquals(2, response.getSecondPlace().getPlace());
        assertEquals(3, response.getThirdPlace().getPlace());
    }

    // AwardResponse(UUID, AwardPlacing, AwardPlacing, AwardPlacing)
    @Test
    void testConstructor_withUuidAndPlacings_thenKeepsTheUuidAndPlacings() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        AwardPlacing first = new AwardPlacing(1, "First", "1.png");
        AwardPlacing second = new AwardPlacing(2, "Second", "2.png");
        AwardPlacing third = new AwardPlacing(3, "Third", "3.png");

        // Act
        AwardResponse response = new AwardResponse(uuid, first, second, third);

        // Assert
        assertEquals(uuid, response.getUuid());
        assertSame(first, response.getFirstPlace());
        assertSame(second, response.getSecondPlace());
        assertSame(third, response.getThirdPlace());
    }

    // AwardResponse(UUID, String, AwardPlacing, AwardPlacing, AwardPlacing)
    @Test
    void testConstructor_withUuidTitleAndPlacings_thenKeepsTheTitle() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        AwardPlacing first = new AwardPlacing(1, "First", "1.png");

        // Act
        AwardResponse response = new AwardResponse(uuid, "Title", first, null, null);

        // Assert
        assertEquals(uuid, response.getUuid());
        assertEquals("Title", response.getTitle());
        assertSame(first, response.getFirstPlace());
        assertNull(response.getSecondPlace());
        assertNull(response.getThirdPlace());
    }

    // AwardResponse(UUID, String, String, String, String, List<String>, AwardPlacing, AwardPlacing, AwardPlacing)
    @Test
    void testConstructor_withUuidAndAllMetadata_thenKeepsTheUuidMetadataAndPlacings() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        AwardPlacing first = new AwardPlacing(1, "First", "1.png");

        // Act
        AwardResponse response = new AwardResponse(uuid, "Title", "Summary", "Description", "Category",
                List.of("tag"), first, null, null);

        // Assert
        assertEquals(uuid, response.getUuid());
        assertEquals("Title", response.getTitle());
        assertEquals("Summary", response.getSummary());
        assertEquals("Description", response.getDescription());
        assertEquals("Category", response.getCategory());
        assertEquals(List.of("tag"), response.getTags());
        assertSame(first, response.getFirstPlace());
    }

    // AwardResponse(String, String, String, String, List<String>, AwardPlacing, AwardPlacing, AwardPlacing)
    @Test
    void testConstructor_withAllMetadataAndNoUuid_thenGeneratesAUuid() {
        // Arrange
        AwardPlacing first = new AwardPlacing(1, "First", "1.png");

        // Act
        AwardResponse response = new AwardResponse("Title", "Summary", "Description", "Category", List.of("tag"),
                first, null, null);

        // Assert
        assertNotNull(response.getUuid());
        assertEquals("Title", response.getTitle());
        assertEquals(List.of("tag"), response.getTags());
        assertSame(first, response.getFirstPlace());
    }
}
