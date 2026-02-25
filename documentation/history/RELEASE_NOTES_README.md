# Release History & Documentation

Complete historical archive and documentation of all HPSC Website Backend releases.

–––

## 📑 Table of Contents

- [📖 Overview](#-overview)
- [🗺️ Quick Navigation](#-quick-navigation)
    - [🎯 Current Release](#-current-release)
    - [📊 Previous Major Releases](#-previous-major-releases)
    - [🔧 Development Releases](#-development-releases)
- [📋 Comprehensive References](#-comprehensive-references)
- [📁 File Organization](#-file-organization)
- [🎓 How to Use This Documentation](#-how-to-use-this-documentation)
- [📊 Version Summary Table](#version-summary-table)
- [⚠️ Breaking Changes Overview](#-breaking-changes-overview)
- [📊 Feature Matrix](#-feature-matrix)
- [📅 Timeline](#-timeline)
- [📈 Key Metrics](#-key-metrics)
- [🚀 Migration Guides](#-migration-guides)
- [⚠️ Known Issues](#-known-issues-by-version)
- [📚 Related Documentation](#-related-documentation)
- [🤝 Contributing & Support](#-contributing--support)
- [📝 Document Metadata](#-document-metadata)
- [🔗 Quick Links](#-quick-links)

–––

## 📖 Overview

This directory contains comprehensive documentation of the project's evolution from version 1.0.0
(January 4, 2026) through version 5.1.0 (February 25, 2026).

–––

## 🗺️ Quick Navigation

### 🎯 Current Release

— **[Version 5.1.0](RELEASE_NOTES_v5.1.0.md)** – Test Suite Enhancement & Code Quality Consolidation
– **Release Date:** February 25, 2026
– **Status:** Current/Stable
– **Key Features:** Test organisation, duplicate elimination, improved maintainability

### 📊 Previous Major Releases

— **[Version 5.0.0](RELEASE_NOTES_v5.0.0.md)** – Semantic Versioning Transition & Infrastructure
Consolidation (Feb 24, 2026)
– **[Version 4.1.0](RELEASE_NOTES_v4.1.0.md)** – CRUD Enhancement & API Maturity (Feb 13, 2026)
– **[Version 4.0.0](RELEASE_NOTES_v4.0.0.md)** – Domain Refactoring & Quality Assurance
(Feb 11, 2026)⚠️ Breaking Changes
– **[Version 3.1.0](RELEASE_NOTES_v3.1.0.md)** – Exception Handling Consolidation (Feb 10, 2026)
– **[Version 3.0.0](RELEASE_NOTES_v3.0.0.md)** – Domain Model Restructuring & IPSC Specialisation
(Feb 10, 2026)⚠️ Breaking Changes

### 🔧 Development Releases

– **[Version 2.0.0](RELEASE_NOTES_v2.0.0.md)** – Service–Oriented Architecture & Modularity (Feb 8, 2026) ⚠️
Breaking Changes
– **[Version 1.1.3](RELEASE_NOTES_v1.1.3.md)** – Documentation Enhancement & Mapper Centralisation
(Jan 28, 2026)
– **[Version 1.1.2](RELEASE_NOTES_v1.1.2.md)** – Project Documentation (Jan 20, 2026)
– **[Version 1.1.1](RELEASE_NOTES_v1.1.1.md)** – API Clarity & Javadoc Standardization (Jan 16, 2026)
– **[Version 1.1.0](RELEASE_NOTES_v1.1.0.md)** – Award Processing & Core Model Refactoring (Jan 14, 2026)
– **[Version 1.0.0](RELEASE_NOTES_v1.0.0.md)** – Foundation & Image Gallery (Jan 4, 2026)

–––

## 📋 Comprehensive References

### 📖 Project History & Evolution

— **[HISTORY.md](/HISTORY.md)** – Comprehensive project history
– 8 distinct development phases
– Architectural evolution diagrams
– Feature timeline tracking
– Key lessons and design decisions
– Future roadmap implications

### 📚 Complete Release Notes Archive

— **[RELEASE_NOTES_HISTORY.md](RELEASE_NOTES_HISTORY.md)** – Complete archive of all release notes
– All 11 versions in one document
– Version progression summary table
– Breaking changes documentation
– Cumulative feature matrix
– Feature and documentation evolution tables
– Testing coverage progression

–––

## 📁 File Organization

```
documentation/history/
├── README.md                          ← You are here
├── HISTORY.md                         (Project history & evolution)
├── RELEASE_NOTES_HISTORY.md          (Complete release archive)
├── RELEASE_NOTES_v5.1.0.md           (Current release)
├── RELEASE_NOTES_v5.0.0.md
├── v3.1.0.md
├── v3.0.0.md
├── v2.0.0.md
├── v1.1.3.md
├── v1.1.2.md
├── v1.1.1.md
├── v1.1.0.md
└── v1.0.0.md
```

–––

## 🎓 How to Use This Documentation

### 📈 For Users Upgrading Between Versions

1. **Check for breaking changes:**
   – Review the specific version file (e.g., `v4.0.0.md`)
   – Look for the ⚠️ Breaking Changes section
   – Read Migration Notes for an upgrade path

2. **Understand new features:**
   – Review the "Key Features" or "What's New" section
   – Check feature matrix in `RELEASE_NOTES_HISTORY.md`

3. **Plan your upgrade:**
   – Follow specific migration instructions
   – Allocate time for testing
   – Review dependencies changes

### 👥 For New Team Members

1. **Start with the project overview:**
   – Read `HISTORY.md` for complete evolution
   – Review the "Project Philosophy Evolution" section
   – Understand 8 development phases

2. **Learn architectural progression:**
   – Review "Architectural Evolution" diagrams in `HISTORY.md`
   – See how architecture improved over time
   – Understand design decisions

3. **Understand the current state:**
   – Read `v5.0.0.md` for current features
   – Review recent releases (v4.x and v5.0.0)
   – Check known issues and TODOs

### 👔 For Project Managers/Leads

1. **Track project evolution:**
   – Review version progression table in `RELEASE_NOTES_HISTORY.md`
   – Check milestone achievements in `HISTORY.md`
   – Understand feature development timeline

2. **Plan future releases:**
   – Review "Future Roadmap Implications" in `HISTORY.md`
   – Check TODOs in current release notes
   – Plan based on historical pace and themes

3. **Understand testing coverage:**
   – Check testing evolution table in `RELEASE_NOTES_HISTORY.md`
   – See progression from ~30% to ~85% coverage
   – Plan testing investments

### 💻 For Developers

1. **Understand the codebase:**
   – Read `HISTORY.md` evolution sections for understanding changes
   – Review architectural diagrams and progression
   – Understand domain-specific decisions

2. **Learn about recent changes:**
   – Review recent release notes (v4.x – v5.0.0)
   – Check breaking changes documentation
   – Understand the entity initialisation framework

3. **Plan feature development:**
   – Review known TODOs in the current release
   – Check "Key Learnings" section in `HISTORY.md`
   – Understand patterns used in the codebase

–––

## Version Summary Table

| Version | Date | Theme | Status | Breaking Changes |
|–––––––––––|––––––––––––––|––––––––––––––––––––––––––––––––|––––––––|––––––––––––––––––––|
| **5.1.0** | Feb 25, 2026 | Test Suite Enhancement | Stable | ✅ None |
| **5.0.0** | Feb 24, 2026 | Semantic Versioning Transition | Stable | ✅ None |
| **4.1.0** | Feb 13, 2026 | CRUD Enhancement | Stable | ✅ None |
| **4.0.0** | Feb 11, 2026 | Domain Refactoring | Stable | ⚠️ Entity renames |
| **3.1.0** | Feb 10, 2026 | Exception Consolidation | Stable | ✅ None |
| **3.0.0** | Feb 10, 2026 | Domain Specialisation | Stable | ⚠️ Domain changes |
| **2.0.0** | Feb 8, 2026 | Service Architecture | Stable | ⚠️ Service changes |
| **1.1.3** | Jan 28, 2026 | Documentation | Stable | ✅ None |
| **1.1.2** | Jan 20, 2026 | Documentation | Stable | ✅ None |
| **1.1.1** | Jan 16, 2026 | API Clarity | Stable | ✅ None |
| **1.1.0** | Jan 14, 2026 | Award Processing | Stable | ✅ None |
| **1.0.0** | Jan 4, 2026 | Foundation | Legacy | N/A |

–––

## ⚠️ Breaking Changes Overview

### 💥 Major Breaking Changes (Require Code Updates)

— **v4.0.0** – Entity renaming: `Match` → `IpscMatch`, `MatchStage` → `IpscMatchStage`
– **v3.0.0** – Domain restructuring: `Discipline` → `FirearmType`, removed `Club` entity, introduced
firearm–type classifications
– **v2.0.0** – Service refactoring: `IpscService` → `WinMssService`, DTO layer introduction, removed legacy
response models

### ✅ Non–Breaking Releases

— **v5.0.0, v4.1.0, v3.1.0, v1.1.3, v1.1.2, v1.1.1, v1.1.0** – All fully backward compatible

–––

## 📊 Feature Matrix

### 🎯 Feature Availability by Version

| Feature | v1.0 | v1.1 | v2.0 | v3.0 | v4.0 | v4.1 | v5.0 | v5.1 |
|–––––––––––––––––––––|––––––|––––––|––––––|––––––|––––––|––––––|––––––|––––––|
| Image Gallery | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Award Processing | | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Match Management | | | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Competitor Tracking | | | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| IPSC Integration | | | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| CRUD Operations | | | | | | ✅ | ✅ | ✅ |
| Semantic Versioning | | | | | | | ✅ | ✅ |
| Test Organisation | | | | | | | | ✅ |

–––

## 📅 Timeline

```
2026–01–04 ──→ v1.0.0 (Foundation)
               └─→ 2026–01–14 ──→ v1.1.0 (Awards)
                   └─→ 2026–01–16 ──→ v1.1.1 (Javadoc)
                       └─→ 2026–01–20 ──→ v1.1.2 (Docs)
                           └─→ 2026–01–28 ──→ v1.1.3 (Mapper)
                               └─→ 2026–02–08 ──→ v2.0.0 (Services)
                                   └─→ 2026–02–10 ──→ v3.0.0 (Domain)
                                       └─→ 2026–02–10 ──→ v3.1.0 (Exceptions)
                                           └─→ 2026–02–11 ──→ v4.0.0 (Refactor)
                                               └─→ 2026–02–13 ──→ v4.1.0 (CRUD)
                                                   └─→ 2026–02–24 ──→ v5.0.0 (SemVer)
                                                       └─→ 2026–02–25 ──→ v5.1.0 (Tests)
```

**Duration:** 52+ weeks (Jan 4 – Feb 25, 2026)  
**Total Releases:** 12  
**Average Release Frequency:** ~4.5 days

–––

## 📈 Key Metrics

### 🧪 Test Coverage Evolution

— **v1.0.0:** ~30% coverage
– **v1.1.0:** ~40% coverage
– **v2.0.0:** ~60% coverage
– **v3.0.0:** ~65% coverage
– **v4.0.0:** ~75% coverage
– **v4.1.0:** ~80% coverage
– **v5.0.0:** ~85% coverage
– **v5.1.0:** ~85% coverage (improved organisation and consolidation)

### 📚 Documentation Evolution

| Document | v1.0 | v1.1 | v2.0 | v3.0 | v4.0 | v5.0 |
|–––––––––––––––|––––––|––––––|––––––|––––––|––––––|––––––|
| Javadoc | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| README | | ✅ | ✅ | ✅ | ✅ | ✅ |
| ARCHITECTURE | | ✅ | ✅ | ✅ | ✅ | ✅ |
| CHANGELOG | | | | | | ✅ |
| Release Notes | | | | | | ✅ |
| History | | | | | | ✅ |

–––

## 🚀 Migration Guides

### ⬆️ Upgrading to v5.0.0

**From v4.1.0:** ✅ No migration required (drop–in replacement)

**From earlier versions:** Check intermediate version notes for breaking changes

### ⬆️ Upgrading to v4.0.0 from v3.x

⚠️ **Breaking changes:** Entity renames required

— See [v4.0.0.md](RELEASE_NOTES_v4.0.0.md) "Migration Notes" section

### ⬆️ Upgrading to v3.0.0 from v2.0.0

⚠️ **Breaking changes:** Domain restructuring required

— See [v3.0.0.md](RELEASE_NOTES_v3.0.0.md) "Migration Notes" section

### ⬆️ Upgrading to v2.0.0 from v1.x

⚠️ **Breaking changes:** Service architecture changes

— See [v2.0.0.md](RELEASE_NOTES_v2.0.0.md) "Migration Notes" section

–––

## ⚠️ Known Issues by Version

### v5.1.0

— 📝 Database update scenario in tests disabled pending architecture review
– 🧪 Additional test scenarios for extended service layers (marked for v5.2.0)

### v5.0.0

— 📝 Javadoc gaps in some protected methods (partially addressed in v5.1.0)
– 🧪 Additional test scenarios for edge cases (test consolidation completed in v5.1.0)

### v4.1.0

– ✅ None documented

### v4.0.0

– ✅ None documented

### Earlier Versions

— Refer to individual version files for details

–––

## 📚 Related Documentation

### 🏠 Root Directory

— **[/README.md](/README.md)** – Project overview
– **[/ARCHITECTURE.md](/ARCHITECTURE.md)** – System architecture
– **[/RELEASE_NOTES.md](/RELEASE_NOTES.md)** – Current release notes
– **[/CHANGELOG.md](/CHANGELOG.md)** – Technical changelog

### 📋 Archive Directory

— **[/archive/ARCHIVE.md](/documentation/archive/ARCHIVE.md)** – Legacy release archive (v1.0.0 – v4.1.0)

–––

## 🤝 Contributing & Support

### 🔍 Found an Issue?

Please report issues at:
[GitHub Issues](https://github.com/tahoni/hpsc–web–springboot/issues)

### 💬 Have Questions?

For questions about specific releases:

1. Check the individual version file (e.g., `v5.0.0.md`)
2. Review `HISTORY.md` for architectural context
3. Check `RELEASE_NOTES_HISTORY.md` for feature matrix

### 👥 Want to Contribute?

Please follow the contribution guidelines in the main repository.

–––

## 📝 Document Metadata

— **Created:** February 24, 2026
– **Last Updated:** February 25, 2026
– **Coverage:** v1.0.0 – v5.1.0 (12 releases)
– **Timeline:** January 4 – February 25, 2026 (52+ weeks)
– **Total Files:** 14 documentation files
– **Total Content:** ~2,300+ lines

–––

## 🔗 Quick Links

– 📖 [Architecture Guide](/ARCHITECTURE.md)
– 📋 [Current Release Notes](/RELEASE_NOTES.md)
– 📝 [Change Log](/CHANGELOG.md)
– 📚 [Project History](/HISTORY.md)
– 🗂️ [Release Archive](RELEASE_NOTES_HISTORY.md)

–––

**Last Updated:** February 25, 2026  
**Status:** Complete and Current

