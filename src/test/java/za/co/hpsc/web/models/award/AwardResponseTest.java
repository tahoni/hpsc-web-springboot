package za.co.hpsc.web.models.award;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AwardResponseTest {

    private final AwardPlacing first =
            new AwardPlacing(1, "Gold", "gold.png");
    private final AwardPlacing second =
            new AwardPlacing(2, "Silver", "silver.png");
    private final AwardPlacing third =
            new AwardPlacing(3, "Bronze", "bronze.png");

    @Test
    void testConstructor_withAwardPlaces_thenInitialisesUuidAndAwardPlaces() {
        // Act
        AwardResponse response = new AwardResponse(first, second, third);

        // Assert
        assertNotNull(response.getUuid());
        assertEquals(first, response.getFirstPlace());
        assertEquals(second, response.getSecondPlace());
        assertEquals(third, response.getThirdPlace());
    }

    @Test
    void testConstructor_withUuidAndAwardPlaces_thenInitialisesUuidAndAwardPlaces() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        AwardResponse response = new AwardResponse(uuid, first, second, third);

        // Assert
        assertEquals(uuid, response.getUuid());
        assertEquals(first, response.getFirstPlace());
        assertEquals(second, response.getSecondPlace());
        assertEquals(third, response.getThirdPlace());
    }

    @Test
    void testFullConstructor_thenInitialisesAllFields() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        List<String> tags = List.of("sports", "annual");

        // Act
        AwardResponse response = new AwardResponse(
                uuid, "Title", "Summary", "Desc", "Cat", tags, first, second, third
        );

        // Assert meta-data
        assertEquals(uuid, response.getUuid());
        assertEquals("Title", response.getTitle());
        assertEquals("Summary", response.getSummary());
        assertEquals("Desc", response.getDescription());
        assertEquals("Cat", response.getCategory());
        assertEquals(2, response.getTags().size());
        assertTrue(response.getTags().containsAll(tags));
        // Assert places
        assertEquals(first, response.getFirstPlace());
        assertEquals(second, response.getSecondPlace());
        assertEquals(third, response.getThirdPlace());
    }

    @Test
    void testShortConstructor_withUuidAwardPlacesAndTitle_thenInitialisesFieldsCorrectly() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        AwardResponse response = new AwardResponse(uuid, "Mini Title", first, second, third);

        // Assert meta-data
        assertEquals(uuid, response.getUuid());
        assertEquals("Mini Title", response.getTitle());
        // Assert places
        assertEquals(first, response.getFirstPlace());
        assertEquals(second, response.getSecondPlace());
        assertEquals(third, response.getThirdPlace());
    }

    @Test
    void testConstructor_withoutUuidButWithBasicFields_thenInitialisesAllFields() {
        // Act
        AwardResponse response = new AwardResponse(
                "Title", "Summary", "Desc", "Cat", List.of("tag"), first, second, third
        );

        // Assert meta-data
        assertNotNull(response.getUuid());
        assertEquals("Title", response.getTitle());
        assertEquals("Summary", response.getSummary());
        assertEquals("Desc", response.getDescription());
        assertEquals("Cat", response.getCategory());
        assertEquals(1, response.getTags().size());
        assertTrue(response.getTags().contains("tag"));
        // Assert places
        assertEquals(first, response.getFirstPlace());
        assertEquals(second, response.getSecondPlace());
        assertEquals(third, response.getThirdPlace());
    }

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

    @Test
    void testConstructor_withAwardRequest_thenInitialisesAllFields() {
        // Arrange
        AwardRequest request = new AwardRequest("Request Title", "Request Ceremony", "Winner 1", "Winner 2", "Winner 3");
        request.setSummary("Request Sum");
        request.setDescription("Request Desc");
        request.setCategory("Request Cat");
        request.setTags(List.of("Request-tag"));
        request.setImageFilePath("/path/to/img");
        request.setDate("2023-10-10");
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
}
