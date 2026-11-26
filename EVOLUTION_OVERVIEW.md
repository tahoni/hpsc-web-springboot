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

Phases 1 to 7 (versions 1.0.0 to 4.1.0) are archived, unchanged, in
[`documentation/archive/v1-v4/EVOLUTION_OVERVIEW_v1-v4.md`](/documentation/archive/v1-v4/EVOLUTION_OVERVIEW_v1-v4.md).

Phases 8 to 18 (versions 5.0.0 to 7.4.1) are archived, unchanged, in
[`documentation/archive/v5-v7/EVOLUTION_OVERVIEW_v5-v7.md`](/documentation/archive/v5-v7/EVOLUTION_OVERVIEW_v5-v7.md).

Phases 19 to 43 (versions 8.0.0 to 9.1.0) are archived, unchanged, in
[`documentation/archive/v8-v9/EVOLUTION_OVERVIEW_v8-v9.md`](/documentation/archive/v8-v9/EVOLUTION_OVERVIEW_v8-v9.md).

Phases 44 to 50 (versions 10.0.0 to 13.3.0) are archived, unchanged, in
[`documentation/archive/v10-v13/EVOLUTION_OVERVIEW_v10-v13.md`](/documentation/archive/v10-v13/EVOLUTION_OVERVIEW_v10-v13.md).

Phases 51 to 60 (versions 14.0.0 to 16.4.1) are archived, unchanged, in
[`documentation/archive/v14-v16/EVOLUTION_OVERVIEW_v14-v16.md`](/documentation/archive/v14-v16/EVOLUTION_OVERVIEW_v14-v16.md).

---

### Phase 61: Reporting Failed Rows in the Competitor and Match Imports (v17.0.0)

**Controllers & Services**

- `IpscCompetitorServiceImpl.createCompetitors` and `IpscMatchServiceImpl.createMatches` save each row in its own
  transaction, so a row that is missing a required field, has an unrecognised gender, firearm type or category, names an
  unknown club or lacks a required club number is reported and skipped instead of failing the whole request with a
  `400` or `404`; a CSV that is blank or cannot be parsed still fails the request
- A competitor row that repeats a club number an earlier row of the same file already imported is reported as failed
  instead of silently updating the competitor that row just saved
- `MatchCompetitorMapper.resolveFirearmType`, `IpscMatchCompetitorServiceImpl` and `IpscShooterLogServiceImpl` lose
  branches that could never run, with no change in behaviour
- `TransactionService.saveMatches`, `saveMatchCompetitors` and `saveShooterResults` are removed with their tests, as no
  production code called them

**Models**

- `MatchBulkResponseHolder` replaces `MatchResponseHolder` and carries a `MatchBulkResponse` for every row, with its
  `rowNumber`, `success`, `message`, the created `match` and, for a failed row, a `matchRow` holding every value the row
  supplied
- `CompetitorBulkResponse` gains `rowNumber`, `success` and a `competitorRow`, and `CompetitorResponseHolder` becomes
  `CompetitorBulkResponseHolder`; `MatchCompetitorBulkResponse` and `ShooterLogBulkResponse` gain `rowNumber`
- `CompetitorRow` and `MatchRow` report a failed row's values as text, like `MatchCompetitorRow`
- `MatchResponse.warnings` is renamed `messages`

**Documentation**

- `ARCHITECTURE.md` describes the competitor and match bulk imports as saving each row in its own transaction, ending in
  the new result holders, and the controller OpenAPI text and service Javadoc say the same
- Versions 14.0.0 to 16.4.1 are archived, unchanged, in `documentation/archive/v14-v16/`, with their release notes and
  pull request descriptions

**Build & Metadata**

- Improvement plan: Gaps #51 and #52 are added and closed, leaving all 52 gaps closed and none open
- Project version bumped to 17.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Giving the competitor and match imports the per-row, report-and-continue behaviour that the match competitor and
  shooter log imports already had, and scoping the release as a MAJOR for the breaking response changes

**Test Coverage:**

- The suite stands at 1,601 tests, three skipped unless `FLYWAY_MYSQL_URL` is set, with new tests for the row classes,
  the `messages` JSON, failed rows, row numbers, a repeated club number, data integrity conflicts and CSV read failures,
  taking line coverage to 99.2% and branch coverage to 100%

---

### Phase 62: Recording the Matches Behind a Shooter Log Overall (v18.0.0)

**Database & Domain**

- `V11_13_0__create_shooter_log_overall_match.sql` creates the `shooter_log_overall_match` join table, with foreign keys
  to `shooter_log_overall` and `ipsc_match` and an index on `match_id`
- `ShooterLogOverall.matches` keeps the matches an overall was calculated from, being the competitor's best
  `SHOOTER_LOG_BEST_MATCHES` results in the shooter log
- The lazy associations of `Competitor`, `IpscMatch`, `MatchCompetitor`, `ShooterLog`, `ShooterLogOverall` and
  `ShooterResult` are left out of the generated `equals`, `hashCode` and `toString`: Hibernate compares the matches of
  an overall on commit, which read a match's lazy club and failed with a `LazyInitializationException` outside a session

**Controllers & Services**

- `IpscShooterLogOverallServiceImpl.calculateShooterLogOverall` picks each competitor's best results once, averages
  those same results, records their matches, takes equal percentages from the most recent match first and recalculates
  an overall calculated before its matches were recorded or from other matches
- `ShooterLogOverallRepository.findAllByShooterLogIdWithDetails` and `findAllWithDetails` fetch each overall's matches

**Models**

- `ShooterLogOverallResponse.matchIds` lists the identifiers of the matches an overall was calculated from

**Configuration**

- `springdoc.api-docs.enabled` and `springdoc.swagger-ui.enabled` are `false` in `application.properties` and `true` in
  `application-dev.properties`, so the OpenAPI document and Swagger UI are served only under `dev`, flagged **Breaking**

**Documentation**

- `README.md`, `AGENTS.md`, `ARCHITECTURE.md` and `CONTRIBUTING.md` say the OpenAPI document and Swagger UI are served
  only under `dev`; `ARCHITECTURE.md` lists `ShooterLogOverall`'s link to `IpscMatch` and drops the removed
  `exceptions/` test directory, and `AGENTS.md` describes the service test tiers as they are
- `@since` tags are added to the four mapper classes and 74 public and package-private methods and constructors

**Build & Metadata**

- Improvement plan: Gaps #53, #54 and #55 are added and closed, leaving all 55 gaps closed and none open
- Project version bumped to 18.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Making an overall say which matches it was calculated from, making the calculation safe once the session has closed,
  and scoping the release as a MAJOR for taking the OpenAPI document and Swagger UI off by default

**Test Coverage:**

- The suite stands at 1,495 tests, three skipped unless `FLYWAY_MYSQL_URL` is set, with line coverage at 98.6% and
  branch coverage at 99.9%; brittle tests are removed or loosened, and new cases cover the recorded matches, the fetch
  joins and the calculation without a surrounding transaction

---

### Phase 63: Ranking Competitors Across the Whole Match (v18.1.0)

**Database & Domain**

- `V11_14_0__create_match_result.sql` creates the `match_result` table, with foreign keys to `ipsc_match`, `competitor`
  and `match_competitor`
- `MatchResult` has the same structure as `ShooterResult`, and ranks a competitor in the match overall, independent of
  competitor category and division

**Controllers & Services**

- `IpscMatchResultController` serves `POST /ipsc/match-results/matches/{matchId}`,
  `GET /ipsc/match-results/matches/{matchId}` and `GET /ipsc/match-results`
- `IpscMatchResultServiceImpl` ranks a match's verified competitors with points above zero together by match points,
  shares a rank between competitors on equal points and skips the next, and sets each percentage against the winner's
  points; it recalculates only when the match, or a competitor, was updated at least five minutes after the results
  were calculated, a competitor has no result yet or a result belongs to a competitor who no longer qualifies
- `TransactionService.replaceMatchResults` saves and deletes a match's results in one transaction, flushing so a
  constraint violation surfaces from the call
- `MatchResultRepository` reads a match's results, and every match's, with their associations fetched

**Models**

- `MatchResultResponse`, `MatchResultMatchResponse` and `MatchResultResponseHolder` return a match linked to its results

**Configuration**

- `logback.version` is raised from `1.6.5` to `1.6.7`, and `h2.version` is pinned to `2.5.252`, since the `2.4.240`
  Spring Boot manages has a known vulnerability

**Documentation**

- `ARCHITECTURE.md`, `README.md` and `flyway-migration-versioning.md` describe `MatchResult`, its controller, service,
  response models and migration
- Link definitions are added for the version headings of the archived changelogs, and bracketed text that is not a
  link is escaped

**Build & Metadata**

- Improvement plan: Gap #56 is added and closed, leaving all 56 gaps closed and none open
- Project version bumped to 18.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Ranking each verified competitor against the whole match rather than only within their firearm type, division and
  category, and scoping the release as a MINOR for adding endpoints without changing any existing contract

**Test Coverage:**

- The suite stands at 1,537 tests, three skipped unless `FLYWAY_MYSQL_URL` is set, with line coverage at 98.7% and
  branch coverage at 99.9%; new cases cover the calculation, ranking, the staleness rule, the controller endpoints
  and the response mapping

---

### Phase 64: Ranking Competitors Within Their Division (v18.1.1)

**Services**

- `IpscShooterResultServiceImpl` ranks a match's verified competitors with points within their firearm type and
  division, and `IpscShooterLogOverallServiceImpl` ranks a shooter log's overalls within their division, instead of
  also splitting each group by competitor category; the category is still stored on every `ShooterResult` and
  `ShooterLogOverall`

**Configuration**

- `tomcat.version` is pinned to `11.0.29`, since the `11.0.27` Spring Boot manages has a known vulnerability

**Documentation**

- The Javadoc and OpenAPI text of both services and controllers, `ARCHITECTURE.md` and `README.md` describe the new
  ranking, closing Gap #57

**Build & Metadata**

- Improvement plan: Gap #57 is added and closed, leaving all 57 gaps closed and none open
- Project version bumped to 18.1.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Ranking competitors against the others in their division whatever their category, and scoping the release as a
  PATCH because no endpoint or response field changes

**Test Coverage:**

- The suite stands at 1,538 tests, three skipped unless `FLYWAY_MYSQL_URL` is set, with line coverage at 98.7% and
  branch coverage at 99.9%; the category tests now expect one ranking, and a new overall test does the same
