# Release Notes – Version 18.2.0

**Release Date:** November 26, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Ranking Competitors Within Their Division**

Version 18.2.0 is a minor release. Shooter results ranked a competitor only against the others in their firearm type,
division and competitor category, and a shooter log's overall results only against those in their division and
category, so a junior and a senior in the same division never appeared in one ranking. Shooter results are now ranked
within a firearm type and division, and overall results within a division, whatever the competitor category. The
category is still stored on every row, a new migration marks every stored result as not yet calculated, and the
application recalculates those results in the background when it starts, so existing data is ranked again without a
request. The release also pins `tomcat.version` above the version Spring Boot manages, after that version was found to
have a known vulnerability. No request field or response field changes, but the calculation endpoints can now answer
`409 Conflict` while the recalculation runs, an externally visible addition, so it is a minor release.

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
- No row is deleted, so the old ranks are still returned until each is recalculated

### 🚀 Recalculated When the Service Starts

- `StartupRecalculationRunner` finds every match with a shooter result, and every shooter log with an overall result,
  whose `date_calculated` is `null`, and calculates the matches first and then the logs
- It runs in a background thread, so startup is not delayed and the application serves requests while it works
- A match or log that fails is logged and skipped, so one bad record neither stops the others nor prevents startup, and
  it is tried again on the next start
- While it works, `POST /ipsc/shooter-results/matches/{matchId}` and
  `POST /ipsc/shooter-log-overall-results/logs/{shooterLogId}` answer `409 Conflict`, so a request never writes the same
  results as the runner; the runner's own thread is exempt, and the request can be repeated once it has finished

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
- **`StartupRecalculationStatus` and `RecalculationInProgressException`:** The status records whether the startup
  recalculation is running, and the shooter result and shooter log overall calculations throw the exception, which
  `ControllerAdvice` maps to `409 Conflict`, while it is. The `409` is in the OpenAPI description of both `POST`
  endpoints.
- **`StartupRecalculationRunner`:** Calculates, in a background thread when the application starts, the shooter results
  of every match, and then the overall results of every shooter log, that has a row whose `date_calculated` is `null`,
  using `ShooterResultRepository.findMatchIdsWithoutDateCalculated` and
  `ShooterLogOverallRepository.findShooterLogIdsWithoutDateCalculated`.

### Changed

- **Shooter results and shooter log overall results:** Shooter results are ranked within their firearm type and
  division, and overall results within their division, instead of also within their competitor category. Competitors
  who were ranked in separate category groups now share one ranking, so their ranks and percentages change when
  their match or shooter log is next calculated.
- **Javadoc and OpenAPI text:** `IpscShooterResultService`, `IpscShooterLogOverallService`, their implementations and
  controllers describe the new ranking.
- **`pom.xml`:** `tomcat.version` is pinned to `11.0.29`, overriding the `11.0.27` Spring Boot manages, which has a
  known security vulnerability.
- **`StartupRecalculationRunner.run`:** The `ApplicationArguments` parameter is annotated `@NonNull` (JSpecify), as the
  `ApplicationRunner` contract requires; behaviour is unchanged.
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

No endpoint, request field or response field changes, and nothing is removed. Deployments and clients can adopt the
release without changes, apart from retrying a refused calculation:

1. **Let Flyway apply `V11_15_0`, then let the service start.** The migration runs at startup and clears
   `date_calculated` on every `shooter_result` and `shooter_log_overall` row, and `StartupRecalculationRunner` then
   calculates every match and shooter log with such a row in a background thread. Startup is not delayed, but a match
   or shooter log returns its old per-category ranks until it has been recalculated; the log reports how many are being
   recalculated and names any that could not be, which are tried again on the next start.
   A calculation requested while it runs, with `POST /ipsc/shooter-results/matches/{matchId}` or
   `POST /ipsc/shooter-log-overall-results/logs/{shooterLogId}`, is refused with `409 Conflict`, so a client should try
   again shortly.
2. **Expect shared rankings.** A client that showed a separate league table per competitor category should group by
   `competitorCategory` itself, which each response still carries.

---

## 📊 Statistics

- **Files Changed:** 28 against `main` before this release's history and release notes; 853 insertions and 49
  deletions
- **New Source Files:** 4 (`V11_15_0__mark_shooter_results_for_recalculation.sql`, `StartupRecalculationRunner`,
  `StartupRecalculationStatus` and `RecalculationInProgressException`)
- **New Test Files:** 3 (`StartupRecalculationRunnerTest`, `StartupRecalculationStatusTest` and
  `ShooterResultRepositoryIntegrationTest`)
- **Deleted Test Files:** 0
- **New Dependencies:** 0
- **Improvement Plan:** Gap #57 added and closed, leaving all 57 closed

---

## 🧭 Design Notes

- **Rank within the division.** One ranking per division, with the category kept as a label, answers who won the
  division, where a ranking per category could not.
- **Keep the category stored.** `competitor_category` is NOT NULL on `shooter_result` and `shooter_log_overall` and is
  still returned, so a client can group by it without a schema change or migration.
- **Refuse, don't wait.** A calculation requested during the recalculation fails at once with a `409`, since waiting
  could hold a request for as long as a large database takes, and the data it would write is about to be written anyway.
  The runner's thread is the one exempt, so the same services do the work either way.
- **Refresh what the migration marks, off the startup path.** The migration only marks rows, and the runner finds them
  by that mark, so a failed recalculation leaves its rows marked and is retried on the next start without any extra
  state.
- **Pin what is vulnerable, and say why.** `tomcat.version` is overridden because the managed version has a known
  vulnerability, and the pin stays until the Spring Boot parent manages a fixed release.
- **A MINOR.** The release changes no request or response field, but it adds a startup recalculation and a `409`
  response the calculation endpoints did not have, which `AGENTS.md`'s Semantic Versioning rules classify as MINOR
  since they are externally visible additions; the rank values clients see can also differ.

---

## 🧪 Testing

- `./mvnw test` passes: 1,558 tests with no failures or errors, 3 of them skipped because `FlywayMysqlMigrationTest`
  runs only where `FLYWAY_MYSQL_URL` is set; `./mvnw clean verify -Pcoverage` meets the 97% line and branch minimums, at
  98.6% line and 99.9% branch coverage.
- The shooter result test for several categories now expects one ranking, and a new overall test covers competitors in
  different categories.
- New tests cover the startup runner, including a match or log that fails, a failing query and the background thread,
  the status that refuses calculations while it runs, the `409` mapping and both services' refusal, and the two
  queries it uses.

---

## 🐛 Known Issues

- A bulk competitor row without a club number always creates a competitor, so re-importing a file of such rows, as
  every row for a home club other than HPSC's is, creates them again.
- A match or shooter log that cannot be recalculated at startup keeps its old per-category ranks, with a warning in the
  log, until it is recalculated by a later start or a calculation request.
- The recalculation runs in the background, so reads made while it works can still return old per-category ranks, and
  the two calculation `POST`s are refused with `409 Conflict` until it finishes, which on a large database takes a
  while; stopping the application during it leaves the rest marked, to be recalculated on the next start.
- `pom.xml` still overrides `tomcat.version`, `logback.version`, `h2.version`, the Jackson BOM properties and
  `mysql-connector-j`, set above Spring Boot's managed versions.

---

## 🔮 Future Enhancements

- Drop the remaining dependency pins once Spring Boot manages versions at least as new.
- Consider reporting the recalculation's progress, such as through a health indicator, so a deployment can tell when it
  has finished.
- Consider a safer key than the club number for matching non-HPSC competitor rows, if re-importing them proves a
  problem.

---

## 👥 Contributors

- Leoni Lubbinge

---

## 📝 Notes

Version 18.2.0 ranks shooter results within a firearm type and division, and shooter log overall results within a
division, whatever the competitor category, marks the stored results for recalculation and recalculates them at startup,
and pins `tomcat.version` above a vulnerable managed version. It leaves the improvement plan with all 57 gaps closed
and none open.
