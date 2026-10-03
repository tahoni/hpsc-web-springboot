-- =============================================================================================
-- V10.1.0 -- Add the overall-score columns to match_competitor, after points: percentage, time,
-- percentage_of_possible_points, hit_factor, the hit-zone counts (alpha, charlie, delta), and
-- the miss and penalty counts. All are optional (nullable).
-- =============================================================================================

ALTER TABLE match_competitor
    ADD COLUMN percentage DECIMAL(19, 6) NULL AFTER points,
    ADD COLUMN time DECIMAL(19, 6) NULL AFTER percentage,
    ADD COLUMN percentage_of_possible_points DECIMAL(19, 6) NULL AFTER time,
    ADD COLUMN hit_factor DECIMAL(19, 6) NULL AFTER percentage_of_possible_points,
    ADD COLUMN alpha INT NULL AFTER hit_factor,
    ADD COLUMN charlie INT NULL AFTER alpha,
    ADD COLUMN delta INT NULL AFTER charlie,
    ADD COLUMN misses INT NULL AFTER delta,
    ADD COLUMN no_penalty_misses INT NULL AFTER misses,
    ADD COLUMN no_shoots INT NULL AFTER no_penalty_misses,
    ADD COLUMN procedural_errors INT NULL AFTER no_shoots,
    ADD COLUMN additional_penalties INT NULL AFTER procedural_errors;
