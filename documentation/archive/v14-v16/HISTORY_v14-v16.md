# HPSC Website Backend History Archive (v14.0.0 – v16.4.1)

The per-version narrative history of versions 14.0.0 to 16.4.1 of the HPSC Website Backend project, archived from
[`HISTORY.md`](/HISTORY.md) to keep that file a manageable size. The entries are moved unchanged, grouped under
the `HISTORY.md` section each came from. See [`HISTORY.md`](/HISTORY.md) for the sections that span every
version (Feature Timeline, Key Learnings, Project Philosophy Evolution and Conclusion) and for versions 17.0.0 onwards,
[`CHANGELOG_v14-v16.md`](/documentation/archive/v14-v16/CHANGELOG_v14-v16.md) for the archived change log,
[`EVOLUTION_OVERVIEW_v14-v16.md`](/documentation/archive/v14-v16/EVOLUTION_OVERVIEW_v14-v16.md) for the archived
Phase-by-phase narrative, [`HISTORY_v10-v13.md`](/documentation/archive/v10-v13/HISTORY_v10-v13.md) for versions 10.0.0
to 13.3.0, [`HISTORY_v8-v9.md`](/documentation/archive/v8-v9/HISTORY_v8-v9.md) for versions 8.0.0 to 9.1.0,
[`HISTORY_v5-v7.md`](/documentation/archive/v5-v7/HISTORY_v5-v7.md) for versions 5.0.0 to 7.4.1 and
[`HISTORY_v1-v4.md`](/documentation/archive/v1-v4/HISTORY_v1-v4.md) for versions 1.0.0 to 4.1.0.

---

## Table of Contents

- [📅 Historical Timeline](#-historical-timeline)
- [🎯 Major Milestones](#-major-milestones)
- [🛤️ Future Roadmap Implications](#-future-roadmap-implications)

---

## 📅 Historical Timeline

### Version 16.4.1 (November 10, 2026)

**Theme:** Making an Absent Home Club Explicit

**Key Focus:**

- `IpscMatchCompetitorServiceImpl.resolveCompetitorHomeClub` returns an `Optional<ClubIdentifier>`, empty when the
  competitor has no home club or the home club has no identifier, instead of `null`
- `isForClub` passes the value on through the null-safe `isSameClub`, so which rows the import counts as the club's
  does not change
- Scoped as `v16.4.1` **PATCH**: an internal refactor of a protected method that changes no endpoint, request,
  response or configuration
- Improvement plan: no gap is added, closed or progressed, and all 50 gaps stay closed with none open
- The suite stands at 1,569 tests, three of which are skipped unless `FLYWAY_MYSQL_URL` is set
- Project version bumped to 16.4.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 16.4.0 (November 10, 2026)

**Theme:** Changing Existing Records in Bulk

**Key Focus:**

- `PUT` and `PATCH /ipsc/shooter-logs/bulk` (`IpscShooterLogController.updateShooterLogs` and `patchShooterLogs`)
  replace and partially update existing shooter logs from `text/plain` CSV, with a `ShooterLogId` column beside the
  `StartDate`, `EndDate` and `MatchIds` columns of the bulk create
- Each row goes through `updateShooterLog` or `patchShooterLog` in its own transaction, so a failed row, such as one
  with no `ShooterLogId` or an unknown log, is reported with the values it supplied and the rest are still saved; the
  endpoints answer `200`, or `422` if every row failed
- `ShooterLogPatchRequest.addMatchIds` and `removeMatchIds`, and the `AddMatchIds` and `RemoveMatchIds` CSV columns,
  let a `PATCH` add matches to and remove them from a log without restating the rest, while `matchIds` still replaces
  the list and a match both added and removed is a `400`
- A bulk competitor import row whose club number is an existing competitor's updates and verifies that competitor
  instead of creating another, and `CompetitorBulkResponse` in `CompetitorResponseHolder.results` reports each row
- `MatchResponse.warnings` reports a match outside the dates of a shooter log that covers it when the match is read,
  and when it is edited without changing its day, built by `ShooterLogHelpers.describeOutsideDates`; the match is
  still returned and a move to another day outside the dates is still refused
- The match, competitor and match competitor CSV imports stop reading their own ID column, which was bound but never
  used, and a file that still has it imports as before
- `flyway-mysql` is no longer pinned to `13.7.0`, so it takes the `12.4.0` Spring Boot manages and matches
  `flyway-core` and the Maven plugin; all 38 migrations were applied to a throwaway MySQL 8.4 on it
- A `migrations` job in `build.yml` runs `FlywayMysqlMigrationTest` against a throwaway `mysql:8.4` service container
  on every push and pull request, so a migration that MySQL rejects shows before deployment
- `README.md`, `ARCHITECTURE.md` and the OpenAPI descriptions describe the bulk updates, the competitor update and the
  match warnings
- Scoped as `v16.4.0` **MINOR**: the new endpoints and the optional `warnings`, `results` and request fields are
  backward-compatible additions, and nothing existing breaks
- Improvement plan: Gaps #48, #49 and #50 are added and closed, leaving all 50 gaps closed and none open
- The suite stands at 1,569 tests, three of which are skipped unless `FLYWAY_MYSQL_URL` is set
- Project version bumped to 16.4.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 16.3.0 (November 10, 2026)

**Theme:** Shooter Log Bulk CSV Import

**Key Focus:**

- `POST /ipsc/shooter-logs/bulk` (`IpscShooterLogController.createShooterLogs`) creates shooter logs from `text/plain`
  CSV data, one log per row with `StartDate`, `EndDate` and `MatchIds` columns, the match IDs separated by semicolons
- `IpscShooterLogService.createShooterLogs` creates each row through `createShooterLog`, in its own transaction, so a
  row gets the same checks as a single log: both dates present, the end not before the start, every match existing and
  every match scheduled within the row's dates
- A failed row is reported with the values it supplied and the reason, and the rest are still created; the endpoint
  answers `201` if any row was created and `422` if every row failed
- `ShooterLogRequestCsvMixIn` binds the CSV columns onto `ShooterLogRequest` with no column required, so a row without a
  date is reported on its own instead of failing the file; `ShooterLogBulkResponse`, `ShooterLogBulkResponseHolder` and
  `ShooterLogRow` carry the per-row results
- `ShooterLogRequest.validate` rejects a null match ID with a `ValidationException`, a `400`, where it previously
  reached the repository and failed with a `500`
- `README.md`, `ARCHITECTURE.md`, the service Javadoc and the OpenAPI descriptions describe the import, and
  `ARCHITECTURE.md` gains a Shooter Log Bulk CSV Import Flow diagram
- Scoped as `v16.3.0` **MINOR**: the new endpoint is a backward-compatible addition, and the stricter match ID check
  only turns a `500` into a `400`
- Improvement plan: Gap #47 is added and closed, leaving all 47 gaps closed and none open
- The suite stands at 1,524 passing tests
- Project version bumped to 16.3.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 16.2.0 (November 10, 2026)

**Theme:** Verifying Competitors in Bulk

**Key Focus:**

- `POST /ipsc/competitors/verify` (`IpscCompetitorController.verifyCompetitors`) marks several competitors as verified
  from a list of their IDs and returns one `CompetitorVerifyResponse` per distinct ID; an ID that matches nobody is
  reported in its own result instead of failing the others
- `IpscCompetitorService.verifyCompetitors` verifies every competitor that exists in a single transaction through
  `TransactionService.saveCompetitors`, leaving one that is already verified as it is and reporting it as such
- `CompetitorVerifyRequest`, `CompetitorVerifyResponse` and `CompetitorVerifyResponseHolder` carry the request and the
  per-competitor results, and `CompetitorRepository.findAllByIdInWithHomeClubAndEmailAddresses` fetch-joins the
  competitors' home clubs and email addresses so their responses map outside a transaction
- A match competitor import row whose competitor is not verified is still imported, but its result now carries a message
  saying the entry is left out of shooter results until the competitor is verified; the message of an imported row was
  always empty before
- `README.md`, `ARCHITECTURE.md`, the service Javadoc and the controllers' OpenAPI descriptions describe both
- Scoped as `v16.2.0` **MINOR**: the new endpoint is a backward-compatible addition, and the message only fills a
  response field that already existed
- Improvement plan: Gap #46 is added and closed, leaving all 46 gaps closed and none open
- The suite stands at 1,498 passing tests
- Project version bumped to 16.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 16.1.2 (November 9, 2026)

**Theme:** Shooter Log Dates Enforced After the Log Is Written

**Key Focus:**

- `IpscShooterLogOverallServiceImpl.calculateShooterLogOverall` refuses, with a `400`, a shooter log that covers a match
  scheduled outside the log's start and end dates, and calculates and changes nothing
- `IpscMatchServiceImpl.updateMatch` and `patchMatch` refuse, with a `400` naming the log, moving a match to another day
  outside the dates of a shooter log that covers it; a match that stays on the same day is not checked, so a log
  already out of step does not block unrelated edits
- A match updated after a log's overall results were calculated marks them out of date, since moving a match within the
  log can change which of a competitor's matches is the most recent
- `ShooterLogHelpers` holds the date check once, as `validateMatchesWithinDates` and `isWithinDates`, shared by
  `IpscShooterLogServiceImpl`, the overall calculation and the reschedule check;
  `ShooterLogRepository.findAllByMatchesId` finds the logs that cover a match
- The unreachable missing-scheduled-date check is removed, since `ipsc_match.scheduled_date` is `NOT NULL`
- `README.md`, `ARCHITECTURE.md`, the service Javadoc and the OpenAPI descriptions state the rule and its `400`
  responses
- Scoped as `v16.1.2` **PATCH**: it fixes a gap in a rule the API already enforced on writing a log, adding no endpoint,
  request or response field, and the one new repository method is internal
- Improvement plan: Gap #45 is added and closed, leaving all 45 gaps closed and none open
- The suite stands at 1,476 passing tests
- Project version bumped to 16.1.2 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 16.1.1 (November 8, 2026)

**Theme:** Verified-Only Shooter Results

**Key Focus:**

- `IpscShooterResultServiceImpl.calculateMatchShooterResult` includes only competitors whose `isVerified` is `true` and
  whose points are above zero, so a competitor without points, or one who is not verified, gets no result and no longer
  counts towards a group winner's points or anyone's percentage
- A result whose competitor stops qualifying, such as after their points were cleared, is deleted on the next
  calculation, and any such stale result forces a recalculation
- `IpscShooterLogOverallServiceImpl.calculateShooterLogOverall` includes only verified competitors, deleting the overall
  of one who is not
- `TransactionService.replaceShooterResults` deletes stale results and saves the recalculated ones in one transaction
- `README.md`, `ARCHITECTURE.md`, the service Javadoc and the controllers' OpenAPI descriptions state which competitors
  are included, and that only the competitor endpoints can verify a competitor: the match competitor import still
  imports an entry for an unverified competitor, which only appears in results once its competitor is verified
- The unreachable points filter in `IpscShooterResultServiceImpl.rank` and the null fallback on the winner's points are
  removed, as are the null-category fallback to `NONE` in both services and the null-safe ordering of a match's
  scheduled date, whose columns are `NOT NULL`
- The tolerance for a shooter result without a percentage in `IpscShooterLogOverallServiceImpl` is kept and documented
- Scoped as `v16.1.1` **PATCH**: no endpoint, request or response contract changes, and the one new method is internal
- Improvement plan: Gaps #43 and #44 are added and closed, leaving all 44 gaps closed and none open
- The suite stands at 1,447 passing tests
- Project version bumped to 16.1.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 16.1.0 (November 8, 2026)

**Theme:** Shooter Log Management & Overall Results

**Key Focus:**

- `IpscShooterLogController` creates, replaces, partially updates, reads and deletes shooter logs under
  `/ipsc/shooter-logs`, through `IpscShooterLogService`, with the matches a log covers set by match ID
- A match must be scheduled within its shooter log's start and end dates, both inclusive, and a log with overall
  results cannot be deleted
- `IpscShooterLogOverallController` calculates and reads a shooter log's overall results under
  `/ipsc/shooter-log-overall-results`, through `IpscShooterLogOverallService`: each competitor's overall percentage is
  the average of their best `IpscConstants.SHOOTER_LOG_BEST_MATCHES` (four) percentages, ranked within their division
  and competitor category, with ties sharing a rank
- `ShooterLogCompetitor`, its repository and the `shooter_log_competitor` table are removed with
  `V11_12_0__drop_shooter_log_competitor.sql`, since a competitor's rank and percentage in a log live in
  `shooter_result`
- The unused score-scale constants, `SystemConstants.DEFAULT_SCALE` and `NumberUtil.calculateSum` are removed, and
  `IpscConstants.REPLACE_IN_NAMES_REGEX` is inlined into `CompetitorHelpers.RANGE_OFFICER_MARKER`
- Scoped as `v16.1.0` **MINOR**: the new endpoints are backward-compatible additions, and nothing removed was part of
  the public API
- `jacoco.version` is bumped to 0.8.16 and `springdoc-openapi-bom` to 3.2.0
- Improvement plan: Gaps #6, #41 and #42 are closed, leaving all 42 gaps closed and none open
- The suite stands at 1,439 passing tests
- Project version bumped to 16.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

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
- Spring Boot is bumped to 4.1.2, which manages Tomcat `11.0.27`, so the `tomcat.version` override is removed
- `json-smart`, `json-path` and `jsonassert` are excluded from the test classpath (CVE-2026-113602 and
  CVE-2026-110974, no fixed releases), and the `asm` pin that existed for them is removed
- Improvement plan: Gap #41 is added for the score-scale constants and `NumberUtil.calculateSum` that v15.0.0
  reinstated without a production caller, and Gap #26 is closed, leaving 39 gaps closed, #6 partially completed and #41
  open
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

---

## 🎯 Major Milestones

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

### Milestone 54: Shooter Log Management & Overall Results (v16.1.0)

- `IpscShooterLogController` and `IpscShooterLogService` manage shooter logs and the matches they cover, refusing a
  match scheduled outside the log's dates
- `IpscShooterLogOverallController` and `IpscShooterLogOverallService` calculate and read a log's overall results from
  each competitor's best four match percentages
- `ShooterLogCompetitor` and its table are dropped
- Improvement plan Gaps #6, #41 and #42 are closed, so every tracked gap is now closed

### Milestone 55: Verified-Only Shooter Results (v16.1.1)

- Shooter results and a shooter log's overall results include only verified competitors, and match results only
  competitors with points above zero
- Results whose competitor stops qualifying are deleted on the next calculation
- The docs, Javadoc and OpenAPI text say who is included and how a competitor becomes verified
- Improvement plan Gaps #43 and #44 are added and closed, so every tracked gap stays closed

### Milestone 56: Shooter Log Dates Enforced After the Log Is Written (v16.1.2)

- A shooter log's calculation refuses a match outside the log's dates, and rescheduling a match outside the dates of a
  log that covers it is refused
- A match updated after the overall results were calculated marks them out of date
- One shared date check serves the log, the calculation and the reschedule
- Improvement plan Gap #45 is added and closed, so every tracked gap stays closed

### Milestone 57: Verifying Competitors in Bulk (v16.2.0)

- `POST /ipsc/competitors/verify` verifies several competitors in one transaction, reporting an unknown ID in its own
  result
- A match competitor import row for an unverified competitor is still imported, with a message saying it is left out of
  results until the competitor is verified
- Improvement plan Gap #46 is added and closed, so every tracked gap stays closed

### Milestone 58: Shooter Log Bulk CSV Import (v16.3.0)

- `POST /ipsc/shooter-logs/bulk` creates shooter logs from CSV, each row on its own and checked as a single log is
- A failed row is reported with the values it supplied while the rest are still created, and the endpoint answers `422`
  only when every row failed
- A null match ID in a shooter log request is a `400` instead of a `500`
- Improvement plan Gap #47 is added and closed, so every tracked gap stays closed

### Milestone 59: Changing Existing Records in Bulk (v16.4.0)

- `PUT` and `PATCH /ipsc/shooter-logs/bulk` replace and update existing shooter logs from CSV, each row on its own,
  and a `PATCH` can add and remove matches
- A bulk competitor row for an existing club number updates and verifies that competitor, with a result for every row
- A match outside a covering shooter log's dates reports it in its `warnings` when read or edited
- `flyway-mysql` follows the Flyway version Spring Boot manages, with the migrations checked against MySQL 8.4
- A `migrations` CI job runs the Flyway migrations against MySQL 8.4 on every push and pull request
- Improvement plan Gaps #48, #49 and #50 are added and closed, so every tracked gap stays closed

### Milestone 60: Making an Absent Home Club Explicit (v16.4.1)

- `IpscMatchCompetitorServiceImpl.resolveCompetitorHomeClub` returns an `Optional<ClubIdentifier>` rather than `null`,
  with the rows the import counts as the club's unchanged
- No improvement plan gap is added or closed, so every tracked gap stays closed

---

## 🛤️ Future Roadmap Implications

### Previously Completed (v16.4.1)

- `IpscMatchCompetitorServiceImpl.resolveCompetitorHomeClub` returns an `Optional<ClubIdentifier>`, empty when the
  competitor has no home club or the home club has no identifier, instead of `null`
- Improvement plan: no gap is added or closed, leaving all 50 gaps closed and none open
- Project version bumped to 16.4.1 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v16.4.0)

- `PUT` and `PATCH /ipsc/shooter-logs/bulk` replace and update shooter logs from CSV through
  `IpscShooterLogService.updateShooterLogs` and `patchShooterLogs`, each row on its own, answering `422` only when
  every row failed
- `ShooterLogPatchRequest.addMatchIds` and `removeMatchIds` add and remove matches on the single and the bulk `PATCH`
- `IpscCompetitorServiceImpl.createCompetitors` updates and verifies a competitor whose club number a row names, with
  `CompetitorBulkResponse` and `CompetitorResponseHolder.results` reporting each row
- `MatchResponse.warnings`, filled by `IpscMatchServiceImpl` and `ShooterLogHelpers.describeOutsideDates`, reports a
  match outside a covering log's dates
- The match, competitor and match competitor CSV imports no longer read their own ID column
- `flyway-mysql` is no longer pinned above the Flyway version Spring Boot manages
- `FlywayMysqlMigrationTest` and a `migrations` job in `build.yml` run the Flyway migrations against MySQL 8.4 in CI
- Improvement plan: Gaps #48, #49 and #50 are added and closed, leaving all 50 gaps closed and none open
- Project version bumped to 16.4.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v16.3.0)

- `POST /ipsc/shooter-logs/bulk` creates shooter logs from CSV through `IpscShooterLogService.createShooterLogs`, each
  row on its own and checked as a single log is, answering `422` only when every row failed
- `ShooterLogRequestCsvMixIn`, `ShooterLogBulkResponse`, `ShooterLogBulkResponseHolder` and `ShooterLogRow` support it,
  and a failed row is reported with the values it supplied
- `ShooterLogRequest.validate` rejects a null match ID with a `400`
- Improvement plan: Gap #47 is added and closed, leaving all 47 gaps closed and none open
- Project version bumped to 16.3.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v16.2.0)

- `POST /ipsc/competitors/verify` verifies several competitors in one transaction through
  `IpscCompetitorService.verifyCompetitors`, reporting an ID that matches nobody in its own result
- `CompetitorVerifyRequest`, `CompetitorVerifyResponse`, `CompetitorVerifyResponseHolder` and
  `CompetitorRepository.findAllByIdInWithHomeClubAndEmailAddresses` support it
- A match competitor import row whose competitor is not verified carries a message saying it is left out of shooter
  results until the competitor is verified
- Improvement plan: Gap #46 is added and closed, leaving all 46 gaps closed and none open
- Project version bumped to 16.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v16.1.2)

- A shooter log's overall calculation refuses a match outside the log's dates, and rescheduling a match outside the
  dates of a shooter log that covers it is refused, both with a `400`
- A match updated after the overall results were calculated marks them out of date
- `ShooterLogHelpers` holds the date check once, shared by the log, the calculation and the reschedule, and the
  unreachable missing-scheduled-date check is removed
- Improvement plan: Gap #45 is added and closed, leaving all 45 gaps closed and none open
- Project version bumped to 16.1.2 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v16.1.1)

- Shooter results include only verified competitors with points above zero, and a shooter log's overall results only
  verified competitors
- A result whose competitor no longer qualifies is deleted on the next calculation, through the new
  `TransactionService.replaceShooterResults`
- `README.md`, `ARCHITECTURE.md`, the service Javadoc and the OpenAPI descriptions state who is included and how a
  competitor becomes verified
- Unreachable null handling in the shooter result and overall services is removed, and the kept tolerance for a
  missing percentage is documented
- Improvement plan: Gaps #43 and #44 are added and closed, leaving all 44 gaps closed and none open
- Project version bumped to 16.1.1 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v16.1.0)

- `IpscShooterLogController`/`IpscShooterLogService` create, replace, patch, read and delete shooter logs, and refuse a
  match outside the log's dates
- `IpscShooterLogOverallController`/`IpscShooterLogOverallService` calculate and read a shooter log's overall results
  from each competitor's best `IpscConstants.SHOOTER_LOG_BEST_MATCHES` percentages
- `ShooterLogCompetitor`, `ShooterLogCompetitorRepository` and the `shooter_log_competitor` table are removed with
  `V11_12_0__drop_shooter_log_competitor.sql`
- The unused score-scale constants, `SystemConstants.DEFAULT_SCALE` and `NumberUtil.calculateSum` are removed
- `jacoco.version` and `springdoc-openapi-bom` are bumped
- Improvement plan: Gaps #6, #41 and #42 are closed, leaving all 42 gaps closed and none open
- Project version bumped to 16.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v16.0.0)

- `Division.RIFLE_PCC` is added, and the semi-auto rifle divisions are renamed with
  `V11_11_0__rename_semi_auto_rifle_division_names.sql`
- `CompetitorHelpers` strips trailing modified-entry digits and `IpscConstants.REPLACE_IN_NAMES_REGEX` removes a "TRO"
  marker
- `IpscMatchCompetitorServiceImpl.createMatchCompetitors` runs in one transaction
- Spring Boot is bumped to 4.1.2 and the `tomcat.version` override is removed
- `json-smart` and `jsonassert` are excluded from the test classpath and the `asm` pin is removed
- Improvement plan: Gap #41 is added and Gap #26 closed, leaving 39 closed, #6 partially completed and #41 open
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
