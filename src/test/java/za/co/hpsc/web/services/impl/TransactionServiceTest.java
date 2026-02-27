package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.domain.DtoMapping;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransactionServiceTest {

    @Mock
    private PlatformTransactionManager transactionManager;
    @Mock
    private ClubRepository clubRepository;
    @Mock
    private CompetitorRepository competitorRepository;
    @Mock
    private IpscMatchRepository ipscMatchRepository;
    @Mock
    private IpscMatchStageRepository ipscMatchStageRepository;
    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;
    @Mock
    private MatchStageCompetitorRepository matchStageCompetitorRepository;
    @Mock
    private TransactionStatus transactionStatus;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    // =====================================================================
    // Tests for saveMatchResults - Input Validation and Null Handling
    // =====================================================================

    @Test
    public void testSaveMatchResults_whenDtoMappingIsNull_thenReturnsEmpty() {
        // Arrange

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(null)
        );

        // Assert
        assertTrue(result.isEmpty());
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
        assertTrue(result.isEmpty());
        verify(transactionManager, never()).getTransaction(any());
    }


    // =====================================================================
    // Tests for saveMatchResults - Valid Data Processing
    // =====================================================================

    @Test
    public void testSaveMatchResults_whenMatchProvided_thenSavesMatchAndCommits() {
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
        verify(competitorRepository, never()).saveAll(anyList());
        verify(ipscMatchStageRepository, never()).saveAll(anyList());
        verify(matchCompetitorRepository, never()).saveAll(anyList());
        verify(matchStageCompetitorRepository, never()).saveAll(anyList());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenClubProvided_thenSavesClubAndMatch() {
        // Arrange
        MatchDto matchDto = buildMatchDto(100, null);
        ClubDto clubDto = buildClubDto(100L, "Test Club");
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
        verify(clubRepository, times(1)).save(club);
        verify(ipscMatchRepository, times(1)).save(any(IpscMatch.class));
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenFullMappingProvided_thenSavesAllEntities() {
        // Arrange
        MatchDto matchDto = buildMatchDto(101, 1L);
        ClubDto clubDto = buildClubDto(200L, "Full Club");
        CompetitorDto competitorDto = buildCompetitorDto(300L, 1);
        MatchStageDto matchStageDto = buildMatchStageDto(matchDto, 1);
        MatchCompetitorDto matchCompetitorDto = buildMatchCompetitorDto(competitorDto, matchDto);
        MatchStageCompetitorDto matchStageCompetitorDto =
                buildMatchStageCompetitorDto(competitorDto, matchStageDto);

        DtoMapping dtoMapping = buildFullMapping(matchDto, clubDto, competitorDto,
                matchStageDto, matchCompetitorDto, matchStageCompetitorDto);

        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        when(clubRepository.findById(clubDto.getId())).thenReturn(Optional.of(new Club()));
        when(ipscMatchRepository.findById(matchDto.getId())).thenReturn(Optional.of(new IpscMatch()));

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        verify(clubRepository, times(1)).save(any(Club.class));
        verify(ipscMatchRepository, times(1)).save(any(IpscMatch.class));
        verify(competitorRepository, times(1)).saveAll(anyList());
        verify(ipscMatchStageRepository, times(1)).saveAll(anyList());
        verify(matchCompetitorRepository, times(1)).saveAll(anyList());
        verify(matchStageCompetitorRepository, times(1)).saveAll(anyList());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenMatchNameIsBlank_thenStillCommits() {
        // Arrange
        MatchDto matchDto = buildMatchDto(100, null);
        matchDto.setName("   ");
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
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenEmptyCollections_thenSkipsSaveAll() {
        // Arrange
        MatchDto matchDto = buildMatchDto(100, null);
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
        verify(competitorRepository, never()).saveAll(anyList());
        verify(ipscMatchStageRepository, never()).saveAll(anyList());
        verify(matchCompetitorRepository, never()).saveAll(anyList());
        verify(matchStageCompetitorRepository, never()).saveAll(anyList());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenNullMaps_thenRollsBackAndThrowsFatalException() {
        // Arrange
        MatchDto matchDto = buildMatchDto(100, null);
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setCompetitorMap(null);
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        // Act & Assert
        assertThrows(FatalException.class, () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).getTransaction(any());
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }


    // =====================================================================
    // Helper methods for building test data
    // =====================================================================

    private static ClubDto buildClubDto(Long clubId, String name) {
        ClubDto clubDto = new ClubDto();
        clubDto.setId(clubId);
        clubDto.setName(name);
        clubDto.setAbbreviation("TC");
        return clubDto;
    }

    private static MatchDto buildMatchDto(int matchIndex, Long matchId) {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(matchIndex);
        matchDto.setId(matchId);
        matchDto.setName("Match " + matchIndex);
        matchDto.setScheduledDate(LocalDateTime.now());
        return matchDto;
    }

    private static CompetitorDto buildCompetitorDto(Long competitorId, int competitorIndex) {
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(competitorId);
        competitorDto.setIndex(competitorIndex);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C" + competitorIndex);
        return competitorDto;
    }

    private static MatchStageDto buildMatchStageDto(MatchDto matchDto, int stageNumber) {
        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setMatch(matchDto);
        matchStageDto.setStageNumber(stageNumber);
        matchStageDto.setStageName("Stage " + stageNumber);
        return matchStageDto;
    }

    private static MatchCompetitorDto buildMatchCompetitorDto(CompetitorDto competitorDto, MatchDto matchDto) {
        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setMatch(matchDto);
        matchCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
        matchCompetitorDto.setMatchIndex(matchDto.getIndex());
        return matchCompetitorDto;
    }

    private static MatchStageCompetitorDto buildMatchStageCompetitorDto(CompetitorDto competitorDto,
                                                                        MatchStageDto matchStageDto) {
        MatchStageCompetitorDto matchStageCompetitorDto = new MatchStageCompetitorDto();
        matchStageCompetitorDto.setCompetitor(competitorDto);
        matchStageCompetitorDto.setMatchStage(matchStageDto);
        matchStageCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
        matchStageCompetitorDto.setMatchStageIndex(matchStageDto.getIndex());
        return matchStageCompetitorDto;
    }

    private static DtoMapping buildFullMapping(MatchDto matchDto,
                                               ClubDto clubDto,
                                               CompetitorDto competitorDto,
                                               MatchStageDto matchStageDto,
                                               MatchCompetitorDto matchCompetitorDto,
                                               MatchStageCompetitorDto matchStageCompetitorDto) {
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setClub(clubDto);

        HashMap<java.util.UUID, CompetitorDto> competitorMap = new HashMap<>();
        competitorMap.put(competitorDto.getUuid(), competitorDto);
        dtoMapping.setCompetitorMap(competitorMap);

        HashMap<java.util.UUID, MatchStageDto> matchStageMap = new HashMap<>();
        matchStageMap.put(matchStageDto.getUuid(), matchStageDto);
        dtoMapping.setMatchStageMap(matchStageMap);

        HashMap<java.util.UUID, MatchCompetitorDto> matchCompetitorMap = new HashMap<>();
        matchCompetitorMap.put(matchCompetitorDto.getUuid(), matchCompetitorDto);
        dtoMapping.setMatchCompetitorMap(matchCompetitorMap);

        HashMap<java.util.UUID, MatchStageCompetitorDto> matchStageCompetitorMap = new HashMap<>();
        matchStageCompetitorMap.put(matchStageCompetitorDto.getUuid(), matchStageCompetitorDto);
        dtoMapping.setMatchStageCompetitorMap(matchStageCompetitorMap);
        return dtoMapping;
    }
}
