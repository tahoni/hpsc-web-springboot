package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.response.EnrolledResponse;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// TODO: redo this
class MatchStageCompetitorDtoTest {

    @Test
    void testConstructor_withMatchStageCompetitorEntity_ShouldInitializeFields() {
        // Arrange
        MatchStageCompetitor entity = mock(MatchStageCompetitor.class);
        Competitor competitor = mock(Competitor.class);
        IpscMatchStage matchStage = mock(IpscMatchStage.class);

        when(entity.getId()).thenReturn(1L);
        when(entity.getCompetitor()).thenReturn(competitor);
        when(entity.getMatchStage()).thenReturn(matchStage);
        when(entity.getCompetitorCategory()).thenReturn(CompetitorCategory.SENIOR);
        when(entity.getFirearmType()).thenReturn(FirearmType.HANDGUN);
        when(entity.getDivision()).thenReturn(Division.PRODUCTION);
        when(entity.getPowerFactor()).thenReturn(PowerFactor.MINOR);
        when(entity.getScoreA()).thenReturn(10);
        when(entity.getScoreB()).thenReturn(5);
        when(entity.getScoreC()).thenReturn(3);
        when(entity.getScoreD()).thenReturn(2);
        when(entity.getPoints()).thenReturn(100);
        when(entity.getMisses()).thenReturn(1);
        when(entity.getPenalties()).thenReturn(0);
        when(entity.getProcedurals()).thenReturn(0);
        when(entity.getHasDeduction()).thenReturn(false);
        when(entity.getDeductionPercentage()).thenReturn(BigDecimal.ZERO);
        when(entity.getIsDisqualified()).thenReturn(false);
        when(entity.getTime()).thenReturn(new BigDecimal("10.5"));
        when(entity.getHitFactor()).thenReturn(new BigDecimal("5.2"));
        when(entity.getStagePoints()).thenReturn(new BigDecimal("90.0"));
        when(entity.getStagePercentage()).thenReturn(new BigDecimal("90.0"));
        when(entity.getStageRanking()).thenReturn(new BigDecimal("1"));
        when(entity.getDateCreated()).thenReturn(LocalDateTime.now());
        when(entity.getDateEdited()).thenReturn(LocalDateTime.now());

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(entity);

        // Assert
        assertEquals(1L, dto.getId());
        assertNotNull(dto.getCompetitor());
        assertNotNull(dto.getMatchStage());
        assertEquals(CompetitorCategory.SENIOR, dto.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, dto.getFirearmType());
        assertEquals(Division.PRODUCTION, dto.getDivision());
        assertEquals(PowerFactor.MINOR, dto.getPowerFactor());
        assertEquals(10, dto.getScoreA());
        assertEquals(5, dto.getScoreB());
        assertEquals(3, dto.getScoreC());
        assertEquals(2, dto.getScoreD());
        assertEquals(100, dto.getPoints());
        assertEquals(1, dto.getMisses());
        assertEquals(0, dto.getPenalties());
        assertEquals(0, dto.getProcedurals());
        assertFalse(dto.getHasDeduction());
        assertEquals(BigDecimal.ZERO, dto.getDeductionPercentage());
        assertFalse(dto.getIsDisqualified());
        assertEquals(new BigDecimal("10.5"), dto.getTime());
        assertEquals(new BigDecimal("5.2"), dto.getHitFactor());
        assertEquals(new BigDecimal("90.0"), dto.getStagePoints());
        assertEquals(new BigDecimal("90.0"), dto.getStagePercentage());
        assertEquals(new BigDecimal("1"), dto.getStageRanking());
    }

    @Test
    void testConstructor_withCompetitorDtoAndMatchStageDto_ShouldInitializeFields() {
        // Arrange
        CompetitorDto competitorDto = mock(CompetitorDto.class);
        MatchStageDto matchStageDto = mock(MatchStageDto.class);
        when(competitorDto.getDefaultCompetitorCategory()).thenReturn(CompetitorCategory.LADY);

        // Act
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto(competitorDto, matchStageDto);

        // Assert
        assertEquals(competitorDto, dto.getCompetitor());
        assertEquals(matchStageDto, dto.getMatchStage());
        assertEquals(CompetitorCategory.LADY, dto.getCompetitorCategory());
        assertNotNull(dto.getDateCreated());
        assertNotNull(dto.getDateUpdated());
        assertNotNull(dto.getDateEdited());
    }

    @Test
    void testInit_withScoreResponseAndEnrolledResponse_ShouldInitializeFields() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        ScoreResponse scoreResponse = mock(ScoreResponse.class);
        EnrolledResponse enrolledResponse = mock(EnrolledResponse.class);
        MatchStageDto matchStageDto = mock(MatchStageDto.class);

        when(scoreResponse.getScoreA()).thenReturn(10);
        when(scoreResponse.getScoreB()).thenReturn(5);
        when(scoreResponse.getScoreC()).thenReturn(3);
        when(scoreResponse.getScoreD()).thenReturn(2);
        when(scoreResponse.getFinalScore()).thenReturn(100);
        when(scoreResponse.getMisses()).thenReturn(1);
        when(scoreResponse.getPenalties()).thenReturn(0);
        when(scoreResponse.getProcedurals()).thenReturn(0);
        when(scoreResponse.getDeduction()).thenReturn(false);
        when(scoreResponse.getDeductionPercentage()).thenReturn("0.0");
        when(scoreResponse.getIsDisqualified()).thenReturn(false);
        when(scoreResponse.getTime()).thenReturn("10.5");
        when(scoreResponse.getHitFactor()).thenReturn("5.2");
        when(scoreResponse.getLastModified()).thenReturn(LocalDateTime.now());

        when(enrolledResponse.getCompetitorId()).thenReturn(1);
        when(enrolledResponse.getMatchId()).thenReturn(1);
        when(enrolledResponse.getMajorPowerFactor()).thenReturn(false);
        when(enrolledResponse.getDivisionId()).thenReturn(1); // Assuming `1` maps to a valid Division
        when(enrolledResponse.getCompetitorCategoryId()).thenReturn(1); // Assuming `1` maps to a valid Category

        when(matchStageDto.getIndex()).thenReturn(1);
        when(matchStageDto.getMaxPoints()).thenReturn(120);

        // Act
        dto.init(scoreResponse, enrolledResponse, matchStageDto);

        // Assert
        assertEquals(10, dto.getScoreA());
        assertEquals(5, dto.getScoreB());
        assertEquals(3, dto.getScoreC());
        assertEquals(2, dto.getScoreD());
        assertEquals(100, dto.getPoints());
        assertEquals(1, dto.getMisses());
        assertEquals(0, dto.getPenalties());
        assertEquals(0, dto.getProcedurals());
        assertFalse(dto.getHasDeduction());
        assertEquals(new BigDecimal("0.0"), dto.getDeductionPercentage());
        assertFalse(dto.getIsDisqualified());
        assertEquals(new BigDecimal("10.5"), dto.getTime());
        assertEquals(new BigDecimal("5.2"), dto.getHitFactor());
        assertEquals(new BigDecimal("100"), dto.getStagePoints());
        assertNotNull(dto.getStagePercentage());
        assertEquals(matchStageDto, dto.getMatchStage());
        assertEquals(PowerFactor.MINOR, dto.getPowerFactor());
    }

    @Test
    void testToString_ShouldReturnFormattedString() {
        // Arrange
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        MatchStageDto matchStageDto = mock(MatchStageDto.class);
        CompetitorDto competitorDto = mock(CompetitorDto.class);

        when(matchStageDto.toString()).thenReturn("Stage 1");
        when(competitorDto.toString()).thenReturn("John Doe");

        dto.setMatchStage(matchStageDto);
        dto.setCompetitor(competitorDto);

        // Act
        String result = dto.toString();

        // Assert
        assertEquals("Stage 1: John Doe", result);
    }
}
