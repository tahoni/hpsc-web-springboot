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
Checklist**, **Documentation Conventions**, **Git Workflow** (Branching Model), **Evergreen Documentation** (reverse
sync rule), **Build & Run Commands** and **Architecture** sections for accurate technical detail (build/test commands,
package layout, database profiles) when writing `RELEASE_NOTES.md`/the PR description. Treat it as the single source of
truth; do not reinterpret or contradict its rules. Follow the Release Checklist steps **in order** — the version number
and date must be final before anything downstream references them.

Steps:

1. **Confirm the diff against `main`** (gathered above) covers everything that changed for this release — re-run
   `git log main..HEAD` / `git diff --stat main...HEAD` yourself if the branch has moved on since this skill started.
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
   `[Unreleased]` entries, fills in anything missing, flags drifted entries, and consolidates duplicate `#### <Area>`
   sub-headers. The next step renames `[Unreleased]` wholesale, so it must be fully accurate first. If it flags any
   entries as drifted, resolve those with the user before continuing — don't fold a flagged entry into the new version
   section unresolved.
6. **Add a `CHANGELOG.md` entry.** Rename `## 🧪 [Unreleased]`'s accumulated entries (now synced in the previous step)
   into a new `## 🧾 [$VERSION] - YYYY-MM-DD` section, using only the categories that apply (`➕ Added`, `🔄 Changed`,
   `🐛 Fixed`, `⚠️ Deprecated`, `🗑️ Removed`, `🔐 Security`). Update the Table of Contents and move the "← Current" marker
   to the new version, then start a fresh empty `## 🧪 [Unreleased]` section above it.
7. **Extend `HISTORY.md`.** Add a Historical Timeline entry, a Phase and a Milestone for `$VERSION`, at the same
   narrative depth and style as the existing entries, placed at the top (immediately below the Overview section) to keep
   reverse chronological order. If the release is significant enough to have shifted the project's trajectory, also
   thread it through the other version-by-version sections (Architectural Evolution, Feature Timeline, Key Learnings,
   Future Roadmap, Conclusion/footer), using the immediately preceding version's treatment as the template.
8. **Update or create `RELEASE_NOTES.md`.** Follow the established section order: Theme → Key Highlights → What's New
   (Added/Changed/Fixed/Removed) → Migration Guide → Statistics → Design Notes → Testing → Known Issues → Future
   Enhancements → Contributors → Notes. Cover **everything** that changed for this version, not just the most recent
   commit. For the **Contributors** section, list every unique commit author on the release branch since it diverged
   from `main` — `git log main..HEAD --format='%an'` (or the equivalent GitHub "Contributors" view for the release's
   PRs), deduplicated — rather than a generic placeholder like "Development Team", and include every account found, bots
   (e.g. `dependabot[bot]`, `ImgBotApp`) included.
9. **Apply the reverse sync rule**: check whether any of this version's changes are relevant to `README.md` (goal, tech
   stack, project structure, quick start) or `ARCHITECTURE.md` (system design, layering, data flows) and update those
   files accordingly — keeping both release-agnostic (no version numbers or counts that drift, per AGENTS.md's Evergreen
   Documentation rules).
10. **Update `CONTRIBUTING.md`** only if this version's changes affect developer setup, database profiles, workflow or
    testing conventions documented there.
11. **Archive `RELEASE_NOTES.md`.** Once finalised, copy it byte-for-byte (no edits, no trimming) to
    `documentation/history/RELEASE_NOTES_v$VERSION.md`.
12. **Write `documentation/history/PR_DESCRIPTION_v$VERSION.md`** — the body text for the release pull request. Keep it
    small — a PR body, not a second `RELEASE_NOTES.md`: a few bullets per section, high-level only. Structure:
    - `## 🎯 Summary` — two to four bullets on what the release is and why
    - `## 📦 Key Changes` — condensed from the CHANGELOG entry's categories (Added/Changed/Fixed/Removed), high-level
      rather than exhaustive
    - `## 🧪 Test Plan` — checklist of what was verified (build, tests, manual checks)
    - `## 🔗 Related Documentation` — links to `RELEASE_NOTES.md`, `CHANGELOG.md`, `HISTORY.md`

Commit these in logical chunks per AGENTS.md's Git Workflow — the version bump, the CHANGELOG/HISTORY/RELEASE_NOTES
documentation and the PR description are separate concerns. Do not run `git commit`, `git push` or open the PR
yourself — draft the files and stop for review.

## 📤 Output

Once all files above are written, tell the user the release branch (`release/v$VERSION`) is ready to open as a PR
against `develop` (per the GitFlow branching model in AGENTS.md), using
`documentation/history/PR_DESCRIPTION_v$VERSION.md` as the PR body. Once that PR merges, remind them a second PR
promoting `develop` into `main` is still needed to actually ship the release — tag the resulting commit on `main` as
`v$VERSION`.

Remind the user to verify all tests still pass (`./mvnw test`) before finishing and confirm no version-specific info
leaked into `README.md`/`ARCHITECTURE.md`.
