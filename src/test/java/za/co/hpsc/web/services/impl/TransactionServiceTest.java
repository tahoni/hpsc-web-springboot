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
import za.co.hpsc.web.models.ipsc.domain.DtoToEntityMapping;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// TODO: add AAA comments
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private PlatformTransactionManager transactionManager;
    @Mock
    private ClubRepository clubRepository;
    @Mock
    private IpscMatchRepository ipscMatchRepository;
    @Mock
    private TransactionStatus transactionStatus;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    // =====================================================================
    // Tests for saveMatchResults - Consolidated edge cases + partial/full
    // =====================================================================

    @Test
    public void testSaveMatchResults_whenMatchResultsIsNull_thenReturnsEmpty() {
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(null)
        );

        assertFalse(result.isPresent());
        verify(transactionManager, never()).getTransaction(any());
    }

    @Test
    public void testSaveMatchResults_whenMatchIsNull_thenReturnsEmpty() {
        DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping();
        dtoToEntityMapping.setMatch(null);

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoToEntityMapping)
        );

        assertFalse(result.isPresent());
        verify(transactionManager, never()).getTransaction(any());
    }

    @Test
    public void testSaveMatchResults_whenMatchHasOnlyRequiredFields_thenSavesMatch() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName(matchDto.getName());

        DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping();
        dtoToEntityMapping.setMatch(matchDto);

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoToEntityMapping)
        );

        assertTrue(result.isPresent());
        assertEquals(ipscMatch.getId(), result.get().getId());
        verify(transactionManager).getTransaction(any());
        verify(ipscMatchRepository).save(any(IpscMatch.class));
        verify(clubRepository, never()).save(any());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenMatchHasClub_thenSavesBothClubAndMatch() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);
        ClubDto clubDto = new ClubDto();
        clubDto.setId(100L);
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("TC");

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName(matchDto.getName());

        Club club = new Club();
        club.setId(clubDto.getId());
        club.setName(clubDto.getName());

        DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping();
        dtoToEntityMapping.setMatch(matchDto);
        dtoToEntityMapping.setClub(clubDto);

        when(clubRepository.findById(clubDto.getId())).thenReturn(Optional.of(club));

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoToEntityMapping)
        );

        assertTrue(result.isPresent());
        assertEquals(ipscMatch.getId(), result.get().getId());
        verify(clubRepository).save(any(Club.class));
        verify(ipscMatchRepository).save(any(IpscMatch.class));
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_withFullMatchResults_thenSavesAllEntities() {
        MatchDto matchDto = buildMatchDto(100, 1L);
        ClubDto clubDto = new ClubDto();
        clubDto.setId(100L);
        clubDto.setName("Full Club");

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setId(1L);
        ipscMatch.setName(matchDto.getName());

        Club club = new Club();
        club.setId(clubDto.getId());
        club.setName(clubDto.getName());

        DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping();
        dtoToEntityMapping.setMatch(matchDto);
        dtoToEntityMapping.setClub(clubDto);
        dtoToEntityMapping.setCompetitorMap(new HashMap<>());
        dtoToEntityMapping.setMatchStageMap(new HashMap<>());
        dtoToEntityMapping.setMatchCompetitorMap(new HashMap<>());
        dtoToEntityMapping.setMatchStageCompetitorMap(new HashMap<>());

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        when(clubRepository.findById(clubDto.getId())).thenReturn(Optional.of(club));
        when(ipscMatchRepository.findById(matchDto.getId())).thenReturn(Optional.of(ipscMatch));

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoToEntityMapping)
        );

        assertTrue(result.isPresent());
        assertEquals(ipscMatch.getId(), result.get().getId());
        verify(clubRepository).save(any(Club.class));
        verify(ipscMatchRepository).save(any(IpscMatch.class));
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenDomainServiceReturnsEmpty_thenReturnsEmpty() {
        MatchDto matchDto = buildMatchDto(100, null);
        DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping();
        dtoToEntityMapping.setMatch(matchDto);

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoToEntityMapping)
        );

        assertTrue(result.isPresent());
        verify(transactionManager).getTransaction(any());
        verify(ipscMatchRepository, times(1)).save(any(IpscMatch.class));
        verify(clubRepository, never()).save(any());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenDomainServiceReturnsHolderWithNullMatch_thenReturnsEmpty() {
        MatchDto matchDto = buildMatchDto(100, null);

        DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping();
        dtoToEntityMapping.setMatch(null);

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoToEntityMapping)
        );

        assertFalse(result.isPresent());
        verify(ipscMatchRepository, never()).save(any());
        verify(clubRepository, never()).save(any());
    }

    @Test
    public void testSaveMatchResults_whenExceptionOccurs_thenRollsBackAndThrowsFatalException() {
        MatchDto matchDto = buildMatchDto(100, 1L);

        DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping();
        dtoToEntityMapping.setMatch(matchDto);

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        when(ipscMatchRepository.findById(100L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(FatalException.class, () ->
                transactionService.saveMatchResults(dtoToEntityMapping)
        );
        verify(transactionManager).getTransaction(any());
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void testSaveMatchResults_whenMatchNameIsBlank_thenStillProcesses() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);
        matchDto.setName("   ");

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName("   ");

        DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping();
        dtoToEntityMapping.setMatch(matchDto);

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoToEntityMapping)
        );

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).save(any(IpscMatch.class));
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_withEmptyCollections_thenSavesMatch() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName(matchDto.getName());

        DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping();
        dtoToEntityMapping.setMatch(matchDto);
        dtoToEntityMapping.setMatchStageMap(new HashMap<>());
        dtoToEntityMapping.setCompetitorMap(new HashMap<>());
        dtoToEntityMapping.setMatchCompetitorMap(new HashMap<>());
        dtoToEntityMapping.setMatchStageCompetitorMap(new HashMap<>());

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoToEntityMapping)
        );

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).save(any(IpscMatch.class));
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_withNullCollections_thenSavesMatch() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        MatchDto matchDto = buildMatchDto(100, null);

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName(matchDto.getName());

        DtoToEntityMapping dtoToEntityMapping = new DtoToEntityMapping();
        dtoToEntityMapping.setMatch(matchDto);

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoToEntityMapping)
        );

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).save(any(IpscMatch.class));
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
