## 🎯 Summary

- Adds shooter log management: create, replace, patch, read and delete shooter logs under `/ipsc/shooter-logs`, with
  every match scheduled inside the log's dates
- Calculates and reads a shooter log's overall results under `/ipsc/shooter-log-overall-results`, from each
  competitor's best four match percentages
- Drops the unused `shooter_log_competitor` table and a few unused constants and helpers
- A MINOR release: the new endpoints are backward-compatible and nothing removed was part of the public API

## 📦 Key Changes

- **Added:** `IpscShooterLogController`, `IpscShooterLogOverallController`, their services, request and response DTOs,
  new `TransactionService` methods and repository finders, and `IpscConstants.SHOOTER_LOG_BEST_MATCHES`
- **Changed:** `jacoco.version` bumped to 0.8.16 and `springdoc-openapi-bom` to 3.2.0
- **Removed:** `ShooterLogCompetitor` and its repository, with `V11_12_0__drop_shooter_log_competitor.sql` dropping the
  table; the unused score-scale constants, `SystemConstants.DEFAULT_SCALE` and `NumberUtil.calculateSum`
- **Documentation:** improvement plan Gaps #6, #41 and #42 closed; Flyway versioning table gains `V11_12_0`
- **Tests:** new branch-coverage tests for shooter log overall, shooter log patch, shooter result and match competitor
  resolution
- **Build:** project version bumped to 16.1.0 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw clean test` passes: 1,439 tests, no failures, errors or skips
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[16.1.0]`
- [x] Confirm Flyway applies `V11_12_0` against a MySQL database that still holds `shooter_log_competitor`
- [x] `./mvnw verify -Pcoverage` passes; the only missed branches left are simple null guards and one unreachable check

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
