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
    public Optional<IpscMatch> findMatchByName(String name) {
        if ((name == null) || (name.isBlank())) {
            return Optional.empty();
        }
        return matchRepository.findByNameWithClubAndStages(name);
    }

    @Override
    public Optional<IpscMatch> findMatchWithCompetitors(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return matchRepository.findByIdWithCompetitors(id);
    }

    @Override
    public Optional<IpscMatch> findMatchWithStages(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return matchRepository.findByIdWithStages(id);
    }
}
