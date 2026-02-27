# Changelog

All notable changes to the HPSC Website Backend project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) as of version 5.0.0.

---

## 📑 Table of Contents

- [🧪 Unreleased](#-unreleased)
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

### 🔄 Changed

### 🐛 Fixed

### ⚠️ Deprecated

### 🗑️ Removed

### 🔐 Security

---

## 🧾 [5.2.0] - 2026-02-27

### ➕ Added

#### 🏗️ Architecture & Domain Model

- **DtoMapping class:** New comprehensive DTO mapping with map-based storage for improved data organisation
- **EntityMapping class:** New entity-level mapping structure for clear separation of persistence concerns
- **DtoToEntityMapping class:** Bridge layer between DTOs and entities with Optional-based accessors (91
  lines)
- **MatchEntityHolder class:** Dedicated holder for match entity initialisation workflows
- **MatchEntityService interface:** Contract for match entity operations
- **MatchEntityServiceImpl:** Implementation with comprehensive initialisation logic

#### 🧪 Test Coverage

- **DtoToEntityMappingTest:** 716 lines of comprehensive tests covering all mapping scenarios
    - Constructor tests (3 scenarios)
    - MatchDto accessor tests (3 scenarios)
    - MatchEntity accessor tests (2 scenarios)
    - Competitor DTO list tests (6 scenarios)
    - MatchStage DTO list tests (6 scenarios)
    - MatchCompetitor DTO list tests (6 scenarios)
    - MatchStageCompetitor DTO list tests (6 scenarios)
    - Entity setter tests (12 scenarios covering all entity types)
    - Comprehensive null, empty, partial, and full data coverage
- **TransactionServiceTest:** 2,000+ lines with extensive edge case coverage
    - Null/empty/blank input tests
    - Partial and full input tests
    - Edge case handling
- **Enhanced test coverage** across all consolidated test suites with generateIpscMatchRecordHolder output
  verification

#### 📊 Service Enhancements

- **Array initialisation:** All DTO arrays initialised to empty arrays instead of null to prevent NPE
- **Club filtering:** Enhanced club abbreviation filtering logic in match entity initialisation
- **Optional return types:** `importWinMssCabFile()` now returns Optional for better null handling
- **Initialisation methods:** New dedicated methods for match-related entity initialisation

### 🔄 Changed

#### 🏗️ Core Services (Major Refactoring)

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

#### 🗄️ Entity Models

- **IpscMatch:** Simplified OneToMany annotations for better JPA relationships (7 lines changed)
- **IpscMatchStage:** Enhanced entity relationships (19 lines changed)
- **MatchCompetitor:** Updated relationships (22 lines changed)
- **MatchStageCompetitor:** Improved entity mapping (24 lines changed)
- **Club:** Minor updates (3 lines changed)
- **Competitor:** Minor updates (2 lines changed)

#### 📦 DTOs

- **MatchCompetitorDto:** Array initialisation to prevent null (6 lines changed)
- **MatchResultsDto:** Removed match entity reference (3 lines changed)

#### 🗂️ Repository Layer

- **IpscMatchRepository:** Updated for new entity structure (2 lines changed)

#### 🌐 Controllers

- **IpscController:** Updated for service changes (4 lines changed)

#### 🧪 Test Suites (Comprehensive Consolidation)

- **IpscMatchResultServiceImplTest:** 1,802 lines added – complete consolidation with enhanced coverage
    - Direct testing of initScores alongside indirect testing through initMatchResults
    - Null/empty/blank field edge cases
    - Partial and full field scenarios
    - Section-based organisation maintained from v5.1.0
- **IpscMatchResultServiceTest:** 2,197 lines removed - migrated to ImplTest
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

#### 🛡️ Null Safety

- **Array initialisation:** Initialised arrays to prevent null pointer exceptions in DTOs
- **Enhanced null checks:** Improved null safety throughout match result processing
- **Optional handling:** Better handling of Optional return types throughout the codebase

#### 🧪 Test Quality

- **Duplicate removal:** Eliminated duplicate test methods across multiple test suites
- **Disabled tests:** Removed disabled test annotations, all tests now active or properly skipped
- **Empty/partial handling:** Corrected handling of empty and partial match results
- **Assertion clarity:** Enhanced test assertion precision and clarity

#### 🧹 Code organisation

- **Unused dependencies:** Removed unused DomainService from integration tests
- **Mock clean-up:** Removed unused domain service mocks from test code
- **Import optimisation:** Streamlined test imports for better clarity

### ⚠️ Deprecated

None.

### 🗑️ Removed

#### 🏗️ Deprecated Code

- **Old MatchEntityHolder:** Replaced with new implementation
- **Match entity in DTOs:** Removed from MatchResultsDto for cleaner separation

#### 🧹 Configuration & IDE Files

- **JetBrains .idea files:** Removed all .idea configuration files from version control
- **Updated .gitignore:** Permanently exclude JetBrains config files
- **Unused properties:** Cleaned up application.properties

#### 🧪 Test Code

- **Duplicate tests:** Removed across all test suites (estimated 3,000+ lines of duplicates)
- **Unused mocks:** Removed unused DomainService mocks
- **Old test files:** Consolidated into Impl test files

### 🔐 Security

No security-related changes in this release.

---

## 🧾 [5.1.0] - 2026-02-25

### ➕ Added

#### 🧪 Test Suite Enhancements

- **Test organisation improvements** in `IpscMatchResultServiceImplTest`
    - Section-based test grouping for improved navigation and understanding
    - Six distinct test sections: Null Input Handling, Null Collections and Fields, Match Name Field Handling,
      Club Fields Handling, Partial and Complete Data Scenarios, Edge Cases
    - Clear separation of concerns between test categories

#### ✅ Test Quality Improvements

- **Comprehensive test coverage metrics** with detailed test categorisation
- **23 unit tests** covering all critical scenarios for IPSC match result service
- **Section-based documentation** for enhanced test maintainability

### 🔄 Changed

#### 🧪 Test Infrastructure

- **Test organisation:** Restructured `IpscMatchResultServiceImplTest` with logical section-based grouping
    - Null Input Handling section (2 tests)
    - Null Collections and Fields section (5 tests)
    - Match Name Field Handling section (3 tests)
    - Club Fields Handling section (2 tests)
    - Partial and Complete Data Scenarios section (6 tests)
    - Edge Cases section (4 tests)
    - Database Interaction section (1 skipped test)
- **Test naming:** Standardised naming conventions for consistency (
  `testMethod_whenCondition_thenExpectedBehavior`)
- **Code style:** Improved spacing and formatting for better readability
- **Documentation:** Enhanced test section comments with clear headers and visual separators

### 🐛 Fixed

#### 🧪 Test Quality

- **Duplicate test elimination:** Removed duplicate
  `testInitMatchResults_withMultipleStagesAndScores_thenMapsCorrectly()` test method
- **Code clean-up:** Removed TODO comment about adding sections (now complete)
- **Test file consolidation:** Ensured no redundant test coverage

### ⚠️ Deprecated

### 🗑️ Removed

- **Duplicate test:** `testInitMatchResults_withMultipleStagesAndScores_thenMapsCorrectly()` - Removed exact
  duplicate at the end of the file

### 🔐 Security

---

## 🧾 [5.0.0] - 2026-02-24

### ➕ Added

#### 🏗️ Domain Entity Initialisation Framework

- **`DomainServiceImpl.initClubEntity(ClubDto)`** - Initialise club entities from DTO objects with automatic
  database lookup and fallback to new entity creation
- **`DomainServiceImpl.initClubEntity(ClubIdentifier)`** - Initialise club entities from enumeration values
  for predefined club references
- **`DomainServiceImpl.initMatchEntity(MatchDto, Club)`** - Sophisticated match entity initialisation with
  repository lookup, optional entity creation, and club association
- **`DomainServiceImpl.initCompetitorEntities(List<CompetitorDto>)`** - Batch competitor entity initialisation
  with UUID generation and optional database persistence
- **`DomainServiceImpl.initMatchStageEntities(List<MatchStageDto>, IpscMatch)`** - Initialise match stages
  with proper relationship linking to parent match entities
- **`DomainServiceImpl.initMatchCompetitorEntities(List<MatchCompetitorDto>, Map<UUID, Competitor>)`** -
  Establish many-to-many relationships between matches and competitors
- **`DomainServiceImpl.initMatchStageCompetitorEntities(List<MatchStageCompetitorDto>, ...)`** - Complex
  initialisation of stage-specific competitor records with score and performance data

#### 📊 IPSC Match Record Generation

- **`IpscMatchServiceImpl.generateIpscMatchRecordHolder(List<IpscMatch>)`** - Convert IPSC match entities to
  comprehensive match records for external representation
- **`IpscMatchServiceImpl.initIpscMatchResponse(IpscMatch, List<CompetitorMatchRecord>)`** - Build complete
  IPSC match response records with embedded competitor data
- **`IpscMatchServiceImpl.initCompetitor(Competitor, MatchCompetitorRecord, List<MatchStageCompetitorRecord>)`
  ** - Create detailed competitor match records with stage-wise performance data
- **`IpscMatchServiceImpl.initMatchCompetitor(Competitor, List<MatchCompetitor>)`** - Extract and process
  match-level competitor records from database entities
- **`IpscMatchServiceImpl.initMatchStageCompetitor(Competitor, List<MatchStageCompetitor>)`** - Generate
  stage-specific competitor records with individual stage scores

#### 🔧 Service Layer

- **`IpscMatchResultServiceImpl`** - Enhanced with comprehensive null handling and processing for match
  results
    - Improved edge case handling
    - Better robustness in match result transformation
    - Additional null-safety checks

### ⚠️ Deprecated

### 🗑️ Removed

### 🔐 Security

---

## 🧾 [5.0.0] - 2026-02-24

### ➕ Added

#### 🏗️ Domain Entity Initialisation Framework

- **`DomainServiceImpl.initClubEntity(ClubDto)`** - Initialise club entities from DTO objects with automatic
  database lookup and fallback to new entity creation
- **`DomainServiceImpl.initClubEntity(ClubIdentifier)`** - Initialise club entities from enumeration values
  for predefined club references
- **`DomainServiceImpl.initMatchEntity(MatchDto, Club)`** - Sophisticated match entity initialisation with
  repository lookup, optional entity creation, and club association
- **`DomainServiceImpl.initCompetitorEntities(List<CompetitorDto>)`** - Batch competitor entity initialisation
  with UUID generation and optional database persistence
- **`DomainServiceImpl.initMatchStageEntities(List<MatchStageDto>, IpscMatch)`** - Initialise match stages
  with proper relationship linking to parent match entities
- **`DomainServiceImpl.initMatchCompetitorEntities(List<MatchCompetitorDto>, Map<UUID, Competitor>)`** -
  Establish many-to-many relationships between matches and competitors
- **`DomainServiceImpl.initMatchStageCompetitorEntities(List<MatchStageCompetitorDto>, ...)`** - Complex
  initialisation of stage-specific competitor records with score and performance data

#### 📊 IPSC Match Record Generation

- **`IpscMatchServiceImpl.generateIpscMatchRecordHolder(List<IpscMatch>)`** - Convert IPSC match entities to
  comprehensive match records for external representation
- **`IpscMatchServiceImpl.initIpscMatchResponse(IpscMatch, List<CompetitorMatchRecord>)`** - Build complete
  IPSC match response records with embedded competitor data
- **`IpscMatchServiceImpl.initCompetitor(Competitor, MatchCompetitorRecord, List<MatchStageCompetitorRecord>)`
  ** - Create detailed competitor match records with stage-wise performance data
- **`IpscMatchServiceImpl.initMatchCompetitor(Competitor, List<MatchCompetitor>)`** - Extract and process
  match-level competitor records from database entities
- **`IpscMatchServiceImpl.initMatchStageCompetitor(Competitor, List<MatchStageCompetitor>)`** - Generate
  stage-specific competitor records with individual stage scores

#### 🔗 IPSC Response Processing Pipeline

- **`IpscMatchServiceImpl.addClubToMatch(IpscResponse, IpscRequestHolder)`** - Intelligent club association
  logic that matches clubs from request data to match response records with fallback mechanisms
- **`IpscMatchServiceImpl.addMembersToMatch(IpscResponse, IpscRequestHolder)`** - Associate enrolled members
  with match responses based on match ID filtering

#### ⚙️ Enhanced IPSC Result Service

- **`IpscMatchResultServiceImpl.initMatchResults(IpscResponse)`** - Complete IPSC response-to-DTO
  transformation pipeline
- **`IpscMatchResultServiceImpl.initClub(ClubResponse)`** - Convert IPSC club response objects to club DTOs
  with database lookup and enrichment
- **`IpscMatchResultServiceImpl.initMatch(IpscResponse, ClubDto)`** - Create or update match DTOs from IPSC
  responses with optional database lookup and update avoidance
- **`IpscMatchResultServiceImpl.initStages(MatchDto, List<StageResponse>)`** - Map IPSC stage responses to
  match stage DTOs
- **`IpscMatchResultServiceImpl.initScores(MatchResultsDto, IpscResponse)`** - Process and aggregate
  competitor scores across match stages

#### 📦 DTO Architecture Enhancements

- **`ClubDto(Club)`** - Constructor for creating DTOs from club entities
- **`ClubDto(ClubResponse)`** - Constructor for creating DTOs from IPSC response objects
- **`ClubDto(ClubIdentifier)`** - Constructor for creating DTOs from enumerated club identifiers
- **`ClubDto(Club, ClubIdentifier)`** - Constructor supporting fallback initialisation from club identifier if
  entity is null support

### 🔄 Changed

#### 📐 Version Management

- **Adopted Semantic Versioning (SemVer):** Project now follows [SemVer 2.0.0](https://semver.org/)
  specification
- **Version Format:** Changed from the legacy scheme (v1.x to v4.x) to `MAJOR.MINOR.PATCH` format
- **Release Documentation:** Structured release notes following industry-standard conventions

#### 🔄 Entity Initialisation Strategy

- **Repository Integration:** Entity initialisation methods now query the database to check for existing
  entities before creating new ones
- **Fallback Handling:** Robust fallback mechanisms when entities are not found in the database
- **Transactional Consistency:** All entity creation and update operations maintain transactional integrity
  through `TransactionService`

#### 🔀 Data Processing Pipelines

- **Multi-Step Processing:** IPSC responses now go through coordinated initialisation steps for clubs,
  matches, stages, and competitors
- **Error Handling:** Enhanced validation and error messages for data transformation failures
- **Null Safety:** Comprehensive null checks throughout data processing pipelines

#### 🧪 Test Infrastructure (Post-Release Enhancement)

- **Test Organisation:** Restructured DTO test classes with clear section headers and logical grouping
- **Naming Standards:** Standardised test naming to `testMethod_whenCondition_thenExpectedBehavior` pattern
- **Test Coverage Expansion:** Added 151+ new unit tests for DTO classes (MatchStageDtoTest: 48, ScoreDtoTest:
  26, MatchStageCompetitorDtoTest: 77)
- **AAA Pattern:** Consistent Arrange-Act-Assert structure implemented across all new tests
- **Edge Case Coverage:** Extensive null/empty/blank field-testing, boundary value testing
- **Documentation:** Comprehensive test documentation and inline comments

### 🐛 Fixed

#### 🔗 Entity Relationship Management

- Fixed edge cases in entity initialisation when creating stages with missing `maxPoints` values
- Resolved mapping issues between DTOs and domain entities during update operations
- Corrected null-safety handling in the recursive establishment of entity relationships

#### 🔄 Data Transformation

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

#### 🛠️ CRUD Operations for IPSC Entities

- **`IpscMatchRepository`** - Create, Read, Update, Delete operations for IPSC match entities
- **`IpscMatchStageRepository`** - CRUD support for match stage entities
- **Service layer CRUD:** Implemented corresponding service methods for all CRUD operations
- **Transactional handling:** Transaction management for all write operations

#### ✅ Enhanced Input Validation

- **DTO Validation:** Additional `@NotNull` annotations on critical DTO fields
- **Bean Validation:** Jakarta Validation annotations integrated throughout request/response DTOs
- **Error Messages:** Detailed validation error reporting

#### 🧪 Testing Improvements

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

#### 🔄 Major IPSC Domain Refactoring

- **Entity Renames:** `Match` → `IpscMatch`, `MatchStage` → `IpscMatchStage`
- **Repository Updates:** New `IpscMatchRepository` and `IpscMatchStageRepository` interfaces
- **Enhanced Type Safety:** Improved domain model clarity through explicit entity naming

#### ✅ Improved Input Validation

- **Multi-layered Validation:** Validation at controller, service, and entity levels
- **Error Mapping:** Comprehensive error response generation with detailed messages

#### ⚠️ Exception Handling Improvements

- **Global Exception Handler:** Centralised exception handling for consistent error responses
- **Custom Exceptions:** Domain-specific exception types for clearer error semantics

#### 🧪 Comprehensive Testing

- **Unit Test Coverage:** Extensive test coverage for service implementations
- **Integration Testing:** Full pipeline testing from controller through persistence layer
- **Bug Fixes:** Tests added to prevent regression of known issues

#### 🔧 XML Parsing Bug Fixes

- Fixed edge cases in XML parsing logic
- Improved handling of malformed XML structures
- Enhanced validation of parsed XML data

### Changed

#### 💥 Breaking Changes

- **Entity Renaming:** Consumers must update references from `Match` to `IpscMatch`
- **Repository Interface Changes:** Update injection points to use `IpscMatchRepository` and
  `IpscMatchStageRepository`
- **Service Method Names:** Some service method signatures updated for consistency

#### 🗄️ Database

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

### 📐 Semantic Versioning (Current)

As of version 5.0.0, this project follows [Semantic Versioning 2.0.0](https://semver.org/):

- **MAJOR** version for incompatible API changes
- **MINOR** version for backward-compatible functionality additions
- **PATCH** version for backward-compatible bug fixes

### 📐 Legacy Versioning (v1.x - v4.x)

Earlier releases used a non-semantic versioning scheme. For historical documentation,
see [ARCHIVE.md](/documentation/archive/ARCHIVE.md).

---

## 🚀 Upgrade Guide

### ⬆️ From v4.1.0 to v5.0.0

**Breaking Changes:** None

1. Update the version in `pom.xml` to `5.0.0`
2. Run `./mvnw clean install` to rebuild the project
3. Restart the application
4. Existing data and configurations remain compatible

### ⬆️ From v4.0.0 to v4.1.0

**Breaking Changes:** None

Migration: See v4.1.0 release notes

### ⬆️ From v3.x to v4.x

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
4. Submit a pull request with detailed description

---

## 💬 Support

For issues, feature requests, or questions:

- **GitHub Issues:**
  [tahoni/hpsc-web-springboot/issues](https://github.com/tahoni/hpsc-web-springboot/issues)
- **Repository:** [tahoni/hpsc-web-springboot](https://github.com/tahoni/hpsc-web-springboot)

---

**Last Updated:** 2026-02-24

