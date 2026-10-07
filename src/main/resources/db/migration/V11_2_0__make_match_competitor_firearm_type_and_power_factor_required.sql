-- =============================================================================================
-- V11.2.0 -- Make match_competitor.firearm_type and match_competitor.power_factor NOT NULL, to
-- match the MatchCompetitor mappings (nullable = false). This reverses the firearm_type half of
-- V8.1.0, which had made it nullable.
--
-- MySQL refuses the change, and leaves the columns as they were, if any existing row has a NULL
-- in either column -- backfill those rows first, for example:
--   SELECT id FROM match_competitor WHERE firearm_type IS NULL OR power_factor IS NULL;
--
-- uk_match_competitor_entry (competitor_id, match_id, firearm_type) is unchanged, but now that
-- firearm_type can no longer be NULL it constrains every row to one entry per competitor, match
-- and firearm type again.
-- =============================================================================================

ALTER TABLE match_competitor
    MODIFY COLUMN firearm_type VARCHAR(255) NOT NULL,
    MODIFY COLUMN power_factor VARCHAR(255) NOT NULL;
