package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Match;
import za.co.hpsc.web.models.ipsc.response.MatchResponse;
import za.co.hpsc.web.repositories.MatchRepository;
import za.co.hpsc.web.services.MatchService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
// TODO: Javadoc
public class MatchServiceImpl implements MatchService {
    protected final MatchRepository matchRepository;

    public MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Optional<Match> findMatch(MatchResponse matchResponse) {
        if (matchResponse == null) {
            return Optional.empty();
        }

        Optional<Match> match = Optional.empty();
        // Filters matches by date
        List<Match> matchList = matchRepository.findAllByScheduledDate(matchResponse.getMatchDate().toLocalDate());
        // Filters matches by name when present
        if (!matchResponse.getMatchName().isBlank()) {
            matchList = matchList.stream()
                    .filter(m -> matchResponse.getMatchName().equals(m.getName()))
                    .toList();
        }
        match = matchList.stream().findFirst();

        return match;
    }
}
