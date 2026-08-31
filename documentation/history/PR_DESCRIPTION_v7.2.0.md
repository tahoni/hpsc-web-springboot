## 🎯 Summary

- Closes 4 real JaCoCo coverage gaps and adds proper interface-contract unit tests for `AwardService`/`ImageService`.
- Formalises and retrofits a new AGENTS.md test-organisation convention (one-line `// methodName()` group headers, a fixed ordering rule) across 26 existing test files, with zero behavioural changes.
- Adds two Claude Code commands (`/scaffold-unit-tests`, `/scaffold-integration-tests`) that codify this repo's actual test conventions, replacing a stale, wrong-project prompt file.
- Upgrades Spring Boot 4.0.7 → 4.1.0, with every `pom.xml` change verified against Boot's real dependency-management POM rather than guessed — including bumping the Flyway plugin's pinned `flyway-mysql` to match Boot's newly managed Flyway version.

## 📦 Key Changes

**Added**
- `services/AwardServiceTest`, `services/ImageServiceTest` (interface-contract unit tests)
- Coverage-gap tests: `ControllerResponseTest`, `FirearmTypeTest`, `ControllerAdviceTest`
- `/scaffold-unit-tests`, `/scaffold-integration-tests` Claude Code commands

**Changed**
- 26 test files retrofitted with the new `// methodName()` grouping/ordering convention
- `HpscWebApplicationTests` renamed to `HpscWebApplicationTest`
- `pom.xml`: Spring Boot `4.0.7` → `4.1.0`, `flyway-mysql` `11.14.1` → `12.4.0`, several redundant version overrides removed
- New `CLAUDE.md` Git Workflow section states PR targets directly (`feature/*` → `develop`; `release/*`/`hotfix/*` → `main`)

**Fixed**
- `CLAUDE.md` missing `AGENTS.md` cross-link and an incorrect package attribution
- False "AssertJ is used" claim removed from five docs — the suite uses JUnit Jupiter `Assertions` only

**Removed**
- One Lombok-only test; the two old impl-level `createAwards` tests superseded by the new interface-level ones

## 🧪 Test Plan

- [x] `./mvnw test` — full suite, 492 tests, all passing
- [x] `./mvnw verify -Pcoverage` — JaCoCo report clean, including the repackage step, on Spring Boot 4.1.0
- [x] `./mvnw flyway:info` / `flyway:migrate` — verified against a real local MySQL 9.5 dev database with the bumped `flyway-mysql` dependency
- [x] Coverage: 95.7%/91.7% → 97.3%/98.1% (line/branch)
- [x] Confirmed no version-specific info leaked into `README.md`/`ARCHITECTURE.md`

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-720---2026-08-25)
- [HISTORY.md](/HISTORY.md)
