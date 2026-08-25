---
description: Ensure every notable change on the current branch is reflected in CHANGELOG.md's [Unreleased] section, filling in anything missing.
argument-hint: [optional base branch to diff against, defaults to develop]
allowed-tools: Bash(git status:*), Bash(git branch:*), Bash(git merge-base:*), Bash(git log:*), Bash(git --no-pager log:*), Bash(git diff:*), Bash(git --no-pager diff:*), Read, Edit
---

# Sync Unreleased Changes

Optional base branch override: $ARGUMENTS (defaults to `develop`; use `main` instead when the current branch is a `hotfix/*` branch, per AGENTS.md's Git Workflow)

## 🔍 Current state

Current branch:
!`git branch --show-current`

Base branch candidates:
!`git branch --list develop main`

Merge base with base branch:
!`git merge-base HEAD $ARGUMENTS 2>/dev/null || git merge-base HEAD develop 2>/dev/null || git merge-base HEAD main`

Commits on this branch not yet on the base branch:
!`git --no-pager log --oneline $(git merge-base HEAD ${ARGUMENTS:-develop} 2>/dev/null || git merge-base HEAD develop 2>/dev/null || git merge-base HEAD main)..HEAD`

Full diff of this branch against its base:
!`git --no-pager diff $(git merge-base HEAD ${ARGUMENTS:-develop} 2>/dev/null || git merge-base HEAD develop 2>/dev/null || git merge-base HEAD main)..HEAD`

Working tree status (uncommitted changes, if any):
!`git status --short`

Uncommitted diff (staged and unstaged), if any:
!`git --no-pager diff HEAD`

Current `[Unreleased]` section of CHANGELOG.md:
@CHANGELOG.md

Conventions to follow: @AGENTS.md @CLAUDE.md

## 🚀 Instructions

Read and strictly follow the **Documentation Conventions** and **Git Workflow** sections in AGENTS.md (loaded above), plus CLAUDE.md for accurate technical detail when describing what changed. Treat both as the single source of truth.

1. **Determine the branch's full change set.** Combine the committed diff (branch vs. base) and any uncommitted working-tree diff above — together these are every change this branch introduces. Ignore merge commits' own diffs; look at the actual content changes.
2. **Read the existing `## 🧪 [Unreleased]` section** of CHANGELOG.md (loaded above) and build a mental list of what it already documents.
3. **Cross-check every notable change against that list.** A change is "notable" per Keep a Changelog norms — new/changed/fixed/deprecated/removed behaviour, public API, schema, config, or documentation structure. Skip purely mechanical noise (formatting-only diffs, generated file churn) unless AGENTS.md calls it out specifically (e.g. dependency security overrides *are* notable).
4. **For each notable change not already covered**, draft a CHANGELOG entry:
   - Place it under the matching standard subheading (`### ➕ Added`, `### 🔄 Changed`, `### 🐛 Fixed`, `### ⚠️ Deprecated`, `### 🗑️ Removed`, `### 🔐 Security`) — create the subheading if the file's `[Unreleased]` section is missing it, in that category order.
   - Group it under the right `#### <Area>` sub-header (matching existing area names already used in the file where one fits, e.g. `Domain`, `Repositories`, `Documentation`, `Tooling`, `Configuration`; introduce a new one only if nothing existing fits).
   - Follow the established bullet style: bold the backticked class/method/file/entity name, a colon, then a concise em-dash-separated description of what changed and why.
   - British English spelling and grammar throughout.
5. **For each change already covered**, verify the existing entry is still accurate against the actual diff (right file/class named, description still matches what the code does) — flag any that have drifted, but don't rewrite entries that are still correct just to change their wording.
6. **Do not remove or alter entries** for changes unrelated to this branch's diff — this command only adds/corrects coverage for what this branch actually introduced.
7. **Apply the edits directly to `CHANGELOG.md`** using Edit, inserting each new bullet under its correct subheading/area (creating empty category headings only if genuinely needed, matching the file's existing heading order) — do not leave the fix as a suggestion. Do not touch the Table of Contents or any released version section.

## 📤 Output

After editing CHANGELOG.md, report concisely:

1. **Coverage check summary** — how many notable changes were found on the branch, how many were already documented, how many were newly added.
2. **What was added**, as a fenced `markdown` diff-style list of the new bullets (or "Nothing to add — `[Unreleased]` already covers every notable change on this branch.").
3. **Any entries flagged as drifted** (inaccurate against the current diff) that need manual review, with a one-line reason each — do not silently rewrite these; call them out for the user to confirm.
