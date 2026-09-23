# Release Notes – Version 8.3.0

**Release Date:** September 2, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Match Bulk CSV Import**

Version 8.3.0 brings the match domain to parity with the competitor domain: `IpscMatchController.createMatches`
(`POST /ipsc/matches/bulk`, consumes `text/csv`) persists matches, together with their stages, from CSV data,
mirroring `IpscCompetitorController.createCompetitors`'s v8.1.0 bulk-import shape. Each row is created via the
existing single-`createMatch` validation/club/firearm-type/category-resolution logic — no new cross-entity
orchestration. This closes `documentation/roadmap/improvement-plan.md`'s Gap #8, which `ARCHITECTURE.md` had
documented since v8.1.0 as the one asymmetry left between the two IPSC domains' bulk-import support.

---

## ⭐ Key Highlights

### 📥 Match Bulk CSV Import

- **`IpscMatchController.createMatches`** — new `POST /ipsc/matches/bulk` endpoint, consumes `text/csv`, returns
  `201 Created` with a `MatchResponseHolder` body; propagates `ValidationException`/`NonFatalException`/
  `FatalException` exactly as `createMatch` does for a single row
- **`IpscMatchService`/`IpscMatchServiceImpl.createMatches`** — parses CSV data into `MatchRequestForCSV` rows and
  creates each match via the existing `createMatch` logic; new protected helpers `readMatches` (CSV → rows,
  mirroring `IpscCompetitorServiceImpl`'s CSV-parsing pattern), `toRequest` (row → `MatchRequest`) and `parseStages`
- **`MatchRequestForCSV`** — new request model (`models/ipsc/match/request/`), matching `CompetitorRequestForCSV`'s
  `UpperCamelCase`, `@JsonCreator`-constructor pattern for both CSV and JSON input
- **`MatchResponseHolder`** — new response container (`models/ipsc/match/response/`), mirroring
  `CompetitorResponseHolder`

### 🧩 A Delimited Stages Cell, Not a Nested List

- CSV has no native concept of a repeated group, so `MatchRequest`'s nested `stages` list needed a single-cell
  representation. An initial `numberOfStages` count-only field was implemented, then dropped once it was clear a
  count alone couldn't carry each stage's name — in its place, `MatchRequestForCSV.stages` is a single
  semicolon-separated CSV cell of `<stageNumber>-<stageName>` entries (e.g. `"1-Stage One;2-Stage Two"`)
- **`IpscMatchServiceImpl.parseStages`** — splits each entry on its *first* `-` only, so a stage name that itself
  contains a hyphen (e.g. `"Stage One - The Bank Job"`) round-trips correctly; trims surrounding whitespace and
  skips blank entries

---

## 📦 What's New

### Added

#### Controllers

- **`IpscMatchController`:** New `createMatches` endpoint (`POST /ipsc/matches/bulk`, consumes `text/csv`) for
  bulk-creating IPSC matches, together with their stages, from CSV data, following the same bulk-import convention
  as `IpscCompetitorController.createCompetitors`

#### Services

- **`IpscMatchService`/`IpscMatchServiceImpl`:** New `createMatches` method that parses CSV data into
  `MatchRequestForCSV` rows and creates each match via the existing `createMatch` validation/club/
  firearm-type/category-resolution logic; new `readMatches` and `toRequest` protected helpers mirror
  `IpscCompetitorServiceImpl`'s CSV-parsing pattern, and a new `parseStages` helper splits a row's
  semicolon-separated `Stages` cell into `MatchStageRequest`s, splitting each entry on its first `-` into
  `<stageNumber>-<stageName>`

#### Models

- **`MatchRequestForCSV`:** New class binding `MatchDate`/`MatchName`/`Club`/`MatchFirearmType`/`MatchCategory`/
  `Stages` to their `UpperCamelCase` column/property names for CSV/JSON deserialization, matching
  `CompetitorRequestForCSV`'s pattern
- **`MatchResponseHolder`:** New response container holding the `MatchResponse`s created by a bulk CSV import,
  mirroring `CompetitorResponseHolder`

#### Documentation

- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` — new Gap #8 (match bulk CSV import),
  closed in the same release it was added
- `ARCHITECTURE.md` — Feature Support/Service Layer/Data Flow sections updated to reflect match bulk CSV import,
  removing the stale "removed pending a rebuild" language

### Changed

#### Documentation

- `HISTORY.md` — new Historical Timeline entry, Phase 23 and Milestone 23 for v8.3.0

---

## 🚀 Migration Guide

### For API Consumers

- New endpoint: `POST /ipsc/matches/bulk`, consuming `text/csv` with header row
  `MatchDate,MatchName,Club,MatchFirearmType,MatchCategory,Stages`. `MatchDate`/`MatchName` are required per row;
  `Stages` is a single semicolon-separated cell of `<stageNumber>-<stageName>` entries (e.g.
  `"1-Stage One;2-Stage Two"`), not a nested JSON array. Returns `201 Created` with a `MatchResponseHolder` body
  (`{"matches": [...]}`) on success, or the existing `400`/`404`/`500` `ControllerResponse` error shape on failure.
- No changes to any existing endpoint — this is purely additive.

### For Developers

- No schema or configuration changes; no data migration required.

---

## 📊 Statistics

- **Total Commits:** 12
- **Files Changed:** 20
- **Insertions:** 1,578 lines
- **Deletions:** 167 lines
- **Net Change:** +1,411 lines
- **New Source Files:** 2 (`MatchRequestForCSV.java`, `MatchResponseHolder.java`)
- **Deleted Files:** 0
- **New Test Files:** 1 (`MatchRequestForCSVTest.java`)

---

## 🧭 Design Notes

- **Mirror an established pattern rather than inventing a new one.** `IpscMatchController.createMatches`/
  `IpscMatchService.createMatches` deliberately copy `IpscCompetitorController.createCompetitors`'s shape —
  same endpoint convention, same "parse then delegate to the existing single-item `create` method" structure — so
  the match and competitor bulk-import flows stay easy to reason about together rather than diverging for no reason.
- **A delimited cell over a nested representation, once the alternative was tried and found wanting.** The first
  attempt at representing a match's stages in CSV was a `numberOfStages` count field; it was dropped before it ever
  reached `develop` once it became clear a count alone can't carry each stage's name. The semicolon-delimited
  `<stageNumber>-<stageName>` cell that replaced it is the smallest representation that actually carries the data a
  CSV row needs.
- **Split on the *first* delimiter, not every occurrence.** `parseStages` splits each stage entry on its first `-`
  only, so a stage name containing a hyphen (a genuinely common case for shooting stage names, e.g.
  `"Stage One - The Bank Job"`) is preserved intact rather than truncated or mis-split.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (836 tests, 0 failures/errors).
- New tests: `IpscMatchControllerTest` (`createMatches`'s `201` response, delegation, exception propagation),
  `IpscMatchServiceTest` (CSV validation, row-level club/firearm-type/category resolution, stage parsing, bulk
  persistence, exercised through the interface with mocked repositories), `IpscMatchServiceImplTest` (the impl-only
  `parseStages`/`readMatches`/`toRequest` protected helpers), `MatchRequestForCSVTest` (`UpperCamelCase` JSON/CSV
  (de)serialisation and required-field enforcement).

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- No automatic build/test gate runs on pull requests yet — `./mvnw test`/`./mvnw verify -Pcoverage` remain
  reviewer/local-only (carried over from v7.2.0).
- No static analysis gate runs in CI following Qodana's removal in v8.2.0 — CodeQL (security) and JaCoCo
  (coverage) remain the only automated quality gates.

---

## 🔮 Future Enhancements

- Build a `MatchScoreService`/`ShooterLogService` (interface + `impl/` split) over the existing repositories,
  following the same phased pattern that closed Gap #1 and Gap #8.
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint —
  their `@JsonCreator` constructors and required-field enforcement are already correct and ready for this.
- Add a `build.yml` (or extend `codeql.yml`'s trigger set) running `./mvnw verify -Pcoverage` on push/PR, and a
  JaCoCo coverage-check rule wired into it, so a coverage regression fails the build automatically.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.3.0 is a scoped, single-domain feature release: it brings the match domain's bulk CSV import to parity
with the competitor domain's, established in v8.1.0, and closes the roadmap gap `ARCHITECTURE.md` had left
documenting that asymmetry. No schema, configuration or existing-endpoint changes are involved.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
