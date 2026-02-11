# HPSC Website Backend

## Release Notes

### Version 4.0.0 - _2026-02-11_

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

#### Testing Improvements

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

🤖 Generated with [Claude Code](https://claude.com/claude-code)
