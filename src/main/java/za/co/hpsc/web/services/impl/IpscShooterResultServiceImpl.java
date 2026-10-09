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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default {@link IpscShooterResultService}, reading shooter results from the persisted match competitors.
 *
 * @since 15.0.0
 */
// TODO: sort match competitors
@Service
public class IpscShooterResultServiceImpl implements IpscShooterResultService {
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
                        shooterResultsByMatchId.getOrDefault(match.getId(), List.of())))
                .toList();
    }
}
