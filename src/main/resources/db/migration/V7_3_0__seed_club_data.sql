-- =============================================================================================
-- V7.3.0 -- Seed the club table with the recognised clubs from ClubIdentifier.
--
-- Persists the 5 named ClubIdentifier constants (SOSC, HPSC, PMPSC, VISITOR, ALL); UNKNOWN is the
-- default/unmatched placeholder and is not a real club, so it is intentionally excluded. name and
-- abbreviation come from ClubIdentifier.name/abbreviation; identifier is set to the same
-- abbreviation, matching ClubIdentifierConverter's persisted representation.
-- =============================================================================================

INSERT INTO club (name, abbreviation, identifier, date_created)
VALUES ('Safari Outdoor Shooting Club', 'SOSC', 'SOSC', CURRENT_TIMESTAMP),
       ('Hartbeespoortdam Practical Shooting Club', 'HPSC', 'HPSC', CURRENT_TIMESTAMP),
       ('Pretoria Military Practical Shooting Club', 'PMPSC', 'PMPSC', CURRENT_TIMESTAMP),
       ('Visitor', 'V', 'V', CURRENT_TIMESTAMP),
       ('Eufees Clubs', 'All', 'All', CURRENT_TIMESTAMP);
