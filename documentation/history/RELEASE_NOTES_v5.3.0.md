# Release Notes - Version 5.3.0

**Release Date:** March 15, 2026  
**Status:** ✨ Stable

---

## 🎯 Theme

**Service Consolidation, Custom JPA Converters & Repository Optimisation**

Version 5.3.0 delivers a focused set of architectural improvements centred on service consolidation,
type-safe JPA attribute converters, and repository query optimisation. This release removes the
`IpscMatchResultService` and `ScoreDto` classes by consolidating their responsibilities into the
`DomainService` and `IpscMatchService` layers; introduces six new JPA attribute converters to replace
`@Enumerated(EnumType.STRING)` across all match-related entities; and delivers significant repository
query enhancements for improved data fetching. The changes also transition `DtoMapping` from a class to
a Java record construct, improving immutability and clarity throughout the mapping layer.

---

## ⭐ Key Highlights

### 🔌 Custom JPA Attribute Converters

- **Six new type-safe attribute converters** replacing `@Enumerated(EnumType.STRING)` annotations
    - `ClubIdentifierConverter`: Type-safe conversion for club identifier enum
    - `CompetitorCategoryConverter`: Handles competitor category persistence
    - `DivisionConverter`: Converts division enum values to/from the database
    - `FirearmTypeConverter`: Handles firearm type persistence
    - `MatchCategoryConverter`: Converts match category enum values
    - `PowerFactorConverter`: Handles power factor persistence

### 🏗️ Service Consolidation

- **IpscMatchResultService Removed:** Complete removal of `IpscMatchResultService` interface and
  `IpscMatchResultServiceImpl` implementation (379 lines)
    - Match result initialisation logic consolidated into `DomainService`
    - Score and competitor processing moved to `IpscMatchService`
    - Cleaner service boundaries with reduced inter-service dependencies
- **ScoreDto Removed:** `ScoreDto` class removed as part of service consolidation
    - Score data now handled directly through response objects
    - Reduced DTO proliferation and simplified data flow
- **ClubEntityService Simplified:** Streamlined to a single `findClubByNameOrAbbreviation` method
    - Removed `findClubById`, `findClubByName`, and `findClubByAbbreviation` methods
    - Focused interface with single responsibility

### 📦 DtoMapping as Java Record

- **DtoMapping converted to Java record:** Transitioned from a mutable class to an immutable record
    - Simplified initialisation with compact record constructor
    - Improved immutability and clarity of DTO mapping state
    - Streamlined transaction stubbing and test setup
    - Better alignment with modern Java design patterns

### 🗄️ JPA Entity Relationship Enhancements

- **Added `mappedBy` to `@OneToMany` relationships** across the entity hierarchy
    - `IpscMatch`: Updated `@OneToMany` annotations with `mappedBy`
    - `IpscMatchStage`: Enhanced entity mapping with `mappedBy` for stage competitors
    - `MatchCompetitor`: Improved bidirectional relationship management
    - `MatchStageCompetitor`: Enhanced mapping with proper ownership side declaration
- **Updated cascade types** for improved entity lifecycle management
    - Fixed cascade type configurations for entity relationships
    - Improved transaction handling for complex match operations

### 🔍 Repository Query Optimisation

- **Enhanced JPQL queries** for improved data fetching strategies
    - Added scheduled date to match retrieval for uniqueness constraints
    - Optimised competitor retrieval using `Set` for deduplication and performance
    - Removed unnecessary fetch joins to reduce query complexity
    - Streamlined club and competitor lookup methods
- **Match stage competitor enhancements:** Improved null handling in retrieval methods
- **Comprehensive query cleanup:** Removed unused query methods across repositories

### 🔧 Service Layer Refinement

- **DomainServiceImpl Enhancements:** 270 lines changed
    - Enhanced `initMatchEntities` method with detailed Javadoc
    - Improved null handling throughout match entity processing
    - Streamlined match result handling with better flow control
    - Removed unused properties and cleaned up service implementation
- **IpscMatchServiceImpl Major Refactoring:** 546 lines changed
    - Improved match results processing with consolidated logic
    - Removed commented-out code for cleaner implementation
    - Enhanced integration with updated DomainService
- **TransactionServiceImpl Improvements:** 22 lines changed
    - Enhanced null handling in transaction processing
    - Improved list initialisation for match operations

### 🧪 Comprehensive Test Suite Updates

#### Expanded Test Suites

- **DomainServiceTest:** 787 lines added with enhanced coverage
    - Comprehensive `initMatchEntities` test cases
    - Simplified assertions with consolidated imports
    - Additional test cases for improved method coverage
    - Detailed Javadoc documentation in test methods

#### Major Consolidations

- **IpscMatchServiceTest:** 3,156 lines changed – comprehensive consolidation
    - Disabled specific test cases pending architecture review
    - Enhanced test helper methods for reusable initialisation logic
    - Streamlined parameter handling and object creation
    - Improved test organisation and readability
- **TransactionServiceTest:** 1,031 lines changed
    - Updated assertions to use `getFirst()` for improved clarity
    - Enabled previously disabled tests with updated verification counts
    - Streamlined transaction stubbing for better test isolation
- **IpscServiceIntegrationTest:** 113 lines changed
    - Added comprehensive integration tests for `importWinMssCabFile`
    - Enabled previously disabled integration tests
    - Cleaned up bean definitions for improved test configuration

#### Removed Test Suites

- **IpscMatchResultServiceTest:** 1,802 lines removed – service deleted, tests no longer required
- **ScoreDtoTest:** 643 lines removed – ScoreDto deleted, tests no longer required

#### Test Quality Improvements

- **Helper method extraction:** Reusable initialisation logic extracted to helper methods
- **Object creation simplification:** Streamlined test object creation with reduced boilerplate
- **Unused code removal:** Cleaned up unused methods and assertions across test suites
- **DomainService mock consistency:** Added and removed DomainService mocks where appropriate

### 📊 Data Flow Improvements

- **Score Processing Refactoring:**
    - Removed `ScoreDto` from data flow; score responses processed directly
    - Improved target/scoring handling in DTO mapping
    - Enhanced null handling for score-related fields
- **Competitor Data Processing:**
    - Optimised competitor lookup using `Set` for deduplication
    - Improved competitor number and ICS alias value handling
    - Enhanced competitor DTO initialisation flow

### 🔄 Code Quality & Maintainability

- **Javadoc Enhancements:**
    - Added detailed Javadoc for `IpscMatchStage.init()` method
    - Updated Javadoc for `findMatchByNameAndScheduledDate` for parameter clarity
    - Cleaned up Javadoc in entity services removing unused methods
- **Code Cleanup:**
    - Removed commented-out code from `IpscMatchServiceImpl`
    - Cleaned up properties files removing unused configurations
    - Updated TODO comments to specify required additions
- **Configuration:**
    - Updated datasource configuration for improved connectivity
    - Updated `logback-spring.xml` for logging improvements
    - Cleaned up `application.properties` and `application-test.properties`

### 🛠️ Development & Build

- **Spring Boot Upgrade:**
    - Updated from Spring Boot 4.0.3 to 4.1.0-SNAPSHOT
    - Added Spring Snapshots repository configuration to `pom.xml`
- **Constants Updates:**
    - Updated IPSC constants values for competitor number and ICS alias
- **Build Success:**
    - All tests compile successfully
    - Zero test failures
    - Clean build with no warnings

---

## 📦 What's New

### Added Features

#### Custom JPA Converters

- `ClubIdentifierConverter` class: Type-safe conversion for `ClubIdentifier` enum
- `CompetitorCategoryConverter` class: Handles `CompetitorCategory` persistence
- `DivisionConverter` class: Converts `Division` enum values
- `FirearmTypeConverter` class: Handles `FirearmType` persistence
- `MatchCategoryConverter` class: Converts `MatchCategory` enum values
- `PowerFactorConverter` class: Handles `PowerFactor` persistence

#### Services

- Enhanced `DomainService` interface with match result initialisation methods
- Enhanced `IpscMatchService` interface with consolidated match processing

#### Test Coverage

- 787 lines of new DomainService tests
- Comprehensive integration tests for `IpscService.importWinMssCabFile`

### Changed

#### Core Services

- **DomainServiceImpl:** 270 lines changed – enhanced `initMatchEntities` and null handling
- **IpscMatchServiceImpl:** 546 lines changed – consolidated match results processing
- **TransactionServiceImpl:** 22 lines changed – improved null handling
- **IpscServiceImpl:** 11 lines changed – minor updates
- **MatchEntityServiceImpl:** 24 lines changed – streamlined implementation
- **ClubEntityServiceImpl:** 24 lines changed – simplified to single method
- **ClubEntityService:** 27 lines changed – removed unused methods

#### Domain Mapping

- **DtoToEntityMapping:** 79 lines changed – enhanced with additional test cases and documentation
- **DtoMapping:** Converted to Java record construct (streamlined initialisation)

#### Entity Models

- **IpscMatch:** 14 lines changed – `mappedBy` and cascade type updates
- **IpscMatchStage:** 26 lines changed – `mappedBy`, Javadoc, and entity mapping
- **MatchCompetitor:** 20 lines changed – improved bidirectional relationship
- **MatchStageCompetitor:** 24 lines changed – enhanced mapping
- **Competitor:** 11 lines changed – minor relationship updates
- **Club:** 2 lines changed – minor updates

#### DTOs

- **MatchStageDto:** 95 lines changed – enhanced target/scoring handling
- **MatchStageCompetitorDto:** 82 lines changed – improved initialisation
- **MatchCompetitorDto:** 62 lines changed – streamlined constructor and init logic
- **CompetitorDto:** 27 lines changed – optimised initialisation
- **MatchDto:** 10 lines changed – minor updates
- **ClubDto:** 6 lines changed – minor updates
- **MatchResultsDto:** 1 line changed – minor cleanup

#### Repository Layer

- **IpscMatchRepository:** 10 lines changed – optimised queries with scheduled date
- Competitor retrieval methods updated to use `Set` for performance
- Match stage competitor retrieval enhanced with null handling

#### Test Suites

- **DomainServiceTest:** 787 lines added – enhanced coverage
- **IpscMatchServiceTest:** 3,156 lines changed – major consolidation
- **TransactionServiceTest:** 1,031 lines changed – enhanced and enabled tests
- **IpscServiceIntegrationTest:** 113 lines changed – integration tests added
- **DtoToEntityMappingTest:** 171 lines changed – additional test cases
- **MatchStageCompetitorDtoTest:** 243 lines changed – updated for DTO changes
- **MatchStageDtoTest:** 50 lines changed – updated assertions
- **CompetitorDtoTest:** 73 lines changed – updated for DTO refactoring
- **AwardCeremonyResponseTest:** 20 lines changed – minor updates
- **StringUtilTest:** 71 lines changed – updated utility tests

### Fixed

#### Entity Relationships

- Correct `mappedBy` declarations for bidirectional `@OneToMany` relationships
- Fixed cascade type configurations for entity lifecycle management
- Improved null handling in entity relationship resolution

#### Repository Queries

- Removed unnecessary fetch joins reducing query complexity
- Fixed match retrieval to properly include scheduled date constraint
- Improved club and competitor lookup accuracy

#### Code Quality

- Removed duplicate code patterns in test setups
- Fixed test assertions for improved clarity (use of `getFirst()`)
- Corrected typo in `RELEASE_NOTES_HISTORY.md` (competitor association section)

### Removed

#### Services & Classes

- `IpscMatchResultService` interface – consolidated into `DomainService`/`IpscMatchService`
- `IpscMatchResultServiceImpl` class – 379 lines removed
- `ScoreDto` class – 50 lines removed; replaced by direct response handling

#### Entity Service Methods

- `ClubEntityService.findClubById()` – removed unused method
- `ClubEntityService.findClubByName()` – removed unused method
- `ClubEntityService.findClubByAbbreviation()` – removed unused method
- Various unused helper methods removed from entity service implementations

#### Test Classes

- `IpscMatchResultServiceTest` – 1,802 lines removed (service deleted)
- `ScoreDtoTest` – 643 lines removed (class deleted)

---

## 🔄 Migration Guide

### For Developers

#### Service Layer Changes

1. **IpscMatchResultService Removal:**
    - `IpscMatchResultService` is no longer available; use `DomainService.initMatchEntities()` instead
    - `IpscMatchResultServiceImpl` removed; match result processing consolidated into `DomainService`
    - Update all injection points and usages accordingly

2. **ScoreDto Removal:**
    - `ScoreDto` is no longer available; use `ScoreResponse` directly where needed
    - Update any code that constructs or processes `ScoreDto` objects

3. **ClubEntityService Simplified:**
    - `findClubById()`, `findClubByName()`, and `findClubByAbbreviation()` removed
    - Use `findClubByNameOrAbbreviation()` as the primary lookup method

4. **DtoMapping as Record:**
    - `DtoMapping` is now a Java record; use record-style construction
    - Field access via record accessor methods

#### JPA Entity Changes

1. **`@OneToMany` with `mappedBy`:**
    - All bidirectional `@OneToMany` relationships now include `mappedBy`
    - Ensure the owning side (`@ManyToOne`) annotations are correctly set on child entities
    - Database schema unchanged; no migration script required

2. **Custom Converters:**
    - `@Enumerated(EnumType.STRING)` replaced by custom `AttributeConverter` implementations
    - Database column values remain the same (string representations)
    - No data migration required

#### Test Updates

1. **Removed test classes:**
    - Remove any references to `IpscMatchResultServiceTest`
    - Remove any references to `ScoreDtoTest`
    - Update test configurations that used `IpscMatchResultService` mocks

2. **Updated test helpers:**
    - Use new helper methods in `IpscMatchServiceTest` for reusable initialisation logic
    - Update mock verification counts where `TransactionServiceTest` was modified

### For API Consumers

No breaking changes to public APIs. All changes are internal refactoring with backward-compatible interfaces.

---

## 📊 Statistics

- **Total Commits:** ~45
- **Files Changed:** 59
- **Insertions:** 5,686 lines
- **Deletions:** 4,613 lines
- **Net Change:** +1,073 lines
- **Test Files Added/Modified:** 14
- **Service Files Modified:** 9
- **Entity Files Modified:** 6
- **New Converter Files:** 6

---

## 🧪 Testing

### Test Execution Summary

- **Unit Tests:** All passing
- **Integration Tests:** All passing
- **Coverage Areas:**
    - Custom JPA converter conversions
    - Entity relationship management
    - Repository query correctness
    - Service consolidation correctness
    - Null input and edge case handling
    - Match result processing end-to-end
    - Integration pipeline for CAB file import

### Test Quality Metrics

- **Consistent naming:** 100% compliance with naming standards
- **AAA pattern:** Applied throughout the test suite
- **Mock isolation:** Proper mock-based testing
- **Edge case coverage:** Comprehensive null/empty/blank scenarios
- **Helper methods:** Reusable test initialisation helpers

---

## 🔍 Technical Details

### Architectural Changes

#### Custom JPA Converter Architecture

```
Entity Field (Enum)
    ↓
AttributeConverter<Enum, String>
    ↓
Database Column (String)
```

#### Benefits

1. **Type Safety:** Explicit conversion logic rather than implicit `EnumType.STRING`
2. **Flexibility:** Custom conversion logic per enum type
3. **Testability:** Converters can be unit tested independently
4. **Consistency:** Uniform conversion pattern across all enum fields

#### Service Consolidation

```
Before v5.3.0:
IpscController → IpscService → IpscMatchResultService → ClubEntityService
                                                       → MatchEntityService

After v5.3.0:
IpscController → IpscService → DomainService (match result init)
                             → IpscMatchService (match processing)
```

#### Benefits

1. **Fewer Service Dependencies:** Reduced inter-service coupling
2. **Clearer Boundaries:** DomainService owns entity initialisation
3. **Simplified Testing:** Fewer mocks required in service tests
4. **Better Cohesion:** Related functionality grouped logically

### Repository Design

#### Optimised Queries

- Match retrieval includes scheduled date for uniqueness
- Competitor queries use `Set` to prevent duplicates
- Unnecessary fetch joins removed for performance
- Stage competitor queries include null safety

---

## 🐛 Known Issues

None reported.

---

## 🔮 Future Enhancements

- Performance optimisation for large match imports
- Additional validation for match data integrity
- Enhanced error messaging for failed imports
- Real-time match result processing
- Additional IPSC data format support

---

## 👥 Contributors

Development Team

---

## 📝 Notes

This release represents a focused consolidation effort reducing service complexity while improving type
safety through custom JPA converters. The removal of `IpscMatchResultService` and `ScoreDto` simplifies
the service architecture by consolidating related functionality, reducing the number of moving parts. The
transition of `DtoMapping` to a Java record improves immutability, while the six new JPA converters
provide explicit, testable conversion logic for all enum-mapped fields.

The comprehensive repository query optimisation and entity relationship corrections deliver improved data
integrity and query efficiency, laying the groundwork for planned performance optimisation in future
releases.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history/)**



