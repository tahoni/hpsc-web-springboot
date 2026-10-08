package za.co.hpsc.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.mappers.MatchCompetitorMapper;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.request.MatchCompetitorRequest;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorResponse;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorBulkResponse;
import za.co.hpsc.web.models.ipsc.matchcompetitor.response.MatchCompetitorBulkResponseHolder;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.impl.IpscEntityClubServiceImpl;
import za.co.hpsc.web.services.impl.IpscMatchCompetitorServiceImpl;
import za.co.hpsc.web.services.impl.TransactionServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link IpscMatchCompetitorService} contract, exercised entirely through the interface type, with
 * the repositories mocked, and a real {@link TransactionServiceImpl} committing through those mocks under a mocked
 * {@link PlatformTransactionManager}.
 */
@ExtendWith(MockitoExtension.class)
public class IpscMatchCompetitorServiceTest {

    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private IpscEntityCompetitorService ipscEntityCompetitorService;

    @Mock
    private PlatformTransactionManager transactionManager;

    private IpscMatchCompetitorService ipscMatchCompetitorService;

    @BeforeEach
    void setUp() {
        TransactionService transactionService = new TransactionServiceImpl(competitorRepository,
                ipscMatchRepository, matchCompetitorRepository, transactionManager);
        ipscMatchCompetitorService = new IpscMatchCompetitorServiceImpl(matchCompetitorRepository,
                new MatchCompetitorMapper(competitorRepository, ipscMatchRepository, ipscEntityCompetitorService),
                new IpscEntityClubServiceImpl(mock(ClubRepository.class)), transactionService);
    }

    // createMatchCompetitor()
    @Test
    void testCreateMatchCompetitor_whenRequestIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(null));
    }

    @Test
    void testCreateMatchCompetitor_whenCompetitorIdIsMissing_thenThrowsValidationException() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setCompetitorId(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(request));
    }

    @Test
    void testCreateMatchCompetitor_whenMatchIdIsMissing_thenThrowsValidationException() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setMatchId(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(request));
    }

    @Test
    void testCreateMatchCompetitor_whenCompetitorCategoryIsEmpty_thenThrowsValidationException() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setCompetitorCategory("");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(request));
    }

    @Test
    void testCreateMatchCompetitor_whenDivisionIsBlank_thenThrowsValidationException() {
        // Arrange
        MatchCompetitorRequest request = validRequest();
        request.setDivision("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(request));
    }

    @Test
    void testCreateMatchCompetitor_whenCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(competitorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(validRequest()));
        verify(matchCompetitorRepository, never()).save(any());
    }

    @Test
    void testCreateMatchCompetitor_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor(1L)));
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(validRequest()));
        verify(matchCompetitorRepository, never()).save(any());
    }

    @Test
    void testCreateMatchCompetitor_whenDivisionIsUnknown_thenThrowsValidationException() {
        // Arrange
        stubCompetitorAndMatch();
        MatchCompetitorRequest request = validRequest();
        request.setDivision("Not A Division");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(request));
    }

    @Test
    void testCreateMatchCompetitor_whenCategoryIsUnknown_thenThrowsValidationException() {
        // Arrange
        stubCompetitorAndMatch();
        MatchCompetitorRequest request = validRequest();
        request.setCompetitorCategory("Not A Category");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(request));
    }

    @Test
    void testCreateMatchCompetitor_whenFirearmTypeIsUnknown_thenUsesTheFirearmTypeOfTheDivision() {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.save(any(MatchCompetitor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        MatchCompetitorRequest request = validRequest();
        request.setFirearmType("Not A Firearm");

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.createMatchCompetitor(request);

        // Assert
        assertEquals(FirearmType.HANDGUN, response.getFirearmType());
        assertEquals(Division.OPEN, response.getDivision());
    }

    @Test
    void testCreateMatchCompetitor_whenPowerFactorIsUnknown_thenThrowsValidationException() {
        // Arrange
        stubCompetitorAndMatch();
        MatchCompetitorRequest request = validRequest();
        request.setPowerFactor("Not A Power Factor");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(request));
    }

    @Test
    void testCreateMatchCompetitor_whenMatchClubIsUnknown_thenThrowsValidationException() {
        // Arrange
        stubCompetitorAndMatch();
        MatchCompetitorRequest request = validRequest();
        request.setMatchClub("Not A Club");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(request));
    }

    @Test
    void testCreateMatchCompetitor_whenEntryAlreadyExists_thenThrowsValidationException() {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.findByCompetitorIdAndMatchIdAndFirearmType(1L, 2L, FirearmType.HANDGUN))
                .thenReturn(Optional.of(matchCompetitor(9L)));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitor(validRequest()));
        verify(matchCompetitorRepository, never()).save(any());
    }

    @Test
    void testCreateMatchCompetitor_whenDuplicateAddedBeforeSave_thenThrowsValidationException() {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.save(any(MatchCompetitor.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscMatchCompetitorService.createMatchCompetitor(validRequest()));
        assertInstanceOf(DataIntegrityViolationException.class, exception.getCause());
    }

    @Test
    void testCreateMatchCompetitor_whenRequestIsValid_thenSavesAndMapsAllFields() {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.save(any(MatchCompetitor.class))).thenAnswer(invocation -> {
            MatchCompetitor saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.createMatchCompetitor(validRequest());

        // Assert
        assertEquals(5L, response.getMatchCompetitorId());
        assertEquals(1L, response.getCompetitorId());
        assertEquals(2L, response.getMatchId());
        assertEquals(ClubIdentifier.HPSC, response.getMatchClub());
        assertEquals(CompetitorCategory.JUNIOR, response.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, response.getFirearmType());
        assertEquals(Division.OPEN, response.getDivision());
        assertEquals(PowerFactor.MAJOR, response.getPowerFactor());
        assertEquals(new BigDecimal("95.5"), response.getPoints());
        assertEquals(new BigDecimal("2"), response.getOverallRanking());
        assertEquals(new BigDecimal("1"), response.getClubRanking());
        assertEquals(Boolean.FALSE, response.getIsVisitor());
    }

    @Test
    void testCreateMatchCompetitor_whenOptionalFieldsAreOmitted_thenTheyAreNull() {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.save(any(MatchCompetitor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        request.setMatchId(2L);
        request.setCompetitorCategory("Junior");
        request.setFirearmType("Handgun");
        request.setDivision("Open Division");
        request.setPowerFactor("Major");

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.createMatchCompetitor(request);

        // Assert
        assertNull(response.getMatchClub());
        assertEquals(PowerFactor.MAJOR, response.getPowerFactor());
        assertNull(response.getPoints());
        assertNull(response.getOverallRanking());
        assertNull(response.getClubRanking());
        assertNull(response.getIsVisitor());
    }

    // createMatchCompetitors()
    private static final String VALID_CSV = """
            CompetitorId,MatchId,Class,Cats,FirearmType,Div,PF
            1,2,HPSC,Junior,Handgun,Open Division,Major
            """;

    @Test
    void testCreateMatchCompetitors_whenCsvIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitors(null));
    }

    @Test
    void testCreateMatchCompetitors_whenCsvIsBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitors("  \t\n "));
    }

    @Test
    void testCreateMatchCompetitors_whenCsvCannotBeParsed_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscMatchCompetitorService.createMatchCompetitors("This is not valid CSV data\nJane\n"));
    }

    @Test
    void testCreateMatchCompetitors_whenRowIsValid_thenSavesAndMapsIt() throws Exception {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.save(any(MatchCompetitor.class))).thenAnswer(invocation -> {
            MatchCompetitor saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        // Act
        MatchCompetitorBulkResponseHolder holder = ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV);

        // Assert
        assertEquals(1, holder.getMatchCompetitors().size());
        MatchCompetitorResponse response = holder.getMatchCompetitors().getFirst().getMatchCompetitor();
        assertEquals(5L, response.getMatchCompetitorId());
        assertEquals(1L, response.getCompetitorId());
        assertEquals(2L, response.getMatchId());
        assertEquals(CompetitorCategory.JUNIOR, response.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, response.getFirearmType());
        assertEquals(Division.OPEN, response.getDivision());
    }

    @Test
    void testCreateMatchCompetitors_whenClubIsGiven_thenSkipsRowsForOtherClubs() throws Exception {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.save(any(MatchCompetitor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        String csvData = """
                CompetitorId,MatchId,Class,Cats,FirearmType,Div,PF
                1,2,HPSC,Junior,Handgun,Open Division,Major
                1,2,SOSC,Junior,Handgun,Open Division,Major
                1,2,,Junior,Handgun,Open Division,Major
                """;

        // Act
        MatchCompetitorBulkResponseHolder holder =
                ipscMatchCompetitorService.createMatchCompetitors(csvData, "HPSC");

        // Assert
        assertEquals(3, holder.getMatchCompetitors().size());
        assertTrue(holder.getMatchCompetitors().get(0).isSuccess());
        assertFalse(holder.getMatchCompetitors().get(1).isSuccess());
        assertTrue(holder.getMatchCompetitors().get(1).getMessage().startsWith("Skipped: match club is not"));
        assertFalse(holder.getMatchCompetitors().get(2).isSuccess());
        verify(matchCompetitorRepository, times(1)).save(any(MatchCompetitor.class));
    }

    @Test
    void testCreateMatchCompetitors_whenClubIsGiven_thenCreatesRowsForCompetitorsFromThatClubWhateverTheirMatchClub()
            throws Exception {
        // Arrange
        Competitor homeClubCompetitor = competitor(1L);
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);
        homeClubCompetitor.setHomeClub(club);
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(homeClubCompetitor));
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match(2L)));
        when(matchCompetitorRepository.save(any(MatchCompetitor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        String csvData = """
                CompetitorId,MatchId,Class,Cats,FirearmType,Div,PF
                1,2,SOSC,Junior,Handgun,Open Division,Major
                """;

        // Act
        MatchCompetitorBulkResponseHolder holder =
                ipscMatchCompetitorService.createMatchCompetitors(csvData, "HPSC");

        // Assert
        assertEquals(1, holder.getMatchCompetitors().size());
        assertTrue(holder.getMatchCompetitors().getFirst().isSuccess());
        assertEquals(ClubIdentifier.SOSC, holder.getMatchCompetitors().getFirst().getMatchCompetitor().getMatchClub());
    }

    @Test
    void testCreateMatchCompetitors_whenClubIsGivenAndRowCompetitorDoesNotExist_thenSkipsTheRow() throws Exception {
        // Arrange
        when(competitorRepository.findById(9L)).thenReturn(Optional.empty());
        String csvData = """
                CompetitorId,MatchId,Class,Cats,FirearmType,Div,PF
                9,2,SOSC,Junior,Handgun,Open Division,Major
                """;

        // Act
        MatchCompetitorBulkResponseHolder holder =
                ipscMatchCompetitorService.createMatchCompetitors(csvData, "HPSC");

        // Assert
        assertFalse(holder.getMatchCompetitors().getFirst().isSuccess());
        assertTrue(holder.getMatchCompetitors().getFirst().getMessage().startsWith("Skipped: match club is not"));
        verify(matchCompetitorRepository, never()).save(any(MatchCompetitor.class));
    }

    @Test
    void testCreateMatchCompetitors_whenClubIsGivenAndRowClubIsUnknown_thenReportsTheRowAsFailed() throws Exception {
        // Arrange
        String csvData = """
                CompetitorId,MatchId,Class,Cats,FirearmType,Div,PF
                1,2,Not A Club,Junior,Handgun,Open Division,Major
                """;

        // Act
        MatchCompetitorBulkResponseHolder holder =
                ipscMatchCompetitorService.createMatchCompetitors(csvData, "HPSC");

        // Assert
        assertFalse(holder.getMatchCompetitors().getFirst().isSuccess());
        assertTrue(holder.getMatchCompetitors().getFirst().getMessage().contains("Unknown match club"));
        verify(matchCompetitorRepository, never()).save(any(MatchCompetitor.class));
    }

    @Test
    void testCreateMatchCompetitors_whenClubIsNull_thenImportsOnlyTheHomeClubsRows() throws Exception {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.save(any(MatchCompetitor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        String csvData = """
                CompetitorId,MatchId,Class,Cats,FirearmType,Div,PF
                1,2,HPSC,Junior,Handgun,Open Division,Major
                1,2,SOSC,Junior,Handgun,Open Division,Major
                1,2,,Junior,Handgun,Open Division,Major
                """;

        // Act
        MatchCompetitorBulkResponseHolder holder =
                ipscMatchCompetitorService.createMatchCompetitors(csvData, null);

        // Assert
        assertEquals(3, holder.getMatchCompetitors().size());
        assertTrue(holder.getMatchCompetitors().get(0).isSuccess());
        assertFalse(holder.getMatchCompetitors().get(1).isSuccess());
        assertTrue(holder.getMatchCompetitors().get(1).getMessage().startsWith("Skipped"));
        assertFalse(holder.getMatchCompetitors().get(2).isSuccess());
    }

    @Test
    void testCreateMatchCompetitors_whenClubIsUnknown_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class,
                () -> ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV, "Not A Club"));
    }

    @Test
    void testCreateMatchCompetitors_whenRowHasMatchCompetitorId_thenIgnoresItAndCreates() throws Exception {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.save(any(MatchCompetitor.class))).thenAnswer(invocation -> {
            MatchCompetitor saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });
        String csvData = """
                MatchCompetitorId,CompetitorId,MatchId,Class,Cats,FirearmType,Div,PF
                99,1,2,HPSC,Junior,Handgun,Open Division,Major
                """;

        // Act
        MatchCompetitorBulkResponseHolder holder = ipscMatchCompetitorService.createMatchCompetitors(csvData);

        // Assert
        assertEquals(5L, holder.getMatchCompetitors().getFirst().getMatchCompetitor().getMatchCompetitorId());
        verify(matchCompetitorRepository, never()).findByIdWithCompetitorAndMatch(any());
    }

    @Test
    void testCreateMatchCompetitors_whenRowIsMissingRequiredColumn_thenThrowsValidationException() {
        // Arrange - Division is absent from the header
        String csvData = """
                CompetitorId,MatchId,Class,Cats,FirearmType
                1,2,HPSC,Junior,Handgun
                """;

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.createMatchCompetitors(csvData));
        verify(matchCompetitorRepository, never()).save(any());
    }

    @Test
    void testCreateMatchCompetitors_whenRowHasBlankRequiredValue_thenReportsRowAsFailed() throws Exception {
        // Arrange
        String csvData = """
                CompetitorId,MatchId,Class,Cats,FirearmType,Div,PF
                1,2,HPSC,Junior,Handgun,
                """;

        // Act
        MatchCompetitorBulkResponseHolder holder = ipscMatchCompetitorService.createMatchCompetitors(csvData);

        // Assert
        assertEquals(1, holder.getMatchCompetitors().size());
        MatchCompetitorBulkResponse result = holder.getMatchCompetitors().getFirst();
        assertFalse(result.isSuccess());
        assertEquals("Division is required.", result.getMessage());
        verify(matchCompetitorRepository, never()).save(any());
    }

    @Test
    void testCreateMatchCompetitors_whenCompetitorDoesNotExist_thenReportsRowAsFailedAndSavesNone() throws Exception {
        // Arrange
        when(competitorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        MatchCompetitorBulkResponseHolder holder = ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV);

        // Assert
        MatchCompetitorBulkResponse result = holder.getMatchCompetitors().getFirst();
        assertFalse(result.isSuccess());
        assertEquals("No competitor found with ID 1", result.getMessage());
        assertEquals(1L, result.getMatchCompetitor().getCompetitorId());
        assertEquals(2L, result.getMatchCompetitor().getMatchId());
        verify(matchCompetitorRepository, never()).save(any());
    }

    @Test
    void testCreateMatchCompetitors_whenRowDuplicatesAnExistingEntry_thenReportsRowAsFailedAndSavesNone() throws Exception {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.findByCompetitorIdAndMatchIdAndFirearmType(1L, 2L, FirearmType.HANDGUN))
                .thenReturn(Optional.of(matchCompetitor(9L)));

        // Act
        MatchCompetitorBulkResponseHolder holder = ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV);

        // Assert
        MatchCompetitorBulkResponse result = holder.getMatchCompetitors().getFirst();
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already has a"));
        verify(matchCompetitorRepository, never()).save(any());
    }

    @Test
    void testCreateMatchCompetitors_whenTwoRowsDuplicateEachOther_thenSavesTheFirstAndSkipsTheSecond() throws Exception {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.findByCompetitorIdAndMatchIdAndFirearmType(1L, 2L, FirearmType.HANDGUN))
                .thenReturn(Optional.empty(), Optional.of(matchCompetitor(5L)));
        when(matchCompetitorRepository.save(any(MatchCompetitor.class))).thenAnswer(invocation -> {
            MatchCompetitor saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });
        String csvData = """
                CompetitorId,MatchId,Class,Cats,FirearmType,Div,PF
                1,2,HPSC,Junior,Handgun,Open Division,Major
                1,2,HPSC,Lady,Handgun,Production Division,Major
                """;

        // Act
        MatchCompetitorBulkResponseHolder holder = ipscMatchCompetitorService.createMatchCompetitors(csvData);

        // Assert
        assertEquals(2, holder.getMatchCompetitors().size());
        assertTrue(holder.getMatchCompetitors().get(0).isSuccess());
        assertFalse(holder.getMatchCompetitors().get(1).isSuccess());
        verify(matchCompetitorRepository, times(1)).save(any());
    }

    @Test
    void testCreateMatchCompetitors_whenOneRowFails_thenStillSavesTheOtherRows() throws Exception {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.save(any(MatchCompetitor.class))).thenAnswer(invocation -> {
            MatchCompetitor saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });
        String csvData = """
                CompetitorId,MatchId,Class,Cats,FirearmType,Div,PF
                1,2,HPSC,Junior,Handgun,
                1,2,HPSC,Junior,Handgun,Open Division,Major
                """;

        // Act
        MatchCompetitorBulkResponseHolder holder = ipscMatchCompetitorService.createMatchCompetitors(csvData);

        // Assert
        assertEquals(2, holder.getMatchCompetitors().size());
        assertFalse(holder.getMatchCompetitors().get(0).isSuccess());
        assertTrue(holder.getMatchCompetitors().get(1).isSuccess());
        assertEquals(5L, holder.getMatchCompetitors().get(1).getMatchCompetitor().getMatchCompetitorId());
    }

    @Test
    void testCreateMatchCompetitors_whenSaveViolatesAnIntegrityConstraint_thenReportsRowAsFailed() throws Exception {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.save(any(MatchCompetitor.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        // Act
        MatchCompetitorBulkResponseHolder holder = ipscMatchCompetitorService.createMatchCompetitors(VALID_CSV);

        // Assert
        assertFalse(holder.getMatchCompetitors().getFirst().isSuccess());
    }

    // updateMatchCompetitor()
    @Test
    void testUpdateMatchCompetitor_whenRequestIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.updateMatchCompetitor(5L, null));
    }

    @Test
    void testUpdateMatchCompetitor_whenMatchCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorService.updateMatchCompetitor(999L, validRequest()));
        verify(matchCompetitorRepository, never()).save(any());
    }

    @Test
    void testUpdateMatchCompetitor_whenRequestIsValid_thenReplacesFields() {
        // Arrange
        stubCompetitorAndMatch();
        MatchCompetitor existing = matchCompetitor(5L);
        existing.setDivision(Division.STANDARD);
        existing.setPowerFactor(PowerFactor.MAJOR);
        existing.setPoints(new BigDecimal("1"));
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(existing));
        when(matchCompetitorRepository.save(existing)).thenReturn(existing);

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.updateMatchCompetitor(5L, validRequest());

        // Assert
        assertEquals(5L, response.getMatchCompetitorId());
        assertEquals(Division.OPEN, response.getDivision());
        assertEquals(new BigDecimal("95.5"), response.getPoints());
    }

    @Test
    void testUpdateMatchCompetitor_whenOnlyTheEntryItselfMatchesTheKey_thenSaves() {
        // Arrange
        stubCompetitorAndMatch();
        MatchCompetitor existing = matchCompetitor(5L);
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(existing));
        when(matchCompetitorRepository.findByCompetitorIdAndMatchIdAndFirearmType(1L, 2L, FirearmType.HANDGUN))
                .thenReturn(Optional.of(existing));
        when(matchCompetitorRepository.save(existing)).thenReturn(existing);

        // Act & Assert
        assertDoesNotThrow(() -> ipscMatchCompetitorService.updateMatchCompetitor(5L, validRequest()));
    }

    @Test
    void testUpdateMatchCompetitor_whenAnotherEntryMatchesTheKey_thenThrowsValidationException() {
        // Arrange
        stubCompetitorAndMatch();
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(matchCompetitor(5L)));
        when(matchCompetitorRepository.findByCompetitorIdAndMatchIdAndFirearmType(1L, 2L, FirearmType.HANDGUN))
                .thenReturn(Optional.of(matchCompetitor(9L)));

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.updateMatchCompetitor(5L, validRequest()));
        verify(matchCompetitorRepository, never()).save(any());
    }

    // patchMatchCompetitor()
    @Test
    void testPatchMatchCompetitor_whenMatchCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscMatchCompetitorService.patchMatchCompetitor(999L, new MatchCompetitorPatchRequest()));
    }

    @Test
    void testPatchMatchCompetitor_whenAllFieldsAreNull_thenLeavesEverythingUnchanged() {
        // Arrange
        MatchCompetitor existing = matchCompetitor(5L);
        existing.setPoints(new BigDecimal("80"));
        existing.setIsVisitor(true);
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(existing));
        when(matchCompetitorRepository.save(existing)).thenReturn(existing);

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.patchMatchCompetitor(5L,
                new MatchCompetitorPatchRequest());

        // Assert
        assertEquals(Division.OPEN, response.getDivision());
        assertEquals(new BigDecimal("80"), response.getPoints());
        assertEquals(Boolean.TRUE, response.getIsVisitor());
    }

    @Test
    void testPatchMatchCompetitor_whenRequiredFieldsAreEmptyOrBlank_thenLeavesThemUnchanged() {
        // Arrange
        MatchCompetitor existing = matchCompetitor(5L);
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(existing));
        when(matchCompetitorRepository.save(existing)).thenReturn(existing);
        MatchCompetitorPatchRequest patch = new MatchCompetitorPatchRequest();
        patch.setCompetitorCategory("");
        patch.setFirearmType("  ");
        patch.setDivision("");

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.patchMatchCompetitor(5L, patch);

        // Assert
        assertEquals(CompetitorCategory.JUNIOR, response.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, response.getFirearmType());
        assertEquals(Division.OPEN, response.getDivision());
    }

    @Test
    void testPatchMatchCompetitor_whenFieldsAreProvided_thenOnlyTheyChange() {
        // Arrange
        MatchCompetitor existing = matchCompetitor(5L);
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(existing));
        when(matchCompetitorRepository.save(existing)).thenReturn(existing);
        MatchCompetitorPatchRequest patch = new MatchCompetitorPatchRequest();
        patch.setMatchClub("HPSC");
        patch.setCompetitorCategory("Senior");
        patch.setDivision("Standard Division");
        patch.setPowerFactor("Minor");
        patch.setPoints(new BigDecimal("70"));
        patch.setOverallRanking(new BigDecimal("4"));
        patch.setClubRanking(new BigDecimal("3"));
        patch.setIsVisitor(true);

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.patchMatchCompetitor(5L, patch);

        // Assert
        assertEquals(ClubIdentifier.HPSC, response.getMatchClub());
        assertEquals(CompetitorCategory.SENIOR, response.getCompetitorCategory());
        assertEquals(Division.STANDARD, response.getDivision());
        assertEquals(PowerFactor.MINOR, response.getPowerFactor());
        assertEquals(new BigDecimal("70"), response.getPoints());
        assertEquals(new BigDecimal("4"), response.getOverallRanking());
        assertEquals(new BigDecimal("3"), response.getClubRanking());
        assertEquals(Boolean.TRUE, response.getIsVisitor());
        assertEquals(FirearmType.HANDGUN, response.getFirearmType());
    }

    @Test
    void testPatchMatchCompetitor_whenOverallScoresAreProvided_thenTheyAreCopiedOntoTheEntry() {
        // Arrange
        MatchCompetitor existing = matchCompetitor(5L);
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(existing));
        when(matchCompetitorRepository.save(existing)).thenReturn(existing);
        MatchCompetitorPatchRequest patch = new MatchCompetitorPatchRequest();
        patch.setPercentage(new BigDecimal("98.25"));
        patch.setTime(new BigDecimal("41.5"));
        patch.setPercentageOfPossiblePoints(new BigDecimal("93.75"));
        patch.setAlpha(40);
        patch.setCharlie(5);
        patch.setDelta(1);
        patch.setMisses(2);
        patch.setNoPenaltyMisses(1);
        patch.setNoShoots(1);
        patch.setProceduralErrors(3);
        patch.setAdditionalPenalties(4);

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.patchMatchCompetitor(5L, patch);

        // Assert
        assertEquals(new BigDecimal("98.25"), response.getPercentage());
        assertEquals(new BigDecimal("41.5"), response.getTime());
        assertEquals(new BigDecimal("93.75"), response.getPercentageOfPossiblePoints());
        assertEquals(40, response.getAlpha());
        assertEquals(5, response.getCharlie());
        assertEquals(1, response.getDelta());
        assertEquals(2, response.getMisses());
        assertEquals(1, response.getNoPenaltyMisses());
        assertEquals(1, response.getNoShoots());
        assertEquals(3, response.getProceduralErrors());
        assertEquals(4, response.getAdditionalPenalties());
    }

    @Test
    void testPatchMatchCompetitor_whenCompetitorIdChanges_thenResolvesNewCompetitor() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(matchCompetitor(5L)));
        when(competitorRepository.findById(7L)).thenReturn(Optional.of(competitor(7L)));
        when(matchCompetitorRepository.save(any(MatchCompetitor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        MatchCompetitorPatchRequest patch = new MatchCompetitorPatchRequest();
        patch.setCompetitorId(7L);

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.patchMatchCompetitor(5L, patch);

        // Assert
        assertEquals(7L, response.getCompetitorId());
    }

    @Test
    void testPatchMatchCompetitor_whenNewCompetitorDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(matchCompetitor(5L)));
        when(competitorRepository.findById(7L)).thenReturn(Optional.empty());
        MatchCompetitorPatchRequest patch = new MatchCompetitorPatchRequest();
        patch.setCompetitorId(7L);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorService.patchMatchCompetitor(5L, patch));
    }

    @Test
    void testPatchMatchCompetitor_whenMatchIdChanges_thenResolvesNewMatch() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(matchCompetitor(5L)));
        when(ipscMatchRepository.findById(8L)).thenReturn(Optional.of(match(8L)));
        when(matchCompetitorRepository.save(any(MatchCompetitor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        MatchCompetitorPatchRequest patch = new MatchCompetitorPatchRequest();
        patch.setMatchId(8L);

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.patchMatchCompetitor(5L, patch);

        // Assert
        assertEquals(8L, response.getMatchId());
    }

    @Test
    void testPatchMatchCompetitor_whenNewMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(matchCompetitor(5L)));
        when(ipscMatchRepository.findById(8L)).thenReturn(Optional.empty());
        MatchCompetitorPatchRequest patch = new MatchCompetitorPatchRequest();
        patch.setMatchId(8L);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorService.patchMatchCompetitor(5L, patch));
    }

    @Test
    void testPatchMatchCompetitor_whenDivisionIsUnknown_thenThrowsValidationException() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(matchCompetitor(5L)));
        MatchCompetitorPatchRequest patch = new MatchCompetitorPatchRequest();
        patch.setDivision("Not A Division");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.patchMatchCompetitor(5L, patch));
        verify(matchCompetitorRepository, never()).save(any());
    }

    @Test
    void testPatchMatchCompetitor_whenFirearmTypeChangeDuplicatesAnotherEntry_thenThrowsValidationException() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(matchCompetitor(5L)));
        when(matchCompetitorRepository.findByCompetitorIdAndMatchIdAndFirearmType(1L, 2L, FirearmType.RIFLE))
                .thenReturn(Optional.of(matchCompetitor(9L)));
        MatchCompetitorPatchRequest patch = new MatchCompetitorPatchRequest();
        patch.setFirearmType("Rifle");
        patch.setDivision("Rifle Open Division");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchCompetitorService.patchMatchCompetitor(5L, patch));
        verify(matchCompetitorRepository, never()).save(any());
    }

    // getMatchCompetitor()
    @Test
    void testGetMatchCompetitor_whenItExists_thenReturnsMappedResponse() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(matchCompetitor(5L)));

        // Act
        MatchCompetitorResponse response = ipscMatchCompetitorService.getMatchCompetitor(5L);

        // Assert
        assertEquals(5L, response.getMatchCompetitorId());
        assertEquals(1L, response.getCompetitorId());
        assertEquals(2L, response.getMatchId());
    }

    @Test
    void testGetMatchCompetitor_whenItDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorService.getMatchCompetitor(999L));
    }

    // getAllMatchCompetitors()
    @Test
    void testGetAllMatchCompetitors_whenSomeExist_thenReturnsAllMapped() {
        // Arrange
        when(matchCompetitorRepository.findAllWithCompetitorAndMatch())
                .thenReturn(List.of(matchCompetitor(5L), matchCompetitor(6L)));

        // Act
        List<MatchCompetitorResponse> responses = ipscMatchCompetitorService.getAllMatchCompetitors();

        // Assert
        assertEquals(2, responses.size());
        assertEquals(5L, responses.get(0).getMatchCompetitorId());
        assertEquals(6L, responses.get(1).getMatchCompetitorId());
    }

    @Test
    void testGetAllMatchCompetitors_whenNoneExist_thenReturnsEmptyList() {
        // Arrange
        when(matchCompetitorRepository.findAllWithCompetitorAndMatch()).thenReturn(List.of());

        // Act & Assert
        assertTrue(ipscMatchCompetitorService.getAllMatchCompetitors().isEmpty());
    }

    // deleteMatchCompetitor()
    @Test
    void testDeleteMatchCompetitor_whenItDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchCompetitorService.deleteMatchCompetitor(999L));
        verify(matchCompetitorRepository, never()).delete(any(MatchCompetitor.class));
    }

    @Test
    void testDeleteMatchCompetitor_whenItExists_thenDeletesAndFlushes() {
        // Arrange
        MatchCompetitor existing = matchCompetitor(5L);
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(existing));

        // Act
        assertDoesNotThrow(() -> ipscMatchCompetitorService.deleteMatchCompetitor(5L));

        // Assert
        verify(matchCompetitorRepository).delete(existing);
        verify(matchCompetitorRepository).flush();
    }

    @Test
    void testDeleteMatchCompetitor_whenStillReferenced_thenThrowsValidationException() {
        // Arrange
        when(matchCompetitorRepository.findByIdWithCompetitorAndMatch(5L)).thenReturn(Optional.of(matchCompetitor(5L)));
        doThrow(new DataIntegrityViolationException("FK violation")).when(matchCompetitorRepository).flush();

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscMatchCompetitorService.deleteMatchCompetitor(5L));
        assertInstanceOf(DataIntegrityViolationException.class, exception.getCause());
    }

    // Helpers
    private void stubCompetitorAndMatch() {
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor(1L)));
        when(ipscMatchRepository.findById(2L)).thenReturn(Optional.of(match(2L)));
    }

    private MatchCompetitorRequest validRequest() {
        MatchCompetitorRequest request = new MatchCompetitorRequest();
        request.setCompetitorId(1L);
        request.setMatchId(2L);
        request.setMatchClub("HPSC");
        request.setCompetitorCategory("Junior");
        request.setFirearmType("Handgun");
        request.setDivision("Open Division");
        request.setPowerFactor("Major");
        request.setPoints(new BigDecimal("95.5"));
        request.setOverallRanking(new BigDecimal("2"));
        request.setClubRanking(new BigDecimal("1"));
        request.setIsVisitor(false);
        return request;
    }

    private Competitor competitor(Long id) {
        Competitor competitor = new Competitor();
        competitor.setId(id);
        return competitor;
    }

    private IpscMatch match(Long id) {
        IpscMatch match = new IpscMatch();
        match.setId(id);
        return match;
    }

    private MatchCompetitor matchCompetitor(Long id) {
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(id);
        matchCompetitor.setCompetitor(competitor(1L));
        matchCompetitor.setMatch(match(2L));
        matchCompetitor.setCompetitorCategory(CompetitorCategory.JUNIOR);
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitor.setDivision(Division.OPEN);
        matchCompetitor.setPowerFactor(PowerFactor.MAJOR);
        return matchCompetitor;
    }
}
