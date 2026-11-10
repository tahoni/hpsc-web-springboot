# Release Notes – Version 16.3.0

**Release Date:** November 10, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Shooter Log Bulk CSV Import**

Version 16.3.0 is a minor release. Competitors, matches and match competitors can each be loaded from CSV, with every
row reported on its own, but a shooter log could only be created one request at a time. A new endpoint creates shooter
logs from CSV, one log per row, and checks each row exactly as a single log is checked. A row that fails is reported
with the values it supplied and the reason, and the rest are still created. Nothing existing changes shape, so the
release is backward-compatible.

---

## ⭐ Key Highlights

### 📥 Bulk Import

- `POST /ipsc/shooter-logs/bulk` creates shooter logs from `text/plain` CSV data, one log per row, with `StartDate`,
  `EndDate` and `MatchIds` columns; the match IDs in a row are separated by semicolons
- Each row is created through `createShooterLog` in its own transaction, so a row needs both dates, an end date not
  before the start date, and existing matches that are all scheduled within the row's dates
- The endpoint answers `201` if any row was created, or the CSV had no rows, and `422` if every row failed; blank or
  unreadable CSV is a `400`

### 🧾 Per-Row Reporting

- A failed row comes back as a `ShooterLogBulkResponse` with `success` set to `false`, the reason in `message` and a
  `ShooterLogRow` holding every value the row supplied, as text
- A row without a start or end date is reported on its own instead of failing the file, because
  `ShooterLogRequestCsvMixIn` marks no column as required

### 🛡️ Null Match IDs

- `ShooterLogRequest.validate` rejects a null match ID with a `ValidationException`, a `400`; before, it reached the
  repository and failed with a `500`

### 📖 Documented Import

- `README.md` and `ARCHITECTURE.md` describe the import, and `ARCHITECTURE.md` gains a Shooter Log Bulk CSV Import Flow
  diagram
- The Javadoc on `IpscShooterLogService` and the OpenAPI description on `IpscShooterLogController` cover the columns,
  the per-row behaviour and the `201`, `400`, `422` and `500` responses

---

## 📦 What's New

### Added

- `IpscShooterLogController.createShooterLogs`, serving `POST /ipsc/shooter-logs/bulk`
- `IpscShooterLogService.createShooterLogs` and its implementation, with `readShooterLogs` parsing the CSV
- `ShooterLogRequestCsvMixIn`, binding the CSV column names onto `ShooterLogRequest`
- `ShooterLogBulkResponse`, `ShooterLogBulkResponseHolder` and `ShooterLogRow`, with `ShooterLogRowTest` and
  `ShooterLogRequestCsvMixInTest`
- Controller, service and `@SpringBootTest` integration tests for the import

### Changed

- `ShooterLogRequest.validate` rejects a null match ID with a `ValidationException`
- `IpscCompetitorService` and `IpscShooterLogServiceImpl` drop an unused import each, with no change in behaviour
- `README.md`, `ARCHITECTURE.md`, `IpscShooterLogService` and `IpscShooterLogController` describe the import
- `documentation/roadmap/improvement-plan.md` and `improvement-plan-tasks.md` add and close Gap #47

### Fixed

Nothing is fixed.

### Removed

Nothing is removed.

---

## 🚀 Migration Guide

No action is needed: no existing endpoint, request or response changes shape, and there is no schema change.

To use the import, post `text/plain` CSV with a header row to `POST /ipsc/shooter-logs/bulk`:

```
StartDate,EndDate,MatchIds
yyyy-MM-dd,yyyy-MM-dd,0;0
```

A date must be `yyyy-MM-dd`; a date in another format makes the whole file a `400`, while a missing date or an unknown
match fails only its own row. Check each result's `success` rather than the status alone, since a `201` can still carry
failed rows.

---

## 📊 Statistics

- **Files Changed:** 18 against `main` before this release's documentation; 815 insertions and 13 deletions
- **New Source Files:** 4 (`ShooterLogRequestCsvMixIn`, `ShooterLogBulkResponse`, `ShooterLogBulkResponseHolder` and
  `ShooterLogRow`), plus two test classes
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **Every row goes through the single-log path.** `createShooterLogs` calls `createShooterLog` for each row, so the
  date, match and within-the-dates rules are written once and a row cannot be accepted that a single request would
  refuse.
- **One bad row does not spoil the file.** Each row is saved in its own transaction, and a failed row is reported and
  skipped, as the competitor, match and match competitor imports treat theirs.
- **Nothing is required at the column level.** The mix-in marks no column required, so a missing date is a row failure
  with its values shown, not a parse error that rejects every row.
- **`422` only when nothing was created.** A file where some rows succeed is a `201`, so the caller reads each row's
  result for the failures.
- **A MINOR.** The new endpoint is an externally visible, backward-compatible addition, which `AGENTS.md`'s Semantic
  Versioning rules classify as MINOR; the stricter match ID check only turns a `500` into a `400`.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` passes: 1,524 tests with no failures, errors or skips.
- New tests cover `ShooterLogRequestCsvMixIn`'s column binding, `ShooterLogRow`, the null match ID check,
  `IpscShooterLogController.createShooterLogs` returning `201` and `422`, and `IpscShooterLogService.createShooterLogs`
  creating good rows, reporting a missing date, an unknown match and a match outside the dates, and rejecting blank or
  unparseable CSV, in Mockito unit tests and `@SpringBootTest` integration tests.
- Overall coverage is 99.15% branch and 98.63% line, above the 97% minimum.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports,
  is read but never used as the row's own ID, since the imports only create records.
- The bulk competitor import only creates competitors, so it cannot verify one that already exists; use
  `POST /ipsc/competitors/verify` afterwards.
- The shooter log import only creates logs; there is no way to add matches to, or update, an existing log in bulk.
- `pom.xml` still overrides `logback.version`, the Jackson BOM properties, `mysql-connector-j` and `flyway-mysql`, set
  above Spring Boot's managed versions.

---

## 🔮 Future Enhancements

- Consider updating existing shooter logs, or adding matches to them, from CSV if loading a season by hand proves slow.
- Drop the remaining dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge

---

## 📝 Notes

Version 16.3.0 gives shooter logs the report-and-continue CSV import that competitors, matches and match competitors
already had. It leaves the improvement plan with all 47 gaps closed and none open.

---
