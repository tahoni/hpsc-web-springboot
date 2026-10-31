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
- Improvement plan: Gap #41 is added and Gap #26 closed, leaving 39 closed, #6 partially completed and #41 open
- Project version bumped to 16.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Keeping stored division names in step with the enum, and matching names as they arrive in real exports

**Test Coverage:**

- The suite stands at 1,327 passing tests, with new `cleanCompetitorName` tests for trailing digits and role markers
