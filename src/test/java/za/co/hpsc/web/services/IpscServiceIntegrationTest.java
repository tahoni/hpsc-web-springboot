package za.co.hpsc.web.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.holders.records.IpscMatchRecordHolder;
import za.co.hpsc.web.models.ipsc.records.CompetitorRecord;
import za.co.hpsc.web.models.ipsc.records.IpscMatchRecord;
import za.co.hpsc.web.models.ipsc.records.MatchCompetitorOverallResultsRecord;
import za.co.hpsc.web.models.ipsc.records.MatchCompetitorStageResultRecord;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.impl.IpscServiceImpl;
import za.co.hpsc.web.services.impl.TransactionServiceImpl;
import za.co.hpsc.web.services.impl.TransformationServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// TODO: add all fields
// TODO: add tests for enum value reads and writes
// TODO: standard naming
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class IpscServiceIntegrationTest {
    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private IpscService ipscService;

    @Bean
    public TransformationService ipscMatchService(ClubEntityService clubEntityService,
                                                  MatchEntityService matchEntityService,
                                                  MatchStageEntityService matchStageEntityService,
                                                  CompetitorEntityService competitorEntityService,
                                                  MatchCompetitorEntityService matchCompetitorEntityService,
                                                  MatchStageCompetitorEntityService matchStageCompetitorEntityService) {
        return new TransformationServiceImpl(clubEntityService, matchEntityService,
                matchStageEntityService, competitorEntityService,
                matchCompetitorEntityService, matchStageCompetitorEntityService);
    }

    @Bean
    public TransactionService transactionService(ClubRepository clubRepository,
                                                 IpscMatchRepository ipscMatchRepository,
                                                 IpscMatchStageRepository ipscMatchStageRepository,
                                                 MatchCompetitorRepository matchCompetitorRepository,
                                                 MatchStageCompetitorRepository matchStageCompetitorRepository) {
        return new TransactionServiceImpl(platformTransactionManager, clubRepository,
                competitorRepository, ipscMatchRepository, ipscMatchStageRepository,
                matchCompetitorRepository, matchStageCompetitorRepository);
    }

    @Bean
    public IpscService ipscService(TransformationService transformationService,
                                   DomainService domainService,
                                   TransactionService transactionService) {
        return new IpscServiceImpl(transformationService, domainService,
                transactionService);
    }

    // Test Group: importWinMssCabFile - Integration tests
    // Test Group: Null/Empty/Blank Input Handling
    @Test
    public void importWinMssCabFile_whenCabFileIsNull_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile(null)
        );
    }

    @Test
    public void importWinMssCabFile_whenCabFileIsEmpty_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile("")
        );
    }

    @Test
    public void importWinMssCabFile_whenCabFileIsBlank_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile("   \t\n  ")
        );
    }

    // Test Group: Invalid JSON Format Handling
    @Test
    public void importWinMssCabFile_whenJsonIsInvalid_thenThrowsFatalException() {
        String invalidJson = "This is not valid JSON at all";

        assertThrows(FatalException.class, () ->
                ipscService.importWinMssCabFile(invalidJson)
        );
    }

    @Test
    public void importWinMssCabFile_whenJsonHasMissingBraces_thenThrowsFatalException() {
        String malformedJson = """
                {
                    "club": "<xml><data><row ClubId='1'/></data></xml>"
                """;

        assertThrows(FatalException.class, () ->
                ipscService.importWinMssCabFile(malformedJson)
        );
    }

    // Test Group: Valid Complete Data Processing
    @Test
    public void importWinMssCabFile_withCompleteValidData_thenReturnsIpscMatchRecordHolder() {
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='BBB' Club='Test Club' Contact='Admin'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match' MatchDt='2025-09-06T10:00:00' Chrono='True' ClubId='1'/></data></xml>",
                    "stage": "<xml><data><row StageId='200' StageName='Test Stage' MatchId='100'/></data></xml>",
                    "tag": "<xml><data><row TagId='10' Tag='Test Tag'/></data></xml>",
                    "member": "<xml><data><row MemberId='50' Firstname='John' Lastname='Doe' Register='True' DOB='1973-02-17T00:00:00' IcsAlias='15000'/></data></xml>",
                    "classify": "<xml><data><row MemberId='50' DivisionId='1' IntlId='5000' NatlId='500'/></data></xml>",
                    "enrolled": "<xml><data><row MemberId='50' CompId='500' MatchId='100' RefNo='BBB'/></data></xml>",
                    "squad": "<xml><data><row SquadId='20' Squad='Squad A' MatchId='100'/></data></xml>",
                    "team": "<xml><data><row TeamId='20' Team='Team A' MatchId='100'/></data></xml>",
                    "score": "<xml><data><row MemberId='50' StageId='200' MatchId='100' HitFactor='6.08433734939759' ShootTime='0.87' FinalScore='101'/></data></xml>"
                }
                """;

        List<IpscMatchRecordHolder> recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        assertNotNull(recordHolder);
        assertFalse(recordHolder.isEmpty());
        assertNotNull(recordHolder.getFirst());
        IpscMatchRecordHolder firstRecord = recordHolder.getFirst();

        assertFalse(firstRecord.matches().isEmpty());
        IpscMatchRecord matchRecord = firstRecord.matches().getFirst();
        assertEquals("Test Match", matchRecord.name());
        assertEquals("2025-09-06 10:00", matchRecord.scheduledDate());
        assertEquals("Test Club (BBB)", matchRecord.clubName());

        assertFalse(matchRecord.competitors().isEmpty());
        CompetitorRecord competitorRecord = matchRecord.competitors().getFirst();
        assertEquals("John", competitorRecord.firstName());
        assertEquals("Doe", competitorRecord.lastName());
        assertEquals("1973-02-17", competitorRecord.dateOfBirth());
        assertEquals("", competitorRecord.middleNames());
        assertNull(competitorRecord.sapsaNumber());
        assertEquals("15000", competitorRecord.competitorNumber());

        // TODO: re-enable when results are properly mapped and returned in the record
        MatchCompetitorOverallResultsRecord overallResultRecord = competitorRecord.results().overall();
        assertEquals("101.0000", overallResultRecord.matchPoints());

        assertFalse(competitorRecord.results().stages().isEmpty());
        MatchCompetitorStageResultRecord firstStageResultRecord = competitorRecord.results().stages().getFirst();
        assertEquals("Test Stage", firstStageResultRecord.stageName());
        assertEquals("0.87", firstStageResultRecord.time());
        assertEquals("6.0843", firstStageResultRecord.hitFactor());
        assertEquals("101.0000", firstStageResultRecord.stagePoints());
    }

    // Test Group: Valid Complete Data Processing
    @Test
    public void importWinMssCabFile_withCompleteValidDataClubNullAndFilter_thenReturnsIpscMatchRecordHolder() {
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='BBB' Club='Test Club' Contact='Admin'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match' MatchDt='2025-09-06T10:00:00' Chrono='True' ClubId='1'/></data></xml>",
                    "stage": "<xml><data><row StageId='200' StageName='Test Stage' MatchId='100'/></data></xml>",
                    "tag": "<xml><data><row TagId='10' Tag='Test Tag'/></data></xml>",
                    "member": "<xml><data><row MemberId='50' Firstname='John' Lastname='Doe' Register='True' DOB='1973-02-17T00:00:00' IcsAlias='1500'/></data></xml>",
                    "classify": "<xml><data><row MemberId='50' DivisionId='1' IntlId='5000' NatlId='500'/></data></xml>",
                    "enrolled": "<xml><data><row MemberId='50' CompId='500' MatchId='100'/></data></xml>",
                    "squad": "<xml><data><row SquadId='20' Squad='Squad A' MatchId='100'/></data></xml>",
                    "team": "<xml><data><row TeamId='20' Team='Team A' MatchId='100'/></data></xml>",
                    "score": "<xml><data><row MemberId='50' StageId='200' MatchId='100' HitFactor='6.08433734939759' FinalScore='101'/></data></xml>"
                }
                """;

        List<IpscMatchRecordHolder> recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        assertNotNull(recordHolder);
        assertFalse(recordHolder.isEmpty());
        assertNotNull(recordHolder.getFirst());
        IpscMatchRecordHolder firstRecord = recordHolder.getFirst();

        assertFalse(firstRecord.matches().isEmpty());
        IpscMatchRecord matchRecord = firstRecord.matches().getFirst();
        assertEquals("Test Match", matchRecord.name());
        assertEquals("2025-09-06 10:00", matchRecord.scheduledDate());
        assertEquals("Test Club (BBB)", matchRecord.clubName());

        assertTrue(matchRecord.competitors().isEmpty());
    }

    // Test Group: Partial Data Processing
    @Test
    public void importWinMssCabFile_withPartialMatchData_thenProcessesSuccessfully() {
        // Arrange - Only match required fields, other sections mostly empty
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='ABC'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Partial Match'/></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "classify": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "team": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        assertNotNull(recordHolder);
    }

    // Test Group: Empty XML Sections Handling
    @Test
    public void importWinMssCabFile_withEmptyXmlSections_thenProcessesWithEmptyData() {
        String cabFileContent = """
                {
                    "club": "<xml><data></data></xml>",
                    "match": "<xml><data></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "classify": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "team": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        assertNotNull(recordHolder);
    }

    // Test Group: Multiple Records Processing
    @Test
    public void importWinMssCabFile_withMultipleMatches_thenProcessesAllMatches() {
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='ABC' Club='Club A'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Match 1'/><row MatchId='101' MatchName='Match 2'/></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "classify": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "team": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        assertNotNull(recordHolder);
    }

    // Test Group: Error Handling During Processing
    @Test
    public void importWinMssCabFile_whenJsonIsInvalidStructure_thenDoesNotThrowValidationException() {
        String cabFileContent = """
                {
                    "club": "<xml><data></data></xml>",
                    "match": "<xml><data></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "classify": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "team": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        assertDoesNotThrow(() -> ipscService.importWinMssCabFile(cabFileContent));
    }

    // Test Group: Empty Strings vs Null Values in XML
    @Test
    public void importWinMssCabFile_withEmptyStringXmlFields_thenProcessesSuccessfully() {
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='' Club=''/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName=''/></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "classify": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "team": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        assertNotNull(recordHolder);
    }

    // Test Group: Special Characters and Unicode Handling
    @Test
    public void importWinMssCabFile_withSpecialCharactersInData_thenProcessesSuccessfully() {
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='ABC' Club='Test Club &amp; Co.'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Match &lt;2025&gt;'/></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "classify": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "team": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        assertNotNull(recordHolder);
    }
}

