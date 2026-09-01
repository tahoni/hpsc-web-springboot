# Project History

A comprehensive historical overview of the HPSC Website Backend project from start to current release, documenting the
evolution of architecture, features and design philosophy across all versions.

---

## Table of Contents

- [📅 Historical Timeline](#-historical-timeline)
- [📖 Evolution Overview](#-evolution-overview)
- [🎯 Major Milestones](#-major-milestones)
- [🏛️ Architectural Evolution](#-architectural-evolution)
- [✨ Feature Timeline](#-feature-timeline)
- [💡 Project Philosophy Evolution](#-project-philosophy-evolution)
- [📚 Key Learnings](#-key-learnings)
- [🚀 Future Roadmap](#-future-roadmap-implications)
- [🎓 Conclusion](#-conclusion)

---

## 📅 Historical Timeline

### Version 8.2.0 (September 1, 2026)

**Theme:** Competitor Multi-Email Support & Bulk CSV Separator Standardisation

**Key Focus:**

- `Competitor.emailAddress` (a single, optional `String`) replaced with `emailAddresses` (`List<String>`), mapped
  via `@ElementCollection`/`@CollectionTable` onto a new `competitor_email` child table — a competitor can now have
  zero or more email addresses; `V7_2_0__add_competitor_emails.sql` backfills the new table from any existing
  non-blank `email_address` values before dropping that column
- `CompetitorRequest`/`CompetitorResponse` renamed `emailAddress` to `emailAddresses`; `CompetitorRequestForCSV`
  keeps a single `String` CSV cell but now holds zero or more semicolon-separated addresses (e.g. `"a@x.com;b@x.com"`),
  split into a list via `IpscCompetitorServiceImpl`'s new `splitEmailAddresses` helper
- New shared `SystemConstants.ARRAY_SEPARATOR` (`";"`) constant; `AwardServiceImpl`/`ImageServiceImpl`'s bulk CSV
  parsing switched from `"|"` to it, so every bulk CSV endpoint's multi-value cells (competitor email addresses,
  image/award tags) now share one separator convention, with the `AwardController`/`ImageController`/
  `IpscCompetitorController` Swagger examples updated to match
- Qodana static analysis removed entirely (`.github/workflows/qodana.yml`, `qodana.yaml`, and every reference in
  `ARCHITECTURE.md`/`CONTRIBUTING.md`/`AGENTS.md`'s CI/CD documentation): a release audit found it had failed on
  every CI run since v8.1.1 added it, and `documentation/roadmap/improvement-plan.md`'s Gap #7 closes as not
  applicable rather than delivered
- Project version bumped to 8.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.1.1 (September 1, 2026)

**Theme:** CI Static Analysis, Release-Process Self-Maintenance & Coverage Regression Fixes

**Key Focus:**

- New `.github/workflows/qodana.yml` runs JetBrains' `qodana-action` against the existing `qodana.yaml` config on
  push/PR to `develop`/`main`, mirroring `codeql.yml`'s trigger branches; results upload as SARIF to GitHub code
  scanning
- Two new Claude Code skills, `update-improvement-plan-gaps` (full codebase sweep for new gaps) and
  `sync-improvement-plan-gaps` (diff-driven check for gaps a branch's own work has closed or progressed), formalise
  the manual roadmap-maintenance work performed by hand across v8.0.0/v8.1.0; `generate-pr-description` renamed to
  `prep-version-release` to reflect its actual scope, and now runs both new skills as its own first step
- `AGENTS.md`'s Release Checklist re-synced against `prep-version-release`'s actual process, which had drifted
  ahead of it — three new steps added (improvement-plan gap check, `[Unreleased]` completeness verification,
  conditional `CONTRIBUTING.md` update), described tool-agnostically
- Recreated `NonFatalExceptionTest`/`FatalExceptionTest`/`ValidationExceptionTest` — these existed as of v7.2.0 but
  were dropped with no replacement, leaving the exception hierarchy at 20% line coverage; added tests for the
  `models/ipsc/shared` scoring groundwork classes (0% coverage) and every `patchCompetitor`/`patchMatch` field's
  previously-untested success path. Full-suite coverage rose from 92.9%/93.4% to 98.34%/98.84% (line/branch),
  746 → 775 tests
- Spring Boot parent bumped `4.1.0` → `4.1.1`, dropping the now-redundant `jackson-databind`/`log4j-api`
  `dependencyManagement` overrides; the recurring dependency-currency check then caught a third redundant override,
  `jackson-bom.version`, confirmed against the parent POM directly
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` gain two new gaps (match-scoring
  service/controller layer; Qodana CI wiring — the latter partially progressed by this release's own workflow
  addition), and `CONTRIBUTING.md` gains a new "🗺️ Roadmap" section documenting both files' structure
- Project version bumped to 8.1.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.1.0 (September 1, 2026)

**Theme:** Competitor Bulk CSV Import & Required-Field Enforcement Fixes

**Key Focus:**

- New `IpscCompetitorController.createCompetitors` (`POST /ipsc/competitors/bulk`, consumes `text/csv`) backed by
  `IpscCompetitorService`/`IpscCompetitorServiceImpl`, following `AwardController`/`ImageController`'s bulk-import
  convention — but, unlike those, actually persisting each row via the existing `createCompetitor`
  validation/gender/home-club-resolution logic
- New `CompetitorRequestForCSV`/`CompetitorResponseHolder` models (`models/ipsc/competitor/`)
- Found and fixed a recurring Jackson gotcha: `@JsonProperty(required = true)` is silently inert without a matching
  `@JsonCreator` constructor. `CompetitorRequestForCSV`, `CompetitorRequest`, `MatchRequest`, `MatchStageRequest` and
  the not-yet-wired `MatchOverallScoresRequest`/`MatchStageScoresRequest` (plus their CSV variants) all gained a
  `@JsonCreator` constructor, each parameter bound via `@JsonProperty`, replacing their Lombok `@AllArgsConstructor`
- The two scores CSV variants' constructors now match their plain counterpart's signature exactly (including a
  CSV-absent `matchId`), making them usable as a `csvMapper.addMixIn(...)` mixin, matching
  `AwardServiceImpl`/`ImageServiceImpl` — though neither is wired into a controller yet
- Corrected a genuine mismatch found while fixing the above: `CompetitorRequest`'s Jackson-required third field was
  `competitorNumber`, when `IpscCompetitorServiceImpl.validateForCreate` actually requires `clubNumber`
- `@JsonFormat(pattern = HpscConstants.HPSC_INPUT_DATE_FORMAT)` added to `CompetitorRequest`/`CompetitorRequestForCSV`/
  `MatchRequest`'s `LocalDate` fields, making the accepted `yyyy-MM-dd` format explicit
- New unit tests for every touched request model's JSON/CSV (de)serialization and required-field enforcement, plus
  `IpscCompetitorController`/`Service`/`ServiceImpl` coverage for the new bulk endpoint
- Project version bumped to 8.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.0.0 (August 31, 2026)

**Theme:** IPSC Module Rebuild Complete — Competitor & Match CRUD

**Key Focus:**

- `IpscController`'s empty stub replaced by `IpscCompetitorController`/`IpscMatchController`, full CRUD
  (`create`/`update`/`patch`/`get`, plus `getAllMatches`) backed by new `IpscCompetitorService`/`IpscMatchService` +
  impls — resolves club/gender/firearm-type/match-category by name, and `patchMatch` upserts stages by stage number
  rather than replacing the whole list
- New `Gender` enum capabilities (`name`/`abbreviation` fields, case-insensitive `fromName()`, `toString()`) and new
  `GenderConverter`, wired onto `Competitor.gender`
- New `CompetitorRequest`/`CompetitorResponse` and `MatchResponse`/`MatchStageResponse` DTOs; `models/ipsc/request`
  split into `models/ipsc/match/request`/`models/ipsc/scores/request` to match the module's per-concern shape
- `AwardService`/`ImageService.processCsv` renamed to `createAwards`/`createImages`; bulk CSV endpoints moved to
  `/awards/bulk`/`/images/bulk` and now return `201 Created`; enum `getByX` factory methods renamed to `fromX` across
  all six enums
- Comprehensive Javadoc/`@since` pass across models, converters, exceptions, utils, constants and `ControllerAdvice`
- Qodana JVM static analysis re-added (`qodana.yaml`); the project's AI-agent tooling migrated from
  `.claude/commands/*.md` slash commands to `.claude/skills/*/SKILL.md` Skills
- `AGENTS.md`/`CLAUDE.md` merged into a single tool-agnostic reference; new line-wrapping, extended Arrange-Act-Assert
  and test-helper-placement conventions
- Extensive new test coverage: `IpscCompetitorController`/`Service`/`ServiceImpl` and
  `IpscMatchController`/`Service`/`ServiceImpl` unit + integration tests, `GenderTest`, `GenderConverterTest`
- Project version bumped to 8.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 7.4.1 (August 29, 2026)

**Theme:** Documentation Reflow & Historical Narrative Additions

**Key Focus:**

- Every root-level documentation file (`AGENTS.md`, `ARCHITECTURE.md`, `CLAUDE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`,
  `HISTORY.md`, `README.md`, `RELEASE_NOTES.md`) rewrapped to a consistent ~120-character line width, matching
  CLAUDE.md's existing wrap width — prose, list items and table columns realigned, with a handful of incidental
  copyedits (AGENTS.md's own serial-comma rule example corrected to follow the rule it states) surfacing along the way
- New "Major Version Goals" subsection under this file's Project Philosophy Evolution, summarising the driving goal
  behind each major version line (4.x, 5.x, 6.x, 7.x)
- New "Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)" entry, capturing the test-convention,
  documentation-accuracy and AI-agent-tooling work spanning those three releases
- Project version bumped to 7.4.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 7.4.0 (August 29, 2026)

**Theme:** IPSC Request DTOs, Route Clean-up & Documentation Conventions

**Key Focus:**

- New `za.co.hpsc.web.models.ipsc.request` package — `MatchRequest`/`MatchStageRequest`/`MatchStagesRequest` for
  match/stage submission and `MatchOverallResultRequest`/`MatchStageResultRequest` (plus
  `MatchOverallResultRequestForCSV`/`MatchStageResultRequestForCSV` abstract CSV variants) for competitor result
  submission, shaped to match Practiscore's export format; `MatchRequest` gains a `matchId` field for updating an
  existing match (previously creation-only); all carry field- and class-level Javadoc mirroring the new shared
  `IpscCommonScore`/`IpscMatchScore`/`IpscMatchStageScore` DTOs' Comstock-scoring documentation — groundwork for the
  IPSC module rebuild, not yet wired to any endpoint
- `AwardController`/`ImageController` route prefixes dropped from `/v1/awards`/`/v1/images` to `/awards`/`/images` — the
  unused `/v1` API versioning segment removed
- New AGENTS.md Serial commas rule (no comma before the final `and`/`or` in a list of three or more items) and a
  tightened British English rule that now covers code identifiers as well as prose — both applied retroactively across
  the existing documentation set, which surfaced and corrected two American-spelled test method names
- `documentation/roadmap/`'s `IMPROVEMENT_PLAN.md`/`TASKS.md` renamed to `improvement-plan.md`/
  `improvement-plan-tasks.md` for kebab-case consistency with the rest of the tooling docs
- New Claude Code command `/sync-unreleased-changes`, diffing the branch against its base plus any uncommitted changes
  to fill in missing `CHANGELOG.md` entries automatically; `RELEASE_NOTES.md`'s Contributors section now sourced from
  `git log`'s unique authors rather than a generic placeholder
- Release hygiene: `log4j-api` overridden to `2.25.5` for CVE-2026-49844; `.gitignore`/`.aiignore` refreshed from
  upstream templates; `README.md`'s H1 heading restored
- Project version bumped to 7.4.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 7.3.0 (August 25, 2026)

**Theme:** Documentation Accuracy Pass & PR Summary Tooling

**Key Focus:**

- New Claude Code command `/generate-pr-summary`, which condenses a version's archived `PR_DESCRIPTION_vX.Y.Z.md` and
  `RELEASE_NOTES_vX.Y.Z.md` into a short, plain, Bitbucket-style PR summary — a distillation rather than a restatement
  of this repo's own emoji-heavy documentation style; its Output instructions were subsequently clarified to require
  the raw, unrendered Markdown source in the fenced block
- `README.md`'s Introduction and Features sections corrected to stop describing match management, competitor/club CRUD,
  WinMSS import and XML/multi-format processing as existing capabilities; only `AwardController`/`ImageController` CSV
  processing is implemented today, with the match/competitor domain's service and controller layer still being rebuilt
  (as already noted in `CLAUDE.md`)
- `README.md`'s coverage-report instructions corrected from the non-functional `./mvnw test jacoco:report` to
  `./mvnw verify -Pcoverage`; the stray `1.x – 4.x` version range in its documentation-map description removed, per
  AGENTS.md's evergreen-documentation rule
- `ARCHITECTURE.md`'s test package tree corrected (removed the nonexistent `domain/` test package, added the missing
  `converters/`/`exceptions/` packages) and its CI/CD & Quality Gates table no longer overstates the `Build & Tests`
  gate as an "All PRs" GitHub Actions trigger — only `codeql.yml` runs automatically; `./mvnw test` is run locally/by
  reviewers
- No domain entities, repositories, services or API surface changed in this release — purely a documentation-accuracy
  and tooling pass
- Project version bumped to 7.3.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 7.2.0 (August 25, 2026)

**Theme:** Test Suite Conventions, AI-Agent Tooling and Dependency Maintenance

**Key Focus:**

- New interface-contract unit tests `services/AwardServiceTest`/`services/ImageServiceTest` (Mockito-based, testing
  `createAwards` through the `AwardService`/`ImageService` interface type rather than the impl class); new tests closing 4
  JaCoCo-identified coverage gaps in `ControllerResponseTest`, `FirearmTypeTest` and `ControllerAdviceTest` — overall
  suite coverage rose from 95.7%/91.7% to 97.3%/98.1% (line/branch)
- New Claude Code commands `/scaffold-unit-tests` (migrated from a stale, wrong-project prompt file and corrected to
  this repo's real interface/impl test split) and `/scaffold-integration-tests` (new, `@SpringBootTest`-based, following
  `AwardServiceIntegrationTest`/`ImageServiceIntegrationTest` as the template)
- `HpscWebApplicationTests` renamed to `HpscWebApplicationTest` to match the project's `<ClassName>Test` naming
  convention; 26 existing test files retrofitted with a new AGENTS.md test convention (a one-line `// methodName()`
  header per method group, ordered constructors → public → protected → alphabetical → `toString()` last) — no test
  behaviour changed, purely comments and reordering
- **Dependency maintenance:** Spring Boot parent upgraded `4.0.7` → `4.1.0`, with now-redundant `pom.xml` version
  overrides cleaned up (`spring-framework.version`/`tomcat.version` now match Boot's own defaults; a long-standing
  `commons.lang3.version` typo — Boot's real property is hyphenated — removed; `maven-dependency-plugin` pin removed,
  now Boot-managed) and the flyway-maven-plugin's separately-pinned `flyway-mysql` bumped `11.14.1` → `12.4.0` to match
  Boot's newly-managed `flyway.version`
- Verified via the full test suite (492 tests, up from 483 at the start of this release), `./mvnw verify -Pcoverage` and
  manual Flyway commands (`flyway:info`/`flyway:migrate`) against a real local MySQL 9.5 dev database — no domain
  entities, repositories or API surface changed in this release
- New CLAUDE.md Git Workflow section states the branching model's PR targets directly (`feature/*` → `develop`;
  `release/vX.Y.Z`/`hotfix/*` → `main`); CLAUDE.md now cross-links to AGENTS.md and corrects its package-overview table;
  a false claim that AssertJ is used for assertions (it is explicitly excluded from `pom.xml`) was removed from five
  project docs
- Project version bumped to 7.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 7.1.0 (August 24, 2026)

**Theme:** Shooter Log Refinement — Power Factor Scoping & Match Reference

**Key Focus:**

- `ShooterLogEntry` renamed to `ShooterLogCompetitor` (table `shooter_log_entry` → `shooter_log_competitor`), matching
  this entity's role as a per-competitor snapshot row rather than a generic log entry
- `ShooterLog` gains a non-nullable `powerFactor` (`PowerFactor`, via the existing `PowerFactorConverter`) — snapshots
  are now scoped by power factor as well as firearm type
- `ShooterLogCompetitor` gains a nullable `points` column (the points each contributing `MatchCompetitor` row
  contributed to the snapshot's `logValue`) and a non-nullable `match` (`@ManyToOne IpscMatch`) relation alongside the
  existing `matchCompetitor` link
- `ShooterLogRepository.findAllByCompetitorIdAndFirearmType` renamed to
  `findAllByCompetitorIdAndFirearmTypeAndPowerFactor`, now filtering by `PowerFactor` as well
- New `ShooterLogCompetitorRepository` (`findAllByShooterLogId`) supersedes `ShooterLogEntryRepository`; new Flyway
  migration `V7_1_0__update_shooter_log_schema.sql` renames the table, its unique-index and FK constraints, and adds the
  new columns — both tables remain empty in every environment (no calculation service populates them yet), so no
  backfill was required
- Project version bumped to 7.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`
- Alongside the schema work, this release migrates the repository's AI-agent prompt files from
  `.github/prompts/*.prompt.md` to `.claude/commands/*.md`, adopts GitFlow branching in `AGENTS.md` and adds
  `CONTRIBUTING.md`

### Version 7.0.0 (August 11, 2026)

**Theme:** Match Results, Visitor Tracking & Shooter Log Data Model

**Key Focus:**

- Six entities parked under `domain/old/` (`Club`, `Competitor`, `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`,
  `MatchStageCompetitor`) promoted back into `za.co.hpsc.web.domain`; the `.old` package removed entirely
- `Club` gains a unique `identifier` (`ClubIdentifier`, via `ClubIdentifierConverter`) tying a club row to
  HPSC/SOSC/PMPSC — visitors are derived relationally, not as a fourth club row
- `Competitor` gains a nullable `homeClub` (`@ManyToOne Club`) relation for home-club membership
- `MatchCompetitor.matchRanking` renamed to `overallRanking`; new `clubRanking` (same-club rank per firearm type) and
  `isVisitor` (`true` when `matchClub` differs from the host match's club); new unique constraint
  `(competitor_id, match_id, firearm_type)`
- `MatchStageCompetitor` FK changed from `competitor` to `matchCompetitor`, removing duplicated `competitorCategory`/
  `division`/`firearmType`/`powerFactor`/`matchClub` fields; new unique constraint
  `(match_competitor_id, match_stage_id)`
- `IpscMatchStage` gains a new unique constraint `(match_id, stage_number)`
- New `ShooterLog` entity (competitor, club, firearmType, `logValue`, `calculatedDate`) and `ShooterLogEntry` entity
  (`rankInLog`, unique constraint `(shooter_log_id, match_competitor_id)`) persist best-4-match shooter-log snapshots
- `repositories/` package (previously emptied in preparation for this rework) rebuilt from scratch with 8 new
  `JpaRepository` interfaces
- No new enums or converters — `ClubIdentifier` and `FirearmType` (with existing `AttributeConverter`s) are reused
- Project version bumped to 7.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`
- Verified via `./mvnw clean compile` and `HpscWebApplicationTests` (H2 schema build for all 8 entities); no dedicated
  new unit/integration tests added for the new/changed domain model in this release
- Statistics: 1 commit, 15 files changed, +207 insertions, -30 deletions

### Version 6.0.0 (May 1, 2026)

**Theme:** Dedicated Match CRUD API, Service Encapsulation & Package Restructuring

**Key Focus:**

- `IpscMatchController` introduced at `/v2/ipsc/matches` with full CRUD (POST, PUT, PATCH, GET)
- `IpscMatchService` + `IpscMatchServiceImpl` added as dedicated match management service layer
- `MatchOnlyDto`, `MatchOnlyRequest`, `MatchOnlyResponse`, `MatchOnlyResultsDto` introduced for match-only operations
  without stages
- `DomainServiceImpl` decoupled from JPA repositories — now delegates to entity services exclusively
- New entity service methods: `findClubById`, `findCompetitorById`, `findMatchStageCompetitorById`
- `IpscUtil` utility class added for club and match display-string formatting
- All IPSC models moved from `models/ipsc/` to `models/ipsc/common/`; new `models/ipsc/match/` sub-package
- Three new match search request models: `MatchSearchRequest`, `MatchSearchDateRequest`, `MatchSearchIdRequest`
- `IpscMemberController` stub registered at `/ipsc/member`
- `TransformationService.mapMatchOnly(MatchOnlyRequest)` added; `mapMatchResults` no longer throws `ValidationException`
- `ControllerAdvice` enhanced with structured logging across all exception handlers
- Spring Boot upgraded from 4.0.5 to 4.0.6; MIT licence and SCM metadata added to `pom.xml`
- 8 new test classes (~1,300 lines): `IpscMatchControllerTest`, `IpscMatchServiceTest`, `IpscMatchIntegrationTest`,
  `MatchOnlyDtoTest`, `MatchOnlyRequestTest`, `MatchOnlyResponseTest`, `MatchResponseTest`, `IpscUtilTest`;
  `IpscControllerTest` removed
- Statistics: 40 commits, 165 files changed, +6,779 insertions, -3,501 deletions

### Version 5.4.0 (April 26, 2026)

**Theme:** Competitor Enrolment, Service Transformation & Comprehensive Test Expansion

**Key Focus:**

- `EnrolledCompetitorDto` introduced (138 lines) for tracking enrolled competitors through the IPSC pipeline
- `IpscMatchService` renamed to `TransformationService`; `TransformationServiceImpl` introduced (1,098 lines)
- `ClubIdentifier` enhanced with abbreviation field; `ClubIdentifierConverter` updated for persistence
- Competitor SAPSA number validation and duplicate filtering added to `CompetitorDto`
- Package restructure: `ipsc/domain` → `ipsc/data`; records and holders reorganised
- 20+ new test classes added (~7,000 lines): controllers, converters, domain entities, exceptions, integration
- `IpscMatchServiceTest` removed (10,076 lines); `TransformationServiceTest` introduced (1,026 lines)
- Qodana JVM linter and JaCoCo 0.8.14 code coverage added to the CI/CD pipeline
- Bug fixes: PCC Optics division code, ControllerAdvice error handling, ClubIdentifier abbreviation
- Statistics: ~75 commits, 123 files changed, +12,713 insertions, -13,358 deletions

### Version 5.3.0 (March 15, 2026)

**Theme:** Service Consolidation, Custom JPA Converters & Repository Optimisation

**Key Focus:**

- Six new custom JPA attribute converters replacing `@Enumerated(EnumType.STRING)` for all enum types
- Complete removal of `IpscMatchResultService` and `ScoreDto`; functionality consolidated into `DomainService` and
  `IpscMatchService`
- `DtoMapping` transitioned from class to Java record construct for immutability
- Added `mappedBy` to all bidirectional `@OneToMany` entity relationships; fixed cascade types
- Repository query optimisation: scheduled date in match queries, `Set` for competitor deduplication, removed
  unnecessary fetch joins
- Major test updates: DomainServiceTest (+787 lines), IpscMatchServiceTest (3,156 lines changed), TransactionServiceTest
  (1,031 lines changed), IpscMatchResultServiceTest removed (1,802 lines), ScoreDtoTest removed (643 lines)
- Spring Boot upgraded from 4.0.3 to 4.1.0-SNAPSHOT
- Statistics: ~45 commits, 59 files changed, +5,686 insertions, -4,613 deletions

### Version 5.2.0 (February 27, 2026)

**Theme:** Match Results Processing Enhancement & Architecture Refactoring

**Key Focus:**

- New three-tier mapping system (DtoMapping, EntityMapping, DtoToEntityMapping)
- Enhanced match entity handling with a dedicated MatchEntityService
- Comprehensive test coverage: 716 lines of DtoToEntityMapping tests, 2,000+ lines of TransactionService tests
- Consolidated test suites across all services and utilities
- Enhanced null safety with array initialisation
- Major service refactoring: IpscMatchServiceImpl (246 lines), IpscMatchResultServiceImpl (333 lines),
  TransactionServiceImpl (198 lines)
- Statistics: 26 commits, 61 files changed, +13,567 insertions, -5,898 deletions

### Version 5.1.0 (February 25, 2026)

**Theme:** Test Suite Enhancement & Code Quality Consolidation

**Key Focus:**

- Comprehensive test reorganisation with 6 logical sections
- Elimination of duplicate test cases
- Enhanced test readability and maintainability
- Build stability: 23 passing tests, 0 failures, 1 skipped

### Version 5.0.0 (February 24, 2026)

**Theme:** Semantic Versioning Transition & Infrastructure Consolidation

### Version 4.1.0 (February 13, 2026)

**Theme:** CRUD Enhancement & API Maturity

### Version 4.0.0 (February 11, 2026)

**Theme:** Domain Refactoring & Quality Assurance

### Version 3.1.0 (February 10, 2026)

**Theme:** Exception Handling Consolidation

### Version 3.0.0 (February 10, 2026)

**Theme:** Domain Model Restructuring & IPSC Specialisation

### Version 2.0.0 (February 8, 2026)

**Theme:** Service-Oriented Architecture & Modularity

### Version 1.1.3 (January 28, 2026)

**Theme:** Documentation Enhancement & Mapper Centralisation

### Version 1.1.2 (January 20, 2026)

**Theme:** Project Documentation

### Version 1.1.1 (January 16, 2026)

**Theme:** API Clarity & Javadoc Standardisation

### Version 1.1.0 (January 14, 2026)

**Theme:** Award Processing & Core Model Refactoring

### Version 1.0.0 (January 4, 2026)

**Theme:** Foundation & Image Gallery

---

## 📖 Evolution Overview

The HPSC Website Backend project has evolved through distinct phases, each addressing specific architectural and feature
requirements:

### Phase 1: Foundation (v1.0.0)

**Duration:** January 4, 2026 - January 4, 2026

The inaugural release established the core infrastructure for the HPSC platform with a focus on image gallery
functionality.

**Key Accomplishments:**

- Initial Spring Boot application bootstrap with modern tech stack
- CSV-based image data processing engine
- MIME type inference and flexible column mapping
- Robust error handling framework (custom exceptions)
- Initial API controllers and REST endpoints
- Comprehensive Javadoc documentation

**Architecture Highlights:**

- Controller → Service → Model → Repository pattern
- CSV processing pipeline with validation
- Custom exception hierarchy (ValidationException, FatalException, CsvReadException)
- Global exception handler (ApiControllerAdvice)

**Technical Focus:**

- Data parsing and transformation
- Error handling and validation
- API documentation and clarity

---

### Phase 2: Feature Expansion (v1.1.0 – v1.1.3)

**Duration:** January 14, 2026 – January 28, 2026

Rapid iteration adding award processing, improving code quality and establishing documentation standards.

**Key Accomplishments:**

**v1.1.0 – Award Processing Integration**

- Comprehensive award processing with CSV support
- New service layer pattern (`HpscAwardService`)
- Award ceremony grouping and structured responses
- Enhanced input validation across all models
- Base `Request` and `Response` classes for metadata standardisation
- Integration of OpenAPI (Swagger UI) for API documentation
- Extensive unit test coverage for new features

**v1.1.1 – API Clarity**

- Javadoc standardisation across codebase
- Improved parameter documentation
- Enhanced validation annotations
- Better IDE assistance through improved documentation

**v1.1.2 – Project Documentation**

- Creation of README.md (project overview and setup)
- Creation of ARCHITECTURE.md (detailed system design)
- Comprehensive onboarding materials

**v1.1.3 – Code Quality & Documentation**

- Central Division → DisciplinesInDivision mapper
- Introduction of `Division.NONE` enum value
- Expanded Javadoc coverage
- Improved utility class design (private constructors)
- Spring Boot security update (4.0.2)

**Architecture Highlights:**

- Formalised service layer pattern
- Introduction of generic request/response base classes
- Centralised error response handling
- OpenAPI integration for automatic documentation

**Technical Focus:**

- Code documentation and maintainability
- Project documentation and onboarding
- Code quality and style enforcement
- Framework integration (OpenAPI)

---

### Phase 3: Architectural Transformation (v2.0.0)

**Duration:** February 8, 2026

Major refactoring introducing service-oriented architecture and comprehensive DTO layer.

**Key Accomplishments:**

**Service Layer Revolution**

- Replaced monolithic `IpscService` with specialised services:
    - `WinMssService` - CAB file import and XML processing
    - `MatchResultService` - Core match result transformation
    - `TransactionService` - Transaction management
    - `IpscMatchService` - IPSC-specific match operations
    - Domain-specific services (Competitor, MatchCompetitor, MatchStage, MatchStageCompetitor)

**DTO Architecture Introduction**

- Comprehensive DTO layer (`MatchDto`, `MatchResultsDto`, `CompetitorDto`, `MatchStageDto`, `MatchStageCompetitorDto`,
  `MatchCompetitorDto`)
- Request/response unification (removed `-ForXml` variants)
- UUID-based mapping between requests and domain objects
- Improved separation of concerns

**Domain Model Evolution**

- Removed `Club` entity (replaced with `ClubReference` enum)
- Enhanced timestamps and scoring fields across entities
- Introduction of competitor categories
- `XmlDataWrapper` for generic XML processing

**Testing & Quality**

- Comprehensive test coverage for new services
- Edge case handling (null values, initialisation logic)
- Transactional behaviour testing

**Architecture Highlights:**

- Modular service architecture
- DTO pattern for data transfer
- Transaction management abstraction
- Specialised domain services

**Technical Focus:**

- Architectural modularity and testability
- Data transformation pipelines
- Service-oriented design patterns
- Transaction safety

---

### Phase 4: Domain Specialisation (v3.0.0)

**Duration:** February 10, 2026

Comprehensive domain model restructuring for IPSC compliance and firearm-type classification.

**Key Accomplishments:**

**Domain Model Restructuring**

- `Discipline` enum → `FirearmType` enum (Handgun, PCC, Rifle, Shotgun, Mini Rifle, .22 Handgun)
- Division mapper restructure: `DivisionToDisciplinesInDivisionMapper` → `FirearmTypeToDivisions`
- Reintroduction of `Club` entity with proper JPA relationships
- Competitor category field standardisation across all models
- Match entity firearm type classification

**IPSC Specialisation**

- Firearm-type-specific division mappings
- Enhanced `FirearmType` enum with division retrieval methods
- Firearm type inference in match helpers
- IPSC-compliant scoring and ranking structures

**Entity Enhancement**

- `Club` entity with bidirectional `@OneToMany` relationship to `Match`
- `ClubRepository` and `ClubService`/`ClubServiceImpl`
- Enhanced `Match` entity with firearm type and club reference
- `MatchStage` entity with `maxPoints` field

**Comprehensive Testing**

- New test classes: `FirearmTypeTest`, `FirearmTypeToDivisionsTest`, `ClubDtoTest`, `ClubReferenceTest`
- Updated test classes for the new domain structure
- Expanded test coverage for enum utilities

**Documentation Enhancement**

- Detailed Javadoc for all domain entities and DTOs
- README.md feature expansion
- ARCHITECTURE.md domain documentation
- Entity initialisation method documentation

**Architecture Highlights:**

- Firearm-type-based classification system
- Club entity relationship management
- IPSC-specific domain modelling
- Enhanced enum utility methods

**Technical Focus:**

- IPSC domain compliance
- Entity relationship design
- Firearm-type classification
- Comprehensive test coverage

---

### Phase 5: Quality Assurance & Simplification (v3.1.0)

**Duration:** February 10, 2026

Focus on exception handling consolidation and API documentation accuracy.

**Key Accomplishments:**

**Exception Handling Consolidation**

- Merged generic exception handlers in ControllerAdvice
- Unified `Exception` and `RuntimeException` handling
- Combined `IllegalArgumentException` and `MismatchedInputException` handlers
- Removed redundant `CsvReadException` handler
- Streamlined error response generation

**API Documentation Improvements**

- Added `@Operation` annotations for clarity
- Fixed request body schema references
- Improved exception propagation documentation
- Removed unnecessary try-catch patterns

**Bug Fixes**

- Fixed XML parsing null return issue
- Enhanced exception context preservation
- Aligned XML and JSON parsing error handling

**Code Quality**

- Simplified exception handling architecture
- Improved error response consistency
- Better alignment with API documentation

**Architecture Highlights:**

- Simplified exception handling chain
- Improved error propagation flow
- Better documented API contracts

**Technical Focus:**

- Exception handling simplification
- API documentation accuracy
- Error consistency

---

### Phase 6: Major IPSC Refactoring (v4.0.0)

**Duration:** February 11, 2026

Significant domain entity refactoring with comprehensive testing and improved validation.

**Key Accomplishments:**

**Domain Entity Refactoring**

- `Match` → `IpscMatch` entity rename
- `MatchStage` → `IpscMatchStage` entity rename
- `MatchRepository` → `IpscMatchRepository` repository rename
- Removed `MatchStageRepository` (consolidated into `IpscMatchStageRepository`)
- Updated all dependent classes across services, controllers, helpers and tests

**Enhanced Validation & Robustness**

- Multi-layered validation (controller, service, entity levels)
- `@NotNull` annotations on critical service methods
- Enhanced DTO validation throughout processing
- Improved null-safety in data transformation

**Match Processing Improvements**

- Refactored match result processing logic
- Introduced `MatchResultsDtoHolder` for DTO management
- Enhanced CAB file import with modular methods
- Improved transaction error recovery

**Comprehensive Testing**

- Created `IpscMatchServiceImplTest` (985 lines)
- Significantly expanded `WinMssServiceTest`
- Updated all test classes for entity renames
- Complete pipeline testing coverage

**Bug Fixes**

- Fixed XML parsing edge cases
- Resolved entity mapping issues
- Enhanced error recovery mechanisms

**Code Quality Improvements**

- Improved modularity and separation of concerns
- Enhanced code readability and maintainability
- Better encapsulation through helper classes
- Simplified complex method implementations

**Architecture Highlights:**

- Explicit IPSC domain naming
- Enhanced validation layers
- Comprehensive test coverage
- Improved error handling

**Technical Focus:**

- Domain clarity through entity naming
- Validation robustness
- Comprehensive test coverage
- Infrastructure stability

---

### Phase 7: CRUD Enhancement & API Maturity (v4.1.0)

**Duration:** February 13, 2026

Added complete CRUD capabilities for IPSC entities and supporting improvements.

**Key Accomplishments:**

**CRUD Operations**

- Full Create, Read, Update, Delete support for `IpscMatch`
- Full CRUD support for `IpscMatchStage`
- Repository interface implementations
- Service layer CRUD methods
- Transactional handling for all write operations

**API Maturity**

- CRUD endpoints for match and stage management
- Enhanced request validation for create/update operations
- Improved DTO validation and null-safety
- Request/response schema updates

**Enhanced Persistence**

- Transactional boundaries for data consistency
- Foreign key constraint management
- Cascade behaviour specification
- Entity initialisation logic reuse

**Testing Improvements**

- Unit tests for CRUD operations
- Integration tests for service behaviour
- Validation failure test cases
- Edge case coverage

**Documentation & Migration**

- CRUD operation documentation
- Database schema migration notes
- Repository/service migration guidance
- Test fixture requirements

**Architecture Highlights:**

- Complete CRUD lifecycle
- Transactional consistency
- Enhanced entity persistence patterns

**Technical Focus:**

- Complete data lifecycle management
- API maturity and completeness
- Entity persistence best practices

---

### Phase 8: Semantic Versioning Transition (v5.0.0)

**Duration:** February 24, 2026

Strategic release consolidating infrastructure improvements and transitioning to semantic versioning.

**Key Accomplishments:**

**Semantic Versioning Adoption**

- Transition from legacy non-semantic versioning (v1.x – v4.x)
- Full compliance with [Semantic Versioning 2.0.0](https://semver.org/)
- Clear MAJOR.MINOR.PATCH version format
- Future release predictability

**Entity Initialisation Framework**

- Comprehensive entity initialisation methods across DomainServiceImpl
- Club entity initialisation from DTOs and enumerations
- Match entity initialisation with repository integration
- Competitor entity batch processing
- Stage entity relationship management
- Complex competitor-stage association methods

**IPSC Match Record Generation**

- `generateIpscMatchRecordHolder()` for match record creation
- Detailed competitor match record generation
- Stage-wise competitor record processing
- Performance metric calculation and aggregation

**IPSC Response Processing Pipeline**

- Club association with fallback mechanisms
- Member enrollment association
- Score aggregation across stages
- Complete response enrichment

**DTO Architecture Enhancements**

- Multiple constructor patterns for flexible initialisation
- Update methods from various sources
- Strong typing and null-safety
- Comprehensive string representations

**Infrastructure Consolidation**

- Leveraging Spring Boot 4.0.3 and Java 25
- Enhanced transaction management
- Multi-layered validation
- Improved error handling

**Documentation Excellence**

- Comprehensive RELEASE_NOTES.md
- Detailed CHANGELOG.md following Keep a Changelog format
- Legacy archive with deprecation notice
- Architecture documentation updates

**Testing & Quality**

- Extensive unit and integration tests for the service layer
- Mock-based testing with Mockito
- Complex entity initialisation testing
- Multi-scenario edge case coverage

**Comprehensive DTO Unit Testing (Post-Release Enhancement)**

- **MatchStageDtoTest:** 48 tests covering constructors, init() methods and toString() implementations
    - Single and dual-parameter constructor tests (11 tests)
    - init() method tests with null handling, partial/full population (19 tests)
    - toString() method tests with edge cases, club information, stage numbers (18 tests)
    - Edge cases: null fields, empty/blank strings, zero/negative/large stage numbers

- **ScoreDtoTest:** 26 tests covering all constructor patterns
    - No-argument constructor tests (3 tests)
    - ScoreResponse constructor tests with null/empty/blank handling (16 tests)
    - All-argument constructor tests (3 tests)
    - Constructor equivalence tests (2 tests)
    - Edge cases: zero values, negative values, max values, empty/blank strings, partial population

- **MatchStageCompetitorDtoTest:** 77 tests providing comprehensive coverage
    - No-argument constructor tests (3 tests)
    - MatchStageCompetitor entity constructor tests with edge cases (10 tests)
    - CompetitorDto + MatchStageDto constructor tests (6 tests)
    - All-arguments' constructor tests with 28 parameters (3 tests)
    - init() method tests covering ScoreResponse, EnrolledResponse, MatchStageDto combinations (24 tests)
    - toString() method tests with comprehensive scenarios (29 tests)
    - Edge cases: null entities, partial/full population, zero/negative/max values, enum mapping (PowerFactor, Division,
      FirearmType, CompetitorCategory), stage percentage calculation, special characters, Unicode support, long strings

**Test Quality Metrics**

- Clear naming: All tests follow `testMethod_whenCondition_thenExpectedBehavior` pattern
- AAA structure: Arrange-Act-Assert pattern with clear comments throughout
- Comprehensive assertions: Multiple assertions per test validating all aspects
- Edge case coverage: Extensive null, empty, blank and boundary value testing
- Organised sections: Tests grouped by functionality with clear section headers
- Field-by-field validation: Every field tested in isolation and combination scenarios
- Total DTO tests added: 151+ (48 + 26 + 77)

**Post-Release Test Enhancements (Post-v5.0.0)**

- **IpscMatchServiceTest:** Renamed from `IpscMatchEntityServiceImplTest` for improved clarity and consistency
    - Enhanced test coverage for match results processing
    - Improved test organisation and naming conventions
- **IpscMatchResultServiceImpl:** Enhanced with comprehensive null handling and processing for match results
    - Additional edge case coverage
    - Improved robustness in match result transformation
- **WinMSS Integration Tests:** Added comprehensive integration tests for `importWinMssCabFile`
    - Validation scenario coverage (multiple test cases)
    - Processing scenario testing (end-to-end pipeline verification)
    - Comprehensive CAB file import testing
- **FirearmTypeToDivisionsTest:** Enhanced with comprehensive cases and improved naming
    - Extended coverage of firearm types to division mappings
    - Improved test readability and maintainability
- **Test Documentation:** Improved comments in test classes for clarity and consistency
    - Better inline documentation
    - Enhanced code maintainability

**Documentation & Code Quality Improvements (Post-v5.0.0)**

- **Javadoc Standardisation:** Enhanced DTO and model Javadoc for consistency and clarity
    - Removed redundant "Must not be null" comments where `@NotNull` annotations enforce constraints
    - Standardised parameter descriptions across all DTOs (MatchDto, CompetitorDto, ClubDto, MatchStageDto, ScoreDto,
      MatchStageCompetitorDto, MatchCompetitorDto)
    - Improved method-level documentation for better understanding
    - Consistent documentation style throughout the codebase
- **Code Quality:** Continuous refinement of documentation standards
    - Emphasis on clarity over redundancy
    - Leveraging annotation-based constraints for null safety documentation
    - Focus on meaningful descriptions rather than repetitive boilerplate

**Consolidated Test Structure**

- **ClubDtoTest:** Reorganised with section headers for constructors, init(), toString()
- **CompetitorDtoTest:** Consolidated structure with logical grouping
- **MatchDtoTest:** Structured tests with clear subsections
- All existing tests were updated to follow consistent patterns

**Architecture Highlights:**

- Entity lifecycle management framework
- Response generation pipeline
- DTO pattern consistency
- Infrastructure consolidation

**Technical Focus:**

- Versioning standards adoption
- Entity initialisation robustness
- Data transformation completeness
- Infrastructure consolidation

---

### Phase 9: Test Quality Enhancement (v5.1.0)

**Duration:** February 25, 2026

Strategic focus on test suite quality, organisation and maintainability.

**Key Accomplishments:**

**Test Suite Reorganisation**

- Restructured `IpscMatchResultServiceImplTest` with 6 logical sections:
    - Null Input Handling (2 tests)
    - Null Collections and Fields (5 tests)
    - Match Name Field Handling (3 tests)
    - Club Fields Handling (2 tests)
    - Partial and Complete Data Scenarios (6 tests)
    - Edge Cases (4 tests)
    - Database Interaction (1 skipped test)

**Duplicate Test Elimination**

- Identified and removed duplicate test methods
- Reduced the test count from 24 to 23 while maintaining coverage
- Eliminated redundant test code

**Test Quality Improvements**

- Standardised all tests naming to `testMethod_whenCondition_thenExpectedBehavior` pattern
- Enhanced test readability with clear section headers and visual separators
- Improved code style and spacing for better navigation
- Added comprehensive test documentation

**Build Stability**

- 23 passing tests, 0 failures, 1 skipped
- Clean Maven builds with all dependencies resolved
- AAA (Arrange-Act-Assert) pattern consistently applied

**Architecture Highlights:**

- Section-based test organisation
- Improved test discoverability
- Enhanced maintainability

**Technical Focus:**

- Test quality and clarity
- Code organisation
- Documentation standards
- Maintainability improvements

---

### Phase 10: Architecture Refactoring (v5.2.0)

**Duration:** February 27, 2026

Major architectural improvement focused on match results processing, entity initialisation and comprehensive test
coverage.

**Key Accomplishments:**

**Three-Tier Mapping Architecture**

- **DtoMapping:** Comprehensive DTO mapping with map-based storage
- **EntityMapping:** Entity-level mapping structure for persistence layer
- **DtoToEntityMapping:** Bridge layer with Optional-based accessors (91 lines)
- Improved separation of concerns between DTOs and entities

**Match Entity Handling Enhancement**

- New `MatchEntityService` interface and implementation
- `MatchEntityHolder` for dedicated entity initialisation workflows
- Enhanced club filtering with abbreviation-based logic
- Streamlined initialisation methods with single responsibilities

**Service Layer Refactoring**

- **IpscMatchServiceImpl:** 246 lines changed
    - Refactored `generateIpscMatchRecordHolder()` with improved entity initialisation
    - Simplified OneToMany annotations for better JPA relationships
    - Removed match entity from DTOs for cleaner separation
- **IpscMatchResultServiceImpl:** 333 lines changed
    - Comprehensive refactoring of `initMatchResults()` method
    - Enhanced `initScores()` with better null handling
    - Improved handling of multiple match results and stages
- **TransactionServiceImpl:** 198 lines changed
    - Added initialisation methods for match-related entities
    - Improved transaction handling for complex operations
- **IpscServiceImpl:** 106 lines changed
    - Updated `importWinMssCabFile()` to return Optional
    - Enhanced compatibility with a new mapping architecture

**Comprehensive Test Consolidation**

- **DtoToEntityMappingTest:** 716 lines of comprehensive tests
    - Constructor, accessor and setter tests
    - Null, empty, partial and full data coverage
- **TransactionServiceTest:** 2,000+ lines with extensive edge cases
- Consolidated test suites across all services:
    - IpscMatchResultServiceImplTest, IpscServiceTest, IpscMatchServiceTest
    - AwardServiceTest, DomainServiceTest, ImageServiceTest
- Utility test consolidation:
    - DateUtilTest, NumberUtilTest, StringUtilTest, ValueUtilTest
- Removed 3,000+ lines of duplicate tests
- All tests follow `testMethod_whenCondition_thenExpectedBehavior` naming
- AAA (Arrange-Act-Assert) comments throughout

**Null Safety Improvements**

- initialised arrays in DTOs to prevent NullPointerException
- Enhanced null checks throughout match result processing
- Optional return types for better null handling

**Entity and DTO Updates**

- Entity models: IpscMatch, IpscMatchStage, MatchCompetitor, MatchStageCompetitor
- DTOs: MatchCompetitorDto, MatchResultsDto
- Repository: IpscMatchRepository
- Controller: IpscController

**Statistics**

- 26 commits
- 61 files changed
- +13,567 insertions
- -5,898 deletions
- Net: +7,669 lines

**Architecture Highlights:**

- Three-tier mapping system
- Enhanced separation of concerns
- Dedicated entity service layer
- Comprehensive null safety

**Technical Focus:**

- Architectural modularity
- Test consolidation and quality
- Null safety and robustness
- Code maintainability

---

### Phase 22: Competitor Multi-Email Support & Bulk CSV Separator Standardisation (v8.2.0)

**Duration:** September 1, 2026

A domain feature release: competitors gain multiple email addresses, and every bulk CSV endpoint's multi-value cell
format is unified onto one separator convention.

**Key Accomplishments:**

**Competitor Multi-Email Support**

- `Competitor.emailAddress` (a single, optional `String`) replaced with `emailAddresses` (`List<String>`), mapped
  via `@ElementCollection`/`@CollectionTable` onto a new `competitor_email` child table
- `V7_2_0__add_competitor_emails.sql` backfills the new table from any existing non-blank `email_address` values,
  then drops that column
- `CompetitorRequest`/`CompetitorResponse` renamed `emailAddress` to `emailAddresses`; `CompetitorRequestForCSV`
  keeps a single CSV cell but now holds zero or more semicolon-separated addresses, split via
  `IpscCompetitorServiceImpl`'s new `splitEmailAddresses` helper

**Bulk CSV Separator Standardisation**

- New shared `SystemConstants.ARRAY_SEPARATOR` (`";"`); `AwardServiceImpl`/`ImageServiceImpl`'s bulk CSV parsing
  switched from `"|"` to it, so competitor email addresses and image/award tags now share one multi-value cell
  convention, with the `AwardController`/`ImageController`/`IpscCompetitorController` Swagger examples updated to
  match

**Static Analysis Removal**

- Qodana static analysis removed entirely: `.github/workflows/qodana.yml`, `qodana.yaml`, and every reference in
  `ARCHITECTURE.md`/`CONTRIBUTING.md`/`AGENTS.md`'s CI/CD documentation. A release audit found it had failed on
  every CI run since v8.1.1 added it — a missing `QODANA_TOKEN` secret and an unconditional SARIF-upload step — with
  no working baseline left to preserve
- `documentation/roadmap/improvement-plan.md`'s Gap #7 (Qodana CI wiring) closed as not applicable rather than
  delivered

**Architecture Highlights:**

- No layering change — a domain-model extension (`@ElementCollection` child table) and a cross-cutting constant,
  both within the existing service/controller structure

**Technical Focus:**

- Domain-model evolution (single value → collection, backed by a new child table)
- Cross-endpoint consistency (shared constant replacing two independently hardcoded separators)

**Test Coverage:**

- Existing CSV parsing, request/response model and bulk-import tests updated for the new `emailAddresses` shape and
  separator

---

### Phase 21: CI Static Analysis, Release-Process Self-Maintenance & Coverage Regression Fixes (v8.1.1)

**Duration:** September 1, 2026

A process-and-quality patch release: no new domain feature, but real coverage-regression fixes, a self-maintaining
release-checklist/roadmap tooling loop, and a completed CI quality gate that had sat configured but unwired since
v8.0.0.

**Key Accomplishments:**

**CI Static Analysis**

- New `.github/workflows/qodana.yml` runs `JetBrains/qodana-action` against the existing `qodana.yaml` config on
  push/PR to `develop`/`main`, mirroring `codeql.yml`'s trigger branches; results upload as SARIF to GitHub code
  scanning, so no Qodana Cloud token or other secret is required

**Release-Process Self-Maintenance**

- New `update-improvement-plan-gaps`/`sync-improvement-plan-gaps` Claude Code skills formalise the manual
  roadmap-gap-maintenance work done by hand across v8.0.0/v8.1.0 — a full-sweep audit and a diff-driven check,
  respectively; `generate-pr-description` renamed to `prep-version-release` to reflect its actual scope and now
  runs both new skills as its first step
- `AGENTS.md`'s Release Checklist re-synced against `prep-version-release`'s actual, drifted-ahead process: three
  new steps (improvement-plan gap check, `[Unreleased]` completeness verification, conditional `CONTRIBUTING.md`
  update), described tool-agnostically without naming the skills

**Coverage Regression Fixes**

- Recreated `NonFatalExceptionTest`/`FatalExceptionTest`/`ValidationExceptionTest`, covering every constructor
  overload — these existed as of v7.2.0 but were dropped somewhere before now with no replacement, leaving the
  exception hierarchy at 20% line coverage
- New tests for the `models/ipsc/shared` scoring groundwork classes (0% coverage previously) and every
  `patchCompetitor`/`patchMatch` field's previously-untested success path
- Full-suite coverage rose from 92.9%/93.4% to 98.34%/98.84% (line/branch), 746 → 775 tests

**Dependency Clean-up**

- Spring Boot parent bumped `4.1.0` → `4.1.1`, dropping the now-redundant `jackson-databind`/`log4j-api`
  `dependencyManagement` overrides
- The recurring dependency-currency check then caught a third redundant override, `jackson-bom.version`, confirmed
  against the parent POM directly rather than an echoed property

**Documentation & Roadmap**

- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` gain two new gaps (match-scoring
  service/controller layer; Qodana CI wiring, the latter partially progressed by this release's own workflow
  addition) and close Gap #5 (`jackson-databind` override)
- `CONTRIBUTING.md` gains a new "🗺️ Roadmap" section documenting both roadmap files' structure and maintenance
  convention — the only one of `README.md`/`AGENTS.md`/`ARCHITECTURE.md`/`CONTRIBUTING.md` that didn't already list
  them

**Architecture Highlights:**

- No architectural change — this release is process, tooling, dependency and test-coverage work only

**Technical Focus:**

- CI/CD quality gate completion (static analysis)
- Release-process and roadmap-documentation self-maintenance
- Test-coverage regression recovery
- Dependency currency

**Test Coverage:**

- 29 new tests added (746 → 775): the recreated exception hierarchy tests, three new shared-scoring-model test
  classes, and expanded `patchCompetitor`/`patchMatch` success-path coverage

---

### Phase 20: Competitor Bulk CSV Import & Required-Field Enforcement Fixes (v8.1.0)

**Duration:** September 1, 2026

Extends the IPSC competitor module completed in v8.0.0 with bulk CSV import, then — while building and testing it —
uncovers and fixes a subtle Jackson gotcha affecting every `@JsonProperty(required = true)` field added across the
IPSC request models to date: without a matching `@JsonCreator` constructor, the annotation never actually fires.

**Key Accomplishments:**

**Competitor Bulk CSV Import**

- `IpscCompetitorController.createCompetitors` (`POST /ipsc/competitors/bulk`, consumes `text/csv`) parses CSV data
  into `CompetitorRequestForCSV` rows and creates each competitor via the existing `createCompetitor` logic — unlike
  `AwardController`/`ImageController`'s bulk endpoints, which only build response objects without persisting
- New `CompetitorRequestForCSV` (CSV-mapped, `UpperCamelCase` headers) and `CompetitorResponseHolder` models

**Required-Field Enforcement Fix**

- Root cause: `@JsonProperty(required = true)` only fires for creator (constructor) parameters — a class deserialised
  via its default no-args constructor and setters silently accepts a missing "required" field as `null`
- `CompetitorRequestForCSV`, `CompetitorRequest`, `MatchRequest`, `MatchStageRequest`, `MatchOverallScoresRequest`/
  `MatchStageScoresRequest` and their CSV variants each gained a `@JsonCreator` constructor with every parameter
  bound via `@JsonProperty`, replacing their Lombok `@AllArgsConstructor`
- `CompetitorRequest`'s required third field corrected from `competitorNumber` to `clubNumber`, matching
  `IpscCompetitorServiceImpl.validateForCreate`'s actual validation
- The scores CSV variants' constructors now match their plain counterparts' signatures exactly, verified via a
  `csvMapper.addMixIn(...)` mixin test — the same pattern `AwardServiceImpl`/`ImageServiceImpl` already use

**Architecture Highlights:**

- Confirms `MatchOverallScoresRequest`/`MatchStageScoresRequest` remain groundwork — their constructors and
  annotations are now correct, but neither is wired into a controller nor service yet

**Technical Focus:**

- Bulk data import (competitor CSV)
- Jackson deserialisation correctness (`@JsonCreator`/`@JsonProperty(required = true)`)
- Request-model test coverage

**Test Coverage:**

- New unit tests across `IpscCompetitorController`/`Service`/`ServiceImpl`'s bulk import, and eight request-model
  test classes (`CompetitorRequestTest`, `CompetitorRequestForCSVTest`, `MatchRequestTest`, `MatchStageRequestTest`,
  `MatchOverallScoresRequestTest`, `MatchStageScoresRequestTest`, `MatchOverallScoresRequestForCSVTest`,
  `MatchStageScoresRequestForCSVTest`)

---

### Phase 19: IPSC Module Completion — Competitor & Match CRUD (v8.0.0)

**Duration:** August 31, 2026

Completes the IPSC module rebuild that v6.0.0 through v7.4.0 laid groundwork for: `IpscController`'s empty stub is
replaced by two full CRUD controllers backed by new services and DTOs, alongside a comprehensive Javadoc/`@since`
documentation pass and a migration of the project's AI-agent tooling from slash commands to Skills.

**Key Accomplishments:**

**IPSC Competitor & Match CRUD**

- `IpscCompetitorController`/`IpscMatchController` — full CRUD (`create`/`update`/`patch`/`get`, plus `getAllMatches`)
  on `/ipsc/competitors`/`/ipsc/matches`, following the project's action-named REST method convention
- `IpscCompetitorService`/`IpscMatchService` + impls resolve club/gender/firearm-type/match-category by name (404/400
  via the existing exception hierarchy), map requests to/from the existing `Competitor`/`IpscMatch`/`IpscMatchStage`
  entities and persist via the existing repositories; `patchMatch` upserts stages by stage number rather than
  replacing the whole list
- New `CompetitorRequest`/`CompetitorResponse` and `MatchResponse`/`MatchStageResponse` DTOs

**Gender Enum & Persistence**

- `Gender` gains `name`/`abbreviation` fields, a case-insensitive `fromName()` factory and a `toString()` override,
  bringing it in line with the project's other enums
- New `GenderConverter` (`AttributeConverter<Gender, String>`), wired onto `Competitor.gender` via `@Convert`

**Rename & Consistency Sweep**

- `AwardService`/`ImageService.processCsv` renamed to `createAwards`/`createImages`; their bulk CSV endpoints moved to
  `/awards/bulk`/`/images/bulk` and now return `201 Created`
- `getByName`/`getByAbbreviation`/`getByCode`/`getByAbbreviationOrName` factory methods renamed to `fromX` across
  `ClubIdentifier`, `CompetitorCategory`, `Division`, `FirearmType`, `MatchCategory` and `PowerFactor`

**Documentation & Tooling**

- Comprehensive Javadoc/`@since` pass across models, converters, exceptions, utils, constants and `ControllerAdvice`
- `AGENTS.md`/`CLAUDE.md` merged into a single tool-agnostic reference; new line-wrapping, extended Arrange-Act-Assert
  and test-helper-placement conventions
- The project's AI-agent tooling migrated from `.claude/commands/*.md` slash commands to `.claude/skills/*/SKILL.md`
  Skills; `generate-pr-description` now runs `sync-unreleased-changes` as a prerequisite step
- Qodana JVM static analysis re-added (`qodana.yaml`)

**Architecture Highlights:**

- `IpscController`'s empty stub retired — the IPSC module now has real, resource-oriented competitor and match CRUD
  endpoints, completing work begun as groundwork back in v6.0.0
- `models/ipsc/request` split into `models/ipsc/match/request`/`models/ipsc/scores/request`, matching the module's
  per-concern shape; competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains
  groundwork, not yet consumed by any controller

**Technical Focus:**

- IPSC domain-layer completion (competitor/match CRUD)
- Consistency (naming, Javadoc coverage, AI-agent tooling)
- Static analysis integration (Qodana)

**Test Coverage:**

- New unit and integration test coverage for `IpscCompetitorController`/`Service`/`ServiceImpl` and
  `IpscMatchController`/`Service`/`ServiceImpl`, plus `GenderTest`/`GenderConverterTest` — the largest single-release
  test expansion since v5.4.0

---

### Phase 18: Documentation Reflow & Historical Narrative Additions (v7.4.1)

**Duration:** August 29, 2026

A documentation-only patch release: no domain-model, API or test-behaviour change. Rewraps the entire root-level
documentation set to a consistent line width and extends `HISTORY.md`'s own narrative sections.

**Key Accomplishments:**

**Documentation Reflow**

- `AGENTS.md`, `ARCHITECTURE.md`, `CLAUDE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, `HISTORY.md`, `README.md` and
  `RELEASE_NOTES.md` rewrapped to a consistent ~120-character line width — prose, list items and table columns
  realigned, matching `CLAUDE.md`'s pre-existing wrap width
- A handful of incidental copyedits surfaced along the way, including a fix to AGENTS.md's own serial-comma rule
  example, which had previously violated the rule it describes

**Historical Narrative Additions**

- New "Major Version Goals" subsection under Project Philosophy Evolution, summarising the driving goal behind each
  major version line (4.x, 5.x, 6.x, 7.x)
- New "Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)" entry, capturing the test-convention,
  documentation-accuracy and AI-agent-tooling work spanning those three releases

**Build & Metadata**

- Project version bumped to 7.4.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Test Coverage:**

- No test changes — this release touches only Markdown documentation and version metadata

**Architecture Highlights:**

- No architectural change

**Technical Focus:**

- Documentation consistency (line width, table alignment)
- Historical-record completeness (major version goals, process-discipline narrative)

---

### Phase 17: IPSC Request DTOs, Route Clean-up & Documentation Conventions (v7.4.0)

**Duration:** August 29, 2026

A mixed release: new IPSC request DTOs laid as groundwork for the module rebuild, a small API clean-up and a round of
documentation-convention tightening applied across the existing docs.

**Key Accomplishments:**

**IPSC Request DTOs**

- New `za.co.hpsc.web.models.ipsc.request` package — `MatchRequest`/`MatchStageRequest`/`MatchStagesRequest` for
  match/stage submission and `MatchOverallResultRequest`/`MatchStageResultRequest` for competitor result submission,
  shaped to match Practiscore's export format
- `MatchOverallResultRequestForCSV`/`MatchStageResultRequestForCSV` abstract CSV variants of the result request DTOs;
  `MatchRequest` gains a `matchId` field for updating an existing match (previously creation-only)
- New shared `za.co.hpsc.web.models.ipsc.shared` package — `IpscCommonScore` (fields shared by Comstock-scored,
  hit-factor IPSC results), `IpscMatchScore` (adds `percentageOfPossiblePoints`) and `IpscMatchStageScore` (adds
  `rawPoints`/`hitFactor`)
- All new DTOs carry field- and class-level Javadoc documenting how Comstock scoring works; not yet wired to
  `IpscController`, which remains an empty stub

**API Route Clean-up**

- `AwardController`/`ImageController` route prefixes dropped from `/v1/awards`/`/v1/images` to `/awards`/`/images` — the
  unused `/v1` API versioning segment removed

**Documentation Conventions**

- New AGENTS.md Serial commas rule — lists of three or more items no longer take a comma before the final `and`/`or`
- AGENTS.md's British English rule tightened to cover code identifiers (class/method/variable names) as well as prose,
  dropping the previous exception
- Both rules applied retroactively across `CLAUDE.md`, `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md`,
  `CHANGELOG.md`, `HISTORY.md` and the Claude Code command files; the identifier-spelling sweep surfaced and corrected
  two American-spelled test method names (`Initializes`→`Initialises`, `Recognized`→`Recognised`) across `RequestTest`,
  `ResponseTest`, `AwardRequestForCSVTest` and `ImageResponseTest`
- `documentation/roadmap/`'s `IMPROVEMENT_PLAN.md`/`TASKS.md` renamed to `improvement-plan.md`/
  `improvement-plan-tasks.md` for kebab-case consistency with the rest of the tooling docs

**AI-Agent Tooling & Process**

- New Claude Code command `/sync-unreleased-changes` — diffs the current branch against its base plus any uncommitted
  changes, cross-checks the result against `CHANGELOG.md`'s `[Unreleased]` section and fills in any missing entries
  directly in the file
- `RELEASE_NOTES.md`'s Contributors section now sourced from `git log`'s unique commit authors (bots included) instead
  of a generic placeholder, per a new AGENTS.md Release Checklist rule

**Release Hygiene**

- `log4j-api` overridden `2.25.4` → `2.25.5`, closing CVE-2026-49844 — a transitive dependency via
  `spring-boot-starter-logging`, never actually reachable since this project uses Logback, but the pin removes the
  flagged advisory
- `.gitignore`/`.aiignore` refreshed from the latest upstream templates; `README.md`'s H1 heading restored (lost in an
  earlier commit)

**Build & Metadata**

- Project version bumped to 7.4.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Test Coverage:**

- No dedicated new unit test coverage was added for the new IPSC request DTOs in this pass — they're groundwork, not yet
  exercised by any controller/service; verified via `./mvnw clean compile` and the existing suite passing unchanged
- The four renamed test methods keep their existing coverage — only the names changed, no behaviour or assertions
  touched

**Architecture Highlights:**

- No architectural change — the new DTOs extend the existing `models/ipsc/` package structure without altering the
  layered architecture; `IpscController` remains an empty stub

**Technical Focus:**

- IPSC domain-layer groundwork (request DTOs, Comstock-scoring shared fields)
- API surface cleanup (route prefix)
- Documentation-convention consistency (serial commas, identifier spelling, file naming)

---

### Phase 16: Test Suite Conventions, AI-Agent Tooling and Dependency Maintenance (v7.2.0)

**Duration:** August 25, 2026

A process-and-tooling release with no domain-model or API surface changes: formalises test-file conventions, closes
coverage gaps identified by JaCoCo, adds two new Claude Code scaffolding commands and upgrades the Spring Boot parent.

**Key Accomplishments:**

**Test Coverage & Structure**

- `services/AwardServiceTest`/`services/ImageServiceTest` — new Mockito-based interface-contract unit tests, exercising
  `createAwards` through the `AwardService`/`ImageService` interface type rather than the impl class
- Four JaCoCo-identified coverage gaps closed: `ControllerResponse(boolean, String)` and the
  derived-success-from-error-presence branch of `ControllerResponse(LocalDateTime, String, String)`;
  `FirearmType.toString()` for both enum-constructor shapes; `ControllerAdvice.logError`'s null-throwable, wrapped-cause
  and null-`WebRequest` branches (this class's branch coverage went 92% → 100%). Overall suite coverage rose from
  95.7%/91.7% to 97.3%/98.1% (line/branch)
- `HpscWebApplicationTests` renamed to `HpscWebApplicationTest` to match the project's `<ClassName>Test` naming
  convention
- 26 existing test files retrofitted with a new AGENTS.md test convention: a one-line `// methodName()` header before
  each method's test group, ordered constructors → public → protected → alphabetical by name → `toString()` last — no
  test behaviour changed, purely comments and reordering

**AI-Agent Tooling**

- `/scaffold-unit-tests` migrated from a stale `.github/prompts/scaffold-unit-tests.prompt.md` that referenced a
  different project's package and an invented "Layer 1/2/3" test pattern; corrected to this repo's real interface/impl
  test split
- New `/scaffold-integration-tests`, `@SpringBootTest`-based, following `AwardServiceIntegrationTest`/
  `ImageServiceIntegrationTest` as the template
- Both commands defer to their loaded `AGENTS.md`/`CLAUDE.md` rather than restating conventions inline, accept multiple
  targets per invocation and never commit on their own
- `AwardServiceIntegrationTest`/`ImageServiceIntegrationTest` now exclude datasource/JPA/messaging autoconfiguration,
  since neither service touches the database

**Dependency Maintenance**

- Spring Boot parent upgraded `4.0.7` → `4.1.0`; now-redundant `pom.xml` version overrides cleaned up
  (`spring-framework.version`/`tomcat.version` now match Boot's own defaults; a long-standing `commons.lang3.version`
  typo — Boot's real property is hyphenated — removed; `maven-dependency-plugin` pin removed, now Boot-managed)
- flyway-maven-plugin's separately-pinned `flyway-mysql` bumped `11.14.1` → `12.4.0` to match Boot's newly-managed
  `flyway.version` — plugin-scoped dependencies don't inherit Boot's dependency management, so this now needs manual
  sync on every future parent bump, documented inline in the POM

**Documentation & Process**

- New CLAUDE.md Git Workflow section states the branching model's PR targets directly (`feature/*` → `develop`;
  `release/vX.Y.Z`/`hotfix/*` → `main`); AGENTS.md/CONTRIBUTING.md's develop-first rule gains a "for testing before they
  ship" clarification
- CLAUDE.md now cross-links to AGENTS.md and corrects its package-overview table (`ControllerAdvice` lives in
  `configs/`, not `exceptions/`)
- A false claim that AssertJ is used for assertions (it is explicitly excluded from `spring-boot-starter-webmvc-test` in
  `pom.xml`) removed from AGENTS.md, CLAUDE.md, README.md, ARCHITECTURE.md and CONTRIBUTING.md

**Build & Metadata**

- Project version bumped to 7.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Test Coverage**

- No dedicated new domain/repository/controller test coverage was needed (none of those layers changed); verified via
  the full test suite (492 tests, up from 483 at the start of this release), `./mvnw verify -Pcoverage` and manual
  Flyway commands (`flyway:info`/`flyway:migrate`) against a real local MySQL 9.5 dev database

**Architecture Highlights:**

- No architectural change — this release is entirely process, tooling and dependency maintenance, keeping the test suite
  and AI-agent conventions consistent ahead of future feature work

**Technical Focus:**

- Test-suite consistency and coverage completeness
- AI-agent tooling accuracy (correcting a migrated command that referenced the wrong project)
- Dependency currency and Maven POM hygiene

---

### Phase 15: Shooter Log Refinement (v7.1.0)

**Duration:** August 24, 2026

A focused follow-up to the v7.0.0 shooter-log data model, correcting its scope (power factor) and its name
(`ShooterLogEntry` → `ShooterLogCompetitor`) before any calculation service is built on top of it.

**Key Accomplishments:**

**Shooter Log Rename & Rescoping**

- `ShooterLogEntry` renamed to `ShooterLogCompetitor` — the entity is a per-competitor snapshot row, not a generic log
  entry and the new name says so
- `ShooterLog.powerFactor` (`PowerFactor`, via the existing `PowerFactorConverter`, not nullable) — the best-4-match
  calculation is now scoped by power factor as well as firearm type
- `ShooterLogCompetitor.points` (nullable) — records the points each contributing `MatchCompetitor` row contributed to
  the snapshot's `logValue`
- `ShooterLogCompetitor.match` (`@ManyToOne IpscMatch`, not nullable) — a direct match reference alongside the existing
  `matchCompetitor` link
- `ShooterLogRepository.findAllByCompetitorIdAndFirearmType` renamed to
  `findAllByCompetitorIdAndFirearmTypeAndPowerFactor`

**Repository & Migration**

- New `ShooterLogCompetitorRepository` (`findAllByShooterLogId`) supersedes `ShooterLogEntryRepository`
- `V7_1_0__update_shooter_log_schema.sql` renames the table (and its unique index/FKs) and adds the new columns — both
  `shooter_log` and `shooter_log_competitor` remain empty in every environment, so the migration needed no backfill

**Tooling & Process**

- AI agent prompt files migrated from `.github/prompts/*.prompt.md` to `.claude/commands/*.md`
- `AGENTS.md` adopts the GitFlow branching model; `CONTRIBUTING.md` added for new-developer onboarding

**Build & Metadata**

- Project version bumped to 7.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Test Coverage**

- No dedicated new unit/integration test coverage added for the renamed/extended entity in this release — consistent
  with v7.0.0, `shooter_log`/`shooter_log_competitor` remain schema-only pending a calculation service

**Architecture Highlights:**

- Confirms the v7.0.0 decision that shooter logs are persisted snapshots, not a live view, by scoping them correctly
  (power factor) before any consumer is built against the schema

**Technical Focus:**

- Naming accuracy and schema correctness ahead of the shooter-log calculation service
- Continued domain-layer groundwork, deferring service/controller wiring to a future release

---

### Phase 14: Match Results, Visitor Tracking & Shooter Log Data Model (v7.0.0)

**Duration:** August 11, 2026

Extended the IPSC domain model to support club-scoped match results, match visitor tracking and a persisted shooter-log
ranking, promoting six entities parked under `domain/old/` back into the live domain package and pairing them with a
fully rebuilt repository layer.

**Key Accomplishments:**

**Domain Promotion & `domain/old/` Retirement**

- Six entities (`Club`, `Competitor`, `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`, `MatchStageCompetitor`) promoted
  from `za.co.hpsc.web.domain.old` back into `za.co.hpsc.web.domain`
- `.old` package removed entirely

**Club-Scoped Results & Visitor Tracking**

- `Club.identifier` (`ClubIdentifier`, via `ClubIdentifierConverter`, unique) ties a club row to HPSC/SOSC/PMPSC
- `Competitor.homeClub` — nullable `@ManyToOne Club` relation for home-club membership
- `MatchCompetitor.matchRanking` renamed `overallRanking`; new `clubRanking` for same-club ranking per firearm type; new
  `isVisitor` flag (`true` when `matchClub` differs from the host match's club)
- Visitors modelled relationally — not as a fourth club row
- New unique constraint on `MatchCompetitor`: `(competitor_id, match_id, firearm_type)`

**Per-Stage Results Repointed to `MatchCompetitor`**

- `MatchStageCompetitor` FK changed from `competitor` to `matchCompetitor`, so a stage score attaches to the specific
  firearm-type entry rather than duplicating `competitorCategory`/`division`/`firearmType`/ `powerFactor`/`matchClub`
  fields
- New unique constraint: `(match_competitor_id, match_stage_id)`
- `IpscMatchStage` gains a new unique constraint: `(match_id, stage_number)`

**Shooter Log Persistence**

- New `ShooterLog` entity — competitor, club, firearmType, `logValue` (`BigDecimal(19,6)`, average of the best 4 match
  scores), `calculatedDate`
- New `ShooterLogEntry` entity — links a `ShooterLog` snapshot to the contributing `MatchCompetitor` rows via
  `rankInLog` (1–4); unique constraint `(shooter_log_id, match_competitor_id)`
- Persisted as point-in-time snapshots rather than a live view — no calculation job/service yet

**Repository Layer Rebuild**

- `repositories/` package (emptied in preparation for this rework) rebuilt from scratch with 8 new `JpaRepository`
  interfaces: `ClubRepository`, `CompetitorRepository`, `IpscMatchRepository`, `IpscMatchStageRepository`,
  `MatchCompetitorRepository`, `MatchStageCompetitorRepository`, `ShooterLogRepository`, `ShooterLogEntryRepository`

**No New Enums or Converters**

- `ClubIdentifier` and `FirearmType`, with their existing `AttributeConverter`s, are reused as-is

**Build & Metadata**

- Project version bumped to 7.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Test Coverage**

- `./mvnw clean compile` succeeds for all 8 entities and 8 repositories
- `HpscWebApplicationTests` — Spring context boots against H2 (`ddl-auto=create-drop`); Hibernate builds the schema for
  all 8 entities, validating every `@JoinColumn`, converter and unique constraint (1/1 passing)
- No dedicated new unit/integration test coverage added for the new/changed domain model in this release

**Statistics**

- 1 commit
- 15 files changed
- +207 insertions
- -30 deletions
- Net: +177 lines

**Architecture Highlights:**

- Overall vs. club results live on the same `MatchCompetitor` row rather than a separate `MatchResult` table
- Per-stage results repointed from `Competitor` to `MatchCompetitor` to support multiple firearm-type entries per
  competitor per match
- Shooter logs are persisted snapshots, trading a not-yet-built recalculation step for historical stability

**Technical Focus:**

- Club-scoped and visitor-aware match results
- Persisted shooter-log data model
- Domain-layer groundwork ahead of service/controller wiring
- Repository layer rebuilt around the promoted domain model

---

### Phase 13: Dedicated Match CRUD API & Service Encapsulation (v6.0.0)

**Duration:** May 1, 2026

Introduced a versioned, resource-oriented match management API, completed the entity service encapsulation layer and
restructured all IPSC model packages for long-term growth.

**Key Accomplishments:**

**Dedicated Match CRUD API**

- **`IpscMatchController`** introduced at `/v2/ipsc/matches` (134 lines) with full CRUD:
    - `POST` — create a new IPSC match
    - `PUT {matchId}` — fully replace an existing match
    - `PATCH {matchId}` — partially update an existing match
    - `GET {matchId}` — retrieve a match by ID
    - All operations return `ResponseEntity<MatchOnlyResponse>` with typed OpenAPI annotations
- **`IpscMemberController`** stub registered at `/ipsc/member` for future member management

**IpscMatchService Layer**

- **`IpscMatchService` interface** (22 lines) — dedicated match CRUD contract:
    - `insertMatch`, `updateMatch`, `modifyMatch`, `getMatch` — all return `Optional<MatchOnlyResponse>`
- **`IpscMatchServiceImpl`** (135 lines) — full implementation backed by `DomainService` and `TransactionService`

**Match-Specific Model Layer**

- `MatchOnlyDto` (82 lines) — lightweight match DTO; auto-resolves `FirearmType` and stamps `dateEdited` on init
- `MatchOnlyRequest` (49 lines) — JSON request body for match CRUD operations
- `MatchOnlyResponse` (83 lines) — response envelope returned by `IpscMatchController`
- `MatchOnlyResultsDto` (18 lines) — internal results holder
- `MatchSearchRequest`, `MatchSearchDateRequest`, `MatchSearchIdRequest` — future search support

**DomainServiceImpl — Repository Decoupling**

- Removed direct injection of all six JPA repositories from `DomainServiceImpl`
- All data access operations are delegated to the entity service layer:
    - `ClubEntityService`, `CompetitorEntityService`, `MatchEntityService`
    - `MatchStageEntityService`, `MatchCompetitorEntityService`, `MatchStageCompetitorEntityService`
- New entity service methods: `findClubById`, `findCompetitorById`, `findMatchStageCompetitorById`

**IPSC Model Package Restructuring**

- All `models/ipsc/` classes promoted to `models/ipsc/common/` sub-package
- New sibling `models/ipsc/match/` sub-package for match-only models
- Old flat `models/ipsc/response/` (`ClubResponse`, `MatchResponse`) replaced by `models/ipsc/common/response/`
  counterparts

**IpscUtil — String Formatting Utility**

- `IpscUtil` (66 lines): `clubTostring`, `matchToString` — centralises `"Match @ Club (ABBR)"` display-string
  construction used across the match and club DTOs

**TransformationService Updates**

- `mapMatchOnly(MatchOnlyRequest)` method added for the match CRUD pipeline
- `mapMatchResults` no longer declares `throws ValidationException`

**Enhanced Logging & Error Handling**

- Structured logging added to all `ControllerAdvice` exception handlers (119 lines changed)
- `ValidationException` removed from handler method signatures

**Build & Metadata**

- Spring Boot upgraded 4.0.5 → 4.0.6
- MIT Licence, developer profile and SCM connection added to `pom.xml`
- `logback-spring.xml` updated with additional logger configuration

**Test Coverage**

- **New (8 classes, ~1,300 lines):** `IpscMatchControllerTest`, `IpscMatchServiceTest`, `IpscMatchIntegrationTest`,
  `MatchOnlyDtoTest`, `MatchOnlyRequestTest`, `MatchOnlyResponseTest`, `MatchResponseTest`, `IpscUtilTest`
- **Updated:** `TransformationServiceTest` (+747 lines), `DomainServiceTest` (+247 lines), `TransactionServiceTest`
  (+246 lines), `ValueUtilTest` (+294 lines)
- **Removed:** `IpscControllerTest` (156 lines — superseded by `IpscMatchControllerTest`)

**Statistics**

- 40 commits
- 165 files changed
- +6,779 insertions
- -3,501 deletions
- Net: +3,278 lines

**Architecture Highlights:**

- Dedicated `/v2/ipsc/matches` API separate from the bulk-import flow
- `DomainServiceImpl` no longer reaches past entity services to repositories
- `models/ipsc/common/` + `models/ipsc/match/` provide clear model homes as the domain grows
- `IpscUtil` centralises display-string logic previously scattered across DTOs

**Technical Focus:**

- Versioned match management API
- Service layer encapsulation and repository decoupling
- Package restructuring for domain growth
- Continued test coverage expansion

---

### Phase 12: Competitor Enrolment & Service Transformation (v5.4.0)

**Duration:** April 26, 2026

The most extensive single-release test expansion in the project's history, alongside competitor enrolment support, a
major service renaming and CI/CD quality gate integration.

**Key Accomplishments:**

**Competitor Enrolment & Members CRUD**

- `EnrolledCompetitorDto` introduced (138 lines) for tracking enrolled competitors through the processing pipeline
- Competitor SAPSA number validation via `IpscUtil` (max number check)
- Duplicate competitor filtering in `CompetitorDto` by SAPSA number and ID
- Updated ICS alias and competitor number constants in `IpscConstants`

**Service Transformation Architecture**

- `IpscMatchService` renamed to `TransformationService` for improved semantic clarity
- `TransformationServiceImpl` introduced (1,098 lines) replacing `IpscMatchServiceImpl` (867 lines removed)
- `MatchHolder` data class (23 lines) introduced for match data encapsulation
- `MatchCompetitorEntityService` updated to return lists for bulk retrieval
- `MatchStageCompetitorEntityService` enhanced with list-based retrieval

**ClubIdentifier Enhancement**

- Abbreviation field added to `ClubIdentifier` enum (38 lines changed)
- `ClubIdentifierConverter` updated to use abbreviation for database persistence
- `DomainServiceImpl` updated to use abbreviation for club lookup

**Model Package Restructuring**

- `domain` package renamed to `data`: `DtoMapping`, `DtoToEntityMapping`, `EntityMapping` relocated
- Holders reorganised: `MatchResultsDto`, `MatchResultsDtoHolder` → `holders/dto`; new `IpscMatchRecordHolder`
- Records restructured: `CompetitorMatchRecord` → `CompetitorRecord`; new `CompetitorResultRecord`,
  `MatchCompetitorOverallResultsRecord`, `MatchCompetitorStageResultRecord`

**Comprehensive Test Suite Expansion**

- 20+ new test classes, ~7,000 lines of new test code — the largest single-release test expansion
- New controller tests: `AwardControllerTest`, `ImageControllerTest`, `IpscControllerTest`, `ControllerAdviceTest`
- New converter tests: all 6 JPA attribute converters now have dedicated test classes
- New domain entity tests: `ClubTest`, `CompetitorTest`, `IpscMatchTest`, `IpscMatchStageTest`, `MatchCompetitorTest`,
  `MatchStageCompetitorTest`
- New exception tests: `FatalExceptionTest`, `NonFatalExceptionTest`, `ValidationExceptionTest`
- New integration tests: `AwardServiceIntegrationTest`, `ImageServiceIntegrationTest`,
  `DtoToEntityMappingIntegrationTest`
- New service tests: `TransformationServiceTest` (1,026 lines), `MatchCompetitorDtoTest`
- Removed: `IpscMatchServiceTest` (10,076 lines — service renamed)

**CI/CD & Code Quality**

- Qodana JVM linter configured in `qodana.yaml` (`jetbrains/qodana-jvm`)
- JaCoCo 0.8.14 coverage profile added to `pom.xml`; reports to `/coverage` directory
- `code_quality.yml` enhanced with extended branch patterns and dependency installation step
- `qodana.yml` removed (duplicate); `.aiignore` file added

**Bug Fixes**

- PCC Optics division constant value corrected
- `ControllerAdvice` error handling improved
- `ClubIdentifierConverter` updated to use abbreviation for persistence
- Unused firearm type assignment removed
- Spring Framework version stabilised from 7.0.8 to 7.0.7

**Statistics**

- ~75 commits
- 123 files changed
- +12,713 insertions
- -13,358 deletions
- Net: -645 lines

**Architecture Highlights:**

- `TransformationService` replacing `IpscMatchService` for semantic clarity
- `MatchHolder` encapsulating match data
- List-based returns from `MatchCompetitorEntityService`
- Qodana static analysis and JaCoCo coverage gates in CI/CD

**Technical Focus:**

- Competitor enrolment and SAPSA validation
- Service renaming and semantic clarity
- Comprehensive test suite expansion across all layers
- CI/CD quality automation

---

### Phase 11: Service Consolidation & Type Safety (v5.3.0)

**Duration:** March 15, 2026

Focused consolidation of services, introduction of custom JPA converters and repository query optimisation.

**Key Accomplishments:**

**Custom JPA Attribute Converters**

- Six new `AttributeConverter` implementations replacing `@Enumerated(EnumType.STRING)`:
    - `ClubIdentifierConverter`, `CompetitorCategoryConverter`, `DivisionConverter`
    - `FirearmTypeConverter`, `MatchCategoryConverter`, `PowerFactorConverter`
- Explicit, testable conversion logic per enum type
- No data migration required; column values are unchanged

**Service Consolidation**

- **`IpscMatchResultService` removed:** Interface and `IpscMatchResultServiceImpl` (379 lines) fully deleted
    - Match result initialisation consolidated into `DomainService`
    - Score and competitor processing moved to `IpscMatchService`
- **`ScoreDto` removed:** 50 lines; score data handled directly via `ScoreResponse`
- **`ClubEntityService` simplified:** Reduced to single `findClubByNameOrAbbreviation` method

**DtoMapping as Java Record**

- Transitioned `DtoMapping` from mutable class to immutable Java record
- Compact record constructor simplifying initialisation
- Streamlined test setup with cleaner transaction stubbing

**JPA Entity Relationship Corrections**

- Added `mappedBy` to all bidirectional `@OneToMany` relationships across entity hierarchy:
    - `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`, `MatchStageCompetitor`
- Fixed cascade type configurations for correct entity lifecycle management
- Added detailed Javadoc for `IpscMatchStage.init()` method

**Repository Query Optimisation**

- Added the scheduled date to match retrieval for uniqueness constraints
- Optimised competitor retrieval using `Set` for deduplication and performance
- Removed unnecessary fetch joins across repository methods
- Improved null handling in match stage competitor retrieval

**Service Layer Refinement**

- **DomainServiceImpl:** 270 lines changed – enhanced `initMatchEntities` with Javadoc; improved null handling
- **IpscMatchServiceImpl:** 546 lines changed – consolidated match results processing; removed commented-out code
- **TransactionServiceImpl:** 22 lines changed – improved null handling and list initialisation

**Test Suite Overhaul**

- **DomainServiceTest:** 787 lines added – comprehensive `initMatchEntities` coverage
- **IpscMatchServiceTest:** 3,156 lines changed – comprehensive consolidation with helper methods
- **TransactionServiceTest:** 1,031 lines changed – `getFirst()` assertions; enabled disabled tests
- **IpscServiceIntegrationTest:** 113 lines changed – integration tests for `importWinMssCabFile`
- **Removed:** `IpscMatchResultServiceTest` (1,802 lines), `ScoreDtoTest` (643 lines)

**Spring Boot Upgrade**

- Updated from Spring Boot 4.0.3 to 4.1.0-SNAPSHOT
- Added Spring Snapshots repository configuration

**Statistics**

- ~45 commits
- 59 files changed
- +5,686 insertions
- -4,613 deletions
- Net: +1,073 lines

**Architecture Highlights:**

- Custom JPA converters for type-safe enum persistence
- Consolidated service boundaries
- Immutable DtoMapping record
- Correct bidirectional JPA relationships

**Technical Focus:**

- Service consolidation and simplification
- Type-safe JPA attribute conversion
- Repository query accuracy and performance
- Test suite refinement

---

## 🎯 Major Milestones

### Milestone 1: Project Foundation (v1.0.0)

- Initial Spring Boot application
- Image gallery CSV processing
- Basic API infrastructure
- Custom exception hierarchy

**Achievement:** Established the foundation for the HPSC platform with core image processing capabilities.

---

### Milestone 2: Feature Expansion (v1.1.0 - v1.1.3)

- Award processing system
- OpenAPI documentation
- Comprehensive project documentation
- Code quality standards

**Achievement:** Expanded platform features and established professional documentation standards.

---

### Milestone 3: Architectural modernisation (v2.0.0)

- Service-oriented architecture
- Comprehensive DTO layer
- Modular service design
- Transaction management

**Achievement:** Transformed from monolithic to modular architecture enabling better maintainability and testing.

---

### Milestone 4: Domain Specialisation (v3.0.0)

- IPSC-specific domain modelling
- Firearm-type classification
- Club entity reintroduction
- Comprehensive enum utilities

**Achievement:** Aligned domain model with IPSC standards for specialised shooting competition management.

---

### Milestone 5: Quality & Simplification (v3.1.0)

- Exception handling consolidation
- API documentation accuracy
- Error handling consistency
- Simplified architecture

**Achievement:** Improved code quality and simplified error handling while maintaining functionality.

---

### Milestone 6: Domain Clarity (v4.0.0)

- Entity naming clarification
- Comprehensive test coverage
- Enhanced validation layers
- IPSC entity specialisation

**Achievement:** Clarified domain model through explicit entity naming (Match → IpscMatch) improving code clarity.

---

### Milestone 7: Feature Completeness (v4.1.0)

- Full CRUD operations
- Complete API maturity
- Transactional consistency
- Production readiness

**Achievement:** Completed CRUD lifecycle enabling full data management capabilities.

---

### Milestone 8: Standards Adoption (v5.0.0)

- Semantic versioning adoption
- Entity initialisation framework
- Response generation pipeline
- Infrastructure consolidation

**Achievement:** Adopted industry standards and consolidated infrastructure for long-term maintainability.

---

### Milestone 9: Test Quality Enhancement (v5.1.0)

- Test suite reorganisation with 6 logical sections
- Duplicate test elimination
- Standardised test naming conventions
- Enhanced test documentation and readability

**Achievement:** Improved test infrastructure quality through comprehensive reorganisation and consolidation.

---

### Milestone 10: Architecture Refactoring (v5.2.0)

- Three-tier mapping system (DtoMapping, EntityMapping, DtoToEntityMapping)
- Enhanced match entity handling with MatchEntityService
- Comprehensive test consolidation (2,000+ lines across multiple suites)
- Enhanced null safety and code quality
- Major service refactoring (61 files, +13,567 lines)

**Achievement:** Significant architectural improvement with cleaner separation of concerns, enhanced null safety and
comprehensive test coverage across all services and utilities.

---

### Milestone 22: Competitor Multi-Email Support & Bulk CSV Separator Standardisation (v8.2.0)

- `Competitor.emailAddress` (`String`) replaced with `emailAddresses` (`List<String>`), backed by a new
  `competitor_email` child table and a backfilling Flyway migration
- New `SystemConstants.ARRAY_SEPARATOR` unifies `AwardServiceImpl`/`ImageServiceImpl`'s bulk CSV parsing with the
  competitor domain's semicolon-separated multi-value convention
- Qodana static analysis removed after a roadmap audit found it had been failing on every CI run since v8.1.1;
  `improvement-plan.md`'s Gap #7 closed as not applicable

**Achievement:** Extended the competitor domain to support more than one email address, closed a lingering
inconsistency between the competitor and award/image bulk CSV endpoints' multi-value cell formats, and removed a
CI quality gate that had never once succeeded.

---

### Milestone 21: CI Static Analysis, Release-Process Self-Maintenance & Coverage Regression Fixes (v8.1.1)

- New `.github/workflows/qodana.yml` completes a CI quality gate that had sat configured but unwired since v8.0.0
- New `update-improvement-plan-gaps`/`sync-improvement-plan-gaps` skills formalise roadmap-gap maintenance;
  `generate-pr-description` renamed to `prep-version-release` and now runs both as its first step
- Recreated a deleted exception-hierarchy test suite and closed several other coverage gaps, taking the suite from
  92.9%/93.4% to 98.34%/98.84% (line/branch)
- Spring Boot bumped `4.1.0` → `4.1.1`; three now-redundant dependency overrides dropped, one found by the
  recurring dependency-currency check itself

**Achievement:** Closed a real, silent test-coverage regression, completed a CI quality gate the project's own
documentation had flagged as configured-but-unwired since v8.0.0, and built the tooling for the release process to
keep auditing its own roadmap documentation going forward — no new domain feature, but meaningful process maturity.

---

### Milestone 20: Competitor Bulk CSV Import & Required-Field Fixes (v8.1.0)

- `IpscCompetitorController.createCompetitors` (`POST /ipsc/competitors/bulk`) persists competitors from CSV data,
  unlike `AwardController`/`ImageController`'s response-only bulk endpoints
- Discovered and fixed a Jackson gotcha affecting every IPSC request model to date: `@JsonProperty(required = true)`
  needs a matching `@JsonCreator` constructor to actually enforce anything
- `CompetitorRequest`, `CompetitorRequestForCSV`, `MatchRequest`, `MatchStageRequest` and the scores request models
  all gained a `@JsonCreator` constructor; `CompetitorRequest`'s required field corrected from `competitorNumber` to
  `clubNumber`
- New unit tests across the bulk-import feature and every touched request model

**Achievement:** Extended the IPSC competitor module with bulk CSV import, and — while testing it — found and fixed a
silent validation gap present across every `@JsonProperty(required = true)` field added since the required-field
pattern was first introduced.

---

### Milestone 19: IPSC Module Completion (v8.0.0)

- `IpscCompetitorController`/`IpscMatchController` full CRUD, replacing the long-standing empty `IpscController` stub
- New `IpscCompetitorService`/`IpscMatchService` + impls; new `Gender` enum capabilities and `GenderConverter`
- Comprehensive Javadoc/`@since` documentation pass; AI-agent tooling migrated from slash commands to Skills
- Largest test expansion since v5.4.0: full unit and integration coverage for both new controllers/services

**Achievement:** Completed the IPSC module rebuild begun as groundwork in v6.0.0 — the platform now has real,
resource-oriented competitor and match management, not just an empty stub.

---

### Milestone 18: Documentation Reflow & Historical Narrative Additions (v7.4.1)

- Entire root-level documentation set rewrapped to a consistent ~120-character line width
- New "Major Version Goals" and "Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)" narrative sections added
  to `HISTORY.md`

**Achievement:** Brought every root documentation file to a consistent line width and filled in two gaps in the
project's own historical narrative — no domain/service/architecture or test changes.

---

### Milestone 17: IPSC Request DTOs, Route Clean-up & Documentation Conventions (v7.4.0)

- New `models/ipsc/request`/`models/ipsc/shared` packages — `MatchRequest`/`MatchStageRequest`/`MatchStagesRequest`/
  `MatchOverallResultRequest`/`MatchStageResultRequest` (plus CSV variants) and `IpscCommonScore`/`IpscMatchScore`/
  `IpscMatchStageScore`, groundwork for the IPSC module rebuild
- `AwardController`/`ImageController` routes simplified from `/v1/awards`/`/v1/images` to `/awards`/`/images`
- New AGENTS.md Serial commas rule and a British English rule tightened to cover code identifiers, both applied across
  the existing documentation set; `documentation/roadmap/` renamed to kebab-case
- New `/sync-unreleased-changes` Claude Code command; `RELEASE_NOTES.md` Contributors now sourced from `git log`

**Achievement:** Laid IPSC request-DTO groundwork for the module rebuild while cleaning up a stale API route and
tightening the project's own documentation conventions — no domain/service/architecture changes.

---

### Milestone 16: Test Suite Conventions, AI-Agent Tooling and Dependency Maintenance (v7.2.0)

- 26 test files retrofitted with a new `// methodName()` header-comment/ordering convention; 4 JaCoCo-identified
  coverage gaps closed; suite coverage rose from 95.7%/91.7% to 97.3%/98.1% (line/branch)
- New `/scaffold-unit-tests` (corrected from a stale, wrong-project prompt) and `/scaffold-integration-tests` Claude
  Code commands
- Spring Boot parent upgraded `4.0.7` → `4.1.0`; redundant `pom.xml` version overrides cleaned up; `flyway-mysql` bumped
  to match Boot's newly managed Flyway version
- Verified via the full test suite (492 tests), `./mvnw verify -Pcoverage` and manual Flyway commands against a real
  MySQL dev database

**Achievement:** Brought the entire existing test suite into line with a newly formalised AGENTS.md convention, closed
every coverage gap JaCoCo could find and corrected/extended the AI-agent tooling — all without touching the domain
model, keeping the codebase consistent ahead of future feature work.

---

### Milestone 15: Shooter Log Refinement (v7.1.0)

- `ShooterLogEntry` renamed to `ShooterLogCompetitor` for naming accuracy
- `ShooterLog.powerFactor` scopes best-4-match snapshots by power factor as well as firearm type
- `ShooterLogCompetitor` gains `points` and a direct `match` reference alongside `matchCompetitor`
- `ShooterLogCompetitorRepository` supersedes `ShooterLogEntryRepository`; `ShooterLogRepository` finder renamed to
  include `PowerFactor`
- Repository AI-agent tooling migrated to `.claude/commands/*.md`; `AGENTS.md` adopts GitFlow; `CONTRIBUTING.md` added

**Achievement:** Corrected the naming and scope of the v7.0.0 shooter-log data model before any calculation service is
built against it, keeping the schema accurate ahead of the service/controller wiring still to come.

---

### Milestone 14: Match Results, Visitor Tracking & Shooter Log Data Model (v7.0.0)

- Six entities promoted from `domain/old/` back into the live `domain` package; `.old` package removed
- `Club.identifier`, `Competitor.homeClub`, `MatchCompetitor.clubRanking`/`isVisitor` support club-scoped results and
  relational visitor tracking
- `MatchStageCompetitor` repointed from `Competitor` to `MatchCompetitor` to support multiple firearm-type entries per
  competitor per match
- New `ShooterLog`/`ShooterLogEntry` entities persist best-4-match shooter-log snapshots
- `repositories/` package rebuilt from scratch with 8 new `JpaRepository` interfaces

**Achievement:** Extended the IPSC domain model with club-scoped results, visitor tracking and a persisted shooter-log
data model, restoring the six entities parked under `domain/old/` and pairing them with a complete repository layer —
domain-layer groundwork ahead of the service/controller/import-pipeline wiring still to come.

---

### Milestone 13: Dedicated Match CRUD API & Service Encapsulation (v6.0.0)

- `IpscMatchController` introduced at `/v2/ipsc/matches` with full CRUD (POST, PUT, PATCH, GET)
- `IpscMatchService` + `IpscMatchServiceImpl` added as dedicated match management service
- `DomainServiceImpl` fully decoupled from repositories — entity services used exclusively
- All IPSC models moved to `models/ipsc/common/`; `models/ipsc/match/` sub-package introduced
- `IpscUtil` added for centralised club/match display-string formatting
- Spring Boot upgraded 4.0.5 → 4.0.6; MIT licence and SCM metadata populated in `pom.xml`

**Achievement:** Established a versioned, resource-oriented match management API and completed the entity service
encapsulation layer, ensuring `DomainServiceImpl` respects the layered architecture described in CLAUDE.md. The IPSC
model package restructuring provides dedicated homes for shared and match-specific models as the domain grows.

---

### Milestone 12: Competitor Enrolment & Service Transformation (v5.4.0)

- `EnrolledCompetitorDto` introduced for enrolled competitor tracking through the IPSC pipeline
- `IpscMatchService` renamed to `TransformationService` for semantic clarity
- SAPSA number validation and competitor deduplication in `CompetitorDto`
- 20+ new test classes (~7,000 lines) — the largest single-release test expansion in project history
- Qodana JVM linting and JaCoCo code coverage integrated into the CI/CD pipeline

**Achievement:** Delivered the project's most comprehensive test suite expansion, introduced competitor enrolment
support and SAPSA validation, modernised the service naming for improved clarity and strengthened the CI/CD pipeline
with static analysis and code coverage quality gates.

---

### Milestone 11: Service Consolidation & Type Safety (v5.3.0)

- Six custom JPA attribute converters for type-safe enum persistence
- IpscMatchResultService and ScoreDto removed; functionality consolidated
- DtoMapping converted to Java record for immutability
- All bidirectional @OneToMany relationships corrected with mappedBy
- Repository queries optimised with Set deduplication and scheduled date constraints

**Achievement:** Focused service consolidation and type-safety improvements simplifying the service architecture,
correcting JPA entity relationships and improving repository query accuracy.

---

## 🏛️ Architectural Evolution

### v1.0.0: Monolithic Foundation

```
Controller → Service → Repository → Entity
         ↓
      Models
         ↓
   Exception Handlers
```

**Characteristics:**

- Single service for image processing
- Direct controller-service-repository flow
- Basic entity relationships
- Centralised exception handling

---

### v2.0.0: Modular Services

```
           Controller
              ↓
    ┌─────────┴──────────┐
    ↓                    ↓
WinMssService    MatchResultService
    ↓                    ↓
 Repository     TransactionService
    ↓                    ↓
 Entity        DomainServices
    ↓                    ↓
   DTOs          Models/DTOs
```

**Characteristics:**

- Specialised services for different domains
- DTO layer for data transfer
- Transaction abstraction
- Improved separation of concerns

---

### v3.0.0: Domain-Specific Models

```
       IPSC Controller
            ↓
    ┌───────┴────────┐
    ↓                ↓
IpscService    DomainService
    ↓                ↓
 Firearm      Club    Match    Stage
 Types        ↓        ↓        ↓
 (Enums)    Entity  Entity   Entity
    ↓         ↓        ↓        ↓
Repository  Repository
```

**Characteristics:**

- IPSC-specific domain modelling
- Firearm-type classification
- Club entity relationship
- Specialised enums for IPSC

---

### v4.0.0: Explicit IPSC Focus

```
       IpscController
            ↓
    ┌───────┴────────┐
    ↓                ↓
IpscMatchService  DomainService
    ↓                ↓
 IpscMatch*    IpscMatch*Stage
 Repository    Repository
    ↓                ↓
 IpscMatch*    IpscMatch*Stage
   Entity         Entity
```

**Characteristics:**

- Explicit IPSC entity naming
- Comprehensive validation layers
- Enhanced test coverage
- Clear domain boundaries

---

### v5.0.0: Consolidated Framework

```
       IpscController
            ↓
  ┌─────────┼─────────┐
  ↓         ↓         ↓
Service   Domain    IPSC
Layer     Layer    Services
  ↓         ↓         ↓
Entity    Entity    Records
Layer   Initialisation
  ↓      Framework
Repository Layer
```

**Characteristics:**

- Entity initialisation framework
- Response generation pipeline
- Consolidated infrastructure
- Industry-standard versioning

---

### v5.2.0: Three-Tier Mapping Architecture

```
       IpscController
            ↓
  ┌─────────┼─────────┐
  ↓         ↓         ↓
Service   Match     IPSC
Layer    Entity   Services
  ↓      Service     ↓
  ↓         ↓    DtoMapping
  ↓         ↓         ↓
  ↓    DtoToEntity   ↓
  ↓     Mapping      ↓
  ↓         ↓        ↓
  ↓   EntityMapping  ↓
  ↓         ↓        ↓
Repository Layer
  ↓
Entity Layer
```

**Characteristics:**

- Three-tier mapping system (DTO → Bridge → Entity)
- Dedicated MatchEntityService
- Enhanced null safety with Optional
- Comprehensive test consolidation
- Cleaner separation of concerns

---

### v8.0.0: IPSC Competitor & Match CRUD

```
IpscCompetitorController        IpscMatchController
        ↓                              ↓
IpscCompetitorService          IpscMatchService
        ↓                              ↓
  CompetitorRepository    IpscMatchRepository / IpscMatchStageRepository
        ↓                              ↓
     Competitor                IpscMatch / IpscMatchStage
        ↓                              ↓
  GenderConverter          ClubIdentifierConverter / FirearmTypeConverter / MatchCategoryConverter
```

**Characteristics:**

- `IpscController`'s empty stub retired — the module now has real, layered CRUD endpoints matching the
  `Controller → Service → Repository → Entity` pattern established since v1.0.0
- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only, not
  yet consumed by any controller — the next stage of the rebuild
- No changes to the Award/Image CSV pipeline's own architecture beyond the `processCsv` → `createAwards`/`createImages`
  rename

---

### v7.2.0: Test Suite Conventions & Tooling

```
Test Class Structure
├── // <ClassName>(ParamTypes) — constructors first
├── // publicMethod()          — alphabetical, overloads by param count/type
├── // protectedMethod()       — after all public methods
└── // toString()              — always last
        ↓
Claude Code Commands
├── /scaffold-unit-tests        (interface + impl-only test split)
└── /scaffold-integration-tests (new — @SpringBootTest, public-interface-only calls)
```

**Characteristics:**

- No domain/repository/architectural change — this release formalises and retrofits a test-file convention (26 files),
  closes 4 JaCoCo coverage gaps and corrects/extends AI-agent tooling
- Spring Boot parent `4.0.7` → `4.1.0`, with redundant `pom.xml` overrides removed and `flyway-mysql` kept in sync with
  Boot's managed `flyway.version`

---

### v7.1.0: Shooter Log Correction

```
                  Repository Layer
                  ├── ShooterLogRepository (findAllBy...AndPowerFactor)
                  └── ShooterLogCompetitorRepository (new — supersedes ShooterLogEntryRepository)
                        ↓
                  Entity Layer
                  ├── ShooterLog (+ powerFactor)
                  └── ShooterLogCompetitor (renamed from ShooterLogEntry; + points, + match)
                        ↓
                  AttributeConverters (PowerFactorConverter — reused, no new converters)
```

**Characteristics:**

- Same domain/repository shape as v7.0.0 — this release corrects the shooter-log entity's name and scope rather than
  changing the architecture around it
- `ShooterLog`/`ShooterLogCompetitor` remain schema-only; still no calculation service consumes them
- Flyway migration `V7_1_0__update_shooter_log_schema.sql` renames the table and its constraints in place — no backfill
  needed since both tables are still empty everywhere

---

### v7.0.0: Club-Scoped Results, Visitor Tracking & Shooter Log Model

```
IpscController          IpscMatchController (/v2/ipsc/matches)
     ↓                        ↓
IpscService          IpscMatchService
     ↓               (unchanged — no service/controller wiring for the new fields yet)
TransformationService        ↓
     ↓               DomainService
     ↓                    ↓
     └──────────► Entity Services (unchanged)
                        ↓
                  Repository Layer (rebuilt — 8 JpaRepository interfaces)
                        ↓
                  Entity Layer (promoted from domain/old/, extended)
                  ├── Club (+ identifier)
                  ├── Competitor (+ homeClub)
                  ├── MatchCompetitor (+ overallRanking, clubRanking, isVisitor)
                  ├── MatchStageCompetitor (→ matchCompetitor FK)
                  ├── IpscMatchStage (+ unique constraint)
                  └── ShooterLog / ShooterLogEntry (new)
                        ↓
                  AttributeConverters (ClubIdentifier, FirearmType — reused, no new converters)
```

**Characteristics:**

- `domain/old/` retired — all six entities live in `za.co.hpsc.web.domain` again
- Club-scoped and visitor-aware match results without a separate `MatchResult` table
- Per-stage results attach to a `MatchCompetitor` (firearm-type entry), not directly to a `Competitor`
- `ShooterLog`/`ShooterLogEntry` persist point-in-time best-4-match snapshots
- `repositories/` package fully rebuilt (8 interfaces); no new enums or converters required
- Domain-layer groundwork only — service, controller and import-pipeline wiring still to come

---

### v6.0.0: Versioned Match API & Fully Encapsulated Domain Layer

```
IpscController          IpscMatchController (/v2/ipsc/matches)
     ↓                        ↓
IpscService          IpscMatchService
     ↓               (insert/update/modify/get)
TransformationService        ↓
     ↓               DomainService
     ↓               (entity services only — no direct repo access)
     ↓                    ↓
     └──────────► ClubEntityService
                  CompetitorEntityService
                  MatchEntityService
                  MatchStageEntityService
                  MatchCompetitorEntityService
                  MatchStageCompetitorEntityService
                        ↓
                  Repository Layer
                        ↓
                  Entity Layer
                        ↓
                  AttributeConverters
```

**Model package structure:**

```
models/ipsc/
├── common/   ← all shared IPSC models
│   ├── dto/, request/, response/, records/, holders/, data/, divisions/
└── match/    ← match-only models
    ├── dto/, request/, response/, holders/dto/
```

**Characteristics:**

- Dedicated `/v2/ipsc/matches` API — create, replace, patch, retrieve matches
- `DomainServiceImpl` no longer injects repositories directly; entity services are the only access path
- `models/ipsc/common/` + `models/ipsc/match/` provide clear package boundaries
- `IpscUtil` centralises club and match display-string construction
- `IpscMemberController` stub registered for upcoming member management

---

### v5.4.0: Transformation Service Architecture

```
       IpscController
            ↓
  ┌─────────┼─────────┐
  ↓         ↓         ↓
Service   Domain    Transformation
Layer     Service    Service
  ↓       (init)   (processing)
  ↓         ↓    MatchHolder ↓
  ↓    DtoToEntity   ↓
  ↓     Mapping      ↓
  ↓    (data pkg)    ↓
  ↓         ↓        ↓
Repository Layer
  ↓  (List-based returns)
Entity Layer
  ↓
AttributeConverters
(ClubIdentifier uses abbreviation)
```

**Characteristics:**

- `TransformationService` replacing `IpscMatchService` for semantic clarity
- `MatchHolder` encapsulating match data passing
- `MatchCompetitorEntityService` returns lists for bulk operations
- `domain` package renamed to `data` for mapping classes
- CI/CD quality gates: Qodana JVM static analysis and JaCoCo coverage

---

### v5.3.0: Consolidated Service Architecture

```
       IpscController
            ↓
  ┌─────────┼─────────┐
  ↓         ↓         ↓
Service   Domain    IPSC
Layer     Service   Match
  ↓       (init)    Service
  ↓         ↓    (processing)
  ↓    DtoToEntity   ↓
  ↓     Mapping      ↓
  ↓    (record)      ↓
  ↓         ↓        ↓
Repository Layer
  ↓  (Set-based, scheduled date)
Entity Layer
  ↓
AttributeConverters
(ClubIdentifier, CompetitorCategory,
 Division, FirearmType,
 MatchCategory, PowerFactor)
```

**Characteristics:**

- Consolidated service boundaries (IpscMatchResultService removed)
- Custom JPA AttributeConverters for type-safe enum persistence
- DtoMapping as immutable Java record
- Correct bidirectional `@OneToMany` relationships with `mappedBy`
- Optimised repository queries

---

## ✨ Feature Timeline

### Data Processing Features

- **v1.0.0:** Image CSV processing, MIME type inference
- **v1.1.0:** Award CSV processing
- **v2.0.0:** CAB file import, XML processing, UUID mapping
- **v3.0.0:** Firearm-type classification, enhanced scoring
- **v4.0.0:** Enhanced entity mapping, validation layers
- **v5.0.0:** Entity initialisation framework, record generation
- **v5.2.0:** Three-tier mapping architecture, enhanced match entity handling
- **v5.3.0:** Custom JPA converters; optimised repository queries; `Set`-based competitor deduplication
- **v5.4.0:** `EnrolledCompetitorDto`; SAPSA number validation; competitor deduplication by SAPSA+ID
- **v6.0.0:** `IpscUtil` for club/match string formatting; `MatchOnlyDto` match pipeline; match search request models
  (`MatchSearchRequest`, `MatchSearchDateRequest`, `MatchSearchIdRequest`)

### Domain Management Features

- **v1.0.0:** Image entities
- **v1.1.0:** Award entities
- **v2.0.0:** Match, Competitor, Stage entities
- **v3.0.0:** Club reintroduction, Firearm types
- **v4.0.0:** IpscMatch, IpscMatchStage entities
- **v5.0.0:** Advanced initialisation patterns
- **v5.2.0:** DtoMapping, EntityMapping, DtoToEntityMapping, MatchEntityService
- **v5.3.0:** Custom AttributeConverters for all enums; DtoMapping as Java record; corrected `@OneToMany` mappedBy
  declarations; ClubEntityService simplified
- **v5.4.0:** `EnrolledCompetitorDto`; `ClubIdentifier` abbreviation; records' restructuring; `domain` → `data` package;
  `MatchHolder`; `TransformationService`
- **v6.0.0:** `MatchOnlyDto`/`Request`/`Response` for match CRUD; `models/ipsc/common/` + `models/ipsc/match/` package
  split; entity service methods `findClubById`, `findCompetitorById`, `findMatchStageCompetitorById`;
  `DomainServiceImpl` fully decoupled from repositories
- **v7.0.0:** Six entities promoted from `domain/old/` back into `domain`; `Club.identifier`, `Competitor.homeClub`,
  `MatchCompetitor.overallRanking`/`clubRanking`/`isVisitor`; `MatchStageCompetitor` repointed to `matchCompetitor`; new
  `ShooterLog`/`ShooterLogEntry` entities; `repositories/` package rebuilt with 8 `JpaRepository` interfaces
- **v7.1.0:** `ShooterLogEntry` renamed to `ShooterLogCompetitor`; `ShooterLog.powerFactor`;
  `ShooterLogCompetitor.points`/`match`; `ShooterLogRepository` finder renamed to include `PowerFactor`; new
  `ShooterLogCompetitorRepository`
- **v8.0.0:** `Gender` gains `name`/`abbreviation`/`fromName()`/`toString()`; new `GenderConverter`;
  `CompetitorRequest`/`CompetitorResponse`, `MatchResponse`/`MatchStageResponse` DTOs; `models/ipsc/request` split into
  `models/ipsc/match/request`/`models/ipsc/scores/request`

### API Capabilities

- **v1.0.0:** Image endpoints
- **v1.1.0:** Award endpoints, OpenAPI documentation
- **v2.0.0:** Match result endpoints
- **v3.0.0:** Enhanced IPSC endpoints
- **v4.0.0:** Refactored IPSC endpoints
- **v4.1.0:** Complete CRUD endpoints
- **v5.0.0:** Mature API with record generation
- **v5.2.0:** Enhanced null safety with Optional return types
- **v5.3.0:** Consolidated service API; IpscMatchResultService removed from the internal contract
- **v5.4.0:** Improved error handling in ControllerAdvice; IpscController updates
- **v6.0.0:** `/v2/ipsc/matches` CRUD API (POST, PUT, PATCH, GET) via `IpscMatchController`; structured logging in
  `ControllerAdvice`; `IpscMemberController` stub at `/ipsc/member`
- **v8.0.0:** `IpscCompetitorController`/`IpscMatchController` full CRUD (`create`/`update`/`patch`/`get`, plus
  `getAllMatches`) on `/ipsc/competitors`/`/ipsc/matches`, replacing the empty `IpscController` stub;
  `AwardController`/`ImageController` bulk endpoints moved to `/awards/bulk`/`/images/bulk`, returning `201 Created`

### Testing Coverage

- **v1.0.0:** Basic unit tests
- **v1.1.0:** Service and model tests
- **v2.0.0:** Comprehensive service tests
- **v3.0.0:** Domain model tests (279+ lines)
- **v4.0.0:** Integration tests (985+ lines)
- **v5.0.0:** Advanced entity initialisation tests
- **v5.0.0+:** Comprehensive DTO unit tests (151+ tests)
    - MatchStageDtoTest (48 tests): Constructors, init(), toString()
    - ScoreDtoTest (26 tests): All constructor patterns, edge cases
    - MatchStageCompetitorDtoTest (77 tests): Complete lifecycle coverage
- **v5.1.0:** Test quality enhancement (section-based organisation, duplicate elimination)
- **v5.2.0:** Comprehensive test consolidation
    - DtoToEntityMappingTest (716 lines)
    - TransactionServiceTest (2,000+ lines)
    - Consolidated all service and utility tests
    - Removed 3,000+ lines of duplicate tests
    - All tests follow a standardised naming convention
    - Consolidated test structure across all DTO classes
    - Edge case testing: null/empty/blank fields, boundary values, enum mapping
    - Special character and Unicode support validation
    - Format consistency and mutability testing
- **v5.0.0+ (Post-Release):** Test refactoring and enhanced coverage
    - IpscMatchServiceTest: Renamed from IpscMatchEntityServiceImplTest with enhanced match results processing coverage
    - IpscMatchResultServiceImpl: Comprehensive null handling enhancements
    - WinMSS Integration Tests: Comprehensive importWinMssCabFile validation and processing scenarios
    - FirearmTypeToDivisionsTest: Enhanced with comprehensive cases and improved naming
    - Test documentation improvements across all test classes
- **v5.3.0:** Service consolidation test overhaul
    - DomainServiceTest: 787 lines added – comprehensive `initMatchEntities` coverage
    - IpscMatchServiceTest: 3,156 lines changed – comprehensive consolidation with helper methods
    - TransactionServiceTest: 1,031 lines changed – enabled tests, `getFirst()` assertions
    - IpscServiceIntegrationTest: 113 lines changed – `importWinMssCabFile` integration tests
    - Removed IpscMatchResultServiceTest (1,802 lines) – service deleted
    - Removed ScoreDtoTest (643 lines) – class deleted
- **v5.4.0:** Largest single-release test expansion in project history
    - 20+ new test classes, ~7,000 lines of new test code
    - New controller tests (4), converter tests (6), domain entity tests (6), exception tests (3)
    - New integration tests (3): `AwardServiceIntegrationTest`, `ImageServiceIntegrationTest`,
      `DtoToEntityMappingIntegrationTest`
    - New service tests: `TransformationServiceTest` (1,026 lines), `MatchCompetitorDtoTest`
    - Removed `IpscMatchServiceTest` (10,076 lines – service renamed to `TransformationService`)
    - Updated major suites: DomainServiceTest (1,428), TransactionServiceTest (1,736), IpscServiceIntegrationTest (649),
      IpscServiceTest (737)
- **v6.0.0:** 8 new test classes (~1,300 lines) covering the match CRUD pipeline end-to-end
    - `IpscMatchControllerTest`, `IpscMatchServiceTest` (unit)
    - `IpscMatchIntegrationTest` (H2 integration — match persistence)
    - `MatchOnlyDtoTest`, `MatchOnlyRequestTest`, `MatchOnlyResponseTest`, `MatchResponseTest` (model)
    - `IpscUtilTest` (utility — string formatting edge cases)
    - `IpscControllerTest` removed; covered by `IpscMatchControllerTest`
    - Major suite updates: `TransformationServiceTest` (+747), `DomainServiceTest` (+247), `TransactionServiceTest`
      (+246), `ValueUtilTest` (+294)
- **v7.0.0:** No new dedicated unit/integration test coverage for the promoted/extended entities or the 8 new
  repositories — verified instead via `./mvnw clean compile` and `HpscWebApplicationTests` (Spring context boot against
  H2, Hibernate schema build validating every `@JoinColumn`, converter and unique constraint across all 8 entities)
- **v7.1.0:** No new dedicated unit/integration test coverage for the renamed/rescoped `ShooterLogCompetitor` entity —
  same verification approach as v7.0.0 (compile + `HpscWebApplicationTests` H2 schema build)
- **v7.2.0:** New interface-contract tests `AwardServiceTest`/`ImageServiceTest`; 4 JaCoCo-identified coverage gaps
  closed in `ControllerResponseTest`, `FirearmTypeTest`, `ControllerAdviceTest` (suite coverage 95.7%/91.7% →
  97.3%/98.1%); `HpscWebApplicationTests` renamed to `HpscWebApplicationTest`; 26 existing test files retrofitted with
  the new `// methodName()` header-comment/ordering convention (comments and reordering only — no behaviour change)
- **v8.0.0:** New unit and integration test coverage for `IpscCompetitorController`/`Service`/`ServiceImpl` and
  `IpscMatchController`/`Service`/`ServiceImpl`; new `GenderTest`/`GenderConverterTest`; mechanical test updates for the
  `fromX` enum-factory rename — the largest single-release test expansion since v5.4.0

### Documentation Quality

- **v1.0.0:** Inline Javadoc
- **v1.1.0:** Standardised documentation, OpenAPI
- **v1.1.2:** README and ARCHITECTURE guides
- **v3.0.0:** Enhanced Javadoc across codebase
- **v5.0.0:** RELEASE_NOTES, CHANGELOG, HISTORY
- **v5.2.0:** Comprehensive release documentation with breaking changes analysis
- **v5.3.0:** v5.3.0 release notes, changelog entry, history update; Javadoc for `IpscMatchStage.init()` and
  `findMatchByNameAndScheduledDate`
- **v5.4.0:** v5.4.0 release notes, changelog entry, history update; Javadoc on `EnrolledCompetitorDto` and
  `TransformationService` interface
- **v6.0.0:** v6.0.0 release notes, changelog entry, history update; CLAUDE.md added for AI assistant context
- **v7.0.0:** v7.0.0 release notes, changelog entry, history update
- **v7.1.0:** v7.1.0 release notes, changelog entry, history update; AI-agent prompt files migrated to
  `.claude/commands/*.md`; `AGENTS.md` adopts GitFlow; `CONTRIBUTING.md` added
- **v7.2.0:** v7.2.0 release notes, changelog entry, history update; new `// methodName()` test-comment/ordering
  convention added to AGENTS.md; new CLAUDE.md Git Workflow section with explicit PR-target guidance;
  `/scaffold-unit-tests`/`/scaffold-integration-tests` Claude Code commands added; false AssertJ claim removed from five
  docs; CLAUDE.md package-overview table corrected
- **v8.0.0:** v8.0.0 release notes, changelog entry, history update; comprehensive Javadoc/`@since` pass across models,
  converters, exceptions, utils, constants and `ControllerAdvice`; `AGENTS.md`/`CLAUDE.md` merged into a single
  tool-agnostic reference; AI-agent tooling migrated from slash commands to Skills

---

## 💡 Project Philosophy Evolution

### Major Version Goals

- **Version 4.x (v4.0.0 – v4.1.0):** Establish explicit IPSC domain naming (`Match` → `IpscMatch`, `MatchStage` →
  `IpscMatchStage`) backed by multi-layered validation, then complete the entity lifecycle with full CRUD support and
  transactional consistency — turning the renamed domain model into a fully operable API surface.
- **Version 5.x (v5.0.0 – v5.4.0):** Mature the codebase into a stable, well-tested, standards-compliant foundation —
  formalise Semantic Versioning, consolidate and simplify the service layer and extend real domain capability
  (competitor enrolment) only once that foundation was solid.
- **Version 6.x (v6.0.0):** Turn the v5.x foundation into an actual product surface — a dedicated, versioned match CRUD
  API, complete the layered-architecture discipline by fully decoupling `DomainServiceImpl` from repositories and
  restructure the IPSC model packages for long-term growth.
- **Version 7.x (v7.0.0 – v7.4.0):** Rebuild IPSC domain-layer groundwork deliberately ahead of the service/controller
  layer — which had since been removed pending a rebuild — while investing in process discipline: formalised test
  conventions, AI-agent tooling and increasingly rigorous documentation accuracy and consistency.
- **Version 8.x (v8.0.0 – v8.1.1):** Complete the IPSC module rebuild that v6.x–v7.x deliberately deferred — real
  competitor and match CRUD replacing the empty controller stub — while consolidating the project's own documentation
  (`AGENTS.md`/`CLAUDE.md` merge) and AI-agent tooling (commands → Skills) into a single, coherent source of truth.
  Then extend that foundation with competitor bulk CSV import and a project-wide correctness fix ensuring
  `@JsonProperty(required = true)` actually enforces required fields via matching `@JsonCreator` constructors.
  Finally, close a silent test-coverage regression, complete the CI static-analysis gate, and build tooling so the
  release process keeps auditing its own roadmap documentation going forward.

### Initial Phase (v1.0.0)

**Focus:** Foundation & Basic Functionality

- Establish a working Spring Boot application
- Implement CSV data processing
- Create basic API endpoints
- Error handling foundation

### Growth Phase (v1.1.0 – v2.0.0)

**Focus:** Feature Expansion & Modularity

- Add new feature domains (awards)
- Introduce service-oriented architecture
- Establish documentation standards
- Improve code quality

### Specialisation Phase (v3.0.0 – v4.0.0)

**Focus:** IPSC Domain Compliance & Quality

- Align with IPSC standards
- Enhance domain clarity
- Comprehensive testing
- Production readiness

### Maturity Phase (v4.1.0 – v5.0.0)

**Focus:** Completeness, Standards & Infrastructure

- Complete CRUD capabilities
- Industry-standard versioning
- Infrastructure consolidation
- Entity initialisation framework

### Refinement Phase (v5.1.0 – v5.2.0)

**Focus:** Quality, Architecture & Maintainability

- Test suite enhancement and consolidation
- Architectural refactoring with separation of concerns
- Enhanced null safety and robustness
- Comprehensive test coverage across all layers
- Code quality and maintainability improvements
- Test suite enhancement and refactoring
- Improved code maintainability
- Long-term maintainability

### Consolidation Phase (v5.3.0)

**Focus:** Service Consolidation, Type Safety & Repository Efficiency

- Removal of unnecessary service abstractions (`IpscMatchResultService`)
- Custom JPA converters for explicit, type-safe enum persistence
- Immutable DtoMapping record for cleaner data flow
- Correct JPA bidirectional relationship declarations
- Repository query optimisation for performance and accuracy
- Continued test suite refinement and integration test expansion

### Enrolment Phase (v5.4.0)

**Focus:** Competitor Enrolment, Service Clarity & Comprehensive Test Coverage

- Introduce `EnrolledCompetitorDto` for member enrolment tracking through the IPSC pipeline
- Rename `IpscMatchService` to `TransformationService` for semantic accuracy
- SAPSA number validation and competitor deduplication in `CompetitorDto`
- Expand test coverage to all layers — controllers, converters, entities, exceptions, integration
- Establish Qodana JVM linting and JaCoCo coverage as CI/CD quality gates
- Package reorganisation (`domain` → `data`) and records restructuring for semantic clarity

### API Productisation Phase (v6.0.0)

**Focus:** Versioned Match API, Repository Decoupling & Package Structure

- Introduce a dedicated, versioned match CRUD API (`/v2/ipsc/matches`) separate from the bulk-import controller
- Complete the entity service encapsulation: `DomainServiceImpl` no longer bypasses the service layer to reach JPA
  repositories
- Restructure all IPSC model packages under `models/ipsc/common/` with a dedicated `models/ipsc/match/` sub-package,
  providing clear boundaries for shared vs. match-specific models
- Centralise display-string construction in `IpscUtil`, eliminating scattered formatting logic
- Support future member management and match search features with stub controller and search request models

### Domain Extension Phase (v7.0.0)

**Focus:** Match Results, Visitor Tracking & Shooter Log Data Model

- Promote the six entities parked under `domain/old/` back into the live `domain` package, retiring the `.old` package
- Model club-scoped match results and match visitors relationally — `homeClub`, `clubRanking`, `isVisitor` — rather than
  introducing a fourth club row
- Repoint per-stage results from `Competitor` to `MatchCompetitor` to support multiple firearm-type entries per
  competitor per match
- Introduce `ShooterLog`/`ShooterLogEntry` as persisted best-4-match snapshots, deferring the recalculation job/service
  to a future release
- Rebuild the `repositories/` package from scratch alongside the promoted domain model
- Deliver domain-layer groundwork deliberately ahead of service, controller and import-pipeline wiring

### Refinement Phase (v7.1.0)

**Focus:** Shooter Log Naming Accuracy & Scope Correction

- Rename `ShooterLogEntry` to `ShooterLogCompetitor` to reflect its role as a per-competitor snapshot row
- Scope shooter-log snapshots by `PowerFactor` as well as `FirearmType`
- Add a direct `match` reference and a `points` field to `ShooterLogCompetitor`, ahead of the future calculation service
  that will populate them
- Adopt GitFlow branching and add `CONTRIBUTING.md` for new-developer onboarding

### Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)

**Focus:** Test Conventions, Documentation Accuracy & AI-Agent Tooling

- Formalise a repo-wide test-file convention (method-comment headers, group ordering) retrofitted across 26 existing
  test files; close every JaCoCo-identified coverage gap (v7.2.0)
- Correct README.md/ARCHITECTURE.md to describe only what the codebase actually implements, removing overstated
  capability claims (v7.3.0)
- New AGENTS.md Serial commas rule and a British English rule tightened to cover code identifiers, both applied
  retroactively across the entire documentation set (v7.4.0)
- Resume IPSC domain-layer groundwork with new request DTOs, deliberately ahead of the service/controller layer removed
  pending a rebuild (v7.4.0)
- Add `/scaffold-unit-tests`, `/scaffold-integration-tests`, `/generate-pr-summary` and `/sync-unreleased-changes`
  Claude Code commands, keeping AI-agent tooling in sync with the project's actual conventions (v7.2.0 – v7.4.0)

### Module Completion Phase (v8.0.0)

**Focus:** IPSC Competitor & Match CRUD, Documentation Consolidation & AI-Agent Tooling

- Replace `IpscController`'s empty stub with full competitor and match CRUD, backed by new services and DTOs
- Extend `Gender` to match the shape of the project's other enums, paired with a new `GenderConverter`
- Rename `processCsv` to `createAwards`/`createImages` and enum `getByX` factories to `fromX`, removing naming
  inconsistencies accumulated across earlier releases
- Merge `CLAUDE.md`'s guidance into `AGENTS.md` as a single tool-agnostic reference; migrate AI-agent tooling from
  slash commands to Skills
- Invest in a comprehensive Javadoc/`@since` documentation pass and re-add Qodana JVM static analysis

---

## 📚 Key Learnings

### Architectural Insights

1. **Service Modularity:** Breaking monolithic services (v2.0.0) dramatically improved testability and maintainability
2. **Domain Clarity:** Explicit entity naming (v4.0.0) reduced confusion and improved code navigation
3. **Test-Driven Quality:** Comprehensive test suites enabled confident refactoring and bug fixes
4. **Documentation Priority:** Early documentation (v1.1.2) established clear system understanding

### Design Decisions

1. **DTO Layer:** Introduction in v2.0.0 created crucial separation between API contracts and domain models
2. **Firearm-Type Classification:** v3.0.0 restructuring improved IPSC compliance without major disruption
3. **Entity Initialisation Framework:** v5.0.0 consolidation provides a unified pattern for complex entity setup4.
   **Semantic Versioning:** Late adoption (v5.0.0) aligns with industry standards for future releases

### Technical Evolution

1. **Exception Handling:** Simplified approach (v3.1.0) improved maintainability without sacrificing clarity
2. **Validation Layers:** Multi-layered validation (v4.0.0) ensures data integrity across tiers
3. **Transaction Management:** Abstraction layer (v2.0.0) enables consistent data consistency patterns
4. **Test Coverage:** Growing investment from basic tests to comprehensive integration testing
5. **DTO Testing Excellence:** Post-v5.0.0 comprehensive DTO unit testing (151+ tests) establishes quality standards
    - Systematic testing of all constructor patterns
    - Complete init() method coverage with parameter combinations
    - toString() validation across all scenarios
    - Edge case mastery: null, empty, blank, boundary values
    - Enum mapping validation across all enums (PowerFactor, Division, FirearmType, CompetitorCategory)
    - Special character and Unicode support verification
    - Consistent test organisation with AAA pattern and clear naming conventions
6. **Continuous Test Refinement:** Ongoing test improvements demonstrate commitment to quality
    - Test class renaming for clarity (IpscMatchEntityServiceImplTest → IpscMatchServiceTest)
    - Enhanced null handling in service implementations
    - Comprehensive integration testing for complex workflows (WinMSS CAB import)
    - Documentation improvements ensuring maintainability
7. **Test Suite Consolidation (v5.1.0):** Structural improvements to test organisation and quality
    - Comprehensive test reorganisation with 6 logical sections for better navigation
    - Elimination of duplicate test cases while maintaining complete coverage
    - Section-based grouping: Null Input Handling, Null Collections & Fields, Match Name Field Handling, Club Fields
      Handling, Partial/Complete Data Scenarios, Edge Cases
    - Improved test readability with clear headers and consistent formatting
    - Enhanced maintainability through reduced code duplication (1 duplicate test removed)
    - All tests follow `testMethod_whenCondition_thenExpectedBehavior` naming pattern
    - Consolidated IpscMatchResultServiceImplTest from 24 to 23 tests with zero reduction in effective coverage
8. **Architectural Refactoring (v5.2.0):** Major architectural improvements with comprehensive scope
    - Three-tier mapping system (DtoMapping, EntityMapping, DtoToEntityMapping) for clear separation
    - Dedicated MatchEntityService for specialised entity handling
    - Comprehensive test consolidation across all services and utilities (removed 3,000+ duplicate lines)
    - Enhanced null safety with array initialisation and Optional return types
    - Major service refactoring: 61 files, +13,567 insertions, -5,898 deletions
    - New comprehensive tests: DtoToEntityMappingTest (716 lines), TransactionServiceTest (2,000+ lines)
    - All tests consistently follow the AAA pattern with standardised naming
9. **Service Consolidation & Type Safety (v5.3.0):** Focused consolidation and infrastructure improvements
    - Six custom JPA AttributeConverters replacing `@Enumerated(EnumType.STRING)` for type-safe persistence
    - `IpscMatchResultService` and `ScoreDto` fully removed; functionality consolidated into `DomainService` and
      `IpscMatchService`
    - `DtoMapping` transitioned to Java record for immutability and clarity
    - All bidirectional `@OneToMany` relationships corrected with `mappedBy` declarations
    - Repository queries optimised: `Set` deduplication, scheduled date constraints, fetch join removal
    - Test suite overhaul: DomainServiceTest (+787 lines), IpscMatchServiceTest (3,156 lines changed)
    - Statistics: ~45 commits, 59 files changed, +5,686 insertions, -4,613 deletions
10. **Competitor Enrolment & Service Transformation (v5.4.0):** Largest single-release test expansion
    - `EnrolledCompetitorDto` introduced for competitor enrolment tracking; SAPSA validation added
    - `IpscMatchService` renamed to `TransformationService`; `TransformationServiceImpl` (1,098 lines)
    - `ClubIdentifier` abbreviation field added; `ClubIdentifierConverter` updated for persistence
    - 20+ new test classes (~7,000 lines) across controllers, converters, entities, exceptions, integration
    - Qodana JVM linting and JaCoCo 0.8.14 code coverage integrated into CI/CD
    - Statistics: ~75 commits, 123 files changed, +12,713 insertions, -13,358 deletions
11. **Dedicated Match CRUD API & Service Encapsulation (v6.0.0):** Versioned API and layer enforcement
    - `IpscMatchController` at `/v2/ipsc/matches` establishes a versioned, resource-oriented match API
    - `DomainServiceImpl` fully decoupled from repositories — completing the intended layered architecture
    - All IPSC models moved to `models/ipsc/common/`; `models/ipsc/match/` added for match-specific classes
    - `IpscUtil` centralises display-string construction; `MatchOnlyDto/Request/Response` support match CRUD
    - 8 new test classes (~1,300 lines) covering controller, service, integration, DTO and utility layers
    - Statistics: 40 commits, 165 files changed, +6,779 insertions, -3,501 deletions
12. **Match Results, Visitor Tracking & Shooter Log Data Model (v7.0.0):** Domain-layer groundwork ahead of the pipeline
    - Six entities promoted from `domain/old/` back into `domain`; `.old` package retired
    - `Club.identifier`, `Competitor.homeClub`, `MatchCompetitor.clubRanking`/`isVisitor` model club-scoped results and
      visitors relationally
    - `MatchStageCompetitor` repointed from `Competitor` to `MatchCompetitor`; three new unique constraints added across
      the domain model
    - New `ShooterLog`/`ShooterLogEntry` entities persist best-4-match snapshots; no calculation job/service yet
    - `repositories/` package rebuilt from scratch with 8 new `JpaRepository` interfaces
    - No dedicated new unit/integration test coverage added for the new/changed entities in this release
    - Statistics: 1 commit, 15 files changed, +207 insertions, -30 deletions
13. **Shooter Log Refinement (v7.1.0):** Naming accuracy and scope correction ahead of the calculation service
    - `ShooterLogEntry` renamed to `ShooterLogCompetitor`; entity gains `points` and a direct `match` reference
    - `ShooterLog.powerFactor` scopes snapshots by power factor as well as firearm type
    - `ShooterLogRepository` finder renamed to include `PowerFactor`; new `ShooterLogCompetitorRepository` supersedes
      `ShooterLogEntryRepository`
    - Migration renames the table/constraints in place — no backfill needed, both tables remain empty
    - Repository tooling migrated from `.github/prompts/` to `.claude/commands/`; `AGENTS.md` adopts GitFlow;
      `CONTRIBUTING.md` added
14. **IPSC Module Completion (v8.0.0):** Full competitor and match CRUD, six releases after v6.0.0 first laid the
    groundwork
    - `IpscCompetitorController`/`IpscMatchController` replace the empty `IpscController` stub with real, layered CRUD
    - New `IpscCompetitorService`/`IpscMatchService` + impls, `Gender` enum enhancements and `GenderConverter`
    - Naming consistency sweep: `processCsv` → `createAwards`/`createImages`, enum `getByX` → `fromX` factories
    - Comprehensive Javadoc/`@since` pass; AI-agent tooling migrated from slash commands to Skills
    - Largest single-release test expansion since v5.4.0 — full unit and integration coverage for both new
      controllers/services
15. **Jackson Required-Field Gotcha (v8.1.0):** `@JsonProperty(required = true)` only fires for creator (constructor)
    parameters — a class deserialised via its default no-args constructor and setters silently treats a missing
    "required" field as `null`
    - Fixed across every IPSC request model to date by adding a `@JsonCreator` constructor with each parameter bound
      via `@JsonProperty`, replacing the affected classes' Lombok `@AllArgsConstructor`
    - Verified with a throwaway `csvMapper.addMixIn(...)` scratch test that the two not-yet-wired scores CSV variants
      are correctly mixin-compatible with their plain counterparts, the same pattern
      `AwardServiceImpl`/`ImageServiceImpl` already use for `AwardRequestForCSV`/`ImageRequestForCsv`
    - Caught a genuine validation mismatch along the way: `CompetitorRequest`'s Jackson-required field was
      `competitorNumber`, not the actually-validated `clubNumber`

---

## 🚀 Future Roadmap Implications

Based on the evolution to v8.2.0, the following areas are identified for future enhancement:

### Recently Completed (v8.2.0)

- `Competitor.emailAddress` (`String`) replaced with `emailAddresses` (`List<String>`), backed by a new
  `competitor_email` child table and a backfilling Flyway migration
- New `SystemConstants.ARRAY_SEPARATOR` unifies `AwardServiceImpl`/`ImageServiceImpl`'s bulk CSV parsing with the
  competitor domain's semicolon-separated multi-value convention
- Qodana static analysis removed entirely — it had failed on every CI run since v8.1.1 added it (missing
  `QODANA_TOKEN` secret, unconditional SARIF upload) — closing `documentation/roadmap/improvement-plan.md`'s Gap #7
  as not applicable
- Project version bumped to 8.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.1.1)

- New `.github/workflows/qodana.yml` completes the Qodana static-analysis CI gate, configured but unwired since
  v8.0.0
- Recreated the deleted exception-hierarchy test suite and closed other coverage gaps; full-suite coverage rose
  from 92.9%/93.4% to 98.34%/98.84% (line/branch)
- Spring Boot bumped `4.1.0` → `4.1.1`; `jackson-databind`, `log4j-api` and `jackson-bom.version` overrides all
  dropped as redundant
- `documentation/roadmap/improvement-plan.md` gains two new gaps (match-scoring service layer; remaining Qodana
  CI-verification work) and closes the `jackson-databind` override gap
- Project version bumped to 8.1.1 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.1.0)

- New `IpscCompetitorController.createCompetitors` (`POST /ipsc/competitors/bulk`) persists competitors from CSV
  data via the existing `createCompetitor` logic
- New `CompetitorRequestForCSV`/`CompetitorResponseHolder` models
- Fixed a Jackson gotcha affecting every `@JsonProperty(required = true)` field added to date: `CompetitorRequest`,
  `CompetitorRequestForCSV`, `MatchRequest`, `MatchStageRequest` and the scores request models all gained a
  `@JsonCreator` constructor so required fields are actually enforced
- `CompetitorRequest`'s required field corrected from `competitorNumber` to `clubNumber`
- Project version bumped to 8.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.0.0)

- `IpscCompetitorController`/`IpscMatchController` full CRUD, replacing the empty `IpscController` stub
- New `IpscCompetitorService`/`IpscMatchService` + impls; new `CompetitorRequest`/`CompetitorResponse`,
  `MatchResponse`/`MatchStageResponse` DTOs
- `Gender` enum gains `name`/`abbreviation`/`fromName()`/`toString()`; new `GenderConverter`
- `processCsv` renamed to `createAwards`/`createImages`; bulk endpoints moved to `/awards/bulk`/`/images/bulk`,
  returning `201 Created`; enum `getByX` factories renamed to `fromX`
- Comprehensive Javadoc/`@since` pass; `AGENTS.md`/`CLAUDE.md` merged; AI-agent tooling migrated from slash commands to
  Skills; Qodana JVM static analysis re-added
- Project version bumped to 8.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v7.2.0)

- New interface-contract unit tests `AwardServiceTest`/`ImageServiceTest`, exercising `createAwards` through the interface
  type rather than the impl class
- 4 JaCoCo-identified coverage gaps closed (`ControllerResponse`, `FirearmType.toString()`,
  `ControllerAdvice.logError`); suite coverage rose from 95.7%/91.7% to 97.3%/98.1% (line/branch)
- `HpscWebApplicationTests` renamed to `HpscWebApplicationTest`; 26 existing test files retrofitted with a new
  `// methodName()` header-comment/ordering convention
- New `/scaffold-unit-tests` (corrected from a stale, wrong-project prompt) and `/scaffold-integration-tests` Claude
  Code commands
- Spring Boot parent upgraded `4.0.7` → `4.1.0`; redundant `pom.xml` version overrides removed; `flyway-mysql` bumped
  `11.14.1` → `12.4.0` to match Boot's newly-managed `flyway.version`
- Project version bumped to 7.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v7.1.0)

- `ShooterLogEntry` renamed to `ShooterLogCompetitor` (table `shooter_log_entry` → `shooter_log_competitor`)
- `ShooterLog.powerFactor` (`PowerFactor`, not nullable) scopes snapshots by power factor as well as firearm type
- `ShooterLogCompetitor.points` (nullable) and `ShooterLogCompetitor.match` (`@ManyToOne IpscMatch`, not nullable) added
- `ShooterLogRepository.findAllByCompetitorIdAndFirearmType` renamed to
  `findAllByCompetitorIdAndFirearmTypeAndPowerFactor`
- New `ShooterLogCompetitorRepository` supersedes `ShooterLogEntryRepository`
- Project version bumped to 7.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v7.0.0)

- Six entities promoted from `domain/old/` back into `za.co.hpsc.web.domain`; `.old` package removed
- `Club.identifier` (`ClubIdentifier`, unique) ties a club to HPSC/SOSC/PMPSC
- `Competitor.homeClub` — nullable `@ManyToOne Club` relation for home-club membership
- `MatchCompetitor.matchRanking` renamed `overallRanking`; new `clubRanking` and `isVisitor` fields; new unique
  constraint `(competitor_id, match_id, firearm_type)`
- `MatchStageCompetitor` repointed from `competitor` to `matchCompetitor`; new unique constraint
  `(match_competitor_id, match_stage_id)`
- `IpscMatchStage` gains new unique constraint `(match_id, stage_number)`
- New `ShooterLog`/`ShooterLogEntry` entities persist best-4-match shooter-log snapshots
- `repositories/` package rebuilt from scratch with 8 new `JpaRepository` interfaces
- Project version bumped to 7.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v6.0.0)

- `IpscMatchController` introduced at `/v2/ipsc/matches` with full CRUD (POST, PUT, PATCH, GET)
- `IpscMatchService` + `IpscMatchServiceImpl` added as dedicated match management service
- `MatchOnlyDto`, `MatchOnlyRequest`, `MatchOnlyResponse`, `MatchOnlyResultsDto` introduced
- `DomainServiceImpl` fully decoupled from JPA repositories; delegates to entity services only
- New entity service methods: `findClubById`, `findCompetitorById`, `findMatchStageCompetitorById`
- `IpscUtil` added for centralised club/match display-string formatting
- All IPSC models moved to `models/ipsc/common/`; `models/ipsc/match/` sub-package introduced
- Match search request models: `MatchSearchRequest`, `MatchSearchDateRequest`, `MatchSearchIdRequest`
- `IpscMemberController` stub registered at `/ipsc/member`
- Spring Boot upgraded 4.0.5 → 4.0.6; MIT licence and SCM metadata added to `pom.xml`
- 8 new test classes (~1,300 lines); `IpscControllerTest` removed

### Previously Completed (v5.4.0 and earlier)

- `EnrolledCompetitorDto` introduced for enrolled competitor tracking through the IPSC pipeline
- `IpscMatchService` renamed to `TransformationService`; `TransformationServiceImpl` (1,098 lines)
- `ClubIdentifier` abbreviation field added; `ClubIdentifierConverter` updated
- SAPSA number validation and competitor deduplication in `CompetitorDto`
- 20+ new test classes (~7,000 lines) — the largest single-release expansion
- Qodana JVM linting and JaCoCo code coverage integrated into CI/CD
- Package restructure: `ipsc/domain` → `ipsc/data`; records and holders reorganised
- Six custom JPA attribute converters (ClubIdentifier, CompetitorCategory, Division, FirearmType, MatchCategory,
  PowerFactor)
- IpscMatchResultService and ScoreDto removed; match result processing consolidated
- Three-tier mapping architecture (DtoMapping, EntityMapping, DtoToEntityMapping)
- Repository query optimisation (Set deduplication, scheduled date, fetch join removal)
- Test suite reorganisation and consolidation (from v5.1.0, v5.2.0, v5.3.0)

### Short-term (Minor Releases)

- Wire service/controller/import support for `clubRanking`, `isVisitor`, `ShooterLog` and `ShooterLogEntry` —
  currently schema-only (`homeClub` now wired via `IpscCompetitorService`)
- Build a `ShooterLogService` to calculate and persist best-4-match snapshots — no calculation job/service exists yet
- Populate `overallRanking`, `clubRanking` and `isVisitor` during match-result import
- Seed `Club.identifier` (HPSC, SOSC, PMPSC) and backfill `Competitor.homeClub`
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) to an endpoint — still
  groundwork, not yet consumed by any controller
- Add entity, repository and integration test coverage for the promoted/extended domain model
- Performance optimisation for large-scale match processing
- Enhanced diagnostic logging

### Medium-term (v7.x+)

- REST API endpoints for enrolled competitor management
- Additional IPSC data format support
- Bulk match processing capabilities
- Enhanced error reporting and recovery
- Performance metrics and monitoring
- Advanced query optimisation

### Long-term (Future Major Versions)

- Real-time match result processing
- Enhanced integrations with external systems
- Advanced reporting and analytics

---

## 🎓 Conclusion

The HPSC Website Backend has evolved from a simple image gallery application into a sophisticated, specialised platform
for managing practical shooting competition data. This evolution demonstrates a commitment to:

- **Continuous Improvement:** Regular releases addressing quality, features and standards
- **Domain Alignment:** Progressive refinement toward IPSC compliance and specialisation
- **Architectural Excellence:** Evolution from monolithic to modular, testable architecture with three-tier mapping and
  consolidated service boundaries
- **Standard Adoption:** Adoption of industry-standard practices (SemVer, documentation patterns)
- **Quality Focus:** Investment in comprehensive testing and documentation
- **Code Maintainability:** Systematic refinement of test organisation, consolidation and architectural separation
  (v5.1.0, v5.2.0, v5.3.0, v5.4.0)
- **Type Safety:** Custom JPA converters ensuring explicit, testable enum persistence (v5.3.0)
- **Service Simplicity:** Removal of unnecessary abstractions for cleaner, more cohesive architecture (v5.3.0, v5.4.0)
- **Competitor Enrolment:** First-class tracking of competitor participation through dedicated DTOs and validation
  workflows (v5.4.0)
- **CI/CD Quality Gates:** Qodana JVM static analysis and JaCoCo coverage enforcement raising the quality baseline
  across the entire codebase (v5.4.0)
- **Versioned Match API:** Dedicated `/v2/ipsc/matches` controller providing resource-oriented CRUD separate from the
  bulk-import flow (v6.0.0)
- **Layer Enforcement:** `DomainServiceImpl` no longer reaches past entity services into repositories, fully realising
  the layered architecture (v6.0.0)
- **Club-Scoped Results & Visitor Tracking:** `Club.identifier`, `Competitor.homeClub` and
  `MatchCompetitor.clubRanking`/`isVisitor` model club results and visitors relationally (v7.0.0)
- **Shooter Log Foundations:** `ShooterLog`/`ShooterLogEntry` persist best-4-match snapshots, laying the data model for
  a future ranking calculation service (v7.0.0)
- **Shooter Log Correction:** `ShooterLogEntry` renamed to `ShooterLogCompetitor` and rescoped by `PowerFactor`, keeping
  the data model accurate before a calculation service is built against it (v7.1.0)
- **Test Suite Consistency:** A formalised, repo-wide test-file convention (method-comment headers, group ordering)
  retrofitted across the existing suite, alongside four JaCoCo-identified coverage gaps closed (v7.2.0)
- **IPSC Module Completion:** `IpscCompetitorController`/`IpscMatchController` replace the empty `IpscController` stub
  with full, layered competitor and match CRUD — completing work begun as groundwork in v6.0.0 (v8.0.0)
- **Bulk Import Extended & Validation Correctness:** `IpscCompetitorController.createCompetitors` extends the CSV
  bulk-import pattern to competitors with genuine persistence, and a project-wide fix ensures
  `@JsonProperty(required = true)` actually enforces required fields via matching `@JsonCreator` constructors (v8.1.0)

The transition to Semantic Versioning in v5.0.0, the test suite consolidation in v5.1.0, the major architectural
refactoring in v5.2.0, the service consolidation with custom converters in v5.3.0, the competitor enrolment system with
service transformation in v5.4.0, the dedicated match CRUD API with service encapsulation in v6.0.0, the match
results/visitor tracking/shooter log data model in v7.0.0, the shooter-log naming/scope correction in v7.1.0, the
test-convention formalisation and dependency maintenance in v7.2.0 and the IPSC module's completion with full
competitor/match CRUD in v8.0.0 mark significant maturation points where the project demonstrates stable, predictable
releases with clear separation of concerns. These releases serve as a solid foundation for the shooting club's digital
operations, with a clear commitment to long-term maintainability and quality.

Version 5.3.0 delivers focused, high-value improvements: type-safe JPA converters, correct entity relationships,
optimised repositories and a consolidated service architecture that reduces complexity without sacrificing capability.

Version 5.4.0 extends that foundation with competitor enrolment tracking, SAPSA number validation,
`TransformationService` replacing `IpscMatchService`, a comprehensive package restructure from `ipsc/domain` to
`ipsc/data` and a significant test expansion. All underpinned by Qodana JVM static analysis and JaCoCo code coverage
enforcement that set a new quality baseline for the project.

Version 6.0.0 marks a decisive architectural milestone: `DomainServiceImpl` no longer bypasses the entity service
boundary to reach JPA repositories, `IpscMatchController` establishes a versioned, resource-oriented match API at
`/v2/ipsc/matches` and the IPSC model packages are restructured under `models/ipsc/common/` and `models/ipsc/match/` —
providing clear, scalable homes for shared and match-specific models as the domain continues to grow.

Version 7.0.0 extends the domain model with club-scoped results, visitor tracking and a persisted shooter log: the six
entities parked under `domain/old/` are promoted back into `domain`, `Club`, `Competitor` and `MatchCompetitor` gain the
fields needed to model home clubs, club rankings and match visitors relationally, `MatchStageCompetitor` is repointed to
`MatchCompetitor` to support multiple firearm-type entries per competitor and the new `ShooterLog`/`ShooterLogEntry`
entities persist best-4-match snapshots — all paired with a `repositories/` package rebuilt from scratch. This release
is deliberately domain-layer groundwork; the service, controller and import-pipeline wiring to make these fields
load-bearing remains for a future release.

Version 7.1.0 is a focused follow-up to v7.0.0's shooter-log data model: `ShooterLogEntry` is renamed to
`ShooterLogCompetitor` for naming accuracy, `ShooterLog` gains a `powerFactor` column so best-4-match snapshots are
scoped correctly and `ShooterLogCompetitor` gains `points` and a direct `match` reference. Both tables remain
schema-only — still no calculation service consumes them — so this release is about getting the shape right before that
service is built. Alongside the schema work, the release also migrates the repository's AI-agent prompt files to Claude
Code commands, adopts GitFlow branching and adds `CONTRIBUTING.md`.

Version 7.2.0 touches no domain model, repository or API surface at all — it is entirely process, tooling and dependency
maintenance. A new AGENTS.md test convention (a one-line `// methodName()` header before each method's test group,
ordered constructors → public → protected → alphabetical → `toString()` last) is retrofitted across 26 existing test
files, four JaCoCo-identified coverage gaps are closed (raising suite coverage from 95.7%/91.7% to 97.3%/98.1%). Two
new Claude Code commands (`/scaffold-unit-tests`, `/scaffold-integration-tests`) are also added to keep future test
scaffolding consistent with these conventions automatically. The release also upgrades the Spring Boot parent to 4.1.0,
cleaning up several dependency-version overrides that had quietly become redundant or, in one case, never actually
worked (a typo'd property name) and adds an explicit Git Workflow section to CLAUDE.md stating GitFlow's PR targets
directly (`feature/*` → `develop`; `release/*`/`hotfix/*` → `main`) instead of deferring entirely to AGENTS.md.

Version 8.0.0 completes the IPSC module rebuild that v6.0.0 first began: `IpscController`'s long-standing empty stub is
replaced by `IpscCompetitorController`/`IpscMatchController`, backed by new `IpscCompetitorService`/`IpscMatchService`
implementations, real competitor and match CRUD with club/gender/firearm-type/match-category resolution and the
largest test expansion since v5.4.0. Alongside the domain work, the release also merges `CLAUDE.md`'s guidance into a
single `AGENTS.md` reference. Also migrates the project's AI-agent tooling from slash commands to Skills and re-adds Qodana
JVM static analysis — marking the transition from a project with significant architectural groundwork to one with a
genuinely complete, if still growing, IPSC feature set.

---

**Document Created:** February 24, 2026  
**Last Updated:** August 31, 2026  
**Coverage:** Version 1.0.0 (January 4, 2026) through Version 8.0.0 (August 31, 2026)  
**Reference:** See [CHANGELOG.md](CHANGELOG.md) and [ARCHIVE.md](/documentation/archive/ARCHIVE.md) for detailed
technical information

**Recent Updates (v8.0.0):**

- `IpscCompetitorController`/`IpscMatchController` full CRUD, replacing the empty `IpscController` stub; new
  `IpscCompetitorService`/`IpscMatchService` + impls
- New `CompetitorRequest`/`CompetitorResponse`, `MatchResponse`/`MatchStageResponse` DTOs; `models/ipsc/request` split
  into `models/ipsc/match/request`/`models/ipsc/scores/request`
- `Gender` enum gains `name`/`abbreviation`/`fromName()`/`toString()`; new `GenderConverter`
- `AwardService`/`ImageService.processCsv` renamed to `createAwards`/`createImages`; bulk endpoints moved to
  `/awards/bulk`/`/images/bulk`, returning `201 Created`; enum `getByX` factories renamed to `fromX`
- Comprehensive Javadoc/`@since` pass across models, converters, exceptions, utils, constants and `ControllerAdvice`
- `AGENTS.md`/`CLAUDE.md` merged into a single tool-agnostic reference; AI-agent tooling migrated from
  `.claude/commands/*.md` slash commands to `.claude/skills/*/SKILL.md` Skills; Qodana JVM static analysis re-added
- Project version bumped to 8.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation
- Largest single-release test expansion since v5.4.0: full unit and integration coverage for both new
  controllers/services, plus `GenderTest`/`GenderConverterTest`

**Previous Update (v7.2.0):**

- New interface-contract unit tests `services/AwardServiceTest`/`services/ImageServiceTest`, exercising `createAwards`
  through the `AwardService`/`ImageService` interface type rather than the impl class
- Four JaCoCo-identified coverage gaps closed: `ControllerResponse(boolean, String)` and the derived-success branch of
  `ControllerResponse(LocalDateTime, String, String)`; `FirearmType.toString()`; `ControllerAdvice.logError`'s
  null-throwable/wrapped-cause/null-`WebRequest` branches — suite coverage rose from 95.7%/91.7% to 97.3%/98.1%
  (line/branch)
- New Claude Code commands `/scaffold-unit-tests` (corrected from a stale, wrong-project prompt file) and
  `/scaffold-integration-tests` (new)
- `HpscWebApplicationTests` renamed to `HpscWebApplicationTest`; 26 existing test files retrofitted with a new
  `// methodName()` header-comment/ordering convention — comments and reordering only, no behaviour change
- Spring Boot parent upgraded `4.0.7` → `4.1.0`; redundant `pom.xml` version overrides removed
  (`spring-framework.version`, `tomcat.version`, a typo'd `commons.lang3.version`, `maven-dependency-plugin` pin);
  `flyway-mysql` bumped `11.14.1` → `12.4.0` to match Boot's newly-managed `flyway.version`
- Project version bumped to 7.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation
- Verified via the full test suite (492 tests), `./mvnw verify -Pcoverage` and manual Flyway commands against a real
  local MySQL 9.5 dev database
- New CLAUDE.md Git Workflow section states PR targets directly (`feature/*` → `develop`; `release/*`/`hotfix/*` →
  `main`); a false claim that AssertJ is used for assertions removed from five project docs

**Previous Update (v7.1.0):**

- `ShooterLogEntry` renamed to `ShooterLogCompetitor` (table `shooter_log_entry` → `shooter_log_competitor`)
- `ShooterLog.powerFactor` (`PowerFactor`, via the existing `PowerFactorConverter`, not nullable) scopes snapshots by
  power factor as well as firearm type
- `ShooterLogCompetitor.points` (nullable) and `ShooterLogCompetitor.match` (`@ManyToOne IpscMatch`, not nullable) added
- `ShooterLogRepository.findAllByCompetitorIdAndFirearmType` renamed to
  `findAllByCompetitorIdAndFirearmTypeAndPowerFactor`
- New `ShooterLogCompetitorRepository` (`findAllByShooterLogId`) supersedes `ShooterLogEntryRepository`
- `V7_1_0__update_shooter_log_schema.sql` Flyway migration renames the table/constraints and adds the new columns — no
  backfill needed, both tables remain empty
- Project version bumped to 7.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation
- No dedicated new unit/integration test coverage added for the renamed/rescoped entity in this release
- Repository AI agent prompt files migrated from `.github/prompts/*.prompt.md` to `.claude/commands/*.md`; `AGENTS.md`
  adopts GitFlow; `CONTRIBUTING.md` added

**Previous Update (v7.0.0):**

- Six entities promoted from `domain/old/` back into `za.co.hpsc.web.domain`; `.old` package removed entirely
- `Club.identifier` (`ClubIdentifier`, via `ClubIdentifierConverter`, unique) ties a club row to HPSC/SOSC/PMPSC
- `Competitor.homeClub` — nullable `@ManyToOne Club` relation for home-club membership
- `MatchCompetitor.matchRanking` renamed `overallRanking`; new `clubRanking` and `isVisitor` fields; new unique
  constraint `(competitor_id, match_id, firearm_type)`
- `MatchStageCompetitor` FK changed from `competitor` to `matchCompetitor`; new unique constraint
  `(match_competitor_id, match_stage_id)`
- `IpscMatchStage` gains new unique constraint `(match_id, stage_number)`
- New `ShooterLog`/`ShooterLogEntry` entities persist best-4-match shooter-log snapshots
- `repositories/` package rebuilt from scratch with 8 new `JpaRepository` interfaces
- No new enums or converters — `ClubIdentifier` and `FirearmType` reused as-is
- Project version bumped to 7.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation
- No dedicated new unit/integration test coverage added for the new/changed domain model in this release
- Statistics: 1 commit, 15 files changed, +207 insertions, -30 deletions

**Previous Update (v6.0.0):**

- `IpscMatchController` introduced at `/v2/ipsc/matches` with full CRUD (POST, PUT, PATCH, GET)
- `IpscMatchService` + `IpscMatchServiceImpl` added as dedicated match management service layer
- `MatchOnlyDto`, `MatchOnlyRequest`, `MatchOnlyResponse`, `MatchOnlyResultsDto` introduced
- `DomainServiceImpl` fully decoupled from JPA repositories; delegates exclusively to entity services
- New entity service methods: `findClubById`, `findCompetitorById`, `findMatchStageCompetitorById`
- `IpscUtil` utility class added for club and match display-string formatting
- All IPSC models moved from `models/ipsc/` to `models/ipsc/common/`; new `models/ipsc/match/` added
- Match search request models: `MatchSearchRequest`, `MatchSearchDateRequest`, `MatchSearchIdRequest`
- `IpscMemberController` stub registered at `/ipsc/member`
- Spring Boot upgraded 4.0.5 → 4.0.6; MIT licence and SCM metadata added to `pom.xml`
- 8 new test classes (~1,300 lines); `IpscControllerTest` removed (superseded)
- Statistics: 40 commits, 165 files changed, +6,779 insertions, -3,501 deletions

**Previous Update (v5.4.0):**

- `EnrolledCompetitorDto` introduced for first-class competitor enrolment tracking
- SAPSA number validation and competitor deduplication logic was added
- `IpscMatchService` renamed to `TransformationService` for clearer intent
- Package restructure: `ipsc/domain` → `ipsc/data` across all entities, repositories and services
- `CompetitorMatchRecord` split into `CompetitorRecord`, `CompetitorResultRecord`, `MatchCompetitorOverallResultsRecord`
  and `MatchCompetitorStageResultRecord`
- `MatchHolder` data class introduced for match context management
- `ClubIdentifier` enum extended with abbreviation field
- Qodana JVM linter (`jetbrains/qodana-jvm:2025.3`) and JaCoCo 0.8.14 code coverage added
- Significant test expansion across integration and unit test suites
- Statistics: ~75 commits, 123 files changed, +12,713 insertions, -13,358 deletions

**Previous Update (v5.3.0):**

- Six custom JPA attribute converters replacing `@Enumerated(EnumType.STRING)` across all enum-mapped fields
- Complete removal of `IpscMatchResultService` (interface + implementation, 379 lines) and `ScoreDto` (50 lines)
- `DtoMapping` converted from class to Java record for immutability
- Added `mappedBy` to all bidirectional `@OneToMany` entity relationships; cascade types fixed
- Repository queries optimised: scheduled date in match queries; `Set` deduplication for competitors; unnecessary fetch
  joins removed
- `ClubEntityService` simplified: removed `findClubById`, `findClubByName`, `findClubByAbbreviation`
- Test suite overhaul: DomainServiceTest (+787 lines), IpscMatchServiceTest (3,156 lines changed),
  TransactionServiceTest (1,031 lines changed), IpscServiceIntegrationTest (113 lines changed)
- Removed IpscMatchResultServiceTest (1,802 lines) and ScoreDtoTest (643 lines)
- Spring Boot upgraded from 4.0.3 to 4.1.0-SNAPSHOT
- Statistics: ~45 commits, 59 files changed, +5,686 insertions, -4,613 deletions

**Previous Update (v5.2.0):**

- Three-tier mapping architecture (DtoMapping, EntityMapping, DtoToEntityMapping)
- Enhanced match entity handling with a dedicated MatchEntityService
- Comprehensive test consolidation: DtoToEntityMappingTest (716 lines), TransactionServiceTest (2,000+ lines)
- Major service refactoring: IpscMatchServiceImpl (246 lines), IpscMatchResultServiceImpl (333 lines),
  TransactionServiceImpl (198 lines)
- Enhanced null safety with array initialisation and Optional return types
- Removed 3,000+ lines of duplicate tests across all test suites
- Consolidated utility tests: DateUtil, NumberUtil, StringUtil, ValueUtil
- All tests follow consistent `testMethod_whenCondition_thenExpectedBehavior` naming pattern with AAA comments
- Statistics: 26 commits, 61 files changed, +13,567 insertions, -5,898 deletions

**Previous Update (v5.1.0):**

- Test suite reorganisation with 6 logical sections
- Duplicate test elimination (1 duplicate removed)
- Enhanced test readability with clear section headers and visual separators
- Consolidated 24 tests to 23 with improved maintainability
- Section-based grouping: Null Input Handling, Null Collections & Fields, Match Name Field Handling, Club Fields
  Handling, Partial/Complete Data Scenarios, Edge Cases
- All tests follow consistent `testMethod_whenCondition_thenExpectedBehavior` naming pattern
- 100% test pass rate maintained (23 passing, 1 skipped as expected)
