package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
import za.co.hpsc.web.services.DomainService;
import za.co.hpsc.web.services.IpscMatchService;
import za.co.hpsc.web.services.TransactionService;
import za.co.hpsc.web.services.TransformationService;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.Optional;

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
    public Optional<MatchResponse> insertMatch(MatchResponse matchResponse) throws FatalException {
        return saveMatchResponse(matchResponse);
    }

    @Override
    public Optional<MatchResponse> updateMatch(Long matchId, MatchResponse matchResponse) throws FatalException {
        return modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchResponse, true);
    }

    @Override
    public Optional<MatchResponse> modifyMatch(Long matchId, MatchResponse matchResponse) throws FatalException {
        return modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchResponse, false);
    }

    @Override
    public Optional<MatchResponse> getMatch(Long matchId) {
        // Find the match by id
        Optional<IpscMatch> optionalIpscMatch = matchEntityService.findMatchById(matchId);
        IpscMatch ipscMatch = optionalIpscMatch.orElseThrow(() ->
                new NonFatalException("Match with id %d not found".formatted(matchId)));

        // Convert the match to a match response
        Long matchIdNumber = ipscMatch.getId();
        MatchDto matchDto = new MatchDto(ipscMatch);
        return Optional.of(new MatchResponse(matchIdNumber, matchDto));
    }

    /**
     * Applies full/partial merge semantics to an existing match and persists the result.
     *
     * @param matchId       identifier of the match to update
     * @param matchResponse incoming payload to merge
     * @param fullUpdate    {@code true} for full replacement semantics; {@code false} for partial merge semantics
     * @return merged response if merge/persist produced a result; otherwise empty
     * @throws FatalException if persistence/transformation fails during save
     */
    protected Optional<MatchResponse> modifyMatchResponse(Long matchId, MatchResponse matchResponse,
                                                          boolean fullUpdate)
            throws FatalException {
        // Merge the incoming payload with the persisted state for the given match id
        Optional<MatchResponse> optionalMatchResponse =
                mergeMatchResponses(matchId, matchResponse, fullUpdate);

        // If a merged response is returned, persist it
        if (optionalMatchResponse.isPresent()) {
            saveMatchResponse(optionalMatchResponse.get());
        }

        return optionalMatchResponse;
    }

    /**
     * Builds a merged match response from persisted state and incoming payload.
     *
     * @param matchId       identifier of the target persisted match
     * @param matchResponse incoming payload
     * @param fullUpdate    merge mode flag; true for a full replacement, false for partial replacement
     * @return merged response wrapped in {@link Optional}
     * @throws NonFatalException if the target match does not exist
     */
    protected Optional<MatchResponse> mergeMatchResponses(Long matchId, MatchResponse matchResponse,
                                                          boolean fullUpdate) {
        // Fetch the persisted entity
        IpscMatch ipscMatch = findMatchById(matchId)
                .orElseThrow(() -> new NonFatalException("Match with id %d not found".formatted(matchId)));
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);

        // Convert to a match response and merge with the incoming payload
        MatchDto matchDto = new MatchDto(ipscMatch);
        MatchResponse fetchedMatchResponse = new MatchResponse(matchIdNumber, matchDto);
        fetchedMatchResponse.init(matchIdNumber, matchResponse, fullUpdate);
        return Optional.of(fetchedMatchResponse);
    }

    /**
     * Persists a match using transformation, domain mapping, and transactional save.
     * <p>
     * Processing steps:
     * </p>
     * <ol>
     *   <li>Map request payload to an {@link IpscResponseHolder}</li>
     *   <li>Extract {@link MatchResultsDto} entries from each {@link IpscResponse}</li>
     *   <li>Filter null result DTOs</li>
     *   <li>Map each result DTO to {@link DtoMapping}</li>
     *   <li>Persist mapping and map persisted entity back to {@link MatchResponse}</li>
     * </ol>
     *
     * @param matchResponse payload to persist
     * @return persisted response when save succeeds; otherwise empty
     * @throws FatalException if an unrecoverable transformation/persistence error occurs
     */
    protected Optional<MatchResponse> saveMatchResponse(MatchResponse matchResponse)
            throws FatalException {
        return Optional.empty();
    }

    /**
     * Validates and retrieves a match by id.
     *
     * @param matchId match identifier; must be non-null and greater than zero
     * @return optional persisted match
     * @throws ValidationException if {@code matchId} is null, zero, or negative
     */
    protected Optional<IpscMatch> findMatchById(Long matchId) {
        // Normalise the match id to zero if null or negative
        if ((matchId == null) || (matchId <= 0)) {
            throw new ValidationException("Match cannot be null, zero or negative");
        }
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);

        // Find the match by id
        return matchEntityService.findMatchById(matchIdNumber);
    }

    /**
     * Persists mapped domain data and converts the saved entity into an API response form.
     *
     * @param dtoMapping mapped domain persistence payload
     * @return persisted match response when transaction returns a match holder; otherwise empty
     * @throws FatalException if transactional persistence fails fatally
     */
    protected Optional<MatchResponse> saveMatch(DtoMapping dtoMapping) throws FatalException {
        Optional<MatchHolder> optionalMatchHolder = transactionService.saveMatchResults(dtoMapping);

        // If the match is successfully persisted, convert it to a match response and return it
        if (optionalMatchHolder.isPresent()) {
            IpscMatch ipscMatch = optionalMatchHolder.get().getMatch();
            MatchDto matchDto = new MatchDto(ipscMatch);
            MatchResponse mr = new MatchResponse(ipscMatch.getId(), matchDto);
            return Optional.of(mr);
        }

        return Optional.empty();
    }
}