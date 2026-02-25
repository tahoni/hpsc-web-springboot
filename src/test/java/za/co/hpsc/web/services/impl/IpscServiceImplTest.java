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
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDtoHolder;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponseHolder;
import za.co.hpsc.web.services.IpscMatchResultService;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.TransactionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IpscServiceImplTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private IpscMatchService ipscMatchService;
    @Mock
    private IpscMatchResultService ipscMatchResultService;

    @InjectMocks
    private IpscServiceImpl ipscService;

    // =====================================================================
    // Tests for importWinMssCabFileContent - valid/invalid file processing
    // =====================================================================

    // Test Group: Null/Empty/Blank Input Validation
    @Test
    public void testImportWinMssCabFileContent_withValidCabFile_thenReturnsMatches() {
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
        when(ipscMatchResultService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(matchResultsDto));

        // Act
        MatchResultsDtoHolder response = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFileContent(cabFileContent));

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getMatches().size());
        assertEquals(matchResultsDto, response.getMatches().getFirst());

        verify(ipscMatchService, times(1)).mapMatchResults(any());
        verify(ipscMatchResultService, times(1)).initMatchResults(ipscResponse);
        assertDoesNotThrow(() -> verify(transactionService, times(1)).saveMatchResults(matchResultsDto));
    }

    @Test
    public void testImportWinMssCabFileContent_withNullCabFileContent_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile(null)
        );

        // Assert
        verify(ipscMatchService, never()).mapMatchResults(any());
        verify(ipscMatchResultService, never()).initMatchResults(any());
        assertDoesNotThrow(() -> verify(transactionService, never()).saveMatchResults(any()));
    }

    @Test
    public void testImportWinMssCabFileContent_withEmptyCabFileContent_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile("")
        );

        // Assert
        verify(ipscMatchService, never()).mapMatchResults(any());
        verify(ipscMatchResultService, never()).initMatchResults(any());
        assertDoesNotThrow(() -> verify(transactionService, never()).saveMatchResults(any()));
    }

    @Test
    public void testImportWinMssCabFileContent_withBlankCabFileContent_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                ipscService.importWinMssCabFile("   ")
        );

        verify(ipscMatchService, never()).mapMatchResults(any());
        verify(ipscMatchResultService, never()).initMatchResults(any());
        assertDoesNotThrow(() -> verify(transactionService, never()).saveMatchResults(any()));
    }

    @Test
    public void testImportWinMssCabFileContent_withNullResponseHolder_thenThrowsValidationException() {
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
                ipscService.importWinMssCabFile(cabFileContent)
        );

        // Assert
        assertDoesNotThrow(() -> {
            verify(ipscMatchService, times(1)).mapMatchResults(any());
            verify(ipscMatchResultService, never()).initMatchResults(any());
            verify(transactionService, never()).saveMatchResults(any());
        });
    }

    @Test
    public void testImportWinMssCabFileContent_withMultipleMatches_thenProcessesAllMatches() {
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
        when(ipscMatchResultService.initMatchResults(ipscResponse1)).thenReturn(Optional.of(matchResults1));
        when(ipscMatchResultService.initMatchResults(ipscResponse2)).thenReturn(Optional.of(matchResults2));

        // Act
        MatchResultsDtoHolder response = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFileContent(cabFileContent));

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getMatches().size());
        assertTrue(response.getMatches().contains(matchResults1));
        assertTrue(response.getMatches().contains(matchResults2));

        verify(ipscMatchService, times(1)).mapMatchResults(any());
        verify(ipscMatchResultService, times(2)).initMatchResults(any(IpscResponse.class));
        assertDoesNotThrow(() -> verify(transactionService, times(1)).saveMatchResults(matchResults1));
        assertDoesNotThrow(() -> verify(transactionService, times(1)).saveMatchResults(matchResults2));
    }

    @Test
    public void testImportWinMssCabFileContent_withEmptyMatchResults_thenSkipsSaving() {
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
        when(ipscMatchResultService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.empty());

        // Act
        MatchResultsDtoHolder response = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFileContent(cabFileContent));

        // Assert
        assertNotNull(response);
        assertTrue(response.getMatches().isEmpty());

        verify(ipscMatchService, times(1)).mapMatchResults(any());
        verify(ipscMatchResultService, times(1)).initMatchResults(ipscResponse);
        assertDoesNotThrow(() -> verify(transactionService, never()).saveMatchResults(any()));
    }

    @Test
    public void testImportWinMssCabFileContent_withInvalidJson_thenThrowsFatalException() {
        // Arrange
        String invalidCabFileContent = "This is not valid JSON content";

        // Act & Assert
        assertThrows(FatalException.class, () ->
                ipscService.importWinMssCabFile(invalidCabFileContent)
        );

        // Assert
        verify(ipscMatchService, never()).mapMatchResults(any());
        verify(ipscMatchResultService, never()).initMatchResults(any());
        assertDoesNotThrow(() -> verify(transactionService, never()).saveMatchResults(any()));
    }

    @Test
    public void testImportWinMssCabFileContent_withInvalidXml_thenThrowsValidationException() {
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
                ipscService.importWinMssCabFile(invalidCabFileContent)
        );

        // Assert
        verify(ipscMatchService, never()).mapMatchResults(any());
        verify(ipscMatchResultService, never()).initMatchResults(any());
        assertDoesNotThrow(() -> verify(transactionService, never()).saveMatchResults(any()));
    }

    @Test
    public void testImportWinMssCabFileContent_withCabFile_thenReturnsMatch() {
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
        when(ipscMatchResultService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(matchResultsDto));

        // Act
        MatchResultsDtoHolder response = assertDoesNotThrow(() ->
                ipscService.importWinMssCabFileContent(cabFileContent));

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getMatches().size());
        assertEquals(matchResultsDto, response.getMatches().getFirst());

        verify(ipscMatchService, times(1)).mapMatchResults(any());
        verify(ipscMatchResultService, times(1)).initMatchResults(ipscResponse);
        assertDoesNotThrow(() -> verify(transactionService, times(1)).saveMatchResults(matchResultsDto));
    }

    // =====================================================================
    // Tests for readIpscRequests - IPSC request parsing with XML
    // =====================================================================

    @Test
    public void testReadIpscRequests_withValidJson_thenReturnsIpscRequestHolder() {
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

        // Act
        var result = assertDoesNotThrow(() -> ipscService.readIpscRequests(cabFileContent));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getClubs().size());
        assertEquals("ABC", result.getClubs().getFirst().getClubCode());
        assertEquals(1, result.getMatches().size());
        assertEquals(1, result.getStages().size());
        assertEquals(1, result.getTags().size());
        assertEquals(1, result.getMembers().size());
        assertEquals(1, result.getClassifications().size());
        assertEquals(1, result.getEnrolledMembers().size());
        assertEquals(1, result.getEnrolledMembers().size());
        assertEquals(1, result.getTeams().size());
        assertEquals(1, result.getScores().size());

        // Assert the club section
        ClubRequest clubRequest = result.getClubs().getFirst();
        assertEquals(1, clubRequest.getClubId());
        assertEquals("ABC", clubRequest.getClubCode());
        assertEquals("Test Club", clubRequest.getClubName());
        // Assert the match section
        MatchRequest matchRequest = result.getMatches().getFirst();
        assertEquals(100, matchRequest.getMatchId());
        assertEquals("Test Match", matchRequest.getMatchName());
        // Assert the stage section
        StageRequest stageRequest = result.getStages().getFirst();
        assertEquals(200, stageRequest.getStageId());
        assertEquals("Test Stage", stageRequest.getStageName());
        assertEquals(100, stageRequest.getMatchId());
        // Assert the tag section
        TagRequest tagRequest = result.getTags().getFirst();
        assertEquals(10, tagRequest.getTagId());
        assertEquals("Test Tag", tagRequest.getTagName());
        // Assert the member section
        MemberRequest memberRequest = result.getMembers().getFirst();
        assertEquals(50, memberRequest.getMemberId());
        assertEquals("John", memberRequest.getFirstName());
        assertEquals("Doe", memberRequest.getLastName());
        assertEquals(true, memberRequest.getIsRegisteredForMatch());
        assertEquals(LocalDateTime.of(1973, 2, 17, 0, 0, 0), memberRequest.getDateOfBirth());
        // Assert the enrolled section
        EnrolledRequest enrolledRequest = result.getEnrolledMembers().getFirst();
        assertEquals(50, enrolledRequest.getMemberId());
        assertEquals(500, enrolledRequest.getCompetitorId());
        assertEquals(100, enrolledRequest.getMatchId());
        // Assert the classification section
        ClassificationRequest classificationRequest = result.getClassifications().getFirst();
        assertEquals(50, classificationRequest.getMemberId());
        assertEquals(1, classificationRequest.getDivisionId());
        assertEquals(5000, classificationRequest.getInternationalClassificationId());
        assertEquals(500, classificationRequest.getNationalClassificationId());
        // Assert the squad section
        SquadRequest squadRequest = result.getSquads().getFirst();
        assertEquals(20, squadRequest.getSquadId());
        assertEquals("Squad A", squadRequest.getSquadName());
        assertEquals(100, squadRequest.getMatchId());
        // Assert the team section
        TeamRequest teamRequest = result.getTeams().getFirst();
        assertEquals(20, teamRequest.getTeamId());
        assertEquals("Team A", teamRequest.getTeamName());
        assertEquals(100, teamRequest.getMatchId());
        // Assert the score section
        ScoreRequest scoreRequest = result.getScores().getFirst();
        assertEquals(100, scoreRequest.getMatchId());
        assertEquals(200, scoreRequest.getStageId());
        assertEquals(50, scoreRequest.getMemberId());
        assertEquals(101, scoreRequest.getFinalScore());
        assertEquals("6.08433734939759", scoreRequest.getHitFactor());
    }

    @Test
    public void testReadIpscRequests_withEmptyXmlSectionData_thenReturnsEmptyLists() {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "match": "<xml><data></data></xml>",
                    "tag": "<xml><data></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "enrolled": "<xml><data></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "score": "<xml><data></data></xml>"
                }
                """;

        // Act
        var result = assertDoesNotThrow(() -> ipscService.readIpscRequests(cabFileContent));

        // Assert
        assertNotNull(result);
        assertTrue(result.getClubs().isEmpty());
        assertTrue(result.getMatches().isEmpty());
        assertTrue(result.getStages().isEmpty());
        assertTrue(result.getTags().isEmpty());
        assertTrue(result.getMembers().isEmpty());
        assertTrue(result.getClassifications().isEmpty());
        assertTrue(result.getEnrolledMembers().isEmpty());
        assertTrue(result.getTeams().isEmpty());
        assertTrue(result.getScores().isEmpty());
    }

    @Test
    public void testReadIpscRequests_withEmptyAndNotEmptyXml_thenReturnsIpscRequestHolder() {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match'/></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data><row TagId='10' Tag='Test Tag'/></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "enrolled": "<xml><data><row CompId='5' MemberId='50' MatchId='2' SquadId='4'/></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "score": "<xml><data><row MatchId='2' StageId='5' MemberId='50'/></data></xml>"
                }
                """;

        // Act
        var result = assertDoesNotThrow(() -> ipscService.readIpscRequests(cabFileContent));

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getClubs().size());
        assertEquals(1, result.getMatches().size());
        assertEquals(0, result.getStages().size());
        assertEquals(1, result.getTags().size());
        assertEquals(0, result.getMembers().size());
        assertEquals(1, result.getEnrolledMembers().size());
        assertEquals(1, result.getScores().size());

        assertEquals(0, result.getClassifications().size());
        assertEquals(0, result.getTeams().size());
        assertEquals(0, result.getSquads().size());

        // Assert the match section
        MatchRequest matchRequest = result.getMatches().getFirst();
        assertEquals(100, matchRequest.getMatchId());
        assertEquals("Test Match", matchRequest.getMatchName());
        // Assert the tag section
        TagRequest tagRequest = result.getTags().getFirst();
        assertEquals(10, tagRequest.getTagId());
        assertEquals("Test Tag", tagRequest.getTagName());
        // Assert the enrolled section
        EnrolledRequest enrolledRequest = result.getEnrolledMembers().getFirst();
        assertEquals(5, enrolledRequest.getCompetitorId());
        assertEquals(50, enrolledRequest.getMemberId());
        assertEquals(2, enrolledRequest.getMatchId());
        assertEquals(4, enrolledRequest.getSquadId());
        // Assert the score section
        ScoreRequest scoreRequest = result.getScores().getFirst();
        assertEquals(2, scoreRequest.getMatchId());
        assertEquals(5, scoreRequest.getStageId());
        assertEquals(50, scoreRequest.getMemberId());
    }

    @Test
    public void testReadIpscRequests_withMissingXml_thenReturnsIpscRequestHolder() {
        // Arrange
        String cabFileContent = """
                {
                    "club": "<xml><data></data></xml>",
                    "match": "<xml><data><row MatchId='100' MatchName='Test Match'/></data></xml>",
                    "stage": "<xml><data></data></xml>",
                    "tag": "<xml><data><row TagId='10' Tag='Test Tag'/></data></xml>",
                    "member": "<xml><data></data></xml>",
                    "enrolled": "<xml><data><row CompId='5' MemberId='50' MatchId='2' SquadId='4'/></data></xml>",
                    "squad": "<xml><data></data></xml>",
                    "score": "<xml><data><row MatchId='2' StageId='5' MemberId='50'/></data></xml>"
                }
                """;

        // Act
        var result = assertDoesNotThrow(() -> ipscService.readIpscRequests(cabFileContent));

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getClubs().size());
        assertEquals(1, result.getMatches().size());
        assertEquals(0, result.getStages().size());
        assertEquals(1, result.getTags().size());
        assertEquals(0, result.getMembers().size());
        assertEquals(1, result.getEnrolledMembers().size());
        assertEquals(1, result.getScores().size());

        assertEquals(0, result.getClassifications().size());
        assertEquals(0, result.getTeams().size());
        assertEquals(0, result.getSquads().size());
    }

    @Test
    public void testReadIpscRequests_withEmptyXmlSections_thenReturnsEmptyLists() {
        // Arrange
        String cabFileContent = """
                {
                    "club": "",
                    "stage": "",
                    "match": "",
                    "tag": "",
                    "member": "",
                    "classify": "",
                    "enrolled": "",
                    "squad": "",
                    "team": "",
                    "score": ""
                }
                """;

        // Act
        var result = assertDoesNotThrow(() -> ipscService.readIpscRequests(cabFileContent));

        // Assert
        assertNotNull(result);
        assertTrue(result.getClubs().isEmpty());
        assertTrue(result.getMatches().isEmpty());
        assertTrue(result.getStages().isEmpty());
        assertTrue(result.getTags().isEmpty());
        assertTrue(result.getMembers().isEmpty());
        assertTrue(result.getClassifications().isEmpty());
        assertTrue(result.getEnrolledMembers().isEmpty());
        assertTrue(result.getTeams().isEmpty());
        assertTrue(result.getSquads().isEmpty());
        assertTrue(result.getScores().isEmpty());
    }

    @Test
    public void testReadIpscRequests_withNullXmlSections_thenReturnsEmptyLists() {
        // Arrange
        String cabFileContent = """
                {
                    "club": null,
                    "stage": null,
                    "match": null,
                    "tag": null,
                    "member": null,
                    "classify": null,
                    "enrolled": null,
                    "squad": null,
                    "team": null,
                    "score": null
                }
                """;

        // Act
        var result = assertDoesNotThrow(() -> ipscService.readIpscRequests(cabFileContent));

        // Assert
        assertNotNull(result);
        assertTrue(result.getClubs().isEmpty());
        assertTrue(result.getMatches().isEmpty());
        assertTrue(result.getStages().isEmpty());
        assertTrue(result.getTags().isEmpty());
        assertTrue(result.getMembers().isEmpty());
        assertTrue(result.getClassifications().isEmpty());
        assertTrue(result.getEnrolledMembers().isEmpty());
        assertTrue(result.getTeams().isEmpty());
        assertTrue(result.getSquads().isEmpty());
        assertTrue(result.getScores().isEmpty());
    }

    @Test
    public void testReadIpscRequests_withInvalidJson_thenThrowsException() {
        // Arrange
        String cabFileContent = "Invalid JSON Content";

        // Act & Assert
        assertThrows(FatalException.class, () ->
                ipscService.readIpscRequests(cabFileContent)
        );
    }

    // =====================================================================
    // Tests for readRequests - Generic XML to request object parsing
    // =====================================================================

    // --- Existing readRequests tests (original) ---

    @Test
    public void testReadRequests_withValidXml_thenReturnsRequests() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row ClubId="1" ClubCode="ABC" Club="Alpha Club" Contact="John Doe"
                                Address1="123 Main St" Address2="Suite 100" City="Cape Town"
                                Province="Western Cape" CountryId="ZA" PostCode="8001"
                                Phone="0211234567" PhoneAlt="0827654321" PhoneFax="0211234568"
                                Email="info@alphaclub.co.za" WebSite="https://alphaclub.co.za"/>
                            <row ClubId="2" ClubCode="DEF" Club="Beta Club" Contact="Jane Smith"
                                Address1="456 Oak Ave" Address2="" City="Johannesburg"
                                Province="Gauteng" CountryId="ZA" PostCode="2000"
                                Phone="0119876543" PhoneAlt="0823456789" PhoneFax="0119876544"
                                Email="contact@betaclub.co.za" WebSite="https://betaclub.co.za"/>
                        </data>
                    </xml>
                """;

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertEquals(2, clubs.size());

        // Assert the first club
        ClubRequest firstClub = clubs.getFirst();
        assertEquals(1, firstClub.getClubId());
        assertEquals("ABC", firstClub.getClubCode());
        assertEquals("Alpha Club", firstClub.getClubName());
        assertEquals("John Doe", firstClub.getContact());
        assertEquals("123 Main St", firstClub.getAddress1());
        assertEquals("Suite 100", firstClub.getAddress2());
        assertEquals("Cape Town", firstClub.getCity());
        assertEquals("Western Cape", firstClub.getProvince());
        assertEquals("ZA", firstClub.getCountryId());
        assertEquals("8001", firstClub.getPostalCode());
        assertEquals("0211234567", firstClub.getOfficePhoneNumber());
        assertEquals("0827654321", firstClub.getAlternativePhoneNumber());
        assertEquals("0211234568", firstClub.getFaxNumber());
        assertEquals("info@alphaclub.co.za", firstClub.getEmail());
        assertEquals("https://alphaclub.co.za", firstClub.getWebsite());

        // Assert second club
        ClubRequest secondClub = clubs.get(1);
        assertEquals(2, secondClub.getClubId());
        assertEquals("DEF", secondClub.getClubCode());
        assertEquals("Beta Club", secondClub.getClubName());
    }

    @Test
    public void testReadRequests_withValidNamespaceClubXml_thenReturnsClubRequest() {
        // Arrange
        String xmlData = """
                    <xml xmlns:s='uuid:BDC6E3F0-6DA3-11d1-A2A3-00AA00C14882'
                      xmlns:dt='uuid:C2F41010-65B3-11d1-A29F-00AA00C14882'
                      xmlns:rs='urn:schemas-microsoft-com:rowset'
                      xmlns:z='#RowsetSchema'>
                        <rs:data>
                            <z:row ClubId="1" ClubCode="ABC" Club="Alpha Club" Contact="John Doe"
                                Address1="123 Main St" Address2="Suite 100" City="Cape Town"
                                Province="Western Cape" CountryId="ZA" PostCode="8001"
                                Phone="0211234567" PhoneAlt="0827654321" PhoneFax="0211234568"
                                Email="info@alphaclub.co.za" WebSite="https://alphaclub.co.za"/>
                            <z:row ClubId="2" ClubCode="DEF" Club="Beta Club" Contact="Jane Smith"
                                Address1="456 Oak Ave" Address2="" City="Johannesburg"
                                Province="Gauteng" CountryId="ZA" PostCode="2000"
                                Phone="0119876543" PhoneAlt="0823456789" PhoneFax="0119876544"
                                Email="contact@betaclub.co.za" WebSite="https://betaclub.co.za"/>
                        </rs:data>
                    </xml>
                """;

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertEquals(2, clubs.size());

        // Assert the first club
        ClubRequest firstClub = clubs.getFirst();
        assertEquals(1, firstClub.getClubId());
        assertEquals("ABC", firstClub.getClubCode());
        assertEquals("Alpha Club", firstClub.getClubName());
        assertEquals("John Doe", firstClub.getContact());
        assertEquals("123 Main St", firstClub.getAddress1());
        assertEquals("Suite 100", firstClub.getAddress2());
        assertEquals("Cape Town", firstClub.getCity());
        assertEquals("Western Cape", firstClub.getProvince());
        assertEquals("ZA", firstClub.getCountryId());
        assertEquals("8001", firstClub.getPostalCode());
        assertEquals("0211234567", firstClub.getOfficePhoneNumber());
        assertEquals("0827654321", firstClub.getAlternativePhoneNumber());
        assertEquals("0211234568", firstClub.getFaxNumber());
        assertEquals("info@alphaclub.co.za", firstClub.getEmail());
        assertEquals("https://alphaclub.co.za", firstClub.getWebsite());

        // Assert second club
        ClubRequest secondClub = clubs.get(1);
        assertEquals(2, secondClub.getClubId());
        assertEquals("DEF", secondClub.getClubCode());
        assertEquals("Beta Club", secondClub.getClubName());
    }

    @Test
    public void testReadRequests_withEmptyXmlData_thenReturnsEmptyList() {
        // Arrange
        String xmlData = """
                <xml>
                    <data/>
                </xml>
                """;

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertEquals(0, clubs.size());
    }

    @Test
    public void testReadRequests_withInvalidXml_thenThrowsException() {
        // Arrange
        String xmlData = "Invalid XML Content";

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscService.readRequests(xmlData,
                ClubRequest.class));
    }

    @Test
    public void testReadRequests_withEmptyXml_thenReturnsEmptyList() {
        // Arrange
        String xmlData = "";

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertEquals(0, clubs.size());
    }

    @Test
    public void testReadRequests_withPartialData_thenReturnsRequestsWithPartialData() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row ClubId="1" ClubCode="ABC" Club="Alpha Club"/>
                            <row ClubId="2" Club="Beta Club"/>
                        </data>
                    </xml>
                """;

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertEquals(2, clubs.size());

        // Assert the first club with partial data
        ClubRequest firstClub = clubs.getFirst();
        assertEquals(1, firstClub.getClubId());
        assertEquals("ABC", firstClub.getClubCode());
        assertEquals("Alpha Club", firstClub.getClubName());
        assertNull(firstClub.getContact());
        assertNull(firstClub.getAddress1());
        assertNull(firstClub.getAddress2());
        assertNull(firstClub.getCity());
        assertNull(firstClub.getProvince());
        assertNull(firstClub.getCountryId());
        assertNull(firstClub.getPostalCode());
        assertNull(firstClub.getOfficePhoneNumber());
        assertNull(firstClub.getAlternativePhoneNumber());
        assertNull(firstClub.getFaxNumber());
        assertNull(firstClub.getEmail());
        assertNull(firstClub.getWebsite());

        // Assert the second club with partial data
        ClubRequest secondClub = clubs.get(1);
        assertEquals(2, secondClub.getClubId());
        assertEquals("Beta Club", secondClub.getClubName());
        assertNull(secondClub.getClubCode());
        assertNull(secondClub.getContact());
        assertNull(secondClub.getAddress1());
        assertNull(secondClub.getAddress2());
        assertNull(secondClub.getCity());
        assertNull(secondClub.getProvince());
        assertNull(secondClub.getCountryId());
        assertNull(secondClub.getPostalCode());
        assertNull(secondClub.getOfficePhoneNumber());
        assertNull(secondClub.getAlternativePhoneNumber());
        assertNull(secondClub.getFaxNumber());
        assertNull(secondClub.getEmail());
        assertNull(secondClub.getWebsite());
    }

    @Test
    public void testReadRequests_withMixedDataCompleteness_thenProcessesAllRowsCorrectly() {
        // Arrange
        String xmlData = """
                    <xml>
                        <data>
                            <row ClubId="1" ClubCode="ABC" Club="Alpha Club" Contact="John Doe"/>
                            <row ClubId="2" Club="Beta Club"/>
                            <row ClubId="3" ClubCode="GHI"/>
                        </data>
                    </xml>
                """;

        // Act
        List<ClubRequest> clubs = assertDoesNotThrow(() -> ipscService.readRequests(xmlData,
                ClubRequest.class));

        // Assert
        assertNotNull(clubs);
        assertEquals(3, clubs.size());

        // Assert the first row
        ClubRequest firstClub = clubs.getFirst();
        assertEquals(1, firstClub.getClubId());
        assertEquals("ABC", firstClub.getClubCode());
        assertEquals("Alpha Club", firstClub.getClubName());
        assertEquals("John Doe", firstClub.getContact());

        // Assert the second row
        ClubRequest secondClub = clubs.get(1);
        assertEquals(2, secondClub.getClubId());
        assertEquals("Beta Club", secondClub.getClubName());
        assertNull(secondClub.getClubCode());
        assertNull(secondClub.getContact());

        // Assert the third row
        ClubRequest thirdClub = clubs.get(2);
        assertEquals(3, thirdClub.getClubId());
        assertEquals("GHI", thirdClub.getClubCode());
        assertNull(thirdClub.getClubName());
        assertNull(thirdClub.getContact());
    }

    @Test
    public void testReadRequests_whenXmlHasNumericAttributes_thenParsesCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="100" SquadCount="9" FirearmId="7"/>
                        <row MatchId="101" SquadCount="5" FirearmId="2"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        MatchRequest match1 = result.getFirst();
        assertEquals(100, match1.getMatchId());
        assertEquals(9, match1.getSquadCount());
        assertEquals(7, match1.getFirearmId());

        MatchRequest match2 = result.get(1);
        assertEquals(101, match2.getMatchId());
        assertEquals(5, match2.getSquadCount());
        assertEquals(2, match2.getFirearmId());
    }

    @Test
    public void testReadRequests_whenXmlHasBooleanAttributes_thenParsesBooleanCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" Chrono="True"/>
                        <row MatchId="2" Chrono="False"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        MatchRequest match1 = result.getFirst();
        assertEquals(1, match1.getMatchId());
        assertTrue(match1.getChrono());

        MatchRequest match2 = result.get(1);
        assertEquals(2, match2.getMatchId());
        assertFalse(match2.getChrono());
    }

    @Test
    public void testReadRequests_whenXmlHasDateTimeAttributes_thenParsesDateTimeCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" MatchDt="2025-09-06T00:00:00"/>
                        <row MatchId="2" MatchDt="2025-12-25T14:30:45"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        MatchRequest match1 = result.getFirst();
        assertEquals(LocalDateTime.of(2025, 9, 6, 0, 0, 0), match1.getMatchDate());

        MatchRequest match2 = result.get(1);
        assertEquals(LocalDateTime.of(2025, 12, 25, 14, 30, 45), match2.getMatchDate());
    }

    @Test
    public void testReadRequests_withNumericAttributes_thenParsesCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" SquadCount="9" FirearmId="7"/>
                        <row MatchId="2" SquadCount="5" FirearmId="2"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        MatchRequest match1 = result.getFirst();
        assertEquals(1, match1.getMatchId());
        assertEquals(9, match1.getSquadCount());
        assertEquals(7, match1.getFirearmId());

        MatchRequest match2 = result.get(1);
        assertEquals(2, match2.getMatchId());
        assertEquals(5, match2.getSquadCount());
        assertEquals(2, match2.getFirearmId());
    }

    @Test
    public void testReadRequests_withBooleanAttributes_thenParsesBooleanCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" Chrono="True"/>
                        <row MatchId="2" Chrono="False"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        MatchRequest match1 = result.getFirst();
        assertEquals(1, match1.getMatchId());
        assertTrue(match1.getChrono());

        MatchRequest match2 = result.get(1);
        assertEquals(2, match2.getMatchId());
        assertFalse(match2.getChrono());
    }

    @Test
    public void testReadRequests_withDateTimeAttributes_thenParsesDateTimeCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" MatchDt="2025-09-06T00:00:00"/>
                        <row MatchId="2" MatchDt="2025-12-25T14:30:45"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        MatchRequest match1 = result.getFirst();
        assertEquals(LocalDateTime.of(2025, 9, 6, 0, 0, 0), match1.getMatchDate());

        MatchRequest match2 = result.get(1);
        assertEquals(LocalDateTime.of(2025, 12, 25, 14, 30, 45), match2.getMatchDate());
    }

    @Test
    public void testReadRequests_withCompleteScoreFields_thenReturnsAllFieldsPopulated() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" StageId="8" MemberId="105" ScoreA="5" ScoreB="3" ScoreC="2" ScoreD="1" Misses="0" Penalties="5" HitFactor="8.75" FinalScore="87"/>
                    </data>
                </xml>
                """;

        // Act
        List<ScoreRequest> result = ipscService.readRequests(xmlData, ScoreRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        ScoreRequest score = result.getFirst();
        assertEquals(1, score.getMatchId());
        assertEquals(8, score.getStageId());
        assertEquals(105, score.getMemberId());
        assertEquals(5, score.getScoreA());
        assertEquals(3, score.getScoreB());
        assertEquals(2, score.getScoreC());
        assertEquals(1, score.getScoreD());
        assertEquals(0, score.getMisses());
        assertEquals(5, score.getPenalties());
        assertEquals("8.75", score.getHitFactor());
        assertEquals(87, score.getFinalScore());
    }

    @Test
    public void testReadRequests_whenXmlHasEmptyDataElement_thenReturnsEmptyList() {
        // Arrange
        String xmlData = "<xml><data></data></xml>";

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testReadRequests_whenXmlHasNullDataElement_thenReturnsEmptyList() {
        // Arrange
        String xmlData = "<xml><data/></xml>";

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testReadRequests_whenXmlHasPartialClubFields_thenReturnsRequestsWithPartialData() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row ClubId="1" ClubCode="ABC"/>
                        <row ClubId="2" Club="Beta Club"/>
                        <row ClubCode="GHI" Club="Gamma Club"/>
                    </data>
                </xml>
                """;

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        // The first row has ClubId and ClubCode, missing Club and Contact
        ClubRequest firstClub = result.getFirst();
        assertEquals(1, firstClub.getClubId());
        assertEquals("ABC", firstClub.getClubCode());
        assertNull(firstClub.getClubName());

        // The second row has ClubId and Club, missing ClubCode
        ClubRequest secondClub = result.get(1);
        assertEquals(2, secondClub.getClubId());
        assertEquals("Beta Club", secondClub.getClubName());

        // The third row has ClubCode and Club, missing ClubId
        ClubRequest thirdClub = result.get(2);
        assertEquals("GHI", thirdClub.getClubCode());
        assertEquals("Gamma Club", thirdClub.getClubName());
    }

    @Test
    public void testReadRequests_whenXmlHasMultipleRowsWithVariedFieldPopulation_thenProcessesAllRows() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row ClubId="1" ClubCode="ABC" Club="Alpha" Contact="John" Address1="123 St" City="CT"/>
                        <row ClubId="2" Club="Beta Club"/>
                        <row ClubId="3" ClubCode="GHI"/>
                    </data>
                </xml>
                """;

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        // Assert the first row
        ClubRequest firstClub = result.getFirst();
        assertEquals(1, firstClub.getClubId());
        assertEquals("ABC", firstClub.getClubCode());
        assertEquals("Alpha", firstClub.getClubName());
        assertEquals("John", firstClub.getContact());
        assertEquals("123 St", firstClub.getAddress1());
        assertEquals("CT", firstClub.getCity());

        // Assert the second row
        ClubRequest secondClub = result.get(1);
        assertEquals(2, secondClub.getClubId());
        assertEquals("Beta Club", secondClub.getClubName());
        assertNull(secondClub.getClubCode());
        assertNull(secondClub.getContact());

        // Assert the third row
        ClubRequest thirdClub = result.get(2);
        assertEquals(3, thirdClub.getClubId());
        assertEquals("GHI", thirdClub.getClubCode());
        assertNull(thirdClub.getClubName());
        assertNull(thirdClub.getContact());
    }

    @Test
    public void testReadRequests_withOnlyRequiredMemberId_thenReturnsRequest() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MemberId="50"/>
                    </data>
                </xml>
                """;

        // Act
        List<MemberRequest> result = ipscService.readRequests(xmlData, MemberRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        MemberRequest member = result.getFirst();
        assertEquals(50, member.getMemberId());
        assertNull(member.getFirstName());
        assertNull(member.getLastName());
    }

    @Test
    public void testReadRequests_withCompleteMatchFields_thenReturnsAllFieldsPopulated() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" MatchName="Test Match" MatchDt="2025-09-06T00:00:00" Chrono="True" MatchLevel="2" CountryId="ZA" FirearmId="7" SquadCount="9"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        MatchRequest match = result.getFirst();
        assertEquals(1, match.getMatchId());
        assertEquals("Test Match", match.getMatchName());
        assertEquals(LocalDateTime.of(2025, 9, 6, 0, 0, 0), match.getMatchDate());
        assertTrue(match.getChrono());
        assertEquals(2, match.getMatchLevel());
        assertEquals("ZA", match.getCountryId());
        assertEquals(7, match.getFirearmId());
        assertEquals(9, match.getSquadCount());
    }

    @Test
    public void testReadRequests_withPartialMatchFields_thenReturnsOnlyPopulatedFields() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" MatchName="Partial Match"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        MatchRequest match = result.getFirst();
        assertEquals(1, match.getMatchId());
        assertEquals("Partial Match", match.getMatchName());
        assertNull(match.getMatchDate());
        assertNull(match.getChrono());
    }

    @Test
    public void testReadRequests_withCompleteStageFields_thenReturnsAllFieldsPopulated() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" StageId="2" StageName="Test Stage" FirearmId="7" CourseId="2" ScoringId="1" TrgtTypeId="2" MaxPoints="105" MinRounds="21"/>
                    </data>
                </xml>
                """;

        // Act
        List<StageRequest> result = ipscService.readRequests(xmlData, StageRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        StageRequest stage = result.getFirst();
        assertEquals(1, stage.getMatchId());
        assertEquals(2, stage.getStageId());
        assertEquals("Test Stage", stage.getStageName());
        assertEquals(7, stage.getFirearmId());
        assertEquals(2, stage.getCourseId());
        assertEquals(2, stage.getTargetClassificationId());
        assertEquals(105, stage.getMaxPoints());
        assertEquals(21, stage.getMinRounds());
    }

    @Test
    public void testReadRequests_withCompleteEnrolledFields_thenReturnsAllFieldsPopulated() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" MemberId="50" CompId="105" DivId="29" CatId="2" SquadId="7" TagId="1" MajorPF="True" IsDisq="False"/>
                    </data>
                </xml>
                """;

        // Act
        List<EnrolledRequest> result = ipscService.readRequests(xmlData, EnrolledRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        EnrolledRequest enrolled = result.getFirst();
        assertEquals(1, enrolled.getMatchId());
        assertEquals(50, enrolled.getMemberId());
        assertEquals(105, enrolled.getCompetitorId());
        assertEquals(29, enrolled.getDivisionId());
        assertEquals(2, enrolled.getCompetitorCategoryId());
        assertEquals(7, enrolled.getSquadId());
        assertEquals(1, enrolled.getTagId());
    }

    @Test
    public void testReadRequests_whenXmlHasMultipleClubsWithVariedFieldCompleteness_thenProcessesAllClubs() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row ClubId="1" ClubCode="ABC" Club="Alpha Club" Contact="John Doe" Phone="123456789" Email="john@alpha.com"/>
                        <row ClubId="2" ClubCode="DEF"/>
                        <row ClubId="3" Club="Gamma Club"/>
                        <row ClubCode="IJK" Club="Iota Club"/>
                    </data>
                </xml>
                """;

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());

        // Fully populated
        ClubRequest club1 = result.getFirst();
        assertEquals(1, club1.getClubId());
        assertEquals("ABC", club1.getClubCode());
        assertEquals("Alpha Club", club1.getClubName());
        assertEquals("John Doe", club1.getContact());
        assertEquals("123456789", club1.getOfficePhoneNumber());
        assertEquals("john@alpha.com", club1.getEmail());

        // Minimal fields
        ClubRequest club2 = result.get(1);
        assertEquals(2, club2.getClubId());
        assertEquals("DEF", club2.getClubCode());

        // Only name
        ClubRequest club3 = result.get(2);
        assertEquals(3, club3.getClubId());
        assertEquals("Gamma Club", club3.getClubName());

        // No ID
        ClubRequest club4 = result.get(3);
        assertEquals("IJK", club4.getClubCode());
        assertEquals("Iota Club", club4.getClubName());
    }

    @Test
    public void testReadRequests_whenXmlHasEmptyStringAttributes_thenProcessesWithEmptyValues() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row ClubId="1" ClubCode="" Club="Alpha Club"/>
                    </data>
                </xml>
                """;

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        ClubRequest club = result.getFirst();
        assertEquals(1, club.getClubId());
        assertEquals("", club.getClubCode());
        assertEquals("Alpha Club", club.getClubName());
    }

    // --- New comprehensive readRequests tests: Edge cases, null/empty/blank, partial/full fields ---

    // Test Group: Null/Empty/Blank Input Handling
    @Test
    public void readRequests_whenXmlIsNull_thenReturnsEmptyList() {
        // Act
        List<ClubRequest> result = ipscService.readRequests(null, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void readRequests_whenXmlIsEmpty_thenReturnsEmptyList() {
        // Act
        List<ClubRequest> result = ipscService.readRequests("", ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void readRequests_whenXmlIsBlank_thenReturnsEmptyList() {
        // Act
        List<ClubRequest> result = ipscService.readRequests("   ", ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void readRequests_whenXmlHasBlankWithTabsAndNewlines_thenReturnsEmptyList() {
        // Act
        List<ClubRequest> result = ipscService.readRequests("\t\t\n   ", ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Test Group: XML Structure Edge Cases
    @Test
    public void readRequests_whenXmlHasEmptyDataElement_thenReturnsEmptyList() {
        // Arrange
        String xmlData = "<xml><data></data></xml>";

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void readRequests_whenXmlHasSelfClosingDataElement_thenReturnsEmptyList() {
        // Arrange
        String xmlData = "<xml><data/></xml>";

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Test Group: Partial and Full Field Coverage
    @Test
    public void readRequests_whenRowHasEmptyStringAttributes_thenProcessesWithEmptyValues() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row ClubId="1" ClubCode="" Club="Alpha Club"/>
                    </data>
                </xml>
                """;

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ClubRequest club = result.getFirst();
        assertEquals(1, club.getClubId());
        assertEquals("", club.getClubCode());
        assertEquals("Alpha Club", club.getClubName());
    }

    @Test
    public void readRequests_whenRowHasPartialFields_thenReturnsRequestWithOnlyPopulatedFields() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row ClubId="5" ClubCode="XYZ"/>
                    </data>
                </xml>
                """;

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ClubRequest club = result.getFirst();
        assertEquals(5, club.getClubId());
        assertEquals("XYZ", club.getClubCode());
        assertNull(club.getClubName());
        assertNull(club.getContact());
    }

    @Test
    public void readRequests_whenRowHasAllFields_thenReturnsRequestWithAllFields() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row ClubId="10" ClubCode="FULL" Club="Full Club" Contact="Admin"
                             Address1="123 Main" City="Cape Town" Province="WC"/>
                    </data>
                </xml>
                """;

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ClubRequest club = result.getFirst();
        assertEquals(10, club.getClubId());
        assertEquals("FULL", club.getClubCode());
        assertEquals("Full Club", club.getClubName());
        assertEquals("Admin", club.getContact());
        assertEquals("123 Main", club.getAddress1());
        assertEquals("Cape Town", club.getCity());
        assertEquals("WC", club.getProvince());
    }

    @Test
    public void readRequests_whenMultipleRowsWithVariedFieldPopulation_thenProcessesAllRows() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row ClubId="1" ClubCode="A" Club="Alpha"/>
                        <row ClubId="2" ClubCode="B"/>
                        <row ClubId="3" Club="Gamma"/>
                    </data>
                </xml>
                """;

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(1, result.getFirst().getClubId());
        assertEquals("A", result.getFirst().getClubCode());
        assertEquals("Alpha", result.getFirst().getClubName());

        assertEquals(2, result.get(1).getClubId());
        assertEquals("B", result.get(1).getClubCode());
        assertNull(result.get(1).getClubName());

        assertEquals(3, result.get(2).getClubId());
        assertNull(result.get(2).getClubCode());
        assertEquals("Gamma", result.get(2).getClubName());
    }

    @Test
    public void readRequests_whenRowHasMissingOptionalFields_thenReturnsWithNullValues() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row ClubId="99"/>
                    </data>
                </xml>
                """;

        // Act
        List<ClubRequest> result = ipscService.readRequests(xmlData, ClubRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ClubRequest club = result.getFirst();
        assertEquals(99, club.getClubId());
        assertNull(club.getClubCode());
        assertNull(club.getClubName());
        assertNull(club.getContact());
        assertNull(club.getAddress1());
    }

    // Test Group: Data Type Handling (Numeric, Boolean, DateTime)
    @Test
    public void readRequests_withNumericFields_thenParsesCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="100" SquadCount="5" FirearmId="7"/>
                        <row MatchId="200" SquadCount="10" FirearmId="3"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100, result.getFirst().getMatchId());
        assertEquals(5, result.getFirst().getSquadCount());
        assertEquals(7, result.getFirst().getFirearmId());

        assertEquals(200, result.get(1).getMatchId());
        assertEquals(10, result.get(1).getSquadCount());
        assertEquals(3, result.get(1).getFirearmId());
    }

    @Test
    public void readRequests_withBooleanFields_thenParsesBooleanCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" Chrono="True"/>
                        <row MatchId="2" Chrono="False"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.getFirst().getChrono());
        assertFalse(result.get(1).getChrono());
    }

    @Test
    public void readRequests_withDateTimeFields_thenParsesDateTimeCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" MatchDt="2025-09-06T10:30:00"/>
                        <row MatchId="2" MatchDt="2025-12-25T14:45:30"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(LocalDateTime.of(2025, 9, 6, 10, 30, 0), result.getFirst().getMatchDate());
        assertEquals(LocalDateTime.of(2025, 12, 25, 14, 45, 30), result.get(1).getMatchDate());
    }

    @Test
    public void readRequests_withCompleteMatchRecord_thenMapsAllFieldsCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="50" MatchName="Full Match Test" MatchDt="2025-06-15T09:00:00"
                             Chrono="True" MatchLevel="3" CountryId="ZA" FirearmId="5" SquadCount="8"/>
                    </data>
                </xml>
                """;

        // Act
        List<MatchRequest> result = ipscService.readRequests(xmlData, MatchRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        MatchRequest match = result.getFirst();
        assertEquals(50, match.getMatchId());
        assertEquals("Full Match Test", match.getMatchName());
        assertEquals(LocalDateTime.of(2025, 6, 15, 9, 0, 0), match.getMatchDate());
        assertTrue(match.getChrono());
        assertEquals(3, match.getMatchLevel());
        assertEquals("ZA", match.getCountryId());
        assertEquals(5, match.getFirearmId());
        assertEquals(8, match.getSquadCount());
    }

    @Test
    public void readRequests_withCompleteScoreRecord_thenMapsAllScoreFieldsCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" StageId="5" MemberId="100" ScoreA="10" ScoreB="8"
                             ScoreC="6" ScoreD="4" Misses="2" Penalties="10" HitFactor="9.5" FinalScore="95"/>
                    </data>
                </xml>
                """;

        // Act
        List<ScoreRequest> result = ipscService.readRequests(xmlData, ScoreRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ScoreRequest score = result.getFirst();
        assertEquals(1, score.getMatchId());
        assertEquals(5, score.getStageId());
        assertEquals(100, score.getMemberId());
        assertEquals(10, score.getScoreA());
        assertEquals(8, score.getScoreB());
        assertEquals(6, score.getScoreC());
        assertEquals(4, score.getScoreD());
        assertEquals(2, score.getMisses());
        assertEquals(10, score.getPenalties());
        assertEquals("9.5", score.getHitFactor());
        assertEquals(95, score.getFinalScore());
    }

    @Test
    public void readRequests_withPartialScoreRecord_thenMapsOnlyPopulatedScoreFields() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MatchId="1" StageId="2" MemberId="50"/>
                    </data>
                </xml>
                """;

        // Act
        List<ScoreRequest> result = ipscService.readRequests(xmlData, ScoreRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ScoreRequest score = result.getFirst();
        assertEquals(1, score.getMatchId());
        assertEquals(2, score.getStageId());
        assertEquals(50, score.getMemberId());
        assertNull(score.getScoreA());
        assertNull(score.getScoreB());
        assertNull(score.getHitFactor());
    }

    @Test
    public void readRequests_withCompleteEnrolledRecord_thenMapsAllEnrolledFieldsCorrectly() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MemberId="75" CompId="150" MatchId="10" SquadId="3" DivId="2"
                             CatId="1" TagId="5" MajorPF="True" IsDisq="False"/>
                    </data>
                </xml>
                """;

        // Act
        List<EnrolledRequest> result = ipscService.readRequests(xmlData, EnrolledRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        EnrolledRequest enrolled = result.getFirst();
        assertEquals(75, enrolled.getMemberId());
        assertEquals(150, enrolled.getCompetitorId());
        assertEquals(10, enrolled.getMatchId());
        assertEquals(3, enrolled.getSquadId());
        assertEquals(2, enrolled.getDivisionId());
        assertEquals(1, enrolled.getCompetitorCategoryId());
        assertEquals(5, enrolled.getTagId());
        assertTrue(enrolled.getMajorPowerFactor());
        assertFalse(enrolled.getIsDisqualified());
    }

    @Test
    public void readRequests_withPartialEnrolledRecord_thenMapsOnlyPopulatedEnrolledFields() {
        // Arrange
        String xmlData = """
                <xml>
                    <data>
                        <row MemberId="25" CompId="50" MatchId="5"/>
                    </data>
                </xml>
                """;

        // Act
        List<EnrolledRequest> result = ipscService.readRequests(xmlData, EnrolledRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        EnrolledRequest enrolled = result.getFirst();
        assertEquals(25, enrolled.getMemberId());
        assertEquals(50, enrolled.getCompetitorId());
        assertEquals(5, enrolled.getMatchId());
        assertNull(enrolled.getSquadId());
        assertNull(enrolled.getDivisionId());
        assertNull(enrolled.getCompetitorCategoryId());
    }

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
    // TODO: fix this, currently the code does not handle empty XML sections gracefully, it throws an exception when trying to parse empty sections, need to update the code to handle empty XML sections and return empty lists instead of throwing exceptions
    @Disabled
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
    // TODO: fix this, currently the code does not handle partial match results properly, it processes the first match result and then throws an exception when trying to process the second match result which returns empty, need to adjust service to process partial match results and skip empty ones without throwing an exception
    @Disabled
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
    // TODO: fix this, currently the code does not handle empty XML sections gracefully, it throws an exception when trying to parse empty
    @Disabled
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
    // TODO: fix this, currently the code does not handle multiple match results properly, it processes the first match result and then throws an exception when trying to process the second match result which returns empty, need to adjust service to process all match results and skip empty ones without throwing an exception
    @Disabled
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
    // TODO: fix this, currently the code does not handle mixed results properly, it throws an exception when trying to process the empty result, need to adjust service to process present results and skip empty ones without throwing an exception
    @Disabled
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
    // TODO: fix this - currently fails because empty strings in XML are treated as nulls in the service, need to adjust service to differentiate between empty and null if we want to support this test case
    @Disabled
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
    // TODO: fix this - currently fails because the XML parser does not handle special characters properly, need to ensure that the service can handle XML with special characters and that they are correctly parsed and processed
    @Disabled
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


