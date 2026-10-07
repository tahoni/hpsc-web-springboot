# HPSC Website Backend History Archive (v8.0.0 – v9.1.0)

The per-version narrative history of versions 8.0.0 to 9.1.0 of the HPSC Website Backend project, archived from
[`../../../HISTORY.md`](/HISTORY.md) to keep that file a manageable size. The entries are moved unchanged, grouped under
the `../../../HISTORY.md` section each came from. See [`HISTORY.md`](/HISTORY.md) for the sections that span every
version (Feature Timeline, Key Learnings, Project Philosophy Evolution and Conclusion) and for versions 10.0.0 onwards,
[`CHANGELOG_v8-v9.md`](/documentation/archive/v8-v9/CHANGELOG_v8-v9.md) for the archived change log,
[`EVOLUTION_OVERVIEW_v8-v9.md`](/documentation/archive/v8-v9/EVOLUTION_OVERVIEW_v8-v9.md) for the archived
Phase-by-phase narrative, and [`HISTORY_v1-v7.md`](/documentation/archive/v1-v7/HISTORY_v1-v7.md) for versions 1.0.0 to
7.4.1.

---

## Table of Contents

- [📅 Historical Timeline](#-historical-timeline)
- [🎯 Major Milestones](#-major-milestones)
- [🏛️ Architectural Evolution](#-architectural-evolution)
- [🛤️ Future Roadmap Implications](#-future-roadmap-implications)

---

## 📅 Historical Timeline

### Version 9.1.0 (October 3, 2026)

**Theme:** Match Competitor API & Shooter Log Rework

**Key Focus:**

- New `IpscMatchCompetitorController` at `/ipsc/match-competitors`, backed by `IpscMatchCompetitorService` and its
  implementation, with `MatchCompetitorRequest`, `MatchCompetitorPatchRequest` and `MatchCompetitorResponse`: create,
  replace (`PUT`), patch, get one or all, and delete a competitor's entry in a match. A competitor can have one entry
  per match and firearm type, and `competitorCategory` is a list in all three models
- A bulk `POST /ipsc/match-competitors/bulk` endpoint takes `text/csv` through the new `MatchCompetitorRequestCsvMixIn`
  and returns a `MatchCompetitorResponseHolder`; every row is checked before any is saved, so either every row is
  created or none is, and a row that duplicates an existing entry, or another row, is refused
- `MatchCompetitorRequest` gains a `@JsonCreator` constructor with a `@JsonProperty` on every parameter, and a
  `matchCompetitorId`; `MatchRequest`'s constructor and the match CSV mix-in stop requiring `matchFirearmType` and
  `matchCategory`, so a JSON body or CSV row that omits them is read rather than rejected
- A match competitor can have several competitor categories, held in the new `match_competitor_category` table, and a
  shooter log competitor likewise in `shooter_log_competitor_category`; `MatchCompetitor.division` and
  `competitorCategory` are required and `firearmType` becomes optional
- `ShooterLog` is reworked to a date range linked to many matches through `shooter_log_match`; the new
  `ShooterLogOverall` entity holds a rank and points per competitor category and division; `ShooterLogCompetitor`
  links to its `Competitor` directly and gains `dateCalculated`, `competitorCategory` and `division`
- New optional `Competitor.isVerified` flag, on `CompetitorRequest` (JSON and CSV), `CompetitorPatchRequest` and
  `CompetitorResponse`, with existing competitors backfilled to `true`; `emailAddresses` now follows
  `cellphoneNumber` in the competitor models
- `TransactionService` gains `saveMatchCompetitor`, `saveMatchCompetitors` and `deleteMatchCompetitor`; deleting a
  match is also refused while it is linked to a shooter log, and deleting a competitor while they are in a shooter
  log or have an overall row
- Eleven Flyway migrations, `V8_0_0` to `V8_9_0`, carry the schema changes
- The release's improvement-plan sweep found no new gaps and closed none; Gap #6 moves to partially completed, since the
  match competitor half of the scoring layer now exists and the shooter-log half does not, leaving no open gap, and
  Gap #26 now waits on a Spring Boot release that manages Tomcat `11.0.26`
- Scoped as `v9.1.0` **MINOR**: new endpoints and new optional fields with nothing removed or made stricter
- Project version bumped to 9.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`
- The `mysql-connector-j` pin is dropped, since Spring Boot `4.1.1` now manages the newer `9.7.0`
- `AGENTS.md`'s Release Checklist and the `prep-version-release` skill gain a step that aligns the Markdown tables in
  the files a release touches, before `RELEASE_NOTES.md` is archived

### Version 9.0.0 (October 1, 2026)

**Theme:** Match Stage Removal & CSV Import via Jackson Mix-Ins

**Key Focus:**

- `IpscMatchStage` and `MatchStageCompetitor` removed together with their repositories, the `IpscMatch.stages`
  collection, `MatchStageRequest`/`MatchStageResponse`, the `stages` field on `MatchRequest`/`MatchResponse` and the
  match CSV import's `Stages` column; the new `V7_9_0__drop_ipsc_match_stage.sql` migration drops the
  `match_stage_competitor` and `ipsc_match_stage` tables, discarding any existing stage data
- `TransactionService.saveMatch(IpscMatch, List, StageSaveMode)` and the `StageSaveMode` enum removed with the stage
  replace/upsert logic, leaving `saveMatch(IpscMatch)` as the only single-match save; `IpscMatchService.deleteMatch`
  no longer checks for stage results
- The CSV import models are replaced by Jackson mix-ins: new `MatchRequestCsvMixIn` and `CompetitorRequestCsvMixIn` bind
  the CSV column headers onto `MatchRequest`'s and `CompetitorRequest`'s constructors, so each import reads a row
  straight into the request model. `MatchRequestForCSV`, `CompetitorRequestForCSV` and the `toRequest` copy step in
  `IpscMatchServiceImpl` go; `IpscCompetitorServiceImpl.toRequest` becomes `normaliseCsvRequest`, keeping the row's
  `competitorId`
- Both CSV imports now accept a header that omits optional columns and ignore unknown columns; only `MatchDate`,
  `MatchName`, `MatchFirearmType` and `MatchCategory` (matches) and `FirstName` and `LastName` (competitors) must be
  present. `CompetitorRequest.emailAddresses` defaults to an empty list and the import splits the `EmailAddresses`
  cell itself
- `MatchRequest.matchFirearmType` and `matchCategory` become required properties, so a JSON body that leaves either
  out is rejected when it is read
- New `MatchPatchRequest` and `CompetitorPatchRequest` replace the full request models on
  `PATCH /ipsc/matches/{matchId}` and `PATCH /ipsc/competitors/{competitorId}`: no field is required, and a field
  left out is left unchanged
- `IpscCompetitorServiceImpl.resolveHomeClub` also resolves a home club by abbreviation when no club has a matching
  name
- The `MYSQL_USER` environment variable is no longer read by `application.properties`; the `dev` and `prod` profiles
  set the datasource username themselves, and `mysql-connector-j` is pinned to `9.4.0`
- The release's improvement-plan sweep found no new gaps and closed none; Gap #6's evidence now notes that the stage
  repositories it listed are gone, and Gap #6 remains the only open gap, with #26 still waiting on a Spring Boot
  release that manages Tomcat `11.0.25`
- Scoped as `v9.0.0` **MAJOR**: `stages` is removed from the match request and response, two entities and their
  tables are dropped, `matchFirearmType` and `matchCategory` become required and `MYSQL_USER` is no longer read
- Project version bumped to 9.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`
- Vulnerable dependencies addressed in `pom.xml`: `tomcat.version` raised to `11.0.26`, a new `logback.version`
  override of `1.6.5`, the Jackson BOM properties raised, and `flyway-mysql` pinned above the
  Spring Boot-managed versions

### Version 8.12.0 (September 29, 2026)

**Theme:** Competitor CSV Import Casing Normalisation

**Key Focus:**

- New `StringUtils.toProperCase`, backed by the new `org.apache.commons:commons-text` dependency's `WordUtils`,
  upper-cases the first letter of each word and lower-cases the rest, treating spaces, hyphens and apostrophes as
  word breaks
- New `za.co.hpsc.web.helpers` package and its first class, `CompetitorHelpers`, lower-cases surname particles
  (`van`, `der`, `du`, `de`, `le` and the like) when they precede the surname proper, so `Van Der Merwe` becomes
  `van der Merwe` (also after a hyphen), and capitalises the letter after a Gaelic `Mc` prefix (`McDonald`),
  without touching a surname that merely starts with particle letters (`Dube`, `Vanderbilt`) or a Zulu `Mch`/`Mcu`
  surname (`Mchunu`)
- `IpscCompetitorServiceImpl.toRequest` now proper-cases the competitor CSV import's free-text columns
  (`FirstName`, `LastName`, `MiddleNames`, `Nickname`, `Gender`) before passing the `LastName` through the new
  particle-casing helper, so imported names read consistently regardless of how they were typed in the source
  spreadsheet; `HomeClub`, `ClubNumber`, `CompetitorNumber`, `IdNumber`, `CellphoneNumber` and `EmailAddresses` are
  kept as supplied, and the JSON create/update/patch endpoints are unaffected
- `DateUtil`, `NumberUtil`, `StringUtil` and `ValueUtil` renamed to `DateUtils`, `NumberUtils`, `StringUtils` and
  `ValueUtils` (and their test classes to match) — internal classes only, so there is no API change
- New `.github/workflows/code_quality.yml` runs Qodana static analysis on every GitFlow branch and PRs into
  `main`/`develop`, and the CodeQL and dependency-submission workflows' push triggers now cover `release/*`,
  `feature/*`, `bugfix/*` and `hotfix/*` too
- Dependabot's `github-actions` group update (PR #152) bumps `docker/setup-buildx-action` to `v4` and
  `docker/build-push-action` to `v7` in `docker.yml`, merged into `develop` and then into the release branch
- `AGENTS.md` and `CONTRIBUTING.md` add `bugfix/<short-description>` as its own standard GitFlow branch type
- The release's improvement-plan sweep recorded Gap #32 for Qodana's return to CI, which the plan still described as
  removed, and closed it — Gap #6 remains the only open gap, with #26 still waiting on a Spring Boot release that
  manages Tomcat `11.0.25`
- Scoped as `v8.12.0` **MINOR**: a backward-compatible new import-formatting capability, with no existing endpoint,
  request/response contract or configuration property changed
- Project version bumped to 8.12.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.11.1 (September 27, 2026)

**Theme:** Docker Image Build in CI & Branch Coverage Gate

**Key Focus:**

- New `.github/workflows/docker.yml` builds the `Dockerfile` with `docker/build-push-action` on every push and PR to
  `main`/`develop` — build only, never pushed — so a change that breaks the image fails CI instead of surfacing at
  deployment, closing the Known Issue v8.11.0 shipped with
- Layers are reused across runs through the GitHub Actions cache; tests stay in `build.yml`, since the `Dockerfile`
  skips them
- `ARCHITECTURE.md`'s CI/CD & Quality Gates table gains a Docker Image row, and `CONTRIBUTING.md`'s summary of it
  names the new gate
- `pom.xml`'s JaCoCo `check` execution gains a 97% `BRANCH` minimum beside the existing `LINE` one, ending the
  line-only deviation Gap #4 recorded and the Known Issue carried since v8.4.0; branch coverage stands at 99.09%
- The release's improvement-plan sweep recorded Gap #31 for the untracked Docker CI gap and closed it — Gap #6
  remains the only open gap, with #26 still waiting on a Spring Boot release that manages Tomcat `11.0.25`
- Scoped as `v8.11.1` **PATCH**: CI, build tooling and documentation only, with no change to the API,
  configuration or schema
- Project version bumped to 8.11.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.11.0 (September 27, 2026)

**Theme:** Docker Deployment, Actuator Health Checks & Flyway at Startup

**Key Focus:**

- New multi-stage `Dockerfile`: built with the Maven wrapper on a JDK 25 image, run as a non-root user on a Java 25
  JRE from Spring Boot's extracted JAR layers, under the `prod` profile by default, with `SPRING_DATASOURCE_URL`,
  `MYSQL_USER` and `MYSQL_PASSWORD` supplied at run time
- New `docker-compose.yml` runs the application against a MySQL 8.4 container, with credentials from a gitignored
  `.env` copied from `.env.example`, named volumes for the database and logs, and a health-gated start order
- New `spring-boot-starter-actuator` dependency exposes `/hpsc-web/actuator/health`, including a database check,
  which the image's `HEALTHCHECK` polls
- Bringing up the Compose database exposed a long-standing defect: Spring Boot 4 moved Flyway's auto-configuration
  into its own module, so with only `flyway-core` on the classpath Flyway had never run at startup and every
  `spring.flyway.*` property was ignored; `spring-boot-starter-flyway` replaces it
- The `prod` profile now baselines a hand-built schema without Flyway's history at `7.0.0`, as `local` already did,
  so production's first start applies `V7_1_0` onwards rather than failing on the existing tables
- The release's improvement-plan sweep recorded Gap #30 for the Flyway drift and closed it — Gap #6 remains the only
  open gap, with #26 still waiting on a Spring Boot release
- Scoped as `v8.11.0` **MINOR** for the new health endpoint and Docker setup; no existing API, configuration property
  or schema changes
- Project version bumped to 8.11.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.10.2 (September 26, 2026)

**Theme:** Claude Code Review for Dependabot PRs & First Dependabot Updates

**Key Focus:**

- `.github/workflows/claude-code-review.yml` gains `allowed_bots: 'dependabot'`, so the Claude code review now runs
  on Dependabot's version- and security-update PRs, which it previously skipped as bot-authored
- Dependabot-triggered runs can only read Dependabot secrets, so `CLAUDE_CODE_OAUTH_TOKEN` must also be stored as a
  Dependabot secret — documented in `ARCHITECTURE.md`'s CI/CD & Quality Gates section
- Shipped as its own release rather than folded into the already-shipped v8.10.1, per `AGENTS.md`'s rule that a
  released version's contents are never changed
- Dependabot's first three PRs landed on `develop`: GitHub Actions `checkout` `v7`, `setup-java` `v6` and
  `upload-artifact` `v7`; springdoc `3.1.1`, JaCoCo `0.8.15` and Maven `3.9.16` (the regenerated wrapper also makes
  `mvnw` executable, so `dependency-submission.yml`'s `chmod` step is dropped); and `flyway-mysql` `13.7.0`
- That last bump put `flyway-mysql` 13.7.0 beside Spring Boot's 12.4.0 `flyway-maven-plugin`, mixing two Flyway
  majors; the plugin dependency now uses `${flyway.version}`, inherited from the parent, so it can't drift again
- Scoped as `v8.10.2` **PATCH**: CI only, with no change to the API, configuration or schema
- Project version bumped to 8.10.2 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.10.1 (September 26, 2026)

**Theme:** Explicit Dependency Submission & Dependabot Configuration

**Key Focus:**

- New `.github/workflows/dependency-submission.yml` replaces GitHub's built-in "Automatic Dependency Submission
  (Maven)" — the one check on `develop`→`main` PRs with no workflow file behind it — resolving the dependency
  graph with the project's own JDK 25 and Maven wrapper instead of the built-in JDK 21 without the wrapper; the
  built-in submission must be turned off in the repository's Code security settings
- New `.github/dependabot.yml` enables weekly Maven and GitHub Actions version updates, opened against `develop`,
  with Maven minor/patch bumps and all Actions bumps each grouped into one PR
- Dependabot security-update PRs, which always target `main`, are handled as hotfixes: `AGENTS.md`'s Branching
  Model and `CONTRIBUTING.md`'s Merging section name them as the only exception besides `hotfix/*`, merged into
  `main` and then carried into `develop` by merging `main` back into it
- The release's own improvement-plan sweep recorded Gap #29 for that branching conflict and closed it — Gap #6
  remains the only open gap, with #26 still waiting on a Spring Boot release
- Scoped as `v8.10.1` **PATCH**: CI and documentation only, with no change to the API, configuration or schema
- Project version bumped to 8.10.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.10.0 (September 26, 2026)

**Theme:** Strict Semantic Versioning, Production Profile & Roadmap Gap Closure

**Key Focus:**

- `AGENTS.md` makes Semantic Versioning a strict rule: a new Semantic Versioning subsection defines what counts as a
  MAJOR, MINOR or PATCH change for this project, how a release is classified from `CHANGELOG.md`'s `[Unreleased]`
  section, and that backward-incompatible entries are flagged `**Breaking:**` as they land
- The `prep-version-release` skill validates the requested version against those rules before bumping and re-checks
  it after syncing `[Unreleased]`; `generate-commit-message` and `sync-unreleased-changes` apply and audit the
  `**Breaking:**` prefix, and the latter reports the release level `[Unreleased]` implies
- The Release Checklist's `pom.xml` step now re-checks every manual dependency-version override against the Spring
  Boot parent's own `spring-boot-dependencies` POM — a check the improvement plan had assumed existed but no step
  performed
- New `application-prod.properties` gives production its own `prod` profile (`localhost:3306/hpsc_prod`); the
  database-profile docs now match what the properties files configure, including the no-profile run's externally
  supplied datasource URL and the `local` profile's own credentials
- `logback-spring.xml`'s undocumented `staging` profile removed
- New `IpscMatchTest` guards `IpscMatch.stages`' exclusion from Lombok's `toString`/`equals`/`hashCode`, which
  otherwise recurse through `IpscMatchStage.match`; 966 → 970 tests
- Three `update-improvement-plan-gaps` sweeps recorded Gaps #25–#28: #25, #27 and #28 closed, and #26 (the
  `tomcat.version` override) progressed until a Spring Boot release manages Tomcat `11.0.25` — Gap #6 remains open
- `CHANGELOG.md`, `RELEASE_NOTES.md` and the archived v8.7.0/v8.9.0 release documents now list Removed after Fixed,
  matching `AGENTS.md`'s category order
- Scoped as `v8.10.0` **MINOR** under the new rules for the optional `prod` profile — the first release classified by
  them rather than by precedent
- Project version bumped to 8.10.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 8.9.0 (September 26, 2026)

**Theme:** Competitor Paid-Up Flags, Explicit Transaction Boundary & Lazy Loading

**Key Focus:**

- `Competitor` gains nullable `paidUpSapsa`/`paidUpClub` flags via `V7_8_0__add_competitor_paid_up_flags.sql`,
  wired through `CompetitorRequest`, `CompetitorRequestForCSV` and `CompetitorResponse`; the competitor CSV bulk
  import now requires trailing `PaidUpSapsa`/`PaidUpClub` header columns (values may be blank) — existing CSV files
  need the two columns added
- Every `@ManyToOne` association switched back from `FetchType.EAGER` to `FetchType.LAZY`, reversing v8.7.0; since
  `spring.jpa.open-in-view` is disabled, new `left join fetch` repository queries
  (`IpscMatchRepository.findByIdWithClub`/`findAllWithClub`,
  `CompetitorRepository.findByIdWithHomeClubAndEmailAddresses`/`findAllWithHomeClubAndEmailAddresses`) load what
  each response reads
- New `TransactionService`/`TransactionServiceImpl` commits every competitor and match write in its own explicit
  `TransactionTemplate` transaction; `IpscCompetitorServiceImpl`/`IpscMatchServiceImpl` drop `@Transactional`,
  validating and building entities outside any transaction. Bulk CSV imports now build every row before saving any,
  then save all rows in one transaction
- `IpscMatch` gains a cascaded, orphan-removing `@OneToMany(mappedBy = "match")` `stages` collection — the domain
  model's only bidirectional relationship — so deleting a match removes its stages by cascade
- Create/update/patch match responses now list stages ordered by stage number, matching `getMatch`
- Unused `jackson-dataformat-xml`/`commons-lang3` dependencies removed
- 63 new tests (903 → 966): `TransactionService`'s full 3-tier split, a new `repositories/` package of repository
  integration tests (fetch-join queries, the stage cascade, every `existsBy…` check) and service integration tests
  run without a surrounding transaction to prove each write really commits; coverage 98.77%/99.09% line/branch
- Closed `improvement-plan.md`'s Gaps #13–#17 (documentation drift) and, from this release's own audit, Gaps #18–#24
  (unused dependencies, stale `ARCHITECTURE.md` patterns/data flows/repository descriptions, the 3-tier test rule,
  `AGENTS.md`'s club name, missing repository tests and the `homeClub` backfill, closed as not applicable) — only
  Gap #6 remains open
- `AGENTS.md`'s Release Checklist now makes the Future Roadmap Implications log and "Major Version Goals" mandatory
  per release; both were backfilled through v8.8.0
- Scoped as `v8.9.0` **MINOR** for the new competitor fields, with the competitor CSV's two new required header
  columns called out as a client-facing migration — matching the v8.5.0/v8.7.0 precedent
- Project version bumped to 8.9.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

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
  `documentation/EVOLUTION_OVERVIEW.md` — it had grown to roughly half of `HISTORY.md`'s 4,095 lines,
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
- New `AGENTS.md` conventions: Member ordering (constructors → public → protected → private),
  REST URL/handler-naming rules condensed from `standard-rest-conventions.md`, and a Release Checklist step verifying
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

---

## 🎯 Major Milestones

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
- JaCoCo `LINE`/`COVEREDRATIO` floor tightened `0.86` → `0.97`, confirmed holding at a fresh 98.44%/98.98% (line/branch,
  868 tests) baseline, closing Gap #4
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

- `HISTORY.md`'s "📖 Evolution Overview" section split out into new `documentation/EVOLUTION_OVERVIEW.md`,
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

### Milestone 35: Competitor Paid-Up Flags & Explicit Transaction Boundary (v8.9.0)

- Competitors record whether their SAPSA and club memberships are paid up, through the API and CSV import alike
- Competitor and match writes are committed only by `TransactionService`, in explicit transactions, with bulk
  imports all-or-nothing
- Lazy association loading paired with fetch-join queries, so responses never depend on an open session
- Gaps #13–#24 closed, leaving only the scoring/shooter-log layer (Gap #6) open

**Achievement:** Made the persistence layer's transaction and loading behaviour explicit and tested at every tier,
while clearing the improvement plan of every documentation-accuracy gap.

### Milestone 36: Strict Semantic Versioning & Production Profile (v8.10.0)

- Semantic Versioning becomes a strict, documented rule, enforced by the release skill and flagged in the CHANGELOG as
  breaking changes land
- Production gets its own `prod` profile, and the profile documentation matches the properties files
- Manual dependency-version overrides are re-checked at every release
- Gaps #25, #27 and #28 closed, and #26 progressed, leaving Gap #6 the only open gap

**Achievement:** Turned the project's version numbers from a matter of precedent into a rule the release process
checks, and made the documented runtime profiles match the ones that actually exist.

### Milestone 37: Explicit Dependency Submission & Dependabot Configuration (v8.10.1)

- Every check on a `develop`→`main` PR now comes from a workflow file in the repository
- Dependabot version updates configured, targeting `develop`
- Dependabot security PRs given a place in the GitFlow model, as hotfixes
- Gap #29 recorded and closed

**Achievement:** Brought the repository's dependency tooling under version control and fitted it into the branching
model, rather than leaving it to GitHub's defaults.

### Milestone 38: Claude Code Review for Dependabot PRs & First Dependabot Updates (v8.10.2)

- The automated Claude code review extends to Dependabot's PRs, including the security updates merged straight into
  `main`
- Dependabot's first updates merged; the one that broke a hand-kept version sync (`flyway-mysql`) was fixed by
  deriving the version from Spring Boot instead
- The first release shipped as a follow-up PATCH under the rule that a released version is never changed

**Achievement:** Gave Dependabot's PRs — including those that bypass `develop` — the same automated review as
everyone else's.

### Milestone 39: Docker Deployment, Actuator Health Checks & Flyway at Startup (v8.11.0)

- The application ships as a Docker image, with a Compose setup that runs it against its own MySQL database
- An Actuator health endpoint gives deployments, and the image itself, something to poll
- Flyway migrations run at startup for the first time since the move to Spring Boot 4, and production's hand-built
  schema is baselined rather than rejected
- Gap #30 recorded and closed

**Achievement:** Gave the application a reproducible way to run outside a developer's machine, and in doing so found
and fixed the schema migrations the documentation had long assumed were running.

### Milestone 40: Docker Image Build in CI & Branch Coverage Gate (v8.11.1)

- The Docker image is built on every push and PR, giving the `Dockerfile` the same automatic gate as the Maven build
- The one Known Issue v8.11.0 introduced is closed in the very next release
- The coverage gate enforces branch coverage as well as line coverage, as Gap #4 originally proposed
- Gap #31 recorded and closed

**Achievement:** Made a broken image fail a pull request rather than a deployment, and a branch-coverage regression
fail the build rather than slip through.

### Milestone 41: Competitor CSV Import Casing Normalisation (v8.12.0)

- The competitor CSV import proper-cases its free-text columns and lower-cases surname particles, so imported names
  read consistently regardless of how they were typed in the source spreadsheet
- The new `za.co.hpsc.web.helpers` package establishes a home for competitor-specific normalisation logic, separate
  from the general-purpose `za.co.hpsc.web.utils` classes it builds on
- `Util` classes renamed to `Utils`, closing a naming inconsistency with the rest of the codebase's plural utility
  class names
- A Qodana static-analysis workflow, and CodeQL/dependency-submission triggers widened to every GitFlow branch, bring
  CI feedback to the branch where a problem is introduced

**Achievement:** Made bulk-imported competitor data consistent to read regardless of how the source spreadsheet was
typed, without changing the CSV format or JSON endpoints consumers already rely on.

### Milestone 42: Match Stage Removal & Mix-In CSV Binding (v9.0.0)

- The stage model — `IpscMatchStage`, `MatchStageCompetitor`, their repositories, requests, responses and the
  `IpscMatch.stages` collection — is removed, along with the tables behind it, so a match is described by its own
  fields and its competitor results alone
- The two bulk CSV imports bind their columns onto the request models through Jackson mix-ins instead of copying from
  dedicated CSV models, so there is one request model per resource, and both tolerate optional columns being left out
- `PATCH` on matches and competitors takes dedicated patch models with no required fields, keeping a partial update
  partial now that the full request models require more
- Project's first MAJOR release since v8.0.0, with its breaking changes flagged `**Breaking:**` in `CHANGELOG.md` as
  they landed

**Achievement:** Removed the match stage model and the duplicate CSV models in one release, flagging each
backward-incompatible change as it landed so the MAJOR classification was visible before the release was cut.

### Milestone 43: Match Competitor API (v9.1.0)

- Match competitors, the entries that hold a competitor's results in a match, get their own service and controller,
  with create, replace, patch, get, delete and a bulk CSV import, closing the first half of the scoring layer that
  Gap #6 tracks
- The bulk import reuses the pattern the competitor and match imports established — a Jackson mix-in binding the CSV
  columns onto the request model — and adds an all-or-nothing batch save that refuses a duplicate within the file
- A match competitor and a shooter log competitor can each have several competitor categories, and the shooter log
  entities are reworked around a date range of matches, ready for the shooter-log service that remains
- Project's first MINOR release since v8.12.0, with nothing removed or made stricter

**Achievement:** Built the match competitor endpoints on the existing pattern and extended the CSV imports to a third
resource, without breaking any existing caller.

---

## 🏛️ Architectural Evolution

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

### v8.9.0: Explicit Transaction Boundary & Lazy Persistence

```
IpscCompetitorController           IpscMatchController
        ↓                                  ↓
IpscCompetitorService              IpscMatchService
   (validate, build — no transaction)      (validate, build — no transaction)
        ↓ reads                            ↓ reads
CompetitorRepository               IpscMatchRepository / IpscMatchStageRepository
   (fetch-join queries)               (fetch-join queries)
        ↓ writes                           ↓ writes
                 TransactionService
        (explicit TransactionTemplate transactions)
```

**Characteristics:**

- One class, `TransactionService`, owns every competitor/match commit; the IPSC services no longer declare
  `@Transactional` at all
- Every `@ManyToOne` is `LAZY`, and reads that feed a response load their associations up front through
  `left join fetch` queries, since `spring.jpa.open-in-view` stays disabled
- `IpscMatch.stages` is the domain model's single bidirectional, cascaded relationship — composition, since a stage
  can't exist without its match — while every other relationship stays unidirectional and reject-not-cascade

### v9.0.0: Stage-Free Match Model & Mix-In CSV Binding

```
CSV row ──(CsvMapper + *RequestCsvMixIn)──→ MatchRequest / CompetitorRequest
                                                     ↓
IpscMatchController / IpscCompetitorController → IpscMatchService / IpscCompetitorService
                                                     ↓
                                    TransactionService.saveMatch(IpscMatch)
```

**Characteristics:**

- `IpscMatch` no longer owns any child collection, so the one bidirectional, cascaded relationship that v8.9.0
  described is gone, and every remaining relationship is unidirectional and reject-not-cascade
- A CSV row is read straight into the same request model a JSON body uses, through a mix-in that supplies the column
  names, so a field added to a request model is picked up by both entry points without a second model to keep in step
- Each `PATCH` has its own request model with no required fields, while `POST` and `PUT` keep the full model with its
  required fields

### v9.1.0: Match Competitor API & Shooter Log Rework

```
CSV row ──(CsvMapper + MatchCompetitorRequestCsvMixIn)──→ MatchCompetitorRequest
                                                              ↓
IpscMatchCompetitorController → IpscMatchCompetitorService → MatchCompetitorRepository
                                                              ↓
              TransactionService.saveMatchCompetitor / saveMatchCompetitors / deleteMatchCompetitor
```

**Characteristics:**

- A third resource, the match competitor, follows the competitor and match pattern: an interface and implementation
  service, a controller, a full request model for `POST` and `PUT`, a patch request model with no required fields, and
  a CSV mix-in over the same request model
- A batch import checks every row, including against the other rows, before saving, and commits through one
  `TransactionService` transaction that flushes inside it, so a constraint violation surfaces as a 400
- Competitor categories are a child table per entity rather than a column, so one entry can carry several
- `ShooterLog` no longer owns a competitor, club or firearm type; it is a date range linked to matches, with
  `ShooterLogCompetitor` and `ShooterLogOverall` linked to `Competitor` directly

---

## 🛤️ Future Roadmap Implications

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

- `HISTORY.md`'s Evolution Overview split out into `documentation/EVOLUTION_OVERVIEW.md`
- All 52 archived release notes/PR descriptions regrouped into `../v1-v4/v1`–`v8/` by major version,
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

### Previously Completed (v8.8.0)

- New `DELETE /ipsc/competitors/{competitorId}` and `DELETE /ipsc/matches/{matchId}` endpoints, returning `204`,
  `400` when still referenced or `404` when missing
- Deletes reject rather than cascade: a record still referenced by match results, stage results or shooter logs is
  refused, while what it owns (a competitor's emails, a match's stages) goes with it
- Each delete is flushed inside the service method, so a reference added concurrently still surfaces as a `400`
- New `existsBy…` repository queries back the dependent-row checks
- Gap #12 closed; Gap #13 recorded (Claude Code workflows missing from the CI/CD documentation)
- Project version bumped to 8.8.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.9.0)

- New nullable `Competitor.paidUpSapsa`/`paidUpClub` columns via `V7_8_0__add_competitor_paid_up_flags.sql`; the
  competitor CSV now requires `PaidUpSapsa`/`PaidUpClub` header columns
- `@ManyToOne` associations switched to `FetchType.LAZY`, with fetch-join repository queries loading what responses
  read
- New `TransactionService` commits every competitor/match write in an explicit transaction; bulk imports save all
  rows in one transaction
- `IpscMatch.stages` becomes a cascaded `@OneToMany`, the domain model's only bidirectional relationship
- Unused `jackson-dataformat-xml`/`commons-lang3` dependencies removed
- New repository integration tests and `TransactionService` test tiers; 903 → 966 tests
- Gaps #13–#24 closed, leaving only Gap #6 open
- Project version bumped to 8.9.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.10.0)

- New Semantic Versioning subsection in `AGENTS.md`; `prep-version-release` validates each release's version against
  it, and `**Breaking:**` CHANGELOG entries are flagged as they land
- The Release Checklist re-checks every manual `pom.xml` dependency-version override at each release
- New `prod` profile (`application-prod.properties`); database-profile docs corrected; unused `staging` logging
  profile removed
- New `IpscMatchTest` guarding the `IpscMatch.stages` Lombok exclusions; 966 → 970 tests
- Gaps #25, #27 and #28 closed and #26 progressed, leaving only Gap #6 open
- Project version bumped to 8.10.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.10.1)

- New `.github/workflows/dependency-submission.yml` replaces GitHub's built-in Maven dependency submission, using the
  project's own JDK 25 and Maven wrapper
- New `.github/dependabot.yml`: weekly, grouped Maven and GitHub Actions version updates targeting `develop`
- Dependabot security-update PRs handled as hotfixes in `AGENTS.md`'s and `CONTRIBUTING.md`'s branching rules
- Gap #29 recorded and closed, leaving only Gap #6 open
- Project version bumped to 8.10.1 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.10.2)

- The Claude code review runs on Dependabot's PRs (`allowed_bots: 'dependabot'`), with `CLAUDE_CODE_OAUTH_TOKEN`
  also needed as a Dependabot secret
- Dependabot's first updates: GitHub Actions to `checkout@v7`/`setup-java@v6`/`upload-artifact@v7`, springdoc
  `3.1.1`, JaCoCo `0.8.15`, Maven `3.9.16`
- `flyway-mysql` in the Flyway plugin now follows `${flyway.version}`, fixing Dependabot's mismatched `13.7.0` bump
- Project version bumped to 8.10.2 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.11.0)

- New multi-stage `Dockerfile` (JDK 25 build, non-root Java 25 JRE runtime from Spring Boot's JAR layers, `prod`
  profile by default) and `docker-compose.yml` running it against MySQL 8.4, with `.env.example` for credentials
- New `spring-boot-starter-actuator` exposing `/hpsc-web/actuator/health`, polled by the image's `HEALTHCHECK`
- `spring-boot-starter-flyway` replaces `flyway-core`, so Flyway migrations run at startup under Spring Boot 4; the
  `prod` profile baselines a hand-built schema at `7.0.0`
- Gap #30 recorded and closed, leaving only Gap #6 open
- Project version bumped to 8.11.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v8.11.1)

- New `.github/workflows/docker.yml` builds the `Dockerfile` on every push and PR to `main`/`develop` (build only,
  never pushed, cached through GitHub Actions), listed in `ARCHITECTURE.md`'s CI/CD & Quality Gates table
- The JaCoCo coverage gate enforces a 97% `BRANCH` minimum beside the `LINE` one
- Gap #31 recorded and closed, leaving only Gap #6 open
- Project version bumped to 8.11.1 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v9.1.0)

- New `IpscMatchCompetitorController`, `IpscMatchCompetitorService` and implementation, and
  `MatchCompetitorRequest`/`MatchCompetitorPatchRequest`/`MatchCompetitorResponse` for `/ipsc/match-competitors`,
  with a bulk `POST /ipsc/match-competitors/bulk` CSV import through the new `MatchCompetitorRequestCsvMixIn` and
  `MatchCompetitorResponseHolder`
- `TransactionService.saveMatchCompetitor`, `saveMatchCompetitors` and `deleteMatchCompetitor`; the match and
  competitor delete checks also cover shooter logs and the new overall rows
- `MatchCompetitor` and `ShooterLogCompetitor` hold a list of competitor categories in child tables, and
  `MatchCompetitor.firearmType` is optional
- `ShooterLog` reworked to a date range linked to matches, with the new `ShooterLogOverall` entity and
  `ShooterLogOverallRepository`, and eleven `V8_0_0` to `V8_9_0` Flyway migrations
- `Competitor.isVerified` added, and `emailAddresses` moved after `cellphoneNumber` in the competitor models
- `MatchRequest.matchFirearmType` and `matchCategory` no longer required, in JSON and CSV
- Improvement plan: no new gaps and none closed; Gap #6 moves to partially completed, with no open gap left, and
  Gap #26 now waits on a Spring Boot release that manages Tomcat `11.0.26`
- `mysql-connector-j` pin dropped now that Spring Boot manages a newer version
- New table-alignment step in `AGENTS.md`'s Release Checklist and the `prep-version-release` skill
- Project version bumped to 9.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v9.0.0)

- `IpscMatchStage`, `MatchStageCompetitor`, their repositories, `MatchStageRequest`/`MatchStageResponse` and the
  `stages` field on `MatchRequest`/`MatchResponse` removed, with the `V7_9_0__drop_ipsc_match_stage.sql` migration
  dropping their tables and `TransactionService.saveMatch`'s stage overload and `StageSaveMode` removed
- New `MatchRequestCsvMixIn` and `CompetitorRequestCsvMixIn` read each CSV row straight into the request model, and
  the imports tolerate omitted optional columns; `CompetitorRequestForCSV` and `MatchRequestForCSV` removed
- New `MatchPatchRequest` and `CompetitorPatchRequest` for the `PATCH` endpoints, and `MatchRequest.matchFirearmType`
  and `matchCategory` now required
- `IpscCompetitorServiceImpl.resolveHomeClub` also resolves a club by abbreviation, and `normaliseCsvRequest` keeps
  the row's `competitorId`
- `MYSQL_USER` no longer read by `application.properties` (the `dev` and `prod` profiles set the username) and
  `mysql-connector-j` pinned to `9.4.0`
- Improvement plan: no new gaps and none closed; Gap #6's evidence notes the removed stage repositories
- Project version bumped to 9.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation
- Dependency vulnerabilities addressed: `tomcat.version` `11.0.26`, `logback.version` `1.6.5`, Jackson BOM properties
  raised and `flyway-mysql` pinned

### Previously Completed (v8.12.0)

- New `StringUtils.toProperCase` (backed by the new `commons-text` dependency) and
  `CompetitorHelpers.toSentenceCaseLastName` (the new `za.co.hpsc.web.helpers` package's first class) normalise casing
  on competitor CSV import
- `IpscCompetitorServiceImpl.toRequest` proper-cases the CSV import's free-text columns and lower-cases surname
  particles, leaving `HomeClub`, `ClubNumber`, `CompetitorNumber` and `EmailAddresses` as supplied
- `DateUtil`, `NumberUtil`, `StringUtil` and `ValueUtil` renamed to `DateUtils`, `NumberUtils`, `StringUtils` and
  `ValueUtils`, closing a naming inconsistency
- New Qodana workflow, CodeQL/dependency-submission triggers widened to every GitFlow branch, and `bugfix/*` documented
  as a standard branch type
- Dependabot bumps `docker/setup-buildx-action` to `v4` and `docker/build-push-action` to `v7` in `docker.yml`
- Gap #32 (the plan still describing Qodana as removed, after `code_quality.yml` brought it back to CI) recorded and
  closed — Gap #6 remains the only open gap, with #26 still waiting on a Spring Boot release that manages Tomcat
  `11.0.25`
- Project version bumped to 8.12.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Short-term (Minor Releases)

- Wire service/controller/import support for `clubRanking`, `isVisitor`, `ShooterLog` and `ShooterLogCompetitor` —
  currently schema-only (`homeClub` now wired via `IpscCompetitorService`)
- Build a `ShooterLogService` to calculate and persist best-4-match snapshots — no calculation job/service exists yet
- Populate `overallRanking`, `clubRanking` and `isVisitor` during match-result import
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) to an endpoint — still
  groundwork, not yet consumed by any controller
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
