# Release Notes - Version 1.1.0

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

## ⭐ Key Highlights

### 🏆 Award Processing System

- **Award processing:** New `HpscAwardService` for award processing logic
- **CSV support:** Award CSV data processing with flexible column mapping
- **Award models:** New request/response models:
    - `AwardRequest`
    - `AwardResponse`
    - Related model classes
- **`AwardController`:** New API endpoints for award operations
- **Award grouping:** `AwardCeremonyResponse` for structured award ceremony data

### 📦 Core Model Refactoring

- **Base classes:** Introduced generic `Request` and `Response` base classes for metadata standardisation
- **Error handling:** Standardized error responses using new `ErrorResponse` model
- **Validation:** Enhanced field validation across all models using:
    - `@NotNull` annotation
    - `@NotBlank` annotation
    - Additional validation annotations

- **Utilities:** Introduced `ValueUtil` for consistent null-to-default initialization

### 📚 API Documentation

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

## 🧪 Testing Summary

### ✓ Test Coverage

- **Award Service tests:** Added comprehensive unit tests for `HpscAwardService`
- **Response tests:** Added tests for `AwardResponse` and `AwardCeremonyResponse`
- **Image Service tests:** Improved `HpscImageServiceTest` with detailed assertions
- **Overall coverage:** Expanded test coverage for new features

### 🎯 Test Scenarios

- ✅ Award processing CSV inputs
- ✅ Award ceremony grouping
- ✅ Image service validation (file paths, tags, descriptions)
- ✅ Error handling and validation paths

---

## 🔧 Technical Enhancements

### 📚 Maven & Configuration

- **Maven plugin:** Added `springdoc-openapi-maven-plugin` for OpenAPI documentation generation during
  integration-test phase
- **Configuration:** Added OpenAPI configuration settings

### 📚 Code Quality

- **Documentation:** Comprehensive Javadoc comments throughout
- **Null safety:** Enhanced null checking and validation
- **Consistency:** Standardised patterns across award and image processing

---

## 📦 Dependencies

### 🏢 New

- **springdoc-openapi:** For OpenAPI documentation generation and Swagger UI

### ⬆️ Updated

- **Spring Boot:** 4.0.2

### ✅ Unchanged

- **Java:** 25

---

## 🔗 API Endpoints

### 🏆 Award Processing

- **POST /awards** – Create or process awards
- **GET /awards** - Retrieve award data
- **POST /award-ceremonies** - Group awards by ceremony

### 🖼️ Image Processing (Enhanced)

- Existing endpoints improved with better error handling

---

## 📦 Migration Guide

**Partially breaking changes** – Core model restructuring:

1. **Request/Response base classes:** Use new base classes for consistency
2. **Error responses:** Updated error response handling through `ErrorResponse`
3. **Validation:** Add validation annotations to custom models

**No database schema changes required** – DTOs and models are orthogonal to the persistence layer.

---

## 👥 Credits

@tahoni

---

**Release Date:** January 14, 2026  
**Status:** Stable  
**Previous Version:** 1.0.0  
**Next Version:** 1.1.1
