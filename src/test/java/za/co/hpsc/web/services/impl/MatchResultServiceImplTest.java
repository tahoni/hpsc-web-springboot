package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;
import za.co.hpsc.web.models.ipsc.request.MemberRequest;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.services.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchResultServiceImplTest {

    @Mock
    private ClubService clubService;

    @Mock
    private MatchService matchService;

    @Mock
    private CompetitorService competitorService;

    @Mock
    private MatchStageService matchStageService;

    @Mock
    private MatchCompetitorService matchCompetitorService;

    @Mock
    private MatchStageCompetitorService matchStageCompetitorService;

    @InjectMocks
    private MatchResultServiceImpl matchResultService;

    @Test
    public void testInitClub_withNullResponse_thenReturnsEmptyOptional() {
        // Act
        Optional<ClubDto> result = matchResultService.initClub(null);

        // Assert
        assertTrue(result.isEmpty());
        verifyNoInteractions(clubService);
    }

    @Test
    public void testInitClub_withExistingClub_thenReturnsPopulatedClubDto() {
        // Arrange
        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubName("Test Club");
        clubResponse.setClubCode("TC");

        Club existingClub = new Club();
        existingClub.setId(101L);
        existingClub.setName("Test Club");
        existingClub.setAbbreviation("TC");

        when(clubService.findClub("Test Club", "TC")).thenReturn(Optional.of(existingClub));

        // Act
        Optional<ClubDto> result = matchResultService.initClub(clubResponse);

        // Assert
        assertTrue(result.isPresent());
        ClubDto clubDto = result.get();
        assertEquals(101L, clubDto.getId());
        assertEquals("Test Club", clubDto.getName());
        assertEquals("TC", clubDto.getAbbreviation());
        verify(clubService, times(1)).findClub("Test Club", "TC");
    }

    @Test
    public void testInitClub_withNonExistingClub_thenReturnsClubDtoFromResponse() {
        // Arrange
        ClubResponse clubResponse = new ClubResponse();
        clubResponse.setClubName("Non-existent Club");
        clubResponse.setClubCode("NC");

        when(clubService.findClub("Non-existent Club", "NC")).thenReturn(Optional.empty());

        // Act
        Optional<ClubDto> result = matchResultService.initClub(clubResponse);

        // Assert
        assertTrue(result.isPresent());
        ClubDto clubDto = result.get();
        assertNull(clubDto.getId());
        assertEquals("Non-existent Club", clubDto.getName());
        assertEquals("NC", clubDto.getAbbreviation());
        verify(clubService, times(1)).findClub("Non-existent Club", "NC");
    }

    @Test
    public void testInitMatch_withExistingMatchAndNoNewerScores_thenReturnsEmptyOptional() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchName("Existing Match");
        matchResponse.setMatchDate(LocalDate.of(2025, 1, 15).atStartOfDay());
        ipscResponse.setMatch(matchResponse);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setLastModified(LocalDateTime.of(2025, 1, 10, 10, 0));
        ipscResponse.setScores(List.of(scoreResponse));

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(1L);
        existingMatch.setName("Existing Match");
        existingMatch.setScheduledDate(LocalDate.of(2025, 1, 15));
        existingMatch.setDateUpdated(LocalDateTime.of(2025, 1, 20, 10, 0));

        when(matchService.findMatch("Existing Match", LocalDate.of(2025, 1, 15).atStartOfDay()))
                .thenReturn(Optional.of(existingMatch));

        ClubDto clubDto = new ClubDto("Test Club", "TC");

        // Act
        Optional<MatchDto> result = matchResultService.initMatch(ipscResponse, clubDto);

        // Assert
        assertTrue(result.isEmpty());
        verify(matchService, times(1)).findMatch("Existing Match", LocalDate.of(2025, 1, 15).atStartOfDay());
    }

    @Test
    public void testInitMatch_withExistingMatchAndNewerScores_thenReturnsPopulatedMatchDto() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchName("Existing Match");
        matchResponse.setMatchDate(LocalDate.of(2025, 1, 15).atStartOfDay());
        ipscResponse.setMatch(matchResponse);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setLastModified(LocalDateTime.of(2025, 1, 25, 10, 0));
        ipscResponse.setScores(List.of(scoreResponse));

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(1L);
        existingMatch.setName("Existing Match");
        existingMatch.setScheduledDate(LocalDate.of(2025, 1, 15));
        existingMatch.setDateUpdated(LocalDateTime.of(2025, 1, 20, 10, 0));

        when(matchService.findMatch("Existing Match", LocalDate.of(2025, 1, 15).atStartOfDay()))
                .thenReturn(Optional.of(existingMatch));

        ClubDto clubDto = new ClubDto(1L, "Test Club", "TC");

        // Act
        Optional<MatchDto> result = matchResultService.initMatch(ipscResponse, clubDto);

        // Assert
        assertTrue(result.isPresent());
        MatchDto matchDto = result.get();
        assertEquals(1L, matchDto.getId());
        assertEquals(clubDto, matchDto.getClub());
        verify(matchService, times(1)).findMatch("Existing Match", LocalDate.of(2025, 1, 15).atStartOfDay());
    }

    @Test
    public void testInitMatch_withNonExistingMatch_thenReturnsNewMatchDto() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchName("New Match");
        matchResponse.setMatchDate(LocalDate.of(2025, 2, 1).atStartOfDay());
        ipscResponse.setMatch(matchResponse);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setLastModified(LocalDateTime.of(2025, 2, 1, 10, 0));
        ipscResponse.setScores(List.of(scoreResponse));

        when(matchService.findMatch("New Match", LocalDate.of(2025, 2, 1).atStartOfDay()))
                .thenReturn(Optional.empty());

        ClubDto clubDto = new ClubDto(null, "New Club", "NC");

        // Act
        Optional<MatchDto> result = matchResultService.initMatch(ipscResponse, clubDto);

        // Assert
        assertTrue(result.isPresent());
        MatchDto matchDto = result.get();
        assertNull(matchDto.getId());
        assertEquals(clubDto, matchDto.getClub());
        verify(matchService, times(1)).findMatch("New Match", LocalDate.of(2025, 2, 1).atStartOfDay());
    }

    @Test
    public void testInitStages_withNullStageResponses_thenReturnsEmptyList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        // Act
        List<MatchStageDto> result = matchResultService.initStages(matchDto, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(matchStageService);
    }

    @Test
    public void testInitStages_withEmptyStageResponses_thenReturnsEmptyList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        // Act
        List<MatchStageDto> result = matchResultService.initStages(matchDto, List.of());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(matchStageService);
    }

    @Test
    public void testInitStages_withExistingMatchStages_thenReturnsPopulatedMatchStageDtoList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse1 = new StageResponse();
        stageResponse1.setStageId(1);
        stageResponse1.setStageName("Stage 1");

        StageResponse stageResponse2 = new StageResponse();
        stageResponse2.setStageId(2);
        stageResponse2.setStageName("Stage 2");

        List<StageResponse> stageResponses = List.of(stageResponse1, stageResponse2);

        IpscMatch match = new IpscMatch();
        match.setId(1L);

        IpscMatchStage matchStage1 = new IpscMatchStage();
        matchStage1.setId(10L);
        matchStage1.setMatch(match);
        matchStage1.setStageNumber(1);

        IpscMatchStage matchStage2 = new IpscMatchStage();
        matchStage2.setId(20L);
        matchStage2.setMatch(match);
        matchStage2.setStageNumber(2);

        when(matchStageService.findMatchStage(1L, 1)).thenReturn(Optional.of(matchStage1));
        when(matchStageService.findMatchStage(1L, 2)).thenReturn(Optional.of(matchStage2));

        // Act
        List<MatchStageDto> result = matchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(20L, result.get(1).getId());
        verify(matchStageService, times(1)).findMatchStage(1L, 1);
        verify(matchStageService, times(1)).findMatchStage(1L, 2);
    }

    @Test
    public void testInitStages_withNonExistingMatchStages_thenReturnsNewMatchStageDtoList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        StageResponse stageResponse1 = new StageResponse();
        stageResponse1.setStageId(1);
        stageResponse1.setStageName("New Stage 1");

        StageResponse stageResponse2 = new StageResponse();
        stageResponse2.setStageId(2);
        stageResponse2.setStageName("New Stage 2");

        List<StageResponse> stageResponses = List.of(stageResponse1, stageResponse2);

        when(matchStageService.findMatchStage(1L, 1)).thenReturn(Optional.empty());
        when(matchStageService.findMatchStage(1L, 2)).thenReturn(Optional.empty());

        // Act
        List<MatchStageDto> result = matchResultService.initStages(matchDto, stageResponses);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNull(result.get(0).getId());
        assertNull(result.get(1).getId());
        verify(matchStageService, times(1)).findMatchStage(1L, 1);
        verify(matchStageService, times(1)).findMatchStage(1L, 2);
    }

    @Test
    public void testInitScores_withNullMatchResultsDto_thenReturnsEarly() {
        // Arrange
        IpscResponse ipscResponse = new IpscResponse();

        // Act
        matchResultService.initScores(null, ipscResponse);

        // Assert
        verifyNoInteractions(competitorService, matchCompetitorService, matchStageCompetitorService);
    }

    @Test
    public void testInitScores_withNullIpscResponse_thenReturnsEarly() {
        // Arrange
        MatchResultsDto matchResultsDto = new MatchResultsDto();

        // Act
        matchResultService.initScores(matchResultsDto, null);

        // Assert
        verifyNoInteractions(competitorService, matchCompetitorService, matchStageCompetitorService);
    }

    @Test
    public void testInitScores_withNullScores_thenReturnsEarly() {
        // Arrange
        MatchResultsDto matchResultsDto = new MatchResultsDto();
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(null);

        // Act
        matchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        verifyNoInteractions(competitorService, matchCompetitorService, matchStageCompetitorService);
    }

    @Test
    public void testInitScores_withNullMembers_thenReturnsEarly() {
        // Arrange
        MatchResultsDto matchResultsDto = new MatchResultsDto();
        IpscResponse ipscResponse = new IpscResponse();
        ipscResponse.setScores(List.of());
        ipscResponse.setMembers(null);

        // Act
        matchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        verifyNoInteractions(competitorService, matchCompetitorService, matchStageCompetitorService);
    }

    @Test
    public void testInitScores_withNoMembersWithScores_thenInitializesEmptyCompetitorsList() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        MatchResultsDto matchResultsDto = new MatchResultsDto(matchDto);

        IpscResponse ipscResponse = new IpscResponse();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMemberId(100);
        ipscResponse.setScores(List.of(scoreResponse));

        MemberRequest memberResponse = new MemberRequest();
        memberResponse.setMemberId(200);
        ipscResponse.setMembers(List.of(memberResponse));

        // Act
        matchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        assertNotNull(matchResultsDto.getCompetitors());
        assertTrue(matchResultsDto.getCompetitors().isEmpty());
        verifyNoInteractions(competitorService, matchCompetitorService, matchStageCompetitorService);
    }

    @Test
    public void testInitScores_withExistingCompetitorsAndMatchCompetitors_thenInitializesCorrectly() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        MatchResultsDto matchResultsDto = new MatchResultsDto(matchDto);

        IpscResponse ipscResponse = new IpscResponse();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMemberId(100);
        scoreResponse.setStageId(1);
        ipscResponse.setScores(List.of(scoreResponse));

        MemberRequest memberResponse = new MemberRequest();
        memberResponse.setMemberId(100);
        memberResponse.setIcsAlias("ALIAS100");
        memberResponse.setFirstName("John");
        memberResponse.setLastName("Doe");
        ipscResponse.setMembers(List.of(memberResponse));

        Competitor existingCompetitor = new Competitor();
        existingCompetitor.setId(10L);
        existingCompetitor.setCompetitorNumber("ALIAS100");

        MatchCompetitor existingMatchCompetitor = new MatchCompetitor();
        existingMatchCompetitor.setId(20L);

        when(competitorService.findCompetitor("ALIAS100", "John", "Doe", null))
                .thenReturn(Optional.of(existingCompetitor));
        when(matchCompetitorService.findMatchCompetitor(1L, 10L))
                .thenReturn(Optional.of(existingMatchCompetitor));

        // Act
        matchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        assertNotNull(matchResultsDto.getCompetitors());
        assertEquals(1, matchResultsDto.getCompetitors().size());
        assertEquals(10L, matchResultsDto.getCompetitors().getFirst().getId());
        assertEquals(1, matchResultsDto.getMatchCompetitors().size());
        verify(competitorService, times(1)).findCompetitor("ALIAS100", "John", "Doe", null);
        verify(matchCompetitorService, times(1)).findMatchCompetitor(1L, 10L);
    }

    @Test
    public void testInitScores_withNonExistingCompetitorsAndMatchCompetitors_thenCreatesNew() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        MatchResultsDto matchResultsDto = new MatchResultsDto(matchDto);

        IpscResponse ipscResponse = new IpscResponse();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMemberId(100);
        scoreResponse.setStageId(1);
        ipscResponse.setScores(List.of(scoreResponse));

        MemberRequest memberResponse = new MemberRequest();
        memberResponse.setMemberId(100);
        memberResponse.setIcsAlias("ALIAS100");
        memberResponse.setFirstName("John");
        memberResponse.setLastName("Doe");
        ipscResponse.setMembers(List.of(memberResponse));

        when(competitorService.findCompetitor("ALIAS100", "John", "Doe", null))
                .thenReturn(Optional.empty());
        when(matchCompetitorService.findMatchCompetitor(1L, null))
                .thenReturn(Optional.empty());

        // Act
        matchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        assertNotNull(matchResultsDto.getCompetitors());
        assertEquals(1, matchResultsDto.getCompetitors().size());
        assertNull(matchResultsDto.getCompetitors().getFirst().getId());
        assertEquals(1, matchResultsDto.getMatchCompetitors().size());
        verify(competitorService, times(1)).findCompetitor("ALIAS100", "John", "Doe", null);
        verify(matchCompetitorService, times(1)).findMatchCompetitor(1L, null);
    }

    @Test
    public void testInitScores_withMatchStageCompetitorsScores_thenInitializesCorrectly() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(1L);

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setId(50L);
        stageDto.setStageNumber(1);

        MatchResultsDto matchResultsDto = new MatchResultsDto(matchDto);
        matchResultsDto.setStages(List.of(stageDto));

        IpscResponse ipscResponse = new IpscResponse();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setMemberId(100);
        scoreResponse.setStageId(1);
        ipscResponse.setScores(List.of(scoreResponse));

        MemberRequest memberResponse = new MemberRequest();
        memberResponse.setMemberId(100);
        memberResponse.setIcsAlias("ALIAS100");
        memberResponse.setFirstName("John");
        memberResponse.setLastName("Doe");
        ipscResponse.setMembers(List.of(memberResponse));

        Competitor existingCompetitor = new Competitor();
        existingCompetitor.setId(10L);

        MatchCompetitor existingMatchCompetitor = new MatchCompetitor();
        existingMatchCompetitor.setId(20L);

        MatchStageCompetitor existingMatchStageCompetitor = new MatchStageCompetitor();
        existingMatchStageCompetitor.setId(30L);

        when(competitorService.findCompetitor("ALIAS100", "John", "Doe", null))
                .thenReturn(Optional.of(existingCompetitor));
        when(matchCompetitorService.findMatchCompetitor(1L, 10L))
                .thenReturn(Optional.of(existingMatchCompetitor));
        when(matchStageCompetitorService.findMatchStageCompetitor(50L, 10L))
                .thenReturn(Optional.of(existingMatchStageCompetitor));

        // Act
        matchResultService.initScores(matchResultsDto, ipscResponse);

        // Assert
        assertNotNull(matchResultsDto.getMatchStageCompetitors());
        assertEquals(1, matchResultsDto.getMatchStageCompetitors().size());
        assertEquals(30L, matchResultsDto.getMatchStageCompetitors().getFirst().getId());
        verify(matchStageCompetitorService, times(1)).findMatchStageCompetitor(50L, 10L);
    }
}
