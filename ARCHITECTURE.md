# HPSC Website Backend Architecture

This document describes the architectural design, directory structure, and core concepts
of the Hartbeespoortdam Practical Shooting Club (HPSC) Spring Boot backend.

## 📑 Table of Contents

- [⚙️ Technology Stack](#-technology-stack)
- [📁 Project Structure](#project-structure)
- [🎯 System Overview](#-system-overview)
- [🏗️ Layered Structure](#-layered-structure)
    - [📊 Presentation Layer](#-1-presentation-layer-zacohpscwebcontrollers)
    - [⚡ Service Layer](#-2-service-layer-zacohpscwebservices)
    - [🗄️ Persistence Layer](#-3-persistence-layer-zacohpscwebrepositories--zacohpscwebdomain)
    - [📦 Model Layer](#-4-model-layer-zacohpscwebmodels)
    - [🔧 Domain Support Layers](#-5-domain-support-layers)
    - [🛡️ Exception and Error Handling](#-6-exception-and-error-handling-zacohpscwebexceptions)
- [🔄 Key Design Patterns](#-key-design-patterns)
- [🔀 Data Flow](#-data-flow)
    - [📈 Typical Request-Response Flow](#-typical-request-response-flow)
    - [📥 Data Import Flow (CSV/XML Processing)](#-data-import-flow-csvxml-processing)
- [✅ Quality Attributes](#-quality-attributes)
- [📚 Development Guidelines](#-development-guidelines)

## ⚙️ Technology Stack

The application is built using modern technologies:

- **Framework**: [Spring Boot 4.0.2](https://spring.io/projects/spring-boot)
- **Build Tool**: [Maven 3.9](https://maven.apache.org/)
- **Language**: [Java 25](https://www.oracle.com/java/)
- **Database**: MySQL with Spring Data JPA and Hibernate 7.2
- **Data Processing**: Jackson (JSON, CSV, XML), Apache Commons Lang3
- **API Documentation**: SpringDoc OpenAPI 2.8.5 (Swagger UI)
- **Validation**: Hibernate Validator with Jakarta Validation
- **Testing**: JUnit 5, Mockito, Spring Test, Spring REST Docs

## Project Structure

The project follows a conventional Maven and Spring Boot layout, and separates presentation (`controllers`),
business (`services`), and data/DTOs (`models`) for clear layering and testability.

```text
├───.github                                     # CI/CD workflows (GitHub Actions)
│   └───workflows                               # GitHub Actions workflow definitions
├───.mvn                                        # Maven wrapper files
│   └───wrapper                                 # Maven wrapper jar and properties
├───documentation                               # Project documents and templates
│   └───templates                               # Documentation templates
├───src                                         # Source code
│   ├───main                                    # Main application source
│   │   ├───java                                # Java source files
│   │   │   └───za                              # Root package
│   │   │       └───co                          # Company domain
│   │   │           └───hpsc                    # Project namespace
│   │   │               └───web                 # Application packages
│   │   │                   ├───configs         # Spring configuration classes
│   │   │                   ├───constants       # Application-wide constant values
│   │   │                   ├───controllers     # REST controllers / HTTP endpoints
│   │   │                   ├───domain          # JPA entities representing database tables
│   │   │                   ├───enums           # Enumeration types used across the application
│   │   │                   ├───exceptions      # Global and domain-specific error handlers
│   │   │                   ├───helpers         # Domain-specific helper classes
│   │   │                   ├───models          # DTOs and domain models used across layers
│   │   │                   │   ├───award       # Award-related request/response models
│   │   │                   │   ├───image       # Image gallery request/response models
│   │   │                   │   ├───ipsc        # IPSC match data request/response models
│   │   │                   │   └───shared      # Shared models used across features
│   │   │                   ├───repositories    # Spring Data JPA repositories
│   │   │                   ├───services        # Service interfaces and business logic
│   │   │                   │   └───impl        # Concrete service implementations
│   │   │                   └───utils           # Helper utilities and common functions
│   │   └───resources                           # Application resources
│   │       ├───static                          # Static assets served by the application
│   │       └───application.properties          # Application configuration
│   └───test                                    # Unit/integration tests source
│       └───java                                # Java test source files
│           └───za                              # Root package
│               └───co                          # Company domain
│                   └───hpsc                    # Project namespace
│                       └───web                 # Application test packages
│                           ├───domain          # Tests of JPA entities
│                           ├───enums           # Tests of enumeration types
│                           ├───models          # Tests of DTOs and domain models
│                           ├───services        # Tests of service interfaces and business logic
│                           │   └───impl        # Tests of concrete service implementations
│                           └───utils           # Tests of helper utilities and common functions
```

## 🎯 System Overview

The HPSC Website Backend is a REST API service that manages practical shooting club operations, including:

- **Match Management**: Processing and storing shooting match results from WinMSS and IPSC formats
- **Competitor Tracking**: Maintaining competitor profiles, classifications, and performance history
- **Award Ceremonies**: Managing award data and ceremony information
- **Image Gallery**: Serving static image assets for the club website
- **IPSC Integration**: Supporting IPSC (International Practical Shooting Confederation) data standards

The application follows a classic **N-Tier Architecture** pattern, ensuring a clean separation of concerns
between the entry points, business logic, data access, and persistence layers.

## 🏗️ Layered Structure

### 📊 1. Presentation Layer (`za.co.hpsc.web.controllers`)

- Responsible for handling incoming HTTP requests.
- Maps REST endpoints to service layer methods.
- Handles Request/Response DTO mapping and basic input validation via Spring's validation framework.

### ⚡ 2. Service Layer (`za.co.hpsc.web.services`)

- Contains the core business logic.
- Orchestrates data flow between controllers, repositories, and data processing components.
- Implementation of the **CSV Processing Engine**, which transforms raw flat-file data into structured domain
  objects.
- Implementation of **WinMSS** and **IPSC Match** data processing services.
- **Transaction Management**: Handles database transactions via `TransactionService`.

### 🗄️ 3. Persistence Layer (`za.co.hpsc.web.repositories` & `za.co.hpsc.web.domain`)

- **Repositories**: Spring Data JPA interfaces for database access patterns.
- **Domain Entities**: JPA entities representing database tables.
- Provides abstraction over database operations with automatic query generation.

### 📦 4. Model Layer (`za.co.hpsc.web.models`)

- Defines the data structures used throughout the application.
- Includes **DTOs** (Data Transfer Objects) for external communication and internal domain representations.
- Organised by feature domains.
- Base request/response wrappers for consistent API contracts.

### 🔧 5. Domain Support Layers

- **Enums** (`za.co.hpsc.web.enums`): Type-safe enumerations for domain concepts.
- **Helpers** (`za.co.hpsc.web.helpers`): Domain-specific helper classes and business logic utilities.
- **Constants** (`za.co.hpsc.web.constants`): Application-wide constant definitions.
- **Utils** (`za.co.hpsc.web.utils`): Generic utility functions.

### 🛡️ 6. Exception and Error Handling (`za.co.hpsc.web.exceptions`)

- Global exception handling mechanism using `@ControllerAdvice`.
- Custom exception hierarchy: `FatalException`, `NonFatalException`, `ValidationException`.
- Translates internal processing errors and validation failures into standardised, user-friendly JSON
  responses.

## 🔄 Key Design Patterns

- **Repository Pattern**: Spring Data JPA repositories abstract database access and provide clean data access
  APIs.
- **Service Layer Pattern**: Business logic is encapsulated in service classes, separated from controllers and
  repositories.
- **DTO Pattern**: Data Transfer Objects decouple external API contracts from internal domain models.
- **Strategy Pattern**: Used in CSV/XML parsing to handle different column ordering and data formats
  dynamically.
- **Transaction Management**: Declarative transaction boundaries using Spring's `@Transactional` annotation.
- **Inference Logic**: The system includes a MIME-type inference engine to automatically classify media based
  on file extensions or metadata.
- **Global Error Handling**: Centralised management of application state and error feedback via
  `@ControllerAdvice`.

## 🔀 Data Flow

### 📈 Typical Request-Response Flow

1. **Request**: A client makes a request to a REST endpoint (e.g., `/api/results`).
2. **Controller**: Receives and validates the request, extracting parameters and request body.
3. **Service Layer**: Controller delegates to appropriate service(s) for business logic processing.
4. **Data Access**: Service interacts with repositories to query or persist data via JPA.
5. **Transformation**: Service transforms domain entities to DTOs for external consumption.
6. **Response**: Controller wraps the result in a standardised response format and returns JSON to the client.

### 📥 Data Import Flow (CSV/XML Processing)

1. **File Upload**: Client uploads a CSV or XML file to an import endpoint.
2. **Parser Invocation**: Service invokes appropriate parser (WinMSS, IPSC).
3. **Data Extraction**: Parser reads file, applies mapping rules, and validates data.
4. **Entity Creation**: Parsed data is transformed into JPA entities.
5. **Persistence**: Entities are saved to the database via repositories within a transaction.
6. **Response**: Import summary or errors are returned to the client.

## ✅ Quality Attributes

- **Scalability**: Stateless service design with database-backed persistence allows for horizontal scaling.
- **Maintainability**: Strict package naming conventions, clear separation of concerns, and comprehensive test
  coverage ensure long-term maintainability.
- **Robustness**: Multi-layered validation (controller, service, entity) and global error handling prevent
  malformed data from propagating through the system.
- **Testability**: Dependency injection and interface-based design enable comprehensive unit and integration
  testing.
- **Extensibility**: Domain-driven design with enums, helpers, and strategy patterns allows easy addition of
  new features.
- **Data Integrity**: JPA entity relationships with cascading rules and transaction management ensure data
  consistency.

## 📚 Development Guidelines

Refer to the [README.md](README.md) for detailed instructions on local setup, commands, and
coding standards.
