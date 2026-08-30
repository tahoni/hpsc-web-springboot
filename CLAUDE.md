# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository. See 
[`AGENTS.md`](AGENTS.md) for the broader, tool-agnostic conventions that also apply here — documentation conventions, git
workflow, the release checklist and tracking complex tasks with a todo list.

## Project Overview

HPSC Web is a Spring Boot REST API backend for the Handgun and Practical Shooting Club (HPSC) platform. It manages IPSC
match data, competitor tracking, club operations, awards and image gallery. There is no frontend — this is a pure API
server.

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

# Tests + JaCoCo coverage report (target/site/jacoco/)
./mvnw verify -Pcoverage
```

## Database Profiles

See [`CONTRIBUTING.md`](CONTRIBUTING.md#-database-profiles)'s Database Profiles section for the profile/DDL matrix.
Tests activate the `test` profile automatically; no database setup is required to run them.

## Code Quality & CI

See [`ARCHITECTURE.md`](ARCHITECTURE.md#-cicd--quality-gates)'s CI/CD & Quality Gates table for CodeQL/JaCoCo triggers.

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

| Package         | Role                                                                                                                                                                           |
|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `controllers/`  | `AwardController`, `ImageController`, `IpscMatchController` (full CRUD), `IpscCompetitorController`, `IpscRankingsController`, `IpscScoresController` (these three remain empty stubs) |
| `services/`     | `AwardService`, `ImageService`, `IpscMatchService`, plus implementations under `services/impl/`                                                                                |
| `repositories/` | Spring Data JPA repos for the 8 entities below                                                                                                                                 |
| `domain/`       | JPA entities: `Club`, `Competitor`, `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`, `MatchStageCompetitor`, `ShooterLog`, `ShooterLogCompetitor`                             |
| `models/`       | DTOs grouped by domain: `award/`, `image/`, `ipsc/`, plus `Request`/`Response`/`ControllerResponse` at the package root                                                        |
| `converters/`   | Custom JPA `AttributeConverter` implementations for enum-typed entity fields                                                                                                   |
| `configs/`      | Spring configuration: `ControllerAdvice` (global exception mapping), OpenAPI config                                                                                            |
| `exceptions/`   | `FatalException`, `NonFatalException`, `ValidationException` — the exception hierarchy mapped by `ControllerAdvice` (in `configs/`) to standard JSON error responses           |
| `enums/`        | `ClubIdentifier`, `CompetitorCategory`, `Division`, `FirearmType`, `Gender`, `MatchCategory`, `PowerFactor`                                                                    |
| `constants/`    | `HpscConstants`, `IpscConstants`, `SystemConstants`                                                                                                                            |
| `utils/`        | `DateUtil`, `NumberUtil`, `StringUtil`, `ValueUtil`                                                                                                                            |

> The wider IPSC match-import service, model and entity-service layers described in earlier versions of this document
> (`IpscService`, `TransformationService`, `DomainService`, `TransactionService`, entity services) remain removed from
> the codebase pending a rebuild. `IpscMatchController`/`IpscMatchService`/`models/ipsc/match/` have since been rebuilt
> from scratch (full CRUD: create/update/patch/get on `/ipsc/matches`, backed directly by `IpscMatchRepository`,
> `IpscMatchStageRepository` and `ClubRepository` — no `TransformationService`/`DomainService` layer). `models/ipsc/scores/`
> and `models/ipsc/shared/` also exist (request/shared score DTOs), but nothing consumes them yet.
> `IpscCompetitorController`, `IpscRankingsController` and `IpscScoresController` are still empty stubs — don't
> reference those three, or the removed service classes above, as if they exist until they're rebuilt.

### Exception handling

All exceptions should extend `FatalException`, `NonFatalException` or `ValidationException`. The `ControllerAdvice`
automatically maps these to the correct HTTP status and JSON response shape — do not catch and re-throw as generic
`RuntimeException`.

## Testing Patterns

- Controller tests use Mockito (`@ExtendWith(MockitoExtension.class)`) to mock the service layer; they do not start a
  Spring context.
- Service/repository integration tests use the `test` profile (H2).
- Test class names follow `<ClassName>Test`; test method names follow
  `test<Scenario>_when<Condition>_then<Expectation>`.
- JUnit Jupiter's `Assertions` are used for assertions throughout — AssertJ is explicitly excluded from
  `spring-boot-starter-webmvc-test` in `pom.xml`, so it is not available.
