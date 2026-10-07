# HPSC Website Backend Change Log Archive (v1.0.0 – v4.1.0)

The change log entries for versions 1.0.0 to 4.1.0 of the HPSC Website Backend project, archived from
[`CHANGELOG.md`](/CHANGELOG.md) to keep that file a manageable size. The entries are moved unchanged. See
[`CHANGELOG.md`](/CHANGELOG.md) for the current version, the unreleased changes and versions 10.0.0 onwards,
[`HISTORY_v1-v4.md`](/documentation/archive/v1-v4/HISTORY_v1-v4.md) for the archived narrative history of the same
versions, and [`CHANGELOG_v5-v7.md`](/documentation/archive/v5-v7/CHANGELOG_v5-v7.md) for versions 5.0.0 to 7.4.1.

---

### Table of Contents

- [🧾 Version 4.1.0](#-410---2026-02-13)
- [🧾 Version 4.0.0](#-400---2026-02-11)
- [🧾 Version 3.1.0](#-310---2026-02-10)
- [🧾 Version 3.0.0](#-300---2026-02-10)
- [🧾 Version 2.0.0](#-200---2026-02-08)
- [🧾 Version 1.1.3](#-113---2026-01-28)
- [🧾 Version 1.1.2](#-112---2026-01-20)
- [🧾 Version 1.1.1](#-111---2026-01-16)
- [🧾 Version 1.1.0](#-110---2026-01-14)
- [🧾 Version 1.0.0](#-100---2026-01-04)

---

### 🧾 [4.1.0] - 2026-02-13

#### ➕ Added

##### CRUD Operations for IPSC Entities

- **`IpscMatchRepository`** - Create, Read, Update, Delete operations for IPSC match entities
- **`IpscMatchStageRepository`** - CRUD support for match stage entities
- **Service layer CRUD:** Implemented corresponding service methods for all CRUD operations
- **Transactional handling:** Transaction management for all write operations

##### Enhanced Input Validation

- **DTO Validation:** Additional `@NotNull` annotations on critical DTO fields
- **Bean Validation:** Jakarta Validation annotations integrated throughout request/response DTOs
- **Error Messages:** Detailed validation error reporting

##### Testing Improvements

- **Unit Tests:** Added comprehensive unit tests for CRUD endpoints
- **Integration Tests:** Extended integration tests for service behaviour
- Test coverage for validation failures and edge cases

#### 🔄 Changed

- Improved request validation on create/update DTOs
- Enhanced repository query methods with additional filtering options
- Refined service layer contracts for better API consistency

#### 🐛 Fixed

- Edge cases in entity initialisation when creating stages with missing `maxPoints`
- Mapping issues between DTOs and domain entities during updates

---

### 🧾 [4.0.0] - 2026-02-11

#### ➕ Added

##### Major IPSC Domain Refactoring

- **Entity Renames:** `Match` → `IpscMatch`, `MatchStage` → `IpscMatchStage`
- **Repository Updates:** New `IpscMatchRepository` and `IpscMatchStageRepository` interfaces
- **Enhanced Type Safety:** Improved domain model clarity through explicit entity naming

##### Improved Input Validation

- **Multi-layered Validation:** Validation at controller, service and entity levels
- **Error Mapping:** Comprehensive error response generation with detailed messages

##### Exception Handling Improvements

- **Global Exception Handler:** Centralised exception handling for consistent error responses
- **Custom Exceptions:** Domain-specific exception types for clearer error semantics

##### Comprehensive Testing

- **Unit Test Coverage:** Extensive test coverage for service implementations
- **Integration Testing:** Full pipeline testing from controller through persistence layer
- **Bug Fixes:** Tests added to prevent regression of known issues

##### XML Parsing Bug Fixes

- Fixed edge cases in XML parsing logic
- Improved handling of malformed XML structures
- Enhanced validation of parsed XML data

#### 🔄 Changed

##### Breaking Changes

- **Entity Renaming:** Consumers must update references from `Match` to `IpscMatch`
- **Repository Interface Changes:** Update injection points to use `IpscMatchRepository` and `IpscMatchStageRepository`
- **Service Method Names:** Some service method signatures updated for consistency

##### Database

- **Schema Updates:** Reflected entity renames in JPA configuration
- **Migration Path:** Existing data remains compatible; no data loss during migration

#### ⚠️ Deprecated

- Old `MatchRepository` interface (replaced by `IpscMatchRepository`)
- Old service method signatures (superseded by refactored versions)

---

### 🧾 [3.1.0] - 2026-02-10

#### ➕ Added

- Enhancement to IPSC data processing pipeline
- Improved error handling for specific match processing scenarios

#### 🔄 Changed

- Refactored some internal service implementations
- Updated repository query methods

---

### 🧾 [3.0.0] - 2026-02-10

#### ➕ Added

- Major feature release for IPSC integration
- Enhanced data processing capabilities

#### 🔄 Changed

- Significant internal restructuring

---

### 🧾 [2.0.0] - 2026-02-08

#### ➕ Added

- Major refactoring of core services
- New repository patterns

#### 🔄 Changed

- Restructured service layer

---

### 🧾 [1.1.3] - 2026-01-28

#### 🐛 Fixed

- Bug fixes and stability improvements

---

### 🧾 [1.1.2] - 2026-01-20

#### ➕ Added

- Minor feature enhancements

---

### 🧾 [1.1.1] - 2026-01-16

#### 🐛 Fixed

- Specific bug fixes

---

### 🧾 [1.1.0] - 2026-01-14

#### ➕ Added

- New functionality and improvements

---

### 🧾 [1.0.0] - 2026-01-04

#### ➕ Added

- Initial release of HPSC Website Backend
- Core REST API for match management
- Basic IPSC integration
- Competitor and club management
- Image gallery support
- Award ceremony management
