# HPSC Website Backend Change Log Archive (v14.0.0 – v16.4.1)

The change log entries for versions 14.0.0 to 16.4.1 of the HPSC Website Backend project, archived from
[`CHANGELOG.md`](/CHANGELOG.md) to keep that file a manageable size. The entries are moved unchanged. See
[`CHANGELOG.md`](/CHANGELOG.md) for the current version, the unreleased changes and versions 17.0.0 onwards,
[`HISTORY_v14-v16.md`](/documentation/archive/v14-v16/HISTORY_v14-v16.md) for the archived narrative history of the
same versions, [`CHANGELOG_v10-v13.md`](/documentation/archive/v10-v13/CHANGELOG_v10-v13.md) for versions 10.0.0 to
13.3.0, [`CHANGELOG_v8-v9.md`](/documentation/archive/v8-v9/CHANGELOG_v8-v9.md) for versions 8.0.0 to 9.1.0,
[`CHANGELOG_v5-v7.md`](/documentation/archive/v5-v7/CHANGELOG_v5-v7.md) for versions 5.0.0 to 7.4.1 and
[`CHANGELOG_v1-v4.md`](/documentation/archive/v1-v4/CHANGELOG_v1-v4.md) for versions 1.0.0 to 4.1.0.

---

### Table of Contents

- [🧾 Version 16.4.1](#-1641---2026-11-10)
- [🧾 Version 16.4.0](#-1640---2026-11-10)
- [🧾 Version 16.3.0](#-1630---2026-11-10)
- [🧾 Version 16.2.0](#-1620---2026-11-10)
- [🧾 Version 16.1.2](#-1612---2026-11-09)
- [🧾 Version 16.1.1](#-1611---2026-11-08)
- [🧾 Version 16.1.0](#-1610---2026-11-08)
- [🧾 Version 16.0.0](#-1600---2026-10-31)
- [🧾 Version 15.0.0](#-1500---2026-10-17)
- [🧾 Version 14.0.0](#-1400---2026-10-09)

---

### 🧾 [16.4.1] - 2026-11-10

#### 🔄 Changed

##### Services

- **`IpscMatchCompetitorServiceImpl.resolveCompetitorHomeClub`:** Returns an `Optional<ClubIdentifier>`, empty when the
  competitor has no home club or the home club has no identifier, instead of `null`. `isForClub` passes the value on
  through the null-safe `isSameClub`, so which rows the import counts as the club's does not change.

---

### 🧾 [16.4.0] - 2026-11-10

#### ➕ Added

##### Controllers

- **`IpscShooterLogController.updateShooterLogs` and `patchShooterLogs`:** New `PUT /ipsc/shooter-logs/bulk` and
  `PATCH /ipsc/shooter-logs/bulk` endpoints, taking `text/plain` CSV with a `ShooterLogId` column and the same
  `StartDate`, `EndDate` and `MatchIds` columns as the bulk create, plus `AddMatchIds` and `RemoveMatchIds` columns
  that the `PATCH` applies. Each row is saved on its own, so a row that fails is reported with the values it supplied
  and the reason while the rest are still saved. They return `200`, or `422` if every row failed.

##### Models

- **`ShooterLogPatchRequest.addMatchIds` and `removeMatchIds`:** Add matches to, and remove them from, a shooter log's
  current matches without restating the rest, applied after any `matchIds`, which still replaces them in full. Nothing
  is looked up by `validate()`, which refuses a null ID or a match that is both added and removed.
- **`ShooterLogBulkRequest` and `ShooterLogBulkRequestCsvMixIn`:** A CSV row of a bulk shooter log update: a
  `ShooterLogRequest` that also carries the `ShooterLogId` of the log to change and the `addMatchIds` and
  `removeMatchIds` a bulk partial update applies.
- **`ShooterLogRow.shooterLogId`, `addMatchIds` and `removeMatchIds`:** The `ShooterLogId` and the matches to add and
  remove that a failed bulk update row supplied; empty for a row that creates a log.
- **`MatchResponse.warnings`:** Lists the problems found with a match when it is read, left out of the JSON when empty.
- **`CompetitorBulkResponse`:** The result of one row of the bulk competitor import: its row number, a message and
  the competitor it created or updated.
- **`CompetitorResponseHolder.results`:** Holds a `CompetitorBulkResponse` for every row of the bulk competitor import,
  in CSV order. `competitors` is unchanged.

##### Services

- **`IpscShooterLogService.updateShooterLogs` and `patchShooterLogs`:** Replace or partially update shooter logs from
  CSV, each row through `updateShooterLog` or `patchShooterLog` in its own transaction. A bulk partial update treats an
  empty date or `MatchIds` column as unchanged, so clearing a log's matches takes a bulk replacement, and adds and
  removes matches through its `AddMatchIds` and `RemoveMatchIds` columns.
- **`IpscShooterLogService.patchShooterLog`:** Now adds the matches in `addMatchIds` and removes those in
  `removeMatchIds` from the log's current matches, ignoring one already covered when adding and one not covered when
  removing, then checks every match the log ends up with against its dates as before. An unknown match to add is a
  `404`, and a match both added and removed is a `400`.
- **`IpscMatchServiceImpl.getMatch` and `getAllMatches`:** A match whose date is outside the dates of a shooter log that
  covers it is now reported in its `warnings` when it is read, rather than only when the log's overall results are
  calculated. The match is still returned, so it stays readable and fixable.
- **`IpscMatchServiceImpl.updateMatch` and `patchMatch`:** An edit that leaves a match on the same day is still not
  blocked by a shooter log it is already out of step with, but now reports the log in the response's `warnings`.
- **`ShooterLogHelpers.describeOutsideDates`:** Builds the warning for a match scheduled outside a covering shooter
  log's dates, from the same `isWithinDates` check the calculation and reschedule paths use, for the match read and
  edit responses to report.
- **`IpscCompetitorServiceImpl.createCompetitors`:** A bulk import row whose club number is an existing competitor's now
  updates and verifies that competitor instead of creating another. The competitor takes the row's data, as in a full
  update, and is marked verified. A row without a club number, as every row for a home club other than HPSC's is,
  always creates a competitor, as neither a name nor an ID number is enough to tell two people apart.

##### Tests

- **`FlywayMysqlMigrationTest`:** Runs every Flyway migration against an empty MySQL database through the application's
  own runtime classpath, and checks that each migration file is applied, none is pending or failed, the application
  tables exist and the checksums validate. It runs only when `FLYWAY_MYSQL_URL` names the database, with
  `FLYWAY_MYSQL_USER` and `FLYWAY_MYSQL_PASSWORD`, and is skipped everywhere else, as the `test` profile bypasses Flyway
  on H2.

##### Tooling

- **`.github/workflows/build.yml`:** A new `migrations` job starts a throwaway `mysql:8.4` service container, the
  version `docker-compose.yml` runs, and runs `FlywayMysqlMigrationTest` against it on every push and pull request to
  `main` and `develop`, so a migration that MySQL rejects shows before deployment.

#### 🔄 Changed

##### Controllers

- **`IpscCompetitorController.createCompetitors` and `IpscMatchController`:** The OpenAPI descriptions now say that a
  bulk competitor row for an existing competitor updates and verifies it, and that a match outside the dates of a
  shooter log that covers it carries a message in its `warnings` when it is read or edited.

##### Models

- **`MatchRequestCsvMixIn`, `CompetitorRequestCsvMixIn` and `MatchCompetitorRequestCsvMixIn`:** The bulk CSV imports no
  longer read a `MatchId`, `CompetitorId` or `MatchCompetitorId` column respectively, as it was bound but never used as
  the row's own ID, since the imports only create records. A file that still has the column imports as before, as
  unknown columns are ignored. The match competitor import still reads `CompetitorId` and `MatchId`, which name the
  competitor and match an entry belongs to, and the JSON request bodies are unchanged.
- **`MatchRequest`, `CompetitorRequest` and `MatchCompetitorRequest`:** Each gains a constructor without its own ID,
  which the CSV mix-ins bind in place of the JSON constructor.

##### Services

- **`IpscCompetitorServiceImpl.normaliseCsvRequest`:** No longer passes on a competitor ID, as a CSV row carries none.

##### Dependencies

- **`pom.xml`:** `flyway-mysql` is no longer pinned to `13.7.0`, so it takes the `12.4.0` Spring Boot manages. The pin
  put Flyway 13's MySQL module on the runtime classpath beside the `12.4.0` `flyway-core` that
  `spring-boot-starter-flyway` brings in, and beside the `12.4.0` Maven plugin, mixing two Flyway majors. All three now
  share one version, and `mysql-connector-j`, `logback.version` and the Jackson BOM properties stay pinned above
  Spring Boot's.

##### Documentation

- **`README.md` and `ARCHITECTURE.md`:** Describe the bulk shooter log replace and update with its add and remove
  columns, the bulk competitor import updating and verifying a competitor by club number, and the match `warnings`.
- **`improvement-plan.md` and `improvement-plan-tasks.md`:** Add Gaps #48, #49 and #50, each closed in this release, to
  the plan and its task list.
- **`ARCHITECTURE.md` and `CONTRIBUTING.md`:** List the migrations gate in the CI/CD & Quality Gates table and the
  gate summary, and the new `migrations/` test directory in the project structure tree.

---

### 🧾 [16.3.0] - 2026-11-10

#### ➕ Added

##### Controllers

- **`IpscShooterLogController.createShooterLogs`:** New `POST /ipsc/shooter-logs/bulk` creates shooter logs from CSV
  data, one log per row with `StartDate`, `EndDate` and `MatchIds` columns, the match IDs separated by semicolons. Each
  row is saved on its own: a failed row is reported with the values it supplied and the reason, and the rest are still
  created. It returns `201` if any row was created and `422` if every row failed.

##### Services

- **`IpscShooterLogService.createShooterLogs`:** Reads the CSV and creates each row through `createShooterLog`, so a row
  gets the same checks as a single log: both dates present, the end not before the start, every match existing and
  every match scheduled within the row's dates.

##### Models

- **`ShooterLogRequestCsvMixIn`:** Binds the CSV column names onto `ShooterLogRequest`. No column is required, so a row
  without a date is reported on its own instead of failing the file.
- **`ShooterLogBulkResponse`, `ShooterLogBulkResponseHolder` and `ShooterLogRow`:** The per-row results of an import,
  with a failed row's values as text.

#### 🔄 Changed

##### Services

- **`IpscCompetitorService` and `IpscShooterLogServiceImpl`:** Remove an unused import each, `CompetitorVerifyResponse`
  and `java.time.LocalDate`, with no change in behaviour.

##### Models

- **`ShooterLogRequest.validate`:** Rejects a null match ID with a `ValidationException`, a `400`, where it previously
  reached the repository and failed with a `500`.

##### Documentation

- **`README.md`, `ARCHITECTURE.md`, `IpscShooterLogService` and `IpscShooterLogController`:** Describe the shooter log
  bulk import and add its flow diagram.
- **`improvement-plan.md` and `improvement-plan-tasks.md`:** Gap #47 (shooter logs could only be created one at a time,
  unlike the other bulk-imported records) is added and closed in v16.3.0.
- **`CHANGELOG.md`:** Link definitions for `[Unreleased]` and every released version, as compare links between
  consecutive tags, so each `[X.Y.Z]` heading resolves as a link instead of an unresolved reference label.
- **`AGENTS.md` and the `prep-version-release` skill:** The Release Checklist and the skill now add each release's link
  definition and move `[Unreleased]` forward, so the headings keep resolving.

---

### 🧾 [16.2.0] - 2026-11-10

#### ➕ Added

##### Controllers

- **`IpscCompetitorController.verifyCompetitors`:** New `POST /ipsc/competitors/verify` marks several competitors as
  verified from a list of their IDs, returning one result per distinct ID. Each ID that matches a competitor is
  verified, and one that matches nobody is reported in its own result instead of failing the others.

##### Services

- **`IpscCompetitorService.verifyCompetitors`:** Verifies every competitor that exists in a single transaction through
  `TransactionService.saveCompetitors`, leaving one that is already verified as it is and reporting it as such.

##### Models

- **`CompetitorVerifyRequest`, `CompetitorVerifyResponse` and `CompetitorVerifyResponseHolder`:** The request carrying
  the competitor IDs, and the per-competitor results of a bulk verification.

##### Repositories

- **`CompetitorRepository.findAllByIdInWithHomeClubAndEmailAddresses`:** Fetch-joins the home club and email addresses
  of several competitors, so their responses can be mapped outside a transaction.

#### 🔄 Changed

##### Services

- **`IpscMatchCompetitorServiceImpl.createMatchCompetitors`:** A row whose competitor is not verified is still
  imported, but its result now carries a message saying the entry is left out of shooter results until the competitor
  is verified. The message of a successfully imported row was always empty before.

##### Documentation

- **`README.md`, `ARCHITECTURE.md`, `IpscCompetitorService`, `IpscCompetitorController`,
  `IpscMatchCompetitorController` and `MatchCompetitorBulkResponse`:** Describe verifying competitors in bulk and the
  message on a match competitor import row whose competitor is not verified.
- **`improvement-plan.md` and `improvement-plan-tasks.md`:** Gap #46 (verifying competitors took one request each, and
  a match competitor import gave no sign that a row is left out of results) is added and closed in v16.2.0.

---

### 🧾 [16.1.2] - 2026-11-09

#### ➕ Added

##### Repositories

- **`ShooterLogRepository.findAllByMatchesId`:** Finds the shooter logs that cover a match, so rescheduling it can be
  checked against their dates.

#### 🔄 Changed

##### Services

- **`IpscShooterLogServiceImpl.validateMatchesWithinDates`:** The `ValidationException` for a match with no scheduled
  date is removed, along with the three tests that only covered it — `ipsc_match.scheduled_date` is `NOT NULL`, so a
  match always has one. The check itself now lives in `ShooterLogHelpers.validateMatchesWithinDates`.

##### Documentation

- **`README.md`, `ARCHITECTURE.md`, `IpscShooterLogOverallService`, `IpscShooterLogOverallController`,
  `IpscMatchService` and `IpscMatchController`:** State that a calculation is refused if a match falls outside the
  log's dates, and that rescheduling a match outside the dates of a shooter log that covers it is refused. The `400`
  responses are added to the calculate, `PUT` and `PATCH` endpoints' OpenAPI documentation.
- **`improvement-plan.md` and `improvement-plan-tasks.md`:** Gap #45 (a shooter log's dates enforced only when the log
  was written, so a rescheduled match slipped outside them) is added and closed in v16.1.2.

#### 🐛 Fixed

##### Services

- **`IpscShooterLogOverallServiceImpl.calculateShooterLogOverall`:** A `ValidationException`, a `400`, is now thrown if
  a match the shooter log covers is scheduled outside its start and end dates, both inclusive, and nothing is
  calculated or changed. The dates were only enforced when a log was written, so a match rescheduled afterwards was
  still counted. A match updated after the overall results were calculated also marks them out of date, as moving a
  match within the log can change which of a competitor's matches is the most recent.
- **`IpscMatchServiceImpl.updateMatch` and `patchMatch`:** A `ValidationException`, a `400`, is now thrown if the new
  date puts the match outside the start and end dates of a shooter log that covers it, naming the log, so a match can
  no longer be moved out of step with its logs. The check only runs when the match moves to another day, so a log
  already out of step does not block unrelated edits.
- **`ShooterLogHelpers`:** New helper holding the date check that `IpscShooterLogServiceImpl` ran when a log was
  written, as `validateMatchesWithinDates` and `isWithinDates`, so the log, the overall calculation and the match
  reschedule check apply the same rule.

---

### 🧾 [16.1.1] - 2026-11-08

#### ➕ Added

##### Services

- **`TransactionService.replaceShooterResults`:** New method that deletes stale shooter results and saves the
  recalculated ones in a single transaction, flushing before it commits so a constraint violation surfaces from the
  call.

#### 🔄 Changed

##### Services

- **`IpscShooterResultServiceImpl.calculateMatchShooterResult`:** Match competitors without points (none, or 0) are
  now excluded from the shooter results — they get no result and no longer count towards the group's winner points or
  anyone's percentage. Previously they were saved with a null rank and percentage.
- **`IpscShooterResultServiceImpl.calculateMatchShooterResult`:** A result whose competitor no longer has points, such
  as after the points were cleared, is deleted on the next calculation. Any such stale result also forces a
  recalculation, so a change made within the recalculation threshold is not missed.
- **`IpscShooterResultServiceImpl.calculateMatchShooterResult`:** Only competitors whose `isVerified` is `true` are
  included in the shooter results — unverified competitors, and those whose verification was never recorded, get no
  result and no longer count towards the group's winner points. A result for a competitor who is no longer verified is
  deleted on the next calculation.
- **`IpscShooterResultServiceImpl.rank`:** The points filter and "not ranked" note are removed, and the winner's
  points no longer fall back to null — both were unreachable once competitors without points are excluded before
  grouping.
- **`IpscShooterLogOverallServiceImpl.calculateShooterLogOverall`:** Only verified competitors are included in the
  overall shooter log results, so an unverified competitor's overall is no longer ranked and is deleted on the next
  calculation.
- **`IpscShooterResultServiceImpl` and `IpscShooterLogOverallServiceImpl`:** The fallback from a null competitor
  category to `NONE`, and the null-safe ordering of a match's scheduled date, are removed — both were unreachable,
  since `competitor_category` and `scheduled_date` are `NOT NULL`. The two tests that only covered the category
  fallbacks are removed with them.
- **`IpscShooterLogOverallServiceImpl.averagePercentage` and `rank`:** Their handling of a missing percentage is kept
  and now documented, along with `IpscShooterLogOverallService`'s Javadoc — `percentage_in_log` is a nullable column,
  so a row inserted by hand or by an earlier version could lack one, and tolerating it keeps one bad row from failing
  a whole log's calculation.

##### Documentation

- **`README.md` and `ARCHITECTURE.md`:** State that shooter results include only verified competitors with points above
  zero, and that shooter log overall results include only verified competitors. They also record that a competitor
  becomes verified by setting `isVerified` when it is created, or through `PUT` or
  `PATCH /ipsc/competitors/{competitorId}` afterwards, since the bulk import only creates competitors. They also state
  that the match competitor import still imports an entry for an unverified competitor, which is only included in
  results once the competitor is verified, and that neither it nor the match import verifies anyone.
- **`IpscShooterResultService`, `IpscShooterLogOverallService` and their controllers:** Javadoc and OpenAPI
  `@Operation` descriptions now state that only verified competitors, and for match results only those with points
  above zero, are included, and that a result is removed when its competitor stops qualifying.
- **`improvement-plan.md` and `improvement-plan-tasks.md`:** Gaps #43 (verified-only results with no documented way
  for an imported competitor to become verified) and #44 (Javadoc and OpenAPI text describing results for every
  competitor) are added and closed in v16.1.1.

---

### 🧾 [16.1.0] - 2026-11-08

#### ➕ Added

##### Controllers

- **`IpscShooterLogController`:** New controller under `/ipsc/shooter-logs`: `POST` creates a shooter log, `PUT` and
  `PATCH /{shooterLogId}` replace or partially update one, `GET` returns every log, `GET /{shooterLogId}` one log
  and `DELETE /{shooterLogId}` removes one.
- **`IpscShooterLogOverallController`:** New controller under `/ipsc/shooter-log-overall-results`:
  `POST /logs/{shooterLogId}` calculates a shooter log's overall results, `GET /logs/{shooterLogId}` returns them
  for one log and `GET` returns them for every log, each a `ShooterLogOverallResponseHolder` linking the log to its
  overall results.

##### Services

- **`IpscShooterLogService`/`IpscShooterLogServiceImpl`:** New service to create, replace, patch, get and delete a
  shooter log, with the matches it covers set by match ID. A request naming a match that does not exist is refused,
  as is one that leaves a match scheduled outside the log's start and end dates (inclusive), and a log that has overall
  results cannot be deleted.
- **`TransactionService.saveShooterLog` and `deleteShooterLog`:** Save a shooter log with its matches loaded, and
  delete one, each in its own transaction.
- **`IpscShooterLogOverallService`/`IpscShooterLogOverallServiceImpl`:** New service that brings each of a log's
  matches' shooter results up to date, then gives each competitor an overall percentage — the average of their
  best `IpscConstants.SHOOTER_LOG_BEST_MATCHES` (four) percentages across the log's matches, or of all of them if
  they have fewer — and ranks them within their division and competitor category, taken from
  their most recent match, with ties sharing a rank. It recalculates only when the overall results are out of date, and
  removes the overall results of competitors who no longer have a result in the log.
- **`TransactionService.replaceShooterLogOverallResults`:** Saves and deletes shooter log overall results in one
  transaction.

##### Repositories

- **`ShooterLogOverallRepository.existsByShooterLogId`:** New finder, so a shooter log with overall results can be
  refused on delete.
- **`ShooterLogRepository`, `ShooterLogOverallRepository` and `ShooterResultRepository`:** New fetch-joining finders
  (`findByIdWithMatches`, `findAllWithMatches`, `findAllByShooterLogIdWithDetails`, `findAllWithDetails` and
  `findAllByMatchIdInWithDetails`) so responses can be mapped outside a transaction.

##### Models

- **`ShooterLogRequest` and `ShooterLogPatchRequest`:** New request DTOs for a shooter log's start and end dates and
  the IDs of the matches it covers. A replacement needs both dates, with the end date not before the start date.
- **`ShooterLogOverallResponseHolder`, `ShooterLogOverallResponse` and `ShooterLogResponse`:** New response DTOs
  linking a shooter log to its overall results, each with the competitor's names and number, category, division, rank
  and percentage in the log and when they were calculated.

##### Constants

- **`IpscConstants.SHOOTER_LOG_BEST_MATCHES`:** New constant (`4`) — the number of a competitor's best matches in a
  shooter log that count towards their overall percentage.

#### 🔄 Changed

##### Dependencies

- **`jacoco.version`:** Bumped from 0.8.15 to 0.8.16
- **`springdoc-openapi-bom`:** Bumped from 3.1.1 to 3.2.0

##### Documentation

- **`ARCHITECTURE.md`:** The Domain Entities table no longer lists `ShooterLogCompetitor`, and its columns are
  re-padded. The controller, service and Feature Support tables and the project tree gain the shooter log overall
  controller, service and response DTOs.
- **`README.md`/`CONTRIBUTING.md`:** No longer say the shooter log service and controller layer is still being built.
- **`flyway-migration-versioning.md`:** Current State table gains a row for `V11_12_0__drop_shooter_log_competitor.sql`.
- **`improvement-plan.md`/`improvement-plan-tasks.md`:** Gaps #41, #6 and #42 move to Completed.

#### 🗑️ Removed

##### Database

- **`V11_12_0__drop_shooter_log_competitor.sql`:** New migration that drops the `shooter_log_competitor` table, which
  nothing references any more — a competitor's rank and percentage in a log live in `shooter_result`. Any rows in the
  table are discarded.

##### Domain

- **`ShooterLogCompetitor`:** Removed the entity mapping `shooter_log_competitor`, together with its
  **`ShooterLogCompetitorRepository`**, its repository integration test and the `ScoringFixtures.shooterLogCompetitor`
  fixture.

##### Services

- **`IpscCompetitorServiceImpl.deleteCompetitor`/`IpscMatchServiceImpl.deleteMatch`:** No longer check
  `ShooterLogCompetitorRepository` before deleting; the competitor check still covers `ShooterLogOverall` rows and the
  match check still covers `ShooterLog` links.

##### Constants

- **`IpscConstants.MATCH_POINTS_SCALE`, `HIT_FACTOR_SCALE` and `TIME_SCALE`:** Removed — nothing referenced them; only
  `PERCENTAGE_SCALE` has a caller.
- **`SystemConstants.DEFAULT_SCALE`:** Removed — only `NumberUtilTest` read it.
- **`IpscConstants.REPLACE_IN_NAMES_REGEX`:** Removed — its pattern is inlined into
  `CompetitorHelpers.RANGE_OFFICER_MARKER`, its only caller, whose Javadoc now also covers the `TRO`/`(TRO)` training
  range officer marker the pattern already matched. The Javadoc of `IpscEntityCompetitorService` and its
  implementation no longer links to the constant.

##### Utilities

- **`NumberUtil.calculateSum`:** Removed with its unit tests — only those tests called it. `NumberUtilTest`'s
  `calculatePercentage` tests now use a local scale.

---

### 🧾 [16.0.0] - 2026-10-31

#### ➕ Added

##### Database

- **`V11_11_0__rename_semi_auto_rifle_division_names.sql`:** New migration that renames the stored `division` "Rifle
  Open" and "Rifle Standard" in `match_competitor`, `shooter_log_competitor` and `shooter_log_overall` to "Rifle Semi
  Auto Open" and "Rifle Semi Auto Standard"; without it an existing row's division reads back as `null`

##### Domain

- **`Division.RIFLE_PCC`:** New "Rifle PCC" rifle division

##### Tests

- **`CompetitorHelpersTest`:** New `cleanCompetitorName` tests for removing a trailing suffix of one, two, three
  or more digits, alone, after a position prefix and alongside a range officer marker, and for keeping digits that
  are not at the end of the name
- **`IpscShooterLogOverallServiceTest`:** New `calculateShooterLogOverall` tests for recalculating when an overall or
  a shooter result was never calculated, when the shooter log was updated after the overall, for comparing against the
  oldest of several overalls, for not recalculating when the log was updated before the overall, and for falling back
  to the `NONE` category when the latest result has none
- **`IpscShooterLogServiceTest`:** New `patchShooterLog` tests for patching a log that has no start date or no end
  date
- **`IpscShooterResultServiceTest`:** New `calculateMatchShooterResult` tests for a competitor without a category, an
  existing result that was never calculated, a match without competitors and results calculated at different times
- **`MatchCompetitorMapperTest`:** New `populateResolvableFields` tests for resolving the competitor by ID, by number
  only, by name only, when none is identified, not found or ambiguous, and for resolving the match by ID

#### 🔄 Changed

##### Utilities

- **`CompetitorHelpers`:** Competitor names now have trailing modified-entry digits stripped, via
  `MODIFIED_SUFFIX`, when they are normalised
- **`IpscConstants.REPLACE_IN_NAMES_REGEX`:** Now also removes a "TRO" (or "(TRO)") role marker from competitor
  names before they are matched by full name

##### Domain

- **`Division`:** **Breaking:** `RIFLE_SEMI_AUTO_OPEN` and `RIFLE_SEMI_AUTO_STANDARD` display names are now "Rifle Semi
  Auto Open" and "Rifle Semi Auto Standard" (were "Rifle Open" and "Rifle Standard"), so a division given by the old
  names no longer resolves

##### Dependencies

- **`spring-boot-starter-parent`:** Bumped from 4.1.1 to 4.1.2, with `asm.version` from 9.11 to 9.11.1 and
  `mysql-connector-j` from 26.7.0 to 26.10.0
- **`jackson-2-bom.version`, `jackson-bom.version`:** Bumped from 2.22.3 to 2.22.4 and from 3.2.3 to 3.2.4

##### Services

- **`IpscMatchCompetitorServiceImpl.createMatchCompetitors`:** Now `@Transactional`, so a bulk import is saved or
  rolled back as one unit

##### Documentation

- **`documentation/recommendations/flyway-migration-versioning.md`:** Current State table gains a row for
  `V11_11_0__rename_semi_auto_rifle_division_names.sql`
- **`documentation/roadmap/improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #41 (the score-scale
  constants and `NumberUtil.calculateSum` reinstated in 15.0.0 with no production caller), added under ⚪ Open and
  to the Roadmap's Next row
- **`documentation/roadmap/improvement-plan.md`, `improvement-plan-tasks.md`:** Gap #26 (the `tomcat.version`
  override) moves to ✅ Completed, since Spring Boot 4.1.2 manages Tomcat `11.0.27`

#### 🗑️ Removed

##### Dependencies

- **`tomcat.version`:** Removed the `pom.xml` override of Tomcat `11.0.26`, because Spring Boot 4.1.2 manages `11.0.27`
- **`asm.version`:** Removed the `org.ow2.asm:asm` pin and its `dependencyManagement` entry, since `asm` is no longer on
  the classpath once `json-smart` is excluded

#### 🔐 Security

##### Dependencies

- **`spring-boot-starter-webmvc-test`:** Excludes `net.minidev:json-smart` and `com.jayway.jsonpath:json-path`, since
  `json-smart` 2.6.0, the latest release, is vulnerable to CVE-2026-113602 and nothing in the tests uses JsonPath
- **`spring-boot-starter-webmvc-test`:** Also excludes `org.skyscreamer:jsonassert`, whose `android-json`
  0.0.20131108.vaadin1 is vulnerable to CVE-2026-110974 with no fixed release, and nothing in the tests uses JSONAssert

---

### 🧾 [15.0.0] - 2026-10-17

#### ➕ Added

##### Database

- **`V11_10_0__create_shooter_result.sql`:** New migration creating `shooter_result`, linked to `ipsc_match`,
  `competitor` and `match_competitor`, unique per match and match competitor

##### Domain

- **`ShooterResult`:** New entity mirroring `ShooterLogCompetitor`, with a `match` in place of the `shooterLog`

##### Repositories

- **`ShooterResultRepository`:** New repository with `findAllByMatchIdWithDetails` and `findAllWithDetails`, which
  fetch-join the match, competitor and match competitor of one match's results, or of every result
- **`MatchCompetitorRepository.findAllByMatchIdWithCompetitorAndMatch`:** New query that fetch-joins the competitor
  and match for one match's entries

##### Models

- **`ShooterResultResponse`:** New response describing a shooter result — its ID, the match, competitor and match
  competitor IDs, the competitor's names and number, their category and division, and their rank and percentage in
  the log, the percentage as a `String` formatted to `IpscConstants.PERCENTAGE_SCALE` for display, with the date they
  were calculated
- **`ShooterMatchResultResponse`:** New response describing the match a set of shooter results were shot in — its ID,
  name and date
- **`ShooterResponseHolder`:** New container linking one `ShooterMatchResultResponse` with the
  `ShooterResultResponse`s of the competitors who shot that match

##### Services

- **`IpscShooterResultService`:** New service with `calculateMatchShooterResult`, which creates or updates a
  `ShooterResult` for each of a match's competitors — ranking them within their firearm type, division and competitor
  category (those with none a group of their own) by match points, highest first, with ties sharing a rank, and
  setting each one's percentage to their points as a percentage of the group winner's, and the date calculated — then
  returns the match with its results. It only recalculates when the results are out of date, that is when a competitor
  has no result yet or the match or a competitor was updated at least five minutes after the oldest result was
  calculated, and otherwise returns the current results as they are. The service also has `getShooterResults`, which
  returns a match linked to its results, and `getAllShooterResults`, which returns one such holder per match, oldest
  match date first (then by ID), with no results for a match nobody has a result in; `IpscShooterResultServiceImpl`
  throws `NonFatalException` for a match that does not exist
- **`TransactionService.saveShooterResults`:** New method that saves a batch of shooter results in one transaction,
  flushing so a unique constraint violation surfaces as a `DataIntegrityViolationException`

##### Controllers

- **`IpscShooterResultController`:** New endpoint `POST /ipsc/shooter-results/matches/{matchId}`, which calculates
  a match's shooter results and returns the match linked to them, and read-only endpoints
  `GET /ipsc/shooter-results/matches/{matchId}` and `GET /ipsc/shooter-results`, returning a match linked to its
  shooter results, or one such holder per match, including matches without results; an unknown match ID returns a
  404

##### Utilities

- **`NumberUtil`:** Reinstated, with its tests, after its removal in 14.0.0 — `calculatePercentage`, `calculateSum`
  and `formatBigDecimal` each take the `scale` to round to (`RoundingMode.HALF_UP`), with a null or zero whole giving
  a zero percentage and a null value or list giving zero

##### Constants

- **`IpscConstants`:** New `MATCH_POINTS_SCALE`, `HIT_FACTOR_SCALE`, `TIME_SCALE` and `PERCENTAGE_SCALE` decimal-place
  constants for scores; only `PERCENTAGE_SCALE` is referenced by code so far, by the shooter result calculation and
  `ShooterResultResponse`
- **`SystemConstants.DEFAULT_SCALE`:** Reinstated, with a value of 5, as the default scale for `NumberUtil`
  calculations

#### 🔄 Changed

##### Database

- **`V11_9_0__rename_shooter_log_points_to_percentage_in_log.sql`:** New migration that renames `points` to
  `percentage_in_log` in `shooter_log_competitor` and `shooter_log_overall`

##### Domain

- **`ShooterLogCompetitor`, `ShooterLogOverall`:** **Breaking:** renamed `points` to `percentageInLog`, and its
  column to `percentage_in_log`

##### Mappers

- **`MatchCompetitorMapper`:** No longer copies `overallRanking` or `clubRanking` from a request onto the entity,
  on create or patch, since the entity no longer has them

##### Controllers

- **`IpscMatchCompetitorController`:** Updated the bulk CSV example to drop the `OverallRanking` and `ClubRanking`
  columns

##### Documentation

- **Archived versions 10.0.0 – 13.3.0:** Moved the `CHANGELOG`, `HISTORY` and `EVOLUTION_OVERVIEW` entries of those
  versions into `CHANGELOG_v10-v13.md`, `HISTORY_v10-v13.md` and `EVOLUTION_OVERVIEW_v10-v13.md`, and the v10 – v13
  release notes and pull request descriptions, from `documentation/history/` to `documentation/archive/v10-v13/`; the
  links in `README.md`, `AGENTS.md`, `CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md` and the
  `prep-version-release` skill follow the new locations, and the thirteen v8.5.1 – v8.12.0 pull request descriptions
  now link to their entries in `CHANGELOG_v8-v9.md` rather than to `CHANGELOG.md` anchors that no longer exist

#### 🗑️ Removed

##### Database

- **`V11_8_0__remove_match_competitor_rankings.sql`:** New migration that drops the `overall_ranking` and
  `club_ranking` columns from `match_competitor`

##### Domain

- **`MatchCompetitor`:** **Breaking:** removed the `overallRanking` and `clubRanking` fields and their columns

##### Models

- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`:** **Breaking:** removed the optional `overallRanking` and
  `clubRanking` from the create, replace and patch request bodies and from the constructor, so rankings can no longer
  be set through the API
- **`MatchCompetitorRequestCsvMixIn`:** **Breaking:** removed the optional `OverallRanking` and `ClubRanking` columns
  from the bulk match competitor CSV import
- **`MatchCompetitorResponse`:** **Breaking:** removed `overallRanking` and `clubRanking` from the response body
- **`MatchCompetitorRow`:** Removed `overallRanking` and `clubRanking` from the all-text import row, so a failed bulk
  row no longer reports them

#### 🔐 Security

##### Dependencies

- **`org.ow2.asm:asm`:** Pinned to 9.11 through `dependencyManagement` and a new `asm.version` property, replacing
  the vulnerable 9.7.1 that `json-smart`'s `accessors-smart` brings in transitively through
  `spring-boot-starter-test`

---

### 🧾 [14.0.0] - 2026-10-09

#### ➕ Added

##### Database

- **`V11_7_0__drop_division_suffix_from_division_names.sql`:** New migration that renames the stored `division` in
  `match_competitor`, `shooter_log_competitor` and `shooter_log_overall` to the new `Division` names, dropping the
  trailing " Division" and mapping `PCC Optic Division` and `PCC Iron Division` to `PCC Optics` and `PCC Irons`; without
  it an existing row's division reads back as `null`

##### Models

- **`MatchCompetitorRow`:** New all-text description of a bulk import row, with every value the row supplied and a
  missing value as an empty string; `MatchCompetitorBulkResponse` carries it as `row` for a row that failed

##### Helpers

- **`MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields`:** New helper that describes each required field
  still unset on a match competitor, saying whether the request left it out or gave a value that could not be
  resolved; a blank or generic competitor number is treated as not specified

##### Services

- **`IpscEntityClubService.findByCodeOrAbbreviation`:** New lookup of a club by abbreviation, name, or club identifier
  code, abbreviation or name; `IpscEntityClubServiceImpl` now takes a `ClubRepository`. An unknown club throws
  `ValidationException`, and a blank code throws `NonFatalException`
- **`IpscEntityClubService.findByCodeOrAbbreviationWithDefault`:** New lookup that uses a default club identifier when
  the club name is blank

#### 🔄 Changed

##### Enums

- **`CompetitorCategory.fromName`:** Now returns an empty `Optional` for an unrecognised name instead of falling back
  to `NONE`; a null or blank name resolves to `NONE`
- **`Division`:** **Breaking:** dropped the trailing " Division" from each division's name (for example "Open" instead of
  "Open Division"), so divisions are matched by their short names, and a value in the old form no longer resolves
- **`Division`:** **Breaking:** `PCC_OPTICS` and `PCC_IRON` are renamed `"PCC Optics"` and `"PCC Irons"` (from
  `"PCC Optic"` and `"PCC Iron"`), so a division given by the singular name no longer resolves

##### Helpers

- **`CompetitorHelpers.cleanCompetitorName`:** Now removes every leading position prefix, not only the first
- **`MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields`:** The "Competitor not found for name"
  message now quotes the competitor name cleaned by `CompetitorHelpers.cleanCompetitorName`

##### Mappers

- **`CompetitorMapper.resolveHomeClub`:** **Breaking:** now resolves a competitor's home club through
  `IpscEntityClubService.findByCodeOrAbbreviationWithDefault`, so it can be given by club identifier code,
  abbreviation or name as well as abbreviation or name; an unknown home club now throws `ValidationException` (400)
  instead of `NonFatalException` (404). A blank home club is still left unset. `CompetitorMapper` no longer takes a
  `ClubRepository`
- **`MatchMapper.resolveClub`:** **Breaking:** now resolves a club through
  `IpscEntityClubService.findByCodeOrAbbreviationWithDefault`, so a match's club can be given by abbreviation, name,
  or club identifier code or abbreviation; an unknown club now throws `ValidationException` (400) instead of
  `NonFatalException` (404). `MatchMapper` no longer takes a `ClubRepository`, and `FatalException` is dropped from
  `resolveClub`, `applyFields`, `applyPatchFields` and the match service and controller methods that only declared it
  for the club lookup
- **`MatchCompetitorMapper.resolveCompetitorCategory`:** A null or blank competitor category now resolves to
  `CompetitorCategory.NONE` instead of throwing; an unrecognised one still throws `ValidationException`. Covered
  by new `MatchCompetitorMapperTest` cases for null, empty and whitespace-only values
- **`MatchCompetitorMapper.populateResolvableFields`:** A blank competitor category is now kept as
  `CompetitorCategory.NONE` instead of being cleared to `null`, so a bulk import row no longer reports it as missing; an
  unrecognised one is still left `null`
- **`MatchCompetitorMapper.resolveFirearmType`:** Now takes the competitor's `Division` and falls back to the
  division's firearm type when the firearm type name is unknown, instead of throwing
- **`MatchCompetitorMapper.resolveMatchClub`:** Now matches a club by name, abbreviation or identifier code, in that
  order
- **`MatchCompetitorRowMapper`:** New mapper that describes a failed bulk import row as a `MatchCompetitorRow`,
  reporting the values already resolved onto the `MatchCompetitor` — the competitor and match identifiers, the
  competitor's number, and the match club, category, firearm type, division and power factor — in place of the row's
  own text; `failedRow` now takes the `MatchCompetitor` and uses it, so a row that fails as a duplicate or for a
  missing field is reported by its resolved values. A reported match club is now the club's name, and the competitor's
  name is always the row's own name cleaned by `CompetitorHelpers.cleanCompetitorName`, whether or not a competitor
  was resolved

##### Services

- **`IpscMatchCompetitorServiceImpl.createMatchCompetitors`:** A row is now checked for every missing or
  unresolvable required field at once, through `MatchCompetitorMapper.populateResolvableFields`, instead of failing on
  the first one thrown, so its message lists them all; a blank category, division, firearm type or power factor is
  reported as not specified
- **`ClubService` and `EntityIpscCompetitorService`:** Renamed to `IpscEntityClubService` and
  `IpscEntityCompetitorService` (and their implementations and tests) so the IPSC entity services share a naming
  pattern; `findCompetitor` is now `findCompetitorByIdentifierAndFullName`, and dependants rename their fields to match
- **`IpscMatchCompetitorServiceImpl.toResponse`:** Now validates the match competitor first and throws
  `ValidationException` naming every violated constraint, instead of building a response from an incomplete entity;
  the service now takes a `jakarta.validation.Validator`
- **`IpscMatchCompetitorServiceImpl.failedRow`:** **Breaking:** a row that could not be imported is now reported with
  no `MatchCompetitorResponse` (`null`) and only its values in `matchCompetitorRow`, instead of a partly filled
  response, so a client reading `matchCompetitor` for a failed row now gets `null`; the `toFailedResponse` helper is
  removed. `MatchCompetitorBulkResponse.matchCompetitor` is now `@Nullable`

##### Models

- **`MatchCompetitor`:** The competitor, match, competitor category, firearm type, division and power factor are now
  `@NotNull`, matching their `nullable = false` columns
- **`MatchCompetitorRequest`:** The match ID, competitor category, division and power factor are no
  longer rejected by bean validation, and the competitor category is optional in the CSV import (`Cats`), so a bulk
  import can report a row that is missing them instead of failing the whole request; `validate()` still requires
  them when a match competitor is created or replaced
- **`MatchCompetitorResponse.competitorId`:** Dropped `@NonNull`, so a response can be built for a match competitor
  that is not linked to a competitor
- **`MatchCompetitorBulkResponseHolder.matchCompetitors`:** **Breaking:** renamed back to `matchCompetitorResults`, so
  a bulk match competitor import's response body carries `matchCompetitorResults` instead of `matchCompetitors`;
  `IpscMatchCompetitorController` reads the renamed getter
- **`CompetitorResponse`, `CompetitorResponseHolder`, `MatchResponse`, `MatchResponseHolder` and
  `MatchCompetitorBulkResponseHolder`:** Replaced `org.jspecify.annotations.NonNull` with
  `jakarta.validation.constraints.NotNull` on their required fields, so they can be checked by a `Validator`

##### Documentation

- **`documentation/roadmap/improvement-plan.md`, `improvement-plan-tasks.md`:** Gaps #38 (test-only utility and enum
  methods), #39 (`ClubIdentifier.code` and `fromCode`) and #40 (no data migration for the renamed `Division` names) are
  recorded and closed in v14.0.0, with the Roadmap, At a Glance and Success Criteria entries updated to match
- **`documentation/recommendations/flyway-migration-versioning.md`:** Current State table gains a row for
  `V11_7_0__drop_division_suffix_from_division_names.sql`
- **`ARCHITECTURE.md`:** Describes the renamed `IpscEntityClubService` and `IpscEntityCompetitorService`, the new
  `MatchCompetitorHelpers` and `MatchCompetitorRowMapper`, and a bulk import row that fails for missing or
  unresolvable fields; drops `DateUtil` and `NumberUtil` and `MatchCompetitorResponseHolder`
- **`AGENTS.md`:** The final-utility-class example now names `ValueUtil` instead of the removed `NumberUtil`, and the
  test-heading example uses `// fromName()` instead of `// fromCode()`

#### 🗑️ Removed

##### Utilities

- **`NumberUtil` and `DateUtil`:** Removed both classes and their tests — `calculatePercentage`, `calculateSum`,
  `formatBigDecimal`, `formatDate` and `formatDateTime` were called only by tests
- **`StringUtil.formatStringWithNamedParameters` and `ValueUtil.nullAsZeroBigDecimal`:** Removed with their tests, as
  nothing in production code called them

##### Constants

- **`SystemConstants.DEFAULT_SCALE`:** Removed, as nothing read it once `NumberUtil` took the scale as a parameter

##### Models

- **`MatchCompetitorResponseHolder`:** Removed the unused container class; bulk imports return
  `MatchCompetitorBulkResponseHolder`

##### Tests

- **`AwardControllerTest`, `ImageControllerTest`, `IpscCompetitorControllerTest`, `IpscMatchControllerTest` and
  `IpscMatchCompetitorControllerTest`:** Removed the tests that only checked the controller delegates to its service
  or lets the service's exception propagate

[16.4.1]: https://github.com/tahoni/hpsc-web-springboot/compare/v16.4.0...v16.4.1
[16.4.0]: https://github.com/tahoni/hpsc-web-springboot/compare/v16.3.0...v16.4.0
[16.3.0]: https://github.com/tahoni/hpsc-web-springboot/compare/v16.2.0...v16.3.0
[16.2.0]: https://github.com/tahoni/hpsc-web-springboot/compare/v16.1.2...v16.2.0
[16.1.2]: https://github.com/tahoni/hpsc-web-springboot/compare/v16.1.1...v16.1.2
[16.1.1]: https://github.com/tahoni/hpsc-web-springboot/compare/v16.1.0...v16.1.1
[16.1.0]: https://github.com/tahoni/hpsc-web-springboot/compare/v16.0.0...v16.1.0
[16.0.0]: https://github.com/tahoni/hpsc-web-springboot/compare/v15.0.0...v16.0.0
[15.0.0]: https://github.com/tahoni/hpsc-web-springboot/compare/v14.0.0...v15.0.0
[14.0.0]: https://github.com/tahoni/hpsc-web-springboot/compare/v13.3.0...v14.0.0
