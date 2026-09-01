# Release Notes – Version 8.1.1

**Release Date:** September 1, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**CI Static Analysis, Release-Process Self-Maintenance & Coverage Regression Fixes**

Version 8.1.1 is a process-and-quality patch release: no new domain feature, but a completed CI quality gate that
had sat configured but unwired since v8.0.0, a real test-coverage regression closed, and new tooling so the release
process keeps auditing its own roadmap documentation going forward. `.github/workflows/qodana.yml` finally wires up
Qodana static analysis; `NonFatalException`/`FatalException`/`ValidationException` regain the dedicated test suite
they lost somewhere after v7.2.0; and `update-improvement-plan-gaps`/`sync-improvement-plan-gaps` formalise the
manual roadmap-gap maintenance performed by hand across v8.0.0/v8.1.0.

---

## ⭐ Key Highlights

### 🔬 CI Static Analysis Completed

- **`.github/workflows/qodana.yml`** — runs `JetBrains/qodana-action` against the existing `qodana.yaml`
  configuration, triggered on push/PR to `develop`/`main`, mirroring `codeql.yml`'s trigger branches. Results
  upload as SARIF to GitHub code scanning alongside CodeQL, so no Qodana Cloud token or other secret is required

### 🛠️ Release-Process Self-Maintenance

- **`update-improvement-plan-gaps`/`sync-improvement-plan-gaps`** — two new Claude Code skills formalising the
  manual roadmap-gap-maintenance work performed by hand across v8.0.0/v8.1.0: a full codebase sweep for brand-new
  gaps, and a narrower, diff-driven check for gaps a branch's own work has closed or progressed
- **`generate-pr-description` renamed to `prep-version-release`** — better reflects what the skill actually does
  (the whole release-prep checklist, not just the PR description step); its new step 2 runs both gap-maintenance
  skills before any version-specific work begins
- **`AGENTS.md`'s Release Checklist** — re-synced against `prep-version-release`'s actual process, which had
  drifted ahead of it: three new steps (improvement-plan gap check, `[Unreleased]` completeness verification,
  conditional `CONTRIBUTING.md` update), described tool-agnostically

### 🐛 Coverage Regression Fixed

- **Root cause:** `NonFatalExceptionTest`/`FatalExceptionTest`/`ValidationExceptionTest` existed as of v7.2.0 but
  were dropped somewhere between then and now with no replacement, leaving the exception hierarchy at 20% line
  coverage
- Recreated all three, covering every constructor overload, and added new tests for the `models/ipsc/shared`
  scoring groundwork classes (0% coverage previously) and every `patchCompetitor`/`patchMatch` field's
  previously untested success path
- Full-suite coverage rose from 92.9%/93.4% to **98.34%/98.84%** (line/branch), 746 → 775 tests

### 📦 Dependency Clean-up

- **Spring Boot parent bumped `4.1.0` → `4.1.1`**, dropping the now-redundant `jackson-databind`/`log4j-api`
  `dependencyManagement` overrides
- The recurring dependency-currency check then caught a third redundant override, **`jackson-bom.version`**,
  confirmed against the parent POM directly rather than an echoed property

---

## 📦 What's New

### Added

#### CI/CD & Configuration

- `.github/workflows/qodana.yml`

#### Tooling

- `/update-improvement-plan-gaps`, `/sync-improvement-plan-gaps` Claude Code skills

#### Documentation

- `CLAUDE.md` — new "Working on Complex Tasks" section
- `CONTRIBUTING.md` — new "🗺️ Roadmap" section documenting `documentation/roadmap/improvement-plan.md`/
  `improvement-plan-tasks.md`'s structure and maintenance convention

#### Tests

- `NonFatalExceptionTest`, `FatalExceptionTest`, `ValidationExceptionTest`
- `IpscCommonScoreTest`, `IpscMatchScoreTest`, `IpscMatchStageScoreTest`
- `patchCompetitor`/`patchMatch` success-path additions to `IpscCompetitorServiceTest`/`IpscMatchServiceTest`

### Changed

#### Build & Metadata

- `pom.xml` — Spring Boot parent `4.1.0` → `4.1.1`; `jackson-databind`, `log4j-api` and `jackson-bom.version`
  `dependencyManagement`/property overrides removed as redundant; developer contact email corrected
- Project version bumped to 8.1.1 in `pom.xml` and the `@OpenAPIDefinition` annotation

#### Documentation

- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` — Gap #5 (`jackson-databind` override)
  closed; two new gaps added (match-scoring service/controller layer; Qodana CI wiring, partially progressed by
  this release's own workflow addition)
- `AGENTS.md`'s Release Checklist re-synced with `prep-version-release`'s actual process (see Key Highlights)

#### Tooling

- `.claude/skills/generate-pr-description` renamed to `prep-version-release`

---

## 🔄 Migration Guide

### For API Consumers

No API-facing changes in this release — every change is internal (CI configuration, tests, dependency management,
release-process tooling and documentation).

### For Developers

- The `generate-pr-description` Claude Code skill is now `prep-version-release` — update any local muscle memory or
  scripts referencing the old name.
- `pom.xml`'s `jackson-bom.version` property no longer exists; the resolved version (`3.1.5`) is unchanged, now
  inherited from the Spring Boot parent directly.

---

## 📊 Statistics

- **Total Commits:** 35
- **Files Changed:** 26
- **Insertions:** 1,684 lines
- **Deletions:** 246 lines
- **Net Change:** +1,438 lines
- **New Source Files:** 0
- **New Test Files:** 6
- **Deleted Test Files:** 0

---

## 🧭 Design Notes

- **A recurring check paid for itself within the same release.** Gap #5's "re-check dependency overrides at each
  release" task was written for `jackson-databind`/`log4j-api`, but running it fresh during this release's own
  gap-sync sweep caught a third instance (`jackson-bom.version`) that both previous passes had missed — direct
  validation that the recurring-check discipline, not just the one-off fix, is the actual payoff.
- **A silently deleted test suite is a real regression, not just a coverage number.** `NonFatalExceptionTest`/
  `FatalExceptionTest`/`ValidationExceptionTest` existed as of v7.2.0's own CHANGELOG entry, but nothing flagged
  their disappearance until a fresh coverage sweep this release. Recreating them closed a genuine gap in the
  exception hierarchy's safety net, not merely a cosmetic percentage.
- **Confirm against the source, not an echoed property.** `./mvnw help:evaluate` only echoes back whatever value a
  `pom.xml` property already has, even a redundant one — confirming `jackson-bom.version`'s redundancy required
  reading Spring Boot 4.1.1's own managed default directly from the parent POM in the local repository cache.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (775 tests, up from 746).
- New tests: `NonFatalExceptionTest`, `FatalExceptionTest`, `ValidationExceptionTest` (every constructor overload);
  `IpscCommonScoreTest`, `IpscMatchScoreTest`, `IpscMatchStageScoreTest` (handwritten all-args constructors);
  `patchCompetitor`/`patchMatch` success-path additions to `IpscCompetitorServiceTest`/`IpscMatchServiceTest`.
- Full suite re-verified after the `jackson-bom.version` override removal and the Spring Boot parent bump.

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- `.github/workflows/qodana.yml` hasn't yet been verified as succeeding in a real Actions run — this branch hasn't
  been pushed since the workflow was added.

---

## 🔮 Future Enhancements

- Verify `.github/workflows/qodana.yml` succeeds in CI, then drop `ARCHITECTURE.md`'s "no CI workflow wired up yet"
  caveat on the `Static Analysis` row.
- Add a `build.yml` (or extend `codeql.yml`'s trigger set) running `./mvnw verify -Pcoverage` on push/PR, and a
  JaCoCo coverage-check rule wired into it, so a coverage regression fails the build automatically.
- Build a `MatchScoreService`/`ShooterLogService` (interface + `impl/` split) over the existing repositories,
  following the same phased pattern that closed Gap #1.
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint —
  their `@JsonCreator` constructors and required-field enforcement are already correct and ready for this.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.1.1 completes a CI quality gate the project's own architecture documentation had flagged as configured
but unwired since v8.0.0, closes a real silent test-coverage regression from a deleted test suite and builds the
tooling for the release process to keep auditing its own roadmap documentation going forward; no new domain
feature, but meaningful process maturity ahead of the next feature release.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
