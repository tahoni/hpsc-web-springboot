# HPSC Website Backend History Archive (v10.0.0 – v13.3.0)

The per-version narrative history of versions 10.0.0 to 13.3.0 of the HPSC Website Backend project, archived from
[`HISTORY.md`](/HISTORY.md) to keep that file a manageable size. The entries are moved unchanged, grouped under
the `HISTORY.md` section each came from. See [`HISTORY.md`](/HISTORY.md) for the sections that span every
version (Feature Timeline, Key Learnings, Project Philosophy Evolution and Conclusion) and for versions 14.0.0 onwards,
[`CHANGELOG_v10-v13.md`](/documentation/archive/v10-v13/CHANGELOG_v10-v13.md) for the archived change log,
[`EVOLUTION_OVERVIEW_v10-v13.md`](/documentation/archive/v10-v13/EVOLUTION_OVERVIEW_v10-v13.md) for the archived
Phase-by-phase narrative, [`HISTORY_v8-v9.md`](/documentation/archive/v8-v9/HISTORY_v8-v9.md) for versions 8.0.0 to 9.1.0,
[`HISTORY_v5-v7.md`](/documentation/archive/v5-v7/HISTORY_v5-v7.md) for versions 5.0.0 to 7.4.1 and
[`HISTORY_v1-v4.md`](/documentation/archive/v1-v4/HISTORY_v1-v4.md) for versions 1.0.0 to 4.1.0.

---

## Table of Contents

- [📅 Historical Timeline](#-historical-timeline)
- [🎯 Major Milestones](#-major-milestones)
- [🏛️ Architectural Evolution](#-architectural-evolution)
- [🛤️ Future Roadmap Implications](#-future-roadmap-implications)

---

## 📅 Historical Timeline

### Version 13.3.0 (October 7, 2026)

**Theme:** NGPSA Membership Flag & Club Number Normalisation

**Key Focus:**

- New `Competitor.paidUpNgpsa` nullable `Boolean`, stored in a `paid_up_ngpsa` column that the
  `V11_6_0__add_competitor_paid_up_ngpsa.sql` migration adds after `paid_up_sapsa`, records whether a competitor's NGPSA
  membership is paid up
- `CompetitorRequest`, `CompetitorPatchRequest` and `CompetitorResponse` gain an optional `paidUpNgpsa` field, and the
  competitor CSV import a `PaidUpNgpsa` column; it is stored as `null` when omitted, and a patch leaves it unchanged
- `CompetitorMapper.resolveClubNumber` removes all spaces from a club number and trims it, so `applyFields` and
  `applyPatchFields` store a consistent value
- Scoped as `v13.3.0` **MINOR**: the new optional field and CSV column are additions, nothing is backward-incompatible
  and the migration adds a nullable column
- No improvement plan gaps are closed or progressed: 35 stay closed, #6, #26 and #38 stay partially completed and #39
  stays open
- Project version bumped to 13.3.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 13.2.0 (October 7, 2026)

**Theme:** Club Number Matching & Backtracking-Free Name Patterns

**Key Focus:**

- `EntityIpscCompetitorServiceImpl.findCompetitor` first matches the trimmed competitor number as a club number with
  `CompetitorRepository.findByClubNumber`, before converting it to a number, so a numeric club number takes precedence
  over a competitor number with the same value; the competitor number, ID number and name lookups follow when no club
  number matches
- `CompetitorHelpers` no longer backtracks: `POSITION_PREFIX` is possessive and now accepts any number of digits at the
  very start of the name, `WHITESPACE` matches only a run of two or more whitespace characters, possessively, and
  `MC_PREFIX` tests the start of the word with `lookingAt()` and a fixed pattern
- The `AwardServiceImpl.mapAwards` null error said "Image request list", copied from the image service, and now says
  "Award request list", and its parameter drops a `@NonNull` that its own null check contradicted
- `ImageResponse.setMimeType` resets a blank MIME type that cannot be inferred from the file name to an empty string,
  as a null one already was
- The archived versions 1.0.0 – 9.1.0 move from `documentation/history/` to `documentation/archive/v1-v4/`, `v5-v7/` and
  `v8-v9/`, and `ARCHIVE.md` moves to `documentation/legacy/`, with the links in the documentation and the
  `prep-version-release` skill updated
- Scoped as `v13.2.0` **MINOR**: the club-number stage is an addition, nothing is backward-incompatible and no migration
  is needed
- No improvement plan gaps are closed or progressed: 35 stay closed, #6, #26 and #38 stay partially completed and #39
  stays open
- The suite stands at 1,280 passing tests
- Project version bumped to 13.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 13.1.0 (October 7, 2026)

**Theme:** Request-Body Validation, jspecify Nullness & Competitor Name Cleaning

**Key Focus:**

- The three IPSC controllers annotate their `@RequestBody` parameters with `@Valid`, and the competitor, match and match
  competitor requests carry `@NotBlank` and `@NotNull` constraints with the messages `validate()` already used, so a
  request missing a required field is rejected on arrival; `ControllerAdvice.handleMethodArgumentNotValidException`
  answers `400 Bad Request` listing every violated constraint instead of the generic `500`
- `org.jspecify.annotations.NonNull` replaces `jakarta.validation.constraints.NotNull` on the services', mappers' and
  response models' parameters and fields, and `org.jspecify:jspecify` is declared as a direct dependency rather than
  received transitively
- `MatchCompetitorRequest.validate()` returns `void` and throws on failure, as the other request models do
- New `CompetitorHelpers.cleanCompetitorName` strips a leading position, an `RO` or `(RO)` marker and full stops and
  collapses whitespace; `MatchCompetitorMapper` cleans the request's name with it and
  `EntityIpscCompetitorServiceImpl.findCompetitor` normalises the full name, so a PractiScore-style
  `1 - John Smith (RO)` finds its competitor
- Documentation tweaks: `flyway-migration-versioning.md`'s Current State table is re-aligned and a missing comma is
  added to `AGENTS.md`
- Scoped as `v13.1.0` **MINOR**: the new `400` response and the broader name matching are externally visible additions,
  and nothing is backward-incompatible
- No improvement plan gaps are closed or progressed: 35 stay closed, #6, #26 and #38 stay partially completed and #39
  stays open
- The suite stands at 1,277 passing tests
- Project version bumped to 13.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 13.0.0 (October 7, 2026)

**Theme:** Mapper Extraction, Division–Firearm Type Consistency & Match Competitor Contract Tightening

**Key Focus:**

- The field copying and lookups that the three IPSC services carried move into new `CompetitorMapper`, `MatchMapper`
  and `MatchCompetitorMapper` components, each with a new `applyPatchFields`, so the services no longer need the
  repositories those lookups used and a request model can be copied onto an entity without one
- `Division` records the `FirearmType` it is shot with, and every division has a name of its own: the shotgun, .22 and
  mini rifle divisions that shared a handgun name are renamed, `V11_4_0` renames the stored values by firearm type, and
  `V11_5_0` renames the stored category `Lady, Senior` to `Lady Senior`
- A match competitor's division must belong to its firearm type — checked in `MatchCompetitorRequest.validate` and in
  `MatchCompetitorMapper` — and the firearm type may now be left out, in which case it is taken from the division
- **Breaking:** `MatchCompetitorResponse` replaces `competitorName` with a `competitorNames` list, so the JSON of the
  match competitor endpoints and the bulk import results changes shape
- **Breaking:** `powerFactor` is required on a match competitor request, as `firearmType` already was, and both columns
  are `NOT NULL` after `V11_2_0`; `firearmType` is then relaxed again, so a request may omit it
- **Breaking:** the accepted division names change with the renames above, and a request using the old `Lady, Senior`
  category name is rejected
- The match competitor's score fields move into `IpscMatchScore`, which `MatchCompetitorRequest` and
  `MatchCompetitorPatchRequest` now extend, `weightedPoints` is renamed `points` and the power factor leaves the score
  models
- `MatchCompetitor` gains an optional `date_calculated` column, the unused `code` and `abbreviation` fields and the
  lookups that only tests called are removed from the enums, and `CompetitorCategory` gains an abbreviation
- `CompetitorRequest`, `MatchRequest` and `MatchCompetitorRequest` each carry their own `validate()`, and null contracts
  are marked with `@Nullable`
- Improvement plan Gap #38 is partially completed and Gap #39 is recorded; `.gitattributes` normalises text files to LF
  and `qodana.yaml` moves to the `qodana.recommended` profile
- Scoped as `v13.0.0` **MAJOR**: the response shape, the required power factor and the renamed division and category
  names are backward-incompatible and flagged `**Breaking:**` in `CHANGELOG.md`
- Project version bumped to 13.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`
- The suite stands at 1,265 passing tests

### Version 12.0.0 (October 5, 2026)

**Theme:** Match Competitor Lookup, Partial Bulk Import & Club Filtering

**Key Focus:**

- `EntityIpscCompetitorService.findCompetitor(competitorNumber, fullName)` is wired into the match competitor service:
  the number is a string that may also be an ID number, the lookup tries the competitor number, the ID number and then
  the full name, and a lookup that does not find exactly one competitor throws instead of returning an empty result
- **Breaking:** the shared alias numbers 15000 and 16000 no longer match a competitor by number, and a number that is
  not a whole number is looked up as an ID number, so an unmatched one answers `404` instead of being refused up front
  with a `400`
- **Breaking:** the match competitor bulk import is a partial import — each row is saved on its own, and a row that
  fails is reported and skipped while the rest are created; an import in which every row fails answers `422`
- **Breaking:** the bulk response models are renamed `MatchCompetitorBulkResponse` and
  `MatchCompetitorBulkResponseHolder`, and the response body carries `matchCompetitors` instead of
  `matchCompetitorResults`, with each result naming its competitor by `competitorName` and `competitorNumber`
- **Breaking:** the bulk import is limited to one club, HPSC's own club unless an optional `club` query parameter asks
  for another: only the rows whose match club, or whose competitor's home club, is that club are created, and the rest
  are reported as skipped; a new `ClubService` holds the null-safe club comparisons
- A competitor created or updated without a nickname takes its first name as the nickname, and the range officer marker
  `RO` or `(RO)` is removed from a name wherever it appears when a competitor is looked up by name
- A match no longer needs a match category: one that is omitted takes the default, Club Shoot, and a supplied one may be
  given by display name or constant name, ignoring case and surrounding whitespace
- `ControllerResponse` derives `success` from its error the right way round, and `findCompetitor` no longer fails on a
  numeric value too long for an `int`
- The `*Utils` classes are renamed back to `DateUtil`, `NumberUtil`, `StringUtil` and `ValueUtil`, `StringUtil` gains
  `hasText` and the unused `MAX_SAPSA_NUMBER` and four score-scale constants are removed from `IpscConstants`, whose
  remaining members are documented
- Improvement plan Gaps #36 and #37 are closed, and a Qodana configuration and a `mysql-connector-j` pin are added
- Scoped as `v12.0.0` **MAJOR**: four groups of changes are backward-incompatible and flagged `**Breaking:**` in
  `CHANGELOG.md` — the alias numbers and ID-number lookup, the partial bulk import, the renamed response field and
  the HPSC-limited bulk import
- Project version bumped to 12.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`
- The suite stands at 1,227 passing tests

### Version 11.0.0 (October 4, 2026)

**Theme:** Competitor Contract Tightening & Competitor Lookup Service

**Key Focus:**

- Every `POST /bulk` endpoint — awards, images, competitors, matches and match competitors — now consumes `text/plain`
  rather than `text/csv`, and answers `415` to anything else; the body is unchanged
- The competitor's nickname is renamed `nickname` → `nickName` in the entity field, the JSON property and the CSV
  column (`Nickname` → `NickName`), and the `nickname` column becomes `nick_name` through the `V11_0_0` migration
- `competitorNumber` becomes a whole number: `Competitor.competitorNumber` is an `Integer` held in an `INT` column
  through `V11_1_0`, which is refused when existing data is non-numeric; competitor and match competitor requests (JSON
  and CSV `CompetitorNumber`/`Mem #`) still send a string, but it must be a whole number, a blank means not supplied
  and a blank in a patch leaves the stored number unchanged, and `CompetitorResponse` returns a whole number
- New `EntityIpscCompetitorService.findCompetitor(fullName, competitorNumber)` resolves one competitor by number,
  skipping the excluded ICS aliases 15000 and 16000, and then by full name — "FirstName LastName" or
  "NickName LastName", ignoring case, with an RO suffix stripped — narrowed to the number matches; it is not yet wired
  into a caller
- New `MatchCompetitorResult` and `MatchCompetitorResultHolder` response models, unused so far
- `CompetitorRepository`'s finders are renamed `findAll…` and gain
  `findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase`; `IpscConstants.MAX_SAPSA_NUMBER` rises from 99,999 to
  999,999 and `EXCLUDE_ICS_ALIAS` becomes a `List<Integer>`; `IpscCompetitorServiceImpl.resolveCompetitorNumber`
  falls back to the SAPSA number
- Three-tier tests for the new service take the suite to 1,109 passing tests
- Scoped as `v11.0.0` **MAJOR**: three groups of changes are backward-incompatible and flagged `**Breaking:**` in
  `CHANGELOG.md` — the `text/plain` bulk endpoints, the `nickName` rename and the whole-number `competitorNumber`
- Project version bumped to 11.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

### Version 10.0.0 (October 3, 2026)

**Theme:** PractiScore-Style Match Competitor Import & Overall Scores

**Key Focus:**

- `MatchCompetitor` gains the overall-score columns a PractiScore export carries — `percentage`, `time`,
  `percentage_of_possible_points`, the `alpha`, `charlie` and `delta` hit counts, `misses`, `no_penalty_misses`,
  `no_shoots`, `procedural_errors` and `additional_penalties` — as optional fields on `MatchCompetitorRequest`,
  `MatchCompetitorPatchRequest` and `MatchCompetitorResponse`, and `matchPoints` is renamed `points` in the JSON
  contract and the table
- The match competitor CSV import takes a PractiScore export's headers (`Class`, `Cats`, `Div`, `PF`, `Pts`, `%`,
  `Time`, `% psbl`, `A`, `C`, `D`, `M`, `NPM`, `NS`, `Proc`, `Apen`), so a results export can be imported without
  renaming its columns
- `competitorId` is no longer required: a request can identify its competitor by `competitorNumber` (CSV `Mem #`),
  matched exactly, or by `name` (CSV `Name`), matched as "First Last" ignoring case, through new
  `CompetitorRepository.findByCompetitorNumber` and `findByFullNameIgnoreCase` finders; a number or name that matches no
  competitor, or several, is refused
- `competitorCategory` returns to a single category in a `competitor_category` column on `MatchCompetitor` and
  `ShooterLogCompetitor`, replacing the list and the two child tables that v9.1.0 introduced; `V10_2_0` collapses
  existing rows to their alphabetically first category
- The unused `ipsc.scores.request` package — `MatchOverallScoresRequest`, `MatchStageScoresRequest` and their CSV
  forms, which modelled the removed stage tables — is deleted, its overall-score fields now living on
  `MatchCompetitor`
- Four Flyway migrations, `V10_0_0` to `V10_3_0`; running them against an empty MySQL 8.4 database exposed that
  `V10_2_0` placed a column `AFTER` one that `V8_3_0` had already dropped, so it failed on every database, and it now
  places the column after `match_competitor_id`
- The release's improvement-plan sweep recorded Gaps #33 and #34 — the Flyway versioning document's table and
  `ARCHITECTURE.md`'s deleted DTO package — and closed both, leaving no open gap; Gap #6 stays partially completed
- Scoped as `v10.0.0` **MAJOR**: three changes are backward-incompatible and flagged `**Breaking:**` in `CHANGELOG.md`
  — `matchPoints` renamed to `points`, the renamed CSV headers, and `competitorCategory` a single value rather than a
  list
- Project version bumped to 10.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation in `HpscWebApplication.java`

---

## 🎯 Major Milestones

### Milestone 44: PractiScore-Style Match Competitor Import (v10.0.0)

- A PractiScore results export imports into the match competitor endpoints without renaming its columns, and carries
  its overall scores — percentage, time, hit counts, misses and penalties — onto `MatchCompetitor`
- A row no longer needs a database id: the competitor is found by membership number or by name, and an ambiguous or
  unknown match is refused rather than guessed
- `competitorCategory` goes back to a single column, undoing the child tables v9.1.0 added once a PractiScore row was
  seen to carry one category
- Project's second MAJOR release in a row, with three breaking changes flagged `**Breaking:**` in `CHANGELOG.md` as
  they landed

**Achievement:** Aligned the match competitor model and CSV import to the format the data actually arrives in, and
proved the full migration chain by running it against an empty database, which caught a migration that could never
have run.

### Milestone 45: Competitor Contract Tightening & Lookup Service (v11.0.0)

- Every bulk endpoint declares the one content type it reads, `text/plain`, so a client sending `text/csv` is told so
  with a `415` rather than silently accepted
- The competitor's nickname is spelt `nickName` everywhere — entity, JSON, CSV and column — and `competitorNumber` is
  a whole number in the entity and the table, while requests still send it as a string that must parse as one
- A competitor can be found from a name and a number together: number first, then name, with the ICS aliases skipped
  and a nickname accepted in place of a first name, ready for a caller to use
- Project's third MAJOR release in a row, with three groups of breaking changes flagged `**Breaking:**` in
  `CHANGELOG.md` as they landed

**Achievement:** Settled the competitor's contract — one content type, one spelling and one type for the membership
number — and built the lookup that depends on it, with a migration that refuses data it cannot convert rather than
losing it.

### Milestone 46: Match Competitor Lookup, Partial Import & Club Filtering (v12.0.0)

- A match competitor request can name its competitor by number, ID number or name, and the lookup that finds it is the
  one `EntityIpscCompetitorService` built in the previous release, now with a caller
- The bulk import reports each row on its own, so one bad row no longer costs the whole file, and it imports one club's
  rows, HPSC's unless another is asked for
- A match no longer has to carry a category, and the club comparisons live in one `ClubService` instead of being
  repeated
- Project's fourth MAJOR release in a row, with four groups of breaking changes flagged `**Breaking:**` in
  `CHANGELOG.md` as they landed

**Achievement:** Put the competitor lookup to work and made the bulk import tolerant — a row is created, skipped or
failed, and said so — while narrowing it to the club the data is for.

### Milestone 47: Mappers, Division–Firearm Type Consistency & Match Competitor Contract Tightening (v13.0.0)

- The services stop doing their own field copying and lookups: three mapper components own them, and each gained the
  partial-update logic a patch needs
- A division knows its firearm type and has a name of its own, so a match competitor can be checked for a division that
  does not belong to its firearm type, and a division can be read back from its stored name alone
- A match competitor response lists its competitor's names once each, a request must carry a power factor, and a firearm
  type left out is taken from the division
- Project's fifth MAJOR release in a row, with the breaking changes flagged `**Breaking:**` in `CHANGELOG.md` as they
  landed

**Achievement:** Separated mapping from orchestration and made the match competitor's enumerated values agree with each
other — a division belongs to a firearm type, and each name identifies one division — with migrations that rewrite the
stored values to match.

### Milestone 48: Request-Body Validation, jspecify Nullness & Competitor Name Cleaning (v13.1.0)

- A request body missing a required field is now refused with a `400` by Bean Validation before it reaches the service,
  instead of surfacing as a `500`
- Nullness is expressed with jspecify's `@NonNull`, a direct dependency, in place of Jakarta's `@NotNull`, which only
  looked like a validation constraint on parameters it never validated
- A competitor name carrying a leading position, an `RO` marker or full stops now finds its competitor
- The first MINOR release since v9.1.0, and the end of the run of five consecutive MAJOR releases

### Milestone 49: Club Number Matching & Backtracking-Free Name Patterns (v13.2.0)

- A numeric club number now finds its competitor ahead of a competitor number with the same value
- The competitor name patterns are possessive or fixed, so none of them backtracks, and a leading position may be any
  number of digits
- The award list's null error now names the right request list, and a blank image MIME type is reset to an empty string
- The archived v1 – v9 history moves to `documentation/archive/v1-v4/`, `v5-v7/` and `v8-v9/`

### Milestone 50: NGPSA Membership Flag & Club Number Normalisation (v13.3.0)

- A competitor records whether their NGPSA membership is paid up, through the request, patch, response and CSV import
- A club number loses its spaces and surrounding whitespace before it is stored
- `V11_6_0__add_competitor_paid_up_ngpsa.sql` adds the nullable column

---

## 🏛️ Architectural Evolution

### v10.0.0: PractiScore-Style Match Competitor Import

```
PractiScore CSV ──(CsvMapper + MatchCompetitorRequestCsvMixIn)──→ MatchCompetitorRequest
                                                                       ↓
                 IpscMatchCompetitorServiceImpl: competitorId → competitorNumber → name
                                                                       ↓
                                   CompetitorRepository.findByCompetitorNumber / findByFullNameIgnoreCase
```

**Characteristics:**

- A request identifies its competitor in priority order — id, then number, then name — and the service refuses a
  number or name that matches none or several, so an import never attaches a result to a guessed competitor
- The CSV mix-in's column names follow a PractiScore export, so the import format is the export format; the JSON field
  names stay descriptive (`points`, `percentageOfPossiblePoints`)
- `MatchCompetitor` holds its overall scores as optional columns, and `competitorCategory` is a single value again on
  both `MatchCompetitor` and `ShooterLogCompetitor`, with no child tables

### v11.0.0: Typed Competitor Number & Competitor Lookup Service

```
request (competitorNumber: String, "NickName"/"FirstName" + LastName)
                          ↓  whole-number check — refused if not an integer, blank = not supplied
Competitor.competitorNumber (Integer, INT column)
                          ↓
EntityIpscCompetitorService.findCompetitor(fullName, competitorNumber)
        number (ICS aliases 15000/16000 skipped) → full name, narrowed to the number matches
                          ↓
CompetitorRepository.findAll… / findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase
```

**Characteristics:**

- The request models keep `competitorNumber` a string, so the JSON and CSV contract reads the same, and the whole-number
  rule is enforced by the service when it reads the request; only the entity, the table and `CompetitorResponse` hold a
  number
- Number resolution comes before name resolution, and the name only narrows the number's matches, so a name cannot
  select a different competitor than the number does
- The lookup sits behind the entity service layer and is not yet called, so the competitor and match competitor
  services still resolve competitors as v10.0.0 did
- Every bulk endpoint reads `text/plain`, so the content type is the same for all five, whatever the resource

### v12.0.0: Partial Bulk Import & Club-Filtered Match Competitors

```
POST /ipsc/match-competitors/bulk?club=...  (CSV as text/plain)
                          ↓
IpscMatchCompetitorServiceImpl.createMatchCompetitors(csvData, club)
        club → target club (HPSC's own club by default)
                          ↓  for each row, on its own
isForClub: matchClub is the target club, else the competitor's home club is  (ClubService.isSameClub)
                          ↓                       ↓ no → "Skipped" result
validateForCreate → applyFields → resolveCompetitor
        competitor ID, else EntityIpscCompetitorService.findCompetitor(number, fullName)
                          ↓
save (own transaction)  →  MatchCompetitorBulkResponse(success, message, row)
                          ↓
MatchCompetitorBulkResponseHolder (201, or 422 if every row failed)
```

**Characteristics:**

- Each row is its own unit of work, so a row that fails or is skipped is reported with its reason and the rest are
  still created; only unreadable CSV or a missing header column fails the whole request
- The club filter runs before a row is validated, so a row for another club is skipped rather than failed, while a row
  for the club that is invalid is still reported as failed
- Club comparisons go through `ClubService`, so `IpscCompetitorServiceImpl` and the match competitor service no longer
  each repeat the null checks
- A match's category and club both default when omitted, so a match request needs only a name, a date and a firearm
  type

---

## 🛤️ Future Roadmap Implications

### Previously Completed (v13.3.0)

- New `Competitor.paidUpNgpsa` with the `V11_6_0__add_competitor_paid_up_ngpsa.sql` migration, carried through
  `CompetitorRequest`, `CompetitorPatchRequest`, `CompetitorResponse`, the competitor CSV import and `CompetitorMapper`
- `CompetitorMapper.resolveClubNumber` removes all spaces from a club number and trims it
- Improvement plan unchanged: 35 gaps closed, #6, #26 and #38 partially completed and #39 open
- Project version bumped to 13.3.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v13.2.0)

- `EntityIpscCompetitorServiceImpl.findCompetitor` matches the competitor number as a club number first, with
  `CompetitorRepository.findByClubNumber`
- `CompetitorHelpers`' `POSITION_PREFIX`, `WHITESPACE` and `MC_PREFIX` patterns no longer backtrack
- `AwardServiceImpl.mapAwards` names the "Award request list" in its null error
- Improvement plan unchanged: 35 gaps closed, #6, #26 and #38 partially completed and #39 open
- The suite stands at 1,280 passing tests
- Project version bumped to 13.2.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v13.1.0)

- `@Valid` on the three IPSC controllers' request bodies, with Bean Validation constraints on the requests and
  `ControllerAdvice.handleMethodArgumentNotValidException` answering `400 Bad Request` for a violation
- `org.jspecify.annotations.NonNull` replaces `jakarta.validation.constraints.NotNull` and `org.jspecify:jspecify` is a
  direct dependency; `MatchCompetitorRequest.validate()` returns `void`
- New `CompetitorHelpers.cleanCompetitorName`, used by `MatchCompetitorMapper` and by
  `EntityIpscCompetitorServiceImpl.findCompetitor`
- Improvement plan unchanged: 35 gaps closed, #6, #26 and #38 partially completed and #39 open
- Project version bumped to 13.1.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v13.0.0)

- New `CompetitorMapper`, `MatchMapper` and `MatchCompetitorMapper` components holding the services' field copying and
  lookups, each with an `applyPatchFields`, documented in `ARCHITECTURE.md`
- `Division` gains a `FirearmType` and unique names, with `V11_4_0` renaming the stored divisions and `V11_5_0` renaming
  the stored category `Lady, Senior` to `Lady Senior`
- A match competitor's division is checked against its firearm type, and a missing firearm type is taken from the
  division
- `MatchCompetitorResponse.competitorNames` replaces `competitorName`, `powerFactor` is required and `firearmType` and
  `powerFactor` are `NOT NULL` columns, and `MatchCompetitor` gains an optional `date_calculated` column
- Score fields moved into `IpscMatchScore`, `weightedPoints` renamed `points`, and the unused `code`, `abbreviation`
  and lookup methods removed from the enums
- Improvement plan Gap #38 partially completed and Gap #39 recorded, `.gitattributes` normalised to LF and `qodana.yaml`
  moved to the `qodana.recommended` profile
- The suite stands at 1,265 passing tests
- Project version bumped to 13.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v12.0.0)

- `EntityIpscCompetitorService.findCompetitor(competitorNumber, fullName)` wired into the match competitor service,
  matching the number, then the ID number, then the full name, with the alias numbers 15000 and 16000 no longer matched
- Match competitor bulk import made a partial import with per-row results, the response models renamed
  `MatchCompetitorBulkResponse` and `MatchCompetitorBulkResponseHolder` and an import in which every row fails answering
  `422`
- Bulk import limited to one club, HPSC's unless a `club` parameter asks for another, with a new `ClubService`
- Competitor nickname defaulting to the first name, the RO marker removed from a name wherever it appears, an optional
  match category and `ControllerResponse.success` fixed
- `*Util` classes renamed back to the singular, `StringUtil.hasText` added and `IpscConstants` documented and pruned
- Improvement plan Gaps #36 and #37 closed, a Qodana configuration added and `mysql-connector-j` pinned
- The suite stands at 1,227 passing tests
- Project version bumped to 12.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v11.0.0)

- Every `POST /bulk` endpoint consumes `text/plain` instead of `text/csv`, answering `415` otherwise
- Competitor `nickname` renamed `nickName` in the entity, the JSON property and the CSV column (`NickName`), with the
  column renamed `nick_name` by `V11_0_0`
- `Competitor.competitorNumber` an `Integer` in an `INT` column by `V11_1_0`, refused when existing data is
  non-numeric; requests still send a string that must be a whole number, and `CompetitorResponse` returns one
- New `EntityIpscCompetitorService.findCompetitor`, resolving a competitor by number and then by full name, not yet
  wired into a caller, and the unused `MatchCompetitorResult` and `MatchCompetitorResultHolder` response models
- `CompetitorRepository` finders renamed `findAll…` with the new
  `findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase`; `IpscConstants.MAX_SAPSA_NUMBER` raised to 999,999 and
  `EXCLUDE_ICS_ALIAS` a `List<Integer>`; `resolveCompetitorNumber` falls back to the SAPSA number
- Three-tier tests for the new service, taking the suite to 1,109 passing tests
- Project version bumped to 11.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation

### Previously Completed (v10.0.0)

- `MatchCompetitor` gains the overall-score columns and `MatchCompetitorRequest`/`MatchCompetitorPatchRequest`/
  `MatchCompetitorResponse` the matching optional fields, with `matchPoints` renamed `points`, by `V10_0_0` and
  `V10_1_0`
- `MatchCompetitorRequestCsvMixIn` takes a PractiScore export's headers, and the controller's CSV example shows them
- `competitorId` optional: `competitorNumber` and `name` resolve the competitor through the new
  `CompetitorRepository.findByCompetitorNumber` and `findByFullNameIgnoreCase`
- `competitorCategory` a single value again on `MatchCompetitor` and `ShooterLogCompetitor`, replacing the child tables
  by `V10_2_0`, whose column placement is fixed so the migration chain runs from an empty database
- `MatchOverallScoresRequest`, `MatchOverallScoresRequestForCSV`, `MatchStageScoresRequest` and
  `MatchStageScoresRequestForCSV` and their tests deleted
- Project version bumped to 10.0.0 in `pom.xml` and the `@OpenAPIDefinition` annotation
