-- =============================================================================================
-- V7.4.0 -- Make competitor.club_number nullable.
--
-- club_number is now only meaningful for HPSC's own members: IpscCompetitorServiceImpl requires
-- it when the competitor's home club is HPSC, and forces it to NULL for every other home club,
-- including none. The existing uk_competitor_club_number unique constraint is left in place --
-- MySQL/H2 treat multiple NULLs as distinct under a UNIQUE constraint, so this doesn't relax
-- uniqueness for populated values.
--
-- Existing rows are brought in line with the new rule: any competitor whose home club isn't HPSC
-- (including those with no home club at all) has club_number cleared, matching what
-- IpscCompetitorServiceImpl would now compute for them.
-- =============================================================================================

ALTER TABLE competitor
    MODIFY COLUMN club_number VARCHAR(255) NULL;

UPDATE competitor c
    LEFT JOIN club hc ON hc.id = c.home_club_id
SET c.club_number = NULL
WHERE (hc.identifier IS NULL OR hc.identifier <> 'HPSC');
