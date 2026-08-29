## 🎯 Summary

- Documentation-only patch release: rewraps every root-level documentation file to a consistent ~120-character line
  width, matching `CLAUDE.md`'s pre-existing wrap width.
- Adds two new narrative sections to `HISTORY.md` — a "Major Version Goals" summary and a "Process & Documentation
  Discipline Phase (v7.2.0 – v7.4.0)" entry — filling gaps in the project's own historical record.
- Fixes a spacing regression the rewrap itself introduced (`init()`/`toString()` gained a stray space in nine
  `HISTORY.md` references) before it shipped.
- No domain model, API surface or test behaviour changes.

## 📦 Key Changes

**Added**

- `HISTORY.md` — "Major Version Goals" subsection and "Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)"
  phase entry

**Changed**

- Every root-level documentation file rewrapped to a consistent ~120-character line width

**Fixed**

- `HISTORY.md` — nine `init()`/`toString()` references corrected after the reflow introduced a stray space

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (no source code changed)
- [x] Manually diffed every rewrapped file against its pre-reflow content to confirm no prose changed beyond line
      width and the noted copyedits
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/RELEASE_NOTES_v7.4.1.md`

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)
