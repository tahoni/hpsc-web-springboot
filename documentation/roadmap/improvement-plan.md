# Improvement Plan

This document synthesises the goals and constraints stated across this repository's documentation and configuration into
a single set of prioritised improvement opportunities. Unlike [`README.md`](/README.md) and 
[`ARCHITECTURE.md`](/ARCHITECTURE.md), it is not evergreen — it reflects a point-in-time reading of the project and
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

| Source                                              | Goal / constraint                                                                                                                                                                                                                                                                                                    |
|-----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `README.md`, `ARCHITECTURE.md`                      | Rebuild the match/competitor domain's service and controller layer on top of the existing JPA entities and repositories — ✅ delivered in v8.0.0 as `IpscCompetitorService`/`IpscMatchService` and their controllers                                                                                                 |
| `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md`   | Build the match/competitor **scoring** and shooter-log service/controller layer over the existing JPA entities, repositories and already-fixed request DTOs — explicitly called out as still being built, not aspirational                                                                                           |
| `ARCHITECTURE.md` (Layered Architecture)            | Strict unidirectional layering: Controller → Service → Repository → Database; no layer may skip the one below it, and controllers must carry no business logic                                                                                                                                                       |
| `ARCHITECTURE.md` (Exception handling), `CLAUDE.md` | All exceptions extend `FatalException`, `NonFatalException` or `ValidationException`, handled centrally by `ControllerAdvice` — never caught and rethrown as generic `RuntimeException`                                                                                                                              |
| `ARCHITECTURE.md` (CI/CD & Quality Gates)           | Security analysis (CodeQL) and Build & Tests (`build.yml`, `./mvnw verify -Pcoverage`) are automatic gates on push/PR to `main`/`develop`; the latter also enforces an 86% JaCoCo line-coverage minimum (see Gap #2/#4); Qodana static analysis was removed in v8.2.0 after never once succeeding in CI (see Gap #7) |
| `AGENTS.md` (Git Workflow, Release Checklist)       | GitFlow branching (`develop` → `release/vX.Y.Z` → `main`, `hotfix/*` direct to `main`), Semantic Versioning and a fixed, ordered release checklist covering `pom.xml`, `HpscWebApplication.java`, `CHANGELOG.md`, `HISTORY.md`, `RELEASE_NOTES.md` and archived per-version docs                                     |
| `AGENTS.md` (Documentation Conventions)             | British English spelling throughout prose and Javadoc; every heading carries a reused or deliberately new emoji; `README.md`/`ARCHITECTURE.md` stay version-agnostic (reverse-synced from release docs, not the other way round)                                                                                     |
| `AGENTS.md` (Test Conventions), `CLAUDE.md`         | Mockito-only controller tests (no Spring context), H2-backed service/repository integration tests, `<ClassName>Test` / `test<Scenario>_when<Condition>_then<Expectation>` naming, AssertJ unavailable (excluded in `pom.xml`)                                                                                        |
| `pom.xml`                                           | Track current Spring Boot / Java releases closely (Java 25, Spring Boot 4.1.1) — this currency itself creates a maintenance constraint (see [Gaps](#-gaps--improvement-opportunities))                                                                                                                               |
| `application.properties` (prod/dev/test)            | Flyway is the schema source of truth for MySQL (prod/dev); the `test` profile bypasses it entirely via Hibernate `create-drop` against H2 — the two schema paths can silently diverge                                                                                                                                |
| `CONTRIBUTING.md`, `application.properties`         | Three distinct runtime profiles (none/prod, `dev`, `test`) with different database engines and DDL strategies must all stay usable without extra setup burden for new contributors                                                                                                                                   |

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

### 2. No automatic build/test gate on pull requests — ✅ Closed in v8.3.1

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

### 3. Award/Image CSV pipelines never persist — ✅ Closed in v8.3.1

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

### 4. Coverage is measured but not enforced — ✅ Closed in v8.4.0

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
improvement. The chosen 51% floor is deliberately a low-regression backstop, not "near the current baseline"
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
(98.16%/98.94% line/branch, still 836 tests as of this pass, confirmed unchanged by a fresh local
`./mvnw verify -Pcoverage` run) rather than pinned exactly to it, leaving a small margin so ordinary line-count
fluctuation doesn't trip the gate while still being genuinely "near" the baseline this gap's Proposed improvement
asked for. Verified locally that `./mvnw verify -Pcoverage` passes cleanly at the new threshold before landing it.
The `BRANCH` counter is still not separately enforced — only `LINE`, as established when this gate was first added
in v8.3.1 — which remains a deliberate, documented deviation from the original "line/branch minimum" wording rather
than an oversight.

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

### 7. Qodana static analysis is configured but never runs in CI — ✅ Closed as not applicable in v8.2.0

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

### 8. Match bulk CSV import remains removed pending a rebuild — ✅ Closed in v8.3.0

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
semicolon-separated `<stageNumber>-<stageName>` cell rather than a nested list (CSV has no native nested-row
representation). `MatchResponseHolder` mirrors `CompetitorResponseHolder`. `IpscMatchController.createMatches`
(`POST /ipsc/matches/bulk`, consumes `text/csv`) and `IpscMatchService`/`IpscMatchServiceImpl.createMatches` mirror
`IpscCompetitorController`/`IpscCompetitorServiceImpl`'s `createCompetitors` shape exactly: a `readMatches` CSV-parsing
helper, a `toRequest` row-to-`MatchRequest` mapper, and (new relative to the competitor flow, since matches have no
CSV-native nested-stage representation) a `parseStages` helper splitting the delimited `Stages` cell into
`MatchStageRequest`s. `ARCHITECTURE.md`'s stale "match bulk-import remains removed pending a rebuild" language and its
competitor-only endpoint/service/data-flow documentation are updated in the same release to reflect this.

### 9. `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` is declared but never applied — ✅ Closed in v8.4.0

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

---

## 🚀 Roadmap

| Phase       | Focus                                                                                                                                                                                                                                                                                                                 |
|-------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Now**     | #2 delivered in v8.3.1: `.github/workflows/build.yml` runs `./mvnw verify -Pcoverage` on push/PR to `develop`/`main`, also enforcing #4's JaCoCo line-coverage floor — raised from 51% to 86% to 97% across v8.3.1/v8.4.0, now closed. #7 is closed as not applicable: Qodana was removed in v8.2.0 rather than fixed |
| **Next**    | Begin the match scoring / shooter-log service and controller layer (#6), following the same phased pattern that closed #1                                                                                                                                                                                             |
| **Later**   | No items currently scoped — #9, this phase's previous occupant, closed in v8.4.0                                                                                                                                                                                                                                        |
| **Ongoing** | #5's overrides are gone as of v8.1.1; keep re-checking for new manual dependency-version overrides becoming redundant at each release per the Release Checklist                                                                                                                                                       |

---

## ✅ Success Criteria

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
