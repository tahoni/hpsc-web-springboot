# HPSC Website Backend Architecture

This document describes the architectural design, directory structure, and core concepts
of the Hartbeespoortdam Practical Shooting Club (HPSC) Spring Boot backend.

## Table of Contents

- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [System Overview](#system-overview)
- [Layered Structure](#layered-structure)
    - [Presentation Layer](#1-presentation-layer-zacohpscwebcontrollers)
    - [Service Layer](#2-service-layer-zacohpscwebservices)
    - [Model Layer](#3-model-layer-zacohpscwebmodels)
    - [Exception and Error Handling](#4-exception-and-error-handling-zacohpscwebexceptions)
- [Key Design Patterns](#key-design-patterns)
- [Data Flow](#data-flow)
- [Quality Attributes](#quality-attributes)
- [Development Guidelines](#development-guidelines)

## Technology Stack

The application is built using modern technologies:

- **Framework**: [Spring Boot 4](https://spring.io/projects/spring-boot)
- **Build Tool**: [Maven 3.9](https://maven.apache.org/)
- **Language**: [Java 25](https://www.oracle.com/java/)

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
│   │   │                   ├───exceptions      # Global and domain-specific error handlers
│   │   │                   ├───models          # DTOs and domain models used across layers
│   │   │                   ├───services        # Service interfaces and business logic
│   │   │                   │   └───impl        # Concrete service implementations
│   │   │                   └───utils           # Helper utilities and common functions
│   │   └───resources                           # Application resources
│   │       └───static                          # Static assets served by the application.
│   └───test                                    # Unit/integration tests source 
│       └───java                                # Java test source files
│           └───za                              # Root package
│               └───co                          # Company domain
│                   └───hpsc                    # Project namespace
│                       └───web                 # Application test packages
│                           ├───models          # Tests of DTOs and domain models used across layers
│                           ├───services        # Tests of service interfaces and business logic
│                           │   └───impl        # Tests of concrete service implementations
│                           └───utils           # Tests of helper utilities and common functions
```

## System Overview

The application follows a classic **N-Tier Architecture** pattern, ensuring a clean separation of concerns
between the entry points, business logic, and data processing layers.

## Layered Structure

### 1. Presentation Layer (`za.co.hpsc.web.controllers`)

- Responsible for handling incoming HTTP requests.
- Maps REST endpoints to service layer methods.
- Handles Request/Response DTO mapping and basic input validation via Spring's validation framework.

### 2. Service Layer (`za.co.hpsc.web.services`)

- Contains the core business logic.
- Orchestrates data flow between the controllers and the data processing components.
- Implementation of the **CSV Processing Engine**, which transforms raw flat-file data into structured domain
  objects.

### 3. Model Layer (`za.co.hpsc.web.models`)

- Defines the data structures used throughout the application.
- Includes **DTOs** (Data Transfer Objects) for external communication and internal domain representations for
  data processing.

### 4. Exception and Error Handling (`za.co.hpsc.web.exceptions`)

- Global exception handling mechanism using `@ControllerAdvice`.
- Translates internal processing errors and validation failures into standardised, user-friendly JSON
  responses.

## Key Design Patterns

- **Strategy Pattern**: Used in CSV parsing to handle different column ordering and data formats dynamically.
- **Inference Logic**: The system includes a MIME-type inference engine to automatically classify media based
  on file extensions or metadata.
- **Global Error Handling**: Centralised management of application state and error feedback.

## Data Flow

1. **Request**: A client makes a request to a REST endpoint (e.g., `/api/gallery`).
2. **Processing**: The Controller delegates to the Service. If data is sourced from CSV, the Service invokes
   the CSV Parser.
3. **Transformation**: The CSV Parser reads the source, applies mapping rules, and produces Model objects.
4. **Response**: The Service returns the processed data to the Controller, which sends a JSON response to the
   client.

## Quality Attributes

- **Scalability**: Stateless service design allows for horizontal scaling.
- **Maintainability**: Strict package naming conventions and separation of business logic from transport
  protocols.
- **Robustness**: Detailed validation at the entry point and during data parsing prevents malformed data from
  propagating through the system.

## Development Guidelines

Refer to the [README.md](./README.md) for detailed instructions on local setup, commands, and
coding standards.
