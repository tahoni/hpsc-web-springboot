package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.award.AwardCeremonyResponse;
import za.co.hpsc.web.models.award.AwardCeremonyResponseHolder;
import za.co.hpsc.web.models.award.AwardResponse;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AwardServiceTest {

    @InjectMocks
    private HpscAwardService hpscAwardService;

    @Test
    void testProcessCsv_withValidCsvData_thenReturnsListOfAwards() {
        // Arrange
        String csvData = """
                title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Award 1,Summary 1,Description 1,Category 1,tag1|tag2,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,John Doe,Alice Smith,Bob Johnson,w1.png,w2.png,w3.png
                Award 2,Summary 2,Description 2,Category 2,tag3|tag4,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,Mary Jane,Tom Brown,Karen White,wX.png,wY.png,wZ.png
                """;

        // Act
        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() -> hpscAwardService.processCsv(csvData));

        // Assert
        assertNotNull(responseHolder);
        // Assert ceremonies
        List<AwardCeremonyResponse> awardCeremonies = responseHolder.getAwardCeremonies();
        assertEquals(1, awardCeremonies.size());
        // Assert first and only ceremony
        AwardCeremonyResponse ceremonyResponse = awardCeremonies.getFirst();
        assertNotNull(ceremonyResponse.getUuid());
        assertEquals(LocalDate.of(2023, 10, 10), ceremonyResponse.getDate());
        assertEquals("/path/to", ceremonyResponse.getImageFilePath());
        assertEquals("Ceremony 1", ceremonyResponse.getTitle());
        assertEquals("Ceremony Summary 1", ceremonyResponse.getSummary());
        assertEquals("Ceremony Description 1", ceremonyResponse.getDescription());
        assertEquals("Ceremony Category 1", ceremonyResponse.getCategory());
        assertEquals(List.of("tags1"), ceremonyResponse.getTags());
        // Assert awards
        assertEquals(2, ceremonyResponse.getAwards().size());
        List<AwardResponse> awards = ceremonyResponse.getAwards();
        // Assert the first award
        AwardResponse firstAward = awards.getFirst();
        assertEquals("Award 1", firstAward.getTitle());
        assertEquals("Summary 1", firstAward.getSummary());
        assertEquals("Description 1", firstAward.getDescription());
        assertEquals("Category 1", firstAward.getCategory());
        assertEquals(List.of("tag1", "tag2"), firstAward.getTags());
        assertEquals("John Doe", firstAward.getFirstPlace().getName());
        assertEquals("Alice Smith", firstAward.getSecondPlace().getName());
        assertEquals("Bob Johnson", firstAward.getThirdPlace().getName());
        assertEquals("w1.png", firstAward.getFirstPlace().getImageFilePath());
        assertEquals("w2.png", firstAward.getSecondPlace().getImageFilePath());
        assertEquals("w3.png", firstAward.getThirdPlace().getImageFilePath());
        // Assert the second award
        AwardResponse secondAward = awards.getLast();
        assertEquals("Award 2", secondAward.getTitle());
        assertEquals("Summary 2", secondAward.getSummary());
        assertEquals("Description 2", secondAward.getDescription());
        assertEquals("Category 2", secondAward.getCategory());
        assertEquals(List.of("tag3", "tag4"), secondAward.getTags());
        assertEquals("Mary Jane", secondAward.getFirstPlace().getName());
        assertEquals("Tom Brown", secondAward.getSecondPlace().getName());
        assertEquals("Karen White", secondAward.getThirdPlace().getName());
        assertEquals("wX.png", secondAward.getFirstPlace().getImageFilePath());
        assertEquals("wY.png", secondAward.getSecondPlace().getImageFilePath());
        assertEquals("wZ.png", secondAward.getThirdPlace().getImageFilePath());
    }

    @Test
    void testProcessCsv_withInvalidCsvData_thenThrowsException() {
        // Arrange
        String csvData = """
                ceremonyTitle,imageFilePath,title,firstPlace,secondPlace,thirdPlace
                Ceremony 1,path/to/image1.png
                """; // This is an intentionally incomplete row.

        // Act & Assert
        assertThrows(ValidationException.class, () -> hpscAwardService.processCsv(csvData));
    }

    @Test
    void testProcessCsv_withEmptyCsvData_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> hpscAwardService.processCsv(""));
    }

    @Test
    void testProcessCsv_withNullCsvData_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> hpscAwardService.processCsv(null));
    }

    @Test
    void testProcessCsv_withInvalidCsvFormat_thenThrowsException() {
        // Arrange
        String csvData = "Invalid CSV Format";

        // Act & Assert
        assertThrows(ValidationException.class, () -> hpscAwardService.processCsv(csvData));
    }
}