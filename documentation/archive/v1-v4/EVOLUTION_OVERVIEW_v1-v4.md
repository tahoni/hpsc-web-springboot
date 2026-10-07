# HPSC Website Backend Evolution Overview Archive (v1.0.0 – v4.1.0)

The Phase-by-phase narrative for Phases 1 to 7 (versions 1.0.0 to 4.1.0) of the HPSC Website Backend project, archived
from [`EVOLUTION_OVERVIEW.md`](/EVOLUTION_OVERVIEW.md) to keep that file a manageable size. The entries are moved
unchanged. See [`EVOLUTION_OVERVIEW.md`](/EVOLUTION_OVERVIEW.md) for Phase 44 (v10.0.0) onwards,
[`HISTORY_v1-v4.md`](/documentation/archive/v1-v4/HISTORY_v1-v4.md) for the archived timeline, milestones and
architectural evolution, [`CHANGELOG_v1-v4.md`](/documentation/archive/v1-v4/CHANGELOG_v1-v4.md) for the archived change
log and [`EVOLUTION_OVERVIEW_v5-v7.md`](/documentation/archive/v5-v7/EVOLUTION_OVERVIEW_v5-v7.md) for Phases 8 to 18.

---

## 📖 Evolution Overview

The HPSC Website Backend project evolved through these distinct phases up to v4.1.0, each addressing specific
architectural and feature requirements:

### Phase 1: Foundation (v1.0.0)

**Duration:** January 4, 2026 - January 4, 2026

The inaugural release established the core infrastructure for the HPSC platform with a focus on image gallery
functionality.

**Key Accomplishments:**

- Initial Spring Boot application bootstrap with modern tech stack
- CSV-based image data processing engine
- MIME type inference and flexible column mapping
- Robust error handling framework (custom exceptions)
- Initial API controllers and REST endpoints
- Comprehensive Javadoc documentation

**Architecture Highlights:**

- Controller → Service → Model → Repository pattern
- CSV processing pipeline with validation
- Custom exception hierarchy (ValidationException, FatalException, CsvReadException)
- Global exception handler (ApiControllerAdvice)

**Technical Focus:**

- Data parsing and transformation
- Error handling and validation
- API documentation and clarity

---

### Phase 2: Feature Expansion (v1.1.0 – v1.1.3)

**Duration:** January 14, 2026 – January 28, 2026

Rapid iteration adding award processing, improving code quality and establishing documentation standards.

**Key Accomplishments:**

**v1.1.0 – Award Processing Integration**

- Comprehensive award processing with CSV support
- New service layer pattern (`HpscAwardService`)
- Award ceremony grouping and structured responses
- Enhanced input validation across all models
- Base `Request` and `Response` classes for metadata standardisation
- Integration of OpenAPI (Swagger UI) for API documentation
- Extensive unit test coverage for new features

**v1.1.1 – API Clarity**

- Javadoc standardisation across codebase
- Improved parameter documentation
- Enhanced validation annotations
- Better IDE assistance through improved documentation

**v1.1.2 – Project Documentation**

- Creation of README.md (project overview and setup)
- Creation of ARCHITECTURE.md (detailed system design)
- Comprehensive onboarding materials

**v1.1.3 – Code Quality & Documentation**

- Central Division → DisciplinesInDivision mapper
- Introduction of `Division.NONE` enum value
- Expanded Javadoc coverage
- Improved utility class design (private constructors)
- Spring Boot security update (4.0.2)

**Architecture Highlights:**

- Formalised service layer pattern
- Introduction of generic request/response base classes
- Centralised error response handling
- OpenAPI integration for automatic documentation

**Technical Focus:**

- Code documentation and maintainability
- Project documentation and onboarding
- Code quality and style enforcement
- Framework integration (OpenAPI)

---

### Phase 3: Architectural Transformation (v2.0.0)

**Duration:** February 8, 2026

Major refactoring introducing service-oriented architecture and comprehensive DTO layer.

**Key Accomplishments:**

**Service Layer Revolution**

- Replaced monolithic `IpscService` with specialised services:
    - `WinMssService` - CAB file import and XML processing
    - `MatchResultService` - Core match result transformation
    - `TransactionService` - Transaction management
    - `IpscMatchService` - IPSC-specific match operations
    - Domain-specific services (Competitor, MatchCompetitor, MatchStage, MatchStageCompetitor)

**DTO Architecture Introduction**

- Comprehensive DTO layer (`MatchDto`, `MatchResultsDto`, `CompetitorDto`, `MatchStageDto`, `MatchStageCompetitorDto`,
  `MatchCompetitorDto`)
- Request/response unification (removed `-ForXml` variants)
- UUID-based mapping between requests and domain objects
- Improved separation of concerns

**Domain Model Evolution**

- Removed `Club` entity (replaced with `ClubReference` enum)
- Enhanced timestamps and scoring fields across entities
- Introduction of competitor categories
- `XmlDataWrapper` for generic XML processing

**Testing & Quality**

- Comprehensive test coverage for new services
- Edge case handling (null values, initialisation logic)
- Transactional behaviour testing

**Architecture Highlights:**

- Modular service architecture
- DTO pattern for data transfer
- Transaction management abstraction
- Specialised domain services

**Technical Focus:**

- Architectural modularity and testability
- Data transformation pipelines
- Service-oriented design patterns
- Transaction safety

---

### Phase 4: Domain Specialisation (v3.0.0)

**Duration:** February 10, 2026

Comprehensive domain model restructuring for IPSC compliance and firearm-type classification.

**Key Accomplishments:**

**Domain Model Restructuring**

- `Discipline` enum → `FirearmType` enum (Handgun, PCC, Rifle, Shotgun, Mini Rifle, .22 Handgun)
- Division mapper restructure: `DivisionToDisciplinesInDivisionMapper` → `FirearmTypeToDivisions`
- Reintroduction of `Club` entity with proper JPA relationships
- Competitor category field standardisation across all models
- Match entity firearm type classification

**IPSC Specialisation**

- Firearm-type-specific division mappings
- Enhanced `FirearmType` enum with division retrieval methods
- Firearm type inference in match helpers
- IPSC-compliant scoring and ranking structures

**Entity Enhancement**

- `Club` entity with bidirectional `@OneToMany` relationship to `Match`
- `ClubRepository` and `ClubService`/`ClubServiceImpl`
- Enhanced `Match` entity with firearm type and club reference
- `MatchStage` entity with `maxPoints` field

**Comprehensive Testing**

- New test classes: `FirearmTypeTest`, `FirearmTypeToDivisionsTest`, `ClubDtoTest`, `ClubReferenceTest`
- Updated test classes for the new domain structure
- Expanded test coverage for enum utilities

**Documentation Enhancement**

- Detailed Javadoc for all domain entities and DTOs
- README.md feature expansion
- ARCHITECTURE.md domain documentation
- Entity initialisation method documentation

**Architecture Highlights:**

- Firearm-type-based classification system
- Club entity relationship management
- IPSC-specific domain modelling
- Enhanced enum utility methods

**Technical Focus:**

- IPSC domain compliance
- Entity relationship design
- Firearm-type classification
- Comprehensive test coverage

---

### Phase 5: Quality Assurance & Simplification (v3.1.0)

**Duration:** February 10, 2026

Focus on exception handling consolidation and API documentation accuracy.

**Key Accomplishments:**

**Exception Handling Consolidation**

- Merged generic exception handlers in ControllerAdvice
- Unified `Exception` and `RuntimeException` handling
- Combined `IllegalArgumentException` and `MismatchedInputException` handlers
- Removed redundant `CsvReadException` handler
- Streamlined error response generation

**API Documentation Improvements**

- Added `@Operation` annotations for clarity
- Fixed request body schema references
- Improved exception propagation documentation
- Removed unnecessary try-catch patterns

**Bug Fixes**

- Fixed XML parsing null return issue
- Enhanced exception context preservation
- Aligned XML and JSON parsing error handling

**Code Quality**

- Simplified exception handling architecture
- Improved error response consistency
- Better alignment with API documentation

**Architecture Highlights:**

- Simplified exception handling chain
- Improved error propagation flow
- Better documented API contracts

**Technical Focus:**

- Exception handling simplification
- API documentation accuracy
- Error consistency

---

### Phase 6: Major IPSC Refactoring (v4.0.0)

**Duration:** February 11, 2026

Significant domain entity refactoring with comprehensive testing and improved validation.

**Key Accomplishments:**

**Domain Entity Refactoring**

- `Match` → `IpscMatch` entity rename
- `MatchStage` → `IpscMatchStage` entity rename
- `MatchRepository` → `IpscMatchRepository` repository rename
- Removed `MatchStageRepository` (consolidated into `IpscMatchStageRepository`)
- Updated all dependent classes across services, controllers, helpers and tests

**Enhanced Validation & Robustness**

- Multi-layered validation (controller, service, entity levels)
- `@NotNull` annotations on critical service methods
- Enhanced DTO validation throughout processing
- Improved null-safety in data transformation

**Match Processing Improvements**

- Refactored match result processing logic
- Introduced `MatchResultsDtoHolder` for DTO management
- Enhanced CAB file import with modular methods
- Improved transaction error recovery

**Comprehensive Testing**

- Created `IpscMatchServiceImplTest` (985 lines)
- Significantly expanded `WinMssServiceTest`
- Updated all test classes for entity renames
- Complete pipeline testing coverage

**Bug Fixes**

- Fixed XML parsing edge cases
- Resolved entity mapping issues
- Enhanced error recovery mechanisms

**Code Quality Improvements**

- Improved modularity and separation of concerns
- Enhanced code readability and maintainability
- Better encapsulation through helper classes
- Simplified complex method implementations

**Architecture Highlights:**

- Explicit IPSC domain naming
- Enhanced validation layers
- Comprehensive test coverage
- Improved error handling

**Technical Focus:**

- Domain clarity through entity naming
- Validation robustness
- Comprehensive test coverage
- Infrastructure stability

---

### Phase 7: CRUD Enhancement & API Maturity (v4.1.0)

**Duration:** February 13, 2026

Added complete CRUD capabilities for IPSC entities and supporting improvements.

**Key Accomplishments:**

**CRUD Operations**

- Full Create, Read, Update, Delete support for `IpscMatch`
- Full CRUD support for `IpscMatchStage`
- Repository interface implementations
- Service layer CRUD methods
- Transactional handling for all write operations

**API Maturity**

- CRUD endpoints for match and stage management
- Enhanced request validation for create/update operations
- Improved DTO validation and null-safety
- Request/response schema updates

**Enhanced Persistence**

- Transactional boundaries for data consistency
- Foreign key constraint management
- Cascade behaviour specification
- Entity initialisation logic reuse

**Testing Improvements**

- Unit tests for CRUD operations
- Integration tests for service behaviour
- Validation failure test cases
- Edge case coverage

**Documentation & Migration**

- CRUD operation documentation
- Database schema migration notes
- Repository/service migration guidance
- Test fixture requirements

**Architecture Highlights:**

- Complete CRUD lifecycle
- Transactional consistency
- Enhanced entity persistence patterns

**Technical Focus:**

- Complete data lifecycle management
- API maturity and completeness
- Entity persistence best practices

---
