# Release Notes – Version 5.4.0

**Release Date:** April 26, 2026  
**Status:** ✨ Stable

---

## 🎯 Theme

**Competitor Enrolment, Service Transformation & Comprehensive Test Expansion**

Version 5.4.0 delivers a significant expansion of member and competitor handling capabilities, a major
service-layer architectural transformation, and the most extensive test suite growth in the project's
history. This release introduces `EnrolledCompetitorDto` for tracking enrolled competitors through the
IPSC processing pipeline; renames and restructures `IpscMatchService` into the more descriptive
`TransformationService`; enhances the `ClubIdentifier` enum with abbreviation support; adds SAPSA
number validation and duplicate competitor filtering; and reorganises the model package hierarchy for
improved clarity. The release also introduces twenty-plus new test classes covering controllers,
converters, domain entities, exception types, models, and service integration, along with improved
CI/CD pipelines featuring Qodana JVM linting and JaCoCo code coverage integration.

---

## ⭐ Key Highlights

### 👥 Competitor Enrolment & Members CRUD

- **EnrolledCompetitorDto introduced:** New DTO (138 lines) capturing the full enrolled competitor
  profile through the IPSC processing pipeline
    - Complete field set for tracking competitor enrolment state
    - Comprehensive Javadoc documentation across all fields
    - Full test coverage with `EnrolledCompetitorDtoTest` (196 lines)
- **Competitor duplicate filtering:** `CompetitorDto` enhanced to deduplicate competitors based on
  SAPSA number and ID
- **SAPSA number validation:** Maximum SAPSA number validation added via `IpscUtil`
- **ICS alias value updates:** Updated ICS alias and competitor number constants in `IpscConstants`

### 🏗️ Service Transformation Architecture

- **IpscMatchService renamed to TransformationService:** Service interface and implementation
  restructured for improved semantic clarity
    - `TransformationServiceImpl` introduced (1,098 lines) replacing the former `IpscMatchServiceImpl`
      (867 lines removed)
    - Interface updated with comprehensive Javadoc (`TransformationService`, 27 lines)
    - Improved method naming and data handling throughout
- **MatchHolder introduced:** New `MatchHolder` data class (23 lines) for improved match data
  encapsulation in `IpscMatchService`
- **MatchCompetitorEntityService:** Updated to return lists for bulk competitor retrieval
- **MatchStageCompetitorEntityService:** Enhanced with improved list-based retrieval

### 🏷️ ClubIdentifier Enhancement

- **Abbreviation field added to `ClubIdentifier` enum** (38 lines changed)
    - Each club identifier now carries a dedicated abbreviation value
    - `ClubIdentifierConverter` updated to use abbreviation for database persistence
    - `DomainServiceImpl` updated to use abbreviation for club lookup
    - Full test coverage updated in `ClubIdentifierTest`

### 📦 Model Package Restructuring

- **`domain` package renamed to `data`** for mapping classes
    - `DtoMapping`, `DtoToEntityMapping`, and `EntityMapping` moved from `ipsc/domain` to `ipsc/data`
    - Improved semantic clarity between domain entities and data mapping classes
- **Holders' package reorganisation:**
    - `MatchResultsDto` and `MatchResultsDtoHolder` consolidated into `holders/dto`
    - `IpscRequestHolder` and `IpscResponseHolder` moved to dedicated `holders` sub-packages
    - New `IpscMatchRecordHolder` added to holders (10 lines)
- **Record restructuring:**
    - `CompetitorMatchRecord` renamed to `CompetitorRecord` (10 lines)
    - `CompetitorResultRecord` introduced (13 lines)
    - `MatchCompetitorOverallResultsRecord` introduced (9 lines)
    - `MatchCompetitorStageResultRecord` introduced (renamed from prior record)
    - `IpscMatchRecord` minor update (2 lines)
    - Removed `MatchCompetitorRecord` (16 lines) and `IpscMatchRecordHolder` from records (8 lines)

### 🔧 Controller & Error Handling Improvements

- **ControllerAdvice enhanced:** Improved error handling with updated response management
    - Fixed handler methods for `IllegalArgumentException` and validation errors
    - Comprehensive new test class `ControllerAdviceTest` (299 lines)
- **IpscController updated:** Minor updates to controller endpoint handling (6 lines)

### 🧪 Comprehensive Test Suite Expansion

The most extensive single-release test expansion in the project's history, adding over 7,000 lines of
new test code across 20+ new test classes.

#### New Controller Tests

- **`AwardControllerTest`** (163 lines) – endpoint validation for award processing
- **`ImageControllerTest`** (163 lines) – endpoint validation for image processing
- **`IpscControllerTest`** (156 lines) – endpoint validation for IPSC operations
- **`ControllerAdviceTest`** (299 lines) – comprehensive error handling test coverage

#### New Converter Tests

- **`ClubIdentifierConverterTest`** (76 lines)
- **`CompetitorCategoryConverterTest`** (85 lines)
- **`DivisionConverterTest`** (85 lines)
- **`FirearmTypeConverterTest`** (103 lines)
- **`MatchCategoryConverterTest`** (76 lines)
- **`PowerFactorConverterTest`** (85 lines)

#### New Domain Entity Tests

- **`ClubTest`** (467 lines) – comprehensive Club entity coverage
- **`CompetitorTest`** (354 lines) – Competitor entity coverage
- **`IpscMatchTest`** (367 lines) – IpscMatch entity coverage
- **`IpscMatchStageTest`** (333 lines) – IpscMatchStage entity coverage
- **`MatchCompetitorTest`** (364 lines) – MatchCompetitor entity coverage
- **`MatchStageCompetitorTest`** (645 lines) – MatchStageCompetitor entity coverage

#### New Exception Tests

- **`FatalExceptionTest`** (161 lines)
- **`NonFatalExceptionTest`** (173 lines)
- **`ValidationExceptionTest`** (115 lines)

#### New Model & Utility Tests

- **`ControllerResponseTest`** (89 lines)
- **`RequestTest`** (100 lines)
- **`AwardRequestForCSVTest`** (394 lines)
- **`ImageRequestForCSVTest`** (244 lines)
- **`EnrolledCompetitorDtoTest`** (196 lines)
- **`FirearmTypeToDivisionsTest`** (40 lines)
- **`ValueUtilTest`** (100 lines)

#### New Integration Tests

- **`AwardServiceIntegrationTest`** (295 lines) – end-to-end award CSV processing
- **`ImageServiceIntegrationTest`** (348 lines) – end-to-end image CSV processing
- **`DtoToEntityMappingIntegrationTest`** (71 lines) – mapping pipeline validation

#### New Service Tests

- **`TransformationServiceTest`** (1,026 lines) – comprehensive coverage for the new service
- **`MatchCompetitorDtoTest`** (253 lines)

#### Updated Existing Test Suites

- **`DomainServiceTest`:** 1,428 lines changed – enhanced coverage
- **`IpscServiceIntegrationTest`:** 649 lines changed – expanded integration scenarios
- **`TransactionServiceTest`:** 1,736 lines changed – comprehensive updates
- **`IpscServiceTest`:** 737 lines changed
- **`CompetitorDtoTest`:** 119 lines changed – SAPSA validation coverage
- **`DtoToEntityMappingTest`:** 157 lines changed – package move updates

#### Removed Test Suites

- **`IpscMatchServiceTest`:** 10,076 lines removed – service renamed to `TransformationService`

### 🔄 Code Quality & CI/CD

- **Qodana JVM Linter:** `qodana.yaml` updated with `jetbrains/qodana-jvm` linter
    - Configured for JVM-targeted static analysis
    - Licence auditing enabled via `CheckDependencyLicenses`
- **JaCoCo Code Coverage:** `pom.xml` updated with JaCoCo 0.8.14 configuration and coverage profile
    - Coverage reports output to `/coverage` directory
    - Integrated with Qodana code quality workflow
- **Code Quality Workflow:** `.github/workflows/code_quality.yml` enhanced (34 lines)
    - Added dependency installation step
    - Extended branch pattern support (feature, bugfix, hotfix branches)
- **`.aiignore` file added:** Excludes AI-irrelevant files from AI assistant context
- **`qodana.yml` removed:** Duplicate Qodana configuration removed; consolidated in `qodana.yaml`
- **Spring Framework stabilised:** Reverted `spring-framework.version` from 7.0.8 to 7.0.7

### 🐛 Bug Fixes

- **PCC Optics division code:** Fixed incorrect division constant value
- **Division constants:** Updated `IpscConstants` for accurate competitor number and ICS alias values
- **ControllerAdvice error handling:** Fixed exception handler methods for improved response management
- **ClubIdentifier abbreviation:** Updated converter to correctly use abbreviation for database persistence
- **Removed unused firearm type assignment** from the match processing path

---

## 📦 What's New

### Added Features

#### New Classes & Records

- `EnrolledCompetitorDto` – enrolled competitor DTO with full Javadoc
- `MatchHolder` – match data holder for `IpscMatchService`
- `IpscMatchRecordHolder` - match data holder record
- `CompetitorResultRecord` – competitor result record
- `MatchCompetitorOverallResultsRecord` – overall results record
- `MatchCompetitorStageResultRecord` – stage result record
- `TransformationService` interface – replaces `IpscMatchService`
- `TransformationServiceImpl` class (1,098 lines) – replaces `IpscMatchServiceImpl`

#### New Constants

- `HpscConstants` updated (3 lines)
- `MatchConstants` updated (3 lines)
- `SystemConstants` updated (3 lines)

#### CI/CD & Configuration

- `.aiignore` file for AI assistant context management
- JaCoCo 0.8.14 coverage profile in `pom.xml`
- Qodana JVM linter configuration in `qodana.yaml`
- Branch patterns for feature, bugfix, and hotfix in `code_quality.yml`

### Changed

#### Services

- **TransformationServiceImpl:** 1,098 lines introduced (replaces `IpscMatchServiceImpl`)
- **DomainServiceImpl:** 139 lines changed – enhanced competitor and match handling
- **TransactionServiceImpl:** 87 lines changed – updated with list-based operations
- **IpscServiceImpl:** 34 lines changed
- **CompetitorEntityServiceImpl:** 7 lines changed
- **ImageServiceImpl:** 2 lines changed
- **MatchCompetitorEntityServiceImpl:** 9 lines changed – returns lists
- **MatchStageCompetitorEntityServiceImpl:** 9 lines changed – enhanced retrieval

#### Domain Entities

- **Club:** 15 lines changed
- **Competitor:** 17 lines changed
- **IpscMatch:** 13 lines changed
- **IpscMatchStage:** 12 lines changed
- **MatchCompetitor:** 16 lines changed
- **MatchStageCompetitor:** 18 lines changed

#### Enumerations

- **ClubIdentifier:** 38 lines changed – abbreviation field added
- **Division:** 2 lines changed – PCC Optics fix

#### DTOs

- **CompetitorDto:** 13 lines changed – SAPSA deduplication
- **MatchCompetitorDto:** 22 lines changed
- **ClubDto:** 3 lines changed
- **MatchStageCompetitorDto:** 5 lines changed
- **MatchDto:** 2 lines changed

#### Models & Requests

- **AwardRequest:** 7 lines changed
- **AwardRequestForCSV:** 40 lines changed – updated constructors and JSON handling
- **ImageRequestForCsv:** 12 lines changed – updated constructors
- **ImageResponseHolder:** 3 lines changed
- **ClassificationRequest:** 4 lines changed
- **EnrolledRequest:** 6 lines changed
- **MemberRequest:** 10 lines changed
- **ScoreRequest:** 6 lines changed
- **Response:** 2 lines changed

#### Repositories

- **MatchCompetitorRepository:** 4 lines changed
- **MatchStageCompetitorRepository:** 4 lines changed

#### Service Interfaces

- **DomainService:** 37 lines changed
- **IpscService:** 2 lines changed
- **MatchCompetitorEntityService:** 24 lines changed – returns lists
- **MatchStageCompetitorEntityService:** 25 lines changed
- **TransactionService:** 8 lines changed
- **TransformationService:** 27 lines (new interface, replaces `IpscMatchService`)

#### Config & Controllers

- **ControllerAdvice:** 35 lines changed
- **AwardController:** 2 lines changed
- **ImageController:** 2 lines changed
- **IpscController:** 6 lines changed
- **ClubIdentifierConverter:** 4 lines changed – uses abbreviation

#### Package Moves

- `DtoMapping`, `DtoToEntityMapping`, `EntityMapping`: `ipsc/domain` → `ipsc/data`
- `MatchResultsDto`, `MatchResultsDtoHolder`: moved to `ipsc/holders/dto`
- `IpscRequestHolder`, `IpscResponseHolder`: moved to `ipsc/holders` sub-packages

### Fixed

- PCC Optics division constant value
- `ControllerAdvice` exception response handling
- `ClubIdentifierConverter` database persistence using abbreviation
- Unused firearm type assignment removed from match processing
- SAPSA number deduplication for competitor loading
- Division constants values in `IpscConstants`

### Removed

#### Services & Implementations

- `IpscMatchService` interface – replaced by `TransformationService`
- `IpscMatchServiceImpl` class – 867 lines replaced by `TransformationServiceImpl`

#### Records

- `MatchCompetitorRecord` – replaced by `CompetitorRecord`
- `IpscMatchRecordHolder` (from the records' package) – moved to holders

#### Configuration

- `qodana.yml` – duplicate removed; configuration consolidated in `qodana.yaml`

#### Test Classes

- `IpscMatchServiceTest` – 10,076 lines removed (service renamed to `TransformationService`)

---

## 🔄 Migration Guide

### For Developers

#### Service Layer Changes

1. **IpscMatchService renamed to TransformationService:**
    - `IpscMatchService` interface no longer exists; use `TransformationService` instead
    - `IpscMatchServiceImpl` replaced by `TransformationServiceImpl`
    - Update all injection points and usages accordingly
    - Update test classes referencing `IpscMatchService`

2. **MatchHolder for match data:**
    - Use `MatchHolder` for encapsulating match data in service calls
    - Replaces direct pass-through of match parameters

3. **MatchCompetitorEntityService returns lists:**
    - Methods that previously returned single values now return `List<>` types
    - Update all call sites to handle list results

#### Package Changes

1. **Mapping classes relocated:**
    - `DtoMapping`: `za.co.hpsc.web.models.ipsc.domain` → `za.co.hpsc.web.models.ipsc.data`
    - `DtoToEntityMapping`: same package move
    - `EntityMapping`: same package move
    - Update all import statements accordingly

2. **Holders restructured:**
    - `MatchResultsDto` and `MatchResultsDtoHolder`: update imports to `holders/dto`
    - `IpscRequestHolder` and `IpscResponseHolder`: update imports to new `holders` sub-packages

3. **Records restructured:**
    - `CompetitorMatchRecord` renamed to `CompetitorRecord` – update all usages
    - Use `CompetitorResultRecord` for result-specific data
    - Use `MatchCompetitorOverallResultsRecord` and `MatchCompetitorStageResultRecord` for result details

#### ClubIdentifier Changes

1. **Abbreviation field available:**
    - `ClubIdentifier.getAbbreviation()` now available for all club identifiers
    - Database persists abbreviation via updated `ClubIdentifierConverter`
    - No schema migration required

#### Test Updates

1. **Removed test classes:**
    - Remove any references to `IpscMatchServiceTest`
    - Replace with `TransformationServiceTest` for equivalent coverage

2. **New test classes are available:**
    - All new test classes follow the `testMethod_whenCondition_thenExpectedBehavior` naming convention
    - Integration tests available for Award, Image, and DtoToEntityMapping services

### For API Consumers

No breaking changes to public APIs. All changes are internal refactoring with backward-compatible interfaces.

---

## 📊 Statistics

- **Total Commits:** ~75
- **Files Changed:** 123
- **Insertions:** 12,713 lines
- **Deletions:** 13,358 lines
- **Net Change:** -645 lines
- **New Test Files:** 20+
- **Test Lines Added:** ~7,000
- **Service Files Modified:** 10
- **Entity Files Modified:** 6
- **New DTO/Record Classes:** 5

---

## 🧪 Testing

### Test Execution Summary

- **Unit Tests:** All passing
- **Integration Tests:** All passing
- **Coverage Areas:**
    - Controller endpoint validation
    - JPA attribute converter round-trips
    - Domain entity lifecycle and null handling
    - Exception hierarchy correctness
    - Competitor SAPSA deduplication logic
    - Award and image CSV integration pipelines
    - Transformation service match processing
    - Enrolled competitor DTO construction
    - ClubIdentifier abbreviation persistence

### Test Quality Metrics

- **Consistent naming:** 100% compliance with `testMethod_whenCondition_thenExpectedBehavior` standard
- **AAA pattern:** Applied throughout the test suite
- **Mock isolation:** Proper mock-based testing with Mockito
- **Edge case coverage:** Comprehensive null/empty/blank, partial/full data scenarios
- **Integration coverage:** End-to-end pipeline tests for CSV processing and CAB import

---

## 🔍 Technical Details

### Architectural Changes

#### Service Transformation

```
Before v5.4.0:
IpscController → IpscService → IpscMatchService → DomainService
                                                 → TransactionService

After v5.4.0:
IpscController → IpscService → TransformationService → DomainService
                                                      → TransactionService
```

#### Benefits

1. **Semantic Clarity:** `TransformationService` better describes its data-transformation role
2. **Improved Encapsulation:** `MatchHolder` encapsulates match data passing
3. **List-based Returns:** `MatchCompetitorEntityService` returns lists for bulk operations
4. **Consistent Patterns:** Aligned with domain service naming conventions

#### Competitor Deduplication

```
CompetitorDto construction
    ↓
SAPSA number validation (max number check via IpscUtil)
    ↓
Deduplication filter (by SAPSA number + ID)
    ↓
Unique competitor collection returned
```

#### Updated Package Structure

```
models/ipsc/
├── data/                    ← renamed from domain/
│   ├── DtoMapping
│   ├── DtoToEntityMapping
│   └── EntityMapping
├── dto/
│   ├── EnrolledCompetitorDto (new)
│   └── ...existing DTOs...
├── holders/
│   ├── data/
│   │   └── MatchHolder (new)
│   ├── dto/
│   │   ├── MatchResultsDto
│   │   └── MatchResultsDtoHolder
│   ├── records/
│   │   └── IpscMatchRecordHolder (new)
│   ├── request/
│   │   └── IpscRequestHolder
│   └── response/
│       └── IpscResponseHolder
└── records/
    ├── CompetitorRecord (renamed from CompetitorMatchRecord)
    ├── CompetitorResultRecord (new)
    ├── IpscMatchRecord
    ├── MatchCompetitorOverallResultsRecord (new)
    └── MatchCompetitorStageResultRecord (new)
```

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
- REST API endpoints for enrolled competitor management

---

## 👥 Contributors

Development Team

---

## 📝 Notes

This release represents the largest test expansion in the project's history, adding over twenty new
test classes and approximately seven thousand lines of new test code. The renaming of `IpscMatchService`
to `TransformationService` improves semantic clarity in the service architecture. The introduction of
`EnrolledCompetitorDto` and SAPSA number validation lays the foundation for full competitor enrolment
management. The package reorganisation (`domain` → `data`) and the records' restructuring align the model
hierarchy with its intended semantics.

The CI/CD improvements — Qodana JVM linting, JaCoCo code coverage, and refined code quality workflows
— establish a stronger quality gate for future development, ensuring ongoing code health and compliance.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history/)**