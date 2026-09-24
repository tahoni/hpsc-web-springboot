## 🎯 Summary

- Adds `DELETE /ipsc/competitors/{competitorId}` and `DELETE /ipsc/matches/{matchId}`, completing both domains'
  CRUD set and closing Gap #12.
- Records still referenced by match results, stage results or shooter logs are **refused with `400`, not
  cascaded**, so scoring history is never deleted as a side effect. A competitor's emails and a match's stages are
  deleted with them.
- Purely additive — no schema migration and no change to existing endpoints.

## 📦 Key Changes

**Added**

- `IpscCompetitorController.deleteCompetitor` / `IpscMatchController.deleteMatch` (`204`, `400` when referenced,
  `404` when missing), backed by new `deleteCompetitor`/`deleteMatch` service methods
- `existsBy…` dependent-row queries on four repositories
- Gap #13 in the improvement plan: the Claude Code review/assistant workflows aren't in the CI/CD documentation

**Changed**

- `ARCHITECTURE.md` documents the reject-not-cascade delete rule; `standard-rest-conventions.md`'s examples now
  cover both controllers' full verb sets
- Gap #12 closed; project version bumped to 8.8.0

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (901 tests, 0 failures/errors), up from 878 at v8.7.0
- [x] `./mvnw verify -Pcoverage` — 98.69% line / 99.03% branch coverage, JaCoCo gate passing
- [x] H2-backed integration tests confirm a real delete removes the record with its emails or stages, and that a
      referenced record is refused and kept
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.8.0.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's changes
- [x] `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` checked — a full sweep recorded new
      Gap #13; the branch-diff sync confirmed Gap #12's closure and found nothing else closed or progressed

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-880---2026-09-24)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
