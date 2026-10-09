-- =============================================================================================
-- V11.8.0 -- Remove match_competitor.overall_ranking and club_ranking (MatchCompetitor no longer has them).
-- =============================================================================================

ALTER TABLE match_competitor
    DROP COLUMN overall_ranking,
    DROP COLUMN club_ranking;
