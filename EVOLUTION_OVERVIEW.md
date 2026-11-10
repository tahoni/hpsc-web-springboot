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

---

### Phase 51: Complete Failure Reporting for Bulk Imports, Club Resolution & Shorter Division Names (v14.0.0)

**Duration:** October 9, 2026

A major release: a failed bulk match competitor row now lists every missing or unresolvable field with the values it
supplied, a club is resolved by code, abbreviation or name through one service, and the divisions lose their trailing
" Division". The division names, the `400` for an unknown club, the renamed `matchCompetitorResults` field and the
`null` failed-row response are backward-incompatible, so it is MAJOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Database**

- `V11_7_0__drop_division_suffix_from_division_names.sql` renames the stored `division` in `match_competitor`,
  `shooter_log_competitor` and `shooter_log_overall`

**Helpers, Mappers & Models**

- `MatchCompetitorHelpers.getErrorMessagesForMissingRequiredFields` describes each required field still unset, and
  `MatchCompetitorMapper.populateResolvableFields` resolves what it can without throwing
- `MatchCompetitorRow` and `MatchCompetitorRowMapper` describe a failed row by the values already resolved; the
  `MatchCompetitorBulkResponse.matchCompetitor` of a failed row is `null`
- `MatchCompetitor` fields are `@NotNull`, and `Division` and `CompetitorCategory` follow the new naming and lookup
  rules

**Services**

- `IpscEntityClubService` finds a club by code, abbreviation or name; `ClubService` and `EntityIpscCompetitorService`
  are renamed, and `FatalException` is dropped where only the club lookup declared it
- `IpscMatchCompetitorServiceImpl.createMatchCompetitors` checks a row once and lists every problem

**Removals**

- `NumberUtil`, `DateUtil`, `SystemConstants.DEFAULT_SCALE`, two unused utility methods and
  `MatchCompetitorResponseHolder`, together with controller tests that only checked delegation
- Improvement plan: Gaps #38, #39 and #40 closed, leaving 38 closed and #6 and #26 partially completed

**Build & Metadata**

- Project version bumped to 14.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Making a failed bulk import actionable, resolving clubs in one place and matching divisions by their short names

**Test Coverage:**

- The suite stands at 1,263 passing tests, with new tests for the helper, the row mapper, the club service and the new
  division names

### Phase 52: Shooter Result Calculation & Reading (v15.0.0)

**Duration:** October 17, 2026

A major release: a match's shooter results are calculated and read through new endpoints, ranked by match points
within each competitor's firearm type, division and competitor category. The removed overall and club rankings and the
renamed shooter log `points` are backward-incompatible, so it is MAJOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Database**

- `V11_8_0__remove_match_competitor_rankings.sql` drops `overall_ranking` and `club_ranking` from `match_competitor`
- `V11_9_0__rename_shooter_log_points_to_percentage_in_log.sql` renames `points` to `percentage_in_log` in
  `shooter_log_competitor` and `shooter_log_overall`
- `V11_10_0__create_shooter_result.sql` creates `shooter_result`, unique per match and match competitor

**Services & Controllers**

- `IpscShooterResultService.calculateMatchShooterResult` ranks a match's competitors within their firearm type,
  division and competitor category, with ties sharing a rank and each percentage relative to the group winner, and
  recalculates only when the results are out of date
- `IpscShooterResultController` exposes `POST /ipsc/shooter-results/matches/{matchId}` and the read endpoints
  `GET /ipsc/shooter-results/matches/{matchId}` and `GET /ipsc/shooter-results`
- `TransactionService.saveShooterResults` saves a batch of results in one transaction

**Models & Utilities**

- `ShooterResultResponse`, `ShooterMatchResultResponse` and `ShooterResponseHolder` describe a match linked to its
  results, with the percentage shown as a display string
- `NumberUtil`, the score scale constants and `SystemConstants.DEFAULT_SCALE` are reinstated

**Build & Metadata**

- `org.ow2.asm:asm` is pinned to 9.11
- Improvement plan: Gap #6 progresses, leaving 38 closed and #6 and #26 partially completed
- Project version bumped to 15.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Ranking a match's competitors fairly within their group and keeping the stored results current

**Test Coverage:**

- The suite stands at 1,320 passing tests, with new tests for the shooter result service, controller and `NumberUtil`

### Phase 53: Rifle PCC Division, Clearer Semi-Auto Division Names & Cleaner Competitor Name Matching (v16.0.0)

**Duration:** October 31, 2026

A major release: the Rifle PCC division is added, and the semi-auto rifle divisions are renamed "Rifle Semi Auto Open"
and "Rifle Semi Auto Standard". A division given by the old "Rifle Open" or "Rifle Standard" names no longer resolves,
which is backward-incompatible, so it is MAJOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Database**

- `V11_11_0__rename_semi_auto_rifle_division_names.sql` renames the stored `division` in `match_competitor`,
  `shooter_log_competitor` and `shooter_log_overall`, so existing rows still read back as a division

**Domain & Utilities**

- `Division.RIFLE_PCC` adds the "Rifle PCC" division
- `CompetitorHelpers.cleanCompetitorName` strips trailing modified-entry digits of any length, and
  `IpscConstants.REPLACE_IN_NAMES_REGEX` also removes a "TRO" or "(TRO)" role marker, so more names match their
  competitor

**Services**

- `IpscMatchCompetitorServiceImpl.createMatchCompetitors` is `@Transactional`, so a bulk import is saved or rolled back
  as one unit

**Build & Metadata**

- Spring Boot is bumped to 4.1.2, which manages Tomcat `11.0.27`, so the `tomcat.version` override is removed
- `json-smart`, `json-path` and `jsonassert` are excluded from the test classpath (CVE-2026-113602 and
  CVE-2026-110974, no fixed releases), and the `asm` pin that existed for them is removed
- Improvement plan: Gap #41 is added and Gap #26 closed, leaving 39 closed, #6 partially completed and #41 open
- Project version bumped to 16.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Keeping stored division names in step with the enum, and matching names as they arrive in real exports

**Test Coverage:**

- The suite stands at 1,327 passing tests, with new `cleanCompetitorName` tests for trailing digits and role markers


### Phase 54: Shooter Log Management & Overall Results (v16.1.0)

**Database**

- `V11_12_0__drop_shooter_log_competitor.sql` drops the `shooter_log_competitor` table, which nothing references any
  more, since a competitor's rank and percentage in a log live in `shooter_result`

**Controllers & Services**

- `IpscShooterLogController` and `IpscShooterLogService` create, replace, patch, read and delete shooter logs under
  `/ipsc/shooter-logs`, with the matches a log covers set by match ID; a match scheduled outside the log's start and end
  dates is refused, and a log with overall results cannot be deleted
- `IpscShooterLogOverallController` and `IpscShooterLogOverallService` calculate and read a log's overall results under
  `/ipsc/shooter-log-overall-results`: each competitor's overall percentage is the average of their best
  `IpscConstants.SHOOTER_LOG_BEST_MATCHES` (four) percentages, ranked within their division and competitor category
  with ties sharing a rank, and recalculated only when out of date
- `TransactionService` gains `saveShooterLog`, `deleteShooterLog` and `replaceShooterLogOverallResults`

**Domain & Utilities**

- `ShooterLogCompetitor` and `ShooterLogCompetitorRepository` are removed
- `IpscConstants.MATCH_POINTS_SCALE`, `HIT_FACTOR_SCALE`, `TIME_SCALE` and `REPLACE_IN_NAMES_REGEX`,
  `SystemConstants.DEFAULT_SCALE` and `NumberUtil.calculateSum` are removed; the name regex is inlined into
  `CompetitorHelpers.RANGE_OFFICER_MARKER`

**Build & Metadata**

- `jacoco.version` is bumped to 0.8.16 and `springdoc-openapi-bom` to 3.2.0
- Improvement plan: Gaps #6, #41 and #42 are closed, leaving all 42 gaps closed and none open
- Project version bumped to 16.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Letting a shooter log be built over the API and calculated from the matches inside its dates

**Test Coverage:**

- The suite stands at 1,439 passing tests, with new controller, service and integration tests for both shooter log
  stacks and request validation tests


### Phase 55: Verified-Only Shooter Results (v16.1.1)

**Controllers & Services**

- `IpscShooterResultServiceImpl` includes only competitors whose `isVerified` is `true` and whose points are above zero,
  and deletes the result of one who stops qualifying; any such stale result forces a recalculation
- `IpscShooterLogOverallServiceImpl` includes only verified competitors and deletes the overall of one who is not
- `TransactionService.replaceShooterResults` deletes stale results and saves the recalculated ones in one transaction
- The unreachable points filter in `IpscShooterResultServiceImpl.rank`, the null fallback on the winner's points, the
  null-category fallback to `NONE` in both services and the null-safe ordering of a match's scheduled date are removed,
  since their columns are `NOT NULL`; the tolerance for a missing percentage in `IpscShooterLogOverallServiceImpl`
  stays, documented, because that column is nullable

**Documentation**

- `README.md` and `ARCHITECTURE.md` state who is included in results and that only the competitor endpoints verify a
  competitor; the service Javadoc and the controllers' OpenAPI descriptions say the same

**Build & Metadata**

- Improvement plan: Gaps #43 and #44 are added and closed, leaving all 44 gaps closed and none open
- Project version bumped to 16.1.1 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Making a result mean a verified competitor who scored, and keeping stored results in step with who still qualifies

**Test Coverage:**

- The suite stands at 1,447 passing tests, with new service tests for null, zero and unverified competitors and for
  stale-result deletion, and a `TransactionService` test for `replaceShooterResults`


### Phase 56: Shooter Log Dates Enforced After the Log Is Written (v16.1.2)

**Controllers & Services**

- `IpscShooterLogOverallServiceImpl.calculateShooterLogOverall` refuses a log that covers a match outside its dates,
  and a match updated after the overall results were calculated marks them out of date
- `IpscMatchServiceImpl.updateMatch` and `patchMatch` refuse moving a match to another day outside the dates of a
  shooter log that covers it, found through `ShooterLogRepository.findAllByMatchesId`
- `ShooterLogHelpers` holds the date check once for `IpscShooterLogServiceImpl`, the calculation and the reschedule
  check, and the unreachable missing-scheduled-date check is removed

**Documentation**

- `README.md` and `ARCHITECTURE.md` state the rule; the service Javadoc and the controllers' OpenAPI descriptions add
  the `400` responses

**Build & Metadata**

- Improvement plan: Gap #45 is added and closed, leaving all 45 gaps closed and none open
- Project version bumped to 16.1.2 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Keeping a rule enforced on writing a shooter log true when a match changes afterwards

**Test Coverage:**

- The suite stands at 1,476 passing tests, with new helper, match service and overall service unit and integration
  tests


### Phase 57: Verifying Competitors in Bulk (v16.2.0)

**Controllers & Services**

- `IpscCompetitorController.verifyCompetitors` serves `POST /ipsc/competitors/verify`, and
  `IpscCompetitorService.verifyCompetitors` verifies every competitor that exists in a single transaction through
  `TransactionService.saveCompetitors`, reporting an unknown ID, and one already verified, in its own result
- `IpscMatchCompetitorServiceImpl.createMatchCompetitors` still imports a row whose competitor is not verified, with a
  message saying the entry is left out of shooter results until the competitor is verified

**Models & Repositories**

- `CompetitorVerifyRequest`, `CompetitorVerifyResponse` and `CompetitorVerifyResponseHolder` carry the request and the
  per-competitor results
- `CompetitorRepository.findAllByIdInWithHomeClubAndEmailAddresses` fetch-joins the home clubs and email addresses of
  several competitors

**Documentation**

- `README.md` and `ARCHITECTURE.md` describe bulk verification and the import message; the service Javadoc and the
  controllers' OpenAPI descriptions say the same

**Build & Metadata**

- Improvement plan: Gap #46 is added and closed, leaving all 46 gaps closed and none open
- Project version bumped to 16.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Making the verified-competitor rule workable: verifying many competitors at once, and telling an importer when a row
  will be left out of results

**Test Coverage:**

- The suite stands at 1,498 passing tests, with new controller, request model, service and integration tests for
  bulk verification and for the unverified match competitor import message
