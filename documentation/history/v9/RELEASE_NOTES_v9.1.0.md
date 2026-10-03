# Release Notes – Version 9.1.0

**Release Date:** October 3, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Match Competitor API & Shooter Log Rework**

Version 9.1.0 is a minor release. Match competitors — a competitor's entry in one match, in one firearm type — get
their own service and controller at `/ipsc/match-competitors`, with create, replace, patch, get, delete and a bulk CSV
import. A match competitor, and a shooter log competitor, can now have several competitor categories, and the shooter
log entities are reworked around a date range of matches, with a new `ShooterLogOverall` entity for overall standings.
Competitors also gain an optional `isVerified` flag. Nothing is removed and no existing request is made stricter, so
existing callers keep working.

---

## ⭐ Key Highlights

### 🎯 Match Competitor Endpoints

- New `IpscMatchCompetitorController` at `/ipsc/match-competitors`: create, replace (`PUT`), patch, get one or all, and
  delete a competitor's entry in a match
- A competitor can have one entry per match and firearm type, under one or more competitor categories —
  `competitorCategory` is a list in the request, patch and response models
- A patch applies only the fields it supplies; a delete is refused while other records still reference the entry

### 📥 Match Competitor Bulk Import

- New `POST /ipsc/match-competitors/bulk` takes `text/csv` and returns the created entries
- Every row is checked before any is saved, so either every row is created or none is; a row that duplicates an
  existing entry, or another row in the file, is refused
- New `MatchCompetitorRequestCsvMixIn` binds the CSV headers (`CompetitorId`, `MatchId`, `CompetitorCategory`,
  `Division`, …) onto the request; `CompetitorCategory` is one cell of categories separated by `;`

### 🗂️ Several Competitor Categories

- `MatchCompetitor.competitorCategory` and `ShooterLogCompetitor.competitorCategory` are now lists, held in the new
  `match_competitor_category` and `shooter_log_competitor_category` tables, and must have at least one
- `MatchCompetitor.division` is required and `MatchCompetitor.firearmType` is now optional

### 📆 Shooter Log Rework

- `ShooterLog` is now a date range linked to many matches through `shooter_log_match`, rather than a per-competitor
  log
- New `ShooterLogOverall` entity holds a rank and points per competitor category and division; `ShooterLogCompetitor`
  links to its `Competitor` directly and gains `dateCalculated`, `competitorCategory` and `division`

### ✅ Verified Competitors

- New optional `Competitor.isVerified` flag on `CompetitorRequest` (JSON and the `IsVerified` CSV column),
  `CompetitorPatchRequest` and `CompetitorResponse`; existing competitors are backfilled to `true`

---

## 📦 What's New

### Added

#### API

- **`IpscMatchCompetitorController`:** New `/ipsc/match-competitors` endpoints, with `MatchCompetitorRequest`,
  `MatchCompetitorPatchRequest` and `MatchCompetitorResponse`
- **`IpscMatchCompetitorController.createMatchCompetitors`:** New `POST /ipsc/match-competitors/bulk` endpoint, returning
  a `MatchCompetitorResponseHolder`. A `MatchCompetitorId` column is read but ignored, since the import only creates
  entries
- **`MatchCompetitorRequestCsvMixIn`:** New Jackson mix-in binding the CSV headers onto `MatchCompetitorRequest`
- **`isVerified` on competitors:** Optional field on `CompetitorRequest`, `CompetitorPatchRequest` and
  `CompetitorResponse`

#### Services

- **`IpscMatchCompetitorService`:** New service behind the endpoints; validates required fields, resolves the
  competitor, match and enumerated values, and refuses an entry that duplicates another for the same competitor, match
  and firearm type
- **`TransactionService.saveMatchCompetitor`, `saveMatchCompetitors`, `deleteMatchCompetitor`:** New transactional
  writes; the batch save and the delete flush inside the transaction, so a constraint violation is reported as a 400

#### Domain

- **`ShooterLogOverall`:** New entity, linked to its `ShooterLog` and `Competitor`; a competitor has one row per
  shooter log
- **`ShooterLogCompetitor.dateCalculated`, `competitorCategory`, `division`:** New fields
- **`Competitor.isVerified`:** New optional `Boolean` flag

#### Repositories

- **`MatchCompetitorRepository`:** New `findByCompetitorIdAndMatchIdAndFirearmType`, `findByIdWithCompetitorAndMatch`
  and `findAllWithCompetitorAndMatch` queries
- **`ShooterLogOverallRepository`:** New repository, with `findAllByShooterLogId` and `existsByCompetitorId`
- **`ShooterLogRepository.existsByMatchesId`:** New query used by the match delete check

#### Database

- **`V8_0_0` to `V8_9_0`:** Eleven Flyway migrations covering the shooter log competitor columns, the
  `shooter_log_overall` and `shooter_log_match` tables, the competitor and overall links, `competitor.is_verified` and
  the two competitor category child tables — see `CHANGELOG.md` for each

#### Tests

- New tests for the match competitor controller, service, implementation, request models and CSV mix-in, the
  `ShooterLogOverallRepository` and `TransactionService.saveMatchCompetitors`

### Changed

#### API

- **`MatchRequest`, `MatchRequestCsvMixIn`:** `matchFirearmType` and `matchCategory` are no longer required — a JSON
  body, or a CSV header or row, that omits them is read rather than rejected
- **`CompetitorRequest`, `CompetitorResponse`, `CompetitorRequestCsvMixIn`:** `emailAddresses` follows
  `cellphoneNumber` in the constructor parameters; the JSON and CSV formats are unchanged

#### Services

- **`IpscMatchServiceImpl.deleteMatch`:** Also refuses a match linked to a shooter log through `shooter_log_match`
- **`IpscCompetitorServiceImpl.deleteCompetitor`:** Now asks `ShooterLogCompetitorRepository.existsByCompetitorId` and
  `ShooterLogOverallRepository.existsByCompetitorId`, since a shooter log no longer has a competitor of its own
- **`TransactionServiceImpl`, `IpscMatchServiceImpl`, `IpscCompetitorServiceImpl`:** Constructors take the repositories
  their new checks need

#### Domain

- **`MatchCompetitor`:** `division` and `competitorCategory` are required, `competitorCategory` is a list, and
  `firearmType` is optional
- **`ShooterLog`:** Reworked to a date range and its matches; `competitor`, `club`, `firearmType`, `powerFactor`,
  `logValue` and `calculatedDate` are removed
- **`ShooterLogCompetitor`:** Links to `Competitor` directly and is unique per shooter log and competitor; the direct
  `match` link is removed

#### Repositories

- **`ShooterLogCompetitorRepository`, `ShooterLogRepository`:** `existsByMatchId`,
  `findAllByCompetitorIdAndFirearmTypeAndPowerFactor` and `existsByCompetitorId` are removed or replaced to match

#### Build & Metadata

- Project version bumped to **9.1.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

### Dependencies

#### Database

- **`mysql-connector-j`:** The `9.4.0` pin in `pom.xml` is dropped, since Spring Boot `4.1.1` now manages `9.7.0`

---

## 🚀 Migration Guide

- **Database.** Eleven Flyway migrations run at startup. `V8_0_1` adds `NOT NULL` `competitor_category` and `division`
  columns to `shooter_log_competitor`, and `V8_6_0` adds a `NOT NULL` `competitor_id` there and links
  `shooter_log_overall`; both **fail if those tables already hold rows**. `V8_1_0` makes `match_competitor.division`
  and `competitor_category` `NOT NULL`, which fails if any existing row has a null in either.
- **Shooter log tables.** `V8_4_0` drops `shooter_log`'s `competitor_id`, `club_id`, `firearm_type`, `power_factor`,
  `log_value` and `calculated_date` columns, and `V8_3_0` drops `shooter_log_competitor.match_id`. Back up any shooter
  log data you want to keep before deploying.
- **Competitor categories.** Existing `match_competitor` categories are copied into `match_competitor_category` before
  the old column is dropped; the new endpoints take and return `competitorCategory` as a list.
- **Verified competitors.** `is_verified` is added as nullable and existing competitors are set to `true`.
- **Matches.** A match request or CSV row may now omit `matchFirearmType` and `matchCategory`.
- **MySQL connector.** `mysql-connector-j` now follows Spring Boot's managed version, `9.7.0`, rather than `9.4.0`.
- **No API changes are required** for existing callers; the new endpoints and fields are additive.

---

## 📊 Statistics

- **Files Changed:** 67 on the release branch against `main` (counted before the release documentation was written);
  4,310 insertions and 237 deletions
- **New Source Files:** 10 (`IpscMatchCompetitorController`, `IpscMatchCompetitorService`,
  `IpscMatchCompetitorServiceImpl`, `MatchCompetitorRequest`, `MatchCompetitorPatchRequest`,
  `MatchCompetitorRequestCsvMixIn`, `MatchCompetitorResponse`, `MatchCompetitorResponseHolder`, `ShooterLogOverall`,
  `ShooterLogOverallRepository`), plus eleven Flyway migrations and their tests
- **Deleted Source Files:** 0
- **New Dependencies:** 0 (the `mysql-connector-j` pin is dropped)

---

## 🧭 Design Notes

- **One more resource on the same pattern.** Match competitors follow the competitor and match design: an interface and
  implementation service, a full request model for `POST` and `PUT`, a patch model with no required fields, and a CSV
  mix-in over the same request model.
- **A batch that is all or nothing.** The bulk import builds every row and checks it — including against the other
  rows, not only the database — before saving, and saves them in one transaction that flushes, so a late constraint
  violation is a 400 rather than a half-imported file.
- **Constructor annotations decide.** With a `@JsonCreator`, a property's requirement is set on the constructor
  parameter, so the request models' fields and constructors were kept in step, and `MatchRequest` was relaxed in both.
- **Categories in a child table.** A list of categories per entry is a child table rather than a delimited column, so
  it stays queryable and unique per pair.
- **A MINOR.** New endpoints and new optional fields are backward-compatible additions; nothing is removed or made
  stricter, which `AGENTS.md`'s Semantic Versioning rules classify as MINOR.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` passes on the release branch: 1,095 tests across 77 test classes, with 0
  failures, 0 errors and 0 skipped. JaCoCo reports 98.22% line and 98.97% branch coverage, clearing the 97% gate.
- New controller, service, implementation and integration tests cover the match competitor endpoints; request-model
  tests cover JSON serialization, deserialization and each required field; `MatchCompetitorRequestCsvMixInTest` covers
  the CSV header binding.
- New `ShooterLogOverallRepositoryIntegrationTest` and `TransactionServiceTest` cases cover the new repository and
  batch save.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports
  is read but never used as the row's own ID, since the imports only create records.
- `MatchRequest` and `MatchRequestCsvMixIn` now accept a missing `matchFirearmType` or `matchCategory`, so the service
  is the only check that rejects a create or replace without them.
- No service or controller operates on `ShooterLog`, `ShooterLogCompetitor` or `ShooterLogOverall` yet, and no
  calculation job fills them in (Gap #6, partially completed).
- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- `pom.xml` still overrides `tomcat.version` (to `11.0.26`) for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26). `logback.version`, the Jackson BOM properties and `flyway-mysql` are likewise set above
  Spring Boot's managed versions.

---

## 🔮 Future Enhancements

- Build a `ShooterLogService` and controller over the existing repositories, committing through `TransactionService`,
  following the same phased pattern that closed Gap #1 (Gap #6).
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint.
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.26` or later (Gap #26), and the
  other dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 9.1.0 adds the match competitor endpoints and reworks the shooter log entities beneath them, as additions
that keep existing callers working — see the Migration Guide for the migrations that run on upgrade.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
