# HPSC Website Backend

## 🧾 Change Log

All notable changes to the HPSC Website Backend project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) as of version 5.0.0.

---

### Table of Contents

- [🧪 Unreleased](#-unreleased)
- [🧾 Version 13.3.0](#-1330---2026-10-07) ← Current
- [🧾 Version 13.2.0](#-1320---2026-10-07)
- [🧾 Version 13.1.0](#-1310---2026-10-07)
- [🧾 Version 13.0.0](#-1300---2026-10-07)
- [🧾 Version 12.0.0](#-1200---2026-10-05)
- [🧾 Version 11.0.0](#-1100---2026-10-04)
- [🧾 Version 10.0.0](#-1000---2026-10-03)
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
  instead of `NonFatalException` (404). A blank home club is still left unset
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
- **`IpscMatchCompetitorServiceImpl.failedRow`:** A row that could not be imported is now reported with no
  `MatchCompetitorResponse` (`null`) and only its values in `matchCompetitorRow`, instead of a partly filled response; the
  `toFailedResponse` helper is removed. `MatchCompetitorBulkResponse.matchCompetitor` is now `@Nullable`

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

---

### 🧾 [13.3.0] - 2026-10-07

#### ➕ Added

##### Database

- **`V11_6_0__add_competitor_paid_up_ngpsa.sql`:** New migration adding a nullable `competitor.paid_up_ngpsa` boolean
  column, placed after `paid_up_sapsa` — records whether a competitor's NGPSA membership is paid up

##### Models

- **`Competitor.paidUpNgpsa`:** New `Boolean` field mapped to `paid_up_ngpsa`
- **`CompetitorRequest`, `CompetitorPatchRequest` and `CompetitorResponse`:** New optional `paidUpNgpsa` field (and
  `PaidUpNgpsa` CSV column on `CompetitorRequestCsvMixIn`) — stored as `null` when omitted; the `CompetitorRequest`
  all-arguments constructor gains a `paidUpNgpsa` parameter between `paidUpSapsa` and `paidUpClub`

##### Mappers

- **`CompetitorMapper`:** `applyFields` and `applyPatchFields` now copy `paidUpNgpsa`; the patch leaves it unchanged
  when omitted

##### Services

- **`IpscCompetitorServiceImpl`:** CSV rows now carry `paidUpNgpsa` through to the `CompetitorRequest`

##### Controllers

- **`IpscCompetitorController`:** The competitor CSV import example now includes the `PaidUpNgpsa` column

##### Tests

- **`CompetitorMapperTest`, `CompetitorPatchRequestTest`, `CompetitorRequestCsvMixInTest`, `CompetitorRequestTest` and
  `IpscCompetitorServiceImplTest`:** New and extended tests covering `paidUpNgpsa` across the mapper, JSON, CSV and
  entity-to-response paths

#### 🔄 Changed

##### Mappers

- **`CompetitorMapper.resolveClubNumber`:** now normalises a supplied club number by removing all spaces and trimming
  surrounding whitespace before returning it, so `applyFields` and `applyPatchFields` store a consistent value; the
  Javadoc describes the normalisation

---

### 🧾 [13.2.0] - 2026-10-07

#### ➕ Added

##### Services

- **`EntityIpscCompetitorServiceImpl.findCompetitor`:** New first stage matches the trimmed competitor number as a club
  number with `CompetitorRepository.findByClubNumber`, before converting it to a number, and returns that competitor at
  once; a numeric club number therefore takes precedence over a competitor number with the same value. The competitor
  number, ID number and name lookups follow when no club number matches

#### 🔄 Changed

##### Helpers

- **`CompetitorHelpers.cleanCompetitorName`:** The position and whitespace patterns no longer backtrack:
  `POSITION_PREFIX` is possessive and `WHITESPACE` matches only a run of two or more whitespace characters. A leading
  position is now any number of digits followed by an optional whitespace character and a hyphen, at the very start of
  the name, so `123-John` now cleans to `John` and `-12-John Smith` is left alone; a single tab or newline between
  words is no longer turned into a space
- **`CompetitorHelpers.MC_PREFIX`:** Tests only the start of the word with `lookingAt()`, so the pattern is a fixed
  four-step match with no trailing `.*`; no change in behaviour

##### Services

- **`AwardServiceImpl.mapAwards`:** Dropped `@NonNull` from the `awardRequestList` parameter, as the method checks for
  null itself and throws a `ValidationException`; no change in behaviour

##### Documentation

- **Archived versions 1.0.0 – 9.1.0:** Moved the `CHANGELOG`, `HISTORY` and `EVOLUTION_OVERVIEW` entries of those
  versions into `CHANGELOG_v1-v4.md`, `CHANGELOG_v5-v7.md` and `CHANGELOG_v8-v9.md` and their `HISTORY_*` and
  `EVOLUTION_OVERVIEW_*` counterparts, and the v1 – v9 release notes and pull request descriptions, from
  `documentation/history/` to `documentation/archive/v1-v4/`, `v5-v7/` and `v8-v9/`, and
  `documentation/archive/ARCHIVE.md` to `documentation/legacy/ARCHIVE.md`; the links in `README.md`, `AGENTS.md`,
  `CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md` and the `prep-version-release` skill follow the new locations

#### 🐛 Fixed

##### Services

- **`AwardServiceImpl.mapAwards`:** The error logged and the `ValidationException` thrown for a null list said "Image
  request list" (copied from the image service); they now say "Award request list", and the test asserts the message

##### Models

- **`ImageResponse.setMimeType`:** A blank MIME type that could not be inferred from the file name is now reset to
  an empty string, as a null one already was, because the check tests `hasText` on the field rather than only null

---

### 🧾 [13.1.0] - 2026-10-07

#### ➕ Added

##### Configuration

- **`ControllerAdvice`:** New `handleMethodArgumentNotValidException` maps a request body that fails Bean Validation to
  a `400 Bad Request` listing every violated constraint, instead of the generic `500`

##### Models

- **`CompetitorRequest`, `MatchRequest`, `MatchCompetitorRequest`:** Added `@NotBlank` and `@NotNull` constraints for
  the fields `validate()` already requires, with the same messages, so a `@Valid` request body is checked on arrival

#### 🔄 Changed

##### Build & Configuration

- **`pom.xml`:** Declared `org.jspecify:jspecify` as a direct dependency (version managed by Spring Boot), as the
  code now uses its `@NonNull` annotation rather than receiving it transitively

##### Controllers

- **`IpscCompetitorController`, `IpscMatchController`, `IpscMatchCompetitorController`:** Annotated the
  `@RequestBody` request parameters with `@Valid`, so a request missing a required field is now rejected with a `400`
  by Bean Validation before it reaches the service

##### Helpers

- **`CompetitorHelpers`:** New `cleanCompetitorName` removes a leading position of up to two digits followed by a
  `-` (e.g. `1 - John Smith`), any `RO` or `(RO)` marker and all full stops, replaces runs of whitespace with a single
  space, trims the result and returns an empty string for a null name

##### Services

- **`EntityIpscCompetitorServiceImpl.findCompetitor`:** Normalises the full name with
  `CompetitorHelpers.cleanCompetitorName`, so a leading position and full stops are now also ignored when matching
  by name
- **Services and mappers:** Replaced `jakarta.validation.constraints.NotNull` with
  `org.jspecify.annotations.NonNull` on method parameters; the explicit null checks are unchanged

##### Mappers

- **`MatchCompetitorMapper`:** `applyFields` and `applyPatchFields` now clean the request's competitor name with
  `CompetitorHelpers.cleanCompetitorName` before looking the competitor up

##### Models

- **`MatchCompetitorRequest`:** `validate()` now returns `void` and throws on failure, as the other request models do;
  it previously also returned `true`, which no caller used
- **Response models:** Replaced `jakarta.validation.constraints.NotNull` with `org.jspecify.annotations.NonNull`; no
  behaviour change, as Bean Validation was not applied to responses

##### Documentation

- **`flyway-migration-versioning.md`:** Re-aligned the Current State table's columns
- **`AGENTS.md`:** Added a missing comma in the Release Checklist's Flyway cross-check step

---

### 🧾 [13.0.0] - 2026-10-07

#### ➕ Added

##### Documentation

- **`documentation/roadmap/improvement-plan.md`, `improvement-plan-tasks.md`:** New open Gap #38 — eight utility and
  enum methods and `SystemConstants.DEFAULT_SCALE` are used only by tests, with a matching task block, Roadmap row and
  Success Criteria entry
- **`documentation/roadmap/improvement-plan.md`, `improvement-plan-tasks.md`:** New open Gap #39 —
  `ClubIdentifier.code` and `fromCode` are used only by tests, with a matching task block, Roadmap row and Success
  Criteria entry

##### Mappers

- **`CompetitorMapper`:** New component holding `applyFields`, the new `applyPatchFields` and the gender, home club,
  club number and competitor number lookups that `IpscCompetitorServiceImpl` carried, so a request model no longer
  needs a repository to be copied onto an entity
- **`MatchMapper`:** New component holding `applyFields`, the new `applyPatchFields` and the club, firearm type and
  match category lookups that `IpscMatchServiceImpl` carried
- **`MatchCompetitorMapper`:** New component holding `applyFields`, the new `applyPatchFields` and the competitor,
  match, match club, competitor category, firearm type, division and power factor lookups that
  `IpscMatchCompetitorServiceImpl` carried

##### Tests

- **`CompetitorMapperTest`, `MatchMapperTest`, `MatchCompetitorMapperTest`:** The unit tests for the moved helpers,
  plus new `applyPatchFields` tests covering unchanged fields, partial patches, club number re-resolution and the
  lookup failures

#### 🔄 Changed

##### Build & Configuration

- **`.gitattributes`:** Added `* text=auto eol=lf` so text files are checked out with LF line endings regardless of
  `core.autocrlf`; the `mvnw` and `*.cmd` rules still apply
- **`qodana.yaml`:** Switched from the `qodana.starter` to the `qodana.recommended` profile — adds the `LicenseAudit`
  inspection with `licenseRules` that allow permissive licences (Apache-2.0, MIT, BSD, EPL-2.0, ISC) and prohibit GPL
  and AGPL dependencies, plus further Java best-practice inspections alongside `JavadocReference`

##### Constants

- **`SystemConstants`:** Now `final`, as it is a utility class with a private constructor

##### Controllers

- **`IpscMatchCompetitorController.createMatchCompetitors`:** Now uses `HttpStatus.UNPROCESSABLE_CONTENT` instead of the
  deprecated `UNPROCESSABLE_ENTITY` when every row fails — the response is still `422`, so the API is unchanged

##### Converters

- **`ClubIdentifierConverter`, `FirearmTypeConverter`:** Conversion methods that can return `null` are now annotated
  `@Nullable` (JSpecify), so the null contract is explicit

##### Database

- **`V11_5_0__rename_lady_senior_competitor_category.sql`:** Renames the stored competitor category `Lady, Senior` to
  `Lady Senior` in `match_competitor`, `shooter_log_competitor` and `shooter_log_overall`, matching the renamed
  `CompetitorCategory.SENIOR_LADY`; without it those rows no longer match a category
- **`V11_4_0__make_non_handgun_division_names_unique.sql`:** Renames the stored divisions in `match_competitor` and
  `shooter_log_competitor` to match the renamed `Division` names — the shotgun, .22 and mini rifle divisions that shared
  a handgun name are renamed by the row's firearm type, `Semi Auto Open Division` and `Semi Auto Standard Division`
  become `Rifle Open Division` and `Rifle Standard Division`, and `Standard Manual Division` becomes
  `Shotgun Standard Manual Division`; `shooter_log_overall` has no firearm type and keeps its names, which read back as
  the handgun divisions
- **`V11_2_0__make_match_competitor_firearm_type_and_power_factor_required.sql`:** `match_competitor.firearm_type` and
  `power_factor` become `NOT NULL`, reversing `V8_1_0`'s `firearm_type` change and restoring one entry per competitor,
  match and firearm type; the migration is refused, leaving the columns as they were, if any existing row has a `NULL`
  in either — backfill those rows first
- **`V11_3_0__add_match_competitor_date_calculated.sql`:** Adds the nullable `match_competitor.date_calculated`
  column after `is_visitor`, so it sits before `date_created`; existing rows stay `NULL`

##### Documentation

- **`ARCHITECTURE.md`:** Project Structure trees and a new Mappers section describe the `mappers` package
- **`ARCHITECTURE.md`, `README.md`:** The `models/ipsc/shared/` comment no longer calls the shared score fields
  groundwork, and the Match Scoring Domain Model bullet says the match competitor endpoints are built on them while
  the shooter-log layer is still to come
- **`documentation/recommendations/flyway-migration-versioning.md`:** Current State table gains rows for `V11_4_0` and
  `V11_5_0`, and `V11_2_0` and `V11_3_0` are attributed to this release
- **`documentation/roadmap/improvement-plan.md`, `improvement-plan-tasks.md`:** Gap #38 is partially completed —
  `Division.fromAbbreviationOrName` and the other test-only enum methods are removed — with the Roadmap row, At a
  Glance and Success Criteria updated to match

##### Domain

- **`MatchCompetitor`:** `firearmType` and `powerFactor` are now `nullable = false`
- **`MatchCompetitor.dateCalculated`:** New optional `date_calculated` column, placed before `dateCreated`, recording
  when the row's scores were calculated — as `ShooterLogCompetitor` and `ShooterLogOverall` already do

##### Enums

- **`ClubIdentifier`:** The `code` values are now `"C SOSC"` (SOSC), `"B HPSC"`, `"A PMPSC"` and `"U VISITOR"`
- **`CompetitorCategory`:** **Breaking:** The `SENIOR_LADY` name is now `"Lady Senior"` (was `"Lady, Senior"`), so an
  import row or request using the old name is rejected; the `fromName` Javadoc also says "category" rather than
  "division", and the converter tests use the new name
- **`CompetitorCategory`:** **Breaking:** Replaced the `code` field and `fromCode(Integer)` with a `String`
  `abbreviation` field (`J`, `SJ`, `L`, `LS`, `S`, `SS`, `GS`)
- **`Division`:** Added a `firearmType` field (a `FirearmType`) recording the firearm type each division is shot with
- **`Division`:** **Breaking:** The shotgun, .22 and mini rifle divisions that shared a display name with a handgun
  one are renamed so every name is unique (for example `SHOTGUN_OPEN` is now `"Shotgun Open Division"` and `OPEN_22`
  `".22 Open Division"`), so `Division.fromName(String)` and `DivisionConverter` resolve each division unambiguously
- **`Division`:** **Breaking:** `RIFLE_SEMI_AUTO_OPEN` and `RIFLE_SEMI_AUTO_STANDARD` are renamed `"Rifle Open
  Division"` and `"Rifle Standard Division"`, `SHOTGUN_STANDARD` is renamed `"Shotgun Semi Division"`,
  `SHOTGUN_STANDARD_MANUAL` is renamed `"Shotgun Standard Manual Division"`, and `RIFLE_MANUAL_ACTION_CONTEMPORARY` and
  `RIFLE_MANUAL_ACTION_BOLT` are replaced by `RIFLE_STANDARD_MANUAL` (`"Rifle Standard Manual Division"`)
- **`Division`:** **Breaking:** Removed the `abbreviation` and `code` fields and `fromAbbreviation(String)`,
  `fromAbbreviationOrName(String)` and `fromCode(Integer)`; `fromName(String)` is unchanged
- **`FirearmType`:** **Breaking:** Removed the `code` field and `fromCode(Integer)`
- **`PowerFactor`:** **Breaking:** Removed the `abbreviation` field and `fromAbbreviation(String)`

##### Helpers

- **`CompetitorHelpers.toSentenceCaseLastName`:** A null last name now returns an empty string instead of `null`;
  Javadoc and unit test updated

##### Mappers

- **`MatchCompetitorMapper`:** `applyFields` and `applyPatchFields` now reject a division that does not belong to the
  firearm type, through the new `validateDivisionMatchesFirearmType`; a patch that changes either one is checked against
  the other's current value
- **`MatchCompetitorMapper`:** `applyFields` and `applyPatchFields` now take a missing or unrecognised firearm type from the
  division (`applyFields`, or `applyPatchFields` when the entity has none and the request patches the division)

##### Models

- **`MatchCompetitorRequest`:** **Breaking:** `firearmType` is no longer required: `validate()` accepts a null or blank
  value and `MatchCompetitorMapper` takes the firearm type from the division instead
- **`MatchCompetitorRequest`:** `validate()` now also rejects a division that does not belong to the firearm type, when
  both are known values, so a create or replace is refused before anything is looked up
- **`IpscCommonScore`, `IpscMatchScore`, `IpscMatchStageScore`:** `weightedPoints` is renamed `points`, and
  `powerFactor` is removed from the score, which the competitor's entry holds instead; the fields are now `protected`
  so subclasses can use them, and the constructors lose their `powerFactor` parameter
- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`:** Extend `IpscMatchScore` instead of declaring their own
  copies of the score fields (`points`, `percentage`, `time`, the hit counts and penalties); the JSON is unchanged
- **`CompetitorResponse`, `MatchResponse`:** Each gains a constructor that builds the response from its entity, and
  its Javadoc now states which fields may be null
- **`MatchCompetitorResponse`:** **Breaking:** The `competitorName` string is replaced by a `competitorNames` list
  holding the competitor's "First Last" and "Nick Last" names once each (a null or duplicate nickname is skipped), so
  the JSON of `/ipsc/match-competitors` responses and bulk import results changes shape; also gains a constructor that
  builds it from a `MatchCompetitor`, and its Javadoc now covers the partly set response for a failed bulk import row
- **`CompetitorRequest.validate`, `MatchRequest.validate`:** Now documented in Javadoc, and unit tests added for them
  and for `MatchCompetitorRequest.validate`
- **`MatchCompetitorRequest.validate`:** New method, with Javadoc, holding the required-field checks that
  `IpscMatchCompetitorServiceImpl.validateForCreate` repeated inline; `validateForCreate` now calls it
- **`MatchCompetitorRequest`, `MatchCompetitorResponse`:** **Breaking:** `powerFactor` is now required, as
  `firearmType` already was — `validate()` rejects a request without one ("Power factor is required."), so bulk import
  rows and API calls that omit it now fail; `firearmType` and `powerFactor` are `@NotNull` on the response

##### Services

- **`IpscCompetitorServiceImpl`, `IpscMatchServiceImpl`:** Map entities with the new response constructors instead of
  long all-args calls
- **`IpscCompetitorServiceImpl`, `IpscMatchServiceImpl`:** `validateForCreate` calls `CompetitorRequest.validate` and
  `MatchRequest.validate` instead of repeating the required-field checks inline; the rules and messages are unchanged
- **`IpscMatchCompetitorServiceImpl`:** `patchMatchCompetitor` ignores a blank `powerFactor` instead of clearing the
  power factor
- **`IpscMatchCompetitorServiceImpl`:** `toResponse` uses the new constructor, and `toFailedResponse` wraps the
  requested name in a list, or leaves it empty when the request has none
- **`IpscCompetitorServiceImpl`:** `resolveClubNumber`, `resolveGender`, `resolveHomeClub` and `parseCompetitorNumber`
  are now annotated `@Nullable`, so the null contract is explicit
- **`IpscMatchCompetitorServiceImpl`:** `resolveCompetitorHomeClub`, `resolveMatchClub` and `resolvePowerFactor` are now
  annotated `@Nullable`, so the null contract is explicit
- **`IpscCompetitorServiceImpl`:** Delegates field copying and lookups to `CompetitorMapper`; its constructor takes the
  mapper in place of `ClubRepository` and `ClubService`, and `patchCompetitor` calls `applyPatchFields`
- **`IpscMatchServiceImpl`:** Delegates field copying and lookups to `MatchMapper`; its constructor takes the mapper in
  place of `ClubRepository`, and `patchMatch` calls `applyPatchFields`
- **`IpscMatchCompetitorServiceImpl`:** Delegates field copying and lookups to `MatchCompetitorMapper`; its
  constructor takes the mapper in place of `CompetitorRepository`, `IpscMatchRepository` and
  `EntityIpscCompetitorService`, and `patchMatchCompetitor` calls `applyPatchFields`
- **`IpscMatchCompetitorServiceImpl`:** Removed the unused `parseCompetitorNumber`; `toFailedResponse` still uses the
  lenient `CompetitorHelpers.getCompetitorNumberAsInteger`

##### Utils

- **`NumberUtil`:** `calculatePercentage` and `calculateSum` now take a `scale` parameter instead of always using
  `SystemConstants.DEFAULT_SCALE`, so callers choose the result's decimal places; Javadoc updated and unit tests added
  for non-default scales
- **`StringUtil`:** `toString` and `toProperCase` are now annotated `@Nullable`, as both return `null` for a null input

#### 🐛 Fixed

##### Utils

- **`NumberUtil.calculatePercentage`:** The intermediate division now uses `scale + 2` digits instead of `scale * 2` —
  a scale below 2 rounded the ratio too early (2/3 at scale 0 gave `100` instead of `67`); results at the default scale
  are unchanged

---

### 🧾 [12.0.0] - 2026-10-05

#### ➕ Added

##### Build & Configuration

- **`qodana.yaml`:** New Qodana configuration for the `code_quality.yml` workflow — the `qodana.starter` profile plus
  the `JavadocReference` inspection, so broken `{@link}` and `@see` references are reported

##### Helpers

- **`CompetitorHelpers.getCompetitorNumberAsInteger`:** New helper that converts a competitor number to an `int` —
  surrounding whitespace is ignored, and a number that is null, blank or not a whole number, or one of the shared
  alias numbers `15000` and `16000`, converts to `0` meaning "no competitor number"; includes Javadoc and unit tests

##### Repositories

- **`CompetitorRepository.findAllByIdNumber`:** New derived query returning every competitor with a given ID number,
  used by `findCompetitor` to match a supplied value against ID numbers

##### Services

- **`IpscMatchCompetitorService.createMatchCompetitors(String, String)`:** **Breaking:** the bulk import is now limited
  to one club — HPSC's own club unless an optional `club` (name or abbreviation) asks for another — and imports only
  the rows whose `matchClub` is that club, or whose competitor's home club is. Any other row, including one whose
  competitor can't be resolved, is reported as skipped rather than created, so there is still one result per CSV row,
  and an unknown club answers a `ValidationException`. A row that used to be created for another club, or for a
  competitor with no home club, is now skipped unless the matching `club` is given. The one-argument method imports
  HPSC's rows, and `IpscMatchCompetitorServiceImpl.isForClub` does the per-row check through `ClubService`; includes
  Javadoc and unit and integration tests
- **`ClubService`:** New service, with `ClubServiceImpl`, holding the null-safe club comparisons — `isSameClub(Club,
  ClubIdentifier)` returns `true` only when a club's identifier is the given `ClubIdentifier`, and
  `isSameClub(ClubIdentifier, ClubIdentifier)` only when a non-null identifier is the target;
  `IpscCompetitorServiceImpl` takes it as a constructor dependency and its `isMemberOfHomeClub` now takes just the club
  and compares it with the home club through it; includes Javadoc and unit and integration tests

##### Utils

- **`StringUtil.hasText`:** New helper that returns `true` only when a string is neither null nor blank, replacing the
  private copy in `IpscMatchCompetitorServiceImpl` that checks the competitor number and name on a request; includes
  Javadoc and unit tests

#### 🔄 Changed

##### Build & Configuration

- **`pom.xml`:** `mysql-connector-j` is now pinned to `26.7.0` instead of the version Spring Boot manages — re-check
  it against the parent's managed version at each release, per the Release Checklist's override step

##### Controllers

- **`IpscMatchCompetitorController`:** The bulk import endpoint (`POST /bulk`) accepts an optional `club` query
  parameter that picks the club to import rows for, HPSC's own club when omitted, as
  `createMatchCompetitors(String, String)` does

##### Services

- **`IpscCompetitorServiceImpl.isMemberOfHomeClub`:** New overload, `isMemberOfHomeClub(Club, ClubIdentifier)`, that
  checks a club against the home club passed in; `isMemberOfHomeClub(Club)` now delegates to it with the default home
  club, `IpscConstants.HOME_CLUB_IDENTIFIER` (HPSC), so existing callers behave as before; includes Javadoc and unit
  tests
- **`EntityIpscCompetitorServiceImpl.findCompetitor`:** The range officer marker, `RO` or `(RO)`, is now removed from
  a name wherever it appears — at the start, in the middle or at the end — instead of only at the end, and the
  whitespace left behind is collapsed, so `RO Jane Doe` and `Jane (RO) Doe` match `Jane Doe`; `RO` must be a whole word,
  so a name such as `Romeo` or `PEDRO` is left alone. `IpscConstants.REPLACE_IN_NAMES_REGEX` and the Javadoc follow,
  with unit, service and integration tests
- **`IpscMatchServiceImpl.resolveMatchCategory`:** A match category that is null, empty or blank now resolves to
  `IpscConstants.DEFAULT_MATCH_CATEGORY` (`Club Shoot`) instead of answering a `ValidationException`, as the club
  already defaults when omitted; a category that is supplied must still be known, by display name or constant name
  (`Club Shoot` or `CLUB_SHOOT`), ignoring case and surrounding whitespace. Documented, with unit tests
- **`IpscMatchServiceImpl.validateForCreate`:** The match category is no longer required to create or replace a
  match — a request without one answers no `ValidationException` ("Match category is required.") but takes the
  default match category, as `resolveMatchCategory` already supplies one and the club defaults when omitted; the
  `IpscMatchService` Javadoc and the unit, service and integration tests expect the default
- **`EntityIpscCompetitorService.findCompetitor`:** The competitor number is now a `String` that may also be an ID
  number — the lookup tries the competitor number, then the ID number, then the full name, narrowing several number
  matches by name. `null` is accepted for either argument when the other is given, a `ValidationException` is thrown
  when both are null or blank, and an exception is thrown, rather than an empty `Optional` returned, when no single
  competitor matches — a `NonFatalException` when none do, a `ValidationException` when several do, including
  several that share the number but not the name
- **`EntityIpscCompetitorService.findCompetitor`:** Parameters reordered to `(competitorNumber, fullName)`.
  `IpscMatchCompetitorServiceImpl` now takes an `EntityIpscCompetitorService`, and its `resolveCompetitor` resolves a
  competitor by ID, otherwise through the new `findCompetitorOrThrow(String, String)`, which trims the competitor
  number and matches it, then the ID number, then the full name — an unmatched number and name still throws a
  `NonFatalException`, while an ambiguous one, including several that share the number but not the name, keeps
  throwing a `ValidationException`
- **`IpscMatchCompetitorServiceImpl.resolveCompetitor`:** The match competitor create and patch endpoints now match a
  competitor given by number and name through `findCompetitor`, so a name also matches a nickname, a number also
  matches an ID number, several competitors sharing a number are narrowed by name, and a name is tried when the number
  matches nobody
- **`IpscMatchCompetitorServiceImpl.resolveCompetitor`:** **Breaking:** the shared alias numbers `15000` and `16000`
  are no longer matched by competitor number, so a request that identified a competitor by one of them now resolves
  only if the name matches, and a number that is not a whole number is looked up as an ID number, answering `404`
  when nothing matches, instead of being rejected up front with a `400`
- **`IpscCompetitorServiceImpl`:** A competitor created or updated without a nickname, or with an empty or blank
  one, now takes its first name as the nickname, instead of being saved with none
- **`IpscMatchCompetitorServiceImpl.createMatchCompetitors`:** **Breaking:** the bulk import is now a partial import
  rather than all or nothing — each row is saved on its own, and a row that is missing a required field, has an
  unrecognised enumerated value, names an unknown competitor or match, or duplicates another entry is skipped and
  reported in its `MatchCompetitorBulkResponse` (`success` of `false` and a `message`) while the other rows are still
  created, instead of the whole import answering `400` or `404`. Only unreadable CSV, or a header missing a required
  column, still answers `400`, and an import in which every row fails answers `422` with the same per-row results.
  Its interface and controller Javadoc and Swagger descriptions are updated to match

##### Models

- **`MatchCompetitorResult`, `MatchCompetitorResultHolder`:** Renamed to `MatchCompetitorBulkResponse` and
  `MatchCompetitorBulkResponseHolder`, and the holder's `matchCompetitorResults` field to `matchCompetitors`, so the
  bulk import's response models follow the `*Response`/`*ResponseHolder` naming — a bulk match competitor import's
  response body now carries `matchCompetitors` instead of `matchCompetitorResults`
- **`MatchCompetitorResponse`:** New `competitorName` and `competitorNumber` fields, so a bulk import result
  identifies the competitor it relates to even when the row failed; a persisted match competitor's name is its nickname
  and last name
- **`MatchCompetitorBulkResponse`:** `success` now defaults to `true` and `message` to an empty string

##### Utils

- **`StringUtil.hasText`:** The enums, `ControllerResponse`, `ImageResponse` and the service implementations now use it,
  imported statically, in place of their own null and blank checks — no behaviour change except that
  `ImageResponse.setMimeType` now also ignores a blank MIME type guessed from the file name

- **`StringUtil.toProperCase`:** `IpscCompetitorServiceImpl` now imports it statically, like `hasText` — no behaviour
  change

- **`DateUtil`, `NumberUtil`, `StringUtil`, `ValueUtil`:** Renamed back from `DateUtils`, `NumberUtils`, `StringUtils`
  and `ValueUtils`, the plural names introduced in 8.12.0 (and their test classes to match) — internal classes
  only, so there is no API change

##### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** Gap #36 (`EntityIpscCompetitorService` and the match
  competitor result models built but not wired in) closed in v12.0.0 — both files, the roadmap and the Success
  Criteria updated, and the Open section now empty
- **`ARCHITECTURE.md`, `AGENTS.md`:** Name the renamed `*Util` classes and `MatchCompetitorBulkResponse` models, and
  list `ClubService` in the services table
- **`IpscConstants`:** Every constant now has Javadoc saying what it is for and where it is used — including the
  alias competitor numbers, the range officer name marker and the home club's role in the bulk import — and the class
  Javadoc describes what it holds
- **`ClubService`, `IpscMatchCompetitorService`, `IpscMatchCompetitorServiceImpl`:** `ClubService`'s methods,
  `createMatchCompetitors`' club filter (check order, skipped versus failed rows, the HPSC default) and `isForClub`,
  `resolveCompetitorHomeClub` are documented
- **`EntityIpscCompetitorService`, `IpscMatchCompetitorServiceImpl`:** `findCompetitor`'s interface Javadoc now
  describes the lookup order, the accepted null and blank arguments and the exceptions thrown, and
  `findCompetitorOrThrow(String, String)` and `resolveCompetitor` gain matching Javadoc
- **`DateUtil`, `NumberUtil`, `StringUtil`, `ValueUtil`:** Methods gain `@since` tags, and the class-level Javadoc
  and usage examples name the renamed classes
- **`ControllerAdvice`, `FatalException`, `NonFatalException`, `ValidationException`, `Request`, `Response`,
  `TransactionService`:** Methods and constructors that lacked one gain a `@since` tag
- **`EntityIpscCompetitorServiceImpl`:** `findCompetitor` gains Javadoc that inherits the interface documentation
  (`{@inheritDoc}`) and adds implementation notes on the lookup stages — competitor number, ID number, then full
  name — the name normalisation, and the exception thrown when no single competitor is found

##### Tests

- **`IpscCompetitorServiceImplTest`:** New `applyFields` tests for the nickname defaulting to the first name when
  it is null, empty or blank, replacing an existing nickname, and a supplied nickname being kept
- **`IpscMatchCompetitorServiceTest`, `IpscMatchCompetitorServiceImplTest`, `IpscMatchCompetitorServiceIntegrationTest`,
  `IpscMatchCompetitorControllerTest`:** Updated for the partial bulk import and `MatchCompetitorBulkResponseHolder` —
  a failed row is asserted as an unsuccessful `MatchCompetitorBulkResponse` rather than a thrown exception, with new
  cases for a failing row not stopping the others, for a row duplicating an earlier row in the same import, for the
  `422` when every row fails and for `toFailedResponse`
- **`CompetitorHelpersTest`:** Cover `getCompetitorNumberAsInteger` for numeric, whitespace-padded, zero-padded, null,
  blank and non-numeric numbers, and for the excluded ICS aliases `15000` and `16000`
- **`EntityIpscCompetitorServiceTest`, `EntityIpscCompetitorServiceImplTest`,
  `EntityIpscCompetitorServiceIntegrationTest`:** Brought up to date with the `String` competitor number and the
  `NonFatalException`/`ValidationException` behaviour of `findCompetitor`, and extended to cover the ID number
  lookup, non-numeric, zero, negative, whitespace-padded, `+`-prefixed, null and blank numbers, null names and the
  blank-input validation
- **`IpscMatchCompetitorServiceImplTest`, `IpscMatchCompetitorServiceTest`,
  `IpscMatchCompetitorServiceIntegrationTest`:** Cover `resolveCompetitor` delegating to `EntityIpscCompetitorService`
  and the `NonFatalException` and `ValidationException` thrown for an unmatched and an ambiguous name

#### 🐛 Fixed

##### Models

- **`ControllerResponse`:** The constructor taking only a timestamp, message and error derived `success` the wrong way
  round, so a response with an error was marked successful and one without was not — `success` is now `false` when the
  error has a value and `true` when it is null or blank; its unit tests are corrected to match

##### Services

- **`EntityIpscCompetitorServiceImpl`:** `findCompetitor` no longer throws a `NumberFormatException` for a numeric
  value too long for an `int`, such as a 13-digit ID number — it skips the competitor number lookup and matches the
  value against ID numbers instead

#### 🗑️ Removed

##### Constants

- **`IpscConstants.MAX_SAPSA_NUMBER`:** Removed, as nothing referenced it — internal constant only, so there is no API
  change
- **`IpscConstants.MATCH_POINTS_SCALE`, `HIT_FACTOR_SCALE`, `TIME_SCALE`, `PERCENTAGE_SCALE`:** Removed, as nothing
  referenced them — internal constants only, so there is no API change; the scoring layer can introduce its own scales
  with the code that applies them

### 🧾 [11.0.0] - 2026-10-04

#### ➕ Added

##### Models

- **`MatchCompetitorResult`, `MatchCompetitorResultHolder`:** New response models for a bulk match competitor import —
  a `MatchCompetitorResult` records whether one row succeeded, a message and the `MatchCompetitorResponse` it relates
  to, and the holder carries the results in import order

##### Services

- **`EntityIpscCompetitorService`, `EntityIpscCompetitorServiceImpl`:** New `findCompetitor(fullName, competitorNumber)`
  lookup, registered as a Spring `@Service`, that resolves a single `Competitor` — by competitor number when exactly
  one matches (numbers in `IpscConstants.EXCLUDE_ICS_ALIAS` are skipped), otherwise by full name (narrowed to the number
  matches when there are any), where the full name is either "FirstName LastName" or "Nickname LastName" — returning an
  empty `Optional` when no single competitor can be determined

#### 🔄 Changed

##### Domain

- **`Competitor`:** **Breaking:** The `nickname` field is renamed `nickName` and its column `nickname` is renamed
  `nick_name`, by `V11_0_0__rename_competitor_nickname_to_nick_name.sql` (type, nullability and existing values are
  preserved); `getNickname`/`setNickname` become `getNickName`/`setNickName`, and the repository query and
  `EntityIpscCompetitorServiceImpl` follow
- **`Competitor`:** **Breaking:** `competitorNumber` changes from a `String` to an `Integer` and its column
  `competitor_number` from `VARCHAR(255)` to `INT`, by `V11_1_0__change_competitor_number_to_int.sql` (still
  optional); the migration is refused, and the column left as it was, if any existing value is not a whole number

##### Constants

- **`IpscConstants.MAX_SAPSA_NUMBER`:** Raised from `99_999` to `999_999`
- **`IpscConstants.EXCLUDE_ICS_ALIAS`:** Now a `List<Integer>` (`15000`, `16000`) instead of a `List<String>`, to
  match the numeric competitor number

##### API

- **`AwardController`, `ImageController`, `IpscCompetitorController`, `IpscMatchController`,
  `IpscMatchCompetitorController`:** **Breaking:** The `POST /bulk` endpoints now consume `text/plain` instead of
  `text/csv`, so a request sent with `Content-Type: text/csv` is refused with a 415 error; their OpenAPI request-body
  content type matches, and the CSV body is unchanged
- **`CompetitorRequest`, `CompetitorPatchRequest`, `CompetitorRequestCsvMixIn`, `MatchCompetitorRequest`,
  `MatchCompetitorPatchRequest`, `MatchCompetitorRequestCsvMixIn`:** **Breaking:** `competitorNumber` (CSV
  `CompetitorNumber`, and `Mem #` for match competitors) stays a string in requests, but must now be a whole number:
  it is read as one, and a value that is not, such as `C-1`, is refused with a validation error; a blank value is
  treated as not supplied
- **`CompetitorResponse`:** **Breaking:** `competitorNumber` is now a whole number instead of a string
- **`CompetitorRequest`, `CompetitorPatchRequest`, `CompetitorResponse`, `CompetitorRequestCsvMixIn`:** **Breaking:**
  The competitor's nickname property is renamed from `nickname` to `nickName` in JSON, and its CSV column from
  `Nickname` to `NickName`; a CSV that still has a `Nickname` column imports without a nickname, because unknown
  columns are ignored

##### Repositories

- **`CompetitorRepository`:** `findByCompetitorNumber` and `findByFullNameIgnoreCase` renamed to
  `findAllByCompetitorNumber` and `findAllByFullNameIgnoreCase` (callers in `IpscMatchCompetitorServiceImpl` and its
  tests updated), and the new `findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase` matches a competitor's
  "FirstName LastName" or "Nickname LastName" full name, ignoring case; `findAllByFullNameIgnoreCase` still matches
  only "FirstName LastName", and `findAllByCompetitorNumber` now takes an `Integer`

##### Services

- **`IpscCompetitorServiceImpl.applyFields`, `IpscCompetitorServiceImpl.resolveCompetitorNumber`:** A competitor's
  `competitorNumber` is no longer copied straight from the request — the new `resolveCompetitorNumber` helper uses the
  request's `competitorNumber` when it is supplied, otherwise falls back to the request's `sapsaNumber`, and resolves
  to `null` when neither is supplied; the new `parseCompetitorNumber` reads the request's text as a whole number (a
  blank value is `null`, anything else that is not a whole number is refused with a `ValidationException`), and a
  patch with a blank `competitorNumber` leaves the stored number unchanged
- **`IpscMatchCompetitorServiceImpl.resolveCompetitor`:** Reads the request's `competitorNumber` as a whole number
  before looking the competitor up, ignoring surrounding whitespace and refusing a value that is not a whole number
  with a `ValidationException`

##### Documentation

- **Class-level `@since` tags:** Added to `IpscMatchCompetitorController`, `CompetitorPatchRequest`,
  `CompetitorRequestCsvMixIn`, `MatchPatchRequest`, `MatchRequestCsvMixIn`, `MatchCompetitorPatchRequest`,
  `MatchCompetitorRequestCsvMixIn`, `MatchCompetitorResponse`, `MatchCompetitorResponseHolder`,
  `IpscMatchCompetitorService` and `TransactionService`, which had none
- **`EntityIpscCompetitorService`:** Javadoc describing the number-first, full-name-second lookup in `findCompetitor`,
  including the "RO" suffix handling and the empty result for no match or an ambiguous one
- **`IpscCompetitorServiceImpl.resolveCompetitorNumber`:** Javadoc describing the competitor-number-first,
  SAPSA-number-fallback resolution and the `null` result when neither is supplied

##### Testing

- **`EntityIpscCompetitorServiceTest`, `EntityIpscCompetitorServiceImplTest`,
  `EntityIpscCompetitorServiceIntegrationTest`:** New three-tier tests for `findCompetitor` — the interface contract
  with a mocked repository (number, full-name and nickname matching, excluded aliases, the "RO" suffix and ambiguous or
  missing matches), the impl's repository calls (normalised arguments and skipped queries) and an end-to-end run against
  the H2 `test` database
- **`IpscCompetitorServiceImplTest`:** New `resolveCompetitorNumber` tests covering both arguments `null`, only the
  competitor number, only the SAPSA number, both supplied (competitor number wins), a blank competitor number with and
  without a SAPSA number and one that is not a whole number, plus `parseCompetitorNumber`

#### 🐛 Fixed

##### Services

- **`EntityIpscCompetitorServiceImpl.findCompetitor`:** The "RO" suffix is now stripped from a full name that has
  leading or trailing whitespace — the name is trimmed before the suffix is removed, so `"Jane Doe (RO) "` no longer
  misses the competitor it names

### 🧾 [10.0.0] - 2026-10-03

#### ➕ Added

##### Domain

- **`MatchCompetitor` overall scores:** New optional columns after `points` — `percentage`, `time`,
  `percentage_of_possible_points`, the `alpha`, `charlie` and `delta` hit counts, `misses`,
  `no_penalty_misses`, `no_shoots`, `procedural_errors` and `additional_penalties`, added by
  `V10_1_0__add_match_competitor_overall_scores.sql` (which also created a `hit_factor` column that
  `V10_3_0__remove_match_competitor_hit_factor.sql` drops again, so none remains)

##### API

- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorRequestCsvMixIn`:** New optional `name`
  (CSV `Name`) and `competitorNumber` (CSV `Mem #`) — find the competitor when `competitorId` is omitted: the number
  is matched exactly against `Competitor.competitorNumber`, otherwise the full name, "First Last", is matched
  case-insensitively; `competitorId` wins over the number, and the number over the name, and a number or name that
  matches no competitor, or more than one, is refused
- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorResponse`:** New optional overall-score
  fields — `percentage`, `time`, `percentageOfPossiblePoints`, `alpha`, `charlie`, `delta`, `misses`,
  `noPenaltyMisses`, `noShoots`, `proceduralErrors` and `additionalPenalties` (CSV columns named as in a PractiScore
  export — `%`, `Time`, `% psbl`, `A`, `C`, `D`, `M`, `NPM`, `NS`, `Proc` and `Apen`), carried
  through `IpscMatchCompetitorServiceImpl` to and from `MatchCompetitor`

##### Repositories

- **`CompetitorRepository.findByCompetitorNumber`, `CompetitorRepository.findByFullNameIgnoreCase`:** New finders —
  the first matches `Competitor.competitorNumber` exactly, the second the competitor's "First Last" full name,
  ignoring case; both return a list so a match on several competitors can be refused

#### 🔄 Changed

##### Documentation

- **`CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md`:** Versions 1.0.0 – 7.4.1 archived, unchanged, to
  `documentation/history/CHANGELOG_v1-v7.md`, `documentation/history/HISTORY_v1-v7.md` (the per-version
  Historical Timeline, Milestone, Architectural Evolution and completed-work entries) and
  `documentation/history/EVOLUTION_OVERVIEW_v1-v7.md` (Phases 1 – 18) to keep the files a manageable size; the
  cross-version sections stay in `HISTORY.md`. `AGENTS.md`, `README.md` and the `prep-version-release` skill list the
  new files, and the v7.2.0 and v7.3.0 PR descriptions link to the archive
- **`ARCHITECTURE.md`:** The deleted `models/ipsc/scores/request/` package and its "groundwork only" DTOs are dropped
  from the Project Structure tree and the `models/ipsc/…` section
- **`flyway-migration-versioning.md`:** The Current State table gains the sixteen migrations it was missing —
  `V7_9_0`, `V8_0_0` to `V8_9_0` and `V10_0_0` to `V10_3_0`
- **`AGENTS.md`, `prep-version-release` skill:** The reverse-sync step now also checks that table against
  `db/migration/`, and the skill checks `ARCHITECTURE.md`'s Project Structure tree
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** Gaps #33 and #34 recorded and closed, and Gap #6 progress
  noted

##### Domain

- **`MatchCompetitor.matchPoints`:** Renamed to `points` — the `match_points` column is renamed to `points` by
  `V10_0_0__rename_match_competitor_match_points_to_points.sql`, keeping its type and values
- **`MatchCompetitor.competitorCategory`, `ShooterLogCompetitor.competitorCategory`:** Back to a single
  `CompetitorCategory` held in a `competitor_category` column, replacing the list and the
  `match_competitor_category` and `shooter_log_competitor_category` child tables, by
  `V10_2_0__move_competitor_category_back_to_a_column.sql` — a row that had several categories keeps the
  alphabetically first, and one with none keeps an empty category

##### API

- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorResponse`:** **Breaking:**
  `matchPoints` renamed to `points` in the JSON contract — existing callers must use the new name
- **`MatchCompetitorRequestCsvMixIn`:** **Breaking:** CSV columns renamed to match a PractiScore export —
  `MatchClub` → `Class`, `CompetitorCategory` → `Cats`, `Division` → `Div`, `PowerFactor` → `PF` and `MatchPoints` →
  `Pts` — existing import files must use the new headers
- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorRequestCsvMixIn`,
  `MatchCompetitorResponse`:** **Breaking:** `competitorCategory` (CSV `Cats`) is a single category again rather
  than a list — a JSON string instead of an array, and one value per CSV cell instead of a `;`-separated list;
  `IpscMatchCompetitorServiceImpl` no longer resolves several categories
- **`MatchCompetitorRequest`, `MatchCompetitorRequestCsvMixIn`:** `competitorId` (CSV `CompetitorId`) is no longer
  required — a request must carry it, a `competitorNumber` or a `name`, which `IpscMatchCompetitorServiceImpl`
  resolves through `CompetitorRepository.findByFullNameIgnoreCase`
- **`IpscMatchCompetitorController`:** The CSV import's Swagger example now shows the new PractiScore-style headers,
  including `Name`, `Mem #` and the overall-score columns

#### 🗑️ Removed

##### Models

- **`MatchOverallScoresRequest`, `MatchOverallScoresRequestForCSV`, `MatchStageScoresRequest`,
  `MatchStageScoresRequestForCSV`:** The `ipsc.scores.request` package and its tests are deleted — they modelled
  per-stage and overall match scores for the `MatchStage` table and entities, which were removed, and nothing
  references them any more; the overall-score fields now live on `MatchCompetitor`

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
