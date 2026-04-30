package za.co.hpsc.web.models.ipsc.match.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import za.co.hpsc.web.models.ipsc.common.dto.MatchDto;
import za.co.hpsc.web.models.ipsc.common.response.StageResponse;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;

import java.util.List;

// TODO: add Javadoc
// TODO: add test
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchOnlyResponse extends MatchOnlyRequest {
    private List<StageResponse> stages;

    public MatchOnlyResponse(Long matchId, MatchDto matchDto) {
        super(matchId, matchDto);
    }

    public MatchOnlyResponse(MatchOnlyRequest matchOnlyRequest) {
        super(matchOnlyRequest);
    }

    public void init(Long matchId, MatchOnlyRequest right, boolean fullUpdate) {
        this.setMatchId(matchId);

        if (fullUpdate) {
            this.setMatchName(right.getMatchName());
            this.setMatchDate(right.getMatchDate());

            this.setClub(right.getClub());
            this.setFirearm(right.getFirearm());

            this.setSquadCount(right.getSquadCount());

        } else {
            this.setMatchName((right.getMatchName() != null) ? right.getMatchName() : this.getMatchName());
            this.setMatchDate((right.getMatchDate() != null) ? right.getMatchDate() : this.getMatchDate());

            this.setClub((right.getClub() != null) ? right.getClub() : this.getClub());
            this.setFirearm((right.getFirearm() != null) ? right.getFirearm() : this.getFirearm());

            this.setSquadCount((right.getSquadCount() != null) ? right.getSquadCount() : this.getSquadCount());
        }
    }
}
