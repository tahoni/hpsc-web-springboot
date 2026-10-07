## 🎯 Summary

- Domain feature release: brings the match domain's bulk CSV import to parity with the competitor domain's,
  established in v8.1.0.
- `IpscMatchController.createMatches` (`POST /ipsc/matches/bulk`, consumes `text/csv`) persists matches, together
  with their stages, from CSV data, via the existing `createMatch` validation/club/firearm-type/category-resolution
  logic — no new cross-entity orchestration.
- A match's stages are represented in CSV as a single semicolon-separated `<stageNumber>-<stageName>` cell, not a
  nested list — an earlier `numberOfStages` count-only design was tried and dropped in favour of this before either
  reached `develop`.
- Closes `documentation/roadmap/improvement-plan.md`'s Gap #8, which `ARCHITECTURE.md` had documented since v8.1.0
  as the one asymmetry left between the two IPSC domains' bulk-import support.

## 📦 Key Changes

**Added**

- `IpscMatchController.createMatches` (`POST /ipsc/matches/bulk`, consumes `text/csv`)
- `IpscMatchService`/`IpscMatchServiceImpl.createMatches`, with new `readMatches`/`toRequest`/`parseStages` helpers
- `MatchRequestForCSV`, `MatchResponseHolder` models (`models/ipsc/match/`)
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` — Gap #8 added and closed in the same
  release

**Changed**

- `ARCHITECTURE.md`, `README.md` — reverse-synced to describe match bulk CSV import; stale "removed pending a
  rebuild" language for the match domain removed

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (836 tests, 0 failures/errors)
- [x] New unit tests across `IpscMatchController`/`Service`/`ServiceImpl`'s bulk import and `MatchRequestForCSV`'s
      JSON/CSV (de)serialisation and required-field enforcement
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/RELEASE_NOTES_v8.3.0.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md`

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)
