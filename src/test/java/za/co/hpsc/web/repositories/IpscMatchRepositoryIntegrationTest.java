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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link IpscMatchRepository}'s custom queries and
 * against the H2 {@code test} profile database. Each test flushes and clears the persistence context before
 * reading back, so it observes what was really written rather than cached entities.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class IpscMatchRepositoryIntegrationTest {

    @Autowired
    private IpscMatchRepository ipscMatchRepository;

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
}
