package za.co.hpsc.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponseHolder;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.repositories.ShooterLogCompetitorRepository;
import za.co.hpsc.web.services.impl.IpscMatchServiceImpl;
import za.co.hpsc.web.services.impl.TransactionServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link IpscMatchService} contract, exercised entirely through the
 * interface type, with {@link IpscMatchRepository}/{@link ClubRepository}
 * mocked, and a real {@link TransactionServiceImpl} committing through
 * those mocks under a mocked {@link PlatformTransactionManager}. See {@link IpscMatchServiceIntegrationTest} for the same
 * contract exercised against a real H2-backed Spring context.
 */
@ExtendWith(MockitoExtension.class)
public class IpscMatchServiceTest {

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;

    @Mock
    private ShooterLogCompetitorRepository shooterLogCompetitorRepository;

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    private IpscMatchService ipscMatchService;

    @BeforeEach
    void setUp() {
        TransactionService transactionService = new TransactionServiceImpl(competitorRepository,
                ipscMatchRepository, transactionManager);
        ipscMatchService = new IpscMatchServiceImpl(ipscMatchRepository, clubRepository,
                matchCompetitorRepository, shooterLogCompetitorRepository, transactionService);
    }

    // createMatch()
    @Test
    void testCreateMatch_whenRequestIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(null));
    }

    @Test
    void testCreateMatch_whenMatchNameIsBlank_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest("Test Club");
        request.setMatchName("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchDateIsMissing_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest("Test Club");
        request.setMatchDate(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenClubIsMissing_thenDefaultsToDefaultMatchClub() {
        // Arrange
        stubDefaultClub();
        stubMatchSaveReturnsSameEntity();
        MatchRequest request = validRequest(null);

        // Act
        MatchResponse response = assertDoesNotThrow(() -> ipscMatchService.createMatch(request));

        // Assert
        assertEquals(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER, response.getClub());
    }

    @Test
    void testCreateMatch_whenClubIsBlank_thenDefaultsToDefaultMatchClub() {
        // Arrange
        stubDefaultClub();
        stubMatchSaveReturnsSameEntity();
        MatchRequest request = validRequest("  ");

        // Act
        MatchResponse response = assertDoesNotThrow(() -> ipscMatchService.createMatch(request));

        // Assert
        assertEquals(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER, response.getClub());
    }

    @Test
    void testCreateMatch_whenClubIsMissingAndDefaultClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(clubRepository.findByIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER)).thenReturn(Optional.empty());
        MatchRequest request = validRequest(null);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchFirearmTypeIsMissing_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest("Test Club");
        request.setMatchFirearmType(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchCategoryIsMissing_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest("Test Club");
        request.setMatchCategory(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());
        MatchRequest request = validRequest("No Such Club");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchFirearmTypeIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        stubExistingClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");
        request.setMatchFirearmType("Not A Firearm Type");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchCategoryIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        stubExistingClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");
        request.setMatchCategory("Not A Category");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenRequestIsValid_thenReturnsMappedResponse() {
        // Arrange
        stubExistingClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        stubMatchSaveReturnsSameEntity();
        MatchRequest request = validRequest("Test Club");

        // Act
        MatchResponse response = assertDoesNotThrow(() -> ipscMatchService.createMatch(request));

        // Assert
        assertEquals(1L, response.getMatchId());
        assertEquals("Club Championship", response.getMatchName());
        assertEquals(LocalDate.of(2026, 9, 12), response.getMatchDate());
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, response.getClub());
        assertEquals(FirearmType.HANDGUN, response.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, response.getMatchCategory());
        assertEquals(LocalTime.of(8, 0), response.getStartTime());
        assertEquals(LocalTime.of(17, 0), response.getEndTime());
        assertEquals("https://example.com/matches/1", response.getUrl());
    }

    // createMatches()
    @Test
    void testCreateMatches_whenCsvDataIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatches(null));
    }

    @Test
    void testCreateMatches_whenCsvDataIsBlank_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatches("   "));
    }

    @Test
    void testCreateMatches_whenCsvDataIsMalformed_thenThrowsValidationException() {
        // Arrange
        String csvData = "NotAHeader\nSomeRow,Value";

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatches(csvData));
    }

    @Test
    void testCreateMatches_whenSingleValidRow_thenReturnsHolderWithMappedResponse() {
        // Arrange
        stubExistingClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        stubMatchSaveReturnsSameEntity();
        String csvData = """
                MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,StartTime,EndTime,Url
                2026-09-12,Club Championship,Test Club,%s,%s,08:00,17:00,https://example.com/matches/1
                """.formatted(FirearmType.HANDGUN, MatchCategory.CLUB_SHOOT);

        // Act
        MatchResponseHolder holder = assertDoesNotThrow(() -> ipscMatchService.createMatches(csvData));

        // Assert
        assertEquals(1, holder.getMatches().size());
        assertEquals("Club Championship", holder.getMatches().getFirst().getMatchName());
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, holder.getMatches().getFirst().getClub());
        assertEquals(LocalTime.of(8, 0), holder.getMatches().getFirst().getStartTime());
        assertEquals(LocalTime.of(17, 0), holder.getMatches().getFirst().getEndTime());
        assertEquals("https://example.com/matches/1", holder.getMatches().getFirst().getUrl());
    }

    @Test
    void testCreateMatches_whenMultipleValidRows_thenPersistsEachRowInOrder() {
        // Arrange
        stubExistingClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        stubMatchSaveReturnsSameEntity();
        String csvData = """
                MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,StartTime,EndTime,Url
                2026-09-12,First Match,Test Club,%1$s,%2$s
                2026-09-19,Second Match,Test Club,%1$s,%2$s
                """.formatted(FirearmType.HANDGUN, MatchCategory.CLUB_SHOOT);

        // Act
        MatchResponseHolder holder = assertDoesNotThrow(() -> ipscMatchService.createMatches(csvData));

        // Assert
        assertEquals(2, holder.getMatches().size());
        assertEquals("First Match", holder.getMatches().get(0).getMatchName());
        assertEquals("Second Match", holder.getMatches().get(1).getMatchName());
        verify(ipscMatchRepository, times(2)).save(any(IpscMatch.class));
    }

    @Test
    void testCreateMatches_whenRowIsMissingRequiredField_thenThrowsValidationException() {
        // Arrange - MatchName is present but blank, so it survives CSV parsing and instead trips
        // createMatch's own validation
        String csvData = """
                MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,StartTime,EndTime,Url
                2026-09-12,,Test Club,%s,%s
                """.formatted(FirearmType.HANDGUN, MatchCategory.CLUB_SHOOT);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatches(csvData));
    }

    @Test
    void testCreateMatches_whenRowHasUnrecognisedFirearmType_thenThrowsValidationException() {
        // Arrange
        stubExistingClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        String csvData = """
                MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,StartTime,EndTime,Url
                2026-09-12,Club Championship,Test Club,Not A Firearm Type,%s
                """.formatted(MatchCategory.CLUB_SHOOT);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatches(csvData));
    }

    @Test
    void testCreateMatches_whenRowClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());
        String csvData = """
                MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,StartTime,EndTime,Url
                2026-09-12,Club Championship,No Such Club,%s,%s
                """.formatted(FirearmType.HANDGUN, MatchCategory.CLUB_SHOOT);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.createMatches(csvData));
    }

    // deleteMatch()
    @Test
    void testDeleteMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findByIdWithClub(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.deleteMatch(999L));
        verify(ipscMatchRepository, never()).delete(any(IpscMatch.class));
    }

    @Test
    void testDeleteMatch_whenMatchHasCompetitorResults_thenThrowsValidationException() {
        // Arrange
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(newMatch(1L)));
        when(matchCompetitorRepository.existsByMatchId(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.deleteMatch(1L));
        verify(ipscMatchRepository, never()).delete(any(IpscMatch.class));
    }

    @Test
    void testDeleteMatch_whenMatchHasShooterLogEntries_thenThrowsValidationException() {
        // Arrange
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(newMatch(1L)));
        when(matchCompetitorRepository.existsByMatchId(1L)).thenReturn(false);
        when(shooterLogCompetitorRepository.existsByMatchId(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.deleteMatch(1L));
        verify(ipscMatchRepository, never()).delete(any(IpscMatch.class));
    }

    @Test
    void testDeleteMatch_whenMatchHasNoDependents_thenDeletesMatch() {
        // Arrange
        IpscMatch match = newMatch(1L);
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(match));
        when(matchCompetitorRepository.existsByMatchId(1L)).thenReturn(false);
        when(shooterLogCompetitorRepository.existsByMatchId(1L)).thenReturn(false);

        // Act
        assertDoesNotThrow(() -> ipscMatchService.deleteMatch(1L));

        // Assert
        verify(ipscMatchRepository).delete(match);
        verify(ipscMatchRepository).flush();
    }

    @Test
    void testDeleteMatch_whenReferenceAddedBeforeFlush_thenThrowsValidationException() {
        // Arrange
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(newMatch(1L)));
        when(matchCompetitorRepository.existsByMatchId(1L)).thenReturn(false);
        when(shooterLogCompetitorRepository.existsByMatchId(1L)).thenReturn(false);
        doThrow(new DataIntegrityViolationException("FK violation")).when(ipscMatchRepository).flush();

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscMatchService.deleteMatch(1L));
        assertInstanceOf(DataIntegrityViolationException.class, exception.getCause());
    }

    // getAllMatches()
    @Test
    void testGetAllMatches_whenNoMatchesExist_thenReturnsEmptyList() {
        when(ipscMatchRepository.findAllWithClub()).thenReturn(List.of());

        List<MatchResponse> matches = ipscMatchService.getAllMatches();

        assertTrue(matches.isEmpty());
    }

    @Test
    void testGetAllMatches_whenMatchesExist_thenReturnsAll() {
        // Arrange
        Club club = newClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        IpscMatch first = new IpscMatch();
        first.setId(1L);
        first.setName("First Match");
        first.setClub(club);
        first.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        first.setMatchFirearmType(FirearmType.HANDGUN);
        first.setMatchCategory(MatchCategory.CLUB_SHOOT);

        IpscMatch second = new IpscMatch();
        second.setId(2L);
        second.setName("Second Match");
        second.setClub(club);
        second.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        second.setMatchFirearmType(FirearmType.HANDGUN);
        second.setMatchCategory(MatchCategory.CLUB_SHOOT);

        when(ipscMatchRepository.findAllWithClub()).thenReturn(List.of(first, second));

        // Act
        List<MatchResponse> matches = ipscMatchService.getAllMatches();

        // Assert
        assertEquals(2, matches.size());
        assertTrue(matches.stream().anyMatch(match -> match.getMatchId().equals(1L)));
        assertTrue(matches.stream().anyMatch(match -> match.getMatchId().equals(2L)));
    }

    // getMatch()
    @Test
    void testGetMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findByIdWithClub(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.getMatch(999L));
    }

    @Test
    void testGetMatch_whenMatchExists_thenReturnsMatch() {
        // Arrange
        Club club = newClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Club Championship");
        match.setClub(club);
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(match));

        // Act
        MatchResponse fetched = assertDoesNotThrow(() -> ipscMatchService.getMatch(1L));

        // Assert
        assertEquals(1L, fetched.getMatchId());
        assertEquals("Club Championship", fetched.getMatchName());
    }

    // patchMatch()
    @Test
    void testPatchMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findByIdWithClub(999L)).thenReturn(Optional.empty());
        MatchRequest request = new MatchRequest();
        request.setMatchName("Renamed");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.patchMatch(999L, request));
    }

    @Test
    void testPatchMatch_whenClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        MatchRequest patch = new MatchRequest();
        patch.setClub("No Such Club");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.patchMatch(1L, patch));
    }

    @Test
    void testPatchMatch_whenMatchFirearmTypeIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));

        MatchRequest patch = new MatchRequest();
        patch.setMatchFirearmType("Not A Firearm Type");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.patchMatch(1L, patch));
    }

    @Test
    void testPatchMatch_whenClubIsProvided_thenClubIsResolvedAndSet() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        existing.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));
        stubExistingClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        stubMatchSaveReturnsSameEntity();

        MatchRequest patch = new MatchRequest();
        patch.setClub("Test Club");

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, patched.getClub());
    }

    @Test
    void testPatchMatch_whenMatchDateIsProvided_thenMatchDateChanges() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));
        stubMatchSaveReturnsSameEntity();

        LocalDate newDate = LocalDate.of(2027, 3, 20);
        MatchRequest patch = new MatchRequest();
        patch.setMatchDate(newDate);

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals(newDate, patched.getMatchDate());
    }

    @Test
    void testPatchMatch_whenStartAndEndTimeAreProvided_thenStartAndEndTimeChange() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        existing.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));
        stubMatchSaveReturnsSameEntity();

        LocalTime newStartTime = LocalTime.of(8, 0);
        LocalTime newEndTime = LocalTime.of(17, 0);
        MatchRequest patch = new MatchRequest();
        patch.setStartTime(newStartTime);
        patch.setEndTime(newEndTime);

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals(newStartTime, patched.getStartTime());
        assertEquals(newEndTime, patched.getEndTime());
    }

    @Test
    void testPatchMatch_whenUrlIsProvided_thenUrlChanges() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        existing.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));
        stubMatchSaveReturnsSameEntity();

        MatchRequest patch = new MatchRequest();
        patch.setUrl("https://example.com/matches/1");

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals("https://example.com/matches/1", patched.getUrl());
    }

    @Test
    void testPatchMatch_whenMatchFirearmTypeIsProvided_thenMatchFirearmTypeIsResolvedAndSet() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        existing.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));
        stubMatchSaveReturnsSameEntity();

        MatchRequest patch = new MatchRequest();
        patch.setMatchFirearmType(FirearmType.RIFLE.toString());

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals(FirearmType.RIFLE, patched.getMatchFirearmType());
    }

    @Test
    void testPatchMatch_whenMatchCategoryIsProvided_thenMatchCategoryIsResolvedAndSet() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        existing.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));
        stubMatchSaveReturnsSameEntity();

        MatchRequest patch = new MatchRequest();
        patch.setMatchCategory(MatchCategory.CLUB_SHOOT.toString());

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals(MatchCategory.CLUB_SHOOT, patched.getMatchCategory());
    }

    @Test
    void testPatchMatch_whenMatchCategoryIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));

        MatchRequest patch = new MatchRequest();
        patch.setMatchCategory("Not A Match Category");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.patchMatch(1L, patch));
    }

    @Test
    void testPatchMatch_whenOnlyMatchNameIsProvided_thenOnlyMatchNameChanges() {
        // Arrange
        Club club = newClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        existing.setName("Club Championship");
        existing.setClub(club);
        existing.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        existing.setMatchFirearmType(FirearmType.HANDGUN);
        existing.setMatchCategory(MatchCategory.CLUB_SHOOT);
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));
        when(ipscMatchRepository.save(any(IpscMatch.class))).thenReturn(existing);

        MatchRequest patch = new MatchRequest();
        patch.setMatchName("Renamed Championship");

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals("Renamed Championship", patched.getMatchName());
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, patched.getClub());
    }

    // updateMatch()
    @Test
    void testUpdateMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscMatchService.updateMatch(999L, validRequest("Test Club")));
    }

    @Test
    void testUpdateMatch_whenMatchNameIsMissing_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest("Test Club");
        request.setMatchName(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.updateMatch(1L, request));
    }

    @Test
    void testUpdateMatch_whenClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscMatchService.updateMatch(1L, validRequest("No Such Club")));
    }

    @Test
    void testUpdateMatch_whenRequestIsValid_thenReplacesAllFields() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(existing));
        when(ipscMatchRepository.save(any(IpscMatch.class))).thenReturn(existing);

        Club otherClub = stubExistingClub("Other Club", ClubIdentifier.SOSC);


        MatchRequest replacement = new MatchRequest();
        replacement.setMatchName("Different Match");
        replacement.setMatchDate(LocalDate.of(2027, 1, 1));
        replacement.setClub("Other Club");
        replacement.setMatchFirearmType(FirearmType.RIFLE.toString());
        replacement.setMatchCategory(MatchCategory.LEAGUE.toString());
        replacement.setStartTime(LocalTime.of(10, 0));
        replacement.setEndTime(LocalTime.of(16, 0));
        replacement.setUrl("https://example.com/matches/different");

        // Act
        MatchResponse updated = assertDoesNotThrow(() -> ipscMatchService.updateMatch(1L, replacement));

        // Assert
        assertEquals(1L, updated.getMatchId());
        assertEquals("Different Match", updated.getMatchName());
        assertEquals(LocalDate.of(2027, 1, 1), updated.getMatchDate());
        assertEquals(ClubIdentifier.SOSC, updated.getClub());
        assertEquals(FirearmType.RIFLE, updated.getMatchFirearmType());
        assertEquals(MatchCategory.LEAGUE, updated.getMatchCategory());
        assertEquals(LocalTime.of(10, 0), updated.getStartTime());
        assertEquals(LocalTime.of(16, 0), updated.getEndTime());
        assertEquals("https://example.com/matches/different", updated.getUrl());
        assertSame(otherClub, existing.getClub());
    }

    // Helpers
    private MatchRequest validRequest(String club) {
        MatchRequest request = new MatchRequest();
        request.setMatchName("Club Championship");
        request.setMatchDate(LocalDate.of(2026, 9, 12));
        request.setStartTime(LocalTime.of(8, 0));
        request.setEndTime(LocalTime.of(17, 0));
        request.setClub(club);
        request.setMatchFirearmType(FirearmType.HANDGUN.toString());
        request.setMatchCategory(MatchCategory.CLUB_SHOOT.toString());
        request.setUrl("https://example.com/matches/1");
        return request;
    }

    private Club newClub(String name, ClubIdentifier identifier) {
        Club club = new Club();
        club.setId(10L);
        club.setName(name);
        club.setIdentifier(identifier);
        return club;
    }

    private Club stubExistingClub(String name, ClubIdentifier identifier) {
        Club club = newClub(name, identifier);
        when(clubRepository.findByName(name)).thenReturn(Optional.of(club));
        return club;
    }

    private Club stubDefaultClub() {
        Club club = newClub("Eufees Clubs", IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER);
        when(clubRepository.findByIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER)).thenReturn(Optional.of(club));
        return club;
    }

    private void stubMatchSaveReturnsSameEntity() {
        when(ipscMatchRepository.save(any(IpscMatch.class))).thenAnswer(invocation -> {
            IpscMatch match = invocation.getArgument(0);
            match.setId(1L);
            return match;
        });
    }

    private IpscMatch newMatch(Long id) {
        IpscMatch match = new IpscMatch();
        match.setId(id);
        match.setName("Club Championship");
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        return match;
    }
}
