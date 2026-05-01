# Release Notes – Version 6.0.0

**Release Date:** May 1, 2026  
**Status:** ✨ Stable

---

## 🎯 Theme

**Dedicated Match CRUD API, Service Encapsulation & Package Restructuring**

Version 6.0.0 introduces a dedicated IPSC match management REST API at `/v2/ipsc/matches`, backed by the
new `IpscMatchService` and a full set of match-specific request/response models. `DomainServiceImpl` is
decoupled from JPA repositories and now delegates entirely to entity services, improving testability and
layering. All IPSC model classes are reorganised under a `common` sub-package with a dedicated
`match` sub-package for match-only models. Three new match search request types are introduced for
future query endpoints, `IpscUtil` lands as a dedicated string-formatting utility, and Spring Boot is
upgraded to 4.0.6. The release is supported by eight new test classes covering the new controller,
service, DTOs, and utility.

---

## ⭐ Key Highlights

### 🆕 Dedicated Match CRUD API

- **`IpscMatchController`** — new controller mapped to `/v2/ipsc/matches` (134 lines)
    - `POST /v2/ipsc/matches` — create a new IPSC match
    - `PUT /v2/ipsc/matches/{matchId}` — fully replace an existing match
    - `PATCH /v2/ipsc/matches/{matchId}` — partially update an existing match
    - `GET /v2/ipsc/matches/{matchId}` — retrieve a match by ID
    - Full OpenAPI / Swagger annotations on all four operations
    - Returns `ResponseEntity<MatchOnlyResponse>` with typed error responses
- **`IpscMemberController`** — stub controller registered at `/ipsc/member` for upcoming member management

### 🏗️ IpscMatchService Layer

- **`IpscMatchService` interface** (22 lines) — dedicated service contract for match operations:
    - `insertMatch(MatchOnlyRequest)` → `Optional<MatchOnlyResponse>`
    - `updateMatch(Long matchId, MatchOnlyRequest)` → `Optional<MatchOnlyResponse>`
    - `modifyMatch(Long matchId, MatchOnlyRequest)` → `Optional<MatchOnlyResponse>`
    - `getMatch(Long matchId)` → `Optional<MatchOnlyResponse>`
- **`IpscMatchServiceImpl`** (135 lines) — full implementation backed by `DomainService` and
  `TransactionService`

### 📦 Match-Specific Model Layer

- **`MatchOnlyDto`** (82 lines) — lightweight match DTO without stages; initialised from
  `MatchOnlyRequest` with automatic firearm-type resolution and timestamp stamping
- **`MatchOnlyRequest`** (49 lines) — JSON request body for match create / update operations
- **`MatchOnlyResponse`** (83 lines) — response envelope for match operations
- **`MatchOnlyResultsDto`** (18 lines) — internal holder passed through the service chain

### 🔍 Match Search Request Models

Three new request types for future query/search endpoints:
- **`MatchSearchRequest`** — multi-criteria: match IDs array, name, date range
- **`MatchSearchDateRequest`** — date-range search: `startDate`, `endDate`, `matchName`
- **`MatchSearchIdRequest`** — ID-array-based lookup

### 🔧 DomainServiceImpl — Repository Decoupling

`DomainServiceImpl` no longer injects JPA repositories directly. All data access is now routed through
entity services, completing the intended layering:

```
Before v6.0.0:
DomainServiceImpl → ClubRepository
                 → CompetitorRepository
                 → IpscMatchRepository
                 → IpscMatchStageRepository
                 → MatchCompetitorRepository
                 → MatchStageCompetitorRepository

After v6.0.0:
DomainServiceImpl → ClubEntityService
                 → CompetitorEntityService
                 → MatchEntityService
                 → MatchStageEntityService
                 → MatchCompetitorEntityService
                 → MatchStageCompetitorEntityService
```

New entity service methods that were added to support the refactor:

- `ClubEntityService.findClubById(Long)` / `ClubEntityServiceImpl` implementation
- `CompetitorEntityService.findCompetitorById(Long)` / implementation
- `MatchStageCompetitorEntityService.findMatchStageCompetitorById(Long)` / implementation

### 📂 IPSC Model Package Restructuring

All IPSC model classes previously at `models/ipsc/` are now grouped under `models/ipsc/common/`,
with a new sibling sub-package `models/ipsc/match/` for match-only models:

```
models/ipsc/
├── common/
│   ├── data/            (DtoMapping, DtoToEntityMapping, EntityMapping)
│   ├── divisions/       (all firearm-type division classes)
│   ├── dto/             (ClubDto, CompetitorDto, MatchDto, MatchStageDto, …)
│   ├── holders/
│   │   ├── data/        (MatchHolder)
│   │   ├── dto/         (MatchResultsDto, MatchResultsDtoHolder)
│   │   ├── records/     (IpscMatchRecordHolder)
│   │   ├── request/     (IpscRequestHolder)
│   │   └── response/    (IpscResponseHolder)
│   ├── records/         (CompetitorRecord, CompetitorResultRecord, …)
│   ├── request/         (IpscRequest, MatchRequest, MatchSearchRequest, …)
│   └── response/        (ClubResponse, EnrolledResponse, IpscResponse, MatchResponse, …)
└── match/
    ├── dto/             (MatchOnlyDto)
    ├── holders/dto/     (MatchOnlyResultsDto)
    ├── request/         (MatchOnlyRequest)
    └── response/        (MatchOnlyResponse)
```

### 🛠️ IpscUtil — Match & Club String Formatting

New utility class `IpscUtil` (66 lines) centralises display-string construction:
- `clubTostring(name, abbreviation)` — concatenates name + `(abbreviation)`, collapses duplicates
- `matchToString(name, clubName, abbreviation)` — builds `"Match Name @ Club (ABBR)"` format
- `matchToString(name, ClubDto)` — convenience overload for `ClubDto`-based callers

### 🔄 TransformationService Updates

- `mapMatchResults` no longer declares `throws ValidationException` — exception converted to a
  runtime path internally
- New `mapMatchOnly(MatchOnlyRequest)` method added to support the match CRUD pipeline

### 🪵 Enhanced Logging & Error Handling

- **`ControllerAdvice`** (119 lines changed) — structured logging added to all exception handlers;
  `ValidationException` removed from method signatures
- **`logback-spring.xml`** — additional appender/logger configuration added

### ⬆️ Spring Boot 4.0.6 Upgrade

- `spring-boot-dependencies` BOM bumped from `4.0.5` to `4.0.6`
- Lombok exclusion plugin configuration reorganised under the Spring Boot plugin block

### 📜 pom.xml Metadata

- MIT License declaration added (`<license>`)
- Developer profile populated (`<developer>: tahoni / Leoni Lubbinge`)
- SCM connection and URL filled in for GitHub

### 🧪 Test Coverage Expansion

Eight new test classes were added; and one removed.

#### New Test Classes

| Class                      | Lines | Scope                                         |
|----------------------------|-------|-----------------------------------------------|
| `IpscMatchControllerTest`  | 49    | Unit — controller layer                       |
| `IpscMatchServiceTest`     | 269   | Unit — service layer                          |
| `IpscMatchIntegrationTest` | 237   | Integration — H2 in-memory                    |
| `MatchOnlyDtoTest`         | 202   | Unit — DTO init and field mapping             |
| `MatchOnlyRequestTest`     | 234   | Unit — request constructor and field mapping  |
| `MatchOnlyResponseTest`    | 165   | Unit — response constructor and field mapping |
| `MatchResponseTest`        | 46    | Unit — common response model                  |
| `IpscUtilTest`             | 114   | Unit — string formatting utility              |

#### Updated Test Classes

- **`TransformationServiceTest`** — +747 lines covering `mapMatchOnly` and updated signatures
- **`DomainServiceTest`** — +247 lines covering entity-service delegation
- **`TransactionServiceTest`** — +246 lines covering `findMatchStageCompetitorById` path
- **`ValueUtilTest`** — +294 lines covering null-handling improvements
- **`IpscServiceIntegrationTest`** — +99 lines for expanded integration scenarios
- Domain entity and DTO tests updated for `common` package path changes

#### Removed Test Classes

- **`IpscControllerTest`** (156 lines) — match operations moved to `IpscMatchController`; covered by
  `IpscMatchControllerTest`

---

## 📦 What's New

### Added

#### Controllers

- `IpscMatchController` — `/v2/ipsc/matches` CRUD API (POST, PUT, PATCH, GET)
- `IpscMemberController` — `/ipsc/member` stub (placeholder for member management)

#### Services

- `IpscMatchService` interface — match CRUD contract
- `IpscMatchServiceImpl` — match CRUD implementation

#### Entity Service Methods

- `ClubEntityService.findClubById(Long)`
- `CompetitorEntityService.findCompetitorById(Long)`
- `MatchStageCompetitorEntityService.findMatchStageCompetitorById(Long)`

#### Models

- `models/ipsc/match/dto/MatchOnlyDto`
- `models/ipsc/match/request/MatchOnlyRequest`
- `models/ipsc/match/response/MatchOnlyResponse`
- `models/ipsc/match/holders/dto/MatchOnlyResultsDto`
- `models/ipsc/common/request/MatchSearchRequest`
- `models/ipsc/common/request/MatchSearchDateRequest`
- `models/ipsc/common/request/MatchSearchIdRequest`

#### Utilities

- `IpscUtil` — club and match display-string formatting

### Changed

#### Package Paths (breaking for internal code only)

- All `models/ipsc/` classes moved to `models/ipsc/common/` — update all import statements
- `ClubResponse`, `MatchResponse`: `models/ipsc/response/` → `models/ipsc/common/response/`

#### Services

- `DomainServiceImpl` — replaced direct repository injection with entity service injection
- `TransformationServiceImpl` — imports updated to `models/ipsc/common/*`; `mapMatchOnly` added
- `TransformationService.mapMatchResults` — `throws ValidationException` removed from signature
- `IpscController` — match-related endpoints extracted to `IpscMatchController` (78 lines removed)

#### Config & Infrastructure

- `ControllerAdvice` — logging added; `ValidationException` removed from handler signatures
- `pom.xml` — Spring Boot 4.0.5 → 4.0.6; license, developer, SCM metadata populated
- `logback-spring.xml` — additional logger configuration

### Removed

#### Source

- Direct repository injection from `DomainServiceImpl`
- Old `models/ipsc/response/ClubResponse` (superseded by `common/response/ClubResponse`)
- Old `models/ipsc/response/MatchResponse` (superseded by `common/response/MatchResponse`)

#### Tests

- `IpscControllerTest` — replaced by `IpscMatchControllerTest`

---

## 🔄 Migration Guide

### For Developers

#### Import Path Changes

All IPSC model imports must be updated from `models/ipsc.*` to `models/ipsc.common.*`:

```java
// Before
import za.co.hpsc.web.models.ipsc.dto.*;
import za.co.hpsc.web.models.ipsc.request.*;
import za.co.hpsc.web.models.ipsc.response.*;
import za.co.hpsc.web.models.ipsc.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.records.*;

// After
import za.co.hpsc.web.models.ipsc.common.dto.*;
import za.co.hpsc.web.models.ipsc.common.request.*;
import za.co.hpsc.web.models.ipsc.common.response.*;
import za.co.hpsc.web.models.ipsc.common.holders.data.MatchHolder;
import za.co.hpsc.web.models.ipsc.common.records.*;
```

Match-specific models are under the new `match` sub-package:

```java
import za.co.hpsc.web.models.ipsc.match.dto.MatchOnlyDto;
import za.co.hpsc.web.models.ipsc.match.request.MatchOnlyRequest;
import za.co.hpsc.web.models.ipsc.match.response.MatchOnlyResponse;
```

#### DomainServiceImpl

If you extend or test `DomainServiceImpl` directly, update constructor injection — the six repository
parameters are replaced by the six entity service parameters in the same order.

#### TransformationService

Remove any `catch (ValidationException e)` blocks around `mapMatchResults` calls — the method no
longer declares that checked exception.

### For API Consumers

No breaking changes to existing public API endpoints. All prior endpoints under `/v1/ipsc/` remain
unchanged. The new match CRUD endpoints are additive at `/v2/ipsc/matches`.

---

## 📊 Statistics

- **Total Commits:** 40
- **Files Changed:** 165
- **Insertions:** 6,779 lines
- **Deletions:** 3,501 lines
- **Net Change:** +3,278 lines
- **New Source Files:** 15
- **New Test Files:** 8
- **Deleted Test Files:** 1

---

## 🧪 Testing

### Test Execution Summary

- **Unit Tests:** All passing
- **Integration Tests:** All passing
- **Coverage Areas:**
    - `IpscMatchController` endpoint routing and response mapping
    - `IpscMatchService` insert / update / modify / get paths
    - `IpscMatchIntegrationTest` end-to-end match persistence via H2
    - `MatchOnlyDto` initialisation from request and entity
    - `MatchOnlyRequest` / `MatchOnlyResponse` constructor and field coverage
    - `IpscUtil` club and match string formatting edge cases
    - `DomainService` entity-service delegation
    - `TransactionService` `findMatchStageCompetitorById` path
    - Null handling in `ValueUtil` expansions
    - `TransformationService.mapMatchOnly` behaviour

---

## 🐛 Known Issues

None reported.

---

## 🔮 Future Enhancements

- Implement match search endpoints using `MatchSearchRequest` / `MatchSearchDateRequest` /
  `MatchSearchIdRequest`
- Full `IpscMemberController` implementation for member CRUD
- Javadoc coverage for `IpscMatchController`, `IpscMatchService`, `IpscUtil`, and `MatchOnlyDto`
- Performance optimisation for large match imports
- Enhanced error messaging for failed match operations

---

## 👥 Contributors

Development Team

---

## 📝 Notes

Version 6.0.0 marks a significant milestone in the layering of the HPSC backend: `DomainServiceImpl`
no longer reaches past the entity service boundary into JPA repositories, enforcing the architecture
described in CLAUDE.md. The introduction of `IpscMatchController` at `/v2/ipsc/matches` establishes a
versioned, resource-oriented match API separate from the bulk-import flow in `IpscController`. The IPSC
model package restructuring (`ipsc/` → `ipsc/common/` + `ipsc/match/`) provides a clear home for both
shared and match-specific models as the domain grows. Together, these changes prepare the codebase for
upcoming member management and match search features.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**