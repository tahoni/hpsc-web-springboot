# Improvement Plan

This document synthesises the goals and constraints stated across this repository's documentation and configuration into a single set of prioritised improvement opportunities. Unlike [`../../README.md`](/README.md) and [`../../ARCHITECTURE.md`](/ARCHITECTURE.md), it is not evergreen — it reflects a point-in-time reading of the project and should be revisited whenever a major gap it names is closed or a new one is identified.

## Table of Contents

- [🎯 Purpose & Scope](#-purpose--scope)
- [⚙️ Goals & Constraints (Synthesised)](#-goals--constraints-synthesised)
- [🔍 Gaps & Improvement Opportunities](#-gaps--improvement-opportunities)
- [🚀 Roadmap](#-roadmap)
- [✅ Success Criteria](#-success-criteria)
- [📚 Related Documentation](#-related-documentation)

---

## 🎯 Purpose & Scope

This plan draws only on what the repository already states about itself — `../../README.md`, `../../ARCHITECTURE.md`, `../../AGENTS.md`, `../../CLAUDE.md`, `../../CONTRIBUTING.md`, `../../HISTORY.md`'s Future Roadmap sections, `../../pom.xml`, `application*.properties`, and `../../.github/workflows` — rather than introducing new goals. Where the documentation and the configuration disagree, or where a stated goal has no corresponding work item yet, that gap is called out below as an improvement opportunity.

It complements, rather than duplicates, `../../HISTORY.md`'s per-release "🚀 Future Roadmap Implications" section: that section tracks what changed release-to-release, while this document tracks the standing, cross-release gaps between the project's stated intent and its current state.

---

## ⚙️ Goals & Constraints (Synthesised)

| Source                                                          | Goal / constraint                                                                                                                                                                                                                                                                                          |
|-----------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `../../README.md`, `../../ARCHITECTURE.md`                      | Rebuild the match/competitor domain's service and controller layer on top of the existing JPA entities and repositories — explicitly called out as in-progress, not aspirational                                                                                                                           |
| `../../ARCHITECTURE.md` (Layered Architecture)                  | Strict unidirectional layering: Controller → Service → Repository → Database; no layer may skip the one below it, and controllers must carry no business logic                                                                                                                                             |
| `../../ARCHITECTURE.md` (Exception handling), `../../CLAUDE.md` | All exceptions extend `FatalException`, `NonFatalException`, or `ValidationException`, handled centrally by `ControllerAdvice` — never caught and rethrown as generic `RuntimeException`                                                                                                                   |
| `../../ARCHITECTURE.md` (CI/CD & Quality Gates)                 | Security analysis (CodeQL) and code coverage (JaCoCo) are established quality gates; `./mvnw test` is documented as reviewer/local-only, not an automatic gate                                                                                                                                             |
| `../../AGENTS.md` (Git Workflow, Release Checklist)             | GitFlow branching (`develop` → `release/vX.Y.Z` → `main`, `hotfix/*` direct to `main`), Semantic Versioning, and a fixed, ordered release checklist covering `../../pom.xml`, `HpscWebApplication.java`, `../../CHANGELOG.md`, `../../HISTORY.md`, `../../RELEASE_NOTES.md`, and archived per-version docs |
| `../../AGENTS.md` (Documentation Conventions)                   | British English spelling throughout prose and Javadoc; every heading carries a reused or deliberately new emoji; `../../README.md`/`../../ARCHITECTURE.md` stay version-agnostic (reverse-synced from release docs, not the other way round)                                                               |
| `../../AGENTS.md` (Test Conventions), `../../CLAUDE.md`         | Mockito-only controller tests (no Spring context), H2-backed service/repository integration tests, `<ClassName>Test` / `test<Scenario>_when<Condition>_then<Expectation>` naming, AssertJ unavailable (excluded in `../../pom.xml`)                                                                        |
| `../../pom.xml`                                                 | Track current Spring Boot / Java releases closely (Java 25, Spring Boot 4.1.0) — this currency itself creates a maintenance constraint (see [Gaps](#-gaps--improvement-opportunities))                                                                                                                     |
| `application.properties` (prod/dev/test)                        | Flyway is the schema source of truth for MySQL (prod/dev); the `test` profile bypasses it entirely via Hibernate `create-drop` against H2 — the two schema paths can silently diverge                                                                                                                      |
| `../../CONTRIBUTING.md`, `application.properties`               | Three distinct runtime profiles (none/prod, `dev`, `test`) with different database engines and DDL strategies must all stay usable without extra setup burden for new contributors                                                                                                                         |

---

## 🔍 Gaps & Improvement Opportunities

### 1. Match/competitor service and controller layer is the single largest stated gap

**Evidence:** `../../README.md`, `../../ARCHITECTURE.md`, and `../../CLAUDE.md` all independently flag that `IpscController` is an empty stub, that `repositories/` currently has no service-layer caller, and that the service/model/entity-service layers described in earlier project versions were removed pending a rebuild.

**Why it matters:** Every other goal in this document (layering discipline, test conventions, exception handling) exists to be applied to real code — right now the domain with the most entities (8 JPA entities, 6 converters) has no API surface exercising it at all.

**Proposed improvement:** Treat this as the primary roadmap item, phased to match the existing `AwardService`/`ImageService` pattern:
1. Introduce entity-level services (one per aggregate root — `Club`, `Competitor`, `IpscMatch`) following the existing interface + `impl/` split.
2. Add `IpscController` endpoints incrementally, each backed by `@SpringBootTest` integration tests per `scaffold-integration-tests` conventions.
3. Only then layer in cross-entity orchestration (match import, bulk competitor operations) — avoid rebuilding the removed `TransformationService`/`DomainService` abstraction until a concrete need reappears; the earlier version's complexity is exactly what was removed.

### 2. No automatic build/test gate on pull requests

**Evidence:** `../../ARCHITECTURE.md`'s own CI/CD & Quality Gates table states the `Build & Tests` gate runs "locally / by reviewers before merge" — `../../.github/workflows` contains only `codeql.yml`. `../../AGENTS.md`'s Merging rules require "all tests pass" before a `release/*` branch merges, but nothing enforces that automatically.

**Why it matters:** A GitFlow model with `feature/*` → `develop` and `release/*` → `develop` → `main` promotion depends on tests being genuinely green at each merge; today that depends entirely on reviewer discipline.

**Proposed improvement:** Add a `build.yml` (or extend `codeql.yml`'s trigger set) that runs `./mvnw verify -Pcoverage` on push/PR to `develop` and `main`, mirroring CodeQL's existing trigger branches. This closes a gap the project's own architecture document already names.

### 3. Award/Image CSV pipelines never persist

**Evidence:** `../../ARCHITECTURE.md`'s data-flow diagram for the only implemented pipeline notes `AwardService.processCsv()`/`ImageService.processCsv()` "parses CSV via Jackson CsvMapper, maps to response records — **no persistence**".

**Why it matters:** `../../README.md` describes the platform as managing "IPSC match data, competitor tracking, club operations, awards" — but the only working endpoints today are stateless transforms. It's unclear from the documentation whether this is a deliberate interim design (a preview/validation step ahead of a future persistence layer) or an oversight.

**Proposed improvement:** Not a code change by itself — clarify intent first. If CSV processing is meant to stay stateless (e.g. a client-side preview step before a separate import), say so explicitly in `../../README.md`/`../../ARCHITECTURE.md`. If persistence is intended, scope it as its own roadmap item once repository wiring exists (see #1).

### 4. Coverage is measured but not enforced

**Evidence:** `../../HISTORY.md` tracks line/branch coverage percentages release over release (97.3%/98.1% as of v7.2.0) via the JaCoCo `coverage` Maven profile, but nothing fails a build when coverage regresses.

**Why it matters:** Manually re-reading a percentage in `../../HISTORY.md` each release is exactly the kind of drift the project's own documentation conventions try to avoid elsewhere (e.g. the evergreen-documentation rule against version-coupled narrative in `../../README.md`).

**Proposed improvement:** Add a JaCoCo coverage-check rule (e.g. `<rule>` with a line/branch minimum near the current baseline) to the `coverage` profile, and wire it into the CI gate proposed in #2, so a regression fails the build rather than only showing up in the next `../../HISTORY.md` entry.

### 5. `jackson-databind` version override is a standing manual constraint

**Evidence:** `../../pom.xml` explicitly pins `jackson-databind` to `2.21.5` with the comment: "Spring Boot 4.1.0 still manages jackson-databind (2.x) one patch behind its fix version; override it explicitly until a Spring Boot release picks up 2.21.5 by default."

**Why it matters:** This is a manually tracked, easy-to-forget override — nothing flags when the upstream Spring Boot BOM catches up and the override becomes redundant (the same category of clean-up the v7.2.0 release already did for `spring-framework.version`/`tomcat.version`/`commons.lang3.version`).

**Proposed improvement:** No code change needed now — just note it as a recurring release-checklist check: each release, confirm whether the parent's managed `jackson-databind` version has caught up, and drop the override in the same pass the version bump happens.

---

## 🚀 Roadmap

| Phase       | Focus                                                                                                                                                                         |
|-------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Now**     | Add the CI build/test gate (#2) — lowest effort, closes a gap the project's own docs already flag                                                                             |
| **Next**    | Begin the match/competitor service layer (#1), starting with single-entity services and their integration tests                                                               |
| **Later**   | Coverage enforcement (#4) once the new service layer's tests establish a fresh baseline; clarify the CSV persistence question (#3) as part of scoping the next domain feature |
| **Ongoing** | Re-check the `jackson-databind` override (#5) at each release per the Release Checklist                                                                                       |

---

## ✅ Success Criteria

- `IpscController` exposes at least one real, tested endpoint backed by the existing entity/repository layer, closing the gap named identically in `../../README.md`, `../../ARCHITECTURE.md`, and `../../CLAUDE.md`.
- `./mvnw verify -Pcoverage` (or equivalent) runs automatically on PRs to `develop`/`main`, so `../../ARCHITECTURE.md`'s CI/CD & Quality Gates table can drop the "locally / by reviewers" caveat on the `Build & Tests` row.
- Coverage regressions fail CI rather than being caught only when the next `../../HISTORY.md` entry is written.
- This document's Gaps section shrinks over time as items close — closed items should move into `../../HISTORY.md`'s per-version Future Roadmap notes rather than being deleted silently from here.

---

## 📚 Related Documentation

See `../../README.md`'s [📚 Documentation](/README.md#-documentation) section for the full documentation map. Most relevant to this plan:

- [`../../ARCHITECTURE.md`](/ARCHITECTURE.md) — the CI/CD & Quality Gates table and layered-architecture rules this plan builds on
- [`../../AGENTS.md`](/AGENTS.md) — the Git Workflow and Release Checklist referenced throughout
- [`../../HISTORY.md`](/HISTORY.md) — per-release "🚀 Future Roadmap Implications" sections this plan complements
