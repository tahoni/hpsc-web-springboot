-- =============================================================================================
-- V10.3.0 -- Remove match_competitor.hit_factor (MatchCompetitor no longer has hitFactor).
-- =============================================================================================

ALTER TABLE match_competitor
    DROP COLUMN hit_factor;
