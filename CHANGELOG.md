# HPSC Website Backend

## 🧾 Change Log

All notable changes to the HPSC Website Backend project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) as of version 5.0.0.

---

### Table of Contents

- [🧪 Unreleased](#-unreleased)
- [🧾 Version 15.0.0](#-1500---2026-10-17) ← Current
- [🧾 Version 14.0.0](#-1400---2026-10-09)
- [🗄️ Archived Versions 10.0.0 – 13.3.0](/documentation/archive/v10-v13/CHANGELOG_v10-v13.md)
- [🗄️ Archived Versions 8.0.0 – 9.1.0](/documentation/archive/v8-v9/CHANGELOG_v8-v9.md)
- [🗄️ Archived Versions 5.0.0 – 7.4.1](/documentation/archive/v5-v7/CHANGELOG_v5-v7.md)
- [🗄️ Archived Versions 1.0.0 – 4.1.0](/documentation/archive/v1-v4/CHANGELOG_v1-v4.md)
- [📋 Version Policy](#-version-policy)
- [🚀 Upgrade Guide](#-upgrade-guide)
- [🤝 Contributing](#-contributing)
- [💬 Support](#-support)

---

### 🧪 [Unreleased]

#### ➕ Added

##### Database

- **`V11_11_0__rename_semi_auto_rifle_division_names.sql`:** New migration that renames the stored `division` "Rifle
  Open" and "Rifle Standard" in `match_competitor`, `shooter_log_competitor` and `shooter_log_overall` to "Rifle Semi
  Auto Open" and "Rifle Semi Auto Standard"; without it an existing row's division reads back as `null`

##### Domain

- **`Division.RIFLE_PCC`:** New "Rifle PCC" rifle division

##### Tests

- **`CompetitorHelpersTest`:** New `cleanCompetitorName` tests for removing a one-, two- or three-digit trailing
  suffix, alone and after a position prefix, and for keeping digits that are not at the end of the name

#### 🔄 Changed

##### Utilities

- **`CompetitorHelpers`:** Competitor names now have a trailing modified-entry digit stripped, via
  `MODIFIED_SUFFIX`, when they are normalised
- **`IpscConstants.REPLACE_IN_NAMES_REGEX`:** Now also removes a "TRO" (or "(TRO)") role marker from competitor
  names before they are matched by full name

##### Domain

- **`Division`:** **Breaking:** `RIFLE_SEMI_AUTO_OPEN` and `RIFLE_SEMI_AUTO_STANDARD` display names are now "Rifle Semi
  Auto Open" and "Rifle Semi Auto Standard" (were "Rifle Open" and "Rifle Standard"), so a division given by the old
  names no longer resolves

##### Services

- **`IpscMatchCompetitorServiceImpl.createMatchCompetitors`:** Now `@Transactional`, so a bulk import is saved or
  rolled back as one unit

##### Documentation

- **`documentation/recommendations/flyway-migration-versioning.md`:** Current State table gains a row for
  `V11_11_0__rename_semi_auto_rifle_division_names.sql`

---

### 🧾 [15.0.0] - 2026-10-17

#### ➕ Added

##### Database

- **`V11_10_0__create_shooter_result.sql`:** New migration creating `shooter_result`, linked to `ipsc_match`,
  `competitor` and `match_competitor`, unique per match and match competitor

##### Domain

- **`ShooterResult`:** New entity mirroring `ShooterLogCompetitor`, with a `match` in place of the `shooterLog`

##### Repositories

- **`ShooterResultRepository`:** New repository with `findAllByMatchIdWithDetails` and `findAllWithDetails`, which
  fetch-join the match, competitor and match competitor of one match's results, or of every result
- **`MatchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch`:** New query that fetch-joins the competitor
  and match for one match's entries

##### Models

- **`ShooterResultResponse`:** New response describing a shooter result — its ID, the match, competitor and match
  competitor IDs, the competitor's names and number, their category and division, and their rank and percentage in
  the log, the percentage as a `String` formatted to `IpscConstants.PERCENTAGE_SCALE` for display, with the date they
  were calculated
- **`ShooterMatchResultResponse`:** New response describing the match a set of shooter results were shot in — its ID,
  name and date
- **`ShooterResponseHolder`:** New container linking one `ShooterMatchResultResponse` with the
  `ShooterResultResponse`s of the competitors who shot that match

##### Services

- **`IpscShooterResultService`:** New service with `calculateMatchShooterResult`, which creates or updates a
  `ShooterResult` for each of a match's competitors — ranking them within their firearm type, division and competitor
  category (those with none a group of their own) by match points, highest first, with ties sharing a rank, and
  setting each one's percentage to their points as a percentage of the group winner's, and the date calculated — then
  returns the match with its results. It only recalculates when the results are out of date, that is when a competitor
  has no result yet or the match or a competitor was updated at least five minutes after the oldest result was
  calculated, and otherwise returns the current results as they are. The service also has `getShooterResults`, which
  returns a match linked to its results, and `getAllShooterResults`, which returns one such holder per match, oldest
  match date first (then by ID), with no results for a match nobody has a result in; `IpscShooterResultServiceImpl`
  throws `NonFatalException` for a match that does not exist
- **`TransactionService.saveShooterResults`:** New method that saves a batch of shooter results in one transaction,
  flushing so a unique constraint violation surfaces as a `DataIntegrityViolationException`

##### Controllers

- **`IpscShooterResultController`:** New endpoint `POST /ipsc/shooter-results/matches/{matchId}`, which calculates
  a match's shooter results and returns the match linked to them, and read-only endpoints
  `GET /ipsc/shooter-results/matches/{matchId}` and `GET /ipsc/shooter-results`, returning a match linked to its
  shooter results, or one such holder per match, including matches without results; an unknown match ID returns a
  404

##### Utilities

- **`NumberUtil`:** Reinstated, with its tests, after its removal in 14.0.0 — `calculatePercentage`, `calculateSum`
  and `formatBigDecimal` each take the `scale` to round to (`RoundingMode.HALF_UP`), with a null or zero whole giving
  a zero percentage and a null value or list giving zero

##### Constants

- **`IpscConstants`:** New `MATCH_POINTS_SCALE`, `HIT_FACTOR_SCALE`, `TIME_SCALE` and `PERCENTAGE_SCALE` decimal-place
  constants for scores; only `PERCENTAGE_SCALE` is referenced by code so far, by the shooter result calculation and
  `ShooterResultResponse`
- **`SystemConstants.DEFAULT_SCALE`:** Reinstated, with a value of 5, as the default scale for `NumberUtil`
  calculations

#### 🔄 Changed

##### Database

- **`V11_9_0__rename_shooter_log_points_to_percentage_in_log.sql`:** New migration that renames `points` to
  `percentage_in_log` in `shooter_log_competitor` and `shooter_log_overall`

##### Domain

- **`ShooterLogCompetitor`, `ShooterLogOverall`:** **Breaking:** renamed `points` to `percentageInLog`, and its
  column to `percentage_in_log`

##### Mappers

- **`MatchCompetitorMapper`:** No longer copies `overallRanking` or `clubRanking` from a request onto the entity,
  on create or patch, since the entity no longer has them

##### Controllers

- **`IpscMatchCompetitorController`:** Updated the bulk CSV example to drop the `OverallRanking` and `ClubRanking`
  columns

##### Documentation

- **Archived versions 10.0.0 – 13.3.0:** Moved the `CHANGELOG`, `HISTORY` and `EVOLUTION_OVERVIEW` entries of those
  versions into `CHANGELOG_v10-v13.md`, `HISTORY_v10-v13.md` and `EVOLUTION_OVERVIEW_v10-v13.md`, and the v10 – v13
  release notes and pull request descriptions, from `documentation/history/` to `documentation/archive/v10-v13/`; the
  links in `README.md`, `AGENTS.md`, `CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md` and the
  `prep-version-release` skill follow the new locations, and the thirteen v8.5.1 – v8.12.0 pull request descriptions
  now link to their entries in `CHANGELOG_v8-v9.md` rather than to `CHANGELOG.md` anchors that no longer exist

#### 🗑️ Removed

##### Database

- **`V11_8_0__remove_match_competitor_rankings.sql`:** New migration that drops the `overall_ranking` and
  `club_ranking` columns from `match_competitor`

##### Domain

- **`MatchCompetitor`:** **Breaking:** removed the `overallRanking` and `clubRanking` fields and their columns

##### Models

- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`:** **Breaking:** removed the optional `overallRanking` and
  `clubRanking` from the create, replace and patch request bodies and from the constructor, so rankings can no longer
  be set through the API
- **`MatchCompetitorRequestCsvMixIn`:** **Breaking:** removed the optional `OverallRanking` and `ClubRanking` columns
  from the bulk match competitor CSV import
- **`MatchCompetitorResponse`:** **Breaking:** removed `overallRanking` and `clubRanking` from the response body
- **`MatchCompetitorRow`:** Removed `overallRanking` and `clubRanking` from the all-text import row, so a failed bulk
  row no longer reports them

#### 🔐 Security

##### Dependencies

- **`org.ow2.asm:asm`:** Pinned to 9.11 through `dependencyManagement` and a new `asm.version` property, replacing
  the vulnerable 9.7.1 that `json-smart`'s `accessors-smart` brings in transitively through
  `spring-boot-starter-test`

---

### 🧾 [14.0.0] - 2026-10-09

#### ➕ Added

##### Database

- **`V11_7_0__drop_division_suffix_from_division_names.sql`:** New migration that renames the stored `division` in
  `match_competitor`, `shooter_log_competitor` and `shooter_log_overall` to the new `Division` names, dropping the
  trailing " Division" and mapping `PCC Optic Division` and `PCC Iron Division` to `PCC Optics` and `PCC Irons`; without
  it an existing row's division reads back as `null`

##### Models

- **`MatchCompetitorRow`:** New all-text description of a bulk import row, with every value the row supplied and a
  missing value as an empty string; `MatchCompetitorBulkResponse` carries it as `row` for a row that failed

##### Helpers

- **`MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields`:** New helper that describes each required field
  still unset on a match competitor, saying whether the request left it out or gave a value that could not be
  resolved; a blank or generic competitor number is treated as not specified

##### Services

- **`IpscEntityClubService.findByCodeOrAbbreviation`:** New lookup of a club by abbreviation, name, or club identifier
  code, abbreviation or name; `IpscEntityClubServiceImpl` now takes a `ClubRepository`. An unknown club throws
  `ValidationException`, and a blank code throws `NonFatalException`
- **`IpscEntityClubService.findByCodeOrAbbreviationWithDefault`:** New lookup that uses a default club identifier when
  the club name is blank

#### 🔄 Changed

##### Enums

- **`CompetitorCategory.fromName`:** Now returns an empty `Optional` for an unrecognised name instead of falling back
  to `NONE`; a null or blank name resolves to `NONE`
- **`Division`:** **Breaking:** dropped the trailing " Division" from each division's name (for example "Open" instead of
  "Open Division"), so divisions are matched by their short names, and a value in the old form no longer resolves
- **`Division`:** **Breaking:** `PCC_OPTICS` and `PCC_IRON` are renamed `"PCC Optics"` and `"PCC Irons"` (from
  `"PCC Optic"` and `"PCC Iron"`), so a division given by the singular name no longer resolves

##### Helpers

- **`CompetitorHelpers.cleanCompetitorName`:** Now removes every leading position prefix, not only the first
- **`MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields`:** The "Competitor not found for name"
  message now quotes the competitor name cleaned by `CompetitorHelpers.cleanCompetitorName`

##### Mappers

- **`CompetitorMapper.resolveHomeClub`:** **Breaking:** now resolves a competitor's home club through
  `IpscEntityClubService.findByCodeOrAbbreviationWithDefault`, so it can be given by club identifier code,
  abbreviation or name as well as abbreviation or name; an unknown home club now throws `ValidationException` (400)
  instead of `NonFatalException` (404). A blank home club is still left unset. `CompetitorMapper` no longer takes a
  `ClubRepository`
- **`MatchMapper.resolveClub`:** **Breaking:** now resolves a club through
  `IpscEntityClubService.findByCodeOrAbbreviationWithDefault`, so a match's club can be given by abbreviation, name,
  or club identifier code or abbreviation; an unknown club now throws `ValidationException` (400) instead of
  `NonFatalException` (404). `MatchMapper` no longer takes a `ClubRepository`, and `FatalException` is dropped from
  `resolveClub`, `applyFields`, `applyPatchFields` and the match service and controller methods that only declared it
  for the club lookup
- **`MatchCompetitorMapper.resolveCompetitorCategory`:** A null or blank competitor category now resolves to
  `CompetitorCategory.NONE` instead of throwing; an unrecognised one still throws `ValidationException`. Covered
  by new `MatchCompetitorMapperTest` cases for null, empty and whitespace-only values
- **`MatchCompetitorMapper.populateResolvableFields`:** A blank competitor category is now kept as
  `CompetitorCategory.NONE` instead of being cleared to `null`, so a bulk import row no longer reports it as missing; an
  unrecognised one is still left `null`
- **`MatchCompetitorMapper.resolveFirearmType`:** Now takes the competitor's `Division` and falls back to the
  division's firearm type when the firearm type name is unknown, instead of throwing
- **`MatchCompetitorMapper.resolveMatchClub`:** Now matches a club by name, abbreviation or identifier code, in that
  order
- **`MatchCompetitorRowMapper`:** New mapper that describes a failed bulk import row as a `MatchCompetitorRow`,
  reporting the values already resolved onto the `MatchCompetitor` — the competitor and match identifiers, the
  competitor's number, and the match club, category, firearm type, division and power factor — in place of the row's
  own text; `failedRow` now takes the `MatchCompetitor` and uses it, so a row that fails as a duplicate or for a
  missing field is reported by its resolved values. A reported match club is now the club's name, and the competitor's
  name is always the row's own name cleaned by `CompetitorHelpers.cleanCompetitorName`, whether or not a competitor
  was resolved

##### Services

- **`IpscMatchCompetitorServiceImpl.createMatchCompetitors`:** A row is now checked for every missing or
  unresolvable required field at once, through `MatchCompetitorMapper.populateResolvableFields`, instead of failing on
  the first one thrown, so its message lists them all; a blank category, division, firearm type or power factor is
  reported as not specified
- **`ClubService` and `EntityIpscCompetitorService`:** Renamed to `IpscEntityClubService` and
  `IpscEntityCompetitorService` (and their implementations and tests) so the IPSC entity services share a naming
  pattern; `findCompetitor` is now `findCompetitorByIdentifierAndFullName`, and dependants rename their fields to match
- **`IpscMatchCompetitorServiceImpl.toResponse`:** Now validates the match competitor first and throws
  `ValidationException` naming every violated constraint, instead of building a response from an incomplete entity;
  the service now takes a `jakarta.validation.Validator`
- **`IpscMatchCompetitorServiceImpl.failedRow`:** **Breaking:** a row that could not be imported is now reported with
  no `MatchCompetitorResponse` (`null`) and only its values in `matchCompetitorRow`, instead of a partly filled
  response, so a client reading `matchCompetitor` for a failed row now gets `null`; the `toFailedResponse` helper is
  removed. `MatchCompetitorBulkResponse.matchCompetitor` is now `@Nullable`

##### Models

- **`MatchCompetitor`:** The competitor, match, competitor category, firearm type, division and power factor are now
  `@NotNull`, matching their `nullable = false` columns
- **`MatchCompetitorRequest`:** The match ID, competitor category, division and power factor are no
  longer rejected by bean validation, and the competitor category is optional in the CSV import (`Cats`), so a bulk
  import can report a row that is missing them instead of failing the whole request; `validate()` still requires
  them when a match competitor is created or replaced
- **`MatchCompetitorResponse.competitorId`:** Dropped `@NonNull`, so a response can be built for a match competitor
  that is not linked to a competitor
- **`MatchCompetitorBulkResponseHolder.matchCompetitors`:** **Breaking:** renamed back to `matchCompetitorResults`, so
  a bulk match competitor import's response body carries `matchCompetitorResults` instead of `matchCompetitors`;
  `IpscMatchCompetitorController` reads the renamed getter
- **`CompetitorResponse`, `CompetitorResponseHolder`, `MatchResponse`, `MatchResponseHolder` and
  `MatchCompetitorBulkResponseHolder`:** Replaced `org.jspecify.annotations.NonNull` with
  `jakarta.validation.constraints.NotNull` on their required fields, so they can be checked by a `Validator`

##### Documentation

- **`documentation/roadmap/improvement-plan.md`, `improvement-plan-tasks.md`:** Gaps #38 (test-only utility and enum
  methods), #39 (`ClubIdentifier.code` and `fromCode`) and #40 (no data migration for the renamed `Division` names) are
  recorded and closed in v14.0.0, with the Roadmap, At a Glance and Success Criteria entries updated to match
- **`documentation/recommendations/flyway-migration-versioning.md`:** Current State table gains a row for
  `V11_7_0__drop_division_suffix_from_division_names.sql`
- **`ARCHITECTURE.md`:** Describes the renamed `IpscEntityClubService` and `IpscEntityCompetitorService`, the new
  `MatchCompetitorHelpers` and `MatchCompetitorRowMapper`, and a bulk import row that fails for missing or
  unresolvable fields; drops `DateUtil` and `NumberUtil` and `MatchCompetitorResponseHolder`
- **`AGENTS.md`:** The final-utility-class example now names `ValueUtil` instead of the removed `NumberUtil`, and the
  test-heading example uses `// fromName()` instead of `// fromCode()`

#### 🗑️ Removed

##### Utilities

- **`NumberUtil` and `DateUtil`:** Removed both classes and their tests — `calculatePercentage`, `calculateSum`,
  `formatBigDecimal`, `formatDate` and `formatDateTime` were called only by tests
- **`StringUtil.formatStringWithNamedParameters` and `ValueUtil.nullAsZeroBigDecimal`:** Removed with their tests, as
  nothing in production code called them

##### Constants

- **`SystemConstants.DEFAULT_SCALE`:** Removed, as nothing read it once `NumberUtil` took the scale as a parameter

##### Models

- **`MatchCompetitorResponseHolder`:** Removed the unused container class; bulk imports return
  `MatchCompetitorBulkResponseHolder`

##### Tests

- **`AwardControllerTest`, `ImageControllerTest`, `IpscCompetitorControllerTest`, `IpscMatchControllerTest` and
  `IpscMatchCompetitorControllerTest`:** Removed the tests that only checked the controller delegates to its service
  or lets the service's exception propagate

---

### 🗄️ Archived Versions (10.0.0 – 13.3.0)

The change log entries for versions 10.0.0 to 13.3.0 are archived, unchanged, in
[`documentation/archive/v10-v13/CHANGELOG_v10-v13.md`](/documentation/archive/v10-v13/CHANGELOG_v10-v13.md).

---

### 🗄️ Archived Versions (8.0.0 – 9.1.0)

The change log entries for versions 8.0.0 to 9.1.0 are archived, unchanged, in
[`documentation/archive/v8-v9/CHANGELOG_v8-v9.md`](/documentation/archive/v8-v9/CHANGELOG_v8-v9.md).

---

### 🗄️ Archived Versions (5.0.0 – 7.4.1)

The change log entries for versions 5.0.0 to 7.4.1 are archived, unchanged, in
[`documentation/archive/v5-v7/CHANGELOG_v5-v7.md`](/documentation/archive/v5-v7/CHANGELOG_v5-v7.md).

---

### 🗄️ Archived Versions (1.0.0 – 4.1.0)

The change log entries for versions 1.0.0 to 4.1.0 are archived, unchanged, in
[`documentation/archive/v1-v4/CHANGELOG_v1-v4.md`](/documentation/archive/v1-v4/CHANGELOG_v1-v4.md).

---

### 📋 Version Policy

#### Semantic Versioning (Current)

As of version 5.0.0, this project follows [Semantic Versioning 2.0.0](https://semver.org/):

- **MAJOR** version for incompatible API changes
- **MINOR** version for backward-compatible functionality additions
- **PATCH** version for backward-compatible bug fixes

Every release must follow these rules strictly — see [`AGENTS.md`'s Semantic Versioning
section](/AGENTS.md#semantic-versioning) for what counts as a MAJOR, MINOR or PATCH change in this project and how
each release is classified from the `[Unreleased]` section.

#### Legacy Versioning (v1.x – v4.x)

Earlier releases used a non-semantic versioning scheme. For historical documentation,
see [ARCHIVE.md](/documentation/legacy/ARCHIVE.md).

---

### 🚀 Upgrade Guide

#### From v5.3.0 to v5.4.0

**Breaking Changes:** None

1. Update the version in `pom.xml` to `5.4.0`
2. Replace any `IpscMatchService` injection points with `TransformationService`
3. Update import statements for classes moved from `ipsc/domain` to `ipsc/data`
4. Update `MatchCompetitorEntityService` call sites to handle `List<>` return types
5. Run `./mvnw clean install` to rebuild the project

#### From v4.1.0 to v5.0.0

**Breaking Changes:** None

1. Update the version in `pom.xml` to `5.0.0`
2. Run `./mvnw clean install` to rebuild the project
3. Restart the application
4. Existing data and configurations remain compatible

#### From v4.0.0 to v4.1.0

**Breaking Changes:** None

Migration: See v4.1.0 release notes

#### From v3.x to v4.x

**Breaking Changes:** Yes

- Update entity references from `Match` to `IpscMatch`
- Update service injections to use `IpscMatchRepository`
- See v4.0.0 release notes for a detailed migration guide

---

### 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Create a feature branch from `main`
2. Make your changes with comprehensive test coverage
3. Document your changes in the appropriate sections of this CHANGELOG
4. Submit a pull request with a detailed description

---

### 💬 Support

For issues, feature requests or questions:

- **GitHub Issues:** [tahoni/hpsc-web-springboot/issues](https://github.com/tahoni/hpsc-web-springboot/issues)
- **Repository:** [tahoni/hpsc-web-springboot](https://github.com/tahoni/hpsc-web-springboot)

---

**Last Updated:** 2026-08-25
