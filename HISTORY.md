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

### Version 16.0.0 (October 31, 2026)

**Theme:** Rifle PCC Division, Clearer Semi-Auto Division Names & Cleaner Competitor Name Matching

**Key Focus:**

- New `Division.RIFLE_PCC` ("Rifle PCC") rifle division
- `RIFLE_SEMI_AUTO_OPEN` and `RIFLE_SEMI_AUTO_STANDARD` display names become "Rifle Semi Auto Open" and "Rifle Semi
  Auto Standard", with `V11_11_0__rename_semi_auto_rifle_division_names.sql` renaming the stored values in
  `match_competitor`, `shooter_log_competitor` and `shooter_log_overall`
- `CompetitorHelpers.cleanCompetitorName` strips trailing modified-entry digits of any length, and
  `IpscConstants.REPLACE_IN_NAMES_REGEX` also removes a "TRO" or "(TRO)" role marker, so more names match their
  competitor
- `IpscMatchCompetitorServiceImpl.createMatchCompetitors` is `@Transactional`, so a bulk import is saved or rolled back
  as one unit
- Scoped as `v16.0.0` **MAJOR**: a division given by the old "Rifle Open" or "Rifle Standard" names no longer
  resolves, flagged in `CHANGELOG.md`
- Improvement plan: Gap #41 is added for the score-scale constants and `NumberUtil.calculateSum` that v15.0.0
  reinstated without a production caller, leaving 38 gaps closed, #6 and #26 partially completed and #41 open
- The suite stands at 1,327 passing tests
- Project version bumped to 16.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 15.0.0 (October 17, 2026)

**Theme:** Shooter Result Calculation & Reading

**Key Focus:**

- New `ShooterResult` entity, `ShooterResultRepository` and `V11_10_0__create_shooter_result.sql` store a match
  competitor's rank and percentage in a match, mirroring `ShooterLogCompetitor` but linked to a match
- New `IpscShooterResultService` and `IpscShooterResultController` expose
  `POST /ipsc/shooter-results/matches/{matchId}`, which calculates a match's results, and
  `GET /ipsc/shooter-results/matches/{matchId}` and `GET /ipsc/shooter-results`, which read them as a match linked to
  its results
- `calculateMatchShooterResult` ranks a match's competitors by match points within their firearm type, division and
  competitor category (those with none a group of their own), with ties sharing a rank, and sets each percentage
  relative to the group winner; it recalculates only when the results are out of date, and saves them in one
  transaction through the new `TransactionService.saveShooterResults`
- `MatchCompetitor` drops `overallRanking` and `clubRanking` (`V11_8_0__remove_match_competitor_rankings.sql`), and
  they leave the request, patch, CSV import and response models
- The shooter log `points` are renamed `percentageInLog`
  (`V11_9_0__rename_shooter_log_points_to_percentage_in_log.sql`), shown by `ShooterResultResponse` as a display string
- `NumberUtil` is reinstated, with `IpscConstants`' score scales and `SystemConstants.DEFAULT_SCALE`
- `org.ow2.asm:asm` is pinned to 9.11 to replace the vulnerable 9.7.1 that `json-smart` brings in
- Scoped as `v15.0.0` **MAJOR**: the removed ranking fields and CSV columns and the renamed `points` property are
  backward-incompatible, each flagged in `CHANGELOG.md`
- Improvement plan: Gap #6 progresses with the shooter result endpoints, leaving 38 gaps closed and #6 and #26
  partially completed
- The suite stands at 1,320 passing tests
- Project version bumped to 15.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 14.0.0 (October 9, 2026)

**Theme:** Complete Failure Reporting for Bulk Imports, Club Resolution & Shorter Division Names

**Key Focus:**

- New `MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields` and
  `MatchCompetitorMapper.populateResolvableFields` let `IpscMatchCompetitorServiceImpl.createMatchCompetitors` check a
  bulk import row once and list every missing or unresolvable required field, instead of failing on the first
- A failed row is reported with no `MatchCompetitorResponse` and its values in the new `MatchCompetitorRow`, built by
  the new `MatchCompetitorRowMapper`; `MatchCompetitorBulkResponseHolder.matchCompetitors` is renamed back to
  `matchCompetitorResults`
- New `IpscEntityClubService.findByCodeOrAbbreviation` and `findByCodeOrAbbreviationWithDefault` resolve a club by code,
  abbreviation or name, and `CompetitorMapper.resolveHomeClub`, `MatchMapper.resolveClub` and
  `MatchCompetitorMapper.resolveMatchClub` use them, so an unknown club answers `400` and `FatalException` is dropped
  from the match service and controller methods that only declared it
- `ClubService` and `EntityIpscCompetitorService` are renamed `IpscEntityClubService` and
  `IpscEntityCompetitorService`, and `findCompetitor` becomes `findCompetitorByIdentifierAndFullName`
- Each `Division` drops its trailing " Division" and `PCC_OPTICS` and `PCC_IRON` become `PCC Optics` and `PCC Irons`;
  `V11_7_0__drop_division_suffix_from_division_names.sql` renames the stored values
- `CompetitorCategory.fromName` returns `NONE` for a null or blank name and an empty `Optional` for an unrecognised one;
  a missing firearm type is taken from the division
- `MatchCompetitor` fields are `@NotNull` and `IpscMatchCompetitorServiceImpl.toResponse` validates the entity first
- `NumberUtil`, `DateUtil`, `SystemConstants.DEFAULT_SCALE`, two unused `StringUtil` and `ValueUtil` methods and
  `MatchCompetitorResponseHolder` are removed, together with controller tests that only checked delegation
- Scoped as `v14.0.0` **MAJOR**: the division renames, the `400` for an unknown club, the renamed response field and the
  `null` failed-row response are backward-incompatible, each flagged in `CHANGELOG.md`
- Improvement plan: Gaps #38, #39 and #40 are closed, leaving 38 gaps closed and only #6 and #26 partially completed
- The suite stands at 1,263 passing tests
- Project version bumped to 14.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

The entries for versions 10.0.0 to 13.3.0 are archived, unchanged, in
[`documentation/archive/v10-v13/HISTORY_v10-v13.md`](/documentation/archive/v10-v13/HISTORY_v10-v13.md).

The entries for versions 8.0.0 to 9.1.0 are archived, unchanged, in
[`documentation/archive/v8-v9/HISTORY_v8-v9.md`](/documentation/archive/v8-v9/HISTORY_v8-v9.md).

The entries for versions 5.0.0 to 7.4.1 are archived, unchanged, in
[`documentation/archive/v5-v7/HISTORY_v5-v7.md`](/documentation/archive/v5-v7/HISTORY_v5-v7.md).

The entries for versions 1.0.0 to 4.1.0 are archived, unchanged, in
[`documentation/archive/v1-v4/HISTORY_v1-v4.md`](/documentation/archive/v1-v4/HISTORY_v1-v4.md).

---

## 📖 Evolution Overview

The full Phase-by-phase narrative for every release has moved to
[`EVOLUTION_OVERVIEW.md`](/EVOLUTION_OVERVIEW.md) to keep this file a
manageable size, since that narrative alone had grown to roughly half of it. See the
[📅 Historical Timeline](#-historical-timeline) above for the same releases summarised more concisely, and
[🎯 Major Milestones](#-major-milestones) below for each release's headline achievement.

---

## 🎯 Major Milestones

Milestones 1 to 7 (versions 1.0.0 to 4.1.0) are archived, unchanged, in
[`documentation/archive/v1-v4/HISTORY_v1-v4.md`](/documentation/archive/v1-v4/HISTORY_v1-v4.md).

Milestones 8 to 18 (versions 5.0.0 to 7.4.1) are archived, unchanged, in
[`documentation/archive/v5-v7/HISTORY_v5-v7.md`](/documentation/archive/v5-v7/HISTORY_v5-v7.md).

Milestones 19 to 43 (versions 8.0.0 to 9.1.0) are archived, unchanged, in
[`documentation/archive/v8-v9/HISTORY_v8-v9.md`](/documentation/archive/v8-v9/HISTORY_v8-v9.md).

Milestones 44 to 50 (versions 10.0.0 to 13.3.0) are archived, unchanged, in
[`documentation/archive/v10-v13/HISTORY_v10-v13.md`](/documentation/archive/v10-v13/HISTORY_v10-v13.md).

---

### Milestone 51: Complete Failure Reporting for Bulk Imports, Club Resolution & Shorter Division Names (v14.0.0)

- A failed bulk match competitor row lists every missing or unresolvable field and the values it supplied
- A club is resolved by code, abbreviation or name through `IpscEntityClubService`
- A division is matched by its short name, and `V11_7_0__drop_division_suffix_from_division_names.sql` renames the
  stored values
- Improvement plan Gaps #38, #39 and #40 are closed

### Milestone 52: Shooter Result Calculation & Reading (v15.0.0)

- A match's shooter results are calculated, ranked by firearm type, division and competitor category, and read through
  `IpscShooterResultController`
- Results are stored in the new `ShooterResult` entity and recalculated only when they are out of date
- `MatchCompetitor` drops its overall and club rankings, which the results now supersede
- Improvement plan Gap #6 progresses

### Milestone 53: Rifle PCC Division, Clearer Semi-Auto Division Names & Cleaner Competitor Name Matching (v16.0.0)

- `Division.RIFLE_PCC` adds the "Rifle PCC" division, and the semi-auto rifle divisions are named "Rifle Semi Auto Open"
  and "Rifle Semi Auto Standard", with `V11_11_0__rename_semi_auto_rifle_division_names.sql` renaming the stored values
- Competitor names match despite trailing modified-entry digits and a "TRO" role marker
- A bulk match competitor import is saved or rolled back as one unit
- Improvement plan Gap #41 is added

---

## 🏛️ Architectural Evolution

The entries for versions 5.0.0 to 7.2.0 are archived, unchanged, in
[`documentation/archive/v5-v7/HISTORY_v5-v7.md`](/documentation/archive/v5-v7/HISTORY_v5-v7.md).

The entries for versions 1.0.0 to 4.0.0 are archived, unchanged, in
[`documentation/archive/v1-v4/HISTORY_v1-v4.md`](/documentation/archive/v1-v4/HISTORY_v1-v4.md).

The entries for versions 8.0.0 to 9.1.0 are archived, unchanged, in
[`documentation/archive/v8-v9/HISTORY_v8-v9.md`](/documentation/archive/v8-v9/HISTORY_v8-v9.md).

The entries for versions 10.0.0 to 13.3.0 are archived, unchanged, in
[`documentation/archive/v10-v13/HISTORY_v10-v13.md`](/documentation/archive/v10-v13/HISTORY_v10-v13.md).

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
- **v8.9.0:** New `TransactionServiceTest`/`TransactionServiceImplTest`/`TransactionServiceIntegrationTest` 3-tier
  split; a new `repositories/` package of repository integration tests covering the fetch-join queries, the stage
  cascade and every `existsBy…` check; service integration tests run without a surrounding transaction; 903 → 966
  tests, 98.77%/99.09% line/branch coverage

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
- **Version 8.x (v8.0.0 – v8.12.0):** Complete the IPSC module rebuild that v6.x–v7.x deliberately deferred — real
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
  structure, and correcting `CHANGELOG.md` heading-depth drift across every convention document and skill. Finally,
  make the persistence layer's behaviour explicit — competitor paid-up flags, every write committed by a dedicated
  `TransactionService` and lazy associations loaded through fetch-join queries — while clearing every
  documentation-accuracy gap the improvement plan tracked. Then turn Semantic Versioning from precedent into a rule
  the release process enforces, give production its own profile and make every documented runtime profile match the
  configuration behind it, then bring the repository's dependency tooling — dependency submission and Dependabot —
  under version control and into the branching model, extend the automated code review to Dependabot's PRs, and
  absorb its first updates. Then make the application deployable as a container — a Docker image, a Compose setup
  with its own MySQL database and an Actuator health endpoint — and, in running it against an empty database, make
  Flyway's migrations actually run at startup as the documentation had always described, then build that image on
  every pull request so a broken `Dockerfile` fails CI rather than a deployment, and hold branch coverage to the same
  97% floor as line coverage. Then normalise the competitor CSV import's casing — proper-casing free-text columns and
  lower-casing surname particles — so imported data reads consistently regardless of how it was typed, backed by a
  new `helpers` package and an internal `Util` → `Utils` naming clean-up.
- **Version 9.x (v9.0.0 – v9.1.0):** Narrow the match domain to what the project actually uses — removing the
  stage model and its tables — and collapse the duplicate CSV request models into the request models themselves
  through Jackson mix-ins, so the bulk imports and the JSON endpoints share one contract. Because that tightens the
  public API (a removed field, now-required properties, a configuration variable no longer read), the release is the
  project's first MAJOR since v8.0.0, with each breaking change flagged in `CHANGELOG.md` as it landed. Then build the
  first half of the scoring layer — match competitors, with their own service, controller and bulk CSV import — and
  rework the shooter log entities beneath it, as backward-compatible additions that leave every existing endpoint
  unchanged.
- **Version 10.x (v10.0.0):** Make the match competitor import accept the format the data arrives in — a PractiScore
  export's headers and overall scores, with the competitor found by membership number or name instead of a database id
  — and settle `competitorCategory` back to a single value. Because that renames a JSON field, renames the CSV headers
  and turns a list back into a single value, the release is the project's second consecutive MAJOR, with each breaking
  change flagged in `CHANGELOG.md` as it landed.
- **Version 11.x (v11.0.0):** Settle the competitor's contract before more is built on it — one content type
  (`text/plain`) for every bulk endpoint, one spelling (`nickName`) for the nickname and a whole number for
  `competitorNumber`, which the new competitor lookup service matches on before it falls back to the name. Because
  that changes the content type the bulk endpoints accept, renames a JSON property and a CSV column and refuses a
  competitor number that is not a whole number, the release is the project's third consecutive MAJOR, with each
  breaking change flagged in `CHANGELOG.md` as it landed.
- **Version 12.x (v12.0.0):** Put the competitor lookup to work and make the bulk import tolerant — a match competitor
  request finds its competitor by number, ID number or name, each row of a bulk import is created, skipped or failed on
  its own, and the import is limited to one club, HPSC's unless another is asked for. Because that changes how a
  competitor number is matched, what a bulk import answers, the name of a response field and which rows an import
  creates, the release is the project's fourth consecutive MAJOR, with each breaking change flagged in `CHANGELOG.md`
  as it landed.
- **Version 13.x (v13.0.0 – v13.3.0):** Make the match competitor's values consistent and the services thinner — field
  copying and lookups move into mappers, a division belongs to a firearm type and has a name of its own, and a response
  lists a competitor's names once each. Because that changes the shape of a response field, requires a power factor and
  changes the division and category names a request may use, v13.0.0 is the project's fifth consecutive MAJOR release,
  with each breaking change flagged in `CHANGELOG.md` as it landed. v13.1.0 then makes the contract enforceable at the
  door and forgiving of the data's names — `@Valid` request bodies answer `400`, jspecify's `@NonNull` expresses
  nullness and `CompetitorHelpers.cleanCompetitorName` normalises a competitor's name — as a backward-compatible MINOR.
  v13.2.0 then tightens that matching — a numeric club number takes precedence over a competitor number of the same
  value, and the name-cleaning patterns no longer backtrack — as a second backward-compatible MINOR. v13.3.0 then
  records whether a competitor's NGPSA membership is paid up and stores a club number without spaces — a third
  backward-compatible MINOR.
- **Version 14.x (v14.0.0):** Make a failed bulk import useful and the lookups uniform — a bulk match competitor row is
  checked once and reports every missing or unresolvable field with the values it supplied, a club is resolved by code,
  abbreviation or name through one service, and a division is matched by its short name. Because that changes the
  division names a request may use, the status an unknown club answers, the name of a response field and the shape of a
  failed row, v14.0.0 is the project's sixth consecutive MAJOR release, with each breaking change flagged in
  `CHANGELOG.md` as it landed.
- **Version 15.x (v15.0.0):** Turn a match's scores into ranked results — a match's competitors are ranked by match
  points within their firearm type, division and competitor category, each with a percentage of the group winner's
  points, stored in a new `ShooterResult` entity and served by one controller. Because that retires the overall and
  club rankings on a match competitor and renames the shooter log `points`, v15.0.0 is the project's seventh
  consecutive MAJOR release, with each breaking change flagged in `CHANGELOG.md` as it landed.
- **Version 16.x (v16.0.0):** Make more of the real match data resolve — the Rifle PCC division is added, the semi-auto
  rifle divisions get names that say what they are, and competitor names match despite trailing modified-entry digits
  and a "TRO" role marker, while a bulk match competitor import is saved or rolled back as one unit. Because the
  renamed divisions change the names a request may use, v16.0.0 is the project's eighth consecutive MAJOR release,
  with the breaking change flagged in `CHANGELOG.md` as it landed.

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


16. **Explicit Transaction Boundary (v8.9.0):** With `spring.jpa.open-in-view` disabled and associations lazy,
    where a transaction starts and ends decides what a response can read
    - Moving every competitor/match write into `TransactionService`'s explicit `TransactionTemplate` transactions made
      the boundary visible in one class instead of spread across `@Transactional` service methods
    - `orphanRemoval` only sees removals relative to a collection's last-flushed snapshot, so replacing stages still
      deletes them explicitly and flushes before inserting reused stage numbers
    - Integration tests that are themselves `@Transactional` absorb the code's own transactions and hide commit and
      lazy-loading bugs — so the new tests that prove commits run without a surrounding transaction

17. **Stage Removal & One Request Model per Resource (v9.0.0):** Removing a part of a domain model is cheaper done in
    one release than left half-supported, and a second model for an entry point is a second contract to keep in step
    - Dropping `IpscMatchStage`/`MatchStageCompetitor` took the replace/upsert logic, the `StageSaveMode` overload and
      a large share of the match tests with it — and with the stage collection gone, so did the v8.9.0 reliance on a
      single cascaded relationship
    - Binding CSV columns onto the request model through a Jackson mix-in meant the CSV models and the copy step in
      each service could go, and surfaced a genuine defect: the competitor CSV normalisation blanked `competitorId`
    - Tightening a request model (`matchFirearmType`, `matchCategory` now required) broke `PATCH`, which reuses it,
      so each `PATCH` got its own model with no required fields

18. **Constructor Annotations Govern a `@JsonCreator` (v9.1.0):** With a creator constructor, a property's
    requirement is decided by the constructor parameter, not the field it ends up in
    - Marking a field `@JsonProperty(required = true)` while the creator's parameter was unannotated left a request
      model reading a missing value as `null`, so the field annotation and the constructor have to be kept in step
    - Relaxing `matchFirearmType` and `matchCategory` on `MatchRequest` took the constructor and the CSV mix-in as well
      as the fields, and a test per property proved each of the three entry points agreed
    - A batch import is only atomic if every row is checked first: refusing a duplicate against the database does not
      catch two rows in the same file that duplicate each other

19. **A Migration Chain Is Only Proven From Empty (v10.0.0):** Each migration is written against the schema as it
    stood then, so an edit that names an earlier column can pass review and still never run
    - `V10_2_0` added a column `AFTER match_id`, which `V8_3_0` had dropped; no developer database had applied it, so
      nothing had failed, and only migrating an empty MySQL 8.4 database from `V7_0_0` onward exposed it
    - A refactor that returns a column to its earlier shape (`competitorCategory` back from a child table) needs the
      same check, because the migration reverses several earlier ones

20. **Tighten a Type Where Bad Data Can Still Be Refused (v11.0.0):** Changing a column's type is safe only if the
    data can be converted, and a migration that cannot convert it should stop rather than guess
    - `V11_1_0` turns `competitor_number` into an `INT` and is refused when a stored number is non-numeric, so an old
      value that was never a number surfaces at migration time instead of being lost
    - Keeping `competitorNumber` a string in the requests while holding a number in the entity left the JSON and CSV
      contract readable by existing clients, and put the whole-number rule in one place
    - A lookup that depends on the number being comparable (`findCompetitor`) was only worth building once the number
      was a number, which is why the contract change and the new service shipped together

---

## 🛤️ Future Roadmap Implications

Based on the evolution to v16.0.0, the following areas are identified for future enhancement:

The completed-work logs for versions 5.4.0 and earlier up to 7.2.0 are archived, unchanged, in
[`documentation/archive/v5-v7/HISTORY_v5-v7.md`](/documentation/archive/v5-v7/HISTORY_v5-v7.md).

The completed-work logs for versions 8.0.0 to 9.1.0 are archived, unchanged, in
[`documentation/archive/v8-v9/HISTORY_v8-v9.md`](/documentation/archive/v8-v9/HISTORY_v8-v9.md).

The completed-work logs for versions 10.0.0 to 13.3.0 are archived, unchanged, in
[`documentation/archive/v10-v13/HISTORY_v10-v13.md`](/documentation/archive/v10-v13/HISTORY_v10-v13.md).

### Recently Completed (v16.0.0)

- `Division.RIFLE_PCC` is added, and the semi-auto rifle divisions are renamed with
  `V11_11_0__rename_semi_auto_rifle_division_names.sql`
- `CompetitorHelpers` strips trailing modified-entry digits and `IpscConstants.REPLACE_IN_NAMES_REGEX` removes a "TRO"
  marker
- `IpscMatchCompetitorServiceImpl.createMatchCompetitors` runs in one transaction
- Improvement plan: Gap #41 is added, leaving 38 closed, #6 and #26 partially completed and #41 open
- Project version bumped to 16.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v15.0.0)

- `ShooterResult`, `ShooterResultRepository` and `V11_10_0__create_shooter_result.sql` store a match's calculated
  results; `IpscShooterResultService` and `IpscShooterResultController` calculate and read them
- Results are ranked within firearm type, division and competitor category, and recalculated only when out of date
- `MatchCompetitor` drops `overallRanking` and `clubRanking`, and the shooter log `points` become `percentageInLog`
- `NumberUtil` is reinstated with the score scale constants
- `org.ow2.asm:asm` is pinned to 9.11 in `pom.xml`
- Improvement plan: Gap #6 progresses, leaving 38 closed and #6 and #26 partially completed
- Project version bumped to 15.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v14.0.0)

- A bulk match competitor row reports every missing or unresolvable field with its values, through
  `MatchCompetitorHelpers`, `MatchCompetitorMapper.populateResolvableFields`, `MatchCompetitorRow` and
  `MatchCompetitorRowMapper`
- `IpscEntityClubService` resolves a club by code, abbreviation or name for home clubs, matches and match competitors;
  `ClubService` and `EntityIpscCompetitorService` are renamed to match
- `Division` names drop the " Division" suffix, with `V11_7_0__drop_division_suffix_from_division_names.sql`
- Test-only utilities are removed: `NumberUtil`, `DateUtil` and `SystemConstants.DEFAULT_SCALE`
- Improvement plan: Gaps #38, #39 and #40 closed, leaving 38 closed and #6 and #26 partially completed
- Project version bumped to 14.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

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
single `AGENTS.md` reference. Also migrates the project's AI-agent tooling from slash commands to Skills and re-adds
Qodana JVM static analysis — marking the transition from a project with significant architectural groundwork to one with
a genuinely complete, if still growing, IPSC feature set.

Version 9.0.0 narrows that feature set to what the project uses: the match stage model and its tables are removed, and
the bulk CSV imports read straight into the request models through Jackson mix-ins instead of through duplicate CSV
models. It is the project's first MAJOR release since v8.0.0, because the match request and response lose `stages`,
`matchFirearmType` and `matchCategory` become required and `MYSQL_USER` is no longer read.

Version 9.1.0 builds on that narrowed model with the first half of the scoring layer: match competitors get their own
service, controller and bulk CSV import, and the shooter log entities are reworked to a date range of matches ready
for the service that remains. It is a MINOR release, since it adds endpoints and optional fields and removes or
tightens nothing.

Version 10.0.0 reshapes that first half of the scoring layer around the format the data arrives in: match competitors
carry a PractiScore export's overall scores and headers, are found by membership number or name, and hold a single
competitor category again. It is a MAJOR release, because the JSON field `matchPoints`, the CSV headers and the shape of
`competitorCategory` all change for existing callers.

Version 11.0.0 settles the competitor's contract and builds the lookup that depends on it: every bulk endpoint reads
`text/plain`, the nickname is spelt `nickName` throughout and `competitorNumber` is a whole number in the entity and
the table, with a new service that finds a competitor by number and then by name. It is a MAJOR release, because the
bulk endpoints' content type, the nickname's JSON property and CSV column and the whole-number rule for
`competitorNumber` all change for existing callers.

Version 12.0.0 puts the lookup to work and makes the bulk import tolerant: a match competitor request finds its
competitor by number, ID number or name, each row of an import is created, skipped or failed on its own, and the import
is limited to the club the data is for, HPSC's unless another is asked for. It is a MAJOR release, because the way a
competitor number is matched, what a bulk import answers, the name of a response field and which rows an import
creates all change for existing callers.

Version 13.0.0 makes the match competitor's values agree with one another and slims the services: three mappers take
over the field copying and lookups, a division belongs to a firearm type and has a name that identifies it, and the
stored divisions and categories are renamed by migration to match. It is a MAJOR release, because the shape of a
response field, the required power factor and the division and category names a request may use all change for
existing callers.

Version 13.1.0 makes that contract enforceable and the competitor lookup more forgiving: the controllers validate
request bodies and answer `400` for a missing field, jspecify's `@NonNull` expresses nullness and a competitor's name is
cleaned of a leading position, an `RO` marker and full stops before it is matched. It is a MINOR release, because the
`400` response and the broader name matching are additions and nothing is backward-incompatible.

Version 13.2.0 tightens that name matching without changing its contract: a competitor number is tried as a club number
first, so a numeric club number wins over a competitor number of the same value, and the name-cleaning patterns are
hardened so that none of them backtracks. It is a MINOR release, because the club-number stage is an addition and
nothing is backward-incompatible.

Version 13.3.0 adds an optional `paidUpNgpsa` flag to a competitor, through a nullable `paid_up_ngpsa` column and the
request, patch, response and CSV import, and stores a club number without spaces. It is a MINOR release, because the
flag is an addition, nothing is backward-incompatible and the migration adds a nullable column.

Version 14.0.0 makes a failed bulk match competitor import report every missing or unresolvable field together with the
values the row supplied, resolves clubs by code, abbreviation or name through `IpscEntityClubService`, and drops the
trailing " Division" from every division name. It is a MAJOR release, because the division names, the `400` for an
unknown club, the renamed `matchCompetitorResults` field and the `null` failed-row response are backward-incompatible.

Version 15.0.0 calculates a match's shooter results — each competitor's rank and percentage within their firearm type,
division and competitor category — stores them in a new `ShooterResult` entity and serves them through
`IpscShooterResultController`. It is a MAJOR release, because the removed `overallRanking` and `clubRanking` fields and
CSV columns and the renamed shooter log `points` property are backward-incompatible.

Version 16.0.0 adds the Rifle PCC division, renames the semi-auto rifle divisions to "Rifle Semi Auto Open" and "Rifle
Semi Auto Standard" with a migration for the stored values, and makes competitor name matching ignore trailing
modified-entry digits and a "TRO" role marker. It is a MAJOR release, because a division given by the old "Rifle Open"
or "Rifle Standard" names no longer resolves.
