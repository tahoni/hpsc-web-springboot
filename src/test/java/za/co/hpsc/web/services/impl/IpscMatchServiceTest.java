package za.co.hpsc.web.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.models.ipsc.data.DtoMapping;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
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

        when(transformationService.mapMatchOnly(matchResponse)).thenReturn(holder);
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

        when(transformationService.mapMatchOnly(any(MatchResponse.class))).thenReturn(holder);
        when(transformationService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(resultsDto));
        when(domainService.initMatchEntities(resultsDto, null, null)).thenReturn(Optional.of(mapping));

        ipscMatchService.updateMatch(matchId, incoming);

        verify(matchEntityService, atLeastOnce()).findMatchById(matchId);
        verify(transformationService).mapMatchOnly(any(MatchResponse.class));
        verify(transactionService).saveMatchResults(mapping);
    }

    @Test
    void modifyMatch_whenMatchDoesNotExist_thenThrowsException() throws FatalException {
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
    void mergeMatchResponses_whenMatchExists_thenReturnsMergedResponse() throws FatalException {
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

        when(transformationService.mapMatchOnly(matchResponse)).thenReturn(holder);
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

        when(transformationService.mapMatchOnly(matchResponse)).thenReturn(holder);
        when(transformationService.initMatchResults(any(IpscResponse.class))).thenReturn(Optional.of(resultsDto));
        when(domainService.initMatchEntities(resultsDto, null, null)).thenReturn(Optional.empty());

        ipscMatchService.saveMatchResponse(matchResponse);

        verify(domainService).initMatchEntities(resultsDto, null, null);
        verifyNoInteractions(transactionService);
    }
}