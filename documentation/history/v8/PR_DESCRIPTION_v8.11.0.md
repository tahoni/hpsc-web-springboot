## 🎯 Summary

- Adds a **Docker image** (multi-stage `Dockerfile`, non-root JRE runtime, `prod` profile by default) and a **Docker
  Compose setup** that runs it against its own MySQL 8.4 container.
- Adds a **health endpoint**, `/hpsc-web/actuator/health` (Spring Boot Actuator, database check included), which the
  image's `HEALTHCHECK` polls.
- **Fixes Flyway never running at startup** under Spring Boot 4 — `spring-boot-starter-flyway` replaces `flyway-core`
  — and baselines production's hand-built schema at `7.0.0`, so its first start applies the pending migrations.
- **MINOR** release for the new endpoint; no existing API, configuration property or schema changes.

## 📦 Key Changes

**Added**

- `Dockerfile`, `.dockerignore`, `docker-compose.yml`, `.env.example`; `README.md`'s Running with Docker section
- `spring-boot-starter-actuator` — the health endpoint only, with Actuator's defaults
- Gap #30 (Flyway documented but never run at startup) recorded and closed in the improvement plan

**Changed**

- `ARCHITECTURE.md`'s Technology Stack gains a Containerisation row; `IpscMatchTest`'s stage tests separate Act from
  Assert

**Fixed**

- `spring-boot-starter-flyway` replaces `flyway-core`, so migrations run at startup in every MySQL profile
- `application-prod.properties` baselines a non-empty schema without Flyway's history at `7.0.0`

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` — 970 tests, 0 failures/errors; 98.77% line / 99.09% branch coverage, JaCoCo gate
      passing
- [x] `tomcat.version` override re-checked: Spring Boot 4.1.1 still manages `11.0.24`, so it stays (Gap #26)
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.11.0.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's changes
- [ ] `docker compose up --build` against an empty database: schema created, `/hpsc-web/actuator/health` reports `UP`
- [ ] Back up the production database and confirm its tables match the v7.0.0 shape before deploying (see the
      Migration Guide)

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-8110---2026-09-27)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
