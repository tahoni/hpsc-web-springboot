package za.co.hpsc.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponse;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponseHolder;
import za.co.hpsc.web.models.award.response.AwardResponse;
import za.co.hpsc.web.services.impl.AwardServiceImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AwardService} contract, exercised entirely through the
 * interface type. Covers {@code createAwards} - the interface's only declared method.
 * Impl-specific helper methods ({@code readAwards}, {@code mapAwards}) are covered by
 * {@link za.co.hpsc.web.services.impl.AwardServiceImplTest}.
 */
@ExtendWith(MockitoExtension.class)
public class AwardServiceTest {

    private static final String CSV_HEADER =
            "title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName\n";

    @InjectMocks
    private AwardServiceImpl awardServiceImpl;

    private AwardService awardService;

    @BeforeEach
    void setUp() {
        awardService = awardServiceImpl;
    }

    // createAwards()
    @Test
    void testCreateAwards_whenCsvDataIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.createAwards(null));
    }

    @Test
    void testCreateAwards_whenCsvDataIsEmpty_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.createAwards(""));
    }

    @Test
    void testCreateAwards_whenCsvDataIsBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.createAwards("   \t\n  "));
    }

    @Test
    void testCreateAwards_whenCsvIsPlainText_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.createAwards("Invalid CSV Format"));
    }

    @Test
    void testCreateAwards_whenRequiredColumnsAreMissing_thenThrowsValidationException() {
        // Arrange
        String csvData = """
                ceremonyTitle,imageFilePath,title,firstPlace,secondPlace,thirdPlace
                Ceremony 1,path/to/image1.png
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () -> awardService.createAwards(csvData));
    }

    @Test
    void testCreateAwards_whenHeaderOnlyWithNoDataRows_thenReturnsEmptyCeremonyList() {
        // Act
        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() -> awardService.createAwards(CSV_HEADER));

        // Assert
        assertNotNull(responseHolder);
        assertTrue(responseHolder.getAwardCeremonies().isEmpty());
    }

    @Test
    void testCreateAwards_whenSingleCeremonyWithSingleAwardAndAllFields_thenReturnsAllFieldsMapped() {
        // Arrange
        String csvData = CSV_HEADER +
                "Top Shooter,Best shooter award,Annual top shooter description,Overall,ipsc|hpsc,2026-04-10,awards/top-shooter,IPSC Gala 2026,Annual gala summary,Gala description,Gala Category,gala|annual,Jane Doe,John Roe,Sam Poe,jane.png,john.png,sam.png\n";

        // Act
        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() -> awardService.createAwards(csvData));

        // Assert - Verify ceremony data
        List<AwardCeremonyResponse> ceremonies = responseHolder.getAwardCeremonies();
        assertEquals(1, ceremonies.size());
        AwardCeremonyResponse ceremony = ceremonies.getFirst();
        assertNotNull(ceremony.getUuid());
        assertEquals("IPSC Gala 2026", ceremony.getTitle());
        assertEquals("Annual gala summary", ceremony.getSummary());
        assertEquals(LocalDate.of(2026, 4, 10), ceremony.getDate());
        assertEquals("awards/top-shooter", ceremony.getImageFilePath());

        // Assert - Verify award data
        assertEquals(1, ceremony.getAwards().size());
        AwardResponse award = ceremony.getAwards().getFirst();
        assertEquals("Top Shooter", award.getTitle());
        assertEquals(List.of("ipsc", "hpsc"), award.getTags());
        assertEquals("Jane Doe", award.getFirstPlace().getName());
        assertEquals("jane.png", award.getFirstPlace().getImageFilePath());
        assertEquals("John Roe", award.getSecondPlace().getName());
        assertEquals("Sam Poe", award.getThirdPlace().getName());
    }

    @Test
    void testCreateAwards_whenMultipleCeremoniesProvided_thenGroupsAwardsByCeremonyTitle() {
        // Arrange
        String csvData = CSV_HEADER +
                "Award A1,,,,,,,Ceremony Alpha,,,,,Alice,Bob,,,,\n" +
                "Award A2,,,,,,,Ceremony Alpha,,,,,Carol,Dan,,,,\n" +
                "Award B1,,,,,,,Ceremony Beta,,,,,Eve,Frank,,,,\n";

        // Act
        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() -> awardService.createAwards(csvData));

        // Assert
        List<AwardCeremonyResponse> ceremonies = responseHolder.getAwardCeremonies();
        assertEquals(2, ceremonies.size());
        assertEquals("Ceremony Alpha", ceremonies.getFirst().getTitle());
        assertEquals(2, ceremonies.getFirst().getAwards().size());
        assertEquals("Ceremony Beta", ceremonies.get(1).getTitle());
        assertEquals(1, ceremonies.get(1).getAwards().size());
    }

    @Test
    void testCreateAwards_whenColumnsAreReordered_thenMapsAllFieldsCorrectly() {
        // Arrange
        String csvData = """
                ceremonyTitle,firstPlaceName,secondPlaceName,thirdPlaceName,title,imageFilePath,date,summary,description,category,tags,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Annual Gala,Gold Winner,Silver Winner,Bronze Winner,Best Shot,awards/best,2026-06-15,Shot summary,Shot description,Precision,precision|accuracy,Gala summary,Gala description,Elite,elite|prestige,gold.png,silver.png,bronze.png
                """;

        // Act
        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() -> awardService.createAwards(csvData));

        // Assert
        AwardCeremonyResponse ceremony = responseHolder.getAwardCeremonies().getFirst();
        assertEquals("Annual Gala", ceremony.getTitle());
        AwardResponse award = ceremony.getAwards().getFirst();
        assertEquals("Best Shot", award.getTitle());
        assertEquals("Gold Winner", award.getFirstPlace().getName());
        assertEquals("gold.png", award.getFirstPlace().getImageFilePath());
    }
}
