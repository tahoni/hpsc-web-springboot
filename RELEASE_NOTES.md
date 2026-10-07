# Release Notes – Version 13.2.0

**Release Date:** October 7, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Club Number Matching & Backtracking-Free Name Patterns**

Version 13.2.0 is a minor release. A competitor is now looked up by club number first, before the number is
converted to an integer. The regular expressions that clean a competitor's name no longer backtrack. A misleading
error message in the award service is also corrected. No database migration runs and no existing request is rejected
that was accepted before.

---

## ⭐ Key Highlights

### 🔎 Club Number First

- `EntityIpscCompetitorServiceImpl.findCompetitor` matches the trimmed competitor number as a club number with
  `CompetitorRepository.findByClubNumber` before anything else and returns that competitor at once
- The competitor number, ID number and name lookups follow only when no club number matches, so a numeric club number
  now wins over a competitor number with the same value

### 🧼 Patterns That Cannot Backtrack

- `CompetitorHelpers.POSITION_PREFIX` is possessive and anchored at the start of the name, `WHITESPACE` matches only a
  possessive run of two or more whitespace characters, and `MC_PREFIX` is a fixed pattern tested with `lookingAt()`
- A leading position is now any number of digits followed by an optional whitespace character and a hyphen, so
  `123-John` cleans to `John` and `-12-John Smith` is left alone

### 🐛 A Truthful Error

- `AwardServiceImpl.mapAwards` says "Award request list cannot be null." where it used to say "Image request list", a
  slip copied from the image service

---

## 📦 What's New

### Added

- `findCompetitor` matches the competitor number as a club number first, through `CompetitorRepository.findByClubNumber`

### Changed

- `CompetitorHelpers.cleanCompetitorName` uses possessive `POSITION_PREFIX` and `WHITESPACE` patterns: a leading
  position is any number of digits at the start of the name, and a single tab or newline between words is no longer
  turned into a space
- `CompetitorHelpers.MC_PREFIX` is tested only at the start of the word with `lookingAt()`; its behaviour is unchanged
- `AwardServiceImpl.mapAwards` drops a `@NonNull` on its parameter that its own null check contradicted
- The archived versions 1.0.0 – 7.4.1 move from `documentation/history/` to `documentation/archive/v1-v7/`, and
  `ARCHIVE.md` moves to `documentation/legacy/`; the documentation links follow

### Fixed

- `AwardServiceImpl.mapAwards` logs and throws "Award request list cannot be null." for a null list
- `ImageResponse.setMimeType` resets a blank MIME type that cannot be inferred from the file name to an empty string

---

## 🚀 Migration Guide

No migration is needed. Two behaviours differ for an unusual input: a competitor number that is both one competitor's
club number and another's competitor number now finds the club number's competitor, and a name with a hyphen after a
long run of digits, such as `123-John`, now has the digits removed as a position.

---

## 📊 Statistics

- **Files Changed:** 53 against `main`, including this release's notes and PR description; 868
  insertions and 471 deletions
- **New Source Files:** 0
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **A club number is an identifier.** It is unique, so matching it first, as written, is cheaper and more certain than
  narrowing by a competitor number or a name.
- **Possessive over clever.** Each name pattern either consumes its whole run in one step or has nothing left to give
  back, so no input can make the engine retry.
- **A MINOR.** The new first lookup stage is new functionality, and nothing removed or renamed breaks an existing
  caller, which `AGENTS.md`'s Semantic Versioning rules classify as MINOR.

---

## 🧪 Testing

- `./mvnw test` passes: 1,280 tests with no failures, errors or skips.
- New unit tests cover the club number match, a blank number skipping the club query, the fall back to the competitor
  number, the cleaned position and whitespace, and the `mapAwards` message.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports,
  is read but never used as the row's own ID, since the imports only create records.
- No service or controller operates on `ShooterLog`, `ShooterLogCompetitor` or `ShooterLogOverall` yet, and no
  calculation job fills them in (Gap #6, partially completed).
- `pom.xml` still overrides `tomcat.version` (to `11.0.26`) for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26). `logback.version`, the Jackson BOM properties and `flyway-mysql` are likewise set above
  Spring Boot's managed versions, and `mysql-connector-j` is pinned to `26.7.0` against the `9.7.0` it manages.
- Seven utility methods and `SystemConstants.DEFAULT_SCALE` are still used only by tests (Gap #38, partially completed),
  and so are `ClubIdentifier.code` and `fromCode` (Gap #39).
- Stored `Manual Action Contemporary Division` and `Manual Action Bolt Division` values are not migrated and read back
  as `null`.

---

## 🔮 Future Enhancements

- Build a `ShooterLogService` and controller over the existing repositories, committing through `TransactionService`,
  following the same phased pattern that closed Gap #1 (Gap #6).
- Remove or give a caller to the remaining test-only utility methods, `SystemConstants.DEFAULT_SCALE` and
  `ClubIdentifier.code` (Gaps #38 and #39).
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.26` or later (Gap #26), and the
  other dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge
- Claude Code (Claude Sonnet 5.5) — co-author of most of the release's commits

---

## 📝 Notes

Version 13.2.0 looks a competitor up by club number first and stops the competitor name patterns backtracking. No
database migration runs on upgrade and no existing caller breaks.

---
