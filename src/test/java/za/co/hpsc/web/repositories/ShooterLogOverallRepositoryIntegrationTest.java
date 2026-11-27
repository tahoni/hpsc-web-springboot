package za.co.hpsc.web.repositories;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;
import za.co.hpsc.web.domain.ShooterLogOverall;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spring-context integration test for {@link ShooterLogOverallRepository}'s {@code existsByCompetitorId} query — the
 * check behind the reject-not-cascade competitor delete — and the fetch joins of its {@code WithDetails} queries,
 * against the H2 {@code test} profile database. The fetch tests flush and clear the persistence context before reading
 * back, so they observe what the query loaded rather than cached entities.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ShooterLogOverallRepositoryIntegrationTest {

    @Autowired
    private ShooterLogOverallRepository shooterLogOverallRepository;

    @Autowired
    private EntityManager entityManager;

    // existsByCompetitorId()
    @Test
    void testExistsByCompetitorId_whenCompetitorHasAnOverallRow_thenReturnsTrue() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        persistOverall(competitor);

        // Act & Assert
        assertTrue(shooterLogOverallRepository.existsByCompetitorId(competitor.getId()));
    }

    @Test
    void testExistsByCompetitorId_whenOnlyAnotherCompetitorHasAnOverallRow_thenReturnsFalse() {
        // Arrange
        Competitor competitor = ScoringFixtures.competitor(entityManager, "Jane");
        persistOverall(ScoringFixtures.competitor(entityManager, "John"));

        // Act & Assert
        assertFalse(shooterLogOverallRepository.existsByCompetitorId(competitor.getId()));
    }

    // findAllByShooterLogIdWithDetails()
    @Test
    void testFindAllByShooterLogIdWithDetails_whenOverallHasMatches_thenFetchesThemOnceWithTheLogAndCompetitor() {
        // Arrange
        ShooterLog shooterLog = ScoringFixtures.shooterLog(entityManager);
        IpscMatch first = ScoringFixtures.match(entityManager, "Match A");
        IpscMatch second = ScoringFixtures.match(entityManager, "Match B");
        persistOverall(shooterLog, ScoringFixtures.competitor(entityManager, "Jane"), first, second);
        flushAndClear();

        // Act
        List<ShooterLogOverall> found =
                shooterLogOverallRepository.findAllByShooterLogIdWithDetails(shooterLog.getId());

        // Assert
        // The matches are joined one row each, so the overall must still come back once
        assertEquals(1, found.size());
        ShooterLogOverall overall = found.getFirst();
        assertTrue(Hibernate.isInitialized(overall.getShooterLog()));
        assertTrue(Hibernate.isInitialized(overall.getCompetitor()));
        assertTrue(Hibernate.isInitialized(overall.getMatches()));
        assertEquals(Set.of(first.getId(), second.getId()), matchIdsOf(overall));
    }

    @Test
    void testFindAllByShooterLogIdWithDetails_whenOverallHasNoMatches_thenStillReturnsItWithEmptyMatches() {
        // Arrange
        ShooterLog shooterLog = ScoringFixtures.shooterLog(entityManager);
        persistOverall(shooterLog, ScoringFixtures.competitor(entityManager, "Jane"));
        flushAndClear();

        // Act
        List<ShooterLogOverall> found =
                shooterLogOverallRepository.findAllByShooterLogIdWithDetails(shooterLog.getId());

        // Assert
        assertEquals(1, found.size());
        assertTrue(Hibernate.isInitialized(found.getFirst().getMatches()));
        assertTrue(found.getFirst().getMatches().isEmpty());
    }

    @Test
    void testFindAllByShooterLogIdWithDetails_whenAnotherLogHasOveralls_thenReturnsOnlyTheRequestedLogsOveralls() {
        // Arrange
        ShooterLog shooterLog = ScoringFixtures.shooterLog(entityManager);
        ShooterLog otherLog = ScoringFixtures.shooterLog(entityManager);
        IpscMatch match = ScoringFixtures.match(entityManager, "Match A");
        ShooterLogOverall overall =
                persistOverall(shooterLog, ScoringFixtures.competitor(entityManager, "Jane"), match);
        persistOverall(otherLog, ScoringFixtures.competitor(entityManager, "John"), match);
        flushAndClear();

        // Act
        List<ShooterLogOverall> found =
                shooterLogOverallRepository.findAllByShooterLogIdWithDetails(shooterLog.getId());

        // Assert
        assertEquals(List.of(overall.getId()), found.stream().map(ShooterLogOverall::getId).toList());
    }

    // findAllWithDetails()
    @Test
    void testFindAllWithDetails_whenOverallHasMatches_thenFetchesThemOnceWithTheLogAndCompetitor() {
        // Arrange
        ShooterLog shooterLog = ScoringFixtures.shooterLog(entityManager);
        IpscMatch first = ScoringFixtures.match(entityManager, "Match A");
        IpscMatch second = ScoringFixtures.match(entityManager, "Match B");
        ShooterLogOverall saved =
                persistOverall(shooterLog, ScoringFixtures.competitor(entityManager, "Jane"), first, second);
        flushAndClear();

        // Act
        List<ShooterLogOverall> found = shooterLogOverallRepository.findAllWithDetails().stream()
                .filter(overall -> saved.getId().equals(overall.getId())).toList();

        // Assert
        assertEquals(1, found.size());
        ShooterLogOverall overall = found.getFirst();
        assertTrue(Hibernate.isInitialized(overall.getShooterLog()));
        assertTrue(Hibernate.isInitialized(overall.getCompetitor()));
        assertTrue(Hibernate.isInitialized(overall.getMatches()));
        assertEquals(Set.of(first.getId(), second.getId()), matchIdsOf(overall));
    }

    @Test
    void testFindAllWithDetails_whenOverallHasNoMatches_thenStillReturnsItWithEmptyMatches() {
        // Arrange
        ShooterLogOverall saved = persistOverall(ScoringFixtures.shooterLog(entityManager),
                ScoringFixtures.competitor(entityManager, "Jane"));
        flushAndClear();

        // Act
        List<ShooterLogOverall> found = shooterLogOverallRepository.findAllWithDetails().stream()
                .filter(overall -> saved.getId().equals(overall.getId())).toList();

        // Assert
        assertEquals(1, found.size());
        assertTrue(Hibernate.isInitialized(found.getFirst().getMatches()));
        assertTrue(found.getFirst().getMatches().isEmpty());
    }

    // findShooterLogIdsWithoutDateCalculated()
    @Test
    void testFindShooterLogIdsWithoutDateCalculated_whenOnlySomeLogsHaveAnUncalculatedOverall_thenReturnsThoseOnce() {
        // Arrange
        ShooterLog uncalculated = ScoringFixtures.shooterLog(entityManager);
        ShooterLog calculated = ScoringFixtures.shooterLog(entityManager);
        persistOverall(uncalculated, ScoringFixtures.competitor(entityManager, "Jane"));
        persistOverall(uncalculated, ScoringFixtures.competitor(entityManager, "John"));
        persistOverall(calculated, ScoringFixtures.competitor(entityManager, "Jill"))
                .setDateCalculated(LocalDateTime.now());
        flushAndClear();

        // Act
        List<Long> found = shooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated();

        // Assert
        assertTrue(found.contains(uncalculated.getId()));
        assertFalse(found.contains(calculated.getId()));
        assertEquals(1, found.stream().filter(uncalculated.getId()::equals).count());
    }

    private void persistOverall(Competitor competitor) {
        persistOverall(ScoringFixtures.shooterLog(entityManager), competitor);
    }

    private ShooterLogOverall persistOverall(ShooterLog shooterLog, Competitor competitor, IpscMatch... matches) {
        ShooterLogOverall overall = new ShooterLogOverall();
        overall.setShooterLog(shooterLog);
        overall.setCompetitor(competitor);
        overall.setCompetitorCategory(CompetitorCategory.NONE);
        overall.setDivision(Division.OPEN);
        overall.setMatches(new ArrayList<>(List.of(matches)));
        entityManager.persist(overall);
        return overall;
    }

    private Set<Long> matchIdsOf(ShooterLogOverall overall) {
        return overall.getMatches().stream().map(IpscMatch::getId).collect(Collectors.toSet());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
