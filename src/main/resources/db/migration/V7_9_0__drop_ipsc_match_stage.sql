-- =============================================================================================
-- V7.9.0 -- Drop ipsc_match_stage and match_stage_competitor. Match stages, and the per-stage
-- competitor results that referenced them, are no longer modelled. match_stage_competitor goes
-- first, as it holds a foreign key to ipsc_match_stage.
-- =============================================================================================

DROP TABLE match_stage_competitor;
DROP TABLE ipsc_match_stage;
