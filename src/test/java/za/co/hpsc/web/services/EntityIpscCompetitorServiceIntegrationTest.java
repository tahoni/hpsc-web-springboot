package za.co.hpsc.web.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.repositories.CompetitorRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link EntityIpscCompetitorService} - exercised through
 * the interface type, with a real Spring-wired {@code EntityIpscCompetitorServiceImpl} bean
 * backed by the H2 {@code test} profile database.
 */
@Slf4j
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
@Transactional
class EntityIpscCompetitorServiceIntegrationTest {

    @Autowired
    private EntityIpscCompetitorService entityIpscCompetitorService;

    @Autowired
    private CompetitorRepository competitorRepository;

    // findCompetitor()
    @Test
    void testFindCompetitor_whenOneCompetitorHasTheNumber_thenReturnsItRegardlessOfTheName() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, "880001");

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Someone Else", 880001);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberAndOneHasTheFullName_thenReturnsTheNameMatch() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, "880002");

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("jane doe", 880099);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberAndOneHasTheNickname_thenReturnsTheNicknameMatch() {
        // Arrange
        Competitor saved = saveCompetitor("Janet", "Doe", "Jenny", "880003");

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jenny Doe", 880099);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenTheNameHasAnRoSuffix_thenMatchesTheNameWithoutIt() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, "880004");

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe RO", 880099);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorMatchesTheNumberOrTheName_thenReturnsEmpty() {
        // Arrange
        saveCompetitor("Jane", "Doe", null, "880005");

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Nobody Here", 880099);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheFullNameAndNoneTheNumber_thenReturnsEmpty() {
        // Arrange
        saveCompetitor("Jane", "Doe", null, "880006");
        saveCompetitor("Janet", "Doe", "Jane", "880007");

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 880099);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndOneHasTheFullName_thenReturnsThatCompetitor() {
        // Arrange
        Competitor jane = saveCompetitor("Jane", "Doe", null, "880008");
        saveCompetitor("John", "Doe", null, "880008");

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 880008);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(jane.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndOneHasTheNickname_thenReturnsThatCompetitor() {
        // Arrange
        Competitor janet = saveCompetitor("Janet", "Doe", "Jenny", "880009");
        saveCompetitor("John", "Doe", null, "880009");

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jenny Doe", 880009);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(janet.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndNoneHasTheName_thenReturnsEmpty() {
        // Arrange
        saveCompetitor("John", "Doe", null, "880010");
        saveCompetitor("Jack", "Doe", null, "880010");

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 880010);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndSeveralHaveTheName_thenReturnsEmpty() {
        // Arrange
        saveCompetitor("Jane", "Doe", null, "880011");
        saveCompetitor("Janet", "Doe", "Jane", "880011");

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 880011);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindCompetitor_whenTheNumberIsAnExcludedAlias_thenMatchesByNameOnly() {
        // Arrange
        saveCompetitor("Jack", "Doe", null, "15000");
        Competitor jane = saveCompetitor("Jane", "Doe", null, "880012");

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 15000);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(jane.getId(), result.get().getId());
    }

    // Helpers
    private Competitor saveCompetitor(String firstName, String lastName, String nickname, String competitorNumber) {
        Competitor competitor = new Competitor();
        competitor.setFirstName(firstName);
        competitor.setLastName(lastName);
        competitor.setNickName(nickname);
        competitor.setCompetitorNumber(competitorNumber);
        return competitorRepository.save(competitor);
    }
}
