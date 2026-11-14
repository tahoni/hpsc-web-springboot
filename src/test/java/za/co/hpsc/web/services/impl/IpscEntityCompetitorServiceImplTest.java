package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.IpscEntityCompetitorServiceTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for how {@link IpscEntityCompetitorServiceImpl} drives its
 * {@link CompetitorRepository}: the normalised arguments it passes and the queries it skips. The
 * impl declares no helper methods beyond the interface, so the matching outcomes of
 * {@code findCompetitor} are covered by
 * {@link IpscEntityCompetitorServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
public class IpscEntityCompetitorServiceImplTest {

    @Mock
    private CompetitorRepository competitorRepository;

    @InjectMocks
    private IpscEntityCompetitorServiceImpl entityIpscCompetitorService;

    // findCompetitor()
    @Test
    void testFindCompetitor_whenTheNumberMatchesAClubNumber_thenReturnsItWithoutAnyOtherQuery() {
        // Arrange
        Competitor competitor = new Competitor();
        when(competitorRepository.findByClubNumber("042")).thenReturn(Optional.of(competitor));

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitorByIdentifierAndFullName(" 042 ", "Jane Doe");

        // Assert
        assertEquals(Optional.of(competitor), result);
        verify(competitorRepository).findByClubNumber("042");
        verifyNoMoreInteractions(competitorRepository);
    }

    @Test
    void testFindCompetitor_whenANameContainsRoInsideAWord_thenLeavesItAlone() {
        // Arrange
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(anyString()))
                .thenReturn(List.of(new Competitor()));

        // Act
        entityIpscCompetitorService.findCompetitorByIdentifierAndFullName("42", "Romeo Doe");
        entityIpscCompetitorService.findCompetitorByIdentifierAndFullName("42", "PEDRO Smith");
        entityIpscCompetitorService.findCompetitorByIdentifierAndFullName("42", "Jane ROUX");

        // Assert - only a whole-word RO is a marker
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Romeo Doe");
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("PEDRO Smith");
        verify(competitorRepository).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane ROUX");
    }
}
