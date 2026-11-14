-- =============================================================================================
-- V11.13.0 -- Link shooter_log_overall to ipsc_match many-to-many through a
-- shooter_log_overall_match join table, backing ShooterLogOverall.matches: the matches used to
-- calculate an overall, being the best matches in the shooter log's matches.
-- =============================================================================================

CREATE TABLE shooter_log_overall_match
(
    shooter_log_overall_id BIGINT NOT NULL,
    match_id               BIGINT NOT NULL,
    PRIMARY KEY (shooter_log_overall_id, match_id),
    CONSTRAINT fk_slom_shooter_log_overall FOREIGN KEY (shooter_log_overall_id) REFERENCES shooter_log_overall (id),
    CONSTRAINT fk_slom_match FOREIGN KEY (match_id) REFERENCES ipsc_match (id)
) ENGINE = InnoDB;

CREATE INDEX idx_slom_match ON shooter_log_overall_match (match_id);
