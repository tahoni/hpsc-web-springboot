package za.co.hpsc.web.models;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AwardResponseTest {

    private final AwardResponse.AwardPlace first =
            new AwardResponse.AwardPlace(1, "Gold", "gold.png");
    private final AwardResponse.AwardPlace second =
            new AwardResponse.AwardPlace(2, "Silver", "silver.png");
    private final AwardResponse.AwardPlace third =
            new AwardResponse.AwardPlace(3, "Bronze", "bronze.png");

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

        assertEquals(uuid, response.getUuid());
        assertEquals("Title", response.getTitle());
        assertEquals("Summary", response.getSummary());
        assertEquals("Desc", response.getDescription());
        assertEquals("Cat", response.getCategory());
        assertEquals(first, response.getFirstPlace());
        assertEquals(second, response.getSecondPlace());
        assertEquals(third, response.getThirdPlace());
        assertEquals(2, response.getTags().size());
        assertTrue(response.getTags().containsAll(tags));
    }

    @Test
    void testShortConstructor_withUuidAwardPlacesAndTitle_thenInitialisesFieldsCorrectly() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        AwardResponse response = new AwardResponse(uuid, "Mini Title", first, second, third);

        // Assert
        assertEquals(uuid, response.getUuid());
        assertEquals("Mini Title", response.getTitle());
        assertEquals(first, response.getFirstPlace());
        assertEquals(second, response.getSecondPlace());
        assertEquals(third, response.getThirdPlace());
    }

    @Test
    void testConstructor_withoutUuidButWithBasicFields_thenInitialisesAllFields() {
        AwardResponse response = new AwardResponse(
                "Title", "Summary", "Desc", "Cat", List.of("tag"), first, second, third
        );

        assertNotNull(response.getUuid());
        assertEquals("Title", response.getTitle());
        assertEquals("Summary", response.getSummary());
        assertEquals("Desc", response.getDescription());
        assertEquals("Cat", response.getCategory());
        assertEquals(first, response.getFirstPlace());
        assertEquals(second, response.getSecondPlace());
        assertEquals(third, response.getThirdPlace());
        assertEquals(1, response.getTags().size());
        assertTrue(response.getTags().contains("tag"));
    }

    @Test
    void testConstructor_withIndividualStrings_thenInitialisesAllFields() {
        // Act
        AwardResponse response = new AwardResponse(
                "T", "S", "D", "C", List.of("tag"),
                "First Name", "Second Name", "Third Name",
                "img1.jpg", "img2.jpg", "img3.jpg"
        );

        // Assert
        assertEquals("T", response.getTitle());
        assertEquals("S", response.getSummary());
        assertEquals("D", response.getDescription());
        assertEquals("C", response.getCategory());
        assertEquals(1, response.getTags().size());
        assertTrue(response.getTags().contains("tag"));

        assertEquals("First Name", response.getFirstPlace().getName());
        assertEquals("Second Name", response.getSecondPlace().getName());
        assertEquals("Third Name", response.getThirdPlace().getName());

        assertEquals("img1.jpg", response.getFirstPlace().getImageFilePath());
        assertEquals("img2.jpg", response.getSecondPlace().getImageFilePath());
        assertEquals("img3.jpg", response.getThirdPlace().getImageFilePath());

        assertEquals(1, response.getFirstPlace().getPlace());
        assertEquals(2, response.getSecondPlace().getPlace());
        assertEquals(3, response.getThirdPlace().getPlace());
    }

    @Test
    void testConstructor_withAwardRequest_thenInitialisesAllFields() {
        // Arrange
        AwardRequest request = new AwardRequest();
        request.setTitle("Req Title");
        request.setSummary("Req Sum");
        request.setDescription("Req Desc");
        request.setCategory("Req Cat");
        request.setTags(List.of("req-tag"));
        request.setFirstPlaceName("Winner 1");
        request.setSecondPlaceName("Winner 2");
        request.setThirdPlaceName("Winner 3");
        request.setFirstPlaceImageFileName("win1.png");
        request.setSecondPlaceImageFilePath("win2.png");
        request.setThirdPlaceImageFilePath("win3.png");

        // Act
        AwardResponse response = new AwardResponse(request);

        // Assert
        assertNotNull(response.getUuid());
        assertEquals("Req Sum", response.getSummary());
        assertEquals("Req Desc", response.getDescription());
        assertEquals("Req Cat", response.getCategory());
        assertEquals(1, response.getTags().size());
        assertTrue(response.getTags().contains("req-tag"));

        assertEquals("Winner 1", response.getFirstPlace().getName());
        assertEquals("Winner 2", response.getSecondPlace().getName());
        assertEquals("Winner 3", response.getThirdPlace().getName());

        assertEquals("win1.png", response.getFirstPlace().getImageFilePath());
        assertEquals("win2.png", response.getSecondPlace().getImageFilePath());
        assertEquals("win3.png", response.getThirdPlace().getImageFilePath());

        assertEquals(1, response.getFirstPlace().getPlace());
        assertEquals(2, response.getSecondPlace().getPlace());
        assertEquals(3, response.getThirdPlace().getPlace());
    }
}
