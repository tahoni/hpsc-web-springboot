-- =============================================================================================
-- V10.2.0 -- Return the competitor category of match_competitor and shooter_log_competitor to a
-- single competitor_category column, replacing the match_competitor_category and
-- shooter_log_competitor_category child tables added by V8.8.0 and V8.9.0
-- (MatchCompetitor.competitorCategory and ShooterLogCompetitor.competitorCategory are a single
-- CompetitorCategory again).
--
-- A row that had several categories keeps only the alphabetically first of them, and a row with
-- none keeps an empty category (CompetitorCategory.NONE), so the column can be NOT NULL.
-- =============================================================================================

ALTER TABLE match_competitor
    ADD COLUMN competitor_category VARCHAR(255) NULL AFTER match_club;

UPDATE match_competitor mc
SET mc.competitor_category = COALESCE((SELECT MIN(c.competitor_category)
                                       FROM match_competitor_category c
                                       WHERE c.match_competitor_id = mc.id), '');

ALTER TABLE match_competitor
    MODIFY COLUMN competitor_category VARCHAR(255) NOT NULL;

DROP TABLE match_competitor_category;

ALTER TABLE shooter_log_competitor
    ADD COLUMN competitor_category VARCHAR(255) NULL AFTER match_id;

UPDATE shooter_log_competitor slc
SET slc.competitor_category = COALESCE((SELECT MIN(c.competitor_category)
                                        FROM shooter_log_competitor_category c
                                        WHERE c.shooter_log_competitor_id = slc.id), '');

ALTER TABLE shooter_log_competitor
    MODIFY COLUMN competitor_category VARCHAR(255) NOT NULL;

DROP TABLE shooter_log_competitor_category;
