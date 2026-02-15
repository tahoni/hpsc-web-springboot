package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.services.MatchEntityService;

import java.util.List;
import java.util.Optional;

// TOOD: add tests
@Slf4j
@Service
public class MatchEntityServiceImpl implements MatchEntityService {
    protected final IpscMatchRepository matchRepository;

    public MatchEntityServiceImpl(IpscMatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Optional<IpscMatch> findMatch(String name) {
        if (name == null) {
            return Optional.empty();
        }

        // Filters matches by name
        return matchRepository.findByName(name);
    }

    @Override
    public List<IpscMatch> findMatches(ClubIdentifier clubIdentifier) {
        // TODO: implement this method to find matches based on the provided club identifier
        return List.of();
    }
}
