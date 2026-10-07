## 🎯 Summary

- Backfills three releases' worth of missing `HISTORY.md` Phase/Milestone entries (v8.4.1, v8.4.2, v8.5.0), closing
  `improvement-plan.md`'s Gap #10.
- Brings `HISTORY.md`'s own version-keyed sections into consistent ascending order and prunes a stale legacy footer
  from its Conclusion section.
- Re-scopes this release from the originally-planned `v8.6.0` **MINOR** version down to `v8.5.1` **PATCH**, after
  confirming the branch's full diff against `main` is documentation/tooling-only — matching the precedent set by
  v8.4.1/v8.4.2.
- Adds a standard Claude Code attribution footer to the `prep-version-release`/`generate-pr-summary` skills' drafted
  output, and picks up a couple of unrelated wording fixes (`CONTRIBUTING.md`, a Flyway comment).

## 📦 Key Changes

- **Added:** `HISTORY.md` Phase 26/27/28/29 and Milestone 26/27/28/29 entries; `improvement-plan.md`'s new "📋 At a
  Glance" gap-status index.
- **Changed:** `improvement-plan.md`/`improvement-plan-tasks.md`'s Roadmap table and Gap #10 lifecycle brought fully
  up to date; `HISTORY.md`'s "Major Version Goals" extended through v8.5.1 and its Evolution Overview/Major
  Milestones/Architectural Evolution/Future Roadmap Implications sections reordered to ascending (oldest-first);
  `CONTRIBUTING.md`/`application-local.properties` wording tightened; `prep-version-release`/`generate-pr-summary`
  skills gained an attribution footer.
- **Removed:** `HISTORY.md`'s stale legacy "Document Created"/"Last Updated"/"Coverage" metadata and "Recent
  Updates"/"Previous Update" update log, pruned from the end of its Conclusion section.
- No `Fixed`/`Deprecated`/`Security` changes this release — see `CHANGELOG.md`'s `[8.5.1]` section for the full
  itemised list.

## 🧪 Test Plan

- [ ] `./mvnw test` passes cleanly (no source changes in this release; last confirmed at v8.5.0 with 870 tests).
- [ ] `./mvnw verify -Pcoverage` passes cleanly (coverage figures unchanged from v8.5.0).
- [x] Verified `ARCHITECTURE.md`'s Project Structure tree still matches disk (no drift found).
- [x] Verified no version-specific numbers leaked into `README.md`/`ARCHITECTURE.md`.

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-851---2026-09-13)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
