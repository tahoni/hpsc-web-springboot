package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.MatchRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    @NotNull
    private Integer matchId = 0;
    private String matchName = "";
    private LocalDateTime matchDate = LocalDate.now().atStartOfDay();
    private Integer clubId = 0;

    private Integer squadCount = 0;
    private Integer firearmId = 0;

    public MatchResponse(MatchRequest match) {
        this.matchId = match.getMatchId();
        this.matchName = match.getMatchName();
        this.matchDate = match.getMatchDate();
        this.clubId = match.getClubId();
        this.squadCount = match.getSquadCount();
        this.firearmId = match.getFirearmId();
    }
}
