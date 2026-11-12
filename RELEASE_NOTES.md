# Release Notes – Version 17.0.0

**Release Date:** November 12, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Reporting Failed Rows in the Competitor and Match Imports**

Version 17.0.0 is a major release. The bulk competitor import and the bulk match import used to save every row in one
transaction, so a single bad row failed the whole request with a `400` or `404` and left nothing saved. They now work
as the match competitor and shooter log imports already did: each row is saved in its own transaction, and a row that
cannot be imported is reported, with its row number, its reason and the values it supplied, while the other rows are
still imported. Because that changes what the match import answers, what a bad row does to a request and the name of a
response field, it is a breaking release.

---

## ⭐ Key Highlights

### 🛡️ A Bad Row No Longer Hides the Good Ones

- A competitor row that is missing a required field, has an unrecognised gender, names an unknown home club or lacks a
  required club number is reported with `success` `false` and skipped
- A match row that is missing a required field, has an unrecognised firearm type or category or names an unknown club is
  reported and skipped
- Each row is saved in its own transaction, so the other rows are still imported
- A CSV that is blank or cannot be parsed still fails the request

### 🔢 A Result for Every Row

- `MatchBulkResponseHolder.results` holds a `MatchBulkResponse` for every match row: its `rowNumber`, `success`,
  `message`, the created `match` and, for a failed row, a `matchRow` with every value the row supplied
- `CompetitorBulkResponse` gains `rowNumber`, `success` and a `competitorRow`, and the match competitor and shooter log
  bulk responses gain `rowNumber`, so every bulk row says which line of the file it came from
- A competitor row that repeats a club number an earlier row of the same file already imported is reported as failed
  instead of silently updating the competitor that row just saved

### ♻️ Tidier Code

- `MatchResponse.warnings` is renamed `messages`
- `TransactionService.saveMatches`, `saveMatchCompetitors` and `saveShooterResults` are removed, as nothing called them
- Branches that could never run are removed from the match competitor mapper and the match competitor and shooter log
  services

---

## 📦 What's New

### Added

- **`CompetitorBulkResponse.rowNumber`, `success` and `competitorRow`:** Each row of the bulk competitor import reports
  its row number, whether it was created or updated and, for a failed row, a `CompetitorRow` holding every value it
  supplied, with the email addresses in one value separated by `;`.
- **`MatchBulkResponse` and `MatchRow`:** New per-row result and failed-row types for the bulk match import.
- **`MatchCompetitorBulkResponse.rowNumber` and `ShooterLogBulkResponse.rowNumber`:** Each row of the bulk match
  competitor import and of the bulk shooter log create, replace and update imports carries its `rowNumber`.
- **Tests:** New `CompetitorRowTest`, `MatchRowTest`, `MatchResponseTest`, `ShooterLogResponseTest` and
  `ShooterResultResponseTest`, and new service, mapper and impl cases, bringing line coverage to 99.2% and branch
  coverage to 100%.

### Changed

- **Breaking: `MatchResponse.warnings` renamed to `messages`:** The list keeps its behaviour, left out of the JSON when
  empty, but is serialised as `messages`.
- **Breaking: bulk match import reports failed rows:** `MatchResponseHolder` is renamed `MatchBulkResponseHolder` and
  carries a `results` list; a bad row is reported and skipped instead of failing the request.
- **Breaking: bulk competitor import reports failed rows:** A bad row is reported with `success` `false` and skipped
  instead of failing the request with a `400` or `404`.
- **`CompetitorResponseHolder`:** Renamed `CompetitorBulkResponseHolder`, and so is its OpenAPI schema name; the JSON it
  serialises to is unchanged.
- **`IpscCompetitorServiceImpl.createCompetitors`:** A row that repeats a club number an earlier row of the same file
  already imported is reported as failed. A club number that matches a competitor already in the database still updates
  and verifies it.
- **`MatchCompetitorMapper.resolveFirearmType`, `IpscMatchCompetitorServiceImpl` and `IpscShooterLogServiceImpl`:**
  Branches that could never run are removed, with no change in behaviour.
- **`ARCHITECTURE.md`:** The competitor and match bulk CSV import flows say each row is saved in its own transaction.
  Closes improvement plan Gap #51.
- **Archived versions 14.0.0 – 16.4.1:** Their `CHANGELOG`, `HISTORY` and `EVOLUTION_OVERVIEW` entries move, unchanged,
  into `documentation/archive/v14-v16/`, with the v14 – v16 release notes and pull request descriptions from
  `documentation/history/`, which now starts at v17.0.0; `README.md`, `AGENTS.md` and the `prep-version-release`
  skill follow the new locations.

### Fixed

Nothing is fixed.

### Removed

- **`TransactionService.saveMatches`, `saveMatchCompetitors` and `saveShooterResults`:** Removed with their tests, as no
  production code called them. Closes improvement plan Gap #52.

---

## 🚀 Migration Guide

No configuration property, database schema or endpoint path changes. Clients of the bulk imports need to adapt:

1. **Read `messages` instead of `warnings`.** Any client that reads a match's `warnings` from a read, edit or shooter
   log response must read `messages`.
2. **Check each row of a bulk match or competitor import.** A bad row no longer fails the request with a `400` or
   `404`: read each result's `success` and, when it is `false`, its `message` and `matchRow` or `competitorRow`. A
   blank or unparseable CSV still answers `400`.
3. **Read the match import's `results`.** The bulk match import answers with a `MatchBulkResponseHolder` whose `results`
   lists every row; the `MatchResponseHolder` type is gone.
4. **Regenerate any client built from the OpenAPI document.** `MatchResponseHolder` is now `MatchBulkResponseHolder`
   and `CompetitorResponseHolder` is now `CompetitorBulkResponseHolder`.

---

## 📊 Statistics

- **Files Changed:** 53 against `main` before this release's history and release notes; 1,654 insertions and 447
  deletions
- **New Source Files:** 4 (`CompetitorRow`, `MatchBulkResponse`, `MatchBulkResponseHolder`, `MatchRow`)
- **Renamed Source Files:** 1 (`CompetitorResponseHolder` to `CompetitorBulkResponseHolder`)
- **Deleted Source Files:** 1 (`MatchResponseHolder`)
- **New Dependencies:** 0
- **Archived Documentation:** 20 release notes and pull request descriptions moved, and 3 archive files created

---

## 🧭 Design Notes

- **One import behaviour everywhere.** Match competitors and shooter logs already reported failed rows and carried on;
  competitors and matches now do too, so a client handles every bulk import the same way.
- **A failed row reports what it was given.** `CompetitorRow` and `MatchRow` hold the values as text, so a row that
  failed to parse can still be shown back to the person who supplied it.
- **A file is not a database.** A repeated club number within one file is reported rather than treated as an update,
  since the second row would otherwise overwrite what the first just saved.
- **A MAJOR.** The renamed `messages` field, the new match import response and a bad row no longer failing the request
  are backward-incompatible, which `AGENTS.md`'s Semantic Versioning rules classify as MAJOR.

---

## 🧪 Testing

- `./mvnw test` passes: 1,601 tests with no failures or errors, 3 of them skipped because `FlywayMysqlMigrationTest`
  runs only where `FLYWAY_MYSQL_URL` is set.
- New tests cover the row classes, the `messages` field's JSON, failed rows, row numbers, a repeated club number, data
  integrity conflicts, CSV read failures and the shooter log bulk patch.

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

Version 17.0.0 gives the bulk competitor and match imports the per-row, report-and-continue behaviour of the other bulk
imports. It leaves the improvement plan with all 52 gaps closed and none open.
