package za.co.hpsc.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.impl.EntityIpscCompetitorServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link EntityIpscCompetitorService} contract, exercised entirely through
 * the interface type with the competitor repository mocked. Covers {@code findCompetitor} - the
 * interface's only declared method. How the impl calls the repository is covered by
 * {@link za.co.hpsc.web.services.impl.EntityIpscCompetitorServiceImplTest}.
 */
@ExtendWith(MockitoExtension.class)
public class EntityIpscCompetitorServiceTest {

    @Mock
    private CompetitorRepository competitorRepository;

    @InjectMocks
    private EntityIpscCompetitorServiceImpl entityIpscCompetitorServiceImpl;

    private EntityIpscCompetitorService entityIpscCompetitorService;

    @BeforeEach
    void setUp() {
        entityIpscCompetitorService = entityIpscCompetitorServiceImpl;
    }

    // findCompetitor()
    @Test
    void testFindCompetitor_whenOneCompetitorHasTheNumber_thenReturnsItWithoutMatchingTheName() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber("1234")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Someone Else", 1234);

        // Assert
        assertTrue(result.isPresent());
        assertSame(competitor, result.get());
        verify(competitorRepository, never()).findAllByFullNameIgnoreCase(anyString());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberAndOneHasTheName_thenReturnsTheNameMatch() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber("1234")).thenReturn(List.of());
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 1234);

        // Assert
        assertTrue(result.isPresent());
        assertSame(competitor, result.get());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberOrTheName_thenReturnsEmpty() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber("1234")).thenReturn(List.of());
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of());

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 1234);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberAndSeveralHaveTheName_thenReturnsEmpty() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber("1234")).thenReturn(List.of());
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe"))
                .thenReturn(List.of(competitor("Jane", "Doe", null), competitor("Janet", "Doe", "Jane")));

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 1234);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndOneHasTheFirstName_thenReturnsThatCompetitor() {
        // Arrange
        Competitor jane = competitor("Jane", "Doe", null);
        Competitor john = competitor("John", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber("1234")).thenReturn(List.of(jane, john));

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 1234);

        // Assert
        assertTrue(result.isPresent());
        assertSame(jane, result.get());
        verify(competitorRepository, never()).findAllByFullNameIgnoreCase(anyString());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndOneHasTheNickname_thenReturnsThatCompetitor() {
        // Arrange
        Competitor janet = competitor("Janet", "Doe", "Jane");
        Competitor john = competitor("John", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber("1234")).thenReturn(List.of(janet, john));

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 1234);

        // Assert
        assertTrue(result.isPresent());
        assertSame(janet, result.get());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndTheNameDiffersInCase_thenReturnsThatCompetitor() {
        // Arrange
        Competitor jane = competitor("Jane", "Doe", null);
        Competitor john = competitor("John", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber("1234")).thenReturn(List.of(jane, john));

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("jANE dOE", 1234);

        // Assert
        assertTrue(result.isPresent());
        assertSame(jane, result.get());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndNoneHasTheName_thenReturnsEmpty() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber("1234"))
                .thenReturn(List.of(competitor("John", "Doe", null), competitor("Jack", "Doe", null)));

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 1234);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndSeveralHaveTheName_thenReturnsEmpty() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber("1234"))
                .thenReturn(List.of(competitor("Jane", "Doe", null), competitor("Janet", "Doe", "Jane")));

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 1234);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindCompetitor_whenTheNumberIsAnExcludedAlias_thenMatchesByNameOnly() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe", 15000);

        // Assert
        assertTrue(result.isPresent());
        assertSame(competitor, result.get());
        verify(competitorRepository, never()).findAllByCompetitorNumber(anyString());
    }

    @Test
    void testFindCompetitor_whenTheNameHasAnRoSuffix_thenMatchesTheNameWithoutIt() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber("1234")).thenReturn(List.of());
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = entityIpscCompetitorService.findCompetitor("Jane Doe RO", 1234);

        // Assert
        assertTrue(result.isPresent());
        assertSame(competitor, result.get());
    }

    // Helpers
    private Competitor competitor(String firstName, String lastName, String nickname) {
        Competitor competitor = new Competitor();
        competitor.setFirstName(firstName);
        competitor.setLastName(lastName);
        competitor.setNickname(nickname);
        return competitor;
    }
}
