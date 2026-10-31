-- =============================================================================================
-- V11.11.0 -- Rename the stored semi auto rifle divisions to the new Division names: "Rifle Open"
-- becomes "Rifle Semi Auto Open" and "Rifle Standard" becomes "Rifle Semi Auto Standard".
-- Division.fromName matches only the new names, so without this a stored division reads back as
-- null.
--
-- A value other than "Rifle Open" or "Rifle Standard" is left unchanged ("Rifle Standard Manual"
-- is unaffected, as the match is on the whole value).
-- =============================================================================================

UPDATE match_competitor
SET division = CASE
                   WHEN division = 'Rifle Open' THEN 'Rifle Semi Auto Open'
                   WHEN division = 'Rifle Standard' THEN 'Rifle Semi Auto Standard'
                   ELSE division
    END;

UPDATE shooter_log_competitor
SET division = CASE
                   WHEN division = 'Rifle Open' THEN 'Rifle Semi Auto Open'
                   WHEN division = 'Rifle Standard' THEN 'Rifle Semi Auto Standard'
                   ELSE division
    END;

UPDATE shooter_log_overall
SET division = CASE
                   WHEN division = 'Rifle Open' THEN 'Rifle Semi Auto Open'
                   WHEN division = 'Rifle Standard' THEN 'Rifle Semi Auto Standard'
                   ELSE division
    END;
