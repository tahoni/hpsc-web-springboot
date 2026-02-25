package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.domain.MatchEntityHolder;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.services.DomainService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private DomainService domainService;
    @Mock
    private PlatformTransactionManager transactionManager;
    @Mock
    private ClubRepository clubRepository;
    @Mock
    private IpscMatchRepository ipscMatchRepository;
    @Mock
    private TransactionStatus transactionStatus;

    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;

    // =====================================================================
    // Tests for saveMatchResults - Consolidated edge cases + partial/full
    // =====================================================================

    @Test
    public void saveMatchResults_whenMatchResultsIsNull_thenReturnsEmpty() {
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionServiceImpl.saveMatchResults(null)
        );

        assertFalse(result.isPresent());
        verify(transactionManager, never()).getTransaction(any());
        verify(domainService, never()).initMatchEntities(any());
    }

    @Test
    public void saveMatchResults_whenMatchIsNull_thenReturnsEmpty() {
        MatchResultsDto matchResults = new MatchResultsDto();
        matchResults.setMatch(null);

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionServiceImpl.saveMatchResults(matchResults)
        );

        assertFalse(result.isPresent());
        verify(transactionManager, never()).getTransaction(any());
        verify(domainService, never()).initMatchEntities(any());
    }

    @Test
    public void saveMatchResults_whenMatchHasOnlyRequiredFields_thenSavesMatch() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName(matchDto.getName());

        MatchEntityHolder entityHolder = new MatchEntityHolder();
        entityHolder.setMatch(ipscMatch);

        when(domainService.initMatchEntities(matchResults)).thenReturn(Optional.of(entityHolder));

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionServiceImpl.saveMatchResults(matchResults)
        );

        assertTrue(result.isPresent());
        assertEquals(ipscMatch, result.get());
        verify(transactionManager).getTransaction(any());
        verify(domainService).initMatchEntities(matchResults);
        verify(ipscMatchRepository).save(ipscMatch);
        verify(clubRepository, never()).save(any());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void saveMatchResults_whenMatchHasClub_thenSavesBothClubAndMatch() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("TC");

        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        matchResults.setClub(clubDto);

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName(matchDto.getName());

        Club club = new Club();
        club.setName(clubDto.getName());

        MatchEntityHolder entityHolder = new MatchEntityHolder();
        entityHolder.setMatch(ipscMatch);
        entityHolder.setClub(club);

        when(domainService.initMatchEntities(matchResults)).thenReturn(Optional.of(entityHolder));

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionServiceImpl.saveMatchResults(matchResults)
        );

        assertTrue(result.isPresent());
        assertEquals(ipscMatch, result.get());
        verify(clubRepository).save(club);
        verify(ipscMatchRepository).save(ipscMatch);
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void saveMatchResults_withFullMatchResults_thenSavesAllEntities() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, 1L);
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Full Club");

        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        matchResults.setClub(clubDto);
        matchResults.setStages(List.of(buildMatchStageDto(matchDto, 10, 10, 10L)));
        matchResults.setCompetitors(new ArrayList<>());
        matchResults.setMatchCompetitors(new ArrayList<>());
        matchResults.setMatchStageCompetitors(new ArrayList<>());

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setId(1L);
        ipscMatch.setName(matchDto.getName());

        Club club = new Club();
        club.setName(clubDto.getName());

        MatchEntityHolder entityHolder = new MatchEntityHolder();
        entityHolder.setMatch(ipscMatch);
        entityHolder.setClub(club);
        entityHolder.setMatchStages(new ArrayList<>());
        entityHolder.setCompetitors(new ArrayList<>());
        entityHolder.setMatchCompetitors(new ArrayList<>());
        entityHolder.setMatchStageCompetitors(new ArrayList<>());

        when(domainService.initMatchEntities(matchResults)).thenReturn(Optional.of(entityHolder));

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionServiceImpl.saveMatchResults(matchResults)
        );

        assertTrue(result.isPresent());
        assertEquals(ipscMatch, result.get());
        verify(clubRepository).save(club);
        verify(ipscMatchRepository).save(ipscMatch);
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void saveMatchResults_whenDomainServiceReturnsEmpty_thenReturnsEmpty() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);

        when(domainService.initMatchEntities(matchResults)).thenReturn(Optional.empty());

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionServiceImpl.saveMatchResults(matchResults)
        );

        assertFalse(result.isPresent());
        verify(transactionManager).getTransaction(any());
        verify(domainService).initMatchEntities(matchResults);
        verify(ipscMatchRepository, never()).save(any());
        verify(clubRepository, never()).save(any());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void saveMatchResults_whenDomainServiceReturnsHolderWithNullMatch_thenReturnsEmpty() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);

        MatchEntityHolder entityHolder = new MatchEntityHolder();
        entityHolder.setMatch(null);

        when(domainService.initMatchEntities(matchResults)).thenReturn(Optional.of(entityHolder));

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionServiceImpl.saveMatchResults(matchResults)
        );

        assertFalse(result.isPresent());
        verify(ipscMatchRepository, never()).save(any());
        verify(clubRepository, never()).save(any());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void saveMatchResults_whenExceptionOccurs_thenRollsBackAndThrowsFatalException() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);

        when(domainService.initMatchEntities(matchResults))
                .thenThrow(new RuntimeException("Database error"));

        FatalException exception = assertThrows(FatalException.class, () ->
                transactionServiceImpl.saveMatchResults(matchResults)
        );

        assertTrue(exception.getMessage().contains("Unable to save the match"));
        assertTrue(exception.getMessage().contains("Database error"));
        verify(transactionManager).getTransaction(any());
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void saveMatchResults_whenMatchNameIsBlank_thenStillProcesses() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);
        matchDto.setName("   ");
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName("   ");

        MatchEntityHolder entityHolder = new MatchEntityHolder();
        entityHolder.setMatch(ipscMatch);

        when(domainService.initMatchEntities(matchResults)).thenReturn(Optional.of(entityHolder));

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionServiceImpl.saveMatchResults(matchResults)
        );

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).save(ipscMatch);
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void saveMatchResults_withEmptyCollections_thenSavesMatch() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        matchResults.setCompetitors(new ArrayList<>());
        matchResults.setStages(new ArrayList<>());
        matchResults.setMatchCompetitors(new ArrayList<>());
        matchResults.setMatchStageCompetitors(new ArrayList<>());
        matchResults.setScores(new ArrayList<>());

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName(matchDto.getName());

        MatchEntityHolder entityHolder = new MatchEntityHolder();
        entityHolder.setMatch(ipscMatch);
        entityHolder.setMatchStages(new ArrayList<>());
        entityHolder.setCompetitors(new ArrayList<>());
        entityHolder.setMatchCompetitors(new ArrayList<>());
        entityHolder.setMatchStageCompetitors(new ArrayList<>());

        when(domainService.initMatchEntities(matchResults)).thenReturn(Optional.of(entityHolder));

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionServiceImpl.saveMatchResults(matchResults)
        );

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).save(ipscMatch);
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void saveMatchResults_withNullCollections_thenSavesMatch() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);
        MatchResultsDto matchResults = new MatchResultsDto(matchDto);
        matchResults.setCompetitors(null);
        matchResults.setStages(null);
        matchResults.setMatchCompetitors(null);
        matchResults.setMatchStageCompetitors(null);
        matchResults.setScores(null);

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName(matchDto.getName());

        MatchEntityHolder entityHolder = new MatchEntityHolder();
        entityHolder.setMatch(ipscMatch);

        when(domainService.initMatchEntities(matchResults)).thenReturn(Optional.of(entityHolder));

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionServiceImpl.saveMatchResults(matchResults)
        );

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).save(ipscMatch);
        verify(transactionManager).commit(transactionStatus);
    }


    // =====================================================================
    // Helper methods for building test data
    // =====================================================================

    private static MatchDto buildMatchDto(int matchIndex, Long matchId) {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(matchIndex);
        matchDto.setId(matchId);
        matchDto.setName("Match " + matchIndex);
        matchDto.setScheduledDate(LocalDateTime.now());
        return matchDto;
    }

    private static MatchStageDto buildMatchStageDto(MatchDto matchDto, int stageId, int stageNumber, Long stagePkId) {
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchDto);
        stageDto.setIndex(stageId);
        stageDto.setStageNumber(stageNumber);
        stageDto.setId(stagePkId);
        return stageDto;
    }
}
