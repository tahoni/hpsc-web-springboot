# Release Notes – Version 8.1.0

**Release Date:** September 1, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Competitor Bulk CSV Import & Required-Field Enforcement Fixes**

Version 8.1.0 extends the IPSC competitor module completed in v8.0.0 with bulk CSV import —
`IpscCompetitorController.createCompetitors` follows the same bulk-import convention as
`AwardController`/`ImageController`, but, unlike those, actually persists each row. While building and testing that
feature, this release also uncovers and fixes a subtle Jackson gotcha affecting every `@JsonProperty(required = true)`
field added across the IPSC request models to date: without a matching `@JsonCreator` constructor, the annotation
never actually fires. `CompetitorRequestForCSV`, `CompetitorRequest`, `MatchRequest`, `MatchStageRequest` and the
not-yet-wired `MatchOverallScoresRequest`/`MatchStageScoresRequest` (plus their CSV variants) are all corrected, each
gaining a `@JsonCreator` constructor and comprehensive new unit test coverage.

---

## ⭐ Key Highlights

### 🆕 Competitor Bulk CSV Import

- **`IpscCompetitorController.createCompetitors`** (`POST /ipsc/competitors/bulk`, consumes `text/csv`) — parses CSV
  data into `CompetitorRequestForCSV` rows and creates each competitor via the existing
  `IpscCompetitorService.createCompetitor` validation/gender/home-club-resolution logic
- Unlike `AwardController`/`ImageController`'s bulk endpoints, which only build response objects without persisting,
  this endpoint actually saves each competitor
- New **`CompetitorRequestForCSV`** (CSV-mapped, `UpperCamelCase` Practiscore-style headers) and
  **`CompetitorResponseHolder`** models (`models/ipsc/competitor/`)

### 🐛 Required-Field Enforcement Fix

- **Root cause:** `@JsonProperty(required = true)` only fires for creator (constructor) parameters — a class relying
  on its default no-args constructor and setters silently accepts a missing "required" field as `null`
- **`CompetitorRequestForCSV`, `CompetitorRequest`, `MatchRequest`, `MatchStageRequest`** — each gained a
  `@JsonCreator` constructor with every parameter bound via `@JsonProperty`, replacing their Lombok
  `@AllArgsConstructor` (same signature/order, so every existing positional constructor call across the codebase and
  test suite is unaffected). A missing required field now genuinely throws `MismatchedInputException` during parsing
- **`CompetitorRequest`** — its Jackson-required third field was `competitorNumber`, when
  `IpscCompetitorServiceImpl.validateForCreate` actually requires `clubNumber`; corrected to match
- **`MatchOverallScoresRequest`/`MatchStageScoresRequest`** (plus their CSV variants) — brought in line with the same
  fix even though neither is wired into a controller yet; the two CSV variants' constructors now include `matchId`
  (typically `null`, since it isn't part of the CSV export) so their signature matches their plain counterpart's
  exactly, making them usable as a `csvMapper.addMixIn(...)` mixin — the same pattern
  `AwardServiceImpl`/`ImageServiceImpl` already use for `AwardRequestForCSV`/`ImageRequestForCsv`

### 📅 Explicit Date Format

- **`CompetitorRequest`/`CompetitorRequestForCSV`/`MatchRequest`** — `@JsonFormat(pattern =
  HpscConstants.HPSC_INPUT_DATE_FORMAT)` added to their `LocalDate` fields (`dateOfBirth`/`matchDate`), making the
  accepted `yyyy-MM-dd` input format explicit rather than relying on Jackson's default parsing, matching
  `AwardRequestForCSV`'s existing use of the same pattern

### 🧪 Test Coverage Expansion

- New unit tests for `IpscCompetitorController`/`IpscCompetitorService`/`IpscCompetitorServiceImpl`'s bulk import
- New unit tests for every touched request model's JSON/CSV (de)serialization and required-field enforcement:
  `CompetitorRequestTest`, `CompetitorRequestForCSVTest`, `MatchRequestTest`, `MatchStageRequestTest`,
  `MatchOverallScoresRequestTest`, `MatchStageScoresRequestTest`, `MatchOverallScoresRequestForCSVTest`,
  `MatchStageScoresRequestForCSVTest`

---

## 📦 What's New

### Added

#### Controllers

- `IpscCompetitorController.createCompetitors` — `POST /ipsc/competitors/bulk`, consumes `text/csv`

#### Services

- `IpscCompetitorService`/`IpscCompetitorServiceImpl.createCompetitors`

#### Models

- `CompetitorRequestForCSV`, `CompetitorResponseHolder`

#### Tests

- `IpscCompetitorControllerTest`, `IpscCompetitorServiceTest`, `IpscCompetitorServiceIntegrationTest`,
  `IpscCompetitorServiceImplTest` — new `createCompetitors` coverage
- `CompetitorRequestForCSVTest`, `CompetitorRequestTest`, `MatchRequestTest`, `MatchStageRequestTest`,
  `MatchOverallScoresRequestTest`, `MatchStageScoresRequestTest`, `MatchOverallScoresRequestForCSVTest`,
  `MatchStageScoresRequestForCSVTest`

### Changed

#### Models

- `MatchRequest`, `MatchStageRequest`, `MatchResponse`, `MatchStageResponse`, `MatchOverallScoresRequest`,
  `MatchOverallScoresRequestForCSV`, `MatchStageScoresRequest`, `MatchStageScoresRequestForCSV` — required fields
  documented with `@NotNull`, then corrected to `@JsonProperty(required = true)` backed by a new `@JsonCreator`
  constructor so the requirement is actually enforced
- `CompetitorRequestForCSV` — `firstName`/`lastName` require a `@JsonCreator` constructor bound to their
  `UpperCamelCase` column names; a CSV row or JSON payload missing either now fails at parse time
- `CompetitorRequest` — gained a `@JsonCreator` constructor; required field corrected from `competitorNumber` to
  `clubNumber`
- `CompetitorRequest`/`CompetitorRequestForCSV`/`MatchRequest` — explicit `@JsonFormat` date pattern
- `CompetitorResponse` — `@NotNull` documentation added to `competitorId`/`firstName`/`lastName`/`clubNumber`

### Removed

#### Configuration

- `application.properties` — `hpsc.web.app.club.filter.abbreviation`, unused dead configuration

---

## 🔄 Migration Guide

### For API Consumers

- **A CSV row or JSON payload missing a required field now fails at parse time (`400 Bad Request`) instead of being
  silently accepted as `null`.** This applies to `POST`/`PUT`/`PATCH` requests against `IpscCompetitorController` and
  `IpscMatchController` — `firstName`/`lastName`/`clubNumber` for competitors, `matchDate`/`matchName` for matches,
  `stageNumber` for match stages. If any caller was previously relying on these fields being silently optional,
  requests omitting them will now be rejected.
- **New bulk import endpoint:** `POST /ipsc/competitors/bulk` (`Content-Type: text/csv`) creates competitors from a
  CSV file, following the same `UpperCamelCase`-header convention Practiscore exports use.

### For Developers

- **`CompetitorRequest`/`CompetitorRequestForCSV`/`MatchRequest`/`MatchStageRequest`** no longer have a
  Lombok-generated `@AllArgsConstructor`; a handwritten `@JsonCreator` constructor with the same signature replaces
  it, so existing positional constructor calls are unaffected, but new callers should be aware the constructor is no
  longer auto-generated.

---

## 📊 Statistics

- **Total Commits:** 23
- **Files Changed:** 41
- **Insertions:** 3,015 lines
- **Deletions:** 78 lines
- **Net Change:** +2,937 lines
- **New Source Files:** 2
- **New Test Files:** 8
- **Deleted Test Files:** 0

---

## 🧭 Design Notes

- **Fix the bug everywhere it appears, not just where it was found.** The Jackson required-field gotcha was first
  discovered in `CompetitorRequestForCSV`, but a repo-wide check found the same `@AllArgsConstructor` +
  `@JsonProperty(required = true)` pattern already applied to `MatchRequest`/`MatchStageRequest` and — even though
  unused by any controller yet — `MatchOverallScoresRequest`/`MatchStageScoresRequest`. All were corrected in this
  release rather than leaving a latent bug for whoever wires the scores models up next.
- **Verify the fix, don't just assume it.** A throwaway `csvMapper.addMixIn(...)` scratch test (written, run, then
  discarded) confirmed the scores CSV variants' constructors are genuinely mixin-compatible with their plain
  counterparts before committing to that design, the same pattern `AwardServiceImpl`/`ImageServiceImpl` already use.
- **Correct a validation mismatch found along the way.** `CompetitorRequest`'s Jackson-required field was
  `competitorNumber`, but `IpscCompetitorServiceImpl.validateForCreate` actually requires `clubNumber` — a genuine,
  pre-existing inconsistency between the JSON contract and the business rule, fixed as part of the same pass.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (746 tests).
- New unit tests: `IpscCompetitorControllerTest` (`createCompetitors`), `IpscCompetitorServiceTest`/
  `IpscCompetitorServiceIntegrationTest` (validation, row-level resolution, bulk persistence),
  `IpscCompetitorServiceImplTest` (`readCompetitors`/`toRequest`).
- New model tests: `CompetitorRequestForCSVTest`, `CompetitorRequestTest`, `MatchRequestTest`,
  `MatchStageRequestTest`, `MatchOverallScoresRequestTest`, `MatchStageScoresRequestTest`,
  `MatchOverallScoresRequestForCSVTest`, `MatchStageScoresRequestForCSVTest` — JSON/CSV (de)serialization,
  `@JsonFormat` date patterns, Practiscore column mapping (via a concrete test subclass for the abstract CSV classes)
  and required-field enforcement (`MismatchedInputException`).

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only — not
  yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried over
  from v7.0.0 – v7.1.0).

---

## 🔮 Future Enhancements

- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint — their
  `@JsonCreator` constructors and required-field enforcement are now correct and ready for this.
- Build a `ShooterLogService` to calculate and persist best-4-match snapshots.
- Populate `overallRanking`, `clubRanking` and `isVisitor` during match-result import; seed `Club.identifier` and
  backfill `Competitor.homeClub`.
- Add entity, repository and integration test coverage for the promoted/extended domain model from v7.0.0.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.1.0 extends the competitor module with bulk CSV import and, while building and testing it, uncovers and
fixes a subtle Jackson gotcha present across every `@JsonProperty(required = true)` field added to the IPSC request
models to date — `@JsonProperty(required = true)` only fires for creator (constructor) parameters, so a class relying
on its default no-args constructor and setters was silently accepting a missing "required" field as `null`.
`CompetitorRequestForCSV`, `CompetitorRequest`, `MatchRequest`, `MatchStageRequest` and the not-yet-wired
`MatchOverallScoresRequest`/`MatchStageScoresRequest` are all corrected, each backed by comprehensive new unit test
coverage confirming the fix actually works.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
