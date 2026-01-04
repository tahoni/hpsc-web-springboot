# HPSC website back-end

## Release Notes

### Version 1.0.0 - _2026-01-04_

Introduce robust image gallery functionality in a Spring Boot application, focusing on CSV-based
image data processing, improved error handling, and better maintainability.

#### Key Changes

- **_Feature Additions_**:
    - Added image processing with CSV support, including new models (`ImageRequest`, `ImageResponse`, etc.),
      `HpscImageService` for processing logic, and `ImageController` for API endpoints.
    - Introduced tag parsing and extended `ImageResponse` with `List<String>` for tags, improving data
      flexibility.
    - Enabled MIME type inference and handling for rearranged CSV columns, partial data, and array separators.

- **_Refactorings and Improvements_**:
    - Migrated models to the `za.co.hpsc.web.models` package and refactored CSV processing for better
      validation and exception handling (e.g., custom exceptions like `ValidationException`, `FatalException`,
      and `CsvReadException`).
    - Enhanced exception management in `ApiControllerAdvice` to cover mismatched inputs and non-fatal errors.
    - Added comprehensive Javadoc comments across classes and methods for better documentation.

- **_Testing and Configuration_**:
    - Updated unit tests to validate new logic, including exception scenarios, MIME type inference, and CSV
      edge cases.
    - Adjusted project configurations (e.g., `.gitignore`, Material Theme, code styles, and Maven
      dependencies).
    - Fixed minor issues like README typos and removed unused code/elements.

#### Impact

- No breaking changes are expected; this primarily adds features and improves stability.
- Version bumped to `1.0.0` in `pom.xml`.

#### Testing

- All new and updated features are covered by unit tests in classes like `HpscImageServiceTest` and
  `ImageResponseTest`.
- Manual verification is recommended for CSV processing endpoints.

#### Changes by

@tahoni
