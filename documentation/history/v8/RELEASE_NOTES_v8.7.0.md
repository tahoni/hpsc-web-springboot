# Release Notes – Version 8.7.0

**Release Date:** September 24, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Competitor Listing Endpoint, Stage Delimiter Change & Dependency Clean-up**

Version 8.7.0 is a minor feature release. It adds `GET /ipsc/competitors`, which lists every competitor, so the
competitor API now has the same "get all" read that the match API already had through `getAllMatches`. It also
brings in two client-facing changes. A match CSV's `Stages` cell now separates each stage number from its name with
`:` instead of `-`, and the app now runs on Spring Boot's default port `8080` instead of `8081`. Behind the API, every
domain `@ManyToOne` association switches to eager fetching, and springdoc moves to its Spring Boot 4 line (`3.1.0`),
with its version managed by an imported BOM. The unused Spring REST Docs dependency is removed. `getAllMatches`'
Swagger response is corrected to describe an array. This release's own improvement-plan audit recorded a new
Gap #12: competitors and matches are documented as "full CRUD", yet neither can be deleted.

---

## ⭐ Key Highlights

### 🌐 Competitor Listing Endpoint

- New `GET /ipsc/competitors` returns every competitor as a JSON array of `CompetitorResponse`s, or `[]` when there
  are none
- Documented in Swagger as an array, and `GET /ipsc/matches` now is too — it previously described a single
  `MatchResponse`

### 📥 Stage Delimiter Change

- Match CSV stage entries are now `<stageNumber>:<stageName>`, e.g. `"1:Stage One;2:Stage Two"`
- Only the first `:` splits the number from the name, so stage names can still contain a colon
- The old `1-Stage One` form is now rejected with `400 Bad Request` — see the Migration Guide

### ⚙️ Runtime & Dependencies

- The app now listens on port `8080` (Spring Boot's default); the app, Swagger UI and OpenAPI URLs in the
  documentation follow
- springdoc `2.8.5` → `3.1.0`, versioned through `springdoc-openapi-bom`; `spring-restdocs-mockmvc` removed
- Domain `@ManyToOne` associations switched from `LAZY` to `EAGER` fetching

---

## 📦 What's New

### Added

#### Controllers

- **`IpscCompetitorController.getAllCompetitors`:** New `GET /ipsc/competitors` endpoint returning every IPSC
  competitor as a JSON array of `CompetitorResponse`s — the collection counterpart to `GET /{competitorId}`,
  mirroring `IpscMatchController.getAllMatches`

#### Services

- **`IpscCompetitorService.getAllCompetitors`:** Returns every persisted competitor mapped to a
  `CompetitorResponse`, or an empty list when there are none

#### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #12, with a matching "⚪ Open" task block — the
  "full CRUD" claims in `README.md`/`ARCHITECTURE.md` have no delete operation behind them, and
  `standard-rest-conventions.md`'s current-state examples omit `getAllMatches`/`IpscCompetitorController`

### Changed

#### Services

- **`IpscMatchServiceImpl.parseStages`:** A match CSV's `Stages` cell now separates each entry's stage number from
  its name with `:` instead of `-`; only the first `:` splits, and entries in the old `1-Stage One` form are rejected
  with a `ValidationException`

#### Domain

- **`Competitor`, `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`, `MatchStageCompetitor`, `ShooterLog`,
  `ShooterLogCompetitor`:** Every `@ManyToOne` association switched from `FetchType.LAZY` to `FetchType.EAGER`

#### Configuration

- **`application.properties`:** `server.port=8081` override removed, so the app now starts on port `8080`;
  `README.md`, `AGENTS.md`, `ARCHITECTURE.md` and `CONTRIBUTING.md` updated to match

#### Build & Metadata

- Project version bumped to **8.7.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match
- **`springdoc-openapi-starter-webmvc-ui`:** Bumped from `2.8.5` to `3.1.0`, the springdoc line built for Spring
  Boot 4, with its version now coming from an imported `springdoc-openapi-bom` in a new `<dependencyManagement>`
  section

#### Documentation

- **`IpscMatchController`, `MatchRequestForCSV`, `improvement-plan.md`, `improvement-plan-tasks.md`:** Bulk CSV
  Swagger example, Javadoc and roadmap references updated to the `<stageNumber>:<stageName>` format
- **`improvement-plan.md`:** "🌳 At a Glance", the "🛤️ Roadmap" table's **Next** row and "☑️ Success Criteria"
  updated for Gap #12

### Removed

#### Build & Metadata

- **`spring-restdocs-mockmvc`:** Unused test dependency dropped from `pom.xml`, along with the Spring REST Docs
  mentions in `README.md`'s and `ARCHITECTURE.md`'s tech-stack lists and `HELP.md`'s reference links

### Fixed

#### Controllers

- **`IpscMatchController.getAllMatches`:** Swagger's `200` response schema now documents an array of
  `MatchResponse`s via `@ArraySchema`, matching the `List` the endpoint actually returns

#### Documentation

- **`ARCHITECTURE.md`'s Project Structure tree:** Stale directory comments corrected against disk
  (`documentation/history/`, `documentation/roadmap/`, the test `services/`/`services/impl/` tiers) and the missing
  `banner.txt` added

---

## 🚀 Migration Guide

**Match CSV stage delimiter (breaking for CSV imports).** Any CSV sent to `POST /ipsc/matches/bulk` must separate
each stage number from its name with `:`:

```
Before: 1-Stage One;2-Stage Two
After:  1:Stage One;2:Stage Two
```

Rows still using `-` now fail with `400 Bad Request` ("Invalid stage entry (expected
<stageNumber>:<stageName>)"). Stage names that contain a hyphen need no escaping.

**Server port.** The app no longer overrides Spring Boot's default port. Update anything pointing at `8081` —
bookmarks, API clients, reverse-proxy or firewall rules — to `8080`:

- App: `http://localhost:8080/hpsc-web`
- Swagger UI: `http://localhost:8080/hpsc-web/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/hpsc-web/v3/api-docs`

To keep `8081`, set `server.port=8081` in an environment-specific properties file or pass `--server.port=8081`.

**New endpoint.** `GET /ipsc/competitors` is additive; no existing endpoint's request or response shape changed.

---

## 📊 Statistics

- **Total Commits:** 16 (7 feature commits — stage delimiter, springdoc/REST Docs, port, eager fetching,
  springdoc BOM, competitor-listing endpoint and `getAllMatches` schema fix — plus this release's Gap #12,
  `HELP.md`, `ARCHITECTURE.md` tree, version bump, release documentation, PR description, two
  spelling/punctuation fix and statistics refresh commits)
- **Files Changed:** 36
- **Insertions:** 900 lines
- **Deletions:** 181 lines
- **Net Change:** +719 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Mirror the existing collection endpoint.** `getAllCompetitors` follows `getAllMatches` exactly — an unpaged
  `List` from `findAll()`, mapped through the service's existing `toResponse` — so both domains expose the same
  read operations the same way. Paging can be added to both together if list sizes ever call for it.
- **Document arrays as arrays.** A `@Schema(implementation = X.class)` on a `List` response makes Swagger describe a
  single object; wrapping it in `@ArraySchema` gives clients the correct contract. The new endpoint used the correct
  form from the start, and `getAllMatches` was fixed to match rather than left inconsistent.
- **Only the first separator splits.** Parsing each stage entry on its first `:` keeps stage names free-form (e.g.
  `"1:Stage One: The Bank Job"`), which is the same rule the `-` delimiter followed.
- **Let the BOM own springdoc's version.** Spring Boot's parent doesn't manage springdoc, so importing
  `springdoc-openapi-bom` keeps the version in one place instead of on each springdoc artefact.
- **Record, don't guess.** The "full CRUD" / no-delete mismatch surfaced during this release's audit, but choosing
  between adding deletes (with a policy for dependent score and shooter-log rows) and rewording the docs is a
  product decision, so it's tracked as Gap #12 rather than resolved in passing.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (878 tests, 0 failures/errors), up from 872 in v8.6.2.
- `./mvnw verify -Pcoverage` — 98.66% line / 99.00% branch coverage, JaCoCo gate passing.
- Six new tests for `getAllCompetitors`, two per tier: `IpscCompetitorControllerTest` (200 response, delegation),
  `IpscCompetitorServiceTest` (empty and populated repository, mocked) and `IpscCompetitorServiceIntegrationTest`
  (empty and populated H2 database).
- Existing stage-parsing tests in `IpscMatchServiceImplTest`, `IpscMatchServiceTest`, `MatchRequestForCSVTest` and
  `IpscMatchControllerTest` moved to the `:` delimiter; the "only the first separator splits" tests now use a colon
  inside the stage name.
- Verified that springdoc resolves to `3.1.0` through the imported BOM (`./mvnw dependency:tree`).

---

## 🐛 Known Issues

- Competitors and matches can't be deleted through the API, although `README.md`/`ARCHITECTURE.md` describe both as
  "full CRUD" (new Gap #12).
- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- The `BRANCH` coverage counter is still not separately enforced by the JaCoCo `check` execution — only `LINE` is,
  as established when the gate was first added in v8.3.1.

---

## 🔮 Future Enhancements

- Resolve Gap #12: either add `DELETE /ipsc/competitors/{competitorId}` and `DELETE /ipsc/matches/{matchId}`, with
  explicit handling of dependent rows, or reword the "CRUD" claims to "create, read and update".
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

Version 8.7.0 rounds out the competitor API's read operations and tidies the build's documentation dependencies.
The stage-delimiter and port changes are the only client-facing migrations; everything else is additive or internal.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
