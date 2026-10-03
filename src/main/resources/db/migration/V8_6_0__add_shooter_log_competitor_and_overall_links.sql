-- =============================================================================================
-- V8.6.0 -- Link shooter_log_competitor to competitor, and shooter_log_overall to shooter_log and
-- competitor, matching ShooterLogCompetitor.competitor, ShooterLogOverall.shooterLog and
-- ShooterLogOverall.competitor. Each table becomes unique per shooter log and competitor.
--
-- The new columns are NOT NULL with no default, so this fails if either table already has rows.
-- Both are still schema-only (no calculation service populates them yet), so they should be empty
-- in every environment; otherwise backfill first (shooter_log_competitor.competitor_id from
-- match_competitor.competitor_id).
-- =============================================================================================

ALTER TABLE shooter_log_competitor
    ADD COLUMN competitor_id BIGINT NOT NULL AFTER shooter_log_id,
    ADD CONSTRAINT fk_slc_competitor FOREIGN KEY (competitor_id) REFERENCES competitor (id);

-- The new unique key goes in before the old one is dropped: fk_slc_shooter_log needs an index
-- that starts with shooter_log_id at all times.
ALTER TABLE shooter_log_competitor
    ADD CONSTRAINT uk_slc_log_competitor UNIQUE (shooter_log_id, competitor_id);

ALTER TABLE shooter_log_competitor
    DROP INDEX uk_shooter_log_competitor;

ALTER TABLE shooter_log_overall
    ADD COLUMN shooter_log_id BIGINT NOT NULL AFTER id,
    ADD COLUMN competitor_id BIGINT NOT NULL AFTER shooter_log_id,
    ADD CONSTRAINT fk_slo_shooter_log FOREIGN KEY (shooter_log_id) REFERENCES shooter_log (id),
    ADD CONSTRAINT fk_slo_competitor FOREIGN KEY (competitor_id) REFERENCES competitor (id),
    ADD CONSTRAINT uk_slo_log_competitor UNIQUE (shooter_log_id, competitor_id);
