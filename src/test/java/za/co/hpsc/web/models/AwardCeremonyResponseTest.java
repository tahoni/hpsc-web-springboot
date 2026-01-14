package za.co.hpsc.web.models;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.constants.HpscConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// TODO: add all fields
// TODO: test precedence
class AwardCeremonyResponseTest {

    private final String now = DateTimeFormatter.ofPattern(HpscConstants.HPSC_DATE_FORMAT).format(LocalDateTime.now());
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
        AwardCeremonyResponse response = new AwardCeremonyResponse(uuid, now, "path/to/img",
                sampleAwards);

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
                uuid, "Title", "Summary", "Desc", "Cat", tags, now, "img.png",
                sampleAwards
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
        AwardCeremonyResponse response = new AwardCeremonyResponse(uuid, "Short Title", now, "img.png",
                sampleAwards);

        // Assert
        assertEquals(uuid, response.getUuid());
        assertEquals(now, response.getDate());
        assertEquals("Short Title", response.getTitle());
    }

    @Test
    void testConstructorWithoutUuidButWithMetadata_thenInitialisesUuidAndMetadata() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(
                "Title", "Sum", "Desc", "Cat", List.of("T"), now, "img.png",
                sampleAwards
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
    void testConstructor_withValidAwardRequestList_thenInitialisesAllFieldsCorrectly() {
        // Arrange
        AwardRequest request1 = new AwardRequest();
        request1.setCeremonyTitle("Ceremony 2024");
        request1.setCeremonyDescription("Annual Awards");
        request1.setCeremonySummary("Summary 2024");
        request1.setCeremonyCategory("General");
        request1.setCeremonyTags(List.of("Tag1", "Tag2"));
        request1.setDate("2024-01-01");
        request1.setImageFilePath("/path/to/ceremony/image.jpg");

        request1.setTitle("Award 1 Title");
        request1.setFirstPlaceName("Winner A");
        request1.setSecondPlaceName("Winner B");
        request1.setThirdPlaceName("Winner C");

        AwardRequest request2 = new AwardRequest();
        request2.setTitle("Award 2 Title");
        request2.setFirstPlaceName("Winner X");
        request2.setSecondPlaceName("Winner Y");
        request2.setThirdPlaceName("Winner Z");

        List<AwardRequest> requestList = List.of(request1, request2);

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(requestList);

        // Assert
        assertEquals("Ceremony 2024", response.getTitle());
        assertEquals("Annual Awards", response.getDescription());
        assertEquals("Summary 2024", response.getSummary());
        assertEquals("General", response.getCategory());
        assertEquals(List.of("Tag1", "Tag2"), response.getTags());
        assertEquals("2024-01-01", response.getDate());
        assertEquals("/path/to/ceremony/image.jpg", response.getImageFilePath());

        assertNotNull(response.getAwards());
        assertEquals(2, response.getAwards().size());

        assertEquals("Award 1 Title", response.getAwards().get(0).getTitle());
        assertEquals("Winner A", response.getAwards().get(0).getFirstPlace().getName());

        assertEquals("Award 2 Title", response.getAwards().get(1).getTitle());
        assertEquals("Winner X", response.getAwards().get(1).getFirstPlace().getName());
    }

    @Test
    void testConstructor_withValidAwardRequestListPrecedence_thenInitialisesAllFieldsCorrectly() {
        // Arrange
        AwardRequest request1 = new AwardRequest();
        request1.setCeremonyTitle("Ceremony 2024");
        request1.setCeremonyDescription("Annual Awards");
        request1.setCeremonySummary("Summary 2024");
        request1.setCeremonyCategory("General");
        request1.setCeremonyTags(List.of("Tag1", "Tag2"));
        request1.setDate("2024-01-01");
        request1.setImageFilePath("/path/to/ceremony/image.jpg");

        request1.setTitle("Award 1 Title");
        request1.setFirstPlaceName("Winner A");
        request1.setSecondPlaceName("Winner B");
        request1.setThirdPlaceName("Winner C");

        AwardRequest request2 = new AwardRequest();
        request2.setCeremonyTitle("Ceremony 2025");
        request2.setCeremonyDescription("Annual Awards");
        request2.setCeremonySummary("Summary 2025");
        request2.setCeremonyCategory("General");
        request2.setCeremonyTags(List.of("Tag1", "Tag2"));
        request2.setDate("2025-01-01");
        request2.setImageFilePath("/path/to/ceremony/image.jpg");

        request2.setTitle("Award 2 Title");
        request2.setFirstPlaceName("Winner X");
        request2.setSecondPlaceName("Winner Y");
        request2.setThirdPlaceName("Winner Z");

        List<AwardRequest> requestList = List.of(request1, request2);

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(requestList);

        // Assert
        assertEquals("Ceremony 2024", response.getTitle());
        assertEquals("Annual Awards", response.getDescription());
        assertEquals("Summary 2024", response.getSummary());
        assertEquals("General", response.getCategory());
        assertEquals(List.of("Tag1", "Tag2"), response.getTags());
        assertEquals("2024-01-01", response.getDate());
        assertEquals("/path/to/ceremony/image.jpg", response.getImageFilePath());

        assertNotNull(response.getAwards());
        assertEquals(2, response.getAwards().size());

        assertEquals("Award 1 Title", response.getAwards().get(0).getTitle());
        assertEquals("Winner A", response.getAwards().get(0).getFirstPlace().getName());

        assertEquals("Award 2 Title", response.getAwards().get(1).getTitle());
        assertEquals("Winner X", response.getAwards().get(1).getFirstPlace().getName());
    }

    @Test
    void testConstructor_withEmptyAwardRequestList_thenInitialisesEmptyFields() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(new ArrayList<>());

        // Assert
        assertNotNull(response.getUuid());
        assertNull(response.getTitle());
        assertNull(response.getDate());
        assertEquals("", response.getImageFilePath());
        assertTrue(response.getAwards().isEmpty());
    }

    @Test
    void testConstructor_withNullAwardRequestList_thenInitialisesEmptyFields() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(null);

        // Assert
        assertNotNull(response.getUuid());
        assertNull(response.getTitle());
        assertNull(response.getDate());
        assertEquals("", response.getImageFilePath());
        assertTrue(response.getAwards().isEmpty());
    }
}
