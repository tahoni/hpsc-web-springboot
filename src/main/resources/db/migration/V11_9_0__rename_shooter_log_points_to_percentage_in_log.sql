-- =============================================================================================
-- V11.9.0 -- Rename shooter_log_competitor.points and shooter_log_overall.points to
-- percentage_in_log (ShooterLogCompetitor and ShooterLogOverall now call it percentageInLog).
-- =============================================================================================

ALTER TABLE shooter_log_competitor
    RENAME COLUMN points TO percentage_in_log;

ALTER TABLE shooter_log_overall
    RENAME COLUMN points TO percentage_in_log;
