# Pull Request – Version 7.4.0

**Base:** `develop` ← **Compare:** `release/v7.4.0`

---

## 🎯 Summary

- Lays down a new `models/ipsc/request`/`models/ipsc/shared` package of DTOs (`MatchRequest`, `MatchStageRequest`, `MatchStagesRequest`, `MatchOverallResultRequest`, `MatchStageResultRequest` plus CSV variants, and shared Comstock-scoring fields) as groundwork for the IPSC module rebuild — not yet wired to `IpscController`.
- Drops the unused `/v1` route prefix from `AwardController`/`ImageController` (`/v1/awards` → `/awards`, `/v1/images` → `/images`).
- Adds a new AGENTS.md Serial commas rule and tightens the British English rule to cover code identifiers, applying both retroactively across the existing documentation set.
- Adds the `/sync-unreleased-changes` Claude Code command and a handful of release-hygiene fixes (log4j-api CVE override, `.gitignore`/`.aiignore` refresh, README H1 restore).

## 📦 Key Changes

**Added**
- `za.co.hpsc.web.models.ipsc.request`/`.shared` DTOs; `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md`; `/sync-unreleased-changes` command

**Changed**
- AGENTS.md's Serial commas + identifier-spelling rules, applied across `CLAUDE.md`, `README.md`, `ARCHITECTURE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, `HISTORY.md` and the Claude Code command files
- `documentation/roadmap/` renamed to kebab-case
- Four test method names corrected to British-English spelling
- `.gitignore`/`.aiignore` refreshed

**Fixed**
- `AwardController`/`ImageController` route prefix (breaking — see Migration Guide in `RELEASE_NOTES.md`)
- `README.md`'s missing H1 heading

**Removed**
- `.aiignore`'s dedicated `.claude/`/`.github/` AI-only exclusion block

**Security**
- `log4j-api` overridden to `2.25.5` for CVE-2026-49844

## 🧪 Test Plan

- [x] `./mvnw clean compile` — new IPSC DTOs compile cleanly
- [x] `./mvnw test` — full suite passing, including the four renamed test methods
- [x] Manually reviewed the documentation sweep against the new Serial commas/identifier-spelling rules
- [ ] No dedicated test coverage added for the new IPSC request/shared DTOs — groundwork only, not yet exercised by any controller or service (see Known Issues in `RELEASE_NOTES.md`)

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)
