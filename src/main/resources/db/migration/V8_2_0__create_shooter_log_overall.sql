-- =============================================================================================
-- V8.2.0 -- Create shooter_log_overall, backing the ShooterLogOverall entity: a rank and points
-- per competitor category and division, with the date they were calculated.
-- =============================================================================================

CREATE TABLE shooter_log_overall
(
    id                   BIGINT         NOT NULL AUTO_INCREMENT,
    competitor_category  VARCHAR(255)   NOT NULL,
    division             VARCHAR(255)   NOT NULL,
    rank_in_log          INT            NULL,
    points               DECIMAL(19, 6) NULL,
    date_calculated      DATETIME       NULL,
    date_created         DATETIME       NOT NULL,
    date_updated         DATETIME       NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;
