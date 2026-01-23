package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.award.AwardCeremonyResponse;
import za.co.hpsc.web.models.award.AwardRequest;
import za.co.hpsc.web.models.award.AwardResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AwardServiceImplTest {

    @InjectMocks
    private AwardServiceImpl awardService;

    @Test
    public void testReadAwards_wihValidCsv_thenReturnsAwardRequestList() {
        // Arrange
        String csvData = """
                title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Award 1,Summary 1,Description 1,Category 1,tag1|tag2,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,John Doe,Alice Smith,Bob Johnson,w1.png,w2.png,w3.png
                Award 2,Summary 2,Description 2,Category 2,tag3|tag4,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,Mary Jane,Tom Brown,Karen White,wX.png,wY.png,wZ.png
                """;

        // Act
        List<AwardRequest> awardRequests = assertDoesNotThrow(() ->
                awardService.readAwards(csvData));

        // Assert
        assertNotNull(awardRequests);
        assertEquals(2, awardRequests.size());

        // Assert the first award (meta-data)
        AwardRequest firstAward = awardRequests.getFirst();
        assertEquals(LocalDate.of(2023, 10, 10), firstAward.getDate());
        assertEquals("/path/to", firstAward.getImageFilePath());
        assertEquals("Ceremony 1", firstAward.getCeremonyTitle());
        assertEquals("Ceremony Summary 1", firstAward.getCeremonySummary());
        assertEquals("Ceremony Description 1", firstAward.getCeremonyDescription());
        assertEquals("Ceremony Category 1", firstAward.getCeremonyCategory());
        assertEquals(List.of("tags1"), firstAward.getCeremonyTags());
        // Assert the first award (award data)
        assertEquals("Award 1", firstAward.getTitle());
        assertEquals("Summary 1", firstAward.getSummary());
        assertEquals("Description 1", firstAward.getDescription());
        assertEquals("Category 1", firstAward.getCategory());
        assertEquals(List.of("tag1", "tag2"), firstAward.getTags());
        // Assert the first award (award places)
        assertEquals("John Doe", firstAward.getFirstPlaceName());
        assertEquals("Alice Smith", firstAward.getSecondPlaceName());
        assertEquals("Bob Johnson", firstAward.getThirdPlaceName());
        assertEquals("w1.png", firstAward.getFirstPlaceImageFileName());
        assertEquals("w2.png", firstAward.getSecondPlaceImageFileName());
        assertEquals("w3.png", firstAward.getThirdPlaceImageFileName());

        // Assert the second award (meta-data)
        AwardRequest secondAward = awardRequests.get(1);
        assertEquals(LocalDate.of(2023, 10, 10), secondAward.getDate());
        assertEquals("/path/to", secondAward.getImageFilePath());
        assertEquals("Ceremony 1", secondAward.getCeremonyTitle());
        assertEquals("Ceremony Summary 1", secondAward.getCeremonySummary());
        assertEquals("Ceremony Description 1", secondAward.getCeremonyDescription());
        assertEquals("Ceremony Category 1", secondAward.getCeremonyCategory());
        assertEquals(List.of("tags1"), secondAward.getCeremonyTags());
        // Assert the second award (award data)
        assertEquals("Award 2", secondAward.getTitle());
        assertEquals("Summary 2", secondAward.getSummary());
        assertEquals("Description 2", secondAward.getDescription());
        assertEquals("Category 2", secondAward.getCategory());
        assertEquals(List.of("tag3", "tag4"), secondAward.getTags());
        // Assert the second award (award places)
        assertEquals("Mary Jane", awardRequests.get(1).getFirstPlaceName());
        assertEquals("Tom Brown", awardRequests.get(1).getSecondPlaceName());
        assertEquals("Karen White", awardRequests.get(1).getThirdPlaceName());
        assertEquals("wX.png", awardRequests.get(1).getFirstPlaceImageFileName());
        assertEquals("wY.png", awardRequests.get(1).getSecondPlaceImageFileName());
        assertEquals("wZ.png", awardRequests.get(1).getThirdPlaceImageFileName());
    }

    @Test
    void testReadAwards_withValidCsvRearrangedColumns_thenReturnsAwardRequestList() {
        // Arrange
        String csvData = """
                title,description,summary,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Award 1,Description 1,Summary 1,Category 1,tag1|tag2,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,John Doe,Alice Smith,Bob Johnson,w1.png,w2.png,w3.png
                Award 2,Description 2,Summary 2,Category 2,tag3|tag4,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,Mary Jane,Tom Brown,Karen White,wX.png,wY.png,wZ.png
                """;

        // Act
        List<AwardRequest> awardRequests = assertDoesNotThrow(() ->
                awardService.readAwards(csvData));


        // Assert
        assertNotNull(awardRequests);
        assertEquals(2, awardRequests.size());

        // Assert the first award (meta-data)
        AwardRequest firstAward = awardRequests.getFirst();
        assertEquals(LocalDate.of(2023, 10, 10), firstAward.getDate());
        assertEquals("/path/to", firstAward.getImageFilePath());
        assertEquals("Ceremony 1", firstAward.getCeremonyTitle());
        assertEquals("Ceremony Summary 1", firstAward.getCeremonySummary());
        assertEquals("Ceremony Description 1", firstAward.getCeremonyDescription());
        assertEquals("Ceremony Category 1", firstAward.getCeremonyCategory());
        assertEquals(List.of("tags1"), firstAward.getCeremonyTags());
        // Assert the first award (award data)
        assertEquals("Award 1", firstAward.getTitle());
        assertEquals("Summary 1", firstAward.getSummary());
        assertEquals("Description 1", firstAward.getDescription());
        assertEquals("Category 1", firstAward.getCategory());
        assertEquals(List.of("tag1", "tag2"), firstAward.getTags());
        // Assert the first award (award places)
        assertEquals("John Doe", firstAward.getFirstPlaceName());
        assertEquals("Alice Smith", firstAward.getSecondPlaceName());
        assertEquals("Bob Johnson", firstAward.getThirdPlaceName());
        assertEquals("w1.png", firstAward.getFirstPlaceImageFileName());
        assertEquals("w2.png", firstAward.getSecondPlaceImageFileName());
        assertEquals("w3.png", firstAward.getThirdPlaceImageFileName());

        // Assert the second award (meta-data)
        AwardRequest secondAward = awardRequests.get(1);
        assertEquals(LocalDate.of(2023, 10, 10), secondAward.getDate());
        assertEquals("/path/to", secondAward.getImageFilePath());
        assertEquals("Ceremony 1", secondAward.getCeremonyTitle());
        assertEquals("Ceremony Summary 1", secondAward.getCeremonySummary());
        assertEquals("Ceremony Description 1", secondAward.getCeremonyDescription());
        assertEquals("Ceremony Category 1", secondAward.getCeremonyCategory());
        assertEquals(List.of("tags1"), secondAward.getCeremonyTags());
        // Assert the second award (award data)
        assertEquals("Award 2", secondAward.getTitle());
        assertEquals("Summary 2", secondAward.getSummary());
        assertEquals("Description 2", secondAward.getDescription());
        assertEquals("Category 2", secondAward.getCategory());
        assertEquals(List.of("tag3", "tag4"), secondAward.getTags());
        // Assert the second award (award places)
        assertEquals("Mary Jane", awardRequests.get(1).getFirstPlaceName());
        assertEquals("Tom Brown", awardRequests.get(1).getSecondPlaceName());
        assertEquals("Karen White", awardRequests.get(1).getThirdPlaceName());
        assertEquals("wX.png", awardRequests.get(1).getFirstPlaceImageFileName());
        assertEquals("wY.png", awardRequests.get(1).getSecondPlaceImageFileName());
        assertEquals("wZ.png", awardRequests.get(1).getThirdPlaceImageFileName());
    }

    @Test
    public void testReadAwards_withLargeCsv_thenReturnsAwardRequestList() throws ValidationException, FatalException {
        // Arrange
        StringBuilder largeCsv = new StringBuilder("title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName\n");

        for (int i = 0; i < 1000; i++) {
            // Appends award details to CSV string builder
            largeCsv.append("Title ").append(i).append(",Summary ").append(i).append(",Description ").append(i)
                    .append(",Category ").append(i % 10).append(",Tag").append(i % 10);
            largeCsv.append(",2023-10-10").append(",/path/to/image").append(i);
            // Appends ceremony data to CSV string builder
            largeCsv.append(",Ceremony Title ").append(i).append(",Ceremony Summary ").append(i).append(",Ceremony Description ").append(i)
                    .append(",Ceremony Category ").append(i % 10).append(",ceremonyTag").append(i % 10);
            // Appends winners and image filenames to CSV string builder
            largeCsv.append(",First ").append(i).append(",Second ").append(i).append(",Third ").append(i)
                    .append(",imgA").append(i).append(".png").append(",imgB").append(i).append(".png").append(",imgC").append(i).append(".png\n");
        }

        // Act
        List<AwardRequest> awardRequests = awardService.readAwards(largeCsv.toString());

        // Assert
        assertNotNull(awardRequests);
        assertEquals(1000, awardRequests.size());

        // Assert random awards (award data)
        assertEquals("Title 200", awardRequests.get(200).getTitle());
        assertEquals("Summary 200", awardRequests.get(200).getSummary());
        // Assert random awards (ceremony data)
        assertEquals("Ceremony Title 400", awardRequests.get(400).getCeremonyTitle());
        assertEquals("Ceremony Description 400", awardRequests.get(400).getCeremonyDescription());
        // Assert random awards (award places)
        assertEquals("First 600", awardRequests.get(600).getFirstPlaceName());
        assertEquals("imgA600.png", awardRequests.get(600).getFirstPlaceImageFileName());
        // Assert random awards (ceremony tags)
        assertEquals("Tag0", awardRequests.get(800).getTags().getFirst());

        // Assert last award (ceremony data)
        assertEquals(LocalDate.of(2023, 10, 10), awardRequests.get(999).getDate());
        assertEquals("/path/to/image999", awardRequests.get(999).getImageFilePath());
        assertEquals("Ceremony Title 999", awardRequests.get(999).getCeremonyTitle());
        assertEquals("Ceremony Summary 999", awardRequests.get(999).getCeremonySummary());
        assertEquals("Ceremony Description 999", awardRequests.get(999).getCeremonyDescription());
        assertEquals("Ceremony Category 9", awardRequests.get(999).getCeremonyCategory());
        assertEquals("ceremonyTag9", awardRequests.get(999).getCeremonyTags().getLast());
        // Assert last award (award data)
        assertEquals("Title 999", awardRequests.get(999).getTitle());
        assertEquals("Summary 999", awardRequests.get(999).getSummary());
        assertEquals("Description 999", awardRequests.get(999).getDescription());
        assertEquals("Category 9", awardRequests.get(999).getCategory());
        assertEquals("Tag9", awardRequests.get(999).getTags().getLast());
        // Assert last award (award places)
        assertEquals("First 999", awardRequests.get(999).getFirstPlaceName());
        assertEquals("imgA999.png", awardRequests.get(999).getFirstPlaceImageFileName());
        assertEquals("Second 999", awardRequests.get(999).getSecondPlaceName());
        assertEquals("imgB999.png", awardRequests.get(999).getSecondPlaceImageFileName());
        assertEquals("Third 999", awardRequests.get(999).getThirdPlaceName());
        assertEquals("imgC999.png", awardRequests.get(999).getThirdPlaceImageFileName());
    }

    @Test
    void testReadAwards_withMissingCsvColumns_thenThrowsException() {
        // Arrange
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
    void testReadAwards_withInvalidCsvData_thenThrowsException() {
        // Arrange
        String invalidCsvData = """
                title,description,summary,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Invalid Row Without Correct Columns
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                awardService.readAwards(invalidCsvData));
    }

    @Test
    void testReadAwards_withEmptyCsvData_thenReturnsEmptyList() {
        // Arrange
        String emptyCsvData = "title,description,summary,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName\n";

        // Act
        List<AwardRequest> awardRequests = assertDoesNotThrow(() ->
                awardService.readAwards(emptyCsvData));

        // Assert
        assertNotNull(awardRequests);
        assertTrue(awardRequests.isEmpty());
    }

    @Test
    void testReadAwards_withInvalidCsvStructure_thenThrowsException() {
        // Arrange
        String invalidCsvStructure = """
                Invalid_Header1,Invalid_Header2,Invalid_Header3
                value1,value2
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                awardService.readAwards(invalidCsvStructure));
    }

    @Test
    void testReadAwards_withInvalidCsv_thenThrowsException() {
        // Arrange
        String invalidCsv = """
                Invalid CSV With One Column and no Header
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                awardService.readAwards(invalidCsv));
    }

    @Test
    public void testReadAwards_withBlankCsv_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.readAwards("  "));
    }

    @Test
    public void testReadAwards_withEmptyCsv_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.readAwards(""));
    }

    @Test
    public void testReadAwards_withNullCsv_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.readAwards(null));
    }

    @Test
    void testMapAwards_withValidAwardRequests_thenReturnsAwardCeremonyResponseList() {
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

        // Assert
        assertEquals(2, responses.size());

        // Test the first ceremony
        AwardCeremonyResponse ceremonyA = responses.getFirst();
        assertEquals("Ceremony A", ceremonyA.getTitle());
        assertEquals("/path/to/images1", ceremonyA.getImageFilePath());
        // Test the first ceremony's awards'
        assertEquals(2, ceremonyA.getAwards().size());
        List<AwardResponse> awardsCeremonyA = ceremonyA.getAwards();
        // Test the first award of the first ceremony
        assertEquals("Award 1", awardsCeremonyA.getFirst().getTitle());
        assertEquals("Alice", awardsCeremonyA.getFirst().getFirstPlace().getName());
        assertEquals("Bob", awardsCeremonyA.getFirst().getSecondPlace().getName());
        assertEquals("Charlie", awardsCeremonyA.getFirst().getThirdPlace().getName());
        // Test the second award of the first ceremony
        assertEquals("Award 2", awardsCeremonyA.getLast().getTitle());
        assertEquals("Dan", awardsCeremonyA.getLast().getFirstPlace().getName());
        assertEquals("Eve", awardsCeremonyA.getLast().getSecondPlace().getName());
        assertEquals("Frank", awardsCeremonyA.getLast().getThirdPlace().getName());

        // Test the second ceremony
        AwardCeremonyResponse ceremonyB = responses.getLast();
        assertEquals("Ceremony B", ceremonyB.getTitle());
        assertEquals("/path/to/images3", ceremonyB.getImageFilePath());
        // Test the second ceremony's awards'
        assertEquals(1, ceremonyB.getAwards().size());
        List<AwardResponse> awardsCeremonyB = ceremonyB.getAwards();
        // Test the first (and only) award of the second ceremony
        assertEquals("Award 3", awardsCeremonyB.getFirst().getTitle());
        assertEquals("Grace", awardsCeremonyB.getFirst().getFirstPlace().getName());
        assertEquals("Heidi", awardsCeremonyB.getFirst().getSecondPlace().getName());
        assertEquals("Ivan", awardsCeremonyB.getFirst().getThirdPlace().getName());
    }

    @Test
    void testMapAwards_withEmptyImageRequestList_thenReturnsEmptyList() {
        // Arrange
        List<AwardRequest> awardRequests = new ArrayList<>();

        // Act
        List<AwardCeremonyResponse> responses = awardService.mapAwards(awardRequests);

        // Assert
        assertTrue(responses.isEmpty());
    }

    @Test
    void testMapAwards_withNullAwardRequestList_thenThrowsException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.mapAwards(null));
    }
}