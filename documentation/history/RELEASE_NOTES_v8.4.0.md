# Release Notes – Version 8.4.0

**Release Date:** September 3, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Club Domain Defaults, Optional Club Numbers & Documentation Convention Hardening**

Version 8.4.0 extends the "apply the domain default" pattern from the competitor module to the match module: a
match's `club` is no longer a hard-required field — a missing or blank value now resolves to a new
`ClubIdentifier.ALL` ("Eufees Clubs") constant, seeded into the database by a new Flyway migration, instead of
failing validation. The same release relaxes competitor `clubNumber` to be required only when the competitor's home
club is HPSC. Alongside these two domain-rule changes, the JaCoCo coverage-regression floor introduced in v8.3.1 is
tightened from 86% to 97% — now genuinely close to the real, freshly re-measured baseline — closing
`documentation/roadmap/improvement-plan.md`'s Gap #4 and Gap #9. The remainder of the release is documentation and
convention hardening: a new Member ordering convention, REST naming rules promoted from recommendation to
convention, a release-checklist backstop that keeps `ARCHITECTURE.md`'s Project Structure tree honest, the
`improvement-plan.md`/`improvement-plan-tasks.md` restructure into ✅ Completed/🟡 Partially Completed/⚪ Open
sections, and a Tomcat security patch closing three critical CVEs.

---

## ⭐ Key Highlights

### 🎯 Match Club Defaulting

- New `ClubIdentifier.ALL` constant (`"Eufees Clubs"` / `"All"` / `"ALL"`) represents a match hosted jointly by
  `SOSC`/`HPSC`/`PMPSC`; new `V7_3_0__seed_club_data.sql` migration seeds the `club` table with every named
  `ClubIdentifier` constant
- `IpscMatchServiceImpl.resolveClub()` now defaults a missing/blank match `club` to
  `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` instead of `validateForCreate` throwing `ValidationException`;
  throws `NonFatalException` if even the default club is missing from the database, or a `FatalException` if the
  constant itself is null
- Closes Gap #9: the previously unused `DEFAULT_MATCH_CLUB_IDENTIFIER` constant is now genuinely wired in

### 🔓 Optional Competitor Club Numbers

- `IpscCompetitorServiceImpl.resolveClubNumber()` centralises a new rule: `clubNumber` is required only when the
  competitor's home club is HPSC, and forced to `null` for every other home club, on create, update and patch
- `Competitor.clubNumber` column relaxed to nullable via new `V7_4_0__make_club_number_nullable.sql`, which also
  clears `club_number` on any existing non-HPSC competitor

### 🛡️ Coverage Floor Reaches Its Real Baseline

- JaCoCo `LINE`/`COVEREDRATIO` minimum tightened `0.86` → `0.97`, after the 86% floor was confirmed holding cleanly
  in CI on both the `develop` and `main` `build.yml` runs that shipped v8.3.1
- A fresh `./mvnw verify -Pcoverage` run at this release's prep time measured 98.44%/98.98% (line/branch), 868
  tests — up from 836 — confirming the new floor holds with a small, deliberate margin. Closes Gap #4

### 🔐 Tomcat Security Patch

- `tomcat-embed-core`/`-el`/`-websocket` overridden `11.0.24` → `11.0.25` via a new `pom.xml` `tomcat.version`
  property, closing three critical CVEs still transitively pinned by `spring-boot-starter-parent:4.1.1`

### 📚 Documentation & Convention Hardening

- New `AGENTS.md` Member ordering convention (constructors → public → protected → private);
  `IpscCompetitorServiceImpl`/`IpscMatchServiceImpl` reordered to match
- REST URL-path/handler-naming rules promoted from a recommendations doc into an actual `AGENTS.md` convention
- New Release Checklist step verifies `ARCHITECTURE.md`'s Project Structure tree against disk at every release
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` restructured into ✅ Completed/
  🟡 Partially Completed/⚪ Open sections, replacing the previous flat Now/Next/Later/Ongoing phasing

---

## 📦 What's New

### Added

#### Database

- **`V7_3_0__seed_club_data.sql`:** Seeds the `club` table with the 5 named `ClubIdentifier` constants (`SOSC`,
  `HPSC`, `PMPSC`, `VISITOR`, `ALL`); `UNKNOWN` intentionally excluded as the enum's unmatched placeholder

#### Enums

- **`ClubIdentifier`:** New `ALL` constant (`"Eufees Clubs"` / `"All"` / `"ALL"`) representing a match hosted
  jointly by the three real clubs

### Changed

#### API

- **`IpscCompetitorController`/`IpscCompetitorServiceImpl`:** `clubNumber` required only when home club is HPSC;
  `CompetitorRequest.clubNumber` no longer a Jackson-required property
- **`IpscMatchController`/`IpscMatchServiceImpl`:** `club` defaults to `DEFAULT_MATCH_CLUB_IDENTIFIER` instead of
  failing validation when missing/blank; `createMatch`/`updateMatch`/`patchMatch` now declare `throws
  FatalException` to carry the new defensive null-default check

#### Configuration

- **`pom.xml`:** JaCoCo `LINE`/`COVEREDRATIO` minimum raised `0.86` → `0.97`. Closes Gap #4

#### Constants

- **`SystemConstants`:** New `TIME_FORMAT`, `DEFAULT_DATE_FORMAT` and `DEFAULT_DATE_TIME_FORMAT` constants; unused
  `LONG_DATE_FORMAT`/`LONG_DATE_TIME_FORMAT` dropped
- **`SystemConstants`, `IpscConstants`:** Every constant actually referenced elsewhere gained a one-line field
  Javadoc

#### Database

- **`Competitor.clubNumber`:** Column relaxed from `nullable = false` to nullable

#### Documentation

- **`AGENTS.md`:** New Tech Stack note on Flyway migration versioning being independent of the app version; new
  REST conventions and Member ordering subsections; new Release Checklist step 9 (renumbering 9/10 → 10/11)
- **`documentation/recommendations/flyway-migration-versioning.md`:** New recommendations doc
- **`CONTRIBUTING.md`:** Matching condensed bullets for the new REST naming and member-ordering conventions
- **`documentation/roadmap/improvement-plan.md`, `improvement-plan-tasks.md`:** Restructured into ✅ Completed/
  🟡 Partially Completed/⚪ Open sections

#### Models

- **`AwardRequestForCSV`, `CompetitorRequest`, `CompetitorRequestForCSV`, `MatchRequest`, `MatchRequestForCSV`:**
  `@JsonFormat` patterns repointed from the removed `HpscConstants` to `SystemConstants`/`IpscConstants` equivalents

#### Services

- **`IpscCompetitorServiceImpl`, `IpscMatchServiceImpl`:** Reordered to match the new Member ordering convention

#### Tests

- Extensive coverage added/updated across `IpscCompetitorService(Impl)?Test`, `IpscMatchService(Impl)?Test` and
  `IpscMatchControllerTest` for the club-default and clubNumber-nullability behaviour changes, `FatalException`
  propagation, and the `resolveClub(String, ClubIdentifier)` overload

#### Tooling

- **`sync-improvement-plan-gaps`, `update-improvement-plan-gaps`:** Updated for the new gap-status section structure

### Fixed

#### Documentation

- **`ARCHITECTURE.md`:** Brought fully back in sync with disk — missing `.claude/skills/`, `documentation/
  recommendations/` and `db/migration/` directories added; `Gender`/`GenderConverter`, `HpscConstants` removal,
  bidirectional-entity-relationship inaccuracies, the stale 51% coverage figure and several Project Structure tree
  drifts all corrected
- **`README.md`:** Installation/Execution steps corrected to match the actual `MYSQL_USER`/`MYSQL_PASSWORD`
  env-var-driven `dev` profile flow
- **`HISTORY.md`:** Restored "Standards Adoption" wording after an earlier typo-fix pass had singularised it

### Removed

#### Constants

- **`HpscConstants`:** Removed entirely — its sole constant was an alias for `SystemConstants.ISO_DATE_FORMAT`

### Security

- **`tomcat-embed-core`/`tomcat-embed-el`/`tomcat-embed-websocket`:** Overridden `11.0.24` → `11.0.25`, closing
  [CVE-2026-68525](https://nvd.nist.gov/vuln/detail/CVE-2026-68525),
  [CVE-2026-65905](https://nvd.nist.gov/vuln/detail/CVE-2026-65905) and
  [CVE-2026-65182](https://nvd.nist.gov/vuln/detail/CVE-2026-65182)

---

## 🔄 Migration Guide

### For API Consumers

- **Match `club` is no longer required.** Omitting it, or sending a blank value, on `POST`/`PUT /ipsc/matches` now
  resolves to `"Eufees Clubs"` (`ClubIdentifier.ALL`) instead of a `400 Bad Request` ("Club is required.").
  Consumers relying on that validation error to catch a missing club should switch to checking the response for
  the default club name instead.
- **Competitor `clubNumber` is no longer always required.** It's now required only when `homeClub` is `"HPSC"`;
  supplying it for any other home club (or none) is accepted but silently ignored — the stored value is always
  `null` in that case.

### For Developers

- Two new Flyway migrations (`V7_3_0__seed_club_data.sql`, `V7_4_0__make_club_number_nullable.sql`) run
  automatically against MySQL dev/prod profiles on next startup; the `test` profile's H2 `create-drop` schema
  already reflects the current entity/column state.
- `HpscConstants` no longer exists — any external tooling or forked code referencing it must switch to
  `SystemConstants.DEFAULT_DATE_FORMAT` or `IpscConstants.IPSC_INPUT_DATE_FORMAT`.
- The JaCoCo coverage floor is now 97% (up from 86%) — a genuine coverage regression that would previously have
  passed CI between 86% and 97% will now fail the build.

---

## 📊 Statistics

- **Total Commits:** 52
- **Files Changed:** 48
- **Insertions:** 2,448 lines
- **Deletions:** 696 lines
- **Net Change:** +1,752 lines
- **New Source Files:** 0
- **Deleted Files:** 1 (`HpscConstants.java`)
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Mirror an existing pattern rather than invent a new one.** `IpscMatchServiceImpl.resolveClub()`'s
  "default, then validate the default exists" shape directly mirrors
  `IpscCompetitorServiceImpl.resolveHomeClub()`/`resolveClubNumber()`, so the match domain gains defaulting
  behaviour without introducing a second convention for the same problem.
- **Defend against the default itself being unset, even though it never is today.** `resolveClub()` throws a new
  `FatalException` if `IpscConstants.DEFAULT_MATCH_CLUB_IDENTIFIER` is null — a deliberately defensive check for a
  constant that's always `ClubIdentifier.ALL` in practice, matching the same "stays unit testable" pattern already
  used for `HOME_CLUB_IDENTIFIER`.
- **Tighten the coverage floor only after the previous threshold proves itself in CI.** The 86% → 97% jump waited
  for confirmation that the 86% floor held cleanly across a full `develop` → `main` promotion (v8.3.1) before being
  raised again, rather than jumping straight to the real baseline in one step.
- **Promote a recommendation to a convention only once it's actually been followed in practice.** The REST
  naming and member-ordering rules existed as prose recommendations before this release; both are now stated as
  enforced `AGENTS.md` conventions, reflecting patterns already consistently applied across the codebase.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` — full suite passing (868 tests, 0 failures/errors); line coverage 98.44%, branch
  coverage 98.98% — comfortably above the new 97% floor.
- New tests cover `resolveClub`/`resolveClubNumber`'s default-application and validation paths, `FatalException`
  propagation through `IpscMatchController`, and the `resolveClub(String, ClubIdentifier)` overload directly.

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- The `BRANCH` coverage counter is still not separately enforced by the JaCoCo `check` execution — only `LINE` is,
  as established when the gate was first added in v8.3.1.

---

## 🔮 Future Enhancements

- Build a `MatchScoreService`/`ShooterLogService` (interface + `impl/` split) over the existing repositories,
  following the same phased pattern that closed Gap #1 and Gap #8.
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint — their
  `@JsonCreator` constructors and required-field enforcement are already correct and ready for this.
- Consider enforcing a `BRANCH`-level JaCoCo minimum alongside the existing `LINE` one, now that the `LINE` floor
  sits close to its real baseline.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.4.0 extends the competitor module's "domain default" pattern to matches, closes out the coverage-floor
work started in v8.3.1 by reaching a genuinely near-baseline 97% minimum, and removes a stale `HpscConstants` class
in favour of the shared date-format constants introduced alongside it. Closes
`documentation/roadmap/improvement-plan.md`'s Gap #4 and Gap #9.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
