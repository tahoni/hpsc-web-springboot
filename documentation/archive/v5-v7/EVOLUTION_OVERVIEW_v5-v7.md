# HPSC Website Backend Evolution Overview Archive (v5.0.0 – v7.4.1)

The Phase-by-phase narrative for Phases 8 to 18 (versions 5.0.0 to 7.4.1) of the HPSC Website Backend project, archived
from [`EVOLUTION_OVERVIEW.md`](/EVOLUTION_OVERVIEW.md) to keep that file a manageable size. The entries are moved
unchanged. See [`EVOLUTION_OVERVIEW.md`](/EVOLUTION_OVERVIEW.md) for Phase 44 (v10.0.0) onwards,
[`HISTORY_v5-v7.md`](/documentation/archive/v5-v7/HISTORY_v5-v7.md) for the archived timeline, milestones and
architectural evolution, [`CHANGELOG_v5-v7.md`](/documentation/archive/v5-v7/CHANGELOG_v5-v7.md) for the archived change
log and [`EVOLUTION_OVERVIEW_v1-v4.md`](/documentation/archive/v1-v4/EVOLUTION_OVERVIEW_v1-v4.md) for Phases 1 to 7.

---

## 📖 Evolution Overview

The HPSC Website Backend project evolved through these distinct phases from v5.0.0 to v7.4.1, each addressing specific
architectural and feature requirements:

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

- Project version bumped to 7.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in
  `HpscWebApplication.java`

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

- Project version bumped to 7.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in
  `HpscWebApplication.java`

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
- Both commands defer to their loaded `AGENTS.md`/`CLAUDE.md` rather than restating conventions inline, accept
  multiple targets per invocation and never commit on their own
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

- Project version bumped to 7.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in
  `HpscWebApplication.java`

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
- `roadmap`'s `IMPROVEMENT_PLAN.md`/`TASKS.md` renamed to `improvement-plan.md`/
  `improvement-plan-tasks.md` for kebab-case consistency with the rest of the tooling docs

**AI-Agent Tooling & Process**

- New Claude Code command `/sync-unreleased-changes` — diffs the current branch against its base plus any uncommitted
  changes, cross-checks the result against `CHANGELOG.md`'s `[Unreleased]` section and fills in any missing
  entries directly in the file
- `RELEASE_NOTES.md`'s Contributors section now sourced from `git log`'s unique commit authors (bots included)
  instead of a generic placeholder, per a new AGENTS.md Release Checklist rule

**Release Hygiene**

- `log4j-api` overridden `2.25.4` → `2.25.5`, closing CVE-2026-49844 — a transitive dependency via
  `spring-boot-starter-logging`, never actually reachable since this project uses Logback, but the pin removes the
  flagged advisory
- `.gitignore`/`.aiignore` refreshed from the latest upstream templates; `README.md`'s H1 heading restored
  (lost in an earlier commit)

**Build & Metadata**

- Project version bumped to 7.4.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in
  `HpscWebApplication.java`

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

- Project version bumped to 7.4.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in
  `HpscWebApplication.java`

**Test Coverage:**

- No test changes — this release touches only Markdown documentation and version metadata

**Architecture Highlights:**

- No architectural change

**Technical Focus:**

- Documentation consistency (line width, table alignment)
- Historical-record completeness (major version goals, process-discipline narrative)
