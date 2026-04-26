package za.co.hpsc.web.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponse;
import za.co.hpsc.web.models.award.response.AwardCeremonyResponseHolder;
import za.co.hpsc.web.models.award.response.AwardResponse;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class AwardServiceIntegrationTest {

    private static final String CSV_HEADER =
            "title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName\n";

    @Autowired
    private AwardService awardService;

    // Test Group: Null/Empty/Blank Input Handling

    @Test
    public void testProcessCsv_whenCsvDataIsNull_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> awardService.processCsv(null));
    }

    @Test
    public void testProcessCsv_whenCsvDataIsEmpty_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> awardService.processCsv(""));
    }

    @Test
    public void testProcessCsv_whenCsvDataIsBlank_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> awardService.processCsv("   \t\n  "));
    }

    // Test Group: Invalid CSV Format Handling

    @Test
    public void testProcessCsv_whenCsvIsPlainText_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                awardService.processCsv("This is not valid CSV data"));
    }

    @Test
    public void testProcessCsv_whenRequiredColumnsAreMissing_thenThrowsValidationException() {
        String csvData = """
                title,imageFilePath,ceremonyTitle
                Award 1,/path/to/image,Ceremony 1
                """;
        assertThrows(ValidationException.class, () -> awardService.processCsv(csvData));
    }

    // Test Group: Valid Single Ceremony Processing

    @Test
    public void testProcessCsv_whenSingleCeremonyWithSingleAwardAndAllFields_thenReturnsAllFieldsMapped() {
        String csvData = CSV_HEADER +
                "Top Shooter,Best shooter award,Annual top shooter description,Overall,ipsc|hpsc,2026-04-10,awards/top-shooter,IPSC Gala 2026,Annual gala summary,Gala description,Gala Category,gala|annual,Jane Doe,John Roe,Sam Poe,jane.png,john.png,sam.png\n";

        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() ->
                awardService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<AwardCeremonyResponse> ceremonies = responseHolder.getAwardCeremonies();
        assertEquals(1, ceremonies.size());

        AwardCeremonyResponse ceremony = ceremonies.getFirst();
        assertNotNull(ceremony.getUuid());
        assertEquals("IPSC Gala 2026", ceremony.getTitle());
        assertEquals("Annual gala summary", ceremony.getSummary());
        assertEquals("Gala description", ceremony.getDescription());
        assertEquals("Gala Category", ceremony.getCategory());
        assertEquals(List.of("gala", "annual"), ceremony.getTags());
        assertEquals(LocalDate.of(2026, 4, 10), ceremony.getDate());
        assertEquals("awards/top-shooter", ceremony.getImageFilePath());

        assertEquals(1, ceremony.getAwards().size());
        AwardResponse award = ceremony.getAwards().getFirst();
        assertNotNull(award.getUuid());
        assertEquals("Top Shooter", award.getTitle());
        assertEquals("Best shooter award", award.getSummary());
        assertEquals("Annual top shooter description", award.getDescription());
        assertEquals("Overall", award.getCategory());
        assertEquals(List.of("ipsc", "hpsc"), award.getTags());
        assertEquals("Jane Doe", award.getFirstPlace().getName());
        assertEquals("jane.png", award.getFirstPlace().getImageFilePath());
        assertEquals("John Roe", award.getSecondPlace().getName());
        assertEquals("john.png", award.getSecondPlace().getImageFilePath());
        assertEquals("Sam Poe", award.getThirdPlace().getName());
        assertEquals("sam.png", award.getThirdPlace().getImageFilePath());
    }

    @Test
    public void testProcessCsv_whenSingleCeremonyWithMultipleAwards_thenGroupsAllAwardsUnderOneCeremony() {
        String csvData = CSV_HEADER +
                "Top Shooter,,,Overall,,2026-04-10,awards/,Club Gala 2026,,,,,Jane Doe,John Roe,Sam Poe,,,\n" +
                "Best Lady,,,Ladies,,2026-04-10,awards/,Club Gala 2026,,,,,Alice Brown,Carol White,Dana Green,,,\n" +
                "Junior Award,,,Junior,,2026-04-10,awards/,Club Gala 2026,,,,,Tom Young,,,,,\n";

        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() ->
                awardService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<AwardCeremonyResponse> ceremonies = responseHolder.getAwardCeremonies();
        assertEquals(1, ceremonies.size());

        AwardCeremonyResponse ceremony = ceremonies.getFirst();
        assertEquals("Club Gala 2026", ceremony.getTitle());
        assertEquals(3, ceremony.getAwards().size());

        assertEquals("Top Shooter", ceremony.getAwards().getFirst().getTitle());
        assertEquals("Jane Doe", ceremony.getAwards().getFirst().getFirstPlace().getName());
        assertEquals("Best Lady", ceremony.getAwards().get(1).getTitle());
        assertEquals("Alice Brown", ceremony.getAwards().get(1).getFirstPlace().getName());
        assertEquals("Junior Award", ceremony.getAwards().get(2).getTitle());
        assertEquals("Tom Young", ceremony.getAwards().get(2).getFirstPlace().getName());
    }

    @Test
    public void testProcessCsv_whenHeaderOnlyWithNoDataRows_thenReturnsEmptyCeremonyList() {
        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() ->
                awardService.processCsv(CSV_HEADER));

        assertNotNull(responseHolder);
        assertTrue(responseHolder.getAwardCeremonies().isEmpty());
    }

    // Test Group: Valid Multiple Ceremonies Processing

    @Test
    public void testProcessCsv_whenMultipleCeremoniesProvided_thenGroupsAwardsByCeremonyTitle() {
        String csvData = """
                title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Award A1,,,,,,,Ceremony Alpha,,,,,Alice,Bob,,,,
                Award A2,,,,,,,Ceremony Alpha,,,,,Carol,Dan,,,,
                Award B1,,,,,,,Ceremony Beta,,,,,Eve,Frank,,,,
                """;

        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() ->
                awardService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<AwardCeremonyResponse> ceremonies = responseHolder.getAwardCeremonies();
        assertEquals(2, ceremonies.size());

        AwardCeremonyResponse alpha = ceremonies.getFirst();
        assertEquals("Ceremony Alpha", alpha.getTitle());
        assertEquals(2, alpha.getAwards().size());
        assertEquals("Award A1", alpha.getAwards().getFirst().getTitle());
        assertEquals("Alice", alpha.getAwards().getFirst().getFirstPlace().getName());
        assertEquals("Award A2", alpha.getAwards().get(1).getTitle());
        assertEquals("Carol", alpha.getAwards().get(1).getFirstPlace().getName());

        AwardCeremonyResponse beta = ceremonies.get(1);
        assertEquals("Ceremony Beta", beta.getTitle());
        assertEquals(1, beta.getAwards().size());
        assertEquals("Award B1", beta.getAwards().getFirst().getTitle());
        assertEquals("Eve", beta.getAwards().getFirst().getFirstPlace().getName());
    }

    @Test
    public void testProcessCsv_whenSameCeremonyTitleDiffersByCaseButIsConsecutive_thenGroupedAsSameCeremony() {
        String csvData = """
                title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Award 1,,,,,,,Club Gala,,,,,Jane,,,,,
                Award 2,,,,,,,CLUB GALA,,,,,John,,,,,
                """;

        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() ->
                awardService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<AwardCeremonyResponse> ceremonies = responseHolder.getAwardCeremonies();
        assertEquals(1, ceremonies.size());
        assertEquals(2, ceremonies.getFirst().getAwards().size());
        assertEquals("Award 1", ceremonies.getFirst().getAwards().getFirst().getTitle());
        assertEquals("Award 2", ceremonies.getFirst().getAwards().get(1).getTitle());
    }

    @Test
    public void testProcessCsv_whenSameCeremonyTitleRepeatsNonConsecutively_thenCreatesSeparateGroups() {
        String csvData = """
                title,summary,description,category,tags,date,imageFilePath,ceremonyTitle,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceName,secondPlaceName,thirdPlaceName,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Award A1,,,,,,,Ceremony A,,,,,Alice,,,,,
                Award B1,,,,,,,Ceremony B,,,,,Bob,,,,,
                Award A2,,,,,,,Ceremony A,,,,,Carol,,,,,
                """;

        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() ->
                awardService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<AwardCeremonyResponse> ceremonies = responseHolder.getAwardCeremonies();
        assertEquals(3, ceremonies.size());

        assertEquals("Ceremony A", ceremonies.getFirst().getTitle());
        assertEquals(1, ceremonies.getFirst().getAwards().size());
        assertEquals("Award A1", ceremonies.getFirst().getAwards().getFirst().getTitle());

        assertEquals("Ceremony B", ceremonies.get(1).getTitle());
        assertEquals(1, ceremonies.get(1).getAwards().size());
        assertEquals("Award B1", ceremonies.get(1).getAwards().getFirst().getTitle());

        assertEquals("Ceremony A", ceremonies.get(2).getTitle());
        assertEquals(1, ceremonies.get(2).getAwards().size());
        assertEquals("Award A2", ceremonies.get(2).getAwards().getFirst().getTitle());
    }

    // Test Group: CSV Field Parsing

    @Test
    public void testProcessCsv_whenColumnsAreReordered_thenMapsAllFieldsCorrectly() {
        String csvData = """
                ceremonyTitle,firstPlaceName,secondPlaceName,thirdPlaceName,title,imageFilePath,date,summary,description,category,tags,ceremonySummary,ceremonyDescription,ceremonyCategory,ceremonyTags,firstPlaceImageFileName,secondPlaceImageFileName,thirdPlaceImageFileName
                Annual Gala,Gold Winner,Silver Winner,Bronze Winner,Best Shot,awards/best,2026-06-15,Shot summary,Shot description,Precision,precision|accuracy,Gala summary,Gala description,Elite,elite|prestige,gold.png,silver.png,bronze.png
                """;

        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() ->
                awardService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<AwardCeremonyResponse> ceremonies = responseHolder.getAwardCeremonies();
        assertEquals(1, ceremonies.size());

        AwardCeremonyResponse ceremony = ceremonies.getFirst();
        assertEquals("Annual Gala", ceremony.getTitle());
        assertEquals("Gala summary", ceremony.getSummary());
        assertEquals("Gala description", ceremony.getDescription());
        assertEquals("Elite", ceremony.getCategory());
        assertEquals(List.of("elite", "prestige"), ceremony.getTags());
        assertEquals(LocalDate.of(2026, 6, 15), ceremony.getDate());
        assertEquals("awards/best", ceremony.getImageFilePath());

        AwardResponse award = ceremony.getAwards().getFirst();
        assertEquals("Best Shot", award.getTitle());
        assertEquals("Shot summary", award.getSummary());
        assertEquals("Shot description", award.getDescription());
        assertEquals("Precision", award.getCategory());
        assertEquals(List.of("precision", "accuracy"), award.getTags());
        assertEquals("Gold Winner", award.getFirstPlace().getName());
        assertEquals("gold.png", award.getFirstPlace().getImageFilePath());
        assertEquals("Silver Winner", award.getSecondPlace().getName());
        assertEquals("silver.png", award.getSecondPlace().getImageFilePath());
        assertEquals("Bronze Winner", award.getThirdPlace().getName());
        assertEquals("bronze.png", award.getThirdPlace().getImageFilePath());
    }

    @Test
    public void testProcessCsv_whenTagsUsePipeSeparator_thenParsesEachTagAsListEntry() {
        String csvData = CSV_HEADER +
                "Podium Award,,,,ipsc|hpsc|production,,,Club Awards 2026,,,,gala|annual,Jane Doe,,,,,\n";

        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() ->
                awardService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<AwardCeremonyResponse> ceremonies = responseHolder.getAwardCeremonies();
        assertEquals(1, ceremonies.size());

        AwardCeremonyResponse ceremony = ceremonies.getFirst();
        assertEquals("Club Awards 2026", ceremony.getTitle());
        assertEquals(List.of("gala", "annual"), ceremony.getTags());

        AwardResponse award = ceremony.getAwards().getFirst();
        assertEquals("Podium Award", award.getTitle());
        assertEquals(List.of("ipsc", "hpsc", "production"), award.getTags());
    }

    @Test
    public void testProcessCsv_whenDateFieldIsProvided_thenParsesDateAsLocalDate() {
        String csvData = CSV_HEADER +
                "Annual Trophy,,,,,2026-11-20,,Trophy Ceremony,,,,,Best Shooter,,,,,\n";

        AwardCeremonyResponseHolder responseHolder = assertDoesNotThrow(() ->
                awardService.processCsv(csvData));

        assertNotNull(responseHolder);
        List<AwardCeremonyResponse> ceremonies = responseHolder.getAwardCeremonies();
        assertEquals(1, ceremonies.size());

        AwardCeremonyResponse ceremony = ceremonies.getFirst();
        assertEquals("Trophy Ceremony", ceremony.getTitle());
        assertEquals(LocalDate.of(2026, 11, 20), ceremony.getDate());
    }
}