# Release Notes – Version 9.0.0

**Release Date:** October 1, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Match Stages Removed & CSV Import Simplification**

Version 9.0.0 is a major release. Match stages — the `IpscMatchStage` and `MatchStageCompetitor` entities and
everything built on them — are removed from the match API, the services and the database, since stage results are not
recorded through this API. The bulk CSV imports are also simplified: each row is now read straight into a
`MatchRequest` or `CompetitorRequest` through a Jackson mix-in, so the intermediate CSV models and their copy steps
are gone, and a CSV header may omit optional columns. `PATCH` on a match or competitor gets its own request model, so
a partial update no longer has to repeat the required fields. The datasource username also moves from the
`MYSQL_USER` environment variable into the `dev` and `prod` profiles.

---

## ⭐ Key Highlights

### 🧹 Stages Removed

- `MatchRequest` and `MatchResponse` lose `stages`, and the bulk match CSV import no longer has a `Stages` column
- `IpscMatchStage`, `MatchStageCompetitor` and their repositories are removed, and a new Flyway migration drops their
  tables
- `TransactionService.saveMatch` is back to a single `saveMatch(IpscMatch)`

### 📥 Simpler CSV Imports

- New `MatchRequestCsvMixIn` and `CompetitorRequestCsvMixIn` bind the CSV headers onto the request constructors, so the
  `MatchRequestForCSV` and `CompetitorRequestForCSV` models are removed
- A CSV header may omit optional columns and unknown columns are ignored; only the required columns must be present
- The accepted CSV columns are otherwise unchanged

### ✏️ Truly Partial `PATCH`

- New `MatchPatchRequest` and `CompetitorPatchRequest` replace `MatchRequest` and `CompetitorRequest` on the `PATCH`
  endpoints — no field is required, and any field left out is left unchanged
- A competitor patch that omits `emailAddresses` keeps the competitor's existing addresses

### 🔒 Stricter Match Requests

- `MatchRequest.matchFirearmType` and `matchCategory` are now required properties, so a JSON body that leaves either
  out is rejected when it is read

### 🏠 Home Club by Abbreviation

- A competitor's home club now also resolves by club abbreviation when no club has a matching name

---

## 📦 What's New

### Changed

#### Match API

- **Breaking — `MatchRequest`, `MatchResponse`:** The `stages` field is gone from both, and the bulk CSV import no
  longer has a `Stages` column
- **Breaking — `MatchRequest.matchFirearmType`, `MatchRequest.matchCategory`:** Now required properties, like
  `matchDate` and `matchName`. They were already needed to create or replace a match, so only the error response
  changes
- **`TransactionService.saveMatch`:** The `saveMatch(IpscMatch, List, StageSaveMode)` overload and the `StageSaveMode`
  enum are removed
- **`IpscMatchService.deleteMatch`:** No longer checks for recorded stage results; a match is still refused deletion
  while it has competitor results or is referenced by shooter logs
- **`MatchRequestCsvMixIn`:** New Jackson mix-in; `MatchRequestForCSV` and `IpscMatchServiceImpl.toRequest` are removed
- **`MatchPatchRequest`, `IpscMatchService.patchMatch`, `IpscMatchController.patchMatch`:** New request model for
  `PATCH /ipsc/matches/{matchId}`. It has no `matchId`, so a body that sent one now has it ignored
- **`IpscMatchServiceImpl.readMatches`:** A CSV header may omit optional columns and unknown columns are ignored;
  `MatchDate`, `MatchName`, `MatchFirearmType` and `MatchCategory` must be present in the header and in every row.
  A `MatchId` column is read but never used
- **`IpscMatchController.createMatches`:** The bulk import's Swagger request schema is now plain text with an example
  header row

#### Competitor API

- **`CompetitorRequestCsvMixIn`:** New Jackson mix-in; `CompetitorRequestForCSV` is removed and
  `IpscCompetitorServiceImpl.toRequest` becomes `normaliseCsvRequest`, taking the `CompetitorRequest` directly and
  keeping the row's `competitorId`
- **`CompetitorRequest.emailAddresses`:** Now defaults to an empty list rather than `null`;
  `IpscCompetitorServiceImpl.splitEmailAddresses` is removed
- **`IpscCompetitorServiceImpl.readCompetitors`:** A CSV header may omit optional columns and unknown columns are
  ignored; only `FirstName` and `LastName` are required. A `CompetitorId` column is read but never used
- **`IpscCompetitorController.createCompetitors`:** The bulk import's Swagger request schema is now plain text with an
  example header row
- **`CompetitorPatchRequest`, `IpscCompetitorService.patchCompetitor`, `IpscCompetitorController.patchCompetitor`:**
  New request model for `PATCH /ipsc/competitors/{competitorId}`. Its `emailAddresses` is `null` unless supplied, and
  it has no `competitorId`, so a body that sent one now has it ignored
- **`IpscCompetitorServiceImpl.resolveHomeClub`:** Also resolves a home club by abbreviation

#### Documentation

- **`README.md`, `ARCHITECTURE.md`:** Stages dropped from the match description, the entity and repository tables and
  the Project Structure tree; the CSV models replaced by the mix-ins and patch request models
- **`CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md`:** Past-release entries keep the class
  names they were written with

#### Tests

- **New tests:** `MatchRequestCsvMixInTest`, `CompetitorRequestCsvMixInTest`, `MatchPatchRequestTest` and
  `CompetitorPatchRequestTest`
- **Stage tests:** Stage coverage removed from the match, competitor, repository and transaction service tests

#### Build & Metadata

- Project version bumped to **9.0.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match
- **`mysql-connector-j`:** Pinned to `9.4.0` in `pom.xml` instead of the Spring Boot-managed version

### Configuration

- **Breaking — `spring.datasource.username`, `MYSQL_USER`:** The username is no longer read from `MYSQL_USER`; the
  `dev` and `prod` profiles now set it (`hpsc_dev`, `hpsc_prod`)
- **`application-dev.properties`:** The datasource URL now points at `127.0.0.1` rather than `localhost`
- **`application-local.properties`:** Drops the `MYSQL_LOCAL_PASSWORD` override, so the local profile uses
  `MYSQL_PASSWORD` like the others

### Removed

#### Persistence

- **Breaking — `IpscMatchStage`, `MatchStageCompetitor`:** Both entities are removed, together with
  `IpscMatchStageRepository`, `MatchStageCompetitorRepository` and the `IpscMatch.stages` collection
- **`V7_9_0__drop_ipsc_match_stage.sql`:** New Flyway migration dropping the `match_stage_competitor` and
  `ipsc_match_stage` tables with `DROP TABLE IF EXISTS`. Any existing stage data is discarded

#### Match API

- **`MatchStageRequest`, `MatchStageResponse`:** Removed, along with `IpscMatchServiceImpl.toStages` and `parseStages`

### Security

#### Dependencies

- **`tomcat.version`:** Raised from `11.0.25` to `11.0.26` in `pom.xml`
- **`logback.version`:** New `pom.xml` override pinning Logback to `1.6.5`
- **`jackson-2-bom.version`, `jackson-bom.version`:** New `pom.xml` overrides raising the Jackson 2 BOM to `2.22.3`
  and the Jackson 3 BOM to `3.2.3`, so `jackson-core` and `jackson-dataformat-csv` follow
- **`flyway-mysql`:** Pinned to `13.7.0` instead of the Spring Boot-managed version

---

## 🚀 Migration Guide

- **Stages.** Stop sending `stages` in match requests and drop the `Stages` column from match CSV imports. Stop reading
  `stages` from match responses. The new migration drops the `ipsc_match_stage` and `match_stage_competitor` tables, so
  **back up any stage data you want to keep before deploying** — it is discarded.
- **Match requests.** Always send `matchFirearmType` and `matchCategory` (alongside `matchDate` and `matchName`) on
  `POST` and `PUT`. A body without them is now rejected when it is read; the service already needed them.
- **`PATCH` requests.** `PATCH /ipsc/matches/{matchId}` and `PATCH /ipsc/competitors/{competitorId}` no longer need any
  body field, and a `matchId` or `competitorId` in the body is ignored — the path ID decides. A competitor patch that
  leaves out `emailAddresses` now keeps the existing addresses.
- **Database username.** If a deployment supplied the datasource username through `MYSQL_USER`, run it with the `dev`
  or `prod` profile (which set `hpsc_dev` and `hpsc_prod`), or set `spring.datasource.username` itself.
- **Local profile.** If you set `MYSQL_LOCAL_PASSWORD`, set `MYSQL_PASSWORD` instead.
- **CSV imports.** The accepted column names are unchanged apart from `Stages`, so existing match and competitor CSV
  files keep working once that column is removed.

---

## 📊 Statistics

- **Files Changed:** 63 on the release branch against `main` (9 added, 13 deleted, 41 modified, counted before the
  release documentation was written); 1,505 insertions and 3,086 deletions
- **New Source Files:** 4 (`MatchRequestCsvMixIn`, `CompetitorRequestCsvMixIn`, `MatchPatchRequest`,
  `CompetitorPatchRequest`), plus the `V7_9_0__drop_ipsc_match_stage.sql` migration and their tests
- **Deleted Source Files:** 8 (`IpscMatchStage`, `MatchStageCompetitor`, `IpscMatchStageRepository`,
  `MatchStageCompetitorRepository`, `MatchRequestForCSV`, `CompetitorRequestForCSV`, `MatchStageRequest`,
  `MatchStageResponse`), plus their tests
- **New Dependencies:** 0 (the Jackson BOMs are imported and four existing dependencies re-pinned — see Security)

---

## 🧭 Design Notes

- **Read CSV rows straight into the request.** Binding the headers onto the request constructor through a mix-in
  removes a model and a copy step per import, and keeps the CSV and JSON paths validated by the same type.
- **A separate patch model.** With `MatchRequest` requiring its date, name, firearm type and category, reusing it for
  `PATCH` would force callers to repeat them. `MatchPatchRequest` and `CompetitorPatchRequest` have no required field
  and no ID, since the path identifies the resource.
- **`null` means "leave unchanged" in a patch.** A competitor patch's `emailAddresses` is `null` unless supplied, so
  omitting it cannot clear the stored addresses; the full request defaults it to an empty list.
- **IDs in CSV rows are read, not used.** The imports only create records, so a `MatchId` or `CompetitorId` column is
  accepted but ignored.
- **Stages removed rather than hidden.** The entities, repositories, DTOs and tables go together, so no dead schema is
  left behind — which is also why the migration is destructive.
- **A MAJOR.** Removing `stages` from the match contract, making two request properties required, removing the stage
  entities and dropping `MYSQL_USER` each break existing callers or deployments, which `AGENTS.md`'s Semantic
  Versioning rules classify as MAJOR.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` passes on the release branch: 947 tests across 69 test classes, with 0 failures, 0 errors
  and 0 skipped. JaCoCo reports 98.32% line and 98.69% branch coverage, clearing the 97% gate.
- New `MatchRequestCsvMixInTest` and `CompetitorRequestCsvMixInTest` cover the CSV header binding, including omitted
  optional columns and missing required ones; `MatchPatchRequestTest` and `CompetitorPatchRequestTest` cover the patch
  models.
- Stage coverage is removed from the match, competitor, repository and transaction service tests along with the
  entities it covered.

---

## 🐛 Known Issues

- Existing stage data is discarded by the `V7_9_0__drop_ipsc_match_stage.sql` migration — back it up first if it is
  needed.
- A `MatchId` or `CompetitorId` column in a CSV import is read but never used.
- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- A competitor or match referenced by results or shooter logs can't be deleted through the API, since no endpoint
  removes those rows yet (carried over from v8.8.0, pending Gap #6).
- `pom.xml` still overrides `tomcat.version` (to `11.0.26`) for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26). `logback.version`, the Jackson BOM properties and `flyway-mysql` are likewise set above
  Spring Boot's managed versions.
- `mysql-connector-j` is pinned to `9.4.0`, below the `9.7.0` that Spring Boot 4.1.1 manages.

---

## 🔮 Future Enhancements

- Build a `MatchScoreService`/`ShooterLogService` over the existing repositories, committing through
  `TransactionService`, following the same phased pattern that closed Gap #1 (Gap #6).
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint.
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.26` or later (Gap #26), and the
  other dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 9.0.0 removes match stages and simplifies how the CSV imports and `PATCH` endpoints read their input, at the
cost of several breaking changes — see the Migration Guide before upgrading.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
