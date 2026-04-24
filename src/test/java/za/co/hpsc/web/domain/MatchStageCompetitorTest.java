package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.dto.MatchStageCompetitorDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MatchStageCompetitorTest {

    // =====================================================================
    // init(MatchStageCompetitorDto)
    // =====================================================================

    @Test
    void testInit_whenDtoIsFullyPopulated_thenMapsAllFields() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.SENIOR);
        dto.setFirearmType(FirearmType.HANDGUN);
        dto.setDivision(Division.PRODUCTION);
        dto.setPowerFactor(PowerFactor.MINOR);
        dto.setScoreA(20);
        dto.setScoreB(15);
        dto.setScoreC(10);
        dto.setScoreD(5);
        dto.setPoints(175);
        dto.setMisses(2);
        dto.setPenalties(1);
        dto.setProcedurals(0);
        dto.setHasDeduction(false);
        dto.setDeductionPercentage(BigDecimal.ZERO);
        dto.setIsDisqualified(false);
        dto.setTime(new BigDecimal("38.25"));
        dto.setHitFactor(new BigDecimal("4.58"));
        dto.setStagePoints(new BigDecimal("175.0000"));
        dto.setStagePercentage(new BigDecimal("95.50"));
        dto.setStageRanking(BigDecimal.ONE);

        // Act
        entity.init(dto);

        // Assert
        assertEquals(CompetitorCategory.SENIOR, entity.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, entity.getFirearmType());
        assertEquals(Division.PRODUCTION, entity.getDivision());
        assertEquals(PowerFactor.MINOR, entity.getPowerFactor());
        assertEquals(20, entity.getScoreA());
        assertEquals(15, entity.getScoreB());
        assertEquals(10, entity.getScoreC());
        assertEquals(5, entity.getScoreD());
        assertEquals(175, entity.getPoints());
        assertEquals(2, entity.getMisses());
        assertEquals(1, entity.getPenalties());
        assertEquals(0, entity.getProcedurals());
        assertEquals(false, entity.getHasDeduction());
        assertEquals(BigDecimal.ZERO, entity.getDeductionPercentage());
        assertEquals(false, entity.getIsDisqualified());
        assertEquals(new BigDecimal("38.25"), entity.getTime());
        assertEquals(new BigDecimal("4.58"), entity.getHitFactor());
        assertEquals(new BigDecimal("175.0000"), entity.getStagePoints());
        assertEquals(new BigDecimal("95.50"), entity.getStagePercentage());
        assertEquals(BigDecimal.ONE, entity.getStageRanking());
    }

    @Test
    void testInit_whenAllScoresAreNull_thenScoresAreNull() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setScoreA(null);
        dto.setScoreB(null);
        dto.setScoreC(null);
        dto.setScoreD(null);

        // Act
        entity.init(dto);

        // Assert
        assertNull(entity.getScoreA());
        assertNull(entity.getScoreB());
        assertNull(entity.getScoreC());
        assertNull(entity.getScoreD());
    }

    @Test
    void testInit_whenPerformanceMetricsAreNull_thenMetricsAreNull() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setPoints(null);
        dto.setMisses(null);
        dto.setPenalties(null);
        dto.setProcedurals(null);

        // Act
        entity.init(dto);

        // Assert
        assertNull(entity.getPoints());
        assertNull(entity.getMisses());
        assertNull(entity.getPenalties());
        assertNull(entity.getProcedurals());
    }

    @Test
    void testInit_whenTimeAndHitFactorAreNull_thenTimeAndHitFactorAreNull() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setTime(null);
        dto.setHitFactor(null);

        // Act
        entity.init(dto);

        // Assert
        assertNull(entity.getTime());
        assertNull(entity.getHitFactor());
    }

    @Test
    void testInit_whenStageRankingFieldsAreNull_thenRankingFieldsAreNull() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setStagePoints(null);
        dto.setStagePercentage(null);
        dto.setStageRanking(null);

        // Act
        entity.init(dto);

        // Assert
        assertNull(entity.getStagePoints());
        assertNull(entity.getStagePercentage());
        assertNull(entity.getStageRanking());
    }

    @Test
    void testInit_whenDeductionFieldsAreNull_thenDeductionFieldsAreNull() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setHasDeduction(null);
        dto.setDeductionPercentage(null);

        // Act
        entity.init(dto);

        // Assert
        assertNull(entity.getHasDeduction());
        assertNull(entity.getDeductionPercentage());
    }

    @Test
    void testInit_whenIsDisqualifiedIsNull_thenIsDisqualifiedIsNull() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setIsDisqualified(null);

        // Act
        entity.init(dto);

        // Assert
        assertNull(entity.getIsDisqualified());
    }

    @Test
    void testInit_whenCompetitorCategoryIsNone_thenCompetitorCategoryIsNone() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);

        // Act
        entity.init(dto);

        // Assert
        assertEquals(CompetitorCategory.NONE, entity.getCompetitorCategory());
    }

    @Test
    void testInit_whenFirearmTypeIsNull_thenFirearmTypeIsNull() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.JUNIOR);
        dto.setFirearmType(null);

        // Act
        entity.init(dto);

        // Assert
        assertNull(entity.getFirearmType());
    }

    @Test
    void testInit_whenDivisionIsNull_thenDivisionIsNull() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setDivision(null);

        // Act
        entity.init(dto);

        // Assert
        assertNull(entity.getDivision());
    }

    @Test
    void testInit_whenPowerFactorIsNull_thenPowerFactorIsNull() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setPowerFactor(null);

        // Act
        entity.init(dto);

        // Assert
        assertNull(entity.getPowerFactor());
    }

    @Test
    void testInit_whenAllScoresAreZero_thenScoresAreZero() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setScoreA(0);
        dto.setScoreB(0);
        dto.setScoreC(0);
        dto.setScoreD(0);
        dto.setPoints(0);
        dto.setMisses(0);
        dto.setPenalties(0);
        dto.setProcedurals(0);
        dto.setTime(BigDecimal.ZERO);
        dto.setHitFactor(BigDecimal.ZERO);
        dto.setStagePoints(BigDecimal.ZERO);
        dto.setStagePercentage(BigDecimal.ZERO);
        dto.setStageRanking(BigDecimal.ZERO);
        dto.setDeductionPercentage(BigDecimal.ZERO);

        // Act
        entity.init(dto);

        // Assert
        assertEquals(0, entity.getScoreA());
        assertEquals(0, entity.getScoreB());
        assertEquals(0, entity.getScoreC());
        assertEquals(0, entity.getScoreD());
        assertEquals(0, entity.getPoints());
        assertEquals(0, entity.getMisses());
        assertEquals(0, entity.getPenalties());
        assertEquals(0, entity.getProcedurals());
        assertEquals(BigDecimal.ZERO, entity.getTime());
        assertEquals(BigDecimal.ZERO, entity.getHitFactor());
        assertEquals(BigDecimal.ZERO, entity.getStagePoints());
        assertEquals(BigDecimal.ZERO, entity.getStagePercentage());
        assertEquals(BigDecimal.ZERO, entity.getStageRanking());
        assertEquals(BigDecimal.ZERO, entity.getDeductionPercentage());
    }

    @Test
    void testInit_whenIsDisqualifiedIsTrue_thenIsDisqualifiedIsTrue() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setIsDisqualified(true);

        // Act
        entity.init(dto);

        // Assert
        assertEquals(true, entity.getIsDisqualified());
    }

    @Test
    void testInit_whenHasDeductionIsTrue_thenHasDeductionIsTrue() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setHasDeduction(true);
        dto.setDeductionPercentage(new BigDecimal("10.00"));

        // Act
        entity.init(dto);

        // Assert
        assertEquals(true, entity.getHasDeduction());
        assertEquals(new BigDecimal("10.00"), entity.getDeductionPercentage());
    }

    @Test
    void testInit_whenCalledTwice_thenLastDtoValuesOverwrite() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();

        MatchStageCompetitorDto first = new MatchStageCompetitorDto();
        first.setCompetitorCategory(CompetitorCategory.JUNIOR);
        first.setFirearmType(FirearmType.HANDGUN);
        first.setDivision(Division.PRODUCTION);
        first.setPowerFactor(PowerFactor.MINOR);
        first.setScoreA(10);
        first.setPoints(100);
        first.setTime(new BigDecimal("45.00"));
        first.setHitFactor(new BigDecimal("2.22"));
        first.setStagePoints(new BigDecimal("100.0000"));
        first.setIsDisqualified(false);

        MatchStageCompetitorDto second = new MatchStageCompetitorDto();
        second.setCompetitorCategory(CompetitorCategory.SENIOR);
        second.setFirearmType(FirearmType.PCC);
        second.setDivision(Division.PCC_OPTICS);
        second.setPowerFactor(PowerFactor.MAJOR);
        second.setScoreA(20);
        second.setPoints(200);
        second.setTime(new BigDecimal("30.00"));
        second.setHitFactor(new BigDecimal("6.67"));
        second.setStagePoints(new BigDecimal("200.0000"));
        second.setIsDisqualified(true);

        // Act
        entity.init(first);
        entity.init(second);

        // Assert
        assertEquals(CompetitorCategory.SENIOR, entity.getCompetitorCategory());
        assertEquals(FirearmType.PCC, entity.getFirearmType());
        assertEquals(Division.PCC_OPTICS, entity.getDivision());
        assertEquals(PowerFactor.MAJOR, entity.getPowerFactor());
        assertEquals(20, entity.getScoreA());
        assertEquals(200, entity.getPoints());
        assertEquals(new BigDecimal("30.00"), entity.getTime());
        assertEquals(new BigDecimal("6.67"), entity.getHitFactor());
        assertEquals(new BigDecimal("200.0000"), entity.getStagePoints());
        assertEquals(true, entity.getIsDisqualified());
    }

    @Test
    void testInit_whenAllCompetitorCategoriesAreProvided_thenEachCategoryIsSetCorrectly() {
        // Arrange & Act & Assert
        for (CompetitorCategory category : CompetitorCategory.values()) {
            MatchStageCompetitor entity = new MatchStageCompetitor();
            MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
            dto.setCompetitorCategory(category);

            entity.init(dto);

            assertEquals(category, entity.getCompetitorCategory());
        }
    }

    @Test
    void testInit_whenDtoIsApplied_thenDoesNotAffectCompetitorOrMatchStage() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        Competitor competitor = new Competitor();
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        entity.setCompetitor(competitor);

        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageNumber(3);
        matchStage.setStageName("Speed Stage");
        entity.setMatchStage(matchStage);

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.SENIOR);
        dto.setFirearmType(FirearmType.HANDGUN);
        dto.setDivision(Division.OPEN);
        dto.setPowerFactor(PowerFactor.MAJOR);

        // Act
        entity.init(dto);

        // Assert
        assertSame(competitor, entity.getCompetitor());
        assertSame(matchStage, entity.getMatchStage());
        assertEquals("John", entity.getCompetitor().getFirstName());
        assertEquals(3, entity.getMatchStage().getStageNumber());
    }

    // =====================================================================
    // toString()
    // =====================================================================

    @Test
    void testToString_whenMatchStageAndCompetitorAreSet_thenFormatsStageAndCompetitorWithColon() {
        // Arrange
        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageName("El Presidente");
        matchStage.setStageNumber(3);

        Competitor competitor = new Competitor();
        competitor.setFirstName("John");
        competitor.setLastName("Doe");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);

        // Act
        String result = entity.toString();

        // Assert
        assertEquals("El Presidente (3): John Doe", result);
    }

    @Test
    void testToString_whenMatchStageIsNull_thenUsesUnknownForStage() {
        // Arrange
        Competitor competitor = new Competitor();
        competitor.setFirstName("Jane");
        competitor.setLastName("Smith");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(null);
        entity.setCompetitor(competitor);

        // Act
        String result = entity.toString();

        // Assert
        assertEquals("Unknown: Jane Smith", result);
    }

    @Test
    void testToString_whenCompetitorIsNull_thenUsesUnknownForCompetitor() {
        // Arrange
        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageName("Stage One");
        matchStage.setStageNumber(1);

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(matchStage);
        entity.setCompetitor(null);

        // Act
        String result = entity.toString();

        // Assert
        assertEquals("Stage One (1): Unknown", result);
    }

    @Test
    void testToString_whenBothMatchStageAndCompetitorAreNull_thenReturnsUnknownForBoth() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(null);
        entity.setCompetitor(null);

        // Act
        String result = entity.toString();

        // Assert
        assertEquals("Unknown: Unknown", result);
    }

    @Test
    void testToString_whenCompetitorHasMiddleName_thenIncludesMiddleNameInOutput() {
        // Arrange
        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageName("Finals");
        matchStage.setStageNumber(5);

        Competitor competitor = new Competitor();
        competitor.setFirstName("Alice");
        competitor.setMiddleNames("Grace");
        competitor.setLastName("Cooper");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);

        // Act
        String result = entity.toString();

        // Assert
        assertTrue(result.contains("Alice Grace Cooper"));
    }

    @Test
    void testToString_whenCalledMultipleTimes_thenReturnsConsistentResult() {
        // Arrange
        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageName("Consistent Stage");
        matchStage.setStageNumber(2);

        Competitor competitor = new Competitor();
        competitor.setFirstName("Bob");
        competitor.setLastName("Brown");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);

        // Act
        String result1 = entity.toString();
        String result2 = entity.toString();
        String result3 = entity.toString();

        // Assert
        assertEquals(result1, result2);
        assertEquals(result2, result3);
    }

    @Test
    void testToString_whenCalledAfterInit_thenMatchStageAndCompetitorAreUnaffected() {
        // Arrange
        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageName("Speed Stage");
        matchStage.setStageNumber(4);

        Competitor competitor = new Competitor();
        competitor.setFirstName("Tom");
        competitor.setLastName("Hardy");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);

        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.SUPER_SENIOR);
        dto.setFirearmType(FirearmType.RIFLE);
        dto.setDivision(Division.RIFLE_SEMI_AUTO_OPEN);
        dto.setPowerFactor(PowerFactor.MAJOR);
        dto.setPoints(180);
        entity.init(dto);

        // Act
        String result = entity.toString();

        // Assert
        assertTrue(result.contains("Speed Stage"));
        assertTrue(result.contains("4"));
        assertTrue(result.contains("Tom Hardy"));
    }

    @Test
    void testToString_whenCalled_thenContainsStageInfoAndCompetitorName() {
        // Arrange
        IpscMatchStage matchStage = new IpscMatchStage();
        matchStage.setStageName("HPSC Stage 3");
        matchStage.setStageNumber(3);

        Competitor competitor = new Competitor();
        competitor.setFirstName("Sarah");
        competitor.setLastName("Connor");

        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.setMatchStage(matchStage);
        entity.setCompetitor(competitor);

        // Act
        String result = entity.toString();

        // Assert
        assertTrue(result.contains("HPSC Stage 3"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("Sarah Connor"));
        assertTrue(result.contains(":"));
    }

    @Test
    void testInit_whenDtoIsNull_thenThrowsNullPointerException() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> entity.init(null));
    }

    @Test
    void testInit_whenCompetitorCategoryIsNull_thenCompetitorCategoryIsSetToNull() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        MatchStageCompetitorDto dto = new MatchStageCompetitorDto();
        dto.setCompetitorCategory(null);
        dto.setFirearmType(FirearmType.HANDGUN);
        dto.setDivision(Division.OPEN);
        dto.setPowerFactor(PowerFactor.MAJOR);

        // Act
        entity.init(dto);

        // Assert
        assertNull(entity.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, entity.getFirearmType());
        assertEquals(Division.OPEN, entity.getDivision());
        assertEquals(PowerFactor.MAJOR, entity.getPowerFactor());
    }

    @Test
    void testOnInsert_whenInvoked_thenInitializesDateCreatedAndDateUpdatedWithSameValue() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        LocalDateTime before = LocalDateTime.now();

        // Act
        entity.onInsert();
        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getDateUpdated());
        assertEquals(entity.getDateCreated(), entity.getDateUpdated());
        assertFalse(entity.getDateCreated().isBefore(before));
        assertFalse(entity.getDateCreated().isAfter(after));
    }

    @Test
    void testOnUpdate_whenInvoked_thenUpdatesDateUpdatedWithoutChangingDateCreated() {
        // Arrange
        MatchStageCompetitor entity = new MatchStageCompetitor();
        entity.onInsert();
        LocalDateTime created = entity.getDateCreated();
        LocalDateTime updatedBefore = entity.getDateUpdated();

        // Act
        entity.onUpdate();

        // Assert
        assertEquals(created, entity.getDateCreated());
        assertNotNull(entity.getDateUpdated());
        assertFalse(entity.getDateUpdated().isBefore(updatedBefore));
    }
}

