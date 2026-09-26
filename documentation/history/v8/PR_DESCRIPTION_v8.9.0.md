## 🎯 Summary

- Adds nullable **`paidUpSapsa`/`paidUpClub`** flags to competitors (Flyway `V7_8_0`), through the JSON API and CSV
  import. The competitor CSV now **requires `PaidUpSapsa`/`PaidUpClub` header columns** — a client-facing migration.
- Makes the persistence layer explicit: every `@ManyToOne` is now `LAZY` with fetch-join queries for what responses
  read, and a new **`TransactionService`** commits every competitor/match write in its own explicit transaction —
  the IPSC services no longer use `@Transactional`. Bulk imports stay all-or-nothing.
- Clears the improvement plan's documentation-accuracy gaps: #13–#17, plus #18–#24 from this release's own audit.
  Only Gap #6 (scoring/shooter-log layer) remains open.

## 📦 Key Changes

**Added**

- `Competitor.paidUpSapsa`/`paidUpClub` and matching request/response fields
- `TransactionService`/`TransactionServiceImpl`; fetch-join queries on `IpscMatchRepository`/`CompetitorRepository`
- `IpscMatch.stages` as a cascaded, orphan-removing collection
- `TransactionService`'s full 3-tier tests and six repository integration test classes

**Changed**

- `@ManyToOne` associations `EAGER` → `LAZY`; IPSC services commit through `TransactionService`
- Match create/update/patch responses list stages by stage number
- Release Checklist makes the Future Roadmap log and "Major Version Goals" mandatory per release

**Fixed**

- Documentation drift across `ARCHITECTURE.md`, `AGENTS.md`, `README.md`, `CONTRIBUTING.md`, `HISTORY.md` and
  `flyway-migration-versioning.md` (Gaps #13–#24)

**Removed**

- Unused `jackson-dataformat-xml` and `commons-lang3` dependencies

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (966 tests, 0 failures/errors), up from 903 at v8.8.0
- [x] `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing
- [x] Integration tests run without a surrounding transaction confirm real commits, rollbacks and all-or-nothing bulk
      imports against H2
- [x] Repository integration tests confirm the fetch-join queries load their associations and the stage cascade
      persists, orphan-removes and deletes
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.9.0.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's changes
- [x] `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` checked — a full sweep recorded Gaps
      #18–#24, all fixed in this release; the branch-diff sync attributed Gaps #13–#17 to v8.9.0

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-890---2026-09-26)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
