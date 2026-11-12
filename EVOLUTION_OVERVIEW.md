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
