package za.co.hpsc.web.models.ipsc.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.models.ipsc.dto.CompetitorDto;
import za.co.hpsc.web.models.ipsc.dto.MatchStageDto;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DtoToEntityMappingIntegrationTest {
    private DtoMapping dtoMapping;
    private DtoToEntityMapping dtoToEntityMapping;

    @BeforeEach
    public void setUp() {
        dtoMapping = new DtoMapping();
        dtoToEntityMapping = new DtoToEntityMapping(dtoMapping);
    }

    @Test
    public void testCompleteFlow_withAllMappings_thenAllDataStored() {
        // Arrange
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Complete Test Match");
        dtoToEntityMapping.setMatch(matchEntity);

        HashMap<UUID, CompetitorDto> competitorMap = new HashMap<>();
        CompetitorDto competitorDto = new CompetitorDto();
        UUID competitorUuid = UUID.randomUUID();
        competitorDto.setUuid(competitorUuid);
        competitorMap.put(competitorUuid, competitorDto);
        dtoMapping.setCompetitorMap(competitorMap);

        HashMap<UUID, MatchStageDto> stageMap = new HashMap<>();
        MatchStageDto matchStageDto = new MatchStageDto();
        UUID stageUuid = UUID.randomUUID();
        matchStageDto.setUuid(stageUuid);
        stageMap.put(stageUuid, matchStageDto);
        dtoMapping.setMatchStageMap(stageMap);

        // Act & Assert
        assertTrue(dtoToEntityMapping.getMatchEntity().isPresent());
        assertEquals(1, dtoToEntityMapping.getCompetitorDtoList().size());
        assertEquals(1, dtoToEntityMapping.getMatchStageDtoList().size());
    }

    @Test
    public void testMultipleCompetitors_withPartialData_thenStoresAll() {
        // Arrange
        HashMap<UUID, CompetitorDto> competitorMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            CompetitorDto competitorDto = new CompetitorDto();
            competitorDto.setUuid(UUID.randomUUID());
            competitorDto.setFirstName("Competitor" + i);
            competitorMap.put(competitorDto.getUuid(), competitorDto);
        }
        dtoMapping.setCompetitorMap(competitorMap);

        // Act
        List<CompetitorDto> result = dtoToEntityMapping.getCompetitorDtoList();

        // Assert
        assertEquals(5, result.size());
    }
}
