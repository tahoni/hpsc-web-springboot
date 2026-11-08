-- =============================================================================================
-- V11.12.0 -- Drop the shooter_log_competitor table, along with the ShooterLogCompetitor entity
-- that mapped it. Its per-competitor rank and percentage in a log now live in shooter_result
-- (V11.10.0), and nothing references the table any more (its child
-- shooter_log_competitor_category table was dropped in V10.2.0).
--
-- This discards any rows in the table.
-- =============================================================================================

DROP TABLE shooter_log_competitor;
