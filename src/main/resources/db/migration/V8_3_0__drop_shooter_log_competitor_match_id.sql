-- =============================================================================================
-- V8.3.0 -- Drop shooter_log_competitor.match_id (and its foreign key): the match is reached
-- through match_competitor_id, so ShooterLogCompetitor no longer maps a direct match reference.
-- =============================================================================================

ALTER TABLE shooter_log_competitor
    DROP FOREIGN KEY fk_slc_match;

ALTER TABLE shooter_log_competitor
    DROP COLUMN match_id;
