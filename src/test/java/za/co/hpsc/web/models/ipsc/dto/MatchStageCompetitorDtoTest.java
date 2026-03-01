package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.models.ipsc.response.EnrolledResponse;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MatchStageCompetitorDtoTest {

    // Constructor mapping - No Parameter

    // Default Construction
    @Test
    void testDefaultConstructor_whenNoArguments_thenInitializesWithDefaults() {
        // Arrange & Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getCompetitorIndex());
        assertNull(dto.getMatchStageIndex());
        assertNull(dto.getCompetitor());
        assertNull(dto.getMatchStage());
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
        assertNull(dto.getClub());
        assertNull(dto.getFirearmType());
        assertNull(dto.getDivision());
        assertNull(dto.getPowerFactor());
        assertNull(dto.getScoreA());
        assertNull(dto.getScoreB());
        assertNull(dto.getScoreC());
        assertNull(dto.getScoreD());
        assertNull(dto.getPoints());
        assertNull(dto.getMisses());
        assertNull(dto.getPenalties());
        assertNull(dto.getProcedurals());
        assertNull(dto.getHasDeduction());
        assertNull(dto.getDeductionPercentage());
        assertNull(dto.getTime());
        assertNull(dto.getHitFactor());
        assertNull(dto.getStagePoints());
        assertNull(dto.getStagePercentage());
        assertNull(dto.getStageRanking());
        assertNull(dto.getIsDisqualified());
        assertNull(dto.getDateEdited());
    }

    @Test
    void testDefaultConstructor_whenNoArguments_thenUuidIsUnique() {
        // Arrange & Act
        MatchStageCompetitorDto dto1 = new MatchStageCompetitorDto();
        MatchStageCompetitorDto dto2 = new MatchStageCompetitorDto();

        // Assert
        assertNotNull(dto1.getUuid());
        assertNotNull(dto2.getUuid());
        assertNotEquals(dto1.getUuid(), dto2.getUuid());
    }

    @Test
    void testDefaultConstructor_whenNoArguments_thenCompetitorCategoryDefaultsToNone() {
        // Arrange & Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        // Assert
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
    }

    // Constructor mapping - MatchStageCompetitor Entity Parameter

    // Null Entity
    @Test
    void testConstructor_whenMatchStageCompetitorEntityNull_thenInitializesWithDefaults() {
        // Act & Assert
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(null);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getCompetitorIndex());
        assertNull(dto.getMatchStageIndex());
        assertNull(dto.getCompetitor());
        assertNull(dto.getMatchStage());
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
        assertNull(dto.getClub());
        assertNull(dto.getFirearmType());
        assertNull(dto.getDivision());
        assertNull(dto.getPowerFactor());
        assertNull(dto.getScoreA());
        assertNull(dto.getScoreB());
        assertNull(dto.getScoreC());
        assertNull(dto.getScoreD());
        assertNull(dto.getPoints());
        assertNull(dto.getMisses());
        assertNull(dto.getPenalties());
        assertNull(dto.getProcedurals());
        assertNull(dto.getHasDeduction());
        assertNull(dto.getDeductionPercentage());
        assertNull(dto.getTime());
        assertNull(dto.getHitFactor());
        assertNull(dto.getStagePoints());
        assertNull(dto.getStagePercentage());
        assertNull(dto.getStageRanking());
        assertNull(dto.getIsDisqualified());
        assertNull(dto.getDateEdited());
    }

    // Null Fields in Entity
    @Test
    void testConstructor_whenMatchStageCompetitorEntityHasNullFields_thenMapsNulls() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Test Club");

        IpscMatch match = new IpscMatch();
        match.setId(10L);
        match.setName("Test Match");
        match.setClub(club);

        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setId(5L);
        matchStage.setMatch(match);
        matchStage.setStageNumber(1);

        Competitor competitor = new Competitor();
        competitor.setId(20L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setId(100L);
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);
        entity.setCompetitorCategory(null);
        entity.setFirearmType(null);
        entity.setDivision(null);
        entity.setPowerFactor(null);
        entity.setScoreA(null);
        entity.setScoreB(null);
        entity.setScoreC(null);
        entity.setScoreD(null);
        entity.setPoints(null);
        entity.setMisses(null);
        entity.setPenalties(null);
        entity.setProcedurals(null);
        entity.setHasDeduction(null);
        entity.setDeductionPercentage(null);
        entity.setTime(null);
        entity.setHitFactor(null);
        entity.setStagePoints(null);
        entity.setStagePercentage(null);
        entity.setStageRanking(null);
        entity.setIsDisqualified(null);
        entity.setDateEdited(null);

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(entity);

        // Assert
        assertEquals(100L, dto.getId());
        assertNotNull(dto.getCompetitor());
        assertNotNull(dto.getMatchStage());
        assertNotNull(dto.getClub());
        assertNull(dto.getCompetitorCategory());
        assertNull(dto.getFirearmType());
        assertNull(dto.getDivision());
        assertNull(dto.getPowerFactor());
        assertNull(dto.getScoreA());
        assertNull(dto.getScoreB());
        assertNull(dto.getScoreC());
        assertNull(dto.getScoreD());
        assertNull(dto.getPoints());
        assertNull(dto.getMisses());
        assertNull(dto.getPenalties());
        assertNull(dto.getProcedurals());
        assertNull(dto.getHasDeduction());
        assertNull(dto.getDeductionPercentage());
        assertNull(dto.getTime());
        assertNull(dto.getHitFactor());
        assertNull(dto.getStagePoints());
        assertNull(dto.getStagePercentage());
        assertNull(dto.getStageRanking());
        assertNull(dto.getIsDisqualified());
        assertNull(dto.getDateEdited());
    }

    // Partially Populated Entity
    @Test
    void testConstructor_whenMatchStageCompetitorEntityPartiallyPopulated_thenMapsSetFieldsAndNulls() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Test Club");

        IpscMatch match = new IpscMatch();
        match.setId(10L);
        match.setName("Test Match");
        match.setClub(club);

        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setId(5L);
        matchStage.setMatch(match);
        matchStage.setStageNumber(2);

        Competitor competitor = new Competitor();
        competitor.setId(20L);
        competitor.setFirstName("Jane");
        competitor.setLastName("Smith");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setId(101L);
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);
        entity.setCompetitorCategory(CompetitorCategory.SENIOR);
        entity.setFirearmType(FirearmType.HANDGUN);
        entity.setDivision(Division.PRODUCTION);
        entity.setPowerFactor(PowerFactor.MINOR);
        entity.setScoreA(10);
        entity.setScoreB(5);
        // scoreC, scoreD not set
        entity.setPoints(150);
        entity.setMisses(2);
        // penalties, procedurals not set
        entity.setTime(BigDecimal.valueOf(45.32));
        entity.setHitFactor(BigDecimal.valueOf(3.31));
        // other fields are not set

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(entity);

        // Assert
        assertEquals(101L, dto.getId());
        assertNotNull(dto.getCompetitor());
        assertNotNull(dto.getMatchStage());
        assertNotNull(dto.getClub());
        assertEquals(CompetitorCategory.SENIOR, dto.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, dto.getFirearmType());
        assertEquals(Division.PRODUCTION, dto.getDivision());
        assertEquals(PowerFactor.MINOR, dto.getPowerFactor());
        assertEquals(10, dto.getScoreA());
        assertEquals(5, dto.getScoreB());
        assertNull(dto.getScoreC());
        assertNull(dto.getScoreD());
        assertEquals(150, dto.getPoints());
        assertEquals(2, dto.getMisses());
        assertNull(dto.getPenalties());
        assertNull(dto.getProcedurals());
        assertEquals(BigDecimal.valueOf(45.32), dto.getTime());
        assertEquals(BigDecimal.valueOf(3.31), dto.getHitFactor());
        assertNull(dto.getStagePoints());
        assertNull(dto.getStagePercentage());
        assertNull(dto.getStageRanking());
    }

    // Fully Populated Entity
    @Test
    void testConstructor_whenMatchStageCompetitorEntityFullyPopulated_thenMapsAllFields() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Championship Club");
        club.setAbbreviation("CC");

        IpscMatch match = new IpscMatch();
        match.setId(10L);
        match.setName("Championship Match");
        match.setClub(club);

        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setId(5L);
        matchStage.setMatch(match);
        matchStage.setStageNumber(3);
        matchStage.setStageName("Stage 3 - Precision");

        Competitor competitor = new Competitor();
        competitor.setId(20L);
        competitor.setFirstName("John");
        competitor.setLastName("Champion");
        competitor.setCompetitorNumber("12345");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setId(102L);
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);
        entity.setCompetitorCategory(CompetitorCategory.JUNIOR);
        entity.setFirearmType(FirearmType.HANDGUN);
        entity.setDivision(Division.OPEN);
        entity.setPowerFactor(PowerFactor.MAJOR);
        entity.setScoreA(20);
        entity.setScoreB(10);
        entity.setScoreC(5);
        entity.setScoreD(2);
        entity.setPoints(175);
        entity.setMisses(1);
        entity.setPenalties(0);
        entity.setProcedurals(0);
        entity.setHasDeduction(false);
        entity.setDeductionPercentage(BigDecimal.ZERO);
        entity.setTime(BigDecimal.valueOf(38.25));
        entity.setHitFactor(BigDecimal.valueOf(4.58));
        entity.setStagePoints(BigDecimal.valueOf(175));
        entity.setStagePercentage(BigDecimal.valueOf(95.5));
        entity.setStageRanking(BigDecimal.ONE);
        entity.setIsDisqualified(false);
        entity.setDateEdited(LocalDateTime.of(2026, 2, 25, 10, 30));

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(entity);

        // Assert
        assertEquals(102L, dto.getId());
        assertNotNull(dto.getCompetitor());
        assertEquals("John", dto.getCompetitor().getFirstName());
        assertEquals("Champion", dto.getCompetitor().getLastName());
        assertNotNull(dto.getMatchStage());
        assertEquals(3, dto.getMatchStage().getStageNumber());
        assertNotNull(dto.getClub());
        assertEquals("Championship Club", dto.getClub().getName());
        assertEquals(CompetitorCategory.JUNIOR, dto.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, dto.getFirearmType());
        assertEquals(Division.OPEN, dto.getDivision());
        assertEquals(PowerFactor.MAJOR, dto.getPowerFactor());
        assertEquals(20, dto.getScoreA());
        assertEquals(10, dto.getScoreB());
        assertEquals(5, dto.getScoreC());
        assertEquals(2, dto.getScoreD());
        assertEquals(175, dto.getPoints());
        assertEquals(1, dto.getMisses());
        assertEquals(0, dto.getPenalties());
        assertEquals(0, dto.getProcedurals());
        assertEquals(false, dto.getHasDeduction());
        assertEquals(BigDecimal.ZERO, dto.getDeductionPercentage());
        assertEquals(BigDecimal.valueOf(38.25), dto.getTime());
        assertEquals(BigDecimal.valueOf(4.58), dto.getHitFactor());
        assertEquals(BigDecimal.valueOf(175), dto.getStagePoints());
        assertEquals(BigDecimal.valueOf(95.5), dto.getStagePercentage());
        assertEquals(BigDecimal.ONE, dto.getStageRanking());
        assertEquals(false, dto.getIsDisqualified());
        assertEquals(LocalDateTime.of(2026, 2, 25, 10, 30), dto.getDateEdited());
    }

    @Test
    void testConstructor_whenMatchStageCompetitorEntityFullyPopulated_thenUuidIsNotNull() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Test Club");

        IpscMatch match = new IpscMatch();
        match.setId(10L);
        match.setName("Test Match");
        match.setClub(club);

        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setId(5L);
        matchStage.setMatch(match);
        matchStage.setStageNumber(1);

        Competitor competitor = new Competitor();
        competitor.setId(20L);
        competitor.setFirstName("Test");
        competitor.setLastName("User");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setId(103L);
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(entity);

        // Assert
        assertNotNull(dto.getUuid());
    }

    // Edge Cases - Zero Values
    @Test
    void testConstructor_whenMatchStageCompetitorEntityHasZeroScores_thenMapsZeros() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Test Club");

        IpscMatch match = new IpscMatch();
        match.setId(10L);
        match.setName("Test Match");
        match.setClub(club);

        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setId(5L);
        matchStage.setMatch(match);
        matchStage.setStageNumber(1);

        Competitor competitor = new Competitor();
        competitor.setId(20L);
        competitor.setFirstName("Zero");
        competitor.setLastName("Scores");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setId(104L);
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);
        entity.setScoreA(0);
        entity.setScoreB(0);
        entity.setScoreC(0);
        entity.setScoreD(0);
        entity.setPoints(0);
        entity.setMisses(0);
        entity.setPenalties(0);
        entity.setProcedurals(0);
        entity.setTime(BigDecimal.ZERO);
        entity.setHitFactor(BigDecimal.ZERO);
        entity.setStagePoints(BigDecimal.ZERO);
        entity.setStagePercentage(BigDecimal.ZERO);
        entity.setStageRanking(BigDecimal.ZERO);
        entity.setDeductionPercentage(BigDecimal.ZERO);

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(entity);

        // Assert
        assertEquals(0, dto.getScoreA());
        assertEquals(0, dto.getScoreB());
        assertEquals(0, dto.getScoreC());
        assertEquals(0, dto.getScoreD());
        assertEquals(0, dto.getPoints());
        assertEquals(0, dto.getMisses());
        assertEquals(0, dto.getPenalties());
        assertEquals(0, dto.getProcedurals());
        assertEquals(BigDecimal.ZERO, dto.getTime());
        assertEquals(BigDecimal.ZERO, dto.getHitFactor());
        assertEquals(BigDecimal.ZERO, dto.getStagePoints());
        assertEquals(BigDecimal.ZERO, dto.getStagePercentage());
        assertEquals(BigDecimal.ZERO, dto.getStageRanking());
        assertEquals(BigDecimal.ZERO, dto.getDeductionPercentage());
    }

    // Edge Cases - Negative Values
    @Test
    void testConstructor_whenMatchStageCompetitorEntityHasNegativeScores_thenMapsNegatives() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Test Club");

        IpscMatch match = new IpscMatch();
        match.setId(10L);
        match.setName("Test Match");
        match.setClub(club);

        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setId(5L);
        matchStage.setMatch(match);
        matchStage.setStageNumber(1);

        Competitor competitor = new Competitor();
        competitor.setId(20L);
        competitor.setFirstName("Negative");
        competitor.setLastName("Scores");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setId(105L);
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);
        entity.setPoints(-10);
        entity.setPenalties(-5);
        entity.setTime(BigDecimal.valueOf(-1.0));

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(entity);

        // Assert
        assertEquals(-10, dto.getPoints());
        assertEquals(-5, dto.getPenalties());
        assertEquals(BigDecimal.valueOf(-1.0), dto.getTime());
    }

    // Edge Cases - Large Values
    @Test
    void testConstructor_whenMatchStageCompetitorEntityHasMaxValues_thenMapsMaxValues() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Test Club");

        IpscMatch match = new IpscMatch();
        match.setId(10L);
        match.setName("Test Match");
        match.setClub(club);

        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setId(5L);
        matchStage.setMatch(match);
        matchStage.setStageNumber(1);

        Competitor competitor = new Competitor();
        competitor.setId(20L);
        competitor.setFirstName("Max");
        competitor.setLastName("Values");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setId(106L);
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);
        entity.setScoreA(Integer.MAX_VALUE);
        entity.setScoreB(Integer.MAX_VALUE);
        entity.setPoints(Integer.MAX_VALUE);
        entity.setMisses(Integer.MAX_VALUE);
        entity.setTime(new BigDecimal("999999.99"));
        entity.setHitFactor(new BigDecimal("999.99"));

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(entity);

        // Assert
        assertEquals(Integer.MAX_VALUE, dto.getScoreA());
        assertEquals(Integer.MAX_VALUE, dto.getScoreB());
        assertEquals(Integer.MAX_VALUE, dto.getPoints());
        assertEquals(Integer.MAX_VALUE, dto.getMisses());
        assertEquals(new BigDecimal("999999.99"), dto.getTime());
        assertEquals(new BigDecimal("999.99"), dto.getHitFactor());
    }

    // Constructor mapping - CompetitorDto and MatchStageDto Parameters

    // Both Parameters Null
    @Test
    void testConstructor_whenBothParametersNull_thenHandlesNullGracefully() {
        // Arrange & Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(null, null);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getCompetitor());
        assertNull(dto.getMatchStage());
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
    }

    // CompetitorDto Null
    @Test
    void testConstructor_whenCompetitorDtoNull_thenMatchStageNotSet() {
        // Arrange
        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(2);

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(null, matchStageDto);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getCompetitor());
        assertNull(dto.getMatchStage());
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
    }

    // MatchStageDto Null
    @Test
    void testConstructor_whenMatchStageDtoNull_thenSetsCompetitorOnly() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, null);

        // Assert
        assertNotNull(dto.getUuid());
        assertNotNull(dto.getCompetitor());
        assertEquals("John", dto.getCompetitor().getFirstName());
        assertEquals("Doe", dto.getCompetitor().getLastName());
        assertNull(dto.getMatchStage());
        assertEquals(CompetitorCategory.SENIOR, dto.getCompetitorCategory());
    }

    // Both Parameters Provided
    @Test
    void testConstructor_whenBothParametersProvided_thenMapsAllFields() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Jane");
        competitorDto.setLastName("Smith");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(3);
        matchStageDto.setStageName("Speed Stage");

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Assert
        assertNotNull(dto.getUuid());
        assertNotNull(dto.getCompetitor());
        assertEquals("Jane", dto.getCompetitor().getFirstName());
        assertEquals("Smith", dto.getCompetitor().getLastName());
        assertNotNull(dto.getMatchStage());
        assertEquals(3, dto.getMatchStage().getStageNumber());
        assertEquals("Speed Stage", dto.getMatchStage().getStageName());
        assertEquals(CompetitorCategory.GRAND_SENIOR, dto.getCompetitorCategory());
    }

    // CompetitorDto with Null Default Category
    @Test
    void testConstructor_whenCompetitorDtoHasNullDefaultCategory_thenSetsNullCategory() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("User");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(1);

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Assert
        assertNull(dto.getCompetitorCategory());
    }

    // CompetitorDto with NONE Category
    @Test
    void testConstructor_whenCompetitorDtoHasNoneCategory_thenSetsNoneCategory() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("New");
        competitorDto.setLastName("Shooter");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(1);

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Assert
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
    }

    // Constructor mapping - All Parameters

    // Fully Populated with All Parameters
    @Test
    void testAllArgsConstructor_whenAllParametersProvided_thenMapsAllFields() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("All");
        competitorDto.setLastName("Args");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(5);

        ClubDto clubDto = new ClubDto();
        clubDto.setName("All Args Club");

        LocalDateTime dateEdited = LocalDateTime.of(2026, 2, 20, 15, 30);

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(
                null,  // uuid
                200L,  // id
                10,    // competitorIndex
                5,     // matchStageIndex
                competitorDto,
                matchStageDto,
                CompetitorCategory.SUPER_SENIOR,
                ClubIdentifier.HPSC,
                FirearmType.HANDGUN,
                Division.STANDARD,
                PowerFactor.MAJOR,
                15,    // scoreA
                10,    // scoreB
                5,     // scoreC
                2,     // scoreD
                150,   // points
                1,     // misses
                0,     // penalties
                0,     // procedurals
                false, // hasDeduction
                BigDecimal.ZERO,  // deductionPercentage
                BigDecimal.valueOf(42.50),  // time
                BigDecimal.valueOf(3.53),   // hitFactor
                BigDecimal.valueOf(150),    // stagePoints
                BigDecimal.valueOf(88.5),   // stagePercentage
                BigDecimal.valueOf(3),      // stageRanking
                false, // isDisqualified
                dateEdited
        );

        // Assert
        assertNull(dto.getUuid());
        assertEquals(200L, dto.getId());
        assertEquals(10, dto.getCompetitorIndex());
        assertEquals(5, dto.getMatchStageIndex());
        assertNotNull(dto.getCompetitor());
        assertEquals("All", dto.getCompetitor().getFirstName());
        assertNotNull(dto.getMatchStage());
        assertEquals(5, dto.getMatchStage().getStageNumber());
        assertEquals(CompetitorCategory.SUPER_SENIOR, dto.getCompetitorCategory());
        assertNotNull(dto.getClub());
        assertEquals("All Args Club", dto.getClub().getName());
        assertEquals(FirearmType.HANDGUN, dto.getFirearmType());
        assertEquals(Division.STANDARD, dto.getDivision());
        assertEquals(PowerFactor.MAJOR, dto.getPowerFactor());
        assertEquals(15, dto.getScoreA());
        assertEquals(10, dto.getScoreB());
        assertEquals(5, dto.getScoreC());
        assertEquals(2, dto.getScoreD());
        assertEquals(150, dto.getPoints());
        assertEquals(1, dto.getMisses());
        assertEquals(0, dto.getPenalties());
        assertEquals(0, dto.getProcedurals());
        assertEquals(false, dto.getHasDeduction());
        assertEquals(BigDecimal.ZERO, dto.getDeductionPercentage());
        assertEquals(BigDecimal.valueOf(42.50), dto.getTime());
        assertEquals(BigDecimal.valueOf(3.53), dto.getHitFactor());
        assertEquals(BigDecimal.valueOf(150), dto.getStagePoints());
        assertEquals(BigDecimal.valueOf(88.5), dto.getStagePercentage());
        assertEquals(BigDecimal.valueOf(3), dto.getStageRanking());
        assertEquals(false, dto.getIsDisqualified());
        assertEquals(dateEdited, dto.getDateEdited());
    }

    // All Parameters Null
    @Test
    void testAllArgsConstructor_whenAllParametersNull_thenMapsAllNulls() {
        // Arrange & Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(
                null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null
        );

        // Assert
        assertNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getCompetitorIndex());
        assertNull(dto.getMatchStageIndex());
        assertNull(dto.getCompetitor());
        assertNull(dto.getMatchStage());
        assertNull(dto.getCompetitorCategory());
        assertNull(dto.getClub());
        assertNull(dto.getFirearmType());
        assertNull(dto.getDivision());
        assertNull(dto.getPowerFactor());
        assertNull(dto.getScoreA());
        assertNull(dto.getScoreB());
        assertNull(dto.getScoreC());
        assertNull(dto.getScoreD());
        assertNull(dto.getPoints());
        assertNull(dto.getMisses());
        assertNull(dto.getPenalties());
        assertNull(dto.getProcedurals());
        assertNull(dto.getHasDeduction());
        assertNull(dto.getDeductionPercentage());
        assertNull(dto.getTime());
        assertNull(dto.getHitFactor());
        assertNull(dto.getStagePoints());
        assertNull(dto.getStagePercentage());
        assertNull(dto.getStageRanking());
        assertNull(dto.getIsDisqualified());
        assertNull(dto.getDateEdited());
    }

    // Partial Parameters Provided
    @Test
    void testAllArgsConstructor_whenPartialParametersProvided_thenMapsProvidedFields() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Partial");
        competitorDto.setLastName("Args");

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(
                null,  // uuid
                150L,  // id
                8,     // competitorIndex
                null,  // matchStageIndex
                competitorDto,
                null,  // matchStage
                CompetitorCategory.LADY,
                null,  // club
                FirearmType.HANDGUN,
                null,  // division
                PowerFactor.MINOR,
                10,    // scoreA
                null,  // scoreB
                null,  // scoreC
                null,  // scoreD
                100,   // points
                null,  // misses
                null,  // penalties
                null,  // procedurals
                null,  // hasDeduction
                null,  // deductionPercentage
                BigDecimal.valueOf(50.00),  // time
                null,  // hitFactor
                null,  // stagePoints
                null,  // stagePercentage
                null,  // stageRanking
                false, // isDisqualified
                null   // dateEdited
        );

        // Assert
        assertNull(dto.getUuid());
        assertEquals(150L, dto.getId());
        assertEquals(8, dto.getCompetitorIndex());
        assertNull(dto.getMatchStageIndex());
        assertNotNull(dto.getCompetitor());
        assertEquals("Partial", dto.getCompetitor().getFirstName());
        assertNull(dto.getMatchStage());
        assertEquals(CompetitorCategory.LADY, dto.getCompetitorCategory());
        assertNull(dto.getClub());
        assertEquals(FirearmType.HANDGUN, dto.getFirearmType());
        assertNull(dto.getDivision());
        assertEquals(PowerFactor.MINOR, dto.getPowerFactor());
        assertEquals(10, dto.getScoreA());
        assertNull(dto.getScoreB());
        assertNull(dto.getScoreC());
        assertNull(dto.getScoreD());
        assertEquals(100, dto.getPoints());
        assertNull(dto.getMisses());
        assertNull(dto.getPenalties());
        assertNull(dto.getProcedurals());
        assertNull(dto.getHasDeduction());
        assertNull(dto.getDeductionPercentage());
        assertEquals(BigDecimal.valueOf(50.00), dto.getTime());
        assertNull(dto.getHitFactor());
        assertNull(dto.getStagePoints());
        assertNull(dto.getStagePercentage());
        assertNull(dto.getStageRanking());
        assertEquals(false, dto.getIsDisqualified());
        assertNull(dto.getDateEdited());
    }

    // init() mappings

    // Null Parameters
    @Test
    void testInit_whenAllParametersNull_thenKeepsExistingValues() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setScoreA(10);
        dto.setPoints(100);
        dto.setTime(BigDecimal.valueOf(45.0));

        // Act
        dto.init(null, null, null);

        // Assert
        assertEquals(10, dto.getScoreA());
        assertEquals(100, dto.getPoints());
        assertEquals(BigDecimal.valueOf(45.0), dto.getTime());
    }

    @Test
    void testInit_whenScoreResponseNull_thenKeepsExistingValues() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setScoreA(5);
        dto.setPoints(50);

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(10);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(2);

        // Act
        dto.init(null, enrolledResponse, matchStageDto);

        // Assert
        assertEquals(5, dto.getScoreA());
        assertEquals(50, dto.getPoints());
    }

    @Test
    void testInit_whenEnrolledResponseNull_thenSetsScoreDataOnly() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(15);
        scoreResponse.setScoreB(10);
        scoreResponse.setFinalScore(125);
        scoreResponse.setTime("42.50");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(3);

        // Act
        dto.init(scoreResponse, null, matchStageDto);

        // Assert
        assertEquals(15, dto.getScoreA());
        assertEquals(10, dto.getScoreB());
        assertEquals(125, dto.getPoints());
        assertEquals("42.50", dto.getTime().toString());
        assertNull(dto.getCompetitorIndex());
        assertNull(dto.getMatchStageIndex());
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
    }

    @Test
    void testInit_whenMatchStageDtoNull_thenSetsScoreDataWithoutPercentage() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(12);
        scoreResponse.setFinalScore(100);
        scoreResponse.setTime("40.00");

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(5);
        enrolledResponse.setDivisionId(1);

        // Act
        dto.init(scoreResponse, enrolledResponse, null);

        // Assert
        assertEquals(12, dto.getScoreA());
        assertEquals(100, dto.getPoints());
        assertEquals(BigDecimal.valueOf(100), dto.getStagePoints());
        assertNull(dto.getStagePercentage());
        assertNull(dto.getMatchStageIndex());
    }

    // Basic Field Mapping from ScoreResponse
    @Test
    void testInit_whenScoreResponseProvided_thenMapsAllScoreFields() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(20);
        scoreResponse.setScoreB(15);
        scoreResponse.setScoreC(10);
        scoreResponse.setScoreD(5);
        scoreResponse.setFinalScore(175);
        scoreResponse.setMisses(2);
        scoreResponse.setPenalties(1);
        scoreResponse.setProcedurals(0);
        scoreResponse.setDeduction(false);
        scoreResponse.setDeductionPercentage("0.00");
        scoreResponse.setIsDisqualified(false);
        scoreResponse.setTime("38.25");
        scoreResponse.setHitFactor("4.58");
        scoreResponse.setLastModified(LocalDateTime.of(2026, 2, 20, 10, 30));

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(1);
        matchStageDto.setMaxPoints(200);

        // Act
        dto.init(scoreResponse, null, matchStageDto);

        // Assert
        assertEquals(20, dto.getScoreA());
        assertEquals(15, dto.getScoreB());
        assertEquals(10, dto.getScoreC());
        assertEquals(5, dto.getScoreD());
        assertEquals(175, dto.getPoints());
        assertEquals(2, dto.getMisses());
        assertEquals(1, dto.getPenalties());
        assertEquals(0, dto.getProcedurals());
        assertEquals(false, dto.getHasDeduction());
        assertEquals(new BigDecimal("0.00"), dto.getDeductionPercentage());
        assertEquals(false, dto.getIsDisqualified());
        assertEquals(new BigDecimal("38.25"), dto.getTime());
        assertEquals(new BigDecimal("4.58"), dto.getHitFactor());
        assertEquals(BigDecimal.valueOf(175), dto.getStagePoints());
        assertNotNull(dto.getStagePercentage());
        assertEquals(LocalDateTime.of(2026, 2, 20, 10, 30), dto.getDateEdited());
    }

    // Null Fields in ScoreResponse
    @Test
    void testInit_whenScoreResponseHasNullFields_thenMapsNulls() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(null);
        scoreResponse.setScoreB(null);
        scoreResponse.setScoreC(null);
        scoreResponse.setScoreD(null);
        scoreResponse.setFinalScore(null);
        scoreResponse.setMisses(null);
        scoreResponse.setPenalties(null);
        scoreResponse.setProcedurals(null);
        scoreResponse.setDeduction(null);
        scoreResponse.setDeductionPercentage(null);
        scoreResponse.setIsDisqualified(null);
        scoreResponse.setTime(null);
        scoreResponse.setHitFactor(null);
        scoreResponse.setLastModified(null);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(2);

        // Act
        dto.init(scoreResponse, null, matchStageDto);

        // Assert
        assertNull(dto.getScoreA());
        assertNull(dto.getScoreB());
        assertNull(dto.getScoreC());
        assertNull(dto.getScoreD());
        assertNull(dto.getPoints());
        assertNull(dto.getMisses());
        assertNull(dto.getPenalties());
        assertNull(dto.getProcedurals());
        assertNull(dto.getHasDeduction());
        assertEquals(BigDecimal.ZERO, dto.getDeductionPercentage());
        assertNull(dto.getIsDisqualified());
        assertEquals(BigDecimal.ZERO, dto.getTime());
        assertEquals(BigDecimal.ZERO, dto.getHitFactor());
        assertEquals(BigDecimal.ZERO, dto.getStagePoints());
        assertNull(dto.getDateEdited());
    }

    // Empty String Fields in ScoreResponse
    @Test
    void testInit_whenScoreResponseHasEmptyStrings_thenConvertsToZero() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(10);
        scoreResponse.setFinalScore(100);
        scoreResponse.setTime("");
        scoreResponse.setHitFactor("");
        scoreResponse.setDeductionPercentage("");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(3);

        // Act
        dto.init(scoreResponse, null, matchStageDto);

        // Assert
        assertEquals(BigDecimal.ZERO, dto.getTime());
        assertEquals(BigDecimal.ZERO, dto.getHitFactor());
        assertEquals(BigDecimal.ZERO, dto.getDeductionPercentage());
    }

    // Blank String Fields in ScoreResponse
    @Test
    void testInit_whenScoreResponseHasBlankStrings_thenConvertsToZero() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(5);
        scoreResponse.setFinalScore(50);
        scoreResponse.setTime("   ");
        scoreResponse.setHitFactor("   ");
        scoreResponse.setDeductionPercentage("   ");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(4);

        // Act
        dto.init(scoreResponse, null, matchStageDto);

        // Assert
        assertEquals(BigDecimal.ZERO, dto.getTime());
        assertEquals(BigDecimal.ZERO, dto.getHitFactor());
        assertEquals(BigDecimal.ZERO, dto.getDeductionPercentage());
    }

    // Partially Populated ScoreResponse
    @Test
    void testInit_whenScoreResponsePartiallyPopulated_thenMapsSetFieldsAndNulls() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(15);
        scoreResponse.setScoreB(10);
        // scoreC, scoreD not set
        scoreResponse.setFinalScore(125);
        scoreResponse.setMisses(1);
        // penalties, procedurals not set
        scoreResponse.setTime("45.50");
        // hitFactor not set

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(5);

        // Act
        dto.init(scoreResponse, null, matchStageDto);

        // Assert
        assertEquals(15, dto.getScoreA());
        assertEquals(10, dto.getScoreB());
        assertNull(dto.getScoreC());
        assertNull(dto.getScoreD());
        assertEquals(125, dto.getPoints());
        assertEquals(1, dto.getMisses());
        assertNull(dto.getPenalties());
        assertNull(dto.getProcedurals());
        assertEquals(new BigDecimal("45.50"), dto.getTime());
        assertEquals(BigDecimal.ZERO, dto.getHitFactor());
    }

    // Fully Populated with All Three Parameters
    @Test
    void testInit_whenAllParametersFullyPopulated_thenMapsAllFields() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(20);
        scoreResponse.setScoreB(15);
        scoreResponse.setScoreC(10);
        scoreResponse.setScoreD(5);
        scoreResponse.setFinalScore(180);
        scoreResponse.setMisses(1);
        scoreResponse.setPenalties(0);
        scoreResponse.setProcedurals(0);
        scoreResponse.setDeduction(false);
        scoreResponse.setDeductionPercentage("0.00");
        scoreResponse.setIsDisqualified(false);
        scoreResponse.setTime("40.00");
        scoreResponse.setHitFactor("4.50");
        scoreResponse.setLastModified(LocalDateTime.of(2026, 2, 18, 14, 45));

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(25);
        enrolledResponse.setDivisionId(1);
        enrolledResponse.setCompetitorCategoryId(2);
        enrolledResponse.setMajorPowerFactor(true);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(8);
        matchStageDto.setStageNumber(8);
        matchStageDto.setMaxPoints(200);

        // Act
        dto.init(scoreResponse, enrolledResponse, matchStageDto);

        // Assert
        // Score fields
        assertEquals(20, dto.getScoreA());
        assertEquals(15, dto.getScoreB());
        assertEquals(10, dto.getScoreC());
        assertEquals(5, dto.getScoreD());
        assertEquals(180, dto.getPoints());
        assertEquals(1, dto.getMisses());
        assertEquals(0, dto.getPenalties());
        assertEquals(0, dto.getProcedurals());

        // Deduction fields
        assertEquals(false, dto.getHasDeduction());
        assertEquals(new BigDecimal("0.00"), dto.getDeductionPercentage());

        // Time and hit factor
        assertEquals(new BigDecimal("40.00"), dto.getTime());
        assertEquals(new BigDecimal("4.50"), dto.getHitFactor());

        // Stage performance
        assertEquals(BigDecimal.valueOf(180), dto.getStagePoints());
        assertNotNull(dto.getStagePercentage());

        // Competitor attributes from EnrolledResponse
        assertEquals(25, dto.getCompetitorIndex());
        assertEquals(8, dto.getMatchStageIndex());
        assertEquals(PowerFactor.MAJOR, dto.getPowerFactor());
        assertNotNull(dto.getDivision());
        assertNotNull(dto.getFirearmType());
        assertNotNull(dto.getCompetitorCategory());

        // Status and date
        assertEquals(false, dto.getIsDisqualified());
        assertEquals(LocalDateTime.of(2026, 2, 18, 14, 45), dto.getDateEdited());

        // Match stage
        assertNotNull(dto.getMatchStage());
        assertEquals(8, dto.getMatchStage().getStageNumber());
    }

    // EnrolledResponse Field Mapping
    @Test
    void testInit_whenEnrolledResponseProvided_thenMapsCompetitorAttributes() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(10);
        scoreResponse.setFinalScore(100);

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(15);
        enrolledResponse.setDivisionId(2);
        enrolledResponse.setCompetitorCategoryId(3);
        enrolledResponse.setMajorPowerFactor(false);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(6);

        // Act
        dto.init(scoreResponse, enrolledResponse, matchStageDto);

        // Assert
        assertEquals(15, dto.getCompetitorIndex());
        assertEquals(6, dto.getMatchStageIndex());
        assertEquals(PowerFactor.MINOR, dto.getPowerFactor());
        assertNotNull(dto.getDivision());
        assertNotNull(dto.getFirearmType());
        assertNotNull(dto.getCompetitorCategory());
        assertNotNull(dto.getMatchStage());
    }

    // EnrolledResponse with Null Fields
    @Test
    void testInit_whenEnrolledResponseHasNullFields_thenHandlesNullsGracefully() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(8);
        scoreResponse.setFinalScore(80);

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(null);
        enrolledResponse.setDivisionId(null);
        enrolledResponse.setCompetitorCategoryId(null);
        enrolledResponse.setMajorPowerFactor(null);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(7);

        // Act
        dto.init(scoreResponse, enrolledResponse, matchStageDto);

        // Assert
        assertNull(dto.getCompetitorIndex());
        assertEquals(7, dto.getMatchStageIndex());
        assertEquals(PowerFactor.MINOR, dto.getPowerFactor());
        assertNull(dto.getDivision());
        assertNull(dto.getFirearmType());
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
    }

    // Edge Cases - Zero Values
    @Test
    void testInit_whenScoreResponseHasZeroValues_thenMapsZeros() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(0);
        scoreResponse.setScoreB(0);
        scoreResponse.setScoreC(0);
        scoreResponse.setScoreD(0);
        scoreResponse.setFinalScore(0);
        scoreResponse.setMisses(0);
        scoreResponse.setPenalties(0);
        scoreResponse.setProcedurals(0);
        scoreResponse.setTime("0.00");
        scoreResponse.setHitFactor("0.00");
        scoreResponse.setDeductionPercentage("0.00");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(9);
        matchStageDto.setMaxPoints(0);

        // Act
        dto.init(scoreResponse, null, matchStageDto);

        // Assert
        assertEquals(0, dto.getScoreA());
        assertEquals(0, dto.getScoreB());
        assertEquals(0, dto.getScoreC());
        assertEquals(0, dto.getScoreD());
        assertEquals(0, dto.getPoints());
        assertEquals(0, dto.getMisses());
        assertEquals(0, dto.getPenalties());
        assertEquals(0, dto.getProcedurals());
        assertEquals(new BigDecimal("0.00"), dto.getTime());
        assertEquals(new BigDecimal("0.00"), dto.getHitFactor());
        assertEquals(BigDecimal.ZERO, dto.getStagePoints());
    }

    // Edge Cases - Negative Values
    @Test
    void testInit_whenScoreResponseHasNegativeValues_thenMapsNegatives() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(-5);
        scoreResponse.setFinalScore(-50);
        scoreResponse.setMisses(-1);
        scoreResponse.setPenalties(-10);
        scoreResponse.setTime("-1.00");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(10);

        // Act
        dto.init(scoreResponse, null, matchStageDto);

        // Assert
        assertEquals(-5, dto.getScoreA());
        assertEquals(-50, dto.getPoints());
        assertEquals(-1, dto.getMisses());
        assertEquals(-10, dto.getPenalties());
        assertEquals(new BigDecimal("-1.00"), dto.getTime());
    }

    // Edge Cases - Large Values
    @Test
    void testInit_whenScoreResponseHasMaxValues_thenMapsMaxValues() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(Integer.MAX_VALUE);
        scoreResponse.setScoreB(Integer.MAX_VALUE);
        scoreResponse.setFinalScore(Integer.MAX_VALUE);
        scoreResponse.setMisses(Integer.MAX_VALUE);
        scoreResponse.setTime("999999.99");
        scoreResponse.setHitFactor("999.99");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(11);
        matchStageDto.setMaxPoints(Integer.MAX_VALUE);

        // Act
        dto.init(scoreResponse, null, matchStageDto);

        // Assert
        assertEquals(Integer.MAX_VALUE, dto.getScoreA());
        assertEquals(Integer.MAX_VALUE, dto.getScoreB());
        assertEquals(Integer.MAX_VALUE, dto.getPoints());
        assertEquals(Integer.MAX_VALUE, dto.getMisses());
        assertEquals(new BigDecimal("999999.99"), dto.getTime());
        assertEquals(new BigDecimal("999.99"), dto.getHitFactor());
    }

    // Stage Percentage Calculation
    @Test
    void testInit_whenMatchStageDtoHasMaxPoints_thenCalculatesStagePercentage() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setFinalScore(150);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(12);
        matchStageDto.setMaxPoints(200);

        // Act
        dto.init(scoreResponse, null, matchStageDto);

        // Assert
        assertEquals(BigDecimal.valueOf(150), dto.getStagePoints());
        assertNotNull(dto.getStagePercentage());
        // 150/200 = 75%
        assertTrue(dto.getStagePercentage().compareTo(BigDecimal.valueOf(70)) > 0);
        assertTrue(dto.getStagePercentage().compareTo(BigDecimal.valueOf(80)) < 0);
    }

    @Test
    void testInit_whenMatchStageDtoHasNullMaxPoints_thenStagePercentageIsNull() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setFinalScore(150);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(13);
        matchStageDto.setMaxPoints(null);

        // Act
        dto.init(scoreResponse, null, matchStageDto);

        // Assert
        assertEquals(BigDecimal.valueOf(150), dto.getStagePoints());
        assertNull(dto.getStagePercentage());
    }

    // PowerFactor Mapping
    @Test
    void testInit_whenEnrolledResponseHasMajorPowerFactorTrue_thenSetsMajor() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setFinalScore(100);

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(20);
        enrolledResponse.setMajorPowerFactor(true);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(14);

        // Act
        dto.init(scoreResponse, enrolledResponse, matchStageDto);

        // Assert
        assertEquals(PowerFactor.MAJOR, dto.getPowerFactor());
    }

    @Test
    void testInit_whenEnrolledResponseHasMajorPowerFactorFalse_thenSetsMinor() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setFinalScore(100);

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(21);
        enrolledResponse.setMajorPowerFactor(false);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(15);

        // Act
        dto.init(scoreResponse, enrolledResponse, matchStageDto);

        // Assert
        assertEquals(PowerFactor.MINOR, dto.getPowerFactor());
    }

    // CompetitorCategory Mapping
    @Test
    void testInit_whenEnrolledResponseHasCompetitorCategoryId_thenMapsCategory() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setFinalScore(100);

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(22);
        enrolledResponse.setCompetitorCategoryId(2);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(16);

        // Act
        dto.init(scoreResponse, enrolledResponse, matchStageDto);

        // Assert
        assertNotNull(dto.getCompetitorCategory());
        assertNotEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
    }

    @Test
    void testInit_whenEnrolledResponseHasNullCompetitorCategoryId_thenSetsNone() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setFinalScore(100);

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(23);
        enrolledResponse.setCompetitorCategoryId(null);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(17);

        // Act
        dto.init(scoreResponse, enrolledResponse, matchStageDto);

        // Assert
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
    }

    @Test
    void testInit_whenEnrolledResponseHasZeroCompetitorCategoryId_thenSetsNone() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setFinalScore(100);

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(24);
        enrolledResponse.setCompetitorCategoryId(0);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(18);

        // Act
        dto.init(scoreResponse, enrolledResponse, matchStageDto);

        // Assert
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
    }

    // Overwriting Existing Values
    @Test
    void testInit_whenOverwritingExistingValues_thenReplacesProperly() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setScoreA(99);
        dto.setScoreB(99);
        dto.setPoints(999);
        dto.setMisses(99);
        dto.setPenalties(99);
        dto.setTime(BigDecimal.valueOf(999.99));
        dto.setHitFactor(BigDecimal.valueOf(99.99));
        dto.setStagePoints(BigDecimal.valueOf(999));
        dto.setCompetitorIndex(999);
        dto.setMatchStageIndex(999);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setScoreA(10);
        scoreResponse.setScoreB(8);
        scoreResponse.setFinalScore(90);
        scoreResponse.setMisses(1);
        scoreResponse.setPenalties(0);
        scoreResponse.setTime("30.00");
        scoreResponse.setHitFactor("3.00");

        EnrolledResponse enrolledResponse = new EnrolledResponse();
        enrolledResponse.setCompetitorId(50);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setIndex(20);

        // Act
        dto.init(scoreResponse, enrolledResponse, matchStageDto);

        // Assert
        assertEquals(10, dto.getScoreA());
        assertEquals(8, dto.getScoreB());
        assertEquals(90, dto.getPoints());
        assertEquals(1, dto.getMisses());
        assertEquals(0, dto.getPenalties());
        assertEquals(new BigDecimal("30.00"), dto.getTime());
        assertEquals(new BigDecimal("3.00"), dto.getHitFactor());
        assertEquals(BigDecimal.valueOf(90), dto.getStagePoints());
        assertEquals(50, dto.getCompetitorIndex());
        assertEquals(20, dto.getMatchStageIndex());
    }

    // toString() behavior

    // Fully Populated - MatchStage and Competitor Provided
    @Test
    void testToString_whenMatchStageAndCompetitorFullyPopulated_thenReturnsCompleteString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("TC");

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Championship Match");
        matchDto.setClub(clubDto);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(5);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertTrue(result.contains("5"));
        assertTrue(result.contains("Championship Match"));
        assertTrue(result.contains("Test Club"));
        assertTrue(result.contains("John"));
        assertTrue(result.contains("Doe"));
        assertTrue(result.contains(":"));
    }

    @Test
    void testToString_whenFullyPopulatedWithAllScores_thenReturnsFormattedString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Premium Club");
        clubDto.setAbbreviation("PC");

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Annual Match");
        matchDto.setClub(clubDto);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(3);
        matchStageDto.setStageName("Speed Stage");
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Jane");
        competitorDto.setLastName("Smith");
        competitorDto.setCompetitorNumber("12345");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);
        dto.setScoreA(20);
        dto.setScoreB(15);
        dto.setPoints(175);
        dto.setTime(BigDecimal.valueOf(42.50));

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("3"));
        assertTrue(result.contains("Annual Match"));
        assertTrue(result.contains("Jane"));
        assertTrue(result.contains("Smith"));
    }

    // MatchStage Null
    @Test
    void testToString_whenMatchStageNull_thenThrowsNullPointerException() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("User");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setMatchStage(null);
        dto.setCompetitor(competitorDto);

        // Act & Assert
        assertThrows(NullPointerException.class, dto::toString);
    }

    // Competitor Null
    @Test
    void testToString_whenCompetitorNull_thenThrowsNullPointerException() {
        // Arrange
        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(1);

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setMatchStage(matchStageDto);
        dto.setCompetitor(null);

        // Act & Assert
        assertThrows(NullPointerException.class, dto::toString);
    }

    // Both Null
    @Test
    void testToString_whenBothMatchStageAndCompetitorNull_thenThrowsNullPointerException() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setMatchStage(null);
        dto.setCompetitor(null);

        // Act & Assert
        assertThrows(NullPointerException.class, dto::toString);
    }

    // MatchStage with Null Fields
    @Test
    void testToString_whenMatchStageHasNullFields_thenReturnsStringWithNullValues() {
        // Arrange
        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(0);
        matchStageDto.setMatch(null);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Test");
        competitorDto.setLastName("User");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("0"));
        assertTrue(result.contains(":"));
    }

    // Competitor with Null Name Fields
    @Test
    void testToString_whenCompetitorHasNullNames_thenReturnsStringWithNullNames() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Test Match");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(2);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName(null);
        competitorDto.setLastName(null);

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("2"));
        assertTrue(result.contains("Test Match"));
    }

    // Competitor with Empty Names
    @Test
    void testToString_whenCompetitorHasEmptyNames_thenReturnsStringWithEmptyNames() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Test Match");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(3);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("");
        competitorDto.setLastName("");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("3"));
        assertTrue(result.contains(":"));
    }

    // Competitor with Blank Names
    @Test
    void testToString_whenCompetitorHasBlankNames_thenReturnsStringWithBlankNames() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Test Match");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(4);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("   ");
        competitorDto.setLastName("   ");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("4"));
        assertTrue(result.contains(":"));
    }

    // Partially Populated MatchStage
    @Test
    void testToString_whenMatchStagePartiallyPopulated_thenReturnsPartialString() {
        // Arrange
        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(6);
        // Match is not set

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Partial");
        competitorDto.setLastName("User");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("6"));
        assertTrue(result.contains("Partial"));
        assertTrue(result.contains("User"));
    }

    // Partially Populated Competitor
    @Test
    void testToString_whenCompetitorPartiallyPopulated_thenReturnsPartialString() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Partial Match");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(7);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("OnlyFirst");
        competitorDto.setLastName(null);

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("7"));
        assertTrue(result.contains("Partial Match"));
        assertTrue(result.contains("OnlyFirst"));
    }

    // Score Fields Do Not Affect toString
    @Test
    void testToString_whenScoreFieldsSet_thenDoesNotIncludeScores() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Score Match");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(8);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Score");
        competitorDto.setLastName("Shooter");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);
        dto.setScoreA(20);
        dto.setScoreB(15);
        dto.setScoreC(10);
        dto.setScoreD(5);
        dto.setPoints(175);
        dto.setTime(BigDecimal.valueOf(42.50));
        dto.setHitFactor(BigDecimal.valueOf(4.12));

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertFalse(result.contains("20"));
        assertFalse(result.contains("175"));
        assertFalse(result.contains("42.50"));
        assertFalse(result.contains("4.12"));
        assertTrue(result.contains("8"));
        assertTrue(result.contains("Score"));
    }

    // Stage Number Zero
    @Test
    void testToString_whenStageNumberZero_thenReturnsZero() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Zero Stage");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(0);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Zero");
        competitorDto.setLastName("Test");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("0"));
        assertTrue(result.contains("Zero Stage"));
    }

    // Stage Number Negative
    @Test
    void testToString_whenStageNumberNegative_thenReturnsNegative() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Negative Test");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(-1);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Negative");
        competitorDto.setLastName("Test");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("-1"));
    }

    // Large Stage Number
    @Test
    void testToString_whenStageNumberLarge_thenReturnsLargeNumber() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Large Stage Match");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(999);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Large");
        competitorDto.setLastName("Stage");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("999"));
    }

    // Club Information in MatchStage
    @Test
    void testToString_whenMatchStageHasClub_thenIncludesClubInfo() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Elite Club");
        clubDto.setAbbreviation("EC");

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Elite Match");
        matchDto.setClub(clubDto);

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(10);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Elite");
        competitorDto.setLastName("Shooter");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("10"));
        assertTrue(result.contains("Elite Match"));
        assertTrue(result.contains("Elite Club") || result.contains("EC"));
        assertTrue(result.contains("Elite"));
        assertTrue(result.contains("Shooter"));
    }

    // Format Consistency
    @Test
    void testToString_whenCalledMultipleTimes_thenReturnsConsistentFormat() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Consistency Match");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(11);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Consistent");
        competitorDto.setLastName("User");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result1 = dto.toString();
        String result2 = dto.toString();
        String result3 = dto.toString();

        // Assert
        assertEquals(result1, result2);
        assertEquals(result2, result3);
    }

    // Mutability Check
    @Test
    void testToString_whenFieldsChangedAfterCreation_thenReflectsChanges() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Original Match");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(12);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Original");
        competitorDto.setLastName("User");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act - Get original
        String originalResult = dto.toString();

        // Modify
        competitorDto.setFirstName("Modified");
        competitorDto.setLastName("NewUser");

        // Act - Get modified
        String modifiedResult = dto.toString();

        // Assert
        assertTrue(originalResult.contains("Original"));
        assertTrue(modifiedResult.contains("Modified"));
        assertTrue(modifiedResult.contains("NewUser"));
        assertNotEquals(originalResult, modifiedResult);
    }

    // Special Characters in Names
    @Test
    void testToString_whenNamesContainSpecialCharacters_thenHandlesCorrectly() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Special-Match #1");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(13);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("O'Brien");
        competitorDto.setLastName("Smith-Jones");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("O'Brien"));
        assertTrue(result.contains("Smith-Jones"));
        assertTrue(result.contains("Special-Match #1"));
    }

    // Unicode Characters
    @Test
    void testToString_whenNamesContainUnicodeCharacters_thenHandlesCorrectly() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Чемпионат");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(14);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("José");
        competitorDto.setLastName("Müller");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("José"));
        assertTrue(result.contains("Müller"));
        assertTrue(result.contains("Чемпионат"));
    }

    // Long Names
    @Test
    void testToString_whenNamesAreLong_thenHandlesCorrectly() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Very Long Match Name That Goes On And On And On");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(15);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Verylongfirstnamewithoutspaces");
        competitorDto.setLastName("Equallylonglastnamewithoutspaceseither");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Verylongfirstnamewithoutspaces"));
        assertTrue(result.contains("Equallylonglastnamewithoutspaceseither"));
        assertTrue(result.contains("Very Long Match Name"));
    }

    // Competitor Number Not in toString
    @Test
    void testToString_whenCompetitorNumberSet_thenDoesNotIncludeCompetitorNumber() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Number Match");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(16);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Number");
        competitorDto.setLastName("Test");
        competitorDto.setCompetitorNumber("99999");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        // Competitor number should not be in the result
        assertFalse(result.contains("99999"));
        assertTrue(result.contains("Number"));
        assertTrue(result.contains("Test"));
    }

    // Category and Division Do Not Affect toString
    @Test
    void testToString_whenCategoryAndDivisionSet_thenDoesNotIncludeThem() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Category Match");

        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setStageNumber(17);
        matchStageDto.setMatch(matchDto);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("Category");
        competitorDto.setLastName("Test");

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);
        dto.setCompetitorCategory(CompetitorCategory.SENIOR);
        dto.setDivision(Division.PRODUCTION);
        dto.setFirearmType(FirearmType.HANDGUN);
        dto.setPowerFactor(PowerFactor.MAJOR);

        // Act
        String result = dto.toString();

        // Assert
        assertNotNull(result);
        assertFalse(result.contains("SENIOR"));
        assertFalse(result.contains("PRODUCTION"));
        assertFalse(result.contains("HANDGUN"));
        assertFalse(result.contains("MAJOR"));
        assertTrue(result.contains("17"));
        assertTrue(result.contains("Category"));
    }
}
