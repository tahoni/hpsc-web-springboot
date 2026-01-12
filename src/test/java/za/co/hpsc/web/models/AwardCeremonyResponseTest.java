package za.co.hpsc.web.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AwardCeremonyResponseTest {

    private final LocalDateTime now = LocalDateTime.now();
    private final List<AwardResponse> sampleAwards = List.of(
            new AwardResponse(new AwardResponse.AwardPlace(1, "A", "a.png"),
                    new AwardResponse.AwardPlace(2, "B", "b.png"),
                    new AwardResponse.AwardPlace(3, "C", "c.png"))
    );

    @Test
    void testDefaultConstructor_thenInitialisesFieldsCorrectly() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse();

        // Assert
        assertNotNull(response.getUuid());
        assertNull(response.getDate());
        assertTrue(response.getImageFilePath().isEmpty());
        assertTrue(response.getAwards().isEmpty());
    }

    @Test
    void testConstructor_withBasicFields_thenInitialisesFieldsCorrectly() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(now, null, null);

        // Assert
        assertNotNull(response.getUuid());
        assertEquals(now, response.getDate());
        assertTrue(response.getImageFilePath().isEmpty());
        assertTrue(response.getAwards().isEmpty());
    }

    @Test
    void testConstructor_withUuidAndBasicFields_thenInitialisesFieldsCorrectly() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(uuid, now, "path/to/img", sampleAwards);

        // Assert
        assertEquals(uuid, response.getUuid());
        assertEquals(now, response.getDate());
        assertEquals("path/to/img", response.getImageFilePath());
        assertEquals(1, response.getAwards().size());
        assertTrue(response.getAwards().containsAll(sampleAwards));
    }

    @Test
    void testFullConstructor_thenInitialisesAllFields() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        List<String> tags = List.of("tag1", "tag2");

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(
                uuid, "Title", "Summary", "Desc", "Cat", tags, now, "img.png", sampleAwards
        );

        // Assert
        assertEquals(uuid, response.getUuid());
        assertEquals(now, response.getDate());
        assertEquals("Title", response.getTitle());
        assertEquals("Summary", response.getSummary());
        assertEquals("Desc", response.getDescription());
        assertEquals("Cat", response.getCategory());
        assertEquals("img.png", response.getImageFilePath());
        assertEquals(1, response.getAwards().size());
        assertTrue(response.getAwards().containsAll(sampleAwards));
        assertEquals(2, response.getTags().size());
        assertTrue(response.getTags().containsAll(tags));
    }

    @Test
    void testConstructor_withUuidTitleAndDetails_thenInitialisesFieldsCorrectly() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(uuid, "Short Title", now, "img.png", sampleAwards);

        // Assert
        assertEquals(uuid, response.getUuid());
        assertEquals(now, response.getDate());
        assertEquals("Short Title", response.getTitle());
    }

    @Test
    void testConstructorWithoutUuidButWithMetadata_thenInitialisesUuidAndMetadata() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(
                "Title", "Sum", "Desc", "Cat", List.of("T"), now, "img.png", sampleAwards
        );

        assertNotNull(response.getUuid());
        assertEquals(now, response.getDate());
        assertEquals("Title", response.getTitle());
        assertEquals("Sum", response.getSummary());
        assertEquals("Desc", response.getDescription());
        assertEquals("Cat", response.getCategory());
        assertEquals("img.png", response.getImageFilePath());
        assertEquals(1, response.getTags().size());
        assertTrue(response.getTags().contains("T"));

        assertEquals(1, response.getAwards().size());
        assertTrue(response.getAwards().containsAll(sampleAwards));
    }

    @Test
    void testConstructor_withAwardRequestList_thenInitialisesAllFields() {
        // Arrange
        AwardRequest req1 = new AwardRequest();
        req1.setDate(now);
        req1.setImageFilePath("ceremony.png");
        req1.setTitle("Award 1");
        req1.setFirstPlaceName("Winner 1");
        req1.setSecondPlaceName("Winner 2");
        req1.setThirdPlaceName("Winner 3");

        AwardRequest req2 = new AwardRequest();
        req2.setTitle("Award 2");
        req2.setFirstPlaceName("Winner A");
        req2.setSecondPlaceName("Winner B");
        req2.setThirdPlaceName("Winner C");

        List<AwardRequest> requests = List.of(req1, req2);

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(requests);

        assertNotNull(response.getUuid());
        assertEquals(now, response.getDate());
        assertEquals("ceremony.png", response.getImageFilePath());
        assertEquals(2, response.getAwards().size());

        assertEquals("Award 1", response.getAwards().get(0).getTitle());
        assertEquals("Award 2", response.getAwards().get(1).getTitle());

        assertEquals("Winner 1", response.getAwards().get(0).getFirstPlace().getName());
        assertEquals("Winner 2", response.getAwards().get(0).getSecondPlace().getName());
        assertEquals("Winner 3", response.getAwards().get(0).getThirdPlace().getName());
        assertEquals("Winner A", response.getAwards().get(1).getFirstPlace().getName());
        assertEquals("Winner B", response.getAwards().get(1).getSecondPlace().getName());
        assertEquals("Winner C", response.getAwards().get(1).getThirdPlace().getName());
    }

    @Test
    void testConstructor_withEmptyAwardRequestList_thenInitialisesEmptyFields() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(new ArrayList<>());

        // Assert
        assertNotNull(response.getUuid());
        assertNull(response.getDate());
        assertTrue(response.getAwards().isEmpty());
    }

    @Test
    void testConstructor_withNullAwardRequestList_thenInitialisesEmptyFields() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(null);

        // Assert
        assertNotNull(response.getUuid());
        assertNull(response.getDate());
        assertTrue(response.getAwards().isEmpty());
    }
}
