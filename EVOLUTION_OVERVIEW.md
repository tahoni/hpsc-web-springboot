# HPSC Website Backend Evolution Overview

The full Phase-by-phase narrative of the HPSC Website Backend project's evolution, one entry per release —
split out from [`HISTORY.md`](/HISTORY.md) to keep that file a manageable size, since this section alone had grown
to roughly half of it. See `HISTORY.md`'s [📅 Historical Timeline](/HISTORY.md#-historical-timeline) for the
same releases summarised more concisely, and its [🎯 Major Milestones](/HISTORY.md#-major-milestones) for each
release's headline achievement.

---

## 📖 Evolution Overview

The HPSC Website Backend project has evolved through distinct phases, each addressing specific architectural and feature
requirements:

Phases 1 to 18 (versions 1.0.0 to 7.4.1) are archived, unchanged, in
[`documentation/history/EVOLUTION_OVERVIEW_v1-v7.md`](/documentation/history/EVOLUTION_OVERVIEW_v1-v7.md).

---

### Phase 19: IPSC Module Completion — Competitor & Match CRUD (v8.0.0)

**Duration:** August 31, 2026

Completes the IPSC module rebuild that v6.0.0 through v7.4.0 laid groundwork for: `IpscController`'s empty stub is
replaced by two full CRUD controllers backed by new services and DTOs, alongside a comprehensive Javadoc/`@since`
documentation pass and a migration of the project's AI-agent tooling from slash commands to Skills.

**Key Accomplishments:**

**IPSC Competitor & Match CRUD**

- `IpscCompetitorController`/`IpscMatchController` — full CRUD (`create`/`update`/`patch`/`get`, plus `getAllMatches`)
  on `/ipsc/competitors`/`/ipsc/matches`, following the project's action-named REST method convention
- `IpscCompetitorService`/`IpscMatchService` + impls resolve club/gender/firearm-type/match-category by name (404/400
  via the existing exception hierarchy), map requests to/from the existing `Competitor`/`IpscMatch`/`IpscMatchStage`
  entities and persist via the existing repositories; `patchMatch` upserts stages by stage number rather than
  replacing the whole list
- New `CompetitorRequest`/`CompetitorResponse` and `MatchResponse`/`MatchStageResponse` DTOs

**Gender Enum & Persistence**

- `Gender` gains `name`/`abbreviation` fields, a case-insensitive `fromName()` factory and a `toString()` override,
  bringing it in line with the project's other enums
- New `GenderConverter` (`AttributeConverter<Gender, String>`), wired onto `Competitor.gender` via `@Convert`

**Rename & Consistency Sweep**

- `AwardService`/`ImageService.processCsv` renamed to `createAwards`/`createImages`; their bulk CSV endpoints moved to
  `/awards/bulk`/`/images/bulk` and now return `201 Created`
- `getByName`/`getByAbbreviation`/`getByCode`/`getByAbbreviationOrName` factory methods renamed to `fromX` across
  `ClubIdentifier`, `CompetitorCategory`, `Division`, `FirearmType`, `MatchCategory` and `PowerFactor`

**Documentation & Tooling**

- Comprehensive Javadoc/`@since` pass across models, converters, exceptions, utils, constants and `ControllerAdvice`
- `AGENTS.md`/`CLAUDE.md` merged into a single tool-agnostic reference; new line-wrapping, extended Arrange-Act-Assert
  and test-helper-placement conventions
- The project's AI-agent tooling migrated from `.claude/commands/*.md` slash commands to `.claude/skills/*/SKILL.md`
  Skills; `generate-pr-description` now runs `sync-unreleased-changes` as a prerequisite step
- Qodana JVM static analysis re-added (`qodana.yaml`)

**Architecture Highlights:**

- `IpscController`'s empty stub retired — the IPSC module now has real, resource-oriented competitor and match CRUD
  endpoints, completing work begun as groundwork back in v6.0.0
- `models/ipsc/request` split into `models/ipsc/match/request`/`models/ipsc/scores/request`, matching the module's
  per-concern shape; competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains
  groundwork, not yet consumed by any controller

**Technical Focus:**

- IPSC domain-layer completion (competitor/match CRUD)
- Consistency (naming, Javadoc coverage, AI-agent tooling)
- Static analysis integration (Qodana)

**Test Coverage:**

- New unit and integration test coverage for `IpscCompetitorController`/`Service`/`ServiceImpl` and
  `IpscMatchController`/`Service`/`ServiceImpl`, plus `GenderTest`/`GenderConverterTest` — the largest single-release
  test expansion since v5.4.0

---

### Phase 20: Competitor Bulk CSV Import & Required-Field Enforcement Fixes (v8.1.0)

**Duration:** September 1, 2026

Extends the IPSC competitor module completed in v8.0.0 with bulk CSV import, then — while building and testing it —
uncovers and fixes a subtle Jackson gotcha affecting every `@JsonProperty(required = true)` field added across the
IPSC request models to date: without a matching `@JsonCreator` constructor, the annotation never actually fires.

**Key Accomplishments:**

**Competitor Bulk CSV Import**

- `IpscCompetitorController.createCompetitors` (`POST /ipsc/competitors/bulk`, consumes `text/csv`) parses CSV data
  into `CompetitorRequestForCSV` rows and creates each competitor via the existing `createCompetitor` logic — unlike
  `AwardController`/`ImageController`'s bulk endpoints, which only build response objects without persisting
- New `CompetitorRequestForCSV` (CSV-mapped, `UpperCamelCase` headers) and `CompetitorResponseHolder` models

**Required-Field Enforcement Fix**

- Root cause: `@JsonProperty(required = true)` only fires for creator (constructor) parameters — a class deserialised
  via its default no-args constructor and setters silently accepts a missing "required" field as `null`
- `CompetitorRequestForCSV`, `CompetitorRequest`, `MatchRequest`, `MatchStageRequest`, `MatchOverallScoresRequest`/
  `MatchStageScoresRequest` and their CSV variants each gained a `@JsonCreator` constructor with every parameter
  bound via `@JsonProperty`, replacing their Lombok `@AllArgsConstructor`
- `CompetitorRequest`'s required third field corrected from `competitorNumber` to `clubNumber`, matching
  `IpscCompetitorServiceImpl.validateForCreate`'s actual validation
- The score CSV variants' constructors now match their plain counterparts' signatures exactly, verified via a
  `csvMapper.addMixIn(...)` mixin test — the same pattern `AwardServiceImpl`/`ImageServiceImpl` already use

**Architecture Highlights:**

- Confirms `MatchOverallScoresRequest`/`MatchStageScoresRequest` remain groundwork — their constructors and
  annotations are now correct, but neither is wired into a controller nor service yet

**Technical Focus:**

- Bulk data import (competitor CSV)
- Jackson deserialisation correctness (`@JsonCreator`/`@JsonProperty(required = true)`)
- Request-model test coverage

**Test Coverage:**

- New unit tests across `IpscCompetitorController`/`Service`/`ServiceImpl`'s bulk import, and eight request-model
  test classes (`CompetitorRequestTest`, `CompetitorRequestForCSVTest`, `MatchRequestTest`, `MatchStageRequestTest`,
  `MatchOverallScoresRequestTest`, `MatchStageScoresRequestTest`, `MatchOverallScoresRequestForCSVTest`,
  `MatchStageScoresRequestForCSVTest`)

---

### Phase 21: CI Static Analysis, Release-Process Self-Maintenance & Coverage Regression Fixes (v8.1.1)

**Duration:** September 1, 2026

A process-and-quality patch release: no new domain feature, but real coverage-regression fixes, a self-maintaining
release-checklist/roadmap tooling loop, and a completed CI quality gate that had sat configured but unwired since
v8.0.0.

**Key Accomplishments:**

**CI Static Analysis**

- New `.github/workflows/qodana.yml` runs `JetBrains/qodana-action` against the existing `qodana.yaml` config on
  push/PR to `develop`/`main`, mirroring `codeql.yml`'s trigger branches; results upload as SARIF to GitHub code
  scanning, so no Qodana Cloud token or other secret is required

**Release-Process Self-Maintenance**

- New `update-improvement-plan-gaps`/`sync-improvement-plan-gaps` Claude Code skills formalise the manual
  roadmap-gap-maintenance work done by hand across v8.0.0/v8.1.0 — a full-sweep audit and a diff-driven check,
  respectively; `generate-pr-description` renamed to `prep-version-release` to reflect its actual scope and now
  runs both new skills as its first step
- `AGENTS.md`'s Release Checklist re-synced against `prep-version-release`'s actual, drifted-ahead process: three
  new steps (improvement-plan gap check, `[Unreleased]` completeness verification, conditional `CONTRIBUTING.md`
  update), described tool-agnostically without naming the skills

**Coverage Regression Fixes**

- Recreated `NonFatalExceptionTest`/`FatalExceptionTest`/`ValidationExceptionTest`, covering every constructor
  overload — these existed as of v7.2.0 but were dropped somewhere before now with no replacement, leaving the
  exception hierarchy at 20% line coverage
- New tests for the `models/ipsc/shared` scoring groundwork classes (0% coverage previously) and every
  `patchCompetitor`/`patchMatch` field's previously untested success path
- Full-suite coverage rose from 92.9%/93.4% to 98.34%/98.84% (line/branch), 746 → 775 tests

**Dependency Clean-up**

- Spring Boot parent bumped `4.1.0` → `4.1.1`, dropping the now-redundant `jackson-databind`/`log4j-api`
  `dependencyManagement` overrides
- The recurring dependency-currency check then caught a third redundant override, `jackson-bom.version`, confirmed
  against the parent POM directly rather than an echoed property

**Documentation & Roadmap**

- `roadmap/improvement-plan.md`/`improvement-plan-tasks.md` gain two new gaps (match-scoring
  service/controller layer; Qodana CI wiring, the latter partially progressed by this release's own workflow
  addition) and close Gap #5 (`jackson-databind` override)
- `CONTRIBUTING.md` gains a new "🗺️ Roadmap" section documenting both roadmap files' structure and maintenance
  convention — the only one of `README.md`/`AGENTS.md`/`ARCHITECTURE.md`/`CONTRIBUTING.md` that didn't already list
  them

**Architecture Highlights:**

- No architectural change — this release is process, tooling, dependency and test-coverage work only

**Technical Focus:**

- CI/CD quality gate completion (static analysis)
- Release-process and roadmap-documentation self-maintenance
- Test-coverage regression recovery
- Dependency currency

**Test Coverage:**

- 29 new tests added (746 → 775): the recreated exception hierarchy tests, three new shared-scoring-model test
  classes, and expanded `patchCompetitor`/`patchMatch` success-path coverage

---

### Phase 22: Competitor Multi-Email Support & Bulk CSV Separator Standardisation (v8.2.0)

**Duration:** September 1, 2026

A domain feature release: competitors gain multiple email addresses, and every bulk CSV endpoint's multi-value cell
format is unified onto one separator convention.

**Key Accomplishments:**

**Competitor Multi-Email Support**

- `Competitor.emailAddress` (a single, optional `String`) replaced with `emailAddresses` (`List<String>`), mapped
  via `@ElementCollection`/`@CollectionTable` onto a new `competitor_email` child table
- `V7_2_0__add_competitor_emails.sql` backfills the new table from any existing non-blank `email_address` values,
  then drops that column
- `CompetitorRequest`/`CompetitorResponse` renamed `emailAddress` to `emailAddresses`; `CompetitorRequestForCSV`
  keeps a single CSV cell but now holds zero or more semicolon-separated addresses, split via
  `IpscCompetitorServiceImpl`'s new `splitEmailAddresses` helper

**Bulk CSV Separator Standardisation**

- New shared `SystemConstants.ARRAY_SEPARATOR` (`";"`); `AwardServiceImpl`/`ImageServiceImpl`'s bulk CSV parsing
  switched from `"|"` to it, so competitor email addresses and image/award tags now share one multi-value cell
  convention, with the `AwardController`/`ImageController`/`IpscCompetitorController` Swagger examples updated to
  match

**Static Analysis Removal**

- Qodana static analysis removed entirely: `.github/workflows/qodana.yml`, `qodana.yaml`, and every reference in
  `ARCHITECTURE.md`/`CONTRIBUTING.md`/`AGENTS.md`'s CI/CD documentation. A release audit found it had failed on
  every CI run since v8.1.1 added it — a missing `QODANA_TOKEN` secret and an unconditional SARIF-upload step — with
  no working baseline left to preserve
- `roadmap/improvement-plan.md`'s Gap #7 (Qodana CI wiring) closed as not applicable rather than
  delivered

**Architecture Highlights:**

- No layering change — a domain-model extension (`@ElementCollection` child table) and a cross-cutting constant,
  both within the existing service/controller structure

**Technical Focus:**

- Domain-model evolution (single value → collection, backed by a new child table)
- Cross-endpoint consistency (shared constant replacing two independently hardcoded separators)

**Bug Fix**

- `IpscCompetitorServiceImpl.applyFields`/`patchCompetitor` stored the caller-supplied `emailAddresses` `List`
  reference directly onto the entity; an immutable list (e.g. `List.of(...)`) crashed with an unhandled
  `UnsupportedOperationException` when Hibernate merged an update, bypassing the exception hierarchy entirely. Found
  while adding genuinely-multiple-address test coverage (every existing test had only used a single-element list);
  both methods now defensively copy into a new `ArrayList`

**Test Coverage:**

- Existing CSV parsing, request/response model and bulk-import tests updated for the new `emailAddresses` shape and
  separator
- New multi-address tests (2+ emails in one list/CSV cell) added to `CompetitorRequestTest`,
  `IpscCompetitorServiceImplTest`, `IpscCompetitorServiceTest` and `IpscCompetitorServiceIntegrationTest`; 781 → 790
  tests

---

### Phase 23: Match Bulk CSV Import (v8.3.0)

**Duration:** September 2, 2026

Extends the IPSC match module with bulk CSV import, mirroring the competitor bulk-import convention v8.1.0
established — but designing its multi-stage cell format from a discarded first attempt rather than reusing an
existing pattern outright.

**Key Accomplishments:**

**Match Bulk CSV Import**

- `IpscMatchController.createMatches` (`POST /ipsc/matches/bulk`, consumes `text/csv`) parses CSV data into
  `MatchRequestForCSV` rows and creates each match via the existing `createMatch`
  validation/club/firearm-type/category-resolution logic, matching `IpscCompetitorController.createCompetitors`'s
  bulk-import shape
- New `MatchRequestForCSV` (CSV-mapped, `UpperCamelCase` headers, `@JsonCreator` constructor) and
  `MatchResponseHolder` models (`models/ipsc/match/`)
- `IpscMatchServiceImpl` gains three protected helpers: `readMatches` (CSV → `MatchRequestForCSV` rows, mirroring
  `IpscCompetitorServiceImpl`'s CSV-parsing pattern), `toRequest` (row → `MatchRequest`), and `parseStages`, which
  splits each `Stages` cell entry on its first `-` into a `MatchStageRequest`

**Stages Cell Design**

- CSV has no native concept of a repeated group, so a match's stages — a nested list on `MatchRequest` — needed a
  single-cell representation; an initial `numberOfStages` count field was implemented, then dropped in favour of a
  single semicolon-separated `stages` cell of `<stageNumber>-<stageName>` entries (e.g. `"1-Stage One;2-Stage Two"`)
  before either design reached `develop`, once it was clear a count alone couldn't carry each stage's name

**Architecture Highlights:**

- No layering change — extends the existing controller/service/model triad `IpscCompetitorController`'s bulk import
  already established, applied to the match domain

**Technical Focus:**

- Bulk data import (match CSV)
- CSV multi-value cell design (delimited-entry parsing vs. a count-only field)
- Request-model test coverage

**Test Coverage:**

- New unit tests across `IpscMatchController`/`Service`/`ServiceImpl`'s bulk import, and `MatchRequestForCSVTest`'s
  `UpperCamelCase` JSON/CSV (de)serialisation and required-field enforcement

---

### Phase 24: CI Build/Test Gate, Coverage Enforcement & CSV Persistence Clarity (v8.3.1)

**Duration:** September 2, 2026

A process-and-quality patch release: no new domain feature, but a completed CI build/test gate, the project's first
coverage-regression-enforcement rule (tightened twice within the same branch), and a resolved documentation
ambiguity around Award/Image CSV persistence — closing out work v8.1.1's coverage-regression fixes and Gap #4's
baseline-setting groundwork left open, plus Gap #3's design-intent question left open since v8.1.0.

**Key Accomplishments:**

**CI Build/Test Gate**

- New `.github/workflows/build.yml` runs `./mvnw verify -Pcoverage` on push/PR to `develop`/`main`, mirroring
  `codeql.yml`'s trigger branches — sets up JDK 25 via `actions/setup-java` (Maven-cached), builds/tests via
  `sh ./mvnw` (`mvnw` isn't tracked with the execute bit in git) and uploads the JaCoCo HTML/XML report as a build
  artefact

**Coverage Enforcement**

- New JaCoCo `check` execution in the `coverage` Maven profile enforces a `BUNDLE`-level `LINE`/`COVEREDRATIO`
  minimum, wired into the new CI gate so a coverage regression fails the build
- Set initially to `0.51` (51%) as a deliberately low regression backstop, then raised to `0.86` (86%) within the
  same branch: a fresh coverage run measured the real baseline at 98.16% line / 98.94% branch coverage (836 tests,
  up from 775 at v8.1.1) — tightening the floor further, closer to that baseline, is left as a follow-up once the
  86% threshold has run cleanly in CI

**CSV Persistence Clarity**

- `AwardService.createAwards()`/`ImageService.createImages()` confirmed intentionally stateless by design, not an
  unfinished persistence layer — `README.md`'s Award Ceremonies/Image Gallery bullets and `ARCHITECTURE.md`'s
  Service Layer table and Award/Image CSV Processing Flow section now say so explicitly

**Documentation**

- `ARCHITECTURE.md`/`CONTRIBUTING.md`'s CI/CD & Quality Gates tables updated to reflect the new gate and rule,
  dropping the stale "locally / by reviewers"/"All PRs" language
- `ARCHITECTURE.md`'s Award/Image CSV Processing Flow diagram and `documentation/roadmap/improvement-plan.md`'s
  Gap #3 Evidence corrected from the stale `processCsv()` method name to `createAwards()`/`createImages()`, renamed
  back in v8.0.0; `AwardControllerTest`/`ImageControllerTest`'s matching `// processCsv()` test-grouping comments
  corrected the same way
- `roadmap/improvement-plan.md`/`improvement-plan-tasks.md`: Gap #2 (no automatic build/test gate)
  closed; Gap #3 (CSV persistence ambiguity) closed; Gap #4 (coverage measured but not enforced) marked partially
  progressed

**Architecture Highlights:**

- No architectural change — this release is CI/CD tooling and documentation work only

**Technical Focus:**

- CI/CD quality gate completion (build/test automation)
- Coverage-regression enforcement (tightened in two steps, still short of the real baseline)
- Documentation-intent clarification (Award/Image CSV persistence)

**Test Coverage:**

- No dedicated new unit/integration test coverage added for this release; verified via the full test suite
  (836 tests), `./mvnw verify -Pcoverage` and a fresh baseline measurement (98.16%/98.94% line/branch)

---

### Phase 25: Club Domain Defaults, Optional Club Numbers & Documentation Convention Hardening (v8.4.0)

**Duration:** September 3, 2026

A domain-and-documentation release: two real behavioural changes to the IPSC club-handling rules — matches now
default to a shared `ALL` club instead of failing validation, and competitor club numbers are required only for HPSC
members — alongside the coverage-regression floor finally reaching its real baseline and a round of convention
hardening (member ordering, REST naming, a release-checklist tree-accuracy backstop) that closes Gap #4 and Gap #9,
both left open or partially progressed since v8.1.1/v8.3.1's coverage groundwork.

**Key Accomplishments:**

**Club Domain Defaults**

- New `ClubIdentifier.ALL` constant (`"Eufees Clubs"` / `"All"` / `"ALL"`) represents a match hosted jointly by
  `SOSC`/`HPSC`/`PMPSC`; new `V7_3_0__seed_club_data.sql` migration seeds the `club` table with every named
  `ClubIdentifier` constant
- `IpscMatchServiceImpl.resolveClub()` now defaults a missing/blank match `club` to
  `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` instead of `validateForCreate` throwing `ValidationException`,
  mirroring the competitor domain's existing "apply the domain default" pattern; throws `NonFatalException` if even
  the default club is missing from the database, or a new `FatalException` if the constant itself is null. Closes
  Gap #9

**Optional Club Numbers**

- `IpscCompetitorServiceImpl.resolveClubNumber()` centralises a new rule: `clubNumber` is required only when the
  competitor's home club is HPSC, and forced to `null` otherwise, on create, update and patch
- `Competitor.clubNumber` column relaxed to nullable via new `V7_4_0__make_club_number_nullable.sql`, clearing
  `club_number` on any existing non-HPSC competitor

**Coverage Enforcement Completion**

- JaCoCo `LINE`/`COVEREDRATIO` floor tightened `0.86` → `0.97`, now genuinely near the real baseline after holding
  cleanly in CI across the `develop` and `main` runs that shipped v8.3.1; a final measurement this release
  (98.44%/98.98% line/branch, 868 tests) confirms it holds with room to spare. Closes Gap #4

**Convention Hardening**

- New `AGENTS.md` Member ordering convention (constructors → public → protected → private, private helpers always
  last); `IpscCompetitorServiceImpl`/`IpscMatchServiceImpl` reordered to match
- REST URL-path/handler-naming rules promoted from `standard-rest-conventions.md`'s recommendations into an actual
  `AGENTS.md` convention
- New Release Checklist step verifies `ARCHITECTURE.md`'s Project Structure tree against disk at every release — a
  backstop after this branch caught `.claude/skills/` and the removed `HpscConstants` class both stale in the tree
- `roadmap/improvement-plan.md`/`improvement-plan-tasks.md` restructured into ✅ Completed/
  🟡 Partially Completed/⚪ Open sections, replacing the previous flat Now/Next/Later/Ongoing phasing

**Security**

- `tomcat-embed-core`/`-el`/`-websocket` overridden `11.0.24` → `11.0.25` via a new `pom.xml` `tomcat.version`
  property, closing three critical CVEs still pinned by `spring-boot-starter-parent:4.1.1`'s dependency management

**Architecture Highlights:**

- No structural architectural change — this release is domain-rule refinement (club defaulting/optional fields), a
  coverage-floor completion and documentation/convention hardening

**Technical Focus:**

- Domain-default patterns extended from the competitor module to the match module
- Coverage-regression enforcement reaching its real, sustainable baseline
- Documentation and convention consistency (member ordering, REST naming, tree-accuracy backstop)

**Test Coverage:**

- New `resolveClub(String, ClubIdentifier)` cases and `FatalException`/`NonFatalException` propagation coverage
  across `IpscMatchServiceImplTest`/`IpscMatchServiceIntegrationTest`/`IpscMatchControllerTest`; suite grew from 836
  to 868 tests, verified via a fresh `./mvnw verify -Pcoverage` run (98.44%/98.98% line/branch)

---

### Phase 26: Documentation Cross-Reference Consolidation & Icon Registry Sync (v8.4.1)

**Duration:** September 4, 2026

A documentation-only patch release: condenses `AGENTS.md`/`CONTRIBUTING.md`'s duplicated content into
cross-references, backfills the icon registry and cleans up latent `CHANGELOG.md`/`AGENTS.md` defects — no
domain-model, API or test-behaviour change.

**Key Accomplishments:**

**Cross-Reference Consolidation**

- `AGENTS.md`/`CONTRIBUTING.md`'s full/near-verbatim content duplicates condensed into highlights-and-link
  references — Git Workflow's Branching Model, Conventions and Directory Tree Maintenance bullets, the Exception
  handling and CHANGELOG-same-change/Evergreen bullets, and the CI/CD & Quality Gates table (now pointed at
  `ARCHITECTURE.md`, its actual source of truth). Git Workflow's "Merging" subsection consolidated as
  `CONTRIBUTING.md`'s sole canonical copy, since `sync-unreleased-changes`/`sync-improvement-plan-gaps` need only
  the Branching Model and Conventions subsections to remain in `AGENTS.md`
- New `AGENTS.md` "🧩 Claude Code Skills" and "🗺️ Roadmap Planning" sections, both mirrored with a short pointer
  in `CONTRIBUTING.md`

**Icon Registry Sync**

- `AGENTS.md`'s icon registry backfilled with 25 previously-unregistered icons already in real use, plus a new
  "Reserved" sub-table tracking the sibling `hpsc-web-vite` repository's frontend-specific icons — synced twice
  this release as `hpsc-web-vite`'s own registry grew, reciprocally gaining `🧬` (Data model / DTOs) in return.
  Several icon collisions resolved across `README.md`, `ARCHITECTURE.md`, `HISTORY.md`, `RELEASE_NOTES.md` and 17
  archived per-version release notes
- `CHANGELOG.md`'s duplicate, truncated `[5.0.0]` section removed, and a pre-existing broken example in the Serial
  Commas convention (identical "e.g." and "not" contrast phrases) corrected

**Build & Metadata**

- Project version bumped to 8.4.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No architectural change — this release only restructures/cross-references existing documentation and fixes
  latent documentation defects

**Technical Focus:**

- Documentation cross-reference consolidation, reducing duplicated source-of-truth content
- Icon registry completeness and cross-repository icon consistency

**Test Coverage:**

- No test changes — this release touches only Markdown documentation and version metadata

---

### Phase 27: Root Document Title Standardisation & Source-of-Truth Clarification (v8.4.2)

**Duration:** September 4, 2026

A documentation-only patch release: clarifies `AGENTS.md` as the project's ultimate source of truth for conventions
and standardises root document titles to a consistent project-name prefix — no domain-model, API or test-behaviour
change.

**Key Accomplishments:**

**Source-of-Truth Clarification**

- `AGENTS.md` now states, right before its Documentation File Map, that it is this project's ultimate source of
  truth for conventions — every other file's workflow/convention guidance points back to it rather than restating it
- `CONTRIBUTING.md`'s intro carries the matching pointer, stating `AGENTS.md` wins if anything else in the
  repository's documentation ever contradicts it

**Root Document Title Standardisation**

- `CHANGELOG.md`'s title changed from "Changelog" to "HPSC Website Backend", with a new "🧾 Change Log"
  second-level heading beneath it, matching `README.md`'s existing project name; every heading below it — Table of
  Contents, each version and their category/area sub-headers — demoted one level to nest correctly under the new
  heading
- `CONTRIBUTING.md`/`HISTORY.md`'s H1 titles gain the same "HPSC Website Backend" prefix for consistency

**Build & Metadata**

- Project version bumped to 8.4.2 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No architectural change — this release standardises document titles and clarifies documentation precedence only

**Technical Focus:**

- Documentation precedence clarity (a single, explicit source of truth)
- Root document title consistency

**Test Coverage:**

- No test changes — this release touches only Markdown documentation and version metadata

---

### Phase 28: Match Start/End Time Tracking (v8.5.0)

**Duration:** September 4, 2026

A domain feature release: adds nullable start/end timestamps to `IpscMatch`, wired end-to-end through the request/
response models, CSV bulk import and service layer, alongside a handful of British English documentation corrections.

**Key Accomplishments:**

**Match Start/End Time Tracking**

- `IpscMatch` gains nullable `startTime`/`endTime` (`LocalDateTime`) columns, alongside the existing `scheduledDate`,
  via new `V7_5_0__add_ipsc_match_start_end_time.sql`; wired end-to-end through `MatchRequest`, `MatchRequestForCSV`
  and `MatchResponse`, and `IpscMatchServiceImpl`'s `applyFields`/`patchMatch`/`toRequest`/`toResponse`
- CSV bulk import (`POST /matches/csv`) now requires `StartTime`/`EndTime` header columns, like every other
  `MatchRequestForCSV` property, consistent with this endpoint's existing all-columns-required header validation —
  existing CSV templates need updating to add them (values may be left blank)

**Documentation Fixes**

- `README.md`'s "License" heading/prose corrected to British English "Licence"; `CONTRIBUTING.md`'s own Serial
  Commas rule example corrected to no longer violate the rule it illustrates; `AGENTS.md`'s British English
  exception for `LICENSE.md` narrowed to just the filename and the file's own content, so every other reference to
  it spells it "Licence" instead of carving out a wider exception

**Build & Metadata**

- Project version bumped to 8.5.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No structural architectural change — extends an existing entity (`IpscMatch`) with two new nullable columns
  following the established request/response/service wiring pattern

**Technical Focus:**

- Match-timing data completeness
- British English documentation consistency

**Test Coverage:**

- New coverage in `IpscMatchServiceIntegrationTest` proves the two-column round-trip through the real
  H2/Hibernate/JPA layer, not just mocked repositories; `IpscMatchServiceTest`'s CSV bulk-import test now supplies
  actual `StartTime`/`EndTime` values, closing the one gap where that mapping was never verified

---

### Phase 29: HISTORY.md Phase/Milestone Backfill & Release Re-Scoping to a Patch Version (v8.5.1)

**Duration:** September 13, 2026

A documentation-only patch release: backfills three releases' worth of missing `HISTORY.md` Phase/Milestone entries,
then discovers its own scope is documentation/tooling-only and re-scopes itself from a planned minor version down to
a patch — no domain-model, API or test-behaviour change.

**Key Accomplishments:**

**HISTORY.md Phase/Milestone Backfill**

- "📖 Evolution Overview"/"🎯 Major Milestones" backfilled with Phase 26/27/28 and Milestone 26/27/28 entries for
  v8.4.1, v8.4.2 and v8.5.0, summarising each release's already-written Historical Timeline content — closes
  `improvement-plan.md`'s Gap #10
- "Major Version Goals" Version 8.x entry extended from `v8.0.0 – v8.1.1` to `v8.0.0 – v8.5.1`, narrating the domain
  broadening and documentation-process discipline delivered across v8.2.0 – v8.5.1

**Improvement Plan Maintenance**

- `improvement-plan.md` gains a new "📋 At a Glance" gap-status index; its "🗺️ Roadmap" table's **Now**/**Next**
  rows refreshed to drop already-closed #2/#7 and promote #6; Gap #10 tracked from ⚪ Open through to ✅ Completed
  in both `improvement-plan.md` and `improvement-plan-tasks.md`

**Release Re-Scoping**

- This release's entire diff against `main` proved documentation/tooling-only, so it was re-scoped from the
  originally-planned `v8.6.0` **MINOR** version down to `v8.5.1` **PATCH**, per `CHANGELOG.md`'s Version Policy —
  matching the precedent set by v8.4.1/v8.4.2 — with the branch and every in-flight documentation reference renamed
  to match before this release-prep pass

**Chronological Consistency**

- "📖 Evolution Overview", "🎯 Major Milestones", "🏛️ Architectural Evolution" and "🗺️ Future Roadmap
  Implications" reordered to ascending (oldest-first), matching "✨ Feature Timeline"/"💡 Project Philosophy
  Evolution"'s existing convention — only "📅 Historical Timeline" keeps its most-recent-first order.
  Architectural Evolution's `v5.3.0`/`v5.4.0` entries, previously stranded in a broken mixed order after `v8.0.0`,
  are now correctly interleaved between `v5.2.0` and `v6.0.0`
- Stale "Document Created"/"Last Updated"/"Coverage" metadata and the "Recent Updates"/"Previous Update"
  bold-labelled update log removed from the end of the Conclusion section — badly out of date (stopped at
  v8.0.0/v5.1.0) and superseded by this file's own proper narrative sections

**Tooling & Minor Fixes**

- `prep-version-release`/`generate-pr-summary` skills now end their drafted PR description/summary with the
  standard Claude Code attribution footer
- `CONTRIBUTING.md` spells out "and" instead of "&"; a Flyway baseline comment in `application-local.properties`
  tightened

**Build & Metadata**

- Project version bumped to 8.5.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No architectural change — this release backfills historical documentation, reorders and prunes stale content
  within `HISTORY.md`, and corrects its own version scope only

**Technical Focus:**

- Documentation completeness (closing a three-release Phase/Milestone backlog)
- Internal chronological consistency across `HISTORY.md`'s own sections
- Correct Semantic Versioning classification of documentation-only releases

**Test Coverage:**

- No test changes — this release touches only Markdown documentation, two Claude Code skill files and version
  metadata

---

### Phase 30: Match URL Field, Start/End Time Precision Fix & Test Architecture Formalisation (v8.6.0)

**Duration:** September 23, 2026

A domain correction release: adds a nullable `url` field to `IpscMatch`, corrects `startTime`/`endTime` from
`LocalDateTime` to `LocalTime`, and formally documents the already-established 3-tier service test architecture.

**Key Accomplishments:**

**Match URL Field**

- `IpscMatch` gains a new nullable `url` column via `V7_6_0__add_ipsc_match_url.sql` — a URL with more information
  about a match (e.g. a results page or event listing); wired end-to-end through `MatchRequest`,
  `MatchRequestForCSV`, `MatchResponse` and `IpscMatchServiceImpl`'s `applyFields`/`patchMatch`/`toRequest`/
  `toResponse`, with a matching `Url` column added to the CSV bulk import header

**Start/End Time Precision Fix**

- `IpscMatch.startTime`/`endTime` corrected from `LocalDateTime` to `LocalTime` via
  `V7_7_0__change_ipsc_match_start_end_time_to_time.sql` — these were always time-of-day-only values alongside
  `scheduledDate`, so the redundant date component v8.5.0 introduced is dropped. New
  `IpscConstants.IPSC_INPUT_TIME_FORMAT` (`HH:mm`) constant replaces `IPSC_INPUT_DATE_TIME_FORMAT` on both fields;
  JSON/CSV values are now bare `HH:mm` instead of `yyyy-MM-dd HH:mm` — existing CSV templates and API clients need
  updating to drop the date component

**Test Architecture Formalisation**

- `AGENTS.md`'s Test Conventions section now formally documents the 3-tier service test architecture
  (`<Service>Test`/`<Service>ImplTest`/`<Service>IntegrationTest`) already followed by all four services, replacing
  a one-line pointer that previously only described the pattern piecemeal across the `scaffold-unit-tests`/
  `scaffold-integration-tests` skills

**Build & Metadata**

- Project version bumped to 8.6.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No structural architectural change — extends `IpscMatch` with one new nullable column and corrects the Java type
  of two existing ones, following the established request/response/service wiring pattern

**Technical Focus:**

- Match-metadata completeness (`url`)
- Data-type correctness (time-of-day values no longer carry a redundant date component)
- Test convention documentation completeness

**Test Coverage:**

- `IpscMatchServiceIntegrationTest`, `IpscMatchServiceTest`, `IpscMatchServiceImplTest`, `MatchRequestTest` and
  `MatchRequestForCSVTest` updated so `url` round-trips through JSON, CSV import and the real H2/Hibernate/JPA
  layer, and `startTime`/`endTime` fixtures/CSV rows/JSON payloads reflect the new `LocalTime`/`HH:mm` shape

---

### Phase 31: documentation/history/ Reorganisation & Evolution Overview Split (v8.6.1)

**Duration:** September 23, 2026

A documentation-only patch release: splits `HISTORY.md`'s largest section out into this file, regroups the
per-version archive into major-version subdirectories and updates every tool/doc that reads or writes those
paths — no domain-model, API or test-behaviour change.

**Key Accomplishments:**

**Evolution Overview Split**

- `HISTORY.md`'s "📖 Evolution Overview" section (the Phase-by-phase narrative you are reading now) split out into
  this file, `documentation/history/EVOLUTION_OVERVIEW.md` — it had grown to roughly half of `HISTORY.md`'s
  4,095 lines. `HISTORY.md` keeps a short pointer section under the same heading/anchor, so its Table of Contents
  entry still resolves; every other section stays in `HISTORY.md` unchanged

**documentation/history/ Reorganisation**

- All 52 archived `RELEASE_NOTES_vX.Y.Z.md`/`PR_DESCRIPTION_vX.Y.Z.md` files regrouped from a flat
  `history` directory into `v1/` – `v8/` subdirectories by major version, moved with `git mv` to
  preserve history; this file is unaffected, staying directly in `documentation/history/`
- `AGENTS.md`'s Documentation File Map and Release Checklist (two independent copies of the archive-path
  references), `README.md`'s Documentation table, and the `prep-version-release`/`generate-pr-summary`/
  `update-improvement-plan-gaps` skills all updated to read/write `documentation/history/v<major>/...` paths,
  deriving `<major>` from a version's leading number before the first `.`

**Release Scoping**

- This release's entire diff against `main` proved documentation/tooling-only, so it was scoped as `v8.6.1`
  **PATCH** rather than a new minor version, matching the precedent set by v8.4.1/v8.4.2/v8.5.1

**Build & Metadata**

- Project version bumped to 8.6.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No architectural change — this release reorganises documentation structure only

**Technical Focus:**

- Documentation file-size and directory-scale management
- Keeping tooling (skills) and doc-structure descriptions (`AGENTS.md`/`README.md`) in sync with the new layout

**Test Coverage:**

- No test changes — this release touches only Markdown documentation, three Claude Code skill files and version
  metadata

---

### Phase 32: CHANGELOG.md Heading-Depth Correction, Future Roadmap Refresh & Icon Registry Sync (v8.6.2)

**Duration:** September 24, 2026

A documentation-only patch release: corrects how the project's own conventions describe `CHANGELOG.md`'s heading
structure, which had drifted one level shallower than the file itself, refreshes `HISTORY.md`'s stale
forward-looking roadmap lists and syncs heading icons with the shared project template — no domain-model, API or
test-behaviour change.

**Key Accomplishments:**

**CHANGELOG.md Convention Correction**

- `AGENTS.md`, `CONTRIBUTING.md` and the `generate-commit-message`, `prep-version-release`, `scaffold-unit-tests`,
  `scaffold-integration-tests` and `sync-unreleased-changes` skills described `CHANGELOG.md` as
  `## 🧪 [Unreleased]` → `### <category>` → `#### <Area>`, while the file has actually used `###`/`####`/`#####`;
  every reference was corrected to match
- `AGENTS.md`'s Git Workflow Conventions now spell out the full `#### <category>` → `##### <Area>` nesting, reuse of
  existing Area names and the bold-lead-in bullet style; `generate-commit-message` also notes that security-relevant
  fixes belong under `#### 🔐 Security`
- Reverse-synced from the shared project template, which had already corrected the same drift in its own copy of
  these conventions — keeping this project's Conventional Commits prefixes and bold-lead-in bullet style rather than
  adopting the template's generic defaults

**Future Roadmap Refresh**

- `HISTORY.md`'s "🛤️ Future Roadmap Implications" Short-term/Medium-term lists refreshed against what has actually
  shipped: the club-seeding bullet reduced to its still-outstanding `Competitor.homeClub` backfill half (the `club`
  table was already seeded in v8.4.0 via `V7_3_0__seed_club_data.sql`), `ShooterLogEntry` renamed to
  `ShooterLogCompetitor`, "Medium-term (v7.x+)" relabelled "Medium-term (Later v8.x Releases)" and "Bulk match
  processing capabilities" dropped as delivered by v8.3.0's bulk CSV import
- Recorded as Gap #11 in `roadmap/improvement-plan.md` by this release's own improvement-plan audit,
  then closed within the same release; items overlapping the still-open Gap #6 were left in place

**Roadmap Icon & Structure Sync**

- `🛤️` now marks Roadmap (replacing `🗺️`) and `☑️` marks Checklist / success criteria in `AGENTS.md`'s icon
  registry, both moved out of the icons reserved for `hpsc-web-vite`; every live Roadmap, Future Roadmap
  Implications and Success Criteria heading switched to match, as did the two improvement-plan skills' stale `🚀`/`✅`
  references
- `improvement-plan.md`/`improvement-plan-tasks.md` synced with the shared project template's structure: project
  title, a note on the four kinds of gap an audit looks for, a fuller Related Documentation list, a note on
  annotating checked task items and no more hard-coded gap count in the tasks file's intro
- The rest of `AGENTS.md`'s icon registry then followed: restructured to mirror the template's core table, with
  its backend / API service extension set adopted as this project's own and its component-based frontend set kept
  reserved, and every live heading realigned to match — Documentation Conventions (`✍️`), Documentation File Map
  (`🗺️`), Key Design Patterns (`🧭`), Data Flow (`🔃`), Development Guidelines (`🛠️`), Getting Started (`🚀`), At a
  Glance (`🌳`) and Related Documentation (`🔗`)

**Formatting**

- Table column padding realigned in `AGENTS.md`'s skills table and `README.md`'s Documentation table

**Release Scoping**

- This release's entire diff against `main` proved documentation/tooling-only, so it was scoped as `v8.6.2`
  **PATCH**, matching the precedent set by v8.4.1/v8.4.2/v8.5.1/v8.6.1

**Build & Metadata**

- Project version bumped to 8.6.2 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No architectural change — this release corrects documented conventions only

**Technical Focus:**

- Keeping written conventions (`AGENTS.md`, skills) consistent with the files they describe
- Reverse-syncing generic improvements from the shared project template while preserving project-specific choices

**Test Coverage:**

- No test changes — this release touches only Markdown documentation, seven Claude Code skill files and version
  metadata

---

### Phase 33: Competitor Listing Endpoint, Stage Delimiter Change & Dependency Clean-up (v8.7.0)

**Duration:** September 24, 2026

A minor feature release: adds a collection endpoint listing every competitor, changes the match CSV's stage
delimiter from `-` to `:`, switches the domain's `@ManyToOne` associations to eager fetching, moves the app to
Spring Boot's default port and brings springdoc onto its Spring Boot 4 line.

**Key Accomplishments:**

**Competitor Listing Endpoint**

- New `IpscCompetitorController.getAllCompetitors` (`GET /ipsc/competitors`), backed by
  `IpscCompetitorService.getAllCompetitors`, returns every competitor as a JSON array of `CompetitorResponse`s,
  or an empty array when there are none — the collection counterpart to `GET /{competitorId}`, mirroring
  `IpscMatchController.getAllMatches`
- Its Swagger `200` response is documented as an array via `@ArraySchema`; `getAllMatches`' response, which
  described a single `MatchResponse`, was corrected the same way

**Stage Delimiter Change**

- `IpscMatchServiceImpl.parseStages` now splits each `Stages` entry on its first `:` instead of `-` (e.g.
  `"1:Stage One;2:Stage Two"`), so stage names may still contain a `:`; entries in the old `1-Stage One` form are
  rejected with a `ValidationException` — existing CSV templates and API clients need updating
- `IpscMatchController`'s bulk CSV Swagger example, `MatchRequestForCSV`'s Javadoc and the roadmap documents
  updated to the `<stageNumber>:<stageName>` format

**Eager Association Fetching**

- Every `@ManyToOne` on `Competitor`, `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`, `MatchStageCompetitor`,
  `ShooterLog` and `ShooterLogCompetitor` switched from `FetchType.LAZY` to `FetchType.EAGER`, so referenced
  entities load together with their owner rather than on first access

**Configuration**

- `server.port=8081` removed from `application.properties`, so the app now runs on Spring Boot's default port
  `8080`; `README.md`, `AGENTS.md`, `ARCHITECTURE.md` and `CONTRIBUTING.md` updated to the new app, Swagger UI and
  OpenAPI URLs

**Dependency Clean-up**

- `springdoc-openapi-starter-webmvc-ui` bumped from `2.8.5` to `3.1.0`, with its version now managed by an
  imported `springdoc-openapi-bom` in a new `<dependencyManagement>` section, since Spring Boot's parent doesn't
  manage springdoc
- The unused `spring-restdocs-mockmvc` test dependency removed, together with the Spring REST Docs mentions in
  `README.md`'s and `ARCHITECTURE.md`'s tech-stack lists and `HELP.md`'s reference links

**Roadmap**

- New Gap #12 recorded in `roadmap/improvement-plan.md`: competitors and matches are documented as
  "full CRUD", yet neither domain has a delete operation — now the roadmap's **Next** item
- `ARCHITECTURE.md`'s Project Structure tree re-verified against disk per the Release Checklist, correcting stale
  `documentation/history/`, `documentation/roadmap/` and test-tier comments and adding the missing `banner.txt`

**Build & Metadata**

- Project version bumped to 8.7.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No structural architectural change — the new endpoint follows the existing Controller → Service → Repository
  pattern, and the fetch-type switch changes only when referenced entities are loaded

**Technical Focus:**

- API completeness for the competitor domain's read operations
- Accurate OpenAPI documentation of collection responses
- Keeping documentation-tooling dependencies on lines built for the current Spring Boot major version

**Test Coverage:**

- New `getAllCompetitors` tests across `IpscCompetitorControllerTest`, `IpscCompetitorServiceTest` and
  `IpscCompetitorServiceIntegrationTest`; `IpscMatchServiceImplTest`, `IpscMatchServiceTest`,
  `MatchRequestForCSVTest` and `IpscMatchControllerTest` fixtures moved to the `:` stage delimiter

---

### Phase 34: Competitor & Match Delete Endpoints (v8.8.0)

**Duration:** September 24, 2026

A minor feature release: adds delete endpoints for competitors and matches, closing the last missing verb in both
domains' CRUD set, with a deletion rule that refuses records still referenced by scoring or shooter-log rows.

**Key Accomplishments:**

**Delete Endpoints**

- New `IpscCompetitorController.deleteCompetitor` (`DELETE /ipsc/competitors/{competitorId}`) and
  `IpscMatchController.deleteMatch` (`DELETE /ipsc/matches/{matchId}`), each returning `204 No Content`, `400` when
  the record is still referenced and `404` when it doesn't exist
- Backed by new `IpscCompetitorService.deleteCompetitor`/`IpscMatchService.deleteMatch`, both `@Transactional`

**Reject-Not-Cascade Deletion Rule**

- A competitor with `MatchCompetitor` (match result) or `ShooterLog` rows, or a match with `MatchCompetitor`,
  `MatchStageCompetitor` or `ShooterLogCompetitor` rows, is refused with a `ValidationException` naming the reason
- What a record owns is deleted with it: a competitor's `competitor_email` rows via its `@ElementCollection`, and a
  match's `IpscMatchStage` rows, removed as managed entities before the match itself so the flush deletes them first
- Each delete is flushed inside the service method and a `DataIntegrityViolationException` rethrown as a
  `ValidationException`, closing the race window between the checks and the delete
- New `existsByCompetitorId`/`existsByMatchId`/`existsByMatchStageMatchId` queries on `MatchCompetitorRepository`,
  `MatchStageCompetitorRepository`, `ShooterLogRepository` and `ShooterLogCompetitorRepository` back the checks

**Documentation**

- `ARCHITECTURE.md` gained a Service Layer note on the deletion rule
- `standard-rest-conventions.md`'s "🔍 Current State in This Codebase" now names `IpscCompetitorController`
  alongside `IpscMatchController`, covering every verb including `getAll` and `delete`

**Roadmap**

- Gap #12 (competitor/match "full CRUD" claims without a delete operation) closed and moved to ✅ Completed
- New Gap #13 recorded: `claude.yml` and `claude-code-review.yml` are live but missing from `ARCHITECTURE.md`'s
  CI/CD & Quality Gates table — now the roadmap's **Next** item

**Build & Metadata**

- Project version bumped to 8.8.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- No structural architectural change and no schema migration — the new endpoints follow the existing
  Controller → Service → Repository pattern, with the dependent-row checks kept in the service layer

**Technical Focus:**

- API completeness for the competitor and match domains
- Protecting scoring and shooter-log history from accidental deletion

**Test Coverage:**

- 25 new tests across the controller, service-contract and integration test tiers —
  `IpscCompetitorControllerTest`, `IpscMatchControllerTest`, `IpscCompetitorServiceTest`, `IpscMatchServiceTest`,
  `IpscCompetitorServiceIntegrationTest` and `IpscMatchServiceIntegrationTest` — covering the delete, not-found and
  reject paths (including a reference added before the flush), with H2-backed integration tests

---

### Phase 35: Competitor Paid-Up Flags, Explicit Transaction Boundary & Lazy Loading (v8.9.0)

**Duration:** September 26, 2026

A minor release: competitors gain SAPSA and club paid-up flags, and the persistence layer is reworked so that lazy
associations are loaded through fetch-join queries and every competitor/match write is committed by a dedicated
`TransactionService` — alongside a release-prep audit that closed every open documentation-accuracy gap.

**Competitor Paid-Up Flags**

- New nullable `Competitor.paidUpSapsa`/`paidUpClub` `Boolean` columns via `V7_8_0__add_competitor_paid_up_flags.sql`
- `CompetitorRequest`, `CompetitorRequestForCSV` and `CompetitorResponse` gain matching fields; an omitted flag is
  stored as `null` on create/update and left unchanged on patch
- The competitor CSV bulk import now requires trailing `PaidUpSapsa`/`PaidUpClub` header columns — a client-facing
  migration for existing CSV files

**Lazy Loading & Fetch-Join Queries**

- Every `@ManyToOne` association switched from `FetchType.EAGER` back to `FetchType.LAZY`, reversing v8.7.0
- New `left join fetch` queries on `IpscMatchRepository` and `CompetitorRepository` load a match's club and a
  competitor's home club and email addresses, since `spring.jpa.open-in-view` is disabled

**Explicit Transaction Boundary**

- New `TransactionService`/`TransactionServiceImpl` commits competitor and match saves and deletes in explicit
  `TransactionTemplate` transactions, returning entities with their response associations loaded
- `IpscCompetitorServiceImpl`/`IpscMatchServiceImpl` drop `@Transactional`; bulk CSV imports build every row first,
  then save all rows in one transaction
- `IpscMatch` gains a cascaded, orphan-removing `stages` collection; stage replace/upsert logic moves into
  `TransactionServiceImpl`, and match responses list stages by stage number

**Dependencies**

- Unused `jackson-dataformat-xml` and `commons-lang3` dependencies removed

**Documentation & Tooling**

- `ARCHITECTURE.md`'s data flows, design patterns, repository descriptions and Quality Attributes now describe the
  transaction boundary and cascade; `AGENTS.md`'s club name and 3-tier test rule corrected
- `AGENTS.md`'s Release Checklist and the `prep-version-release` skill make the Future Roadmap Implications log and
  "Major Version Goals" mandatory for every release, and both were backfilled through v8.8.0

**Roadmap**

- Gaps #13–#17 closed, and new Gaps #18–#24 recorded and closed by this release's own audit (#23 as not applicable)
  — only Gap #6, the scoring/shooter-log layer, remains open

**Build & Metadata**

- Project version bumped to 8.9.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- A single, explicit write path: services validate and build, `TransactionService` commits
- `IpscMatch.stages` is the only cascaded relationship; everything else stays reject-not-cascade

**Technical Focus:**

- Making transaction boundaries and association loading explicit and testable
- Clearing documentation drift across the core project documents

**Test Coverage:**

- 63 new tests (903 → 966): `TransactionService`'s full 3-tier split, six repository integration test classes and
  service integration tests run without a surrounding transaction; coverage 98.77%/99.09% line/branch


### Phase 36: Strict Semantic Versioning & Production Profile (v8.10.0)

**Duration:** September 26, 2026

A minor release: Semantic Versioning becomes a strict rule that the release process checks rather than a matter of
precedent, production gains its own `prod` profile, and the database-profile and logging configuration docs are
brought in line with what actually exists.

**Key Accomplishments:**

**Semantic Versioning**

- New Semantic Versioning subsection in `AGENTS.md`'s Git Workflow, defining MAJOR/MINOR/PATCH for this project's
  public API (REST contracts, import formats, configuration properties and environment variables)
- Releases are classified from `CHANGELOG.md`'s `[Unreleased]` section, highest-ranking change first, with
  backward-incompatible entries flagged `**Breaking:**` as they land
- `prep-version-release` validates the requested version before bumping and re-checks it after syncing
  `[Unreleased]`; `generate-commit-message` and `sync-unreleased-changes` apply and audit the `**Breaking:**` prefix

**Configuration**

- New `application-prod.properties` (`localhost:3306/hpsc_prod`, reading `MYSQL_USER`/`MYSQL_PASSWORD`)
- `logback-spring.xml`'s `staging` profile, backed by no properties file or documentation, removed

**Documentation & Tooling**

- The database-profile docs in `AGENTS.md`, `CONTRIBUTING.md`, `README.md` and `ARCHITECTURE.md` now match the
  properties files: the no-profile run's externally supplied datasource URL, the new `prod` profile and the `local`
  profile's own user and password variable
- The Release Checklist re-checks every manual `pom.xml` dependency-version override against the Spring Boot parent's
  own `spring-boot-dependencies` POM
- `CHANGELOG.md`, `RELEASE_NOTES.md` and the archived v8.7.0/v8.9.0 release documents list Removed after Fixed

**Roadmap**

- Gaps #25–#28 recorded by three improvement-plan sweeps: #25 (entity-level unit tests), #27 (database-profile docs)
  and #28 (`staging` logging profile) closed; #26 (`tomcat.version` override) progressed and waiting on a Spring Boot
  release — Gap #6 remains open

**Build & Metadata**

- Project version bumped to 8.10.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Architecture Highlights:**

- A version number now follows from the change set itself, checked at release time

**Technical Focus:**

- Making release versioning, runtime profiles and dependency overrides explicit and checkable

**Test Coverage:**

- 4 new tests (966 → 970): `IpscMatchTest` guards `IpscMatch.stages`' exclusion from Lombok's
  `toString`/`equals`/`hashCode`; coverage unchanged at 98.77%/99.09% line/branch


### Phase 37: Explicit Dependency Submission & Dependabot Configuration (v8.10.1)

**Duration:** September 26, 2026

A patch release, CI and documentation only: the one GitHub check with no workflow file behind it is replaced by an
explicit workflow, Dependabot version updates are configured, and Dependabot's security PRs are fitted into the
GitFlow branching model.

**Key Accomplishments:**

**CI/CD**

- New `.github/workflows/dependency-submission.yml` submits the Maven dependency graph with
  `advanced-security/maven-dependency-submission-action`, on push to `main`/`develop` or manual dispatch, using the
  project's own JDK 25 and Maven wrapper (made executable first, since `mvnw` is committed without its executable
  bit); it replaces GitHub's built-in automatic dependency submission, which ran on JDK 21 without the wrapper
- New `.github/dependabot.yml`: weekly Maven and GitHub Actions version updates targeting `develop`, with Maven
  minor/patch bumps and all Actions bumps each grouped into one PR

**Branching Model**

- Dependabot security-update PRs always target the default branch, `main`, whatever `target-branch` says; they are
  now handled as hotfixes — merged into `main`, then carried into `develop` by merging `main` back into it, since
  Dependabot deletes its branch after merging
- `AGENTS.md`'s Branching Model gains a `dependabot/*` entry, and `CONTRIBUTING.md`'s Merging section a matching rule

**Documentation**

- `ARCHITECTURE.md`'s CI/CD & Quality Gates table gains a Dependency Submission row, and its Project Structure tree
  lists `.github/dependabot.yml`; `CONTRIBUTING.md`'s CI summary names the new workflow

**Roadmap**

- Gap #29 (Dependabot security PRs bypassing the GitFlow rule for `main`) recorded and closed — Gap #6 remains open,
  and #26 still waits on a Spring Boot release

**Build & Metadata**

- Project version bumped to 8.10.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Keeping every CI check and dependency-update rule in version control, and inside the branching model

**Test Coverage:**

- No test changes (970 tests); coverage unchanged at 98.77%/99.09% line/branch


### Phase 38: Claude Code Review for Dependabot PRs & First Dependabot Updates (v8.10.2)

**Duration:** September 26, 2026

A patch release: the automated Claude code review, which skipped every bot-authored PR, now reviews Dependabot's too
— shipped as its own release because v8.10.1 had already reached `main` — and Dependabot's first updates are
absorbed, with one that broke a hand-kept Flyway version sync fixed at the root.

**Key Accomplishments:**

**CI/CD**

- `.github/workflows/claude-code-review.yml` gains `allowed_bots: 'dependabot'` — narrower than `'*'`, which would
  admit every bot — so Dependabot's version-update PRs into `develop` and security-update PRs into `main` are reviewed
- Dependabot-triggered workflow runs can only read Dependabot secrets, so `CLAUDE_CODE_OAUTH_TOKEN` must be stored as
  a Dependabot secret as well as an Actions secret; `ARCHITECTURE.md`'s CI/CD & Quality Gates section says so
- Dependabot's first GitHub Actions update: `actions/checkout` `v7`, `actions/setup-java` `v6` and
  `actions/upload-artifact` `v7` across every workflow
- `dependency-submission.yml`'s `chmod +x mvnw` step dropped, since `mvnw` is now committed as executable

**Dependencies**

- Dependabot's first Maven minor/patch update: `springdoc-openapi-bom` `3.1.1`, `jacoco-maven-plugin` `0.8.15` and
  Maven `3.9.16` through a regenerated wrapper; `mvnw.cmd`'s line endings renormalised afterwards
- Dependabot bumped the Flyway plugin's `flyway-mysql` dependency to `13.7.0` while the plugin stayed on Spring Boot's
  `12.4.0`, mixing two Flyway majors; it now uses `${flyway.version}`, a property plugin dependencies do inherit from
  the parent, replacing the hand-kept sync that `pom.xml`'s own comment had warned about

**Build & Metadata**

- Project version bumped to 8.10.2 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Applying the same review to every PR, bot-authored or not, and never rewriting a shipped release
- Replacing a hand-synced version with one derived from the parent, so automated updates can't break it

**Test Coverage:**

- No test changes (970 tests); coverage unchanged at 98.77%/99.09% line/branch


### Phase 39: Docker Deployment, Actuator Health Checks & Flyway at Startup (v8.11.0)

**Duration:** September 27, 2026

A minor release: the application gains a Docker image, a Compose setup with its own MySQL database and an Actuator
health endpoint — and bringing up that empty database revealed that Flyway had never run at startup under Spring
Boot 4, which is fixed here.

**Key Accomplishments:**

**Deployment**

- New multi-stage `Dockerfile`: the Maven wrapper builds the JAR on `eclipse-temurin:25-jdk`, and a non-root `hpsc`
  user runs it on `eclipse-temurin:25-jre` from Spring Boot's extracted layers, so dependency layers stay cached
  between builds; `.dockerignore` keeps the build context to `.mvn/`, `mvnw`, `pom.xml` and `src/`
- The image defaults to the `prod` profile, reads `SPRING_DATASOURCE_URL`, `MYSQL_USER` and `MYSQL_PASSWORD` at run
  time and takes JVM options from `JAVA_OPTS`; its `HEALTHCHECK` polls the health endpoint, with a 90-second start
  period for Flyway's first migration
- New `docker-compose.yml` runs the image against a `mysql:8.4` container, starting the application only once MySQL
  reports healthy; the database and log files persist in named volumes
- Credentials come from a gitignored `.env`, copied from the new `.env.example`, and Compose refuses to start without
  them; optional `APP_PORT`/`MYSQL_PORT` move the host ports, with `MYSQL_PORT=3307` recommended beside a local MySQL

**Health Checks**

- New `spring-boot-starter-actuator` dependency exposing `/hpsc-web/actuator/health` with Actuator's defaults — the
  health endpoint only, including a database check

**Schema Migrations**

- Spring Boot 4 moved Flyway's auto-configuration into its own `spring-boot-flyway` module, so with only `flyway-core`
  on the classpath Flyway never ran at startup and `spring.flyway.*` was ignored in every profile; databases had only
  been migrated by hand through the Maven plugin. `spring-boot-starter-flyway` replaces `flyway-core`
- `application-prod.properties` baselines a non-empty schema without Flyway's history at `7.0.0`, as
  `application-local.properties` already did; an empty database is still built in full from `V7_0_0`

**Documentation**

- `README.md` gains a Running with Docker section; the tech stacks in `README.md`, `ARCHITECTURE.md` and
  `AGENTS.md` list Actuator, and `ARCHITECTURE.md`'s gains a Containerisation row; `CONTRIBUTING.md`'s Database
  Profiles table and `AGENTS.md`'s Flyway note name the `prod` baseline

**Roadmap**

- Gap #30 (Flyway documented as managing the schema but never running at startup) recorded and closed — Gap #6
  remains open, and #26 still waits on a Spring Boot release

**Build & Metadata**

- Project version bumped to 8.11.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- A reproducible, containerised way to run the application, and schema migrations that run where the docs said they
  did

**Test Coverage:**

- No new tests (970 tests); `IpscMatchTest`'s stage tests now separate Act from Assert; coverage unchanged at
  98.77%/99.09% line/branch

### Phase 40: Docker Image Build in CI & Branch Coverage Gate (v8.11.1)

**Duration:** September 27, 2026

A patch release: v8.11.0 shipped a `Dockerfile` that no CI workflow built, and recorded that as a Known Issue; this
release adds the missing gate, so a change that breaks the image fails a pull request instead of a deployment. It
also extends the coverage gate from line coverage alone to branch coverage too.

**Key Accomplishments:**

**CI/CD**

- New `.github/workflows/docker.yml` builds the `Dockerfile` with `docker/setup-buildx-action` and
  `docker/build-push-action` on every push and PR to `main`/`develop`, with `push: false` — the image is checked, never
  published
- Layers are cached through the GitHub Actions cache (`type=gha`, `mode=max`), so unchanged dependency layers aren't
  rebuilt on each run; tests stay in `build.yml`, since the `Dockerfile` skips them
- The workflow runs with read-only `contents` permission, as the image is never pushed to a registry
- `pom.xml`'s JaCoCo `check` execution gains a 97% `BRANCH` `COVEREDRATIO` limit beside the 97% `LINE` one, so a
  branch-coverage regression fails `build.yml` too — the "line/branch minimum" Gap #4 originally proposed, and the
  end of a Known Issue carried since v8.4.0

**Documentation**

- `ARCHITECTURE.md`'s CI/CD & Quality Gates table gains a Docker Image row, and `CONTRIBUTING.md`'s summary of that
  table names the Docker image build; its Code Coverage row now names line and branch coverage

**Roadmap**

- Gap #31 (CI never built the Docker image) recorded and closed — Gap #6 remains open, and #26 still waits on a Spring
  Boot release, as Spring Boot 4.1.1 still manages Tomcat `11.0.24`

**Build & Metadata**

- Project version bumped to 8.11.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Giving the container image the same automatic CI gate as the Maven build, and enforcing branch coverage as well
  as line coverage

**Test Coverage:**

- No test changes (970 tests); coverage unchanged at 98.77%/99.09% line/branch, both now above an enforced 97%
  floor

### Phase 41: Competitor CSV Import Casing Normalisation (v8.12.0)

**Duration:** September 29, 2026

A minor release: the competitor CSV import stored names exactly as typed in the source spreadsheet, so `VAN DER MERWE`
and `van der merwe` produced different-looking records. The import now normalises casing on the way in, without
touching the CSV format or the JSON endpoints. The release also adds Qodana static analysis to CI and widens the
CodeQL and dependency-submission triggers to every GitFlow branch.

**Key Accomplishments:**

**Services**

- New `StringUtils.toProperCase`, backed by the new `org.apache.commons:commons-text` dependency's `WordUtils`,
  upper-cases the first letter of each word and lower-cases the rest, treating spaces, hyphens and apostrophes as
  word breaks
- New `za.co.hpsc.web.helpers` package and its first class, `CompetitorHelpers.toSentenceCaseLastName`, lower-cases
  surname particles (`van`, `der`, `du`, `de`, `le` and the like) when they precede the surname proper, also after a
  hyphen, and capitalises the letter after a Gaelic `Mc` prefix; it matches whole words only, so `Dube`, `Vanderbilt`
  and the Zulu `Mchunu` are left alone
- `IpscCompetitorServiceImpl.toRequest` proper-cases `FirstName`, `LastName`, `MiddleNames`, `Nickname` and `Gender`,
  then passes the `LastName` through the particle helper; `HomeClub`, `ClubNumber`, `CompetitorNumber`, `IdNumber`,
  `CellphoneNumber` and `EmailAddresses` are kept as supplied
- `DateUtil`, `NumberUtil`, `StringUtil` and `ValueUtil` renamed to their plural `Utils` names (internal only)

**CI/CD**

- New `.github/workflows/code_quality.yml` runs `JetBrains/qodana-action` on every push to `main`, `release/*`,
  `feature/*`, `bugfix/*` and `hotfix/*`, plus PRs into `main`/`develop`
- `codeql.yml` and `dependency-submission.yml` push triggers now also cover `release/*`, `feature/*`, `bugfix/*` and
  `hotfix/*`, so a failure surfaces on the branch that introduced it
- Dependabot's `github-actions` group update (PR #152) bumps `docker/setup-buildx-action` to `v4` and
  `docker/build-push-action` to `v7` in `docker.yml`

**Documentation**

- `AGENTS.md` and `CONTRIBUTING.md` add `bugfix/<short-description>` as its own standard GitFlow branch type
- `ARCHITECTURE.md` lists the new `helpers` package and a Helpers table

**Roadmap**

- Gap #32 (the plan still describing Qodana as removed, after `code_quality.yml` brought it back to CI) recorded and
  closed — Gap #6 remains the only open gap, and #26 still waits on a Spring Boot release that manages Tomcat
  `11.0.25`

**Build & Metadata**

- Project version bumped to 8.12.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Making bulk-imported competitor data consistent to read, and running static analysis on every GitFlow branch

**Test Coverage:**

- New `IpscCompetitorServiceImplTest` `toRequest` tests for all-upper-case, all-lower-case and particle-bearing CSV
  rows, alongside tests for the two new helpers

### Phase 42: Match Stage Removal & CSV Import via Jackson Mix-Ins (v9.0.0)

**Duration:** October 1, 2026

A major release: the match domain drops its stage model, and the two bulk CSV imports stop going through dedicated CSV
models. The match request and response lose `stages`, two entities and their tables are removed, `matchFirearmType`
and `matchCategory` become required and the `MYSQL_USER` variable is no longer read, so the release is MAJOR under
the Semantic Versioning rules.

**Key Accomplishments:**

**Persistence**

- `IpscMatchStage`, `MatchStageCompetitor`, `IpscMatchStageRepository`, `MatchStageCompetitorRepository` and the
  `IpscMatch.stages` collection removed; the new `V7_9_0__drop_ipsc_match_stage.sql` migration drops the
  `match_stage_competitor` and `ipsc_match_stage` tables with `DROP TABLE IF EXISTS`, discarding any existing stage
  data

**Match API**

- `MatchStageRequest`, `MatchStageResponse` and the `stages` field on `MatchRequest`/`MatchResponse` removed, along
  with the `Stages` CSV column; `TransactionService.saveMatch`'s stage overload and `StageSaveMode` go too, and
  `IpscMatchService.deleteMatch` no longer checks for stage results
- `MatchRequest.matchFirearmType` and `matchCategory` are required properties, so a JSON body that leaves either out
  is rejected when it is read
- New `MatchPatchRequest` for `PATCH /ipsc/matches/{matchId}`, with no required fields

**Competitor API**

- New `CompetitorPatchRequest` for `PATCH /ipsc/competitors/{competitorId}`, so a patch no longer repeats `firstName`
  and `lastName`; its `emailAddresses` is `null` unless supplied, so omitting it keeps the existing addresses
- `IpscCompetitorServiceImpl.resolveHomeClub` also resolves a home club by abbreviation
- `IpscCompetitorServiceImpl.normaliseCsvRequest` (formerly `toRequest`) keeps the row's `competitorId`
- `CompetitorRequest.emailAddresses` defaults to an empty list, and the CSV import splits the `EmailAddresses` cell on
  the shared array element separator itself

**CSV Import**

- New `MatchRequestCsvMixIn` and `CompetitorRequestCsvMixIn` bind the CSV column headers onto the request models'
  constructors, so each row is read straight into a `MatchRequest`/`CompetitorRequest`; `MatchRequestForCSV` and
  `CompetitorRequestForCSV` are removed
- A header may omit optional columns, and unknown columns are ignored; the `MatchId` and `CompetitorId` columns are
  read but never used, since the imports only create records
- The imports' Swagger request schemas are now plain text with an example header row

**Configuration & Dependencies**

- `spring.datasource.username` is set by the `dev` and `prod` profiles (`hpsc_dev`, `hpsc_prod`) and no longer read
  from `MYSQL_USER`; the `dev` datasource URL points at `127.0.0.1` and the `local` profile drops its
  `MYSQL_LOCAL_PASSWORD` override
- `mysql-connector-j` pinned to `9.4.0`

**Documentation**

- `README.md` and `ARCHITECTURE.md` drop stages from the match description, the entity and repository tables and the
  Project Structure tree
- Past `CHANGELOG.md`, `HISTORY.md` and `EVOLUTION_OVERVIEW.md` entries keep the class names they were written with

**Roadmap**

- No new gaps and none closed; Gap #6's evidence notes that the stage repositories it listed are gone, and Gap #6
  remains the only open gap, with #26 still waiting on a Spring Boot release that manages Tomcat `11.0.25`

**Build & Metadata**

- Project version bumped to 9.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Security**

- `tomcat.version` raised to `11.0.26`, `logback.version` pinned to `1.6.5`, the Jackson BOM properties raised, and
  `flyway-mysql` pinned above the Spring Boot-managed versions

**Technical Focus:**

- Narrowing the match domain and giving each resource one request model for both its JSON and CSV entry points

**Test Coverage:**

- New `MatchRequestCsvMixInTest`, `CompetitorRequestCsvMixInTest`, `MatchPatchRequestTest` and
  `CompetitorPatchRequestTest`, replacing `MatchRequestForCSVTest`, `CompetitorRequestForCSVTest` and
  `MatchStageRequestTest`; stage coverage removed from the match, competitor, repository and transaction service tests

### Phase 43: Match Competitor API & Shooter Log Rework (v9.1.0)

**Duration:** October 3, 2026

A minor release: match competitors, the entries holding a competitor's results in a match, get their own endpoints and
bulk CSV import, and the shooter log entities are reworked beneath them. It adds endpoints and optional fields and
removes or tightens nothing, so it is MINOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Match Competitor API**

- New `IpscMatchCompetitorController` at `/ipsc/match-competitors` and `IpscMatchCompetitorService` with its
  implementation: create, replace (`PUT`), patch, get one or all, and delete, with `MatchCompetitorRequest`,
  `MatchCompetitorPatchRequest` and `MatchCompetitorResponse`; a competitor can have one entry per match and firearm
  type, under one or more categories
- A patch applies only the fields it supplies and never clears a required one; a duplicate entry is refused on create,
  replace and patch
- New `POST /ipsc/match-competitors/bulk` takes `text/csv` through `MatchCompetitorRequestCsvMixIn` and returns a
  `MatchCompetitorResponseHolder`; every row is checked, including against the other rows and the existing entries,
  before any is saved, and all are saved in one transaction
- `MatchCompetitorRequest` has a `@JsonCreator` constructor with a `@JsonProperty` on every parameter, and a
  `matchCompetitorId`

**Match & Competitor API**

- `MatchRequest.matchFirearmType` and `matchCategory` are no longer required in JSON or CSV
- New optional `Competitor.isVerified` on `CompetitorRequest` (JSON and CSV), `CompetitorPatchRequest` and
  `CompetitorResponse`; `emailAddresses` follows `cellphoneNumber` in the competitor models

**Persistence**

- `MatchCompetitor.competitorCategory` and `ShooterLogCompetitor.competitorCategory` are lists held in the
  `match_competitor_category` and `shooter_log_competitor_category` tables; `division` is required on both, and
  `MatchCompetitor.firearmType` is optional
- `ShooterLog` is a date range linked to matches through `shooter_log_match`; new `ShooterLogOverall` entity and
  `ShooterLogOverallRepository`; `ShooterLogCompetitor` links to `Competitor` directly and gains `dateCalculated`
- `Competitor.isVerified` column, backfilled to `true`
- Eleven Flyway migrations, `V8_0_0` to `V8_9_0`

**Services**

- `TransactionService.saveMatchCompetitor`, `saveMatchCompetitors` and `deleteMatchCompetitor`, the delete and batch
  save flushing inside the transaction so a constraint violation is reported as a 400
- `IpscMatchService.deleteMatch` also refuses a match linked to a shooter log, and
  `IpscCompetitorService.deleteCompetitor` a competitor in a shooter log or with an overall row

**Roadmap**

- No new gaps and none closed; Gap #6 moves to partially completed, with the match competitor half built and the
  shooter-log half not, leaving no open gap, and Gap #26 now waits on a Spring Boot release that manages Tomcat
  `11.0.26`

**Build & Metadata**

- Project version bumped to 9.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Dependencies**

- The `mysql-connector-j` pin is dropped, since Spring Boot `4.1.1` now manages `9.7.0`

**Documentation**

- `AGENTS.md`'s Release Checklist and the `prep-version-release` skill gain a step that aligns the Markdown tables in
  the files a release touches, measured in display columns and run before `RELEASE_NOTES.md` is archived; the later
  steps are renumbered

**Technical Focus:**

- Extending the request-model-plus-mix-in pattern to a third resource, with an all-or-nothing batch import

**Test Coverage:**

- New controller, service, implementation and integration tests for the match competitor endpoints, request model and
  CSV mix-in, and for `ShooterLogOverallRepository`; new `TransactionServiceTest` cases for `saveMatchCompetitors`

### Phase 44: PractiScore-Style Match Competitor Import (v10.0.0)

**Duration:** October 3, 2026

A major release: the match competitor model and CSV import are reshaped around a PractiScore results export. Match
competitors carry the export's overall scores, are found by membership number or name, and hold a single competitor
category again. Three changes are backward-incompatible, so it is MAJOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Match Competitor API**

- `MatchCompetitorRequest`, `MatchCompetitorPatchRequest` and `MatchCompetitorResponse` gain optional `percentage`,
  `time`, `percentageOfPossiblePoints`, `alpha`, `charlie`, `delta`, `misses`, `noPenaltyMisses`, `noShoots`,
  `proceduralErrors` and `additionalPenalties`, carried through `IpscMatchCompetitorServiceImpl` to and from
  `MatchCompetitor`
- **Breaking:** `matchPoints` is renamed `points` in the JSON contract
- **Breaking:** `MatchCompetitorRequestCsvMixIn`'s columns follow a PractiScore export — `MatchClub` → `Class`,
  `CompetitorCategory` → `Cats`, `Division` → `Div`, `PowerFactor` → `PF`, `MatchPoints` → `Pts`, plus `%`,
  `Time`, `% psbl`, `A`, `C`, `D`, `M`, `NPM`, `NS`, `Proc` and `Apen` for the overall scores; the controller's
  CSV example shows them
- **Breaking:** `competitorCategory` (CSV `Cats`) is a single category again, a JSON string rather than an array and one
  value per CSV cell rather than a `;`-separated list

**Competitor Resolution**

- `competitorId` is no longer required: a request carries it, a `competitorNumber` (CSV `Mem #`) or a `name` (CSV
  `Name`), and `IpscMatchCompetitorServiceImpl` resolves them in that order; the number is matched exactly against
  `Competitor.competitorNumber` and the name as "First Last", ignoring case
- New `CompetitorRepository.findByCompetitorNumber` and `findByFullNameIgnoreCase` return a list, so a number or name
  that matches no competitor, or more than one, is refused

**Persistence**

- `V10_0_0` renames `match_competitor.match_points` to `points`; `V10_1_0` adds the overall-score columns;
  `V10_2_0` returns `competitor_category` to a single column on `match_competitor` and `shooter_log_competitor`,
  dropping `match_competitor_category` and `shooter_log_competitor_category`, with a row that had several categories
  keeping the alphabetically first and one with none an empty category; `V10_3_0` drops the `hit_factor` column that
  `V10_1_0` had created
- Migrating an empty MySQL 8.4 database from `V7_0_0` found that `V10_2_0` placed `shooter_log_competitor`'s new column
  `AFTER match_id`, which `V8_3_0` had dropped, so the migration failed on every database; it now places the column
  after `match_competitor_id`, and all 25 migrations apply from empty

**Removals**

- `MatchOverallScoresRequest`, `MatchOverallScoresRequestForCSV`, `MatchStageScoresRequest` and
  `MatchStageScoresRequestForCSV` and their tests are deleted — they modelled scores for the removed stage tables and
  nothing referenced them; the overall-score fields now live on `MatchCompetitor`

**Build & Metadata**

- Project version bumped to 10.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Matching the import format to the source data, and resolving a record's owner by a natural key rather than a
  database id, refusing a missing or ambiguous match

**Test Coverage:**

- Updated request, patch request, CSV mix-in, service, implementation and integration tests for the renamed and new
  fields, the single category and competitor resolution; the deleted request models' tests are removed

### Phase 45: Competitor Contract Tightening & Competitor Lookup Service (v11.0.0)

**Duration:** October 4, 2026

A major release: the competitor contract is tightened and normalised, and a competitor lookup service is added. Every
bulk endpoint reads `text/plain`, the nickname is spelt `nickName` throughout and `competitorNumber` is a whole number.
Three groups of changes are backward-incompatible, so it is MAJOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Bulk Endpoints**

- **Breaking:** every `POST /bulk` endpoint — awards, images, competitors, matches and match competitors — consumes
  `text/plain` instead of `text/csv`, and answers `415` to any other content type; the request body is unchanged

**Competitor Nickname**

- **Breaking:** `nickname` is renamed `nickName` in the `Competitor` entity field and the JSON property, and the CSV
  column `Nickname` becomes `NickName`
- `V11_0_0` renames the `competitor.nickname` column to `nick_name`, keeping its type, nullability and values

**Competitor Number**

- **Breaking:** `Competitor.competitorNumber` is an `Integer`, held in an `INT` column by `V11_1_0`, which is refused
  when existing data is non-numeric
- **Breaking:** competitor and match competitor requests (JSON and CSV `CompetitorNumber`/`Mem #`) still send a string,
  but it must be a whole number and is refused otherwise; a blank means not supplied, and a blank in a patch leaves the
  stored number unchanged; `CompetitorResponse` returns a whole number

**Competitor Lookup Service**

- New `EntityIpscCompetitorService.findCompetitor(fullName, competitorNumber)` and its implementation resolve one
  `Competitor`: first by number, skipping the excluded ICS aliases 15000 and 16000, then by full name — "FirstName
  LastName" or "NickName LastName", ignoring case, trimmed first and with an RO suffix stripped — narrowed to the
  number matches; it is not yet wired into a caller
- New `MatchCompetitorResult` and `MatchCompetitorResultHolder` response models, unused so far

**Repository & Services**

- `CompetitorRepository`'s finders are renamed `findAll…`, and the new
  `findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase` backs the name lookup
- `IpscConstants.MAX_SAPSA_NUMBER` rises from 99,999 to 999,999 and `EXCLUDE_ICS_ALIAS` becomes a `List<Integer>`
- `IpscCompetitorServiceImpl.resolveCompetitorNumber` falls back to the SAPSA number
- Class-level `@since` tags are added

**Build & Metadata**

- Project version bumped to 11.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Giving the competitor one content type, one spelling and one numeric type, and refusing data that cannot be
  converted rather than guessing

**Test Coverage:**

- Three-tier tests for `EntityIpscCompetitorService` and its implementation, and updated request, response, CSV and
  repository tests for the renamed and retyped fields; the suite stands at 1,109 passing tests

### Phase 46: Match Competitor Lookup, Partial Bulk Import & Club Filtering (v12.0.0)

**Duration:** October 5, 2026

A major release: the competitor lookup built in v11.0.0 is wired into the match competitor service, the bulk import
becomes a partial import limited to one club, and a match no longer needs a category. Four groups of changes are
backward-incompatible, so it is MAJOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Competitor Lookup**

- `EntityIpscCompetitorService.findCompetitor(competitorNumber, fullName)` is called by `IpscMatchCompetitorServiceImpl`
  through `resolveCompetitor`: the number, which may also be an ID number, then the full name, narrowing several number
  matches by name, and an exception rather than an empty result when no single competitor matches
- **Breaking:** the shared alias numbers 15000 and 16000 are no longer matched by competitor number, and a number that
  is not a whole number is looked up as an ID number, answering `404` when nothing matches instead of a `400`
- The range officer marker `RO` or `(RO)` is removed from a name wherever it appears, and a competitor created or
  updated without a nickname takes its first name as the nickname
- New `CompetitorHelpers.getCompetitorNumberAsInteger` and `CompetitorRepository.findAllByIdNumber`

**Bulk Import**

- **Breaking:** `createMatchCompetitors` is a partial import — each row is saved on its own and a failed row is
  reported and skipped while the rest are created; an import in which every row fails answers `422`
- **Breaking:** `MatchCompetitorResult` and `MatchCompetitorResultHolder` are renamed `MatchCompetitorBulkResponse` and
  `MatchCompetitorBulkResponseHolder`, the body carries `matchCompetitors` instead of `matchCompetitorResults`, and
  `MatchCompetitorResponse` gains `competitorName` and `competitorNumber`
- **Breaking:** the import is limited to one club — HPSC's own club unless the optional `club` query parameter names
  another — and imports the rows whose match club, or whose competitor's home club, is that club, reporting the rest as
  skipped
- New `ClubService`, with `ClubServiceImpl`, holds the null-safe club comparisons, and
  `IpscCompetitorServiceImpl.isMemberOfHomeClub` takes the home club as a parameter, defaulting to HPSC

**Matches**

- The match category is optional: one that is null, empty or blank takes the default, Club Shoot, and a supplied one may
  be a display name or a constant name, ignoring case and surrounding whitespace

**Fixes**

- `ControllerResponse` derives `success` from its error the right way round
- `findCompetitor` no longer throws a `NumberFormatException` for a numeric value too long for an `int`

**Housekeeping**

- `DateUtil`, `NumberUtil`, `StringUtil` and `ValueUtil` are renamed back from their plural names, `StringUtil` gains
  `hasText`, and `IpscConstants` loses `MAX_SAPSA_NUMBER` and four unused score-scale constants and documents the rest
- Improvement plan Gaps #36 and #37 are closed, `qodana.yaml` is added and `mysql-connector-j` is pinned to 26.7.0

**Build & Metadata**

- Project version bumped to 12.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Putting the lookup to work, and making an import report each row instead of refusing the file

**Test Coverage:**

- Three-tier tests for the lookup wiring, the partial import, the club filter and `ClubService`; the suite stands at
  1,227 passing tests

### Phase 47: Mappers, Division–Firearm Type Consistency & Match Competitor Contract Tightening (v13.0.0)

**Duration:** October 7, 2026

A major release: the field copying and lookups leave the three IPSC services for mapper components, a division records
the firearm type it is shot with and has a name of its own, and the match competitor's contract tightens. Three groups
of changes are backward-incompatible, so it is MAJOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Mappers**

- New `CompetitorMapper`, `MatchMapper` and `MatchCompetitorMapper` hold `applyFields`, a new `applyPatchFields` and the
  gender, club, match, category, firearm type, division and power factor lookups that the services carried, so a service
  constructor takes a mapper in place of the repositories those lookups used
- `MatchCompetitorMapper` rejects a division that does not belong to the firearm type, through
  `validateDivisionMatchesFirearmType`, checking a patch against the other value's current one, and takes a missing
  firearm type from the division

**Enums**

- `Division` gains a `firearmType` and every division has a unique name: the shotgun, .22 and mini rifle divisions that
  shared a handgun name are renamed, the semi auto rifle divisions become `Rifle Open Division` and
  `Rifle Standard Division`, and `SHOTGUN_STANDARD_MANUAL` becomes `Shotgun Standard Manual Division`
- **Breaking:** the accepted division names change, and `SENIOR_LADY` is now `Lady Senior`, so a request using
  `Lady, Senior` is rejected
- The unused `code` and `abbreviation` fields and the lookups that only tests called are removed from `Division`,
  `FirearmType`, `PowerFactor` and `CompetitorCategory`, which gains an abbreviation instead of its code

**Database**

- `V11_2_0` makes `match_competitor.firearm_type` and `power_factor` `NOT NULL`, `V11_3_0` adds the optional
  `date_calculated` column, `V11_4_0` renames the stored divisions by firearm type and `V11_5_0` renames the stored
  category `Lady, Senior` to `Lady Senior`

**Models**

- **Breaking:** `MatchCompetitorResponse` replaces `competitorName` with a `competitorNames` list holding the
  competitor's "First Last" and "Nick Last" names once each
- **Breaking:** `powerFactor` is required on a match competitor request, as `firearmType` already was; `firearmType` is
  then made optional again, taken from the division when left out
- `MatchCompetitorRequest` and `MatchCompetitorPatchRequest` extend `IpscMatchScore`, `weightedPoints` is renamed
  `points` and the power factor leaves the score models
- `CompetitorRequest`, `MatchRequest` and `MatchCompetitorRequest` each carry their own `validate()`, and
  `CompetitorResponse`, `MatchResponse` and `MatchCompetitorResponse` build themselves from their entities

**Housekeeping**

- `NumberUtil.calculatePercentage` and `calculateSum` take a `scale` and the percentage rounding is fixed, null
  contracts are marked `@Nullable`, `.gitattributes` normalises text files to LF and `qodana.yaml` moves to the
  `qodana.recommended` profile
- Improvement plan Gap #38 is partially completed and Gap #39 is recorded

**Build & Metadata**

- Project version bumped to 13.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Separating mapping from orchestration, and making the match competitor's enumerated values agree with each other

**Test Coverage:**

- Unit tests for the three mappers, including `applyPatchFields`, the division and firearm type checks and the new
  enum behaviour, with the integration tests adjusted for the stricter requests; the suite stands at 1,265 passing
  tests

### Phase 48: Request-Body Validation, jspecify Nullness & Competitor Name Cleaning (v13.1.0)

**Duration:** October 7, 2026

A minor release: the IPSC controllers validate their request bodies and answer `400` for a violation, jspecify's
`@NonNull` replaces Jakarta's `@NotNull` for nullness, and a competitor's name is cleaned before it is matched. All of
it is backward-compatible, so it is MINOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Validation**

- `IpscCompetitorController`, `IpscMatchController` and `IpscMatchCompetitorController` annotate their `@RequestBody`
  parameters with `@Valid`, and `CompetitorRequest`, `MatchRequest` and `MatchCompetitorRequest` gain `@NotBlank` and
  `@NotNull` constraints with the messages their `validate()` methods already used
- New `ControllerAdvice.handleMethodArgumentNotValidException` maps a violation to a `400 Bad Request` listing every
  violated constraint, instead of the generic `500`
- `MatchCompetitorRequest.validate()` returns `void` and throws on failure, as the other request models do

**Nullness**

- `org.jspecify.annotations.NonNull` replaces `jakarta.validation.constraints.NotNull` on the services', mappers' and
  response models' parameters and fields, with the explicit null checks unchanged, and `org.jspecify:jspecify` is
  declared as a direct dependency in `pom.xml`

**Competitor Names**

- New `CompetitorHelpers.cleanCompetitorName` removes a leading position of up to two digits followed by `-`, an `RO` or
  `(RO)` marker and all full stops, collapses whitespace and returns an empty string for a null name
- `MatchCompetitorMapper.applyFields` and `applyPatchFields` clean the request's competitor name before the lookup, and
  `EntityIpscCompetitorServiceImpl.findCompetitor` normalises the full name before matching by name

**Documentation**

- `flyway-migration-versioning.md`'s Current State table is re-aligned and a missing comma is added to `AGENTS.md`
- Improvement plan unchanged: 35 gaps closed, #6, #26 and #38 partially completed and #39 open

**Build & Metadata**

- Project version bumped to 13.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Rejecting an invalid request at the controller, and normalising the competitor name that the lookup matches on

**Test Coverage:**

- `CompetitorHelpersTest` covers `cleanCompetitorName` and `ControllerAdviceTest` covers the `400` handling of an
  invalid request body
- The suite stands at 1,277 passing tests

---

**For the full project history, see [HISTORY.md](/HISTORY.md)**
