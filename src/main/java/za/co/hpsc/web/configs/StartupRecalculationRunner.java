package za.co.hpsc.web.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import za.co.hpsc.web.repositories.ShooterLogOverallRepository;
import za.co.hpsc.web.repositories.ShooterResultRepository;
import za.co.hpsc.web.services.IpscShooterLogOverallService;
import za.co.hpsc.web.services.IpscShooterResultService;

import java.util.List;

/**
 * Recalculates, when the application starts, every match's shooter results and every shooter log's overall results that
 * have a row whose {@code date_calculated} is {@code null}. Such a row was never calculated, or was marked for
 * recalculation by a migration, such as {@code V11_15_0}, after the way results are ranked changed; the shooter result
 * and shooter log overall services treat it as out of date, so calculating ranks it again.
 *
 * <p>
 * A match or shooter log that fails to recalculate is logged and skipped, so one bad record neither stops the others
 * from being recalculated nor prevents the application from starting. It is tried again on the next start, as its rows
 * still have no {@code date_calculated}.
 * </p>
 *
 * @since 18.1.1
 */
@Slf4j
@Component
public class StartupRecalculationRunner implements ApplicationRunner {
    private final ShooterResultRepository shooterResultRepository;
    private final ShooterLogOverallRepository shooterLogOverallRepository;
    private final IpscShooterResultService ipscShooterResultService;
    private final IpscShooterLogOverallService ipscShooterLogOverallService;

    public StartupRecalculationRunner(ShooterResultRepository shooterResultRepository,
                                      ShooterLogOverallRepository shooterLogOverallRepository,
                                      IpscShooterResultService ipscShooterResultService,
                                      IpscShooterLogOverallService ipscShooterLogOverallService) {
        this.shooterResultRepository = shooterResultRepository;
        this.shooterLogOverallRepository = shooterLogOverallRepository;
        this.ipscShooterResultService = ipscShooterResultService;
        this.ipscShooterLogOverallService = ipscShooterLogOverallService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Matches first, as a shooter log's overall results are calculated from its matches' shooter results
        List<Long> matchIds = shooterResultRepository.findMatchIdsWithoutDateCalculated();
        List<Long> shooterLogIds = shooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated();
        if (matchIds.isEmpty() && shooterLogIds.isEmpty()) {
            return;
        }
        log.info("Recalculating the shooter results of {} match(es) and the overall results of {} shooter log(s)",
                matchIds.size(), shooterLogIds.size());

        for (Long matchId : matchIds) {
            try {
                ipscShooterResultService.calculateMatchShooterResult(matchId);
            } catch (RuntimeException e) {
                log.warn("Could not recalculate the shooter results of match {}: {}", matchId, e.getMessage());
            }
        }
        for (Long shooterLogId : shooterLogIds) {
            try {
                ipscShooterLogOverallService.calculateShooterLogOverall(shooterLogId);
            } catch (RuntimeException e) {
                log.warn("Could not recalculate the overall results of shooter log {}: {}", shooterLogId,
                        e.getMessage());
            }
        }
    }
}
