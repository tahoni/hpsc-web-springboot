# HPSC website

## Change Log

### Table of Contents

- [Version 1.0.0](#version-100---_2026-01-04_)

### [Version 1.0.0](https://github.com/tahoni/hpsc-web-springboot/releases/tag/version-1.0.0) - _2026-01-04_

Introduced robust image gallery functionality in a Spring Boot application, focusing on CSV-based image data
processing, improved error handling, and better maintainability.

#### Enhancements and Updates

- Added image processing with CSV support, including new models (`ImageRequest`, `ImageResponse`, etc.),
  `HpscImageService` for processing logic, and `ImageController` for API endpoints.
- Introduced tag parsing and extended `ImageResponse` with `List<String>` for tags, improving data
  flexibility.
- Enabled MIME type inference and handling for rearranged CSV columns, partial data, and array separators.

#### General Code Improvements

- Migrated models to the `za.co.hpsc.web.models` package and refactored CSV processing for better validation
  and exception handling (e.g., custom exceptions like `ValidationException`, `FatalException`, and
  `CsvReadException`).
- Enhanced exception management in `ApiControllerAdvice` to cover mismatched inputs and non-fatal errors.
- Updated unit tests to validate new logic, including exception scenarios, MIME type inference, and CSV edge
  cases.

#### Licence and Documentation

- Added comprehensive Javadoc comments across classes and methods for better documentation.

#### General Technical Changes

- Adjusted project configurations (e.g., `.gitignore`, Material Theme, code styles, and Maven dependencies).

#### Changes by

@tahoni
