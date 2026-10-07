-- =============================================================================================
-- V11.4.0 -- Give each non-handgun Division a name that is unique across all divisions, so
-- DivisionConverter can read a division back from its name alone. Before this, the shotgun,
-- .22 and mini rifle divisions shared a display name with a handgun one (for example
-- "Open Division"), and always read back as the handgun division.
--
-- The semi auto rifle divisions become "Rifle Open Division" and "Rifle Standard Division", and
-- "Standard Manual Division" becomes "Shotgun Standard Manual Division". The
-- handgun and PCC divisions keep their names. Each row's division is renamed
-- according to its firearm type, taken from match_competitor.firearm_type (and, for
-- shooter_log_competitor, from the match_competitor it points at).
--
-- shooter_log_overall is not changed: it has no firearm type to tell the divisions apart, so
-- its rows keep the names they were written with, which read back as the handgun divisions.
-- =============================================================================================

UPDATE match_competitor
SET division = CASE
                   WHEN firearm_type = 'Shotgun' AND division = 'Open Division' THEN 'Shotgun Open Division'
                   WHEN firearm_type = 'Shotgun' AND division = 'Modified Division' THEN 'Shotgun Modified Division'
                   WHEN firearm_type = 'Shotgun' AND division = 'Standard Division' THEN 'Shotgun Semi Division'
                   WHEN firearm_type = 'Handgun .22' AND division = 'Open Division' THEN '.22 Open Division'
                   WHEN firearm_type = 'Handgun .22' AND division = 'Standard Division' THEN '.22 Standard Division'
                   WHEN firearm_type = 'Handgun .22' AND division = 'Classic Division' THEN '.22 Classic Division'
                   WHEN firearm_type = 'Handgun .22' AND division = 'Optics Division' THEN '.22 Optics Division'
                   WHEN firearm_type = 'Mini Rifle' AND division = 'Open Division' THEN 'Mini Rifle Open Division'
                   WHEN firearm_type = 'Mini Rifle' AND division = 'Standard Division' THEN 'Mini Rifle Standard Division'
                   ELSE division
    END;

UPDATE shooter_log_competitor slc
    JOIN match_competitor mc ON mc.id = slc.match_competitor_id
SET slc.division = CASE
                   WHEN mc.firearm_type = 'Shotgun' AND slc.division = 'Open Division' THEN 'Shotgun Open Division'
                   WHEN mc.firearm_type = 'Shotgun' AND slc.division = 'Modified Division' THEN 'Shotgun Modified Division'
                   WHEN mc.firearm_type = 'Shotgun' AND slc.division = 'Standard Division' THEN 'Shotgun Semi Division'
                   WHEN mc.firearm_type = 'Handgun .22' AND slc.division = 'Open Division' THEN '.22 Open Division'
                   WHEN mc.firearm_type = 'Handgun .22' AND slc.division = 'Standard Division' THEN '.22 Standard Division'
                   WHEN mc.firearm_type = 'Handgun .22' AND slc.division = 'Classic Division' THEN '.22 Classic Division'
                   WHEN mc.firearm_type = 'Handgun .22' AND slc.division = 'Optics Division' THEN '.22 Optics Division'
                   WHEN mc.firearm_type = 'Mini Rifle' AND slc.division = 'Open Division' THEN 'Mini Rifle Open Division'
                   WHEN mc.firearm_type = 'Mini Rifle' AND slc.division = 'Standard Division' THEN 'Mini Rifle Standard Division'
                   ELSE slc.division
    END;

UPDATE match_competitor
SET division = CASE division
                   WHEN 'Semi Auto Open Division' THEN 'Rifle Open Division'
                   WHEN 'Semi Auto Standard Division' THEN 'Rifle Standard Division'
                   WHEN 'Standard Manual Division' THEN 'Shotgun Standard Manual Division'
                   ELSE division
    END;

UPDATE shooter_log_competitor
SET division = CASE division
                   WHEN 'Semi Auto Open Division' THEN 'Rifle Open Division'
                   WHEN 'Semi Auto Standard Division' THEN 'Rifle Standard Division'
                   WHEN 'Standard Manual Division' THEN 'Shotgun Standard Manual Division'
                   ELSE division
    END;
