# Release Notes – Version 12.0.0

**Release Date:** October 5, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Match Competitor Lookup, Partial Bulk Import & Club Filtering**

Version 12.0.0 is a major release. The competitor lookup built in 11.0.0 is wired into the match competitor service,
the match competitor bulk import becomes a partial import limited to one club, and a match no longer needs a category.
Four groups of these changes break existing callers — see the Migration Guide.

---

## ⭐ Key Highlights

### 🔎 Match Competitors Find Their Competitor

- A match competitor request names its competitor by ID, or by competitor number and name, and
  `EntityIpscCompetitorService.findCompetitor(competitorNumber, fullName)` finds it: the competitor number first, then
  the ID number, then the full name, narrowing several number matches by name
- The number is a string that may also be an ID number, and a lookup that does not find exactly one competitor throws —
  a `404` when none is found, a `400` when several are
- The range officer marker `RO` or `(RO)` is removed from a name wherever it appears, and a competitor saved without a
  nickname takes its first name as the nickname

### 📥 A Partial Bulk Import

- Every row of a match competitor bulk import is saved on its own: a row that fails is reported and skipped while the
  rest are created, and the response carries one result per CSV row
- An import in which every row fails answers `422` with the same per-row results; unreadable CSV or a missing header
  column still answers `400`
- The response models are `MatchCompetitorBulkResponse` and `MatchCompetitorBulkResponseHolder`, and each result names
  its competitor by `competitorName` and `competitorNumber`

### 🏠 Club-Filtered Imports

- The bulk import creates only the rows for one club — HPSC's own club unless the optional `club` query parameter
  names another — whose match club, or whose competitor's home club, is that club; every other row is reported as
  skipped
- A new `ClubService` holds the null-safe club comparisons, so the services no longer repeat them

### 🏷️ Optional Match Category

- A match no longer needs a match category: one that is omitted takes the default, Club Shoot, and a supplied one may
  be a display name or a constant name, ignoring case and surrounding whitespace

---

## 📦 What's New

### Added

- `ClubService` and `ClubServiceImpl`, with `isSameClub(Club, ClubIdentifier)` and
  `isSameClub(ClubIdentifier, ClubIdentifier)`
- `IpscMatchCompetitorService.createMatchCompetitors(String, String)`, taking the optional club, and an optional `club`
  query parameter on `POST /ipsc/match-competitors/bulk`
- `IpscCompetitorServiceImpl.isMemberOfHomeClub(Club, ClubIdentifier)`, defaulting to HPSC through
  `isMemberOfHomeClub(Club)`
- `CompetitorHelpers.getCompetitorNumberAsInteger` and `CompetitorRepository.findAllByIdNumber`
- `StringUtil.hasText`, replacing the private copy in `IpscMatchCompetitorServiceImpl`
- `qodana.yaml`, with the `qodana.starter` profile and the `JavadocReference` inspection

### Changed

- **Breaking:** the shared alias numbers 15000 and 16000 are no longer matched by competitor number, and a number that
  is not a whole number is looked up as an ID number, answering `404` when nothing matches instead of a `400`
- **Breaking:** `createMatchCompetitors` is a partial import, so a row that is missing a required field, has an
  unrecognised value, names an unknown competitor or match, or duplicates another entry is reported and skipped instead
  of failing the whole import
- **Breaking:** `MatchCompetitorResult` and `MatchCompetitorResultHolder` are renamed `MatchCompetitorBulkResponse` and
  `MatchCompetitorBulkResponseHolder`, and the response body carries `matchCompetitors` instead of
  `matchCompetitorResults`
- **Breaking:** the bulk import is limited to one club, HPSC's unless another is asked for, and skips the rest
- `EntityIpscCompetitorService.findCompetitor` takes `(competitorNumber, fullName)` and throws instead of returning an
  empty result when it does not find exactly one competitor
- `IpscMatchServiceImpl` no longer requires a match category, and `resolveMatchCategory` accepts a constant name and
  surrounding whitespace
- `MatchCompetitorResponse` gains `competitorName` and `competitorNumber`, and `MatchCompetitorBulkResponse` defaults
  to a successful result with an empty message
- The `DateUtils`, `NumberUtils`, `StringUtils` and `ValueUtils` classes are renamed back to `DateUtil`, `NumberUtil`,
  `StringUtil` and `ValueUtil`, and the enums, `ControllerResponse`, `ImageResponse` and the service implementations
  use `StringUtil.hasText`
- `IpscConstants` is documented, and `mysql-connector-j` is pinned to `26.7.0` in `pom.xml`

### Fixed

- `ControllerResponse` derives `success` from its error the right way round, so a response with an error is no longer
  marked successful
- `findCompetitor` no longer throws a `NumberFormatException` for a numeric value too long for an `int`, such as a
  13-digit ID number

### Removed

- `IpscConstants.MAX_SAPSA_NUMBER`, `MATCH_POINTS_SCALE`, `HIT_FACTOR_SCALE`, `TIME_SCALE` and `PERCENTAGE_SCALE`, which
  nothing referenced

---

## 🚀 Migration Guide

- **Match competitor bulk importers.** Read the response's `matchCompetitors` instead of `matchCompetitorResults`, and
  check each result's `success` and `message`: a row that fails no longer fails the request. An import in which every
  row fails answers `422` rather than `400` or `404`.
- **Club.** The bulk import now creates only HPSC's rows. To import another club's rows pass `club` (its name or
  abbreviation); a row for a club other than the one imported, or for a competitor with no home club and no matching
  `matchClub`, is reported as skipped.
- **Competitor numbers.** A request that identified a competitor by 15000 or 16000 now resolves only if the name
  matches. A number that is not a whole number is looked up as an ID number, so an unmatched one answers `404`.
- **No database changes.** This release adds no Flyway migration.

---

## 📊 Statistics

- **Files Changed:** 89 on the release branch against `develop` (counted with the release documentation included);
  3,787 insertions and 1,013 deletions
- **New Source Files:** 2 (`ClubService` and `ClubServiceImpl`), plus `qodana.yaml`, two test classes
  (`ClubServiceTest` and `ClubServiceIntegrationTest`) and the archived release notes and PR description
- **Renamed Source Files:** 10 — the four `*Utils` classes and their four test classes, and the two bulk response
  models
- **Deleted Source Files:** 0
- **New Dependencies:** 0 (`mysql-connector-j` is now pinned to `26.7.0`)

---

## 🧭 Design Notes

- **A row is its own unit of work.** Each row is validated and saved in its own transaction, so a bad row costs
  one row, not the file, and the response says which.
- **Skipped is not failed.** A row for another club is skipped before it is validated, while a row for the club that is
  invalid is reported as failed, so the response tells a filtered row from a broken one.
- **One place for club comparisons.** `ClubService` owns the null-safe checks that the competitor and match competitor
  services used to repeat.
- **Defaults instead of requirements.** The club and the match category both default when omitted, so a match needs
  only a name, a date and a firearm type.
- **A MAJOR.** The alias numbers, the partial import, the renamed response field and the HPSC-limited import break
  existing callers, which `AGENTS.md`'s Semantic Versioning rules classify as MAJOR.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` passes: 1,227 tests with no failures, errors or skips, and the coverage gate is met —
  99.0% of branches (396 of 400), 98.1% of lines and 98.2% of instructions.
- New unit, implementation and integration tests cover the lookup wiring, the partial import, the club filter, the home
  club paths, `ClubService`, the match category defaults and the range officer marker in every position.
- No Flyway migration changed, so nothing was run against MySQL; the tests use the H2 `test` profile.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports
  is read but never used as the row's own ID, since the imports only create records.
- No service or controller operates on `ShooterLog`, `ShooterLogCompetitor` or `ShooterLogOverall` yet, and no
  calculation job fills them in (Gap #6, partially completed).
- `pom.xml` still overrides `tomcat.version` (to `11.0.26`) for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26). `logback.version`, the Jackson BOM properties and `flyway-mysql` are likewise set above
  Spring Boot's managed versions, and `mysql-connector-j` is pinned to `26.7.0` against the `9.7.0` it manages.
- The `club` parameter is matched against a row's `matchClub` and its competitor's home club only, so a competitor
  with no home club and no `matchClub` is skipped whichever club is asked for.

---

## 🔮 Future Enhancements

- Build a `ShooterLogService` and controller over the existing repositories, committing through `TransactionService`,
  following the same phased pattern that closed Gap #1 (Gap #6).
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.26` or later (Gap #26), and the
  other dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge
- `dependabot[bot]` — dependency update commits merged into the release
- Claude Code (Claude Sonnet 5.5) — co-author of most of the release's commits

---

## 📝 Notes

Version 12.0.0 puts the competitor lookup to work and makes the match competitor bulk import tolerant and club-aware —
a partial import, limited to HPSC's rows unless another club is asked for — as a breaking change for existing callers;
see the Migration Guide for what to change. No database migration runs on upgrade.

---
