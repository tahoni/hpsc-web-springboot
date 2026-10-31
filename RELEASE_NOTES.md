# Release Notes – Version 16.0.0

**Release Date:** October 31, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Rifle PCC Division, Clearer Semi-Auto Division Names & Cleaner Competitor Name Matching**

Version 16.0.0 is a major release. The Rifle PCC division is added, and the semi-auto rifle divisions are renamed so
their names say what they are. A division given by the old "Rifle Open" or "Rifle Standard" names no longer resolves,
which is flagged **Breaking** in `CHANGELOG.md`. Competitor names also match more reliably, and a bulk match competitor
import is saved or rolled back as one unit.

---

## ⭐ Key Highlights

### 🔫 Divisions

- New `Division.RIFLE_PCC`, shown as "Rifle PCC"
- `RIFLE_SEMI_AUTO_OPEN` and `RIFLE_SEMI_AUTO_STANDARD` are now "Rifle Semi Auto Open" and "Rifle Semi Auto Standard"
  (they were "Rifle Open" and "Rifle Standard")
- `V11_11_0__rename_semi_auto_rifle_division_names.sql` renames the stored `division` in `match_competitor`,
  `shooter_log_competitor` and `shooter_log_overall`; without it an existing row would read back as `null`

### 🧹 Competitor Name Matching

- `CompetitorHelpers.cleanCompetitorName` strips trailing modified-entry digits of any length, such as `Jane Doe 12`,
  with or without whitespace around them, and keeps digits that are not at the end of the name
- `IpscConstants.REPLACE_IN_NAMES_REGEX` also removes a "TRO" or "(TRO)" role marker, so `Jane Doe (TRO)` matches
  `Jane Doe`

### 🔄 Bulk Import Transaction

- `IpscMatchCompetitorServiceImpl.createMatchCompetitors` is `@Transactional`, so a bulk match competitor import is
  saved or rolled back as one unit

---

## 📦 What's New

### Added

- `Division.RIFLE_PCC`
- `V11_11_0__rename_semi_auto_rifle_division_names.sql`
- New `cleanCompetitorName` tests for trailing digits of one or more digits, alone, after a position prefix and
  alongside a range officer marker

### Changed

- **Breaking:** `Division.RIFLE_SEMI_AUTO_OPEN` and `RIFLE_SEMI_AUTO_STANDARD` display names are now "Rifle Semi Auto
  Open" and "Rifle Semi Auto Standard", so a division given by the old names no longer resolves
- `CompetitorHelpers` strips trailing modified-entry digits when it normalises a competitor name
- `IpscConstants.REPLACE_IN_NAMES_REGEX` also removes a "TRO" or "(TRO)" role marker
- `IpscMatchCompetitorServiceImpl.createMatchCompetitors` is `@Transactional`
- Spring Boot is bumped to 4.1.2, `asm.version` to 9.11.1 and `mysql-connector-j` to 26.10.0
- `documentation/roadmap/improvement-plan.md` and `improvement-plan-tasks.md` add Gap #41 and close Gap #26
- `documentation/recommendations/flyway-migration-versioning.md` gains a row for `V11_11_0`

### Removed

- The `tomcat.version` override in `pom.xml`, since Spring Boot 4.1.2 manages Tomcat `11.0.27`

---

## 🚀 Migration Guide

Flyway runs `V11_11_0` on startup, so the stored division names change with no action. Callers must change what they
send:

- Send "Rifle Semi Auto Open" and "Rifle Semi Auto Standard" instead of "Rifle Open" and "Rifle Standard" in match
  competitor request bodies and in the bulk match competitor CSV import. A division given by the old names no longer
  resolves.
- Expect the new names in responses.

---

## 📊 Statistics

- **Files Changed:** 15 against `main` before this release's documentation; 172 insertions and 16 deletions
- **New Source Files:** 1 (a migration)
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **Migrate the stored names with the enum.** A division is read back by its display name, so renaming one without
  rewriting the stored values would leave existing rows reading back as `null`. The migration ships with the rename.
- **Normalise names at the edge.** Trailing digits and role markers are removed where a name is cleaned, so every
  lookup by name benefits, rather than each caller handling them.
- **One unit of work for an import.** Running the bulk import in a single transaction means a failure part-way leaves
  no half-imported match.
- **A MAJOR.** A division given by the old names no longer resolves, which `AGENTS.md`'s Semantic Versioning rules
  classify as backward-incompatible.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` passes: 1,327 tests with no failures, errors or skips.
- New unit tests cover `cleanCompetitorName` for trailing digits and role markers, and the existing division
  assertions use the new names.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports,
  is read but never used as the row's own ID, since the imports only create records.
- No service or controller operates on `ShooterLog`, `ShooterLogCompetitor` or `ShooterLogOverall` yet (Gap #6,
  partially completed).
- `IpscConstants`' `MATCH_POINTS_SCALE`, `HIT_FACTOR_SCALE` and `TIME_SCALE`, `SystemConstants.DEFAULT_SCALE` and
  `NumberUtil.calculateSum` have no production caller (Gap #41).
- `pom.xml` still overrides `logback.version`, the Jackson BOM properties and `flyway-mysql`, set above Spring Boot's
  managed versions, and pins `asm.version`, since Spring Boot does not manage `asm`.

---

## 🔮 Future Enhancements

- Build a `ShooterLogService` and controller over the existing repositories, committing through `TransactionService`,
  following the same phased pattern that closed Gap #1 (Gap #6).
- Give the reinstated score-scale constants and `NumberUtil.calculateSum` a caller, or remove them (Gap #41).
- Drop the remaining dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge
- dependabot[bot]
- Claude Code (Claude Sonnet 5.5) — co-author of the release's commits

---

## 📝 Notes

Version 16.0.0 adds the Rifle PCC division, renames the semi-auto rifle divisions and migrates their stored values, and
makes competitor name matching ignore trailing modified-entry digits and a "TRO" role marker. It leaves the improvement
plan at 39 closed gaps, with #6 partially completed and #41 open.

---
