# Release Notes – Version 7.4.0

**Release Date:** August 29, 2026
**Status:** ✨ Stable

---

## 🎯 Theme

**IPSC Request DTOs, Route Cleanup & Documentation Conventions**

Version 7.4.0 is a mixed release. It lays down a new `models/ipsc/request`/`models/ipsc/shared` package of DTOs — `MatchRequest`, `MatchStageRequest`, `MatchStagesRequest`, `MatchOverallResultRequest`, `MatchStageResultRequest` (plus CSV variants) and the shared `IpscCommonScore`/`IpscMatchScore`/`IpscMatchStageScore` — as groundwork for the IPSC module rebuild; these are not yet wired to `IpscController`, which remains an empty stub. Separately, `AwardController`/`ImageController` drop their unused `/v1` route prefix, and a round of documentation-convention tightening (a new Serial commas rule, and a British English rule that now covers code identifiers) is applied across the existing documentation set. A new `/sync-unreleased-changes` Claude Code command and a handful of release-hygiene fixes round out the release.

---

## ⭐ Key Highlights

### 📦 New IPSC Request DTOs

- `za.co.hpsc.web.models.ipsc.request`: `MatchRequest`/`MatchStageRequest`/`MatchStagesRequest` for match/stage submission, `MatchOverallResultRequest`/`MatchStageResultRequest` for competitor result submission and `MatchOverallResultRequestForCSV`/`MatchStageResultRequestForCSV` abstract CSV variants — all shaped to match Practiscore's export format.
- `MatchRequest` gains a `matchId` field so an existing match can be updated, not just created.
- `za.co.hpsc.web.models.ipsc.shared`: `IpscCommonScore` (fields shared by Comstock-scored, hit-factor IPSC results), `IpscMatchScore` (adds `percentageOfPossiblePoints`) and `IpscMatchStageScore` (adds `rawPoints`/`hitFactor`).
- All new DTOs carry field- and class-level Javadoc documenting how Comstock scoring works. This is deliberate domain-layer groundwork — nothing in `IpscController` consumes these yet.

### 🔀 `/v1` Route Prefix Dropped

- `AwardController` now maps to `/awards` instead of `/v1/awards`; `ImageController` now maps to `/images` instead of `/v1/images` — the unused API versioning segment is gone.

### 📝 Documentation Convention Tightening

- New AGENTS.md **Serial commas** rule: lists of three or more items no longer take a comma before the final `and`/`or`.
- AGENTS.md's British English rule is tightened to also cover code identifiers (class/method/variable names), not just prose — dropping the previous exception.
- Both rules are applied retroactively across `CLAUDE.md`, `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, `HISTORY.md` and the Claude Code command files. The identifier-spelling sweep found and corrected two American spellings in existing test method names.
- `documentation/roadmap/`'s `IMPROVEMENT_PLAN.md`/`TASKS.md` are renamed to `improvement-plan.md`/`improvement-plan-tasks.md` for kebab-case consistency with the rest of the tooling docs.

### 🤖 New `/sync-unreleased-changes` Command

- Diffs the current branch against its base (`develop`/`main`) plus any uncommitted changes, cross-checks the result against `CHANGELOG.md`'s `[Unreleased]` section and fills in any missing entries directly in the file.

---

## 📦 What's New

### Added

#### Models

- `za.co.hpsc.web.models.ipsc.request` — `MatchRequest`, `MatchStageRequest`, `MatchStagesRequest`, `MatchOverallResultRequest`, `MatchStageResultRequest`, `MatchOverallResultRequestForCSV`, `MatchStageResultRequestForCSV`
- `za.co.hpsc.web.models.ipsc.shared` — `IpscCommonScore`, `IpscMatchScore`, `IpscMatchStageScore`

#### Documentation

- `documentation/roadmap/improvement-plan.md` / `improvement-plan-tasks.md` — synthesised repo goals/gaps and their checkbox-level task breakdown

#### Tooling

- `.claude/commands/sync-unreleased-changes.md`

### Changed

#### Documentation

- `AGENTS.md` — new Serial commas rule; British English rule tightened to cover code identifiers; both applied across the existing documentation set
- `documentation/roadmap/` — `IMPROVEMENT_PLAN.md`/`TASKS.md` renamed to `improvement-plan.md`/`improvement-plan-tasks.md`
- `RELEASE_NOTES.md` Contributors — now sourced from `git log`'s unique commit authors instead of a generic placeholder

#### Testing

- `RequestTest`, `ResponseTest`, `AwardRequestForCSVTest`, `ImageResponseTest` — method names corrected to British-English spelling

#### Configuration

- `.gitignore` / `.aiignore` — refreshed from the latest upstream templates

### Fixed

#### Controllers

- `AwardController` — route prefix `/v1/awards` → `/awards`
- `ImageController` — route prefix `/v1/images` → `/images`

#### Documentation

- `README.md` — restored the missing H1 heading

### Removed

#### Configuration

- `.aiignore` — dropped the dedicated `.claude/`/`.github/` AI-only exclusion block

### Security

- `log4j-api` overridden `2.25.4` → `2.25.5`, closing CVE-2026-49844

---

## 🔄 Migration Guide

### For Deployers

- **No schema or configuration changes in this release.** The `.gitignore`/`.aiignore` refresh and `log4j-api` override are the only build-adjacent changes, neither of which affects deployment.

### For Developers

- **Breaking: `AwardController`/`ImageController` route paths changed.** `/v1/awards` → `/awards` and `/v1/images` → `/images`. Update any client, integration test or API-gateway configuration that hard-codes the old `/v1` paths.
- **No domain/service-layer changes** — the new IPSC request DTOs aren't referenced by any controller or service yet, so there's nothing to update in calling code.
- **New identifier-spelling rule:** code identifiers (class/method/variable names) must now use British English spelling, same as prose — see AGENTS.md's Documentation Conventions.
- **New command:** `/sync-unreleased-changes` cross-checks a branch's full diff against `CHANGELOG.md`'s `[Unreleased]` section and fills in anything missing.

---

## 📊 Statistics

- **Total Commits:** 25 (plus this release's version bump and documentation files)
- **Files Changed:** 36 (plus this release's version bump and documentation files)

---

## 🧭 Design Notes

- **Request DTOs before endpoints.** The new IPSC request DTOs are shaped to match Practiscore's actual CSV/JSON export format first, deliberately ahead of any `IpscController` wiring — the same domain-groundwork-before-service pattern used for the v7.0.0/v7.1.0 shooter-log entities.
- **Drop the unused API version segment.** `/v1` never had a `/v2` counterpart in the current codebase (the old `/v2/ipsc/matches` controller was removed along with the earlier service layer) — keeping a versioning segment with no second version to distinguish from was misleading, so it's dropped rather than kept for its own sake.
- **Tighten conventions, then apply them immediately.** Rather than adding the Serial commas/identifier-spelling rules to AGENTS.md and leaving the existing docs to drift out of compliance, this release sweeps every existing doc and the one pair of test method names the identifier rule newly covers, in the same change.

---

## 🧪 Testing

- `./mvnw clean compile` — all new IPSC request/shared DTOs compile cleanly.
- `./mvnw test` — full suite passing, including the four renamed test methods (names only changed — no assertions or behaviour touched).
- Manually reviewed the documentation sweep against AGENTS.md's new Serial commas and identifier-spelling rules.

---

## 🐛 Known Issues

- Carried over from v7.0.0–v7.3.0: no calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only.
- No dedicated test coverage yet for the new IPSC request/shared DTOs — they're groundwork, not yet exercised by any controller or service.
- `IpscController` remains an empty stub; the new request DTOs aren't wired to any endpoint in this release.

---

## 🔮 Future Enhancements

- Wire the new IPSC request DTOs into `IpscController` endpoints, backed by the existing entity/repository layer.
- Add dedicated unit test coverage for the new IPSC request/shared DTOs.
- Rebuild the match/competitor service and controller layer that `README.md` describes as groundwork-only.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

This release combines domain-layer groundwork (new IPSC request DTOs, shaped ahead of the module rebuild) with a small API cleanup (`/v1` route prefix removed) and a documentation-convention tightening pass (serial commas, identifier spelling) applied across the entire existing documentation set. No domain entities, repositories or services changed.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
