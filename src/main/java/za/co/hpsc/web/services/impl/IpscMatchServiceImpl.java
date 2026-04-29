package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.models.ipsc.data.DtoMapping;
import za.co.hpsc.web.models.ipsc.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.holders.dto.MatchResultsDto;
import za.co.hpsc.web.models.ipsc.holders.dto.MatchResultsDtoHolder;
import za.co.hpsc.web.models.ipsc.holders.response.IpscResponseHolder;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.shared.MatchWithStages;
import za.co.hpsc.web.services.*;
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
    protected final MatchEntityService matchEntityService;

    public IpscMatchServiceImpl(TransformationService transformationService,
                                DomainService domainService,
                                TransactionService transactionService,
                                MatchEntityService matchEntityService) {
        this.transformationService = transformationService;
        this.domainService = domainService;
        this.transactionService = transactionService;
        this.matchEntityService = matchEntityService;
    }

    @Override
    public void insertMatch(MatchWithStages matchWithStages) throws FatalException {
        saveMatchResponse(matchWithStages);
    }

    @Override
    public void updateMatch(Long matchId, MatchWithStages matchWithStages) {
        modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchWithStages, true);
    }

    @Override
    public void modifyMatch(Long matchId, MatchWithStages matchWithStages) {
        modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchWithStages, false);
    }

    @Override
    public List<MatchWithStages> getMatches(MatchWithStages matchWithStages) {
        return List.of();
    }

    @Override
    public Optional<MatchWithStages> getMatch(Long matchId) {
        return Optional.empty();
    }

    /**
     * Applies update semantics to an existing match and persists the merged result.
     * <p>
     * This method is used by both full update (PUT-like) and partial update (PATCH-like) flows.
     * It first normalises the identifier, checks whether the match exists, merges incoming values,
     * and then persists the merged payload.
     * </p>
     * <p>
     * If no match exists for the resolved id, the method exits without persisting anything.
     * </p>
     *
     * @param matchId       identifier of the target match; {@code null} resolves to {@code 0}
     * @param matchResponse incoming payload containing fields to apply
     * @param fullUpdate    {@code true} for replacement semantics, {@code false} for patch semantics
     */
    protected void modifyMatchResponse(Long matchId, MatchResponse matchResponse,
                                       boolean fullUpdate) {
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);

        Optional<IpscMatch> ipscMatch = matchEntityService.findMatchById(matchIdNumber);
        if (ipscMatch.isEmpty()) {
            return;
        }

        Optional<MatchResponse> optionalMatchResponse =
                mergeMatchResponses(matchIdNumber, matchResponse, fullUpdate);
        optionalMatchResponse.ifPresent(response -> {
            try {
                saveMatchResponse(response);
            } catch (FatalException e) {
                throw new RuntimeFatalException(e);
            }
        });
    }

    /**
     * Produces a merged {@link MatchResponse} by combining persisted match state with an incoming payload.
     * <p>
     * The persisted entity is converted to {@link MatchDto}, then to {@link MatchResponse},
     * and finally merged through {@link MatchResponse#init(MatchResponse, boolean)}.
     * </p>
     *
     * @param matchId       identifier of the target match; {@code null} resolves to {@code 0}
     * @param matchResponse incoming payload to merge into the persisted representation
     * @param fullUpdate    {@code true} to overwrite fields, {@code false} for partial merge
     * @return merged match response wrapped in {@link Optional}, or empty if no persisted match is found
     */
    protected Optional<MatchResponse> mergeMatchResponses(Long matchId, MatchResponse matchResponse,
                                                          boolean fullUpdate) {
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);

        Optional<IpscMatch> ipscMatch = matchEntityService.findMatchById(matchIdNumber);
        if (ipscMatch.isEmpty()) {
            return Optional.empty();
        }

        MatchDto matchDto = new MatchDto(ipscMatch.get());
        MatchResponse fetchedMatchResponse = new MatchResponse(matchIdNumber, matchDto);
        fetchedMatchResponse.init(matchResponse, fullUpdate);
        return Optional.of(fetchedMatchResponse);
    }

    /**
     * Executes the match save pipeline from API payload to transactional persistence.
     * <ol>
     *   <li>Maps the payload into an {@link IpscResponseHolder}</li>
     *   <li>Transforms each response into {@link MatchResultsDto}</li>
     *   <li>Filters null DTO entries</li>
     *   <li>Maps each DTO into domain entities</li>
     *   <li>Persists each successful mapping</li>
     * </ol>
     *
     * @param matchResponse payload to transform and persist
     * @throws FatalException when an unrecoverable transformation or persistence error occurs
     */
    protected void saveMatchResponse(MatchResponse matchResponse) throws FatalException {
        IpscResponseHolder ipscResponseHolder = transformationService.mapMatchOnly(matchResponse);

        List<MatchResultsDto> matchResultsList = new ArrayList<>();
        for (IpscResponse ipscResponse : ipscResponseHolder.getIpscList()) {
            Optional<MatchResultsDto> optionalMatchResults = transformationService.initMatchResults(ipscResponse);
            optionalMatchResults.ifPresent(matchResultsList::add);
        }

        MatchResultsDtoHolder matchResultsDtoHolder = new MatchResultsDtoHolder(matchResultsList);
        if (matchResultsDtoHolder.getMatches() == null) {
            return;
        }

        List<MatchResultsDto> ipscResultsList = matchResultsDtoHolder.getMatches().stream()
                .filter(Objects::nonNull)
                .toList();

        for (MatchResultsDto matchResultsDto : ipscResultsList) {
            Optional<DtoMapping> optionalDtoToEntityMapping =
                    domainService.initMatchEntities(matchResultsDto, null, null);
            if (optionalDtoToEntityMapping.isPresent()) {
                DtoMapping dtoMapping = optionalDtoToEntityMapping.get();
                transactionService.saveMatchResults(dtoMapping);
            }
        }
    }

    private static class RuntimeFatalException extends RuntimeException {
        RuntimeFatalException(FatalException cause) {
            super(cause);
        }
    }
}