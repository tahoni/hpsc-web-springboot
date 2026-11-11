# Release Notes – Version 16.4.0

**Release Date:** November 10, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Changing Existing Records in Bulk**

Version 16.4.0 is a minor release. The bulk CSV imports could create records but not change them: an existing shooter
log could be replaced or patched only one request at a time, a competitor row for someone already stored hit the
unique club number, and a match that had fallen outside the dates of its shooter log said nothing when it was read.
New `PUT` and `PATCH` bulk endpoints change existing shooter logs, with `PATCH` able to add and remove matches, a
competitor row for an existing club number now updates and verifies that competitor, and a match reports the conflict
in its `warnings`. Every addition is optional or new, so the release is backward-compatible.

---

## ⭐ Key Highlights

### 📥 Bulk Shooter Log Updates

- `PUT /ipsc/shooter-logs/bulk` replaces existing shooter logs and `PATCH /ipsc/shooter-logs/bulk` updates them, from
  `text/plain` CSV with a `ShooterLogId` column beside `StartDate`, `EndDate` and `MatchIds`
- Each row goes through `updateShooterLog` or `patchShooterLog` in its own transaction, so a failed row is reported
  with the values it supplied and the rest are still saved; the endpoints answer `200`, or `422` if every row failed
- In a bulk `PATCH` an empty column leaves that field unchanged, and a bulk `PUT` needs both dates and clears the
  matches when `MatchIds` is empty

### ➕ Adding and Removing Matches

- `ShooterLogPatchRequest` gains `addMatchIds` and `removeMatchIds`, and the bulk `PATCH` the matching `AddMatchIds`
  and `RemoveMatchIds` columns, so a log's matches change without restating them
- `matchIds` still replaces the list in full and is applied first, then the additions, then the removals; adding a
  match the log already covers, or removing one it does not, changes nothing
- An unknown match to add is a `404`, a match both added and removed is a `400`, and every match the log ends up with
  is still checked against its dates

### 🏆 Competitor Import Updates

- A `POST /ipsc/competitors/bulk` row whose club number is an existing competitor's updates that competitor with the
  row's data and marks it verified, instead of creating another
- A row without a club number, as every row for a home club other than HPSC's is, always creates a competitor, since
  neither a name nor an ID number can tell two people apart
- `CompetitorBulkResponseHolder.results` holds a `CompetitorBulkResponse` for every row, with its row number, a message
  and the competitor; `competitors` is unchanged

### ⚠️ Match Warnings

- A match outside the dates of a shooter log that covers it now says so in `MatchResponse.warnings` when it is read,
  and when it is edited without changing its day
- The match is still returned, so it stays readable and fixable, and a move to another day outside the dates is still
  refused with a `400`

### 🧪 Migrations in CI

- A new `migrations` job in `build.yml` runs `FlywayMysqlMigrationTest` against a throwaway `mysql:8.4` service
  container on every push and pull request to `main` and `develop`
- The test applies every migration to an empty database through the application's runtime classpath, and checks that
  none is pending or failed, the application tables exist and the checksums validate
- It runs only when `FLYWAY_MYSQL_URL` is set, so the normal build, which uses H2, needs no MySQL and reports it as
  skipped

### 🛠️ Housekeeping

- `flyway-mysql` takes the `12.4.0` Spring Boot manages instead of a `13.7.0` pin that mixed two Flyway majors; all 38
  migrations were applied to a throwaway MySQL 8.4 on it
- The match, competitor and match competitor CSV imports stop reading their own ID column, which was bound but never
  used

---

## 📦 What's New

### Added

- `IpscShooterLogController.updateShooterLogs` and `patchShooterLogs`, serving `PUT` and
  `PATCH /ipsc/shooter-logs/bulk`, with `IpscShooterLogService.updateShooterLogs` and `patchShooterLogs`
- `ShooterLogBulkRequest` and `ShooterLogBulkRequestCsvMixIn`, binding the bulk update columns
- `FlywayMysqlMigrationTest` and the `migrations` job in `.github/workflows/build.yml`, running the Flyway migrations
  against MySQL 8.4
- `ShooterLogPatchRequest.addMatchIds` and `removeMatchIds`, with `validate`, and `ShooterLogRow.shooterLogId`,
  `addMatchIds` and `removeMatchIds`
- `CompetitorBulkResponse` and `CompetitorBulkResponseHolder.results`
- `MatchResponse.warnings`, `IpscMatchServiceImpl` filling it, and `ShooterLogHelpers.describeOutsideDates`
- Mockito, mix-in and `@SpringBootTest` integration tests for the above

### Changed

- `IpscShooterLogService.patchShooterLog` adds and removes matches after any replacement
- `IpscCompetitorServiceImpl.createCompetitors` updates and verifies a competitor whose club number a row names
- `MatchRequestCsvMixIn`, `CompetitorRequestCsvMixIn` and `MatchCompetitorRequestCsvMixIn` stop reading a `MatchId`,
  `CompetitorId` or `MatchCompetitorId` column respectively, and `MatchRequest`, `CompetitorRequest` and
  `MatchCompetitorRequest` gain a constructor without their own ID
- `pom.xml` no longer pins `flyway-mysql`
- `ARCHITECTURE.md` and `CONTRIBUTING.md` list the migrations gate, and `ARCHITECTURE.md`'s project tree the new
  `migrations/` test directory
- `README.md`, `ARCHITECTURE.md`, the match and competitor controller OpenAPI text and the service Javadoc describe
  the changes
- `documentation/roadmap/improvement-plan.md` and `improvement-plan-tasks.md` add and close Gaps #48, #49 and #50

### Fixed

Nothing is fixed.

### Removed

Nothing is removed.

---

## 🚀 Migration Guide

No action is needed: no existing endpoint, request or response loses or changes a field, and there is no schema
change. A file that still has a `MatchId`, `CompetitorId` or `MatchCompetitorId` column imports as before, as unknown
columns are ignored.

Two behaviours differ. A bulk competitor row whose club number already exists now updates and verifies that
competitor, where it failed on the unique club number before. A failed row of the shooter log bulk create now also
carries empty `shooterLogId`, `addMatchIds` and `removeMatchIds` values in its `shooterLogRow`.

To change existing shooter logs, post `text/plain` CSV with a header row to `PUT` or `PATCH /ipsc/shooter-logs/bulk`:

```
ShooterLogId,StartDate,EndDate,MatchIds,AddMatchIds,RemoveMatchIds
0,yyyy-MM-dd,yyyy-MM-dd,0;0,0;0,0;0
```

The `PUT` reads `ShooterLogId`, `StartDate`, `EndDate` and `MatchIds` and ignores the other two columns; the `PATCH`
reads all of them and leaves any empty column unchanged. A date must be `yyyy-MM-dd`; a date in another format makes
the whole file a `400`, while a missing log ID, an unknown log or match, or a match outside the dates fails only its
own row. Check each result's `success` rather than the status alone, since a `200` can still carry failed rows.

---

## 📊 Statistics

- **Files Changed:** 41 against `main` before this release's history and release notes; 1,921 insertions and 99
  deletions
- **New Source Files:** 3 (`CompetitorBulkResponse`, `ShooterLogBulkRequest` and `ShooterLogBulkRequestCsvMixIn`),
  plus two test classes
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0, with one pin removed

---

## 🧭 Design Notes

- **Every bulk row goes through the single-record path.** `updateShooterLogs` and `patchShooterLogs` call
  `updateShooterLog` and `patchShooterLog` for each row, so the date, match and within-the-dates rules are written
  once and a row cannot be accepted that a single request would refuse.
- **Add and remove sit beside replace.** `matchIds` keeps meaning "this is the list", so existing callers are
  unaffected, and `addMatchIds` and `removeMatchIds` are the optional way to change it without restating it.
- **The club number is the only duplicate check.** It is the one unique identifier, so a competitor row matches on it
  alone; a name or an ID number can be shared, and treating either as the same person would merge people.
- **A warning, not a refusal.** A match already out of step with its log stays readable and editable, so it can be
  fixed, and an unrelated edit is not blocked; only a move to another day outside the dates is refused.
- **Migrations are checked on MySQL, not H2.** The `test` profile bypasses Flyway and builds the schema on H2, so
  `FlywayMysqlMigrationTest` runs the real migrations against MySQL 8.4 in CI only, switched on by `FLYWAY_MYSQL_URL`,
  which keeps the normal build free of a MySQL requirement.
- **A MINOR.** The new endpoints and optional fields are externally visible, backward-compatible additions, which
  `AGENTS.md`'s Semantic Versioning rules classify as MINOR; nothing existing is removed or changes shape.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` passes: 1,569 tests with no failures or errors, 3 of them skipped because
  `FlywayMysqlMigrationTest` runs only where `FLYWAY_MYSQL_URL` is set.
- New tests cover the bulk update mix-in's column binding, `ShooterLogPatchRequest.validate`, the `PUT` and `PATCH`
  bulk controller statuses, `updateShooterLogs` and `patchShooterLogs` with valid and invalid rows, adding and
  removing matches, the competitor update by club number, and the match `warnings` on read and on a same-day edit, in
  Mockito unit tests and `@SpringBootTest` integration tests.
- `FlywayMysqlMigrationTest` applies all 38 migrations to a MySQL 8.4 database on `flyway-core` and `flyway-mysql`
  `12.4.0` and validates their checksums; the `migrations` CI job runs it, and it was also run locally against a
  throwaway `mysql:8.4` container.
- Overall coverage is 98.74% branch and 98.36% line, above the 97% minimum.

---

## 🐛 Known Issues

- A bulk competitor row without a club number always creates a competitor, so re-importing a file of such rows, as
  every row for a home club other than HPSC's is, creates them again.
- `pom.xml` still overrides `logback.version`, the Jackson BOM properties and `mysql-connector-j`, set above Spring
  Boot's managed versions.

---

## 🔮 Future Enhancements

- Drop the remaining dependency pins once Spring Boot manages versions at least as new.
- Consider a safer key than the club number for matching non-HPSC competitor rows, if re-importing them proves a
  problem.

---

## 👥 Contributors

- Leoni Lubbinge

---

## 📝 Notes

Version 16.4.0 lets the bulk imports change what they created, and has a match say when it has fallen out of step with
its shooter log. It leaves the improvement plan with all 50 gaps closed and none open.

---
