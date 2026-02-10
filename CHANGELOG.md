# HPSC Website Backend

## Change Log

### Table of Contents

- [Version 3.0.0](#version-300---_2026-02-10_)
- [Version 2.0.0](#version-200--_2026-02-08_)
- [Version 1.1.3](#version-113--_2026-01-28_)
- [Version 1.1.2](#version-112--_2026-01-20_)
- [Version 1.1.1](#version-111--_2026-01-16_)
- [Version 1.1.0](#version-110--_2026-01-14_)
- [Version 1.0.0](#version-100--_2026-01-04_)

### [Version 3.0.0](https://github.com/tahoni/hpsc-web-springboot/releases/tag/version-3.0.0) - _2026-02-10_

Major refactoring of the IPSC match results processing system to improve modularity, maintainability, and
testability. This release introduces significant domain model changes, replaces discipline-based
classification with firearm-type-based divisions, reintroduces the `Club` entity, and enhances documentation
throughout the codebase.

#### Breaking Changes

⚠️ **This is a major version update with breaking changes:**

- **`Discipline` enum removed** in favor of `FirearmType` enum
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

- **`ClubDto` introduced**: New DTO for club data transfer with initialization methods
- **`CompetitorDto` enhanced**: Added `defaultCompetitorCategory` field and improved initialization logic
- **`MatchDto` enhanced**: Added `Club` entity mapping, firearm type handling, and detailed Javadoc
- **`MatchStageDto` enhanced**: Added `maxPoints` field and initialization logic
- **`MatchStageCompetitorDto` enhanced**: Added percentage calculation based on stage max points, removed
  `matchPercentage`
- **`MatchCompetitorDto` enhanced**: Added `CompetitorCategory` field and improved initialization

**_Entity Initialization Methods_**

- Added comprehensive `init()` methods across all domain entities (`Club`, `Competitor`, `Match`,
  `MatchCompetitor`, `MatchStage`, `MatchStageCompetitor`) with detailed Javadoc explaining initialization
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
- **`CompetitorCategoryTest`**: Updated to test new null-safe behavior
- **`MatchHelpersTest`**: Updated to test firearm type inference logic
- **`MatchResultServiceImplTest`**: Updated to test new club entity handling

**_Removed Tests_**

- **`DisciplineTest`**: Removed (202 lines) - discipline enum deprecated
- **`DivisionToDisciplinesInDivisionMapperTest`**: Removed (43 lines) - mapper class deprecated

#### Documentation

**_README.md Updates_**

- Expanded **Features** section with detailed descriptions of match management, competitor tracking, club
  operations, award ceremonies, image gallery, and IPSC integration
- Added comprehensive **Technology** section listing all frameworks, libraries, and tools
- Enhanced **Prerequisites** section with database configuration requirements
- Added detailed **Testing** section with test categories, frameworks, and coverage areas
- Improved **Introduction** with feature highlights

**_ARCHITECTURE.md Updates_**

- Added **Persistence Layer** section documenting repositories and domain entities
- Expanded **System Overview** with feature descriptions
- Enhanced **Technology** section with complete tech stack details
- Added **Domain Support Layers** section documenting enums, helpers, constants, and utils
- Updated **Data Flow** section with detailed request-response and data import flows
- Enhanced **Quality Attributes** section with testability, extensibility, and data integrity
- Updated **Project Structure** with new packages and modules

#### Configuration Changes

- **Version bumps**: Updated `pom.xml`, `HpscWebApplication.java`, and release notes to version 3.0.0
- **Constants**: Added `DEFAULT_MATCH_CATEGORY` to `IpscConstants`

#### Code Quality Improvements

- **Javadoc enhancement**: Added detailed Javadoc across all domain entities, DTOs, enums, and services
- **TODO cleanup**: Removed outdated TODO comments across codebase
- **Null safety**: Enhanced null handling in enum lookup methods
- **Code formatting**: Improved readability and consistency

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

### [Version 1.1.3](https://github.com/tahoni/hpsc-web-springboot/releases/tag/version-1.1.3) -_2026-01-28_

Improved maintainability and clarity by expanding Javadoc coverage across the codebase.
Introduced a central `Division` → `DisciplinesInDivision` mapping.

#### Enhancements and Updates

- Introduced a central mapper that resolves each `Division` enum to its corresponding
  `DisciplinesInDivision` singleton implementation.
- `Division` now includes an additional value: `NONE`.

#### Tests and Quality Assurance

- Added and updated unit tests to validate the mapping behaviour.
- Simplified test setup, fixed grammar issues in test code/docs, and added coverage for validation-oriented
  methods.

#### General Code Improvements

- Ensured utility classes cannot be instantiated (e.g. via private constructors).
- Improved string formatting and readability in match helper utilities.
- Removed unused constants in `MatchConstants`.

#### Licence and Documentation

- Expanded and refined Javadoc across multiple areas including:
    - Domain entities (e.g., match-related entities)
    - Enums (improved descriptions/clarity)
    - Division discipline model classes (`Disciplines*` classes)

#### General Technical Changes

- Removed an unnecessary IDE file: `.idea/data_source_mapping.xml`
- Updated `.gitignore` to prevent committing similar IDE metadata in future.

#### Dependencies

- Bumped spring-boot-starter-parent to version 4.0.2 to address security vulnerabilities.

#### Changes by

@tahoni

### [Version 1.1.2](https://github.com/tahoni/hpsc-web-springboot/releases/tag/version-1.1.2) -_2026-01-20_

Adds or updates project documentation and onboarding material.
Documents development, build, test, and deploy workflows for the project.

#### Licence and Documentation

- Created the `README.md` file describing the project overall.
- Created the `ARCHITECTURE.md` file describing the architecture of the project.

#### Changes by

@tahoni

### [Version 1.1.1](https://github.com/tahoni/hpsc-web-springboot/releases/tag/version-1.1.1) -_2026-01-16_

Improves code maintainability and API clarity by standardising Javadoc documentation across several key
components.

#### Licence and Documentation

- Refined class-level documentation of `AwardController` to better describe its role in CSV processing.
  Improved method documentation for `@PostMapping` endpoints, specifically detailing input requirements and
  return types.
- Enhanced parameter descriptions of `ImageController` for CSV processing, ensuring consistent documentation
  with the `AwardController`.
- Standardised Javadoc comments for `FatalException` and `NonFatalException` to match Java's core exception
  patterns. Removed unnecessary imports.
- Improved Javadoc annotations, validation constraints, and standardized nullability descriptions across the
  model layer to improve IDE assistance and API documentation generation.

#### Changes by

@tahoni

### [Version 1.1.0](https://github.com/tahoni/hpsc-web-springboot/releases/tag/version-1.1.0) -_2026-01-14_

Introduced comprehensive support for award processing, refactored core models for better
consistency, and integrated OpenAPI documentation. Also, significantly improved test coverage and
null-safety across the application.

#### Enhancements and Updates

- Added award processing with CSV support, including new models (`AwardRequest`, `AwardResponse`,
  etc.), `HpscAwardService` for processing logic, and `AwardController` for API endpoints.
- Added `AwardCeremonyResponse` to group awards by ceremony for more structured API responses.

#### Testing and Quality Assurance

- Added comprehensive unit tests for `AwardService`, `AwardResponse`, and `AwardCeremonyResponse`.
- Improved existing `HpscImageServiceTest` with detailed assertions for file paths, tags, and
  descriptions.

#### General Code Improvements

- Introduced generic `Request` and `Response` base classes to standardise metadata handling
  across the project.
- Standardised error handling using a new `ErrorResponse` model in `ApiControllerAdvice`.
- Enhanced field validation across all models using `@NotNull` and `@NotBlank`.
- Added `ValueUtil` to provide consistent null-to-default initialisation for common types.

#### Licence and Documentation

- Added extensive Javadoc comments across models, services, and utility classes.
- Integrated `springdoc-openapi` with detailed annotations on controllers and models.

#### General Technical Changes

- Added `springdoc-openapi-maven-plugin` to generate OpenAPI documentation during the
  integration-test phase.

#### Changes by

@tahoni

### [Version 1.0.0](https://github.com/tahoni/hpsc-web-springboot/releases/tag/version-1.0.0) -_2026-01-04_

Introduced robust image gallery functionality in a Spring Boot application, focusing on CSV-based
image data processing, improved error handling, and better maintainability.

#### Enhancements and Updates

- Added image processing with CSV support, including new models (`ImageRequest`, `ImageResponse`, etc.),
- `HpscImageService` for processing logic, and `ImageController` for API endpoints.
- Introduced tag parsing and extended `ImageResponse` with `List<String>` for tags, improving data
  flexibility.
- Enabled MIME type inference and handling for rearranged CSV columns, partial data, and array
  separators.

#### Tests and Quality Assurance

- Updated unit tests to validate new logic, including exception scenarios, MIME type inference,
  and CSV edge cases.

#### General Code Improvements

- Migrated models to the `za.co.hpsc.web.models` package and refactored CSV processing for better
  validation and exception handling (e.g., custom exceptions like `ValidationException`, `FatalException`,
  and `CsvReadException`).
- Enhanced exception management in `ApiControllerAdvice` to cover mismatched inputs and non-fatal
  errors.

#### Licence and Documentation

- Added comprehensive Javadoc comments across classes and methods for better documentation.

#### General Technical Changes

- Adjusted project configurations (e.g., `.gitignore`, Material Theme, code styles, and Maven
  dependencies).

#### Changes by

@tahoni

🤖 Generated with [Claude Code](https://claude.com/claude-code)
