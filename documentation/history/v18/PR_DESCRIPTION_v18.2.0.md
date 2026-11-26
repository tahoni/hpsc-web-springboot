## 🎯 Summary

- Shooter results are now ranked within their firearm type and division, and shooter log overall results within their
  division, whatever the competitor category, which is still stored on every row
- A MINOR release: no request or response field is added, removed or changed, but the startup recalculation and the
  `409 Conflict` its lock adds to the two calculation endpoints are externally visible additions
- `tomcat.version` is pinned above the version Spring Boot manages, which has a known vulnerability
- A migration marks every stored result for recalculation, and a startup runner recalculates every marked match and
  shooter log in the background, so existing data is ranked again without a request or a slower start

## 📦 Key Changes

- **Added:** `V11_15_0`, which clears `date_calculated` on `shooter_result` and `shooter_log_overall`, and
  `StartupRecalculationRunner`, which recalculates every match and shooter log with such a row in the background at
  startup, and `StartupRecalculationStatus` with `RecalculationInProgressException`, which make the two calculation
  `POST`s answer `409 Conflict` while it runs
- **Changed:** `IpscShooterResultServiceImpl` groups by firearm type and division and `IpscShooterLogOverallServiceImpl`
  by division, instead of also by competitor category; the Javadoc and OpenAPI text of both services and controllers
  say so; `pom.xml` pins `tomcat.version` to `11.0.29`
- **Tests:** the shooter result category test expects one ranking, and a new overall test does the same
- **Documentation:** `ARCHITECTURE.md` and `README.md` stop saying results are ranked per competitor category;
  `CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md` and `RELEASE_NOTES.md` cover 18.2.0; improvement plan Gap #57 is
  added and closed
- **Build:** project version bumped to 18.2.0 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw clean verify -Pcoverage` passes: 1,558 tests, no failures or errors, 3 skipped unless `FLYWAY_MYSQL_URL`
  is set, meeting the 97% line and branch minimums at 98.6% line and 99.9% branch coverage
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[18.2.0]`
- [x] Run the migrations against MySQL, with `FLYWAY_MYSQL_URL` set, and confirm `V11_15_0` applies
- [ ] While the startup recalculation runs, `POST` a calculation and confirm it answers `409`, then confirm it succeeds
  once the log reports the recalculation finished
- [ ] Start the application against data calculated before this release and confirm the log reports the recalculation
  and the old ranks are gone
- [ ] Start the application, calculate a match with competitors in different categories in one division and confirm
  they share one ranking in `GET /ipsc/shooter-results/matches/{matchId}`, then calculate a shooter log's overall
  results and confirm the same

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
