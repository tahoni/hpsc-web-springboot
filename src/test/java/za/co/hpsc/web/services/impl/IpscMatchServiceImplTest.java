package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link IpscMatchServiceImpl}'s impl-only protected helper methods
 * ({@code applyFields}, {@code findMatchOrThrow}, {@code replaceStages}, {@code resolveClub},
 * {@code resolveFirearmType}, {@code resolveMatchCategory}, {@code toResponse},
 * {@code upsertStages}, {@code validateForCreate}) - not declared on
 * {@link za.co.hpsc.web.services.IpscMatchService}. The interface's
 * create/update/patch/get/get-all contract is covered by
 * {@link za.co.hpsc.web.services.IpscMatchServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
class IpscMatchServiceImplTest {

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private IpscMatchStageRepository ipscMatchStageRepository;

    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private IpscMatchServiceImpl ipscMatchServiceImpl;

    // applyFields()
    @Test
    void testApplyFields_whenRequestIsValid_thenCopiesAllFieldsOntoMatch() {
        // Arrange
        Club club = new Club();
        club.setId(10L);
        club.setName("Test Club");
        club.setIdentifier(ClubIdentifier.HPSC);
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));
        MatchRequest request = validRequest("Test Club");
        IpscMatch match = new IpscMatch();

        // Act
        ipscMatchServiceImpl.applyFields(match, request);

        // Assert
        assertSame(club, match.getClub());
        assertEquals("Club Championship", match.getName());
        assertEquals(LocalDate.of(2026, 9, 12).atStartOfDay(), match.getScheduledDate());
        assertEquals(FirearmType.HANDGUN, match.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, match.getMatchCategory());
    }

    @Test
    void testApplyFields_whenClubDoesNotExist_thenThrowsNonFatalException() {
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());
        MatchRequest request = validRequest("No Such Club");

        assertThrows(NonFatalException.class, () -> ipscMatchServiceImpl.applyFields(new IpscMatch(), request));
    }

    @Test
    void testApplyFields_whenFirearmTypeIsUnrecognised_thenThrowsValidationException() {
        Club club = new Club();
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));
        MatchRequest request = validRequest("Test Club");
        request.setMatchFirearmType("Not A Firearm Type");

        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.applyFields(new IpscMatch(), request));
    }

    @Test
    void testApplyFields_whenMatchCategoryIsUnrecognised_thenThrowsValidationException() {
        Club club = new Club();
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));
        MatchRequest request = validRequest("Test Club");
        request.setMatchCategory("Not A Category");

        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.applyFields(new IpscMatch(), request));
    }

    // findMatchOrThrow()
    @Test
    void testFindMatchOrThrow_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        when(ipscMatchRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NonFatalException.class, () -> ipscMatchServiceImpl.findMatchOrThrow(999L));
    }

    @Test
    void testFindMatchOrThrow_whenMatchExists_thenReturnsMatch() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        when(ipscMatchRepository.findById(1L)).thenReturn(Optional.of(match));

        // Act
        IpscMatch found = assertDoesNotThrow(() -> ipscMatchServiceImpl.findMatchOrThrow(1L));

        // Assert
        assertSame(match, found);
    }

    // replaceStages()
    @Test
    void testReplaceStages_whenStageRequestsIsNull_thenDeletesExistingAndReturnsEmptyList() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        IpscMatchStage existingStage = new IpscMatchStage();
        existingStage.setId(100L);
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of(existingStage));

        // Act
        List<IpscMatchStage> result = ipscMatchServiceImpl.replaceStages(match, null);

        // Assert
        assertTrue(result.isEmpty());
        verify(ipscMatchStageRepository, times(1)).deleteAllInBatch(List.of(existingStage));
    }

    @Test
    void testReplaceStages_whenStageRequestsIsEmpty_thenReturnsEmptyListWithoutSaving() {
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of());

        List<IpscMatchStage> result = ipscMatchServiceImpl.replaceStages(match, List.of());

        assertTrue(result.isEmpty());
        verify(ipscMatchStageRepository, never()).save(any(IpscMatchStage.class));
    }

    @Test
    void testReplaceStages_whenStageRequestsProvided_thenPersistsEachAndReturnsInOrder() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L)).thenReturn(List.of());
        when(ipscMatchStageRepository.save(any(IpscMatchStage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<MatchStageRequest> stageRequests = List.of(
                new MatchStageRequest(null, 1, "Stage 1"),
                new MatchStageRequest(null, 2, "Stage 2"));

        // Act
        List<IpscMatchStage> result = ipscMatchServiceImpl.replaceStages(match, stageRequests);

        // Assert
        assertEquals(2, result.size());
        assertSame(match, result.getFirst().getMatch());
        assertEquals(1, result.get(0).getStageNumber());
        assertEquals("Stage 1", result.get(0).getStageName());
        assertEquals(2, result.get(1).getStageNumber());
        assertEquals("Stage 2", result.get(1).getStageName());
        verify(ipscMatchStageRepository, times(2)).save(any(IpscMatchStage.class));
    }

    // resolveClub()
    @Test
    void testResolveClub_whenClubExists_thenReturnsClub() {
        // Arrange
        Club club = new Club();
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));

        // Act
        Club resolved = assertDoesNotThrow(() -> ipscMatchServiceImpl.resolveClub("Test Club"));

        // Assert
        assertSame(club, resolved);
    }

    @Test
    void testResolveClub_whenClubDoesNotExist_thenThrowsNonFatalException() {
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        assertThrows(NonFatalException.class, () -> ipscMatchServiceImpl.resolveClub("No Such Club"));
    }

    // resolveFirearmType()
    @Test
    void testResolveFirearmType_whenFirearmTypeIsValid_thenReturnsMatchingFirearmType() {
        assertEquals(FirearmType.HANDGUN, ipscMatchServiceImpl.resolveFirearmType(FirearmType.HANDGUN.toString()));
    }

    @Test
    void testResolveFirearmType_whenFirearmTypeIsUnrecognised_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.resolveFirearmType("Not A Firearm Type"));
    }

    // resolveMatchCategory()
    @Test
    void testResolveMatchCategory_whenMatchCategoryIsValid_thenReturnsMatchingCategory() {
        assertEquals(MatchCategory.CLUB_SHOOT, ipscMatchServiceImpl.resolveMatchCategory(MatchCategory.CLUB_SHOOT.toString()));
    }

    @Test
    void testResolveMatchCategory_whenMatchCategoryIsUnrecognised_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.resolveMatchCategory("Not A Category"));
    }

    // toResponse()
    @Test
    void testToResponse_whenMatchHasClub_thenMapsClubIdentifier() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(ClubIdentifier.HPSC);
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Club Championship");
        match.setClub(club);
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);

        // Act
        MatchResponse response = ipscMatchServiceImpl.toResponse(match, List.of());

        // Assert
        assertEquals(1L, response.getMatchId());
        assertEquals("Club Championship", response.getMatchName());
        assertEquals(LocalDate.of(2026, 9, 12), response.getMatchDate());
        assertEquals(ClubIdentifier.HPSC, response.getClub());
        assertEquals(FirearmType.HANDGUN, response.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, response.getMatchCategory());
    }

    @Test
    void testToResponse_whenMatchHasNoClub_thenClubIsNull() {
        IpscMatch match = new IpscMatch();
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());

        MatchResponse response = ipscMatchServiceImpl.toResponse(match, List.of());

        assertNull(response.getClub());
    }

    @Test
    void testToResponse_whenStagesProvided_thenMapsStagesInOrder() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());

        IpscMatchStage stage1 = new IpscMatchStage();
        stage1.setId(100L);
        stage1.setStageNumber(1);
        stage1.setStageName("Stage 1");
        IpscMatchStage stage2 = new IpscMatchStage();
        stage2.setId(101L);
        stage2.setStageNumber(2);
        stage2.setStageName("Stage 2");

        // Act
        MatchResponse response = ipscMatchServiceImpl.toResponse(match, List.of(stage1, stage2));

        // Assert
        assertEquals(2, response.getStages().size());
        assertEquals(100L, response.getStages().get(0).getStageId());
        assertEquals("Stage 1", response.getStages().get(0).getStageName());
        assertEquals(101L, response.getStages().get(1).getStageId());
        assertEquals("Stage 2", response.getStages().get(1).getStageName());
    }

    // upsertStages()
    @Test
    void testUpsertStages_whenStageNumberMatchesExisting_thenUpdatesThatStageInPlace() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        IpscMatchStage existingStage = new IpscMatchStage();
        existingStage.setId(100L);
        existingStage.setStageNumber(1);
        existingStage.setStageName("Original Name");
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L))
                .thenReturn(List.of(existingStage));
        when(ipscMatchStageRepository.save(any(IpscMatchStage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<IpscMatchStage> result = ipscMatchServiceImpl.upsertStages(match,
                List.of(new MatchStageRequest(null, 1, "Updated Name")));

        // Assert
        assertEquals(1, result.size());
        assertSame(existingStage, result.getFirst());
        assertEquals("Updated Name", existingStage.getStageName());
        verify(ipscMatchStageRepository, times(1)).save(existingStage);
    }

    @Test
    void testUpsertStages_whenStageNumberIsNew_thenAddsStageWithoutRemovingExisting() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        IpscMatchStage existingStage = new IpscMatchStage();
        existingStage.setId(100L);
        existingStage.setStageNumber(1);
        existingStage.setStageName("Stage 1");
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L))
                .thenReturn(List.of(existingStage));
        ArgumentCaptor<IpscMatchStage> savedStageCaptor = ArgumentCaptor.forClass(IpscMatchStage.class);
        when(ipscMatchStageRepository.save(savedStageCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ipscMatchServiceImpl.upsertStages(match, List.of(new MatchStageRequest(null, 2, "Stage 2")));

        // Assert
        assertEquals("Stage 1", existingStage.getStageName());
        IpscMatchStage savedStage = savedStageCaptor.getValue();
        assertSame(match, savedStage.getMatch());
        assertEquals(2, savedStage.getStageNumber());
        assertEquals("Stage 2", savedStage.getStageName());
    }

    @Test
    void testUpsertStages_whenStageNameIsNullOnRequest_thenExistingStageNameIsUnchanged() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        IpscMatchStage existingStage = new IpscMatchStage();
        existingStage.setId(100L);
        existingStage.setStageNumber(1);
        existingStage.setStageName("Original Name");
        when(ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(1L))
                .thenReturn(List.of(existingStage));
        when(ipscMatchStageRepository.save(any(IpscMatchStage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ipscMatchServiceImpl.upsertStages(match, List.of(new MatchStageRequest(null, 1, null)));

        // Assert
        assertEquals("Original Name", existingStage.getStageName());
    }

    // validateForCreate()
    @Test
    void testValidateForCreate_whenRequestIsNull_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.validateForCreate(null));
    }

    @Test
    void testValidateForCreate_whenMatchNameIsBlank_thenThrowsValidationException() {
        MatchRequest request = validRequest("Test Club");
        request.setMatchName("  ");

        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenMatchDateIsMissing_thenThrowsValidationException() {
        MatchRequest request = validRequest("Test Club");
        request.setMatchDate(null);

        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenClubIsBlank_thenThrowsValidationException() {
        MatchRequest request = validRequest("  ");

        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenMatchFirearmTypeIsBlank_thenThrowsValidationException() {
        MatchRequest request = validRequest("Test Club");
        request.setMatchFirearmType("  ");

        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenMatchCategoryIsBlank_thenThrowsValidationException() {
        MatchRequest request = validRequest("Test Club");
        request.setMatchCategory("  ");

        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenRequestIsValid_thenDoesNotThrow() {
        assertDoesNotThrow(() -> ipscMatchServiceImpl.validateForCreate(validRequest("Test Club")));
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
}
