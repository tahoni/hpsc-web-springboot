package za.co.hpsc.web.mappers;

import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import za.co.hpsc.web.constants.IpscConstants;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.enums.Gender;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorPatchRequest;
import za.co.hpsc.web.models.ipsc.competitor.request.CompetitorRequest;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.services.ClubService;

import java.util.ArrayList;
import java.util.Optional;

import static za.co.hpsc.web.utils.StringUtil.hasText;

/**
 * Copies the fields of a {@link CompetitorRequest} or {@link CompetitorPatchRequest} onto a {@link Competitor},
 * resolving the values that need a lookup, such as the gender, home club and club number, along the way.
 *
 * <p>
 * Kept out of the request classes because resolving a home club needs the {@link ClubRepository}, which a request
 * model shouldn't depend on.
 * </p>
 */
@Component
public class CompetitorMapper {
    private final ClubRepository clubRepository;
    private final ClubService clubService;

    public CompetitorMapper(ClubRepository clubRepository, ClubService clubService) {
        this.clubRepository = clubRepository;
        this.clubService = clubService;
    }

    /**
     * Copies the fields of a {@link CompetitorRequest} onto a {@link Competitor}, resolving the
     * gender and named home club in the process. An omitted {@code paidUpSapsa} or
     * {@code paidUpClub} or {@code isVerified} is stored as {@code null}, and a {@code nickName}
     * that is omitted, empty or blank defaults to the first name, replacing any nickname the
     * competitor already has, as every field is overwritten.
     *
     * @param competitor the entity to populate; must not be null.
     * @param request    the request carrying the field values; must not be null.
     * @throws ValidationException if the request's gender doesn't match a known {@link Gender},
     *                             or the resolved home club is {@link IpscConstants#HOME_CLUB_ABBREVIATION}
     *                             but no club number was supplied.
     * @throws NonFatalException   if the request's home club name doesn't match an existing club.
     */
    public void applyFields(@NotNull Competitor competitor, @NotNull CompetitorRequest request) {
        competitor.setFirstName(request.getFirstName());
        competitor.setLastName(request.getLastName());
        competitor.setMiddleNames(request.getMiddleNames());
        competitor.setNickName(hasText(request.getNickName()) ? request.getNickName() : request.getFirstName());
        competitor.setDateOfBirth(request.getDateOfBirth());
        competitor.setGender(resolveGender(request.getGender()));
        Club homeClub = resolveHomeClub(request.getHomeClub());
        competitor.setHomeClub(homeClub);
        competitor.setSapsaNumber(request.getSapsaNumber());
        competitor.setCompetitorNumber(resolveCompetitorNumber(request.getCompetitorNumber(), request.getSapsaNumber()));
        competitor.setClubNumber(resolveClubNumber(homeClub, request.getClubNumber()));
        competitor.setIdNumber(request.getIdNumber());
        competitor.setCellphoneNumber(request.getCellphoneNumber());
        competitor.setEmailAddresses(
                (request.getEmailAddresses() != null) ? new ArrayList<>(request.getEmailAddresses()) : new ArrayList<>());
        competitor.setPaidUpSapsa(request.getPaidUpSapsa());
        competitor.setPaidUpClub(request.getPaidUpClub());
        competitor.setIsVerified(request.getIsVerified());
    }

    /**
     * Copies only the non-null fields of a {@link CompetitorPatchRequest} onto a {@link Competitor},
     * resolving the gender and named home club in the process. Fields that are null in the request
     * are left unchanged. The club number is re-resolved when either the home club or the club
     * number is supplied, falling back to the competitor's existing club number.
     *
     * @param competitor the entity to patch; must not be null.
     * @param request    the request carrying the field values; must not be null.
     * @throws ValidationException if the request's gender doesn't match a known {@link Gender},
     *                             or the resolved home club is {@link IpscConstants#HOME_CLUB_ABBREVIATION}
     *                             but no club number was supplied.
     * @throws NonFatalException   if the request's home club name doesn't match an existing club.
     */
    public void applyPatchFields(@NotNull Competitor competitor, @NotNull CompetitorPatchRequest request) {
        if (request.getFirstName() != null) {
            competitor.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            competitor.setLastName(request.getLastName());
        }
        if (request.getMiddleNames() != null) {
            competitor.setMiddleNames(request.getMiddleNames());
        }
        if (request.getNickName() != null) {
            competitor.setNickName(request.getNickName());
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
        Integer competitorNumber = parseCompetitorNumber(request.getCompetitorNumber());
        if (competitorNumber != null) {
            competitor.setCompetitorNumber(competitorNumber);
        }
        if ((request.getHomeClub() != null) || (request.getClubNumber() != null)) {
            String clubNumber = (request.getClubNumber() != null) ? request.getClubNumber() : competitor.getClubNumber();
            competitor.setClubNumber(resolveClubNumber(competitor.getHomeClub(), clubNumber));
        }
        if (request.getIdNumber() != null) {
            competitor.setIdNumber(request.getIdNumber());
        }
        if (request.getCellphoneNumber() != null) {
            competitor.setCellphoneNumber(request.getCellphoneNumber());
        }
        if (request.getEmailAddresses() != null) {
            competitor.setEmailAddresses(new ArrayList<>(request.getEmailAddresses()));
        }
        if (request.getPaidUpSapsa() != null) {
            competitor.setPaidUpSapsa(request.getPaidUpSapsa());
        }
        if (request.getPaidUpClub() != null) {
            competitor.setPaidUpClub(request.getPaidUpClub());
        }
        if (request.getIsVerified() != null) {
            competitor.setIsVerified(request.getIsVerified());
        }
    }

    /**
     * Resolves a competitor's club number against their home club.
     *
     * <p>
     * A club number is only meaningful for HPSC's own members: it's required when
     * {@code homeClub} is {@link IpscConstants#HOME_CLUB_IDENTIFIER}, and forced to {@code null}
     * for every other home club, including none, regardless of what was supplied on the request.
     * </p>
     *
     * @param homeClub   the competitor's resolved home club; may be null.
     * @param clubNumber the club number supplied on the request; may be null or blank.
     * @return {@code clubNumber} when {@code homeClub} is {@link IpscConstants#HOME_CLUB_ABBREVIATION},
     * otherwise {@code null}.
     * @throws ValidationException if {@code homeClub} is {@link IpscConstants#HOME_CLUB_ABBREVIATION}
     *                             but {@code clubNumber} is null or blank.
     */
    public @Nullable String resolveClubNumber(Club homeClub, String clubNumber) {
        if (!isMemberOfHomeClub(homeClub)) {
            return null;
        }

        if (!hasText(clubNumber)) {
            throw new ValidationException(String.format("Club number is required for %s competitors.",
                    IpscConstants.HOME_CLUB_ABBREVIATION));
        }

        return clubNumber;
    }

    /**
     * Checks whether a club is HPSC's own club, the home club identified by
     * {@link IpscConstants#HOME_CLUB_IDENTIFIER}.
     *
     * <p>
     * Shorthand for {@link #isMemberOfHomeClub(Club, ClubIdentifier)} with the default home club.
     * </p>
     *
     * @param club the club to check; may be {@code null}.
     * @return {@code true} if {@code club} is the default home club; {@code false} otherwise.
     */
    public boolean isMemberOfHomeClub(Club club) {
        return isMemberOfHomeClub(club, IpscConstants.HOME_CLUB_IDENTIFIER);
    }

    /**
     * Checks whether a club is the given home club.
     *
     * <p>
     * The club is compared with {@code homeClubIdentifier} through
     * {@link ClubService#isSameClub(Club, ClubIdentifier)}, so a {@code null} club, a club with no identifier or a
     * {@code null} {@code homeClubIdentifier} is simply not a member.
     * </p>
     *
     * @param club               the club to check; may be {@code null}.
     * @param homeClubIdentifier the identifier of the home club to check against; may be {@code null}, in which case
     *                           no club is a member. {@link #isMemberOfHomeClub(Club)} passes
     *                           {@link IpscConstants#HOME_CLUB_IDENTIFIER}, HPSC's own club.
     * @return {@code true} if {@code club} is the home club identified by {@code homeClubIdentifier}; {@code false}
     * otherwise.
     */
    public boolean isMemberOfHomeClub(Club club, ClubIdentifier homeClubIdentifier) {
        return clubService.isSameClub(club, homeClubIdentifier);
    }

    /**
     * Resolves a competitor's gender by name.
     *
     * @param gender the gender name to look up; may be null or blank, in which case no gender
     *               is set.
     * @return the matching {@link Gender}, or {@code null} if {@code gender} wasn't supplied.
     * @throws ValidationException if {@code gender} was supplied but doesn't match a known gender.
     */
    public @Nullable Gender resolveGender(String gender) {
        if (!hasText(gender)) {
            return null;
        }

        return Gender.fromName(gender)
                .orElseThrow(() -> new ValidationException("Unknown gender: " + gender));
    }

    /**
     * Resolves a competitor's home club by name or abbreviation.
     *
     * @param clubName the club name/abbreviation to look up; may be null or blank, in which case
     *                 no home club is set.
     * @return the matching {@link Club}, or {@code null} if {@code clubName} wasn't supplied.
     * @throws NonFatalException if {@code clubName} was supplied but doesn't match an existing club.
     */
    public @Nullable Club resolveHomeClub(String clubName) {
        if (!hasText(clubName)) {
            return null;
        }

        Optional<Club> optionalClub = clubRepository.findByName(clubName).or(() -> clubRepository.findByAbbreviation(clubName));
        return optionalClub.orElseThrow(() -> new NonFatalException("No club found with name " + clubName));
    }

    /**
     * Resolves the competitor number to use for a competitor, preferring an explicit competitor
     * number and falling back to the SAPSA number.
     *
     * <p>
     * A blank competitor number is treated the same as a {@code null} one, and so triggers the
     * fallback.
     * </p>
     *
     * @param competitorNumber the explicit competitor number, as text, may be null or blank.
     * @param sapsaNumber      the SAPSA number to fall back on; may be null.
     * @return {@code competitorNumber} as a whole number if it was supplied, otherwise {@code sapsaNumber},
     * or {@code null} if neither was supplied.
     * @throws ValidationException if {@code competitorNumber} is not a whole number.
     */
    public Integer resolveCompetitorNumber(String competitorNumber, Integer sapsaNumber) {
        Integer parsedCompetitorNumber = parseCompetitorNumber(competitorNumber);
        return (parsedCompetitorNumber != null) ? parsedCompetitorNumber : sapsaNumber;
    }

    /**
     * Converts a competitor number received as text to the whole number it is stored as.
     *
     * @param competitorNumber the competitor number as text; surrounding whitespace is ignored. May be null or
     *                         blank.
     * @return the competitor number as a whole number, or {@code null} if it is null or blank.
     * @throws ValidationException if the competitor number is not a whole number.
     */
    public @Nullable Integer parseCompetitorNumber(String competitorNumber) {
        if (!hasText(competitorNumber)) {
            return null;
        }
        try {
            return Integer.valueOf(competitorNumber.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Competitor number must be a whole number: " + competitorNumber.trim());
        }
    }
}
