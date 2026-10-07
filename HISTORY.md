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

### Version 13.2.0 (October 7, 2026)

**Theme:** Club Number Matching & Backtracking-Free Name Patterns

**Key Focus:**

- `EntityIpscCompetitorServiceImpl.findCompetitor` first matches the trimmed competitor number as a club number with
  `CompetitorRepository.findByClubNumber`, before converting it to a number, so a numeric club number takes precedence
  over a competitor number with the same value; the competitor number, ID number and name lookups follow when no club
  number matches
- `CompetitorHelpers` no longer backtracks: `POSITION_PREFIX` is possessive and now accepts any number of digits at the
  very start of the name, `WHITESPACE` matches only a run of two or more whitespace characters, possessively, and
  `MC_PREFIX` tests the start of the word with `lookingAt()` and a fixed pattern
- The `AwardServiceImpl.mapAwards` null error said "Image request list", copied from the image service, and now says
  "Award request list", and its parameter drops a `@NonNull` that its own null check contradicted
- `ImageResponse.setMimeType` resets a blank MIME type that cannot be inferred from the file name to an empty string,
  as a null one already was
- The archived versions 1.0.0 – 7.4.1 move from `documentation/history/` to `documentation/archive/v1-v7/`, and
  `ARCHIVE.md` moves to `documentation/legacy/`, with the links in the documentation and the `prep-version-release`
  skill updated
- Scoped as `v13.2.0` **MINOR**: the club-number stage is an addition, nothing is backward-incompatible and no migration
  is needed
- No improvement plan gaps are closed or progressed: 35 stay closed, #6, #26 and #38 stay partially completed and #39
  stays open
- The suite stands at 1,280 passing tests
- Project version bumped to 13.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 13.1.0 (October 7, 2026)

**Theme:** Request-Body Validation, jspecify Nullness & Competitor Name Cleaning

**Key Focus:**

- The three IPSC controllers annotate their `@RequestBody` parameters with `@Valid`, and the competitor, match and match
  competitor requests carry `@NotBlank` and `@NotNull` constraints with the messages `validate()` already used, so a
  request missing a required field is rejected on arrival; `ControllerAdvice.handleMethodArgumentNotValidException`
  answers `400 Bad Request` listing every violated constraint instead of the generic `500`
- `org.jspecify.annotations.NonNull` replaces `jakarta.validation.constraints.NotNull` on the services', mappers' and
  response models' parameters and fields, and `org.jspecify:jspecify` is declared as a direct dependency rather than
  received transitively
- `MatchCompetitorRequest.validate()` returns `void` and throws on failure, as the other request models do
- New `CompetitorHelpers.cleanCompetitorName` strips a leading position, an `RO` or `(RO)` marker and full stops and
  collapses whitespace; `MatchCompetitorMapper` cleans the request's name with it and
  `EntityIpscCompetitorServiceImpl.findCompetitor` normalises the full name, so a PractiScore-style
  `1 - John Smith (RO)` finds its competitor
- Documentation tweaks: `flyway-migration-versioning.md`'s Current State table is re-aligned and a missing comma is
  added to `AGENTS.md`
- Scoped as `v13.1.0` **MINOR**: the new `400` response and the broader name matching are externally visible additions,
  and nothing is backward-incompatible
- No improvement plan gaps are closed or progressed: 35 stay closed, #6, #26 and #38 stay partially completed and #39
  stays open
- The suite stands at 1,277 passing tests
- Project version bumped to 13.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 13.0.0 (October 7, 2026)

**Theme:** Mapper Extraction, Division–Firearm Type Consistency & Match Competitor Contract Tightening

**Key Focus:**

- The field copying and lookups that the three IPSC services carried move into new `CompetitorMapper`, `MatchMapper`
  and `MatchCompetitorMapper` components, each with a new `applyPatchFields`, so the services no longer need the
  repositories those lookups used and a request model can be copied onto an entity without one
- `Division` records the `FirearmType` it is shot with, and every division has a name of its own: the shotgun, .22 and
  mini rifle divisions that shared a handgun name are renamed, `V11_4_0` renames the stored values by firearm type, and
  `V11_5_0` renames the stored category `Lady, Senior` to `Lady Senior`
- A match competitor's division must belong to its firearm type — checked in `MatchCompetitorRequest.validate` and in
  `MatchCompetitorMapper` — and the firearm type may now be left out, in which case it is taken from the division
- **Breaking:** `MatchCompetitorResponse` replaces `competitorName` with a `competitorNames` list, so the JSON of the
  match competitor endpoints and the bulk import results changes shape
- **Breaking:** `powerFactor` is required on a match competitor request, as `firearmType` already was, and both columns
  are `NOT NULL` after `V11_2_0`; `firearmType` is then relaxed again, so a request may omit it
- **Breaking:** the accepted division names change with the renames above, and a request using the old `Lady, Senior`
  category name is rejected
- The match competitor's score fields move into `IpscMatchScore`, which `MatchCompetitorRequest` and
  `MatchCompetitorPatchRequest` now extend, `weightedPoints` is renamed `points` and the power factor leaves the score
  models
- `MatchCompetitor` gains an optional `date_calculated` column, the unused `code` and `abbreviation` fields and the
  lookups that only tests called are removed from the enums, and `CompetitorCategory` gains an abbreviation
- `CompetitorRequest`, `MatchRequest` and `MatchCompetitorRequest` each carry their own `validate()`, and null contracts
  are marked with `@Nullable`
- Improvement plan Gap #38 is partially completed and Gap #39 is recorded; `.gitattributes` normalises text files to LF
  and `qodana.yaml` moves to the `qodana.recommended` profile
- Scoped as `v13.0.0` **MAJOR**: the response shape, the required power factor and the renamed division and category
  names are backward-incompatible and flagged `**Breaking:**` in `CHANGELOG.md`
- Project version bumped to 13.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`
- The suite stands at 1,265 passing tests

### Version 12.0.0 (October 5, 2026)

**Theme:** Match Competitor Lookup, Partial Bulk Import & Club Filtering

**Key Focus:**

- `EntityIpscCompetitorService.findCompetitor(competitorNumber, fullName)` is wired into the match competitor service:
  the number is a string that may also be an ID number, the lookup tries the competitor number, the ID number and then
  the full name, and a lookup that does not find exactly one competitor throws instead of returning an empty result
- **Breaking:** the shared alias numbers 15000 and 16000 no longer match a competitor by number, and a number that is
  not a whole number is looked up as an ID number, so an unmatched one answers `404` instead of being refused up front
  with a `400`
- **Breaking:** the match competitor bulk import is a partial import — each row is saved on its own, and a row that
  fails is reported and skipped while the rest are created; an import in which every row fails answers `422`
- **Breaking:** the bulk response models are renamed `MatchCompetitorBulkResponse` and
  `MatchCompetitorBulkResponseHolder`, and the response body carries `matchCompetitors` instead of
  `matchCompetitorResults`, with each result naming its competitor by `competitorName` and `competitorNumber`
- **Breaking:** the bulk import is limited to one club, HPSC's own club unless an optional `club` query parameter asks
  for another: only the rows whose match club, or whose competitor's home club, is that club are created, and the rest
  are reported as skipped; a new `ClubService` holds the null-safe club comparisons
- A competitor created or updated without a nickname takes its first name as the nickname, and the range officer marker
  `RO` or `(RO)` is removed from a name wherever it appears when a competitor is looked up by name
- A match no longer needs a match category: one that is omitted takes the default, Club Shoot, and a supplied one may be
  given by display name or constant name, ignoring case and surrounding whitespace
- `ControllerResponse` derives `success` from its error the right way round, and `findCompetitor` no longer fails on a
  numeric value too long for an `int`
- The `*Utils` classes are renamed back to `DateUtil`, `NumberUtil`, `StringUtil` and `ValueUtil`, `StringUtil` gains
  `hasText` and the unused `MAX_SAPSA_NUMBER` and four score-scale constants are removed from `IpscConstants`, whose
  remaining members are documented
- Improvement plan Gaps #36 and #37 are closed, and a Qodana configuration and a `mysql-connector-j` pin are added
- Scoped as `v12.0.0` **MAJOR**: four groups of changes are backward-incompatible and flagged `**Breaking:**` in
  `CHANGELOG.md` — the alias numbers and ID-number lookup, the partial bulk import, the renamed response field and
  the HPSC-limited bulk import
- Project version bumped to 12.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`
- The suite stands at 1,227 passing tests

### Version 11.0.0 (October 4, 2026)

**Theme:** Competitor Contract Tightening & Competitor Lookup Service

**Key Focus:**

- Every `POST /bulk` endpoint — awards, images, competitors, matches and match competitors — now consumes `text/plain`
  rather than `text/csv`, and answers `415` to anything else; the body is unchanged
- The competitor's nickname is renamed `nickname` → `nickName` in the entity field, the JSON property and the CSV
  column (`Nickname` → `NickName`), and the `nickname` column becomes `nick_name` through the `V11_0_0` migration
- `competitorNumber` becomes a whole number: `Competitor.competitorNumber` is an `Integer` held in an `INT` column
  through `V11_1_0`, which is refused when existing data is non-numeric; competitor and match competitor requests (JSON
  and CSV `CompetitorNumber`/`Mem #`) still send a string, but it must be a whole number, a blank means not supplied
  and a blank in a patch leaves the stored number unchanged, and `CompetitorResponse` returns a whole number
- New `EntityIpscCompetitorService.findCompetitor(fullName, competitorNumber)` resolves one competitor by number,
  skipping the excluded ICS aliases 15000 and 16000, and then by full name — "FirstName LastName" or
  "NickName LastName", ignoring case, with an RO suffix stripped — narrowed to the number matches; it is not yet wired
  into a caller
- New `MatchCompetitorResult` and `MatchCompetitorResultHolder` response models, unused so far
- `CompetitorRepository`'s finders are renamed `findAll…` and gain
  `findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase`; `IpscConstants.MAX_SAPSA_NUMBER` rises from 99,999 to
  999,999 and `EXCLUDE_ICS_ALIAS` becomes a `List<Integer>`; `IpscCompetitorServiceImpl.resolveCompetitorNumber`
  falls back to the SAPSA number
- Three-tier tests for the new service take the suite to 1,109 passing tests
- Scoped as `v11.0.0` **MAJOR**: three groups of changes are backward-incompatible and flagged `**Breaking:**` in
  `CHANGELOG.md` — the `text/plain` bulk endpoints, the `nickName` rename and the whole-number `competitorNumber`
- Project version bumped to 11.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 10.0.0 (October 3, 2026)

**Theme:** PractiScore-Style Match Competitor Import & Overall Scores

**Key Focus:**

- `MatchCompetitor` gains the overall-score columns a PractiScore export carries — `percentage`, `time`,
  `percentage_of_possible_points`, the `alpha`, `charlie` and `delta` hit counts, `misses`, `no_penalty_misses`,
  `no_shoots`, `procedural_errors` and `additional_penalties` — as optional fields on `MatchCompetitorRequest`,
  `MatchCompetitorPatchRequest` and `MatchCompetitorResponse`, and `matchPoints` is renamed `points` in the JSON
  contract and the table
- The match competitor CSV import takes a PractiScore export's headers (`Class`, `Cats`, `Div`, `PF`, `Pts`, `%`,
  `Time`, `% psbl`, `A`, `C`, `D`, `M`, `NPM`, `NS`, `Proc`, `Apen`), so a results export can be imported without
  renaming its columns
- `competitorId` is no longer required: a request can identify its competitor by `competitorNumber` (CSV `Mem #`),
  matched exactly, or by `name` (CSV `Name`), matched as "First Last" ignoring case, through new
  `CompetitorRepository.findByCompetitorNumber` and `findByFullNameIgnoreCase` finders; a number or name that matches no
  competitor, or several, is refused
- `competitorCategory` returns to a single category in a `competitor_category` column on `MatchCompetitor` and
  `ShooterLogCompetitor`, replacing the list and the two child tables that v9.1.0 introduced; `V10_2_0` collapses
  existing rows to their alphabetically first category
- The unused `ipsc.scores.request` package — `MatchOverallScoresRequest`, `MatchStageScoresRequest` and their CSV
  forms, which modelled the removed stage tables — is deleted, its overall-score fields now living on
  `MatchCompetitor`
- Four Flyway migrations, `V10_0_0` to `V10_3_0`; running them against an empty MySQL 8.4 database exposed that
  `V10_2_0` placed a column `AFTER` one that `V8_3_0` had already dropped, so it failed on every database, and it now
  places the column after `match_competitor_id`
- The release's improvement-plan sweep recorded Gaps #33 and #34 — the Flyway versioning document's table and
  `ARCHITECTURE.md`'s deleted DTO package — and closed both, leaving no open gap; Gap #6 stays partially completed
- Scoped as `v10.0.0` **MAJOR**: three changes are backward-incompatible and flagged `**Breaking:**` in `CHANGELOG.md`
  — `matchPoints` renamed to `points`, the renamed CSV headers, and `competitorCategory` a single value rather than a
  list
- Project version bumped to 10.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

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

The entries for versions 1.0.0 to 7.4.1 are archived, unchanged, in
[`documentation/archive/v1-v7/HISTORY_v1-v7.md`](/documentation/archive/v1-v7/HISTORY_v1-v7.md).

---

## 📖 Evolution Overview

The full Phase-by-phase narrative for every release has moved to
[`EVOLUTION_OVERVIEW.md`](/EVOLUTION_OVERVIEW.md) to keep this file a
manageable size, since that narrative alone had grown to roughly half of it. See the
[📅 Historical Timeline](#-historical-timeline) above for the same releases summarised more concisely, and
[🎯 Major Milestones](#-major-milestones) below for each release's headline achievement.

---

## 🎯 Major Milestones

Milestones 1 to 18 (versions 1.0.0 to 7.4.1) are archived, unchanged, in
[`documentation/archive/v1-v7/HISTORY_v1-v7.md`](/documentation/archive/v1-v7/HISTORY_v1-v7.md).

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

### Milestone 44: PractiScore-Style Match Competitor Import (v10.0.0)

- A PractiScore results export imports into the match competitor endpoints without renaming its columns, and carries
  its overall scores — percentage, time, hit counts, misses and penalties — onto `MatchCompetitor`
- A row no longer needs a database id: the competitor is found by membership number or by name, and an ambiguous or
  unknown match is refused rather than guessed
- `competitorCategory` goes back to a single column, undoing the child tables v9.1.0 added once a PractiScore row was
  seen to carry one category
- Project's second MAJOR release in a row, with three breaking changes flagged `**Breaking:**` in `CHANGELOG.md` as
  they landed

**Achievement:** Aligned the match competitor model and CSV import to the format the data actually arrives in, and
proved the full migration chain by running it against an empty database, which caught a migration that could never
have run.

### Milestone 45: Competitor Contract Tightening & Lookup Service (v11.0.0)

- Every bulk endpoint declares the one content type it reads, `text/plain`, so a client sending `text/csv` is told so
  with a `415` rather than silently accepted
- The competitor's nickname is spelt `nickName` everywhere — entity, JSON, CSV and column — and `competitorNumber` is
  a whole number in the entity and the table, while requests still send it as a string that must parse as one
- A competitor can be found from a name and a number together: number first, then name, with the ICS aliases skipped
  and a nickname accepted in place of a first name, ready for a caller to use
- Project's third MAJOR release in a row, with three groups of breaking changes flagged `**Breaking:**` in
  `CHANGELOG.md` as they landed

**Achievement:** Settled the competitor's contract — one content type, one spelling and one type for the membership
number — and built the lookup that depends on it, with a migration that refuses data it cannot convert rather than
losing it.

### Milestone 46: Match Competitor Lookup, Partial Import & Club Filtering (v12.0.0)

- A match competitor request can name its competitor by number, ID number or name, and the lookup that finds it is the
  one `EntityIpscCompetitorService` built in the previous release, now with a caller
- The bulk import reports each row on its own, so one bad row no longer costs the whole file, and it imports one club's
  rows, HPSC's unless another is asked for
- A match no longer has to carry a category, and the club comparisons live in one `ClubService` instead of being
  repeated
- Project's fourth MAJOR release in a row, with four groups of breaking changes flagged `**Breaking:**` in
  `CHANGELOG.md` as they landed

**Achievement:** Put the competitor lookup to work and made the bulk import tolerant — a row is created, skipped or
failed, and said so — while narrowing it to the club the data is for.

### Milestone 47: Mappers, Division–Firearm Type Consistency & Match Competitor Contract Tightening (v13.0.0)

- The services stop doing their own field copying and lookups: three mapper components own them, and each gained the
  partial-update logic a patch needs
- A division knows its firearm type and has a name of its own, so a match competitor can be checked for a division that
  does not belong to its firearm type, and a division can be read back from its stored name alone
- A match competitor response lists its competitor's names once each, a request must carry a power factor, and a firearm
  type left out is taken from the division
- Project's fifth MAJOR release in a row, with the breaking changes flagged `**Breaking:**` in `CHANGELOG.md` as they
  landed

**Achievement:** Separated mapping from orchestration and made the match competitor's enumerated values agree with each
other — a division belongs to a firearm type, and each name identifies one division — with migrations that rewrite the
stored values to match.

### Milestone 48: Request-Body Validation, jspecify Nullness & Competitor Name Cleaning (v13.1.0)

- A request body missing a required field is now refused with a `400` by Bean Validation before it reaches the service,
  instead of surfacing as a `500`
- Nullness is expressed with jspecify's `@NonNull`, a direct dependency, in place of Jakarta's `@NotNull`, which only
  looked like a validation constraint on parameters it never validated
- A competitor name carrying a leading position, an `RO` marker or full stops now finds its competitor
- The first MINOR release since v9.1.0, and the end of the run of five consecutive MAJOR releases

### Milestone 49: Club Number Matching & Backtracking-Free Name Patterns (v13.2.0)

- A numeric club number now finds its competitor ahead of a competitor number with the same value
- The competitor name patterns are possessive or fixed, so none of them backtracks, and a leading position may be any
  number of digits
- The award list's null error now names the right request list, and a blank image MIME type is reset to an empty string
- The archived v1 – v7 history moves to `documentation/archive/v1-v7/`

---

## 🏛️ Architectural Evolution

The entries for versions 1.0.0 to 7.2.0 are archived, unchanged, in
[`documentation/archive/v1-v7/HISTORY_v1-v7.md`](/documentation/archive/v1-v7/HISTORY_v1-v7.md).

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

### v10.0.0: PractiScore-Style Match Competitor Import

```
PractiScore CSV ──(CsvMapper + MatchCompetitorRequestCsvMixIn)──→ MatchCompetitorRequest
                                                                       ↓
                 IpscMatchCompetitorServiceImpl: competitorId → competitorNumber → name
                                                                       ↓
                                   CompetitorRepository.findByCompetitorNumber / findByFullNameIgnoreCase
```

**Characteristics:**

- A request identifies its competitor in priority order — id, then number, then name — and the service refuses a
  number or name that matches none or several, so an import never attaches a result to a guessed competitor
- The CSV mix-in's column names follow a PractiScore export, so the import format is the export format; the JSON field
  names stay descriptive (`points`, `percentageOfPossiblePoints`)
- `MatchCompetitor` holds its overall scores as optional columns, and `competitorCategory` is a single value again on
  both `MatchCompetitor` and `ShooterLogCompetitor`, with no child tables

### v11.0.0: Typed Competitor Number & Competitor Lookup Service

```
request (competitorNumber: String, "NickName"/"FirstName" + LastName)
                          ↓  whole-number check — refused if not an integer, blank = not supplied
Competitor.competitorNumber (Integer, INT column)
                          ↓
EntityIpscCompetitorService.findCompetitor(fullName, competitorNumber)
        number (ICS aliases 15000/16000 skipped) → full name, narrowed to the number matches
                          ↓
CompetitorRepository.findAll… / findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase
```

**Characteristics:**

- The request models keep `competitorNumber` a string, so the JSON and CSV contract reads the same, and the whole-number
  rule is enforced by the service when it reads the request; only the entity, the table and `CompetitorResponse` hold a number
- Number resolution comes before name resolution, and the name only narrows the number's matches, so a name cannot
  select a different competitor than the number does
- The lookup sits behind the entity service layer and is not yet called, so the competitor and match competitor
  services still resolve competitors as v10.0.0 did
- Every bulk endpoint reads `text/plain`, so the content type is the same for all five, whatever the resource

### v12.0.0: Partial Bulk Import & Club-Filtered Match Competitors

```
POST /ipsc/match-competitors/bulk?club=...  (CSV as text/plain)
                          ↓
IpscMatchCompetitorServiceImpl.createMatchCompetitors(csvData, club)
        club → target club (HPSC's own club by default)
                          ↓  for each row, on its own
isForClub: matchClub is the target club, else the competitor's home club is  (ClubService.isSameClub)
                          ↓                       ↓ no → "Skipped" result
validateForCreate → applyFields → resolveCompetitor
        competitor ID, else EntityIpscCompetitorService.findCompetitor(number, fullName)
                          ↓
save (own transaction)  →  MatchCompetitorBulkResponse(success, message, row)
                          ↓
MatchCompetitorBulkResponseHolder (201, or 422 if every row failed)
```

**Characteristics:**

- Each row is its own unit of work, so a row that fails or is skipped is reported with its reason and the rest are
  still created; only unreadable CSV or a missing header column fails the whole request
- The club filter runs before a row is validated, so a row for another club is skipped rather than failed, while a row
  for the club that is invalid is still reported as failed
- Club comparisons go through `ClubService`, so `IpscCompetitorServiceImpl` and the match competitor service no longer
  each repeat the null checks
- A match's category and club both default when omitted, so a match request needs only a name, a date and a firearm
  type

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
- **Version 13.x (v13.0.0 – v13.2.0):** Make the match competitor's values consistent and the services thinner — field
  copying and lookups move into mappers, a division belongs to a firearm type and has a name of its own, and a response
  lists a competitor's names once each. Because that changes the shape of a response field, requires a power factor and
  changes the division and category names a request may use, v13.0.0 is the project's fifth consecutive MAJOR release,
  with each breaking change flagged in `CHANGELOG.md` as it landed. v13.1.0 then makes the contract enforceable at the
  door and forgiving of the data's names — `@Valid` request bodies answer `400`, jspecify's `@NonNull` expresses
  nullness and `CompetitorHelpers.cleanCompetitorName` normalises a competitor's name — as a backward-compatible MINOR.
  v13.2.0 then tightens that matching — a numeric club number takes precedence over a competitor number of the same
  value, and the name-cleaning patterns no longer backtrack — as a second backward-compatible MINOR.

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

Based on the evolution to v13.2.0, the following areas are identified for future enhancement:

The completed-work logs for versions 5.4.0 and earlier up to 7.2.0 are archived, unchanged, in
[`documentation/archive/v1-v7/HISTORY_v1-v7.md`](/documentation/archive/v1-v7/HISTORY_v1-v7.md).

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
- All 52 archived release notes/PR descriptions regrouped into `documentation/archive/v1-v7/v1`–`v8/` by major version,
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

### Recently Completed (v13.2.0)

- `EntityIpscCompetitorServiceImpl.findCompetitor` matches the competitor number as a club number first, with
  `CompetitorRepository.findByClubNumber`
- `CompetitorHelpers`' `POSITION_PREFIX`, `WHITESPACE` and `MC_PREFIX` patterns no longer backtrack
- `AwardServiceImpl.mapAwards` names the "Award request list" in its null error
- Improvement plan unchanged: 35 gaps closed, #6, #26 and #38 partially completed and #39 open
- The suite stands at 1,280 passing tests
- Project version bumped to 13.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v13.1.0)

- `@Valid` on the three IPSC controllers' request bodies, with Bean Validation constraints on the requests and
  `ControllerAdvice.handleMethodArgumentNotValidException` answering `400 Bad Request` for a violation
- `org.jspecify.annotations.NonNull` replaces `jakarta.validation.constraints.NotNull` and `org.jspecify:jspecify` is a
  direct dependency; `MatchCompetitorRequest.validate()` returns `void`
- New `CompetitorHelpers.cleanCompetitorName`, used by `MatchCompetitorMapper` and by
  `EntityIpscCompetitorServiceImpl.findCompetitor`
- Improvement plan unchanged: 35 gaps closed, #6, #26 and #38 partially completed and #39 open
- Project version bumped to 13.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v13.0.0)

- New `CompetitorMapper`, `MatchMapper` and `MatchCompetitorMapper` components holding the services' field copying and
  lookups, each with an `applyPatchFields`, documented in `ARCHITECTURE.md`
- `Division` gains a `FirearmType` and unique names, with `V11_4_0` renaming the stored divisions and `V11_5_0` renaming
  the stored category `Lady, Senior` to `Lady Senior`
- A match competitor's division is checked against its firearm type, and a missing firearm type is taken from the
  division
- `MatchCompetitorResponse.competitorNames` replaces `competitorName`, `powerFactor` is required and `firearmType` and
  `powerFactor` are `NOT NULL` columns, and `MatchCompetitor` gains an optional `date_calculated` column
- Score fields moved into `IpscMatchScore`, `weightedPoints` renamed `points`, and the unused `code`, `abbreviation`
  and lookup methods removed from the enums
- Improvement plan Gap #38 partially completed and Gap #39 recorded, `.gitattributes` normalised to LF and `qodana.yaml`
  moved to the `qodana.recommended` profile
- The suite stands at 1,265 passing tests
- Project version bumped to 13.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v12.0.0)

- `EntityIpscCompetitorService.findCompetitor(competitorNumber, fullName)` wired into the match competitor service,
  matching the number, then the ID number, then the full name, with the alias numbers 15000 and 16000 no longer matched
- Match competitor bulk import made a partial import with per-row results, the response models renamed
  `MatchCompetitorBulkResponse` and `MatchCompetitorBulkResponseHolder` and an import in which every row fails answering
  `422`
- Bulk import limited to one club, HPSC's unless a `club` parameter asks for another, with a new `ClubService`
- Competitor nickname defaulting to the first name, the RO marker removed from a name wherever it appears, an optional
  match category and `ControllerResponse.success` fixed
- `*Util` classes renamed back to the singular, `StringUtil.hasText` added and `IpscConstants` documented and pruned
- Improvement plan Gaps #36 and #37 closed, a Qodana configuration added and `mysql-connector-j` pinned
- The suite stands at 1,227 passing tests
- Project version bumped to 12.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v11.0.0)

- Every `POST /bulk` endpoint consumes `text/plain` instead of `text/csv`, answering `415` otherwise
- Competitor `nickname` renamed `nickName` in the entity, the JSON property and the CSV column (`NickName`), with the
  column renamed `nick_name` by `V11_0_0`
- `Competitor.competitorNumber` an `Integer` in an `INT` column by `V11_1_0`, refused when existing data is
  non-numeric; requests still send a string that must be a whole number, and `CompetitorResponse` returns one
- New `EntityIpscCompetitorService.findCompetitor`, resolving a competitor by number and then by full name, not yet
  wired into a caller, and the unused `MatchCompetitorResult` and `MatchCompetitorResultHolder` response models
- `CompetitorRepository` finders renamed `findAll…` with the new
  `findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase`; `IpscConstants.MAX_SAPSA_NUMBER` raised to 999,999 and
  `EXCLUDE_ICS_ALIAS` a `List<Integer>`; `resolveCompetitorNumber` falls back to the SAPSA number
- Three-tier tests for the new service, taking the suite to 1,109 passing tests
- Project version bumped to 11.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v10.0.0)

- `MatchCompetitor` gains the overall-score columns and `MatchCompetitorRequest`/`MatchCompetitorPatchRequest`/
  `MatchCompetitorResponse` the matching optional fields, with `matchPoints` renamed `points`, by `V10_0_0` and
  `V10_1_0`
- `MatchCompetitorRequestCsvMixIn` takes a PractiScore export's headers, and the controller's CSV example shows them
- `competitorId` optional: `competitorNumber` and `name` resolve the competitor through the new
  `CompetitorRepository.findByCompetitorNumber` and `findByFullNameIgnoreCase`
- `competitorCategory` a single value again on `MatchCompetitor` and `ShooterLogCompetitor`, replacing the child tables
  by `V10_2_0`, whose column placement is fixed so the migration chain runs from an empty database
- `MatchOverallScoresRequest`, `MatchOverallScoresRequestForCSV`, `MatchStageScoresRequest` and
  `MatchStageScoresRequestForCSV` and their tests deleted
- Project version bumped to 10.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

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
- Dependency vulnerabilities addressed: `tomcat.version` `11.0.26`, `logback.version` `1.6.5`, Jackson BOM properties raised and
  `flyway-mysql` pinned

### Previously Completed (v8.12.0)

- New `StringUtils.toProperCase` (backed by the new `commons-text` dependency) and `CompetitorHelpers.toSentenceCaseLastName`
  (the new `za.co.hpsc.web.helpers` package's first class) normalise casing on competitor CSV import
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
