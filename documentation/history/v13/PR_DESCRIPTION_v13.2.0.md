## 🎯 Summary

- **Looks a competitor up by club number first**: `findCompetitor` matches the trimmed competitor number as a club
  number before converting it to a number, so a numeric club number now wins over a competitor number with the same
  value.
- **Stops the name patterns backtracking**: `CompetitorHelpers` uses possessive `POSITION_PREFIX` and `WHITESPACE`
  patterns and a fixed `MC_PREFIX` tested with `lookingAt()`.
- A **MINOR** release: nothing breaking and no database migration.

## 📦 Key Changes

**Added**

- `findCompetitor` matches the competitor number as a club number first, through `CompetitorRepository.findByClubNumber`

**Changed**

- A leading position is any number of digits at the start of the name, and a single tab or newline between words is no
  longer turned into a space
- `MC_PREFIX` uses `lookingAt()` with a fixed-length pattern; behaviour is unchanged
- `AwardServiceImpl.mapAwards` drops a `@NonNull` on its parameter that its own null check contradicted
- Archived versions 1.0.0 – 9.1.0 move to `documentation/archive/v1-v4/`, `v5-v7/` and `v8-v9/`, and `ARCHIVE.md` to
  `documentation/legacy/`
- Version bumped to 13.2.0 in `pom.xml` and `@OpenAPIDefinition`

**Fixed**

- `AwardServiceImpl.mapAwards` says "Award request list cannot be null." instead of "Image request list"
- `ImageResponse.setMimeType` resets a blank MIME type to an empty string, as a null one already was

## 🧪 Test Plan

- [x] `./mvnw test` — 1,280 tests, no failures, errors or skips
- [ ] Qodana, CodeQL and Docker workflows pass on this PR
- [x] `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v13/RELEASE_NOTES_v13.2.0.md`

## 👥 Contributors

Leoni Lubbinge and Claude Code (Claude Sonnet 5.5, co-author of most commits).

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
