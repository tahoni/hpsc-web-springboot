# Architecture Documentation

This document outlines the architectural design of the HPSC Back-end application.

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

### 4. Exception & Error Handling (`za.co.hpsc.web.exceptions`)

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

```