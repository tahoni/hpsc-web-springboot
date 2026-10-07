# HPSC Website Backend History Archive (v1.0.0 – v4.1.0)

The per-version narrative history of versions 1.0.0 to 4.1.0 of the HPSC Website Backend project, archived from
[`../../../HISTORY.md`](/HISTORY.md) to keep that file a manageable size. The entries are moved unchanged, grouped under
the `../../../HISTORY.md` section each came from. See [`HISTORY.md`](/HISTORY.md) for the sections that span every
version (Feature Timeline, Key Learnings, Project Philosophy Evolution and Conclusion) and for versions 10.0.0 onwards,
[`CHANGELOG_v1-v4.md`](/documentation/archive/v1-v4/CHANGELOG_v1-v4.md) for the archived change log, [`EVOLUTION_OVERVIEW_v1-v4.md`](/documentation/archive/v1-v4/EVOLUTION_OVERVIEW_v1-v4.md) for the archived Phase-by-phase narrative and
[`HISTORY_v5-v7.md`](/documentation/archive/v5-v7/HISTORY_v5-v7.md) for versions 5.0.0 to 7.4.1.

---

## Table of Contents

- [📅 Historical Timeline](#-historical-timeline)
- [🎯 Major Milestones](#-major-milestones)
- [🏛️ Architectural Evolution](#-architectural-evolution)

---

## 📅 Historical Timeline

### Version 4.1.0 (February 13, 2026)

**Theme:** CRUD Enhancement & API Maturity

### Version 4.0.0 (February 11, 2026)

**Theme:** Domain Refactoring & Quality Assurance

### Version 3.1.0 (February 10, 2026)

**Theme:** Exception Handling Consolidation

### Version 3.0.0 (February 10, 2026)

**Theme:** Domain Model Restructuring & IPSC Specialisation

### Version 2.0.0 (February 8, 2026)

**Theme:** Service-Oriented Architecture & Modularity

### Version 1.1.3 (January 28, 2026)

**Theme:** Documentation Enhancement & Mapper Centralisation

### Version 1.1.2 (January 20, 2026)

**Theme:** Project Documentation

### Version 1.1.1 (January 16, 2026)

**Theme:** API Clarity & Javadoc Standardisation

### Version 1.1.0 (January 14, 2026)

**Theme:** Award Processing & Core Model Refactoring

### Version 1.0.0 (January 4, 2026)

**Theme:** Foundation & Image Gallery

---

## 🎯 Major Milestones

### Milestone 1: Project Foundation (v1.0.0)

- Initial Spring Boot application
- Image gallery CSV processing
- Basic API infrastructure
- Custom exception hierarchy

**Achievement:** Established the foundation for the HPSC platform with core image processing capabilities.

---

### Milestone 2: Feature Expansion (v1.1.0 - v1.1.3)

- Award processing system
- OpenAPI documentation
- Comprehensive project documentation
- Code quality standards

**Achievement:** Expanded platform features and established professional documentation standards.

---

### Milestone 3: Architectural modernisation (v2.0.0)

- Service-oriented architecture
- Comprehensive DTO layer
- Modular service design
- Transaction management

**Achievement:** Transformed from monolithic to modular architecture enabling better maintainability and testing.

---

### Milestone 4: Domain Specialisation (v3.0.0)

- IPSC-specific domain modelling
- Firearm-type classification
- Club entity reintroduction
- Comprehensive enum utilities

**Achievement:** Aligned domain model with IPSC standards for specialised shooting competition management.

---

### Milestone 5: Quality & Simplification (v3.1.0)

- Exception handling consolidation
- API documentation accuracy
- Error handling consistency
- Simplified architecture

**Achievement:** Improved code quality and simplified error handling while maintaining functionality.

---

### Milestone 6: Domain Clarity (v4.0.0)

- Entity naming clarification
- Comprehensive test coverage
- Enhanced validation layers
- IPSC entity specialisation

**Achievement:** Clarified domain model through explicit entity naming (Match → IpscMatch) improving code clarity.

---

### Milestone 7: Feature Completeness (v4.1.0)

- Full CRUD operations
- Complete API maturity
- Transactional consistency
- Production readiness

**Achievement:** Completed CRUD lifecycle enabling full data management capabilities.

---

---

## 🏛️ Architectural Evolution

### v1.0.0: Monolithic Foundation

```
Controller → Service → Repository → Entity
         ↓
      Models
         ↓
   Exception Handlers
```

**Characteristics:**

- Single service for image processing
- Direct controller-service-repository flow
- Basic entity relationships
- Centralised exception handling

---

### v2.0.0: Modular Services

```
           Controller
              ↓
    ┌─────────┴──────────┐
    ↓                    ↓
WinMssService    MatchResultService
    ↓                    ↓
 Repository     TransactionService
    ↓                    ↓
 Entity        DomainServices
    ↓                    ↓
   DTOs          Models/DTOs
```

**Characteristics:**

- Specialised services for different domains
- DTO layer for data transfer
- Transaction abstraction
- Improved separation of concerns

---

### v3.0.0: Domain-Specific Models

```
       IPSC Controller
            ↓
    ┌───────┴────────┐
    ↓                ↓
IpscService    DomainService
    ↓                ↓
 Firearm      Club    Match    Stage
 Types        ↓        ↓        ↓
 (Enums)    Entity  Entity   Entity
    ↓         ↓        ↓        ↓
Repository  Repository
```

**Characteristics:**

- IPSC-specific domain modelling
- Firearm-type classification
- Club entity relationship
- Specialised enums for IPSC

---

### v4.0.0: Explicit IPSC Focus

```
       IpscController
            ↓
    ┌───────┴────────┐
    ↓                ↓
IpscMatchService  DomainService
    ↓                ↓
 IpscMatch*    IpscMatch*Stage
 Repository    Repository
    ↓                ↓
 IpscMatch*    IpscMatch*Stage
   Entity         Entity
```

**Characteristics:**

- Explicit IPSC entity naming
- Comprehensive validation layers
- Enhanced test coverage
- Clear domain boundaries

---
