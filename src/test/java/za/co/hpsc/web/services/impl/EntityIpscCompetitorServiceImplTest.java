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

import static org.junit.jupiter.api.Assertions.*;
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
    void testFindCompetitor_whenCalled_thenQueriesTheNumberAsAString() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(42)).thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("Jane Doe", 42);

        // Assert
        verify(competitorRepository).findAllByCompetitorNumber(42);
    }

    @Test
    void testFindCompetitor_whenTheNameHasAnRoSuffix_thenQueriesTheNormalisedName() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(42)).thenReturn(List.of());

        // Act
        entityIpscCompetitorService.findCompetitor("Jane Doe RO", 42);

        // Assert
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe");
    }

    @Test
    void testFindCompetitor_whenTheNameHasABracketedRoSuffix_thenQueriesTheNormalisedName() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(42)).thenReturn(List.of());

        // Act
        entityIpscCompetitorService.findCompetitor("  Jane Doe (RO)  ", 42);

        // Assert
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe");
    }

    @Test
    void testFindCompetitor_whenTheNumberIsAnExcludedAlias_thenSkipsTheNumberQuery() {
        // Arrange
        int excludedNumber = IpscConstants.EXCLUDE_ICS_ALIAS.getFirst();
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of());

        // Act
        entityIpscCompetitorService.findCompetitor("Jane Doe", excludedNumber);

        // Assert
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe");
    }

    @Test
    void testFindCompetitor_whenOneCompetitorHasTheNumber_thenSkipsTheNameQuery() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(42)).thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitor("Jane Doe", 42);

        // Assert
        verify(competitorRepository, never()).findAllByFullNameIgnoreCase(anyString());
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
        entityIpscCompetitorService.findCompetitor("Jane Doe", 42);

        // Assert
        verify(competitorRepository, never()).findAllByFullNameIgnoreCase(anyString());
    }
}
