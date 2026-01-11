# Architecture Documentation

This document outlines the architectural design and technical stack of the HPSC Website Back-end.

## High-Level Overview

The application follows a standard N-tier architecture, optimized for Spring Boot's ecosystem. It is designed
to be a lightweight yet robust back-end service that processes structured data (CSV) and serves it via RESTful
endpoints.

## Technical Stack

- **Language**: Java 25
- **Framework**: Spring Boot 4.0.1
- **Build Tool**: Maven
- **Data Formats**: JSON (API), CSV (Data Input)
- **Documentation**: SpringDoc OpenAPI (Swagger), Spring REST Docs
- **Utilities**: Lombok, Jackson (CSV & Databind)

## Layered Architecture

### 1. Presentation Layer (Controllers)

Located in `za.co.hpsc.web.controllers`. These classes handle HTTP requests, validate input
headers/parameters, and delegate to the service layer.

- `ImageController`: Handles image gallery data requests.
- `AwardController`: Manages award-related information.

### 2. Service Layer (Business Logic)

Located in `za.co.hpsc.web.services`. This layer contains the core business rules.

- **ImageService**: Orchestrates the parsing and transformation of CSV data into structured `ImageResponse`
  objects.
- **CSV Processing**: Uses Jackson CSV for high-performance parsing with custom logic for MIME type detection
  and tag extraction.

### 3. Model Layer (Data Transfer)

Located in `za.co.hpsc.web.models`.

- Defines the contract between the front-end and back-end.
- Uses `ImageResponseHolder` as a wrapper for bulk data transfers.

### 4. Cross-Cutting Concerns

- **Error Handling**: Centralized in `ApiControllerAdvice`, providing consistent JSON error responses for
  `ValidationException`, `FatalException`, and `CsvReadException`.
- **Validation**: Leverages `spring-boot-starter-validation` for declarative bean validation.

## Data Flow: CSV to API

1. **Request**: The client sends a request (optionally containing CSV data or triggering a CSV read).
2. **Parsing**: `ImageService` parses the raw CSV string. It handles variations in column order and missing
   data gracefully.
3. **Transformation**: The service maps CSV rows to `ImageResponse` DTOs, inferring MIME types based on file
   extensions.
4. **Response**: The `ImageController` wraps the result in an `ImageResponseHolder` and returns it as JSON.

## Design Principles

- **Fail-Fast**: Strict validation ensures that malformed data is caught early with clear error messages.
- **Maintainability**: Clear separation between parsing logic, business rules, and API definitions.
- **Extensibility**: The service-interface pattern allows for easy replacement or extension of data processing
  logic.

```
