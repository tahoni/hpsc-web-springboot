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

Comprehensive refactoring of the IPSC/WinMSS match result processing system, introducing modular service
architecture, improved data models, and enhanced transactional support.

#### Enhancements and Updates

- **Service Refactoring**: Renamed and restructured `IpscService` to `WinMssService`, introduced new service
  layers including `MatchResultsService`, `TransactionService`, and modularised service implementations.
- **Data Model Enhancements**:
    - Updated domain models (`Match`, `Competitor`, `MatchCompetitor`, `MatchStageCompetitor`) with new fields
      including timestamps (`dateCreated`, `dateUpdated`), additional scoring details, and deduction support
    - Added relationship between `Competitor` and `MatchStageCompetitor` directly.
    - Introduced `CompetitorConstants` and updated `IpscConstants` with exclusion lists.
- **DTO Restructuring**: Removed legacy response models and introduced modular DTOs for matches, clubs,
  stages, and competitors.

#### General Code Improvements

- **Code Quality**: Standardised line wrapping at 110 characters, enhanced null safety, and improved error
  handling.

#### General Technical Changes

- **Configuration Updates**: Updated `.gitignore`, fixed changelog template, and minor documentation
  improvements.

#### Licence and Documentation

- **Improved Documentation**: Added comprehensive Javadoc comments across services, models, enums, and
  controllers.

#### Dependencies

- **Dependency Management**: Cleaned up `pom.xml` to use Spring Boot BOM-managed versions for Jackson
  dependencies, added Apache Commons Lang3.

#### Tests and Quality Assurance

- Added comprehensive unit tests for `WinMssServiceImpl` and `WinMssService` covering
  XML/JSON parsing scenarios.
- Added tests for match results calculation in `MatchResultServiceImpl`.

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
