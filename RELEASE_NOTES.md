# Release Notes – Version 15.0.0

**Release Date:** October 17, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Shooter Result Calculation & Reading**

Version 15.0.0 is a major release. A match's shooter results — each competitor's rank and percentage within their
firearm type, division and competitor category — are now calculated, stored in a new `ShooterResult` entity and read
through new endpoints. The overall and club rankings on a match competitor are removed, and the shooter log `points` are
renamed. Several of these change what an existing caller sends or receives, so each is flagged **Breaking** in
`CHANGELOG.md`.

---

## ⭐ Key Highlights

### 🏆 Shooter Result Calculation

- `IpscShooterResultService.calculateMatchShooterResult` creates or updates a `ShooterResult` for each of a match's
  competitors, ranking them by match points within their firearm type, division and competitor category, with
  competitors in no category (`NONE`) a group of their own
- Competitors on equal points share a rank and the next rank is skipped; a competitor without points gets neither a
  rank nor a percentage
- Each percentage is the competitor's points as a percentage of their group winner's, to
  `IpscConstants.PERCENTAGE_SCALE` decimal places
- Results are recalculated only when they are out of date: a competitor has no result yet, or the match or a competitor
  was updated at least five minutes after the oldest result was calculated. Otherwise, the current results are returned
- `TransactionService.saveShooterResults` saves a batch in one transaction, so a unique constraint violation surfaces as
  a `DataIntegrityViolationException`

### 🌐 Shooter Result Endpoints

- `POST /ipsc/shooter-results/matches/{matchId}` calculates a match's results and returns the match linked to them
- `GET /ipsc/shooter-results/matches/{matchId}` and `GET /ipsc/shooter-results` return a match linked to its results, or
  one such holder per match, oldest match date first, including matches without results
- An unknown match ID answers `404`
- `ShooterResultResponse` shows the percentage as a display string

### 🗑️ Rankings Removed

- `MatchCompetitor` drops `overallRanking` and `clubRanking`, and `V11_8_0__remove_match_competitor_rankings.sql` drops
  their columns
- They leave `MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorResponse`,
  `MatchCompetitorRow` and the bulk CSV import

### 🔐 Dependency Security

- `org.ow2.asm:asm` is pinned to 9.11 through `dependencyManagement`, replacing the vulnerable 9.7.1 that
  `json-smart`'s `accessors-smart` brings in through `spring-boot-starter-test`

---

## 📦 What's New

### Added

- `ShooterResult`, `ShooterResultRepository` and `V11_10_0__create_shooter_result.sql`
- `IpscShooterResultService`, `IpscShooterResultServiceImpl` and `IpscShooterResultController`
- `ShooterResultResponse`, `ShooterMatchResultResponse` and `ShooterResponseHolder`
- `MatchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch`
- `TransactionService.saveShooterResults`
- `NumberUtil`, reinstated with its tests, and `IpscConstants`' `MATCH_POINTS_SCALE`, `HIT_FACTOR_SCALE`,
  `TIME_SCALE` and `PERCENTAGE_SCALE`, with `SystemConstants.DEFAULT_SCALE`

### Changed

- **Breaking:** `ShooterLogCompetitor` and `ShooterLogOverall` rename `points` to `percentageInLog`, and its column to
  `percentage_in_log` (`V11_9_0__rename_shooter_log_points_to_percentage_in_log.sql`)
- `MatchCompetitorMapper` no longer copies `overallRanking` or `clubRanking`
- `IpscMatchCompetitorController`'s bulk CSV example drops the `OverallRanking` and `ClubRanking` columns
- The v10.0.0 – v13.3.0 `CHANGELOG`, `HISTORY` and `EVOLUTION_OVERVIEW` entries, release notes and pull request
  descriptions move to `documentation/archive/v10-v13/`, and the links that pointed at them follow

### Removed

- **Breaking:** `MatchCompetitor.overallRanking` and `clubRanking`, and their columns
- **Breaking:** the optional `overallRanking` and `clubRanking` from the create, replace and patch request bodies, and
  from the response body
- **Breaking:** the optional `OverallRanking` and `ClubRanking` columns from the bulk match competitor CSV import

### Security

- `org.ow2.asm:asm` pinned to 9.11 through the new `asm.version` property

---

## 🚀 Migration Guide

Flyway runs `V11_8_0`, `V11_9_0` and `V11_10_0` on startup, so the schema changes with no action. The data in the
dropped ranking columns is not kept. Callers must change what they send and read:

- Stop sending `overallRanking` and `clubRanking` in match competitor request bodies and drop the `OverallRanking` and
  `ClubRanking` columns from the bulk match competitor CSV import. Do not read them from a response.
- Code that reads `points` on `ShooterLogCompetitor` or `ShooterLogOverall` uses `percentageInLog`.
- Call `POST /ipsc/shooter-results/matches/{matchId}` to calculate a match's results before reading them.

---

## 📊 Statistics

- **Files Changed:** 49 against `main` before this release's documentation and version bump; 2,253 insertions and 120
  deletions
- **New Source Files:** 12 (9 classes and 3 migrations)
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **Rank within a like-for-like group.** A competitor is ranked against others who shot the same firearm type, division
  and competitor category, and competitors in no category are a group of their own rather than being mixed into another.
- **Recalculate only when stale.** A five-minute threshold treats updates made around the time of the calculation, or
  lost to a database's timestamp rounding, as no change, so reading results does not repeat the work.
- **Rankings belong to the results.** `ShooterResult` holds the rank and percentage, so the overall and club rankings on
  `MatchCompetitor` are removed rather than kept in two places.
- **A MAJOR.** The removed ranking fields and CSV columns and the renamed `points` property are backward-incompatible,
  which `AGENTS.md`'s Semantic Versioning rules classify as MAJOR.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` passes: 1,320 tests with no failures, errors or skips.
- New unit tests cover `IpscShooterResultService` (including the category grouping and the recalculation rule),
  `IpscShooterResultController` and `NumberUtil`, with a `@SpringBootTest` integration test for the service.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports,
  is read but never used as the row's own ID, since the imports only create records.
- No service or controller operates on `ShooterLog`, `ShooterLogCompetitor` or `ShooterLogOverall` yet (Gap #6,
  partially completed).
- `pom.xml` still overrides `tomcat.version` (to `11.0.26`) for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26). `logback.version`, the Jackson BOM properties and `flyway-mysql` are likewise set above
  Spring Boot's managed versions, `mysql-connector-j` is pinned to `26.7.0` against the `9.7.0` it manages, and
  `asm.version` is pinned because Spring Boot does not manage `asm`.
- Stored `Manual Action Contemporary Division` and `Manual Action Bolt Division` values are not migrated and read back
  as `null`.

---

## 🔮 Future Enhancements

- Build a `ShooterLogService` and controller over the existing repositories, committing through `TransactionService`,
  following the same phased pattern that closed Gap #1 (Gap #6).
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.26` or later (Gap #26), and the
  other dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge
- Claude Code (Claude Sonnet 5.5) — co-author of the release's commits

---

## 📝 Notes

Version 15.0.0 calculates and reads a match's shooter results, ranked within firearm type, division and competitor
category, and removes the overall and club rankings from a match competitor. It leaves the improvement plan at 38 closed
gaps, with #6 progressed and only #6 and #26 partially completed.

---
