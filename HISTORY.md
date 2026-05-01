# Project History

A comprehensive historical overview of the HPSC Website Backend project from inception to current
release, documenting the evolution of architecture, features, and design philosophy across all versions.

---

## 📑 Table of Contents

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

### Version 6.0.0 (May 1, 2026)

**Theme:** Dedicated Match CRUD API, Service Encapsulation & Package Restructuring

**Key Focus:**

- `IpscMatchController` introduced at `/v2/ipsc/matches` with full CRUD (POST, PUT, PATCH, GET)
- `IpscMatchService` + `IpscMatchServiceImpl` added as dedicated match management service layer
- `MatchOnlyDto`, `MatchOnlyRequest`, `MatchOnlyResponse`, `MatchOnlyResultsDto` introduced for
  match-only operations without stages
- `DomainServiceImpl` decoupled from JPA repositories — now delegates to entity services exclusively
- New entity service methods: `findClubById`, `findCompetitorById`, `findMatchStageCompetitorById`
- `IpscUtil` utility class added for club and match display-string formatting
- All IPSC models moved from `models/ipsc/` to `models/ipsc/common/`; new `models/ipsc/match/` sub-package
- Three new match search request models: `MatchSearchRequest`, `MatchSearchDateRequest`,
  `MatchSearchIdRequest`
- `IpscMemberController` stub registered at `/ipsc/member`
- `TransformationService.mapMatchOnly(MatchOnlyRequest)` added; `mapMatchResults` no longer throws
  `ValidationException`
- `ControllerAdvice` enhanced with structured logging across all exception handlers
- Spring Boot upgraded from 4.0.5 to 4.0.6; MIT licence and SCM metadata added to `pom.xml`
- 8 new test classes (~1,300 lines): `IpscMatchControllerTest`, `IpscMatchServiceTest`,
  `IpscMatchIntegrationTest`, `MatchOnlyDtoTest`, `MatchOnlyRequestTest`, `MatchOnlyResponseTest`,
  `MatchResponseTest`, `IpscUtilTest`; `IpscControllerTest` removed
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
- Complete removal of `IpscMatchResultService` and `ScoreDto`; functionality consolidated into
  `DomainService` and `IpscMatchService`
- `DtoMapping` transitioned from class to Java record construct for immutability
- Added `mappedBy` to all bidirectional `@OneToMany` entity relationships; fixed cascade types
- Repository query optimisation: scheduled date in match queries, `Set` for competitor deduplication,
  removed unnecessary fetch joins
- Major test updates: DomainServiceTest (+787 lines), IpscMatchServiceTest (3,156 lines changed),
  TransactionServiceTest (1,031 lines changed), IpscMatchResultServiceTest removed (1,802 lines),
  ScoreDtoTest removed (643 lines)
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

The HPSC Website Backend project has evolved through distinct phases, each addressing specific architectural
and feature requirements:

### 🏗️ Phase 1: Foundation (v1.0.0)

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

### 📈 Phase 2: Feature Expansion (v1.1.0 – v1.1.3)

**Duration:** January 14, 2026 – January 28, 2026

Rapid iteration adding award processing, improving code quality, and establishing documentation standards.

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

### 🔄 Phase 3: Architectural Transformation (v2.0.0)

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

- Comprehensive DTO layer (`MatchDto`, `MatchResultsDto`, `CompetitorDto`, `MatchStageDto`,
  `MatchStageCompetitorDto`, `MatchCompetitorDto`)
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

### 🎯 Phase 4: Domain Specialisation (v3.0.0)

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

### ✅ Phase 5: Quality Assurance & Simplification (v3.1.0)

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

### 🔍 Phase 6: Major IPSC Refactoring (v4.0.0)

**Duration:** February 11, 2026

Significant domain entity refactoring with comprehensive testing and improved validation.

**Key Accomplishments:**

**Domain Entity Refactoring**

- `Match` → `IpscMatch` entity rename
- `MatchStage` → `IpscMatchStage` entity rename
- `MatchRepository` → `IpscMatchRepository` repository rename
- Removed `MatchStageRepository` (consolidated into `IpscMatchStageRepository`)
- Updated all dependent classes across services, controllers, helpers, and tests

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

### 📦 Phase 7: CRUD Enhancement & API Maturity (v4.1.0)

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

### 🎖️ Phase 8: Semantic Versioning Transition (v5.0.0)

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

- **MatchStageDtoTest:** 48 tests covering constructors, init() methods, and toString() implementations
    - Single and dual-parameter constructor tests (11 tests)
    - init() method tests with null handling, partial/full population (19 tests)
    - toString() method tests with edge cases, club information, stage numbers (18 tests)
    - Edge cases: null fields, empty/blank strings, zero/negative/large stage numbers

- **ScoreDtoTest:** 26 tests covering all constructor patterns
    - No-argument constructor tests (3 tests)
    - ScoreResponse constructor tests with null/empty/blank handling (16 tests)
    - All-arguments constructor tests (3 tests)
    - Constructor equivalence tests (2 tests)
    - Edge cases: zero values, negative values, max values, empty/blank strings, partial population

- **MatchStageCompetitorDtoTest:** 77 tests providing comprehensive coverage
    - No-argument constructor tests (3 tests)
    - MatchStageCompetitor entity constructor tests with edge cases (10 tests)
    - CompetitorDto + MatchStageDto constructor tests (6 tests)
    - All-arguments' constructor tests with 28 parameters (3 tests)
    - init() method tests covering ScoreResponse, EnrolledResponse, MatchStageDto combinations (24 tests)
    - toString() method tests with comprehensive scenarios (29 tests)
    - Edge cases: null entities, partial/full population, zero/negative/max values, enum mapping (PowerFactor,
      Division, FirearmType, CompetitorCategory), stage percentage calculation, special characters, Unicode
      support, long strings

**Test Quality Metrics**

- Clear naming: All tests follow `testMethod_whenCondition_thenExpectedBehavior` pattern
- AAA structure: Arrange-Act-Assert pattern with clear comments throughout
- Comprehensive assertions: Multiple assertions per test validating all aspects
- Edge case coverage: Extensive null, empty, blank, and boundary value testing
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
    - Standardised parameter descriptions across all DTOs (MatchDto, CompetitorDto, ClubDto, MatchStageDto,
      ScoreDto, MatchStageCompetitorDto, MatchCompetitorDto)
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

### 🧪 Phase 9: Test Quality Enhancement (v5.1.0)

**Duration:** February 25, 2026

Strategic focus on test suite quality, organisation, and maintainability.

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

### 🏗️ Phase 10: Architecture Refactoring (v5.2.0)

**Duration:** February 27, 2026

Major architectural improvement focused on match results processing, entity initialisation, and comprehensive
test coverage.

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
    - Constructor, accessor, and setter tests
    - Null, empty, partial, and full data coverage
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

### 🆕 Phase 13: Dedicated Match CRUD API & Service Encapsulation (v6.0.0)

**Duration:** May 1, 2026

Introduced a versioned, resource-oriented match management API, completed the entity service
encapsulation layer, and restructured all IPSC model packages for long-term growth.

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
- **`IpscMatchServiceImpl`** (135 lines) — full implementation backed by `DomainService` and
  `TransactionService`

**Match-Specific Model Layer**

- `MatchOnlyDto` (82 lines) — lightweight match DTO; auto-resolves `FirearmType` and stamps
  `dateEdited` on init
- `MatchOnlyRequest` (49 lines) — JSON request body for match CRUD operations
- `MatchOnlyResponse` (83 lines) — response envelope returned by `IpscMatchController`
- `MatchOnlyResultsDto` (18 lines) — internal results holder
- `MatchSearchRequest`, `MatchSearchDateRequest`, `MatchSearchIdRequest` — future search support

**DomainServiceImpl — Repository Decoupling**

- Removed direct injection of all six JPA repositories from `DomainServiceImpl`
- All data access delegated to the entity service layer:
    - `ClubEntityService`, `CompetitorEntityService`, `MatchEntityService`
    - `MatchStageEntityService`, `MatchCompetitorEntityService`,
      `MatchStageCompetitorEntityService`
- New entity service methods: `findClubById`, `findCompetitorById`,
  `findMatchStageCompetitorById`

**IPSC Model Package Restructuring**

- All `models/ipsc/` classes promoted to `models/ipsc/common/` sub-package
- New sibling `models/ipsc/match/` sub-package for match-only models
- Old flat `models/ipsc/response/` (`ClubResponse`, `MatchResponse`) replaced by
  `models/ipsc/common/response/` counterparts

**IpscUtil — String Formatting Utility**

- `IpscUtil` (66 lines): `clubTostring`, `matchToString` — centralises `"Match @ Club (ABBR)"`
  display-string construction used across the match and club DTOs

**TransformationService Updates**

- `mapMatchOnly(MatchOnlyRequest)` method added for the match CRUD pipeline
- `mapMatchResults` no longer declares `throws ValidationException`

**Enhanced Logging & Error Handling**

- Structured logging added to all `ControllerAdvice` exception handlers (119 lines changed)
- `ValidationException` removed from handler method signatures

**Build & Metadata**

- Spring Boot upgraded 4.0.5 → 4.0.6
- MIT License, developer profile, and SCM connection added to `pom.xml`
- `logback-spring.xml` updated with additional logger configuration

**Test Coverage**

- **New (8 classes, ~1,300 lines):** `IpscMatchControllerTest`, `IpscMatchServiceTest`,
  `IpscMatchIntegrationTest`, `MatchOnlyDtoTest`, `MatchOnlyRequestTest`,
  `MatchOnlyResponseTest`, `MatchResponseTest`, `IpscUtilTest`
- **Updated:** `TransformationServiceTest` (+747 lines), `DomainServiceTest` (+247 lines),
  `TransactionServiceTest` (+246 lines), `ValueUtilTest` (+294 lines)
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

### 👥 Phase 12: Competitor Enrolment & Service Transformation (v5.4.0)

**Duration:** April 26, 2026

The most extensive single-release test expansion in the project's history, alongside competitor enrolment
support, a major service renaming, and CI/CD quality gate integration.

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
- New controller tests: `AwardControllerTest`, `ImageControllerTest`, `IpscControllerTest`,
  `ControllerAdviceTest`
- New converter tests: all 6 JPA attribute converters now have dedicated test classes
- New domain entity tests: `ClubTest`, `CompetitorTest`, `IpscMatchTest`, `IpscMatchStageTest`,
  `MatchCompetitorTest`, `MatchStageCompetitorTest`
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

### 🔌 Phase 11: Service Consolidation & Type Safety (v5.3.0)

**Duration:** March 15, 2026

Focused consolidation of services, introduction of custom JPA converters, and repository query optimisation.

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

- **DomainServiceImpl:** 270 lines changed – enhanced `initMatchEntities` with Javadoc; improved
  null handling
- **IpscMatchServiceImpl:** 546 lines changed – consolidated match results processing; removed
  commented-out code
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

### 🏁 Milestone 1: Project Foundation (v1.0.0)

- ✅ Initial Spring Boot application
- ✅ Image gallery CSV processing
- ✅ Basic API infrastructure
- ✅ Custom exception hierarchy

**Achievement:** Established the foundation for the HPSC platform with core image processing capabilities.

---

### 🚀 Milestone 2: Feature Expansion (v1.1.0 - v1.1.3)

- ✅ Award processing system
- ✅ OpenAPI documentation
- ✅ Comprehensive project documentation
- ✅ Code quality standards

**Achievement:** Expanded platform features and established professional documentation standards.

---

### 🔄 Milestone 3: Architectural modernisation (v2.0.0)

- ✅ Service-oriented architecture
- ✅ Comprehensive DTO layer
- ✅ Modular service design
- ✅ Transaction management

**Achievement:** Transformed from monolithic to modular architecture enabling better maintainability and
testing.

---

### 🎯 Milestone 4: Domain Specialisation (v3.0.0)

- ✅ IPSC-specific domain modelling
- ✅ Firearm-type classification
- ✅ Club entity reintroduction
- ✅ Comprehensive enum utilities

**Achievement:** Aligned domain model with IPSC standards for specialised shooting competition management.

---

### ✅ Milestone 5: Quality & Simplification (v3.1.0)

- ✅ Exception handling consolidation
- ✅ API documentation accuracy
- ✅ Error handling consistency
- ✅ Simplified architecture

**Achievement:** Improved code quality and simplified error handling while maintaining functionality.

---

### 🔍 Milestone 6: Domain Clarity (v4.0.0)

- ✅ Entity naming clarification
- ✅ Comprehensive test coverage
- ✅ Enhanced validation layers
- ✅ IPSC entity specialisation

**Achievement:** Clarified domain model through explicit entity naming (Match → IpscMatch) improving code
clarity.

---

### 📦 Milestone 7: Feature Completeness (v4.1.0)

- ✅ Full CRUD operations
- ✅ Complete API maturity
- ✅ Transactional consistency
- ✅ Production readiness

**Achievement:** Completed CRUD lifecycle enabling full data management capabilities.

---

### 🎖️ Milestone 8: Standards Adoption (v5.0.0)

- ✅ Semantic versioning adoption
- ✅ Entity initialisation framework
- ✅ Response generation pipeline
- ✅ Infrastructure consolidation

**Achievement:** Adopted industry standards and consolidated infrastructure for long-term maintainability.

---

### 🧪 Milestone 9: Test Quality Enhancement (v5.1.0)

- ✅ Test suite reorganisation with 6 logical sections
- ✅ Duplicate test elimination
- ✅ Standardised test naming conventions
- ✅ Enhanced test documentation and readability

**Achievement:** Improved test infrastructure quality through comprehensive reorganisation and consolidation.

---

### 🏗️ Milestone 10: Architecture Refactoring (v5.2.0)

- ✅ Three-tier mapping system (DtoMapping, EntityMapping, DtoToEntityMapping)
- ✅ Enhanced match entity handling with MatchEntityService
- ✅ Comprehensive test consolidation (2,000+ lines across multiple suites)
- ✅ Enhanced null safety and code quality
- ✅ Major service refactoring (61 files, +13,567 lines)

**Achievement:** Significant architectural improvement with cleaner separation of concerns, enhanced null
safety, and comprehensive test coverage across all services and utilities.

---

### 🆕 Milestone 13: Dedicated Match CRUD API & Service Encapsulation (v6.0.0)

- ✅ `IpscMatchController` introduced at `/v2/ipsc/matches` with full CRUD (POST, PUT, PATCH, GET)
- ✅ `IpscMatchService` + `IpscMatchServiceImpl` added as dedicated match management service
- ✅ `DomainServiceImpl` fully decoupled from repositories — entity services used exclusively
- ✅ All IPSC models moved to `models/ipsc/common/`; `models/ipsc/match/` sub-package introduced
- ✅ `IpscUtil` added for centralised club/match display-string formatting
- ✅ Spring Boot upgraded 4.0.5 → 4.0.6; MIT licence and SCM metadata populated in `pom.xml`

**Achievement:** Established a versioned, resource-oriented match management API and completed the
entity service encapsulation layer, ensuring `DomainServiceImpl` respects the layered architecture
described in CLAUDE.md. The IPSC model package restructuring provides dedicated homes for shared
and match-specific models as the domain grows.

---

### 👥 Milestone 12: Competitor Enrolment & Service Transformation (v5.4.0)

- ✅ `EnrolledCompetitorDto` introduced for enrolled competitor tracking through the IPSC pipeline
- ✅ `IpscMatchService` renamed to `TransformationService` for semantic clarity
- ✅ SAPSA number validation and competitor deduplication in `CompetitorDto`
- ✅ 20+ new test classes (~7,000 lines) — the largest single-release test expansion in project history
- ✅ Qodana JVM linting and JaCoCo code coverage integrated into the CI/CD pipeline

**Achievement:** Delivered the project's most comprehensive test suite expansion, introduced competitor
enrolment support and SAPSA validation, modernised the service naming for improved clarity, and
strengthened the CI/CD pipeline with static analysis and code coverage quality gates.

---

### 🔌 Milestone 11: Service Consolidation & Type Safety (v5.3.0)

- ✅ Six custom JPA attribute converters for type-safe enum persistence
- ✅ IpscMatchResultService and ScoreDto removed; functionality consolidated
- ✅ DtoMapping converted to Java record for immutability
- ✅ All bidirectional @OneToMany relationships corrected with mappedBy
- ✅ Repository queries optimised with Set deduplication and scheduled date constraints

**Achievement:** Focused service consolidation and type-safety improvements simplifying the service
architecture, correcting JPA entity relationships, and improving repository query accuracy.

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
- CI/CD quality gates: Qodana JVM static analysis + JaCoCo coverage

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

### 📊 Data Processing Features

- **v1.0.0:** Image CSV processing, MIME type inference
- **v1.1.0:** Award CSV processing
- **v2.0.0:** CAB file import, XML processing, UUID mapping
- **v3.0.0:** Firearm-type classification, enhanced scoring
- **v4.0.0:** Enhanced entity mapping, validation layers
- **v5.0.0:** Entity initialisation framework, record generation
- **v5.2.0:** Three-tier mapping architecture, enhanced match entity handling
- **v5.3.0:** Custom JPA converters; optimised repository queries; `Set`-based competitor deduplication
- **v5.4.0:** `EnrolledCompetitorDto`; SAPSA number validation; competitor deduplication by SAPSA+ID
- **v6.0.0:** `IpscUtil` for club/match string formatting; `MatchOnlyDto` match pipeline; match search
  request models (`MatchSearchRequest`, `MatchSearchDateRequest`, `MatchSearchIdRequest`)

### 🏛️ Domain Management Features

- **v1.0.0:** Image entities
- **v1.1.0:** Award entities
- **v2.0.0:** Match, Competitor, Stage entities
- **v3.0.0:** Club reintroduction, Firearm types
- **v4.0.0:** IpscMatch, IpscMatchStage entities
- **v5.0.0:** Advanced initialisation patterns
- **v5.2.0:** DtoMapping, EntityMapping, DtoToEntityMapping, MatchEntityService
- **v5.3.0:** Custom AttributeConverters for all enums; DtoMapping as Java record; corrected
  `@OneToMany` mappedBy declarations; ClubEntityService simplified
- **v5.4.0:** `EnrolledCompetitorDto`; `ClubIdentifier` abbreviation; records' restructuring;
  `domain` → `data` package; `MatchHolder`; `TransformationService`
- **v6.0.0:** `MatchOnlyDto`/`Request`/`Response` for match CRUD; `models/ipsc/common/` +
  `models/ipsc/match/` package split; entity service methods `findClubById`, `findCompetitorById`,
  `findMatchStageCompetitorById`; `DomainServiceImpl` fully decoupled from repositories

### 🌐 API Capabilities

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
- **v6.0.0:** `/v2/ipsc/matches` CRUD API (POST, PUT, PATCH, GET) via `IpscMatchController`;
  structured logging in `ControllerAdvice`; `IpscMemberController` stub at `/ipsc/member`

### 🧪 Testing Coverage

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
    - IpscMatchServiceTest: Renamed from IpscMatchEntityServiceImplTest with enhanced match results processing
      coverage
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
    - Updated major suites: DomainServiceTest (1,428), TransactionServiceTest (1,736),
      IpscServiceIntegrationTest (649), IpscServiceTest (737)
- **v6.0.0:** 8 new test classes (~1,300 lines) covering the match CRUD pipeline end-to-end
    - `IpscMatchControllerTest`, `IpscMatchServiceTest` (unit)
    - `IpscMatchIntegrationTest` (H2 integration — match persistence)
    - `MatchOnlyDtoTest`, `MatchOnlyRequestTest`, `MatchOnlyResponseTest`, `MatchResponseTest` (model)
    - `IpscUtilTest` (utility — string formatting edge cases)
    - `IpscControllerTest` removed; covered by `IpscMatchControllerTest`
    - Major suite updates: `TransformationServiceTest` (+747), `DomainServiceTest` (+247),
      `TransactionServiceTest` (+246), `ValueUtilTest` (+294)

### 📚 Documentation Quality

- **v1.0.0:** Inline Javadoc
- **v1.1.0:** Standardised documentation, OpenAPI
- **v1.1.2:** README and ARCHITECTURE guides
- **v3.0.0:** Enhanced Javadoc across codebase
- **v5.0.0:** RELEASE_NOTES, CHANGELOG, HISTORY
- **v5.2.0:** Comprehensive release documentation with breaking changes analysis
- **v5.3.0:** v5.3.0 release notes, changelog entry, history update; Javadoc for
  `IpscMatchStage.init()` and `findMatchByNameAndScheduledDate`
- **v5.4.0:** v5.4.0 release notes, changelog entry, history update; Javadoc on `EnrolledCompetitorDto`
  and `TransformationService` interface
- **v6.0.0:** v6.0.0 release notes, changelog entry, history update; CLAUDE.md added for AI
  assistant context

---

## 💡 Project Philosophy Evolution

### 🏗️ Initial Phase (v1.0.0)

**Focus:** Foundation & Basic Functionality

- Establish a working Spring Boot application
- Implement CSV data processing
- Create basic API endpoints
- Error handling foundation

### 📈 Growth Phase (v1.1.0 – v2.0.0)

**Focus:** Feature Expansion & Modularity

- Add new feature domains (awards)
- Introduce service-oriented architecture
- Establish documentation standards
- Improve code quality

### 🎯 Specialisation Phase (v3.0.0 – v4.0.0)

**Focus:** IPSC Domain Compliance & Quality

- Align with IPSC standards
- Enhance domain clarity
- Comprehensive testing
- Production readiness

### 🚀 Maturity Phase (v4.1.0 – v5.0.0)

**Focus:** Completeness, Standards & Infrastructure

- Complete CRUD capabilities
- Industry-standard versioning
- Infrastructure consolidation
- Entity initialisation framework

### 🔬 Refinement Phase (v5.1.0 – v5.2.0)

**Focus:** Quality, Architecture & Maintainability

- Test suite enhancement and consolidation
- Architectural refactoring with separation of concerns
- Enhanced null safety and robustness
- Comprehensive test coverage across all layers
- Code quality and maintainability improvements
- Test suite enhancement and refactoring
- Improved code maintainability
- Long-term maintainability

### 🔌 Consolidation Phase (v5.3.0)

**Focus:** Service Consolidation, Type Safety & Repository Efficiency

- Removal of unnecessary service abstractions (`IpscMatchResultService`)
- Custom JPA converters for explicit, type-safe enum persistence
- Immutable DtoMapping record for cleaner data flow
- Correct JPA bidirectional relationship declarations
- Repository query optimisation for performance and accuracy
- Continued test suite refinement and integration test expansion

### 👥 Enrolment Phase (v5.4.0)

**Focus:** Competitor Enrolment, Service Clarity & Comprehensive Test Coverage

- Introduce `EnrolledCompetitorDto` for member enrolment tracking through the IPSC pipeline
- Rename `IpscMatchService` to `TransformationService` for semantic accuracy
- SAPSA number validation and competitor deduplication in `CompetitorDto`
- Expand test coverage to all layers — controllers, converters, entities, exceptions, integration
- Establish Qodana JVM linting and JaCoCo coverage as CI/CD quality gates
- Package reorganisation (`domain` → `data`) and records restructuring for semantic clarity

### 🆕 API Productisation Phase (v6.0.0)

**Focus:** Versioned Match API, Repository Decoupling & Package Structure

- Introduce a dedicated, versioned match CRUD API (`/v2/ipsc/matches`) separate from the bulk-import
  controller
- Complete the entity service encapsulation: `DomainServiceImpl` no longer bypasses the service layer
  to reach JPA repositories
- Restructure all IPSC model packages under `models/ipsc/common/` with a dedicated `models/ipsc/match/`
  sub-package, providing clear boundaries for shared vs. match-specific models
- Centralise display-string construction in `IpscUtil`, eliminating scattered formatting logic
- Support future member management and match search features with stub controller and search request models

---

## 📚 Key Learnings

### 🏗️ Architectural Insights

1. **Service Modularity:** Breaking monolithic services (v2.0.0) dramatically improved testability and
   maintainability
2. **Domain Clarity:** Explicit entity naming (v4.0.0) reduced confusion and improved code navigation
3. **Test-Driven Quality:** Comprehensive test suites enabled confident refactoring and bug fixes
4. **Documentation Priority:** Early documentation (v1.1.2) established clear system understanding

### 🎨 Design Decisions

1. **DTO Layer:** Introduction in v2.0.0 created crucial separation between API contracts and domain models
2. **Firearm-Type Classification:** v3.0.0 restructuring improved IPSC compliance without major disruption
3. **Entity Initialisation Framework:** v5.0.0 consolidation provides a unified pattern for complex entity
   setup4. **Semantic Versioning:** Late adoption (v5.0.0) aligns with industry standards for future releases

### ⚙️ Technical Evolution

1. **Exception Handling:** Simplified approach (v3.1.0) improved maintainability without sacrificing clarity
2. **Validation Layers:** Multi-layered validation (v4.0.0) ensures data integrity across tiers
3. **Transaction Management:** Abstraction layer (v2.0.0) enables consistent data consistency patterns
4. **Test Coverage:** Growing investment from basic tests to comprehensive integration testing
5. **DTO Testing Excellence:** Post-v5.0.0 comprehensive DTO unit testing (151+ tests) establishes quality
   standards
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
    - Section-based grouping: Null Input Handling, Null Collections & Fields, Match Name Field Handling,
      Club Fields Handling, Partial/Complete Data Scenarios, Edge Cases
    - Improved test readability with clear headers and consistent formatting
    - Enhanced maintainability through reduced code duplication (1 duplicate test removed)
    - All tests follow `testMethod_whenCondition_thenExpectedBehavior` naming pattern
    - Consolidated IpscMatchResultServiceImplTest from 24 to 23 tests with zero reduction in effective
      coverage
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
    - `IpscMatchResultService` and `ScoreDto` fully removed; functionality consolidated into `DomainService`
      and `IpscMatchService`
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
    - 8 new test classes (~1,300 lines) covering controller, service, integration, DTO, and utility layers
    - Statistics: 40 commits, 165 files changed, +6,779 insertions, -3,501 deletions

---

## 🚀 Future Roadmap Implications

Based on the evolution to v6.0.0, the following areas are identified for future enhancement:

### ✅ Recently Completed (v6.0.0)

- ✅ `IpscMatchController` introduced at `/v2/ipsc/matches` with full CRUD (POST, PUT, PATCH, GET)
- ✅ `IpscMatchService` + `IpscMatchServiceImpl` added as dedicated match management service
- ✅ `MatchOnlyDto`, `MatchOnlyRequest`, `MatchOnlyResponse`, `MatchOnlyResultsDto` introduced
- ✅ `DomainServiceImpl` fully decoupled from JPA repositories; delegates to entity services only
- ✅ New entity service methods: `findClubById`, `findCompetitorById`, `findMatchStageCompetitorById`
- ✅ `IpscUtil` added for centralised club/match display-string formatting
- ✅ All IPSC models moved to `models/ipsc/common/`; `models/ipsc/match/` sub-package introduced
- ✅ Match search request models: `MatchSearchRequest`, `MatchSearchDateRequest`,
  `MatchSearchIdRequest`
- ✅ `IpscMemberController` stub registered at `/ipsc/member`
- ✅ Spring Boot upgraded 4.0.5 → 4.0.6; MIT licence and SCM metadata added to `pom.xml`
- ✅ 8 new test classes (~1,300 lines); `IpscControllerTest` removed

### ✅ Previously Completed (v5.4.0)

- ✅ `EnrolledCompetitorDto` introduced for enrolled competitor tracking through the IPSC pipeline
- ✅ `IpscMatchService` renamed to `TransformationService`; `TransformationServiceImpl` (1,098 lines)
- ✅ `ClubIdentifier` abbreviation field added; `ClubIdentifierConverter` updated
- ✅ SAPSA number validation and competitor deduplication in `CompetitorDto`
- ✅ 20+ new test classes (~7,000 lines) — the largest single-release expansion
- ✅ Qodana JVM linting (`jetbrains/qodana-jvm`) integrated into CI/CD
- ✅ JaCoCo 0.8.14 code coverage profile added; reports to `/coverage`
- ✅ Package restructure: `ipsc/domain` → `ipsc/data`; records and holders reorganised
- ✅ PCC Optics division constant and ControllerAdvice error handling fixed

### ✅ Previously Completed (v5.3.0 and earlier)

- ✅ Six custom JPA attribute converters (ClubIdentifier, CompetitorCategory, Division, FirearmType,
  MatchCategory, PowerFactor)
- ✅ IpscMatchResultService and ScoreDto removed; match result processing consolidated
- ✅ DtoMapping converted to Java record for immutability
- ✅ All @OneToMany relationships corrected with mappedBy declarations
- ✅ Repository query optimisation (Set deduplication, scheduled date, fetch join removal)
- ✅ Three-tier mapping architecture (DtoMapping, EntityMapping, DtoToEntityMapping)
- ✅ Enhanced match entity handling with MatchEntityService
- ✅ Comprehensive test consolidation across all services and utilities
- ✅ Test suite reorganisation and consolidation (from v5.1.0, v5.2.0)

### 🔄 Short-term (Minor Releases)

- Implement match search endpoints using `MatchSearchRequest`, `MatchSearchDateRequest`,
  `MatchSearchIdRequest`
- Full `IpscMemberController` implementation for member CRUD
- Complete Javadoc coverage for `IpscMatchController`, `IpscMatchService`, `IpscUtil`, `MatchOnlyDto`
- Performance optimisation for large-scale match processing
- Enhanced diagnostic logging

### 📦 Medium-term (v6.x+)

- REST API endpoints for enrolled competitor management
- Additional IPSC data format support
- Bulk match processing capabilities
- Enhanced error reporting and recovery
- Performance metrics and monitoring
- Advanced query optimisation

### 🎯 Long-term (v7.0+)

- Potential domain model expansions
- Real-time match result processing
- Enhanced integrations with external systems
- Advanced reporting and analytics

---

## 🎓 Conclusion

The HPSC Website Backend has evolved from a simple image gallery application into a sophisticated, specialised
platform for managing practical shooting competition data. This evolution demonstrates a commitment to:

- **Continuous Improvement:** Regular releases addressing quality, features, and standards
- **Domain Alignment:** Progressive refinement toward IPSC compliance and specialisation
- **Architectural Excellence:** Evolution from monolithic to modular, testable architecture with three-tier
  mapping and consolidated service boundaries
- **Standards Adoption:** Adoption of industry-standard practices (SemVer, documentation patterns)
- **Quality Focus:** Investment in comprehensive testing and documentation
- **Code Maintainability:** Systematic refinement of test organisation, consolidation, and architectural
  separation (v5.1.0, v5.2.0, v5.3.0, v5.4.0)
- **Type Safety:** Custom JPA converters ensuring explicit, testable enum persistence (v5.3.0)
- **Service Simplicity:** Removal of unnecessary abstractions for cleaner, more cohesive architecture
  (v5.3.0, v5.4.0)
- **Competitor Enrolment:** First-class tracking of competitor participation through dedicated DTOs and
  validation workflows (v5.4.0)
- **CI/CD Quality Gates:** Qodana JVM static analysis and JaCoCo coverage enforcement raising the quality
  baseline across the entire codebase (v5.4.0)
- **Versioned Match API:** Dedicated `/v2/ipsc/matches` controller providing resource-oriented CRUD
  separate from the bulk-import flow (v6.0.0)
- **Layer Enforcement:** `DomainServiceImpl` no longer reaches past entity services into repositories,
  fully realising the layered architecture (v6.0.0)

The transition to Semantic Versioning in v5.0.0, the test suite consolidation in v5.1.0, the major
architectural refactoring in v5.2.0, the service consolidation with custom converters in v5.3.0, the
competitor enrolment system with service transformation in v5.4.0, and the dedicated match CRUD API with
service encapsulation in v6.0.0 mark significant maturation points where the project demonstrates stable,
predictable releases with clear separation of concerns. These releases serve as a solid foundation for the
shooting club's digital operations, with a clear commitment to long-term maintainability and quality.

Version 5.3.0 delivers focused, high-value improvements: type-safe JPA converters, correct entity
relationships, optimised repositories, and a consolidated service architecture that reduces complexity
without sacrificing capability.

Version 5.4.0 extends that foundation with competitor enrolment tracking, SAPSA number validation,
`TransformationService` replacing `IpscMatchService`, a comprehensive package restructure from
`ipsc/domain` to `ipsc/data`, and a significant test expansion — all underpinned by Qodana JVM static
analysis and JaCoCo code coverage enforcement that set a new quality baseline for the project.

Version 6.0.0 marks a decisive architectural milestone: `DomainServiceImpl` no longer bypasses the
entity service boundary to reach JPA repositories, `IpscMatchController` establishes a versioned,
resource-oriented match API at `/v2/ipsc/matches`, and the IPSC model packages are restructured under
`models/ipsc/common/` and `models/ipsc/match/` — providing clear, scalable homes for shared and
match-specific models as the domain continues to grow.

---

**Document Created:** February 24, 2026  
**Last Updated:** May 1, 2026  
**Coverage:** Version 1.0.0 (January 4, 2026) through Version 6.0.0 (May 1, 2026)  
**Reference:** See [CHANGELOG.md](CHANGELOG.md) and [ARCHIVE.md](/documentation/archive/ARCHIVE.md) for
detailed technical information

**Recent Updates (v6.0.0):**

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
- Package restructure: `ipsc/domain` → `ipsc/data` across all entities, repositories, and services
- `CompetitorMatchRecord` split into `CompetitorRecord`, `CompetitorResultRecord`,
  `MatchCompetitorOverallResultsRecord`, and `MatchCompetitorStageResultRecord`
- `MatchHolder` data class introduced for match context management
- `ClubIdentifier` enum extended with abbreviation field
- Qodana JVM linter (`jetbrains/qodana-jvm:2025.3`) and JaCoCo 0.8.14 code coverage added
- Significant test expansion across integration and unit test suites
- Statistics: ~75 commits, 123 files changed, +12,713 insertions, -13,358 deletions

**Previous Update (v5.3.0):**

- Six custom JPA attribute converters replacing `@Enumerated(EnumType.STRING)` across all enum-mapped fields
- Complete removal of `IpscMatchResultService` (interface + implementation, 379 lines) and `ScoreDto`
  (50 lines)
- `DtoMapping` converted from class to Java record for immutability
- Added `mappedBy` to all bidirectional `@OneToMany` entity relationships; cascade types fixed
- Repository queries optimised: scheduled date in match queries; `Set` deduplication for competitors;
  unnecessary fetch joins removed
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
- Section-based grouping: Null Input Handling, Null Collections & Fields, Match Name Field Handling, Club
  Fields Handling, Partial/Complete Data Scenarios, Edge Cases
- All tests follow consistent `testMethod_whenCondition_thenExpectedBehavior` naming pattern
- 100% test pass rate maintained (23 passing, 1 skipped as expected)

