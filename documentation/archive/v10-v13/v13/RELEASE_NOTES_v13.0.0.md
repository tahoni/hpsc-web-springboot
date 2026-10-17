# Release Notes – Version 13.0.0

**Release Date:** October 7, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Mapper Extraction, Division–Firearm Type Consistency & Match Competitor Contract Tightening**

Version 13.0.0 is a major release. The field copying and lookups that the competitor, match and match competitor
services carried move into mapper components, a division records the firearm type it is shot with and has a name of its
own, and the match competitor's contract tightens. Three groups of these changes break existing callers — see the
Migration Guide.

---

## ⭐ Key Highlights

### 🗺️ Mappers Take Over the Field Copying

- New `CompetitorMapper`, `MatchMapper` and `MatchCompetitorMapper` hold `applyFields`, a new `applyPatchFields` and the
  lookups the services carried, so a request model can be copied onto an entity without a repository and the services
  keep only the orchestration
- `IpscCompetitorServiceImpl`, `IpscMatchServiceImpl` and `IpscMatchCompetitorServiceImpl` take a mapper in place of
  the repositories and services those lookups used, and their `patch…` methods call `applyPatchFields`

### 🔫 A Division Belongs to a Firearm Type

- `Division` has a `firearmType`, and every division has a name of its own: the shotgun, .22 and mini rifle divisions
  that shared a handgun name are renamed (for example `"Shotgun Open Division"` and `".22 Open Division"`), the 
  semi-auto rifle divisions become `"Rifle Open Division"` and `"Rifle Standard Division"`, and the shotgun standard manual
  division becomes `"Shotgun Standard Manual Division"`
- `MatchCompetitorRequest.validate` and `MatchCompetitorMapper` reject a division that does not belong to the firearm
  type, and a firearm type that is left out is taken from the division
- `V11_4_0` renames the stored divisions by each row's firearm type

### 🧾 A Tighter Match Competitor Contract

- `MatchCompetitorResponse` lists the competitor's names in `competitorNames`, once each, in place of `competitorName`
- `powerFactor` is required on a match competitor request, and `match_competitor.firearm_type` and `power_factor` are
  `NOT NULL`
- The stored category `Lady, Senior` is renamed `Lady Senior`, matching `CompetitorCategory.SENIOR_LADY`, by `V11_5_0`

### 🧹 Leaner Enums and Models

- The `code` and `abbreviation` fields, and the lookup methods that only tests called, are removed from `Division`,
  `FirearmType` and `PowerFactor`, and `CompetitorCategory` swaps its code for an abbreviation
- `MatchCompetitorRequest` and `MatchCompetitorPatchRequest` extend `IpscMatchScore`, whose `weightedPoints` is renamed
  `points`, and the power factor leaves the score models

---

## 📦 What's New

### Added

- `CompetitorMapper`, `MatchMapper` and `MatchCompetitorMapper`, with `applyFields`, `applyPatchFields` and the lookups
  they replace, and `MatchCompetitorMapper.validateDivisionMatchesFirearmType`
- `Division.getFirearmType()`
- `CompetitorCategory.getAbbreviation()`, with the values `J`, `SJ`, `L`, `LS`, `S`, `SS` and `GS`
- `V11_2_0` (firearm type and power factor `NOT NULL`), `V11_3_0` (the optional `match_competitor.date_calculated`
  column), `V11_4_0` (unique division names) and `V11_5_0` (`Lady Senior`)
- `MatchCompetitor.dateCalculated`, `CompetitorRequest.validate`, `MatchRequest.validate` and
  `MatchCompetitorRequest.validate`, and constructors that build `CompetitorResponse`, `MatchResponse` and
  `MatchCompetitorResponse` from their entities

### Changed

- **Breaking:** `MatchCompetitorResponse.competitorName` is replaced by a `competitorNames` list, so the JSON of the
  `/ipsc/match-competitors` responses and of the bulk import results changes shape
- **Breaking:** `powerFactor` is required on a match competitor request, so a bulk import row or API call that leaves it
  out now fails
- **Breaking:** the division names a request may use change: the shotgun, .22 and mini rifle divisions that shared a
  handgun name are renamed, the semi-auto rifle divisions become `Rifle Open Division` and `Rifle Standard Division`,
  and `Standard Manual Division` becomes `Shotgun Standard Manual Division`
- **Breaking:** the category `Lady, Senior` is now `Lady Senior`, so a request or import row using the old name is
  rejected
- `MatchCompetitorRequest.firearmType` is optional: `validate` accepts a null or blank value and the mapper takes it from
  the division
- `NumberUtil.calculatePercentage` and `calculateSum` take a `scale` parameter, `CompetitorHelpers.toSentenceCaseLastName`
  returns an empty string for a null last name, and `SystemConstants` is `final`
- `IpscMatchCompetitorServiceImpl.patchMatchCompetitor` ignores a blank `powerFactor` instead of clearing it
- `ClubIdentifier`'s codes are `"C SOSC"`, `"B HPSC"`, `"A PMPSC"` and `"U VISITOR"`
- Methods that can return `null` are annotated `@Nullable`, `IpscMatchCompetitorController.createMatchCompetitors` uses
  `HttpStatus.UNPROCESSABLE_CONTENT` instead of the deprecated `UNPROCESSABLE_ENTITY`, `.gitattributes` normalises text
  files to LF, and `qodana.yaml` uses the `qodana.recommended` profile

### Fixed

- `NumberUtil.calculatePercentage` divides with `scale + 2` digits instead of `scale * 2`, so a scale below 2 no longer
  rounds the ratio too early (2/3 at scale 0 gave `100` instead of `67`)

### Removed

- `Division.code`, `abbreviation`, `fromAbbreviation`, `fromAbbreviationOrName` and `fromCode`, `FirearmType.code` and
  `fromCode`, `PowerFactor.abbreviation` and `fromAbbreviation`, and `CompetitorCategory.code` and `fromCode` — none had
  a production caller outside the enum
- `Division.RIFLE_MANUAL_ACTION_CONTEMPORARY` and `RIFLE_MANUAL_ACTION_BOLT`, replaced by `RIFLE_STANDARD_MANUAL`
- `IpscMatchCompetitorServiceImpl.parseCompetitorNumber`, and `powerFactor` from `IpscCommonScore`, `IpscMatchScore` and
  `IpscMatchStageScore`

---

## 🚀 Migration Guide

- **Match competitor API consumers.** Read the competitor's names from `competitorNames` (a list) instead of
  `competitorName`, in both the single-entry endpoints and the bulk import results.
- **Match competitor importers and callers.** Send a `powerFactor` on every request. A `firearmType` may be omitted and
  is then taken from the division; if both are sent, the division must belong to the firearm type.
- **Division names.** Use `Shotgun Open Division`, `Shotgun Modified Division`, `Shotgun Semi Division`,
  `.22 Open Division`, `.22 Standard Division`, `.22 Classic Division`, `.22 Optics Division`,
  `Mini Rifle Open Division`, `Mini Rifle Standard Division`, `Rifle Open Division`, `Rifle Standard Division` and
  `Shotgun Standard Manual Division` for the divisions that were renamed. The two manual action rifle divisions are removed in favour of `Rifle Standard Manual Division`, and any
  stored `Manual Action Contemporary Division` or `Manual Action Bolt Division` is not migrated and reads back as
  `null`.
- **Category.** Use `Lady Senior` instead of `Lady, Senior`.
- **Database.** Four Flyway migrations run on upgrade. `V11_2_0` is refused, leaving the columns as they were, if any
  `match_competitor` row has a `NULL` `firearm_type` or `power_factor` — backfill those rows first. `V11_4_0` and
  `V11_5_0` rewrite the stored divisions and the category; `shooter_log_overall` keeps its divisions, which read back
  as the handgun ones.

---

## 📊 Statistics

- **Files Changed:** 75 against `develop`, counted before this release's notes and PR description were added;
  4,204 insertions and 2,509 deletions
- **New Source Files:** 3 mappers (`CompetitorMapper`, `MatchMapper`, `MatchCompetitorMapper`) and their 3 test classes,
  plus 4 Flyway migrations
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **Mapping is not orchestration.** The mappers copy and resolve, and the services decide what to save, so a request can
  be turned into an entity — and a patch applied — in a unit test with no repository.
- **A name identifies one division.** With every division named uniquely, `DivisionConverter` can read a division back
  from the column alone, and the division's firearm type can then be checked against the entry's.
- **Validate early, and again where it matters.** `MatchCompetitorRequest.validate` refuses a mismatched division before
  anything is looked up, and `MatchCompetitorMapper` repeats the check for a patch, which carries only some fields.
- **Migrate what is renamed.** Each renamed division or category has a migration that rewrites the stored value, using
  the row's firearm type where the old name was shared.
- **A MAJOR.** The response shape, the required power factor and the renamed division and category names break
  existing callers, which `AGENTS.md`'s Semantic Versioning rules classify as MAJOR.

---

## 🧪 Testing

- `./mvnw test` passes: 1,265 tests with no failures, errors or skips.
- New unit tests cover the three mappers, including `applyPatchFields`, the division and firearm type checks, the unique
  division names and each division's firearm type, and the requests' `validate()` methods; the integration tests cover a
  firearm type taken from the division.
- The new migrations were not run against MySQL; the tests use the H2 `test` profile.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports,
  is read but never used as the row's own ID, since the imports only create records.
- No service or controller operates on `ShooterLog`, `ShooterLogCompetitor` or `ShooterLogOverall` yet, and no
  calculation job fills them in (Gap #6, partially completed).
- `pom.xml` still overrides `tomcat.version` (to `11.0.26`) for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26). `logback.version`, the Jackson BOM properties and `flyway-mysql` are likewise set above
  Spring Boot's managed versions, and `mysql-connector-j` is pinned to `26.7.0` against the `9.7.0` it manages.
- Seven utility methods and `SystemConstants.DEFAULT_SCALE` are still used only by tests (Gap #38, partially completed),
  and so are `ClubIdentifier.code` and `fromCode` (Gap #39).
- Stored `Manual Action Contemporary Division` and `Manual Action Bolt Division` values are not migrated and read back
  as `null`.

---

## 🔮 Future Enhancements

- Build a `ShooterLogService` and controller over the existing repositories, committing through `TransactionService`,
  following the same phased pattern that closed Gap #1 (Gap #6).
- Remove or give a caller to the remaining test-only utility methods, `SystemConstants.DEFAULT_SCALE` and
  `ClubIdentifier.code` (Gaps #38 and #39).
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.26` or later (Gap #26), and the
  other dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge
- Claude Code (Claude Sonnet 5.5) — co-author of most of the release's commits

---

## 📝 Notes

Version 13.0.0 moves the field copying into mappers, gives every division a name and a firearm type of its own and
tightens the match competitor's contract — a breaking change for existing callers; see the Migration Guide for what to
change. Four database migrations run on upgrade.

---
