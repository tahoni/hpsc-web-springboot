package za.co.hpsc.web.models.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultsDto {
    private MatchDto match;
    private ClubDto club;
    List<CompetitorDto> competitors = new ArrayList<>();
    List<MatchStageDto> stages = new ArrayList<>();
    List<MatchCompetitorDto> matchCompetitors = new ArrayList<>();
    List<MatchStageCompetitorDto> matchStageCompetitors = new ArrayList<>();

    public MatchResultsDto(MatchDto match) {
        this.match = match;
        this.club = match.getClub();
    }
}
