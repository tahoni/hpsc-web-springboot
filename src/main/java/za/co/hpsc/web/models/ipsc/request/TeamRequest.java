package za.co.hpsc.web.models.ipsc.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequest {
    @NotNull
    private Integer matchId;
    @NotNull
    private Integer teamId;
    private String teamName;

    private Integer divisionId;
    private Integer nonTeamCategoryId;
}
