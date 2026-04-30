package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.data.DtoMapping;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.holders.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.shared.MatchWithStages;
import za.co.hpsc.web.services.DomainService;
import za.co.hpsc.web.services.TransactionService;
import za.co.hpsc.web.services.TransformationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Disabled
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
    void insertMatch_whenValidMatchResponse_thenDelegatesToSavePipeline() throws FatalException {
        MatchWithStages matchResponse = new MatchWithStages();
        IpscResponseHolder holder = new IpscResponseHolder(List.of(new IpscResponse()));
        MatchResultsDto resultsDto = new MatchResultsDto(new MatchDto());
        DtoMapping mapping = new DtoMapping();

//        when(transformationService.mapMatchOnly(matchResponse)).thenReturn(holder);
        when(transformationService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(resultsDto));
        when(domainService.initMatchEntities(resultsDto, null, null)).thenReturn(Optional.of(mapping));

        ipscMatchService.insertMatch(matchResponse);

        verify(transformationService).mapMatchOnly(matchResponse);
        verify(transformationService).initMatchResults(any(IpscResponse.class));
        verify(domainService).initMatchEntities(resultsDto, null, null);
        verify(transactionService).saveMatchResults(mapping);
    }

    @Test
    void updateMatch_whenMatchExists_thenMergesAndSavesUsingFullUpdate() throws FatalException {
        Long matchId = 10L;
        MatchWithStages incoming = new MatchWithStages();

        IpscMatch persisted = new IpscMatch();
        persisted.setId(matchId);
        persisted.setName("Persisted");
        persisted.setScheduledDate(LocalDateTime.of(2026, 4, 10, 8, 0));

        when(matchEntityService.findMatchById(matchId)).thenReturn(Optional.of(persisted));

        IpscResponseHolder holder = new IpscResponseHolder(List.of(new IpscResponse()));
        MatchResultsDto resultsDto = new MatchResultsDto(new MatchDto());
        DtoMapping mapping = new DtoMapping();

//        when(transformationService.mapMatchOnly(any(MatchResponse.class))).thenReturn(holder);
        when(transformationService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(resultsDto));
        when(domainService.initMatchEntities(resultsDto, null, null)).thenReturn(Optional.of(mapping));

        ipscMatchService.updateMatch(matchId, incoming);

        verify(matchEntityService, atLeastOnce()).findMatchById(matchId);
        verify(transformationService).mapMatchOnly(any(MatchResponse.class));
        verify(transactionService).saveMatchResults(mapping);
    }

    @Test
    void modifyMatch_whenMatchDoesNotExist_thenThrowsException() {
        Long matchId = 99L;
        MatchWithStages incoming = new MatchWithStages();

        when(matchEntityService.findMatchById(matchId)).thenReturn(Optional.empty());

        assertThrows(NonFatalException.class, () -> ipscMatchService.modifyMatch(matchId, incoming));

        verify(matchEntityService).findMatchById(matchId);
        verifyNoInteractions(transformationService);
        verifyNoInteractions(domainService);
        verifyNoInteractions(transactionService);
    }

    @Test
    void mergeMatchResponses_whenMatchExists_thenReturnsMergedResponse() {
        Long matchId = 7L;
        MatchResponse incoming = new MatchResponse(7, "Incoming Name",
                LocalDateTime.of(2026, 6, 1, 12, 0), 123, 3, 1);

        IpscMatch persisted = new IpscMatch();
        persisted.setId(matchId);
        persisted.setName("Existing Name");
        persisted.setScheduledDate(LocalDateTime.of(2026, 5, 1, 12, 0));

        when(matchEntityService.findMatchById(matchId)).thenReturn(Optional.of(persisted));

        Optional<MatchResponse> result = ipscMatchService.mergeMatchResponses(matchId, incoming, true);

        assertTrue(result.isPresent());
        assertEquals("Incoming Name", result.get().getMatchName());
        assertEquals(LocalDateTime.of(2026, 6, 1, 12, 0), result.get().getMatchDate());
    }

    @Test
    void mergeMatchResponses_whenMatchMissing_thenThrowsException() {
        when(matchEntityService.findMatchById(15L)).thenReturn(Optional.empty());

        assertThrows(NonFatalException.class, () -> ipscMatchService.mergeMatchResponses(15L,
                new MatchResponse(15, "Any", LocalDateTime.now(), 1, 1, 1), false));
    }

    @Test
    void saveMatchResponse_whenNoMatchResultsProduced_thenSkipsPersistence() throws FatalException {
        MatchResponse matchResponse = new MatchResponse(3, "No Results",
                LocalDateTime.of(2026, 4, 1, 10, 0), 100, 5, 1);
        IpscResponseHolder holder = new IpscResponseHolder(List.of(new IpscResponse(), new IpscResponse()));

//        when(transformationService.mapMatchOnly(matchResponse)).thenReturn(holder);
        when(transformationService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.empty());

        ipscMatchService.saveMatchResponse(matchResponse);

        verify(transformationService).mapMatchOnly(matchResponse);
        verify(transformationService, times(2)).initMatchResults(any(IpscResponse.class));
        verifyNoInteractions(domainService);
        verifyNoInteractions(transactionService);
    }

    @Test
    void saveMatchResponse_whenDomainMappingMissing_thenSkipsTransactionSave() throws FatalException {
        MatchResponse matchResponse = new MatchResponse(4, "No Mapping",
                LocalDateTime.of(2026, 4, 1, 10, 0), 100, 5, 1);
        IpscResponseHolder holder = new IpscResponseHolder(List.of(new IpscResponse()));
        MatchResultsDto resultsDto = new MatchResultsDto(new MatchDto());

//        when(transformationService.mapMatchOnly(matchResponse)).thenReturn(holder);
        when(transformationService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(resultsDto));
        when(domainService.initMatchEntities(resultsDto, null, null)).thenReturn(Optional.empty());

        ipscMatchService.saveMatchResponse(matchResponse);

        verify(domainService).initMatchEntities(resultsDto, null, null);
        verifyNoInteractions(transactionService);
    }

    @Test
    void insertMatch_whenPersistedMatchIsReturned_thenReturnsSavedMatchResponse() throws FatalException {
        MatchResponse request = new MatchResponse(1, "Insert Match",
                LocalDateTime.of(2026, 4, 1, 10, 0), 120, 6, 1);

        IpscResponseHolder holder = new IpscResponseHolder(List.of(new IpscResponse()));
        MatchResultsDto resultsDto = new MatchResultsDto(new MatchDto());
        DtoMapping mapping = new DtoMapping();

        IpscMatch persisted = new IpscMatch();
        persisted.setId(55L);
        persisted.setName("Persisted Match");
        MatchHolder matchHolder = new MatchHolder();
        matchHolder.setMatch(persisted);

//        when(transformationService.mapMatchOnly(request)).thenReturn(holder);
        when(transformationService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(resultsDto));
        when(domainService.initMatchEntities(resultsDto, null, null)).thenReturn(Optional.of(mapping));
        when(transactionService.saveMatchResults(mapping)).thenReturn(Optional.of(matchHolder));

        Optional<MatchResponse> result = ipscMatchService.insertMatch(request);

        assertTrue(result.isPresent());
        assertEquals(55L, Optional.ofNullable(result.get().getMatchId()).orElse(0).longValue());
        verify(transactionService).saveMatchResults(mapping);
    }

    @Test
    void updateMatch_whenMatchIdIsNull_thenThrowsValidationException() {
        MatchResponse incoming = new MatchResponse();

        assertThrows(ValidationException.class, () -> ipscMatchService.updateMatch(null, incoming));

        verifyNoInteractions(matchEntityService);
        verifyNoInteractions(transformationService);
        verifyNoInteractions(domainService);
        verifyNoInteractions(transactionService);
    }

    @Test
    void updateMatch_whenPersistedAndSaved_thenReturnsMergedSavedResponse() throws FatalException {
        Long matchId = 25L;
        MatchResponse incoming = new MatchResponse(25, "Updated Name",
                LocalDateTime.of(2026, 6, 2, 14, 0), 110, 5, 1);

        IpscMatch existing = new IpscMatch();
        existing.setId(matchId);
        existing.setName("Existing Name");
        existing.setScheduledDate(LocalDateTime.of(2026, 5, 1, 9, 0));

        IpscResponseHolder holder = new IpscResponseHolder(List.of(new IpscResponse()));
        MatchResultsDto resultsDto = new MatchResultsDto(new MatchDto());
        DtoMapping mapping = new DtoMapping();

        IpscMatch persisted = new IpscMatch();
        persisted.setId(matchId);
        persisted.setName("Updated Name");
        MatchHolder matchHolder = new MatchHolder();
        matchHolder.setMatch(persisted);

        when(matchEntityService.findMatchById(matchId)).thenReturn(Optional.of(existing));
//        when(transformationService.mapMatchOnly(any(MatchResponse.class))).thenReturn(holder);
        when(transformationService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(resultsDto));
        when(domainService.initMatchEntities(resultsDto, null, null)).thenReturn(Optional.of(mapping));
        when(transactionService.saveMatchResults(mapping)).thenReturn(Optional.of(matchHolder));

        Optional<MatchResponse> result = ipscMatchService.updateMatch(matchId, incoming);

        assertTrue(result.isPresent());
        assertEquals(matchId.intValue(), result.get().getMatchId());
        verify(matchEntityService, atLeastOnce()).findMatchById(matchId);
        verify(transactionService).saveMatchResults(mapping);
    }

    @Test
    void saveMatchResponse_whenMappingExistsButTransactionReturnsEmpty_thenReturnsEmpty() throws FatalException {
        MatchResponse request = new MatchResponse(8, "Transaction Empty",
                LocalDateTime.of(2026, 4, 1, 10, 0), 100, 5, 1);

        IpscResponseHolder holder = new IpscResponseHolder(List.of(new IpscResponse()));
        MatchResultsDto resultsDto = new MatchResultsDto(new MatchDto());
        DtoMapping mapping = new DtoMapping();

//        when(transformationService.mapMatchOnly(request)).thenReturn(holder);
        when(transformationService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(resultsDto));
        when(domainService.initMatchEntities(resultsDto, null, null)).thenReturn(Optional.of(mapping));
        when(transactionService.saveMatchResults(mapping)).thenReturn(Optional.empty());

        Optional<MatchResponse> result = ipscMatchService.saveMatchResponse(request);

        assertTrue(result.isEmpty());
        verify(transactionService).saveMatchResults(mapping);
    }

    @Test
    void findMatchById_whenIdIsZero_thenThrowsValidationException() {
        assertThrows(ValidationException.class, () -> ipscMatchService.findMatchById(0L));

        verifyNoInteractions(matchEntityService);
    }

    @Test
    void getMatch_whenMatchExists_thenReturnsMappedResponse() {
        Long matchId = 31L;
        IpscMatch persisted = new IpscMatch();
        persisted.setId(matchId);
        persisted.setName("Retrieved Match");
        persisted.setScheduledDate(LocalDateTime.of(2026, 4, 20, 11, 0));

        when(matchEntityService.findMatchById(matchId)).thenReturn(Optional.of(persisted));

        Optional<MatchResponse> result = ipscMatchService.getMatch(matchId);

        assertTrue(result.isPresent());
        assertEquals(matchId.intValue(), result.get().getMatchId());
        assertEquals("Retrieved Match", result.get().getMatchName());
        verify(matchEntityService).findMatchById(matchId);
    }
}