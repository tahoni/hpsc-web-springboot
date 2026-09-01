## 🎯 Summary

- Domain feature release: competitors can now hold more than one email address, and every bulk CSV endpoint's
  multi-value cell format is unified onto one shared separator.
- `Competitor.emailAddress` (`String`) becomes `emailAddresses` (`List<String>`), backed by a new
  `competitor_email` child table and a backfilling Flyway migration.
- `AwardService`/`ImageService`'s bulk CSV parsing switches from `|` to `;` via a new
  `SystemConstants.ARRAY_SEPARATOR`, matching the competitor domain's convention.
- A roadmap audit run as part of this release found `.github/workflows/qodana.yml` (added in v8.1.1) had actually
  failed on every run since; rather than fix it, Qodana static analysis is removed from the project entirely.
- New genuinely-multiple-address tests (not just single-address or null/empty) surfaced a real bug:
  `IpscCompetitorServiceImpl.applyFields`/`patchCompetitor` crashed with an unhandled
  `UnsupportedOperationException` on an immutable `emailAddresses` list at update time — now fixed.

## 📦 Key Changes

**Changed**

- `Competitor.emailAddress` → `emailAddresses` (`List<String>`), new `competitor_email` child table,
  `V7_2_0__add_competitor_emails.sql` migration
- `CompetitorRequest`, `CompetitorResponse`, `CompetitorRequestForCSV` — `emailAddress` renamed to `emailAddresses`
- `IpscCompetitorServiceImpl` — new `splitEmailAddresses` helper; `applyFields`/`patchCompetitor`/`toRequest`/
  `toResponse` updated
- `SystemConstants.ARRAY_SEPARATOR` (`";"`) — adopted by `AwardServiceImpl`/`ImageServiceImpl`, replacing `"|"`
- `AwardController`, `ImageController`, `IpscCompetitorController` — Swagger examples updated to match
- `ARCHITECTURE.md`, `CONTRIBUTING.md`, `AGENTS.md` — every Qodana reference in the CI/CD documentation removed
- `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` — Gap #7 closed as not applicable

**Fixed**

- `IpscCompetitorServiceImpl` — `applyFields`/`patchCompetitor` now defensively copy `emailAddresses` into a new
  `ArrayList` instead of storing the caller-supplied `List` reference directly

**Removed**

- `.github/workflows/qodana.yml`, `qodana.yaml` — Qodana static analysis; had failed on every CI run since v8.1.1
  added it (missing `QODANA_TOKEN` secret, unconditional SARIF-upload step)

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (790 tests, up from 775; 0 failures/errors)
- [x] Added genuinely-multiple-address tests across every layer `emailAddresses` touches (`CompetitorRequestTest`,
      `IpscCompetitorServiceImplTest`, `IpscCompetitorServiceTest`, `IpscCompetitorServiceIntegrationTest`)
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/RELEASE_NOTES_v8.2.0.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md`

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)
