---
description: Condense a version's PR_DESCRIPTION.md and RELEASE_NOTES.md into a very short, Bitbucket-style PR summary.
argument-hint: <version, e.g. 7.2.0>
allowed-tools: Bash(cat:*), Read
---

# Generate PR Summary

Version: $ARGUMENTS

## 🔍 Current state

PR description:
!`cat documentation/history/PR_DESCRIPTION_v$ARGUMENTS.md 2>/dev/null || echo "(not found — run /generate-pr-description $ARGUMENTS first)"`

Release notes:
!`cat documentation/history/RELEASE_NOTES_v$ARGUMENTS.md 2>/dev/null || echo "(not found — run /generate-pr-description $ARGUMENTS first)"`

Conventions to follow: @AGENTS.md

## 🚀 Instructions

If `$ARGUMENTS` is blank or either source file above wasn't found, ask the user which version to summarise rather than guessing.

Condense the two documents above (both already finalised for v$ARGUMENTS, per AGENTS.md's Release Checklist) into a single, very short PR summary — a bird's-eye view for a reviewer who won't read the full release notes, in the plain, low-ceremony style typical of a Bitbucket pull request description. This is a distillation, not a restatement: don't reproduce this repo's own emoji-heavy documentation style or `RELEASE_NOTES.md`'s full section-by-section detail.

1. **Read both source documents fully.** The release notes' Theme and Key Highlights sections are the best source for *why*; the PR description's Key Changes are the best source for *what*.
2. **Write the summary in three parts, no more:**
   - **One short paragraph** (2–3 sentences) stating what this PR does and why, distilled from the Theme/Summary — not copied verbatim.
   - **A "Key changes" bullet list** — at most 5–6 bullets, each as a single line, high-level only (no class/file-level detail, no nested sub-bullets).
   - **One line on verification**, only if it's notable (e.g. a real-database check, a coverage jump) — omit entirely if it's just "tests pass".
3. **Keep it short overall.** The whole thing should take less than 30 seconds to read. If a part would only restate the paragraph above, drop it.
4. **Use plain headings, not this repo's icon convention.** `## Summary` / `## Key changes` (or no headings at all if the content is short enough to stand alone) — never the emoji-prefixed headings used in `RELEASE_NOTES.md`/`CHANGELOG.md`.
5. **British English** spelling and grammar throughout, per AGENTS.md's Documentation Conventions.

## 📤 Output

Output the finished PR summary as a single fenced code block (language tag `markdown`) containing the raw Markdown source as literal text — headings as `##`, bold as `**text**`, bullets as `-`, left unrendered so the user can copy the exact source, not Claude's own rendered formatting. Ready to paste directly into a Bitbucket pull request description. Don't write it to a file — this command only drafts, for the user to copy.
