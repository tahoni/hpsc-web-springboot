# HPSC Website Backend

## Change Log

### Table of Contents

- [Version 2.0.0](#version-200--_2026-02-08_)
- [Version 1.1.3](#version-113--_2026-01-28_)
- [Version 1.1.2](#version-112--_2026-01-20_)
- [Version 1.1.1](#version-111--_2026-01-16_)
- [Version 1.1.0](#version-110--_2026-01-14_)
- [Version 1.0.0](#version-100--_2026-01-04_)

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
