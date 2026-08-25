---
description: Prepare a new version release — RELEASE_NOTES.md, CHANGELOG.md, HISTORY.md, reverse-synced docs, and a draft release PR description — following AGENTS.md's Release Checklist.
argument-hint: <version, e.g. 7.2.0>
allowed-tools: Bash(git log:*), Bash(git diff:*), Bash(git --no-pager diff:*), Bash(git branch:*), Read, Edit, Write
---

# Generate New Version PR

Prepare a release for version: $1

## 🔍 Current state

Branch:
!`git branch --show-current`

Changes relative to main:
!`git --no-pager diff --stat main...HEAD`

Commit log relative to main:
!`git log main..HEAD --oneline`

Conventions to follow: @AGENTS.md @CLAUDE.md

## 🚀 Instructions

Read and strictly follow **all conventions defined in AGENTS.md and CLAUDE.md** (both loaded above) — in particular AGENTS.md's **Release Checklist**, **Documentation Conventions**, **Git Workflow** (Branching Model), and **Evergreen Documentation** (reverse sync rule) sections, and CLAUDE.md for accurate technical detail (build/test commands, package layout, database profiles) when writing `RELEASE_NOTES.md`/the PR description. Treat both as the single source of truth; do not reinterpret or contradict their rules. Follow the Release Checklist steps **in order** — the version number and date must be final before anything downstream references them.

Steps:
1. **Confirm the diff against `main`** (shown above) covers everything that changed for this release — re-run `git log main..HEAD` / `git diff --stat main...HEAD` yourself if the branch has moved on since this command started.
2. **Bump `pom.xml`.** Update the `<version>` under `<project>` (not the parent POM's version) to `$1`.
3. **Bump the OpenAPI version.** Update the `version` attribute of `@OpenAPIDefinition` in `HpscWebApplication.java` to match.
4. **Add a `CHANGELOG.md` entry.** Rename `## 🧪 [Unreleased]`'s accumulated entries into a new `## 🧾 [$1] - YYYY-MM-DD` section, using only the categories that apply (`➕ Added`, `🔄 Changed`, `🐛 Fixed`, `⚠️ Deprecated`, `🗑️ Removed`, `🔐 Security`). Update the Table of Contents and move the "← Current" marker to the new version, then start a fresh empty `## 🧪 [Unreleased]` section above it.
5. **Extend `HISTORY.md`.** Add a Historical Timeline entry, a Phase, and a Milestone for `$1`, at the same narrative depth and style as the existing entries, placed at the top (immediately below the Overview section) to keep reverse chronological order. If the release is significant enough to have shifted the project's trajectory, also thread it through the other version-by-version sections (Architectural Evolution, Feature Timeline, Key Learnings, Future Roadmap, Conclusion/footer), using the immediately preceding version's treatment as the template.
6. **Update or create `RELEASE_NOTES.md`.** Follow the established section order: Theme → Key Highlights → What's New (Added/Changed/Fixed/Removed) → Migration Guide → Statistics → Design Notes → Testing → Known Issues → Future Enhancements → Contributors → Notes. Cover **everything** that changed for this version, not just the most recent commit.
7. **Apply the reverse sync rule**: check whether any of this version's changes are relevant to `README.md` (goal, tech stack, project structure, quick start) or `ARCHITECTURE.md` (system design, layering, data flows) and update those files accordingly — keeping both release-agnostic (no version numbers or counts that drift, per AGENTS.md's Evergreen Documentation rules).
8. **Update `CONTRIBUTING.md`** only if this version's changes affect developer setup, database profiles, workflow, or testing conventions documented there.
9. **Archive `RELEASE_NOTES.md`.** Once finalised, copy it byte-for-byte (no edits, no trimming) to `documentation/history/RELEASE_NOTES_v$1.md`.
10. **Write `documentation/history/PR_DESCRIPTION_v$1.md`** — the body text for the release pull request. Keep it small — a PR body, not a second `RELEASE_NOTES.md`: a few bullets per section, high-level only. Structure:
    - `## 🎯 Summary` — two to four bullets on what the release is and why
    - `## 📦 Key Changes` — condensed from the CHANGELOG entry's categories (Added/Changed/Fixed/Removed), high-level rather than exhaustive
    - `## 🧪 Test Plan` — checklist of what was verified (build, tests, manual checks)
    - `## 🔗 Related Documentation` — links to `RELEASE_NOTES.md`, `CHANGELOG.md`, `HISTORY.md`

Commit these in logical chunks per AGENTS.md's Git Workflow — the version bump, the CHANGELOG/HISTORY/RELEASE_NOTES documentation, and the PR description are separate concerns. Do not run `git commit`, `git push`, or open the PR yourself — draft the files and stop for review.

## 📤 Output

Once all files above are written, tell the user the release branch (`release/v$1`) is ready to open as a PR against `develop` (per the GitFlow branching model in AGENTS.md), using `documentation/history/PR_DESCRIPTION_v$1.md` as the PR body. Once that PR merges, remind them a second PR promoting `develop` into `main` is still needed to actually ship the release — tag the resulting commit on `main` as `v$1`.

Remind the user to verify all tests still pass (`./mvnw test`) before finishing and confirm no version-specific info leaked into `README.md`/`ARCHITECTURE.md`.
