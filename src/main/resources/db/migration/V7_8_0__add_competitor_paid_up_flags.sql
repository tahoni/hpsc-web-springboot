-- =============================================================================================
-- V7.8.0 -- Add competitor.paid_up_sapsa and competitor.paid_up_club, flagging whether a
-- competitor's SAPSA and club memberships are paid up.
-- =============================================================================================

ALTER TABLE competitor
    ADD COLUMN paid_up_sapsa BOOLEAN NULL AFTER cellphone_number,
    ADD COLUMN paid_up_club  BOOLEAN NULL AFTER paid_up_sapsa;
