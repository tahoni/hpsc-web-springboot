package za.co.hpsc.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.impl.IpscEntityCompetitorServiceImpl;
import za.co.hpsc.web.services.impl.IpscEntityCompetitorServiceImplTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link IpscEntityCompetitorService} contract, exercised entirely through
 * the interface type with the competitor repository mocked. Covers {@code findCompetitor} - the
 * interface's only declared method. How the impl calls the repository is covered by
 * {@link IpscEntityCompetitorServiceImplTest}.
 */
@ExtendWith(MockitoExtension.class)
public class IpscEntityCompetitorServiceTest {

    @Mock
    private CompetitorRepository competitorRepository;

    @InjectMocks
    private IpscEntityCompetitorServiceImpl ipscEntityCompetitorServiceImpl;

    private IpscEntityCompetitorService ipscEntityCompetitorService;

    @BeforeEach
    void setUp() {
        ipscEntityCompetitorService = ipscEntityCompetitorServiceImpl;
    }

    // findCompetitor()
    @Test
    void testFindCompetitor_whenOneCompetitorHasTheNumber_thenReturnsItWithoutMatchingTheName() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Someone Else");

        // Assert
        assertTrue(result.isPresent());
        assertSame(competitor, result.get());
        verify(competitorRepository, never()).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(anyString());
    }

    @Test
    void testFindCompetitor_whenOneCompetitorHasTheNumberAndTheNameIsNull_thenReturnsIt() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", null);

        // Assert
        assertTrue(result.isPresent());
        assertSame(competitor, result.get());
    }

    @Test
    void testFindCompetitor_whenOneCompetitorHasTheIdNumber_thenReturnsItWithoutMatchingTheName() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByIdNumber("AB123456")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("AB123456", "Someone Else");

        // Assert
        assertTrue(result.isPresent());
        assertSame(competitor, result.get());
        verify(competitorRepository, never()).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(anyString());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberAndOneHasTheName_thenReturnsTheNameMatch() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of());
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Jane Doe");

        // Assert
        assertTrue(result.isPresent());
        assertSame(competitor, result.get());
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberOrTheName_thenThrowsNonFatalException() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of());
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of());

        // Act & Assert
        NonFatalException exception = assertThrows(NonFatalException.class,
                () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Jane Doe"));
        assertTrue(exception.getMessage().startsWith("No competitors"));
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheNumberAndSeveralHaveTheName_thenThrowsValidationException() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of());
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe"))
                .thenReturn(List.of(competitor("Jane", "Doe", null), competitor("Janet", "Doe", "Jane")));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Jane Doe"));
        assertTrue(exception.getMessage().startsWith("Two or more"));
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndOneHasTheFirstName_thenReturnsThatCompetitor() {
        // Arrange
        Competitor jane = competitor("Jane", "Doe", null);
        Competitor john = competitor("John", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of(jane, john));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Jane Doe");

        // Assert
        assertTrue(result.isPresent());
        assertSame(jane, result.get());
        verify(competitorRepository, never()).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(anyString());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndOneHasTheNickname_thenReturnsThatCompetitor() {
        // Arrange
        Competitor janet = competitor("Janet", "Doe", "Jane");
        Competitor john = competitor("John", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of(janet, john));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Jane Doe");

        // Assert
        assertTrue(result.isPresent());
        assertSame(janet, result.get());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndTheNameDiffersInCase_thenReturnsThatCompetitor() {
        // Arrange
        Competitor jane = competitor("Jane", "Doe", null);
        Competitor john = competitor("John", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of(jane, john));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "jANE dOE");

        // Assert
        assertTrue(result.isPresent());
        assertSame(jane, result.get());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndNoneHasTheName_thenThrowsValidationException() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(1234))
                .thenReturn(List.of(competitor("John", "Doe", null), competitor("Jack", "Doe", null)));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Jane Doe"));
        assertTrue(exception.getMessage().startsWith("Two or more"));
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndSeveralHaveTheName_thenThrowsValidationException() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(1234))
                .thenReturn(List.of(competitor("Jane", "Doe", null), competitor("Janet", "Doe", "Jane")));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Jane Doe"));
        assertTrue(exception.getMessage().startsWith("Two or more"));
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheIdNumberAndOneHasTheName_thenReturnsThatCompetitor() {
        // Arrange
        Competitor jane = competitor("Jane", "Doe", null);
        Competitor john = competitor("John", "Doe", null);
        when(competitorRepository.findAllByIdNumber("AB123456")).thenReturn(List.of(jane, john));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("AB123456", "Jane Doe");

        // Assert
        assertSame(jane, result.orElseThrow());
        verify(competitorRepository, never()).findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(anyString());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheIdNumberAndNoneHasTheName_thenThrowsValidationException() {
        // Arrange
        when(competitorRepository.findAllByIdNumber("AB123456"))
                .thenReturn(List.of(competitor("John", "Doe", null), competitor("Jack", "Doe", null)));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("AB123456", "Jane Doe"));
        assertTrue(exception.getMessage().startsWith("Two or more"));
    }

    @Test
    void testFindCompetitor_whenNoCompetitorHasTheIdNumberAndOneHasTheName_thenReturnsTheNameMatch() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("AB123456", "Jane Doe");

        // Assert
        assertSame(competitor, result.orElseThrow());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndSeveralTheIdNumber_thenNarrowsTheCombinedMatchesByName() {
        // Arrange
        Competitor jane = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber(1234))
                .thenReturn(List.of(jane, competitor("John", "Doe", null)));
        when(competitorRepository.findAllByIdNumber("1234"))
                .thenReturn(List.of(competitor("Jack", "Doe", null), competitor("Jill", "Doe", null)));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Jane Doe");

        // Assert
        assertSame(jane, result.orElseThrow());
    }

    @Test
    void testFindCompetitor_whenTheNumberIsAnExcludedAlias_thenMatchesByNameOnly() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("15000", "Jane Doe");

        // Assert
        assertTrue(result.isPresent());
        assertSame(competitor, result.get());
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
    }

    @Test
    void testFindCompetitor_whenTheNumberIsNotNumeric_thenMatchesByIdNumberThenName() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("ABC", "Jane Doe");

        // Assert
        assertSame(competitor, result.orElseThrow());
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
    }

    @Test
    void testFindCompetitor_whenTheNumberHasSurroundingWhitespace_thenMatchesTheNumber() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName(" 1234 ", "Someone Else");

        // Assert
        assertSame(competitor, result.orElseThrow());
    }

    @Test
    void testFindCompetitor_whenTheNumberIsNegative_thenMatchesByIdNumberThenName() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("-5", "Jane Doe");

        // Assert
        assertSame(competitor, result.orElseThrow());
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
    }

    @Test
    void testFindCompetitor_whenTheNumberIsZero_thenMatchesByNameOnly() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("0", "Jane Doe");

        // Assert
        assertSame(competitor, result.orElseThrow());
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
    }

    @Test
    void testFindCompetitor_whenTheNumberIsNull_thenMatchesByName() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName(null, "Jane Doe");

        // Assert
        assertSame(competitor, result.orElseThrow());
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
    }

    @Test
    void testFindCompetitor_whenTheNumberIsBlank_thenMatchesByName() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("  ", "Jane Doe");

        // Assert
        assertSame(competitor, result.orElseThrow());
        verify(competitorRepository, never()).findAllByCompetitorNumber(any());
    }

    @Test
    void testFindCompetitor_whenTheNameHasAnRoSuffix_thenMatchesTheNameWithoutIt() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of());
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe")).thenReturn(List.of(competitor));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Jane Doe RO");

        // Assert
        assertTrue(result.isPresent());
        assertSame(competitor, result.get());
    }

    @Test
    void testFindCompetitor_whenTheNameHasRoInTheMiddleOrAtTheStart_thenMatchesTheNameWithoutIt() {
        // Arrange
        Competitor competitor = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of());
        when(competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase("Jane Doe"))
                .thenReturn(List.of(competitor));

        // Act & Assert
        assertSame(competitor, ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Jane RO Doe").orElseThrow());
        assertSame(competitor, ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "(RO) Jane Doe").orElseThrow());
    }

    @Test
    void testFindCompetitor_whenSeveralHaveTheNumberAndTheNameHasAnRoSuffix_thenNarrowsByTheNameWithoutIt() {
        // Arrange
        Competitor jane = competitor("Jane", "Doe", null);
        when(competitorRepository.findAllByCompetitorNumber(1234))
                .thenReturn(List.of(jane, competitor("John", "Doe", null)));

        // Act
        Optional<Competitor> result = ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", "Jane Doe (RO)");

        // Assert
        assertSame(jane, result.orElseThrow());
    }

    @Test
    void testFindCompetitor_whenTheNameIsNullAndNoCompetitorHasTheNumber_thenThrowsNonFatalException() {
        // Arrange
        when(competitorRepository.findAllByCompetitorNumber(1234)).thenReturn(List.of());

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("1234", null));
    }

    @Test
    void testFindCompetitor_whenTheNameAndNumberAreNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName(null, null));
        verifyNoInteractions(competitorRepository);
    }

    @Test
    void testFindCompetitor_whenTheNameAndNumberAreBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName("", " "));
        verifyNoInteractions(competitorRepository);
    }

    @Test
    void testFindCompetitor_whenTheNameIsNullAndTheNumberIsBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName(" ", null));
        verifyNoInteractions(competitorRepository);
    }

    @Test
    void testFindCompetitor_whenTheNameIsBlankAndTheNumberIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscEntityCompetitorService.findCompetitorByIdentifierAndFullName(null, ""));
        verifyNoInteractions(competitorRepository);
    }

    // Helpers
    private Competitor competitor(String firstName, String lastName, String nickname) {
        Competitor competitor = new Competitor();
        competitor.setFirstName(firstName);
        competitor.setLastName(lastName);
        competitor.setNickName(nickname);
        return competitor;
    }
}
