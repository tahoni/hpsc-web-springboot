package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.repositories.ClubRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link IpscEntityClubServiceImpl}'s implementation-specific behaviour - that it stops consulting
 * {@link ClubRepository} once the club is found. The interface's contract is covered by
 * {@link za.co.hpsc.web.services.IpscEntityClubServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
class IpscEntityClubServiceImplTest {
    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private IpscEntityClubServiceImpl ipscEntityClubServiceImpl;

    // findByCodeOrAbbreviation()
    @Test
    void testFindByCodeOrAbbreviation_whenAbbreviationMatches_thenReturnsClubWithoutSearchingFurther() {
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

    // Helpers
    private Club club(ClubIdentifier identifier) {
        Club club = new Club();
        club.setName(identifier.getName());
        club.setAbbreviation(identifier.getAbbreviation());
        club.setIdentifier(identifier);
        return club;
    }
}
