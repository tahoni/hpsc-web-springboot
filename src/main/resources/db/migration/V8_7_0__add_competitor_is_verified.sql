-- =============================================================================================
-- V8.7.0 -- Add competitor.is_verified, flagging whether a competitor has been verified. The
-- column is optional (nullable); every competitor that already exists is marked verified.
-- =============================================================================================

ALTER TABLE competitor
    ADD COLUMN is_verified BOOLEAN NULL AFTER paid_up_club;

UPDATE competitor
SET is_verified = TRUE;
