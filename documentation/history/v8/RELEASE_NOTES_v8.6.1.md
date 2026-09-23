# Release Notes – Version 8.6.1

**Release Date:** September 23, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**`documentation/history/` Reorganization & Evolution Overview Split**

Version 8.6.1 is a documentation-only patch release with no source-code, schema or dependency changes. It splits
`HISTORY.md`'s largest, fastest-growing section — the Phase-by-phase "Evolution Overview" narrative, roughly half
of the file's 4,095 lines — out into its own companion file, and regroups the 52-file archive of past
`RELEASE_NOTES_vX.Y.Z.md`/`PR_DESCRIPTION_vX.Y.Z.md` files from a flat directory into major-version subdirectories.
Every tool and doc that reads or writes those paths — three Claude Code skills, `AGENTS.md`'s Documentation File Map
and Release Checklist, and `README.md`'s Documentation table — is updated to match. As with v8.4.1/v8.4.2/v8.5.1,
a doc/tooling-only diff against `main` scopes this as a **PATCH** version, not a new minor.

---

## ⭐ Key Highlights

### 📖 Evolution Overview Split

- `HISTORY.md`'s "📖 Evolution Overview" section (the Phase-by-phase narrative) split out into new
  `documentation/history/EVOLUTION_OVERVIEW.md` — it had grown to roughly half of `HISTORY.md`'s 4,095 lines,
  making the file unwieldy. `HISTORY.md` keeps a short pointer section under the same heading/anchor, so its
  Table of Contents entry still resolves; every other section stays in `HISTORY.md` unchanged

### 🗂️ documentation/history/ Reorganization

- All 52 archived `RELEASE_NOTES_vX.Y.Z.md`/`PR_DESCRIPTION_vX.Y.Z.md` files regrouped from a flat directory into
  `v1/` – `v8/` subdirectories by major version, moved with `git mv` to preserve history. `EVOLUTION_OVERVIEW.md`
  is unaffected — it's a standing living file, not a per-version archive
- `AGENTS.md`'s Documentation File Map and Release Checklist (two independent copies of the archive-path
  references), `README.md`'s Documentation table, and the `prep-version-release`/`generate-pr-summary`/
  `update-improvement-plan-gaps` skills all updated to read/write `documentation/history/v<major>/...` paths

---

## 📦 What's New

### Changed

#### Documentation

- **`HISTORY.md`:** "📖 Evolution Overview" section split out into new `documentation/history/EVOLUTION_OVERVIEW.md`
  — a short pointer under the same heading/anchor keeps the Table of Contents entry resolving; every other section
  unchanged
- **`AGENTS.md`, `README.md`:** Documentation File Map/Documentation tables updated to list `EVOLUTION_OVERVIEW.md`
  as a standing exception in `documentation/history/` living directly there, and to describe the new `v1/` – `v8/`
  subdirectory grouping for the versioned archive files
- **`AGENTS.md`'s Release Checklist, `prep-version-release` skill:** The "Extend `HISTORY.md`" step now specifies
  that the Phase entry lands in `documentation/history/EVOLUTION_OVERVIEW.md` while the Historical Timeline entry
  and Milestone stay in `HISTORY.md`; the archive/PR-description steps target the new `v<major>/` subdirectory
- **`documentation/history/`:** All 52 archived files regrouped from a flat directory into `v1/` – `v8/`
  subdirectories by major version, moved with `git mv` to preserve history
- **`generate-pr-summary`, `update-improvement-plan-gaps` skills:** Updated to read/glob archived release docs at
  `documentation/history/v<major>/...` instead of the old flat path

---

## 🚀 Migration Guide

No code, schema, configuration or dependency changes in this release — nothing for API consumers to migrate. If you
or any tooling outside this repository links directly to a `documentation/history/RELEASE_NOTES_vX.Y.Z.md` or
`PR_DESCRIPTION_vX.Y.Z.md` path, update it to `documentation/history/v<major>/...` (e.g.
`documentation/history/v8/RELEASE_NOTES_v8.6.0.md`) — the generic `documentation/history/` directory link at the
bottom of every archived release-notes file is unaffected.

---

## 📊 Statistics

- **Total Commits:** 10 (7 feature commits across the two source branches this release combines, plus this
  release's version bump, documentation and PR description commits)
- **Files Changed:** 65
- **Insertions:** 2,675 lines
- **Deletions:** 2,373 lines
- **Net Change:** +302 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Split the section that's actually growing, not the whole file arbitrarily.** `HISTORY.md`'s "Evolution
  Overview" alone accounted for roughly half its size and is the section every release adds the most to (a full
  `Phase N` block per version) — isolating it addresses the actual growth driver in one move, rather than an
  even but less-targeted split across all sections.
- **Group archives by major version, not by count.** Even the smallest major-version groups (v2, v6: one file
  each) get their own subdirectory, prioritising a consistent, predictable structure over optimising for today's
  group sizes — v7 and v8 already show how large a single major version's archive can grow (12 and 24 files
  respectively).
- **Update every tool that writes to a path before the path changes underneath it.** The `prep-version-release`,
  `generate-pr-summary` and `update-improvement-plan-gaps` skills were all updated in the same release as the
  move, not left to fail on the next invocation.
- **Leave frozen historical records alone.** Old `CHANGELOG.md` entries and a closed `improvement-plan.md` Gap's
  Evidence text that name pre-move filenames are deliberately left unedited, consistent with this project's own
  convention of never rewriting historical prose — they're plain text, not links, so nothing breaks for a reader.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (872 tests, 0 failures/errors), unchanged from v8.6.0 (no source changes in
  this release).
- `./mvnw verify -Pcoverage` — coverage figures unchanged from v8.6.0.
- No new tests added; this release makes no code changes. Verified instead: every moved file shows as a `git`
  rename (not delete+add), `git log --follow` traces full history on sampled v1/v8 files, and a repo-wide grep for
  `documentation/history/RELEASE_NOTES_v`/`PR_DESCRIPTION_v` outside `documentation/history/` and the accepted
  historical exceptions returns zero matches.

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

Version 8.6.1 is a documentation-only patch focused entirely on keeping `HISTORY.md` and `documentation/history/`
navigable as the project's release history keeps accumulating. No product-facing behaviour changed.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
