## 🎯 Summary

- Limits a match's shooter results to verified competitors with points above zero, and a shooter log's overall results
  to verified competitors
- Deletes the result of a competitor who stops qualifying, such as after their points are cleared, on the next
  calculation
- Documents who is included in results and how a competitor becomes verified, and corrects the Javadoc and OpenAPI text
- A PATCH release: no endpoint, request or response contract changes and the one new method is internal

## 📦 Key Changes

- **Added:** `TransactionService.replaceShooterResults`, which deletes stale results and saves the recalculated ones in
  one transaction
- **Changed:** `IpscShooterResultServiceImpl` and `IpscShooterLogOverallServiceImpl` include only verified competitors,
  and match results only those with points above zero; the unreachable null handling in
  `IpscShooterResultServiceImpl.rank` is removed
- **Documentation:** `README.md`, `ARCHITECTURE.md`, the service Javadoc and the controllers' OpenAPI descriptions state
  who is included and that the match competitor import does not verify anyone; improvement plan Gaps #43 and #44 added
  and closed
- **Tests:** new service tests for null, zero and unverified competitors and stale-result deletion, an integration test
  and `TransactionService` tests
- **Build:** project version bumped to 16.1.1 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw clean verify -Pcoverage` passes: 1,449 tests, no failures, errors or skips, and coverage above the
  97% minimum
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[16.1.1]`
- [ ] Check which existing competitors have no `isVerified` value and mark the ones who should appear in results as
  verified, since no migration backfills them
- [ ] Recalculate a match's results and a shooter log's overall results and confirm unverified competitors are removed

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
