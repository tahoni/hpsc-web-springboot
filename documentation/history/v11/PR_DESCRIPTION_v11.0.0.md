## 🎯 Summary

- **Tightens the competitor contract**: every bulk CSV endpoint consumes `text/plain`, the competitor's `nickname`
  becomes `nickName`, and `competitorNumber` becomes a whole number.
- **Adds `EntityIpscCompetitorService.findCompetitor`**, which resolves a single competitor from a full name and a
  competitor number — number first, then "FirstName LastName" or "NickName LastName".
- A **MAJOR** release: three groups of breaking API changes — see the Migration Guide in `RELEASE_NOTES.md` for what
  callers must change and the two migrations that run on upgrade.

## 📦 Key Changes

**Added**

- `EntityIpscCompetitorService` and its implementation, a Spring `@Service` not yet called by any other service
- `MatchCompetitorResult` and `MatchCompetitorResultHolder` response models, not yet returned by any endpoint
- Two Flyway migrations, `V11_0_0` and `V11_1_0`

**Changed**

- **Breaking:** every `POST /bulk` endpoint consumes `text/plain` instead of `text/csv`
- **Breaking:** `nickname` renamed `nickName` in the entity, JSON and CSV (`NickName`), and `nick_name` in the table
- **Breaking:** `competitorNumber` is an `Integer` in the entity and an `INT` column, returned as a number by
  `CompetitorResponse`; requests keep a string, which must be a whole number
- `CompetitorRepository` finders renamed `findAll*`, with a new nickname-aware full-name finder
- `IpscConstants.MAX_SAPSA_NUMBER` raised to `999_999`
- Version bumped to 11.0.0 in `pom.xml` and `@OpenAPIDefinition`

**Fixed**

- The "RO" suffix is stripped from a full name that has trailing whitespace

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` — 1,109 tests, no failures, errors or skips; coverage gate met (98.7% of branches)
- [ ] `V11_1_0` checked against a MySQL database: no `competitor.competitor_number` value is other than a whole number
- [ ] Qodana, CodeQL and Docker workflows pass on this PR
- [x] `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v11/RELEASE_NOTES_v11.0.0.md`

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
