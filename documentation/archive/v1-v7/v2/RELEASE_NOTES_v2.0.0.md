# 🧾 Release Notes – Version 2.0.0

**Status:** ✨ Stable

---

## 🎯 Theme

Service-Oriented Architecture & Modularity

---

## 📖 Overview

Major refactoring of the IPSC match results processing system to introduce a service-oriented architecture
with dedicated DTOs, removing legacy code and enhancing documentation.

---

## ⭐ Key Highlights

### 🔄 Architecture & Services

- **New Service Layer:** Introduced `WinMssService` (replacing `IpscService`), `MatchResultService`,
  `TransactionService`, `IpscMatchService`
- **Specialised services:** New services for `Competitor`, `MatchCompetitor`, `MatchStage`,
  `MatchStageCompetitor`
- **Modular Processing:** Broke down monolithic match processing logic into discrete, testable methods
- **Transaction Management:** Added transactional support with dedicated `TransactionService`

### 📊 Domain Model Improvements

- **`Club` entity removed:** Replaced with simpler `ClubReference` enum
- **Enhanced Models:** Added fields for timestamps, scoring, ranking, and competitor categories
- **DTO Layer:** Introduced comprehensive DTOs:
    - `MatchDto`
    - `MatchResultsDto`
    - `CompetitorDto`
    - `MatchStageDto`
    - `MatchStageCompetitorDto`
    - `MatchCompetitorDto`
- **UUID Mapping:** Implemented UUID-based mapping between requests and domain objects

### 🔀 Request/Response Refactoring

- **Unified Models:** Consolidated XML and JSON request models by removing `-ForXml` variants
- **Modular Responses:** Replaced monolithic response objects with specialised responses
- **Enhanced Mapping:** Added constructors for request-to-response mappings
- **XmlDataWrapper:** Introduced for generic XML parsing

---

## 🗑️ Removed Components

- **Legacy `IpscService`** - Replaced with `WinMssService`
- **Legacy response models** – Replaced with a DTO-based approach
- **`Club` entity** - Replaced with `ClubReference` enum
- **`DateUtil` class** - Functionality inlined into utilities

---

## 🆕 New Components

### 🔧 Services

- `WinMssService` - CAB file and XML processing
- `MatchResultService` - Core match result transformation
- `TransactionService` - Transaction management abstraction
- `IpscMatchService` - IPSC-specific match operations
- Domain-specific services for Competitor, Stage, and relationships

### 📦 DTOs

- `MatchDto` - Match data transfer
- `MatchResultsDto` - Combined match and result data
- `CompetitorDto` - Competitor information
- `MatchStageDto` - Stage details
- And related association DTOs

### 📦 Models

- `XmlDataWrapper` - Generic XML processing wrapper
- Enhanced request/response models

---

## 🔧 Technical Enhancements

### 📚 Documentation

- **Javadoc expansion:** Added comprehensive Javadoc comments across services, models, and DTOs
- **Code comments:** Enhanced documentation throughout the codebase

### 📚 Code Quality

- **Code Style:** Enforced 110-character line wrapping for improved readability
- **Null Safety:** Introduced null checks in service layer and domain classes
- **Constants:** Renamed `CompetitorConstants` to `MatchLogConstants`; added `IpscConstants`

### 🧪 Testing Summary

### ✓ Test Coverage

- **Comprehensive Test Coverage:** Added tests for `WinMssServiceImpl`, `MatchResultServiceImpl`,
  `IpscMatchService`
- **Edge case handling:** Tests for error conditions and unusual inputs

### 🎯 Test Scenarios

- ✅ XML/JSON parsing
- ✅ Null handling
- ✅ Initialization logic
- ✅ Transactional behavior

---

## ⚙️ Configuration Changes

- **Application Config:** Added case-insensitivity for JSON property names

---

## 📦 Dependencies

### ⬆️ Updated

- **Dependencies:** Updated `../../../../pom.xml` with required dependencies for enhanced XML/JSON processing

### ✅ Unchanged

- **Spring Boot:** 4.0.3
- **Java:** 25

---

## 📦 Migration Guide

### 📋 Migration Notes

**Significant breaking changes:**

1. **Service references:** Update from `IpscService` to `WinMssService`
2. **Response models:** Update from legacy models to new DTOs
3. **Club entity:** Remove any club entity references, use `ClubReference` enum instead
4. **Date handling:** Update any `DateUtil` usages to inline date handling

**No database schema changes are required** – domain entities and relationships remain compatible.

---

## 🧪 Testing Summary

### ✓ Test Coverage

- New comprehensive test suites for all major services
- Test coverage for XML/JSON parsing

### 🎯 Test Scenarios

- ✅ Transaction handling
- ✅ Entity initialisation and persistence tests

---

## 👥 Credits

@tahoni

---

**Release Date:** February 8, 2026  
**Status:** Stable  
**Previous Version:** 1.1.3  
**Next Version:** 3.0.0
