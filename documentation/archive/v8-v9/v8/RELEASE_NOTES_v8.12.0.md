# Release Notes – Version 8.12.0

**Release Date:** September 29, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Competitor CSV Import Casing Normalisation**

Version 8.12.0 is a minor release. The competitor CSV import used to store names exactly as typed in the source
spreadsheet, so `VAN DER MERWE`, `Van Der Merwe` and `van der merwe` produced differently-formatted records. The import
now proper-cases its free-text columns and lower-cases surname particles (`van`, `der`, `du`, `de`, `le` and the
like), so imported names read consistently whatever case they arrive in. The CSV format and the JSON endpoints are
unchanged. The release also adds Qodana static analysis to CI and widens the CodeQL and dependency-submission
triggers to every GitFlow branch.

---

## ⭐ Key Highlights

### 🔤 Consistent Imported Names

- `FirstName`, `LastName`, `MiddleNames`, `Nickname` and `Gender` are proper-cased on import
  (`o'NEIL-smith` → `O'Neil-Smith`)
- Surname particles are lower-cased ahead of the surname proper, including after a hyphen (`VAN DER MERWE` →
  `van der Merwe`, `SMITH-VAN DER MERWE` → `Smith-van der Merwe`), while `Dube` and `Vanderbilt` are left alone
- A Gaelic `Mc` prefix is corrected (`MCDONALD` → `McDonald`), and curly apostrophes are handled like straight ones
  (`o’NEIL` → `O’Neil`)
- `HomeClub`, `ClubNumber`, `CompetitorNumber`, `IdNumber`, `CellphoneNumber` and `EmailAddresses` keep the case
  supplied

### 🔬 Static Analysis in CI

- New Qodana workflow runs on every push to `main`, `release/*`, `feature/*`, `bugfix/*` and `hotfix/*`, plus PRs
  into `main`/`develop`
- CodeQL and dependency submission now trigger on the same branches, so a failure surfaces where it was introduced

### 🌿 `bugfix/*` Branch Type

- `../../../../AGENTS.md` and `CONTRIBUTING.md` document `bugfix/<short-description>` as its own standard GitFlow branch type

---

## 📦 What's New

### Added

#### CI/CD & Configuration

- **`../../../../.github/workflows/code_quality.yml`:** New Qodana static-analysis workflow, running `JetBrains/qodana-action` on
  every push to `main`, `release/*`, `feature/*`, `bugfix/*` and `hotfix/*`, plus PRs into `main`/`develop`

#### Services

- **`StringUtils.toProperCase`:** Upper-cases the first letter of each word and lower-cases the rest, treating spaces,
  hyphens and apostrophes as word breaks, backed by the new `org.apache.commons:commons-text` dependency's `WordUtils`
- **`CompetitorHelpers.toSentenceCaseLastName`:** New helper in the new `za.co.hpsc.web.helpers` package that
  lower-cases surname particles (also after a hyphen) when they precede the surname proper and capitalises the letter
  after a `Mc` prefix. Only whole words are matched. Listed in
  `../../../../ARCHITECTURE.md`'s Project Structure tree and a new Helpers table

#### Tests

- **`IpscCompetitorServiceImplTest`:** New `toRequest` tests for all-upper-case and all-lower-case CSV rows, an
  upper-case last name with several particles, one that merely starts with particle letters, and ID and cellphone
  numbers that keep their case

### Changed

#### CI/CD & Configuration

- **`codeql.yml`, `dependency-submission.yml`:** Push triggers now also cover `release/*`, `feature/*`, `bugfix/*` and
  `hotfix/*`
- **`docker.yml`:** Dependabot's `github-actions` group update (PR #152) bumps `docker/setup-buildx-action` to `v4` and
  `docker/build-push-action` to `v7`, both major bumps with no change to the workflow's inputs

#### Services

- **`IpscCompetitorServiceImpl.toRequest`:** The competitor CSV import proper-cases the free-text columns listed above
  and passes the `LastName` through `CompetitorHelpers.toSentenceCaseLastName`. The JSON create, update and patch
  endpoints are unaffected

#### Utils

- **`DateUtils`, `NumberUtils`, `StringUtils`, `ValueUtils`:** Renamed from their singular `Util` names (and their test
  classes to match) — internal classes only, so there is no API change

#### Documentation

- **`../../../../AGENTS.md`, `CONTRIBUTING.md`:** `bugfix/<short-description>` added as a standard GitFlow branch type
- **`../../../../ARCHITECTURE.md`, `CONTRIBUTING.md`:** CI/CD & Quality Gates table gains a Static Analysis (Qodana) row, and its
  CodeQL and Dependency Submission triggers now read "any GitFlow branch"
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #32 recorded and closed — the plan still described
  Qodana as removed, after `code_quality.yml` brought it back to CI

#### Build & Metadata

- Project version bumped to **8.12.0** in `../../../../pom.xml`; `@OpenAPIDefinition` version updated to match

---

## 🚀 Migration Guide

No action needed. No endpoint, JSON field, configuration property or schema changed. The only behavioural difference
is that competitors created through the CSV import are now stored with normalised casing; records already in the
database are not modified.

---

## 📊 Statistics

- **New Source Files:** 2 (`CompetitorHelpers`, `../../../../.github/workflows/code_quality.yml`), plus their tests
- **New Dependencies:** 1 (`org.apache.commons:commons-text`)
- **Deleted Files:** 0 (four `Util` classes renamed to `Utils`)

---

## 🧭 Design Notes

- **Proper-case, then fix particles.** `toProperCase` knows nothing about surnames, so the particle rule is a separate
  step applied only to `LastName`, keeping the general-purpose utility free of competitor-specific logic.
- **Hyphens are word breaks.** Particles are found in space- and hyphen-delimited words alike; the final word is always
  the surname proper.
- **Whole words only.** Particles are matched as whole words, so `Dube` (starts with `du`) and `Vanderbilt` (starts
  with `van`) are never altered.
- **Codes and lookups are untouched.** `HomeClub` is matched exactly against club names, and club and competitor
  numbers are codes (club numbers also unique), so none of them is re-cased.
- **A MINOR, not a PATCH.** The import's stored output changes in a backward-compatible way, which `../../../../AGENTS.md`'s
  Semantic Versioning rules classify as new functionality.

---

## 🧪 Testing

- New `IpscCompetitorServiceImplTest` `toRequest` cases pin the casing behaviour for upper-case, lower-case,
  particle-bearing, hyphenated, `Mc`-prefixed and curly-apostrophe rows, including that home club, competitor, club, ID
  and cellphone numbers and email addresses keep their case.
- `./mvnw verify -Pcoverage` passes on the release branch: 1110 tests, 0 failures/errors/skipped; 98.65% line / 99.13%
  branch coverage, with the JaCoCo gate (97% line and branch) passing.

---

## 🐛 Known Issues

- Proper-casing is still naive about other internal capitals: only the `Mc` prefix is corrected, so `MacDonald` is
  imported as `Macdonald` (`Mac` is ambiguous — `Mackenzie`), and `Mch`/`Mcu` surnames (`Mchunu`) are deliberately left
  as `Mchunu` rather than `McHunu`, at the cost of `McUsher`-style Scottish names.
- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- A competitor or match referenced by results or shooter logs can't be deleted through the API, since no endpoint
  removes those rows yet (carried over from v8.8.0, pending Gap #6).
- `../../../../pom.xml` still overrides `tomcat.version` to `11.0.25` for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26).
- The Claude code review on Dependabot PRs fails until `CLAUDE_CODE_OAUTH_TOKEN` is also stored as a Dependabot
  secret, if not already done for v8.10.2.

---

## 🔮 Future Enhancements

- Build a `MatchScoreService`/`ShooterLogService` over the existing repositories, committing through
  `TransactionService`, following the same phased pattern that closed Gap #1 (Gap #6).
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint.
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.25` or later (Gap #26).

---

## 👥 Contributors

Leoni Lubbinge, `dependabot[bot]`

---

## 📝 Notes

Version 8.12.0 makes bulk-imported competitor data consistent to read without changing the CSV format or the JSON
endpoints consumers already rely on, and adds static analysis to every GitFlow branch's CI.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
