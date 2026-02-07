package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.repositories.MatchRepository;
import za.co.hpsc.web.services.MatchService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {
    protected final MatchRepository matchRepository;

    public MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Optional<Match> findMatch(String name, LocalDateTime scheduledDateTime) {
        Optional<Match> match = Optional.empty();

        // Filters matches by date
        List<Match> matchList = matchRepository.findAllByScheduledDate(scheduledDateTime.toLocalDate());

        // Filters matches by name when present
        if (!name.isBlank()) {
            matchList = matchList.stream()
                    .filter(m -> m.getName().equals(name))
                    .toList();
        }

        match = matchList.stream().findFirst();
        return match;
    }
}
