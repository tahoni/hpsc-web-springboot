# Release Notes – Version 8.3.1

**Release Date:** September 2, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**CI Build/Test Gate, Coverage Enforcement & CSV Persistence Clarity**

Version 8.3.1 is a process-and-quality patch release: pull requests to `main`/`develop` now run a real, automatic
build/test gate for the first time — new `.github/workflows/build.yml` runs `./mvnw verify -Pcoverage` on push/PR,
mirroring `codeql.yml`'s trigger branches. Wired into that same run is the project's first coverage-regression rule:
a new JaCoCo `check` execution enforces a `BUNDLE`-level `LINE`/`COVEREDRATIO` minimum, initially `0.51` (51%) as a
deliberate low-regression backstop, then raised to `0.86` (86%) within the same branch — still short of the real
baseline, which a fresh `./mvnw verify -Pcoverage` run measured at 98.16%/98.94% (line/branch), 836 tests. Also
confirmed and documented: `AwardService.createAwards()`/`ImageService.createImages()` CSV processing is
intentionally stateless by design, not an unfinished persistence layer. This closes
`documentation/roadmap/improvement-plan.md`'s Gap #2 (no automatic build/test gate) and Gap #3 (CSV persistence
ambiguity), and partially progresses Gap #4 (coverage measured but not enforced).

---

## ⭐ Key Highlights

### 🔬 Automatic Build/Test Gate

- New `.github/workflows/build.yml` triggers on push/PR to `main`/`develop`, mirroring `codeql.yml`'s trigger
  branches — sets up JDK 25 via `actions/setup-java` (Maven-cached), builds/tests via `sh ./mvnw` (`mvnw` isn't
  tracked with the execute bit in git, so it must be invoked through `sh`) and uploads the JaCoCo HTML/XML report as
  a build artefact
- Closes Gap #2: the `Build & Tests` gate no longer runs only "locally / by reviewers before merge"

### 🛡️ First Coverage-Regression Rule

- New `jacoco-maven-plugin` `check` execution in `pom.xml`'s `coverage` profile enforces a minimum line-coverage
  ratio at the `BUNDLE` level, wired into the new CI gate so a real coverage regression fails the build
- Set initially to `0.51` (51%) as a low-regression backstop, then raised to `0.86` (86%) within the same branch —
  still below the actual ~98% baseline, so tightening further remains a documented follow-up

### ✅ Award/Image CSV Processing Confirmed Stateless by Design

- `AwardService.createAwards()`/`ImageService.createImages()` never persist parsed CSV rows — confirmed as
  intentional design, not an unfinished persistence layer, and now stated explicitly in `README.md`/`ARCHITECTURE.md`
- Closes Gap #3, which had tracked this as an open question since v8.1.0

---

## 📦 What's New

### Added

#### CI/CD

- **`.github/workflows/build.yml`:** New workflow runs `./mvnw verify -Pcoverage` on push/PR to `main`/`develop`,
  mirroring `codeql.yml`'s trigger branches — sets up JDK 25 via `actions/setup-java` (Maven-cached), builds/tests
  via `sh ./mvnw` and uploads the JaCoCo HTML/XML report as a build artefact. Closes Gap #2

### Changed

#### Configuration

- **`pom.xml`:** New `jacoco-maven-plugin` `check` execution in the `coverage` profile enforces a `BUNDLE`-level
  `LINE`/`COVEREDRATIO` minimum, initially `0.51` (51%) as a deliberately low regression backstop, then raised to
  `0.86` (86%) within the same branch, wired into `build.yml`'s CI gate so a coverage regression fails the build —
  still short of the ~98% real baseline. Partially progresses Gap #4

#### Documentation

- **`README.md`/`ARCHITECTURE.md`:** Confirmed `AwardService.createAwards()`/`ImageService.createImages()` CSV
  processing is intentionally stateless by design, not an unfinished persistence layer — closes Gap #3
- **`ARCHITECTURE.md`/`CONTRIBUTING.md`:** CI/CD & Quality Gates tables updated to reflect the new `build.yml` gate
  and JaCoCo coverage-check rule, dropping the stale "locally / by reviewers"/"All PRs" language
- **`documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md`:** Gap #2 closed in v8.3.1; Gap #3
  closed in v8.3.1; Gap #4 marked partially progressed in v8.3.1, noting the refreshed coverage baseline
  (98.16%/98.94% line/branch, 836 tests) and the JaCoCo floor tightened twice within the same branch (51% → 86%)
- **`HISTORY.md`:** New Historical Timeline entry, Phase 24 and Milestone 24 for v8.3.1; the previously stale
  coverage figure (98.34%/98.84% at v8.1.1, unrefreshed across v8.2.0/v8.3.0) is now current

### Fixed

- **`ARCHITECTURE.md`/`documentation/roadmap/improvement-plan.md`:** Corrected stale `processCsv()` method
  references (renamed to `createAwards()`/`createImages()` in v8.0.0) in the Award/Image CSV Processing Flow
  diagram and Gap #3's Evidence text
- **`AwardControllerTest`/`ImageControllerTest`:** Corrected stale `// processCsv()` test-grouping comments to
  `// createAwards()`/`// createImages()`, matching the same v8.0.0 rename

---

## 🔄 Migration Guide

### For API Consumers

- No API changes — this release is CI/build-tooling only.

### For Developers

- Any branch pushed, or PR opened, against `main`/`develop` now automatically runs the full test suite plus a
  coverage check via GitHub Actions; a coverage regression below 86% line coverage fails CI. No local workflow
  change is required — `./mvnw verify -Pcoverage` behaves exactly as it always has locally.
- `AwardService.createAwards()`/`ImageService.createImages()` remain intentionally stateless — this release only
  documents that design decision explicitly, it does not change their behaviour.

---

## 📊 Statistics

- **Total Commits:** 18
- **Files Changed:** 13
- **Insertions:** 361 lines
- **Deletions:** 80 lines
- **Net Change:** +281 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **A regression floor tightened in two steps, still short of the real baseline.** The 51% minimum landed first as a
  backstop against a severe regression, then was raised to 86% within the same branch — closer to the real ~98%
  baseline but deliberately not at it yet, since the new threshold hasn't run in CI to confirm it holds cleanly.
- **Coverage-check wired into the same execution a contributor already runs locally.** The `check` goal was added to
  the existing `coverage` profile's `verify` phase rather than a separate profile, so CI enforces exactly what
  `./mvnw verify -Pcoverage` already reproduces locally.
- **Refresh the real baseline once, in the same release that adds enforcement.** Rather than leave `HISTORY.md`'s
  last-recorded coverage figure (98.34%/98.84% at v8.1.1) to keep drifting silently, this release re-measures and
  records the current baseline (98.16%/98.94%, 836 tests) alongside the new CI rule that will catch regressions from
  here on.
- **Resolve an open design question rather than leave it implicit.** Gap #3 asked whether `AwardService`/
  `ImageService`'s stateless CSV processing was deliberate or an oversight; rather than leave that answer implied
  only by contrast with the competitor/match bulk-import flows, this release states it directly in `README.md`/
  `ARCHITECTURE.md`.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` — full suite passing (836 tests, 0 failures/errors); line coverage 98.16% (1,068/1,088
  lines covered), branch coverage 98.94% (279/282 branches covered).
- No dedicated new unit/integration test coverage was added for this release — it is CI/build-tooling and
  documentation work only.

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- The enforced coverage floor (86%) is still below the actual ~98% baseline — a real regression that drops coverage
  below the real baseline but stays above 86% would not yet fail CI; tightening the threshold further is a
  documented follow-up. The 86% threshold itself hasn't yet run in CI to confirm it holds cleanly.

---

## 🔮 Future Enhancements

- Confirm the new 86% JaCoCo line-coverage floor holds cleanly in CI, then continue tightening it closer to the
  real ~98% baseline.
- Build a `MatchScoreService`/`ShooterLogService` (interface + `impl/` split) over the existing repositories,
  following the same phased pattern that closed Gap #1 and Gap #8.
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint — their
  `@JsonCreator` constructors and required-field enforcement are already correct and ready for this.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.3.1 is a process/tooling and documentation-clarity release: no domain feature or API change, but a real,
previously missing CI safety net (automatic build/test gate), the project's first automated coverage-regression
check and a resolved design ambiguity around Award/Image CSV persistence. Closes
`documentation/roadmap/improvement-plan.md`'s Gap #2 and Gap #3, and partially progresses Gap #4 — carried since
v7.2.0/v8.0.0/v8.1.0 respectively.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
