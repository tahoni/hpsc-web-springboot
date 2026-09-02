# Roadmap Task List

A concrete, checkbox-level breakdown of [`improvement-plan.md`](improvement-plan.md)'s eight gaps, organised by that
document's Now/Next/Later/Ongoing phasing. Each section names its originating gap number for traceability back to the
evidence and reasoning there.

## Table of Contents

- [🚀 Now](#-now)
- [🏗️ Next](#-next)
- [🔬 Later](#-later)
- [🔄 Ongoing](#-ongoing)

---

## 🚀 Now

**CI build/test gate** *(improvement-plan.md → Gap #2)* — ✅ Closed (version pending)

- [x] Add `.github/workflows/build.yml`, triggered on push/PR to `develop` and `main`, mirroring `codeql.yml`'s trigger
  branches
- [x] Run `./mvnw verify -Pcoverage` as the workflow's build step — invoked as `sh ./mvnw ...` rather than a direct
  `./mvnw ...`, since `mvnw` isn't tracked with the execute bit in git and would fail with "Permission denied" on
  the Ubuntu runner otherwise
- [x] Confirm the workflow fails the PR check when a test fails (not just when the build doesn't compile) — the
  new JaCoCo `check` execution (see Gap #4 below) also fails it on a coverage regression, not just a test failure
- [x] Once live, update `ARCHITECTURE.md`'s CI/CD & Quality Gates table to drop the "locally / by reviewers" caveat on
  the `Build & Tests` row — `CONTRIBUTING.md`'s matching table updated too

**Qodana CI wiring** *(improvement-plan.md → Gap #7)* — ✅ Closed as not applicable in v8.2.0

- [x] Add `.github/workflows/qodana.yml` using JetBrains' `qodana-action`, triggered on push/PR to `develop` and
  `main`, mirroring `codeql.yml`'s trigger branches — done in v8.1.1, removed again in v8.2.0 (see below)
- [x] ~~Confirm the workflow runs against the existing `qodana.yaml` config without further changes~~ — checked via
  `gh run list --workflow=qodana.yml`: every run had failed, not merely gone unverified (missing `QODANA_TOKEN`
  secret, unconditional SARIF upload). Rather than fix both issues, `.github/workflows/qodana.yml`/`qodana.yaml`
  were removed entirely in v8.2.0 — no working baseline existed to preserve
- [x] ~~Once live, update `ARCHITECTURE.md`'s CI/CD & Quality Gates table to drop the "no CI workflow wired up yet"
  caveat on the `Static Analysis` row~~ — done differently: the `Static Analysis` row was removed from that table
  entirely in v8.2.0, along with every other Qodana reference in `ARCHITECTURE.md`/`CONTRIBUTING.md`/`AGENTS.md`

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

**Match bulk CSV import** *(improvement-plan.md → Gap #8)* — ✅ Closed in v8.3.0

- [x] Introduce `MatchRequestForCSV`/`MatchResponseHolder` (`models/ipsc/match/request/`,
  `models/ipsc/match/response/`), mirroring `CompetitorRequestForCSV`/`CompetitorResponseHolder`'s `UpperCamelCase`
  CSV/JSON `@JsonCreator` pattern — with stages represented as a single semicolon-separated
  `<stageNumber>-<stageName>` cell, since CSV has no native nested-row representation
- [x] Add `IpscMatchController.createMatches` (`POST /ipsc/matches/bulk`, consumes `text/csv`) and
  `IpscMatchService`/`IpscMatchServiceImpl.createMatches`, mirroring `IpscCompetitorController`/
  `IpscCompetitorServiceImpl.createCompetitors`'s `readMatches`/`toRequest` shape, persisting each row via the
  existing single-`createMatch` validation/club/firearm-type/category-resolution logic
- [x] Add a `parseStages` helper splitting the delimited `Stages` cell into `MatchStageRequest`s — new relative to
  the competitor flow, which has no equivalent nested-collection column to parse
- [x] Add Mockito-based controller/service/impl unit tests per the `scaffold-unit-tests` conventions —
  `IpscMatchControllerTest`/`IpscMatchServiceTest`/`IpscMatchServiceImplTest`/`MatchRequestForCSVTest`
- [x] Update `ARCHITECTURE.md`'s stale "match bulk-import remains removed pending a rebuild" language and its
  competitor-only endpoint/service/data-flow documentation to reflect the new endpoint

**Coverage enforcement** *(improvement-plan.md → Gap #4)* — 🟡 Partially progressed (version pending)

- [x] ~~Baseline the real coverage figure before setting a rule~~ — done: targeted tests for the exception
  hierarchy, the `models/ipsc/shared` scoring classes and every untested `patchCompetitor`/`patchMatch` field
  success-path brought the suite from 92.9%/93.4% to 98.34%/98.84% (line/branch), 746 → 775 tests
- [x] Add a JaCoCo `<rule>` (line/branch minimum near the current baseline) to the `coverage` Maven profile — done
  differently: a `LINE`/`COVEREDRATIO` minimum of `0.51` (51%), a deliberately low regression backstop rather than
  "near the current baseline" (~98%) — tightening it is left as a follow-up once the gate has run cleanly for a few
  releases
- [x] Wire that rule into the CI gate added in the Now phase, so a coverage regression fails the build — the
  `check` execution runs as part of `build.yml`'s `./mvnw verify -Pcoverage` step
- [ ] Refresh `HISTORY.md`'s coverage figure at the same time, so it stops drifting from the real number — still
  outstanding

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
  already paid off once, within this same v8.1.1 release: the `jackson-bom.version` property (pinned `3.1.5`)
  matched Boot 4.1.1's own managed default exactly, so it was dropped too. This specific instance is closed, but
  the practice itself stays in force for future releases

---

Check items off in place as work lands; don't delete a task outright. Once every item under a gap is checked, fold a
short summary into `HISTORY.md`'s next per-version Future Roadmap notes and mark the gap closed here (e.g. strike it
through with a "✅ Closed in vX.Y.Z" note), per `improvement-plan.md`'s Success Criteria.
