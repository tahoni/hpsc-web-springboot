-- =============================================================================================
-- V10.0.0 -- Rename match_competitor.match_points to points. The column keeps its type and
-- nullability; existing values are preserved.
-- =============================================================================================

ALTER TABLE match_competitor
    CHANGE COLUMN match_points points DECIMAL(19, 6) NULL;
