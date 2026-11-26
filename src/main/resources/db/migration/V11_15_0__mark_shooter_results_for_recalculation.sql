-- =============================================================================================
-- V11.15.0 -- Shooter results and shooter log overall results are no longer ranked within a
-- competitor category, so every rank and percentage stored before this migration is out of
-- date. Clearing date_calculated marks each row as not yet calculated, which the shooter result
-- and shooter log overall services treat as out of date: the next calculation of a match or a
-- shooter log ranks it again, instead of returning the old results as they are. No row is
-- deleted, so the old ranks are still returned until then.
-- =============================================================================================

UPDATE shooter_result
SET date_calculated = NULL;

UPDATE shooter_log_overall
SET date_calculated = NULL;
