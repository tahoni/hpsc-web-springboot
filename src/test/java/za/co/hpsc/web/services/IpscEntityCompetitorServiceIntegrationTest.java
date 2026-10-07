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
 * Spring-context integration test for {@link IpscEntityCompetitorService} - exercised through
 * the interface type, with a real Spring-wired {@code EntityIpscCompetitorServiceImpl} bean
 * backed by the H2 {@code test} profile database.
 */
@Slf4j
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
@Transactional
class IpscEntityCompetitorServiceIntegrationTest {

    @Autowired
    private IpscEntityCompetitorService ipscEntityCompetitorService;

    @Autowired
    private CompetitorRepository competitorRepository;

    // findCompetitor()
    @Test
    void testFindCompetitor_whenOneCompetitorHasTheNumber_thenReturnsItRegardlessOfTheName() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, 880001);

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880001", "Someone Else");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberAndOneHasTheFullName_thenReturnsTheNameMatch() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, 880002);

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880099", "jane doe");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberAndOneHasTheNickname_thenReturnsTheNicknameMatch() {
        // Arrange
        Competitor saved = saveCompetitor("Janet", "Doe", "Jenny", 880003);

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880099", "Jenny Doe");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenTheNameHasAnRoSuffix_thenMatchesTheNameWithoutIt() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, 880004);

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880099", "Jane Doe RO");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenTheNameHasRoInTheMiddle_thenMatchesTheNameWithoutIt() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, 880005);

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880099", "Jane (RO) Doe");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorMatchesTheNumberOrTheName_thenThrowsNonFatalException() {
        // Arrange
        saveCompetitor("Jane", "Doe", null, 880005);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880099", "Nobody Here"));
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheFullNameAndNoneTheNumber_thenThrowsValidationException() {
        // Arrange
        saveCompetitor("Jane", "Doe", null, 880006);
        saveCompetitor("Janet", "Doe", "Jane", 880007);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880099", "Jane Doe"));
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndOneHasTheFullName_thenReturnsThatCompetitor() {
        // Arrange
        Competitor jane = saveCompetitor("Jane", "Doe", null, 880008);
        saveCompetitor("John", "Doe", null, 880008);

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880008", "Jane Doe");

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
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880009", "Jenny Doe");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(janet.getId(), result.get().getId());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndNoneHasTheName_thenThrowsValidationException() {
        // Arrange
        saveCompetitor("John", "Doe", null, 880010);
        saveCompetitor("Jack", "Doe", null, 880010);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880010", "Jane Doe"));
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndSeveralHaveTheName_thenThrowsValidationException() {
        // Arrange
        saveCompetitor("Jane", "Doe", null, 880011);
        saveCompetitor("Janet", "Doe", "Jane", 880011);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880011", "Jane Doe"));
    }

    @Test
    void testFindCompetitor_whenTheNumberIsAnExcludedAlias_thenMatchesByNameOnly() {
        // Arrange
        saveCompetitor("Jack", "Doe", null, 15000);
        Competitor jane = saveCompetitor("Jane", "Doe", null, 880012);

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("15000", "Jane Doe");

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
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("ZZ880013", "Someone Else");

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
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("8001015009087", "Someone Else");

        // Assert
        assertEquals(saved.getId(), result.orElseThrow().getId());
    }

    @Test
    void testFindCompetitor_whenOnlyTheNumberIsGivenAndNoCompetitorHasIt_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880098", null));
    }

    @Test
    void testFindCompetitor_whenOnlyTheNumberIsGivenAndOneCompetitorHasIt_thenReturnsIt() {
        // Arrange
        Competitor saved = saveCompetitor("Jane", "Doe", null, 880014);

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("880014", null);

        // Assert
        assertEquals(saved.getId(), result.orElseThrow().getId());
    }

    @Test
    void testFindCompetitor_whenTheNameAndNumberAreBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName(" ", ""));
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
