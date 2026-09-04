# Release Notes – Version 7.3.0

**Release Date:** August 25, 2026
**Status:** ✨ Stable

---

## 🎯 Theme

**Documentation Accuracy Pass & PR Summary Tooling**

Version 7.3.0 is a small, documentation-focused release. `README.md` and `ARCHITECTURE.md` had drifted from what the codebase actually does — describing match management, competitor/club CRUD, and WinMSS/XML processing as shipped features when only `AwardController`/`ImageController` CSV processing exists today, and claiming a "Build & Tests" CI gate that isn't wired up as a GitHub Actions trigger. Both are corrected in line with `CLAUDE.md`'s existing, accurate account of the codebase. Separately, a new `/generate-pr-summary` Claude Code command condenses an already-finalised `RELEASE_NOTES.md`/`PR_DESCRIPTION.md` pair into a short, plain PR summary for pasting into Bitbucket. No domain entities, repositories, services, or API surface changed in this release.

---

## ⭐ Key Highlights

### 📝 README.md & ARCHITECTURE.md Corrected to Match Actual Capabilities

- `README.md`'s Introduction and Features sections no longer describe match management, competitor tracking, club CRUD, WinMSS import, or XML/multi-format processing as existing capabilities — only `AwardController`/`ImageController` CSV processing is implemented today. The Introduction now names the JPA entities/repositories that exist for clubs, competitors, matches, match stages, and shooter logs as groundwork for a service/controller layer that is still being rebuilt.
- `README.md`'s test-coverage instructions corrected from `./mvnw test jacoco:report` (non-functional — JaCoCo only binds via the `coverage` Maven profile) to `./mvnw verify -Pcoverage`.
- The `1.x – 4.x` version range in `README.md`'s documentation-map description of `ARCHIVE.md` removed, per AGENTS.md's evergreen-documentation rule against version ranges as well as exact version numbers.
- `ARCHITECTURE.md`'s test package tree corrected — the nonexistent `domain/` test package removed, the missing `converters/`/`exceptions/` packages added.
- `ARCHITECTURE.md`'s CI/CD & Quality Gates table no longer claims the `Build & Tests` gate runs on "All PRs" via GitHub Actions — only `codeql.yml` is wired up as an automatic trigger; the table now reflects that `./mvnw test` is run locally/by reviewers before merge.

### 🤖 New `/generate-pr-summary` Command

- Condenses a version's archived `PR_DESCRIPTION_vX.Y.Z.md` and `RELEASE_NOTES_vX.Y.Z.md` into a short paragraph plus a capped bullet list, for pasting straight into a Bitbucket pull request description.
- Deliberately doesn't reproduce this repo's own emoji-heavy documentation style or `RELEASE_NOTES.md`'s full section-by-section detail — a distillation for a reviewer who won't read the full release notes, not a second copy of them.
- Its Output instructions were subsequently clarified to require the fenced block contain raw, unrendered Markdown source (literal `##`/`**`/`-`), not Claude's own rendered formatting, so a pasted-in preview shows the exact source.

---

## 📦 What's New

### Fixed

#### Documentation

- `README.md` — Introduction/Features capability claims, coverage-report command, stray version range
- `ARCHITECTURE.md` — test package tree, CI/CD & Quality Gates table

### Added

#### Tooling

- `.claude/commands/generate-pr-summary.md`

### Changed

#### Tooling

- `.claude/commands/generate-pr-summary.md` — Output instructions clarified to require raw, unrendered Markdown source in the fenced output block

---

## 🚀 Migration Guide

### For Deployers

- **No schema, dependency, or configuration changes in this release.** Documentation and tooling only.

### For Developers

- **No API, service, or domain-layer changes** — nothing to update in calling code.
- **Use `./mvnw verify -Pcoverage`**, not `./mvnw test jacoco:report`, to generate a JaCoCo coverage report — `README.md` previously documented the non-functional form.
- **New command:** `/generate-pr-summary <version>` condenses an already-finalised `RELEASE_NOTES_vX.Y.Z.md`/`PR_DESCRIPTION_vX.Y.Z.md` pair into a short PR summary; run it after `/generate-pr-description` has produced those files.

---

## 📊 Statistics

- **Total Commits:** 2
- **Files Changed:** 4 (plus this release's version bump and documentation files)

---

## 🧭 Design Notes

- **Correct the docs to match the code, not the other way round.** `CLAUDE.md` already gave an accurate account of what's implemented (`AwardController`/`ImageController` only, `IpscController` an empty stub); `README.md`/`ARCHITECTURE.md` had simply fallen out of sync with it. This release brings the evergreen docs back in line rather than rewriting `CLAUDE.md`.
- **A PR summary is a distillation, not a restatement.** `/generate-pr-summary` deliberately drops this repo's own emoji headings and per-area detail — its job is to save a reviewer from reading the full release notes, not to duplicate them.

---

## 🧪 Testing

- `./mvnw test` — full suite, unchanged, all passing (documentation-only change; no source under test was touched).
- Manually reviewed `README.md`/`ARCHITECTURE.md` against the current codebase (`AwardController`/`ImageController`, `IpscController` stub, `.github/workflows/codeql.yml`) to confirm every corrected claim.

---

## 🐛 Known Issues

- Carried over from v7.0.0–v7.2.0: no calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only.

---

## 🔮 Future Enhancements

- Carried over from v7.0.0–v7.2.0: `ShooterLogService` to compute/persist best-4 `ShooterLog` snapshots; controller/service endpoints exposing shooter logs.
- Rebuild the match/competitor service and controller layer that `README.md` now correctly describes as groundwork-only.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

This release is entirely a documentation-accuracy and tooling pass — no domain model, repository, or API surface changed. It corrects `README.md`/`ARCHITECTURE.md` claims that had drifted from what the codebase actually implements and adds a small Claude Code command for condensing release documentation into a PR summary.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
