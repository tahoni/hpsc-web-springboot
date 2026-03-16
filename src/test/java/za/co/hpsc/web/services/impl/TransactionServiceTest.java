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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(null);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(null);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        ClubDto clubDto = new ClubDto();
        clubDto.setId(100L);
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("TC");
        Club club = new Club();
        club.setId(clubDto.getId());
        club.setName(clubDto.getName());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(101);
        matchDto.setId(1L);
        matchDto.setName("Match 101");
        matchDto.setScheduledDate(LocalDateTime.now());

        ClubDto clubDto = new ClubDto();
        clubDto.setId(200L);
        clubDto.setName("Full Club");
        clubDto.setAbbreviation("TC");

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(300L);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setMatch(matchDto);
        matchStageDto.setStageNumber(1);
        matchStageDto.setStageName("Stage 1");

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setMatch(matchDto);
        matchCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
        matchCompetitorDto.setMatchIndex(matchDto.getIndex());

        MatchStageCompetitorDto matchStageCompetitorDto = new MatchStageCompetitorDto();
        matchStageCompetitorDto.setCompetitor(competitorDto);
        matchStageCompetitorDto.setMatchStage(matchStageDto);
        matchStageCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
        matchStageCompetitorDto.setMatchStageIndex(matchStageDto.getIndex());

        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setClub(clubDto);
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        dtoMapping.setMatchStageMap(singleEntryMap(matchStageDto.getUuid(), matchStageDto));
        dtoMapping.setMatchCompetitorMap(singleEntryMap(matchCompetitorDto.getUuid(), matchCompetitorDto));
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(matchStageCompetitorDto.getUuid(), matchStageCompetitorDto));

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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(null);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        matchDto.setName("   ");
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(null);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(100);
        matchDto.setId(null);
        matchDto.setName("Match 100");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        stubTransactionStart();
        doThrow(new RuntimeException("DB error")).when(ipscMatchRepository).save(any());

        assertThrows(FatalException.class, () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void saveMatchResults_whenCompetitorSaveAllThrows_thenRollsBackAndThrowsFatalException() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        stubTransactionStart();
        doThrow(new RuntimeException("DB error")).when(competitorRepository).saveAll(anyList());

        assertThrows(FatalException.class, () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void saveMatchResults_whenMatchStageSaveAllThrows_thenRollsBackAndThrowsFatalException() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchDto);
        stageDto.setStageNumber(1);
        stageDto.setStageName("Stage 1");
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setMatchStageMap(singleEntryMap(stageDto.getUuid(), stageDto));
        stubTransactionStart();
        doThrow(new RuntimeException("DB error")).when(ipscMatchStageRepository).saveAll(anyList());

        assertThrows(FatalException.class, () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void saveMatchResults_whenMatchCompetitorSaveAllThrows_thenRollsBackAndThrowsFatalException() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");
        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setMatch(matchDto);
        matchCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
        matchCompetitorDto.setMatchIndex(matchDto.getIndex());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchDto);
        stageDto.setStageNumber(1);
        stageDto.setStageName("Stage 1");
        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setCompetitor(competitorDto);
        mscDto.setMatchStage(stageDto);
        mscDto.setCompetitorIndex(competitorDto.getIndex());
        mscDto.setMatchStageIndex(stageDto.getIndex());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        ClubDto clubDto = new ClubDto();
        clubDto.setId(999L);
        clubDto.setName("Missing Club");
        clubDto.setAbbreviation("TC");
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(1L);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        stubTransactionStart();
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existingMatch));

        Optional<IpscMatch> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping));

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).findById(1L);
    }

    @Test
    public void saveMatchResults_whenMatchIdNotFoundInRepository_thenCreatesNewMatch() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(1L);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(42);
        matchDto.setId(null);
        matchDto.setName("Match 42");
        matchDto.setScheduledDate(LocalDateTime.now());
        matchDto.setName("Championship Match");
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        Optional<Club> result = transactionService.getClub(null, mapping);

        assertTrue(result.isEmpty());
        verify(clubRepository, never()).findById(any());
    }

    @Test
    public void getClub_whenClubDtoHasNullId_thenCreatesNewClubWithoutRepositoryLookup() {
        ClubDto clubDto = new ClubDto();
        clubDto.setId(null);
        clubDto.setName("Fresh Club");
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

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
        ClubDto clubDto = new ClubDto();
        clubDto.setId(5L);
        clubDto.setName("New Name");
        clubDto.setAbbreviation("TC");
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        when(clubRepository.findById(5L)).thenReturn(Optional.of(repositoryClub));

        Optional<Club> result = transactionService.getClub(clubDto, mapping);

        assertTrue(result.isPresent());
        verify(clubRepository).findById(5L);
        assertEquals(5L, result.get().getId());
        assertEquals("New Name", result.get().getName());   // init() overwrites name
    }

    @Test
    public void getClub_whenClubDtoIdNotFound_thenCreatesNewClub() {
        ClubDto clubDto = new ClubDto();
        clubDto.setId(77L);
        clubDto.setName("Missing Club");
        clubDto.setAbbreviation("TC");
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        Optional<IpscMatch> result = transactionService.getIpscMatch(mapping);

        assertTrue(result.isPresent());
        assertEquals("Match 1", result.get().getName());
        verify(ipscMatchRepository, never()).findById(any());
    }

    @Test
    public void getIpscMatch_whenMatchDtoIdFound_thenLoadsFromRepository() {
        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(10L);
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(10L);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        when(ipscMatchRepository.findById(10L)).thenReturn(Optional.of(existingMatch));

        Optional<IpscMatch> result = transactionService.getIpscMatch(mapping);

        assertTrue(result.isPresent());
        verify(ipscMatchRepository).findById(10L);
        assertEquals(10L, result.get().getId());
    }

    @Test
    public void getIpscMatch_whenMatchDtoIdNotFound_thenCreatesNewMatchEntity() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(10L);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchDto);
        stageDto.setStageNumber(1);
        stageDto.setStageName("Stage 1");
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setMatchStageMap(singleEntryMap(stageDto.getUuid(), stageDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping); // match entity is NOT set

        var result = transactionService.getIpscMatchStages(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ipscMatchStageRepository, never()).findById(any());
    }

    @Test
    public void getIpscMatchStages_whenStageHasNullId_thenCreatesNewStageEntity() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchDto);
        stageDto.setStageNumber(2);
        stageDto.setStageName("Stage 2");
        stageDto.setId(null);
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchDto);
        stageDto.setStageNumber(1);
        stageDto.setStageName("Stage 1");
        stageDto.setId(200L);
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchDto);
        stageDto.setStageNumber(3);
        stageDto.setStageName("Stage 3");
        stageDto.setId(300L);
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setCompetitorMap(new HashMap<>());
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getCompetitors(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(competitorRepository, never()).findById(any());
    }

    @Test
    public void getCompetitors_whenCompetitorHasNullId_thenCreatesNewCompetitorEntity() {
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getCompetitors(mapping);

        assertEquals(1, result.size());
        verify(competitorRepository, never()).findById(any());
    }

    @Test
    public void getCompetitors_whenCompetitorIdFound_thenLoadsFromRepository() {
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(400L);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(401L);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setMatchCompetitorMap(new HashMap<>());
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchCompetitors(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getMatchCompetitors_whenMatchCompetitorHasExistingId_thenSetsIdOnEntity() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");
        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setMatch(matchDto);
        matchCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
        matchCompetitorDto.setMatchIndex(matchDto.getIndex());
        matchCompetitorDto.setId(777L);

        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setCompetitorMap(singleEntryMap(competitorDto.getUuid(), competitorDto));
        dtoMapping.setMatchCompetitorMap(singleEntryMap(matchCompetitorDto.getUuid(), matchCompetitorDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchCompetitors(mapping);

        assertEquals(1, result.size());
        assertEquals(777L, result.getFirst().getId());
    }

    @Test
    public void getMatchCompetitors_whenMatchCompetitorHasNullId_thenEntityHasNullId() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");
        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setMatch(matchDto);
        matchCompetitorDto.setCompetitorIndex(competitorDto.getIndex());
        matchCompetitorDto.setMatchIndex(matchDto.getIndex());
        // id remains null (default)

        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
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
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchStageDto targetStage = new MatchStageDto();
        targetStage.setMatch(matchDto);
        targetStage.setStageNumber(1);
        targetStage.setStageName("Stage 1");
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");

        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setCompetitor(competitorDto);
        mscDto.setMatchStage(targetStage);
        mscDto.setCompetitorIndex(competitorDto.getIndex());
        mscDto.setMatchStageIndex(targetStage.getIndex());
        mscDto.setMatchStage(null);   // null matchStage → must be filtered

        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(mscDto.getUuid(), mscDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getMatchStageCompetitors_whenDifferentStageUuid_thenFiltersOut() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchStageDto targetStage = new MatchStageDto();
        targetStage.setMatch(matchDto);
        targetStage.setStageNumber(1);
        targetStage.setStageName("Stage 1");
        MatchStageDto differentStage = new MatchStageDto();
        differentStage.setMatch(matchDto);
        differentStage.setStageNumber(2);
        differentStage.setStageName("Stage 2"); // different UUID
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");

        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setCompetitor(competitorDto);
        mscDto.setMatchStage(differentStage);
        mscDto.setCompetitorIndex(competitorDto.getIndex());
        mscDto.setMatchStageIndex(differentStage.getIndex());

        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(mscDto.getUuid(), mscDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getMatchStageCompetitors_whenMatchingStageUuid_thenIncludesCompetitorInResult() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchStageDto targetStage = new MatchStageDto();
        targetStage.setMatch(matchDto);
        targetStage.setStageNumber(1);
        targetStage.setStageName("Stage 1");
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");

        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setCompetitor(competitorDto);
        mscDto.setMatchStage(targetStage);
        mscDto.setCompetitorIndex(competitorDto.getIndex());
        mscDto.setMatchStageIndex(targetStage.getIndex());

        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(mscDto.getUuid(), mscDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getMatchStageCompetitors_whenMatchStageCompetitorHasExistingId_thenSetsIdOnEntity() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchStageDto targetStage = new MatchStageDto();
        targetStage.setMatch(matchDto);
        targetStage.setStageNumber(1);
        targetStage.setStageName("Stage 1");
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");

        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setCompetitor(competitorDto);
        mscDto.setMatchStage(targetStage);
        mscDto.setCompetitorIndex(competitorDto.getIndex());
        mscDto.setMatchStageIndex(targetStage.getIndex());
        mscDto.setId(888L);

        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(mscDto.getUuid(), mscDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertEquals(1, result.size());
        assertEquals(888L, result.getFirst().getId());
    }

    @Test
    public void getMatchStageCompetitors_whenMatchStageCompetitorHasNullId_thenEntityHasNullId() {
        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(1);
        matchDto.setId(null);
        matchDto.setName("Match 1");
        matchDto.setScheduledDate(LocalDateTime.now());
        MatchStageDto targetStage = new MatchStageDto();
        targetStage.setMatch(matchDto);
        targetStage.setStageNumber(1);
        targetStage.setStageName("Stage 1");
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setIndex(1);
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("Competitor");
        competitorDto.setCompetitorNumber("C1");

        MatchStageCompetitorDto mscDto = new MatchStageCompetitorDto();
        mscDto.setCompetitor(competitorDto);
        mscDto.setMatchStage(targetStage);
        mscDto.setCompetitorIndex(competitorDto.getIndex());
        mscDto.setMatchStageIndex(targetStage.getIndex());
        // id is null by default

        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(matchDto);
        dtoMapping.setMatchStageCompetitorMap(singleEntryMap(mscDto.getUuid(), mscDto));
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        var result = transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertEquals(1, result.size());
        assertNull(result.getFirst().getId());
    }

    // =====================================================================
    // Helper Methods
    // =====================================================================

    private void stubTransactionStart() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
    }

    private static <K, V> HashMap<K, V> singleEntryMap(K key, V value) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
