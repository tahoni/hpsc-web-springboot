## 🎯 Summary

- The bulk competitor and match imports save each row in its own transaction and report a row that cannot be imported,
  instead of failing the whole request and saving nothing
- Every bulk import row now carries its `rowNumber`, and a failed competitor or match row reports the values it supplied
- A MAJOR release: the renamed `messages` field, the new match import response and a bad row no longer failing the
  request are backward-incompatible, each flagged **Breaking** in `CHANGELOG.md`

## 📦 Key Changes

- **Added:** `MatchBulkResponse` and `MatchRow`; `CompetitorBulkResponse.rowNumber`, `success` and `competitorRow` with
  the new `CompetitorRow`; `rowNumber` on the match competitor and shooter log bulk responses
- **Changed:** `MatchResponse.warnings` is now `messages`; `MatchResponseHolder` is now `MatchBulkResponseHolder` with a
  result per row; `CompetitorResponseHolder` is now `CompetitorBulkResponseHolder`; a competitor row repeating an
  earlier row's club number is reported as failed; unreachable branches are removed
- **Removed:** `TransactionService.saveMatches`, `saveMatchCompetitors` and `saveShooterResults`, which nothing called
- **Documentation:** `ARCHITECTURE.md` describes the imports as per-row; `CHANGELOG.md`, `HISTORY.md`,
  `EVOLUTION_OVERVIEW.md` and `RELEASE_NOTES.md` cover 17.0.0; improvement plan Gaps #51 and #52 are added and closed
- **Build:** project version bumped to 17.0.0 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw test` passes: 1,601 tests, no failures or errors, 3 skipped unless `FLYWAY_MYSQL_URL` is set
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[17.0.0]`
- [ ] Import a competitor CSV and a match CSV that each include a bad row, and confirm the bad row is reported with its
  values while the other rows are saved

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
