# Release Notes – Version 8.6.2

**Release Date:** September 24, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**`CHANGELOG.md` Heading-Depth Convention Correction & Future Roadmap Refresh**

Version 8.6.2 is a documentation-only patch release with no source-code, schema or dependency changes. It corrects
how the project's own written conventions describe `CHANGELOG.md`'s heading structure: `AGENTS.md`,
`CONTRIBUTING.md` and five Claude Code skills all described the file as `## 🧪 [Unreleased]` → `### <category>` →
`#### <Area>`, one level shallower than the `###`/`####`/`#####` depth the file has actually used. The fix was
reverse-synced from the shared project template, which had already corrected the same drift in its own copy of these
conventions. It also refreshes `HISTORY.md`'s forward-looking Future Roadmap lists, which still named already
delivered and renamed work — recorded as Gap #11 by this release's own improvement-plan audit and closed within the
same release. As with v8.4.1/v8.4.2/v8.5.1/v8.6.1, a doc/tooling-only diff against `main` scopes this as a **PATCH**
version.

---

## ⭐ Key Highlights

### 🧾 CHANGELOG.md Convention Correction

- Every `CHANGELOG.md` heading reference in `AGENTS.md`, `CONTRIBUTING.md` and the `generate-commit-message`,
  `prep-version-release`, `scaffold-unit-tests`, `scaffold-integration-tests` and `sync-unreleased-changes` skills
  now matches the file's actual `### 🧪 [Unreleased]` → `#### <category>` → `##### <Area>` nesting
- `AGENTS.md`'s Git Workflow Conventions now spell out the full category/Area nesting, reuse of existing Area names
  and the bold-lead-in bullet style, instead of leaving them implicit

### 🗺️ Future Roadmap Refresh (Gap #11)

- `HISTORY.md`'s Short-term/Medium-term Future Roadmap lists now name only genuinely outstanding work: club seeding
  (shipped in v8.4.0) and bulk match import (shipped in v8.3.0) dropped, `ShooterLogEntry` renamed to
  `ShooterLogCompetitor`, and "Medium-term (v7.x+)" relabelled for the current major version
- Gap #11 recorded and closed in `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md`, leaving
  Gap #6 as the only open gap

### 🔁 Template Reverse Sync

- Only the template's generic correction was brought back — this project keeps its own Conventional Commits prefixes
  and bold-lead-in bullet style rather than adopting the template's plain-imperative defaults

---

## 📦 What's New

### Fixed

#### Documentation

- **`AGENTS.md`, `CONTRIBUTING.md`:** `CHANGELOG.md` heading references corrected from `## 🧪 [Unreleased]`/
  `## 🧾 [X.Y.Z]` to the `### 🧪 [Unreleased]`/`### 🧾 [X.Y.Z]` depth the file actually uses. `AGENTS.md`'s Git
  Workflow Conventions now spell out the full `#### <category>` → `##### <Area>` nesting, Area reuse and the
  bold-lead-in bullet style — reverse-synced from the shared project template
- **`HISTORY.md`:** "🗺️ Future Roadmap Implications" Short-term/Medium-term lists refreshed against what has
  actually shipped — the club-seeding bullet reduced to its still-outstanding `Competitor.homeClub` backfill half,
  `ShooterLogEntry` renamed to `ShooterLogCompetitor`, "Medium-term (v7.x+)" relabelled "Medium-term (Later v8.x
  Releases)" and "Bulk match processing capabilities" dropped as delivered by v8.3.0's bulk CSV import
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #11 recorded for those stale lists, then closed in
  this same release, with "📋 At a Glance", the "🗺️ Roadmap" table and "✅ Success Criteria" updated to match

#### Tooling

- **`generate-commit-message`, `prep-version-release`, `scaffold-unit-tests`, `scaffold-integration-tests`,
  `sync-unreleased-changes` skills:** Category and Area heading depths corrected from `###`/`####` to the
  `####`/`#####` levels `CHANGELOG.md` actually uses; `generate-commit-message` also notes that security-relevant
  fixes belong under `#### 🔐 Security`

---

## 🚀 Migration Guide

No code, schema, configuration or dependency changes in this release — nothing for API consumers to migrate. Anyone
drafting `CHANGELOG.md` entries by hand or with the project's Claude Code skills should nest them as
`### 🧪 [Unreleased]` → `#### <category>` → `##### <Area>`, which is what the file already used.

---

## 📊 Statistics

- **Total Commits:** 9 (2 feature commits from `feature/docs`, plus this release's version bump, roadmap update,
  documentation, PR description, serial-comma fix, commit-count update and Gap #11 fix commits)
- **Files Changed:** 18
- **Insertions:** 529 lines
- **Deletions:** 132 lines
- **Net Change:** +397 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Fix the description, not the file.** `CHANGELOG.md` has consistently used `###`/`####`/`#####` across every
  release; the drift was in the conventions describing it, so the conventions were corrected rather than
  restructuring all 36 released versions of changelog history to match a wrong description.
- **Reverse-sync selectively.** The shared project template also moved its root documentation files into
  `documentation/current/`, switched to plain-imperative commit messages and trimmed its skills down to pointers at
  `AGENTS.md`. None of those were brought back: the first is template-specific, the second contradicts this
  project's established Conventional Commits history and the third would drop Spring/Maven-specific detail these
  skills deliberately carry.
- **Spell out conventions agents rely on.** Heading depth and bullet style were previously only implied by the
  file's existing entries; stating them in `AGENTS.md` means an agent following the conventions can't reproduce the
  old off-by-one drift.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (872 tests, 0 failures/errors), unchanged from v8.6.1 (no source changes in
  this release).
- No new tests added; this release makes no code changes. Verified instead: no `## 🧪 [Unreleased]`/`## 🧾 [`
  heading references remain in `AGENTS.md`, `CONTRIBUTING.md` or `.claude/skills/`, and every released version in
  `CHANGELOG.md` uses the `### 🧾 [X.Y.Z]` depth the corrected conventions now describe.

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

Version 8.6.2 is a documentation-only patch that brings the project's written `CHANGELOG.md` conventions back in
line with the file itself. No product-facing behaviour changed.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
