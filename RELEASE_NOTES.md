# Release Notes – Version 8.2.0

**Release Date:** September 1, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Competitor Multi-Email Support & Bulk CSV Separator Standardisation**

Version 8.2.0 is a domain feature release: competitors can now hold more than one email address, and every bulk CSV
endpoint's multi-value cell format is unified onto a single, shared separator. `Competitor.emailAddress` becomes
`emailAddresses` (`List<String>`), backed by a new `competitor_email` child table; `AwardService`/`ImageService`'s
bulk CSV parsing switches from `|` to `;` to match. A roadmap audit run as part of this release also found that
`.github/workflows/qodana.yml` — added in v8.1.1 — had actually been failing on every run since; rather than fix
it, Qodana static analysis is removed from the project entirely in this release.

---

## ⭐ Key Highlights

### ✉️ Competitor Multi-Email Support

- **`Competitor.emailAddresses`** — replaces the single, optional `emailAddress` `String` with a `List<String>`,
  mapped via `@ElementCollection`/`@CollectionTable` onto a new `competitor_email` child table
  (`competitor_id` FK, `email_address`) — a competitor can now have zero or more email addresses
- **`V7_2_0__add_competitor_emails.sql`** — new Flyway migration creates the child table, backfills it from any
  existing non-blank `email_address` values, then drops that column
- **`CompetitorRequest`/`CompetitorResponse`** — `emailAddress` renamed to `emailAddresses`;
  **`CompetitorRequestForCSV`** keeps a single CSV cell but now holds zero or more semicolon-separated addresses
  (e.g. `"a@x.com;b@x.com"`), split into a list via `IpscCompetitorServiceImpl`'s new `splitEmailAddresses` helper

### 🔀 Bulk CSV Separator Standardisation

- **`SystemConstants.ARRAY_SEPARATOR`** — new shared `";"` constant; `AwardServiceImpl`/`ImageServiceImpl`'s bulk
  CSV parsing switched from `"|"` to it, so competitor email addresses and image/award tags now share one
  multi-value cell convention across every bulk CSV endpoint
- `AwardController`/`ImageController`/`IpscCompetitorController`'s Swagger examples updated to match

### 🐛 Multi-Address Test Coverage & a Real Bug Found Along the Way

- New tests exercise a genuine multi-element `emailAddresses` list (not just single-address or empty/null cases)
  across every layer this feature touches: `CompetitorRequest` JSON (de)serialisation, `IpscCompetitorServiceImpl`'s
  `applyFields`/`toResponse` helpers, and `IpscCompetitorService`'s `createCompetitor`/`createCompetitors`/
  `patchCompetitor` contract, at both the unit and Spring-context integration level
- Writing the `patchCompetitor` integration test surfaced a real bug: `applyFields`/`patchCompetitor` stored the
  caller-supplied `List` reference directly onto the entity. An immutable list (e.g. `List.of(...)`) crashed with
  an unhandled `UnsupportedOperationException` when Hibernate merged the update — bypassing the
  `FatalException`/`NonFatalException`/`ValidationException` hierarchy entirely and surfacing as a raw 500. Both
  methods now defensively copy into a new `ArrayList` before storing

### 🗑️ Qodana Static Analysis Removed

- **`.github/workflows/qodana.yml`, `qodana.yaml`** — `documentation/roadmap/improvement-plan.md` previously
  recorded this workflow as merely "not yet verified as succeeding." This release's own audit
  (`gh run list --workflow=qodana.yml`) found it has actually failed on every run since v8.1.1 added it: a missing
  `QODANA_TOKEN` repository secret (release-line Qodana linters require one since 2023.2), plus an unconditional
  `github/codeql-action/upload-sarif@v4` step that also fails with `Input required and not supplied: sarif_file`
  when the scan produced nothing to upload. Rather than fix both issues, both files are removed, along with every
  Qodana reference across `ARCHITECTURE.md`, `CONTRIBUTING.md` and `AGENTS.md`'s CI/CD documentation — Gap #7 is
  closed as not applicable rather than delivered

---

## 📦 What's New

### Changed

#### Domain

- `Competitor.emailAddress` (`String`) replaced with `emailAddresses` (`List<String>`), backed by a new
  `competitor_email` child table

#### Models

- `CompetitorRequest`, `CompetitorResponse` — `emailAddress` renamed to `emailAddresses`
- `CompetitorRequestForCSV` — `emailAddress` renamed to `emailAddresses`; still a single CSV cell, now holding
  zero or more semicolon-separated addresses

#### Services

- `IpscCompetitorServiceImpl` — `applyFields`, `patchCompetitor`, `toRequest`, `toResponse` updated for
  `emailAddresses`; new `splitEmailAddresses` helper

#### Controllers

- `IpscCompetitorController` — bulk CSV endpoint's Swagger example header updated from `EmailAddress` to
  `EmailAddresses`
- `AwardController`, `ImageController` — Swagger examples updated for the `;` separator

#### Database

- `V7_2_0__add_competitor_emails.sql` — new Flyway migration for the `competitor_email` table and backfill

#### Constants

- `SystemConstants.ARRAY_SEPARATOR` — new shared `";"` constant, adopted by `AwardServiceImpl`/`ImageServiceImpl`

#### Documentation

- `ARCHITECTURE.md`, `CONTRIBUTING.md` — CI/CD & Quality Gates tables' `Static Analysis` row removed
- `AGENTS.md` — `CodeQL/Qodana/JaCoCo` trigger reference updated to `CodeQL/JaCoCo`
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` — Gap #7 (Qodana CI wiring) closed as not
  applicable

### Fixed

#### Services

- `IpscCompetitorServiceImpl` — `applyFields`/`patchCompetitor` now defensively copy `emailAddresses` into a new
  `ArrayList` instead of storing the caller-supplied `List` reference directly, avoiding an unhandled
  `UnsupportedOperationException` if that list is immutable

### Removed

#### CI/CD & Configuration

- `.github/workflows/qodana.yml`, `qodana.yaml` — Qodana static analysis removed after failing on every CI run
  since v8.1.1 added it (see Key Highlights)

---

## 🔄 Migration Guide

### For API Consumers

- `IpscCompetitorController`'s JSON request/response bodies now carry `emailAddresses` (a JSON array) instead of
  `emailAddress` (a single string) — update any client deserialising `CompetitorRequest`/`CompetitorResponse`.
- The competitor bulk CSV endpoint's `EmailAddress` column is now `EmailAddresses`, and multiple values in that
  cell must be separated with `;` rather than any other delimiter.
- The award and image bulk CSV endpoints' multi-value columns (`tags`) now use `;` as the separator instead of `|`
  — update any client generating CSV for these endpoints.

### For Developers

- Existing `competitor.email_address` data is preserved: the Flyway migration backfills it into the new
  `competitor_email` table before dropping the old column, so no manual data migration is required beyond running
  the migration itself.
- Qodana static analysis is gone from CI and from the project entirely — `.github/workflows/qodana.yml` and
  `qodana.yaml` no longer exist. No local setup is required to accommodate this; there's simply nothing to run.

---

## 📊 Statistics

- **Total Commits:** 11
- **Files Changed:** 29
- **Insertions:** 363 lines
- **Deletions:** 191 lines
- **Net Change:** +172 lines
- **New Source Files:** 1 (`V7_2_0__add_competitor_emails.sql`)
- **Deleted Files:** 2 (`.github/workflows/qodana.yml`, `qodana.yaml`)
- **New Test Files:** 0
- **Deleted Test Files:** 0

---

## 🧭 Design Notes

- **One separator, not two.** Competitor email addresses and image/award tags were independently using `;` and `|`
  respectively for the same kind of thing — a multi-value CSV cell. Introducing `SystemConstants.ARRAY_SEPARATOR`
  and standardising both onto `;` removes a needless inconsistency rather than adding a new one.
- **A child table, not a delimited column.** `emailAddresses` is modelled as a proper `@ElementCollection` table
  rather than a delimited string column, keeping the domain model queryable and consistent with how the rest of the
  schema represents one-to-many data; the CSV-cell delimiting is a transport-format concern confined to
  `CompetitorRequestForCSV`, not the entity itself.
- **Verify CI claims against the Actions tab, not the workflow file.** `qodana.yml` read as complete and correctly
  configured; only checking `gh run list` against real run history revealed it had never actually succeeded — and
  a workflow that has never worked isn't worth patching over removing, once no working baseline exists to protect.
- **A genuinely multi-element test case finds bugs a singleton doesn't.** Every existing `emailAddresses` test used
  either a single-element list or null/empty — none had ever exercised a list with more than one address moving
  through a real Hibernate merge. Adding that case is what surfaced the `UnsupportedOperationException` bug, not a
  code review of the mapping logic itself.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (790 tests, up from 781; 0 failures/errors).
- Updated tests: `IpscCompetitorControllerTest`, `CompetitorRequestForCSVTest`, `CompetitorRequestTest`,
  `AwardServiceIntegrationTest`, `AwardServiceTest`, `ImageServiceIntegrationTest`, `ImageServiceTest`,
  `IpscCompetitorServiceIntegrationTest`, `IpscCompetitorServiceTest`, `AwardServiceImplTest`, `ImageServiceImplTest`,
  `IpscCompetitorServiceImplTest` — all updated for the `emailAddresses` shape and the `;` separator.
- New genuinely-multiple-address tests added to `CompetitorRequestTest`, `IpscCompetitorServiceImplTest`,
  `IpscCompetitorServiceTest` and `IpscCompetitorServiceIntegrationTest`, closing the gap where every existing
  `emailAddresses` test used only a single-element list (or null/empty).

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- No automatic build/test gate runs on pull requests yet — `./mvnw test`/`./mvnw verify -Pcoverage` remain
  reviewer/local-only (carried over from v7.2.0).
- No static analysis gate runs in CI following Qodana's removal (see Key Highlights) — CodeQL (security) and
  JaCoCo (coverage) remain the only automated quality gates.

---

## 🔮 Future Enhancements

- If static analysis in CI is wanted again, scope it as a new roadmap gap rather than re-adding Qodana as
  previously configured — a `QODANA_TOKEN` secret would still be required, or a Community linter that doesn't need
  one.
- Add a `build.yml` (or extend `codeql.yml`'s trigger set) running `./mvnw verify -Pcoverage` on push/PR, and a
  JaCoCo coverage-check rule wired into it, so a coverage regression fails the build automatically.
- Build a `MatchScoreService`/`ShooterLogService` (interface + `impl/` split) over the existing repositories,
  following the same phased pattern that closed Gap #1.
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint —
  their `@JsonCreator` constructors and required-field enforcement are already correct and ready for this.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.2.0 extends the competitor domain to support more than one email address and closes a lingering
inconsistency between the competitor and award/image bulk CSV endpoints' multi-value cell formats. A roadmap audit
run alongside the release also surfaced that the Qodana CI workflow added in v8.1.1 has never actually succeeded;
tracked as an updated finding on Gap #7 rather than fixed in this release, since it's an infrastructure/secrets
concern independent of this version's domain changes.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
