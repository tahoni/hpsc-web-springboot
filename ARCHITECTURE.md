# HPSC Website Backend Architecture

This document describes the architectural design, directory structure and core concepts of the Hartbeespoortdam Practical Shooting Club (HPSC) Spring Boot backend.

## Table of Contents

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
    - [📥 Award / Image CSV Processing Flow](#-award--image-csv-processing-flow)
- [✅ Quality Attributes](#-quality-attributes)
- [🔬 CI/CD & Quality Gates](#-cicd--quality-gates)
- [📚 Development Guidelines](#-development-guidelines)

---

## ⚙️ Technology Stack

| Component         | Technology                                                          |
|-------------------|---------------------------------------------------------------------|
| Framework         | Spring Boot (see `pom.xml` for the pinned version)                  |
| Language          | Java (see `<java.version>` in `pom.xml`)                            |
| Build             | Maven, via the provided `./mvnw` wrapper                            |
| Database (prod)   | MySQL (env vars `MYSQL_USER` / `MYSQL_PASSWORD`)                    |
| Database (test)   | H2 in-memory (`create-drop`, profile `test`)                        |
| ORM               | Spring Data JPA, Hibernate                                          |
| Schema migrations | Flyway (`src/main/resources/db/migration/`)                         |
| Data processing   | Jackson (JSON/CSV/XML), Apache Commons Lang3                        |
| API documentation | SpringDoc OpenAPI (Swagger UI at `/hpsc-web/swagger-ui/index.html`) |
| Validation        | Hibernate Validator, Jakarta Validation                             |
| Testing           | JUnit, Mockito, Spring Test                                         |
| Code coverage     | JaCoCo (Maven `coverage` profile)                                   |
| Code generation   | Lombok                                                              |
| Port / context    | `8081` / `/hpsc-web`                                                |

---

## 📁 Project Structure

```text
├───.github/
│   └───workflows/              # GitHub Actions — CI/CD, CodeQL
├───.mvn/wrapper/               # Maven wrapper
├───documentation/
│   ├───archive/                # Legacy release archive (see ARCHIVE.md)
│   ├───history/                # Per-version release notes (RELEASE_NOTES_vX.Y.Z.md)
│   └───roadmap/                # Concrete task-list breakdown of improvement-plan.md's gaps
├───src/
│   ├───main/java/za/co/hpsc/web/
│   │   ├───configs/            # Spring configuration (ControllerAdvice, OpenAPI)
│   │   ├───constants/          # Application-wide constants
│   │   │                           HpscConstants, IpscConstants, SystemConstants
│   │   ├───controllers/        # REST controllers
│   │   │                           AwardController, ImageController
│   │   │                           IpscController          (empty stub — no endpoints yet)
│   │   ├───converters/         # Custom JPA AttributeConverters for all enum fields
│   │   │                           ClubIdentifierConverter, CompetitorCategoryConverter
│   │   │                           DivisionConverter, FirearmTypeConverter
│   │   │                           MatchCategoryConverter, PowerFactorConverter
│   │   ├───domain/             # JPA entities (database tables)
│   │   │                           Club, Competitor, IpscMatch, IpscMatchStage
│   │   │                           MatchCompetitor, MatchStageCompetitor
│   │   │                           ShooterLog, ShooterLogCompetitor
│   │   ├───enums/              # Domain enumerations
│   │   │                           ClubIdentifier, CompetitorCategory, Division
│   │   │                           FirearmType, MatchCategory, PowerFactor
│   │   ├───exceptions/         # Custom exception hierarchy + ControllerAdvice mapping
│   │   ├───models/             # DTOs, request/response models
│   │   │   ├───award/          # Award request/response models
│   │   │   ├───image/          # Image gallery request/response models
│   │   │   ├───ipsc/
│   │   │   │   ├───request/    # IPSC match/stage/result request DTOs (groundwork)
│   │   │   │   └───shared/     # Comstock-scoring shared fields (groundwork)
│   │   │   ├───shared/         # Placing
│   │   │   └───(root)          # Request, Response, ControllerResponse
│   │   ├───repositories/       # Spring Data JPA interfaces (not yet wired to any service)
│   │   ├───services/           # Service interfaces
│   │   │   └───impl/           # Service implementations
│   │   └───utils/              # Utility classes
│   │                               DateUtil, NumberUtil, StringUtil, ValueUtil
│   └───main/resources/
│       ├───logback-spring.xml  # Logging configuration
│       └───application*.properties
└───src/test/java/za/co/hpsc/web/
    ├───configs/                # ControllerAdvice tests
    ├───controllers/            # Controller unit tests (Mockito, no Spring context)
    ├───converters/             # AttributeConverter unit tests
    ├───enums/                  # Enum unit tests
    ├───exceptions/             # Exception hierarchy unit tests
    ├───models/                 # DTO / model unit tests
    ├───services/               # Service integration tests (H2)
    │   └───impl/               # Service unit tests (Mockito)
    └───utils/                  # Utility unit tests
```

---

## 🎯 System Overview

The HPSC Website Backend is a pure REST API server (no frontend) that manages practical shooting club operations. Core responsibilities:

| Domain                        | Description                                                                                                                                                    |
|-------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Award Ceremonies**          | Award data and ceremony grouping, processed from CSV                                                                                                           |
| **Image Gallery**             | Image metadata processing from CSV                                                                                                                             |
| **Match & Competitor Domain** | JPA entities and repositories exist for matches, competitors, clubs and shooter logs, but the service/controller layer that operates on them is being rebuilt  |

The application follows a strict **N-Tier Layered Architecture** with unidirectional dependencies:

```
HTTP Request
    → Controller
        → Service
            → Repository
                → Database
```

---

## 🏗️ Layered Architecture

### 📊 1. Presentation Layer (`za.co.hpsc.web.controllers`)

Handles incoming HTTP requests. Does not contain business logic.

| Controller        | Mapping                     | Responsibility                            |
|-------------------|-----------------------------|-------------------------------------------|
| `AwardController` | `/hpsc-web/awards`          | Award CSV processing                      |
| `ImageController` | `/hpsc-web/images`          | Image CSV processing                      |
| `IpscController`  | `/hpsc-web/ipsc/competitor` | Empty stub — no endpoints implemented yet |

All controllers:
- Validate request bodies and path variables at the boundary
- Delegate all processing to the service layer
- Return `ResponseEntity<T>` with typed response models
- Are annotated with full OpenAPI (`@Tag`, `@Operation`, `@ApiResponse`) metadata

`ControllerAdvice` in `za.co.hpsc.web.configs` catches all `FatalException`, `NonFatalException` and `ValidationException` instances and maps them to standard JSON error responses with structured logging.

---

### ⚡ 2. Service Layer (`za.co.hpsc.web.services`)

Contains all business logic.

| Interface      | Implementation     | Role                 |
|----------------|--------------------|----------------------|
| `AwardService` | `AwardServiceImpl` | Award CSV processing |
| `ImageService` | `ImageServiceImpl` | Image CSV processing |

> The match/competitor domain's service layer (bulk import, CRUD, entity initialisation) is being rebuilt — only `AwardService`/`ImageService` currently exist. Neither of them calls into the repository layer directly; `repositories/` currently has no service-layer caller.

---

### 🗄️ 3. Persistence Layer (`za.co.hpsc.web.repositories` & `za.co.hpsc.web.domain`)

#### Domain Entities (`za.co.hpsc.web.domain`)

The JPA entities map to database tables:

| Entity                 | Table                    | Key Relationships                                                                                     |
|------------------------|--------------------------|-------------------------------------------------------------------------------------------------------|
| `Club`                 | `club`                   | One-to-many → `IpscMatch`, `Competitor` (home club), `ShooterLog`                                     |
| `Competitor`           | `competitor`             | Many-to-one ← `Club` (home club, optional); One-to-many → `MatchCompetitor`, `ShooterLog`             |
| `IpscMatch`            | `ipsc_match`             | Many-to-one ← `Club`; One-to-many → `IpscMatchStage`, `MatchCompetitor`, `ShooterLogCompetitor`       |
| `IpscMatchStage`       | `ipsc_match_stage`       | Many-to-one ← `IpscMatch`; One-to-many → `MatchStageCompetitor`                                       |
| `MatchCompetitor`      | `match_competitor`       | Many-to-one ← `IpscMatch`, `Competitor`; One-to-many → `MatchStageCompetitor`, `ShooterLogCompetitor` |
| `MatchStageCompetitor` | `match_stage_competitor` | Many-to-one ← `IpscMatchStage`, `MatchCompetitor`                                                     |
| `ShooterLog`           | `shooter_log`            | Many-to-one ← `Competitor`, `Club`; One-to-many → `ShooterLogCompetitor`                              |
| `ShooterLogCompetitor` | `shooter_log_competitor` | Many-to-one ← `ShooterLog`, `MatchCompetitor`, `IpscMatch`                                            |

All bidirectional `@OneToMany` relationships include `mappedBy` to avoid duplicate join table creation.

#### Custom JPA Attribute Converters (`za.co.hpsc.web.converters`)

All enum-typed entity fields use explicit `AttributeConverter` implementations rather than `@Enumerated(EnumType.STRING)`. This provides testable, type-safe conversion logic:

| Converter                     | Enum                 | DB Column Value     |
|-------------------------------|----------------------|---------------------|
| `ClubIdentifierConverter`     | `ClubIdentifier`     | Abbreviation string |
| `CompetitorCategoryConverter` | `CompetitorCategory` | String              |
| `DivisionConverter`           | `Division`           | String              |
| `FirearmTypeConverter`        | `FirearmType`        | String              |
| `MatchCategoryConverter`      | `MatchCategory`      | String              |
| `PowerFactorConverter`        | `PowerFactor`        | String              |

#### Repositories (`za.co.hpsc.web.repositories`)

One Spring Data JPA interface per entity. Custom query methods supplement the standard CRUD operations (e.g., `IpscMatchRepository.findAllByClubId`, `ShooterLogRepository.findAllByCompetitorIdAndFirearmTypeAndPowerFactor`).

---

### 📦 4. Model Layer (`za.co.hpsc.web.models`)

DTOs and request/response models, grouped by feature domain:

#### `models/award/` and `models/image/`
Request/response models for the award and image CSV pipelines.

#### `models/shared/`
`Placing`, a shared result-placement model.

#### Package root (`za.co.hpsc.web.models`)
`Request` and `Response` base wrappers provide common metadata fields. `ControllerResponse` is the standard JSON envelope.

#### `models/ipsc/request/` and `models/ipsc/shared/`
Request DTOs for the IPSC module rebuild — `MatchRequest`/`MatchStageRequest`/`MatchStagesRequest` for match/stage submission, `MatchOverallResultRequest`/`MatchStageResultRequest` (plus CSV variants) for competitor result submission, and the shared Comstock-scoring fields in `IpscCommonScore`/`IpscMatchScore`/`IpscMatchStageScore`. Groundwork only — not yet consumed by `IpscController`.

---

### 🔧 5. Support Layers

#### Enumerations (`za.co.hpsc.web.enums`)

| Enum                 | Purpose                                            |
|----------------------|----------------------------------------------------|
| `ClubIdentifier`     | Known clubs with name and abbreviation             |
| `CompetitorCategory` | IPSC competitor classification                     |
| `Division`           | IPSC division (per firearm type)                   |
| `FirearmType`        | Firearm classification (Handgun, PCC, Rifle, etc.) |
| `Gender`             | Competitor gender classification                   |
| `MatchCategory`      | Match classification                               |
| `PowerFactor`        | Major / Minor power factor                         |

#### Utilities (`za.co.hpsc.web.utils`)

| Class        | Responsibility                                                                 |
|--------------|--------------------------------------------------------------------------------|
| `DateUtil`   | Date formatting and parsing helpers                                            |
| `NumberUtil` | Numeric parsing and formatting helpers                                         |
| `StringUtil` | String normalisation helpers                                                   |
| `ValueUtil`  | Null-safe default-value helpers (`nullAsEmptyString`, etc.)                    |

#### Constants (`za.co.hpsc.web.constants`)

`HpscConstants`, `IpscConstants`, `SystemConstants` — application-wide constant definitions shared across services and converters.

---

### 🛡️ 6. Exception and Error Handling (`za.co.hpsc.web.exceptions`)

Custom exception hierarchy enforces a consistent error-handling contract:

| Exception             | HTTP Mapping                | Use                                  |
|-----------------------|-----------------------------|--------------------------------------|
| `FatalException`      | `500 Internal Server Error` | Unrecoverable processing failures    |
| `NonFatalException`   | `400 Bad Request`           | Recoverable business-rule violations |
| `ValidationException` | `400 Bad Request`           | Input validation failures            |

`ControllerAdvice` (in `configs/`) intercepts all three exception types and translates them to a standardised JSON error response. Structured logging is applied in every handler — do not catch and re-throw as `RuntimeException`.

---

## 🔄 Key Design Patterns

| Pattern                   | Where Used                                                                                          |
|---------------------------|-----------------------------------------------------------------------------------------------------|
| **Layered Architecture**  | Controller → Service → Repository → DB — no layer may skip the one below it                         |
| **Repository Pattern**    | Spring Data JPA repos abstract all DB access                                                        |
| **Service Layer Pattern** | All business logic lives in service classes; controllers and repos are kept thin                    |
| **DTO Pattern**           | `models/` DTOs decouple external API contracts from JPA entities                                    |
| **Strategy Pattern**      | CSV/XML converters (`converters/` package) handle format variants behind a common interface         |
| **Custom JPA Converters** | `AttributeConverter` implementations replace `@Enumerated` for type-safe, testable enum persistence |
| **Global Error Handling** | `ControllerAdvice` translates domain exceptions to HTTP responses with structured logging           |

---

## 🔀 Data Flow

### 📈 Typical Request-Response Flow

```
Client → HTTP Request
    → Controller (validate input, extract body/path vars)
        → Service (business logic)
            → Repository (Spring Data JPA query, where applicable)
                → Database
            ← Domain model / DTO
        ← Service result
    ← Controller wraps in ResponseEntity<T>
← HTTP Response (JSON)
```

### 📥 Award / Image CSV Processing Flow

The only data-processing pipeline currently implemented, handled by `AwardController` and `ImageController`:

```
Client uploads CSV (Content-Type: text/csv)
    → AwardController / ImageController
        → AwardService.processCsv() / ImageService.processCsv()
            (parses CSV via Jackson CsvMapper, maps to response records — no persistence)
        ← AwardCeremonyResponseHolder / ImageResponseHolder
    ← ResponseEntity<...>
← JSON response
```

> The match/competitor bulk-import and CRUD flows described in earlier versions of this document (`IpscController`, WinMSS CAB import, `/v2/ipsc/matches` CRUD) have been removed pending a rebuild of that service layer.

---

## ✅ Quality Attributes

| Attribute           | How It Is Achieved                                                                                                                    |
|---------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| **Scalability**     | Stateless REST design; database-backed persistence allows horizontal scaling                                                          |
| **Maintainability** | Strict layering, package-by-feature model structure, Javadoc and CLAUDE.md guidance                                                   |
| **Robustness**      | Multi-layered validation (controller, service, entity), global exception mapping, `ValueUtil` null-safe helpers                       |
| **Testability**     | Interface-based design, Mockito-based unit tests for controllers and services, H2 integration tests for the full persistence pipeline |
| **Extensibility**   | Firearm-type enums + division mappings, strategy-pattern converters                                                                   |
| **Data Integrity**  | JPA cascade rules, bidirectional `mappedBy` declarations, `@Transactional` service methods, custom attribute converters               |
| **Type Safety**     | Custom `AttributeConverter` implementations for all enum-typed columns replace `@Enumerated(EnumType.STRING)`                         |

---

## 🔬 CI/CD & Quality Gates

| Gate                  | Tool                  | Trigger                                                                         |
|-----------------------|-----------------------|---------------------------------------------------------------------------------|
| **Security Analysis** | CodeQL                | Push / PR to `main` / `develop`; weekly schedule                                |
| **Code Coverage**     | JaCoCo                | `./mvnw verify -Pcoverage` — reports at `target/site/jacoco/`                   |
| **Build & Tests**     | Maven (`./mvnw test`) | Run locally / by reviewers before merge; H2 in-memory — no external DB required |

---

## 📚 Development Guidelines

Refer to [CLAUDE.md](CLAUDE.md) for AI-assistant-oriented guidance, and [README.md](README.md) for local setup, build commands, database profiles and coding standards. See README.md's [📚 Documentation](README.md#-documentation) section for a full map of this project's documentation.

**Key rules enforced by convention:**

- Controllers must not contain business logic — delegate to services only
- All exceptions must extend `FatalException`, `NonFatalException` or `ValidationException`
- Test class names: `<ClassName>Test`; test method names: `test<Scenario>_when<Condition>_then<Expectation>`
- JUnit Jupiter's `Assertions` for assertions; Mockito for mocking in unit tests; H2 + `test` profile for integration tests
