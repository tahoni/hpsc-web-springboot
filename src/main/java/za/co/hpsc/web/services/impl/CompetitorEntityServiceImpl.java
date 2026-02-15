package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.CompetitorEntityService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO: add tests
@Slf4j
@Service
public class CompetitorEntityServiceImpl implements CompetitorEntityService {
    protected final CompetitorRepository competitorRepository;

    public CompetitorEntityServiceImpl(CompetitorRepository competitorRepository) {
        this.competitorRepository = competitorRepository;
    }

    @Override
    public Optional<Competitor> findCompetitor(String icsAlias, String firstName, String lastName,
                                               LocalDateTime dateTimeOfBirth) {

        Optional<Competitor> competitor = Optional.empty();

        // Attempts competitor lookup by SAPSA number or alias
        if ((icsAlias != null) && (!icsAlias.isBlank()) && (NumberUtils.isCreatable((icsAlias)))) {
            Integer sapsaNumber = Integer.parseInt(icsAlias);
            if (!IpscConstants.EXCLUDE_ICS_ALIAS.contains(sapsaNumber)) {
                competitor = competitorRepository.findBySapsaNumber(sapsaNumber);
            }
        }

        // If the competitor was not found
        if (competitor.isEmpty()) {
            List<Competitor> competitorList = new ArrayList<>();
            // Attempt to find the competitor by first and last name
            competitorList = competitorRepository.findAllByFirstNameAndLastName(firstName, lastName);
            if (competitorList.isEmpty()) {
                return Optional.empty();
            }

            // Filters list by date of birth if present
            List<Competitor> filteredCompetitorList = new ArrayList<>();
            if (dateTimeOfBirth != null) {
                // Filters list to matching dates of birth
                filteredCompetitorList = competitorList.stream()
                        .filter(c -> dateTimeOfBirth.toLocalDate().equals(c.getDateOfBirth()))
                        .toList();
            }

            // If no matching dates of birth were found, return the first matching competitor
            if (filteredCompetitorList.isEmpty()) {
                return competitorList.stream().findFirst();
            }

            // Finds the first matching competitor without a SAPSA number
            Optional<Competitor> filteredCompetitor = competitorList.stream()
                    .filter(c -> c.getSapsaNumber() == null)
                    .findFirst();
            if (filteredCompetitor.isEmpty()) {
                // Finds the first matching competitor with a SAPSA number
                return filteredCompetitorList.stream().findFirst();
            }
        }

        return competitor;
    }
}
