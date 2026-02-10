# HPSC Website Backend

## Release Notes

### Version 2.1.0 - _2026-02-10_

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
