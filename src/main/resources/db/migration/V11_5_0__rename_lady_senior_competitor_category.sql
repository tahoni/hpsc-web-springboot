-- =============================================================================================
-- V11.5.0 -- Rename the stored competitor category "Lady, Senior" to "Lady Senior", matching
-- the new CompetitorCategory.SENIOR_LADY name. CompetitorCategory.fromName ignores whitespace
-- but not commas, so without this the old value no longer matches any category.
-- =============================================================================================

UPDATE match_competitor
SET competitor_category = 'Lady Senior'
WHERE competitor_category = 'Lady, Senior';

UPDATE shooter_log_competitor
SET competitor_category = 'Lady Senior'
WHERE competitor_category = 'Lady, Senior';

UPDATE shooter_log_overall
SET competitor_category = 'Lady Senior'
WHERE competitor_category = 'Lady, Senior';
