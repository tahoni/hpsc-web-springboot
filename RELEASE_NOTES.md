# HPSC Website Backend

## Release Notes

### Version 3.0.0 - _2026-02-10_

Major refactoring of the IPSC match results processing system to improve modularity, maintainability, and
testability. This release introduces significant domain model changes, replaces discipline-based
classification with firearm-type-based divisions, reintroduces the `Club` entity, and enhances documentation
throughout the codebase.

#### Breaking Changes

⚠️ **This is a major version update with breaking changes:**

- **`Discipline` enum removed** in favor of `FirearmType` enum
- **`Division` enum restructured** to align with IPSC firearm-type-specific divisions
- **Discipline-to-division mappings removed**: `DisciplinesInDivision`, `DisciplinesHandgun`,
  `DisciplinesPcc`, `DisciplinesRifle`, `DisciplinesShotgun`, `DisciplinesMiniRifle`, `Disciplines22Handgun`,
  and `DivisionToDisciplinesInDivisionMapper` classes removed
- **`Competitor` entity field renamed**: `category` → `defaultCompetitorCategory`
- **`Match` entity changes**: `matchDivision` field replaced with `matchFirearmType`; `club` string field
  replaced with `Club` entity relationship and `ClubReference` enum
- **`MatchStage` entity enhancement**: Added `maxPoints` field for stage scoring
- **`MatchStageCompetitorDto` changes**: Removed `matchPercentage` field, added percentage calculation based
  on stage max points

#### Enhancements and Updates

**_Domain Model Refactoring_**

- **`Club` entity reintroduced**: Added `Club` JPA entity with bidirectional `@OneToMany` relationship to
  `Match`, including `ClubRepository` and `ClubService`/`ClubServiceImpl`
- **`FirearmType` enum introduced**: New enum representing IPSC firearm types (Handgun, PCC, Rifle, Shotgun,
  Mini Rifle, .22 Handgun) with division mappings
- **`Division` enum enhanced**: Expanded to include all IPSC divisions across multiple firearm types with
  firearm-type classification methods
- **`CompetitorCategory` field added**: Added across `Competitor`, `MatchCompetitor`, and related DTOs for
  better competitor classification
- **Firearm-type-to-division mappings**: New mapping classes `DivisionsForFirearmType`, `DivisionsHandgun`,
  `DivisionsPcc`, `DivisionsRifle`, `DivisionsShotgun`, `DivisionsMiniRifle`, `Divisions22Handgun`, and
  `FirearmTypeToDivisions` mapper
- **`ClubReference` enum enhanced**: Added lookup methods and additional club definitions

**_Service Layer Improvements_**

- **`ClubService` and `ClubServiceImpl`**: New service layer for club management operations
- **`TransactionService` enhanced**: Improved error handling and transactional boundaries
- **`MatchResultService` updated**: Refactored to handle firearm-type mappings and `Club` entity relationships
- **`AwardService` updated**: Adjusted to work with new domain model structure

**_DTO Layer Enhancements_**

- **`ClubDto` introduced**: New DTO for club data transfer with initialization methods
- **`CompetitorDto` enhanced**: Added `defaultCompetitorCategory` field and improved initialization logic
- **`MatchDto` enhanced**: Added `Club` entity mapping, firearm type handling, and detailed Javadoc
- **`MatchStageDto` enhanced**: Added `maxPoints` field and initialization logic
- **`MatchStageCompetitorDto` enhanced**: Added percentage calculation based on stage max points, removed
  `matchPercentage`
- **`MatchCompetitorDto` enhanced**: Added `CompetitorCategory` field and improved initialization

**_Entity Initialization Methods_**

- Added comprehensive `init()` methods across all domain entities (`Club`, `Competitor`, `Match`,
  `MatchCompetitor`, `MatchStage`, `MatchStageCompetitor`) with detailed Javadoc explaining initialization
  logic
- Enhanced null safety and graceful handling of invalid or missing data

**_Enum Utility Methods_**

- **`Division` enum**: Added `getFirearmTypeFromDivision()` and `getDivisionFromCode()` methods with
  null/invalid input handling
- **`FirearmType` enum**: Added `getFirearmTypeFromCode()` and division retrieval methods
- **`CompetitorCategory` enum**: Refactored `getCompetitorCategoryFromCode()` to return `NONE` for null,
  blank, or unmatched inputs (instead of throwing exceptions)
- **`ClubReference` enum**: Added `getClubFromCode()` lookup method

**_Helper Classes_**

- **`MatchHelpers` updated**: Added firearm type inference methods and division lookup logic

#### Tests and Quality Assurance

**_New Test Coverage_**

- **`FirearmTypeTest`**: Comprehensive tests for firearm type enum (132+ lines)
- **`FirearmTypeToDivisionsTest`**: Extensive tests for firearm-type-to-division mappings (279+ lines)
- **`ClubDtoTest`**: Tests for club DTO initialization (77+ lines)
- **`CompetitorDtoTest`**: Tests for competitor DTO with new category field (97+ lines)
- **`MatchDtoTest`**: Tests for match DTO with club entity mapping (101+ lines)
- **`MatchStageDtoTest`**: Tests for match stage DTO with max points (59+ lines)
- **`ClubReferenceTest`**: Tests for club reference enum (99+ lines)

**_Updated Test Coverage_**

- **`DivisionTest`**: Expanded to cover new firearm-type-specific divisions (194+ lines, +194 additions)
- **`CompetitorCategoryTest`**: Updated to test new null-safe behavior
- **`MatchHelpersTest`**: Updated to test firearm type inference logic
- **`MatchResultServiceImplTest`**: Updated to test new club entity handling

**_Removed Tests_**

- **`DisciplineTest`**: Removed (202 lines) - discipline enum deprecated
- **`DivisionToDisciplinesInDivisionMapperTest`**: Removed (43 lines) - mapper class deprecated

#### Documentation

**_README.md Updates_**

- Expanded **Features** section with detailed descriptions of match management, competitor tracking, club
  operations, award ceremonies, image gallery, and IPSC integration
- Added comprehensive **Technology** section listing all frameworks, libraries, and tools
- Enhanced **Prerequisites** section with database configuration requirements
- Added detailed **Testing** section with test categories, frameworks, and coverage areas
- Improved **Introduction** with feature highlights

**_ARCHITECTURE.md Updates_**

- Added **Persistence Layer** section documenting repositories and domain entities
- Expanded **System Overview** with feature descriptions
- Enhanced **Technology** section with complete tech stack details
- Added **Domain Support Layers** section documenting enums, helpers, constants, and utils
- Updated **Data Flow** section with detailed request-response and data import flows
- Enhanced **Quality Attributes** section with testability, extensibility, and data integrity
- Updated **Project Structure** with new packages and modules

#### Configuration Changes

- **Version bumps**: Updated `pom.xml`, `HpscWebApplication.java`, and release notes to version 3.0.0
- **Constants**: Added `DEFAULT_MATCH_CATEGORY` to `IpscConstants`

#### Code Quality Improvements

- **Javadoc enhancement**: Added detailed Javadoc across all domain entities, DTOs, enums, and services
- **TODO cleanup**: Removed outdated TODO comments across codebase
- **Null safety**: Enhanced null handling in enum lookup methods
- **Code formatting**: Improved readability and consistency

#### Migration Notes

For developers upgrading from version 2.0.0:

1. **Replace `Discipline` references with `FirearmType`**: Update all code referencing the `Discipline` enum
   to use `FirearmType` instead
2. **Update `Division` handling**: Review division-related logic as divisions are now firearm-type-specific
3. **Update `Competitor` field access**: Replace `category` with `defaultCompetitorCategory`
4. **Update `Match` entity access**: Replace `matchDivision` with `matchFirearmType`; update club references
   to use `Club` entity
5. **Remove discipline-division mapper usage**: Replace `DivisionToDisciplinesInDivisionMapper` with
   `FirearmTypeToDivisions`
6. **Update test assertions**: Review tests using `CompetitorCategory.getCompetitorCategoryFromCode()` as it
   now returns `NONE` for invalid inputs instead of throwing exceptions
7. **Database migration required**: New `Club` table and updated foreign key relationships require schema
   updates

#### Changes by

@tahoni

🤖 Generated with [Claude Code](https://claude.com/claude-code)
