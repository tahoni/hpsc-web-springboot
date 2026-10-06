# HPSC Website Backend

## 🧾 Change Log

All notable changes to the HPSC Website Backend project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) as of version 5.0.0.

---

### Table of Contents

- [🧪 Unreleased](#-unreleased)
- [🧾 Version 12.0.0](#-1200---2026-10-05) ← Current
- [🧾 Version 11.0.0](#-1100---2026-10-04)
- [🧾 Version 10.0.0](#-1000---2026-10-03)
- [🧾 Version 9.1.0](#-910---2026-10-03)
- [🧾 Version 9.0.0](#-900---2026-10-01)
- [🧾 Version 8.12.0](#-8120---2026-09-29)
- [🧾 Version 8.11.1](#-8111---2026-09-27)
- [🧾 Version 8.11.0](#-8110---2026-09-27)
- [🧾 Version 8.10.2](#-8102---2026-09-26)
- [🧾 Version 8.10.1](#-8101---2026-09-26)
- [🧾 Version 8.10.0](#-8100---2026-09-26)
- [🧾 Version 8.9.0](#-890---2026-09-26)
- [🧾 Version 8.8.0](#-880---2026-09-24)
- [🧾 Version 8.7.0](#-870---2026-09-24)
- [🧾 Version 8.6.2](#-862---2026-09-24)
- [🧾 Version 8.6.1](#-861---2026-09-23)
- [🧾 Version 8.6.0](#-860---2026-09-23)
- [🧾 Version 8.5.1](#-851---2026-09-13)
- [🧾 Version 8.5.0](#-850---2026-09-04)
- [🧾 Version 8.4.2](#-842---2026-09-04)
- [🧾 Version 8.4.1](#-841---2026-09-04)
- [🧾 Version 8.4.0](#-840---2026-09-03)
- [🧾 Version 8.3.1](#-831---2026-09-02)
- [🧾 Version 8.3.0](#-830---2026-09-02)
- [🧾 Version 8.2.0](#-820---2026-09-01)
- [🧾 Version 8.1.1](#-811---2026-09-01)
- [🧾 Version 8.1.0](#-810---2026-09-01)
- [🧾 Version 8.0.0](#-800---2026-08-31)
- [🗄️ Archived Versions 1.0.0 – 7.4.1](/documentation/history/CHANGELOG_v1-v7.md)
- [📋 Version Policy](#-version-policy)
- [🚀 Upgrade Guide](#-upgrade-guide)
- [🤝 Contributing](#-contributing)
- [💬 Support](#-support)

---

### 🧪 [Unreleased]

#### ➕ Added

##### Documentation

- **`documentation/roadmap/improvement-plan.md`, `improvement-plan-tasks.md`:** New open Gap #38 — eight utility and
  enum methods and `SystemConstants.DEFAULT_SCALE` are used only by tests, with a matching task block, Roadmap row and
  Success Criteria entry

#### 🔄 Changed

##### Build & Configuration

- **`qodana.yaml`:** Switched from the `qodana.starter` to the `qodana.recommended` profile — adds the `LicenseAudit`
  inspection with `licenseRules` that allow permissive licences (Apache-2.0, MIT, BSD, EPL-2.0, ISC) and prohibit GPL
  and AGPL dependencies, plus further Java best-practice inspections alongside `JavadocReference`

##### Constants

- **`SystemConstants`:** Now `final`, as it is a utility class with a private constructor

##### Controllers

- **`IpscMatchCompetitorController.createMatchCompetitors`:** Now uses `HttpStatus.UNPROCESSABLE_CONTENT` instead of the
  deprecated `UNPROCESSABLE_ENTITY` when every row fails — the response is still `422`, so the API is unchanged

##### Converters

- **`ClubIdentifierConverter`, `FirearmTypeConverter`:** Conversion methods that can return `null` are now annotated
  `@Nullable` (JSpecify), so the null contract is explicit

##### Database

- **`V11_2_0__make_match_competitor_firearm_type_and_power_factor_required.sql`:** `match_competitor.firearm_type` and
  `power_factor` become `NOT NULL`, reversing `V8_1_0`'s `firearm_type` change and restoring one entry per competitor,
  match and firearm type; the migration is refused, leaving the columns as they were, if any existing row has a `NULL`
  in either — backfill those rows first
- **`V11_3_0__add_match_competitor_date_calculated.sql`:** Adds the nullable `match_competitor.date_calculated`
  column after `is_visitor`, so it sits before `date_created`; existing rows stay `NULL`

##### Domain

- **`MatchCompetitor`:** `firearmType` and `powerFactor` are now `nullable = false`
- **`MatchCompetitor.dateCalculated`:** New optional `date_calculated` column, placed before `dateCreated`, recording
  when the row's scores were calculated — as `ShooterLogCompetitor` and `ShooterLogOverall` already do

##### Helpers

- **`CompetitorHelpers.toSentenceCaseLastName`:** A null last name now returns an empty string instead of `null`;
  Javadoc and unit test updated

##### Models

- **`CompetitorResponse`, `MatchResponse`:** Each gains a constructor that builds the response from its entity, and
  its Javadoc now states which fields may be null
- **`MatchCompetitorResponse`:** **Breaking:** The `competitorName` string is replaced by a `competitorNames` list
  holding the competitor's "First Last" and "Nick Last" names once each (a null or duplicate nickname is skipped), so
  the JSON of `/ipsc/match-competitors` responses and bulk import results changes shape; also gains a constructor that
  builds it from a `MatchCompetitor`, and its Javadoc now covers the partly set response for a failed bulk import row

- **`CompetitorRequest.validate`, `MatchRequest.validate`:** Now documented in Javadoc, and unit tests added for them
  and for `MatchCompetitorRequest.validate`
- **`MatchCompetitorRequest.validate`:** New method, with Javadoc, holding the required-field checks that
  `IpscMatchCompetitorServiceImpl.validateForCreate` repeated inline; `validateForCreate` now calls it
- **`MatchCompetitorRequest`, `MatchCompetitorResponse`:** **Breaking:** `powerFactor` is now required, as
  `firearmType` already was — `validate()` rejects a request without one ("Power factor is required."), so bulk import
  rows and API calls that omit it now fail; `firearmType` and `powerFactor` are `@NotNull` on the response

##### Services

- **`IpscCompetitorServiceImpl`, `IpscMatchServiceImpl`:** Map entities with the new response constructors instead of
  long all-args calls
- **`IpscCompetitorServiceImpl`, `IpscMatchServiceImpl`:** `validateForCreate` calls `CompetitorRequest.validate` and
  `MatchRequest.validate` instead of repeating the required-field checks inline; the rules and messages are unchanged
- **`IpscMatchCompetitorServiceImpl`:** `patchMatchCompetitor` ignores a blank `powerFactor` instead of clearing the
  power factor
- **`IpscMatchCompetitorServiceImpl`:** `toResponse` uses the new constructor, and `toFailedResponse` wraps the
  requested name in a list, or leaves it empty when the request has none
- **`IpscCompetitorServiceImpl`:** `resolveClubNumber`, `resolveGender`, `resolveHomeClub` and `parseCompetitorNumber`
  are now annotated `@Nullable`, so the null contract is explicit
- **`IpscMatchCompetitorServiceImpl`:** `resolveCompetitorHomeClub`, `resolveMatchClub` and `resolvePowerFactor` are now
  annotated `@Nullable`, so the null contract is explicit

##### Utils

- **`NumberUtil`:** `calculatePercentage` and `calculateSum` now take a `scale` parameter instead of always using
  `SystemConstants.DEFAULT_SCALE`, so callers choose the result's decimal places; Javadoc updated and unit tests added
  for non-default scales
- **`StringUtil`:** `toString` and `toProperCase` are now annotated `@Nullable`, as both return `null` for a null input

#### 🐛 Fixed

##### Utils

- **`NumberUtil.calculatePercentage`:** The intermediate division now uses `scale + 2` digits instead of `scale * 2` —
  a scale below 2 rounded the ratio too early (2/3 at scale 0 gave `100` instead of `67`); results at the default scale
  are unchanged

---

### 🧾 [12.0.0] - 2026-10-05

#### ➕ Added

##### Build & Configuration

- **`qodana.yaml`:** New Qodana configuration for the `code_quality.yml` workflow — the `qodana.starter` profile plus
  the `JavadocReference` inspection, so broken `{@link}` and `@see` references are reported

##### Helpers

- **`CompetitorHelpers.getCompetitorNumberAsInteger`:** New helper that converts a competitor number to an `int` —
  surrounding whitespace is ignored, and a number that is null, blank or not a whole number, or one of the shared
  alias numbers `15000` and `16000`, converts to `0` meaning "no competitor number"; includes Javadoc and unit tests

##### Repositories

- **`CompetitorRepository.findAllByIdNumber`:** New derived query returning every competitor with a given ID number,
  used by `findCompetitor` to match a supplied value against ID numbers

##### Services

- **`IpscMatchCompetitorService.createMatchCompetitors(String, String)`:** **Breaking:** the bulk import is now limited
  to one club — HPSC's own club unless an optional `club` (name or abbreviation) asks for another — and imports only
  the rows whose `matchClub` is that club, or whose competitor's home club is. Any other row, including one whose
  competitor can't be resolved, is reported as skipped rather than created, so there is still one result per CSV row,
  and an unknown club answers a `ValidationException`. A row that used to be created for another club, or for a
  competitor with no home club, is now skipped unless the matching `club` is given. The one-argument method imports
  HPSC's rows, and `IpscMatchCompetitorServiceImpl.isForClub` does the per-row check through `ClubService`; includes
  Javadoc and unit and integration tests
- **`ClubService`:** New service, with `ClubServiceImpl`, holding the null-safe club comparisons — `isSameClub(Club,
  ClubIdentifier)` returns `true` only when a club's identifier is the given `ClubIdentifier`, and
  `isSameClub(ClubIdentifier, ClubIdentifier)` only when a non-null identifier is the target; `IpscCompetitorServiceImpl`
  takes it as a constructor dependency and its `isMemberOfHomeClub` now takes just the club and compares it with the
  home club through it; includes Javadoc and unit and integration tests

##### Utils

- **`StringUtil.hasText`:** New helper that returns `true` only when a string is neither null nor blank, replacing the
  private copy in `IpscMatchCompetitorServiceImpl` that checks the competitor number and name on a request; includes
  Javadoc and unit tests

#### 🔄 Changed

##### Build & Configuration

- **`pom.xml`:** `mysql-connector-j` is now pinned to `26.7.0` instead of the version Spring Boot manages — re-check
  it against the parent's managed version at each release, per the Release Checklist's override step

##### Controllers

- **`IpscMatchCompetitorController`:** The bulk import endpoint (`POST /bulk`) accepts an optional `club` query
  parameter that picks the club to import rows for, HPSC's own club when omitted, as
  `createMatchCompetitors(String, String)` does

##### Services

- **`IpscCompetitorServiceImpl.isMemberOfHomeClub`:** New overload, `isMemberOfHomeClub(Club, ClubIdentifier)`, that
  checks a club against the home club passed in; `isMemberOfHomeClub(Club)` now delegates to it with the default home
  club, `IpscConstants.HOME_CLUB_IDENTIFIER` (HPSC), so existing callers behave as before; includes Javadoc and unit
  tests
- **`EntityIpscCompetitorServiceImpl.findCompetitor`:** The range officer marker, `RO` or `(RO)`, is now removed from
  a name wherever it appears — at the start, in the middle or at the end — instead of only at the end, and the
  whitespace left behind is collapsed, so `RO Jane Doe` and `Jane (RO) Doe` match `Jane Doe`; `RO` must be a whole word,
  so a name such as `Romeo` or `PEDRO` is left alone. `IpscConstants.REPLACE_IN_NAMES_REGEX` and the Javadoc follow,
  with unit, service and integration tests
- **`IpscMatchServiceImpl.resolveMatchCategory`:** A match category that is null, empty or blank now resolves to
  `IpscConstants.DEFAULT_MATCH_CATEGORY` (`Club Shoot`) instead of answering a `ValidationException`, as the club
  already defaults when omitted; a category that is supplied must still be known, by display name or constant name
  (`Club Shoot` or `CLUB_SHOOT`), ignoring case and surrounding whitespace. Documented, with unit tests
- **`IpscMatchServiceImpl.validateForCreate`:** The match category is no longer required to create or replace a
  match — a request without one answers no `ValidationException` ("Match category is required.") but takes the
  default match category, as `resolveMatchCategory` already supplies one and the club defaults when omitted; the
  `IpscMatchService` Javadoc and the unit, service and integration tests expect the default
- **`EntityIpscCompetitorService.findCompetitor`:** The competitor number is now a `String` that may also be an ID
  number — the lookup tries the competitor number, then the ID number, then the full name, narrowing several number
  matches by name. `null` is accepted for either argument when the other is given, a `ValidationException` is thrown
  when both are null or blank, and an exception is thrown, rather than an empty `Optional` returned, when no single
  competitor matches — a `NonFatalException` when none do, a `ValidationException` when several do, including
  several that share the number but not the name
- **`EntityIpscCompetitorService.findCompetitor`:** Parameters reordered to `(competitorNumber, fullName)`.
  `IpscMatchCompetitorServiceImpl` now takes an `EntityIpscCompetitorService`, and its `resolveCompetitor` resolves a
  competitor by ID, otherwise through the new `findCompetitorOrThrow(String, String)`, which trims the competitor
  number and matches it, then the ID number, then the full name — an unmatched number and name still throws a
  `NonFatalException`, while an ambiguous one, including several that share the number but not the name, keeps
  throwing a `ValidationException`
- **`IpscMatchCompetitorServiceImpl.resolveCompetitor`:** The match competitor create and patch endpoints now match a
  competitor given by number and name through `findCompetitor`, so a name also matches a nickname, a number also
  matches an ID number, several competitors sharing a number are narrowed by name, and a name is tried when the number
  matches nobody
- **`IpscMatchCompetitorServiceImpl.resolveCompetitor`:** **Breaking:** the shared alias numbers `15000` and `16000`
  are no longer matched by competitor number, so a request that identified a competitor by one of them now resolves
  only if the name matches, and a number that is not a whole number is looked up as an ID number, answering `404`
  when nothing matches, instead of being rejected up front with a `400`
- **`IpscCompetitorServiceImpl`:** A competitor created or updated without a nickname, or with an empty or blank
  one, now takes its first name as the nickname, instead of being saved with none
- **`IpscMatchCompetitorServiceImpl.createMatchCompetitors`:** **Breaking:** the bulk import is now a partial import
  rather than all or nothing — each row is saved on its own, and a row that is missing a required field, has an
  unrecognised enumerated value, names an unknown competitor or match, or duplicates another entry is skipped and
  reported in its `MatchCompetitorBulkResponse` (`success` of `false` and a `message`) while the other rows are still
  created, instead of the whole import answering `400` or `404`. Only unreadable CSV, or a header missing a required
  column, still answers `400`, and an import in which every row fails answers `422` with the same per-row results.
  Its interface and controller Javadoc and Swagger descriptions are updated to match

##### Models

- **`MatchCompetitorResult`, `MatchCompetitorResultHolder`:** Renamed to `MatchCompetitorBulkResponse` and
  `MatchCompetitorBulkResponseHolder`, and the holder's `matchCompetitorResults` field to `matchCompetitors`, so the
  bulk import's response models follow the `*Response`/`*ResponseHolder` naming — a bulk match competitor import's
  response body now carries `matchCompetitors` instead of `matchCompetitorResults`
- **`MatchCompetitorResponse`:** New `competitorName` and `competitorNumber` fields, so a bulk import result
  identifies the competitor it relates to even when the row failed; a persisted match competitor's name is its nickname
  and last name
- **`MatchCompetitorBulkResponse`:** `success` now defaults to `true` and `message` to an empty string

##### Utils

- **`StringUtil.hasText`:** The enums, `ControllerResponse`, `ImageResponse` and the service implementations now use it,
  imported statically, in place of their own null and blank checks — no behaviour change except that
  `ImageResponse.setMimeType` now also ignores a blank MIME type guessed from the file name

- **`StringUtil.toProperCase`:** `IpscCompetitorServiceImpl` now imports it statically, like `hasText` — no behaviour
  change

- **`DateUtil`, `NumberUtil`, `StringUtil`, `ValueUtil`:** Renamed back from `DateUtils`, `NumberUtils`, `StringUtils`
  and `ValueUtils`, the plural names introduced in 8.12.0 (and their test classes to match) — internal classes
  only, so there is no API change

##### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** Gap #36 (`EntityIpscCompetitorService` and the match
  competitor result models built but not wired in) closed in v12.0.0 — both files, the roadmap and the Success
  Criteria updated, and the Open section now empty
- **`ARCHITECTURE.md`, `AGENTS.md`:** Name the renamed `*Util` classes and `MatchCompetitorBulkResponse` models, and
  list `ClubService` in the services table
- **`IpscConstants`:** Every constant now has Javadoc saying what it is for and where it is used — including the
  alias competitor numbers, the range officer name marker and the home club's role in the bulk import — and the class
  Javadoc describes what it holds
- **`ClubService`, `IpscMatchCompetitorService`, `IpscMatchCompetitorServiceImpl`:** `ClubService`'s methods,
  `createMatchCompetitors`' club filter (check order, skipped versus failed rows, the HPSC default) and `isForClub`,
  `resolveCompetitorHomeClub` are documented
- **`EntityIpscCompetitorService`, `IpscMatchCompetitorServiceImpl`:** `findCompetitor`'s interface Javadoc now
  describes the lookup order, the accepted null and blank arguments and the exceptions thrown, and
  `findCompetitorOrThrow(String, String)` and `resolveCompetitor` gain matching Javadoc
- **`DateUtil`, `NumberUtil`, `StringUtil`, `ValueUtil`:** Methods gain `@since` tags, and the class-level Javadoc
  and usage examples name the renamed classes
- **`ControllerAdvice`, `FatalException`, `NonFatalException`, `ValidationException`, `Request`, `Response`,
  `TransactionService`:** Methods and constructors that lacked one gain a `@since` tag
- **`EntityIpscCompetitorServiceImpl`:** `findCompetitor` gains Javadoc that inherits the interface documentation
  (`{@inheritDoc}`) and adds implementation notes on the lookup stages — competitor number, ID number, then full
  name — the name normalisation, and the exception thrown when no single competitor is found

##### Tests

- **`IpscCompetitorServiceImplTest`:** New `applyFields` tests for the nickname defaulting to the first name when
  it is null, empty or blank, replacing an existing nickname, and a supplied nickname being kept
- **`IpscMatchCompetitorServiceTest`, `IpscMatchCompetitorServiceImplTest`, `IpscMatchCompetitorServiceIntegrationTest`,
  `IpscMatchCompetitorControllerTest`:** Updated for the partial bulk import and `MatchCompetitorBulkResponseHolder` —
  a failed row is asserted as an unsuccessful `MatchCompetitorBulkResponse` rather than a thrown exception, with new
  cases for a failing row not stopping the others, for a row duplicating an earlier row in the same import, for the
  `422` when every row fails and for `toFailedResponse`
- **`CompetitorHelpersTest`:** Cover `getCompetitorNumberAsInteger` for numeric, whitespace-padded, zero-padded, null,
  blank and non-numeric numbers, and for the excluded ICS aliases `15000` and `16000`
- **`EntityIpscCompetitorServiceTest`, `EntityIpscCompetitorServiceImplTest`,
  `EntityIpscCompetitorServiceIntegrationTest`:** Brought up to date with the `String` competitor number and the
  `NonFatalException`/`ValidationException` behaviour of `findCompetitor`, and extended to cover the ID number
  lookup, non-numeric, zero, negative, whitespace-padded, `+`-prefixed, null and blank numbers, null names and the
  blank-input validation
- **`IpscMatchCompetitorServiceImplTest`, `IpscMatchCompetitorServiceTest`,
  `IpscMatchCompetitorServiceIntegrationTest`:** Cover `resolveCompetitor` delegating to `EntityIpscCompetitorService`
  and the `NonFatalException` and `ValidationException` thrown for an unmatched and an ambiguous name

#### 🐛 Fixed

##### Models

- **`ControllerResponse`:** The constructor taking only a timestamp, message and error derived `success` the wrong way
  round, so a response with an error was marked successful and one without was not — `success` is now `false` when the
  error has a value and `true` when it is null or blank; its unit tests are corrected to match

##### Services

- **`EntityIpscCompetitorServiceImpl`:** `findCompetitor` no longer throws a `NumberFormatException` for a numeric
  value too long for an `int`, such as a 13-digit ID number — it skips the competitor number lookup and matches the
  value against ID numbers instead

#### 🗑️ Removed

##### Constants

- **`IpscConstants.MAX_SAPSA_NUMBER`:** Removed, as nothing referenced it — internal constant only, so there is no API
  change
- **`IpscConstants.MATCH_POINTS_SCALE`, `HIT_FACTOR_SCALE`, `TIME_SCALE`, `PERCENTAGE_SCALE`:** Removed, as nothing
  referenced them — internal constants only, so there is no API change; the scoring layer can introduce its own scales
  with the code that applies them

### 🧾 [11.0.0] - 2026-10-04

#### ➕ Added

##### Models

- **`MatchCompetitorResult`, `MatchCompetitorResultHolder`:** New response models for a bulk match competitor import —
  a `MatchCompetitorResult` records whether one row succeeded, a message and the `MatchCompetitorResponse` it relates
  to, and the holder carries the results in import order

##### Services

- **`EntityIpscCompetitorService`, `EntityIpscCompetitorServiceImpl`:** New `findCompetitor(fullName, competitorNumber)`
  lookup, registered as a Spring `@Service`, that resolves a single `Competitor` — by competitor number when exactly
  one matches (numbers in `IpscConstants.EXCLUDE_ICS_ALIAS` are skipped), otherwise by full name (narrowed to the number
  matches when there are any), where the full name is either "FirstName LastName" or "Nickname LastName" — returning an
  empty `Optional` when no single competitor can be determined

#### 🔄 Changed

##### Domain

- **`Competitor`:** **Breaking:** The `nickname` field is renamed `nickName` and its column `nickname` is renamed
  `nick_name`, by `V11_0_0__rename_competitor_nickname_to_nick_name.sql` (type, nullability and existing values are
  preserved); `getNickname`/`setNickname` become `getNickName`/`setNickName`, and the repository query and
  `EntityIpscCompetitorServiceImpl` follow
- **`Competitor`:** **Breaking:** `competitorNumber` changes from a `String` to an `Integer` and its column
  `competitor_number` from `VARCHAR(255)` to `INT`, by `V11_1_0__change_competitor_number_to_int.sql` (still
  optional); the migration is refused, and the column left as it was, if any existing value is not a whole number

##### Constants

- **`IpscConstants.MAX_SAPSA_NUMBER`:** Raised from `99_999` to `999_999`
- **`IpscConstants.EXCLUDE_ICS_ALIAS`:** Now a `List<Integer>` (`15000`, `16000`) instead of a `List<String>`, to
  match the numeric competitor number

##### API

- **`AwardController`, `ImageController`, `IpscCompetitorController`, `IpscMatchController`,
  `IpscMatchCompetitorController`:** **Breaking:** The `POST /bulk` endpoints now consume `text/plain` instead of
  `text/csv`, so a request sent with `Content-Type: text/csv` is refused with a 415 error; their OpenAPI request-body
  content type matches, and the CSV body is unchanged
- **`CompetitorRequest`, `CompetitorPatchRequest`, `CompetitorRequestCsvMixIn`, `MatchCompetitorRequest`,
  `MatchCompetitorPatchRequest`, `MatchCompetitorRequestCsvMixIn`:** **Breaking:** `competitorNumber` (CSV
  `CompetitorNumber`, and `Mem #` for match competitors) stays a string in requests, but must now be a whole number:
  it is read as one, and a value that is not, such as `C-1`, is refused with a validation error; a blank value is
  treated as not supplied
- **`CompetitorResponse`:** **Breaking:** `competitorNumber` is now a whole number instead of a string
- **`CompetitorRequest`, `CompetitorPatchRequest`, `CompetitorResponse`, `CompetitorRequestCsvMixIn`:** **Breaking:**
  The competitor's nickname property is renamed from `nickname` to `nickName` in JSON, and its CSV column from
  `Nickname` to `NickName`; a CSV that still has a `Nickname` column imports without a nickname, because unknown
  columns are ignored

##### Repositories

- **`CompetitorRepository`:** `findByCompetitorNumber` and `findByFullNameIgnoreCase` renamed to
  `findAllByCompetitorNumber` and `findAllByFullNameIgnoreCase` (callers in `IpscMatchCompetitorServiceImpl` and its
  tests updated), and the new `findAllByFirstNameLastNameOrNickNameLastNameIgnoreCase` matches a competitor's
  "FirstName LastName" or "Nickname LastName" full name, ignoring case; `findAllByFullNameIgnoreCase` still matches
  only "FirstName LastName", and `findAllByCompetitorNumber` now takes an `Integer`

##### Services

- **`IpscCompetitorServiceImpl.applyFields`, `IpscCompetitorServiceImpl.resolveCompetitorNumber`:** A competitor's
  `competitorNumber` is no longer copied straight from the request — the new `resolveCompetitorNumber` helper uses the
  request's `competitorNumber` when it is supplied, otherwise falls back to the request's `sapsaNumber`, and resolves
  to `null` when neither is supplied; the new `parseCompetitorNumber` reads the request's text as a whole number (a
  blank value is `null`, anything else that is not a whole number is refused with a `ValidationException`), and a
  patch with a blank `competitorNumber` leaves the stored number unchanged
- **`IpscMatchCompetitorServiceImpl.resolveCompetitor`:** Reads the request's `competitorNumber` as a whole number
  before looking the competitor up, ignoring surrounding whitespace and refusing a value that is not a whole number
  with a `ValidationException`

##### Documentation

- **Class-level `@since` tags:** Added to `IpscMatchCompetitorController`, `CompetitorPatchRequest`,
  `CompetitorRequestCsvMixIn`, `MatchPatchRequest`, `MatchRequestCsvMixIn`, `MatchCompetitorPatchRequest`,
  `MatchCompetitorRequestCsvMixIn`, `MatchCompetitorResponse`, `MatchCompetitorResponseHolder`,
  `IpscMatchCompetitorService` and `TransactionService`, which had none
- **`EntityIpscCompetitorService`:** Javadoc describing the number-first, full-name-second lookup in `findCompetitor`,
  including the "RO" suffix handling and the empty result for no match or an ambiguous one
- **`IpscCompetitorServiceImpl.resolveCompetitorNumber`:** Javadoc describing the competitor-number-first,
  SAPSA-number-fallback resolution and the `null` result when neither is supplied

##### Testing

- **`EntityIpscCompetitorServiceTest`, `EntityIpscCompetitorServiceImplTest`, `EntityIpscCompetitorServiceIntegrationTest`:**
  New three-tier tests for `findCompetitor` — the interface contract with a mocked repository (number, full-name and
  nickname matching, excluded aliases, the "RO" suffix and ambiguous or missing matches), the impl's repository calls
  (normalised arguments and skipped queries) and an end-to-end run against the H2 `test` database
- **`IpscCompetitorServiceImplTest`:** New `resolveCompetitorNumber` tests covering both arguments `null`, only the
  competitor number, only the SAPSA number, both supplied (competitor number wins), a blank competitor number with and
  without a SAPSA number and one that is not a whole number, plus `parseCompetitorNumber`

#### 🐛 Fixed

##### Services

- **`EntityIpscCompetitorServiceImpl.findCompetitor`:** The "RO" suffix is now stripped from a full name that has
  leading or trailing whitespace — the name is trimmed before the suffix is removed, so `"Jane Doe (RO) "` no longer
  misses the competitor it names

### 🧾 [10.0.0] - 2026-10-03

#### ➕ Added

##### Domain

- **`MatchCompetitor` overall scores:** New optional columns after `points` — `percentage`, `time`,
  `percentage_of_possible_points`, the `alpha`, `charlie` and `delta` hit counts, `misses`,
  `no_penalty_misses`, `no_shoots`, `procedural_errors` and `additional_penalties`, added by
  `V10_1_0__add_match_competitor_overall_scores.sql` (which also created a `hit_factor` column that
  `V10_3_0__remove_match_competitor_hit_factor.sql` drops again, so none remains)

##### API

- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorRequestCsvMixIn`:** New optional `name`
  (CSV `Name`) and `competitorNumber` (CSV `Mem #`) — find the competitor when `competitorId` is omitted: the number
  is matched exactly against `Competitor.competitorNumber`, otherwise the full name, "First Last", is matched
  case-insensitively; `competitorId` wins over the number, and the number over the name, and a number or name that
  matches no competitor, or more than one, is refused
- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorResponse`:** New optional overall-score
  fields — `percentage`, `time`, `percentageOfPossiblePoints`, `alpha`, `charlie`, `delta`, `misses`,
  `noPenaltyMisses`, `noShoots`, `proceduralErrors` and `additionalPenalties` (CSV columns named as in a PractiScore
  export — `%`, `Time`, `% psbl`, `A`, `C`, `D`, `M`, `NPM`, `NS`, `Proc` and `Apen`), carried
  through `IpscMatchCompetitorServiceImpl` to and from `MatchCompetitor`

##### Repositories

- **`CompetitorRepository.findByCompetitorNumber`, `CompetitorRepository.findByFullNameIgnoreCase`:** New finders —
  the first matches `Competitor.competitorNumber` exactly, the second the competitor's "First Last" full name,
  ignoring case; both return a list so a match on several competitors can be refused

#### 🔄 Changed

##### Documentation

- **`CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md`:** Versions 1.0.0 – 7.4.1 archived, unchanged,
  to `documentation/history/CHANGELOG_v1-v7.md`, `documentation/history/HISTORY_v1-v7.md` (the per-version Historical
  Timeline, Milestone, Architectural Evolution and completed-work entries) and
  `documentation/history/EVOLUTION_OVERVIEW_v1-v7.md` (Phases 1 – 18) to keep the files a manageable size; the
  cross-version sections stay in `HISTORY.md`. `AGENTS.md`, `README.md` and the `prep-version-release` skill list the
  new files, and the v7.2.0 and v7.3.0 PR descriptions link to the archive
- **`ARCHITECTURE.md`:** The deleted `models/ipsc/scores/request/` package and its "groundwork only" DTOs are dropped
  from the Project Structure tree and the `models/ipsc/…` section
- **`flyway-migration-versioning.md`:** The Current State table gains the sixteen migrations it was missing —
  `V7_9_0`, `V8_0_0` to `V8_9_0` and `V10_0_0` to `V10_3_0`
- **`AGENTS.md`, `prep-version-release` skill:** The reverse-sync step now also checks that table against
  `db/migration/`, and the skill checks `ARCHITECTURE.md`'s Project Structure tree
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** Gaps #33 and #34 recorded and closed, and Gap #6 progress
  noted

##### Domain

- **`MatchCompetitor.matchPoints`:** Renamed to `points` — the `match_points` column is renamed to `points` by
  `V10_0_0__rename_match_competitor_match_points_to_points.sql`, keeping its type and values
- **`MatchCompetitor.competitorCategory`, `ShooterLogCompetitor.competitorCategory`:** Back to a single
  `CompetitorCategory` held in a `competitor_category` column, replacing the list and the
  `match_competitor_category` and `shooter_log_competitor_category` child tables, by
  `V10_2_0__move_competitor_category_back_to_a_column.sql` — a row that had several categories keeps the
  alphabetically first, and one with none keeps an empty category

##### API

- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorResponse`:** **Breaking:**
  `matchPoints` renamed to `points` in the JSON contract — existing callers must use the new name
- **`MatchCompetitorRequestCsvMixIn`:** **Breaking:** CSV columns renamed to match a PractiScore export —
  `MatchClub` → `Class`, `CompetitorCategory` → `Cats`, `Division` → `Div`, `PowerFactor` → `PF` and `MatchPoints` →
  `Pts` — existing import files must use the new headers
- **`MatchCompetitorRequest`, `MatchCompetitorPatchRequest`, `MatchCompetitorRequestCsvMixIn`,
  `MatchCompetitorResponse`:** **Breaking:** `competitorCategory` (CSV `Cats`) is a single category again rather
  than a list — a JSON string instead of an array, and one value per CSV cell instead of a `;`-separated list;
  `IpscMatchCompetitorServiceImpl` no longer resolves several categories
- **`MatchCompetitorRequest`, `MatchCompetitorRequestCsvMixIn`:** `competitorId` (CSV `CompetitorId`) is no longer
  required — a request must carry it, a `competitorNumber` or a `name`, which `IpscMatchCompetitorServiceImpl`
  resolves through `CompetitorRepository.findByFullNameIgnoreCase`
- **`IpscMatchCompetitorController`:** The CSV import's Swagger example now shows the new PractiScore-style headers,
  including `Name`, `Mem #` and the overall-score columns

#### 🗑️ Removed

##### Models

- **`MatchOverallScoresRequest`, `MatchOverallScoresRequestForCSV`, `MatchStageScoresRequest`,
  `MatchStageScoresRequestForCSV`:** The `ipsc.scores.request` package and its tests are deleted — they modelled
  per-stage and overall match scores for the `MatchStage` table and entities, which were removed, and nothing
  references them any more; the overall-score fields now live on `MatchCompetitor`

### 🧾 [9.1.0] - 2026-10-03

#### ➕ Added

##### Domain

- **`ShooterLogCompetitor.dateCalculated`:** New nullable `LocalDateTime` column — records when a row's rank and
  points were calculated, separately from `dateCreated` and `dateUpdated`
- **`ShooterLogCompetitor.competitorCategory`, `ShooterLogCompetitor.division`:** New required fields — each row now
  records the competitor's categories, a list held in the `shooter_log_competitor_category` table, and their division
- **`ShooterLogOverall`:** New entity holding a rank and points per competitor category and division, with the date
  they were calculated, for overall log standings
- **`ShooterLogOverall.shooterLog`, `ShooterLogOverall.competitor`:** Required links to the `ShooterLog` and `Competitor` a
  row belongs to; a competitor has one row per shooter log
- **`Competitor.isVerified`:** New optional `Boolean` flag — whether the competitor has been verified; existing
  competitors are backfilled to `true`

##### API

- **`isVerified` on competitors:** Optional field on `CompetitorRequest` (JSON and CSV `IsVerified` column),
  `CompetitorPatchRequest` (`null` leaves it unchanged) and `CompetitorResponse`
- **`IpscMatchCompetitorController`:** New `/ipsc/match-competitors` endpoints to create, replace (`PUT`), patch, get (one
  or all) and delete a competitor's entry in a match, with `MatchCompetitorRequest`, `MatchCompetitorPatchRequest` and
  `MatchCompetitorResponse`; a competitor can have one entry per match and firearm type, under one or more
  categories — `competitorCategory` is a list in all three
- **`MatchCompetitorRequestCsvMixIn`:** New Jackson mix-in binding UpperCamelCase CSV column headers onto
  `MatchCompetitorRequest`'s constructor, with `CompetitorCategory` as one cell of categories split on the shared array
  separator and unknown columns ignored
- **`IpscMatchCompetitorController.createMatchCompetitors`:** New `POST /ipsc/match-competitors/bulk` endpoint taking
  `text/csv` and returning a `MatchCompetitorResponseHolder` with `201`; every row is checked before any is saved, so
  either every row is created or none is. A `MatchCompetitorId` column is read but ignored, since the import only
  creates entries

##### Services

- **`IpscMatchCompetitorService`:** New service behind the match competitor endpoints; validates required fields, resolves
  the competitor, match and enumerated values, and refuses an entry that duplicates another for the same competitor,
  match and firearm type
- **`IpscMatchCompetitorService.createMatchCompetitors`:** Reads the CSV through `MatchCompetitorRequestCsvMixIn`,
  builds every row by the rules of `createMatchCompetitor`, and refuses a row that duplicates an existing entry, or
  another row, for the same competitor, match and firearm type
- **`TransactionService.saveMatchCompetitors`:** New transactional batch write for match competitors; it flushes inside
  the transaction so a unique constraint violation is reported as a 400
- **`TransactionService.saveMatchCompetitor`, `TransactionService.deleteMatchCompetitor`:** New transactional writes
  for match competitors; the delete flushes inside the transaction so a foreign-key violation is reported as a 400

##### Repositories

- **`MatchCompetitorRepository`:** New `findByCompetitorIdAndMatchIdAndFirearmType`,
  `findByIdWithCompetitorAndMatch` and `findAllWithCompetitorAndMatch` queries
- **`ShooterLogOverallRepository`:** New repository for `ShooterLogOverall`, with `findAllByShooterLogId` and
  `existsByCompetitorId`
- **`ShooterLogRepository.existsByMatchesId`:** New query — whether a match is linked to any shooter log through
  `shooter_log_match`, for the match delete check

##### Database

- **`V8_0_0__add_shooter_log_competitor_date_calculated`:** Adds the nullable `date_calculated` column to
  `shooter_log_competitor`
- **`V8_0_1__add_shooter_log_competitor_category_and_division`:** Adds the `NOT NULL` `competitor_category` and
  `division` columns to `shooter_log_competitor`, after `match_id`; fails if the table already has rows. `V8_9_0` later
  moves `competitor_category` into its own table
- **`V8_2_0__create_shooter_log_overall`:** Creates the `shooter_log_overall` table behind `ShooterLogOverall`
- **`V8_7_0__add_competitor_is_verified`:** Adds the nullable `is_verified` column to `competitor`, after
  `paid_up_club`, and sets it to `true` for every existing row
- **`V8_8_0__move_match_competitor_category_to_child_table`:** Creates the `match_competitor_category` table — a
  `competitor_category` per `match_competitor`, unique per pair — copies each existing row's category into it, then
  drops `match_competitor.competitor_category`
- **`V8_9_0__move_shooter_log_competitor_category_to_child_table`:** Creates the `shooter_log_competitor_category`
  table — a `competitor_category` per `shooter_log_competitor`, unique per pair — copies each existing row's category
  into it, then drops `shooter_log_competitor.competitor_category`

##### Tests

- **`IpscMatchCompetitorControllerTest`, `IpscMatchCompetitorServiceTest`, `IpscMatchCompetitorServiceImplTest`,
  `IpscMatchCompetitorServiceIntegrationTest`:** New tests for the match competitor controller and service
- **`TransactionServiceTest`:** New tests for `saveMatchCompetitors`, committing the batch in one transaction and
  rolling it back when the flush fails
- **`MatchCompetitorRequestTest`, `MatchCompetitorPatchRequestTest`:** New tests for the match competitor request
  models — JSON serialization and deserialization, including each required field being rejected when missing
- **`MatchCompetitorRequestCsvMixInTest`:** New tests for the match competitor CSV mix-in
- **`ShooterLogOverallRepositoryIntegrationTest`:** New integration tests for `ShooterLogOverallRepository`

#### 🔄 Changed

##### Services

- **`TransactionServiceImpl`:** Constructor now also takes a `MatchCompetitorRepository`
- **`IpscMatchServiceImpl`, `IpscCompetitorServiceImpl`:** Constructors now take the repositories their delete checks
  need — a `ShooterLogRepository` for the match service, and a `ShooterLogCompetitorRepository` and
  `ShooterLogOverallRepository` in place of the `ShooterLogRepository` for the competitor service
- **`IpscMatchServiceImpl.deleteMatch`:** Also refuses a match that is linked to a shooter log through
  `shooter_log_match`, using the new `ShooterLogRepository.existsByMatchesId`, as well as one with shooter log
  competitors
- **`IpscCompetitorServiceImpl.deleteCompetitor`:** The check for shooter logs now asks
  `ShooterLogCompetitorRepository.existsByCompetitorId`, since a shooter log no longer has a competitor of its own, and
  also checks `ShooterLogOverallRepository.existsByCompetitorId` — a competitor in any shooter log, or with an overall
  shooter log row, is refused deletion

##### API

- **`CompetitorRequest`, `CompetitorResponse`, `CompetitorRequestCsvMixIn`:** `emailAddresses` now follows
  `cellphoneNumber` in the constructor parameters, rather than coming last. The JSON and CSV formats are unchanged,
  since both read by name
- **`MatchRequest`, `MatchRequestCsvMixIn`:** `matchFirearmType` and `matchCategory` are no longer required at
  deserialization — a JSON body, or a CSV header or row, that omits them is read rather than rejected with a
  `MismatchedInputException`
- **`MatchCompetitorRequest`:** New `@JsonCreator` constructor with a `@JsonProperty` on every parameter, and a
  `matchCompetitorId` field, `null` when creating a new match competitor. `firearmType` is no longer required at
  deserialization, and a missing required field is now rejected when the request is read

##### Repositories

- **`ShooterLogCompetitorRepository`:** `existsByMatchId` is replaced by `existsByCompetitorId` and
  `existsByMatchCompetitorMatchId`, since a shooter log competitor no longer has a direct `match`
- **`ShooterLogRepository`:** `findAllByCompetitorIdAndFirearmTypeAndPowerFactor` and `existsByCompetitorId` are
  removed, since a shooter log no longer has a competitor, firearm type or power factor of its own

##### Domain

- **`Competitor`, `CompetitorPatchRequest`:** `emailAddresses` moved to follow `cellphoneNumber`, matching the order
  of the request and response models. Only the Java field order changes, not the table's columns
- **`MatchCompetitor.division`, `MatchCompetitor.competitorCategory`:** Both are now required. `division` is also
  mapped with `DivisionConverter` — previously it had no explicit mapping and the column was nullable
- **`MatchCompetitor.competitorCategory`:** Now a `List<CompetitorCategory>` — a match competitor can have several
  categories, stored in the new `match_competitor_category` table rather than a column, and must have at least one
- **`ShooterLogCompetitor.competitorCategory`:** Now a `List<CompetitorCategory>` too, stored in the new
  `shooter_log_competitor_category` table rather than a column, and must have at least one
- **`MatchCompetitor.firearmType`:** Now optional. The `(competitor_id, match_id, firearm_type)` unique constraint no
  longer limits rows with a null `firearm_type`, since MySQL treats NULLs as distinct
- **`ShooterLogCompetitor.competitor`, `ShooterLogCompetitor.match`:** A shooter log competitor now links to its
  `Competitor` directly and is unique per shooter log and competitor, rather than per match competitor. The direct `match`
  link is removed — the match is reached through `matchCompetitor`
- **`ShooterLog`:** Reworked to a date range and its matches — the `competitor`, `club`, `firearmType`, `powerFactor`,
  `logValue` and `calculatedDate` fields are removed, and `startDate` and `endDate` are added. `matches` is a new
  many-to-many link to `IpscMatch` through `shooter_log_match`: a shooter log covers many matches and a match can be in
  many shooter logs

##### Database

- **`V8_1_0__update_match_competitor_column_nullability`:** Makes `match_competitor.division` and
  `match_competitor.competitor_category` `NOT NULL`, and `match_competitor.firearm_type` nullable; the `NOT NULL`
  changes fail if any existing row has a null in either column
- **`V8_3_0__drop_shooter_log_competitor_match_id`:** Drops `shooter_log_competitor.match_id` and its foreign key —
  the match is reached through `match_competitor_id`
- **`V8_4_0__rework_shooter_log_columns`:** Drops `shooter_log`'s `competitor_id`, `club_id`, `firearm_type`,
  `power_factor`, `log_value` and `calculated_date` columns (and the competitor and club foreign keys), and adds nullable
  `start_date` and `end_date` `DATE` columns
- **`V8_5_0__create_shooter_log_match`:** Creates the `shooter_log_match` join table, with foreign keys to
  `shooter_log` and `ipsc_match`, linking shooter logs and matches many-to-many
- **`V8_6_0__add_shooter_log_competitor_and_overall_links`:** Adds the `NOT NULL` `competitor_id` column, with a
  foreign key, to `shooter_log_competitor`, and `shooter_log_id` and `competitor_id` columns, with foreign keys, to
  `shooter_log_overall`; both tables become unique per shooter log and competitor (replacing
  `shooter_log_competitor`'s unique key on shooter log and match competitor). Fails if either table already has rows

##### Documentation

- **`AGENTS.md` Release Checklist, `prep-version-release` skill:** New step to align the Markdown tables in the files a
  release touches — `README.md` and `ARCHITECTURE.md`, the new release's sections of `CHANGELOG.md` and `HISTORY.md`,
  and `RELEASE_NOTES.md` — measured in display columns so emoji line up, and run before `RELEASE_NOTES.md` is
  archived. The later checklist and skill steps are renumbered

#### 📦 Dependencies

##### Database

- **`mysql-connector-j`:** The `9.4.0` pin in `pom.xml` is dropped, since Spring Boot `4.1.1` now manages `9.7.0`, which
  is newer — the connector follows the parent again

### 🧾 [9.0.0] - 2026-10-01

#### 🔄 Changed

##### Match API

- **Breaking — `MatchRequest`, `MatchResponse`:** The `stages` field is gone from both. A match request or response
  no longer carries stages, and the bulk CSV import no longer has a `Stages` column
- **`TransactionService.saveMatch`:** The `saveMatch(IpscMatch, List, StageSaveMode)` overload and the `StageSaveMode`
  enum are removed along with the stage replace/upsert logic — `saveMatch(IpscMatch)` is the only single-match save
- **`IpscMatchService.deleteMatch`:** No longer checks for recorded stage results, as there are none to check — a
  match is still refused deletion while it has competitor results or is referenced by shooter logs
- **`MatchRequestCsvMixIn`:** New Jackson mix-in binding the CSV column headers onto `MatchRequest`'s constructor, so
  the bulk import reads each row straight into a `MatchRequest` — the accepted CSV columns are unchanged. The
  `MatchRequestForCSV` model is removed along with `IpscMatchServiceImpl.toRequest`, which only copied it into a
  `MatchRequest`
- **Breaking — `MatchRequest.matchFirearmType`, `MatchRequest.matchCategory`:** Now required properties, like
  `matchDate` and `matchName` — a JSON request body that leaves either out is rejected when it is read, instead of
  reaching the service. They were already needed to create or replace a match, so only the error response changes
- **`MatchPatchRequest`, `IpscMatchService.patchMatch`, `IpscMatchController.patchMatch`:** New request model for
  `PATCH /ipsc/matches/{matchId}`, replacing `MatchRequest` there — no field is required, since the match is identified
  by the path ID alone, and any field left out is left unchanged. This keeps a patch partial now that `MatchRequest`
  requires its match date, name, firearm type and category. It has no `matchId`, so a body that sent one now has it
  ignored
- **`IpscMatchServiceImpl.readMatches`:** A CSV header may now omit optional columns, and unknown columns are ignored —
  `MatchDate`, `MatchName`, `MatchFirearmType` and `MatchCategory` must be present in the header and in every row, and
  a CSV that lacks any of them is rejected as invalid. A `MatchId` column is read but never used, since the import only
  creates matches
- **`IpscMatchController.createMatches`:** The bulk import's Swagger request schema is now plain text, with its example
  header row, rather than the removed CSV model

##### Competitor API

- **`CompetitorRequestCsvMixIn`:** New Jackson mix-in binding the CSV column headers onto `CompetitorRequest`'s
  constructor, so the bulk import reads each row straight into a `CompetitorRequest` — the accepted CSV columns are
  unchanged. The `CompetitorRequestForCSV` model is removed; `IpscCompetitorServiceImpl.toRequest`, which copied it into
  a `CompetitorRequest` while normalising name casing, becomes `normaliseCsvRequest` and takes the `CompetitorRequest`
  directly, keeping the row's `competitorId` rather than blanking it
- **`CompetitorRequest.emailAddresses`:** Now defaults to an empty list rather than `null`. The CSV import splits the
  semicolon-separated `EmailAddresses` cell on the shared array element separator itself, so
  `IpscCompetitorServiceImpl.splitEmailAddresses` is removed. The accepted CSV format is unchanged
- **`IpscCompetitorServiceImpl.readCompetitors`:** A CSV header may now omit optional columns, and unknown columns are
  ignored — only `FirstName` and `LastName` are still required, and a row that lacks either is rejected as invalid. A
  `CompetitorId` column is read but never used, since the import only creates competitors
- **`IpscCompetitorController.createCompetitors`:** The bulk import's Swagger request schema is now plain text, with its
  example header row, rather than the removed CSV model
- **`CompetitorPatchRequest`, `IpscCompetitorService.patchCompetitor`, `IpscCompetitorController.patchCompetitor`:** New
  request model for `PATCH /ipsc/competitors/{competitorId}`, replacing `CompetitorRequest` there — no field is
  required, since the competitor is identified by the path ID alone, so a patch no longer has to repeat `firstName` and
  `lastName`. Its `emailAddresses` is `null` unless supplied, so a patch that omits it keeps the competitor's addresses
  instead of clearing them. It has no `competitorId`, so a body that sent one now has it ignored
- **`IpscCompetitorServiceImpl.resolveHomeClub`:** A competitor's home club now also resolves by club abbreviation when
  no club has a matching name, so a competitor request or CSV row may name the club either way

##### Documentation

- **`README.md`, `ARCHITECTURE.md`:** Stages dropped from the match description, the entity and repository tables and
  the Project Structure tree, and the CSV models replaced by the new mix-ins and patch request models
- **`CHANGELOG.md`, `HISTORY.md`, `documentation/EVOLUTION_OVERVIEW.md`:** Past-release entries keep the class
  names they were written with, rather than the renamed CSV request models

##### Tests

- **`MatchRequestCsvMixInTest`, `CompetitorRequestCsvMixInTest`, `MatchPatchRequestTest`,
  `CompetitorPatchRequestTest`:** New tests for the CSV mix-ins and the patch request models, replacing
  `MatchRequestForCSVTest`, `CompetitorRequestForCSVTest` and `MatchStageRequestTest`
- **Stage tests:** Stage coverage removed from the match, competitor, repository and transaction service tests, along
  with `MatchStageCompetitorRepositoryIntegrationTest`

#### 📦 Dependencies

##### Database

- **`mysql-connector-j`:** Pinned to `9.4.0` in `pom.xml` instead of the Spring Boot-managed version

#### 🔧 Configuration

##### Database

- **Breaking — `spring.datasource.username`, `MYSQL_USER`:** The username is no longer read from the `MYSQL_USER`
  environment variable in `application.properties`; the `dev` and `prod` profiles now set it (`hpsc_dev`,
  `hpsc_prod`), so a deployment that relied on `MYSQL_USER` must switch profile or set the property itself
- **`application-dev.properties`:** The datasource URL now points at `127.0.0.1` rather than `localhost`
- **`application-local.properties`:** Drops the `MYSQL_LOCAL_PASSWORD` override, so the local profile now uses
  `MYSQL_PASSWORD` like the others

#### 🗑️ Removed

##### Persistence

- **Breaking — `IpscMatchStage`, `MatchStageCompetitor`:** Both entities are removed, together with
  `IpscMatchStageRepository`, `MatchStageCompetitorRepository` and the `IpscMatch.stages` collection
- **`V7_9_0__drop_ipsc_match_stage.sql`:** New Flyway migration dropping the `match_stage_competitor` and
  `ipsc_match_stage` tables with `DROP TABLE IF EXISTS`, in that order, since the former holds a foreign key to the
  latter. Any existing stage data is discarded

##### Match API

- **`MatchStageRequest`, `MatchStageResponse`:** Removed, along with `IpscMatchServiceImpl.toStages` and
  `parseStages`

##### Constants

- **`IpscConstants.EXCLUDE_CLUB_IDENTIFIERS`, `IpscConstants.STAGE_POINTS_SCALE`:** Removed — nothing references
  either any more

#### 🔐 Security

##### Dependencies

- **`tomcat.version`:** Raised from `11.0.25` to `11.0.26` in `pom.xml`
- **`logback.version`:** New `pom.xml` override pinning Logback to `1.6.5`
- **`jackson-2-bom.version`, `jackson-bom.version`:** New `pom.xml` overrides raising the Jackson 2 BOM to `2.22.3`
  and the Jackson 3 BOM to `3.2.3`, so `jackson-core` and `jackson-dataformat-csv` follow. They override the
  properties Spring Boot imports the BOMs through, rather than importing a Jackson POM, whose parent chain would
  re-pin `junit-bom`
- **`flyway-mysql`:** Now pinned to `13.7.0` instead of the Spring Boot-managed version

### 🧾 [8.12.0] - 2026-09-29

#### ➕ Added

##### CI/CD & Configuration

- **`.github/workflows/code_quality.yml`:** New Qodana static-analysis workflow, running `JetBrains/qodana-action` on
  every push to `main`, `release/*`, `feature/*`, `bugfix/*` and `hotfix/*`, plus PRs into `main`/`develop`

##### Services

- **`StringUtils.toProperCase`:** New helper that upper-cases the first letter of each word and lower-cases the rest,
  treating spaces, hyphens and apostrophes, straight or curly, as word breaks (e.g. `o'NEIL-smith` → `O'Neil-Smith`) — backed by the new
  `org.apache.commons:commons-text` dependency's `WordUtils`
- **`CompetitorHelpers.toSentenceCaseLastName`:** New helper in the new `za.co.hpsc.web.helpers` package that
  lower-cases surname particles (`van`, `der`, `du`, `de`, `le` and the like) when they precede the surname proper —
  e.g. `Van Der Merwe` → `van der Merwe`, including after a hyphen (`Smith-Van Der Merwe` → `Smith-van der Merwe`) —
  and capitalises the letter after a Gaelic `Mc` prefix (`Mcdonald` → `McDonald`, leaving the Zulu `Mch`/`Mcu`
  surnames such as `Mchunu` alone). Only whole words are matched, so `Dube` and `Vanderbilt` are left alone. Listed in `ARCHITECTURE.md`'s Project Structure tree and a new Helpers table

##### Tests

- **`IpscCompetitorServiceImplTest`:** New `toRequest` tests for all-upper-case and all-lower-case CSV rows, an
  upper-case last name with several particles (`DE LA REY` → `de la Rey`) and one that merely starts with particle
  letters (`DUBE` → `Dube`), hyphenated, `Mc`-prefixed and curly-apostrophe last names, and lower- and upper-case alphanumeric ID and cellphone numbers — pinning that the home
  club, competitor, club, ID and cellphone numbers and email addresses keep their case whatever case they arrive in

#### 🔄 Changed

##### CI/CD & Configuration

- **`.github/workflows/codeql.yml`, `.github/workflows/dependency-submission.yml`:** Push triggers now also cover
  `release/*`, `feature/*`, `bugfix/*` and `hotfix/*` branches, not just `main`/`develop`, so a CodeQL or
  dependency-submission failure surfaces on the branch it was introduced on rather than only once it reaches
  `develop`
- **`.github/workflows/docker.yml`:** Dependabot's `github-actions` group update (PR #152, merged into `develop` and
  then into this release branch) bumps `docker/setup-buildx-action` from `v3` to `v4` and `docker/build-push-action`
  from `v6` to `v7` — both major bumps, with no change to the workflow's inputs

##### Services

- **`IpscCompetitorServiceImpl.toRequest`:** The competitor CSV import now proper-cases the `FirstName`, `LastName`,
  `MiddleNames`, `Nickname` and `Gender` columns, so imported names are stored consistently regardless of how they
  were typed — `HomeClub` (matched exactly against club names), `ClubNumber` and `CompetitorNumber` (codes, club
  numbers also being unique), `IdNumber`, `CellphoneNumber` and `EmailAddresses` are kept as supplied. The JSON
  create, update and patch endpoints are unaffected
- **`IpscCompetitorServiceImpl.toRequest`:** The imported `LastName` is also passed through
  `CompetitorHelpers.toSentenceCaseLastName` after proper-casing, so `VAN DER MERWE` is stored as `van der Merwe`
  rather than `Van Der Merwe`

##### Utils

- **`DateUtils`, `NumberUtils`, `StringUtils`, `ValueUtils`:** Renamed from `DateUtil`, `NumberUtil`, `StringUtil` and
  `ValueUtil` (and their test classes to match) — internal classes only, so there is no API change

##### Documentation

- **`AGENTS.md`, `CONTRIBUTING.md`:** Added `bugfix/<short-description>` as its own standard GitFlow branch type,
  split out of `feature/*`'s prior "feature and bug-fix work" description — non-critical bug fixes not yet in
  production branch from, and PR back into, `develop`, same as `feature/*`, distinguishing them from `hotfix/*`,
  which is reserved for defects already in production
- **`ARCHITECTURE.md`, `CONTRIBUTING.md`:** CI/CD & Quality Gates table gains a Static Analysis (Qodana) row, and its
  CodeQL and Dependency Submission triggers now read "any GitFlow branch", matching the widened workflow triggers
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #32 from an `update-improvement-plan-gaps` sweep —
  Qodana returned to CI as `code_quality.yml`, but the plan still described it as removed in v8.2.0 (Gap #7) — closed
  within this release, with "🌳 At a Glance", the "⚙️ Goals & Constraints" CI/CD row, the "🛤️ Roadmap" **Next** row and
  "☑️ Success Criteria" updated to match

### 🧾 [8.11.1] - 2026-09-27

#### ➕ Added

##### CI/CD & Configuration

- **`.github/workflows/docker.yml`:** New workflow building the `Dockerfile` on every push and PR to `main`/`develop`,
  so a change that breaks the image fails CI instead of surfacing at deployment — the Known Issue v8.11.0 recorded.
  Built with `docker/build-push-action` and never pushed, reusing layers through the GitHub Actions cache. Listed in
  `ARCHITECTURE.md`'s CI/CD & Quality Gates table and `CONTRIBUTING.md`'s summary of it

##### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #31 from an `update-improvement-plan-gaps` sweep —
  CI never built the Docker image, the Known Issue and Future Enhancement v8.11.0 recorded with no gap tracking it —
  closed within this release by `docker.yml`, with "🌳 At a Glance", the "⚙️ Goals & Constraints" CI/CD row, the
  "🛤️ Roadmap" **Next** row and "☑️ Success Criteria" updated to match

#### 🔄 Changed

##### CI/CD & Configuration

- **`pom.xml` (`jacoco-maven-plugin`'s `check` execution):** The coverage gate now also enforces a 97% `BRANCH`
  minimum alongside the existing 97% `LINE` one, so a branch-coverage regression fails `build.yml` too — ending the
  line-only deviation recorded under Gap #4 and the Known Issue carried since v8.4.0. Branch coverage stands at
  99.09%. Reflected in `ARCHITECTURE.md`'s CI/CD & Quality Gates table and `improvement-plan.md`

##### Build & Metadata

- Project version bumped to **8.11.1** in `pom.xml`; `@OpenAPIDefinition` version updated to match

### 🧾 [8.11.0] - 2026-09-27

#### ➕ Added

##### CI/CD & Configuration

- **`Dockerfile`, `.dockerignore`:** New multi-stage Docker image — built with the Maven wrapper on a JDK 25 image,
  then run as a non-root user on a Java 25 JRE from Spring Boot's extracted JAR layers, so dependency layers stay
  cached between builds. It defaults to the `prod` profile and reads `SPRING_DATASOURCE_URL`, `MYSQL_USER` and
  `MYSQL_PASSWORD` at run time; `JAVA_OPTS` passes JVM options. Its `HEALTHCHECK` polls Actuator's health endpoint,
  with a 90-second start period for Flyway's first migration. Documented in `README.md`'s new Running with Docker
  section
- **`spring-boot-starter-actuator`:** New dependency exposing `/hpsc-web/actuator/health` (Actuator's defaults: the
  health endpoint only, including a database check) for deployments to poll. Added to the tech stacks in
  `README.md`, `ARCHITECTURE.md` and `AGENTS.md`
- **`docker-compose.yml`, `.env.example`:** New Compose setup running the application against a MySQL 8.4 container
  — the application waits for MySQL's health check, Flyway creates the schema on first start, and the database and
  log files persist in named volumes. Credentials come from a gitignored `.env`, copied from `.env.example`, and
  Compose refuses to start without them. `.env.example` recommends `MYSQL_PORT=3307` when a local MySQL already
  listens on 3306

##### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #30 from an `update-improvement-plan-gaps` sweep —
  every doc described Flyway as managing the MySQL schema, but it never ran at startup — closed within this release
  (see Fixed), with "🌳 At a Glance", the "⚙️ Goals & Constraints" Flyway row, the "🛤️ Roadmap" **Next** row and
  "☑️ Success Criteria" updated to match

#### 🔄 Changed

##### Tests

- **`IpscMatchTest`:** The two stage tests now separate Act from Assert — linking the stage is the Act step, in place
  of a combined `// Act & Assert` comment — per `AGENTS.md`'s Arrange-Act-Assert convention

##### Documentation

- **`ARCHITECTURE.md`:** The Technology Stack table gains a Containerisation row (`Dockerfile`, `docker-compose.yml`),
  and its Schema migrations row now says Flyway applies them at startup

##### Build & Metadata

- Project version bumped to **8.11.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

#### 🐛 Fixed

##### Build & Configuration

- **`spring-boot-starter-flyway`:** Flyway migrations now run at startup. Spring Boot 4 moved Flyway's
  auto-configuration into its own `spring-boot-flyway` module, so with only `flyway-core` on the classpath the
  application never migrated and `spring.flyway.*` was ignored in every profile — found when the Docker Compose
  database came up empty. The starter replaces the direct `flyway-core` dependency
- **`application-prod.properties`:** Baselines a non-empty production schema without Flyway's history at `7.0.0`
  (`baseline-on-migrate`, as the `local` profile does), so the first start applies `V7_1_0` onwards instead of
  failing on the existing tables; an empty database is still built in full. Reflected in `CONTRIBUTING.md`'s Database
  Profiles table and `AGENTS.md`'s Flyway note

### 🧾 [8.10.2] - 2026-09-26

#### 🔄 Changed

##### CI/CD & Configuration

- **`.github/workflows/claude-code-review.yml`:** The Claude code review now also runs on Dependabot's PRs
  (`allowed_bots: 'dependabot'`), which it previously skipped as bot-authored. Dependabot-triggered runs can only read
  Dependabot secrets, so `CLAUDE_CODE_OAUTH_TOKEN` must also be stored as a Dependabot secret for these reviews to
  run; documented in `ARCHITECTURE.md`'s CI/CD & Quality Gates section
- **`actions/checkout`, `actions/setup-java`, `actions/upload-artifact`:** Bumped `v4` → `v7`, `v4` → `v6` and
  `v4` → `v7` across every workflow — Dependabot's first grouped GitHub Actions update (#140)
- **`.github/workflows/dependency-submission.yml`:** The "Make Maven wrapper executable" step is gone — `mvnw` is now
  committed with its executable bit, set by Dependabot's Maven wrapper update

##### Build & Metadata

- **`springdoc-openapi-bom`, `jacoco-maven-plugin`, Maven wrapper:** Bumped `3.1.0` → `3.1.1`, `0.8.14` → `0.8.15`
  and Maven `3.9.12` → `3.9.16` (`.mvn/wrapper/maven-wrapper.properties`, regenerated `mvnw`/`mvnw.cmd`, `mvnw` now
  executable) — Dependabot's first grouped Maven minor/patch update (#138)
- Project version bumped to **8.10.2** in `pom.xml`; `@OpenAPIDefinition` version updated to match

#### 🐛 Fixed

##### Build & Metadata

- **`flyway-maven-plugin`'s `flyway-mysql` dependency:** Dependabot bumped it `12.4.0` → `13.7.0` (#139), but the
  plugin itself is Spring Boot's managed `12.4.0` and `flyway-mysql` 13.7.0 requires `flyway-core` 13.7.0, mixing two
  Flyway majors on the plugin's classpath. It now uses `${flyway.version}` — plugin-scoped dependencies don't inherit
  the parent's `dependencyManagement`, but they do inherit its properties — so it always matches the plugin, with no
  hand-pinned version to keep in sync or for Dependabot to bump
- **`mvnw.cmd`:** Renormalised to the line endings `.gitattributes` specifies — Dependabot's wrapper update had
  committed it with CRLF line endings

### 🧾 [8.10.1] - 2026-09-26

#### ➕ Added

##### CI/CD & Configuration

- **`.github/workflows/dependency-submission.yml`:** New explicit Maven dependency-submission workflow, replacing
  GitHub's built-in "Automatic Dependency Submission (Maven)" — the one check on `develop`→`main` PRs with no
  workflow file behind it. It resolves the dependency graph with the project's own JDK 25 and Maven wrapper (the
  built-in one used JDK 21 and ignored the wrapper) on push to `main`/`develop` or manual dispatch; the built-in
  submission must be turned off in the repository's Code security settings. Documented in `ARCHITECTURE.md`'s CI/CD
  & Quality Gates table and `CONTRIBUTING.md`
- **`.github/dependabot.yml`:** New Dependabot version-update configuration — weekly Maven and GitHub Actions
  updates, opened against `develop` per the GitFlow branching model, with Maven minor/patch bumps and all Actions
  bumps each grouped into one PR. Security updates are configured separately and still target `main`. Added to
  `ARCHITECTURE.md`'s Project Structure tree

##### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #29 from an `update-improvement-plan-gaps` sweep —
  Dependabot security-update PRs target `main`, bypassing the GitFlow rule that only `develop` and `hotfix/*` reach
  it — closed within this release (see Changed)

#### 🔄 Changed

##### Documentation

- **`AGENTS.md`, `CONTRIBUTING.md`, `.github/dependabot.yml`:** Dependabot security-update PRs, which always target
  `main`, are now handled as hotfixes — merged into `main` so the fix ships at once, then carried into `develop` by
  merging `main` back into it, since Dependabot deletes its branch after merging. `AGENTS.md`'s Branching Model names
  them as the only exception besides `hotfix/*`, and a new `dependabot/*` entry separates them from version-update
  PRs, which target `develop` like any `feature/*` PR. Closes `improvement-plan.md`'s Gap #29

##### Build & Metadata

- Project version bumped to **8.10.1** in `pom.xml`; `@OpenAPIDefinition` version updated to match

### 🧾 [8.10.0] - 2026-09-26

#### ➕ Added

##### Configuration

- **`application-prod.properties`:** New `prod` profile pointing production at `localhost:3306/hpsc_prod`, still
  reading `MYSQL_USER`/`MYSQL_PASSWORD` — the no-profile run is unchanged and still takes its URL from outside;
  documented in `AGENTS.md`, `ARCHITECTURE.md` and `CONTRIBUTING.md`

##### Tests

- **`IpscMatchTest`:** New entity unit test guarding `IpscMatch.stages`' exclusion from Lombok's `toString`/`equals`/
  `hashCode` — `IpscMatchStage.match` points straight back, so dropping either exclusion makes both recurse into a
  `StackOverflowError` — plus `stages` initialising empty and mutable. The other seven entities have no hand-written
  behaviour, so get no unit tests of their own; closes `improvement-plan.md`'s Gap #25

##### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New open Gaps #25–#26 from an
  `update-improvement-plan-gaps` sweep — entity-level unit tests are a stated goal in `HISTORY.md` and the v8.9.0
  release notes with nothing tracking it (#25); `pom.xml`'s `tomcat.version` CVE override has been a standing manual
  pin since v8.3.1, although the plan said no overrides remained (#26). "🌳 At a Glance", "🛤️ Roadmap" **Next** and
  **Ongoing** rows, "☑️ Success Criteria" and the "⚙️ Goals & Constraints" `pom.xml` row updated to match, and that
  table's stale ~98.4% coverage figure replaced with v8.9.0's measured 98.77%
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #27 from a second `update-improvement-plan-gaps`
  sweep — the database-profile docs promised a setup the properties files don't provide — closed within this release
  (see Fixed)
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #28 from a third `update-improvement-plan-gaps`
  sweep — `logback-spring.xml` configured a `staging` profile that existed nowhere else — closed within this release
  (see Removed)

#### 🔄 Changed

##### Tooling

- **`AGENTS.md`, `prep-version-release`:** Semantic Versioning is now a strict, documented rule — a new Semantic
  Versioning subsection under Git Workflow defines what counts as a MAJOR, MINOR or PATCH change for this project,
  how to classify a release from `[Unreleased]`, and that breaking changes are flagged in `CHANGELOG.md` as they land;
  the Release Checklist and the `prep-version-release` skill now validate the requested version against those rules
  before bumping, and re-check it after syncing `[Unreleased]`
- **`generate-commit-message`, `sync-unreleased-changes`:** Both skills now apply `AGENTS.md`'s `**Breaking:**`
  prefix to CHANGELOG entries for backward-incompatible changes — `sync-unreleased-changes` also flags existing
  entries whose prefix is missing or wrong as drifted, and reports the release level the synced `[Unreleased]` section
  implies
- **`AGENTS.md`, `prep-version-release`:** The Release Checklist's `pom.xml` bump step now re-checks every manual
  dependency-version override against the version the Spring Boot parent's own `spring-boot-dependencies` POM manages,
  and drops any the parent has caught up with — the plan had said this happened "per the Release Checklist", but no
  step did it. Progresses `improvement-plan.md`'s Gap #26: the `tomcat.version` override stays until a Spring Boot GA
  release manages Tomcat `11.0.25` or later

##### Documentation

- **`HISTORY.md`, `ARCHITECTURE.md`, `README.md`:** The Short-term roadmap no longer plans entity-level unit tests
  for the whole domain model; `ARCHITECTURE.md`'s test tree gains a `domain/` entry and `README.md`'s unit-test
  categories now include entities
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** Gap #25 closed in v8.10.0 and moved to ✅ Completed, with
  "🌳 At a Glance", the "🛤️ Roadmap" **Next** row and "☑️ Success Criteria" updated to match

- **`README.md`, `CHANGELOG.md`:** The Semantic Versioning note and the Version Policy section now point at
  `AGENTS.md`'s Semantic Versioning section as the definition of each release level, rather than calling the
  Version Policy the full policy

##### Build & Metadata

- Project version bumped to **8.10.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

#### 🐛 Fixed

##### Documentation

- **`AGENTS.md`, `CONTRIBUTING.md`, `README.md`:** The database-profile docs said a run with no profile needs
  only `MYSQL_USER`/`MYSQL_PASSWORD`, but `application.properties` sets no `spring.datasource.url`, so
  the URL must be supplied externally (e.g. `SPRING_DATASOURCE_URL`) — now stated in `AGENTS.md`'s run command and
  `CONTRIBUTING.md`'s Database Profiles table. The "regardless of profile" credentials wording now excludes `local`,
  which connects as `hpsc_dev` with `MYSQL_LOCAL_PASSWORD`, and `CONTRIBUTING.md`'s Database Profiles table gains
  `local` and `prod` rows. Closes `improvement-plan.md`'s Gap #27

#### 🗑️ Removed

##### Configuration

- **`logback-spring.xml`:** Removed the `staging` `<springProfile>` block and its `logs/application-staging.log`
  appender — no staging environment exists, there was no `application-staging.properties` behind it and no doc
  mentioned it, so `staging` had no datasource URL of its own and couldn't start without one supplied externally.
  The remaining blocks (`default`, `dev`, `local`, `prod`, `test`) each match a documented profile; closes
  `improvement-plan.md`'s Gap #28

### 🧾 [8.9.0] - 2026-09-26

#### ➕ Added

##### Domain

- **`Competitor.paidUpSapsa`, `Competitor.paidUpClub`:** New nullable `Boolean` columns — whether a competitor's
  SAPSA and club memberships are paid up
- **`IpscMatch.stages`:** New `@OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)`
  collection — the domain model's only bidirectional, cascaded relationship, since a stage can't exist without its
  match; no schema change

##### Repositories

- **`IpscMatchRepository.findByIdWithClub`, `findAllWithClub`:** New `left join fetch` queries loading a match's
  `club` with it
- **`CompetitorRepository.findByIdWithHomeClubAndEmailAddresses`, `findAllWithHomeClubAndEmailAddresses`:** New
  `left join fetch` queries loading a competitor's `homeClub` and `emailAddresses` with it

##### Services

- **`TransactionService`, `TransactionServiceImpl`:** New service that commits competitor and match writes —
  `saveCompetitor`/`saveCompetitors`/`deleteCompetitor` and `saveMatch`/`saveMatches`/`deleteMatch`, plus a
  `saveMatch` overload that replaces or upserts an existing match's stages — each in its own explicit
  `TransactionTemplate` transaction, returning entities with the associations their responses read already loaded

##### API Models

- **`CompetitorRequest`, `CompetitorRequestForCSV`, `CompetitorResponse`:** New `paidUpSapsa`/`paidUpClub` fields;
  an omitted flag is stored as `null` on create/update and left unchanged on patch

##### Database

- **`V7_8_0__add_competitor_paid_up_flags.sql`:** New Flyway migration — adds nullable `paid_up_sapsa` and
  `paid_up_club` `BOOLEAN` columns to `competitor`

##### Tests

- **`IpscCompetitorServiceIntegrationTest`, `IpscCompetitorServiceTest`, `IpscCompetitorServiceImplTest`,
  `CompetitorRequestTest`, `CompetitorRequestForCSVTest`:** Cover the new paid-up flags
- **`TransactionServiceImplTest`:** New unit tests for every commit method, including rollback on failure, and the
  stage replace/upsert logic moved from `IpscMatchServiceImplTest`
- **`IpscMatchServiceIntegrationTest`, `IpscCompetitorServiceIntegrationTest`:** New tests run with no surrounding
  transaction, so each create/update/patch/delete must really commit and be readable by the next call, plus a check
  that a bulk match import with one bad row commits nothing
- **`TransactionServiceTest`, `TransactionServiceIntegrationTest`:** Complete `TransactionService`'s 3-tier test
  split — the contract through the interface, and real H2 commits/rollbacks in an integration test that is
  deliberately not `@Transactional`, so a surrounding test transaction can't hide whether each write commits
- **`IpscMatchRepositoryIntegrationTest`, `CompetitorRepositoryIntegrationTest`,
  `MatchCompetitorRepositoryIntegrationTest`, `MatchStageCompetitorRepositoryIntegrationTest`,
  `ShooterLogRepositoryIntegrationTest`, `ShooterLogCompetitorRepositoryIntegrationTest`:** New repository tests for the
  fetch-join queries, `IpscMatch.stages`' cascade persist/orphan removal/cascade delete, the email collection's delete
  and every `existsBy…` check behind the reject-not-cascade deletes, with a shared `ScoringFixtures` helper

##### Documentation

- **`improvement-plan.md`:** New Gaps #14–#17 from an `update-improvement-plan-gaps` sweep — `flyway-migration-
  versioning.md`'s Current State table stops five migrations short of what's on disk (#14); `ARCHITECTURE.md`'s
  Quality Attributes table contradicts its own Persistence Layer section on JPA cascade/`mappedBy`, and misattributes
  database-profiles documentation to `README.md` instead of `CONTRIBUTING.md` (#15); `CONTRIBUTING.md`'s Running
  Tests example names a renamed-away test method (#16); `HISTORY.md`'s Future Roadmap Implications "Recently
  Completed" log hasn't been extended since v8.4.0, missing nine shipped releases (#17). "🌳 At a Glance", "🛤️
  Roadmap" **Next** row and "☑️ Success Criteria" updated to match
- **`improvement-plan-tasks.md`:** New "⚪ Open" checkbox blocks for Gaps #14–#17
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gaps #18–#24 from the v8.9.0 release-prep
  `update-improvement-plan-gaps` sweep, all closed within this release (see Fixed and Removed)

#### 🔄 Changed

##### Controllers

- **`IpscCompetitorController.createCompetitors`:** The competitor CSV header now requires trailing `PaidUpSapsa` and
  `PaidUpClub` columns (a row may leave them empty); existing CSV files need the two header columns added

##### Domain

- **`Competitor.emailAddresses`:** The `email_address` element column mapping no longer declares `nullable = false`
- **`Competitor`, `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`, `MatchStageCompetitor`, `ShooterLog`,
  `ShooterLogCompetitor`:** Every `@ManyToOne` association now uses `FetchType.LAZY` instead of `FetchType.EAGER` —
  loading an entity no longer pulls in its whole parent chain, reversing v8.7.0's switch to eager fetching

##### Services

- **`IpscMatchServiceImpl`, `IpscCompetitorServiceImpl`:** No longer `@Transactional` — they validate requests and
  build or modify entities outside any transaction, then commit through `TransactionService`. Their bulk CSV imports
  now validate and build every row before saving any, then save all rows in one transaction, so they stay
  all-or-nothing. Stage replace/upsert logic moved into `TransactionServiceImpl`, and create/update/patch responses
  now list stages ordered by stage number, matching `getMatch`
- **`IpscMatchServiceImpl`, `IpscCompetitorServiceImpl`:** `findMatchOrThrow`/`getAllMatches` and
  `findCompetitorOrThrow`/`getAllCompetitors` now load through the new fetch-join queries, so `toResponse` can read
  the club, home club and email addresses outside a transaction — `spring.jpa.open-in-view` is disabled, so a
  lazily-loaded association would otherwise throw `LazyInitializationException`
- **`IpscMatchServiceImpl.deleteMatch`:** Now commits the delete through `TransactionService.deleteMatch`, with the
  match's stages removed by `IpscMatch.stages`' cascade rather than explicitly; the reject-not-cascade checks for
  results and shooter logs are unchanged
- **`TransactionServiceImpl.replaceStages`, `upsertStages`:** Moved from `IpscMatchServiceImpl`, and now work on the
  managed match's `IpscMatch.stages` collection; `replaceStages` removes the old stages as managed entities and
  flushes before inserting the replacements, instead of `deleteAllInBatch`'s bulk query

##### Tests

- **`TransactionServiceImplTest`:** Now covers only the impl's protected helpers (`loadAssociations`, `replaceStages`,
  `upsertStages`); its public-contract tests moved to the new `TransactionServiceTest`
- **`IpscMatchServiceTest`, `IpscCompetitorServiceTest`:** Build their service with a real `TransactionServiceImpl`
  over the same repository mocks and a mocked `PlatformTransactionManager`; match tests now seed existing stages on
  the match's `stages` collection instead of stubbing `findAllByMatchIdOrderByStageNumber`

##### CI/CD & Configuration

- **`claude-code-review.yml`:** The commented-out `paths:` filter example still named its template's
  TypeScript/JavaScript globs — now `src/**/*.java`, `src/main/resources/**` and `pom.xml`, still left commented out

##### Tooling

- **`prep-version-release`, `AGENTS.md`:** The Release Checklist's `HISTORY.md` step now makes updating "Major
  Version Goals" mandatory for every release — extending the current major version's range and narrative, or adding
  a new entry for a new major version — rather than leaving it unmentioned

##### Documentation

- **`ARCHITECTURE.md`, `AGENTS.md`, `CONTRIBUTING.md`, `IpscMatchService`, `IpscCompetitorService`:** Describe
  `TransactionService` as where writes are committed, replacing the `@Transactional` services, and the bulk imports'
  validate-everything-then-save-in-one-transaction behaviour

##### Build & Metadata

- Project version bumped to **8.9.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

#### 🐛 Fixed

##### Documentation

- **`flyway-migration-versioning.md`:** Related Documentation pointed to `ARCHITECTURE.md` for content that actually
  lives in `CONTRIBUTING.md`'s Database Profiles section — removed the incorrect bullet and folded its claim into the
  `CONTRIBUTING.md` entry
- **`improvement-plan.md`:** Reverted "low-regression backstop" to "low regression backstop" — the hyphen bound "low"
  to "regression" instead of the intended "low [threshold], regression backstop" reading, nearly inverting the
  meaning
- **`ARCHITECTURE.md`, `CONTRIBUTING.md`, `AGENTS.md`:** The CI/CD & Quality Gates documentation left out the two
  live Claude Code workflows — `ARCHITECTURE.md`'s table gains "Automated Code Review" (`claude-code-review.yml`, every
  PR, advisory) and "AI Assistant" (`claude.yml`, on `@claude` mention) rows plus a note on the
  `CLAUDE_CODE_OAUTH_TOKEN` secret, its Project Structure tree's `.github/workflows/` comment is widened, and the other
  two files' summaries match, closing `improvement-plan.md`'s Gap #13
- **`flyway-migration-versioning.md`:** Current State table extended with the five migrations missing since v8.4.0
  (`V7_4_0` through `V7_8_0`); a new step 5 in "🔢 Choosing the Next Version" now has the next migration's author add
  its own row, closing `improvement-plan.md`'s Gap #14
- **`ARCHITECTURE.md`:** The Quality Attributes table's "Data Integrity" row claimed JPA cascade rules and
  bidirectional `mappedBy` declarations the domain model didn't have, contradicting the Persistence Layer section —
  both now describe `IpscMatch.stages` as the one cascaded relationship, and the Development Guidelines paragraph
  points at `CONTRIBUTING.md`, not `README.md`, for database profiles, closing `improvement-plan.md`'s Gap #15
- **`CONTRIBUTING.md`:** The Running Tests single-method example named
  `AwardControllerTest#testProcessCsv_whenValidCsvData_thenReturns200`, which was renamed away — now
  `testCreateAwards_whenValidCsvData_thenReturns200`, closing `improvement-plan.md`'s Gap #16
- **`HISTORY.md`:** "Major Version Goals"' Version 8.x entry stopped at v8.5.1 — its range now runs to v8.8.0, and
  its narrative covers v8.6.0 – v8.8.0's match/competitor API completion, platform tidy-up and documentation work
- **`HISTORY.md`:** The Future Roadmap Implications "Recently Completed" log stopped at v8.4.0 — added entries for
  v8.4.1 through v8.8.0 and updated the section's opening sentence to v8.8.0, closing `improvement-plan.md`'s Gap #17
- **`AGENTS.md`, `prep-version-release`:** The Release Checklist's `HISTORY.md` step now updates the Future Roadmap
  Implications log for every release, rather than only "if the release is significant enough" — the condition that
  let it fall nine releases behind
- **`README.md`, `ARCHITECTURE.md`, `AGENTS.md`:** Tech-stack lines no longer claim XML processing or Apache Commons
  (Gap #18)
- **`ARCHITECTURE.md`:** The Key Design Patterns table's "Strategy Pattern" row described CSV/XML converters that
  never existed — replaced with a "Transaction Boundary" row for `TransactionService`, and the Extensibility row now
  credits the enum `AttributeConverter`s (Gap #19)
- **`AGENTS.md`:** The 3-tier test rule named four services — it now names all five, including `TransactionService`,
  and records its integration test's deliberate lack of `@Transactional` (Gap #20)
- **`ARCHITECTURE.md`:** The overview and request-flow diagrams now route writes through `TransactionService`, the
  bulk-import flows describe saving every row in one transaction, the stale "removed pending a rebuild" note is gone,
  and the repositories' tree comment and examples match their real wiring and queries (Gap #21)
- **`AGENTS.md`:** Expanded HPSC as "Handgun and Practical Shooting Club" — now "Hartbeespoortdam Practical Shooting
  Club", matching every other source (Gap #22)
- **`HISTORY.md`:** Dropped the Short-term roadmap's `Competitor.homeClub` backfill — no column reliably identifies a
  competitor's home club, so it's closed as not applicable (Gap #23) — and narrowed the test-coverage bullet to the
  entity-level unit tests still missing
- **`README.md`:** The Testing section claimed domain-entity unit tests and repository tests that didn't exist — it
  now describes the suite as it is, including the new repository integration tests (Gap #24)

#### 🗑️ Removed

##### Build & Metadata

- **`jackson-dataformat-xml`, `commons-lang3`:** Unused dependencies dropped — nothing in `src/` produced or consumed
  XML or used Apache Commons, so dropping `jackson-dataformat-xml` only removes Spring MVC's unused XML content
  negotiation; closes `improvement-plan.md`'s Gap #18

### 🧾 [8.8.0] - 2026-09-24

#### ➕ Added

##### Controllers

- **`IpscCompetitorController.deleteCompetitor`, `IpscMatchController.deleteMatch`:** New
  `DELETE /ipsc/competitors/{competitorId}` and `DELETE /ipsc/matches/{matchId}` endpoints, returning
  `204 No Content` — `400` when the record is still referenced, `404` when it doesn't exist

##### Services

- **`IpscCompetitorService.deleteCompetitor`:** Deletes a competitor together with their email addresses, refusing
  with a `ValidationException` while any `MatchCompetitor` (match result) or `ShooterLog` row still references them
- **`IpscMatchService.deleteMatch`:** Deletes a match together with its stages, refusing with a
  `ValidationException` while any `MatchCompetitor`, `MatchStageCompetitor` or `ShooterLogCompetitor` row still
  references it — so scoring history is never deleted as a side effect
- **`IpscCompetitorServiceImpl.deleteCompetitor`, `IpscMatchServiceImpl.deleteMatch`:** The delete is flushed inside
  the method and a `DataIntegrityViolationException` rethrown as a `ValidationException`, so a reference added by
  another request between the dependent-row checks and the delete still returns `400` rather than `500`

##### Repositories

- **`MatchCompetitorRepository`, `MatchStageCompetitorRepository`, `ShooterLogRepository`,
  `ShooterLogCompetitorRepository`:** New `existsByCompetitorId`/`existsByMatchId`/`existsByMatchStageMatchId`
  queries backing the delete operations' dependent-row checks

##### Documentation

- **`improvement-plan.md`:** New Gap #13 — `.github/workflows/`' `claude.yml` (`@claude` assistant) and
  `claude-code-review.yml` (automated review on every PR) are live but missing from `ARCHITECTURE.md`'s CI/CD &
  Quality Gates table, its Project Structure tree comment and `CONTRIBUTING.md`'s summary of that table; the
  "🌳 At a Glance" list, "🛤️ Roadmap" **Next** row and "☑️ Success Criteria" updated to match
- **`improvement-plan-tasks.md`:** New "⚪ Open" checkbox block for Gap #13 — document both workflows and the
  `CLAUDE_CODE_OAUTH_TOKEN` secret they rely on, and optionally tidy the review workflow's template `paths:` comment

#### 🔄 Changed

##### Documentation

- **`ARCHITECTURE.md`:** New Service Layer note on the delete rule — a competitor's emails and a match's stages are
  removed with it, but a record still referenced by scoring or shooter-log rows is refused rather than cascaded
- **`standard-rest-conventions.md`:** "🔍 Current State in This Codebase" now names `IpscCompetitorController`
  alongside `IpscMatchController` as full-pattern examples, covering every verb including `getAll` and `delete`
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** Gap #12 closed in v8.8.0 by the new delete operations and
  moved to ✅ Completed; "🌳 At a Glance", the "🛤️ Roadmap" table's **Next** row and "☑️ Success Criteria" updated
  to match

##### Build & Metadata

- Project version bumped to **8.8.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

### 🧾 [8.7.0] - 2026-09-24

#### ➕ Added

##### Controllers

- **`IpscCompetitorController.getAllCompetitors`:** New `GET /ipsc/competitors` endpoint returning every IPSC
  competitor as a JSON array of `CompetitorResponse`s — the collection counterpart to `GET /{competitorId}`,
  mirroring `IpscMatchController.getAllMatches`

##### Services

- **`IpscCompetitorService.getAllCompetitors`:** Returns every persisted competitor mapped to a
  `CompetitorResponse`, or an empty list when there are none

##### Documentation

- **`improvement-plan.md`:** New Gap #12 — `README.md`/`ARCHITECTURE.md` describe competitors and matches as "full
  CRUD", yet neither `IpscCompetitorController`/`IpscMatchController` nor their services expose a delete operation;
  `standard-rest-conventions.md`'s current-state examples also omit `getAllMatches`/`IpscCompetitorController`
- **`improvement-plan-tasks.md`:** New "⚪ Open" checkbox block for Gap #12 — decide on a delete operation (with
  explicit handling of dependent rows) or reword the "CRUD" claims, then refresh the REST conventions examples

#### 🔄 Changed

##### Services

- **`IpscMatchServiceImpl.parseStages`:** A match CSV's `Stages` cell now separates each entry's stage number from
  its name with `:` instead of `-` (e.g. `"1:Stage One;2:Stage Two"`); only the first `:` splits, so stage names may
  still contain one. Entries in the old `1-Stage One` form are now rejected with a `ValidationException`

##### Documentation

- **`IpscMatchController`, `MatchRequestForCSV`, `improvement-plan.md`, `improvement-plan-tasks.md`:** Bulk CSV
  Swagger example, Javadoc and roadmap references updated to the `<stageNumber>:<stageName>` format
- **`improvement-plan.md`:** "🌳 At a Glance" lists Gap #12 as a second ⚪ Open gap, the "🛤️ Roadmap" table's **Next** row
  points at it instead of the "no items currently scoped" placeholder and "☑️ Success Criteria" gains
  a matching bullet

##### Domain

- **`Competitor`, `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`, `MatchStageCompetitor`, `ShooterLog`,
  `ShooterLogCompetitor`:** Every `@ManyToOne` association switched from `FetchType.LAZY` to `FetchType.EAGER`, so
  the referenced entity is loaded along with its owner rather than on first access

##### Build & Metadata

- Project version bumped to **8.7.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match
- **`springdoc-openapi-starter-webmvc-ui`:** Bumped from `2.8.5` to `3.1.0`, the springdoc line built for Spring
  Boot 4
- **`pom.xml`:** springdoc's version now comes from an imported `springdoc-openapi-bom` in a new
  `<dependencyManagement>` section, since Spring Boot's parent doesn't manage it, so the
  `springdoc-openapi-starter-webmvc-ui` dependency no longer declares its own version

##### Configuration

- **`application.properties`:** `server.port=8081` override removed, so the app now starts on Spring Boot's default
  port `8080`; the port and Swagger/OpenAPI URLs in `README.md`, `AGENTS.md`, `ARCHITECTURE.md` and
  `CONTRIBUTING.md` updated to match

#### 🐛 Fixed

##### Controllers

- **`IpscMatchController.getAllMatches`:** Swagger's `200` response schema now documents an array of
  `MatchResponse`s via `@ArraySchema`, rather than a single `MatchResponse` object, matching the `List` the
  endpoint actually returns

##### Documentation

- **`ARCHITECTURE.md`'s Project Structure tree:** Stale comments corrected against disk — `documentation/history/`
  now describes its per-major-version subdirectories and `EVOLUTION_OVERVIEW.md`, `documentation/roadmap/` names
  `improvement-plan.md` alongside its task breakdown, the test `services/`/`services/impl/` comments match the
  3-tier service test split, and the previously missing `banner.txt` is listed

#### 🗑️ Removed

##### Build & Metadata

- **`spring-restdocs-mockmvc`:** Unused test dependency dropped from `pom.xml`, along with the Spring REST Docs
  mentions in `README.md`'s and `ARCHITECTURE.md`'s tech-stack lists
- **`HELP.md`:** Spring REST Docs reference link dropped, following the `spring-restdocs-mockmvc` removal above

### 🧾 [8.6.2] - 2026-09-24

#### 🔄 Changed

##### Documentation

- **`AGENTS.md`'s icon registry:** Restructured to match the shared project template's — its core icon table
  verbatim, then the template's backend / API service extension set as this project's own established icons and
  its component-based frontend set kept reserved (as used by `hpsc-web-vite`). Notable moves: `🛤️` now marks
  Roadmap (replacing `🗺️`, which now marks the Documentation file map) and `☑️` marks Checklist, so `✅` stays
  reserved for completed status; `🔃` splits Data flow off `🔀` (now Git workflow only); `🧭` takes design patterns
  off `🔄` (now changed items only); `✍️`/`🛠️`/`♻️`/`💰`/`👍`/`🌳`/`🏆`/`🗝️`/`⏭️`/`⏳`/`🔁`/`⚖️` move from reserved to
  core; `🗂️` moves from "Documentation file index" to reserved (frontend feature-based organisation); `🧭`'s
  concept widened to "Design notes / design patterns" to name its Key Design Patterns use
- **`AGENTS.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md`, `improvement-plan.md`, `documentation/recommendations/`:**
  Headings realigned with the new registry — Documentation Conventions `📚` → `✍️`, Documentation File Map
  `🗂️` → `🗺️`, Key Design Patterns `🔄` → `🧭`, Data Flow `🔀` → `🔃`, Development Guidelines `📚` → `🛠️`, Getting
  Started `🔧` → `🚀`, At a Glance `📋` → `🌳` and Related Documentation `📚` → `🔗`
- **`AGENTS.md`, `CONTRIBUTING.md`, `README.md`, `HISTORY.md`, `improvement-plan.md`:** "Roadmap Planning",
  "Roadmap", "Future Roadmap Implications" and "Success Criteria" headings and their Table of Contents entries
  switched to `🛤️`/`☑️` to match; earlier `CHANGELOG.md`/history entries keep the icons they were written with
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** Synced with the template's roadmap structure — title
  now "HPSC Website Backend Improvement Plan", a new note on the four kinds of gap an audit looks for, a fuller
  Related Documentation list (adding `CONTRIBUTING.md` and `improvement-plan-tasks.md`), and a note on annotating
  checked task items; the tasks file's intro no longer states a gap count that drifts with every new gap

##### Tooling

- **`sync-improvement-plan-gaps`, `update-improvement-plan-gaps` skills:** Stale `🚀 Roadmap`/`🚀 Future Roadmap
  Implications`/`✅ Success Criteria` references updated to `🛤️`/`☑️`

#### 🐛 Fixed

##### Documentation

- **`AGENTS.md`, `CONTRIBUTING.md`:** `CHANGELOG.md` heading references corrected from `## 🧪 [Unreleased]`/
  `## 🧾 [X.Y.Z]` to the `### 🧪 [Unreleased]`/`### 🧾 [X.Y.Z]` depth the file actually uses. `AGENTS.md`'s Git
  Workflow Conventions now spell out the full `#### <category>` → `##### <Area>` nesting, Area reuse and the
  bold-lead-in bullet style — reverse-synced from the shared project template
- **`HISTORY.md`:** "🛤️ Future Roadmap Implications" Short-term/Medium-term lists refreshed against what has
  actually shipped — the club-seeding bullet reduced to its still-outstanding `Competitor.homeClub` backfill half (the
  `club` table was already seeded in v8.4.0), `ShooterLogEntry` renamed to `ShooterLogCompetitor`,
  "Medium-term (v7.x+)" relabelled "Medium-term (Later v8.x Releases)" and "Bulk match processing capabilities"
  dropped as delivered by v8.3.0's bulk CSV import
- **`CONTRIBUTING.md`:** Serial-comma example corrected — its "not" half repeated the correct form ("prose, comments and
  Javadoc") instead of showing the forbidden one ("prose, comments, and Javadoc")
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #11 recorded for those stale lists, then closed in
  this same release — "🌳 At a Glance" counts, the "🛤️ Roadmap" table's **Next** row and "☑️ Success Criteria"
  updated to match

##### Tooling

- **`generate-commit-message`, `prep-version-release`, `scaffold-unit-tests`, `scaffold-integration-tests`,
  `sync-unreleased-changes` skills:** Category and Area heading depths corrected from `###`/`####` to the
  `####`/`#####` levels `CHANGELOG.md` actually uses; `generate-commit-message` also notes that security-relevant
  fixes belong under `#### 🔐 Security`

### 🧾 [8.6.1] - 2026-09-23

#### 🔄 Changed

##### Documentation

- **`HISTORY.md`:** "📖 Evolution Overview" section (the Phase-by-phase narrative) split out into new
  `documentation/EVOLUTION_OVERVIEW.md` — it had grown to roughly half of `HISTORY.md`'s 4,095 lines,
  making the file unwieldy. `HISTORY.md` keeps a short pointer section under the same heading/anchor, so its
  Table of Contents entry still resolves; "📅 Historical Timeline", "🎯 Major Milestones" and every other section
  stay in `HISTORY.md` unchanged
- **`AGENTS.md`, `README.md`:** Documentation File Map/Documentation tables updated to list
  `EVOLUTION_OVERVIEW.md` as a standing exception in `documentation/history/` — a single living file rather than
  a per-version archive
- **`AGENTS.md`'s Release Checklist, `prep-version-release` skill:** The "Extend `HISTORY.md`" step now specifies
  that the Phase entry lands in `documentation/EVOLUTION_OVERVIEW.md` while the Historical Timeline entry
  and Milestone stay in `HISTORY.md` — one paired entry per release, split across two files
- **`documentation/history/`:** All 52 archived `RELEASE_NOTES_vX.Y.Z.md`/`PR_DESCRIPTION_vX.Y.Z.md` files
  regrouped from a flat directory into `v1/` – `v8/` subdirectories by major version (e.g.
  `documentation/history/v8/RELEASE_NOTES_v8.6.0.md`), moved with `git mv` to preserve history.
  `EVOLUTION_OVERVIEW.md` is unaffected — it's a standing living file, not a per-version archive
- **`AGENTS.md`, `README.md`:** Documentation File Map/Documentation tables updated to describe the new
  `documentation/history/vN/` subdirectory grouping
- **`prep-version-release`, `generate-pr-summary`, `update-improvement-plan-gaps` skills:** Updated to read/write
  archived release docs at `documentation/history/v<major>/...` instead of the old flat path, deriving `<major>`
  from the version's leading number before the first `.`

### 🧾 [8.6.0] - 2026-09-23

#### ➕ Added

##### Domain

- **`IpscMatch.url`:** New nullable `String` column — a URL with more information about a match (e.g. a results
  page or event listing)

##### API Models

- **`MatchRequest`, `MatchRequestForCSV`, `MatchResponse`:** New nullable `url` field

##### Database

- **`V7_6_0__add_ipsc_match_url.sql`:** New Flyway migration — adds a nullable `url` column to `ipsc_match`

##### Tests

- **`IpscMatchServiceIntegrationTest`, `IpscMatchServiceTest`, `IpscMatchServiceImplTest`, `MatchRequestTest`,
  `MatchRequestForCSVTest`:** `url` now flows through each fixture/CSV row and is asserted in the create/patch/
  update/get happy-path tests, proving it round-trips through JSON, CSV import and the real H2/Hibernate/JPA layer

#### 🔄 Changed

##### Domain

- **`IpscMatch.startTime`, `IpscMatch.endTime`:** Changed from `LocalDateTime` to `LocalTime` — these were always
  time-of-day-only values alongside `scheduledDate`, so the redundant date component is dropped

##### API Models

- **`MatchRequest`, `MatchRequestForCSV`, `MatchResponse`:** `startTime`/`endTime` changed from `LocalDateTime` to
  `LocalTime`, now formatted per the new `IpscConstants.IPSC_INPUT_TIME_FORMAT` (`HH:mm`) instead of
  `IPSC_INPUT_DATE_TIME_FORMAT` (`yyyy-MM-dd HH:mm`)
- **`IpscMatchController`:** `createMatches`'s OpenAPI CSV example's `StartTime`/`EndTime` columns updated to the
  bare `HH:mm` pattern

##### Database

- **`V7_7_0__change_ipsc_match_start_end_time_to_time.sql`:** New Flyway migration — changes `ipsc_match`'s
  `start_time`/`end_time` columns from `DATETIME` to `TIME`

##### API

- **CSV bulk import (`POST /matches/csv`):** The header row must now include a `Url` column, like every other
  `MatchRequestForCSV` property — existing CSV templates need updating to add it (values may be left blank)
- **JSON/CSV `startTime`/`endTime`:** Now accepted/returned as bare `HH:mm` time-of-day values instead of
  `yyyy-MM-dd HH:mm` — existing CSV templates and API clients need updating to drop the date component

##### Tests

- **`IpscMatchServiceIntegrationTest`, `IpscMatchServiceTest`, `IpscMatchServiceImplTest`, `MatchRequestTest`,
  `MatchRequestForCSVTest`:** `startTime`/`endTime` fixtures, CSV rows and JSON payloads updated from
  `LocalDateTime`/`yyyy-MM-dd HH:mm` to `LocalTime`/`HH:mm`

##### Services

- **`IpscMatchServiceImpl`:** `applyFields`, `patchMatch`, `toRequest` and `toResponse` now carry `url` through
  between `MatchRequest`/`MatchRequestForCSV`, `IpscMatch` and `MatchResponse`

##### Documentation

- **`AGENTS.md`:** Test Conventions section now formally documents the 3-tier service test architecture
  (`<Service>Test`/`<Service>ImplTest`/`<Service>IntegrationTest`) already followed by all four services —
  previously only described piecemeal across the `scaffold-unit-tests`/`scaffold-integration-tests` skills

### 🧾 [8.5.1] - 2026-09-13

#### ➕ Added

##### Documentation

- **`improvement-plan.md`:** New Gap #10 — `HISTORY.md`'s "📖 Evolution Overview"/"🎯 Major Milestones" sections
  haven't been extended past Phase 25/Milestone 25 (v8.4.0), leaving v8.4.1, v8.4.2 and v8.5.0 without a matching
  Phase/Milestone entry despite `AGENTS.md`'s Release Checklist requiring one unconditionally for every release
- **`improvement-plan-tasks.md`:** New "⚪ Open" checkbox block for Gap #10, backfilling Phase 26/27/28 and
  Milestone 26/27/28 for v8.4.1/v8.4.2/v8.5.0

#### 🔄 Changed

##### Documentation

- **`improvement-plan.md`:** New "📋 At a Glance" section (originally added as "📋 Gap Status Summary", later
  renamed), listing every numbered gap by completion status — a quick-reference index ahead of the full per-gap
  detail in "🔍 Gaps & Improvement Opportunities"
- **`improvement-plan.md`:** "🗺️ Roadmap" table's **Now** row dropped #2/#7, both already closed, and promoted #6 (match
  scoring / shooter-log service and controller layer) up from **Next** as the only remaining open gap; **Next**
  is now unscoped
- **`improvement-plan.md`:** "📋 At a Glance" section's #6 entry corrected to say it's the current **Now** roadmap
  focus, not **Next** — stale after the Roadmap table update above promoted it
- **`improvement-plan.md`:** "🗺️ Roadmap" table's **Next** row now points at Gap #10's `HISTORY.md` Phase/Milestone
  backfill, replacing the "unscoped" placeholder
- **`improvement-plan-tasks.md`:** Intro line's gap count updated from "nine" to "ten" to include Gap #10
- **`HISTORY.md`:** New "Phase 26"/"Milestone 26" (v8.4.1), "Phase 27"/"Milestone 27" (v8.4.2) and "Phase 28"/
  "Milestone 28" (v8.5.0) entries backfilled into "📖 Evolution Overview"/"🎯 Major Milestones", summarising each
  release's existing Historical Timeline content — closes `improvement-plan.md`'s Gap #10
- **`improvement-plan.md`:** Gap #10 marked "✅ Closed in v8.5.1" with an Outcome paragraph, moved from "⚪ Open"
  to "✅ Completed"; "📋 At a Glance", the "🗺️ Roadmap" table's **Next** row and "✅ Success Criteria" updated to
  match
- **`improvement-plan-tasks.md`:** Gap #10's checkbox block moved to "✅ Completed" with its first three items
  checked off; the fourth (re-checking whether `release/v8.5.1` itself needs a Phase/Milestone) deliberately left
  unchecked, deferred to that release's own prep pass
- **`HISTORY.md`:** "Major Version Goals" subsection's Version 8.x entry extended from `v8.0.0 – v8.1.1` to
  `v8.0.0 – v8.5.1`, adding narrative for the domain broadening (multi-value competitor emails, match bulk CSV
  import, relaxed club-requirement defaults, match start/end time tracking) and documentation-process discipline
  (icon-registry/cross-reference consolidation, root-document title standardisation, `HISTORY.md`'s own
  Phase/Milestone backfill) delivered across v8.2.0 – v8.5.1
- **`HISTORY.md`:** "📖 Evolution Overview", "🎯 Major Milestones", "🏛️ Architectural Evolution" and
  "🗺️ Future Roadmap Implications" reordered to ascending (oldest-first), matching the convention already used by
  "✨ Feature Timeline" and "💡 Project Philosophy Evolution" — only "📅 Historical Timeline" keeps its
  most-recent-first order, per standard changelog convention. Architectural Evolution's `v5.3.0`/`v5.4.0` entries,
  previously stranded after `v8.0.0` in a broken mixed order, are now correctly interleaved between `v5.2.0` and
  `v6.0.0`; Future Roadmap Implications' forward-looking Short-term/Medium-term/Long-term headings are left in
  their existing position after the reordered version blocks

##### Tooling

- **`prep-version-release`, `generate-pr-summary`:** Both skills now end their drafted PR description/summary with the
  standard Claude Code attribution footer (`🤖 Generated with [Claude Code](https://claude.com/claude-code)`),
  marking them as Claude-drafted like any other PR description it opens

#### 🗑️ Removed

##### Documentation

- **`HISTORY.md`:** Stale "Document Created"/"Last Updated"/"Coverage" metadata line and the "Recent Updates"/
  "Previous Update" bold-labelled update log at the end of the Conclusion section, both badly out of date (stopped
  at v8.0.0/v5.1.0) and superseded by the Historical Timeline, Evolution Overview and Major Milestones sections
  elsewhere in the file

### 🧾 [8.5.0] - 2026-09-04

#### ➕ Added

##### Domain

- **`IpscMatch.startTime`, `IpscMatch.endTime`:** New nullable `LocalDateTime` columns — record when a match actually
  started and ended, alongside the existing `scheduledDate`

##### Documentation

- **`README.md`:** "License" heading/prose corrected to British English "Licence" (the `LICENSE.md` filename itself
  is unchanged, per `AGENTS.md`'s British English exceptions for filenames)

##### API Models

- **`MatchRequest`, `MatchRequestForCSV`, `MatchResponse`:** New nullable `startTime`/`endTime` fields, formatted per
  `IpscConstants.IPSC_INPUT_DATE_TIME_FORMAT` (`yyyy-MM-dd HH:mm`) on the two request DTOs
- **`IpscMatchController`:** `createMatches`'s OpenAPI CSV example now includes the new `StartTime`/`EndTime` columns

##### Database

- **`V7_5_0__add_ipsc_match_start_end_time.sql`:** New Flyway migration — adds nullable `start_time`/`end_time`
  columns to `ipsc_match`

##### Tests

- **`IpscMatchServiceIntegrationTest`:** `startTime`/`endTime` now flow through `validRequest`'s fixture and are
  asserted in `createMatch`/`getMatch`/`updateMatch`'s happy-path tests, plus a new
  `testPatchMatch_whenStartAndEndTimeAreProvided_thenStartAndEndTimeChange` — proves the two new columns actually
  round-trip through the real H2/Hibernate/JPA layer, not just mocked repositories
- **`IpscMatchServiceTest`:** `startTime`/`endTime` now flow through `validRequest`'s fixture and are asserted in
  `createMatch`'s and `updateMatch`'s happy-path tests; `testCreateMatches_whenSingleValidRow_...`'s CSV row now
  supplies actual `StartTime`/`EndTime` values (previously left blank) with matching assertions, closing the one
  gap where CSV import's mapping of these two fields was never verified

#### 🔄 Changed

##### API

- **CSV bulk import (`POST /matches/csv`):** The header row must now include `StartTime`/`EndTime` columns, like
  every other `MatchRequestForCSV` property — consistent with this endpoint's existing all-columns-required header
  validation, but existing CSV templates need updating to add them (values may be left blank)

##### Services

- **`IpscMatchServiceImpl`:** `applyFields`, `patchMatch`, `toRequest` and `toResponse` now carry `startTime`/
  `endTime` through between `MatchRequest`/`MatchRequestForCSV`, `IpscMatch` and `MatchResponse`

##### Documentation

- **`AGENTS.md`:** British English exception for `LICENSE.md` narrowed — only the filename and the file's own
  content stay American English "License"; every other reference to it (headings, tables, ToC entries, prose)
  now spells it "Licence", matching the rest of the project's British English convention rather than carving out
  an exception for it

#### 🐛 Fixed

##### Documentation

- **`CONTRIBUTING.md`:** Its own Serial Commas rule example ("prose, comments, and Javadoc") violated the rule it was
  illustrating — corrected to "prose, comments and Javadoc"

### 🧾 [8.4.2] - 2026-09-04

#### ➕ Added

##### Documentation

- **`AGENTS.md`:** Now states, right before its Documentation File Map, that it is this project's ultimate source of
  truth for conventions — every other file's workflow/convention guidance (`CONTRIBUTING.md` included) points back to
  it rather than restating it
- **`CONTRIBUTING.md`:** Intro now points to `AGENTS.md` for the full set of conventions AI coding agents and
  contributors follow, and states that `AGENTS.md` wins if anything else in the repository's documentation ever
  contradicts it

#### 🔄 Changed

##### Documentation

- **`CHANGELOG.md`:** Title changed from "Changelog" to "HPSC Website Backend", with a new "🧾 Change Log"
  second-level heading beneath it, matching `README.md`'s existing project name; every heading below it — Table of
  Contents, each version and their Added/Changed/Fixed/Removed/Security subsections and area sub-headers — drops one
  level to nest correctly under the new heading
- **`CONTRIBUTING.md`/`HISTORY.md`:** H1 titles gain the same "HPSC Website Backend" prefix, for consistency with
  `README.md` and the retitled `CHANGELOG.md`

### 🧾 [8.4.1] - 2026-09-04

#### ➕ Added

##### Documentation

- **`AGENTS.md`/`CONTRIBUTING.md`:** New "🧩 Claude Code Skills" section documenting the project-specific skills
  under `.claude/skills/` — `AGENTS.md` lists each skill and its purpose in a table; `CONTRIBUTING.md` adds a short
  pointer to it for new contributors. New `🧩` icon added to `AGENTS.md`'s icon registry for tooling/automation
  sections
- **`AGENTS.md`:** New "Reserved" sub-table under "Icons in headings" listing the 26 icons from the sibling
  `hpsc-web-vite` repository's own icon registry that aren't used here (frontend-specific concepts like routing,
  styling/theming, layout regions and page-content zones) — kept reserved so they're never accidentally
  repurposed here for an unrelated concept, mirroring the reserved table `hpsc-web-vite`'s own `AGENTS.md`
  already keeps for this repository's icons
- **`AGENTS.md`:** 12 more icons reserved from `hpsc-web-vite`'s registry (`✍️`, `👍`, `🌊`, `🧵`, `🧱`, `🌳`, `🏆`,
  `🗝️`, `⏭️`, `⏳`, `🔁`, `⚖️`), claimed there for frontend-specific concepts (content strategy, layout/component
  shape, decision trees, timeline markers, trade-off comparisons) since the reserved table was last synced —
  keeps the two repositories' icon registries reconciled, per the "sync icons" workflow. `hpsc-web-vite`'s own
  reserved table gains `🧬` (Data model / DTOs) in return
- **`AGENTS.md`:** New "🗺️ Roadmap Planning" section giving `documentation/roadmap/`'s file structure and gap
  status/numbering conventions a dedicated home, promoted out of the "Documentation File Map" section where it
  was previously buried as a sub-bullet — cross-links the `update-improvement-plan-gaps`/`sync-improvement-plan-gaps`
  skills and the Release Checklist's first step
- **`AGENTS.md`:** 25 previously-unregistered icons added to the icon registry table, backfilling ones already in
  real use but missing from it — `🔀` (Data flow / Git workflow), `📁` (Project / directory structure), `🏗️`
  (Layered architecture), `🌐` (Presentation / API layer), `⚡` (Service layer), `🗄️` (Database / persistence),
  `🧬` (Data model / DTOs), `📈` (Request-response flow), `📥` (Inbound / import flow), `🗺️` (Roadmap),
  `🧾` (Version entry), `💬` (Support / contact), `🤝` (Contributing guidelines), `📅` (Timeline), `💡`
  (Philosophy / rationale), `🎓` (Conclusion), `⭐` (Key highlights), `📦` (What's new / key changes), `📊`
  (Statistics), `🧭` (Design notes), `🔮` (Future enhancements), `👥` (Contributors), `📝` (Notes), `🛡️`
  (Robustness / validation hardening, reused across several past releases' Key Highlights) and `🔓` (Optional /
  relaxed constraint)
- **`AGENTS.md`:** `🟡` (Partially completed) and `⚪` (Open / not started) added to the icon registry table,
  completing the three-way status scheme alongside the already-registered `✅`, used throughout
  `documentation/roadmap/improvement-plan.md` and `improvement-plan-tasks.md`
- **`AGENTS.md`:** `🤔` (Reasoning), `🔢` (Numbering / sequence) and `🏷️` (Naming convention) added to the icon
  registry table, backfilling icons already used in `documentation/recommendations/flyway-migration-versioning.md`
  and `standard-rest-conventions.md`
- **`AGENTS.md`:** `🗂️` (Documentation file index) and `🌲` (Evergreen documentation) added to the icon registry
  table for the "Documentation File Map" and "Evergreen Documentation" headings, which had no registry entry

#### 🔄 Changed

##### Documentation

- **`documentation/recommendations/flyway-migration-versioning.md`/`standard-rest-conventions.md`:** "Overview"
  headings (and their Table of Contents entries) now use `📖`, already registered in `AGENTS.md`'s icon table for
  "Introduction / overview", instead of `🌐` — which is registered for the unrelated "Presentation / API layer"
  concept
- **`ARCHITECTURE.md`:** "Presentation Layer" and "Model Layer" headings (and their Table of Contents entries)
  now use dedicated `🌐`/`🧬` icons instead of `📊`/`📦`, which were already registered in `AGENTS.md`'s icon
  table for the unrelated "Statistics" and "What's new / key changes" concepts
- **`AGENTS.md`:** `📋`'s registered concept widened from "Prerequisites" to "Prerequisites / policy", since
  `CHANGELOG.md`'s "Version Policy" heading reuses it for a distinct but compatible checklist-style concept
- **`AGENTS.md`:** Reordered the icon registry table under "Icons in headings" so icons group by the doc(s) that
  established them — `README.md`/`ARCHITECTURE.md` first, then `AGENTS.md`/`CONTRIBUTING.md`, then
  `CHANGELOG.md`/`HISTORY.md`, then `RELEASE_NOTES.md`/PR description — rather than their prior arbitrary order
- **`CONTRIBUTING.md`:** "Roadmap" section condensed to a short pointer at `AGENTS.md`'s new "Roadmap Planning"
  section (matching the "Documentation Conventions" section's existing highlights-and-link pattern), instead of
  duplicating the full file-structure and status-convention detail now maintained in one place
- **`AGENTS.md`:** `✅`'s registered concept broadened from "Quality attributes" to "Quality attributes /
  completed", since it's also the established icon for the "Completed" status alongside `🟡`/`⚪` throughout
  `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md`
- **`AGENTS.md`/`CONTRIBUTING.md`:** Git Workflow's "Merging" subsection — which PR-merge strategy, tagging and
  branch-clean-up steps each branch type gets — removed from `AGENTS.md` and consolidated as `CONTRIBUTING.md`'s
  sole, canonical copy, since it describes a human contributor's GitHub mechanics rather than something referenced
  by any Claude Code skill; `AGENTS.md`'s Branching Model bullets now point to `CONTRIBUTING.md`'s Merging section
  instead of duplicating it. `CONTRIBUTING.md`'s copy also gains a proper link to its own "Cutting a Release"
  section and fixes a stray "commit (s)" typo
- **`CONTRIBUTING.md`:** Documentation Conventions section's intro now links to `AGENTS.md`'s specific "Documentation
  Conventions" section anchor instead of the bare file, matching the highlights-and-link pattern every other such
  section (Roadmap, Claude Code Skills, Cutting a Release, Architecture at a Glance) already links precisely
- **`CONTRIBUTING.md`:** Its Documentation Conventions highlights' five remaining unlinked "see `AGENTS.md`'s X
  rule" mentions (British English's exceptions list, Serial commas, Line wrapping, the icon registry, Javadoc) now
  link to the specific `AGENTS.md` subsection anchor each one names, instead of plain backtick text
- **`CONTRIBUTING.md`:** Condensed the remaining full/near-verbatim duplicates of `AGENTS.md` content into
  highlights-and-link references, matching the pattern already used elsewhere in this file: the Branching Model
  (GitFlow) bullets (kept in `AGENTS.md` only, since `sync-unreleased-changes`/`sync-improvement-plan-gaps` read it
  there for hotfix-branch detection), the Git Workflow "Conventions" and Directory Tree Maintenance bullets, the
  Exception handling bullet under Architecture at a Glance, and the CHANGELOG-same-change and Evergreen/reverse-sync
  bullets under Documentation Conventions — each now links to the specific `AGENTS.md` anchor instead of restating
  it. Also condensed the CI/CD & Quality Gates table into a link to `ARCHITECTURE.md`'s own table, its actual source
  of truth (per `AGENTS.md`'s own cross-reference), since the two had drifted to slightly different wording
- **`CONTRIBUTING.md`:** Getting Started's app-URL/Swagger-URL sentence restates `AGENTS.md`'s Project Overview
  bullets verbatim without citing it — now links to `AGENTS.md#-project-overview`, matching the same fix just made
  in the sibling `hpsc-web-vite` repository's `CONTRIBUTING.md` for its analogous routing bullet

#### 🐛 Fixed

##### Documentation

- **`CHANGELOG.md`:** Removed a duplicate, truncated `## 🧾 [5.0.0] - 2026-02-24` section that preceded the real,
  complete one — the duplicate heading text meant GitHub suffixed the second heading's anchor, so the Table of
  Contents' "Version 5.0.0" link only ever reached the incomplete copy
- **`documentation/history/RELEASE_NOTES_v6.0.0.md`/`v7.0.0.md`/`v7.2.0.md`/`v8.0.0.md`:** Five archived
  subheadings reused an already-registered icon for an unrelated concept — `📂`→`📁` (Project/directory
  structure), `📅`→`🔢` (collided with Timeline; the heading is actually about numbering), `🔒`→`🔐`
  (Security), `👤`→`🧬` (collided with Author; the heading is actually about a domain enum/DTO), `📈`→`🧪`
  (collided with Request-response flow; the heading is actually about test coverage)
- **`AGENTS.md`:** "Documentation Conventions" heading used `📝`, which is registered for the unrelated "Notes"
  concept, and didn't match `CONTRIBUTING.md`'s equivalent heading (`📚`) for the same section — switched to `📚`
  in both places for consistency
- **`AGENTS.md`:** "Documentation File Map" and the new "Roadmap Planning" section both used `🎯`/`🗺️`
  inconsistently with each other and with `CONTRIBUTING.md`'s `🗺️ Roadmap` heading — "Roadmap Planning" now uses
  `🗺️` (matching `CONTRIBUTING.md` and the registry's "Roadmap" concept), and "Documentation File Map" switches to
  a new dedicated `🗂️` icon instead of competing for `🗺️` within the same file's own Table of Contents
- **`README.md`:** "API Documentation" and "Documentation" were two separate headings both using `📚` within the
  same Table of Contents — switched "API Documentation" to `🌐`, already registered for the closely related
  "Presentation / API layer" concept
- **`AGENTS.md`:** `🔄`'s registered concept corrected from "Changed items / data flow" to "Changed items /
  design patterns" — `ARCHITECTURE.md`'s actual "Data Flow" heading uses `🔀`, not `🔄`; `🔄` is really used for
  "Key Design Patterns", which the old description never reflected
- **`HISTORY.md`:** "Future Roadmap Implications" heading used `🚀` instead of `🗺️`, despite naming the same
  Roadmap concept `🗺️` is registered for and used consistently everywhere else — switched to `🗺️`
- **`RELEASE_NOTES.md`/`documentation/history/RELEASE_NOTES_v5.2.0.md`…`v8.4.0.md`:** "Migration Guide" heading
  used `🔄` instead of `🚀`, the icon `CHANGELOG.md`'s equivalent "Upgrade Guide" heading already uses for the
  same underlying concept — switched to `🚀` across the current file and all 17 archived releases sharing this
  same pre-existing inconsistency, at the user's explicit request to extend the fix to the archives
- **`AGENTS.md`:** `🐛`'s registered concept broadened from "Fixed items" to "Bugs / fixed items", since
  `RELEASE_NOTES.md`'s "Known Issues" heading (28 of 30 archived releases) consistently reuses it for open,
  not-yet-fixed bugs — a deliberate, long-standing pattern the narrower description didn't account for
- **`documentation/roadmap/improvement-plan.md`:** Its own "Roadmap" heading and both inline references to
  `HISTORY.md`'s "Future Roadmap Implications" section used `🚀` instead of `🗺️` — the same inconsistency just
  fixed in `HISTORY.md` itself, now corrected here too, plus a pre-existing line-wrap violation nearby
- **`AGENTS.md`/`CONTRIBUTING.md`:** Serial Commas rule's own example was broken in both files — the "e.g." phrase
  and its "not" contrast phrase were identical (e.g. "clubs, competitors and matches", not "clubs, competitors and
  matches"), so it never actually illustrated the rule; the "not" side now shows the Oxford-comma version it's
  meant to contrast against

### 🧾 [8.4.0] - 2026-09-03

#### ➕ Added

##### Database

- **`V7_3_0__seed_club_data.sql`:** New Flyway migration seeds the `club` table with the 5 named
  `ClubIdentifier` constants (`SOSC`, `HPSC`, `PMPSC`, `VISITOR`, `ALL`); `UNKNOWN` is the enum's
  default/unmatched placeholder rather than a real club, so it's intentionally excluded. `name`/
  `abbreviation` come from `ClubIdentifier.name`/`abbreviation`, and `identifier` is set to the
  same abbreviation, matching `ClubIdentifierConverter`'s persisted representation

##### Enums

- **`ClubIdentifier`:** New `ALL` constant (`"Eufees Clubs"` / `"All"` / `"ALL"`) alongside the
  existing club constants; class Javadoc extended to explain that `ALL` is used in the Match
  domain to indicate a match hosted jointly by the three real clubs (`SOSC`, `HPSC`, `PMPSC`)
  rather than by a single one of them

#### 🔄 Changed

##### API

- **`IpscCompetitorController`/`IpscCompetitorServiceImpl`:** `clubNumber` is no longer unconditionally required on
  competitor create/update — it's now required only when the competitor's home club is HPSC, and forced to
  `null` for every other home club, including none, regardless of what a request supplies. `CompetitorRequest`'s
  `clubNumber` is no longer a Jackson-required property. New `IpscCompetitorServiceImpl.resolveClubNumber()`
  centralises the rule for `createCompetitor()`/`updateCompetitor()` (via `applyFields()`) and `patchCompetitor()`,
  which re-applies it whenever a patch touches `homeClub` or `clubNumber`
- **`IpscMatchController`/`IpscMatchServiceImpl`:** `club` is no longer unconditionally required on match
  create/update — a missing or blank `club` now resolves to `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER`
  (`ClubIdentifier.ALL`, the seeded `"Eufees Clubs"` club) instead of `validateForCreate` throwing
  `ValidationException("Club is required.")`. `IpscMatchServiceImpl.resolveClub()` now mirrors
  `IpscCompetitorServiceImpl.resolveHomeClub()`/`resolveClubNumber()`'s "apply the domain default" pattern,
  throwing `NonFatalException` if even the default club is missing from the database, or a new `FatalException`
  if `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` itself is null — a defensive check, since the constant is
  always `ClubIdentifier.ALL` today. `IpscMatchService`'s `createMatch`/`updateMatch`/`patchMatch` (and
  `IpscMatchController`'s matching endpoints) now declare `throws FatalException` to carry this; `resolveClub()`/
  `applyFields()` do the same on the impl side. `resolveClub(String)` now delegates to a new
  `resolveClub(String, ClubIdentifier)` overload that takes the default identifier as a parameter rather than
  reading the constant directly — the same "stays unit testable if that constant were ever null" pattern
  `IpscCompetitorServiceImpl.isHpscMember()` already uses for `HOME_CLUB_IDENTIFIER`. Closes
  `documentation/roadmap/improvement-plan.md`'s Gap #9

##### Configuration

- **`pom.xml`:** `jacoco-maven-plugin`'s `check` execution `LINE`/`COVEREDRATIO` minimum raised from `0.86` to
  `0.97`, now genuinely near the real baseline (98.16%/98.94% line/branch, 836 tests as of that pass, reconfirmed
  unchanged by a fresh `./mvnw verify -Pcoverage` run) after the 86% floor was confirmed holding cleanly in CI on
  both the `develop` and `main` `build.yml` runs that shipped v8.3.1. The suite continued to grow afterwards, within
  the same v8.4.0 branch; a final `./mvnw verify -Pcoverage` re-run at this release's prep time measured
  98.44%/98.98% line/branch, 868 tests — still comfortably clear of the new floor. Closes
  `documentation/roadmap/improvement-plan.md`'s Gap #4

##### Constants

- **`SystemConstants`:** New `TIME_FORMAT` (`"HH:mm"`), `DEFAULT_DATE_FORMAT` (alias for `ISO_DATE_FORMAT`) and
  `DEFAULT_DATE_TIME_FORMAT` (alias for `ISO_DATE_TIME_FORMAT`) constants; `ISO_DATE_TIME_FORMAT` now composes
  from the new `TIME_FORMAT` instead of an inline `" HH:mm"` literal — same resulting `"yyyy-MM-dd HH:mm"` value.
  Unused `LONG_DATE_FORMAT`/`LONG_DATE_TIME_FORMAT` dropped; nothing in the codebase referenced either
- **`SystemConstants`, `IpscConstants`:** Every constant actually referenced elsewhere in the codebase gained a
  one-line field Javadoc explaining its purpose (`DEFAULT_SCALE`, `TIME_FORMAT`, `ISO_DATE_FORMAT`,
  `ISO_DATE_TIME_FORMAT`, `DEFAULT_DATE_FORMAT`, `ARRAY_SEPARATOR`, `IPSC_INPUT_DATE_FORMAT`,
  `DEFAULT_MATCH_CLUB_IDENTIFIER`, `HOME_CLUB_ABBREVIATION`, `HOME_CLUB_IDENTIFIER`). Fields with no reference
  anywhere outside their own declaration (e.g. `EXCLUDE_ICS_ALIAS`, `MAX_SAPSA_NUMBER`, `STAGE_POINTS_SCALE`,
  `DEFAULT_MATCH_CATEGORY`) were deliberately left undocumented rather than inventing a rationale for dead code

##### Controllers

- **`IpscCompetitorController`:** Javadoc's hardcoded "HPSC" mentions in each `@throws ValidationException`
  description replaced with `{@link IpscConstants#HOME_CLUB_ABBREVIATION}` links, matching the constants
  centralisation above

##### Database

- **`Competitor.clubNumber`:** Column relaxed from `nullable = false` to nullable, matching the new HPSC-only
  requirement above; the `uk_competitor_club_number` unique constraint is unchanged, since MySQL/H2 treat
  multiple `NULL`s as distinct under a `UNIQUE` constraint. New `V7_4_0__make_club_number_nullable.sql` migration
  relaxes the column and clears `club_number` on any existing competitor whose home club isn't HPSC

##### Documentation

- **`AGENTS.md`:** New Tech Stack note explaining that `db/migration/V<X>_<Y>_<Z>__*.sql` filenames are their own
  independent counter, baselined at `7.0.0`, and do not track `pom.xml`'s app version — prompted by
  `V7_2_0__add_competitor_emails.sql` actually shipping in app v8.2.0, coinciding by name only with the wholly
  unrelated app release v7.2.0
- **`documentation/recommendations/flyway-migration-versioning.md`:** New recommendations doc expanding on that
  note — why independent versioning is the right call for this project, how to choose the next migration's version
  number and a table of every existing migration against the app version it actually shipped in. Documentation
  File Map's `documentation/recommendations/` row updated to reference it
- **`AGENTS.md`:** New REST conventions subsection under Architecture, condensing
  `standard-rest-conventions.md`'s URL path/handler method naming rules into an actual convention rather than
  leaving them purely non-binding; that recommendations doc's intro updated to point back to it
- **`CONTRIBUTING.md`:** New REST endpoint/method naming bullet added to the "Rules enforced by convention" list
  (mirrors `AGENTS.md`'s new subsection); Database Profiles section's Flyway migration guidance extended to note
  that the version number is independent of the app version, per `AGENTS.md`'s Tech Stack note
- **`AGENTS.md`:** New Release Checklist step 9 verifies `ARCHITECTURE.md`'s Project Structure tree against the
  actual repository structure at every release — a backstop for the per-change Directory Tree Maintenance rule,
  which this branch caught slipping (the tracked `.claude/skills/` directory and the removed `HpscConstants` class
  both went stale in the tree before being caught here). Steps 9/10 renumbered to 10/11
- **`AGENTS.md`:** New Member ordering subsection under Architecture — constructors, then public methods, then (in
  a non-`final`, extendable class) protected methods, then private methods last, keeping each visibility group's
  existing relative order rather than alphabetising (that stricter rule stays specific to test classes, per Test
  Conventions). `CONTRIBUTING.md`'s "Rules enforced by convention" list gained a matching condensed bullet
- **`documentation/roadmap/improvement-plan.md`, `improvement-plan-tasks.md`:** "Gaps & Improvement Opportunities"
  restructured from a flat, inline-status numbered list into three explicit sections — ✅ Completed, 🟡 Partially
  Completed, ⚪ Open (gap headers demoted from `###` to `####`, numbers unchanged and never resequenced) — so a
  reader can see what's still open at a glance; `improvement-plan-tasks.md`'s checkbox lists mirror the same three
  sections instead of the previous Now/Next/Later/Ongoing phasing, which had become mostly historical now that
  eight of nine gaps are closed. The forward-looking Now/Next/Later/Ongoing Roadmap table is unchanged — a separate
  priority concept, not a completion-status tracker. `AGENTS.md`'s roadmap doc-map entry and `CONTRIBUTING.md`'s
  condensed Roadmap section document the new convention; a stale `improvement-plan-tasks.md#-next` anchor left over
  from the old phasing, in Gap #1's checklist link, corrected to `#-completed`

##### Models

- **`AwardRequestForCSV`, `CompetitorRequest`, `CompetitorRequestForCSV`, `MatchRequest`, `MatchRequestForCSV`:**
  Each `LocalDate` field's `@JsonFormat(pattern = ...)` now points at `SystemConstants.DEFAULT_DATE_FORMAT`
  (`AwardRequestForCSV.date`) or `IpscConstants.IPSC_INPUT_DATE_FORMAT` (the IPSC competitor/match classes'
  `dateOfBirth`/`matchDate`) instead of the now-removed `HpscConstants.HPSC_INPUT_DATE_FORMAT` — every constant
  resolves to the same `"yyyy-MM-dd"` pattern, so the accepted input format itself is unchanged
- **`MatchRequest`/`MatchRequestForCSV`:** `club` field/constructor-param Javadoc now documents the new
  default-to-`IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` behaviour above, instead of implying the name is
  always resolved against an existing club

##### Services

- **`IpscCompetitorService`:** Same Javadoc link swap as `IpscCompetitorController` above, across
  `createCompetitor`/`createCompetitors`/`updateCompetitor`/`patchCompetitor`'s `@throws ValidationException`
  descriptions and their "home club is HPSC" parameter notes
- **`IpscCompetitorServiceImpl`, `IpscMatchServiceImpl`:** Reordered to match the new Member ordering convention —
  `readCompetitors`/`toRequest`/`splitEmailAddresses` and `readMatches`/`toRequest`/`parseStages` respectively were
  each sitting between two public methods; moved down after every public method, alongside the rest of each
  class's protected helpers. No behavioural change, purely a reorder

##### Tests

- **`IpscCompetitorServiceIntegrationTest`, `IpscCompetitorServiceTest`, `IpscMatchServiceIntegrationTest`,
  `IpscMatchServiceTest`, `IpscMatchServiceImplTest`:** Hardcoded `ClubIdentifier.HPSC` references switched to
  `IpscConstants.HOME_CLUB_IDENTIFIER`, matching production code's new constant; no behavioural change, since the
  constant currently always resolves to `ClubIdentifier.HPSC`
- **`IpscMatchServiceImplTest`, `IpscMatchServiceTest`, `IpscMatchServiceIntegrationTest`:** `whenClubIsBlank`/
  `whenClubIsMissing` cases that previously asserted `ValidationException` now assert the club defaults to
  `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER`, matching the behaviour change above; new cases cover
  `resolveClub`/`createMatch` throwing `NonFatalException` when even the default club is missing
- **`IpscMatchServiceImplTest`, `IpscMatchServiceIntegrationTest`, `IpscMatchControllerTest`:** Test methods that
  call `createMatch`/`updateMatch`/`patchMatch`/`applyFields` directly (as fixture setup or the method under
  test) now declare/wrap for the new checked `FatalException`, matching the production signature changes above;
  no behavioural change to the tests themselves
- **`IpscMatchServiceImplTest`:** New `resolveClub(String, ClubIdentifier)` cases cover the new overload directly:
  throwing `FatalException` when the default identifier is null (with a null or blank club name), and correctly
  ignoring the default identifier when a club name is supplied
- **`IpscMatchControllerTest`:** New `testCreateMatch_whenServiceThrowsFatalException_thenExceptionPropagates`,
  `testPatchMatch_whenServiceThrowsFatalException_thenExceptionPropagates` and
  `testUpdateMatch_whenServiceThrowsFatalException_thenExceptionPropagates` cases confirm a `FatalException`
  thrown by the service propagates through the controller, mirroring the existing `createMatches` coverage for
  the same exception type

##### Tooling

- **`sync-improvement-plan-gaps`, `update-improvement-plan-gaps`:** Both skills updated to read/write the new
  ✅ Completed / 🟡 Partially Completed / ⚪ Open section structure above instead of the old Now/Next/Later/Ongoing
  phase headers, including moving a gap's whole block between sections as its status changes

#### 🐛 Fixed

##### Database

- **`V7_2_0__add_competitor_emails.sql`:** Dropped an unnecessary hyphen in the header comment ("infrequently-changed" →
  "infrequently changed")

##### Documentation

- **`ARCHITECTURE.md`:** Dropped `HpscConstants` from the Project Structure tree's `constants/` comment and the
  Constants support-layer subsection's class list — the class no longer exists, removed earlier this branch
- **`ARCHITECTURE.md`:** Project Structure tree was missing the tracked `.claude/skills/` directory entirely.
  Added it, described generically rather than naming individual skills — unlike the fixed, slow-moving package
  structure the tree lists class names for elsewhere, skills are added/removed often enough that naming each one
  would drift. `AGENTS.md`'s Directory Tree Maintenance rule and `CONTRIBUTING.md`'s condensed mention of it both
  gained a clarifying bullet: tracked tooling directories (`.claude/`, `.github/`) belong in the tree, even though
  they sit alongside gitignored directories at the repository root — only `.gitignore`-covered directories are
  excluded from it
- **`ARCHITECTURE.md`:** Project Structure tree brought fully in sync with disk, per the new Release Checklist
  step above: added the previously undocumented `documentation/recommendations/` and
  `src/main/resources/db/migration/` directories, and added `Gender`/`GenderConverter` to the `enums/`/
  `converters/` class lists — both existed on disk already but were never added to the tree
- **`documentation/recommendations/flyway-migration-versioning.md`:** Realigned the Current State table's column
  widths; a follow-up pass restored "sub-versions"' hyphen after an editor pass had dropped it to "subversions",
  which reads as "acts of subversion" rather than the intended meaning
- **`HISTORY.md`:** Restored "Standards Adoption" in the Conclusion's bullet list after an earlier typo-fix pass
  had singularised it to "Standard Adoption", flipping its meaning and leaving it inconsistent with the Milestone 8
  heading
- **`ARCHITECTURE.md`/`CONTRIBUTING.md`:** CI/CD & Quality Gates tables' Code Coverage row corrected from a stale
  "minimum 51% line coverage" to "minimum 97%", matching the floor `pom.xml` actually enforces since it was
  tightened in this branch
- **`ARCHITECTURE.md`:** Dropped `v8.1.0`/`v8.3.0` version references from the Service Layer section's IPSC bulk
  CSV import note — this file is evergreen documentation and must not name specific project versions, per its own
  Evergreen Documentation rule in `AGENTS.md`
- **`ARCHITECTURE.md`:** Domain Entities table corrected — it described every relationship as bidirectional
  (`One-to-many ←/→`) with `mappedBy` collections, but the domain model has no `@OneToMany` fields at all; every
  relationship is unidirectional `@ManyToOne` from the child side. Table and its footnote rewritten to match
- **`README.md`:** Installation and Execution steps corrected — they told a new developer to hand-edit
  `application.properties` with a literal username/password against a `hpsc_db` database, but the application
  actually reads credentials from `MYSQL_USER`/`MYSQL_PASSWORD` env vars regardless of profile and has no
  `spring.datasource.url` outside a profile, so the documented steps couldn't actually start the app. Now mirrors
  `CONTRIBUTING.md`'s `dev` profile flow (`hpsc_dev`) and links to it for the full profile matrix; the startup URL
  corrected to include the `/hpsc-web` context path
- **`ARCHITECTURE.md`:** Project Structure tree and Model Layer section corrected — both described a top-level
  `models/shared/` package holding `Placing`, but it actually lives at `models/award/shared/Placing.java`; no
  top-level `models/shared/` package exists. The tree also gained the previously undocumented `models/ipsc/competitor/`
  request/response directories and the `models/ipsc/match/response/` directory it was missing
- **`ARCHITECTURE.md`:** Added the missing `GenderConverter` row to the Custom JPA Attribute Converters table —
  the class exists in `converters/` and `Gender` is already listed in the Enumerations table, but the converter
  itself had never been added
- **`ARCHITECTURE.md`:** Project Structure tree's `configs/` comment corrected from "Spring configuration
  (ControllerAdvice, OpenAPI)" to "Spring configuration (ControllerAdvice)" — `configs/` only contains
  `ControllerAdvice`; `@OpenAPIDefinition` is actually on `HpscWebApplication.java`
- **`ARCHITECTURE.md`:** Presentation Layer table's Mapping column made consistent — `AwardController`/
  `ImageController` were shown with the `/hpsc-web` context-path prefix while the IPSC controllers weren't, but
  none of the actual `@RequestMapping` values include it (Spring prepends `server.servlet.context-path`
  automatically). All four rows now show the mapping as declared in code
- **`ARCHITECTURE.md`:** Technology Stack table's Testing row gained `Spring REST Docs`, matching `README.md`'s
  Technology section and the `spring-restdocs-mockmvc` dependency already declared in `pom.xml`
- **`ARCHITECTURE.md`:** Project Structure tree's `constants/`, `controllers/`, `converters/`, `domain/`, `enums/`,
  `models/` (root), `repositories/` and `utils/` comments dropped their individual class-name listings — same
  rationale already applied to `.claude/skills/`: classes are added, renamed and removed far more often than the
  packages that hold them, so naming each one drifts out of sync (as `GenderConverter` above just did). `AGENTS.md`'s
  Directory Tree Maintenance rule and `CONTRIBUTING.md`'s condensed mention both gained a matching bullet making the
  no-class-names rule explicit for the whole tree, not just skills

#### 🗑️ Removed

##### Constants

- **`HpscConstants`:** Removed entirely. Its sole constant, `HPSC_INPUT_DATE_FORMAT`, was just an alias for
  `SystemConstants.ISO_DATE_FORMAT`; every former user now references `SystemConstants.DEFAULT_DATE_FORMAT` or
  `IpscConstants.IPSC_INPUT_DATE_FORMAT` directly instead (see Changed → Models above)

#### 🔐 Security

- **`tomcat-embed-core`/`tomcat-embed-el`/`tomcat-embed-websocket`:** Overridden `11.0.24` → `11.0.25` via a new
  `pom.xml` `tomcat.version` property, closing three critical GitHub-flagged advisories:
  [CVE-2026-68525](https://nvd.nist.gov/vuln/detail/CVE-2026-68525) (GHSA-h3x4-894j-xpx5, FORM authentication
  incorrect authorization), [CVE-2026-65905](https://nvd.nist.gov/vuln/detail/CVE-2026-65905)
  (GHSA-9xv2-5v5q-p794, DIGEST authenticator replay bypass) and
  [CVE-2026-65182](https://nvd.nist.gov/vuln/detail/CVE-2026-65182) (GHSA-gcx9-497g-6cp6, improper access
  control) — all transitive via `spring-boot-starter-tomcat`, still pinned to `11.0.24` by
  `spring-boot-starter-parent:4.1.1`'s dependency management with no newer 4.1.x release yet published

### 🧾 [8.3.1] - 2026-09-02

#### ➕ Added

##### CI/CD

- **`.github/workflows/build.yml`:** New workflow runs `./mvnw verify -Pcoverage` on push/PR to `main`/`develop`,
  mirroring `codeql.yml`'s trigger branches — sets up JDK 25 via `actions/setup-java` (Maven-cached), builds/tests
  via `sh ./mvnw` (`mvnw` isn't tracked with the execute bit in git) and uploads the JaCoCo HTML/XML report as a
  build artefact. Closes `documentation/roadmap/improvement-plan.md`'s Gap #2

#### 🔄 Changed

##### Configuration

- **`pom.xml`:** New `jacoco-maven-plugin` `check` execution in the `coverage` profile enforces a `BUNDLE`-level
  `LINE`/`COVEREDRATIO` minimum, initially `0.51` (51%) as a deliberately low regression backstop, then raised to
  `0.86` (86%) within the same branch, wired into `build.yml`'s CI gate so a coverage regression fails the build —
  still short of the ~98% real baseline. Partially progresses Gap #4

##### Documentation

- **`README.md`/`ARCHITECTURE.md`:** Confirmed `AwardService.createAwards()`/`ImageService.createImages()` CSV
  processing is intentionally stateless by design, not an unfinished persistence layer — closes Gap #3
- **`ARCHITECTURE.md`/`CONTRIBUTING.md`:** CI/CD & Quality Gates tables updated to reflect the new `build.yml` gate
  and JaCoCo coverage-check rule, dropping the stale "locally / by reviewers"/"All PRs" language
- **`documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md`:** Gap #2 closed in v8.3.1; Gap #3
  closed in v8.3.1; Gap #4 marked partially progressed in v8.3.1, noting the refreshed coverage baseline (98.16%/98.94%
  line/branch, 836 tests) and the JaCoCo floor tightened twice within the same branch (51% → 86%);
  `HISTORY.md`'s coverage figure refresh is done, recorded in its Historical Timeline, Phase 24 and Milestone 24

#### 🐛 Fixed

- **`ARCHITECTURE.md`/`documentation/roadmap/improvement-plan.md`:** Corrected stale `processCsv()` method
  references (renamed to `createAwards()`/`createImages()` in v8.0.0) in the Award/Image CSV Processing Flow
  diagram and Gap #3's Evidence text
- **`AwardControllerTest`/`ImageControllerTest`:** Corrected stale `// processCsv()` test-grouping comments to
  `// createAwards()`/`// createImages()`, matching the same v8.0.0 rename

### 🧾 [8.3.0] - 2026-09-02

#### ➕ Added

##### Controllers

- **`IpscMatchController`:** New `createMatches` endpoint (`POST /ipsc/matches/bulk`, consumes `text/csv`)
  for bulk-creating IPSC matches, together with their stages, from CSV data, following the same
  bulk-import convention as `IpscCompetitorController.createCompetitors`

##### Services

- **`IpscMatchService`/`IpscMatchServiceImpl`:** New `createMatches` method that parses CSV data into
  `MatchRequestForCSV` rows and creates each match via the existing `createMatch` validation/club/
  firearm-type/category-resolution logic; new `readMatches` and `toRequest` protected helpers mirror
  `IpscCompetitorServiceImpl`'s CSV-parsing pattern, and a new `parseStages` helper splits a row's
  semicolon-separated `Stages` cell into `MatchStageRequest`s, splitting each entry on its first `-` into
  `<stageNumber>-<stageName>`

##### Models

- **`MatchRequestForCSV`:** New class-level Javadoc and `@JsonCreator` constructor, binding `MatchDate`/
  `MatchName`/`Club`/`MatchFirearmType`/`MatchCategory`/`Stages` to their `UpperCamelCase`
  column/property names for CSV/JSON deserialization — matching `CompetitorRequestForCSV`'s
  pattern. The `stages` field is a single semicolon-separated CSV cell of
  `<stageNumber>-<stageName>` entries (e.g. `"1-Stage One;2-Stage Two"`)
- **`MatchResponseHolder`:** New response container (`models/ipsc/match/response/`) holding the
  `MatchResponse`s created by a bulk CSV import, mirroring `CompetitorResponseHolder`

##### Tests

- **`MatchRequestForCSVTest`:** New tests covering `MatchRequestForCSV`'s `UpperCamelCase` JSON (de)serialization, its
  CSV deserialization via `CsvMapper`/`CsvSchema`, and the `@JsonCreator`
  constructor's enforcement of `matchDate`/`matchName` as required creator properties
- **`IpscMatchControllerTest`:** New tests covering `createMatches`'s `201` response, delegation to the
  service and propagation of `ValidationException`/`NonFatalException`/`FatalException`
- **`IpscMatchServiceTest`:** New tests covering `createMatches`'s CSV validation, row-level club/firearm-type/
  category resolution, stage parsing and bulk persistence, exercised through the interface with mocked
  repositories
- **`IpscMatchServiceImplTest`:** New tests covering the impl-only `parseStages`/`readMatches`/`toRequest`
  protected helper methods

### 🧾 [8.2.0] - 2026-09-01

#### 🔄 Changed

##### Domain

- **`Competitor`:** `emailAddress` (a single, optional `String`) replaced with `emailAddresses`
  (`List<String>`), mapped via `@ElementCollection`/`@CollectionTable` onto a new `competitor_email`
  child table — a competitor can now have zero or more email addresses

##### Models

- **`CompetitorRequest`, `CompetitorResponse`:** `emailAddress` (`String`) renamed to `emailAddresses`
  (`List<String>`)
- **`CompetitorRequestForCSV`:** `emailAddress` renamed to `emailAddresses`; still a single `String`
  CSV cell, but now holding zero or more semicolon-separated email addresses (e.g.
  `"a@x.com;b@x.com"`), split into a list when mapped onto `CompetitorRequest`

##### Services

- **`IpscCompetitorServiceImpl`:** `applyFields`, `patchCompetitor`, `toRequest` and `toResponse`
  updated for `emailAddresses`; new `splitEmailAddresses` helper parses a CSV row's
  semicolon-separated email cell into a `List<String>`, trimming entries and dropping blanks

##### Controllers

- **`IpscCompetitorController`:** Bulk CSV endpoint's Swagger example header updated from
  `EmailAddress` to `EmailAddresses`

##### Database

- **`V7_2_0__add_competitor_emails.sql`:** New Flyway migration adding the `competitor_email` table (`competitor_id` FK,
  `email_address`), backfilling it from any existing non-blank
  `competitor.email_address` values, then dropping that column

##### Constants

- **`SystemConstants.ARRAY_SEPARATOR`:** New shared `";"` constant, and `ImageServiceImpl`/
  `AwardServiceImpl`'s bulk CSV parsing switched from `"|"` to it, so every bulk CSV endpoint's
  multi-value cells (competitor email addresses, image/award tags) now share one separator
  convention; the `ImageController`/`AwardController` Swagger examples and their CSV parsing tests
  are updated to match

##### Documentation

- **`ARCHITECTURE.md`, `CONTRIBUTING.md`:** CI/CD & Quality Gates tables' `Static Analysis` row removed
- **`AGENTS.md`:** `CodeQL/Qodana/JaCoCo` trigger reference updated to `CodeQL/JaCoCo`
- **`documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md`:** Gap #7 (Qodana CI wiring) closed as
  not applicable, rather than delivered — see 🗑️ Removed below for why

#### 🐛 Fixed

##### Services

- **`IpscCompetitorServiceImpl`:** `applyFields`/`patchCompetitor` now defensively copy
  `request.getEmailAddresses()` into a new `ArrayList` before storing it on the entity, instead of storing the
  caller-supplied `List` reference directly. An immutable list (e.g. `List.of(...)`) previously crashed with an
  unhandled `UnsupportedOperationException` when Hibernate merged an update, bypassing the
  `FatalException`/`NonFatalException`/`ValidationException` hierarchy entirely; found while adding multi-address
  test coverage for `patchCompetitor`

#### 🗑️ Removed

##### CI/CD & Configuration

- **`.github/workflows/qodana.yml`, `qodana.yaml`:** Qodana static analysis removed. It had failed on every CI run
  since v8.1.1 added it — a missing `QODANA_TOKEN` repository secret (release-line Qodana linters require one
  since 2023.2) and an unconditional SARIF-upload step that also failed independently — so there was no working
  configuration left to preserve

### 🧾 [8.1.1] - 2026-09-01

#### ➕ Added

##### CI/CD & Configuration

- **`.github/workflows/qodana.yml`:** New workflow running JetBrains' `qodana-action` against the existing
  `qodana.yaml` configuration, triggered on push/PR to `develop` and `main` (mirroring `codeql.yml`'s trigger
  branches). Results upload as SARIF to GitHub code scanning alongside CodeQL, so no Qodana Cloud token or other
  secret is required

##### Documentation

- **`CLAUDE.md`:** New "Working on Complex Tasks" section instructing use of the TodoWrite tool for multistep or
  non-trivial tasks, matching `AGENTS.md`'s existing "Track complex work with a todo list" Git Workflow convention
  and the sibling `hpsc-web-vite` project's `CLAUDE.md`
- **`CONTRIBUTING.md`:** New "🗺️ Roadmap" section documenting `documentation/roadmap/improvement-plan.md`/
  `improvement-plan-tasks.md`'s structure (Goals & Constraints table, numbered gap sections, Roadmap/Success
  Criteria) and their not-evergreen, closed-in-place maintenance convention — the only one of `README.md`/
  `AGENTS.md`/`ARCHITECTURE.md`/`CONTRIBUTING.md` that didn't already list these files

##### Tooling

- **`/update-improvement-plan-gaps`:** New Claude Code skill that audits the codebase against
  `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` and records any newly identified,
  newly-closed or newly-progressed gaps in both files, following the same evidence-based methodology used to write
  and maintain the plan's existing gaps by hand this release. Never commits — drafts the edits and stops for review,
  same as `prep-version-release`
- **`/sync-improvement-plan-gaps`:** New Claude Code skill, narrower than `/update-improvement-plan-gaps` above —
  checks the current branch's diff (mirroring `sync-unreleased-changes`' merge-base/diff-gathering approach) against
  only the plan's already-tracked gaps, to catch one this branch's own work closed or progressed. Never adds a new
  gap number itself; flags anything that looks like one for a separate `/update-improvement-plan-gaps` sweep instead.
  It also never commits on its own

##### Testing

- **`NonFatalExceptionTest`, `FatalExceptionTest`, `ValidationExceptionTest`:** New test classes covering all
  constructor overloads of the three exception hierarchy base classes, closing a real regression — these existed as
  of v7.2.0 but were dropped somewhere between then and now with no replacement, leaving the classes at 20% line
  coverage (only the single-`message` constructor got incidental exercise via other tests)
- **`IpscCommonScoreTest`, `IpscMatchScoreTest`, `IpscMatchStageScoreTest`:** New test classes for the
  `models/ipsc/shared` scoring groundwork classes (0% coverage previously, as nothing references them outside
  Javadoc yet), each covering the one handwritten all-args constructor per `AGENTS.md`'s rule against testing
  Lombok-generated behaviour in isolation
- **`IpscCompetitorServiceTest`, `IpscMatchServiceTest`:** Added success-path coverage for every `patchCompetitor`/
  `patchMatch` field that was previously only exercised via its validation-failure branch (e.g. `clubNumber`,
  `homeClub`/`club`, `gender`/`matchFirearmType`/`matchCategory` resolution, `matchDate` and the remaining simple
  string/date/numeric fields) — patching a single field's happy path had never actually been asserted for most
  fields since the endpoints were introduced in v8.0.0. Also adds the missing "field is `null`" counterpart to each
  existing "field is blank" validation test (`clubNumber`, match `club`) to close a branch JaCoCo flagged as
  unreached
- Full-suite line/branch coverage rose from 92.9%/93.4% to 98.34%/98.84% as a result (746 → 775 tests); see
  `documentation/roadmap/improvement-plan.md`'s Gap #4 for the remaining, deliberately untested gaps (three
  structurally-unreachable `IOException` catch blocks in the CSV `read*()` methods, `ImageResponse`'s dead
  null-fallback branch, the unused `IpscConstants` class, and `HpscWebApplication.main()`)

#### 🔄 Changed

##### Build & Metadata

- Project version bumped to **8.1.1** in `pom.xml`; `@OpenAPIDefinition` version updated to match
- **`pom.xml`:** Spring Boot parent bumped `4.1.0` → `4.1.1`. As part of this:
    - Removed the `jackson-databind` (`2.21.5`) `dependencyManagement` override — Boot 4.1.1 now manages this version
      itself
    - Removed the `log4j-api` (`2.25.5`, CVE-2026-49844 fix) `dependencyManagement` override — Boot 4.1.1 now manages
      this version itself
    - Corrected the developer contact email (`leonil@tahoni.info` → `tahoni@gmail.com`)
    - Updated the flyway-maven-plugin's inline sync comment to reference `4.1.1`; the pinned `flyway-mysql` version
      (`12.4.0`) is unchanged, as Boot 4.1.1 still manages `flyway.version` at `12.4.0`
    - Verified: full test suite (746 tests) passes against the bumped parent
- **`pom.xml`:** Removed the `jackson-bom.version` property override (pinned `3.1.5`) — found by Gap #5's own
  recurring-check task during this release's gap-sync sweep, confirmed redundant against
  `spring-boot-dependencies:4.1.1`'s own managed default (also `3.1.5`) via the parent POM directly, not just an
  echoed property. Verified: full test suite (775 tests) passes with the override removed

##### Documentation

- **`documentation/roadmap/improvement-plan.md`, `improvement-plan-tasks.md`:** Gap #1 (match/competitor service and
  controller layer) and Gap #5 (`jackson-databind` version override) marked ✅ Closed — in v8.0.0 and by this
  release's Spring Boot bump respectively — with Outcome notes and checked-off task lists rather than deleted
  analysis, per the plan's own Success Criteria instructions. Gap #3 (Award/Image CSV persistence) gains a Progress
  note: v8.1.0's competitor bulk CSV import and its `ARCHITECTURE.md` contrast narrow the ambiguity for that one
  domain, but the underlying Award/Image question stays open. The Roadmap table promotes Gap #4 (coverage
  enforcement) into the vacated Next slot and rewords the Ongoing row now that Gap #5's specific overrides are gone
- **`AGENTS.md`'s Release Checklist:** Re-synced against `prep-version-release`'s actual, current process, which had
  drifted ahead of it — adds a new step 1 to check `improvement-plan.md`/`improvement-plan-tasks.md` for gaps before
  version-specific work begins, a new step 4 to verify `CHANGELOG.md`'s `[Unreleased]` section is complete before
  renaming it and a new step 8 to update `CONTRIBUTING.md` when applicable, matching the skill's steps 2, 5 and 10
  respectively (described tool-agnostically, without naming the skill). Also fixes a stale Build & Run Commands
  pointer that named only CodeQL/JaCoCo among `ARCHITECTURE.md`'s CI/CD gates, missing Qodana

##### Tooling

- **`.claude/skills/generate-pr-description` renamed to `prep-version-release`:** Better reflects what the skill
  actually does (the whole release-prep checklist, not just the PR description step); its `generate-pr-summary`
  cross-reference is updated to match
- **`prep-version-release`:** New step 2 runs `update-improvement-plan-gaps` then `sync-improvement-plan-gaps`, in
  that order, before any version-specific work begins — the full codebase sweep for brand-new gaps first, then the
  diff-driven check for gaps this branch's own work has closed or progressed, since the latter needs the plan
  already reflecting whatever the former just found. Renumbers the remaining checklist steps accordingly
- **`AGENTS.md`'s Release Checklist step 4 and `prep-version-release`'s matching step:** Both now end by checking
  whether `improvement-plan.md`'s "⚙️ Goals & Constraints" table needs a matching update after `HISTORY.md` is
  extended — the table is synthesised partly from `HISTORY.md`'s Future Roadmap Implications sections, so a change
  there can leave it stale. `improvement-plan.md`'s own "🎯 Purpose & Scope" section states the same dependency

### 🧾 [8.1.0] - 2026-09-01

#### ➕ Added

##### Controllers

- **`IpscCompetitorController`:** New `createCompetitors` endpoint (`POST /ipsc/competitors/bulk`, consumes
  `text/csv`) for bulk-creating IPSC competitors from CSV data, following the same bulk-import convention as
  `AwardController.createAwards`/`ImageController.createImages`

##### Services

- **`IpscCompetitorService`/`IpscCompetitorServiceImpl`:** New `createCompetitors` method that parses CSV data into
  `CompetitorRequestForCSV` rows and creates each competitor via the existing `createCompetitor` validation/gender/
  home-club-resolution logic — unlike `AwardService`/`ImageService`'s CSV endpoints, which only build response
  objects without persisting

##### Models

- **`CompetitorRequestForCSV`:** New CSV-mapped request model (`models/ipsc/competitor/request/`) for bulk competitor
  import, mirroring `CompetitorRequest`'s fields other than `competitorId`
- **`CompetitorResponseHolder`:** New response container (`models/ipsc/competitor/response/`) holding the
  `CompetitorResponse`s created by a bulk CSV import

##### Tests

- **`IpscCompetitorControllerTest`:** New tests covering `createCompetitors`'s `201` response, delegation to the
  service and propagation of `ValidationException`/`NonFatalException`/`FatalException`
- **`IpscCompetitorServiceTest`, `IpscCompetitorServiceIntegrationTest`:** New tests covering `createCompetitors`'s
  validation, row-level gender/home-club resolution and bulk persistence, exercised through the interface with mocked
  repositories and against the real H2-backed Spring context
- **`IpscCompetitorServiceImplTest`:** New tests covering the impl-only `readCompetitors`/`toRequest` protected
  helper methods
- **`CompetitorRequestForCSVTest`:** New tests covering `CompetitorRequestForCSV`'s `UpperCamelCase` JSON
  (de)serialization and `@JsonFormat`-patterned `dateOfBirth`, its CSV deserialization via `CsvMapper`/`CsvSchema`,
  and the `@JsonCreator` constructor's enforcement of `firstName`/`lastName` as required creator properties
- **`CompetitorRequestTest`:** New tests covering `CompetitorRequest`'s `@JsonCreator` constructor — JSON
  (de)serialization, `competitorNumber` no longer being required, and `firstName`/`lastName`/`clubNumber` each
  throwing `MismatchedInputException` when missing
- **`MatchRequestTest`:** New tests covering `MatchRequest`'s JSON (de)serialization, including its nested `stages`
  list and `@JsonFormat`-patterned `matchDate`, and `matchDate`/`matchName` each throwing `MismatchedInputException`
  when missing
- **`MatchStageRequestTest`:** New tests covering `MatchStageRequest`'s JSON (de)serialization and `stageNumber`
  throwing `MismatchedInputException` when missing
- **`MatchOverallScoresRequestTest`, `MatchStageScoresRequestTest`:** New tests covering their `@JsonCreator`
  constructors' JSON (de)serialization and required fields (`matchId`/`name`/`membershipNumber`, plus `stageNumber`
  for the stage variant) each throwing `MismatchedInputException` when missing
- **`MatchOverallScoresRequestForCSVTest`, `MatchStageScoresRequestForCSVTest`:** New tests covering the CSV
  variants' Practiscore column mapping (both `@JsonProperty`-overridden columns like `Mem#`/`HF` and the
  `@JsonNaming`-transformed ones like `Time`) via a concrete test subclass, required-field enforcement, and using
  each as a `csvMapper.addMixIn(...)` mixin onto its plain counterpart — the same pattern
  `AwardServiceImpl`/`ImageServiceImpl` use for `AwardRequestForCSV`/`ImageRequestForCsv`

#### 🔄 Changed

##### Models

- **`MatchRequest`, `MatchStageRequest`, `MatchResponse`, `MatchStageResponse`, `MatchOverallScoresRequest`,
  `MatchOverallScoresRequestForCSV`, `MatchStageScoresRequest`, `MatchStageScoresRequestForCSV`:** Added `@NotNull`
  to fields that are always required (e.g. `matchName`, `matchDate`, `stageNumber`, `name`, `membershipNumber`),
  documenting the existing contract rather than changing behaviour — matching the `@NotNull` already used on
  `CompetitorRequest`/`ImageRequest`
- **`MatchRequest`, `MatchStageRequest`:** `@NotNull` on `matchDate`/`matchName`/`stageNumber` above was later
  switched to `@JsonProperty(required = true)`, but neither class had a `@JsonCreator` constructor, so the
  annotation was a no-op — a missing field just deserialised as `null` via the Lombok no-args constructor and
  setters. Both classes gained a `@JsonCreator` constructor with each parameter bound via `@JsonProperty`,
  replacing `@AllArgsConstructor` (same signature/order, so every existing positional `new MatchRequest(...)`/
  `new MatchStageRequest(...)` call is unaffected) — a missing `matchDate`, `matchName` or `stageNumber` now
  throws `MismatchedInputException` during parsing, matching the fix already applied to
  `CompetitorRequestForCSV`/`CompetitorRequest`
- **`CompetitorRequestForCSV`:** `firstName`/`lastName` switched from `@NotNull` to `@JsonProperty(required = true)`,
  and a `@JsonCreator` constructor added with each of its 13 parameters bound to its
  `UpperCamelCase` column name explicitly (a multi-argument creator needs this, since `@JsonNaming` alone only
  governs serialisation) — so a CSV row or JSON payload missing either column now fails with
  `MismatchedInputException` during parsing, rather than only being caught later by
  `IpscCompetitorService.createCompetitor`'s validation. Matches the required-column enforcement
  `AwardRequestForCSV`/`ImageRequestForCsv` already have
- **`CompetitorRequest`, `CompetitorRequestForCSV`, `MatchRequest`:** Added
  `@JsonFormat(pattern = HpscConstants.HPSC_INPUT_DATE_FORMAT)` to their `LocalDate` fields (`dateOfBirth`/
  `matchDate`), making the accepted `yyyy-MM-dd` input format explicit rather than relying on Jackson's default
  `LocalDate` parsing — matching `AwardRequestForCSV`'s existing use of the same pattern on its `date` field
- **`CompetitorRequest`:** Added a `@JsonCreator` constructor with each of its 14 parameters bound via
  `@JsonProperty`, replacing the Lombok `@AllArgsConstructor` (same signature, so `IpscCompetitorServiceImpl
  .toRequest`'s positional call is unaffected) — `firstName`/`lastName` remain `required = true`, and
  `@JsonProperty(required = true)` moves from `competitorNumber` to `clubNumber`, correcting a mismatch between
  the JSON-level requirement and `IpscCompetitorServiceImpl.validateForCreate`'s actual required fields (`firstName`,
  `lastName`, `clubNumber`)
- **`CompetitorResponse`:** Added `@NotNull` to `competitorId`, `firstName`, `lastName` and `clubNumber` — every
  persisted competitor always has these set, documenting the existing contract rather than changing behaviour,
  matching the `@NotNull` already used on `CompetitorRequest`/`ImageRequest`
- **`MatchOverallScoresRequest`, `MatchStageScoresRequest`, `MatchOverallScoresRequestForCSV`,
  `MatchStageScoresRequestForCSV`:** `@NotNull` on `matchId`/`name`/`membershipNumber` (plus `stageNumber` for the
  stage variants) switched to `@JsonProperty(required = true)`, and each class gained a `@JsonCreator` constructor
  with every parameter bound via `@JsonProperty`, replacing `@AllArgsConstructor` — the same fix already applied to
  `MatchRequest`/`MatchStageRequest`. The two CSV variants' constructors bind each parameter to its exact Practiscore
  column name (the field's own override, or the `@JsonNaming` `UpperCamelCase` transform where there's none), and
  now include `matchId` (typically `null`, since it isn't part of the CSV export) so their signature matches their
  plain counterpart's exactly — making them usable as a `csvMapper.addMixIn(...)` mixin, the same pattern
  `AwardServiceImpl`/`ImageServiceImpl` use for `AwardRequestForCSV`/`ImageRequestForCsv`. None of these four classes
  are wired into a controller or service yet, so this only affects future consumers. `name`/`stageNumber`/
  `membershipNumber` also carry `@JsonProperty(required = true)` at the field level on the two CSV variants, matching
  the field-level annotation already present alongside the constructor-level one on
  `CompetitorRequestForCSV`/`CompetitorRequest`

#### 🗑️ Removed

##### Configuration

- **`application.properties`:** `hpsc.web.app.club.filter.abbreviation` — not read anywhere in the codebase via
  `@Value`/`@ConfigurationProperties`, and not referenced by any other `application-*.properties` file

### 🧾 [8.0.0] - 2026-08-31

#### ➕ Added

##### Controllers

- **`IpscMatchController`:** Rebuilt from an empty stub into a full CRUD controller on `/ipsc/matches` — `createMatch`
  (`POST`), `updateMatch` (`PUT /{matchId}`, full replace), `patchMatch` (`PATCH /{matchId}`, partial update),
  `getMatch` (`GET /{matchId}`) and `getAllMatches` (`GET`, returns every match), following this project's action-named
  REST method convention (`create`/`update`/`patch`/`get`, not `post`/`put`/`patch`/`get`)
- **`IpscCompetitorController`:** Rebuilt from an empty stub into a full CRUD controller on `/ipsc/competitors` —
  `createCompetitor` (`POST`), `updateCompetitor` (`PUT /{competitorId}`, full replace), `patchCompetitor`
  (`PATCH /{competitorId}`, partial update) and `getCompetitor` (`GET /{competitorId}`)

##### Services

- **`IpscMatchService`/`IpscMatchServiceImpl`:** New service backing `IpscMatchController` — resolves the request's club
  by name (404 via `NonFatalException` if not found) and its firearm type/category by name (400 via
  `ValidationException` if unrecognised), maps `MatchRequest` to/from the existing `IpscMatch`/`IpscMatchStage`
  entities, and persists via the existing `IpscMatchRepository`/`IpscMatchStageRepository`. `patchMatch` upserts stages
  by stage number (updating a matching stage in place, adding a new one otherwise) rather than replacing the whole stage
  list, unlike `updateMatch`'s full replace; `getAllMatches` returns every persisted match together with its stages
- **`IpscCompetitorService`/`IpscCompetitorServiceImpl`:** New service backing `IpscCompetitorController` — resolves the
  request's optional home club by name (404 via `NonFatalException` if named but not found) and its optional gender by
  name (400 via `ValidationException` if unrecognised), maps `CompetitorRequest` to/from the existing `Competitor`
  entity and back out to a `CompetitorResponse`, and persists via the existing `CompetitorRepository`. Unlike
  `IpscMatchService`'s club, the home club (and now gender) is optional — a `null`/blank name simply leaves the field
  unset, and `updateCompetitor`'s full replace clears any previously set home club/gender that the request omits

##### Models

- **`MatchRequest`:** Gains `matchFirearmType`/`matchCategory` fields, typed as free-text `String`s resolved by name
  against `FirearmType`/`MatchCategory` in the service layer (matching how `club` is already resolved against `Club`) —
  required by `IpscMatchService` to persist an `IpscMatch` (which has no other source for them)
- **`MatchResponse`/`MatchStageResponse`:** New response DTOs (`models/ipsc/match/response/`) returned by
  `IpscMatchController`'s endpoints — unlike the request, `MatchResponse.club` is typed as `ClubIdentifier` rather than
  a plain `String`, since a persisted match's club is always resolvable
- **`CompetitorRequest`:** New request DTO (`models/ipsc/competitor/request/`) mirroring `Competitor`'s persisted
  fields — `gender` is a free-text `String` resolved by name against `Gender` in the service layer (matching how
  `homeClub` is already resolved against `Club`)
- **`CompetitorResponse`:** New response DTO (`models/ipsc/competitor/response/`) returned by `IpscCompetitorController`
  's endpoints — mirrors `CompetitorRequest`'s fields, except `homeClub` is typed as `ClubIdentifier` rather than a
  plain club-name `String`, since a persisted competitor's home club is always resolvable

##### Converters

- **`GenderConverter`:** New `AttributeConverter<Gender, String>`, wired onto `Competitor.gender` via `@Convert` —
  converts blank/invalid stored values to `null` instead of letting `@Enumerated(STRING)` throw, matching the
  null-safety already used by the other enum converters

##### Tests

- **`IpscMatchControllerTest`:** New Mockito-only unit test covering `IpscMatchController`'s five endpoints
- **`IpscMatchServiceIntegrationTest`:** New H2-backed integration test covering `IpscMatchService`'s full contract —
  validation, club/match not-found (404), create/replace/patch/get/get-all and the patch-vs-replace stage semantics
- **`IpscCompetitorControllerTest`:** New Mockito-only unit test covering `IpscCompetitorController`'s four endpoints
- **`IpscCompetitorServiceIntegrationTest`:** New H2-backed integration test covering `IpscCompetitorService`'s full
  contract — validation, competitor/home-club not-found (404), unrecognised gender (400), create/replace/patch/get and
  the optional-home-club semantics
- **`IpscMatchServiceTest`, `IpscCompetitorServiceTest`:** New Mockito-based unit tests for the `IpscMatchService`/
  `IpscCompetitorService` interface contracts, exercised through the interface type with their repository dependencies
  mocked — the same contract as `IpscMatchServiceIntegrationTest`/`IpscCompetitorServiceIntegrationTest`, but isolated
  from the H2-backed Spring context for faster, focused coverage
- **`IpscMatchServiceImplTest`, `IpscCompetitorServiceImplTest`:** New Mockito-based unit tests for
  `IpscMatchServiceImpl`'s/`IpscCompetitorServiceImpl`'s impl-only protected helper methods (`applyFields`,
  `resolveClub`/`resolveHomeClub`, `resolveFirearmType`/`resolveGender`, `resolveMatchCategory`, `toResponse`,
  `validateForCreate`, plus `findMatchOrThrow`/`findCompetitorOrThrow` and, for matches,
  `replaceStages`/`upsertStages`) — not declared on the `IpscMatchService`/`IpscCompetitorService` interfaces, so not
  covered by `IpscMatchServiceTest`/`IpscCompetitorServiceTest`, matching the existing
  `AwardServiceImplTest`/`ImageServiceImplTest` split between interface-level and impl-only coverage
- **`GenderTest`:** New unit test covering `Gender.fromName`'s exact/case-insensitive/no-match/null/blank lookup
  behaviour and its new `toString()` override
- **`GenderConverterTest`:** New unit test covering `GenderConverter`'s `convertToDatabaseColumn`/
  `convertToEntityAttribute`, including the null/blank/unrecognised-name-to-`null` fallback behaviour

##### Documentation

- **`documentation/recommendations/standard-rest-conventions.md`:** New reference document covering REST endpoint (URL)
  and method naming conventions, grounded in this codebase's actual controllers (`AwardController`/`ImageController`'s
  `createAwards`/`createImages`, `IpscMatchController`'s full CRUD) — `AGENTS.md`'s Documentation File Map updated to
  list the new `documentation/recommendations/` folder
- **`AGENTS.md`:** New Line wrapping rule under Documentation Conventions — wrap prose lines in every Markdown file
  between 100 and 120 characters, excluding GFM tables, fenced code blocks, directory trees and diagrams;
  `CONTRIBUTING.md`'s Documentation Conventions summary updated to reference it

##### Tooling

- **`/generate-commit-message`:** Now also surfaces commits already made on the current branch (via `git merge-base`
  against `develop`/`main`), so drafted messages and CHANGELOG entries stay consistent with — and don't duplicate —
  changes committed outside the current Claude session
- **`/sync-unreleased-changes`:** Now also sweeps the whole `[Unreleased]` section for `#### <Area>` sub-headers
  repeated within the same `### <Category>` block and merges them into one, concatenating their bullets in original
  order — catches drift left by earlier runs or by commits that each added their own block for the same area

##### CI/CD & Configuration

- **`qodana.yaml`:** Re-added — `jetbrains/qodana-jvm:2026.2` linter on the `qodana.starter` profile, targeting JDK
  25; quality-gate thresholds left commented out. `ARCHITECTURE.md`'s Technology Stack and CI/CD & Quality Gates
  tables, and `CONTRIBUTING.md`'s own Quality Gates table, reverse-synced to list it again as running locally/via IDE
  only, since no CI workflow triggers it

#### 🔄 Changed

##### Models

- **`MatchOverallResultRequest`/`MatchStageResultRequest`:** Renamed to `MatchOverallScoresRequest`/
  `MatchStageScoresRequest` (with their CSV variants) — each instance holds every competitor's scores for a match/stage,
  not a single competitor's, so the singular "Result" naming was misleading
- **`za.co.hpsc.web.models.ipsc.request`:** Split into `za.co.hpsc.web.models.ipsc.match.request` (match/stage
  submission DTOs) and `za.co.hpsc.web.models.ipsc.scores.request` (competitor scores submission DTOs)
- **`Placing`:** Moved from `models/shared` to `models/award/shared`, since it's only used to back award placements;
  `AwardPlacing`'s import updated accordingly
- **`MatchOverallScoresRequest`/`MatchStageScoresRequest`** (and their CSV variants): `division`, `club` and
  `powerFactor` are now typed as `Division`, `ClubIdentifier` and `PowerFactor` respectively, and `categories` as
  `List<CompetitorCategory>` — previously all four were free-text `String` fields
- **`Request`, `Response`, `AwardRequest`, `AwardRequestForCSV`, `AwardResponse`, `AwardCeremonyResponse`,
  `AwardCeremonyResponseHolder`:** Added `@since 1.1.0` class-level tags
- **`ControllerResponse`, `AwardPlacing`, `Placing`:** Added `@since 1.1.3` class-level tags
- **`ImageRequest`, `ImageRequestForCsv`, `ImageResponse`, `ImageResponseHolder`:** Added `@since 1.0.0` class-level
  tags
- **`MatchOverallScoresRequestForCSV`, `MatchStageScoresRequestForCSV`, `MatchOverallScoresRequest`,
  `MatchStageScoresRequest`, `MatchStageRequest`, `IpscMatchStageScore`, `IpscMatchScore`, `IpscCommonScore`:** Added
  `@since 7.4.0` class-level tags
- **`MatchRequest`:** Added `@since 1.1.3` class-level tag
- **`ControllerResponse`, `Request`, `Response`, `AwardRequestForCSV`, `AwardCeremonyResponse`, `AwardResponse`,
  `ImageRequest`, `ImageResponse`:** Added `@since` tags to individual methods introduced later than the class itself

##### Controllers

- **`AwardController`, `ImageController`:** Their `createAwards`/`createImages` methods already followed this project's
  action-named REST method convention; the underlying `AwardService.processCsv`/`ImageService.processCsv` calls they
  delegate to have now been renamed to match — see the `Services` entry below
- **`ImageController`, `AwardController`:** Added class-level `@since` tags (`1.0.0`, `1.1.0` respectively)
- **`AwardController`, `ImageController`:** Bulk CSV endpoints moved from `POST /awards`/`POST /images` to
  `POST /awards/bulk`/`POST /images/bulk` and now return `201 Created` (previously `200 OK`), matching
  `IpscCompetitorController.createCompetitor`'s create-endpoint convention; `@Operation` summary/description reworded
  from generic CSV processing to bulk creation

##### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** Rewrapped to a consistent ~120-character line width — no
  content changes
- **`CLAUDE.md`:** Removed a stale Runtime line (claimed Spring Boot `4.0.5`; `pom.xml`'s parent is `4.1.0`) and two
  generic Maven test-invocation examples; replaced the Database Profiles table (which had drifted out of sync with
  `CONTRIBUTING.md`'s — it said "manual migrations" where Flyway is actually used) and the Code Quality & CI section
  (duplicating `ARCHITECTURE.md`'s CI/CD & Quality Gates table) with pointers to those files
- **`AGENTS.md`, `CLAUDE.md`:** `CLAUDE.md`'s Project Overview, Build & Run Commands, Architecture and Testing
  Patterns sections merged into `AGENTS.md` so any AI coding agent — not just Claude Code — gets the same guidance;
  `CLAUDE.md` reduced to a short pointer, since nothing in it was Claude-Code-specific. `README.md`'s and `AGENTS.md`'s
  own Documentation File Map, `ARCHITECTURE.md`'s Development Guidelines section, and the five `.claude/commands/*.md`
  skill files that cited `CLAUDE.md`'s removed sections updated to reference `AGENTS.md` (or `CONTRIBUTING.md` directly
  for the Database Profiles table) instead
- **`AGENTS.md`:** Test Conventions gains a helper-placement rule — private fixture/setup helpers go after every
  `@Test` method, under a `// Helpers` comment, so the `@Test` methods stay together at the top, uninterrupted by
  fixture code; `IpscCompetitorServiceIntegrationTest`/`IpscMatchServiceIntegrationTest` updated to match
- **`AGENTS.md`:** Arrange-Act-Assert rule extended to require a `// Arrange`, `// Act` or `// Assert` comment marking
  each phase present in a test — a phase's comment is omitted only when that phase doesn't apply. Tests verifying a
  thrown exception (typically `assertThrows(...)`) mark that call with a single `// Act & Assert` comment instead,
  since the act and assert happen in one statement
- **`ARCHITECTURE.md`:** Package layout, Controllers/Services tables and model documentation reverse-synced to the IPSC
  module rebuild — `IpscController`'s empty-stub row replaced by `IpscCompetitorController`/`IpscMatchController`
  (and their `IpscCompetitorService`/`IpscMatchService` counterparts), the package tree's `models/ipsc/request/` split
  into `models/ipsc/match/request/`/`models/ipsc/scores/request/` to match, and the groundwork note on the service
  layer narrowed to reflect that only the CRUD services above currently exist
- **`ARCHITECTURE.md`:** System Overview table's single "Match & Competitor Domain" row split into "IPSC Competitors &
  Matches" (now full CRUD) and "Match Scoring & Shooter Logs" (still groundwork); the `models/ipsc/match/request/`
  package-tree comment no longer says "(groundwork)" now that `IpscMatchController` consumes it; the `repositories/`
  comment now names which repositories are wired to the new IPSC services versus still unwired
- **`README.md`:** Introduction and Features sections updated to describe the new IPSC competitor/match CRUD as
  implemented, narrowing the "still being rebuilt" language to the match-scoring/shooter-log domain that remains
  groundwork
- **`CONTRIBUTING.md`:** Layered-architecture note narrowed from "the match/competitor domain's service layer" to "the
  match/competitor scoring domain's service layer", since the competitor/match CRUD service layer now exists

##### Tests

- **`AwardServiceIntegrationTest`, `AwardServiceTest`, `ImageServiceIntegrationTest`, `ImageServiceTest`,
  `IpscCompetitorServiceIntegrationTest`, `IpscCompetitorServiceTest`, `IpscMatchServiceIntegrationTest`,
  `IpscMatchServiceTest`, `AwardServiceImplTest`, `ImageServiceImplTest`, `IpscCompetitorServiceImplTest`,
  `IpscMatchServiceImplTest`:** Missing `// Arrange`/`// Act`/`// Assert`/`// Act & Assert` comments added throughout,
  per `AGENTS.md`'s extended Arrange-Act-Assert rule above
- **`AwardControllerTest`, `ImageControllerTest`, `IpscCompetitorControllerTest`, `IpscMatchControllerTest`,
  `IpscCompetitorServiceTest`, `IpscMatchServiceImplTest`:** Removed redundant `verify(mock, times(1))` calls —
  simplified to bare `verify(mock)`, since `times(1)` is Mockito's default and asserted nothing extra, per
  `AGENTS.md`'s existing brittle-assertion rule. `verify(mock, times(2))` in
  `IpscMatchServiceImplTest.testReplaceStages_whenStageRequestsProvided_thenPersistsEachAndReturnsInOrder` left as
  is — that count is the actual behaviour under test
- **`HpscWebApplicationTest`, `AwardServiceIntegrationTest`, `ImageServiceIntegrationTest`:** Stopped excluding
  `DataSourceAutoConfiguration`/`HibernateJpaAutoConfiguration` — `@SpringBootTest` with no `classes=` boots the whole
  app via component scan, and now that `IpscMatchServiceImpl` genuinely depends on JPA, excluding it broke context
  loading for every test that boots the full context, not just tests of JPA-touching services
- **`AwardControllerTest`, `ImageControllerTest`:** Updated to mock/verify the renamed `createAwards`/`createImages`
  service methods and assert `201 Created` instead of `200 OK`
- **`AwardServiceTest`, `AwardServiceIntegrationTest`, `ImageServiceTest`, `ImageServiceIntegrationTest`:** Updated to
  call the renamed `createAwards`/`createImages` methods
- **`ClubIdentifierTest`, `CompetitorCategoryTest`, `DivisionTest`, `FirearmTypeTest`, `MatchCategoryTest`,
  `PowerFactorTest`:** Updated to call the renamed `fromName`/`fromAbbreviation`/`fromCode`/`fromAbbreviationOrName`
  factory methods, including their test method names (e.g. `testGetByAbbreviation_*` → `testFromAbbreviation_*`)

##### Configs

- **`ControllerAdvice`:** Gains a class-level `@since 1.0.0` tag and full `@param`/`@return` Javadoc on every exception
  handler and helper method — none of it was previously documented; `@since` tags also added to the individual
  handler/helper methods introduced later than the class itself — `handleValidationException`/`handleNonFatalException`
  at `5.4.0`; `handleHttpMessageConversionException`, `handleUnhandledException`, `buildErrorResponse` and both
  `logError` overloads at `7.0.0`

##### Services

- **`ImageService`:** Added `@since 1.0.0` class-level tag
- **`AwardService`:** Added `@since 1.1.0` class-level tag
- **`AwardService.processCsv`, `ImageService.processCsv`:** Renamed to `createAwards`/`createImages`, matching the
  already-named `AwardController.createAwards`/`ImageController.createImages`; Javadoc reworded to describe the
  CSV-to-response transform (no persistence) and now documents the previously-undeclared `ValidationException` thrown
  for null/blank/unparseable CSV

##### Utils

- **`ValueUtil`:** Added `@since 1.1.0` class-level tag
- **`NumberUtil`, `StringUtil`:** Added `@since 1.1.3` class-level tags
- **`DateUtil`:** `@since` corrected from `2.0.0` to `4.1.0` — the class was originally added in `2.0.0` but later
  deleted and reintroduced in `4.1.0`, which is when it actually became continuously available
- **`ValueUtil`:** Removed the private constructor's Javadoc block in favour of the plain
  `// Utility class, not to be instantiated` inline comment already beside it — the block only restated what the comment
  already says
- **`NumberUtil`, `StringUtil`, `ValueUtil`:** Added `@since` tags to individual methods introduced later than the class
  itself — e.g. `ValueUtil.nullAsDefault`/`nullAsDefaultString` at `7.0.0`, added long after the class's own `1.1.0`

##### Constants

- **`HpscConstants`:** Added `@since 1.1.0` class-level tag
- **`IpscConstants`, `SystemConstants`:** Added `@since 1.1.3` class-level tags
- **`HpscConstants`, `IpscConstants`, `SystemConstants`:** Private constructors now carry a
  `// Prevent instantiation of this utility class` comment, matching the convention used by `ValueUtil`
- **`IpscConstants`:** Gains `IPSC_INPUT_DATE_FORMAT` (`SystemConstants.ISO_DATE_FORMAT`); `IPSC_INPUT_DATE_TIME_FORMAT`
  now sources `SystemConstants.ISO_DATE_TIME_FORMAT` instead of the removed `T_SEPARATED_DATE_TIME_FORMAT`, so all four
  `IPSC_*` format constants are consistent with the plain ISO formats used elsewhere in the project
- **`SystemConstants`:** Removed `T_SEPARATED_DATE_TIME_FORMAT` — its only consumer, `IpscConstants`, no longer uses it

##### Enums

- **`ClubIdentifier`:** Added class-level Javadoc matching the convention already used by the other enums, and corrected
  its `fromName`/`fromAbbreviation`/`fromCode` Javadoc, which still referred to a stale `ClubReference` type name and an
  inaccurate "null or negative" description for the (`String`-typed) `code` parameter
- **`ClubIdentifier`, `CompetitorCategory`, `Division`, `FirearmType`, `MatchCategory`, `PowerFactor`:** Renamed
  `getByName`/`getByAbbreviation`/`getByCode`/`getByAbbreviationOrName` factory methods to `fromName`/
  `fromAbbreviation`/`fromCode`/`fromAbbreviationOrName` — a more idiomatic name for an `Optional`-returning static
  factory; behaviour unchanged
- **`Gender`:** Gains `name`/`abbreviation` fields, a case-insensitive `fromName()` factory method and a `toString()`
  override, bringing it in line with the shape of the other enums
- **`Gender`:** Added class-level Javadoc and `fromName()` method Javadoc, bringing it in line with the other enums, all
  of which were already documented
- **`Gender`:** Added `@since 7.0.0` class-level tag
- **`ClubIdentifier`:** Added `@since 5.0.0` class-level tag
- **`CompetitorCategory`, `Division`, `FirearmType`, `MatchCategory`, `PowerFactor`:** Added `@since 1.1.3` class-level
  tags
- **`ClubIdentifier`, `CompetitorCategory`, `Division`, `FirearmType`, `Gender`:** Added `@since` tags to individual
  factory/lookup methods introduced later than the class itself

##### Converters

- **`ClubIdentifierConverter`, `CompetitorCategoryConverter`, `DivisionConverter`, `FirearmTypeConverter`,
  `MatchCategoryConverter`, `PowerFactorConverter`:** Parameter names aligned to `AttributeConverter`'s own convention
  (`attribute`/`dbData`), for consistency with the new `GenderConverter`
- **`ClubIdentifierConverter`, `CompetitorCategoryConverter`, `DivisionConverter`, `FirearmTypeConverter`,
  `MatchCategoryConverter`, `PowerFactorConverter`:** Updated to call the renamed `fromX` factory methods
- **`GenderConverter`:** `convertToEntityAttribute` now delegates to `Gender.fromName(...).orElse(null)` instead of a
  manual `Gender.valueOf()`/try-catch — lookups are now case-insensitive, matching the other enum converters
- **`ClubIdentifierConverter`, `CompetitorCategoryConverter`, `DivisionConverter`, `FirearmTypeConverter`,
  `GenderConverter`, `MatchCategoryConverter`, `PowerFactorConverter`:** Added class-level Javadoc describing what each
  converter stores on write and how it resolves values on read — none previously had any
- **`ClubIdentifierConverter`, `CompetitorCategoryConverter`, `DivisionConverter`, `FirearmTypeConverter`,
  `MatchCategoryConverter`, `PowerFactorConverter`:** Added `@since 5.3.0` class-level tags
- **`GenderConverter`:** Added `@since 8.0.0` class-level tag, matching this in-progress, still-unreleased version

##### Exceptions

- **`FatalException`, `NonFatalException`, `ValidationException`:** Trimmed constructor Javadoc that duplicated verbatim
  JDK prose (`initCause`, `getMessage()`/`getCause()` references) down to concise, project-specific wording; corrected
  `@since` tags that had been copied from `java.lang.Exception`/`IllegalArgumentException` (`1.4`/`1.5`/`1.7`) to this
  project's own version history (`1.0.0`), the version in which all these constructors were actually introduced
- **`FatalException`, `NonFatalException`, `ValidationException`:** Added class-level `@since 1.0.0` tags

##### Tooling

- **`.claude/commands/generate-commit-message.md`, `generate-pr-description.md`, `sync-unreleased-changes.md`,
  `generate-pr-summary.md`, `scaffold-unit-tests.md`, `scaffold-integration-tests.md`:** Converted from Claude Code
  slash commands to Skills, moved to `.claude/skills/<name>/SKILL.md` — rewritten so Claude runs the previous
  `` !`cmd` `` bash blocks and `@file` includes itself (skills don't get a slash command's auto-expansion), with
  `$ARGUMENTS`/`$1` replaced by the skill's `args`; cross-references between them updated to the new skill names
- **`generate-pr-description`:** Gains a new step that runs the `sync-unreleased-changes` skill (base `develop`, since
  release branches are cut from it) before renaming `[Unreleased]` into the new version's section, so the CHANGELOG is
  fully accurate before being folded into the release

#### 🐛 Fixed

##### Domain

- **`Competitor.gender`:** Removed a stray `@Enumerated(EnumType.STRING)` left over from before `GenderConverter`
  existed — Hibernate 7 rejects a field carrying both `@Enumerated` and a custom `@Convert`, so any Spring context that
  actually initialises JPA (previously none did) failed to start. Only surfaced once `IpscMatchServiceIntegrationTest`
  became this project's first JPA-backed test

##### Documentation

- **`documentation/history/RELEASE_NOTES_v7.1.0.md`:** Corrected its `.claude/commands/generate-commit-message.md`
  reference to `../../.claude/commands/generate-commit-message.md` — the archived file lives two directories below the
  repository root, so the unprefixed relative link was broken
- **`documentation/history/RELEASE_NOTES_v7.2.0.md`, `PR_DESCRIPTION_v7.2.0.md`:** Corrected stale `processCsv`
  references to `createAwards`, matching `AwardService.processCsv`'s/`ImageService.processCsv`'s rename above

#### 🗑️ Removed

##### Controllers

- **`IpscController`:** Deleted — its `@RequestMapping("/ipsc/competitor")` role is superseded by the new
  `IpscCompetitorController` stub as part of the IPSC module split into per-concern controllers

##### Models

- **`MatchStagesRequest`:** Deleted — this unused wrapper around `matchId` plus a `List<MatchStageRequest>` was never
  consumed by any controller; callers adding or updating stages on an existing match now just pass a plain
  `List<MatchStageRequest>` directly

##### Tests

- **`FatalExceptionTest`, `NonFatalExceptionTest`, `ValidationExceptionTest`:** Deleted — every test in these files only
  exercised the JDK superclass constructor delegation (`Exception`/`RuntimeException`/`IllegalArgumentException` storing
  a message/cause), with no HPSC-specific logic of their own to protect against regression

---

### 🗄️ Archived Versions (1.0.0 – 7.4.1)

The change log entries for versions 1.0.0 to 7.4.1 are archived, unchanged, in
[`documentation/history/CHANGELOG_v1-v7.md`](/documentation/history/CHANGELOG_v1-v7.md).

---

### 📋 Version Policy

#### Semantic Versioning (Current)

As of version 5.0.0, this project follows [Semantic Versioning 2.0.0](https://semver.org/):

- **MAJOR** version for incompatible API changes
- **MINOR** version for backward-compatible functionality additions
- **PATCH** version for backward-compatible bug fixes

Every release must follow these rules strictly — see [`AGENTS.md`'s Semantic Versioning
section](/AGENTS.md#semantic-versioning) for what counts as a MAJOR, MINOR or PATCH change in this project and how
each release is classified from the `[Unreleased]` section.

#### Legacy Versioning (v1.x – v4.x)

Earlier releases used a non-semantic versioning scheme. For historical documentation,
see [ARCHIVE.md](/documentation/archive/ARCHIVE.md).

---

### 🚀 Upgrade Guide

#### From v5.3.0 to v5.4.0

**Breaking Changes:** None

1. Update the version in `pom.xml` to `5.4.0`
2. Replace any `IpscMatchService` injection points with `TransformationService`
3. Update import statements for classes moved from `ipsc/domain` to `ipsc/data`
4. Update `MatchCompetitorEntityService` call sites to handle `List<>` return types
5. Run `./mvnw clean install` to rebuild the project

#### From v4.1.0 to v5.0.0

**Breaking Changes:** None

1. Update the version in `pom.xml` to `5.0.0`
2. Run `./mvnw clean install` to rebuild the project
3. Restart the application
4. Existing data and configurations remain compatible

#### From v4.0.0 to v4.1.0

**Breaking Changes:** None

Migration: See v4.1.0 release notes

#### From v3.x to v4.x

**Breaking Changes:** Yes

- Update entity references from `Match` to `IpscMatch`
- Update service injections to use `IpscMatchRepository`
- See v4.0.0 release notes for a detailed migration guide

---

### 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Create a feature branch from `main`
2. Make your changes with comprehensive test coverage
3. Document your changes in the appropriate sections of this CHANGELOG
4. Submit a pull request with a detailed description

---

### 💬 Support

For issues, feature requests or questions:

- **GitHub Issues:** [tahoni/hpsc-web-springboot/issues](https://github.com/tahoni/hpsc-web-springboot/issues)
- **Repository:** [tahoni/hpsc-web-springboot](https://github.com/tahoni/hpsc-web-springboot)

---

**Last Updated:** 2026-08-25
