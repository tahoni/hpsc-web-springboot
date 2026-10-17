## 🎯 Summary

- **Validates request bodies on arrival**: the competitor, match and match competitor controllers use `@Valid`, and a
  body that fails Bean Validation now gets a `400 Bad Request` listing every violated constraint instead of a `500`.
- **Cleans competitor names before a lookup**: new `CompetitorHelpers.cleanCompetitorName` removes a leading position,
  an `RO` or `(RO)` marker and full stops, and is used by `MatchCompetitorMapper` and `findCompetitor`.
- A **MINOR** release: nothing breaking and no database migration.

## 📦 Key Changes

**Added**

- `ControllerAdvice.handleMethodArgumentNotValidException`, and `@NotBlank` and `@NotNull` constraints on
  `CompetitorRequest`, `MatchRequest` and `MatchCompetitorRequest`
- `CompetitorHelpers.cleanCompetitorName`

**Changed**

- `@Valid` on the controllers' `@RequestBody` parameters
- `MatchCompetitorMapper.applyFields` and `applyPatchFields`, and
  `EntityIpscCompetitorServiceImpl.findCompetitor`, clean the competitor name;
  a full stop in a name is now ignored when matching
- `jakarta` `@NotNull` replaced by `jspecify` `@NonNull`, with `org.jspecify:jspecify` declared as a dependency, and
  `MatchCompetitorRequest.validate()` returns `void`
- Flyway versioning table re-aligned and a comma added in `AGENTS.md`
- Version bumped to 13.1.0 in `pom.xml` and `@OpenAPIDefinition`

## 🧪 Test Plan

- [x] `./mvnw test` — 1,277 tests, no failures, errors or skips
- [ ] Qodana, CodeQL and Docker workflows pass on this PR
- [x] `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v13/RELEASE_NOTES_v13.1.0.md`

## 👥 Contributors

Leoni Lubbinge and Claude Code (Claude Sonnet 5.5, co-author of most commits).

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
