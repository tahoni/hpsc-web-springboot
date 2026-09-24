# HPSC Website Backend Evolution Overview

The full Phase-by-phase narrative of the HPSC Website Backend project's evolution, one entry per release —
split out from [`HISTORY.md`](/HISTORY.md) to keep that file a manageable size, since this section alone had grown
to roughly half of it. See `HISTORY.md`'s [📅 Historical Timeline](/HISTORY.md#-historical-timeline) for the
same releases summarised more concisely, and its [🎯 Major Milestones](/HISTORY.md#-major-milestones) for each
release's headline achievement.

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
- The score CSV variants' constructors now match their plain counterparts' signatures exactly, verified via a
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
  `patchCompetitor`/`patchMatch` field's previously untested success path
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

**Bug Fix**

- `IpscCompetitorServiceImpl.applyFields`/`patchCompetitor` stored the caller-supplied `emailAddresses` `List`
  reference directly onto the entity; an immutable list (e.g. `List.of(...)`) crashed with an unhandled
  `UnsupportedOperationException` when Hibernate merged an update, bypassing the exception hierarchy entirely. Found
  while adding genuinely-multiple-address test coverage (every existing test had only used a single-element list);
  both methods now defensively copy into a new `ArrayList`

**Test Coverage:**

- Existing CSV parsing, request/response model and bulk-import tests updated for the new `emailAddresses` shape and
  separator
- New multi-address tests (2+ emails in one list/CSV cell) added to `CompetitorRequestTest`,
  `IpscCompetitorServiceImplTest`, `IpscCompetitorServiceTest` and `IpscCompetitorServiceIntegrationTest`; 781 → 790
  tests

---

### Phase 23: Match Bulk CSV Import (v8.3.0)

**Duration:** September 2, 2026

Extends the IPSC match module with bulk CSV import, mirroring the competitor bulk-import convention v8.1.0
established — but designing its multi-stage cell format from a discarded first attempt rather than reusing an
existing pattern outright.

**Key Accomplishments:**

**Match Bulk CSV Import**

- `IpscMatchController.createMatches` (`POST /ipsc/matches/bulk`, consumes `text/csv`) parses CSV data into
  `MatchRequestForCSV` rows and creates each match via the existing `createMatch`
  validation/club/firearm-type/category-resolution logic, matching `IpscCompetitorController.createCompetitors`'s
  bulk-import shape
- New `MatchRequestForCSV` (CSV-mapped, `UpperCamelCase` headers, `@JsonCreator` constructor) and
  `MatchResponseHolder` models (`models/ipsc/match/`)
- `IpscMatchServiceImpl` gains three protected helpers: `readMatches` (CSV → `MatchRequestForCSV` rows, mirroring
  `IpscCompetitorServiceImpl`'s CSV-parsing pattern), `toRequest` (row → `MatchRequest`), and `parseStages`, which
  splits each `Stages` cell entry on its first `-` into a `MatchStageRequest`

**Stages Cell Design**

- CSV has no native concept of a repeated group, so a match's stages — a nested list on `MatchRequest` — needed a
  single-cell representation; an initial `numberOfStages` count field was implemented, then dropped in favour of a
  single semicolon-separated `stages` cell of `<stageNumber>-<stageName>` entries (e.g. `"1-Stage One;2-Stage Two"`)
  before either design reached `develop`, once it was clear a count alone couldn't carry each stage's name

**Architecture Highlights:**

- No layering change — extends the existing controller/service/model triad `IpscCompetitorController`'s bulk import
  already established, applied to the match domain

**Technical Focus:**

- Bulk data import (match CSV)
- CSV multi-value cell design (delimited-entry parsing vs. a count-only field)
- Request-model test coverage

**Test Coverage:**

- New unit tests across `IpscMatchController`/`Service`/`ServiceImpl`'s bulk import, and `MatchRequestForCSVTest`'s
  `UpperCamelCase` JSON/CSV (de)serialisation and required-field enforcement

---

### Phase 24: CI Build/Test Gate, Coverage Enforcement & CSV Persistence Clarity (v8.3.1)

**Duration:** September 2, 2026

A process-and-quality patch release: no new domain feature, but a completed CI build/test gate, the project's first
coverage-regression-enforcement rule (tightened twice within the same branch), and a resolved documentation
ambiguity around Award/Image CSV persistence — closing out work v8.1.1's coverage-regression fixes and Gap #4's
baseline-setting groundwork left open, plus Gap #3's design-intent question left open since v8.1.0.

**Key Accomplishments:**

**CI Build/Test Gate**

- New `.github/workflows/build.yml` runs `./mvnw verify -Pcoverage` on push/PR to `develop`/`main`, mirroring
  `codeql.yml`'s trigger branches — sets up JDK 25 via `actions/setup-java` (Maven-cached), builds/tests via
  `sh ./mvnw` (`mvnw` isn't tracked with the execute bit in git) and uploads the JaCoCo HTML/XML report as a build
  artefact

**Coverage Enforcement**

- New JaCoCo `check` execution in the `coverage` Maven profile enforces a `BUNDLE`-level `LINE`/`COVEREDRATIO`
  minimum, wired into the new CI gate so a coverage regression fails the build
- Set initially to `0.51` (51%) as a deliberately low regression backstop, then raised to `0.86` (86%) within the
  same branch: a fresh coverage run measured the real baseline at 98.16% line / 98.94% branch coverage (836 tests,
  up from 775 at v8.1.1) — tightening the floor further, closer to that baseline, is left as a follow-up once the
  86% threshold has run cleanly in CI

**CSV Persistence Clarity**

- `AwardService.createAwards()`/`ImageService.createImages()` confirmed intentionally stateless by design, not an
  unfinished persistence layer — `README.md`'s Award Ceremonies/Image Gallery bullets and `ARCHITECTURE.md`'s
  Service Layer table and Award/Image CSV Processing Flow section now say so explicitly

**Documentation**

- `ARCHITECTURE.md`/`CONTRIBUTING.md`'s CI/CD & Quality Gates tables updated to reflect the new gate and rule,
  dropping the stale "locally / by reviewers"/"All PRs" language
- `ARCHITECTURE.md`'s Award/Image CSV Processing Flow diagram and `documentation/roadmap/improvement-plan.md`'s
  Gap #3 Evidence corrected from the stale `processCsv()` method name to `createAwards()`/`createImages()`, renamed
  back in v8.0.0; `AwardControllerTest`/`ImageControllerTest`'s matching `// processCsv()` test-grouping comments
  corrected the same way
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md`: Gap #2 (no automatic build/test gate)
  closed; Gap #3 (CSV persistence ambiguity) closed; Gap #4 (coverage measured but not enforced) marked partially
  progressed

**Architecture Highlights:**

- No architectural change — this release is CI/CD tooling and documentation work only

**Technical Focus:**

- CI/CD quality gate completion (build/test automation)
- Coverage-regression enforcement (tightened in two steps, still short of the real baseline)
- Documentation-intent clarification (Award/Image CSV persistence)

**Test Coverage:**

- No dedicated new unit/integration test coverage added for this release; verified via the full test suite
  (836 tests), `./mvnw verify -Pcoverage` and a fresh baseline measurement (98.16%/98.94% line/branch)

---

### Phase 25: Club Domain Defaults, Optional Club Numbers & Documentation Convention Hardening (v8.4.0)

**Duration:** September 3, 2026

A domain-and-documentation release: two real behavioural changes to the IPSC club-handling rules — matches now
default to a shared `ALL` club instead of failing validation, and competitor club numbers are required only for HPSC
members — alongside the coverage-regression floor finally reaching its real baseline and a round of convention
hardening (member ordering, REST naming, a release-checklist tree-accuracy backstop) that closes Gap #4 and Gap #9,
both left open or partially progressed since v8.1.1/v8.3.1's coverage groundwork.

**Key Accomplishments:**

**Club Domain Defaults**

- New `ClubIdentifier.ALL` constant (`"Eufees Clubs"` / `"All"` / `"ALL"`) represents a match hosted jointly by
  `SOSC`/`HPSC`/`PMPSC`; new `V7_3_0__seed_club_data.sql` migration seeds the `club` table with every named
  `ClubIdentifier` constant
- `IpscMatchServiceImpl.resolveClub()` now defaults a missing/blank match `club` to
  `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` instead of `validateForCreate` throwing `ValidationException`,
  mirroring the competitor domain's existing "apply the domain default" pattern; throws `NonFatalException` if even
  the default club is missing from the database, or a new `FatalException` if the constant itself is null. Closes
  Gap #9

**Optional Club Numbers**

- `IpscCompetitorServiceImpl.resolveClubNumber()` centralises a new rule: `clubNumber` is required only when the
  competitor's home club is HPSC, and forced to `null` otherwise, on create, update and patch
- `Competitor.clubNumber` column relaxed to nullable via new `V7_4_0__make_club_number_nullable.sql`, clearing
  `club_number` on any existing non-HPSC competitor

**Coverage Enforcement Completion**

- JaCoCo `LINE`/`COVEREDRATIO` floor tightened `0.86` → `0.97`, now genuinely near the real baseline after holding
  cleanly in CI across the `develop` and `main` runs that shipped v8.3.1; a final measurement this release
  (98.44%/98.98% line/branch, 868 tests) confirms it holds with room to spare. Closes Gap #4

**Convention Hardening**

- New `AGENTS.md` Member ordering convention (constructors → public → protected → private, private helpers always
  last); `IpscCompetitorServiceImpl`/`IpscMatchServiceImpl` reordered to match
- REST URL-path/handler-naming rules promoted from `standard-rest-conventions.md`'s recommendations into an actual
  `AGENTS.md` convention
- New Release Checklist step verifies `ARCHITECTURE.md`'s Project Structure tree against disk at every release — a
  backstop after this branch caught `.claude/skills/` and the removed `HpscConstants` class both stale in the tree
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` restructured into ✅ Completed/
  🟡 Partially Completed/⚪ Open sections, replacing the previous flat Now/Next/Later/Ongoing phasing

**Security**

- `tomcat-embed-core`/`-el`/`-websocket` overridden `11.0.24` → `11.0.25` via a new `pom.xml` `tomcat.version`
  property, closing three critical CVEs still pinned by `spring-boot-starter-parent:4.1.1`'s dependency management

**Architecture Highlights:**

- No structural architectural change — this release is domain-rule refinement (club defaulting/optional fields), a
  coverage-floor completion and documentation/convention hardening

**Technical Focus:**

- Domain-default patterns extended from the competitor module to the match module
- Coverage-regression enforcement reaching its real, sustainable baseline
- Documentation and convention consistency (member ordering, REST naming, tree-accuracy backstop)

**Test Coverage:**

- New `resolveClub(String, ClubIdentifier)` cases and `FatalException`/`NonFatalException` propagation coverage
  across `IpscMatchServiceImplTest`/`IpscMatchServiceIntegrationTest`/`IpscMatchControllerTest`; suite grew from 836
  to 868 tests, verified via a fresh `./mvnw verify -Pcoverage` run (98.44%/98.98% line/branch)

---

### Phase 26: Documentation Cross-Reference Consolidation & Icon Registry Sync (v8.4.1)

**Duration:** September 4, 2026

A documentation-only patch release: condenses `AGENTS.md`/`CONTRIBUTING.md`'s duplicated content into
cross-references, backfills the icon registry and cleans up latent `CHANGELOG.md`/`AGENTS.md` defects — no
domain-model, API or test-behaviour change.

**Key Accomplishments:**

**Cross-Reference Consolidation**

- `AGENTS.md`/`CONTRIBUTING.md`'s full/near-verbatim content duplicates condensed into highlights-and-link
  references — Git Workflow's Branching Model, Conventions and Directory Tree Maintenance bullets, the Exception
  handling and CHANGELOG-same-change/Evergreen bullets, and the CI/CD & Quality Gates table (now pointed at
  `ARCHITECTURE.md`, its actual source of truth). Git Workflow's "Merging" subsection consolidated as
  `CONTRIBUTING.md`'s sole canonical copy, since `sync-unreleased-changes`/`sync-improvement-plan-gaps` need only
  the Branching Model and Conventions subsections to remain in `AGENTS.md`
- New `AGENTS.md` "🧩 Claude Code Skills" and "🗺️ Roadmap Planning" sections, both mirrored with a short pointer
  in `CONTRIBUTING.md`

**Icon Registry Sync**

- `AGENTS.md`'s icon registry backfilled with 25 previously-unregistered icons already in real use, plus a new
  "Reserved" sub-table tracking the sibling `hpsc-web-vite` repository's frontend-specific icons — synced twice
  this release as `hpsc-web-vite`'s own registry grew, reciprocally gaining `🧬` (Data model / DTOs) in return.
  Several icon collisions resolved across `README.md`, `ARCHITECTURE.md`, `HISTORY.md`, `RELEASE_NOTES.md` and 17
  archived per-version release notes
- `CHANGELOG.md`'s duplicate, truncated `[5.0.0]` section removed, and a pre-existing broken example in the Serial
  Commas convention (identical "e.g." and "not" contrast phrases) corrected

**Build & Metadata**

- Project version bumped to 8.4.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No architectural change — this release only restructures/cross-references existing documentation and fixes
  latent documentation defects

**Technical Focus:**

- Documentation cross-reference consolidation, reducing duplicated source-of-truth content
- Icon registry completeness and cross-repository icon consistency

**Test Coverage:**

- No test changes — this release touches only Markdown documentation and version metadata

---

### Phase 27: Root Document Title Standardisation & Source-of-Truth Clarification (v8.4.2)

**Duration:** September 4, 2026

A documentation-only patch release: clarifies `AGENTS.md` as the project's ultimate source of truth for conventions
and standardises root document titles to a consistent project-name prefix — no domain-model, API or test-behaviour
change.

**Key Accomplishments:**

**Source-of-Truth Clarification**

- `AGENTS.md` now states, right before its Documentation File Map, that it is this project's ultimate source of
  truth for conventions — every other file's workflow/convention guidance points back to it rather than restating it
- `CONTRIBUTING.md`'s intro carries the matching pointer, stating `AGENTS.md` wins if anything else in the
  repository's documentation ever contradicts it

**Root Document Title Standardisation**

- `CHANGELOG.md`'s title changed from "Changelog" to "HPSC Website Backend", with a new "🧾 Change Log"
  second-level heading beneath it, matching `README.md`'s existing project name; every heading below it — Table of
  Contents, each version and their category/area sub-headers — demoted one level to nest correctly under the new
  heading
- `CONTRIBUTING.md`/`HISTORY.md`'s H1 titles gain the same "HPSC Website Backend" prefix for consistency

**Build & Metadata**

- Project version bumped to 8.4.2 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No architectural change — this release standardises document titles and clarifies documentation precedence only

**Technical Focus:**

- Documentation precedence clarity (a single, explicit source of truth)
- Root document title consistency

**Test Coverage:**

- No test changes — this release touches only Markdown documentation and version metadata

---

### Phase 28: Match Start/End Time Tracking (v8.5.0)

**Duration:** September 4, 2026

A domain feature release: adds nullable start/end timestamps to `IpscMatch`, wired end-to-end through the request/
response models, CSV bulk import and service layer, alongside a handful of British English documentation corrections.

**Key Accomplishments:**

**Match Start/End Time Tracking**

- `IpscMatch` gains nullable `startTime`/`endTime` (`LocalDateTime`) columns, alongside the existing `scheduledDate`,
  via new `V7_5_0__add_ipsc_match_start_end_time.sql`; wired end-to-end through `MatchRequest`, `MatchRequestForCSV`
  and `MatchResponse`, and `IpscMatchServiceImpl`'s `applyFields`/`patchMatch`/`toRequest`/`toResponse`
- CSV bulk import (`POST /matches/csv`) now requires `StartTime`/`EndTime` header columns, like every other
  `MatchRequestForCSV` property, consistent with this endpoint's existing all-columns-required header validation —
  existing CSV templates need updating to add them (values may be left blank)

**Documentation Fixes**

- `README.md`'s "License" heading/prose corrected to British English "Licence"; `CONTRIBUTING.md`'s own Serial
  Commas rule example corrected to no longer violate the rule it illustrates; `AGENTS.md`'s British English
  exception for `LICENSE.md` narrowed to just the filename and the file's own content, so every other reference to
  it spells it "Licence" instead of carving out a wider exception

**Build & Metadata**

- Project version bumped to 8.5.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No structural architectural change — extends an existing entity (`IpscMatch`) with two new nullable columns
  following the established request/response/service wiring pattern

**Technical Focus:**

- Match-timing data completeness
- British English documentation consistency

**Test Coverage:**

- New coverage in `IpscMatchServiceIntegrationTest` proves the two-column round-trip through the real
  H2/Hibernate/JPA layer, not just mocked repositories; `IpscMatchServiceTest`'s CSV bulk-import test now supplies
  actual `StartTime`/`EndTime` values, closing the one gap where that mapping was never verified

---

### Phase 29: HISTORY.md Phase/Milestone Backfill & Release Re-Scoping to a Patch Version (v8.5.1)

**Duration:** September 13, 2026

A documentation-only patch release: backfills three releases' worth of missing `HISTORY.md` Phase/Milestone entries,
then discovers its own scope is documentation/tooling-only and re-scopes itself from a planned minor version down to
a patch — no domain-model, API or test-behaviour change.

**Key Accomplishments:**

**HISTORY.md Phase/Milestone Backfill**

- "📖 Evolution Overview"/"🎯 Major Milestones" backfilled with Phase 26/27/28 and Milestone 26/27/28 entries for
  v8.4.1, v8.4.2 and v8.5.0, summarising each release's already-written Historical Timeline content — closes
  `improvement-plan.md`'s Gap #10
- "Major Version Goals" Version 8.x entry extended from `v8.0.0 – v8.1.1` to `v8.0.0 – v8.5.1`, narrating the domain
  broadening and documentation-process discipline delivered across v8.2.0 – v8.5.1

**Improvement Plan Maintenance**

- `improvement-plan.md` gains a new "📋 At a Glance" gap-status index; its "🗺️ Roadmap" table's **Now**/**Next**
  rows refreshed to drop already-closed #2/#7 and promote #6; Gap #10 tracked from ⚪ Open through to ✅ Completed
  in both `improvement-plan.md` and `improvement-plan-tasks.md`

**Release Re-Scoping**

- This release's entire diff against `main` proved documentation/tooling-only, so it was re-scoped from the
  originally-planned `v8.6.0` **MINOR** version down to `v8.5.1` **PATCH**, per `CHANGELOG.md`'s Version Policy —
  matching the precedent set by v8.4.1/v8.4.2 — with the branch and every in-flight documentation reference renamed
  to match before this release-prep pass

**Chronological Consistency**

- "📖 Evolution Overview", "🎯 Major Milestones", "🏛️ Architectural Evolution" and "🗺️ Future Roadmap
  Implications" reordered to ascending (oldest-first), matching "✨ Feature Timeline"/"💡 Project Philosophy
  Evolution"'s existing convention — only "📅 Historical Timeline" keeps its most-recent-first order.
  Architectural Evolution's `v5.3.0`/`v5.4.0` entries, previously stranded in a broken mixed order after `v8.0.0`,
  are now correctly interleaved between `v5.2.0` and `v6.0.0`
- Stale "Document Created"/"Last Updated"/"Coverage" metadata and the "Recent Updates"/"Previous Update"
  bold-labelled update log removed from the end of the Conclusion section — badly out of date (stopped at
  v8.0.0/v5.1.0) and superseded by this file's own proper narrative sections

**Tooling & Minor Fixes**

- `prep-version-release`/`generate-pr-summary` skills now end their drafted PR description/summary with the
  standard Claude Code attribution footer
- `CONTRIBUTING.md` spells out "and" instead of "&"; a Flyway baseline comment in `application-local.properties`
  tightened

**Build & Metadata**

- Project version bumped to 8.5.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No architectural change — this release backfills historical documentation, reorders and prunes stale content
  within `HISTORY.md`, and corrects its own version scope only

**Technical Focus:**

- Documentation completeness (closing a three-release Phase/Milestone backlog)
- Internal chronological consistency across `HISTORY.md`'s own sections
- Correct Semantic Versioning classification of documentation-only releases

**Test Coverage:**

- No test changes — this release touches only Markdown documentation, two Claude Code skill files and version
  metadata

---

### Phase 30: Match URL Field, Start/End Time Precision Fix & Test Architecture Formalisation (v8.6.0)

**Duration:** September 23, 2026

A domain correction release: adds a nullable `url` field to `IpscMatch`, corrects `startTime`/`endTime` from
`LocalDateTime` to `LocalTime`, and formally documents the already-established 3-tier service test architecture.

**Key Accomplishments:**

**Match URL Field**

- `IpscMatch` gains a new nullable `url` column via `V7_6_0__add_ipsc_match_url.sql` — a URL with more information
  about a match (e.g. a results page or event listing); wired end-to-end through `MatchRequest`,
  `MatchRequestForCSV`, `MatchResponse` and `IpscMatchServiceImpl`'s `applyFields`/`patchMatch`/`toRequest`/
  `toResponse`, with a matching `Url` column added to the CSV bulk import header

**Start/End Time Precision Fix**

- `IpscMatch.startTime`/`endTime` corrected from `LocalDateTime` to `LocalTime` via
  `V7_7_0__change_ipsc_match_start_end_time_to_time.sql` — these were always time-of-day-only values alongside
  `scheduledDate`, so the redundant date component v8.5.0 introduced is dropped. New
  `IpscConstants.IPSC_INPUT_TIME_FORMAT` (`HH:mm`) constant replaces `IPSC_INPUT_DATE_TIME_FORMAT` on both fields;
  JSON/CSV values are now bare `HH:mm` instead of `yyyy-MM-dd HH:mm` — existing CSV templates and API clients need
  updating to drop the date component

**Test Architecture Formalisation**

- `AGENTS.md`'s Test Conventions section now formally documents the 3-tier service test architecture
  (`<Service>Test`/`<Service>ImplTest`/`<Service>IntegrationTest`) already followed by all four services, replacing
  a one-line pointer that previously only described the pattern piecemeal across the `scaffold-unit-tests`/
  `scaffold-integration-tests` skills

**Build & Metadata**

- Project version bumped to 8.6.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No structural architectural change — extends `IpscMatch` with one new nullable column and corrects the Java type
  of two existing ones, following the established request/response/service wiring pattern

**Technical Focus:**

- Match-metadata completeness (`url`)
- Data-type correctness (time-of-day values no longer carry a redundant date component)
- Test convention documentation completeness

**Test Coverage:**

- `IpscMatchServiceIntegrationTest`, `IpscMatchServiceTest`, `IpscMatchServiceImplTest`, `MatchRequestTest` and
  `MatchRequestForCSVTest` updated so `url` round-trips through JSON, CSV import and the real H2/Hibernate/JPA
  layer, and `startTime`/`endTime` fixtures/CSV rows/JSON payloads reflect the new `LocalTime`/`HH:mm` shape

---

### Phase 31: documentation/history/ Reorganization & Evolution Overview Split (v8.6.1)

**Duration:** September 23, 2026

A documentation-only patch release: splits `HISTORY.md`'s largest section out into this file, regroups the
per-version archive into major-version subdirectories, and updates every tool/doc that reads or writes those
paths — no domain-model, API or test-behaviour change.

**Key Accomplishments:**

**Evolution Overview Split**

- `HISTORY.md`'s "📖 Evolution Overview" section (the Phase-by-phase narrative you are reading now) split out into
  this file, `documentation/history/EVOLUTION_OVERVIEW.md` — it had grown to roughly half of `HISTORY.md`'s
  4,095 lines. `HISTORY.md` keeps a short pointer section under the same heading/anchor, so its Table of Contents
  entry still resolves; every other section stays in `HISTORY.md` unchanged

**documentation/history/ Reorganization**

- All 52 archived `RELEASE_NOTES_vX.Y.Z.md`/`PR_DESCRIPTION_vX.Y.Z.md` files regrouped from a flat
  `documentation/history/` directory into `v1/` – `v8/` subdirectories by major version, moved with `git mv` to
  preserve history; this file is unaffected, staying directly in `documentation/history/`
- `AGENTS.md`'s Documentation File Map and Release Checklist (two independent copies of the archive-path
  references), `README.md`'s Documentation table, and the `prep-version-release`/`generate-pr-summary`/
  `update-improvement-plan-gaps` skills all updated to read/write `documentation/history/v<major>/...` paths,
  deriving `<major>` from a version's leading number before the first `.`

**Release Scoping**

- This release's entire diff against `main` proved documentation/tooling-only, so it was scoped as `v8.6.1`
  **PATCH** rather than a new minor version, matching the precedent set by v8.4.1/v8.4.2/v8.5.1

**Build & Metadata**

- Project version bumped to 8.6.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No architectural change — this release reorganizes documentation structure only

**Technical Focus:**

- Documentation file-size and directory-scale management
- Keeping tooling (skills) and doc-structure descriptions (`AGENTS.md`/`README.md`) in sync with the new layout

**Test Coverage:**

- No test changes — this release touches only Markdown documentation, three Claude Code skill files and version
  metadata

---

### Phase 32: CHANGELOG.md Heading-Depth Convention Correction & Future Roadmap Refresh (v8.6.2)

**Duration:** September 24, 2026

A documentation-only patch release: corrects how the project's own conventions describe `CHANGELOG.md`'s heading
structure, which had drifted one level shallower than the file itself, and refreshes `HISTORY.md`'s stale
forward-looking roadmap lists — no domain-model, API or test-behaviour change.

**Key Accomplishments:**

**CHANGELOG.md Convention Correction**

- `AGENTS.md`, `CONTRIBUTING.md` and the `generate-commit-message`, `prep-version-release`, `scaffold-unit-tests`,
  `scaffold-integration-tests` and `sync-unreleased-changes` skills described `CHANGELOG.md` as
  `## 🧪 [Unreleased]` → `### <category>` → `#### <Area>`, while the file has actually used `###`/`####`/`#####`;
  every reference was corrected to match
- `AGENTS.md`'s Git Workflow Conventions now spell out the full `#### <category>` → `##### <Area>` nesting, reuse of
  existing Area names and the bold-lead-in bullet style; `generate-commit-message` also notes that security-relevant
  fixes belong under `#### 🔐 Security`
- Reverse-synced from the shared project template, which had already corrected the same drift in its own copy of
  these conventions — keeping this project's Conventional Commits prefixes and bold-lead-in bullet style rather than
  adopting the template's generic defaults

**Future Roadmap Refresh**

- `HISTORY.md`'s "🛤️ Future Roadmap Implications" Short-term/Medium-term lists refreshed against what has actually
  shipped: the club-seeding bullet reduced to its still-outstanding `Competitor.homeClub` backfill half (the `club`
  table was already seeded in v8.4.0 via `V7_3_0__seed_club_data.sql`), `ShooterLogEntry` renamed to
  `ShooterLogCompetitor`, "Medium-term (v7.x+)" relabelled "Medium-term (Later v8.x Releases)" and "Bulk match
  processing capabilities" dropped as delivered by v8.3.0's bulk CSV import
- Recorded as Gap #11 in `documentation/roadmap/improvement-plan.md` by this release's own improvement-plan audit,
  then closed within the same release; items overlapping the still-open Gap #6 were left in place

**Roadmap Icon & Structure Sync**

- `🛤️` now marks Roadmap (replacing `🗺️`) and `☑️` marks Checklist / success criteria in `AGENTS.md`'s icon
  registry, both moved out of the icons reserved for `hpsc-web-vite`; every live Roadmap, Future Roadmap
  Implications and Success Criteria heading switched to match, as did the two improvement-plan skills' stale `🚀`/`✅`
  references
- `improvement-plan.md`/`improvement-plan-tasks.md` synced with the shared project template's structure: project
  title, a note on the four kinds of gap an audit looks for, a fuller Related Documentation list, a note on
  annotating checked task items, and no more hard-coded gap count in the tasks file's intro
- The rest of `AGENTS.md`'s icon registry then followed: restructured to mirror the template's core table, with
  its backend / API service extension set adopted as this project's own and its component-based frontend set kept
  reserved, and every live heading realigned to match — Documentation Conventions (`✍️`), Documentation File Map
  (`🗺️`), Key Design Patterns (`🧭`), Data Flow (`🔃`), Development Guidelines (`🛠️`), Getting Started (`🚀`), At a
  Glance (`🌳`) and Related Documentation (`🔗`)

**Formatting**

- Table column padding realigned in `AGENTS.md`'s skills table and `README.md`'s Documentation table

**Release Scoping**

- This release's entire diff against `main` proved documentation/tooling-only, so it was scoped as `v8.6.2`
  **PATCH**, matching the precedent set by v8.4.1/v8.4.2/v8.5.1/v8.6.1

**Build & Metadata**

- Project version bumped to 8.6.2 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No architectural change — this release corrects documented conventions only

**Technical Focus:**

- Keeping written conventions (`AGENTS.md`, skills) consistent with the files they describe
- Reverse-syncing generic improvements from the shared project template while preserving project-specific choices

**Test Coverage:**

- No test changes — this release touches only Markdown documentation, five Claude Code skill files and version
  metadata

---

**For the full project history, see [HISTORY.md](/HISTORY.md)**
