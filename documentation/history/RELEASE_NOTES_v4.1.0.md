# Release Notes - Version 4.1.0

**Status:** ✨ Stable

---

## 🎯 Theme

CRUD Enhancement & API Maturity

---

## Overview

This release adds core CRUD (Create, Read, Update, Delete) capabilities and supporting improvements for the
refactored IPSC domain introduced in version 4.0.0.

---

## Key Features

### CRUD Operations for IPSC Entities

- **IpscMatch CRUD:** Complete create, read, update, delete support for IPSC match entities
- **IpscMatchStage CRUD:** Full CRUD support for match stage entities
- **Repository implementation:** New `IpscMatchRepository` and `IpscMatchStageRepository` with CRUD methods
- **Service layer:** Corresponding service methods for all CRUD operations
- **Transactional handling:** Transaction management for all write operations ensuring data integrity

### Enhanced Input Validation

- **DTO Validation:** Additional `@NotNull` annotations on critical DTO fields
- **Bean Validation:** Jakarta Validation annotations integrated throughout request/response DTOs
- **Error Messages:** Detailed validation error reporting for better API usability

### Testing Improvements

- **Unit Tests:** Added comprehensive unit tests for CRUD endpoints
- **Integration Tests:** Extended integration tests for service behavior validation
- **Error Cases:** Test coverage for validation failures and edge cases

---

## Breaking Changes

- **No new breaking changes:** Previous breaking changes from v4.0.0 (e.g., `Match` → `IpscMatch`) remain in
  effect
- **Full backward compatibility:** With v4.0.0 implementations

---

## Enhancements and Updates

- CRUD endpoints and service operations added for IPSC entities
- Improved request validation on create/update DTOs
- Transactional handling added for create/update/delete operations to ensure data integrity
- Reused existing domain initialisation logic (`init()` methods) for entity creation
- Enhanced DTO validation and null-safety for CRUD flows
- Added unit and integration tests covering CRUD endpoints and service behaviour
- API docs updated to include CRUD operations and request/response schemas

---

## Bug Fixes

- Fixed edge cases in entity initialisation when creating stages with missing `maxPoints`
- Resolved mapping issues between DTOs and domain entities during updates

---

## API Changes

- Request/response DTOs updated to include necessary fields for create/update flows

---

## Migration Notes

- **Database schema:** Ensure tables `ipsc_match` and `ipsc_match_stage` are present
- **Foreign-key constraints:** Review cascade behavior for delete operations to avoid accidental data loss
- **Repositories/services:** Continue using new repository interfaces from v4.0.0
- **Test fixtures:** Integration tests may require test data fixtures for create/update flows

---

## 📦 Dependencies

### Unchanged from v4.0.0

- **Spring Boot:** 4.0.3
- **Java:** 25
- **All other dependencies:** Remain the same

---

## 🧪 Testing Summary

### New Tests Added

- CRUD operation unit tests
- CRUD endpoint integration tests
- Validation failure scenarios
- Edge case coverage

### Test Coverage

- Extended `IpscMatchServiceImplTest` with CRUD scenarios
- Added `IpscMatchStageServiceTest` coverage
- Enhanced error handling tests

---

## 👥 Credits

@tahoni

---

**Release Date:** February 13, 2026  
**Status:** Stable  
**Previous Version:** 4.0.0  
**Next Version:** 5.0.0

