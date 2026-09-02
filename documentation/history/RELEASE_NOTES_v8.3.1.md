# Release Notes – Version 8.3.1

**Release Date:** September 2, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**CI Build/Test Gate & Coverage Enforcement**

Version 8.3.1 is a process-and-quality patch release: pull requests to `main`/`develop` now run a real, automatic
build/test gate for the first time — new `.github/workflows/build.yml` runs `./mvnw verify -Pcoverage` on push/PR,
mirroring `codeql.yml`'s trigger branches. Wired into that same run is the project's first coverage-regression rule:
a new JaCoCo `check` execution enforces a `BUNDLE`-level `LINE`/`COVEREDRATIO` minimum of `0.51` (51%) — deliberately
a low regression backstop rather than a threshold near the real baseline, which a fresh `./mvnw verify -Pcoverage`
run measured at 98.16%/98.94% (line/branch), 836 tests. This closes `documentation/roadmap/improvement-plan.md`'s
Gap #2 (no automatic build/test gate) and partially progresses Gap #4 (coverage measured but not enforced).

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
  ratio of `0.51` (51%) at the `BUNDLE` level, wired into the new CI gate so a real coverage regression fails the
  build
- Deliberately set far below the actual ~98% baseline — a backstop against a severe regression, not a strict
  threshold that could block merges on day one; tightening it closer to the real baseline is a documented follow-up
  once the gate has run cleanly for a few releases

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
  `LINE`/`COVEREDRATIO` minimum of `0.51` (51%), wired into `build.yml`'s CI gate so a coverage regression fails the
  build — a deliberately low regression backstop, not a threshold near the current ~98% baseline. Partially
  progresses Gap #4

#### Documentation

- **`ARCHITECTURE.md`/`CONTRIBUTING.md`:** CI/CD & Quality Gates tables updated to reflect the new `build.yml` gate
  and JaCoCo coverage-check rule, dropping the stale "locally / by reviewers"/"All PRs" language
- **`documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md`:** Gap #2 closed in v8.3.1; Gap #4
  marked partially progressed in v8.3.1, noting the refreshed coverage baseline (98.16%/98.94% line/branch, 836
  tests)
- **`HISTORY.md`:** New Historical Timeline entry, Phase 24 and Milestone 24 for v8.3.1; the previously stale
  coverage figure (98.34%/98.84% at v8.1.1, unrefreshed across v8.2.0/v8.3.0) is now current

---

## 🔄 Migration Guide

### For API Consumers

- No API changes — this release is CI/build-tooling only.

### For Developers

- Any branch pushed, or PR opened, against `main`/`develop` now automatically runs the full test suite plus a
  coverage check via GitHub Actions; a coverage regression below 51% line coverage fails CI. No local workflow
  change is required — `./mvnw verify -Pcoverage` behaves exactly as it always has locally.

---

## 📊 Statistics

- **Total Commits:** 7 (plus this release-prep documentation pass, committed separately)
- **Files Changed:** 9
- **Insertions:** 269 lines
- **Deletions:** 62 lines
- **Net Change:** +207 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **A deliberately low regression floor, not a strict gate.** The 51% minimum is a backstop against a severe
  regression, not a threshold set "near" the real ~98% baseline — a strict day-one threshold risked blocking merges
  on legitimate work before the gate had proven itself reliable across a few releases.
- **Coverage-check wired into the same execution a contributor already runs locally.** The `check` goal was added to
  the existing `coverage` profile's `verify` phase rather than a separate profile, so CI enforces exactly what
  `./mvnw verify -Pcoverage` already reproduces locally.
- **Refresh the real baseline once, in the same release that adds enforcement.** Rather than leave `HISTORY.md`'s
  last-recorded coverage figure (98.34%/98.84% at v8.1.1) to keep drifting silently, this release re-measures and
  records the current baseline (98.16%/98.94%, 836 tests) alongside the new CI rule that will catch regressions from
  here on.

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
- The enforced coverage floor (51%) is deliberately far below the actual ~98% baseline — a real regression that
  drops coverage below the real baseline but stays above 51% would not yet fail CI; tightening the threshold is a
  documented follow-up once the gate has run cleanly for a few releases.

---

## 🔮 Future Enhancements

- Tighten the JaCoCo line-coverage floor closer to the real ~98% baseline once the new CI gate has run cleanly
  across a few releases.
- Build a `MatchScoreService`/`ShooterLogService` (interface + `impl/` split) over the existing repositories,
  following the same phased pattern that closed Gap #1 and Gap #8.
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint — their
  `@JsonCreator` constructors and required-field enforcement are already correct and ready for this.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.3.1 is a process/tooling release: no domain feature or API change, but a real, previously-missing CI
safety net (automatic build/test gate) and the project's first automated coverage-regression check, closing
`documentation/roadmap/improvement-plan.md`'s Gap #2 and partially progressing Gap #4 — both carried since v7.2.0/
v8.0.0.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
