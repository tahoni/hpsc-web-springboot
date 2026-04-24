package za.co.hpsc.web.models.award.request;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// TODO: standard naming
public class AwardRequestForCSVTest {
    @NoArgsConstructor
    private static class TestAwardRequestForCSV extends AwardRequestForCSV {
        TestAwardRequestForCSV(String title, String ceremonyTitle, String firstPlaceName,
                               String secondPlaceName, String thirdPlaceName) {
            super(title, ceremonyTitle, firstPlaceName, secondPlaceName, thirdPlaceName);
        }
    }

    @Test
    void constructor_whenRequiredFieldsProvided_thenMapsCoreFieldsAndInitializesTagLists() {
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
    void constructor_whenOptionalPlaceNamesAreNull_thenKeepsOptionalNamesNull() {
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
    void tags_whenMutated_thenPersistsAddedValues() {
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
    void setters_whenMetadataProvided_thenUpdatesAllOptionalFields() {
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
    void jsonDeserialization_whenUnknownProperty_thenThrowsMismatchedInputException() {
        // Arrange
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        String jsonUnknownProperty = """
                {
                  "title": "Podium Shot",
                  "name": "Podium Shot",
                }
                """;

        // Act & Assert
        assertThrows(MismatchedInputException.class,
                () -> objectMapper.readValue(jsonUnknownProperty, TestAwardRequestForCSV.class));
    }

    @Test
    void jsonDeserialization_whenValidPayloadProvided_thenCreatesInstanceUsingJsonCreatorAndParsesDate() throws Exception {
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
