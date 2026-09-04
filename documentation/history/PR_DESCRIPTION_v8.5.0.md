## 🎯 Summary

- Adds `startTime`/`endTime` (`LocalDateTime`) tracking to IPSC matches, alongside the existing `scheduledDate` —
  records when a match actually started and ended, not just when it was scheduled to be shot.
- Wired end-to-end: `IpscMatch` domain entity (new `V7_5_0` Flyway migration), `MatchRequest`/`MatchRequestForCSV`/
  `MatchResponse` DTOs, and `IpscMatchServiceImpl`'s `applyFields`/`patchMatch`/`toRequest`/`toResponse`.
- New test coverage proves the two columns round-trip through the real H2/Hibernate/JPA layer
  (`IpscMatchServiceIntegrationTest`), not just mocked repositories, and that CSV bulk import maps them correctly.
- Includes a small, unrelated British English documentation fix (`README.md`'s "License" → "Licence",
  `CONTRIBUTING.md`'s self-violating Serial Commas example, and `AGENTS.md`'s `LICENSE.md` exception narrowed to
  just the filename/content).

## 📦 Key Changes

**Added**

- `IpscMatch.startTime`, `IpscMatch.endTime` — new nullable `LocalDateTime` columns
- `V7_5_0__add_ipsc_match_start_end_time.sql` — new Flyway migration
- `MatchRequest`, `MatchRequestForCSV`, `MatchResponse` — new nullable `startTime`/`endTime` fields
- `IpscMatchController`'s CSV bulk-import OpenAPI example now includes `StartTime`/`EndTime`
- Test coverage: `IpscMatchServiceIntegrationTest` (real DB round-trip across create/get/patch/update),
  `IpscMatchServiceTest` (CSV bulk-import mapping)

**Changed**

- CSV bulk import (`POST /matches/csv`) now requires `StartTime`/`EndTime` header columns, like every other
  `MatchRequestForCSV` property — existing CSV templates need updating (values may be left blank)
- `IpscMatchServiceImpl`'s field-mapping methods now carry `startTime`/`endTime` through
- `AGENTS.md`'s British English exception for `LICENSE.md` narrowed to just the filename/content — every other
  reference to it now spells it "Licence"

**Fixed**

- `CONTRIBUTING.md`'s Serial Commas rule example corrected to no longer violate the rule it illustrates

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (870 tests, 0 failures/errors), up from 868 at v8.4.0
- [x] `./mvnw verify -Pcoverage` — 98.65% line / 98.99% branch coverage, up from 98.44%/98.98% at v8.4.0
- [x] `IpscMatchServiceIntegrationTest` confirms `startTime`/`endTime` persist and read back through the real H2
      database, not just mocked repositories
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/RELEASE_NOTES_v8.5.0.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's own
      changes
- [x] Verified `ARCHITECTURE.md`'s Project Structure tree against disk — no drift (migration directory is
      referenced generically, not per-file)
- [x] `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` checked — no new, closed or
      progressed gaps from this branch's diff

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)
