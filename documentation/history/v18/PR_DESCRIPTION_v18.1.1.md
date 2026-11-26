## 🎯 Summary

- Shooter results are now ranked within their firearm type and division, and shooter log overall results within their
  division, whatever the competitor category, which is still stored on every row
- A PATCH release: no endpoint, request field or response field is added, removed or changed
- `tomcat.version` is pinned above the version Spring Boot manages, which has a known vulnerability
- Results already calculated keep their old ranks until the match or a competitor is updated after them

## 📦 Key Changes

- **Changed:** `IpscShooterResultServiceImpl` groups by firearm type and division and `IpscShooterLogOverallServiceImpl`
  by division, instead of also by competitor category; the Javadoc and OpenAPI text of both services and controllers
  say so; `pom.xml` pins `tomcat.version` to `11.0.29`
- **Tests:** the shooter result category test expects one ranking, and a new overall test does the same
- **Documentation:** `ARCHITECTURE.md` and `README.md` stop saying results are ranked per competitor category;
  `CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md` and `RELEASE_NOTES.md` cover 18.1.1; improvement plan Gap #57 is
  added and closed
- **Build:** project version bumped to 18.1.1 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw clean verify -Pcoverage` passes: 1,538 tests, no failures or errors, 3 skipped unless `FLYWAY_MYSQL_URL`
  is set, meeting the 97% line and branch minimums at 98.7% line and 99.9% branch coverage
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[18.1.1]`
- [ ] Start the application, calculate a match with competitors in different categories in one division and confirm
  they share one ranking in `GET /ipsc/shooter-results/matches/{matchId}`, then calculate a shooter log's overall
  results and confirm the same

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
