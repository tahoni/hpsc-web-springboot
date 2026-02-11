# HPSC Website Backend

## Change Log

### Table of Contents

- [Version 4.0.0](#version-400---_2026-02-11_)
- [Version 3.1.0](#version-310---_2026-02-10_)
- [Version 3.0.0](#version-300---_2026-02-10_)
- [Version 2.0.0](#version-200--_2026-02-08_)

### [Version 4.0.0](https://github.com/tahoni/hpsc-web-springboot/releases/tag/version-4.0.0) - _2026-02-11_

This release includes significant refactoring of the IPSC match system, enhanced input validation, improved
exception handling, comprehensive test coverage, and bug fixes for XML parsing.

#### Breaking Changes

⚠️ **This is a major version update with breaking changes:**

- **Renamed domain entities**: `Match` → `IpscMatch`, `MatchStage` → `IpscMatchStage`
- **Renamed repository**: `MatchRepository` → `IpscMatchRepository`
- **Removed**: `MatchStageRepository` (replaced with `IpscMatchStageRepository`)

#### Enhancements and Updates

**_IPSC System Refactoring_**

- **Renamed domain entities**: Refactored `Match` to `IpscMatch` and `MatchStage` to `IpscMatchStage` for
  improved naming clarity and consistency
- **Repository consolidation**: Renamed `MatchRepository` to `IpscMatchRepository` and replaced
  `MatchStageRepository` with `IpscMatchStageRepository`
- **Updated dependencies**: Propagated entity name changes across all dependent services, controllers,
  helpers, and repositories
- **Domain model updates**: Updated entity relationships in `MatchCompetitor` and `MatchStageCompetitor` to
  reference renamed entities

**_Input Validation and Robustness_**

- **Enhanced null safety**: Added `@NotNull` annotations to critical service methods in `WinMssService`,
  `MatchService`, and `MatchStageService`
- **Improved DTO validation**: Enhanced null checks and input validation in DTO processing throughout
  `MatchDto`, `MatchStageDto`, and related models
- **Request validation**: Added validation annotations to `IpscRequest` and `XmlDataWrapper` for improved data
  integrity

**_Match Processing Improvements_**

- **Refactored match result processing**: Refined match and score processing logic in `MatchResultServiceImpl`
  for better accuracy and maintainability
- **Introduced DTO holder**: Added `MatchResultsDtoHolder` class to improve DTO management and encapsulation
- **Enhanced CAB file import**: Improved `WinMssServiceImpl` modularity by refactoring CAB file import logic
  into smaller, more focused methods
- **Transaction handling**: Enhanced transaction processing and error recovery in `TransactionServiceImpl`

**_API Documentation Enhancements_**

- **Updated endpoint documentation**: Improved API documentation for image endpoints in `ImageController`
- **Enhanced award endpoints**: Updated API documentation for award-related operations in `AwardController`
- **Refined IPSC controller**: Enhanced documentation and endpoint definitions in `IpscController`

**_Response Model Refinements_**

- **Removed default values**: Cleaned up response model fields by removing default values in `ClubResponse`,
  `EnrolledResponse`, `MemberResponse`, `ScoreResponse`, `StageResponse`, `MatchResponse`, `TagResponse`, and
  `IpscResponse` for cleaner JSON serialization
- **Consistent null handling**: Standardized nullable field handling across all response models

**_Exception Handling Improvements_**

- **Enhanced error responses**: Improved exception handling in `ControllerAdvice` for more consistent error
  responses
- **Better error propagation**: Enhanced exception propagation from service layer to controller layer

#### Bug Fixes

**_XML Parsing Error Handling_**

- **Fixed XML parsing bugs**: Resolved critical issues in XML parsing logic within `WinMssServiceImpl`
- **Improved error recovery**: Enhanced error handling and recovery mechanisms during XML data processing
- **Consistent exception handling**: Aligned XML and JSON parsing error handling for uniform behaviour

#### Tests and Quality Assurance

**_Comprehensive Test Coverage_**

- **Enhanced WinMssService testing**: Significantly expanded `WinMssServiceTest` with comprehensive test cases
  covering CAB file import, XML parsing, and error scenarios
- **New test suite**: Added complete test coverage with `IpscMatchServiceImplTest` (985 lines of new test
  code)
- **Improved existing tests**: Enhanced test coverage in `MatchResultServiceImplTest`,
  `WinMssServiceImplTest`, and `MatchHelpersTest`
- **Updated test fixtures**: Updated `HpscWebApplicationTests` and helper tests to reflect entity renames

#### Code Quality Improvements

- **Improved modularity**: Refactored service implementations for better separation of concerns and
  testability
- **Enhanced maintainability**: Simplified complex methods and improved code readability across multiple
  services
- **Better encapsulation**: Introduced helper classes and DTOs to improve data handling and reduce coupling

#### Migration Notes

This release includes breaking changes due to entity renaming. Migration steps required:

- **Database schema updates**: If using JPA auto-DDL, table names will change from `match` to `ipsc_match` and
  `match_stage` to `ipsc_match_stage`. Manual migration scripts may be required for production databases.
- **API contracts**: Endpoint behaviour remains unchanged, but internal processing has been refactored
- **Custom queries**: Any custom queries or native SQL referencing `Match` or `MatchStage` entities must be
  updated to use `IpscMatch` and `IpscMatchStage`
- **Repository references**: Update any direct repository references from `MatchRepository` to
  `IpscMatchRepository`

#### Changes by

@tahoni

### [Version 3.1.0](https://github.com/tahoni/hpsc-web-springboot/releases/tag/version-3.1.0) - _2026-02-10_

A maintenance release focused on improving exception handling consistency, enhancing API documentation, and
fixing critical bugs in XML parsing. This release simplifies the global exception handling architecture while
maintaining backward compatibility.

#### Enhancements and Updates

**_Exception Handling Improvements_**

- **Consolidated exception handlers**: Merged `Exception` and `RuntimeException` handlers in
  `ControllerAdvice` into a single unified method for consistent error responses
- **Simplified validation error handling**: Combined `IllegalArgumentException` and `MismatchedInputException`
  handlers to reduce code duplication
- **Removed redundant handlers**: Eliminated `CsvReadException` handler as it is now covered by the generic
  exception handling mechanism
- **Code reduction**: Streamlined `ControllerAdvice` from approximately 100 lines to 70 lines while
  maintaining full functionality

**_API Documentation Enhancements_**

- **Enhanced OpenAPI annotations**: Added `@Operation` annotation to `importWinMssCabData` endpoint in
  `IpscController` with clear summary and description
- **Corrected request body schema**: Fixed `@RequestBody` schema reference from `ControllerResponse` to
  `IpscRequest` for accurate API documentation
- **Improved exception declaration**: Added explicit `throws ValidationException, FatalException` declarations
  for better exception propagation and API contract clarity
- **Simplified exception handling**: Removed unnecessary try-catch block in `IpscController`, allowing
  exceptions to propagate naturally to global exception handler

#### Bug Fixes

**_XML Parsing Error Handling_**

- **Fixed missing return statement**: Resolved critical bug in `WinMssServiceImpl` where XML parsing errors
  could result in null return values
- **Enhanced exception propagation**: Added proper `ValidationException` re-throwing to preserve exception
  context and error details
- **Improved error consistency**: Aligned XML parsing error handling with JSON parsing patterns for consistent
  behaviour across data formats

#### Code Quality Improvements

- **Exception handling architecture**: Simplified and standardised exception handling patterns across the
  application
- **Error propagation**: Enhanced exception flow from service layer through controller layer to global
  exception handler
- **Documentation accuracy**: Improved alignment between code behaviour and API documentation

#### Migration Notes

This release is fully backward-compatible with version 3.0.0. No migration steps are required.

- **API contracts unchanged**: All endpoint signatures and response formats remain consistent
- **Exception responses unchanged**: Error response structure and HTTP status codes remain the same
- **No database changes**: No schema updates or data migrations required

#### Changes by

@tahoni

🤖 Generated with [Claude Code](https://claude.com/claude-code)

### [Version 3.0.0](https://github.com/tahoni/hpsc-web-springboot/releases/tag/version-3.0.0) - _2026-02-10_

Major refactoring of the IPSC match results processing system to improve modularity, maintainability, and
testability. This release introduces significant domain model changes, replaces discipline-based
classification with firearm-type-based divisions, reintroduces the `Club` entity, and enhances documentation
throughout the codebase.

#### Breaking Changes

⚠️ **This is a major version update with breaking changes:**

- **`Discipline` enum removed** in favour of `FirearmType` enum
- **`Division` enum restructured** to align with IPSC firearm-type-specific divisions
- **Discipline-to-division mappings removed**: `DisciplinesInDivision`, `DisciplinesHandgun`,
  `DisciplinesPcc`, `DisciplinesRifle`, `DisciplinesShotgun`, `DisciplinesMiniRifle`, `Disciplines22Handgun`,
  and `DivisionToDisciplinesInDivisionMapper` classes removed
- **`Competitor` entity field renamed**: `category` → `defaultCompetitorCategory`
- **`Match` entity changes**: `matchDivision` field replaced with `matchFirearmType`; `club` string field
  replaced with `Club` entity relationship and `ClubReference` enum
- **`MatchStage` entity enhancement**: Added `maxPoints` field for stage scoring
- **`MatchStageCompetitorDto` changes**: Removed `matchPercentage` field, added percentage calculation based
  on stage max points

#### Enhancements and Updates

**_Domain Model Refactoring_**

- **`Club` entity reintroduced**: Added `Club` JPA entity with bidirectional `@OneToMany` relationship to
  `Match`, including `ClubRepository` and `ClubService`/`ClubServiceImpl`
- **`FirearmType` enum introduced**: New enum representing IPSC firearm types (Handgun, PCC, Rifle, Shotgun,
  Mini Rifle, .22 Handgun) with division mappings
- **`Division` enum enhanced**: Expanded to include all IPSC divisions across multiple firearm types with
  firearm-type classification methods
- **`CompetitorCategory` field added**: Added across `Competitor`, `MatchCompetitor`, and related DTOs for
  better competitor classification
- **Firearm-type-to-division mappings**: New mapping classes `DivisionsForFirearmType`, `DivisionsHandgun`,
  `DivisionsPcc`, `DivisionsRifle`, `DivisionsShotgun`, `DivisionsMiniRifle`, `Divisions22Handgun`, and
  `FirearmTypeToDivisions` mapper
- **`ClubReference` enum enhanced**: Added lookup methods and additional club definitions

**_Service Layer Improvements_**

- **`ClubService` and `ClubServiceImpl`**: New service layer for club management operations
- **`TransactionService` enhanced**: Improved error handling and transactional boundaries
- **`MatchResultService` updated**: Refactored to handle firearm-type mappings and `Club` entity relationships
- **`AwardService` updated**: Adjusted to work with new domain model structure

**_DTO Layer Enhancements_**

- **`ClubDto` introduced**: New DTO for club data transfer with initialisation methods
- **`CompetitorDto` enhanced**: Added `defaultCompetitorCategory` field and improved initialisation logic
- **`MatchDto` enhanced**: Added `Club` entity mapping, firearm type handling, and detailed Javadoc
- **`MatchStageDto` enhanced**: Added `maxPoints` field and initialisation logic
- **`MatchStageCompetitorDto` enhanced**: Added percentage calculation based on stage max points, removed
  `matchPercentage`
- **`MatchCompetitorDto` enhanced**: Added `CompetitorCategory` field and improved initialisation

**_Entity Initialisation Methods_**

- Added comprehensive `init()` methods across all domain entities (`Club`, `Competitor`, `Match`,
  `MatchCompetitor`, `MatchStage`, `MatchStageCompetitor`) with detailed Javadoc explaining initialisation
  logic
- Enhanced null safety and graceful handling of invalid or missing data

**_Enum Utility Methods_**

- **`Division` enum**: Added `getFirearmTypeFromDivision()` and `getDivisionFromCode()` methods with
  null/invalid input handling
- **`FirearmType` enum**: Added `getFirearmTypeFromCode()` and division retrieval methods
- **`CompetitorCategory` enum**: Refactored `getCompetitorCategoryFromCode()` to return `NONE` for null,
  blank, or unmatched inputs (instead of throwing exceptions)
- **`ClubReference` enum**: Added `getClubFromCode()` lookup method

**_Helper Classes_**

- **`MatchHelpers` updated**: Added firearm type inference methods and division lookup logic

#### Tests and Quality Assurance

**_New Test Coverage_**

- **`FirearmTypeTest`**: Comprehensive tests for firearm type enum (132+ lines)
- **`FirearmTypeToDivisionsTest`**: Extensive tests for firearm-type-to-division mappings (279+ lines)
- **`ClubDtoTest`**: Tests for club DTO initialization (77+ lines)
- **`CompetitorDtoTest`**: Tests for competitor DTO with new category field (97+ lines)
- **`MatchDtoTest`**: Tests for match DTO with club entity mapping (101+ lines)
- **`MatchStageDtoTest`**: Tests for match stage DTO with max points (59+ lines)
- **`ClubReferenceTest`**: Tests for club reference enum (99+ lines)

**_Updated Test Coverage_**

- **`DivisionTest`**: Expanded to cover new firearm-type-specific divisions (194+ lines, +194 additions)
- **`CompetitorCategoryTest`**: Updated to test new null-safe behaviour
- **`MatchHelpersTest`**: Updated to test firearm type inference logic
- **`MatchResultServiceImplTest`**: Updated to test new club entity handling

**_Removed Tests_**

- **`DisciplineTest`**: Removed (202 lines) – discipline enum deprecated
- **`DivisionToDisciplinesInDivisionMapperTest`**: Removed (43 lines) – mapper class deprecated

#### Code Quality Improvements

- **Javadoc enhancement**: Added detailed Javadoc across all domain entities, DTOs, enums, and services
- **TODO cleanup**: Removed outdated TODO comments across codebase
- **Null safety**: Enhanced null handling in enum lookup methods
- **Code formatting**: Improved readability and consistency

#### Configuration Changes

- **Constants**: Added `DEFAULT_MATCH_CATEGORY` to `IpscConstants`

#### Documentation

**_README.md Updates_**

- Expanded **Features** section with detailed descriptions of match management, competitor tracking, club
  operations, award ceremonies, image gallery, and IPSC integration
- Added a comprehensive **Technology** section listing all frameworks, libraries, and tools
- Enhanced **Prerequisites** section with database configuration requirements
- Added a detailed **Testing** section with test categories, frameworks, and coverage areas
- Improved **Introduction** with feature highlights

**_ARCHITECTURE.md Updates_**

- Added **Persistence Layer** section documenting repositories and domain entities
- Expanded **System Overview** with feature descriptions
- Enhanced **Technology** section with complete tech stack details
- Added **Domain Support Layers** section documenting enums, helpers, constants, and utils
- Updated **Data Flow** section with detailed request-response and data import flows
- Enhanced **Quality Attributes** section with testability, extensibility, and data integrity
- Updated **Project Structure** with new packages and modules

#### Migration Notes

For developers upgrading from version 2.0.0:

1. **Replace `Discipline` references with `FirearmType`**: Update all code referencing the `Discipline` enum
   to use `FirearmType` instead
2. **Update `Division` handling**: Review division-related logic as divisions are now firearm-type-specific
3. **Update `Competitor` field access**: Replace `category` with `defaultCompetitorCategory`
4. **Update `Match` entity access**: Replace `matchDivision` with `matchFirearmType`; update club references
   to use `Club` entity
5. **Remove discipline-division mapper usage**: Replace `DivisionToDisciplinesInDivisionMapper` with
   `FirearmTypeToDivisions`
6. **Update test assertions**: Review tests using `CompetitorCategory.getCompetitorCategoryFromCode()` as it
   now returns `NONE` for invalid inputs instead of throwing exceptions
7. **Database migration required**: New `Club` table and updated foreign key relationships require schema
   updates

#### Changes by

@tahoni

🤖 Generated with [Claude Code](https://claude.com/claude-code)

### [Version 2.0.0](https://github.com/tahoni/hpsc-web-springboot/releases/tag/version-2.0.0) -_2026-02-08_

Major refactoring of the IPSC match results processing system to improve modularity, maintainability, and
testability. Introduces a service-oriented architecture with dedicated DTOs, removes legacy code, and
enhances documentation throughout the codebase.

#### Enhancements and Updates

**_Architecture & Services_**

- _New Service Layer_: Introduced `WinMssService` (replacing `IpscService`), `MatchResultService`,
  `TransactionService`, `IpscMatchService`, and specialised services for `Competitor`, `MatchCompetitor`,
  `MatchStage`, and `MatchStageCompetitor`.
- _Modular Processing_: Broke down monolithic match processing logic into discrete, testable methods for
  initialisation, persistence, and mapping.
- _Transaction Management_: Added transactional support with dedicated `TransactionService` for safe match
  result persistence.

**_Domain Model Improvements_**

- _Entity Removal_: Removed `Club` entity and `ClubRepository`, replacing with `ClubReference` enum for
  simpler club management.
- _Enhanced Models_: Added new fields for timestamps, scoring, ranking, and competitor categories across
  domain models.
- _DTO Layer_: Introduced comprehensive DTOs (`MatchDto`, `MatchResultsDto`, `CompetitorDto`,
  `MatchStageDto`, `MatchStageCompetitorDto`, `MatchCompetitorDto`) for better separation of concerns.
- _UUID Mapping_: Implemented UUID-based mapping between requests and domain objects.

**_Request/Response Refactoring_**

- _Unified Models_: Consolidated XML and JSON request models by removing `-ForXml` variants and introducing
  `XmlDataWrapper` for generic XML parsing.
- _Modular Responses_: Replaced monolithic response objects with specialised responses for matches, clubs,
  stages, and competitors.
- _Enhanced Mapping_: Added constructors for request-to-response mappings with improved field coverage.

#### Tests and Quality Assurance

- _Comprehensive Test Coverage_: Added tests for `WinMssServiceImpl`, `MatchResultServiceImpl`,
  and `IpscMatchService`.
- _Test Scenarios_: Cover XML/JSON parsing, null handling, initialisation logic, transactional behaviour,
  and edge cases.

#### General Code Improvements

- _Documentation_: Added comprehensive Javadoc comments across services, models, and DTOs.
- _Code Style_: Enforced 110-character line wrapping for improved readability.
- _Null Safety_: Introduced null checks in service layer `find` methods and domain class `init` methods.
- _Constants_: Renamed `CompetitorConstants` to `MatchLogConstants` and added `IpscConstants`.
- _Utility Cleanup_: Removed `DateUtil` class and inlined its functionality; enhanced `NumberUtil`,
  `ValueUtil`, and `StringUtil`.

#### Configuration Changes

- _Application Config_: Added case-insensitivity for JSON property names.

#### Documentation

- _Documentation Updates_: Updated CHANGELOG.md, RELEASE_NOTES.md, and documentation templates.

#### Dependencies

- _Dependency Updates_: Updated `pom.xml` with required dependencies for enhanced XML/JSON processing.

#### Migration Notes

- `Club` entity replaced with `ClubReference` enum - update any code referencing club entities.
- `IpscService` renamed to `WinMssService` - update service references.
- Legacy response models (`MatchLogResponse`, `MatchResultResponse`) removed in favour of modular DTOs.
- `DateUtil` removed - date handling now inline or in existing utilities.

#### Changes by

@tahoni
