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
    public void insertMatch(MatchResponse matchResponse) throws FatalException {
        saveMatchResponse(matchResponse);
    }

    @Override
    public void updateMatch(Long matchId, MatchResponse matchResponse) throws FatalException {
        modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchResponse, true);
    }

    @Override
    public void modifyMatch(Long matchId, MatchResponse matchResponse) throws FatalException {
        modifyMatchResponse(ValueUtil.nullAsZero(matchId), matchResponse, false);
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
     * Applies full or partial update semantics to an existing match and persists the merged result.
     * <p>
     * The method delegates merge logic to {@link #mergeMatchResponses(Long, MatchResponse, boolean)}
     * and, when a merged payload is returned, persists it via {@link #saveMatchResponse(MatchResponse)}.
     * </p>
     *
     * @param matchId       identifier of the target match; {@code null} is normalised to {@code 0}
     * @param matchResponse incoming payload used to update persisted state
     * @param fullUpdate    {@code true} for full replacement semantics, {@code false} for partial merge semantics
     * @throws FatalException if persistence/transformation fails while saving the merged match
     */
    protected void modifyMatchResponse(Long matchId, MatchResponse matchResponse,
                                       boolean fullUpdate) throws FatalException {
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);

        Optional<MatchResponse> optionalMatchResponse =
                mergeMatchResponses(matchIdNumber, matchResponse, fullUpdate);

        if (optionalMatchResponse.isPresent()) {
            saveMatchResponse(optionalMatchResponse.get());
        }
    }

    /**
     * Merges an incoming match payload with the persisted match state for the given id.
     * <p>
     * The persisted entity is loaded and converted to {@link MatchResponse}, then merged with
     * the incoming payload via {@link MatchResponse#init(MatchResponse, boolean)}.
     * </p>
     * <p>
     * This implementation throws when the match is not found.
     * </p>
     *
     * @param matchId       identifier of the target match; {@code null} is normalised to {@code 0}
     * @param matchResponse incoming payload to merge into persisted state
     * @param fullUpdate    {@code true} to apply full overwrite behaviour, {@code false} for patch behaviour
     * @return an {@link Optional} containing the merged {@link MatchResponse}
     * @throws FatalException if no persisted match exists for the resolved {@code matchId}
     */
    protected Optional<MatchResponse> mergeMatchResponses(Long matchId, MatchResponse matchResponse,
                                                          boolean fullUpdate) throws FatalException {
        Long matchIdNumber = ValueUtil.nullAsZero(matchId);

        IpscMatch ipscMatch = matchEntityService.findMatchById(matchIdNumber)
                .orElseThrow(() -> new FatalException("Match with id %d not found".formatted(matchIdNumber)));

        MatchDto matchDto = new MatchDto(ipscMatch);
        MatchResponse fetchedMatchResponse = new MatchResponse(matchIdNumber, matchDto);
        fetchedMatchResponse.init(matchResponse, fullUpdate);
        return Optional.of(fetchedMatchResponse);
    }

    /**
     * Persists a match payload through transformation, domain mapping, and transactional save.
     * <p>
     * Processing flow:
     * </p>
     * <ol>
     *   <li>Maps the incoming match payload to an {@link IpscResponseHolder} via
     *       {@link TransformationService#mapMatchOnly(MatchResponse)}.</li>
     *   <li>Transforms each {@link IpscResponse} entry into {@link MatchResultsDto} objects.</li>
     *   <li>Builds {@link MatchResultsDtoHolder}, then filters out null DTO entries.</li>
     *   <li>Maps each DTO to domain data using {@link DomainService#initMatchEntities(MatchResultsDto, String, String)}.</li>
     *   <li>Persists successful mappings using {@link TransactionService#saveMatchResults(DtoMapping)}.</li>
     * </ol>
     * <p>
     * If the holder returns {@code null} for matches, processing exits without persistence.
     * DTOs that cannot be mapped are skipped.
     * </p>
     *
     * @param matchResponse match payload to persist
     * @throws FatalException if a fatal transformation or persistence error occurs
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
}