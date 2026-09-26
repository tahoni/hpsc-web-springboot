# Release Notes – Version 8.10.0

**Release Date:** September 26, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Strict Semantic Versioning, Production Profile & Roadmap Gap Closure**

Version 8.10.0 is a minor release. Semantic Versioning stops being a matter of precedent and becomes a rule the
release process checks: `AGENTS.md` now defines what counts as a MAJOR, MINOR or PATCH change for this project,
breaking changes are flagged in `CHANGELOG.md` as they land, and the release skill refuses a version the change set
doesn't justify. Production gains its own `prod` profile, and the database-profile and logging documentation are
brought in line with the configuration that actually exists. Three improvement-plan sweeps recorded Gaps #25–#28;
this release closes three of them and progresses the fourth, leaving only Gap #6 — the scoring/shooter-log layer —
open. It is the first release classified under the new rules: MINOR, for the new optional `prod` profile.

---

## ⭐ Key Highlights

### 🔢 Strict Semantic Versioning

- New Semantic Versioning subsection in `AGENTS.md`'s Git Workflow defining MAJOR (breaking REST contract, import
  format or configuration change), MINOR (backward-compatible additions and deprecations) and PATCH (fixes and
  changes with no externally visible effect)
- A release is classified from `CHANGELOG.md`'s `[Unreleased]` section — the highest-ranking change wins — and
  backward-incompatible entries start with `**Breaking:**`
- `prep-version-release` validates the requested version before bumping, and again after syncing `[Unreleased]`

### 🏭 Production Profile

- New `application-prod.properties` points the `prod` profile at `localhost:3306/hpsc_prod`, reading
  `MYSQL_USER`/`MYSQL_PASSWORD`
- The no-profile run is unchanged and still takes its datasource URL from outside the application — now documented

### 🛤️ Roadmap

- Gaps #25 (entity-level unit tests), #27 (database-profile docs) and #28 (`staging` logging profile) closed
- Gap #26 (`tomcat.version` override) progressed: overrides are now re-checked at every release, and this one stays
  until a Spring Boot release manages Tomcat `11.0.25`
- Only Gap #6 — the match scoring / shooter-log service and controller layer — remains open

---

## 📦 What's New

### Added

#### Configuration

- **`application-prod.properties`:** New `prod` profile pointing production at `localhost:3306/hpsc_prod`, still
  reading `MYSQL_USER`/`MYSQL_PASSWORD`; documented in `AGENTS.md`, `ARCHITECTURE.md` and `CONTRIBUTING.md`

#### Tests

- **`IpscMatchTest`:** New entity unit test guarding `IpscMatch.stages`' exclusion from Lombok's
  `toString`/`equals`/`hashCode` — `IpscMatchStage.match` points straight back, so dropping either exclusion makes
  both recurse into a `StackOverflowError` — plus `stages` initialising empty and mutable (Gap #25)

#### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** Gaps #25–#28 recorded by three
  `update-improvement-plan-gaps` sweeps

### Changed

#### Tooling

- **`AGENTS.md`, `prep-version-release`:** Semantic Versioning is now a strict, documented rule, and the release
  skill validates each requested version against it
- **`generate-commit-message`, `sync-unreleased-changes`:** Both skills apply the `**Breaking:**` prefix;
  `sync-unreleased-changes` flags a missing or wrong prefix as drifted and reports the release level `[Unreleased]`
  implies
- **`AGENTS.md`, `prep-version-release`:** The Release Checklist's `pom.xml` step re-checks every manual
  dependency-version override against the Spring Boot parent's own `spring-boot-dependencies` POM (Gap #26)

#### Documentation

- **`HISTORY.md`, `ARCHITECTURE.md`, `README.md`:** The Short-term roadmap no longer plans entity-level unit tests
  for the whole domain model; the test tree gains a `domain/` entry and the unit-test categories include entities
- **`CHANGELOG.md`, `RELEASE_NOTES.md`, archived v8.7.0/v8.9.0 release notes and PR descriptions:** Removed now
  listed after Fixed, matching `AGENTS.md`'s category order
- **`README.md`, `CHANGELOG.md`:** The Semantic Versioning note and Version Policy section point at `AGENTS.md`'s
  Semantic Versioning section as the definition of each release level

#### Build & Metadata

- Project version bumped to **8.10.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

### Fixed

#### Documentation

- **`AGENTS.md`, `CONTRIBUTING.md`, `README.md`:** The database-profile docs said a run with no profile needs only
  `MYSQL_USER`/`MYSQL_PASSWORD`, but `application.properties` sets no `spring.datasource.url` — the URL must be
  supplied externally (e.g. `SPRING_DATASOURCE_URL`). The credentials wording now excludes `local`, which connects as
  `hpsc_dev` with `MYSQL_LOCAL_PASSWORD`, and the Database Profiles table gains `local` and `prod` rows (Gap #27)

### Removed

#### Configuration

- **`logback-spring.xml`:** The `staging` `<springProfile>` block and its `logs/application-staging.log` appender —
  no staging environment exists, and the profile had no properties file or documentation behind it (Gap #28)

---

## 🚀 Migration Guide

No client-facing migration is needed: no endpoint, JSON field, import format or database schema changed.

**Deployments:** the new `prod` profile is optional. An existing deployment that runs with no profile keeps working
exactly as before, supplying its datasource URL externally (e.g. `SPRING_DATASOURCE_URL`) alongside
`MYSQL_USER`/`MYSQL_PASSWORD`. To use the new profile instead, start the application with
`-Dspring-boot.run.profiles=prod` (or `SPRING_PROFILES_ACTIVE=prod`); it connects to `localhost:3306/hpsc_prod` and
logs to `logs/application-prod.log`.

**`staging` profile:** it had no datasource of its own and no documentation. `logback-spring.xml` now has no block
for it, so a deployment that still activated `staging` would get no log output — switch it to `prod`.

**Contributors:** CHANGELOG entries for backward-incompatible changes must now start with `**Breaking:**`, and a
release's version must be the one-step increment its change set implies — see `AGENTS.md`'s Semantic Versioning
section.

---

## 📊 Statistics

- **Total Commits:** 13 (Semantic Versioning rules and their skill updates, Gaps #25–#28 and their fixes, the `prod`
  profile, the `staging` profile removal, the Removed/Fixed reorder, plus this release's version bump, release
  documentation and PR description commits)
- **Files Changed:** 24
- **Insertions:** 1,000 lines
- **Deletions:** 304 lines
- **Net Change:** +696 lines
- **New Source Files:** 1 (`application-prod.properties`)
- **Deleted Files:** 0
- **New Test Files:** 1 (`IpscMatchTest`)

---

## 🧭 Design Notes

- **Versions follow from the change set.** Earlier releases were scoped by precedent — "matching the v8.5.0/v8.7.0
  precedent". The new rules make the classification mechanical: read `[Unreleased]`, find the highest-ranking change,
  increment by one step. This release shows why it matters: its gaps were first marked against v8.9.1, until the
  `prod` profile — a backward-compatible addition — made it a MINOR release.
- **Flag breaking changes when they land.** A `**Breaking:**` prefix written with the change is far more reliable
  than reconstructing compatibility at release time, and it lets the release skill check the requested version
  against what `[Unreleased]` already says.
- **Document the URL, don't require it.** Adding a required `${MYSQL_URL}` placeholder to `application.properties`
  would have changed what every existing no-profile deployment must supply — itself a breaking change under the new
  rules. The URL stays external and documented; production gets an opt-in profile instead.
- **Test what Lombok can't be trusted with.** `AGENTS.md` rules out tests of Lombok-generated behaviour, and seven of
  the eight entities are nothing else. `IpscMatch` is the exception: its bidirectional `stages` exclusion is a
  deliberate choice whose loss would crash `toString`/`hashCode`, so it gets a regression test.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (970 tests, 0 failures/errors), up from 966 in v8.9.0.
- `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing.
- 4 new tests in `IpscMatchTest`; removing `IpscMatch.stages`' Lombok exclusions was confirmed to fail three of them
  with `StackOverflowError`.

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- A competitor or match referenced by results or shooter logs can't be deleted through the API, since no endpoint
  removes those rows yet (carried over from v8.8.0, pending Gap #6).
- The `BRANCH` coverage counter is still not separately enforced by the JaCoCo `check` execution — only `LINE` is.
- `pom.xml` still overrides `tomcat.version` to `11.0.25` for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26).

---

## 🔮 Future Enhancements

- Build a `MatchScoreService`/`ShooterLogService` over the existing repositories, committing through
  `TransactionService`, following the same phased pattern that closed Gap #1 (Gap #6).
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint.
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.25` or later (Gap #26).
- Consider enforcing a `BRANCH`-level JaCoCo minimum alongside the existing `LINE` one.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.10.0 changes no endpoint, field or schema. Its weight is in process: version numbers now follow a rule the
release checks, the documented runtime profiles match the configuration, and the improvement plan is down to one
open gap and one waiting on an upstream release.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
