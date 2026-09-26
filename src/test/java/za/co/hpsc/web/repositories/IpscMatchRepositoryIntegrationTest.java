package za.co.hpsc.web.repositories;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link IpscMatchRepository}'s custom queries and
 * {@link IpscMatch}'s cascaded {@link IpscMatch#getStages() stages} mapping, against the H2
 * {@code test} profile database. Each test flushes and clears the persistence context before
 * reading back, so it observes what was really written rather than cached entities.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class IpscMatchRepositoryIntegrationTest {

    @Autowired
    private IpscMatchRepository ipscMatchRepository;

    @Autowired
    private IpscMatchStageRepository ipscMatchStageRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EntityManager entityManager;

    // findByIdWithClub()
    @Test
    void testFindByIdWithClub_whenMatchExists_thenClubIsFetchedWithMatch() {
        // Arrange
        IpscMatch match = newMatch("Club Championship");
        match.setClub(createClub());
        Long matchId = ipscMatchRepository.save(match).getId();
        flushAndClear();

        // Act
        IpscMatch found = ipscMatchRepository.findByIdWithClub(matchId).orElseThrow();

        // Assert
        assertTrue(Hibernate.isInitialized(found.getClub()));
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, found.getClub().getIdentifier());
    }

    @Test
    void testFindByIdWithClub_whenMatchHasNoClub_thenStillReturnsMatch() {
        // Arrange
        Long matchId = ipscMatchRepository.save(newMatch("Club Championship")).getId();
        flushAndClear();

        // Act & Assert
        assertNull(ipscMatchRepository.findByIdWithClub(matchId).orElseThrow().getClub());
    }

    @Test
    void testFindByIdWithClub_whenMatchDoesNotExist_thenReturnsEmpty() {
        // Act & Assert
        assertTrue(ipscMatchRepository.findByIdWithClub(999L).isEmpty());
    }

    // findAllWithClub()
    @Test
    void testFindAllWithClub_whenMatchesExist_thenEachClubIsFetchedWithItsMatch() {
        // Arrange
        Club club = createClub();
        IpscMatch first = newMatch("First Match");
        first.setClub(club);
        IpscMatch second = newMatch("Second Match");
        second.setClub(club);
        ipscMatchRepository.saveAll(List.of(first, second, newMatch("Clubless Match")));
        flushAndClear();

        // Act
        List<IpscMatch> matches = ipscMatchRepository.findAllWithClub();

        // Assert
        assertEquals(3, matches.size());
        assertTrue(matches.stream().allMatch(match -> Hibernate.isInitialized(match.getClub())));
    }

    // IpscMatch.stages cascade
    @Test
    void testSave_whenNewMatchHasStages_thenStagesArePersistedWithIt() {
        // Arrange
        IpscMatch match = newMatch("Club Championship");
        addStage(match, 1, "Stage 1");
        addStage(match, 2, "Stage 2");

        // Act
        Long matchId = ipscMatchRepository.save(match).getId();
        flushAndClear();

        // Assert
        assertEquals(List.of("Stage 1", "Stage 2"), stageNames(matchId));
    }

    @Test
    void testSave_whenStageIsRemovedFromCollection_thenOrphanRemovalDeletesIt() {
        // Arrange
        IpscMatch match = newMatch("Club Championship");
        addStage(match, 1, "Stage 1");
        addStage(match, 2, "Stage 2");
        Long matchId = ipscMatchRepository.save(match).getId();
        flushAndClear();
        IpscMatch managed = ipscMatchRepository.findById(matchId).orElseThrow();

        // Act
        managed.getStages().removeIf(stage -> stage.getStageNumber() == 2);
        flushAndClear();

        // Assert
        assertEquals(List.of("Stage 1"), stageNames(matchId));
    }

    @Test
    void testDelete_whenMatchHasStages_thenStagesAreDeletedWithIt() {
        // Arrange
        IpscMatch match = newMatch("Club Championship");
        addStage(match, 1, "Stage 1");
        Long matchId = ipscMatchRepository.save(match).getId();
        flushAndClear();

        // Act
        ipscMatchRepository.delete(ipscMatchRepository.findById(matchId).orElseThrow());
        flushAndClear();

        // Assert
        assertFalse(ipscMatchRepository.existsById(matchId));
        assertEquals(0, ipscMatchStageRepository.count());
    }

    // Helpers
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private Club createClub() {
        Club club = new Club();
        club.setName("Test Club");
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);
        return clubRepository.save(club);
    }

    private IpscMatch newMatch(String name) {
        IpscMatch match = new IpscMatch();
        match.setName(name);
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        return match;
    }

    private void addStage(IpscMatch match, int stageNumber, String stageName) {
        IpscMatchStage stage = new IpscMatchStage();
        stage.setMatch(match);
        stage.setStageNumber(stageNumber);
        stage.setStageName(stageName);
        match.getStages().add(stage);
    }

    private List<String> stageNames(Long matchId) {
        return ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(matchId).stream()
                .map(IpscMatchStage::getStageName)
                .toList();
    }
}
