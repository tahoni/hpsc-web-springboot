-- =============================================================================================
-- V11.3.0 -- Record when each match_competitor row's scores were calculated. The column is
-- optional (nullable), so existing rows stay NULL until they are next calculated.
-- =============================================================================================

ALTER TABLE match_competitor
    ADD COLUMN date_calculated DATETIME(6) NULL AFTER is_visitor;
