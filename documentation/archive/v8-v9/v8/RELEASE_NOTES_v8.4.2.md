# Release Notes – Version 8.4.2

**Release Date:** September 4, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Root Document Title Standardisation & Source-of-Truth Clarification**

Version 8.4.2 is a documentation-only patch release with no source-code, schema or dependency changes. It states
explicitly, for the first time, that `AGENTS.md` is this project's ultimate source of truth for conventions, and
standardises the H1 titles of `CHANGELOG.md`, `CONTRIBUTING.md` and `HISTORY.md` to match `README.md`'s existing
"HPSC Website Backend" project name — `CHANGELOG.md` gains a nested "Change Log" heading in the process, cascading a
one-level heading demotion through the rest of the file.

---

## ⭐ Key Highlights

### 📚 AGENTS.md as the Ultimate Source of Truth

- `AGENTS.md` now states, right before its Documentation File Map, that it is this project's ultimate source of
  truth for conventions — every other file's workflow/convention guidance points back to it rather than restating
  it
- `CONTRIBUTING.md`'s intro carries the matching pointer, stating `AGENTS.md` wins if anything else in the
  repository's documentation ever contradicts it

### 🏷️ Root Document Title Standardisation

- `CHANGELOG.md`'s title changed from "Changelog" to "HPSC Website Backend", with a new "🧾 Change Log"
  second-level heading beneath it, matching `README.md`'s existing project name
- `CONTRIBUTING.md`/`HISTORY.md`'s H1 titles gain the same "HPSC Website Backend" prefix, for consistency across
  every root document
- Every heading below `CHANGELOG.md`'s new "Change Log" heading — Table of Contents, each version and their
  Added/Changed/Fixed/Removed/Security subsections and area sub-headers — demoted one level to nest correctly
  beneath it

---

## 📦 What's New

### Added

#### Documentation

- **`AGENTS.md`:** Now states, right before its Documentation File Map, that it is this project's ultimate source of
  truth for conventions — every other file's workflow/convention guidance (`CONTRIBUTING.md` included) points back to
  it rather than restating it
- **`CONTRIBUTING.md`:** Intro now points to `AGENTS.md` for the full set of conventions AI coding agents and
  contributors follow, and states that `AGENTS.md` wins if anything else in the repository's documentation ever
  contradicts it

### Changed

#### Documentation

- **`CHANGELOG.md`:** Title changed from "Changelog" to "HPSC Website Backend", with a new "🧾 Change Log"
  second-level heading beneath it, matching `README.md`'s existing project name; every heading below it — Table of
  Contents, each version and their Added/Changed/Fixed/Removed/Security subsections and area sub-headers — drops one
  level to nest correctly under the new heading
- **`CONTRIBUTING.md`/`HISTORY.md`:** H1 titles gain the same "HPSC Website Backend" prefix, for consistency with
  `README.md` and the retitled `CHANGELOG.md`

---

## 🚀 Migration Guide

No code, schema, configuration or dependency changes in this release — nothing for API consumers or developers to
migrate. Anyone linking directly to a `CHANGELOG.md` version-section anchor is unaffected, since anchor text is
unchanged; only the heading *level* markup around it changed.

---

## 📊 Statistics

- **Total Commits:** 9
- **Files Changed:** 9
- **Insertions:** 494 lines
- **Deletions:** 449 lines
- **Net Change:** +45 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **State the source-of-truth relationship explicitly, rather than leaving it implied.** Every other document already
  pointed to `AGENTS.md` for the conventions it restates a summary of, but nothing said outright that `AGENTS.md`
  wins on conflict — `CONTRIBUTING.md`'s intro now says so directly, so drift between the two has one obvious
  resolution.
- **A title change that adds a heading level must cascade, not just insert.** Turning `CHANGELOG.md`'s old H1 into a
  nested H2 under a new project-name H1 would have left every version section still notionally "under" the old
  title text if the levels below it hadn't also shifted down — the one-level demotion keeps the document's actual
  heading hierarchy consistent with its new structure, not just its rendered title.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (868 tests, 0 failures/errors), confirmed during release prep. No source
  changes in this release to affect it.
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

Version 8.4.2 is a documentation-only patch focused entirely on making `AGENTS.md`'s role as this project's ultimate
source of truth explicit, and standardising root document titles around `README.md`'s existing project name. No
product-facing behaviour changed.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
