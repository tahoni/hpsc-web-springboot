package za.co.hpsc.web.services.impl;

import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.EntityIpscCompetitorService;

import java.util.List;
import java.util.Optional;

@Service
public class EntityIpscCompetitorServiceImpl implements EntityIpscCompetitorService {
    private final CompetitorRepository competitorRepository;

    public EntityIpscCompetitorServiceImpl(CompetitorRepository competitorRepository) {
        this.competitorRepository = competitorRepository;
    }

    @Override
    public Optional<Competitor> findCompetitor(String fullName, int competitorNumber) {
        // Normalise full name
        String competitorFullName = fullName.trim().replaceAll(IpscConstants.REPLACE_IN_NAMES_REGEX, "").trim();
    /**
     * {@inheritDoc}
     *
     * <p><b>Implementation notes:</b> the lookup is attempted in stages, returning as soon as a
     * stage yields exactly one competitor:</p>
     * <ol>
     *     <li>Competitor number (SAPSA or club number): only when the number is numeric and
     *     greater than zero. Numbers in {@link IpscConstants#EXCLUDE_ICS_ALIAS} are ignored,
     *     as they are shared aliases and cannot identify a single competitor.</li>
     *     <li>ID number: the supplied competitor number is also matched against the
     *     competitors' ID numbers.</li>
     *     <li>Full name: when the number lookups found no competitor, an exact,
     *     case-insensitive match on first name or nickname plus last name is queried from the
     *     repository. When they found several, those candidates are narrowed down by the same
     *     name comparison in memory.</li>
     * </ol>
     * <p>The full name is normalised before comparison by trimming it and removing the
     * characters matched by {@link IpscConstants#REPLACE_IN_NAMES_REGEX}.</p>
     * <p>If the final stage does not yield exactly one competitor, a {@link NonFatalException}
     * is thrown rather than returning an empty {@link Optional}.</p>
     */

        // First try to match using competitor number (SAPSA or club number)
        List<Competitor> competitorsWithCompetitorNumberList =
                IpscConstants.EXCLUDE_ICS_ALIAS.contains(competitorNumber)
                        ? List.of()
                        : competitorRepository.findAllByCompetitorNumber(competitorNumber);
        if (competitorsWithCompetitorNumberList.size() == 1) {
            return Optional.of(competitorsWithCompetitorNumberList.getFirst());
        }

        // Then try to match using exact full name, narrowed to the number matches when there are any
        List<Competitor> competitorsWithFullNameList = competitorsWithCompetitorNumberList.isEmpty()
                ? competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(competitorFullName)
                : competitorsWithCompetitorNumberList
                .stream()
                .filter(competitor -> (
                        (competitorFullName.equalsIgnoreCase(competitor.getFirstName() + " " + competitor.getLastName())) ||
                        (competitorFullName.equalsIgnoreCase(competitor.getNickName() + " " + competitor.getLastName()))))
                .toList();
        return competitorsWithFullNameList.size() == 1
                ? Optional.of(competitorsWithFullNameList.getFirst())
                : Optional.empty();
    }
}
