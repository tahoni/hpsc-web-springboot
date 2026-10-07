package za.co.hpsc.web.mappers;

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
import za.co.hpsc.web.models.ipsc.match.request.MatchPatchRequest;
import za.co.hpsc.web.repositories.ClubRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MatchMapper}, with {@link ClubRepository} mocked.
 */
@ExtendWith(MockitoExtension.class)
class MatchMapperTest {

    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private MatchMapper matchMapper;

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
        assertDoesNotThrow(() -> matchMapper.applyFields(match, request));

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
        assertThrows(NonFatalException.class, () -> matchMapper.applyFields(new IpscMatch(), request));
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
        assertThrows(ValidationException.class, () -> matchMapper.applyFields(new IpscMatch(), request));
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
        assertThrows(ValidationException.class, () -> matchMapper.applyFields(new IpscMatch(), request));
    }

    // applyPatchFields()
    @Test
    void testApplyPatchFields_whenRequestHasAllFields_thenCopiesAllFieldsOntoMatch() {
        // Arrange
        Club club = new Club();
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));
        MatchPatchRequest request = new MatchPatchRequest();
        request.setClub("Test Club");
        request.setMatchName("Renamed Championship");
        request.setMatchDate(LocalDate.of(2026, 10, 3));
        request.setStartTime(LocalTime.of(9, 0));
        request.setEndTime(LocalTime.of(16, 0));
        request.setMatchFirearmType(FirearmType.PCC.toString());
        request.setMatchCategory(MatchCategory.CLUB_SHOOT.toString());
        request.setUrl("https://example.com/matches/2");
        IpscMatch match = new IpscMatch();

        // Act
        assertDoesNotThrow(() -> matchMapper.applyPatchFields(match, request));

        // Assert
        assertSame(club, match.getClub());
        assertEquals("Renamed Championship", match.getName());
        assertEquals(LocalDate.of(2026, 10, 3).atStartOfDay(), match.getScheduledDate());
        assertEquals(LocalTime.of(9, 0), match.getStartTime());
        assertEquals(LocalTime.of(16, 0), match.getEndTime());
        assertEquals(FirearmType.PCC, match.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, match.getMatchCategory());
        assertEquals("https://example.com/matches/2", match.getUrl());
    }

    @Test
    void testApplyPatchFields_whenRequestIsEmpty_thenLeavesEveryFieldUnchanged() {
        // Arrange
        IpscMatch match = existingMatch();

        // Act
        assertDoesNotThrow(() -> matchMapper.applyPatchFields(match, new MatchPatchRequest()));

        // Assert
        assertEquals("Club Championship", match.getName());
        assertEquals(LocalDate.of(2026, 9, 12).atStartOfDay(), match.getScheduledDate());
        assertEquals(LocalTime.of(8, 0), match.getStartTime());
        assertEquals(LocalTime.of(17, 0), match.getEndTime());
        assertEquals(FirearmType.HANDGUN, match.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, match.getMatchCategory());
        assertEquals("https://example.com/matches/1", match.getUrl());
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testApplyPatchFields_whenOnlySomeFieldsAreSupplied_thenChangesOnlyThose() {
        // Arrange
        IpscMatch match = existingMatch();
        MatchPatchRequest request = new MatchPatchRequest();
        request.setMatchName("Renamed Championship");
        request.setUrl("https://example.com/matches/2");

        // Act
        assertDoesNotThrow(() -> matchMapper.applyPatchFields(match, request));

        // Assert
        assertEquals("Renamed Championship", match.getName());
        assertEquals("https://example.com/matches/2", match.getUrl());
        assertEquals(LocalTime.of(8, 0), match.getStartTime());
        assertEquals(FirearmType.HANDGUN, match.getMatchFirearmType());
        verifyNoInteractions(clubRepository);
    }

    @Test
    void testApplyPatchFields_whenClubIsUnknown_thenThrowsNonFatalException() {
        // Arrange
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());
        MatchPatchRequest request = new MatchPatchRequest();
        request.setClub("No Such Club");

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchMapper.applyPatchFields(new IpscMatch(), request));
    }

    @Test
    void testApplyPatchFields_whenFirearmTypeIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        MatchPatchRequest request = new MatchPatchRequest();
        request.setMatchFirearmType("Not A Firearm");

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchMapper.applyPatchFields(new IpscMatch(), request));
    }

    @Test
    void testApplyPatchFields_whenMatchCategoryIsUnrecognised_thenThrowsValidationException() {
        // Arrange
        MatchPatchRequest request = new MatchPatchRequest();
        request.setMatchCategory("Not A Category");

        // Act & Assert
        assertThrows(ValidationException.class, () -> matchMapper.applyPatchFields(new IpscMatch(), request));
    }

    // resolveClub()
    @Test
    void testResolveClub_whenClubExists_thenReturnsClub() {
        // Arrange
        Club club = new Club();
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));

        // Act
        Club resolved = assertDoesNotThrow(() -> matchMapper.resolveClub("Test Club"));

        // Assert
        assertSame(club, resolved);
    }

    @Test
    void testResolveClub_whenClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(clubRepository.findByName("No Such Club")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchMapper.resolveClub("No Such Club"));
    }

    @Test
    void testResolveClub_whenClubNameIsNull_thenReturnsDefaultMatchClub() {
        // Arrange
        Club defaultClub = new Club();
        defaultClub.setIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER);
        when(clubRepository.findByIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER))
                .thenReturn(Optional.of(defaultClub));

        // Act
        Club resolved = assertDoesNotThrow(() -> matchMapper.resolveClub(null));

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
        Club resolved = assertDoesNotThrow(() -> matchMapper.resolveClub("  "));

        // Assert
        assertSame(defaultClub, resolved);
    }

    @Test
    void testResolveClub_whenClubNameIsNullAndDefaultClubDoesNotExist_thenThrowsNonFatalException() {
        // Arrange
        when(clubRepository.findByIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NonFatalException.class, () -> matchMapper.resolveClub(null));
    }

    @Test
    void testResolveClub_whenClubNameIsNullAndDefaultIdentifierIsNull_thenThrowsFatalException() {
        // Act & Assert
        assertThrows(FatalException.class, () -> matchMapper.resolveClub(null, null));
    }

    @Test
    void testResolveClub_whenClubNameIsBlankAndDefaultIdentifierIsNull_thenThrowsFatalException() {
        // Act & Assert
        assertThrows(FatalException.class, () -> matchMapper.resolveClub("  ", null));
    }

    @Test
    void testResolveClub_whenClubNameIsSuppliedAndDefaultIdentifierIsNull_thenIgnoresDefaultIdentifier() {
        // Arrange
        Club club = new Club();
        club.setName("Test Club");
        when(clubRepository.findByName("Test Club")).thenReturn(Optional.of(club));

        // Act
        Club resolved = assertDoesNotThrow(() -> matchMapper.resolveClub("Test Club", null));

        // Assert
        assertSame(club, resolved);
    }

    // resolveFirearmType()
    @Test
    void testResolveFirearmType_whenFirearmTypeIsValid_thenReturnsMatchingFirearmType() {
        assertEquals(FirearmType.HANDGUN, matchMapper.resolveFirearmType(FirearmType.HANDGUN.toString()));
    }

    @Test
    void testResolveFirearmType_whenFirearmTypeIsUnrecognised_thenThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> matchMapper.resolveFirearmType("Not A Firearm Type"));
    }

    // resolveMatchCategory()
    @Test
    void testResolveMatchCategory_whenMatchCategoryIsValid_thenReturnsMatchingCategory() {
        assertEquals(MatchCategory.CLUB_SHOOT, matchMapper.resolveMatchCategory(MatchCategory.CLUB_SHOOT.toString()));
    }

    @Test
    void testResolveMatchCategory_whenMatchCategoryIsNullEmptyOrBlank_thenReturnsTheDefaultCategory() {
        // Act & Assert
        assertEquals(IpscConstants.DEFAULT_MATCH_CATEGORY, matchMapper.resolveMatchCategory(null));
        assertEquals(IpscConstants.DEFAULT_MATCH_CATEGORY, matchMapper.resolveMatchCategory(""));
        assertEquals(IpscConstants.DEFAULT_MATCH_CATEGORY, matchMapper.resolveMatchCategory("  	 "));
    }

    @Test
    void testResolveMatchCategory_whenMatchCategoryIsUnrecognised_thenThrowsValidationException() {
        // Act
        ValidationException exception = assertThrows(ValidationException.class,
                () -> matchMapper.resolveMatchCategory("Not A Category"));

        // Assert - a supplied category that doesn't resolve is an error, not a fall back to the default
        assertEquals("Unknown match category: Not A Category", exception.getMessage());
    }

    @Test
    void testResolveMatchCategory_whenMatchCategoryIsTheEnumConstantName_thenReturnsMatchingCategory() {
        // Arrange - the constant name differs from the display name, "Club Shoot"
        assertNotEquals(MatchCategory.CLUB_SHOOT.getName(), MatchCategory.CLUB_SHOOT.name());

        // Act & Assert
        assertEquals(MatchCategory.CLUB_SHOOT, matchMapper.resolveMatchCategory("CLUB_SHOOT"));
        assertEquals(MatchCategory.CLUB_SHOOT, matchMapper.resolveMatchCategory("club_shoot"));
    }

    @Test
    void testResolveMatchCategory_whenMatchCategoryHasSurroundingWhitespace_thenReturnsMatchingCategory() {
        // Act & Assert
        assertEquals(MatchCategory.CLUB_SHOOT,
                matchMapper.resolveMatchCategory(" " + MatchCategory.CLUB_SHOOT.getName() + "	"));
        assertEquals(MatchCategory.CLUB_SHOOT, matchMapper.resolveMatchCategory("  CLUB_SHOOT  "));
    }

    @Test
    void testResolveMatchCategory_whenUnrecognisedCategoryHasSurroundingWhitespace_thenMessageKeepsTheSuppliedValue() {
        // Act
        ValidationException exception = assertThrows(ValidationException.class,
                () -> matchMapper.resolveMatchCategory(" Not A Category "));

        // Assert
        assertEquals("Unknown match category:  Not A Category ", exception.getMessage());
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

    private IpscMatch existingMatch() {
        IpscMatch match = new IpscMatch();
        match.setName("Club Championship");
        match.setScheduledDate(LocalDate.of(2026, 9, 12).atStartOfDay());
        match.setStartTime(LocalTime.of(8, 0));
        match.setEndTime(LocalTime.of(17, 0));
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.CLUB_SHOOT);
        match.setUrl("https://example.com/matches/1");
        return match;
    }
}
