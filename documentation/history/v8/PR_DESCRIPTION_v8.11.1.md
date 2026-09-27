## 🎯 Summary

- Adds a **Docker image build to CI**: `.github/workflows/docker.yml` builds the `Dockerfile` on every push and PR to
  `main`/`develop`, closing the Known Issue v8.11.0 shipped with — a broken image now fails CI rather than a
  deployment.
- **Enforces branch coverage**: the JaCoCo gate now fails below 97% branch coverage as well as below 97% line
  coverage (branch coverage is 99.09%), ending a Known Issue carried since v8.4.0.
- Records the Docker gap in the improvement plan as **Gap #31**, closed in this release, leaving Gap #6 as the only
  open gap.
- A **PATCH** release: CI, build tooling and documentation only, with no API, configuration or schema change.

## 📦 Key Changes

**Added**

- `.github/workflows/docker.yml`: `docker/build-push-action`, build only (`push: false`), GitHub Actions layer cache;
  listed in `ARCHITECTURE.md`'s CI/CD & Quality Gates table and `CONTRIBUTING.md`
- Gap #31 in `improvement-plan.md`/`improvement-plan-tasks.md`, recorded and closed

**Changed**

- JaCoCo `check` execution: a 97% `BRANCH` limit beside the 97% `LINE` one; `ARCHITECTURE.md` and Gap #4 updated
- Version bumped to 8.11.1 in `pom.xml` and `@OpenAPIDefinition`; `tomcat.version` override kept, as Spring Boot
  4.1.1 still manages Tomcat `11.0.24` (Gap #26)

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` — full suite passing (970 tests, 0 failures/errors), unchanged
      from v8.11.0; 98.77% line / 99.09% branch coverage, JaCoCo gate (line and branch) passing
- [x] `docker.yml` validated as YAML
- [x] `docker build --no-cache -t hpsc-web .` builds the image locally (non-root `hpsc` user, version 8.11.1)
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.11.1.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's changes
- [x] The new `Docker Image` check passes on this PR

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-8111---2026-09-27)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
