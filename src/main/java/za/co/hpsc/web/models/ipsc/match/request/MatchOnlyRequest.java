package za.co.hpsc.web.models.ipsc.match.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.hpsc.web.models.ipsc.common.dto.MatchDto;

import java.time.LocalDateTime;

// TODO: add Javadoc
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchOnlyRequest {
    @NotNull
    private Long matchId;
    private String matchName;
    @NotNull
    private LocalDateTime matchDate;
    private String club;

    private String firearm;

    private Integer squadCount = 0;

    public MatchOnlyRequest(Long matchId, MatchDto matchDto) {
        this.matchId = matchId;
        this.matchName = matchDto.getName();
        this.matchDate = matchDto.getScheduledDate();
        this.club = ((matchDto.getClub() != null) ? matchDto.getClub().getName() : null);
        this.firearm = ((matchDto.getMatchFirearmType() != null) ?
                matchDto.getMatchFirearmType().getNames().getFirst() : null);
    }

    public MatchOnlyRequest(MatchOnlyRequest right) {
        this.matchId = right.getMatchId();
        this.matchName = right.getMatchName();
        this.matchDate = right.getMatchDate();
        this.club = right.getClub();
        this.firearm = right.getFirearm();
        this.squadCount = right.getSquadCount();
    }
}
