# HPSC Website Backend

## Release Notes

### Version 2.0.0 - _2026-02-08_

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
