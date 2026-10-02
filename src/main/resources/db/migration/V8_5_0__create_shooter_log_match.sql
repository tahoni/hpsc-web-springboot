-- =============================================================================================
-- V8.5.0 -- Link shooter_log to ipsc_match many-to-many through a shooter_log_match join table,
-- backing ShooterLog.matches: one shooter log covers many matches, and one match can appear in
-- many shooter logs.
-- =============================================================================================

CREATE TABLE shooter_log_match
(
    shooter_log_id BIGINT NOT NULL,
    match_id       BIGINT NOT NULL,
    PRIMARY KEY (shooter_log_id, match_id),
    CONSTRAINT fk_slm_shooter_log FOREIGN KEY (shooter_log_id) REFERENCES shooter_log (id),
    CONSTRAINT fk_slm_match FOREIGN KEY (match_id) REFERENCES ipsc_match (id)
) ENGINE = InnoDB;

CREATE INDEX idx_slm_match ON shooter_log_match (match_id);
