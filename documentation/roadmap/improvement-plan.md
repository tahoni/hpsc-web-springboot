# Improvement Plan

This document synthesises the goals and constraints stated across this repository's documentation and configuration into
a single set of prioritised improvement opportunities. Unlike [`README.md`](/README.md) and [
`ARCHITECTURE.md`](/ARCHITECTURE.md), it is not evergreen — it reflects a point-in-time reading of the project and
should be revisited whenever a major gap it names is closed or a new one is identified.

## Table of Contents

- [🎯 Purpose & Scope](#-purpose--scope)
- [⚙️ Goals & Constraints (Synthesised)](#-goals--constraints-synthesised)
- [🔍 Gaps & Improvement Opportunities](#-gaps--improvement-opportunities)
- [🚀 Roadmap](#-roadmap)
- [✅ Success Criteria](#-success-criteria)
- [📚 Related Documentation](#-related-documentation)

---

## 🎯 Purpose & Scope

This plan draws only on what the repository already states about itself — `README.md`, `ARCHITECTURE.md`, `AGENTS.md`,
`CLAUDE.md`, `CONTRIBUTING.md`, `HISTORY.md`'s Future Roadmap sections, `pom.xml`, `application*.properties` and
`.github/workflows` — rather than introducing new goals. Where the documentation and the configuration disagree, or
where a stated goal has no corresponding work item yet, that gap is called out below as an improvement opportunity.

It complements, rather than duplicates, `HISTORY.md`'s per-release "🚀 Future Roadmap Implications" section: that section
tracks what changed release-to-release, while this document tracks the standing, cross-release gaps between the
project's stated intent and its current state.

Because the "⚙️ Goals & Constraints" table below is synthesised partly from `HISTORY.md`'s Future Roadmap
Implications sections, check whether that table needs a matching update whenever `HISTORY.md` changes — most
concretely, whenever a release is being prepped and `HISTORY.md` gains its new Historical Timeline entry, per
`AGENTS.md`'s Release Checklist.

---

## ⚙️ Goals & Constraints (Synthesised)

| Source                                              | Goal / constraint                                                                                                                                                                                                                                                                |
|-----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `README.md`, `ARCHITECTURE.md`                      | Rebuild the match/competitor domain's service and controller layer on top of the existing JPA entities and repositories — ✅ delivered in v8.0.0 as `IpscCompetitorService`/`IpscMatchService` and their controllers                                                             |
| `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md`   | Build the match/competitor **scoring** and shooter-log service/controller layer over the existing JPA entities, repositories and already-fixed request DTOs — explicitly called out as still being built, not aspirational                                                       |
| `ARCHITECTURE.md` (Layered Architecture)            | Strict unidirectional layering: Controller → Service → Repository → Database; no layer may skip the one below it, and controllers must carry no business logic                                                                                                                   |
| `ARCHITECTURE.md` (Exception handling), `CLAUDE.md` | All exceptions extend `FatalException`, `NonFatalException` or `ValidationException`, handled centrally by `ControllerAdvice` — never caught and rethrown as generic `RuntimeException`                                                                                          |
| `ARCHITECTURE.md` (CI/CD & Quality Gates)           | Security analysis (CodeQL) and code coverage (JaCoCo) are established quality gates; `./mvnw test` is documented as reviewer/local-only, not an automatic gate; Qodana static analysis is wired into CI as of v8.1.1 but has failed on every run since (see Gap #7)              |
| `AGENTS.md` (Git Workflow, Release Checklist)       | GitFlow branching (`develop` → `release/vX.Y.Z` → `main`, `hotfix/*` direct to `main`), Semantic Versioning and a fixed, ordered release checklist covering `pom.xml`, `HpscWebApplication.java`, `CHANGELOG.md`, `HISTORY.md`, `RELEASE_NOTES.md` and archived per-version docs |
| `AGENTS.md` (Documentation Conventions)             | British English spelling throughout prose and Javadoc; every heading carries a reused or deliberately new emoji; `README.md`/`ARCHITECTURE.md` stay version-agnostic (reverse-synced from release docs, not the other way round)                                                 |
| `AGENTS.md` (Test Conventions), `CLAUDE.md`         | Mockito-only controller tests (no Spring context), H2-backed service/repository integration tests, `<ClassName>Test` / `test<Scenario>_when<Condition>_then<Expectation>` naming, AssertJ unavailable (excluded in `pom.xml`)                                                    |
| `pom.xml`                                           | Track current Spring Boot / Java releases closely (Java 25, Spring Boot 4.1.1) — this currency itself creates a maintenance constraint (see [Gaps](#-gaps--improvement-opportunities))                                                                                           |
| `application.properties` (prod/dev/test)            | Flyway is the schema source of truth for MySQL (prod/dev); the `test` profile bypasses it entirely via Hibernate `create-drop` against H2 — the two schema paths can silently diverge                                                                                            |
| `CONTRIBUTING.md`, `application.properties`         | Three distinct runtime profiles (none/prod, `dev`, `test`) with different database engines and DDL strategies must all stay usable without extra setup burden for new contributors                                                                                               |

---

## 🔍 Gaps & Improvement Opportunities

### 1. Match/competitor service and controller layer is the single largest stated gap — ✅ Closed in v8.0.0

**Evidence:** `README.md`, `ARCHITECTURE.md` and `CLAUDE.md` all independently flag that `IpscController` is an empty
stub, that `repositories/` currently has no service-layer caller and that the service/model/entity-service layers
described in earlier project versions were removed pending a rebuild.

**Why it matters:** Every other goal in this document (layering discipline, test conventions, exception handling) exists
to be applied to real code — right now the domain with the most entities (8 JPA entities, 6 converters) has no API
surface exercising it at all.

**Proposed improvement:** Treat this as the primary roadmap item, phased to match the existing `AwardService`/
`ImageService` pattern:

1. Introduce entity-level services (one per aggregate root — `Club`, `Competitor`, `IpscMatch`) following the existing
   interface + `impl/` split.
2. Add `IpscController` endpoints incrementally, each backed by `@SpringBootTest` integration tests per
   `scaffold-integration-tests` conventions.
3. Only then layer in cross-entity orchestration (match import, bulk competitor operations) — avoid rebuilding the
   removed `TransformationService`/`DomainService` abstraction until a concrete need reappears; the earlier version's
   complexity is exactly what was removed.

**Outcome:** Delivered in v8.0.0 as `IpscCompetitorService`/`IpscMatchService` (interface + `impl/` split) with full
CRUD controllers superseding the empty `IpscController` stub, each backed by Mockito controller tests and
`@SpringBootTest` integration tests. Club resolution stayed inline (`resolveHomeClub`/`resolveClub` against
`ClubRepository`) rather than via a dedicated `ClubService` — a simpler equivalent, not a gap. Cross-entity
orchestration was deliberately held off per step 3 above, until v8.1.0's competitor bulk CSV import reused the
existing single-`createCompetitor` logic per row instead of introducing new orchestration. See
[`improvement-plan-tasks.md`](improvement-plan-tasks.md#-next) for the full checklist.

### 2. No automatic build/test gate on pull requests

**Evidence:** `ARCHITECTURE.md`'s own CI/CD & Quality Gates table states the `Build & Tests` gate runs "locally / by
reviewers before merge" — `.github/workflows` contains only `codeql.yml` and, as of Gap #7, `qodana.yml`; neither
runs `./mvnw test`. `AGENTS.md`'s Merging rules require "all tests pass" before a `release/*` branch merges, but
nothing enforces that automatically.

**Why it matters:** A GitFlow model with `feature/*` → `develop` and `release/*` → `develop` → `main` promotion depends
on tests being genuinely green at each merge; today that depends entirely on reviewer discipline.

**Proposed improvement:** Add a `build.yml` (or extend `codeql.yml`'s trigger set) that runs `./mvnw verify -Pcoverage`
on push/PR to `develop` and `main`, mirroring CodeQL's existing trigger branches. This closes a gap the project's own
architecture document already names.

### 3. Award/Image CSV pipelines never persist — 🟡 Partially narrowed in v8.1.0

**Evidence:** `ARCHITECTURE.md`'s data-flow diagram for the only implemented pipeline notes `AwardService.processCsv()`/
`ImageService.processCsv()` "parses CSV via Jackson CsvMapper, maps to response records — **no persistence**".

**Why it matters:** `README.md` describes the platform as managing "IPSC match data, competitor tracking, club
operations, awards" — but the only working endpoints today are stateless transforms. It's unclear from the documentation
whether this is a deliberate interim design (a preview/validation step ahead of a future persistence layer) or an
oversight.

**Proposed improvement:** Not a code change by itself — clarify intent first. If CSV processing is meant to stay
stateless (e.g. a client-side preview step before a separate import), say so explicitly in `README.md`/
`ARCHITECTURE.md`. If persistence is intended, scope it as its own roadmap item once repository wiring exists (see #1).

**Progress:** With #1 closed, v8.1.0 delivered `IpscCompetitorController.createCompetitors` — a deliberately scoped,
persisting CSV import for the competitor domain — and `ARCHITECTURE.md` now contrasts it directly against the
Award/Image flow ("without persisting anything") in an adjacent data-flow section. That narrows the ambiguity for
readers, but the underlying question for `AwardService`/`ImageService` themselves — deliberate design or oversight —
is still unresolved and not yet stated explicitly in `README.md`/`ARCHITECTURE.md`.

### 4. Coverage is measured but not enforced

**Evidence:** `HISTORY.md` tracks line/branch coverage percentages release over release (97.3%/98.1% as of v7.2.0) via
the JaCoCo `coverage` Maven profile, but nothing fails a build when coverage regresses. That v7.2.0 figure was never
updated for v8.0.0 or v8.1.0, despite the suite growing from 492 to 746 tests — and running `./mvnw verify -Pcoverage`
against the current tree shows the real figure has since dropped to **92.9% line / 93.4% branch**; not merely gone
stale in the document but actively regressed from what `HISTORY.md` still states.

**Why it matters:** Manually re-reading a percentage in `HISTORY.md` each release is exactly the kind of drift the
project's own documentation conventions try to avoid elsewhere (e.g. the evergreen-documentation rule against
version-coupled narrative in `README.md`).

**Proposed improvement:** Add a JaCoCo coverage-check rule (e.g. `<rule>` with a line/branch minimum near the current
baseline) to the `coverage` profile, and wire it into the CI gate proposed in #2, so a regression fails the build rather
than only showing up in the next `HISTORY.md` entry.

**Progress:** The regression identified above is fixed — targeted tests for the exception hierarchy (previously 20%
covered, a real regression from a since-deleted test suite), the `models/ipsc/shared` scoring groundwork classes
(previously 0%) and every untested `patchCompetitor`/`patchMatch` field success-path brought the suite from
92.9%/93.4% to **98.34%/98.84%** (line/branch), 746 → 775 tests. The remaining ~1.6% is deliberately left uncovered:
three structurally-unreachable `IOException` catch blocks in the CSV `read*()` methods (`AwardServiceImpl`,
`ImageServiceImpl`, `IpscCompetitorServiceImpl` — reachable only by a real I/O failure, not a malformed `String`),
`ImageResponse.setMimeType`'s null-fallback branch (dead code — `mimeType` is field-initialised to `""` and can
never be null when checked), the still-unused `IpscConstants` class (no test-a-constants-class convention exists in
this codebase), and `HpscWebApplication.main()` (excluded as impractical/low-value — testing it would start a real
embedded server). `HISTORY.md`'s coverage figure still needs refreshing to this new baseline once #2's CI gate lands.

### 5. `jackson-databind` version override is a standing manual constraint — ✅ Closed in v8.1.1

**Evidence:** `pom.xml` explicitly pins `jackson-databind` to `2.21.5` with the comment: "Spring Boot 4.1.0 still
manages jackson-databind (2.x) one patch behind its fix version; override it explicitly until a Spring Boot release
picks up 2.21.5 by default."

**Why it matters:** This is a manually tracked, easy-to-forget override — nothing flags when the upstream Spring Boot
BOM catches up and the override becomes redundant (the same category of clean-up the v7.2.0 release already did for
`spring-framework.version`/`tomcat.version`/`commons.lang3.version`).

**Proposed improvement:** No code change needed now — only note it as a recurring release-checklist check: each release,
confirm whether the parent's managed `jackson-databind` version has caught up, and drop the override in the same pass
the version bump happens.

**Outcome:** Bumping the `spring-boot-starter-parent` to `4.1.1` picked up `jackson-databind` `2.21.5` by default, so
the manual override was dropped from `pom.xml`'s `dependencyManagement` in the same pass, exactly as this gap
proposed. A second manual override added later for `log4j-api` (CVE-2026-49844) was resolved by the parent bump the
same way and dropped alongside it. The recurring check itself then caught a third instance during v8.1.1's own
release prep: the `jackson-bom.version` property (pinned `3.1.5`) matched Boot 4.1.1's own managed default exactly
(confirmed via the parent POM directly, not just an echoed property), so it was redundant too and has been dropped.
This category of clean-up recurs — re-check remaining manual overrides at each future release per the Release
Checklist (this gap's Ongoing counterpart, #5's own recurring check, stays in force even though the specific
overrides it named are gone).

### 6. Match scoring / shooter-log service and controller layer are not yet built

**Evidence:** `ARCHITECTURE.md`'s Feature Support table states, "JPA entities and repositories exist for
match/competitor scoring and shooter logs, but the service/controller layer that operates on them is still being
built"; its `repositories/` package comment marks `MatchCompetitor`/`MatchStageCompetitor`/`ShooterLog*` as "not yet
wired"; its Model Layer note calls `MatchOverallScoresRequest`/`MatchStageScoresRequest` "groundwork only — not yet
consumed by any controller". `README.md` and `CONTRIBUTING.md` independently restate the same gap, and
`documentation/history/RELEASE_NOTES_v8.1.0.md`'s Known Issues/Future Enhancements carry it forward from v8.0.0,
explicitly noting that the request DTOs' `@JsonCreator`/required-field fix (closed alongside Gap #1) leaves them
"ready" for wiring.

**Why it matters:** This is the same shape of gap that closed Gap #1 — JPA/repository layer exists, service/
controller layer doesn't — but for the scoring/shooter-log domain specifically, and it is now the most-repeated
"known gap" across the project's own documentation, yet was not separately tracked here.

**Proposed improvement:** Apply the same phased pattern that closed Gap #1: introduce `MatchScoreService`/
`ShooterLogService` (interface + `impl/` split) over the existing repositories, add controller endpoints backed by
`@SpringBootTest` integration tests, and only then consider cross-entity orchestration (e.g. importing a full
Practiscore results export) once a concrete need reappears. The request DTOs' required-field enforcement is already
fixed (see Gap #1's Outcome), so this gap is scoped to the service/controller layer alone.

### 7. Qodana static analysis is configured but never runs in CI — 🟡 Partially progressed in v8.1.1

**Evidence:** `ARCHITECTURE.md`'s CI/CD & Quality Gates table states the `Static Analysis` (Qodana JVM) gate is "Run
locally / via IDE against `qodana.yaml` — no CI workflow wired up yet" — still true of `ARCHITECTURE.md` itself as
written, though see Progress below. `qodana.yaml` is fully configured (profile `qodana.starter`, `projectJDK: "25"`,
linter `jetbrains/qodana-jvm:2026.2`); at the time this gap was written, `.github/workflows/` contained only
`codeql.yml`, with no Qodana Scan action.

**Why it matters:** Distinct from Gap #2 (which is about the `./mvnw test`/build gate, not static analysis) — a
second, separately named quality gate the architecture document itself already flags as configured but not
automated, sitting unused since the config was written.

**Proposed improvement:** Add a `qodana.yml` workflow (JetBrains' `qodana-action`) triggered on push/PR to `develop`
and `main`, mirroring `codeql.yml`'s (and the proposed `build.yml`'s) trigger branches. Once live, update
`ARCHITECTURE.md`'s CI/CD & Quality Gates table to drop the "no CI workflow wired up yet" caveat on the `Static
Analysis` row — the same closing move Gap #2 proposes for `Build & Tests`.

**Progress:** `.github/workflows/qodana.yml` now exists, running `JetBrains/qodana-action` against the existing
`qodana.yaml` config on push/PR to `develop`/`main`, mirroring `codeql.yml`'s trigger branches exactly as proposed.
Not yet closed: `ARCHITECTURE.md`'s CI/CD & Quality Gates table still states "no CI workflow wired up yet" on the
`Static Analysis` row — that update is deliberately deferred until a real run is confirmed green, per this gap's own
"once live" wording. This is now confirmed further off than "merely unverified": every run since the workflow was
added (`gh run list --workflow=qodana.yml`, five most recent runs on `develop`/`main` as of this check) has failed
with the same two errors — `qodana scan failed with exit code 1` because release-line Qodana linters since 2023.2
require a `QODANA_TOKEN` (no such secret is configured in this repository), and a second, independent failure in the
same job, `Input required and not supplied: sarif_file`, because the `github/codeql-action/upload-sarif@v4` step
still runs even when the scan step produced no SARIF file to upload. Both need fixing — provisioning a
`QODANA_TOKEN` repository secret (or switching to a Community linter that doesn't require one) and conditioning the
SARIF upload step on the scan step's success — before this gap can close.

---

## 🚀 Roadmap

| Phase       | Focus                                                                                                                                                                                                                  |
|-------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Now**     | Add the CI build/test gate (#2) and fix the failing Qodana workflow (#7 — missing `QODANA_TOKEN` secret and an unconditional SARIF upload step) — both lowest effort, closing gaps the project's own docs already flag |
| **Next**    | Coverage enforcement (#4), now against the current, refreshed 92.9%/93.4% baseline; then begin the match scoring / shooter-log service and controller layer (#6), following the same phased pattern that closed #1     |
| **Later**   | Clarify the remaining CSV persistence question (#3) for `AwardService`/`ImageService` as part of scoping the next domain feature                                                                                       |
| **Ongoing** | #5's overrides are gone as of v8.1.1; keep re-checking for new manual dependency-version overrides becoming redundant at each release per the Release Checklist                                                        |

---

## ✅ Success Criteria

- ✅ Met in v8.0.0: `IpscCompetitorController`/`IpscMatchController` expose real, tested endpoints backed by the
  existing entity/repository layer, closing the gap named identically in `README.md`, `ARCHITECTURE.md` and
  `CLAUDE.md`.
- `./mvnw verify -Pcoverage` (or equivalent) runs automatically on PRs to `develop`/`main`, so `ARCHITECTURE.md`'s
  CI/CD & Quality Gates table can drop the "locally / by reviewers" caveat on the `Build & Tests` row.
- Coverage regressions fail CI rather than being caught only when the next `HISTORY.md` entry is written.
- `ARCHITECTURE.md`'s CI/CD & Quality Gates table can drop its "no CI workflow wired up yet" caveat on the
  `Static Analysis` row once Qodana runs automatically alongside CodeQL.
- A real `MatchScoreController`/`ShooterLogController` (or equivalent) exists and is tested, closing the gap
  `README.md`, `ARCHITECTURE.md` and `CONTRIBUTING.md` currently described as "still being built".
- This document's Gaps section shrinks over time as items close — closed items should move into `HISTORY.md`'s
  per-version Future Roadmap notes rather than being deleted silently from here.

---

## 📚 Related Documentation

See `README.md`'s [📚 Documentation](/README.md#-documentation) section for the full documentation map. Most relevant to
this plan:

- [`ARCHITECTURE.md`](/ARCHITECTURE.md) — the CI/CD & Quality Gates table and layered-architecture rules this plan
  builds on
- [`AGENTS.md`](/AGENTS.md) — the Git Workflow and Release Checklist referenced throughout
- [`HISTORY.md`](/HISTORY.md) — per-release "🚀 Future Roadmap Implications" sections this plan complements
