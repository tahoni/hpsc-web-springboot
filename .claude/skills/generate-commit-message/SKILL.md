---
name: generate-commit-message
description: Generate a commit message (and matching CHANGELOG.md entry) for the current working tree changes, following AGENTS.md's Git Workflow conventions. Use whenever the user is about to commit, asks for a commit message/summary, or asks how to describe the current changes.
user-invocable: true
allowed-tools:
  - Bash(git status:*)
  - Bash(git diff:*)
  - Bash(git --no-pager diff:*)
  - Bash(git log:*)
  - Bash(git --no-pager log:*)
  - Bash(git merge-base:*)
  - Bash(git branch:*)
  - Read
  - Edit
---

# Generate Commit Message

Optional scope narrowing may be passed as `args` when this skill is invoked (limit the message to specific files/areas; leave blank for all staged/unstaged changes).

## 🔍 Gather current state

Before drafting, run these yourself and read their output:

1. `git status --short`
2. `git --no-pager diff --stat`
3. `git --no-pager diff HEAD` (full diff, staged and unstaged)
4. Commits already made on this branch — context only, may include commits made outside this session by anyone:
   ```
   base=$(git merge-base develop HEAD 2>/dev/null || git merge-base main HEAD 2>/dev/null)
   git --no-pager log --oneline "$base"..HEAD 2>/dev/null
   git --no-pager diff --stat "$base"..HEAD 2>/dev/null
   ```
5. Read `AGENTS.md` in full for conventions.

## 🚀 Instructions

Read and strictly follow the **Git Workflow** section in AGENTS.md, plus its **Build & Run Commands** and **Architecture** sections for accurate technical detail (build/test commands, package names, architecture) when describing what changed. Treat it as the single source of truth; do not reinterpret or contradict its rules.

This skill drafts a message for **whatever is currently staged/unstaged** — it is not limited to changes made in the current Claude session. Use the "commits already made on this branch" context above purely to stay consistent (matching scope naming, avoiding duplicate CHANGELOG entries for work already committed by anyone) — never fold already-committed work into the new message or CHANGELOG block. If that context reveals an already-committed change with no matching CHANGELOG entry, flag it to the user and point them at the `sync-unreleased-changes` skill rather than drafting it here.

1. **Inspect the changes above**, do not guess — review the actual diff hunks so the message describes real behaviour, not assumptions. If scope narrowing was passed in `args`, only consider matching files.
2. **Compose the message** in this exact shape:

   ```
   <scope>: <Brief description>

   - <optional bullet of notable detail>
   - <optional bullet of notable detail>
   ```

   - **Summary line**: `<scope>: <Brief description>` — imperative or descriptive, lower-case after the colon, no trailing period, ideally <= 72 characters. Use the scope prefix that matches this repo's history (`feat`, `fix`, `docs`, `refactor`, `test`, `chore`).
   - **Body bullets**: optional. Include them only when the change is non-obvious or touches multiple areas; each bullet should state *what* changed and *why*, not restate the file list.
   - If the change closes a GitHub issue, add a trailer line `Closes #<issue>` — only when there genuinely is one; don't invent a reference.
3. **Draft `CHANGELOG.md` entries** for the notable changes, to go under `## 🧪 [Unreleased]`. Per AGENTS.md's Documentation Conventions and the existing `[Unreleased]` entries in the file as a style reference:
   - Place entries under the matching standard subheading (`### ➕ Added`, `### 🔄 Changed`, `### 🐛 Fixed`, `### ⚠️ Deprecated`, `### 🗑️ Removed`, `### 🔐 Security`) — only the ones that apply.
   - Within each subheading, group related entries under a `#### <Area>` sub-header (e.g. `#### Domain`, `#### Repositories`, `#### Database`), matching the existing style in the file.
   - Each bullet: bold the backticked class/method/entity name, followed by a colon and a concise em-dash-separated description of what changed and why — e.g. `` - **`ShooterLog.powerFactor`:** New `PowerFactor` column — snapshots are now scoped by power factor as well as firearm type ``.
   - Be specific: name the actual class/file/behaviour, not vague statements like "improved tests".
4. **Group unrelated work**: if the diff contains clearly unrelated changes, propose separate commits with a message and separate CHANGELOG entries for each rather than forcing one message.
5. **British English** spelling, grammar and punctuation throughout (e.g. "licence", "colour", "initialise"), per AGENTS.md's Documentation Conventions.
6. **Sanity-check against conventions**: no secrets or credentials referenced, no vague messages such as "fixed stuff" or "updates".

## 📤 Output

Do **not** run `git add` or `git commit` yourself — this skill only drafts, for the user to review and run.

1. The final commit message(s) as fenced code blocks, each followed by a ready-to-run `git commit` command
2. Any **CHANGELOG.md additions** in a separate fenced code block under the `## 🧪 [Unreleased]` section (the exact text to add, so the user can copy it directly into CHANGELOG.md — per AGENTS.md's rule, this update belongs in the same commit as the change it documents)
3. If proposing multiple commits, output one message block and one commit command per commit, in the order they should be made, followed by a single consolidated CHANGELOG.md block with all entries

Example output structure:

**Commit 1:**
```
docs: add commit message skill
...
```

```bash
git commit -m "docs: add commit message skill" ...
```

**CHANGELOG.md entries:**
```markdown
### ➕ Added

#### Tooling

- **`generate-commit-message` skill:** Drafts commit messages and matching CHANGELOG.md entries from the working tree diff
```
