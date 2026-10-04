-- =============================================================================================
-- V11.1.0 -- Change competitor.competitor_number from VARCHAR(255) to INT. The column stays
-- optional (nullable). Every existing value must already be a whole number: MySQL refuses the
-- change, and leaves the column as it was, if any value is not.
-- =============================================================================================

ALTER TABLE competitor
    MODIFY COLUMN competitor_number INT NULL;
