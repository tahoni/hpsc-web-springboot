package za.co.hpsc.web.models.ipsc.dto;

import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.domain.MatchStageCompetitor;
import za.co.hpsc.web.enums.*;
import za.co.hpsc.web.models.ipsc.common.dto.CompetitorDto;
import za.co.hpsc.web.models.ipsc.common.dto.EnrolledCompetitorDto;
import za.co.hpsc.web.models.ipsc.common.dto.MatchCompetitorDto;
import za.co.hpsc.web.models.ipsc.common.dto.MatchStageCompetitorDto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnrolledCompetitorDtoTest {

    @Test
    void testDefaultConstructor_whenInvoked_thenInitializesDefaults() {
        // Act
        EnrolledCompetitorDto dto = new EnrolledCompetitorDto();

        // Assert
        assertNotNull(dto.getUuid(), "uuid should be generated");
        assertNull(dto.getCompetitorIndex(), "competitorIndex should default to null");
        assertNull(dto.getCompetitor(), "competitor should default to null");
        assertNotNull(dto.getCompetitorMatches(), "competitorMatches list should be initialized");
        assertTrue(dto.getCompetitorMatches().isEmpty(), "competitorMatches should start empty");
        assertNotNull(dto.getCompetitorMatchStages(), "competitorMatchStages list should be initialized");
        assertTrue(dto.getCompetitorMatchStages().isEmpty(), "competitorMatchStages should start empty");
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory(), "default category should be NONE");
        assertNull(dto.getClub());
        assertNull(dto.getFirearmType());
        assertNull(dto.getDivision());
        assertNull(dto.getPowerFactor());
    }

    @Test
    void testAllArgsConstructor_whenValuesProvided_thenAssignsProvidedValues() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Integer index = 7;
        CompetitorDto competitor = mock(CompetitorDto.class);

        MatchCompetitorDto matchDto = mock(MatchCompetitorDto.class);
        MatchStageCompetitorDto stageDto = mock(MatchStageCompetitorDto.class);

        // Act
        EnrolledCompetitorDto dto = new EnrolledCompetitorDto(
                uuid,
                index,
                competitor,
                java.util.List.of(matchDto),
                java.util.List.of(stageDto),
                CompetitorCategory.SENIOR,
                ClubIdentifier.UNKNOWN,
                FirearmType.HANDGUN,
                Division.OPEN,
                PowerFactor.MAJOR
        );

        // Assert
        assertEquals(uuid, dto.getUuid());
        assertEquals(index, dto.getCompetitorIndex());
        assertEquals(competitor, dto.getCompetitor());
        assertEquals(1, dto.getCompetitorMatches().size());
        assertEquals(1, dto.getCompetitorMatchStages().size());
        assertEquals(CompetitorCategory.SENIOR, dto.getCompetitorCategory());
        assertEquals(ClubIdentifier.UNKNOWN, dto.getClub());
        assertEquals(FirearmType.HANDGUN, dto.getFirearmType());
        assertEquals(Division.OPEN, dto.getDivision());
        assertEquals(PowerFactor.MAJOR, dto.getPowerFactor());
    }

    @Test
    void testConstructorFromMatchCompetitor_whenEntityProvided_thenMapsFields() {
        // Arrange
        MatchCompetitor entity = mock(MatchCompetitor.class);
        za.co.hpsc.web.domain.Competitor domainCompetitor = mock(za.co.hpsc.web.domain.Competitor.class);

        when(entity.getCompetitor()).thenReturn(domainCompetitor);
        when(entity.getMatchClub()).thenReturn(ClubIdentifier.UNKNOWN);
        when(entity.getCompetitorCategory()).thenReturn(CompetitorCategory.SENIOR);
        when(entity.getFirearmType()).thenReturn(FirearmType.HANDGUN);
        when(entity.getDivision()).thenReturn(Division.OPEN);
        when(entity.getPowerFactor()).thenReturn(PowerFactor.MAJOR);

        // Act
        EnrolledCompetitorDto dto = new EnrolledCompetitorDto(entity);

        // Assert
        assertNotNull(dto.getCompetitor(), "competitor dto should be mapped");
        assertEquals(ClubIdentifier.UNKNOWN, dto.getClub());
        assertEquals(CompetitorCategory.SENIOR, dto.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, dto.getFirearmType());
        assertEquals(Division.OPEN, dto.getDivision());
        assertEquals(PowerFactor.MAJOR, dto.getPowerFactor());
        assertNotNull(dto.getCompetitorMatches(), "default list should remain initialized");
        assertNotNull(dto.getCompetitorMatchStages(), "default list should remain initialized");
    }

    @Test
    void testConstructorFromMatchCompetitor_whenEntityIsNull_thenKeepsDefaults() {
        // Act
        EnrolledCompetitorDto dto = new EnrolledCompetitorDto((MatchCompetitor) null);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getCompetitor());
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
        assertNull(dto.getClub());
        assertNull(dto.getFirearmType());
        assertNull(dto.getDivision());
        assertNull(dto.getPowerFactor());
        assertNotNull(dto.getCompetitorMatches());
        assertNotNull(dto.getCompetitorMatchStages());
    }

    @Test
    void testConstructorFromMatchStageCompetitor_whenEntityProvided_thenMapsFields() {
        // Arrange
        MatchStageCompetitor entity = mock(MatchStageCompetitor.class);
        za.co.hpsc.web.domain.Competitor domainCompetitor = mock(za.co.hpsc.web.domain.Competitor.class);

        when(entity.getCompetitor()).thenReturn(domainCompetitor);
        when(entity.getMatchClub()).thenReturn(ClubIdentifier.UNKNOWN);
        when(entity.getCompetitorCategory()).thenReturn(CompetitorCategory.SENIOR);
        when(entity.getFirearmType()).thenReturn(FirearmType.HANDGUN);
        when(entity.getDivision()).thenReturn(Division.OPEN);
        when(entity.getPowerFactor()).thenReturn(PowerFactor.MAJOR);

        // Act
        EnrolledCompetitorDto dto = new EnrolledCompetitorDto(entity);

        // Assert
        assertNotNull(dto.getCompetitor(), "competitor dto should be mapped");
        assertEquals(ClubIdentifier.UNKNOWN, dto.getClub());
        assertEquals(CompetitorCategory.SENIOR, dto.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, dto.getFirearmType());
        assertEquals(Division.OPEN, dto.getDivision());
        assertEquals(PowerFactor.MAJOR, dto.getPowerFactor());
    }

    @Test
    void testConstructorFromMatchStageCompetitor_whenEntityIsNull_thenKeepsDefaults() {
        // Act
        EnrolledCompetitorDto dto = new EnrolledCompetitorDto((MatchStageCompetitor) null);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getCompetitor());
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
        assertNull(dto.getClub());
        assertNull(dto.getFirearmType());
        assertNull(dto.getDivision());
        assertNull(dto.getPowerFactor());
    }

    @Test
    void testConstructorFromMatchCompetitorDto_whenDtoProvided_thenMapsFields() {
        // Arrange
        MatchCompetitorDto matchCompetitorDto = mock(MatchCompetitorDto.class);
        CompetitorDto competitorDto = mock(CompetitorDto.class);

        when(matchCompetitorDto.getCompetitor()).thenReturn(competitorDto);
        when(matchCompetitorDto.getClub()).thenReturn(ClubIdentifier.UNKNOWN);
        when(matchCompetitorDto.getCompetitorCategory()).thenReturn(CompetitorCategory.SENIOR);
        when(matchCompetitorDto.getFirearmType()).thenReturn(FirearmType.HANDGUN);
        when(matchCompetitorDto.getDivision()).thenReturn(Division.OPEN);
        when(matchCompetitorDto.getPowerFactor()).thenReturn(PowerFactor.MAJOR);

        // Act
        EnrolledCompetitorDto dto = new EnrolledCompetitorDto(matchCompetitorDto);

        // Assert
        assertEquals(competitorDto, dto.getCompetitor());
        assertEquals(ClubIdentifier.UNKNOWN, dto.getClub());
        assertEquals(CompetitorCategory.SENIOR, dto.getCompetitorCategory());
        assertEquals(FirearmType.HANDGUN, dto.getFirearmType());
        assertEquals(Division.OPEN, dto.getDivision());
        assertEquals(PowerFactor.MAJOR, dto.getPowerFactor());
    }

    @Test
    void testConstructorFromMatchCompetitorDto_whenDtoIsNull_thenKeepsDefaults() {
        // Act
        EnrolledCompetitorDto dto = new EnrolledCompetitorDto((MatchCompetitorDto) null);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getCompetitor());
        assertEquals(CompetitorCategory.NONE, dto.getCompetitorCategory());
        assertNull(dto.getClub());
        assertNull(dto.getFirearmType());
        assertNull(dto.getDivision());
        assertNull(dto.getPowerFactor());
    }
}
