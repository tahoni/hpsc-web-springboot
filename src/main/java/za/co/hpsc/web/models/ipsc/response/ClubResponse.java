package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.ClubRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubResponse {
    @NotNull
    private Integer clubId;
    private String clubCode;
    private String clubName;

    public ClubResponse(ClubRequest clubRequest) {
        this.clubId = clubRequest.getClubId();
        this.clubCode = clubRequest.getClubCode();
        this.clubName = clubRequest.getClubName();
    }

    public ClubResponse(@NotNull Integer clubId) {
        this.clubId = clubId;
    }
}
