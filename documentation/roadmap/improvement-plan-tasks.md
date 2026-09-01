# Roadmap Task List

A concrete, checkbox-level breakdown of [`improvement-plan.md`](improvement-plan.md)'s seven gaps, organised by that
document's Now/Next/Later/Ongoing phasing. Each section names its originating gap number for traceability back to the
evidence and reasoning there.

## Table of Contents

- [🚀 Now](#-now)
- [🏗️ Next](#-next)
- [🔬 Later](#-later)
- [🔄 Ongoing](#-ongoing)

---

## 🚀 Now

**CI build/test gate** *(improvement-plan.md → Gap #2)*

- [ ] Add `.github/workflows/build.yml`, triggered on push/PR to `develop` and `main`, mirroring `codeql.yml`'s trigger
  branches
- [ ] Run `./mvnw verify -Pcoverage` as the workflow's build step
- [ ] Confirm the workflow fails the PR check when a test fails (not just when the build doesn't compile)
- [ ] Once live, update `ARCHITECTURE.md`'s CI/CD & Quality Gates table to drop the "locally / by reviewers" caveat on
  the `Build & Tests` row

**Qodana CI wiring** *(improvement-plan.md → Gap #7)*

- [ ] Add `.github/workflows/qodana.yml` using JetBrains' `qodana-action`, triggered on push/PR to `develop` and
  `main`, mirroring `codeql.yml`'s trigger branches
- [ ] Confirm the workflow runs against the existing `qodana.yaml` config without further changes
- [ ] Once live, update `ARCHITECTURE.md`'s CI/CD & Quality Gates table to drop the "no CI workflow wired up yet"
  caveat on the `Static Analysis` row

---

## 🏗️ Next

**Match/competitor service and controller layer** *(improvement-plan.md → Gap #1)* — ✅ Closed in v8.0.0

- [x] Introduce `ClubService` / `ClubServiceImpl` (interface + `impl/` split, matching the existing `AwardService`/
  `ImageService` pattern) — implemented differently: club resolution stayed inline (`resolveHomeClub`/`resolveClub`
  calling `ClubRepository` directly from `IpscCompetitorServiceImpl`/`IpscMatchServiceImpl`) rather than via a
  dedicated `ClubService`; a simpler equivalent, not a gap
- [x] Introduce `CompetitorService` / `CompetitorServiceImpl` — shipped as `IpscCompetitorService`/
  `IpscCompetitorServiceImpl`
- [x] Introduce `IpscMatchService` / `IpscMatchServiceImpl`
- [x] Add `@SpringBootTest` integration tests for each new service, per the `scaffold-integration-tests` conventions —
  `IpscCompetitorServiceIntegrationTest`/`IpscMatchServiceIntegrationTest`
- [x] Add the first real `IpscController` endpoint (s), backed by the new service (s) — shipped as
  `IpscCompetitorController`/`IpscMatchController` (full CRUD), superseding the empty `IpscController` stub, which
  was deleted
- [x] Add Mockito-based controller unit tests for the new endpoint (s), per the `scaffold-unit-tests` conventions —
  `IpscCompetitorControllerTest`/`IpscMatchControllerTest`
- [x] Hold off on cross-entity orchestration (match import, bulk competitor operations) until a concrete need
  reappears — don't rebuild the removed `TransformationService`/`DomainService` abstraction pre-emptively —
  honoured; v8.1.0's competitor bulk CSV import reuses the existing single-`createCompetitor` logic per row
  instead of introducing new cross-entity orchestration

**Coverage enforcement** *(improvement-plan.md → Gap #4)*

- [ ] Add a JaCoCo `<rule>` (line/branch minimum near the current baseline) to the `coverage` Maven profile — note
  the baseline is the current 92.9%/93.4% (line/branch), not the stale 97.3%/98.1% still recorded in `HISTORY.md`
- [ ] Wire that rule into the CI gate added in the Now phase, so a coverage regression fails the build
- [ ] Refresh `HISTORY.md`'s coverage figure at the same time, so it stops drifting from the real number

**Match scoring / shooter-log service and controller layer** *(improvement-plan.md → Gap #6)*

- [ ] Introduce `MatchScoreService`/`MatchScoreServiceImpl` (interface + `impl/` split) over the existing
  `MatchCompetitor`/`MatchStageCompetitor` repositories
- [ ] Introduce `ShooterLogService`/`ShooterLogServiceImpl` over the existing `ShooterLog*` repositories
- [ ] Add controller endpoints for both, backed by `@SpringBootTest` integration tests per the
  `scaffold-integration-tests` conventions
- [ ] Add Mockito-based controller unit tests per the `scaffold-unit-tests` conventions
- [ ] Hold off on cross-entity orchestration (e.g. a full Practiscore results import) until a concrete need
  reappears, per the same discipline that closed Gap #1
- [ ] Once live, update `ARCHITECTURE.md`'s Feature Support table and `README.md`/`CONTRIBUTING.md`'s matching notes
  to drop the "still being built" language

---

## 🔬 Later

**CSV persistence clarification** *(improvement-plan.md → Gap #3)* — 🟡 Partially narrowed in v8.1.0

- [x] If persistence is intended: scope it as its own roadmap item once the service layer from the Next phase
  exists — done for the competitor domain specifically: `IpscCompetitorController.createCompetitors` (v8.1.0) is a
  scoped, deliberate persisting-CSV-import feature, and `ARCHITECTURE.md` now contrasts it directly against the
  Award/Image flow ("without persisting anything") in an adjacent data-flow section
- [ ] Decide whether `AwardService`/`ImageService` CSV processing itself is meant to stay stateless by design or
  should gain persistence — still undecided; the v8.1.0 contrast narrows the ambiguity but doesn't resolve it
- [ ] If stateless by design: state that explicitly in `README.md`/`ARCHITECTURE.md` for `AwardService`/
  `ImageService` themselves, not just by implication via the new competitor flow

---

## 🔄 Ongoing

**Dependency currency check** *(improvement-plan.md → Gap #5)* — ✅ Closed in v8.1.1

- [x] At each release, confirm whether Spring Boot's managed `jackson-databind` version has caught up to the manual
  override in `pom.xml` — confirmed for v8.1.1: the `spring-boot-starter-parent` bump to `4.1.1` manages
  `jackson-databind` `2.21.5` directly
- [x] Drop the override in the same pass as the version bump once it's redundant — done, along with a second
  `log4j-api` override (CVE-2026-49844 fix) added since, also picked up by the same bump
- [ ] Recurring: repeat this check at each future release for any newly added manual dependency-version overrides —
  this specific instance is closed, but the practice itself stays in force

---

Check items off in place as work lands; don't delete a task outright. Once every item under a gap is checked, fold a
short summary into `HISTORY.md`'s next per-version Future Roadmap notes and mark the gap closed here (e.g. strike it
through with a "✅ Closed in vX.Y.Z" note), per `improvement-plan.md`'s Success Criteria.
