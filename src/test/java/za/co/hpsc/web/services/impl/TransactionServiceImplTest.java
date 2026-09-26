package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.services.TransactionService.StageSaveMode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TransactionServiceImpl}, with its repositories and
 * {@link PlatformTransactionManager} mocked, so each test also checks that the work was committed
 * (or rolled back) through the transaction manager. See
 * {@link za.co.hpsc.web.services.IpscCompetitorServiceIntegrationTest}/
 * {@link za.co.hpsc.web.services.IpscMatchServiceIntegrationTest} for the same writes exercised
 * against a real H2-backed Spring context.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private IpscMatchStageRepository ipscMatchStageRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private TransactionStatus transactionStatus;

    private TransactionServiceImpl transactionServiceImpl;

    @BeforeEach
    void setUp() {
        transactionServiceImpl = new TransactionServiceImpl(competitorRepository, ipscMatchRepository,
                ipscMatchStageRepository, transactionManager);
    }

    // saveCompetitor()
    @Test
    void testSaveCompetitor_whenCalled_thenSavesAndCommits() {
        // Arrange
        stubTransaction();
        Competitor competitor = new Competitor();
        when(competitorRepository.save(competitor)).thenReturn(competitor);

        // Act
        Competitor result = transactionServiceImpl.saveCompetitor(competitor);

        // Assert
        assertSame(competitor, result);
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    void testSaveCompetitor_whenSaveFails_thenRollsBackAndRethrows() {
        // Arrange
        stubTransaction();
        Competitor competitor = new Competitor();
        when(competitorRepository.save(competitor)).thenThrow(new DataIntegrityViolationException("duplicate"));

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> transactionServiceImpl.saveCompetitor(competitor));
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(any());
    }

    // saveCompetitors()
    @Test
    void testSaveCompetitors_whenCalled_thenSavesEachInOneTransactionInOrder() {
        // Arrange
        stubTransaction();
        Competitor first = new Competitor();
        first.setFirstName("Jane");
        Competitor second = new Competitor();
        second.setFirstName("John");
        when(competitorRepository.save(any(Competitor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Competitor> result = transactionServiceImpl.saveCompetitors(List.of(first, second));

        // Assert
        assertEquals(List.of(first, second), result);
        verify(transactionManager, times(1)).getTransaction(any());
        verify(transactionManager).commit(transactionStatus);
    }

    // deleteCompetitor()
    @Test
    void testDeleteCompetitor_whenCalled_thenDeletesFlushesAndCommits() {
        // Arrange
        stubTransaction();
        Competitor competitor = new Competitor();

        // Act
        transactionServiceImpl.deleteCompetitor(competitor);

        // Assert
        verify(competitorRepository).delete(competitor);
        verify(competitorRepository).flush();
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    void testDeleteCompetitor_whenFlushFails_thenRollsBackAndRethrows() {
        // Arrange
        stubTransaction();
        doThrow(new DataIntegrityViolationException("FK violation")).when(competitorRepository).flush();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class,
                () -> transactionServiceImpl.deleteCompetitor(new Competitor()));
        verify(transactionManager).rollback(transactionStatus);
    }

    // saveMatch(IpscMatch)
    @Test
    void testSaveMatch_whenCalledWithMatchOnly_thenSavesWithoutTouchingStages() {
        // Arrange
        stubTransaction();
        IpscMatch match = new IpscMatch();
        when(ipscMatchRepository.save(match)).thenReturn(match);

        // Act
        IpscMatch result = transactionServiceImpl.saveMatch(match);

        // Assert
        assertSame(match, result);
        verifyNoInteractions(ipscMatchStageRepository);
        verify(transactionManager).commit(transactionStatus);
    }

    // saveMatch(IpscMatch, List, StageSaveMode)
    @Test
    void testSaveMatch_whenModeIsReplace_thenReplacesStagesOnSavedMatch() {
        // Arrange
        stubTransaction();
        IpscMatch match = matchWithStage(1, "Old Stage");
        when(ipscMatchRepository.save(match)).thenReturn(match);
        IpscMatchStage replacement = newStage(1, "New Stage");

        // Act
        IpscMatch result = transactionServiceImpl.saveMatch(match, List.of(replacement), StageSaveMode.REPLACE);

        // Assert
        assertEquals(List.of(replacement), result.getStages());
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    void testSaveMatch_whenModeIsUpsert_thenUpsertsStagesOnSavedMatch() {
        // Arrange
        stubTransaction();
        IpscMatch match = matchWithStage(1, "Stage 1");
        when(ipscMatchRepository.save(match)).thenReturn(match);
        IpscMatchStage added = newStage(2, "Stage 2");

        // Act
        IpscMatch result = transactionServiceImpl.saveMatch(match, List.of(added), StageSaveMode.UPSERT);

        // Assert
        assertEquals(2, result.getStages().size());
        assertTrue(result.getStages().contains(added));
    }

    @Test
    void testSaveMatch_whenModeIsUpsertAndStagesIsNull_thenLeavesStagesUntouched() {
        // Arrange
        stubTransaction();
        IpscMatch match = matchWithStage(1, "Stage 1");
        when(ipscMatchRepository.save(match)).thenReturn(match);

        // Act
        IpscMatch result = transactionServiceImpl.saveMatch(match, null, StageSaveMode.UPSERT);

        // Assert
        assertEquals(1, result.getStages().size());
        verifyNoInteractions(ipscMatchStageRepository);
    }

    // saveMatches()
    @Test
    void testSaveMatches_whenCalled_thenSavesEachInOneTransactionInOrder() {
        // Arrange
        stubTransaction();
        IpscMatch first = new IpscMatch();
        first.setName("First Match");
        IpscMatch second = new IpscMatch();
        second.setName("Second Match");
        when(ipscMatchRepository.save(any(IpscMatch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<IpscMatch> result = transactionServiceImpl.saveMatches(List.of(first, second));

        // Assert
        assertEquals(List.of(first, second), result);
        verify(transactionManager, times(1)).getTransaction(any());
        verify(transactionManager).commit(transactionStatus);
    }

    // deleteMatch()
    @Test
    void testDeleteMatch_whenCalled_thenDeletesFlushesAndCommits() {
        // Arrange
        stubTransaction();
        IpscMatch match = new IpscMatch();

        // Act
        transactionServiceImpl.deleteMatch(match);

        // Assert
        verify(ipscMatchRepository).delete(match);
        verify(ipscMatchRepository).flush();
        verify(transactionManager).commit(transactionStatus);
    }

    @Test
    void testDeleteMatch_whenFlushFails_thenRollsBackAndRethrows() {
        // Arrange
        stubTransaction();
        doThrow(new DataIntegrityViolationException("FK violation")).when(ipscMatchRepository).flush();

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> transactionServiceImpl.deleteMatch(new IpscMatch()));
        verify(transactionManager).rollback(transactionStatus);
    }

    // replaceStages()
    @Test
    void testReplaceStages_whenStagesIsNull_thenDeletesExistingAndLeavesNone() {
        // Arrange
        IpscMatch match = matchWithStage(1, "Stage 1");
        IpscMatchStage existingStage = match.getStages().getFirst();

        // Act
        transactionServiceImpl.replaceStages(match, null);

        // Assert
        assertTrue(match.getStages().isEmpty());
        verify(ipscMatchStageRepository).deleteAll(List.of(existingStage));
        verify(ipscMatchRepository).flush();
        verify(ipscMatchStageRepository, never()).save(any(IpscMatchStage.class));
    }

    @Test
    void testReplaceStages_whenStagesIsEmpty_thenLeavesNoneWithoutSaving() {
        // Arrange
        IpscMatch match = new IpscMatch();

        // Act
        transactionServiceImpl.replaceStages(match, List.of());

        // Assert
        assertTrue(match.getStages().isEmpty());
        verify(ipscMatchStageRepository, never()).save(any(IpscMatchStage.class));
    }

    @Test
    void testReplaceStages_whenStagesProvided_thenDeletesOldBeforeSavingEachNewInOrder() {
        // Arrange
        IpscMatch match = matchWithStage(1, "Old Stage");
        IpscMatchStage first = newStage(1, "Stage 1");
        IpscMatchStage second = newStage(2, "Stage 2");

        // Act
        transactionServiceImpl.replaceStages(match, List.of(first, second));

        // Assert
        assertEquals(List.of(first, second), match.getStages());
        assertSame(match, first.getMatch());
        assertSame(match, second.getMatch());
        var inOrder = inOrder(ipscMatchStageRepository, ipscMatchRepository);
        inOrder.verify(ipscMatchStageRepository).deleteAll(anyList());
        inOrder.verify(ipscMatchRepository).flush();
        inOrder.verify(ipscMatchStageRepository).save(first);
        inOrder.verify(ipscMatchStageRepository).save(second);
    }

    // upsertStages()
    @Test
    void testUpsertStages_whenStageNumberMatchesExisting_thenUpdatesThatStageInPlace() {
        // Arrange
        IpscMatch match = matchWithStage(1, "Original Name");
        IpscMatchStage existingStage = match.getStages().getFirst();

        // Act
        transactionServiceImpl.upsertStages(match, List.of(newStage(1, "Updated Name")));

        // Assert
        assertEquals(List.of(existingStage), match.getStages());
        assertEquals("Updated Name", existingStage.getStageName());
        verify(ipscMatchStageRepository, never()).save(any(IpscMatchStage.class));
    }

    @Test
    void testUpsertStages_whenStageNumberIsNew_thenAddsStageWithoutRemovingExisting() {
        // Arrange
        IpscMatch match = matchWithStage(1, "Stage 1");
        IpscMatchStage existingStage = match.getStages().getFirst();
        IpscMatchStage newStage = newStage(2, "Stage 2");

        // Act
        transactionServiceImpl.upsertStages(match, List.of(newStage));

        // Assert
        assertEquals(List.of(existingStage, newStage), match.getStages());
        assertEquals("Stage 1", existingStage.getStageName());
        assertSame(match, newStage.getMatch());
        verify(ipscMatchStageRepository).save(newStage);
    }

    @Test
    void testUpsertStages_whenStageNameIsNull_thenExistingStageNameIsUnchanged() {
        // Arrange
        IpscMatch match = matchWithStage(1, "Original Name");

        // Act
        transactionServiceImpl.upsertStages(match, List.of(newStage(1, null)));

        // Assert
        assertEquals("Original Name", match.getStages().getFirst().getStageName());
    }

    // Helpers
    private void stubTransaction() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
    }

    private IpscMatch matchWithStage(int stageNumber, String stageName) {
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        IpscMatchStage stage = newStage(stageNumber, stageName);
        stage.setId(100L);
        stage.setMatch(match);
        match.getStages().add(stage);
        return match;
    }

    private IpscMatchStage newStage(int stageNumber, String stageName) {
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageNumber(stageNumber);
        stage.setStageName(stageName);
        return stage;
    }
}
