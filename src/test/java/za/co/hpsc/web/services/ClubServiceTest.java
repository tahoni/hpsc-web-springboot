package za.co.hpsc.web.services;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.services.impl.ClubServiceImpl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link ClubService} contract, exercised through the interface type. See
 * {@link ClubServiceIntegrationTest} for the same contract against a real Spring context.
 */
class ClubServiceTest {
    private final ClubService clubService = new ClubServiceImpl();

    // isSameClub(Club, ClubIdentifier)
    @Test
    void testIsSameClub_whenClubHasTargetIdentifier_thenReturnsTrue() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);

        // Act & Assert
        assertTrue(clubService.isSameClub(club, ClubIdentifier.HPSC));
    }

    @Test
    void testIsSameClub_whenClubHasDifferentIdentifier_thenReturnsFalse() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);

        // Act & Assert
        assertFalse(clubService.isSameClub(club, ClubIdentifier.SOSC));
    }

    @Test
    void testIsSameClub_whenClubHasNoIdentifier_thenReturnsFalse() {
        // Act & Assert
        assertFalse(clubService.isSameClub(new Club(), ClubIdentifier.HPSC));
    }

    @Test
    void testIsSameClub_whenClubIsNull_thenReturnsFalse() {
        // Act & Assert
        assertFalse(clubService.isSameClub((Club) null, ClubIdentifier.HPSC));
    }

    @Test
    void testIsSameClub_whenTargetIdentifierIsNull_thenReturnsFalse() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);

        // Act & Assert
        assertFalse(clubService.isSameClub(club, null));
    }

    @Test
    void testIsSameClub_whenClubAndTargetIdentifierAreBothNull_thenReturnsFalse() {
        // Act & Assert
        assertFalse(clubService.isSameClub((Club) null, null));
        assertFalse(clubService.isSameClub(new Club(), null));
    }

    // isSameClub(ClubIdentifier, ClubIdentifier)
    @Test
    void testIsSameClubIdentifier_whenIdentifiersMatch_thenReturnsTrue() {
        // Act & Assert
        assertTrue(clubService.isSameClub(ClubIdentifier.HPSC, ClubIdentifier.HPSC));
    }

    @Test
    void testIsSameClubIdentifier_whenIdentifiersDiffer_thenReturnsFalse() {
        // Act & Assert
        assertFalse(clubService.isSameClub(ClubIdentifier.HPSC, ClubIdentifier.SOSC));
    }

    @Test
    void testIsSameClubIdentifier_whenIdentifierIsNull_thenReturnsFalse() {
        // Act & Assert
        assertFalse(clubService.isSameClub((ClubIdentifier) null, ClubIdentifier.HPSC));
        assertFalse(clubService.isSameClub(ClubIdentifier.HPSC, (ClubIdentifier) null));
    }

    @Test
    void testIsSameClubIdentifier_whenBothIdentifiersAreNull_thenReturnsFalse() {
        // Act & Assert
        assertFalse(clubService.isSameClub((ClubIdentifier) null, (ClubIdentifier) null));
    }
}
