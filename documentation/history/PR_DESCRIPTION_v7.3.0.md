## 🎯 Summary

- Corrects `README.md` and `ARCHITECTURE.md`, which had drifted from what the codebase actually implements — overstating match management, competitor/club CRUD, and WinMSS/XML processing as shipped features, and claiming a "Build & Tests" CI gate that isn't wired up as a GitHub Actions trigger.
- Adds a new `/generate-pr-summary` Claude Code command that condenses an already-finalised `RELEASE_NOTES.md`/`PR_DESCRIPTION.md` pair into a short, plain PR summary.
- No domain entities, repositories, services, or API surface changed in this release.

## 📦 Key Changes

**Fixed**
- `README.md` — Introduction/Features no longer claim match management, competitor/club CRUD, WinMSS import, or XML/multi-format processing; coverage-report command corrected to `./mvnw verify -Pcoverage`; stray `1.x – 4.x` version range removed
- `ARCHITECTURE.md` — test package tree corrected (`domain/` removed, `converters/`/`exceptions/` added); CI/CD & Quality Gates table no longer overstates `Build & Tests` as an "All PRs" GitHub Actions trigger

**Added**
- `/generate-pr-summary` Claude Code command

## 🧪 Test Plan

- [x] `./mvnw test` — full suite, unchanged, all passing (documentation-only change)
- [x] Manually verified each corrected `README.md`/`ARCHITECTURE.md` claim against the current codebase and `.github/workflows/codeql.yml`
- [x] Confirmed no version-specific info leaked into `README.md`/`ARCHITECTURE.md`

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-730---2026-08-25)
- [HISTORY.md](/HISTORY.md)
