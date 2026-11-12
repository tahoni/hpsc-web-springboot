## 🎯 Summary

- Keeps a shooter log's start and end dates true after the log is written, since they were only enforced while it was
  created, replaced or patched
- Refuses, with a `400`, calculating the overall results of a log that covers a match outside its dates
- Refuses, with a `400`, rescheduling a match outside the dates of a shooter log that covers it
- A PATCH release: no endpoint, request or response field changes and the one new repository method is internal

## 📦 Key Changes

- **Added:** `ShooterLogRepository.findAllByMatchesId`, which finds the shooter logs that cover a match
- **Changed:** `IpscShooterLogServiceImpl` no longer checks for a match with no scheduled date, since the column is
  `NOT NULL`; the date check now lives in the new `ShooterLogHelpers`, shared by the log, the calculation and the
  reschedule check
- **Fixed:** `IpscShooterLogOverallServiceImpl.calculateShooterLogOverall` refuses a match outside the log's dates, and
  a match updated after the overall results were calculated marks them out of date; `IpscMatchServiceImpl.updateMatch`
  and `patchMatch` refuse moving a match outside a covering log's dates
- **Documentation:** `README.md`, `ARCHITECTURE.md`, the service Javadoc and the OpenAPI descriptions state the rule and
  its `400` responses; improvement plan Gap #45 added and closed
- **Tests:** new helper, match service and overall service unit and integration tests
- **Build:** project version bumped to 16.1.2 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` passes: 1,476 tests, no failures, errors or skips, and coverage above the 97% minimum
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[16.1.2]`
- [ ] Check for shooter logs that already cover a match outside their dates, and put each right, since their overall
  results cannot be calculated until then
- [ ] Move a match outside a covering log's dates through `PUT` and `PATCH /ipsc/matches/{matchId}` and confirm a `400`
  naming the log

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
