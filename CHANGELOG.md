# HPSC Website Backend

## 🧾 Change Log

All notable changes to the HPSC Website Backend project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) as of version 5.0.0.

---

### Table of Contents

- [🧪 Unreleased](#-unreleased)
- [🧾 Version 18.0.0](#-1800---2026-11-14) ← Current
- [🧾 Version 17.0.0](#-1700---2026-11-12)
- [🗄️ Archived Versions 14.0.0 – 16.4.1](/documentation/archive/v14-v16/CHANGELOG_v14-v16.md)
- [🗄️ Archived Versions 10.0.0 – 13.3.0](/documentation/archive/v10-v13/CHANGELOG_v10-v13.md)
- [🗄️ Archived Versions 8.0.0 – 9.1.0](/documentation/archive/v8-v9/CHANGELOG_v8-v9.md)
- [🗄️ Archived Versions 5.0.0 – 7.4.1](/documentation/archive/v5-v7/CHANGELOG_v5-v7.md)
- [🗄️ Archived Versions 1.0.0 – 4.1.0](/documentation/archive/v1-v4/CHANGELOG_v1-v4.md)
- [📋 Version Policy](#-version-policy)
- [🚀 Upgrade Guide](#-upgrade-guide)
- [🤝 Contributing](#-contributing)
- [💬 Support](#-support)

---

### 🧪 [Unreleased]

#### ➕ Added

##### Database

- Migration `V11_14_0__create_match_result.sql` creates `match_result`, backing the new `MatchResult` entity

##### Domain

- `MatchResult` entity, with the same structure as `ShooterResult`, for calculating a shooter's rank in the match
  overall, independent of competitor category and division

##### API

- `POST /ipsc/match-results/matches/{matchId}` calculates `rankInLog` and `percentageInLog` for every verified
  competitor with points in a match, ranked together across the whole match with no split by firearm type, division
  or competitor category; `GET /ipsc/match-results/matches/{matchId}` and `GET /ipsc/match-results` read them back
- `IpscMatchResultController`, `IpscMatchResultService`, `MatchResultRepository`, `TransactionService.replaceMatchResults`
  and the `MatchResultResponse`, `MatchResultMatchResponse` and `MatchResultResponseHolder` response classes

##### Tests

- **`IpscMatchResultServiceTest`, `IpscMatchResultServiceIntegrationTest`, `IpscMatchResultControllerTest` and
  `MatchResultResponseTest`:** Cover match result calculation, ranking and percentages, the five-minute staleness
  rule, retrieval, the controller endpoints and the response mapping; `TransactionServiceTest` and the service tests
  that build `TransactionServiceImpl` now pass it a `MatchResultRepository`

#### 🔄 Changed

##### Configuration

- **`pom.xml`:** `logback.version` is raised from `1.6.5` to `1.6.7`, and `h2.version` is pinned to `2.5.252`, since
  the `2.4.240` Spring Boot manages has a known vulnerability

##### Documentation

- **`ARCHITECTURE.md`, `README.md` and `flyway-migration-versioning.md`:** Describe `MatchResult`, the
  `IpscMatchResultController` and `IpscMatchResultService`, the match result response models and the
  `V11_14_0__create_match_result.sql` migration, which they left out
- **Archived changelogs and release notes:** Version headings in `CHANGELOG_v1-v4.md`, `CHANGELOG_v5-v7.md`,
  `CHANGELOG_v8-v9.md` and `CHANGELOG_v10-v13.md` now have link definitions, and bracketed text that is not a link is
  escaped, so no link label is left unresolved
- **`improvement-plan.md` and `improvement-plan-tasks.md`:** Record Gap #56, the missing `MatchResult` documentation,
  and its closure, and add the `logback.version` and `h2.version` overrides to the roadmap's Ongoing row

---

### 🧾 [18.0.0] - 2026-11-14

#### ➕ Added

##### Database

- **`V11_13_0__create_shooter_log_overall_match.sql`:** Creates the `shooter_log_overall_match` join table linking a
  `shooter_log_overall` to the `ipsc_match`es it was calculated from, with foreign keys to both and an index on
  `match_id`.

##### Domain

- **`ShooterLogOverall.matches`:** The matches an overall was calculated from, being the competitor's best matches in
  the shooter log, kept in the new `shooter_log_overall_match` table.

##### Models

- **`ShooterLogOverallResponse.matchIds`:** Each overall result now lists the identifiers of the matches it was
  calculated from, empty if it was calculated from none.

#### 🔄 Changed

##### Configuration

- **Breaking: the OpenAPI document and Swagger UI are off by default:** `springdoc.api-docs.enabled` and
  `springdoc.swagger-ui.enabled` are set to `false` in `application.properties`, so the OpenAPI `/v3/api-docs` endpoint
  and the Swagger UI are no longer exposed by default, and to `true` in `application-dev.properties`, so they stay
  available during development; the production profile inherits the default. A deployment that serves the OpenAPI
  document, for example to generate a client from it, or the Swagger UI must set both properties to `true`.

##### Domain

- **Lazy associations left out of `equals`, `hashCode` and `toString`:** `Competitor.homeClub` and `emailAddresses`,
  `IpscMatch.club`, `MatchCompetitor.competitor` and `match`, `ShooterLog.matches`, `ShooterLogOverall.shooterLog`,
  `competitor` and `matches`, and `ShooterResult.match`, `competitor` and `matchCompetitor` no longer take part in the
  generated methods. Hibernate calls `hashCode` on the matches of a `@ManyToMany` list when it compares them on commit,
  which read the match's club and failed with a `LazyInitializationException` when calculating a shooter log's overall
  results. Two entities now compare equal when their own columns match, whatever their lazy associations hold.

##### Services

- **`IpscShooterLogOverallServiceImpl.calculateShooterLogOverall`:** Records the matches each overall was calculated
  from: the competitor's best `SHOOTER_LOG_BEST_MATCHES` results by percentage, with equal percentages taken from the
  most recent match first. The overall percentage is still the average of those same results. An overall calculated
  before its matches were recorded, or from other matches, is treated as out of date and recalculated.

##### Repositories

- **`ShooterLogOverallRepository`:** `findAllByShooterLogIdWithDetails` and `findAllWithDetails` also fetch each
  overall's matches, so they can be read outside a transaction.

##### Documentation

- **`AGENTS.md` and `ARCHITECTURE.md`:** The 3-tier service test rule now says every service has an interface test and an
  integration test, and an `ImplTest` only where its impl has helper methods worth testing directly, instead of
  listing five services that follow the split, and `ARCHITECTURE.md`'s test tree no longer lists the removed
  `exceptions/` directory. Closes improvement plan Gap #55.
- **Missing `@since` tags:** Added them to the Javadoc of `CompetitorMapper`, `MatchCompetitorMapper`,
  `MatchCompetitorRowMapper` and `MatchMapper`, and of 74 public and package-private methods and constructors in the
  mappers, helpers, request and response models, `ControllerAdvice` and the service implementations, each with the
  release that first contained it. The two private records and the protected and private helper methods stay untagged.
- **`README.md`, `AGENTS.md`, `ARCHITECTURE.md` and `CONTRIBUTING.md`:** Now say the OpenAPI document and Swagger UI are
  served only under the `dev` profile, and are off by default and under `prod`, instead of presenting them as always
  available. `README.md` also says how to serve them under another profile, by setting `springdoc.api-docs.enabled` and
  `springdoc.swagger-ui.enabled` to `true`. Closes improvement plan Gap #53.
- **`ARCHITECTURE.md`:** The Domain Entities table now lists `ShooterLogOverall`'s many-to-many link to `IpscMatch`
  through `shooter_log_overall_match`, and the sentence below it says `ShooterLog.matches` and
  `ShooterLogOverall.matches` are `@ManyToMany` links kept in join tables rather than a `@ManyToOne`. The
  `IpscShooterLogOverallService` row and the `ShooterLogOverallResponse` description mention the matches an overall was
  calculated from, as `matchIds`. Closes improvement plan Gap #54.
- **`improvement-plan.md` and `improvement-plan-tasks.md`:** Record two new open gaps, #53 (the OpenAPI document and
  Swagger UI are off by default and on only in `dev`, but `README.md`, `AGENTS.md` and `ARCHITECTURE.md` present them as
  always available) and #54 (`ARCHITECTURE.md`'s entity table omits `ShooterLogOverall.matches`), and remove a stale
  Success Criteria entry for Gap #6, which the "✅ Met in v16.1.0" entry already supersedes.

##### Tests

- **Brittle mapper, helper and util tests removed:** `ValueUtilTest` drops nine redundant or vacuous tests (special
  characters, Unicode, a 10,000-character string, a 1000-item list, a duplicate of the custom-`toString` test and two
  that never asserted what their names claimed). The mapper tests no longer assert unknown-category message text, a
  constant's value or `verify(never())` on lookups the result already proves, `MatchCompetitorMapperTest` shares its
  field assertions and takes the club name from `ClubIdentifier.HPSC`, and the helper tests check the distinguishing
  token of a "not found" message instead of the whole sentence.
- **Brittle model, enum and exception tests removed:** The constructor and getter pass-through tests in the request and
  response tests go, keeping the UUID, MIME type, derived-success and default-list logic and one test for each
  hand-written constructor, and the enum tests drop their repeated lookups and `toString` restatements. The JSON
  serialisation tests check representative fields instead of every property, `AwardCeremonyResponseTest` uses a fixed
  date instead of `LocalDate.now()`, and `CompetitorVerifyRequestTest` expects `MismatchedInputException` rather than
  any `Exception`. The exception tests and the `IpscCommonScoreTest`, `IpscMatchScoreTest` and
  `IpscMatchStageScoreTest` classes stay, as their hand-written constructors need them to keep line coverage above the
  97% minimum.
- **Brittle controller, advice, repository and migration tests removed:** The controller tests that only stubbed a
  service and asserted the same body came back, and the ones that checked an exception propagates (the controllers
  have no handlers; `ControllerAdviceTest` covers the mapping), are deleted, keeping the status-decision tests, so each
  endpoint still has a status test. `ControllerAdviceTest` drops its timestamp and blank-message tests and merges each
  handler's single-assertion tests, and two misnamed tests are fixed. The repository tests filter by the IDs they
  saved instead of counting a shared table, and `FlywayMysqlMigrationTest` now asserts no pending or failed
  migrations instead of counting `.sql` files from the working directory. The `matchIds` JSON-shape check moves into
  `ShooterLogOverallResponseTest`.
- **Brittle service tests removed:** `IpscEntityClubServiceImplTest` and `IpscEntityCompetitorServiceImplTest` shrink
  to one test each and `TransactionServiceImplTest` is removed, as the interface tests already cover the same cases by
  result rather than by the order the mocks are consulted. Five of the six "reading the CSV fails" tests that mocked
  `CsvMapper`, the 1000-row CSV tests and other tests that only mirrored a mapper or a setter are deleted. Exact message
  text is now asserted by exception type or one key token, `times(N)` is dropped where the result already proves the
  count, and the integration tests compare row counts by before and after deltas or by the IDs they saved instead of
  absolute counts on the shared database. The repeated CSV headers and warning checks move into constants and helpers.
- **`ShooterLogOverallResponseTest`:** New tests for the response built from an overall, covering `matchIds` for an
  overall with matches, with none and with its matches not set.
- **`IpscShooterLogOverallServiceTest`:** The best-matches case now also checks which matches the overall records, and
  the existing overall fixtures carry their matches. New cases cover equal percentages, fewer matches than the limit,
  results without a percentage, a match shared by two results, replacing an existing overall's matches, and an overall
  being recalculated when its matches are missing or differ but not when only their order does, and a case with matches
  whose club cannot be loaded. The read cases now check `matchIds`.
- **`IpscShooterLogOverallServiceIntegrationTest`:** New cases record each competitor's best matches in the
  `shooter_log_overall_match` table, check that recalculating does not duplicate them or the overalls the queries
  return, that deleting an overall removes its match rows, and that both read endpoints return `matchIds`. A further
  case runs the calculation and reads the results without a surrounding transaction, which is what exposed the
  `LazyInitializationException` fixed under Domain above.
- **`ShooterLogOverallRepositoryIntegrationTest`:** New cases check that `findAllByShooterLogIdWithDetails` and
  `findAllWithDetails` fetch each overall's shooter log, competitor and matches, return an overall with several matches
  once and an overall with none with an empty list, and that the first returns only the requested log's overalls.

---

### 🧾 [17.0.0] - 2026-11-12

#### ➕ Added

##### Models

- **`MatchCompetitorBulkResponse.rowNumber` and `ShooterLogBulkResponse.rowNumber`:** Each row of the bulk match
  competitor import and of the bulk shooter log create, replace and update imports now carries its `rowNumber`, like
  the competitor and match bulk rows.
- **`CompetitorBulkResponse.success`:** Each row of the bulk competitor import now carries a `success` flag like the
  match competitor and shooter log bulk rows: `true` for a row that was created or updated, and `false` for a row that
  failed, with the reason in its `message`, no `competitor` and a `competitorRow` holding every value the row
  supplied, as text.
- **`CompetitorBulkResponse.competitorRow`:** The new `CompetitorRow` type, like `MatchCompetitorRow`, reports the
  values of a failed row, with the email addresses in one value separated by `;`. It is `null` for a row that was
  imported.

##### Tests

- **`CompetitorRowTest`, `MatchRowTest`, `MatchResponseTest`, `ShooterLogResponseTest` and
  `ShooterResultResponseTest`:** New tests for the row classes, the `messages` field's JSON and the responses built from
  a log with no loaded matches or an uncalculated percentage, and the service, mapper and impl tests gain cases for
  failed rows, row numbers, a repeated club number, data integrity conflicts, CSV read failures and the shooter log bulk
  patch — bringing line coverage to 99.2% and branch coverage to 100%.

#### 🔄 Changed

##### Models

- **Breaking: `MatchResponse.warnings` renamed to `messages`:** The list of problems found with a match when it is read
  keeps its behaviour (left out of the JSON when empty) but is now serialised as `messages`, so clients that read
  `warnings` must read `messages` instead.
- **`CompetitorResponseHolder`:** Renamed `CompetitorBulkResponseHolder`, after the `CompetitorBulkResponse` rows it
  carries, and so is its OpenAPI schema name; the JSON it serialises to is unchanged.

##### Services

- **Breaking: bulk match import reports failed rows:** `MatchResponseHolder` is renamed `MatchBulkResponseHolder` and
  now carries a `results` list with a `MatchBulkResponse` for every row: its `rowNumber`, `success`, `message`, the
  created `match` and, for a row that failed, a `matchRow` (the new `MatchRow`) holding every value the row supplied. A
  row that is missing a required field, has an unrecognised firearm type or category or names an unknown club is now
  reported and skipped instead of failing the whole request with a 400 or 404. Each row is saved in its own transaction,
  so the other rows are still imported. A CSV that is blank or cannot be parsed still fails the request. Clients that
  relied on a bad row failing the whole import must now check each row's `success`.
- **Breaking: bulk competitor import reports failed rows:** A row that is missing a required field, has an unrecognised
  gender, names an unknown home club or lacks a required club number is now reported in its result with `success`
  `false` and skipped, instead of failing the whole request with a 400 or 404. Each row is saved in its own transaction,
  so the other rows are still imported. A CSV that is blank or cannot be parsed still fails the request. Clients that
  relied on a bad row failing the whole import must now check each row's `success`.
- **`IpscCompetitorServiceImpl.createCompetitors`:** A row that repeats a club number an earlier row of the same file
  already imported is now reported as failed — "Club number … was already imported by row N of this file." — instead of
  silently updating the competitor that earlier row just saved. A club number that matches a competitor already in the
  database still updates and verifies it, and a failed row does not reserve its club number.
- **`MatchCompetitorMapper.resolveFirearmType`, `IpscMatchCompetitorServiceImpl` and `IpscShooterLogServiceImpl`:**
  Branches that could never run are removed — the "division has no firearm type" error, as every division has one, and
  the null guards on the bulk request's match ID lists, which default to empty lists — with no change in behaviour. The
  null check on the target club stays, so a skipped row's message only names a club when there is one.

##### Documentation

- **`ARCHITECTURE.md`:** The competitor and match bulk CSV import flows, and the surrounding sentences, now say each row
  is saved in its own transaction and a failed row is reported and skipped, instead of all rows being saved in one
  transaction so that a bad row leaves none persisted. The flows end in `CompetitorBulkResponseHolder` and
  `MatchBulkResponseHolder`, each with a result per row. Closes improvement plan Gap #51.
- **Archived the v14 – v16 release notes and pull request descriptions:** Moved them from `documentation/history/` to
  `documentation/archive/v14-v16/`, leaving `documentation/history/` with the v17 releases onwards; `README.md` and
  `AGENTS.md` now say that folder starts at v17.0.0
- **Archived versions 14.0.0 – 16.4.1:** Moved the `CHANGELOG`, `HISTORY` and `EVOLUTION_OVERVIEW` entries of those
  versions into `CHANGELOG_v14-v16.md`, `HISTORY_v14-v16.md` and `EVOLUTION_OVERVIEW_v14-v16.md` in
  `documentation/archive/v14-v16/`, with the link definitions their headings use; the links in `README.md`,
  `AGENTS.md`, `CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md` and the `prep-version-release` skill follow the
  new locations

#### 🗑️ Removed

##### Services

- **`TransactionService.saveMatches`, `saveMatchCompetitors` and `saveShooterResults`:** Removed with their tests, as
  no production code called them — the bulk match import now saves each row through `saveMatch`, the match competitor
  import through `saveMatchCompetitor` and a shooter result calculation through `replaceShooterResults`. Closes
  improvement plan Gap #52.

---

### 🗄️ Archived Versions (14.0.0 – 16.4.1)

The change log entries for versions 14.0.0 to 16.4.1 are archived, unchanged, in
[`documentation/archive/v14-v16/CHANGELOG_v14-v16.md`](/documentation/archive/v14-v16/CHANGELOG_v14-v16.md).

---

### 🗄️ Archived Versions (10.0.0 – 13.3.0)

The change log entries for versions 10.0.0 to 13.3.0 are archived, unchanged, in
[`documentation/archive/v10-v13/CHANGELOG_v10-v13.md`](/documentation/archive/v10-v13/CHANGELOG_v10-v13.md).

---

### 🗄️ Archived Versions (8.0.0 – 9.1.0)

The change log entries for versions 8.0.0 to 9.1.0 are archived, unchanged, in
[`documentation/archive/v8-v9/CHANGELOG_v8-v9.md`](/documentation/archive/v8-v9/CHANGELOG_v8-v9.md).

---

### 🗄️ Archived Versions (5.0.0 – 7.4.1)

The change log entries for versions 5.0.0 to 7.4.1 are archived, unchanged, in
[`documentation/archive/v5-v7/CHANGELOG_v5-v7.md`](/documentation/archive/v5-v7/CHANGELOG_v5-v7.md).

---

### 🗄️ Archived Versions (1.0.0 – 4.1.0)

The change log entries for versions 1.0.0 to 4.1.0 are archived, unchanged, in
[`documentation/archive/v1-v4/CHANGELOG_v1-v4.md`](/documentation/archive/v1-v4/CHANGELOG_v1-v4.md).

---

### 📋 Version Policy

#### Semantic Versioning (Current)

As of version 5.0.0, this project follows [Semantic Versioning 2.0.0](https://semver.org/):

- **MAJOR** version for incompatible API changes
- **MINOR** version for backward-compatible functionality additions
- **PATCH** version for backward-compatible bug fixes

Every release must follow these rules strictly — see [`AGENTS.md`'s Semantic Versioning
section](/AGENTS.md#semantic-versioning) for what counts as a MAJOR, MINOR or PATCH change in this project and how
each release is classified from the `[Unreleased]` section.

#### Legacy Versioning (v1.x – v4.x)

Earlier releases used a non-semantic versioning scheme. For historical documentation,
see [ARCHIVE.md](/documentation/legacy/ARCHIVE.md).

---

### 🚀 Upgrade Guide

#### From v5.3.0 to v5.4.0

**Breaking Changes:** None

1. Update the version in `pom.xml` to `5.4.0`
2. Replace any `IpscMatchService` injection points with `TransformationService`
3. Update import statements for classes moved from `ipsc/domain` to `ipsc/data`
4. Update `MatchCompetitorEntityService` call sites to handle `List<>` return types
5. Run `./mvnw clean install` to rebuild the project

#### From v4.1.0 to v5.0.0

**Breaking Changes:** None

1. Update the version in `pom.xml` to `5.0.0`
2. Run `./mvnw clean install` to rebuild the project
3. Restart the application
4. Existing data and configurations remain compatible

#### From v4.0.0 to v4.1.0

**Breaking Changes:** None

Migration: See v4.1.0 release notes

#### From v3.x to v4.x

**Breaking Changes:** Yes

- Update entity references from `Match` to `IpscMatch`
- Update service injections to use `IpscMatchRepository`
- See v4.0.0 release notes for a detailed migration guide

---

### 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Create a feature branch from `main`
2. Make your changes with comprehensive test coverage
3. Document your changes in the appropriate sections of this CHANGELOG
4. Submit a pull request with a detailed description

---

### 💬 Support

For issues, feature requests or questions:

- **GitHub Issues:** [tahoni/hpsc-web-springboot/issues](https://github.com/tahoni/hpsc-web-springboot/issues)
- **Repository:** [tahoni/hpsc-web-springboot](https://github.com/tahoni/hpsc-web-springboot)

---

**Last Updated:** 2026-08-25

[Unreleased]: https://github.com/tahoni/hpsc-web-springboot/compare/v18.0.0...HEAD
[18.0.0]: https://github.com/tahoni/hpsc-web-springboot/compare/v17.0.0...v18.0.0
[17.0.0]: https://github.com/tahoni/hpsc-web-springboot/compare/v16.4.1...v17.0.0
