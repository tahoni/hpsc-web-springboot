package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.award.request.AwardRequest;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponse;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponseHolder;
import za.co.hpsc.web.models.award.response.AwardResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AwardServiceTest {

    @InjectMocks
    private AwardServiceImpl awardService;

    // =====================================================================
    // Tests for processCsv - Valid Data Processing
    // =====================================================================

    @Test
    public void testProcessCsv_whenValidCsvData_thenReturnsAwardCeremonyResponseHolder() {
        // Arrange
        String csvData = """
                title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Award 1,Summary 1,Description 1,Category 1,tag1|tag2,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,John Doe,Alice Smith,Bob Johnson,w1.png,w2.png,w3.png
                Award 2,Summary 2,Description 2,Category 2,tag3|tag4,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,Mary Jane,Tom Brown,Karen White,wX.png,wY.png,wZ.png
                """;

        // Act
        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() -> awardService.processCsv(csvData));

        // Assert - Verify holder structure
        assertNotNull(responseHolder);
        List<AwardCeremonyResponse> awardCeremonies = responseHolder.getAwardCeremonies();
        assertEquals(1, awardCeremonies.size());

        // Assert - Verify ceremony data
        AwardCeremonyResponse ceremonyResponse = awardCeremonies.getFirst();
        assertNotNull(ceremonyResponse.getUuid());
        assertEquals(LocalDate.of(2023, 10, 10), ceremonyResponse.getDate());
        assertEquals("/path/to", ceremonyResponse.getImageFilePath());
        assertEquals("Ceremony 1", ceremonyResponse.getTitle());
        assertEquals("Ceremony Summary 1", ceremonyResponse.getSummary());
        assertEquals("Ceremony Description 1", ceremonyResponse.getDescription());
        assertEquals("Ceremony Category 1", ceremonyResponse.getCategory());
        assertEquals(List.of("tags1"), ceremonyResponse.getTags());

        // Assert - Verify awards count
        assertEquals(2, ceremonyResponse.getAwards().size());
        List<AwardResponse> awards = ceremonyResponse.getAwards();

        // Assert - Verify first award
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

        // Assert - Verify second award
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

    // =====================================================================
    // Tests for processCsv - Input Validation and Error Handling
    // =====================================================================

    @Test
    public void testProcessCsv_whenInvalidCsvData_thenThrowsValidationException() {
        // Arrange
        String csvData = """
                ceremonyTitle,imageFilePath,title,firstPlace,secondPlace,thirdPlace
                Ceremony 1,path/to/image1.png
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.processCsv(csvData));
    }

    @Test
    public void testProcessCsv_whenEmptyCsvData_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.processCsv(""));
    }

    @Test
    public void testProcessCsv_whenNullCsvData_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.processCsv(null));
    }

    @Test
    public void testProcessCsv_whenInvalidCsvFormat_thenThrowsValidationException() {
        // Arrange
        String csvData = "Invalid CSV Format";

        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.processCsv(csvData));
    }

    // =====================================================================
    // Tests for readAwards - Valid Data Processing
    // =====================================================================

    @Test
    public void testReadAwards_whenValidCsv_thenReturnsAwardRequestList() {
        // Arrange
        String csvData = """
                title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Award 1,Summary 1,Description 1,Category 1,tag1|tag2,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,John Doe,Alice Smith,Bob Johnson,w1.png,w2.png,w3.png
                Award 2,Summary 2,Description 2,Category 2,tag3|tag4,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,Mary Jane,Tom Brown,Karen White,wX.png,wY.png,wZ.png
                """;

        // Act
        List<AwardRequest> awardRequests = assertDoesNotThrow(() ->
                awardService.readAwards(csvData));

        // Assert - Verify list size
        assertNotNull(awardRequests);
        assertEquals(2, awardRequests.size());

        // Assert - Verify first award (ceremony data)
        AwardRequest firstAward = awardRequests.getFirst();
        assertEquals(LocalDate.of(2023, 10, 10), firstAward.getDate());
        assertEquals("/path/to", firstAward.getImageFilePath());
        assertEquals("Ceremony 1", firstAward.getCeremonyTitle());
        assertEquals("Ceremony Summary 1", firstAward.getCeremonySummary());
        assertEquals("Ceremony Description 1", firstAward.getCeremonyDescription());
        assertEquals("Ceremony Category 1", firstAward.getCeremonyCategory());
        assertEquals(List.of("tags1"), firstAward.getCeremonyTags());

        // Assert - Verify first award (award data)
        assertEquals("Award 1", firstAward.getTitle());
        assertEquals("Summary 1", firstAward.getSummary());
        assertEquals("Description 1", firstAward.getDescription());
        assertEquals("Category 1", firstAward.getCategory());
        assertEquals(List.of("tag1", "tag2"), firstAward.getTags());

        // Assert - Verify first award (award places)
        assertEquals("John Doe", firstAward.getFirstPlaceName());
        assertEquals("Alice Smith", firstAward.getSecondPlaceName());
        assertEquals("Bob Johnson", firstAward.getThirdPlaceName());
        assertEquals("w1.png", firstAward.getFirstPlaceImageFileName());
        assertEquals("w2.png", firstAward.getSecondPlaceImageFileName());
        assertEquals("w3.png", firstAward.getThirdPlaceImageFileName());

        // Assert - Verify second award (ceremony data)
        AwardRequest secondAward = awardRequests.get(1);
        assertEquals(LocalDate.of(2023, 10, 10), secondAward.getDate());
        assertEquals("/path/to", secondAward.getImageFilePath());
        assertEquals("Ceremony 1", secondAward.getCeremonyTitle());
        assertEquals("Ceremony Summary 1", secondAward.getCeremonySummary());
        assertEquals("Ceremony Description 1", secondAward.getCeremonyDescription());
        assertEquals("Ceremony Category 1", secondAward.getCeremonyCategory());
        assertEquals(List.of("tags1"), secondAward.getCeremonyTags());

        // Assert - Verify second award (award data)
        assertEquals("Award 2", secondAward.getTitle());
        assertEquals("Summary 2", secondAward.getSummary());
        assertEquals("Description 2", secondAward.getDescription());
        assertEquals("Category 2", secondAward.getCategory());
        assertEquals(List.of("tag3", "tag4"), secondAward.getTags());

        // Assert - Verify second award (award places)
        assertEquals("Mary Jane", awardRequests.get(1).getFirstPlaceName());
        assertEquals("Tom Brown", awardRequests.get(1).getSecondPlaceName());
        assertEquals("Karen White", awardRequests.get(1).getThirdPlaceName());
        assertEquals("wX.png", awardRequests.get(1).getFirstPlaceImageFileName());
        assertEquals("wY.png", awardRequests.get(1).getSecondPlaceImageFileName());
        assertEquals("wZ.png", awardRequests.get(1).getThirdPlaceImageFileName());
    }

    @Test
    public void testReadAwards_whenValidCsvWithRearrangedColumns_thenReturnsAwardRequestList() {
        // Arrange - CSV with columns in different order
        String csvData = """
                title,description,summary,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Award 1,Description 1,Summary 1,Category 1,tag1|tag2,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,John Doe,Alice Smith,Bob Johnson,w1.png,w2.png,w3.png
                Award 2,Description 2,Summary 2,Category 2,tag3|tag4,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,Mary Jane,Tom Brown,Karen White,wX.png,wY.png,wZ.png
                """;

        // Act
        List<AwardRequest> awardRequests = assertDoesNotThrow(() ->
                awardService.readAwards(csvData));

        // Assert - Verify list size and first award
        assertNotNull(awardRequests);
        assertEquals(2, awardRequests.size());

        AwardRequest firstAward = awardRequests.getFirst();
        assertEquals(LocalDate.of(2023, 10, 10), firstAward.getDate());
        assertEquals("/path/to", firstAward.getImageFilePath());
        assertEquals("Ceremony 1", firstAward.getCeremonyTitle());
        assertEquals("Award 1", firstAward.getTitle());
        assertEquals("Summary 1", firstAward.getSummary());
        assertEquals("Description 1", firstAward.getDescription());
        assertEquals("Category 1", firstAward.getCategory());
        assertEquals(List.of("tag1", "tag2"), firstAward.getTags());
        assertEquals("John Doe", firstAward.getFirstPlaceName());
        assertEquals("Alice Smith", firstAward.getSecondPlaceName());
        assertEquals("Bob Johnson", firstAward.getThirdPlaceName());
        assertEquals("w1.png", firstAward.getFirstPlaceImageFileName());
        assertEquals("w2.png", firstAward.getSecondPlaceImageFileName());
        assertEquals("w3.png", firstAward.getThirdPlaceImageFileName());

        // Assert - Verify second award
        AwardRequest secondAward = awardRequests.get(1);
        assertEquals(LocalDate.of(2023, 10, 10), secondAward.getDate());
        assertEquals("/path/to", secondAward.getImageFilePath());
        assertEquals("Ceremony 1", secondAward.getCeremonyTitle());
        assertEquals("Award 2", secondAward.getTitle());
        assertEquals("Summary 2", secondAward.getSummary());
        assertEquals("Description 2", secondAward.getDescription());
        assertEquals("Category 2", secondAward.getCategory());
        assertEquals(List.of("tag3", "tag4"), secondAward.getTags());
        assertEquals("Mary Jane", awardRequests.get(1).getFirstPlaceName());
        assertEquals("Tom Brown", awardRequests.get(1).getSecondPlaceName());
        assertEquals("Karen White", awardRequests.get(1).getThirdPlaceName());
        assertEquals("wX.png", awardRequests.get(1).getFirstPlaceImageFileName());
        assertEquals("wY.png", awardRequests.get(1).getSecondPlaceImageFileName());
        assertEquals("wZ.png", awardRequests.get(1).getThirdPlaceImageFileName());
    }

    @Test
    public void testReadAwards_whenLargeCsv_thenReturnsAwardRequestList() {
        // Arrange - Create large CSV with 1000 rows
        StringBuilder largeCsv = new StringBuilder("title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName\n");

        for (int i = 0; i < 1000; i++) {
            largeCsv.append("Title ").append(i).append(",Summary ").append(i).append(",Description ").append(i)
                    .append(",Category ").append(i % 10).append(",Tag").append(i % 10);
            largeCsv.append(",2023-10-10").append(",/path/to/image").append(i);
            largeCsv.append(",Ceremony Title ").append(i).append(",Ceremony Summary ").append(i)
                    .append(",Ceremony Description ").append(i).append(",Ceremony Category ").append(i % 10)
                    .append(",ceremonyTag").append(i % 10);
            largeCsv.append(",First ").append(i).append(",Second ").append(i).append(",Third ").append(i)
                    .append(",imgA").append(i).append(".png").append(",imgB").append(i).append(".png")
                    .append(",imgC").append(i).append(".png\n");
        }

        // Act
        List<AwardRequest> awardRequests = assertDoesNotThrow(() ->
                awardService.readAwards(largeCsv.toString()));

        // Assert - Verify total count
        assertNotNull(awardRequests);
        assertEquals(1000, awardRequests.size());

        // Assert - Verify random awards throughout the list
        assertEquals("Title 200", awardRequests.get(200).getTitle());
        assertEquals("Summary 200", awardRequests.get(200).getSummary());
        assertEquals("Ceremony Title 400", awardRequests.get(400).getCeremonyTitle());
        assertEquals("Ceremony Description 400", awardRequests.get(400).getCeremonyDescription());
        assertEquals("First 600", awardRequests.get(600).getFirstPlaceName());
        assertEquals("imgA600.png", awardRequests.get(600).getFirstPlaceImageFileName());
        assertEquals("Tag0", awardRequests.get(800).getTags().getFirst());

        // Assert - Verify last award has complete data
        assertEquals(LocalDate.of(2023, 10, 10), awardRequests.get(999).getDate());
        assertEquals("/path/to/image999", awardRequests.get(999).getImageFilePath());
        assertEquals("Ceremony Title 999", awardRequests.get(999).getCeremonyTitle());
        assertEquals("Ceremony Summary 999", awardRequests.get(999).getCeremonySummary());
        assertEquals("Ceremony Description 999", awardRequests.get(999).getCeremonyDescription());
        assertEquals("Ceremony Category 9", awardRequests.get(999).getCeremonyCategory());
        assertEquals("ceremonyTag9", awardRequests.get(999).getCeremonyTags().getLast());
        assertEquals("Title 999", awardRequests.get(999).getTitle());
        assertEquals("Summary 999", awardRequests.get(999).getSummary());
        assertEquals("Description 999", awardRequests.get(999).getDescription());
        assertEquals("Category 9", awardRequests.get(999).getCategory());
        assertEquals("Tag9", awardRequests.get(999).getTags().getLast());
        assertEquals("First 999", awardRequests.get(999).getFirstPlaceName());
        assertEquals("imgA999.png", awardRequests.get(999).getFirstPlaceImageFileName());
        assertEquals("Second 999", awardRequests.get(999).getSecondPlaceName());
        assertEquals("imgB999.png", awardRequests.get(999).getSecondPlaceImageFileName());
        assertEquals("Third 999", awardRequests.get(999).getThirdPlaceName());
        assertEquals("imgC999.png", awardRequests.get(999).getThirdPlaceImageFileName());
    }

    // =====================================================================
    // Tests for readAwards - Input Validation and Error Handling
    // =====================================================================

    @Test
    public void testReadAwards_whenEmptyCsvData_thenReturnsEmptyList() {
        // Arrange - CSV with only header, no data rows
        String emptyCsvData = "title,description,summary,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName\n";

        // Act
        List<AwardRequest> awardRequests = assertDoesNotThrow(() ->
                awardService.readAwards(emptyCsvData));

        // Assert
        assertNotNull(awardRequests);
        assertTrue(awardRequests.isEmpty());
    }

    @Test
    public void testReadAwards_whenMissingCsvColumns_thenThrowsValidationException() {
        // Arrange - CSV with missing required columns
        String csvData = """
                title,summary,imageFilePath,ceremonyTitle,ceremonySummary
                Award 1,Summary 1,/path/to,Ceremony 1,Ceremony Summary 1
                Award 2,Summary 2,/path/to,Ceremony 1,Ceremony Summary 1
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                awardService.readAwards(csvData));
    }

    @Test
    public void testReadAwards_whenInvalidCsvData_thenThrowsValidationException() {
        // Arrange - CSV row with incorrect number of columns
        String invalidCsvData = """
                title,description,summary,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Invalid Row Without Correct Columns
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                awardService.readAwards(invalidCsvData));
    }

    @Test
    public void testReadAwards_whenInvalidCsvStructure_thenThrowsValidationException() {
        // Arrange - CSV with invalid headers
        String invalidCsvStructure = """
                Invalid_Header1,Invalid_Header2,Invalid_Header3
                value1,value2
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                awardService.readAwards(invalidCsvStructure));
    }

    @Test
    public void testReadAwards_whenInvalidCsvFormat_thenThrowsValidationException() {
        // Arrange - Single column with no proper header
        String invalidCsv = """
                Invalid CSV With One Column and no Header
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                awardService.readAwards(invalidCsv));
    }

    @Test
    public void testReadAwards_whenBlankCsv_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.readAwards("  "));
    }

    @Test
    public void testReadAwards_whenEmptyStringCsv_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.readAwards(""));
    }

    @Test
    public void testReadAwards_whenNullCsv_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.readAwards(null));
    }

    // =====================================================================
    // Tests for mapAwards - Valid Data Processing
    // =====================================================================

    @Test
    public void testMapAwards_whenValidAwardRequests_thenReturnsAwardCeremonyResponseList() {
        // Arrange
        AwardRequest request1 = new AwardRequest("Award 1", "Ceremony A", "Alice", "Bob", "Charlie");
        request1.setImageFilePath("/path/to/images1");

        AwardRequest request2 = new AwardRequest("Award 2", "Ceremony A", "Dan", "Eve", "Frank");
        request2.setImageFilePath("/path/to/images2.png");

        AwardRequest request3 = new AwardRequest("Award 3", "Ceremony B", "Grace", "Heidi", "Ivan");
        request3.setImageFilePath("/path/to/images3");

        List<AwardRequest> awardRequests = List.of(request1, request2, request3);

        // Act
        List<AwardCeremonyResponse> responses = awardService.mapAwards(awardRequests);

        // Assert - Verify ceremony count
        assertEquals(2, responses.size());

        // Assert - Verify first ceremony structure
        AwardCeremonyResponse ceremonyA = responses.getFirst();
        assertEquals("Ceremony A", ceremonyA.getTitle());
        assertEquals("/path/to/images1", ceremonyA.getImageFilePath());
        assertEquals(2, ceremonyA.getAwards().size());

        // Assert - Verify first award of first ceremony
        List<AwardResponse> awardsCeremonyA = ceremonyA.getAwards();
        assertEquals("Award 1", awardsCeremonyA.getFirst().getTitle());
        assertEquals("Alice", awardsCeremonyA.getFirst().getFirstPlace().getName());
        assertEquals("Bob", awardsCeremonyA.getFirst().getSecondPlace().getName());
        assertEquals("Charlie", awardsCeremonyA.getFirst().getThirdPlace().getName());

        // Assert - Verify second award of first ceremony
        assertEquals("Award 2", awardsCeremonyA.getLast().getTitle());
        assertEquals("Dan", awardsCeremonyA.getLast().getFirstPlace().getName());
        assertEquals("Eve", awardsCeremonyA.getLast().getSecondPlace().getName());
        assertEquals("Frank", awardsCeremonyA.getLast().getThirdPlace().getName());

        // Assert - Verify second ceremony structure
        AwardCeremonyResponse ceremonyB = responses.getLast();
        assertEquals("Ceremony B", ceremonyB.getTitle());
        assertEquals("/path/to/images3", ceremonyB.getImageFilePath());
        assertEquals(1, ceremonyB.getAwards().size());

        // Assert - Verify award in second ceremony
        List<AwardResponse> awardsCeremonyB = ceremonyB.getAwards();
        assertEquals("Award 3", awardsCeremonyB.getFirst().getTitle());
        assertEquals("Grace", awardsCeremonyB.getFirst().getFirstPlace().getName());
        assertEquals("Heidi", awardsCeremonyB.getFirst().getSecondPlace().getName());
        assertEquals("Ivan", awardsCeremonyB.getFirst().getThirdPlace().getName());
    }

    // =====================================================================
    // Tests for mapAwards - Input Validation and Error Handling
    // =====================================================================

    @Test
    public void testMapAwards_whenEmptyAwardRequestList_thenReturnsEmptyList() {
        // Arrange
        List<AwardRequest> awardRequests = new ArrayList<>();

        // Act
        List<AwardCeremonyResponse> responses = awardService.mapAwards(awardRequests);

        // Assert
        assertTrue(responses.isEmpty());
    }

    @Test
    public void testMapAwards_whenNullAwardRequestList_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.mapAwards(null));
    }
}

