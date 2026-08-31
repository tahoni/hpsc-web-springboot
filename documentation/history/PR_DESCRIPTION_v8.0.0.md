# Pull Request – Version 8.0.0

**Base:** `develop` ← **Compare:** `release/v8.0.0`

---

## 🎯 Summary

- Completes the IPSC module rebuild that v6.0.0 through v7.4.0 laid groundwork for: `IpscController`'s empty stub is replaced by `IpscCompetitorController`/`IpscMatchController`, full CRUD backed by new `IpscCompetitorService`/`IpscMatchService` implementations and new request/response DTOs.
- Extends `Gender` with `name`/`abbreviation`/`fromName()`/`toString()` and adds a new `GenderConverter`, wired onto `Competitor.gender`.
- Renames `processCsv` to `createAwards`/`createImages` and every enum's `getByX` factory methods to `fromX`, clearing naming inconsistencies accumulated across earlier releases.
- Invests in a comprehensive Javadoc/`@since` documentation pass, merges `CLAUDE.md`'s guidance into a single `AGENTS.md` reference, migrates the project's AI-agent tooling from slash commands to Skills, and re-adds Qodana JVM static analysis.

## 📦 Key Changes

**Added**
- `IpscCompetitorController`/`IpscMatchController`, `IpscCompetitorService`/`IpscMatchService` + impls
- `CompetitorRequest`/`CompetitorResponse`, `MatchResponse`/`MatchStageResponse` DTOs
- `GenderConverter`; `.claude/skills/*` Skills (converted from `.claude/commands/*.md`); `qodana.yaml`

**Changed**
- `AwardService.processCsv`/`ImageService.processCsv` → `createAwards`/`createImages`; bulk endpoints moved to `/awards/bulk`/`/images/bulk`, returning `201 Created`
- Enum `getByX` factory methods → `fromX` across `ClubIdentifier`, `CompetitorCategory`, `Division`, `FirearmType`, `MatchCategory`, `PowerFactor`
- `models/ipsc/request` split into `models/ipsc/match/request`/`models/ipsc/scores/request`
- `AGENTS.md`/`CLAUDE.md` merged; new line-wrapping, extended Arrange-Act-Assert and test-helper-placement conventions
- `README.md`/`ARCHITECTURE.md`/`CONTRIBUTING.md` reverse-synced to describe the new IPSC CRUD as implemented

**Removed**
- `IpscController` (superseded); `MatchStagesRequest` (unused); `FatalExceptionTest`/`NonFatalExceptionTest`/`ValidationExceptionTest` (JDK-delegation-only coverage)

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing
- [x] New unit + integration coverage for `IpscCompetitorController`/`Service`/`ServiceImpl` and `IpscMatchController`/`Service`/`ServiceImpl`
- [x] New `GenderTest`/`GenderConverterTest`
- [x] Mechanical test updates for the `fromX` enum-factory rename and the `createAwards`/`createImages` rename verified against the existing suites
- [ ] Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only — no controller wiring or dedicated test coverage yet (see Known Issues in `RELEASE_NOTES.md`)

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)
