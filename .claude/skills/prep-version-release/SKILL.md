---
name: prep-version-release
description: Prepare a new version release — RELEASE_NOTES.md, CHANGELOG.md, HISTORY.md, reverse-synced docs and a draft release PR description — following AGENTS.md's Release Checklist. Use whenever the user is preparing/cutting a release PR or asks to draft release documentation for a version.
user-invocable: true
allowed-tools:
  - Bash(git log:*)
  - Bash(git --no-pager log:*)
  - Bash(git diff:*)
  - Bash(git --no-pager diff:*)
  - Bash(git branch:*)
  - Bash(git status:*)
  - Bash(git merge-base:*)
  - Read
  - Edit
  - Write
---

# Prepare Version Release

The version to prepare a release for is passed as `args` (e.g. `7.2.0`) — if not supplied, ask the user for it before
proceeding. The rest of this skill refers to that value as `$VERSION`.

## 🔍 Gather current state

Before drafting, run these yourself and read their output:

1. `git branch --show-current`
2. `git --no-pager diff --stat main...HEAD` (changes relative to `main`)
3. `git log main..HEAD --oneline` (commit log relative to `main`)
4. Read `AGENTS.md` in full for conventions.

## 🚀 Instructions

Read and strictly follow **all conventions defined in AGENTS.md** (loaded above) — in particular its **Release
Checklist**, **Documentation Conventions**, **Git Workflow** (Branching Model, Semantic Versioning), **Evergreen
Documentation** (reverse sync rule), **Build & Run Commands** and **Architecture** sections for accurate technical
detail (build/test commands, package layout, database profiles) when writing `RELEASE_NOTES.md`/the PR description.
Treat it as the single source of truth; do not reinterpret or contradict its rules. Follow the Release Checklist steps
**in order** — the version number and date must be final before anything downstream references them.

Steps:

1. **Confirm the diff against `main`** (gathered above) covers everything that changed for this release — re-run
   `git log main..HEAD` / `git diff --stat main...HEAD` yourself if the branch has moved on since this skill started.
   Then **validate `$VERSION` against AGENTS.md's Semantic Versioning rules** before anything references it: find the
   latest released version (the top `### 🧾 [X.Y.Z]` heading in `CHANGELOG.md`), classify this release from
   `### 🧪 [Unreleased]` and the diff as MAJOR, MINOR or PATCH (highest-ranking change wins — any breaking change is
   MAJOR; any externally visible addition or deprecation is MINOR; otherwise PATCH), and check that `$VERSION` is
   exactly the one-step increment that classification implies, in plain `X.Y.Z` form. If it isn't — wrong level,
   skipped or reused number, or a `v` prefix/suffix — stop and tell the user which version SemVer calls for and why,
   citing the deciding changes; only continue once they confirm a version. If any change's backward compatibility is
   unclear, ask rather than guessing downward.
2. **Run the `update-improvement-plan-gaps` skill, then the `sync-improvement-plan-gaps` skill, in that order.** The
   first does a full codebase sweep for brand-new gaps against `documentation/roadmap/improvement-plan.md`/
   `improvement-plan-tasks.md`; the second then checks whether this branch's own diff has closed or progressed any of
   the gaps already tracked there (its own diff-gathering step needs the plan to already reflect anything new the
   first skill just found). Neither commits on its own — review their draft edits with the user before continuing.
3. **Bump `pom.xml`.** Update the `<version>` under `<project>` (not the parent POM's version) to `$VERSION`.
4. **Bump the OpenAPI version.** Update the `version` attribute of `@OpenAPIDefinition` in `HpscWebApplication.java` to
   match.
5. **Run the `sync-unreleased-changes` skill before touching CHANGELOG.md.** Release branches are cut from `develop`
   (per AGENTS.md's Branching Model), so invoke it with its default base (`develop`) — never skip this even if
   `[Unreleased]` looks complete: it cross-checks every commit and any uncommitted diff against the actual
   `[Unreleased]` entries, fills in anything missing, flags drifted entries and consolidates duplicate `##### <Area>`
   sub-headers. The next step renames `[Unreleased]` wholesale, so it must be fully accurate first. If it flags any
   entries as drifted, resolve those with the user before continuing — don't fold a flagged entry into the new version
   section unresolved. Re-run step 1's Semantic Versioning classification against the synced `[Unreleased]` section;
   if a newly surfaced entry changes it (e.g. an unflagged breaking change), stop, agree the corrected version with the
   user, and redo steps 3 and 4 with it before continuing.
6. **Add a `CHANGELOG.md` entry.** Rename `### 🧪 [Unreleased]`'s accumulated entries (now synced in the previous
   step) into a new `### 🧾 [$VERSION] - YYYY-MM-DD` section, keeping only the `#### <category>` headings that apply
   (`➕ Added`, `🔄 Changed`, `🐛 Fixed`, `⚠️ Deprecated`, `🗑️ Removed`, `🔐 Security`) and their `##### <Area>`
   subheadings. Update the Table of Contents and move the "← Current" marker to the new version, then start a fresh
   empty `### 🧪 [Unreleased]` section above it.
7. **Extend `HISTORY.md`.** Add a Historical Timeline entry and a Milestone for `$VERSION`, at the same narrative
   depth and style as the existing entries, placed at the top (immediately below the Overview section) to keep
   reverse chronological order, plus a matching Phase entry at the end of
   `documentation/history/EVOLUTION_OVERVIEW.md` (that file is `HISTORY.md`'s Evolution Overview section, split out
   once it grew to roughly half of `HISTORY.md`'s size — one paired Phase/Milestone entry still lands per release,
   just in two files now). If the release is significant enough to have shifted the project's trajectory, also
   thread it through the other version-by-version sections (Architectural Evolution, Feature Timeline, Key Learnings,
   Conclusion/footer), using the immediately preceding version's treatment as the template. Regardless of
   significance, always rename the Future Roadmap Implications section's current `### Recently Completed (vX.Y.Z)`
   entry to `### Previously Completed (vX.Y.Z)`, add a `### Recently Completed ($VERSION)` entry summarising this
   release at the same depth as the existing entries, and update the section's "Based on the evolution to vX.Y.Z"
   opening sentence to `$VERSION`. Also always update the "Major Version Goals" subsection under Project Philosophy
   Evolution: extend the current major version's `Version N.x (vN.0.0 – vX.Y.Z)` range to end at `$VERSION` and
   weave this release's driving goal into that entry's narrative, or, for a new major version, add a new
   `Version N.x ($VERSION)` entry. Then check whether `documentation/roadmap/improvement-plan.md`'s "⚙️ Goals &
   Constraints" table needs a matching update — it's synthesised partly from `HISTORY.md`'s Future Roadmap
   Implications sections, so a change here can leave that table stale.
8. **Update or create `RELEASE_NOTES.md`.** Follow the established section order: Theme → Key Highlights → What's New
   (Added/Changed/Fixed/Removed) → Migration Guide → Statistics → Design Notes → Testing → Known Issues → Future
   Enhancements → Contributors → Notes. Cover **everything** that changed for this version, not just the most recent
   commit. For the **Contributors** section, list every unique commit author on the release branch since it diverged
   from `main` — `git log main..HEAD --format='%an'` (or the equivalent GitHub "Contributors" view for the release's
   PRs), deduplicated — rather than a generic placeholder like "Development Team", and include every account found; bots
   (e.g. `dependabot[bot]`, `ImgBotApp`) included.
9. **Apply the reverse sync rule**: check whether any of this version's changes are relevant to `README.md` (goal, tech
   stack, project structure, quick start) or `ARCHITECTURE.md` (system design, layering, data flows) and update those
   files accordingly — keeping both release-agnostic (no version numbers or counts that drift, per AGENTS.md's Evergreen
   Documentation rules).
10. **Update `CONTRIBUTING.md`** only if this version's changes affect developer setup, database profiles, workflow or
    testing conventions documented there.
11. **Archive `RELEASE_NOTES.md`.** Once finalised, copy it byte-for-byte (no edits, no trimming) to
    `documentation/history/v<major>/RELEASE_NOTES_v$VERSION.md`, where `<major>` is `$VERSION`'s leading number
    before the first `.` (e.g. `7.2.0` → `v7`) — create that `v<major>/` directory first if this is the first
    release of a new major version.
12. **Write `documentation/history/v<major>/PR_DESCRIPTION_v$VERSION.md`** (same `v<major>/` subdirectory as
    step 11) — the body text for the release pull request. Keep it
    small — a PR body, not a second `RELEASE_NOTES.md`: a few bullets per section, high-level only. Structure:
    - `## 🎯 Summary` — two to four bullets on what the release is and why
    - `## 📦 Key Changes` — condensed from the CHANGELOG entry's categories (Added/Changed/Fixed/Removed), high-level
      rather than exhaustive
    - `## 🧪 Test Plan` — checklist of what was verified (build, tests, manual checks)
    - `## 🔗 Related Documentation` — links to `RELEASE_NOTES.md`, `CHANGELOG.md`, `HISTORY.md`
    - End the file with the standard Claude Code attribution footer (a blank line, then
      `🤖 Generated with [Claude Code](https://claude.com/claude-code)`) — this is a PR description drafted by Claude
      Code and should be marked as such, same as any other PR description it opens.

Commit these in logical chunks per AGENTS.md's Git Workflow — the version bump, the CHANGELOG/HISTORY/RELEASE_NOTES
documentation and the PR description are separate concerns. Do not run `git commit`, `git push` or open the PR
yourself — draft the files and stop for review.

## 📤 Output

Once all files above are written, tell the user the release branch (`release/v$VERSION`) is ready to open as a PR
against `develop` (per the GitFlow branching model in AGENTS.md), using
`documentation/history/v<major>/PR_DESCRIPTION_v$VERSION.md` as the PR body. Once that PR merges, remind them a second PR
promoting `develop` into `main` is still needed to actually ship the release — tag the resulting commit on `main` as
`v$VERSION`.

Remind the user to verify all tests still pass (`./mvnw test`) before finishing and confirm no version-specific info
leaked into `README.md`/`ARCHITECTURE.md`.
