## 🎯 Summary

- Replaces GitHub's built-in Maven **dependency submission** — the one check on `develop`→`main` PRs with no
  workflow file behind it — with an explicit workflow using the project's JDK 25 and Maven wrapper.
- Adds **`.github/dependabot.yml`**: weekly, grouped Maven and GitHub Actions version updates targeting `develop`.
- Handles Dependabot **security-update PRs as hotfixes**, since they always target `main` (Gap #29, recorded and
  closed). Only Gap #6 remains open.
- Scoped as **PATCH** — CI and documentation only; no API, configuration or schema change.

## 📦 Key Changes

**Added**

- `.github/workflows/dependency-submission.yml`
- `.github/dependabot.yml`

**Changed**

- `AGENTS.md`/`CONTRIBUTING.md` branching rules: `dependabot/*` entry; security PRs merged into `main`, then `main`
  merged back into `develop`
- `ARCHITECTURE.md`'s CI/CD & Quality Gates table and Project Structure tree

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (970 tests, 0 failures/errors), unchanged from v8.10.0
- [x] `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing
- [x] `dependency-submission.yml` and `dependabot.yml` validated as YAML
- [x] `pom.xml` overrides re-checked against `spring-boot-dependencies:4.1.1` — `tomcat.version` still needed
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.10.1.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's changes
- [ ] After merging to `main`: turn off Settings → Code security → "Automatic dependency submission"

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-8101---2026-09-26)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
