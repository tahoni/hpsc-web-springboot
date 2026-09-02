## 🎯 Summary

- Process/tooling and documentation-clarity patch release: closes a long-standing CI gap — pull requests to
  `main`/`develop` now run a real, automatic build/test gate for the first time via new `.github/workflows/build.yml`.
- Adds the project's first coverage-regression rule: a JaCoCo `check` execution, tightened twice within this same
  branch from a 51% to an 86% minimum line-coverage floor, wired into the same CI run.
- Refreshes `HISTORY.md`'s coverage baseline, which had drifted since v8.1.1 (98.34%/98.84%, 775 tests) — a fresh
  `./mvnw verify -Pcoverage` run now records the real current figure (98.16%/98.94%, 836 tests).
- Confirms `AwardService.createAwards()`/`ImageService.createImages()` CSV processing is intentionally stateless by
  design, not an unfinished persistence layer, and states that explicitly in `README.md`/`ARCHITECTURE.md`.
- Fixes stale `processCsv()` method references (renamed to `createAwards()`/`createImages()` in v8.0.0) left in
  `ARCHITECTURE.md`, `improvement-plan.md` and two test files' method-grouping comments.
- Closes `documentation/roadmap/improvement-plan.md`'s Gap #2 and Gap #3, and partially progresses Gap #4.

## 📦 Key Changes

**Added**

- `.github/workflows/build.yml` — runs `./mvnw verify -Pcoverage` on push/PR to `main`/`develop`, mirroring
  `codeql.yml`'s trigger branches; uploads the JaCoCo report as a build artefact

**Changed**

- `pom.xml` — new JaCoCo `check` execution enforces a `LINE`/`COVEREDRATIO` minimum, 51% initially then 86% within
  the same branch, wired into the new CI gate
- `README.md`/`ARCHITECTURE.md` — confirmed Award/Image CSV processing is intentionally stateless by design
- `ARCHITECTURE.md`/`CONTRIBUTING.md` — CI/CD & Quality Gates tables updated to reflect the new gate and rule
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` — Gap #2 closed, Gap #3 closed, Gap #4
  partially progressed, all attributed to v8.3.1
- `HISTORY.md` — new Historical Timeline entry, Phase 24 and Milestone 24; coverage baseline refreshed to
  98.16%/98.94% (836 tests)

**Fixed**

- `ARCHITECTURE.md`/`improvement-plan.md`/`AwardControllerTest`/`ImageControllerTest` — stale `processCsv()`
  references corrected to `createAwards()`/`createImages()`

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` — full suite passing (836 tests, 0 failures/errors)
- [x] JaCoCo `check` rule confirmed passing against the real ~98% baseline (well above the 86% floor)
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/RELEASE_NOTES_v8.3.1.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's own
      changes

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)
