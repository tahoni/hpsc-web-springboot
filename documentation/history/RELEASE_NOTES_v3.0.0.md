# 🧾 Release Notes - Version 3.0.0

**Status:** ✨ Stable

---

## 📖 Overview

Major refactoring of the IPSC match results processing system to improve modularity, maintainability, and
testability. Introduces IPSC-specific domain modeling with firearm-type-based classifications.

---

## ⚠️ Breaking Changes

**This is a major version update with breaking changes:**

- **`Discipline` enum removed** in favor of `FirearmType` enum
- **`Division` enum restructured** to align with IPSC firearm-type-specific divisions
- **Discipline-to-division mappers removed:** Multiple mapper classes removed in favor of
  `FirearmTypeToDivisions`
- **`Competitor` entity field renamed:** `category` → `defaultCompetitorCategory`
- **`Match` entity changes:** `matchDivision` field replaced with `matchFirearmType`; club string replaced
  with `Club` entity

---

## ⭐ Key Highlights

### 🎯 Domain Model Refactoring

- **`Club` entity reintroduced:** Added `Club` JPA entity with bidirectional `@OneToMany` relationship to
  `Match`
- **`FirearmType` enum introduced:** New enum representing IPSC firearm types (Handgun, PCC, Rifle, Shotgun,
  Mini Rifle, .22 Handgun)
- **`Division` enum enhanced:** Expanded to include all IPSC divisions with firearm-type classification
  methods
- **CompetitorCategory field added:** Added across `Competitor`, `MatchCompetitor`, and related DTOs
- **Firearm-type mappings:** New mapping classes for divisions by firearm type

### 🔧 Service Layer Improvements

- **`ClubService` introduced:** New service layer for club management operations
- **Enhanced services:** Updated `MatchResultService`, `AwardService`, and transaction handling
- **DTO layer enhancements:** Introduced `ClubDto` and enhanced existing DTOs with new fields

### 🧪 Testing Summary

### ✓ Test Coverage

- **279+ new test lines:** Extensive tests for `FirearmTypeToDivisions` mapper
- **Domain model tests:** Added tests for firearm type, club, and division enumerations
- **Updated test suites:** Enhanced existing tests to reflect new domain structure

### 🎯 Test Scenarios

- ✅ Firearm type classification
- ✅ Division mapping by firearm type
- ✅ Club entity relationships
- ✅ Competitor category handling
- ✅ Match entity initialization
- ✅ Stage max points handling

---

## 🆕 New Components

### 🏛️ Domain Entities

- `Club` entity with bidirectional relationships
- `ClubRepository` for club persistence

### 🔤 Enums

- `FirearmType` - IPSC firearm type classification
- Updated `Division` enum with firearm-type mappings
- Enhanced `CompetitorCategory` enum

### 🔧 Services

- `ClubService` / `ClubServiceImpl` - Club management

### 🧩 Mappers

- `FirearmTypeToDivisions` - Centralized firearm-type to division mapping
- `DivisionsHandgun`, `DivisionsPcc`, `DivisionsRifle`, `DivisionsShotgun`, `DivisionsMiniRifle`,
  `Divisions22Handgun`

### 📦 DTOs

- `ClubDto` - Club data transfer object with multiple constructors

---

## 🗑️ Removed Components

The following classes were removed in this version as they are no longer needed:

- `Discipline` enum
- `DisciplinesInDivision` and all `Disciplines*` classes
- `DivisionToDisciplinesInDivisionMapper`

---

## ✨ Enhanced Components

- `Match` entity - Added firearm type and club relationship
- `MatchStage` entity - Added `maxPoints` field for stage scoring
- `Competitor` entity - Field name standardization
- All related DTOs and repositories

---

## 📦 Migration Guide

### 📋 Migration Notes

**Significant breaking changes require:**

1. **Replace `Discipline` references with `FirearmType`**
    - Update all code referencing the `Discipline` enum to use `FirearmType` instead

2. **Update `Division` handling**
    - Review division-related logic as divisions are now firearm-type-specific
    - Use `FirearmTypeToDivisions` mapper instead of old discipline mappers

3. **Update `Competitor` field access**
    - Replace `category` field access with `defaultCompetitorCategory`

4. **Update `Match` entity access**
    - Replace `matchDivision` with `matchFirearmType`
    - Update club references to use `Club` entity instead of string

5. **Update division-division mapper usage**
    - Remove `DivisionToDisciplinesInDivisionMapper` usage
    - Replace with `FirearmTypeToDivisions` mapper

6. **Database migration required**
    - New `Club` table and updated foreign key relationships
    - Manual migration scripts may be required for production databases

---

## 📦 Dependencies

### ✅ Unchanged

- **Spring Boot:** 4.0.3
- **Java:** 25
- **All other dependencies:** Remain the same

---

## 🧪 Testing Summary

### ✓ Test Coverage

- ✅ Firearm type classification
- ✅ Division mapping by firearm type
- ✅ Club entity relationships
- ✅ Competitor category handling
- ✅ Match entity initialization
- ✅ Stage max points handling

### 🎯 Test Scenarios

- `FirearmTypeTest` - 132+ lines of comprehensive tests
- `FirearmTypeToDivisionsTest` - 279+ lines of mapper tests
- `ClubDtoTest` - 77+ lines of DTO initialization tests
- `ClubReferenceTest` - 99+ lines of enum tests
- `DivisionTest` - Expanded for new firearm-type divisions (194+ lines)
- `CompetitorDtoTest` - Tests for new category field
- `MatchDtoTest` - Tests for club entity mapping
- `MatchStageDtoTest` - Tests for max points field

---

## 📚 Documentation

- Enhanced Javadoc across all domain entities and DTOs
- README.md feature expansion
- ARCHITECTURE.md domain documentation updates

---

## 👥 Credits

@tahoni

---

**Release Date:** February 10, 2026  
**Status:** Stable  
**Previous Version:** 2.0.0  
**Next Version:** 3.1.0
