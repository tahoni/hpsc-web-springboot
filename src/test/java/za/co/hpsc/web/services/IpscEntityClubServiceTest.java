package za.co.hpsc.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.services.impl.IpscEntityClubServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link IpscEntityClubService} contract, exercised through the interface type. See
 * {@link IpscEntityClubServiceIntegrationTest} for the same contract against a real Spring context.
 */
@ExtendWith(MockitoExtension.class)
class IpscEntityClubServiceTest {
    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private IpscEntityClubServiceImpl ipscEntityClubServiceImpl;

    private IpscEntityClubService ipscEntityClubService;

    @BeforeEach
    void setUp() {
        ipscEntityClubService = ipscEntityClubServiceImpl;
    }

    // findByCodeOrAbbreviation()
    @Test
    void testFindByCodeOrAbbreviation_whenNullOrBlank_thenThrowsNonFatalExceptionWithoutQueryingTheRepository() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscEntityClubService.findByCodeOrAbbreviation(null));
        assertThrows(NonFatalException.class, () -> ipscEntityClubService.findByCodeOrAbbreviation("  "));
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testFindByCodeOrAbbreviation_whenAbbreviationMatches_thenReturnsClub() {
        // Arrange
        Club club = club(ClubIdentifier.HPSC);
        when(clubRepository.findByAbbreviation("HPSC")).thenReturn(Optional.of(club));

        // Act & Assert
        assertSame(club, ipscEntityClubService.findByCodeOrAbbreviation("HPSC"));
    }

    @Test
    void testFindByCodeOrAbbreviation_whenOnlyNameMatches_thenReturnsClub() {
        // Arrange
        Club club = club(ClubIdentifier.HPSC);
        String name = ClubIdentifier.HPSC.getName();
        when(clubRepository.findByAbbreviation(name)).thenReturn(Optional.empty());
        when(clubRepository.findByName(name)).thenReturn(Optional.of(club));

        // Act & Assert
        assertSame(club, ipscEntityClubService.findByCodeOrAbbreviation(name));
    }

    @Test
    void testFindByCodeOrAbbreviation_whenOnlyIdentifierCodeMatches_thenReturnsClubWithThatIdentifier() {
        // Arrange
        Club club = club(ClubIdentifier.HPSC);
        String code = ClubIdentifier.HPSC.getCode();
        when(clubRepository.findByAbbreviation(code)).thenReturn(Optional.empty());
        when(clubRepository.findByName(code)).thenReturn(Optional.empty());
        when(clubRepository.findByIdentifier(ClubIdentifier.HPSC)).thenReturn(Optional.of(club));

        // Act & Assert
        assertSame(club, ipscEntityClubService.findByCodeOrAbbreviation(code));
    }

    @Test
    void testFindByCodeOrAbbreviation_whenIdentifierMatchesButNoClubIsPersisted_thenThrowsNonFatalException() {
        // Arrange
        String code = ClubIdentifier.HPSC.getCode();
        when(clubRepository.findByAbbreviation(code)).thenReturn(Optional.empty());
        when(clubRepository.findByName(code)).thenReturn(Optional.empty());
        when(clubRepository.findByIdentifier(ClubIdentifier.HPSC)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscEntityClubService.findByCodeOrAbbreviation(code));
    }

    @Test
    void testFindByCodeOrAbbreviation_whenNothingMatches_thenThrowsValidationException() {
        // Arrange
        when(clubRepository.findByAbbreviation("Nope")).thenReturn(Optional.empty());
        when(clubRepository.findByName("Nope")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscEntityClubService.findByCodeOrAbbreviation("Nope"));
        verify(clubRepository, never()).findByIdentifier(any());
    }

    // findByCodeOrAbbreviationWithDefault()
    @Test
    void testFindByCodeOrAbbreviationWithDefault_whenNullOrBlank_thenReturnsClubOfDefaultIdentifier() {
        // Arrange
        Club club = club(ClubIdentifier.HPSC);
        when(clubRepository.findByIdentifier(ClubIdentifier.HPSC)).thenReturn(Optional.of(club));

        // Act & Assert
        assertSame(club, ipscEntityClubService.findByCodeOrAbbreviationWithDefault(null, ClubIdentifier.HPSC));
        assertSame(club, ipscEntityClubService.findByCodeOrAbbreviationWithDefault("  ", ClubIdentifier.HPSC));
        verify(clubRepository, never()).findByAbbreviation(any());
    }

    @Test
    void testFindByCodeOrAbbreviationWithDefault_whenBlankAndDefaultClubIsNotPersisted_thenThrowsNonFatalException() {
        // Arrange
        when(clubRepository.findByIdentifier(ClubIdentifier.HPSC)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscEntityClubService.findByCodeOrAbbreviationWithDefault("", ClubIdentifier.HPSC));
    }

    @Test
    void testFindByCodeOrAbbreviationWithDefault_whenBlankAndDefaultIsNull_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscEntityClubService.findByCodeOrAbbreviationWithDefault(null, null));
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testFindByCodeOrAbbreviationWithDefault_whenClubCodeIsGiven_thenIgnoresTheDefault() {
        // Arrange
        Club club = club(ClubIdentifier.SOSC);
        when(clubRepository.findByAbbreviation("SOSC")).thenReturn(Optional.of(club));

        // Act & Assert
        assertSame(club, ipscEntityClubService.findByCodeOrAbbreviationWithDefault("SOSC", ClubIdentifier.HPSC));
        verify(clubRepository, never()).findByIdentifier(any());
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
    private Club club(ClubIdentifier identifier) {
        Club club = new Club();
        club.setName(identifier.getName());
        club.setAbbreviation(identifier.getAbbreviation());
        club.setIdentifier(identifier);
        return club;
    }
}
