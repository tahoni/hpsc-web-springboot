---
name: update-improvement-plan-gaps
description: Audit the codebase against documentation/roadmap/improvement-plan.md and improvement-plan-tasks.md, and record any newly identified gaps (or newly-closed/newly-progressed ones) in both files. Use whenever the user asks to check for new gaps, re-audit the improvement plan, or update the roadmap after a chunk of work has landed.
user-invocable: true
allowed-tools:
  - Bash(git branch:*)
  - Bash(git log:*)
  - Bash(git --no-pager log:*)
  - Bash(git diff:*)
  - Bash(git --no-pager diff:*)
  - Bash(./mvnw verify:*)
  - Bash(awk:*)
  - Read
  - Grep
  - Glob
  - Edit
  - Write
---

# Update Improvement Plan Gaps

An optional focus area or hint can be passed as `args` (e.g. `"check the scoring domain"` or a specific gap number to
re-verify) — steer the audit toward it, but still do the full sweep below rather than skipping straight to it.

## 🔍 Gather current state

Read these fully before drafting anything — you cannot tell what's *new* without first knowing what's already tracked:

1. `documentation/roadmap/improvement-plan.md` and `improvement-plan-tasks.md`, in full. Both group gaps into three
   status sections — ✅ Completed, 🟡 Partially Completed, ⚪ Open — mirrored identically across the two files.
   Note every gap's number, title, current section and status suffix (✅ Closed in vX.Y.Z / 🟡 Partially completed
   in vX.Y.Z / no suffix if ⚪ Open) — this is the baseline you're diffing against, and gap numbers are never
   reused or renumbered, even when a gap moves between sections.
2. `git branch --show-current` — if it's a `release/vX.Y.Z` branch, that's the version any newly closed gap should be
   annotated with; don't guess or reuse an already-shipped version number (a gap closed by work on this branch was
   **not** closed in whatever version `HISTORY.md`'s most recent entry already covers).
3. `AGENTS.md`, `README.md`, `ARCHITECTURE.md`, `CLAUDE.md`, `CONTRIBUTING.md` — the same sources
   `improvement-plan.md`'s own "🎯 Purpose & Scope" section says it draws from.
4. `HISTORY.md`'s most recent "🚀 Future Roadmap Implications" entries, and the most recent
   `documentation/history/RELEASE_NOTES_v*.md`'s "Known Issues" and "Future Enhancements" sections.
5. `.github/workflows/`, `pom.xml`, `application*.properties` — config that a doc's claims might have drifted from.

## 🚀 Instructions

Look specifically for the same four things the plan's existing gaps were built from:

1. **A stated-but-unbuilt goal.** Any place a doc uses language like "not yet implemented", "still being built",
   "in progress", "groundwork only", "TODO" or "future enhancement" to describe real work — and that specific gap
   isn't yet one of the tracked numbers from step 1 above.
2. **Doc-vs-doc or doc-vs-code disagreement.** The same category of drift the existing gaps were built from — e.g.
   two docs contradicting each other, or a doc's claim not matching what `Glob`/`Grep`/`Read` show in the actual
   source tree. That is, an unused class, a workflow file that doesn't exist, a config flag nobody reads.
3. **A stale number.** A percentage, count or version reference stated in a doc that a quick check (e.g.
   `./mvnw verify -Pcoverage` for a coverage figure, or `git log --oneline | wc -l` for a commit count) shows is no
   longer accurate. Don't run expensive checks speculatively — only when a doc actually states a checkable number.
4. **A newly met precondition on an existing gap.** Re-read each ⚪ Open (or 🟡 Partially Completed) gap's own text
   for phrases like "once X exists" or "once the service layer lands" — if recent work (check `git log` since the
   gap was last touched) has now satisfied that precondition, that gap's section should move; even if it isn't
   fully closed yet.

For anything you find, follow `improvement-plan.md`'s own established template exactly — read a couple of its
existing gap sections first and match their shape and tone, not just their headings:

- **New gap:** append `#### N. <Title>` (next sequential number — never reuse or resequence existing numbers) under
  the "⚪ Open" subsection of "🔍 Gaps & Improvement Opportunities", with **Evidence:**, **Why it matters:**, and
  **Proposed improvement:** paragraphs, citing the exact files/lines that back it up.
- **Newly closed gap:** append a `— ✅ Closed in vX.Y.Z` suffix to that gap's `####` header (per step 2 above for
  the version) and add an **Outcome:** paragraph, then move the whole `#### N. ...` block into the "✅ Completed"
  subsection — never delete or rewrite the original analysis, per the plan's own "✅ Success Criteria" instruction
  to move closed items into `HISTORY.md` rather than erasing them here.
- **Newly progressed (not yet closed) gap:** add a `— 🟡 Partially completed in vX.Y.Z`-style suffix and a
  **Progress:** paragraph describing what changed and what's still open, then move the whole block into the
  "🟡 Partially Completed" subsection (replacing its "*No gaps are currently partially completed*" placeholder text
  if it's currently empty).
- Update the "⚙️ Goals & Constraints" table, the "🚀 Roadmap" table, and the "✅ Success Criteria" list to match
  every change made above — these three sections drift out of sync with the gap list if touched inconsistently.

Then mirror every change into `improvement-plan-tasks.md`, under the same ✅ Completed / 🟡 Partially Completed /
⚪ Open sections:

- A new gap gets a new checkbox block under "⚪ Open" (match the phrasing style of the "🚀 Roadmap" table entry you
  just wrote in `improvement-plan.md`).
- Check off items in place for closed/progressed work — **never delete a task line**, per the file's own closing
  instruction. Add a short note after a checked item explaining how it was actually fulfilled if that differs from
  the original wording.
- If a gap's status changes (e.g. its first item gets checked, or every item is now checked), move its whole
  block to the matching section (⚪ Open → 🟡 Partially Completed → ✅ Completed) rather than leaving a stale
  copy behind. Never skip a section (a gap with only some items checked belongs in 🟡 Partially Completed, not
  ✅ Completed).

Before finishing, run a line-wrap check on both files' diffs (100–120 characters; tables, code blocks and diagrams
are exempt, per `AGENTS.md`'s Documentation Conventions):

```
awk '{ if (length($0) > 120) print FILENAME":"FNR": "length($0) }' documentation/roadmap/improvement-plan.md documentation/roadmap/improvement-plan-tasks.md
```

If the sweep finds nothing new and nothing to progress, say so plainly rather than manufacturing a gap to report.

Do not run `git commit` or `git push` — draft the edits and stop for the user's review, same as `prep-version-release`
does.

## 📤 Output

Summarise what changed: new gap numbers added, any gaps closed/progressed and the version they're attributed to, and
which of the four checks above (if any) turned up nothing. Point the user at the specific `###` sections so they can
review the evidence before it's committed.
