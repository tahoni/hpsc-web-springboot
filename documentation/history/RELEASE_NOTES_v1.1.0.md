# 📋 Release Notes - Version 1.1.0

**Release Date:** January 14, 2026  
**Status:** ✨ Stable

---

## 🎯 Theme

Award Processing & Core Model Refactoring

---

## 📖 Overview

Introduced comprehensive support for award processing with CSV support and refactored core models for better
consistency. Integrated OpenAPI documentation and significantly improved test coverage.

---

## ⭐ Key Features

### 🏆 Award Processing System

- **Award processing:** New `HpscAwardService` for award processing logic
- **CSV support:** Award CSV data processing with flexible column mapping
- **Award models:** New request/response models:
    - `AwardRequest`
    - `AwardResponse`
    - Related model classes
- **`AwardController`:** New API endpoints for award operations
- **Award grouping:** `AwardCeremonyResponse` for structured award ceremony data

### 🏗️ Core Model Refactoring

- **Base classes:** Introduced generic `Request` and `Response` base classes for metadata standardization
- **Error handling:** Standardized error responses using new `ErrorResponse` model
- **Validation:** Enhanced field validation across all models using:
    - `@NotNull` annotation
    - `@NotBlank` annotation
    - Additional validation annotations

- **Utilities:** Introduced `ValueUtil` for consistent null-to-default initialization

### 📖 API Documentation

- **OpenAPI integration:** Integrated `springdoc-openapi` for automatic API documentation
- **Javadoc expansion:** Extensive Javadoc comments across:
    - Models
    - Services
    - Utilities
- **Controller annotations:** Detailed annotations on controllers and models for documentation generation

---

## 🆕 New Components

### 🔧 Services

- `HpscAwardService` - Core award processing logic
- Related service implementations

### 📊 Models

- Base `Request` class - Generic request model
- Base `Response` class - Generic response model
- `ErrorResponse` - Standardized error response model
- Award-specific models (AwardRequest, AwardResponse, etc.)

### 🔌 Utilities

- `ValueUtil` - Consistent null-to-default initialization

---

## 🧪 Testing & Quality

- **Award Service tests:** Added comprehensive unit tests for `HpscAwardService`
- **Response tests:** Added tests for `AwardResponse` and `AwardCeremonyResponse`
- **Image Service tests:** Improved `HpscImageServiceTest` with:
    - Detailed assertions for file paths
    - Tag validation
    - Description validation

- **Overall coverage:** Expanded test coverage for new features

---

## ⚙️ Technical Changes

### Maven Configuration

- **Maven plugin:** Added `springdoc-openapi-maven-plugin` for OpenAPI documentation generation during
  integration-test phase
- **Configuration:** Added OpenAPI configuration settings

### 📚 Code Quality

- **Documentation:** Comprehensive Javadoc comments throughout
- **Null safety:** Enhanced null checking and validation
- **Consistency:** Standardized patterns across award and image processing

---

## 📦 Dependencies

### New

- **springdoc-openapi:** For OpenAPI documentation generation and Swagger UI

### Updated

- **Spring Boot:** 4.0.2

### Unchanged

- **Java:** 25

---

## 🔗 API Endpoints

### 🏆 Award Processing

- **POST /awards** - Create or process awards
- **GET /awards** - Retrieve award data
- **POST /award-ceremonies** - Group awards by ceremony

### 🖼️ Image Processing (Enhanced)

- Existing endpoints improved with better error handling

---

## 📦 Migration Guide

**Partially breaking changes** - Core model restructuring:

1. **Request/Response base classes:** Use new base classes for consistency
2. **Error responses:** Updated error response handling through `ErrorResponse`
3. **Validation:** Add validation annotations to custom models

**No database schema changes required** - DTOs and models are orthogonal to persistence layer.

---

## 🧪 Testing Summary

- New test suites for award processing
- Enhanced test coverage for image processing
- Comprehensive validation testing
- Error scenario coverage

---

## 👥 Credits

@tahoni

---

**Release Date:** January 14, 2026  
**Status:** Stable  
**Previous Version:** 1.0.0  
**Next Version:** 1.1.1

