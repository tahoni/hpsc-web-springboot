## 🎯 Summary

- **Adds the match competitor endpoints** at `/ipsc/match-competitors`: create, replace, patch, get one or all and
  delete a competitor's entry in a match, backed by the new `IpscMatchCompetitorService`.
- **Adds a bulk CSV import** for match competitors, `POST /ipsc/match-competitors/bulk`, that checks every row before
  saving any, so a file is created in full or not at all.
- **Reworks the shooter log entities** — a match competitor and a shooter log competitor can have several competitor
  categories, `ShooterLog` becomes a date range of matches and the new `ShooterLogOverall` holds overall standings.
- A **MINOR** release: new endpoints and optional fields only, nothing removed or made stricter — see the Migration
  Guide in `../../../../RELEASE_NOTES.md` for the eleven migrations that run on upgrade.

## 📦 Key Changes

**Added**

- `IpscMatchCompetitorController`, `IpscMatchCompetitorService` and `MatchCompetitorRequest`/
  `MatchCompetitorPatchRequest`/`MatchCompetitorResponse`; one entry per competitor, match and firearm type
- `MatchCompetitorRequestCsvMixIn`, `MatchCompetitorResponseHolder` and the bulk endpoint; a duplicate of an existing
  entry, or of another row, is refused
- `TransactionService.saveMatchCompetitor`, `saveMatchCompetitors` and `deleteMatchCompetitor`
- `ShooterLogOverall` and `ShooterLogOverallRepository`; `ShooterLogCompetitor.dateCalculated`, `competitorCategory`
  and `division`
- Optional `Competitor.isVerified` on the competitor request, patch and response models (existing rows set to `true`)
- Eleven Flyway migrations, `V8_0_0` to `V8_9_0`

**Changed**

- `MatchRequest.matchFirearmType` and `matchCategory` are no longer required, in JSON or CSV
- `MatchCompetitor` and `ShooterLogCompetitor` hold a list of competitor categories in child tables; `division` is
  required on `MatchCompetitor` and `firearmType` is optional
- `ShooterLog` is a date range linked to matches, and `ShooterLogCompetitor` links to `Competitor` directly
- Deleting a match or competitor is also refused while a shooter log references it
- `emailAddresses` follows `cellphoneNumber` in the competitor models; JSON and CSV formats are unchanged
- Version bumped to 9.1.0 in `../../../../pom.xml` and `@OpenAPIDefinition`
- `../../../../AGENTS.md`'s Release Checklist and the `prep-version-release` skill gain a table-alignment step

**Dependencies**

- The `mysql-connector-j` `9.4.0` pin is dropped now that Spring Boot `4.1.1` manages `9.7.0`; the `tomcat.version`
  override stays (Gap #26)

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` — 1,095 tests across 77 classes, 0 failures/errors/skipped; 98.22% line / 98.97%
  branch coverage, JaCoCo gate (97%) passing
- [ ] Qodana, CodeQL and Docker workflows pass on this PR
- [x] `../../../../RELEASE_NOTES.md` archived byte-for-byte to ``
- [x] No version-specific references leaked into `../../../../README.md`/`ARCHITECTURE.md`
- [x] Markdown table columns aligned in every tracked document that was changed

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
