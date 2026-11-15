# Release Notes – Version 18.1.0

**Release Date:** November 15, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Ranking Competitors Across the Whole Match**

Version 18.1.0 is a minor release. Shooter results rank a competitor only against the others in their firearm type,
division and competitor category, so there was no way to say who won a match overall. Match results rank every verified
competitor with points together, whatever their firearm type, division or category, and are calculated and read through
new `/ipsc/match-results` endpoints. The release also pins `h2.version` above the version Spring Boot manages, after
that version was found to have a known vulnerability, and adds the link definitions the archived changelogs were
missing. It only adds to the API, so it is a minor release.

---

## ⭐ Key Highlights

### 🏆 A Match Ranked as a Whole

- `MatchResult`, in the new `match_result` table, holds a verified competitor's rank and percentage in the match
  overall, independent of competitor category and division
- `POST /ipsc/match-results/matches/{matchId}` calculates a match's results, and
  `GET /ipsc/match-results/matches/{matchId}` and `GET /ipsc/match-results` read them back
- Competitors on equal points share a rank and the next rank is skipped; each percentage is the competitor's points as a
  percentage of the match winner's
- Only verified competitors with points above zero are included, as for shooter results

### ♻️ Recalculated Only When Out of Date

- Calculating again updates the existing results instead of adding duplicates, and does nothing while they are current
- Results are out of date once the match, or one of its competitors, was updated at least five minutes after they were
  calculated, a competitor has no result yet, or a result belongs to a competitor who no longer qualifies
- `TransactionService.replaceMatchResults` saves and deletes a match's results in one transaction, so a constraint
  violation surfaces from the call

### 🛡️ A Safer Test Database

- `h2.version` is pinned to `2.5.252`, because the `2.4.240` that Spring Boot manages has a known vulnerability
- `logback.version` is raised from `1.6.5` to `1.6.7`

### 📚 Documentation That Keeps Up

- `ARCHITECTURE.md`, `README.md` and `flyway-migration-versioning.md` describe `MatchResult`, its endpoints and the
  `V11_14_0` migration
- The archived changelogs have link definitions for their version headings, and bracketed text that is not a link is
  escaped, so no link label is left unresolved

---

## 📦 What's New

### Added

- **`V11_14_0__create_match_result.sql`:** Creates the `match_result` table, backing the new `MatchResult` entity, with
  foreign keys to `ipsc_match`, `competitor` and `match_competitor`.
- **`MatchResult`:** A competitor's rank and percentage in the match overall, with the same structure as
  `ShooterResult`.
- **`IpscMatchResultController`:** `POST /ipsc/match-results/matches/{matchId}` calculates `rankInLog` and
  `percentageInLog` for every verified competitor with points in a match; `GET /ipsc/match-results/matches/{matchId}`
  and `GET /ipsc/match-results` read them back.
- **`IpscMatchResultService`, `MatchResultRepository` and `TransactionService.replaceMatchResults`:** The service,
  repository and write that sit behind the controller.
- **`MatchResultResponse`, `MatchResultMatchResponse` and `MatchResultResponseHolder`:** The response classes, linking a
  match to its results.
- **Tests:** `IpscMatchResultServiceTest`, `IpscMatchResultServiceIntegrationTest`, `IpscMatchResultControllerTest` and
  `MatchResultResponseTest`.

### Changed

- **`pom.xml`:** `logback.version` is raised from `1.6.5` to `1.6.7`, and `h2.version` is pinned to `2.5.252`, since the
  `2.4.240` Spring Boot manages has a known vulnerability.
- **`ARCHITECTURE.md`, `README.md` and `flyway-migration-versioning.md`:** Describe `MatchResult`, the controller and
  service, the match result response models and the `V11_14_0` migration, which they left out. Closes improvement plan
  Gap #56.
- **Archived changelogs and release notes:** Version headings in `CHANGELOG_v1-v4.md`, `CHANGELOG_v5-v7.md`,
  `CHANGELOG_v8-v9.md` and `CHANGELOG_v10-v13.md` have link definitions, and bracketed text that is not a link is
  escaped.

### Fixed

Nothing is fixed.

### Removed

Nothing is removed.

---

## 🚀 Migration Guide

No endpoint, request or response contract changes, and nothing is removed. Deployments and clients can adopt the
release without changes:

1. **Let Flyway apply `V11_14_0`.** It runs at startup and creates the `match_result` table; nothing else in the schema
   changes.
2. **Calculate a match's results to see them.** `GET /ipsc/match-results` returns every match with no results until
   `POST /ipsc/match-results/matches/{matchId}` has been called for it.

---

## 📊 Statistics

- **Files Changed:** 41 against `main` before this release's history and release notes; 2,076 insertions and 40
  deletions
- **New Source Files:** 9 (`V11_14_0__create_match_result.sql`, `MatchResult`, `IpscMatchResultController`,
  `IpscMatchResultService`, `IpscMatchResultServiceImpl`, `MatchResultRepository` and the three response classes)
- **New Test Files:** 4 (`IpscMatchResultServiceTest`, `IpscMatchResultServiceIntegrationTest`,
  `IpscMatchResultControllerTest` and `MatchResultResponseTest`)
- **Deleted Test Files:** 0
- **New Dependencies:** 0
- **Improvement Plan:** Gap #56 added and closed, leaving all 56 closed

---

## 🧭 Design Notes

- **Rank the whole match once.** A match result ranks every verified competitor with points together, so a client gets
  one league table without merging the per-category results of shooter results.
- **Reuse the shooter result shape.** `MatchResult` has the same structure as `ShooterResult`, so the two are read and
  mapped the same way and differ only in what they rank against.
- **Recalculate only what is stale.** A result is rewritten only when the match or a competitor changed after it was
  calculated, so a repeated request is cheap and the calculation date stays meaningful.
- **Pin what is vulnerable, and say why.** `h2.version` is overridden because the managed version has a known
  vulnerability, and the pin stays until the Spring Boot parent manages a fixed release.
- **A MINOR.** The release adds an endpoint and a table and changes no existing contract, which `AGENTS.md`'s Semantic
  Versioning rules classify as MINOR.

---

## 🧪 Testing

- `./mvnw test` passes: 1,537 tests with no failures or errors, 3 of them skipped because `FlywayMysqlMigrationTest`
  runs only where `FLYWAY_MYSQL_URL` is set; `./mvnw verify -Pcoverage` meets the 97% line and branch minimums, at 98.7%
  line and 99.9% branch coverage.
- New tests cover the calculation, ranking and percentages, the five-minute staleness rule, retrieval, the controller
  endpoints and the response mapping.

---

## 🐛 Known Issues

- A bulk competitor row without a club number always creates a competitor, so re-importing a file of such rows, as
  every row for a home club other than HPSC's is, creates them again.
- `pom.xml` still overrides `logback.version`, `h2.version`, the Jackson BOM properties and `mysql-connector-j`, set
  above Spring Boot's managed versions.

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

Version 18.1.0 ranks every verified competitor across the whole match through new match result endpoints, and pins
`h2.version` above a vulnerable managed version. It leaves the improvement plan with all 56 gaps closed and none open.
