package za.co.hpsc.web.services;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchStageRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring-context integration test for {@link IpscMatchService} - exercised through the
 * interface type, with a real Spring-wired {@code IpscMatchServiceImpl} bean backed by the
 * H2 {@code test} profile database. Unlike {@code AwardService}/{@code ImageService}, this
 * target genuinely depends on the datasource and JPA, so those auto-configurations are kept.
 */
@Slf4j
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@SpringBootTest
@Transactional
class IpscMatchServiceIntegrationTest {

    @Autowired
    private IpscMatchService ipscMatchService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private CompetitorRepository competitorRepository;

    @Autowired
    private IpscMatchRepository ipscMatchRepository;

    @Autowired
    private IpscMatchStageRepository ipscMatchStageRepository;

    @Autowired
    private MatchCompetitorRepository matchCompetitorRepository;

    @Autowired
    private EntityManager entityManager;

    // createMatch()
    @Test
    void testCreateMatch_whenRequestIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(null));
    }

    @Test
    void testCreateMatch_whenMatchNameIsMissing_thenThrowsValidationException() {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");
        request.setMatchName(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchDateIsMissing_thenThrowsValidationException() {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");
        request.setMatchDate(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenClubIsBlank_thenDefaultsToDefaultMatchClub() {
        // Arrange
        createClub("Eufees Clubs", IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("  ");

        // Act
        MatchResponse response = assertDoesNotThrow(() -> ipscMatchService.createMatch(request));

        // Assert
        assertEquals(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER, response.getClub());
    }

    @Test
    void testCreateMatch_whenClubIsMissing_thenDefaultsToDefaultMatchClub() {
        // Arrange
        createClub("Eufees Clubs", IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER);
        MatchRequest request = validRequest(null);

        // Act
        MatchResponse response = assertDoesNotThrow(() -> ipscMatchService.createMatch(request));

        // Assert
        assertEquals(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER, response.getClub());
    }

    @Test
    void testCreateMatch_whenClubIsMissingAndDefaultClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        MatchRequest request = validRequest(null);

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchFirearmTypeIsMissing_thenThrowsValidationException() {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");
        request.setMatchFirearmType(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchCategoryIsMissing_thenThrowsValidationException() {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");
        request.setMatchCategory(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchFirearmTypeIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");
        request.setMatchFirearmType("Not A Firearm Type");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchCategoryIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");
        request.setMatchCategory("Not A Category");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        MatchRequest request = validRequest("No Such Club");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenRequestIsValidWithNoStages_thenPersistsMatchWithEmptyStageList() {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");

        // Act
        MatchResponse response = assertDoesNotThrow(() -> ipscMatchService.createMatch(request));

        // Assert
        assertNotNull(response.getMatchId());
        assertEquals("Club Championship", response.getMatchName());
        assertEquals(LocalDate.of(2026, 9, 12), response.getMatchDate());
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, response.getClub());
        assertEquals(FirearmType.HANDGUN, response.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, response.getMatchCategory());
        assertEquals(LocalTime.of(8, 0), response.getStartTime());
        assertEquals(LocalTime.of(17, 0), response.getEndTime());
        assertEquals("https://example.com/matches/1", response.getUrl());
        assertTrue(response.getStages().isEmpty());
    }

    @Test
    void testCreateMatch_whenRequestIncludesStages_thenPersistsStagesInOrder() {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
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

    // deleteMatch()
    @Test
    void testDeleteMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.deleteMatch(999L));
    }

    @Test
    void testDeleteMatch_whenMatchHasNoDependents_thenDeletesMatchAndStages() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");
        request.setStages(List.of(new MatchStageRequest(null, 1, "Stage 1"), new MatchStageRequest(null, 2, "Stage 2")));
        MatchResponse created = ipscMatchService.createMatch(request);

        // Act
        assertDoesNotThrow(() -> ipscMatchService.deleteMatch(created.getMatchId()));

        // Assert
        assertFalse(ipscMatchRepository.existsById(created.getMatchId()));
        assertTrue(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(created.getMatchId()).isEmpty());
        assertThrows(NonFatalException.class, () -> ipscMatchService.getMatch(created.getMatchId()));
    }

    @Test
    void testDeleteMatch_whenMatchHasCompetitorResults_thenThrowsValidationExceptionAndKeepsMatch()
            throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));
        recordCompetitorResult(created.getMatchId());

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.deleteMatch(created.getMatchId()));
        assertTrue(ipscMatchRepository.existsById(created.getMatchId()));
    }

    // getMatch()
    @Test
    void testGetMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.getMatch(999L));
    }

    @Test
    void testGetMatch_whenMatchExists_thenReturnsMatchWithStages() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");
        request.setStages(List.of(new MatchStageRequest(null, 1, "Stage 1")));
        MatchResponse created = ipscMatchService.createMatch(request);

        // Act
        MatchResponse fetched = assertDoesNotThrow(() -> ipscMatchService.getMatch(created.getMatchId()));

        // Assert
        assertEquals(created.getMatchId(), fetched.getMatchId());
        assertEquals("Club Championship", fetched.getMatchName());
        assertEquals(LocalTime.of(8, 0), fetched.getStartTime());
        assertEquals(LocalTime.of(17, 0), fetched.getEndTime());
        assertEquals("https://example.com/matches/1", fetched.getUrl());
        assertEquals(1, fetched.getStages().size());
        assertEquals("Stage 1", fetched.getStages().getFirst().getStageName());
    }

    // getAllMatches()
    @Test
    void testGetAllMatches_whenNoMatchesExist_thenReturnsEmptyList() {
        // Act
        List<MatchResponse> matches = ipscMatchService.getAllMatches();

        // Assert
        assertTrue(matches.isEmpty());
    }

    @Test
    void testGetAllMatches_whenMatchesExist_thenReturnsAllWithStages() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest firstRequest = validRequest("Test Club");
        firstRequest.setStages(List.of(new MatchStageRequest(null, 1, "Stage 1")));
        MatchResponse first = ipscMatchService.createMatch(firstRequest);

        MatchRequest secondRequest = validRequest("Test Club");
        secondRequest.setMatchName("Second Match");
        MatchResponse second = ipscMatchService.createMatch(secondRequest);

        // Act
        List<MatchResponse> matches = ipscMatchService.getAllMatches();

        // Assert
        assertEquals(2, matches.size());
        assertTrue(matches.stream().anyMatch(match ->
                match.getMatchId().equals(first.getMatchId()) && (match.getStages().size() == 1)));
        assertTrue(matches.stream().anyMatch(match -> match.getMatchId().equals(second.getMatchId())));
    }

    // patchMatch()
    @Test
    void testPatchMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        MatchRequest request = new MatchRequest();
        request.setMatchName("Renamed");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.patchMatch(999L, request));
    }

    @Test
    void testPatchMatch_whenOnlyMatchNameIsProvided_thenOnlyMatchNameChanges() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));

        MatchRequest patch = new MatchRequest();
        patch.setMatchName("Renamed Championship");

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(created.getMatchId(), patch));

        // Assert
        assertEquals("Renamed Championship", patched.getMatchName());
        assertEquals(LocalDate.of(2026, 9, 12), patched.getMatchDate());
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, patched.getClub());
        assertEquals(FirearmType.HANDGUN, patched.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, patched.getMatchCategory());
    }

    @Test
    void testPatchMatch_whenStartAndEndTimeAreProvided_thenStartAndEndTimeChange() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));

        LocalTime newStartTime = LocalTime.of(9, 30);
        LocalTime newEndTime = LocalTime.of(18, 30);
        MatchRequest patch = new MatchRequest();
        patch.setStartTime(newStartTime);
        patch.setEndTime(newEndTime);

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(created.getMatchId(), patch));

        // Assert
        assertEquals(newStartTime, patched.getStartTime());
        assertEquals(newEndTime, patched.getEndTime());
        assertEquals("Club Championship", patched.getMatchName());
    }

    @Test
    void testPatchMatch_whenUrlIsProvided_thenUrlChanges() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));

        MatchRequest patch = new MatchRequest();
        patch.setUrl("https://example.com/matches/updated");

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(created.getMatchId(), patch));

        // Assert
        assertEquals("https://example.com/matches/updated", patched.getUrl());
        assertEquals("Club Championship", patched.getMatchName());
    }

    @Test
    void testPatchMatch_whenClubDoesNotExist_thenThrowsNonFatalException() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));

        MatchRequest patch = new MatchRequest();
        patch.setClub("No Such Club");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.patchMatch(created.getMatchId(), patch));
    }

    @Test
    void testPatchMatch_whenMatchFirearmTypeIsUnrecognised_thenThrowsValidationException() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));

        MatchRequest patch = new MatchRequest();
        patch.setMatchFirearmType("Not A Firearm Type");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.patchMatch(created.getMatchId(), patch));
    }

    @Test
    void testPatchMatch_whenStagesAreOmitted_thenExistingStagesAreUnchanged() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest createRequest = validRequest("Test Club");
        createRequest.setStages(List.of(new MatchStageRequest(null, 1, "Stage 1")));
        MatchResponse created = ipscMatchService.createMatch(createRequest);

        MatchRequest patch = new MatchRequest();
        patch.setMatchName("Renamed Championship");

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(created.getMatchId(), patch));

        // Assert
        assertEquals(1, patched.getStages().size());
        assertEquals("Stage 1", patched.getStages().getFirst().getStageName());
    }

    @Test
    void testPatchMatch_whenStageNumberMatchesExisting_thenUpdatesThatStageInPlace() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest createRequest = validRequest("Test Club");
        createRequest.setStages(List.of(new MatchStageRequest(null, 1, "Original Name")));
        MatchResponse created = ipscMatchService.createMatch(createRequest);
        Long originalStageId = created.getStages().getFirst().getStageId();

        MatchRequest patch = new MatchRequest();
        patch.setStages(List.of(new MatchStageRequest(null, 1, "Updated Name")));

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(created.getMatchId(), patch));

        // Assert
        assertEquals(1, patched.getStages().size());
        assertEquals(originalStageId, patched.getStages().getFirst().getStageId());
        assertEquals("Updated Name", patched.getStages().getFirst().getStageName());
    }

    @Test
    void testPatchMatch_whenStageNumberIsNew_thenAddsStageWithoutRemovingExisting() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest createRequest = validRequest("Test Club");
        createRequest.setStages(List.of(new MatchStageRequest(null, 1, "Stage 1")));
        MatchResponse created = ipscMatchService.createMatch(createRequest);

        MatchRequest patch = new MatchRequest();
        patch.setStages(List.of(new MatchStageRequest(null, 2, "Stage 2")));

        // Act
        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(created.getMatchId(), patch));

        // Assert
        assertEquals(2, patched.getStages().size());
        assertEquals("Stage 1", patched.getStages().get(0).getStageName());
        assertEquals("Stage 2", patched.getStages().get(1).getStageName());
    }

    // updateMatch()
    @Test
    void testUpdateMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest request = validRequest("Test Club");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.updateMatch(999L, request));
    }

    @Test
    void testUpdateMatch_whenMatchNameIsMissing_thenThrowsValidationException() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));
        MatchRequest request = validRequest("Test Club");
        request.setMatchName(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchService.updateMatch(created.getMatchId(), request));
    }

    @Test
    void testUpdateMatch_whenClubDoesNotExist_thenThrowsNonFatalException() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));
        MatchRequest request = validRequest("No Such Club");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchService.updateMatch(created.getMatchId(), request));
    }

    @Test
    void testUpdateMatch_whenRequestIsValid_thenReplacesAllFields() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        createClub("Other Club", ClubIdentifier.SOSC);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));

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
        MatchResponse updated = assertDoesNotThrow(() -> ipscMatchService.updateMatch(created.getMatchId(), replacement));

        // Assert
        assertEquals(created.getMatchId(), updated.getMatchId());
        assertEquals("Different Match", updated.getMatchName());
        assertEquals(LocalDate.of(2027, 1, 1), updated.getMatchDate());
        assertEquals(ClubIdentifier.SOSC, updated.getClub());
        assertEquals(FirearmType.RIFLE, updated.getMatchFirearmType());
        assertEquals(MatchCategory.LEAGUE, updated.getMatchCategory());
        assertEquals(LocalTime.of(10, 0), updated.getStartTime());
        assertEquals(LocalTime.of(16, 0), updated.getEndTime());
        assertEquals("https://example.com/matches/different", updated.getUrl());
    }

    @Test
    void testUpdateMatch_whenRequestOmitsPreviouslyPersistedStages_thenOldStagesAreRemoved() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest createRequest = validRequest("Test Club");
        createRequest.setStages(List.of(new MatchStageRequest(null, 1, "Stage 1")));
        MatchResponse created = ipscMatchService.createMatch(createRequest);

        MatchRequest replacement = validRequest("Test Club");

        // Act
        MatchResponse updated = assertDoesNotThrow(() -> ipscMatchService.updateMatch(created.getMatchId(), replacement));

        // Assert
        assertTrue(updated.getStages().isEmpty());
    }

    @Test
    void testUpdateMatch_whenRequestIncludesNewStages_thenOldStagesAreReplaced() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchRequest createRequest = validRequest("Test Club");
        createRequest.setStages(List.of(new MatchStageRequest(null, 1, "Original Stage")));
        MatchResponse created = ipscMatchService.createMatch(createRequest);
        Long originalStageId = created.getStages().getFirst().getStageId();

        MatchRequest replacement = validRequest("Test Club");
        replacement.setStages(List.of(new MatchStageRequest(null, 1, "Replacement Stage")));

        // Act
        MatchResponse updated = assertDoesNotThrow(() -> ipscMatchService.updateMatch(created.getMatchId(), replacement));

        // Assert
        assertEquals(1, updated.getStages().size());
        assertNotEquals(originalStageId, updated.getStages().getFirst().getStageId());
        assertEquals("Replacement Stage", updated.getStages().getFirst().getStageName());
    }

    // Without a surrounding transaction
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testMatchLifecycle_whenNoTransactionIsActive_thenEachWriteIsCommittedAndReadableAfterwards()
            throws FatalException {
        // Each service call below runs without this test's usual rolled-back transaction, so every
        // write must be committed by TransactionService and every read must work on its own.
        Long matchId = null;
        try {
            // Arrange
            createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
            MatchRequest request = validRequest("Test Club");
            request.setStages(List.of(new MatchStageRequest(null, 2, "Stage 2"), new MatchStageRequest(null, 1, "Stage 1")));

            // Act & Assert - create, then read back in a separate call
            matchId = ipscMatchService.createMatch(request).getMatchId();
            MatchResponse fetched = ipscMatchService.getMatch(matchId);
            assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, fetched.getClub());
            assertEquals(List.of("Stage 1", "Stage 2"),
                    fetched.getStages().stream().map(stage -> stage.getStageName()).toList());

            // Act & Assert - replace, reusing a stage number
            MatchRequest update = validRequest("Test Club");
            update.setStages(List.of(new MatchStageRequest(null, 1, "Replacement Stage")));
            ipscMatchService.updateMatch(matchId, update);
            assertEquals(List.of("Replacement Stage"),
                    ipscMatchService.getMatch(matchId).getStages().stream().map(stage -> stage.getStageName()).toList());

            // Act & Assert - upsert a new stage alongside the existing one
            MatchRequest patch = new MatchRequest();
            patch.setStages(List.of(new MatchStageRequest(null, 2, "Added Stage")));
            ipscMatchService.patchMatch(matchId, patch);
            assertEquals(List.of("Replacement Stage", "Added Stage"),
                    ipscMatchService.getMatch(matchId).getStages().stream().map(stage -> stage.getStageName()).toList());

            // Act & Assert - delete
            ipscMatchService.deleteMatch(matchId);
            assertFalse(ipscMatchRepository.existsById(matchId));
            matchId = null;
        } finally {
            if (matchId != null) {
                ipscMatchService.deleteMatch(matchId);
            }
            clubRepository.findByName("Test Club").ifPresent(clubRepository::delete);
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testCreateMatches_whenNoTransactionIsActiveAndARowIsInvalid_thenNoRowIsCommitted() {
        try {
            // Arrange
            createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
            String csvData = """
                    MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,Stages,StartTime,EndTime,Url
                    2026-09-12,First Match,Test Club,%1$s,%2$s,,,,
                    2026-09-13,Second Match,No Such Club,%1$s,%2$s,,,,
                    """.formatted(FirearmType.HANDGUN, MatchCategory.CLUB_SHOOT);

            // Act & Assert
            assertThrows(NonFatalException.class, () -> ipscMatchService.createMatches(csvData));
            assertTrue(ipscMatchService.getAllMatches().isEmpty());
        } finally {
            clubRepository.findByName("Test Club").ifPresent(clubRepository::delete);
        }
    }

    // IpscMatchRepository.findByIdWithClub()
    @Test
    void testFindByIdWithClub_whenMatchExists_thenClubIsFetchedWithMatch() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));
        entityManager.flush();
        entityManager.clear();

        // Act
        IpscMatch match = ipscMatchRepository.findByIdWithClub(created.getMatchId()).orElseThrow();

        // Assert
        assertTrue(Hibernate.isInitialized(match.getClub()));
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, match.getClub().getIdentifier());
    }

    // IpscMatchRepository.findAllWithClub()
    @Test
    void testFindAllWithClub_whenMatchesExist_thenEachClubIsFetchedWithItsMatch() throws FatalException {
        // Arrange
        createClub("Test Club", IpscConstants.HOME_CLUB_IDENTIFIER);
        ipscMatchService.createMatch(validRequest("Test Club"));
        MatchRequest secondRequest = validRequest("Test Club");
        secondRequest.setMatchName("Second Match");
        ipscMatchService.createMatch(secondRequest);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<IpscMatch> matches = ipscMatchRepository.findAllWithClub();

        // Assert
        assertEquals(2, matches.size());
        assertTrue(matches.stream().allMatch(match -> Hibernate.isInitialized(match.getClub())));
    }

    // Helpers
    private void createClub(String name, ClubIdentifier identifier) {
        Club club = new Club();
        club.setName(name);
        club.setIdentifier(identifier);
        clubRepository.save(club);
    }

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

    private void recordCompetitorResult(Long matchId) {
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setLastName("Doe");
        competitor = competitorRepository.save(competitor);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setCompetitor(competitor);
        matchCompetitor.setMatch(ipscMatchRepository.findById(matchId).orElseThrow());
        matchCompetitor.setFirearmType(FirearmType.HANDGUN);
        matchCompetitorRepository.save(matchCompetitor);
    }
}
