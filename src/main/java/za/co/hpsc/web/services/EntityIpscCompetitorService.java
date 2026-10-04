package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Competitor;

import java.util.Optional;

/**
 * The {@code EntityIpscCompetitorService} interface looks up an already persisted IPSC
 * {@link Competitor} from the loosely-specified identity found in imported data, such as a
 * member's full name and competitor number.
 *
 * @since 11.0.0
 */
public interface EntityIpscCompetitorService {
    /**
     * Finds the single persisted competitor that matches the given full name and competitor number.
     *
     * <p>
     * The competitor number is tried first: if exactly one competitor has it, that competitor is
     * returned. Numbers in {@link za.co.hpsc.web.constants.IpscConstants#EXCLUDE_ICS_ALIAS} are
     * ignored. Otherwise the full name, either "FirstName LastName" or "NickName LastName", is
     * matched ignoring case, after removing the "RO" suffix described by
     * {@link za.co.hpsc.web.constants.IpscConstants#REPLACE_IN_NAMES_REGEX}. When several
     * competitors share the number, the full name only narrows those matches.
     * </p>
     *
     * @param fullName         the competitor's full name. Must not be null.
     * @param competitorNumber the competitor's number (SAPSA or club number).
     * @return the matching competitor, or an empty {@link Optional} if no competitor, or more
     * than one competitor, matches.
     */
    Optional<Competitor> findCompetitor(String fullName, int competitorNumber);
}
