package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.AwardCeremonyResponse;
import za.co.hpsc.web.models.AwardCeremonyResponseHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AwardServiceTest {

    @InjectMocks
    private HpscAwardService hpscAwardService = new HpscAwardService();

    @Test
    void testProcessCsv_withValidCsvData_thenReturnsListOfAwards() {
        // Arrange
        String csvData = """
                title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFilePath,thirdPlaceImageFilePath
                Award 1,Summary 1,Description 1,Category 1,tag1|tag2,2023-10-10,path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,ctag1,John Doe,Alice Smith,Bob Johnson,w1.png,w2.png,w3.png";
                Award 2,Summary 2,Description 2,Category 2,tag3|tag4,2023-10-10,path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,ctag1,Mary Jane,Tom Brown,Karen White,w1.png,w2.png,w3.png";
                """;

        // Act
        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() -> hpscAwardService.processCsv(csvData));

        // Assert
        assertNotNull(responseHolder);
        List<AwardCeremonyResponse> awardCeremonies = responseHolder.getAwardCeremonies();
        assertEquals(2, awardCeremonies.size());
        AwardCeremonyResponse ceremonyResponse = awardCeremonies.getFirst();
        assertEquals(2, ceremonyResponse.getAwards().size());
        assertEquals("Ceremony 1", ceremonyResponse.getAwards().get(0).getTitle());
        assertEquals("Award 2", ceremonyResponse.getAwards().get(1).getSummary());
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