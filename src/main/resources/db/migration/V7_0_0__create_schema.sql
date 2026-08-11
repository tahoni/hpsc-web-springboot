-- =============================================================================================
-- V7.0.0 -- Create the match domain schema as it stands at v7.0.0.
--
-- Assumes a genuinely EMPTY database: this creates every table already in its v7.0.0 shape
-- (results per club, visitor tracking, multi-firearm-type match entries, shooter logs), rather
-- than recreating the old v6.0.0 shape and altering it. There is no pre-existing schema to
-- migrate from and no baselining involved -- see spring.flyway.* in application.properties. As
-- the first and only migration, this file's own version (7.0.0) is where Flyway's schema
-- history starts.
--
-- Column types use VARCHAR(255) for unspecified-length string/enum columns and DECIMAL(19,6)
-- for decimals, matching Hibernate's defaults for the corresponding @Convert-mapped entity
-- fields.
--
-- Note: MatchCompetitor.division has no @Convert/@Enumerated annotation in the entity (unlike
-- every other enum field, which is explicitly converter-mapped) -- this looks like an oversight
-- carried over from earlier versions rather than an intentional design. It's modelled here as
-- VARCHAR(255), consistent with how Division is persisted everywhere else, rather than
-- replicating the apparent bug.
-- =============================================================================================

CREATE TABLE club
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    abbreviation  VARCHAR(255) NULL,
    identifier    VARCHAR(10)  NOT NULL,
    date_created  DATETIME     NOT NULL,
    date_updated  DATETIME     NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_club_name UNIQUE (name),
    CONSTRAINT uk_club_identifier UNIQUE (identifier)
) ENGINE = InnoDB;

CREATE TABLE competitor
(
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    first_name         VARCHAR(255) NOT NULL,
    last_name          VARCHAR(255) NOT NULL,
    middle_names       VARCHAR(255) NULL,
    nickname           VARCHAR(255) NULL,
    date_of_birth      DATE         NULL,
    gender             VARCHAR(255) NULL,
    home_club_id       BIGINT       NULL,
    sapsa_number       INT          NULL,
    competitor_number  VARCHAR(255) NULL,
    club_number        VARCHAR(255) NOT NULL,
    id_number          VARCHAR(255) NULL,
    cellphone_number   VARCHAR(255) NULL,
    email_address      VARCHAR(255) NULL,
    date_created       DATETIME     NOT NULL,
    date_updated       DATETIME     NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_competitor_club_number UNIQUE (club_number),
    CONSTRAINT fk_competitor_home_club FOREIGN KEY (home_club_id) REFERENCES club (id)
) ENGINE = InnoDB;

CREATE TABLE ipsc_match
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    club_id             BIGINT       NULL,
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
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    match_id          BIGINT       NOT NULL,
    stage_number      INT          NOT NULL,
    stage_name        VARCHAR(255) NULL,
    range_number      INT          NULL,
    target_paper      INT          NULL,
    target_popper     INT          NULL,
    target_plates     INT          NULL,
    target_disappear  INT          NULL,
    target_penalty    INT          NULL,
    min_rounds        INT          NULL,
    max_points        INT          NULL,
    date_created      DATETIME     NOT NULL,
    date_updated      DATETIME     NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ipsc_match_stage_match FOREIGN KEY (match_id) REFERENCES ipsc_match (id),
    CONSTRAINT uk_match_stage_number UNIQUE (match_id, stage_number)
) ENGINE = InnoDB;

CREATE TABLE match_competitor
(
    id                   BIGINT         NOT NULL AUTO_INCREMENT,
    competitor_id        BIGINT         NOT NULL,
    match_id             BIGINT         NOT NULL,
    match_club           VARCHAR(255)   NULL,
    competitor_category  VARCHAR(255)   NULL,
    firearm_type         VARCHAR(255)   NOT NULL,
    division             VARCHAR(255)   NULL,
    power_factor         VARCHAR(255)   NULL,
    match_points         DECIMAL(19, 6) NULL,
    overall_ranking      DECIMAL(19, 6) NULL,
    club_ranking         DECIMAL(19, 6) NULL,
    is_visitor           BIT(1)         NULL,
    date_created         DATETIME       NOT NULL,
    date_updated         DATETIME       NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_match_competitor_competitor FOREIGN KEY (competitor_id) REFERENCES competitor (id),
    CONSTRAINT fk_match_competitor_match FOREIGN KEY (match_id) REFERENCES ipsc_match (id),
    CONSTRAINT uk_match_competitor_entry UNIQUE (competitor_id, match_id, firearm_type)
) ENGINE = InnoDB;

CREATE TABLE match_stage_competitor
(
    id                    BIGINT         NOT NULL AUTO_INCREMENT,
    match_competitor_id   BIGINT         NOT NULL,
    match_stage_id        BIGINT         NOT NULL,
    score_a               INT            NULL,
    score_b               INT            NULL,
    score_c               INT            NULL,
    score_d               INT            NULL,
    points                INT            NULL,
    misses                INT            NULL,
    penalties             INT            NULL,
    procedurals           INT            NULL,
    has_deduction         BIT(1)         NULL,
    deduction_percentage  DECIMAL(19, 6) NULL,
    time                  DECIMAL(19, 6) NULL,
    hit_factor            DECIMAL(19, 6) NULL,
    stage_points          DECIMAL(19, 6) NULL,
    stage_percentage      DECIMAL(19, 6) NULL,
    stage_ranking         DECIMAL(19, 6) NULL,
    is_disqualified       BIT(1)         NULL,
    date_created          DATETIME       NOT NULL,
    date_updated          DATETIME       NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_match_stage_competitor_stage FOREIGN KEY (match_stage_id) REFERENCES ipsc_match_stage (id),
    CONSTRAINT fk_msc_match_competitor FOREIGN KEY (match_competitor_id) REFERENCES match_competitor (id),
    CONSTRAINT uk_match_stage_competitor_entry UNIQUE (match_competitor_id, match_stage_id)
) ENGINE = InnoDB;

CREATE TABLE shooter_log
(
    id               BIGINT         NOT NULL AUTO_INCREMENT,
    competitor_id    BIGINT         NOT NULL,
    club_id          BIGINT         NOT NULL,
    firearm_type     VARCHAR(255)   NOT NULL,
    log_value        DECIMAL(19, 6) NULL,
    calculated_date  DATETIME       NOT NULL,
    date_created     DATETIME       NOT NULL,
    date_updated     DATETIME       NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_shooter_log_competitor FOREIGN KEY (competitor_id) REFERENCES competitor (id),
    CONSTRAINT fk_shooter_log_club FOREIGN KEY (club_id) REFERENCES club (id)
) ENGINE = InnoDB;

CREATE TABLE shooter_log_entry
(
    id                   BIGINT   NOT NULL AUTO_INCREMENT,
    shooter_log_id       BIGINT   NOT NULL,
    match_competitor_id  BIGINT   NOT NULL,
    rank_in_log          INT      NULL,
    date_created         DATETIME NOT NULL,
    date_updated         DATETIME NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sle_shooter_log FOREIGN KEY (shooter_log_id) REFERENCES shooter_log (id),
    CONSTRAINT fk_sle_match_competitor FOREIGN KEY (match_competitor_id) REFERENCES match_competitor (id),
    CONSTRAINT uk_shooter_log_entry UNIQUE (shooter_log_id, match_competitor_id)
) ENGINE = InnoDB;
