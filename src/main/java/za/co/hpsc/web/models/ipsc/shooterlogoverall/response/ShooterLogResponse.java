package za.co.hpsc.web.models.ipsc.shooterlogoverall.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.ShooterLog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The shooter log that a set of {@link ShooterLogOverallResponse}s belong to, as returned by
 * {@code IpscShooterLogOverallController}.
 *
 * @see ShooterLogOverallResponseHolder
 * @since 16.1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShooterLogResponse {
    /** The identifier of the shooter log. */
    private Long shooterLogId;
    /** The date the shooter log starts on, may be null. */
    private LocalDate startDate;
    /** The date the shooter log ends on, may be null. */
    private LocalDate endDate;
    /** The identifiers of the matches the shooter log covers; empty if it covers none. */
    private List<Long> matchIds = new ArrayList<>();

    /**
     * Creates a shooter log description from a persisted shooter log.
     *
     * @param shooterLog the shooter log to convert. Must not be null. Its lazily loaded matches are read here, so
     *                   call this after fetch-joining them or while the persistence session is still open.
     */
    public ShooterLogResponse(ShooterLog shooterLog) {
        this.shooterLogId = shooterLog.getId();
        this.startDate = shooterLog.getStartDate();
        this.endDate = shooterLog.getEndDate();
        this.matchIds = (shooterLog.getMatches() == null) ? new ArrayList<>()
                : shooterLog.getMatches().stream().map(IpscMatch::getId).toList();
    }
}
