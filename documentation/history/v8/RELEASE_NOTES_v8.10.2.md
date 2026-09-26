# Release Notes – Version 8.10.2

**Release Date:** September 26, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Claude Code Review for Dependabot PRs**

Version 8.10.2 is a patch release, CI only. The automated Claude code review skipped every bot-authored pull request,
so the Dependabot PRs introduced in v8.10.1 — including the security updates that go straight into `main` — got no
review. It now reviews Dependabot's PRs too. Because v8.10.1 had already reached `main`, this ships as its own
release rather than being folded into it, per `AGENTS.md`'s rule that a released version is never changed.

---

## ⭐ Key Highlights

### 🤖 Dependabot PRs Reviewed

- `.github/workflows/claude-code-review.yml` gains `allowed_bots: 'dependabot'`
- Covers Dependabot's version-update PRs into `develop` and security-update PRs into `main`
- Limited to Dependabot rather than `'*'`, so no other bot can trigger the review

### 🔑 Dependabot Secret Required

- Workflow runs started by Dependabot can only read Dependabot secrets, not Actions secrets
- `CLAUDE_CODE_OAUTH_TOKEN` must be stored as a Dependabot secret too, or the review fails on Dependabot PRs

---

## 📦 What's New

### Changed

#### CI/CD & Configuration

- **`.github/workflows/claude-code-review.yml`:** The Claude code review now also runs on Dependabot's PRs
  (`allowed_bots: 'dependabot'`), which it previously skipped as bot-authored; `CLAUDE_CODE_OAUTH_TOKEN` must also be
  stored as a Dependabot secret. Documented in `ARCHITECTURE.md`'s CI/CD & Quality Gates section

#### Build & Metadata

- Project version bumped to **8.10.2** in `pom.xml`; `@OpenAPIDefinition` version updated to match

---

## 🚀 Migration Guide

No application migration is needed: no endpoint, JSON field, import format, configuration property or database
schema changed.

**Repository settings:** add `CLAUDE_CODE_OAUTH_TOKEN` under Settings → Secrets and variables → Dependabot, with the
same value as the existing Actions secret. Without it, the Claude review job fails on Dependabot PRs; the review is
advisory, so it doesn't block merging either way.

---

## 📊 Statistics

- **Total Commits:** 4 (the Dependabot review change, plus this release's version bump, release documentation and PR
  description commits)
- **Files Changed:** 10
- **Insertions:** 306 lines
- **Deletions:** 97 lines
- **Net Change:** +209 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **A shipped release stays shipped.** The change arrived after v8.10.1 had merged into `main`. Rewriting v8.10.1's
  notes to include it would make them describe something that was never released, so it ships as v8.10.2 instead.
- **Name the bot, don't wildcard it.** `'*'` would let any bot or GitHub App trigger a review — the action warns
  against it for exactly that reason. Only Dependabot's PRs needed covering, so only Dependabot is allowed.
- **Review what bypasses `develop` too.** Dependabot security PRs merge straight into `main` as hotfixes, skipping the
  `develop` stage where most review happens; an automated review there matters more, not less.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (970 tests, 0 failures/errors), unchanged from v8.10.1.
- `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing.
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

Version 8.10.2 changes nothing a client or deployment can see. It closes the gap v8.10.1 opened — Dependabot PRs
arriving without the automated review every other PR gets — once the Dependabot secret is in place.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
