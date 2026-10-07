-- =============================================================================================
-- V11.6.0 -- Add competitor.paid_up_ngpsa, flagging whether a competitor's NGPSA membership
-- is paid up.
-- =============================================================================================

ALTER TABLE competitor
    ADD COLUMN paid_up_ngpsa BOOLEAN NULL AFTER paid_up_sapsa;
