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
import java.util.Objects;
import java.util.Optional;

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

        List<Competitor> competitorList = new ArrayList<>();

        // Attempts competitor lookup by SAPSA number or alias
        if ((icsAlias != null) && (!icsAlias.isBlank()) && (NumberUtils.isCreatable((icsAlias)))) {
            Integer sapsaNumber = Integer.parseInt(icsAlias);
            if (!IpscConstants.EXCLUDE_ICS_ALIAS.contains(icsAlias)) {
                competitorList = competitorRepository.findAllBySapsaNumber(sapsaNumber);
            }
        }

        // If the competitor was not found
        if (competitorList.isEmpty()) {
            // Attempt to find the competitor by first and last name
            competitorList = competitorRepository.findAllByFirstNameAndLastName(firstName, lastName);
            if (competitorList.isEmpty()) {
                return Optional.empty();
            }
        }

        // Filters list by date of birth if present
        List<Competitor> filteredCompetitorList = competitorList;
        if (dateTimeOfBirth != null) {
            // Filters list to matching dates of birth
            filteredCompetitorList = competitorList.stream()
                    .filter(Objects::nonNull)
                    .filter(c -> dateTimeOfBirth.toLocalDate().equals(c.getDateOfBirth()))
                    .toList();
        }

        List<Competitor> filteredListWithSapsaNumber = new ArrayList<>();
        List<Competitor> filteredListWithoutSapsaNumber = new ArrayList<>();
        if (!filteredCompetitorList.isEmpty()) {
            // Finds the first matching competitor with a SAPSA number
            filteredListWithSapsaNumber = filteredCompetitorList.stream()
                    .filter(Objects::nonNull)
                    .filter(c -> c.getSapsaNumber() != null)
                    .toList();
            // Finds the first matching competitor without a SAPSA number
            filteredListWithoutSapsaNumber = filteredCompetitorList.stream()
                    .filter(Objects::nonNull)
                    .filter(c -> c.getSapsaNumber() != null)
                    .toList();
        }

        List<Competitor> finalCompetitorList = new ArrayList<>();
        if (!filteredListWithSapsaNumber.isEmpty()) {
            finalCompetitorList = filteredListWithSapsaNumber;
        } else if (!filteredListWithoutSapsaNumber.isEmpty()) {
            finalCompetitorList = filteredListWithoutSapsaNumber;
        } else {
            finalCompetitorList = filteredCompetitorList;
        }

        return finalCompetitorList.stream().filter(Objects::nonNull).findFirst();
    }
}
