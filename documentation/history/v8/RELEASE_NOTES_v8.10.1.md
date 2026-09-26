# Release Notes – Version 8.10.1

**Release Date:** September 26, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Explicit Dependency Submission & Dependabot Configuration**

Version 8.10.1 is a patch release, CI and documentation only. The one check on a `develop`→`main` pull request that
had no workflow file behind it — GitHub's built-in Maven dependency submission — is replaced by an explicit workflow
that uses the project's own JDK and Maven wrapper. Dependabot version updates are configured for Maven and GitHub
Actions, targeting `develop`, and Dependabot's security-update PRs — which always target `main` — are given a place
in the GitFlow branching model as hotfixes. The release's own improvement-plan sweep recorded that branching conflict
as Gap #29 and closed it, leaving Gap #6 the only open gap.

---

## ⭐ Key Highlights

### 📦 Explicit Dependency Submission

- New `.github/workflows/dependency-submission.yml` submits the Maven dependency graph on push to `main`/`develop`
  or manual dispatch
- Resolves dependencies with the project's JDK 25 and Maven wrapper, where GitHub's built-in submission used JDK 21
  and ignored the wrapper
- GitHub's built-in automatic dependency submission must be turned off in the repository's Code security settings,
  or both will submit snapshots

### 🤖 Dependabot

- New `.github/dependabot.yml`: weekly Maven and GitHub Actions version updates, opened against `develop`
- Maven minor/patch bumps grouped into one PR, all GitHub Actions bumps into another; major Maven bumps get a PR each
- Security-update PRs, which always target `main`, are handled as hotfixes — merged into `main`, then carried into
  `develop`

### 🛤️ Roadmap

- Gap #29 recorded and closed
- Only Gap #6 — the match scoring / shooter-log service and controller layer — remains open; Gap #26 still waits on
  a Spring Boot release

---

## 📦 What's New

### Added

#### CI/CD & Configuration

- **`.github/workflows/dependency-submission.yml`:** New explicit Maven dependency-submission workflow, replacing
  GitHub's built-in "Automatic Dependency Submission (Maven)"; documented in `ARCHITECTURE.md`'s CI/CD & Quality
  Gates table and `CONTRIBUTING.md`
- **`.github/dependabot.yml`:** New Dependabot version-update configuration — weekly, grouped Maven and GitHub
  Actions updates targeting `develop`; added to `ARCHITECTURE.md`'s Project Structure tree

#### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** Gap #29 recorded — Dependabot security-update PRs target
  `main`, bypassing the GitFlow rule that only `develop` and `hotfix/*` reach it

### Changed

#### Documentation

- **`AGENTS.md`, `CONTRIBUTING.md`, `.github/dependabot.yml`:** Dependabot security-update PRs are handled as
  hotfixes — merged into `main`, then carried into `develop` by merging `main` back into it, since Dependabot deletes
  its branch after merging; a new `dependabot/*` entry separates them from version-update PRs, which target `develop`
  like any `feature/*` PR (Gap #29)

#### Build & Metadata

- Project version bumped to **8.10.1** in `pom.xml`; `@OpenAPIDefinition` version updated to match

---

## 🚀 Migration Guide

No application migration is needed: no endpoint, JSON field, import format, configuration property or database
schema changed.

**Repository settings:** once this release reaches `main`, turn off Settings → Code security → Dependency graph →
"Automatic dependency submission", so GitHub's built-in submission and the new workflow don't both submit snapshots.

**Dependabot:** it reads `.github/dependabot.yml` from the default branch, so weekly version-update PRs start once
this release is on `main`. Expect a PR proposing `tomcat.version` `11.0.26` — merging it is fine, but removing the
override altogether (Gap #26) still waits on a Spring Boot release that manages Tomcat `11.0.25` or later.

**Contributors:** a Dependabot security-update PR is merged into `main` like a `hotfix/*`, then `main` is merged back
into `develop` — see `CONTRIBUTING.md`'s Merging section.

---

## 📊 Statistics

- **Total Commits:** 6 (the dependency-submission workflow, the Dependabot configuration and the Gap #29 branching
  rule, plus this release's version bump, release documentation and PR description commits)
- **Files Changed:** 15
- **Insertions:** 582 lines
- **Deletions:** 135 lines
- **Net Change:** +447 lines
- **New Source Files:** 0
- **New Configuration Files:** 2 (`.github/workflows/dependency-submission.yml`, `.github/dependabot.yml`)
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Every check in version control.** A check that GitHub runs from a hidden, dynamic workflow can't be reviewed,
  pinned or changed in a PR. The explicit workflow makes dependency submission as visible as the build and CodeQL.
- **Resolve dependencies the way the build does.** The built-in submission used JDK 21 and the runner's own Maven;
  the project builds with JDK 25 and its Maven wrapper. The explicit workflow matches the build, so the dependency
  graph describes what actually ships.
- **Security fixes are hotfixes.** GitHub always opens Dependabot security PRs against the default branch, so the
  choice was between fighting that — retargeting each PR, or making `develop` the default — and treating them like
  the urgent production fixes they are. Handling them as hotfixes ships the fix at once and keeps `develop` in step.
- **Group the routine, isolate the risky.** Minor and patch bumps are grouped to keep PR noise down; major Maven bumps
  arrive one at a time, since those are the ones the Semantic Versioning rules say may break things.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (970 tests, 0 failures/errors), unchanged from v8.10.0.
- `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing.
- `.github/workflows/dependency-submission.yml` and `.github/dependabot.yml` validated as YAML; the workflow runs for
  the first time on the push that lands this release on `develop`.

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- A competitor or match referenced by results or shooter logs can't be deleted through the API, since no endpoint
  removes those rows yet (carried over from v8.8.0, pending Gap #6).
- The `BRANCH` coverage counter is still not separately enforced by the JaCoCo `check` execution — only `LINE` is.
- `pom.xml` still overrides `tomcat.version` to `11.0.25` for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26).
- GitHub's built-in automatic dependency submission is a repository setting and must be turned off by hand — see the
  Migration Guide.

---

## 🔮 Future Enhancements

- Build a `MatchScoreService`/`ShooterLogService` over the existing repositories, committing through
  `TransactionService`, following the same phased pattern that closed Gap #1 (Gap #6).
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint.
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.25` or later (Gap #26).
- Consider enforcing a `BRANCH`-level JaCoCo minimum alongside the existing `LINE` one.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.10.1 changes nothing a client or deployment can see. It moves the repository's dependency tooling out of
GitHub's defaults and into version control, and fits Dependabot into the branching model the rest of the project
already follows.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
