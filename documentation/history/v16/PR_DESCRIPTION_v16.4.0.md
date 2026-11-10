## 🎯 Summary

- Lets the bulk imports change what they created: existing shooter logs can be replaced and updated from CSV, a
  competitor row for an existing club number updates it, and a match that has fallen out of step with its log says so
- Adds `PUT` and `PATCH /ipsc/shooter-logs/bulk`, and lets a `PATCH` add and remove a log's matches without restating
  them
- A bulk competitor row whose club number is an existing competitor's updates and verifies that competitor
- A MINOR release: new endpoints and optional response and request fields, all backward-compatible

## 📦 Key Changes

- **Added:** `IpscShooterLogController.updateShooterLogs` and `patchShooterLogs` with the matching service methods,
  `ShooterLogBulkRequest`, `ShooterLogBulkRequestCsvMixIn`, `ShooterLogPatchRequest.addMatchIds` and `removeMatchIds`,
  `CompetitorBulkResponse`, `CompetitorResponseHolder.results`, `MatchResponse.warnings` and
  `ShooterLogHelpers.describeOutsideDates`; the bulk endpoints answer `200`, or `422` if every row failed
- **Changed:** the competitor import updates and verifies a competitor by club number; the CSV imports stop reading
  their own ID column; `flyway-mysql` is no longer pinned above the Flyway version Spring Boot manages
- **Documentation:** `README.md`, `ARCHITECTURE.md`, the OpenAPI text and the Javadoc describe the changes;
  improvement plan Gaps #48, #49 and #50 added and closed
- **Tests:** new controller, mix-in, request, service and integration tests
- **Build:** project version bumped to 16.4.0 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` passes: 1,564 tests, no failures, errors or skips, with 98.36% line and 98.74% branch
  coverage, above the 97% minimum
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[16.4.0]`
- [x] All 38 Flyway migrations applied to a throwaway MySQL 8.4 on Flyway `12.4.0`
- [ ] Post a CSV with valid, unknown-log, missing-ID and out-of-dates rows to `PUT` and `PATCH /ipsc/shooter-logs/bulk`
  and check each row's result and the `200`
- [ ] `PATCH` a shooter log's `addMatchIds` and `removeMatchIds` and confirm the matches change, and a match in both is
  a `400`
- [ ] Re-import a competitor CSV whose club numbers already exist and confirm the competitors are updated and verified
- [ ] Read a match that is outside a covering log's dates and confirm its `warnings`

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
