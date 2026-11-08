# Release Notes – Version 16.1.1

**Release Date:** November 8, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Verified-Only Shooter Results**

Version 16.1.1 is a patch release. Shooter results and a shooter log's overall results now count only verified
competitors, and a match's results also leave out competitors without points. A result whose competitor stops
qualifying is removed the next time results are calculated. No endpoint, request or response changes shape, so the
release is backward-compatible.

---

## ⭐ Key Highlights

### ✅ Who Appears in Results

- A match's shooter results include only competitors whose `isVerified` is `true` and whose points are above zero. A
  competitor who is not verified, whose verification was never recorded, or who has null or zero points gets no result
- Those competitors no longer count towards a group's winner points, so they cannot lower anyone else's percentage
- A shooter log's overall results include only verified competitors, and the overall of one who is not is removed

### 🧹 Stale Results

- A result whose competitor stops qualifying, such as after their points were cleared or their verification was
  withdrawn, is deleted on the next calculation
- Any such stale result also forces a recalculation, so a change made within the five-minute recalculation threshold is
  not missed
- `TransactionService.replaceShooterResults` deletes the stale results and saves the recalculated ones in one
  transaction

### 📖 Documented Rules

- `README.md` and `ARCHITECTURE.md` state who is included in results, and how a competitor becomes verified: by
  setting `isVerified` when the competitor is created (the `isVerified` field, or the `IsVerified` CSV column), or
  through `PUT` or `PATCH /ipsc/competitors/{competitorId}` afterwards
- The match competitor import still imports an entry for an unverified competitor, but that entry only appears in
  results once its competitor is verified. Neither it nor the match import verifies anyone
- The Javadoc on `IpscShooterResultService` and `IpscShooterLogOverallService` and the OpenAPI descriptions on both
  result controllers describe the same rules

### ♻️ Unused Code

- The points filter and "not ranked" note in `IpscShooterResultServiceImpl.rank`, and the null fallback on the winner's
  points, are removed, since competitors without points are excluded before they are grouped
- The fallback from a null competitor category to `NONE` in `IpscShooterResultServiceImpl` and
  `IpscShooterLogOverallServiceImpl`, and the null-safe ordering of a match's scheduled date, are removed, since
  `competitor_category` and `scheduled_date` are `NOT NULL`
- The tolerance for a shooter result without a percentage in `IpscShooterLogOverallServiceImpl` is kept and now
  documented: `percentage_in_log` is a nullable column, so a row inserted by hand or by an earlier version could lack
  one, and ignoring it keeps one bad row from failing a whole log's calculation

---

## 📦 What's New

### Added

- `TransactionService.replaceShooterResults`/`TransactionServiceImpl.replaceShooterResults`
- Service tests for null, zero and unverified competitors and for deleting a stale result, an integration test that
  clears a competitor's points between two calculations, and `TransactionService` tests for the new method

### Changed

- `IpscShooterResultServiceImpl.calculateMatchShooterResult` includes only verified competitors with points above zero,
  and deletes the result of one who no longer qualifies
- `IpscShooterLogOverallServiceImpl.calculateShooterLogOverall` includes only verified competitors
- `IpscShooterResultServiceImpl.rank` no longer filters on points, and the winner's points no longer fall back to
  `null`
- `IpscShooterResultServiceImpl` and `IpscShooterLogOverallServiceImpl` no longer fall back from a null competitor
  category to `NONE`, and the overall service no longer orders a match's scheduled date null-safely
- `IpscShooterLogOverallService` and `IpscShooterLogOverallServiceImpl` document why a missing percentage is tolerated
- `README.md`, `ARCHITECTURE.md`, the `IpscShooterResultService` and `IpscShooterLogOverallService` Javadoc and the
  controllers' OpenAPI `@Operation` descriptions state who is included in results
- `documentation/roadmap/improvement-plan.md` and `improvement-plan-tasks.md` add and close Gaps #43 and #44

### Removed

Nothing is removed.

---

## 🚀 Migration Guide

No action is needed for callers: no existing endpoint, request or response changes shape. The responses simply omit
competitors who are not verified or have no points.

Competitors created since `V8_7_0` without an `isVerified` value are not verified, so they are left out of shooter
results and overall results until they are marked verified. `V8_7_0` marked only the competitors that existed at the
time as verified, and no migration backfills the others. Before deploying, check which competitors should appear in
results and set `isVerified` for them through `PUT` or `PATCH /ipsc/competitors/{competitorId}`.

Results that already exist for competitors who do not qualify are deleted the next time their match's results are
calculated, so recalculate each match's results, or the shooter log's overall results, after verifying competitors.

---

## 📊 Statistics

- **Files Changed:** 18 against `main` before this release's documentation; 554 insertions and 68 deletions
- **New Source Files:** 0
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **Verification is never defaulted.** A competitor created without `isVerified` stays unverified, so being verified is
  always a deliberate act through the competitor endpoints. Defaulting it on import would have made the flag
  meaningless.
- **Only `true` counts.** A competitor whose `isVerified` is `false` or `null` is excluded alike, so an unrecorded
  verification is treated as not verified.
- **Excluded, not unranked.** Competitors without points used to be saved with a null rank and percentage. They now get
  no result at all, so a result always means a ranked competitor and a group's winner is always a verified competitor
  who scored.
- **Stale results are deleted, not hidden.** A result for a competitor who no longer qualifies is removed in the same
  transaction that saves the recalculated ones, rather than being filtered out when it is read.
- **The match competitor import is not stricter.** It still imports an entry for an unverified competitor, so the data
  is kept and the entry appears in results as soon as its competitor is verified.
- **Unreachable code is removed, defensive code is kept.** A fallback is removed only when a `NOT NULL` column or the
  calling code makes it impossible to reach. Null handling on a nullable column, such as a missing percentage, stays
  and says why, so a bad row degrades one result rather than the whole calculation.
- **A PATCH.** No contract changes and the one new method is internal, which `AGENTS.md`'s Semantic Versioning rules
  classify as a backward-compatible fix.

---

## 🧪 Testing

- `./mvnw clean verify -Pcoverage` passes: 1,447 tests with no failures, errors or skips.
- New tests cover null, zero and unverified competitors in the shooter result and overall result services, deleting a
  stale result when points are cleared or verification is withdrawn, and `TransactionService.replaceShooterResults`
  including its rollback when the flush fails. The two tests that only covered the removed category fallbacks are
  removed with them.
- Overall coverage is 98.91% branch and 98.80% line, above the 97% minimum, and `IpscShooterResultServiceImpl` has
  no missed lines or branches. The only missed code in the two services is the null guard on a shooter log's matches.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports,
  is read but never used as the row's own ID, since the imports only create records.
- Calculating a log's overall results does not re-check its matches' dates; they are enforced when the log is written,
  so a match rescheduled afterwards is still counted.
- Shooter logs can only be created one at a time; there is no bulk import for them.
- The match competitor import does not say that an entry's competitor is unverified, so an entry that never appears in
  results gives no sign of why.
- Competitors can only be verified one at a time, and the bulk competitor import only creates competitors, so it
  cannot verify one that already exists.
- `pom.xml` still overrides `logback.version`, the Jackson BOM properties and `flyway-mysql`, set above Spring Boot's
  managed versions.

---

## 🔮 Future Enhancements

- Add a bulk import for shooter logs if creating them one by one proves slow.
- Re-check a log's matches against its dates when its overall results are calculated, or when a match is rescheduled.
- Report, or skip, a match competitor import row whose competitor is not verified, and consider a way to verify
  competitors in bulk.
- Drop the remaining dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge
- Claude Code (Claude Sonnet 5.5) — co-author of the release's commits

---

## 📝 Notes

Version 16.1.1 limits shooter results and overall results to verified competitors with points, and removes the result of
one who stops qualifying. It leaves the improvement plan with all 44 gaps closed and none open.

---
