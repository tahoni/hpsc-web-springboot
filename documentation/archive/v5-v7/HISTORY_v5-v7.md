# HPSC Website Backend History Archive (v5.0.0 – v7.4.1)

The per-version narrative history of versions 5.0.0 to 7.4.1 of the HPSC Website Backend project, archived from
[`HISTORY.md`](/HISTORY.md) to keep that file a manageable size. The entries are moved unchanged, grouped under the
`HISTORY.md` section each came from. See [`HISTORY.md`](/HISTORY.md) for the sections that span every version (Feature
Timeline, Key Learnings, Project Philosophy Evolution and Conclusion) and for versions 10.0.0 onwards,
[`CHANGELOG_v5-v7.md`](/documentation/archive/v5-v7/CHANGELOG_v5-v7.md) for the archived change log,
[`EVOLUTION_OVERVIEW_v5-v7.md`](/documentation/archive/v5-v7/EVOLUTION_OVERVIEW_v5-v7.md) for the archived
Phase-by-phase narrative and [`HISTORY_v1-v4.md`](/documentation/archive/v1-v4/HISTORY_v1-v4.md) for versions 1.0.0 to
4.1.0.

---

## Table of Contents

- [📅 Historical Timeline](#-historical-timeline)
- [🎯 Major Milestones](#-major-milestones)
- [🏛️ Architectural Evolution](#-architectural-evolution)
- [🛤️ Future Roadmap Implications](#-future-roadmap-implications)

---

## 📅 Historical Timeline

### Version 7.4.1 (August 29, 2026)

**Theme:** Documentation Reflow & Historical Narrative Additions

**Key Focus:**

- Every root-level documentation file (`AGENTS.md`, `ARCHITECTURE.md`, `CLAUDE.md`, `CONTRIBUTING.md`,
  `CHANGELOG.md`, `HISTORY.md`, `README.md`, `RELEASE_NOTES.md`) rewrapped to a consistent ~120-character line width,
  matching CLAUDE.md's existing wrap width — prose, list items and table columns realigned, with a handful of incidental
  copyedits (AGENTS.md's own serial-comma rule example corrected to follow the rule it states) surfacing along the way
- New "Major Version Goals" subsection under this file's Project Philosophy Evolution, summarising the driving goal
  behind each major version line (4.x, 5.x, 6.x, 7.x)
- New "Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)" entry, capturing the test-convention,
  documentation-accuracy and AI-agent-tooling work spanning those three releases
- Project version bumped to 7.4.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in
  `HpscWebApplication.java`

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
  to fill in missing `CHANGELOG.md` entries automatically; `RELEASE_NOTES.md`'s Contributors section now
  sourced from `git log`'s unique authors rather than a generic placeholder
- Release hygiene: `log4j-api` overridden to `2.25.5` for CVE-2026-49844; `.gitignore`/`.aiignore` refreshed
  from upstream templates; `README.md`'s H1 heading restored
- Project version bumped to 7.4.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in
  `HpscWebApplication.java`

### Version 7.3.0 (August 25, 2026)

**Theme:** Documentation Accuracy Pass & PR Summary Tooling

**Key Focus:**

- New Claude Code command `/generate-pr-summary`, which condenses a version's archived `PR_DESCRIPTION_vX.Y.Z.md` and
  `RELEASE_NOTES_vX.Y.Z.md` into a short, plain, Bitbucket-style PR summary — a distillation rather than a restatement
  of this repo's own emoji-heavy documentation style; its Output instructions were subsequently clarified to require
  the raw, unrendered Markdown source in the fenced block
- `README.md`'s Introduction and Features sections corrected to stop describing match management,
  competitor/club CRUD, WinMSS import and XML/multi-format processing as existing capabilities; only
  `AwardController`/`ImageController` CSV processing is implemented today, with the match/competitor domain's service
  and controller layer still being rebuilt (as already noted in `CLAUDE.md`)
- `README.md`'s coverage-report instructions corrected from the non-functional `./mvnw test jacoco:report` to
  `./mvnw verify -Pcoverage`; the stray `1.x – 4.x` version range in its documentation-map description removed, per
  AGENTS.md's evergreen-documentation rule
- `ARCHITECTURE.md`'s test package tree corrected (removed the nonexistent `domain/` test package, added the
  missing `converters/`/`exceptions/` packages) and its CI/CD & Quality Gates table no longer overstates the `Build &
  Tests` gate as an "All PRs" GitHub Actions trigger — only `codeql.yml` runs automatically; `./mvnw test` is run
  locally/by reviewers
- No domain entities, repositories, services or API surface changed in this release — purely a documentation-accuracy
  and tooling pass
- Project version bumped to 7.3.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in
  `HpscWebApplication.java`

### Version 7.2.0 (August 25, 2026)

**Theme:** Test Suite Conventions, AI-Agent Tooling and Dependency Maintenance

**Key Focus:**

- New interface-contract unit tests `services/AwardServiceTest`/`services/ImageServiceTest` (Mockito-based, testing
  `createAwards` through the `AwardService`/`ImageService` interface type rather than the impl class); new tests closing
  4 JaCoCo-identified coverage gaps in `ControllerResponseTest`, `FirearmTypeTest` and `ControllerAdviceTest` — overall
  suite coverage rose from 95.7%/91.7% to 97.3%/98.1% (line/branch)
- New Claude Code commands `/scaffold-unit-tests` (migrated from a stale, wrong-project prompt file and corrected to
  this repo's real interface/impl test split) and `/scaffold-integration-tests` (new, `@SpringBootTest`-based, following
  `AwardServiceIntegrationTest`/`ImageServiceIntegrationTest` as the template)
- `HpscWebApplicationTests` renamed to `HpscWebApplicationTest` to match the project's `<ClassName>Test` naming
  convention; 26 existing test files retrofitted with a new AGENTS.md test convention (a one-line `// methodName()`
  header per method group, ordered constructors → public → protected → alphabetical → `toString()` last) — no test
  behaviour changed, purely comments and reordering
- **Dependency maintenance:** Spring Boot parent upgraded `4.0.7` → `4.1.0`, with now-redundant `pom.xml`
  version overrides cleaned up (`spring-framework.version`/`tomcat.version` now match Boot's own defaults; a
  long-standing `commons.lang3.version` typo — Boot's real property is hyphenated — removed; `maven-dependency-plugin`
  pin removed, now Boot-managed) and the flyway-maven-plugin's separately-pinned `flyway-mysql` bumped `11.14.1` →
  `12.4.0` to match Boot's newly-managed `flyway.version`
- Verified via the full test suite (492 tests, up from 483 at the start of this release), `./mvnw verify -Pcoverage` and
  manual Flyway commands (`flyway:info`/`flyway:migrate`) against a real local MySQL 9.5 dev database — no domain
  entities, repositories or API surface changed in this release
- New CLAUDE.md Git Workflow section states the branching model's PR targets directly (`feature/*` → `develop`;
  `release/vX.Y.Z`/`hotfix/*` → `main`); CLAUDE.md now cross-links to AGENTS.md and corrects its package-overview table;
  a false claim that AssertJ is used for assertions (it is explicitly excluded from `pom.xml`) was removed from
  five project docs
- Project version bumped to 7.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in
  `HpscWebApplication.java`

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
- Project version bumped to 7.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in
  `HpscWebApplication.java`
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
- Project version bumped to 7.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in
  `HpscWebApplication.java`
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

---

## 🎯 Major Milestones

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

### Milestone 11: Service Consolidation & Type Safety (v5.3.0)

- Six custom JPA attribute converters for type-safe enum persistence
- IpscMatchResultService and ScoreDto removed; functionality consolidated
- DtoMapping converted to Java record for immutability
- All bidirectional @OneToMany relationships corrected with mappedBy
- Repository queries optimised with Set deduplication and scheduled date constraints

**Achievement:** Focused service consolidation and type-safety improvements simplifying the service architecture,
correcting JPA entity relationships and improving repository query accuracy.

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

### Milestone 15: Shooter Log Refinement (v7.1.0)

- `ShooterLogEntry` renamed to `ShooterLogCompetitor` for naming accuracy
- `ShooterLog.powerFactor` scopes best-4-match snapshots by power factor as well as firearm type
- `ShooterLogCompetitor` gains `points` and a direct `match` reference alongside `matchCompetitor`
- `ShooterLogCompetitorRepository` supersedes `ShooterLogEntryRepository`; `ShooterLogRepository` finder renamed to
  include `PowerFactor`
- Repository AI-agent tooling migrated to `.claude/commands/*.md`; `AGENTS.md` adopts GitFlow;
  `CONTRIBUTING.md` added

**Achievement:** Corrected the naming and scope of the v7.0.0 shooter-log data model before any calculation service is
built against it, keeping the schema accurate ahead of the service/controller wiring still to come.

---

### Milestone 16: Test Suite Conventions, AI-Agent Tooling and Dependency Maintenance (v7.2.0)

- 26 test files retrofitted with a new `// methodName()` header-comment/ordering convention; 4 JaCoCo-identified
  coverage gaps closed; suite coverage rose from 95.7%/91.7% to 97.3%/98.1% (line/branch)
- New `/scaffold-unit-tests` (corrected from a stale, wrong-project prompt) and `/scaffold-integration-tests` Claude
  Code commands
- Spring Boot parent upgraded `4.0.7` → `4.1.0`; redundant `pom.xml` version overrides cleaned up;
  `flyway-mysql` bumped to match Boot's newly managed Flyway version
- Verified via the full test suite (492 tests), `./mvnw verify -Pcoverage` and manual Flyway commands against a real
  MySQL dev database

**Achievement:** Brought the entire existing test suite into line with a newly formalised AGENTS.md convention, closed
every coverage gap JaCoCo could find and corrected/extended the AI-agent tooling — all without touching the domain
model, keeping the codebase consistent ahead of future feature work.

---

### Milestone 17: IPSC Request DTOs, Route Clean-up & Documentation Conventions (v7.4.0)

- New `models/ipsc/request`/`models/ipsc/shared` packages — `MatchRequest`/`MatchStageRequest`/`MatchStagesRequest`/
  `MatchOverallResultRequest`/`MatchStageResultRequest` (plus CSV variants) and `IpscCommonScore`/`IpscMatchScore`/
  `IpscMatchStageScore`, groundwork for the IPSC module rebuild
- `AwardController`/`ImageController` routes simplified from `/v1/awards`/`/v1/images` to `/awards`/`/images`
- New AGENTS.md Serial commas rule and a British English rule tightened to cover code identifiers, both applied across
  the existing documentation set; `documentation/roadmap/` renamed to kebab-case
- New `/sync-unreleased-changes` Claude Code command; `RELEASE_NOTES.md` Contributors now sourced from `git
  log`

**Achievement:** Laid IPSC request-DTO groundwork for the module rebuild while cleaning up a stale API route and
tightening the project's own documentation conventions — no domain/service/architecture changes.

---

### Milestone 18: Documentation Reflow & Historical Narrative Additions (v7.4.1)

- Entire root-level documentation set rewrapped to a consistent ~120-character line width
- New "Major Version Goals" and "Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)" narrative sections added
  to `HISTORY.md`

**Achievement:** Brought every root documentation file to a consistent line width and filled in two gaps in the
project's own historical narrative — no domain/service/architecture or test changes.

---

## 🏛️ Architectural Evolution

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
- Spring Boot parent `4.0.7` → `4.1.0`, with redundant `pom.xml` overrides removed and `flyway-mysql` kept in
  sync with Boot's managed `flyway.version`

---

## 🛤️ Future Roadmap Implications

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

### Previously Completed (v7.1.0)

- `ShooterLogEntry` renamed to `ShooterLogCompetitor` (table `shooter_log_entry` → `shooter_log_competitor`)
- `ShooterLog.powerFactor` (`PowerFactor`, not nullable) scopes snapshots by power factor as well as firearm type
- `ShooterLogCompetitor.points` (nullable) and `ShooterLogCompetitor.match` (`@ManyToOne IpscMatch`, not nullable) added
- `ShooterLogRepository.findAllByCompetitorIdAndFirearmType` renamed to
  `findAllByCompetitorIdAndFirearmTypeAndPowerFactor`
- New `ShooterLogCompetitorRepository` supersedes `ShooterLogEntryRepository`
- Project version bumped to 7.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v7.2.0)

- New interface-contract unit tests `AwardServiceTest`/`ImageServiceTest`, exercising `createAwards` through the
  interface type rather than the impl class
- 4 JaCoCo-identified coverage gaps closed (`ControllerResponse`, `FirearmType.toString()`,
  `ControllerAdvice.logError`); suite coverage rose from 95.7%/91.7% to 97.3%/98.1% (line/branch)
- `HpscWebApplicationTests` renamed to `HpscWebApplicationTest`; 26 existing test files retrofitted with a new
  `// methodName()` header-comment/ordering convention
- New `/scaffold-unit-tests` (corrected from a stale, wrong-project prompt) and `/scaffold-integration-tests` Claude
  Code commands
- Spring Boot parent upgraded `4.0.7` → `4.1.0`; redundant `pom.xml` version overrides removed; `flyway-mysql`
  bumped `11.14.1` → `12.4.0` to match Boot's newly-managed `flyway.version`
- Project version bumped to 7.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation
