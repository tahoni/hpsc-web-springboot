# HPSC website back-end

## Release Notes

### Version 1.1.1 - _2026-01-16_

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
