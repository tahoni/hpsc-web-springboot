# Release Notes – Version 16.1.2

**Release Date:** November 9, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Shooter Log Dates Enforced After the Log Is Written**

Version 16.1.2 is a patch release. A shooter log's start and end dates were enforced only while the log itself was
written, so a match rescheduled afterwards was still counted in the log's overall results. The overall calculation now
refuses a log with a match outside its dates, rescheduling a match outside the dates of a log that covers it is refused,
and a match updated after the overall results were calculated marks them out of date. No endpoint, request or response
changes shape, so the release is backward-compatible.

---

## ⭐ Key Highlights

### 📆 Dates Checked When the Calculation Runs

- `POST /ipsc/shooter-log-overall-results/logs/{shooterLogId}` throws a `ValidationException`, a `400`, if a match
  the shooter log covers is scheduled outside the log's start and end dates, both inclusive, and calculates and changes
  nothing
- A match updated after the overall results were calculated now marks them out of date, because moving a match within
  the log can change which of a competitor's matches is the most recent

### 🔒 Dates Checked When a Match Moves

- `PUT` and `PATCH /ipsc/matches/{matchId}` throw a `ValidationException`, a `400`, if the new date puts the match
  outside the dates of a shooter log that covers it, and the message names the log
- The check only runs when the match moves to another day, so a log already out of step does not block unrelated edits
  to its match

### ♻️ One Shared Check

- `ShooterLogHelpers.validateMatchesWithinDates` and `isWithinDates` hold the date rule once, used when a log is
  written, when its overall results are calculated and when a match is rescheduled, so the three cannot drift
- The check for a match with no scheduled date is removed, since `ipsc_match.scheduled_date` is `NOT NULL`

### 📖 Documented Rules

- `README.md` and `ARCHITECTURE.md` state that a calculation is refused for a match outside the log's dates, and that
  rescheduling a match outside the dates of a log that covers it is refused
- The Javadoc on `IpscShooterLogOverallService` and `IpscMatchService`, and the OpenAPI descriptions on both
  controllers, describe the rule, with the `400` responses added to the calculate, `PUT` and `PATCH` endpoints

---

## 📦 What's New

### Added

- `ShooterLogRepository.findAllByMatchesId`, which finds the shooter logs that cover a match
- `ShooterLogHelpers` and `ShooterLogHelpersTest`
- Unit and integration tests for the overall calculation's date check, the match reschedule check and the out-of-date
  rule for an updated match

### Changed

- `IpscShooterLogServiceImpl.validateMatchesWithinDates` no longer checks for a match with no scheduled date, and the
  three tests that only covered it are removed; the check now lives in `ShooterLogHelpers`
- `README.md`, `ARCHITECTURE.md`, the `IpscShooterLogOverallService` and `IpscMatchService` Javadoc and the controllers'
  OpenAPI descriptions state the date rule and its `400` responses
- `documentation/roadmap/improvement-plan.md` and `improvement-plan-tasks.md` add and close Gap #45

### Fixed

- `IpscShooterLogOverallServiceImpl.calculateShooterLogOverall` refuses a log with a match outside its dates, and a
  match updated after the overall results were calculated marks them out of date
- `IpscMatchServiceImpl.updateMatch` and `patchMatch` refuse moving a match outside the dates of a shooter log that
  covers it

### Removed

Nothing is removed.

---

## 🚀 Migration Guide

No action is needed for callers: no existing endpoint, request or response changes shape, and there is no schema
change. The only difference is that two requests that used to succeed are now refused with a `400`: calculating the
overall results of a shooter log that covers a match outside its dates, and moving a match to a day outside the dates of
a shooter log that covers it.

A shooter log that already has a match outside its dates cannot be calculated until it is put right, either by
changing the log's dates through `PUT` or `PATCH /ipsc/shooter-logs/{shooterLogId}` or by moving the match back. A
match in that state can still be edited, as long as it stays on the same day.

---

## 📊 Statistics

- **Files Changed:** 19 against `main` before this release's documentation; 757 insertions and 85 deletions
- **New Source Files:** 1 (`ShooterLogHelpers`), plus its test class
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **The rule is checked where it can be broken.** A log's dates define which matches belong to it, so they are checked
  when the log is written, when a match moves and when the results are calculated, rather than trusting that the
  earlier check still holds.
- **Rescheduling is refused, not repaired.** A match is never silently removed from a log, or the log's dates widened,
  to make a new date fit. The caller gets a `400` naming the log and decides.
- **Only a move to another day is checked.** A match that stays on its day skips the check, so a log already left out of
  step does not block unrelated edits to its match, and it is still caught when the log is calculated.
- **A moved match makes the results stale.** Moving a match within the log can change which of a competitor's matches
  is the most recent, so a match updated after the overall results were calculated marks them out of date.
- **One helper.** `ShooterLogHelpers` is shared by the log, the calculation and the reschedule check, so the inclusive
  start and end comparison is written once.
- **A PATCH.** No contract changes and the one new repository method is internal, which `AGENTS.md`'s Semantic
  Versioning rules classify as a backward-compatible fix.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` passes: 1,476 tests with no failures, errors or skips.
- New tests cover `ShooterLogHelpers`, the overall calculation refusing a match outside the log's dates (inclusive at
  both ends) and treating an updated match as making the results out of date, and `updateMatch` and `patchMatch`
  refusing a move outside a covering log's dates while allowing one that stays on the same day, in Mockito unit tests
  and `@SpringBootTest` integration tests. The three tests that only covered the removed missing-date check are removed.
- Overall coverage is 99.09% branch and 98.81% line, above the 97% minimum.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports,
  is read but never used as the row's own ID, since the imports only create records.
- A shooter log already out of step with a match's date is only caught when its overall results are calculated, or when
  the match moves to another day; it is not reported when either date is left alone.
- Shooter logs can only be created one at a time; there is no bulk import for them.
- The match competitor import does not say that an entry's competitor is unverified, so an entry that never appears in
  results gives no sign of why.
- Competitors can only be verified one at a time, and the bulk competitor import only creates competitors, so it
  cannot verify one that already exists.
- `pom.xml` still overrides `logback.version`, the Jackson BOM properties, `mysql-connector-j` and `flyway-mysql`, set
  above Spring Boot's managed versions.

---

## 🔮 Future Enhancements

- Add a bulk import for shooter logs if creating them one by one proves slow.
- Report a match whose date is outside a covering log's dates when it is read, not only when the log is calculated.
- Report, or skip, a match competitor import row whose competitor is not verified, and consider a way to verify
  competitors in bulk.
- Drop the remaining dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge
- Claude Code (Claude Sonnet 5.5) — co-author of the release's commits

---

## 📝 Notes

Version 16.1.2 keeps a shooter log's dates true after the log is written: a calculation refuses a match outside them and
a match can no longer be rescheduled outside them. It leaves the improvement plan with all 45 gaps closed and none open.

---
