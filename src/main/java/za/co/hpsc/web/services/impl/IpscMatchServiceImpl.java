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
    public void insertMatch(MatchResponse matchResponse)
            throws FatalException {
        saveMatchResponse(matchResponse);
    }

    @Override
    public void updateMatch(Long matchId, MatchResponse matchResponse)
            throws FatalException {
        modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchResponse, true);
    }

    @Override
    public void modifyMatch(Long matchId, MatchResponse matchResponse)
            throws FatalException {
        modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchResponse, false);
    }

    @Override
    public List<MatchResponse> getMatches(MatchSearchRequest matchSearchRequest) {
        return List.of();
    }

    @Override
    public Optional<MatchResponse> getMatch(Long matchId) {
        return Optional.empty();
    }

    /**
     * Applies either a full update or partial update to an existing match response and persists it.
     * <p>
     * If no existing match is found for the provided {@code matchId}, the method exits without changes.
     * </p>
     *
     * @param matchId       the target match identifier; {@code null} is treated as zero
     * @param matchResponse the incoming match payload used to update the existing match
     * @param fullUpdate    {@code true} for full replacement semantics, {@code false} for partial merge semantics
     * @throws FatalException if persistence fails during save
     */
    protected void modifyMatchResponse(Long matchId, MatchResponse matchResponse,
                                       boolean fullUpdate)
            throws FatalException {
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);

        // Finds the existing match; if not found, exits without changes
        Optional<IpscMatch> ipscMatch =
                matchEntityService.findMatchById(matchIdNumber);
        if (ipscMatch.isEmpty()) {
            return;
        }

        // Merges the incoming payload with the existing match
        Optional<MatchResponse> optionalMatchResponse = mergeMatchResponses(matchIdNumber, matchResponse,
                fullUpdate);
        if (optionalMatchResponse.isPresent()) {
            saveMatchResponse(optionalMatchResponse.get());
        }
    }

    /**
     * Merges an incoming match payload with the currently persisted match state.
     * <p>
     * The persisted match is first converted into a {@link MatchResponse}, after which
     * {@link MatchResponse#init(MatchResponse, boolean)} is used to apply either full or partial updates.
     * </p>
     *
     * @param matchId       the target match identifier; {@code null} is treated as zero
     * @param matchResponse the incoming payload to merge into the persisted match response
     * @param fullUpdate    {@code true} to overwrite fields (including nulls), {@code false} to patch only non-null fields
     * @return an {@link Optional} containing the merged {@link MatchResponse}, or empty if the match does not exist
     */
    protected Optional<MatchResponse> mergeMatchResponses(Long matchId, MatchResponse matchResponse,
                                                          boolean fullUpdate) {
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);

        // Finds the existing match; if not found, exits without changes
        Optional<IpscMatch> ipscMatch = matchEntityService.findMatchById(matchIdNumber);
        if (ipscMatch.isEmpty()) {
            return Optional.empty();
        }

        // Converts the persisted match to a response and merges the incoming payload
        MatchDto matchDto = new MatchDto(ipscMatch.get());
        MatchResponse fetchedMatchResponse = new MatchResponse(matchIdNumber, matchDto);
        fetchedMatchResponse.init(matchResponse, fullUpdate);
        return Optional.of(fetchedMatchResponse);
    }

    /**
     * Persists a single match payload by running it through transformation, domain mapping,
     * and transactional save steps.
     * <p>
     * Flow:
     * </p>
     * <ol>
     *   <li>Wrap match into IPSC response holder via {@link TransformationService#mapMatchOnly(MatchResponse)}.</li>
     *   <li>Transform responses to {@link MatchResultsDto} instances.</li>
     *   <li>Map DTOs to domain mappings via {@link DomainService#initMatchEntities}.</li>
     *   <li>Persist mappings via {@link TransactionService#saveMatchResults}.</li>
     * </ol>
     *
     * @param matchResponse the match payload to persist
     * @throws FatalException if a fatal error occurs during transformation or persistence
     */
    protected void saveMatchResponse(MatchResponse matchResponse)
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
            return;
        }

        // Filter out null matches
        List<MatchResultsDto> ipscResultsList = matchResultsDtoHolder.getMatches().stream()
                .filter(Objects::nonNull)
                .toList();

        // Iterates the DTOs, maps them to entities, and persists the results
        for (MatchResultsDto matchResultsDto : ipscResultsList) {
            // Maps the DTO to an entity
            Optional<DtoMapping> optionalDtoToEntityMapping =
                    domainService.initMatchEntities(matchResultsDto, null, null);
            if (optionalDtoToEntityMapping.isPresent()) {
                // Persists the entity
                DtoMapping dtoMapping = optionalDtoToEntityMapping.get();
                transactionService.saveMatchResults(dtoMapping);
            }
        }
    }
}