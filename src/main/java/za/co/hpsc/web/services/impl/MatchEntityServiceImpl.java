package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.services.MatchEntityService;

import java.time.LocalDateTime;
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
    public Optional<IpscMatch> findMatchByNameAndScheduledDate(String name, LocalDateTime scheduledDateTime) {
        if (name == null) {
            return Optional.empty();
        }
        List<IpscMatch> matchList = matchRepository.findAllByName(name);

        // No match found
        if (matchList.isEmpty()) {
            return Optional.empty();
        }

        // Exactly one match found
        if (matchList.size() == 1) {
            return matchList.stream().findFirst();
        }

        // More than one match found, filter by scheduled date
        if (scheduledDateTime == null) {
            return matchList.stream().findFirst();
        }
        return matchList.stream().filter(m -> m.getScheduledDate().equals(scheduledDateTime)).findFirst();
    }
}
