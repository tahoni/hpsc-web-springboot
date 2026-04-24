package za.co.hpsc.web.domain;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.enums.CompetitorCategory;
import za.co.hpsc.web.enums.Division;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.PowerFactor;
import za.co.hpsc.web.models.ipsc.dto.MatchCompetitorDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MatchCompetitorTest {

    private IpscMatch buildMatch(String name, LocalDateTime scheduledDate) {
        IpscMatch match = new IpscMatch();
        match.setName(name);
        match.setScheduledDate(scheduledDate);
        return match;
    }

    private Competitor buildCompetitor(String firstName, String lastName) {
        Competitor competitor = new Competitor();
        competitor.setFirstName(firstName);
        competitor.setLastName(lastName);
        return competitor;
    }

    // =====================================================================
    // init(MatchCompetitorDto)
    // =====================================================================

    @Test
    void testInit_whenDtoIsFullyPopulated_thenMapsAllFields() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        MatchCompetitorDto dto = new MatchCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.SENIOR);
        dto.setFirearmType(FirearmType.HANDGUN);
        dto.setDivision(Division.PRODUCTION);
        dto.setPowerFactor(PowerFactor.MINOR);
        dto.setMatchPoints(new BigDecimal("245.6700"));
        dto.setMatchRanking(new BigDecimal("98.25"));

        // Act
        matchCompetitor.init(dto);

        // Assert
        assertEquals(CompetitorCategory.SENIOR, matchCompetitor.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, matchCompetitor.getFirearmType());
        assertEquals(Division.PRODUCTION, matchCompetitor.getDivision());
        assertEquals(PowerFactor.MINOR, matchCompetitor.getPowerFactor());
        assertEquals(new BigDecimal("245.6700"), matchCompetitor.getMatchPoints());
        assertEquals(new BigDecimal("98.25"), matchCompetitor.getMatchRanking());
    }

    @Test
    void testInit_whenCompetitorCategoryIsNone_thenCompetitorCategoryIsNone() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        MatchCompetitorDto dto = new MatchCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setFirearmType(FirearmType.HANDGUN);
        dto.setDivision(Division.OPEN);
        dto.setPowerFactor(PowerFactor.MAJOR);

        // Act
        matchCompetitor.init(dto);

        // Assert
        assertEquals(CompetitorCategory.NONE, matchCompetitor.getCompetitorCategory());
    }

    @Test
    void testInit_whenFirearmTypeIsNull_thenFirearmTypeIsNull() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        MatchCompetitorDto dto = new MatchCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.JUNIOR);
        dto.setFirearmType(null);
        dto.setDivision(Division.STANDARD);
        dto.setPowerFactor(PowerFactor.MINOR);

        // Act
        matchCompetitor.init(dto);

        // Assert
        assertNull(matchCompetitor.getFirearmType());
    }

    @Test
    void testInit_whenDivisionIsNull_thenDivisionIsNull() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        MatchCompetitorDto dto = new MatchCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.LADY);
        dto.setFirearmType(FirearmType.HANDGUN);
        dto.setDivision(null);
        dto.setPowerFactor(PowerFactor.MINOR);

        // Act
        matchCompetitor.init(dto);

        // Assert
        assertNull(matchCompetitor.getDivision());
    }

    @Test
    void testInit_whenPowerFactorIsNull_thenPowerFactorIsNull() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        MatchCompetitorDto dto = new MatchCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setFirearmType(FirearmType.RIFLE);
        dto.setDivision(Division.RIFLE_SEMI_AUTO_OPEN);
        dto.setPowerFactor(null);

        // Act
        matchCompetitor.init(dto);

        // Assert
        assertNull(matchCompetitor.getPowerFactor());
    }

    @Test
    void testInit_whenMatchPointsAreNull_thenMatchPointsAreNull() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        MatchCompetitorDto dto = new MatchCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setMatchPoints(null);
        dto.setMatchRanking(null);

        // Act
        matchCompetitor.init(dto);

        // Assert
        assertNull(matchCompetitor.getMatchPoints());
        assertNull(matchCompetitor.getMatchRanking());
    }

    @Test
    void testInit_whenMatchPointsAreZero_thenMatchPointsAreZero() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        MatchCompetitorDto dto = new MatchCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.NONE);
        dto.setMatchPoints(BigDecimal.ZERO);
        dto.setMatchRanking(BigDecimal.ZERO);

        // Act
        matchCompetitor.init(dto);

        // Assert
        assertEquals(BigDecimal.ZERO, matchCompetitor.getMatchPoints());
        assertEquals(BigDecimal.ZERO, matchCompetitor.getMatchRanking());
    }

    @Test
    void testInit_whenCalledTwice_thenLastDtoValuesOverwrite() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();

        MatchCompetitorDto first = new MatchCompetitorDto();
        first.setCompetitorCategory(CompetitorCategory.JUNIOR);
        first.setFirearmType(FirearmType.HANDGUN);
        first.setDivision(Division.PRODUCTION);
        first.setPowerFactor(PowerFactor.MINOR);
        first.setMatchPoints(new BigDecimal("100.00"));
        first.setMatchRanking(new BigDecimal("50.00"));

        MatchCompetitorDto second = new MatchCompetitorDto();
        second.setCompetitorCategory(CompetitorCategory.SENIOR);
        second.setFirearmType(FirearmType.PCC);
        second.setDivision(Division.PCC_OPTICS);
        second.setPowerFactor(PowerFactor.MAJOR);
        second.setMatchPoints(new BigDecimal("300.00"));
        second.setMatchRanking(new BigDecimal("95.50"));

        // Act
        matchCompetitor.init(first);
        matchCompetitor.init(second);

        // Assert
        assertEquals(CompetitorCategory.SENIOR, matchCompetitor.getCompetitorCategory());
        assertEquals(FirearmType.PCC, matchCompetitor.getFirearmType());
        assertEquals(Division.PCC_OPTICS, matchCompetitor.getDivision());
        assertEquals(PowerFactor.MAJOR, matchCompetitor.getPowerFactor());
        assertEquals(new BigDecimal("300.00"), matchCompetitor.getMatchPoints());
        assertEquals(new BigDecimal("95.50"), matchCompetitor.getMatchRanking());
    }

    // =====================================================================
    // toString()
    // =====================================================================

    @Test
    void testToString_whenMatchAndCompetitorAreSet_thenFormatsMatchAndCompetitorWithColon() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("Spring Classic", LocalDateTime.of(2026, 4, 24, 10, 30)));
        matchCompetitor.setCompetitor(buildCompetitor("John", "Doe"));

        // Act
        String result = matchCompetitor.toString();

        // Assert
        assertEquals("Spring Classic (2026-04-24 10:30): John Doe", result);
    }

    @Test
    void testToString_whenCompetitorHasMiddleName_thenIncludesMiddleNameInOutput() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("Club Match", LocalDateTime.of(2026, 6, 1, 9, 0)));
        Competitor competitor = buildCompetitor("Jane", "Smith");
        competitor.setMiddleNames("Anne");
        matchCompetitor.setCompetitor(competitor);

        // Act
        String result = matchCompetitor.toString();

        // Assert
        assertTrue(result.contains("Jane Anne Smith"));
    }

    @Test
    void testToString_whenCalledMultipleTimes_thenReturnsConsistentResult() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("Consistent Match", LocalDateTime.of(2026, 4, 24, 10, 30)));
        matchCompetitor.setCompetitor(buildCompetitor("Alice", "Cooper"));

        // Act
        String result1 = matchCompetitor.toString();
        String result2 = matchCompetitor.toString();

        // Assert
        assertEquals(result1, result2);
    }

    @Test
    void testToString_containsMatchNameInOutput() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("HPSC League Round 1", LocalDateTime.of(2026, 5, 15, 8, 0)));
        matchCompetitor.setCompetitor(buildCompetitor("Bob", "Brown"));

        // Act
        String result = matchCompetitor.toString();

        // Assert
        assertTrue(result.contains("HPSC League Round 1"));
    }

    @Test
    void testToString_containsCompetitorNameInOutput() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("Club Shoot", LocalDateTime.of(2026, 5, 15, 8, 0)));
        matchCompetitor.setCompetitor(buildCompetitor("Bob", "Brown"));

        // Act
        String result = matchCompetitor.toString();

        // Assert
        assertTrue(result.contains("Bob Brown"));
    }

    @Test
    void testToString_afterInit_thenMatchAndCompetitorAreUnaffected() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("Winter Shoot", LocalDateTime.of(2026, 7, 10, 9, 0)));
        matchCompetitor.setCompetitor(buildCompetitor("Tom", "Hardy"));

        MatchCompetitorDto dto = new MatchCompetitorDto();
        dto.setCompetitorCategory(CompetitorCategory.SENIOR);
        dto.setFirearmType(FirearmType.HANDGUN);
        dto.setDivision(Division.OPEN);
        dto.setPowerFactor(PowerFactor.MAJOR);
        matchCompetitor.init(dto);

        // Act
        String result = matchCompetitor.toString();

        // Assert
        assertTrue(result.contains("Winter Shoot"));
        assertTrue(result.contains("Tom Hardy"));
    }

    @Test
    void init_whenDtoIsNull_thenThrowsNullPointerException() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> matchCompetitor.init(null));
    }

    @Test
    void toString_whenMatchIsNull_thenThrowsNullPointerException() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(null);
        matchCompetitor.setCompetitor(buildCompetitor("John", "Doe"));

        // Act & Assert
        assertThrows(NullPointerException.class, matchCompetitor::toString);
    }

    @Test
    void toString_whenCompetitorIsNull_thenThrowsNullPointerException() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setMatch(buildMatch("Spring Classic", LocalDateTime.of(2026, 4, 24, 10, 30)));
        matchCompetitor.setCompetitor(null);

        // Act & Assert
        assertThrows(NullPointerException.class, matchCompetitor::toString);
    }

    @Test
    void onInsert_whenInvoked_thenSetsDateCreatedAndDateUpdatedToSameValue() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();

        // Act
        matchCompetitor.onInsert();

        // Assert
        assertNotNull(matchCompetitor.getDateCreated());
        assertNotNull(matchCompetitor.getDateUpdated());
        assertEquals(matchCompetitor.getDateCreated(), matchCompetitor.getDateUpdated());
    }

    @Test
    void onUpdate_whenInvoked_thenRefreshesDateUpdatedAndPreservesDateCreated() {
        // Arrange
        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.onInsert();
        LocalDateTime createdBeforeUpdate = matchCompetitor.getDateCreated();
        LocalDateTime updatedBeforeUpdate = matchCompetitor.getDateUpdated();

        // Act
        matchCompetitor.onUpdate();

        // Assert
        assertEquals(createdBeforeUpdate, matchCompetitor.getDateCreated());
        assertNotNull(matchCompetitor.getDateUpdated());
        assertFalse(matchCompetitor.getDateUpdated().isBefore(updatedBeforeUpdate));
    }
}

