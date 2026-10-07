## 🎯 Summary

- **Moves field copying into mappers**: `CompetitorMapper`, `MatchMapper` and `MatchCompetitorMapper` take over the
  lookups and field copying the three IPSC services carried, each with a new `applyPatchFields`.
- **Ties a division to its firearm type**: every `Division` has a unique name and a `FirearmType`, a match competitor's
  division is checked against its firearm type, and a firearm type that is left out is taken from the division.
- A **MAJOR** release: three groups of breaking API changes — see the Migration Guide in `RELEASE_NOTES.md` for what
  callers must change. Four Flyway migrations run on upgrade.

## 📦 Key Changes

**Added**

- `CompetitorMapper`, `MatchMapper` and `MatchCompetitorMapper`, and `Division.getFirearmType()`
- `V11_2_0` (firearm type and power factor `NOT NULL`), `V11_3_0` (`match_competitor.date_calculated`), `V11_4_0`
  (unique division names) and `V11_5_0` (`Lady Senior`)
- `validate()` on `CompetitorRequest`, `MatchRequest` and `MatchCompetitorRequest`, and entity-based constructors on the
  response models

**Changed**

- **Breaking:** `MatchCompetitorResponse.competitorName` replaced by a `competitorNames` list
- **Breaking:** `powerFactor` is required on a match competitor request
- **Breaking:** the shotgun, .22, mini rifle and semi-auto rifle division names are renamed, and `Lady, Senior` is now
  `Lady Senior`
- `MatchCompetitorRequest.firearmType` is optional and taken from the division when left out
- Score fields moved into `IpscMatchScore` (`weightedPoints` renamed `points`), and `NumberUtil` percentage and sum
  methods take a `scale`
- `.gitattributes` normalises text files to LF; `qodana.yaml` uses the `qodana.recommended` profile
- Version bumped to 13.0.0 in `pom.xml` and `@OpenAPIDefinition`

**Fixed**

- `NumberUtil.calculatePercentage` no longer rounds the ratio too early at a scale below 2

**Removed**

- The unused `code` and `abbreviation` fields and the test-only lookups from `Division`, `FirearmType`, `PowerFactor`
  and `CompetitorCategory`, and the two manual action rifle divisions

## 🧪 Test Plan

- [x] `./mvnw test` — 1,265 tests, no failures, errors or skips
- [ ] `V11_2_0` to `V11_5_0` run against a MySQL profile with existing match competitor rows (the tests use H2)
- [ ] Qodana, CodeQL and Docker workflows pass on this PR
- [x] `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v13/RELEASE_NOTES_v13.0.0.md`

## 👥 Contributors

Leoni Lubbinge and Claude Code (Claude Sonnet 5.5, co-author of most commits).

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
