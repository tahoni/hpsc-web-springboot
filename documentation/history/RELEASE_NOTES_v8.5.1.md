# Release Notes – Version 8.5.1

**Release Date:** September 13, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**HISTORY.md Phase/Milestone Backfill & Release Re-Scoping to a Patch Version**

Version 8.5.1 is a documentation-only patch release with no source-code, schema or dependency changes. It backfills
three releases' worth of missing `HISTORY.md` "📖 Evolution Overview"/"🎯 Major Milestones" entries (v8.4.1, v8.4.2
and v8.5.0), closing `improvement-plan.md`'s Gap #10 — the mandatory per-release Phase/Milestone step `AGENTS.md`'s
Release Checklist requires unconditionally. Partway through drafting this release, its own diff against `main`
turned out to be documentation/tooling-only, so it was re-scoped from the originally-planned `v8.6.0` **MINOR**
version down to `v8.5.1` **PATCH**, matching the precedent already set by v8.4.1/v8.4.2.

---

## ⭐ Key Highlights

### 📖 HISTORY.md Phase/Milestone Backfill

- New "Phase 26"/"Milestone 26" (v8.4.1), "Phase 27"/"Milestone 27" (v8.4.2) and "Phase 28"/"Milestone 28" (v8.5.0)
  entries, summarising each release's already-written Historical Timeline content at the same narrative depth as
  the surrounding entries — closes `improvement-plan.md`'s Gap #10
- "Major Version Goals" Version 8.x entry extended from `v8.0.0 – v8.1.1` to `v8.0.0 – v8.5.1`, narrating the domain
  broadening (multi-value competitor emails, match bulk CSV import, relaxed club-requirement defaults, match
  start/end time tracking) and documentation-process discipline delivered across v8.2.0 – v8.5.1

### 🗺️ Improvement Plan Maintenance

- New "📋 At a Glance" section in `improvement-plan.md`, indexing every numbered gap by completion status ahead of
  the full per-gap detail
- "🗺️ Roadmap" table's **Now** row dropped already-closed #2/#7 and promoted #6 (match scoring / shooter-log
  service and controller layer) as the sole remaining open gap; **Next** cycled through pointing at Gap #10's
  backfill work, then back to unscoped once Gap #10 closed
- Gap #10 tracked end-to-end: opened, closed in v8.5.1 with an Outcome paragraph, and its `improvement-plan-tasks.md`
  checkbox block — including the final "does this release need its own Phase/Milestone" item — fully checked off
  once this release's own Phase 29/Milestone 29 landed

### 🔀 Release Re-Scoping to a Patch Version

- This release's entire diff against `main` proved documentation/tooling-only (no `src/main/java`/`src/test`
  change), so per `CHANGELOG.md`'s Version Policy it was re-scoped from the originally-planned `v8.6.0` **MINOR**
  version down to `v8.5.1` **PATCH** — matching the precedent set by v8.4.1/v8.4.2
- The branch and every in-flight `CHANGELOG.md`/`HISTORY.md`/improvement-plan reference to `v8.6.0` renamed to
  `v8.5.1` ahead of this release-prep pass

### 🧩 Tooling & Minor Fixes

- `prep-version-release`/`generate-pr-summary` skills now end their drafted PR description/summary with the
  standard Claude Code attribution footer, marking them as Claude-drafted like any other PR description Claude
  Code opens
- `CONTRIBUTING.md` spells out "and" instead of "&" in its CI/CD & Quality Gates cross-reference; a Flyway baseline
  comment in `application-local.properties` tightened for clarity

---

## 📦 What's New

### Added

#### Documentation

- **`HISTORY.md`:** New "Phase 26"/"Milestone 26" (v8.4.1), "Phase 27"/"Milestone 27" (v8.4.2), "Phase 28"/
  "Milestone 28" (v8.5.0) and this release's own "Phase 29"/"Milestone 29" (v8.5.1) entries in "📖 Evolution
  Overview"/"🎯 Major Milestones"
- **`improvement-plan.md`:** New "📋 At a Glance" section indexing every gap by completion status; Gap #10 (the
  `HISTORY.md` backfill gap itself), now closed

### Changed

#### Documentation

- **`improvement-plan.md`:** "🗺️ Roadmap" table's **Now**/**Next** rows refreshed to drop already-closed gaps and
  reflect Gap #10's lifecycle; "✅ Success Criteria" updated to match
- **`improvement-plan-tasks.md`:** Intro line's gap count updated from "nine" to "ten"; Gap #10's checkbox block
  moved to "✅ Completed" with every item checked, including the final re-check of this release's own Phase/
  Milestone need
- **`HISTORY.md`:** "Major Version Goals" Version 8.x entry extended from `v8.0.0 – v8.1.1` to `v8.0.0 – v8.5.1`
- **`CONTRIBUTING.md`:** Spells out "and" instead of "&" in its CI/CD & Quality Gates cross-reference sentence

#### Configuration

- **`application-local.properties`:** Flyway baseline comment wording tightened ("baselined" → "baseline", "on
  first run" → "on the first run")

#### Tooling

- **`prep-version-release`, `generate-pr-summary`:** Both skills now end their drafted PR description/summary with
  the standard Claude Code attribution footer (`🤖 Generated with [Claude Code](https://claude.com/claude-code)`)

---

## 🚀 Migration Guide

No code, schema, configuration or dependency changes in this release — nothing for API consumers or developers to
migrate. `application-local.properties`' comment wording tweak is cosmetic only.

---

## 📊 Statistics

- **Total Commits:** 15
- **Files Changed:** 10
- **Insertions:** 423 lines
- **Deletions:** 20 lines
- **Net Change:** +403 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Backfill from what's already written, not from scratch.** Each new Phase/Milestone entry summarises the
  matching release's already-written Historical Timeline content rather than re-researching it — the same approach
  Gap #10's Proposed improvement specified.
- **Correct the release's own Semantic Versioning classification before it ships, not after.** Once the branch's
  diff against `main` was confirmed documentation/tooling-only, re-scoping from `v8.6.0` to `v8.5.1` immediately —
  renaming the branch and every in-flight reference — avoided shipping a MINOR-versioned release with no
  backward-compatible functionality addition to justify it.
- **A gap's own closure can leave one task item pointing at the future.** Gap #10's fourth checklist item
  deliberately stayed unchecked across two commits, since it asked whether *this* release needs a Phase/Milestone —
  a question only this release's own prep pass could answer.

---

## 🧪 Testing

- `./mvnw test` — full suite unaffected; no source changes in this release. Last confirmed passing at v8.5.0 (870
  tests, 0 failures/errors).
- `./mvnw verify -Pcoverage` — coverage figures unchanged from v8.5.0 (98.65% line / 98.99% branch); no source
  changes to affect them.
- No new tests added; this release makes no code changes.

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- The `BRANCH` coverage counter is still not separately enforced by the JaCoCo `check` execution — only `LINE` is,
  as established when the gate was first added in v8.3.1.

---

## 🔮 Future Enhancements

- Build a `MatchScoreService`/`ShooterLogService` (interface + `impl/` split) over the existing repositories,
  following the same phased pattern that closed Gap #1 and Gap #8.
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint — their
  `@JsonCreator` constructors and required-field enforcement are already correct and ready for this.
- Consider enforcing a `BRANCH`-level JaCoCo minimum alongside the existing `LINE` one, now that the `LINE` floor
  sits close to its real baseline.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.5.1 is a documentation-only patch focused entirely on closing a three-release `HISTORY.md` Phase/Milestone
backlog and correcting this release's own Semantic Versioning classification. No product-facing behaviour changed.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
