# Release Notes – Version 8.9.0

**Release Date:** September 26, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Competitor Paid-Up Flags, Explicit Transaction Boundary & Lazy Loading**

Version 8.9.0 is a minor feature release. Competitors gain two new flags recording whether their SAPSA and club
memberships are paid up, available through the JSON API and the CSV bulk import alike. Underneath, the persistence
layer is made explicit: every `@ManyToOne` association is loaded lazily, reads that build a response fetch their
associations through dedicated `left join fetch` queries, and every competitor and match write is committed by a
single new `TransactionService` in its own explicit transaction — the IPSC services no longer declare
`@Transactional` at all. A match's stages become a cascaded collection of the match itself. The release also clears
the improvement plan of every documentation-accuracy gap: Gaps #13–#17, plus Gaps #18–#24 found and fixed by this
release's own audit, leaving only Gap #6 — the scoring/shooter-log layer — open.

---

## ⭐ Key Highlights

### 🏷️ Competitor Paid-Up Flags

- New nullable `paidUpSapsa`/`paidUpClub` fields on `CompetitorRequest`, `CompetitorRequestForCSV` and
  `CompetitorResponse`, stored in new `competitor` columns via Flyway migration `V7_8_0`
- An omitted flag is stored as `null` on create/update and left unchanged on patch
- The competitor CSV now requires `PaidUpSapsa` and `PaidUpClub` header columns (values may be blank) — see the
  Migration Guide

### 🔒 Explicit Transaction Boundary

- New `TransactionService` commits every competitor and match save and delete in its own `TransactionTemplate`
  transaction
- `IpscCompetitorService`/`IpscMatchService` validate and build entities outside any transaction, then hand them over
- Bulk CSV imports build every row before saving any, then save all rows in one transaction — still all-or-nothing

### 💤 Lazy Loading, Loaded Up Front

- Every `@ManyToOne` switched from `EAGER` back to `LAZY`, so loading an entity no longer drags in its whole parent
  chain
- New fetch-join queries load a match's club and a competitor's home club and email addresses with them, since
  `spring.jpa.open-in-view` is disabled
- `IpscMatch.stages` is now a cascaded, orphan-removing collection — deleting a match removes its stages by cascade

### 🛤️ Roadmap

- Gaps #13–#17 closed, and Gaps #18–#24 recorded and closed by this release's own audit (#23 as not applicable)
- Only Gap #6 — the match scoring / shooter-log service and controller layer — remains open

---

## 📦 What's New

### Added

#### Domain

- **`Competitor.paidUpSapsa`, `Competitor.paidUpClub`:** New nullable `Boolean` columns — whether a competitor's
  SAPSA and club memberships are paid up
- **`IpscMatch.stages`:** New `@OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)`
  collection — the domain model's only bidirectional, cascaded relationship; no schema change

#### Repositories

- **`IpscMatchRepository.findByIdWithClub`, `findAllWithClub`:** New `left join fetch` queries loading a match's
  `club` with it
- **`CompetitorRepository.findByIdWithHomeClubAndEmailAddresses`, `findAllWithHomeClubAndEmailAddresses`:** New
  `left join fetch` queries loading a competitor's `homeClub` and `emailAddresses` with it

#### Services

- **`TransactionService`, `TransactionServiceImpl`:** New service committing competitor and match writes —
  `saveCompetitor`/`saveCompetitors`/`deleteCompetitor`, `saveMatch`/`saveMatches`/`deleteMatch`, plus a `saveMatch`
  overload that replaces or upserts an existing match's stages — each in its own explicit transaction, returning
  entities with the associations their responses read already loaded

#### API Models

- **`CompetitorRequest`, `CompetitorRequestForCSV`, `CompetitorResponse`:** New `paidUpSapsa`/`paidUpClub` fields

#### Database

- **`V7_8_0__add_competitor_paid_up_flags.sql`:** Adds nullable `paid_up_sapsa` and `paid_up_club` `BOOLEAN` columns
  to `competitor`

#### Tests

- **`TransactionServiceTest`, `TransactionServiceImplTest`, `TransactionServiceIntegrationTest`:** `TransactionService`'s
  full 3-tier split, including every commit method's rollback on failure and real H2 commits/rollbacks
- **Six new repository integration test classes** (`src/test/.../repositories/`): the fetch-join queries, the stage
  cascade (persist, orphan removal, delete), the email collection's delete and every `existsBy…` check behind the
  reject-not-cascade deletes
- **`IpscMatchServiceIntegrationTest`, `IpscCompetitorServiceIntegrationTest`:** New tests run with no surrounding
  transaction, so each create/update/patch/delete must really commit, plus a check that a bulk match import with one
  bad row commits nothing
- Paid-up flag coverage across the competitor service, request and CSV model tests

#### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gaps #14–#24, all closed within this release

### Changed

#### Controllers

- **`IpscCompetitorController.createCompetitors`:** The competitor CSV header now requires trailing `PaidUpSapsa`
  and `PaidUpClub` columns

#### Domain

- **All seven entities with a `@ManyToOne`:** Now `FetchType.LAZY` instead of `FetchType.EAGER`, reversing v8.7.0
- **`Competitor.emailAddresses`:** The `email_address` element column mapping no longer declares `nullable = false`

#### Services

- **`IpscMatchServiceImpl`, `IpscCompetitorServiceImpl`:** No longer `@Transactional` — they commit through
  `TransactionService`; bulk imports save all rows in one transaction, and match create/update/patch responses list
  stages ordered by stage number
- **`IpscMatchServiceImpl`, `IpscCompetitorServiceImpl`:** Load through the new fetch-join queries, so responses can
  read associations outside a transaction
- **`IpscMatchServiceImpl.deleteMatch`:** Commits through `TransactionService.deleteMatch`, with stages removed by
  cascade; the reject-not-cascade checks are unchanged
- **`TransactionServiceImpl.replaceStages`, `upsertStages`:** Moved from `IpscMatchServiceImpl`, now working on the
  managed `IpscMatch.stages` collection

#### Tests

- **`IpscMatchServiceTest`, `IpscCompetitorServiceTest`:** Built with a real `TransactionServiceImpl` over the same
  repository mocks and a mocked `PlatformTransactionManager`

#### CI/CD & Configuration

- **`claude-code-review.yml`:** The commented-out `paths:` example now uses Java globs instead of its template's
  TypeScript/JavaScript ones

#### Tooling

- **`prep-version-release`, `AGENTS.md`:** The Release Checklist now makes updating `HISTORY.md`'s Future Roadmap
  Implications log and "Major Version Goals" mandatory for every release

#### Documentation Updates

- **`ARCHITECTURE.md`, `AGENTS.md`, `CONTRIBUTING.md`, service Javadoc:** Describe `TransactionService` as where writes
  are committed, and the bulk imports' validate-then-save-in-one-transaction behaviour

#### Build & Metadata

- Project version bumped to **8.9.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

### Removed

#### Build & Metadata

- **`jackson-dataformat-xml`, `commons-lang3`:** Unused dependencies dropped — nothing produced or consumed XML or
  used Apache Commons

### Fixed

#### Documentation

- **`ARCHITECTURE.md`:** CI/CD & Quality Gates table lists the Claude Code review and assistant workflows (Gap #13);
  the Quality Attributes and Persistence Layer sections agree on cascade/`mappedBy` (Gap #15); the non-existent
  "Strategy Pattern" converters replaced with a "Transaction Boundary" row (Gap #19); data flows and repository
  descriptions route writes through `TransactionService` (Gap #21)
- **`flyway-migration-versioning.md`:** Current State table covers every migration through `V7_8_0`, with a step
  keeping it current (Gap #14), and a wrong cross-reference removed
- **`CONTRIBUTING.md`:** The Running Tests example names a test that exists (Gap #16)
- **`HISTORY.md`:** Future Roadmap Implications log and "Major Version Goals" backfilled through v8.8.0 (Gap #17);
  the `Competitor.homeClub` backfill dropped from the roadmap as not applicable (Gap #23)
- **`AGENTS.md`:** 3-tier test rule names all five services (Gap #20); HPSC expanded as "Hartbeespoortdam Practical
  Shooting Club" (Gap #22)
- **`README.md`, `ARCHITECTURE.md`, `AGENTS.md`:** Tech stack no longer claims XML or Apache Commons (Gap #18);
  `README.md`'s Testing section describes the suite as it is (Gap #24)

---

## 🚀 Migration Guide

**Competitor CSV bulk import (`POST /ipsc/competitors/bulk`):** the header must now include `PaidUpSapsa` and
`PaidUpClub` columns. Values may be left blank (stored as `null`); a file without the two header columns is
rejected with `400 Bad Request`. Add them to existing CSV templates:

```csv
FirstName,LastName,...,EmailAddresses,PaidUpSapsa,PaidUpClub
Jane,Doe,...,jane.doe@example.com,true,
```

**Database:** Flyway applies `V7_8_0__add_competitor_paid_up_flags.sql` automatically on startup against MySQL. It
only adds two nullable columns, so existing rows are unaffected.

**Behaviour changes worth checking in clients:**

| Change                              | Effect                                                                                      |
|-------------------------------------|---------------------------------------------------------------------------------------------|
| Match create/update/patch responses | `stages` are ordered by stage number, as `GET` already returned them — not in request order |
| XML content negotiation             | `Accept: application/xml` is no longer served; every endpoint returns JSON                  |
| Bulk CSV imports                    | Still all-or-nothing; a bad row now fails before anything is written                        |

No endpoint was added or removed, and no JSON field was removed or made required.

---

## 📊 Statistics

- **Total Commits:** 23 (the paid-up flags feature and its follow-ups, Gaps #13–#17, lazy loading, fetch-join queries,
  `TransactionService`, the HISTORY.md checklist changes, plus this release's version bump, Gaps #18–#24, release
  documentation and PR description commits)
- **Files Changed:** 57
- **Insertions:** 3,965 lines
- **Deletions:** 672 lines
- **Net Change:** +3,293 lines
- **New Source Files:** 3 (`TransactionService`, `TransactionServiceImpl`, `V7_8_0__add_competitor_paid_up_flags.sql`)
- **Deleted Files:** 0
- **New Test Files:** 10 (six repository integration tests, the `ScoringFixtures` helper and three `TransactionService`
  test tiers)

---

## 🧭 Design Notes

- **One place commits.** Spreading `@Transactional` across service methods hid where each transaction began and
  ended. `TransactionService` now owns every competitor/match write, so the boundary is one class, and the services
  above it are plain validation and mapping code.
- **Lazy by default, fetched on purpose.** With `open-in-view` disabled, a lazy association read after the
  transaction closes throws. Rather than go back to eager loading everywhere, the reads that build responses use
  `left join fetch` queries, and `TransactionService` loads the same associations before returning an entity.
- **Cascade only for composition.** A stage can't exist without its match, so `IpscMatch.stages` cascades; every
  other relationship stays unidirectional and reject-not-cascade, protecting scoring history.
- **Delete, flush, then insert.** Replacing stages that reuse a stage number would trip the `(match_id,
  stage_number)` unique constraint if Hibernate ordered the inserts first, so the old stages are deleted and flushed
  before the new ones are saved. `orphanRemoval` alone isn't enough: it only sees removals relative to the
  collection's last flushed snapshot.
- **Build everything, then save once.** Bulk imports validate and build every row before touching the database, so a
  bad row costs nothing and the single save keeps the import atomic.
- **Test without the safety net.** An `@Transactional` test absorbs the code's own transactions, hiding commit and
  lazy-loading bugs. The tests that prove real commits run without a surrounding transaction and clean up after
  themselves.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (966 tests, 0 failures/errors), up from 903 in v8.8.0.
- `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing.
- 63 new tests: `TransactionServiceTest`/`TransactionServiceImplTest`/`TransactionServiceIntegrationTest` (every
  commit method, rollback on failure, stage replace/upsert, real H2 commits and rollbacks); six repository
  integration test classes (fetch-join queries, stage cascade persist/orphan removal/delete, email collection delete,
  every `existsBy…` check); no-transaction lifecycle tests for both IPSC services and an all-or-nothing bulk match
  import check; and paid-up flag coverage in the competitor service and model tests.

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- A competitor or match referenced by results or shooter logs can't be deleted through the API, since no endpoint
  removes those rows yet (carried over from v8.8.0, pending Gap #6).
- The `BRANCH` coverage counter is still not separately enforced by the JaCoCo `check` execution — only `LINE` is.
- The domain model has repository integration tests but no entity-level unit tests.

---

## 🔮 Future Enhancements

- Build a `MatchScoreService`/`ShooterLogService` over the existing repositories, committing through
  `TransactionService`, following the same phased pattern that closed Gap #1 (Gap #6).
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint.
- Add entity-level unit tests for the domain model.
- Consider enforcing a `BRANCH`-level JaCoCo minimum alongside the existing `LINE` one.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.9.0 adds one small, client-visible feature — competitor paid-up flags — and spends the rest of its weight
making the persistence layer's behaviour explicit and tested at every tier. The only client-facing migration is the
competitor CSV's two new header columns.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
