package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.enums.ClubReference;
import za.co.hpsc.web.models.ipsc.domain.MatchEntityHolder;
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.repositories.*;
import za.co.hpsc.web.services.IpscMatchDomainService;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

// TODO: Javadoc
@Slf4j
@Service
public class IpscMatchDomainServiceImpl implements IpscMatchDomainService {
    protected final ClubRepository clubRepository;
    protected final CompetitorRepository competitorRepository;
    protected final IpscMatchRepository ipscMatchRepository;
    protected final IpscMatchStageRepository ipscMatchStageRepository;
    protected final MatchCompetitorRepository matchCompetitorRepository;
    protected final MatchStageCompetitorRepository matchStageCompetitorRepository;

    public IpscMatchDomainServiceImpl(ClubRepository clubRepository,
                                      CompetitorRepository competitorRepository,
                                      IpscMatchRepository ipscMatchRepository,
                                      IpscMatchStageRepository ipscMatchStageRepository,
                                      MatchCompetitorRepository matchCompetitorRepository,
                                      MatchStageCompetitorRepository matchStageCompetitorRepository) {
        this.clubRepository = clubRepository;
        this.ipscMatchRepository = ipscMatchRepository;
        this.competitorRepository = competitorRepository;
        this.ipscMatchStageRepository = ipscMatchStageRepository;
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.matchStageCompetitorRepository = matchStageCompetitorRepository;
    }

    @Override
    public Optional<MatchEntityHolder> initMatchEntities(@NotNull MatchResultsDto matchResults) {
        if (matchResults.getMatch() == null) {
            return Optional.empty();
        }

        AtomicReference<Optional<MatchEntityHolder>> optionalMatchEntityHolder =
                new AtomicReference<>(Optional.empty());

        Club club = initClubEntity(matchResults.getClub()).orElse(null);
        Optional<IpscMatch> optionalMatch = initMatchEntity(matchResults.getMatch(), club);

        optionalMatch.ifPresent(match -> {
            Map<UUID, Competitor> competitorMap = initCompetitorEntities(matchResults.getCompetitors());
            Map<UUID, IpscMatchStage> matchStageMap = initMatchStageEntities(matchResults.getStages(), match);

            Map<UUID, MatchCompetitor> matchCompetitorMap =
                    initMatchCompetitorEntities(matchResults.getMatchCompetitors(),
                            match,
                            competitorMap, ClubReference.HPSC);
            Map<UUID, MatchStageCompetitor> matchStageCompetitorMap =
                    initMatchStageCompetitorEntities(matchResults.getMatchStageCompetitors(),
                            matchStageMap, competitorMap, ClubReference.HPSC);

            optionalMatchEntityHolder.set(Optional.of(new MatchEntityHolder(match, club,
                    matchStageMap.values().stream().toList(),
                    competitorMap.values().stream().toList(),
                    matchCompetitorMap.values().stream().toList(),
                    matchStageCompetitorMap.values().stream().toList())));
        });

        return optionalMatchEntityHolder.get();
    }

    /**
     *
     * @param clubDto
     */
    protected Optional<Club> initClubEntity(ClubDto clubDto) {
        if (clubDto != null) {
            // Find the club entity if present
            Optional<Club> optionalClubEntity = Optional.empty();
            if (clubDto.getId() != null) {
                optionalClubEntity = clubRepository.findById(clubDto.getId());
            }

            // Add attributes to the club
            optionalClubEntity.ifPresent(clubEntity -> clubEntity.init(clubDto));
            return optionalClubEntity;
        }

        return Optional.empty();
    }

    /**
     *
     * @param matchDto
     * @param clubEntity
     * @return
     */
    protected Optional<IpscMatch> initMatchEntity(@NotNull MatchDto matchDto, Club clubEntity) {
        // Find the match entity if present
        Optional<IpscMatch> optionalIpscMatchEntity = Optional.empty();
        if (matchDto.getId() != null) {
            optionalIpscMatchEntity = ipscMatchRepository.findById(matchDto.getId());
        }

        // Initialise the match entity from DTO or create a new entity
        IpscMatch matchEntity = optionalIpscMatchEntity.orElse(new IpscMatch());
        // Add attributes to the match
        matchEntity.init(matchDto, clubEntity);
        // Link the match to the stage
        matchEntity.setClub(clubEntity);

        return Optional.of(matchEntity);
    }

    /**
     *
     * @param competitorDtoList
     */
    protected Map<UUID, Competitor> initCompetitorEntities(List<CompetitorDto> competitorDtoList) {
        Map<UUID, Competitor> competitorMap = new HashMap<>();
        if (competitorDtoList != null) {

            // Initialise and accumulate competitor entities from DTOs
            competitorDtoList.forEach(competitorDto -> {
                // Find the competitor entity if present
                Optional<Competitor> optionalCompetitorEntity = Optional.empty();
                if (competitorDto.getId() != null) {
                    optionalCompetitorEntity = competitorRepository.findById(competitorDto.getId());
                }

                // Initialise the competitor entity from DTO or create a new entity
                Competitor competitorEntity = optionalCompetitorEntity.orElse(new Competitor());
                // Add attributes to the competitor
                competitorEntity.init(competitorDto);

                // Update the map of competitors
                competitorMap.put(competitorDto.getUuid(), competitorEntity);
            });
        }

        return competitorMap;
    }

    /**
     *
     * @param matchStageDtoList
     * @param matchEntity
     * @return
     */
    protected Map<UUID, IpscMatchStage> initMatchStageEntities(List<MatchStageDto> matchStageDtoList,
                                                               @NotNull IpscMatch matchEntity) {
        Map<UUID, IpscMatchStage> matchStageMap = new HashMap<>();
        if (matchStageDtoList != null) {

            // Initialise and accumulate match stages from DTOs
            matchStageDtoList.forEach(stage -> {
                // Find the match stage entity if present
                Optional<IpscMatchStage> optionalIpscMatchStageEntity = Optional.empty();
                if (stage.getId() != null) {
                    optionalIpscMatchStageEntity = ipscMatchStageRepository.findById(stage.getId());
                }

                // Initialise the match stage entity from DTO or create a new entity
                IpscMatchStage matchStageEntity = optionalIpscMatchStageEntity.orElse(new IpscMatchStage());
                // Add attributes to the match stage
                matchStageEntity.init(stage, matchEntity);
                // Link the match stage to the match
                matchStageEntity.setMatch(matchEntity);

                // Update the map of match stages
                matchStageMap.put(stage.getUuid(), matchStageEntity);
            });
        }

        return matchStageMap;
    }

    /**
     *
     * @param matchCompetitors
     * @param matchEntity
     * @param competitorMap
     * @param clubReference
     * @return
     */
    protected Map<UUID, MatchCompetitor> initMatchCompetitorEntities(List<MatchCompetitorDto> matchCompetitors,
                                                                     IpscMatch matchEntity,
                                                                     Map<UUID, Competitor> competitorMap,
                                                                     ClubReference clubReference) {

        Map<UUID, MatchCompetitor> matchCompetitorMap = new HashMap<>();
        if (matchCompetitors != null) {

            // Initialise and accumulate match competitors from DTOs
            for (MatchCompetitorDto matchCompetitorDto : matchCompetitors) {
                // Find the competitor entity
                Competitor competitorEntity = competitorMap.get(matchCompetitorDto.getCompetitor().getUuid());
                if (competitorEntity == null) {
                    return new HashMap<>();
                }

                // Find the match competitor entity if present
                Optional<MatchCompetitor> optionalMatchCompetitorEntity = Optional.empty();
                if (matchCompetitorDto.getId() != null) {
                    optionalMatchCompetitorEntity =
                            matchCompetitorRepository.findById(matchCompetitorDto.getId());
                }

                // Initialise the match competitor entity from DTO or create a new entity
                MatchCompetitor matchCompetitorEntity = optionalMatchCompetitorEntity
                        .orElse(new MatchCompetitor());

                // Filter by club reference if specified
                if ((clubReference != null) && (!clubReference.equals(ClubReference.UNKNOWN))) {
                    if (!clubReference.equals(matchCompetitorDto.getClub())) {
                        continue;
                    }
                }

                // Add attributes to the match competitor
                matchCompetitorEntity.init(matchCompetitorDto, matchEntity, competitorEntity);
                // Link the match competitor to the match and competitor
                matchCompetitorEntity.setMatch(matchEntity);
                matchCompetitorEntity.setCompetitor(competitorEntity);

                // Update the map of match competitors
                matchCompetitorMap.put(matchCompetitorDto.getUuid(), matchCompetitorEntity);
            }
        }

        return matchCompetitorMap;
    }

    /**
     *
     * @param matchStageCompetitors
     * @param matchStageMap
     * @param competitorMap
     * @param clubReference
     */
    // TODO: add tests
    protected Map<UUID, MatchStageCompetitor> initMatchStageCompetitorEntities(List<MatchStageCompetitorDto> matchStageCompetitors,
                                                                               Map<UUID, IpscMatchStage> matchStageMap, Map<UUID, Competitor> competitorMap,
                                                                               ClubReference clubReference) {

        Map<UUID, MatchStageCompetitor> matchStageCompetitorMap = new HashMap<>();
        if (matchStageCompetitors != null) {
            // Initialises and accumulates match stage competitors from DTOs
            for (MatchStageCompetitorDto matchStageCompetitorDto : matchStageCompetitors) {
                // Find the competitor entity
                Competitor competitorEntity = competitorMap.get(matchStageCompetitorDto.getCompetitor().getUuid());
                if (competitorEntity == null) {
                    return new HashMap<>();
                }

                // Find the match stage entity
                IpscMatchStage matchStageEntity =
                        matchStageMap.get(matchStageCompetitorDto.getMatchStage().getUuid());

                // Find the match stage competitor entity if present
                Optional<MatchStageCompetitor> optionalMatchStageEntity = Optional.empty();
                if (matchStageCompetitorDto.getId() != null) {
                    optionalMatchStageEntity =
                            matchStageCompetitorRepository.findById(matchStageCompetitorDto.getId());
                }

                // Initialises the match stage competitor entity from DTO or create a new entity
                MatchStageCompetitor matchStageCompetitorEntity = optionalMatchStageEntity
                        .orElse(new MatchStageCompetitor());

                // Filter by club reference if specified
                if ((clubReference != null) && (!clubReference.equals(ClubReference.UNKNOWN))) {
                    if (!clubReference.equals(matchStageCompetitorDto.getClub())) {
                        continue;
                    }
                }

                // Add attributes to the match stage competitor
                matchStageCompetitorEntity.init(matchStageCompetitorDto, matchStageEntity, competitorEntity);
                // Link the match stage competitor to the match stage and competitor
                matchStageCompetitorEntity.setMatchStage(matchStageEntity);
                matchStageCompetitorEntity.setCompetitor(competitorEntity);

                // Update the map of match stage competitors
                matchStageCompetitorMap.put(matchStageCompetitorDto.getUuid(), matchStageCompetitorEntity);
            }
        }

        return matchStageCompetitorMap;
    }
}
