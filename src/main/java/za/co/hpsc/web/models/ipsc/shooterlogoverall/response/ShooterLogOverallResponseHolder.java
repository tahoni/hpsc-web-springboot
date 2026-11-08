package za.co.hpsc.web.models.ipsc.shooterlogoverall.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.models.ipsc.shooterlog.response.ShooterLogResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class linking one shooter log with the {@link ShooterLogOverallResponse}s of all the competitors
 * in it.
 *
 * @see za.co.hpsc.web.controllers.IpscShooterLogOverallController
 * @since 16.1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShooterLogOverallResponseHolder {
    /** The shooter log the overall results belong to. */
    @NotNull
    private ShooterLogResponse shooterLog;
    /** The overall results of the competitors in the shooter log; empty if none has been calculated. */
    @NotNull
    private List<ShooterLogOverallResponse> shooterLogOverallResults = new ArrayList<>();
}
