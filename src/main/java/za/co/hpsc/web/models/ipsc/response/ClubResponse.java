package za.co.hpsc.web.models.ipsc.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.request.ClubRequest;

/**
 * Represents a response model for club-related data in the system.
 *
 * <p>
 * This class encapsulates details such as name and other relevant fields. It provides
 * functionality to populate its fields directly from an {@link ClubRequest} object.
 * <.p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubResponse {
    @NotNull
    private Integer clubId = 0;
    private String clubCode = "";
    private String clubName = "";

    /**
     * Constructs a new {@code ClubResponse} object by initialising its fields using the values
     * from a given {@link ClubRequest} object.
     *
     * @param clubRequest the {@link ClubRequest} object containing data to initialise
     *                    the {@code ClubResponse} instance.
     */
    public ClubResponse(ClubRequest clubRequest) {
        this.clubId = clubRequest.getClubId();
        this.clubCode = clubRequest.getClubCode();
        this.clubName = clubRequest.getClubName();
    }

    public ClubResponse(@NotNull Integer clubId) {
        this.clubId = clubId;
    }
}
