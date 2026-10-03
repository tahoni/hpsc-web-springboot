-- =============================================================================================
-- V8.0.0 -- Record when each shooter_log_competitor row's rank and points were calculated.
-- =============================================================================================

ALTER TABLE shooter_log_competitor
    ADD COLUMN date_calculated DATETIME(6) NULL AFTER points;
