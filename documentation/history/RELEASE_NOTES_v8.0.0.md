# Release Notes – Version 8.0.0

**Release Date:** August 31, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**IPSC Module Rebuild Complete — Competitor & Match CRUD**

Version 8.0.0 completes the IPSC module rebuild that v6.0.0 through v7.4.0 laid groundwork for. `IpscController`'s
long-standing empty stub is replaced by two full CRUD controllers — `IpscCompetitorController` and
`IpscMatchController` — backed by new `IpscCompetitorService`/`IpscMatchService` implementations, new request/response
DTOs and a `Gender` enum extended to match the shape of the project's other enums. Alongside the domain work, this
release renames `processCsv` to `createAwards`/`createImages` and every enum's `getByX` factory methods to `fromX`,
invests in a comprehensive Javadoc/`@since` documentation pass, merges `CLAUDE.md`'s guidance into a single
`AGENTS.md` reference, migrates the project's AI-agent tooling from slash commands to Skills and re-adds Qodana JVM
static analysis.

---

## ⭐ Key Highlights

### 🆕 IPSC Competitor & Match CRUD

- **`IpscCompetitorController`** — full CRUD on `/ipsc/competitors`: `createCompetitor` (`POST`), `updateCompetitor`
  (`PUT /{competitorId}`, full replace), `patchCompetitor` (`PATCH /{competitorId}`, partial update),
  `getCompetitor` (`GET /{competitorId}`)
- **`IpscMatchController`** — full CRUD on `/ipsc/matches`: `createMatch` (`POST`), `updateMatch`
  (`PUT /{matchId}`, full replace), `patchMatch` (`PATCH /{matchId}`, partial update), `getMatch`
  (`GET /{matchId}`) and `getAllMatches` (`GET`, returns every match)
- Both follow this project's action-named REST method convention (`create`/`update`/`patch`/`get`, not
  `post`/`put`/`patch`/`get`)

### 🏗️ IpscCompetitorService & IpscMatchService

- **`IpscCompetitorService`/`IpscCompetitorServiceImpl`** — resolves the request's optional home club by name (404 via
  `NonFatalException` if named but not found) and its optional gender by name (400 via `ValidationException` if
  unrecognised), maps `CompetitorRequest` to/from the existing `Competitor` entity and persists via the existing
  `CompetitorRepository`. Unlike the match service's club, the home club (and gender) is optional — a `null`/blank
  name simply leaves the field unset, and `updateCompetitor`'s full replace clears any previously set value the
  request omits
- **`IpscMatchService`/`IpscMatchServiceImpl`** — resolves the request's club by name (404 if not found) and its
  firearm type/category by name (400 if unrecognised), maps `MatchRequest` to/from the existing
  `IpscMatch`/`IpscMatchStage` entities and persists via the existing repositories. `patchMatch` upserts stages by
  stage number (updating a matching stage in place, adding a new one otherwise) rather than replacing the whole stage
  list, unlike `updateMatch`'s full replace; `getAllMatches` returns every persisted match together with its stages

### 📦 New Request/Response DTOs & Package Restructuring

- **`CompetitorRequest`**/**`CompetitorResponse`** (`models/ipsc/competitor/`) — mirror `Competitor`'s persisted
  fields; `CompetitorResponse.homeClub` is typed as `ClubIdentifier` rather than a plain club-name `String`, since a
  persisted competitor's home club is always resolvable
- **`MatchResponse`**/**`MatchStageResponse`** (`models/ipsc/match/response/`) — unlike the request,
  `MatchResponse.club` is typed as `ClubIdentifier` rather than a plain `String`
- **`MatchRequest`** gains `matchFirearmType`/`matchCategory` fields, resolved by name against `FirearmType`/
  `MatchCategory` in the service layer, matching how `club` is already resolved
- `models/ipsc/request` split into `models/ipsc/match/request/` (match/stage submission) and
  `models/ipsc/scores/request/` (competitor scores submission) to match the module's per-concern shape

### 👤 Gender Enum & Persistence

- **`Gender`** gains `name`/`abbreviation` fields, a case-insensitive `fromName()` factory method and a `toString()`
  override, bringing it in line with the shape of the project's other enums
- **`GenderConverter`** — new `AttributeConverter<Gender, String>`, wired onto `Competitor.gender` via `@Convert`;
  converts blank/invalid stored values to `null` instead of letting `@Enumerated(STRING)` throw, matching the other
  enum converters

### 🔄 Rename & Consistency Sweep

- **`AwardService.processCsv`**/**`ImageService.processCsv`** renamed to `createAwards`/`createImages`, matching the
  already-named `AwardController.createAwards`/`ImageController.createImages`
- **Bulk CSV endpoints** moved from `POST /awards`/`POST /images` to `POST /awards/bulk`/`POST /images/bulk` and now
  return `201 Created` (previously `200 OK`), matching `IpscCompetitorController.createCompetitor`'s convention
- **`getByName`/`getByAbbreviation`/`getByCode`/`getByAbbreviationOrName`** factory methods renamed to
  `fromName`/`fromAbbreviation`/`fromCode`/`fromAbbreviationOrName` across `ClubIdentifier`, `CompetitorCategory`,
  `Division`, `FirearmType`, `MatchCategory` and `PowerFactor` — behaviour unchanged

### 📚 Comprehensive Javadoc/`@since` Pass

- Class- and method-level `@since` tags added across models, converters, exceptions, utils, constants and enums,
  correcting several that had drifted or were missing entirely (e.g. `DateUtil`'s `@since` corrected from `2.0.0` to
  `4.1.0` after tracing its delete/recreate history)
- **`ControllerAdvice`** gains a class-level `@since 1.0.0` tag and full `@param`/`@return` Javadoc on every exception
  handler and helper method — none of it was previously documented
- **`FatalException`/`NonFatalException`/`ValidationException`** constructor Javadoc trimmed of duplicated JDK prose;
  `@since` tags corrected from the JDK superclasses' own versions to this project's actual `1.0.0` introduction

### 🛠️ Documentation Consolidation & AI-Agent Tooling

- **`AGENTS.md`/`CLAUDE.md`** — `CLAUDE.md`'s Project Overview, Build & Run Commands, Architecture and Testing
  Patterns sections merged into `AGENTS.md` so any AI coding agent gets the same guidance; `CLAUDE.md` reduced to a
  short pointer
- **AI-agent tooling** migrated from `.claude/commands/*.md` slash commands to `.claude/skills/*/SKILL.md` Skills;
  `generate-pr-description` now runs `sync-unreleased-changes` as a prerequisite step before drafting a release
- New `AGENTS.md` conventions: line wrapping (100–120 characters), an extended Arrange-Act-Assert rule requiring an
  explicit `// Arrange`/`// Act`/`// Assert` comment per phase and a test-helper-placement rule (private
  fixture/setup helpers go after every `@Test` method)
- **`qodana.yaml`** re-added — `jetbrains/qodana-jvm:2026.2` linter on the `qodana.starter` profile, targeting JDK 25

### 🧪 Test Coverage Expansion

The largest single-release test expansion since v5.4.0: full unit and integration coverage for both new
controllers/services, plus new coverage for the `Gender` enum and its converter.

---

## 📦 What's New

### Added

#### Controllers

- `IpscCompetitorController` — full CRUD on `/ipsc/competitors`
- `IpscMatchController` — full CRUD on `/ipsc/matches`

#### Services

- `IpscCompetitorService`/`IpscCompetitorServiceImpl`
- `IpscMatchService`/`IpscMatchServiceImpl`

#### Models

- `CompetitorRequest`, `CompetitorResponse`
- `MatchResponse`, `MatchStageResponse`

#### Converters

- `GenderConverter`

#### Tooling

- `.claude/skills/generate-commit-message`, `generate-pr-description`, `sync-unreleased-changes`,
  `generate-pr-summary`, `scaffold-unit-tests`, `scaffold-integration-tests`
- `qodana.yaml`

#### Tests

- `IpscCompetitorControllerTest`, `IpscCompetitorServiceTest`, `IpscCompetitorServiceIntegrationTest`,
  `IpscCompetitorServiceImplTest`
- `IpscMatchControllerTest`, `IpscMatchServiceTest`, `IpscMatchServiceIntegrationTest`, `IpscMatchServiceImplTest`
- `GenderTest`, `GenderConverterTest`

### Changed

#### Models

- `MatchOverallResultRequest`/`MatchStageResultRequest` renamed to `MatchOverallScoresRequest`/
  `MatchStageScoresRequest` (with CSV variants); their `division`/`club`/`powerFactor`/`categories` fields strongly
  typed instead of free-text `String`s
- `models/ipsc/request` split into `models/ipsc/match/request`/`models/ipsc/scores/request`
- `Placing` moved from `models/shared` to `models/award/shared`

#### Services

- `AwardService.processCsv`/`ImageService.processCsv` renamed to `createAwards`/`createImages`

#### Controllers

- `AwardController`/`ImageController` bulk CSV endpoints moved to `/awards/bulk`/`/images/bulk`, returning
  `201 Created`

#### Enums

- `getByX` factory methods renamed to `fromX` across `ClubIdentifier`, `CompetitorCategory`, `Division`,
  `FirearmType`, `MatchCategory`, `PowerFactor`
- `Gender` gains `name`/`abbreviation`, `fromName()`, `toString()`

#### Tooling

- All `.claude/commands/*.md` slash commands converted to `.claude/skills/*/SKILL.md` Skills

### Removed

#### Controllers

- `IpscController` — superseded by `IpscCompetitorController`/`IpscMatchController`

#### Models

- `MatchStagesRequest` — unused wrapper, never consumed by any controller

#### Tests

- `FatalExceptionTest`, `NonFatalExceptionTest`, `ValidationExceptionTest` — only exercised JDK superclass
  constructor delegation, no project-specific logic

---

## 🔄 Migration Guide

### For API Consumers

- **`IpscController`'s `/ipsc/competitor` endpoint is gone.** Competitor and match management now live at
  `/ipsc/competitors` and `/ipsc/matches` respectively, via `IpscCompetitorController`/`IpscMatchController`.
- **Bulk award/image endpoints moved and changed status code.** `POST /awards`/`POST /images` are now
  `POST /awards/bulk`/`POST /images/bulk`, returning `201 Created` instead of `200 OK`.

### For Developers

- **`AwardService.processCsv`/`ImageService.processCsv`** are now `createAwards`/`createImages` — update any direct
  callers.
- **Enum factory methods renamed.** `getByName`/`getByAbbreviation`/`getByCode`/`getByAbbreviationOrName` are now
  `fromName`/`fromAbbreviation`/`fromCode`/`fromAbbreviationOrName` on `ClubIdentifier`, `CompetitorCategory`,
  `Division`, `FirearmType`, `MatchCategory` and `PowerFactor`.
- **`MatchOverallResultRequest`/`MatchStageResultRequest`** are now `MatchOverallScoresRequest`/
  `MatchStageScoresRequest`, under `za.co.hpsc.web.models.ipsc.scores.request` instead of
  `za.co.hpsc.web.models.ipsc.request`.
- **AI-agent tooling moved.** Anything invoking a `/generate-commit-message`-style slash command should instead use
  the equivalent Skill under `.claude/skills/`.

---

## 📊 Statistics

- **Total Commits:** 68
- **Files Changed:** 123
- **Insertions:** 6,859 lines
- **Deletions:** 1,722 lines
- **Net Change:** +5,137 lines
- **New Source Files:** 11
- **New Test Files:** 10
- **Deleted Test Files:** 3

---

## 🧭 Design Notes

- **Complete the rebuild before extending it further.** v6.0.0 through v7.4.0 deliberately laid IPSC domain-layer
  groundwork (DTOs, shared scoring fields) ahead of the service/controller layer. This release closes that gap with
  real, resource-oriented CRUD rather than adding yet more groundwork on top of an empty stub.
- **Fix naming inconsistencies while the module is already being touched.** `processCsv`→`createAwards`/`createImages`
  and `getByX`→`fromX` were both pre-existing inconsistencies unrelated to the IPSC rebuild itself, but this release
  was a natural point to clear them rather than let them compound further.
- **Consolidate documentation and tooling alongside the domain work.** Merging `CLAUDE.md` into `AGENTS.md` and
  migrating slash commands to Skills isn't IPSC-specific, but both were overdue and benefit from landing in the same
  release as a broader round of Javadoc/`@since` accuracy work.
- **Run `sync-unreleased-changes` before every release from now on.** Auditing this release's own `CHANGELOG.md`
  surfaced several gaps (new test files, a reverse-sync to `ARCHITECTURE.md`, an under-described `ControllerAdvice`
  entry) that a manual pass had missed — `generate-pr-description` now enforces this automatically.

---

## 🧪 Testing

- `./mvnw test` — full suite passing.
- New unit tests: `IpscCompetitorServiceTest`, `IpscMatchServiceTest`, `IpscCompetitorServiceImplTest`,
  `IpscMatchServiceImplTest`, `IpscCompetitorControllerTest`, `IpscMatchControllerTest`, `GenderTest`,
  `GenderConverterTest`.
- New integration tests: `IpscCompetitorServiceIntegrationTest`, `IpscMatchServiceIntegrationTest` — H2-backed,
  covering validation, not-found (404) and unrecognised-value (400) paths, and the full create/replace/patch/get
  contract.
- Mechanical test updates: `ClubIdentifierTest`, `CompetitorCategoryTest`, `DivisionTest`, `FirearmTypeTest`,
  `MatchCategoryTest`, `PowerFactorTest` updated to call the renamed `fromX` factory methods;
  `AwardControllerTest`/`ImageControllerTest`/`AwardServiceTest`/`ImageServiceTest`/their integration tests updated
  for the `createAwards`/`createImages` rename and `201 Created`.

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only — not
  yet wired to any controller.
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried over
  from v7.0.0 – v7.1.0).

---

## 🔮 Future Enhancements

- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint.
- Build a `ShooterLogService` to calculate and persist best-4-match snapshots.
- Populate `overallRanking`, `clubRanking` and `isVisitor` during match-result import; seed `Club.identifier` and
  backfill `Competitor.homeClub`.
- Add entity, repository and integration test coverage for the promoted/extended domain model from v7.0.0.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.0.0 completes the IPSC module rebuild begun as groundwork in v6.0.0: `IpscController`'s empty stub is
replaced by `IpscCompetitorController`/`IpscMatchController`, backed by new services, DTOs and the largest test
expansion since v5.4.0. Alongside the domain work, this release also renames long-standing inconsistent method names,
invests in a comprehensive Javadoc/`@since` documentation pass, consolidates `CLAUDE.md` into `AGENTS.md`, migrates
the project's AI-agent tooling from slash commands to Skills and re-adds Qodana JVM static analysis — marking the
transition from a project with substantial architectural groundwork to one with a genuinely complete, if still
growing, IPSC feature set.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
