package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.domain.DtoMapping;
import za.co.hpsc.web.models.ipsc.domain.DtoToEntityMapping;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        stubTransactionStart();

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        verify(transactionManager).getTransaction(any());
        verify(ipscMatchRepository, times(2)).save(any(IpscMatch.class));
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
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setClub(clubDto);

        stubTransactionStart();
        when(clubRepository.findById(clubDto.getId())).thenReturn(Optional.of(club));

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        verify(clubRepository, times(1)).save(club);
        verify(ipscMatchRepository, times(2)).save(any(IpscMatch.class));
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

        stubTransactionStart();
        when(clubRepository.findById(clubDto.getId())).thenReturn(Optional.of(new Club()));

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        verify(clubRepository, times(1)).save(any(Club.class));
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
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        stubTransactionStart();

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        verify(ipscMatchRepository, times(2)).save(any(IpscMatch.class));
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenEmptyCollections_thenSkipsSaveAll() {
        // Arrange
        MatchDto matchDto = buildMatchDto(100, null);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setMatchStageMap(new HashMap<>());
        dtoMapping.setCompetitorMap(new HashMap<>());
        dtoMapping.setMatchCompetitorMap(new HashMap<>());
        dtoMapping.setMatchStageCompetitorMap(new HashMap<>());
        stubTransactionStart();

        // Act
        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping)
        );

        // Assert
        assertTrue(result.isPresent());
        verify(ipscMatchRepository, times(2)).save(any(IpscMatch.class));
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
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setCompetitorMap(null);
        stubTransactionStart();

        // Act & Assert
        assertThrows(FatalException.class, () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).getTransaction(any());
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }


    // =====================================================================
    // Tests for saveMatchResults - Transaction Rollback on Repository Failure
    // =====================================================================

    @Test
    public void saveMatchResults_whenIpscMatchSaveThrows_thenRollsBackAndThrowsFatalException() {
        MatchDto matchDto = buildMatchDto(1, null);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        stubTransactionStart();
        doThrow(new RuntimeException("DB error")).when(ipscMatchRepository).save(any());

        assertThrows(FatalException.class, () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void saveMatchResults_whenCompetitorSaveAllThrows_thenRollsBackAndThrowsFatalException() {
        MatchDto matchDto = buildMatchDto(1, null);
        CompetitorDto competitorDto = buildCompetitorDto(null, 1);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        stubTransactionStart();
        doThrow(new RuntimeException("DB error")).when(competitorRepository).saveAll(anyList());

        assertThrows(FatalException.class, () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void saveMatchResults_whenMatchStageSaveAllThrows_thenRollsBackAndThrowsFatalException() {
        MatchDto matchDto = buildMatchDto(1, null);
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setMatchStageMap(singleEntryMap(stageDto.getUuid(), stageDto));
        stubTransactionStart();
        doThrow(new RuntimeException("DB error")).when(ipscMatchStageRepository).saveAll(anyList());

        assertThrows(FatalException.class, () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void saveMatchResults_whenMatchCompetitorSaveAllThrows_thenRollsBackAndThrowsFatalException() {
        MatchDto matchDto = buildMatchDto(1, null);
        CompetitorDto competitorDto = buildCompetitorDto(null, 1);
        MatchCompetitorDto matchCompetitorDto = buildMatchCompetitorDto(competitorDto, matchDto);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        dtoMapping.setMatchCompetitorMap(singleEntryMap(matchCompetitorDto.getUuid(), matchCompetitorDto));
        stubTransactionStart();
        doThrow(new RuntimeException("DB error")).when(matchCompetitorRepository).saveAll(anyList());

        assertThrows(FatalException.class, () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void saveMatchResults_whenMatchStageCompetitorSaveAllThrows_thenRollsBackAndThrowsFatalException() {
        MatchDto matchDto = buildMatchDto(1, null);
        CompetitorDto competitorDto = buildCompetitorDto(null, 1);
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);
        MatchStageCompetitorDto mscDto = buildMatchStageCompetitorDto(competitorDto, stageDto);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        dtoMapping.setMatchStageMap(singleEntryMap(stageDto.getUuid(), stageDto));
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(mscDto.getUuid(), mscDto));
        stubTransactionStart();
        doThrow(new RuntimeException("DB error")).when(matchStageCompetitorRepository).saveAll(anyList());

        assertThrows(FatalException.class, () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    // =====================================================================
    // Tests for saveMatchResults - Club Resolution
    // =====================================================================

    @Test
    public void saveMatchResults_whenClubHasNullId_thenCreatesNewClubWithoutRepositoryLookup() {
        ClubDto clubDto = new ClubDto();
        clubDto.setId(null);
        clubDto.setName("New Club");
        clubDto.setAbbreviation("NC");
        DtoMapping dtoMapping = buildMappingWithMatch(buildMatchDto(1, null));
        dtoMapping.setClub(clubDto);
        stubTransactionStart();

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping));

        assertTrue(result.isPresent());
        verify(clubRepository, never()).findById(any());
        verify(clubRepository).save(any(Club.class));
    }

    @Test
    public void saveMatchResults_whenClubIdNotFoundInRepository_thenCreatesNewClub() {
        ClubDto clubDto = buildClubDto(999L, "Missing Club");
        DtoMapping dtoMapping = buildMappingWithMatch(buildMatchDto(1, null));
        dtoMapping.setClub(clubDto);
        stubTransactionStart();
        when(clubRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping));

        assertTrue(result.isPresent());
        verify(clubRepository).findById(999L);
        verify(clubRepository).save(any(Club.class));
    }

    // =====================================================================
    // Tests for saveMatchResults - Match Resolution
    // =====================================================================

    @Test
    public void saveMatchResults_whenMatchHasExistingId_thenLoadsMatchFromRepository() {
        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(1L);
        MatchDto matchDto = buildMatchDto(1, 1L);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        stubTransactionStart();
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existingMatch));

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping));

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).findById(1L);
    }

    @Test
    public void saveMatchResults_whenMatchIdNotFoundInRepository_thenCreatesNewMatch() {
        MatchDto matchDto = buildMatchDto(1, 1L);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        stubTransactionStart();
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping));

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).findById(1L);
        verify(ipscMatchRepository, times(2)).save(any(IpscMatch.class));
    }

    // =====================================================================
    // Tests for saveMatchResults - Returned Value
    // =====================================================================

    @Test
    public void saveMatchResults_whenSuccessful_thenReturnsMatchEntityWithCorrectName() {
        MatchDto matchDto = buildMatchDto(42, null);
        matchDto.setName("Championship Match");
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        stubTransactionStart();

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping));

        assertTrue(result.isPresent());
        assertEquals("Championship Match", result.get().getName());
    }

    // =====================================================================
    // Tests for getClub (protected)
    // =====================================================================

    @Test
    public void getClub_whenNullClubDto_thenReturnsEmpty() {
        DtoToEntityMapping mapping = new DtoToEntityMapping(buildMappingWithMatch(buildMatchDto(1, null)));

        Optional<Club> result = transactionService.getClub(null, mapping);

        assertTrue(result.isEmpty());
        verify(clubRepository, never()).findById(any());
    }

    @Test
    public void getClub_whenClubDtoHasNullId_thenCreatesNewClubWithoutRepositoryLookup() {
        ClubDto clubDto = new ClubDto();
        clubDto.setId(null);
        clubDto.setName("Fresh Club");
        DtoToEntityMapping mapping = new DtoToEntityMapping(buildMappingWithMatch(buildMatchDto(1, null)));

        Optional<Club> result = transactionService.getClub(clubDto, mapping);

        assertTrue(result.isPresent());
        verify(clubRepository, never()).findById(any());
        assertEquals("Fresh Club", result.get().getName());
    }

    @Test
    public void getClub_whenClubDtoIdFound_thenUsesEntityFromRepository() {
        Club repositoryClub = new Club();
        repositoryClub.setId(5L);
        repositoryClub.setName("Old Name");
        ClubDto clubDto = buildClubDto(5L, "New Name");
        DtoToEntityMapping mapping = new DtoToEntityMapping(buildMappingWithMatch(buildMatchDto(1, null)));
        when(clubRepository.findById(5L)).thenReturn(Optional.of(repositoryClub));

        Optional<Club> result = transactionService.getClub(clubDto, mapping);

        assertTrue(result.isPresent());
        verify(clubRepository).findById(5L);
        assertEquals(5L, result.get().getId());
        assertEquals("New Name", result.get().getName());   // init() overwrites name
    }

    @Test
    public void getClub_whenClubDtoIdNotFound_thenCreatesNewClub() {
        ClubDto clubDto = buildClubDto(77L, "Missing Club");
        DtoToEntityMapping mapping = new DtoToEntityMapping(buildMappingWithMatch(buildMatchDto(1, null)));
        when(clubRepository.findById(77L)).thenReturn(Optional.empty());

        Optional<Club> result = transactionService.getClub(clubDto, mapping);

        assertTrue(result.isPresent());
        verify(clubRepository).findById(77L);
        assertEquals("Missing Club", result.get().getName());
    }

    // =====================================================================
    // Tests for getIpscMatch (protected)
    // =====================================================================

    @Test
    public void getIpscMatch_whenMatchDtoHasNullId_thenCreatesNewMatchEntity() {
        MatchDto matchDto = buildMatchDto(1, null);
        DtoToEntityMapping mapping = new DtoToEntityMapping(buildMappingWithMatch(matchDto));

        Optional<IpscMatch> result = transactionService.getIpscMatch(mapping);

        assertTrue(result.isPresent());
        assertEquals("Match 1", result.get().getName());
        verify(ipscMatchRepository, never()).findById(any());
    }

    @Test
    public void getIpscMatch_whenMatchDtoIdFound_thenLoadsFromRepository() {
        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(10L);
        MatchDto matchDto = buildMatchDto(1, 10L);
        DtoToEntityMapping mapping = new DtoToEntityMapping(buildMappingWithMatch(matchDto));
        when(ipscMatchRepository.findById(10L)).thenReturn(Optional.of(existingMatch));

        Optional<IpscMatch> result = transactionService.getIpscMatch(mapping);

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).findById(10L);
        assertEquals(10L, result.get().getId());
    }

    @Test
    public void getIpscMatch_whenMatchDtoIdNotFound_thenCreatesNewMatchEntity() {
        MatchDto matchDto = buildMatchDto(1, 10L);
        DtoToEntityMapping mapping = new DtoToEntityMapping(buildMappingWithMatch(matchDto));
        when(ipscMatchRepository.findById(10L)).thenReturn(Optional.empty());

        Optional<IpscMatch> result = transactionService.getIpscMatch(mapping);

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).findById(10L);
        assertEquals("Match 1", result.get().getName());
    }

    // =====================================================================
    // Tests for getIpscMatchStages (protected)
    // =====================================================================

    @Test
    public void getIpscMatchStages_whenMatchEntityIsNull_thenReturnsEmptyList() {
        MatchDto matchDto = buildMatchDto(1, null);
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setMatchStageMap(singleEntryMap(stageDto.getUuid(), stageDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping); // match entity is NOT set

        var result = transactionService.getIpscMatchStages(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ipscMatchStageRepository, never()).findById(any());
    }

    @Test
    public void getIpscMatchStages_whenStageHasNullId_thenCreatesNewStageEntity() {
        MatchDto matchDto = buildMatchDto(1, null);
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 2);
        stageDto.setId(null);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setMatchStageMap(singleEntryMap(stageDto.getUuid(), stageDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        transactionService.getIpscMatch(mapping); // sets match entity

        var result = transactionService.getIpscMatchStages(mapping);

        assertEquals(1, result.size());
        verify(ipscMatchStageRepository, never()).findById(any());
        assertEquals("Stage 2", result.getFirst().getStageName());
    }

    @Test
    public void getIpscMatchStages_whenStageIdFound_thenLoadsFromRepository() {
        MatchDto matchDto = buildMatchDto(1, null);
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 1);
        stageDto.setId(200L);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setMatchStageMap(singleEntryMap(stageDto.getUuid(), stageDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        transactionService.getIpscMatch(mapping); // sets match entity

        IpscMatchStage existingStage = new IpscMatchStage();
        existingStage.setId(200L);
        existingStage.setStageNumber(1);
        when(ipscMatchStageRepository.findById(200L)).thenReturn(Optional.of(existingStage));

        var result = transactionService.getIpscMatchStages(mapping);

        assertEquals(1, result.size());
        verify(ipscMatchStageRepository).findById(200L);
        assertEquals(200L, result.getFirst().getId());
    }

    @Test
    public void getIpscMatchStages_whenStageIdNotFound_thenCreatesNewStageEntity() {
        MatchDto matchDto = buildMatchDto(1, null);
        MatchStageDto stageDto = buildMatchStageDto(matchDto, 3);
        stageDto.setId(300L);
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setMatchStageMap(singleEntryMap(stageDto.getUuid(), stageDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        transactionService.getIpscMatch(mapping); // sets match entity

        var result = transactionService.getIpscMatchStages(mapping);

        assertEquals(1, result.size());
        assertEquals("Stage 3", result.getFirst().getStageName());
    }

    // =====================================================================
    // Tests for getCompetitors (protected)
    // =====================================================================

    @Test
    public void getCompetitors_whenEmptyList_thenReturnsEmptyList() {
        DtoMapping dtoMapping = buildMappingWithMatch(buildMatchDto(1, null));
        dtoMapping.setCompetitorMap(new HashMap<>());
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getCompetitors(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(competitorRepository, never()).findById(any());
    }

    @Test
    public void getCompetitors_whenCompetitorHasNullId_thenCreatesNewCompetitorEntity() {
        CompetitorDto competitorDto = buildCompetitorDto(null, 1);
        DtoMapping dtoMapping = buildMappingWithMatch(buildMatchDto(1, null));
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getCompetitors(mapping);

        assertEquals(1, result.size());
        verify(competitorRepository, never()).findById(any());
    }

    @Test
    public void getCompetitors_whenCompetitorIdFound_thenLoadsFromRepository() {
        CompetitorDto competitorDto = buildCompetitorDto(400L, 1);
        DtoMapping dtoMapping = buildMappingWithMatch(buildMatchDto(1, null));
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        Competitor existingCompetitor = new Competitor();
        existingCompetitor.setId(400L);
        when(competitorRepository.findById(400L)).thenReturn(Optional.of(existingCompetitor));

        var result = transactionService.getCompetitors(mapping);

        assertEquals(1, result.size());
        verify(competitorRepository).findById(400L);
        assertEquals(400L, result.getFirst().getId());
    }

    @Test
    public void getCompetitors_whenCompetitorIdNotFound_thenCreatesNewCompetitorEntity() {
        CompetitorDto competitorDto = buildCompetitorDto(401L, 1);
        DtoMapping dtoMapping = buildMappingWithMatch(buildMatchDto(1, null));
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        when(competitorRepository.findById(401L)).thenReturn(Optional.empty());

        var result = transactionService.getCompetitors(mapping);

        assertEquals(1, result.size());
        verify(competitorRepository).findById(401L);
    }

    // =====================================================================
    // Tests for getMatchCompetitors (protected)
    // =====================================================================

    @Test
    public void getMatchCompetitors_whenEmptyList_thenReturnsEmptyList() {
        DtoMapping dtoMapping = buildMappingWithMatch(buildMatchDto(1, null));
        dtoMapping.setMatchCompetitorMap(new HashMap<>());
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchCompetitors(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getMatchCompetitors_whenMatchCompetitorHasExistingId_thenSetsIdOnEntity() {
        MatchDto matchDto = buildMatchDto(1, null);
        CompetitorDto competitorDto = buildCompetitorDto(null, 1);
        MatchCompetitorDto matchCompetitorDto = buildMatchCompetitorDto(competitorDto, matchDto);
        matchCompetitorDto.setId(777L);

        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        dtoMapping.setMatchCompetitorMap(singleEntryMap(matchCompetitorDto.getUuid(), matchCompetitorDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchCompetitors(mapping);

        assertEquals(1, result.size());
        assertEquals(777L, result.getFirst().getId());
    }

    @Test
    public void getMatchCompetitors_whenMatchCompetitorHasNullId_thenEntityHasNullId() {
        MatchDto matchDto = buildMatchDto(1, null);
        CompetitorDto competitorDto = buildCompetitorDto(null, 1);
        MatchCompetitorDto matchCompetitorDto = buildMatchCompetitorDto(competitorDto, matchDto);
        // id remains null (default)

        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        dtoMapping.setMatchCompetitorMap(singleEntryMap(matchCompetitorDto.getUuid(), matchCompetitorDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchCompetitors(mapping);

        assertEquals(1, result.size());
        assertNull(result.getFirst().getId());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitors (protected)
    // =====================================================================

    @Test
    public void getMatchStageCompetitors_whenNullMatchStageOnDto_thenFiltersOut() {
        MatchDto matchDto = buildMatchDto(1, null);
        MatchStageDto targetStage = buildMatchStageDto(matchDto, 1);
        CompetitorDto competitorDto = buildCompetitorDto(null, 1);

        MatchStageCompetitorDto mscDto = buildMatchStageCompetitorDto(competitorDto, targetStage);
        mscDto.setMatchStage(null);   // null matchStage → must be filtered

        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(mscDto.getUuid(), mscDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getMatchStageCompetitors_whenDifferentStageUuid_thenFiltersOut() {
        MatchDto matchDto = buildMatchDto(1, null);
        MatchStageDto targetStage = buildMatchStageDto(matchDto, 1);
        MatchStageDto differentStage = buildMatchStageDto(matchDto, 2); // different UUID
        CompetitorDto competitorDto = buildCompetitorDto(null, 1);

        MatchStageCompetitorDto mscDto = buildMatchStageCompetitorDto(competitorDto, differentStage);

        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(mscDto.getUuid(), mscDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getMatchStageCompetitors_whenMatchingStageUuid_thenIncludesCompetitorInResult() {
        MatchDto matchDto = buildMatchDto(1, null);
        MatchStageDto targetStage = buildMatchStageDto(matchDto, 1);
        CompetitorDto competitorDto = buildCompetitorDto(null, 1);

        MatchStageCompetitorDto mscDto = buildMatchStageCompetitorDto(competitorDto, targetStage);

        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(mscDto.getUuid(), mscDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getMatchStageCompetitors_whenMatchStageCompetitorHasExistingId_thenSetsIdOnEntity() {
        MatchDto matchDto = buildMatchDto(1, null);
        MatchStageDto targetStage = buildMatchStageDto(matchDto, 1);
        CompetitorDto competitorDto = buildCompetitorDto(null, 1);

        MatchStageCompetitorDto mscDto = buildMatchStageCompetitorDto(competitorDto, targetStage);
        mscDto.setId(888L);

        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(mscDto.getUuid(), mscDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertEquals(1, result.size());
        assertEquals(888L, result.getFirst().getId());
    }

    @Test
    public void getMatchStageCompetitors_whenMatchStageCompetitorHasNullId_thenEntityHasNullId() {
        MatchDto matchDto = buildMatchDto(1, null);
        MatchStageDto targetStage = buildMatchStageDto(matchDto, 1);
        CompetitorDto competitorDto = buildCompetitorDto(null, 1);

        MatchStageCompetitorDto mscDto = buildMatchStageCompetitorDto(competitorDto, targetStage);
        // id is null by default

        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(mscDto.getUuid(), mscDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertEquals(1, result.size());
        assertNull(result.getFirst().getId());
    }

    // =====================================================================
    // Helper Methods
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
        DtoMapping dtoMapping = buildMappingWithMatch(matchDto);
        dtoMapping.setClub(clubDto);

        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        dtoMapping.setMatchStageMap(singleEntryMap(matchStageDto.getUuid(), matchStageDto));
        dtoMapping.setMatchCompetitorMap(singleEntryMap(matchCompetitorDto.getUuid(), matchCompetitorDto));
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(matchStageCompetitorDto.getUuid(), matchStageCompetitorDto));
        return dtoMapping;
    }

    private void stubTransactionStart() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
    }

    private static DtoMapping buildMappingWithMatch(MatchDto matchDto) {
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        return dtoMapping;
    }

    private static <K, V> HashMap<K, V> singleEntryMap(K key, V value) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}
