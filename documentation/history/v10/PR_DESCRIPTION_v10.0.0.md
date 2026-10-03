## 🎯 Summary

- **Reshapes the match competitor models and CSV import** around a PractiScore export: renamed CSV headers,
  `matchPoints` becomes `points`, and new overall-score fields (`%`, `Time`, `% psbl`, `A`, `C`, `D`, `M`, `NPM`,
  `NS`, `Proc`, `Apen`).
- **Identifies a competitor by number or name**, so `competitorId` is no longer required.
- **Returns `competitorCategory` to a single value** in a plain column, replacing the list and its two child tables.
- A **MAJOR** release: three breaking API changes — see the Migration Guide in `RELEASE_NOTES.md` for what callers
  must change and the four migrations that run on upgrade.

## 📦 Key Changes

**Added**

- Optional `name` (CSV `Name`) and `competitorNumber` (CSV `Mem #`) on the match competitor request and patch models,
  with `CompetitorRepository.findByCompetitorNumber` and `findByFullNameIgnoreCase`
- Optional overall-score fields on the request, patch and response models, and matching `match_competitor` columns
- Four Flyway migrations, `V10_0_0` to `V10_3_0`

**Changed**

- **Breaking:** `matchPoints` renamed to `points` in the JSON contract and the table
- **Breaking:** CSV headers renamed — `MatchClub` → `Class`, `CompetitorCategory` → `Cats`, `Division` → `Div`,
  `PowerFactor` → `PF`, `MatchPoints` → `Pts`
- **Breaking:** `competitorCategory` is a single category rather than a list, on `MatchCompetitor` and
  `ShooterLogCompetitor`
- `competitorId` is no longer required when a `competitorNumber` or `name` is supplied
- Version bumped to 10.0.0 in `pom.xml` and `@OpenAPIDefinition`
- `CHANGELOG.md`, `HISTORY.md` and `EVOLUTION_OVERVIEW.md` archive versions 1.0.0 – 7.4.1 under `documentation/history/`

**Removed**

- The unused `MatchOverallScoresRequest`, `MatchOverallScoresRequestForCSV`, `MatchStageScoresRequest` and
  `MatchStageScoresRequestForCSV` models and their tests

## 🧪 Test Plan

- [x] `./mvnw test` — 1,101 tests, no failures, errors or skips
- [ ] `./mvnw verify -Pcoverage` — **to be run** on the release branch before merging
- [x] Flyway migrations applied cleanly to an empty MySQL 8.4 database, `V7_0_0` through `V10_3_0` (25 migrations)
- [ ] Qodana, CodeQL and Docker workflows pass on this PR
- [x] `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v10/RELEASE_NOTES_v10.0.0.md`

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
