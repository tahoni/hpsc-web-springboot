# Release Notes – Version 8.11.1

**Release Date:** September 27, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Docker Image Build in CI**

Version 8.11.1 is a patch release. v8.11.0 added a `Dockerfile`, but no CI workflow built it, and the release notes
recorded that as a Known Issue: a change that broke the image would only surface the next time someone built it by
hand or deployed it. A new GitHub Actions workflow now builds the image on every push and pull request to `main` and
`develop`, so a broken `Dockerfile` fails CI like any other regression. The release also tidies one sub-heading in
v8.11.0's changelog and release notes, and records the Docker gap in the improvement plan as Gap #31, closed here.

---

## ⭐ Key Highlights

### 🔬 Docker Image Built in CI

- New `.github/workflows/docker.yml` builds the `Dockerfile` on every push and PR to `main`/`develop`
- Build only, never pushed — the image is checked, not published, so the workflow needs only read access
- Layers are reused across runs through the GitHub Actions cache; tests stay in `build.yml`

### 🛤️ Roadmap

- Gap #31 (CI never built the Docker image) recorded and closed, leaving Gap #6 as the only open gap

---

## 📦 What's New

### Added

#### CI/CD & Configuration

- **`.github/workflows/docker.yml`:** Builds the `Dockerfile` with `docker/build-push-action` on every push and PR to
  `main`/`develop`, so a change that breaks the image fails CI instead of surfacing at deployment — the Known Issue
  v8.11.0 recorded. The image is never pushed, and layers are reused through the GitHub Actions cache. Listed in
  `ARCHITECTURE.md`'s CI/CD & Quality Gates table and `CONTRIBUTING.md`'s summary of it

#### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #31 — CI never built the Docker image, with no gap
  tracking it — recorded and closed within this release

### Changed

#### Documentation

- **`CHANGELOG.md`, `RELEASE_NOTES.md`, `RELEASE_NOTES_v8.11.0.md`:** v8.11.0's Flyway fix is now filed under the
  existing `Build & Metadata` area rather than a near-duplicate `Build & Configuration` sub-heading

#### Build & Metadata

- Project version bumped to **8.11.1** in `pom.xml`; `@OpenAPIDefinition` version updated to match

---

## 🚀 Migration Guide

No action needed. No endpoint, JSON field, import format, configuration property or schema changed; the new workflow
runs only in GitHub Actions and changes nothing about how the application is built, run or deployed.

---

## 📊 Statistics

- **Total Commits:** 8 (the Docker CI workflow and the v8.11.0 sub-heading fix, plus this release's Gap #31, version
  bump, release documentation and PR description commits, and two recording the local and CI image builds)
- **Files Changed:** 14
- **Insertions:** 438 lines
- **Deletions:** 138 lines
- **Net Change:** +300 lines
- **New Source Files:** 1 (`.github/workflows/docker.yml`)
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Build, don't publish.** Publishing an image isn't a stated goal, so the workflow sets `push: false` and runs with
  read-only `contents` permission — no registry credentials, and nothing a pull request can push.
- **No second test run.** The `Dockerfile` skips tests, and the workflow doesn't add them back: `build.yml` already
  runs the full suite with coverage on the same triggers, so the image build only has to prove the image builds.
- **Cached layers.** `cache-from`/`cache-to` with `type=gha,mode=max` keep unchanged dependency layers between runs,
  which the `Dockerfile`'s layer order (dependencies resolved before `src/` is copied) is built to exploit.
- **A PATCH, not a MINOR.** The new workflow is CI tooling with no externally visible behaviour change, which
  `AGENTS.md`'s Semantic Versioning rules classify as a PATCH.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` — full suite passing (970 tests, 0 failures/errors/skipped), unchanged from v8.11.0;
  98.77% line / 99.09% branch coverage, JaCoCo gate passing.
- `docker build --no-cache -t hpsc-web .` — the image builds locally from the release branch, running as the non-root
  `hpsc` user with an `Implementation-Version` of 8.11.1.
- The new `Docker Image` workflow passed on this release's own pull request (#150), its first run in CI.

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
- The Claude code review on Dependabot PRs fails until `CLAUDE_CODE_OAUTH_TOKEN` is also stored as a Dependabot
  secret, if not already done for v8.10.2.

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

Version 8.11.1 changes nothing that runs in production. It closes the one Known Issue v8.11.0 introduced, so the
Docker image now gets the same automatic CI gate as the Maven build.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
