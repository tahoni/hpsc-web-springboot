package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatchDtoTest {

    // Constructor mapping

    // Single parameter constructor - IpscMatch
    @Test
    void testConstructor_whenIpscMatchNull_thenKeepsDefaults() {
        // Arrange & Act
        MatchDto dto = new MatchDto(null);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getIndex());
        assertNull(dto.getClub());
        assertEquals("", dto.getName());
        assertNull(dto.getScheduledDate());
        assertNull(dto.getMatchFirearmType());
        assertNull(dto.getMatchCategory());
    }

    @Test
    void testConstructor_whenIpscMatchHasNullFields_thenMapsNulls() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(42L);
        match.setName(null);
        match.setScheduledDate(null);
        match.setMatchFirearmType(null);
        match.setMatchCategory(null);
        match.setClub(null);

        // Act
        MatchDto dto = new MatchDto(match);

        // Assert
        assertEquals(42L, dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getScheduledDate());
        assertNull(dto.getMatchFirearmType());
        assertNull(dto.getMatchCategory());
        assertNull(dto.getClub());
    }

    @Test
    void testConstructor_whenIpscMatchFullyPopulated_thenMapsAllFields() {
        // Arrange
        Club club = new Club();
        club.setId(10L);
        club.setName("Test Club");
        club.setAbbreviation("TC");

        IpscMatch match = new IpscMatch();
        match.setId(99L);
        match.setName("Championship Match");
        match.setScheduledDate(LocalDateTime.of(2026, 6, 15, 9, 0));
        match.setMatchFirearmType(FirearmType.HANDGUN);
        match.setMatchCategory(MatchCategory.LEAGUE);
        match.setClub(club);
        match.setDateEdited(LocalDateTime.of(2026, 6, 10, 14, 30));

        // Act
        MatchDto dto = new MatchDto(match);

        // Assert
        assertEquals(99L, dto.getId());
        assertEquals("Championship Match", dto.getName());
        assertEquals(LocalDateTime.of(2026, 6, 15, 9, 0), dto.getScheduledDate());
        assertEquals(FirearmType.HANDGUN, dto.getMatchFirearmType());
        assertEquals(MatchCategory.LEAGUE, dto.getMatchCategory());
        assertNotNull(dto.getClub());
        assertEquals(10L, dto.getClub().getId());
        assertEquals("Test Club", dto.getClub().getName());
        assertEquals(LocalDateTime.of(2026, 6, 10, 14, 30), dto.getDateEdited());
    }

    @Test
    void testConstructor_whenIpscMatchPartiallyPopulated_thenMapsSetFieldsAndNulls() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(50L);
        match.setName("Practice Match");
        match.setScheduledDate(LocalDateTime.of(2026, 7, 20, 10, 0));

        // Act
        MatchDto dto = new MatchDto(match);

        // Assert
        assertEquals(50L, dto.getId());
        assertEquals("Practice Match", dto.getName());
        assertEquals(LocalDateTime.of(2026, 7, 20, 10, 0), dto.getScheduledDate());
        assertNull(dto.getMatchFirearmType());
        assertNull(dto.getMatchCategory());
        assertNull(dto.getClub());
        assertNull(dto.getDateEdited());
    }

    @Test
    void testConstructor_whenIpscMatchHasEmptyOrBlankName_thenMapsCorrectly() {
        // Test 1: Empty name
        IpscMatch match1 = new IpscMatch();
        match1.setId(1L);
        match1.setName("");

        MatchDto dto1 = new MatchDto(match1);

        assertEquals("", dto1.getName());

        // Test 2: Blank name
        IpscMatch match2 = new IpscMatch();
        match2.setId(2L);
        match2.setName("   ");

        MatchDto dto2 = new MatchDto(match2);

        assertEquals("   ", dto2.getName());
    }

    // Two parameter constructor - IpscMatch and ClubDto
    @Test
    void testConstructor_whenIpscMatchAndClubDtoNull_thenKeepsDefaults() {
        // Act
        MatchDto dto = new MatchDto(null, null);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getClub());
    }

    @Test
    void testConstructor_whenIpscMatchProvidedAndClubDtoNull_thenUsesMatchClub() {
        // Arrange
        Club club = new Club();
        club.setId(5L);
        club.setName("Match Club");

        IpscMatch match = new IpscMatch();
        match.setId(20L);
        match.setName("Test Match");
        match.setClub(club);

        // Act
        MatchDto dto = new MatchDto(match, null);

        // Assert
        assertEquals(20L, dto.getId());
        assertNotNull(dto.getClub());
        assertEquals(5L, dto.getClub().getId());
        assertEquals("Match Club", dto.getClub().getName());
    }

    @Test
    void testConstructor_whenIpscMatchAndClubDtoProvided_thenUsesClubDto() {
        // Arrange
        Club matchClub = new Club();
        matchClub.setId(5L);
        matchClub.setName("Match Club");

        IpscMatch match = new IpscMatch();
        match.setId(20L);
        match.setName("Test Match");
        match.setClub(matchClub);

        ClubDto clubDto = new ClubDto();
        clubDto.setIndex(10);
        clubDto.setName("DTO Club");

        // Act
        MatchDto dto = new MatchDto(match, clubDto);

        // Assert
        assertEquals(20L, dto.getId());
        assertEquals(10, dto.getClubIndex());
        assertEquals("DTO Club", dto.getClub().getName());
    }

    @Test
    void testConstructor_whenIpscMatchProvidedAndClubDtoProvidedButMatchClubNull_thenUsesClubDto() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(30L);
        match.setName("Test Match");
        match.setClub(null);

        ClubDto clubDto = new ClubDto();
        clubDto.setIndex(15);
        clubDto.setName("DTO Club Only");

        // Act
        MatchDto dto = new MatchDto(match, clubDto);

        // Assert
        assertEquals(30L, dto.getId());
        assertEquals(15, dto.getClubIndex());
        assertEquals("DTO Club Only", dto.getClub().getName());
    }

    @Test
    void testConstructor_whenIpscMatchProvidedAndBothClubsNull_thenClubIsNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(40L);
        match.setName("No Club Match");
        match.setClub(null);

        // Act
        MatchDto dto = new MatchDto(match, null);

        // Assert
        assertEquals(40L, dto.getId());
        assertNull(dto.getClub());
        assertNull(dto.getClubIndex());
    }

    @Test
    void testConstructor_whenIpscMatchPopulated_thenUuidIsNotNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(100L);
        match.setName("UUID Test Match");

        // Act
        MatchDto dto = new MatchDto(match);

        // Assert
        assertNotNull(dto.getUuid());
    }


    // init() mapping

    // Null and existing value handling
    @Test
    void testInit_whenMatchResponseNull_thenKeepsExistingValues() {
        // Arrange
        MatchDto dto = new MatchDto();
        dto.setIndex(5);
        dto.setName("Existing Match");
        dto.setScheduledDate(LocalDateTime.of(2026, 5, 1, 10, 0));
        dto.setMatchFirearmType(FirearmType.HANDGUN);
        dto.setMatchCategory(MatchCategory.CLUB_SHOOT);

        // Act
        dto.init(null, null, null);

        // Assert
        assertEquals(5, dto.getIndex());
        assertEquals("Existing Match", dto.getName());
        assertEquals(LocalDateTime.of(2026, 5, 1, 10, 0), dto.getScheduledDate());
        assertEquals(FirearmType.HANDGUN, dto.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, dto.getMatchCategory());
        assertNull(dto.getDateEdited());
    }

    // Basic field mapping
    @Test
    void testInit_whenMatchResponseProvidedAndClubDtoNull_thenMapsMatchAndSetsDateToNow() {
        // Arrange
        MatchDto dto = new MatchDto();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(12);
        matchResponse.setMatchName("Spring Championship");
        matchResponse.setMatchDate(LocalDateTime.of(2026, 4, 15, 9, 0));
        matchResponse.setFirearmId(1);
        matchResponse.setClubId(7);

        LocalDateTime beforeInit = LocalDateTime.now();

        // Act
        dto.init(matchResponse, null, null);

        LocalDateTime afterInit = LocalDateTime.now();

        // Assert
        assertEquals(12, dto.getIndex());
        assertEquals("Spring Championship", dto.getName());
        assertEquals(LocalDateTime.of(2026, 4, 15, 9, 0), dto.getScheduledDate());
        assertEquals(FirearmType.HANDGUN, dto.getMatchFirearmType());
        assertEquals(MatchCategory.CLUB_SHOOT, dto.getMatchCategory());
        assertEquals(7, dto.getClubIndex());
        assertNotNull(dto.getDateEdited());
        assertTrue(dto.getDateEdited().isAfter(beforeInit.minusSeconds(1)) &&
                dto.getDateEdited().isBefore(afterInit.plusSeconds(1)));
    }

    // Club handling
    @Test
    void testInit_whenMatchResponseAndClubDtoProvided_thenMapsClubDto() {
        // Arrange
        MatchDto dto = new MatchDto();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(20);
        matchResponse.setMatchName("Summer Classic");
        matchResponse.setMatchDate(LocalDateTime.of(2026, 7, 10, 8, 30));
        matchResponse.setFirearmId(2);
        matchResponse.setClubId(10);

        ClubDto clubDto = new ClubDto();
        clubDto.setIndex(15);
        clubDto.setName("Elite Shooting Club");
        clubDto.setAbbreviation("ESC");

        // Act
        dto.init(matchResponse, clubDto, null);

        // Assert
        assertEquals(20, dto.getIndex());
        assertEquals("Summer Classic", dto.getName());
        assertEquals(15, dto.getClubIndex());
        assertEquals("Elite Shooting Club", dto.getClub().getName());
    }

    // Score response handling
    @Test
    void testInit_whenMatchResponseWithScoreResponses_thenSetsDateEditedToLatestScore() {
        // Arrange
        MatchDto dto = new MatchDto();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(30);
        matchResponse.setMatchName("Fall Challenge");
        matchResponse.setMatchDate(LocalDateTime.of(2026, 10, 5, 10, 0));
        matchResponse.setFirearmId(1);

        ScoreResponse score1 = new ScoreResponse();
        score1.setMatchId(30);
        score1.setLastModified(LocalDateTime.of(2026, 10, 6, 12, 0));

        ScoreResponse score2 = new ScoreResponse();
        score2.setMatchId(30);
        score2.setLastModified(LocalDateTime.of(2026, 10, 7, 15, 30));

        ScoreResponse score3 = new ScoreResponse();
        score3.setMatchId(30);
        score3.setLastModified(LocalDateTime.of(2026, 10, 6, 18, 0));

        List<ScoreResponse> scoreResponses = List.of(score1, score2, score3);

        // Act
        dto.init(matchResponse, null, scoreResponses);

        // Assert
        assertEquals(30, dto.getIndex());
        assertEquals(LocalDateTime.of(2026, 10, 7, 15, 30), dto.getDateEdited());
    }

    @Test
    void testInit_whenMatchResponseWithEmptyScoreResponsesList_thenSetsDateEditedToNow() {
        // Arrange
        MatchDto dto = new MatchDto();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(40);
        matchResponse.setMatchName("Winter Tournament");
        matchResponse.setMatchDate(LocalDateTime.of(2026, 12, 1, 9, 0));
        matchResponse.setFirearmId(1);

        LocalDateTime beforeInit = LocalDateTime.now();

        // Act
        dto.init(matchResponse, null, List.of());

        LocalDateTime afterInit = LocalDateTime.now();

        // Assert
        assertEquals(40, dto.getIndex());
        assertNotNull(dto.getDateEdited());
        assertTrue(dto.getDateEdited().isAfter(beforeInit.minusSeconds(1)) &&
                dto.getDateEdited().isBefore(afterInit.plusSeconds(1)));
    }

    @Test
    void testInit_whenMatchResponseWithScoreResponsesForDifferentMatch_thenFiltersOutWrongScores() {
        // Arrange
        MatchDto dto = new MatchDto();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(50);
        matchResponse.setMatchName("Target Match");
        matchResponse.setMatchDate(LocalDateTime.of(2026, 8, 20, 10, 0));
        matchResponse.setFirearmId(1);

        ScoreResponse score1 = new ScoreResponse();
        score1.setMatchId(50);
        score1.setLastModified(LocalDateTime.of(2026, 8, 21, 12, 0));

        ScoreResponse score2 = new ScoreResponse();
        score2.setMatchId(99); // Different match
        score2.setLastModified(LocalDateTime.of(2026, 8, 22, 15, 0));

        ScoreResponse score3 = new ScoreResponse();
        score3.setMatchId(50);
        score3.setLastModified(LocalDateTime.of(2026, 8, 21, 16, 0));

        List<ScoreResponse> scoreResponses = List.of(score1, score2, score3);

        // Act
        dto.init(matchResponse, null, scoreResponses);

        // Assert
        assertEquals(50, dto.getIndex());
        assertEquals(LocalDateTime.of(2026, 8, 21, 16, 0), dto.getDateEdited());
    }

    // Edge cases - null/empty/blank field handling
    @Test
    void testInit_whenMatchResponseHasNullEmptyOrBlankMatchName_thenMapsCorrectly() {
        // Test 1: Null matchName
        MatchDto dto1 = new MatchDto();
        MatchResponse matchResponse1 = new MatchResponse();
        matchResponse1.setMatchId(60);
        matchResponse1.setMatchName(null);
        matchResponse1.setMatchDate(LocalDateTime.of(2026, 9, 10, 10, 0));
        matchResponse1.setFirearmId(1);

        dto1.init(matchResponse1, null, null);

        assertEquals(60, dto1.getIndex());
        assertNull(dto1.getName());

        // Test 2: Empty matchName
        MatchDto dto2 = new MatchDto();
        MatchResponse matchResponse2 = new MatchResponse();
        matchResponse2.setMatchId(61);
        matchResponse2.setMatchName("");
        matchResponse2.setMatchDate(LocalDateTime.of(2026, 9, 11, 10, 0));
        matchResponse2.setFirearmId(1);

        dto2.init(matchResponse2, null, null);

        assertEquals(61, dto2.getIndex());
        assertEquals("", dto2.getName());

        // Test 3: Blank matchName
        MatchDto dto3 = new MatchDto();
        MatchResponse matchResponse3 = new MatchResponse();
        matchResponse3.setMatchId(62);
        matchResponse3.setMatchName("   ");
        matchResponse3.setMatchDate(LocalDateTime.of(2026, 9, 12, 10, 0));
        matchResponse3.setFirearmId(1);

        dto3.init(matchResponse3, null, null);

        assertEquals(62, dto3.getIndex());
        assertEquals("   ", dto3.getName());
    }

    // Firearm type mapping
    @Test
    void testInit_whenMatchResponseHasNullFirearmId_thenKeepsExistingFirearmType() {
        // Arrange
        MatchDto dto = new MatchDto();
        dto.setMatchFirearmType(FirearmType.HANDGUN);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(70);
        matchResponse.setMatchName("Test Match");
        matchResponse.setMatchDate(LocalDateTime.of(2026, 11, 1, 10, 0));
        matchResponse.setFirearmId(null);

        // Act
        dto.init(matchResponse, null, null);

        // Assert
        assertEquals(FirearmType.HANDGUN, dto.getMatchFirearmType());
    }

    // Comprehensive mapping tests
    @Test
    void testInit_whenMatchResponseFullyPopulated_thenMapsAllFields() {
        // Arrange
        MatchDto dto = new MatchDto();
        dto.setMatchFirearmType(FirearmType.HANDGUN);

        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(71);
        matchResponse.setMatchName("Test Match 2");
        matchResponse.setMatchDate(LocalDateTime.of(2026, 11, 2, 10, 0));
        matchResponse.setFirearmId(9999); // Invalid ID

        // Act
        dto.init(matchResponse, null, null);

        // Assert
        assertEquals(FirearmType.HANDGUN, dto.getMatchFirearmType());
    }

    // Comprehensive mapping tests
    @Test
    void testInit_whenScoreResponsesContainNulls_thenFiltersNulls() {
        // Arrange
        MatchDto dto = new MatchDto();
        MatchResponse matchResponse = new MatchResponse();
        matchResponse.setMatchId(90);
        matchResponse.setMatchName("Null Score Test");
        matchResponse.setMatchDate(LocalDateTime.of(2026, 11, 20, 10, 0));
        matchResponse.setFirearmId(1);

        ScoreResponse score1 = new ScoreResponse();
        score1.setMatchId(90);
        score1.setLastModified(LocalDateTime.of(2026, 11, 21, 12, 0));

        List<ScoreResponse> scoreResponses = java.util.Arrays.asList(score1, null, null);

        // Act
        dto.init(matchResponse, null, scoreResponses);

        // Assert
        assertEquals(90, dto.getIndex());
    }


    // toString() behavior

    // Fully Populated - Club and Club Name Provided
    @Test
    void testToString_whenClubAndClubNameProvided_thenReturnsMatchNameAtNameOfClub() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("HPSC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Club Shoot");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Eufees Club Shoot @ Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }

    @Test
    void testToString_whenClubAndClubNameAndAbbreviationProvided_thenIncludesAllDetails() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Elite Shooting Range");
        clubDto.setAbbreviation("ESR");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Spring Championship");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Spring Championship @ Elite Shooting Range (ESR)", result);
    }

    @Test
    void testToString_whenFullyPopulatedWithLongNames_thenReturnsCompleteString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("South African Practical Shooting Championship Club");
        clubDto.setAbbreviation("SAPSCC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("National Qualification Round - February 2026");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("National Qualification Round - February 2026 @ South African Practical Shooting Championship Club (SAPSCC)", result);
    }

    // Partially Populated - Club Missing
    @Test
    void testToString_whenClubAndClubNameMissing_thenReturnsMatchName() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Club Shoot");

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Eufees Club Shoot", result);
    }

    @Test
    void testToString_whenClubIsNullButNameProvided_thenReturnsMatchNameOnly() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Practice Match");
        matchDto.setClub(null);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Practice Match", result);
    }

    @Test
    void testToString_whenClubHasNullName_thenHandlesGracefully() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName(null);
        clubDto.setAbbreviation("ABC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match Name");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        // Should handle null club name gracefully
        assertNotNull(result);
        assertTrue(result.contains("Match Name"));
    }

    // Null Name Handling
    @Test
    void testToString_whenMatchNameNull_thenReturnsEmptyString() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName(null);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenMatchNameNullAndClubProvided_thenIncludesClub() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("TC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName(null);
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Test Club"));
    }

    // Empty Name Handling
    @Test
    void testToString_whenMatchNameEmpty_thenReturnsEmptyString() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("");

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenMatchNameEmptyAndClubProvided_thenIncludesClub() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("TC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("@"));
        assertTrue(result.contains("Test Club"));
    }

    // Blank Name Handling
    @Test
    void testToString_whenMatchNameBlank_thenReturnsEmptyString() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("   ");

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("", result);
    }

    @Test
    void testToString_whenMatchNameBlankAndClubProvided_thenIncludesClub() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("TC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("   ");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("@"));
        assertTrue(result.contains("Test Club"));
    }

    // Club Name Null/Empty/Blank Handling
    @Test
    void testToString_whenClubNameNullButAbbreviationProvided_thenHandlesNullClubName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName(null);
        clubDto.setAbbreviation("ABC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
    }

    @Test
    void testToString_whenClubNameEmptyButAbbreviationProvided_thenHandlesEmptyClubName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation("ABC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
    }

    @Test
    void testToString_whenClubNameBlankButAbbreviationProvided_thenHandlesBlankClubName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("   ");
        clubDto.setAbbreviation("ABC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
    }

    // Club Abbreviation Null/Empty/Blank Handling
    @Test
    void testToString_whenClubAbbreviationNull_thenHandlesNullAbbreviation() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        clubDto.setAbbreviation(null);
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Match"));
        assertTrue(result.contains("Test Club"));
    }

    @Test
    void testToString_whenClubAbbreviationEmpty_thenHandlesEmptyAbbreviation() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Match"));
        assertTrue(result.contains("Test Club"));
    }

    @Test
    void testToString_whenClubAbbreviationBlank_thenHandlesBlankAbbreviation() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("   ");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Match"));
        assertTrue(result.contains("Test Club"));
    }

    // Both Club Name and Abbreviation Null/Empty/Blank
    @Test
    void testToString_whenClubNameAndAbbreviationBothNull_thenHandlesNullClubFields() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName(null);
        clubDto.setAbbreviation(null);
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
    }

    @Test
    void testToString_whenClubNameAndAbbreviationBothEmpty_thenHandlesEmptyClubFields() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation("");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
    }

    @Test
    void testToString_whenClubNameAndAbbreviationBothBlank_thenHandlesBlankClubFields() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("   ");
        clubDto.setAbbreviation("   ");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
    }

    // Default Constructor
    @Test
    void testToString_whenDefaultConstructor_thenReturnsEmptyOrDefault() {
        // Arrange
        MatchDto matchDto = new MatchDto();

        // Act
        String result = matchDto.toString();

        // Assert
        assertNotNull(result);
    }

    // With Other Fields Set (Not Affecting toString)
    @Test
    void testToString_whenWithIdAndIndex_thenIgnoresIdAndIndex() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setId(999L);
        matchDto.setIndex(888);
        matchDto.setName("Match Name");

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Match Name", result);
        assertFalse(result.contains("999"));
        assertFalse(result.contains("888"));
    }

    @Test
    void testToString_whenWithScheduledDate_thenIgnoresScheduledDate() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match Name");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 6, 15, 9, 0));

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Match Name", result);
        assertFalse(result.contains("2026"));
    }

    @Test
    void testToString_whenWithFirearmType_thenIgnoresFirearmType() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match Name");
        matchDto.setMatchFirearmType(FirearmType.HANDGUN);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Match Name", result);
        assertFalse(result.contains("HANDGUN"));
    }

    @Test
    void testToString_whenWithMatchCategory_thenIgnoresMatchCategory() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match Name");
        matchDto.setMatchCategory(MatchCategory.LEAGUE);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Match Name", result);
        assertFalse(result.contains("LEAGUE"));
    }

    @Test
    void testToString_whenWithAllOtherFields_thenOnlyUsesMatchNameAndClub() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("TC");
        MatchDto matchDto = new MatchDto();
        matchDto.setId(100L);
        matchDto.setIndex(50);
        matchDto.setName("Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 6, 15, 9, 0));
        matchDto.setMatchFirearmType(FirearmType.SHOTGUN);
        matchDto.setMatchCategory(MatchCategory.CLUB_SHOOT);
        matchDto.setClub(clubDto);
        matchDto.setDateEdited(LocalDateTime.of(2026, 6, 1, 10, 0));

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Match @ Test Club (TC)", result);
        assertFalse(result.contains("100"));
        assertFalse(result.contains("50"));
        assertFalse(result.contains("SHOTGUN"));
        assertFalse(result.contains("CLUB_SHOOT"));
    }

    // Consistency and Mutability
    @Test
    void testToString_whenCalledMultipleTimes_thenReturnsConsistentResults() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Club");
        clubDto.setAbbreviation("C");
        matchDto.setClub(clubDto);

        // Act
        String result1 = matchDto.toString();
        String result2 = matchDto.toString();
        String result3 = matchDto.toString();

        // Assert
        assertEquals(result1, result2);
        assertEquals(result2, result3);
        assertEquals("Match @ Club (C)", result1);
    }

    @Test
    void testToString_whenNameChangedAfterCall_thenReflectsNewName() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Original Name");

        // Act
        String result1 = matchDto.toString();
        matchDto.setName("Updated Name");
        String result2 = matchDto.toString();

        // Assert
        assertEquals("Original Name", result1);
        assertEquals("Updated Name", result2);
        assertNotEquals(result1, result2);
    }

    @Test
    void testToString_whenClubAddedAfterInitialization_thenReflectsChange() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");

        // Act
        String result1 = matchDto.toString();
        ClubDto clubDto = new ClubDto();
        clubDto.setName("New Club");
        clubDto.setAbbreviation("NC");
        matchDto.setClub(clubDto);
        String result2 = matchDto.toString();

        // Assert
        assertEquals("Match", result1);
        assertEquals("Match @ New Club (NC)", result2);
        assertNotEquals(result1, result2);
    }

    @Test
    void testToString_whenClubRemovedAfterInitialization_thenReflectsChange() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Club");
        clubDto.setAbbreviation("C");
        matchDto.setClub(clubDto);

        // Act
        String result1 = matchDto.toString();
        matchDto.setClub(null);
        String result2 = matchDto.toString();

        // Assert
        assertEquals("Match @ Club (C)", result1);
        assertEquals("Match", result2);
        assertNotEquals(result1, result2);
    }

    // Special Characters
    @Test
    void testToString_whenNamesContainSpecialCharacters_thenIncludesCharacters() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("São Paulo Shooting Club");
        clubDto.setAbbreviation("SPÇ");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("National Open 2026 - Men's Category");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("National Open 2026 - Men's Category @ São Paulo Shooting Club (SPÇ)", result);
        assertTrue(result.contains("ã"));
        assertTrue(result.contains("Ç"));
        assertTrue(result.contains("-"));
    }

    @Test
    void testToString_whenNamesContainNumbers_thenIncludesNumbers() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Club 123");
        clubDto.setAbbreviation("C123");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match 2026 - Round 5");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertEquals("Match 2026 - Round 5 @ Club 123 (C123)", result);
        assertTrue(result.contains("123"));
        assertTrue(result.contains("2026"));
        assertTrue(result.contains("5"));
    }

    // Very Long Names
    @Test
    void testToString_whenNamesAreLong_thenReturnsFullLength() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("South African Practical Shooting Championship Committee");
        clubDto.setAbbreviation("SAPSCC");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("International Practical Shooting Confederation World Championship Qualifier");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        String expected = "International Practical Shooting Confederation World Championship Qualifier @ South African Practical Shooting Championship Committee (SAPSCC)";
        assertEquals(expected, result);
        assertTrue(result.length() > 100);
    }

    // With Extra Whitespace
    @Test
    void testToString_whenNamesHaveLeadingTrailingWhitespace_thenIncludesWhitespace() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("  Club Name  ");
        clubDto.setAbbreviation("  CN  ");
        MatchDto matchDto = new MatchDto();
        matchDto.setName("  Match Name  ");
        matchDto.setClub(clubDto);

        // Act
        String result = matchDto.toString();

        // Assert
        assertTrue(result.contains("Match Name"));
        assertTrue(result.contains("Club Name"));
    }
}
