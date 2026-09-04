ALTER TABLE ipsc_match
    ADD COLUMN start_time DATETIME NULL AFTER scheduled_date,
    ADD COLUMN end_time   DATETIME NULL AFTER start_time;
