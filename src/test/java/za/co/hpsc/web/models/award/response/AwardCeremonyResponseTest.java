package za.co.hpsc.web.models.award.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.award.request.AwardRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AwardCeremonyResponseTest {

    private final LocalDate now = LocalDate.now();
    private final List<AwardResponse> sampleAwards = List.of(
            new AwardResponse(new AwardPlacing(1, "A", "a.png"),
                    new AwardPlacing(2, "B", "b.png"),
                    new AwardPlacing(3, "C", "c.png"))
    );

    @Test
    void testDefaultConstructor_thenInitialisesFieldsCorrectly() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse();

        // Assert basic data
        assertNotNull(response.getUuid());
        assertNull(response.getDate());
        assertTrue(response.getImageFilePath().isEmpty());
        // Assert meta-data
        assertNull(response.getTitle());
        // Assert awards
        assertTrue(response.getAwards().isEmpty());
    }

    @Test
    void testConstructor_withBasicFields_thenInitialisesFieldsCorrectly() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(now, null, null);

        // Assert basic data
        assertNotNull(response.getUuid());
        assertEquals(now, response.getDate());
        assertTrue(response.getImageFilePath().isEmpty());
        // Assert meta-data
        assertNull(response.getTitle());
        // Assert awards
        assertTrue(response.getAwards().isEmpty());
    }

    @Test
    void testConstructor_withUuidAndBasicFields_thenInitialisesFieldsCorrectly() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(uuid, now, "/path/to/img",
                sampleAwards);

        // Assert basic data
        assertEquals(uuid, response.getUuid());
        assertEquals(now, response.getDate());
        assertEquals("/path/to/img", response.getImageFilePath());
        // Assert meta-data
        assertNull(response.getTitle());
        // Assert awards
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
                uuid, "Title", "Summary", "Desc", "Cat", tags, now, "/path/to/img",
                sampleAwards
        );

        // Assert basic data
        assertEquals(uuid, response.getUuid());
        assertEquals(now, response.getDate());
        assertEquals("/path/to/img", response.getImageFilePath());
        // Assert meta-data
        assertEquals("Title", response.getTitle());
        assertEquals("Summary", response.getSummary());
        assertEquals("Desc", response.getDescription());
        assertEquals("Cat", response.getCategory());
        assertEquals(2, response.getTags().size());
        assertTrue(response.getTags().containsAll(tags));

        // Assert awards
        assertEquals(1, response.getAwards().size());
        assertTrue(response.getAwards().containsAll(sampleAwards));
    }

    @Test
    void testConstructor_withUuidTitleAndDetails_thenInitialisesFieldsCorrectly() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(uuid, "Short Title", now, "/path/to/img",
                sampleAwards);

        // Assert basic data
        assertEquals(uuid, response.getUuid());
        assertEquals(now, response.getDate());
        assertEquals("/path/to/img", response.getImageFilePath());
        // Assert meta-data
        assertEquals("Short Title", response.getTitle());

        // Assert awards
        assertEquals(1, response.getAwards().size());
        assertTrue(response.getAwards().containsAll(sampleAwards));
    }

    @Test
    void testConstructorWithoutUuidButWithMetadata_thenInitialisesUuidAndMetadata() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(
                "Title", "Sum", "Desc", "Cat", List.of("T"), now, "/path/to/img", sampleAwards
        );

        // Assert basic data
        assertNotNull(response.getUuid());
        assertEquals(now, response.getDate());
        assertEquals("/path/to/img", response.getImageFilePath());
        // Assert meta-data
        assertEquals("Title", response.getTitle());
        assertEquals("Sum", response.getSummary());
        assertEquals("Desc", response.getDescription());
        assertEquals("Cat", response.getCategory());
        assertEquals(1, response.getTags().size());
        assertTrue(response.getTags().contains("T"));

        // Assert awards
        assertEquals(1, response.getAwards().size());
        assertTrue(response.getAwards().containsAll(sampleAwards));
    }

    @Test
    void testConstructor_withValidAwardRequestList_thenInitialisesAllFieldsCorrectly() {
        // Arrange first award request
        AwardRequest request = new AwardRequest("Award 1", "Ceremony 2024", "Winner A", "Winner B", "Winner C");
        request.setCeremonyDescription("Annual Awards");
        request.setCeremonySummary("Summary 2024");
        request.setCeremonyCategory("General");
        request.setCeremonyTags(List.of("Tag1", "Tag2"));
        request.setDate(LocalDate.of(2024, 1, 1));
        request.setImageFilePath("/path/to/img");
        request.setDescription("Ceremony 2024 Award 1");
        request.setSummary("Summary 2024 Award 1");
        request.setCategory("Category 1");
        request.setTags(List.of("Tag3", "Tag4"));
        request.setFirstPlaceImageFileName("imga.png");
        request.setSecondPlaceImageFileName("imgb.png");
        request.setThirdPlaceImageFileName("imgc.png");
        AwardRequest request1 = request;

        // Arrange second award request
        AwardRequest request2 = new AwardRequest("Award 2", "Ceremony 2025",
                "Winner X", "Winner Y", "Winner Z");
        request2.setDescription("Ceremony 2024 Award 2");
        request2.setSummary("Summary 2024 Award 2");
        request2.setCategory("Category 2");
        request2.setTags(List.of("Tag5", "Tag6"));
        request2.setFirstPlaceImageFileName("imgx.png");
        request2.setSecondPlaceImageFileName("imgy.png");
        request2.setThirdPlaceImageFileName("imgz.png");

        // Arrange all requests in a list
        List<AwardRequest> requestList = List.of(request1, request2);

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(requestList);

        // Assert basic data
        assertNotNull(response.getUuid());
        assertEquals(LocalDate.of(2024, 1, 1), response.getDate());
        assertEquals("/path/to/img", response.getImageFilePath());
        // Assert meta-data
        assertEquals("Ceremony 2024", response.getTitle());
        assertEquals("Annual Awards", response.getDescription());
        assertEquals("Summary 2024", response.getSummary());
        assertEquals("General", response.getCategory());
        assertEquals(List.of("Tag1", "Tag2"), response.getTags());

        // Assert awards
        assertNotNull(response.getAwards());
        assertEquals(2, response.getAwards().size());
        List<AwardResponse> awards = response.getAwards();

        // Assert first award
        AwardResponse firstAward = awards.getFirst();
        assertEquals("Award 1", firstAward.getTitle());
        assertEquals("Ceremony 2024 Award 1", firstAward.getDescription());
        assertEquals("Summary 2024 Award 1", firstAward.getSummary());
        assertEquals("Category 1", firstAward.getCategory());
        assertEquals(List.of("Tag3", "Tag4"), firstAward.getTags());
        // Assert first award places
        assertEquals("Winner A", firstAward.getFirstPlace().getName());
        assertEquals("Winner B", firstAward.getSecondPlace().getName());
        assertEquals("Winner C", firstAward.getThirdPlace().getName());
        assertEquals("imga.png", firstAward.getFirstPlace().getImageFilePath());
        assertEquals("imgb.png", firstAward.getSecondPlace().getImageFilePath());
        assertEquals("imgc.png", firstAward.getThirdPlace().getImageFilePath());
        assertEquals(1, firstAward.getFirstPlace().getPlace());
        assertEquals(2, firstAward.getSecondPlace().getPlace());
        assertEquals(3, firstAward.getThirdPlace().getPlace());

        // Assert second award
        AwardResponse secondAward = awards.get(1);
        assertEquals("Award 2", secondAward.getTitle());
        assertEquals("Ceremony 2024 Award 2", secondAward.getDescription());
        assertEquals("Summary 2024 Award 2", secondAward.getSummary());
        assertEquals("Category 2", secondAward.getCategory());
        assertEquals(List.of("Tag5", "Tag6"), secondAward.getTags());
        // Assert second award places
        assertEquals("Winner X", secondAward.getFirstPlace().getName());
        assertEquals("Winner Y", secondAward.getSecondPlace().getName());
        assertEquals("Winner Z", secondAward.getThirdPlace().getName());
        assertEquals("imgx.png", secondAward.getFirstPlace().getImageFilePath());
        assertEquals("imgy.png", secondAward.getSecondPlace().getImageFilePath());
        assertEquals("imgz.png", secondAward.getThirdPlace().getImageFilePath());
        assertEquals(1, secondAward.getFirstPlace().getPlace());
        assertEquals(2, secondAward.getSecondPlace().getPlace());
        assertEquals(3, secondAward.getThirdPlace().getPlace());
    }

    @Test
    void testConstructor_withValidAwardRequestListPrecedence_thenInitialisesAllFieldsCorrectly() {
        // Arrange first award request
        AwardRequest request3 = new AwardRequest("Award 1 Title", "Ceremony 2024", "Winner A", "Winner B", "Winner C");
        request3.setCeremonyDescription("Annual Awards");
        request3.setCeremonySummary("Summary 2024");
        request3.setCeremonyCategory("General");
        request3.setCeremonyTags(List.of("Tag1", "Tag2"));
        request3.setDate(LocalDate.of(2024, 1, 1));
        request3.setImageFilePath("/path/to/img1");
        request3.setDescription(null);
        request3.setSummary(null);
        request3.setCategory(null);
        request3.setTags(null);
        request3.setFirstPlaceImageFileName("imga.png");
        request3.setSecondPlaceImageFileName("imgb.png");
        request3.setThirdPlaceImageFileName("imgc.png");
        AwardRequest request1 = request3;

        // Arrange second award request
        AwardRequest request = new AwardRequest("Award 2 Title", "Ceremony 2025", "Winner X", "Winner Y", "Winner Z");
        request.setCeremonyDescription("Annual Awards");
        request.setCeremonySummary("Summary 2025");
        request.setCeremonyCategory("General");
        request.setCeremonyTags(List.of("Tag1", "Tag2"));
        request.setDate(LocalDate.of(2025, 1, 1));
        request.setImageFilePath("/path/to/img2");
        request.setDescription(null);
        request.setSummary(null);
        request.setCategory(null);
        request.setTags(null);
        request.setFirstPlaceImageFileName("imgx.png");
        request.setSecondPlaceImageFileName("imgy.png");
        request.setThirdPlaceImageFileName("imgz.png");
        AwardRequest request2 = request;

        // Arrange all requests in a list
        List<AwardRequest> requestList = List.of(request1, request2);

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(requestList);

        // Assert basic data
        assertNotNull(response.getUuid());
        assertEquals(LocalDate.of(2024, 1, 1), response.getDate());
        assertEquals("/path/to/img1", response.getImageFilePath());
        // Assert meta-data
        assertEquals("Ceremony 2024", response.getTitle());
        assertEquals("Annual Awards", response.getDescription());
        assertEquals("Summary 2024", response.getSummary());
        assertEquals("General", response.getCategory());
        assertEquals(List.of("Tag1", "Tag2"), response.getTags());

        // Assert awards
        assertNotNull(response.getAwards());
        assertEquals(2, response.getAwards().size());
        List<AwardResponse> awards = response.getAwards();

        // Assert first award
        AwardResponse firstAward = awards.getFirst();
        assertEquals("Award 1 Title", firstAward.getTitle());
        assertEquals("Winner A", firstAward.getFirstPlace().getName());
        assertEquals("Winner B", firstAward.getSecondPlace().getName());
        assertEquals("Winner C", firstAward.getThirdPlace().getName());
        assertEquals("imga.png", firstAward.getFirstPlace().getImageFilePath());
        assertEquals("imgb.png", firstAward.getSecondPlace().getImageFilePath());
        assertEquals("imgc.png", firstAward.getThirdPlace().getImageFilePath());
        assertEquals(1, firstAward.getFirstPlace().getPlace());
        assertEquals(2, firstAward.getSecondPlace().getPlace());
        assertEquals(3, firstAward.getThirdPlace().getPlace());

        // Assert second award
        AwardResponse secondAward = awards.get(1);
        assertEquals("Award 2 Title", secondAward.getTitle());
        assertEquals("Winner X", secondAward.getFirstPlace().getName());
        assertEquals("imgx.png", secondAward.getFirstPlace().getImageFilePath());
        assertEquals("imgy.png", secondAward.getSecondPlace().getImageFilePath());
        assertEquals("imgz.png", secondAward.getThirdPlace().getImageFilePath());
        assertEquals(1, secondAward.getFirstPlace().getPlace());
        assertEquals(2, secondAward.getSecondPlace().getPlace());
        assertEquals(3, secondAward.getThirdPlace().getPlace());
    }

    @Test
    void testConstructor_withEmptyAwardRequestList_thenInitialisesEmptyFields() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(new ArrayList<>());

        // Assert basic data
        assertNotNull(response.getUuid());
        assertNull(response.getDate());
        assertEquals("", response.getImageFilePath());
        // Assert meta-data
        assertNull(response.getTitle());
        // Assert awards
        assertTrue(response.getAwards().isEmpty());
    }

    @Test
    void testConstructor_withNullAwardRequestList_thenInitialisesEmptyFields() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(null);

        // Assert
        assertNotNull(response.getUuid());
        assertNull(response.getDate());
        assertEquals("", response.getImageFilePath());
        // Assert meta-data
        assertNull(response.getTitle());
        // Assert awards
        assertTrue(response.getAwards().isEmpty());
    }
}
