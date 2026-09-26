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
import za.co.hpsc.web.domain.Competitor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link CompetitorRepository}'s custom queries and
 * {@link Competitor}'s {@code emailAddresses} element collection, against the H2 {@code test}
 * profile database. Each test flushes and clears the persistence context before reading back, so
 * it observes what was really written rather than cached entities.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CompetitorRepositoryIntegrationTest {

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EntityManager entityManager;

    // findByIdWithHomeClubAndEmailAddresses()
    @Test
    void testFindByIdWithHomeClubAndEmailAddresses_whenCompetitorExists_thenBothAreFetchedWithCompetitor() {
        // Arrange
        Competitor competitor = newCompetitor("Jane");
        competitor.setHomeClub(createClub());
        competitor.setEmailAddresses(List.of("jane.doe@example.com", "jane@example.org"));
        Long competitorId = competitorRepository.save(competitor).getId();
        flushAndClear();

        // Act
        Competitor found = competitorRepository.findByIdWithHomeClubAndEmailAddresses(competitorId).orElseThrow();

        // Assert
        assertTrue(Hibernate.isInitialized(found.getHomeClub()));
        assertTrue(Hibernate.isInitialized(found.getEmailAddresses()));
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, found.getHomeClub().getIdentifier());
        assertEquals(List.of("jane.doe@example.com", "jane@example.org"), found.getEmailAddresses());
    }

    @Test
    void testFindByIdWithHomeClubAndEmailAddresses_whenCompetitorHasNeither_thenStillReturnsCompetitor() {
        // Arrange
        Long competitorId = competitorRepository.save(newCompetitor("Jane")).getId();
        flushAndClear();

        // Act
        Competitor found = competitorRepository.findByIdWithHomeClubAndEmailAddresses(competitorId).orElseThrow();

        // Assert
        assertNull(found.getHomeClub());
        assertTrue(found.getEmailAddresses().isEmpty());
    }

    @Test
    void testFindByIdWithHomeClubAndEmailAddresses_whenCompetitorDoesNotExist_thenReturnsEmpty() {
        // Act & Assert
        assertTrue(competitorRepository.findByIdWithHomeClubAndEmailAddresses(999L).isEmpty());
    }

    // findAllWithHomeClubAndEmailAddresses()
    @Test
    void testFindAllWithHomeClubAndEmailAddresses_whenCompetitorsExist_thenEachIsReturnedOnceWithBothFetched() {
        // Arrange
        Competitor first = newCompetitor("Jane");
        first.setHomeClub(createClub());
        first.setEmailAddresses(List.of("jane.doe@example.com", "jane@example.org"));
        competitorRepository.saveAll(List.of(first, newCompetitor("John")));
        flushAndClear();

        // Act
        List<Competitor> competitors = competitorRepository.findAllWithHomeClubAndEmailAddresses();

        // Assert
        assertEquals(2, competitors.size());
        assertTrue(competitors.stream().allMatch(competitor ->
                Hibernate.isInitialized(competitor.getHomeClub())
                        && Hibernate.isInitialized(competitor.getEmailAddresses())));
    }

    // Competitor.emailAddresses
    @Test
    void testDelete_whenCompetitorHasEmailAddresses_thenTheirRowsAreDeletedWithIt() {
        // Arrange
        Competitor competitor = newCompetitor("Jane");
        competitor.setEmailAddresses(List.of("jane.doe@example.com"));
        Long competitorId = competitorRepository.save(competitor).getId();
        flushAndClear();

        // Act
        competitorRepository.delete(competitorRepository.findById(competitorId).orElseThrow());
        flushAndClear();

        // Assert
        assertEquals(0L, ((Number) entityManager.createNativeQuery("select count(*) from competitor_email")
                .getSingleResult()).longValue());
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

    private Competitor newCompetitor(String firstName) {
        Competitor competitor = new Competitor();
        competitor.setFirstName(firstName);
        competitor.setLastName("Doe");
        return competitor;
    }
}
