-- =============================================================================================
-- V11.7.0 -- Rename the stored divisions to the new Division names, which drop the trailing
-- " Division" (for example "Open Division" becomes "Open" and "Shotgun Semi Division" becomes
-- "Shotgun Semi"), and rename "PCC Optic Division" and "PCC Iron Division" to "PCC Optics" and
-- "PCC Irons". Division.fromName matches only the new names, so without this a stored division
-- reads back as null.
--
-- A value that does not end in " Division", or that starts with "Manual Action" (a division
-- that no longer exists), is left unchanged.
-- =============================================================================================

UPDATE match_competitor
SET division = CASE
                   WHEN division = 'PCC Optic Division' THEN 'PCC Optics'
                   WHEN division = 'PCC Iron Division' THEN 'PCC Irons'
                   WHEN division LIKE '% Division' AND division NOT LIKE 'Manual Action%'
                       THEN SUBSTRING(division, 1, CHAR_LENGTH(division) - 9)
                   ELSE division
    END;

UPDATE shooter_log_competitor
SET division = CASE
                   WHEN division = 'PCC Optic Division' THEN 'PCC Optics'
                   WHEN division = 'PCC Iron Division' THEN 'PCC Irons'
                   WHEN division LIKE '% Division' AND division NOT LIKE 'Manual Action%'
                       THEN SUBSTRING(division, 1, CHAR_LENGTH(division) - 9)
                   ELSE division
    END;

UPDATE shooter_log_overall
SET division = CASE
                   WHEN division = 'PCC Optic Division' THEN 'PCC Optics'
                   WHEN division = 'PCC Iron Division' THEN 'PCC Irons'
                   WHEN division LIKE '% Division' AND division NOT LIKE 'Manual Action%'
                       THEN SUBSTRING(division, 1, CHAR_LENGTH(division) - 9)
                   ELSE division
    END;
