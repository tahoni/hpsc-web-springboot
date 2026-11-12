# Release Notes – Version 16.1.0

**Release Date:** November 8, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Shooter Log Management & Overall Results**

Version 16.1.0 is a minor release. Shooter logs can now be created and managed over the API, and a log's overall
results are calculated from each competitor's best four match percentages. Nothing existing changes for callers, so the
release is backward-compatible. The unused `shooter_log_competitor` table and a handful of unused constants and helpers
are removed.

---

## ⭐ Key Highlights

### 📒 Shooter Logs

- `IpscShooterLogController` serves `/ipsc/shooter-logs`: `POST` creates a log, `PUT` and `PATCH /{shooterLogId}`
  replace or partially update one, `GET` returns every log, `GET /{shooterLogId}` returns one and
  `DELETE /{shooterLogId}` removes one
- A log is a start date, an end date and the matches it covers, set by match ID. A request that names a match that does
  not exist is refused with a `404`
- Every match must be scheduled within the log's start and end dates, both inclusive, and a log that has overall
  results cannot be deleted; both are refused with a `400`

### 🏁 Shooter Log Overall Results

- `IpscShooterLogOverallController` serves `/ipsc/shooter-log-overall-results`: `POST /logs/{shooterLogId}` calculates
  a log's overall results, `GET /logs/{shooterLogId}` returns them for one log and `GET` returns them for every log
- A competitor's overall percentage is the average of their best `IpscConstants.SHOOTER_LOG_BEST_MATCHES` (four)
  percentages across the log's matches, or of all of them if they have fewer
- Competitors are ranked within their division and competitor category, taken from their most recent match, with ties
  sharing a rank
- Results are recalculated only when they are out of date, and the overall results of competitors who no longer have a
  result in the log are removed

### 🗄️ Database Clean-up

- `V11_12_0__drop_shooter_log_competitor.sql` drops the `shooter_log_competitor` table, which nothing references any
  more, since a competitor's rank and percentage in a log live in `shooter_result`
- `ShooterLogCompetitor`, `ShooterLogCompetitorRepository` and their fixture and tests are removed

### ♻️ Unused Code

- `IpscConstants.MATCH_POINTS_SCALE`, `HIT_FACTOR_SCALE` and `TIME_SCALE`, `SystemConstants.DEFAULT_SCALE` and
  `NumberUtil.calculateSum` are removed, since nothing in production called them (Gap #41)
- `IpscConstants.REPLACE_IN_NAMES_REGEX` is inlined into `CompetitorHelpers.RANGE_OFFICER_MARKER`, whose Javadoc now
  also covers the `TRO` training range officer marker

---

## 📦 What's New

### Added

- `IpscShooterLogController` and `IpscShooterLogOverallController`
- `IpscShooterLogService`/`IpscShooterLogServiceImpl` and
  `IpscShooterLogOverallService`/`IpscShooterLogOverallServiceImpl`
- `TransactionService.saveShooterLog`, `deleteShooterLog` and `replaceShooterLogOverallResults`
- `ShooterLogOverallRepository.existsByShooterLogId`, and fetch-joining finders on `ShooterLogRepository`,
  `ShooterLogOverallRepository` and `ShooterResultRepository` so responses can be mapped outside a transaction
- `ShooterLogRequest`, `ShooterLogPatchRequest`, `ShooterLogResponse`, `ShooterLogOverallResponse` and
  `ShooterLogOverallResponseHolder`
- `IpscConstants.SHOOTER_LOG_BEST_MATCHES`
- Controller, service, integration and request validation tests for both shooter log stacks

### Changed

- `jacoco.version` is bumped from 0.8.15 to 0.8.16 and `springdoc-openapi-bom` from 3.1.1 to 3.2.0
- `ARCHITECTURE.md`, `README.md` and `CONTRIBUTING.md` describe the shooter log controllers and services
- `documentation/recommendations/flyway-migration-versioning.md` gains a row for `V11_12_0`
- `documentation/roadmap/improvement-plan.md` and `improvement-plan-tasks.md` close Gaps #6, #41 and #42

### Removed

- `ShooterLogCompetitor`, `ShooterLogCompetitorRepository`, `ScoringFixtures.shooterLogCompetitor` and the
  `shooter_log_competitor` table
- The `ShooterLogCompetitorRepository` checks in `IpscCompetitorServiceImpl.deleteCompetitor` and
  `IpscMatchServiceImpl.deleteMatch`
- `IpscConstants.MATCH_POINTS_SCALE`, `HIT_FACTOR_SCALE`, `TIME_SCALE` and `REPLACE_IN_NAMES_REGEX`,
  `SystemConstants.DEFAULT_SCALE` and `NumberUtil.calculateSum`

---

## 🚀 Migration Guide

No action is needed for callers: no existing endpoint, request or response changes.

Flyway runs `V11_12_0` on startup and drops `shooter_log_competitor`. Any rows in the table are discarded, and nothing
read them, so check that no external report or query still uses the table before deploying.

---

## 📊 Statistics

- **Files Changed:** 55 against `main` before this release's documentation; 4,252 insertions and 536 deletions
- **New Source Files:** 12 (11 classes and a migration)
- **Renamed Source Files:** 0
- **Deleted Source Files:** 2
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **Best four, not four.** A competitor with fewer than four matches is averaged over the matches they have, rather
  than divided by four, so a newcomer is not penalised for matches they could not shoot. A match with no percentage is
  ignored.
- **Category and division from the latest match.** A competitor can change either during a log, so the most recent
  match decides where they are ranked.
- **Recalculate only when out of date.** Overall results are rewritten only when they are out of date, and the rows of
  competitors who no longer have a result are deleted in the same transaction.
- **Dates are enforced where a log is written.** `IpscShooterLogServiceImpl` checks every match against the log's dates
  on create, replace and patch, so a patch that moves a date is checked against the matches already linked.
- **Writes go through `TransactionService`.** Both new services commit through it, so a constraint violation surfaces
  as a `ValidationException` rather than a raw `DataIntegrityViolationException`.
- **A MINOR.** The release adds endpoints and removes nothing that was part of the public API, which `AGENTS.md`'s
  Semantic Versioning rules classify as backward-compatible new functionality.

---

## 🧪 Testing

- `./mvnw clean test` passes: 1,439 tests with no failures, errors or skips.
- New tests cover both controllers with Mockito, both services at the interface and implementation tiers, both services
  end-to-end against the H2 `test` profile, the request validation, and the transaction methods.
- Branch coverage is closed for the shooter log overall, shooter log patch, shooter result and match competitor
  resolution paths. Only simple null guards and one unreachable defensive check are left uncovered.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports,
  is read but never used as the row's own ID, since the imports only create records.
- Calculating a log's overall results does not re-check its matches' dates; they are enforced when the log is written,
  so a match rescheduled afterwards is still counted.
- Shooter logs can only be created one at a time; there is no bulk import for them.
- `pom.xml` still overrides `logback.version`, the Jackson BOM properties and `flyway-mysql`, set above Spring Boot's
  managed versions.

---

## 🔮 Future Enhancements

- Add a bulk import for shooter logs if creating them one by one proves slow.
- Re-check a log's matches against its dates when its overall results are calculated, or when a match is rescheduled.
- Drop the remaining dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge
- dependabot[bot]
- Claude Code (Claude Sonnet 5.5) — co-author of the release's commits

---

## 📝 Notes

Version 16.1.0 adds shooter log management and overall results, and drops the unused `shooter_log_competitor` table
and a few unused constants. It leaves the improvement plan with all 42 gaps closed and none open.

---
