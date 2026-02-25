# Release Notes - Version 1.1.3

**Release Date:** January 28, 2026  
**Status:** ✨ Stable

---

## 🎯 Theme

Documentation Enhancement & Mapper Centralization

---

## 📖 Overview

Improved maintainability and clarity through expanded Javadoc coverage and introduction of centralized mapping
patterns.

---

## ⭐ Key Highlights

### 🔧 Mapper Centralization

- **Central mapper:** Introduced mapper that resolves each `Division` enum to its corresponding
  `DisciplinesInDivision` singleton
- **Division.NONE:** Added new enum value for "no division" scenarios
- **Improved mappings:** Centralized discipline-to-division resolution
- **Code organization:** Better separation of concerns in mapper architecture

### 📚 Code Improvements

- **Utility classes:** Ensured utility classes cannot be instantiated (via private constructors)
- **String formatting:** Improved formatting and readability in match helper utilities
- **Constants cleanup:** Removed unused constants from `MatchConstants`

### 📚 Documentation

- **Javadoc expansion:** Enhanced documentation across:
    - Domain entities
    - Enumeration types
    - Division model classes (`Disciplines*` classes)
    - Helper utilities

### 🧪 Testing Summary

### ✓ Test Coverage

- **Unit tests:** Added and updated tests to validate mapper behavior
- **Test setup:** Simplified test setup and fixed grammar issues in test code/docs
- **Validation tests:** Added coverage for validation-oriented methods

### 🎯 Test Scenarios

- ✅ Division-to-discipline mapping
- ✅ Division.NONE handling
- ✅ Mapper behavior regression checks

---

## 🛡️ Security & Updates

- **Spring Boot upgrade:** Bumped to version 4.0.2 to address security vulnerabilities
- **IDE cleanup:** Removed unnecessary IDE files (`.idea/data_source_mapping.xml`)
- **Git configuration:** Updated `.gitignore` to prevent similar IDE metadata

---

## 📦 Dependencies

### ⬆️ Updated

- **Spring Boot:** 4.0.2 (security patch)

### ✅ Unchanged

- **Java:** 25
- **All other dependencies:** Remain the same

---

## 📦 Migration Guide

### 📋 Migration Notes

**Fully backward-compatible:** No migration steps required from v1.1.2.

---

## 👥 Credits

@tahoni

---

**Release Date:** January 28, 2026  
**Status:** Stable  
**Previous Version:** 1.1.2  
**Next Version:** 2.0.0
