package za.co.hpsc.web.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.repositories.ClubRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the {@link IpscEntityClubService} contract against a real Spring context, which also checks the
 * service is registered as a bean.
 */
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
@Transactional
class IpscEntityClubServiceIntegrationTest {
    @Autowired
    private IpscEntityClubService ipscEntityClubService;

    @Autowired
    private ClubRepository clubRepository;

    // findByCodeOrAbbreviation()
    @Test
    void testFindByCodeOrAbbreviation_whenNullOrBlank_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscEntityClubService.findByCodeOrAbbreviation(null));
        assertThrows(NonFatalException.class, () -> ipscEntityClubService.findByCodeOrAbbreviation("  "));
    }

    @Test
    void testFindByCodeOrAbbreviation_whenAbbreviationMatches_thenReturnsClub() throws FatalException {
        // Arrange
        Club club = persistedClub(ClubIdentifier.HPSC);
        club.setAbbreviation("HPSC-TEST");
        clubRepository.saveAndFlush(club);

        // Act & Assert
        assertEquals(club.getId(), ipscEntityClubService.findByCodeOrAbbreviation("HPSC-TEST").getId());
    }

    @Test
    void testFindByCodeOrAbbreviation_whenNameMatches_thenReturnsClub() throws FatalException {
        // Arrange
        Club club = persistedClub(ClubIdentifier.HPSC);

        // Act & Assert
        assertEquals(club.getId(), ipscEntityClubService.findByCodeOrAbbreviation(club.getName()).getId());
    }

    @Test
    void testFindByCodeOrAbbreviation_whenOnlyIdentifierCodeMatches_thenReturnsClubWithThatIdentifier() throws FatalException {
        // Arrange
        Club club = persistedClub(ClubIdentifier.HPSC);

        // Act & Assert
        assertEquals(club.getId(),
                ipscEntityClubService.findByCodeOrAbbreviation(ClubIdentifier.HPSC.getCode()).getId());
    }

    @Test
    void testFindByCodeOrAbbreviation_whenNothingMatches_thenThrowsValidationException() throws FatalException {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscEntityClubService.findByCodeOrAbbreviation("Nope"));
    }

    // findByCodeOrAbbreviationWithDefault()
    @Test
    void testFindByCodeOrAbbreviationWithDefault_whenBlank_thenReturnsClubOfDefaultIdentifier() throws FatalException {
        // Arrange
        Club club = persistedClub(ClubIdentifier.HPSC);

        // Act & Assert
        assertEquals(club.getId(),
                ipscEntityClubService.findByCodeOrAbbreviationWithDefault(null, ClubIdentifier.HPSC).getId());
    }

    @Test
    void testFindByCodeOrAbbreviationWithDefault_whenBlankAndDefaultIsNull_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscEntityClubService.findByCodeOrAbbreviationWithDefault("", null));
    }

    @Test
    void testFindByCodeOrAbbreviationWithDefault_whenClubCodeIsGiven_thenIgnoresTheDefault() throws FatalException {
        // Arrange
        Club club = persistedClub(ClubIdentifier.HPSC);

        // Act & Assert
        assertEquals(club.getId(), ipscEntityClubService
                .findByCodeOrAbbreviationWithDefault(club.getName(), ClubIdentifier.SOSC).getId());
    }

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

    // Helpers
    private Club persistedClub(ClubIdentifier identifier) {
        return clubRepository.findByIdentifier(identifier).orElseGet(() -> {
            Club club = new Club();
            club.setName(identifier.getName());
            club.setIdentifier(identifier);
            return clubRepository.saveAndFlush(club);
        });
    }
}
