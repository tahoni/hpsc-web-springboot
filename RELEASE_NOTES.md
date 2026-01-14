# HPSC website back-end

## Release Notes

### Version 1.1.0 - _2026-01-14_

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
- Added `ValueUtil` to provide consistent null-to-default initialization for common types.

#### Licence and Documentation

- Added extensive Javadoc comments across models, services, and utility classes.
- Integrated `springdoc-openapi` with detailed annotations on controllers and models.

#### General Technical Changes

- Added `springdoc-openapi-maven-plugin` to generate OpenAPI documentation during the
  integration-test phase.

#### Changes by

@tahoni
