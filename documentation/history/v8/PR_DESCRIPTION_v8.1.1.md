## 🎯 Summary

- Process-and-quality patch release: no new domain feature, but a completed CI quality gate, a real test-coverage
  regression fixed, and new tooling so the release process keeps auditing its own roadmap documentation.
- `.github/workflows/qodana.yml` wires up JetBrains' Qodana static analysis, which had sat configured but unwired
  in CI since v8.0.0.
- Recreates `NonFatalExceptionTest`/`FatalExceptionTest`/`ValidationExceptionTest` — dropped somewhere after
  v7.2.0 with no replacement — plus other coverage-gap tests, taking the suite from 92.9%/93.4% to 98.34%/98.84%
  (line/branch), 746 → 775 tests.
- New `update-improvement-plan-gaps`/`sync-improvement-plan-gaps` skills; `generate-pr-description` renamed to
  `prep-version-release`; `AGENTS.md`'s Release Checklist re-synced with the skill's actual process.
- Spring Boot bumped `4.1.0` → `4.1.1`, dropping three now-redundant `dependencyManagement`/property overrides.

## 📦 Key Changes

**Added**

- `.github/workflows/qodana.yml` — Qodana static analysis CI workflow
- `/update-improvement-plan-gaps`, `/sync-improvement-plan-gaps` Claude Code skills
- `NonFatalExceptionTest`, `FatalExceptionTest`, `ValidationExceptionTest`, `IpscCommonScoreTest`,
  `IpscMatchScoreTest`, `IpscMatchStageScoreTest`
- `CONTRIBUTING.md` — new "🗺️ Roadmap" section documenting the improvement-plan files

**Changed**

- `pom.xml` — Spring Boot parent `4.1.0` → `4.1.1`; `jackson-databind`/`log4j-api`/`jackson-bom.version` overrides
  removed as redundant; project version bumped to 8.1.1
- `AGENTS.md`'s Release Checklist — three new steps re-syncing it with `prep-version-release`'s actual process
- `.claude/skills/generate-pr-description` renamed to `prep-version-release`
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` — Gap #5 closed; two new gaps added
  (match-scoring service layer; Qodana CI wiring)
- `IpscCompetitorServiceTest`/`IpscMatchServiceTest` — added `patchCompetitor`/`patchMatch` success-path coverage

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (775 tests, up from 746)
- [x] `./mvnw verify -Pcoverage` — line/branch coverage confirmed at 98.34%/98.84%
- [x] Confirmed `jackson-bom.version`'s resolved value (`3.1.5`) is unchanged after removing the override, via
      Spring Boot 4.1.1's own parent POM
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/RELEASE_NOTES_v8.1.1.md`

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)
