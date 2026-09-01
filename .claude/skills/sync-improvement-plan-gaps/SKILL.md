---
name: sync-improvement-plan-gaps
description: Check the current branch's actual changes against documentation/roadmap/improvement-plan.md's existing gaps, and mark any that this branch has closed or progressed. Use whenever the user asks to sync/update the improvement plan after landing work, or as a prerequisite before drafting a release.
user-invocable: true
allowed-tools:
  - Bash(git status:*)
  - Bash(git branch:*)
  - Bash(git merge-base:*)
  - Bash(git log:*)
  - Bash(git --no-pager log:*)
  - Bash(git diff:*)
  - Bash(git --no-pager diff:*)
  - Bash(awk:*)
  - Read
  - Edit
---

# Sync Improvement Plan Gaps

An optional base branch override may be passed as `args` (defaults to `develop`; use `main` instead when the current
branch is a `hotfix/*` branch, per AGENTS.md's Git Workflow).

This skill is deliberately narrow: it only re-checks gaps `improvement-plan.md` **already tracks**, against what this
branch actually changed. It never looks for brand-new gaps — that's `update-improvement-plan-gaps`' job, which audits
the whole codebase against the docs rather than a branch's diff. Run this one after landing a chunk of work; run that
one for a periodic full sweep.

## 🔍 Gather current state

Before drafting, run these yourself and read their output:

1. `git branch --show-current`
2. `git branch --list develop main` (base branch candidates)
3. Determine the merge base: `git merge-base HEAD <base>` where `<base>` is `args` if supplied, falling back to
   `develop`, then `main`. If the current branch **is** `develop` or `main` (nothing to diff against itself), fall
   back to the previous commit reachable from `HEAD` that looks like a release/merge boundary, or ask the user for a
   comparison point rather than guessing.
4. `git --no-pager log --oneline <merge-base>..HEAD` (commits on this branch not yet on the base branch)
5. `git --no-pager diff <merge-base>..HEAD` (full diff of this branch against its base)
6. `git status --short` (working tree status — uncommitted changes, if any)
7. `git --no-pager diff HEAD` (uncommitted diff, staged and unstaged, if any)
8. Read `documentation/roadmap/improvement-plan.md` and `improvement-plan-tasks.md` in full. For every gap that is
   not already annotated `✅ Closed`, note its number, title and — most importantly — its **Proposed improvement**
   text: that's the concrete claim you're checking the diff against.
9. Read `AGENTS.md` in full for conventions.

## 🚀 Instructions

1. **Combine the committed and uncommitted diffs** from steps 4-7 above into one change set — that's everything this
   branch introduces relative to its base.
2. **For each open (or only-partially-progressed) gap**, decide whether the diff satisfies its Proposed improvement,
   in full or in part:
    - Read the gap's Proposed improvement text closely — a gap is only "closed" when the diff does what it actually
      asks, not merely something adjacent or related to the same area of code.
    - Search the diff for the specific files, classes, config or workflows the gap's Evidence names — a gap can't be
      closed by a diff that never touches what its Evidence pointed at.
    - A gap can be **fully closed**, **newly progressed but still open**, or **untouched** by this branch — most gaps
      will be untouched on any given branch, and that's the expected, unremarkable outcome.
3. **Leave every gap this branch didn't touch exactly as it is.** This skill corrects/adds status for what changed on
   this branch — it does not re-litigate, reword or re-open gaps the branch had nothing to do with.
4. **For a newly closed gap**, in `improvement-plan.md`:
    - Append `— ✅ Closed in vX.Y.Z` to that gap's `###` header, where the version is read from the current
      `release/vX.Y.Z` branch name (or, off a release branch, ask the user which version this will ship as — never
      guess or reuse an already-shipped version number).
    - Add an **Outcome:** paragraph describing what actually shipped and citing the specific files/commits, without
      deleting or rewriting the original Evidence/Why it matters/Proposed improvement analysis.
5. **For a newly progressed (not yet closed) gap**, add a `— 🟡 Partially progressed in vX.Y.Z`-style suffix to the
   header and a **Progress:** paragraph describing what changed and what's still outstanding — matching the tone of
   this plan's existing Progress/Outcome notes.
6. **Update the "⚙️ Goals & Constraints" table, the "🚀 Roadmap" table, and the "✅ Success Criteria" list** in
   `improvement-plan.md` wherever a status change above affects them (e.g. a gap moving out of the Now/Next phase, or
   a Success Criteria bullet becoming met) — these drift out of sync with the gap list if touched inconsistently.
7. **Never add a new gap number.** If the diff reveals something that looks like a genuinely new, previously-untracked
   gap, mention it in the output below instead of adding it here — that's `update-improvement-plan-gaps`' job, not
   this skill's.
8. **Mirror every status change into `improvement-plan-tasks.md`**, under the same Now/Next/Later/Ongoing phase
   headers:
    - Check off the specific task-list items this branch's diff fulfilled — **never delete a task line**, per the
      file's own closing instruction. Add a short note after each checked item explaining how it was actually
      fulfilled if that differs from the original wording.
    - Annotate the block's header the same way as the corresponding `improvement-plan.md` section (✅ Closed / 🟡
      Partially progressed).
    - If a gap's phase changes as a result (e.g. promoted from Later to Next because this branch met a precondition),
      move its whole block to the new phase section rather than leaving a stale copy behind.

Before finishing, run a line-wrap check on both files:

```
awk '{ if (length($0) > 120) print FILENAME":"FNR": "length($0) }' documentation/roadmap/improvement-plan.md documentation/roadmap/improvement-plan-tasks.md
```

Do not run `git commit` or `git push` — draft the edits and stop for the user's review, same as `prep-version-release`
does.

## 📤 Output

Report concisely:

1. **Gaps checked** — how many open/partial gaps were evaluated against this branch's diff.
2. **Newly closed**, each with the version it's attributed to and a one-line reason (or "None.").
3. **Newly progressed but still open**, each with a one-line reason (or "None.").
4. **Untouched by this branch** — just the count, not a re-listing of every gap.
5. **Anything that looked like a genuinely new gap** while reading the diff, flagged for a separate
   `update-improvement-plan-gaps` run rather than added here (or "None spotted.").
