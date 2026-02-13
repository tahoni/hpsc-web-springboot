package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.ClubReference;
import za.co.hpsc.web.models.ipsc.domain.MatchEntityHolder;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MatchDomainServiceImpl}.
 *
 * <p>
 * Tests the {@code initMatchEntities} method and its related helper methods for initialising
 * match domain entities from DTOs. Covers various scenarios including valid inputs, null inputs,
 * empty collections, and entity lookups from repositories.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MatchDomainServiceImpl Tests")
public class MatchDomainServiceImplTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private IpscMatchStageRepository ipscMatchStageRepository;

    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;

    @Mock
    private MatchStageCompetitorRepository matchStageCompetitorRepository;

    @InjectMocks
    private MatchDomainServiceImpl matchDomainService;

    private MatchResultsDto matchResultsDto;
    private MatchDto matchDto;
    private Club clubEntity;
    private IpscMatch matchEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test data
        ClubDto clubDto = new ClubDto();
        clubDto.setId(1L);
        clubDto.setName("Test Club");
        clubDto.setAbbreviation("TC");

        clubEntity = new Club();
        clubEntity.setId(1L);
        clubEntity.setName("Test Club");
        clubEntity.setAbbreviation("TC");

        matchDto = new MatchDto();
        matchDto.setId(100L);
        matchDto.setName("Test Match");
        matchDto.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));
        matchDto.setClub(clubDto);

        matchEntity = new IpscMatch();
        matchEntity.setId(100L);
        matchEntity.setName("Test Match");
        matchEntity.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));
        matchEntity.setClub(clubEntity);

        matchResultsDto = new MatchResultsDto();
        matchResultsDto.setMatch(matchDto);
        matchResultsDto.setClub(clubDto);
    }

    @Test
    @DisplayName("initMatchEntities returns empty Optional when match results has null match")
    public void testInitMatchEntities_withNullMatch_returnsEmpty() {
        // Arrange
        MatchResultsDto nullMatchDto = new MatchResultsDto();
        nullMatchDto.setMatch(null);

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(nullMatchDto);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("initMatchEntities initializes match entities with valid data")
    public void testInitMatchEntities_withValidData_returnsMatchEntityHolder() {
        // Arrange
        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertNotNull(holder.getMatch());
        assertEquals(100L, holder.getMatch().getId());
        assertEquals("Test Match", holder.getMatch().getName());
        assertNotNull(holder.getClub());
        assertEquals(1L, holder.getClub().getId());
    }

    @Test
    @DisplayName("initMatchEntities creates match entity when not found in repository")
    public void testInitMatchEntities_matchNotInRepository_createsNewMatch() {
        // Arrange
        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.empty());

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertNotNull(holder.getMatch());
    }

    @Test
    @DisplayName("initMatchEntities initializes match with null club")
    public void testInitMatchEntities_withNullClub_returnsMatchHolder() {
        // Arrange
        matchResultsDto.setClub(null);
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertNotNull(holder.getMatch());
        assertNull(holder.getClub());
    }

    @Test
    @DisplayName("initMatchEntities initializes competitors")
    public void testInitMatchEntities_withCompetitors_initializesCompetitorEntities() {
        // Arrange
        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setId(1L);
        competitorDto1.setUuid(UUID.randomUUID());
        competitorDto1.setFirstName("John");
        competitorDto1.setLastName("Doe");
        competitorDto1.setCompetitorNumber("C001");

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setId(2L);
        competitorDto2.setUuid(UUID.randomUUID());
        competitorDto2.setFirstName("Jane");
        competitorDto2.setLastName("Smith");
        competitorDto2.setCompetitorNumber("C002");

        matchResultsDto.setCompetitors(Arrays.asList(competitorDto1, competitorDto2));

        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        competitor1.setFirstName("John");
        competitor1.setLastName("Doe");
        competitor1.setCompetitorNumber("C001");

        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);
        competitor2.setFirstName("Jane");
        competitor2.setLastName("Smith");
        competitor2.setCompetitorNumber("C002");

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor1));
        when(competitorRepository.findById(2L)).thenReturn(Optional.of(competitor2));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertNotNull(holder.getCompetitors());
        assertEquals(2, holder.getCompetitors().size());
    }

    @Test
    @DisplayName("initMatchEntities initializes match stages")
    public void testInitMatchEntities_withStages_initializesMatchStageEntities() {
        // Arrange
        MatchStageDto stageDto1 = new MatchStageDto();
        stageDto1.setId(10L);
        stageDto1.setUuid(UUID.randomUUID());
        stageDto1.setStageName("Stage 1");
        stageDto1.setStageNumber(1);

        MatchStageDto stageDto2 = new MatchStageDto();
        stageDto2.setId(11L);
        stageDto2.setUuid(UUID.randomUUID());
        stageDto2.setStageName("Stage 2");
        stageDto2.setStageNumber(2);

        matchResultsDto.setStages(Arrays.asList(stageDto1, stageDto2));

        IpscMatchStage stage1 = new IpscMatchStage();
        stage1.setId(10L);
        stage1.setStageName("Stage 1");
        stage1.setStageNumber(1);

        IpscMatchStage stage2 = new IpscMatchStage();
        stage2.setId(11L);
        stage2.setStageName("Stage 2");
        stage2.setStageNumber(2);

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));
        when(ipscMatchStageRepository.findById(10L)).thenReturn(Optional.of(stage1));
        when(ipscMatchStageRepository.findById(11L)).thenReturn(Optional.of(stage2));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertNotNull(holder.getMatchStages());
        assertEquals(2, holder.getMatchStages().size());
    }

    @Test
    @DisplayName("initMatchEntities initializes match competitors")
    public void testInitMatchEntities_withMatchCompetitors_initializesMatchCompetitors() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");
        competitorDto.setCompetitorNumber("C001");

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(UUID.randomUUID());
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClub(ClubReference.HPSC);

        matchResultsDto.setCompetitors(Collections.singletonList(competitorDto));
        matchResultsDto.setMatchCompetitors(Collections.singletonList(matchCompetitorDto));

        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setCompetitorNumber("C001");

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(20L);

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));
        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertNotNull(holder.getMatchCompetitors());
        assertEquals(1, holder.getMatchCompetitors().size());
    }

    @Test
    @DisplayName("initMatchEntities initializes match stage competitors")
    public void testInitMatchEntities_withMatchStageCompetitors_initializesMatchStageCompetitors() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        UUID stageUuid = UUID.randomUUID();

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");
        competitorDto.setCompetitorNumber("C001");

        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setId(10L);
        stageDto.setUuid(stageUuid);
        stageDto.setStageName("Stage 1");
        stageDto.setStageNumber(1);

        MatchStageCompetitorDto stageCompetitorDto = new MatchStageCompetitorDto();
        stageCompetitorDto.setId(30L);
        stageCompetitorDto.setUuid(UUID.randomUUID());
        stageCompetitorDto.setCompetitor(competitorDto);
        stageCompetitorDto.setMatchStage(stageDto);
        stageCompetitorDto.setClub(ClubReference.HPSC);

        matchResultsDto.setCompetitors(Collections.singletonList(competitorDto));
        matchResultsDto.setStages(Collections.singletonList(stageDto));
        matchResultsDto.setMatchStageCompetitors(Collections.singletonList(stageCompetitorDto));

        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setCompetitorNumber("C001");

        IpscMatchStage stage = new IpscMatchStage();
        stage.setId(10L);
        stage.setStageName("Stage 1");
        stage.setStageNumber(1);

        MatchStageCompetitor stageCompetitor = new MatchStageCompetitor();
        stageCompetitor.setId(30L);

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));
        when(ipscMatchStageRepository.findById(10L)).thenReturn(Optional.of(stage));
        when(matchStageCompetitorRepository.findById(30L)).thenReturn(Optional.of(stageCompetitor));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertNotNull(holder.getMatchStageCompetitors());
        assertEquals(1, holder.getMatchStageCompetitors().size());
    }

    @Test
    @DisplayName("initMatchEntities handles empty collections")
    public void testInitMatchEntities_withEmptyCollections_returnsMatchHolder() {
        // Arrange
        matchResultsDto.setCompetitors(new ArrayList<>());
        matchResultsDto.setStages(new ArrayList<>());
        matchResultsDto.setMatchCompetitors(new ArrayList<>());
        matchResultsDto.setMatchStageCompetitors(new ArrayList<>());

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertNotNull(holder.getMatch());
        assertEquals(0, holder.getCompetitors().size());
        assertEquals(0, holder.getMatchStages().size());
        assertEquals(0, holder.getMatchCompetitors().size());
        assertEquals(0, holder.getMatchStageCompetitors().size());
    }

    @Test
    @DisplayName("initMatchEntities handles null collections")
    public void testInitMatchEntities_withNullCollections_returnsMatchHolder() {
        // Arrange
        matchResultsDto.setCompetitors(null);
        matchResultsDto.setStages(null);
        matchResultsDto.setMatchCompetitors(null);
        matchResultsDto.setMatchStageCompetitors(null);

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertNotNull(holder.getMatch());
        assertEquals(0, holder.getCompetitors().size());
        assertEquals(0, holder.getMatchStages().size());
        assertEquals(0, holder.getMatchCompetitors().size());
        assertEquals(0, holder.getMatchStageCompetitors().size());
    }

    @Test
    @DisplayName("initMatchEntities filters match competitors by club reference")
    public void testInitMatchEntities_matchCompetitorFiltering_filtersByClubReference() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");
        competitorDto.setCompetitorNumber("C001");

        MatchCompetitorDto hpscCompetitorDto = new MatchCompetitorDto();
        hpscCompetitorDto.setId(20L);
        hpscCompetitorDto.setUuid(UUID.randomUUID());
        hpscCompetitorDto.setCompetitor(competitorDto);
        hpscCompetitorDto.setClub(ClubReference.HPSC);

        MatchCompetitorDto soscCompetitorDto = new MatchCompetitorDto();
        soscCompetitorDto.setId(21L);
        soscCompetitorDto.setUuid(UUID.randomUUID());
        soscCompetitorDto.setCompetitor(competitorDto);
        soscCompetitorDto.setClub(ClubReference.SOSC); // Different club

        matchResultsDto.setCompetitors(Collections.singletonList(competitorDto));
        matchResultsDto.setMatchCompetitors(Arrays.asList(hpscCompetitorDto, soscCompetitorDto));

        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setCompetitorNumber("C001");

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(20L);

        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(21L);

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));
        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor1));
        when(matchCompetitorRepository.findById(21L)).thenReturn(Optional.of(matchCompetitor2));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        // Should only include HPSC competitors
        assertEquals(1, holder.getMatchCompetitors().size());
    }

    @Test
    @DisplayName("initMatchEntities links entities correctly")
    public void testInitMatchEntities_linksEntitiesCorrectly() {
        // Arrange
        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertEquals(clubEntity, holder.getClub());
        assertEquals(matchEntity, holder.getMatch());
        assertEquals(clubEntity.getId(), holder.getMatch().getClub().getId());
    }

    @Test
    @DisplayName("initMatchEntities creates new competitor when not found in repository")
    public void testInitMatchEntities_competitorNotInRepository_createsNewCompetitor() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");
        competitorDto.setCompetitorNumber("C001");

        matchResultsDto.setCompetitors(Collections.singletonList(competitorDto));

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));
        when(competitorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertEquals(1, holder.getCompetitors().size());
    }

    @Test
    @DisplayName("initMatchEntities creates new match stage when not found in repository")
    public void testInitMatchEntities_stageNotInRepository_createsNewStage() {
        // Arrange
        MatchStageDto stageDto = new MatchStageDto();
        stageDto.setId(10L);
        stageDto.setUuid(UUID.randomUUID());
        stageDto.setStageName("Stage 1");
        stageDto.setStageNumber(1);

        matchResultsDto.setStages(Collections.singletonList(stageDto));

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));
        when(ipscMatchStageRepository.findById(10L)).thenReturn(Optional.empty());

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertEquals(1, holder.getMatchStages().size());
    }

    @Test
    @DisplayName("initMatchEntities handles multiple entities of same type")
    public void testInitMatchEntities_withMultipleEntitiesOfSameType_initializesAll() {
        // Arrange
        List<CompetitorDto> competitors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CompetitorDto competitorDto = new CompetitorDto();
            competitorDto.setId((long) i);
            competitorDto.setUuid(UUID.randomUUID());
            competitorDto.setFirstName("John");
            competitorDto.setLastName("Competitor " + i);
            competitorDto.setCompetitorNumber("C00" + i);
            competitors.add(competitorDto);
        }

        matchResultsDto.setCompetitors(competitors);

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));
        when(competitorRepository.findById(anyLong())).thenReturn(Optional.of(new Competitor()));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertEquals(5, holder.getCompetitors().size());
    }

    @Test
    @DisplayName("initMatchEntities processes match without club")
    public void testInitMatchEntities_matchWithoutClub_processesSuccessfully() {
        // Arrange
        matchDto.setClub(null);
        matchResultsDto.setClub(null);

        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<MatchEntityHolder> result = matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        assertTrue(result.isPresent());
        MatchEntityHolder holder = result.get();
        assertNotNull(holder.getMatch());
        assertNull(holder.getClub());
    }

    @Test
    @DisplayName("initMatchEntities verifies repository calls with correct parameters")
    public void testInitMatchEntities_verifiesRepositoryCalls() {
        // Arrange
        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        matchDomainService.initMatchEntities(matchResultsDto);

        // Assert
        verify(clubRepository, times(1)).findById(1L);
        verify(ipscMatchRepository, times(1)).findById(100L);
    }
}

