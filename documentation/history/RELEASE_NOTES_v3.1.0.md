# 📋 Release Notes - Version 3.1.0

**Release Date:** February 10, 2026  
**Status:** ✨ Stable

---

## 🎯 Theme

Exception Handling Consolidation

---

## 📖 Overview

A maintenance release focused on improving exception handling consistency, enhancing API documentation, and
fixing critical bugs in XML parsing while maintaining backward compatibility.

---

## ⭐ Key Improvements

### 🔧 Exception Handling Consolidation

- **Merged exception handlers:** Consolidated `Exception` and `RuntimeException` handlers in
  `ControllerAdvice`
- **Simplified validation errors:** Combined `IllegalArgumentException` and `MismatchedInputException`
  handlers
- **Removed redundant handlers:** Eliminated `CsvReadException` handler (covered by generic exception
  handling)
- **Streamlined code:** Reduced `ControllerAdvice` from ~100 lines to ~70 lines while maintaining full
  functionality

### API Documentation Enhancements

- **Enhanced OpenAPI annotations:** Added `@Operation` annotations with clear summary and description
- **Corrected schemas:** Fixed `@RequestBody` schema references for accurate API documentation
- **Improved exception declaration:** Added explicit `throws` declarations for better exception propagation
- **Simplified error handling:** Removed unnecessary try-catch blocks allowing natural exception propagation

### Bug Fixes

- **Fixed XML parsing:** Resolved critical bug where XML parsing errors resulted in null return values
- **Enhanced exception context:** Added proper exception re-throwing to preserve context and error details
- **Improved consistency:** Aligned XML parsing error handling with JSON parsing patterns

---

## Code Quality Improvements

- Simplified exception handling architecture
- Improved error response consistency
- Better alignment with API documentation
- Enhanced code readability

---

## Migration Notes

**Fully backward-compatible:** No migration steps required from v3.0.0.

- **API contracts unchanged:** All endpoint signatures and response formats remain consistent
- **Exception responses unchanged:** Error response structure and HTTP status codes remain the same
- **No database changes:** No schema updates or data migrations required

---

## Dependencies

### Unchanged

- **Spring Boot:** 4.0.3
- **Java:** 25
- **All other dependencies:** Remain the same

---

## Testing Summary

- All existing tests remain valid
- Exception handling tests updated for consolidated handlers
- No new test suites required

---

## Credits

@tahoni

---

**Release Date:** February 10, 2026  
**Status:** Stable  
**Previous Version:** 3.0.0  
**Next Version:** 4.0.0

