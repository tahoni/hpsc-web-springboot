package za.co.hpsc.web.services.impl;

import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.EntityIpscCompetitorService;

import java.util.*;
import java.util.stream.Collectors;

public class EntityIpscCompetitorServiceImpl implements EntityIpscCompetitorService {
    private final CompetitorRepository competitorRepository;

    public EntityIpscCompetitorServiceImpl(CompetitorRepository competitorRepository) {
        this.competitorRepository = competitorRepository;
    }

    @Override
    public Optional<Competitor> findCompetitor(String fullName, int competitorNumber) {
        List<Competitor> competitorsWithCompetitorNumberList = new ArrayList<>();
        List<Competitor> competitorsWithFullNameList = new ArrayList<>();
        List<Competitor> combinedCompetitorList = new ArrayList<>();

        // Normalised competitor number
        String competitorNumberString = String.valueOf(competitorNumber);
        // Normalise full name
        String competitorFullName = fullName.replaceAll(IpscConstants.REPLACE_IN_NAMES_REGEX, "").trim();

        try {
            // First try to match using competitor number (SAPSA or club number)
            if (!IpscConstants.EXCLUDE_ICS_ALIAS.contains(competitorNumberString)) {
                competitorsWithCompetitorNumberList = competitorRepository.findAllByCompetitorNumber(competitorNumberString);
            }
            if (competitorsWithCompetitorNumberList.size() == 1) {
                return Optional.of(competitorsWithCompetitorNumberList.getFirst());
            }

            // Then try to match using exact full name
            if (competitorsWithCompetitorNumberList.isEmpty()) {
                competitorsWithFullNameList = competitorRepository.findAllByFullNameIgnoreCase(competitorFullName);
            } else {
                competitorsWithFullNameList = competitorsWithCompetitorNumberList
                        .stream()
                        .filter( c -> competitorFullName.equalsIgnoreCase(
                                c.getFirstName() + " " + c.getLastName()))
                        .collect(Collectors.toList());
            }
            if (competitorsWithFullNameList.size() == 1) {
                return Optional.of(competitorsWithFullNameList.getFirst());
            }

            // More than one competitor number match or more than one full name match
            Map<String, List<Competitor>> competitorsByCompetitorNumber = competitorsWithCompetitorNumberList
                    .stream()
                    .collect(Collectors.groupingBy(Competitor::getCompetitorNumber));

            Map<String, List<Competitor>> competitorsByFullName = competitorsWithFullNameList
                    .stream()
                    .collect(Collectors.groupingBy(competitor ->
                            (competitor.getFirstName() + " " + competitor.getLastName()).toLowerCase()));

            // Find all competitors that are in the competitor number and full name lists
            combinedCompetitorList.addAll(competitorsWithCompetitorNumberList);
            combinedCompetitorList.retainAll(competitorsWithFullNameList);
            if (combinedCompetitorList.size() == 1) {
                return Optional.of(combinedCompetitorList.getFirst());
            }

            // Find all competitors that are in the competitor full name and number lists
            combinedCompetitorList.clear();
            combinedCompetitorList.addAll(competitorsWithFullNameList);
            combinedCompetitorList.retainAll(competitorsWithCompetitorNumberList);
            if (combinedCompetitorList.size() == 1) {
                return Optional.of(combinedCompetitorList.getFirst());
            }

            return Optional.empty();
        } finally {
            competitorsWithCompetitorNumberList.clear();
            competitorsWithFullNameList.clear();
            combinedCompetitorList.clear();
        }
    }
}
