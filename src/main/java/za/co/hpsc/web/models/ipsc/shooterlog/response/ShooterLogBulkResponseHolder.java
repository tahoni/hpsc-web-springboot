package za.co.hpsc.web.models.ipsc.shooterlog.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A container class holding the {@link ShooterLogBulkResponse}s of a bulk shooter log import, one per imported row,
 * each recording whether that row succeeded.
 *
 * @see ShooterLogBulkResponse
 * @since 16.3.0
 */
@Getter
@Setter
@AllArgsConstructor
public class ShooterLogBulkResponseHolder {
    /** The result of each imported shooter log, in the same order as the import. */
    @NotNull
    private List<ShooterLogBulkResponse> shooterLogResults;
}
