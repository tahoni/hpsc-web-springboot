package za.co.hpsc.web.models.ipsc.dto;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.models.ipsc.response.StageResponse;

import static org.junit.jupiter.api.Assertions.*;

public class MatchStageDtoTest {

    // Constructor mapping - Single Parameter

    // Null and Empty Cases
    @Test
    void testConstructor_whenIpscMatchStageNull_thenKeepsDefaults() {
        // Arrange & Act
        MatchStageDto dto = new MatchStageDto(null);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getIndex());
        assertNull(dto.getMatch());
        assertEquals(0, dto.getStageNumber());
        assertNull(dto.getStageName());
        assertNull(dto.getRangeNumber());
    }

    @Test
    void testConstructor_whenIpscMatchStageHasNullFields_thenMapsNulls() {
        // Arrange
        IpscMatchStage stage = getIpscMatchStage();

        // Act
        MatchStageDto dto = new MatchStageDto(stage);

        // Assert
        assertEquals(42L, dto.getId());
        assertNotNull(dto.getMatch());
        assertNull(dto.getStageNumber());
        assertNull(dto.getStageName());
        assertNull(dto.getRangeNumber());
        assertNull(dto.getTargetPaper());
        assertNull(dto.getTargetPopper());
        assertNull(dto.getTargetPlates());
        assertNull(dto.getTargetDisappear());
        assertNull(dto.getTargetPenalty());
        assertNull(dto.getMinRounds());
        assertNull(dto.getMaxPoints());
    }

    @Test
    void testConstructor_whenIpscMatchStageHasEmptyOrBlankStageName_thenDoesNotMapStageName() {
        // Arrange - Test with empty string
        IpscMatch match1 = new IpscMatch();
        match1.setId(10L);
        match1.setName("Test Match");

        IpscMatchStage stage1 = new IpscMatchStage();
        stage1.setId(1L);
        stage1.setMatch(match1);
        stage1.setStageNumber(1);
        stage1.setStageName("");

        // Act
        MatchStageDto dto1 = new MatchStageDto(stage1);

        // Assert
        assertNull(dto1.getStageName()); // Single param constructor doesn't map stageName

        // Arrange - Test with blank string
        IpscMatch match2 = new IpscMatch();
        match2.setId(10L);
        match2.setName("Test Match");

        IpscMatchStage stage2 = new IpscMatchStage();
        stage2.setId(2L);
        stage2.setMatch(match2);
        stage2.setStageNumber(2);
        stage2.setStageName("   ");

        // Act
        MatchStageDto dto2 = new MatchStageDto(stage2);

        // Assert
        assertNull(dto2.getStageName()); // Single param constructor doesn't map stageName
    }

    // Partially Populated
    @Test
    void testConstructor_whenIpscMatchStagePartiallyPopulated_thenMapsSetFieldsAndNulls() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(10L);
        match.setName("Test Match");

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(50L);
        stage.setMatch(match);
        stage.setStageNumber(3);
        stage.setStageName("Stage 3");
        stage.setRangeNumber(1);

        // Act
        MatchStageDto dto = new MatchStageDto(stage);

        // Assert
        assertEquals(50L, dto.getId());
        assertEquals(3, dto.getStageNumber());
        assertNull(dto.getStageName()); // Single param constructor doesn't map stageName
        assertEquals(1, dto.getRangeNumber());
        assertNull(dto.getTargetPaper());
        assertNull(dto.getTargetPopper());
        assertNull(dto.getMinRounds());
        assertNull(dto.getMaxPoints());
    }

    // Fully Populated
    @Test
    void testConstructor_whenIpscMatchStageFullyPopulated_thenMapsAllFields() {
        // Arrange
        MatchStageDto dto = getMatchStageDto();

        // Assert
        assertEquals(99L, dto.getId());
        assertNotNull(dto.getMatch());
        assertEquals("Championship Match", dto.getMatch().getName());
        assertEquals(5, dto.getStageNumber());
        assertNull(dto.getStageName()); // Single param constructor doesn't map stageName
        assertEquals(2, dto.getRangeNumber());
        assertEquals(8, dto.getTargetPaper());
        assertEquals(4, dto.getTargetPopper());
        assertEquals(6, dto.getTargetPlates());
        assertEquals(2, dto.getTargetDisappear());
        assertEquals(0, dto.getTargetPenalty());
        assertEquals(20, dto.getMinRounds());
        assertEquals(100, dto.getMaxPoints());
    }

    @Test
    void testConstructor_whenIpscMatchStagePopulated_thenUuidIsNotNull() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(10L);

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(100L);
        stage.setMatch(match);
        stage.setStageNumber(1);

        // Act
        MatchStageDto dto = new MatchStageDto(stage);

        // Assert
        assertNotNull(dto.getUuid());
    }

    // Constructor mapping - Two Parameters

    // Null and Empty Cases
    @Test
    void testConstructor_whenIpscMatchStageAndMatchDtoNull_thenKeepsDefaults() {
        // Act
        MatchStageDto dto = new MatchStageDto(null, null);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getMatch());
        assertEquals(0, dto.getStageNumber());
    }

    @Test
    void testConstructor_whenIpscMatchStageNullAndMatchDtoProvided_thenKeepsDefaults() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Test Match");

        // Act
        MatchStageDto dto = new MatchStageDto(null, matchDto);

        // Assert
        assertNotNull(dto.getUuid());
        assertNull(dto.getId());
        assertNull(dto.getMatch());
    }

    @Test
    void testConstructor_whenIpscMatchStageProvidedAndMatchDtoNull_thenUsesStageMatch() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(5L);
        match.setName("Stage Match");

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(20L);
        stage.setMatch(match);
        stage.setStageNumber(2);

        // Act
        MatchStageDto dto = new MatchStageDto(stage, null);

        // Assert
        assertEquals(20L, dto.getId());
        assertNull(dto.getMatch());
        assertEquals(2, dto.getStageNumber());
    }

    // Fully Populated
    @Test
    void testConstructor_whenIpscMatchStageAndMatchDtoProvided_thenUsesMatchDto() {
        // Arrange
        IpscMatch match = new IpscMatch();
        match.setId(5L);
        match.setName("Entity Match");

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(20L);
        stage.setMatch(match);
        stage.setStageNumber(3);
        stage.setStageName("Stage 3");

        MatchDto matchDto = new MatchDto();
        matchDto.setName("DTO Match");

        // Act
        MatchStageDto dto = new MatchStageDto(stage, matchDto);

        // Assert
        assertEquals(20L, dto.getId());
        assertNotNull(dto.getMatch());
        assertEquals("DTO Match", dto.getMatch().getName());
        assertEquals(3, dto.getStageNumber());
        assertEquals("Stage 3", dto.getStageName());
    }

    @Test
    void testConstructor_whenIpscMatchStageAndMatchDtoFullyPopulated_thenMapsAllFields() {
        // Arrange
        MatchStageDto dto = getStageDto();

        // Assert
        assertEquals(30L, dto.getId());
        assertEquals("Full Match", dto.getMatch().getName());
        assertEquals(7, dto.getStageNumber());
        assertEquals("Precision Stage", dto.getStageName());
        assertEquals(3, dto.getRangeNumber());
        assertEquals(10, dto.getTargetPaper());
        assertEquals(5, dto.getTargetPopper());
        assertEquals(8, dto.getTargetPlates());
        assertEquals(3, dto.getTargetDisappear());
        assertEquals(1, dto.getTargetPenalty());
        assertEquals(25, dto.getMinRounds());
        assertEquals(150, dto.getMaxPoints());
    }

    // init() mappings

    // Null and Existing Value Handling
    @Test
    void testInit_whenBothParametersNull_thenKeepsExistingValues() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();
        dto.setIndex(5);
        dto.setStageNumber(3);
        dto.setStageName("Existing Stage");
        dto.setRangeNumber(1);

        // Act
        dto.init(null, null);

        // Assert
        assertEquals(5, dto.getIndex());
        assertEquals(3, dto.getStageNumber());
        assertEquals("Existing Stage", dto.getStageName());
        assertEquals(1, dto.getRangeNumber());
    }

    @Test
    void testInit_whenMatchDtoNullAndStageResponseProvided_thenKeepsExistingValues() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();
        dto.setStageNumber(2);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(10);
        stageResponse.setStageName("New Stage");

        // Act
        dto.init(null, stageResponse);

        // Assert
        assertEquals(10, dto.getStageNumber());
        assertEquals(10, dto.getIndex());
    }

    @Test
    void testInit_whenStageResponseNullAndMatchDtoProvided_thenUpdatesValues() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();
        dto.setStageNumber(4);

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Test Match");

        // Act
        dto.init(matchDto, null);

        // Assert
        assertEquals(4, dto.getStageNumber());
        assertEquals("Test Match", dto.getMatch().getName());
    }

    // Basic Field Mapping
    @Test
    void testInit_whenMatchDtoAndStageResponseProvided_thenMapsAllFields() {
        // Arrange
        MatchStageDto dto = getDto();

        // Assert
        assertEquals(12, dto.getIndex());
        assertEquals("Spring Championship", dto.getMatch().getName());
        assertEquals(12, dto.getStageNumber());
        assertEquals("Stage 12 - Accuracy", dto.getStageName());
        assertEquals(0, dto.getRangeNumber());
        assertEquals(6, dto.getTargetPaper());
        assertEquals(3, dto.getTargetPopper());
        assertEquals(4, dto.getTargetPlates());
        assertEquals(1, dto.getTargetDisappear());
        assertEquals(0, dto.getTargetPenalty());
        assertEquals(15, dto.getMinRounds());
        assertEquals(75, dto.getMaxPoints());
    }

    // Edge Cases - Null/Empty/Blank Stage Name
    @Test
    void testInit_whenStageResponseHasNullEmptyOrBlankStageName_thenMapsCorrectly() {
        // Test 1: Null stageName
        MatchStageDto dto1 = new MatchStageDto();
        MatchDto matchDto1 = new MatchDto();
        matchDto1.setName("Test Match");
        StageResponse stageResponse1 = new StageResponse();
        stageResponse1.setStageId(20);
        stageResponse1.setStageName(null);

        dto1.init(matchDto1, stageResponse1);

        assertEquals(20, dto1.getIndex());
        assertNull(dto1.getStageName());

        // Test 2: Empty stageName
        MatchStageDto dto2 = new MatchStageDto();
        MatchDto matchDto2 = new MatchDto();
        matchDto2.setName("Test Match");
        StageResponse stageResponse2 = new StageResponse();
        stageResponse2.setStageId(21);
        stageResponse2.setStageName("");

        dto2.init(matchDto2, stageResponse2);

        assertEquals(21, dto2.getIndex());
        assertEquals("", dto2.getStageName());

        // Test 3: Blank stageName
        MatchStageDto dto3 = new MatchStageDto();
        MatchDto matchDto3 = new MatchDto();
        matchDto3.setName("Test Match");
        StageResponse stageResponse3 = new StageResponse();
        stageResponse3.setStageId(22);
        stageResponse3.setStageName("   ");

        dto3.init(matchDto3, stageResponse3);

        assertEquals(22, dto3.getIndex());
        assertEquals("   ", dto3.getStageName());
    }

    @Test
    void testInit_whenStageResponseHasNullTargetFields_thenMapsNulls() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Test Match");

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(30);
        stageResponse.setStageName("Stage 30");
        stageResponse.setTargetPaper(null);
        stageResponse.setTargetPopper(null);
        stageResponse.setTargetPlates(null);
        stageResponse.setTargetDisappear(null);
        stageResponse.setTargetPenalty(null);
        stageResponse.setMinRounds(null);
        stageResponse.setMaxPoints(null);

        // Act
        dto.init(matchDto, stageResponse);

        // Assert
        assertEquals(30, dto.getIndex());
        assertNull(dto.getTargetPaper());
        assertNull(dto.getTargetPopper());
        assertNull(dto.getTargetPlates());
        assertNull(dto.getTargetDisappear());
        assertNull(dto.getTargetPenalty());
        assertNull(dto.getMinRounds());
        assertNull(dto.getMaxPoints());
    }

    // Partially Populated
    @Test
    void testInit_whenStageResponsePartiallyPopulated_thenMapsSetFieldsAndNulls() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Test Match");

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(40);
        stageResponse.setStageName("Partial Stage");
        stageResponse.setTargetPaper(5);

        // Act
        dto.init(matchDto, stageResponse);

        // Assert
        assertEquals(40, dto.getIndex());
        assertEquals("Partial Stage", dto.getStageName());
        assertEquals(5, dto.getTargetPaper());
        assertNull(dto.getTargetPopper());
        assertNull(dto.getMinRounds());
        assertNull(dto.getMaxPoints());
    }

    // Range Number Behavior
    @Test
    void testInit_whenStageResponseProvided_thenRangeNumberSetToZero() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();
        dto.setRangeNumber(5);

        MatchDto matchDto = new MatchDto();
        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(50);

        // Act
        dto.init(matchDto, stageResponse);

        // Assert
        assertEquals(0, dto.getRangeNumber());
    }

    // Fully Populated
    @Test
    void testInit_whenStageResponseFullyPopulated_thenMapsAllFields() {
        // Arrange
        MatchStageDto dto = getMatchStageDto1();

        // Assert
        assertEquals(60, dto.getIndex());
        assertEquals("Complete Match", dto.getMatch().getName());
        assertEquals(60, dto.getStageNumber());
        assertEquals("Stage 60 - Complete", dto.getStageName());
        assertEquals(0, dto.getRangeNumber());
        assertEquals(12, dto.getTargetPaper());
        assertEquals(8, dto.getTargetPopper());
        assertEquals(10, dto.getTargetPlates());
        assertEquals(4, dto.getTargetDisappear());
        assertEquals(2, dto.getTargetPenalty());
        assertEquals(30, dto.getMinRounds());
        assertEquals(200, dto.getMaxPoints());
    }

    // Special Cases - Zero and Max Values
    @Test
    void testInit_whenStageResponseHasAllZeroTargets_thenMapsZeros() {
        // Arrange
        MatchStageDto dto = getMatchStageDto2();

        // Assert
        assertEquals(70, dto.getIndex());
        assertEquals(0, dto.getTargetPaper());
        assertEquals(0, dto.getTargetPopper());
        assertEquals(0, dto.getTargetPlates());
        assertEquals(0, dto.getTargetDisappear());
        assertEquals(0, dto.getTargetPenalty());
        assertEquals(0, dto.getMinRounds());
        assertEquals(0, dto.getMaxPoints());
    }

    @Test
    void testInit_whenStageResponseHasMaxValues_thenMapsCorrectly() {
        // Arrange
        MatchStageDto dto = getMatchStageDto3();

        // Assert
        assertEquals(100, dto.getIndex());
        assertEquals(100, dto.getStageNumber());
        assertEquals(999, dto.getTargetPaper());
        assertEquals(999, dto.getTargetPopper());
        assertEquals(999, dto.getTargetPlates());
        assertEquals(999, dto.getTargetDisappear());
        assertEquals(999, dto.getTargetPenalty());
        assertEquals(999, dto.getMinRounds());
        assertEquals(99999, dto.getMaxPoints());
    }

    // Selective Field Population
    @Test
    void testInit_whenOnlyStageNamePopulated_thenMapsOnlyNameAndKeepsNulls() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Test Match");

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(80);
        stageResponse.setStageName("Name Only");

        // Act
        dto.init(matchDto, stageResponse);

        // Assert
        assertEquals(80, dto.getIndex());
        assertEquals("Name Only", dto.getStageName());
        assertNull(dto.getTargetPaper());
        assertNull(dto.getTargetPopper());
        assertNull(dto.getTargetPlates());
        assertNull(dto.getTargetDisappear());
        assertNull(dto.getTargetPenalty());
        assertNull(dto.getMinRounds());
        assertNull(dto.getMaxPoints());
    }

    @Test
    void testInit_whenOnlyTargetFieldsPopulated_thenMapsTargetsAndKeepsNulls() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Target Match");

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(90);
        stageResponse.setStageName(null);
        stageResponse.setTargetPaper(7);
        stageResponse.setTargetPopper(5);
        stageResponse.setTargetPlates(6);
        stageResponse.setTargetDisappear(2);
        stageResponse.setTargetPenalty(1);

        // Act
        dto.init(matchDto, stageResponse);

        // Assert
        assertEquals(90, dto.getIndex());
        assertNull(dto.getStageName());
        assertEquals(7, dto.getTargetPaper());
        assertEquals(5, dto.getTargetPopper());
        assertEquals(6, dto.getTargetPlates());
        assertEquals(2, dto.getTargetDisappear());
        assertEquals(1, dto.getTargetPenalty());
        assertNull(dto.getMinRounds());
        assertNull(dto.getMaxPoints());
    }

    @Test
    void testInit_whenOnlyRoundsAndPointsPopulated_thenMapsPointsAndKeepsNulls() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Points Match");

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(110);
        stageResponse.setStageName(null);
        stageResponse.setMinRounds(50);
        stageResponse.setMaxPoints(500);

        // Act
        dto.init(matchDto, stageResponse);

        // Assert
        assertEquals(110, dto.getIndex());
        assertNull(dto.getStageName());
        assertNull(dto.getTargetPaper());
        assertNull(dto.getTargetPopper());
        assertNull(dto.getTargetPlates());
        assertNull(dto.getTargetDisappear());
        assertNull(dto.getTargetPenalty());
        assertEquals(50, dto.getMinRounds());
        assertEquals(500, dto.getMaxPoints());
    }

    // Match DTO with Various Name States
    @Test
    void testInit_whenMatchDtoHasNullName_thenStillMapsStageData() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();

        MatchDto matchDto = new MatchDto();
        matchDto.setName(null);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(120);
        stageResponse.setStageName("Null Match Name");
        stageResponse.setTargetPaper(3);

        // Act
        dto.init(matchDto, stageResponse);

        // Assert
        assertEquals(120, dto.getIndex());
        assertNull(dto.getMatch().getName());
        assertEquals("Null Match Name", dto.getStageName());
        assertEquals(3, dto.getTargetPaper());
    }

    @Test
    void testInit_whenMatchDtoHasEmptyName_thenStillMapsStageData() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();

        MatchDto matchDto = new MatchDto();
        matchDto.setName("");

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(130);
        stageResponse.setStageName("Empty Match Name");
        stageResponse.setTargetPopper(2);

        // Act
        dto.init(matchDto, stageResponse);

        // Assert
        assertEquals(130, dto.getIndex());
        assertEquals("", dto.getMatch().getName());
        assertEquals("Empty Match Name", dto.getStageName());
        assertEquals(2, dto.getTargetPopper());
    }

    @Test
    void testInit_whenMatchDtoHasBlankName_thenStillMapsStageData() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();

        MatchDto matchDto = new MatchDto();
        matchDto.setName("   ");

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(140);
        stageResponse.setStageName("Blank Match Name");
        stageResponse.setTargetPlates(4);

        // Act
        dto.init(matchDto, stageResponse);

        // Assert
        assertEquals(140, dto.getIndex());
        assertEquals("   ", dto.getMatch().getName());
        assertEquals("Blank Match Name", dto.getStageName());
        assertEquals(4, dto.getTargetPlates());
    }

    // Overwriting Existing Values
    @Test
    void testInit_whenOverwritingExistingValues_thenReplacesProperly() {
        // Arrange
        MatchStageDto dto = new MatchStageDto();
        dto.setIndex(999);
        dto.setStageNumber(999);
        dto.setStageName("Old Name");
        dto.setRangeNumber(99);
        dto.setTargetPaper(99);
        dto.setTargetPopper(99);
        dto.setTargetPlates(99);
        dto.setTargetDisappear(99);
        dto.setTargetPenalty(99);
        dto.setMinRounds(99);
        dto.setMaxPoints(99);

        MatchDto matchDto = new MatchDto();
        matchDto.setName("New Match");

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(150);
        stageResponse.setStageName("New Name");
        stageResponse.setTargetPaper(1);
        stageResponse.setTargetPopper(2);
        stageResponse.setTargetPlates(3);
        stageResponse.setTargetDisappear(4);
        stageResponse.setTargetPenalty(5);
        stageResponse.setMinRounds(10);
        stageResponse.setMaxPoints(50);

        // Act
        dto.init(matchDto, stageResponse);

        // Assert
        assertEquals(150, dto.getIndex());
        assertEquals(150, dto.getStageNumber());
        assertEquals("New Name", dto.getStageName());
        assertEquals(0, dto.getRangeNumber());
        assertEquals(1, dto.getTargetPaper());
        assertEquals(2, dto.getTargetPopper());
        assertEquals(3, dto.getTargetPlates());
        assertEquals(4, dto.getTargetDisappear());
        assertEquals(5, dto.getTargetPenalty());
        assertEquals(10, dto.getMinRounds());
        assertEquals(50, dto.getMaxPoints());
    }

    // toString() behavior

    // Fully Populated - Stage Number and Match Provided
    @Test
    void testToString_whenStageNumberProvided_thenReturnsStageNumberAndMatch() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("HPSC");
        clubDto.setAbbreviation("HPSC");

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Club Shoot");
        matchDto.setClub(clubDto);

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(1);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("1 for Eufees Club Shoot @ HPSC", result);
    }

    @Test
    void testToString_whenStageNumberProvidedWithoutClub_thenReturnsStageNumberAndMatchName() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Simple Match");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(5);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("5 for Simple Match", result);
    }

    // Stage Number Null/Missing or High Values
    @Test
    void testToString_whenStageNumberHighValue_thenReturnsHighNumber() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Multi-Stage Match");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(99);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("99 for Multi-Stage Match", result);
    }

    // Stage Number Missing or Null
    @Test
    void testToString_whenStageNumberMissing_thenReturnsZeroAndMatch() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Club Shoot");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("0 for Eufees Club Shoot", result);
    }

    @Test
    void testToString_whenStageNumberNull_thenReturnsZeroAndMatch() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Eufees Club Shoot");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(null);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("0 for Eufees Club Shoot", result);
    }

    // Match Name Null/Empty/Blank
    @Test
    void testToString_whenMatchNameNull_thenReturnsStageNumber() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName(null);

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(3);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("3 for", result);
    }

    @Test
    void testToString_whenMatchNameEmpty_thenReturnsStageNumber() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(4);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("4 for", result);
    }

    @Test
    void testToString_whenMatchNameBlank_thenReturnsStageNumber() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("   ");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(2);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("2 for", result);
    }

    // Match Null
    @Test
    void testToString_whenMatchNull_thenReturnsStageNumber() {
        // Arrange
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(7);
        stageDto.setMatch(null);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("7 for", result);
    }

    // Club Information Variations
    @Test
    void testToString_whenClubWithName_thenIncludesClubName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Hartbeespoortdam Practical Shooting Club");
        clubDto.setAbbreviation("HPSC");

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Championship");
        matchDto.setClub(clubDto);

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(10);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("10 for Championship @ Hartbeespoortdam Practical Shooting Club (HPSC)", result);
    }

    @Test
    void testToString_whenClubWithoutAbbreviation_thenIncludesNameOnly() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Shooting Club");
        clubDto.setAbbreviation(null);

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Match");
        matchDto.setClub(clubDto);

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(6);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("6 for Match @ Shooting Club", result);
    }

    @Test
    void testToString_whenClubNameEmpty_thenIncludesEmptyClubName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("");
        clubDto.setAbbreviation("SC");

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Event");
        matchDto.setClub(clubDto);

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(8);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("8 for Event @ SC", result);
    }

    @Test
    void testToString_whenClubNameBlank_thenIncludesBlankClubName() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("   ");
        clubDto.setAbbreviation("ABC");

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Tournament");
        matchDto.setClub(clubDto);

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(9);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("9 for Tournament @ ABC", result);
    }

    // Stage Number with Various Values
    @Test
    void testToString_whenStageNumberZero_thenReturnsZero() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Zero Stage Match");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(0);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("0 for Zero Stage Match", result);
    }

    @Test
    void testToString_whenStageNumberNegative_thenReturnsNegativeNumber() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Negative Test");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(-1);
        stageDto.setMatch(matchDto);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("-1 for Negative Test", result);
    }

    // Full Field Population Variations
    @Test
    void testToString_whenFullyPopulatedWithAllDetails_thenReturnsCompleteString() {
        // Arrange
        ClubDto clubDto = new ClubDto();
        clubDto.setName("Premier Shooting Range");
        clubDto.setAbbreviation("PSR");

        MatchDto matchDto = new MatchDto();
        matchDto.setName("Annual Championship");
        matchDto.setClub(clubDto);

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(15);
        stageDto.setMatch(matchDto);
        stageDto.setStageName("Precision Shooting");
        stageDto.setRangeNumber(3);
        stageDto.setTargetPaper(10);
        stageDto.setTargetPopper(5);
        stageDto.setTargetPlates(8);
        stageDto.setTargetDisappear(3);
        stageDto.setTargetPenalty(2);
        stageDto.setMinRounds(25);
        stageDto.setMaxPoints(250);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("15 for Annual Championship @ Premier Shooting Range (PSR)", result);
    }

    // Partial Field Population
    @Test
    void testToString_whenPartiallyPopulated_thenReturnsFormattedString() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Partial Match");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(11);
        stageDto.setMatch(matchDto);
        stageDto.setStageName("Partial Stage");

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("11 for Partial Match", result);
    }

    @Test
    void testToString_whenMatchWithStageNameIgnored_thenDoesNotIncludeStageName() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Simple Match");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(12);
        stageDto.setMatch(matchDto);
        stageDto.setStageName("This Should Not Appear");

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("12 for Simple Match", result);
        assertFalse(result.contains("This Should Not Appear"));
    }

    @Test
    void testToString_whenTargetFieldsSet_thenDoesNotIncludeTargets() {
        // Arrange
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Target Match");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setStageNumber(13);
        stageDto.setMatch(matchDto);
        stageDto.setTargetPaper(99);
        stageDto.setTargetPopper(99);
        stageDto.setTargetPlates(99);

        // Act
        String result = stageDto.toString();

        // Assert
        assertEquals("13 for Target Match", result);
        assertFalse(result.contains("99"));
    }

    private static @NonNull IpscMatchStage getIpscMatchStage() {
        IpscMatch match = new IpscMatch();
        match.setId(10L);
        match.setName("Test Match");

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(42L);
        stage.setMatch(match);
        stage.setStageNumber(null);
        stage.setStageName(null);
        stage.setRangeNumber(null);
        stage.setTargetPaper(null);
        stage.setTargetPopper(null);
        stage.setTargetPlates(null);
        stage.setTargetDisappear(null);
        stage.setTargetPenalty(null);
        stage.setMinRounds(null);
        stage.setMaxPoints(null);
        return stage;
    }

    private static @NonNull MatchStageDto getMatchStageDto() {
        IpscMatchStage stage = buildIpscMatchStage(
                99L, "Championship Match", 5, "Stage 5 - Speed Shooting", 2,
                8, 4, 6, 2, 0, 20, 100
        );
        return new MatchStageDto(stage);
    }

    private static @NonNull MatchStageDto getStageDto() {
        IpscMatchStage stage = buildIpscMatchStage(
                30L, null, 7, "Precision Stage", 3,
                10, 5, 8, 3, 1, 25, 150
        );
        MatchDto matchDto = new MatchDto();
        matchDto.setName("Full Match");
        return new MatchStageDto(stage, matchDto);
    }

    private static @NonNull MatchStageDto getDto() {
        return buildInitDto(
                "Spring Championship", 12, "Stage 12 - Accuracy",
                6, 3, 4, 1, 0, 15, 75, null, null
        );
    }

    private static @NonNull MatchStageDto getMatchStageDto1() {
        return buildInitDto(
                "Complete Match", 60, "Stage 60 - Complete",
                12, 8, 10, 4, 2, 30, 200, null, null
        );
    }

    private static @NonNull MatchStageDto getMatchStageDto2() {
        return buildInitDto(
                "Zero Target Match", 70, "No Targets",
                0, 0, 0, 0, 0, 0, 0, null, null
        );
    }

    private static @NonNull MatchStageDto getMatchStageDto3() {
        return buildInitDto(
                "Max Values Match", 100, "Maximum Values",
                999, 999, 999, 999, 999, 999, 99999, 999, 99
        );
    }

    private static @NonNull MatchStageDto buildInitDto(
            String matchName,
            int stageId,
            String stageName,
            Integer targetPaper,
            Integer targetPopper,
            Integer targetPlates,
            Integer targetDisappear,
            Integer targetPenalty,
            Integer minRounds,
            Integer maxPoints,
            Integer initialIndex,
            Integer initialStageNumber
    ) {
        MatchStageDto dto = new MatchStageDto();
        dto.setIndex(initialIndex);
        dto.setStageNumber(initialStageNumber);

        MatchDto matchDto = new MatchDto();
        matchDto.setName(matchName);

        StageResponse stageResponse = new StageResponse();
        stageResponse.setStageId(stageId);
        stageResponse.setStageName(stageName);
        stageResponse.setTargetPaper(targetPaper);
        stageResponse.setTargetPopper(targetPopper);
        stageResponse.setTargetPlates(targetPlates);
        stageResponse.setTargetDisappear(targetDisappear);
        stageResponse.setTargetPenalty(targetPenalty);
        stageResponse.setMinRounds(minRounds);
        stageResponse.setMaxPoints(maxPoints);

        dto.init(matchDto, stageResponse);
        return dto;
    }

    private static @NonNull IpscMatchStage buildIpscMatchStage(
            Long stageId,
            String matchName,
            Integer stageNumber,
            String stageName,
            Integer rangeNumber,
            Integer targetPaper,
            Integer targetPopper,
            Integer targetPlates,
            Integer targetDisappear,
            Integer targetPenalty,
            Integer minRounds,
            Integer maxPoints
    ) {
        IpscMatch match = new IpscMatch();
        match.setId(10L);
        match.setName(matchName);

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(stageId);
        stage.setMatch(match);
        stage.setStageNumber(stageNumber);
        stage.setStageName(stageName);
        stage.setRangeNumber(rangeNumber);
        stage.setTargetPaper(targetPaper);
        stage.setTargetPopper(targetPopper);
        stage.setTargetPlates(targetPlates);
        stage.setTargetDisappear(targetDisappear);
        stage.setTargetPenalty(targetPenalty);
        stage.setMinRounds(minRounds);
        stage.setMaxPoints(maxPoints);
        return stage;
    }
}

