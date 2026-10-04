package za.co.hpsc.web.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
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
        Competitor saved = saveCompetitor("Jane", "Doe", null, 880001);

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Someone Else", "880001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberAndOneHasTheFullName_thenReturnsTheNameMatch() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, 880002);

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("jane doe", "880099");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberAndOneHasTheNickname_thenReturnsTheNicknameMatch() {
        // Arrange
        Competitor saved = saveCompetitor("Janet", "Doe", "Jenny", 880003);

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jenny Doe", "880099");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenTheNameHasAnRoSuffix_thenMatchesTheNameWithoutIt() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, 880004);

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe RO", "880099");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorMatchesTheNumberOrTheName_thenThrowsNonFatalException() {
        // Arrange
        saveCompetitor("Jane", "Doe", null, 880005);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> entityIpscCompetitorService.findCompetitor("Nobody Here", "880099"));
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheFullNameAndNoneTheNumber_thenThrowsNonFatalException() {
        // Arrange
        saveCompetitor("Jane", "Doe", null, 880006);
        saveCompetitor("Janet", "Doe", "Jane", 880007);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> entityIpscCompetitorService.findCompetitor("Jane Doe", "880099"));
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndOneHasTheFullName_thenReturnsThatCompetitor() {
        // Arrange
        Competitor jane = saveCompetitor("Jane", "Doe", null, 880008);
        saveCompetitor("John", "Doe", null, 880008);

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", "880008");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(jane.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndOneHasTheNickname_thenReturnsThatCompetitor() {
        // Arrange
        Competitor janet = saveCompetitor("Janet", "Doe", "Jenny", 880009);
        saveCompetitor("John", "Doe", null, 880009);

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jenny Doe", "880009");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(janet.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndNoneHasTheName_thenThrowsNonFatalException() {
        // Arrange
        saveCompetitor("John", "Doe", null, 880010);
        saveCompetitor("Jack", "Doe", null, 880010);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> entityIpscCompetitorService.findCompetitor("Jane Doe", "880010"));
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndSeveralHaveTheName_thenThrowsNonFatalException() {
        // Arrange
        saveCompetitor("Jane", "Doe", null, 880011);
        saveCompetitor("Janet", "Doe", "Jane", 880011);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> entityIpscCompetitorService.findCompetitor("Jane Doe", "880011"));
    }

    @Test
    void testFindCompetitor_whenTheNumberIsAnExcludedAlias_thenMatchesByNameOnly() {
        // Arrange
        saveCompetitor("Jack", "Doe", null, 15000);
        Competitor jane = saveCompetitor("Jane", "Doe", null, 880012);

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", "15000");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(jane.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenOneCompetitorHasTheIdNumber_thenReturnsItRegardlessOfTheName() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, 880013);
        saved.setIdNumber("ZZ880013");
        competitorRepository.save(saved);

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Someone Else", "ZZ880013");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenTheIdNumberIsTooLongForAnInt_thenReturnsTheCompetitorWithThatIdNumber() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, 880015);
        saved.setIdNumber("8001015009087");
        competitorRepository.save(saved);

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Someone Else", "8001015009087");

        // Assert
        assertEquals(saved.getId(), result.orElseThrow().getId());
    }

    @Test
    void testFindCompetitor_whenOnlyTheNumberIsGivenAndNoCompetitorHasIt_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> entityIpscCompetitorService.findCompetitor(null, "880098"));
    }

    @Test
    void testFindCompetitor_whenOnlyTheNumberIsGivenAndOneCompetitorHasIt_thenReturnsIt() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, 880014);

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor(null, "880014");

        // Assert
        assertEquals(saved.getId(), result.orElseThrow().getId());
    }

    @Test
    void testFindCompetitor_whenTheNameAndNumberAreBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> entityIpscCompetitorService.findCompetitor("", " "));
    }

    // Helpers
    private Competitor saveCompetitor(String firstName, String lastName, String nickname, Integer competitorNumber) {
        Competitor competitor = new Competitor();
        competitor.setFirstName(firstName);
        competitor.setLastName(lastName);
        competitor.setNickName(nickname);
        competitor.setCompetitorNumber(competitorNumber);
        return competitorRepository.save(competitor);
    }
}
