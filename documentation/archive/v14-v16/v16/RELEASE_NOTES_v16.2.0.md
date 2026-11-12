# Release Notes – Version 16.2.0

**Release Date:** November 10, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Verifying Competitors in Bulk**

Version 16.2.0 is a minor release. Shooter results count only verified competitors, but the only way to verify an
existing one was a `PUT` or `PATCH` per competitor, and a match competitor import gave no sign that a row's competitor
would be left out of results. A new endpoint verifies several competitors from a list of their IDs in one transaction,
and an import row for an unverified competitor now carries a message saying so. Nothing existing changes shape, so the
release is backward-compatible.

---

## ⭐ Key Highlights

### ✅ Bulk Verification

- `POST /ipsc/competitors/verify` marks several competitors as verified from a list of their IDs and returns one result
  per distinct ID, in the order first given
- Every competitor that exists is verified in a single transaction through `TransactionService.saveCompetitors`, and one
  that is already verified is left as it is and reported as such
- An ID that matches no competitor is reported in its own result and does not fail the others; an empty list, or one
  containing a null, is refused with a `400`

### 💬 Say Why a Row Is Left Out

- A match competitor import row whose competitor is not verified is still imported, but its result now carries a message
  saying the entry is left out of shooter results until the competitor is verified
- The message of a row imported without issue stays empty, so existing callers that treat an empty message as success
  are unaffected

### 📖 Documented Workflow

- `README.md` and `ARCHITECTURE.md` describe verifying competitors in bulk and the message on an import row
- The Javadoc on `IpscCompetitorService` and `MatchCompetitorBulkResponse`, and the OpenAPI descriptions on
  `IpscCompetitorController` and `IpscMatchCompetitorController`, say the same

---

## 📦 What's New

### Added

- `IpscCompetitorController.verifyCompetitors`, serving `POST /ipsc/competitors/verify`
- `IpscCompetitorService.verifyCompetitors` and its implementation
- `CompetitorVerifyRequest`, `CompetitorVerifyResponse` and `CompetitorVerifyResponseHolder`
- `CompetitorRepository.findAllByIdInWithHomeClubAndEmailAddresses`, which fetch-joins the home clubs and email
  addresses of several competitors
- Controller, request model, service and `@SpringBootTest` integration tests for bulk verification, and service and
  integration tests for the unverified match competitor import message

### Changed

- `IpscMatchCompetitorServiceImpl.createMatchCompetitors` gives a row whose competitor is not verified a message, and
  still imports it
- `README.md`, `ARCHITECTURE.md`, `IpscCompetitorService`, `IpscCompetitorController`,
  `IpscMatchCompetitorController` and `MatchCompetitorBulkResponse` describe both
- `documentation/roadmap/improvement-plan.md` and `improvement-plan-tasks.md` add and close Gap #46

### Fixed

Nothing is fixed.

### Removed

Nothing is removed.

---

## 🚀 Migration Guide

No action is needed: no existing endpoint, request or response changes shape, and there is no schema change.

The one visible difference is that the `message` of a match competitor import row can now be non-empty for a row that
succeeded. A caller that treats any non-empty `message` as a failure should check `success` instead; a row for an
unverified competitor has `success` set to `true` and a message beginning "Imported, but".

---

## 📊 Statistics

- **Files Changed:** 19 against `main` before this release's documentation; 632 insertions and 11 deletions
- **New Source Files:** 3 (`CompetitorVerifyRequest`, `CompetitorVerifyResponse` and `CompetitorVerifyResponseHolder`),
  plus the request's test class
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **Verification stays deliberate.** Nothing defaults `isVerified` to `true`, and neither import verifies anyone; the
  new endpoint only makes the existing manual step cheaper for many competitors.
- **One bad ID does not spoil the batch.** An unknown ID is a result, not an error, so a list with one stale ID still
  verifies the rest, as the bulk imports treat a failed row.
- **One transaction.** The competitors are written together through `TransactionService.saveCompetitors`, so the batch
  commits or rolls back as one unit, and only competitors that are not already verified are written.
- **A warning, not a refusal.** A row for an unverified competitor is still imported, because the entry is correct and
  will count once the competitor is verified; the message tells the caller why it is not in results yet.
- **A MINOR.** The new endpoint is an externally visible, backward-compatible addition, which `AGENTS.md`'s Semantic
  Versioning rules classify as MINOR.

---

## 🧪 Testing

- `./mvnw test` passes: 1,498 tests with no failures or errors.
- New tests cover `CompetitorVerifyRequest`'s validation, `IpscCompetitorController.verifyCompetitors`, and
  `IpscCompetitorService.verifyCompetitors` verifying, reporting an unknown ID, leaving an already verified competitor
  as it is and de-duplicating a repeated ID, in Mockito unit tests and `@SpringBootTest` integration tests, plus the
  message on a match competitor import row for an unverified competitor.

---

## 🐛 Known Issues

- A `MatchCompetitorId`, `MatchId` or `CompetitorId` column in the match, competitor or match competitor CSV imports,
  is read but never used as the row's own ID, since the imports only create records.
- A shooter log already out of step with a match's date is only caught when its overall results are calculated, or when
  the match moves to another day; it is not reported when either date is left alone.
- Shooter logs can only be created one at a time; there is no bulk import for them.
- The bulk competitor import only creates competitors, so it cannot verify one that already exists; use
  `POST /ipsc/competitors/verify` afterwards.
- `pom.xml` still overrides `logback.version`, the Jackson BOM properties, `mysql-connector-j` and `flyway-mysql`, set
  above Spring Boot's managed versions.

---

## 🔮 Future Enhancements

- Add a bulk import for shooter logs if creating them one by one proves slow.
- Report a match whose date is outside a covering log's dates when it is read, not only when the log is calculated.
- Drop the remaining dependency pins once Spring Boot manages versions at least as new.

---

## 👥 Contributors

- Leoni Lubbinge

---

## 📝 Notes

Version 16.2.0 makes the verified-competitor rule practical to work with: one request verifies many competitors, and a
match competitor import says when a row's competitor is left out of results. It leaves the improvement plan with all 46
gaps closed and none open.

---
