package za.co.hpsc.web.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.impl.IpscMatchServiceImpl;
import za.co.hpsc.web.services.impl.IpscServiceImpl;
import za.co.hpsc.web.services.impl.TransactionServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class IpscServiceIntegrationTest {
    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private CompetitorRepository competitorRepository;
    @Autowired
    private DomainService domainService;
    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private IpscService ipscService;

    @Bean
    public IpscService winMssService(IpscMatchService ipscMatchService,
                                     IpscMatchResultService ipscMatchResultService,
                                     DomainService domainService,
                                     TransactionService transactionService) {
        return new IpscServiceImpl(ipscMatchService, ipscMatchResultService, domainService,
                transactionService);
    }

    @Bean
    public IpscMatchService ipscMatchService(TransactionService transactionService) {
        return new IpscMatchServiceImpl(transactionService);
    }

    @Bean
    public TransactionService transactionService(DomainService domainService,
                                                 ClubRepository clubRepository,
                                                 IpscMatchRepository ipscMatchRepository,
                                                 IpscMatchStageRepository ipscMatchStageRepository,
                                                 MatchCompetitorRepository matchCompetitorRepository,
                                                 MatchStageCompetitorRepository matchStageCompetitorRepository) {
        return new TransactionServiceImpl(platformTransactionManager, domainService, clubRepository,
                competitorRepository, ipscMatchRepository, ipscMatchStageRepository,
                matchCompetitorRepository, matchStageCompetitorRepository);
    }

    // =====================================================================
    // Tests for importWinMssCabFile - Integration tests
    // =====================================================================

    // Test Group: Null/Empty/Blank Input Handling
    @Test
    public void importWinMssCabFile_whenCabFileIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile(null)
        );
    }

    @Test
    public void importWinMssCabFile_whenCabFileIsEmpty_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile("")
        );
    }

    @Test
    public void importWinMssCabFile_whenCabFileIsBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile("   \t\n  ")
        );
    }

    // Test Group: Invalid JSON Format Handling
    @Test
    public void importWinMssCabFile_whenJsonIsInvalid_thenThrowsFatalException() {
        // Arrange
        String invalidJson = "This is not valid JSON at all";

        // Act & Assert
        assertThrows(FatalException.class, () ->
                ipscService.importWinMssCabFile(invalidJson)
        );
    }

    @Test
    public void importWinMssCabFile_whenJsonHasMissingBraces_thenThrowsFatalException() {
        // Arrange
        String malformedJson = """
                {
                    "club": "<xml><data><row ClubId='1'/></data></xml>"
                """;

        // Act & Assert
        assertThrows(FatalException.class, () ->
                ipscService.importWinMssCabFile(malformedJson)
        );
    }

    // Test Group: Valid Complete Data Processing
    @Test
    public void importWinMssCabFile_withCompleteValidData_thenReturnsIpscMatchRecordHolder() {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='ABC' Club='Test Club' Contact='Admin'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match' MatchDt='2025-09-06T10:00:00' Chrono='True'/></data></xml>",
                    "stage": "<xml><data><row StageId='200' StageName='Test Stage' MatchId='100'/></data></xml>",
                    "tag": "<xml><data><row TagId='10' Tag='Test Tag'/></data></xml>",
                    "member": "<xml><data><row MemberId='50' Firstname='John' Lastname='Doe' Register='True' DOB='1973-02-17T00:00:00'/></data></xml>",
                    "classify": "<xml><data><row MemberId='50' DivisionId='1' IntlId='5000' NatlId='500'/></data></xml>",
                    "enrolled": "<xml><data><row MemberId='50' CompId='500' MatchId='100'/></data></xml>",
                    "squad": "<xml><data><row SquadId='20' Squad='Squad A' MatchId='100'/></data></xml>",
                    "team": "<xml><data><row TeamId='20' Team='Team A' MatchId='100'/></data></xml>",
                    "score": "<xml><data><row MemberId='50' StageId='200' MatchId='100' HitFactor='6.08433734939759' FinalScore='101'/></data></xml>"
                }
                """;

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
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

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
    }

    // Test Group: Empty XML Sections Handling
    @Test
    public void importWinMssCabFile_withEmptyXmlSections_thenProcessesWithEmptyData() {
        // Arrange
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

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
    }

    // Test Group: Multiple Records Processing
    @Test
    public void importWinMssCabFile_withMultipleMatches_thenProcessesAllMatches() {
        // Arrange
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

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
    }

    // Test Group: Error Handling During Processing
    @Test
    public void importWinMssCabFile_whenJsonIsInvalidStructure_thenDoesNotThrowValidationException() {
        // Arrange
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

        // Act & Assert
        assertDoesNotThrow(() -> ipscService.importWinMssCabFile(cabFileContent));
    }

    // Test Group: Empty Strings vs Null Values in XML
    @Test
    public void importWinMssCabFile_withEmptyStringXmlFields_thenProcessesSuccessfully() {
        // Arrange
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

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
    }

    // Test Group: Special Characters and Unicode Handling
    @Test
    public void importWinMssCabFile_withSpecialCharactersInData_thenProcessesSuccessfully() {
        // Arrange
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

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
    }
}

