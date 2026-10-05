## 🎯 Summary

- **Puts the competitor lookup to work**: `EntityIpscCompetitorService.findCompetitor` is wired into the match
  competitor service, matching a competitor by number, then ID number, then full name.
- **Makes the match competitor bulk import tolerant and club-aware**: each row is saved on its own, and the import
  creates one club's rows — HPSC's own unless a `club` parameter asks for another.
- A **MAJOR** release: four groups of breaking API changes — see the Migration Guide in `RELEASE_NOTES.md` for what
  callers must change. No database migration runs on upgrade.

## 📦 Key Changes

**Added**

- `ClubService` and `ClubServiceImpl`, holding the null-safe club comparisons
- An optional `club` query parameter on `POST /ipsc/match-competitors/bulk`
- `CompetitorHelpers.getCompetitorNumberAsInteger`, `CompetitorRepository.findAllByIdNumber` and `StringUtil.hasText`
- `qodana.yaml` with the `JavadocReference` inspection

**Changed**

- **Breaking:** the alias numbers 15000 and 16000 no longer match by competitor number, and a non-numeric number is
  looked up as an ID number, answering `404` instead of `400`
- **Breaking:** the bulk import is a partial import with one result per row, and `422` when every row fails
- **Breaking:** `MatchCompetitorResult(Holder)` renamed `MatchCompetitorBulkResponse(Holder)`, with `matchCompetitors`
  replacing `matchCompetitorResults` in the body
- **Breaking:** the bulk import is limited to one club, HPSC's by default, and reports other rows as skipped
- The match category is optional and defaults to Club Shoot; a nickname defaults to the first name; the RO marker is
  removed from a name wherever it appears
- `*Utils` classes renamed back to `*Util`; `IpscConstants` documented; `mysql-connector-j` pinned to `26.7.0`
- Version bumped to 12.0.0 in `pom.xml` and `@OpenAPIDefinition`

**Fixed**

- `ControllerResponse` derives `success` from its error the right way round
- `findCompetitor` no longer fails on a numeric value too long for an `int`

**Removed**

- `IpscConstants.MAX_SAPSA_NUMBER` and four unused score-scale constants

## 🧪 Test Plan

- [x] `./mvnw test` — 1,227 tests, no failures, errors or skips
- [ ] `./mvnw verify -Pcoverage` — coverage gate (not re-run for this release)
- [ ] `mysql-connector-j` `26.7.0` checked against a MySQL profile (the application starts and connects)
- [ ] Qodana, CodeQL and Docker workflows pass on this PR
- [x] `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v12/RELEASE_NOTES_v12.0.0.md`

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
