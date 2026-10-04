package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.exceptions.NonFatalException;
import za.co.hpsc.web.exceptions.ValidationException;

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
     * The lookup is attempted in this order, returning as soon as exactly one competitor matches:
     * </p>
     * <ol>
     *     <li>The competitor number (SAPSA or club number), when it is numeric and greater than zero.
     *     Numbers in {@link za.co.hpsc.web.constants.IpscConstants#EXCLUDE_ICS_ALIAS} are ignored.</li>
     *     <li>The same value as an ID number.</li>
     *     <li>The full name, either "FirstName LastName" or "NickName LastName", matched ignoring
     *     case, after removing the text described by
     *     {@link za.co.hpsc.web.constants.IpscConstants#REPLACE_IN_NAMES_REGEX} (such as the "RO"
     *     suffix). When the number or ID number matched several competitors, the full name only
     *     narrows those matches; when they matched none, all competitors are searched by name.</li>
     * </ol>
     *
     * @param competitorNumber the competitor's number (SAPSA or club number) or ID number. May be
     *                         null or blank when a full name is supplied.
     * @param fullName         the competitor's full name. May be null or blank when a competitor
     *                         number is supplied.
     * @return the single matching competitor. Never empty: when no unique match is found, an
     * exception is thrown instead.
     * @throws ValidationException if both the full name and the competitor number are null or
     *                             blank, or if more than one competitor matches the number, ID
     *                             number or name, including when the name matches none of the
     *                             competitors that share the number.
     * @throws NonFatalException   if no competitor matches the number, ID number or name.
     */
    Optional<Competitor> findCompetitor(String competitorNumber, String fullName)
        throws ValidationException, NonFatalException;
}
