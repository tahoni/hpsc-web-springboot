# Release Notes – Version 8.8.0

**Release Date:** September 24, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Competitor & Match Delete Endpoints**

Version 8.8.0 is a minor feature release. It adds `DELETE /ipsc/competitors/{competitorId}` and
`DELETE /ipsc/matches/{matchId}`, the last verb missing from both domains, so the "full CRUD" that `README.md` and
`ARCHITECTURE.md` have long described is now real. Deletion follows one rule: a record that scoring or shooter-log
history still points at is refused with `400 Bad Request` instead of being cascaded away, while the things a record
owns outright — a competitor's email addresses and a match's stages — are deleted with it. The release closes
Gap #12 in the improvement plan. Its own audit recorded a new Gap #13: the Claude Code review and assistant GitHub
workflows run on this repository but aren't listed in the CI/CD documentation.

---

## ⭐ Key Highlights

### 🌐 Delete Endpoints

- New `DELETE /ipsc/competitors/{competitorId}` and `DELETE /ipsc/matches/{matchId}`, each returning
  `204 No Content` on success
- `404 Not Found` when the competitor or match doesn't exist
- `400 Bad Request` when the record is still referenced by results or shooter logs

### 🛡️ Reject, Don't Cascade

- A competitor with match results (`MatchCompetitor`) or shooter logs (`ShooterLog`) can't be deleted
- A match with competitor results (`MatchCompetitor`), stage results (`MatchStageCompetitor`) or shooter-log
  entries (`ShooterLogCompetitor`) can't be deleted
- Owned data goes with its record: a competitor's emails and a match's stages are deleted in the same transaction

### 🛤️ Roadmap

- Gap #12 closed — the "CRUD" claims in `README.md`/`ARCHITECTURE.md` are now accurate
- New Gap #13 — document `claude.yml` and `claude-code-review.yml` in `ARCHITECTURE.md`'s CI/CD & Quality Gates
  table

---

## 📦 What's New

### Added

#### Controllers

- **`IpscCompetitorController.deleteCompetitor`, `IpscMatchController.deleteMatch`:** New
  `DELETE /ipsc/competitors/{competitorId}` and `DELETE /ipsc/matches/{matchId}` endpoints, returning
  `204 No Content` — `400` when the record is still referenced, `404` when it doesn't exist

#### Services

- **`IpscCompetitorService.deleteCompetitor`:** Deletes a competitor together with their email addresses, refusing
  with a `ValidationException` while any `MatchCompetitor` (match result) or `ShooterLog` row still references them
- **`IpscMatchService.deleteMatch`:** Deletes a match together with its stages, refusing with a
  `ValidationException` while any `MatchCompetitor`, `MatchStageCompetitor` or `ShooterLogCompetitor` row still
  references it — so scoring history is never deleted as a side effect
- **`IpscCompetitorServiceImpl.deleteCompetitor`, `IpscMatchServiceImpl.deleteMatch`:** The delete is flushed inside
  the method and a `DataIntegrityViolationException` rethrown as a `ValidationException`, so a reference added by
  another request between the dependent-row checks and the delete still returns `400` rather than `500`

#### Repositories

- **`MatchCompetitorRepository`, `MatchStageCompetitorRepository`, `ShooterLogRepository`,
  `ShooterLogCompetitorRepository`:** New `existsByCompetitorId`/`existsByMatchId`/`existsByMatchStageMatchId`
  queries backing the delete operations' dependent-row checks

#### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #13, with a matching "⚪ Open" task block —
  `claude.yml` (`@claude` assistant) and `claude-code-review.yml` (automated review on every PR) are live but
  missing from `ARCHITECTURE.md`'s CI/CD & Quality Gates table, its Project Structure tree comment and
  `CONTRIBUTING.md`'s summary of that table

### Changed

#### Documentation Updates

- **`ARCHITECTURE.md`:** New Service Layer note on the delete rule — owned emails and stages are removed with their
  record, but a record still referenced by scoring or shooter-log rows is refused rather than cascaded
- **`standard-rest-conventions.md`:** "🔍 Current State in This Codebase" now names `IpscCompetitorController`
  alongside `IpscMatchController` as full-pattern examples, covering every verb including `getAll` and `delete`
- **`improvement-plan.md`, `improvement-plan-tasks.md`:** Gap #12 closed in v8.8.0 and moved to ✅ Completed;
  "🌳 At a Glance", the "🛤️ Roadmap" table's **Next** row and "☑️ Success Criteria" updated for Gaps #12 and #13

#### Build & Metadata

- Project version bumped to **8.8.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

---

## 🚀 Migration Guide

No migration is needed. Both endpoints are additive; no existing endpoint's request or response shape changed, and
there is no schema migration.

Clients that start using the new endpoints should handle three outcomes:

| Status            | Meaning                                                                                    |
|-------------------|--------------------------------------------------------------------------------------------|
| `204 No Content`  | Deleted, together with the competitor's emails or the match's stages                       |
| `400 Bad Request` | Still referenced by match results, stage results or shooter logs — the message names which |
| `404 Not Found`   | No competitor or match with that ID                                                        |

A `400` lasts for as long as those references exist. There is no API for removing results or shooter logs yet, so
a referenced record can't currently be deleted through the API at all.

---

## 📊 Statistics

- **Total Commits:** 9 (the delete-endpoint feature commit, its REST-conventions and Gap #12 documentation commits,
  plus this release's Gap #13, version bump, release documentation, PR description, delete race-window fix and
  release documentation refresh commits)
- **Files Changed:** 28
- **Insertions:** 1,201 lines
- **Deletions:** 191 lines
- **Net Change:** +1,010 lines
- **New Source Files:** 0
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Reject, don't cascade.** Match results and shooter logs are the club's scoring history; deleting them as a side
  effect of removing a competitor or match would lose data nobody asked to lose. Refusing with a `400` that names
  the blocking rows leaves the decision with the caller, and keeps a raw foreign-key violation from ever reaching
  the client.
- **Owned data goes with its owner.** A competitor's emails and a match's stages mean nothing on their own, so
  they're deleted with their record rather than blocking it. Emails go through the `@ElementCollection`; stages are
  deleted as managed entities before the match, not with a bulk query, so the flush removes them first.
- **Check existence, not counts.** The dependent-row checks use Spring Data `existsBy…` queries, which stop at the
  first matching row rather than loading or counting them.
- **Close the race window.** The `existsBy…` checks and the delete aren't atomic, so another request could add a
  result in between. Each delete is therefore flushed inside the service method, where a foreign-key violation is
  caught and rethrown as the same `400` the checks give. Left to commit, it would surface only after the method
  had returned, as a `500`.
- **Look up first, then check.** Each delete resolves the record before checking references, so a missing ID is a
  `404` rather than a misleading "still referenced" `400`.

---

## 🧪 Testing

- `./mvnw test` — full suite passing (903 tests, 0 failures/errors), up from 878 in v8.7.0.
- `./mvnw verify -Pcoverage` — 98.70% line / 99.03% branch coverage, JaCoCo gate passing.
- 25 new tests for the delete operations: `IpscCompetitorControllerTest`/`IpscMatchControllerTest` (`204` response,
  delegation and propagated `ValidationException`/`NonFatalException`), `IpscCompetitorServiceTest`/
  `IpscMatchServiceTest` (delete, not found, each rejecting reference and a reference added before the flush,
  mocked) and
  `IpscCompetitorServiceIntegrationTest`/`IpscMatchServiceIntegrationTest` (not found, a real delete of the record
  with its emails or stages, and a referenced record being refused and kept, against H2).

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- A competitor or match referenced by results or shooter logs can't be deleted through the API, since no endpoint
  removes those rows yet — a consequence of the reject-not-cascade rule above, pending Gap #6.
- `claude.yml` and `claude-code-review.yml` aren't listed in `ARCHITECTURE.md`'s CI/CD & Quality Gates table
  (new Gap #13).
- The `BRANCH` coverage counter is still not separately enforced by the JaCoCo `check` execution — only `LINE` is,
  as established when the gate was first added in v8.3.1.

---

## 🔮 Future Enhancements

- Resolve Gap #13: add the Claude Code review and assistant workflows to `ARCHITECTURE.md`'s CI/CD & Quality Gates
  table and Project Structure tree, and to `CONTRIBUTING.md`'s summary.
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

Version 8.8.0 completes the competitor and match APIs' create, read, update and delete set. Everything in it is
additive, and the deletion rule errs on the side of keeping scoring history.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
