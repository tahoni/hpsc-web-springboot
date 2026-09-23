# Release Notes – Version 8.4.1

**Release Date:** September 4, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Documentation Cross-Reference Consolidation & Icon Registry Sync**

Version 8.4.1 is a documentation-only patch release with no source-code, schema or dependency changes. It
consolidates `AGENTS.md`/`CONTRIBUTING.md`'s remaining full and near-verbatim content duplicates into the
highlights-and-link pattern already established elsewhere in those files — condensing Git Workflow's Branching
Model, Conventions and Directory Tree Maintenance bullets, an unlinked Exception handling restatement, and a
CI/CD & Quality Gates table that had drifted from its actual source of truth in `ARCHITECTURE.md`. It also
reconciles the icon registry with the sibling `hpsc-web-vite` repository twice over, backfills 25 previously
unregistered icons already in real use and fixes several icon collisions and a broken example in the Serial
Commas convention.

---

## ⭐ Key Highlights

### 🔀 Git Workflow Consolidation

- `CONTRIBUTING.md`'s Branching Model (GitFlow) bullets, Git Workflow "Conventions" and Directory Tree Maintenance
  bullets condensed into highlights-and-link references pointing at `AGENTS.md`, replacing full/near-verbatim
  restatements
- Git Workflow's "Merging" subsection removed from `AGENTS.md` and consolidated as `CONTRIBUTING.md`'s sole
  canonical copy — it describes a human contributor's GitHub mechanics, not something any Claude Code skill reads
  directly, unlike the Branching Model and Conventions subsections `sync-unreleased-changes`/
  `sync-improvement-plan-gaps` depend on remaining in `AGENTS.md`

### 🔗 Unlinked Cross-Reference Cleanup

- `CONTRIBUTING.md`'s Exception handling bullet, CHANGELOG-same-change and Evergreen/reverse-sync bullets now link
  to the specific `AGENTS.md` subsection each one restates, matching the pattern its sibling bullets already
  followed
- CI/CD & Quality Gates table condensed into a link to `ARCHITECTURE.md`'s own table — the actual source of truth
  per `AGENTS.md`'s own cross-reference — since the two copies had drifted to slightly different column wording

### 🧩 New AGENTS.md Sections

- New "🧩 Claude Code Skills" and "🗺️ Roadmap Planning" sections in `AGENTS.md`, both mirrored with a short
  pointer in `CONTRIBUTING.md`

### 🗺️ Icon Registry Sync with hpsc-web-vite

- `AGENTS.md`'s icon registry backfilled with 25 previously-unregistered icons already in real use across this
  repository's documentation
- New "Reserved" sub-table tracking the sibling `hpsc-web-vite` repository's frontend-specific icons, synced twice
  this release as that repository's own registry grew — `hpsc-web-vite` reciprocally reserves `🧬` (Data model /
  DTOs) in return
- Several icon collisions resolved across `README.md`, `ARCHITECTURE.md`, `HISTORY.md`, `RELEASE_NOTES.md` and 17
  archived per-version release notes

### 🐛 Documentation Fixes

- `CHANGELOG.md`'s duplicate, truncated `[5.0.0]` section removed
- Serial Commas convention's own example fixed — the "e.g." and "not" contrast phrases were identical in both
  `AGENTS.md` and `CONTRIBUTING.md`, so the example never actually illustrated the rule

---

## 📦 What's New

### Added

#### Documentation

- **`AGENTS.md`/`CONTRIBUTING.md`:** New "🧩 Claude Code Skills" section documenting the project-specific skills
  under `.claude/skills/`; new `🧩` icon registered for tooling/automation sections
- **`AGENTS.md`:** New "Reserved" sub-table under "Icons in headings" tracking the sibling `hpsc-web-vite`
  repository's frontend-specific icon registry, synced twice this release (38 icons reserved in total)
- **`AGENTS.md`:** New "🗺️ Roadmap Planning" section, promoted out of "Documentation File Map" into its own home
- **`AGENTS.md`:** 25 previously-unregistered icons backfilled into the icon registry table, plus `🟡`/`⚪` for the
  roadmap status scheme and `🗂️`/`🌲` for the "Documentation File Map"/"Evergreen Documentation" headings

### Changed

#### Documentation

- **`AGENTS.md`/`ARCHITECTURE.md`/`documentation/recommendations/*.md`:** Several icon meanings corrected or
  widened where a heading's actual concept didn't match its registered description, or reused an icon already
  claimed for something unrelated
- **`AGENTS.md`:** Icon registry table reordered to group icons by the document(s) that established them
- **`CONTRIBUTING.md`:** "Roadmap" section condensed to a pointer at `AGENTS.md`'s new "Roadmap Planning" section
- **`AGENTS.md`/`CONTRIBUTING.md`:** Git Workflow's "Merging" subsection consolidated as `CONTRIBUTING.md`'s sole
  canonical copy; `CONTRIBUTING.md`'s remaining full/near-verbatim duplicates of `AGENTS.md` content (Branching
  Model, Conventions, Directory Tree Maintenance, Exception handling, CHANGELOG-same-change, Evergreen/reverse-sync,
  CI/CD & Quality Gates) condensed into highlights-and-link references

### Fixed

#### Documentation

- **`CHANGELOG.md`:** Removed a duplicate, truncated `[5.0.0]` section whose heading collision had broken its
  Table of Contents anchor
- **`documentation/history/RELEASE_NOTES_v6.0.0.md`/`v7.0.0.md`/`v7.2.0.md`/`v8.0.0.md`:** Five archived
  sub-headings reusing an already-registered icon for an unrelated concept, corrected
- **`AGENTS.md`/`README.md`/`HISTORY.md`/`documentation/roadmap/improvement-plan.md`:** Several heading icons
  corrected for internal consistency (Documentation Conventions, Documentation File Map/Roadmap Planning, API
  Documentation vs Documentation, Future Roadmap Implications)
- **`RELEASE_NOTES.md`/17 archived per-version release notes:** "Migration Guide" heading switched to `🚀`, matching
  `CHANGELOG.md`'s equivalent "Upgrade Guide" heading, at the user's explicit request to extend the fix to the
  archives
- **`AGENTS.md`/`CONTRIBUTING.md`:** Serial Commas rule's own example corrected — the "not" contrast phrase now
  actually shows the Oxford-comma version it's meant to contrast against

---

## 🚀 Migration Guide

No code, schema, configuration or dependency changes in this release — nothing for API consumers or developers to
migrate.

---

## 📊 Statistics

- **Total Commits:** 32
- **Files Changed:** 31
- **Insertions:** 439 lines
- **Deletions:** 231 lines
- **Net Change:** +208 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Consolidate duplicated content into the file that skills actually read, not the file that reads more naturally
  standalone.** Git Workflow's Branching Model and Conventions subsections stay in `AGENTS.md`, not
  `CONTRIBUTING.md`, specifically because `sync-unreleased-changes`/`sync-improvement-plan-gaps` read `AGENTS.md`
  directly for hotfix-branch detection and commit conventions — the Merging subsection, which no skill reads, moved
  to `CONTRIBUTING.md` instead.
- **A "highlights and link" summary is not the same failure as an unlinked duplicate.** Several sections already
  followed the desired pattern (Roadmap, Claude Code Skills, Release Checklist); this release's fixes targeted only
  the remaining full/near-verbatim restatements that carried no link back to their source at all.

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

Version 8.4.1 is a documentation-only patch focused entirely on internal consistency: consolidating duplicated
`AGENTS.md`/`CONTRIBUTING.md` content into the highlights-and-link pattern the rest of those files already use, and
reconciling the icon registry with the sibling `hpsc-web-vite` repository. No product-facing behaviour changed.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
