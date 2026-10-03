-- =============================================================================================
-- V8.4.0 -- Rework shooter_log to match ShooterLog: drop the per-competitor, club, firearm type,
-- power factor, log value and calculated date columns (and the competitor and club foreign
-- keys), and add start_date and end_date (dates only, no time).
--
-- shooter_log is still schema-only (no calculation service populates it yet), so dropping these
-- columns loses no data.
-- =============================================================================================

ALTER TABLE shooter_log
    DROP FOREIGN KEY fk_shooter_log_competitor,
    DROP FOREIGN KEY fk_shooter_log_club;

ALTER TABLE shooter_log
    DROP COLUMN competitor_id,
    DROP COLUMN club_id,
    DROP COLUMN firearm_type,
    DROP COLUMN power_factor,
    DROP COLUMN log_value,
    DROP COLUMN calculated_date,
    ADD COLUMN start_date DATE NULL AFTER id,
    ADD COLUMN end_date DATE NULL AFTER start_date;
