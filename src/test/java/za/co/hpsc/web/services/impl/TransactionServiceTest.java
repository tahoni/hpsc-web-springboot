package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.domain.DtoMapping;
import za.co.hpsc.web.models.ipsc.domain.DtoToEntityMapping;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    // Helper methods

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

    // Test Group: saveMatchResults – Input Validation

    @Test
    public void testSaveMatchResults_whenDtoMappingIsNull_thenReturnsEmptyOptional() {
        Optional<IpscMatch> result = assertDoesNotThrow(
                () -> transactionService.saveMatchResults(null));

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(transactionManager);
    }

    @Test
    public void testSaveMatchResults_whenMatchDtoIsNull_thenReturnsEmptyOptional() {
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(null);

        Optional<IpscMatch> result = assertDoesNotThrow(
                () -> transactionService.saveMatchResults(dtoMapping));

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(transactionManager);
    }

    // Test Group: saveMatchResults – Happy Path

    @Test
    public void testSaveMatchResults_whenValidDtoMappingWithNoClubOrCollections_thenSavesMatchAndCommits()
            throws FatalException {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        stubTransactionStart();

        Optional<IpscMatch> result = transactionService.saveMatchResults(dtoMapping);

        assertTrue(result.isPresent());
        assertEquals("Test Match", result.get().getName());
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
    public void testSaveMatchResults_whenClubDtoProvided_thenSavesClub() throws FatalException {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        dtoMapping.setClub(clubDto);
        stubTransactionStart();

        transactionService.saveMatchResults(dtoMapping);

        verify(clubRepository).save(any(Club.class));
    }

    @Test
    public void testSaveMatchResults_whenExistingMatchIdProvided_thenFetchesMatchFromRepository()
            throws FatalException {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        dtoMapping.getMatch().setId(10L);
        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(10L);
        existingMatch.setName("Old Name");
        existingMatch.setScheduledDate(LocalDateTime.now());
        when(ipscMatchRepository.findById(10L)).thenReturn(Optional.of(existingMatch));
        stubTransactionStart();

        Optional<IpscMatch> result = transactionService.saveMatchResults(dtoMapping);

        assertTrue(result.isPresent());
        assertEquals("Test Match", result.get().getName());
        verify(ipscMatchRepository).findById(10L);
    }

    @Test
    public void testSaveMatchResults_whenCompetitorsProvided_thenSavesAllCompetitors()
            throws FatalException {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);
        stubTransactionStart();

        transactionService.saveMatchResults(dtoMapping);

        verify(competitorRepository).saveAll(anyList());
    }

    @Test
    public void testSaveMatchResults_whenMatchStagesProvided_thenSavesAllMatchStages()
            throws FatalException {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        MatchStageDto matchStageDto = buildMatchStageDto();
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);
        stubTransactionStart();

        transactionService.saveMatchResults(dtoMapping);

        verify(ipscMatchStageRepository).saveAll(anyList());
    }

    @Test
    public void testSaveMatchResults_whenMatchCompetitorsProvided_thenSavesAllMatchCompetitors()
            throws FatalException {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setMatch(dtoMapping.getMatch());
        dtoMapping.getMatchCompetitorMap().put(matchCompetitorDto.getUuid(), matchCompetitorDto);
        stubTransactionStart();

        transactionService.saveMatchResults(dtoMapping);

        verify(matchCompetitorRepository).saveAll(anyList());
    }

    @Test
    public void testSaveMatchResults_whenMatchStageCompetitorsProvided_thenSavesAllMatchStageCompetitors()
            throws FatalException {
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

        transactionService.saveMatchResults(dtoMapping);

        verify(matchStageCompetitorRepository).saveAll(anyList());
    }

    // Test Group: saveMatchResults – Error / Rollback Handling

    @Test
    public void testSaveMatchResults_whenRepositoryThrowsException_thenRollsBackTransaction() {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        stubTransactionStart();
        when(ipscMatchRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        assertThrows(FatalException.class,
                () -> transactionService.saveMatchResults(dtoMapping));
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    @Test
    public void testSaveMatchResults_whenRepositoryThrowsException_thenThrowsFatalExceptionWithMessage() {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        stubTransactionStart();
        when(ipscMatchRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        FatalException ex = assertThrows(FatalException.class,
                () -> transactionService.saveMatchResults(dtoMapping));

        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().startsWith("Unable to save the match:"));
    }

    // Test Group: getClub

    @Test
    public void testGetClub_whenClubDtoIsNull_thenReturnsEmptyOptional() {
        DtoToEntityMapping mapping = new DtoToEntityMapping(new DtoMapping());

        Optional<Club> result = transactionService.getClub(null, mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(clubRepository);
    }

    @Test
    public void testGetClub_whenClubDtoHasNullId_thenCreatesNewClubWithoutRepositoryCall() {
        DtoToEntityMapping mapping = new DtoToEntityMapping(new DtoMapping());
        ClubDto clubDto = new ClubDto();
        clubDto.setId(null);
        clubDto.setName("New Club");
        clubDto.setAbbreviation("NC");

        Optional<Club> result = transactionService.getClub(clubDto, mapping);

        assertTrue(result.isPresent());
        assertEquals("New Club", result.get().getName());
        assertEquals("NC", result.get().getAbbreviation());
        verifyNoInteractions(clubRepository);
    }

    @Test
    public void testGetClub_whenClubDtoHasIdAndClubExistsInRepository_thenReturnsFetchedAndUpdatedClub() {
        DtoToEntityMapping mapping = new DtoToEntityMapping(new DtoMapping());
        ClubDto clubDto = new ClubDto();
        clubDto.setId(1L);
        clubDto.setName("Updated Club");
        clubDto.setAbbreviation("UC");

        Club existingClub = new Club();
        existingClub.setId(1L);
        existingClub.setName("Old Club");
        when(clubRepository.findById(1L)).thenReturn(Optional.of(existingClub));

        Optional<Club> result = transactionService.getClub(clubDto, mapping);

        assertTrue(result.isPresent());
        assertEquals("Updated Club", result.get().getName());
        assertEquals("UC", result.get().getAbbreviation());
        verify(clubRepository).findById(1L);
    }

    @Test
    public void testGetClub_whenClubDtoHasIdButClubNotInRepository_thenCreatesNewClub() {
        DtoToEntityMapping mapping = new DtoToEntityMapping(new DtoMapping());
        ClubDto clubDto = new ClubDto();
        clubDto.setId(99L);
        clubDto.setName("New Club");
        when(clubRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Club> result = transactionService.getClub(clubDto, mapping);

        assertTrue(result.isPresent());
        assertEquals("New Club", result.get().getName());
        verify(clubRepository).findById(99L);
    }

    @Test
    public void testGetClub_whenClubDtoProvided_thenSetsClubInEntityMapping() {
        // Arrange – set up the club via getClub(), then verify it is associated with the match
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        ClubDto clubDto = new ClubDto();
        clubDto.setName("HPSC");
        clubDto.setAbbreviation("HPSC");

        transactionService.getClub(clubDto, mapping);

        // Verify by calling getIpscMatch which propagates the club onto the match entity
        Optional<IpscMatch> matchResult = transactionService.getIpscMatch(mapping);

        assertTrue(matchResult.isPresent());
        assertNotNull(matchResult.get().getClub());
        assertEquals("HPSC", matchResult.get().getClub().getName());
    }

    // Test Group: getIpscMatch

    @Test
    public void testGetIpscMatch_whenMatchDtoIsNull_thenReturnsEmptyOptional() {
        // Arrange – DtoMapping with no match set
        DtoToEntityMapping mapping = new DtoToEntityMapping(new DtoMapping());

        Optional<IpscMatch> result = transactionService.getIpscMatch(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(ipscMatchRepository);
    }

    @Test
    public void testGetIpscMatch_whenMatchDtoHasNullId_thenCreatesNewMatchWithoutRepositoryCall() {
        DtoMapping dtoMapping = new DtoMapping();
        MatchDto matchDto = buildMatchDto();
        matchDto.setId(null);
        dtoMapping.setMatch(matchDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        Optional<IpscMatch> result = transactionService.getIpscMatch(mapping);

        assertTrue(result.isPresent());
        assertEquals("Test Match", result.get().getName());
        verifyNoInteractions(ipscMatchRepository);
    }

    @Test
    public void testGetIpscMatch_whenMatchDtoHasIdAndMatchExistsInRepository_thenReturnsFetchedAndUpdatedMatch() {
        DtoMapping dtoMapping = new DtoMapping();
        MatchDto matchDto = buildMatchDto();
        matchDto.setId(5L);
        dtoMapping.setMatch(matchDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(5L);
        existingMatch.setName("Old Match");
        existingMatch.setScheduledDate(LocalDateTime.now());
        when(ipscMatchRepository.findById(5L)).thenReturn(Optional.of(existingMatch));

        Optional<IpscMatch> result = transactionService.getIpscMatch(mapping);

        assertTrue(result.isPresent());
        assertEquals("Test Match", result.get().getName());
        verify(ipscMatchRepository).findById(5L);
    }

    @Test
    public void testGetIpscMatch_whenMatchDtoHasIdButMatchNotInRepository_thenCreatesNewMatch() {
        DtoMapping dtoMapping = new DtoMapping();
        MatchDto matchDto = buildMatchDto();
        matchDto.setId(99L);
        dtoMapping.setMatch(matchDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        when(ipscMatchRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<IpscMatch> result = transactionService.getIpscMatch(mapping);

        assertTrue(result.isPresent());
        assertEquals("Test Match", result.get().getName());
        verify(ipscMatchRepository).findById(99L);
    }

    @Test
    public void testGetIpscMatch_whenCalled_thenSetsMatchEntityInMapping() {
        DtoMapping dtoMapping = new DtoMapping();
        dtoMapping.setMatch(buildMatchDto());
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        transactionService.getIpscMatch(mapping);

        assertTrue(mapping.getMatchEntity().isPresent());
    }

    // Test Group: getIpscMatchStages

    @Test
    public void testGetIpscMatchStages_whenMatchEntityIsAbsent_thenReturnsEmptyList() {
        // Arrange – no match entity set
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(ipscMatchStageRepository);
    }

    @Test
    public void testGetIpscMatchStages_whenMatchStageDtoListIsEmpty_thenReturnsEmptyList() {
        // Arrange – match entity present but no stages in the map
        DtoToEntityMapping mapping = buildMappingWithMatchEntity();

        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(ipscMatchStageRepository);
    }

    @Test
    public void testGetIpscMatchStages_whenMatchStageDtoHasNullId_thenCreatesNewStageWithoutRepositoryCall() {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        MatchStageDto matchStageDto = buildMatchStageDto();
        matchStageDto.setId(null);
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);

        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getStageNumber());
        assertEquals("Stage 1", result.get(0).getStageName());
        verifyNoInteractions(ipscMatchStageRepository);
    }

    @Test
    public void testGetIpscMatchStages_whenMatchStageDtoHasIdAndStageExistsInRepository_thenReturnsFetchedStage() {
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
        existingStage.setStageNumber(99); // will be overwritten by init()
        when(ipscMatchStageRepository.findById(7L)).thenReturn(Optional.of(existingStage));

        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).getStageNumber()); // updated from DTO
        verify(ipscMatchStageRepository).findById(7L);
    }

    @Test
    public void testGetIpscMatchStages_whenMatchStageDtoHasIdButStageNotInRepository_thenCreatesNewStage() {
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

        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).getStageNumber());
    }

    @Test
    public void testGetIpscMatchStages_whenMultipleStageDtos_thenReturnsAllStages() {
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

        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        assertEquals(2, result.size());
    }

    @Test
    public void testGetIpscMatchStages_whenStageDtoInMapIsNull_thenFiltersItOut() {
        // Arrange – put a null value into the matchStageMap
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        dtoMapping.getMatchStageMap().put(null, null);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);

        List<IpscMatchStage> result = transactionService.getIpscMatchStages(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // Test Group: getCompetitors

    @Test
    public void testGetCompetitors_whenCompetitorDtoListIsEmpty_thenReturnsEmptyList() {
        // Arrange – competitorMap is empty by default
        DtoMapping dtoMapping = new DtoMapping();
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        List<Competitor> result = transactionService.getCompetitors(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(competitorRepository);
    }

    @Test
    public void testGetCompetitors_whenCompetitorDtoHasNullId_thenCreatesNewCompetitorWithoutRepositoryCall() {
        DtoMapping dtoMapping = new DtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        competitorDto.setId(null);
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        List<Competitor> result = transactionService.getCompetitors(mapping);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
        verifyNoInteractions(competitorRepository);
    }

    @Test
    public void testGetCompetitors_whenCompetitorDtoHasIdAndCompetitorExistsInRepository_thenReturnsFetchedAndUpdatedCompetitor() {
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

        List<Competitor> result = transactionService.getCompetitors(mapping);

        assertFalse(result.isEmpty());
        assertEquals("John", result.get(0).getFirstName()); // updated from DTO
        verify(competitorRepository).findById(3L);
    }

    @Test
    public void testGetCompetitors_whenCompetitorDtoHasIdButCompetitorNotInRepository_thenCreatesNewCompetitor() {
        DtoMapping dtoMapping = new DtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        competitorDto.setId(99L);
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        when(competitorRepository.findById(99L)).thenReturn(Optional.empty());

        List<Competitor> result = transactionService.getCompetitors(mapping);

        assertFalse(result.isEmpty());
        assertEquals("John", result.get(0).getFirstName());
        verify(competitorRepository).findById(99L);
    }

    @Test
    public void testGetCompetitors_whenMultipleCompetitorDtos_thenReturnsAllCompetitors() {
        DtoMapping dtoMapping = new DtoMapping();
        CompetitorDto c1 = buildCompetitorDto();
        c1.setFirstName("Alice");
        CompetitorDto c2 = buildCompetitorDto();
        c2.setFirstName("Bob");
        dtoMapping.getCompetitorMap().put(c1.getUuid(), c1);
        dtoMapping.getCompetitorMap().put(c2.getUuid(), c2);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        List<Competitor> result = transactionService.getCompetitors(mapping);

        assertEquals(2, result.size());
    }

    @Test
    public void testGetCompetitors_whenCalled_thenSetsCompetitorEntitiesInMapping() {
        DtoMapping dtoMapping = new DtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        List<Competitor> competitors = transactionService.getCompetitors(mapping);

        // Assert – verify the competitor was registered in the entity mapping by checking
        // that getCompetitorDtoList still returns the dto (mapping is updated in setCompetitor)
        assertFalse(competitors.isEmpty());
        assertEquals("John", competitors.get(0).getFirstName());
    }

    // Test Group: getMatchCompetitors

    @Test
    public void testGetMatchCompetitors_whenMatchCompetitorListIsEmpty_thenReturnsEmptyList() {
        // Arrange – matchCompetitorMap is empty by default
        DtoToEntityMapping mapping = buildMappingWithMatchEntity();

        List<MatchCompetitor> result = transactionService.getMatchCompetitors(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchCompetitors_whenMatchCompetitorDtoHasNullId_thenCreatesEntityWithNullId() {
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

        List<MatchCompetitor> result = transactionService.getMatchCompetitors(mapping);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertNull(result.get(0).getId());
    }

    @Test
    public void testGetMatchCompetitors_whenMatchCompetitorDtoHasId_thenSetsIdOnEntity() {
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

        List<MatchCompetitor> result = transactionService.getMatchCompetitors(mapping);

        assertFalse(result.isEmpty());
        assertEquals(42L, result.get(0).getId());
    }

    @Test
    public void testGetMatchCompetitors_whenMultipleMatchCompetitorDtos_thenReturnsAll() {
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

        List<MatchCompetitor> result = transactionService.getMatchCompetitors(mapping);

        assertEquals(2, result.size());
    }

    // Test Group: getAllMatchStageCompetitors

    @Test
    public void testGetAllMatchStageCompetitors_whenMatchStageDtoListIsEmpty_thenReturnsEmptyList() {
        // Arrange – no stages in the map
        DtoMapping dtoMapping = new DtoMapping();
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);

        List<MatchStageCompetitor> result = transactionService.getAllMatchStageCompetitors(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllMatchStageCompetitors_whenStageHasNoMatchingCompetitors_thenReturnsEmptyList() {
        // Arrange – stage is present but no stage competitors reference it
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

        List<MatchStageCompetitor> result = transactionService.getAllMatchStageCompetitors(mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllMatchStageCompetitors_whenMultipleStagesEachWithOneCompetitor_thenAccumulatesAll() {
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

        List<MatchStageCompetitor> result = transactionService.getAllMatchStageCompetitors(mapping);

        assertEquals(2, result.size());
    }

    // Test Group: getMatchStageCompetitors

    @Test
    public void testGetMatchStageCompetitors_whenMatchStageCompetitorListIsEmpty_thenReturnsEmptyList() {
        // Arrange – no stage competitors in the map
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        MatchStageDto matchStageDto = buildMatchStageDto();

        List<MatchStageCompetitor> result = transactionService.getMatchStageCompetitors(matchStageDto, mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitors_whenCompetitorMatchesStageUuid_thenReturnsIt() {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchStageDto matchStageDto = buildMatchStageDto();
        dtoMapping.getMatchStageMap().put(matchStageDto.getUuid(), matchStageDto);

        MatchStageCompetitorDto msc = new MatchStageCompetitorDto();
        msc.setCompetitor(competitorDto);
        msc.setMatchStage(matchStageDto); // same UUID – should match
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

        List<MatchStageCompetitor> result =
                transactionService.getMatchStageCompetitors(matchStageDto, mapping);

        assertEquals(1, result.size());
    }

    @Test
    public void testGetMatchStageCompetitors_whenCompetitorDoesNotMatchStageUuid_thenExcludesIt() {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchStageDto targetStage = buildMatchStageDto();
        MatchStageDto otherStage = buildMatchStageDto(); // different auto-generated UUID
        dtoMapping.getMatchStageMap().put(targetStage.getUuid(), targetStage);

        // Competitor belongs to otherStage, NOT to targetStage
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

        List<MatchStageCompetitor> result =
                transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitors_whenCompetitorHasNullMatchStage_thenExcludesIt() {
        DtoMapping dtoMapping = buildMinimalDtoMapping();
        CompetitorDto competitorDto = buildCompetitorDto();
        dtoMapping.getCompetitorMap().put(competitorDto.getUuid(), competitorDto);

        MatchStageDto targetStage = buildMatchStageDto();

        // Stage competitor with null matchStage – must be filtered out
        MatchStageCompetitorDto mscWithNullStage = new MatchStageCompetitorDto();
        mscWithNullStage.setCompetitor(competitorDto);
        mscWithNullStage.setMatchStage(null);
        dtoMapping.getMatchStageCompetitorMap().put(mscWithNullStage.getUuid(), mscWithNullStage);

        DtoToEntityMapping mapping = new DtoToEntityMapping(dtoMapping);
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.now());
        mapping.setMatch(matchEntity);

        List<MatchStageCompetitor> result =
                transactionService.getMatchStageCompetitors(targetStage, mapping);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitors_whenCompetitorDtoHasId_thenSetsIdOnEntity() {
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

        List<MatchStageCompetitor> result =
                transactionService.getMatchStageCompetitors(matchStageDto, mapping);

        assertFalse(result.isEmpty());
        assertEquals(77L, result.get(0).getId());
    }

    @Test
    public void testGetMatchStageCompetitors_whenCompetitorDtoHasNullId_thenCreatesEntityWithNullId() {
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

        List<MatchStageCompetitor> result =
                transactionService.getMatchStageCompetitors(matchStageDto, mapping);

        assertFalse(result.isEmpty());
        assertNull(result.get(0).getId());
    }
}

