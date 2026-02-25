package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.models.ipsc.response.ClubResponse;
import za.co.hpsc.web.models.ipsc.response.IpscResponse;
import za.co.hpsc.web.models.ipsc.response.ScoreResponse;
import za.co.hpsc.web.models.ipsc.response.StageResponse;
import za.co.hpsc.web.services.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class IpscMatchResultServiceImpl implements IpscMatchResultService {
    protected final ClubEntityService clubEntityService;
    protected final MatchEntityService matchEntityService;
    protected final CompetitorEntityService competitorEntityService;
    protected final MatchStageEntityService matchStageEntityService;
    protected final MatchCompetitorEntityService matchCompetitorEntityService;
    protected final MatchStageCompetitorEntityService matchStageCompetitorEntityService;

    @Value("${hpsc-web.results.init-match-result.ignore-updated-date:false}")
    private Boolean ignoreDateUpdated;

    public IpscMatchResultServiceImpl(ClubEntityService clubEntityService,
                                      MatchEntityService matchEntityService,
                                      CompetitorEntityService competitorEntityService,
                                      MatchStageEntityService matchStageEntityService,
                                      MatchCompetitorEntityService matchCompetitorEntityService,
                                      MatchStageCompetitorEntityService matchStageCompetitorEntityService) {

        this.clubEntityService = clubEntityService;
        this.matchEntityService = matchEntityService;
        this.competitorEntityService = competitorEntityService;
        this.matchStageEntityService = matchStageEntityService;
        this.matchCompetitorEntityService = matchCompetitorEntityService;
        this.matchStageCompetitorEntityService = matchStageCompetitorEntityService;
    }

    @Override
    public Optional<MatchResultsDto> initMatchResults(IpscResponse ipscResponse) {
        if (ipscResponse == null) {
            return Optional.empty();
        }

        // Initialises club and match
        Optional<ClubDto> optionalClub = initClub(ipscResponse.getClub());
        Optional<MatchDto> optionalMatch = initMatch(ipscResponse, optionalClub.orElse(null));
        if (optionalMatch.isEmpty()) {
            return Optional.empty();
        }

        // Initialises match details
        MatchDto match = optionalMatch.get();
        MatchResultsDto matchResultsDto = new MatchResultsDto(match);
        matchResultsDto.setStages(initStages(match, ipscResponse.getStages()));

        // Initialises match results
        initScores(matchResultsDto, ipscResponse);

        return Optional.of(matchResultsDto);
    }

    /**
     * Initialises a {@link ClubDto} object based on the provided club response.
     *
     * <p>
     * If a matching club entity with the same name and abbreviation is found in the database,`
     * it is used to populate the DTO; otherwise, a new DTO is created and initialised
     * using the club response.
     * </p>
     *
     * @param clubResponse the response containing club information, which may be used to initialise the DTO.
     *                     If null, the method will return an empty optional.
     * @return an {@code Optional} containing the initialized {@link ClubDto}, or {@code Optional.empty}
     * if the provided clubResponse is null.
     */
    protected Optional<ClubDto> initClub(ClubResponse clubResponse) {
        if (clubResponse == null) {
            return Optional.empty();
        }

        // Attempts to find the club by name or abbreviation in the database
        Optional<Club> optionalClub =
                clubEntityService.findClubByNameOrAbbreviation(clubResponse.getClubName(),
                        clubResponse.getClubCode());

        // Creates a new club DTO, from either the found entity or the match response
        Club club = optionalClub.orElse(null);
        ClubDto clubDto = ((club != null) ? new ClubDto(club) : new ClubDto());
        clubDto.init(clubResponse);

        return Optional.of(clubDto);
    }

    /**
     * Initialises a {@link MatchDto} object based on the provided IPSC response data
     * and club information.
     *
     * <p>The method checks if the match exists in the database, determines whether the response contains
     * newer scores, and skips updates if applicable. If the match is valid, it creates or updates the
     * match DTO and initialises its attributes.
     * </p>
     *
     * @param ipscResponse The response object containing match details and scores from the IPSC system.
     * @param clubDto      The club data transfer object containing information about the club associated with the match.
     * @return An {@code Optional} containing the initialised {@link MatchDto} if the match is valid
     * and meets the update criteria, or {@code Optional.empty()} if no match is initialised.
     */
    protected Optional<MatchDto> initMatch(IpscResponse ipscResponse, ClubDto clubDto) {
        if ((ipscResponse == null) || (ipscResponse.getMatch() == null)) {
            return Optional.empty();
        }

        // Attempts to find the match by name in the database
        Optional<IpscMatch> optionalMatch =
                matchEntityService.findMatchByName(ipscResponse.getMatch().getMatchName());
        boolean ipscMatchExists = optionalMatch.isPresent();
        boolean ipscResponseHasNewerScore = false;

        // Determines the last updated date of the match
        LocalDateTime matchLastUpdated = (optionalMatch.isPresent() ?
                optionalMatch.get().getDateUpdated() : LocalDateTime.now());

        // Skips update if there are no newer scores in the IPSC response
        boolean ignoreMatchDateUpdated = ((ignoreDateUpdated != null ? ignoreDateUpdated : false));
        if ((!ignoreMatchDateUpdated) && (ipscMatchExists)) {
            ipscResponseHasNewerScore = ipscResponse.getScores().stream()
                    .filter(Objects::nonNull)
                    .anyMatch(sr -> matchLastUpdated.isBefore(sr.getLastModified()));
            if (!ipscResponseHasNewerScore) {
                return Optional.empty();
            }
        }

        // Creates a new match DTO, from either the found entity or the match response
        MatchDto matchDto = optionalMatch.map(MatchDto::new).orElseGet(MatchDto::new);

        // Initialises match attributes
        matchDto.init(ipscResponse.getMatch(), clubDto, ipscResponse.getScores());

        return Optional.of(matchDto);
    }

    /**
     * Initialises a list of {@link MatchStageDto} objects based on the given match details
     * and stage response data. If no stage responses are provided, an empty list is returned.
     *
     * @param matchDto       the match details.
     * @param stageResponses a list of stage response objects.
     * @return a list of initialised {@link MatchStageDto} objects based on the input parameters.
     */
    protected List<MatchStageDto> initStages(MatchDto matchDto, List<StageResponse> stageResponses) {
        if ((matchDto == null) || (stageResponses == null)) {
            return new ArrayList<>();
        }

        List<MatchStageDto> matchStageDtoList = new ArrayList<>();
        // Iterates through each stage response
        stageResponses.stream().filter(Objects::nonNull)
                .forEach(stageResponse -> {
                    // Attempts to find the match stage by match ID and stage ID in the database
                    Optional<IpscMatchStage> optionalMatchStage = matchStageEntityService.findMatchStage(matchDto.getId(),
                            stageResponse.getStageId());
                    // Creates a new stage DTO, from either the found entity or the stage response
                    MatchStageDto matchStageDto = optionalMatchStage
                            .map(ms -> new MatchStageDto(ms, matchDto))
                            .orElseGet(MatchStageDto::new);

                    // Initialises stage attributes
                    matchStageDto.init(matchDto, stageResponse);
                    matchStageDtoList.add(matchStageDto);
                });

        return matchStageDtoList;
    }

    /**
     * Initialises the scores and competitors information for a given match results DTO
     * based on the data from the IPSC response.
     *
     * <p>
     * It processes score and member responses, matches them to existing competitors or creates new
     * ones, and updates the match results DTO with the complete competitor and scoring information.
     * </p>
     *
     * @param matchResultsDto the data transfer object for match results to be initialised with
     *                        scores and competitors.
     * @param ipscResponse    the response containing score and member data from an IPSC source.
     */
    protected void initScores(@NotNull MatchResultsDto matchResultsDto, IpscResponse ipscResponse) {
        if ((ipscResponse == null) || (ipscResponse.getScores() == null) || (ipscResponse.getMembers() == null)) {
            return;
        }

        List<ScoreResponse> scoreResponses = ipscResponse.getScores().stream()
                .filter(Objects::nonNull)
                .filter(scoreResponse -> Objects.equals(scoreResponse.getMatchId(), matchResultsDto.getMatch().getIndex()))
                .toList();
        List<ScoreDto> scoreDtos = scoreResponses.stream()
                .filter(Objects::nonNull)
                .map(ScoreDto::new)
                .toList();
        matchResultsDto.setScores(scoreDtos);
    }
}
