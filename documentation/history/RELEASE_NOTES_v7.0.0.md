# Release Notes – Version 7.0.0

**Release Date:** August 11, 2026
**Status:** ✨ Stable

---

## 🎯 Theme

**Match Results, Visitor Tracking & Shooter Log Data Model**

Version 7.0.0 extends the IPSC domain model to support club-scoped results, match visitors, and a persisted "shooter log" — the best-4-matches ranking used by HPSC. The six entities that had been parked under `domain/old/` (pending a redesign) are promoted back into the live `domain` package, extended with the new fields and relations, and paired with a full set of Spring Data repositories (the `repositories/` package was previously emptied in preparation for this rework). Two new entities, `ShooterLog` and `ShooterLogEntry`, are introduced to persist shooter-log snapshots. No existing enums or converters were needed — `ClubIdentifier` and `FirearmType` (with their existing `AttributeConverter`s) are reused throughout. Alongside the domain-model work, this release also lands Flyway-managed database migrations, a Spring Boot patch bump that closes several Dependabot security alerts, a `ValueUtil` bug fix, CI/tooling clean-up, and a documentation-conventions overhaul.

---

## ⭐ Key Highlights

### 🏛️ Domain Package Promotion

- `domain/old/` (6 files: `Club`, `Competitor`, `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`, `MatchStageCompetitor`) removed entirely.
- Their content is promoted into `za.co.hpsc.web.domain` (package `.old` dropped) and extended per the changes below — this restores the entities to the location the (now-empty) `repositories/` package and `CLAUDE.md`'s architecture table already expected.

### 🏢 Club — Formal Club Identity

- New column **`identifier`** (`ClubIdentifier`, via the existing `ClubIdentifierConverter`, unique) — ties a `Club` row to `HPSC` / `SOSC` / `PMPSC`. Only three rows are expected to exist; visitors are **not** modelled as a fourth club row, they're derived relationally (see `MatchCompetitor` below).

### 🧍 Competitor — Home Club Membership

- New relation **`homeClub`** (`@ManyToOne Club`, nullable `home_club_id`) — "member of club". Nullable so an unaffiliated visitor can be recorded without forcing a home club.

### 🏆 MatchCompetitor — Per-Entry Results, Visitors & Multi-Firearm Entries

One row per competitor **per firearm-type entry** in a match:

- **`overallRanking`** (renamed from `matchRanking`) — rank across all competitors in the match for that firearm type.
- **`clubRanking`** *(new)* — rank among only same-club (`matchClub`) competitors in the match for that firearm type.
- **`isVisitor`** *(new, `Boolean`)* — `true` when `matchClub` differs from the host match's club.
- **`firearmType`** is now non-nullable.
- **New unique constraint** `(competitor_id, match_id, firearm_type)` — a competitor may only have multiple entries in the same match if the firearm type differs.

### 🎯 MatchStageCompetitor — Restructured to Follow the Firearm-Type Entry

- **Replaced** the direct `competitor` FK (plus duplicated `matchClub` / `competitorCategory` / `firearmType` / `division` / `powerFactor` fields) with a single **`matchCompetitor`** FK (`@ManyToOne MatchCompetitor`). A stage score now correctly attaches to the specific firearm-type entry it belongs to — required once a competitor can have more than one `MatchCompetitor` row per match (see above). All category/division/firearm/power-factor/club data is inherited via the relation instead of being duplicated.
- **New unique constraint** `(match_competitor_id, match_stage_id)`.
- All existing per-stage scoring fields (`scoreA`-`scoreD`, `points`, `misses`, `penalties`, `procedurals`, `hasDeduction`, `deductionPercentage`, `time`, `hitFactor`, `stagePoints`, `stagePercentage`, `stageRanking`, `isDisqualified`) are unchanged.

### 📅 IpscMatchStage — Stage Numbering Constraint

- **New unique constraint** `(match_id, stage_number)`.

### 📊 Shooter Log — New Persisted Snapshot

- **`ShooterLog`** *(new entity)* — `competitor` FK, `club` FK (the qualifying club, e.g. HPSC), `firearmType` (existing converter), `logValue` (`BigDecimal(19,6)`, the average of the best 4 match scores), `calculatedDate`.
- **`ShooterLogEntry`** *(new entity)* — links a `ShooterLog` snapshot to the specific `MatchCompetitor` rows that contributed to it, with `rankInLog` (1–4) and a unique constraint `(shooter_log_id, match_competitor_id)`.
- No calculation job/service exists yet — this release only adds the persistence shape; a scheduled or on-demand recalculation service is future work.

### 🗄️ Repositories — Rebuilt From Scratch

The `repositories/` package was empty (repositories for these six entities were deliberately removed in a prior commit pending this redesign). Eight `JpaRepository<Entity, Long>` interfaces were (re)created:

| Repository                       | Finder methods                                                                                                              |
|----------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `ClubRepository`                 | `findByName`, `findByAbbreviation`, `findByIdentifier`                                                                      |
| `CompetitorRepository`           | `findByClubNumber`                                                                                                          |
| `IpscMatchRepository`            | `findAllByClubId`                                                                                                           |
| `IpscMatchStageRepository`       | `findAllByMatchIdOrderByStageNumber`                                                                                        |
| `MatchCompetitorRepository`      | `findAllByCompetitorIdAndMatchId`, `findAllByMatchIdAndFirearmType`, `findAllByCompetitorIdAndFirearmTypeAndIsVisitorFalse` |
| `MatchStageCompetitorRepository` | `findAllByMatchCompetitorId`                                                                                                |
| `ShooterLogRepository`           | `findAllByCompetitorIdAndFirearmType`                                                                                       |
| `ShooterLogEntryRepository`      | `findAllByShooterLogId`                                                                                                     |

`findAllByCompetitorIdAndFirearmTypeAndIsVisitorFalse` on `MatchCompetitorRepository` is the intended basis for the future best-4 shooter-log calculation (results, excluding visitors, per firearm type).

### 🗄️ Flyway Database Migrations

- `flyway-core`, `flyway-mysql`, and the `flyway-maven-plugin` build plugin are now part of the build.
- A single create-only migration, `V7_0_0__create_schema.sql`, builds the full v7.0.0 schema (all 7 tables: `club`, `competitor`, `ipsc_match`, `ipsc_match_stage`, `match_competitor`, `match_stage_competitor`, `shooter_log`, `shooter_log_entry`) with FKs and unique constraints matching the entity changes above. This consolidates an earlier two-step design (a `V1__baseline_v6_0_0_schema.sql` baseline plus a `V7_0_0__evolve_match_domain_for_v7.sql` alter script) into one script for a genuinely empty database.
- `spring.flyway.enabled=true` / `spring.flyway.locations=classpath:db/migration` added to `application.properties`; Flyway is explicitly disabled (`spring.flyway.enabled=false`) in the H2 `test` profile, which continues to rely on Hibernate `ddl-auto=create-drop`.
- New `application-local.properties` profile for local MySQL development (`ddl-auto=update`, no Flyway).

### 🔒 Dependency & Security Updates

- `spring-boot-starter-parent` bumped **4.0.6 → 4.0.7**, closing several Dependabot alerts (3 critical) by picking up patched `tomcat-embed-core` (→ 11.0.22), `spring-framework` (→ 7.0.8), `spring-data-commons`, `logback-core`, and `jackson-core`.
- Explicit `jackson-databind` pin added at **2.21.5**, since the Spring Boot 4.0.7 BOM still manages a version one patch behind the fix.
- `jackson-bom.version` bumped 3.1.1 → 3.1.5 to match.
- `flyway-maven-plugin`'s `baselineVersion` / `baselineDescription` corrected to 7.0.0 (was stale at 6.0.0).

### 🐛 Bug Fix

- **`ValueUtil.nullAsDefaultString`** — previously only fell back to the default value when the input itself was `null`. A non-null input whose `toString()` returns `null` fell through and incorrectly returned `null` instead of the default's string form; now explicitly checked and covered by the existing test suite.

### 🧹 CI & Tooling Clean-up

- Qodana CI workflow (`.github/workflows/code_quality.yml`), `qodana.yaml`, and remaining references in `CLAUDE.md` / `ARCHITECTURE.md` removed. CodeQL remains the sole CI security-analysis gate; JaCoCo remains for coverage.
- Obsolete `.github/prompts/create-unit-tests.prompt.md` removed.

### 📚 Documentation Overhaul

- New **`AGENTS.md`** — tool-agnostic conventions for AI coding agents (tech stack, documentation conventions including British English spelling rules, documentation file map, test conventions, directory-tree maintenance, git workflow).
- README.md gains a documentation map cataloguing every doc file's purpose.
- Pinned dependency/tool version numbers (Spring Boot, Java, Maven, Hibernate, SpringDoc, JaCoCo, JUnit) removed from README.md / ARCHITECTURE.md so `pom.xml` is the single source of truth; ARCHITECTURE.md's domain entity table corrected (was stale at "six" entities, now reflects `ShooterLog` / `ShooterLogEntry` and the `MatchStageCompetitor` → `MatchCompetitor` relationship change).
- CHANGELOG.md / HISTORY.md heading-icon usage standardised.
- Duplicate release-notes history files retired: `documentation/history/RELEASE_NOTES_HISTORY.md` and `documentation/history/RELEASE_NOTES_README.md` — both superseded by the root `HISTORY.md`.

---

## 📦 What's New

### Added

#### Domain

- `za.co.hpsc.web.domain.ShooterLog`
- `za.co.hpsc.web.domain.ShooterLogEntry`
- `Club.identifier` (`ClubIdentifier`)
- `Competitor.homeClub` (`Club`)
- `MatchCompetitor.clubRanking`, `MatchCompetitor.isVisitor`
- `IpscMatchStage` unique constraint `(match_id, stage_number)`
- `MatchCompetitor` unique constraint `(competitor_id, match_id, firearm_type)`
- `MatchStageCompetitor` unique constraint `(match_competitor_id, match_stage_id)`

#### Repositories

- `ClubRepository`, `CompetitorRepository`, `IpscMatchRepository`, `IpscMatchStageRepository`, `MatchCompetitorRepository`, `MatchStageCompetitorRepository`, `ShooterLogRepository`, `ShooterLogEntryRepository`

#### Database & Build

- `flyway-core`, `flyway-mysql` dependencies; `flyway-maven-plugin` build plugin
- `db/migration/V7_0_0__create_schema.sql`
- `application-local.properties` profile

#### Documentation

- `AGENTS.md`

### Changed

#### Domain

- `za.co.hpsc.web.domain.old.*` → promoted to `za.co.hpsc.web.domain.*` (package dropped)
- `MatchCompetitor.matchRanking` renamed to `overallRanking`
- `MatchCompetitor.firearmType` changed to non-nullable
- `MatchStageCompetitor` — FK changed from `competitor` to `matchCompetitor`; duplicated category/division/firearm-type/power-factor/club fields removed (now inherited via the relation)

#### Dependencies

- `spring-boot-starter-parent` 4.0.6 → 4.0.7
- `spring-framework.version` 7.0.7 → 7.0.8; `tomcat.version` → 11.0.22; `jackson-bom.version` 3.1.1 → 3.1.5
- `pom.xml` project version 6.0.0 → 7.0.0; `@OpenAPIDefinition` version updated to match

#### Configuration

- `application.properties` — Flyway enabled, pointed at `classpath:db/migration`
- `application-test.properties` — Flyway explicitly disabled (H2 `test` profile unaffected)
- `application-dev.properties` — `hibernate.show_sql` quietened
- `logback-spring.xml` — `local` Spring profile logging block added

#### Documentation

- README.md, ARCHITECTURE.md, CLAUDE.md, CHANGELOG.md, HISTORY.md

### Fixed

- `ValueUtil.nullAsDefaultString` — no longer returns `null` when a non-null value's `toString()` itself returns `null`.

### Removed

#### Source

- `za.co.hpsc.web.domain.old` package (all 6 files) — superseded by the promoted/extended entities above

#### CI & Tooling

- Qodana CI workflow (`code_quality.yml`) and `qodana.yaml`
- `.github/prompts/create-unit-tests.prompt.md`

#### Documentation

- `documentation/history/RELEASE_NOTES_HISTORY.md`, `documentation/history/RELEASE_NOTES_README.md`

---

## 🔄 Migration Guide

### For Deployers

- **Flyway is now a hard requirement for MySQL profiles.** The shipped migration (`V7_0_0__create_schema.sql`) is **create-only** — it assumes a genuinely empty database. A pre-existing MySQL database not already in the exact v7.0.0 shape will need manual reconciliation (or an appropriate Flyway baseline) before this, migration will apply cleanly; there is no longer a baseline-plus-evolve path for pre-v7.0.0 databases.
- The H2 `test` profile is unaffected — it continues to use Hibernate `ddl-auto=create-drop` with Flyway disabled.

### For Developers

- **`MatchCompetitor.matchRanking` → `overallRanking`** — a persisted field/column rename (`match_ranking` → `overall_ranking`). Update any JPQL, native queries, or external consumers that reference the old name.
- **`MatchStageCompetitor`** no longer has its own `competitor`, `matchClub`, `competitorCategory`, `firearmType`, `division`, or `powerFactor` fields — it now points at `MatchCompetitor` via `matchCompetitor` (column `competitor_id` → `match_competitor_id`), and that data must be read via the relation instead.
- **`MatchCompetitor.firearmType`** is now non-nullable; anything constructing a `MatchCompetitor` without a firearm type will fail at persistence time.
- No service, controller, or import-pipeline code references these classes yet in this release, so there are no call sites to update outside tests exercising the domain layer directly.

---

## 📊 Statistics

- **Total Commits:** 18
- **Files Changed:** 37
- **Insertions:** 1,418 lines
- **Deletions:** 2,815 lines
- **Net Change:** -1,397 lines
- **New Entities:** 2 (`ShooterLog`, `ShooterLogEntry`)
- **New Repositories:** 8

---

## 🧭 Design Notes

- **Visitors are relational, not a 4th club row.** A `MatchCompetitor` entry is a visitor when its `matchClub` differs from the hosting match's `club`. `Club` only ever holds the three real clubs (HPSC, SOSC, PMPSC).
- **Overall vs. club results live on the same row.** Rather than a separate `MatchResult` table, `overallRanking` and `clubRanking` are both columns on `MatchCompetitor`, since a result is always 1:1 with a competitor's firearm-type entry in a match.
- **Per-stage results were already close to correct** — the only structural gap was that stage scores pointed at `Competitor` instead of the specific `MatchCompetitor` entry, which breaks once a competitor can have multiple firearm-type entries in one match (req: multi-entry support). Fixed by repointing the FK.
- **Shooter logs are persisted snapshots, not a live view** — chosen so historical shooter-log values survive even after new matches are scored, at the cost of needing a (not-yet-built) recalculation step.

---

## 🧪 Testing

- `./mvnw clean compile` — all 8 entities and 8 repositories compile cleanly.
- `./mvnw test -Dtest=HpscWebApplicationTests` — Spring context boots against H2 (`ddl-auto=create-drop`); Hibernate successfully builds the schema for all 8 entities, validating every `@JoinColumn`, converter, and unique constraint. **Result:** 1/1 passing.
- No dedicated unit/integration tests were added for the new/changed entities or repositories in this pass — see Known Issues.
- The `ValueUtil.nullAsDefaultString` fix is covered by the existing `ValueUtilTest` suite — the `toString()`-returns-null edge case was already asserted there.

---

## 🐛 Known Issues

- No repository-level or service-level test coverage yet for the new/changed domain model.
- No service, controller, or CSV/XML import wiring for `homeClub`, `clubRanking`, `isVisitor`, `ShooterLog`, or `ShooterLogEntry` — the entities exist but nothing populates them yet.
- No shooter-log calculation job/service exists — `ShooterLog`/`ShooterLogEntry` are schema only.
- The Flyway migration is create-only; deploying against a pre-existing, non-empty MySQL database requires manual reconciliation (see Migration Guide).

---

## 🔮 Future Enhancements

- `ShooterLogService` — compute/persist best-4 `ShooterLog` snapshots per competitor/club/firearm type, excluding visitors.
- Populate `MatchCompetitor.overallRanking` / `clubRanking` / `isVisitor` during match-result import.
- Populate `Competitor.homeClub` and `Club.identifier` seed data (HPSC, SOSC, PMPSC).
- Entity, repository, and integration test coverage for all changes in this release.
- Controller/service endpoints exposing overall, per-club, per-stage results and shooter logs.

---

## 👥 Contributors

Development Team

---

## 📝 Notes

This release combines domain-layer groundwork with release-hygiene work, produced across a single branch: promoting `domain/old/` back into `domain/`, extending it to cover results-per-club, visitor tracking, multi-firearm-type match entries, and shooter logs, and rebuilding the `repositories/` package from scratch. It intentionally stops at the persistence boundary — no service, controller, or import-pipeline changes are included — so that the schema design could be validated (via a full Spring context boot against H2, and via Flyway against MySQL) before building the business logic on top of it. Alongside the domain work, the release also consolidates CI and dependency hygiene (the Spring Boot patch bump closing Dependabot alerts, Qodana removal) and a documentation-conventions pass (`AGENTS.md`), so the branch leaves the project on a clean footing before further v7.x feature work begins.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
