# HPSC Website Backend History

A comprehensive historical overview of the HPSC Website Backend project from start to current release, documenting the
evolution of architecture, features and design philosophy across all versions.

---

## Table of Contents

- [📅 Historical Timeline](#-historical-timeline)
- [📖 Evolution Overview](#-evolution-overview)
- [🎯 Major Milestones](#-major-milestones)
- [🏛️ Architectural Evolution](#-architectural-evolution)
- [✨ Feature Timeline](#-feature-timeline)
- [💡 Project Philosophy Evolution](#-project-philosophy-evolution)
- [📚 Key Learnings](#-key-learnings)
- [🛤️ Future Roadmap](#-future-roadmap-implications)
- [🎓 Conclusion](#-conclusion)

---

## 📅 Historical Timeline

### Version 8.8.0 (September 24, 2026)

**Theme:** Competitor & Match Delete Endpoints

**Key Focus:**

- New `DELETE /ipsc/competitors/{competitorId}` and `DELETE /ipsc/matches/{matchId}` endpoints
  (`IpscCompetitorController.deleteCompetitor`/`IpscMatchController.deleteMatch`, backed by
  `IpscCompetitorService.deleteCompetitor`/`IpscMatchService.deleteMatch`), each returning `204 No Content`, or
  `404` when the record doesn't exist
- Deletion rejects rather than cascades: a competitor still referenced by `MatchCompetitor` or `ShooterLog` rows,
  or a match still referenced by `MatchCompetitor`, `MatchStageCompetitor` or `ShooterLogCompetitor` rows, is
  refused with a `ValidationException` (`400`), so scoring history is never deleted as a side effect
- What a record owns goes with it — a competitor's email addresses and a match's stages are removed alongside it
- Each delete is flushed inside the service method, so a reference added by another request between the checks and
  the delete is still reported as a `400`, not a `500`
- New `existsBy…` queries on `MatchCompetitorRepository`, `MatchStageCompetitorRepository`, `ShooterLogRepository`
  and `ShooterLogCompetitorRepository` back the dependent-row checks
- `ARCHITECTURE.md` documents the reject-not-cascade rule, and `standard-rest-conventions.md`'s current-state
  examples now cover both controllers' full `getAll`/`get`/`create`/`update`/`patch`/`delete` sets
- Closed Gap #12 in `documentation/roadmap/improvement-plan.md`, making the "CRUD" claims in `README.md`/
  `ARCHITECTURE.md` accurate; this release's own improvement-plan audit recorded new Gap #13 — the Claude Code
  review/assistant GitHub workflows are missing from the CI/CD & Quality Gates documentation
- Scoped as `v8.8.0` **MINOR** for the two new endpoints — purely additive, with no schema change
- Project version bumped to 8.8.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.7.0 (September 24, 2026)

**Theme:** Competitor Listing Endpoint, Stage Delimiter Change & Dependency Clean-up

**Key Focus:**

- New `GET /ipsc/competitors` endpoint (`IpscCompetitorController.getAllCompetitors`, backed by
  `IpscCompetitorService.getAllCompetitors`) returns every competitor as a JSON array — the collection counterpart
  to `GET /{competitorId}`, mirroring `IpscMatchController.getAllMatches`
- A match CSV's `Stages` cell now separates each stage number from its name with `:` instead of `-` (e.g.
  `"1:Stage One;2:Stage Two"`); only the first `:` splits, and entries in the old `1-Stage One` form are now
  rejected with a `ValidationException` — existing CSV templates need updating
- Every `@ManyToOne` association on the seven domain entities switched from `FetchType.LAZY` to `FetchType.EAGER`,
  so referenced entities load together with their owner
- The `server.port=8081` override removed, so the app now runs on Spring Boot's default port `8080`; every
  documented app, Swagger UI and OpenAPI URL updated to match
- `springdoc-openapi-starter-webmvc-ui` bumped from `2.8.5` to `3.1.0` — the line built for Spring Boot 4 — with
  its version now managed by an imported `springdoc-openapi-bom`; the unused `spring-restdocs-mockmvc` test
  dependency and its documentation mentions removed
- `IpscMatchController.getAllMatches`' Swagger `200` response corrected to document an array of `MatchResponse`s
  rather than a single object
- This release's own improvement-plan audit recorded new Gap #12: competitors and matches are documented as "full
  CRUD", yet no delete operation exists for either
- Scoped as `v8.7.0` **MINOR** for the new endpoint, with the stage-delimiter and port changes called out as
  client-facing migrations
- Project version bumped to 8.7.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.6.2 (September 24, 2026)

**Theme:** `CHANGELOG.md` Heading-Depth Correction, Future Roadmap Refresh & Icon Registry Sync

**Key Focus:**

- `AGENTS.md`, `CONTRIBUTING.md` and five Claude Code skills (`generate-commit-message`, `prep-version-release`,
  `scaffold-unit-tests`, `scaffold-integration-tests`, `sync-unreleased-changes`) described `CHANGELOG.md` as
  `## 🧪 [Unreleased]` → `### <category>` → `#### <Area>` — one level shallower than the `###`/`####`/`#####`
  depth the file has actually used — so every reference was corrected to match
- `AGENTS.md`'s Git Workflow Conventions now spell out the full `#### <category>` → `##### <Area>` nesting, reuse of
  existing Area names and the bold-lead-in bullet style, instead of leaving them implicit;
  `generate-commit-message` also notes that security-relevant fixes belong under `#### 🔐 Security`
- The correction was reverse-synced from the shared project template, which had already fixed the same drift in its
  own copy of these conventions
- This file's own "🛤️ Future Roadmap Implications" Short-term/Medium-term lists refreshed against what has actually
  shipped — the club-seeding bullet reduced to its still-outstanding `Competitor.homeClub` backfill half,
  `ShooterLogEntry` renamed to `ShooterLogCompetitor`, "Medium-term (v7.x+)" relabelled "Medium-term (Later v8.x
  Releases)" and "Bulk match processing capabilities" dropped as delivered by v8.3.0 — recorded and closed as Gap #11
  in `documentation/roadmap/improvement-plan.md` within this same release
- Roadmap icons synced with the shared project template: `🛤️` now marks Roadmap (replacing `🗺️`) and `☑️` marks
  Success Criteria across `AGENTS.md`'s registry and every live Roadmap heading, and `improvement-plan.md`/
  `improvement-plan-tasks.md` pick up the template's generic structure notes
- `AGENTS.md`'s whole icon registry restructured to mirror the template's — its core icons, its backend / API
  service set as this project's own and its frontend set kept reserved — with every live heading realigned to
  match (Documentation Conventions `✍️`, Documentation File Map `🗺️`, Key Design Patterns `🧭`, Data Flow `🔃`,
  Development Guidelines `🛠️`, Getting Started `🚀`, At a Glance `🌳`, Related Documentation `🔗`)
- Minor table column realignment in `AGENTS.md`'s skills table and `README.md`'s Documentation table
- This release's entire diff against `main` proved documentation/tooling-only (no `src/main/java`/`src/test`
  behaviour change), so it was scoped as `v8.6.2` **PATCH**, matching the precedent set by v8.4.1/v8.4.2/v8.5.1/
  v8.6.1
- Project version bumped to 8.6.2 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.6.1 (September 23, 2026)

**Theme:** `documentation/history/` Reorganisation & Evolution Overview Split

**Key Focus:**

- `HISTORY.md`'s "📖 Evolution Overview" section (the Phase-by-phase narrative) split out into new
  `documentation/history/EVOLUTION_OVERVIEW.md` — it had grown to roughly half of `HISTORY.md`'s 4,095 lines,
  making the file unwieldy. `HISTORY.md` keeps a short pointer section under the same heading/anchor, so its
  Table of Contents entry still resolves; every other section stays in `HISTORY.md` unchanged
- All 52 archived `RELEASE_NOTES_vX.Y.Z.md`/`PR_DESCRIPTION_vX.Y.Z.md` files regrouped from a flat
  `documentation/history/` directory into `v1/` – `v8/` subdirectories by major version, moved with `git mv` to
  preserve history; `EVOLUTION_OVERVIEW.md` is unaffected, staying directly in `documentation/history/`
- `AGENTS.md`'s Documentation File Map and Release Checklist (both copies of the archive-path references),
  `README.md`'s Documentation table, and the `prep-version-release`/`generate-pr-summary`/
  `update-improvement-plan-gaps` skills all updated to read/write the new `documentation/history/v<major>/...`
  paths, deriving `<major>` from a version's leading number before the first `.`
- This release's entire diff against `main` proved documentation/tooling-only (no `src/main/java`/`src/test`
  change), so it was scoped as `v8.6.1` **PATCH** rather than a new minor version — matching the precedent set by
  v8.4.1/v8.4.2/v8.5.1
- Project version bumped to 8.6.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.6.0 (September 23, 2026)

**Theme:** Match URL Field, Start/End Time Precision Fix & Test Architecture Formalisation

**Key Focus:**

- `IpscMatch` gains a new nullable `url` column via `V7_6_0__add_ipsc_match_url.sql` — a URL with more information
  about a match (e.g. a results page or event listing) — wired end-to-end through `MatchRequest`,
  `MatchRequestForCSV`, `MatchResponse` and `IpscMatchServiceImpl`'s `applyFields`/`patchMatch`/`toRequest`/
  `toResponse`; `IpscMatchController`'s CSV bulk import header gains a matching `Url` column
- `IpscMatch.startTime`/`endTime` corrected from `LocalDateTime` to `LocalTime` via
  `V7_7_0__change_ipsc_match_start_end_time_to_time.sql` — these were always time-of-day-only values alongside
  `scheduledDate`, so the redundant date component v8.5.0 introduced is dropped. New
  `IpscConstants.IPSC_INPUT_TIME_FORMAT` (`HH:mm`) constant replaces `IPSC_INPUT_DATE_TIME_FORMAT` on both fields;
  JSON/CSV `startTime`/`endTime` values are now bare `HH:mm` instead of `yyyy-MM-dd HH:mm` — existing CSV templates
  and API clients need updating to drop the date component
- `AGENTS.md`'s Test Conventions section now formally documents the 3-tier service test architecture
  (`<Service>Test`/`<Service>ImplTest`/`<Service>IntegrationTest`) already followed by all four services, replacing
  a one-line pointer that previously only described the pattern piecemeal across the `scaffold-unit-tests`/
  `scaffold-integration-tests` skills
- Project version bumped to 8.6.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.5.1 (September 13, 2026)

**Theme:** `HISTORY.md` Phase/Milestone Backfill & Release Re-Scoping to a Patch Version

**Key Focus:**

- `HISTORY.md`'s "📖 Evolution Overview"/"🎯 Major Milestones" sections backfilled with Phase 26/27/28 and
  Milestone 26/27/28 entries for v8.4.1, v8.4.2 and v8.5.0, summarising each release's existing Historical Timeline
  content — closes `improvement-plan.md`'s Gap #10, the three-release gap in the otherwise-mandatory per-release
  Phase/Milestone step
- `improvement-plan.md`/`improvement-plan-tasks.md`: new "📋 At a Glance" gap-status index; "🗺️ Roadmap" table's
  **Now**/**Next** rows refreshed to drop already-closed #2/#7 and promote #6; Gap #10 tracked from ⚪ Open through
  to ✅ Completed
- `HISTORY.md`'s "Major Version Goals" Version 8.x entry extended from `v8.0.0 – v8.1.1` to `v8.0.0 – v8.5.1`,
  narrating the domain broadening (multi-value competitor emails, match bulk CSV import, relaxed club-requirement
  defaults, match start/end time tracking) and documentation-process discipline delivered across v8.2.0 – v8.5.1
- `prep-version-release`/`generate-pr-summary` skills now end their drafted PR description/summary with the standard
  Claude Code attribution footer, marking them as Claude-drafted like any other PR description Claude Code opens
- `HISTORY.md`'s "📖 Evolution Overview", "🎯 Major Milestones", "🏛️ Architectural Evolution" and "🗺️ Future
  Roadmap Implications" reordered to ascending (oldest-first), matching "✨ Feature Timeline"/"💡 Project
  Philosophy Evolution"'s existing convention — only "📅 Historical Timeline" keeps its most-recent-first order;
  stale legacy Conclusion-section metadata and update log (out of date since v8.0.0/v5.1.0) removed
- This release's entire diff against `main` proved documentation/tooling-only (no `src/main/java`/`src/test`
  change), so it was re-scoped from the originally-planned `v8.6.0` **MINOR** version down to `v8.5.1` **PATCH** —
  matching the precedent set by v8.4.1/v8.4.2 — with the branch and every in-flight documentation reference renamed
  to match before this release-prep pass
- Minor wording fixes: `CONTRIBUTING.md` spells out "and" instead of "&"; a Flyway baseline comment in
  `application-local.properties` tightened
- Project version bumped to 8.5.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.5.0 (September 4, 2026)

**Theme:** Match Start/End Time Tracking

**Key Focus:**

- `IpscMatch` gains nullable `startTime`/`endTime` (`LocalDateTime`) columns, alongside the existing
  `scheduledDate`, via new `V7_5_0__add_ipsc_match_start_end_time.sql`; wired end-to-end through `MatchRequest`,
  `MatchRequestForCSV` and `MatchResponse`, and `IpscMatchServiceImpl`'s `applyFields`/`patchMatch`/`toRequest`/
  `toResponse`
- CSV bulk import (`POST /matches/csv`) now requires `StartTime`/`EndTime` header columns, like every other
  `MatchRequestForCSV` property, consistent with this endpoint's existing all-columns-required header validation —
  existing CSV templates need updating to add them (values may be left blank)
- New coverage in `IpscMatchServiceIntegrationTest` proves the two-column round-trip through the real H2/Hibernate/
  JPA layer, not just mocked repositories; `IpscMatchServiceTest`'s CSV bulk-import test now supplies actual
  `StartTime`/`EndTime` values, closing the one gap where that mapping was never verified
- `README.md`'s "License" heading/prose corrected to British English "Licence"; `CONTRIBUTING.md`'s own Serial
  Commas rule example corrected to no longer violate the rule it illustrates; `AGENTS.md`'s British English
  exception for `LICENSE.md` narrowed to just the filename and the file's own content, so every other reference
  to it spells it "Licence" instead of carving out a wider exception
- Project version bumped to 8.5.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.4.2 (September 4, 2026)

**Theme:** Root Document Title Standardisation & Source-of-Truth Clarification

**Key Focus:**

- `AGENTS.md` now states, right before its Documentation File Map, that it is this project's ultimate source of
  truth for conventions — every other file's workflow/convention guidance points back to it rather than restating
  it; `CONTRIBUTING.md`'s intro carries the matching pointer, stating `AGENTS.md` wins if anything else in the
  repository's documentation ever contradicts it
- `CHANGELOG.md`'s title changed from "Changelog" to "HPSC Website Backend", with a new "🧾 Change Log"
  second-level heading beneath it, matching `README.md`'s existing project name; every heading below it — Table of
  Contents, each version and their category/area sub-headers — demoted one level to nest correctly under the new
  heading. `CONTRIBUTING.md`/`HISTORY.md`'s H1 titles gain the same "HPSC Website Backend" prefix for consistency
- Project version bumped to 8.4.2 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.4.1 (September 4, 2026)

**Theme:** Documentation Cross-Reference Consolidation & Icon Registry Sync

**Key Focus:**

- `AGENTS.md`/`CONTRIBUTING.md`'s full/near-verbatim content duplicates condensed into highlights-and-link
  references, matching the pattern already established for sections like Roadmap and Release Checklist — Git
  Workflow's Branching Model, Conventions and Directory Tree Maintenance bullets, the Exception handling and
  CHANGELOG-same-change/Evergreen bullets, and the CI/CD & Quality Gates table (now pointed at `ARCHITECTURE.md`,
  its actual source of truth). Git Workflow's "Merging" subsection consolidated as `CONTRIBUTING.md`'s sole
  canonical copy, since `sync-unreleased-changes`/`sync-improvement-plan-gaps` need only the Branching Model and
  Conventions subsections to remain in `AGENTS.md`
- New `AGENTS.md` "🧩 Claude Code Skills" and "🗺️ Roadmap Planning" sections, both mirrored with a short pointer
  in `CONTRIBUTING.md`
- `AGENTS.md`'s icon registry backfilled with 25 previously-unregistered icons already in real use, plus a new
  "Reserved" sub-table tracking the sibling `hpsc-web-vite` repository's frontend-specific icons — synced twice
  this release as `hpsc-web-vite`'s own registry grew, reciprocally gaining `🧬` (Data model / DTOs) in return.
  Several icon collisions resolved across `README.md`, `ARCHITECTURE.md`, `HISTORY.md`, `RELEASE_NOTES.md` and 17
  archived per-version release notes
- `CHANGELOG.md`'s duplicate, truncated `[5.0.0]` section removed, and a pre-existing broken example in the Serial
  Commas convention (identical "e.g." and "not" contrast phrases) corrected
- Project version bumped to 8.4.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.4.0 (September 3, 2026)

**Theme:** Club Domain Defaults, Optional Club Numbers & Documentation Convention Hardening

**Key Focus:**

- New `ClubIdentifier.ALL` constant (`"Eufees Clubs"` / `"All"` / `"ALL"`) represents a match hosted jointly by all
  three real clubs; new `V7_3_0__seed_club_data.sql` migration seeds the `club` table with every named
  `ClubIdentifier` constant
- `IpscMatchController`/`IpscMatchServiceImpl`: a missing or blank match `club` now defaults to
  `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` (`ClubIdentifier.ALL`) instead of failing validation;
  `resolveClub()` mirrors the competitor domain's "apply the domain default" pattern, throwing
  `NonFatalException`/`FatalException` if the default club is missing from the database or the constant itself is
  null. Closes Gap #9
- `IpscCompetitorController`/`IpscCompetitorServiceImpl`: competitor `clubNumber` is now required only when the
  competitor's home club is HPSC, forced to `null` otherwise; `Competitor.clubNumber` column relaxed to nullable via
  new `V7_4_0__make_club_number_nullable.sql`
- `HpscConstants` removed entirely — its sole constant was an alias for `SystemConstants.ISO_DATE_FORMAT`; every
  former user now references `SystemConstants.DEFAULT_DATE_FORMAT` or `IpscConstants.IPSC_INPUT_DATE_FORMAT` directly
- JaCoCo `LINE`/`COVEREDRATIO` floor tightened from 86% to 97% after holding cleanly in CI across the `develop`/
  `main` runs that shipped v8.3.1, closing Gap #4; a fresh `./mvnw verify -Pcoverage` run at this release's prep
  time measured 98.44%/98.98% line/branch, 868 tests
- `tomcat-embed-core`/`-el`/`-websocket` overridden `11.0.24` → `11.0.25`, closing three critical CVEs still pinned
  by `spring-boot-starter-parent:4.1.1`'s dependency management
- New `AGENTS.md` conventions: Member ordering (constructors → public → protected → private), REST URL/handler-
  naming rules condensed from `standard-rest-conventions.md`, and a Release Checklist step verifying
  `ARCHITECTURE.md`'s Project Structure tree against disk at every release
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` restructured into ✅ Completed/
  🟡 Partially Completed/⚪ Open sections, replacing the previous flat Now/Next/Later/Ongoing phasing
- `ARCHITECTURE.md` brought fully back in sync with disk — missing `.claude/skills/`, `documentation/
  recommendations/` and `db/migration/` directories and `Gender`/`GenderConverter` added; stale bidirectional
  entity relationships, the coverage floor and CSV-flow references corrected
- Project version bumped to 8.4.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.3.1 (September 2, 2026)

**Theme:** CI Build/Test Gate, Coverage Enforcement & CSV Persistence Clarity

**Key Focus:**

- New `.github/workflows/build.yml` runs `./mvnw verify -Pcoverage` on push/PR to `main`/`develop`, mirroring
  `codeql.yml`'s trigger branches — completes `documentation/roadmap/improvement-plan.md`'s Gap #2 (no automatic
  build/test gate on pull requests)
- New JaCoCo `check` execution in `pom.xml`'s `coverage` profile enforces a `BUNDLE`-level `LINE`/`COVEREDRATIO`
  minimum, initially `0.51` (51%) as a deliberately low regression backstop, then raised to `0.86` (86%) within the
  same branch, wired into the new CI gate so a coverage regression fails the build — still below the actual current
  baseline, which a fresh `./mvnw verify -Pcoverage` run measured at 98.16%/98.94% (line/branch), 836 tests — up
  from 775 at v8.1.1; further tightening the floor closer to that baseline is left as a follow-up once the 86%
  threshold has run cleanly in CI, partially progressing Gap #4 (coverage measured but not enforced)
- `AwardService.createAwards()`/`ImageService.createImages()` CSV processing confirmed intentionally stateless by
  design, not an unfinished persistence layer — `README.md`/`ARCHITECTURE.md` now state this explicitly, closing
  Gap #3 (open since v8.1.0)
- `ARCHITECTURE.md`/`CONTRIBUTING.md`'s CI/CD & Quality Gates tables updated to reflect the new gate and drop the
  stale "locally / by reviewers"/"All PRs" language; `ARCHITECTURE.md`'s Award/Image CSV Processing Flow diagram and
  `documentation/roadmap/improvement-plan.md`'s Gap #3 Evidence also corrected from the stale `processCsv()` method
  name to `createAwards()`/`createImages()`, renamed back in v8.0.0
- `AwardControllerTest`/`ImageControllerTest`'s stale `// processCsv()` test-grouping comments corrected to
  `// createAwards()`/`// createImages()`
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md`: Gap #2 closed, Gap #3 closed, Gap #4
  marked partially progressed
- Project version bumped to 8.3.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.3.0 (September 2, 2026)

**Theme:** Match Bulk CSV Import

**Key Focus:**

- New `IpscMatchController.createMatches` (`POST /ipsc/matches/bulk`, consumes `text/csv`) backed by
  `IpscMatchService`/`IpscMatchServiceImpl`, extending the IPSC match module with the same bulk-import convention
  `IpscCompetitorController.createCompetitors` established in v8.1.0 — each row persisted via the existing
  `createMatch` validation/club/firearm-type/category-resolution logic
- New `MatchRequestForCSV` model (`models/ipsc/match/request/`), matching `CompetitorRequestForCSV`'s
  `UpperCamelCase` `@JsonCreator` pattern; its `stages` field is a single semicolon-separated CSV cell of
  `<stageNumber>-<stageName>` entries (e.g. `"1-Stage One;2-Stage Two"`) rather than a nested list, since CSV has no
  native concept of a repeated group — an earlier `numberOfStages` count field was tried and dropped in favour of
  this delimited design before either ever reached `develop`
- New `IpscMatchServiceImpl.parseStages` helper splits each `Stages` cell entry on its first `-` into a
  `MatchStageRequest`; new `readMatches`/`toRequest` helpers mirror `IpscCompetitorServiceImpl`'s CSV-parsing pattern
- New `MatchResponseHolder` response container (`models/ipsc/match/response/`), mirroring `CompetitorResponseHolder`
- New unit tests across `IpscMatchController`/`Service`/`ServiceImpl`'s bulk import, and `MatchRequestForCSV`'s
  `UpperCamelCase` JSON/CSV (de)serialisation and required-field enforcement
- Project version bumped to 8.3.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.2.0 (September 1, 2026)

**Theme:** Competitor Multi-Email Support & Bulk CSV Separator Standardisation

**Key Focus:**

- `Competitor.emailAddress` (a single, optional `String`) replaced with `emailAddresses` (`List<String>`), mapped
  via `@ElementCollection`/`@CollectionTable` onto a new `competitor_email` child table — a competitor can now have
  zero or more email addresses; `V7_2_0__add_competitor_emails.sql` backfills the new table from any existing
  non-blank `email_address` values before dropping that column
- `CompetitorRequest`/`CompetitorResponse` renamed `emailAddress` to `emailAddresses`; `CompetitorRequestForCSV`
  keeps a single `String` CSV cell but now holds zero or more semicolon-separated addresses (e.g. `"a@x.com;b@x.com"`),
  split into a list via `IpscCompetitorServiceImpl`'s new `splitEmailAddresses` helper
- New shared `SystemConstants.ARRAY_SEPARATOR` (`";"`) constant; `AwardServiceImpl`/`ImageServiceImpl`'s bulk CSV
  parsing switched from `"|"` to it, so every bulk CSV endpoint's multi-value cells (competitor email addresses,
  image/award tags) now share one separator convention, with the `AwardController`/`ImageController`/
  `IpscCompetitorController` Swagger examples updated to match
- Qodana static analysis removed entirely (`.github/workflows/qodana.yml`, `qodana.yaml`, and every reference in
  `ARCHITECTURE.md`/`CONTRIBUTING.md`/`AGENTS.md`'s CI/CD documentation): a release audit found it had failed on
  every CI run since v8.1.1 added it, and `documentation/roadmap/improvement-plan.md`'s Gap #7 closes as not
  applicable rather than delivered
- New genuinely multiple-address tests (not just single-address or null/empty) added across every layer
  `emailAddresses` touches, surfacing a real bug: `IpscCompetitorServiceImpl.applyFields`/`patchCompetitor` stored
  the caller-supplied `List` reference directly onto the entity, crashing with an unhandled
  `UnsupportedOperationException` on an immutable list at Hibernate merge time; both now defensively copy into a
  new `ArrayList`
- Project version bumped to 8.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.1.1 (September 1, 2026)

**Theme:** CI Static Analysis, Release-Process Self-Maintenance & Coverage Regression Fixes

**Key Focus:**

- New `.github/workflows/qodana.yml` runs JetBrains' `qodana-action` against the existing `qodana.yaml` config on
  push/PR to `develop`/`main`, mirroring `codeql.yml`'s trigger branches; results upload as SARIF to GitHub code
  scanning
- Two new Claude Code skills, `update-improvement-plan-gaps` (full codebase sweep for new gaps) and
  `sync-improvement-plan-gaps` (diff-driven check for gaps a branch's own work has closed or progressed), formalise
  the manual roadmap-maintenance work performed by hand across v8.0.0/v8.1.0; `generate-pr-description` renamed to
  `prep-version-release` to reflect its actual scope, and now runs both new skills as its own first step
- `AGENTS.md`'s Release Checklist re-synced against `prep-version-release`'s actual process, which had drifted
  ahead of it — three new steps added (improvement-plan gap check, `[Unreleased]` completeness verification,
  conditional `CONTRIBUTING.md` update), described tool-agnostically
- Recreated `NonFatalExceptionTest`/`FatalExceptionTest`/`ValidationExceptionTest` — these existed as of v7.2.0 but
  were dropped with no replacement, leaving the exception hierarchy at 20% line coverage; added tests for the
  `models/ipsc/shared` scoring groundwork classes (0% coverage) and every `patchCompetitor`/`patchMatch` field's
  previously untested success path. Full-suite coverage rose from 92.9%/93.4% to 98.34%/98.84% (line/branch),
  746 → 775 tests
- Spring Boot parent bumped `4.1.0` → `4.1.1`, dropping the now-redundant `jackson-databind`/`log4j-api`
  `dependencyManagement` overrides; the recurring dependency-currency check then caught a third redundant override,
  `jackson-bom.version`, confirmed against the parent POM directly
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` gain two new gaps (match-scoring
  service/controller layer; Qodana CI wiring — the latter partially progressed by this release's own workflow
  addition), and `CONTRIBUTING.md` gains a new "🗺️ Roadmap" section documenting both files' structure
- Project version bumped to 8.1.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.1.0 (September 1, 2026)

**Theme:** Competitor Bulk CSV Import & Required-Field Enforcement Fixes

**Key Focus:**

- New `IpscCompetitorController.createCompetitors` (`POST /ipsc/competitors/bulk`, consumes `text/csv`) backed by
  `IpscCompetitorService`/`IpscCompetitorServiceImpl`, following `AwardController`/`ImageController`'s bulk-import
  convention — but, unlike those, actually persisting each row via the existing `createCompetitor`
  validation/gender/home-club-resolution logic
- New `CompetitorRequestForCSV`/`CompetitorResponseHolder` models (`models/ipsc/competitor/`)
- Found and fixed a recurring Jackson gotcha: `@JsonProperty(required = true)` is silently inert without a matching
  `@JsonCreator` constructor. `CompetitorRequestForCSV`, `CompetitorRequest`, `MatchRequest`, `MatchStageRequest` and
  the not-yet-wired `MatchOverallScoresRequest`/`MatchStageScoresRequest` (plus their CSV variants) all gained a
  `@JsonCreator` constructor, each parameter bound via `@JsonProperty`, replacing their Lombok `@AllArgsConstructor`
- The two scores CSV variants' constructors now match their plain counterpart's signature exactly (including a
  CSV-absent `matchId`), making them usable as a `csvMapper.addMixIn(...)` mixin, matching
  `AwardServiceImpl`/`ImageServiceImpl` — though neither is wired into a controller yet
- Corrected a genuine mismatch found while fixing the above: `CompetitorRequest`'s Jackson-required third field was
  `competitorNumber`, when `IpscCompetitorServiceImpl.validateForCreate` actually requires `clubNumber`
- `@JsonFormat(pattern = HpscConstants.HPSC_INPUT_DATE_FORMAT)` added to `CompetitorRequest`/`CompetitorRequestForCSV`/
  `MatchRequest`'s `LocalDate` fields, making the accepted `yyyy-MM-dd` format explicit
- New unit tests for every touched request model's JSON/CSV (de)serialization and required-field enforcement, plus
  `IpscCompetitorController`/`Service`/`ServiceImpl` coverage for the new bulk endpoint
- Project version bumped to 8.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.0.0 (August 31, 2026)

**Theme:** IPSC Module Rebuild Complete — Competitor & Match CRUD

**Key Focus:**

- `IpscController`'s empty stub replaced by `IpscCompetitorController`/`IpscMatchController`, full CRUD
  (`create`/`update`/`patch`/`get`, plus `getAllMatches`) backed by new `IpscCompetitorService`/`IpscMatchService` +
  impls — resolves club/gender/firearm-type/match-category by name, and `patchMatch` upserts stages by stage number
  rather than replacing the whole list
- New `Gender` enum capabilities (`name`/`abbreviation` fields, case-insensitive `fromName()`, `toString()`) and new
  `GenderConverter`, wired onto `Competitor.gender`
- New `CompetitorRequest`/`CompetitorResponse` and `MatchResponse`/`MatchStageResponse` DTOs; `models/ipsc/request`
  split into `models/ipsc/match/request`/`models/ipsc/scores/request` to match the module's per-concern shape
- `AwardService`/`ImageService.processCsv` renamed to `createAwards`/`createImages`; bulk CSV endpoints moved to
  `/awards/bulk`/`/images/bulk` and now return `201 Created`; enum `getByX` factory methods renamed to `fromX` across
  all six enums
- Comprehensive Javadoc/`@since` pass across models, converters, exceptions, utils, constants and `ControllerAdvice`
- Qodana JVM static analysis re-added (`qodana.yaml`); the project's AI-agent tooling migrated from
  `.claude/commands/*.md` slash commands to `.claude/skills/*/SKILL.md` Skills
- `AGENTS.md`/`CLAUDE.md` merged into a single tool-agnostic reference; new line-wrapping, extended Arrange-Act-Assert
  and test-helper-placement conventions
- Extensive new test coverage: `IpscCompetitorController`/`Service`/`ServiceImpl` and
  `IpscMatchController`/`Service`/`ServiceImpl` unit + integration tests, `GenderTest`, `GenderConverterTest`
- Project version bumped to 8.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 7.4.1 (August 29, 2026)

**Theme:** Documentation Reflow & Historical Narrative Additions

**Key Focus:**

- Every root-level documentation file (`AGENTS.md`, `ARCHITECTURE.md`, `CLAUDE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`,
  `HISTORY.md`, `README.md`, `RELEASE_NOTES.md`) rewrapped to a consistent ~120-character line width, matching
  CLAUDE.md's existing wrap width — prose, list items and table columns realigned, with a handful of incidental
  copyedits (AGENTS.md's own serial-comma rule example corrected to follow the rule it states) surfacing along the way
- New "Major Version Goals" subsection under this file's Project Philosophy Evolution, summarising the driving goal
  behind each major version line (4.x, 5.x, 6.x, 7.x)
- New "Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)" entry, capturing the test-convention,
  documentation-accuracy and AI-agent-tooling work spanning those three releases
- Project version bumped to 7.4.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 7.4.0 (August 29, 2026)

**Theme:** IPSC Request DTOs, Route Clean-up & Documentation Conventions

**Key Focus:**

- New `za.co.hpsc.web.models.ipsc.request` package — `MatchRequest`/`MatchStageRequest`/`MatchStagesRequest` for
  match/stage submission and `MatchOverallResultRequest`/`MatchStageResultRequest` (plus
  `MatchOverallResultRequestForCSV`/`MatchStageResultRequestForCSV` abstract CSV variants) for competitor result
  submission, shaped to match Practiscore's export format; `MatchRequest` gains a `matchId` field for updating an
  existing match (previously creation-only); all carry field- and class-level Javadoc mirroring the new shared
  `IpscCommonScore`/`IpscMatchScore`/`IpscMatchStageScore` DTOs' Comstock-scoring documentation — groundwork for the
  IPSC module rebuild, not yet wired to any endpoint
- `AwardController`/`ImageController` route prefixes dropped from `/v1/awards`/`/v1/images` to `/awards`/`/images` — the
  unused `/v1` API versioning segment removed
- New AGENTS.md Serial commas rule (no comma before the final `and`/`or` in a list of three or more items) and a
  tightened British English rule that now covers code identifiers as well as prose — both applied retroactively across
  the existing documentation set, which surfaced and corrected two American-spelled test method names
- `documentation/roadmap/`'s `IMPROVEMENT_PLAN.md`/`TASKS.md` renamed to `improvement-plan.md`/
  `improvement-plan-tasks.md` for kebab-case consistency with the rest of the tooling docs
- New Claude Code command `/sync-unreleased-changes`, diffing the branch against its base plus any uncommitted changes
  to fill in missing `CHANGELOG.md` entries automatically; `RELEASE_NOTES.md`'s Contributors section now sourced from
  `git log`'s unique authors rather than a generic placeholder
- Release hygiene: `log4j-api` overridden to `2.25.5` for CVE-2026-49844; `.gitignore`/`.aiignore` refreshed from
  upstream templates; `README.md`'s H1 heading restored
- Project version bumped to 7.4.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 7.3.0 (August 25, 2026)

**Theme:** Documentation Accuracy Pass & PR Summary Tooling

**Key Focus:**

- New Claude Code command `/generate-pr-summary`, which condenses a version's archived `PR_DESCRIPTION_vX.Y.Z.md` and
  `RELEASE_NOTES_vX.Y.Z.md` into a short, plain, Bitbucket-style PR summary — a distillation rather than a restatement
  of this repo's own emoji-heavy documentation style; its Output instructions were subsequently clarified to require
  the raw, unrendered Markdown source in the fenced block
- `README.md`'s Introduction and Features sections corrected to stop describing match management, competitor/club CRUD,
  WinMSS import and XML/multi-format processing as existing capabilities; only `AwardController`/`ImageController` CSV
  processing is implemented today, with the match/competitor domain's service and controller layer still being rebuilt
  (as already noted in `CLAUDE.md`)
- `README.md`'s coverage-report instructions corrected from the non-functional `./mvnw test jacoco:report` to
  `./mvnw verify -Pcoverage`; the stray `1.x – 4.x` version range in its documentation-map description removed, per
  AGENTS.md's evergreen-documentation rule
- `ARCHITECTURE.md`'s test package tree corrected (removed the nonexistent `domain/` test package, added the missing
  `converters/`/`exceptions/` packages) and its CI/CD & Quality Gates table no longer overstates the `Build & Tests`
  gate as an "All PRs" GitHub Actions trigger — only `codeql.yml` runs automatically; `./mvnw test` is run locally/by
  reviewers
- No domain entities, repositories, services or API surface changed in this release — purely a documentation-accuracy
  and tooling pass
- Project version bumped to 7.3.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 7.2.0 (August 25, 2026)

**Theme:** Test Suite Conventions, AI-Agent Tooling and Dependency Maintenance

**Key Focus:**

- New interface-contract unit tests `services/AwardServiceTest`/`services/ImageServiceTest` (Mockito-based, testing
  `createAwards` through the `AwardService`/`ImageService` interface type rather than the impl class); new tests closing 4
  JaCoCo-identified coverage gaps in `ControllerResponseTest`, `FirearmTypeTest` and `ControllerAdviceTest` — overall
  suite coverage rose from 95.7%/91.7% to 97.3%/98.1% (line/branch)
- New Claude Code commands `/scaffold-unit-tests` (migrated from a stale, wrong-project prompt file and corrected to
  this repo's real interface/impl test split) and `/scaffold-integration-tests` (new, `@SpringBootTest`-based, following
  `AwardServiceIntegrationTest`/`ImageServiceIntegrationTest` as the template)
- `HpscWebApplicationTests` renamed to `HpscWebApplicationTest` to match the project's `<ClassName>Test` naming
  convention; 26 existing test files retrofitted with a new AGENTS.md test convention (a one-line `// methodName()`
  header per method group, ordered constructors → public → protected → alphabetical → `toString()` last) — no test
  behaviour changed, purely comments and reordering
- **Dependency maintenance:** Spring Boot parent upgraded `4.0.7` → `4.1.0`, with now-redundant `pom.xml` version
  overrides cleaned up (`spring-framework.version`/`tomcat.version` now match Boot's own defaults; a long-standing
  `commons.lang3.version` typo — Boot's real property is hyphenated — removed; `maven-dependency-plugin` pin removed,
  now Boot-managed) and the flyway-maven-plugin's separately-pinned `flyway-mysql` bumped `11.14.1` → `12.4.0` to match
  Boot's newly-managed `flyway.version`
- Verified via the full test suite (492 tests, up from 483 at the start of this release), `./mvnw verify -Pcoverage` and
  manual Flyway commands (`flyway:info`/`flyway:migrate`) against a real local MySQL 9.5 dev database — no domain
  entities, repositories or API surface changed in this release
- New CLAUDE.md Git Workflow section states the branching model's PR targets directly (`feature/*` → `develop`;
  `release/vX.Y.Z`/`hotfix/*` → `main`); CLAUDE.md now cross-links to AGENTS.md and corrects its package-overview table;
  a false claim that AssertJ is used for assertions (it is explicitly excluded from `pom.xml`) was removed from five
  project docs
- Project version bumped to 7.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 7.1.0 (August 24, 2026)

**Theme:** Shooter Log Refinement — Power Factor Scoping & Match Reference

**Key Focus:**

- `ShooterLogEntry` renamed to `ShooterLogCompetitor` (table `shooter_log_entry` → `shooter_log_competitor`), matching
  this entity's role as a per-competitor snapshot row rather than a generic log entry
- `ShooterLog` gains a non-nullable `powerFactor` (`PowerFactor`, via the existing `PowerFactorConverter`) — snapshots
  are now scoped by power factor as well as firearm type
- `ShooterLogCompetitor` gains a nullable `points` column (the points each contributing `MatchCompetitor` row
  contributed to the snapshot's `logValue`) and a non-nullable `match` (`@ManyToOne IpscMatch`) relation alongside the
  existing `matchCompetitor` link
- `ShooterLogRepository.findAllByCompetitorIdAndFirearmType` renamed to
  `findAllByCompetitorIdAndFirearmTypeAndPowerFactor`, now filtering by `PowerFactor` as well
- New `ShooterLogCompetitorRepository` (`findAllByShooterLogId`) supersedes `ShooterLogEntryRepository`; new Flyway
  migration `V7_1_0__update_shooter_log_schema.sql` renames the table, its unique-index and FK constraints, and adds the
  new columns — both tables remain empty in every environment (no calculation service populates them yet), so no
  backfill was required
- Project version bumped to 7.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`
- Alongside the schema work, this release migrates the repository's AI-agent prompt files from
  `.github/prompts/*.prompt.md` to `.claude/commands/*.md`, adopts GitFlow branching in `AGENTS.md` and adds
  `CONTRIBUTING.md`

### Version 7.0.0 (August 11, 2026)

**Theme:** Match Results, Visitor Tracking & Shooter Log Data Model

**Key Focus:**

- Six entities parked under `domain/old/` (`Club`, `Competitor`, `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`,
  `MatchStageCompetitor`) promoted back into `za.co.hpsc.web.domain`; the `.old` package removed entirely
- `Club` gains a unique `identifier` (`ClubIdentifier`, via `ClubIdentifierConverter`) tying a club row to
  HPSC/SOSC/PMPSC — visitors are derived relationally, not as a fourth club row
- `Competitor` gains a nullable `homeClub` (`@ManyToOne Club`) relation for home-club membership
- `MatchCompetitor.matchRanking` renamed to `overallRanking`; new `clubRanking` (same-club rank per firearm type) and
  `isVisitor` (`true` when `matchClub` differs from the host match's club); new unique constraint
  `(competitor_id, match_id, firearm_type)`
- `MatchStageCompetitor` FK changed from `competitor` to `matchCompetitor`, removing duplicated `competitorCategory`/
  `division`/`firearmType`/`powerFactor`/`matchClub` fields; new unique constraint
  `(match_competitor_id, match_stage_id)`
- `IpscMatchStage` gains a new unique constraint `(match_id, stage_number)`
- New `ShooterLog` entity (competitor, club, firearmType, `logValue`, `calculatedDate`) and `ShooterLogEntry` entity
  (`rankInLog`, unique constraint `(shooter_log_id, match_competitor_id)`) persist best-4-match shooter-log snapshots
- `repositories/` package (previously emptied in preparation for this rework) rebuilt from scratch with 8 new
  `JpaRepository` interfaces
- No new enums or converters — `ClubIdentifier` and `FirearmType` (with existing `AttributeConverter`s) are reused
- Project version bumped to 7.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`
- Verified via `./mvnw clean compile` and `HpscWebApplicationTests` (H2 schema build for all 8 entities); no dedicated
  new unit/integration tests added for the new/changed domain model in this release
- Statistics: 1 commit, 15 files changed, +207 insertions, -30 deletions

### Version 6.0.0 (May 1, 2026)

**Theme:** Dedicated Match CRUD API, Service Encapsulation & Package Restructuring

**Key Focus:**

- `IpscMatchController` introduced at `/v2/ipsc/matches` with full CRUD (POST, PUT, PATCH, GET)
- `IpscMatchService` + `IpscMatchServiceImpl` added as dedicated match management service layer
- `MatchOnlyDto`, `MatchOnlyRequest`, `MatchOnlyResponse`, `MatchOnlyResultsDto` introduced for match-only operations
  without stages
- `DomainServiceImpl` decoupled from JPA repositories — now delegates to entity services exclusively
- New entity service methods: `findClubById`, `findCompetitorById`, `findMatchStageCompetitorById`
- `IpscUtil` utility class added for club and match display-string formatting
- All IPSC models moved from `models/ipsc/` to `models/ipsc/common/`; new `models/ipsc/match/` sub-package
- Three new match search request models: `MatchSearchRequest`, `MatchSearchDateRequest`, `MatchSearchIdRequest`
- `IpscMemberController` stub registered at `/ipsc/member`
- `TransformationService.mapMatchOnly(MatchOnlyRequest)` added; `mapMatchResults` no longer throws `ValidationException`
- `ControllerAdvice` enhanced with structured logging across all exception handlers
- Spring Boot upgraded from 4.0.5 to 4.0.6; MIT licence and SCM metadata added to `pom.xml`
- 8 new test classes (~1,300 lines): `IpscMatchControllerTest`, `IpscMatchServiceTest`, `IpscMatchIntegrationTest`,
  `MatchOnlyDtoTest`, `MatchOnlyRequestTest`, `MatchOnlyResponseTest`, `MatchResponseTest`, `IpscUtilTest`;
  `IpscControllerTest` removed
- Statistics: 40 commits, 165 files changed, +6,779 insertions, -3,501 deletions

### Version 5.4.0 (April 26, 2026)

**Theme:** Competitor Enrolment, Service Transformation & Comprehensive Test Expansion

**Key Focus:**

- `EnrolledCompetitorDto` introduced (138 lines) for tracking enrolled competitors through the IPSC pipeline
- `IpscMatchService` renamed to `TransformationService`; `TransformationServiceImpl` introduced (1,098 lines)
- `ClubIdentifier` enhanced with abbreviation field; `ClubIdentifierConverter` updated for persistence
- Competitor SAPSA number validation and duplicate filtering added to `CompetitorDto`
- Package restructure: `ipsc/domain` → `ipsc/data`; records and holders reorganised
- 20+ new test classes added (~7,000 lines): controllers, converters, domain entities, exceptions, integration
- `IpscMatchServiceTest` removed (10,076 lines); `TransformationServiceTest` introduced (1,026 lines)
- Qodana JVM linter and JaCoCo 0.8.14 code coverage added to the CI/CD pipeline
- Bug fixes: PCC Optics division code, ControllerAdvice error handling, ClubIdentifier abbreviation
- Statistics: ~75 commits, 123 files changed, +12,713 insertions, -13,358 deletions

### Version 5.3.0 (March 15, 2026)

**Theme:** Service Consolidation, Custom JPA Converters & Repository Optimisation

**Key Focus:**

- Six new custom JPA attribute converters replacing `@Enumerated(EnumType.STRING)` for all enum types
- Complete removal of `IpscMatchResultService` and `ScoreDto`; functionality consolidated into `DomainService` and
  `IpscMatchService`
- `DtoMapping` transitioned from class to Java record construct for immutability
- Added `mappedBy` to all bidirectional `@OneToMany` entity relationships; fixed cascade types
- Repository query optimisation: scheduled date in match queries, `Set` for competitor deduplication, removed
  unnecessary fetch joins
- Major test updates: DomainServiceTest (+787 lines), IpscMatchServiceTest (3,156 lines changed), TransactionServiceTest
  (1,031 lines changed), IpscMatchResultServiceTest removed (1,802 lines), ScoreDtoTest removed (643 lines)
- Spring Boot upgraded from 4.0.3 to 4.1.0-SNAPSHOT
- Statistics: ~45 commits, 59 files changed, +5,686 insertions, -4,613 deletions

### Version 5.2.0 (February 27, 2026)

**Theme:** Match Results Processing Enhancement & Architecture Refactoring

**Key Focus:**

- New three-tier mapping system (DtoMapping, EntityMapping, DtoToEntityMapping)
- Enhanced match entity handling with a dedicated MatchEntityService
- Comprehensive test coverage: 716 lines of DtoToEntityMapping tests, 2,000+ lines of TransactionService tests
- Consolidated test suites across all services and utilities
- Enhanced null safety with array initialisation
- Major service refactoring: IpscMatchServiceImpl (246 lines), IpscMatchResultServiceImpl (333 lines),
  TransactionServiceImpl (198 lines)
- Statistics: 26 commits, 61 files changed, +13,567 insertions, -5,898 deletions

### Version 5.1.0 (February 25, 2026)

**Theme:** Test Suite Enhancement & Code Quality Consolidation

**Key Focus:**

- Comprehensive test reorganisation with 6 logical sections
- Elimination of duplicate test cases
- Enhanced test readability and maintainability
- Build stability: 23 passing tests, 0 failures, 1 skipped

### Version 5.0.0 (February 24, 2026)

**Theme:** Semantic Versioning Transition & Infrastructure Consolidation

### Version 4.1.0 (February 13, 2026)

**Theme:** CRUD Enhancement & API Maturity

### Version 4.0.0 (February 11, 2026)

**Theme:** Domain Refactoring & Quality Assurance

### Version 3.1.0 (February 10, 2026)

**Theme:** Exception Handling Consolidation

### Version 3.0.0 (February 10, 2026)

**Theme:** Domain Model Restructuring & IPSC Specialisation

### Version 2.0.0 (February 8, 2026)

**Theme:** Service-Oriented Architecture & Modularity

### Version 1.1.3 (January 28, 2026)

**Theme:** Documentation Enhancement & Mapper Centralisation

### Version 1.1.2 (January 20, 2026)

**Theme:** Project Documentation

### Version 1.1.1 (January 16, 2026)

**Theme:** API Clarity & Javadoc Standardisation

### Version 1.1.0 (January 14, 2026)

**Theme:** Award Processing & Core Model Refactoring

### Version 1.0.0 (January 4, 2026)

**Theme:** Foundation & Image Gallery

---

## 📖 Evolution Overview

The full Phase-by-phase narrative for every release has moved to
[`documentation/history/EVOLUTION_OVERVIEW.md`](/documentation/history/EVOLUTION_OVERVIEW.md) to keep this file a
manageable size, since that narrative alone had grown to roughly half of it. See the
[📅 Historical Timeline](#-historical-timeline) above for the same releases summarised more concisely, and
[🎯 Major Milestones](#-major-milestones) below for each release's headline achievement.

---

## 🎯 Major Milestones

### Milestone 1: Project Foundation (v1.0.0)

- Initial Spring Boot application
- Image gallery CSV processing
- Basic API infrastructure
- Custom exception hierarchy

**Achievement:** Established the foundation for the HPSC platform with core image processing capabilities.

---

### Milestone 2: Feature Expansion (v1.1.0 - v1.1.3)

- Award processing system
- OpenAPI documentation
- Comprehensive project documentation
- Code quality standards

**Achievement:** Expanded platform features and established professional documentation standards.

---

### Milestone 3: Architectural modernisation (v2.0.0)

- Service-oriented architecture
- Comprehensive DTO layer
- Modular service design
- Transaction management

**Achievement:** Transformed from monolithic to modular architecture enabling better maintainability and testing.

---

### Milestone 4: Domain Specialisation (v3.0.0)

- IPSC-specific domain modelling
- Firearm-type classification
- Club entity reintroduction
- Comprehensive enum utilities

**Achievement:** Aligned domain model with IPSC standards for specialised shooting competition management.

---

### Milestone 5: Quality & Simplification (v3.1.0)

- Exception handling consolidation
- API documentation accuracy
- Error handling consistency
- Simplified architecture

**Achievement:** Improved code quality and simplified error handling while maintaining functionality.

---

### Milestone 6: Domain Clarity (v4.0.0)

- Entity naming clarification
- Comprehensive test coverage
- Enhanced validation layers
- IPSC entity specialisation

**Achievement:** Clarified domain model through explicit entity naming (Match → IpscMatch) improving code clarity.

---

### Milestone 7: Feature Completeness (v4.1.0)

- Full CRUD operations
- Complete API maturity
- Transactional consistency
- Production readiness

**Achievement:** Completed CRUD lifecycle enabling full data management capabilities.

---

### Milestone 8: Standards Adoption (v5.0.0)

- Semantic versioning adoption
- Entity initialisation framework
- Response generation pipeline
- Infrastructure consolidation

**Achievement:** Adopted industry standards and consolidated infrastructure for long-term maintainability.

---

### Milestone 9: Test Quality Enhancement (v5.1.0)

- Test suite reorganisation with 6 logical sections
- Duplicate test elimination
- Standardised test naming conventions
- Enhanced test documentation and readability

**Achievement:** Improved test infrastructure quality through comprehensive reorganisation and consolidation.

---

### Milestone 10: Architecture Refactoring (v5.2.0)

- Three-tier mapping system (DtoMapping, EntityMapping, DtoToEntityMapping)
- Enhanced match entity handling with MatchEntityService
- Comprehensive test consolidation (2,000+ lines across multiple suites)
- Enhanced null safety and code quality
- Major service refactoring (61 files, +13,567 lines)

**Achievement:** Significant architectural improvement with cleaner separation of concerns, enhanced null safety and
comprehensive test coverage across all services and utilities.

---

### Milestone 11: Service Consolidation & Type Safety (v5.3.0)

- Six custom JPA attribute converters for type-safe enum persistence
- IpscMatchResultService and ScoreDto removed; functionality consolidated
- DtoMapping converted to Java record for immutability
- All bidirectional @OneToMany relationships corrected with mappedBy
- Repository queries optimised with Set deduplication and scheduled date constraints

**Achievement:** Focused service consolidation and type-safety improvements simplifying the service architecture,
correcting JPA entity relationships and improving repository query accuracy.

---

### Milestone 12: Competitor Enrolment & Service Transformation (v5.4.0)

- `EnrolledCompetitorDto` introduced for enrolled competitor tracking through the IPSC pipeline
- `IpscMatchService` renamed to `TransformationService` for semantic clarity
- SAPSA number validation and competitor deduplication in `CompetitorDto`
- 20+ new test classes (~7,000 lines) — the largest single-release test expansion in project history
- Qodana JVM linting and JaCoCo code coverage integrated into the CI/CD pipeline

**Achievement:** Delivered the project's most comprehensive test suite expansion, introduced competitor enrolment
support and SAPSA validation, modernised the service naming for improved clarity and strengthened the CI/CD pipeline
with static analysis and code coverage quality gates.

---

### Milestone 13: Dedicated Match CRUD API & Service Encapsulation (v6.0.0)

- `IpscMatchController` introduced at `/v2/ipsc/matches` with full CRUD (POST, PUT, PATCH, GET)
- `IpscMatchService` + `IpscMatchServiceImpl` added as dedicated match management service
- `DomainServiceImpl` fully decoupled from repositories — entity services used exclusively
- All IPSC models moved to `models/ipsc/common/`; `models/ipsc/match/` sub-package introduced
- `IpscUtil` added for centralised club/match display-string formatting
- Spring Boot upgraded 4.0.5 → 4.0.6; MIT licence and SCM metadata populated in `pom.xml`

**Achievement:** Established a versioned, resource-oriented match management API and completed the entity service
encapsulation layer, ensuring `DomainServiceImpl` respects the layered architecture described in CLAUDE.md. The IPSC
model package restructuring provides dedicated homes for shared and match-specific models as the domain grows.

---

### Milestone 14: Match Results, Visitor Tracking & Shooter Log Data Model (v7.0.0)

- Six entities promoted from `domain/old/` back into the live `domain` package; `.old` package removed
- `Club.identifier`, `Competitor.homeClub`, `MatchCompetitor.clubRanking`/`isVisitor` support club-scoped results and
  relational visitor tracking
- `MatchStageCompetitor` repointed from `Competitor` to `MatchCompetitor` to support multiple firearm-type entries per
  competitor per match
- New `ShooterLog`/`ShooterLogEntry` entities persist best-4-match shooter-log snapshots
- `repositories/` package rebuilt from scratch with 8 new `JpaRepository` interfaces

**Achievement:** Extended the IPSC domain model with club-scoped results, visitor tracking and a persisted shooter-log
data model, restoring the six entities parked under `domain/old/` and pairing them with a complete repository layer —
domain-layer groundwork ahead of the service/controller/import-pipeline wiring still to come.

---

### Milestone 15: Shooter Log Refinement (v7.1.0)

- `ShooterLogEntry` renamed to `ShooterLogCompetitor` for naming accuracy
- `ShooterLog.powerFactor` scopes best-4-match snapshots by power factor as well as firearm type
- `ShooterLogCompetitor` gains `points` and a direct `match` reference alongside `matchCompetitor`
- `ShooterLogCompetitorRepository` supersedes `ShooterLogEntryRepository`; `ShooterLogRepository` finder renamed to
  include `PowerFactor`
- Repository AI-agent tooling migrated to `.claude/commands/*.md`; `AGENTS.md` adopts GitFlow; `CONTRIBUTING.md` added

**Achievement:** Corrected the naming and scope of the v7.0.0 shooter-log data model before any calculation service is
built against it, keeping the schema accurate ahead of the service/controller wiring still to come.

---

### Milestone 16: Test Suite Conventions, AI-Agent Tooling and Dependency Maintenance (v7.2.0)

- 26 test files retrofitted with a new `// methodName()` header-comment/ordering convention; 4 JaCoCo-identified
  coverage gaps closed; suite coverage rose from 95.7%/91.7% to 97.3%/98.1% (line/branch)
- New `/scaffold-unit-tests` (corrected from a stale, wrong-project prompt) and `/scaffold-integration-tests` Claude
  Code commands
- Spring Boot parent upgraded `4.0.7` → `4.1.0`; redundant `pom.xml` version overrides cleaned up; `flyway-mysql` bumped
  to match Boot's newly managed Flyway version
- Verified via the full test suite (492 tests), `./mvnw verify -Pcoverage` and manual Flyway commands against a real
  MySQL dev database

**Achievement:** Brought the entire existing test suite into line with a newly formalised AGENTS.md convention, closed
every coverage gap JaCoCo could find and corrected/extended the AI-agent tooling — all without touching the domain
model, keeping the codebase consistent ahead of future feature work.

---

### Milestone 17: IPSC Request DTOs, Route Clean-up & Documentation Conventions (v7.4.0)

- New `models/ipsc/request`/`models/ipsc/shared` packages — `MatchRequest`/`MatchStageRequest`/`MatchStagesRequest`/
  `MatchOverallResultRequest`/`MatchStageResultRequest` (plus CSV variants) and `IpscCommonScore`/`IpscMatchScore`/
  `IpscMatchStageScore`, groundwork for the IPSC module rebuild
- `AwardController`/`ImageController` routes simplified from `/v1/awards`/`/v1/images` to `/awards`/`/images`
- New AGENTS.md Serial commas rule and a British English rule tightened to cover code identifiers, both applied across
  the existing documentation set; `documentation/roadmap/` renamed to kebab-case
- New `/sync-unreleased-changes` Claude Code command; `RELEASE_NOTES.md` Contributors now sourced from `git log`

**Achievement:** Laid IPSC request-DTO groundwork for the module rebuild while cleaning up a stale API route and
tightening the project's own documentation conventions — no domain/service/architecture changes.

---

### Milestone 18: Documentation Reflow & Historical Narrative Additions (v7.4.1)

- Entire root-level documentation set rewrapped to a consistent ~120-character line width
- New "Major Version Goals" and "Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)" narrative sections added
  to `HISTORY.md`

**Achievement:** Brought every root documentation file to a consistent line width and filled in two gaps in the
project's own historical narrative — no domain/service/architecture or test changes.

---

### Milestone 19: IPSC Module Completion (v8.0.0)

- `IpscCompetitorController`/`IpscMatchController` full CRUD, replacing the long-standing empty `IpscController` stub
- New `IpscCompetitorService`/`IpscMatchService` + impls; new `Gender` enum capabilities and `GenderConverter`
- Comprehensive Javadoc/`@since` documentation pass; AI-agent tooling migrated from slash commands to Skills
- Largest test expansion since v5.4.0: full unit and integration coverage for both new controllers/services

**Achievement:** Completed the IPSC module rebuild begun as groundwork in v6.0.0 — the platform now has real,
resource-oriented competitor and match management, not just an empty stub.

---

### Milestone 20: Competitor Bulk CSV Import & Required-Field Fixes (v8.1.0)

- `IpscCompetitorController.createCompetitors` (`POST /ipsc/competitors/bulk`) persists competitors from CSV data,
  unlike `AwardController`/`ImageController`'s response-only bulk endpoints
- Discovered and fixed a Jackson gotcha affecting every IPSC request model to date: `@JsonProperty(required = true)`
  needs a matching `@JsonCreator` constructor to actually enforce anything
- `CompetitorRequest`, `CompetitorRequestForCSV`, `MatchRequest`, `MatchStageRequest` and the scores request models
  all gained a `@JsonCreator` constructor; `CompetitorRequest`'s required field corrected from `competitorNumber` to
  `clubNumber`
- New unit tests across the bulk-import feature and every touched request model

**Achievement:** Extended the IPSC competitor module with bulk CSV import, and — while testing it — found and fixed a
silent validation gap present across every `@JsonProperty(required = true)` field added since the required-field
pattern was first introduced.

---

### Milestone 21: CI Static Analysis, Release-Process Self-Maintenance & Coverage Regression Fixes (v8.1.1)

- New `.github/workflows/qodana.yml` completes a CI quality gate that had sat configured but unwired since v8.0.0
- New `update-improvement-plan-gaps`/`sync-improvement-plan-gaps` skills formalise roadmap-gap maintenance;
  `generate-pr-description` renamed to `prep-version-release` and now runs both as its first step
- Recreated a deleted exception-hierarchy test suite and closed several other coverage gaps, taking the suite from
  92.9%/93.4% to 98.34%/98.84% (line/branch)
- Spring Boot bumped `4.1.0` → `4.1.1`; three now-redundant dependency overrides dropped, one found by the
  recurring dependency-currency check itself

**Achievement:** Closed a real, silent test-coverage regression, completed a CI quality gate the project's own
documentation had flagged as configured-but-unwired since v8.0.0 and built the tooling for the release process to
keep auditing its own roadmap documentation going forward — no new domain feature, but meaningful process maturity.

---

### Milestone 22: Competitor Multi-Email Support & Bulk CSV Separator Standardisation (v8.2.0)

- `Competitor.emailAddress` (`String`) replaced with `emailAddresses` (`List<String>`), backed by a new
  `competitor_email` child table and a backfilling Flyway migration
- New `SystemConstants.ARRAY_SEPARATOR` unifies `AwardServiceImpl`/`ImageServiceImpl`'s bulk CSV parsing with the
  competitor domain's semicolon-separated multi-value convention
- Qodana static analysis removed after a roadmap audit found it had been failing on every CI run since v8.1.1;
  `improvement-plan.md`'s Gap #7 closed as not applicable
- New genuinely multiple-address tests surfaced a real bug in `IpscCompetitorServiceImpl.applyFields`/
  `patchCompetitor` — an immutable `emailAddresses` list crashed with an unhandled `UnsupportedOperationException`
  on update; both methods now defensively copy into a new `ArrayList`

**Achievement:** Extended the competitor domain to support more than one email address, closed a lingering
inconsistency between the competitor and award/image bulk CSV endpoints' multi-value cell formats, removed a
CI quality gate that had never once succeeded and fixed a real crash-on-update bug found by writing genuinely
thorough multi-address test coverage instead of only single-element cases.

---

### Milestone 23: Match Bulk CSV Import (v8.3.0)

- `IpscMatchController.createMatches` (`POST /ipsc/matches/bulk`) persists matches, together with their stages,
  from CSV data, mirroring `IpscCompetitorController.createCompetitors`'s v8.1.0 bulk-import shape
- New `MatchRequestForCSV`/`MatchResponseHolder` models; a discarded `numberOfStages` count-field attempt was
  replaced with a single semicolon-separated `stages` cell of `<stageNumber>-<stageName>` entries before either
  design reached `develop`
- `improvement-plan.md`'s Gap #8 (match bulk CSV import stated as removed pending a rebuild) closed
- New unit tests across the bulk-import feature and `MatchRequestForCSV`'s JSON/CSV (de)serialisation

**Achievement:** Brought the match domain to parity with the competitor domain's bulk CSV import, closing the last
of the two IPSC entities' bulk-import gaps that `ARCHITECTURE.md` had documented as outstanding since v8.1.0.

---

### Milestone 24: CI Build/Test Gate, Coverage Enforcement & CSV Persistence Clarity (v8.3.1)

- New `.github/workflows/build.yml` runs `./mvnw verify -Pcoverage` on push/PR to `main`/`develop`, closing
  `improvement-plan.md`'s Gap #2
- New JaCoCo `check` execution enforces a `LINE`/`COVEREDRATIO` minimum — 51% initially, then 86% within the same
  branch — wired into the CI gate, still below the real baseline of 98.16%/98.94% (line/branch), 836 tests
- `AwardService.createAwards()`/`ImageService.createImages()` confirmed intentionally stateless by design, closing
  Gap #3
- `ARCHITECTURE.md`/`CONTRIBUTING.md`'s CI/CD & Quality Gates tables updated to match

**Achievement:** Completed the CI build/test gate that had been running only "locally / by reviewers", added the
project's first coverage-regression rule — tightened from 51% to 86% against a freshly measured real baseline of
98.16%/98.94%, with further tightening left as a follow-up — and resolved a standing design-intent ambiguity around
Award/Image CSV persistence.

---

### Milestone 25: Club Domain Defaults, Optional Club Numbers & Documentation Convention Hardening (v8.4.0)

- New `ClubIdentifier.ALL` seeded via `V7_3_0__seed_club_data.sql`; `IpscMatchServiceImpl.resolveClub()` defaults a
  missing/blank match `club` to it instead of failing validation, closing Gap #9
- `IpscCompetitorServiceImpl.resolveClubNumber()` requires `clubNumber` only for HPSC-home-club competitors; column
  relaxed to nullable via `V7_4_0__make_club_number_nullable.sql`
- JaCoCo `LINE`/`COVEREDRATIO` floor tightened `0.86` → `0.97`, confirmed holding at a fresh 98.44%/98.98%
  (line/branch, 868 tests) baseline, closing Gap #4
- `HpscConstants` removed; `AGENTS.md` gained Member ordering and REST naming conventions plus a Project-Structure-
  tree release-checklist backstop; `tomcat-embed-*` patched to 11.0.25 for three critical CVEs

**Achievement:** Extended the domain-default pattern from competitors to matches, completed the coverage-regression
floor's climb to its real baseline and hardened the project's own documentation/convention discipline.

---

### Milestone 26: Documentation Cross-Reference Consolidation & Icon Registry Sync (v8.4.1)

- `AGENTS.md`/`CONTRIBUTING.md`'s duplicated content condensed into highlights-and-link references (Git Workflow,
  Exception handling, CI/CD & Quality Gates); new "🧩 Claude Code Skills"/"🗺️ Roadmap Planning" sections
- Icon registry backfilled with 25 previously-unregistered icons plus a new "Reserved" sub-table for
  `hpsc-web-vite`'s icons, synced twice this release; several icon collisions resolved across root and archived docs
- `CHANGELOG.md`'s duplicate `[5.0.0]` section removed; a broken Serial Commas example corrected

**Achievement:** Reduced duplicated source-of-truth content across `AGENTS.md`/`CONTRIBUTING.md`, completed the icon
registry against real usage across both sibling repositories and cleared two latent documentation defects — no
domain/service/architecture or test changes.

---

### Milestone 27: Root Document Title Standardisation & Source-of-Truth Clarification (v8.4.2)

- `AGENTS.md` now explicitly states it is the project's ultimate source of truth for conventions, with
  `CONTRIBUTING.md` pointing back to it on any contradiction
- `CHANGELOG.md`/`CONTRIBUTING.md`/`HISTORY.md` titles standardised to the "HPSC Website Backend" project name,
  with `CHANGELOG.md`'s headings demoted one level to nest under its new title

**Achievement:** Established a single, explicit source of truth for documentation conventions and brought every
root document's title in line with the project's actual name — no domain/service/architecture or test changes.

---

### Milestone 28: Match Start/End Time Tracking (v8.5.0)

- `IpscMatch` gains nullable `startTime`/`endTime` columns via `V7_5_0__add_ipsc_match_start_end_time.sql`, wired
  through `MatchRequest`/`MatchRequestForCSV`/`MatchResponse` and `IpscMatchServiceImpl`
- CSV bulk import's header validation now requires the two new columns; `README.md`/`CONTRIBUTING.md`/`AGENTS.md`
  gained British English corrections around "Licence"

**Achievement:** Closed a real gap in match-timing data, extending the match domain's request/response/service
wiring pattern to two new nullable timestamp fields.

---

### Milestone 29: HISTORY.md Phase/Milestone Backfill & Release Re-Scoping to a Patch Version (v8.5.1)

- `HISTORY.md` backfilled with Phase 26/27/28 and Milestone 26/27/28 for v8.4.1/v8.4.2/v8.5.0, closing
  `improvement-plan.md`'s Gap #10; "Major Version Goals" extended through v8.5.1
- Re-scoped from a planned `v8.6.0` minor version to `v8.5.1` patch after confirming the full diff against `main`
  is documentation/tooling-only, matching the v8.4.1/v8.4.2 precedent
- Evolution Overview, Major Milestones, Architectural Evolution and Future Roadmap Implications reordered to
  ascending (oldest-first), matching Feature Timeline/Project Philosophy Evolution's existing convention; stale
  legacy Conclusion-section metadata and update log removed
- `prep-version-release`/`generate-pr-summary` skills gained a standard attribution footer on their drafted output

**Achievement:** Closed a three-release documentation backlog, brought this file's own sections into consistent
chronological order and corrected this release's own Semantic Versioning classification before it shipped — no
domain/service/architecture or test changes.

---

### Milestone 30: Match URL Field, Start/End Time Precision Fix & Test Architecture Formalisation (v8.6.0)

- `IpscMatch` gains a nullable `url` column via `V7_6_0__add_ipsc_match_url.sql`, wired through `MatchRequest`/
  `MatchRequestForCSV`/`MatchResponse` and `IpscMatchServiceImpl`, with a matching CSV bulk import `Url` column
- `startTime`/`endTime` corrected from `LocalDateTime` to `LocalTime` via
  `V7_7_0__change_ipsc_match_start_end_time_to_time.sql`, with a new `IpscConstants.IPSC_INPUT_TIME_FORMAT`
  (`HH:mm`) constant replacing the date-carrying format previously used
- `AGENTS.md`'s Test Conventions section formally documents the 3-tier service test architecture already followed
  by all four services

**Achievement:** Extended the match domain with a new metadata field and corrected a data-type mismatch introduced
in v8.5.0, while formalising an already-established test convention in the project's own documentation.

---

### Milestone 31: documentation/history/ Reorganisation & Evolution Overview Split (v8.6.1)

- `HISTORY.md`'s "📖 Evolution Overview" section split out into new `documentation/history/EVOLUTION_OVERVIEW.md`,
  roughly halving `HISTORY.md`'s size; `HISTORY.md` keeps a short pointer under the same heading/anchor
- All 52 archived `RELEASE_NOTES_vX.Y.Z.md`/`PR_DESCRIPTION_vX.Y.Z.md` files regrouped into `v1/` – `v8/`
  subdirectories by major version via `git mv`; `AGENTS.md`, `README.md` and the three release-prep skills updated
  to match

**Achievement:** Kept the project's fastest-growing documentation files navigable as the release history keeps
accumulating, without losing any historical content or `git` history.

---

### Milestone 32: CHANGELOG.md Heading-Depth Correction, Future Roadmap Refresh & Icon Registry Sync (v8.6.2)

- `AGENTS.md`, `CONTRIBUTING.md` and five Claude Code skills corrected to describe `CHANGELOG.md`'s actual
  `### 🧪 [Unreleased]` → `#### <category>` → `##### <Area>` heading depth, one level deeper than previously stated
- `AGENTS.md`'s Git Workflow Conventions extended to spell out Area reuse and the bold-lead-in bullet style,
  reverse-synced from the shared project template
- This file's Future Roadmap Short-term/Medium-term lists refreshed to name only genuinely outstanding work under
  current entity names and version labels, closing the newly recorded Gap #11
- `AGENTS.md`'s heading-icon registry restructured to mirror the shared project template's, with every live
  heading realigned to it

**Achievement:** Brought the project's written `CHANGELOG.md` conventions back in line with the file itself, so any
agent or contributor following `AGENTS.md` or the skills produces correctly nested entries, and cleared already
delivered work out of this file's forward-looking roadmap, while keeping heading icons consistent with sibling
projects built from the same template.

---

### Milestone 33: Competitor Listing Endpoint, Stage Delimiter Change & Dependency Clean-up (v8.7.0)

- New `GET /ipsc/competitors` endpoint returns every competitor, giving the competitor domain the same collection
  read that `IpscMatchController.getAllMatches` already gave matches
- Match CSV stage entries now use `<stageNumber>:<stageName>`, replacing the `-` delimiter
- Domain `@ManyToOne` associations switched to eager fetching; the app moves to Spring Boot's default port `8080`
- springdoc upgraded to its Spring Boot 4 line (`3.1.0`) via an imported BOM, and the unused Spring REST Docs
  dependency removed

**Achievement:** Closed the competitor domain's missing collection endpoint and tidied the build's documentation
dependencies onto the Spring Boot 4-compatible springdoc line, while recording the next API gap (no delete
operation) for a later release.

---

### Milestone 34: Competitor & Match Delete Endpoints (v8.8.0)

- New `DELETE /ipsc/competitors/{competitorId}` and `DELETE /ipsc/matches/{matchId}` endpoints complete the
  competitor and match domains' create, read, update and delete set
- Records still referenced by match results, stage results or shooter logs are refused with a `400` rather than
  cascaded, while a competitor's emails and a match's stages are deleted with them
- Gap #12 closed; new Gap #13 recorded for the undocumented Claude Code GitHub workflows

**Achievement:** Made the long-standing "full CRUD" description of the competitor and match APIs true, with a
deletion rule that protects scoring history rather than silently destroying it.

---

## 🏛️ Architectural Evolution

### v1.0.0: Monolithic Foundation

```
Controller → Service → Repository → Entity
         ↓
      Models
         ↓
   Exception Handlers
```

**Characteristics:**

- Single service for image processing
- Direct controller-service-repository flow
- Basic entity relationships
- Centralised exception handling

---

### v2.0.0: Modular Services

```
           Controller
              ↓
    ┌─────────┴──────────┐
    ↓                    ↓
WinMssService    MatchResultService
    ↓                    ↓
 Repository     TransactionService
    ↓                    ↓
 Entity        DomainServices
    ↓                    ↓
   DTOs          Models/DTOs
```

**Characteristics:**

- Specialised services for different domains
- DTO layer for data transfer
- Transaction abstraction
- Improved separation of concerns

---

### v3.0.0: Domain-Specific Models

```
       IPSC Controller
            ↓
    ┌───────┴────────┐
    ↓                ↓
IpscService    DomainService
    ↓                ↓
 Firearm      Club    Match    Stage
 Types        ↓        ↓        ↓
 (Enums)    Entity  Entity   Entity
    ↓         ↓        ↓        ↓
Repository  Repository
```

**Characteristics:**

- IPSC-specific domain modelling
- Firearm-type classification
- Club entity relationship
- Specialised enums for IPSC

---

### v4.0.0: Explicit IPSC Focus

```
       IpscController
            ↓
    ┌───────┴────────┐
    ↓                ↓
IpscMatchService  DomainService
    ↓                ↓
 IpscMatch*    IpscMatch*Stage
 Repository    Repository
    ↓                ↓
 IpscMatch*    IpscMatch*Stage
   Entity         Entity
```

**Characteristics:**

- Explicit IPSC entity naming
- Comprehensive validation layers
- Enhanced test coverage
- Clear domain boundaries

---

### v5.0.0: Consolidated Framework

```
       IpscController
            ↓
  ┌─────────┼─────────┐
  ↓         ↓         ↓
Service   Domain    IPSC
Layer     Layer    Services
  ↓         ↓         ↓
Entity    Entity    Records
Layer   Initialisation
  ↓      Framework
Repository Layer
```

**Characteristics:**

- Entity initialisation framework
- Response generation pipeline
- Consolidated infrastructure
- Industry-standard versioning

---

### v5.2.0: Three-Tier Mapping Architecture

```
       IpscController
            ↓
  ┌─────────┼─────────┐
  ↓         ↓         ↓
Service   Match     IPSC
Layer    Entity   Services
  ↓      Service     ↓
  ↓         ↓    DtoMapping
  ↓         ↓         ↓
  ↓    DtoToEntity   ↓
  ↓     Mapping      ↓
  ↓         ↓        ↓
  ↓   EntityMapping  ↓
  ↓         ↓        ↓
Repository Layer
  ↓
Entity Layer
```

**Characteristics:**

- Three-tier mapping system (DTO → Bridge → Entity)
- Dedicated MatchEntityService
- Enhanced null safety with Optional
- Comprehensive test consolidation
- Cleaner separation of concerns

---

### v5.3.0: Consolidated Service Architecture

```
       IpscController
            ↓
  ┌─────────┼─────────┐
  ↓         ↓         ↓
Service   Domain    IPSC
Layer     Service   Match
  ↓       (init)    Service
  ↓         ↓    (processing)
  ↓    DtoToEntity   ↓
  ↓     Mapping      ↓
  ↓    (record)      ↓
  ↓         ↓        ↓
Repository Layer
  ↓  (Set-based, scheduled date)
Entity Layer
  ↓
AttributeConverters
(ClubIdentifier, CompetitorCategory,
 Division, FirearmType,
 MatchCategory, PowerFactor)
```

**Characteristics:**

- Consolidated service boundaries (IpscMatchResultService removed)
- Custom JPA AttributeConverters for type-safe enum persistence
- DtoMapping as immutable Java record
- Correct bidirectional `@OneToMany` relationships with `mappedBy`
- Optimised repository queries

---

### v5.4.0: Transformation Service Architecture

```
       IpscController
            ↓
  ┌─────────┼─────────┐
  ↓         ↓         ↓
Service   Domain    Transformation
Layer     Service    Service
  ↓       (init)   (processing)
  ↓         ↓    MatchHolder ↓
  ↓    DtoToEntity   ↓
  ↓     Mapping      ↓
  ↓    (data pkg)    ↓
  ↓         ↓        ↓
Repository Layer
  ↓  (List-based returns)
Entity Layer
  ↓
AttributeConverters
(ClubIdentifier uses abbreviation)
```

**Characteristics:**

- `TransformationService` replacing `IpscMatchService` for semantic clarity
- `MatchHolder` encapsulating match data passing
- `MatchCompetitorEntityService` returns lists for bulk operations
- `domain` package renamed to `data` for mapping classes
- CI/CD quality gates: Qodana JVM static analysis and JaCoCo coverage

---

### v6.0.0: Versioned Match API & Fully Encapsulated Domain Layer

```
IpscController          IpscMatchController (/v2/ipsc/matches)
     ↓                        ↓
IpscService          IpscMatchService
     ↓               (insert/update/modify/get)
TransformationService        ↓
     ↓               DomainService
     ↓               (entity services only — no direct repo access)
     ↓                    ↓
     └──────────► ClubEntityService
                  CompetitorEntityService
                  MatchEntityService
                  MatchStageEntityService
                  MatchCompetitorEntityService
                  MatchStageCompetitorEntityService
                        ↓
                  Repository Layer
                        ↓
                  Entity Layer
                        ↓
                  AttributeConverters
```

**Model package structure:**

```
models/ipsc/
├── common/   ← all shared IPSC models
│   ├── dto/, request/, response/, records/, holders/, data/, divisions/
└── match/    ← match-only models
    ├── dto/, request/, response/, holders/dto/
```

**Characteristics:**

- Dedicated `/v2/ipsc/matches` API — create, replace, patch, retrieve matches
- `DomainServiceImpl` no longer injects repositories directly; entity services are the only access path
- `models/ipsc/common/` + `models/ipsc/match/` provide clear package boundaries
- `IpscUtil` centralises club and match display-string construction
- `IpscMemberController` stub registered for upcoming member management

---

### v7.0.0: Club-Scoped Results, Visitor Tracking & Shooter Log Model

```
IpscController          IpscMatchController (/v2/ipsc/matches)
     ↓                        ↓
IpscService          IpscMatchService
     ↓               (unchanged — no service/controller wiring for the new fields yet)
TransformationService        ↓
     ↓               DomainService
     ↓                    ↓
     └──────────► Entity Services (unchanged)
                        ↓
                  Repository Layer (rebuilt — 8 JpaRepository interfaces)
                        ↓
                  Entity Layer (promoted from domain/old/, extended)
                  ├── Club (+ identifier)
                  ├── Competitor (+ homeClub)
                  ├── MatchCompetitor (+ overallRanking, clubRanking, isVisitor)
                  ├── MatchStageCompetitor (→ matchCompetitor FK)
                  ├── IpscMatchStage (+ unique constraint)
                  └── ShooterLog / ShooterLogEntry (new)
                        ↓
                  AttributeConverters (ClubIdentifier, FirearmType — reused, no new converters)
```

**Characteristics:**

- `domain/old/` retired — all six entities live in `za.co.hpsc.web.domain` again
- Club-scoped and visitor-aware match results without a separate `MatchResult` table
- Per-stage results attach to a `MatchCompetitor` (firearm-type entry), not directly to a `Competitor`
- `ShooterLog`/`ShooterLogEntry` persist point-in-time best-4-match snapshots
- `repositories/` package fully rebuilt (8 interfaces); no new enums or converters required
- Domain-layer groundwork only — service, controller and import-pipeline wiring still to come

---

### v7.1.0: Shooter Log Correction

```
                  Repository Layer
                  ├── ShooterLogRepository (findAllBy...AndPowerFactor)
                  └── ShooterLogCompetitorRepository (new — supersedes ShooterLogEntryRepository)
                        ↓
                  Entity Layer
                  ├── ShooterLog (+ powerFactor)
                  └── ShooterLogCompetitor (renamed from ShooterLogEntry; + points, + match)
                        ↓
                  AttributeConverters (PowerFactorConverter — reused, no new converters)
```

**Characteristics:**

- Same domain/repository shape as v7.0.0 — this release corrects the shooter-log entity's name and scope rather than
  changing the architecture around it
- `ShooterLog`/`ShooterLogCompetitor` remain schema-only; still no calculation service consumes them
- Flyway migration `V7_1_0__update_shooter_log_schema.sql` renames the table and its constraints in place — no backfill
  needed since both tables are still empty everywhere

---

### v7.2.0: Test Suite Conventions & Tooling

```
Test Class Structure
├── // <ClassName>(ParamTypes) — constructors first
├── // publicMethod()          — alphabetical, overloads by param count/type
├── // protectedMethod()       — after all public methods
└── // toString()              — always last
        ↓
Claude Code Commands
├── /scaffold-unit-tests        (interface + impl-only test split)
└── /scaffold-integration-tests (new — @SpringBootTest, public-interface-only calls)
```

**Characteristics:**

- No domain/repository/architectural change — this release formalises and retrofits a test-file convention (26 files),
  closes 4 JaCoCo coverage gaps and corrects/extends AI-agent tooling
- Spring Boot parent `4.0.7` → `4.1.0`, with redundant `pom.xml` overrides removed and `flyway-mysql` kept in sync with
  Boot's managed `flyway.version`

---

### v8.0.0: IPSC Competitor & Match CRUD

```
IpscCompetitorController        IpscMatchController
        ↓                              ↓
IpscCompetitorService          IpscMatchService
        ↓                              ↓
  CompetitorRepository    IpscMatchRepository / IpscMatchStageRepository
        ↓                              ↓
     Competitor                IpscMatch / IpscMatchStage
        ↓                              ↓
  GenderConverter          ClubIdentifierConverter / FirearmTypeConverter / MatchCategoryConverter
```

**Characteristics:**

- `IpscController`'s empty stub retired — the module now has real, layered CRUD endpoints matching the
  `Controller → Service → Repository → Entity` pattern established since v1.0.0
- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only, not
  yet consumed by any controller — the next stage of the rebuild
- No changes to the Award/Image CSV pipeline's own architecture beyond the `processCsv` → `createAwards`/`createImages`
  rename

---

## ✨ Feature Timeline

### Data Processing Features

- **v1.0.0:** Image CSV processing, MIME type inference
- **v1.1.0:** Award CSV processing
- **v2.0.0:** CAB file import, XML processing, UUID mapping
- **v3.0.0:** Firearm-type classification, enhanced scoring
- **v4.0.0:** Enhanced entity mapping, validation layers
- **v5.0.0:** Entity initialisation framework, record generation
- **v5.2.0:** Three-tier mapping architecture, enhanced match entity handling
- **v5.3.0:** Custom JPA converters; optimised repository queries; `Set`-based competitor deduplication
- **v5.4.0:** `EnrolledCompetitorDto`; SAPSA number validation; competitor deduplication by SAPSA+ID
- **v6.0.0:** `IpscUtil` for club/match string formatting; `MatchOnlyDto` match pipeline; match search request models
  (`MatchSearchRequest`, `MatchSearchDateRequest`, `MatchSearchIdRequest`)

### Domain Management Features

- **v1.0.0:** Image entities
- **v1.1.0:** Award entities
- **v2.0.0:** Match, Competitor, Stage entities
- **v3.0.0:** Club reintroduction, Firearm types
- **v4.0.0:** IpscMatch, IpscMatchStage entities
- **v5.0.0:** Advanced initialisation patterns
- **v5.2.0:** DtoMapping, EntityMapping, DtoToEntityMapping, MatchEntityService
- **v5.3.0:** Custom AttributeConverters for all enums; DtoMapping as Java record; corrected `@OneToMany` mappedBy
  declarations; ClubEntityService simplified
- **v5.4.0:** `EnrolledCompetitorDto`; `ClubIdentifier` abbreviation; records' restructuring; `domain` → `data` package;
  `MatchHolder`; `TransformationService`
- **v6.0.0:** `MatchOnlyDto`/`Request`/`Response` for match CRUD; `models/ipsc/common/` + `models/ipsc/match/` package
  split; entity service methods `findClubById`, `findCompetitorById`, `findMatchStageCompetitorById`;
  `DomainServiceImpl` fully decoupled from repositories
- **v7.0.0:** Six entities promoted from `domain/old/` back into `domain`; `Club.identifier`, `Competitor.homeClub`,
  `MatchCompetitor.overallRanking`/`clubRanking`/`isVisitor`; `MatchStageCompetitor` repointed to `matchCompetitor`; new
  `ShooterLog`/`ShooterLogEntry` entities; `repositories/` package rebuilt with 8 `JpaRepository` interfaces
- **v7.1.0:** `ShooterLogEntry` renamed to `ShooterLogCompetitor`; `ShooterLog.powerFactor`;
  `ShooterLogCompetitor.points`/`match`; `ShooterLogRepository` finder renamed to include `PowerFactor`; new
  `ShooterLogCompetitorRepository`
- **v8.0.0:** `Gender` gains `name`/`abbreviation`/`fromName()`/`toString()`; new `GenderConverter`;
  `CompetitorRequest`/`CompetitorResponse`, `MatchResponse`/`MatchStageResponse` DTOs; `models/ipsc/request` split into
  `models/ipsc/match/request`/`models/ipsc/scores/request`

### API Capabilities

- **v1.0.0:** Image endpoints
- **v1.1.0:** Award endpoints, OpenAPI documentation
- **v2.0.0:** Match result endpoints
- **v3.0.0:** Enhanced IPSC endpoints
- **v4.0.0:** Refactored IPSC endpoints
- **v4.1.0:** Complete CRUD endpoints
- **v5.0.0:** Mature API with record generation
- **v5.2.0:** Enhanced null safety with Optional return types
- **v5.3.0:** Consolidated service API; IpscMatchResultService removed from the internal contract
- **v5.4.0:** Improved error handling in ControllerAdvice; IpscController updates
- **v6.0.0:** `/v2/ipsc/matches` CRUD API (POST, PUT, PATCH, GET) via `IpscMatchController`; structured logging in
  `ControllerAdvice`; `IpscMemberController` stub at `/ipsc/member`
- **v8.0.0:** `IpscCompetitorController`/`IpscMatchController` full CRUD (`create`/`update`/`patch`/`get`, plus
  `getAllMatches`) on `/ipsc/competitors`/`/ipsc/matches`, replacing the empty `IpscController` stub;
  `AwardController`/`ImageController` bulk endpoints moved to `/awards/bulk`/`/images/bulk`, returning `201 Created`

### Testing Coverage

- **v1.0.0:** Basic unit tests
- **v1.1.0:** Service and model tests
- **v2.0.0:** Comprehensive service tests
- **v3.0.0:** Domain model tests (279+ lines)
- **v4.0.0:** Integration tests (985+ lines)
- **v5.0.0:** Advanced entity initialisation tests
- **v5.0.0+:** Comprehensive DTO unit tests (151+ tests)
    - MatchStageDtoTest (48 tests): Constructors, init(), toString()
    - ScoreDtoTest (26 tests): All constructor patterns, edge cases
    - MatchStageCompetitorDtoTest (77 tests): Complete lifecycle coverage
- **v5.1.0:** Test quality enhancement (section-based organisation, duplicate elimination)
- **v5.2.0:** Comprehensive test consolidation
    - DtoToEntityMappingTest (716 lines)
    - TransactionServiceTest (2,000+ lines)
    - Consolidated all service and utility tests
    - Removed 3,000+ lines of duplicate tests
    - All tests follow a standardised naming convention
    - Consolidated test structure across all DTO classes
    - Edge case testing: null/empty/blank fields, boundary values, enum mapping
    - Special character and Unicode support validation
    - Format consistency and mutability testing
- **v5.0.0+ (Post-Release):** Test refactoring and enhanced coverage
    - IpscMatchServiceTest: Renamed from IpscMatchEntityServiceImplTest with enhanced match results processing coverage
    - IpscMatchResultServiceImpl: Comprehensive null handling enhancements
    - WinMSS Integration Tests: Comprehensive importWinMssCabFile validation and processing scenarios
    - FirearmTypeToDivisionsTest: Enhanced with comprehensive cases and improved naming
    - Test documentation improvements across all test classes
- **v5.3.0:** Service consolidation test overhaul
    - DomainServiceTest: 787 lines added – comprehensive `initMatchEntities` coverage
    - IpscMatchServiceTest: 3,156 lines changed – comprehensive consolidation with helper methods
    - TransactionServiceTest: 1,031 lines changed – enabled tests, `getFirst()` assertions
    - IpscServiceIntegrationTest: 113 lines changed – `importWinMssCabFile` integration tests
    - Removed IpscMatchResultServiceTest (1,802 lines) – service deleted
    - Removed ScoreDtoTest (643 lines) – class deleted
- **v5.4.0:** Largest single-release test expansion in project history
    - 20+ new test classes, ~7,000 lines of new test code
    - New controller tests (4), converter tests (6), domain entity tests (6), exception tests (3)
    - New integration tests (3): `AwardServiceIntegrationTest`, `ImageServiceIntegrationTest`,
      `DtoToEntityMappingIntegrationTest`
    - New service tests: `TransformationServiceTest` (1,026 lines), `MatchCompetitorDtoTest`
    - Removed `IpscMatchServiceTest` (10,076 lines – service renamed to `TransformationService`)
    - Updated major suites: DomainServiceTest (1,428), TransactionServiceTest (1,736), IpscServiceIntegrationTest (649),
      IpscServiceTest (737)
- **v6.0.0:** 8 new test classes (~1,300 lines) covering the match CRUD pipeline end-to-end
    - `IpscMatchControllerTest`, `IpscMatchServiceTest` (unit)
    - `IpscMatchIntegrationTest` (H2 integration — match persistence)
    - `MatchOnlyDtoTest`, `MatchOnlyRequestTest`, `MatchOnlyResponseTest`, `MatchResponseTest` (model)
    - `IpscUtilTest` (utility — string formatting edge cases)
    - `IpscControllerTest` removed; covered by `IpscMatchControllerTest`
    - Major suite updates: `TransformationServiceTest` (+747), `DomainServiceTest` (+247), `TransactionServiceTest`
      (+246), `ValueUtilTest` (+294)
- **v7.0.0:** No new dedicated unit/integration test coverage for the promoted/extended entities or the 8 new
  repositories — verified instead via `./mvnw clean compile` and `HpscWebApplicationTests` (Spring context boot against
  H2, Hibernate schema build validating every `@JoinColumn`, converter and unique constraint across all 8 entities)
- **v7.1.0:** No new dedicated unit/integration test coverage for the renamed/rescoped `ShooterLogCompetitor` entity —
  same verification approach as v7.0.0 (compile + `HpscWebApplicationTests` H2 schema build)
- **v7.2.0:** New interface-contract tests `AwardServiceTest`/`ImageServiceTest`; 4 JaCoCo-identified coverage gaps
  closed in `ControllerResponseTest`, `FirearmTypeTest`, `ControllerAdviceTest` (suite coverage 95.7%/91.7% →
  97.3%/98.1%); `HpscWebApplicationTests` renamed to `HpscWebApplicationTest`; 26 existing test files retrofitted with
  the new `// methodName()` header-comment/ordering convention (comments and reordering only — no behaviour change)
- **v8.0.0:** New unit and integration test coverage for `IpscCompetitorController`/`Service`/`ServiceImpl` and
  `IpscMatchController`/`Service`/`ServiceImpl`; new `GenderTest`/`GenderConverterTest`; mechanical test updates for the
  `fromX` enum-factory rename — the largest single-release test expansion since v5.4.0

### Documentation Quality

- **v1.0.0:** Inline Javadoc
- **v1.1.0:** Standardised documentation, OpenAPI
- **v1.1.2:** README and ARCHITECTURE guides
- **v3.0.0:** Enhanced Javadoc across codebase
- **v5.0.0:** RELEASE_NOTES, CHANGELOG, HISTORY
- **v5.2.0:** Comprehensive release documentation with breaking changes analysis
- **v5.3.0:** v5.3.0 release notes, changelog entry, history update; Javadoc for `IpscMatchStage.init()` and
  `findMatchByNameAndScheduledDate`
- **v5.4.0:** v5.4.0 release notes, changelog entry, history update; Javadoc on `EnrolledCompetitorDto` and
  `TransformationService` interface
- **v6.0.0:** v6.0.0 release notes, changelog entry, history update; CLAUDE.md added for AI assistant context
- **v7.0.0:** v7.0.0 release notes, changelog entry, history update
- **v7.1.0:** v7.1.0 release notes, changelog entry, history update; AI-agent prompt files migrated to
  `.claude/commands/*.md`; `AGENTS.md` adopts GitFlow; `CONTRIBUTING.md` added
- **v7.2.0:** v7.2.0 release notes, changelog entry, history update; new `// methodName()` test-comment/ordering
  convention added to AGENTS.md; new CLAUDE.md Git Workflow section with explicit PR-target guidance;
  `/scaffold-unit-tests`/`/scaffold-integration-tests` Claude Code commands added; false AssertJ claim removed from five
  docs; CLAUDE.md package-overview table corrected
- **v8.0.0:** v8.0.0 release notes, changelog entry, history update; comprehensive Javadoc/`@since` pass across models,
  converters, exceptions, utils, constants and `ControllerAdvice`; `AGENTS.md`/`CLAUDE.md` merged into a single
  tool-agnostic reference; AI-agent tooling migrated from slash commands to Skills

---

## 💡 Project Philosophy Evolution

### Major Version Goals

- **Version 4.x (v4.0.0 – v4.1.0):** Establish explicit IPSC domain naming (`Match` → `IpscMatch`, `MatchStage` →
  `IpscMatchStage`) backed by multi-layered validation, then complete the entity lifecycle with full CRUD support and
  transactional consistency — turning the renamed domain model into a fully operable API surface.
- **Version 5.x (v5.0.0 – v5.4.0):** Mature the codebase into a stable, well-tested, standards-compliant foundation —
  formalise Semantic Versioning, consolidate and simplify the service layer and extend real domain capability
  (competitor enrolment) only once that foundation was solid.
- **Version 6.x (v6.0.0):** Turn the v5.x foundation into an actual product surface — a dedicated, versioned match CRUD
  API, complete the layered-architecture discipline by fully decoupling `DomainServiceImpl` from repositories and
  restructure the IPSC model packages for long-term growth.
- **Version 7.x (v7.0.0 – v7.4.0):** Rebuild IPSC domain-layer groundwork deliberately ahead of the service/controller
  layer — which had since been removed pending a rebuild — while investing in process discipline: formalised test
  conventions, AI-agent tooling and increasingly rigorous documentation accuracy and consistency.
- **Version 8.x (v8.0.0 – v8.8.0):** Complete the IPSC module rebuild that v6.x–v7.x deliberately deferred — real
  competitor and match CRUD replacing the empty controller stub — while consolidating the project's own documentation
  (`AGENTS.md`/`CLAUDE.md` merge) and AI-agent tooling (commands → Skills) into a single, coherent source of truth.
  Extend that foundation with competitor bulk CSV import and a project-wide correctness fix ensuring
  `@JsonProperty(required = true)` actually enforces required fields via matching `@JsonCreator` constructors, then
  close a silent test-coverage regression and stand up the CI static-analysis gate and build tooling so the release
  process keeps auditing its own roadmap documentation going forward. Broaden the domain surface itself —
  multi-value competitor emails, bulk CSV import extended to matches, club requirements relaxed to
  domain-appropriate defaults, and match start/end time tracking — while sustaining that same process discipline at
  documentation scale: a project-wide icon-registry and cross-reference consolidation, root-document title
  standardisation naming `AGENTS.md` as the project's single source of truth, and finally closing the release
  checklist's own audit loop by backfilling `HISTORY.md`'s Phase/Milestone record for every release that had fallen
  behind it. Then round out the match and competitor APIs — a match URL, start/end times corrected to time-of-day
  values, a competitor listing endpoint and finally delete endpoints that refuse rather than cascade over scoring
  history, making the long-standing "full CRUD" claim true — while tidying the platform underneath (Spring Boot's
  default port, a Spring Boot 4 springdoc line) and keeping the documentation honest at scale: formalising the 3-tier
  service test architecture, splitting `HISTORY.md`'s Evolution Overview and archived release notes into their own
  structure, and correcting `CHANGELOG.md` heading-depth drift across every convention document and skill.

### Initial Phase (v1.0.0)

**Focus:** Foundation & Basic Functionality

- Establish a working Spring Boot application
- Implement CSV data processing
- Create basic API endpoints
- Error handling foundation

### Growth Phase (v1.1.0 – v2.0.0)

**Focus:** Feature Expansion & Modularity

- Add new feature domains (awards)
- Introduce service-oriented architecture
- Establish documentation standards
- Improve code quality

### Specialisation Phase (v3.0.0 – v4.0.0)

**Focus:** IPSC Domain Compliance & Quality

- Align with IPSC standards
- Enhance domain clarity
- Comprehensive testing
- Production readiness

### Maturity Phase (v4.1.0 – v5.0.0)

**Focus:** Completeness, Standards & Infrastructure

- Complete CRUD capabilities
- Industry-standard versioning
- Infrastructure consolidation
- Entity initialisation framework

### Refinement Phase (v5.1.0 – v5.2.0)

**Focus:** Quality, Architecture & Maintainability

- Test suite enhancement and consolidation
- Architectural refactoring with separation of concerns
- Enhanced null safety and robustness
- Comprehensive test coverage across all layers
- Code quality and maintainability improvements
- Test suite enhancement and refactoring
- Improved code maintainability
- Long-term maintainability

### Consolidation Phase (v5.3.0)

**Focus:** Service Consolidation, Type Safety & Repository Efficiency

- Removal of unnecessary service abstractions (`IpscMatchResultService`)
- Custom JPA converters for explicit, type-safe enum persistence
- Immutable DtoMapping record for cleaner data flow
- Correct JPA bidirectional relationship declarations
- Repository query optimisation for performance and accuracy
- Continued test suite refinement and integration test expansion

### Enrolment Phase (v5.4.0)

**Focus:** Competitor Enrolment, Service Clarity & Comprehensive Test Coverage

- Introduce `EnrolledCompetitorDto` for member enrolment tracking through the IPSC pipeline
- Rename `IpscMatchService` to `TransformationService` for semantic accuracy
- SAPSA number validation and competitor deduplication in `CompetitorDto`
- Expand test coverage to all layers — controllers, converters, entities, exceptions, integration
- Establish Qodana JVM linting and JaCoCo coverage as CI/CD quality gates
- Package reorganisation (`domain` → `data`) and records restructuring for semantic clarity

### API Productisation Phase (v6.0.0)

**Focus:** Versioned Match API, Repository Decoupling & Package Structure

- Introduce a dedicated, versioned match CRUD API (`/v2/ipsc/matches`) separate from the bulk-import controller
- Complete the entity service encapsulation: `DomainServiceImpl` no longer bypasses the service layer to reach JPA
  repositories
- Restructure all IPSC model packages under `models/ipsc/common/` with a dedicated `models/ipsc/match/` sub-package,
  providing clear boundaries for shared vs. match-specific models
- Centralise display-string construction in `IpscUtil`, eliminating scattered formatting logic
- Support future member management and match search features with stub controller and search request models

### Domain Extension Phase (v7.0.0)

**Focus:** Match Results, Visitor Tracking & Shooter Log Data Model

- Promote the six entities parked under `domain/old/` back into the live `domain` package, retiring the `.old` package
- Model club-scoped match results and match visitors relationally — `homeClub`, `clubRanking`, `isVisitor` — rather than
  introducing a fourth club row
- Repoint per-stage results from `Competitor` to `MatchCompetitor` to support multiple firearm-type entries per
  competitor per match
- Introduce `ShooterLog`/`ShooterLogEntry` as persisted best-4-match snapshots, deferring the recalculation job/service
  to a future release
- Rebuild the `repositories/` package from scratch alongside the promoted domain model
- Deliver domain-layer groundwork deliberately ahead of service, controller and import-pipeline wiring

### Refinement Phase (v7.1.0)

**Focus:** Shooter Log Naming Accuracy & Scope Correction

- Rename `ShooterLogEntry` to `ShooterLogCompetitor` to reflect its role as a per-competitor snapshot row
- Scope shooter-log snapshots by `PowerFactor` as well as `FirearmType`
- Add a direct `match` reference and a `points` field to `ShooterLogCompetitor`, ahead of the future calculation service
  that will populate them
- Adopt GitFlow branching and add `CONTRIBUTING.md` for new-developer onboarding

### Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)

**Focus:** Test Conventions, Documentation Accuracy & AI-Agent Tooling

- Formalise a repo-wide test-file convention (method-comment headers, group ordering) retrofitted across 26 existing
  test files; close every JaCoCo-identified coverage gap (v7.2.0)
- Correct README.md/ARCHITECTURE.md to describe only what the codebase actually implements, removing overstated
  capability claims (v7.3.0)
- New AGENTS.md Serial commas rule and a British English rule tightened to cover code identifiers, both applied
  retroactively across the entire documentation set (v7.4.0)
- Resume IPSC domain-layer groundwork with new request DTOs, deliberately ahead of the service/controller layer removed
  pending a rebuild (v7.4.0)
- Add `/scaffold-unit-tests`, `/scaffold-integration-tests`, `/generate-pr-summary` and `/sync-unreleased-changes`
  Claude Code commands, keeping AI-agent tooling in sync with the project's actual conventions (v7.2.0 – v7.4.0)

### Module Completion Phase (v8.0.0)

**Focus:** IPSC Competitor & Match CRUD, Documentation Consolidation & AI-Agent Tooling

- Replace `IpscController`'s empty stub with full competitor and match CRUD, backed by new services and DTOs
- Extend `Gender` to match the shape of the project's other enums, paired with a new `GenderConverter`
- Rename `processCsv` to `createAwards`/`createImages` and enum `getByX` factories to `fromX`, removing naming
  inconsistencies accumulated across earlier releases
- Merge `CLAUDE.md`'s guidance into `AGENTS.md` as a single tool-agnostic reference; migrate AI-agent tooling from
  slash commands to Skills
- Invest in a comprehensive Javadoc/`@since` documentation pass and re-add Qodana JVM static analysis

---

## 📚 Key Learnings

### Architectural Insights

1. **Service Modularity:** Breaking monolithic services (v2.0.0) dramatically improved testability and maintainability
2. **Domain Clarity:** Explicit entity naming (v4.0.0) reduced confusion and improved code navigation
3. **Test-Driven Quality:** Comprehensive test suites enabled confident refactoring and bug fixes
4. **Documentation Priority:** Early documentation (v1.1.2) established clear system understanding

### Design Decisions

1. **DTO Layer:** Introduction in v2.0.0 created crucial separation between API contracts and domain models
2. **Firearm-Type Classification:** v3.0.0 restructuring improved IPSC compliance without major disruption
3. **Entity Initialisation Framework:** v5.0.0 consolidation provides a unified pattern for complex entity setup4.
   **Semantic Versioning:** Late adoption (v5.0.0) aligns with industry standards for future releases

### Technical Evolution

1. **Exception Handling:** Simplified approach (v3.1.0) improved maintainability without sacrificing clarity
2. **Validation Layers:** Multi-layered validation (v4.0.0) ensures data integrity across tiers
3. **Transaction Management:** Abstraction layer (v2.0.0) enables consistent data consistency patterns
4. **Test Coverage:** Growing investment from basic tests to comprehensive integration testing
5. **DTO Testing Excellence:** Post-v5.0.0 comprehensive DTO unit testing (151+ tests) establishes quality standards
    - Systematic testing of all constructor patterns
    - Complete init() method coverage with parameter combinations
    - toString() validation across all scenarios
    - Edge case mastery: null, empty, blank, boundary values
    - Enum mapping validation across all enums (PowerFactor, Division, FirearmType, CompetitorCategory)
    - Special character and Unicode support verification
    - Consistent test organisation with AAA pattern and clear naming conventions
6. **Continuous Test Refinement:** Ongoing test improvements demonstrate commitment to quality
    - Test class renaming for clarity (IpscMatchEntityServiceImplTest → IpscMatchServiceTest)
    - Enhanced null handling in service implementations
    - Comprehensive integration testing for complex workflows (WinMSS CAB import)
    - Documentation improvements ensuring maintainability
7. **Test Suite Consolidation (v5.1.0):** Structural improvements to test organisation and quality
    - Comprehensive test reorganisation with 6 logical sections for better navigation
    - Elimination of duplicate test cases while maintaining complete coverage
    - Section-based grouping: Null Input Handling, Null Collections & Fields, Match Name Field Handling, Club Fields
      Handling, Partial/Complete Data Scenarios, Edge Cases
    - Improved test readability with clear headers and consistent formatting
    - Enhanced maintainability through reduced code duplication (1 duplicate test removed)
    - All tests follow `testMethod_whenCondition_thenExpectedBehavior` naming pattern
    - Consolidated IpscMatchResultServiceImplTest from 24 to 23 tests with zero reduction in effective coverage
8. **Architectural Refactoring (v5.2.0):** Major architectural improvements with comprehensive scope
    - Three-tier mapping system (DtoMapping, EntityMapping, DtoToEntityMapping) for clear separation
    - Dedicated MatchEntityService for specialised entity handling
    - Comprehensive test consolidation across all services and utilities (removed 3,000+ duplicate lines)
    - Enhanced null safety with array initialisation and Optional return types
    - Major service refactoring: 61 files, +13,567 insertions, -5,898 deletions
    - New comprehensive tests: DtoToEntityMappingTest (716 lines), TransactionServiceTest (2,000+ lines)
    - All tests consistently follow the AAA pattern with standardised naming
9. **Service Consolidation & Type Safety (v5.3.0):** Focused consolidation and infrastructure improvements
    - Six custom JPA AttributeConverters replacing `@Enumerated(EnumType.STRING)` for type-safe persistence
    - `IpscMatchResultService` and `ScoreDto` fully removed; functionality consolidated into `DomainService` and
      `IpscMatchService`
    - `DtoMapping` transitioned to Java record for immutability and clarity
    - All bidirectional `@OneToMany` relationships corrected with `mappedBy` declarations
    - Repository queries optimised: `Set` deduplication, scheduled date constraints, fetch join removal
    - Test suite overhaul: DomainServiceTest (+787 lines), IpscMatchServiceTest (3,156 lines changed)
    - Statistics: ~45 commits, 59 files changed, +5,686 insertions, -4,613 deletions
10. **Competitor Enrolment & Service Transformation (v5.4.0):** Largest single-release test expansion
    - `EnrolledCompetitorDto` introduced for competitor enrolment tracking; SAPSA validation added
    - `IpscMatchService` renamed to `TransformationService`; `TransformationServiceImpl` (1,098 lines)
    - `ClubIdentifier` abbreviation field added; `ClubIdentifierConverter` updated for persistence
    - 20+ new test classes (~7,000 lines) across controllers, converters, entities, exceptions, integration
    - Qodana JVM linting and JaCoCo 0.8.14 code coverage integrated into CI/CD
    - Statistics: ~75 commits, 123 files changed, +12,713 insertions, -13,358 deletions
11. **Dedicated Match CRUD API & Service Encapsulation (v6.0.0):** Versioned API and layer enforcement
    - `IpscMatchController` at `/v2/ipsc/matches` establishes a versioned, resource-oriented match API
    - `DomainServiceImpl` fully decoupled from repositories — completing the intended layered architecture
    - All IPSC models moved to `models/ipsc/common/`; `models/ipsc/match/` added for match-specific classes
    - `IpscUtil` centralises display-string construction; `MatchOnlyDto/Request/Response` support match CRUD
    - 8 new test classes (~1,300 lines) covering controller, service, integration, DTO and utility layers
    - Statistics: 40 commits, 165 files changed, +6,779 insertions, -3,501 deletions
12. **Match Results, Visitor Tracking & Shooter Log Data Model (v7.0.0):** Domain-layer groundwork ahead of the pipeline
    - Six entities promoted from `domain/old/` back into `domain`; `.old` package retired
    - `Club.identifier`, `Competitor.homeClub`, `MatchCompetitor.clubRanking`/`isVisitor` model club-scoped results and
      visitors relationally
    - `MatchStageCompetitor` repointed from `Competitor` to `MatchCompetitor`; three new unique constraints added across
      the domain model
    - New `ShooterLog`/`ShooterLogEntry` entities persist best-4-match snapshots; no calculation job/service yet
    - `repositories/` package rebuilt from scratch with 8 new `JpaRepository` interfaces
    - No dedicated new unit/integration test coverage added for the new/changed entities in this release
    - Statistics: 1 commit, 15 files changed, +207 insertions, -30 deletions
13. **Shooter Log Refinement (v7.1.0):** Naming accuracy and scope correction ahead of the calculation service
    - `ShooterLogEntry` renamed to `ShooterLogCompetitor`; entity gains `points` and a direct `match` reference
    - `ShooterLog.powerFactor` scopes snapshots by power factor as well as firearm type
    - `ShooterLogRepository` finder renamed to include `PowerFactor`; new `ShooterLogCompetitorRepository` supersedes
      `ShooterLogEntryRepository`
    - Migration renames the table/constraints in place — no backfill needed, both tables remain empty
    - Repository tooling migrated from `.github/prompts/` to `.claude/commands/`; `AGENTS.md` adopts GitFlow;
      `CONTRIBUTING.md` added
14. **IPSC Module Completion (v8.0.0):** Full competitor and match CRUD, six releases after v6.0.0 first laid the
    groundwork
    - `IpscCompetitorController`/`IpscMatchController` replace the empty `IpscController` stub with real, layered CRUD
    - New `IpscCompetitorService`/`IpscMatchService` + impls, `Gender` enum enhancements and `GenderConverter`
    - Naming consistency sweep: `processCsv` → `createAwards`/`createImages`, enum `getByX` → `fromX` factories
    - Comprehensive Javadoc/`@since` pass; AI-agent tooling migrated from slash commands to Skills
    - Largest single-release test expansion since v5.4.0 — full unit and integration coverage for both new
      controllers/services
15. **Jackson Required-Field Gotcha (v8.1.0):** `@JsonProperty(required = true)` only fires for creator (constructor)
    parameters — a class deserialised via its default no-args constructor and setters silently treats a missing
    "required" field as `null`
    - Fixed across every IPSC request model to date by adding a `@JsonCreator` constructor with each parameter bound
      via `@JsonProperty`, replacing the affected classes' Lombok `@AllArgsConstructor`
    - Verified with a throwaway `csvMapper.addMixIn(...)` scratch test that the two not-yet-wired scores CSV variants
      are correctly mixin-compatible with their plain counterparts, the same pattern
      `AwardServiceImpl`/`ImageServiceImpl` already use for `AwardRequestForCSV`/`ImageRequestForCsv`
    - Caught a genuine validation mismatch along the way: `CompetitorRequest`'s Jackson-required field was
      `competitorNumber`, not the actually-validated `clubNumber`

---

## 🛤️ Future Roadmap Implications

Based on the evolution to v8.8.0, the following areas are identified for future enhancement:

### Previously Completed (v5.4.0 and earlier)

- `EnrolledCompetitorDto` introduced for enrolled competitor tracking through the IPSC pipeline
- `IpscMatchService` renamed to `TransformationService`; `TransformationServiceImpl` (1,098 lines)
- `ClubIdentifier` abbreviation field added; `ClubIdentifierConverter` updated
- SAPSA number validation and competitor deduplication in `CompetitorDto`
- 20+ new test classes (~7,000 lines) — the largest single-release expansion
- Qodana JVM linting and JaCoCo code coverage integrated into CI/CD
- Package restructure: `ipsc/domain` → `ipsc/data`; records and holders reorganised
- Six custom JPA attribute converters (ClubIdentifier, CompetitorCategory, Division, FirearmType, MatchCategory,
  PowerFactor)
- IpscMatchResultService and ScoreDto removed; match result processing consolidated
- Three-tier mapping architecture (DtoMapping, EntityMapping, DtoToEntityMapping)
- Repository query optimisation (Set deduplication, scheduled date, fetch join removal)
- Test suite reorganisation and consolidation (from v5.1.0, v5.2.0, v5.3.0)

### Previously Completed (v6.0.0)

- `IpscMatchController` introduced at `/v2/ipsc/matches` with full CRUD (POST, PUT, PATCH, GET)
- `IpscMatchService` + `IpscMatchServiceImpl` added as dedicated match management service
- `MatchOnlyDto`, `MatchOnlyRequest`, `MatchOnlyResponse`, `MatchOnlyResultsDto` introduced
- `DomainServiceImpl` fully decoupled from JPA repositories; delegates to entity services only
- New entity service methods: `findClubById`, `findCompetitorById`, `findMatchStageCompetitorById`
- `IpscUtil` added for centralised club/match display-string formatting
- All IPSC models moved to `models/ipsc/common/`; `models/ipsc/match/` sub-package introduced
- Match search request models: `MatchSearchRequest`, `MatchSearchDateRequest`, `MatchSearchIdRequest`
- `IpscMemberController` stub registered at `/ipsc/member`
- Spring Boot upgraded 4.0.5 → 4.0.6; MIT licence and SCM metadata added to `pom.xml`
- 8 new test classes (~1,300 lines); `IpscControllerTest` removed

### Previously Completed (v7.0.0)

- Six entities promoted from `domain/old/` back into `za.co.hpsc.web.domain`; `.old` package removed
- `Club.identifier` (`ClubIdentifier`, unique) ties a club to HPSC/SOSC/PMPSC
- `Competitor.homeClub` — nullable `@ManyToOne Club` relation for home-club membership
- `MatchCompetitor.matchRanking` renamed `overallRanking`; new `clubRanking` and `isVisitor` fields; new unique
  constraint `(competitor_id, match_id, firearm_type)`
- `MatchStageCompetitor` repointed from `competitor` to `matchCompetitor`; new unique constraint
  `(match_competitor_id, match_stage_id)`
- `IpscMatchStage` gains new unique constraint `(match_id, stage_number)`
- New `ShooterLog`/`ShooterLogEntry` entities persist best-4-match shooter-log snapshots
- `repositories/` package rebuilt from scratch with 8 new `JpaRepository` interfaces
- Project version bumped to 7.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v7.1.0)

- `ShooterLogEntry` renamed to `ShooterLogCompetitor` (table `shooter_log_entry` → `shooter_log_competitor`)
- `ShooterLog.powerFactor` (`PowerFactor`, not nullable) scopes snapshots by power factor as well as firearm type
- `ShooterLogCompetitor.points` (nullable) and `ShooterLogCompetitor.match` (`@ManyToOne IpscMatch`, not nullable) added
- `ShooterLogRepository.findAllByCompetitorIdAndFirearmType` renamed to
  `findAllByCompetitorIdAndFirearmTypeAndPowerFactor`
- New `ShooterLogCompetitorRepository` supersedes `ShooterLogEntryRepository`
- Project version bumped to 7.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v7.2.0)

- New interface-contract unit tests `AwardServiceTest`/`ImageServiceTest`, exercising `createAwards` through the interface
  type rather than the impl class
- 4 JaCoCo-identified coverage gaps closed (`ControllerResponse`, `FirearmType.toString()`,
  `ControllerAdvice.logError`); suite coverage rose from 95.7%/91.7% to 97.3%/98.1% (line/branch)
- `HpscWebApplicationTests` renamed to `HpscWebApplicationTest`; 26 existing test files retrofitted with a new
  `// methodName()` header-comment/ordering convention
- New `/scaffold-unit-tests` (corrected from a stale, wrong-project prompt) and `/scaffold-integration-tests` Claude
  Code commands
- Spring Boot parent upgraded `4.0.7` → `4.1.0`; redundant `pom.xml` version overrides removed; `flyway-mysql` bumped
  `11.14.1` → `12.4.0` to match Boot's newly-managed `flyway.version`
- Project version bumped to 7.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.0.0)

- `IpscCompetitorController`/`IpscMatchController` full CRUD, replacing the empty `IpscController` stub
- New `IpscCompetitorService`/`IpscMatchService` + impls; new `CompetitorRequest`/`CompetitorResponse`,
  `MatchResponse`/`MatchStageResponse` DTOs
- `Gender` enum gains `name`/`abbreviation`/`fromName()`/`toString()`; new `GenderConverter`
- `processCsv` renamed to `createAwards`/`createImages`; bulk endpoints moved to `/awards/bulk`/`/images/bulk`,
  returning `201 Created`; enum `getByX` factories renamed to `fromX`
- Comprehensive Javadoc/`@since` pass; `AGENTS.md`/`CLAUDE.md` merged; AI-agent tooling migrated from slash commands to
  Skills; Qodana JVM static analysis re-added
- Project version bumped to 8.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.1.0)

- New `IpscCompetitorController.createCompetitors` (`POST /ipsc/competitors/bulk`) persists competitors from CSV
  data via the existing `createCompetitor` logic
- New `CompetitorRequestForCSV`/`CompetitorResponseHolder` models
- Fixed a Jackson gotcha affecting every `@JsonProperty(required = true)` field added to date: `CompetitorRequest`,
  `CompetitorRequestForCSV`, `MatchRequest`, `MatchStageRequest` and the score request models all gained a
  `@JsonCreator` constructor so required fields are actually enforced
- `CompetitorRequest`'s required field corrected from `competitorNumber` to `clubNumber`
- Project version bumped to 8.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.1.1)

- New `.github/workflows/qodana.yml` completes the Qodana static-analysis CI gate, configured but unwired since
  v8.0.0
- Recreated the deleted exception-hierarchy test suite and closed other coverage gaps; full-suite coverage rose
  from 92.9%/93.4% to 98.34%/98.84% (line/branch)
- Spring Boot bumped `4.1.0` → `4.1.1`; `jackson-databind`, `log4j-api` and `jackson-bom.version` overrides all
  dropped as redundant
- `documentation/roadmap/improvement-plan.md` gains two new gaps (match-scoring service layer; remaining Qodana
  CI-verification work) and closes the `jackson-databind` override gap
- Project version bumped to 8.1.1 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.2.0)

- `Competitor.emailAddress` (`String`) replaced with `emailAddresses` (`List<String>`), backed by a new
  `competitor_email` child table and a backfilling Flyway migration
- New `SystemConstants.ARRAY_SEPARATOR` unifies `AwardServiceImpl`/`ImageServiceImpl`'s bulk CSV parsing with the
  competitor domain's semicolon-separated multi-value convention
- Qodana static analysis removed entirely — it had failed on every CI run since v8.1.1 added it (missing
  `QODANA_TOKEN` secret, unconditional SARIF upload) — closing `documentation/roadmap/improvement-plan.md`'s Gap #7
  as not applicable
- New genuinely-multiple-address test coverage surfaced and fixed a real bug: `IpscCompetitorServiceImpl`'s
  `applyFields`/`patchCompetitor` now defensively copy `emailAddresses` into a new `ArrayList` instead of storing
  the caller-supplied `List` reference directly, avoiding an unhandled `UnsupportedOperationException` on update
- Project version bumped to 8.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.3.0)

- New `IpscMatchController.createMatches` (`POST /ipsc/matches/bulk`) persists matches, together with their stages,
  from CSV data via the existing `createMatch` logic, mirroring the competitor domain's v8.1.0 bulk-import shape
- New `MatchRequestForCSV`/`MatchResponseHolder` models; its `stages` field is a single semicolon-separated CSV
  cell of `<stageNumber>-<stageName>` entries, replacing a discarded `numberOfStages` count-field attempt before
  either design reached `develop`
- `documentation/roadmap/improvement-plan.md`'s Gap #8 (match bulk CSV import stated as removed pending a rebuild)
  closed
- Project version bumped to 8.3.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.3.1)

- New `.github/workflows/build.yml` runs `./mvnw verify -Pcoverage` on push/PR to `main`/`develop`, closing Gap #2
- New JaCoCo `check` execution enforces a `LINE`/`COVEREDRATIO` minimum — 51% initially, then raised to 86% within
  the same branch — still below the real baseline (98.16%/98.94% line/branch, 836 tests, up from 775 tests /
  98.34%/98.84% at v8.1.1), partially progressing Gap #4
- `AwardService.createAwards()`/`ImageService.createImages()` CSV processing confirmed intentionally stateless by
  design, closing Gap #3
- `ARCHITECTURE.md`/`CONTRIBUTING.md`'s CI/CD & Quality Gates tables updated to match
- Project version bumped to 8.3.1 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.4.0)

- New `ClubIdentifier.ALL`, seeded via `V7_3_0__seed_club_data.sql`; `IpscMatchServiceImpl.resolveClub()` now
  defaults a missing/blank match `club` to it instead of failing validation, closing Gap #9
- `IpscCompetitorServiceImpl.resolveClubNumber()` requires `clubNumber` only when the home club is HPSC;
  `Competitor.clubNumber` column relaxed to nullable via `V7_4_0__make_club_number_nullable.sql`
- New JaCoCo `LINE`/`COVEREDRATIO` floor tightened `0.86` → `0.97`, confirmed holding at a fresh 98.44%/98.98%
  (line/branch, 868 tests) baseline, closing Gap #4
- `HpscConstants` removed; date-format constants consolidated onto `SystemConstants`/`IpscConstants`
- `tomcat-embed-core`/`-el`/`-websocket` patched `11.0.24` → `11.0.25`, closing three critical CVEs
- `AGENTS.md` gained Member ordering and REST naming conventions, plus a Release Checklist step verifying
  `ARCHITECTURE.md`'s Project Structure tree against disk
- Project version bumped to 8.4.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.4.1)

- `AGENTS.md`/`CONTRIBUTING.md`'s near-verbatim duplicates condensed into highlights-and-link references, with
  `CONTRIBUTING.md` kept as the sole canonical copy of Git Workflow's "Merging" subsection
- New `AGENTS.md` "🧩 Claude Code Skills" and "🗺️ Roadmap Planning" sections, each mirrored by a short pointer in
  `CONTRIBUTING.md`
- `AGENTS.md`'s icon registry backfilled with 25 icons already in use, plus a "Reserved" sub-table for the sibling
  `hpsc-web-vite` repository's frontend icons; icon collisions resolved across the root documents and 17 archived
  release notes
- `CHANGELOG.md`'s duplicate, truncated `[5.0.0]` section removed
- Project version bumped to 8.4.1 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.4.2)

- `AGENTS.md` declared this project's ultimate source of truth for conventions, with `CONTRIBUTING.md`'s intro
  pointing back to it
- `CHANGELOG.md` retitled "HPSC Website Backend" under a new "🧾 Change Log" heading, every heading beneath it
  demoted one level; `CONTRIBUTING.md`/`HISTORY.md`'s titles gain the same prefix
- Project version bumped to 8.4.2 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.5.0)

- `IpscMatch` gains nullable `startTime`/`endTime` columns via `V7_5_0__add_ipsc_match_start_end_time.sql`, wired
  through `MatchRequest`, `MatchRequestForCSV`, `MatchResponse` and `IpscMatchServiceImpl`
- Match CSV bulk import now requires `StartTime`/`EndTime` header columns (values may be left blank)
- New `IpscMatchServiceIntegrationTest` coverage proves the round-trip through the real H2/Hibernate layer
- British English "Licence" applied everywhere except `LICENSE.md`'s own filename and content
- Project version bumped to 8.5.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.5.1)

- `HISTORY.md` backfilled with Phase/Milestone 26–28 for v8.4.1, v8.4.2 and v8.5.0, closing Gap #10
- `improvement-plan.md` gains an "At a Glance" gap-status index; its Roadmap's **Now**/**Next** rows refreshed
- `HISTORY.md`'s narrative sections reordered oldest-first (only the Historical Timeline stays newest-first), and
  stale Conclusion-section metadata removed
- Re-scoped from `v8.6.0` **MINOR** to `v8.5.1` **PATCH**, since the whole diff proved documentation/tooling-only
- Project version bumped to 8.5.1 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.6.0)

- New nullable `IpscMatch.url` column via `V7_6_0__add_ipsc_match_url.sql`, wired end-to-end with a matching `Url`
  CSV column
- `IpscMatch.startTime`/`endTime` corrected from `LocalDateTime` to `LocalTime` via
  `V7_7_0__change_ipsc_match_start_end_time_to_time.sql`; JSON/CSV values are now bare `HH:mm`, per the new
  `IpscConstants.IPSC_INPUT_TIME_FORMAT`
- `AGENTS.md`'s Test Conventions formally document the 3-tier `<Service>Test`/`<Service>ImplTest`/
  `<Service>IntegrationTest` architecture
- Project version bumped to 8.6.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.6.1)

- `HISTORY.md`'s Evolution Overview split out into `documentation/history/EVOLUTION_OVERVIEW.md`
- All 52 archived release notes/PR descriptions regrouped into `documentation/history/v1/`–`v8/` by major version,
  with `AGENTS.md`, `README.md` and the affected skills updated to the new paths
- Scoped as a **PATCH**, since the whole diff proved documentation/tooling-only
- Project version bumped to 8.6.1 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.6.2)

- `CHANGELOG.md` heading-depth references corrected to `###`/`####`/`#####` across `AGENTS.md`,
  `CONTRIBUTING.md` and five skills, with `AGENTS.md` now spelling out the full nesting
- This section's Short-term/Medium-term lists refreshed against what had actually shipped, closing Gap #11
- `AGENTS.md`'s icon registry restructured to mirror the shared project template (`🛤️` Roadmap, `☑️` Success
  Criteria), with every live heading realigned
- Scoped as a **PATCH**, since the whole diff proved documentation/tooling-only
- Project version bumped to 8.6.2 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.7.0)

- New `GET /ipsc/competitors` endpoint (`IpscCompetitorController.getAllCompetitors`), mirroring `getAllMatches`
- A match CSV's `Stages` cell now separates stage number from name with `:` instead of `-`; the old form is rejected
- Every `@ManyToOne` association switched from `FetchType.LAZY` to `FetchType.EAGER`
- The `server.port=8081` override removed, so the app runs on Spring Boot's default port `8080`
- `springdoc-openapi-starter-webmvc-ui` bumped `2.8.5` → `3.1.0` via an imported BOM; unused
  `spring-restdocs-mockmvc` removed
- Gap #12 recorded: competitors and matches documented as "full CRUD" with no delete operation
- Project version bumped to 8.7.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Recently Completed (v8.8.0)

- New `DELETE /ipsc/competitors/{competitorId}` and `DELETE /ipsc/matches/{matchId}` endpoints, returning `204`,
  `400` when still referenced or `404` when missing
- Deletes reject rather than cascade: a record still referenced by match results, stage results or shooter logs is
  refused, while what it owns (a competitor's emails, a match's stages) goes with it
- Each delete is flushed inside the service method, so a reference added concurrently still surfaces as a `400`
- New `existsBy…` repository queries back the dependent-row checks
- Gap #12 closed; Gap #13 recorded (Claude Code workflows missing from the CI/CD documentation)
- Project version bumped to 8.8.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Short-term (Minor Releases)

- Wire service/controller/import support for `clubRanking`, `isVisitor`, `ShooterLog` and `ShooterLogCompetitor` —
  currently schema-only (`homeClub` now wired via `IpscCompetitorService`)
- Build a `ShooterLogService` to calculate and persist best-4-match snapshots — no calculation job/service exists yet
- Populate `overallRanking`, `clubRanking` and `isVisitor` during match-result import
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) to an endpoint — still
  groundwork, not yet consumed by any controller
- Add entity-level unit tests for the promoted/extended domain model — repository integration tests exist as of v8.9.0
- Performance optimisation for large-scale match processing
- Enhanced diagnostic logging

### Medium-term (Later v8.x Releases)

- REST API endpoints for enrolled competitor management
- Additional IPSC data format support
- Enhanced error reporting and recovery
- Performance metrics and monitoring
- Advanced query optimisation

### Long-term (Future Major Versions)

- Real-time match result processing
- Enhanced integrations with external systems
- Advanced reporting and analytics

---

## 🎓 Conclusion

The HPSC Website Backend has evolved from a simple image gallery application into a sophisticated, specialised platform
for managing practical shooting competition data. This evolution demonstrates a commitment to:

- **Continuous Improvement:** Regular releases addressing quality, features and standards
- **Domain Alignment:** Progressive refinement toward IPSC compliance and specialisation
- **Architectural Excellence:** Evolution from monolithic to modular, testable architecture with three-tier mapping and
  consolidated service boundaries
- **Standards Adoption:** Adoption of industry-standard practices (SemVer, documentation patterns)
- **Quality Focus:** Investment in comprehensive testing and documentation
- **Code Maintainability:** Systematic refinement of test organisation, consolidation and architectural separation
  (v5.1.0, v5.2.0, v5.3.0, v5.4.0)
- **Type Safety:** Custom JPA converters ensuring explicit, testable enum persistence (v5.3.0)
- **Service Simplicity:** Removal of unnecessary abstractions for cleaner, more cohesive architecture (v5.3.0, v5.4.0)
- **Competitor Enrolment:** First-class tracking of competitor participation through dedicated DTOs and validation
  workflows (v5.4.0)
- **CI/CD Quality Gates:** Qodana JVM static analysis and JaCoCo coverage enforcement raising the quality baseline
  across the entire codebase (v5.4.0)
- **Versioned Match API:** Dedicated `/v2/ipsc/matches` controller providing resource-oriented CRUD separate from the
  bulk-import flow (v6.0.0)
- **Layer Enforcement:** `DomainServiceImpl` no longer reaches past entity services into repositories, fully realising
  the layered architecture (v6.0.0)
- **Club-Scoped Results & Visitor Tracking:** `Club.identifier`, `Competitor.homeClub` and
  `MatchCompetitor.clubRanking`/`isVisitor` model club results and visitors relationally (v7.0.0)
- **Shooter Log Foundations:** `ShooterLog`/`ShooterLogEntry` persist best-4-match snapshots, laying the data model for
  a future ranking calculation service (v7.0.0)
- **Shooter Log Correction:** `ShooterLogEntry` renamed to `ShooterLogCompetitor` and rescoped by `PowerFactor`, keeping
  the data model accurate before a calculation service is built against it (v7.1.0)
- **Test Suite Consistency:** A formalised, repo-wide test-file convention (method-comment headers, group ordering)
  retrofitted across the existing suite, alongside four JaCoCo-identified coverage gaps closed (v7.2.0)
- **IPSC Module Completion:** `IpscCompetitorController`/`IpscMatchController` replace the empty `IpscController` stub
  with full, layered competitor and match CRUD — completing work begun as groundwork in v6.0.0 (v8.0.0)
- **Bulk Import Extended & Validation Correctness:** `IpscCompetitorController.createCompetitors` extends the CSV
  bulk-import pattern to competitors with genuine persistence, and a project-wide fix ensures
  `@JsonProperty(required = true)` actually enforces required fields via matching `@JsonCreator` constructors (v8.1.0)

The transition to Semantic Versioning in v5.0.0, the test suite consolidation in v5.1.0, the major architectural
refactoring in v5.2.0, the service consolidation with custom converters in v5.3.0, the competitor enrolment system with
service transformation in v5.4.0, the dedicated match CRUD API with service encapsulation in v6.0.0, the match
results/visitor tracking/shooter log data model in v7.0.0, the shooter-log naming/scope correction in v7.1.0, the
test-convention formalisation and dependency maintenance in v7.2.0 and the IPSC module's completion with full
competitor/match CRUD in v8.0.0 mark significant maturation points where the project demonstrates stable, predictable
releases with clear separation of concerns. These releases serve as a solid foundation for the shooting club's digital
operations, with a clear commitment to long-term maintainability and quality.

Version 5.3.0 delivers focused, high-value improvements: type-safe JPA converters, correct entity relationships,
optimised repositories and a consolidated service architecture that reduces complexity without sacrificing capability.

Version 5.4.0 extends that foundation with competitor enrolment tracking, SAPSA number validation,
`TransformationService` replacing `IpscMatchService`, a comprehensive package restructure from `ipsc/domain` to
`ipsc/data` and a significant test expansion. All underpinned by Qodana JVM static analysis and JaCoCo code coverage
enforcement that set a new quality baseline for the project.

Version 6.0.0 marks a decisive architectural milestone: `DomainServiceImpl` no longer bypasses the entity service
boundary to reach JPA repositories, `IpscMatchController` establishes a versioned, resource-oriented match API at
`/v2/ipsc/matches` and the IPSC model packages are restructured under `models/ipsc/common/` and `models/ipsc/match/` —
providing clear, scalable homes for shared and match-specific models as the domain continues to grow.

Version 7.0.0 extends the domain model with club-scoped results, visitor tracking and a persisted shooter log: the six
entities parked under `domain/old/` are promoted back into `domain`, `Club`, `Competitor` and `MatchCompetitor` gain the
fields needed to model home clubs, club rankings and match visitors relationally, `MatchStageCompetitor` is repointed to
`MatchCompetitor` to support multiple firearm-type entries per competitor and the new `ShooterLog`/`ShooterLogEntry`
entities persist best-4-match snapshots — all paired with a `repositories/` package rebuilt from scratch. This release
is deliberately domain-layer groundwork; the service, controller and import-pipeline wiring to make these fields
load-bearing remains for a future release.

Version 7.1.0 is a focused follow-up to v7.0.0's shooter-log data model: `ShooterLogEntry` is renamed to
`ShooterLogCompetitor` for naming accuracy, `ShooterLog` gains a `powerFactor` column so best-4-match snapshots are
scoped correctly and `ShooterLogCompetitor` gains `points` and a direct `match` reference. Both tables remain
schema-only — still no calculation service consumes them — so this release is about getting the shape right before that
service is built. Alongside the schema work, the release also migrates the repository's AI-agent prompt files to Claude
Code commands, adopts GitFlow branching and adds `CONTRIBUTING.md`.

Version 7.2.0 touches no domain model, repository or API surface at all — it is entirely process, tooling and dependency
maintenance. A new AGENTS.md test convention (a one-line `// methodName()` header before each method's test group,
ordered constructors → public → protected → alphabetical → `toString()` last) is retrofitted across 26 existing test
files, four JaCoCo-identified coverage gaps are closed (raising suite coverage from 95.7%/91.7% to 97.3%/98.1%). Two
new Claude Code commands (`/scaffold-unit-tests`, `/scaffold-integration-tests`) are also added to keep future test
scaffolding consistent with these conventions automatically. The release also upgrades the Spring Boot parent to 4.1.0,
cleaning up several dependency-version overrides that had quietly become redundant or, in one case, never actually
worked (a typo'd property name) and adds an explicit Git Workflow section to CLAUDE.md stating GitFlow's PR targets
directly (`feature/*` → `develop`; `release/*`/`hotfix/*` → `main`) instead of deferring entirely to AGENTS.md.

Version 8.0.0 completes the IPSC module rebuild that v6.0.0 first began: `IpscController`'s long-standing empty stub is
replaced by `IpscCompetitorController`/`IpscMatchController`, backed by new `IpscCompetitorService`/`IpscMatchService`
implementations, real competitor and match CRUD with club/gender/firearm-type/match-category resolution and the
largest test expansion since v5.4.0. Alongside the domain work, the release also merges `CLAUDE.md`'s guidance into a
single `AGENTS.md` reference. Also migrates the project's AI-agent tooling from slash commands to Skills and re-adds Qodana
JVM static analysis — marking the transition from a project with significant architectural groundwork to one with a
genuinely complete, if still growing, IPSC feature set.
