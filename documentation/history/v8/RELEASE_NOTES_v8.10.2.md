# Release Notes – Version 8.10.2

**Release Date:** September 26, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Claude Code Review for Dependabot PRs & First Dependabot Updates**

Version 8.10.2 is a patch release. The automated Claude code review skipped every bot-authored pull request, so the
Dependabot PRs introduced in v8.10.1 — including the security updates that go straight into `main` — got no review.
It now reviews Dependabot's PRs too. Because v8.10.1 had already reached `main`, this ships as its own release rather
than being folded into it, per `AGENTS.md`'s rule that a released version is never changed. The release also carries
Dependabot's first three updates, and fixes the one that broke: a `flyway-mysql` bump that mixed two Flyway major
versions in the Flyway Maven plugin now follows Spring Boot's own Flyway version instead.

---

## ⭐ Key Highlights

### 🤖 Dependabot PRs Reviewed

- `.github/workflows/claude-code-review.yml` gains `allowed_bots: 'dependabot'`
- Covers Dependabot's version-update PRs into `develop` and security-update PRs into `main`
- Limited to Dependabot rather than `'*'`, so no other bot can trigger the review

### 🔑 Dependabot Secret Required

- Workflow runs started by Dependabot can only read Dependabot secrets, not Actions secrets
- `CLAUDE_CODE_OAUTH_TOKEN` must be stored as a Dependabot secret too, or the review fails on Dependabot PRs

### ⬆️ First Dependabot Updates

- GitHub Actions: `actions/checkout` `v7`, `actions/setup-java` `v6`, `actions/upload-artifact` `v7`
- Maven: `springdoc-openapi-bom` `3.1.1`, `jacoco-maven-plugin` `0.8.15`, Maven `3.9.16` via a regenerated wrapper
- `flyway-mysql` bumped to `13.7.0` against the `12.4.0` Flyway plugin — fixed by deriving it from `${flyway.version}`

---

## 📦 What's New

### Changed

#### CI/CD & Configuration

- **`.github/workflows/claude-code-review.yml`:** The Claude code review now also runs on Dependabot's PRs
  (`allowed_bots: 'dependabot'`), which it previously skipped as bot-authored; `CLAUDE_CODE_OAUTH_TOKEN` must also be
  stored as a Dependabot secret. Documented in `ARCHITECTURE.md`'s CI/CD & Quality Gates section
- **`actions/checkout`, `actions/setup-java`, `actions/upload-artifact`:** Bumped to `v7`, `v6` and `v7` across every
  workflow (Dependabot, #140)
- **`.github/workflows/dependency-submission.yml`:** The `chmod +x mvnw` step is gone — `mvnw` is now committed as
  executable

#### Build & Metadata

- **`springdoc-openapi-bom`, `jacoco-maven-plugin`, Maven wrapper:** Bumped to `3.1.1`, `0.8.15` and Maven `3.9.16`,
  with a regenerated `mvnw`/`mvnw.cmd` (Dependabot, #138)
- Project version bumped to **8.10.2** in `pom.xml`; `@OpenAPIDefinition` version updated to match

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

**Repository settings:** add `CLAUDE_CODE_OAUTH_TOKEN` under Settings → Secrets and variables → Dependabot, with the
same value as the existing Actions secret. Without it, the Claude review job fails on Dependabot PRs; the review is
advisory, so it doesn't block merging either way.

**Local builds:** `./mvnw` downloads Maven `3.9.16` on first use. Local `./mvnw flyway:*` commands work against the
matching Flyway `12.4.0` again. The Maven updates (#138) and the mismatched `flyway-mysql` `13.7.0` (#139) had
already reached `main` through #142, a `develop` promotion without a version bump or release notes; this release
documents them and restores the matching Flyway version there.

---

## 📊 Statistics

- **Total Commits:** 8 (the Dependabot review change, Dependabot's GitHub Actions update, the Flyway fix, plus this
  release's version bump, two release documentation and PR description commits)
- **Files Changed:** 15
- **Insertions:** 630 lines
- **Deletions:** 297 lines
- **Net Change:** +333 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **Note:** Dependabot's Maven updates (#138, #139) reached `main` through #142 before this release, so they are
  documented here but not counted in these figures
- **New Test Files:** 0

---

## 🧭 Design Notes

- **A shipped release stays shipped.** The change arrived after v8.10.1 had merged into `main`. Rewriting v8.10.1's
  notes to include it would make them describe something that was never released, so it ships as v8.10.2 instead.
- **Name the bot, don't wildcard it.** `'*'` would let any bot or GitHub App trigger a review — the action warns
  against it for exactly that reason. Only Dependabot's PRs needed covering, so only Dependabot is allowed.
- **Derive, don't pin.** `pom.xml` already warned that `flyway-mysql` had to be kept in sync with Spring Boot by
  hand — and the first automated update broke that sync. Deriving the version from the parent's `${flyway.version}`
  removes the thing that needed keeping in sync, rather than asking Dependabot to leave it alone.
- **Review what bypasses `develop` too.** Dependabot security PRs merge straight into `main` as hotfixes, skipping the
  `develop` stage where most review happens; an automated review there matters more, not less.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (970 tests, 0 failures/errors) on Maven 3.9.16, JaCoCo 0.8.15 and springdoc
  3.1.1, unchanged from v8.10.1.
- `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing.
- The effective POM resolves the Flyway plugin's `flyway-mysql` to `12.4.0`, matching the plugin.
- `claude-code-review.yml` validated as YAML; `allowed_bots` matching confirmed against `claude-code-action`'s actor
  check, which strips the `[bot]` suffix so `dependabot` matches `dependabot[bot]`.

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

Leoni Lubbinge

---

## 📝 Notes

Version 8.10.2 changes nothing a client can see beyond springdoc's patch update to the generated API docs. It closes
the gap v8.10.1 opened — Dependabot PRs arriving without the automated review every other PR gets — once the
Dependabot secret is in place, and shows why its first updates still need a human eye: one of the three broke a
version sync that nothing in CI exercises.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
