package za.co.hpsc.web.services.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.enums.Gender;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.models.ipsc.competitor.response.CompetitorResponse;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.repositories.CompetitorRepository;
import za.co.hpsc.web.services.IpscCompetitorService;

@Slf4j
@Service
public class IpscCompetitorServiceImpl implements IpscCompetitorService {
    private final CompetitorRepository competitorRepository;
    private final ClubRepository clubRepository;

    public IpscCompetitorServiceImpl(CompetitorRepository competitorRepository, ClubRepository clubRepository) {
        this.competitorRepository = competitorRepository;
        this.clubRepository = clubRepository;
    }

    @Override
    @Transactional
    public CompetitorResponse createCompetitor(CompetitorRequest request) {
        validateForCreate(request);

        Competitor competitor = new Competitor();
        applyFields(competitor, request);
        competitor = competitorRepository.save(competitor);

        return toResponse(competitor);
    }

    @Override
    @Transactional
    public CompetitorResponse updateCompetitor(Long competitorId, CompetitorRequest request) {
        validateForCreate(request);
        Competitor competitor = findCompetitorOrThrow(competitorId);

        applyFields(competitor, request);
        competitor = competitorRepository.save(competitor);

        return toResponse(competitor);
    }

    @Override
    @Transactional
    public CompetitorResponse patchCompetitor(Long competitorId, CompetitorRequest request) {
        Competitor competitor = findCompetitorOrThrow(competitorId);

        if (request.getFirstName() != null) {
            competitor.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            competitor.setLastName(request.getLastName());
        }
        if (request.getMiddleNames() != null) {
            competitor.setMiddleNames(request.getMiddleNames());
        }
        if (request.getNickname() != null) {
            competitor.setNickname(request.getNickname());
        }
        if (request.getDateOfBirth() != null) {
            competitor.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            competitor.setGender(resolveGender(request.getGender()));
        }
        if (request.getHomeClub() != null) {
            competitor.setHomeClub(resolveHomeClub(request.getHomeClub()));
        }
        if (request.getSapsaNumber() != null) {
            competitor.setSapsaNumber(request.getSapsaNumber());
        }
        if (request.getCompetitorNumber() != null) {
            competitor.setCompetitorNumber(request.getCompetitorNumber());
        }
        if (request.getClubNumber() != null) {
            if (request.getClubNumber().isBlank()) {
                throw new ValidationException("Club number cannot be blank.");
            }
            competitor.setClubNumber(request.getClubNumber());
        }
        if (request.getIdNumber() != null) {
            competitor.setIdNumber(request.getIdNumber());
        }
        if (request.getCellphoneNumber() != null) {
            competitor.setCellphoneNumber(request.getCellphoneNumber());
        }
        if (request.getEmailAddress() != null) {
            competitor.setEmailAddress(request.getEmailAddress());
        }
        competitor = competitorRepository.save(competitor);

        return toResponse(competitor);
    }

    @Override
    public CompetitorResponse getCompetitor(Long competitorId) {
        return toResponse(findCompetitorOrThrow(competitorId));
    }

    /**
     * Copies the fields of a {@link CompetitorRequest} onto a {@link Competitor}, resolving the
     * gender and named home club in the process.
     *
     * @param competitor the entity to populate; must not be null.
     * @param request    the request carrying the field values; must not be null.
     * @throws ValidationException if the request's gender doesn't match a known {@link Gender}.
     * @throws NonFatalException   if the request's home club name doesn't match an existing club.
     */
    protected void applyFields(@NotNull Competitor competitor, @NotNull CompetitorRequest request) {
        competitor.setFirstName(request.getFirstName());
        competitor.setLastName(request.getLastName());
        competitor.setMiddleNames(request.getMiddleNames());
        competitor.setNickname(request.getNickname());
        competitor.setDateOfBirth(request.getDateOfBirth());
        competitor.setGender(resolveGender(request.getGender()));
        competitor.setHomeClub(resolveHomeClub(request.getHomeClub()));
        competitor.setSapsaNumber(request.getSapsaNumber());
        competitor.setCompetitorNumber(request.getCompetitorNumber());
        competitor.setClubNumber(request.getClubNumber());
        competitor.setIdNumber(request.getIdNumber());
        competitor.setCellphoneNumber(request.getCellphoneNumber());
        competitor.setEmailAddress(request.getEmailAddress());
    }

    /**
     * Retrieves an existing competitor or throws if none exists with the given ID.
     *
     * @param competitorId the identifier to look up.
     * @return the matching {@link Competitor}.
     * @throws NonFatalException if no competitor with {@code competitorId} exists.
     */
    protected Competitor findCompetitorOrThrow(Long competitorId) {
        return competitorRepository.findById(competitorId)
                .orElseThrow(() -> new NonFatalException("No competitor found with ID " + competitorId));
    }

    /**
     * Resolves a competitor's gender by name.
     *
     * @param gender the gender name to look up; may be null or blank, in which case no gender
     *               is set.
     * @return the matching {@link Gender}, or {@code null} if {@code gender} wasn't supplied.
     * @throws ValidationException if {@code gender} was supplied but doesn't match a known gender.
     */
    protected Gender resolveGender(String gender) {
        if ((gender == null) || gender.isBlank()) {
            return null;
        }

        return Gender.fromName(gender)
                .orElseThrow(() -> new ValidationException("Unknown gender: " + gender));
    }

    /**
     * Resolves a competitor's home club by name.
     *
     * @param clubName the club name to look up; may be null or blank, in which case no home
     *                 club is set.
     * @return the matching {@link Club}, or {@code null} if {@code clubName} wasn't supplied.
     * @throws NonFatalException if {@code clubName} was supplied but doesn't match an existing club.
     */
    protected Club resolveHomeClub(String clubName) {
        if ((clubName == null) || clubName.isBlank()) {
            return null;
        }

        return clubRepository.findByName(clubName)
                .orElseThrow(() -> new NonFatalException("No club found with name " + clubName));
    }

    /**
     * Validates that a request carries every field required to create or fully replace a
     * competitor.
     *
     * @param request the request to validate.
     * @throws ValidationException if a required field is missing.
     */
    protected void validateForCreate(CompetitorRequest request) {
        if (request == null) {
            throw new ValidationException("Competitor request cannot be null.");
        }
        if ((request.getFirstName() == null) || request.getFirstName().isBlank()) {
            throw new ValidationException("First name is required.");
        }
        if ((request.getLastName() == null) || request.getLastName().isBlank()) {
            throw new ValidationException("Last name is required.");
        }
        if ((request.getClubNumber() == null) || request.getClubNumber().isBlank()) {
            throw new ValidationException("Club number is required.");
        }
    }

    /**
     * Maps a persisted competitor to the response shape returned by the controller.
     *
     * @param competitor the competitor to map.
     * @return the mapped {@link CompetitorResponse}.
     */
    protected CompetitorResponse toResponse(Competitor competitor) {
        return new CompetitorResponse(
                competitor.getId(),
                competitor.getFirstName(),
                competitor.getLastName(),
                competitor.getMiddleNames(),
                competitor.getNickname(),
                competitor.getDateOfBirth(),
                competitor.getGender(),
                ((competitor.getHomeClub() != null) ? competitor.getHomeClub().getIdentifier() : null),
                competitor.getSapsaNumber(),
                competitor.getCompetitorNumber(),
                competitor.getClubNumber(),
                competitor.getIdNumber(),
                competitor.getCellphoneNumber(),
                competitor.getEmailAddress());
    }
}
