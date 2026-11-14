# Release Notes – Version 18.0.0

**Release Date:** November 14, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Recording the Matches Behind a Shooter Log Overall**

Version 18.0.0 is a major release. A shooter log overall averages each competitor's best matches in the log, but gave no
sign of which matches those were. It now records them in a new join table and returns their identifiers as `matchIds`.
Recording them exposed a `LazyInitializationException` when overalls were calculated outside a session, which is fixed
by keeping the entities' lazy associations out of their generated `equals`, `hashCode` and `toString`. The release also
turns the OpenAPI document and Swagger UI off by default, so a deployment serves them only on purpose. That last change
alters what a running deployment serves, so it is a breaking release.

---

## ⭐ Key Highlights

### 🧮 An Overall That Names Its Matches

- `ShooterLogOverall.matches` records the matches an overall was calculated from, being the competitor's best
  `SHOOTER_LOG_BEST_MATCHES` results by percentage, in the new `shooter_log_overall_match` table
- `ShooterLogOverallResponse.matchIds` returns their identifiers, empty if an overall was calculated from none
- Equal percentages are taken from the most recent match first, so the same matches are picked every time
- An overall calculated before its matches were recorded, or from other matches, is recalculated

### 🛡️ Safe Outside a Session

- The lazy associations of `Competitor`, `IpscMatch`, `MatchCompetitor`, `ShooterLog`, `ShooterLogOverall` and
  `ShooterResult` no longer take part in the generated `equals`, `hashCode` and `toString`
- Calculating, reading and recalculating overalls works without an open persistence session, which a new test without a
  surrounding transaction proves

### 🔒 API Docs Off by Default

- The OpenAPI document (`/v3/api-docs`) and Swagger UI are off in `application.properties`, so also under `prod`, and on
  in `application-dev.properties`
- `README.md`, `AGENTS.md`, `ARCHITECTURE.md` and `CONTRIBUTING.md` say so, and how to serve them under another profile

### 🧹 A Leaner, Sturdier Suite

- Tests that only mirrored the implementation, duplicated another layer's test or asserted incidental message text,
  `verify` counts or row counts on the shared database are removed or loosened
- `@since` tags are added to the four mapper classes and 74 public and package-private methods and constructors
- `ARCHITECTURE.md` and `AGENTS.md` describe the entity links, service test tiers and test tree as they are

---

## 📦 What's New

### Added

- **`V11_13_0__create_shooter_log_overall_match.sql`:** Creates the `shooter_log_overall_match` join table, with foreign
  keys to `shooter_log_overall` and `ipsc_match` and an index on `match_id`.
- **`ShooterLogOverall.matches`:** The matches an overall was calculated from.
- **`ShooterLogOverallResponse.matchIds`:** The identifiers of those matches.
- **Tests:** New cases for the recorded matches, the fetch joins of `ShooterLogOverallRepository`, matches whose club
  cannot be loaded and the calculation without a surrounding transaction, and a new `ShooterLogOverallResponseTest`.
- **`@since` tags:** On the four mapper classes and 74 public and package-private methods and constructors.

### Changed

- **Breaking: the OpenAPI document and Swagger UI are off by default:** `springdoc.api-docs.enabled` and
  `springdoc.swagger-ui.enabled` are `false` in `application.properties` and `true` in `application-dev.properties`;
  the production profile inherits the default.
- **`IpscShooterLogOverallServiceImpl.calculateShooterLogOverall`:** Records the best matches each overall was
  calculated from, averages those same results, takes equal percentages from the most recent match first and
  recalculates an overall whose matches are missing or differ.
- **`ShooterLogOverallRepository`:** `findAllByShooterLogIdWithDetails` and `findAllWithDetails` also fetch each
  overall's matches.
- **Lazy associations left out of `equals`, `hashCode` and `toString`:** On six entities, so Hibernate's comparison of
  an overall's matches on commit no longer reads a match's lazy club.
- **`README.md`, `AGENTS.md`, `ARCHITECTURE.md` and `CONTRIBUTING.md`:** Say the OpenAPI document and Swagger UI are
  served only under `dev`. `ARCHITECTURE.md` lists `ShooterLogOverall`'s link to `IpscMatch` and drops the removed
  `exceptions/` test directory, and `AGENTS.md` describes the service test tiers as they are. Closes improvement plan
  Gaps #53, #54 and #55.

### Fixed

Nothing is fixed.

### Removed

- **Brittle tests:** The three exception test classes, `IpscCommonScoreTest`, `IpscMatchScoreTest`,
  `IpscMatchStageScoreTest` and `TransactionServiceImplTest`, and the pass-through, duplicate, vacuous and
  message-text tests across the services, controllers, models, enums, mappers, helpers and utils.

---

## 🚀 Migration Guide

No endpoint path, request or response contract is removed. Deployments and clients need to adapt:

1. **Serve the OpenAPI document and Swagger UI on purpose.** They are off by default and under `prod`. A deployment that
   serves them, or a client that is generated from the OpenAPI document, must set `springdoc.api-docs.enabled` and
   `springdoc.swagger-ui.enabled` to `true`. The `dev` profile already does.
2. **Let Flyway apply `V11_13_0`.** It runs at startup and creates the `shooter_log_overall_match` table; nothing else
   in the schema changes.
3. **Expect overalls to be recalculated once.** Existing overalls have no recorded matches, so the next call to
   calculate a shooter log's overalls recalculates each one and records them.
4. **Read `matchIds` if you want the matches.** It is a new field on each overall result; clients that ignore it are
   unaffected.

---

## 📊 Statistics

- **Files Changed:** 119 against `main` before this release's history and release notes; 1,549 insertions and 3,223
  deletions
- **New Source Files:** 1 (`V11_13_0__create_shooter_log_overall_match.sql`)
- **New Test Files:** 1 (`ShooterLogOverallResponseTest`)
- **Deleted Test Files:** 7 (three exception tests, three score tests and `TransactionServiceImplTest`)
- **New Dependencies:** 0
- **Improvement Plan:** Gaps #53, #54 and #55 added and closed, leaving all 55 closed

---

## 🧭 Design Notes

- **Name the matches, don't recompute them.** An overall stores the matches it used, so a client can show them without
  repeating the best-of selection, and a change to those matches can be detected.
- **Entities compare by their own columns.** Generated `equals`, `hashCode` and `toString` must not load a lazy
  association: Hibernate itself calls `hashCode` on collection elements, and a log line would otherwise load them too.
- **Turn docs on, not off.** An API description is useful to a developer and a client generator, but it is also a map
  of a deployment, so it is served only where it is asked for.
- **Tests should fail for a reason.** A test that mirrors the code, repeats another layer's test or pins a sentence
  fails when nothing is wrong and passes when something is; those are removed or loosened.
- **A MAJOR.** An existing deployment that served the OpenAPI document or Swagger UI stops doing so without a
  configuration change, which `AGENTS.md`'s Semantic Versioning rules classify as MAJOR.

---

## 🧪 Testing

- `./mvnw test` passes: 1,489 tests with no failures or errors, 3 of them skipped because `FlywayMysqlMigrationTest`
  runs only where `FLYWAY_MYSQL_URL` is set; `./mvnw verify -Pcoverage` meets the 97% line and branch minimums, at 99.2%
  line and 99.9% branch coverage.
- New tests cover the recorded matches and their recalculation, the fetch joins of `ShooterLogOverallRepository`,
  matches whose club cannot be loaded and the calculation and reads without a surrounding transaction.

---

## 🐛 Known Issues

- A bulk competitor row without a club number always creates a competitor, so re-importing a file of such rows, as
  every row for a home club other than HPSC's is, creates them again.
- `pom.xml` still overrides `logback.version`, the Jackson BOM properties and `mysql-connector-j`, set above Spring
  Boot's managed versions.

---

## 🔮 Future Enhancements

- Drop the remaining dependency pins once Spring Boot manages versions at least as new.
- Consider a safer key than the club number for matching non-HPSC competitor rows, if re-importing them proves a
  problem.

---

## 👥 Contributors

- Leoni Lubbinge

---

## 📝 Notes

Version 18.0.0 makes a shooter log overall name the matches it was calculated from and keeps its calculation safe once
the persistence session has closed, and it turns the OpenAPI document and Swagger UI off by default. It leaves the
improvement plan with all 55 gaps closed and none open.
