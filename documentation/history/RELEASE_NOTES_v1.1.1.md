# Release Notes – Version 1.1.1

**Status:** ✨ Stable

---

## 🎯 Theme

API Clarity & Javadoc Standardisation

---

## 📖 Overview

Improves code maintainability and API clarity through standardised Javadoc documentation across key
components.

---

## ⭐ Key Highlights

### 📚 Controller Documentation

- **`AwardController`:**
    - Refined class-level documentation
    - Detailed method documentation for CSV processing endpoints
    - Clear input requirements and return types

- **`ImageController`:**
    - Enhanced parameter descriptions for CSV processing
    - Consistent documentation with `AwardController`

- **Parameter clarity:** Detailed input requirements and return types in method documentation

### 🛡️ Exception Documentation

- **`FatalException` and `NonFatalException`:**
    - Standardised Javadoc comments to match Java's core exception patterns
    - Clear exception purpose and usage documentation
    - Removed unnecessary imports

### 🧰 Model Layer Documentation

- **Javadoc annotations:** Improved annotations and validation constraints across models
- **Nullability descriptions:** Standardised descriptions to improve IDE assistance
- **API documentation:** Enhanced clarity for OpenAPI documentation generation
- **Field documentation:** Clear descriptions of all model fields

---

## 🧪 Testing Summary

### ✓ Test Coverage

- IDE assistance: Improved IDE autocomplete and inline documentation
- Documentation generation: Better OpenAPI/Swagger documentation output
- Code clarity: Clearer code navigation and understanding

### 🎯 Test Scenarios

- ✅ Verify Javadoc rendering in IDE tooling
- ✅ Validate OpenAPI/Swagger output after annotation updates
- ✅ Confirm documentation consistency across controllers

---

## 📚 Documentation

- **Consistent style:** Standardised Javadoc format across all classes
- **Complete coverage:** All public methods documented
- **Clear examples:** Where applicable, usage examples provided

---

## 📦 Dependencies

### ✅ Unchanged

- **Spring Boot:** 4.0.2
- **Java:** 25
- **All other dependencies:** Remain the same

---

## 📦 Migration Guide

### 📋 Migration Notes

**Fully backward-compatible:** No migration steps are required from v1.1.0.

---

## 👥 Credits

@tahoni

---

**Release Date:** January 16, 2026  
**Status:** Stable  
**Previous Version:** 1.1.0  
**Next Version:** 1.1.2
