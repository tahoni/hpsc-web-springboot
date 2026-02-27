package za.co.hpsc.web.models.ipsc.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.models.ipsc.dto.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DtoToEntityMappingTest {

    private DtoMapping dtoMapping;
    private DtoToEntityMapping dtoToEntityMapping;

    @BeforeEach
    public void setUp() {
        dtoMapping = new DtoMapping();
        dtoToEntityMapping = new DtoToEntityMapping(dtoMapping);
    }

    // =====================================================================
    // Constructor Tests
    // =====================================================================

    @Test
    public void testConstructor_withDtoMapping_thenInitializesWithEntityMapping() {
        // Arrange
        DtoMapping testMapping = new DtoMapping();

        // Act
        DtoToEntityMapping mapping = new DtoToEntityMapping(testMapping);

        // Assert
        assertNotNull(mapping);
        assertNotNull(mapping.getMatchDto());
    }

    @Test
    public void testNoArgsConstructor_thenInitializesWithNull() {
        // Arrange & Act
        DtoToEntityMapping mapping = new DtoToEntityMapping();

        // Assert
        assertNotNull(mapping);
    }

    @Test
    public void testAllArgsConstructor_withBothParams_thenInitializesSuccessfully() {
        // Arrange
        DtoMapping testDtoMapping = new DtoMapping();
        EntityMapping testEntityMapping = new EntityMapping();

        // Act
        DtoToEntityMapping mapping = new DtoToEntityMapping(testDtoMapping, testEntityMapping);

        // Assert
        assertNotNull(mapping);
    }

    // =====================================================================
    // Tests for getMatchDto()
    // =====================================================================

    @Test
    public void testGetMatchDto_whenMatchIsNull_thenReturnsEmpty() {
        // Arrange
        dtoMapping.setMatch(null);

        // Act
        Optional<MatchDto> result = dtoToEntityMapping.getMatchDto();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchDto_whenMatchExists_thenReturnsMatchDto() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Test Match");
        dtoMapping.setMatch(matchDto);

        // Act
        Optional<MatchDto> result = dtoToEntityMapping.getMatchDto();

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Match", result.get().getName());
    }

    @Test
    public void testGetMatchDto_whenMatchHasMinimalData_thenReturnsMatchDto() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        dtoMapping.setMatch(matchDto);

        // Act
        Optional<MatchDto> result = dtoToEntityMapping.getMatchDto();

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get());
    }

    // =====================================================================
    // Tests for getMatchEntity()
    // =====================================================================

    @Test
    public void testGetMatchEntity_whenMatchEntityIsNull_thenReturnsEmpty() {
        // Arrange & Act
        Optional<IpscMatch> result = dtoToEntityMapping.getMatchEntity();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchEntity_whenMatchEntityExists_thenReturnsMatchEntity() {
        // Arrange
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Match Entity");
        dtoToEntityMapping.setMatch(matchEntity);

        // Act
        Optional<IpscMatch> result = dtoToEntityMapping.getMatchEntity();

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Match Entity", result.get().getName());
    }

    // =====================================================================
    // Tests for getCompetitorDtoList()
    // =====================================================================

    @Test
    public void testGetCompetitorDtoList_whenMapIsNull_thenThrowsNullPointerException() {
        // Arrange
        dtoMapping.setCompetitorMap(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> dtoToEntityMapping.getCompetitorDtoList());
    }

    @Test
    public void testGetCompetitorDtoList_whenMapIsEmpty_thenReturnsEmptyList() {
        // Arrange
        dtoMapping.setCompetitorMap(new HashMap<>());

        // Act
        List<CompetitorDto> result = dtoToEntityMapping.getCompetitorDtoList();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCompetitorDtoList_withSingleCompetitor_thenReturnsListWithOne() {
        // Arrange
        HashMap<UUID, CompetitorDto> competitorMap = new HashMap<>();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setFirstName("John");
        UUID uuid = UUID.randomUUID();
        competitorMap.put(uuid, competitorDto);
        dtoMapping.setCompetitorMap(competitorMap);

        // Act
        List<CompetitorDto> result = dtoToEntityMapping.getCompetitorDtoList();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    public void testGetCompetitorDtoList_withMultipleCompetitors_thenReturnsAllNonNull() {
        // Arrange
        HashMap<UUID, CompetitorDto> competitorMap = new HashMap<>();
        CompetitorDto competitor1 = new CompetitorDto();
        competitor1.setFirstName("John");
        CompetitorDto competitor2 = new CompetitorDto();
        competitor2.setFirstName("Jane");
        competitorMap.put(UUID.randomUUID(), competitor1);
        competitorMap.put(UUID.randomUUID(), competitor2);
        dtoMapping.setCompetitorMap(competitorMap);

        // Act
        List<CompetitorDto> result = dtoToEntityMapping.getCompetitorDtoList();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetCompetitorDtoList_withNullValuesInMap_thenFiltersOutNulls() {
        // Arrange
        HashMap<UUID, CompetitorDto> competitorMap = new HashMap<>();
        CompetitorDto competitor1 = new CompetitorDto();
        competitor1.setFirstName("John");
        competitorMap.put(UUID.randomUUID(), competitor1);
        competitorMap.put(UUID.randomUUID(), null);
        dtoMapping.setCompetitorMap(competitorMap);

        // Act
        List<CompetitorDto> result = dtoToEntityMapping.getCompetitorDtoList();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    // =====================================================================
    // Tests for getMatchStageDtoList()
    // =====================================================================

    @Test
    public void testGetMatchStageDtoList_whenMapIsNull_thenThrowsNullPointerException() {
        // Arrange
        dtoMapping.setMatchStageMap(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> dtoToEntityMapping.getMatchStageDtoList());
    }

    @Test
    public void testGetMatchStageDtoList_whenMapIsEmpty_thenReturnsEmptyList() {
        // Arrange
        dtoMapping.setMatchStageMap(new HashMap<>());

        // Act
        List<MatchStageDto> result = dtoToEntityMapping.getMatchStageDtoList();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageDtoList_withSingleStage_thenReturnsListWithOne() {
        // Arrange
        HashMap<UUID, MatchStageDto> stageMap = new HashMap<>();
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageName("Stage 1");
        stageMap.put(UUID.randomUUID(), stageDto);
        dtoMapping.setMatchStageMap(stageMap);

        // Act
        List<MatchStageDto> result = dtoToEntityMapping.getMatchStageDtoList();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Stage 1", result.get(0).getStageName());
    }

    @Test
    public void testGetMatchStageDtoList_withMultipleStages_thenReturnsAll() {
        // Arrange
        HashMap<UUID, MatchStageDto> stageMap = new HashMap<>();
        MatchStageDto stage1 = new MatchStageDto();
        stage1.setStageName("Stage 1");
        MatchStageDto stage2 = new MatchStageDto();
        stage2.setStageName("Stage 2");
        stageMap.put(UUID.randomUUID(), stage1);
        stageMap.put(UUID.randomUUID(), stage2);
        dtoMapping.setMatchStageMap(stageMap);

        // Act
        List<MatchStageDto> result = dtoToEntityMapping.getMatchStageDtoList();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetMatchStageDtoList_withNullValuesInMap_thenFiltersOutNulls() {
        // Arrange
        HashMap<UUID, MatchStageDto> stageMap = new HashMap<>();
        MatchStageDto stage1 = new MatchStageDto();
        stage1.setStageName("Stage 1");
        stageMap.put(UUID.randomUUID(), stage1);
        stageMap.put(UUID.randomUUID(), null);
        dtoMapping.setMatchStageMap(stageMap);

        // Act
        List<MatchStageDto> result = dtoToEntityMapping.getMatchStageDtoList();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Stage 1", result.get(0).getStageName());
    }

    // =====================================================================
    // Tests for getMatchCompetitorDtoList()
    // =====================================================================

    @Test
    public void testGetMatchCompetitorDtoList_whenMapIsNull_thenThrowsNullPointerException() {
        // Arrange
        dtoMapping.setMatchCompetitorMap(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> dtoToEntityMapping.getMatchCompetitorDtoList());
    }

    @Test
    public void testGetMatchCompetitorDtoList_whenMapIsEmpty_thenReturnsEmptyList() {
        // Arrange
        dtoMapping.setMatchCompetitorMap(new HashMap<>());

        // Act
        List<MatchCompetitorDto> result = dtoToEntityMapping.getMatchCompetitorDtoList();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchCompetitorDtoList_withSingleMatchCompetitor_thenReturnsListWithOne() {
        // Arrange
        HashMap<UUID, MatchCompetitorDto> matchCompetitorMap = new HashMap<>();
        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setCompetitorIndex(1);
        matchCompetitorMap.put(UUID.randomUUID(), matchCompetitorDto);
        dtoMapping.setMatchCompetitorMap(matchCompetitorMap);

        // Act
        List<MatchCompetitorDto> result = dtoToEntityMapping.getMatchCompetitorDtoList();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCompetitorIndex());
    }

    @Test
    public void testGetMatchCompetitorDtoList_withNullValuesInMap_thenFiltersOutNulls() {
        // Arrange
        HashMap<UUID, MatchCompetitorDto> matchCompetitorMap = new HashMap<>();
        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setCompetitorIndex(1);
        matchCompetitorMap.put(UUID.randomUUID(), matchCompetitorDto);
        matchCompetitorMap.put(UUID.randomUUID(), null);
        dtoMapping.setMatchCompetitorMap(matchCompetitorMap);

        // Act
        List<MatchCompetitorDto> result = dtoToEntityMapping.getMatchCompetitorDtoList();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // =====================================================================
    // Tests for getMatchStageCompetitorDtoList()
    // =====================================================================

    @Test
    public void testGetMatchStageCompetitorDtoList_whenMapIsNull_thenThrowsNullPointerException() {
        // Arrange
        dtoMapping.setMatchStageCompetitorMap(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> dtoToEntityMapping.getMatchStageCompetitorDtoList());
    }

    @Test
    public void testGetMatchStageCompetitorDtoList_whenMapIsEmpty_thenReturnsEmptyList() {
        // Arrange
        dtoMapping.setMatchStageCompetitorMap(new HashMap<>());

        // Act
        List<MatchStageCompetitorDto> result = dtoToEntityMapping.getMatchStageCompetitorDtoList();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMatchStageCompetitorDtoList_withSingleStageCompetitor_thenReturnsListWithOne() {
        // Arrange
        HashMap<UUID, MatchStageCompetitorDto> stageCompetitorMap = new HashMap<>();
        MatchStageCompetitorDto stageCompetitorDto = new MatchStageCompetitorDto();
        stageCompetitorDto.setCompetitorIndex(1);
        stageCompetitorMap.put(UUID.randomUUID(), stageCompetitorDto);
        dtoMapping.setMatchStageCompetitorMap(stageCompetitorMap);

        // Act
        List<MatchStageCompetitorDto> result = dtoToEntityMapping.getMatchStageCompetitorDtoList();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCompetitorIndex());
    }

    @Test
    public void testGetMatchStageCompetitorDtoList_withNullValuesInMap_thenFiltersOutNulls() {
        // Arrange
        HashMap<UUID, MatchStageCompetitorDto> stageCompetitorMap = new HashMap<>();
        MatchStageCompetitorDto stageCompetitorDto = new MatchStageCompetitorDto();
        stageCompetitorDto.setCompetitorIndex(1);
        stageCompetitorMap.put(UUID.randomUUID(), stageCompetitorDto);
        stageCompetitorMap.put(UUID.randomUUID(), null);
        dtoMapping.setMatchStageCompetitorMap(stageCompetitorMap);

        // Act
        List<MatchStageCompetitorDto> result = dtoToEntityMapping.getMatchStageCompetitorDtoList();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // =====================================================================
    // Tests for setMatch()
    // =====================================================================

    @Test
    public void testSetMatch_withValidMatch_thenSetsMatchEntity() {
        // Arrange
        IpscMatch matchEntity = new IpscMatch();
        matchEntity.setName("Test Match");

        // Act
        dtoToEntityMapping.setMatch(matchEntity);

        // Assert
        assertTrue(dtoToEntityMapping.getMatchEntity().isPresent());
        assertEquals("Test Match", dtoToEntityMapping.getMatchEntity().get().getName());
    }

    @Test
    public void testSetMatch_withNull_thenSetsNullMatchEntity() {
        // Arrange & Act
        dtoToEntityMapping.setMatch(null);

        // Assert
        assertTrue(dtoToEntityMapping.getMatchEntity().isEmpty());
    }

    @Test
    public void testSetMatch_withMinimalMatch_thenSetsMatchEntity() {
        // Arrange
        IpscMatch matchEntity = new IpscMatch();

        // Act
        dtoToEntityMapping.setMatch(matchEntity);

        // Assert
        assertTrue(dtoToEntityMapping.getMatchEntity().isPresent());
    }

    // =====================================================================
    // Tests for setCompetitor()
    // =====================================================================

    @Test
    public void testSetCompetitor_withValidData_thenStoresCompetitor() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        UUID uuid = UUID.randomUUID();
        competitorDto.setUuid(uuid);
        competitorDto.setFirstName("John");
        Competitor competitorEntity = new Competitor();
        competitorEntity.setFirstName("John");

        // Act
        dtoToEntityMapping.setCompetitor(competitorDto, competitorEntity);

        // Assert
        assertNotNull(dtoToEntityMapping.getCompetitorDtoList());
    }

    @Test
    public void testSetCompetitor_withNullCompetitorDto_thenThrowsNullPointerException() {
        // Arrange
        Competitor competitorEntity = new Competitor();

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                dtoToEntityMapping.setCompetitor(null, competitorEntity)
        );
    }

    @Test
    public void testSetCompetitor_withNullEntity_thenStoresNullEntity() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        UUID uuid = UUID.randomUUID();
        competitorDto.setUuid(uuid);

        // Act & Assert - should not throw exception, stores null entity
        assertDoesNotThrow(() -> dtoToEntityMapping.setCompetitor(competitorDto, null));
    }

    // =====================================================================
    // Tests for setMatchStage()
    // =====================================================================

    @Test
    public void testSetMatchStage_withValidData_thenStoresMatchStage() {
        // Arrange
        MatchStageDto matchStageDto = new MatchStageDto();
        UUID uuid = UUID.randomUUID();
        matchStageDto.setUuid(uuid);
        matchStageDto.setStageName("Stage 1");
        IpscMatchStage matchStageEntity = new IpscMatchStage();
        matchStageEntity.setStageName("Stage 1");

        // Act
        dtoToEntityMapping.setMatchStage(matchStageDto, matchStageEntity);

        // Assert
        assertNotNull(dtoToEntityMapping);
    }

    @Test
    public void testSetMatchStage_withNullMatchStageDto_thenThrowsNullPointerException() {
        // Arrange
        IpscMatchStage matchStageEntity = new IpscMatchStage();

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                dtoToEntityMapping.setMatchStage(null, matchStageEntity)
        );
    }

    @Test
    public void testSetMatchStage_withNullEntity_thenStoresNullEntity() {
        // Arrange
        MatchStageDto matchStageDto = new MatchStageDto();
        UUID uuid = UUID.randomUUID();
        matchStageDto.setUuid(uuid);

        // Act & Assert - should not throw exception, stores null entity
        assertDoesNotThrow(() -> dtoToEntityMapping.setMatchStage(matchStageDto, null));
    }

    // =====================================================================
    // Tests for setMatchCompetitor()
    // =====================================================================

    @Test
    public void testSetMatchCompetitor_withValidData_thenStoresMatchCompetitor() {
        // Arrange
        IpscMatch matchEntity = new IpscMatch();
        dtoToEntityMapping.setMatch(matchEntity);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(UUID.randomUUID());
        Competitor competitorEntity = new Competitor();
        dtoToEntityMapping.setCompetitor(competitorDto, competitorEntity);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setUuid(UUID.randomUUID());
        matchCompetitorDto.setCompetitor(competitorDto);
        MatchCompetitor matchCompetitorEntity = new MatchCompetitor();

        // Act
        dtoToEntityMapping.setMatchCompetitor(matchCompetitorDto, matchCompetitorEntity);

        // Assert
        assertNotNull(dtoToEntityMapping);
    }

    @Test
    public void testSetMatchCompetitor_withNullMatchCompetitorDto_thenThrowsNullPointerException() {
        // Arrange
        MatchCompetitor matchCompetitorEntity = new MatchCompetitor();

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                dtoToEntityMapping.setMatchCompetitor(null, matchCompetitorEntity)
        );
    }

    @Test
    public void testSetMatchCompetitor_withNullEntity_thenThrowsNullPointerException() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(UUID.randomUUID());
        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setUuid(UUID.randomUUID());
        matchCompetitorDto.setCompetitor(competitorDto);

        // Act & Assert - null entity causes NPE when setting match/competitor
        assertThrows(NullPointerException.class, () ->
                dtoToEntityMapping.setMatchCompetitor(matchCompetitorDto, null)
        );
    }

    // =====================================================================
    // Tests for setMatchStageCompetitor()
    // =====================================================================

    @Test
    public void testSetMatchStageCompetitor_withValidData_thenStoresMatchStageCompetitor() {
        // Arrange
        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setUuid(UUID.randomUUID());
        IpscMatchStage matchStageEntity = new IpscMatchStage();
        dtoToEntityMapping.setMatchStage(matchStageDto, matchStageEntity);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(UUID.randomUUID());
        Competitor competitorEntity = new Competitor();
        dtoToEntityMapping.setCompetitor(competitorDto, competitorEntity);

        MatchStageCompetitorDto stageCompetitorDto = new MatchStageCompetitorDto();
        stageCompetitorDto.setUuid(UUID.randomUUID());
        stageCompetitorDto.setCompetitor(competitorDto);
        stageCompetitorDto.setMatchStage(matchStageDto);
        MatchStageCompetitor stageCompetitorEntity = new MatchStageCompetitor();

        // Act
        dtoToEntityMapping.setMatchStageCompetitor(stageCompetitorDto, stageCompetitorEntity);

        // Assert
        assertNotNull(dtoToEntityMapping.getMatchStageCompetitorDtoList());
    }

    @Test
    public void testSetMatchStageCompetitor_withNullStageCompetitorDto_thenThrowsNullPointerException() {
        // Arrange
        MatchStageCompetitor stageCompetitorEntity = new MatchStageCompetitor();

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                dtoToEntityMapping.setMatchStageCompetitor(null, stageCompetitorEntity)
        );
    }

    @Test
    public void testSetMatchStageCompetitor_withNullEntity_thenThrowsNullPointerException() {
        // Arrange
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(UUID.randomUUID());
        MatchStageDto matchStageDto = new MatchStageDto();
        matchStageDto.setUuid(UUID.randomUUID());
        MatchStageCompetitorDto stageCompetitorDto = new MatchStageCompetitorDto();
        stageCompetitorDto.setUuid(UUID.randomUUID());
        stageCompetitorDto.setCompetitor(competitorDto);
        stageCompetitorDto.setMatchStage(matchStageDto);

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                dtoToEntityMapping.setMatchStageCompetitor(stageCompetitorDto, null)
        );
    }

    // =====================================================================
    // Integration Tests - Multiple Operations
    // =====================================================================

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

