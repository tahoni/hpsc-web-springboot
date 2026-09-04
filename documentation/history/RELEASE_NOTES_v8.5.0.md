# Release Notes – Version 8.5.0

**Release Date:** September 4, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Match Start/End Time Tracking**

Version 8.5.0 adds `startTime`/`endTime` tracking to IPSC matches, alongside the existing `scheduledDate` — recording
when a match actually started and ended, not just when it was scheduled to be shot. The two new nullable fields are
wired end-to-end: domain, JSON API, CSV bulk import and the underlying service layer, with matching test coverage
proving they persist correctly through the real database, not just mocked repositories.

---

## ⭐ Key Highlights

### 🕐 Match Start/End Time

- `IpscMatch` gains nullable `startTime`/`endTime` (`LocalDateTime`) columns via new
  `V7_5_0__add_ipsc_match_start_end_time.sql`
- `MatchRequest`, `MatchRequestForCSV` and `MatchResponse` carry the new fields through the JSON and CSV APIs,
  formatted as `yyyy-MM-dd HH:mm` per `IpscConstants.IPSC_INPUT_DATE_TIME_FORMAT`
- `IpscMatchServiceImpl`'s `applyFields`, `patchMatch`, `toRequest` and `toResponse` map the fields between the
  request DTOs, the `IpscMatch` entity and the response DTO

### 🧪 Real Database Round-Trip Coverage

- `IpscMatchServiceIntegrationTest` (H2/Hibernate/JPA, not mocks) now proves `startTime`/`endTime` persist and read
  back correctly across `createMatch`, `getMatch`, `patchMatch` and `updateMatch`
- `IpscMatchServiceTest`'s CSV bulk-import test now supplies real `StartTime`/`EndTime` values and asserts on them,
  closing the one gap where that mapping path was never verified

---

## 📦 What's New

### Added

#### Domain

- **`IpscMatch.startTime`, `IpscMatch.endTime`:** New nullable `LocalDateTime` columns — record when a match
  actually started and ended, alongside the existing `scheduledDate`

#### API Models

- **`MatchRequest`, `MatchRequestForCSV`, `MatchResponse`:** New nullable `startTime`/`endTime` fields, formatted
  per `IpscConstants.IPSC_INPUT_DATE_TIME_FORMAT` (`yyyy-MM-dd HH:mm`) on the two request DTOs
- **`IpscMatchController`:** `createMatches`'s OpenAPI CSV example now includes the new `StartTime`/`EndTime`
  columns

#### Database

- **`V7_5_0__add_ipsc_match_start_end_time.sql`:** New Flyway migration — adds nullable `start_time`/`end_time`
  columns to `ipsc_match`

#### Tests

- **`IpscMatchServiceIntegrationTest`:** `startTime`/`endTime` now flow through `validRequest`'s fixture and are
  asserted in `createMatch`/`getMatch`/`updateMatch`'s happy-path tests, plus a new
  `testPatchMatch_whenStartAndEndTimeAreProvided_thenStartAndEndTimeChange` — proves the two new columns actually
  round-trip through the real H2/Hibernate/JPA layer, not just mocked repositories
- **`IpscMatchServiceTest`:** `startTime`/`endTime` now flow through `validRequest`'s fixture and are asserted in
  `createMatch`'s and `updateMatch`'s happy-path tests; `testCreateMatches_whenSingleValidRow_...`'s CSV row now
  supplies actual `StartTime`/`EndTime` values (previously left blank) with matching assertions

#### Documentation

- **`README.md`:** "License" heading/prose corrected to British English "Licence" (the `LICENSE.md` filename
  itself is unchanged, per `AGENTS.md`'s British English exceptions for filenames)

### Changed

#### API

- **CSV bulk import (`POST /matches/csv`):** The header row must now include `StartTime`/`EndTime` columns, like
  every other `MatchRequestForCSV` property — consistent with this endpoint's existing all-columns-required header
  validation, but existing CSV templates need updating to add them (values may be left blank)

#### Services

- **`IpscMatchServiceImpl`:** `applyFields`, `patchMatch`, `toRequest` and `toResponse` now carry `startTime`/
  `endTime` through between `MatchRequest`/`MatchRequestForCSV`, `IpscMatch` and `MatchResponse`

### Fixed

#### Documentation

- **`CONTRIBUTING.md`:** Its own Serial Commas rule example ("prose, comments, and Javadoc") violated the rule it
  was illustrating — corrected to "prose, comments and Javadoc"

---

## 🚀 Migration Guide

- **Database:** Run the new `V7_5_0__add_ipsc_match_start_end_time.sql` Flyway migration (applied automatically on
  startup) — adds nullable `start_time`/`end_time` columns to `ipsc_match`; no backfill required.
- **CSV bulk import:** Any existing `MatchRequestForCSV` CSV template/header used against `POST /matches/csv` must
  be updated to add `StartTime` and `EndTime` columns. Values may be left blank — only the column names are
  required, matching this endpoint's existing behaviour for every other optional field.
- **JSON API:** `startTime`/`endTime` are optional on `MatchRequest`; omitting them is backwards compatible and
  leaves the fields `null`. `MatchResponse` always includes both fields (`null` when unset).

---

## 📊 Statistics

- **Total Commits:** 5
- **Files Changed:** 18
- **Insertions:** 271 lines
- **Deletions:** 44 lines
- **Net Change:** +227 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Extend the shared date-time format, don't invent a new one.** `startTime`/`endTime` reuse
  `IpscConstants.IPSC_INPUT_DATE_TIME_FORMAT` (`yyyy-MM-dd HH:mm`), the same pattern already established for every
  other IPSC request DTO date-time field, rather than introducing a second convention.
- **CSV header strictness applies uniformly, including to new optional fields.** This endpoint's CSV schema already
  required every `MatchRequestForCSV` column present in the header regardless of whether the value itself is
  optional (confirmed by the pre-existing `testReadMatches_whenHeaderIsMissingColumns_thenThrowsValidationException`
  test) — `StartTime`/`EndTime` follow that same rule rather than carving out an exception, keeping the CSV contract
  predictable at the cost of existing templates needing a one-time header update.
- **Prove the round-trip at the layer that actually matters.** Unit tests with mocked repositories can prove field
  *mapping* logic is correct, but only a real-database integration test proves the new nullable columns actually
  persist and read back through Hibernate/JPA/H2 — `IpscMatchServiceIntegrationTest` was the one file with zero
  coverage of the new fields before this release, so closing that gap took priority over duplicating assertions
  already covered elsewhere.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (870 tests, 0 failures/errors), up from 868 at v8.4.0.
- `./mvnw verify -Pcoverage` — 98.65% line / 98.99% branch coverage, up from 98.44%/98.98% at v8.4.0.
- New/extended coverage: `IpscMatchServiceIntegrationTest` (real H2/Hibernate round-trip), `IpscMatchServiceTest`,
  `IpscMatchServiceImplTest`, `MatchRequestTest`, `MatchRequestForCSVTest` — see the Tests entries above for detail.

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- The `BRANCH` coverage counter is still not separately enforced by the JaCoCo `check` execution — only `LINE` is,
  as established when the gate was first added in v8.3.1.

---

## 🔮 Future Enhancements

- Build a `MatchScoreService`/`ShooterLogService` (interface + `impl/` split) over the existing repositories,
  following the same phased pattern that closed Gap #1 and Gap #8.
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint — their
  `@JsonCreator` constructors and required-field enforcement are already correct and ready for this.
- Consider enforcing a `BRANCH`-level JaCoCo minimum alongside the existing `LINE` one, now that the `LINE` floor
  sits close to its real baseline.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.5.0 is a focused feature release adding match start/end time tracking end-to-end, plus a small British
English documentation fix unrelated to the feature itself. No breaking changes to the JSON API; CSV bulk import
templates need a one-time header update to add the two new (optional-value) columns.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
