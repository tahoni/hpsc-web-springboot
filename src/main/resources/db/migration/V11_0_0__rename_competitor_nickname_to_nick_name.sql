-- =============================================================================================
-- V11.0.0 -- Rename competitor.nickname to nick_name. The column keeps its type and
-- nullability; existing values are preserved.
-- =============================================================================================

ALTER TABLE competitor
    CHANGE COLUMN nickname nick_name VARCHAR(255) NULL;
