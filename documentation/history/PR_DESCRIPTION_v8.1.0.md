# Pull Request – Version 8.1.0

**Base:** `develop` ← **Compare:** `release/v8.1.0`

---

## 🎯 Summary

- Extends the IPSC competitor module completed in v8.0.0 with bulk CSV import:
  `IpscCompetitorController.createCompetitors` (`POST /ipsc/competitors/bulk`) follows the same convention as
  `AwardController`/`ImageController`'s bulk endpoints, but actually persists each row.
- While building and testing that feature, discovers and fixes a Jackson gotcha affecting every
  `@JsonProperty(required = true)` field added across the IPSC request models to date: without a matching
  `@JsonCreator` constructor, the annotation is silently a no-op.
- `CompetitorRequestForCSV`, `CompetitorRequest`, `MatchRequest`, `MatchStageRequest` and the not-yet-wired
  `MatchOverallScoresRequest`/`MatchStageScoresRequest` (plus their CSV variants) all gain a `@JsonCreator`
  constructor so their required fields are genuinely enforced.
- Corrects a validation mismatch found along the way — `CompetitorRequest`'s Jackson-required field was
  `competitorNumber`, when `IpscCompetitorServiceImpl.validateForCreate` actually requires `clubNumber`.

## 📦 Key Changes

**Added**

- `IpscCompetitorController.createCompetitors` (`POST /ipsc/competitors/bulk`), backed by
  `IpscCompetitorService`/`IpscCompetitorServiceImpl.createCompetitors`
- `CompetitorRequestForCSV`, `CompetitorResponseHolder` models
- New unit tests across the bulk-import feature and every touched request model (`CompetitorRequestTest`,
  `CompetitorRequestForCSVTest`, `MatchRequestTest`, `MatchStageRequestTest`, `MatchOverallScoresRequestTest`,
  `MatchStageScoresRequestTest`, `MatchOverallScoresRequestForCSVTest`, `MatchStageScoresRequestForCSVTest`)

**Changed**

- `MatchRequest`, `MatchStageRequest`, `CompetitorRequestForCSV`, `CompetitorRequest`,
  `MatchOverallScoresRequest`/`ForCSV`, `MatchStageScoresRequest`/`ForCSV` — gained a `@JsonCreator` constructor with
  every parameter bound via `@JsonProperty`, replacing `@AllArgsConstructor`, so a missing required field now throws
  `MismatchedInputException` during parsing instead of silently deserialising as `null`
- `CompetitorRequest` — required field corrected from `competitorNumber` to `clubNumber`
- `CompetitorRequest`/`CompetitorRequestForCSV`/`MatchRequest` — explicit `@JsonFormat` date pattern on `LocalDate`
  fields
- `CompetitorResponse` — `@NotNull` documentation added to always-set fields
- `README.md`/`ARCHITECTURE.md` reverse-synced to describe the new bulk CSV import endpoint and data flow

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (746 tests)
- [x] New unit and integration coverage for `IpscCompetitorController`/`Service`/`ServiceImpl`'s bulk import
- [x] New unit tests for every touched request model's JSON/CSV (de)serialization and required-field enforcement
- [x] A throwaway `csvMapper.addMixIn(...)` scratch test (written, run, then discarded) confirmed the scores CSV
  variants are genuinely mixin-compatible with their plain counterparts
- [ ] Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  no controller wiring yet (see Known Issues in `RELEASE_NOTES.md`)

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)
