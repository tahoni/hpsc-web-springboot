# Release Notes – Version 10.0.0

**Release Date:** October 3, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**PractiScore-Style Match Competitor Import & Overall Scores**

Version 10.0.0 is a major release. The match competitor request, patch and response models, and their CSV import, are
reshaped to read a PractiScore results export: the CSV headers are renamed, `matchPoints` becomes `points`, and the
overall-score columns (`%`, `Time`, `% psbl`, `A`, `C`, `D`, `M`, `NPM`, `NS`, `Proc` and `Apen`) are added. A request
can now identify its competitor by `competitorNumber` or `name` instead of `competitorId`, and `competitorCategory`
returns to a single value. Three of these changes break existing callers — see the Migration Guide.

---

## ⭐ Key Highlights

### 🏁 PractiScore-Style Match Competitor Import

- The match competitor CSV headers are renamed to those of a PractiScore export — `MatchClub` → `Class`,
  `CompetitorCategory` → `Cats`, `Division` → `Div`, `PowerFactor` → `PF` and `MatchPoints` → `Pts`
- `MatchCompetitor.matchPoints` is renamed `points`, in the JSON contract and the `match_competitor` table
- The bulk import's Swagger example shows the new headers

### 🔎 Identify a Competitor by Number or Name

- `competitorId` is no longer required: a request may carry a `competitorNumber` (CSV `Mem #`) or a `name` (CSV `Name`)
- The number is matched exactly against `Competitor.competitorNumber`, otherwise the "First Last" full name is matched
  ignoring case; `competitorId` wins over the number and the number over the name, and a number or name that matches no
  competitor, or more than one, is refused

### 📊 Overall Scores on a Match Competitor

- New optional fields on the request, patch and response models — `percentage`, `time`, `percentageOfPossiblePoints`,
  `alpha`, `charlie`, `delta`, `misses`, `noPenaltyMisses`, `noShoots`, `proceduralErrors` and `additionalPenalties`
- Stored in new `match_competitor` columns and carried through `IpscMatchCompetitorServiceImpl`

### 🏷️ One Competitor Category Again

- `MatchCompetitor.competitorCategory` and `ShooterLogCompetitor.competitorCategory` return to a single
  `CompetitorCategory` in a `competitor_category` column, replacing the list and its two child tables

---

## 📦 What's New

### Added

#### Domain

- **`MatchCompetitor` overall scores:** New optional columns after `points` — `percentage`, `time`,
  `percentage_of_possible_points`, the `alpha`, `charlie` and `delta` hit counts, `misses`, `no_penalty_misses`,
  `no_shoots`, `procedural_errors` and `additional_penalties`

#### API

- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorRequestCsvMixIn`:** New optional `name`
  (CSV `Name`) and `competitorNumber` (CSV `Mem #`)
- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorResponse`:** New optional overall-score
  fields, with CSV columns named as in a PractiScore export

#### Repositories

- **`CompetitorRepository.findByCompetitorNumber`, `findByFullNameIgnoreCase`:** New finders returning a list, so a
  match on several competitors can be refused

#### Database

- **`V10_0_0` to `V10_3_0`:** Four Flyway migrations — the `match_points` rename, the overall-score columns, the
  category column and the cleanup of an interim `hit_factor` column; see `CHANGELOG.md` for each

### Changed

#### API

- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorResponse`:** **Breaking:** `matchPoints`
  renamed to `points`
- **`MatchCompetitorRequestCsvMixIn`:** **Breaking:** CSV columns renamed to a PractiScore export's headers
- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorRequestCsvMixIn`,
  `MatchCompetitorResponse`:** **Breaking:** `competitorCategory` is a single category rather than a list
- **`MatchCompetitorRequest`, `MatchCompetitorRequestCsvMixIn`:** `competitorId` is no longer required, provided a
  `competitorNumber` or `name` is supplied
- **`IpscMatchCompetitorController`:** The CSV import's Swagger example shows the new headers

#### Domain

- **`MatchCompetitor.matchPoints`:** Renamed `points`
- **`MatchCompetitor.competitorCategory`, `ShooterLogCompetitor.competitorCategory`:** A single category in a
  `competitor_category` column; a row that had several keeps the alphabetically first, and one with none keeps an empty
  category

#### Build & Metadata

- Project version bumped to **10.0.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

#### Documentation

- **`CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md`:** Versions 1.0.0 – 7.4.1 archived, unchanged, under
  `documentation/history/`; the repository-root files keep the later versions

### Removed

#### Models

- **`MatchOverallScoresRequest`, `MatchOverallScoresRequestForCSV`, `MatchStageScoresRequest`,
  `MatchStageScoresRequestForCSV`:** The `ipsc.scores.request` package and its tests are deleted — nothing referenced
  them, and the overall-score fields now live on `MatchCompetitor`

---

## 🚀 Migration Guide

- **JSON callers.** Send and read `points` instead of `matchPoints`, and send `competitorCategory` as a string rather
  than an array.
- **CSV import files.** Rename the headers: `MatchClub` → `Class`, `CompetitorCategory` → `Cats`, `Division` → `Div`,
  `PowerFactor` → `PF` and `MatchPoints` → `Pts`. `Cats` holds one category per cell, not a `;`-separated list.
- **Competitor lookup.** `CompetitorId` may be left out if a `Mem #` (competitor number) or `Name` is given; a number
  or name that matches no competitor, or several, is refused.
- **Database.** Four Flyway migrations run at startup: `V10_0_0` renames `match_competitor.match_points` to `points`,
  `V10_1_0` adds the overall-score columns, `V10_2_0` moves the competitor category back to a column on
  `match_competitor` and `shooter_log_competitor`, and `V10_3_0` drops an interim `hit_factor` column. `V10_2_0` keeps
  the alphabetically first category of a row that had several, and an empty one for a row that had none.
- **Empty database.** Flyway creates the whole schema in an empty database. This release was checked against an empty
  MySQL 8.4 database, migrating from `V7_0_0` through `V10_3_0`, using the corrected `V10_2_0`.
- **Removed models.** Nothing in the application used `MatchOverallScoresRequest` or `MatchStageScoresRequest`; any
  external code that did must move to the `MatchCompetitor` models.

---

## 📊 Statistics

- **Files Changed:** 49 on the release branch against `develop` (counted before the release documentation was
  written); 4,571 insertions and 5,459 deletions, most of them the archived history files
- **New Source Files:** 0, plus four Flyway migrations
- **Deleted Source Files:** 4 (the `ipsc.scores.request` models), plus their four tests
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **Read the export as it comes.** The request models and CSV mix-in follow a PractiScore export's own headers, so a
  results file needs no renaming before import.
- **Several ways to name a competitor.** The service resolves the competitor by ID, then number, then name, and
  refuses an ambiguous or unmatched value rather than guessing.
- **A column, not a child table.** A single category per entry is a plain column again, which is simpler to import and
  query than a list that the export never carries.
- **A MAJOR.** Renaming a JSON field and the CSV headers, and changing `competitorCategory` from a list to a single
  value, break existing callers, which `AGENTS.md`'s Semantic Versioning rules classify as MAJOR.

---

## 🧪 Testing

- `./mvnw test` passes: 1,101 tests, with no failures, errors or skips.
- `./mvnw verify -Pcoverage` has **not yet been run** for this release; run it on the release branch before merging.
- The migrations were run against an empty MySQL 8.4 database through the Flyway Maven plugin: all 25 applied.
- Updated controller, service, implementation, integration and request-model tests cover the renamed fields, the
  single category, the competitor lookup by number and name, and the overall-score fields.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports
  is read but never used as the row's own ID, since the imports only create records.
- `MatchRequest` and `MatchRequestCsvMixIn` accept a missing `matchFirearmType` or `matchCategory`, so the service is
  the only check that rejects a create or replace without them.
- No service or controller operates on `ShooterLog`, `ShooterLogCompetitor` or `ShooterLogOverall` yet, and no
  calculation job fills them in (Gap #6, partially completed).
- `pom.xml` still overrides `tomcat.version` (to `11.0.26`) for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26). `logback.version`, the Jackson BOM properties and `flyway-mysql` are likewise set above
  Spring Boot's managed versions.

---

## 🔮 Future Enhancements

- Build a `ShooterLogService` and controller over the existing repositories, committing through `TransactionService`,
  following the same phased pattern that closed Gap #1 (Gap #6).
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.26` or later (Gap #26), and the
  other dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 10.0.0 reshapes the match competitor models and CSV import around a PractiScore export, as a breaking change
for existing callers — see the Migration Guide for what to change and the migrations that run on upgrade.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
