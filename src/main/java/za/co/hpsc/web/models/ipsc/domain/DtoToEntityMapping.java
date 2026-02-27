package za.co.hpsc.web.models.ipsc.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import za.co.hpsc.web.domain.*;
import za.co.hpsc.web.models.ipsc.dto.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class DtoToEntityMapping {
    private DtoMapping dtoMapping;
    private EntityMapping entityMapping;

    public DtoToEntityMapping(DtoMapping dtoToMapping) {
        this.dtoMapping = dtoToMapping;
        this.entityMapping = new EntityMapping();
    }

    public Optional<MatchDto> getMatchDto() {
        return Optional.ofNullable(dtoMapping.getMatch());
    }

    public Optional<IpscMatch> getMatchEntity() {
        return Optional.ofNullable(entityMapping.getMatch());
    }

    public List<CompetitorDto> getCompetitorDtoList() {
        return dtoMapping.getCompetitorMap().values().stream().filter(Objects::nonNull).toList();
    }

    public List<MatchStageDto> getMatchStageDtoList() {
        return dtoMapping.getMatchStageMap().values().stream().filter(Objects::nonNull).toList();
    }

    public List<MatchCompetitorDto> getMatchCompetitorDtoList() {
        return dtoMapping.getMatchCompetitorMap().values().stream().filter(Objects::nonNull).toList();
    }

    public List<MatchStageCompetitorDto> getMatchStageCompetitorDtoList() {
        return dtoMapping.getMatchStageCompetitorMap().values().stream().filter(Objects::nonNull).toList();
    }

    public void setMatch(IpscMatch matchEntity) {
        entityMapping.setMatch(matchEntity);
    }

    public void setCompetitor(CompetitorDto competitorDto, Competitor competitorEntity) {
        UUID competitorUuid = competitorDto.getUuid();
        entityMapping.getCompetitorMap().put(competitorUuid, competitorEntity);
    }

    public void setMatchStage(MatchStageDto matchStageDto, IpscMatchStage matchStageEntity) {
        UUID matchStageUuid = matchStageDto.getUuid();
        entityMapping.getMatchStageMap().put(matchStageUuid, matchStageEntity);
    }

    public void setMatchCompetitor(MatchCompetitorDto matchCompetitorDto,
                                   MatchCompetitor matchCompetitorEntity) {

        IpscMatch match = entityMapping.getMatch();

        UUID competiorUuid = matchCompetitorDto.getCompetitor().getUuid();
        Competitor competitor = entityMapping.getCompetitorMap().get(competiorUuid);

        UUID matchCompetitorUuid = matchCompetitorDto.getUuid();
        matchCompetitorEntity.setMatch(match);
        matchCompetitorEntity.setCompetitor(competitor);

        entityMapping.getMatchCompetitorMap().put(matchCompetitorUuid, matchCompetitorEntity);
    }

    public void setMatchStageCompetitor(MatchStageCompetitorDto matchStageCompetitorDto,
                                        MatchStageCompetitor matchStageCompetitorEntity) {

        UUID competiorUuid = matchStageCompetitorDto.getCompetitor().getUuid();
        UUID matchStageUuid = matchStageCompetitorDto.getMatchStage().getUuid();
        Competitor competitor = entityMapping.getCompetitorMap().get(competiorUuid);
        IpscMatchStage matchStage = entityMapping.getMatchStageMap().get(matchStageUuid);

        matchStageCompetitorEntity.setCompetitor(competitor);
        matchStageCompetitorEntity.setMatchStage(matchStage);
        UUID matchStageCompetitorUuid = matchStageCompetitorDto.getUuid();

        entityMapping.getMatchStageCompetitorMap().put(matchStageCompetitorUuid, matchStageCompetitorEntity);
    }
}
