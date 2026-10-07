# Release Notes – Version 13.1.0

**Release Date:** October 7, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Request Validation & Competitor Name Cleaning**

Version 13.1.0 is a minor release. A request body that is missing a required field is now rejected with a `400 Bad
Request` by Bean Validation, a competitor's name is cleaned of a leading position, a range officer marker and full stops
before the competitor is looked up, and the code moves from `jakarta` `@NotNull` to `jspecify` `@NonNull` for nullness.
Nothing in it breaks an existing caller.

---

## ⭐ Key Highlights

### ✅ Request Bodies Are Validated on Arrival

- The competitor, match and match competitor controllers annotate their `@RequestBody` parameters with `@Valid`, and
  `CompetitorRequest`, `MatchRequest` and `MatchCompetitorRequest` carry `@NotBlank` and `@NotNull` constraints for the
  fields `validate()` already required, with the same messages
- `ControllerAdvice.handleMethodArgumentNotValidException` maps a body that fails validation to a `400 Bad Request`
  listing every violated constraint, in place of the generic `500`

### 🧼 Competitor Names Are Cleaned Before a Lookup

- New `CompetitorHelpers.cleanCompetitorName` removes a leading position such as `1 - `, any `RO` or `(RO)` range
  officer marker and all full stops, collapses runs of whitespace to one space and trims the result
- `MatchCompetitorMapper.applyFields` and `applyPatchFields` clean the request's competitor name, and
  `EntityIpscCompetitorServiceImpl.findCompetitor` normalises the full name with the same method, so `1 - J. Smith (RO)`
  is looked up as `J Smith`

### 🧹 `jspecify` Nullness

- The mappers, services and response models use `org.jspecify.annotations.NonNull` in place of
  `jakarta.validation.constraints.NotNull`, which Bean Validation never applied to them, and `org.jspecify:jspecify` is
  a direct dependency
- `MatchCompetitorRequest.validate()` returns `void` and throws on failure, as the other request models do

---

## 📦 What's New

### Added

- `ControllerAdvice.handleMethodArgumentNotValidException`, returning a `400 Bad Request` that lists every violated
  constraint
- `@NotBlank` and `@NotNull` constraints on `CompetitorRequest`, `MatchRequest` and `MatchCompetitorRequest`
- `CompetitorHelpers.cleanCompetitorName`

### Changed

- `IpscCompetitorController`, `IpscMatchController` and `IpscMatchCompetitorController` annotate their `@RequestBody`
  parameters with `@Valid`
- `MatchCompetitorMapper.applyFields` and `applyPatchFields` clean the competitor name before looking the competitor up
- `EntityIpscCompetitorServiceImpl.findCompetitor` normalises the full name with
  `CompetitorHelpers.cleanCompetitorName`, so a leading position and full stops are also ignored when matching by name
- `MatchCompetitorRequest.validate()` returns `void` instead of always returning `true`
- `jakarta` `@NotNull` is replaced with `jspecify` `@NonNull` on the services', mappers' and response models' parameters
  and fields, and `pom.xml` declares `org.jspecify:jspecify` as a direct dependency
- `flyway-migration-versioning.md`'s Current State table is re-aligned, and `AGENTS.md` gains a missing comma

---

## 🚀 Migration Guide

No migration is needed. A request body that is missing a required field now gets a `400 Bad Request` where it used to
get a `500`, so a client that treated that status as a server error should handle the `400` instead. A competitor name
that contains a full stop is now looked up without it, so `J. Smith` matches a stored `J Smith` and no longer matches a
stored `J. Smith`.

---

## 📊 Statistics

- **Files Changed:** 48 against `main`, counted before this release's notes and PR description were added; 443
  insertions and 189 deletions
- **New Source Files:** 0
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0 (`org.jspecify:jspecify` was already on the classpath through Spring Boot and is now declared
  directly)

---

## 🧭 Design Notes

- **Validate on arrival.** Bean Validation rejects a request body before it reaches the service, and `validate()` stays
  as the check for the rules a constraint cannot express.
- **One way to clean a name.** The mapper and the lookup share `CompetitorHelpers.cleanCompetitorName`, and it reads the
  range officer marker from `IpscConstants.REPLACE_IN_NAMES_REGEX`, so an imported name and a requested name are
  compared in the same form.
- **A MINOR.** The `400` response and the new helper are additions, and nothing removed or renamed breaks an existing
  caller, which `AGENTS.md`'s Semantic Versioning rules classify as MINOR.

---

## 🧪 Testing

- `./mvnw test` passes: 1,277 tests with no failures, errors or skips.
- New unit tests cover `CompetitorHelpers.cleanCompetitorName` and the cleaned name that `applyPatchFields` looks up.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports,
  is read but never used as the row's own ID, since the imports only create records.
- No service or controller operates on `ShooterLog`, `ShooterLogCompetitor` or `ShooterLogOverall` yet, and no
  calculation job fills them in (Gap #6, partially completed).
- `pom.xml` still overrides `tomcat.version` (to `11.0.26`) for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26). `logback.version`, the Jackson BOM properties and `flyway-mysql` are likewise set above
  Spring Boot's managed versions, and `mysql-connector-j` is pinned to `26.7.0` against the `9.7.0` it manages.
- Seven utility methods and `SystemConstants.DEFAULT_SCALE` are still used only by tests (Gap #38, partially completed),
  and so are `ClubIdentifier.code` and `fromCode` (Gap #39).
- Stored `Manual Action Contemporary Division` and `Manual Action Bolt Division` values are not migrated and read back
  as `null`.

---

## 🔮 Future Enhancements

- Build a `ShooterLogService` and controller over the existing repositories, committing through `TransactionService`,
  following the same phased pattern that closed Gap #1 (Gap #6).
- Remove or give a caller to the remaining test-only utility methods, `SystemConstants.DEFAULT_SCALE` and
  `ClubIdentifier.code` (Gaps #38 and #39).
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.26` or later (Gap #26), and the
  other dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge
- Claude Code (Claude Sonnet 5.5) — co-author of most of the release's commits

---

## 📝 Notes

Version 13.1.0 validates request bodies with Bean Validation and cleans a competitor's name before it is looked up, and
moves the code to `jspecify` nullness annotations. No database migration runs on upgrade and no existing caller breaks.

---
