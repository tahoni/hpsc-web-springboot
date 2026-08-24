-- =============================================================================================
-- V7.1.0 -- Rename shooter_log_entry to shooter_log_competitor, split shooter_log entries by
-- power factor as well as firearm type, record the points each shooter_log_competitor row
-- contributed to the snapshot's log value, and add a direct match reference to each
-- shooter_log_competitor row (alongside the existing match_competitor_id link).
--
-- shooter_log/shooter_log_competitor are still schema-only (no calculation service populates
-- them yet -- see RELEASE_NOTES.md), so both tables are empty in every environment and these
-- changes can be made without a backfill.
-- =============================================================================================

RENAME TABLE shooter_log_entry TO shooter_log_competitor;

ALTER TABLE shooter_log_competitor
    DROP FOREIGN KEY fk_sle_shooter_log,
    DROP FOREIGN KEY fk_sle_match_competitor;

ALTER TABLE shooter_log_competitor
    RENAME INDEX uk_shooter_log_entry TO uk_shooter_log_competitor;

ALTER TABLE shooter_log_competitor
    ADD CONSTRAINT fk_slc_shooter_log FOREIGN KEY (shooter_log_id) REFERENCES shooter_log (id),
    ADD CONSTRAINT fk_slc_match_competitor FOREIGN KEY (match_competitor_id) REFERENCES match_competitor (id);

ALTER TABLE shooter_log
    ADD COLUMN power_factor VARCHAR(255) NOT NULL AFTER firearm_type;

ALTER TABLE shooter_log_competitor
    ADD COLUMN points DECIMAL(19, 6) NULL AFTER rank_in_log;

ALTER TABLE shooter_log_competitor
    ADD COLUMN match_id BIGINT NOT NULL AFTER match_competitor_id;

ALTER TABLE shooter_log_competitor
    ADD CONSTRAINT fk_slc_match FOREIGN KEY (match_id) REFERENCES ipsc_match (id);
