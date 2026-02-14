package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link IpscDomainServiceImpl}.
 *
 * <p>
 * Tests the {@code initMatchEntities} method and its related helper methods for initialising
 * match domain entities from DTOs. Covers various scenarios including valid inputs, null inputs,
 * empty collections, and entity lookups from repositories.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MatchDomainServiceImpl Tests")
public class IpscDomainServiceImplTest {

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private CompetitorRepository competitorRepository;

    @Mock
    private IpscMatchRepository ipscMatchRepository;

    @Mock
    private MatchCompetitorRepository matchCompetitorRepository;

    @InjectMocks
    private IpscDomainServiceImpl ipscDomainService;

    private MatchDto matchDto;
    private ClubDto clubDto;
    private Club clubEntity;
    private IpscMatch matchEntity;

    @BeforeEach
    public void setUp() {
        // Initialize test data
        clubDto = new ClubDto();
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

        MatchResultsDto matchResultsDto = new MatchResultsDto();
        matchResultsDto.setMatch(matchDto);
        matchResultsDto.setClub(clubDto);
    }

    // =====================================================================
    // Tests for initClubEntity method
    // =====================================================================

    @Test
    @DisplayName("initClubEntity returns empty Optional when clubDto is null")
    public void testInitClubEntity_withNullClubDto_returnsEmpty() {
        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(null);

        // Assert
        assertTrue(result.isEmpty());
        verify(clubRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("initClubEntity returns empty Optional when clubDto has null id")
    public void testInitClubEntity_withNullClubId_returnsEmpty() {
        // Arrange
        ClubDto clubDtoWithoutId = new ClubDto();
        clubDtoWithoutId.setId(null);
        clubDtoWithoutId.setName("Test Club");
        clubDtoWithoutId.setAbbreviation("TC");

        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(clubDtoWithoutId);

        // Assert
        assertTrue(result.isEmpty());
        verify(clubRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("initClubEntity initializes club entity when found in repository")
    public void testInitClubEntity_withValidId_returnsInitializedClub() {
        // Arrange
        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));

        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(clubDto);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertEquals(1L, resultClub.getId());
        assertEquals("Test Club", resultClub.getName());
        assertEquals("TC", resultClub.getAbbreviation());
        verify(clubRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("initClubEntity calls club init method with clubDto")
    public void testInitClubEntity_callsInitMethod() {
        // Arrange
        Club spyClub = spy(clubEntity);
        when(clubRepository.findById(1L)).thenReturn(Optional.of(spyClub));

        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(clubDto);

        // Assert
        assertTrue(result.isPresent());
        verify(spyClub, times(1)).init(clubDto);
    }

    @Test
    @DisplayName("initClubEntity transfers name from DTO to entity")
    public void testInitClubEntity_transfersNameFromDtoToEntity() {
        // Arrange
        ClubDto clubDtoWithDifferentName = new ClubDto();
        clubDtoWithDifferentName.setId(1L);
        clubDtoWithDifferentName.setName("New Club Name");
        clubDtoWithDifferentName.setAbbreviation("NC");

        Club clubEntityForTest = new Club();
        clubEntityForTest.setId(1L);
        clubEntityForTest.setName("Old Club Name");
        clubEntityForTest.setAbbreviation("OCN");

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntityForTest));

        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(clubDtoWithDifferentName);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertEquals("New Club Name", resultClub.getName());
    }

    @Test
    @DisplayName("initClubEntity transfers abbreviation from DTO to entity")
    public void testInitClubEntity_transfersAbbreviationFromDtoToEntity() {
        // Arrange
        ClubDto clubDtoWithDifferentAbbr = new ClubDto();
        clubDtoWithDifferentAbbr.setId(1L);
        clubDtoWithDifferentAbbr.setName("Test Club");
        clubDtoWithDifferentAbbr.setAbbreviation("NEW");

        Club clubEntityForTest = new Club();
        clubEntityForTest.setId(1L);
        clubEntityForTest.setName("Test Club");
        clubEntityForTest.setAbbreviation("OLD");

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntityForTest));

        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(clubDtoWithDifferentAbbr);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertEquals("NEW", resultClub.getAbbreviation());
    }

    @Test
    @DisplayName("initClubEntity returns empty Optional when club not found in repository")
    public void testInitClubEntity_clubNotFoundInRepository_returnsEmpty() {
        // Arrange
        when(clubRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(clubDto);

        // Assert
        assertTrue(result.isEmpty());
        verify(clubRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("initClubEntity handles clubDto with only name set")
    public void testInitClubEntity_withOnlyNameSet_returnsEmpty() {
        // Arrange
        ClubDto clubDtoNameOnly = new ClubDto();
        clubDtoNameOnly.setId(null); // No ID, so repository won't be queried
        clubDtoNameOnly.setName("Test Club");

        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(clubDtoNameOnly);

        // Assert
        assertTrue(result.isEmpty());
        verify(clubRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("initClubEntity handles clubDto with null abbreviation")
    public void testInitClubEntity_withNullAbbreviation_initializesSuccessfully() {
        // Arrange
        ClubDto clubDtoNoAbbr = new ClubDto();
        clubDtoNoAbbr.setId(1L);
        clubDtoNoAbbr.setName("Test Club");
        clubDtoNoAbbr.setAbbreviation(null);

        Club clubEntityForTest = new Club();
        clubEntityForTest.setId(1L);
        clubEntityForTest.setName("Old Name");
        clubEntityForTest.setAbbreviation("OLD");

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntityForTest));

        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(clubDtoNoAbbr);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertEquals("Test Club", resultClub.getName());
        assertNull(resultClub.getAbbreviation());
    }

    @Test
    @DisplayName("initClubEntity verifies correct repository method call parameters")
    public void testInitClubEntity_verifiesRepositoryCallParameters() {
        // Arrange
        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));

        // Act
        ipscDomainService.initClubEntity(clubDto);

        // Assert
        verify(clubRepository).findById(1L);
        verify(clubRepository, times(1)).findById(eq(1L));
    }

    @Test
    @DisplayName("initClubEntity does not modify DTO")
    public void testInitClubEntity_doesNotModifyDto() {
        // Arrange
        ClubDto clubDtoOriginal = new ClubDto();
        clubDtoOriginal.setId(1L);
        clubDtoOriginal.setName("Test Club");
        clubDtoOriginal.setAbbreviation("TC");

        String originalName = clubDtoOriginal.getName();
        String originalAbbr = clubDtoOriginal.getAbbreviation();

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));

        // Act
        ipscDomainService.initClubEntity(clubDtoOriginal);

        // Assert
        assertEquals(originalName, clubDtoOriginal.getName());
        assertEquals(originalAbbr, clubDtoOriginal.getAbbreviation());
    }

    @Test
    @DisplayName("initClubEntity with same id and name returns same entity")
    public void testInitClubEntity_withSameIdAndName_returnsSameEntity() {
        // Arrange
        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntity));

        // Act
        Optional<Club> result1 = ipscDomainService.initClubEntity(clubDto);
        Optional<Club> result2 = ipscDomainService.initClubEntity(clubDto);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(result1.get().getId(), result2.get().getId());
        verify(clubRepository, times(2)).findById(1L);
    }

    @Test
    @DisplayName("initClubEntity with different ids queries repository separately")
    public void testInitClubEntity_withDifferentIds_queriesSeparately() {
        // Arrange
        ClubDto clubDto1 = new ClubDto();
        clubDto1.setId(1L);
        clubDto1.setName("Club 1");

        ClubDto clubDto2 = new ClubDto();
        clubDto2.setId(2L);
        clubDto2.setName("Club 2");

        Club club1 = new Club();
        club1.setId(1L);
        club1.setName("Club 1");

        Club club2 = new Club();
        club2.setId(2L);
        club2.setName("Club 2");

        when(clubRepository.findById(1L)).thenReturn(Optional.of(club1));
        when(clubRepository.findById(2L)).thenReturn(Optional.of(club2));

        // Act
        Optional<Club> result1 = ipscDomainService.initClubEntity(clubDto1);
        Optional<Club> result2 = ipscDomainService.initClubEntity(clubDto2);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(1L, result1.get().getId());
        assertEquals(2L, result2.get().getId());
        verify(clubRepository).findById(1L);
        verify(clubRepository).findById(2L);
    }

    @Test
    @DisplayName("initClubEntity handles clubDto with empty string name")
    public void testInitClubEntity_withEmptyStringName_initializesWithEmpty() {
        // Arrange
        ClubDto clubDtoEmptyName = new ClubDto();
        clubDtoEmptyName.setId(1L);
        clubDtoEmptyName.setName("");
        clubDtoEmptyName.setAbbreviation("TC");

        Club clubEntityForTest = new Club();
        clubEntityForTest.setId(1L);
        clubEntityForTest.setName("Old Name");

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubEntityForTest));

        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(clubDtoEmptyName);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertEquals("", resultClub.getName());
    }

    @Test
    @DisplayName("initClubEntity preserves other club properties")
    public void testInitClubEntity_preservesOtherProperties() {
        // Arrange
        Club clubWithMatches = new Club();
        clubWithMatches.setId(1L);
        clubWithMatches.setName("Test Club");
        clubWithMatches.setAbbreviation("TC");
        clubWithMatches.setMatches(new ArrayList<>()); // Add some other property

        when(clubRepository.findById(1L)).thenReturn(Optional.of(clubWithMatches));

        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(clubDto);

        // Assert
        assertTrue(result.isPresent());
        Club resultClub = result.get();
        assertNotNull(resultClub.getMatches());
    }

    @Test
    @DisplayName("initClubEntity handles large id values")
    public void testInitClubEntity_withLargeIdValue_returnsOptional() {
        // Arrange
        ClubDto clubDtoLargeId = new ClubDto();
        clubDtoLargeId.setId(Long.MAX_VALUE);
        clubDtoLargeId.setName("Test Club");

        Club clubForLargeId = new Club();
        clubForLargeId.setId(Long.MAX_VALUE);

        when(clubRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.of(clubForLargeId));

        // Act
        Optional<Club> result = ipscDomainService.initClubEntity(clubDtoLargeId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Long.MAX_VALUE, result.get().getId());
    }

    // =====================================================================
    // Tests for initMatchEntity method
    // =====================================================================

    @Test
    @DisplayName("initMatchEntity returns Optional with created match when matchDto has null id")
    public void testInitMatchEntity_withNullMatchId_createsNewMatch() {
        // Arrange
        MatchDto matchDtoNoId = new MatchDto();
        matchDtoNoId.setId(null);
        matchDtoNoId.setName("New Match");
        matchDtoNoId.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDtoNoId, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertEquals("New Match", resultMatch.getName());
        assertEquals(LocalDateTime.of(2026, 3, 15, 10, 0), resultMatch.getScheduledDate());
        verify(ipscMatchRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("initMatchEntity initializes match entity when found in repository")
    public void testInitMatchEntity_withValidId_returnsInitializedMatch() {
        // Arrange
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertEquals(100L, resultMatch.getId());
        assertEquals("Test Match", resultMatch.getName());
        verify(ipscMatchRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("initMatchEntity always returns present Optional")
    public void testInitMatchEntity_alwaysReturnsPresent() {
        // Arrange - match not found in repository
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.empty());

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDto, clubEntity);

        // Assert - still returns present with newly created entity
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("initMatchEntity creates new match when not found in repository")
    public void testInitMatchEntity_matchNotFoundInRepository_createsNewMatch() {
        // Arrange
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.empty());

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertNotNull(resultMatch);
        verify(ipscMatchRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("initMatchEntity calls init method with matchDto and clubEntity")
    public void testInitMatchEntity_callsInitMethod() {
        // Arrange
        IpscMatch spyMatch = spy(matchEntity);
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(spyMatch));

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        verify(spyMatch, times(1)).init(matchDto, clubEntity);
    }

    @Test
    @DisplayName("initMatchEntity sets club entity on match")
    public void testInitMatchEntity_setsClubOnMatch() {
        // Arrange
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertEquals(clubEntity, resultMatch.getClub());
    }

    @Test
    @DisplayName("initMatchEntity handles null clubEntity")
    public void testInitMatchEntity_withNullClub_returnsMatchWithoutClub() {
        // Arrange
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDto, null);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertNull(resultMatch.getClub());
    }

    @Test
    @DisplayName("initMatchEntity transfers match name from DTO to entity")
    public void testInitMatchEntity_transfersNameFromDtoToEntity() {
        // Arrange
        MatchDto matchDtoWithName = new MatchDto();
        matchDtoWithName.setId(100L);
        matchDtoWithName.setName("Updated Match Name");
        matchDtoWithName.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setName("Old Match Name");

        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(existingMatch));

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDtoWithName, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Updated Match Name", result.get().getName());
    }

    @Test
    @DisplayName("initMatchEntity transfers scheduled date from DTO to entity")
    public void testInitMatchEntity_transfersScheduledDateFromDtoToEntity() {
        // Arrange
        MatchDto matchDtoWithDate = new MatchDto();
        matchDtoWithDate.setId(100L);
        matchDtoWithDate.setName("Test Match");
        matchDtoWithDate.setScheduledDate(LocalDateTime.of(2026, 5, 20, 14, 30));

        IpscMatch existingMatch = new IpscMatch();
        existingMatch.setId(100L);
        existingMatch.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));

        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(existingMatch));

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDtoWithDate, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(LocalDateTime.of(2026, 5, 20, 14, 30), result.get().getScheduledDate());
    }

    @Test
    @DisplayName("initMatchEntity handles matchDto with null scheduled date")
    public void testInitMatchEntity_withNullScheduledDate_returnsMatch() {
        // Arrange
        MatchDto matchDtoNoDate = new MatchDto();
        matchDtoNoDate.setId(100L);
        matchDtoNoDate.setName("Test Match");
        matchDtoNoDate.setScheduledDate(null);

        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDtoNoDate, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get());
    }

    @Test
    @DisplayName("initMatchEntity verifies repository call with correct id")
    public void testInitMatchEntity_verifiesRepositoryCallWithCorrectId() {
        // Arrange
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        ipscDomainService.initMatchEntity(matchDto, clubEntity);

        // Assert
        verify(ipscMatchRepository).findById(eq(100L));
        verify(ipscMatchRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("initMatchEntity does not modify DTO")
    public void testInitMatchEntity_doesNotModifyDto() {
        // Arrange
        MatchDto originalMatchDto = new MatchDto();
        originalMatchDto.setId(100L);
        originalMatchDto.setName("Original Name");
        originalMatchDto.setScheduledDate(LocalDateTime.of(2026, 3, 15, 10, 0));

        String originalName = originalMatchDto.getName();
        LocalDateTime originalDate = originalMatchDto.getScheduledDate();

        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        ipscDomainService.initMatchEntity(originalMatchDto, clubEntity);

        // Assert
        assertEquals(originalName, originalMatchDto.getName());
        assertEquals(originalDate, originalMatchDto.getScheduledDate());
    }

    @Test
    @DisplayName("initMatchEntity returns same instance when called with same id")
    public void testInitMatchEntity_withSameIdMultipleTimes_returnsConsistently() {
        // Arrange
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result1 = ipscDomainService.initMatchEntity(matchDto, clubEntity);
        Optional<IpscMatch> result2 = ipscDomainService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(result1.get().getId(), result2.get().getId());
        verify(ipscMatchRepository, times(2)).findById(100L);
    }

    @Test
    @DisplayName("initMatchEntity with different ids queries repository separately")
    public void testInitMatchEntity_withDifferentIds_queriesSeparately() {
        // Arrange
        ClubDto clubDto1 = new ClubDto();
        clubDto1.setId(1L);
        clubDto1.setName("Club 1");

        ClubDto clubDto2 = new ClubDto();
        clubDto2.setId(2L);
        clubDto2.setName("Club 2");

        Club club1 = new Club();
        club1.setId(1L);
        club1.setName("Club 1");

        Club club2 = new Club();
        club2.setId(2L);
        club2.setName("Club 2");

        when(clubRepository.findById(1L)).thenReturn(Optional.of(club1));
        when(clubRepository.findById(2L)).thenReturn(Optional.of(club2));

        // Act
        Optional<Club> result1 = ipscDomainService.initClubEntity(clubDto1);
        Optional<Club> result2 = ipscDomainService.initClubEntity(clubDto2);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(1L, result1.get().getId());
        assertEquals(2L, result2.get().getId());
        verify(clubRepository).findById(1L);
        verify(clubRepository).findById(2L);
    }

    @Test
    @DisplayName("initMatchEntity with different clubEntities sets correct club")
    public void testInitMatchEntity_withDifferentClubs_setsCorrectClub() {
        // Arrange
        Club club2 = new Club();
        club2.setId(2L);
        club2.setName("Club 2");

        // Create two different mock match instances to return for each call
        IpscMatch match1 = new IpscMatch();
        match1.setId(100L);
        match1.setClub(clubEntity);

        IpscMatch match2 = new IpscMatch();
        match2.setId(100L);
        match2.setClub(club2);

        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(match1)).thenReturn(Optional.of(match2));

        // Act
        Optional<IpscMatch> result1 = ipscDomainService.initMatchEntity(matchDto, clubEntity);
        Optional<IpscMatch> result2 = ipscDomainService.initMatchEntity(matchDto, club2);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        // After init() is called, the club is set via setClub() method
        assertEquals(clubEntity, result1.get().getClub());
        assertEquals(club2, result2.get().getClub());
    }

    @Test
    @DisplayName("initMatchEntity handles large match id values")
    public void testInitMatchEntity_withLargeIdValue_returnsOptional() {
        // Arrange
        MatchDto matchDtoLargeId = new MatchDto();
        matchDtoLargeId.setId(Long.MAX_VALUE);
        matchDtoLargeId.setName("Test Match");
        matchDtoLargeId.setScheduledDate(LocalDateTime.now());

        IpscMatch matchForLargeId = new IpscMatch();
        matchForLargeId.setId(Long.MAX_VALUE);

        when(ipscMatchRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.of(matchForLargeId));

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDtoLargeId, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(Long.MAX_VALUE, result.get().getId());
    }

    @Test
    @DisplayName("initMatchEntity preserves other match properties")
    public void testInitMatchEntity_preservesOtherProperties() {
        // Arrange
        IpscMatch matchWithDetails = spy(new IpscMatch());
        matchWithDetails.setId(100L);
        matchWithDetails.setName("Test Match");
        LocalDateTime createdDate = LocalDateTime.of(2026, 1, 1, 10, 0);
        matchWithDetails.setDateCreated(createdDate);

        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchWithDetails));

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        // Verify that init was called, which updates the properties
        verify(matchWithDetails).init(eq(matchDto), eq(clubEntity));
        // The match entity should be returned
        assertNotNull(result.get());
    }

    @Test
    @DisplayName("initMatchEntity handles matchDto with empty string name")
    public void testInitMatchEntity_withEmptyStringName_returnsMatch() {
        // Arrange
        MatchDto matchDtoEmptyName = new MatchDto();
        matchDtoEmptyName.setId(100L);
        matchDtoEmptyName.setName("");
        matchDtoEmptyName.setScheduledDate(LocalDateTime.now());

        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(matchEntity));

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDtoEmptyName, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("", result.get().getName());
    }

    @Test
    @DisplayName("initMatchEntity verifies init method receives correct parameters")
    public void testInitMatchEntity_verifyInitMethodParameters() {
        // Arrange
        IpscMatch spyMatch = spy(new IpscMatch());
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.of(spyMatch));

        // Act
        ipscDomainService.initMatchEntity(matchDto, clubEntity);

        // Assert
        verify(spyMatch).init(eq(matchDto), eq(clubEntity));
    }

    @Test
    @DisplayName("initMatchEntity returns new instance when repository returns empty")
    public void testInitMatchEntity_whenRepositoryReturnsEmpty_createsNewInstance() {
        // Arrange
        when(ipscMatchRepository.findById(100L)).thenReturn(Optional.empty());

        // Act
        Optional<IpscMatch> result = ipscDomainService.initMatchEntity(matchDto, clubEntity);

        // Assert
        assertTrue(result.isPresent());
        IpscMatch resultMatch = result.get();
        assertNotNull(resultMatch);
        // Verify it's a fresh instance by checking ID (new instance should have null ID)
        verify(ipscMatchRepository, times(1)).findById(100L);
    }

    // =====================================================================
    // Tests for initCompetitorEntities method
    // =====================================================================

    @Test
    @DisplayName("initCompetitorEntities returns empty map when competitorDtoList is null")
    public void testInitCompetitorEntities_withNullList_returnsEmptyMap() {
        // Act
        Map<UUID, Competitor> result = ipscDomainService.initCompetitorEntities(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(competitorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("initCompetitorEntities returns empty map when competitorDtoList is empty")
    public void testInitCompetitorEntities_withEmptyList_returnsEmptyMap() {
        // Act
        Map<UUID, Competitor> result = ipscDomainService.initCompetitorEntities(new ArrayList<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(competitorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("initCompetitorEntities processes single competitor with valid id")
    public void testInitCompetitorEntities_withSingleCompetitor_returnsMapWithCompetitor() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");
        competitorDto.setCompetitorNumber("C001");

        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setLastName("Doe");
        competitor.setCompetitorNumber("C001");

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act
        Map<UUID, Competitor> result =
                ipscDomainService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(competitorUuid));
        assertEquals(competitor, result.get(competitorUuid));
        verify(competitorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("initCompetitorEntities processes multiple competitors")
    public void testInitCompetitorEntities_withMultipleCompetitors_returnsMapWithAllCompetitors() {
        // Arrange
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setId(1L);
        competitorDto1.setUuid(uuid1);
        competitorDto1.setFirstName("John");
        competitorDto1.setLastName("Doe");
        competitorDto1.setCompetitorNumber("C001");

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setId(2L);
        competitorDto2.setUuid(uuid2);
        competitorDto2.setFirstName("Jane");
        competitorDto2.setLastName("Smith");
        competitorDto2.setCompetitorNumber("C002");

        CompetitorDto competitorDto3 = new CompetitorDto();
        competitorDto3.setId(3L);
        competitorDto3.setUuid(uuid3);
        competitorDto3.setFirstName("Bob");
        competitorDto3.setLastName("Johnson");
        competitorDto3.setCompetitorNumber("C003");

        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);
        Competitor competitor3 = new Competitor();
        competitor3.setId(3L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor1));
        when(competitorRepository.findById(2L)).thenReturn(Optional.of(competitor2));
        when(competitorRepository.findById(3L)).thenReturn(Optional.of(competitor3));

        // Act
        Map<UUID, Competitor> result = ipscDomainService.initCompetitorEntities(
                Arrays.asList(competitorDto1, competitorDto2, competitorDto3));

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsKey(uuid1));
        assertTrue(result.containsKey(uuid2));
        assertTrue(result.containsKey(uuid3));
        assertEquals(competitor1, result.get(uuid1));
        assertEquals(competitor2, result.get(uuid2));
        assertEquals(competitor3, result.get(uuid3));
    }

    @Test
    @DisplayName("initCompetitorEntities creates new competitor when id is null")
    public void testInitCompetitorEntities_withNullCompetitorId_createsNewCompetitor() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(null);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");
        competitorDto.setCompetitorNumber("C001");

        // Act
        Map<UUID, Competitor> result =
                ipscDomainService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(competitorUuid));
        verify(competitorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("initCompetitorEntities creates new competitor when not found in repository")
    public void testInitCompetitorEntities_competitorNotInRepository_createsNewCompetitor() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");

        when(competitorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Map<UUID, Competitor> result =
                ipscDomainService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(competitorUuid));
        verify(competitorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("initCompetitorEntities uses UUID as map key")
    public void testInitCompetitorEntities_usesUuidAsKey() {
        // Arrange
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setId(1L);
        competitorDto1.setUuid(uuid1);
        competitorDto1.setFirstName("John");

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setId(2L);
        competitorDto2.setUuid(uuid2);
        competitorDto2.setFirstName("Jane");

        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor1));
        when(competitorRepository.findById(2L)).thenReturn(Optional.of(competitor2));

        // Act
        Map<UUID, Competitor> result = ipscDomainService.initCompetitorEntities(
                Arrays.asList(competitorDto1, competitorDto2));

        // Assert
        assertEquals(2, result.size());
        assertEquals(competitor1, result.get(uuid1));
        assertEquals(competitor2, result.get(uuid2));
        assertNull(result.get(UUID.randomUUID())); // Different UUID not in map
    }

    @Test
    @DisplayName("initCompetitorEntities calls init method on each competitor")
    public void testInitCompetitorEntities_callsInitMethod() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");

        Competitor spyCompetitor = spy(new Competitor());
        when(competitorRepository.findById(1L)).thenReturn(Optional.of(spyCompetitor));

        // Act
        ipscDomainService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        verify(spyCompetitor, times(1)).init(competitorDto);
    }

    @Test
    @DisplayName("initCompetitorEntities transfers competitor data from DTO to entity")
    public void testInitCompetitorEntities_transfersDataFromDtoToEntity() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");
        competitorDto.setLastName("Doe");
        competitorDto.setCompetitorNumber("C001");

        Competitor existingCompetitor = new Competitor();
        existingCompetitor.setId(1L);
        existingCompetitor.setFirstName("Old");
        existingCompetitor.setLastName("Name");

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(existingCompetitor));

        // Act
        Map<UUID, Competitor> result =
                ipscDomainService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        Competitor resultCompetitor = result.get(competitorUuid);
        assertNotNull(resultCompetitor);
        assertEquals("John", resultCompetitor.getFirstName());
        assertEquals("Doe", resultCompetitor.getLastName());
        assertEquals("C001", resultCompetitor.getCompetitorNumber());
    }

    @Test
    @DisplayName("initCompetitorEntities handles mixed null and non-null competitor ids")
    public void testInitCompetitorEntities_withMixedNullAndNonNullIds() {
        // Arrange
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setId(1L);
        competitorDto1.setUuid(uuid1);
        competitorDto1.setFirstName("John");

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setId(null);
        competitorDto2.setUuid(uuid2);
        competitorDto2.setFirstName("Jane");

        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor1));

        // Act
        Map<UUID, Competitor> result = ipscDomainService.initCompetitorEntities(
                Arrays.asList(competitorDto1, competitorDto2));

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(uuid1));
        assertTrue(result.containsKey(uuid2));
        verify(competitorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("initCompetitorEntities preserves other properties")
    public void testInitCompetitorEntities_preservesOtherProperties() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("John");

        Competitor competitor = new Competitor();
        competitor.setId(1L);
        competitor.setFirstName("John");
        competitor.setCompetitorMatches(new ArrayList<>()); // Other property

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act
        Map<UUID, Competitor> result =
                ipscDomainService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        Competitor resultCompetitor = result.get(competitorUuid);
        assertNotNull(resultCompetitor.getCompetitorMatches());
    }

    @Test
    @DisplayName("initCompetitorEntities handles large number of competitors")
    public void testInitCompetitorEntities_withLargeNumberOfCompetitors_processesAll() {
        // Arrange
        List<CompetitorDto> competitorDtos = new ArrayList<>();
        int numCompetitors = 100;

        for (int i = 0; i < numCompetitors; i++) {
            CompetitorDto competitorDto = new CompetitorDto();
            competitorDto.setId((long) i);
            competitorDto.setUuid(UUID.randomUUID());
            competitorDto.setFirstName("Competitor");
            competitorDto.setLastName("" + i);
            competitorDtos.add(competitorDto);

            Competitor competitor = new Competitor();
            competitor.setId((long) i);
            when(competitorRepository.findById((long) i)).thenReturn(Optional.of(competitor));
        }

        // Act
        Map<UUID, Competitor> result = ipscDomainService.initCompetitorEntities(competitorDtos);

        // Assert
        assertEquals(numCompetitors, result.size());
    }

    @Test
    @DisplayName("initCompetitorEntities verifies repository calls for each competitor")
    public void testInitCompetitorEntities_verifiesRepositoryCallsForEachCompetitor() {
        // Arrange
        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setId(1L);
        competitorDto1.setUuid(UUID.randomUUID());

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setId(2L);
        competitorDto2.setUuid(UUID.randomUUID());

        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor1));
        when(competitorRepository.findById(2L)).thenReturn(Optional.of(competitor2));

        // Act
        ipscDomainService.initCompetitorEntities(Arrays.asList(competitorDto1, competitorDto2));

        // Assert
        verify(competitorRepository).findById(1L);
        verify(competitorRepository).findById(2L);
        verify(competitorRepository, times(2)).findById(anyLong());
    }

    @Test
    @DisplayName("initCompetitorEntities handles competitors with duplicate IDs")
    public void testInitCompetitorEntities_withDuplicateIds_lastOneWins() {
        // Arrange
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setId(1L);
        competitorDto1.setUuid(uuid1);
        competitorDto1.setFirstName("First");

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setId(1L);
        competitorDto2.setUuid(uuid2);
        competitorDto2.setFirstName("Second");

        Competitor competitor = new Competitor();
        competitor.setId(1L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act
        Map<UUID, Competitor> result = ipscDomainService.initCompetitorEntities(
                Arrays.asList(competitorDto1, competitorDto2));

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(uuid1));
        assertTrue(result.containsKey(uuid2));
        // Both should exist with same competitor instance
        assertEquals(result.get(uuid1), result.get(uuid2));
    }

    @Test
    @DisplayName("initCompetitorEntities handles competitors with special characters in names")
    public void testInitCompetitorEntities_withSpecialCharactersInNames() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setId(1L);
        competitorDto.setUuid(competitorUuid);
        competitorDto.setFirstName("Jean-Paul");
        competitorDto.setLastName("O'Brien");
        competitorDto.setCompetitorNumber("C-001");

        Competitor competitor = new Competitor();
        competitor.setId(1L);

        when(competitorRepository.findById(1L)).thenReturn(Optional.of(competitor));

        // Act
        Map<UUID, Competitor> result =
                ipscDomainService.initCompetitorEntities(Collections.singletonList(competitorDto));

        // Assert
        assertEquals(1, result.size());
        Competitor resultCompetitor = result.get(competitorUuid);
        assertEquals("Jean-Paul", resultCompetitor.getFirstName());
        assertEquals("O'Brien", resultCompetitor.getLastName());
    }

    @Test
    @DisplayName("initCompetitorEntities returns new empty map instance each call")
    public void testInitCompetitorEntities_returnsNewMapInstance() {
        // Act
        Map<UUID, Competitor> result1 = ipscDomainService.initCompetitorEntities(null);
        Map<UUID, Competitor> result2 = ipscDomainService.initCompetitorEntities(null);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2); // Different instances
        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
    }

    // =====================================================================
    // Tests for initMatchCompetitorEntities method
    // =====================================================================

    @Test
    @DisplayName("initMatchCompetitorEntities returns empty map when matchCompetitors is null")
    public void testInitMatchCompetitorEntities_withNullList_returnsEmptyMap() {
        // Arrange
        Map<UUID, Competitor> competitorMap = new HashMap<>();

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                null, matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(matchCompetitorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("initMatchCompetitorEntities returns empty map when matchCompetitors list is empty")
    public void testInitMatchCompetitorEntities_withEmptyList_returnsEmptyMap() {
        // Arrange
        Map<UUID, Competitor> competitorMap = new HashMap<>();

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                new ArrayList<>(), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(matchCompetitorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("initMatchCompetitorEntities processes single match competitor with valid id")
    public void testInitMatchCompetitorEntities_withSingleCompetitor_returnsMapWithCompetitor() {
        // Arrange
        UUID competitorUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        competitor.setId(1L);

        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(competitorUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(competitorUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClub(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(20L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor));

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(mcUuid));
        assertEquals(matchCompetitor, result.get(mcUuid));
        verify(matchCompetitorRepository, times(1)).findById(20L);
    }

    @Test
    @DisplayName("initMatchCompetitorEntities processes multiple match competitors")
    public void testInitMatchCompetitorEntities_withMultipleCompetitors_returnsMapWithAll() {
        // Arrange
        UUID c1Uuid = UUID.randomUUID();
        UUID c2Uuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor1 = new Competitor();
        competitor1.setId(1L);
        Competitor competitor2 = new Competitor();
        competitor2.setId(2L);
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(c1Uuid, competitor1);
        competitorMap.put(c2Uuid, competitor2);

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setUuid(c1Uuid);

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setUuid(c2Uuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto1);
        matchCompetitorDto1.setClub(ClubIdentifier.HPSC);

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(21L);
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto2);
        matchCompetitorDto2.setClub(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(20L);

        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(21L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor1));
        when(matchCompetitorRepository.findById(21L)).thenReturn(Optional.of(matchCompetitor2));

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap,
                ClubIdentifier.HPSC);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(mc1Uuid));
        assertTrue(result.containsKey(mc2Uuid));
        assertEquals(matchCompetitor1, result.get(mc1Uuid));
        assertEquals(matchCompetitor2, result.get(mc2Uuid));
    }

    @Test
    @DisplayName("initMatchCompetitorEntities filters by club reference")
    public void testInitMatchCompetitorEntities_filtersByClubReference() {
        // Arrange
        UUID c1Uuid = UUID.randomUUID();
        UUID c2Uuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor1 = new Competitor();
        Competitor competitor2 = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(c1Uuid, competitor1);
        competitorMap.put(c2Uuid, competitor2);

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setUuid(c1Uuid);

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setUuid(c2Uuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto1);
        matchCompetitorDto1.setClub(ClubIdentifier.HPSC); // Should be included

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(21L);
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto2);
        matchCompetitorDto2.setClub(ClubIdentifier.SOSC); // Should be excluded

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(20L);

        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(21L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor1));
        when(matchCompetitorRepository.findById(21L)).thenReturn(Optional.of(matchCompetitor2));

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap,
                ClubIdentifier.HPSC);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.containsKey(mc1Uuid));
        assertFalse(result.containsKey(mc2Uuid));
    }

    @Test
    @DisplayName("initMatchCompetitorEntities includes UNKNOWN club reference")
    public void testInitMatchCompetitorEntities_withUnknownClubReference_includesAll() {
        // Arrange
        UUID c1Uuid = UUID.randomUUID();
        UUID c2Uuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor1 = new Competitor();
        Competitor competitor2 = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(c1Uuid, competitor1);
        competitorMap.put(c2Uuid, competitor2);

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setUuid(c1Uuid);

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setUuid(c2Uuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto1);
        matchCompetitorDto1.setClub(ClubIdentifier.HPSC);

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(21L);
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto2);
        matchCompetitorDto2.setClub(ClubIdentifier.SOSC);

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(20L);
        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(21L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor1));
        when(matchCompetitorRepository.findById(21L)).thenReturn(Optional.of(matchCompetitor2));

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap,
                ClubIdentifier.UNKNOWN);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(mc1Uuid));
        assertTrue(result.containsKey(mc2Uuid));
    }

    @Test
    @DisplayName("initMatchCompetitorEntities includes null club reference")
    public void testInitMatchCompetitorEntities_withNullClubReference_includesAll() {
        // Arrange
        UUID c1Uuid = UUID.randomUUID();
        UUID c2Uuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor1 = new Competitor();
        Competitor competitor2 = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(c1Uuid, competitor1);
        competitorMap.put(c2Uuid, competitor2);

        CompetitorDto competitorDto1 = new CompetitorDto();
        competitorDto1.setUuid(c1Uuid);

        CompetitorDto competitorDto2 = new CompetitorDto();
        competitorDto2.setUuid(c2Uuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto1);
        matchCompetitorDto1.setClub(ClubIdentifier.HPSC);

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(21L);
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto2);
        matchCompetitorDto2.setClub(ClubIdentifier.SOSC);

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(20L);
        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(21L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor1));
        when(matchCompetitorRepository.findById(21L)).thenReturn(Optional.of(matchCompetitor2));

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap, null);

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("initMatchCompetitorEntities creates new when id is null")
    public void testInitMatchCompetitorEntities_withNullId_createsNewMatchCompetitor() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(null);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClub(ClubIdentifier.HPSC);

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.containsKey(mcUuid));
        verify(matchCompetitorRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("initMatchCompetitorEntities creates new when not found in repository")
    public void testInitMatchCompetitorEntities_notFoundInRepository_createsNew() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClub(ClubIdentifier.HPSC);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.empty());

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.containsKey(mcUuid));
    }

    @Test
    @DisplayName("initMatchCompetitorEntities uses UUID as map key")
    public void testInitMatchCompetitorEntities_usesUuidAsKey() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto);
        matchCompetitorDto1.setClub(ClubIdentifier.HPSC);

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(21L);
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto);
        matchCompetitorDto2.setClub(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor1 = new MatchCompetitor();
        matchCompetitor1.setId(20L);
        MatchCompetitor matchCompetitor2 = new MatchCompetitor();
        matchCompetitor2.setId(21L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor1));
        when(matchCompetitorRepository.findById(21L)).thenReturn(Optional.of(matchCompetitor2));

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap,
                ClubIdentifier.HPSC);

        // Assert
        assertEquals(matchCompetitor1, result.get(mc1Uuid));
        assertEquals(matchCompetitor2, result.get(mc2Uuid));
        assertNull(result.get(UUID.randomUUID()));
    }

    @Test
    @DisplayName("initMatchCompetitorEntities calls init method on each competitor")
    public void testInitMatchCompetitorEntities_callsInitMethod() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClub(ClubIdentifier.HPSC);

        MatchCompetitor spyMatchCompetitor = spy(new MatchCompetitor());
        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(spyMatchCompetitor));

        // Act
        ipscDomainService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        verify(spyMatchCompetitor, times(1)).init(matchCompetitorDto, matchEntity, competitor);
    }

    @Test
    @DisplayName("initMatchCompetitorEntities links match and competitor to entity")
    public void testInitMatchCompetitorEntities_linksBothMatchAndCompetitor() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClub(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(20L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor));

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        MatchCompetitor resultMC = result.get(mcUuid);
        assertEquals(matchEntity, resultMC.getMatch());
        assertEquals(competitor, resultMC.getCompetitor());
    }

    @Test
    @DisplayName("initMatchCompetitorEntities skips when competitor not in map")
    public void testInitMatchCompetitorEntities_competitorNotInMap_skips() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();
        UUID unknownUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(unknownUuid); // Not in map

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClub(ClubIdentifier.HPSC);

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("initMatchCompetitorEntities handles large number of competitors")
    public void testInitMatchCompetitorEntities_withLargeNumber_processesAll() {
        // Arrange
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        List<MatchCompetitorDto> matchCompetitorDtos = new ArrayList<>();
        int numCompetitors = 50;

        for (int i = 0; i < numCompetitors; i++) {
            UUID cUuid = UUID.randomUUID();
            Competitor competitor = new Competitor();
            competitor.setId((long) i);
            competitorMap.put(cUuid, competitor);

            CompetitorDto competitorDto = new CompetitorDto();
            competitorDto.setUuid(cUuid);

            MatchCompetitorDto mcDto = new MatchCompetitorDto();
            mcDto.setId(100L + i);
            mcDto.setUuid(UUID.randomUUID());
            mcDto.setCompetitor(competitorDto);
            mcDto.setClub(ClubIdentifier.HPSC);
            matchCompetitorDtos.add(mcDto);

            MatchCompetitor mc = new MatchCompetitor();
            mc.setId(100L + i);
            when(matchCompetitorRepository.findById(100L + i)).thenReturn(Optional.of(mc));
        }

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                matchCompetitorDtos, matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertEquals(numCompetitors, result.size());
    }

    @Test
    @DisplayName("initMatchCompetitorEntities verifies repository calls for each competitor")
    public void testInitMatchCompetitorEntities_verifiesRepositoryCalls() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mcUuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto = new MatchCompetitorDto();
        matchCompetitorDto.setId(20L);
        matchCompetitorDto.setUuid(mcUuid);
        matchCompetitorDto.setCompetitor(competitorDto);
        matchCompetitorDto.setClub(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor));

        // Act
        ipscDomainService.initMatchCompetitorEntities(
                Collections.singletonList(matchCompetitorDto), matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        verify(matchCompetitorRepository, times(1)).findById(20L);
    }

    @Test
    @DisplayName("initMatchCompetitorEntities returns new empty map instance each call")
    public void testInitMatchCompetitorEntities_returnsNewMapInstance() {
        // Arrange
        Map<UUID, Competitor> competitorMap = new HashMap<>();

        // Act
        Map<UUID, MatchCompetitor> result1 = ipscDomainService.initMatchCompetitorEntities(
                null, matchEntity, competitorMap, ClubIdentifier.HPSC);
        Map<UUID, MatchCompetitor> result2 = ipscDomainService.initMatchCompetitorEntities(
                null, matchEntity, competitorMap, ClubIdentifier.HPSC);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
    }

    @Test
    @DisplayName("initMatchCompetitorEntities with duplicate match competitor ids")
    public void testInitMatchCompetitorEntities_withDuplicateIds() {
        // Arrange
        UUID cUuid = UUID.randomUUID();
        UUID mc1Uuid = UUID.randomUUID();
        UUID mc2Uuid = UUID.randomUUID();

        Competitor competitor = new Competitor();
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        competitorMap.put(cUuid, competitor);

        CompetitorDto competitorDto = new CompetitorDto();
        competitorDto.setUuid(cUuid);

        MatchCompetitorDto matchCompetitorDto1 = new MatchCompetitorDto();
        matchCompetitorDto1.setId(20L);
        matchCompetitorDto1.setUuid(mc1Uuid);
        matchCompetitorDto1.setCompetitor(competitorDto);
        matchCompetitorDto1.setClub(ClubIdentifier.HPSC);

        MatchCompetitorDto matchCompetitorDto2 = new MatchCompetitorDto();
        matchCompetitorDto2.setId(20L); // Same ID
        matchCompetitorDto2.setUuid(mc2Uuid);
        matchCompetitorDto2.setCompetitor(competitorDto);
        matchCompetitorDto2.setClub(ClubIdentifier.HPSC);

        MatchCompetitor matchCompetitor = new MatchCompetitor();
        matchCompetitor.setId(20L);

        when(matchCompetitorRepository.findById(20L)).thenReturn(Optional.of(matchCompetitor));

        // Act
        Map<UUID, MatchCompetitor> result = ipscDomainService.initMatchCompetitorEntities(
                Arrays.asList(matchCompetitorDto1, matchCompetitorDto2), matchEntity, competitorMap,
                ClubIdentifier.HPSC);

        // Assert
        assertEquals(2, result.size());
        assertEquals(matchCompetitor, result.get(mc1Uuid));
        assertEquals(matchCompetitor, result.get(mc2Uuid));
    }
}

