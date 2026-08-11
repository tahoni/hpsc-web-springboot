-- =============================================================================================
-- V7.0.0 -- Evolve the match domain schema (v6.0.0 -> v7.0.0)
--
-- Covers the domain changes introduced for v7.0.0:
--   - club.identifier                    (links a club row to ClubIdentifier: HPSC/SOSC/PMPSC)
--   - competitor.home_club_id            (competitor's home club membership)
--   - ipsc_match_stage                   (match_id, stage_number) uniqueness
--   - match_competitor.overall_ranking   (renamed from match_ranking)
--   - match_competitor.club_ranking      (new)
--   - match_competitor.is_visitor        (new)
--   - match_competitor                   (competitor_id, match_id, firearm_type) uniqueness
--   - match_stage_competitor             restructured to reference match_competitor directly,
--                                         instead of duplicating competitor/category/firearm
--                                         type/division/power-factor/club fields
--   - shooter_log / shooter_log_entry    (new tables)
--
-- ASSUMPTIONS -- please verify against your actual live schema before running this migration:
--   1. This migration ALTERs an already-existing v6.0.0 schema (club, competitor, ipsc_match,
--      ipsc_match_stage, match_competitor, match_stage_competitor tables with data). It is NOT
--      a from-scratch schema creation script. For a genuinely empty database, run this project's
--      V1__baseline_v6_0_0_schema.sql first (it creates that same v6.0.0 shape from scratch) --
--      V1 + this file together take an empty database to v7.0.0. Pre-existing v6.0.0 databases
--      should instead be baselined at version 6.0.0 (see spring.flyway.baseline-version in
--      application.properties) so V1 is skipped and only this file runs.
--   2. club.abbreviation already holds exactly 'HPSC' / 'SOSC' / 'PMPSC' for the three clubs --
--      the same strings ClubIdentifier's converter persists -- so identifier is backfilled from
--      abbreviation directly. If any club's abbreviation differs, fix it before running this.
--   3. Every match_competitor row already has a non-null firearm_type. If not, the NOT NULL
--      step below will fail -- populate it first.
--   4. String/enum columns without a known length are (re)stated as VARCHAR(255), matching
--      Hibernate's default for unspecified-length @Convert-mapped columns. Adjust if your
--      actual column definitions differ.
--   5. competitor.home_club_id is left NULL for all existing rows -- backfilling actual club
--      membership from historical match data is a data task, not a schema migration, and is
--      left for a follow-up script/process.
--   6. Run this against a staging copy of production first. The match_stage_competitor rework
--      re-links every row via a JOIN back to match_competitor and then drops five columns --
--      verify row counts before and after in staging before touching production.
-- =============================================================================================


-- -------------------------------------------------------------------------------------------
-- 1. club.identifier
-- -------------------------------------------------------------------------------------------
ALTER TABLE club
    ADD COLUMN identifier VARCHAR(10) NULL AFTER abbreviation;

UPDATE club
SET identifier = abbreviation
WHERE identifier IS NULL;

ALTER TABLE club
    MODIFY COLUMN identifier VARCHAR(10) NOT NULL,
    ADD CONSTRAINT uk_club_identifier UNIQUE (identifier);


-- -------------------------------------------------------------------------------------------
-- 2. competitor.home_club_id
-- -------------------------------------------------------------------------------------------
ALTER TABLE competitor
    ADD COLUMN home_club_id BIGINT NULL AFTER gender,
    ADD CONSTRAINT fk_competitor_home_club FOREIGN KEY (home_club_id) REFERENCES club (id);


-- -------------------------------------------------------------------------------------------
-- 3. ipsc_match_stage: one stage number per match
-- -------------------------------------------------------------------------------------------
ALTER TABLE ipsc_match_stage
    ADD CONSTRAINT uk_match_stage_number UNIQUE (match_id, stage_number);


-- -------------------------------------------------------------------------------------------
-- 4. match_competitor: overall/club rankings, visitor flag, per-firearm-type uniqueness
-- -------------------------------------------------------------------------------------------
ALTER TABLE match_competitor
    CHANGE COLUMN match_ranking overall_ranking DECIMAL(19, 6) NULL,
    ADD COLUMN club_ranking DECIMAL(19, 6) NULL AFTER overall_ranking,
    ADD COLUMN is_visitor BIT(1) NULL AFTER club_ranking;

-- Backfill is_visitor: true when the competitor's match_club differs from the club hosting the match
UPDATE match_competitor mc
    JOIN ipsc_match m ON m.id = mc.match_id
    JOIN club c ON c.id = m.club_id
SET mc.is_visitor = (mc.match_club <> c.identifier)
WHERE mc.match_club IS NOT NULL;

ALTER TABLE match_competitor
    MODIFY COLUMN firearm_type VARCHAR(255) NOT NULL,
    ADD CONSTRAINT uk_match_competitor_entry UNIQUE (competitor_id, match_id, firearm_type);


-- -------------------------------------------------------------------------------------------
-- 5. match_stage_competitor: re-point at match_competitor instead of duplicating its fields
-- -------------------------------------------------------------------------------------------
ALTER TABLE match_stage_competitor
    ADD COLUMN match_competitor_id BIGINT NULL AFTER id;

-- Backfill: match each stage score back to the specific (competitor, match, firearm type) entry
-- it belongs to. The match is derived via match_stage_id -> ipsc_match_stage.match_id, and the
-- firearm-type comparison is NULL-safe in case older rows never had it set.
UPDATE match_stage_competitor msc
    JOIN ipsc_match_stage ims ON ims.id = msc.match_stage_id
    JOIN match_competitor mc
         ON mc.competitor_id = msc.competitor_id
             AND mc.match_id = ims.match_id
             AND mc.firearm_type <=> msc.firearm_type
SET msc.match_competitor_id = mc.id;

-- If this fails with a not-null-constraint violation, some match_stage_competitor rows could not
-- be matched to a match_competitor row above -- investigate those rows before re-running.
ALTER TABLE match_stage_competitor
    MODIFY COLUMN match_competitor_id BIGINT NOT NULL;

ALTER TABLE match_stage_competitor
    ADD CONSTRAINT fk_msc_match_competitor FOREIGN KEY (match_competitor_id) REFERENCES match_competitor (id);

-- Drop the old FK on competitor_id before dropping the column; its generated name is looked up
-- dynamically since it depends on how the v6.0.0 schema was originally created.
SET @old_fk_name := (SELECT CONSTRAINT_NAME
                      FROM information_schema.KEY_COLUMN_USAGE
                      WHERE TABLE_SCHEMA = DATABASE()
                        AND TABLE_NAME = 'match_stage_competitor'
                        AND COLUMN_NAME = 'competitor_id'
                        AND REFERENCED_TABLE_NAME = 'competitor'
                      LIMIT 1);
SET @drop_old_fk_sql := CONCAT('ALTER TABLE match_stage_competitor DROP FOREIGN KEY ', @old_fk_name);
PREPARE drop_old_fk_stmt FROM @drop_old_fk_sql;
EXECUTE drop_old_fk_stmt;
DEALLOCATE PREPARE drop_old_fk_stmt;

ALTER TABLE match_stage_competitor
    DROP COLUMN competitor_id,
    DROP COLUMN match_club,
    DROP COLUMN competitor_category,
    DROP COLUMN firearm_type,
    DROP COLUMN division,
    DROP COLUMN power_factor,
    ADD CONSTRAINT uk_match_stage_competitor_entry UNIQUE (match_competitor_id, match_stage_id);


-- -------------------------------------------------------------------------------------------
-- 6. shooter_log / shooter_log_entry: new tables
-- -------------------------------------------------------------------------------------------
CREATE TABLE shooter_log
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    competitor_id    BIGINT       NOT NULL,
    club_id          BIGINT       NOT NULL,
    firearm_type     VARCHAR(255) NOT NULL,
    log_value        DECIMAL(19, 6) NULL,
    calculated_date  DATETIME     NOT NULL,
    date_created     DATETIME     NOT NULL,
    date_updated     DATETIME     NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_shooter_log_competitor FOREIGN KEY (competitor_id) REFERENCES competitor (id),
    CONSTRAINT fk_shooter_log_club FOREIGN KEY (club_id) REFERENCES club (id)
) ENGINE = InnoDB;

CREATE TABLE shooter_log_entry
(
    id                   BIGINT   NOT NULL AUTO_INCREMENT,
    shooter_log_id       BIGINT   NOT NULL,
    match_competitor_id  BIGINT   NOT NULL,
    rank_in_log          INT NULL,
    date_created         DATETIME NOT NULL,
    date_updated         DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sle_shooter_log FOREIGN KEY (shooter_log_id) REFERENCES shooter_log (id),
    CONSTRAINT fk_sle_match_competitor FOREIGN KEY (match_competitor_id) REFERENCES match_competitor (id),
    CONSTRAINT uk_shooter_log_entry UNIQUE (shooter_log_id, match_competitor_id)
) ENGINE = InnoDB;
