# Release Notes – Version 8.10.3

**Release Date:** September 26, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Dependabot's First Updates & Flyway Plugin Version Fix**

Version 8.10.3 is a patch release. Dependabot's first three pull requests updated the GitHub Actions every workflow
uses, springdoc, JaCoCo and Maven itself — and one of them broke something CI never exercises: it bumped the Flyway
Maven plugin's `flyway-mysql` dependency to `13.7.0` while the plugin stayed on Spring Boot's `12.4.0`, mixing two
Flyway major versions. That dependency now follows Spring Boot's own `${flyway.version}`, so it can no longer drift.
Two of the three updates reached `main` through #142 before any release notes described them; this release documents
all three.

---

## ⭐ Key Highlights

### ⬆️ Dependabot's First Updates

- GitHub Actions: `actions/checkout` `v7`, `actions/setup-java` `v6`, `actions/upload-artifact` `v7` (#140)
- Maven: `springdoc-openapi-bom` `3.1.1`, `jacoco-maven-plugin` `0.8.15`, Maven `3.9.16` via a regenerated wrapper
  (#138)
- `mvnw` is now committed as executable, so `dependency-submission.yml` no longer needs its `chmod` step

### 🛠️ Flyway Plugin Version Fix

- Dependabot's `flyway-mysql` `13.7.0` bump (#139) mixed two Flyway majors with the `12.4.0` Flyway Maven plugin
- It now uses `${flyway.version}`: plugin dependencies don't inherit Spring Boot's `dependencyManagement` but do
  inherit its properties
- No hand-pinned version is left to keep in sync, and nothing for Dependabot to bump out of step

---

## 📦 What's New

### Changed

#### CI/CD & Configuration

- **`actions/checkout`, `actions/setup-java`, `actions/upload-artifact`:** Bumped to `v7`, `v6` and `v7` across every
  workflow (Dependabot, #140)
- **`.github/workflows/dependency-submission.yml`:** The `chmod +x mvnw` step is gone — `mvnw` is now committed as
  executable

#### Build & Metadata

- **`springdoc-openapi-bom`, `jacoco-maven-plugin`, Maven wrapper:** Bumped to `3.1.1`, `0.8.15` and Maven `3.9.16`,
  with a regenerated `mvnw`/`mvnw.cmd` (Dependabot, #138)
- Project version bumped to **8.10.3** in `pom.xml`; `@OpenAPIDefinition` version updated to match

### Fixed

#### Build & Metadata

- **`flyway-maven-plugin`'s `flyway-mysql` dependency:** Dependabot's `13.7.0` bump (#139) mixed two Flyway majors
  with the plugin's Spring Boot-managed `12.4.0`; it now uses `${flyway.version}`, inherited from the parent, so it
  always matches the plugin and can't drift again
- **`mvnw.cmd`:** Renormalised to the line endings `.gitattributes` specifies after the wrapper update

---

## 🚀 Migration Guide

No application migration is needed: no endpoint, JSON field, import format, configuration property or database
schema changed.

**Local builds:** `./mvnw` downloads Maven `3.9.16` on first use. Local `./mvnw flyway:*` commands work against a
matching Flyway `12.4.0` again — `main` has carried the mismatched `flyway-mysql` `13.7.0` since #142, and this
release restores the match there.

---

## 📊 Statistics

- **Total Commits:** 9 (v8.10.2's four release commits and Dependabot's GitHub Actions update, plus this release's
  Flyway fix, version bump, release documentation and PR description commits)
- **Files Changed:** 17
- **Insertions:** 831 lines
- **Deletions:** 302 lines
- **Net Change:** +529 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0
- **Note:** measured against `main`, which already holds #138/#139 through #142 but not v8.10.2's release commits
  (#141), so these figures include v8.10.2's Dependabot review change and documentation as well

---

## 🧭 Design Notes

- **Derive, don't pin.** `pom.xml` already warned that `flyway-mysql` had to be kept in sync with Spring Boot by
  hand — and the first automated update broke that sync. Deriving the version from the parent's `${flyway.version}`
  removes the thing that needed keeping in sync, rather than asking Dependabot to leave it alone.
- **Automated updates still need a reviewer.** All three Dependabot PRs passed CI. The broken one only affects the
  Flyway Maven plugin, which no CI job runs — a green build said nothing about it.
- **Document what shipped, even late.** #138 and #139 reached `main` without release notes. Rather than rewrite an
  earlier release's notes, this release records them, per `AGENTS.md`'s rule that a released version is never changed.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (970 tests, 0 failures/errors) on Maven 3.9.16, JaCoCo 0.8.15 and springdoc
  3.1.1, unchanged from v8.10.2.
- `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing.
- The effective POM resolves the Flyway plugin's `flyway-mysql` to `12.4.0`, matching the plugin.
- `dependency-submission.yml` validated as YAML after dropping the `chmod` step.

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
- GitHub's built-in automatic dependency submission is a repository setting and must be turned off by hand, if not
  already done for v8.10.1.
- The Claude code review on Dependabot PRs fails until `CLAUDE_CODE_OAUTH_TOKEN` is also stored as a Dependabot
  secret — see the Migration Guide.

---

## 🔮 Future Enhancements

- Build a `MatchScoreService`/`ShooterLogService` over the existing repositories, committing through
  `TransactionService`, following the same phased pattern that closed Gap #1 (Gap #6).
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint.
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.25` or later (Gap #26).
- Consider enforcing a `BRANCH`-level JaCoCo minimum alongside the existing `LINE` one.

---

## 👥 Contributors

- Leoni Lubbinge
- dependabot[bot]

---

## 📝 Notes

Version 8.10.3 changes nothing a client can see beyond springdoc's patch update to the generated API docs. It is the
first release built from Dependabot's own pull requests, and it shows why they still get a human review: one of three
broke a version sync that nothing in CI exercises.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
