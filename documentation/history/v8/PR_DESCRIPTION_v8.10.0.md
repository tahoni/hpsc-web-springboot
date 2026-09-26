## 🎯 Summary

- Makes **Semantic Versioning a strict rule**: `AGENTS.md` defines MAJOR/MINOR/PATCH for this project, breaking
  changes are flagged `**Breaking:**` in `CHANGELOG.md` as they land, and `prep-version-release` refuses a version the
  change set doesn't justify.
- Adds an optional **`prod` profile** (`application-prod.properties`) and corrects the database-profile docs to match
  the properties files; removes the undocumented `staging` logging profile.
- Records Gaps #25–#28 from three improvement-plan sweeps, closing #25, #27 and #28 and progressing #26. Only Gap #6
  (scoring/shooter-log layer) remains open.
- Scoped as **MINOR** under the new rules, for the new optional profile — no endpoint, field or schema changed.

## 📦 Key Changes

**Added**

- `application-prod.properties` (`localhost:3306/hpsc_prod`)
- `IpscMatchTest`, guarding `IpscMatch.stages`' Lombok `toString`/`equals`/`hashCode` exclusions

**Changed**

- `AGENTS.md` Semantic Versioning section; `prep-version-release`, `generate-commit-message` and
  `sync-unreleased-changes` enforce it
- Release Checklist re-checks manual `pom.xml` dependency-version overrides at every release
- Removed listed after Fixed in `CHANGELOG.md` and the release documents

**Fixed**

- Database-profile docs in `AGENTS.md`, `CONTRIBUTING.md` and `README.md` (no-profile datasource URL, `local`
  credentials)

**Removed**

- `staging` profile block in `logback-spring.xml`

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (970 tests, 0 failures/errors), up from 966 at v8.9.0
- [x] `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing
- [x] Removing `IpscMatch.stages`' Lombok exclusions fails three of `IpscMatchTest`'s four tests with
      `StackOverflowError`
- [x] `pom.xml` overrides re-checked against `spring-boot-dependencies:4.1.1` — `tomcat.version` still needed
      (parent manages `11.0.24`); `flyway-mysql` in step with the parent's `flyway.version`
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.10.0.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's changes
- [x] `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` checked — Gaps #25–#28 recorded and
      attributed to v8.10.0

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-8100---2026-09-26)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
