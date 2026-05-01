package za.co.hpsc.web.models.ipsc.match.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;

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

    public MatchOnlyRequest(Long matchId, MatchOnlyDto matchOnlyDto) {
        this.matchId = matchId;

        if (matchOnlyDto != null) {
            this.matchName = matchOnlyDto.getName();
            this.matchDate = matchOnlyDto.getScheduledDate();
            this.club = ((matchOnlyDto.getClub() != null) ? matchOnlyDto.getClub().getName() : null);
            this.firearm = ((matchOnlyDto.getMatchFirearmType() != null) ?
                    matchOnlyDto.getMatchFirearmType().getNames().getFirst() : null);
        }
    }

    public MatchOnlyRequest(MatchOnlyRequest right) {
        if (right != null) {
            this.matchId = right.getMatchId();
            this.matchName = right.getMatchName();
            this.matchDate = right.getMatchDate();
            this.club = right.getClub();
            this.firearm = right.getFirearm();
            this.squadCount = right.getSquadCount();
        }
    }
}
