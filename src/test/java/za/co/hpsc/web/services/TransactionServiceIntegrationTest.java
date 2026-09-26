package za.co.hpsc.web.services;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.TransactionService.StageSaveMode;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link TransactionService} - exercised through the
 * interface type, with a real Spring-wired {@code TransactionServiceImpl} bean backed by the H2
 * {@code test} profile database.
 *
 * <p>
 * Unlike the other service integration tests, this class is deliberately <strong>not</strong>
 * {@code @Transactional}: a surrounding test transaction would absorb every
 * {@code TransactionService} transaction, hiding whether it really commits or rolls back. Each
 * test therefore works against committed data, which {@link #cleanUp()} deletes afterwards.
 * </p>
 */
@Slf4j
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private IpscMatchRepository ipscMatchRepository;

    @Autowired
    private IpscMatchStageRepository ipscMatchStageRepository;

    @Autowired
    private MatchCompetitorRepository matchCompetitorRepository;

    @AfterEach
    void cleanUp() {
        matchCompetitorRepository.deleteAll();
        ipscMatchRepository.deleteAll();
        competitorRepository.deleteAll();
        clubRepository.deleteAll();
    }

    // saveCompetitor()
    @Test
    void testSaveCompetitor_whenCompetitorIsNew_thenCommitsItWithItsAssociationsLoaded() {
        // Arrange
        Competitor competitor = newCompetitor("Jane");
        competitor.setHomeClub(createClub());
        competitor.setEmailAddresses(List.of("jane.doe@example.com"));

        // Act
        Competitor saved = transactionService.saveCompetitor(competitor);

        // Assert
        assertNotNull(saved.getId());
        assertTrue(Hibernate.isInitialized(saved.getHomeClub()));
        assertTrue(Hibernate.isInitialized(saved.getEmailAddresses()));
        Competitor committed = competitorRepository.findByIdWithHomeClubAndEmailAddresses(saved.getId()).orElseThrow();
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, committed.getHomeClub().getIdentifier());
        assertEquals(List.of("jane.doe@example.com"), committed.getEmailAddresses());
    }

    @Test
    void testSaveCompetitor_whenCompetitorExists_thenCommitsTheChanges() {
        // Arrange
        Competitor existing = transactionService.saveCompetitor(newCompetitor("Jane"));
        existing.setFirstName("Janet");
        existing.setEmailAddresses(List.of("janet@example.com"));

        // Act
        transactionService.saveCompetitor(existing);

        // Assert
        Competitor committed = competitorRepository.findByIdWithHomeClubAndEmailAddresses(existing.getId()).orElseThrow();
        assertEquals("Janet", committed.getFirstName());
        assertEquals(List.of("janet@example.com"), committed.getEmailAddresses());
    }

    // saveCompetitors()
    @Test
    void testSaveCompetitors_whenAllAreValid_thenCommitsEachInOrder() {
        // Act
        List<Competitor> saved = transactionService.saveCompetitors(
                List.of(newCompetitor("Jane"), newCompetitor("John")));

        // Assert
        assertEquals(2, saved.size());
        assertEquals("Jane", saved.get(0).getFirstName());
        assertEquals("John", saved.get(1).getFirstName());
        assertEquals(2, competitorRepository.count());
    }

    @Test
    void testSaveCompetitors_whenOneFails_thenRollsBackEveryOne() {
        // Arrange
        Competitor invalid = newCompetitor("John");
        invalid.setLastName(null);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class,
                () -> transactionService.saveCompetitors(List.of(newCompetitor("Jane"), invalid)));
        assertEquals(0, competitorRepository.count());
    }

    // deleteCompetitor()
    @Test
    void testDeleteCompetitor_whenUnreferenced_thenCommitsTheDeleteWithItsEmailAddresses() {
        // Arrange
        Competitor competitor = newCompetitor("Jane");
        competitor.setEmailAddresses(List.of("jane.doe@example.com"));
        Competitor saved = transactionService.saveCompetitor(competitor);

        // Act
        transactionService.deleteCompetitor(saved);

        // Assert
        assertFalse(competitorRepository.existsById(saved.getId()));
    }

    @Test
    void testDeleteCompetitor_whenStillReferenced_thenThrowsAndRollsBack() {
        // Arrange
        Competitor competitor = transactionService.saveCompetitor(newCompetitor("Jane"));
        IpscMatch match = transactionService.saveMatch(newMatch("Club Championship"));
        recordResult(competitor, match);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> transactionService.deleteCompetitor(competitor));
        assertTrue(competitorRepository.existsById(competitor.getId()));
    }

    // saveMatch(IpscMatch)
    @Test
    void testSaveMatch_whenMatchIsNewWithStages_thenCommitsMatchAndStagesTogether() {
        // Arrange
        IpscMatch match = newMatch("Club Championship");
        addStage(match, 1, "Stage 1");
        addStage(match, 2, "Stage 2");

        // Act
        IpscMatch saved = transactionService.saveMatch(match);

        // Assert
        assertNotNull(saved.getId());
        assertTrue(saved.getStages().stream().allMatch(stage -> stage.getId() != null));
        assertEquals(List.of("Stage 1", "Stage 2"), stageNames(saved.getId()));
    }

    @Test
    void testSaveMatch_whenMatchExists_thenCommitsTheChangesAndLeavesStagesUntouched() {
        // Arrange
        IpscMatch match = newMatch("Club Championship");
        addStage(match, 1, "Stage 1");
        IpscMatch existing = ipscMatchRepository.findByIdWithClub(transactionService.saveMatch(match).getId())
                .orElseThrow();
        existing.setName("Renamed Championship");

        // Act
        IpscMatch saved = transactionService.saveMatch(existing);

        // Assert
        assertEquals("Renamed Championship", ipscMatchRepository.findById(saved.getId()).orElseThrow().getName());
        assertEquals(List.of("Stage 1"), stageNames(saved.getId()));
        assertEquals(1, saved.getStages().size());
    }

    // saveMatch(IpscMatch, List, StageSaveMode)
    @Test
    void testSaveMatch_whenReplacingWithAReusedStageNumber_thenCommitsOnlyTheNewStages() {
        // Arrange
        IpscMatch match = newMatch("Club Championship");
        addStage(match, 1, "Stage 1");
        addStage(match, 2, "Stage 2");
        IpscMatch existing = ipscMatchRepository.findByIdWithClub(transactionService.saveMatch(match).getId())
                .orElseThrow();

        // Act
        IpscMatch saved = transactionService.saveMatch(existing, List.of(newStage(1, "Replacement Stage")),
                StageSaveMode.REPLACE);

        // Assert
        assertEquals(1, saved.getStages().size());
        assertEquals(List.of("Replacement Stage"), stageNames(saved.getId()));
    }

    @Test
    void testSaveMatch_whenUpserting_thenUpdatesMatchingStagesAndAddsTheRest() {
        // Arrange
        IpscMatch match = newMatch("Club Championship");
        addStage(match, 1, "Stage 1");
        IpscMatch existing = ipscMatchRepository.findByIdWithClub(transactionService.saveMatch(match).getId())
                .orElseThrow();

        // Act
        transactionService.saveMatch(existing, List.of(newStage(1, "Renamed Stage"), newStage(2, "Added Stage")),
                StageSaveMode.UPSERT);

        // Assert
        assertEquals(List.of("Renamed Stage", "Added Stage"), stageNames(existing.getId()));
    }

    // saveMatches()
    @Test
    void testSaveMatches_whenOneFails_thenRollsBackEveryOne() {
        // Arrange
        IpscMatch valid = newMatch("First Match");
        IpscMatch invalid = newMatch("Second Match");
        addStage(invalid, 1, "Stage 1");
        addStage(invalid, 1, "Duplicate Stage 1");

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> transactionService.saveMatches(List.of(valid, invalid)));
        assertEquals(0, ipscMatchRepository.count());
        assertEquals(0, ipscMatchStageRepository.count());
    }

    // deleteMatch()
    @Test
    void testDeleteMatch_whenUnreferenced_thenCommitsTheDeleteWithItsStages() {
        // Arrange
        IpscMatch match = newMatch("Club Championship");
        addStage(match, 1, "Stage 1");
        IpscMatch saved = transactionService.saveMatch(match);

        // Act
        transactionService.deleteMatch(saved);

        // Assert
        assertFalse(ipscMatchRepository.existsById(saved.getId()));
        assertEquals(0, ipscMatchStageRepository.count());
    }

    @Test
    void testDeleteMatch_whenStillReferenced_thenThrowsAndRollsBack() {
        // Arrange
        IpscMatch match = transactionService.saveMatch(newMatch("Club Championship"));
        recordResult(transactionService.saveCompetitor(newCompetitor("Jane")), match);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> transactionService.deleteMatch(match));
        assertTrue(ipscMatchRepository.existsById(match.getId()));
    }

    // Helpers
    private Club createClub() {
        Club club = new Club();
        club.setName("Test Club");
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);
        return clubRepository.save(club);
    }

    private Competitor newCompetitor(String firstName) {
        Competitor competitor = new Competitor();
        competitor.setFirstName(firstName);
        competitor.setLastName("Doe");
        return competitor;
    }

    private IpscMatch newMatch(String name) {
        IpscMatch match = new IpscMatch();
        match.setName(name);
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        return match;
    }

    private IpscMatchStage newStage(int stageNumber, String stageName) {
        IpscMatchStage stage = new IpscMatchStage();
        stage.setStageNumber(stageNumber);
        stage.setStageName(stageName);
        return stage;
    }

    private void addStage(IpscMatch match, int stageNumber, String stageName) {
        IpscMatchStage stage = newStage(stageNumber, stageName);
        stage.setMatch(match);
        match.getStages().add(stage);
    }

    private List<String> stageNames(Long matchId) {
        return ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(matchId).stream()
                .map(IpscMatchStage::getStageName)
                .toList();
    }

    private void recordResult(Competitor competitor, IpscMatch match) {
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(match);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitorRepository.save(matchCompetitor);
    }
}
