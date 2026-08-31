package za.co.hpsc.web.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchStageRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.repositories.ClubRepository;

import java.time.LocalDate;
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

    // createMatch()
    @Test
    void testCreateMatch_whenRequestIsNull_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(null));
    }

    @Test
    void testCreateMatch_whenMatchNameIsMissing_thenThrowsValidationException() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");
        request.setMatchName(null);

        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchDateIsMissing_thenThrowsValidationException() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");
        request.setMatchDate(null);

        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenClubIsBlank_thenThrowsValidationException() {
        MatchRequest request = validRequest("  ");

        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchFirearmTypeIsMissing_thenThrowsValidationException() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");
        request.setMatchFirearmType(null);

        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchCategoryIsMissing_thenThrowsValidationException() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");
        request.setMatchCategory(null);

        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchFirearmTypeIsUnrecognised_thenThrowsValidationException() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");
        request.setMatchFirearmType("Not A Firearm Type");

        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenMatchCategoryIsUnrecognised_thenThrowsValidationException() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");
        request.setMatchCategory("Not A Category");

        assertThrows(ValidationException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenClubDoesNotExist_thenThrowsNonFatalException() {
        MatchRequest request = validRequest("No Such Club");

        assertThrows(NonFatalException.class, () -> ipscMatchService.createMatch(request));
    }

    @Test
    void testCreateMatch_whenRequestIsValidWithNoStages_thenPersistsMatchWithEmptyStageList() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");

        MatchResponse response = assertDoesNotThrow(() -> ipscMatchService.createMatch(request));

        assertNotNull(response.getMatchId());
        assertEquals("Club Championship", response.getMatchName());
        assertEquals(LocalDate.of(2026, 9, 12), response.getMatchDate());
        assertEquals(ClubIdentifier.HPSC, response.getClub());
        assertEquals(FirearmType.HANDGUN, response.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, response.getMatchCategory());
        assertTrue(response.getStages().isEmpty());
    }

    @Test
    void testCreateMatch_whenRequestIncludesStages_thenPersistsStagesInOrder() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");
        request.setStages(List.of(
                new MatchStageRequest(null, 1, "Stage 1 - The Bank Job"),
                new MatchStageRequest(null, 2, "Stage 2 - The Getaway")));

        MatchResponse response = assertDoesNotThrow(() -> ipscMatchService.createMatch(request));

        assertEquals(2, response.getStages().size());
        assertNotNull(response.getStages().getFirst().getStageId());
        assertEquals(1, response.getStages().getFirst().getStageNumber());
        assertEquals("Stage 1 - The Bank Job", response.getStages().getFirst().getStageName());
        assertEquals(2, response.getStages().get(1).getStageNumber());
        assertEquals("Stage 2 - The Getaway", response.getStages().get(1).getStageName());
    }

    // getMatch()
    @Test
    void testGetMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        assertThrows(NonFatalException.class, () -> ipscMatchService.getMatch(999L));
    }

    @Test
    void testGetMatch_whenMatchExists_thenReturnsMatchWithStages() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");
        request.setStages(List.of(new MatchStageRequest(null, 1, "Stage 1")));
        MatchResponse created = ipscMatchService.createMatch(request);

        MatchResponse fetched = assertDoesNotThrow(() -> ipscMatchService.getMatch(created.getMatchId()));

        assertEquals(created.getMatchId(), fetched.getMatchId());
        assertEquals("Club Championship", fetched.getMatchName());
        assertEquals(1, fetched.getStages().size());
        assertEquals("Stage 1", fetched.getStages().getFirst().getStageName());
    }

    // getAllMatches()
    @Test
    void testGetAllMatches_whenNoMatchesExist_thenReturnsEmptyList() {
        List<MatchResponse> matches = ipscMatchService.getAllMatches();

        assertTrue(matches.isEmpty());
    }

    @Test
    void testGetAllMatches_whenMatchesExist_thenReturnsAllWithStages() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest firstRequest = validRequest("Test Club");
        firstRequest.setStages(List.of(new MatchStageRequest(null, 1, "Stage 1")));
        MatchResponse first = ipscMatchService.createMatch(firstRequest);

        MatchRequest secondRequest = validRequest("Test Club");
        secondRequest.setMatchName("Second Match");
        MatchResponse second = ipscMatchService.createMatch(secondRequest);

        List<MatchResponse> matches = ipscMatchService.getAllMatches();

        assertEquals(2, matches.size());
        assertTrue(matches.stream().anyMatch(match ->
                match.getMatchId().equals(first.getMatchId()) && (match.getStages().size() == 1)));
        assertTrue(matches.stream().anyMatch(match -> match.getMatchId().equals(second.getMatchId())));
    }

    // patchMatch()
    @Test
    void testPatchMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        MatchRequest request = new MatchRequest();
        request.setMatchName("Renamed");

        assertThrows(NonFatalException.class, () -> ipscMatchService.patchMatch(999L, request));
    }

    @Test
    void testPatchMatch_whenOnlyMatchNameIsProvided_thenOnlyMatchNameChanges() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));

        MatchRequest patch = new MatchRequest();
        patch.setMatchName("Renamed Championship");

        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(created.getMatchId(), patch));

        assertEquals("Renamed Championship", patched.getMatchName());
        assertEquals(LocalDate.of(2026, 9, 12), patched.getMatchDate());
        assertEquals(ClubIdentifier.HPSC, patched.getClub());
        assertEquals(FirearmType.HANDGUN, patched.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, patched.getMatchCategory());
    }

    @Test
    void testPatchMatch_whenClubDoesNotExist_thenThrowsNonFatalException() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));

        MatchRequest patch = new MatchRequest();
        patch.setClub("No Such Club");

        assertThrows(NonFatalException.class, () -> ipscMatchService.patchMatch(created.getMatchId(), patch));
    }

    @Test
    void testPatchMatch_whenMatchFirearmTypeIsUnrecognised_thenThrowsValidationException() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));

        MatchRequest patch = new MatchRequest();
        patch.setMatchFirearmType("Not A Firearm Type");

        assertThrows(ValidationException.class, () -> ipscMatchService.patchMatch(created.getMatchId(), patch));
    }

    @Test
    void testPatchMatch_whenStagesAreOmitted_thenExistingStagesAreUnchanged() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest createRequest = validRequest("Test Club");
        createRequest.setStages(List.of(new MatchStageRequest(null, 1, "Stage 1")));
        MatchResponse created = ipscMatchService.createMatch(createRequest);

        MatchRequest patch = new MatchRequest();
        patch.setMatchName("Renamed Championship");

        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(created.getMatchId(), patch));

        assertEquals(1, patched.getStages().size());
        assertEquals("Stage 1", patched.getStages().getFirst().getStageName());
    }

    @Test
    void testPatchMatch_whenStageNumberMatchesExisting_thenUpdatesThatStageInPlace() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest createRequest = validRequest("Test Club");
        createRequest.setStages(List.of(new MatchStageRequest(null, 1, "Original Name")));
        MatchResponse created = ipscMatchService.createMatch(createRequest);
        Long originalStageId = created.getStages().getFirst().getStageId();

        MatchRequest patch = new MatchRequest();
        patch.setStages(List.of(new MatchStageRequest(null, 1, "Updated Name")));

        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(created.getMatchId(), patch));

        assertEquals(1, patched.getStages().size());
        assertEquals(originalStageId, patched.getStages().getFirst().getStageId());
        assertEquals("Updated Name", patched.getStages().getFirst().getStageName());
    }

    @Test
    void testPatchMatch_whenStageNumberIsNew_thenAddsStageWithoutRemovingExisting() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest createRequest = validRequest("Test Club");
        createRequest.setStages(List.of(new MatchStageRequest(null, 1, "Stage 1")));
        MatchResponse created = ipscMatchService.createMatch(createRequest);

        MatchRequest patch = new MatchRequest();
        patch.setStages(List.of(new MatchStageRequest(null, 2, "Stage 2")));

        MatchResponse patched = assertDoesNotThrow(() -> ipscMatchService.patchMatch(created.getMatchId(), patch));

        assertEquals(2, patched.getStages().size());
        assertEquals("Stage 1", patched.getStages().get(0).getStageName());
        assertEquals("Stage 2", patched.getStages().get(1).getStageName());
    }

    // updateMatch()
    @Test
    void testUpdateMatch_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest request = validRequest("Test Club");

        assertThrows(NonFatalException.class, () -> ipscMatchService.updateMatch(999L, request));
    }

    @Test
    void testUpdateMatch_whenMatchNameIsMissing_thenThrowsValidationException() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));
        MatchRequest request = validRequest("Test Club");
        request.setMatchName(null);

        assertThrows(ValidationException.class, () -> ipscMatchService.updateMatch(created.getMatchId(), request));
    }

    @Test
    void testUpdateMatch_whenClubDoesNotExist_thenThrowsNonFatalException() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));
        MatchRequest request = validRequest("No Such Club");

        assertThrows(NonFatalException.class, () -> ipscMatchService.updateMatch(created.getMatchId(), request));
    }

    @Test
    void testUpdateMatch_whenRequestIsValid_thenReplacesAllFields() {
        createClub("Test Club", ClubIdentifier.HPSC);
        createClub("Other Club", ClubIdentifier.SOSC);
        MatchResponse created = ipscMatchService.createMatch(validRequest("Test Club"));

        MatchRequest replacement = new MatchRequest();
        replacement.setMatchName("Different Match");
        replacement.setMatchDate(LocalDate.of(2027, 1, 1));
        replacement.setClub("Other Club");
        replacement.setMatchFirearmType(FirearmType.RIFLE.toString());
        replacement.setMatchCategory(MatchCategory.LEAGUE.toString());

        MatchResponse updated = assertDoesNotThrow(() -> ipscMatchService.updateMatch(created.getMatchId(), replacement));

        assertEquals(created.getMatchId(), updated.getMatchId());
        assertEquals("Different Match", updated.getMatchName());
        assertEquals(LocalDate.of(2027, 1, 1), updated.getMatchDate());
        assertEquals(ClubIdentifier.SOSC, updated.getClub());
        assertEquals(FirearmType.RIFLE, updated.getMatchFirearmType());
        assertEquals(MatchCategory.LEAGUE, updated.getMatchCategory());
    }

    @Test
    void testUpdateMatch_whenRequestOmitsPreviouslyPersistedStages_thenOldStagesAreRemoved() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest createRequest = validRequest("Test Club");
        createRequest.setStages(List.of(new MatchStageRequest(null, 1, "Stage 1")));
        MatchResponse created = ipscMatchService.createMatch(createRequest);

        MatchRequest replacement = validRequest("Test Club");

        MatchResponse updated = assertDoesNotThrow(() -> ipscMatchService.updateMatch(created.getMatchId(), replacement));

        assertTrue(updated.getStages().isEmpty());
    }

    @Test
    void testUpdateMatch_whenRequestIncludesNewStages_thenOldStagesAreReplaced() {
        createClub("Test Club", ClubIdentifier.HPSC);
        MatchRequest createRequest = validRequest("Test Club");
        createRequest.setStages(List.of(new MatchStageRequest(null, 1, "Original Stage")));
        MatchResponse created = ipscMatchService.createMatch(createRequest);
        Long originalStageId = created.getStages().getFirst().getStageId();

        MatchRequest replacement = validRequest("Test Club");
        replacement.setStages(List.of(new MatchStageRequest(null, 1, "Replacement Stage")));

        MatchResponse updated = assertDoesNotThrow(() -> ipscMatchService.updateMatch(created.getMatchId(), replacement));

        assertEquals(1, updated.getStages().size());
        assertNotEquals(originalStageId, updated.getStages().getFirst().getStageId());
        assertEquals("Replacement Stage", updated.getStages().getFirst().getStageName());
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
        request.setClub(club);
        request.setMatchFirearmType(FirearmType.HANDGUN.toString());
        request.setMatchCategory(MatchCategory.CLUB_SHOOT.toString());
        return request;
    }
}
