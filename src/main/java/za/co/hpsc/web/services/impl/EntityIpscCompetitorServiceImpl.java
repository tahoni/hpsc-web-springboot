package za.co.hpsc.web.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.EntityIpscCompetitorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EntityIpscCompetitorServiceImpl implements EntityIpscCompetitorService {
    private final CompetitorRepository competitorRepository;

    public EntityIpscCompetitorServiceImpl(CompetitorRepository competitorRepository) {
        this.competitorRepository = competitorRepository;
    }

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
    @Override
    public Optional<Competitor> findCompetitor(String fullName, String competitorNumber)
            throws ValidationException, NonFatalException {
        // Either the competitor number or full name must be supplied
        if (((fullName == null) || (fullName.isBlank())) &&
                ((competitorNumber == null) || ((competitorNumber.isBlank())))) {
            throw new ValidationException("Full name or competitor number is required");
        }

        // Normalise full name
        String competitorFullName = StringUtils.trimToEmpty(fullName).replaceAll(IpscConstants.REPLACE_IN_NAMES_REGEX, "").trim();
        // Normalise competitor number
        // A numeric value too long for an int (such as an ID number) is not a competitor number
        int competitorNumberInt = 0;
        if (StringUtils.isNumeric(competitorNumber)) {
            competitorNumberInt = org.apache.commons.lang3.math.NumberUtils.toInt(competitorNumber, 0);
        }

        List<Competitor> competitorsWithCompetitorNumberList = new ArrayList<>();
        // First try to match using competitor number (SAPSA or club number)
        if (competitorNumberInt > 0) {
            competitorsWithCompetitorNumberList =
                    IpscConstants.EXCLUDE_ICS_ALIAS.contains(competitorNumberInt)
                            ? List.of()
                            : competitorRepository.findAllByCompetitorNumber(competitorNumberInt);
            if (competitorsWithCompetitorNumberList.size() == 1) {
                return Optional.of(competitorsWithCompetitorNumberList.getFirst());
            }
        }

        // Then try to match using ID number
        List<Competitor> competitorsWithIdNumberList =
                competitorRepository.findAllByIdNumber(competitorNumber);
        if (competitorsWithIdNumberList.size() == 1) {
            return Optional.of(competitorsWithIdNumberList.getFirst());
        }

        // Combine the two lists
        List<Competitor> competitorsMatchWithCompetitorOrIdNumberList = new ArrayList<>();
        competitorsMatchWithCompetitorOrIdNumberList.addAll(competitorsWithCompetitorNumberList);
        competitorsMatchWithCompetitorOrIdNumberList.addAll(competitorsWithIdNumberList);

        // Then try to match using exact full name, narrowed to the number matches when there are any
        List<Competitor> competitorsMatchWithFullNameList = competitorsMatchWithCompetitorOrIdNumberList.isEmpty()
                ? competitorRepository.findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase(competitorFullName)
                : competitorsMatchWithCompetitorOrIdNumberList
                .stream()
                .filter(competitor -> (
                        (competitorFullName.equalsIgnoreCase(competitor.getFirstName() + " " + competitor.getLastName())) ||
                        (competitorFullName.equalsIgnoreCase(competitor.getNickName() + " " + competitor.getLastName()))))
                .toList();
        if (competitorsMatchWithFullNameList.size() == 1) {
            return Optional.of(competitorsMatchWithFullNameList.getFirst());
        }

        if (competitorsMatchWithFullNameList.isEmpty()) {
            throw new NonFatalException("No competitors with the same competitor number or full name were found");
        } else {
            throw new NonFatalException("Two or more competitors with the same competitor number or name were found");
        }
    }
}
