package za.co.hpsc.web.services.impl;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResponseHolder;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterMatchResultResponse;
import za.co.hpsc.web.models.ipsc.shooterresult.response.ShooterResultResponse;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.IpscShooterResultService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default {@link IpscShooterResultService}, reading shooter results from the persisted match competitors.
 *
 * @since 15.0.0
 */
@Service
public class IpscShooterResultServiceImpl implements IpscShooterResultService {
    /**
     * Orders shooter results by percentage, highest first, then by points, highest first, with results that have no
     * percentage or points last.
     */
    private static final Comparator<ShooterResultResponse> BY_PERCENTAGE_THEN_POINTS_DESCENDING =
            Comparator.comparing(ShooterResultResponse::getPercentage,
                            Comparator.nullsLast(Comparator.<BigDecimal>reverseOrder()))
                    .thenComparing(ShooterResultResponse::getPoints,
                            Comparator.nullsLast(Comparator.<BigDecimal>reverseOrder()));

    private final MatchCompetitorRepository matchCompetitorRepository;
    private final IpscMatchRepository ipscMatchRepository;

    public IpscShooterResultServiceImpl(MatchCompetitorRepository matchCompetitorRepository,
                                        IpscMatchRepository ipscMatchRepository) {
        this.matchCompetitorRepository = matchCompetitorRepository;
        this.ipscMatchRepository = ipscMatchRepository;
    }

    @Override
    public ShooterResponseHolder getShooterResults(Long matchId) throws NonFatalException {
        IpscMatch match = ipscMatchRepository.findById(matchId)
                .orElseThrow(() -> new NonFatalException("No IPSC match found with ID " + matchId));
        List<ShooterResultResponse> shooterResults =
                matchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch(matchId).stream()
                        .map(ShooterResultResponse::new)
                        .sorted(BY_PERCENTAGE_THEN_POINTS_DESCENDING)
                        .toList();
        return new ShooterResponseHolder(new ShooterMatchResultResponse(match), shooterResults);
    }

    @Override
    public List<ShooterResponseHolder> getAllShooterResults() {
        // Grouped by match ID, so a match without results is still returned, with none
        Map<Long, List<ShooterResultResponse>> shooterResultsByMatchId = new HashMap<>();
        for (MatchCompetitor matchCompetitor : matchCompetitorRepository.findAllWithCompetitorAndMatch()) {
            shooterResultsByMatchId
                    .computeIfAbsent(matchCompetitor.getMatch().getId(), id -> new ArrayList<>())
                    .add(new ShooterResultResponse(matchCompetitor));
        }

        return ipscMatchRepository.findAll(Sort.by("scheduledDate", "id")).stream()
                .map(match -> new ShooterResponseHolder(new ShooterMatchResultResponse(match),
                        shooterResultsByMatchId.getOrDefault(match.getId(), List.of()).stream()
                                .sorted(BY_PERCENTAGE_THEN_POINTS_DESCENDING)
                                .toList()))
                .toList();
    }
}
