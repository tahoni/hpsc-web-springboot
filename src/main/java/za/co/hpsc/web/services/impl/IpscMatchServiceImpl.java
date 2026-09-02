package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.enums.FirearmType;
import za.co.hpsc.web.enums.MatchCategory;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.match.request.MatchRequest;
import za.co.hpsc.web.models.ipsc.match.request.MatchStageRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchResponse;
import za.co.hpsc.web.models.ipsc.match.response.MatchStageResponse;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.IpscMatchStageRepository;
import za.co.hpsc.web.services.IpscMatchService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IpscMatchServiceImpl implements IpscMatchService {
    private final IpscMatchRepository ipscMatchRepository;
    private final IpscMatchStageRepository ipscMatchStageRepository;
    private final ClubRepository clubRepository;

    public IpscMatchServiceImpl(IpscMatchRepository ipscMatchRepository,
                                 IpscMatchStageRepository ipscMatchStageRepository,
                                 ClubRepository clubRepository) {
        this.ipscMatchRepository = ipscMatchRepository;
        this.ipscMatchStageRepository = ipscMatchStageRepository;
        this.clubRepository = clubRepository;
    }

    @Override
    @Transactional
    public MatchResponse createMatch(MatchRequest request) {
        validateForCreate(request);

        IpscMatch match = new IpscMatch();
        applyFields(match, request);
        match = ipscMatchRepository.save(match);

        List<IpscMatchStage> stages = replaceStages(match, request.getStages());
        return toResponse(match, stages);
    }

    @Override
    @Transactional
    public MatchResponse updateMatch(Long matchId, MatchRequest request) {
        validateForCreate(request);
        IpscMatch match = findMatchOrThrow(matchId);

        applyFields(match, request);
        match = ipscMatchRepository.save(match);

        List<IpscMatchStage> stages = replaceStages(match, request.getStages());
        return toResponse(match, stages);
    }

    @Override
    @Transactional
    public MatchResponse patchMatch(Long matchId, MatchRequest request) {
        IpscMatch match = findMatchOrThrow(matchId);

        if (request.getClub() != null) {
            match.setClub(resolveClub(request.getClub()));
        }
        if (request.getMatchName() != null) {
            match.setName(request.getMatchName());
        }
        if (request.getMatchDate() != null) {
            match.setScheduledDate(request.getMatchDate().atStartOfDay());
        }
        if (request.getMatchFirearmType() != null) {
            match.setMatchFirearmType(resolveFirearmType(request.getMatchFirearmType()));
        }
        if (request.getMatchCategory() != null) {
            match.setMatchCategory(resolveMatchCategory(request.getMatchCategory()));
        }
        match = ipscMatchRepository.save(match);

        List<IpscMatchStage> stages = (request.getStages() != null)
                ? upsertStages(match, request.getStages())
                : ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(matchId);
        return toResponse(match, stages);
    }

    @Override
    public MatchResponse getMatch(Long matchId) {
        IpscMatch match = findMatchOrThrow(matchId);
        List<IpscMatchStage> stages = ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(matchId);
        return toResponse(match, stages);
    }

    @Override
    public List<MatchResponse> getAllMatches() {
        return ipscMatchRepository.findAll().stream()
                .map(match -> toResponse(match,
                        ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(match.getId())))
                .toList();
    }

    /**
     * Copies the match-level fields of a {@link MatchRequest} onto an {@link IpscMatch},
     * resolving the named club in the process.
     *
     * @param match   the entity to populate; must not be null.
     * @param request the request carrying the field values; must not be null.
     * @throws NonFatalException if the request's club name doesn't match an existing club.
     */
    protected void applyFields(@NotNull IpscMatch match, @NotNull MatchRequest request) {
        match.setClub(resolveClub(request.getClub()));
        match.setName(request.getMatchName());
        match.setScheduledDate(request.getMatchDate().atStartOfDay());
        match.setMatchFirearmType(resolveFirearmType(request.getMatchFirearmType()));
        match.setMatchCategory(resolveMatchCategory(request.getMatchCategory()));
    }

    /**
     * Replaces all of a match's persisted stages with those on the given list.
     *
     * <p>
     * Any stages the match previously had are deleted first, so this is only appropriate for
     * a full create/replace — see {@link #upsertStages} for partial updates.
     * </p>
     *
     * @param match         the match the stages belong to; must not be null and must already
     *                      be persisted.
     * @param stageRequests the stages to persist; may be null or empty, in which case the
     *                      match is simply left with no stages.
     * @return the newly persisted stages, in the order given.
     */
    protected List<IpscMatchStage> replaceStages(@NotNull IpscMatch match, List<MatchStageRequest> stageRequests) {
        // Flushed immediately so the deletes are applied before any replacement stages are
        // inserted — otherwise Hibernate would order the inserts first within the same flush,
        // tripping the (match_id, stage_number) unique constraint on a reused stage number.
        ipscMatchStageRepository.deleteAllInBatch(
                ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(match.getId()));

        if (stageRequests == null) {
            return List.of();
        }

        return stageRequests.stream()
                .map(stageRequest -> {
                    IpscMatchStage stage = new IpscMatchStage();
                    stage.setMatch(match);
                    stage.setStageNumber(stageRequest.getStageNumber());
                    stage.setStageName(stageRequest.getStageName());
                    return ipscMatchStageRepository.save(stage);
                })
                .toList();
    }

    /**
     * Updates or adds stages on a match, matching each request to an existing stage by its
     * stage number. Stages already on the match that aren't mentioned in {@code stageRequests}
     * are left untouched.
     *
     * @param match         the match the stages belong to; must not be null and must already
     *                      be persisted.
     * @param stageRequests the stages to upsert; must not be null.
     * @return all the match's stages after the upsert, ordered by stage number.
     */
    protected List<IpscMatchStage> upsertStages(@NotNull IpscMatch match, @NotNull List<MatchStageRequest> stageRequests) {
        Map<Integer, IpscMatchStage> existingByNumber =
                ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(match.getId()).stream()
                        .collect(Collectors.toMap(IpscMatchStage::getStageNumber, Function.identity()));

        for (MatchStageRequest stageRequest : stageRequests) {
            IpscMatchStage stage = existingByNumber.getOrDefault(stageRequest.getStageNumber(), new IpscMatchStage());
            stage.setMatch(match);
            stage.setStageNumber(stageRequest.getStageNumber());
            if (stageRequest.getStageName() != null) {
                stage.setStageName(stageRequest.getStageName());
            }
            ipscMatchStageRepository.save(stage);
        }

        return ipscMatchStageRepository.findAllByMatchIdOrderByStageNumber(match.getId());
    }

    /**
     * Retrieves an existing match or throws if none exists with the given ID.
     *
     * @param matchId the identifier to look up.
     * @return the matching {@link IpscMatch}.
     * @throws NonFatalException if no match with {@code matchId} exists.
     */
    protected IpscMatch findMatchOrThrow(Long matchId) {
        return ipscMatchRepository.findById(matchId)
                .orElseThrow(() -> new NonFatalException("No IPSC match found with ID " + matchId));
    }

    /**
     * Resolves a club by name.
     *
     * @param clubName the club name to look up.
     * @return the matching {@link Club}.
     * @throws NonFatalException if no club with {@code clubName} exists.
     */
    protected Club resolveClub(String clubName) {
        return clubRepository.findByName(clubName)
                .orElseThrow(() -> new NonFatalException("No club found with name " + clubName));
    }

    /**
     * Resolves a firearm type by name.
     *
     * @param firearmType the firearm type name to look up.
     * @return the matching {@link FirearmType}.
     * @throws ValidationException if no firearm type matches {@code firearmType}.
     */
    protected FirearmType resolveFirearmType(String firearmType) {
        return FirearmType.fromName(firearmType)
                .orElseThrow(() -> new ValidationException("Unknown match firearm type: " + firearmType));
    }

    /**
     * Resolves a match category by name.
     *
     * @param matchCategory the match category name to look up.
     * @return the matching {@link MatchCategory}.
     * @throws ValidationException if no match category matches {@code matchCategory}.
     */
    protected MatchCategory resolveMatchCategory(String matchCategory) {
        return MatchCategory.fromName(matchCategory)
                .orElseThrow(() -> new ValidationException("Unknown match category: " + matchCategory));
    }

    /**
     * Validates that a request carries every field required to create or fully replace a
     * match.
     *
     * @param request the request to validate.
     * @throws ValidationException if a required field is missing.
     */
    protected void validateForCreate(MatchRequest request) {
        if (request == null) {
            throw new ValidationException("Match request cannot be null.");
        }
        if ((request.getMatchName() == null) || request.getMatchName().isBlank()) {
            throw new ValidationException("Match name is required.");
        }
        if (request.getMatchDate() == null) {
            throw new ValidationException("Match date is required.");
        }
        if ((request.getClub() == null) || request.getClub().isBlank()) {
            throw new ValidationException("Club is required.");
        }
        if ((request.getMatchFirearmType() == null) || request.getMatchFirearmType().isBlank()) {
            throw new ValidationException("Match firearm type is required.");
        }
        if ((request.getMatchCategory() == null) || request.getMatchCategory().isBlank()) {
            throw new ValidationException("Match category is required.");
        }
    }

    /**
     * Maps a persisted match and its stages to the response shape returned by the controller.
     *
     * @param match  the match to map.
     * @param stages the match's persisted stages.
     * @return the mapped {@link MatchResponse}.
     */
    protected MatchResponse toResponse(IpscMatch match, List<IpscMatchStage> stages) {
        List<MatchStageResponse> stageResponses = stages.stream()
                .map(stage -> new MatchStageResponse(stage.getId(), stage.getStageNumber(), stage.getStageName()))
                .toList();

        return new MatchResponse(
                match.getId(),
                match.getName(),
                match.getScheduledDate().toLocalDate(),
                ((match.getClub() != null) ? match.getClub().getIdentifier() : null),
                match.getMatchFirearmType(),
                match.getMatchCategory(),
                stageResponses);
    }
}
