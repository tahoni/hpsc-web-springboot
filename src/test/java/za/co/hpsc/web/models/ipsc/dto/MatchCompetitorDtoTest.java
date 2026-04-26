package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.response.EnrolledResponse;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchCompetitorDtoTest {

    @Test
    void testDefaultConstructor_whenInvoked_thenInitializesDefaults() {
        // Arrange & Act
        MatchCompetitorDto dto = new MatchCompetitorDto();

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getCompetitorIndex());
        assertNull(dto.getMatchIndex());
        assertNull(dto.getCompetitor());
        assertNull(dto.getMatch());
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
        assertNull(dto.getClub());
        assertNull(dto.getFirearmType());
        assertNull(dto.getDivision());
        assertNull(dto.getPowerFactor());
        assertNull(dto.getMatchPoints());
        assertNull(dto.getMatchRanking());
        assertNull(dto.getDateEdited());
    }

    @Test
    void testConstructorFromMatchCompetitor_whenEntityProvided_thenMapsFields() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Harties Club");
        club.setAbbreviation("HC");

        IpscMatch match = new IpscMatch();
        match.setId(100L);
        match.setClub(club);
        match.setName("Autumn Cup");
        match.setScheduledDate(LocalDateTime.of(2026, 3, 1, 8, 0));

        Competitor competitor = new Competitor();
        competitor.setId(200L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Shooter");
        competitor.setCompetitorNumber("12345");

        MatchCompetitor entity = new MatchCompetitor();
        entity.setId(300L);
        entity.setCompetitor(competitor);
        entity.setMatch(match);
        entity.setMatchClub(ClubIdentifier.HPSC);
        entity.setCompetitorCategory(CompetitorCategory.SENIOR);
        entity.setFirearmType(FirearmType.HANDGUN);
        entity.setDivision(Division.OPEN);
        entity.setPowerFactor(PowerFactor.MAJOR);
        entity.setMatchPoints(new BigDecimal("250.50"));
        entity.setMatchRanking(new BigDecimal("2"));

        // Act
        MatchCompetitorDto dto = new MatchCompetitorDto(entity);

        // Assert
        assertEquals(300L, dto.getId());
        assertNotNull(dto.getCompetitor());
        assertNotNull(dto.getMatch());
        assertEquals(ClubIdentifier.HPSC, dto.getClub());
        assertEquals(CompetitorCategory.SENIOR, dto.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, dto.getFirearmType());
        assertEquals(Division.OPEN, dto.getDivision());
        assertEquals(PowerFactor.MAJOR, dto.getPowerFactor());
        assertEquals(new BigDecimal("250.50"), dto.getMatchPoints());
        assertEquals(new BigDecimal("2"), dto.getMatchRanking());
    }

    @Test
    void testConstructorFromCompetitorAndMatch_whenCompetitorProvided_thenSetsIndexesAndReferences() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");

        MatchDto matchDto = new MatchDto();
        matchDto.setIndex(7);
        matchDto.setName("Club Match");

        // Act
        MatchCompetitorDto dto = new MatchCompetitorDto(competitorDto, matchDto, 5);

        // Assert
        assertEquals(5, dto.getCompetitorIndex());
        assertEquals(7, dto.getMatchIndex());
        assertEquals(competitorDto, dto.getCompetitor());
        assertEquals(matchDto, dto.getMatch());
    }

    @Test
    void testInit_whenScoresAndEnrollmentProvided_thenAggregatesPointsAndMapsEnums() {
        // Arrange
        MatchCompetitorDto dto = new MatchCompetitorDto();

        ScoreResponse scoreOne = new ScoreResponse();
        scoreOne.setFinalScore(100);
        scoreOne.setLastModified(LocalDateTime.of(2026, 4, 1, 10, 0));

        ScoreResponse scoreTwo = new ScoreResponse();
        scoreTwo.setFinalScore(null);
        scoreTwo.setLastModified(LocalDateTime.of(2026, 4, 1, 11, 0));

        ScoreResponse scoreThree = new ScoreResponse();
        scoreThree.setFinalScore(25);
        scoreThree.setLastModified(LocalDateTime.of(2026, 4, 1, 9, 0));

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(44);
        enrolledResponse.setMatchId(55);
        enrolledResponse.setRefNo("BBB");
        enrolledResponse.setMajorPowerFactor(true);
        enrolledResponse.setDivisionId(1);
        enrolledResponse.setCompetitorCategoryId(3);

        // Act
        dto.init(List.of(scoreOne, scoreTwo, scoreThree), enrolledResponse);

        // Assert
        assertEquals(44, dto.getCompetitorIndex());
        assertEquals(55, dto.getMatchIndex());
        assertEquals(new BigDecimal("125"), dto.getMatchPoints());
        assertEquals(LocalDateTime.of(2026, 4, 1, 11, 0), dto.getDateEdited());
        assertEquals(ClubIdentifier.HPSC, dto.getClub());
        assertEquals(PowerFactor.MAJOR, dto.getPowerFactor());
        assertEquals(Division.OPEN, dto.getDivision());
        assertEquals(FirearmType.HANDGUN, dto.getFirearmType());
        assertEquals(CompetitorCategory.SENIOR, dto.getCompetitorCategory());
    }

    @Test
    void testInit_whenScoresNullAndEnrollmentProvided_thenMapsEnrollmentOnly() {
        // Arrange
        MatchCompetitorDto dto = new MatchCompetitorDto();

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(88);
        enrolledResponse.setMatchId(99);
        enrolledResponse.setRefNo("ZZZ");
        enrolledResponse.setMajorPowerFactor(false);
        enrolledResponse.setDivisionId(999);
        enrolledResponse.setCompetitorCategoryId(null);

        // Act
        dto.init(null, enrolledResponse);

        // Assert
        assertEquals(88, dto.getCompetitorIndex());
        assertEquals(99, dto.getMatchIndex());
        assertNull(dto.getMatchPoints());
        assertNull(dto.getDateEdited());
        assertEquals(ClubIdentifier.UNKNOWN, dto.getClub());
        assertEquals(PowerFactor.MINOR, dto.getPowerFactor());
        assertNull(dto.getDivision());
        assertNull(dto.getFirearmType());
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
    }

    @Test
    void testInit_whenScoresEmpty_thenSetsPointsToZeroAndDateEditedToNow() {
        // Arrange
        MatchCompetitorDto dto = new MatchCompetitorDto();

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(1);
        enrolledResponse.setMatchId(2);
        enrolledResponse.setRefNo("AAA");

        LocalDateTime before = LocalDateTime.now();

        // Act
        dto.init(new ArrayList<>(), enrolledResponse);

        // Assert
        LocalDateTime after = LocalDateTime.now();
        assertEquals(BigDecimal.ZERO, dto.getMatchPoints());
        assertNotNull(dto.getDateEdited());
        assertFalse(dto.getDateEdited().isBefore(before));
        assertFalse(dto.getDateEdited().isAfter(after));
    }

    @Test
    void testInit_whenScoresProvidedAndEnrollmentNull_thenThrowsNullPointerException() {
        // Arrange
        MatchCompetitorDto dto = new MatchCompetitorDto();
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setFinalScore(10);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> dto.init(List.of(scoreResponse), null));
    }

    @Test
    void testToString_whenMatchAndCompetitorProvided_thenReturnsCombinedString() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Regional Match");

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Mary");
        competitorDto.setLastName("Major");

        MatchCompetitorDto dto = new MatchCompetitorDto();
        dto.setMatch(matchDto);
        dto.setCompetitor(competitorDto);

        // Act
        String result = dto.toString();

        // Assert
        assertTrue(result.contains("Regional Match"));
        assertTrue(result.contains("Mary"));
        assertTrue(result.contains("Major"));
        assertTrue(result.contains(":"));
    }

    @Test
    void testToString_whenMatchIsNull_thenThrowsNullPointerException() {
        // Arrange
        MatchCompetitorDto dto = new MatchCompetitorDto();
        dto.setMatch(null);
        dto.setCompetitor(new CompetitorDto());

        // Act & Assert
        assertThrows(NullPointerException.class, dto::toString);
    }
}

