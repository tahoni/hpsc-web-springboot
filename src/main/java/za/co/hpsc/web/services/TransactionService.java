package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.MatchCompetitor;

import java.util.List;

/**
 * The {@code TransactionService} interface commits competitor, match and match competitor changes to the database,
 * each method in its own explicit transaction. The other services validate requests and build or
 * modify entities outside any transaction, then hand them to this service to persist, so this is
 * the only place competitor and match writes are made transactional.
 *
 * <p>
 * Every entity returned has the associations its response mapping reads (a competitor's home club
 * and email addresses, a match's club) already loaded, so it stays usable after the
 * transaction has ended.
 * </p>
 */
public interface TransactionService {
    /**
     * Creates or updates a competitor, together with their email addresses.
     *
     * @param competitor the competitor to save; a new one is created when its ID is
     *                   {@code null}. Must not be null.
     * @return the saved competitor, including its generated ID.
     */
    Competitor saveCompetitor(Competitor competitor);

    /**
     * Creates or updates a batch of competitors in a single transaction, so either every one is
     * saved or none is.
     *
     * @param competitors the competitors to save. Must not be null.
     * @return the saved competitors, in the order given.
     */
    List<Competitor> saveCompetitors(List<Competitor> competitors);

    /**
     * Deletes a competitor, together with their email addresses.
     *
     * @param competitor the competitor to delete. Must not be null and must already be
     *                   persisted.
     * @throws org.springframework.dao.DataIntegrityViolationException if another record still
     *                                                                 references the competitor.
     */
    void deleteCompetitor(Competitor competitor);

    /**
     * Creates or updates a match.
     *
     * @param match the match to save; a new one is created when its ID is {@code null}. Must not
     *              be null.
     * @return the saved match, including its generated ID.
     */
    IpscMatch saveMatch(IpscMatch match);

    /**
     * Creates or updates a batch of matches in a single transaction, so either every one is
     * saved or none is, following the same rules as {@link #saveMatch(IpscMatch)}.
     *
     * @param matches the matches to save. Must not be null.
     * @return the saved matches, in the order given.
     */
    List<IpscMatch> saveMatches(List<IpscMatch> matches);

    /**
     * Deletes a match.
     *
     * @param match the match to delete. Must not be null and must already be persisted.
     * @throws org.springframework.dao.DataIntegrityViolationException if another record still
     *                                                                 references the match.
     */
    void deleteMatch(IpscMatch match);

    /**
     * Creates or updates a match competitor.
     *
     * @param matchCompetitor the match competitor to save; a new one is created when its ID is
     *                        {@code null}. Must not be null.
     * @return the saved match competitor, including its generated ID.
     * @throws org.springframework.dao.DataIntegrityViolationException if the save violates a
     *                                                                 constraint, such as the
     *                                                                 unique competitor, match and
     *                                                                 firearm type key.
     */
    MatchCompetitor saveMatchCompetitor(MatchCompetitor matchCompetitor);

    /**
     * Creates or updates a batch of match competitors in a single transaction, so either every one is
     * saved or none is.
     *
     * @param matchCompetitors the match competitors to save. Must not be null.
     * @return the saved match competitors, in the order given.
     * @throws org.springframework.dao.DataIntegrityViolationException if a unique constraint is violated, such as
     *                                                                  a duplicate competitor, match and firearm
     *                                                                  type.
     */
    List<MatchCompetitor> saveMatchCompetitors(List<MatchCompetitor> matchCompetitors);

    /**
     * Deletes a match competitor.
     *
     * @param matchCompetitor the match competitor to delete. Must not be null and must already be
     *                        persisted.
     * @throws org.springframework.dao.DataIntegrityViolationException if another record still
     *                                                                 references the match
     *                                                                 competitor.
     */
    void deleteMatchCompetitor(MatchCompetitor matchCompetitor);
}
