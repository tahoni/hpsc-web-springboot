package za.co.hpsc.web.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.models.ipsc.response.MemberResponse;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.CompetitorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CompetitorServiceImpl implements CompetitorService {
    protected final CompetitorRepository competitorRepository;

    public CompetitorServiceImpl(CompetitorRepository competitorRepository) {
        this.competitorRepository = competitorRepository;
    }

    // TODO: Javadoc
    @Override
    public Optional<Competitor> findCompetitor(MemberResponse memberResponse) {
        if (memberResponse == null) {
            return Optional.empty();
        }

        Optional<Competitor> competitor = Optional.empty();
        String icsAlias = memberResponse.getIcsAlias();

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
            competitorList = competitorRepository.findAllByFirstNameAndLastName(memberResponse.getFirstName(),
                    memberResponse.getLastName());
            if (competitorList.isEmpty()) {
                return Optional.empty();
            }

            // Filters list by date of birth if present
            if (memberResponse.getDateOfBirth() != null) {
                competitorList = competitorList.stream()
                        .filter(c -> memberResponse.getDateOfBirth().toLocalDate().equals(c.getDateOfBirth()))
                        .toList();
            }

            // Returns the first matching competitor without a SAPSA number
            competitor = competitorList.stream()
                    .filter(c -> c.getSapsaNumber() == null)
                    .findFirst();
        }

        return competitor;
    }
}
