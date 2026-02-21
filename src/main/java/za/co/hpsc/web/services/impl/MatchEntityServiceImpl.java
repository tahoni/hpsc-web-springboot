package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.services.MatchEntityService;

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
    public Optional<IpscMatch> findMatchById(Long id) {
        if ((id == null) || (id == 0L)) {
            return Optional.empty();
        }
        return matchRepository.findById(id);
    }

    @Override
    public Optional<IpscMatch> findMatchByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }
        return matchRepository.findByNameWithClub(name);
    }
}
