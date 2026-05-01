package za.co.hpsc.web.models.ipsc.match.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import za.co.hpsc.web.models.ipsc.common.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.common.response.StageResponse;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;
import za.co.hpsc.web.utils.ValueUtil;

import java.util.List;

// TODO: add Javadoc
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchOnlyResponse extends MatchOnlyRequest {
    private List<StageResponse> stages;

    public MatchOnlyResponse(Long matchId, MatchOnlyDto matchOnlyDto) {
        super(matchId, matchOnlyDto);
    }

    public MatchOnlyResponse(MatchOnlyRequest matchOnlyRequest) {
        super(matchOnlyRequest);
    }

    public MatchOnlyResponse(MatchOnlyDto matchOnlyDto) {
        super();
        if (matchOnlyDto != null) {
            this.setClub(matchOnlyDto.getClubName());
            this.setMatchId(ValueUtil.nullAsDefault(matchOnlyDto.getId(), null));
            this.setMatchName(ValueUtil.nullAsDefaultString(matchOnlyDto.getName(), null));
            this.setMatchDate(ValueUtil.nullAsDefault(matchOnlyDto.getScheduledDate(), null));
            this.setFirearm(ValueUtil.nullAsDefaultString(matchOnlyDto.getMatchFirearmType(), null));
        }
    }

    public MatchOnlyResponse(MatchHolder matchHolder) {
        super();
        if (matchHolder != null) {
            this.setClub((matchHolder.getClub() != null) ? matchHolder.getClub().getName() : null);

            if (matchHolder.getMatch() != null) {
                this.setMatchId(ValueUtil.nullAsDefault(matchHolder.getMatch().getId(), null));
                this.setMatchName(ValueUtil.nullAsDefaultString(matchHolder.getMatch().getName(), null));
                this.setMatchDate(ValueUtil.nullAsDefault(matchHolder.getMatch().getScheduledDate(), null));
                this.setFirearm(ValueUtil.nullAsDefaultString(matchHolder.getMatch().getMatchFirearmType(),
                        null));
            }
        }
    }

    public void init(Long matchId, MatchOnlyRequest right, boolean fullUpdate) {
        this.setMatchId(matchId);

        if (right == null) {
            return;
        }

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
