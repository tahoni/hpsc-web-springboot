---
mode: agent
description: Prepare a new version release — RELEASE_NOTES.md, CHANGELOG.md, HISTORY.md, reverse-synced docs, and a draft release PR description — following this project's Release Checklist in AGENTS.md.
---

# Generate New Version PR

Prepare a release for version: ${input:version:New release version number, e.g., 7.2.0}

## Instructions

Read and strictly follow **all conventions defined in [AGENTS.md](../../AGENTS.md)** — in particular the **Release Checklist**, **Documentation Conventions**, **Git Workflow** (Branching Model), and **Evergreen Documentation** (reverse sync rule) sections. Treat AGENTS.md as the single source of truth; do not reinterpret or contradict its rules. Follow the Release Checklist steps **in order** — the version number and date must be final before anything downstream references them.

Steps:
1. **Diff the `release/v${input:version}` branch against `main`** to determine everything that changed (`git log main..HEAD`, `git diff --stat main...HEAD`) — confirm full coverage before writing anything.
2. **Bump `pom.xml`.** Update the `<version>` under `<project>` (not the parent POM's version) to `${input:version}`.
3. **Bump the OpenAPI version.** Update the `version` attribute of `@OpenAPIDefinition` in `HpscWebApplication.java` to match.
4. **Add a `CHANGELOG.md` entry.** Rename `## 🧪 [Unreleased]`'s accumulated entries into a new `## 🧾 [${input:version}] - YYYY-MM-DD` section, using only the categories that apply (`➕ Added`, `🔄 Changed`, `🐛 Fixed`, `⚠️ Deprecated`, `🗑️ Removed`, `🔐 Security`). Update the Table of Contents and move the "← Current" marker to the new version, then start a fresh empty `## 🧪 [Unreleased]` section above it.
5. **Extend `HISTORY.md`.** Add a Historical Timeline entry, a Phase, and a Milestone for `${input:version}`, at the same narrative depth and style as the existing entries, placed at the top (immediately below the Overview section) to keep reverse chronological order. If the release is significant enough to have shifted the project's trajectory, also thread it through the other version-by-version sections (Architectural Evolution, Feature Timeline, Key Learnings, Future Roadmap, Conclusion/footer), using the immediately preceding version's treatment as the template.
6. **Update or create `RELEASE_NOTES.md`.** Follow the established section order: Theme → Key Highlights → What's New (Added/Changed/Fixed/Removed) → Migration Guide → Statistics → Design Notes → Testing → Known Issues → Future Enhancements → Contributors → Notes. Cover **everything** that changed for this version, not just the most recent commit.
7. **Apply the reverse sync rule**: check whether any of this version's changes are relevant to `README.md` (goal, tech stack, project structure, quick start) or `ARCHITECTURE.md` (system design, layering, data flows) and update those files accordingly — keeping both release-agnostic (no version numbers or counts that drift, per AGENTS.md's Evergreen Documentation rules).
8. **Update `CONTRIBUTING.md`** only if this version's changes affect developer setup, database profiles, workflow, or testing conventions documented there.
9. **Archive `RELEASE_NOTES.md`.** Once finalised, copy it byte-for-byte (no edits, no trimming) to `documentation/history/RELEASE_NOTES_v${input:version}.md`.
10. **Write `documentation/history/PR_DESCRIPTION_v${input:version}.md`** — the body text for the release pull request. Keep it small — a PR body, not a second `RELEASE_NOTES.md`: a few bullets per section, high-level only. Structure:
    - `## 🎯 Summary` — two to four bullets on what the release is and why
    - `## 📦 Key Changes` — condensed from the CHANGELOG entry's categories (Added/Changed/Fixed/Removed), high-level rather than exhaustive
    - `## 🧪 Test Plan` — checklist of what was verified (build, tests, manual checks)
    - `## 🔗 Related Documentation` — links to `RELEASE_NOTES.md`, `CHANGELOG.md`, `HISTORY.md`

Commit these in logical chunks per AGENTS.md's Git Workflow — the version bump, the CHANGELOG/HISTORY/RELEASE_NOTES documentation, and the PR description are separate concerns.

## Output

Once all files above are written, open the PR from `release/v${input:version}` against `main` (per the GitFlow branching model in AGENTS.md), using `documentation/history/PR_DESCRIPTION_v${input:version}.md` as the PR body.

Verify all tests still pass (`./mvnw test`) before finishing, and confirm no version-specific info leaked into `README.md`/`ARCHITECTURE.md`.
