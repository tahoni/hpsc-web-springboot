package za.co.hpsc.web.services.impl;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TransactionServiceImpl}'s impl-only protected helper methods
 * ({@code loadAssociations}, {@code replaceStages}, {@code upsertStages}) - not declared on
 * {@link za.co.hpsc.web.services.TransactionService}. The interface's save/delete contract is
 * covered by {@link za.co.hpsc.web.services.TransactionServiceTest}.
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

    private TransactionServiceImpl transactionServiceImpl;

    @BeforeEach
    void setUp() {
        transactionServiceImpl = new TransactionServiceImpl(competitorRepository, ipscMatchRepository,
                ipscMatchStageRepository, transactionManager);
    }

    // loadAssociations(Competitor)
    @Test
    void testLoadAssociations_whenCompetitorHasPlainAssociations_thenReturnsSameCompetitorUnchanged() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setEmailAddresses(List.of("jane.doe@example.com"));

        // Act
        Competitor result = transactionServiceImpl.loadAssociations(competitor);

        // Assert
        assertSame(competitor, result);
        assertTrue(Hibernate.isInitialized(result.getEmailAddresses()));
        assertEquals(List.of("jane.doe@example.com"), result.getEmailAddresses());
    }

    // loadAssociations(IpscMatch)
    @Test
    void testLoadAssociations_whenMatchHasNoClub_thenReturnsSameMatchUnchanged() {
        // Arrange
        IpscMatch match = matchWithStage(1, "Stage 1");

        // Act
        IpscMatch result = transactionServiceImpl.loadAssociations(match);

        // Assert
        assertSame(match, result);
        assertNull(result.getClub());
        assertEquals(1, result.getStages().size());
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
