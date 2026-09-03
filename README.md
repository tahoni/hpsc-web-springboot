# HPSC Website Backend

The official repository for the Spring Boot backend of the Hartbeespoortdam Practical Shooting Club (HPSC) platform.

## Table of Contents

- [📖 Introduction](#-introduction)
- [🔗 Repository](#-repository)
- [⚙️ Technology](#-technology)
- [✨ Features](#-features)
- [🚀 Instructions](#-instructions)
    - [📋 Prerequisites](#-prerequisites)
    - [🔧 Installation and Execution](#-installation-and-execution)
- [📚 API Documentation](#-api-documentation)
- [🧪 Testing](#-testing)
- [🏛️ Architecture](#-architecture)
- [📚 Documentation](#-documentation)
    - [🗺️ Roadmap](#-roadmap)
- [📜 License](#-license)
- [👤 Author](#-author)

## 📖 Introduction

The HPSC Website backend is a Spring Boot application designed to manage and serve data for the Hartbeespoortdam
Practical Shooting Club platform. It currently provides RESTful APIs for:

- **Award Ceremonies**: Award data and ceremony grouping, processed from CSV — a stateless transform by design,
  not persisted
- **Image Gallery**: Image metadata processing from CSV — a stateless transform by design, not persisted
- **IPSC Competitors**: Competitor record CRUD, including optional home club and gender, plus bulk CSV import
- **IPSC Matches**: Match and match-stage CRUD, including firearm type and match category resolution, plus bulk CSV
  import

JPA entities and repositories also exist for match/competitor scoring and shooter logs. Request DTOs exist for
competitor scores submission, laying the groundwork for match result processing and ranking calculations — the
service and controller layer that will operate on that remaining domain is still being built.

The application emphasises structured data processing and validation, with MySQL-backed persistence via Spring Data JPA
and Hibernate.

## 🔗 Repository

The repository for this project is located at [GitHub](https://github.com/tahoni/hpsc-web-springboot).

Feature requests, suggestions for improvements and bugs can be logged using the
project's [Issues](https://github.com/tahoni/hpsc-web-springboot/issues) page.

## ⚙️ Technology

This is a Spring Boot application built with:

- **Framework**: [Spring Boot](https://spring.io/projects/spring-boot)
- **Language**: [Java](https://www.oracle.com/java/)
- **Build Tool**: [Maven](https://maven.apache.org/) (or the provided `./mvnw` wrapper)
- **Database**: MySQL with Spring Data JPA and Hibernate
- **Schema Migrations**: Flyway
- **Data Processing**: Jackson (JSON, CSV, XML), Apache Commons Lang3
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Validation**: Hibernate Validator with Jakarta Validation
- **Testing**: JUnit, Mockito, Spring Test, Spring REST Docs

Bootstrapped using the [Spring Initializr](https://start.spring.io/).

## ✨ Features

- **CSV Data Processing**: Jackson-based CSV parsing for award ceremony and image gallery data, plus bulk competitor
  and match import that persists each row.
- **IPSC Competitor & Match Management**: Full CRUD for competitors and matches (with stages), including club,
  gender, firearm-type and match-category resolution by name.
- **Match Scoring Domain Model**: JPA entities, repositories, type-safe enum converters and request DTOs for
  match/competitor scoring and shooter logs, ready for the upcoming result-processing service/controller layer.
- **Firearm Type & Division Management**: Type-safe enumerations and mappings for IPSC divisions across multiple firearm
  types.
- **Modern API Standards**: Fully documented REST endpoints via OpenAPI/Swagger UI.
- **Data Integrity**: Multi-layered validation (controller, service, entity) with detailed error reporting.
- **Comprehensive Testing**: Extensive unit and integration test coverage with Spring Test and Mockito.
- **Modern Tech Stack**: Leveraging current Java language features and the Spring Boot framework.

## 🚀 Instructions

### 📋 Prerequisites

- **Java SDK**: See `<java.version>` in `pom.xml` for the required version
- **Maven**: Use the provided `./mvnw` wrapper (pins its own version automatically), or a compatible local installation
- **MySQL**: Any current version (or a compatible database)
- **Database Configuration**: Configure connection details in `application.properties`

### 🔧 Installation and Execution

1. **Clone the repository**:
   ```bash
   git clone https://github.com/tahoni/hpsc-web-springboot.git
   cd hpsc-web-springboot
   ```

2. **Create a local MySQL database** (e.g. `hpsc_dev`), then export the credentials the application always reads
   from the environment, regardless of profile:
   ```bash
   export MYSQL_USER=your_username
   export MYSQL_PASSWORD=your_password
   ```

3. **Build the project**:
   ```bash
   ./mvnw clean install
   ```

4. **Run the application** against the `dev` profile (points at `localhost:3306/hpsc_dev`):
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

The application starts on `http://localhost:8081/hpsc-web`. See
[`CONTRIBUTING.md`](CONTRIBUTING.md#-database-profiles) for the full profile/DDL matrix and other database options.

## 📚 API Documentation

Interactive API documentation is automatically generated using SpringDoc OpenAPI and can be accessed at:

- **Swagger UI**: `http://localhost:8081/hpsc-web/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8081/hpsc-web/v3/api-docs`

The Swagger UI provides a comprehensive, interactive interface for exploring and testing all available REST endpoints.

## 🧪 Testing

The application includes comprehensive test coverage with unit and integration tests.

**Run all tests**:

```bash
./mvnw test
```

**Run tests with coverage report**:

```bash
./mvnw verify -Pcoverage
```

The report is written to `target/site/jacoco/`.

**Test Categories**:

- **Unit Tests**: Domain entities, DTOs, enums, utilities and service logic
- **Integration Tests**: Service implementations and data access layers
- **Test Frameworks**: JUnit, Mockito, Spring Test

Test coverage includes:

- Domain entities and JPA relationships
- DTO initialisation and mapping logic
- Enum validations and lookups
- Service layer business logic
- Repository operations
- Utility methods and helpers

## 🏛️ Architecture

A detailed explanation of the architecture can be found in the [`ARCHITECTURE.md`](ARCHITECTURE.md) file.

## 📚 Documentation

This project's documentation is spread across a few files, each with a distinct purpose:

| File                                   | Purpose                                                                                         |
|----------------------------------------|-------------------------------------------------------------------------------------------------|
| [`README.md`](README.md)               | Project overview, setup and links to the rest of the documentation (this file)                  |
| [`ARCHITECTURE.md`](ARCHITECTURE.md)   | Detailed architectural design, layered structure and CI/CD quality gates                        |
| [`CLAUDE.md`](CLAUDE.md)               | Thin pointer to `AGENTS.md`, kept for Claude Code's filename discovery                          |
| [`AGENTS.md`](AGENTS.md)               | Project overview, build/run commands, architecture and cross-tool conventions for AI agents     |
| [`CONTRIBUTING.md`](CONTRIBUTING.md)   | New-developer onboarding: setup, database profiles, testing, workflow                           |
| [`CHANGELOG.md`](CHANGELOG.md)         | Notable changes per release, in [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) format |
| [`HISTORY.md`](HISTORY.md)             | Narrative history of the project's evolution across all versions                                |
| [`RELEASE_NOTES.md`](RELEASE_NOTES.md) | Detailed release notes for the current/latest version                                           |
| [`LICENSE.md`](LICENSE.md)             | MIT License                                                                                     |
| [`HELP.md`](HELP.md)                   | Spring Initializr reference links (Maven, Spring Boot docs, guides)                             |

[`documentation/history/`](documentation/history) holds one of each of the following files per released version, so past
releases stay individually referenceable as `RELEASE_NOTES.md` and the release PR moves on to the next version:

| File                       | Purpose                                                    |
|----------------------------|------------------------------------------------------------|
| `RELEASE_NOTES_vX.Y.Z.md`  | Archived snapshot of `RELEASE_NOTES.md` at release time    |
| `PR_DESCRIPTION_vX.Y.Z.md` | The release pull request's body, archived for that version |

[`documentation/archive/ARCHIVE.md`](documentation/archive/ARCHIVE.md) is the legacy release archive covering releases
from before the project adopted its current documentation structure (`CHANGELOG.md`, `RELEASE_NOTES.md`, per-version
history). It's a historical record only and isn't maintained going forward.

### 🗺️ Roadmap

[`documentation/roadmap/`](documentation/roadmap) holds in-progress planning documents — not part of the standard
documentation set above, and not required reading to work in this repository:

| File                        | Purpose                                                                                                          |
|-----------------------------|------------------------------------------------------------------------------------------------------------------|
| `improvement-plan.md`       | Synthesised goals/constraints from this project's own docs and configuration, and the resulting gaps and roadmap |
| `improvement-plan-tasks.md` | Concrete, checkbox-level task list broken out from `improvement-plan.md`'s gaps                                  |

This project follows [Semantic Versioning 2.0.0](https://semver.org/) (`MAJOR.MINOR.PATCH`) —
see [CHANGELOG.md](CHANGELOG.md#-version-policy) for the full version policy.

## 📜 License

The copyright license can be found in the [`LICENSE.md`](LICENSE.md) file.

## 👤 Author

**Leoni Lubbinge**

- [![Website Badge](https://custom-icon-badges.demolab.com/badge/https%3A%2F%2Ftahoni.info-blue?logo=file-code)](https://www.tahoni.info)
- [![Email Badge](https://custom-icon-badges.demolab.com/badge/leonil%40tahoni.info-blue?logo=mail)](mailto:leonil@tahoni.info)


- [![Gmail Badge](https://img.shields.io/badge/tahoni%40gmail.com-blue?logo=gmail)](mailto:tahoni@gmail.com)
- [![GitHub Badge](https://img.shields.io/badge/Leoni_Lubbinge-blue?logo=github)](https://github.com/tahoni)
- [![LinkedIn Badge](https://custom-icon-badges.demolab.com/badge/Leoni_Lubbinge-blue.svg?logoSource=feather&logo=linkedin)](https://www.linkedin.com/in/leoni-lubbinge-06066b16/)
