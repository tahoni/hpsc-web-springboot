# Pull Request – Version 7.0.0

**Base:** `main` ← **Compare:** `release/v7.0.0`

---

## 🎯 Summary

- Extends the IPSC domain model to support club-scoped match results, visitor tracking, and a persisted "shooter log" (best-4-matches ranking) — promoting six entities back out of `domain/old/` and rebuilding the `repositories/` package from scratch.
- Adds Flyway-managed database migrations, making schema management explicit for MySQL profiles.
- Bumps Spring Boot to 4.0.7 and pins `jackson-databind`, closing several Dependabot security alerts (3 critical).
- Rounds out the release with a `ValueUtil` bug fix, Qodana CI/tooling removal, and a documentation-conventions pass (new `AGENTS.md`, README/ARCHITECTURE cleanup).

## 📦 Key Changes

- **Added:** `ShooterLog`, `ShooterLogEntry` entities; `Club.identifier`, `Competitor.homeClub`; `MatchCompetitor.clubRanking`/`isVisitor`; 3 new unique constraints; 8 rebuilt repositories; Flyway dependencies + `V7_0_0__create_schema.sql`; `AGENTS.md`.
- **Changed:** `domain.old.*` promoted to `domain.*`; `MatchCompetitor.matchRanking` renamed to `overallRanking`; `MatchStageCompetitor` FK repointed from `competitor` to `matchCompetitor`; Spring Boot 4.0.6 → 4.0.7; project version 6.0.0 → 7.0.0.
- **Fixed:** `ValueUtil.nullAsDefaultString` no longer returns `null` when a non-null value's `toString()` itself returns `null`.
- **Removed:** `domain.old` package; Qodana CI workflow/config; duplicate release-notes history docs.

## 🧪 Test Plan

- [x] `./mvnw clean compile` — all entities and repositories compile cleanly
- [x] `./mvnw test -Dtest=HpscWebApplicationTests` — Spring context boots against H2, Hibernate builds the schema for all 8 entities (1/1 passing)
- [x] `ValueUtilTest` — existing suite covers the `nullAsDefaultString` fix
- [ ] No new repository/service-level test coverage was added for the domain-model changes (see Known Issues in `RELEASE_NOTES.md`)

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)
