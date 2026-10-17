# Release Notes – Version 13.3.0

**Release Date:** October 7, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**NGPSA Membership Flag & Club Number Normalisation**

Version 13.3.0 is a minor release. A competitor now records whether their NGPSA membership is paid up, alongside the
existing SAPSA and club flags, and a club number is stored without spaces. A nullable column is added by migration and
no existing request is rejected that was accepted before.

---

## ⭐ Key Highlights

### 🏷️ NGPSA Membership Flag

- New nullable `Competitor.paidUpNgpsa`, stored in `competitor.paid_up_ngpsa`, which the
  `V11_6_0__add_competitor_paid_up_ngpsa.sql` migration adds after `paid_up_sapsa`
- `CompetitorRequest`, `CompetitorPatchRequest` and `CompetitorResponse` gain an optional `paidUpNgpsa`, and the
  competitor CSV import a `PaidUpNgpsa` column; it is stored as `null` when omitted, and a patch leaves it unchanged

### 🔢 Tidier Club Numbers

- `CompetitorMapper.resolveClubNumber` removes all spaces from a supplied club number and trims it, so `applyFields` and
  `applyPatchFields` store a consistent value

---

## 📦 What's New

### Added

- `Competitor.paidUpNgpsa` and the `V11_6_0__add_competitor_paid_up_ngpsa.sql` migration
- An optional `paidUpNgpsa` field on `CompetitorRequest`, `CompetitorPatchRequest` and `CompetitorResponse`, and a
  `PaidUpNgpsa` CSV column on `CompetitorRequestCsvMixIn`, also shown in the controller's CSV import example
- Tests covering `paidUpNgpsa` across the mapper, JSON, CSV and entity-to-response paths

### Changed

- `CompetitorMapper.resolveClubNumber` removes all spaces from a club number and trims surrounding whitespace
- The `CompetitorRequest` all-arguments constructor gains a `paidUpNgpsa` parameter between `paidUpSapsa` and
  `paidUpClub`

---

## 🚀 Migration Guide

No action is needed. Flyway runs `V11_6_0__add_competitor_paid_up_ngpsa.sql` on startup and existing rows read back with
`paidUpNgpsa` as `null`. Code that calls the `CompetitorRequest` all-arguments constructor directly must pass the new
`paidUpNgpsa` argument; JSON and CSV callers are unaffected. A club number sent with spaces, such as `12 34`, is now
stored as `1234`.

---

## 📊 Statistics

- **Files Changed:** 15 against `main` before this release's documentation; 228 insertions and 24 deletions
- **New Source Files:** 0 (1 new migration)
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **Nullable, not defaulted.** `paidUpNgpsa` is `null` when unknown, so existing competitors are not wrongly recorded as
  unpaid.
- **Normalise on the way in.** Removing spaces where the club number is resolved keeps every stored value consistent
  without a data migration.
- **A MINOR.** The new optional field and CSV column are additions and nothing breaks an existing caller, which
  `AGENTS.md`'s Semantic Versioning rules classify as MINOR.

---

## 🧪 Testing

- `./mvnw test` passes: 1,287 tests with no failures, errors or skips.
- New unit tests cover `paidUpNgpsa` in `CompetitorMapper`, `CompetitorRequest`, `CompetitorPatchRequest`,
  `CompetitorRequestCsvMixIn` and `IpscCompetitorServiceImpl`, and the club number normalisation.

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
- Claude Code (Claude Sonnet 5.5) — co-author of the release's commits

---

## 📝 Notes

Version 13.3.0 records whether a competitor's NGPSA membership is paid up and stores club numbers without spaces. The
migration adds a nullable column and no existing caller breaks.

---
