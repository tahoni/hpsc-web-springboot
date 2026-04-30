package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.common.data.DtoMapping;
import za.co.hpsc.web.models.ipsc.common.data.DtoToEntityMapping;
import za.co.hpsc.web.models.ipsc.common.dto.*;
import za.co.hpsc.web.models.ipsc.common.holders.data.MatchHolder;
import za.co.hpsc.web.repositories.*;

import java.time.LocalDateTime;
import java.util.List;
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

    private MatchDto buildMatchDto() {
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Test Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 3, 31, 10, 0));
        return matchDto;
    }

    private DtoMapping buildMinimalDtoMapping() {
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(buildMatchDto());
        return dtoMapping;
    }

    private CompetitorDto buildCompetitorDto() {
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");
        competitorDto.setCompetitorNumber("C001");
        return competitorDto;
    }

    private MatchStageDto buildMatchStageDto() {
        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(1);
        matchStageDto.setStageName("Stage 1");
        return matchStageDto;
    }

    /**
     * Builds a DtoToEntityMapping that already has a match entity set in its EntityMapping.
     */
    private DtoToEntityMapping buildMappingWithMatchEntity() {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        return mapping;
    }

    private void stubTransactionStart() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
    }

    @Test
    public void testSaveMatchResults_whenDtoMappingIsNull_thenReturnsEmptyOptional() {
        // Act
        Optional<MatchHolder> result = assertDoesNotThrow(
                () -> transactionService.saveMatchResults(null));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(transactionManager);
    }

    @Test
    public void testSaveMatchResults_whenMatchDtoIsNull_thenReturnsEmptyOptional() {
        // Arrange
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(null);

        // Act
        Optional<MatchHolder> result = assertDoesNotThrow(
                () -> transactionService.saveMatchResults(dtoMapping));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(transactionManager);
    }

    @Test
    public void testSaveMatchResults_whenValidDtoMappingWithNoClubOrCollections_thenSavesMatchAndCommits() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        stubTransactionStart();

        // Act
        Optional<MatchHolder> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping));

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().getMatch());
        assertEquals("Test Match", result.get().getMatch().getName());
        verify(ipscMatchRepository, times(2)).save(any(IpscMatch.class));
        verify(transactionManager).commit(transactionStatus);
        verify(transactionManager, never()).rollback(any());
        verifyNoInteractions(clubRepository);
        verifyNoInteractions(competitorRepository);
        verifyNoInteractions(ipscMatchStageRepository);
        verifyNoInteractions(matchCompetitorRepository);
        verifyNoInteractions(matchStageCompetitorRepository);
    }

    @Test
    public void testSaveMatchResults_whenClubDtoProvided_thenSavesClub() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        dtoMapping.setClub(clubDto);
        stubTransactionStart();

        // Act
        assertDoesNotThrow(() -> transactionService.saveMatchResults(dtoMapping));

        // Assert
        verify(clubRepository).save(any(Club.class));
    }

    @Test
    public void testSaveMatchResults_whenExistingMatchIdProvided_thenFetchesMatchFromRepository() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        dtoMapping.getMatch().setId(10L);
        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(10L);
        existingMatch.setName("Old Name");
        existingMatch.setScheduledDate(LocalDateTime.now());
        when(ipscMatchRepository.findById(10L)).thenReturn(Optional.of(existingMatch));
        stubTransactionStart();

        // Act
        Optional<MatchHolder> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping));

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().getMatch());
        assertEquals("Test Match", result.get().getMatch().getName());
        verify(ipscMatchRepository).findById(10L);
    }

    @Test
    public void testSaveMatchResults_whenCompetitorsProvided_thenSavesAllCompetitors() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);
        stubTransactionStart();

        // Act
        assertDoesNotThrow(() -> transactionService.saveMatchResults(dtoMapping));

        // Assert
        verify(competitorRepository).saveAll(anyList());
    }

    @Test
    public void testSaveMatchResults_whenMatchStagesProvided_thenSavesAllMatchStages() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        MatchStageDto matchStageDto = buildMatchStageDto();
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);
        stubTransactionStart();

        // Act
        assertDoesNotThrow(() -> transactionService.saveMatchResults(dtoMapping));

        // Assert
        verify(ipscMatchStageRepository).saveAll(anyList());
    }

    @Test
    public void testSaveMatchResults_whenMatchCompetitorsProvided_thenSavesAllMatchCompetitors() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setMatch(dtoMapping.getMatch());
        dtoMapping.getMatchCompetitorMap().put(matchCompetitorDto.getUuid(), matchCompetitorDto);
        stubTransactionStart();

        // Act
        assertDoesNotThrow(() -> transactionService.saveMatchResults(dtoMapping));

        // Assert
        verify(matchCompetitorRepository).saveAll(anyList());
    }

    @Test
    public void testSaveMatchResults_whenMatchStageCompetitorsProvided_thenSavesAllMatchStageCompetitors() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchStageDto matchStageDto = buildMatchStageDto();
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);

        MatchStageCompetitorDto msc = new MatchStageCompetitorDto();
        msc.setCompetitor(competitorDto);
        msc.setMatchStage(matchStageDto);
        dtoMapping.getMatchStageCompetitorMap().put(msc.getUuid(), msc);
        stubTransactionStart();

        // Act
        assertDoesNotThrow(() -> transactionService.saveMatchResults(dtoMapping));

        // Assert
        verify(matchStageCompetitorRepository).saveAll(anyList());
    }

    @Test
    public void testSaveMatchResults_whenSameCompetitorEnrollsInTwoDivisions_thenSavesTwoMatchCompetitorEntities() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitor = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitor.getUuid(), competitor);

        MatchCompetitorDto mc1 = new MatchCompetitorDto();
        mc1.setCompetitor(competitor);
        mc1.setMatch(dtoMapping.getMatch());
        mc1.setDivision(Division.PRODUCTION);
        mc1.setFirearmType(FirearmType.HANDGUN);

        MatchCompetitorDto mc2 = new MatchCompetitorDto();
        mc2.setCompetitor(competitor);
        mc2.setMatch(dtoMapping.getMatch());
        mc2.setDivision(Division.STANDARD);
        mc2.setFirearmType(FirearmType.HANDGUN);

        dtoMapping.getMatchCompetitorMap().put(mc1.getUuid(), mc1);
        dtoMapping.getMatchCompetitorMap().put(mc2.getUuid(), mc2);
        stubTransactionStart();

        // Act
        Optional<MatchHolder> result = assertDoesNotThrow(() ->
                transactionService.saveMatchResults(dtoMapping));

        // Assert
        assertTrue(result.isPresent());
        List<MatchCompetitor> saved = result.get().getMatchCompetitors();
        assertEquals(2, saved.size());
        List<Division> divisions = saved.stream().map(MatchCompetitor::getDivision).toList();
        assertTrue(divisions.contains(Division.PRODUCTION));
        assertTrue(divisions.contains(Division.STANDARD));
        verify(matchCompetitorRepository).saveAll(anyList());
    }

    @Test
    public void testSaveMatchResults_whenRepositoryThrowsException_thenRollsBackTransaction() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        stubTransactionStart();
        when(ipscMatchRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        // Act / Assert
        assertThrows(FatalException.class,
                () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void testSaveMatchResults_whenRepositoryThrowsException_thenThrowsFatalExceptionWithMessage() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        stubTransactionStart();
        when(ipscMatchRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        // Act
        FatalException ex = assertThrows(FatalException.class,
                () -> transactionService.saveMatchResults(dtoMapping));

        // Assert
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().startsWith("Unable to save the match:"));
    }

    @Test
    public void testGetClub_whenClubDtoIsNull_thenReturnsEmptyOptional() {
        // Act
        Optional<Club> result = transactionService.getClub(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(clubRepository);
    }

    @Test
    public void testGetClub_whenClubDtoHasNullId_thenCreatesNewClubWithoutRepositoryCall() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setId(null);
        clubDto.setName("New Club");
        clubDto.setAbbreviation("NC");

        // Act
        Optional<Club> result = transactionService.getClub(clubDto);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New Club", result.get().getName());
        assertEquals("NC", result.get().getAbbreviation());
        verifyNoInteractions(clubRepository);
    }

    @Test
    public void testGetClub_whenClubDtoHasIdAndClubExistsInRepository_thenReturnsFetchedAndUpdatedClub() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setId(1L);
        clubDto.setName("Updated Club");
        clubDto.setAbbreviation("UC");

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Old Club");
        when(clubRepository.findById(1L)).thenReturn(Optional.of(existingClub));

        // Act
        Optional<Club> result = transactionService.getClub(clubDto);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Updated Club", result.get().getName());
        assertEquals("UC", result.get().getAbbreviation());
        verify(clubRepository).findById(1L);
    }

    @Test
    public void testGetClub_whenClubDtoHasIdButClubNotInRepository_thenCreatesNewClub() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setId(99L);
        clubDto.setName("New Club");
        when(clubRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Club> result = transactionService.getClub(clubDto);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New Club", result.get().getName());
        verify(clubRepository).findById(99L);
    }

    @Test
    public void testGetIpscMatch_whenMatchDtoIsNull_thenReturnsEmptyOptional() {
        // Act
        Optional<IpscMatch> result = transactionService.getIpscMatch((MatchDto) null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(ipscMatchRepository);
    }

    @Test
    public void testGetIpscMatchStages_whenMatchEntityIsAbsent_thenReturnsEmptyList() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        // Act
        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(ipscMatchStageRepository);
    }

    @Test
    public void testGetIpscMatchStages_whenMatchStageDtoListIsEmpty_thenReturnsEmptyList() {
        // Arrange
        DtoToEntityMapping mapping = buildMappingWithMatchEntity();

        // Act
        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(ipscMatchStageRepository);
    }

    @Test
    public void testGetIpscMatchStages_whenMatchStageDtoHasNullId_thenCreatesNewStageWithoutRepositoryCall() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        MatchStageDto matchStageDto = buildMatchStageDto();
        matchStageDto.setId(null);
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);

        // Act
        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().getStageNumber());
        assertEquals("Stage 1", result.getFirst().getStageName());
        verifyNoInteractions(ipscMatchStageRepository);
    }

    @Test
    public void testGetIpscMatchStages_whenMatchStageDtoHasIdAndStageExistsInRepository_thenReturnsFetchedStage() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        MatchStageDto matchStageDto = buildMatchStageDto();
        matchStageDto.setId(7L);
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);

        IpscMatchStage existingStage = new IpscMatchStage();
        existingStage.setId(7L);
        existingStage.setStageNumber(99);
        when(ipscMatchStageRepository.findById(7L)).thenReturn(Optional.of(existingStage));

        // Act
        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.getFirst().getStageNumber());
        verify(ipscMatchStageRepository).findById(7L);
    }

    @Test
    public void testGetIpscMatchStages_whenMatchStageDtoHasIdButStageNotInRepository_thenCreatesNewStage() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        MatchStageDto matchStageDto = buildMatchStageDto();
        matchStageDto.setId(99L);
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        when(ipscMatchStageRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.getFirst().getStageNumber());
    }

    @Test
    public void testGetIpscMatchStages_whenMultipleStageDtos_thenReturnsAllStages() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        MatchStageDto stage1 = buildMatchStageDto();
        stage1.setStageNumber(1);
        MatchStageDto stage2 = buildMatchStageDto();
        stage2.setStageNumber(2);
        dtoMapping.getMatchStageMap().put(stage1.getUuid(), stage1);
        dtoMapping.getMatchStageMap().put(stage2.getUuid(), stage2);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);

        // Act
        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    public void testGetIpscMatchStages_whenStageDtoInMapIsNull_thenFiltersItOut() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        dtoMapping.getMatchStageMap().put(null, null);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);

        // Act
        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCompetitors_whenCompetitorDtoListIsEmpty_thenReturnsEmptyList() {
        // Arrange
        DtoMapping dtoMapping = new DtoMapping();
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        // Act
        List<Competitor> result = transactionService.getCompetitors(mapping);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(competitorRepository);
    }

    @Test
    public void testGetCompetitors_whenCompetitorDtoHasNullId_thenCreatesNewCompetitorWithoutRepositoryCall() {
        // Arrange
        DtoMapping dtoMapping = new DtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        competitorDto.setId(null);
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        // Act
        List<Competitor> result = transactionService.getCompetitors(mapping);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getFirstName());
        assertEquals("Doe", result.getFirst().getLastName());
        verifyNoInteractions(competitorRepository);
    }

    @Test
    public void testGetCompetitors_whenCompetitorDtoHasIdAndCompetitorExistsInRepository_thenReturnsFetchedAndUpdatedCompetitor() {
        // Arrange
        DtoMapping dtoMapping = new DtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        competitorDto.setId(3L);
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        Competitor existingCompetitor = new Competitor();
        existingCompetitor.setId(3L);
        existingCompetitor.setFirstName("Jane");
        existingCompetitor.setLastName("Smith");
        when(competitorRepository.findById(3L)).thenReturn(Optional.of(existingCompetitor));

        // Act
        List<Competitor> result = transactionService.getCompetitors(mapping);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("John", result.getFirst().getFirstName());
        verify(competitorRepository).findById(3L);
    }

    @Test
    public void testGetCompetitors_whenCompetitorDtoHasIdButCompetitorNotInRepository_thenCreatesNewCompetitor() {
        // Arrange
        DtoMapping dtoMapping = new DtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        competitorDto.setId(99L);
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        when(competitorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        List<Competitor> result = transactionService.getCompetitors(mapping);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("John", result.getFirst().getFirstName());
        verify(competitorRepository).findById(99L);
    }

    @Test
    public void testGetCompetitors_whenMultipleCompetitorDtos_thenReturnsAllCompetitors() {
        // Arrange
        DtoMapping dtoMapping = new DtoMapping();
        CompetitorDto c1 = buildCompetitorDto();
        c1.setFirstName("Alice");
        CompetitorDto c2 = buildCompetitorDto();
        c2.setFirstName("Bob");
        dtoMapping.getCompetitorMap().put(c1.getUuid(), c1);
        dtoMapping.getCompetitorMap().put(c2.getUuid(), c2);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        // Act
        List<Competitor> result = transactionService.getCompetitors(mapping);

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    public void testGetCompetitors_whenCalled_thenSetsCompetitorEntitiesInMapping() {
        // Arrange
        DtoMapping dtoMapping = new DtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        // Act
        List<Competitor> competitors = transactionService.getCompetitors(mapping);

        // Assert
        assertFalse(competitors.isEmpty());
        assertEquals("John", competitors.getFirst().getFirstName());
    }

    @Test
    public void testGetMatchCompetitors_whenMatchCompetitorListIsEmpty_thenReturnsEmptyList() {
        // Arrange
        DtoToEntityMapping mapping = buildMappingWithMatchEntity();

        // Act
        List<MatchCompetitor> result = transactionService.getMatchCompetitors(mapping);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchCompetitors_whenMatchCompetitorDtoHasNullId_thenCreatesEntityWithNullId() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(null);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setMatch(dtoMapping.getMatch());
        dtoMapping.getMatchCompetitorMap().put(matchCompetitorDto.getUuid(), matchCompetitorDto);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        mapping.setCompetitor(competitorDto, new Competitor());

        // Act
        List<MatchCompetitor> result = transactionService.getMatchCompetitors(mapping);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertNull(result.getFirst().getId());
    }

    @Test
    public void testGetMatchCompetitors_whenMatchCompetitorDtoHasId_thenSetsIdOnEntity() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(42L);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setMatch(dtoMapping.getMatch());
        dtoMapping.getMatchCompetitorMap().put(matchCompetitorDto.getUuid(), matchCompetitorDto);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        mapping.setCompetitor(competitorDto, new Competitor());

        // Act
        List<MatchCompetitor> result = transactionService.getMatchCompetitors(mapping);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(42L, result.getFirst().getId());
    }

    @Test
    public void testGetMatchCompetitors_whenMultipleMatchCompetitorDtos_thenReturnsAll() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto c1 = buildCompetitorDto();
        c1.setFirstName("Alice");
        CompetitorDto c2 = buildCompetitorDto();
        c2.setFirstName("Bob");
        dtoMapping.getCompetitorMap().put(c1.getUuid(), c1);
        dtoMapping.getCompetitorMap().put(c2.getUuid(), c2);

        MatchCompetitorDto mc1 = new MatchCompetitorDto();
        mc1.setCompetitor(c1);
        mc1.setMatch(dtoMapping.getMatch());
        MatchCompetitorDto mc2 = new MatchCompetitorDto();
        mc2.setCompetitor(c2);
        mc2.setMatch(dtoMapping.getMatch());
        dtoMapping.getMatchCompetitorMap().put(mc1.getUuid(), mc1);
        dtoMapping.getMatchCompetitorMap().put(mc2.getUuid(), mc2);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        mapping.setCompetitor(c1, new Competitor());
        mapping.setCompetitor(c2, new Competitor());

        // Act
        List<MatchCompetitor> result = transactionService.getMatchCompetitors(mapping);

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    public void testGetMatchCompetitors_whenSameCompetitorEntersInTwoDivisions_thenReturnsBothWithCorrectDivisions() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitor = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitor.getUuid(), competitor);

        MatchCompetitorDto mc1 = new MatchCompetitorDto();
        mc1.setCompetitor(competitor);
        mc1.setMatch(dtoMapping.getMatch());
        mc1.setDivision(Division.PRODUCTION);
        mc1.setFirearmType(FirearmType.HANDGUN);

        MatchCompetitorDto mc2 = new MatchCompetitorDto();
        mc2.setCompetitor(competitor);
        mc2.setMatch(dtoMapping.getMatch());
        mc2.setDivision(Division.STANDARD);
        mc2.setFirearmType(FirearmType.HANDGUN);

        dtoMapping.getMatchCompetitorMap().put(mc1.getUuid(), mc1);
        dtoMapping.getMatchCompetitorMap().put(mc2.getUuid(), mc2);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        mapping.setCompetitor(competitor, new Competitor());

        // Act
        List<MatchCompetitor> result = transactionService.getMatchCompetitors(mapping);

        // Assert
        assertEquals(2, result.size());
        List<Division> divisions = result.stream().map(MatchCompetitor::getDivision).toList();
        assertTrue(divisions.contains(Division.PRODUCTION));
        assertTrue(divisions.contains(Division.STANDARD));
    }

    @Test
    public void testGetMatchCompetitors_whenSameCompetitorEntersWithTwoFirearmTypes_thenReturnsBothWithCorrectFirearmTypes() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitor = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitor.getUuid(), competitor);

        MatchCompetitorDto mc1 = new MatchCompetitorDto();
        mc1.setCompetitor(competitor);
        mc1.setMatch(dtoMapping.getMatch());
        mc1.setFirearmType(FirearmType.HANDGUN);
        mc1.setDivision(Division.PRODUCTION);

        MatchCompetitorDto mc2 = new MatchCompetitorDto();
        mc2.setCompetitor(competitor);
        mc2.setMatch(dtoMapping.getMatch());
        mc2.setFirearmType(FirearmType.RIFLE);
        mc2.setDivision(Division.RIFLE_SEMI_AUTO_OPEN);

        dtoMapping.getMatchCompetitorMap().put(mc1.getUuid(), mc1);
        dtoMapping.getMatchCompetitorMap().put(mc2.getUuid(), mc2);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        mapping.setCompetitor(competitor, new Competitor());

        // Act
        List<MatchCompetitor> result = transactionService.getMatchCompetitors(mapping);

        // Assert
        assertEquals(2, result.size());
        List<FirearmType> firearmTypes = result.stream().map(MatchCompetitor::getFirearmType).toList();
        assertTrue(firearmTypes.contains(FirearmType.HANDGUN));
        assertTrue(firearmTypes.contains(FirearmType.RIFLE));
    }

    @Test
    public void testGetAllMatchStageCompetitors_whenMatchStageDtoListIsEmpty_thenReturnsEmptyList() {
        // Arrange
        DtoMapping dtoMapping = new DtoMapping();
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        // Act
        List<MatchStageCompetitor> result = transactionService.getAllMatchStageCompetitors(mapping);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllMatchStageCompetitors_whenStageHasNoMatchingCompetitors_thenReturnsEmptyList() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        MatchStageDto stage1 = buildMatchStageDto();
        dtoMapping.getMatchStageMap().put(stage1.getUuid(), stage1);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        IpscMatchStage stageEntity = new IpscMatchStage();
        stageEntity.setStageNumber(1);
        mapping.setMatchStage(stage1, stageEntity);

        // Act
        List<MatchStageCompetitor> result = transactionService.getAllMatchStageCompetitors(mapping);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllMatchStageCompetitors_whenMultipleStagesEachWithOneCompetitor_thenAccumulatesAll() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchStageDto stage1 = buildMatchStageDto();
        stage1.setStageNumber(1);
        MatchStageDto stage2 = buildMatchStageDto();
        stage2.setStageNumber(2);
        dtoMapping.getMatchStageMap().put(stage1.getUuid(), stage1);
        dtoMapping.getMatchStageMap().put(stage2.getUuid(), stage2);

        MatchStageCompetitorDto msc1 = new MatchStageCompetitorDto();
        msc1.setCompetitor(competitorDto);
        msc1.setMatchStage(stage1);
        MatchStageCompetitorDto msc2 = new MatchStageCompetitorDto();
        msc2.setCompetitor(competitorDto);
        msc2.setMatchStage(stage2);
        dtoMapping.getMatchStageCompetitorMap().put(msc1.getUuid(), msc1);
        dtoMapping.getMatchStageCompetitorMap().put(msc2.getUuid(), msc2);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        Competitor competitorEntity = new Competitor();
        mapping.setCompetitor(competitorDto, competitorEntity);
        IpscMatchStage stageEntity1 = new IpscMatchStage();
        stageEntity1.setStageNumber(1);
        IpscMatchStage stageEntity2 = new IpscMatchStage();
        stageEntity2.setStageNumber(2);
        mapping.setMatchStage(stage1, stageEntity1);
        mapping.setMatchStage(stage2, stageEntity2);

        // Act
        List<MatchStageCompetitor> result = transactionService.getAllMatchStageCompetitors(mapping);

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    public void testGetMatchStageCompetitors_whenMatchStageCompetitorListIsEmpty_thenReturnsEmptyList() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        MatchStageDto matchStageDto = buildMatchStageDto();

        // Act
        List<MatchStageCompetitor> result = transactionService.getMatchStageCompetitors(matchStageDto, mapping);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitors_whenCompetitorMatchesStageUuid_thenReturnsIt() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchStageDto matchStageDto = buildMatchStageDto();
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);

        MatchStageCompetitorDto msc = new MatchStageCompetitorDto();
        msc.setCompetitor(competitorDto);
        msc.setMatchStage(matchStageDto);
        dtoMapping.getMatchStageCompetitorMap().put(msc.getUuid(), msc);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        mapping.setCompetitor(competitorDto, new Competitor());
        IpscMatchStage stageEntity = new IpscMatchStage();
        stageEntity.setStageNumber(1);
        mapping.setMatchStage(matchStageDto, stageEntity);

        // Act
        List<MatchStageCompetitor> result =
                transactionService.getMatchStageCompetitors(matchStageDto, mapping);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    public void testGetMatchStageCompetitors_whenCompetitorDoesNotMatchStageUuid_thenExcludesIt() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchStageDto targetStage = buildMatchStageDto();
        MatchStageDto otherStage = buildMatchStageDto();
        dtoMapping.getMatchStageMap().put(targetStage.getUuid(), targetStage);

        MatchStageCompetitorDto msc = new MatchStageCompetitorDto();
        msc.setCompetitor(competitorDto);
        msc.setMatchStage(otherStage);
        dtoMapping.getMatchStageCompetitorMap().put(msc.getUuid(), msc);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        mapping.setCompetitor(competitorDto, new Competitor());
        IpscMatchStage stageEntity = new IpscMatchStage();
        stageEntity.setStageNumber(1);
        mapping.setMatchStage(targetStage, stageEntity);

        // Act
        List<MatchStageCompetitor> result =
                transactionService.getMatchStageCompetitors(targetStage, mapping);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitors_whenCompetitorHasNullMatchStage_thenExcludesIt() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchStageDto targetStage = buildMatchStageDto();

        MatchStageCompetitorDto mscWithNullStage = new MatchStageCompetitorDto();
        mscWithNullStage.setCompetitor(competitorDto);
        mscWithNullStage.setMatchStage(null);
        dtoMapping.getMatchStageCompetitorMap().put(mscWithNullStage.getUuid(), mscWithNullStage);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);

        // Act
        List<MatchStageCompetitor> result =
                transactionService.getMatchStageCompetitors(targetStage, mapping);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitors_whenCompetitorDtoHasId_thenSetsIdOnEntity() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchStageDto matchStageDto = buildMatchStageDto();
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);

        MatchStageCompetitorDto msc = new MatchStageCompetitorDto();
        msc.setId(77L);
        msc.setCompetitor(competitorDto);
        msc.setMatchStage(matchStageDto);
        dtoMapping.getMatchStageCompetitorMap().put(msc.getUuid(), msc);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        mapping.setCompetitor(competitorDto, new Competitor());
        IpscMatchStage stageEntity = new IpscMatchStage();
        stageEntity.setStageNumber(1);
        mapping.setMatchStage(matchStageDto, stageEntity);

        // Act
        List<MatchStageCompetitor> result =
                transactionService.getMatchStageCompetitors(matchStageDto, mapping);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(77L, result.getFirst().getId());
    }

    @Test
    public void testGetMatchStageCompetitors_whenSameCompetitorCompetesInSameStageWithTwoDivisions_thenReturnsBothWithCorrectDivisions() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitor = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitor.getUuid(), competitor);

        MatchStageDto matchStageDto = buildMatchStageDto();
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);

        MatchStageCompetitorDto msc1 = new MatchStageCompetitorDto();
        msc1.setCompetitor(competitor);
        msc1.setMatchStage(matchStageDto);
        msc1.setDivision(Division.PRODUCTION);
        msc1.setFirearmType(FirearmType.HANDGUN);

        MatchStageCompetitorDto msc2 = new MatchStageCompetitorDto();
        msc2.setCompetitor(competitor);
        msc2.setMatchStage(matchStageDto);
        msc2.setDivision(Division.STANDARD);
        msc2.setFirearmType(FirearmType.HANDGUN);

        dtoMapping.getMatchStageCompetitorMap().put(msc1.getUuid(), msc1);
        dtoMapping.getMatchStageCompetitorMap().put(msc2.getUuid(), msc2);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        mapping.setCompetitor(competitor, new Competitor());
        IpscMatchStage stageEntity = new IpscMatchStage();
        stageEntity.setStageNumber(1);
        mapping.setMatchStage(matchStageDto, stageEntity);

        // Act
        List<MatchStageCompetitor> result =
                transactionService.getMatchStageCompetitors(matchStageDto, mapping);

        // Assert
        assertEquals(2, result.size());
        List<Division> divisions = result.stream().map(MatchStageCompetitor::getDivision).toList();
        assertTrue(divisions.contains(Division.PRODUCTION));
        assertTrue(divisions.contains(Division.STANDARD));
    }

    @Test
    public void testGetMatchStageCompetitors_whenCompetitorDtoHasNullId_thenCreatesEntityWithNullId() {
        // Arrange
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchStageDto matchStageDto = buildMatchStageDto();
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);

        MatchStageCompetitorDto msc = new MatchStageCompetitorDto();
        msc.setId(null);
        msc.setCompetitor(competitorDto);
        msc.setMatchStage(matchStageDto);
        dtoMapping.getMatchStageCompetitorMap().put(msc.getUuid(), msc);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);
        mapping.setCompetitor(competitorDto, new Competitor());
        IpscMatchStage stageEntity = new IpscMatchStage();
        stageEntity.setStageNumber(1);
        mapping.setMatchStage(matchStageDto, stageEntity);

        // Act
        List<MatchStageCompetitor> result =
                transactionService.getMatchStageCompetitors(matchStageDto, mapping);

        // Assert
        assertFalse(result.isEmpty());
        assertNull(result.getFirst().getId());
    }
}

