## 🎯 Summary

- **Removes match stages** from the match API, the services and the database: `stages` leaves `MatchRequest` and
  `MatchResponse`, the `IpscMatchStage` and `MatchStageCompetitor` entities and repositories go, and a new Flyway
  migration drops their tables (existing stage data is discarded).
- **Simplifies the CSV imports**: each row is read straight into a `MatchRequest` or `CompetitorRequest` through a
  Jackson mix-in, so the `*ForCSV` models are gone, and a CSV header may omit optional columns.
- **Adds `MatchPatchRequest` and `CompetitorPatchRequest`**, so `PATCH` on a match or competitor needs no body fields.
- A **MAJOR** release: several changes break existing callers or deployments — see the Migration Guide in
  `RELEASE_NOTES.md`.

## 📦 Key Changes

**Changed**

- **Breaking:** `MatchRequest`/`MatchResponse` lose `stages`, the match CSV import loses its `Stages` column, and
  `MatchRequest.matchFirearmType`/`matchCategory` are now required properties
- **Breaking:** the datasource username is no longer read from `MYSQL_USER`; the `dev` and `prod` profiles set it
- `MatchRequestCsvMixIn`/`CompetitorRequestCsvMixIn` replace `MatchRequestForCSV`/`CompetitorRequestForCSV`;
  `IpscCompetitorServiceImpl.toRequest` becomes `normaliseCsvRequest` and keeps the row's `competitorId`
- `MatchPatchRequest`/`CompetitorPatchRequest` replace the full request models on the `PATCH` endpoints; a competitor
  patch that omits `emailAddresses` keeps the existing addresses
- A competitor's home club also resolves by club abbreviation
- `mysql-connector-j` pinned to `9.4.0`; the `dev` datasource URL uses `127.0.0.1`; the local profile drops
  `MYSQL_LOCAL_PASSWORD`
- Version bumped to 9.0.0 in `pom.xml` and `@OpenAPIDefinition`; `tomcat.version` override kept (Gap #26)

**Security**

- `tomcat.version` raised to `11.0.26`; `logback.version` pinned to `1.6.5`; the Jackson BOMs imported and
  `jackson-dataformat-csv`/`flyway-mysql` pinned above the Spring Boot-managed versions

**Removed**

- **Breaking:** `IpscMatchStage`, `MatchStageCompetitor`, their repositories, `MatchStageRequest`/`MatchStageResponse`
  and `TransactionService.saveMatch`'s stage overload and `StageSaveMode`
- `V7_9_0__drop_ipsc_match_stage.sql` drops the `match_stage_competitor` and `ipsc_match_stage` tables

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` — 947 tests across 69 classes, 0 failures/errors/skipped; 98.32% line / 98.69% branch
  coverage, JaCoCo gate (97%) passing
- [ ] Qodana, CodeQL and Docker workflows pass on this PR
- [x] `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v9/RELEASE_NOTES_v9.0.0.md`
- [x] No version-specific references leaked into `README.md`/`ARCHITECTURE.md`

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
