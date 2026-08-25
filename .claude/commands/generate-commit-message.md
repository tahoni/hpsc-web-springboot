---
description: Generate a commit message (and matching CHANGELOG.md entry) for the current working tree changes, following AGENTS.md's Git Workflow conventions.
argument-hint: [optional scope to narrow the message to]
allowed-tools: Bash(git status:*), Bash(git diff:*), Bash(git --no-pager diff:*), Bash(git log:*), Read, Edit
---

# Generate Commit Message

Optional scope narrowing: $ARGUMENTS (limit the message to specific files/areas; leave blank for all staged/unstaged changes)

## 🔍 Current state

Status:
!`git status --short`

Diff stat:
!`git --no-pager diff --stat`

Full diff (staged and unstaged):
!`git --no-pager diff HEAD`

Conventions to follow: @AGENTS.md

## 🚀 Instructions

Read and strictly follow the **Git Workflow** section in AGENTS.md (loaded above). Treat AGENTS.md as the single source of truth; do not reinterpret or contradict its rules.

1. **Inspect the changes above**, do not guess — review the actual diff hunks so the message describes real behaviour, not assumptions. If `$ARGUMENTS` narrows the scope, only consider matching files.
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
5. **British English** spelling, grammar, and punctuation throughout (e.g. "licence", "colour", "initialise"), per AGENTS.md's Documentation Conventions.
6. **Sanity-check against conventions**: no secrets or credentials referenced, no vague messages such as "fixed stuff" or "updates".

## 📤 Output

Do **not** run `git add` or `git commit` yourself — this command only drafts, for the user to review and run.

1. The final commit message(s) as fenced code blocks, each followed by a ready-to-run `git commit` command
2. Any **CHANGELOG.md additions** in a separate fenced code block under the `## 🧪 [Unreleased]` section (the exact text to add, so the user can copy it directly into CHANGELOG.md — per AGENTS.md's rule, this update belongs in the same commit as the change it documents)
3. If proposing multiple commits, output one message block and one commit command per commit, in the order they should be made, followed by a single consolidated CHANGELOG.md block with all entries

Example output structure:

**Commit 1:**
```
docs: add commit message command
...
```

```bash
git commit -m "docs: add commit message command" ...
```

**CHANGELOG.md entries:**
```markdown
### ➕ Added

#### Tooling

- **`/generate-commit-message`:** New Claude Code command — drafts commit messages and matching CHANGELOG.md entries from the working tree diff
```
