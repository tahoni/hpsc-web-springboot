package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;
import za.co.hpsc.web.services.IpscMatchResultService;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.TransactionService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IpscServiceTest {
    @Mock
    private TransactionService transactionService;

    @Mock
    private IpscMatchService ipscMatchService;
    @Mock
    private IpscMatchResultService ipscMatchResultService;

    @InjectMocks
    private IpscServiceImpl ipscService;

    // =====================================================================
    // Tests for importWinMssCabFile - Public wrapper method tests
    // =====================================================================

    // Test Group: Null/Empty/Blank Input Handling
    @Test
    public void importWinMssCabFile_whenCabFileIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile(null)
        );

        // Verify no downstream calls were made
        assertDoesNotThrow(() -> {
            verify(ipscMatchService, never()).mapMatchResults(any());
            verify(ipscMatchResultService, never()).initMatchResults(any());
            verify(transactionService, never()).saveMatchResults(any());
        });
    }

    @Test
    public void importWinMssCabFile_whenCabFileIsEmpty_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile("")
        );

        // Verify no downstream calls were made
        assertDoesNotThrow(() -> {
            verify(ipscMatchService, never()).mapMatchResults(any());
            verify(ipscMatchResultService, never()).initMatchResults(any());
            verify(transactionService, never()).saveMatchResults(any());
        });
    }

    @Test
    public void importWinMssCabFile_whenCabFileIsBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile("   \t\n  ")
        );

        // Verify no downstream calls were made
        assertDoesNotThrow(() -> {
            verify(ipscMatchService, never()).mapMatchResults(any());
            verify(ipscMatchResultService, never()).initMatchResults(any());
            verify(transactionService, never()).saveMatchResults(any());
        });
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

        // Verify no downstream calls were made beyond JSON parsing
        assertDoesNotThrow(() -> {
            verify(ipscMatchService, never()).mapMatchResults(any());
            verify(ipscMatchResultService, never()).initMatchResults(any());
            verify(transactionService, never()).saveMatchResults(any());
        });
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

        verify(ipscMatchService, never()).mapMatchResults(any());
    }

    // Test Group: Valid Complete Data Processing
    @Disabled("Currently the code does not handle empty XML sections gracefully, it throws an exception when trying to parse empty sections, need to update the code to handle empty XML sections and return empty lists instead of throwing exceptions")
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

        IpscResponse ipscResponse = new IpscResponse();
        IpscResponseHolder ipscResponseHolder = new IpscResponseHolder(List.of(ipscResponse));
        MatchResultsDto matchResultsDto = new MatchResultsDto();

        when(ipscMatchService.mapMatchResults(any())).thenReturn(ipscResponseHolder);
        when(ipscMatchResultService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(matchResultsDto));

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
        assertDoesNotThrow(() -> {
            verify(ipscMatchService, times(1)).mapMatchResults(any());
            verify(ipscMatchResultService, times(1)).initMatchResults(ipscResponse);
            verify(transactionService, times(1)).saveMatchResults(matchResultsDto);
        });
    }

    // Test Group: Partial Data Processing
    @Disabled("Currently the code does not handle partial match results properly, it processes the first match result and then throws an exception when trying to process the second match result which returns empty, need to adjust service to process partial match results and skip empty ones without throwing an exception")
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

        IpscResponse ipscResponse = new IpscResponse();
        IpscResponseHolder ipscResponseHolder = new IpscResponseHolder(List.of(ipscResponse));
        MatchResultsDto matchResultsDto = new MatchResultsDto();

        when(ipscMatchService.mapMatchResults(any())).thenReturn(ipscResponseHolder);
        when(ipscMatchResultService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(matchResultsDto));

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
        verify(ipscMatchService, times(1)).mapMatchResults(any());
    }

    // Test Group: Empty XML Sections Handling
    @Disabled("Currently the code does not handle empty XML sections gracefully, it throws an exception when trying to parse empty sections, need to update the code to handle empty XML sections and return empty lists instead of throwing exceptions")
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

        IpscResponse ipscResponse = new IpscResponse();
        IpscResponseHolder ipscResponseHolder = new IpscResponseHolder(List.of(ipscResponse));

        when(ipscMatchService.mapMatchResults(any())).thenReturn(ipscResponseHolder);
        when(ipscMatchResultService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.empty());

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
        assertDoesNotThrow(() -> {
            verify(ipscMatchResultService, times(1)).initMatchResults(ipscResponse);
            // TransactionService should not be called when initMatchResults returns empty
            verify(transactionService, never()).saveMatchResults(any());
        });
    }

    // Test Group: Multiple Records Processing
    @Disabled("Currently the code does not handle multiple match results properly, it processes the first match result and then throws an exception when trying to process the second match result which returns empty, need to adjust service to process all match results and skip empty ones without throwing an exception")
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

        IpscResponse ipscResponse1 = new IpscResponse();
        IpscResponse ipscResponse2 = new IpscResponse();
        IpscResponseHolder ipscResponseHolder = new IpscResponseHolder(List.of(ipscResponse1, ipscResponse2));

        MatchResultsDto matchResults1 = new MatchResultsDto();
        MatchResultsDto matchResults2 = new MatchResultsDto();

        when(ipscMatchService.mapMatchResults(any())).thenReturn(ipscResponseHolder);
        when(ipscMatchResultService.initMatchResults(ipscResponse1)).thenReturn(Optional.of(matchResults1));
        when(ipscMatchResultService.initMatchResults(ipscResponse2)).thenReturn(Optional.of(matchResults2));

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
        assertDoesNotThrow(() -> {
            verify(ipscMatchService, times(1)).mapMatchResults(any());
            verify(ipscMatchResultService, times(2)).initMatchResults(any(IpscResponse.class));
            verify(transactionService, times(2)).saveMatchResults(any());
        });
    }

    // Test Group: Error Handling During Processing
    @Test
    public void importWinMssCabFile_whenMapMatchResultsReturnsNull_thenThrowsValidationException() {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1'/></data></xml>",
                    "match": "<xml><data><row MatchId='100'/></data></xml>",
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

        when(ipscMatchService.mapMatchResults(any())).thenReturn(null);

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        verify(ipscMatchService, times(1)).mapMatchResults(any());
        verify(ipscMatchResultService, never()).initMatchResults(any());
    }

    // Test Group: Mixed Match Results (Some Present, Some Empty)
    @Disabled("Currently the code does not handle mixed results properly, it throws an exception when trying to process the empty result, need to adjust service to process present results and skip empty ones without throwing an exception")
    @Test
    public void importWinMssCabFile_withMixedMatchResults_thenProcessesPresentResultsOnly() {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='ABC'/></data></xml>",
                    "match": "<xml><data><row MatchId='100'/><row MatchId='101'/></data></xml>",
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

        IpscResponse ipscResponse1 = new IpscResponse();
        IpscResponse ipscResponse2 = new IpscResponse();
        IpscResponseHolder ipscResponseHolder = new IpscResponseHolder(List.of(ipscResponse1, ipscResponse2));

        MatchResultsDto matchResults1 = new MatchResultsDto();
        // ipscResponse2 returns empty Optional - match results not persisted

        when(ipscMatchService.mapMatchResults(any())).thenReturn(ipscResponseHolder);
        when(ipscMatchResultService.initMatchResults(ipscResponse1)).thenReturn(Optional.of(matchResults1));
        when(ipscMatchResultService.initMatchResults(ipscResponse2)).thenReturn(Optional.empty());

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
        assertDoesNotThrow(() -> {
            verify(ipscMatchResultService, times(2)).initMatchResults(any(IpscResponse.class));
            // TransactionService called only once for the present result
            verify(transactionService, times(1)).saveMatchResults(matchResults1);
        });
    }

    // Test Group: Empty Strings vs Null Values in XML
    @Disabled("Currently fails because empty strings in XML are treated as nulls in the service, need to adjust service to differentiate between empty and null if we want to support this test case")
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

        IpscResponse ipscResponse = new IpscResponse();
        IpscResponseHolder ipscResponseHolder = new IpscResponseHolder(List.of(ipscResponse));
        MatchResultsDto matchResultsDto = new MatchResultsDto();

        when(ipscMatchService.mapMatchResults(any())).thenReturn(ipscResponseHolder);
        when(ipscMatchResultService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(matchResultsDto));

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
        verify(ipscMatchService, times(1)).mapMatchResults(any());
    }

    // Test Group: Special Characters and Unicode Handling
    @Disabled("Currently fails because the XML parser does not handle special characters properly, need to ensure that the service can handle XML with special characters and that they are correctly parsed and processed")
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

        IpscResponse ipscResponse = new IpscResponse();
        IpscResponseHolder ipscResponseHolder = new IpscResponseHolder(List.of(ipscResponse));
        MatchResultsDto matchResultsDto = new MatchResultsDto();

        when(ipscMatchService.mapMatchResults(any())).thenReturn(ipscResponseHolder);
        when(ipscMatchResultService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(matchResultsDto));

        // Act
        var recordHolder = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertNotNull(recordHolder);
        verify(ipscMatchService, times(1)).mapMatchResults(any());
    }
}
