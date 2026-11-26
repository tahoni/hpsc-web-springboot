# Release Notes – Version 18.1.1

**Release Date:** November 26, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Ranking Competitors Within Their Division**

Version 18.1.1 is a patch release. Shooter results ranked a competitor only against the others in their firearm type,
division and competitor category, and a shooter log's overall results only against those in their division and
category, so a junior and a senior in the same division never appeared in one ranking. Shooter results are now ranked
within a firearm type and division, and overall results within a division, whatever the competitor category. The
category is still stored on every row, and a new migration marks every stored result as not yet calculated, so each
match and shooter log is ranked again the next time it is calculated. The release also pins `tomcat.version` above the
version Spring Boot manages, after that version was found to have a known vulnerability. No endpoint, request field or
response field changes, so it is a patch release.

---

## ⭐ Key Highlights

### 🏁 One Ranking per Division

- `IpscShooterResultServiceImpl` groups a match's verified competitors by firearm type and division, where it also
  grouped them by competitor category
- `IpscShooterLogOverallServiceImpl` groups a shooter log's overall results by division, where it also grouped them by
  competitor category
- Each competitor's `competitorCategory` is still saved and returned; it no longer affects `rankInLog` or
  `percentageInLog`

### 🔄 Existing Results Marked for Recalculation

- `V11_15_0__mark_shooter_results_for_recalculation.sql` clears `date_calculated` on `shooter_result` and
  `shooter_log_overall`, which the services treat as out of date, so the next calculation of a match or shooter log
  ranks it again instead of returning its old results as they are
- No row is deleted, so the old ranks are still returned until then

### 🛡️ A Safer Tomcat

- `tomcat.version` is pinned to `11.0.29`, since the `11.0.27` Spring Boot manages has a known vulnerability, and the
  pin stays until the parent manages a fixed release

### 📚 Documentation That Keeps Up

- The Javadoc and OpenAPI text of both services and controllers, `ARCHITECTURE.md` and `README.md` no longer say
  results are ranked per competitor category
- Improvement plan Gap #57 is added and closed, leaving all 57 gaps closed

---

## 📦 What's New

### Added

- **`V11_15_0__mark_shooter_results_for_recalculation.sql`:** Clears `date_calculated` on `shooter_result` and
  `shooter_log_overall`, so each match and shooter log is ranked again, without competitor category, the next time it
  is calculated.

### Changed

- **Shooter results and shooter log overall results:** Shooter results are ranked within their firearm type and
  division, and overall results within their division, instead of also within their competitor category. Competitors
  who were ranked in separate category groups now share one ranking, so their ranks and percentages change when
  their match or shooter log is next calculated.
- **Javadoc and OpenAPI text:** `IpscShooterResultService`, `IpscShooterLogOverallService`, their implementations and
  controllers describe the new ranking.
- **`pom.xml`:** `tomcat.version` is pinned to `11.0.29`, overriding the `11.0.27` Spring Boot manages, which has a
  known security vulnerability.
- **`ARCHITECTURE.md` and `README.md`:** Say results are ranked per firearm type and division, and overall results per
  division, whatever the competitor category. Closes improvement plan Gap #57.
- **Tests:** `IpscShooterResultServiceTest` expects competitors in different categories to be ranked together, and
  `IpscShooterLogOverallServiceTest` has a new test that does the same for overall results.

### Fixed

Nothing is fixed.

### Removed

Nothing is removed.

---

## 🚀 Migration Guide

No endpoint, request or response contract changes, and nothing is removed. Deployments and clients can adopt the
release without changes:

1. **Let Flyway apply `V11_15_0`.** It runs at startup and clears `date_calculated` on every `shooter_result` and
   `shooter_log_overall` row. The old ranks are still returned until a match's shooter results or a shooter log's
   overall results are calculated again with `POST /ipsc/shooter-results/matches/{matchId}` or
   `POST /ipsc/shooter-log-overall-results/logs/{shooterLogId}`, which then rank them without competitor category.
2. **Expect shared rankings.** A client that showed a separate league table per competitor category should group by
   `competitorCategory` itself, which each response still carries.

---

## 📊 Statistics

- **Files Changed:** 15 against `main` before this release's history and release notes; 141 insertions and 45
  deletions
- **New Source Files:** 1 (`V11_15_0__mark_shooter_results_for_recalculation.sql`)
- **New Test Files:** 0
- **Deleted Test Files:** 0
- **New Dependencies:** 0
- **Improvement Plan:** Gap #57 added and closed, leaving all 57 closed

---

## 🧭 Design Notes

- **Rank within the division.** One ranking per division, with the category kept as a label, answers who won the
  division, where a ranking per category could not.
- **Keep the category stored.** `competitor_category` is NOT NULL on `shooter_result` and `shooter_log_overall` and is
  still returned, so a client can group by it without a schema change or migration.
- **Pin what is vulnerable, and say why.** `tomcat.version` is overridden because the managed version has a known
  vulnerability, and the pin stays until the Spring Boot parent manages a fixed release.
- **A PATCH.** The release adds nothing and changes no contract, which `AGENTS.md`'s Semantic Versioning rules classify
  as PATCH, though the rank values clients see can differ.

---

## 🧪 Testing

- `./mvnw test` passes: 1,538 tests with no failures or errors, 3 of them skipped because `FlywayMysqlMigrationTest`
  runs only where `FLYWAY_MYSQL_URL` is set; `./mvnw clean verify -Pcoverage` meets the 97% line and branch minimums, at
  98.7% line and 99.9% branch coverage.
- The shooter result test for several categories now expects one ranking, and a new overall test covers competitors in
  different categories.

---

## 🐛 Known Issues

- A bulk competitor row without a club number always creates a competitor, so re-importing a file of such rows, as
  every row for a home club other than HPSC's is, creates them again.
- Results calculated before this release keep their per-category ranks until each match or shooter log is calculated
  again, since the migration marks them out of date rather than recalculating them.
- `pom.xml` still overrides `tomcat.version`, `logback.version`, `h2.version`, the Jackson BOM properties and
  `mysql-connector-j`, set above Spring Boot's managed versions.

---

## 🔮 Future Enhancements

- Drop the remaining dependency pins once Spring Boot manages versions at least as new.
- Consider recalculating every match and shooter log when the service starts, so no result keeps an old per-category
  rank until it is next requested.
- Consider a safer key than the club number for matching non-HPSC competitor rows, if re-importing them proves a
  problem.

---

## 👥 Contributors

- Leoni Lubbinge

---

## 📝 Notes

Version 18.1.1 ranks shooter results within a firearm type and division, and shooter log overall results within a
division, whatever the competitor category, marks the stored results for recalculation and pins `tomcat.version` above
a vulnerable managed version. It leaves the improvement plan with all 57 gaps closed and none open.
