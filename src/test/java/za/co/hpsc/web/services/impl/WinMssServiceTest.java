package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDtoHolder;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.MatchResultService;
import za.co.hpsc.web.services.TransactionService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WinMssServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private IpscMatchService ipscMatchService;
    @Mock
    private MatchResultService matchResultService;

    @InjectMocks
    private WinMssServiceImpl winMssService;

    @Test
    void testImportWinMssCabFile_withValidCabFile_thenReturnsMatches() throws Exception {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='ABC' Club='Test Club'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match'/></data></xml>",
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
        when(matchResultService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(matchResultsDto));

        // Act
        MatchResultsDtoHolder response = winMssService.importWinMssCabFile(cabFileContent);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getMatches().size());
        assertEquals(matchResultsDto, response.getMatches().getFirst());

        verify(ipscMatchService, times(1)).mapMatchResults(any());
        verify(matchResultService, times(1)).initMatchResults(ipscResponse);
        verify(transactionService, times(1)).saveMatchResults(matchResultsDto);
    }

    @Test
    void testImportWinMssCabFile_withNullCabFileContent_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                winMssService.importWinMssCabFile(null)
        );

        // Assert
        verify(ipscMatchService, never()).mapMatchResults(any());
        verify(matchResultService, never()).initMatchResults(any());
        verify(transactionService, never()).saveMatchResults(any());
    }

    @Test
    void testImportWinMssCabFile_withEmptyCabFileContent_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                winMssService.importWinMssCabFile("")
        );

        // Assert
        verify(ipscMatchService, never()).mapMatchResults(any());
        verify(matchResultService, never()).initMatchResults(any());
        verify(transactionService, never()).saveMatchResults(any());
    }

    @Test
    void testImportWinMssCabFile_withBlankCabFileContent_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                winMssService.importWinMssCabFile("   ")
        );

        verify(ipscMatchService, never()).mapMatchResults(any());
        verify(matchResultService, never()).initMatchResults(any());
        verify(transactionService, never()).saveMatchResults(any());
    }

    @Test
    void testImportWinMssCabFile_withNullResponseHolder_thenThrowsValidationException() {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='ABC' Club='Test Club'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match'/></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        when(ipscMatchService.mapMatchResults(any())).thenReturn(null);

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                winMssService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        verify(ipscMatchService, times(1)).mapMatchResults(any());
        verify(matchResultService, never()).initMatchResults(any());
        verify(transactionService, never()).saveMatchResults(any());
    }

    @Test
    void testImportWinMssCabFile_withMultipleMatches_thenProcessesAllMatches() throws Exception {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='ABC' Club='Test Club'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match 1'/><row MatchId='101' MatchName='Test Match 2'/></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        IpscResponse ipscResponse1 = new IpscResponse();
        IpscResponse ipscResponse2 = new IpscResponse();
        IpscResponseHolder ipscResponseHolder = new IpscResponseHolder(List.of(ipscResponse1, ipscResponse2));

        MatchResultsDto matchResults1 = new MatchResultsDto();
        MatchResultsDto matchResults2 = new MatchResultsDto();

        when(ipscMatchService.mapMatchResults(any())).thenReturn(ipscResponseHolder);
        when(matchResultService.initMatchResults(ipscResponse1)).thenReturn(Optional.of(matchResults1));
        when(matchResultService.initMatchResults(ipscResponse2)).thenReturn(Optional.of(matchResults2));

        // Act
        MatchResultsDtoHolder response = winMssService.importWinMssCabFile(cabFileContent);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getMatches().size());
        assertTrue(response.getMatches().contains(matchResults1));
        assertTrue(response.getMatches().contains(matchResults2));

        verify(ipscMatchService, times(1)).mapMatchResults(any());
        verify(matchResultService, times(2)).initMatchResults(any(IpscResponse.class));
        verify(transactionService, times(1)).saveMatchResults(matchResults1);
        verify(transactionService, times(1)).saveMatchResults(matchResults2);
    }

    @Test
    void testImportWinMssCabFile_withEmptyMatchResults_thenSkipsSaving() throws Exception {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='ABC' Club='Test Club'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match'/></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        IpscResponse ipscResponse = new IpscResponse();
        IpscResponseHolder ipscResponseHolder = new IpscResponseHolder(List.of(ipscResponse));

        when(ipscMatchService.mapMatchResults(any())).thenReturn(ipscResponseHolder);
        when(matchResultService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.empty());

        // Act
        MatchResultsDtoHolder response = winMssService.importWinMssCabFile(cabFileContent);

        // Assert
        assertNotNull(response);
        assertTrue(response.getMatches().isEmpty());

        verify(ipscMatchService, times(1)).mapMatchResults(any());
        verify(matchResultService, times(1)).initMatchResults(ipscResponse);
        verify(transactionService, never()).saveMatchResults(any());
    }

    @Test
    void testImportWinMssCabFile_withInvalidJson_thenThrowsFatalException() {
        // Arrange
        String invalidCabFileContent = "This is not valid JSON content";

        // Act & Assert
        assertThrows(FatalException.class, () ->
                winMssService.importWinMssCabFile(invalidCabFileContent)
        );

        // Assert
        verify(ipscMatchService, never()).mapMatchResults(any());
        verify(matchResultService, never()).initMatchResults(any());
        verify(transactionService, never()).saveMatchResults(any());
    }

    @Test
    void testImportWinMssCabFile_withInvalidXml_thenThrowsValidationException() {
        // Arrange
        String invalidCabFileContent = """
                {
                    "club": "This is not valid XML content",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match'/></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                winMssService.importWinMssCabFile(invalidCabFileContent)
        );

        // Assert
        verify(ipscMatchService, never()).mapMatchResults(any());
        verify(matchResultService, never()).initMatchResults(any());
        verify(transactionService, never()).saveMatchResults(any());
    }

    @Test
    void testImportWinMssCabFile_withCabFile_thenReturnsMatch() throws Exception {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data><row ClubId='1' ClubCode='ABC' Club='Test Club'/></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match'/></data></xml>",
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
        when(matchResultService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(matchResultsDto));

        // Act
        MatchResultsDtoHolder response = winMssService.importWinMssCabFile(cabFileContent);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getMatches().size());
        assertEquals(matchResultsDto, response.getMatches().getFirst());

        verify(ipscMatchService, times(1)).mapMatchResults(any());
        verify(matchResultService, times(1)).initMatchResults(ipscResponse);
        verify(transactionService, times(1)).saveMatchResults(matchResultsDto);
    }
}
