package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.repositories.CompetitorRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for how {@link EntityIpscCompetitorServiceImpl} drives its
 * {@link CompetitorRepository}: the normalised arguments it passes and the queries it skips. The
 * impl declares no helper methods beyond the interface, so the matching outcomes of
 * {@code findCompetitor} are covered by
 * {@link za.co.hpsc.web.services.EntityIpscCompetitorServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
public class EntityIpscCompetitorServiceImplTest {

    @Mock
    private CompetitorRepository competitorRepository;

    @InjectMocks
    private EntityIpscCompetitorServiceImpl entityIpscCompetitorService;

    // findCompetitor()
    @Test
    void testFindCompetitor_whenTheNumberIsNumeric_thenQueriesTheNumberAsAnInteger() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(42)).thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("42", "Jane Doe");

        // Assert
        verify(competitorRepository).findAllByCompetitorNumber(42);
    }

    @Test
    void testFindCompetitor_whenTheNameHasAnRoSuffix_thenQueriesTheNormalisedName() {
        // Arrange
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe"))
                .thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("42", "Jane Doe RO");

        // Assert
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe");
    }

    @Test
    void testFindCompetitor_whenTheNameHasABracketedRoSuffix_thenQueriesTheNormalisedName() {
        // Arrange
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe"))
                .thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("42", "  Jane Doe (RO)  ");

        // Assert
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe");
    }

    @Test
    void testFindCompetitor_whenTheNameHasRoAtTheStartOrInTheMiddle_thenQueriesTheNormalisedName() {
        // Arrange
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe"))
                .thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("42", "RO Jane Doe");
        entityIpscCompetitorService.findCompetitor("42", "Jane RO Doe");
        entityIpscCompetitorService.findCompetitor("42", "(RO) Jane Doe");
        entityIpscCompetitorService.findCompetitor("42", "Jane (RO) Doe");
        entityIpscCompetitorService.findCompetitor("42", "Jane(RO) Doe");

        // Assert - every position is removed and the spaces left behind are collapsed
        verify(competitorRepository, times(5))
                .findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe");
    }

    @Test
    void testFindCompetitor_whenTheNameHasBothRoMarkers_thenRemovesThemAll() {
        // Arrange
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe"))
                .thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("42", "RO Jane (RO) Doe RO");

        // Assert
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe");
    }

    @Test
    void testFindCompetitor_whenANameContainsRoInsideAWord_thenLeavesItAlone() {
        // Arrange
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(anyString()))
                .thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("42", "Romeo Doe");
        entityIpscCompetitorService.findCompetitor("42", "PEDRO Smith");
        entityIpscCompetitorService.findCompetitor("42", "Jane ROUX");

        // Assert - only a whole-word RO is a marker
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Romeo Doe");
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("PEDRO Smith");
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane ROUX");
    }

    @Test
    void testFindCompetitor_whenTheNameIsNull_thenQueriesAnEmptyName() {
        // Arrange
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(""))
                .thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("42", null);

        // Assert
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("");
    }

    @Test
    void testFindCompetitor_whenTheNumberIsAnExcludedAlias_thenSkipsTheNumberQuery() {
        // Arrange
        int excludedNumber = IpscConstants.EXCLUDE_ICS_ALIAS.getFirst();
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe"))
                .thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor(String.valueOf(excludedNumber), "Jane Doe");

        // Assert
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
        verify(competitorRepository).findAllByIdNumber(String.valueOf(excludedNumber));
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe");
    }

    @Test
    void testFindCompetitor_whenTheNumberIsNotNumeric_thenSkipsTheNumberQueryAndQueriesItAsAnIdNumber() {
        // Arrange
        when(competitorRepository.findAllByIdNumber("AB123456")).thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("AB123456", "Jane Doe");

        // Assert
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
        verify(competitorRepository).findAllByIdNumber("AB123456");
    }

    @Test
    void testFindCompetitor_whenTheNumberIsTooLongForAnInt_thenSkipsTheNumberQueryAndQueriesItAsAnIdNumber() {
        // Arrange
        when(competitorRepository.findAllByIdNumber("8001015009087")).thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("8001015009087", "Jane Doe");

        // Assert
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
        verify(competitorRepository).findAllByIdNumber("8001015009087");
    }

    @Test
    void testFindCompetitor_whenTheNumberHasSurroundingWhitespace_thenQueriesTheTrimmedNumber() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(42)).thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("  42  ", "Jane Doe");

        // Assert
        verify(competitorRepository).findAllByCompetitorNumber(42);
    }

    @Test
    void testFindCompetitor_whenTheNumberHasALeadingPlus_thenSkipsTheNumberQueryAndQueriesItAsAnIdNumber() {
        // Arrange
        when(competitorRepository.findAllByIdNumber("+42")).thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("+42", "Jane Doe");

        // Assert
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
        verify(competitorRepository).findAllByIdNumber("+42");
    }

    @Test
    void testFindCompetitor_whenTheNumberIsNegative_thenSkipsTheNumberQueryAndQueriesItAsAnIdNumber() {
        // Arrange
        when(competitorRepository.findAllByIdNumber("-5")).thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("-5", "Jane Doe");

        // Assert
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
        verify(competitorRepository).findAllByIdNumber("-5");
    }

    @Test
    void testFindCompetitor_whenTheNumberHasLettersAmongTheDigits_thenSkipsTheNumberQuery() {
        // Arrange
        when(competitorRepository.findAllByIdNumber("12a")).thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("12a", "Jane Doe");

        // Assert
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
    }

    @Test
    void testFindCompetitor_whenTheNumberIsZero_thenSkipsTheNumberQuery() {
        // Arrange
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe"))
                .thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("0", "Jane Doe");

        // Assert
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
    }

    @Test
    void testFindCompetitor_whenTheNumberIsNull_thenSkipsTheNumberQueryAndQueriesANullIdNumber() {
        // Arrange
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe"))
                .thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor(null, "Jane Doe");

        // Assert
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
        verify(competitorRepository).findAllByIdNumber(null);
    }

    @Test
    void testFindCompetitor_whenOneCompetitorHasTheNumber_thenSkipsTheIdNumberAndNameQueries() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(42)).thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("42", "Jane Doe");

        // Assert
        verify(competitorRepository, never()).findAllByIdNumber(anyString());
        verify(competitorRepository, never()).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(anyString());
    }

    @Test
    void testFindCompetitor_whenOneCompetitorHasTheIdNumber_thenSkipsTheNameQuery() {
        // Arrange
        when(competitorRepository.findAllByIdNumber("AB123456")).thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("AB123456", "Jane Doe");

        // Assert
        verify(competitorRepository, never()).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(anyString());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumber_thenNarrowsInMemoryWithoutTheNameQuery() {
        // Arrange
        Competitor jane = new Competitor();
        jane.setFirstName("Jane");
        jane.setLastName("Doe");
        Competitor john = new Competitor();
        john.setFirstName("John");
        john.setLastName("Doe");
        when(competitorRepository.findAllByCompetitorNumber(42)).thenReturn(List.of(jane, john));

        // Act
        entityIpscCompetitorService.findCompetitor("42", "Jane Doe");

        // Assert
        verify(competitorRepository, never()).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(anyString());
    }
}
