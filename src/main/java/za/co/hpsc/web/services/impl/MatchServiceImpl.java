package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.services.MatchService;

import java.util.Optional;

// TOOD: add tests
@Slf4j
@Service
public class MatchServiceImpl implements MatchService {
    protected final IpscMatchRepository matchRepository;

    public MatchServiceImpl(IpscMatchRepository matchRepository) {
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
}
