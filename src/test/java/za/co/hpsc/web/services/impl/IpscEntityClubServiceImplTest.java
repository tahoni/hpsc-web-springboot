package za.co.hpsc.web.services.impl;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link IpscEntityClubServiceImpl}'s implementation-specific behaviour - the order in which it
 * consults {@link ClubRepository} and the fact that it makes no repository call where none is needed. The
 * interface's contract is covered by {@link za.co.hpsc.web.services.IpscEntityClubServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
class IpscEntityClubServiceImplTest {
    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private IpscEntityClubServiceImpl ipscEntityClubServiceImpl;

    // findByCodeOrAbbreviation()
    @Test
    void testFindByCodeOrAbbreviation_whenAbbreviationMatches_thenReturnsClubWithoutSearchingFurther()
            throws FatalException {
        // Arrange
        Club club = club(ClubIdentifier.HPSC);
        when(clubRepository.findByAbbreviation("HPSC")).thenReturn(Optional.of(club));

        // Act
        Club result = ipscEntityClubServiceImpl.findByCodeOrAbbreviation("HPSC");

        // Assert
        assertSame(club, result);
        verify(clubRepository, never()).findByName("HPSC");
        verify(clubRepository, never()).findByIdentifier(ClubIdentifier.HPSC);
    }

    @Test
    void testFindByCodeOrAbbreviation_whenNameMatches_thenDoesNotSearchByIdentifier() throws FatalException {
        // Arrange
        Club club = club(ClubIdentifier.HPSC);
        String name = ClubIdentifier.HPSC.getName();
        when(clubRepository.findByAbbreviation(name)).thenReturn(Optional.empty());
        when(clubRepository.findByName(name)).thenReturn(Optional.of(club));

        // Act
        Club result = ipscEntityClubServiceImpl.findByCodeOrAbbreviation(name);

        // Assert
        assertSame(club, result);
        verify(clubRepository, never()).findByIdentifier(ClubIdentifier.HPSC);
    }

    @Test
    void testFindByCodeOrAbbreviation_whenClubIdentifierAbbreviationMatchesButNotAClubAbbreviation_thenSearchesByIdentifier()
            throws FatalException {
        // Arrange
        Club club = club(ClubIdentifier.SOSC);
        when(clubRepository.findByAbbreviation("SOSC")).thenReturn(Optional.empty());
        when(clubRepository.findByName("SOSC")).thenReturn(Optional.empty());
        when(clubRepository.findByIdentifier(ClubIdentifier.SOSC)).thenReturn(Optional.of(club));

        // Act
        Club result = ipscEntityClubServiceImpl.findByCodeOrAbbreviation("SOSC");

        // Assert
        assertSame(club, result);
    }

    @Test
    void testFindByCodeOrAbbreviation_whenIdentifierIsKnownButNoClubIsPersisted_thenThrowsNonFatalException() {
        // Arrange
        String code = ClubIdentifier.HPSC.getCode();
        when(clubRepository.findByAbbreviation(code)).thenReturn(Optional.empty());
        when(clubRepository.findByName(code)).thenReturn(Optional.empty());
        when(clubRepository.findByIdentifier(ClubIdentifier.HPSC)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscEntityClubServiceImpl.findByCodeOrAbbreviation(code));
    }

    @Test
    void testFindByCodeOrAbbreviation_whenNothingMatches_thenThrowsValidationExceptionWithoutSearchingByIdentifier() {
        // Arrange
        when(clubRepository.findByAbbreviation("Nope")).thenReturn(Optional.empty());
        when(clubRepository.findByName("Nope")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscEntityClubServiceImpl.findByCodeOrAbbreviation("Nope"));
        verify(clubRepository, never()).findByIdentifier(any());
    }

    @Test
    void testFindByCodeOrAbbreviation_whenClubCodeIsBlank_thenThrowsNonFatalExceptionBecauseThereIsNoDefault() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscEntityClubServiceImpl.findByCodeOrAbbreviation(" "));
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testFindByCodeOrAbbreviation_whenOnlyIdentifierCodeMatches_thenSearchesByIdentifierLast()
            throws FatalException {
        // Arrange
        Club club = club(ClubIdentifier.HPSC);
        String code = ClubIdentifier.HPSC.getCode();
        when(clubRepository.findByAbbreviation(code)).thenReturn(Optional.empty());
        when(clubRepository.findByName(code)).thenReturn(Optional.empty());
        when(clubRepository.findByIdentifier(ClubIdentifier.HPSC)).thenReturn(Optional.of(club));

        // Act
        Club result = ipscEntityClubServiceImpl.findByCodeOrAbbreviation(code);

        // Assert
        assertSame(club, result);
    }

    // findByCodeOrAbbreviationWithDefault()
    @Test
    void testFindByCodeOrAbbreviationWithDefault_whenAbbreviationMatches_thenDoesNotSearchByNameOrIdentifier()
            throws FatalException {
        // Arrange
        Club club = club(ClubIdentifier.HPSC);
        when(clubRepository.findByAbbreviation("HPSC")).thenReturn(Optional.of(club));

        // Act
        Club result = ipscEntityClubServiceImpl.findByCodeOrAbbreviationWithDefault("HPSC", null);

        // Assert
        assertSame(club, result);
        verify(clubRepository, never()).findByName("HPSC");
        verify(clubRepository, never()).findByIdentifier(ClubIdentifier.HPSC);
    }

    @Test
    void testFindByCodeOrAbbreviationWithDefault_whenNameMatches_thenDoesNotSearchByIdentifier()
            throws FatalException {
        // Arrange
        Club club = club(ClubIdentifier.HPSC);
        String name = ClubIdentifier.HPSC.getName();
        when(clubRepository.findByAbbreviation(name)).thenReturn(Optional.empty());
        when(clubRepository.findByName(name)).thenReturn(Optional.of(club));

        // Act
        Club result = ipscEntityClubServiceImpl.findByCodeOrAbbreviationWithDefault(name, null);

        // Assert
        assertSame(club, result);
        verify(clubRepository, never()).findByIdentifier(ClubIdentifier.HPSC);
    }

    @Test
    void testFindByCodeOrAbbreviationWithDefault_whenClubIdentifierAbbreviationMatchesButNotAClubAbbreviation_thenSearchesByIdentifier()
            throws FatalException {
        // Arrange
        Club club = club(ClubIdentifier.SOSC);
        when(clubRepository.findByAbbreviation("SOSC")).thenReturn(Optional.empty());
        when(clubRepository.findByName("SOSC")).thenReturn(Optional.empty());
        when(clubRepository.findByIdentifier(ClubIdentifier.SOSC)).thenReturn(Optional.of(club));

        // Act
        Club result = ipscEntityClubServiceImpl.findByCodeOrAbbreviationWithDefault("SOSC", null);

        // Assert
        assertSame(club, result);
    }

    @Test
    void testFindByCodeOrAbbreviationWithDefault_whenClubCodeIsBlank_thenSearchesOnlyByTheDefaultIdentifier()
            throws FatalException {
        // Arrange
        Club club = club(ClubIdentifier.HPSC);
        when(clubRepository.findByIdentifier(ClubIdentifier.HPSC)).thenReturn(Optional.of(club));

        // Act
        Club result = ipscEntityClubServiceImpl.findByCodeOrAbbreviationWithDefault(" ", ClubIdentifier.HPSC);

        // Assert
        assertSame(club, result);
        verify(clubRepository, never()).findByAbbreviation(" ");
        verify(clubRepository, never()).findByName(" ");
    }

    // isSameClub(Club, ClubIdentifier)
    @Test
    void testIsSameClub_whenComparingAClub_thenDoesNotAccessTheRepository() {
        // Act
        boolean result = ipscEntityClubServiceImpl.isSameClub(club(ClubIdentifier.HPSC), ClubIdentifier.HPSC);

        // Assert
        assertTrue(result);
        verifyNoInteractions(clubRepository);
    }

    // isSameClub(ClubIdentifier, ClubIdentifier)
    @Test
    void testIsSameClubIdentifier_whenComparingIdentifiers_thenDoesNotAccessTheRepository() {
        // Act
        boolean result = ipscEntityClubServiceImpl.isSameClub(ClubIdentifier.HPSC, ClubIdentifier.HPSC);

        // Assert
        assertTrue(result);
        verifyNoInteractions(clubRepository);
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
