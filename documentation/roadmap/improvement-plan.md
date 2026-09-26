# HPSC Website Backend Improvement Plan

This document synthesises the goals and constraints stated across this repository's documentation and configuration into
a single set of prioritised improvement opportunities. Unlike [`README.md`](/README.md) and 
[`ARCHITECTURE.md`](/ARCHITECTURE.md), it is not evergreen — it reflects a point-in-time reading of the project and
should be revisited whenever a major gap it names is closed or a new one is identified.

## Table of Contents

- [🎯 Purpose & Scope](#-purpose--scope)
- [⚙️ Goals & Constraints (Synthesised)](#-goals--constraints-synthesised)
- [🔍 Gaps & Improvement Opportunities](#-gaps--improvement-opportunities)
- [🛤️ Roadmap](#-roadmap)
- [☑️ Success Criteria](#-success-criteria)
- [🔗 Related Documentation](#-related-documentation)

---

## 🎯 Purpose & Scope

This plan draws only on what the repository already states about itself — `README.md`, `ARCHITECTURE.md`, `AGENTS.md`,
`CLAUDE.md`, `CONTRIBUTING.md`, `HISTORY.md`'s Future Roadmap sections, `pom.xml`, `application*.properties` and
`.github/workflows` — rather than introducing new goals. Where the documentation and the configuration disagree, or
where a stated goal has no corresponding work item yet, that gap is called out below as an improvement opportunity.

It complements, rather than duplicates, `HISTORY.md`'s per-release "🛤️ Future Roadmap Implications" section: that
section tracks what changed release-to-release, while this document tracks the standing, cross-release gaps
between the project's stated intent and its current state.

Because the "⚙️ Goals & Constraints" table below is synthesised partly from `HISTORY.md`'s Future Roadmap
Implications sections, check whether that table needs a matching update whenever `HISTORY.md` changes — most
concretely, whenever a release is being prepped and `HISTORY.md` gains its new Historical Timeline entry, per
`AGENTS.md`'s Release Checklist.

---

## ⚙️ Goals & Constraints (Synthesised)

| Source                                              | Goal / constraint                                                                                                                                                                                                                                                                                                                                                                            |
|-----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `README.md`, `ARCHITECTURE.md`                      | Rebuild the match/competitor domain's service and controller layer on top of the existing JPA entities and repositories — ✅ delivered in v8.0.0 as `IpscCompetitorService`/`IpscMatchService` and their controllers                                                                                                                                                                         |
| `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md`   | Build the match/competitor **scoring** and shooter-log service/controller layer over the existing JPA entities, repositories and already-fixed request DTOs — explicitly called out as still being built, not aspirational                                                                                                                                                                   |
| `ARCHITECTURE.md` (Layered Architecture)            | Strict unidirectional layering: Controller → Service → Repository → Database; no layer may skip the one below it, and controllers must carry no business logic                                                                                                                                                                                                                               |
| `ARCHITECTURE.md` (Exception handling), `CLAUDE.md` | All exceptions extend `FatalException`, `NonFatalException` or `ValidationException`, handled centrally by `ControllerAdvice` — never caught and rethrown as generic `RuntimeException`                                                                                                                                                                                                      |
| `ARCHITECTURE.md` (CI/CD & Quality Gates)           | Security analysis (CodeQL) and Build & Tests (`build.yml`, `./mvnw verify -Pcoverage`) are automatic gates on push/PR to `main`/`develop`; the latter also enforces a 97% JaCoCo line-coverage minimum, tightened to near the real baseline in v8.4.0 (Gap #4 closed; 98.77% line as of v8.9.0); Qodana static analysis was removed in v8.2.0 after never once succeeding in CI (see Gap #7) |
| `AGENTS.md` (Git Workflow, Release Checklist)       | GitFlow branching (`develop` → `release/vX.Y.Z` → `main`, `hotfix/*` direct to `main`), strict Semantic Versioning (classified from `[Unreleased]`, validated at release time) and a fixed, ordered release checklist covering `pom.xml`, `HpscWebApplication.java`, `CHANGELOG.md`, `HISTORY.md`, `RELEASE_NOTES.md` and archived per-version docs                                          |
| `AGENTS.md` (Documentation Conventions)             | British English spelling throughout prose and Javadoc; every heading carries a reused or deliberately new emoji; `README.md`/`ARCHITECTURE.md` stay version-agnostic (reverse-synced from release docs, not the other way round)                                                                                                                                                             |
| `AGENTS.md` (Test Conventions), `CLAUDE.md`         | Mockito-only controller tests (no Spring context), H2-backed service/repository integration tests, `<ClassName>Test` / `test<Scenario>_when<Condition>_then<Expectation>` naming, AssertJ unavailable (excluded in `pom.xml`)                                                                                                                                                                |
| `pom.xml`                                           | Track current Spring Boot / Java releases closely (Java 25, Spring Boot 4.1.1) — this currency itself creates a maintenance constraint, including a standing `tomcat.version` security override (see Gap #26 and [Gaps](#-gaps--improvement-opportunities))                                                                                                                                  |
| `application.properties` (prod/dev/test)            | Flyway is the schema source of truth for MySQL (prod/dev); the `test` profile bypasses it entirely via Hibernate `create-drop` against H2 — the two schema paths can silently diverge                                                                                                                                                                                                        |
| `CONTRIBUTING.md`, `application.properties`         | Five runtime profiles (none, `prod`, `dev`, special-purpose `local`, `test`) with different database engines and DDL strategies must all stay usable without extra setup burden for new contributors (documented as configured since Gap #27)                                                                                                                                                |

---

## 🔍 Gaps & Improvement Opportunities

Gaps are grouped by completion status — ✅ Completed, 🟡 Partially Completed, ⚪ Open — and numbered sequentially
across the whole document; a number is assigned once and never reused or resequenced, so it stays a gap's stable
identifier even after it moves between sections as its status changes (e.g. Open → Partially Completed → Completed).
Within each section, gaps stay in ascending number order.

Each gap looks for one of four things: a stated-but-unbuilt goal, a doc-vs-doc or doc-vs-code disagreement, a stale
number or a newly met precondition on an existing gap — see the `update-improvement-plan-gaps` skill.

### 🌳 At a Glance

- **✅ Completed (27):**
  - #1 Match/competitor service and controller layer — closed v8.0.0
  - #2 No automatic build/test gate on pull requests — closed v8.3.1
  - #3 Award/Image CSV pipelines never persist — closed v8.3.1 (confirmed deliberate, no persistence planned)
  - #4 Coverage measured but not enforced — closed v8.4.0 (JaCoCo floor raised 51% → 86% → 97%)
  - #5 `jackson-databind` version override is a standing manual constraint — closed v8.1.1
  - #7 Qodana static analysis is configured but never runs in CI — closed v8.2.0 (removed as not applicable)
  - #8 Match bulk CSV import remains removed pending a rebuild — closed v8.3.0
  - #9 `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` is declared but never applied — closed v8.4.0
  - #10 `HISTORY.md`'s Phase/Milestone entries haven't been extended since v8.4.0 — closed v8.5.1
  - #11 `HISTORY.md`'s forward-looking Future Roadmap lists still name delivered or renamed work — closed v8.6.2
  - #12 Competitor/match "full CRUD" claims have no delete operation behind them — closed v8.8.0
  - #13 Claude Code GitHub Actions workflows are missing from the CI/CD & Quality Gates documentation — closed
    v8.9.0
  - #14 `flyway-migration-versioning.md`'s Current State table hasn't been extended since v8.4.0 — closed v8.9.0
  - #15 `ARCHITECTURE.md`'s Quality Attributes table contradicts its own Persistence Layer section on JPA
    cascade/`mappedBy` — closed v8.9.0
  - #16 `CONTRIBUTING.md`'s Running Tests example names a test method that no longer exists — closed v8.9.0
  - #17 `HISTORY.md`'s Future Roadmap Implications "Recently Completed" log hasn't been extended since v8.4.0 —
    closed v8.9.0
  - #18 Tech-stack docs advertise XML and Apache Commons support the code never uses — closed v8.9.0
  - #19 `ARCHITECTURE.md`'s "Strategy Pattern" rows describe converters that don't exist — closed v8.9.0
  - #20 `AGENTS.md`'s 3-tier service test rule hasn't caught up with `TransactionService` — closed v8.9.0
  - #21 `ARCHITECTURE.md`'s data-flow and repository descriptions predate `TransactionService` and the delete
    work — closed v8.9.0
  - #22 `AGENTS.md` names the club differently from every other source — closed v8.9.0
  - #23 The `Competitor.homeClub` backfill is a stated goal with no gap tracking it — closed v8.9.0 (not
    applicable)
  - #24 Entity and repository test coverage is claimed but doesn't exist — closed v8.9.0
  - #25 Entity-level unit tests are a stated goal with no gap tracking it — closed v8.10.0 (`IpscMatchTest` only)
  - #27 Database-profile docs promise a setup the properties files don't provide — closed v8.10.0 (docs plus a
    new `prod` profile)
  - #28 `logback-spring.xml` configures a `staging` profile that exists nowhere else — closed v8.10.0 (block
    removed)
  - #29 Dependabot security-update PRs bypass the GitFlow rule that only `develop` and `hotfix/*` reach `main` —
    closed v8.10.1 (handled as hotfixes)
- **🟡 Partially Completed (1):**
  - #26 The `tomcat.version` override is an untracked standing manual constraint — progressed v8.10.0 (now
    re-checked at every release; the override stays until a Spring Boot GA release manages Tomcat `11.0.25`)
- **⚪ Open (1):**
  - #6 Match scoring / shooter-log service and controller layer are not yet built — current **Now** roadmap focus

### ✅ Completed

#### 1. Match/competitor service and controller layer is the single largest stated gap — ✅ Closed in v8.0.0

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
[`improvement-plan-tasks.md`](improvement-plan-tasks.md#-completed) for the full checklist.

#### 2. No automatic build/test gate on pull requests — ✅ Closed in v8.3.1

**Evidence:** `ARCHITECTURE.md`'s own CI/CD & Quality Gates table states the `Build & Tests` gate runs "locally / by
reviewers before merge" — `.github/workflows` contains only `codeql.yml` and, as of Gap #7, `qodana.yml`; neither
runs `./mvnw test`. `AGENTS.md`'s Merging rules require "all tests pass" before a `release/*` branch merges, but
nothing enforces that automatically.

**Why it matters:** A GitFlow model with `feature/*` → `develop` and `release/*` → `develop` → `main` promotion depends
on tests being genuinely green at each merge; today that depends entirely on reviewer discipline.

**Proposed improvement:** Add a `build.yml` (or extend `codeql.yml`'s trigger set) that runs `./mvnw verify -Pcoverage`
on push/PR to `develop` and `main`, mirroring CodeQL's existing trigger branches. This closes a gap the project's own
architecture document already names.

**Outcome:** Delivered as `.github/workflows/build.yml`, triggered on push/PR to `main`/`develop`, mirroring
`codeql.yml`'s trigger branches exactly as proposed. It sets up JDK 25 via `actions/setup-java` (Maven-cached), then
runs `sh ./mvnw --batch-mode verify -Pcoverage` — `sh` rather than a direct `./mvnw` invocation, since `mvnw` isn't
tracked with the execute bit in git and would otherwise fail with "Permission denied" on the Ubuntu runner — and
uploads the JaCoCo HTML/XML report as a build artefact. `ARCHITECTURE.md`/`CONTRIBUTING.md`'s CI/CD & Quality Gates
tables updated to match, dropping the stale "locally / by reviewers"/"All PRs" language. The `-Pcoverage` run also now
enforces Gap #4's coverage-check rule, closing that gap's CI-wiring half in the same workflow. Delivered on a
`feature/ci-build-test-gate` branch off `develop`, not a `release/*` branch; this release-prep pass fills in the
closing version, v8.3.1.

#### 3. Award/Image CSV pipelines never persist — ✅ Closed in v8.3.1

**Evidence:** `ARCHITECTURE.md`'s data-flow diagram for the only implemented pipeline notes `AwardService.createAwards()`/
`ImageService.createImages()` "parses CSV via Jackson CsvMapper, maps to response records — **no persistence**".

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

**Outcome:** Confirmed as deliberate: `AwardService`/`ImageService` CSV processing is intentionally stateless, not
an unfinished persistence layer. `README.md`'s Award Ceremonies/Image Gallery bullets and `ARCHITECTURE.md`'s
Service Layer table/Award-Image CSV Processing Flow section now state this explicitly, resolving the ambiguity this
gap's Proposed improvement asked to clarify. No persistence is planned for these two pipelines.

#### 4. Coverage is measured but not enforced — ✅ Closed in v8.4.0

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

**Further progress:** A `check` execution was added to the `coverage` profile's `jacoco-maven-plugin` (`BUNDLE`-level,
`LINE`/`COVEREDRATIO` minimum `0.51`), wired into #2's new `build.yml` gate — a regression now fails the build rather
than only surfacing in the next `HISTORY.md` entry, closing the CI-enforcement half of this gap's proposed
improvement. The chosen 51% floor is deliberately a low regression backstop, not "near the current baseline"
(~98%) as originally proposed here; tightening it closer to the real baseline is left as a deliberate follow-up
once the gate has run cleanly for a few releases, rather than risking a strict threshold blocking merges on day
one. Not marked fully closed for that reason. This lands in v8.3.1; `HISTORY.md`'s coverage figure refresh for the
current baseline (measured at 98.16%/98.94% line/branch as of this release-prep pass, 836 tests) is tracked as
this release's own task in `improvement-plan-tasks.md` rather than assumed done here.

**Tightened again (same branch, before v8.3.1 ships):** The `LINE`/`COVEREDRATIO` minimum was raised a second time,
from `0.51` to `0.86` (86%), directly in `pom.xml` — sooner than the "once the gate has run cleanly for a few
releases" plan stated just above, so worth confirming that acceleration is deliberate rather than reverting it here.
Still not marked fully closed: 86% is meaningfully closer to the real baseline than 51% was, but still short of
"near" the 98.16%/98.94% figure, and this new threshold hasn't yet run in CI to confirm it holds cleanly.

**Outcome:** The 86% floor was confirmed holding cleanly in CI (`build.yml` succeeded on both the `develop` push and
the `main` promotion that shipped v8.3.1). With that confirmed, the `LINE`/`COVEREDRATIO` minimum was tightened a
third time, from `0.86` to `0.97` (97%) directly in `pom.xml` — deliberately just under the real baseline
(98.16%/98.94% line/branch, 836 tests as of that pass, confirmed unchanged by a fresh local
`./mvnw verify -Pcoverage` run) rather than pinned exactly to it, leaving a small margin so ordinary line-count
fluctuation doesn't trip the gate while still being genuinely "near" the baseline this gap's Proposed improvement
asked for. Verified locally that `./mvnw verify -Pcoverage` passes cleanly at the new threshold before landing it.
The suite continued to grow afterwards, within the same v8.4.0 branch (further `resolveClub`/`FatalException`
coverage and Javadoc/member-ordering passes); a final `./mvnw verify -Pcoverage` re-run at this release's prep time
measured 98.44%/98.98% line/branch, 868 tests — still comfortably above the 97% floor. The `BRANCH` counter is
still not separately enforced — only `LINE`, as established when this gate was first added
in v8.3.1 — which remains a deliberate, documented deviation from the original "line/branch minimum" wording rather
than an oversight.

#### 5. `jackson-databind` version override is a standing manual constraint — ✅ Closed in v8.1.1

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

#### 7. Qodana static analysis is configured but never runs in CI — ✅ Closed as not applicable in v8.2.0

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

**Outcome:** Rather than fix the two issues Progress identified, `.github/workflows/qodana.yml` and `qodana.yaml`
were removed entirely in v8.2.0, along with every other reference to Qodana across `ARCHITECTURE.md`,
`CONTRIBUTING.md` and `AGENTS.md`'s CI/CD documentation. The gate had never once succeeded since it was added, so
there was no working baseline to preserve, and re-enabling it would still require provisioning a `QODANA_TOKEN`
secret this project doesn't currently have. This closes the gap as not applicable rather than as delivered — if
static analysis in CI is wanted again in the future, it should be scoped as a new gap rather than reopening this one.

#### 8. Match bulk CSV import remains removed pending a rebuild — ✅ Closed in v8.3.0

**Evidence:** `ARCHITECTURE.md`'s Feature Support and Service Layer tables described only competitor CRUD as having
"bulk CSV import", listing match CRUD without it; its Service Layer note stated explicitly, "the wider match domain's
bulk-import and entity-initialisation service layer remains removed pending a rebuild — competitor CRUD now also
supports bulk CSV import"; and its Data Flow section for the match/competitor bulk-import and CRUD flows "described
in earlier versions of this document" stated they "have been removed pending a rebuild", with the competitor bulk CSV
import flow "a new, unrelated implementation, not a restoration of that removed flow" — leaving the match domain's
equivalent undelivered and unmentioned as anything but historical.

**Why it matters:** The same shape of the gap that closed Gap #1 and is tracked as still-open for scoring/shooter-logs in
Gap #6 — a capability the project's own architecture documentation named as deliberately deferred, for the second of
the two domains (`IpscCompetitorController`/`IpscMatchController`) that share an otherwise-identical CRUD shape. Left
unclosed, the asymmetry between "competitor bulk import exists, match bulk import doesn't" has no roadmap entry
explaining whether it's intentional or simply not yet scheduled.

**Proposed improvement:** Apply the same mirrored pattern Gap #1's Outcome already used for the competitor domain:
introduce a `MatchRequestForCSV`/`MatchResponseHolder` pair alongside the existing `CompetitorRequestForCSV`/
`CompetitorResponseHolder`, and an `IpscMatchController.createMatches`/`IpscMatchService.createMatches` pair that
persists each CSV row via the existing single-`createMatch` validation/club/firearm-type/category-resolution logic —
no new cross-entity orchestration, per the discipline Gap #1 established.

**Outcome:** Delivered in v8.3.0. `MatchRequestForCSV` (`models/ipsc/match/request/`) mirrors
`CompetitorRequestForCSV`'s `UpperCamelCase` CSV/JSON `@JsonCreator` pattern, with its stages represented as a single
semicolon-separated `<stageNumber>:<stageName>` cell rather than a nested list (CSV has no native nested-row
representation). `MatchResponseHolder` mirrors `CompetitorResponseHolder`. `IpscMatchController.createMatches`
(`POST /ipsc/matches/bulk`, consumes `text/csv`) and `IpscMatchService`/`IpscMatchServiceImpl.createMatches` mirror
`IpscCompetitorController`/`IpscCompetitorServiceImpl`'s `createCompetitors` shape exactly: a `readMatches` CSV-parsing
helper, a `toRequest` row-to-`MatchRequest` mapper, and (new relative to the competitor flow, since matches have no
CSV-native nested-stage representation) a `parseStages` helper splitting the delimited `Stages` cell into
`MatchStageRequest`s. `ARCHITECTURE.md`'s stale "match bulk-import remains removed pending a rebuild" language and its
competitor-only endpoint/service/data-flow documentation are updated in the same release to reflect this.

#### 9. `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` is declared but never applied — ✅ Closed in v8.4.0

**Evidence:** `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER = ClubIdentifier.ALL` exists (added alongside
`HOME_CLUB_IDENTIFIER` this branch) but is referenced nowhere else in `src/` — grepping the whole tree for
`DEFAULT_MATCH_CLUB_IDENTIFIER` finds only its own declaration. `ClubIdentifier.ALL`'s own Javadoc states it "is
used in the Match domain to indicate that a match is hosted jointly by the three real clubs (`SOSC`, `HPSC` and
`PMPSC`), rather than by a single one of them" — a real, seeded club (`V7_3_0__seed_club_data.sql` inserts
`"Eufees Clubs"` / `ALL`). `IpscMatch.club` and the `ipsc_match.club_id` schema column are both nullable
(`V7_0_0__create_schema.sql`, no `nullable = false` on `IpscMatch`'s `@JoinColumn`), yet
`IpscMatchServiceImpl.validateForCreate` unconditionally rejects a missing club — `if ((request.getClub() == null)
|| request.getClub().isBlank()) { throw new ValidationException("Club is required."); }` — so there is no code
path where a match's club could ever actually default to `ClubIdentifier.ALL`, or be left unset at all.

**Why it matters:** The nullable schema column and the new constant both signal an intended "default to `ALL` when
unspecified" behaviour for joint-club matches, but nothing wires them together yet. A caller who wants to record a
joint-club match today has no shorthand for it — they'd have to already know to pass the literal seeded club name
`"Eufees Clubs"` — so the constant currently only documents an intention rather than doing anything.

**Proposed improvement:** Either (a) wire it in: when `club` is omitted on `createMatch`, resolve it via
`clubRepository.findByIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER)` instead of throwing, mirroring how
`IpscCompetitorServiceImpl.resolveHomeClub`/`resolveClubNumber` already treat an absent optional field as "apply
the domain default" rather than an error; or (b) if joint-club matches are meant to always be created by explicitly
naming `"Eufees Clubs"`, remove the unused constant rather than leaving inert groundwork in `IpscConstants`.

**Outcome:** Delivered option (a). `IpscMatchServiceImpl.validateForCreate` no longer rejects a missing/blank
`club`; `resolveClub` now resolves it via `clubRepository.findByIdentifier(IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER)`
instead, mirroring `IpscCompetitorServiceImpl.resolveHomeClub`/`resolveClubNumber`'s "apply the domain default"
pattern, and throwing `NonFatalException` if even the default club is missing. `MatchRequest`/`MatchRequestForCSV`'s
`club` field Javadoc now documents the default explicitly.

#### 10. `HISTORY.md`'s Phase/Milestone entries haven't been extended since v8.4.0 — ✅ Closed in v8.5.1

**Evidence:** `HISTORY.md`'s "📅 Historical Timeline" section has an entry for every shipped release through v8.5.0
(the current version), but its "📖 Evolution Overview" (`### Phase N: ...`) and "🎯 Major Milestones"
(`### Milestone N: ...`) sections both stop at "Phase 25"/"Milestone 25: Club Domain Defaults, Optional Club
Numbers & Documentation Convention Hardening (v8.4.0)" — no Phase 26/27/28 or Milestone 26/27/28 exists for
v8.4.1 ("Documentation Cross-Reference Consolidation & Icon Registry Sync"), v8.4.2 ("Root Document Title
Standardisation & Source-of-Truth Clarification") or v8.5.0 ("Match Start/End Time Tracking"), even though each of
those three has its own Historical Timeline entry.

**Why it matters:** `AGENTS.md`'s Release Checklist step 6 states, unconditionally, "Add a Historical Timeline
entry, a Phase and a Milestone for the new version" — distinct from the following sentence's *conditional*
"if the release is significant enough" language, which applies only to threading the release through
Architectural Evolution, Feature Timeline, Key Learnings, Future Roadmap Implications and the Conclusion/footer.
Three consecutive releases skipped the mandatory Phase/Milestone step, including v8.5.0, which shipped a real
schema/API/service change (`IpscMatch.startTime`/`endTime`) comparable in scope to v8.4.0's, which did get a
Milestone. A reader following the Evolution Overview/Major Milestones narrative sees it jump straight from v8.4.0
to nothing, understating three releases' worth of actual change.

**Proposed improvement:** Add Phase 26/27/28 (Evolution Overview) and matching Milestone 26/27/28 (Major
Milestones) entries for v8.4.1, v8.4.2 and v8.5.0, at the same narrative depth and style as the existing entries,
summarising each release's already-written Historical Timeline content rather than researching it from scratch.
Then re-check whether this release (currently `release/v8.5.1`) needs its own Phase/Milestone once its scope is
final, per the Release Checklist's standing step 6.

**Outcome:** Delivered exactly as proposed. `HISTORY.md`'s "📖 Evolution Overview" gained "Phase 26: Documentation
Cross-Reference Consolidation & Icon Registry Sync (v8.4.1)", "Phase 27: Root Document Title Standardisation &
Source-of-Truth Clarification (v8.4.2)" and "Phase 28: Match Start/End Time Tracking (v8.5.0)"; "🎯 Major
Milestones" gained the matching Milestone 26/27/28 entries — all four summarising each release's already-written
Historical Timeline content, at the same narrative depth as the surrounding entries. This release
(`release/v8.5.1`) went on to need its own Phase/Milestone too: "Phase 29"/"Milestone 29" (HISTORY.md Phase/
Milestone Backfill & Release Re-Scoping to a Patch Version) landed during this release's own prep pass, closing the
Proposed improvement's second step. `HISTORY.md`'s "📖 Evolution Overview", "🎯 Major Milestones",
"🏛️ Architectural Evolution" and "🗺️ Future Roadmap Implications" sections were then reordered from
most-recent-first to ascending (oldest-first), matching "✨ Feature Timeline"/"💡 Project Philosophy Evolution"'s
existing convention — only "📅 Historical Timeline" keeps its most-recent-first order.

#### 11. `HISTORY.md`'s forward-looking Future Roadmap lists still name delivered or renamed work — ✅ Closed in v8.6.2

**Evidence:** `HISTORY.md`'s "🗺️ Future Roadmap Implications" section ends with three forward-looking lists —
"Short-term (Minor Releases)", "Medium-term (v7.x+)" and "Long-term (Future Major Versions)" — whose content was
last substantively updated in v8.0.0 (commit `fe72b9c` added the "`homeClub` now wired via `IpscCompetitorService`"
note); the "Medium-term (v7.x+)" heading and the club-seeding bullet date back to v7.0.0 (commit `a862701`). Three
of their claims no longer match the code:

- "Seed `Club.identifier` (HPSC, SOSC, PMPSC) and backfill `Competitor.homeClub`" is still listed as outstanding,
  yet `V7_3_0__seed_club_data.sql` (commit `a225eab`, shipped in v8.4.0) already seeds the `club` table with every
  named `ClubIdentifier` — `SOSC`, `HPSC`, `PMPSC`, `VISITOR` and `ALL` — and `HISTORY.md`'s own v8.4.0 Historical
  Timeline entry and "Recently Completed (v8.4.0)" list record that migration as delivered.
- "Wire service/controller/import support for `clubRanking`, `isVisitor`, `ShooterLog` and `ShooterLogEntry`" names
  `ShooterLogEntry`, which was renamed to `ShooterLogCompetitor` in v7.1.0 — no `ShooterLogEntry` class exists
  anywhere under `src/` (`domain/` holds `ShooterLog.java`/`ShooterLogCompetitor.java`), and the same file's own
  v7.1.0 entries record the rename.
- The "Medium-term (v7.x+)" heading still targets a major version the project has already moved past (current:
  v8.6.x), and its "Bulk match processing capabilities" bullet overlaps v8.3.0's delivered
  `IpscMatchController.createMatches` bulk CSV import (Gap #8), without saying whether something beyond it is meant.

**Why it matters:** This plan's own "🎯 Purpose & Scope" names `HISTORY.md`'s Future Roadmap sections as one of the
sources its "⚙️ Goals & Constraints" table is synthesised from, so stale items there can resurface as phantom goals
in any future re-synthesis. A reader of `HISTORY.md` also sees already-shipped work (club seeding) presented as still
outstanding, next to an entity name that no longer exists — the same kind of doc-vs-code drift Gap #10 closed for
the Phase/Milestone sections.

**Proposed improvement:** Refresh the three lists against what has actually shipped: drop (or mark delivered) the
club-seeding bullet — keeping the `Competitor.homeClub` backfill half only if it is still genuinely wanted — rename
`ShooterLogEntry` to `ShooterLogCompetitor`, relabel "Medium-term (v7.x+)" for the current major version, and
either drop "Bulk match processing capabilities" as delivered by Gap #8 or reword it to name what is still missing.
Items that overlap Gap #6 (the `ShooterLog` calculation service and scores-request wiring) can stay, since that gap
is still open. No code change is needed — this is a `HISTORY.md`-only pass, best done during a release-prep pass per
`AGENTS.md`'s Release Checklist.

**Outcome:** Delivered as proposed, in `HISTORY.md` only. The club-seeding bullet was reduced to its still-outstanding
half — "Backfill `Competitor.homeClub` for existing competitors", now noting that the `club` table itself was
already seeded in v8.4.0 via `V7_3_0__seed_club_data.sql` — since no migration or service backfills `homeClub` for
pre-existing competitors yet. `ShooterLogEntry` was renamed to `ShooterLogCompetitor` in the Short-term wiring
bullet, "Medium-term (v7.x+)" was relabelled "Medium-term (Later v8.x Releases)", and "Bulk match processing
capabilities" was dropped as delivered by Gap #8's v8.3.0 bulk CSV import. The items overlapping Gap #6 (the
`ShooterLogService` calculation service and scores-request wiring) were left in place, since that gap is still open.

#### 12. Competitor/match "full CRUD" claims have no delete operation behind them — ✅ Closed in v8.8.0

**Evidence:** `README.md` (lines 30–31 and 68) and `ARCHITECTURE.md` (the Feature Support table's "IPSC
Competitors & Matches" row, plus the Controllers and Services tables) describe `IpscCompetitorController`/
`IpscMatchController` and their services as "Full CRUD" / "CRUD" for competitors and matches (with stages), and
`documentation/recommendations/standard-rest-conventions.md`'s "🔍 Current State in This Codebase" calls
`IpscMatchController` "this codebase's clearest example of the full pattern". Yet neither controller declares a
`@DeleteMapping` handler, and neither `IpscCompetitorService` nor `IpscMatchService` declares a `delete*` method —
grepping `controllers/` and `services/` for `DeleteMapping`/`delete` finds nothing. The only deletion anywhere is
`IpscMatchServiceImpl.replaceStages` removing a match's existing stages as part of a `PUT`/`PATCH`. The same
recommendations section also lists only `createMatch`/`updateMatch`/`patchMatch`/`getMatch`, leaving out the
existing `getAllMatches` collection endpoint and not mentioning `IpscCompetitorController`, which (as of
`getAllCompetitors`) now follows the same pattern.

**Why it matters:** A client reading `README.md` or `ARCHITECTURE.md` expects to be able to remove a competitor
or match created by mistake, for example, a bad bulk CSV import row, and there is no API path to do so short of
editing the database directly. The REST conventions document is meant to be the worked
example for new controllers, so an incomplete "full pattern" there spreads to whatever controller is built next
(Gap #6's scoring/shooter-log layer).

**Proposed improvement:** Either (a) add `deleteCompetitor`/`deleteMatch` (`DELETE /{competitorId}`,
`DELETE /{matchId}`) through the service layer, deciding up front how deletion interacts with dependent rows
(`MatchCompetitor` and `ShooterLog` referencing a competitor; `IpscMatchStage`, `MatchCompetitor` and
`ShooterLogCompetitor` referencing a match), rejecting or cascading explicitly rather than surfacing a raw
foreign-key violation; or (b) if records are intentionally never deleted through the API, reword the "CRUD"
claims in `README.md`/`ARCHITECTURE.md` to "create, read and update" and say so. Either way, refresh
`standard-rest-conventions.md`'s current-state examples to name `getAllMatches`/`getAllCompetitors` and
`IpscCompetitorController`.

**Outcome:** Delivered option (a). `IpscCompetitorService.deleteCompetitor`/`IpscMatchService.deleteMatch` back new
`DELETE /ipsc/competitors/{competitorId}` and `DELETE /ipsc/matches/{matchId}` endpoints
(`IpscCompetitorController.deleteCompetitor`/`IpscMatchController.deleteMatch`), each returning `204 No Content`.
Dependent rows are handled by rejecting, not cascading: a competitor still referenced by `MatchCompetitor` or
`ShooterLog` rows, or a match still referenced by `MatchCompetitor`, `MatchStageCompetitor` or
`ShooterLogCompetitor` rows, is refused with a `ValidationException` (`400`) via new `existsBy…` repository
queries, so scoring history is never silently destroyed. What a record owns goes with it — a competitor's
`competitor_email` rows through its `@ElementCollection`, a match's `IpscMatchStage` rows removed as managed
entities before the match itself. Covered at all three test tiers, including H2-backed integration tests for the
reject and delete paths. The "CRUD" wording in `README.md`/`ARCHITECTURE.md` is therefore now accurate and was
left as is; `ARCHITECTURE.md` gained a note on the reject-not-cascade rule, and
`standard-rest-conventions.md`'s current-state examples now cover both controllers' full `getAll`/`get`/`create`/
`update`/`patch`/`delete` sets.

#### 13. Claude Code GitHub Actions workflows are missing from the CI/CD & Quality Gates documentation — ✅ Closed in v8.9.0

**Evidence:** `.github/workflows/` holds four workflows — `build.yml`, `codeql.yml`, `claude.yml` and
`claude-code-review.yml` — but `ARCHITECTURE.md`'s "🔬 CI/CD & Quality Gates" table lists only CodeQL (Security
Analysis) and `build.yml` (Build & Tests, plus the JaCoCo check it enforces), and `CONTRIBUTING.md`'s own CI/CD
section summarises that table's scope as "CodeQL security analysis, Maven build and tests, JaCoCo coverage".
`claude-code-review.yml` (added in commit `33de202`) runs `anthropics/claude-code-action` on every pull request
(`opened`, `synchronize`, `ready_for_review`, `reopened`), and `claude.yml` (commit `e62888b`) responds to `@claude`
mentions in issues, PR comments and reviews; both authenticate with a `CLAUDE_CODE_OAUTH_TOKEN` repository secret.
Unlike the Qodana gate Gap #7 removed, both are live — `gh run list --workflow=claude-code-review.yml` shows every
recent release PR's review run succeeding. `ARCHITECTURE.md`'s Project Structure tree still describes
`.github/workflows/` as "GitHub Actions — CI/CD, CodeQL".

**Why it matters:** A contributor reading `ARCHITECTURE.md`/`CONTRIBUTING.md` doesn't learn that every pull request
gets an automated AI review, that `@claude` can be invoked on issues and PRs or that both depend on a repository
secret that must stay provisioned — the same kind of doc-vs-code drift Gap #2 and Gap #7 closed for the build and
static-analysis gates. The review workflow's commented-out `paths:` filter also still names TypeScript/JavaScript
globs from its template, a hint it was added as-is rather than tailored to this Java project.

**Proposed improvement:** Add the two workflows to `ARCHITECTURE.md`'s CI/CD & Quality Gates table — e.g. an
"Automated Code Review" row for `claude-code-review.yml` (every PR, advisory rather than merge-blocking) and an
"AI Assistant" row for `claude.yml` (on `@claude` mention) — noting the `CLAUDE_CODE_OAUTH_TOKEN` secret they rely
on, and widen the Project Structure tree's `.github/workflows/` comment generically (e.g. "GitHub Actions — CI/CD,
security analysis and automated review"). `CONTRIBUTING.md`'s summary line then only needs its parenthetical scope
list extended to match. Optionally drop or tailor the review workflow's leftover template `paths:` comment.

**Outcome:** Delivered as proposed. `ARCHITECTURE.md`'s CI/CD & Quality Gates table gained an "Automated Code
Review" row for `claude-code-review.yml` (every PR, advisory only) and an "AI Assistant" row for `claude.yml` (on an
`@claude` mention), with a note beneath it on the `CLAUDE_CODE_OAUTH_TOKEN` secret both depend on; its Project
Structure tree now describes `.github/workflows/` as "CI/CD, security analysis and automated review".
`CONTRIBUTING.md`'s summary line and `AGENTS.md`'s pointer to the table were extended to match. The review
workflow's leftover TypeScript/JavaScript `paths:` template comment was tailored to Java globs rather than dropped,
keeping the opt-in filter available. Shipped in v8.9.0.

#### 14. `flyway-migration-versioning.md`'s Current State table hasn't been extended since v8.4.0 — ✅ Closed in v8.9.0

**Evidence:** `documentation/recommendations/flyway-migration-versioning.md`'s "🔍 Current State in This Codebase"
table lists only `V7_0_0__create_schema.sql` through `V7_3_0__seed_club_data.sql`. `src/main/resources/db/migration/`
now holds five more: `V7_4_0__make_club_number_nullable.sql` (shipped v8.4.0),
`V7_5_0__add_ipsc_match_start_end_time.sql` (v8.5.0), `V7_6_0__add_ipsc_match_url.sql` and
`V7_7_0__change_ipsc_match_start_end_time_to_time.sql` (both v8.6.0), and
`V7_8_0__add_competitor_paid_up_flags.sql` (this branch, still under `CHANGELOG.md`'s `### 🧪 [Unreleased]`).

**Why it matters:** This is the document that explains and demonstrates the project's Flyway-versioning convention —
referenced from `AGENTS.md`'s Tech Stack section and `CONTRIBUTING.md`'s Database Profiles section — so a table that
stops five migrations short of the real history undersells its own "the two counters diverge" argument, missing
exactly the rows a reader would want to check against.

**Proposed improvement:** Add a row per missing migration (`V7_4_0` through `V7_8_0`), following the existing
table's "Shipped in app version"/"Notes" shape — most need no more than the version and a `—`, the way `V7_1_0`'s
row already does for a schema-only change. Fold this refresh into a recurring release-prep check (alongside
`AGENTS.md`'s Release Checklist step verifying `ARCHITECTURE.md`'s Project Structure tree), so it doesn't drift
five versions behind again.

**Outcome:** Delivered differently from the exact proposal: rather than relying on a Release Checklist step, a new
step 5 was added directly to this document's own "🔢 Choosing the Next Version" section, instructing whoever adds
the next migration to also add its row to the Current State table — the table drifted five migrations behind
precisely because no step told an author to update it. The five missing rows (`V7_4_0` through `V7_8_0`) were added
now, with `V7_8_0__add_competitor_paid_up_flags.sql`'s row updated to v8.9.0 at release-prep time. Shipped in
v8.9.0.

#### 15. `ARCHITECTURE.md`'s Quality Attributes table contradicts its own Persistence Layer section on JPA cascade/`mappedBy` — ✅ Closed in v8.9.0

**Evidence:** `ARCHITECTURE.md:387`'s Quality Attributes table states, under "Data Integrity": "JPA cascade rules,
bidirectional `mappedBy` declarations, `@Transactional` service methods, custom attribute converters." Two
subsections earlier, `ARCHITECTURE.md:201` states the opposite, explicitly: "No entity declares a back-referencing
`@OneToMany` collection, so there is no `mappedBy` anywhere in the domain model." `grep -rn
"cascade\|mappedBy\|OneToMany" src/main/java/za/co/hpsc/web/domain/` returns zero matches, confirming line 201 is
the accurate one. Separately, `ARCHITECTURE.md:405` attributes "database profiles" documentation to `README.md`
("See README.md's ... section ... commands, database profiles and coding standards"), but `README.md` itself only
links out to `CONTRIBUTING.md`'s "🗄️ Database Profiles" section (confirmed at `CONTRIBUTING.md:73`) rather than
documenting it directly.

**Why it matters:** The same document contradicts itself on a fairly fundamental persistence-layer claim — a reader
who reads the Quality Attributes table first would expect cascade/`mappedBy` behaviour the domain model doesn't
have, which matters more now that Gap #12 added explicit reject-not-cascade delete logic precisely because there's
no cascade to lean on. The `README.md` misattribution is a smaller instance of the same "which doc actually owns
this" drift.

**Proposed improvement:** Correct line 387 to match line 201's accurate description (e.g. "explicit `@ManyToOne`-only
associations, no cascade or `mappedBy`, `@Transactional` service methods, custom attribute converters"), and correct
line 405's cross-reference to point at `CONTRIBUTING.md` for database profiles rather than `README.md`.

**Outcome:** Delivered differently from the exact proposal: rather than rewording line 387 down to line 201's "no
cascade or `mappedBy`", the domain model was changed so the Quality Attributes table's claim holds where it makes
sense. `IpscMatch` gained a `stages` collection —
`@OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)` — since a stage can't exist
without its match; `IpscMatchServiceImpl.deleteMatch` now relies on that cascade instead of deleting stages itself,
and `replaceStages`/`upsertStages` keep the collection in step with the stages they persist. Every other relationship
stays unidirectional and uncascaded, keeping Gap #12's reject-not-cascade rule for records still referenced by
results or shooter logs. `ARCHITECTURE.md`'s Persistence Layer paragraph, entity table and "Data Integrity" row now
all describe exactly that, and its Development Guidelines paragraph points at `CONTRIBUTING.md` for database
profiles. No Flyway migration was needed, since `mappedBy` adds no column. Shipped in v8.9.0.

#### 16. `CONTRIBUTING.md`'s Running Tests example names a test method that no longer exists — ✅ Closed in v8.9.0

**Evidence:** `CONTRIBUTING.md:101`'s single-test example reads
`./mvnw test -Dtest=AwardControllerTest#testProcessCsv_whenValidCsvData_thenReturns200`. `AwardControllerTest.java`
has no `testProcessCsv_*` method — every method was renamed to `testCreateAwards_*` (e.g.
`testCreateAwards_whenValidCsvData_thenReturns200`) when `AwardService.processCsv`/`AwardController` were renamed to
`createAwards`. A repo-wide grep for `testProcessCsv_whenValidCsvData_thenReturns200` finds only this one line.

**Why it matters:** This is `CONTRIBUTING.md`'s worked example for running a single test — a new contributor
copy-pasting it gets a "no tests found" failure, undermining the exact onboarding step it's meant to demonstrate.

**Proposed improvement:** Update the example to name an existing test, e.g.
`AwardControllerTest#testCreateAwards_whenValidCsvData_thenReturns200`.

**Outcome:** Delivered as proposed: the single-test example now reads
`./mvnw test -Dtest=AwardControllerTest#testCreateAwards_whenValidCsvData_thenReturns200`, confirmed to run the one
existing test. Shipped in v8.9.0.

#### 17. `HISTORY.md`'s Future Roadmap Implications "Recently Completed" log hasn't been extended since v8.4.0 — ✅ Closed in v8.9.0

**Evidence:** `HISTORY.md:2007`'s "🛤️ Future Roadmap Implications" section opens "Based on the evolution to v8.4.0,
the following areas are identified for future enhancement," and its rolling per-release `### Previously Completed
(vX.Y.Z)` / `### Recently Completed (vX.Y.Z)` log stops at `### Recently Completed (v8.4.0)`. Nine shipped releases
are missing from this log: v8.4.1, v8.4.2, v8.5.0, v8.5.1, v8.6.0, v8.6.1, v8.6.2, v8.7.0 and v8.8.0. This is
distinct from Gap #10 (which extended the separate "📖 Evolution Overview"/"🎯 Major Milestones" sections, and does
correctly reach "Milestone 34: ... (v8.8.0)") and from Gap #11 (which only reworded three stale bullets inside the
Short-term/Medium-term/Long-term forward-looking lists that follow this log, not the log itself or its intro
sentence).

**Why it matters:** The same category of drift Gap #10 closed for Evolution Overview/Major Milestones, now found in
a different subsection under the same "🛤️ Future Roadmap Implications" heading — a reader following this log
specifically sees it stop at v8.4.0 even though four Milestones and nine point releases have shipped since.

**Proposed improvement:** Rename the current final entry to `### Previously Completed (v8.4.0)` and add a
`### Recently Completed (vX.Y.Z)` entry for each of the nine missing releases, summarising each release's
already-written Historical Timeline/`CHANGELOG.md` content at the same depth as the existing entries, and update
the section's opening sentence from "Based on the evolution to v8.4.0" to the current version. Apply this as a
recurring Release Checklist check alongside Gap #10's Phase/Milestone step, so this subsection doesn't fall behind
again.

**Outcome:** Delivered as proposed. `### Recently Completed (v8.4.0)` became `### Previously Completed (v8.4.0)`,
new entries were added for v8.4.1 through v8.7.0 (as Previously Completed) and v8.8.0 (as Recently Completed), each
summarising that release's Historical Timeline entry, and the opening sentence now reads "Based on the evolution to
v8.8.0". Rather than a separate Release Checklist step, the root cause was fixed in place: `AGENTS.md`'s Release
Checklist step 6 and the `prep-version-release` skill's matching step had listed the Future Roadmap among the
sections to update only "if the release is significant enough", so both now make this log update unconditional.
Shipped in v8.9.0.

#### 18. Tech-stack docs advertise XML and Apache Commons support the code never uses — ✅ Closed in v8.9.0

**Evidence:** `README.md:57` and `ARCHITECTURE.md:41` list "Jackson (JSON, CSV, XML), Apache Commons Lang3" in the
tech stack, and `AGENTS.md:45` lists "Jackson (JSON/CSV/XML)". `pom.xml` (lines 128–141) declares
`jackson-dataformat-xml`, `commons-lang3` and `commons-text` — the last hand-pinned to `1.15.0`, added on the
v8.9.0 branch (commit `8b1d57c`). A grep of `src/` for `org.apache.commons` or `XmlMapper` finds nothing: no class
imports any of the three.

**Why it matters:** Three unused dependencies widen the attack surface and dependency-update load for nothing — and
`commons-text`'s manual version pin is exactly the kind of standing override Gap #5 cleared out — while the docs
describe XML handling and Commons utilities the code doesn't have. `jackson-dataformat-xml` also silently enables
Spring MVC's XML content negotiation, an undocumented side effect of a dependency nothing uses deliberately.

**Proposed improvement:** Either drop the three dependencies and correct the three tech-stack lines, or — if
`commons-text` is groundwork for upcoming work — record why each is kept so the next audit doesn't re-flag it.

**Outcome:** Delivered as proposed, dropping all three: `jackson-dataformat-xml`, `commons-lang3` and
`commons-text` (with its `1.15.0` pin) are gone from `pom.xml`, and the tech-stack lines in `README.md`,
`ARCHITECTURE.md` and `AGENTS.md` now list Jackson for JSON/CSV only. Nothing in `src/` produced or consumed XML, so
dropping `jackson-dataformat-xml` removes only the unused XML content negotiation.

#### 19. `ARCHITECTURE.md`'s "Strategy Pattern" rows describe converters that don't exist — ✅ Closed in v8.9.0

**Evidence:** `ARCHITECTURE.md:312`'s Key Design Patterns table reads "Strategy Pattern | CSV/XML converters
(`converters/` package) handle format variants behind a common interface", and the Quality Attributes table's
Extensibility row (`:395`) credits "strategy-pattern converters". But the same file's Project Structure tree (`:69`)
and Custom JPA Attribute Converters table — and the code — show `converters/` holds only seven enum
`AttributeConverter<…, String>` classes (e.g. `ClubIdentifierConverter`); no CSV/XML converter exists.

**Why it matters:** A reader looking for the "common interface" behind CSV/XML format variants won't find one, and
the claim contradicts the Custom JPA Converters row directly beneath it, which already describes the package
accurately.

**Proposed improvement:** Remove the Strategy Pattern row (or reword it to something real) and replace
"strategy-pattern converters" in the Extensibility row with an accurate description.

**Outcome:** Delivered by replacement rather than rewording: the Key Design Patterns table's "Strategy Pattern"
row is now a "Transaction Boundary" row describing `TransactionService` — a pattern the code really has — and the
Extensibility row credits the enum `AttributeConverter`s and their `fromX` lookups instead of "strategy-pattern
converters".

#### 20. `AGENTS.md`'s 3-tier service test rule hasn't caught up with `TransactionService` — ✅ Closed in v8.9.0

**Evidence:** `AGENTS.md:415`'s Test Conventions state "All four services (`AwardService`, `ImageService`,
`IpscCompetitorService`, `IpscMatchService`) follow this split; a new service should too." v8.9.0 added a fifth,
`TransactionService`, tested only by `TransactionServiceImplTest` — there is no `TransactionServiceTest` or
`TransactionServiceIntegrationTest`, though the IPSC services' integration tests (including new tests run without a
surrounding transaction) exercise it end to end.

**Why it matters:** The rule's own count is now wrong, and it leaves unclear whether `TransactionService` is a
deliberate exception or an unfinished split — the next service author can't tell which to follow.

**Proposed improvement:** Either add the missing tiers, or record `TransactionService` as a deliberate exception (a
commit-only service whose contract is covered through the IPSC services' integration tests) and correct the count.

**Outcome:** Delivered by adding the missing tiers: the public-contract tests moved out of
`TransactionServiceImplTest` into a new `TransactionServiceTest` (exercised through the interface), leaving the impl
test with the protected `loadAssociations`/`replaceStages`/`upsertStages` helpers, and a new
`TransactionServiceIntegrationTest` commits and rolls back against real H2. That integration test is deliberately not
`@Transactional` — a surrounding test transaction would hide whether `TransactionService` really commits — and cleans
up its committed data after each test. `AGENTS.md`'s rule now names all five services and records that exception.

#### 21. `ARCHITECTURE.md`'s data-flow and repository descriptions predate `TransactionService` and the delete work — ✅ Closed in v8.9.0

**Evidence:** The Competitor/Match Bulk CSV Import flows (`ARCHITECTURE.md:357–358`, `372–374`) still say each row
is persisted "via the same `createCompetitor`/`createMatch` logic", although the Service Layer note (`:172–174`)
and the code (`IpscCompetitorServiceImpl.createCompetitors`, `IpscMatchServiceImpl.createMatches`) now build every
row first and save them all in one `TransactionService` call. The System Overview (`:121–127`) and Typical
Request-Response Flow (`:322–331`) diagrams have no `TransactionService` step, although `AGENTS.md`/
`CONTRIBUTING.md`'s layer diagrams do. The note at `:380–383` still says the CRUD flows "have been removed pending a
rebuild", though CRUD shipped in v8.0.0. The Project Structure tree (`:84`) says repositories are "IPSC ones wired
to services, the rest not yet wired", but all eight are now injected — the scoring/shooter-log ones for the
`existsBy…` delete checks — and the Repositories section's examples (`:230`) name `findAllByClubId`/
`findAllByCompetitorIdAndFirearmTypeAndPowerFactor`, which nothing calls, rather than the fetch-join queries the
services depend on.

**Why it matters:** These are the diagrams a new contributor reads to learn how a write reaches the database, and
they now describe a per-row, service-level persistence path that no longer exists. Gap #6's own Evidence quotes the
"not yet wired" comment as proof of that gap, so it has drifted too.

**Proposed improvement:** Redraw the overview and request flows through `TransactionService`, reword the bulk-import
flows to "builds each row, then saves all rows in one transaction", drop the stale removed-flows note, describe the
current repository wiring with the fetch-join and `existsBy…` queries as examples and refresh Gap #6's Evidence to
match.

**Outcome:** Delivered as proposed. `ARCHITECTURE.md`'s overview and Typical Request-Response Flow diagrams now
route writes through `TransactionService` (reads go straight to repositories); both bulk-import flows describe
building every row and then saving them in one `saveCompetitors`/`saveMatches` transaction; the stale
"removed pending a rebuild" note is gone; the Project Structure tree's `repositories/` comment reads "one per
entity"; and the Repositories section's examples are now the fetch-join and `existsBy…` queries the services rely
on. Gap #6's Evidence gained a note that its quoted "not yet wired" comment has changed, without altering the gap
itself.

#### 22. `AGENTS.md` names the club differently from every other source — ✅ Closed in v8.9.0

**Evidence:** `AGENTS.md:28` introduces the project as serving the "Handgun and Practical Shooting Club (HPSC)",
while `README.md` (lines 3 and 24), `ARCHITECTURE.md:3`, `LICENSE.md:3` and `ClubIdentifier`'s own Javadoc all say
"Hartbeespoortdam Practical Shooting Club".

**Why it matters:** `AGENTS.md` declares itself the project's ultimate source of truth, so its one factual error
about what "HPSC" stands for is the version AI agents are most likely to repeat.

**Proposed improvement:** Correct `AGENTS.md:28` to "Hartbeespoortdam Practical Shooting Club".

**Outcome:** Delivered as proposed: `AGENTS.md`'s Project Overview now expands HPSC as the "Hartbeespoortdam
Practical Shooting Club".

#### 23. The `Competitor.homeClub` backfill is a stated goal with no gap tracking it — ✅ Closed as not applicable in v8.9.0

**Evidence:** `HISTORY.md`'s Future Roadmap Implications Short-term list includes "Backfill `Competitor.homeClub` for
existing competitors — the `club` table itself is already seeded (v8.4.0, `V7_3_0__seed_club_data.sql`)", and Gap
#11's Outcome confirms "no migration or service backfills `homeClub`". None of `V7_0_0` through `V7_8_0` does so.

**Why it matters:** Competitors imported before `homeClub` existed carry no home club, and since v8.4.0 a competitor's
club number is only kept when their home club is HPSC — so those records can't be classified correctly until the
backfill happens, yet nothing here tracks it.

**Proposed improvement:** Decide whether the backfill is still wanted. If so, add a data migration (or a one-off
service operation) that derives `home_club_id` from existing data; if not, drop the bullet from `HISTORY.md`.

**Outcome:** Closed as not applicable. No existing column reliably identifies a competitor's home club — a
club number is only kept for HPSC members — so a backfill would have to guess. Home clubs are instead set through
the competitor update/patch endpoints or a CSV re-import, and the backfill bullet was removed from `HISTORY.md`'s
Short-term roadmap.

#### 24. Entity and repository test coverage is claimed but doesn't exist — ✅ Closed in v8.9.0

**Evidence:** `README.md`'s Testing section claims unit tests for "Domain entities" (line 145), "Domain entities and
JPA relationships" (line 151) and "Repository operations" (line 155), and `HISTORY.md`'s Short-term list still
plans to "Add entity, repository and integration test coverage for the promoted/extended domain model". There is no
`src/test/java/za/co/hpsc/web/domain/` or `repositories/` directory: repositories are exercised only indirectly,
through the IPSC services' integration tests.

**Why it matters:** The README overstates what the suite verifies. The persistence behaviour v8.9.0 made load-bearing
— `IpscMatch.stages`' cascade and orphan removal, and the fetch-join queries the read endpoints now depend on to avoid
`LazyInitializationException` — is only tested as a side effect of service tests.

**Proposed improvement:** Add `@DataJpaTest`-style repository/entity tests for the cascade, the fetch-join queries
and the `existsBy…` checks, or reword `README.md`'s Testing section to describe the coverage that actually exists.

**Outcome:** Delivered both halves. New Spring-context integration tests under `src/test/.../repositories/`
cover `IpscMatchRepository`'s and `CompetitorRepository`'s fetch-join queries (moved there from the service
integration tests), `IpscMatch.stages`' cascade persist, orphan removal and cascade delete, the email element
collection's delete, and every `existsBy…` query behind the reject-not-cascade deletes, with a small
`ScoringFixtures` helper for the scoring/shooter-log records. `README.md`'s Testing section now describes the suite
as it is, without claiming domain-entity unit tests, and `HISTORY.md`'s Short-term roadmap narrows the remaining
work to entity-level unit tests.

#### 25. Entity-level unit tests are a stated goal with no gap tracking it — ✅ Closed in v8.10.0

**Evidence:** `HISTORY.md:2370`'s Short-term list plans to "Add entity-level unit tests for the promoted/extended
domain model", and `documentation/history/v8/RELEASE_NOTES_v8.9.0.md` carries the same item under both Known Issues
("The domain model has repository integration tests but no entity-level unit tests") and Future Enhancements. Gap #24
closed only the repository half of the original claim, and its Outcome records that `HISTORY.md` merely "narrows the
remaining work to entity-level unit tests" — nothing here tracks that remainder. There is still no
`src/test/java/za/co/hpsc/web/domain/` directory. The eight entities under `src/main/java/za/co/hpsc/web/domain/` are
Lombok field holders with no hand-written methods of their own; their cascade, orphan-removal and element-collection
behaviour is already exercised by the repository integration tests Gap #24 added.

**Why it matters:** A stated-but-unbuilt goal repeated in two places, yet untracked here, so it will keep being
carried forward from release to release with nobody deciding whether it's wanted. With no behaviour on the entities
beyond what Lombok generates and what the repository tests already cover, unit tests may add nothing but maintenance
load — but that is a decision to record, not an assumption to leave implicit.

**Proposed improvement:** Decide whether entity-level unit tests are still wanted. If so, add them for whatever
entity behaviour warrants it (e.g. collection defaults such as `IpscMatch.stages`/`Competitor.emailAddresses` being
initialised to an empty list). If not, drop the bullet from `HISTORY.md`'s Short-term list and the item from the next
release's Known Issues/Future Enhancements, as Gap #23 did for the `homeClub` backfill.

**Outcome:** Decided per entity rather than all or nothing, following `AGENTS.md`'s rule against testing
Lombok-generated behaviour. Only `IpscMatch` has behaviour of its own worth a unit test: its `stages` list is
excluded from Lombok's `toString`/`equals`/`hashCode` because `IpscMatchStage.match` points straight back, and
dropping either exclusion makes both methods recurse into a `StackOverflowError`. A new `IpscMatchTest` covers that
(`toString`/`hashCode` on a match with a linked stage, and `equals` ignoring `stages`) plus the `stages` list being
initialised empty and mutable; removing the exclusions was confirmed to fail three of its four tests. The other
seven entities have no hand-written behaviour and are left to the repository integration tests from Gap #24. The
bullet was dropped from `HISTORY.md`'s Short-term list, `ARCHITECTURE.md`'s test tree gained a `domain/` entry
and `README.md`'s unit-test categories now include entities.

#### 27. Database-profile docs promise a setup the properties files don't provide — ✅ Closed in v8.10.0

**Evidence:** `AGENTS.md:72`'s Build & Run Commands say the no-profile run "uses application.properties; requires
MYSQL_USER and MYSQL_PASSWORD env vars", and `CONTRIBUTING.md:77`'s Database Profiles table gives the `(none / prod)`
profile as "MySQL — env vars `MYSQL_USER` / `MYSQL_PASSWORD`". But `application.properties` sets no
`spring.datasource.url` at all, so a run with no profile can't connect unless a URL is supplied some other way (e.g.
`SPRING_DATASOURCE_URL`), which no doc mentions. `CHANGELOG.md`'s 8.4.0 entry already recorded this ("has no
`spring.datasource.url` outside a profile"), but only fixed `README.md`'s steps by switching them to `dev`. Separately,
`README.md:98` and `CONTRIBUTING.md:48` say credentials come from `MYSQL_USER`/`MYSQL_PASSWORD` "regardless of
profile", yet `application-local.properties` hard-codes `spring.datasource.username=hpsc_dev` and reads
`${MYSQL_LOCAL_PASSWORD}` instead. `CONTRIBUTING.md:67`'s note acknowledges the `local` profile but not its different
credentials, and the Database Profiles table has no `local` row.

**Why it matters:** The no-profile row is the one that describes production, and following it as written produces a
startup failure rather than a running app. The "regardless of profile" claim is harmless for `dev`, but a contributor
who does need `local` will set the wrong variables.

**Proposed improvement:** State in `AGENTS.md`'s run command and `CONTRIBUTING.md`'s `(none / prod)` row that the
datasource URL must be supplied externally (e.g. `SPRING_DATASOURCE_URL`), or add a `${MYSQL_URL}`-style placeholder to
`application.properties` and document that variable instead. Qualify the "regardless of profile" wording in
`README.md`/`CONTRIBUTING.md` (except `test` and `local`), and either add a `local` row to the Database Profiles table
or mention its `hpsc_dev` user and `MYSQL_LOCAL_PASSWORD` variable in the existing note.

**Outcome:** Fixed in the docs, plus a new `prod` profile. Adding a required `${MYSQL_URL}` placeholder to
`application.properties` would change what an existing no-profile deployment must supply — a breaking configuration
change under `AGENTS.md`'s Semantic Versioning rules — so the no-profile run keeps taking its URL from outside
(e.g. `SPRING_DATASOURCE_URL`), and `AGENTS.md`'s run command now says so. Alongside it, a new
`application-prod.properties` gives production its own profile (`localhost:3306/hpsc_prod`, still reading
`MYSQL_USER`/`MYSQL_PASSWORD`), with a matching run command in `AGENTS.md` and `ARCHITECTURE.md`'s Database (prod) row
now naming it. `CONTRIBUTING.md`'s Database Profiles table lists every profile's connection settings, splitting
`(none / prod)` into `(none)` and `prod` rows and adding a `local` row (user `hpsc_dev`, `MYSQL_LOCAL_PASSWORD`), which
its `local` note repeats. `README.md`'s and `CONTRIBUTING.md`'s credentials wording now excludes `local` as well as
`test`.

#### 28. `logback-spring.xml` configures a `staging` profile that exists nowhere else — ✅ Closed in v8.10.0

**Evidence:** `src/main/resources/logback-spring.xml` (lines 82–105) has a `<springProfile name="staging">` block
writing to `logs/application-staging.log`, present since the file's early history. No
`application-staging.properties` exists, and no doc mentions a `staging` profile: `CONTRIBUTING.md`'s Database
Profiles table (as corrected by Gap #27) lists only `(none)`, `prod`, `dev`, `local` and `test`, and `README.md`,
`ARCHITECTURE.md` and `AGENTS.md` name no `staging` either. The other five logback blocks (`default`, `dev`,
`local`, `prod`, `test`) each match a documented profile.

**Why it matters:** The same category of drift Gap #27 just closed for the properties files, one file over. A
contributor reading `logback-spring.xml` would reasonably expect a staging environment exists, but activating
`staging` gives no datasource URL — the same startup failure Gap #27 documented for the no-profile run — so the
block is either dead configuration or an environment nobody has documented.

**Proposed improvement:** Decide whether a staging environment is wanted. If not, remove the `staging` block from
`logback-spring.xml`. If so, add an `application-staging.properties` alongside `application-prod.properties` and a
`staging` row to `CONTRIBUTING.md`'s Database Profiles table, and mention it wherever `prod` is documented.

**Outcome:** Resolved by removal: no staging environment is wanted, so the `staging` `<springProfile>` block (and
its `logs/application-staging.log` appender) is gone from `logback-spring.xml`. Its remaining blocks — `default`,
`dev`, `local`, `prod` and `test` — now each match a profile documented in `CONTRIBUTING.md`'s Database Profiles
table (`default` being the no-profile run), so no properties file or doc change was needed.

#### 29. Dependabot security-update PRs bypass the GitFlow rule that only `develop` and `hotfix/*` reach `main` — ✅ Closed in v8.10.1

**Evidence:** `AGENTS.md:475` says `main` "is only ever updated by promoting `develop` after a `release/vX.Y.Z`
branch has merged into it, or directly from a `hotfix/*` branch — never any other source", and `AGENTS.md:486`
repeats that every branch goes to `develop` first, with `hotfix/*` "the sole, deliberate exception". But the
repository has Dependabot security updates enabled (`security_and_analysis.dependabot_security_updates` is
`enabled`), and those PRs open against the default branch — `main`. The new `.github/dependabot.yml` sets
`target-branch: "develop"`, but that only governs version updates; GitHub ignores it for security updates, as the
file's own header comment notes. No doc says how a Dependabot security PR should be handled.

**Why it matters:** The first security advisory against a dependency will produce a PR straight into `main` from a
`dependabot/*` branch — exactly the source `AGENTS.md` forbids — and nothing tells a reviewer (or an AI agent
following `AGENTS.md`) whether to merge it there, retarget it, or treat it as a hotfix. Merged into `main` alone,
the fix is also lost from `develop` at the next release, the failure mode `CONTRIBUTING.md`'s hotfix merge rule
exists to prevent.

**Proposed improvement:** Decide how Dependabot security PRs fit the branching model, then document it in
`AGENTS.md`'s Branching Model and `CONTRIBUTING.md`'s Merging section. Either treat them as hotfixes — merge into
`main`, then carry the same change into `develop`, as for `hotfix/*` — and name `dependabot/*` as a second
exception; or retarget each one to `develop` before merging; or make `develop` the repository's default branch so
security PRs target it directly (which also changes where Dependabot reads its configuration from).

**Outcome:** Dependabot security-update PRs are now handled as hotfixes. `AGENTS.md`'s Branching Model names
them, alongside `hotfix/*`, as the only sources besides `develop` that may update `main`, and a new `dependabot/*`
bullet separates version-update PRs (target `develop`, treated like `feature/*`) from security-update PRs (target
`main`, merged there so the fix ships at once, then carried into `develop`). Because Dependabot deletes its branch
once the PR merges, `CONTRIBUTING.md`'s new Merging rule carries the fix across by merging `main` back into
`develop`, rather than by a second PR from the Dependabot branch as for `hotfix/*`. `CONTRIBUTING.md`'s Branching
Model summary and `.github/dependabot.yml`'s header comment point at the same rule. Neither of the other options was
taken: retargeting each PR to `develop` would delay security fixes to the next release, and making `develop` the
default branch would also move where Dependabot reads its configuration from.

### 🟡 Partially Completed

A gap moves here when it has at least one **Progress** paragraph (per
`update-improvement-plan-gaps`'/`sync-improvement-plan-gaps`' "— 🟡 Partially completed in vX.Y.Z" header suffix)
but hasn't yet reached a final **Outcome** — it moves on to ✅ Completed once it does.

#### 26. The `tomcat.version` override is an untracked standing manual constraint — 🟡 Partially completed in v8.10.0

**Evidence:** `pom.xml` (lines 46–48) pins `tomcat.version` to `11.0.25` with the comment "Override
spring-boot-starter-parent 4.1.1's pinned 11.0.24, which carries three critical CVEs (GHSA-h3x4-894j-xpx5,
GHSA-9xv2-5v5q-p794, GHSA-gcx9-497g-6cp6), all fixed in 11.0.25" — added in v8.3.1 (commit `28af4d1`). Boot 4.1.1 is
still the latest 4.1.x release on Maven Central, and its `spring-boot-dependencies` POM still manages `tomcat.version`
at `11.0.24`, so the override is still needed. Yet this plan's "🛤️ Roadmap" Ongoing row says "#5's overrides are
gone as of v8.1.1", and the "⚙️ Goals & Constraints" `pom.xml` row mentions no override at all.

**Why it matters:** This is exactly the shape of Gap #5 — a manually tracked, easy-to-forget pin that nothing flags
once the upstream BOM catches up — but the plan currently reads as though no such override remains, so the Ongoing
check has nothing concrete pointing it at this one.

**Proposed improvement:** No code change needed now. Record the override in the Ongoing roadmap row and the
Goals & Constraints table, and at each release check whether the parent's managed `tomcat.version` has reached
`11.0.25` or later; drop the override in the same pass the parent is bumped, as Gap #5 did for `jackson-databind`.

**Progress:** The tracking half is done. The plan now records the override in its Ongoing roadmap row and
Goals & Constraints table, and the root cause behind it being missed is fixed: the Ongoing row said overrides were
re-checked "per the Release Checklist", but no checklist step actually did so. `AGENTS.md`'s Release Checklist
step 2 and the `prep-version-release` skill's matching step now re-check every manual `pom.xml` override against
the version the parent's own `spring-boot-dependencies` POM manages, and drop any the parent has caught up with.
The override itself has to stay for now: Spring Boot 4.1.1 is still the latest GA release (4.2.0-M2 is only a
milestone) and still manages Tomcat `11.0.24`. The gap closes once a Spring Boot GA release manages `11.0.25` or
later and the override is dropped.

### ⚪ Open

#### 6. Match scoring / shooter-log service and controller layer are not yet built

**Evidence:** `ARCHITECTURE.md`'s Feature Support table states, "JPA entities and repositories exist for
match/competitor scoring and shooter logs, but the service/controller layer that operates on them is still being
built"; its `repositories/` package comment marks `MatchCompetitor`/`MatchStageCompetitor`/`ShooterLog*` as "not yet
wired"; its Model Layer note calls `MatchOverallScoresRequest`/`MatchStageScoresRequest` "groundwork only — not yet
consumed by any controller". `README.md` and `CONTRIBUTING.md` independently restate the same gap, and
`documentation/history/RELEASE_NOTES_v8.1.0.md`'s Known Issues/Future Enhancements carry it forward from v8.0.0,
explicitly noting that the request DTOs' `@JsonCreator`/required-field fix (closed alongside Gap #1) leaves them
"ready" for wiring. (As of v8.9.0 that `repositories/` comment no longer says "not yet wired": since v8.8.0 the
scoring/shooter-log repositories are injected for the delete-time `existsBy…` checks, but still no service or
controller operates on them, so the gap itself stands.)

**Why it matters:** This is the same shape of gap that closed Gap #1 — JPA/repository layer exists, service/
controller layer doesn't — but for the scoring/shooter-log domain specifically, and it is now the most-repeated
"known gap" across the project's own documentation, yet was not separately tracked here.

**Proposed improvement:** Apply the same phased pattern that closed Gap #1: introduce `MatchScoreService`/
`ShooterLogService` (interface + `impl/` split) over the existing repositories, add controller endpoints backed by
`@SpringBootTest` integration tests, and only then consider cross-entity orchestration (e.g. importing a full
Practiscore results export) once a concrete need reappears. The request DTOs' required-field enforcement is already
fixed (see Gap #1's Outcome), so this gap is scoped to the service/controller layer alone.

---

## 🛤️ Roadmap

| Phase       | Focus                                                                                                                                                                                                                                              |
|-------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Now**     | Begin the match scoring / shooter-log service and controller layer (#6), following the same phased pattern that closed #1                                                                                                                          |
| **Next**    | No items currently scoped — #29 closed in v8.10.1                                                                                                                                                                                                  |
| **Later**   | No items currently scoped — #23 (not applicable) and #24 closed in v8.9.0                                                                                                                                                                          |
| **Ongoing** | #5's overrides are gone as of v8.1.1, but `tomcat.version` has been pinned since v8.3.1 (#26); re-check each release whether the parent's managed version has caught up, and drop any override that has become redundant per the Release Checklist |

---

## ☑️ Success Criteria

- ✅ Met in v8.0.0: `IpscCompetitorController`/`IpscMatchController` expose real, tested endpoints backed by the
  existing entity/repository layer, closing the gap named identically in `README.md`, `ARCHITECTURE.md` and
  `CLAUDE.md`.
- ✅ Met in v8.3.1: `.github/workflows/build.yml` runs `./mvnw verify -Pcoverage` automatically on push/PR to
  `develop`/`main`; `ARCHITECTURE.md`'s CI/CD & Quality Gates table has dropped the "locally / by reviewers" caveat
  on the `Build & Tests` row.
- ✅ Met in v8.4.0: a 97%-minimum JaCoCo `check` rule (raised from 51% to 86% in v8.3.1, then to 97% here, once the
  86% floor was confirmed holding cleanly in CI) fails CI on a real regression, and the floor now sits genuinely
  near the ~98.16%/98.94% actual baseline rather than merely below it.
- ✅ Met in v8.2.0 (as not applicable): the `Static Analysis` row is gone from `ARCHITECTURE.md`'s CI/CD & Quality
  Gates table entirely — Qodana was removed rather than made to run automatically, closing Gap #7 the other way.
- ✅ Met in v8.3.1: `AwardService`/`ImageService` CSV processing is confirmed intentionally stateless, and
  `README.md`/`ARCHITECTURE.md` now say so explicitly, closing Gap #3's ambiguity between deliberate design and
  oversight.
- A real `MatchScoreController`/`ShooterLogController` (or equivalent) exists and is tested, closing the gap
  `README.md`, `ARCHITECTURE.md` and `CONTRIBUTING.md` currently described as "still being built".
- ✅ Met in v8.3.0: `IpscMatchController.createMatches`/`IpscMatchService.createMatches` mirror the competitor bulk
  CSV import pattern, closing Gap #8 and removing the last asymmetry between the two CRUD domains' bulk-import
  support.
- ✅ Met in v8.4.0: `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` is now applied by `IpscMatchServiceImpl.resolveClub`
  when a match's `club` is omitted, closing Gap #9's inert-groundwork-constant gap.
- ✅ Met in v8.5.1: `HISTORY.md`'s "📖 Evolution Overview"/"🎯 Major Milestones" sections now carry a Phase and
  Milestone entry (26/27/28) for every shipped release through v8.5.0, closing Gap #10's backlog.
- ✅ Met in v8.6.2: `HISTORY.md`'s Short-term/Medium-term Future Roadmap lists name only genuinely outstanding work
  under current entity names and version labels, closing Gap #11's drift.
- ✅ Met in v8.8.0: `IpscCompetitorController`/`IpscMatchController` expose tested `DELETE` endpoints that
  refuse records still referenced by scoring or shooter-log rows, making the "CRUD" claims in
  `README.md`/`ARCHITECTURE.md` accurate and closing Gap #12.
- ✅ Met in v8.9.0: `ARCHITECTURE.md`'s CI/CD & Quality Gates table (and `CONTRIBUTING.md`'s summary of
  it) lists every workflow in `.github/workflows/`, including the Claude Code review and assistant workflows,
  closing Gap #13.
- ✅ Met in v8.9.0: `flyway-migration-versioning.md`'s Current State table lists every migration through
  `V7_8_0`, and a new step in "🔢 Choosing the Next Version" keeps it from drifting again, closing Gap #14.
- ✅ Met in v8.9.0: `ARCHITECTURE.md`'s Quality Attributes table and its Persistence Layer section agree on
  cascade/`mappedBy` — both now describe `IpscMatch.stages` as the one cascaded, bidirectional relationship — and its
  database-profiles cross-reference points at `CONTRIBUTING.md`, closing Gap #15.
- ✅ Met in v8.9.0: `CONTRIBUTING.md`'s Running Tests example names a real, existing test method, closing
  Gap #16.
- ✅ Met in v8.9.0: `HISTORY.md`'s Future Roadmap Implications "Recently Completed" log has an entry for
  every shipped release, and the Release Checklist now updates it unconditionally, closing Gap #17.
- ✅ Met in v8.9.0: the tech stack in `README.md`/`ARCHITECTURE.md`/`AGENTS.md` matches the dependencies the code
  actually uses, with no unused dependency left pinned in `pom.xml`, closing Gap #18.
- ✅ Met in v8.9.0: `ARCHITECTURE.md` describes the `converters/` package only as the enum `AttributeConverter`s it
  holds, closing Gap #19.
- ✅ Met in v8.9.0: `AGENTS.md`'s 3-tier test rule names every service, or records `TransactionService` as a deliberate
  exception, closing Gap #20.
- ✅ Met in v8.9.0: `ARCHITECTURE.md`'s data-flow diagrams and repository descriptions show writes committed through
  `TransactionService` and the repositories' real wiring, closing Gap #21.
- ✅ Met in v8.9.0: `AGENTS.md` expands "HPSC" the same way as every other source, closing Gap #22.
- ✅ Met in v8.9.0: existing competitors' `homeClub` is backfilled, or the backfill is explicitly dropped from
  `HISTORY.md`'s roadmap, closing Gap #23.
- ✅ Met in v8.9.0: the domain model's cascade, fetch-join queries and `existsBy…` checks have direct repository/entity
  tests, or `README.md` no longer claims them, closing Gap #24.
- ✅ Met in v8.10.0: entity-level unit tests exist for the domain model's own behaviour (`IpscMatchTest`), and the
  goal is dropped from `HISTORY.md`'s roadmap, closing Gap #25.
- ✅ Met in v8.10.0: `AGENTS.md`, `README.md` and `CONTRIBUTING.md` describe every database profile's connection
  settings as the properties files actually configure them, so the documented no-profile run starts, closing
  Gap #27.
- ✅ Met in v8.10.0: every `<springProfile>` in `logback-spring.xml` matches a documented profile — `staging`
  either removed or backed by its own properties file and `CONTRIBUTING.md` row, closing Gap #28.
- ✅ Met in v8.10.1: `AGENTS.md` and `CONTRIBUTING.md` say how Dependabot security-update PRs are merged, and
  following that keeps every fix on both `main` and `develop`, closing Gap #29.
- `pom.xml` carries no `tomcat.version` override because the Spring Boot parent manages `11.0.25` or later itself,
  closing Gap #26.
- This document's Gaps section shrinks over time as items close — closed items should move into `HISTORY.md`'s
  Future Roadmap Implications section (or its Historical Timeline entries) rather than being deleted silently from
  here.

---

## 🔗 Related Documentation

See `README.md`'s [📚 Documentation](/README.md#-documentation) section for the full documentation map. Most relevant to
this plan:

- [`ARCHITECTURE.md`](/ARCHITECTURE.md) — the CI/CD & Quality Gates table and layered-architecture rules this plan
  builds on
- [`AGENTS.md`](/AGENTS.md) — the Git Workflow, Release Checklist and Roadmap Planning conventions referenced
  throughout
- [`HISTORY.md`](/HISTORY.md) — the "🛤️ Future Roadmap Implications" section this plan complements
- [`CONTRIBUTING.md`](/CONTRIBUTING.md) — contributor-facing setup and pull request checklist
- [`improvement-plan-tasks.md`](improvement-plan-tasks.md) — the checkbox-level task breakdown derived from this
  plan's gaps

