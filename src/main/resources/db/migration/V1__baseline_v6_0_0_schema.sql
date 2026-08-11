-- =============================================================================================
-- V1 -- Baseline schema: the match domain as it existed at v6.0.0.
--
-- This migration exists so a completely EMPTY database can be brought to the current (v7.0.0)
-- schema through the normal Flyway chain: V1 (this file) creates the original six tables as
-- they looked before the v7.0.0 rework, and V7_0_0__evolve_match_domain_for_v7.sql (already in
-- this folder) then alters them the rest of the way -- exactly as it does for a pre-existing
-- v6.0.0 database. Both paths converge on the identical end state.
--
-- Pre-existing v6.0.0 databases (e.g. production) should NOT run this file -- they already have
-- this schema, manually created before Flyway was introduced. That's what
-- spring.flyway.baseline-on-migrate / baseline-version=6.0.0 in application.properties is for:
-- baselining marks everything at or below version 6.0.0 (including this V1) as already applied,
-- so only V7_0_0 runs against them.
--
-- Column types mirror what V7_0_0 assumes it's altering (VARCHAR(255) for unspecified-length
-- string/enum columns, DECIMAL(19,6) for decimals, BIT(1) for booleans) so the two migrations
-- form one consistent pair.
--
-- Note: MatchCompetitor.division has no @Convert/@Enumerated annotation in the v6.0.0 entity
-- (unlike every other enum field, which is explicitly converter-mapped) -- this looks like an
-- oversight carried over from the original code rather than an intentional design. It's modelled
-- here as VARCHAR(255), consistent with how Division is persisted everywhere else, rather than
-- replicating the apparent bug.
-- =============================================================================================

CREATE TABLE club
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    abbreviation  VARCHAR(255) NULL,
    date_created  DATETIME     NOT NULL,
    date_updated  DATETIME     NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_club_name UNIQUE (name)
) ENGINE = InnoDB;

CREATE TABLE competitor
(
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    first_name         VARCHAR(255) NOT NULL,
    last_name          VARCHAR(255) NOT NULL,
    middle_names       VARCHAR(255) NULL,
    nickname           VARCHAR(255) NULL,
    date_of_birth      DATE NULL,
    gender             VARCHAR(255) NULL,
    sapsa_number       INT NULL,
    competitor_number  VARCHAR(255) NULL,
    club_number        VARCHAR(255) NOT NULL,
    id_number          VARCHAR(255) NULL,
    cellphone_number   VARCHAR(255) NULL,
    email_address      VARCHAR(255) NULL,
    date_created       DATETIME     NOT NULL,
    date_updated       DATETIME     NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_competitor_club_number UNIQUE (club_number)
) ENGINE = InnoDB;

CREATE TABLE ipsc_match
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    club_id             BIGINT NULL,
    name                VARCHAR(255) NOT NULL,
    scheduled_date      DATETIME     NOT NULL,
    match_firearm_type  VARCHAR(255) NULL,
    match_category      VARCHAR(255) NULL,
    date_created        DATETIME     NOT NULL,
    date_updated        DATETIME     NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ipsc_match_club FOREIGN KEY (club_id) REFERENCES club (id)
) ENGINE = InnoDB;

CREATE TABLE ipsc_match_stage
(
    id               BIGINT NOT NULL AUTO_INCREMENT,
    match_id         BIGINT NOT NULL,
    stage_number     INT    NOT NULL,
    stage_name       VARCHAR(255) NULL,
    range_number     INT NULL,
    target_paper     INT NULL,
    target_popper    INT NULL,
    target_plates    INT NULL,
    target_disappear INT NULL,
    target_penalty   INT NULL,
    min_rounds       INT NULL,
    max_points       INT NULL,
    date_created     DATETIME NOT NULL,
    date_updated     DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ipsc_match_stage_match FOREIGN KEY (match_id) REFERENCES ipsc_match (id)
) ENGINE = InnoDB;

CREATE TABLE match_competitor
(
    id                    BIGINT NOT NULL AUTO_INCREMENT,
    competitor_id         BIGINT NOT NULL,
    match_id              BIGINT NOT NULL,
    match_club            VARCHAR(255) NULL,
    competitor_category   VARCHAR(255) NULL,
    firearm_type          VARCHAR(255) NULL,
    division              VARCHAR(255) NULL,
    power_factor          VARCHAR(255) NULL,
    match_points          DECIMAL(19, 6) NULL,
    match_ranking         DECIMAL(19, 6) NULL,
    date_created          DATETIME NOT NULL,
    date_updated          DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_match_competitor_competitor FOREIGN KEY (competitor_id) REFERENCES competitor (id),
    CONSTRAINT fk_match_competitor_match FOREIGN KEY (match_id) REFERENCES ipsc_match (id)
) ENGINE = InnoDB;

CREATE TABLE match_stage_competitor
(
    id                    BIGINT NOT NULL AUTO_INCREMENT,
    competitor_id         BIGINT NOT NULL,
    match_stage_id        BIGINT NOT NULL,
    match_club            VARCHAR(255) NULL,
    competitor_category   VARCHAR(255) NULL,
    firearm_type          VARCHAR(255) NULL,
    division              VARCHAR(255) NULL,
    power_factor          VARCHAR(255) NULL,
    score_a               INT NULL,
    score_b               INT NULL,
    score_c               INT NULL,
    score_d               INT NULL,
    points                INT NULL,
    misses                INT NULL,
    penalties             INT NULL,
    procedurals           INT NULL,
    has_deduction         BIT(1) NULL,
    deduction_percentage  DECIMAL(19, 6) NULL,
    time                  DECIMAL(19, 6) NULL,
    hit_factor            DECIMAL(19, 6) NULL,
    stage_points          DECIMAL(19, 6) NULL,
    stage_percentage      DECIMAL(19, 6) NULL,
    stage_ranking         DECIMAL(19, 6) NULL,
    is_disqualified       BIT(1) NULL,
    date_created          DATETIME NOT NULL,
    date_updated          DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_match_stage_competitor_competitor FOREIGN KEY (competitor_id) REFERENCES competitor (id),
    CONSTRAINT fk_match_stage_competitor_stage FOREIGN KEY (match_stage_id) REFERENCES ipsc_match_stage (id)
) ENGINE = InnoDB;
