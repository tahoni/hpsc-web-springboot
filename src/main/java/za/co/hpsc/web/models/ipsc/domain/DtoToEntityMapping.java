package za.co.hpsc.web.models.ipsc.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.dto.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoToEntityMapping {
    private ClubDto club;
    private MatchDto match;

    private Map<UUID, CompetitorDto> competitorMap = new HashMap<>();
    private Map<UUID, MatchStageDto> matchStageMap = new HashMap<>();

    private Map<UUID, MatchCompetitorDto> matchCompetitorMap = new HashMap<>();
    private Map<UUID, MatchStageCompetitorDto> matchStageCompetitorMap = new HashMap<>();
}
