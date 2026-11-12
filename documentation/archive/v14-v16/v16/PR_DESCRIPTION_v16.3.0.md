## 🎯 Summary

- Gives shooter logs the bulk CSV import that competitors, matches and match competitors already had, so a season's logs
  no longer take one request each
- Adds `POST /ipsc/shooter-logs/bulk`, which creates one shooter log per CSV row and checks each row as a single log is
  checked
- A row that fails is reported with the values it supplied and the reason, and the rest are still created
- A MINOR release: a backward-compatible new endpoint, and a null match ID that was a `500` is now a `400`

## 📦 Key Changes

- **Added:** `IpscShooterLogController.createShooterLogs` and `IpscShooterLogService.createShooterLogs`, with
  `ShooterLogRequestCsvMixIn`, `ShooterLogBulkResponse`, `ShooterLogBulkResponseHolder` and `ShooterLogRow`; the
  endpoint answers `201` if any row was created and `422` if every row failed
- **Changed:** `ShooterLogRequest.validate` rejects a null match ID with a `400` instead of letting it fail with a `500`
- **Documentation:** `README.md`, `ARCHITECTURE.md` (with a Shooter Log Bulk CSV Import Flow diagram), the service
  Javadoc and the OpenAPI description describe the import; improvement plan Gap #47 added and closed
- **Tests:** new controller, request mix-in, row model, service and integration tests
- **Build:** project version bumped to 16.3.0 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` passes: 1,524 tests, no failures, errors or skips, with 98.63% line and 99.15% branch
  coverage, above the 97% minimum
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[16.3.0]`
- [ ] Post a CSV with valid, missing-date, unknown-match and out-of-dates rows to `POST /ipsc/shooter-logs/bulk` and
  check each row's result and the `201`
- [ ] Post a CSV where every row fails and confirm a `422`, and a blank body and confirm a `400`

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
