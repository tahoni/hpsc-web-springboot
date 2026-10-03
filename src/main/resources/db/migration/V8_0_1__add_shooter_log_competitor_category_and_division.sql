-- =============================================================================================
-- V8.0.1 -- Record the competitor category and division each shooter_log_competitor row was
-- ranked under, matching ShooterLogCompetitor.competitorCategory and
-- ShooterLogCompetitor.division.
--
-- Both columns are NOT NULL with no default, so this fails if shooter_log_competitor already has
-- rows. The table is still schema-only (no calculation service populates it yet), so it should be
-- empty in every environment; otherwise backfill from match_competitor first.
-- =============================================================================================

ALTER TABLE shooter_log_competitor
    ADD COLUMN competitor_category VARCHAR(255) NOT NULL AFTER match_id,
    ADD COLUMN division VARCHAR(255) NOT NULL AFTER competitor_category;
