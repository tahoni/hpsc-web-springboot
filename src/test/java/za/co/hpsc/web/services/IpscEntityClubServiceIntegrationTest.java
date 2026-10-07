package za.co.hpsc.web.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the {@link IpscEntityClubService} contract against a real Spring context, which also checks the
 * service is registered as a bean.
 */
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
class IpscEntityClubServiceIntegrationTest {
    @Autowired
    private IpscEntityClubService ipscEntityClubService;

    // isSameClub(Club, ClubIdentifier)
    @Test
    void testIsSameClub_whenClubHasTargetIdentifier_thenReturnsTrue() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);

        // Act & Assert
        assertTrue(ipscEntityClubService.isSameClub(club, ClubIdentifier.HPSC));
    }

    @Test
    void testIsSameClub_whenClubHasDifferentIdentifier_thenReturnsFalse() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);

        // Act & Assert
        assertFalse(ipscEntityClubService.isSameClub(club, ClubIdentifier.SOSC));
    }

    @Test
    void testIsSameClub_whenClubHasNoIdentifier_thenReturnsFalse() {
        // Act & Assert
        assertFalse(ipscEntityClubService.isSameClub(new Club(), ClubIdentifier.HPSC));
    }

    @Test
    void testIsSameClub_whenClubIsNull_thenReturnsFalse() {
        // Act & Assert
        assertFalse(ipscEntityClubService.isSameClub((Club) null, ClubIdentifier.HPSC));
    }

    @Test
    void testIsSameClub_whenTargetIdentifierIsNull_thenReturnsFalse() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);

        // Act & Assert
        assertFalse(ipscEntityClubService.isSameClub(club, null));
    }

    @Test
    void testIsSameClub_whenClubAndTargetIdentifierAreBothNull_thenReturnsFalse() {
        // Act & Assert
        assertFalse(ipscEntityClubService.isSameClub((Club) null, null));
        assertFalse(ipscEntityClubService.isSameClub(new Club(), null));
    }

    // isSameClub(ClubIdentifier, ClubIdentifier)
    @Test
    void testIsSameClubIdentifier_whenIdentifiersMatch_thenReturnsTrue() {
        // Act & Assert
        assertTrue(ipscEntityClubService.isSameClub(ClubIdentifier.HPSC, ClubIdentifier.HPSC));
    }

    @Test
    void testIsSameClubIdentifier_whenIdentifiersDiffer_thenReturnsFalse() {
        // Act & Assert
        assertFalse(ipscEntityClubService.isSameClub(ClubIdentifier.HPSC, ClubIdentifier.SOSC));
    }

    @Test
    void testIsSameClubIdentifier_whenIdentifierIsNull_thenReturnsFalse() {
        // Act & Assert
        assertFalse(ipscEntityClubService.isSameClub((ClubIdentifier) null, ClubIdentifier.HPSC));
        assertFalse(ipscEntityClubService.isSameClub(ClubIdentifier.HPSC, (ClubIdentifier) null));
    }

    @Test
    void testIsSameClubIdentifier_whenBothIdentifiersAreNull_thenReturnsFalse() {
        // Act & Assert
        assertFalse(ipscEntityClubService.isSameClub((ClubIdentifier) null, (ClubIdentifier) null));
    }
}
