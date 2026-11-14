package za.co.hpsc.web.models.award.response;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.models.award.request.AwardRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AwardCeremonyResponseTest {

    private final LocalDate now = LocalDate.of(2026, 1, 15);
    private final List<AwardResponse> sampleAwards = List.of(
            new AwardResponse(new AwardPlacing(1, "A", "a.png"),
                    new AwardPlacing(2, "B", "b.png"),
                    new AwardPlacing(3, "C", "c.png"))
    );

    // AwardCeremonyResponse()
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

    // AwardCeremonyResponse(List<AwardRequest>)
    @Test
    void testConstructor_withValidAwardRequestList_thenInitialisesAllFieldsCorrectly() {
        // Arrange
        AwardRequest request1 = createAwardRequest("Award 1", "Winner A", "img", "/path/to/img",
                LocalDate.of(2024, 1, 1));
        request1.setCeremonyDescription("Annual Awards");
        request1.setCeremonySummary("Summary 2024");
        request1.setCeremonyCategory("General");
        request1.setCeremonyTags(List.of("Tag1", "Tag2"));
        request1.setDescription("Ceremony 2024 Award 1");
        request1.setSummary("Summary 2024 Award 1");
        request1.setCategory("Category 1");
        request1.setTags(List.of("Tag3", "Tag4"));

        AwardRequest request2 = createAwardRequest("Award 2", "Winner X", "imgx", null,
                null);
        request2.setDescription("Ceremony 2024 Award 2");
        request2.setSummary("Summary 2024 Award 2");
        request2.setCategory("Category 2");
        request2.setTags(List.of("Tag5", "Tag6"));

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(List.of(request1, request2));

        // Assert ceremony data (taken from the first request)
        assertNotNull(response.getUuid());
        assertEquals(LocalDate.of(2024, 1, 1), response.getDate());
        assertEquals("/path/to/img", response.getImageFilePath());
        assertEquals("Ceremony 2024", response.getTitle());
        assertEquals("Annual Awards", response.getDescription());
        assertEquals("Summary 2024", response.getSummary());
        assertEquals("General", response.getCategory());
        assertEquals(List.of("Tag1", "Tag2"), response.getTags());

        // Assert awards
        assertEquals(2, response.getAwards().size());
        AwardResponse firstAward = response.getAwards().getFirst();
        assertEquals("Award 1", firstAward.getTitle());
        assertEquals("Ceremony 2024 Award 1", firstAward.getDescription());
        assertEquals("Summary 2024 Award 1", firstAward.getSummary());
        assertEquals("Category 1", firstAward.getCategory());
        assertEquals(List.of("Tag3", "Tag4"), firstAward.getTags());
        assertEquals("Winner A", firstAward.getFirstPlace().getName());
        assertEquals("imga.png", firstAward.getFirstPlace().getImageFilePath());
        assertEquals(1, firstAward.getFirstPlace().getPlace());
        assertEquals(2, firstAward.getSecondPlace().getPlace());
        assertEquals(3, firstAward.getThirdPlace().getPlace());

        AwardResponse secondAward = response.getAwards().get(1);
        assertEquals("Award 2", secondAward.getTitle());
        assertEquals("Ceremony 2024 Award 2", secondAward.getDescription());
        assertEquals(List.of("Tag5", "Tag6"), secondAward.getTags());
        assertEquals("Winner X", secondAward.getFirstPlace().getName());
        assertEquals("imgxa.png", secondAward.getFirstPlace().getImageFilePath());
    }

    @Test
    void testConstructor_withValidAwardRequestListPrecedence_thenInitialisesAllFieldsCorrectly() {
        // Arrange
        AwardRequest request1 = createAwardRequest("Award 1 Title", "Winner A", "img", "/path/to/img1",
                LocalDate.of(2024, 1, 1));
        request1.setCeremonyDescription("Annual Awards");
        request1.setCeremonySummary("Summary 2024");
        request1.setCeremonyCategory("General");
        request1.setCeremonyTags(List.of("Tag1", "Tag2"));

        AwardRequest request2 = createAwardRequest("Award 2 Title", "Winner X", "imgx", "/path/to/img2",
                LocalDate.of(2025, 1, 1));
        request2.setCeremonyTitle("Ceremony 2025");
        request2.setCeremonyDescription("Other Awards");

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(List.of(request1, request2));

        // Assert ceremony data comes from the first request, not the second
        assertEquals(LocalDate.of(2024, 1, 1), response.getDate());
        assertEquals("/path/to/img1", response.getImageFilePath());
        assertEquals("Ceremony 2024", response.getTitle());
        assertEquals("Annual Awards", response.getDescription());
        assertEquals("Summary 2024", response.getSummary());
        assertEquals("General", response.getCategory());
        assertEquals(List.of("Tag1", "Tag2"), response.getTags());

        // Assert each award is still built from its own request
        assertEquals(2, response.getAwards().size());
        assertEquals("Award 1 Title", response.getAwards().getFirst().getTitle());
        assertEquals("Winner A", response.getAwards().getFirst().getFirstPlace().getName());
        assertEquals("Award 2 Title", response.getAwards().get(1).getTitle());
        assertEquals("Winner X", response.getAwards().get(1).getFirstPlace().getName());
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

    // AwardCeremonyResponse(LocalDate, String, List<AwardResponse>)
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

    // AwardCeremonyResponse(UUID, String, String, String, String, List<String>, LocalDate, String, List<AwardResponse>)
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

    // Helpers
    private static AwardRequest createAwardRequest(String title, String winner, String imagePrefix,
                                                   String imageFilePath, LocalDate date) {
        AwardRequest request = new AwardRequest(title, "Ceremony 2024", winner, winner + " 2", winner + " 3");
        request.setDate(date);
        request.setImageFilePath(imageFilePath);
        request.setFirstPlaceImageFileName(imagePrefix + "a.png");
        request.setSecondPlaceImageFileName(imagePrefix + "b.png");
        request.setThirdPlaceImageFileName(imagePrefix + "c.png");
        return request;
    }

    // AwardCeremonyResponse(UUID, LocalDate, String, List<AwardResponse>)
    @Test
    void testConstructor_withUuidDateImageAndAwards_thenKeepsThemAndDefaultsNulls() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(uuid, now, "/images", sampleAwards);
        AwardCeremonyResponse defaulted = new AwardCeremonyResponse(uuid, now, null, null);

        // Assert
        assertEquals(uuid, response.getUuid());
        assertEquals(now, response.getDate());
        assertEquals("/images", response.getImageFilePath());
        assertEquals(sampleAwards, response.getAwards());
        assertEquals("", defaulted.getImageFilePath());
        assertTrue(defaulted.getAwards().isEmpty());
    }

    // AwardCeremonyResponse(UUID, String, LocalDate, String, List<AwardResponse>)
    @Test
    void testConstructor_withUuidTitleDateImageAndAwards_thenKeepsTheTitleAndDefaultsNulls() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse(uuid, "Ceremony", now, "/images", sampleAwards);
        AwardCeremonyResponse defaulted = new AwardCeremonyResponse(uuid, "Ceremony", now, null, null);

        // Assert
        assertEquals(uuid, response.getUuid());
        assertEquals("Ceremony", response.getTitle());
        assertEquals("/images", response.getImageFilePath());
        assertEquals(sampleAwards, response.getAwards());
        assertEquals("", defaulted.getImageFilePath());
        assertTrue(defaulted.getAwards().isEmpty());
    }

    // AwardCeremonyResponse(String, String, String, String, List<String>, LocalDate, String, List<AwardResponse>)
    @Test
    void testConstructor_withAllMetadataAndNoUuid_thenGeneratesAUuidAndDefaultsNulls() {
        // Act
        AwardCeremonyResponse response = new AwardCeremonyResponse("Ceremony", "Summary", "Description", "Category",
                List.of("tag"), now, "/images", sampleAwards);
        AwardCeremonyResponse defaulted = new AwardCeremonyResponse("Ceremony", null, null, null, null, now, null, null);

        // Assert
        assertNotNull(response.getUuid());
        assertEquals("Ceremony", response.getTitle());
        assertEquals("Summary", response.getSummary());
        assertEquals(List.of("tag"), response.getTags());
        assertEquals(now, response.getDate());
        assertEquals(sampleAwards, response.getAwards());
        assertEquals("", defaulted.getImageFilePath());
        assertTrue(defaulted.getAwards().isEmpty());
    }
}
