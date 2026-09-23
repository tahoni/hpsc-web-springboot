## 🎯 Summary

- Splits `HISTORY.md`'s "📖 Evolution Overview" section — the Phase-by-phase narrative, roughly half the file's
  4,095 lines — out into new `documentation/history/EVOLUTION_OVERVIEW.md`, leaving a short pointer under the same
  heading/anchor in `HISTORY.md`.
- Regroups all 52 archived `RELEASE_NOTES_vX.Y.Z.md`/`PR_DESCRIPTION_vX.Y.Z.md` files from a flat
  `documentation/history/` directory into `v1/` – `v8/` subdirectories by major version, moved with `git mv` to
  preserve history.
- Updates every tool/doc that reads or writes those paths — `AGENTS.md`, `README.md`, and the
  `prep-version-release`/`generate-pr-summary`/`update-improvement-plan-gaps` skills — to match.
- Docs/tooling-only diff against `main`, so this scopes as `v8.6.1` **PATCH** rather than a new minor version,
  matching the precedent set by v8.4.1/v8.4.2/v8.5.1.

## 📦 Key Changes

- **Changed:** `HISTORY.md`'s Evolution Overview split into `documentation/history/EVOLUTION_OVERVIEW.md`;
  `documentation/history/`'s 52 versioned archive files regrouped into `v1/`–`v8/` subdirectories; `AGENTS.md`
  (Documentation File Map and both Release Checklist archive-path steps), `README.md`, and the
  `prep-version-release`/`generate-pr-summary`/`update-improvement-plan-gaps` skills updated to match.
- No `Added`/`Fixed`/`Deprecated`/`Removed`/`Security` changes this release — see `CHANGELOG.md`'s `[8.6.1]`
  section for the full itemised list.

## 🧪 Test Plan

- [x] `./mvnw test` passes cleanly (872 tests, 0 failures/errors — unchanged from v8.6.0, no source changes).
- [x] `./mvnw verify -Pcoverage` passes cleanly (coverage figures unchanged from v8.6.0).
- [x] Every moved file confirmed as a `git` rename (not delete+add); `git log --follow` traces full history on
      sampled v1/v8 files.
- [x] Repo-wide grep for `documentation/history/RELEASE_NOTES_v`/`PR_DESCRIPTION_v` outside `documentation/history/`
      and the accepted historical exceptions (`CHANGELOG.md`, `improvement-plan.md`) returns zero matches.
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.6.1.md`.
- [x] `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` checked — no new, closed or
      progressed gaps from this branch's diff.

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-861---2026-09-23)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
