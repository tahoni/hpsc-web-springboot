## 🎯 Summary

- Adds a `url` field to `IpscMatch` — a link to more information about a match (e.g. a results page or event
  listing), wired end-to-end through the JSON/CSV APIs and service layer.
- Corrects `IpscMatch.startTime`/`endTime` from `LocalDateTime` to `LocalTime`, dropping the redundant date
  component those fields never needed — a mistake introduced one release ago in v8.5.0.
- Formally documents the 3-tier service test architecture (`<Service>Test`/`<Service>ImplTest`/
  `<Service>IntegrationTest`) in `AGENTS.md`, already followed by all four services but previously only described
  piecemeal across the scaffolding skills.

## 📦 Key Changes

**Added**

- `IpscMatch.url` — new nullable `String` column (`V7_6_0__add_ipsc_match_url.sql`)
- `MatchRequest`, `MatchRequestForCSV`, `MatchResponse` — new nullable `url` field
- `IpscMatchController`'s CSV bulk-import OpenAPI example now includes a `Url` column

**Changed**

- `IpscMatch.startTime`/`endTime` — `LocalDateTime` → `LocalTime`, via new
  `V7_7_0__change_ipsc_match_start_end_time_to_time.sql`
- New `IpscConstants.IPSC_INPUT_TIME_FORMAT` (`HH:mm`) constant; JSON/CSV `startTime`/`endTime` now bare `HH:mm`
  instead of `yyyy-MM-dd HH:mm` — **breaking format change** for existing API clients/CSV templates
- CSV bulk import (`POST /matches/csv`) header row must now include `Url`, like every other `MatchRequestForCSV`
  property
- `IpscMatchServiceImpl`'s `applyFields`/`patchMatch`/`toRequest`/`toResponse` now carry `url` through
- `AGENTS.md`'s Test Conventions section formally documents the 3-tier service test architecture

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (872 tests, 0 failures/errors), up from 870 at v8.5.0
- [x] `./mvnw verify -Pcoverage` — 98.66% line / 99.00% branch coverage, up from 98.65%/98.99% at v8.5.0
- [x] `IpscMatchServiceIntegrationTest` confirms `url` and the corrected `LocalTime` fields persist and read back
      through the real H2 database, not just mocked repositories
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/RELEASE_NOTES_v8.6.0.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's changes
- [x] `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` checked — no new, closed or
      progressed gaps from this branch's diff (both a full sweep and a branch-diff sync confirmed this)

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-860---2026-09-23)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
