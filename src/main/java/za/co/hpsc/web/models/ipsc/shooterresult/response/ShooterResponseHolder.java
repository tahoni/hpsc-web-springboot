package za.co.hpsc.web.models.ipsc.shooterresult.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class linking one match with the {@link ShooterResultResponse}s of all the competitors who shot it.
 *
 * @see za.co.hpsc.web.controllers.IpscShooterResultController
 * @since 15.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShooterResponseHolder {
    /** The match the results were shot in. */
    @NotNull
    private ShooterMatchResultResponse match;
    /** The results of the competitors who shot the match; empty if nobody has a result in it. */
    @NotNull
    private List<ShooterResultResponse> shooterResults = new ArrayList<>();
}
