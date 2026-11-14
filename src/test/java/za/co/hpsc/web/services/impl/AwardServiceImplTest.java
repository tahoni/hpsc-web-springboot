package za.co.hpsc.web.services.impl;

import za.co.hpsc.web.models.award.request.AwardRequestForCSV;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import za.co.hpsc.web.exceptions.FatalException;
import org.mockito.MockedConstruction;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.core.FormatSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.award.request.AwardRequest;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponse;
import za.co.hpsc.web.models.award.response.AwardResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AwardServiceImpl}'s impl-only helper methods ({@code readAwards},
 * {@code mapAwards}) - not declared on {@link za.co.hpsc.web.services.AwardService}. The
 * interface's {@code createAwards} contract is covered by
 * {@link za.co.hpsc.web.services.AwardServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
public class AwardServiceImplTest {
    private static final String CSV_HEADER = "title,description,summary,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName\n";

    @InjectMocks
    private AwardServiceImpl awardService;

    // readAwards()
    @Test
    void testReadAwards_whenReadingTheCsvFails_thenThrowsFatalExceptionWithTheCause() throws IOException {
        // Arrange
        IOException cause = new IOException("disk unavailable");
        ObjectReader reader = mock(ObjectReader.class);
        when(reader.with(any(FormatSchema.class))).thenReturn(reader);
        when(reader.readValues(anyString())).thenThrow(cause);

        try (MockedConstruction<CsvMapper> ignored = mockConstruction(CsvMapper.class,
                (mapper, context) -> {
                    when(mapper.schemaFor(AwardRequestForCSV.class)).thenReturn(CsvSchema.emptySchema());
                    when(mapper.readerFor(AwardRequest.class)).thenReturn(reader);
                })) {

            // Act & Assert
            FatalException thrown = assertThrows(FatalException.class,
                    () -> awardService.readAwards("title,category\\nAward,Cat\\n"));
            assertSame(cause, thrown.getCause());
            assertTrue(thrown.getMessage().contains("disk unavailable"));
        }
    }

    // mapAwards()
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

    @Test
    public void testReadAwards_whenValidCsv_thenMapsEachColumnByItsHeader() {
        // Arrange
        String csvData = CSV_HEADER + """
                Award 1,Description 1,Summary 1,Category 1,tag1;tag2,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,John Doe,Alice Smith,Bob Johnson,w1.png,w2.png,w3.png
                Award 2,Description 2,Summary 2,Category 2,tag3;tag4,2023-10-10,/path/to,Ceremony 1,Ceremony Summary 1,Ceremony Description 1,Ceremony Category 1,tags1,Mary Jane,Tom Brown,Karen White,wX.png,wY.png,wZ.png
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
    public void testReadAwards_whenEmptyCsvData_thenReturnsEmptyList() {
        // Arrange
        String emptyCsvData = CSV_HEADER;

        // Act
        List<AwardRequest> awardRequests = assertDoesNotThrow(() ->
                awardService.readAwards(emptyCsvData));

        // Assert
        assertNotNull(awardRequests);
        assertTrue(awardRequests.isEmpty());
    }

    @Test
    public void testReadAwards_whenMissingCsvColumns_thenThrowsValidationException() {
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
    public void testReadAwards_whenInvalidCsvData_thenThrowsValidationException() {
        // Arrange
        String invalidCsvData = CSV_HEADER + """
                Invalid Row Without Correct Columns
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                awardService.readAwards(invalidCsvData));
    }

    @Test
    public void testReadAwards_whenInvalidCsvStructure_thenThrowsValidationException() {
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
    public void testReadAwards_whenInvalidCsvFormat_thenThrowsValidationException() {
        // Arrange
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
}
