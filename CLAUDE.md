# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HPSC Web is a Spring Boot REST API backend for the Handgun and Practical Shooting Club (HPSC) platform. It manages IPSC match data, competitor tracking, club operations, awards, and image gallery. There is no frontend — this is a pure API server.

- **Runtime**: Java 25, Spring Boot 4.0.5, Spring Framework 7.0.7
- **Port / context path**: `8081` / `/hpsc-web`
- **API docs**: Swagger UI at `http://localhost:8081/hpsc-web/swagger-ui/index.html`

## Build & Run Commands

```bash
# Build
./mvnw clean install

# Run (uses application.properties; requires MYSQL_USER and MYSQL_PASSWORD env vars)
./mvnw spring-boot:run

# Run with dev profile (local MySQL at localhost:3306/hpsc_dev)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# All tests (uses H2 in-memory — no external DB needed)
./mvnw test

# Single test class
./mvnw test -Dtest=IpscControllerTest

# Single test method
./mvnw test -Dtest=IpscControllerTest#testImportWinMssCabData_whenValidContent_thenReturns200

# Tests + JaCoCo coverage report (target/site/jacoco/)
./mvnw verify -Pcoverage
```

## Database Profiles

| Profile       | Database                                         | DDL                        |
|---------------|--------------------------------------------------|----------------------------|
| (none / prod) | MySQL — env vars `MYSQL_USER` / `MYSQL_PASSWORD` | `none` (manual migrations) |
| `dev`         | MySQL `localhost:3306/hpsc_dev`                  | configured in properties   |
| `test`        | H2 in-memory `testdb`                            | `create-drop` (auto)       |

Tests activate the `test` profile automatically; no database setup is required to run them.

## Code Quality & CI

- **Qodana** (JetBrains): primary quality gate, runs on push/PR to `main`, `develop`, `feature/*`, `bugfix/*`, `hotfix/*`, `releases/*`. Config: `qodana.yaml`. Baseline: `baseline.sarif.json`.
- **CodeQL**: security analysis, runs on push/PR to `main`/`develop` and weekly. Config: `.github/workflows/codeql.yml`.
- **JaCoCo**: coverage integrated into the `coverage` Maven profile; reports are consumed by Qodana.

## Architecture

The application follows a strict layered architecture:

```
HTTP Request
    → Controller  (REST endpoint, DTO validation)
    → Service     (business logic, @Transactional)
    → Repository  (Spring Data JPA)
    → MySQL / H2
```

### Key layers and packages (`src/main/java/za/co/hpsc/web/`)

| Package         | Role                                                                                                                               |
|-----------------|------------------------------------------------------------------------------------------------------------------------------------|
| `controllers/`  | `AwardController`, `ImageController`, `IpscController`                                                                             |
| `services/`     | `IpscService`, `AwardService`, `ImageService`, `TransformationService`, `TransactionService`, plus entity-level services           |
| `repositories/` | Spring Data JPA repos for the 6 entities below                                                                                     |
| `domain/`       | JPA entities: `Club`, `Competitor`, `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`, `MatchStageCompetitor`                       |
| `models/`       | DTOs grouped by domain: `award/`, `image/`, `ipsc/`, `shared/`                                                                     |
| `converters/`   | CSV/XML parsing with strategy pattern and MIME-type inference                                                                      |
| `exceptions/`   | `FatalException`, `NonFatalException`, `ValidationException`; `ControllerAdvice` translates these to standard JSON error responses |
| `enums/`        | `ClubIdentifier`, `CompetitorCategory`, `Division`, `FirearmType`, `MatchCategory`, `PowerFactor`                                  |
| `constants/`    | `HpscConstants`, `IpscConstants`, `MatchConstants`, `SystemConstants`                                                              |
| `utils/`        | `DateUtil`, `NumberUtil`, `StringUtil`, `ValueUtil`                                                                                |

### Data flow for IPSC match import

`IpscController` receives CSV/XML match data → `TransformationService` parses it (using `converters/`) → `IpscService` validates and processes → `TransactionService` coordinates persistence across multiple entity services → repositories persist to DB.

### Exception handling

All exceptions should extend `FatalException`, `NonFatalException`, or `ValidationException`. The `ControllerAdvice` automatically maps these to the correct HTTP status and JSON response shape — do not catch and re-throw as generic `RuntimeException`.

## Testing Patterns

- Controller tests use Mockito (`@ExtendWith(MockitoExtension.class)`) to mock the service layer; they do not start a Spring context.
- Service/repository integration tests use the `test` profile (H2).
- Test class names follow `<ClassName>Test`; test method names follow `test<Scenario>_when<Condition>_then<Expectation>`.
- AssertJ is used for assertions throughout.