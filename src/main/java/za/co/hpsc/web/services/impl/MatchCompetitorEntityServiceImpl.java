package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.MatchCompetitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.repositories.MatchCompetitorRepository;
import za.co.hpsc.web.services.MatchCompetitorEntityService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO: add tests
@Slf4j
@Service
public class MatchCompetitorEntityServiceImpl implements MatchCompetitorEntityService {
    protected final MatchCompetitorRepository matchCompetitorRepository;

    public MatchCompetitorEntityServiceImpl(MatchCompetitorRepository matchCompetitorRepository) {
        this.matchCompetitorRepository = matchCompetitorRepository;
    }

    @Override
    public Optional<MatchCompetitor> findMatchCompetitor(Long matchId, Long competitorId) {
        if ((competitorId == null) || (matchId == null)) {
            return Optional.empty();
        }
        return matchCompetitorRepository.findByCompetitorIdAndMatchId(competitorId, matchId);
    }

    @Override
    public List<MatchCompetitor> findAllMatchCompetitors(Long matchId, String clubName) {
        if ((clubName == null) || (matchId == null)) {
            return new ArrayList<>();
        }

        // TODO: comment
        Optional<ClubIdentifier> optionalClubIdentifier = ClubIdentifier.getByName(clubName);
        if (optionalClubIdentifier.isPresent()) {
            ClubIdentifier clubIdentifier = optionalClubIdentifier.get();
            if (!IpscConstants.EXCLUDE_CLUB_IDENTIFIERS.contains(clubIdentifier)) {
                return matchCompetitorRepository.findAllByMatchIdAndClubName(matchId, clubIdentifier);
            }
        }
        return new ArrayList<>();
    }
}
