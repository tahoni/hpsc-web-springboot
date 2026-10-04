# Release Notes – Version 11.0.0

**Release Date:** October 4, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Competitor Contract Tightening & Competitor Lookup**

Version 11.0.0 is a major release. The competitor's contract is tightened: every bulk CSV endpoint now consumes
`text/plain`, the competitor's nickname becomes `nickName`, and the competitor number becomes a whole number. A new
`EntityIpscCompetitorService` resolves a single competitor from a full name and a competitor number. Three groups of
these changes break existing callers — see the Migration Guide.

---

## ⭐ Key Highlights

### 📨 Bulk Endpoints Consume `text/plain`

- Every `POST /bulk` endpoint — awards, images, competitors, matches and match competitors — now consumes `text/plain`
  instead of `text/csv`, and the OpenAPI request-body content type matches
- The CSV body is unchanged; a request sent as `text/csv` is refused with a 415 error

### 🏷️ `nickName`

- `Competitor.nickname` is renamed `nickName`, and its `competitor.nickname` column `nick_name`
- The JSON property is `nickName` and the CSV column `NickName`

### 🔢 A Whole-Number Competitor Number

- `Competitor.competitorNumber` is an `Integer`, stored in an `INT` column, and `CompetitorResponse` returns it as a
  whole number
- Competitor and match competitor requests (JSON, and CSV `CompetitorNumber` and `Mem #`) still send a string, but it
  must be a whole number; anything else is refused, a blank value counts as not supplied, and a blank value in a patch
  leaves the stored number unchanged

### 🔎 Competitor Lookup

- `EntityIpscCompetitorService.findCompetitor(fullName, competitorNumber)` resolves one `Competitor` — by competitor
  number when exactly one matches, otherwise by full name, "FirstName LastName" or "NickName LastName", narrowed to the
  number's matches when there are any
- The numbers `15000` and `16000` are skipped, the name is trimmed and its "RO" suffix removed, and an empty result
  means no single competitor could be determined

---

## 📦 What's New

### Added

#### Services

- **`EntityIpscCompetitorService`, `EntityIpscCompetitorServiceImpl`:** The competitor lookup above, registered as a
  Spring `@Service`

#### Models

- **`MatchCompetitorResult`, `MatchCompetitorResultHolder`:** Response models for a bulk match competitor import — a
  row's success, a message and the `MatchCompetitorResponse`

#### Database

- **`V11_0_0`, `V11_1_0`:** Two Flyway migrations — the `nickname` rename and the `INT` competitor number; see
  `CHANGELOG.md` for each

### Changed

#### API

- **`AwardController`, `ImageController`, `IpscCompetitorController`, `IpscMatchController`,
  `IpscMatchCompetitorController`:** **Breaking:** the `POST /bulk` endpoints consume `text/plain`
- **Competitor and match competitor requests and CSV mix-ins:** **Breaking:** `competitorNumber` must be a whole number
- **`CompetitorResponse`:** **Breaking:** `competitorNumber` is a whole number
- **Competitor requests, patch requests and response, and the CSV mix-in:** **Breaking:** `nickname` is `nickName` and
  the CSV column `Nickname` is `NickName`

#### Domain

- **`Competitor`:** **Breaking:** `nickname` is `nickName` (column `nick_name`), and `competitorNumber` is an `Integer`
  (column `INT`)

#### Constants

- **`IpscConstants.MAX_SAPSA_NUMBER`:** Raised from `99_999` to `999_999`
- **`IpscConstants.EXCLUDE_ICS_ALIAS`:** A list of integers, to match the numeric competitor number

#### Repositories

- **`CompetitorRepository`:** Finders renamed `findAllByCompetitorNumber` and `findAllByFullNameIgnoreCase`, plus
  `findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase`

#### Services

- **`IpscCompetitorServiceImpl`:** The competitor number falls back to the SAPSA number, and is read as a whole number
  by `parseCompetitorNumber`
- **`IpscMatchCompetitorServiceImpl`:** The competitor number is read as a whole number before the competitor is
  looked up

#### Build & Metadata

- Project version bumped to **11.0.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

#### Documentation

- **Class-level `@since` tags:** Added to the classes that had none, and Javadoc for the new lookup and the competitor
  number resolution

### Fixed

#### Services

- **`EntityIpscCompetitorServiceImpl.findCompetitor`:** The "RO" suffix is now stripped from a full name that has
  trailing whitespace

---

## 🚀 Migration Guide

- **Bulk upload clients.** Send `Content-Type: text/plain` to every `POST /bulk` endpoint; the CSV body is unchanged.
- **JSON callers.** Send and read `nickName` instead of `nickname`, and read `competitorNumber` from a competitor
  response as a number rather than a string.
- **CSV import files.** Rename the competitor `Nickname` column to `NickName`; a file that keeps `Nickname` imports
  without nicknames, because unknown columns are ignored. Competitor numbers in `CompetitorNumber` and `Mem #` must be
  whole numbers.
- **Competitor numbers.** A value that is not a whole number, such as `C-1`, is refused with a validation error.
- **Database.** Two Flyway migrations run at startup: `V11_0_0` renames `competitor.nickname` to `nick_name`, and
  `V11_1_0` changes `competitor.competitor_number` from `VARCHAR(255)` to `INT`. `V11_1_0` is refused, and leaves the
  column as it was, if any existing value is not a whole number.
- **Before upgrading.** Run
  `SELECT id, competitor_number FROM competitor WHERE competitor_number NOT REGEXP '^[0-9]+$'` and fix or clear every
  row it returns.

---

## 📊 Statistics

- **Files Changed:** 46 on the release branch against `develop` (counted before the release documentation was
  written); 1,075 insertions and 138 deletions
- **New Source Files:** 4, plus two Flyway migrations and three test classes
- **Deleted Source Files:** 0
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **One number, one type.** A competitor number is a whole number in the entity, the table and the response, which
  lets the repository match it exactly and the lookup compare it directly.
- **Strings in, numbers out.** Requests keep the number as text so the JSON and CSV contract reads as before, and the
  service refuses a value that is not a whole number rather than storing it.
- **Number first, then name.** The lookup resolves by number, and the name only narrows the number's matches, so a
  name cannot select a different competitor than the number does.
- **A MAJOR.** Requiring `text/plain`, renaming `nickname` and returning a whole-number `competitorNumber` break
  existing callers, which `AGENTS.md`'s Semantic Versioning rules classify as MAJOR.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` passes: 1,109 tests with no failures, errors or skips, and the coverage gate is met —
  98.7% of branches (440 of 446), 98.2% of lines and 98.3% of instructions.
- New unit, implementation and integration tests cover `findCompetitor`; updated request-model, CSV mix-in, controller
  and service tests cover `nickName`, the whole-number competitor number and its blank and non-numeric cases.
- The migrations were not run against MySQL for this release; the tests use the H2 `test` profile, where Hibernate
  creates the schema.

---

## 🐛 Known Issues

- `EntityIpscCompetitorService` is not called by any service yet, and `MatchCompetitorResult` and
  `MatchCompetitorResultHolder` are not returned by any endpoint (Gap #36).
- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports
  is read but never used as the row's own ID, since the imports only create records.
- `MatchRequest` and `MatchRequestCsvMixIn` accept a missing `matchFirearmType` or `matchCategory`, so the service is
  the only check that rejects a create or replace without them.
- No service or controller operates on `ShooterLog`, `ShooterLogCompetitor` or `ShooterLogOverall` yet, and no
  calculation job fills them in (Gap #6, partially completed).
- `pom.xml` still overrides `tomcat.version` (to `11.0.26`) for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26). `logback.version`, the Jackson BOM properties and `flyway-mysql` are likewise set above
  Spring Boot's managed versions.

---

## 🔮 Future Enhancements

- Wire `EntityIpscCompetitorService` into the match competitor import, and return `MatchCompetitorResultHolder` from
  it so a bad row is reported rather than failing the whole import (Gap #36).
- Build a `ShooterLogService` and controller over the existing repositories, committing through `TransactionService`,
  following the same phased pattern that closed Gap #1 (Gap #6).
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.26` or later (Gap #26), and the
  other dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 11.0.0 tightens the competitor contract — `text/plain` bulk uploads, `nickName` and a whole-number competitor
number — as a breaking change for existing callers; see the Migration Guide for what to change and the migrations that
run on upgrade.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
