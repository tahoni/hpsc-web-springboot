## 🎯 Summary

- Documentation-only patch release — no source-code, schema or dependency changes.
- States explicitly, for the first time, that `AGENTS.md` is this project's ultimate source of truth for
  conventions, and that it wins if any other documentation ever contradicts it.
- Standardises the H1 titles of `CHANGELOG.md`, `CONTRIBUTING.md` and `HISTORY.md` to match `README.md`'s existing
  "HPSC Website Backend" project name.
- `CHANGELOG.md` gains a nested "🧾 Change Log" heading as part of that retitling, cascading a one-level heading
  demotion through the rest of the file so its hierarchy stays correct.

## 📦 Key Changes

**Added**

- `AGENTS.md` — states, right before its Documentation File Map, that it is this project's ultimate source of truth
  for conventions
- `CONTRIBUTING.md` — intro now points to `AGENTS.md` for the full convention set, and states `AGENTS.md` wins on
  any contradiction

**Changed**

- `CHANGELOG.md` — title changed from "Changelog" to "HPSC Website Backend" with a new "🧾 Change Log" heading
  beneath it; every heading below it demoted one level to nest correctly
- `CONTRIBUTING.md`/`HISTORY.md` — H1 titles gain the same "HPSC Website Backend" prefix

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (868 tests, 0 failures/errors), confirmed during release prep (no
      production code changed this release)
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/RELEASE_NOTES_v8.4.2.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's own
      changes
- [x] Verified `ARCHITECTURE.md`'s Project Structure tree against disk — no drift
- [x] `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` checked — no new, closed or
      progressed gaps from this branch's documentation-only diff

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)
