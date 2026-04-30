package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.common.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;
import za.co.hpsc.web.models.ipsc.match.holders.dto.MatchOnlyResultsDto;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchOnlyResponse;
import za.co.hpsc.web.services.DomainService;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.TransactionService;
import za.co.hpsc.web.services.TransformationService;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.Optional;

// TODO: add JavaDoc
// TODO: add tests
@Slf4j
@Service
public class IpscMatchServiceImpl implements IpscMatchService {

    protected final TransformationService transformationService;
    protected final DomainService domainService;
    protected final TransactionService transactionService;

    protected final MatchEntityServiceImpl matchEntityService;

    public IpscMatchServiceImpl(TransformationService transformationService,
                                DomainService domainService,
                                TransactionService transactionService,
                                MatchEntityServiceImpl matchEntityService) {
        this.transformationService = transformationService;
        this.domainService = domainService;
        this.transactionService = transactionService;
        this.matchEntityService = matchEntityService;
    }

    @Override
    public Optional<MatchOnlyResponse> insertMatch(MatchOnlyRequest matchOnlyRequest)
            throws FatalException {
        return saveMatchResponse(matchOnlyRequest);
    }

    @Override
    public Optional<MatchOnlyResponse> updateMatch(Long matchId, MatchOnlyRequest matchOnlyRequest)
            throws FatalException {
        return modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchOnlyRequest, true);
    }

    @Override
    public Optional<MatchOnlyResponse> modifyMatch(Long matchId, MatchOnlyRequest matchOnlyRequest) throws FatalException {
        return modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchOnlyRequest, false);
    }

    @Override
    public Optional<MatchOnlyResponse> getMatch(Long matchId) {
        // Find the match by id
        Optional<IpscMatch> optionalIpscMatch = matchEntityService.findMatchById(matchId);
        IpscMatch ipscMatch = optionalIpscMatch.orElseThrow(() ->
                new NonFatalException("Match with id %d not found".formatted(matchId)));

        // Convert the match to a match response
        Long matchIdNumber = ipscMatch.getId();
        MatchDto matchDto = new MatchDto(ipscMatch);
        return Optional.of(new MatchOnlyResponse(matchIdNumber, matchDto));
    }

    protected Optional<MatchOnlyResponse> modifyMatchResponse(Long matchId,
                                                              MatchOnlyRequest matchOnlyRequest,
                                                              boolean fullUpdate)
            throws FatalException {
        // Merge the incoming payload with the persisted state for the given match id
        Optional<MatchOnlyResponse> optionalMergedMatchOnlyResponse =
                mergeMatchResponses(matchId, matchOnlyRequest, fullUpdate);

        // If a merged response is returned, persist it
        Optional<MatchOnlyResponse> optionalMatchOnlyResponse = Optional.empty();
        if (optionalMergedMatchOnlyResponse.isPresent()) {
            optionalMatchOnlyResponse = saveMatchResponse(optionalMergedMatchOnlyResponse.get());
        }

        return optionalMatchOnlyResponse;
    }

    protected Optional<MatchOnlyResponse> mergeMatchResponses(Long matchId,
                                                              MatchOnlyRequest matchOnlyRequest,
                                                              boolean fullUpdate) {
        // Fetch the persisted entity
        IpscMatch ipscMatch = findMatchById(matchId)
                .orElseThrow(() -> new NonFatalException("Match with id %d not found".formatted(matchId)));
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);

        // Convert to a match response and merge with the incoming payload
        MatchDto matchDto = new MatchDto(ipscMatch);
        MatchOnlyResponse fetchedMatchWithStagesResponse = new MatchOnlyResponse(matchIdNumber, matchDto);
        fetchedMatchWithStagesResponse.init(matchIdNumber, matchOnlyRequest, fullUpdate);
        return Optional.of(fetchedMatchWithStagesResponse);
    }

    protected Optional<MatchOnlyResponse> saveMatchResponse(MatchOnlyRequest matchOnlyRequest)
            throws FatalException {
        Optional<MatchOnlyDto> optionalMatchOnlyDto = transformationService.mapMatchOnly(matchOnlyRequest);

        if (optionalMatchOnlyDto.isPresent()) {
            Optional<MatchOnlyResultsDto> optionalMatchOnlyResultsDto =
                    domainService.initMatchOnlyEntities(optionalMatchOnlyDto.get());
            if (optionalMatchOnlyResultsDto.isPresent()) {
                transactionService.saveMatch(optionalMatchOnlyResultsDto.get());
            }
        }

        MatchOnlyResponse matchOnlyResponse = new MatchOnlyResponse(matchOnlyRequest);
        return Optional.of(matchOnlyResponse);
    }

    protected Optional<IpscMatch> findMatchById(Long matchId) {
        // Normalise the match id to zero if null or negative
        if ((matchId == null) || (matchId <= 0)) {
            throw new ValidationException("Match cannot be null, zero or negative");
        }
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);

        // Find the match by id
        return matchEntityService.findMatchById(matchIdNumber);
    }
}