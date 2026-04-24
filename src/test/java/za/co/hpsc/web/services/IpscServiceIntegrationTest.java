package za.co.hpsc.web.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.holders.records.IpscMatchRecordHolder;
import za.co.hpsc.web.models.ipsc.records.CompetitorRecord;
import za.co.hpsc.web.models.ipsc.records.CompetitorResultRecord;
import za.co.hpsc.web.models.ipsc.records.IpscMatchRecord;
import za.co.hpsc.web.models.ipsc.records.MatchCompetitorOverallResultsRecord;
import za.co.hpsc.web.models.ipsc.records.MatchCompetitorStageResultRecord;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.impl.IpscServiceImpl;
import za.co.hpsc.web.services.impl.TransactionServiceImpl;
import za.co.hpsc.web.services.impl.TransformationServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class IpscServiceIntegrationTest {
    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private IpscMatchRepository ipscMatchRepository;

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
    public void testImportWinMssCabFile_whenCabFileIsNull_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile(null)
        );
    }

    @Test
    public void testImportWinMssCabFile_whenCabFileIsEmpty_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile("")
        );
    }

    @Test
    public void testImportWinMssCabFile_whenCabFileIsBlank_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile("   \t\n  ")
        );
    }

    // Test Group: Invalid JSON Format Handling
    @Test
    public void testImportWinMssCabFile_whenJsonIsInvalid_thenThrowsFatalException() {
        String invalidJson = "This is not valid JSON at all";

        assertThrows(FatalException.class, () ->
                ipscService.importWinMssCabFile(invalidJson)
        );
    }

    @Test
    public void testImportWinMssCabFile_whenJsonHasMissingBraces_thenThrowsFatalException() {
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
    public void testImportWinMssCabFile_whenDataIsCompleteAndValid_thenReturnsIpscMatchRecordHolder() {
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

        MatchCompetitorOverallResultsRecord overallResultRecord = competitorRecord.results().overall();
        assertEquals("101.0000", overallResultRecord.matchPoints());

        assertFalse(competitorRecord.results().stages().isEmpty());
        MatchCompetitorStageResultRecord firstStageResultRecord = competitorRecord.results().stages().getFirst();
        assertEquals("Test Stage", firstStageResultRecord.stageName());
        assertEquals("0.87", firstStageResultRecord.time());
        assertEquals("6.0843", firstStageResultRecord.hitFactor());
        assertEquals("101.0000", firstStageResultRecord.stagePoints());
    }

    @Test
    public void testImportWinMssCabFile_whenClubDataIsProvided_thenPersistsMatchClubAndCompetitorClubMappings() {
        String matchName = "Club Mapping Match";
        String cabFileContent = buildCabFileContent(
                1102,
                2202,
                152,
                552,
                matchName,
                " Chrono='True'",
                "RefNo='BBB' DivId='4' CatId='3' MajorPF='True'"
        );

        List<IpscMatchRecordHolder> recordHolders = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        assertNotNull(recordHolders);
        assertEquals(1, recordHolders.size());
        assertFalse(recordHolders.getFirst().matches().isEmpty());

        IpscMatchRecord matchRecord = recordHolders.getFirst().matches().getFirst();
        assertEquals(matchName, matchRecord.name());
        assertEquals("Test Club (BBB)", matchRecord.clubName());

        assertFalse(matchRecord.competitors().isEmpty());
        CompetitorRecord competitorRecord = matchRecord.competitors().getFirst();
        assertEquals("Hartbeespoortdam Practical Shooting Club", competitorRecord.clubName());

        IpscMatch persistedMatch = loadSinglePersistedMatch(matchName);
        assertNotNull(persistedMatch.getClub());
        assertEquals("Test Club", persistedMatch.getClub().getName());
        assertEquals("BBB", persistedMatch.getClub().getAbbreviation());

        MatchCompetitor persistedMatchCompetitor = getOnlyMatchCompetitor(persistedMatch);
        assertEquals(ClubIdentifier.HPSC, persistedMatchCompetitor.getMatchClub());

        MatchStageCompetitor persistedMatchStageCompetitor = getOnlyMatchStageCompetitor(persistedMatch);
        assertEquals(ClubIdentifier.HPSC, persistedMatchStageCompetitor.getMatchClub());
    }

    @Test
    public void testImportWinMssCabFile_whenCompetitorsBelongToDifferentClubs_thenDefaultClubFilterKeepsOnlyHpscCompetitors() {
        String matchName = "Club Filter Match";
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='BBB' Club='Test Club' Contact='Admin'/></data></xml>",
                    "match": "<xml><data><row MatchId='1103' MatchName='%s' MatchDt='2026-04-25T09:30:00' ClubId='1' Chrono='True'/></data></xml>",
                    "stage": "<xml><data><row StageId='2203' StageName='Club Filter Stage' MatchId='1103'/></data></xml>",
                    "tag": "<xml><data><row TagId='10' Tag='Test Tag'/></data></xml>",
                    "member": "<xml><data><row MemberId='160' Firstname='Helen' Lastname='Home Club' Register='True' DOB='1991-01-10T00:00:00' IcsAlias='12346'/><row MemberId='161' Firstname='Sean' Lastname='Safari Club' Register='True' DOB='1992-02-11T00:00:00' IcsAlias='12347'/></data></xml>",
                    "classify": "<xml><data><row MemberId='160' DivisionId='4' IntlId='5000' NatlId='500'/><row MemberId='161' DivisionId='4' IntlId='5000' NatlId='500'/></data></xml>",
                    "enrolled": "<xml><data><row MemberId='160' CompId='560' MatchId='1103' RefNo='BBB' DivId='4' MajorPF='True'/><row MemberId='161' CompId='561' MatchId='1103' RefNo='AAA' DivId='4' MajorPF='True'/></data></xml>",
                    "squad": "<xml><data><row SquadId='20' Squad='Squad A' MatchId='1103'/></data></xml>",
                    "team": "<xml><data><row TeamId='20' Team='Team A' MatchId='1103'/></data></xml>",
                    "score": "<xml><data><row MemberId='160' StageId='2203' MatchId='1103' HitFactor='7.1000' ShootTime='1.20' FinalScore='105'/><row MemberId='161' StageId='2203' MatchId='1103' HitFactor='6.1000' ShootTime='1.40' FinalScore='95'/></data></xml>"
                }
                """.formatted(matchName);

        List<IpscMatchRecordHolder> recordHolders = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        assertNotNull(recordHolders);
        assertEquals(1, recordHolders.size());
        assertFalse(recordHolders.getFirst().matches().isEmpty());

        IpscMatchRecord matchRecord = recordHolders.getFirst().matches().getFirst();
        assertEquals(matchName, matchRecord.name());
        assertEquals("Test Club (BBB)", matchRecord.clubName());
        assertEquals(1, matchRecord.competitors().size());

        CompetitorRecord competitorRecord = matchRecord.competitors().getFirst();
        assertEquals("Helen", competitorRecord.firstName());
        assertEquals("Home Club", competitorRecord.lastName());
        assertEquals("Hartbeespoortdam Practical Shooting Club", competitorRecord.clubName());

        IpscMatch persistedMatch = loadSinglePersistedMatch(matchName);
        assertNotNull(persistedMatch.getClub());
        assertEquals("BBB", persistedMatch.getClub().getAbbreviation());
        assertEquals(1, persistedMatch.getMatchCompetitors().size());
        assertEquals(1, persistedMatch.getMatchStages().size());
        assertEquals(1, persistedMatch.getMatchStages().getFirst().getMatchStageCompetitors().size());
        assertEquals(ClubIdentifier.HPSC, persistedMatch.getMatchCompetitors().getFirst().getMatchClub());
        assertEquals(ClubIdentifier.HPSC,
                persistedMatch.getMatchStages().getFirst().getMatchStageCompetitors().getFirst().getMatchClub());
    }

    @Test
    public void testImportWinMssCabFile_whenEnumBackedFieldsAreProvided_thenPersistsAndReloadsMappedEnums() {
        String matchName = "Enum Mapping Match";
        String cabFileContent = buildCabFileContent(
                1100,
                2200,
                150,
                550,
                matchName,
                " FirearmId='1' Chrono='True'",
                "RefNo='BBB' DivId='4' CatId='3' MajorPF='True'"
        );

        List<IpscMatchRecordHolder> recordHolders = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        assertNotNull(recordHolders);
        assertEquals(1, recordHolders.size());
        assertFalse(recordHolders.getFirst().matches().isEmpty());

        IpscMatchRecord matchRecord = recordHolders.getFirst().matches().getFirst();
        assertEquals(matchName, matchRecord.name());
        assertEquals("Handgun", matchRecord.matchFirearmType());
        assertEquals("Club Shoot", matchRecord.matchCategory());

        assertFalse(matchRecord.competitors().isEmpty());
        CompetitorRecord competitorRecord = matchRecord.competitors().getFirst();
        assertEquals("Senior", competitorRecord.competitorCategory());

        CompetitorResultRecord competitorResultRecord = competitorRecord.results();
        assertEquals("Handgun", competitorResultRecord.firearmType());
        assertEquals("Production Division", competitorResultRecord.division());
        assertEquals("Major", competitorResultRecord.powerFactor());

        IpscMatch persistedMatch = loadSinglePersistedMatch(matchName);
        assertEquals(FirearmType.HANDGUN, persistedMatch.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, persistedMatch.getMatchCategory());

        MatchCompetitor persistedMatchCompetitor = getOnlyMatchCompetitor(persistedMatch);
        assertEquals(CompetitorCategory.SENIOR, persistedMatchCompetitor.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, persistedMatchCompetitor.getFirearmType());
        assertEquals(Division.PRODUCTION, persistedMatchCompetitor.getDivision());
        assertEquals(PowerFactor.MAJOR, persistedMatchCompetitor.getPowerFactor());

        MatchStageCompetitor persistedMatchStageCompetitor = getOnlyMatchStageCompetitor(persistedMatch);
        assertEquals(FirearmType.HANDGUN, persistedMatchStageCompetitor.getFirearmType());
        assertEquals(Division.PRODUCTION, persistedMatchStageCompetitor.getDivision());
        assertEquals(PowerFactor.MAJOR, persistedMatchStageCompetitor.getPowerFactor());
    }

    @Test
    public void testImportWinMssCabFile_whenEnumSourceValuesAreUnknown_thenUsesExpectedDefaultsAfterReload() {
        String matchName = "Enum Defaults Match";
        String cabFileContent = buildCabFileContent(
                1101,
                2201,
                151,
                551,
                matchName,
                " FirearmId='999' Chrono='True'",
                "RefNo='BBB' DivId='999' CatId='999' MajorPF='False'"
        );

        List<IpscMatchRecordHolder> recordHolders = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        assertNotNull(recordHolders);
        assertEquals(1, recordHolders.size());
        assertFalse(recordHolders.getFirst().matches().isEmpty());

        IpscMatchRecord matchRecord = recordHolders.getFirst().matches().getFirst();
        assertEquals(matchName, matchRecord.name());
        assertEquals("", matchRecord.matchFirearmType());
        assertEquals("Club Shoot", matchRecord.matchCategory());

        assertFalse(matchRecord.competitors().isEmpty());
        CompetitorRecord competitorRecord = matchRecord.competitors().getFirst();
        assertEquals("", competitorRecord.competitorCategory());

        CompetitorResultRecord competitorResultRecord = competitorRecord.results();
        assertEquals("", competitorResultRecord.firearmType());
        assertEquals("", competitorResultRecord.division());
        assertEquals("Minor", competitorResultRecord.powerFactor());

        IpscMatch persistedMatch = loadSinglePersistedMatch(matchName);
        assertNull(persistedMatch.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, persistedMatch.getMatchCategory());

        MatchCompetitor persistedMatchCompetitor = getOnlyMatchCompetitor(persistedMatch);
        assertEquals(CompetitorCategory.NONE, persistedMatchCompetitor.getCompetitorCategory());
        assertNull(persistedMatchCompetitor.getFirearmType());
        assertNull(persistedMatchCompetitor.getDivision());
        assertEquals(PowerFactor.MINOR, persistedMatchCompetitor.getPowerFactor());

        MatchStageCompetitor persistedMatchStageCompetitor = getOnlyMatchStageCompetitor(persistedMatch);
        assertNull(persistedMatchStageCompetitor.getFirearmType());
        assertNull(persistedMatchStageCompetitor.getDivision());
        assertEquals(PowerFactor.MINOR, persistedMatchStageCompetitor.getPowerFactor());
    }

    // Test Group: Valid Complete Data Processing
    @Test
    public void testImportWinMssCabFile_whenDataIsCompleteAndValidWithClubNullAndFilter_thenReturnsIpscMatchRecordHolder() {
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
    public void testImportWinMssCabFile_whenMatchDataIsPartial_thenProcessesSuccessfully() {
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
    public void testImportWinMssCabFile_whenXmlSectionsAreEmpty_thenProcessesWithEmptyData() {
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
    public void testImportWinMssCabFile_whenMultipleMatchesAreProvided_thenProcessesAllMatches() {
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
    public void testImportWinMssCabFile_whenJsonStructureIsInvalid_thenDoesNotThrowValidationException() {
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
    public void testImportWinMssCabFile_whenXmlFieldsAreEmptyStrings_thenProcessesSuccessfully() {
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
    public void testImportWinMssCabFile_whenDataContainsSpecialCharacters_thenProcessesSuccessfully() {
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

    private String buildCabFileContent(int matchId, int stageId, int memberId, int competitorId,
                                       String matchName, String matchAttributes, String enrolledAttributes) {
        return """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='BBB' Club='Test Club' Contact='Admin'/></data></xml>",
                    "match": "<xml><data><row MatchId='%d' MatchName='%s' MatchDt='2026-04-25T09:00:00' ClubId='1'%s/></data></xml>",
                    "stage": "<xml><data><row StageId='%d' StageName='Enum Stage' MatchId='%d'/></data></xml>",
                    "tag": "<xml><data><row TagId='10' Tag='Test Tag'/></data></xml>",
                    "member": "<xml><data><row MemberId='%d' Firstname='Jane' Lastname='Shooter' Register='True' DOB='1990-05-12T00:00:00' IcsAlias='12345'/></data></xml>",
                    "classify": "<xml><data><row MemberId='%d' DivisionId='4' IntlId='5000' NatlId='500'/></data></xml>",
                    "enrolled": "<xml><data><row MemberId='%d' CompId='%d' MatchId='%d' %s/></data></xml>",
                    "squad": "<xml><data><row SquadId='20' Squad='Squad A' MatchId='%d'/></data></xml>",
                    "team": "<xml><data><row TeamId='20' Team='Team A' MatchId='%d'/></data></xml>",
                    "score": "<xml><data><row MemberId='%d' StageId='%d' MatchId='%d' HitFactor='7.5000' ShootTime='1.23' FinalScore='110'/></data></xml>"
                }
                """.formatted(matchId, matchName, matchAttributes, stageId, matchId, memberId, memberId,
                memberId, competitorId, matchId, enrolledAttributes, matchId, matchId, memberId, stageId, matchId);
    }

    private IpscMatch loadSinglePersistedMatch(String matchName) {
        List<IpscMatch> persistedMatches = ipscMatchRepository.findAllByName(matchName);
        assertEquals(1, persistedMatches.size());
        return persistedMatches.getFirst();
    }

    private MatchCompetitor getOnlyMatchCompetitor(IpscMatch persistedMatch) {
        assertNotNull(persistedMatch);
        assertNotNull(persistedMatch.getMatchCompetitors());
        assertEquals(1, persistedMatch.getMatchCompetitors().size());
        return persistedMatch.getMatchCompetitors().getFirst();
    }

    private MatchStageCompetitor getOnlyMatchStageCompetitor(IpscMatch persistedMatch) {
        assertNotNull(persistedMatch);
        assertNotNull(persistedMatch.getMatchStages());
        assertEquals(1, persistedMatch.getMatchStages().size());

        IpscMatchStage persistedStage = persistedMatch.getMatchStages().getFirst();
        assertNotNull(persistedStage.getMatchStageCompetitors());
        assertEquals(1, persistedStage.getMatchStageCompetitors().size());
        return persistedStage.getMatchStageCompetitors().getFirst();
    }
}

