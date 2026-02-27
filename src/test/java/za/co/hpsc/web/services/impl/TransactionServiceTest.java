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
import za.co.hpsc.web.models.ipsc.domain.DtoMapping;
import za.co.hpsc.web.models.ipsc.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// TODO: add more tests
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
    // Tests for saveMatchResults - Input Validation and Null Handling
    // =====================================================================

    @Test
    public void testSaveMatchResults_whenMatchResultsIsNull_thenReturnsEmpty() {
        // Arrange
        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(null)
        );

        // Assert
        assertFalse(result.isPresent());
        verify(transactionManager, never()).getTransaction(any());
    }

    @Test
    public void testSaveMatchResults_whenMatchIsNull_thenReturnsEmpty() {
        // Arrange
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(null);

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertFalse(result.isPresent());
        verify(transactionManager, never()).getTransaction(any());
    }

    @Test
    public void testSaveMatchResults_whenDomainServiceReturnsHolderWithNullMatch_thenReturnsEmpty() {
        // Arrange
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(null);

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertFalse(result.isPresent());
        verify(ipscMatchRepository, never()).save(any());
        verify(clubRepository, never()).save(any());
    }

    // =====================================================================
    // Tests for saveMatchResults - Valid Data Processing
    // =====================================================================

    @Test
    public void testSaveMatchResults_whenMatchHasOnlyRequiredFields_thenSavesMatch() {
        // Arrange
        MatchDto matchDto = buildMatchDto(100, null);
        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName(matchDto.getName());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ipscMatch.getId(), result.get().getId());
        verify(transactionManager).getTransaction(any());
        verify(ipscMatchRepository, times(1)).save(any(IpscMatch.class));
        verify(clubRepository, never()).save(any());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenMatchHasClub_thenSavesBothClubAndMatch() {
        // Arrange
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
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setClub(clubDto);

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        when(clubRepository.findById(clubDto.getId())).thenReturn(Optional.of(club));

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ipscMatch.getId(), result.get().getId());
        verify(clubRepository, times(1)).save(club);
        verify(ipscMatchRepository, times(1)).save(any(IpscMatch.class));
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenFullMatchResults_thenSavesAllEntities() {
        // Arrange
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
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setClub(clubDto);
        dtoMapping.setCompetitorMap(new HashMap<>());
        dtoMapping.setMatchStageMap(new HashMap<>());
        dtoMapping.setMatchCompetitorMap(new HashMap<>());
        dtoMapping.setMatchStageCompetitorMap(new HashMap<>());

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        when(clubRepository.findById(clubDto.getId())).thenReturn(Optional.of(club));
        when(ipscMatchRepository.findById(matchDto.getId())).thenReturn(Optional.of(ipscMatch));

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ipscMatch.getId(), result.get().getId());
        verify(clubRepository, times(1)).save(club);
        verify(ipscMatchRepository, times(1)).save(ipscMatch);
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenDomainServiceReturnsResults_thenReturnsResults() {
        // Arrange
        MatchDto matchDto = buildMatchDto(100, null);
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        verify(transactionManager).getTransaction(any());
        verify(ipscMatchRepository, times(1)).save(any(IpscMatch.class));
        verify(clubRepository, never()).save(any());
        verify(transactionManager).commit(transactionStatus);
    }

    // =====================================================================
    // Tests for saveMatchResults - Edge Cases and Boundary Conditions
    // =====================================================================

    @Test
    public void testSaveMatchResults_whenExceptionOccurs_thenRollsBackAndThrowsFatalException() {
        // Arrange
        MatchDto matchDto = buildMatchDto(100, 1L);
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        when(ipscMatchRepository.findById(100L)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(FatalException.class, () ->
                transactionService.saveMatchResults(dtoMapping)
        );
        verify(transactionManager).getTransaction(any());
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void testSaveMatchResults_whenMatchNameIsBlank_thenStillProcesses() {
        // Arrange
        MatchDto matchDto = buildMatchDto(100, null);
        matchDto.setName("   ");
        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName("   ");
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        verify(ipscMatchRepository, times(1)).save(any(IpscMatch.class));
        verify(clubRepository, never()).save(any());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenEmptyCollections_thenSavesMatch() {
        // Arrange
        MatchDto matchDto = buildMatchDto(100, null);
        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName(matchDto.getName());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setMatchStageMap(new HashMap<>());
        dtoMapping.setCompetitorMap(new HashMap<>());
        dtoMapping.setMatchCompetitorMap(new HashMap<>());
        dtoMapping.setMatchStageCompetitorMap(new HashMap<>());

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        verify(ipscMatchRepository, times(1)).save(any(IpscMatch.class));
        verify(clubRepository, never()).save(any());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenNullCollections_thenSavesMatch() {
        // Arrange
        MatchDto matchDto = buildMatchDto(100, null);
        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setName(matchDto.getName());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        verify(ipscMatchRepository, times(1)).save(any(IpscMatch.class));
        verify(clubRepository, never()).save(any());
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
}
