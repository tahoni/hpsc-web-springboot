## 🎯 Summary

- Every verified competitor with points in a match is now ranked against the whole match, whatever their firearm type,
  division or competitor category, in a new `match_result` table
- Match results are calculated and read through new `/ipsc/match-results` endpoints, and recalculated only when they
  are out of date
- A MINOR release: the new endpoints only add to the API, and no existing contract is changed or removed
- `h2.version` is pinned above the version Spring Boot manages, which has a known vulnerability

## 📦 Key Changes

- **Added:** `V11_14_0` and the `match_result` table; `MatchResult`; `IpscMatchResultController` with
  `POST /ipsc/match-results/matches/{matchId}`, `GET /ipsc/match-results/matches/{matchId}` and
  `GET /ipsc/match-results`; `IpscMatchResultService`, `MatchResultRepository` and
  `TransactionService.replaceMatchResults`; the match result response classes; new tests for the calculation, ranking,
  the staleness rule, the endpoints and the response mapping
- **Changed:** `logback.version` is raised to `1.6.7` and `h2.version` pinned to `2.5.252`; `ARCHITECTURE.md`,
  `README.md` and `flyway-migration-versioning.md` describe `MatchResult`, its endpoints and the migration; the archived
  changelogs have link definitions for their version headings
- **Documentation:** `CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md` and `RELEASE_NOTES.md` cover 18.1.0;
  improvement plan Gap #56 is added and closed
- **Build:** project version bumped to 18.1.0 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw clean verify -Pcoverage` passes: 1,537 tests, no failures or errors, 3 skipped unless `FLYWAY_MYSQL_URL`
  is set, meeting the 97% line and branch minimums at 98.7% line and 99.9% branch coverage
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[18.1.0]`
- [ ] Start the application, calculate a match's results and confirm each verified competitor with points has a rank
  and percentage, then read them back from `GET /ipsc/match-results/matches/{matchId}` and `GET /ipsc/match-results`
- [ ] Run the migrations against MySQL, with `FLYWAY_MYSQL_URL` set, and confirm `V11_14_0` applies

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
