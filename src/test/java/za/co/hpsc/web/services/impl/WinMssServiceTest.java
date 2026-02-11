package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ControllerResponse;
import za.co.hpsc.web.models.ipsc.request.IpscRequestHolder;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.TransactionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WinMssServiceTest {

    @Mock
    private TransactionService transactionService;
    @Mock
    private IpscMatchService ipscMatchService;

    @InjectMocks
    private WinMssServiceImpl winMssService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testImportWinMssCabFile_withValidCabFile_thenReturnsSuccessResponse() {
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

        IpscResponseHolder ipscResponseHolder = new IpscResponseHolder();
        when(ipscMatchService.mapMatchResults(any(IpscRequestHolder.class)))
                .thenReturn(ipscResponseHolder);

        // Act
        ControllerResponse response = assertDoesNotThrow(() ->
                winMssService.importWinMssCabFile(cabFileContent));

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotEquals("", response.getMessage());
        assertEquals("", response.getError());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testImportWinMssCabFile_withInvalidCabFile_thenReturnsSuccessResponse() {
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

        when(ipscMatchService.mapMatchResults(any(IpscRequestHolder.class)))
                .thenReturn(null);

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                winMssService.importWinMssCabFile(cabFileContent));
    }

    @Test
    void testImportWinMssCabFile_withNullCabFile_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                winMssService.importWinMssCabFile(null));
    }

    @Test
    void testImportWinMssCabFile_withEmptyCabFile_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                winMssService.importWinMssCabFile(""));
    }

    @Test
    void testImportWinMssCabFile_withBlankCabFile_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                winMssService.importWinMssCabFile("   "));
    }
}
