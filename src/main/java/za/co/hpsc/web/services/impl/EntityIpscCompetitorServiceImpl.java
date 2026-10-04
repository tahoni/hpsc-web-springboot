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
        // Normalised competitor number
        String competitorNumberString = String.valueOf(competitorNumber);
        // Normalise full name
        String competitorFullName = fullName.trim().replaceAll(IpscConstants.REPLACE_IN_NAMES_REGEX, "").trim();

        // First try to match using competitor number (SAPSA or club number)
        List<Competitor> competitorsWithCompetitorNumberList =
                IpscConstants.EXCLUDE_ICS_ALIAS.contains(competitorNumberString)
                        ? List.of()
                        : competitorRepository.findAllByCompetitorNumber(competitorNumberString);
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
                        (competitorFullName.equalsIgnoreCase(competitor.getNickname() + " " + competitor.getLastName()))))
                .toList();
        return competitorsWithFullNameList.size() == 1
                ? Optional.of(competitorsWithFullNameList.getFirst())
                : Optional.empty();
    }
}
