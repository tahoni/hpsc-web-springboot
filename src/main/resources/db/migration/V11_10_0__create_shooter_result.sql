-- =============================================================================================
-- V11.10.0 -- Create shooter_result, backing the ShooterResult entity: a competitor's result in a
-- match, with the rank and percentage in the log and the date they were calculated. It mirrors
-- shooter_log_competitor, linked to a match rather than a shooter log.
-- =============================================================================================

CREATE TABLE shooter_result
(
    id                  BIGINT         NOT NULL AUTO_INCREMENT,
    match_id            BIGINT         NOT NULL,
    competitor_id       BIGINT         NOT NULL,
    match_competitor_id BIGINT         NOT NULL,
    competitor_category VARCHAR(255)   NOT NULL,
    division            VARCHAR(255)   NOT NULL,
    rank_in_log         INT            NULL,
    percentage_in_log   DECIMAL(19, 6) NULL,
    date_calculated     DATETIME(6)    NULL,
    date_created        DATETIME       NOT NULL,
    date_updated        DATETIME       NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sr_match FOREIGN KEY (match_id) REFERENCES ipsc_match (id),
    CONSTRAINT fk_sr_competitor FOREIGN KEY (competitor_id) REFERENCES competitor (id),
    CONSTRAINT fk_sr_match_competitor FOREIGN KEY (match_competitor_id) REFERENCES match_competitor (id),
    CONSTRAINT uk_sr_match_match_competitor UNIQUE (match_id, match_competitor_id)
) ENGINE = InnoDB;
