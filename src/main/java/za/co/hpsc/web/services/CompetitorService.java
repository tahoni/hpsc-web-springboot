package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Competitor;

import java.time.LocalDateTime;
import java.util.Optional;

// TODO: Javadoc
public interface CompetitorService {
    Optional<Competitor> findCompetitor(String icsAlias, String firstName, String lastName,
                                        LocalDateTime dateTimeOfBirth);
}
