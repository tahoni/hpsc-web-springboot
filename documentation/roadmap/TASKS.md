# Roadmap Task List

A concrete, checkbox-level breakdown of [`IMPROVEMENT_PLAN.md`](../../IMPROVEMENT_PLAN.md)'s five gaps, organised by that document's Now/Next/Later/Ongoing phasing. Each section names its originating gap number for traceability back to the evidence and reasoning there.

## Table of Contents

- [🚀 Now](#-now)
- [🏗️ Next](#-next)
- [🔬 Later](#-later)
- [🔄 Ongoing](#-ongoing)

---

## 🚀 Now

**CI build/test gate** *(IMPROVEMENT_PLAN.md → Gap #2)*

- [ ] Add `.github/workflows/build.yml`, triggered on push/PR to `develop` and `main`, mirroring `codeql.yml`'s trigger branches
- [ ] Run `./mvnw verify -Pcoverage` as the workflow's build step
- [ ] Confirm the workflow fails the PR check when a test fails (not just when the build doesn't compile)
- [ ] Once live, update `ARCHITECTURE.md`'s CI/CD & Quality Gates table to drop the "locally / by reviewers" caveat on the `Build & Tests` row

---

## 🏗️ Next

**Match/competitor service and controller layer** *(IMPROVEMENT_PLAN.md → Gap #1)*

- [ ] Introduce `ClubService` / `ClubServiceImpl` (interface + `impl/` split, matching the existing `AwardService`/`ImageService` pattern)
- [ ] Introduce `CompetitorService` / `CompetitorServiceImpl`
- [ ] Introduce `IpscMatchService` / `IpscMatchServiceImpl`
- [ ] Add `@SpringBootTest` integration tests for each new service, per the `scaffold-integration-tests` conventions
- [ ] Add the first real `IpscController` endpoint(s), backed by the new service(s)
- [ ] Add Mockito-based controller unit tests for the new endpoint(s), per the `scaffold-unit-tests` conventions
- [ ] Hold off on cross-entity orchestration (match import, bulk competitor operations) until a concrete need reappears — don't rebuild the removed `TransformationService`/`DomainService` abstraction pre-emptively

---

## 🔬 Later

**Coverage enforcement** *(IMPROVEMENT_PLAN.md → Gap #4)*

- [ ] Add a JaCoCo `<rule>` (line/branch minimum near the current baseline) to the `coverage` Maven profile
- [ ] Wire that rule into the CI gate added in the Now phase, so a coverage regression fails the build

**CSV persistence clarification** *(IMPROVEMENT_PLAN.md → Gap #3)*

- [ ] Decide whether `AwardService`/`ImageService` CSV processing is meant to stay stateless by design, or should gain persistence
- [ ] If stateless by design: state that explicitly in `README.md`/`ARCHITECTURE.md`
- [ ] If persistence is intended: scope it as its own roadmap item once the service layer from the Next phase exists

---

## 🔄 Ongoing

**Dependency currency check** *(IMPROVEMENT_PLAN.md → Gap #5)*

- [ ] At each release, confirm whether Spring Boot's managed `jackson-databind` version has caught up to the manual override in `pom.xml`
- [ ] Drop the override in the same pass as the version bump once it's redundant

---

Check items off in place as work lands; don't delete a task outright. Once every item under a gap is checked, fold a short summary into `HISTORY.md`'s next per-version Future Roadmap notes and mark the gap closed here (e.g. strike it through with a "✅ Closed in vX.Y.Z" note), per `IMPROVEMENT_PLAN.md`'s Success Criteria.
