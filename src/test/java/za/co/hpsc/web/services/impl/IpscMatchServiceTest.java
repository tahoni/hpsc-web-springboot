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
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.common.dto.ClubDto;
import za.co.hpsc.web.models.ipsc.common.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;
import za.co.hpsc.web.models.ipsc.match.holders.dto.MatchOnlyResultsDto;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchOnlyResponse;
import za.co.hpsc.web.services.DomainService;
import za.co.hpsc.web.services.TransactionService;
import za.co.hpsc.web.services.TransformationService;
import za.co.hpsc.web.utils.ValueUtil;

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
    void testModifyMatch_withExistingMatchAndPartialUpdate_thenKeepsPersistedValuesForNullFields() throws Exception {
        // Arrange
        MatchOnlyRequest incoming = prepareIncomingMatchOnlyRequest(22L, false,
                "Existing Match", "Updated Match",
                "Updated Club", "Existing Club", FirearmType.RIFLE, null,
                12, 2026, 5, 1, 9, 0);

        // Act
        Optional<MatchOnlyResponse> result = ipscMatchService.modifyMatch(22L, incoming);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(22L, result.get().getMatchId());
        assertEquals("Existing Match", result.get().getMatchName());
        assertEquals(LocalDateTime.of(2026, 5, 1, 9, 0), result.get().getMatchDate());
        assertEquals("Updated Club", result.get().getClub());
        assertEquals("Rifle", result.get().getFirearm());
        assertEquals(0, result.get().getSquadCount());

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
        LocalDateTime expectedDate = LocalDateTime.of(2026, 7, 1, 9, 0);
        IpscMatch existingMatch = buildIpscMatch(40L, "Queried Match", "HPSC",
                FirearmType.HANDGUN, 2026, 7, 1, 9, 0);
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
        LocalDateTime expectedDate = LocalDateTime.of(2026, 9, 1, 8, 0);
        IpscMatch existingMatch = buildIpscMatch(50L, "Found Match", "HPSC",
                FirearmType.RIFLE, 2026, 9, 1, 8, 0);
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
                                     int day,
                                     int hour,
                                     int minute) {
        Club club = new Club();
        club.setName(clubName);

        IpscMatch ipscMatch = new IpscMatch();
        ipscMatch.setId(id);
        ipscMatch.setName(name);
        ipscMatch.setClub(club);
        ipscMatch.setMatchFirearmType(firearmType);
        ipscMatch.setScheduledDate(LocalDateTime.of(year, month, day, hour, minute));
        return ipscMatch;
    }

    private MatchOnlyRequest prepareIncomingMatchOnlyRequest(long id, boolean fullUpdate,
                                                             String existingMatchName,
                                                             String updatedMatchName,
                                                             String existingClubName, String updatedClubName,
                                                             FirearmType existingFirearmType, FirearmType updatedFirearmType,
                                                             int squadCount, int year, int month, int day, int hour, int minute)
            throws FatalException {
        LocalDateTime expectedDate = LocalDateTime.of(year, month, day, hour, minute);
        IpscMatch existingMatch = buildIpscMatch(id, existingMatchName, existingClubName, existingFirearmType,
                year, month, day, hour, minute);
        IpscMatch updatedMatch = buildIpscMatch(id, updatedMatchName, updatedClubName, updatedFirearmType,
                year, month, day, hour, minute);

        Club existingClub = new Club();
        existingClub.setName(existingClubName);
        Club updatedClub = new Club();
        updatedClub.setName(updatedClubName);

        MatchOnlyRequest incoming = new MatchOnlyRequest();
        incoming.setMatchName(existingMatchName);
        incoming.setMatchDate(expectedDate);
        incoming.setClub(existingClubName);
        incoming.setFirearm(ValueUtil.nullAsDefaultString(existingFirearmType, null));
        incoming.setSquadCount(squadCount);

        IpscMatch finalMatch;
        Club finalClub;
        String finalMatchName;
        String finalClubName;
        FirearmType finalFirearmType;
        if (fullUpdate) {
            finalMatch = updatedMatch;
            finalMatchName = updatedMatchName;
            finalClub = updatedClub;
            finalClubName = updatedClubName;
            finalFirearmType = updatedFirearmType;
        } else {
            finalMatch = existingMatch;
            finalMatchName = existingMatchName;
            finalClub = existingClub;
            finalClubName = existingClubName;
            finalFirearmType = existingFirearmType;
        }

        MatchOnlyDto matchOnlyDto = new MatchOnlyDto();
        matchOnlyDto.setId(id);
        matchOnlyDto.setName(finalMatchName);
        matchOnlyDto.setScheduledDate(expectedDate);
        matchOnlyDto.setClubName(finalClubName);
        matchOnlyDto.setMatchFirearmType(finalFirearmType);

        MatchOnlyResultsDto matchOnlyResultsDto = new MatchOnlyResultsDto(matchOnlyDto, null);
        matchOnlyResultsDto.setClub(new ClubDto(finalClub));
        MatchHolder matchHolder = new MatchHolder();
        matchHolder.setMatch(finalMatch);
        matchHolder.setClub(finalClub);

        when(matchEntityService.findMatchById(id)).thenReturn(Optional.of(existingMatch));
        when(transformationService.mapMatchOnly(any(MatchOnlyRequest.class))).thenReturn(Optional.of(matchOnlyDto));
        when(domainService.initMatchOnlyEntities(matchOnlyDto)).thenReturn(Optional.of(matchOnlyResultsDto));
        when(transactionService.saveMatch(matchOnlyResultsDto)).thenReturn(Optional.of(matchHolder));

        return incoming;
    }
}

