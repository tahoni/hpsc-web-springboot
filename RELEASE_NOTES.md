# HPSC Website Backend

## Release Notes

### Version 2.0.0 - _2026-02-10_

Comprehensive refactoring of the IPSC/WinMSS match result processing system, introducing modular
service architecture, improved data models, and enhanced transactional support for better maintainability
and extensibility.

#### Enhancements and Updates

**Service Layer Refactoring**

- Renamed `IpscService` to `WinMssService` to better reflect its purpose.
- Introduced new modular services:
    - `MatchResultService` - Handles match result initialisation and mapping.
    - `TransactionService` - Manages transactional persistence operations.
    - `ClubService`, `CompetitorService`, `MatchService`, `MatchCompetitorService`, `MatchStageService`,
      `MatchStageCompetitorService` - Dedicated service layers for each domain entity.
- Modularised match processing logic with dedicated methods for finding, initialising, and persisting
  entities.

**Data Model Enhancements**

- Added timestamps (`dateCreated`, `dateUpdated`) to `Match`, `MatchCompetitor`, and `MatchStageCompetitor`.
- Enhanced `MatchStageCompetitor` with additional scoring fields:
    - Deduction support.
    - More granular scoring breakdown.
    - Direct relationship with `Competitor` entity.
- Updated constructors across domain classes with `init()` methods for DTO-based initialisation.
- Renamed `CompetitorConstants` to `MatchLogConstants` for clarity.

**DTO Restructuring**

- Removed legacy response models (`MatchLogResponse`, `MatchResultResponse`, etc.).
- Introduced comprehensive DTO package structure:
    - `ClubDto`, `CompetitorDto`, `MatchDto`, `MatchCompetitorDto`, `MatchStageDto`,
      `MatchStageCompetitorDto`.
    - `MatchResultsDto` as the primary response model.
    - Request/Response models for WinMSS data import.
- Removed XML-specific request classes, consolidated to unified request models with XML wrapper support.

#### Tests and Quality Assurance

- Added comprehensive unit tests for `WinMssServiceImpl` covering XML/JSON parsing scenarios.
- Added extensive unit tests for `MatchResultServiceImpl`:
    - Testing `initClub`, `initMatch`, `initStages` methods.
    - Coverage for existing, non-existing, and null data handling.
- Added tests for `IpscMatchService` functionality.

#### General Code Improvements

- Enhanced null safety with explicit null checks in service layer `find` methods.
- Improved error handling throughout the application.
- Refactored utility methods in `NumberUtil` for consistency.

#### General Technical Changes

- Standardised line wrapping at 110 characters for improved readability.
- Updated `.gitignore` to exclude `tsdocs/` directory and improved VS Code comment.

#### Licence and Documentation

- Expanded Javadoc coverage across services, models, enums, and controllers.

#### Dependencies

- Cleaned up `pom.xml`:
    - Leveraged Spring Boot BOM for Jackson dependency management.
    - Added Apache Commons Lang3 (3.19.0).
    - Removed redundant version specifications.

#### Migration Notes

- **Breaking Changes**: `IpscService` interface and implementation have been replaced with `WinMssService`.
- Legacy response models (`MatchLogResponseHolder`, `MatchResultLogResponseHolder`) have been removed.
- Controllers now return `ControllerResponse` instead of specialised response holders.

#### Test Plan

- [x] All existing unit tests pass.
- [x] New unit tests are added for refactored services.
- [x] WinMSS data import functionality validated.
- [x] Match result initialisation logic tested with various scenarios.
- [x] Build completes successfully without errors.

#### Changes by

@tahoni
