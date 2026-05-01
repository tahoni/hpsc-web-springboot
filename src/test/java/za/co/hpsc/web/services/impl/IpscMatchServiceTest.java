package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;
import za.co.hpsc.web.models.ipsc.match.holders.dto.MatchOnlyResultsDto;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchOnlyResponse;
import za.co.hpsc.web.services.DomainService;
import za.co.hpsc.web.services.TransactionService;
import za.co.hpsc.web.services.TransformationService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IpscMatchServiceTest {

    @Mock
    private TransformationService transformationService;

    @Mock
    private DomainService domainService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private MatchEntityServiceImpl matchEntityService;

    @InjectMocks
    private IpscMatchServiceImpl ipscMatchService;

    @Test
    void testInsertMatch_withMappedAndInitialisedEntities_thenSavesAndReturnsResponse() throws Exception {
        // Arrange
        MatchOnlyRequest request = buildRequest(7L, "Inserted Match", "HPSC", "Handgun", 6);

        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setId(7L);
        MatchOnlyResultsDto matchOnlyResultsDto = new MatchOnlyResultsDto(matchOnlyDto, null);

        when(transformationService.mapMatchOnly(request)).thenReturn(Optional.of(matchOnlyDto));
        when(domainService.initMatchOnlyEntities(matchOnlyDto)).thenReturn(Optional.of(matchOnlyResultsDto));

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.insertMatch(request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(7L, result.get().getMatchId());
        assertEquals("Inserted Match", result.get().getMatchName());
        assertEquals("HPSC", result.get().getClub());
        assertEquals("Handgun", result.get().getFirearm());
        assertEquals(6, result.get().getSquadCount());
        verify(transformationService).mapMatchOnly(request);
        verify(domainService).initMatchOnlyEntities(matchOnlyDto);
        verify(transactionService).saveMatch(matchOnlyResultsDto);
    }

    @Test
    void testInsertMatch_withMapMatchOnlyEmpty_thenSkipsDomainAndTransactionAndReturnsResponse() throws Exception {
        // Arrange
        MatchOnlyRequest request = buildRequest(8L, "No Mapping Match", "HPSC", "Rifle", 4);
        when(transformationService.mapMatchOnly(request)).thenReturn(Optional.empty());

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.insertMatch(request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(8L, result.get().getMatchId());
        verify(transformationService).mapMatchOnly(request);
        verifyNoInteractions(domainService);
        verifyNoInteractions(transactionService);
    }

    @Test
    void testInsertMatch_withDomainInitialisationEmpty_thenSkipsTransactionAndReturnsResponse() throws Exception {
        // Arrange
        MatchOnlyRequest request = buildRequest(9L, "No Domain Match", "HPSC", "Shotgun", 2);
        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setId(9L);
        when(transformationService.mapMatchOnly(request)).thenReturn(Optional.of(matchOnlyDto));
        when(domainService.initMatchOnlyEntities(matchOnlyDto)).thenReturn(Optional.empty());

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.insertMatch(request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(9L, result.get().getMatchId());
        verify(transformationService).mapMatchOnly(request);
        verify(domainService).initMatchOnlyEntities(matchOnlyDto);
        verifyNoInteractions(transactionService);
    }

    @Test
    void testUpdateMatch_withExistingMatchAndFullUpdate_thenOverwritesPersistedValues() throws Exception {
        // Arrange
        IpscMatch existingMatch = buildIpscMatch(21L, "Existing Match", "Existing Club", FirearmType.HANDGUN, 2026, 5, 1);
        when(matchEntityService.findMatchById(21L)).thenReturn(Optional.of(existingMatch));
        when(transformationService.mapMatchOnly(any(MatchOnlyRequest.class))).thenReturn(Optional.empty());

        MatchOnlyRequest incoming = new MatchOnlyRequest();
        incoming.setMatchName(null);
        incoming.setMatchDate(LocalDateTime.of(2026, 8, 15, 10, 0));
        incoming.setClub(null);
        incoming.setFirearm("Shotgun");
        incoming.setSquadCount(null);

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.updateMatch(21L, incoming);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(21L, result.get().getMatchId());
        assertNull(result.get().getMatchName());
        assertEquals(LocalDateTime.of(2026, 8, 15, 10, 0), result.get().getMatchDate());
        assertNull(result.get().getClub());
        assertEquals("Shotgun", result.get().getFirearm());
        assertNull(result.get().getSquadCount());

        ArgumentCaptor<MatchOnlyRequest> captor = ArgumentCaptor.forClass(MatchOnlyRequest.class);
        verify(transformationService).mapMatchOnly(captor.capture());
        assertNull(captor.getValue().getMatchName());
        assertNull(captor.getValue().getClub());
        assertEquals("Shotgun", captor.getValue().getFirearm());
    }

    @Test
    void testModifyMatch_withExistingMatchAndPartialUpdate_thenKeepsPersistedValuesForNullFields() throws Exception {
        // Arrange
        IpscMatch existingMatch = buildIpscMatch(22L, "Existing Match", "Existing Club", FirearmType.RIFLE, 2026, 6, 1);
        when(matchEntityService.findMatchById(22L)).thenReturn(Optional.of(existingMatch));
        when(transformationService.mapMatchOnly(any(MatchOnlyRequest.class))).thenReturn(Optional.empty());

        MatchOnlyRequest incoming = new MatchOnlyRequest();
        incoming.setMatchName(null);
        incoming.setMatchDate(null);
        incoming.setClub("Updated Club");
        incoming.setFirearm(null);
        incoming.setSquadCount(12);

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.modifyMatch(22L, incoming);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(22L, result.get().getMatchId());
        assertEquals("Existing Match", result.get().getMatchName());
        assertEquals(LocalDateTime.of(2026, 6, 1, 9, 0), result.get().getMatchDate());
        assertEquals("Updated Club", result.get().getClub());
        assertEquals("Rifle", result.get().getFirearm());
        assertEquals(12, result.get().getSquadCount());

        ArgumentCaptor<MatchOnlyRequest> captor = ArgumentCaptor.forClass(MatchOnlyRequest.class);
        verify(transformationService).mapMatchOnly(captor.capture());
        assertEquals("Existing Match", captor.getValue().getMatchName());
        assertEquals("Updated Club", captor.getValue().getClub());
        assertEquals("Rifle", captor.getValue().getFirearm());
    }

    @Test
    void testUpdateMatch_withMissingMatch_thenThrowsNonFatalException() {
        // Arrange
        when(matchEntityService.findMatchById(30L)).thenReturn(Optional.empty());

        // Act
        NonFatalException exception = assertThrows(NonFatalException.class,
                () -> ipscMatchService.updateMatch(30L, new MatchOnlyRequest()));

        // Assert
        assertTrue(exception.getMessage().contains("Match with id 30 not found"));
        verifyNoInteractions(transformationService);
        verifyNoInteractions(domainService);
        verifyNoInteractions(transactionService);
    }

    @Test
    void testGetMatch_withExistingMatch_thenReturnsMappedResponse() {
        // Arrange
        IpscMatch existingMatch = buildIpscMatch(40L, "Queried Match", "HPSC", FirearmType.HANDGUN, 2026, 7, 1);
        when(matchEntityService.findMatchById(40L)).thenReturn(Optional.of(existingMatch));

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.getMatch(40L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(40L, result.get().getMatchId());
        assertEquals("Queried Match", result.get().getMatchName());
        assertEquals("HPSC", result.get().getClub());
        assertEquals("Handgun", result.get().getFirearm());
    }

    @Test
    void testGetMatch_withMissingMatch_thenThrowsNonFatalException() {
        // Arrange
        when(matchEntityService.findMatchById(41L)).thenReturn(Optional.empty());

        // Act
        NonFatalException exception = assertThrows(NonFatalException.class,
                () -> ipscMatchService.getMatch(41L));

        // Assert
        assertTrue(exception.getMessage().contains("Match with id 41 not found"));
    }

    @Test
    void testFindMatchById_withNullId_thenThrowsValidationException() {
        // Act
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscMatchService.findMatchById(null));

        // Assert
        assertEquals("Match cannot be null, zero or negative", exception.getMessage());
    }

    @Test
    void testFindMatchById_withZeroId_thenThrowsValidationException() {
        // Act
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscMatchService.findMatchById(0L));

        // Assert
        assertEquals("Match cannot be null, zero or negative", exception.getMessage());
    }

    @Test
    void testFindMatchById_withNegativeId_thenThrowsValidationException() {
        // Act
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ipscMatchService.findMatchById(-1L));

        // Assert
        assertEquals("Match cannot be null, zero or negative", exception.getMessage());
    }

    @Test
    void testFindMatchById_withPositiveId_thenReturnsRepositoryResult() {
        // Arrange
        IpscMatch existingMatch = buildIpscMatch(50L, "Found Match", "HPSC", FirearmType.RIFLE, 2026, 9, 1);
        when(matchEntityService.findMatchById(50L)).thenReturn(Optional.of(existingMatch));

        // Act
        Optional<IpscMatch> result = ipscMatchService.findMatchById(50L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(50L, result.get().getId());
        verify(matchEntityService).findMatchById(50L);
    }

    private MatchOnlyRequest buildRequest(Long id, String name, String club, String firearm, Integer squadCount) {
        MatchOnlyRequest request = new MatchOnlyRequest();
        request.setMatchId(id);
        request.setMatchName(name);
        request.setMatchDate(LocalDateTime.of(2026, 5, 1, 9, 0));
        request.setClub(club);
        request.setFirearm(firearm);
        request.setSquadCount(squadCount);
        return request;
    }

    private IpscMatch buildIpscMatch(Long id,
                                     String name,
                                     String clubName,
                                     FirearmType firearmType,
                                     int year,
                                     int month,
                                     int day) {
        Club club = new Club();
        club.setName(clubName);

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setId(id);
        ipscMatch.setName(name);
        ipscMatch.setClub(club);
        ipscMatch.setMatchFirearmType(firearmType);
        ipscMatch.setScheduledDate(LocalDateTime.of(year, month, day, 9, 0));
        return ipscMatch;
    }
}

