## 🎯 Summary

- Adds `GET /ipsc/competitors`, listing every competitor — the competitor API's counterpart to the existing
  `GET /ipsc/matches`.
- Changes the match CSV's stage delimiter from `-` to `:` (`1:Stage One;2:Stage Two`) and moves the app to Spring
  Boot's default port `8080` — both **client-facing changes**, covered in the release notes' Migration Guide.
- Switches domain `@ManyToOne` associations to eager fetching, upgrades springdoc to its Spring Boot 4 line via an
  imported BOM and removes the unused Spring REST Docs dependency.

## 📦 Key Changes

**Added**

- `IpscCompetitorController.getAllCompetitors` / `IpscCompetitorService.getAllCompetitors`
- Gap #12 in the improvement plan: competitors/matches are documented as "full CRUD" but can't be deleted

**Changed**

- `IpscMatchServiceImpl.parseStages` splits stage entries on the first `:` — **breaking** for CSVs still using `-`
- `server.port=8081` override removed; the app, Swagger UI and OpenAPI URLs now use `8080`
- All domain `@ManyToOne` associations `LAZY` → `EAGER`
- springdoc `2.8.5` → `3.1.0`, versioned through `springdoc-openapi-bom`; project version bumped to 8.7.0

**Removed**

- `spring-restdocs-mockmvc` test dependency and its documentation mentions

**Fixed**

- `getAllMatches`' Swagger response now documented as an array rather than a single `MatchResponse`
- `ARCHITECTURE.md`'s Project Structure tree comments corrected against disk

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (878 tests, 0 failures/errors), up from 872 at v8.6.2
- [x] `./mvnw verify -Pcoverage` — 98.66% line / 99.00% branch coverage, JaCoCo gate passing
- [x] `IpscCompetitorServiceIntegrationTest` confirms `getAllCompetitors` against the real H2 database, empty and
      populated
- [x] `./mvnw dependency:tree` confirms springdoc resolves to `3.1.0` through the imported BOM
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.7.0.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's changes
- [x] `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` checked — a full sweep recorded new
      Gap #12; the branch-diff sync found no closed or progressed gaps
- [ ] Swagger UI opened at `http://localhost:8080/hpsc-web/swagger-ui/index.html` to confirm springdoc 3.1.0
      renders and both "get all" endpoints show array responses

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-870---2026-09-24)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
