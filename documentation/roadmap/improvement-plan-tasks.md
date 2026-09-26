# Roadmap Task List

A concrete, checkbox-level breakdown of [`improvement-plan.md`](improvement-plan.md)'s gaps, grouped by that
document's completion status — ✅ Completed, 🟡 Partially Completed, ⚪ Open — matching its own "🔍 Gaps &
Improvement Opportunities" grouping. Each block names its originating gap number for traceability back to the
evidence and reasoning there; within each section, gaps stay in ascending number order.

## Table of Contents

- [✅ Completed](#-completed)
- [🟡 Partially Completed](#-partially-completed)
- [⚪ Open](#-open)

---

## ✅ Completed

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

**CI build/test gate** *(improvement-plan.md → Gap #2)* — ✅ Closed in v8.3.1

- [x] Add `.github/workflows/build.yml`, triggered on push/PR to `develop` and `main`, mirroring `codeql.yml`'s trigger
  branches
- [x] Run `./mvnw verify -Pcoverage` as the workflow's build step — invoked as `sh ./mvnw ...` rather than a direct
  `./mvnw ...`, since `mvnw` isn't tracked with the execute bit in git and would fail with "Permission denied" on
  the Ubuntu runner otherwise
- [x] Confirm the workflow fails the PR check when a test fails (not just when the build doesn't compile) — the
  new JaCoCo `check` execution (see Gap #4 below) also fails it on a coverage regression, not just a test failure
- [x] Once live, update `ARCHITECTURE.md`'s CI/CD & Quality Gates table to drop the "locally / by reviewers" caveat on
  the `Build & Tests` row — `CONTRIBUTING.md`'s matching table updated too
  
**CSV persistence clarification** *(improvement-plan.md → Gap #3)* — ✅ Closed in v8.3.1

- [x] If persistence is intended: scope it as its own roadmap item once the service layer exists — done for the
  competitor domain specifically: `IpscCompetitorController.createCompetitors` (v8.1.0) is a scoped, deliberate
  persisting-CSV-import feature, and `ARCHITECTURE.md` now contrasts it directly against the Award/Image flow
  ("without persisting anything") in an adjacent data-flow section
- [x] Decide whether `AwardService`/`ImageService` CSV processing itself is meant to stay stateless by design or
  should gain persistence — decided in v8.3.1: confirmed intentionally stateless, no persistence planned
- [x] If stateless by design: state that explicitly in `README.md`/`ARCHITECTURE.md` for `AwardService`/
  `ImageService` themselves, not just by implication via the new competitor flow — done in v8.3.1: `README.md`'s
  Award Ceremonies/Image Gallery bullets and `ARCHITECTURE.md`'s Service Layer table/Award-Image CSV Processing
  Flow section now say so explicitly

**Coverage enforcement** *(improvement-plan.md → Gap #4)* — ✅ Closed in v8.4.0

- [x] ~~Baseline the real coverage figure before setting a rule~~ — done: targeted tests for the exception
  hierarchy, the `models/ipsc/shared` scoring classes and every untested `patchCompetitor`/`patchMatch` field
  success-path brought the suite from 92.9%/93.4% to 98.34%/98.84% (line/branch), 746 → 775 tests
- [x] Add a JaCoCo `<rule>` (line/branch minimum near the current baseline) to the `coverage` Maven profile — done
  differently: a `LINE`/`COVEREDRATIO` minimum, initially `0.51` (51%) as a deliberately low regression backstop,
  raised to `0.86` (86%) within the v8.3.1 branch, then to `0.97` (97%) in v8.4.0 once the 86% floor was confirmed
  holding cleanly in CI — genuinely "near the current baseline" (98.16%/98.94%) now, with a small deliberate margin
  rather than pinned exactly to it. `BRANCH` is still not separately enforced, only `LINE` — a documented deviation
  from the original wording, not an oversight
- [x] Wire that rule into the CI gate, so a coverage regression fails the build — the `check` execution runs as
  part of `build.yml`'s `./mvnw verify -Pcoverage` step
- [x] Refresh `HISTORY.md`'s coverage figure at the same time, so it stops drifting from the real number — done in
  v8.3.1: a fresh `./mvnw verify -Pcoverage` run measured the real current baseline at 98.16%/98.94% (line/branch),
  836 tests, recorded in `HISTORY.md`'s Historical Timeline, Phase 24, Milestone 24 and Future Roadmap Implications
  entries
- [x] Confirm the 86% floor holds cleanly in CI, then tighten it closer to the real baseline — confirmed via
  `build.yml` succeeding on both the `develop` push and the `main` promotion that shipped v8.3.1
  (`gh run list --workflow=build.yml`), then tightened to `0.97` in v8.4.0 and re-verified locally with a clean
  `./mvnw verify -Pcoverage` run before landing it
- [x] Re-verify the coverage figure at this release's own prep time, since the suite kept growing after the `0.97`
  tightening landed — a final `./mvnw verify -Pcoverage` run measured 98.44%/98.98% line/branch, 868 tests (up from
  836), still comfortably clear of the 97% floor

**Dependency currency check** *(improvement-plan.md → Gap #5)* — ✅ Closed in v8.1.1

- [x] At each release, confirm whether Spring Boot's managed `jackson-databind` version has caught up to the manual
  override in `pom.xml` — confirmed for v8.1.1: the `spring-boot-starter-parent` bump to `4.1.1` manages
  `jackson-databind` `2.21.5` directly
- [x] Drop the override in the same pass as the version bump once it's redundant — done, along with a second
  `log4j-api` override (CVE-2026-49844 fix) added since, also picked up by the same bump
- [ ] Recurring: repeat this check at each future release for any newly added manual dependency-version overrides —
  already paid off once, within this same v8.1.1 release: the `jackson-bom.version` property (pinned `3.1.5`)
  matched Boot 4.1.1's own managed default exactly, so it was dropped too. This specific instance is closed, but
  the practice itself stays in force for future releases, so this last checkbox stays deliberately unchecked — a
  recurring practice, not an unfinished task, doesn't keep the gap out of ✅ Completed

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

**Match bulk CSV import** *(improvement-plan.md → Gap #8)* — ✅ Closed in v8.3.0

- [x] Introduce `MatchRequestForCSV`/`MatchResponseHolder` (`models/ipsc/match/request/`,
  `models/ipsc/match/response/`), mirroring `CompetitorRequestForCSV`/`CompetitorResponseHolder`'s `UpperCamelCase`
  CSV/JSON `@JsonCreator` pattern — with stages represented as a single semicolon-separated
  `<stageNumber>:<stageName>` cell, since CSV has no native nested-row representation
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

**Unused `DEFAULT_MATCH_CLUB_IDENTIFIER` constant** *(improvement-plan.md → Gap #9)* — ✅ Closed in v8.4.0

- [x] Decide whether joint-club matches should default to `ClubIdentifier.ALL` when `club` is omitted or must
  always name `"Eufees Clubs"` explicitly — decided to default
- [x] Wire `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` into `IpscMatchServiceImpl`'s club resolution instead of
  throwing `"Club is required."` when `club` is omitted, mirroring how `resolveHomeClub`/`resolveClubNumber`
  already treat an absent optional field — `validateForCreate` no longer rejects a missing/blank `club`, and
  `resolveClub` now resolves `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` via `clubRepository.findByIdentifier`
  when none is supplied, throwing `NonFatalException` if even that default club is missing
  
**`HISTORY.md` Phase/Milestone backfill** *(improvement-plan.md → Gap #10)* — ✅ Closed in v8.5.1

- [x] Add "Phase 26: ..." (Evolution Overview) and "Milestone 26: ..." (Major Milestones) entries for v8.4.1
  ("Documentation Cross-Reference Consolidation & Icon Registry Sync"), summarising its existing Historical
  Timeline entry
- [x] Add "Phase 27: ..."/"Milestone 27: ..." entries for v8.4.2 ("Root Document Title Standardisation &
  Source-of-Truth Clarification")
- [x] Add "Phase 28: ..."/"Milestone 28: ..." entries for v8.5.0 ("Match Start/End Time Tracking")
- [x] Re-check whether this release (`release/v8.5.1`) needs its own Phase/Milestone once its scope is final, per
  `AGENTS.md`'s Release Checklist step 6 — yes: added as Phase 29/Milestone 29 during this release's own prep pass

**`HISTORY.md` Future Roadmap list refresh** *(improvement-plan.md → Gap #11)* — ✅ Closed in v8.6.2

- [x] Drop (or mark delivered) the "Seed `Club.identifier` (HPSC, SOSC, PMPSC)" Short-term bullet — shipped in
  v8.4.0 via `V7_3_0__seed_club_data.sql` — keeping the `Competitor.homeClub` backfill half only if still wanted
- [x] Rename `ShooterLogEntry` to `ShooterLogCompetitor` in the Short-term wiring bullet, matching v7.1.0's rename
- [x] Relabel the "Medium-term (v7.x+)" heading for the current major version
- [x] Drop "Bulk match processing capabilities" as delivered by Gap #8's v8.3.0 bulk CSV import, or reword it to
  name what is still missing
- [x] Leave the items overlapping Gap #6 (`ShooterLog` calculation service, scores-request wiring) in place while
  that gap stays open

**Competitor/match delete operation** *(improvement-plan.md → Gap #12)* — ✅ Closed in v8.8.0

- [x] Decide whether competitors and matches should be deletable through the API at all — yes, but only while
  nothing references them
- [x] If yes: add `deleteCompetitor`/`deleteMatch` to `IpscCompetitorService`/`IpscMatchService` and
  `DELETE /{competitorId}`/`DELETE /{matchId}` to their controllers, with explicit reject-or-cascade handling of
  dependent `MatchCompetitor`/`ShooterLog`/`IpscMatchStage`/`ShooterLogCompetitor` rows — done as reject:
  referenced records get a `ValidationException` (`400`), also covering `MatchStageCompetitor`; a match's own
  `IpscMatchStage` rows and a competitor's emails are deleted with it rather than blocking it
- [x] If yes: add Mockito controller/service tests and `@SpringBootTest` integration tests for the new operations
- [x] ~~If no: reword the "CRUD" claims in `README.md`/`ARCHITECTURE.md` to "create, read and update"~~ — not
  needed, since deletes were added and the "CRUD" claims are now accurate
- [x] Refresh `standard-rest-conventions.md`'s "🔍 Current State in This Codebase" examples to name
  `getAllMatches`/`getAllCompetitors` and `IpscCompetitorController`

**Claude Code workflows in the CI/CD documentation** *(improvement-plan.md → Gap #13)* — ✅ Closed in v8.9.0

- [x] Add `claude-code-review.yml` (every PR, advisory) and `claude.yml` (on `@claude` mention) rows to
  `ARCHITECTURE.md`'s CI/CD & Quality Gates table, noting the `CLAUDE_CODE_OAUTH_TOKEN` secret both rely on
- [x] Widen `ARCHITECTURE.md`'s Project Structure tree comment for `.github/workflows/` generically, without listing
  individual workflow files
- [x] Extend `CONTRIBUTING.md`'s CI/CD & Quality Gates summary line to match the updated table
- [x] Optionally drop or tailor `claude-code-review.yml`'s leftover TypeScript/JavaScript `paths:` template comment
  — tailored to `src/**/*.java`, `src/main/resources/**` and `pom.xml`, still commented out

**Flyway migration table refresh** *(improvement-plan.md → Gap #14)* — ✅ Closed in v8.9.0

- [x] Add rows to `flyway-migration-versioning.md`'s Current State table for `V7_4_0__make_club_number_nullable.sql`
  (v8.4.0), `V7_5_0__add_ipsc_match_start_end_time.sql` (v8.5.0), `V7_6_0__add_ipsc_match_url.sql` (v8.6.0),
  `V7_7_0__change_ipsc_match_start_end_time_to_time.sql` (v8.6.0) and `V7_8_0__add_competitor_paid_up_flags.sql`
  (listed as "Unreleased" — confirm actual shipping version when this lands)
- [x] ~~Fold this table refresh into a recurring release-prep check~~ — done differently: added as step 5 of
  `flyway-migration-versioning.md`'s own "🔢 Choosing the Next Version" section instead, so whoever adds the next
  migration updates the table in the same change rather than relying on a separate release-time check

**ARCHITECTURE.md cascade/mappedBy self-contradiction** *(improvement-plan.md → Gap #15)* — ✅ Closed in v8.9.0

- [x] ~~Correct the Quality Attributes table's "Data Integrity" row (line 387) to match the Persistence Layer
  section's "no cascade or `mappedBy`" description (line 201)~~ — done the other way round: `IpscMatch` gained a
  cascaded `@OneToMany(mappedBy = "match")` `stages` collection, and the Persistence Layer section, entity table and
  "Data Integrity" row were all rewritten to describe it as the domain model's one cascaded relationship
- [x] Correct the Development Guidelines cross-reference (line 405) to point at `CONTRIBUTING.md`, not `README.md`,
  for database profiles documentation

**CONTRIBUTING.md dead test-method example** *(improvement-plan.md → Gap #16)* — ✅ Closed in v8.9.0

- [x] Replace `AwardControllerTest#testProcessCsv_whenValidCsvData_thenReturns200` (line 101) with an existing test
  method, e.g. `AwardControllerTest#testCreateAwards_whenValidCsvData_thenReturns200`

**HISTORY.md Future Roadmap Implications log refresh** *(improvement-plan.md → Gap #17)* — ✅ Closed in v8.9.0

- [x] Rename the current final `### Recently Completed (v8.4.0)` entry to `### Previously Completed (v8.4.0)`
- [x] Add a `### Recently Completed (vX.Y.Z)` entry for each of v8.4.1, v8.4.2, v8.5.0, v8.5.1, v8.6.0, v8.6.1,
  v8.6.2, v8.7.0 and v8.8.0, summarising each release's already-written Historical Timeline/`CHANGELOG.md` content
- [x] Update the section's opening sentence from "Based on the evolution to v8.4.0" to the current version
- [x] Fold this refresh into the Release Checklist alongside Gap #10's Phase/Milestone step — done by making
  the existing step 6 (and `prep-version-release`'s matching step) update this log unconditionally, instead of
  only "if the release is significant enough"

**Tech-stack docs vs unused dependencies** *(improvement-plan.md → Gap #18)* — ✅ Closed in v8.9.0

- [x] Decide per dependency (`jackson-dataformat-xml`, `commons-lang3`, `commons-text`) whether to drop it or keep it
  as documented groundwork
- [x] Remove the dropped ones from `pom.xml`, including `commons-text`'s manual version pin
- [x] Correct the tech-stack lines in `README.md`, `ARCHITECTURE.md` and `AGENTS.md` to match

**ARCHITECTURE.md Strategy Pattern rows** *(improvement-plan.md → Gap #19)* — ✅ Closed in v8.9.0

- [x] Remove or reword the Key Design Patterns table's "Strategy Pattern" row — replaced with a "Transaction
  Boundary" row describing `TransactionService`
- [x] Replace "strategy-pattern converters" in the Quality Attributes table's Extensibility row

**AGENTS.md 3-tier test rule and TransactionService** *(improvement-plan.md → Gap #20)* — ✅ Closed in v8.9.0

- [x] Either add `TransactionServiceTest`/`TransactionServiceIntegrationTest`, or record `TransactionService` as a
  deliberate exception in `AGENTS.md`'s Test Conventions
- [x] Correct the rule's "All four services" count and list

**ARCHITECTURE.md data flows and repositories** *(improvement-plan.md → Gap #21)* — ✅ Closed in v8.9.0

- [x] Redraw the System Overview and Typical Request-Response Flow diagrams through `TransactionService`
- [x] Reword both bulk-import flows to "builds each row, then saves all rows in one transaction"
- [x] Remove the stale "removed pending a rebuild" CRUD-flows note
- [x] Update the Project Structure tree's `repositories/` comment and the Repositories section's example queries
- [x] Refresh Gap #6's Evidence, which quotes the old "not yet wired" comment — added a note rather than
  rewriting the original analysis

**AGENTS.md club name** *(improvement-plan.md → Gap #22)* — ✅ Closed in v8.9.0

- [x] Correct `AGENTS.md`'s "Handgun and Practical Shooting Club" to "Hartbeespoortdam Practical Shooting Club"

**Competitor.homeClub backfill** *(improvement-plan.md → Gap #23)* — ✅ Closed as not applicable in v8.9.0

- [x] Decide whether existing competitors' `homeClub` should still be backfilled
- [x] If so, add a data migration or one-off service operation deriving `home_club_id`; if not, drop the bullet from
  `HISTORY.md`'s Short-term list — not wanted: no column reliably identifies a home club, so the bullet was dropped

**Entity and repository tests** *(improvement-plan.md → Gap #24)* — ✅ Closed in v8.9.0

- [x] Add repository/entity tests for `IpscMatch.stages`' cascade and orphan removal, the fetch-join queries and the
  `existsBy…` checks
- [x] Or reword `README.md`'s Testing section to describe the coverage that actually exists — done as well, since
  there are still no entity-level unit tests

**Entity-level unit tests** *(improvement-plan.md → Gap #25)* — ✅ Closed in v8.10.0

- [x] Decide whether entity-level unit tests are still wanted, given the entities hold no hand-written behaviour and
  their persistence behaviour is covered by the repository integration tests — decided per entity: only `IpscMatch`
  has behaviour worth a unit test (its `stages` exclusion from Lombok's `toString`/`equals`/`hashCode`)
- [x] If so, add unit tests for the entity behaviour that warrants it (e.g. empty-collection defaults); if not, drop
  the bullet from `HISTORY.md`'s Short-term list and the item from the next release's Known Issues/Future Enhancements
  — did both halves: added `IpscMatchTest` and dropped the `HISTORY.md` bullet

**Database-profile docs** *(improvement-plan.md → Gap #27)* — ✅ Closed in v8.10.0

- [x] Document that the no-profile (production) run needs its datasource URL supplied externally (e.g.
  `SPRING_DATASOURCE_URL`) in `AGENTS.md`'s run command and `CONTRIBUTING.md`'s `(none / prod)` row — or add a
  `${MYSQL_URL}`-style placeholder to `application.properties` and document that variable instead — documented the
  external URL rather than adding a required placeholder (a breaking configuration change), and added a separate
  `application-prod.properties` profile (`hpsc_prod`) with its own `CONTRIBUTING.md` row and `AGENTS.md` run
  command
- [x] Qualify `README.md`'s/`CONTRIBUTING.md`'s "regardless of profile" credentials wording to exclude `local` as well
  as `test`
- [x] Add a `local` row to `CONTRIBUTING.md`'s Database Profiles table, or name its `hpsc_dev` user and
  `MYSQL_LOCAL_PASSWORD` variable in the existing note — did both

**`staging` logging profile** *(improvement-plan.md → Gap #28)* — ✅ Closed in v8.10.0

- [x] Decide whether a staging environment is wanted — not wanted
- [x] If not, remove the `staging` `<springProfile>` block from `logback-spring.xml`; if so, add
  `application-staging.properties`, a `staging` row in `CONTRIBUTING.md`'s Database Profiles table and matching
  mentions wherever `prod` is documented — removed the block

---

## 🟡 Partially Completed

A gap moves here once at least one of its items is checked, but the block as a whole isn't fully checked off yet —
matching `improvement-plan.md`'s "🟡 Partially Completed" section — and moves on to ✅ Completed once every item is
checked and the gap's own header there carries a "✅ Closed" suffix.

**`tomcat.version` override** *(improvement-plan.md → Gap #26)* — 🟡 Partially completed in v8.10.0

- [x] Each release, check whether the Spring Boot parent's managed `tomcat.version` has reached `11.0.25` or later —
  made a standing step instead: `AGENTS.md`'s Release Checklist step 2 and `prep-version-release` now re-check every
  manual `pom.xml` override against the parent's own `spring-boot-dependencies` POM
- [ ] Once it has, drop the `tomcat.version` override and its comment from `pom.xml` in the same pass as the parent
  bump, and update this plan's Ongoing roadmap row and Goals & Constraints table

---

## ⚪ Open

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

When checking an item off, add a short note after it if it was fulfilled differently from its original wording
(e.g. "— done differently: ..."), or strike it through (`~~...~~`) with a note if it became unnecessary.

---

Check items off in place as work lands; don't delete a task outright. When a gap's first item gets checked, move its
whole block from ⚪ Open into 🟡 Partially Completed; once every item under it is checked, move the block again into
✅ Completed and mark the gap closed there (e.g. strike it through with a "✅ Closed in vX.Y.Z" note) — matching
whatever change was made to its section in `improvement-plan.md`, per that document's Success Criteria.
