package za.co.hpsc.web.models.award.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AwardRequestForCSVTest {
    private static class MinimalAwardRequestForCSV extends AwardRequestForCSV {
        @JsonCreator
        MinimalAwardRequestForCSV(@JsonProperty(value = "title", required = true) String title,
                                  @JsonProperty(value = "ceremonyTitle", required = true) String ceremonyTitle,
                                  @JsonProperty(value = "firstNamePlace", required = true) String firstPlaceName) {
            super(title, ceremonyTitle, firstPlaceName);
        }
    }

    private static class TestAwardRequestForCSV extends AwardRequestForCSV {
        @JsonCreator
        TestAwardRequestForCSV(@JsonProperty(value = "title", required = true) String title,
                               @JsonProperty(value = "ceremonyTitle", required = true) String ceremonyTitle,
                               @JsonProperty(value = "firstPlaceName", required = true) String firstPlaceName,
                               @JsonProperty(value = "secondPlaceName") String secondPlaceName,
                               @JsonProperty(value = "thirdPlaceName") String thirdPlaceName) {
            super(title, ceremonyTitle, firstPlaceName, secondPlaceName, thirdPlaceName);
        }
    }

    @Test
    void testConstructor_whenRequiredFieldsProvided_thenMapsCoreFieldsAndInitializesTagLists() {
        // Arrange & Act
        TestAwardRequestForCSV request =
                new TestAwardRequestForCSV("Top Shooter", "Annual Awards", "Jane Doe", "John Roe", "Sam Poe");

        // Assert
        assertEquals("Top Shooter", request.getTitle());
        assertEquals("Annual Awards", request.getCeremonyTitle());
        assertEquals("Jane Doe", request.getFirstPlaceName());
        assertEquals("John Roe", request.getSecondPlaceName());
        assertEquals("Sam Poe", request.getThirdPlaceName());
        assertNotNull(request.getTags());
        assertNotNull(request.getCeremonyTags());
        assertTrue(request.getTags().isEmpty());
        assertTrue(request.getCeremonyTags().isEmpty());
    }

    @Test
    void testConstructor_whenOptionalPlaceNamesAreNull_thenKeepsOptionalNamesNull() {
        // Arrange & Act
        TestAwardRequestForCSV request =
                new TestAwardRequestForCSV("Top Shooter", "Annual Awards", "Jane Doe", null, null);

        // Assert
        assertEquals("Top Shooter", request.getTitle());
        assertEquals("Annual Awards", request.getCeremonyTitle());
        assertEquals("Jane Doe", request.getFirstPlaceName());
        assertNull(request.getSecondPlaceName());
        assertNull(request.getThirdPlaceName());
    }

    @Test
    void testTags_whenMutated_thenPersistsAddedValues() {
        // Arrange
        TestAwardRequestForCSV request =
                new TestAwardRequestForCSV("Top Shooter", "Annual Awards", "Jane Doe", "John Roe", "Sam Poe");

        // Act
        request.getTags().add("ipsc");
        request.getCeremonyTags().add("hpsc");

        // Assert
        assertEquals(1, request.getTags().size());
        assertEquals("ipsc", request.getTags().getFirst());
        assertEquals(1, request.getCeremonyTags().size());
        assertEquals("hpsc", request.getCeremonyTags().getFirst());
    }

    @Test
    void testSetters_whenMetadataProvided_thenUpdatesAllOptionalFields() {
        // Arrange
        TestAwardRequestForCSV request =
                new TestAwardRequestForCSV("Top Shooter", "Annual Awards", "Jane Doe", "John Roe", "Sam Poe");
        LocalDate date = LocalDate.of(2026, 4, 24);

        // Act
        request.setSummary("Award summary");
        request.setDescription("Award description");
        request.setCategory("Overall");
        request.setCeremonySummary("Ceremony summary");
        request.setCeremonyDescription("Ceremony description");
        request.setCeremonyCategory("Main event");
        request.setDate(date);
        request.setImageFilePath("awards/2026/top-shooter.png");
        request.setFirstPlaceImageFileName("first.png");
        request.setSecondPlaceImageFileName("second.png");
        request.setThirdPlaceImageFileName("third.png");

        // Assert
        assertEquals("Award summary", request.getSummary());
        assertEquals("Award description", request.getDescription());
        assertEquals("Overall", request.getCategory());
        assertEquals("Ceremony summary", request.getCeremonySummary());
        assertEquals("Ceremony description", request.getCeremonyDescription());
        assertEquals("Main event", request.getCeremonyCategory());
        assertEquals(date, request.getDate());
        assertEquals("awards/2026/top-shooter.png", request.getImageFilePath());
        assertEquals("first.png", request.getFirstPlaceImageFileName());
        assertEquals("second.png", request.getSecondPlaceImageFileName());
        assertEquals("third.png", request.getThirdPlaceImageFileName());
    }

    @Test
    void testJsonSerialization_whenFullyPopulated_thenSerializesAllFields() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        TestAwardRequestForCSV request =
                new TestAwardRequestForCSV("Top Shooter", "Annual Awards", "Jane Doe", "John Roe", "Sam Poe");
        request.setSummary("Award summary");
        request.setDescription("Award description");
        request.setCategory("Overall");
        request.setCeremonySummary("Ceremony summary");
        request.setCeremonyDescription("Ceremony description");
        request.setCeremonyCategory("Main event");
        request.setDate(LocalDate.of(2026, 4, 24));
        request.setImageFilePath("awards/2026/top-shooter.png");
        request.setFirstPlaceImageFileName("first.png");
        request.setSecondPlaceImageFileName("second.png");
        request.setThirdPlaceImageFileName("third.png");
        request.getTags().add("ipsc");
        request.getCeremonyTags().add("hpsc");

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals("Top Shooter", node.get("title").asText());
        assertEquals("Annual Awards", node.get("ceremonyTitle").asText());
        assertEquals("Jane Doe", node.get("firstPlaceName").asText());
        assertEquals("John Roe", node.get("secondPlaceName").asText());
        assertEquals("Sam Poe", node.get("thirdPlaceName").asText());
        assertEquals("Award summary", node.get("summary").asText());
        assertEquals("Award description", node.get("description").asText());
        assertEquals("Overall", node.get("category").asText());
        assertEquals("Ceremony summary", node.get("ceremonySummary").asText());
        assertEquals("Ceremony description", node.get("ceremonyDescription").asText());
        assertEquals("Main event", node.get("ceremonyCategory").asText());
        assertEquals("awards/2026/top-shooter.png", node.get("imageFilePath").asText());
        assertEquals("first.png", node.get("firstPlaceImageFileName").asText());
        assertEquals("second.png", node.get("secondPlaceImageFileName").asText());
        assertEquals("third.png", node.get("thirdPlaceImageFileName").asText());
        assertEquals("ipsc", node.get("tags").get(0).asText());
        assertEquals("hpsc", node.get("ceremonyTags").get(0).asText());
    }

    @Test
    void testJsonSerialization_whenOnlyRequiredFieldsSet_thenSerializesWithEmptyTagListsAndNullOptionals() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        TestAwardRequestForCSV request =
                new TestAwardRequestForCSV("Top Shooter", "Annual Awards", "Jane Doe", null, null);

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals("Top Shooter", node.get("title").asText());
        assertEquals("Annual Awards", node.get("ceremonyTitle").asText());
        assertEquals("Jane Doe", node.get("firstPlaceName").asText());
        assertTrue(node.get("secondPlaceName").isNull());
        assertTrue(node.get("thirdPlaceName").isNull());
        assertTrue(node.get("summary").isNull());
        assertTrue(node.get("description").isNull());
        assertTrue(node.get("date").isNull());
        assertTrue(node.get("imageFilePath").isNull());
        assertTrue(node.get("tags").isArray());
        assertTrue(node.get("ceremonyTags").isArray());
        assertEquals(0, node.get("tags").size());
        assertEquals(0, node.get("ceremonyTags").size());
    }

    @Test
    void testJsonDeserialization_whenTitleMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String json = """
                {
                  "ceremonyTitle": "Annual Awards",
                  "firstPlaceName": "Jane Doe"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestAwardRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenCeremonyTitleMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String json = """
                {
                  "title": "Top Shooter",
                  "firstPlaceName": "Jane Doe"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestAwardRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenFirstPlaceNameMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String json = """
                {
                  "title": "Top Shooter",
                  "ceremonyTitle": "Annual Awards"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestAwardRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenAllRequiredFieldsMissing_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String json = """
                {
                  "category": "Overall",
                  "summary": "Award summary"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestAwardRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenEmptyObject_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String json = "{}";

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(json, TestAwardRequestForCSV.class));
    }

    @Test
    void testJsonDeserialization_whenUnknownProperty_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String jsonUnknownProperty = """
                {
                  "title": "Podium Shot",
                  "name": "Podium Shot"
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> objectMapper.readValue(jsonUnknownProperty, TestAwardRequestForCSV.class));
    }

    @Test
    void testThreeParamConstructor_whenRequiredFieldsProvided_thenMapsFieldsAndLeavesOptionalPlaceNamesNull() {
        // Arrange & Act
        MinimalAwardRequestForCSV request =
                new MinimalAwardRequestForCSV("Best Shooter", "Club Awards", "Alice Smith");

        // Assert
        assertEquals("Best Shooter", request.getTitle());
        assertEquals("Club Awards", request.getCeremonyTitle());
        assertEquals("Alice Smith", request.getFirstPlaceName());
        assertNull(request.getSecondPlaceName());
        assertNull(request.getThirdPlaceName());
        assertNotNull(request.getTags());
        assertNotNull(request.getCeremonyTags());
        assertTrue(request.getTags().isEmpty());
        assertTrue(request.getCeremonyTags().isEmpty());
    }

    @Test
    void testJsonDeserialization_whenTagsAndCeremonyTagsInPayload_thenDeserializesTagLists() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {
                  "title": "Top Shooter",
                  "ceremonyTitle": "Annual Awards",
                  "firstPlaceName": "Jane Doe",
                  "tags": ["ipsc", "hpsc"],
                  "ceremonyTags": ["annual", "gala"]
                }
                """;

        // Act
        TestAwardRequestForCSV request = mapper.readValue(json, TestAwardRequestForCSV.class);

        // Assert
        assertEquals(List.of("ipsc", "hpsc"), request.getTags());
        assertEquals(List.of("annual", "gala"), request.getCeremonyTags());
    }

    @Test
    void testSetters_whenTagListsReplaced_thenReflectsNewLists() {
        // Arrange
        TestAwardRequestForCSV request =
                new TestAwardRequestForCSV("Top Shooter", "Annual Awards", "Jane Doe", null, null);
        List<String> tags = new ArrayList<>(List.of("ipsc", "hpsc"));
        List<String> ceremonyTags = new ArrayList<>(List.of("annual", "gala"));

        // Act
        request.setTags(tags);
        request.setCeremonyTags(ceremonyTags);

        // Assert
        assertEquals(tags, request.getTags());
        assertEquals(ceremonyTags, request.getCeremonyTags());
    }

    @Test
    void testJsonSerialization_whenDateSet_thenSerializesInIsoDateFormat() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        TestAwardRequestForCSV request =
                new TestAwardRequestForCSV("Top Shooter", "Annual Awards", "Jane Doe", null, null);
        request.setDate(LocalDate.of(2026, 4, 24));

        // Act
        String json = mapper.writeValueAsString(request);
        JsonNode node = mapper.readTree(json);

        // Assert
        assertEquals("2026-04-24", node.get("date").asText());
    }

    @Test
    void testJsonDeserialization_whenValidPayloadProvided_thenCreatesInstanceUsingJsonCreatorAndParsesDate() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String json = """
                {
                  "title":"Top Shooter",
                  "ceremonyTitle":"Annual Awards",
                  "firstPlaceName":"Jane Doe",
                  "secondPlaceName":"John Roe",
                  "thirdPlaceName":"Sam Poe",
                  "date":"2026-04-24",
                  "category":"Overall"
                }
                """;

        // Act
        TestAwardRequestForCSV request = mapper.readValue(json, TestAwardRequestForCSV.class);

        // Assert
        assertEquals("Top Shooter", request.getTitle());
        assertEquals("Annual Awards", request.getCeremonyTitle());
        assertEquals("Jane Doe", request.getFirstPlaceName());
        assertEquals("John Roe", request.getSecondPlaceName());
        assertEquals("Sam Poe", request.getThirdPlaceName());
        assertEquals(LocalDate.of(2026, 4, 24), request.getDate());
        assertEquals("Overall", request.getCategory());
    }
}
