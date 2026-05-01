# HPSC Website Backend Architecture

This document describes the architectural design, directory structure, and core concepts
of the Hartbeespoortdam Practical Shooting Club (HPSC) Spring Boot backend.

## 📑 Table of Contents

- [⚙️ Technology Stack](#-technology-stack)
- [📁 Project Structure](#-project-structure)
- [🎯 System Overview](#-system-overview)
- [🏗️ Layered Architecture](#-layered-architecture)
    - [📊 Presentation Layer](#-1-presentation-layer-zacohpscwebcontrollers)
    - [⚡ Service Layer](#-2-service-layer-zacohpscwebservices)
    - [🗄️ Persistence Layer](#-3-persistence-layer-zacohpscwebrepositories--zacohpscwebdomain)
    - [📦 Model Layer](#-4-model-layer-zacohpscwebmodels)
    - [🔧 Support Layers](#-5-support-layers)
    - [🛡️ Exception and Error Handling](#-6-exception-and-error-handling-zacohpscwebexceptions)
- [🔄 Key Design Patterns](#-key-design-patterns)
- [🔀 Data Flow](#-data-flow)
    - [📈 Typical Request-Response Flow](#-typical-request-response-flow)
    - [📥 Data Import Flow (CSV/XML)](#-data-import-flow-csvxml-processing)
    - [🆕 Match CRUD Flow](#-match-crud-flow)
- [✅ Quality Attributes](#-quality-attributes)
- [🔬 CI/CD & Quality Gates](#-cicd--quality-gates)
- [📚 Development Guidelines](#-development-guidelines)

---

## ⚙️ Technology Stack

| Component         | Technology                                                                |
|-------------------|---------------------------------------------------------------------------|
| Framework         | Spring Boot 4.0.6                                                         |
| Language          | Java 25                                                                   |
| Build             | Maven 3.9+                                                                |
| Database (prod)   | MySQL (env vars `MYSQL_USER` / `MYSQL_PASSWORD`)                          |
| Database (test)   | H2 in-memory (`create-drop`, profile `test`)                              |
| ORM               | Spring Data JPA, Hibernate 7.2                                            |
| Data processing   | Jackson (JSON/CSV/XML), Apache Commons Lang3                              |
| API documentation | SpringDoc OpenAPI 2.8.5 (Swagger UI at `/hpsc-web/swagger-ui/index.html`) |
| Validation        | Hibernate Validator, Jakarta Validation                                   |
| Testing           | JUnit 5, Mockito, Spring Test                                             |
| Code coverage     | JaCoCo 0.8.14 (Maven `coverage` profile)                                  |
| Static analysis   | Qodana JVM (`jetbrains/qodana-jvm`)                                       |
| Code generation   | Lombok                                                                    |
| Port / context    | `8081` / `/hpsc-web`                                                      |

---

## 📁 Project Structure

```text
├───.github/
│   └───workflows/              # GitHub Actions — CI/CD, CodeQL, Qodana
├───.mvn/wrapper/               # Maven wrapper
├───documentation/
│   ├───archive/                # Legacy release archive
│   └───history/                # Per-version release notes, README, history
├───src/
│   ├───main/java/za/co/hpsc/web/
│   │   ├───configs/            # Spring configuration (ControllerAdvice, OpenAPI)
│   │   ├───constants/          # Application-wide constants
│   │   │                           HpscConstants, IpscConstants, MatchConstants, SystemConstants
│   │   ├───controllers/        # REST controllers
│   │   │                           AwardController, ImageController
│   │   │                           IpscController          (/v1/ipsc/*)
│   │   │                           IpscMatchController     (/v2/ipsc/matches)
│   │   │                           IpscMemberController    (/ipsc/member — stub)
│   │   ├───converters/         # Custom JPA AttributeConverters for all enum fields
│   │   │                           ClubIdentifierConverter, CompetitorCategoryConverter
│   │   │                           DivisionConverter, FirearmTypeConverter
│   │   │                           MatchCategoryConverter, PowerFactorConverter
│   │   ├───domain/             # JPA entities (database tables)
│   │   │                           Club, Competitor, IpscMatch, IpscMatchStage
│   │   │                           MatchCompetitor, MatchStageCompetitor
│   │   ├───enums/              # Domain enumerations
│   │   │                           ClubIdentifier, CompetitorCategory, Division
│   │   │                           FirearmType, MatchCategory, PowerFactor
│   │   ├───exceptions/         # Custom exception hierarchy + ControllerAdvice mapping
│   │   ├───models/             # DTOs, request/response models, holders, records
│   │   │   ├───award/          # Award request/response models
│   │   │   ├───image/          # Image gallery request/response models
│   │   │   ├───ipsc/
│   │   │   │   ├───common/     # Shared IPSC models (all non-match-specific)
│   │   │   │   │   ├───data/       DtoMapping, DtoToEntityMapping, EntityMapping
│   │   │   │   │   ├───divisions/  FirearmType-to-division mapping classes
│   │   │   │   │   ├───dto/        ClubDto, CompetitorDto, MatchDto, MatchStageDto, …
│   │   │   │   │   ├───holders/    MatchHolder, MatchResultsDto, IpscRequestHolder, …
│   │   │   │   │   ├───records/    CompetitorRecord, CompetitorResultRecord, …
│   │   │   │   │   ├───request/    IpscRequest, MatchRequest, MatchSearchRequest, …
│   │   │   │   │   └───response/   ClubResponse, EnrolledResponse, IpscResponse, …
│   │   │   │   └───match/      # Match-only (CRUD) models
│   │   │   │       ├───dto/        MatchOnlyDto
│   │   │   │       ├───holders/dto/ MatchOnlyResultsDto
│   │   │   │       ├───request/    MatchOnlyRequest
│   │   │   │       └───response/   MatchOnlyResponse
│   │   │   └───shared/         # Base Request / Response wrappers, ControllerResponse
│   │   ├───repositories/       # Spring Data JPA interfaces
│   │   ├───services/           # Service interfaces
│   │   │   └───impl/           # Service implementations
│   │   └───utils/              # Utility classes
│   │                               DateUtil, IpscUtil, NumberUtil, StringUtil, ValueUtil
│   └───main/resources/
│       ├───logback-spring.xml  # Logging configuration
│       └───application*.properties
└───src/test/java/za/co/hpsc/web/
    ├───configs/                # ControllerAdvice tests
    ├───controllers/            # Controller unit tests (Mockito, no Spring context)
    ├───domain/                 # Entity unit tests
    ├───enums/                  # Enum unit tests
    ├───models/                 # DTO / model unit tests
    ├───services/               # Service integration tests (H2)
    │   └───impl/               # Service unit tests (Mockito)
    └───utils/                  # Utility unit tests
```

---

## 🎯 System Overview

The HPSC Website Backend is a pure REST API server (no frontend) that manages practical shooting
club operations. Core responsibilities:

| Domain                  | Description                                                                      |
|-------------------------|----------------------------------------------------------------------------------|
| **Match Management**    | Bulk import from WinMSS (CAB/XML) and IPSC formats; CRUD via versioned `/v2` API |
| **Competitor Tracking** | Profiles, classifications, SAPSA number validation, enrolment                    |
| **Award Ceremonies**    | Award data and ceremony grouping                                                 |
| **Image Gallery**       | Image metadata processing from CSV                                               |
| **IPSC Integration**    | IPSC data standards, division-to-firearm-type classification                     |

The application follows a strict **N-Tier Layered Architecture** with unidirectional dependencies:

```
HTTP Request
    → Controller
        → Service
            → Entity Service  (gateway to the repository layer)
                → Repository
                    → Database
```

`DomainServiceImpl` and `TransactionService` operate exclusively through entity services and never
inject JPA repositories directly, enforcing the layering boundary.

---

## 🏗️ Layered Architecture

### 📊 1. Presentation Layer (`za.co.hpsc.web.controllers`)

Handles incoming HTTP requests. Does not contain business logic.

| Controller             | Mapping                     | Responsibility                        |
|------------------------|-----------------------------|---------------------------------------|
| `AwardController`      | `/hpsc-web/awards`          | Award CSV processing                  |
| `ImageController`      | `/hpsc-web/images`          | Image CSV processing                  |
| `IpscController`       | `/hpsc-web/v1/ipsc`         | Bulk WinMSS CAB import                |
| `IpscMatchController`  | `/hpsc-web/v2/ipsc/matches` | Match CRUD (POST / PUT / PATCH / GET) |
| `IpscMemberController` | `/hpsc-web/ipsc/member`     | Member management (stub)              |

All controllers:
- Validate request bodies and path variables at the boundary
- Delegate all processing to the service layer
- Return `ResponseEntity<T>` with typed response models
- Are annotated with full OpenAPI (`@Tag`, `@Operation`, `@ApiResponse`) metadata

`ControllerAdvice` in `za.co.hpsc.web.configs` catches all `FatalException`, `NonFatalException`, and
`ValidationException` instances and maps them to standard JSON error responses with structured logging.

---

### ⚡ 2. Service Layer (`za.co.hpsc.web.services`)

Contains all business logic. The service layer is divided into two tiers:

#### Domain / Orchestrating Services

| Interface               | Implementation              | Role                                                                |
|-------------------------|-----------------------------|---------------------------------------------------------------------|
| `IpscService`           | `IpscServiceImpl`           | Entry point for bulk WinMSS CAB import                              |
| `TransformationService` | `TransformationServiceImpl` | Transforms parsed CSV/XML data into IPSC response structures        |
| `DomainService`         | `DomainServiceImpl`         | Orchestrates entity initialisation (club, competitor, match, stage) |
| `TransactionService`    | `TransactionServiceImpl`    | Coordinates persistence across multiple entity services             |
| `IpscMatchService`      | `IpscMatchServiceImpl`      | Match CRUD operations (insert / update / modify / get)              |
| `AwardService`          | `AwardServiceImpl`          | Award CSV processing                                                |
| `ImageService`          | `ImageServiceImpl`          | Image CSV processing                                                |

#### Entity Services (Repository Gateway)

Entity services are the **only** layer that interacts with Spring Data JPA repositories. Higher-level
services (including `DomainServiceImpl` and `TransactionServiceImpl`) always go through entity services.

| Interface                           | Implementation                          | Managed Entity         |
|-------------------------------------|-----------------------------------------|------------------------|
| `ClubEntityService`                 | `ClubEntityServiceImpl`                 | `Club`                 |
| `CompetitorEntityService`           | `CompetitorEntityServiceImpl`           | `Competitor`           |
| `MatchEntityService`                | `MatchEntityServiceImpl`                | `IpscMatch`            |
| `MatchStageEntityService`           | `MatchStageEntityServiceImpl`           | `IpscMatchStage`       |
| `MatchCompetitorEntityService`      | `MatchCompetitorEntityServiceImpl`      | `MatchCompetitor`      |
| `MatchStageCompetitorEntityService` | `MatchStageCompetitorEntityServiceImpl` | `MatchStageCompetitor` |

All `@Transactional` boundaries live in the service layer.

---

### 🗄️ 3. Persistence Layer (`za.co.hpsc.web.repositories` & `za.co.hpsc.web.domain`)

#### Domain Entities (`za.co.hpsc.web.domain`)

Six JPA entities map to database tables:

| Entity                 | Table                    | Key Relationships                                                       |
|------------------------|--------------------------|-------------------------------------------------------------------------|
| `Club`                 | `club`                   | One-to-many → `IpscMatch`                                               |
| `Competitor`           | `competitor`             | One-to-many → `MatchCompetitor`                                         |
| `IpscMatch`            | `ipsc_match`             | Many-to-one ← `Club`; One-to-many → `IpscMatchStage`, `MatchCompetitor` |
| `IpscMatchStage`       | `ipsc_match_stage`       | Many-to-one ← `IpscMatch`; One-to-many → `MatchStageCompetitor`         |
| `MatchCompetitor`      | `match_competitor`       | Many-to-one ← `IpscMatch`, `Competitor`                                 |
| `MatchStageCompetitor` | `match_stage_competitor` | Many-to-one ← `IpscMatchStage`, `MatchCompetitor`                       |

All bidirectional `@OneToMany` relationships include `mappedBy` to avoid duplicate join table creation.

#### Custom JPA Attribute Converters (`za.co.hpsc.web.converters`)

All enum-typed entity fields use explicit `AttributeConverter` implementations rather than
`@Enumerated(EnumType.STRING)`. This provides testable, type-safe conversion logic:

| Converter                     | Enum                 | DB Column Value     |
|-------------------------------|----------------------|---------------------|
| `ClubIdentifierConverter`     | `ClubIdentifier`     | Abbreviation string |
| `CompetitorCategoryConverter` | `CompetitorCategory` | String              |
| `DivisionConverter`           | `Division`           | String              |
| `FirearmTypeConverter`        | `FirearmType`        | String              |
| `MatchCategoryConverter`      | `MatchCategory`      | String              |
| `PowerFactorConverter`        | `PowerFactor`        | String              |

#### Repositories (`za.co.hpsc.web.repositories`)

One Spring Data JPA interface per entity. Custom query methods supplement the standard CRUD
operations (e.g., `findByNameAndScheduledDate`, `findByIdWithClubStages`).

---

### 📦 4. Model Layer (`za.co.hpsc.web.models`)

DTOs and request/response models, grouped by feature domain:

#### `models/award/` and `models/image/`
Request/response models for the award and image CSV pipelines.

#### `models/shared/`
Base `Request` and `Response` wrappers providing common metadata fields. `ControllerResponse` is the
standard JSON envelope.

#### `models/ipsc/common/`
All shared IPSC models, divided into sub-packages by their roles:

| Sub-package  | Contents                                                                                                                               |
|--------------|----------------------------------------------------------------------------------------------------------------------------------------|
| `data/`      | `DtoMapping` (Java record), `DtoToEntityMapping`, `EntityMapping` — three-tier mapping bridge                                          |
| `divisions/` | `FirearmTypeToDivisions` and per-firearm-type division lists                                                                           |
| `dto/`       | `ClubDto`, `CompetitorDto`, `MatchDto`, `MatchStageDto`, `MatchCompetitorDto`, `MatchStageCompetitorDto`, `EnrolledCompetitorDto`      |
| `holders/`   | `MatchHolder`, `MatchResultsDto`, `IpscRequestHolder`, `IpscResponseHolder`, `IpscMatchRecordHolder`                                   |
| `records/`   | `CompetitorRecord`, `CompetitorResultRecord`, `MatchCompetitorOverallResultsRecord`, `MatchCompetitorStageResultRecord`, `MatchRecord` |
| `request/`   | `IpscRequest`, `MatchRequest`, `MatchSearchRequest`, `MatchSearchDateRequest`, `MatchSearchIdRequest`, and others                      |
| `response/`  | `ClubResponse`, `EnrolledResponse`, `IpscResponse`, `MatchResponse`, `MemberResponse`, `ScoreResponse`, `StageResponse`, `TagResponse` |

#### `models/ipsc/match/`
Match-specific (CRUD) models, kept separate to avoid mixing bulk-import and resource-API concerns:

| Sub-package    | Contents                                                         |
|----------------|------------------------------------------------------------------|
| `dto/`         | `MatchOnlyDto` — lightweight match representation without stages |
| `holders/dto/` | `MatchOnlyResultsDto` — internal results holder                  |
| `request/`     | `MatchOnlyRequest` — JSON body for create / update / patch       |
| `response/`    | `MatchOnlyResponse` — typed response from `IpscMatchController`  |

---

### 🔧 5. Support Layers

#### Enumerations (`za.co.hpsc.web.enums`)

| Enum                 | Purpose                                            |
|----------------------|----------------------------------------------------|
| `ClubIdentifier`     | Known clubs with name and abbreviation             |
| `CompetitorCategory` | IPSC competitor classification                     |
| `Division`           | IPSC division (per firearm type)                   |
| `FirearmType`        | Firearm classification (Handgun, PCC, Rifle, etc.) |
| `MatchCategory`      | Match classification                               |
| `PowerFactor`        | Major / Minor power factor                         |

#### Utilities (`za.co.hpsc.web.utils`)

| Class        | Responsibility                                                                 |
|--------------|--------------------------------------------------------------------------------|
| `DateUtil`   | Date formatting and parsing helpers                                            |
| `IpscUtil`   | Display-string construction for club and match names (`"Match @ Club (ABBR)"`) |
| `NumberUtil` | Numeric parsing and formatting helpers                                         |
| `StringUtil` | String normalisation helpers                                                   |
| `ValueUtil`  | Null-safe default-value helpers (`nullAsEmptyString`, etc.)                    |

#### Constants (`za.co.hpsc.web.constants`)

`HpscConstants`, `IpscConstants`, `MatchConstants`, `SystemConstants` — application-wide constant
definitions shared across services, converters, and DTOs.

---

### 🛡️ 6. Exception and Error Handling (`za.co.hpsc.web.exceptions`)

Custom exception hierarchy enforces a consistent error-handling contract:

| Exception             | HTTP Mapping                | Use                                  |
|-----------------------|-----------------------------|--------------------------------------|
| `FatalException`      | `500 Internal Server Error` | Unrecoverable processing failures    |
| `NonFatalException`   | `400 Bad Request`           | Recoverable business-rule violations |
| `ValidationException` | `400 Bad Request`           | Input validation failures            |

`ControllerAdvice` (in `configs/`) intercepts all three exception types and translates them to a
standardised JSON error response. Structured logging is applied in every handler — do not catch and
re-throw as `RuntimeException`.

---

## 🔄 Key Design Patterns

| Pattern                      | Where Used                                                                                               |
|------------------------------|----------------------------------------------------------------------------------------------------------|
| **Layered Architecture**     | Controller → Service → Entity Service → Repository → DB — no layer may skip the one below it             |
| **Repository Pattern**       | Spring Data JPA repos abstract all DB access; entity services are the only callers                       |
| **Entity Service Pattern**   | Entity services are the single access boundary between business logic and JPA repos                      |
| **Service Layer Pattern**    | All business logic lives in service classes; controllers and repos are kept thin                         |
| **DTO Pattern**              | `models/` DTOs decouple external API contracts from JPA entities                                         |
| **Three-Tier Mapping**       | `DtoMapping` → `DtoToEntityMapping` → `EntityMapping` bridges request models to entities                 |
| **Strategy Pattern**         | CSV/XML converters (`converters/` package) handle format variants behind a common interface              |
| **Custom JPA Converters**    | `AttributeConverter` implementations replace `@Enumerated` for type-safe, testable enum persistence      |
| **Builder / Init Pattern**   | `MatchOnlyDto.init(MatchOnlyRequest)` and entity `init()` methods centralise complex object construction |
| **Optional Returns**         | Service methods return `Optional<T>` to communicate absent results without exceptions                    |
| **Global Error Handling**    | `ControllerAdvice` translates domain exceptions to HTTP responses with structured logging                |
| **Declarative Transactions** | `@Transactional` on service methods; `TransactionService` coordinates multi-entity writes                |

---

## 🔀 Data Flow

### 📈 Typical Request-Response Flow

```
Client → HTTP Request
    → Controller (validate input, extract body/path vars)
        → Service (business logic)
            → Entity Service (data access gateway)
                → Repository (Spring Data JPA query)
                    → Database
                ← Entity
            ← Domain model / DTO
        ← Service result
    ← Controller wraps in ResponseEntity<T>
← HTTP Response (JSON)
```

### 📥 Data Import Flow (CSV/XML Processing)

Handles bulk WinMSS CAB file import via `IpscController`:

```
Client uploads CAB/CSV/XML
    → IpscController
        → IpscService.importWinMssCabFile()
            → TransformationService.mapMatchResults()
                (parses file, builds IpscRequestHolder via converters/)
            → DomainService (initialises entities from DTOs)
                → ClubEntityService / CompetitorEntityService / MatchEntityService / …
                    → Repositories → DB
            → TransactionService (persists MatchCompetitor, MatchStageCompetitor)
                → MatchCompetitorEntityService / MatchStageCompetitorEntityService
                    → Repositories → DB
        ← IpscResponseHolder
    ← IpscController returns ResponseEntity<IpscResponse>
← JSON response
```

### 🆕 Match CRUD Flow

Handles individual match create / update / patch / retrieve via `IpscMatchController`:

```
Client → POST|PUT|PATCH|GET /v2/ipsc/matches[/{matchId}]
    → IpscMatchController (validate MatchOnlyRequest / path var)
        → IpscMatchService.insertMatch() | updateMatch() | modifyMatch() | getMatch()
            → TransformationService.mapMatchOnly()
                (builds MatchOnlyDto from MatchOnlyRequest via IpscUtil)
            → DomainService / TransactionService
                → Entity Services → Repositories → DB
        ← Optional<MatchOnlyResponse>
    ← ResponseEntity<MatchOnlyResponse>  (or 404/500 on empty/error)
← JSON response
```

---

## ✅ Quality Attributes

| Attribute           | How It Is Achieved                                                                                                                    |
|---------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| **Scalability**     | Stateless REST design; database-backed persistence allows horizontal scaling                                                          |
| **Maintainability** | Strict layering (no repo leakage past entity services), package-by-feature model structure, Javadoc and CLAUDE.md guidance            |
| **Robustness**      | Multi-layered validation (controller, service, entity), global exception mapping, `ValueUtil` null-safe helpers                       |
| **Testability**     | Interface-based design, Mockito-based unit tests for controllers and services, H2 integration tests for the full persistence pipeline |
| **Extensibility**   | Firearm-type enums + division mappings, strategy-pattern converters, dedicated `match/` sub-package for new CRUD resources            |
| **Data Integrity**  | JPA cascade rules, bidirectional `mappedBy` declarations, `@Transactional` service methods, custom attribute converters               |
| **Type Safety**     | Custom `AttributeConverter` implementations for all enum-typed columns replace `@Enumerated(EnumType.STRING)`                         |

---

## 🔬 CI/CD & Quality Gates

| Gate                  | Tool                                | Trigger                                                             |
|-----------------------|-------------------------------------|---------------------------------------------------------------------|
| **Static Analysis**   | Qodana JVM (`jetbrains/qodana-jvm`) | Push / PR to `main`, `develop`, `feature/*`, `bugfix/*`, `hotfix/*` |
| **Security Analysis** | CodeQL                              | Push / PR to `main` / `develop`; weekly schedule                    |
| **Code Coverage**     | JaCoCo 0.8.14                       | `./mvnw verify -Pcoverage` — reports at `target/site/jacoco/`       |
| **Build & Tests**     | Maven (`./mvnw test`)               | All PRs; H2 in-memory — no external DB required                     |

---

## 📚 Development Guidelines

Refer to [CLAUDE.md](CLAUDE.md) for AI-assistant-oriented guidance, and [README.md](README.md) for
local setup, build commands, database profiles, and coding standards.

**Key rules enforced by convention:**

- Controllers must not contain business logic — delegate to services only
- Services must not inject repositories directly — use entity services
- All exceptions must extend `FatalException`, `NonFatalException`, or `ValidationException`
- Test class names: `<ClassName>Test`; test method names: `test<Scenario>_when<Condition>_then<Expectation>`
- AssertJ for assertions; Mockito for mocking in unit tests; H2 + `test` profile for integration tests
