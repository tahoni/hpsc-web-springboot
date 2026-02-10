# HPSC Website Backend

## Release Notes

### Version 3.1.0 - _2026-02-10_

A maintenance release focused on improving exception handling consistency, enhancing API documentation, and
fixing critical bugs in XML parsing. This release simplifies the global exception handling architecture while
maintaining backward compatibility.

#### Enhancements and Updates

**_Exception Handling Improvements_**

- **Consolidated exception handlers**: Merged `Exception` and `RuntimeException` handlers in
  `ControllerAdvice` into a single unified method for consistent error responses
- **Simplified validation error handling**: Combined `IllegalArgumentException` and `MismatchedInputException`
  handlers to reduce code duplication
- **Removed redundant handlers**: Eliminated `CsvReadException` handler as it is now covered by the generic
  exception handling mechanism
- **Code reduction**: Streamlined `ControllerAdvice` from approximately 100 lines to 70 lines while
  maintaining full functionality

**_API Documentation Enhancements_**

- **Enhanced OpenAPI annotations**: Added `@Operation` annotation to `importWinMssCabData` endpoint in
  `IpscController` with clear summary and description
- **Corrected request body schema**: Fixed `@RequestBody` schema reference from `ControllerResponse` to
  `IpscRequest` for accurate API documentation
- **Improved exception declaration**: Added explicit `throws ValidationException, FatalException` declarations
  for better exception propagation and API contract clarity
- **Simplified exception handling**: Removed unnecessary try-catch block in `IpscController`, allowing
  exceptions to propagate naturally to global exception handler

#### Bug Fixes

**_XML Parsing Error Handling_**

- **Fixed missing return statement**: Resolved critical bug in `WinMssServiceImpl` where XML parsing errors
  could result in null return values
- **Enhanced exception propagation**: Added proper `ValidationException` re-throwing to preserve exception
  context and error details
- **Improved error consistency**: Aligned XML parsing error handling with JSON parsing patterns for consistent
  behaviour across data formats

#### Configuration Changes

- **Version updates**: Bumped an application version from 3.0.0 to 3.1.0 in `pom.xml` and
  `HpscWebApplication.java`

#### Code Quality Improvements

- **Exception handling architecture**: Simplified and standardised exception handling patterns across the
  application
- **Error propagation**: Enhanced exception flow from service layer through controller layer to global
  exception handler
- **Documentation accuracy**: Improved alignment between code behaviour and API documentation

#### Migration Notes

This release is fully backward-compatible with version 3.0.0. No migration steps are required.

- **API contracts unchanged**: All endpoint signatures and response formats remain consistent
- **Exception responses unchanged**: Error response structure and HTTP status codes remain the same
- **No database changes**: No schema updates or data migrations required

#### Changes by

@tahoni

🤖 Generated with [Claude Code](https://claude.com/claude-code)
