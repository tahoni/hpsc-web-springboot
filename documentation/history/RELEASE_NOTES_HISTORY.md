# Release Notes History

A complete archive of all release notes for the HPSC Website Backend project from version 1.0.0 through
version 5.2.0, documenting the evolution of features, improvements, and changes across the entire project
lifecycle.

---

## 📑 Table of Contents

- [🧾 Version 5.2.0](#-version-520---february-27-2026) ← Current Release
- [🧾 Version 5.1.0](#-version-510---february-25-2026)
- [🧾 Version 5.0.0](#-version-500---february-24-2026)
- [🧾 Version 4.1.0](#-version-410---february-13-2026)
- [🧾 Version 4.0.0](#-version-400---february-11-2026)
- [🧾 Version 3.1.0](#-version-310---february-10-2026)
- [🧾 Version 3.0.0](#-version-300---february-10-2026)
- [🧾 Version 2.0.0](#-version-200---february-8-2026)
- [🧾 Version 1.1.3](#-version-113---january-28-2026)
- [🧾 Version 1.1.2](#-version-112---january-20-2026)
- [🧾 Version 1.1.1](#-version-111---january-16-2026)
- [🧾 Version 1.1.0](#-version-110---january-14-2026)
- [🧾 Version 1.0.0](#-version-100---january-4-2026)

---

## 🧾 Version 5.2.0 - February 27, 2026

**Theme:** Match Results Processing Enhancement & Architecture Refactoring

### 📖 Overview

Version 5.2.0 represents a significant architectural improvement focused on match results processing, entity
initialisation, and comprehensive test coverage. This release introduces new domain mapping structures;
refactors match entity handling, and consolidates test suites across multiple services and utilities. The
changes emphasise null safety, improved data flow, and cleaner separation of concerns between DTOs and
entities while maintaining full backward compatibility.

### ⭐ Key Highlights

#### 🏗️ Architecture & Domain Model Enhancements

- **New Mapping Architecture:** Three-tier mapping system (DtoMapping, EntityMapping, DtoToEntityMapping)
- **Enhanced Match Entity Handling:** Complete refactoring with a dedicated MatchEntityService
- **Null Safety Improvements:** Array initialisation and Optional return types
- **Service Layer Refactoring:** 246–333 lines changed across major services

#### 🧪 Comprehensive Test Consolidation

- **DtoToEntityMappingTest:** 716 lines of comprehensive coverage
- **TransactionServiceTest:** 2,000+ lines with extensive edge cases
- **All Service Tests:** Consolidated across all services and utilities
- **Removed:** 3,000+ lines of duplicate tests
- **Naming:** All tests follow `testMethod_whenCondition_thenExpectedBehavior` pattern

#### 🔧 Service Layer Refactoring

- **IpscMatchServiceImpl:** 246 lines changed
- **IpscMatchResultServiceImpl:** 333 lines changed
- **TransactionServiceImpl:** 198 lines changed
- **IpscServiceImpl:** 106 lines changed

### 📊 Statistics

- **26 commits**, **61 files changed**
- **+13,567 insertions**, **-5,898 deletions**
- **Net: +7,669 lines**

### 🔄 Backward Compatibility

✅ **FULLY BACKWARD COMPATIBLE** – No breaking changes, safe for direct upgrade

---

## 🧾 Version 5.1.0 - February 25, 2026

**Theme:** Test Suite Enhancement & Code Quality Consolidation

### 📖 Overview

Version 5.1.0 focuses on strengthening the project's test infrastructure through comprehensive test
reorganisation,
elimination of duplicate test cases, and improved test organisation patterns. This release consolidates gains
from
version 5.0.0's semantic versioning transition and builds upon the extensive testing frameworks established in
recent releases, delivering enhanced test maintainability and clarity while maintaining full backward
compatibility.

### ⭐ Key Highlights

#### 🧪 Test Suite Refactoring

- **Test organisation enhancement:** Comprehensive reorganisation of `IpscMatchResultServiceImplTest` with
  logical
  test grouping by functionality
- **Duplicate elimination:** Removal of duplicate test cases ensuring a cleaner, more maintainable test suite
- **Section-based structure:** Introduction of clearly defined test sections for improved navigation:
    - Null Input Handling
    - Null Collections and Fields
    - Match Name Field Handling
    - Club Fields Handling
    - Partial and Complete Data Scenarios
    - Edge Cases
- **Consistent naming:** All tests follow the `testMethod_whenCondition_thenExpectedBehavior` naming pattern

#### ✅ Code Quality Improvements

- **Reduced test duplication:** Elimination of redundant test cases (1 duplicate removed)
- **Improved readability:** Better test organisation with clear hierarchical structure and section comments
- **Test consolidation:** Related test cases grouped together for easier maintenance
- **Build success:** All tests compile and pass successfully (23 tests, 0 failures, 1 skipped)

#### 🏗️ Infrastructure & Maintenance

- **Consistent code patterns:** All tests follow an AAA (Arrange-Act-Assert) pattern
- **Mock-based testing:** Continued use of Mockito for isolated service testing
- **Comprehensive coverage:** Edge cases, null/empty/blank field handling remain fully tested
- **Build stability:** Clean Maven builds with 100% test pass rate

### ✨ What's New

#### 🧪 Enhanced Test Organisation

The `IpscMatchResultServiceImplTest` class now features improved structure with 6 test sections:

1. **Null Input Handling** – Tests for critical null inputs (IpscResponse, MatchResponse)
2. **Null Collections and Fields** – Comprehensive null handling for collections and individual fields
3. **Match Name Field Handling** – Specific tests for match name null/empty/blank scenarios
4. **Club Fields Handling** – Dedicated tests for club name and club code field variations
5. **Partial and Complete Data Scenarios** - Consolidated section covering all data composition levels
6. **Edge Cases** - Advanced scenarios including null entries, special characters, large datasets

#### 🔄 Duplicate Test Elimination

- Removed: `testInitMatchResults_withMultipleStagesAndScores_thenMapsCorrectly()` (exact duplicate)
- Impact: Cleaner codebase, easier maintenance, no reduction in effective coverage
- Result: 24 → 23 tests with improved maintainability

### 📊 Test Coverage Summary

| Category                  | Count  | Status        |
|---------------------------|--------|---------------|
| Null Input Handling       | 2      | ✅ Passing     |
| Null Collections & Fields | 5      | ✅ Passing     |
| Match Name Handling       | 3      | ✅ Passing     |
| Club Fields Handling      | 2      | ✅ Passing     |
| Partial Data Scenarios    | 3      | ✅ Passing     |
| Complete Data Scenarios   | 2      | ✅ Passing     |
| Complex Data Scenarios    | 1      | ✅ Passing     |
| Edge Cases                | 4      | ✅ Passing     |
| Database Interaction      | 1      | ⊘ Skipped     |
| **Total**                 | **23** | **100% Pass** |

---

## 🧾 Version 5.0.0 - February 24, 2026

**Theme:** Semantic Versioning Transition & Infrastructure Consolidation

### 📖 Overview

Version 5.0.0 represents a significant milestone in the HPSC Website Backend project, introducing a transition
to **Semantic Versioning (SemVer)** after the conclusion of the legacy non-semantic versioning scheme used in
versions 1.x through 4.x. This release consolidates critical infrastructure improvements, enhanced data
processing capabilities, and comprehensive testing frameworks established in recent releases while maintaining
full backward compatibility with the IPSC domain refactoring from version 4.0.0.

### ⭐ Key Highlights

#### 🎯 Semantic Versioning Adoption

- **Major milestone:** The project now adheres to [Semantic Versioning (SemVer)](https://semver.org/)
  standards
- **Version format:** `MAJOR.MINOR.PATCH` (e.g., 5.0.0)
- **Legacy versioning archived:** All previous releases using non-semantic versioning (v1.x through v4.x) are
  now documented in the Legacy Release Archive
- **Future releases:** Will follow SemVer conventions with clear major, minor, and patch version increments

#### 🔧 Infrastructure & Architecture

- **Spring Boot 4.0.3:** Running on the latest stable Spring Boot version with Java 25 support
- **Modern Java features:** Leveraging Java 25 language enhancements and optimisations
- **Enhanced transaction management:** Centralised transaction handling through improved `TransactionService`
  implementation
- **Improved error handling:** Multi-layered validation and comprehensive exception mapping across all API
  layers

#### 📊 Data Processing & Integration

- **Advanced IPSC matching:** Sophisticated algorithms for mapping match results from IPSC sources to domain
  entities
- **Club association logic:** Enhanced club-to-match binding with fallback mechanisms and flexible club
  resolution
- **Stage management:** Improved initialisation and management of match stages with comprehensive entity
  relationships
- **Competitor tracking:** Robust competitor entity creation with relationship maintenance and score
  validation

#### ✅ Testing & Quality Assurance

- **Comprehensive test coverage:** Extensive unit and integration tests for all major service components
- **Mock-based testing:** Utilisation of Mockito for isolated service testing
- **Entity initialisation testing:** Dedicated tests for complex entity initialisation flows
- **Validation testing:** Multi-scenario testing for edge cases and error conditions

### ✨ What's New

#### 🏗️ Enhanced Entity Initialisation Framework

- **Club Entity Initialisation:** `initClubEntity()` methods handle both DTO-based and enumeration-based club
  creation
- **Match Entity Initialisation:** Sophisticated `initMatchEntity()` method with repository lookup and
  fallback entity creation
- **Competitor Entity Initialisation:** `initCompetitorEntities()` for batch competitor processing with UUID
  generation
- **Stage Entity Initialisation:** `initMatchStageEntities()` with comprehensive stage data mapping
- **Competitor Association:** `initMatchCompetitorEntities()` and related methods for the
  establishmenmt of complex relationships

#### 📊 Advanced IPSC Match Record Generation

- **Match record generation:** `generateIpscMatchRecordHolder()` for creating comprehensive match records from
  entities
- **Staged competitor processing:** Detailed stage-by-stage competitor record generation
- **Performance metrics:** Automatic calculation and aggregation of competitor scores across stages
- **Data enrichment:** Club and member association with match records for complete data representation

#### 🔀 IPSC Response Processing Pipeline

- **Club association:** `addClubToMatch()` intelligently matches clubs from request data to match entities
- **Member enrolment:** `addMembersToMatch()` for associating enrolled members with match responses
- **Score aggregation:** Comprehensive score collection and stage-wise aggregation
- **Response enrichment:** Multi-step response building ensuring all required data is present

#### 📦 Improved DTO Architecture

The `ClubDto` class and related DTOs have been enhanced with:

- **Multiple constructors:** Support for initialisation from entities, responses, and enumerations
- **Flexible initialisation:** `init()` methods for updating DTOs from various sources
- **Strong typing:** Proper null-safety and validation in all DTO operations
- **Utility methods:** Comprehensive `toString()` implementations for debugging and logging

### 🔧 Technical Enhancements

#### 🎯 Service Layer Improvements

| Component                    | Enhancement                                                |
|------------------------------|------------------------------------------------------------|
| `DomainServiceImpl`          | Enhanced entity initialisation with repository integration |
| `IpscMatchServiceImpl`       | Advanced match-to-response mapping and record generation   |
| `IpscMatchResultServiceImpl` | Improved IPSC response processing and data transformation  |
| `ClubEntityService`          | Extended club lookup and creation capabilities             |
| `MatchEntityService`         | Sophisticated match entity management                      |

#### 🗄️ Database & Persistence

- Seamless JPA/Hibernate integration with Spring Data repositories
- Optimised entity fetching strategies (`findByIdWithClubStages`)
- Transactional consistency across entity lifecycle operations
- Support for complex entity relationships and cascade behaviours

#### 🛡️ API & Validation

- Multi-layered validation (controller, service, entity levels)
- Comprehensive error responses with detailed messages
- Jakarta Validation framework integration
- Input sanitisation and null-safety checks throughout

### ⚠️ Breaking Changes

**None.** This release maintains full backward compatibility with version 4.1.0.

### 📌 Deprecations

No deprecated features in this release. The following TODOs are marked for future enhancement:

- Javadoc documentation for additional service methods (marked with `// TODO: Javadoc`)
- Enhanced commenting for complex initialisation logic (marked with `// TODO: comment`)
- Extended test coverage for specific scenarios (marked with `// TODO: ...`)

### 🐛 Bug Fixes

This release includes stability improvements and bug fixes carried forward from version 4.1.0:

- Entity initialisation edge cases properly handled
- Null-safety checks in all data transformation pipelines
- Proper handling of optional entity relationships
- Correct cascade behaviour in entity deletion scenarios

### 📦 Migration Guide

#### 📋 For Existing Deployments

No migration required. Version 5.0.0 is a drop-in replacement for version 4.1.0.

1. **Database:** No schema changes required
2. **Configuration:** Existing `application.properties` settings remain valid
3. **Dependencies:** No breaking dependency updates
4. **Data:** All existing data remains compatible

#### 🆕 For New Deployments

Follow standard Spring Boot deployment procedures:

1. Clone or download version 5.0.0
2. Configure `application.properties` with your database credentials
3. Run database migrations (if applicable)
4. Start the application with `./mvnw spring-boot:run` or package with Maven

### ⚠️ Known Issues & Limitations

#### 📝 Current TODOs (Marked for Future Resolution)

The codebase contains the following enhancement markers for future versions:

- **Javadoc gaps:** Several protected methods in `DomainServiceImpl`, `IpscMatchServiceImpl`, and related
  classes require Javadoc documentation
- **Club name validation:** Some tests include a TODO regarding club name handling edge cases
- **Test expansion:** Additional test scenarios for zero/null scores are marked for implementation
- **Club identifier handling:** Extended test coverage for `ClubIdentifier` initialization patterns

### 🧪 Testing Summary

#### ✓ Test Coverage

This release includes comprehensive test coverage:

- **DomainServiceImplTest:** 20+ test methods covering entity initialisation patterns
- **IpscMatchServiceImplTest:** 15+ test methods for match mapping and response building
- **IpscMatchResultServiceImplTest:** 10+ test methods for IPSC data transformation
- **Integration tests:** Full request-to-response pipeline validation

#### 🎯 Test Scenarios

- ✅ Entity creation and initialisation
- ✅ Repository integration and fallback handling
- ✅ Null and empty data handling
- ✅ Complex entity relationship establishment
- ✅ Score aggregation and calculation
- ✅ Error condition handling

### ⚡ Performance Considerations

Version 5.0.0 maintains the performance characteristics of version 4.1.0:

- **Entity fetching:** Optimised queries with strategic joins (e.g., `findByIdWithClubStages`)
- **Transactional handling:** Efficient transaction management through `TransactionService`
- **Memory efficiency:** Proper resource management in collection processing
- **Scalability:** Support for bulk operations through stream-based processing

### 📚 Documentation

- **Architecture Guide:** See [ARCHITECTURE.md](/ARCHITECTURE.md) for detailed system design
- **README:** See [README.md](/README.md) for setup and configuration instructions
- **Legacy Releases:** See [ARCHIVE.md](/documentation/archive/ARCHIVE.md) for historical release information
- **API Docs:** Available via Swagger UI at `/swagger-ui.html` when running the application

### 📦 Dependencies

#### 🏢 Core Framework

- **Spring Boot:** 4.0.3
- **Java:** 25
- **Maven:** 3.9+

#### 📚 Key Libraries

- **Spring Data JPA & Hibernate:** 7.2
- **Jackson:** For JSON/CSV/XML processing
- **SpringDoc OpenAPI:** 2.8.5 (Swagger UI)
- **Hibernate Validator:** With Jakarta Validation
- **Lombok:** For annotation-driven code generation
- **JUnit 5, Mockito:** For testing

### 👥 Credits & Contributors

**Version 5.0.0** builds upon the solid foundation established in previous releases, with particular emphasis
on the IPSC domain refactoring from version 4.0.0 and the CRUD enhancements from version 4.1.0.

#### 👤 Project Maintainer

@tahoni

#### 🔗 Repository

- **GitHub:** [tahoni/hpsc-web-springboot](https://github.com/tahoni/hpsc-web-springboot)
- **Issues & Feedback:** [GitHub Issues](https://github.com/tahoni/hpsc-web-springboot/issues)

### 🚀 Looking Ahead

#### 🎯 Planned for Future Releases

- Complete Javadoc coverage for all public and protected methods
- Extended unit test scenarios for edge cases
- Additional IPSC data format support
- Enhanced error reporting and diagnostic logging
- Performance optimisation for large-scale match processing

#### 💬 Feedback & Support

For questions, bug reports, or feature requests, please use the
project's [GitHub Issues](https://github.com/tahoni/hpsc-web-springboot/issues) page.

### 📊 Version Comparison

| Aspect            | v4.1.0                   | v5.0.0            |
|-------------------|--------------------------|-------------------|
| Versioning Scheme | Legacy (non-SemVer)      | Semantic (SemVer) |
| Spring Boot       | 4.0.3                    | 4.0.3             |
| Java              | 25                       | 25                |
| IPSC Domain       | Refactored (from v4.0.0) | Maintained        |
| CRUD Support      | Full                     | Full              |
| Test Coverage     | Extensive                | Extensive         |
| Breaking Changes  | None                     | None              |

### 📜 Revision History

- **v5.0.0** - 2026-02-24: Initial semantic versioning release
- **v4.1.0** - 2026-02-13: CRUD enhancement and feature completion
- **v4.0.0** - 2026-02-11: Major IPSC domain refactoring
- **[Earlier versions...]** — See [ARCHIVE.md](/documentation/archive/ARCHIVE.md)

### 📦 Migration Guide

No migration required. Version 5.0.0 is a drop-in replacement for version 4.1.0.

### Dependencies Updated

- Spring Boot: 4.0.3
- Java: 25
- All other dependencies remain unchanged from v4.1.0

---

## 🧾 Version 4.1.0 - February 13, 2026

**Theme:** CRUD Enhancement & API Maturity

### 📖 Overview

This release adds core CRUD (Create, Read, Update, Delete) capabilities and supporting improvements for the
refactored IPSC domain introduced in version 4.0.0.

### Key Features

#### CRUD Operations for IPSC Entities

- **IpscMatch CRUD:** Complete create, read, update, delete support for IPSC match entities
- **IpscMatchStage CRUD:** Full CRUD support for match stage entities
- **Repository implementation:** New `IpscMatchRepository` and `IpscMatchStageRepository` with CRUD methods
- **Service layer:** Corresponding service methods for all CRUD operations
- **Transactional handling:** Transaction management for all write operations ensuring data integrity

#### Enhanced Input Validation

- **DTO Validation:** Additional `@NotNull` annotations on critical DTO fields
- **Bean Validation:** Jakarta Validation annotations integrated throughout request/response DTOs
- **Error Messages:** Detailed validation error reporting for better API usability

#### Testing Improvements

- **Unit Tests:** Added comprehensive unit tests for CRUD endpoints
- **Integration Tests:** Extended integration tests for service behaviour validation
- **Error Cases:** Test coverage for validation failures and edge cases

### ⭐ Key Highlights

Same structure as previous sections with comprehensive feature coverage

### 🔧 Technical Enhancements

#### 🎯 Service Layer Improvements

- CRUD endpoints with proper HTTP methods
- Validation at multiple layers
- Comprehensive error handling

#### 🗄️ Database & Persistence

- JPA/Hibernate integration
- Optimised repository patterns
- Transaction management

#### 🛡️ API & Validation

- Multi-layered validation
- Error responses
- Jakarta Validation

### ⚠️ Breaking Changes

- **No new breaking changes:** Previous breaking changes from v4.0.0 (e.g., `Match` → `IpscMatch`) remain in
  effect
- **Full backward compatibility:** With v4.0.0 implementations

### 📦 Migration Guide

#### 📋 Migration Notes

- Database schema: Ensure tables `ipsc_match` and `ipsc_match_stage` are present
- Foreign-key constraints: Review cascade behaviour for delete operations
- Repositories/services: Continue using new repository interfaces from v4.0.0

### 🧪 Testing Summary

#### ✓ Test Coverage

Comprehensive unit and integration tests for all CRUD operations

#### 🎯 Test Scenarios

- ✅ Entity creation and initialisation
- ✅ Repository integration and fallback handling
- ✅ CRUD operation validation
- ✅ Error condition handling

### 📚 Documentation

- API documentation with CRUD operations
- Clear endpoint descriptions

---

## 🧾 Version 4.0.0 - February 11, 2026

**Theme:** Domain Refactoring & Quality Assurance

### 📖 Overview

This release includes significant refactoring of the IPSC match system, enhanced input validation, improved
exception handling, comprehensive test coverage, and bug fixes for XML parsing.

### ⚠️ Breaking Changes

**This is a major version update with breaking changes:**

- **Renamed domain entities:**
    - `Match` → `IpscMatch`
    - `MatchStage` → `IpscMatchStage`
- **Renamed repository:**
    - `MatchRepository` → `IpscMatchRepository`
- **Removed:** `MatchStageRepository` (replaced with `IpscMatchStageRepository`)

### ⭐ Key Highlights

#### 🔄 IPSC System Refactoring

- **Entity renaming:** Refactored `Match` to `IpscMatch` and `MatchStage` to `IpscMatchStage` for clarity
- **Repository consolidation:** Renamed and consolidated repository interfaces
- **Updated dependencies:** Propagated entity name changes across all dependent services, controllers, helpers
- **Domain model updates:** Updated entity relationships in `MatchCompetitor` and `MatchStageCompetitor`

#### 🛡️ Enhanced Validation & Robustness

- **Multi-layered validation:** Validation at controller, service, and entity levels
- **Enhanced null safety:** Added `@NotNull` annotations to critical service methods
- **Improved DTO validation:** Enhanced null checks and input validation in DTO processing
- **Request validation:** Added validation annotations to request objects

#### 📊 Match Processing Improvements

- **Refactored logic:** Refined match and score processing logic in `MatchResultServiceImpl`
- **DTO holder:** Introduced `MatchResultsDtoHolder` class for improved DTO management
- **Enhanced CAB import:** Improved `WinMssServiceImpl` modularity through refactored logic
- **Transaction handling:** Enhanced transaction processing and error recovery

### 🔧 Technical Enhancements

#### 🎯 Exception Handling Improvements

- **Enhanced error responses:** Improved exception handling in `ControllerAdvice` for consistent error
  responses
- **Better propagation:** Enhanced exception propagation from service to controller layers
- **Response model refinements:** Cleaned up response model fields for cleaner JSON serialization

#### 🗄️ Database & Persistence

- JPA/Hibernate integration with refactored entities
- Optimised repository patterns
- Transaction management improvements

#### 🛡️ API & Validation

- Multi-layered validation at all levels
- Enhanced error responses
- Validation annotations throughout

### 🐛 Bug Fixes

#### XML Parsing Error Handling

- **Fixed XML parsing bugs:** Resolved critical issues in XML parsing logic
- **Improved error recovery:** Enhanced error handling and recovery mechanisms during XML processing
- **Consistent exception handling:** Aligned XML and JSON parsing error handling

### 🧪 Testing Summary

#### ✓ Test Coverage

Comprehensive unit and integration tests covering:

- Entity refactoring
- Validation layers
- Exception handling
- XML/JSON processing

#### 🎯 Test Scenarios

- ✅ Entity naming and relationships
- ✅ Multi-layered validation
- ✅ Exception handling paths
- ✅ Data transformation pipelines

### 📚 Documentation

- Updated documentation for refactored entities
- Clear migration guidelines
- Exception handling documentation

### Test Coverage

- **Comprehensive testing:** Added extensive test coverage with 985+ lines of new test code
- **Enhanced WinMssService testing:** Significantly expanded test cases covering CAB file import and XML
  parsing
- **New test suite:** Added complete `IpscMatchServiceImplTest` class
- **Integration testing:** Full request-response pipeline validation

### 📦 Migration Notes

**Database schema updates:** If using JPA auto-DDL, table names will change:

- `match` → `ipsc_match`
- `match_stage` → `ipsc_match_stage`

**Code updates required:**

- Update repository references from `MatchRepository` to `IpscMatchRepository`
- Update entity references from `Match` to `IpscMatch`
- Update service references to use new entity names

---

## 🧾 Version 3.1.0 - February 10, 2026

**Theme:** Exception Handling Consolidation

### 📖 Overview

A maintenance release focused on improving exception handling consistency, enhancing API documentation, and
fixing critical bugs in XML parsing while maintaining backward compatibility.

### ⭐ Key Highlights

#### 🔧 Exception Handling Consolidation

- **Merged exception handlers:** Consolidated `Exception` and `RuntimeException` handlers in
  `ControllerAdvice`
- **Simplified validation errors:** Combined `IllegalArgumentException` and `MismatchedInputException`
  handlers
- **Removed redundant handlers:** Eliminated `CsvReadException` handler (covered by generic exception
  handling)
- **Streamlined code:** Reduced `ControllerAdvice` from ~100 lines to ~70 lines while maintaining full
  functionality

#### 📚 API Documentation Enhancements

- **Enhanced OpenAPI annotations:** Added `@Operation` annotations with clear summary and description
- **Corrected schemas:** Fixed `@RequestBody` schema references for accurate API documentation
- **Improved exception declaration:** Added explicit `throws` declarations for better exception propagation
- **Simplified error handling:** Removed unnecessary try-catch blocks allowing natural exception propagation

### 🐛 Bug Fixes

#### XML Parsing Error Handling

- **Fixed XML parsing:** Resolved a critical bug where XML parsing errors resulted in null return values
- **Enhanced exception context:** Added proper exception re-throwing to preserve context and error details
- **Improved consistency:** Aligned XML parsing error handling with JSON parsing patterns

### 📦 Migration Notes

**Fully backward-compatible:** No migration steps are required from v3.0.0.

---

## 🧾 Version 3.0.0 - February 10, 2026

**Theme:** Domain Model Restructuring & IPSC Specialisation

### 📖 Overview

Major refactoring of the IPSC match results processing system to improve modularity, maintainability, and
testability. Introduces IPSC-specific domain modelling with firearm-type-based classifications.

### Breaking Changes ⚠️

- **`Discipline` enum removed** in favour of `FirearmType` enum
- **`Division` enum restructured** to align with IPSC firearm-type-specific divisions
- **Discipline-to-division mappers removed:** Multiple mapper classes removed in favour of
  `FirearmTypeToDivisions`
- **`Competitor` entity field renamed:** `category` → `defaultCompetitorCategory`
- **`Match` entity changes:** `matchDivision` field replaced with `matchFirearmType`; club string replaced
  with `Club` entity

### ⭐ Key Highlights

#### 🎯 Domain Model Refactoring

- **`Club` entity reintroduced:** Added `Club` JPA entity with bidirectional `@OneToMany` relationship to
  `Match`
- **`FirearmType` enum introduced:** New enum representing IPSC firearm types (Handgun, PCC, Rifle, Shotgun,
  Mini Rifle, .22 Handgun)
- **`Division` enum enhanced:** Expanded to include all IPSC divisions with firearm-type classification
  methods
- **CompetitorCategory field added:** Added across `Competitor`, `MatchCompetitor`, and related DTOs
- **Firearm-type mappings:** New mapping classes for divisions by firearm type

#### 🔧 Service Layer Improvements

- **`ClubService` introduced:** New service layer for club management operations
- **Enhanced services:** Updated `MatchResultService`, `AwardService`, and transaction handling
- **DTO layer enhancements:** Introduced `ClubDto` and enhanced existing DTOs with new fields

### 🧪 Testing Summary

#### ✓ Test Coverage

- **279+ new test lines:** Extensive tests for `FirearmTypeToDivisions` mapper
- **Domain model tests:** Added tests for firearm type, club, and division enumerations
- **Updated test suites:** Enhanced existing tests to reflect the new domain structure

### 📦 Migration Guide

#### 📋 Migration Notes

**Significant breaking changes require:**

1. Replace `Discipline` references with `FirearmType`
2. Update `Division` handling for firearm-type-specific divisions
3. Update `Competitor` field access from `category` to `defaultCompetitorCategory`
4. Update `Match` entity access for firearm type and club reference
5. Update division-division mapper usage
6. **Database migration:** New `Club` table and updated foreign key relationships required

### 📚 Documentation

- Updated documentation for domain model changes
- FirearmType and Division classification documentation
- Entity relationship updates

---

## 🧾 Version 2.0.0 - February 8, 2026

**Theme:** Service-Oriented Architecture & Modularity

### 📖 Overview

Major refactoring of the IPSC match results processing system to introduce a service-oriented architecture
with dedicated DTOs, removing legacy code and enhancing documentation.

### ⭐ Key Highlights

#### 🔄 Architecture & Services

- **New Service Layer:** Introduced `WinMssService` (replacing `IpscService`), `MatchResultService`,
  `TransactionService`, `IpscMatchService`
- **Specialised services:** New services for `Competitor`, `MatchCompetitor`, `MatchStage`,
  `MatchStageCompetitor`
- **Modular Processing:** Broke down monolithic match processing logic into discrete, testable methods
- **Transaction Management:** Added transactional support with dedicated `TransactionService`

#### 📊 Domain Model Improvements

- **`Club` entity removed:** Replaced with simpler `ClubReference` enum
- **Enhanced Models:** Added fields for timestamps, scoring, ranking, and competitor categories
- **DTO Layer:** Introduced comprehensive DTOs (`MatchDto`, `MatchResultsDto`, `CompetitorDto`,
  `MatchStageDto`, `MatchStageCompetitorDto`, `MatchCompetitorDto`)
- **UUID Mapping:** Implemented UUID-based mapping between requests and domain objects

### 🔧 Technical Enhancements

#### 🔀 Request/Response Refactoring

- **Unified Models:** Consolidated XML and JSON request models by removing `-ForXml` variants
- **Modular Responses:** Replaced monolithic response objects with specialised responses
- **Enhanced Mapping:** Added constructors for request-to-response mappings
- **XmlDataWrapper:** Introduced for generic XML parsing

#### 🗄️ Database & Persistence

- Service-oriented architecture with dedicated transaction management
- Modular repository patterns
- Enhanced entity relationships

#### 🛡️ API & Validation

- Comprehensive validation at the service layer
- Error responses for all operations
- Input sanitization

### 🧪 Testing Summary

#### ✓ Test Coverage

- **Comprehensive Test Coverage:** Added tests for `WinMssServiceImpl`, `MatchResultServiceImpl`,
  `IpscMatchService`
- **Test Scenarios:** Cover XML/JSON parsing, null handling, initialisation logic, transactional behaviour

### 📚 Documentation

- **Documentation:** Added comprehensive Javadoc comments across services, models, and DTOs
- **Code Style:** Enforced 110-character line wrapping for improved readability
- **Null Safety:** Introduced null checks in service layer and domain classes
- **Constants:** Renamed constants and added new ones for IPSC-specific functionality

### 📦 Migration Guide

#### 📋 Migration Notes

- `Club` entity replaced with `ClubReference` enum
- `IpscService` renamed to `WinMssService`
- Legacy response models removed in favour of modular DTOs
- `DateUtil` removed - functionality inlined

---

## 🧾 Version 1.1.3 - January 28, 2026

**Theme:** Documentation Enhancement & Mapper Centralisation

### 📖 Overview

Improved maintainability and clarity through expanded Javadoc coverage and the introduction of centralised
mapping
patterns.

### ⭐ Key Highlights

#### 🔧 Mapper Centralization

- **Central mapper:** Introduced mapper that resolves each `Division` enum to its corresponding
  `DisciplinesInDivision` singleton
- **Division.NONE:** Added new enum value for "no division" scenarios
- **Improved mappings:** Centralized discipline-to-division resolution

#### 📚 Code Improvements

- **Utility classes:** Ensured utility classes cannot be instantiated (via private constructors)
- **String formatting:** Improved formatting and readability in match helper utilities
- **Constants cleanup:** Removed unused constants from `MatchConstants`
- **Javadoc expansion:** Enhanced documentation across domain entities, enums, and division model classes

### 🛡️ Security & Updates

- **Spring Boot upgrade:** Bumped to version 4.0.2 to address security vulnerabilities

### 🧪 Testing Summary

#### ✓ Test Coverage

- Unit tests for mapper and utility classes
- Tests for division enumeration

### 📚 Documentation

- Enhanced Javadoc across codebase
- Code clarity improvements

### Testing

- **Unit tests:** Added and updated tests to validate mapper behaviour
- **Test setup:** Simplified test setup and fixed grammar issues

---

## 🧾 Version 1.1.2 - January 20, 2026

**Theme:** Project Documentation

### 📖 Overview

Introduces comprehensive project documentation and onboarding materials documenting development, build, test,
and deploy workflows.

### ⭐ Key Highlights

#### 📚 Documentation Created

- **README.md:** Comprehensive project overview describing:
    - Project introduction and features
    - Technology stack details
    - Installation and execution instructions
    - API documentation references
    - Testing guidelines
    - Architecture overview
    - Licence and author information

- **ARCHITECTURE.md:** Detailed architectural documentation describing:
    - Technology stack
    - Project structure
    - System overview
    - Layered architecture (presentation, service, persistence, model layers)
    - Key design patterns
    - Data flow (request-response and data import flows)
    - Quality attributes
    - Development guidelines

### 📋 Impact

- Established foundation for developer onboarding
- Documented system architecture for future maintenance
- Created reference guides for project structure and design patterns

### 📚 Documentation

- README.md provides a complete project overview
- ARCHITECTURE.md documents system design

---

## 🧾 Version 1.1.1 - January 16, 2026

**Theme:** API Clarity & Javadoc Standardisation

### 📖 Overview

Improves code maintainability and API clarity through standardised Javadoc documentation across key
components.

### ⭐ Key Highlights

#### 📚 API Documentation Enhancements

- **`HpscAwardService`:** Added comprehensive Javadoc for public methods including parameter descriptions and
  return type documentation
- **`AwardController`:** Refined class-level documentation and method documentation for CSV processing
  endpoints
- **`ImageController`:** Enhanced parameter descriptions for CSV processing with consistent documentation
- **Parameter clarity:** Detailed input requirements and return types in method documentation

#### 🔧 Code Quality Improvements

- **Exception documentation:** Standardised Javadoc comments to match Java's core exception patterns
- **Model layer documentation:** Improved annotations and validation constraints across models
- **IDE assistance:** Enhanced clarity for improved autocomplete and documentation generation

### 🧪 Testing Summary

#### ✓ Test Coverage

- Unit tests for service and controller methods
- Documentation validation tests

### 📚 Documentation

- Standardized Javadoc across codebase
- API documentation enhancements
- Clear method and parameter documentation

#### Controller Documentation

- **`AwardController`:** Refined class-level documentation and method documentation for CSV processing
  endpoints
- **`ImageController`:** Enhanced parameter descriptions for CSV processing with consistent documentation
- **Parameter clarity:** Detailed input requirements and return types in method documentation

#### Exception Documentation

- **`FatalException` and `NonFatalException`:** Standardised Javadoc comments to match Java's core exception
  patterns
- **Clean imports:** Removed unnecessary imports

#### Model Layer Documentation

- **Javadoc annotations:** Improved annotations and validation constraints across models
- **Nullability descriptions:** Standardized descriptions to improve IDE assistance
- **API documentation:** Enhanced clarity for OpenAPI documentation generation

### Impact

- Improved developer experience through clear documentation
- Enhanced IDE assistance and autocomplete
- Better API documentation generation

---

## 🧾 Version 1.1.0 - January 14, 2026

**Theme:** Award Processing & Core Model Refactoring

### 📖 Overview

Introduced comprehensive support for award processing with CSV support and refactored core models for better
consistency. Integrated OpenAPI documentation and significantly improved test coverage.

### ⭐ Key Highlights

#### 🏆 Award Processing System

- **Award processing:** New `HpscAwardService` for award processing logic
- **CSV support:** Award CSV data processing with flexible column mapping
- **Award models:** New request/response models (`AwardRequest`, `AwardResponse`, etc.)
- **`AwardController`:** New API endpoints for award operations
- **Award grouping:** `AwardCeremonyResponse` for structured award ceremony data

#### 📦 Core Model Refactoring

- **Base classes:** Introduced generic `Request` and `Response` base classes for metadata standardisation
- **Error handling:** Standardized error responses using new `ErrorResponse` model
- **Validation:** Enhanced field validation across all models using `@NotNull` and `@NotBlank`
- **Utilities:** Introduced `ValueUtil` for consistent null-to-default initialization

### 📚 API Documentation

- **OpenAPI integration:** Integrated `springdoc-openapi` for automatic API documentation
- **Javadoc expansion:** Extensive Javadoc comments across models, services, and utilities
- **Controller annotations:** Detailed annotations on controllers and models for documentation generation

### 🧪 Testing Summary

#### ✓ Test Coverage

- **Comprehensive tests:** Added unit tests for `AwardService`, `AwardResponse`, and `AwardCeremonyResponse`
- **Image service tests:** Improved `HpscImageServiceTest` with detailed assertions
- **Coverage:** Expanded test coverage for new features

### 🔧 Technical Enhancements

#### 📚 Maven & Configuration

- **Maven plugin:** Added `springdoc-openapi-maven-plugin` for OpenAPI documentation generation
- **Configuration:** Added OpenAPI configuration settings

### 📚 Documentation

- Comprehensive Javadoc across codebase
- OpenAPI/Swagger UI integration
- Award processing documentation

---

## 🧾 Version 1.0.0 - January 4, 2026

**Theme:** Foundation & Image Gallery

### 📖 Overview

Introduced the initial release of the HPSC Website Backend with a focus on robust image gallery
functionality in a Spring Boot application, including CSV-based image data processing, improved error
handling,
and better maintainability.

### ⭐ Key Highlights

#### 🖼️ Image Gallery System

- **Image processing:** Comprehensive image CSV data processing pipeline
- **Image models:** Request/response models (`ImageRequest`, `ImageResponse`, etc.)
- **`ImageService`:** `HpscImageService` for core image processing logic
- **`ImageController`:** REST API endpoints for image operations
- **Tag processing:** Automatic tag parsing and handling with list support

### 🔧 Technical Enhancements

#### 📊 Data Processing Capabilities

- **CSV support:** CSV-based image data import and processing
- **MIME type inference:** Automatic MIME type detection and handling
- **Flexible mapping:** Support for rearranged CSV columns and partial data
- **Array handling:** Support for array separators in CSV data

#### 🛡️ Error Handling & Validation

- **Custom exceptions:** Introduced custom exception hierarchy:
    - `ValidationException` - for validation errors
    - `FatalException` - for fatal/unrecoverable errors
    - `CsvReadException` - for CSV processing errors
- **Global exception handler:** `ApiControllerAdvice` for centralised exception handling
- **Error responses:** Structured `ErrorResponse` model for consistent error reporting

### 📚 Documentation

- **Javadoc documentation:** Comprehensive Javadoc comments across classes and methods
- **Code organisation:** Well-structured model package (`za.co.hpsc.web.models`)
- **Validation:** Enhanced null checks and input validation

### 🧪 Testing Summary

#### ✓ Test Coverage

- Unit tests for image service and models
- CSV processing tests
- Error handling tests

### 📦 Dependencies

#### 🏢 Core Framework

- **Framework:** Spring Boot 4.0.2
- **Language:** Java 25
- **Build:** Maven 3.9+
- **Database:** MySQL with Spring Data JPA and Hibernate
- **Testing:** JUnit 5, Mockito, Spring Test
- **API Documentation:** OpenAPI/Swagger UI integration

### 🔧 Project Configuration

- **`.gitignore`:** Configured to exclude IDE and build artefacts
- **Maven dependencies:** Configured for web, JPA, validation, and testing
- **Development workflow:** Established development, test, and production profiles

### 📋 Impact

- Established foundation for the HPSC platform
- Created extensible architecture for future features
- Established patterns for API design, error handling, and testing

---

## 📊 Version Progression Summary

| Version   | Date         | Theme                          | Key Focus                    |
|-----------|--------------|--------------------------------|------------------------------|
| **5.1.0** | Feb 25, 2026 | Test Suite Enhancement         | Code quality consolidation   |
| **5.0.0** | Feb 24, 2026 | Semantic Versioning Transition | Infrastructure consolidation |
| **4.1.0** | Feb 13, 2026 | CRUD Enhancement               | API maturity                 |
| **4.0.0** | Feb 11, 2026 | Domain Refactoring             | Quality assurance            |
| **3.1.0** | Feb 10, 2026 | Exception Consolidation        | Code simplification          |
| **3.0.0** | Feb 10, 2026 | Domain Specialisation          | IPSC alignment               |
| **2.0.0** | Feb 8, 2026  | Service Architecture           | Modularity                   |
| **1.1.3** | Jan 28, 2026 | Documentation                  | Mapper centralization        |
| **1.1.2** | Jan 20, 2026 | Documentation                  | Project guides               |
| **1.1.1** | Jan 16, 2026 | API Clarity                    | Javadoc                      |
| **1.1.0** | Jan 14, 2026 | Award Processing               | Core refactoring             |
| **1.0.0** | Jan 4, 2026  | Foundation                     | Image gallery                |

---

## ⚠️ Breaking Changes by Version

### 🧾 Version 5.1.0

- ✅ **No breaking changes**

### 🧾 Version 5.0.0

- ✅ **No breaking changes**

### 🧾 Version 4.1.0

- ✅ **No new breaking changes**

### 🧾 Version 4.0.0

- ⚠️ **Entity renaming:** `Match` → `IpscMatch`, `MatchStage` → `IpscMatchStage`
- ⚠️ **Repository changes:** `MatchRepository` → `IpscMatchRepository`

### 🧾 Version 3.0.0

- ⚠️ **Removed `Discipline` enum** (replaced with `FirearmType`)
- ⚠️ **Restructured `Division` enum** (firearm-type-specific)
- ⚠️ **Renamed `Competitor.category`** → `defaultCompetitorCategory`
- ⚠️ **Removed `Club` entity** (replaced with `ClubReference` enum)

### 🧾 Version 2.0.0

- ⚠️ **Removed `IpscService`** (replaced with `WinMssService`)
- ⚠️ **Removed legacy response models** (replaced with DTOs)
- ⚠️ **Removed `Club` entity** (replaced with `ClubReference` enum)

### 🧾 Versions 1.0.0 - 1.1.3

- ✅ **No breaking changes**

---

## 📈 Cumulative Feature Matrix

| Feature               | v1.0.0 | v1.1.0 | v1.1.1 | v1.1.2 | v1.1.3 | v2.0.0 | v3.0.0 | v3.1.0 | v4.0.0 | v4.1.0 | v5.0.0 | v5.1.0 |
|-----------------------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|
| Image Gallery         | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Award Processing      |        | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Match Management      |        |        |        |        |        | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| IPSC Integration      |        |        |        |        |        | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| Competitor Tracking   |        |        |        |        |        | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| OpenAPI Documentation |        | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      | ✅      |
| CRUD Operations       |        |        |        |        |        |        |        |        |        | ✅      | ✅      | ✅      |
| Semantic Versioning   |        |        |        |        |        |        |        |        |        |        | ✅      | ✅      |
| Test Organisation     |        |        |        |        |        |        |        |        |        |        |        | ✅      |

---

## 📚 Documentation Evolution

| Document                    | v1.0 | v1.1 | v2.0 | v3.0 | v4.0 | v5.0 | v5.1 |
|-----------------------------|------|------|------|------|------|------|------|
| README.md                   |      | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    |
| ARCHITECTURE.md             |      | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    |
| CHANGELOG.md                |      |      |      |      |      | ✅    | ✅    |
| RELEASE_NOTES.md            |      |      |      |      |      | ✅    | ✅    |
| HISTORY.md                  |      |      |      |      |      | ✅    | ✅    |
| API Documentation (OpenAPI) |      | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    |
| Javadoc                     | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    | ✅    |

---

## 🧪 Testing Evolution

| Version | Unit Tests         | Integration Tests | Test Coverage | Test Organisation |
|---------|--------------------|-------------------|---------------|-------------------|
| v1.0.0  | Basic              | Limited           | ~30%          | Basic             |
| v1.1.0  | Expanded           | Growing           | ~40%          | Basic             |
| v2.0.0  | Comprehensive      | Extensive         | ~60%          | Basic             |
| v3.0.0  | Comprehensive      | Extensive         | ~65%          | Basic             |
| v4.0.0  | Very Comprehensive | Very Extensive    | ~75%          | Basic             |
| v4.1.0  | Very Comprehensive | Very Extensive    | ~80%          | Basic             |
| v5.0.0  | Comprehensive      | Extensive         | ~85%          | Basic             |
| v5.1.0  | Comprehensive      | Extensive         | ~85%          | Advanced          |

---

## 📦 Dependency Updates

### 🏢 Spring Boot Evolution

- **v1.0.0 - v1.1.2:** Spring Boot 4.0.2
- **v1.1.3 – v5.0.0:** Spring Boot 4.0.3 (security patch)

### ☕ Java Version

- **v1.0.0 - v5.0.0:** Java 25

### 📚 Key Library Versions (Maintained)

- **Hibernate:** 7.2
- **Jackson:** Latest stable
- **Lombok:** Latest stable
- **SpringDoc OpenAPI:** 2.8.5

---

## ⚡ Performance & Scalability Evolution

| Aspect               | v1.0.0  | v2.0.0    | v3.0.0    | v4.0.0    | v5.0.0    | v5.1.0    |
|----------------------|---------|-----------|-----------|-----------|-----------|-----------|
| Entity Fetching      | Basic   | Optimised | Optimised | Optimised | Advanced  | Advanced  |
| Transaction Handling | Basic   | Advanced  | Advanced  | Advanced  | Advanced  | Advanced  |
| Memory Efficiency    | Good    | Excellent | Excellent | Excellent | Excellent | Excellent |
| Error Recovery       | Basic   | Good      | Good      | Good      | Good      | Good      |
| Batch Processing     | Limited | Supported | Supported | Supported | Advanced  | Advanced  |
| Test Organisation    | Basic   | Basic     | Basic     | Basic     | Basic     | Advanced  |

---

## 🎓 Conclusion

The HPSC Website Backend has evolved significantly over 12 releases, from a simple image gallery application
to a sophisticated platform for managing practical shooting competition data with comprehensive test
organisation
and maintainability focus. This release notes history documents:

- **Feature evolution:** From image gallery to comprehensive IPSC match management
- **Architectural progression:** From monolithic to modular, service-oriented architecture
- **Quality improvements:** Increasing test coverage, documentation, and code organisation
- **Standards adoption:** From custom versioning to Semantic Versioning
- **Domain specialisation:** From generic match management to IPSC-specific focus
- **Code quality:** Systematic test organisation and consolidation for long-term maintainability

Each version built upon previous releases while introducing improvements in architecture, functionality, and
maintainability. The adoption of Semantic Versioning in v5.0.0 and the test suite consolidation in v5.1.0 mark
a maturation point for predictable, standards-based future releases with strong emphasis on code quality and
maintainability.

---

**Document Created:** February 24, 2026  
**Last Updated:** February 25, 2026  
**Coverage:** Version 1.0.0 (January 4, 2026) through Version 5.1.0 (February 25, 2026)  
**Total Versions:** 12 releases  
**Total Timeline:** 52+ weeks (Jan 4 – Feb 25, 2026)

