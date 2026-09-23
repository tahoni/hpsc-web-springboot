## 🎯 Summary

- Extends the competitor module's "apply the domain default" pattern to matches: a missing/blank match `club` now
  defaults to a new `ClubIdentifier.ALL` ("Eufees Clubs") constant, seeded by a new Flyway migration, instead of
  failing validation.
- Relaxes competitor `clubNumber` to be required only when the competitor's home club is HPSC, forcing it to `null`
  for every other home club.
- Tightens the JaCoCo `LINE`/`COVEREDRATIO` coverage floor from 86% to 97%, now genuinely near the freshly
  re-measured real baseline (98.44%/98.98% line/branch, 868 tests).
- Removes the unused `HpscConstants` class, consolidating its sole date-format constant onto `SystemConstants`/
  `IpscConstants`.
- Patches `tomcat-embed-core`/`-el`/`-websocket` to `11.0.25`, closing three critical CVEs.
- Adds `AGENTS.md` conventions for member ordering and REST naming, a release-checklist backstop that verifies
  `ARCHITECTURE.md`'s Project Structure tree against disk, and restructures the improvement-plan docs into
  ✅ Completed/🟡 Partially Completed/⚪ Open sections.
- Closes `documentation/roadmap/improvement-plan.md`'s Gap #4 and Gap #9.

## 📦 Key Changes

**Added**

- `V7_3_0__seed_club_data.sql` — seeds the `club` table with every named `ClubIdentifier` constant, including the
  new `ALL`
- `ClubIdentifier.ALL` — represents a match hosted jointly by `SOSC`/`HPSC`/`PMPSC`

**Changed**

- `IpscMatchController`/`IpscMatchServiceImpl` — match `club` now defaults to `DEFAULT_MATCH_CLUB_IDENTIFIER`
  instead of failing validation; `resolveClub()` throws `FatalException`/`NonFatalException` for the defensive
  null-default and missing-club-in-database cases
- `IpscCompetitorController`/`IpscCompetitorServiceImpl` — `clubNumber` required only for HPSC home clubs;
  `Competitor.clubNumber` column relaxed to nullable via new `V7_4_0__make_club_number_nullable.sql`
- `pom.xml` — JaCoCo coverage floor `0.86` → `0.97`
- `SystemConstants`/`IpscConstants` — new date-format constants, field Javadoc added throughout
- `AGENTS.md`/`CONTRIBUTING.md` — new Member ordering and REST naming conventions; new Release Checklist step
  verifying the Project Structure tree against disk
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` — restructured into ✅ Completed/
  🟡 Partially Completed/⚪ Open sections; Gap #4 and Gap #9 closed, attributed to v8.4.0
- `HISTORY.md` — new Historical Timeline entry, Phase 25 and Milestone 25

**Fixed**

- `ARCHITECTURE.md` — brought fully back in sync with disk (missing directories, `Gender`/`GenderConverter`,
  `HpscConstants` removal, bidirectional-relationship inaccuracies, stale coverage figure)
- `README.md` — Installation/Execution steps corrected to the actual env-var-driven `dev` profile flow

**Removed**

- `HpscConstants` — its sole constant was an alias for `SystemConstants.ISO_DATE_FORMAT`

**Security**

- `tomcat-embed-core`/`-el`/`-websocket` `11.0.24` → `11.0.25`, closing three critical CVEs

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` — full suite passing (868 tests, 0 failures/errors); line coverage 98.44%, branch
      coverage 98.98% — comfortably above the new 97% floor (verified during release prep)
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/RELEASE_NOTES_v8.4.0.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's own
      changes
- [ ] Re-run `./mvnw test` immediately before merge, to confirm nothing regressed from the final release-prep
      documentation edits (no production code changed after the coverage run above)

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)
