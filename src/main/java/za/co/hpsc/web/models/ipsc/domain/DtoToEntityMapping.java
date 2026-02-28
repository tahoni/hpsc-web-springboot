package za.co.hpsc.web.models.ipsc.domain;

import lombok.Getter;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.dto.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

// TODO: add unit tests
// TODO: add comments
public class DtoToEntityMapping {
    @Getter
    protected final DtoMapping dtoMapping;
    @Getter
    protected final EntityMapping entityMapping;

    public DtoToEntityMapping(DtoMapping dtoToMapping) {
        this.dtoMapping = Objects.requireNonNullElseGet(dtoToMapping, DtoMapping::new);
        this.entityMapping = new EntityMapping();
    }

    public DtoToEntityMapping(DtoMapping dtoToMapping, EntityMapping entityMapping) {
        this.dtoMapping = Objects.requireNonNullElseGet(dtoToMapping, DtoMapping::new);
        this.entityMapping = Objects.requireNonNullElseGet(entityMapping, EntityMapping::new);
    }

    public Optional<MatchDto> getMatchDto() {
        return Optional.ofNullable(dtoMapping.getMatch());
    }

    public Optional<ClubDto> getClubDto() {
        return Optional.ofNullable(dtoMapping.getClub());
    }

    public Optional<IpscMatch> getMatchEntity() {
        return Optional.ofNullable(entityMapping.getMatch());
    }

    public List<CompetitorDto> getCompetitorDtoList() {
        if (dtoMapping.getCompetitorMap() == null) {
            throw new ValidationException("CompetitorDtoList cannot be null");
        }

        return dtoMapping.getCompetitorMap().values().stream().filter(Objects::nonNull).toList();
    }

    public List<MatchStageDto> getMatchStageDtoList() {
        if (dtoMapping.getMatchStageMap() == null) {
            throw new ValidationException("MatchStageDtoList cannot be null");
        }

        return dtoMapping.getMatchStageMap().values().stream().filter(Objects::nonNull).toList();
    }

    public List<MatchCompetitorDto> getMatchCompetitorDtoList() {
        if (dtoMapping.getMatchCompetitorMap() == null) {
            throw new ValidationException("MatchCompetitorDtoList cannot be null");
        }

        return dtoMapping.getMatchCompetitorMap().values().stream().filter(Objects::nonNull).toList();
    }

    public List<MatchStageCompetitorDto> getMatchStageCompetitorDtoList() {
        if (dtoMapping.getMatchStageCompetitorMap() == null) {
            throw new ValidationException("MatchStageCompetitorDtoList cannot be null");
        }

        return dtoMapping.getMatchStageCompetitorMap().values().stream().filter(Objects::nonNull).toList();
    }

    public void setClub(Club clubEntity) {
        if (clubEntity == null) {
            throw new ValidationException("clubEntity cannot be null");
        }
        entityMapping.setClub(clubEntity);
    }

    public void setMatch(IpscMatch matchEntity) {
        if (matchEntity == null) {
            throw new ValidationException("matchEntity cannot be null");
        }

        matchEntity.setClub(entityMapping.getClub());
        entityMapping.setMatch(matchEntity);
    }

    public void setCompetitor(CompetitorDto competitorDto, Competitor competitorEntity) {
        if ((competitorDto == null) || (competitorEntity == null)) {
            throw new ValidationException("competitorDto and competitorEntity cannot be null");
        }

        UUID competitorUuid = competitorDto.getUuid();
        dtoMapping.getCompetitorMap().put(competitorUuid, competitorDto);
        entityMapping.getCompetitorMap().put(competitorUuid, competitorEntity);
    }

    public void setMatchStage(MatchStageDto matchStageDto, IpscMatchStage matchStageEntity) {
        if ((matchStageDto == null) || (matchStageEntity == null)) {
            throw new ValidationException("matchStageDto and matchStageEntity cannot be null");
        }

        UUID matchStageUuid = matchStageDto.getUuid();
        matchStageEntity.setMatch(entityMapping.getMatch());

        dtoMapping.getMatchStageMap().put(matchStageUuid, matchStageDto);
        entityMapping.getMatchStageMap().put(matchStageUuid, matchStageEntity);
    }

    public void setMatchCompetitor(MatchCompetitorDto matchCompetitorDto,
                                   MatchCompetitor matchCompetitorEntity) {

        if ((matchCompetitorDto == null) || (matchCompetitorEntity == null)) {
            throw new ValidationException("MatchCompetitorDto and MatchCompetitorEntity cannot be null");
        }

        IpscMatch match = entityMapping.getMatch();

        UUID competiorUuid = matchCompetitorDto.getCompetitor().getUuid();
        UUID matchCompetitorUuid = matchCompetitorDto.getUuid();
        Competitor competitor = entityMapping.getCompetitorMap().get(competiorUuid);

        matchCompetitorEntity.setMatch(match);
        matchCompetitorEntity.setCompetitor(competitor);

        dtoMapping.getMatchCompetitorMap().put(matchCompetitorUuid, matchCompetitorDto);
        entityMapping.getMatchCompetitorMap().put(matchCompetitorUuid, matchCompetitorEntity);
    }

    public void setMatchStageCompetitor(MatchStageCompetitorDto matchStageCompetitorDto,
                                        MatchStageCompetitor matchStageCompetitorEntity) {

        if ((matchStageCompetitorDto == null) || (matchStageCompetitorEntity == null)) {
            throw new ValidationException("matchStageCompetitorDto and matchStageCompetitorEntity " +
                    "cannot be null");
        }

        UUID competiorUuid = matchStageCompetitorDto.getCompetitor().getUuid();
        UUID matchStageUuid = matchStageCompetitorDto.getMatchStage().getUuid();
        Competitor competitor = entityMapping.getCompetitorMap().get(competiorUuid);
        IpscMatchStage matchStage = entityMapping.getMatchStageMap().get(matchStageUuid);

        matchStageCompetitorEntity.setCompetitor(competitor);
        matchStageCompetitorEntity.setMatchStage(matchStage);

        UUID matchStageCompetitorUuid = matchStageCompetitorDto.getUuid();
        dtoMapping.getMatchStageCompetitorMap().put(matchStageCompetitorUuid, matchStageCompetitorDto);
        entityMapping.getMatchStageCompetitorMap().put(matchStageCompetitorUuid, matchStageCompetitorEntity);
    }
}
