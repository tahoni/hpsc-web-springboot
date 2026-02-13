# HPSC Website Backend

## Release Notes

### Version 4.1.0 - _2026-02-13_

# HPSC Website Backend

## Release Notes

This release adds core CRUD capabilities and supporting improvements for the refactored IPSC domain
introduced in `4.0.0`.

#### Breaking Changes

- No new major-breaking entity renames in this merge; previous breaking changes from `4.0.0`
  (e.g. `Match` →`IpscMatch`) remain in effect.
- Consumers referencing old repository or service class names should continue to follow
  migration notes from `4.0.0`.

#### Enhancements and Updates

- CRUD endpoints and service operations added for IPSC entities:
    - Added create/read/update/delete support for `IpscMatch` and `IpscMatchStage` via service layer.
    - Implemented corresponding repository interfaces: `IpscMatchRepository` and `IpscMatchStageRepository`.
- Controller/API improvements:
    - Improved request validation on create/update DTOs.
- Service & persistence:
    - Transactional handling added for create/update/delete operations to ensure data integrity.
    - Reused existing domain initialisation logic (`init()` methods) for entity creation.
- Input validation:
    - Enhanced DTO validation and null-safety for CRUD flows (additional `@NotNull` and bean validation
      annotations).
- Tests:
    - Added unit and integration tests covering CRUD endpoints and service behaviour (create/update/delete
      success and error cases).
- Documentation:
    - API docs updated to include CRUD operations and request/response schemas.

#### Bug Fixes

- Fixed edge cases in entity initialisation when creating stages with missing `maxPoints`.
- Resolved mapping issues between DTOs and domain entities during updates.

#### API Changes

- Request/response DTOs updated to include necessary fields for create/update flows.

#### Testing and Quality

- New tests added for CRUD operations and validation error handling.
- Existing `IpscMatchServiceImplTest` and `WinMssServiceTest` extended to include CRUD integration scenarios.

#### Migration Notes

- Database schema:
    - If using JPA auto-DDL, ensure tables `ipsc_match` and `ipsc_match_stage` are present and migrations
      include any new columns used by CRUD operations.
    - Review foreign-key constraints and cascade behaviour for delete operations to avoid accidental data
      loss.
- Repositories/services:
    - Continue to use `IpscMatchRepository` / `IpscMatchStageRepository` as the source-of-truth repository
      interfaces.
- Tests/config:
    - Integration tests may require test data fixtures for create/update flows added in this release.

#### Changes by

@tahoni
