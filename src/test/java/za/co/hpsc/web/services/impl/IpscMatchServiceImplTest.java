package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequestCsv;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link IpscMatchServiceImpl}'s impl-only protected helper methods
 * ({@code applyFields}, {@code findMatchOrThrow}, {@code newMatch}, {@code readMatches},
 * {@code resolveClub}, {@code resolveFirearmType}, {@code resolveMatchCategory},
 * {@code toRequest}, {@code toResponse}, {@code validateForCreate}) - not declared on
 * {@link za.co.hpsc.web.services.IpscMatchService}.
 * The interface's create/update/patch/get/get-all contract is covered by
 * {@link za.co.hpsc.web.services.IpscMatchServiceTest}.
 */
@ExtendWith(MockitoExtension.class)
class IpscMatchServiceImplTest {

    @Mock
    private IpscMatchRepository ipscMatchRepository;

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
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));
        MatchRequest request = validRequest("Test Club");
        IpscMatch match = new IpscMatch();

        // Act
        assertDoesNotThrow(() -> ipscMatchServiceImpl.applyFields(match, request));

        // Assert
        assertSame(club, match.getClub());
        assertEquals("Club Championship", match.getName());
        assertEquals(LocalDate.of(2026, 9, 12).atStartOfDay(), match.getScheduledDate());
        assertEquals(LocalTime.of(8, 0), match.getStartTime());
        assertEquals(LocalTime.of(17, 0), match.getEndTime());
        assertEquals(FirearmType.HANDGUN, match.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, match.getMatchCategory());
        assertEquals("https://example.com/matches/1", match.getUrl());
    }

    @Test
    void testApplyFields_whenClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());
        MatchRequest request = validRequest("No Such Club");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchServiceImpl.applyFields(new IpscMatch(), request));
    }

    @Test
    void testApplyFields_whenFirearmTypeIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        Club club = new Club();
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));
        MatchRequest request = validRequest("Test Club");
        request.setMatchFirearmType("Not A Firearm Type");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.applyFields(new IpscMatch(), request));
    }

    @Test
    void testApplyFields_whenMatchCategoryIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        Club club = new Club();
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));
        MatchRequest request = validRequest("Test Club");
        request.setMatchCategory("Not A Category");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.applyFields(new IpscMatch(), request));
    }

    // findMatchOrThrow()
    @Test
    void testFindMatchOrThrow_whenMatchDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(ipscMatchRepository.findByIdWithClub(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchServiceImpl.findMatchOrThrow(999L));
    }

    @Test
    void testFindMatchOrThrow_whenMatchExists_thenReturnsMatch() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        when(ipscMatchRepository.findByIdWithClub(1L)).thenReturn(Optional.of(match));

        // Act
        IpscMatch found = assertDoesNotThrow(() -> ipscMatchServiceImpl.findMatchOrThrow(1L));

        // Assert
        assertSame(match, found);
    }

    // newMatch()
    @Test
    void testNewMatch_whenRequestIsValid_thenBuildsUnpersistedMatchFromRequest() {
        // Arrange
        Club club = new Club();
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));

        // Act
        IpscMatch match = assertDoesNotThrow(() -> ipscMatchServiceImpl.newMatch(validRequest("Test Club")));

        // Assert
        assertNull(match.getId());
        assertSame(club, match.getClub());
        assertEquals("Club Championship", match.getName());
    }

    @Test
    void testNewMatch_whenRequestIsInvalid_thenThrowsValidationExceptionWithoutResolvingClub() {
        // Arrange
        MatchRequest request = validRequest("Test Club");
        request.setMatchName(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.newMatch(request));
        verifyNoInteractions(clubRepository);
    }

    // readMatches()
    @Test
    void testReadMatches_whenValidCsv_thenReturnsMatchRequestForCSVList() {
        // Arrange
        String csvData = """
                MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,StartTime,EndTime,Url
                2026-04-10,Club Championship,Test Club,Pistol,Level 1
                2026-04-17,Second Match
                """;

        // Act
        List<MatchRequestCsv> rows = assertDoesNotThrow(() -> ipscMatchServiceImpl.readMatches(csvData));

        // Assert
        assertEquals(2, rows.size());

        MatchRequestCsv first = rows.getFirst();
        assertEquals(LocalDate.of(2026, 4, 10), first.getMatchDate());
        assertEquals("Club Championship", first.getMatchName());
        assertEquals("Test Club", first.getClub());
        assertEquals("Pistol", first.getMatchFirearmType());
        assertEquals("Level 1", first.getMatchCategory());

        MatchRequestCsv second = rows.get(1);
        assertEquals("Second Match", second.getMatchName());
        assertNull(second.getClub());
    }

    @Test
    void testReadMatches_whenColumnsAreReordered_thenMapsAllFieldsCorrectly() {
        // Arrange
        String csvData = """
                MatchName,MatchDate,Club,MatchFirearmType,MatchCategory,StartTime,EndTime,Url
                Club Championship,2026-04-10,Test Club,,
                """;

        // Act
        List<MatchRequestCsv> rows = assertDoesNotThrow(() -> ipscMatchServiceImpl.readMatches(csvData));

        // Assert
        assertEquals(1, rows.size());
        assertEquals("Club Championship", rows.getFirst().getMatchName());
        assertEquals(LocalDate.of(2026, 4, 10), rows.getFirst().getMatchDate());
        assertEquals("Test Club", rows.getFirst().getClub());
    }

    @Test
    void testReadMatches_whenHeaderOnlyWithNoDataRows_thenReturnsEmptyList() {
        // Arrange
        String csvData = "MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,StartTime,EndTime,Url\n";

        // Act
        List<MatchRequestCsv> rows = assertDoesNotThrow(() -> ipscMatchServiceImpl.readMatches(csvData));

        // Assert
        assertTrue(rows.isEmpty());
    }

    @Test
    void testReadMatches_whenHeaderIsMissingColumns_thenThrowsValidationException() {
        // Arrange
        String csvData = "MatchDate,MatchName\n2026-04-10,Club Championship\n";

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.readMatches(csvData));
    }

    @Test
    void testReadMatches_whenCsvHasNoHeaderRow_thenThrowsValidationException() {
        // Arrange
        String csvData = "Invalid CSV With One Column and no Header";

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.readMatches(csvData));
    }

    @Test
    void testReadMatches_whenCsvDataIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.readMatches(null));
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
        // Arrange
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchServiceImpl.resolveClub("No Such Club"));
    }

    @Test
    void testResolveClub_whenClubNameIsNull_thenReturnsDefaultMatchClub() {
        // Arrange
        Club defaultClub = new Club();
        defaultClub.setIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER);
        when(clubRepository.findByIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER))
                .thenReturn(Optional.of(defaultClub));

        // Act
        Club resolved = assertDoesNotThrow(() -> ipscMatchServiceImpl.resolveClub(null));

        // Assert
        assertSame(defaultClub, resolved);
    }

    @Test
    void testResolveClub_whenClubNameIsBlank_thenReturnsDefaultMatchClub() {
        // Arrange
        Club defaultClub = new Club();
        defaultClub.setIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER);
        when(clubRepository.findByIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER))
                .thenReturn(Optional.of(defaultClub));

        // Act
        Club resolved = assertDoesNotThrow(() -> ipscMatchServiceImpl.resolveClub("  "));

        // Assert
        assertSame(defaultClub, resolved);
    }

    @Test
    void testResolveClub_whenClubNameIsNullAndDefaultClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(clubRepository.findByIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> ipscMatchServiceImpl.resolveClub(null));
    }

    @Test
    void testResolveClub_whenClubNameIsNullAndDefaultIdentifierIsNull_thenThrowsFatalException() {
        // Act & Assert
        assertThrows(FatalException.class, () -> ipscMatchServiceImpl.resolveClub(null, null));
    }

    @Test
    void testResolveClub_whenClubNameIsBlankAndDefaultIdentifierIsNull_thenThrowsFatalException() {
        // Act & Assert
        assertThrows(FatalException.class, () -> ipscMatchServiceImpl.resolveClub("  ", null));
    }

    @Test
    void testResolveClub_whenClubNameIsSuppliedAndDefaultIdentifierIsNull_thenIgnoresDefaultIdentifier() {
        // Arrange
        Club club = new Club();
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));

        // Act
        Club resolved = assertDoesNotThrow(() -> ipscMatchServiceImpl.resolveClub("Test Club", null));

        // Assert
        assertSame(club, resolved);
    }

    // resolveFirearmType()
    @Test
    void testResolveFirearmType_whenFirearmTypeIsValid_thenReturnsMatchingFirearmType() {
        assertEquals(FirearmType.HANDGUN, ipscMatchServiceImpl.resolveFirearmType(FirearmType.HANDGUN.toString()));
    }

    @Test
    void testResolveFirearmType_whenFirearmTypeIsUnrecognised_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.resolveFirearmType("Not A Firearm Type"));
    }

    // resolveMatchCategory()
    @Test
    void testResolveMatchCategory_whenMatchCategoryIsValid_thenReturnsMatchingCategory() {
        assertEquals(MatchCategory.CLUB_SHOOT, ipscMatchServiceImpl.resolveMatchCategory(MatchCategory.CLUB_SHOOT.toString()));
    }

    @Test
    void testResolveMatchCategory_whenMatchCategoryIsUnrecognised_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.resolveMatchCategory("Not A Category"));
    }

    // toRequest()
    @Test
    void testToRequest_whenAllFieldsPresent_thenMapsAllFieldsOntoMatchRequest() {
        // Arrange
        MatchRequestCsv matchRequestCsv = new MatchRequestCsv(
                LocalDate.of(2026, 4, 10), "Club Championship",
                "Test Club", "Pistol", "Level 1", LocalTime.of(8, 0), LocalTime.of(17, 0),
                "https://example.com/matches/1"
        );

        // Act
        MatchRequest request = ipscMatchServiceImpl.toRequest(matchRequestCsv);

        // Assert
        assertNull(request.getMatchId());
        assertEquals(LocalDate.of(2026, 4, 10), request.getMatchDate());
        assertEquals("Club Championship", request.getMatchName());
        assertEquals(LocalTime.of(8, 0), request.getStartTime());
        assertEquals(LocalTime.of(17, 0), request.getEndTime());
        assertEquals("Test Club", request.getClub());
        assertEquals("Pistol", request.getMatchFirearmType());
        assertEquals("Level 1", request.getMatchCategory());
        assertEquals("https://example.com/matches/1", request.getUrl());
    }

    @Test
    void testToRequest_whenOptionalFieldsAreNull_thenMapsNullsThrough() {
        // Arrange
        MatchRequestCsv matchRequestCsv = new MatchRequestCsv(
                LocalDate.of(2026, 4, 10), "Club Championship", null, null, null, null, null, null);

        // Act
        MatchRequest request = ipscMatchServiceImpl.toRequest(matchRequestCsv);

        // Assert
        assertNull(request.getMatchId());
        assertNull(request.getStartTime());
        assertNull(request.getEndTime());
        assertNull(request.getClub());
        assertNull(request.getMatchFirearmType());
        assertNull(request.getMatchCategory());
        assertNull(request.getUrl());
    }

    // toResponse()
    @Test
    void testToResponse_whenMatchHasClub_thenMapsClubIdentifier() {
        // Arrange
        Club club = new Club();
        club.setIdentifier(IpscConstants.HOME_CLUB_IDENTIFIER);
        IpscMatch match = new IpscMatch();
        match.setId(1L);
        match.setName("Club Championship");
        match.setClub(club);
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        match.setStartTime(LocalTime.of(8, 0));
        match.setEndTime(LocalTime.of(17, 0));
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match.setUrl("https://example.com/matches/1");

        // Act
        MatchResponse response = ipscMatchServiceImpl.toResponse(match);

        // Assert
        assertEquals(1L, response.getMatchId());
        assertEquals("Club Championship", response.getMatchName());
        assertEquals(LocalDate.of(2026, 9, 12), response.getMatchDate());
        assertEquals(LocalTime.of(8, 0), response.getStartTime());
        assertEquals(LocalTime.of(17, 0), response.getEndTime());
        assertEquals(IpscConstants.HOME_CLUB_IDENTIFIER, response.getClub());
        assertEquals(FirearmType.HANDGUN, response.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, response.getMatchCategory());
        assertEquals("https://example.com/matches/1", response.getUrl());
    }

    @Test
    void testToResponse_whenMatchHasNoClub_thenClubIsNull() {
        IpscMatch match = new IpscMatch();
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());

        MatchResponse response = ipscMatchServiceImpl.toResponse(match);

        assertNull(response.getClub());
    }

    // validateForCreate()
    @Test
    void testValidateForCreate_whenRequestIsNull_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.validateForCreate(null));
    }

    @Test
    void testValidateForCreate_whenMatchNameIsBlank_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest("Test Club");
        request.setMatchName("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenMatchDateIsMissing_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest("Test Club");
        request.setMatchDate(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenMatchFirearmTypeIsBlank_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest("Test Club");
        request.setMatchFirearmType("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenMatchCategoryIsBlank_thenThrowsValidationException() {
        // Arrange
        MatchRequest request = validRequest("Test Club");
        request.setMatchCategory("  ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> ipscMatchServiceImpl.validateForCreate(request));
    }

    @Test
    void testValidateForCreate_whenRequestIsValid_thenDoesNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> ipscMatchServiceImpl.validateForCreate(validRequest("Test Club")));
    }

    @Test
    void testValidateForCreate_whenClubIsBlank_thenDoesNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> ipscMatchServiceImpl.validateForCreate(validRequest("  ")));
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
}
