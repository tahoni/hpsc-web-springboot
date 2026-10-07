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

Phases 8 to 18 (versions 5.0.0 to 7.4.1) are archived, unchanged, in
[`documentation/archive/v5-v7/EVOLUTION_OVERVIEW_v5-v7.md`](/documentation/archive/v5-v7/EVOLUTION_OVERVIEW_v5-v7.md).

Phases 1 to 7 (versions 1.0.0 to 4.1.0) are archived, unchanged, in
[`documentation/archive/v1-v4/EVOLUTION_OVERVIEW_v1-v4.md`](/documentation/archive/v1-v4/EVOLUTION_OVERVIEW_v1-v4.md).

Phases 19 to 43 (versions 8.0.0 to 9.1.0) are archived, unchanged, in
[`documentation/archive/v8-v9/EVOLUTION_OVERVIEW_v8-v9.md`](/documentation/archive/v8-v9/EVOLUTION_OVERVIEW_v8-v9.md).

---

### Phase 44: PractiScore-Style Match Competitor Import (v10.0.0)

**Duration:** October 3, 2026

A major release: the match competitor model and CSV import are reshaped around a PractiScore results export. Match
competitors carry the export's overall scores, are found by membership number or name, and hold a single competitor
category again. Three changes are backward-incompatible, so it is MAJOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Match Competitor API**

- `MatchCompetitorRequest`, `MatchCompetitorPatchRequest` and `MatchCompetitorResponse` gain optional `percentage`,
  `time`, `percentageOfPossiblePoints`, `alpha`, `charlie`, `delta`, `misses`, `noPenaltyMisses`, `noShoots`,
  `proceduralErrors` and `additionalPenalties`, carried through `IpscMatchCompetitorServiceImpl` to and from
  `MatchCompetitor`
- **Breaking:** `matchPoints` is renamed `points` in the JSON contract
- **Breaking:** `MatchCompetitorRequestCsvMixIn`'s columns follow a PractiScore export — `MatchClub` → `Class`,
  `CompetitorCategory` → `Cats`, `Division` → `Div`, `PowerFactor` → `PF`, `MatchPoints` → `Pts`, plus `%`,
  `Time`, `% psbl`, `A`, `C`, `D`, `M`, `NPM`, `NS`, `Proc` and `Apen` for the overall scores; the controller's
  CSV example shows them
- **Breaking:** `competitorCategory` (CSV `Cats`) is a single category again, a JSON string rather than an array and one
  value per CSV cell rather than a `;`-separated list

**Competitor Resolution**

- `competitorId` is no longer required: a request carries it, a `competitorNumber` (CSV `Mem #`) or a `name` (CSV
  `Name`), and `IpscMatchCompetitorServiceImpl` resolves them in that order; the number is matched exactly against
  `Competitor.competitorNumber` and the name as "First Last", ignoring case
- New `CompetitorRepository.findByCompetitorNumber` and `findByFullNameIgnoreCase` return a list, so a number or name
  that matches no competitor, or more than one, is refused

**Persistence**

- `V10_0_0` renames `match_competitor.match_points` to `points`; `V10_1_0` adds the overall-score columns;
  `V10_2_0` returns `competitor_category` to a single column on `match_competitor` and `shooter_log_competitor`,
  dropping `match_competitor_category` and `shooter_log_competitor_category`, with a row that had several categories
  keeping the alphabetically first and one with none an empty category; `V10_3_0` drops the `hit_factor` column that
  `V10_1_0` had created
- Migrating an empty MySQL 8.4 database from `V7_0_0` found that `V10_2_0` placed `shooter_log_competitor`'s new column
  `AFTER match_id`, which `V8_3_0` had dropped, so the migration failed on every database; it now places the column
  after `match_competitor_id`, and all 25 migrations apply from empty

**Removals**

- `MatchOverallScoresRequest`, `MatchOverallScoresRequestForCSV`, `MatchStageScoresRequest` and
  `MatchStageScoresRequestForCSV` and their tests are deleted — they modelled scores for the removed stage tables and
  nothing referenced them; the overall-score fields now live on `MatchCompetitor`

**Build & Metadata**

- Project version bumped to 10.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Matching the import format to the source data, and resolving a record's owner by a natural key rather than a
  database id, refusing a missing or ambiguous match

**Test Coverage:**

- Updated request, patch request, CSV mix-in, service, implementation and integration tests for the renamed and new
  fields, the single category and competitor resolution; the deleted request models' tests are removed

### Phase 45: Competitor Contract Tightening & Competitor Lookup Service (v11.0.0)

**Duration:** October 4, 2026

A major release: the competitor contract is tightened and normalised, and a competitor lookup service is added. Every
bulk endpoint reads `text/plain`, the nickname is spelt `nickName` throughout and `competitorNumber` is a whole number.
Three groups of changes are backward-incompatible, so it is MAJOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Bulk Endpoints**

- **Breaking:** every `POST /bulk` endpoint — awards, images, competitors, matches and match competitors — consumes
  `text/plain` instead of `text/csv`, and answers `415` to any other content type; the request body is unchanged

**Competitor Nickname**

- **Breaking:** `nickname` is renamed `nickName` in the `Competitor` entity field and the JSON property, and the CSV
  column `Nickname` becomes `NickName`
- `V11_0_0` renames the `competitor.nickname` column to `nick_name`, keeping its type, nullability and values

**Competitor Number**

- **Breaking:** `Competitor.competitorNumber` is an `Integer`, held in an `INT` column by `V11_1_0`, which is refused
  when existing data is non-numeric
- **Breaking:** competitor and match competitor requests (JSON and CSV `CompetitorNumber`/`Mem #`) still send a string,
  but it must be a whole number and is refused otherwise; a blank means not supplied, and a blank in a patch leaves the
  stored number unchanged; `CompetitorResponse` returns a whole number

**Competitor Lookup Service**

- New `EntityIpscCompetitorService.findCompetitor(fullName, competitorNumber)` and its implementation resolve one
  `Competitor`: first by number, skipping the excluded ICS aliases 15000 and 16000, then by full name — "FirstName
  LastName" or "NickName LastName", ignoring case, trimmed first and with an RO suffix stripped — narrowed to the
  number matches; it is not yet wired into a caller
- New `MatchCompetitorResult` and `MatchCompetitorResultHolder` response models, unused so far

**Repository & Services**

- `CompetitorRepository`'s finders are renamed `findAll…`, and the new
  `findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase` backs the name lookup
- `IpscConstants.MAX_SAPSA_NUMBER` rises from 99,999 to 999,999 and `EXCLUDE_ICS_ALIAS` becomes a `List<Integer>`
- `IpscCompetitorServiceImpl.resolveCompetitorNumber` falls back to the SAPSA number
- Class-level `@since` tags are added

**Build & Metadata**

- Project version bumped to 11.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Giving the competitor one content type, one spelling and one numeric type, and refusing data that cannot be
  converted rather than guessing

**Test Coverage:**

- Three-tier tests for `EntityIpscCompetitorService` and its implementation, and updated request, response, CSV and
  repository tests for the renamed and retyped fields; the suite stands at 1,109 passing tests

### Phase 46: Match Competitor Lookup, Partial Bulk Import & Club Filtering (v12.0.0)

**Duration:** October 5, 2026

A major release: the competitor lookup built in v11.0.0 is wired into the match competitor service, the bulk import
becomes a partial import limited to one club, and a match no longer needs a category. Four groups of changes are
backward-incompatible, so it is MAJOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Competitor Lookup**

- `EntityIpscCompetitorService.findCompetitor(competitorNumber, fullName)` is called by `IpscMatchCompetitorServiceImpl`
  through `resolveCompetitor`: the number, which may also be an ID number, then the full name, narrowing several number
  matches by name, and an exception rather than an empty result when no single competitor matches
- **Breaking:** the shared alias numbers 15000 and 16000 are no longer matched by competitor number, and a number that
  is not a whole number is looked up as an ID number, answering `404` when nothing matches instead of a `400`
- The range officer marker `RO` or `(RO)` is removed from a name wherever it appears, and a competitor created or
  updated without a nickname takes its first name as the nickname
- New `CompetitorHelpers.getCompetitorNumberAsInteger` and `CompetitorRepository.findAllByIdNumber`

**Bulk Import**

- **Breaking:** `createMatchCompetitors` is a partial import — each row is saved on its own and a failed row is
  reported and skipped while the rest are created; an import in which every row fails answers `422`
- **Breaking:** `MatchCompetitorResult` and `MatchCompetitorResultHolder` are renamed `MatchCompetitorBulkResponse` and
  `MatchCompetitorBulkResponseHolder`, the body carries `matchCompetitors` instead of `matchCompetitorResults`, and
  `MatchCompetitorResponse` gains `competitorName` and `competitorNumber`
- **Breaking:** the import is limited to one club — HPSC's own club unless the optional `club` query parameter names
  another — and imports the rows whose match club, or whose competitor's home club, is that club, reporting the rest as
  skipped
- New `ClubService`, with `ClubServiceImpl`, holds the null-safe club comparisons, and
  `IpscCompetitorServiceImpl.isMemberOfHomeClub` takes the home club as a parameter, defaulting to HPSC

**Matches**

- The match category is optional: one that is null, empty or blank takes the default, Club Shoot, and a supplied one may
  be a display name or a constant name, ignoring case and surrounding whitespace

**Fixes**

- `ControllerResponse` derives `success` from its error the right way round
- `findCompetitor` no longer throws a `NumberFormatException` for a numeric value too long for an `int`

**Housekeeping**

- `DateUtil`, `NumberUtil`, `StringUtil` and `ValueUtil` are renamed back from their plural names, `StringUtil` gains
  `hasText`, and `IpscConstants` loses `MAX_SAPSA_NUMBER` and four unused score-scale constants and documents the rest
- Improvement plan Gaps #36 and #37 are closed, `qodana.yaml` is added and `mysql-connector-j` is pinned to 26.7.0

**Build & Metadata**

- Project version bumped to 12.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Putting the lookup to work, and making an import report each row instead of refusing the file

**Test Coverage:**

- Three-tier tests for the lookup wiring, the partial import, the club filter and `ClubService`; the suite stands at
  1,227 passing tests

### Phase 47: Mappers, Division–Firearm Type Consistency & Match Competitor Contract Tightening (v13.0.0)

**Duration:** October 7, 2026

A major release: the field copying and lookups leave the three IPSC services for mapper components, a division records
the firearm type it is shot with and has a name of its own, and the match competitor's contract tightens. Three groups
of changes are backward-incompatible, so it is MAJOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Mappers**

- New `CompetitorMapper`, `MatchMapper` and `MatchCompetitorMapper` hold `applyFields`, a new `applyPatchFields` and the
  gender, club, match, category, firearm type, division and power factor lookups that the services carried, so a service
  constructor takes a mapper in place of the repositories those lookups used
- `MatchCompetitorMapper` rejects a division that does not belong to the firearm type, through
  `validateDivisionMatchesFirearmType`, checking a patch against the other value's current one, and takes a missing
  firearm type from the division

**Enums**

- `Division` gains a `firearmType` and every division has a unique name: the shotgun, .22 and mini rifle divisions that
  shared a handgun name are renamed, the semi auto rifle divisions become `Rifle Open Division` and
  `Rifle Standard Division`, and `SHOTGUN_STANDARD_MANUAL` becomes `Shotgun Standard Manual Division`
- **Breaking:** the accepted division names change, and `SENIOR_LADY` is now `Lady Senior`, so a request using
  `Lady, Senior` is rejected
- The unused `code` and `abbreviation` fields and the lookups that only tests called are removed from `Division`,
  `FirearmType`, `PowerFactor` and `CompetitorCategory`, which gains an abbreviation instead of its code

**Database**

- `V11_2_0` makes `match_competitor.firearm_type` and `power_factor` `NOT NULL`, `V11_3_0` adds the optional
  `date_calculated` column, `V11_4_0` renames the stored divisions by firearm type and `V11_5_0` renames the stored
  category `Lady, Senior` to `Lady Senior`

**Models**

- **Breaking:** `MatchCompetitorResponse` replaces `competitorName` with a `competitorNames` list holding the
  competitor's "First Last" and "Nick Last" names once each
- **Breaking:** `powerFactor` is required on a match competitor request, as `firearmType` already was; `firearmType` is
  then made optional again, taken from the division when left out
- `MatchCompetitorRequest` and `MatchCompetitorPatchRequest` extend `IpscMatchScore`, `weightedPoints` is renamed
  `points` and the power factor leaves the score models
- `CompetitorRequest`, `MatchRequest` and `MatchCompetitorRequest` each carry their own `validate()`, and
  `CompetitorResponse`, `MatchResponse` and `MatchCompetitorResponse` build themselves from their entities

**Housekeeping**

- `NumberUtil.calculatePercentage` and `calculateSum` take a `scale` and the percentage rounding is fixed, null
  contracts are marked `@Nullable`, `.gitattributes` normalises text files to LF and `qodana.yaml` moves to the
  `qodana.recommended` profile
- Improvement plan Gap #38 is partially completed and Gap #39 is recorded

**Build & Metadata**

- Project version bumped to 13.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Separating mapping from orchestration, and making the match competitor's enumerated values agree with each other

**Test Coverage:**

- Unit tests for the three mappers, including `applyPatchFields`, the division and firearm type checks and the new
  enum behaviour, with the integration tests adjusted for the stricter requests; the suite stands at 1,265 passing
  tests

### Phase 48: Request-Body Validation, jspecify Nullness & Competitor Name Cleaning (v13.1.0)

**Duration:** October 7, 2026

A minor release: the IPSC controllers validate their request bodies and answer `400` for a violation, jspecify's
`@NonNull` replaces Jakarta's `@NotNull` for nullness, and a competitor's name is cleaned before it is matched. All of
it is backward-compatible, so it is MINOR under the Semantic Versioning rules.

**Key Accomplishments:**

**Validation**

- `IpscCompetitorController`, `IpscMatchController` and `IpscMatchCompetitorController` annotate their `@RequestBody`
  parameters with `@Valid`, and `CompetitorRequest`, `MatchRequest` and `MatchCompetitorRequest` gain `@NotBlank` and
  `@NotNull` constraints with the messages their `validate()` methods already used
- New `ControllerAdvice.handleMethodArgumentNotValidException` maps a violation to a `400 Bad Request` listing every
  violated constraint, instead of the generic `500`
- `MatchCompetitorRequest.validate()` returns `void` and throws on failure, as the other request models do

**Nullness**

- `org.jspecify.annotations.NonNull` replaces `jakarta.validation.constraints.NotNull` on the services', mappers' and
  response models' parameters and fields, with the explicit null checks unchanged, and `org.jspecify:jspecify` is
  declared as a direct dependency in `pom.xml`

**Competitor Names**

- New `CompetitorHelpers.cleanCompetitorName` removes a leading position of up to two digits followed by `-`, an `RO` or
  `(RO)` marker and all full stops, collapses whitespace and returns an empty string for a null name
- `MatchCompetitorMapper.applyFields` and `applyPatchFields` clean the request's competitor name before the lookup, and
  `EntityIpscCompetitorServiceImpl.findCompetitor` normalises the full name before matching by name

**Documentation**

- `flyway-migration-versioning.md`'s Current State table is re-aligned and a missing comma is added to `AGENTS.md`
- Improvement plan unchanged: 35 gaps closed, #6, #26 and #38 partially completed and #39 open

**Build & Metadata**

- Project version bumped to 13.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Rejecting an invalid request at the controller, and normalising the competitor name that the lookup matches on

**Test Coverage:**

- `CompetitorHelpersTest` covers `cleanCompetitorName` and `ControllerAdviceTest` covers the `400` handling of an
  invalid request body
- The suite stands at 1,277 passing tests

### Phase 49: Club Number Matching & Backtracking-Free Name Patterns (v13.2.0)

**Duration:** October 7, 2026

A small minor release: the competitor lookup tries the number as a club number first, and the name-cleaning patterns
stop backtracking. It is backward-compatible and needs no migration, so it is MINOR under the Semantic Versioning
rules.

**Key Accomplishments:**

**Competitor Lookup**

- `EntityIpscCompetitorServiceImpl.findCompetitor` matches the trimmed competitor number as a club number with
  `CompetitorRepository.findByClubNumber` before converting it to a number, and returns that competitor at once, so a
  numeric club number takes precedence over a competitor number with the same value; the competitor number, ID number
  and name lookups follow when no club number matches

**Name Patterns**

- `CompetitorHelpers.POSITION_PREFIX` is possessive and a leading position is now any number of digits followed by an
  optional whitespace character and a hyphen, at the very start of the name, so `123-John` cleans to `John` and
  `-12-John Smith` is left alone
- `WHITESPACE` matches only a run of two or more whitespace characters, possessively, so a single tab or newline between
  words is no longer turned into a space
- `MC_PREFIX` tests the start of the word with `lookingAt()` and a fixed pattern, with no change in behaviour

**Error Message**

- The null-list error in `AwardServiceImpl.mapAwards` said "Image request list", copied from the image service, and now
  says "Award request list"; the test asserts the message, and the parameter drops a `@NonNull` that its own null check
  contradicted
- `ImageResponse.setMimeType` resets a blank MIME type that cannot be inferred from the file name to an empty string
- Improvement plan unchanged: 35 gaps closed, #6, #26 and #38 partially completed and #39 open

**Documentation**

- The archived versions 1.0.0 – 9.1.0 move from `documentation/history/` to `documentation/archive/v1-v4/`, `v5-v7/` and
  `v8-v9/`, and `ARCHIVE.md` moves to `documentation/legacy/`, with the links in the documentation and the
  `prep-version-release` skill updated

**Build & Metadata**

- Project version bumped to 13.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

**Technical Focus:**

- Keeping the competitor lookup's precedence explicit, and removing backtracking from the patterns that clean a name

**Test Coverage:**

- Tests cover the club-number stage, the hardened name patterns and the `mapAwards` message
- The suite stands at 1,280 passing tests

---

**For the full project history, see [HISTORY.md](/HISTORY.md)**
