package za.co.hpsc.web.services;

import za.co.hpsc.web.domain.Competitor;
import za.co.hpsc.web.domain.IpscMatch;
import za.co.hpsc.web.domain.IpscMatchStage;

import java.util.List;

/**
 * The {@code TransactionService} interface commits competitor and match changes to the database,
 * each method in its own explicit transaction. The other services validate requests and build or
 * modify entities outside any transaction, then hand them to this service to persist, so this is
 * the only place competitor and match writes are made transactional.
 *
 * <p>
 * Every entity returned has the associations its response mapping reads (a competitor's home club
 * and email addresses, a match's club and stages) already loaded, so it stays usable after the
 * transaction has ended.
 * </p>
 */
public interface TransactionService {
    /**
     * How {@link #saveMatch(IpscMatch, List, StageSaveMode)} applies the stages it's given to an
     * existing match.
     */
    enum StageSaveMode {
        /**
         * Delete every stage the match already has, then add the given stages. A {@code null}
         * stage list leaves the match with no stages.
         */
        REPLACE,
        /**
         * Update each existing stage whose stage number matches a given stage, and add the
         * rest, leaving any other existing stages untouched. A given stage's {@code null} name
         * leaves the existing stage's name unchanged, and a {@code null} stage list leaves every
         * stage untouched.
         */
        UPSERT
    }

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
     * Creates or updates a match. For a new match, any stages already on its
     * {@link IpscMatch#getStages() stages} collection are created with it; an existing match's
     * stages are left untouched.
     *
     * @param match the match to save; a new one is created when its ID is {@code null}. Must not
     *              be null.
     * @return the saved match, including its generated ID and stages.
     */
    IpscMatch saveMatch(IpscMatch match);

    /**
     * Updates an existing match and applies the given stages to it according to
     * {@code stageSaveMode}.
     *
     * @param match         the match to save. Must not be null and must already be persisted.
     * @param stages        the stages to apply, identified by stage number; may be null (see
     *                      {@link StageSaveMode}).
     * @param stageSaveMode how to apply {@code stages}. Must not be null.
     * @return the saved match, including all its stages after the save.
     */
    IpscMatch saveMatch(IpscMatch match, List<IpscMatchStage> stages, StageSaveMode stageSaveMode);

    /**
     * Creates or updates a batch of matches in a single transaction, so either every one is
     * saved or none is, following the same rules as {@link #saveMatch(IpscMatch)}.
     *
     * @param matches the matches to save. Must not be null.
     * @return the saved matches, in the order given.
     */
    List<IpscMatch> saveMatches(List<IpscMatch> matches);

    /**
     * Deletes a match, together with its stages.
     *
     * @param match the match to delete. Must not be null and must already be persisted.
     * @throws org.springframework.dao.DataIntegrityViolationException if another record still
     *                                                                 references the match.
     */
    void deleteMatch(IpscMatch match);
}
