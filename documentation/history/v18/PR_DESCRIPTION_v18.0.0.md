## 🎯 Summary

- A shooter log overall now records the matches it was calculated from, in a new join table, and returns their
  identifiers as `matchIds`
- Calculating overalls works once the persistence session has closed, after the entities' lazy associations were left
  out of their generated `equals`, `hashCode` and `toString`
- A MAJOR release: the OpenAPI document and Swagger UI are off by default and on only under the `dev` profile, which
  stops a deployment that relied on them serving them, flagged **Breaking** in `CHANGELOG.md`

## 📦 Key Changes

- **Added:** `V11_13_0` and the `shooter_log_overall_match` table; `ShooterLogOverall.matches`;
  `ShooterLogOverallResponse.matchIds`; `@since` tags on the four mapper classes and 74 public methods; new test cases
  for the recorded matches, the fetch joins and the calculation without a surrounding transaction
- **Changed:** `calculateShooterLogOverall` picks each competitor's best matches once, records them and recalculates an
  overall whose matches are missing or differ; `ShooterLogOverallRepository` fetches the matches; the lazy associations
  of six entities are left out of `equals`, `hashCode` and `toString`; the OpenAPI document and Swagger UI are off by
  default; `README.md`, `AGENTS.md`, `ARCHITECTURE.md` and `CONTRIBUTING.md` say so
- **Removed:** brittle tests that mirrored the implementation, duplicated another layer's test or pinned message text,
  including `TransactionServiceImplTest`
- **Documentation:** `ARCHITECTURE.md` lists `ShooterLogOverall`'s link to `IpscMatch` and the real test tree,
  `AGENTS.md` describes the service test tiers as they are, and `CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md`
  and `RELEASE_NOTES.md` cover 18.0.0; improvement plan Gaps #53, #54 and #55 are added and closed
- **Build:** project version bumped to 18.0.0 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw test` passes: 1,495 tests, no failures or errors, 3 skipped unless `FLYWAY_MYSQL_URL` is set
- [x] `./mvnw verify -Pcoverage` meets the 97% line and branch minimums, at 98.6% line and 99.9% branch coverage
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[18.0.0]`
- [ ] Start the application under the `dev` profile, calculate a shooter log's overalls and confirm each result lists
  its `matchIds`, then confirm `/v3/api-docs` answers under `dev` but not under `prod`
- [ ] Run the migrations against MySQL, with `FLYWAY_MYSQL_URL` set, and confirm `V11_13_0` applies

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
