-- =============================================================================================
-- V8.1.0 -- Align match_competitor's column nullability with the MatchCompetitor mappings:
-- division and competitor_category become NOT NULL (nullable = false), and firearm_type
-- becomes nullable.
--
-- Making division/competitor_category NOT NULL fails if any existing match_competitor row has a
-- NULL in either column -- backfill those rows first.
--
-- uk_match_competitor_entry (competitor_id, match_id, firearm_type) is unchanged, but MySQL
-- treats NULLs as distinct in a unique index, so rows with a NULL firearm_type are no longer
-- constrained to one per competitor and match.
-- =============================================================================================

ALTER TABLE match_competitor
    MODIFY COLUMN competitor_category VARCHAR(255) NOT NULL,
    MODIFY COLUMN division VARCHAR(255) NOT NULL,
    MODIFY COLUMN firearm_type VARCHAR(255) NULL;
