# HPSC Website Backend

## Release Notes

### Version 1.1.3 - _2026-01-28_

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
