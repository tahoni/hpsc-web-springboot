package za.co.hpsc.web.models.ipsc.common.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.common.request.ClubRequest;

/**
 * Response DTO that represents club data returned by the API.
 * <p>
 * This model is typically used to expose club identity details to clients after
 * processing a request, and can be constructed directly from a corresponding
 * {@link ClubRequest} instance when values are carried over.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubResponse {
    @NotNull
    private Integer clubId;
    private String clubCode;
    private String clubName;

    /**
     * Creates a {@code ClubResponse} by copying values from a {@link ClubRequest}.
     *
     * @param clubRequest source request DTO containing club fields to map into this response DTO
     */
    public ClubResponse(ClubRequest clubRequest) {
        if (clubRequest != null) {
            this.clubId = clubRequest.getClubId();
            this.clubCode = clubRequest.getClubCode();
            this.clubName = clubRequest.getClubName();
        }
    }

    /**
     * Creates a {@code ClubResponse} with only the required club identifier set.
     * <p>
     * Useful in flows where only the foreign-key/reference ID is needed.
     * </p>
     *
     * @param clubId required club identifier
     */
    public ClubResponse(@NotNull Integer clubId) {
        this.clubId = clubId;
    }
}