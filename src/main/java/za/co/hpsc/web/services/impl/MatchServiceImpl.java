package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.repositories.IpscMatchRepository;
import za.co.hpsc.web.services.MatchService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {
    protected final IpscMatchRepository matchRepository;

    public MatchServiceImpl(IpscMatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Optional<IpscMatch> findMatch(String name, LocalDateTime scheduledDateTime) {
        Optional<IpscMatch> match = Optional.empty();

        // Filters matches by date
        List<IpscMatch> matchList = new ArrayList<>();
        if (scheduledDateTime != null) {
            matchList = matchRepository.findAllByScheduledDate(scheduledDateTime.toLocalDate());
        } else {
            matchList = matchRepository.findAll();
        }

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
