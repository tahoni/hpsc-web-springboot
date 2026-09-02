package za.co.hpsc.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchStageRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.services.impl.IpscMatchServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link IpscMatchService} contract, exercised entirely through the
 * interface type, with {@link IpscMatchRepository}/{@link IpscMatchStageRepository}/
 * {@link ClubRepository} mocked. See {@link IpscMatchServiceIntegrationTest} for the same
 * contract exercised against a real H2-backed Spring context.
 */
@ExtendWith(MockitoExtension.class)
public class IpscMatchServiceTest {

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private IpscMatchStageRepository ipscMatchStageRepository;

    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private IpscMatchServiceImpl ipscMatchServiceImpl;

    private IpscMatchService ipscMatchService;

    @BeforeEach
    void setUp() {
        ipscMatchService = ipscMatchServiceImpl;
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
    void testCreateMatch_whenClubIsMissing_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenClubIsBlank_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
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
        stubExistingClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");
        request.setMatchFirearmType("Not A Firearm Type");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchCategoryIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        stubExistingClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");
        request.setMatchCategory("Not A Category");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenRequestHasNoStages_thenReturnsMappedResponseWithEmptyStageList() {
        // Arrange
        stubExistingClub("Test Club", ClubIdentifier.HPSC);
        stubMatchSaveReturnsSameEntity();
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of());
        MatchRequest request = validRequest("Test Club");

        // Act
        MatchResponse response = assertDoesNotThrow(() -> ipscMatchService.createMatch(request));

        // Assert
        assertEquals(1L, response.getMatchId());
        assertEquals("Club Championship", response.getMatchName());
        assertEquals(LocalDate.of(2026, 9, 12), response.getMatchDate());
        assertEquals(ClubIdentifier.HPSC, response.getClub());
        assertEquals(FirearmType.HANDGUN, response.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, response.getMatchCategory());
        assertTrue(response.getStages().isEmpty());
    }

    @Test
    void testCreateMatch_whenRequestIncludesStages_thenPersistsStagesInOrder() {
        // Arrange
        stubExistingClub("Test Club", ClubIdentifier.HPSC);
        stubMatchSaveReturnsSameEntity();
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of());
        stubStageSaveAssignsIncrementingId();
        MatchRequest request = validRequest("Test Club");
        request.setStages(List.of(
                new MatchStageRequest(null, 1, "Stage 1 - The Bank Job"),
                new MatchStageRequest(null, 2, "Stage 2 - The Getaway")));

        // Act
        MatchResponse response = assertDoesNotThrow(() -> ipscMatchService.createMatch(request));

        // Assert
        assertEquals(2, response.getStages().size());
        assertNotNull(response.getStages().getFirst().getStageId());
        assertEquals(1, response.getStages().getFirst().getStageNumber());
        assertEquals("Stage 1 - The Bank Job", response.getStages().getFirst().getStageName());
        assertEquals(2, response.getStages().get(1).getStageNumber());
        assertEquals("Stage 2 - The Getaway", response.getStages().get(1).getStageName());
    }

    // getAllMatches()
    @Test
    void testGetAllMatches_whenNoMatchesExist_thenReturnsEmptyList() {
        when(ipscMatchRepository.findAll()).thenReturn(List.of());

        List<MatchResponse> matches = ipscMatchService.getAllMatches();

        assertTrue(matches.isEmpty());
    }

    @Test
    void testGetAllMatches_whenMatchesExist_thenReturnsAllWithStages() {
        // Arrange
        Club club = newClub("Test Club", ClubIdentifier.HPSC);
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

        when(ipscMatchRepository.findAll()).thenReturn(List.of(first, second));

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(100L);
        stage.setStageNumber(1);
        stage.setStageName("Stage 1");
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of(stage));
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(2L)).thenReturn(List.of());

        // Act
        List<MatchResponse> matches = ipscMatchService.getAllMatches();

        // Assert
        assertEquals(2, matches.size());
        assertTrue(matches.stream().anyMatch(match -> match.getMatchId().equals(1L) && (match.getStages().size() == 1)));
        assertTrue(matches.stream().anyMatch(match -> match.getMatchId().equals(2L) && match.getStages().isEmpty()));
    }

    // getMatch()
    @Test
    void testGetMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.getMatch(999L));
    }

    @Test
    void testGetMatch_whenMatchExists_thenReturnsMatchWithStages() {
        // Arrange
        Club club = newClub("Test Club", ClubIdentifier.HPSC);
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Club Championship");
        match.setClub(club);
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(match));

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(100L);
        stage.setStageNumber(1);
        stage.setStageName("Stage 1");
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of(stage));

        // Act
        MatchResponse fetched = assertDoesNotThrow(() -> ipscMatchService.getMatch(1L));

        // Assert
        assertEquals(1L, fetched.getMatchId());
        assertEquals("Club Championship", fetched.getMatchName());
        assertEquals(1, fetched.getStages().size());
        assertEquals("Stage 1", fetched.getStages().getFirst().getStageName());
    }

    // patchMatch()
    @Test
    void testPatchMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findById(999L)).thenReturn(Optional.empty());
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
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));
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
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));

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
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubExistingClub("Test Club", ClubIdentifier.HPSC);
        stubMatchSaveReturnsSameEntity();
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of());

        MatchRequest patch = new MatchRequest();
        patch.setClub("Test Club");

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals(ClubIdentifier.HPSC, patched.getClub());
    }

    @Test
    void testPatchMatch_whenMatchDateIsProvided_thenMatchDateChanges() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubMatchSaveReturnsSameEntity();
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of());

        LocalDate newDate = LocalDate.of(2027, 3, 20);
        MatchRequest patch = new MatchRequest();
        patch.setMatchDate(newDate);

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals(newDate, patched.getMatchDate());
    }

    @Test
    void testPatchMatch_whenMatchFirearmTypeIsProvided_thenMatchFirearmTypeIsResolvedAndSet() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        existing.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubMatchSaveReturnsSameEntity();
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of());

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
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));
        stubMatchSaveReturnsSameEntity();
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of());

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
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));

        MatchRequest patch = new MatchRequest();
        patch.setMatchCategory("Not A Match Category");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.patchMatch(1L, patch));
    }

    @Test
    void testPatchMatch_whenOnlyMatchNameIsProvided_thenOnlyMatchNameChangesAndStagesAreUnchanged() {
        // Arrange
        Club club = newClub("Test Club", ClubIdentifier.HPSC);
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        existing.setName("Club Championship");
        existing.setClub(club);
        existing.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        existing.setMatchFirearmType(FirearmType.HANDGUN);
        existing.setMatchCategory(MatchCategory.CLUB_SHOOT);
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ipscMatchRepository.save(any(IpscMatch.class))).thenReturn(existing);

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(100L);
        stage.setStageNumber(1);
        stage.setStageName("Stage 1");
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of(stage));

        MatchRequest patch = new MatchRequest();
        patch.setMatchName("Renamed Championship");

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals("Renamed Championship", patched.getMatchName());
        assertEquals(ClubIdentifier.HPSC, patched.getClub());
        assertEquals(1, patched.getStages().size());
        assertEquals("Stage 1", patched.getStages().getFirst().getStageName());
        verify(ipscMatchStageRepository, never()).save(any(IpscMatchStage.class));
    }

    @Test
    void testPatchMatch_whenStageNumberMatchesExisting_thenUpdatesThatStageInPlace() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        existing.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ipscMatchRepository.save(any(IpscMatch.class))).thenReturn(existing);

        IpscMatchStage existingStage = new IpscMatchStage();
        existingStage.setId(100L);
        existingStage.setMatch(existing);
        existingStage.setStageNumber(1);
        existingStage.setStageName("Original Name");
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L))
                .thenReturn(List.of(existingStage))
                .thenReturn(List.of(existingStage));
        when(ipscMatchStageRepository.save(any(IpscMatchStage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MatchRequest patch = new MatchRequest();
        patch.setStages(List.of(new MatchStageRequest(null, 1, "Updated Name")));

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals(1, patched.getStages().size());
        assertEquals(100L, patched.getStages().getFirst().getStageId());
        assertEquals("Updated Name", patched.getStages().getFirst().getStageName());
    }

    @Test
    void testPatchMatch_whenStageNumberIsNew_thenAddsStageWithoutRemovingExisting() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        existing.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ipscMatchRepository.save(any(IpscMatch.class))).thenReturn(existing);

        IpscMatchStage existingStage = new IpscMatchStage();
        existingStage.setId(100L);
        existingStage.setMatch(existing);
        existingStage.setStageNumber(1);
        existingStage.setStageName("Stage 1");

        IpscMatchStage newStage = new IpscMatchStage();
        newStage.setId(101L);
        newStage.setMatch(existing);
        newStage.setStageNumber(2);
        newStage.setStageName("Stage 2");

        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L))
                .thenReturn(List.of(existingStage))
                .thenReturn(List.of(existingStage, newStage));
        when(ipscMatchStageRepository.save(any(IpscMatchStage.class))).thenAnswer(invocation -> {
            IpscMatchStage stage = invocation.getArgument(0);
            if (stage.getId() == null) {
                stage.setId(101L);
            }
            return stage;
        });

        MatchRequest patch = new MatchRequest();
        patch.setStages(List.of(new MatchStageRequest(null, 2, "Stage 2")));

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(1L, patch));

        // Assert
        assertEquals(2, patched.getStages().size());
        assertEquals("Stage 1", patched.getStages().get(0).getStageName());
        assertEquals("Stage 2", patched.getStages().get(1).getStageName());
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
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class,
                () -> ipscMatchService.updateMatch(1L, validRequest("No Such Club")));
    }

    @Test
    void testUpdateMatch_whenRequestIsValid_thenReplacesAllFieldsAndStages() {
        // Arrange
        IpscMatch existing = new IpscMatch();
        existing.setId(1L);
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(ipscMatchRepository.save(any(IpscMatch.class))).thenReturn(existing);

        Club otherClub = stubExistingClub("Other Club", ClubIdentifier.SOSC);

        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of());

        MatchRequest replacement = new MatchRequest();
        replacement.setMatchName("Different Match");
        replacement.setMatchDate(LocalDate.of(2027, 1, 1));
        replacement.setClub("Other Club");
        replacement.setMatchFirearmType(FirearmType.RIFLE.toString());
        replacement.setMatchCategory(MatchCategory.LEAGUE.toString());

        // Act
        MatchResponse updated = assertDoesNotThrow(() -> ipscMatchService.updateMatch(1L, replacement));

        // Assert
        assertEquals(1L, updated.getMatchId());
        assertEquals("Different Match", updated.getMatchName());
        assertEquals(LocalDate.of(2027, 1, 1), updated.getMatchDate());
        assertEquals(ClubIdentifier.SOSC, updated.getClub());
        assertEquals(FirearmType.RIFLE, updated.getMatchFirearmType());
        assertEquals(MatchCategory.LEAGUE, updated.getMatchCategory());
        assertSame(otherClub, existing.getClub());
        assertTrue(updated.getStages().isEmpty());
    }

    // Helpers
    private MatchRequest validRequest(String club) {
        MatchRequest request = new MatchRequest();
        request.setMatchName("Club Championship");
        request.setMatchDate(LocalDate.of(2026, 9, 12));
        request.setClub(club);
        request.setMatchFirearmType(FirearmType.HANDGUN.toString());
        request.setMatchCategory(MatchCategory.CLUB_SHOOT.toString());
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

    private void stubMatchSaveReturnsSameEntity() {
        when(ipscMatchRepository.save(any(IpscMatch.class))).thenAnswer(invocation -> {
            IpscMatch match = invocation.getArgument(0);
            match.setId(1L);
            return match;
        });
    }

    private void stubStageSaveAssignsIncrementingId() {
        AtomicLong idCounter = new AtomicLong(1);
        when(ipscMatchStageRepository.save(any(IpscMatchStage.class))).thenAnswer(invocation -> {
            IpscMatchStage stage = invocation.getArgument(0);
            stage.setId(idCounter.getAndIncrement());
            return stage;
        });
    }
}
