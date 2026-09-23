# Release Notes – Version 8.6.0

**Release Date:** September 23, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Match URL Field, Start/End Time Precision Fix & Test Architecture Formalisation**

Version 8.6.0 adds a `url` field to IPSC matches — a link to more information about a match, such as a results page
or event listing — and corrects `IpscMatch.startTime`/`endTime` from `LocalDateTime` to `LocalTime`, dropping the
redundant date component those fields never needed. It also formally documents the 3-tier service test architecture
already followed by all four services, closing a gap between the project's actual testing practice and what
`AGENTS.md` said about it.

---

## ⭐ Key Highlights

### 🔗 Match URL Field

- `IpscMatch` gains a new nullable `url` column via `V7_6_0__add_ipsc_match_url.sql`
- `MatchRequest`, `MatchRequestForCSV` and `MatchResponse` carry the new field through the JSON and CSV APIs;
  `IpscMatchController`'s CSV bulk import header gains a matching `Url` column

### 🕐 Start/End Time Precision Fix

- `IpscMatch.startTime`/`endTime` corrected from `LocalDateTime` to `LocalTime` via
  `V7_7_0__change_ipsc_match_start_end_time_to_time.sql` — these were always time-of-day-only values alongside
  `scheduledDate`, so the redundant date component v8.5.0 introduced is dropped
- New `IpscConstants.IPSC_INPUT_TIME_FORMAT` (`HH:mm`) constant replaces `IPSC_INPUT_DATE_TIME_FORMAT` on both
  fields; JSON/CSV values are now bare `HH:mm` instead of `yyyy-MM-dd HH:mm`

### 🧪 Test Architecture Formalisation

- `AGENTS.md`'s Test Conventions section now formally documents the 3-tier service test architecture
  (`<Service>Test`/`<Service>ImplTest`/`<Service>IntegrationTest`) already followed by all four services, replacing
  a one-line pointer that previously only described the pattern piecemeal across the `scaffold-unit-tests`/
  `scaffold-integration-tests` skills

---

## 📦 What's New

### Added

#### Domain

- **`IpscMatch.url`:** New nullable `String` column — a URL with more information about a match (e.g. a results
  page or event listing)

#### API Models

- **`MatchRequest`, `MatchRequestForCSV`, `MatchResponse`:** New nullable `url` field

#### Database

- **`V7_6_0__add_ipsc_match_url.sql`:** New Flyway migration — adds a nullable `url` column to `ipsc_match`

#### Tests

- **`IpscMatchServiceIntegrationTest`, `IpscMatchServiceTest`, `IpscMatchServiceImplTest`, `MatchRequestTest`,
  `MatchRequestForCSVTest`:** `url` now flows through each fixture/CSV row and is asserted in the create/patch/
  update/get happy-path tests, proving it round-trips through JSON, CSV import and the real H2/Hibernate/JPA layer

### Changed

#### Domain

- **`IpscMatch.startTime`, `IpscMatch.endTime`:** Changed from `LocalDateTime` to `LocalTime` — these were always
  time-of-day-only values alongside `scheduledDate`, so the redundant date component is dropped

#### API Models

- **`MatchRequest`, `MatchRequestForCSV`, `MatchResponse`:** `startTime`/`endTime` changed from `LocalDateTime` to
  `LocalTime`, now formatted per the new `IpscConstants.IPSC_INPUT_TIME_FORMAT` (`HH:mm`) instead of
  `IPSC_INPUT_DATE_TIME_FORMAT` (`yyyy-MM-dd HH:mm`)
- **`IpscMatchController`:** `createMatches`'s OpenAPI CSV example's `StartTime`/`EndTime` columns updated to the
  bare `HH:mm` pattern

#### Database

- **`V7_7_0__change_ipsc_match_start_end_time_to_time.sql`:** New Flyway migration — changes `ipsc_match`'s
  `start_time`/`end_time` columns from `DATETIME` to `TIME`

#### API

- **CSV bulk import (`POST /matches/csv`):** The header row must now include a `Url` column, like every other
  `MatchRequestForCSV` property — existing CSV templates need updating to add it (values may be left blank)
- **JSON/CSV `startTime`/`endTime`:** Now accepted/returned as bare `HH:mm` time-of-day values instead of
  `yyyy-MM-dd HH:mm` — existing CSV templates and API clients need updating to drop the date component

#### Services

- **`IpscMatchServiceImpl`:** `applyFields`, `patchMatch`, `toRequest` and `toResponse` now carry `url` through
  between `MatchRequest`/`MatchRequestForCSV`, `IpscMatch` and `MatchResponse`

#### Tests

- **`IpscMatchServiceIntegrationTest`, `IpscMatchServiceTest`, `IpscMatchServiceImplTest`, `MatchRequestTest`,
  `MatchRequestForCSVTest`:** `startTime`/`endTime` fixtures, CSV rows and JSON payloads updated from
  `LocalDateTime`/`yyyy-MM-dd HH:mm` to `LocalTime`/`HH:mm`

#### Documentation

- **`AGENTS.md`:** Test Conventions section now formally documents the 3-tier service test architecture
  (`<Service>Test`/`<Service>ImplTest`/`<Service>IntegrationTest`) already followed by all four services —
  previously only described piecemeal across the `scaffold-unit-tests`/`scaffold-integration-tests` skills

---

## 🚀 Migration Guide

- **Database:** Run the two new Flyway migrations (applied automatically on startup):
  `V7_6_0__add_ipsc_match_url.sql` adds a nullable `url` column to `ipsc_match` (no backfill required);
  `V7_7_0__change_ipsc_match_start_end_time_to_time.sql` narrows `start_time`/`end_time` from `DATETIME` to `TIME`
  — any existing non-null value's date component is silently dropped by the column type change.
- **CSV bulk import:** Any existing `MatchRequestForCSV` CSV template/header used against `POST /matches/csv` must
  be updated to add a `Url` column (value may be left blank) and to supply `StartTime`/`EndTime` values as bare
  `HH:mm` instead of `yyyy-MM-dd HH:mm`.
- **JSON API:** `url` is optional on `MatchRequest`; omitting it leaves the field `null`, backwards compatible.
  `startTime`/`endTime` are still optional, but any client that previously sent/parsed them as
  `yyyy-MM-dd HH:mm` must switch to bare `HH:mm` — this is a breaking format change for existing integrations that
  populate these fields.

---

## 📊 Statistics

- **Total Commits:** 6 (3 feature commits, plus this release's version bump, documentation and PR description
  commits)
- **Files Changed:** 22
- **Insertions:** 743 lines
- **Deletions:** 215 lines
- **Net Change:** +528 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Correct a type mistake promptly, in the same major/minor cycle it was introduced.** `startTime`/`endTime` were
  added as `LocalDateTime` in v8.5.0, one release ago — they were always time-of-day-only values alongside
  `scheduledDate`, so fixing the type now, before more API consumers integrate against the wrong shape, was
  preferred over living with it indefinitely or waiting for a major version.
- **Extend the shared time-format convention rather than reuse the date-time one.** The new
  `IpscConstants.IPSC_INPUT_TIME_FORMAT` (`HH:mm`) mirrors how `IPSC_INPUT_DATE_TIME_FORMAT` and
  `IPSC_INPUT_DATE_FORMAT` are already centralised constants, rather than inlining the pattern on each field.
- **A breaking format change is still worth making when the previous format was simply wrong.** Unlike most
  additive changes in this project, the `HH:mm` switch is not backwards compatible for existing API clients — but
  shipping it now avoids compounding the mistake with more integrations built against `yyyy-MM-dd HH:mm` for a
  value that was never actually a date-time.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (872 tests, 0 failures/errors), up from 870 at v8.5.0.
- `./mvnw verify -Pcoverage` — 98.66% line / 99.00% branch coverage, up from 98.65%/98.99% at v8.5.0.
- New/extended coverage: `IpscMatchServiceIntegrationTest` (real H2/Hibernate round-trip for `url` and the
  `LocalTime` fields), `IpscMatchServiceTest`, `IpscMatchServiceImplTest`, `MatchRequestTest`,
  `MatchRequestForCSVTest` — see the Tests entries above for detail.

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

Version 8.6.0 is a focused domain-correctness release: one new optional field, one data-type fix for a mistake
introduced in the immediately preceding release, and a documentation formalisation of an already-established test
convention. The `startTime`/`endTime` format change is the one breaking change in this release — existing API
clients and CSV templates that populate those fields need updating.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
