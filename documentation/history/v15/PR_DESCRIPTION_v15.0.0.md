## 🎯 Summary

- **Shooter result calculation**: `IpscShooterResultService` ranks a match's competitors by match points within their
  firearm type, division and competitor category (no category is a group of its own), with each percentage relative to
  the group winner.
- **Shooter result endpoints**: `IpscShooterResultController` calculates a match's results with
  `POST /ipsc/shooter-results/matches/{matchId}` and reads them with two `GET` endpoints, recalculating only when the
  results are out of date.
- **Rankings removed**: `MatchCompetitor` drops `overallRanking` and `clubRanking`, which the results supersede.
- A **MAJOR** release: the removed ranking fields and CSV columns and the renamed shooter log `points` property are
  backward-incompatible.

## 📦 Key Changes

**Added**

- `ShooterResult`, `ShooterResultRepository` and `V11_10_0__create_shooter_result.sql`
- `IpscShooterResultService`, `IpscShooterResultController` and the shooter result response models
- `TransactionService.saveShooterResults`
- `NumberUtil`, reinstated, with the score scale constants

**Changed**

- **Breaking:** `ShooterLogCompetitor` and `ShooterLogOverall` rename `points` to `percentageInLog`
  (`V11_9_0`)
- Version bumped to 15.0.0 in `pom.xml` and `@OpenAPIDefinition`

**Removed**

- **Breaking:** `overallRanking` and `clubRanking` from `MatchCompetitor` (`V11_8_0`), its request and response bodies,
  and the bulk CSV import

**Security**

- `org.ow2.asm:asm` pinned to 9.11, replacing the vulnerable transitive 9.7.1

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` — 1,320 tests, no failures, errors or skips
- [ ] Qodana, CodeQL and Docker workflows pass on this PR
- [x] `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v15/RELEASE_NOTES_v15.0.0.md`

## 👥 Contributors

Leoni Lubbinge and Claude Code (Claude Sonnet 5.5).

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
