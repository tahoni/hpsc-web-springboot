# Release Notes – Version 14.0.0

**Release Date:** October 9, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Complete Failure Reporting for Bulk Imports, Club Resolution & Shorter Division Names**

Version 14.0.0 is a major release. A bulk match competitor import now reports every missing or unresolvable field of a
failed row at once, together with the values the row supplied. Clubs are resolved through one service by code,
abbreviation or name, and the divisions lose their trailing " Division". Several of these change what an existing
caller sends or receives, so each is flagged **Breaking** in `CHANGELOG.md`.

---

## ⭐ Key Highlights

### 🧾 Complete Failure Reporting

- `MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields` describes each required field still unset on a
  match competitor, saying whether the request left it out or gave a value that could not be resolved
- `MatchCompetitorMapper.populateResolvableFields` fills in whatever can be resolved without throwing, so
  `IpscMatchCompetitorServiceImpl.createMatchCompetitors` checks a row once and lists every problem, instead of failing
  on the first
- A failed row is reported with no `MatchCompetitorResponse` and its values in the new `MatchCompetitorRow`, built by
  the new `MatchCompetitorRowMapper` from what was already resolved onto the `MatchCompetitor`
- A blank competitor category, division, firearm type or power factor is reported as not specified, and a blank
  category is accepted as `CompetitorCategory.NONE`

### 🏛️ One Club Lookup

- New `IpscEntityClubService.findByCodeOrAbbreviation` and `findByCodeOrAbbreviationWithDefault` find a club by
  abbreviation, name, or club identifier code, abbreviation or name
- `CompetitorMapper.resolveHomeClub`, `MatchMapper.resolveClub` and `MatchCompetitorMapper.resolveMatchClub` resolve
  through it, so `FatalException` is dropped from the match service and controller methods that only declared it
- `ClubService` and `EntityIpscCompetitorService` are renamed `IpscEntityClubService` and
  `IpscEntityCompetitorService`, so the IPSC entity services share a naming pattern

### 🏷️ Shorter Division Names

- Each `Division` drops its trailing " Division" (for example `Open` instead of `Open Division`), and `PCC_OPTICS` and
  `PCC_IRON` become `PCC Optics` and `PCC Irons`
- `V11_7_0__drop_division_suffix_from_division_names.sql` renames the stored values in `match_competitor`,
  `shooter_log_competitor` and `shooter_log_overall`
- A missing firearm type is taken from the match competitor's division

### 🧹 Dead Code Removed

- `NumberUtil`, `DateUtil`, `SystemConstants.DEFAULT_SCALE`, `StringUtil.formatStringWithNamedParameters`,
  `ValueUtil.nullAsZeroBigDecimal` and `MatchCompetitorResponseHolder` are removed, as only tests or nothing called them

---

## 📦 What's New

### Added

- `MatchCompetitorHelpers`, `MatchCompetitorRowMapper` and `MatchCompetitorRow`, carried by
  `MatchCompetitorBulkResponse.matchCompetitorRow` for a failed bulk import row
- `IpscEntityClubService.findByCodeOrAbbreviation` and `findByCodeOrAbbreviationWithDefault`
- `MatchCompetitorMapper.populateResolvableFields`
- `V11_7_0__drop_division_suffix_from_division_names.sql`

### Changed

- **Breaking:** `Division` names drop the " Division" suffix, and `PCC_OPTICS` and `PCC_IRON` are renamed `PCC Optics`
  and `PCC Irons`, so a division given in the old form no longer resolves
- **Breaking:** `CompetitorMapper.resolveHomeClub` and `MatchMapper.resolveClub` throw `ValidationException` (400)
  instead of `NonFatalException` (404) for an unknown club
- **Breaking:** `MatchCompetitorBulkResponseHolder.matchCompetitors` is renamed back to `matchCompetitorResults`
- **Breaking:** a failed bulk import row has a `null` `MatchCompetitorBulkResponse.matchCompetitor` and its values in
  `matchCompetitorRow`
- `CompetitorCategory.fromName` returns an empty `Optional` for an unrecognised name, and `NONE` for a null or blank
  one; `MatchCompetitorMapper.resolveCompetitorCategory` follows
- `MatchCompetitorMapper.resolveFirearmType` takes the `Division` and falls back to its firearm type
- `MatchCompetitor` fields are `@NotNull`, `IpscMatchCompetitorServiceImpl.toResponse` validates the entity first, and
  the models use `jakarta.validation.constraints.NotNull` in place of jspecify's `NonNull`
- `MatchCompetitorRequest` no longer rejects a missing match ID, category, division or power factor on arrival, and the
  CSV `Cats` column is optional, so the import can report the row

### Removed

- `NumberUtil`, `DateUtil` and their tests, `SystemConstants.DEFAULT_SCALE`, two unused `StringUtil` and `ValueUtil`
  methods, and `MatchCompetitorResponseHolder`
- Controller tests that only checked delegation or exception propagation

---

## 🚀 Migration Guide

Flyway runs `V11_7_0__drop_division_suffix_from_division_names.sql` on startup, so stored divisions are renamed with no
action. Callers must change what they send and read:

- Send a division without " Division" (`Open`, not `Open Division`) and use `PCC Optics` and `PCC Irons`.
- Expect `400` rather than `404` for an unknown club, and read a bulk match competitor import's results from
  `matchCompetitorResults`.
- For a failed bulk row, read `matchCompetitorRow` instead of `matchCompetitor`, which is `null`.
- Code that calls `ClubService` or `EntityIpscCompetitorService` uses `IpscEntityClubService` and
  `IpscEntityCompetitorService`, and `findCompetitor` is now `findCompetitorByIdentifierAndFullName`.

---

## 📊 Statistics

- **Files Changed:** 82 against `main` before this release's documentation and version bump; 3,556 insertions and 2,774
  deletions
- **New Source Files:** 6 (5 classes and 1 migration)
- **Renamed Source Files:** 2
- **Deleted Source Files:** 5
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **Report everything at once.** A bulk import row is checked for all its problems and returned with the values it
  supplied, so a caller can fix the row in one pass.
- **One place to resolve a club.** Putting the lookup in `IpscEntityClubService` lets a home club, a match club and a
  match competitor's club accept the same inputs and fail the same way.
- **Short names.** A division is matched by the name people use, and the migration keeps stored rows readable.
- **A MAJOR.** The division renames, the club error status, the renamed response field and the `null` failed-row
  response are backward-incompatible, which `AGENTS.md`'s Semantic Versioning rules classify as MAJOR.

---

## 🧪 Testing

- `./mvnw test` passes: 1,263 tests with no failures, errors or skips.
- New unit tests cover `MatchCompetitorHelpers`, `MatchCompetitorRowMapper`, `IpscEntityClubService` (unit, mocked
  repository and `@SpringBootTest`), `populateResolvableFields`, the blank and null competitor category and the new
  division names.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports,
  is read but never used as the row's own ID, since the imports only create records.
- No service or controller operates on `ShooterLog`, `ShooterLogCompetitor` or `ShooterLogOverall` yet, and no
  calculation job fills them in (Gap #6, partially completed).
- `pom.xml` still overrides `tomcat.version` (to `11.0.26`) for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26). `logback.version`, the Jackson BOM properties and `flyway-mysql` are likewise set above
  Spring Boot's managed versions, and `mysql-connector-j` is pinned to `26.7.0` against the `9.7.0` it manages.
- Stored `Manual Action Contemporary Division` and `Manual Action Bolt Division` values are not migrated and read back
  as `null`.

---

## 🔮 Future Enhancements

- Build a `ShooterLogService` and controller over the existing repositories, committing through `TransactionService`,
  following the same phased pattern that closed Gap #1 (Gap #6).
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.26` or later (Gap #26), and the
  other dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge
- Claude Code (Claude Sonnet 5.5) — co-author of the release's commits

---

## 📝 Notes

Version 14.0.0 reports every problem of a failed bulk match competitor row with the values it supplied, resolves clubs
through one service, and shortens the division names. It improves the improvement plan to 38 closed gaps, with only #6
and #26 partially completed.

---
