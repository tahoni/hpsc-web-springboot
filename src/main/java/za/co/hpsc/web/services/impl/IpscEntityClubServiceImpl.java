package za.co.hpsc.web.services.impl;

import org.springframework.stereotype.Service;
import za.co.hpsc.web.domain.Club;
import za.co.hpsc.web.enums.ClubIdentifier;
import za.co.hpsc.web.exceptions.FatalException;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;
import za.co.hpsc.web.repositories.ClubRepository;
import za.co.hpsc.web.services.IpscEntityClubService;

import java.util.Optional;

import static za.co.hpsc.web.utils.StringUtil.hasText;

@Service
public class IpscEntityClubServiceImpl implements IpscEntityClubService {
    private final ClubRepository clubRepository;

    /**
     * Creates the service.
     *
     * @param clubRepository the repository used to look up persisted clubs.
     */
    public IpscEntityClubServiceImpl(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation note: delegates to {@link #findByCodeOrAbbreviationWithDefault(String, ClubIdentifier)} with no
     * default club.
     */
    @Override
    public Club findByCodeOrAbbreviation(String clubName)
            throws ValidationException, NonFatalException {
        return findByCodeOrAbbreviationWithDefault(clubName, null);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation note: a blank {@code clubName} is resolved through
     * {@link ClubRepository#findByIdentifier(ClubIdentifier)} alone, or fails without querying the repository when
     * there is no default. Otherwise, the lookup stops at the first hit, in this order: {@link ClubRepository} by
     * abbreviation, {@link ClubRepository} by name, then {@link ClubIdentifier} by code, abbreviation or name
     * resolved through {@link ClubRepository#findByIdentifier(ClubIdentifier)}. Only a name that matches none of
     * these throws {@link ValidationException}; every other failure to find a club throws {@link NonFatalException}.
     */
    @Override
    public Club findByCodeOrAbbreviationWithDefault(String clubName, ClubIdentifier defaultClubIdentifier)
            throws ValidationException, NonFatalException {
        if (!hasText(clubName)) {
            if (defaultClubIdentifier != null) {
                return clubRepository.findByIdentifier(defaultClubIdentifier)
                        .orElseThrow(() -> new NonFatalException("No club found with name " + clubName +
                                " and no default club found with identifier " + defaultClubIdentifier));
            } else {
                throw new NonFatalException("No club found with name " + clubName);
            }
        }

        // Find club by abbreviation or name
        Optional<Club> optionalClub = clubRepository.findByAbbreviation(clubName)
                .or(() -> clubRepository.findByName(clubName));
        if (optionalClub.isPresent()) {
            return optionalClub.get();
        }

        // Find club by identifier
        Optional<ClubIdentifier> optionalClubIdentifier = ClubIdentifier.fromCode(clubName)
                .or(() -> ClubIdentifier.fromAbbreviation(clubName))
                .or(() -> ClubIdentifier.fromName(clubName) );
        if (optionalClubIdentifier.isPresent()) {
            return clubRepository.findByIdentifier(optionalClubIdentifier.get())
                    .orElseThrow(() -> new NonFatalException("No club found with identifier " + clubName));
        } else {
            throw new ValidationException("No club found with name " + clubName);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation note: compares {@link Club#getIdentifier()} to the target by reference, so no repository
     * access is involved.
     */
    @Override
    public boolean isSameClub(Club club, ClubIdentifier targetClubIdentifier) {
        return (targetClubIdentifier != null) && (club != null) && (club.getIdentifier() == targetClubIdentifier);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation note: compares the two enum constants by reference, so no repository access is involved.
     */
    @Override
    public boolean isSameClub(ClubIdentifier clubIdentifier, ClubIdentifier targetClubIdentifier) {
        return (clubIdentifier != null) && (clubIdentifier == targetClubIdentifier);
    }
}
