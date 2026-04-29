package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.data.DtoMapping;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.holders.dto.MatchResultsDtoHolder;
import za.co.hpsc.web.models.ipsc.holders.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.request.MatchSearchRequest;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.services.DomainService;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.TransactionService;
import za.co.hpsc.web.services.TransformationService;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class IpscMatchServiceImpl implements IpscMatchService {
    protected final TransformationService transformationService;
    protected final DomainService domainService;
    protected final TransactionService transactionService;

    protected final MatchEntityServiceImpl matchEntityService;

    public IpscMatchServiceImpl(TransformationService transformationService, DomainService domainService,
                                TransactionService transactionService, MatchEntityServiceImpl matchEntityService) {
        this.transformationService = transformationService;
        this.domainService = domainService;
        this.transactionService = transactionService;
        this.matchEntityService = matchEntityService;
    }

    @Override
    public boolean insertMatch(MatchResponse matchResponse)
            throws FatalException {
        // Saves the match
        return saveMatchResponse(matchResponse).isPresent();
    }

    @Override
    public boolean updateMatch(String matchId, MatchResponse matchResponse)
            throws FatalException {
        return modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchResponse, true).isPresent();
    }

    @Override
    public boolean modifyMatch(String matchId, MatchResponse matchResponse)
            throws FatalException {
        return modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchResponse, false).isPresent();
    }

    @Override
    public List<MatchResponse> getMatches(MatchSearchRequest matchSearchRequest) {
        return List.of();
    }

    @Override
    public MatchResponse getMatch(Integer matchId) {
        return null;
    }


    protected Optional<MatchResponse> modifyMatchResponse(Long matchId, MatchResponse matchResponse, boolean fullUpdate)
            throws FatalException {
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);

        Optional<IpscMatch> ipscMatch =
                matchEntityService.findMatchById(matchIdNumber);
        if (ipscMatch.isEmpty()) {
            return Optional.empty();
        }

        Optional<MatchResponse> optionalMatchResponse = mergeMatchResponses(matchIdNumber, matchResponse, fullUpdate);
        if (optionalMatchResponse.isPresent()) {
            saveMatchResponse(optionalMatchResponse.get());
        }

        return optionalMatchResponse;
    }

    // TODO: comment
    protected Optional<MatchResponse> mergeMatchResponses(Long matchId, MatchResponse matchResponse, boolean fullUpdate) {
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);
        Optional<IpscMatch> ipscMatch =
                matchEntityService.findMatchById(matchIdNumber);
        if (ipscMatch.isEmpty()) {
            return Optional.empty();
        }

        MatchDto matchDto = new MatchDto(ipscMatch.get());
        MatchResponse fetchedMatchResponse = new MatchResponse(matchIdNumber, matchDto);
        fetchedMatchResponse.init(matchResponse, fullUpdate);
        return Optional.of(fetchedMatchResponse);
    }

    protected Optional<MatchHolder> saveMatchResponse(MatchResponse matchResponse)
            throws FatalException {
        IpscResponseHolder ipscResponseHolder = transformationService.mapMatchOnly(matchResponse);

        // Accumulates the match results to be persisted
        List<MatchResultsDto> matchResultsList = new ArrayList<>();
        // Iterates responses and accumulates DTOs
        for (IpscResponse ipscResponse : ipscResponseHolder.getIpscList()) {
            Optional<MatchResultsDto> optionalMatchResults =
                    transformationService.initMatchResults(ipscResponse);
            optionalMatchResults.ifPresent(matchResultsList::add);
        }

        // Initialises the match results DTO holder with the accumulated DTOs
        MatchResultsDtoHolder matchResultsDtoHolder = new MatchResultsDtoHolder(matchResultsList);
        if (matchResultsDtoHolder.getMatches() == null) {
            return Optional.empty();
        }

        // Filter out null matches
        List<MatchResultsDto> ipscResultsList = matchResultsDtoHolder.getMatches().stream()
                .filter(Objects::nonNull)
                .toList();

        List<MatchHolder> matchHolderList = new ArrayList<>();
        // Iterates the DTOs, maps them to entities, and persists the results
        for (MatchResultsDto matchResultsDto : ipscResultsList) {
            // Maps the DTO to an entity
            Optional<DtoMapping> optionalDtoToEntityMapping =
                    domainService.initMatchEntities(matchResultsDto, null, null);
            if (optionalDtoToEntityMapping.isPresent()) {
                // Persists the entity
                DtoMapping dtoMapping = optionalDtoToEntityMapping.get();
                Optional<MatchHolder> optionalMatchHolder = transactionService.saveMatchResults(dtoMapping);
                optionalMatchHolder.ifPresent(matchHolderList::add);
            }
        }

        return matchHolderList.stream().findFirst();
    }
}
