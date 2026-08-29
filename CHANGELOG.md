# Changelog

All notable changes to the HPSC Website Backend project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) as of version 5.0.0.

---

## Table of Contents

- [🧪 Unreleased](#-unreleased)
- [🧾 Version 7.3.0](#-730---2026-08-25) ← Current
- [🧾 Version 7.2.0](#-720---2026-08-25)
- [🧾 Version 7.1.0](#-710---2026-08-24)
- [🧾 Version 7.0.0](#-700---2026-08-11)
- [🧾 Version 6.0.0](#-600---2026-05-01)
- [🧾 Version 5.4.0](#-540---2026-04-26)
- [🧾 Version 5.3.0](#-530---2026-03-15)
- [🧾 Version 5.2.0](#-520---2026-02-27)
- [🧾 Version 5.1.0](#-510---2026-02-25)
- [🧾 Version 5.0.0](#-500---2026-02-24)
- [🧾 Version 4.1.0](#-410---2026-02-13)
- [🧾 Version 4.0.0](#-400---2026-02-11)
- [🧾 Version 3.1.0](#-310---2026-02-10)
- [🧾 Version 3.0.0](#-300---2026-02-10)
- [🧾 Version 2.0.0](#-200---2026-02-08)
- [🧾 Version 1.1.3](#-113---2026-01-28)
- [🧾 Version 1.1.2](#-112---2026-01-20)
- [🧾 Version 1.1.1](#-111---2026-01-16)
- [🧾 Version 1.1.0](#-110---2026-01-14)
- [🧾 Version 1.0.0](#-100---2026-01-04)
- [📋 Version Policy](#-version-policy)
- [🚀 Upgrade Guide](#-upgrade-guide)
- [🤝 Contributing](#-contributing)
- [💬 Support](#-support)

---

## 🧪 [Unreleased]

### ➕ Added

#### Documentation

- **`documentation/roadmap/IMPROVEMENT_PLAN.md`:** New document synthesising the goals and constraints stated across this repository's documentation and configuration into a prioritised set of gaps and a roadmap
- **`documentation/roadmap/TASKS.md`:** New concrete, checkbox-level task list broken out from `documentation/roadmap/IMPROVEMENT_PLAN.md`'s five gaps, organised by its Now/Next/Later/Ongoing phasing
- **`README.md` / `AGENTS.md`:** Both now list `documentation/roadmap/`'s files in their own dedicated Roadmap section, separate from the standard documentation file map/table

#### Models

- **`za.co.hpsc.web.models.ipsc.request`:** New request DTOs for the IPSC module rebuild — `MatchRequest`/`MatchStageRequest`/`MatchStagesRequest` for match/stage submission, and `MatchOverallResultRequest`/`MatchStageResultRequest` (plus `MatchOverallResultRequestForCSV`/`MatchStageResultRequestForCSV` abstract CSV variants) for competitor result submission — all now carry field- and class-level Javadoc mirroring `IpscCommonScore`'s Comstock-scoring documentation below, and `MatchRequest` gains a `matchId` field for updating an existing match (previously creation-only)
- **`IpscCommonScore`:** New base DTO for fields shared by Comstock-scored (hit-factor) IPSC results — percentage, weighted points, time, power factor, alpha/charlie/delta hit counts, and penalty counts — with Javadoc documenting how the Comstock scoring method works
- **`IpscMatchScore`:** New DTO extending `IpscCommonScore` with `percentageOfPossiblePoints`, the match-level accuracy total independent of time
- **`IpscMatchStageScore`:** New DTO extending `IpscCommonScore` with `rawPoints` and `hitFactor` (`rawPoints / time`), the figure a single Comstock stage is ranked on

### 🔄 Changed

#### Configuration

- **`.gitignore`:** Refreshed the JetBrains, Visual Studio Code, Eclipse, and Node sections from the latest upstream templates — adds entries for SonarLint, Apifox Helper, GitHub Copilot, stylelint, pnpm, yarn v3, Vite, Sveltekit, vitepress, and Docusaurus, fixes the `.apt_generated_test/` → `.apt_generated_tests/` typo and the stale "Editor-based Rest Client" comment, and adds new OS, Version Control, and Secrets & Credentials sections; the custom TAHONI block now also ignores `tsdocs/` and `logs/`, and generalises `.claude/*.local.json` to `.claude/*.local.*` (supersedes the `.claude/*.local.json` entry above)
- **`.gitignore`:** Uncommented the `.project` ignore rule, so IntelliJ/Eclipse project description files are now excluded from version control going forward
- **`.aiignore`:** Re-synced with `.gitignore`'s refreshed template sections; its entries stay plain excludes rather than mirroring `.gitignore`'s `!` allowlist patterns (e.g. `.vscode/settings.json`, `.env.example`, `.yarn/patches`), so AI tooling stays conservative even for files git tracks

#### Documentation

- **`RELEASE_NOTES.md` Contributors:** Now sourced from `git log`'s unique commit authors on the release branch (bots included) instead of the generic "Development Team" placeholder, per a new rule in AGENTS.md's Release Checklist; the archived `documentation/history/RELEASE_NOTES_v7.3.0.md` snapshot updated to match, keeping it byte-for-byte identical per AGENTS.md's archiving rule
- **`ARCHITECTURE.md`:** Directory structure tree now lists the new `documentation/roadmap/` folder alongside `archive/` and `history/`

#### Tooling

- **`/generate-pr-description`:** Step 6's `RELEASE_NOTES.md` instructions updated to match AGENTS.md's new Contributors-sourcing rule above
- **`/sync-unreleased-changes`:** New Claude Code command — diffs the current branch against its base (`develop`/`main`) plus any uncommitted changes, cross-checks the result against `CHANGELOG.md`'s `[Unreleased]` section, and fills in any missing entries directly in the file

### 🐛 Fixed

#### Documentation

- **`README.md`:** Restored the missing `#` on the H1 heading, lost in an earlier commit that inverted the intended fix — it was rendering as plain text instead of the page title

### ⚠️ Deprecated

### 🗑️ Removed

#### Configuration

- **`.aiignore`:** Removed the dedicated `.claude/`/`.github/` AI-only exclusion block — those directories, including `.claude/commands`, are no longer hidden from AI context

### 🔐 Security

- **`log4j-api`:** Overridden `2.25.4` → `2.25.5` via a new `pom.xml` `dependencyManagement` pin, closing [CVE-2026-49844](https://nvd.nist.gov/vuln/detail/CVE-2026-49844) (GHSA-qv9r-c865-cp47) — a transitive dependency pulled in via `spring-boot-starter-logging` → `log4j-to-slf4j`; this project uses Logback, not Log4j2's `JsonTemplateLayout`, so the vulnerable code path was never actually reachable, but the pin removes the flagged advisory

---

## 🧾 [7.3.0] - 2026-08-25

### ➕ Added

#### Tooling

- **`/generate-pr-summary`:** New Claude Code command — condenses a version's `PR_DESCRIPTION_vX.Y.Z.md` and `RELEASE_NOTES_vX.Y.Z.md` into a short, plain, Bitbucket-style PR summary (a short paragraph plus a capped bullet list), for pasting into a PR description without this repo's own emoji-heavy documentation style

### 🔄 Changed

#### Tooling

- **`/generate-pr-summary`:** Output instructions clarified to require the fenced block contain raw, unrendered Markdown source (literal `##`/`**`/`-`) rather than Claude's own rendered formatting, so pasted PR summaries preserve exact syntax

### 🐛 Fixed

#### Documentation

- **`README.md`:** Introduction and Features sections no longer describe match management, competitor/club CRUD, WinMSS import, or XML/multi-format processing as existing capabilities — only `AwardController`/`ImageController` CSV processing is implemented today; the match/competitor domain's service and controller layer is still being rebuilt
- **`README.md`:** Coverage-report command corrected from `./mvnw test jacoco:report` (non-functional — JaCoCo is only bound via the `coverage` Maven profile) to `./mvnw verify -Pcoverage`
- **`README.md`:** Removed the `1.x – 4.x` version range from the `ARCHIVE.md` description, per AGENTS.md's rule that `README.md`/`ARCHITECTURE.md` must never reference specific version numbers or ranges
- **`ARCHITECTURE.md`:** Test package tree corrected — removed the nonexistent `domain/` test package and added the missing `converters/`/`exceptions/` packages
- **`ARCHITECTURE.md`:** CI/CD & Quality Gates table's `Build & Tests` row no longer claims an "All PRs" GitHub Actions trigger — only `codeql.yml` exists; reworded to reflect it's run locally/by reviewers

### ⚠️ Deprecated

### 🗑️ Removed

### 🔐 Security

---

## 🧾 [7.2.0] - 2026-08-25

### ➕ Added

#### Documentation

- **`CLAUDE.md`:** New Git Workflow section stating the branching model's PR targets directly (`feature/*` → `develop`; `release/vX.Y.Z`/`hotfix/*` → `main`) and the develop-first-for-testing rule, rather than deferring entirely to `AGENTS.md`

#### Testing

- **`services/AwardServiceTest`, `services/ImageServiceTest`:** New Mockito-based unit tests for the `AwardService`/`ImageService` interface contract (`processCsv`), exercised through the interface type rather than the impl class
- **`ControllerResponseTest`:** Covers the previously-untested `ControllerResponse(boolean, String)` constructor (message/error swap based on `success`), and the `(LocalDateTime, String, String)` constructor's derived-`success`-from-error-presence branch (non-null/non-blank error, and blank-but-non-null error)
- **`FirearmTypeTest`:** Covers `toString()` for both the single-name and multi-name enum constructors (previously untested, despite the sibling `ClubIdentifier` enum having equivalent `toString()` tests)
- **`ControllerAdviceTest`:** Covers `logError`'s three previously untested branches — a `null` throwable, a throwable with a wrapped cause, and a `null` `WebRequest`; JaCoCo branch coverage for this class went from 92% to 100%

#### Tooling

- **`/scaffold-unit-tests`:** New Claude Code command, migrated from `.github/prompts/scaffold-unit-tests.prompt.md` — corrects the stale `za.co.signio.apexservices` package reference and the abstract "Layer 1/2/3" interface-test pattern to match this repo's actual conventions (interface contract tests named `[Class]Test`, exercised via the interface type in `services/`, impl-only helper tests in `services/impl/`, no Lombok-only tests), per the `AwardServiceTest`/`ImageServiceTest` split above. Defers to its loaded `@AGENTS.md`/`@CLAUDE.md` for testing-pattern specifics (JUnit Assertions, method naming, exception hierarchy) rather than restating them inline, so it can't drift out of sync with the source. Never commits — it scaffolds and verifies (`./mvnw test`) only, leaving the result for the user to review and commit.
- **`/scaffold-integration-tests`:** New Claude Code command, copied from `/scaffold-unit-tests` and adapted for `@SpringBootTest`-based service integration tests — follows the pattern in `AwardServiceIntegrationTest`/`ImageServiceIntegrationTest`: named `[Class]IntegrationTest`, `@ActiveProfiles("test")` is mandatory (see CLAUDE.md's Database Profiles table, never `dev`/prod), `@EnableAutoConfiguration` excludes `DataSourceAutoConfiguration`/`HibernateJpaAutoConfiguration`/`RabbitAutoConfiguration` to keep the context lightweight, a real `@Autowired` Spring-wired bean rather than Mockito, and only the target's public interface methods may be called — never an impl class's protected/private helpers, which stay the paired unit test's job. Same defer-to-loaded-docs treatment as `/scaffold-unit-tests`; also never commits.

### 🔄 Changed

#### Testing

- **`AwardServiceImplTest`, `ImageServiceImplTest`:** Stale "TODO: sync" comments replaced with Javadoc cross-referencing the new interface-level tests; these two files were already in sync (14 parallel test cases each) for the impl-only `readAwards`/`mapAwards` and `readImages`/`mapImages` methods
- **`AwardServiceIntegrationTest`, `ImageServiceIntegrationTest`:** Now exclude `DataSourceAutoConfiguration`/`HibernateJpaAutoConfiguration`/`RabbitAutoConfiguration` via `@EnableAutoConfiguration` to keep the Spring context lightweight, since neither service touches the datasource, JPA, or messaging; also replaces their stale "TODO: sync" comments with Javadoc (they were already in sync)
- **`HpscWebApplicationTests` renamed to `HpscWebApplicationTest`:** Matches AGENTS.md's `<ClassName>Test` naming convention (was the Spring Initializr default plural name); its `contextLoads()` method renamed to `testContextLoads_whenSpringContextStarted_thenLoadsSuccessfully` to match the method-naming convention too
- **26 test files:** Retrofitted with AGENTS.md's method-comment/ordering rule (`// methodName()` headers; constructors first, public before protected, alphabetical by name, overloads by parameter count then type, `toString()` last) — `ClubIdentifierConverterTest`, `MatchCategoryConverterTest`, `ClubIdentifierTest`, `DivisionTest`, `PowerFactorTest`, `FatalExceptionTest`, `NonFatalExceptionTest`, `ValidationExceptionTest`, `ControllerResponseTest`, `RequestTest`, `ResponseTest`, `AwardRequestForCSVTest`, `AwardCeremonyResponseTest`, `AwardResponseTest`, `ImageRequestForCSVTest`, `ImageResponseTest`, `AwardServiceIntegrationTest`, `AwardServiceTest`, `ImageServiceIntegrationTest`, `ImageServiceTest`, `AwardServiceImplTest`, `ImageServiceImplTest`, `DateUtilTest`, `NumberUtilTest`, `StringUtilTest`, `ValueUtilTest`. No test bodies, assertions, or names changed — only comments and whole-method reordering; `ValueUtilTest` in particular had its `nullAsEmptyString` tests consolidated from 9 scattered locations into one contiguous group. Verified via `./mvnw test`: same 492 tests, all passing, before and after

#### Build & Metadata

- Project version bumped to **7.2.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match
- **`pom.xml`:** Spring Boot parent bumped `4.0.7` → `4.1.0`. As part of this:
  - Removed the `spring-framework.version`/`tomcat.version` property overrides — both now match Boot 4.1.0's own defaults (`7.0.8`/`11.0.22`) exactly, so they were dead weight
  - Removed the `commons.lang3.version` property — a pre-existing typo (Boot's real property is `commons-lang3.version`, hyphenated) meant this override never actually took effect; Boot 4.1.0 bumps the real one for free (`3.19.0` → `3.20.0`)
  - Removed the `maven-dependency-plugin` version override (`3.6.1`) — Boot 4.1.0 now manages this plugin itself, at `3.10.0`
  - Kept the `jackson-databind` (`2.21.5`) and `jackson-bom` (`3.1.5`) overrides unchanged — Boot 4.1.0's own managed versions (`2.21.4`/`3.1.4`) are still one patch behind
  - Bumped the flyway-maven-plugin's separately-pinned `flyway-mysql` dependency `11.14.1` → `12.4.0`, matching the `flyway.version` Boot 4.1.0 now manages (plugin-scoped dependencies don't inherit Boot's dependencyManagement, so this needs manual sync on every parent bump — now documented inline)
  - Verified: full test suite (492 tests), `./mvnw verify -Pcoverage` (including the repackage step), and `./mvnw flyway:info` against a real local MySQL 9.5 dev database all pass clean

#### Documentation

- **`AGENTS.md`:** Evergreen Documentation rule broadened to prohibit version *ranges* (e.g. `1.x – 4.x`), not just exact version numbers, in `README.md`/`ARCHITECTURE.md`
- **`AGENTS.md`:** Icon registry extended with `🔍` (Current state / inspection) and `📤` (Output)
- **`AGENTS.md`, `CONTRIBUTING.md`:** Branching Model's develop-first rule clarified to note it's "for testing before they ship"
- **`AGENTS.md`:** Test Conventions gains a grouping/ordering rule — each method's tests get a one-line `// methodName()` comment; groups are ordered constructors first, then public before protected, then alphabetically by method name within each visibility (overloads by parameter count then type), `toString()` last regardless of visibility

#### Tooling

- **`.claude/commands/generate-commit-message.md`, `generate-pr-description.md`:** Section headings now carry standard icons (`🔍 Current state`, `🚀 Instructions`, `📤 Output`) per AGENTS.md's heading convention
- **`.claude/commands/generate-pr-description.md`:** Closing instructions now remind the user to tag the merged release commit on `main` and merge `main` back into `develop` afterwards
- **`/scaffold-unit-tests`:** Now accepts multiple space- or comma-separated class names/paths in a single invocation, scaffolding each target independently so one unresolved target doesn't block the rest
- **`.claude/commands/generate-commit-message.md`, `generate-pr-description.md`:** Now also load `@CLAUDE.md` (previously `@AGENTS.md` only), for accurate technical detail — build/test commands, package layout, database profiles — when describing changes

### 🐛 Fixed

#### Documentation

- **`CLAUDE.md`:** Now cross-links to `AGENTS.md` for tool-agnostic conventions (git workflow, release checklist, documentation conventions, todo-list tracking) — previously the only project doc missing this reference
- **`CLAUDE.md`:** Package overview table corrected — `ControllerAdvice` lives in `configs/`, not `exceptions/`; adds the missing `configs/` row
- **`AGENTS.md`, `CLAUDE.md`, `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md`:** Removed the false claim that AssertJ is used for assertions — `assertj-core` is explicitly excluded from `spring-boot-starter-webmvc-test` in `pom.xml`, and every test in the suite uses JUnit Jupiter's `Assertions` instead

### ⚠️ Deprecated

### 🗑️ Removed

#### Testing

- **`ControllerResponseTest.testDefaultConstructor_whenInstantiated_thenUsesFieldDefaults`:** Removed — solely exercised the Lombok-generated `@NoArgsConstructor` and generated getters with no accompanying logic, per AGENTS.md's Test Conventions
- **`services/impl/AwardServiceTest`, `services/impl/ImageServiceTest`:** Removed — their thin `processCsv` coverage, tested directly against the impl class, is superseded by the new interface-level `services/AwardServiceTest`/`services/ImageServiceTest`

### 🔐 Security

---

## 🧾 [7.1.0] - 2026-08-24

### ➕ Added

#### Domain

- **`ShooterLog.powerFactor`:** New `PowerFactor` column (via the existing `PowerFactorConverter`, not nullable) — snapshots are now scoped by power factor as well as firearm type
- **`ShooterLogCompetitor.points`:** New nullable column — records the points each contributing `MatchCompetitor` row contributed to the snapshot's `logValue`
- **`ShooterLogCompetitor.match`:** New `@ManyToOne IpscMatch` relation (`match_id`, not nullable) — direct match reference alongside the existing `matchCompetitor` link

#### Repositories

- **`ShooterLogCompetitorRepository`:** New repository — `findAllByShooterLogId(Long)`

#### Database

- **`V7_1_0__update_shooter_log_schema.sql`:** New Flyway migration — renames `shooter_log_entry` → `shooter_log_competitor`, adds `shooter_log.power_factor`, `shooter_log_competitor.points` and `shooter_log_competitor.match_id`

#### Build & Metadata

- Project version bumped to **7.1.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

### 🔄 Changed

#### Domain

- **`ShooterLogEntry` renamed to `ShooterLogCompetitor`** (table `shooter_log_entry` → `shooter_log_competitor`) — entity gains the `points` and `match` fields above

#### Repositories

- **`ShooterLogRepository.findAllByCompetitorIdAndFirearmType`** renamed to **`findAllByCompetitorIdAndFirearmTypeAndPowerFactor`** — now filters by `PowerFactor` as well

### 🗑️ Removed

#### Domain

- **`ShooterLogEntry`** — superseded by `ShooterLogCompetitor` (see Changed above)

#### Repositories

- **`ShooterLogEntryRepository`** — superseded by `ShooterLogCompetitorRepository`

---

## 🧾 [7.0.0] - 2026-08-11

### ➕ Added

#### Domain

- **`ShooterLog`:** New entity — persisted best-4-match shooter-log snapshot (`competitor`, `club`, `firearmType`, `logValue`, `calculatedDate`)
- **`ShooterLogEntry`:** New entity — links a `ShooterLog` snapshot to the `MatchCompetitor` rows that contributed to it (`rankInLog`, unique constraint `(shooter_log_id, match_competitor_id)`)
- **`Club.identifier`:** New column (`ClubIdentifier`, via the existing `ClubIdentifierConverter`, unique) — ties a `Club` row to `HPSC` / `SOSC` / `PMPSC`
- **`Competitor.homeClub`:** New nullable `@ManyToOne Club` relation for home-club membership
- **`MatchCompetitor.clubRanking`:** New column — rank among same-club competitors for a firearm type
- **`MatchCompetitor.isVisitor`:** New `Boolean` column — `true` when `matchClub` differs from the host match's club
- **`IpscMatchStage`:** New unique constraint `(match_id, stage_number)`
- **`MatchCompetitor`:** New unique constraint `(competitor_id, match_id, firearm_type)`
- **`MatchStageCompetitor`:** New unique constraint `(match_competitor_id, match_stage_id)`

#### Repositories

- **`ClubRepository`, `CompetitorRepository`, `IpscMatchRepository`, `IpscMatchStageRepository`, `MatchCompetitorRepository`, `MatchStageCompetitorRepository`, `ShooterLogRepository`, `ShooterLogEntryRepository`:** `repositories/` package rebuilt from scratch (previously emptied in preparation for this redesign)

#### Build & Metadata

- Project version bumped to **7.0.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

### 🔄 Changed

#### Domain

- `za.co.hpsc.web.domain.old.*` promoted to `za.co.hpsc.web.domain.*` (the `.old` package is dropped)
- **`MatchCompetitor.matchRanking`** renamed to **`overallRanking`**
- **`MatchStageCompetitor`:** FK changed from `competitor` to `matchCompetitor`; duplicated `competitorCategory` / `division` / `firearmType` / `powerFactor` / `matchClub` fields removed — now inherited via the `matchCompetitor` relation

### 🗑️ Removed

- `za.co.hpsc.web.domain.old` package (all 6 files) — superseded by the promoted/extended entities above

---

## 🧾 [6.0.0] - 2026-05-01

### ➕ Added

#### Controllers

- **`IpscMatchController`:** New dedicated match CRUD controller mapped to `/v2/ipsc/matches`
    - `POST /v2/ipsc/matches` — create a new IPSC match
    - `PUT /v2/ipsc/matches/{matchId}` — fully replace an existing match
    - `PATCH /v2/ipsc/matches/{matchId}` — partially update an existing match
    - `GET /v2/ipsc/matches/{matchId}` — retrieve a match by ID
    - Full OpenAPI/Swagger annotations; returns `ResponseEntity<MatchOnlyResponse>`
- **`IpscMemberController`:** Stub controller at `/ipsc/member` (placeholder for member management)

#### Services

- **`IpscMatchService` interface:** Match CRUD contract — `insertMatch`, `updateMatch`, `modifyMatch`, `getMatch`; all return `Optional<MatchOnlyResponse>`
- **`IpscMatchServiceImpl`:** Full implementation (135 lines)
- **`ClubEntityService.findClubById(Long)`:** New entity service method; implemented in `ClubEntityServiceImpl`
- **`CompetitorEntityService.findCompetitorById(Long)`:** New entity service method; implemented in `CompetitorEntityServiceImpl`
- **`MatchStageCompetitorEntityService.findMatchStageCompetitorById(Long)`:** New entity service method; implemented in `MatchStageCompetitorEntityServiceImpl`
- **`TransformationService.mapMatchOnly(MatchOnlyRequest)`:** New method for the match CRUD pipeline

#### Models — `models/ipsc/match/`

- **`MatchOnlyDto`:** Lightweight match DTO (no stages); initialised from `MatchOnlyRequest` with automatic `FirearmType` resolution and `dateEdited` stamping
- **`MatchOnlyRequest`:** JSON request body for match create / update operations
- **`MatchOnlyResponse`:** Response envelope returned by `IpscMatchController`
- **`MatchOnlyResultsDto`:** Internal results holder passed through the service chain

#### Models — `models/ipsc/common/request/`

- **`MatchSearchRequest`:** Multi-criteria search — match IDs array, name, date range
- **`MatchSearchDateRequest`:** Date-range search — `startDate`, `endDate`, `matchName`
- **`MatchSearchIdRequest`:** ID-array-based lookup

#### Utilities

- **`IpscUtil`:** New utility class (66 lines) for club and match display-string formatting
    - `clubTostring(name, abbreviation)`
    - `matchToString(name, clubName, abbreviation)`
    - `matchToString(name, ClubDto)` — convenience overload

#### Test Coverage

- **`IpscMatchControllerTest`** (49 lines) — controller unit tests
- **`IpscMatchServiceTest`** (269 lines) — service unit tests
- **`IpscMatchIntegrationTest`** (237 lines) — end-to-end match persistence via H2
- **`MatchOnlyDtoTest`** (202 lines) — DTO initialisation and field mapping
- **`MatchOnlyRequestTest`** (234 lines) — request constructor and field coverage
- **`MatchOnlyResponseTest`** (165 lines) — response constructor and field coverage
- **`MatchResponseTest`** (46 lines) — common `MatchResponse` model
- **`IpscUtilTest`** (114 lines) — string formatting edge cases

#### Build & Metadata

- **MIT Licence** declared in `pom.xml` (`<license>`)
- **Developer profile** populated in `pom.xml` (`tahoni / Leoni Lubbinge`)
- **SCM connection and URL** filled in `pom.xml` for GitHub

### 🔄 Changed

#### Controllers

- **`IpscController`:** Match-related endpoints extracted to `IpscMatchController` (78 lines removed)

#### Services

- **`DomainServiceImpl`:** Replaced direct JPA repository injection with entity service injection (`ClubEntityService`, `CompetitorEntityService`, `MatchEntityService`, `MatchStageEntityService`, `MatchCompetitorEntityService`, `MatchStageCompetitorEntityService`)
- **`TransformationService.mapMatchResults`:** Removed `throws ValidationException` from signature
- **`TransformationServiceImpl`:** All imports updated to `models/ipsc/common/*`; `mapMatchOnly` method added

#### Config & Infrastructure

- **`ControllerAdvice`:** Structured logging added to all exception handlers; `ValidationException` removed from handler method signatures (119 lines changed)
- **`pom.xml`:** Spring Boot BOM upgraded `4.0.5` → `4.0.6`; Lombok exclusion plugin block reorganised
- **`logback-spring.xml`:** Additional appender/logger configuration added

#### Package Paths — All IPSC Models

All `models/ipsc/` classes moved to `models/ipsc/common/`:

| Old path                 | New path                        |
|--------------------------|---------------------------------|
| `models/ipsc/data/`      | `models/ipsc/common/data/`      |
| `models/ipsc/divisions/` | `models/ipsc/common/divisions/` |
| `models/ipsc/dto/`       | `models/ipsc/common/dto/`       |
| `models/ipsc/holders/`   | `models/ipsc/common/holders/`   |
| `models/ipsc/records/`   | `models/ipsc/common/records/`   |
| `models/ipsc/request/`   | `models/ipsc/common/request/`   |
| `models/ipsc/response/`  | `models/ipsc/common/response/`  |

#### Updated Tests

- **`TransformationServiceTest`** — +747 lines covering `mapMatchOnly` and updated signatures
- **`DomainServiceTest`** — +247 lines covering entity-service delegation
- **`TransactionServiceTest`** — +246 lines covering `findMatchStageCompetitorById` path
- **`ValueUtilTest`** — +294 lines covering null-handling improvements
- **`IpscServiceIntegrationTest`** — +99 lines for expanded integration scenarios
- Domain entity and DTO tests updated for `common` package import paths

### 🗑️ Removed

#### Controllers & Endpoints

- Match CRUD endpoints removed from `IpscController` (moved to `IpscMatchController`)

#### Models

- **`models/ipsc/response/ClubResponse`** — superseded by `models/ipsc/common/response/ClubResponse`
- **`models/ipsc/response/MatchResponse`** — superseded by `models/ipsc/common/response/MatchResponse`

#### Service Internals

- Direct repository injection from `DomainServiceImpl` (replaced by entity services)

#### Tests

- **`IpscControllerTest`** (156 lines) — replaced by `IpscMatchControllerTest`

---

## 🧾 [5.4.0] - 2026-04-26

### ➕ Added

#### Competitor Enrolment

- **EnrolledCompetitorDto:** New DTO (138 lines) tracking enrolled competitors through the IPSC pipeline
    - Complete field set for competitor enrolment state with comprehensive Javadoc
- **MatchHolder:** New data class (23 lines) for match data encapsulation in service calls
- **IpscMatchRecordHolder:** New holder class in the holders' package (10 lines)
- **CompetitorResultRecord:** New record for competitor result data (13 lines)
- **MatchCompetitorOverallResultsRecord:** New record for overall match results (9 lines)
- **MatchCompetitorStageResultRecord:** New record for stage-level result data

#### Service Architecture

- **TransformationService interface:** Replaces `IpscMatchService`; semantically describes the data-transformation role
- **TransformationServiceImpl:** 1,098 lines replacing `IpscMatchServiceImpl`

#### Enumerations

- **ClubIdentifier:** Abbreviation field added (38 lines changed); each identifier now carries a dedicated abbreviation

#### Constants

- **HpscConstants:** 3 lines updated
- **MatchConstants:** 3 lines updated
- **SystemConstants:** 3 lines updated

#### Test Coverage (20+ new test classes, ~7,000 lines)

- **Controller tests:** `AwardControllerTest` (163), `ImageControllerTest` (163), `IpscControllerTest` (156), `ControllerAdviceTest` (299)
- **Converter tests:** `ClubIdentifierConverterTest` (76), `CompetitorCategoryConverterTest` (85), `DivisionConverterTest` (85), `FirearmTypeConverterTest` (103), `MatchCategoryConverterTest` (76), `PowerFactorConverterTest` (85)
- **Domain entity tests:** `ClubTest` (467), `CompetitorTest` (354), `IpscMatchTest` (367), `IpscMatchStageTest` (333), `MatchCompetitorTest` (364), `MatchStageCompetitorTest` (645)
- **Exception tests:** `FatalExceptionTest` (161), `NonFatalExceptionTest` (173), `ValidationExceptionTest` (115)
- **Model & utility tests:** `ControllerResponseTest` (89), `RequestTest` (100), `AwardRequestForCSVTest` (394), `ImageRequestForCSVTest` (244), `EnrolledCompetitorDtoTest` (196), `FirearmTypeToDivisionsTest` (40), `ValueUtilTest` (100)
- **Integration tests:** `AwardServiceIntegrationTest` (295), `ImageServiceIntegrationTest` (348), `DtoToEntityMappingIntegrationTest` (71)
- **Service tests:** `TransformationServiceTest` (1,026), `MatchCompetitorDtoTest` (253)

#### CI/CD & Configuration

- **`.aiignore`:** New file for AI assistant context management
- **Qodana JVM linter:** `qodana.yaml` configured with `jetbrains/qodana-jvm` for static analysis
- **JaCoCo 0.8.14:** Coverage profile added to `pom.xml`; reports output to `/coverage` directory
- **Branch patterns:** Extended in `code_quality.yml` (feature, bugfix, hotfix)

### 🔄 Changed

#### Core Services

- **TransformationServiceImpl:** 1,098 lines introduced (replaces `IpscMatchServiceImpl`)
- **DomainServiceImpl:** 139 lines changed – enhanced competitor and match handling
- **TransactionServiceImpl:** 87 lines changed – list-based operation updates
- **IpscServiceImpl:** 34 lines changed
- **MatchCompetitorEntityServiceImpl:** 9 lines changed – returns lists
- **MatchStageCompetitorEntityServiceImpl:** 9 lines changed – enhanced retrieval
- **CompetitorEntityServiceImpl:** 7 lines changed
- **ImageServiceImpl:** 2 lines changed

#### Service Interfaces

- **TransformationService:** 27 lines (new interface replacing `IpscMatchService`)
- **DomainService:** 37 lines changed
- **MatchCompetitorEntityService:** 24 lines changed – returns lists
- **MatchStageCompetitorEntityService:** 25 lines changed
- **TransactionService:** 8 lines changed
- **IpscService:** 2 lines changed

#### Domain Entities

- **Club:** 15 lines changed
- **Competitor:** 17 lines changed
- **IpscMatch:** 13 lines changed
- **IpscMatchStage:** 12 lines changed
- **MatchCompetitor:** 16 lines changed
- **MatchStageCompetitor:** 18 lines changed

#### DTOs & Models

- **CompetitorDto:** 13 lines changed – SAPSA deduplication and max number validation
- **MatchCompetitorDto:** 22 lines changed
- **AwardRequestForCSV:** 40 lines changed – updated constructors and JSON handling
- **ImageRequestForCsv:** 12 lines changed – updated constructors
- **ClubDto:** 3 lines changed
- **MatchStageCompetitorDto:** 5 lines changed
- **MatchDto:** 2 lines changed
- **AwardRequest:** 7 lines changed

#### Converters

- **ClubIdentifierConverter:** 4 lines changed – uses abbreviation for database persistence

#### Controllers & Config

- **ControllerAdvice:** 35 lines changed – improved error handling
- **IpscController:** 6 lines changed
- **AwardController:** 2 lines changed
- **ImageController:** 2 lines changed

#### Package Moves

- `DtoMapping`, `DtoToEntityMapping`, `EntityMapping`: `ipsc/domain` → `ipsc/data`
- `MatchResultsDto`, `MatchResultsDtoHolder`: moved to `ipsc/holders/dto`
- `IpscRequestHolder`, `IpscResponseHolder`: moved to `ipsc/holders` sub-packages

#### Repository Layer

- **MatchCompetitorRepository:** 4 lines changed
- **MatchStageCompetitorRepository:** 4 lines changed

#### Test Suites (Updated)

- **DomainServiceTest:** 1,428 lines changed – enhanced coverage
- **IpscServiceIntegrationTest:** 649 lines changed – expanded integration scenarios
- **TransactionServiceTest:** 1,736 lines changed – comprehensive updates
- **IpscServiceTest:** 737 lines changed
- **CompetitorDtoTest:** 119 lines changed – SAPSA validation coverage
- **DtoToEntityMappingTest:** 157 lines changed – package move updates
- **ClubIdentifierTest:** 220 lines changed – abbreviation coverage

#### Build & Configuration

- **pom.xml:** 85 lines changed – JaCoCo 0.8.14, Spring Framework 7.0.7 (stabilised from 7.0.8)
- **qodana.yaml:** Configured with `jetbrains/qodana-jvm` linter
- **code_quality.yml:** 34 lines changed – branch patterns extended, a dependency install step added
- **codeql.yml:** 4 lines changed
- **HpscWebApplication:** Application version bumped to 5.4.0

### 🐛 Fixed

#### Constants & Division

- **PCC Optics division constant:** Fixed incorrect value in `IpscConstants`
- **Division constants:** Updated competitor number and ICS alias values in `IpscConstants`
- **Division enum:** 2 lines changed (PCC Optics fix)

#### Converter

- **ClubIdentifierConverter:** Fixed to correctly use abbreviation for database persistence

#### Error Handling

- **ControllerAdvice:** Fixed exception handler methods for improved error response management

#### Match Processing

- Removed unused firearm type assignment from the match processing path

### ⚠️ Deprecated

None.

### 🗑️ Removed

#### Services & Implementations

- **`IpscMatchService` interface:** Replaced by `TransformationService`
- **`IpscMatchServiceImpl` class:** 867 lines replaced by `TransformationServiceImpl`

#### Records

- **`MatchCompetitorRecord`:** Replaced by `CompetitorRecord`
- **`IpscMatchRecordHolder`** (from the records' package): Moved to holders

#### Configuration

- **`qodana.yml`:** Duplicate removed; configuration consolidated in `qodana.yaml`

#### Test Classes

- **`IpscMatchServiceTest`:** 10,076 lines removed (service renamed to `TransformationService`)

### 🔐 Security

No security-related changes in this release.

---

## 🧾 [5.3.0] - 2026-03-15

### ➕ Added

#### Custom JPA Attribute Converters

- **ClubIdentifierConverter:** Type-safe `AttributeConverter` for `ClubIdentifier` enum persistence
- **CompetitorCategoryConverter:** Type-safe `AttributeConverter` for `CompetitorCategory` enum persistence
- **DivisionConverter:** Type-safe `AttributeConverter` for `Division` enum persistence
- **FirearmTypeConverter:** Type-safe `AttributeConverter` for `FirearmType` enum persistence
- **MatchCategoryConverter:** Type-safe `AttributeConverter` for `MatchCategory` enum persistence
- **PowerFactorConverter:** Type-safe `AttributeConverter` for `PowerFactor` enum persistence
- All converters replace `@Enumerated(EnumType.STRING)` with explicit, testable conversion logic

#### Service Enhancements

- **DomainService interface:** Enhanced with match result initialisation methods
- **IpscMatchService interface:** Extended with consolidated match processing capabilities

#### Test Coverage

- **DomainServiceTest:** 787 lines added – comprehensive `initMatchEntities` test cases with Javadoc documentation
- **IpscServiceIntegrationTest:** Comprehensive integration tests for `importWinMssCabFile` including validation and processing scenarios

### 🔄 Changed

#### Core Services (Major Refactoring)

- **DomainServiceImpl:** 270 lines changed
    - Enhanced `initMatchEntities` method with detailed Javadoc
    - Improved null handling throughout match entity processing
    - Streamlined match result handling with better flow control
    - Removed unused properties and cleaned up service implementation
- **IpscMatchServiceImpl:** 546 lines changed
    - Consolidated match results processing with improved logic
    - Removed commented-out code for cleaner implementation
    - Enhanced integration with updated DomainService
- **TransactionServiceImpl:** 22 lines changed
    - Enhanced null handling in transaction processing
    - Improved list initialisation for match operations
- **IpscServiceImpl:** 11 lines changed – minor updates
- **MatchEntityServiceImpl:** 24 lines changed – streamlined implementation
- **ClubEntityServiceImpl:** 24 lines changed – simplified to a single method
- **ClubEntityService:** 27 lines changed – removed unused methods

#### Domain Mapping

- **DtoMapping:** Converted from class to Java record construct
    - Simplified initialisation with a compact record constructor
    - Improved immutability and clarity of the DTO mapping state
    - Streamlined transaction stubbing in tests
- **DtoToEntityMapping:** 79 lines changed – enhanced with additional test cases and documentation

#### Entity Models

- **IpscMatch:** 14 lines changed – `mappedBy` added to `@OneToMany` annotations; cascade type updates
- **IpscMatchStage:** 26 lines changed – `mappedBy` added; Javadoc for `init()` added; entity mapping improvements
- **MatchCompetitor:** 20 lines changed – improved bidirectional `@OneToMany` relationship with `mappedBy`
- **MatchStageCompetitor:** 24 lines changed – enhanced mapping with proper ownership side declaration
- **Competitor:** 11 lines changed – minor relationship updates
- **Club:** 2 lines changed – minor updates

#### DTOs

- **MatchStageDto:** 95 lines changed – enhanced target/scoring handling
- **MatchStageCompetitorDto:** 82 lines changed – improved initialisation
- **MatchCompetitorDto:** 62 lines changed – streamlined constructor and init logic
- **CompetitorDto:** 27 lines changed – optimised initialisation
- **MatchDto:** 10 lines changed – minor updates
- **ClubDto:** 6 lines changed – minor updates
- **MatchResultsDto:** 1 line changed – minor clean-up

#### Repository Layer

- **IpscMatchRepository:** 10 lines changed – added scheduled date to queries for uniqueness constraints
- Competitor retrieval methods updated to use `Set` for deduplication and performance
- Match stage competitor retrieval enhanced with improved null handling
- Removed unnecessary fetch joins across repository methods

#### Test Suites (Comprehensive Updates)

- **IpscMatchServiceTest:** 3,156 lines changed – comprehensive consolidation including disabled tests, helper method extraction, streamlined parameter handling and object creation
- **TransactionServiceTest:** 1,031 lines changed – updated `getFirst()` assertions, enabled previously disabled tests, streamlined transaction stubbing
- **IpscServiceIntegrationTest:** 113 lines changed – integration tests added, previously disabled tests enabled, bean definitions cleaned up
- **DtoToEntityMappingTest:** 171 lines changed – additional test cases and documentation
- **MatchStageCompetitorDtoTest:** 243 lines changed – updated for DTO changes
- **MatchStageDtoTest:** 50 lines changed – updated assertions
- **CompetitorDtoTest:** 73 lines changed – updated for DTO refactoring
- **AwardCeremonyResponseTest:** 20 lines changed – minor updates
- **StringUtilTest:** 71 lines changed – updated utility tests
- **ValueUtilTest:** 2 lines changed – minor updates
- **MatchDtoTest:** 6 lines changed – minor updates

#### Build & Configuration

- **pom.xml:** Updated Spring Boot from 4.0.3 to 4.1.0-SNAPSHOT; added Spring Snapshots repository
- **application-dev.properties:** 13 lines changed – datasource and logging configuration updates
- **application-test.properties:** 6 lines changed – updated test datasource configuration
- **application.properties:** 1 line removed – minor clean-up
- **logback-spring.xml:** 2 lines changed – logging improvements
- **IpscConstants:** Updated competitor number and ICS alias constant values

### 🐛 Fixed

#### Entity Relationships

- **`@OneToMany` `mappedBy`:** Added missing `mappedBy` declarations for all bidirectional relationships across `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`, and `MatchStageCompetitor`
- **Cascade types:** Fixed cascade type configurations for correct entity lifecycle management
- **Null handling:** Improved null handling in entity relationship resolution across match stage competitor retrieval

#### Repository Queries

- **Fetch joins:** Removed unnecessary fetch joins reducing query complexity and improving performance
- **Match retrieval:** Fixed to properly include scheduled date constraint for uniqueness
- **Club and competitor lookup:** Improved accuracy of lookup methods

#### Code Quality

- **Test assertions:** Fixed to use `getFirst()` instead of index-based access for improved clarity
- **Test duplication:** Removed duplicate code patterns in test setups
- **Typo:** Corrected typo in `RELEASE_NOTES_HISTORY.md` competitor association section

### ⚠️ Deprecated

None.

### 🗑️ Removed

#### Services & Classes

- **`IpscMatchResultService` interface:** Fully removed (31 lines); functionality consolidated into `DomainService` and `IpscMatchService`
- **`IpscMatchResultServiceImpl` class:** Fully removed (379 lines); match result processing consolidated into `DomainService`
- **`ScoreDto` class:** Fully removed (50 lines); score data now handled via `ScoreResponse` directly

#### Entity Service Methods

- **`ClubEntityService.findClubById()`:** Removed unused method
- **`ClubEntityService.findClubByName()`:** Removed unused method
- **`ClubEntityService.findClubByAbbreviation()`:** Removed unused method
- Various unused helper methods removed from entity service implementations

#### Test Classes

- **`IpscMatchResultServiceTest`:** 1,802 lines removed – service deleted, tests no longer required
- **`ScoreDtoTest`:** 643 lines removed – `ScoreDto` deleted, tests no longer required

### 🔐 Security

No security-related changes in this release.

---

## 🧾 [5.2.0] - 2026-02-27

### ➕ Added

#### Architecture & Domain Model

- **DtoMapping class:** New comprehensive DTO mapping with map-based storage for improved data organisation
- **EntityMapping class:** New entity-level mapping structure for clear separation of persistence concerns
- **DtoToEntityMapping class:** Bridge layer between DTOs and entities with Optional-based accessors (91 lines)
- **MatchEntityHolder class:** Dedicated holder for match entity initialisation workflows
- **MatchEntityService interface:** Contract for match entity operations
- **MatchEntityServiceImpl:** Implementation with comprehensive initialisation logic

#### Test Coverage

- **DtoToEntityMappingTest:** 716 lines of comprehensive tests covering all mapping scenarios
    - Constructor tests (3 scenarios)
    - MatchDto accessor tests (3 scenarios)
    - MatchEntity accessor tests (2 scenarios)
    - Competitor DTO list tests (6 scenarios)
    - MatchStage DTO list tests (6 scenarios)
    - MatchCompetitor DTO list tests (6 scenarios)
    - MatchStageCompetitor DTO list tests (6 scenarios)
    - Entity setter tests (12 scenarios covering all entity types)
    - Comprehensive null, empty, partial and full data coverage
- **TransactionServiceTest:** 2,000+ lines with extensive edge case coverage
    - Null/empty/blank input tests
    - Partial and full input tests
    - Edge case handling
- **Enhanced test coverage** across all consolidated test suites with generateIpscMatchRecordHolder output verification

#### Service Enhancements

- **Array initialisation:** All DTO arrays initialised to empty arrays instead of null to prevent NPE
- **Club filtering:** Enhanced club abbreviation filtering logic in match entity initialisation
- **Optional return types:** `importWinMssCabFile()` now returns Optional for better null handling
- **Initialisation methods:** New dedicated methods for match-related entity initialisation

### 🔄 Changed

#### Core Services (Major Refactoring)

- **IpscMatchServiceImpl:** 246 lines changed
    - Refactored `generateIpscMatchRecordHolder()` with improved entity initialisation
    - Enhanced club filtering with abbreviation-based logic
    - Simplified OneToMany annotations for better JPA relationship management
    - Removed match entity from DTOs for cleaner data separation
- **IpscMatchResultServiceImpl:** 333 lines changed
    - Comprehensive refactoring of `initMatchResults()` method
    - Enhanced `initScores()` with better null handling
    - Improved match results initialisation logic
    - Better handling of multiple match results and stages
- **IpscServiceImpl:** 106 lines changed
    - Updated `importWinMssCabFile()` to return Optional
    - Enhanced compatibility with a new mapping architecture
- **TransactionServiceImpl:** 198 lines changed
    - Added initialisation methods for match-related entities
    - Improved transaction handling for complex match operations
    - Better structuring of match DTO initialisation
    - Enhanced filtering for match-related entities
- **DomainServiceImpl:** Updated for new architecture

#### Entity Models

- **IpscMatch:** Simplified OneToMany annotations for better JPA relationships (7 lines changed)
- **IpscMatchStage:** Enhanced entity relationships (19 lines changed)
- **MatchCompetitor:** Updated relationships (22 lines changed)
- **MatchStageCompetitor:** Improved entity mapping (24 lines changed)
- **Club:** Minor updates (3 lines changed)
- **Competitor:** Minor updates (2 lines changed)

#### DTOs

- **MatchCompetitorDto:** Array initialisation to prevent null (6 lines changed)
- **MatchResultsDto:** Removed match entity reference (3 lines changed)

#### Repository Layer

- **IpscMatchRepository:** Updated for new entity structure (2 lines changed)

#### Controllers

- **IpscController:** Updated for service changes (4 lines changed)

#### Test Suites (Comprehensive Consolidation)

- **IpscMatchResultServiceImplTest:** 1,802 lines added – complete consolidation with enhanced coverage
    - Direct testing of initScores alongside indirect testing through initMatchResults
    - Null/empty/blank field edge cases
    - Partial and full field scenarios
    - Section-based organisation maintained from v5.1.0
- **IpscMatchResultServiceTest:** 2,197 lines removed – migrated to ImplTest
- **IpscServiceImplTest:** 2,010 lines changed – consolidated with improved test organisation
- **IpscServiceTest:** 844 lines removed – duplicates eliminated
- **IpscMatchServiceTest:** 2,197 lines changed – major consolidation including output verification tests
- **TransactionServiceTest:** 326 lines added with Arrange-Act-Assert comments
- **AwardServiceImplTest:** 302 lines added
- **AwardServiceTest:** 369 lines removed – consolidated into ImplTest
- **DomainServiceImplTest:** 387 lines changed – cleaned up and consolidated
- **DomainServiceTest:** 504 lines removed – duplicates eliminated
- **ImageServiceImplTest:** 186 lines added
- **ImageServiceTest:** 281 lines removed – consolidated into ImplTest
- **DateUtilTest:** 321 lines changed – complete consolidation
- **NumberUtilTest:** 138 lines changed – unified structure
- **StringUtilTest:** 128 lines changed – consolidated tests
- **ValueUtilTest:** 140 lines changed – complete consolidation
- **IpscServiceIntegrationTest:** 28 lines changed – removed unused DomainService
- **MatchStageCompetitorEntityServiceImpl:** 10 lines changed

### 🐛 Fixed

#### Null Safety

- **Array initialisation:** Initialised arrays to prevent null pointer exceptions in DTOs
- **Enhanced null checks:** Improved null safety throughout match result processing
- **Optional handling:** Better handling of Optional return types throughout the codebase

#### Test Quality

- **Duplicate removal:** Eliminated duplicate test methods across multiple test suites
- **Disabled tests:** Removed disabled test annotations, all tests now active or properly skipped
- **Empty/partial handling:** Corrected handling of empty and partial match results
- **Assertion clarity:** Enhanced test assertion precision and clarity

#### Code organisation

- **Unused dependencies:** Removed unused DomainService from integration tests
- **Mock clean-up:** Removed unused domain service mocks from test code
- **Import optimisation:** Streamlined test imports for better clarity

### ⚠️ Deprecated

None.

### 🗑️ Removed

#### Deprecated Code

- **Old MatchEntityHolder:** Replaced with new implementation
- **Match entity in DTOs:** Removed from MatchResultsDto for cleaner separation

#### Configuration & IDE Files

- **JetBrains .idea files:** Removed all .idea configuration files from version control
- **Updated .gitignore:** Permanently exclude JetBrains config files
- **Unused properties:** Cleaned up application.properties

#### Test Code

- **Duplicate tests:** Removed across all test suites (estimated 3,000+ lines of duplicates)
- **Unused mocks:** Removed unused DomainService mocks
- **Old test files:** Consolidated into Impl test files

### 🔐 Security

No security-related changes in this release.

---

## 🧾 [5.1.0] - 2026-02-25

### ➕ Added

#### Test Suite Enhancements

- **Test organisation improvements** in `IpscMatchResultServiceImplTest`
    - Section-based test grouping for improved navigation and understanding
    - Six distinct test sections: Null Input Handling, Null Collections and Fields, Match Name Field Handling, Club Fields Handling, Partial and Complete Data Scenarios, Edge Cases
    - Clear separation of concerns between test categories

#### Test Quality Improvements

- **Comprehensive test coverage metrics** with detailed test categorisation
- **23 unit tests** covering all critical scenarios for IPSC match result service
- **Section-based documentation** for enhanced test maintainability

### 🔄 Changed

#### Test Infrastructure

- **Test organisation:** Restructured `IpscMatchResultServiceImplTest` with logical section-based grouping
    - Null Input Handling section (2 tests)
    - Null Collections and Fields section (5 tests)
    - Match Name Field Handling section (3 tests)
    - Club Fields Handling section (2 tests)
    - Partial and Complete Data Scenarios section (6 tests)
    - Edge Cases section (4 tests)
    - Database Interaction section (1 skipped test)
- **Test naming:** Standardised naming conventions for consistency ( `testMethod_whenCondition_thenExpectedBehavior`)
- **Code style:** Improved spacing and formatting for better readability
- **Documentation:** Enhanced test section comments with clear headers and visual separators

### 🐛 Fixed

#### Test Quality

- **Duplicate test elimination:** Removed duplicate `testInitMatchResults_withMultipleStagesAndScores_thenMapsCorrectly()` test method
- **Code clean-up:** Removed TODO comment about adding sections (now complete)
- **Test file consolidation:** Ensured no redundant test coverage

### ⚠️ Deprecated

### 🗑️ Removed

- **Duplicate test:** `testInitMatchResults_withMultipleStagesAndScores_thenMapsCorrectly()` - Removed exact duplicate at the end of the file

### 🔐 Security

---

## 🧾 [5.0.0] - 2026-02-24

### ➕ Added

#### Domain Entity Initialisation Framework

- **`DomainServiceImpl.initClubEntity(ClubDto)`** - Initialise club entities from DTO objects with automatic database lookup and fallback to new entity creation
- **`DomainServiceImpl.initClubEntity(ClubIdentifier)`** - Initialise club entities from enumeration values for predefined club references
- **`DomainServiceImpl.initMatchEntity(MatchDto, Club)`** - Sophisticated match entity initialisation with repository lookup, optional entity creation and club association
- **`DomainServiceImpl.initCompetitorEntities(List<CompetitorDto>)`** - Batch competitor entity initialisation with UUID generation and optional database persistence
- **`DomainServiceImpl.initMatchStageEntities(List<MatchStageDto>, IpscMatch)`** - Initialise match stages with proper relationship linking to parent match entities
- **`DomainServiceImpl.initMatchCompetitorEntities(List<MatchCompetitorDto>, Map<UUID, Competitor>)`** - Establish many-to-many relationships between matches and competitors
- **`DomainServiceImpl.initMatchStageCompetitorEntities(List<MatchStageCompetitorDto>, ...)`** - Complex initialisation of stage-specific competitor records with score and performance data

#### IPSC Match Record Generation

- **`IpscMatchServiceImpl.generateIpscMatchRecordHolder(List<IpscMatch>)`** - Convert IPSC match entities to comprehensive match records for external representation
- **`IpscMatchServiceImpl.initIpscMatchResponse(IpscMatch, List<CompetitorMatchRecord>)`** - Build complete IPSC match response records with embedded competitor data
- **`IpscMatchServiceImpl.initCompetitor(Competitor, MatchCompetitorRecord, List<MatchStageCompetitorRecord>)`** - Create detailed competitor match records with stage-wise performance data
- **`IpscMatchServiceImpl.initMatchCompetitor(Competitor, List<MatchCompetitor>)`** - Extract and process match-level competitor records from database entities
- **`IpscMatchServiceImpl.initMatchStageCompetitor(Competitor, List<MatchStageCompetitor>)`** - Generate stage-specific competitor records with individual stage scores

#### Service Layer

- **`IpscMatchResultServiceImpl`** - Enhanced with comprehensive null handling and processing for match results
    - Improved edge case handling
    - Better robustness in match result transformation
    - Additional null-safety checks

### ⚠️ Deprecated

### 🗑️ Removed

### 🔐 Security

---

## 🧾 [5.0.0] - 2026-02-24

### ➕ Added

#### Domain Entity Initialisation Framework

- **`DomainServiceImpl.initClubEntity(ClubDto)`** - Initialise club entities from DTO objects with automatic database lookup and fallback to new entity creation
- **`DomainServiceImpl.initClubEntity(ClubIdentifier)`** - Initialise club entities from enumeration values for predefined club references
- **`DomainServiceImpl.initMatchEntity(MatchDto, Club)`** - Sophisticated match entity initialisation with repository lookup, optional entity creation and club association
- **`DomainServiceImpl.initCompetitorEntities(List<CompetitorDto>)`** - Batch competitor entity initialisation with UUID generation and optional database persistence
- **`DomainServiceImpl.initMatchStageEntities(List<MatchStageDto>, IpscMatch)`** - Initialise match stages with proper relationship linking to parent match entities
- **`DomainServiceImpl.initMatchCompetitorEntities(List<MatchCompetitorDto>, Map<UUID, Competitor>)`** - Establish many-to-many relationships between matches and competitors
- **`DomainServiceImpl.initMatchStageCompetitorEntities(List<MatchStageCompetitorDto>, ...)`** - Complex initialisation of stage-specific competitor records with score and performance data

#### IPSC Match Record Generation

- **`IpscMatchServiceImpl.generateIpscMatchRecordHolder(List<IpscMatch>)`** - Convert IPSC match entities to comprehensive match records for external representation
- **`IpscMatchServiceImpl.initIpscMatchResponse(IpscMatch, List<CompetitorMatchRecord>)`** - Build complete IPSC match response records with embedded competitor data
- **`IpscMatchServiceImpl.initCompetitor(Competitor, MatchCompetitorRecord, List<MatchStageCompetitorRecord>)`** - Create detailed competitor match records with stage-wise performance data
- **`IpscMatchServiceImpl.initMatchCompetitor(Competitor, List<MatchCompetitor>)`** - Extract and process match-level competitor records from database entities
- **`IpscMatchServiceImpl.initMatchStageCompetitor(Competitor, List<MatchStageCompetitor>)`** - Generate stage-specific competitor records with individual stage scores

#### IPSC Response Processing Pipeline

- **`IpscMatchServiceImpl.addClubToMatch(IpscResponse, IpscRequestHolder)`** - Intelligent club association logic that matches clubs from request data to match response records with fallback mechanisms
- **`IpscMatchServiceImpl.addMembersToMatch(IpscResponse, IpscRequestHolder)`** - Associate enrolled members with match responses based on match ID filtering

#### Enhanced IPSC Result Service

- **`IpscMatchResultServiceImpl.initMatchResults(IpscResponse)`** - Complete IPSC response-to-DTO transformation pipeline
- **`IpscMatchResultServiceImpl.initClub(ClubResponse)`** - Convert IPSC club response objects to club DTOs with database lookup and enrichment
- **`IpscMatchResultServiceImpl.initMatch(IpscResponse, ClubDto)`** - Create or update match DTOs from IPSC responses with optional database lookup and update avoidance
- **`IpscMatchResultServiceImpl.initStages(MatchDto, List<StageResponse>)`** - Map IPSC stage responses to match stage DTOs
- **`IpscMatchResultServiceImpl.initScores(MatchResultsDto, IpscResponse)`** - Process and aggregate competitor scores across match stages

#### DTO Architecture Enhancements

- **`ClubDto(Club)`** - Constructor for creating DTOs from club entities
- **`ClubDto(ClubResponse)`** - Constructor for creating DTOs from IPSC response objects
- **`ClubDto(ClubIdentifier)`** - Constructor for creating DTOs from enumerated club identifiers
- **`ClubDto(Club, ClubIdentifier)`** - Constructor supporting fallback initialisation from club identifier if the entity is null

### 🔄 Changed

#### Version Management

- **Adopted Semantic Versioning (SemVer):** Project now follows [SemVer 2.0.0](https://semver.org/) specification
- **Version Format:** Changed from the legacy scheme (v1.x to v4.x) to `MAJOR.MINOR.PATCH` format
- **Release Documentation:** Structured release notes following industry-standard conventions

#### Entity Initialisation Strategy

- **Repository Integration:** Entity initialisation methods now query the database to check for existing entities before creating new ones
- **Fallback Handling:** Robust fallback mechanisms when entities are not found in the database
- **Transactional Consistency:** All entity creation and update operations maintain transactional integrity through `TransactionService`

#### Data Processing Pipelines

- **Multi-Step Processing:** IPSC responses now go through coordinated initialisation steps for clubs, matches, stages and competitors
- **Error Handling:** Enhanced validation and error messages for data transformation failures
- **Null Safety:** Comprehensive null checks throughout data processing pipelines

#### Test Infrastructure (Post-Release Enhancement)

- **Test Organisation:** Restructured DTO test classes with clear section headers and logical grouping
- **Naming Standards:** Standardised test naming to `testMethod_whenCondition_thenExpectedBehavior` pattern
- **Test Coverage Expansion:** Added 151+ new unit tests for DTO classes (MatchStageDtoTest: 48, ScoreDtoTest: 26, MatchStageCompetitorDtoTest: 77)
- **AAA Pattern:** Consistent Arrange-Act-Assert structure implemented across all new tests
- **Edge Case Coverage:** Extensive null/empty/blank field-testing, boundary value testing
- **Documentation:** Comprehensive test documentation and inline comments

### 🐛 Fixed

#### Entity Relationship Management

- Fixed edge cases in entity initialisation when creating stages with missing `maxPoints` values
- Resolved mapping issues between DTOs and domain entities during update operations
- Corrected null-safety handling in the recursive establishment of entity relationships

#### Data Transformation

- Improved handling of optional entity relationships during transformation
- Fixed club name resolution from both entity objects and enumeration values
- Enhanced date field handling in match entity initialisation

### ⚠️ Deprecated

No deprecations in this release.

### 🗑️ Removed

No breaking removals in this release. All features from version 4.1.0 remain available.

### 🔐 Security

- No security vulnerabilities were addressed in this release
- All existing security measures from version 4.1.0 are maintained

### 📚 Documentation

- **New:** Comprehensive RELEASE_NOTES.md with semantic versioning transition details
- **New:** Detailed CHANGELOG.md (this file) following Keep a Changelog format
- **Updated:** Architecture documentation updated to reflect entity initialisation patterns
- **Reference:** Legacy release notes archived in ARCHIVE.md with a deprecation notice

---

## 🧾 [4.1.0] - 2026-02-13

### ➕ Added

#### CRUD Operations for IPSC Entities

- **`IpscMatchRepository`** - Create, Read, Update, Delete operations for IPSC match entities
- **`IpscMatchStageRepository`** - CRUD support for match stage entities
- **Service layer CRUD:** Implemented corresponding service methods for all CRUD operations
- **Transactional handling:** Transaction management for all write operations

#### Enhanced Input Validation

- **DTO Validation:** Additional `@NotNull` annotations on critical DTO fields
- **Bean Validation:** Jakarta Validation annotations integrated throughout request/response DTOs
- **Error Messages:** Detailed validation error reporting

#### Testing Improvements

- **Unit Tests:** Added comprehensive unit tests for CRUD endpoints
- **Integration Tests:** Extended integration tests for service behaviour
- Test coverage for validation failures and edge cases

### 🔄 Changed

- Improved request validation on create/update DTOs
- Enhanced repository query methods with additional filtering options
- Refined service layer contracts for better API consistency

### 🐛 Fixed

- Edge cases in entity initialisation when creating stages with missing `maxPoints`
- Mapping issues between DTOs and domain entities during updates

---

## 🧾 [4.0.0] - 2026-02-11

### ➕ Added

#### Major IPSC Domain Refactoring

- **Entity Renames:** `Match` → `IpscMatch`, `MatchStage` → `IpscMatchStage`
- **Repository Updates:** New `IpscMatchRepository` and `IpscMatchStageRepository` interfaces
- **Enhanced Type Safety:** Improved domain model clarity through explicit entity naming

#### Improved Input Validation

- **Multi-layered Validation:** Validation at controller, service and entity levels
- **Error Mapping:** Comprehensive error response generation with detailed messages

#### Exception Handling Improvements

- **Global Exception Handler:** Centralised exception handling for consistent error responses
- **Custom Exceptions:** Domain-specific exception types for clearer error semantics

#### Comprehensive Testing

- **Unit Test Coverage:** Extensive test coverage for service implementations
- **Integration Testing:** Full pipeline testing from controller through persistence layer
- **Bug Fixes:** Tests added to prevent regression of known issues

#### XML Parsing Bug Fixes

- Fixed edge cases in XML parsing logic
- Improved handling of malformed XML structures
- Enhanced validation of parsed XML data

### 🔄 Changed

#### Breaking Changes

- **Entity Renaming:** Consumers must update references from `Match` to `IpscMatch`
- **Repository Interface Changes:** Update injection points to use `IpscMatchRepository` and `IpscMatchStageRepository`
- **Service Method Names:** Some service method signatures updated for consistency

#### Database

- **Schema Updates:** Reflected entity renames in JPA configuration
- **Migration Path:** Existing data remains compatible; no data loss during migration

### ⚠️ Deprecated

- Old `MatchRepository` interface (replaced by `IpscMatchRepository`)
- Old service method signatures (superseded by refactored versions)

---

## 🧾 [3.1.0] - 2026-02-10

### ➕ Added

- Enhancement to IPSC data processing pipeline
- Improved error handling for specific match processing scenarios

### 🔄 Changed

- Refactored some internal service implementations
- Updated repository query methods

---

## 🧾 [3.0.0] - 2026-02-10

### ➕ Added

- Major feature release for IPSC integration
- Enhanced data processing capabilities

### 🔄 Changed

- Significant internal restructuring

---

## 🧾 [2.0.0] - 2026-02-08

### ➕ Added

- Major refactoring of core services
- New repository patterns

### 🔄 Changed

- Restructured service layer

---

## 🧾 [1.1.3] - 2026-01-28

### 🐛 Fixed

- Bug fixes and stability improvements

---

## 🧾 [1.1.2] - 2026-01-20

### ➕ Added

- Minor feature enhancements

---

## 🧾 [1.1.1] - 2026-01-16

### 🐛 Fixed

- Specific bug fixes

---

## 🧾 [1.1.0] - 2026-01-14

### ➕ Added

- New functionality and improvements

---

## 🧾 [1.0.0] - 2026-01-04

### ➕ Added

- Initial release of HPSC Website Backend
- Core REST API for match management
- Basic IPSC integration
- Competitor and club management
- Image gallery support
- Award ceremony management

---

## 📋 Version Policy

### Semantic Versioning (Current)

As of version 5.0.0, this project follows [Semantic Versioning 2.0.0](https://semver.org/):

- **MAJOR** version for incompatible API changes
- **MINOR** version for backward-compatible functionality additions
- **PATCH** version for backward-compatible bug fixes

### Legacy Versioning (v1.x – v4.x)

Earlier releases used a non-semantic versioning scheme. For historical documentation, see [ARCHIVE.md](/documentation/archive/ARCHIVE.md).

---

## 🚀 Upgrade Guide

### From v5.3.0 to v5.4.0

**Breaking Changes:** None

1. Update the version in `pom.xml` to `5.4.0`
2. Replace any `IpscMatchService` injection points with `TransformationService`
3. Update import statements for classes moved from `ipsc/domain` to `ipsc/data`
4. Update `MatchCompetitorEntityService` call sites to handle `List<>` return types
5. Run `./mvnw clean install` to rebuild the project

### From v4.1.0 to v5.0.0

**Breaking Changes:** None

1. Update the version in `pom.xml` to `5.0.0`
2. Run `./mvnw clean install` to rebuild the project
3. Restart the application
4. Existing data and configurations remain compatible

### From v4.0.0 to v4.1.0

**Breaking Changes:** None

Migration: See v4.1.0 release notes

### From v3.x to v4.x

**Breaking Changes:** Yes

- Update entity references from `Match` to `IpscMatch`
- Update service injections to use `IpscMatchRepository`
- See v4.0.0 release notes for a detailed migration guide

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Create a feature branch from `main`
2. Make your changes with comprehensive test coverage
3. Document your changes in the appropriate sections of this CHANGELOG
4. Submit a pull request with a detailed description

---

## 💬 Support

For issues, feature requests or questions:

- **GitHub Issues:** [tahoni/hpsc-web-springboot/issues](https://github.com/tahoni/hpsc-web-springboot/issues)
- **Repository:** [tahoni/hpsc-web-springboot](https://github.com/tahoni/hpsc-web-springboot)

---

**Last Updated:** 2026-08-25
