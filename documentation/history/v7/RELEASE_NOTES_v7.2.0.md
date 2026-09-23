# Release Notes – Version 7.2.0

**Release Date:** August 25, 2026
**Status:** ✨ Stable

---

## 🎯 Theme

**Test Suite Conventions, AI-Agent Tooling & Dependency Maintenance**

Version 7.2.0 touches no domain entities, repositories, or API surface — it's a release-hygiene pass across the test suite, the project's AI-agent tooling, and its dependency baseline. A JaCoCo coverage audit closed four real gaps (a Lombok-adjacent constructor, an enum `toString()`, and three exception-handling branches in `ControllerAdvice`), and a new AGENTS.md convention for grouping and ordering tests by method was retrofitted across 26 existing test files with zero behavioural change. `AwardService`/`ImageService` gain proper interface-contract unit tests, exercised through the interface type rather than the impl class, and two new Claude Code commands (`/scaffold-unit-tests`, `/scaffold-integration-tests`) codify these conventions for future test authoring. Separately, the Spring Boot parent is upgraded from 4.0.7 to 4.1.0, with several now-redundant `pom.xml` version overrides cleaned up along the way (including a long-standing typo that meant a `commons-lang3` override had never actually applied) and the Flyway plugin's MySQL driver bumped to match Boot's newly-managed Flyway version.

---

## ⭐ Key Highlights

### 🧪 Interface-Contract Unit Tests for `AwardService`/`ImageService`

- New `services/AwardServiceTest`/`services/ImageServiceTest` — Mockito-based unit tests exercising `createAwards`, the only method each interface declares, through the interface type rather than the impl class.
- Superseded the old `services/impl/AwardServiceTest`/`services/impl/ImageServiceTest`, whose thin `createAwards` coverage was tested directly against the impl class. Impl-only helper methods (`readAwards`/`mapAwards`, `readImages`/`mapImages`) remain covered by the existing `services/impl/*ImplTest` classes.

### 🧪 Four JaCoCo Coverage Gaps Closed

- **`ControllerResponse(boolean, String)`** — previously completely untested; covers the message/error swap based on `success`.
- **`ControllerResponse(LocalDateTime, String, String)`** — covers the derived-`success`-from-error-presence branch (non-null/non-blank error, and blank-but-non-null error).
- **`FirearmType.toString()`** — covers both the single-name and multi-name enum-constructor shapes, matching the equivalent coverage already present for the sibling `ClubIdentifier` enum.
- **`ControllerAdvice.logError`** — covers all three previously-untested branches (a `null` throwable, a throwable with a wrapped cause, a `null` `WebRequest`); this class's branch coverage went from 92% to 100%.
- Overall suite coverage rose from 95.7%/91.7% (line/branch) to 97.3%/98.1%.

### 📝 New Test Grouping/Ordering Convention, Retrofitted Suite-Wide

- AGENTS.md's Test Conventions gain a new rule: each method's tests are preceded by a one-line `// methodName()` comment, with groups ordered constructors first, then public before protected, then alphabetically by method name (overloads by parameter count then type), `toString()` always last.
- Retrofitted across 26 existing test files with **no behavioural change** — only comments and whole-method reordering. `ValueUtilTest`'s `nullAsEmptyString` tests, previously scattered across 9 non-contiguous locations, are now one contiguous group.
- `HpscWebApplicationTests` renamed to `HpscWebApplicationTest` (and its `contextLoads()` method renamed) to match the project's `<ClassName>Test` naming convention — the Spring Initializr default had never been corrected.

### 🤖 Two New Claude Code Commands

- **`/scaffold-unit-tests`** — migrated from a stale `.github/prompts/scaffold-unit-tests.prompt.md` that referenced a different project's package (`za.co.signio.apexservices`) and an invented "Layer 1/2/3" abstract-interface-test pattern that doesn't exist in this codebase. Corrected to the real convention: interface-contract tests named `[Class]Test` in `services/`, impl-only helper tests in `services/impl/`, no Lombok-only tests.
- **`/scaffold-integration-tests`** — new, `@SpringBootTest`-based, following `AwardServiceIntegrationTest`/`ImageServiceIntegrationTest` as the template: mandatory `@ActiveProfiles("test")`, `@EnableAutoConfiguration` excluding datasource/JPA/messaging auto-configuration, and public-interface-only calls (never an impl class's protected/private helpers).
- Both commands accept multiple targets per invocation, defer to their loaded `AGENTS.md`/`CLAUDE.md` rather than restating conventions inline (so they can't drift out of sync), and never commit on their own.

### 📦 Spring Boot 4.0.7 → 4.1.0

- Verified each `pom.xml` change against Spring Boot 4.1.0's actual dependency-management POM rather than guessing. Removed the `spring-framework.version`/`tomcat.version` overrides (now identical to Boot 4.1.0's own defaults) and a `commons.lang3.version` property that turned out to be a long-standing typo — Boot's real property is hyphenated `commons-lang3.version`, so the override had never actually taken effect. Removed the `maven-dependency-plugin` version pin, since Boot 4.1.0 now manages that plugin itself.
- Kept the `jackson-databind`/`jackson-bom` patch-ahead overrides unchanged, since Boot 4.1.0's own managed versions are still one patch behind the known-fixed versions.
- Bumped the flyway-maven-plugin's separately-pinned `flyway-mysql` dependency from `11.14.1` to `12.4.0`, matching the `flyway.version` Boot 4.1.0 now manages — plugin-scoped dependencies don't inherit Boot's dependency management, so this now needs manual sync on every future parent bump (documented inline in the POM).
- Verified via the full test suite, `./mvnw verify -Pcoverage` (including the repackage step), and `./mvnw flyway:info`/`flyway:migrate` against a real local MySQL 9.5 dev database.

### 🔀 Documentation & Process

- New `CLAUDE.md` Git Workflow section states the branching model's PR targets directly (`feature/*` → `develop`; `release/vX.Y.Z`/`hotfix/*` → `main`) and the develop-first-for-testing rule, instead of deferring entirely to `AGENTS.md`; `AGENTS.md`/`CONTRIBUTING.md`'s develop-first rule gains the same "for testing before they ship" clarification.
- AGENTS.md's Evergreen Documentation rule broadened to prohibit version *ranges* (e.g. `1.x – 4.x`), not just exact version numbers, in `README.md`/`ARCHITECTURE.md`.
- `CLAUDE.md` now cross-links to `AGENTS.md` (previously the only project doc missing this reference), and its package-overview table now correctly attributes `ControllerAdvice` to `configs/` rather than `exceptions/`.
- Removed a false claim, present across `AGENTS.md`/`CLAUDE.md`/`README.md`/`ARCHITECTURE.md`/`CONTRIBUTING.md`, that AssertJ is used for assertions — `assertj-core` is in fact explicitly excluded from `spring-boot-starter-webmvc-test` in `pom.xml`, and the entire suite uses plain JUnit Jupiter `Assertions`.

---

## 📦 What's New

### Added

#### Testing

- `services/AwardServiceTest`, `services/ImageServiceTest`
- Coverage-gap tests in `ControllerResponseTest`, `FirearmTypeTest`, `ControllerAdviceTest`

#### Tooling

- `.claude/commands/scaffold-unit-tests.md`
- `.claude/commands/scaffold-integration-tests.md`

### Changed

#### Testing

- `AwardServiceIntegrationTest`/`ImageServiceIntegrationTest` now exclude datasource/JPA/messaging auto-configuration
- `HpscWebApplicationTests` renamed to `HpscWebApplicationTest`
- 26 test files retrofitted with the `// methodName()` grouping/ordering convention

#### Build & Metadata

- `pom.xml`: Spring Boot parent `4.0.7` → `4.1.0`; `flyway-mysql` `11.14.1` → `12.4.0`; several redundant version overrides removed

#### Documentation

- `AGENTS.md`, `CLAUDE.md`, `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md` — explicit Git Workflow PR-target guidance in `CLAUDE.md`, evergreen version-range rule, AssertJ correction, cross-links

### Fixed

#### Documentation

- `CLAUDE.md` — missing `AGENTS.md` cross-link; package-overview table's `ControllerAdvice` attribution

### Removed

#### Testing

- `ControllerResponseTest.testDefaultConstructor_whenInstantiated_thenUsesFieldDefaults` (Lombok-only)
- `services/impl/AwardServiceTest`, `services/impl/ImageServiceTest` (superseded)

---

## 🚀 Migration Guide

### For Deployers

- **No schema or data changes in this release.** The only environment-relevant change is the Spring Boot 4.1.0 upgrade and the Flyway plugin's `flyway-mysql` bump to `12.4.0`. Both were verified against a real MySQL 9.5 dev database (`flyway:info`/`flyway:migrate`); no migration script changes accompany this release.

### For Developers

- **No API, service, or domain-layer changes** — nothing to update in calling code.
- **New test convention:** when adding tests to any class, group them by method with a `// methodName()` header comment, ordered constructors first, then public before protected, then alphabetically (overloads by parameter count then type), `toString()` last. See AGENTS.md's Test Conventions section for the full rule.
- **New scaffolding commands:** use `/scaffold-unit-tests <ClassName>` or `/scaffold-integration-tests <ServiceName>` to generate new test files following this repo's actual conventions.

---

## 📊 Statistics

- **Total Commits:** 26
- **Files Changed:** 46
- **Test Files Touched:** 34 (2 new, 1 renamed, 1 removed pair, 26 retrofitted, 4 with new coverage)
- **Coverage:** 95.7%/91.7% → 97.3%/98.1% (line/branch)

---

## 🧭 Design Notes

- **Comments and ordering, not behaviour.** The 26-file test-convention retrofit deliberately changed nothing about what's tested or how — only how the tests are labelled and arranged, verified by an identical pass/fail count (492 tests) before and after.
- **Verify against the real dependency-management POM, not assumptions.** The previous, unsuccessful attempt at this Spring Boot upgrade guessed at which version overrides to drop and broke the build. This attempt checked each one against Spring Boot 4.1.0's actual `spring-boot-dependencies` POM before touching it — including discovering that a `commons-lang3` override had a property-name typo and had never worked at all.
- **Plugin-scoped dependencies don't inherit Boot's dependency management.** The flyway-maven-plugin's `flyway-mysql` pin is now documented inline as needing manual sync with Boot's `flyway.version` on every future parent bump, since Maven doesn't propagate `dependencyManagement` into plugin `<dependencies>`.

---

## 🧪 Testing

- `./mvnw test` — full suite, 492 tests, all passing (unchanged count before and after the test-convention retrofit).
- `./mvnw verify -Pcoverage` — JaCoCo report generated cleanly, including the `spring-boot:repackage` step, against Spring Boot 4.1.0.
- `./mvnw flyway:info` / `./mvnw flyway:migrate` / `./mvnw flyway:repair` — run against a real local MySQL 9.5 dev database to verify the Flyway plugin and its bumped `flyway-mysql` dependency; also surfaced and required cleanup of an unrelated stray `V1__.sql` migration artifact (from IDE tooling, not part of this release) that had left a failed entry in the schema history.

---

## 🐛 Known Issues

- Carried over from v7.0.0/v7.1.0: no calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remain schema-only.
- The remaining JaCoCo coverage gaps identified this release (an unreferenced `IpscConstants` class, an unreachable `IOException` catch branch in the CSV readers, a likely-dead null-check in `ImageResponse.setMimeType`, and `HpscWebApplication.main`) were assessed and deliberately left uncovered — see this release's design discussion for why each isn't worth chasing.

---

## 🔮 Future Enhancements

- Carried over from v7.0.0/v7.1.0: `ShooterLogService` to compute/persist best-4 `ShooterLog` snapshots; controller/service endpoints exposing shooter logs.
- Retrofit the remaining out-of-scope test-convention violations noted during this release's audit (e.g. `ControllerAdviceTest`'s exception-severity ordering) if a future pass decides they're worth aligning.
- Identify and eliminate whatever is generating stray, unversioned Flyway migration files (`V1__.sql`) in `src/main/resources/db/migration/` — encountered but not root-caused during this release's Flyway verification.

---

## 👥 Contributors

Development Team

---

## 📝 Notes

This release is entirely release-hygiene and tooling work — no domain model, repository, or API surface changed. It closes out test-coverage gaps found during a JaCoCo audit, formalises and retrofits a new test-organisation convention across the existing suite, adds proper interface-level unit tests for the two existing services, and brings the project's AI-agent scaffolding commands in line with how this repo actually structures its tests. Separately, it upgrades Spring Boot to 4.1.0 with a properly-verified dependency cleanup, correcting an earlier, unsuccessful attempt at the same upgrade that had guessed at version overrides rather than checking them.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
